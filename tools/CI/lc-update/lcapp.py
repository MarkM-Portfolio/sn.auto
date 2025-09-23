# ***************************************************************** 
#                                                                   
# IBM Confidential                                                  
#                                                                   
# OCO Source Materials                                              
#                                                                   
# Copyright IBM Corp. 2011, 2015                                    
#                                                                   
# The source code for this program is not published or otherwise    
# divested of its trade secrets, irrespective of what has been      
# deposited with the U.S. Copyright Office.                         
#                                                                   
# ***************************************************************** 

#
# Lotus Connections installation
#
import java
import sys
import re, glob
import string

# to workaround the issue that the os module in WAS 6.1 doesn't know
# Windows Server 2003 is a NT type OS
windows_os = ["Windows .*"]
for os_pattern in windows_os:
    regex_obj = re.compile(os_pattern)
    os_name = java.lang.System.getProperty("os.name")
    if regex_obj.match(os_name):
        sys.registry.setProperty('python.os', 'nt')
        break

import os
import os.path
import shutil
import traceback
import tempfile
import java.util.Properties as Properties

from com.ibm.ws.scripting import ScriptingException
from com.ibm.websphere.management.exception import AdminException

from com.ibm.lc.install import XmlFile

#
# this is to solve the namespace issue that the AdminX objects are not
# available when this module is imported to the Jython scripts started
# by wsadmin, in the scripts started by wsadmin, pass the 4 objects in
# to make them available here, like this
#   set_wsadmin_refs(AdminConfig, AdminControl, AdminTask, AdminApp)
#
def set_wsadmin_refs(admin_tuple):
    global AdminConfig, AdminControl, AdminTask, AdminApp
    (AdminConfig, AdminControl, AdminTask, AdminApp) = admin_tuple

# return a list of the unique memeber of given sequence
def unique(seq):
    keys = {}
    for e in seq:
        keys[e] = 1
    return keys.keys()

def update_dir_with_files(root_dirs, src_dir, files):
    root_src_dir = root_dirs[0]
    root_dst_dir = root_dirs[1]
    dst_dir = src_dir.replace(root_src_dir, root_dst_dir)
    if not os.path.exists(dst_dir):
        os.mkdir(dst_dir)
    for file in files:
        src_file = os.path.join(src_dir, file)
        dst_file = os.path.join(dst_dir, file)
        if not os.path.isdir(src_file):
            if os.path.exists(dst_file):
                os.remove(dst_file)
            shutil.copy(src_file, dst_dir)

def copytree_replace(root_src_dir, root_dst_dir):
    os.path.walk(root_src_dir, update_dir_with_files, (root_src_dir,root_dst_dir))

class LcError(Exception):
    def __init__(self, value):
        self.value = value
    def __str__(self):
        return repr(self.value)


def get_was_version():
    if WASObject.base_product_version:
        return WASObject.base_product_version
    nodename = None
    try:
        nodename = AdminControl.getNode()
    except ScriptingException:
        print "AdminControl.getNode() failed, looks we are using conntype=NONE"
    if not nodename:
        nodename = Cell().nodes()[0].name
    WASObject.base_product_version = AdminTask.getNodeBaseProductVersion(["-nodeName", nodename])
    return WASObject.base_product_version

# remove any existing was object by selection that matches the attribute given
# selection can be "/Server:server1/CacheProvider:%s/ObjectCacheInstance:/"
def find_and_remove_by_attr(selection, attr_name, attr_value):
    selection_ids = AdminConfig.getid(selection)
    for c_id in selection_ids.splitlines():
        if AdminConfig.showAttribute(c_id, attr_name) == attr_value:
            name  = AdminConfig.showAttribute(c_id, 'name')
            print "Removing [%s] with [%s] = [%s]" % (name, attr_name, attr_value)
            AdminConfig.remove(c_id)

class WASObject:
    base_product_version = None

    def __init__(self, name = ""):
        self.name = name
        self.parent = None

    def scope(self, subscope = ""):
        p_scope = "/"
        if hasattr(self, "parent") and self.parent is not None:
            p_scope = self.parent.scope()
        if hasattr(self, "was_obj_type"):
            o_type = self.was_obj_type
        else:
            o_type = self.__class__.__name__
        scope = "%s%s:%s/" % (p_scope, o_type, self.name)
        return scope + subscope

    def set_was_id(self, the_id):
        self.id = the_id

    def was_id(self):
        if hasattr(self, "id") and self.id:
            return self.id
        ids = AdminConfig.getid(self.scope()).splitlines()
        if len(ids) == 0:
            return ""
        if len(ids) > 1:
            raise LcError, "WAS object is not unique: %s" % ids
        self.id = ids[0]
        return self.id

    def remove(self):
        if self.was_id() != "":
            AdminConfig.remove(self.was_id())
            del self.id

    def exists(self):
        return self.was_id() != ""

    # NOTE: this method may not avaliable to all the WAS object, only to
    # those have properties
    def set_property(self, name, value, properties= "properties"):
        properties = AdminConfig.showAttribute(self.was_id(), properties)
        prop = ""
        if len(properties) > 2:
            properties = properties[1:len(properties)-1]
            for p in properties.split():
                if name == AdminConfig.showAttribute(p, "name"):
                    prop= p
                    break
        attrs = [ ["name", name], ["value", value], ["required", "false"]]
        if prop == "":
            AdminConfig.create("Property", self.was_id(), attrs)
        else:
            AdminConfig.modify(prop, attrs)
        print "Property [%s] set to [%s] on [%s]" % (name, value, self)

    def set_attributes(self, attrs):
        AdminConfig.modify(self.was_id(), attrs)

    def modify(self, attrs):
        AdminConfig.modify(self.was_id(), map(list, attrs.items()))


class Security(WASObject):
    def __init__(self):
        self.name = ""

    def set_webauth(self, auth_value = 'persisting'):
        if auth_value not in ['lazy', 'persisting', 'always']:
            raise LcError, "invalid web auth value - %s" % auth_value
        web_auth = AdminConfig.getid(
            "/Security:/DescriptiveProperty:%s/" % "com.ibm.wsspi.security.web.webAuthReq")
        if web_auth == "":
            raise LcError, 'unable to find ("com.ibm.wsspi.security.web.webAuthReq")'
        if AdminConfig.showAttribute(web_auth, 'value') != auth_value:
            AdminConfig.modify(web_auth, [['value', auth_value]])

    def check_security_settings(self):
        sec = AdminConfig.list('Security')
        if AdminConfig.showAttribute(sec, 'enabled') == 'false':
            print "WARNING - WAS global security is not enabled"
        if AdminConfig.showAttribute(sec, 'appEnabled') == 'false':
            print "WARNING - WAS appliction security is not enabled"

    def enable_global_security(self):
        AdminTask.setGlobalSecurity('[-enabled true]')

    def enable_application_security(self):
        AdminConfig.modify(self.was_id(), [['appEnabled', 'true']])

    def interoperability_mode(self, flag = None):
        property = AdminConfig.getid("/Security:/Property:com.ibm.ws.security.ssoInteropModeEnabled")
        if flag is None:
            return AdminConfig.showAttribute(property, "value")
        if flag == 'false' or not flag:
            value = 'false'
        else:
            value = 'true'
        AdminConfig.modify(property, [['value', value]])

    def sso(self, flag = None):
        sso = AdminConfig.list("SingleSignon")
        if flag is None:
            return AdminConfig.showAttribute(sso, 'enabled')
        if flag == 'false' or not flag:
            value = 'false'
        else:
            value = 'true'
        AdminConfig.modify(sso, [['enabled', value]])

    def sso_domain(self, domain = None):
        sso = AdminConfig.list("SingleSignon")
        if domain is None:
            return AdminConfig.showAttribute(sso, 'domainName')
        AdminConfig.modify(sso, [['domainName', domain]])

    def enable_federated_repositories(self, primary_admin_user):
        print "Set to use Federated User Repositories (WIM)"
        if not hasattr(AdminTask, "configureAdminWIMUserRegistry"):
            print " - The WAS version does not support configureAdminWIMUserRigistry, skip."
            return
        AdminTask.configureAdminWIMUserRegistry([
            '-autoGenerateServerId',
            '-primaryAdminId', primary_admin_user,
            '-verifyRegistry', 'true',
            '-realmName', 'defaultWIMFileBasedRealm'])
        AdminTask.setAdminActiveSecuritySettings(["-activeUserRegistry", "WIMUserRegistry"])

    def updateRealm(self, newRealm):
        print "Updating WAS Realm name"
        currentRealm = AdminTask.getIdMgrDefaultRealm()
        security = AdminConfig.list("Security")
        userReg = AdminConfig.show(security, 'activeUserRegistry')
        userReg = userReg.split(' ')[1].split(']')[0]
        primary_admin_id = AdminConfig.showAttribute(userReg,'primaryAdminId')
        if currentRealm == newRealm:
            return
        AdminTask.renameIdMgrRealm(['-name', currentRealm,
            ' -newName', newRealm ])
        AdminTask.configureAdminWIMUserRegistry(['-realmName', newRealm,
            '-verifyRegistry','false'])
        AdminTask.configureAdminWIMUserRegistry(['-autoGenerateServerId', 'true',
            '-primaryAdminId', primary_admin_id, '-ignoreCase', 'true',
            '-customProperties', '-verifyRegistry', 'false' ])
        AdminTask.updateIdMgrRealm(['-name', newRealm, '-allowOperationIfReposDown', 'false'])
        AdminTask.validateAdminName(['-registryType', 'WIMUserRegistry', '-adminUser', primary_admin_id])

    def addTrustedRealm(self, trustedRealm):
        tRealms = AdminTask.listTrustedRealms('[-communicationType outbound]').splitlines()
        for realm in tRealms:
            if realm == trustedRealm:
                return
        AdminTask.addTrustedRealms(['-communicationType', 'outbound', '-realmList', trustedRealm])

    def remove(self):
        raise LcError, "ERROR, YOU CAN NOT REMOVE SECURITY!!!"


class SessionManager(WASObject):
    def __init__(self, was_id):
        self.set_was_id(was_id)

    def modify_cookie_settings(self, settings):
        cookie = AdminConfig.showAttribute(self.was_id(), 'defaultCookieSettings')
        AdminConfig.modify(cookie, map(list, settings.items()))

    def enable_security_integration(self, value = 'true'):
        self.modify({"enableSecurityIntegration": "false"})
        #AdminConfig.save()


class WebContainer(WASObject):
    def enable_servlet_caching(self, yesno = "true"):
        if (yesno == 'false' or not yesno):
            yesno = 'false'
        else:
            yesno = 'true'
        self.modify({'enableServletCaching': value})


class JavaVirtualMachine(WASObject):
    pass


class CoreGroup(WASObject):
    pass


class ProcessDefinition(WASObject):
    def set_enviroment(self, name, value):
        self.set_property(name, value, "environment")


class Cell(WASObject):
    def __init__(self, name = ""):
        WASObject.__init__(self, name)
        if self.was_id() != "":
            self.name = AdminConfig.showAttribute(self.was_id(), "name")

    def mail_providers(self, name = "Built-in Mail Provider"):
        mp_ids = AdminConfig.getid("/Cell:%s/MailProvider:%s/" % (self.name, name))
        mps = []
        for mpid in mp_ids.splitlines():
            mp = MailProvider()
            mp.set_was_id(mpid)
            mp.parent = self
            mps.append(mp)
        return mps

    def nodes(self, name = ""):
        nodes = []
        scope = self.scope() + "Node:%s/" % name
        node_ids = AdminConfig.getid(scope).splitlines()
        for nid in node_ids:
            #print nid
            n = Node(self)
            n.set_was_id(nid)
            nodes.append(n)
        return nodes

    def clusters(self, name = ""):
        clusters = []
        scope = self.scope() + "ServerCluster:%s/" % name
        cluster_ids = AdminConfig.getid(scope).splitlines()
        for cid in cluster_ids:
            # print "DEBUG - cluster id: %s" % cid
            cl = ServerCluster(self)
            cl.set_was_id(cid)
            clusters.append(cl)
        return clusters

    def security(self):
        sec = Security()
        sec.parent = self
        return sec

    def core_group(self, name = 'DefaultCoreGroup'):
        scope = self.scope() + "CoreGroup:%s/" % name
        cg_id = AdminConfig.getid(scope)
        cg = CoreGroup()
        cg.set_was_id(cg_id)
        return cg

    def resync_all_nodes(self):
        # step 1: reset master cell repository epoch, this will cause all the manual
        # change made to cell be brought to node when sync happens
        cfg = AdminControl.queryNames("*:*,type=ConfigRepository,process=dmgr")
        AdminControl.invoke(cfg, 'refreshRepositoryEpoch')
        # step 2: sync all the nodes except the dmgr itself
        for node in AdminTask.listNodes().splitlines():
            syncl = AdminControl.completeObjectName('type=NodeSync,node=%s,*' % node)
            if syncl == "":
                continue
            print "  node:", node
            AdminControl.invoke(syncl, 'sync')

    def installed_apps(self):
        apps = []
        for app_name in AdminApp.list().splitlines():
            apps.append(Application(app_name))
        return apps

    def create_clusters(self, clusters):
        for c in clusters.keys():
            print "Creating cluster: [%s]" % c
            cl = ServerCluster(self, c)
            if cl.exists():
                print " - Skipped, cluster [%s] already exists." % c
            else:
                cl.create()
            for member in clusters[c]:
                node = Node(self, member['node'])
                if not node.exists():
                    raise LcError, "Unable to find the node [%s]" % member['node']
                servers = node.app_servers(member['name'])
                if len(servers) > 0:
                    print "   - skipped, because member server [%s] already exists." % member['name']
                else:
                    print "   Creating member [%s] on node [%s]" % (member['name'], member['node'])
                    cl.create_member(member['name'], node.name)
                self.update_member_ports(member)
        AdminConfig.save()

    def update_member_ports(self, member):
        if member.has_key('http_port'):
            print "   Changing port number of cluster member [%s] to [%d]." % (member['name'], member['http_port'])
            AdminTask.modifyServerPort(member['name'],
                [ '-nodeName ', member['node'], '-endPointName ',
                  'WC_defaulthost', '-port', member['http_port'],
                  '-modifyShared', 'true'])
        if member.has_key('https_port'):
            print "   Changing port number of cluster member [%s] to [%d]." % (member['name'], member['https_port'])
            AdminTask.modifyServerPort(member['name'],
                [ '-nodeName ', member['node'],
                  '-endPointName ', 'WC_defaulthost_secure', '-port', member['https_port'],
                  '-modifyShared', 'true'])


class Node(WASObject):
    def __init__(self, cell, name = ""):
        WASObject.__init__(self)
        if cell is None:
            cell = Cell()
        self.parent = cell
        self.name = name

    def set_was_id(self, id):
        self.id = id
        self.name = AdminConfig.showAttribute(self.was_id(), "name")
        self.hostname = AdminConfig.showAttribute(self.was_id(), "hostName")

    def cell(self):
        return self.parent

    def servers(self, name = ""):
        return self.servers_by_type(None, name)

    def app_servers(self, name = ""):
        return self.servers_by_type('APPLICATION_SERVER', name)

    def web_servers(self, name = ""):
        return self.servers_by_type('WEB_SERVER', name)

    def deployment_managers(self, name = ""):
        return self.servers_by_type('DEPLOYMENT_MANAGER', name)

    def servers_by_type(self, stype=None, name = ""):
        servers = []
        scope = self.scope() + "Server:%s/" % name
        server_ids = AdminConfig.getid(scope).splitlines()
        for sid in server_ids:
            if stype and stype != AdminConfig.showAttribute(sid, 'serverType'):
                continue
            s = Server(self)
            s.set_was_id(sid)
            s.parent = self
            servers.append(s)
        return servers


class ServerCluster(WASObject):
    def __init__(self, cell, name = ""):
        WASObject.__init__(self)
        if cell is None:
            cell = Cell()
        self.parent = cell
        self.msg_store = {}
        self.msg_store['home'] = None
        self.name = name

    def __str__(self):
        return "WebSphere:cell=%s,cluster=%s" % (cell.name, self.name)

    def set_was_id(self, id):
        self.id = id
        self.name = AdminConfig.showAttribute(self.was_id(), "name")

    def cell(self):
        return self.parent

    def create(self, name = None):
        if name:
            self.name = name
        args = ['-clusterConfig', ['-clusterName', self.name, '-preferLocal', 'true']]
        #print "AdminTask.createCluster(", args, ")"
        AdminTask.createCluster(args)
        return self

    def create_member(self, name, node):
        args = ['-clusterName', self.name,
            '-memberConfig', ['-memberNode', node, '-memberName', name, '-memberWeight', '2',
            '-genUniquePorts', 'true', '-replicatorEntry', 'false']]
        first_member_args = ['-firstMember', ['-templateName', 'default', '-nodeGroup', 'DefaultNodeGroup',
            '-coreGroup', 'DefaultCoreGroup', '-resourcesScope', 'cluster']]
        if len(self.members()) == 0:
            args = args + first_member_args
        #print "AdminTask.createClusterMember(", args, ")"
        AdminTask.createClusterMember(args)
        self.reload_members()
        return self.members()

    def members(self):
        if not hasattr(self, "the_members"):
            self.reload_members()
        return self.the_members

    def nodes(self):
        if not hasattr(self, "the_nodes"):
            self.reload_members()
        return self.the_nodes

    def reload_members(self):
        members = []
        nodes = []
        member_ids = AdminConfig.list('ClusterMember', self.was_id())
        for m in member_ids.splitlines():
            m_node = AdminConfig.showAttribute(m, 'nodeName')
            m_name = AdminConfig.showAttribute(m, 'memberName')
            n = self.cell().nodes(m_node)[0]
            if not n in nodes:
                nodes.append(n)
            s = n.servers(m_name)[0]
            members.append(s)
        self.the_members = members
        self.the_nodes = nodes
        return self.the_members

    def enable_startup_beans_service(self):
        for m in self.members():
            m.enable_startup_beans_service()

    def modify_jvm_settings(self, settings):
        for m in self.members():
            m.modify_jvm_settings(settings)

    def modify_trace_settings(self, settings):
        for m in self.members():
            m.modify_trace_settings(settings)

    def add_host_aliases_to_virtual_host(self, vhost_id):
        for m in self.members():
            m.add_host_aliases_to_virtual_host(vhost_id)

    def update_web_container(self, attrs):
        for m in self.members():
            m.update_web_container(attrs)

    def set_web_container_property(self, name, value):
        for m in self.members():
            m.set_web_container_property(name, value)

    def modify_threadpool(self, pool_name, settings):
        for m in self.members():
            m.modify_threadpool(pool_name, settings)

    def customize_transaction_service(self, attrs):
        for m in self.members():
            m.customize_transaction_service(attrs)

    def modify_log_settings(self, settings):
        for m in self.members():
            m.modify_log_settings(settings)

    def modify_cookie_settings(self, settings):
        for m in self.members():
            m.modify_cookie_settings(settings)

    def enable_security_integration(self, value):
        for m in self.members():
            m.enable_security_integration(value)

    def set_session_manager_property(self, name, value):
        for m in self.members():
            m.set_session_manager_property(name, value)

    def set_environment(self, variables):
        for m in self.members():
            m.set_environment(variables)

    def modify_dynamic_cache(self, settings):
        for m in self.members():
            m.modify_dynamic_cache(settings)

    def stopImmediate(self):
        AdminControl.invoke(self.object_name(), "stopImmediate")

    def stop(self):
        AdminControl.invoke(self.object_name(), "stop")

    def start(self):
        AdminControl.invoke(self.object_name(), "start")

    def restart(self):
        self.reppleStart()

    def reppleStart(self):
        AdminControl.invoke(self.object_name(), "rippleStart")

    def object_name(self):
        return AdminControl.completeObjectName(
            "cell=%s,type=Cluster,name=%s,*" % (self.parent.name, self.name))


class Server(WASObject):
    def __str__(self):
        node = self.node()
        cell = node.cell()
        return "WebSphere:cell=%s,node=%s,server=%s" % (cell.name, node.name, self.name)

    def node(self):
        return self.parent

    def set_was_id(self, id):
        self.id = id
        self.name = AdminConfig.showAttribute(id, "name")

    def java_virtual_machine(self):
        jvm_id = AdminConfig.list("JavaVirtualMachine", self.was_id()).splitlines()[0]
        if jvm_id == "":
            print "WARNING - Unable to locate JavaVirtualMachine of server [%s]" % self.name
            return None
        jvm = JavaVirtualMachine()
        jvm.set_was_id(jvm_id)
        return jvm

    def modify_jvm_settings(self, settings):
        id = self.was_id()
        if id == "":
            raise LcError, "unable to find server: %s" % self.name
        jvm = self.java_virtual_machine()
        if jvm is None :
            raise LcError, "unable to allocate JVM for server: %s" % self.name
        attrs = []
        for n,v in settings.items():
            if n == 'properties':
                for prop_name, prop_value in v.items():
                    jvm.set_property(prop_name, prop_value, "systemProperties")
            else:
                attrs.append([n,v])
        print "Modify JVM settings on [%s] to:" % self.name, attrs
        jvm.set_attributes(attrs)

    def modify_trace_settings(self, settings):
        id = self.was_id()
        if id == "":
            raise LcErrir, "unable to find server: %s" % self.name
        ts = AdminConfig.list("TraceService", id)
        if ts == "":
            raise LcError, "unable to find trace service on server: %s" % self.name
        attrs = []
        for n,v in settings.items():
            attrs.append([n,v])
        print "Modify trace settings on [%s] to:" % self.name, attrs
        AdminConfig.modify(ts, attrs)

    def enable_startup_beans_service(self):
        sbs = AdminConfig.list("StartupBeansService", self.was_id())
        if sbs == "":
            raise LcError, "unable to locate StartupBeansService for server %s" % (self.name)
            return
        sbs_enabled = AdminConfig.showAttribute(sbs, "enable")
        if sbs_enabled == "false":
            print "Enabling Startup Beans Service on [%s] ..." % self.name
            AdminConfig.modify(sbs, [["enable", "true"]])
        print "Startup Beans Serivce enabled."

    def get_ports(self):
        """ returns a dictionary of <PORT_NAME>:<port_number> """
        if not hasattr(self, "ports"):
            self.ports = self.load_ports_info()
        return self.ports

    def load_ports_info(self):
        s_entry = AdminConfig.getid(
            self.node().scope() + "ServerIndex:/ServerEntry:%s" % self.name).splitlines()[0]
        if (s_entry == ""):
            raise LcError, "unable to find ServerEntry for of server %s" % self.name
        result = {}
        seps_str = AdminConfig.showAttribute(s_entry, "specialEndpoints")
        seps = seps_str[1:len(seps_str)-1].split()
        for sep in seps:
            ep_name = AdminConfig.showAttribute(sep, "endPointName")
            ep = AdminConfig.showAttribute(sep, "endPoint")
            port = AdminConfig.showAttribute(ep, "port")
            #print "DEBUG- ", ep_name, ep, port
            result[ep_name] = port
        return result

    def add_host_aliases_to_virtual_host(self, vhost_id):
        aliases_str = AdminConfig.showAttribute(vhost_id, "aliases")
        aliases = aliases_str[1:len(aliases_str)-1].split()
        self.get_ports()
        http_port = self.ports["WC_defaulthost"]
        https_port = self.ports["WC_defaulthost_secure"]
        for alias in aliases:
            hostname = AdminConfig.showAttribute(alias, "hostname")
            port = AdminConfig.showAttribute(alias, "port")
            if port == http_port:
                http_port = None
            if port == https_port:
                https_port = None
        for port in http_port, https_port:
            if port is not None:
                print "Add host alias *:%s to vitual host" % port
                AdminConfig.create("HostAlias", vhost_id, [['hostname','*'],['port',port]])

    def web_container(self):
        container_id = AdminConfig.list("WebContainer", self.was_id()).splitlines()[0]
        if container_id == "":
            print "WARNING - Unable to locate web container"
            return None
        c = WebContainer()
        c.set_was_id(container_id)
        return c

    def update_web_container(self, props):
        print "Update web container of %s with: %s" % (self.name, props)
        self.web_container().modify(props)

    def set_web_container_property(self, name, value):
        self.web_container().set_property(name, value)

    def modify_threadpool(self, tp_name, settings):
        tp = AdminConfig.getid(
            "%sThreadPoolManager:/ThreadPool:%s" % (self.scope(), tp_name))
        if tp == "":
            raise LcError, "failed to locate thread pool %s" % tp_name
        attrs = []
        for k,v in settings.items():
            attrs.append([k,v])
        print "Update WebContainer Thread Pool settings: ", attrs
        AdminConfig.modify(tp, attrs)

    def customize_transaction_service(self, attrs):
        if attrs is None: return
        if len(attrs) == 0: return
        attr_list = []
        for k, v in attrs.items():
            attr_list.append([k,v])
        ts = AdminConfig.list("TransactionService", self.was_id())
        print "Update Transaction Service:", attr_list
        AdminConfig.modify(ts, attr_list)

    def modify_log_settings(self, settings):
        stdout = AdminConfig.showAttribute(self.was_id(), 'outputStreamRedirect')
        stderr = AdminConfig.showAttribute(self.was_id(), 'errorStreamRedirect')
        attrs = []
        for k, v in settings.items():
            attrs.append([k,v])
        print "Update SystemOut.log settings: ", attrs
        AdminConfig.modify(stdout, attrs)
        print "Update SystemErr.log settings: ", attrs
        AdminConfig.modify(stderr, attrs)

    def session_manager(self):
        sm_id = AdminConfig.list("SessionManager", self.was_id())
        sm = SessionManager(sm_id)
        return sm

    def modify_cookie_settings(self, settings):
        self.session_manager().modify_cookie_settings(settings)

    def set_session_manager_property(self, name, value):
        sm = self.session_manager().set_property(name, value)

    def enable_security_integration(self, value):
        self.session_manager().enable_security_integration(value)

    def modify_dynamic_cache(self, settings):
        dc = AdminConfig.list("DynamicCache", self.was_id())
        attrs = []
        for k,v in settings.items():
            attrs.append([k,v])
        AdminConfig.modify(dc, attrs)

    def process_definitions(self):
        pd_ids = AdminConfig.showAttribute(self.was_id(), 'processDefinitions')
        pds = []
        for pd_id in pd_ids[1:-1].split():
            pd = ProcessDefinition()
            pd.set_was_id(pd_id)
            pds.append(pd)
        return pds

    def set_environment(self, variables):
        for pd in self.process_definitions():
            for k,v in variables.items():
                pd.set_enviroment(k, v)


class JAASAuthData(WASObject):
    def __init__(self, name):
        WASObject.__init__(self, name)
        self.uid = ""
        self.password = ""
        self.description = ""
        self.parent = None

    def create(self):
        if self.exists():
            print "Remove existing JAASAuthData [%s]" % self.name
            self.remove()
        scope = "/Security:/"
        if self.parent is not None:
            scope = self.parent.scope("Security:/")
        #print scope
        security = AdminConfig.getid(scope)
        if security == "":
            raise LcError, "unable to get id of cell security"
        attrs = [['alias', self.name],
                 ['userId', self.uid],
                 ['description', self.description],
                 ['password', self.password]]
        #print security
        #print attrs
        AdminConfig.create('JAASAuthData', security, attrs)

    def modify(self, uid, password):
        if not self.exists():
            raise LcError, "JAASAuthData \"%s\" not exists" % self.name
        AdminConfig.modify(self.was_id(), [['userId', uid], ['password', password]])

    def was_id(self):
        if hasattr(self, "id") and self.id:
            return self.id
        scope = "/"
        if self.parent is not None:
            scope = self.parent.scope()
        self.id = ""
        for id in AdminConfig.list("JAASAuthData").splitlines():
            alias = AdminConfig.showAttribute(id, 'alias')
            if alias == self.name:
                self.id = id
                break
        return self.id


class JDBCProvider(WASObject):
    def __init__(self, name):
        self.was_obj_type = "JDBCProvider"
        self.name = name
        self.description = self.name
        self.impl_type = 'Connection pool data source'
        self.parent = None
        self.native_path = ""
        self.driver_jars = []

    def set_classpath(self, driver_home_prefix = None):
        # prefix can be something like "ACTIVITIES", then we will
        # use ACTIVITIES_JDBC_DRIVER_HOME to locate JDBC drivers
        if driver_home_prefix is None:
            if self.name.endswith("JDBC"):
                driver_home_prefix = self.name[0:-len("JDBC")]
            else:
                driver_home_prefix = self.name
            driver_home_prefix = driver_home_prefix.upper()
        self.do_set_classpath(driver_home_prefix)

    def do_set_classpath(self, driver_home_prefix):
        self.classpath = ""
        for jar in self.driver_jars:
            self.classpath += "${%s_JDBC_DRIVER_HOME}/%s;" % (driver_home_prefix, jar)

    def create(self):
        print "Remove existing JDBCProvider [%s], if there is any" % self.name
        self.remove()
        # Force all JDBC Providers to cell level
        scope = "Cell=" + Cell().name
        #if isinstance(self.parent, Server):
        #    scope = "Server=" + self.parent.name
        #elif isinstance(self.parent, Node):
        #    scope = "Node=" + self.parent.name
        #elif isinstance(self.parent, ServerCluster):
        #    scope = "ServerCluster=" + self.parent.name
        #elif isinstance(self.parent, Cell):
        #    scope = "Cell=" + self.parent.name
        #else:
        #    raise LCError, "JDBCProvider parent is bad"
        self.set_classpath()
        AdminTask.createJDBCProvider(
           ['-scope', scope, '-databaseType', self.db_type,
            '-providerType', self.provider_type, '-implementationType', self.impl_type,
            '-name', self.name, '-description', self.description,
            '-classpath', self.classpath, '-nativePath', self.native_path])
        print "JDBCProvider [%s] created" % self.name

    def set_description(self, desp):
        """ update the description of this provider """
        AdminConfig.modify(self.was_id(), [['description', desp]])

    # Note: WAS happen to allow multiple JDBCProvider to have same name
    def remove(self):
        if self.name == "":
            raise LcError, "calling JDBCProvider.remove() with name '' not allowed"
        ids = AdminConfig.getid(self.scope()).splitlines()
        for id in ids:
            print " - Remove JDBCProvider [%s]" % id
            AdminConfig.remove(id)
        self.id = ""


class DB2JDBCProvider(JDBCProvider):
    def __init__(self, name):
        JDBCProvider.__init__(self, name)
        self.db_type = "DB2"
        self.provider_type = 'DB2 Universal JDBC Driver Provider'
        self.native_path = '${DB2UNIVERSAL_JDBC_DRIVER_NATIVEPATH}'
        self.driver_jars = ["db2jcc_license_cu.jar", "db2jcc.jar"]


class DB2JDBCProviderXA(JDBCProvider):
    def __init__(self, name):
        JDBCProvider.__init__(self, name)
        self.db_type = "DB2"
        self.impl_type = 'XA data source'
        self.provider_type = 'DB2 Universal JDBC Driver Provider'
        self.native_path = '${DB2UNIVERSAL_JDBC_DRIVER_NATIVEPATH}'
        self.driver_jars = ["db2jcc.jar",
                "db2jcc_license_cu.jar", "db2jcc_license_cisuz.jar"]
        #implement_class = "com.ibm.db2.jcc.DB2XADataSource"


class SQLServerJDBCProvider(JDBCProvider):
    def __init__(self, name):
        JDBCProvider.__init__(self, name)
        self.db_type ="SQL Server"


class SQLServerJDBCProviderWS(SQLServerJDBCProvider):
    def __init__(self, name):
        SQLServerJDBCProvider.__init__(self, name)
        self.provider_type = "WebSphere embedded ConnectJDBC driver for MS SQL Server"
        self.driver_jars = ["base.jar", "util.jar", "spy.jar", "sqlserver.jar"]


class SQLServerJDBCProviderMS(SQLServerJDBCProvider):
    def __init__(self, name):
        SQLServerJDBCProvider.__init__(self, name)
        self.provider_type = "Microsoft SQL Server JDBC Driver"
        # WAS 7.0 is with Java6, sqljdbc.jar does not support java6, use sqljdbc4.jar
        self.driver_jars = ["sqljdbc4.jar"]


class OracleJDBCProvider(JDBCProvider):
    def __init__(self, name):
        JDBCProvider.__init__(self, name)
        self.db_type = "Oracle"
        self.provider_type = "Oracle JDBC Driver"
        self.driver_jars = ['ojdbc14.jar']


class Oracle11gJDBCProvider(OracleJDBCProvider):
    def __init__(self, name):
        OracleJDBCProvider.__init__(self, name)
        self.driver_jars = ['ojdbc6.jar']


class DataSource(WASObject):
    def __init__(self, name):
        self.was_obj_type = "DataSource"
        self.name = name
        self.jndi = "jdbc/%s" % name
        self.auth = None
        self.provider = None
        self.description = "%s JDBC Data Source" % self.name
        self.db_name = name.upper(),
        self.db_server = "localhost"
        self.db_port = 50000
        self.set_default_properties()
        self.settings = {
            # can be something like:
            #"connectionPool": {"maxConnections": 50},
            #"statementCacheSize": 100
            }

    def set_default_properties(self):
        self.properties = {}

    def create(self):
        if self.exists():
            print "Remove existing Datasource [%s]" % self.name
            self.remove()
        find_and_remove_by_attr("/DataSource:/", "jndiName", self.jndi)

        prvd_id = self.provider.was_id()
        if prvd_id == "":
            raise LcError, "unable to get id for JDBCProvider %s" % self.provider.name

        self.id = AdminTask.createDatasource(prvd_id,
           ['-name', self.name, '-jndiName', self.jndi,
            '-description', self.description,
            '-dataStoreHelperClassName', self.helper_class_name(),
            '-componentManagedAuthenticationAlias', self.auth.name,
            '-configureResourceProperties', self.cfg_rsc_properties()])
        print "DataSource [%s] created" % self.name
        self.add_custom_properties()
        print "Datasource customized properties is added"
        self.apply_settings()
        return self.id

    def cfg_rsc_properties(self):
        props = "[[databaseName java.lang.String %s] " % self.db_name
        props += "[serverName java.lang.String %s] " % self.db_server
        props += "[portNumber java.lang.Integer %s]] " % self.db_port
        return props

    def add_custom_properties(self):
        ds = self.was_id()
        pset = AdminConfig.showAttribute(ds, 'propertySet')
        if pset is None:
            pset = AdminConfig.create('J2EEResourcePropertySet', ds, [])
        #print "PropertySet id = %s" % pset
        existing_properties = self.list_existing_properties(pset)
        for n,v in self.properties.items():
            t = "java.lang.String"
            if type(v) == type(1):
                t = "java.lang.Integer"
            elif v == "true" or v == "false":
                t = "java.lang.Boolean"
            if n in existing_properties.keys():
                AdminConfig.modify(existing_properties[n], [['value',  v],['type', t]])
            else:
                AdminConfig.create('J2EEResourceProperty', pset, [['name', n], ['value', v], ['type', t]])

    def list_existing_properties(self, pset_id):
        """ list existing properties in given J2EEResourcePropertiesSet,
            result is a list of tuple, like: {name:property_id...} """
        prop_ids = AdminConfig.showAttribute(pset_id, "resourceProperties")
        prop_ids = prop_ids[1:len(prop_ids)-1].split()
        prop_dict = {}
        for id in prop_ids:
            name = AdminConfig.showAttribute(id, "name")
            prop_dict[name] = id
        return prop_dict

    def apply_settings(self):
        if len(self.settings) == 0: return
        attrs = []
        for k,v in self.settings.items():
            if hasattr(v, 'keys'):
                attrs1 = []
                for k1,v1 in v.items():
                    attrs1.append([k1,v1])
                v = attrs1
            attrs.append([k,v])
        print "Modify data source settings:", attrs
        AdminConfig.modify(self.was_id(), attrs)


class DB2DataSource(DataSource):
    def __init__(self, name):
        DataSource.__init__(self, name)
        self.db_port = 50000

    def set_default_properties(self):
        self.properties = {
            "driverType": 4,
            "readOnly": "false",
            "currentSchema": "",
            "webSphereDefaultIsolationLevel": 2,
            "connectionSharing": 1 }

    def helper_class_name(self):
        return "com.ibm.websphere.rsadapter.DB2UniversalDataStoreHelper"


class SQLServerDataSource(DataSource):
    def __init__(self, name):
        DataSource.__init__(self, name)
        self.db_port = 1433

    def set_default_properties(self):
        self.properties = { "webSphereDefaultIsolationLevel": 2, "sendStringParametersAsUnicode": "true" }


class SQLServerDataSourceWS(SQLServerDataSource):
    def __init__(self, name):
        SQLServerDataSource.__init__(self, name)

    def helper_class_name(self):
        return "com.ibm.websphere.rsadapter.WSConnectJDBCDataStoreHelper"


class SQLServerDataSourceMS(SQLServerDataSource):
    def __init__(self, name):
        SQLServerDataSource.__init__(self, name)

    def helper_class_name(self):
        return "com.ibm.websphere.rsadapter.MicrosoftSQLServerDataStoreHelper"


class OracleDataSource(DataSource):
    def __init__(self, name):
        DataSource.__init__(self, name)
        self.db_port = 1521

    def helper_class_name(self):
        return "com.ibm.websphere.rsadapter.Oracle10gDataStoreHelper"

    def cfg_rsc_properties(self):
        props = [['URL', 'java.lang.String',
                  "jdbc:oracle:thin:@%s:%d:%s" % (self.db_server, self.db_port, self.db_name)]]
        return props


class Oracle11gDataSource(OracleDataSource):
    def __init__(self, name):
        OracleDataSource.__init__(self, name)

    def helper_class_name(self):
        return "com.ibm.websphere.rsadapter.Oracle11gDataStoreHelper"


class WebSphereVariable:
    def __init__(self, name, value = ""):
        self.parent = None
        self.__was_id = ""
        self.name = name
        self.value = value
        self.description = ""

    def was_id(self):
        self.id = ''
        var_map = AdminConfig.getid(self.parent.scope() + "VariableMap:/")
        vsubs = AdminConfig.list("VariableSubstitutionEntry", var_map).split()
        for s in vsubs:
            name = AdminConfig.showAttribute(s, "symbolicName")
            if name == self.name:
                self.id = s
                break
        return self.id

    def set(self):
        var_id = self.was_id()
        if var_id == "":
            self.create()
        else:
            AdminConfig.modify(var_id,
                [['value', self.value], ['description', self.description]])

    def create(self):
        if not self.was_id() == '':
            print "WAS variable [%s] already defined." % self.name
            return
        var_map = AdminConfig.getid(self.parent.scope() + "VariableMap:/")
        #print "var_map id = %s" % var_map
        AdminConfig.create("VariableSubstitutionEntry", var_map,
            [['symbolicName', self.name],
             ['value', self.value],
             ['description', self.description]])

class SharedLibrary(WASObject):
    def __init__(self, name, attrs={}):
        WASObject.__init__(self, name)
        self.name = name 
        self.native_path = attrs.get('nativePath', '') 
        self.description = attrs.get('description', '')
        self.iso_class_loader = attrs.get('isolatedClassLoader', 'false')
        self.class_path = attrs.get('classPath', '')
        self.parent_mod = ""

    def create(self):
        if self.exists():
            print "Remove existing SharedLibrary [%s]" % self.name
            self.remove()
        scope = "/Cell:%s/" % Cell().name
        attrs = [['name', self.name],
                 ['nativePath', self.native_path],
                 ['description', self.description],
                 ['isolatedClassLoader', self.iso_class_loader],
                 ['classPath', self.class_path]]
        AdminConfig.create('Library', scope, attrs)

    def was_id(self):
        if hasattr(self, "id") and self.id:
            return self.id
        scope = "/"
        if self.parent is not None:
            scope = self.parent.scope()
        self.id = "" 
        for id in AdminConfig.list("Library").splitlines():
            name = AdminConfig.showAttribute(id, 'name')
            if name == self.name:
                self.id = id 
                break
        return self.id

class SIBus(WASObject):
    def remove(self):
        if not self.exists():
            return
        AdminTask.deleteSIBus(['-bus', self.name ])
        del self.id

    def create(self):
        if self.exists():
            print "SIBus [%s] already exists" % self.name
            return self.was_id()
        if not hasattr(self, 'description'):
            self.description = "Lotus Connections Bus"
        if not hasattr(self, 'auth_alias'):
            self.auth_alias = "connectionsAdmin"
        print "Create SIBus [%s]" % self.name
        attrs = [ "-bus", self.name, "-description", self.description, "-interEngineAuthAlias", self.auth_alias,
                  '-highMessageThreshold', 300000 ]
        AdminTask.createSIBus(attrs)

    def add_bus_connector_role(self, user):
        print "Add user [%s] to bus [%s]" % (user, self.name)
        attrs = [ "-bus", self.name, "-user", user ]
        AdminTask.addUserToBusConnectorRole(attrs)

    def add_member(self, member):
        member_found = ""
        for m in AdminConfig.list("SIBusMember", self.was_id()).splitlines():
            if isinstance(member, ServerCluster):
                cluster_attr = AdminConfig.showAttribute(m, "cluster")
                if member.name == cluster_attr:
                    member_found = m
            else:
                node_attr = AdminConfig.showAttribute(m, "node")
                server_attr = AdminConfig.showAttribute(m, "server")
                if member.name == server_attr and member.node().name == node_attr:
                    member_found = m
        if member_found != "":
            print "SIBus member [%s] already exists, id [%s]" % (member.name, member_found)
            return member_found
        print "Creating SIBus member [%s] ..." % member.name
        attrs = ["-bus", self.name ]
                 #"-unlimitedPermanentStoreSize", "true",
                 #"-unlimitedTemporaryStoreSize", "true" ]
                 #"-maxTemporaryStoreSize", 1502,
                 #"-maxPermanentStoreSize", 1502]
        if isinstance(member, ServerCluster):
            log_store  = member.msg_store['home'] + "/%s/log"   % (member.name)
            perm_store = member.msg_store['home'] + "/%s/store" % (member.name)
            temp_store = member.msg_store['home'] + "/%s/store" % (member.name)
            attrs += ["-cluster", member.name, "-fileStore",
                  "-logDirectory", log_store,
                  "-permanentStoreDirectory", perm_store,
                  "-temporaryStoreDirectory", temp_store]
            if member.msg_store['props']:
                for k,v in member.msg_store['props'].items():
                    attrs += [ '-'+k, v ]
            else:
                attrs += [ "-unlimitedPermanentStoreSize", "true", "-unlimitedTemporaryStoreSize", "true" ]
        else:
            attrs += ["-node", member.node().name, "-server", member.name]
        print "AdminTask.addSIBusMembers(", attrs, ")"
        AdminTask.addSIBusMember(attrs)
        print "SIBus member created for [%s]" % member.name

    def add_destination(self, assign_to, dest_spec):
        sib_dest_exists = 0
        for d in AdminConfig.list("SIBDestination", self.was_id()).splitlines():
            d_name = AdminConfig.showAttribute(d, "identifier")
            if d_name == dest_spec["name"]:
                sib_dest_exists = 1
        if sib_dest_exists:
            print "SIBus Destination [%s] already exists" % dest_spec['name']
            return
        attrs = [ "-bus", self.name, "-name", dest_spec["name"],
                  "-type", dest_spec.get("type", "QUEUE"),
                  "-reliability", dest_spec.get("reliability","RELIABLE_PERSISTENT") ]
        if isinstance(assign_to, ServerCluster):
            attrs += [ "-cluster", assign_to.name ]
        else:
            attrs += [ "-node", assign_to.node().name, "-server", assign_to.name ]
        if dest_spec.get("forward_routing_path"):
            attrs += [ "-defaultForwardRoutingPath", [[self.name, dest_spec["forward_routing_path"]]]]
        if dest_spec.get("exceptionDestination"):
            attrs += [ "-exceptionDestination", dest_spec["exceptionDestination"]]
        AdminTask.createSIBDestination(attrs)
        print "SIBus Destination [%s] created" % dest_spec["name"]

    # FIXME give a better name instead of live_on
    # this currently only returns the first engine on belongs to the bus, on given cluster or server
    def engines_on(self, live_on):
        attrs =["-bus", self.name]
        if isinstance(live_on, ServerCluster):
            attrs += ["-cluster", live_on.name ]
        else:
            attrs += ["-node", live_on.node().name, "-server", live_on.name]
        # AdminConfig.list("SIBMessagingEngine") can also list engines
        e_ids = AdminTask.listSIBEngines(attrs).splitlines()
        #names = map(lambda x: AdminConfig.showAttribute(x, 'name'), e_ids)
        return e_ids

    def members(self, server):
        return AdminTask.listSIBusMembers('[-bus ConnectionsBus ]')


class DataReplicationDomain(WASObject):
    def __init__(self, name = ""):
        WASObject.__init__(self, name)
        self.replication_settings = {
            'requestTimeout' : 5,
            'numberOfReplicas' : -1,
            'encryptionType': 'NONE' }

    def create(self):
        domain_ids = AdminConfig.getid(self.scope()).splitlines()
        for domain_id in domain_ids:
            print "Remove existing DataReplicationDomain: [%s]" % domain_id
            AdminConfig.remove(domain_id)
        domain_id = AdminConfig.create('DataReplicationDomain', self.parent.was_id(), [['name', self.name]])
        if domain_id == "":
            raise LCError, "Failed create data replication domain [%s]" % name
        attrs = []
        for k,v in self.replication_settings.items():
            attrs.append([k,v])
        AdminConfig.create('DataReplication', domain_id, attrs)


class MailProvider(WASObject):
    def __init__(self, name=""):
        WASObject.__init__(self, name)
        #mp = AdminConfig.getid(self.cell.scope() + "MailProvider:Built-in Mail Provider/")

    def mail_sessions(self, name=""):
        sessions = []
        for sid in AdminConfig.getid(self.scope() + ("MailSession:%s/" % name)).splitlines():
            s = MailSession()
            s.set_was_id(sid)
            s.parent = self
            sessions.append(s)
        return sessions

    def create_mail_session(self, spec):
        attrs = [
            ["name", spec["name"]],
            ["jndiName", spec["jndiName"]],
            ["mailTransportHost", spec["host"]],
            ["mailTransportUser", spec.get("user", "")],
            ["mailTransportPassword", spec.get("password", "")],
            ["debug", "false"],
            ["strict", "true"],
            ["mailTransportProtocol", "(cells/%s|resources.xml#builtin_smtp)" % self.parent.name],
            ["mailStoreProtocol", "(cells/%s|resources.xml#builtin_pop3)" % self.parent.name] ]
        msessions = self.mail_sessions(spec['name'])
        if msessions:
            print "Mail session [%s] already exists in cell %s" % (spec['name'], self.parent.name)
            for s in msessions: s.remove()
            #return
        #print attrs
        ms_id = AdminConfig.create("MailSession", self.was_id(), attrs)
        #print ms_id
        pset = AdminConfig.create("J2EEResourcePropertySet", ms_id, [])
        #print pset
        if spec.get('user', "") == "":
            basic_auth = "false"
        else:
            basic_auth = "true"
        properties = { "mail.smtp.connectiontimeout":120000,
                       "mail.smtp.timeout":120000,
                       "mail.smtp.port": spec.get("port", 25),
                       "mail.smtp.auth": basic_auth }

        if spec.get("properties"):
            properties.update(spec.get("properties"))

        atts = []
        for n,v in properties.items():
            attrs = [["name", n], ["value", v]]
            if type(v) == type(1):
                attrs += [["type", "java.lang.Integer"]]
            else:
                attrs += [["type", "java.lang.String"]]
            #print attrs
            AdminConfig.create("J2EEResourceProperty", pset, attrs)
        msession = MailSession()
        msession.set_was_id(ms_id)
        return msession


class MailSession(WASObject):
    pass

class Application:
    def __init__(self, name):
        self.name = name

    def restart(self):
        cell = AdminControl.getCell()
        node = AdminControl.getNode()
        #print "Cell=", cell, ", Node=", node
        #print "AdminApp.list() =>", AdminApp.list()
        #print AdminControl.completeObjectName("type=Application,name=%s,*" % self.name)
        apps = AdminControl.queryNames("type=Application,name=%s,*" % self.name).splitlines()
        for app in apps:
            node = re.sub(".*,node=([^,]*).*", r"\1", app)
            server = re.sub(".*,Server=([^,]*).*", r"\1", app)
            app_mgr = AdminControl.queryNames("cell=%s,node=%s,type=ApplicationManager,process=%s,*" % (cell, node, server))
            #print "app_mgr =", app_mgr
            print "Stop app \"%s\" on node \"%s\" server \"%s\"" % (self.name, node, server)
            AdminControl.invoke(app_mgr, 'stopApplication', self.name)
            print "Start app \"%s\" on node \"%s\" server \"%s\"" % (self.name, node, server)
            AdminControl.invoke(app_mgr, 'startApplication', self.name)

    def addFile(self, fpath, uri):
        AdminApp.update(self.name, 'file', ['-operation', 'add', '-contents', fpath, '-contenturi', uri])

    def addModule(self, fpath, uri):
        AdminApp.update(self.name, 'modulefile',
             ['-operation', 'addupdate', '-contents', fpath, '-contenturi', uri])

    def update(self, ear_file):
        if not os.path.exists(ear_file):
            print "ERROR - file %s not exist." % ear_file
            return 0
        AdminApp.update(self.name, "app", [ '-operation', 'update', '-contents', ear_file ])

    def partialUpdate(self, zip_file):
        if not os.path.exists(zip_file):
            print "ERROR -- file %s does not exist." % zip_file
            return 0
        AdminApp.update(self.name, "partialapp", ['-operation', 'update', '-contents', zip_file])

    def setContextRootForWebModules(self, name, uri, context_root):
        AdminApp.edit(self.name, [ '-CtxRootForWebMod', [[name, uri, context_root]]])

    def build_id(self):
        # the output of view command looks like:
        #   '\nApplication Build ID:  [OA4.0] 20120301-1701\n\n'
        bv = AdminApp.view(self.name, '-buildVersion')
        m = re.search('Application Build ID: +(.+)', bv)
        if m:
            return m.group(1)
        return m

    def installed_modules(self):
        keys= ["Module", "URI", "Server"]
        text = AdminApp.view(self.name, ['-MapModulesToServers'])
        return self.admin_app_output_to_list_of_dicts(text, keys)

    def map_to_servers(self, servers):
        opts = []
        svr_strs = map(str, servers)
        for m in self.installed_modules():
            currents = m['Server'].split("+")
            to_add = []
            for s in svr_strs:
                if not s in currents:
                    to_add.append(s)
            if to_add:
                server = "+".join(currents + to_add)
                opts.append([m['Module'], m['URI'], server])
        if not opts:
            print 'all modules already mapped to given servers, skip'
            return
        print 'AdminApp.edit(' + self.name + '[ "-MapModulesToServers",', opts, '] )'
        AdminApp.edit(self.name, ["-MapModulesToServers", opts])
        AdminConfig.save()

    def map_roles(self, roles_map):
        map = []
        for k, v in roles_map.items():
            entry = [k] + v
            map.append(entry)
        AdminApp.edit(self.name, ["-MapRolesToUsers", map])
        AdminConfig.save()

    def roles_map(self):
        keys= ["Role", "Everyone?", "All authenticated?",
               "Mapped users", "Mapped groups",
               "All authenticated in trusted realms?",
               "Mapped users access ids", "Mapped groups access ids"]
        text = AdminApp.view(self.name, '-MapRolesToUsers')
        return self.admin_app_output_to_list_of_dicts(text, keys)

    # convert AdminApp.view() output to list of dictionaries, the out put looks like
    # The first key in keys starts entry, blank lines separates entries.
    def admin_app_output_to_list_of_dicts(self, text, keys):
        entries = []
        entry = None
        for line in text.splitlines():
            line_splitted = line.split(":", 1)
            if len(line_splitted) != 2:
                if entry:
                    entries.append(entry)
                    entry = None
                continue
            key, value = line_splitted
            value = value.strip()
            if key == keys[0]:
                entry = { keys[0]: value }
            if entry is None:
                continue
            if len(line.strip()) == 0:
                entries.append(entry)
                entry = None
            else:
                if key in keys:
                    entry[key] = value
        if entry: entries.append(entry)
        return entries


class LotusConnections:
    def __init__(self, ear_file):
        self.set_ear(ear_file)
        self.install_to = None
        # set the default name to the classname
        self.name = self.__class__.__name__
        self.component_name = self.name.lower()
        self.services_provides = [ self.component_name ]
        self.db_type = "DB2"
        self.data_source_spec = {
            "name": self.component_name,
            "ref": "jdbc/" + self.component_name,
            "jndi": "jdbc/" + self.component_name
            }
        self.default_db_user = {
            "DB2": "LCUSER",
            "Oracle": self.name.upper() + "USER",
            "SQL Server": self.name.upper() + "USER"}
        self.default_db_name = {
            "DB2": self.name.upper(),
            "SQL Server": self.name.upper(),
            "Oracle": "lsconn"}
        self.conn_admin_alias = "connectionsAdmin"
        self.conn_admin_user = ""
        self.conn_admin_password = ""
        self.administrators = []
        self.administrator_groups = []
        self.global_moderators = []
        self.global_mod_groups = []
        self.search_admin = []
        self.search_admin_groups = []
        self.widget_admin = []
        self.widget_admin_groups = []
        self.vhost_name = 'default_host'
        self.modules_settings = {}
        self.modules_to_delete = {}
        self.env_variables= {}
        self.was_variables = {}
        self.custom_was_vars = None
        self.run_as_roles = []
        self.mime_specs = {}
        self.replication_domains = []
        self.cache_specs = []
        self.res_env_entries = []
        self.scheduler_specs = []
        self.wm_specs = []
        self.tm_specs = []
        self.notification_sources = []
        self.mail_session = None
        self.mailin_session = None
        self.si_bus = None
        self.si_bus_dest_specs = []
        self.cf_specs = []
        self.jms_queue_specs = []
        self.jms_topic_specs = []
        self.activation_specs = []
        #known_roles will be set on a per-app basis to be checked against during installation
        self.known_roles = []
        # custom_roles will accept any roles defined in "roles_map" in cfg.py, the specified
        # roles there will be verified with each EAR's known_roles before apply to the app
        self.custom_roles = {}
        self.web_container_specs = { "enableServletCaching": "true" }
        self.web_container_props = {
            "com.ibm.ws.webcontainer.invokefilterscompatibility": "true",
            "com.ibm.ws.webcontainer.assumefiltersuccessonsecurityerror": "true"
            }
        self.web_container_thread_pool = {
            "minimumSize": 50,
            "maximumSize": 75
            }
        self.jvm_settings = {
            "initialHeapSize":384,
            "maximumHeapSize":1536,
            'genericJvmArguments': '-Xgcpolicy:gencon',
            'properties': {
                "com.ibm.ws.cache.CacheConfig.filteredStatusCodes": "304 404 500 500",
                "com.ibm.ws.cache.CacheConfig.filterLRUInvalidation": "true",
                "com.ibm.ws.cache.CacheConfig.filterTimeOutInvalidation": "true",
                "com.ibm.ws.cache.CacheConfig.ignoreValueInInvalidationEvent": "true",
                "com.ibm.ws.cache.CacheConfig.filterInactivityInvalidation": "true"
                }
            }
        self.trace_settings = {
            'startupTraceSpecification': '*=info'
            }
        self.log_settings = {
            'rolloverType': 'BOTH',
            'rolloverSize': 10,
            'maxNumberOfBackupFiles': 10}
        self.transaction_service_props = None
        self.roles_map = {
            'person':   ['No', 'Yes', '', ''],
            'everyone': ['Yes', 'No', '', ''],
            'reader':   ['Yes', 'No', '', ''] }
        self.context_root_prefix = None
        self.context_root = None
        self.shared_libs = []
        self.has_old_2_5_events_producer = None
        self.has_new_3_0_events_publisher = None
        self.has_event_record_consumer = None
        self.has_event_subscriber_ejb = None
        self.has_platform_command_consumer = None
        self.has_following_ejb = None
        self.has_scheduled_task_ejb = None
        self.has_notification_sink_ejb = None
        self.has_mailin_subscriber_mdb = None
        self.require_startup_beans_service = None
        self.config_sref_bootstrap = 1
        self.needs_xa_type_jdbc_provider = None
        self.class_loader_settings = [['mode', 'PARENT_LAST']]
        self.should_replace_ear = None
        self.cookie_settings = {}
        self.dynamic_cache_settings = None
        self.webresources_dir = 'webresources'
        self.starting_weight = 10
        self.oauth_urls = None
        self.use_websphere_saml_sp = None
        # give change to derived classes to setup their names
        self.setup_names()

    def set_ear(self, ear_file):
        self.ear_file = os.path.abspath(ear_file)
        self.kit_dir = os.path.dirname(os.path.dirname(ear_file))

    # override this methods in actual component class to specify correct
    # names for the application
    def setup_names(self):
        pass

    def backup(self, path = "/tmp"):
        AdminApp.export(self.name, path + "/" + self.name + ".ear")
        AdminApp.exportDDL(self.name, path)

    def ear_version(self):
        os.system("unzip -u " + self.ear_file + " META-INF/MANIFEST.MF")
        manifest=open("META-INF/MANIFEST.MF","r").read()
        regex=re.compile("(\[.*\]\ )([0-9]{8})(-)([0-9]{4})(D)?")
        match=regex.search(manifest)
        new_version=match.group()
        return new_version

    def update(self):
        """
        update will cause the application to restart, and if this is multi-server
        setup, it restart after the change is sycnc to that node
        """
        u_opts = [ '-operation', 'update', '-contents', self.ear_file ]
        u_opts += self.ear_opts_common()
        print u_opts
        AdminApp.update(self.name, "app", u_opts)
        AdminConfig.save()

    def update_file(self,file_content,file_uri):
        """
        update will cause the application to restart, and if this is multi-server
        setup, it restart after the change is sycnc to that node

        updates file at file_uri with contents at file_content
        file_uri must be relative to root of the application ear file
        """
        u_opts = [ '-operation', 'update', '-contents', file_content, "-contenturi", file_uri]
        print u_opts
        AdminApp.update(self.name, "file", u_opts)
        AdminConfig.save()

    def export_file(self,file_uri,file_path):
        """
        exports file at file_uri to file_path 
        file__uri must be relative to root of the application ear file
        """

        AdminApp.exportFile(self.name, file_uri, file_path)

        return file_path

    def update_ear_properties_file(self,file_uri,property,value):
        """
        Updates a properties file <file_uri> in the application ear with new (property,value)
        file_uri must be relative to the root of the ear
        """
        try: 
            properties_file = os.path.basename(file_uri)
            tmp_dir = tempfile.mktemp('earContent')
            if not os.path.exists(tmp_dir):
                os.mkdir(tmp_dir)
            tmp_file = self.export_file(file_uri,os.path.join(tmp_dir,properties_file))
            prop = Properties()
            fis = open(tmp_file,'r')
            prop.load(fis)
            fis.close()
            fos = open(tmp_file,'w')
            prop.setProperty(property,value)
            prop.store(fos,'Properties File Enabled for Multi-Tenacy')
            #Update Application
            self.update_file(tmp_file,file_uri)
            shutil.rmtree(tmp_dir)
        except:
            print "ERROR: Update to " + file_uri + " failed"
            traceback.print_exc()

    def uninstall(self):
        AdminApp.uninstall(self.name)
        self.data_source.remove()
        self.data_source.provider.remove()
        self.data_source.auth.remove()

    def install(self, options):
        installed = 0
        try:
            self.do_install(options)
            installed = 1
        except:
            print "ERROR: ", sys.exc_info()[1]
            traceback.print_exc()
        return installed

    def do_install(self, options):
        self.apply_install_options(options)
        if hasattr(self, 'data_source'): self.create_datasource(self.data_source)
        self.set_was_variables()
        self.set_environment()
        self.create_connections_admin_jass_auth()
        self.si_bus.add_member(self.install_to)
        for ds in self.si_bus_dest_specs:
            self.si_bus.add_destination(self.install_to, ds)
        for cf in self.cf_specs:
            self.create_cf(cf)
        for jmsq in self.jms_queue_specs:
            self.create_jms_queue(jmsq)
        for jmst in self.jms_topic_specs:
            self.create_jms_topic(jmst)
        for tm in self.tm_specs:
            self.create_timer_manager(tm)
        for s in self.scheduler_specs:
            self.create_scheduler(s)
        self.create_replication_domains()
        for ci in self.cache_specs:
            self.create_dynamic_cache(ci)
        for wm in self.wm_specs:
            self.create_work_manager(wm)
        for e in self.res_env_entries:
            self.create_resource_env_entry(e)
        self.update_virtual_host()
        for avs in self.activation_specs:
            self.create_activation_spec(avs)
        self.install_to.update_web_container(self.web_container_specs)
        for n,v in self.web_container_props.items():
            self.install_to.set_web_container_property(n, v)
        for sl in self.shared_libs:
            self.create_shared_library(sl)
        self.install_to.set_session_manager_property("HttpSessionIdReuse", "true")
        self.install_to.customize_transaction_service(self.transaction_service_props)
        self.create_mail_session()
        app_exists = AdminConfig.getid("/Deployment:%s/" % self.name) != ''
        if self.ear_file:
            if not app_exists or self.should_replace_ear:
                if app_exists:
                    print "Uninstall application [%s]." % self.name
                    AdminApp.uninstall(self.name)
                print "Install application [%s]." % self.name
                ear_inst_opts = self.ear_install_options()
                self.print_with_password_removed(ear_inst_opts)
                AdminApp.install(self.ear_file, ear_inst_opts)
            else:
                print "Skip install application [%s]." % self.name
            self.map_modules_to_web_servers()
            self.update_app_startup()
            self.update_modules_startup()
            self.delete_modules(self.modules_to_delete)
            self.update_class_loader()
            self.update_session_settings()
        self.install_to.modify_jvm_settings(self.jvm_settings)
        self.install_to.modify_trace_settings(self.trace_settings)
        self.install_to.modify_threadpool(
            "WebContainer", self.web_container_thread_pool)
        self.install_to.modify_log_settings(self.log_settings)
        self.install_to.enable_security_integration('false')
        # we choose to set cookie name in app level, the line below set at server level
        if self.cookie_settings:
            self.install_to.modify_cookie_settings(self.cookie_settings)
        if self.dynamic_cache_settings:
            self.install_to.modify_dynamic_cache(self.dynamic_cache_settings)
        self.install_system_apps()
        if self.require_startup_beans_service:
            self.install_to.enable_startup_beans_service()
        self.additional_post_install()
        self.update_config_files()
        self.laydown_web_resources()
        AdminConfig.save()

    def additional_post_install(self):
        pass

    def apply_install_options(self, opts):
        # we need to use / instead of \ in path, even with Windows, because path
        # has \ saved in WAS env variable will cause problem there.
        self.lc_home = opts['lcHome'].replace("\\", "/")
        self.app_home = self.lc_home + (("/" + self.component_name) * 3)
        self.data_dir = opts.get('dataDirectory', os.path.join(self.lc_home, "data"))
        self.data_dir_local = opts.get('dataDirectoryLocal', self.data_dir)
        self.smartCloud = opts.get('smartCloud')
        self.customization_dir = opts.get('customizationDir', os.path.join(self.data_dir, "customization"))
        if self.smartCloud:
            self.webresources_dir = opts.get('webresourcesDir', os.path.join(self.data_dir_local, "provision", "webresources"))
        else:
            self.webresources_dir = opts.get('webresourcesDir', os.path.join(self.data_dir, "provision", "webresources"))
        self.cell = opts.get("cell", Cell())
        self.cluster = opts.get("cluster")
        self.si_bus = opts.get("connectionsBus")
        self.multitenant = opts.get("multi-tenant")
        self.disableSand = opts.get("disableSand")
        self.urlPattern = opts.get("urlPattern")
        self.topologyName = opts.get('topologyName')
        if self.cluster is None:
            nodes = self.cell.nodes(opts.get("node", ""))
            if len(nodes) == 0:
                raise LcError, "Unable to find node \"%s\"" % opts.get("node", "")
            self.node = nodes[0]
            servers = self.node.app_servers(opts.get("server", ""))
            if len(servers) == 0:
                raise LcError, "Unable to find server \"%s\"" % opts.get("server", "")
            self.server = servers[0]
            self.install_to = self.server
        else:
            clusters = self.cell.clusters(self.cluster)
            if len(clusters) == 0:
                raise LcError, "Unable to find cluster \"%s\"" % self.cluster
            self.cluster = clusters[0]
            self.install_to = self.cluster
            if self.smartCloud:
                print 'Setting message store home to: ' + self.lc_home + "/msgstore/" + self.topologyName
                self.cluster.msg_store['home'] = opts.get('msgStoreHome', self.lc_home + "/msgstore/" + self.topologyName)
            else:
                self.cluster.msg_store['home'] = opts.get('msgStoreHome', self.lc_home + "/msgstore")
            self.cluster.msg_store['props'] = opts.get('msgStore')
        self.web_servers = []
        web_server_nodes = self.cell.nodes(opts.get("webserverNode") or "")
        for node in web_server_nodes:
            for ws in node.web_servers(opts.get("webserver") or ""):
                self.web_servers.append(ws)
        self.lcc_dir = opts.get("LotusConnections-config") or "LotusConnections-config.tmp"
        self.conn_admin_user = opts["connectionsAdminUser"]
        self.conn_admin_password = opts["connectionsAdminPassword"]
        self.cognos_admin_user = opts.get('cognosAdmin', self.conn_admin_user)
        self.cognos_admin_password = opts.get('cognosAdminPassword', self.conn_admin_password)
        self.filenet_admin = opts.get('filenetAdmin', self.conn_admin_user)
        self.filenet_admin_pswd = opts.get('filenetAdminPassword', self.conn_admin_password)
        self.bss_provisioning_admin_user = opts.get("bssProvisioningAdminUser")
        self.shared_libs = opts.get("shared_libs", [])
        self.context_root_prefix = opts.get("contextRootPrefix", "")
        self.context_root = opts.get("contextRoot")
        if self.context_root and self.oauth_urls:
            for oauth_idx in range(len(self.oauth_urls)):
                orig_oauth_url = self.oauth_urls[oauth_idx]
                self.oauth_urls[oauth_idx] = self.update_ctx_root(orig_oauth_url)

        # global-moderator can be either a list of uid strings, or a string of uids
        # separated by '|", by defaut the connectionsAdmin user is given the role
        # 'global-moderator', we need to unify the result list too.
        uids = opts.get("global-moderator", "")
        if isinstance(uids, {}.__class__):
            group_ids = uids.get("groups", "")
            uids = uids.get("users", "")
            if isinstance(group_ids, "".__class__):
                group_ids = group_ids.split("|")
            self.global_mod_groups = unique(group_ids)
        if isinstance(uids, "".__class__):
            uids = uids.split("|")
        self.global_moderators = unique(uids + [self.conn_admin_user])

        uids = opts.get("search-admin", "")
        if isinstance(uids, {}.__class__):
            group_ids = uids.get("groups", "")
            uids = uids.get("users", "")
            if isinstance(group_ids, "".__class__):
                group_ids = group_ids.split("|")
            self.search_admin_groups = unique(group_ids)
        if isinstance(uids, "".__class__):
            uids = uids.split("|")
        self.search_admin = unique(uids + [self.conn_admin_user])

        uids = opts.get("widget-admin", "")
        if isinstance(uids, {}.__class__):
            group_ids = uids.get("groups", "")
            uids = uids.get("users", "")
            if isinstance(group_ids, "".__class__):
                group_ids = group_ids.split("|")
            self.widget_admin_groups = unique(group_ids)
        if isinstance(uids, "".__class__):
            uids = uids.split("|")
        self.widget_admin = unique(uids + [self.conn_admin_user])

        uids = opts.get("admin", "")
        if isinstance(uids, {}.__class__):
            group_ids = uids.get("groups", "")
            uids = uids.get("users", "")
            if isinstance(group_ids, "".__class__):
                group_ids = group_ids.split("|")
            self.administrator_groups = unique(group_ids)
        if isinstance(uids, "".__class__):
            uids = uids.split("|")
        self.administrators = unique(uids)

        #add generic search for any user defined roles
        self.custom_roles = opts.get("roles_map",{})

        self.enable_mail_nofication = (opts.get("enableMailNotification", "true") == "true")
        self.mail_session = opts.get("mailSession")
        if not self.mail_session is None:
            if not self.mail_session.has_key('name'): self.mail_session['name'] = "lcnotification"
            if not self.mail_session.has_key('jndiName'): self.mail_session['jndiName'] = "mail/notification"
        self.db_type = opts.get("dbType", self.db_type)
        db_driver_path = opts.get("dbDriverPath", "/opt/ibm/db2/V10.1/java")
        self.was_variables.setdefault(("%s_HOME" % self.name.upper()), self.app_home)
        self.was_variables["%s_JDBC_DRIVER_HOME" % self.component_name.upper()] = db_driver_path
        self.was_variables["CONNECTIONS_CUSTOMIZATION_PATH"] = self.customization_dir
        self.was_variables["EVENT_ROOT_DIR"] = self.data_dir + "/event"
        self.custom_was_vars = opts.get("was_variables")
        self.enable_wpi = (opts.get("enableWPI", "true") == "true")
        self.enable_wci = (opts.get("enableWCI", "true") == "true")
        self.enable_moderation = (opts.get("enableModeration") == "true")
        self.should_replace_ear = (opts.get("replaceEAR") == "true")
        self.use_websphere_saml_sp = (opts.get("useWebSphereSamlSP") == "true")
        if opts.has_key('startingWeight'):
            self.starting_weight = opts.get("startingWeight")
        logOpts = opts.get('log_options','')
        if logOpts:
            if logOpts.has_key('rolloverType'):
                self.log_settings['rolloverType'] = logOpts.get('rolloverType')
            if logOpts.has_key('rolloverPeriod'):
                self.log_settings['rolloverPeriod'] = logOpts.get('rolloverPeriod')
            if logOpts.has_key('rolloverSize'):
                self.log_settings['rolloverSize'] = logOpts.get('rolloverSize')
            if logOpts.has_key('baseHour'):
                self.log_settings['baseHour'] = logOpts.get('baseHour')
            if logOpts.has_key('maxNumLogFiles'):
                self.log_settings['maxNumberOfBackupFiles'] = logOpts.get('maxNumLogFiles')
        self.web_container_props.update(opts.get('webContainerProperties',{}))
        #check for activation_specs
        actSpecs = opts.get('activation_specs','')
        if actSpecs:
            for opt_spec in actSpecs:
                opt_spec_name = opt_spec.get('name','')
                if opt_spec_name:
                    #search for matching specs, apply changes accordingly
                    for spec in self.activation_specs:
                        spec_name = spec.get('name','')
                        if opt_spec_name == spec_name:
                            spec.update(opt_spec)
        if opts.has_key('webcontainerThreadPool'):
            self.web_container_thread_pool.update(opts.get('webcontainerThreadPool'))
        if opts.has_key('jvm'):
            self.jvm_settings.update(opts.get('jvm'))
        if opts.has_key('trace_options'):
            self.trace_settings.update(opts.get('trace_options'))
        for mod_uri in opts.get('disableModules', []):
            if not self.modules_settings.has_key(mod_uri):
                self.modules_settings[mod_uri] = {}
            self.modules_settings[mod_uri]['auto-start'] = 'false'
        self.modules_to_delete = opts.get('deleteModules', [])
        if opts.has_key('sessionCookieName'):
            self.cookie_settings['name'] = opts.get('sessionCookieName')
        if opts.has_key('sessionCookieDomain'):
            self.cookie_settings['domain'] = opts.get('sessionCookieDomain')
        self.do_apply_install_options(opts)
        if self.data_source_spec:
            # extract db related info from opts based on these keys
            keys = ["dbServer", "dbPort", "dbName", "dbUser", "dbPassword",
                    "dbCustomProperties",
                    "datasourceConnectionPoolMax", "datasourceStatementCacheSize"]
            ds_spec = self.data_source_spec.copy()
            for k in keys:
                if opts.has_key(k):
                    ds_spec[k] = opts[k]
            self.data_source = self.prepare_datasource(ds_spec)

    def do_apply_install_options(self, opts):
        pass

    def ear_install_options(self):
        i_options = [ '-appname', self.name ]
        if isinstance(self.install_to, Server ):
            i_options += [ '-server', self.install_to.name ]
            i_options += [ '-node', self.node.name ]
        elif isinstance(self.install_to, ServerCluster):
            i_options += [ '-cluster', self.install_to.name ]
        else:
            raise LcError, "LCAuto - unsupportd install target"
        i_options += self.ear_opts_common()
        return i_options

    def ear_opts_common(self):
        opts = [ '-defaultbinding.virtual.host', self.vhost_name ]
        opts += self.ear_opts_default_datasource()
        opts += self.ear_opts_BindJndiForEJBNonMessageBinding()
        opts += self.ear_opts_BindJndiForEJBMessageBinding()
        opts += self.ear_opts_MapRolesToUsers()
        opts += self.ear_opts_MapRunAsRolesToUsers()
        opts += self.ear_opts_MapEJBRefToEJB()
        opts += self.ear_opts_MapResEnvRefToRes()
        opts += self.ear_opts_MapResRefToEJB()
        opts += self.ear_opts_contexRootForWebModules()
        opts += self.ear_opts_deployejb_opts()
        opts += self.ear_opts_SharedLibs()
        opts += ['-usedefaultbindings']
        return opts

    def ear_opts_SharedLibs(self):
        if self.shared_libs is None or len(self.shared_libs) == 0:
            return []
        keys= ["Module", "URI", "Shared Libraries"]
        text = AdminApp.view(self.name, ['-MapSharedLibForMod'])
        mod_list = self.admin_app_output_to_list_of_dicts(text, keys)
        if len(mod_list) == 0:
            return []
        opts = []
        modules = {}
        for m in mod_list:
            modules[m["name"]] = {  "URI": m["URI"], 
                                    "Shared Libraries": m["SharedLibs"] 
                                 } 
        for sl in self.shared_libs:
            p_mod = sl.parent_mod
            if isinstance(p_mod, [].__class__):
                for mod in p_mod:
                    opts.append(mod, modules[mod]["URI"],
                                modules[mod]["SharedLibs"] + "+" + sl.name)
            else:
                opts.append(p_mod, modules[p_mod]["URI"], 
                            modules[p_mod]["SharedLibs"] + "+" + sl.name )  
        return ['-MapSharedLibForMod', opts]
     
    def ear_opts_contexRootForWebModules(self):
        if self.context_root_prefix == "" and self.context_root is None:
            return []
        modules = self.web_modules_context_root()
        if len(modules) == 0:
            return []
        opts = []
        for m in modules:
            ctx_root = m["context-root"]
            if self.context_root:
                ctx_root = self.update_ctx_root(m["context-root"])
            opts.append([m["name"], m["URI"], self.context_root_prefix + ctx_root])
        return ['-CtxRootForWebMod', opts]

    def update_ctx_root(self, ctx_root):
        ctx_root_arr = ctx_root.split('/')
        if ctx_root_arr[0] == "" and len(ctx_root_arr) > 1:
            ctx_root_arr[1] = self.context_root
            new_ctx_root = string.join(ctx_root_arr, "/")
        return new_ctx_root

    def ear_opts_default_datasource(self):
        opts =  ['-defaultbinding.datasource.jndi', self.data_source.jndi]
        opts += ['-defaultbinding.datasource.username', self.data_source.auth.name]
        return opts

    def ear_opts_BindJndiForEJBMessageBinding(self):
        bindings = self.mdb_jndi_bindings()
        if not bindings: return []
        return ['-BindJndiForEJBMessageBinding', bindings ]

    def mdb_jndi_bindings(self):
        bindings = []
        if self.has_event_record_consumer:
            bindings.append([
                'ConsumerEJB',
                'EventRecordConsumer',
                'lc.events.consumer.jar,META-INF/ejb-jar.xml',
                '',
                'jms/connections/%s/events/inbound/as' % self.component_name,
                'jms/connections/%s/events/inbound/queue' % self.component_name,
                self.conn_admin_alias])
        if self.has_event_subscriber_ejb:
            bindings.append([
                'ConsumerEJB',
                'EventSubscriber',
                'lc.events.subscribe.jar,META-INF/ejb-jar.xml',
                '',
                'jms/connections/%s/events/consumer/as' % self.component_name,
                'jms/connections/%s/events/consumer/topic' % self.component_name,
                self.conn_admin_alias])
        if self.has_platform_command_consumer:
            # FIXME: this is a workaround for the RIM installer actuall not obey
            # naming rules, for example it mixes dogear and bookmarks for Dogear.
            name_in_jndi = hasattr(self, "name_in_platform_consumer_as_jndi") and \
                self.name_in_platform_consumer_as_jndi or self.component_name
            bindings.append([ "Platform Command Consumer",
                "PlatformCommandConsumerMDB",
                "platformCommand.consumer.jar,META-INF/ejb-jar.xml",
                "",
                "jms/connections/%s/command/consumer/as" % name_in_jndi,
                "jms/connections/command/consumer/topic",
                self.conn_admin_alias])
        if self.has_mailin_subscriber_mdb:
            bindings.append(['MailInMDB', 'MailInSubscriber',
                "lc.mailin.subscribe.jar,META-INF/ejb-jar.xml",
                "",
                "jms/connections/mailin/%s/as" % self.component_name,
                "jms/connections/mailin/%s/topic" % self.component_name,
                self.conn_admin_alias])
        if len(bindings) == 0:
            return []
        return bindings

    def ear_opts_MapRolesToUsers(self):
        if self.roles_map is None or len(self.roles_map) == 0:
            return []

        #iterate through "custom_roles" and add them if they are known
        for role in self.custom_roles.keys():
            if role in self.known_roles:
                self.roles_map[role] = self.custom_roles.get(role) 

        map = []
        for k, v in self.roles_map.items():
            entry = [k] + v
            map.append(entry)
        return [ '-MapRolesToUsers', map ]

    def ear_opts_MapRunAsRolesToUsers(self):
        if len(self.run_as_roles) == 0:
            return []
        return ['-MapRunAsRolesToUsers', self.run_as_roles]

    def ear_opts_BindJndiForEJBNonMessageBinding(self):
        bindings = []
        comp_name = self.component_name
        if self.has_old_2_5_events_producer:
            bindings.append(
                ["JMSSenderEJB",
                 "EventRecordProducer",
                 "lc.events.producer.jar,META-INF/ejb-jar.xml",
                 "ejb/connections/%s/events/producer" % comp_name ])
        if self.has_new_3_0_events_publisher:
            if isinstance(self.has_new_3_0_events_publisher, "a".__class__):
                comp_name = self.has_new_3_0_events_publisher
            bindings.append(
                ["EventPublisher",
                 "EventPublisher",
                 "lc.events.publish.jar,META-INF/ejb-jar.xml",
                 "ejb/connections/%s/events/publisher" % comp_name])
        if self.has_following_ejb:
            bindings.append(
                ["FollowingEJB",
                 "Following",
                 "lc.following.ejb.jar,META-INF/ejb-jar.xml",
                 "ejb/connections/%s/following" % comp_name])
        if self.has_scheduled_task_ejb:
            bindings.append(
                ["lconn.scheduler.ejb",
                 "CommonScheduledTask",
                 "lconn.scheduler.ejb.jar,META-INF/ejb-jar.xml",
                 "ejb/com/ibm/lotus/connections/%s/TaskHandler" % comp_name])
        if self.has_notification_sink_ejb:
            bindings.append(
                ["lconn.scheduler.ejb",
                 "CommonNotificationSink",
                 "lconn.scheduler.ejb.jar,META-INF/ejb-jar.xml",
                 "ejb/com/ibm/lotus/connections/%s/NotificationSink" % comp_name ])
        if len(bindings) == 0:
            return []
        return [ '-BindJndiForEJBNonMessageBinding', bindings ]

    def ear_opts_MapEJBRefToEJB(self):
        refs = []
        comp_name = self.component_name
        if self.has_old_2_5_events_producer:
            refs.append(
                [self.ejb_webui_name,
                 "",
                 self.ejb_webui_xml,
                 "ejb/EventRecordProducer",
                 "com.ibm.lconn.events.producer.bean.EventRecordProducerLocal",
                 "ejb/connections/%s/events/producer" % comp_name])
        if self.has_new_3_0_events_publisher:
            if isinstance(self.has_new_3_0_events_publisher, "a".__class__):
                comp_name = self.has_new_3_0_events_publisher
            refs.append(
                [self.ejb_webui_name,
                 "",
                 self.ejb_webui_xml,
                 "ejb/EventPublisher",
                 "com.ibm.lconn.events.internal.publish.impl.JMSPublisherLocal",
                 "ejb/connections/%s/events/publisher" % comp_name])
        if len(refs) == 0:
            return []
        return [ '-MapEJBRefToEJB', refs ]

    def ear_opts_MapResEnvRefToRes(self):
        return []

    def ear_opts_MapResRefToEJB(self):
        refs = []
        if hasattr(self, "data_source") and hasattr(self, "ejb_webui_name"):
            refs += [
                [self.ejb_webui_name, "", self.ejb_webui_xml,
                 self.data_source_spec['ref'],
                 "javax.sql.DataSource", self.data_source.jndi,
                 "DefaultPrincipalMapping", self.data_source.auth.name ]]
        if self.has_old_2_5_events_producer:
            refs += self.events_producer_resource_refs()
        if refs: return [ '-MapResRefToEJB', refs ]
        return []

    def events_producer_resource_refs(self):
        return [["JMSSenderEJB",
                 "EventRecordProducer",
                 "lc.events.producer.jar,META-INF/ejb-jar.xml",
                 "jms/connectionsNewsInbound",
                 "javax.jms.Queue",
                 "jms/connections/%s/events/outbound/queue" % self.component_name],
                ["JMSSenderEJB",
                 "EventRecordProducer",
                 "lc.events.producer.jar,META-INF/ejb-jar.xml",
                 "jms/connectionsNewsQCF",
                 "javax.jms.QueueConnectionFactory",
                 "jms/connections/%s/qcf" % self.component_name,
                 "DefaultPrincipalMapping",
                 self.conn_admin_alias]]

    def ear_opts_MapModulesToServers(self):
        """
        map the modules to servers, if there is a webserver configured, make
        sure it is added it to the list.
        The options to AdminApp.install() should looks like this
          [-MapModulesToServers', [['.*', '.*', '+WebSphere:cell=mycell,node=mynode,server=server2+WebSphere:cell=mycell,node=mynode,server=server3']]]
        """
        if not self.web_servers:
            return []
        svrs_str = ""
        for s in self.web_servers:
            node = s.node()
            cell = node.cell()
            svrs_str = svrs_str + "+WebSphere:cell=%s,node=%s,server=%s" % (cell.name, node.name, s.name )
        return ['-MapModulesToServers', [['.*', '.*', svrs_str]]]

    def ear_opts_deployejb_opts(self):
        return []

    def set_was_variables(self):
        if isinstance(self.install_to, ServerCluster):
            nodes = self.install_to.nodes()
            cell = self.install_to.cell()
        else:
            nodes = [self.node]
            cell = self.install_to.node().cell()
        if self.custom_was_vars:
            self.was_variables.update(self.custom_was_vars)
        for k,v in self.was_variables.items():
            #for n in nodes:
            #    wv = WebSphereVariable(k, v)
            #    wv.parent = n
            #    wv.set()
            #wv = WebSphereVariable(k, v)
            #wv.parent = self.install_to
            #wv.set()
            wv = WebSphereVariable(k, v)
            if self.smartCloud and (k in ["SEARCH_INDEX_BACKUP_DIR", "CATALOG_INDEX_BACKUP_DIR","ACTIVITY_STREAM_SEARCH_INDEX_BACKUP_DIR"]):
                print "Adding the following variable to node scope only: ", k
                for n in nodes:
                    if n.name.find("acdmgr") >= 0:
                        print "Adding '%s' variable to the node '%s'" % (k, n.name)
                        wv.parent = n
                        wv.set()
                    elif n.name.find("ap01") >= 0:
                        print "Adding '%s' variable to the node '%s'" % (k, n.name)
                        wv.parent = n
                        wv.set()
                    else:
                        print " '%s' variable will NOT be added to the node '%s'" % (k, n.name)
            else:
                wv.parent = cell
            wv.set()

    def set_environment(self):
        self.install_to.set_environment(self.env_variables)

    # returns an array of dictionaries, each dictionary entry is a web module
    # with name, URI, and contex root, like:
    # [ { "name": Communities Web UI",
    #     "URI": "comm.web.war,WEB-INF/web.xml",
    #     "context-root": "/communities"} ]
    def web_modules_context_root(self):
        text = AdminApp.taskInfo(self.ear_file, 'CtxRootForWebMod')
        modules = []
        module = None
        for line in text.splitlines():
            if line.startswith("Web module: "):
                module = { "name": line[len("Web module: "):] }
            if module is None:
                continue
            if len(line.strip()) == 0:
                modules.append(module)
                module = None
            else:
                if line.startswith("URI: "):
                    module["URI"] = line[len("URI: "):]
                elif line.startswith("Context Root: "):
                    module["context-root"] = line[len("Context Root: "):]
        if module: modules.append(module)
        return modules

    def existing_modules(self):
        app_id = AdminConfig.getid("/Deployment:%s/" % self.name)
        if app_id == "":
            raise LcError, "Application [%s] is not installed" % app_name
        obj_ref = AdminConfig.showAttribute(app_id, "deployedObject")
        modules = AdminConfig.showAttribute(obj_ref, "modules")
        # Note that the returned modules is a string like "[(xyz) (abc)]", and
        # str.strip("[]") does not work with the Jython in WAS
        results = {}
        begins_at = 0
        while begins_at < len(modules):
            begins_at = modules.find("(", begins_at)
            if begins_at == -1: break
            ends_at = modules.find(")", begins_at)
            if ends_at == -1: break # wait, this is problem, we started but no ends ?!
            m = modules[begins_at : ends_at+1]
            begins_at = ends_at + 1
            uri = AdminConfig.showAttribute(m, 'uri')
            results[uri] = m
        return results

    def delete_modules(self, list_of_module_uri):
        modules = self.existing_modules()
        #print modules.keys()
        print "Delete modules: ", list_of_module_uri
        for uri in list_of_module_uri:
            if not modules.get(uri):
                print "  WARNING: module '%s' does not exist in '%s'" % (uri, self.name)
                continue
            print "Deleting module '%s' from '%s'" % (uri, self.name)
            AdminApp.update(self.name, 'modulefile',
                ['-operation', 'delete', '-contenturi', uri])

    def update_modules_startup(self):
        print "Update modules startup settings with: ", self.modules_settings
        if len(self.modules_settings) == 0:
            return
        modules = self.existing_modules()
        #print modules.keys()
        for uri,v in self.modules_settings.items():
            if not modules.get(uri):
                print "  WARNING: module '%s' does not exist in '%s'" % (uri, self.name)
                continue
            weight = v.get('startingWeight')
            if weight:
                print "    Set startingWeight of '%s' to %s" % (uri, weight)
                attrs = [['startingWeight', weight]]
                AdminConfig.modify(modules[uri], attrs)
            if v.get('auto-start', 'true') == 'false':
                print "    Disable autostart on all mapped targets"
                mappings_str = AdminConfig.showAttribute(modules[uri], 'targetMappings')
                for mapping in mappings_str[1:len(mappings_str)-1].split():
                    print "    ", mapping
                    AdminConfig.modify(mapping, [['enable', 'false']])

    def update_app_startup(self):
        print "Update app starting weight to: ", self.starting_weight
        app_id = AdminConfig.getid("/Deployment:%s/" % self.name)
        if app_id == "":
            raise LcError, "Application [%s] is not installed" % self.name
        deployed_obj = AdminConfig.showAttribute(app_id, 'deployedObject')
        AdminConfig.modify(deployed_obj, [['startingWeight', self.starting_weight]])

    def update_class_loader(self):
        print "Update class loader policies and settings:"
        app_id = AdminConfig.getid("/Deployment:%s/" % self.name)
        if app_id == "":
            raise LcError, "Application [%s] is not installed" % app_name
        deployed_obj = AdminConfig.showAttribute(app_id, 'deployedObject')
        attrs = []
        if self.class_loader_settings:
            # settings can be [['mode', 'PARENT_LAST']] to set load parent last
            attrs.append(['classloader', self.class_loader_settings])
        if hasattr(self, 'war_class_loader_policy'):
            attrs.append(['warClassLoaderPolicy', self.war_class_loader_policy])
        if not attrs:
            print "nothing about class loader needs update"
            return
        print " -", attrs
        AdminConfig.modify(deployed_obj, attrs)

    def update_session_settings(self):
        if not self.cookie_settings: return
        app_id = AdminConfig.getid("/Deployment:%s/" % self.name)
        if app_id == "":
            raise LcError, "Application [%s] is not installed" % app_name
        deployed_obj = AdminConfig.showAttribute(app_id, 'deployedObject')
        configs = AdminConfig.showAttribute(deployed_obj, 'configs')
        default_cookies_settings = {'defaultCookieSettings': map(list, self.cookie_settings.items())}
        if len(configs) > 2 :
            config = configs[1:-1]
            sm_id = AdminConfig.showAttribute(config, 'sessionManagement')
            sm = SessionManager(sm_id)
            sm.modify(default_cookies_settings)
        else:
            AdminConfig.create('ApplicationConfig', deployed_obj, 
                [['sessionManagement', map(list, default_cookies_settings.items())]])

    def create_connections_admin_jass_auth(self):
        auth = JAASAuthData(self.conn_admin_alias)
        auth.uid = self.conn_admin_user
        auth.password = self.conn_admin_password
        auth.description = "Lotus Connections Administrator"
        auth.create()

    def create_work_manager(self, wm_spec):
        scope = self.install_to.scope()
        if wm_spec.get("scope") == "CELL":
            scope = self.cell.scope()
            del wm_spec["scope"]
        wmp_id = AdminConfig.getid(scope + "WorkManagerProvider:WorkManagerProvider/")
        if wmp_id == "":
            raise Exception, "Unable to allocate WorkManagerProvider"
        wm_id = AdminConfig.getid(scope +
            "WorkManagerProvider:WorkManagerProvider/WorkManagerInfo:%s/" % wm_spec["name"])
        if wm_id != "":
            print "Remove existing Work manager [%s]" % wm_spec["name"]
            AdminConfig.remove(wm_id)
        # find work manager by the jndi name
        wrk_mgrs = AdminConfig.getid(scope + "WorkManagerProvider:WorkManagerProvider/WorkManagerInfo:/")
        for wm_id in wrk_mgrs.splitlines():
            if AdminConfig.showAttribute(wm_id, 'jndiName') == wm_spec['jndiName']:
                wm_name = AdminConfig.showAttribute(wm_id, 'name')
                print "Remove Work Manager [%s] for JNDI [%s]" % (wm_name, wm_spec['jndiName'])
                AdminConfig.remove(wm_id)
        spec = {
            "serviceNames":"security;UserWorkArea;com.ibm.ws.i18n",
            "maxThreads":2 , "minThreads":1 , "numAlarmThreads":1 , "isGrowable":"false",
            "threadPriority":5, "workReqQSize":10, "workReqQFullAction":1 }
        spec.update(wm_spec)
        attrs = []
        for k,v in spec.items():
            attrs += [[k, v]]
        print "Create WorkManager [%s] - " % spec["name"], attrs
        AdminConfig.create("WorkManagerInfo", wmp_id, attrs)
        print "WorkManager [%s] created." % spec["name"]

    def create_scheduler(self, s_spec):
        schd_prvd = AdminConfig.getid(self.install_to.scope() +
                        "SchedulerProvider:SchedulerProvider/")
        if schd_prvd == "":
            raise Exception, "Unable to allocate SchedulerProvider"
        schd_id = AdminConfig.getid(self.install_to.scope() +
            "SchedulerProvider:SchedulerProvider/SchedulerConfiguration:%s/" % s_spec["name"])
        if schd_id != "":
            print "Remove existing Scheduler [%s]" % s_spec["name"]
            AdminConfig.remove(schd_id)
        # remove any scheduler that uses same JNDI name with us
        for schd_id in AdminConfig.getid(self.install_to.scope() +
            "SchedulerProvider:SchedulerProvider/SchedulerConfiguration:/").splitlines():
            if AdminConfig.showAttribute(schd_id, 'jndiName') == s_spec['jndiName']:
                schd_name = AdminConfig.showAttribute(schd_id, 'name')
                print "Remove existing Scheduler [%s] for JNDI [%s]" % (schd_name, s_spec['jndiName'])
                AdminConfig.remove(schd_id)
        attrs = []
        for k,v in s_spec.items():
            attrs += [[k, v]]
        print "Create Scheduler [%s] -" % s_spec["name"], attrs
        AdminConfig.create("SchedulerConfiguration", schd_prvd, attrs)
        print "Scheduler [%s] created." % s_spec["name"]

    def create_timer_manager(self, tm_spec):
        provider_path = self.install_to.scope() + "TimerManagerProvider:TimerManagerProvider/"
        tm_provider = AdminConfig.getid(provider_path)
        if tm_provider == "":
            raise Exception, "Unable to locate timer manager provider!"
        tm_id = AdminConfig.getid(provider_path + "TimerManagerInfo:%s/" % tm_spec["name"])
        if tm_id != "":
            print "Remove existing TimerManager [%s]" % tm_spec["name"]
            AdminConfig.remove(tm_id)
        attrs = []
        for n,v in tm_spec.items():
            attrs += [[n, v]]
        AdminConfig.create("TimerManagerInfo", tm_provider, attrs)
        print "TimerManager [%s] created" % tm_spec["name"]
        return tm_id

    def create_replication_domains(self):
        for spec in self.replication_domains:
            dom = DataReplicationDomain(spec['name'])
            for k in ['requestTimeOut', 'numberOfReplicas', 'encryptionType']:
                if spec.has_key(k):
                    dom.replication_settings[k] = spec[k]
            dom.parent = self.cell
            dom.create()

    def create_dynamic_cache(self, spec):
        cache_name = spec["name"]
        jndi_name = spec["jndiName"]
        provider_name = "CacheProvider"
        if spec.get('scope') == 'CELL':
            scope = self.cell.scope()
        else:
            scope = self.install_to.scope()
        p_id = AdminConfig.getid(scope + "CacheProvider:%s/" % provider_name)
        if p_id == "":
            attrs = [["name", provider_name],
                     ["description", "Cache provider for all Lotus Connections"]]
            p_id = AdminConfig.create("CacheProvider", self.install_to.was_id(), attrs)
        c_id = AdminConfig.getid(scope + "CacheProvider:%s/" % provider_name +
                    "ObjectCacheInstance:%s/" % cache_name)
        if c_id != "":
            print "Remove existing Cache [%s]" % cache_name
            AdminConfig.remove(c_id)
        # remove any existing caches that has same jndiName
        cache_ids = AdminConfig.getid(scope + "CacheProvider:%s/ObjectCacheInstance:/" % provider_name)
        for c_id in cache_ids.splitlines():
            if AdminConfig.showAttribute(c_id, 'jndiName') == spec['jndiName']:
                cache_name_to_remove = AdminConfig.showAttribute(c_id, 'name')
                print "Remove Cache [%s] of JNDI [%s]" % (cache_name_to_remove, spec['jndiName'])
                AdminConfig.remove(c_id)
        attrs = [["name", cache_name], ["jndiName", jndi_name],
                 ["description", cache_name]]
        c_id = AdminConfig.create("ObjectCacheInstance", p_id, attrs)
        default_specs = {
            "cacheSize": 10000, "defaultPriority": 1,
            "enableDiskOffload": "false",
            "diskCacheSizeInGB": 0,
            "diskCacheSizeInEntries": 0,
            "diskCacheEntrySizeInMB": 0,
            "diskCacheCleanupFrequency": 0,
            "disableDependencyId": "true",
            "useListenerContext": "false",
            "enableCacheReplication": "false",
            "replicationType": "NONE",
            "pushFrequency": 1 }
        default_specs.update(spec)
        if default_specs.has_key("scope"): del default_specs['scope']
        if spec.has_key("MemoryCacheEvictionPolicy"):
            mem_policy_id = AdminConfig.create('MemoryCacheEvictionPolicy', c_id,
                spec["MemoryCacheEvictionPolicy"])
            del default_specs["MemoryCacheEvictionPolicy"]
        attrs = []
        for k,v in default_specs.items():
            attrs.append([k,v])
        AdminConfig.modify(c_id, attrs)
        print "Dynamic cache [%s] created." % cache_name
        return c_id

    def create_resource_env_entry(self, entry):
        re_provider = self.create_resource_env_provider(entry["name"])
        ref_id = self.create_resource_provider_referenceable(re_provider,
                     entry['referenceable'], entry['referenceable'])
        entry_id = AdminConfig.getid(
            "/ResourceEnvironmentProvider:%s/ResourceEnvEntry:%s/" % (entry['name'], entry['name']))
        if entry_id != '':
            print "Remove exisitng ResourceEnvEntry [%s]" % entry['name']
            AdminConfig.remove(entry_id)
        attrs = []
        for n,v in entry.items():
            if n == "referenceable":
                v = ref_id
            attrs += [[n, v]]
        print "Create Resource Environment entry [%s]: " % entry['name'], attrs
        return AdminConfig.create("ResourceEnvEntry", re_provider, attrs)

    def create_resource_env_provider(self, name):
        scope = self.install_to.scope()
        re_provider = AdminConfig.getid(scope + "ResourceEnvironmentProvider:%s" % name)
        if re_provider != "":
            print "Remove existing Resource environment provider [%s]" % name
            AdminConfig.remove(re_provider)
        attrs = [["name", name]]
        re_provider = AdminConfig.create("ResourceEnvironmentProvider",
                self.install_to.was_id(), attrs)
        print "Resource enviroment provider [%s] created" % name
        return re_provider

    def create_resource_provider_referenceable(self, provider, name, fclass):
        refs = AdminConfig.showAttribute(provider, "referenceables")
        #print refs
        ref_id = ""
        for r in refs[1:-1].split():
            if fclass == AdminConfig.showAttribute(r, "factoryClassname"):
                ref_id = r
        if ref_id != "":
            print "Remove existing Resource Environment Provider Referenceable [%s]" % fclass
            AdminConfig.remove(ref_id)
        attrs = [ ["classname", name], ["factoryClassname", fclass]]
        ref_id = AdminConfig.create("Referenceable", provider, attrs)
        print "Resource Environment Referenceable created for [%s]" % name
        return ref_id

    def update_virtual_host(self):
        vhost_id = AdminConfig.getid("/VirtualHost:%s/" % self.vhost_name)
        if vhost_id == "":
            raise LcError, "Unable to find virtual host %s" % self.vhost_name
        for m_type, m_ext in self.mime_specs.items():
            self.add_mime_entry(vhost_id, m_type, m_ext)
        self.install_to.add_host_aliases_to_virtual_host(vhost_id)
        print "Virtual host [%s] updated" % self.vhost_name

    def add_mime_entry(self, vhost_id, mime_type, mime_ext):
        mime_entries = AdminConfig.list("MimeEntry").splitlines()
        for id in mime_entries:
            if id == "" or id == "\"":
                continue
            t = AdminConfig.showAttribute(id, "type")
            if t == mime_type:
                print "Remove existing MIME type [%s], id: %s" %(mime_type, id)
                AdminConfig.remove(id)
        attrs = [["type", mime_type], ['extensions', mime_ext]]
        AdminConfig.create("MimeEntry", vhost_id, attrs)

    def create_mail_session(self):
        if self.mail_session is None: return
        print "Creating mail session specified as [%s]" % self.mail_session
        mps = self.cell.mail_providers('Built-in Mail Provider')
        if not mps or len(mps) > 1:
            raise Exception, "Unable to allocate build in mail provider"
            return
        return mps[0].create_mail_session(self.mail_session)

    def create_activation_spec(self, aspec):
        target_id = self.cell.was_id()
        if not aspec.get('scope', 'CELL'):
            target_id = self.install_to.was_id()
        for s in AdminConfig.list("J2CActivationSpec").splitlines():
            if s == "":
                continue
            #print " ==> spec found: ", s
            name = AdminConfig.showAttribute(s, "name")
            if name == aspec["name"]:
                print "Remove existing JMS Activation Specification [%s]" % name
                AdminConfig.remove(s)
            elif AdminConfig.showAttribute(s, 'jndiName') == aspec['jndiName']:
                print "Remove existing JMS Activation Specification [%s] for JNDI [%s]" % (name, aspec['jndiName'])
                AdminConfig.remove(s)
        attrs = [ "-busName", self.si_bus.name,
            "-authenticationAlias", self.conn_admin_alias ]
        for k,v in aspec.items():
            if v == "REPLACE_WITH_ENGINE_NAME": v = self.sib_engine_name()
            attrs += ["-" + k, v]
        AdminTask.createSIBJMSActivationSpec(target_id, attrs)
        print "JMS Activation Specification [%s] created" % aspec["name"]

    # returns the name of the first message engine of the member of the bus.
    # If we install to cluster, the member is the first cluster member, or the
    # member will be the server.
    def sib_engine_name(self):
        engines = self.si_bus.engines_on(self.install_to)
        if len(engines) == 0:
            print "WARNING: unable to find out name of message engine"
            return ""
        engine_name = AdminConfig.showAttribute(engines[0], 'name')
        return engine_name

    def create_cf(self, cf_spec):
        name = cf_spec["name"]
        create_under = self.cell
        if not cf_spec.get('scope', 'CELL') == "CELL":
            create_under = self.install_to
        # if the target needs to be set to the bus member name, we need to made it up like
        # "lc45linux1Node01:server1" for standalone server and "ic45cluster1" for a cluster
        if cf_spec.get('target') == "":
            if isinstance(self.install_to, Server):
                cf_spec['target'] = "%s:%s" % ( self.install_to.node().name, self.install_to.name)
            else:
                cf_spec['target'] = self.install_to.name
        # remove any topic connection factory that has same name or jndiName
        cf_ids = AdminConfig.getid("/J2CResourceAdapter:/J2CConnectionFactory:/").splitlines()
        for cf_id in cf_ids:
            tmp_name = AdminConfig.showAttribute(cf_id, 'name')
            tmp_jndi = AdminConfig.showAttribute(cf_id, 'jndiName')
            if tmp_name != name and tmp_jndi != cf_spec['jndiName']:
                continue
            print "Remove existing Connection Factory [%s][%s]" % (tmp_name, tmp_jndi)
            AdminConfig.remove(cf_id)
        cf_spec["type"] = cf_spec.get("type", "queue")
        cf_spec.update({"busName": self.si_bus.name, "authDataAlias": self.conn_admin_alias})
        attrs = []
        conn_pool = None
        for k,v in cf_spec.items():
            # scope is only used in our script to determine the CF goes
            if k == "scope": continue
            # containerAuthAlias not available on WAS6
            if k == "containerAuthAlias" and get_was_version().startswith("6"): continue
            if k == 'connectionPool':
                conn_pool = v
                continue
            if v == "REPLACE_WITH_ENGINE_NAME": v = self.sib_engine_name()
            attrs += ["-" + k, v]
        cf_id = AdminTask.createSIBJMSConnectionFactory(create_under.was_id(), attrs)
        print "JMS Connection Factory [%s] created" % name
        if conn_pool:
            print "Update connectionPool settings for [%s]" % name
            attrs = []
            for k,v in conn_pool.items():
                attrs.append([k, v])
            cpid = AdminConfig.showAttribute(cf_id, 'connectionPool')
            print " -", cpid, attrs
            AdminConfig.modify(cpid, attrs)
        return cf_id

    def create_jms_queue(self, q_spec):
        existing_queues = AdminTask.listSIBJMSQueues(self.install_to.was_id()).splitlines()
        existing_queues += AdminTask.listSIBJMSQueues(self.cell.was_id()).splitlines()
        for q in existing_queues:
            if q == "":
                continue
            name = AdminConfig.showAttribute(q, "name")
            if name == q_spec['name']:
                print "Remove existing JMS Queue [%s]" % name
                AdminConfig.remove(q)
            elif AdminConfig.showAttribute(q, 'jndiName') == q_spec['jndi']:
                print "Remove existing JMS Queue [%s] for JNDI [%s]" % (name, q_spec['jndi'])
                AdminConfig.remove(q)
        target_id = self.cell.was_id()
        if not q_spec.get("scope", "CELL") == "CELL":
            target_id = self.install_to.was_id()
        attrs = [ "-name", q_spec['name'], "-jndiName", q_spec['jndi'],
                  "-queueName", q_spec['dest'], "-busName", self.si_bus.name ]
        AdminTask.createSIBJMSQueue(target_id, attrs)
        print "JMS Queue [%s] created" % q_spec['name']

    def create_jms_topic(self, topic_spec):
        target_id = self.install_to.was_id()
        if topic_spec.get("scope") == "CELL":
            target_id = self.cell.was_id()
        for t in AdminTask.listSIBJMSTopics(target_id).splitlines():
            if t == "":
                continue
            # print "  Found JMS Topic:", t
            name = AdminConfig.showAttribute(t, "name")
            if name == topic_spec["name"]:
                print "Remove existing JMS Topic [%s]" % name
                AdminConfig.remove(t)
            elif AdminConfig.showAttribute(t, 'jndiName') == topic_spec['jndi']:
                print "Remove exiting JMS Topic [%s] for JNDI [%s]" % (name, topic_spec['jndi'])
                AdminConfig.remove(t)
        attrs = [ "-name", topic_spec['name'], "-jndiName", topic_spec['jndi'],
                  "-topicSpace", topic_spec['topicSpace'],
                  "-busName", self.si_bus.name ]
        if topic_spec.get('topicName'): attrs += [ "-topicName", topic_spec['topicName'] ]
        AdminTask.createSIBJMSTopic(target_id, attrs)
        print "JMS Topic [%s] created" % topic_spec['name']

    def create_shared_library(self, lib_def):
        sharedlib = SharedLibrary(lib_def['name'], lib_def)
        sharedlib.parent_mod = lib_def.get('module', self.component_name.upper())
        sharedlib.create()
        print "Shared Library [%s] has been created." % sharedlib.name

    def get_provider_ds_klasses(self):
        provider_klass = None
        ds_klass = None
        if self.db_type == "DB2":
            if self.needs_xa_type_jdbc_provider:
                provider_klass, ds_klass = DB2JDBCProviderXA, DB2DataSource
            else:
                provider_klass, ds_klass = DB2JDBCProvider, DB2DataSource
        elif self.db_type == "SQL Server":
            if get_was_version().startswith("6"):
                provider_klass, ds_klass = SQLServerJDBCProviderWS, SQLServerDataSourceWS
            else:
                provider_klass, ds_klass = SQLServerJDBCProviderMS, SQLServerDataSourceMS
        elif self.db_type == "Oracle":
            if get_was_version().startswith("6"):
                provider_klass, ds_klass = OracleJDBCProvider, OracleDataSource
            else:
                provider_klass, ds_klass = Oracle11gJDBCProvider, Oracle11gDataSource
        return (provider_klass, ds_klass)

    def prepare_datasource(self, ds_spec):
        # the JAAS Auth Data
        auth= JAASAuthData("%sJAASAuth" % self.component_name)
        auth.uid = ds_spec.get("dbUser", self.default_db_user[self.db_type])
        auth.password = ds_spec.get('dbPassword', '')
        auth.description = "%s JAAS Auth data" % self.name
        provider_klass, ds_klass = self.get_provider_ds_klasses()
        #print provider_klass, ds_klass
        data_source = ds_klass(ds_spec["name"])
        data_source.jndi = ds_spec["jndi"]
        data_source.provider = provider_klass("%sJDBC" % self.component_name)
        data_source.provider.parent = self.cell
        data_source.provider.description = self.name + " JDBC Provider"
        if ds_spec.has_key("datasourceConnectionPoolMax"):
            data_source.settings['connectionPool'] = {"maxConnections": ds_spec["datasourceConnectionPoolMax"]}
            # to workaround the WAS issue that if change the default connectionPool setting the purgePolicy will
            # be changed to "failingConnectionsOnly"
            data_source.settings['connectionPool']['purgePolicy'] = "EntirePool"
        if ds_spec.has_key("datasourceStatementCacheSize"):
            data_source.settings["statementCacheSize"] = ds_spec["datasourceStatementCacheSize"]
        data_source.auth = auth
        data_source.db_server = ds_spec.get('dbServer', 'localhost')
        data_source.db_port = ds_spec.get('dbPort', data_source.db_port)
        data_source.db_name = ds_spec.get('dbName', self.default_db_name[self.db_type])
        if isinstance(ds_spec.get('dbCustomProperties'), {}.__class__):
            data_source.properties.update(ds_spec['dbCustomProperties'])
        self.customize_datasource(data_source)
        return data_source

    def create_datasource(self, data_source):
        # in case the application has special datasource settings
        data_source.provider.create()
        data_source.auth.create()
        data_source.create()
        return data_source

    def customize_datasource(self, data_source):
        pass

    def install_system_apps(self):
        if len(self.scheduler_specs) == 0:
            return
        opts = ['-systemApp']
        if isinstance(self.install_to, Server):
            opts += ['-node', self.node.name, '-server', self.install_to.name]
        elif isinstance(self.install_to, ServerCluster):
            opts += ['-cluster', self.cluster.name]
        opts += ['-appname', 'SchedulerCalendars']
        try:
            AdminApp.install('${WAS_INSTALL_ROOT}/systemApps/SchedulerCalendars.ear', opts)
            print "SchedulerCalendars installed"
        except ScriptingException, msg:
            #print "DEBUG - Caught ScriptingException -", msg
            print "System application [SchedulerCalendars] appears already been installed."
        except AdminException, msg:
            if str(msg).find("ADMA5015E") >= 0:
                print "SchedulerCalendars application already installed"
            else:
                print "Error when installing SchedulerCalendar app -", msg

    def install_was_provided_app(self, app_name):
        if isinstance(self.install_to, Server):
            opts = ['-node', self.node.name, '-server', self.install_to.name]
        elif isinstance(self.install_to, ServerCluster):
            opts = ['-cluster', self.cluster.name]
        opts += ['-appname', app_name]
        try:
            if app_name in AdminApp.list().splitlines():
                print "Application [%s] already been installed." % app_name
            else:
                ear_file = java.lang.System.getenv('WAS_HOME') + '/installableApps/%s.ear' % app_name
                print "AdminApp.install('%s'," % ear_file, opts, ")"
                AdminApp.install(ear_file, opts)
                print "WebSphere Application %s installed" % app_name
            AdminApp.edit(app_name, self.ear_opts_MapModulesToServers())
        except ScriptingException:
            print "ERROR - installation of [%s] failed, you can manully install it" % app_name
            print sys.exc_info()[1]
            traceback.print_exc()

    def map_modules_to_web_servers(self):
        if not self.web_servers:
            print "No web server specified, skip map modules to web server"
            return
        print "Map modules to web servers [%s] ... " % self.web_servers
        #print self.ear_opts_MapModulesToServers()
        AdminApp.edit(self.name, self.ear_opts_MapModulesToServers())

    def update_config_files(self):
        print "Prepare LotusConnections-config XML files in %s ..." % self.lcc_dir
        files = [ 'contentreview-config.xsd', 'contentreview-config.xml',
                  'contentreview-config-i18n.xsd',
                  'directory.services.xsd', 'directory.services.xml',
                  'LotusConnections-config.xsd', 'LotusConnections-config.xml',
                  'library-config.xsd', 'library-config.xml',
                  'gallery-config.xsd', 'gallery-config.xml',
                  'media-gallery-config.xsd', 'media-gallery-config.xml',
                  'url-blacklist-config.xsd', 'url-blacklist-config.xml',
                  'service-location.xsd',
                  'notification-config.xsd', 'notification-config.xml',
                  'proxy-config.xsd', 'proxy-config.tpl',
                  'uiextensions-config.xsd', 'uiextensions-config.xml',
                  'widgets-config.xsd', 'widgets-config.xml',
                  'hystrix-config.properties',
                  'extern' ]

        for f in files:
            self.make_sure_config_file_in_place(f)
        if self.web_servers:
            s = self.web_servers[0]
            url = "http://" + s.node().hostname
            url_ssl = "https://" + s.node().hostname
            http_port = s.get_ports().get('WEBSERVER_ADDRESS', '80')
            if http_port != "80":
                url = url + ":" + s.get_ports()['WEBSERVER_ADDRESS']
        else:
            if isinstance(self.install_to, ServerCluster):
                print "  WARNING: should configure a web server when deploy to clusters."
                server = self.install_to.members()[0]
            else:
                server = self.install_to
            url = "http://" + server.node().hostname + ":" + server.get_ports()['WC_defaulthost']
            url_ssl = "https://" + server.node().hostname + ":" + server.get_ports()['WC_defaulthost_secure']

        lcc_file = self.make_sure_config_file_in_place("LotusConnections-config.xml")
        f = XmlFile(lcc_file)
        for svc_name in self.services_provides:
            print "  Enable Connections service", svc_name
            path_sref = "/tns:config/sloc:serviceReference[@serviceName=\"%s\"]" % svc_name
            f.modify(path_sref + "/sloc:href/sloc:static/@href", url)
            f.modify(path_sref + "/sloc:href/sloc:static/@ssl_href", url_ssl)
            f.modify(path_sref + "/sloc:href/sloc:interService/@href", url_ssl)
            f.modify(path_sref + "/@enabled", "true")
            f.modify(path_sref + "/@ssl_enabled", "true")
            if self.context_root_prefix != "" or self.context_root:
                values = f.get(path_sref + "/sloc:href/sloc:hrefPathPrefix/text()")
                if values:
                    ctx_root = values[0]
                    if self.context_root:
                        ctx_root = self.update_ctx_root(values[0])
                    f.modify(path_sref + "/sloc:href/sloc:hrefPathPrefix/text()",
                        self.context_root_prefix + ctx_root)
            self.update_config_sref_bootstrap(f, svc_name)
            if self.urlPattern:
                slocPattern='<sloc:pattern href="http://'+ self.urlPattern +'" ssl_href="https://'+ self.urlPattern +'" />'
                f.insertSibling(path_sref + "/sloc:href/sloc:interService", slocPattern)
                f.insertText(path_sref + "/sloc:href/sloc:interService", "\n            ")
        self.update_additional_config_files(f, url, url_ssl)
        if self.disableSand:
            sand_path = "/tns:config/sloc:serviceReference[@serviceName=\"sand\"]"
            f.modify(sand_path + "/@enabled", "false")
            f.modify(sand_path + "/@ssl_enabled", "false")
        f.save()
        print "LotusConnections-config.xml prepared"
        self.update_notifications_config()
        self.put_in_notifications_templates()

    def make_sure_config_file_in_place(self, cfg_file):
        f_path = os.path.join(self.lcc_dir, cfg_file)
        if not os.path.exists(self.lcc_dir):
            os.makedirs(self.lcc_dir)
        if not os.path.exists(f_path):
            path_in_kit = os.path.join(self.kit_dir, "LotusConnections-config", cfg_file)
            if os.path.isdir(path_in_kit):
                shutil.copytree(path_in_kit, f_path)
            else:
                shutil.copyfile(path_in_kit, f_path)
            path_in_mt_kit = os.path.join(self.kit_dir, "LotusConnections-config.mt", cfg_file)
            if self.multitenant and os.path.exists(path_in_mt_kit):
                path_in_kit = path_in_mt_kit
                if os.path.isdir(path_in_kit):
                    copytree_replace(path_in_kit, f_path)
                else:
                    shutil.copyfile(path_in_kit, f_path)
        return f_path

    def update_config_sref_bootstrap(self, lcc, svc_name):
        if not self.config_sref_bootstrap: return
        sref_path = "/tns:config/sloc:serviceReference[@serviceName=\"%s\"]" % svc_name
        if isinstance(self.install_to, ServerCluster):
            lcc.setAttr(sref_path,"clusterName", self.install_to.name)
        else:
            lcc.setAttr(sref_path, "bootstrapHost", self.install_to.node().hostname)
            lcc.setAttr(sref_path, "bootstrapPort", self.install_to.get_ports()['BOOTSTRAP_ADDRESS'])

    def update_notifications_config(self):
        if self.enable_mail_nofication:
            return
        ntfy_cfg = self.make_sure_config_file_in_place("notification-config.xml")
        f = XmlFile(ntfy_cfg)
        for src in self.notification_sources:
            xpath = "//source[@name='%s']/type/channel[@name='email']/@enabled" % src
            curr_value = f.get(xpath)
            print "current setting is [%s]" % curr_value
            if curr_value:
                print "Disable E-mail notification for [%s] in notification-config.xml." % src
                f.modify(xpath, "false")
                f.save()

    def put_in_notifications_templates(self):
        templates_goes_to = os.path.join(self.lcc_dir, 'notifications')
        if not os.path.exists(templates_goes_to):
            os.makedirs(templates_goes_to)
        tplt_dirname = self.component_name
        if hasattr(self, "notifications_templates_dirname"):
            tplt_dirname = self.notifications_templates_dirname
        for i in ['resources', tplt_dirname ]:
            path_in_kit = os.path.join(self.kit_dir, "notifications", i)
            target_path = os.path.join(templates_goes_to, i)
            if not os.path.isdir(path_in_kit) or os.path.exists(target_path):
                continue
            shutil.copytree(path_in_kit, target_path)

    def update_additional_config_files(self, lcc, url, url_ssl):
        pass


    def platform_cmd_consumer_as(self, name = None, component_name = None, selector_name = None):
        # give a chance to use different name with the platform command consumer AS, particularly
        # Dogear is an issue, it is not fully renamed to "bookmarks"
        name_in_title = name or self.name
        component_name = component_name or self.component_name
        selector_component = selector_name or component_name
        spec = {"name": "%s Platform Commands Consumer AS" % name_in_title,
             "jndiName": "jms/connections/%s/command/consumer/as" % component_name,
             "destinationJndiName": "jms/connections/command/consumer/topic",
             "destinationType": "Topic",
             "messageSelector": "%s_command='true'" % selector_component,
             "maxConcurrency": 1,
             "clientId": "commands",
             "subscriptionDurability": "Durable",
             "subscriptionName": self.component_name,
             "durableSubscriptionHome": "REPLACE_WITH_ENGINE_NAME",
             "targetType": "ME", "target": "REPLACE_WITH_ENGINE_NAME",
             "targetSignificance": "Required",
             "authenticationAlias": self.conn_admin_alias,
             "shareDurableSubscriptions": "InCluster" }
        return spec

    def events_consumer_as(self):
        spec = {"name":"%s Event Consumer AS" % self.name,
             "jndiName":"jms/connections/%s/events/consumer/as" % self.component_name,
             "destinationJndiName":"jms/connections/%s/events/consumer/topic" % self.component_name,
             "messageSelector": "%s='true'" % self.component_name,
             "clientId": "internal",
             "subscriptionDurability": "Durable",
             "subscriptionName": self.component_name,
             "destinationType": "Topic",
             "durableSubscriptionHome": "REPLACE_WITH_ENGINE_NAME",
             "targetType": "ME", "target": "REPLACE_WITH_ENGINE_NAME",
             "targetSignificance": "Required",
             "shareDurableSubscriptions": "InCluster" }
        return spec

    def notification_consumer_as(self):
        spec = {"name":"%s Notification Event Consumer AS" % self.name,
             "jndiName":"jms/connections/%s/notification/consumer/as" % self.component_name,
             "destinationJndiName":"jms/connections/%s/events/consumer/topic" % self.component_name,
             "messageSelector": "%s_notification='true'" % self.component_name,
             "clientId": "internal",
             "subscriptionDurability": "Durable",
             "subscriptionName": "%s_notification" % self.component_name,
             "destinationType": "Topic",
             "durableSubscriptionHome": "REPLACE_WITH_ENGINE_NAME",
             "targetType": "ME", "target": "REPLACE_WITH_ENGINE_NAME",
             "targetSignificance": "Required",
             "shareDurableSubscriptions": "InCluster" }
        return spec

    def mailin_consumer_as(self, name = None, component_name = None):
        name_in_title = name or self.name
        component_name = component_name or (name and name.lower()) or self.component_name
        spec = {"name":"Mail-in %s AS" % name_in_title,
             "jndiName":"jms/connections/mailin/%s/as" % component_name,
             "destinationJndiName":"jms/connections/mailin/%s/topic" % component_name,
             "clientId": "mailin",
             "subscriptionDurability": "Durable",
             "subscriptionName": component_name,
             "destinationType": "Topic",
             "durableSubscriptionHome": "REPLACE_WITH_ENGINE_NAME",
             "shareDurableSubscriptions": "InCluster" }
        return spec

    def mailin_queue_as(self, name = None, component_name = None):
        name_in_title = name or self.name
        name_in_jndi = component_name or (name and name.lower()) or self.component_name
        spec = {"name":"Mail-In %s AS" % name_in_title,
             "jndiName":"jms/connections/mailin/%s/as" % name_in_jndi,
             "destinationJndiName":"jms/connections/mailin/%s/queue" % name_in_jndi,
             "destinationType": "Queue"}
        return spec

    def deleted_events_consumer_as(self):
        spec = {"name":"%s Deleted Event Consumer AS" % self.name,
             "jndiName":"jms/connections/%s/deleted/consumer/as" % self.component_name,
             "destinationJndiName":"jms/connections/%s/events/consumer/topic" % self.component_name,
             "messageSelector": "%s_deleted='true'" % self.component_name,
             "clientId": "internal",
             "subscriptionDurability": "Durable",
             "subscriptionName": "%s_deleted" % self.component_name,
             "destinationType": "Topic",
             "maxConcurrency": "3",
             "durableSubscriptionHome": "REPLACE_WITH_ENGINE_NAME",
             "targetType": "ME", "target": "REPLACE_WITH_ENGINE_NAME",
             "targetSignificance": "Required",
             "shareDurableSubscriptions": "InCluster" }
        return spec

    def laydown_web_resources(self, d = None):
        web_res_dir = d or ("%s.provision.web" % self.component_name)
        src_path = os.path.join(self.kit_dir, web_res_dir)
        if not os.path.exists(src_path):
            return
        if not os.path.exists(self.webresources_dir):
            os.makedirs(self.webresources_dir)
        print "Copying web resouces from %s to %s" % (src_path, self.webresources_dir)
        for f in os.listdir(src_path):
            ver_pattern = r'[\d]+\.[\d]+\.[\d]+.[\d]{8}-[\d]{4}'
            pattern = re.sub(ver_pattern, '*', f)
            old_files = glob.glob(os.path.join(self.webresources_dir, pattern))
            for old_f in old_files:
                print " --", os.path.basename(old_f)
                if os.path.isdir(old_f):
                    shutil.rmtree(old_f)
                else:
                    os.remove(old_f)
            print " ++", f
            to_copy = os.path.join(src_path, f)
            if os.path.isdir(to_copy):
                shutil.copytree(to_copy, os.path.join(self.webresources_dir, f))
            else:
                shutil.copy2(to_copy, self.webresources_dir)

    # print message with password removed - with limitations!
    # an light weight way to remove password from a relatively short string and
    # print, to avoid passwords be seen in the log files. Known isssue would be
    # if the actual password string is very generic word, and all the occurance
    # of that 'word' will be replaced.
    def print_with_password_removed(self, x):
        text = str(x)
        passwords = [ self.conn_admin_password,
            self.filenet_admin_pswd,
            self.cognos_admin_password ]
        for pswd in passwords:
            text = text.replace("'%s'" % pswd, "'********'")
        print text


class Blogs(LotusConnections):
    def setup_names(self):
        self.ejb_webui_name = "Roller Weblogger"
        self.ejb_webui_xml = "blogs.war,WEB-INF/web.xml"
        self.data_source_spec['ref'] = "jdbc/rollerdb"
        self.data_source_spec['jndi'] = "jdbc/blogs"
        self.default_db_user['Oracle'] = "BLOGSUSER"
        self.default_db_user['SQL Server'] = "BLOGSUSER"
        self.default_db_name['Oracle'] = "lsconn"
        self.mime_specs = { "image/png":"png;PNG" }
        self.activation_specs.append(self.platform_cmd_consumer_as())
        self.has_new_3_0_events_publisher = 1
        self.has_platform_command_consumer = 1
        self.oauth_urls = [ '/blogs/oauth' ]
        self.notification_sources = ['Blogs']
        self.known_roles = ["person","everyone","metrics-reader","admin","global-moderator","search-admin","widget-admin","reader","bss-provioning-admin"]

    def do_apply_install_options(self, opts):
        self.was_variables["BLOGS_CONTENT_DIR"] = self.data_dir + "/blogs/upload"
        self.roles_map["search-admin"] = ['No', 'No', "|".join(self.search_admin), "|".join(self.search_admin_groups)]
        self.roles_map["widget-admin"] = ['No', 'No', "|".join(self.widget_admin), "|".join(self.widget_admin_groups)]
        self.roles_map["admin"] = ['No', 'No', "|".join(self.administrators), "|".join(self.administrator_groups)]
        self.roles_map["global-moderator"] = ['No', 'No', "|".join(self.global_moderators), "|".join(self.global_mod_groups)]
        if self.multitenant:
            self.roles_map["everyone"] = ['No','Yes', '','']
            self.roles_map["reader"] = ['No','Yes','','']
            if self.bss_provisioning_admin_user:
                self.roles_map["bss-provisioning-admin"] = ['No', 'No', self.bss_provisioning_admin_user, '']

    def customize_datasource(self, data_source):
        if self.db_type == "DB2":
            data_source.properties["currentSchema"] = "BLOGS"

    def update_additional_config_files(self, lcc, url, url_ssl):
        if self.enable_moderation:
            self.update_content_review_cfg()

    def update_content_review_cfg(self):
        f_path = self.make_sure_config_file_in_place("contentreview-config.xml")
        f = XmlFile(f_path)
        xpaths = [
            '/config/serviceConfiguration/service[@id="blogs"]/contentApproval/@enabled',
            '/config/serviceConfiguration/service[@id="blogs"]/contentApproval/ownerModerate/@enabled',
            '/config/serviceConfiguration/service[@id="blogs"]/contentFlagging/@enabled',
            '/config/serviceConfiguration/service[@id="blogs"]/contentFlagging/issueCategorization/@enabled'
            ]
        for p in xpaths:
            f.modify(p, 'true')
        f.save()

class Dogear(LotusConnections):
    def setup_names(self):
        self.ejb_webui_name = "Dogear Application"
        self.ejb_webui_xml = "dogear.webui.war,WEB-INF/web.xml"
        self.default_db_user['Oracle'] = 'DOGEARUSER'
        self.default_db_user['SQL Server'] = 'DOGEARUSER'
        self.default_db_name['Oracle'] = 'lsconn'
        self.mime_specs = { "image/png":"png;PNG" }
        self.replication_domains = [{"name": "ConnectionsReplicationDomain"}]
        self.cache_specs = [
            {"name": "dogear.freshness",
             "jndiName": "services/cache/dogear.freshness",
             "cacheSize": 2000, "defaultPriority": 1,
             "enableCacheReplication": "true",
             "replicationType": "NONE",
             "cacheReplication": [["messageBrokerDomainName", "ConnectionsReplicationDomain"]] }]
        self.activation_specs.append(self.platform_cmd_consumer_as("Bookmarks", "bookmarks"))
        self.has_new_3_0_events_publisher = 1
        self.has_platform_command_consumer = 1
        self.name_in_platform_consumer_as_jndi = "bookmarks"
        self.notifications_templates_dirname = "bookmarks"
        self.oauth_urls = [ '/dogear/oauth' ]
        self.notification_sources = ['dogear']
        self.known_roles = ["person","everyone","reader","metrics-reader","search-admin"]

    def do_apply_install_options(self, opts):
        self.was_variables["DOGEAR_FAVICON_DIR"] = self.data_dir + "/dogear/favorite"
        self.roles_map["search-admin"] = ['No', 'No', "|".join(self.search_admin), "|".join(self.search_admin_groups)]

    def update_additional_config_files(self, lcc, url, url_ssl):
        self.make_sure_config_file_in_place("dogear-config-cell.xsd")
        f_path = self.make_sure_config_file_in_place("dogear-config-cell.xml")
        f = XmlFile(f_path)
        f.modify("/tns:config/tns:contextParameters/tns:stringProperty[@name=\"platform\"]/text()", "WAS")
        f.save()


class Activities(LotusConnections):
    def setup_names(self):
        self.ejb_webui_name = "Activities Web UI"
        self.ejb_webui_xml = "oawebui.war,WEB-INF/web.xml"
        self.default_db_name['DB2'] = "OPNACT"
        self.default_db_name['SQL Server'] = "OPNACT"
        self.default_db_name['Oracle'] = "lsconn"
        self.default_db_user['SQL Server'] = "OAUSER"
        self.default_db_user['Oracle'] = "OAUSER"
        self.mime_specs = { "image/png":"png;PNG" }
        self.res_env_entries = [
           {"name": "QuickrWhitelistProvider",
            "jndiName": "qkrenv/ecm/whitelist",
            "referenceable": "com.ibm.lotus.quickr.ecm.provider.WASResEnvProvider" }]
        self.activation_specs.append(self.platform_cmd_consumer_as())
        self.scheduler_specs = [
           {"name":"ActivitiesScheduler",
            "description":"Activities Scheduler",
            "jndiName":"scheduler/activities",
            "datasourceJNDIName": self.data_source_spec['jndi'],
            "datasourceAlias":"activitiesJAASAuth",
            "pollInterval":"30",
            "tablePrefix":"ACTIVITIES.OA_SCHEDULER",
            "useAdminRoles":"true",
            "workManagerInfoJNDIName":"wm/activities"} ]
        self.wm_specs = [
           {"name":"ActivitiesWorkManager",
            "category":"ActivitiesWorkManager",
            "description":"Activities Work Manager",
            "jndiName":"wm/activities",
            "maxThreads":10,
            "minThreads":1,
            "numAlarmThreads":5,
            "isGrowable":"false",
            "threadPriority":5,
            "workReqQSize":10,
            "workReqQFullAction":1 } ]
        self.has_new_3_0_events_publisher = 1
        self.has_platform_command_consumer = 1
        self.has_following_ejb = 1
        self.has_scheduled_task_ejb = 1
        self.has_notification_sink_ejb = 1
        self.require_startup_beans_service = 1
        self.oauth_urls = [ '/activities/oauth' ]
        self.notification_sources = ['Activities']
        self.known_roles = ["person","everyone","reader","metrics-reader",
                            "search-admin","widget-admin","admin","bss-provisioning-admin"]

    def do_apply_install_options(self, opts):
        self.was_variables["ACTIVITIES_CONTENT_DIR"] = self.data_dir + "/activities/content"
        self.was_variables["ACTIVITIES_STATS_DIR"] = self.data_dir + "/activities/statistic"
        self.roles_map["search-admin"] = ['No', 'No', "|".join(self.search_admin), "|".join(self.search_admin_groups)]
        self.roles_map["widget-admin"] = ['No', 'No', "|".join(self.widget_admin), "|".join(self.widget_admin_groups)]
        admins = unique(self.administrators + [self.conn_admin_user])
        self.roles_map["admin"] = ['No', 'No', "|".join(admins), "|".join(self.administrator_groups)]
        if self.multitenant and self.bss_provisioning_admin_user:
            self.roles_map["bss-provisioning-admin"] = ['No', 'No', self.bss_provisioning_admin_user, '']

    def update_additional_config_files(self, lcc, url, url_ssl):
        self.make_sure_config_file_in_place('oa-config.xsd')
        f_path = self.make_sure_config_file_in_place('oa-config.xml')
        if self.multitenant:
            self.update_config_xml_for_multitenant(f_path)
   
    def update_config_xml_for_multitenant(self,xml_file_path):
        f = XmlFile(xml_file_path)
        path_sref="/config"
        f.modify(path_sref + "/Cache/@noStore","true")
        f.modify(path_sref + "/Autosave/@interval","5")
        f.appendText(path_sref,"\n    ")
        f.appendChild(path_sref,"<FederatedSearchOptions disabled='true'/>")
        f.appendText(path_sref,"\n\n    ")
        f.appendComment(path_sref, "Organization enabled specifying that we have an organization present")
        f.appendText(path_sref,"\n    ")
        f.appendChild(path_sref, "<orgEnabled enabled='true'/>")
        f.appendText(path_sref,"\n\n    ")
        f.appendComment(path_sref, "Organization is in the public scope")
        f.appendText(path_sref,"\n    ")
        f.appendChild(path_sref,"<orgIsPublic enabled='true'/>")
        f.appendText(path_sref,"\n\n    ")
        f.appendComment(path_sref, "Organization is in the public scope")
        f.appendText(path_sref,"\n    ")
        f.appendChild(path_sref,"<businessOwnerEnabled enabled='true' />")
        f.appendText(path_sref,"\n    ")
        f.appendChild(path_sref,"<isMultiTenantEnabled enabled='true'/>")
        f.appendText(path_sref,"\n\n    ")
        f.appendComment(path_sref,"Configuration Enabled for Multi-Tenant")
        f.appendText(path_sref,"\n\n")
        f.save()          

class Profiles(LotusConnections):
    def setup_names(self):
        self.services_provides = [ 'profiles', 'personTag' ]
        self.ejb_webui_name = "Profiles"
        self.ejb_webui_xml = "lc.profiles.app.war,WEB-INF/web.xml"
        self.default_db_name['DB2'] = "PEOPLEDB"
        self.default_db_name['SQL Server'] = "PEOPLEDB"
        self.default_db_user['SQL Server'] = "PROFUSER"
        self.default_db_user['Oracle'] = "PROFUSER"
        self.mime_specs = { "image/png":"png;PNG" }
        self.activation_specs.append(self.platform_cmd_consumer_as())
        self.wm_specs = [
           {"name":"ProfilesWorkManager",
            "category":"ProfilesWorkManager",
            "jndiName":"workmanager/profiles",
            "description":"Profiles Work Manager",
            "serviceNames":"security",
            "maxThreads":1 , "minThreads":1 , "numAlarmThreads":1 , "isGrowable":"false",
            "threadPriority":1, "workReqQSize":1, "workReqQFullAction":1 }]
        self.scheduler_specs = [
           {"name":"ProfilesScheduler",
            "description":"Profiles Scheduler",
            "jndiName":"scheduler/profiles",
            "datasourceJNDIName": self.data_source_spec['jndi'],
            "datasourceAlias":"profilesJAASAuth",
            "tablePrefix":"EMPINST.PROFILES_SCHEDULER_",
            "pollInterval":"30",
            "useAdminRoles":"true",
            "workManagerInfoJNDIName":"workmanager/profiles"}
           ]
        self.has_new_3_0_events_publisher = 1
        self.has_scheduled_task_ejb = 1
        self.has_notification_sink_ejb = 1
        #self.needs_xa_type_jdbc_provider = 1
        self.require_startup_beans_service = 1
        self.oauth_urls = [ '/profiles/oauth' ]
        self.notification_sources = ['Profiles']
        self.known_roles = ["everyone","reader","person","allAuthenticated","metrics-reader","admin","search-admin","dsx-admin","org-admin","bss-provisioning-admin"]

    def do_apply_install_options(self, opts):
        self.was_variables["PROFILES_CACHE_DIR"] = self.data_dir + "/profiles/cache"
        self.was_variables["PROFILES_STATS_DIR"] = self.data_dir + "/profiles/statistic"
        self.was_variables["PROFILES_EVENT_CONTENT_DIR"] = self.data_dir + "/profiles/events"
        self.roles_map["dsx-admin"]    = ['No', 'No', self.conn_admin_user, '']
        self.roles_map["search-admin"] = ['No', 'No', "|".join(self.search_admin), "|".join(self.search_admin_groups)]
        admins = unique(self.administrators + [self.conn_admin_user, self.cognos_admin_user])
        self.roles_map["admin"] = ['No', 'No', "|".join(admins), "|".join(self.administrator_groups)]
        if self.multitenant:
            self.roles_map["everyone"] = ['No','Yes', '','']
            self.roles_map["reader"] = ['No','Yes','','']
            if self.bss_provisioning_admin_user:
                self.roles_map["bss-provisioning-admin"] = ['No', 'No', self.bss_provisioning_admin_user, '']

    def customize_datasource(self, data_source):
        if self.db_type == "DB2":
            data_source.properties["currentSchema"] = "EMPINST"

    def update_additional_config_files(self, lcc, url, url_ssl):
        # enable WPI
        sref_path = "/tns:config/sloc:serviceReference[@serviceName=\"directory\"]"
        if self.enable_wpi:
            lcc.modify(sref_path + "/@profiles_directory_service_extension_enabled", "true")
        lcc.modify(sref_path + "/@profiles_directory_service_extension_href",
            "%s/profiles/dsx/" % url )
        files = ['profiles-config.xsd', 'profiles-config.xml',
                 'profiles-policy.xsd', 'profiles-policy.xml',
                 'profiles-types.xsd', 'profiles-types.xml',
                 'profiles-extensions', 'profiles' ]
        for f in files:
            self.make_sure_config_file_in_place(f)


class Homepage(LotusConnections):
    def setup_names(self):
        self.ejb_webui_name = "Homepage"
        self.ejb_webui_xml = "homepage.war,WEB-INF/web.xml"
        self.activation_specs.append(self.platform_cmd_consumer_as())
        self.mime_specs = { "image/png":"png;PNG" }
        self.has_new_3_0_events_publisher = 1
        self.oauth_urls = [ '/homepage/oauth' ]
        self.known_roles = ["person","everyone","reader","metrics-reader","admin"]

    def do_apply_install_options(self, opts):
        self.roles_map['reader'] = [ 'No', 'Yes', '', '']
        admins = unique(self.administrators + [self.conn_admin_user])
        self.roles_map["admin"] = ['No', 'No', "|".join(admins), "|".join(self.administrator_groups)]

    def customize_datasource(self, data_source):
        if self.db_type == "DB2":
            data_source.properties["currentSchema"] = "HOMEPAGE"

    def ear_opts_MapEJBRefToEJB(self):
        opt  = LotusConnections.ear_opts_MapEJBRefToEJB(self)
        refs = opt[1]
        refs.append(
            [self.ejb_webui_name,
             "",
             self.ejb_webui_xml,
             "NewsStoryBean",
             "com.ibm.lconn.news.ejb.client.NewsStoryEJBBean",
             "ejb/connections/news/stories"])
        return opt

    def update_additional_config_files(self, lcc, url, url_ssl):
        files = [ 'gettingstarted-config.xsd', 'gettingstarted-config.xml',
                  'mime-files-config.xsd', 'mime-files-config.xml' ]
        for f in files:
            self.make_sure_config_file_in_place(f)


class Search(LotusConnections):
    def setup_names(self):
        self.starting_weight = 2
        self.ejb_webui_name = "search.indexer"
        self.ejb_webui_xml = "dboard.search.ejb.jar,META-INF/ejb.jar.xml"
        self.default_db_name['DB2'] = "HOMEPAGE"
        self.default_db_name['SQL Server'] = "HOMEPAGE"
        self.default_db_user["Oracle"] = "HOMEPAGEUSER"
        self.default_db_user["SQL Server"] = "HOMEPAGEUSER"
        self.wm_specs = [
           {"name":"SearchWorkManager",
            "category":"SearchWorkManager",
            "jndiName":"wm/search",
            "description":"Search Work Manager" },
           {"name":"SearchIndexingWorkManager",
            "category":"SearchIndexingWorkManager",
            "jndiName":"wm/search-indexing",
            "workReqQFullAction":0,
            "maxThreads":2,
            "minThreads":1,
            "numAlarmThreads":1,
            "isGrowable":"false",
            "threadPriority":1,
            "workReqQSize":12,
            "description":"Search Indexing Work Manager" },
           {"name":"SearchCrawlingWorkManager",
            "category":"SearchCrawlingWorkManager",
            "jndiName":"wm/search-crawling",
            "description":"Search Crawling Work Manager" },
           {"name":"SearchDCSWorkManager",
            "category":"SearchDCSWorkManager",
            "jndiName":"wm/search-dcs",
            "description":"Search DCS Work Manager",
            "workReqQFullAction":0 }
           ]
        self.scheduler_specs = [
           {"name":"LotusConnectionsScheduler",
            "description":"Search Scheduler",
            "jndiName":"scheduler/search",
            "datasourceJNDIName": self.data_source_spec['jndi'],
            "datasourceAlias":"searchJAASAuth",
            "tablePrefix":"HOMEPAGE.LOTUSCONNECTIONS",
            "pollInterval":"30",
            "useAdminRoles":"true",
            "workManagerInfoJNDIName":"wm/search"}
           ]
        self.si_bus_dest_specs = [
            { "name":"connections.search.topic",
              "type":"TopicSpace" } ]
        self.cf_specs = [{"name": "Search TCF", "type":"topic",
            "jndiName": "jms/connections/search/tcf"}]
        self.jms_topic_specs = [
            {"name":"Search Indexer Topic",
             "scope": "CELL",
             "topicSpace":"connections.search.topic",
             "topicName":"connections.search.topic",
             "jndi":"jms/connections/search/topic" },
            {"name":"Quick Result Event Consumer Topic",
             "scope": "CELL",
             "topicSpace":"connections.events",
             "topicName":"//.",
             "jndi":"jms/connections/quickresult/events/consumer/topic" }]
        self.activation_specs = [
            {"name": "Search Indexer Activation Specification",
             "jndiName": "jms/connections/search/as",
             "destinationJndiName": "jms/connections/search/topic",
             "alwaysActivateAllMDBs": "true",
             "destinationType": "Topic" }]
        self.activation_specs.append(self.platform_cmd_consumer_as("Global Search"))
        self.activation_specs.append(self.quickresult_events_consumer_as())
        self.has_new_3_0_events_publisher = 1
        self.require_startup_beans_service = 1
        self.oauth_urls = [ '/search/oauth' ]
        self.known_roles = ["person","everyone","reader","metrics-reader","admin","search-admin","everyone-authenticated","status-reader"]

    def quickresult_events_consumer_as(self):
        name = "quickresult"
        spec = {"name":"%s Event Consumer AS" % name,
             "jndiName":"jms/connections/%s/events/consumer/as" % name,
             "destinationJndiName":"jms/connections/%s/events/consumer/topic" % name,
             "messageSelector": "%s='true'" % name,
             "clientId": "internal",
             "subscriptionDurability": "Durable",
             "subscriptionName": name,
             "destinationType": "Topic",
             "durableSubscriptionHome": "REPLACE_WITH_ENGINE_NAME",
             "targetType": "ME", "target": "REPLACE_WITH_ENGINE_NAME",
             "targetSignificance": "Required",
             "shareDurableSubscriptions": "InCluster" }
        return spec

    def tools_dir(self):
        if self.smartCloud:
            return self.data_dir_local
        else:
            return self.data_dir

    def do_apply_install_options(self, opts):
        self.was_variables["EXTRACTED_FILE_STORE"] = "%s/search/extracted" % self.data_dir
        self.was_variables["SEARCH_INDEX_DIR"] = "%s/search/index" % self.data_dir_local
        self.was_variables["SEARCH_INDEX_BACKUP_DIR"] = "%s/search/index_backup" % self.data_dir_local
        self.was_variables["SEARCH_INDEX_SHARED_COPY_LOCATION"] = "%s/search/staging" % self.data_dir
        self.was_variables["CRAWLER_PAGE_PERSISTENCE_DIR"] = "%s/search/persistence" % self.data_dir_local
        # TODO: this should changes depends on platforms, on windows it is exporter.exe
        self.was_variables["SEARCH_DICTIONARY_DIR"] = "%s/search/dictionary" % self.tools_dir()
        self.was_variables["LD_LIBRARY_PATH"] = "%s/search/stellent/dcs/oiexport" % self.tools_dir()
        self.was_variables["FILE_CONTENT_CONVERSION"] = "%s/search/stellent/dcs/oiexport/exporter" % self.tools_dir()
        self.env_variables["LD_LIBRARY_PATH"] = "%s/search/stellent/dcs/oiexport" % self.tools_dir()
        self.env_variables["LIBPATH"] = "%s/search/stellent/dcs/oiexport" % self.tools_dir()
        self.env_variables["PATH"] = "%s/search/stellent/dcs/oiexport" % self.tools_dir()
        admins = unique(self.administrators + [self.conn_admin_user])
        self.roles_map["admin"] = ['No', 'No', "|".join(admins), "|".join(self.administrator_groups)]
        self.run_as_roles = [["admin", self.conn_admin_user, self.conn_admin_password]]

    def customize_datasource(self, data_source):
        if self.db_type == "DB2":
            data_source.properties["currentSchema"] = "HOMEPAGE"

    def mdb_jndi_bindings(self):
        return [ [self.ejb_webui_name,
             'IndexTopicMDB',
             self.ejb_webui_xml,
             '',
             'jms/connections/%s/as' % self.component_name,
             'jms/connections/%s/topic' % self.component_name,
             self.conn_admin_alias ] ]

    def ear_opts_MapEJBRefToEJB(self):
        opts = [['Search',
            '',
            'search.war,WEB-INF/web.xml',
            'ejb/EventPublisher',
            'com.ibm.lconn.events.internal.publish.impl.JMSPublisherLocal',
            'ejb/connections/search/events/publisher']]
        return ['-MapEJBRefToEJB', opts]

    def ear_opts_MapResRefToEJB(self):
        refs = [
            [self.ejb_webui_name,
             "ScheduleStartup",
             self.ejb_webui_xml,
             self.data_source.jndi,
             "javax.sql.DataSource",
             self.data_source.jndi,
             "DefaultPrincipalMapping",
             self.data_source.auth.name ],
            [self.ejb_webui_name,
             "IndexTopicMDB",
             self.ejb_webui_xml,
             "jms/connectionsSearchTopic",
             "javax.jms.Topic",
             "jms/connections/search/topic"],
            [self.ejb_webui_name,
             "IndexTopicMDB",
             "dboard.search.ejb.jar,META-INF/ejb.jar.xml",
             "jms/connectionsSearchTCF",
             "javax.jms.TopicConnectionFactory",
             "jms/connections/search/tcf",
             "DefaultPrincipalMapping",
             self.data_source.auth.name ]]
        return [ '-MapResRefToEJB', refs ]

    def ear_opts_MapResEnvRefToRes(self):
        refs = [
            ["Search",
             "",
             "search.war,WEB-INF/web.xml",
             self.data_source.jndi,
             "javax.sql.DataSource",
             self.data_source.jndi,
             self.data_source.auth.name ]]
        return [ '-MapResEnvRefToRes', refs ]

    def additional_post_install(self):
        try:
            if not os.path.exists(self.app_home):
                os.makedirs(self.app_home)
            self.create_index_dir()
            self.copy_dictionaries()
            self.copy_stellent()
        except OSError, msg:
            raise LcError, "failed copy search dictionaries or DCS files due to OSError: %s" % msg

    def create_index_dir(self):
        for d in ['index', 'staging']:
            dir_to_create = os.path.join(self.data_dir, "search", d)
            if os.path.exists(dir_to_create):
                print "Remove existing search index at [%s]" % dir_to_create
                shutil.rmtree(dir_to_create)
            os.makedirs(dir_to_create)
        for d in ['index_backup', 'persistence']:
            dir_to_create = os.path.join(self.data_dir_local, "search", d)
            if not os.path.exists(dir_to_create):
                os.makedirs(dir_to_create)

    def copy_dictionaries(self):
        to_path = os.path.join(self.tools_dir(), "search", "dictionary")
        if os.path.exists(to_path):
            shutil.rmtree(to_path)
        print "Copy dictionaries to", to_path
        shutil.copytree(os.path.join(self.kit_dir, "dictionaries"), to_path)

    def copy_stellent(self):
        os_name = java.lang.System.getProperty("os.name")
        os_arch = java.lang.System.getProperty("os.arch")
        if os_name == "Linux":
            os_dir = "Linux_32"
            if os_arch == "x86_64" or os_arch == "amd64":
                os_dir = "Linux_64"
            elif os_arch == "s390x":
                os_dir = "Zlinux"
        elif os_name.startswith("Windows"):
            os_dir = "Windows_32"
            if os_arch in ["x64", "amd64"]:
                os_dir = "Windows_64"
        elif os_name == "AIX":
            os_dir = "AIX_64"
        else:
            print "WARNING: Stellent skipped due to unknown OS [%s] on arch=[%s]" % (os_name, os_arch)
            return 0
        copy_to_dir = os.path.join(self.tools_dir(), "search", "stellent")
        if not os.path.exists(copy_to_dir): os.makedirs(copy_to_dir)
        copy_to_dir = os.path.join(copy_to_dir, "dcs")
        if os.path.exists(copy_to_dir):
            shutil.rmtree(copy_to_dir)
        print "Copy DCS binaries to", copy_to_dir
        shutil.copytree(os.path.join(self.kit_dir, "dcs", "stellent", os_dir),
                        copy_to_dir)
        # unfortunately the Jython comes with WAS 6.1 does not preserve the
        # permission bits with its shutil.copytree()
        if os_name == "Linux" or os_name == "AIX":
            cmd = "chmod +x %s/oiexport/*" % copy_to_dir
            print "Run:", cmd
            os.system(cmd)
        self.update_was_setup_cmd_line_script()

    def update_was_setup_cmd_line_script(self):
        if isinstance(self.install_to, ServerCluster):
            print "NOTE: with ND deployment, we are not able to update the setupCmdLine.sh file."
            return
        script = java.lang.System.getenv('WAS_USER_SCRIPT')
        dcs_path = os.path.join(self.app_home, "dcs", "oiexport")
        if script[-3:len(script)] == '.sh':
            self.add_dcs_to_shell_script(script,
                {"LD_LIBRARY_PATH": dcs_path,
                 "PATH": dcs_path})
        else:
            print "update setupCmdLine.bat for Windows not yet implemented"

    def add_dcs_to_shell_script(self, script, env_vars):
        print "Checking %s ..." % script
        f = open(script, "r")
        for line in f.readlines():
            hash_at = line.find("#")
            if hash_at >= 0:
                line = line[0:hash_at]
            for n,v in env_vars.items():
                pattern = ("\\A(export\\s+|)%s=.*" % n) + v.replace("\\", "\\\\")
                #print "regex patter: '%s'" % pattern
                regex_obj = re.compile(pattern)
                if regex_obj.match(line):
                    print "  %s already set in line: %s" % (n, line)
                    del env_vars[n]
        f.close()
        f = open(script, "a")
        for n,v in env_vars.items():
            print "  adding: export %s=${%s}${%s:+:}\"%s\"" % (n, n, n, v)
            f.write("export %s=${%s}${%s:+:}\"%s\"\n" % (n, n, n, v))
        f.close()

    def update_additional_config_files(self, lcc, url, url_ssl):
        files = ['search-config.xsd', 'search-config.xml']
        for f in files:
            self.make_sure_config_file_in_place(f)


class News(LotusConnections):
    def setup_names(self):
        self.starting_weight = 1
        self.services_provides = ['news', 'sand', 'mediaGallery']
        self.ejb_webui_name = "News Aggregation service"
        self.ejb_webui_xml = "news.web.war,WEB-INF/web.xml"
        self.modules_settings["news.spring.context.jar"] = {'startingWeight': 1000}
        self.modules_settings["news.consumer.jar"] = {'startingWeight': 20000}
        self.default_db_name['DB2'] = "HOMEPAGE"
        self.default_db_name['SQL Server'] = "HOMEPAGE"
        self.default_db_user["Oracle"] = "HOMEPAGEUSER"
        self.default_db_user["SQL Server"] = "HOMEPAGEUSER"
        self.wm_specs = [
           {"name":"NewsWorkManager",
            "maxThreads":10,
            "numAlarmThreads":9,
            "category":"NewsWorkManager",
            "description":"News Work Manager",
            "jndiName":"wm/news"},
           {"name": "External Event Consumer WorkManager",
            "scope": "CELL",
            "category": "EventConsumerManager",
            "description": "News Work Manager",
            "maxThreads": 20 ,
            "jndiName": "wm/connections/events/external"} ]
        self.scheduler_specs = [
           {"name":"NewsScheduler",
            "description":"News Scheduler",
            "jndiName":"scheduler/news",
            "datasourceJNDIName": self.data_source_spec['jndi'],
            "datasourceAlias":"newsJAASAuth",
            "pollInterval":"30",
            "tablePrefix":"HOMEPAGE.NR_SCHEDULER_",
            "useAdminRoles":"false",
            "workManagerInfoJNDIName":"wm/news"},
            ]
        self.si_bus_dest_specs = [
            { "name":"connections.events", "type":"TopicSpace" },
            { "name":"connections.platformCommands", "type":"TopicSpace", "reliability": "ASSURED_PERSISTENT"},
            { "name": "connections.mailin.exception", "type": "Queue", "reliability": "ASSURED_PERSISTENT"},
            { "name": "activitystreamsearch.topic.space", "type": "TopicSpace" },
            { "name": "connections.mailin", "type": "TopicSpace",
              "exceptionDestination": "connections.mailin.exception",
              "reliability": "ASSURED_PERSISTENT"} ]
        self.cf_specs = [
            { "name": "Events Temp QCF", "jndiName": "jms/connections/news/events/temp/qcf" },
            { "scope": "CELL", "name": "PlatformCommand TCF", "type": "topic",
              "jndiName": "jms/connections/profiles/command/tcf",
              "authDataAlias": self.conn_admin_alias, "xaRecoveryAuthAlias": self.conn_admin_alias,
              "clientID": "external",
              "shareDurableSubscriptions": "InCluster",
              "durableSubscriptionHome": "REPLACE_WITH_ENGINE_NAME",
              "nonPersistentMapping": "AsSIBDestination", "persistentMapping": "AsSIBDestination" },
            { "name": "Events TCF", "scope": "CELL", "type": "topic",
              "jndiName": "jms/connections/news/events/tcf",
              "authDataAlias": self.conn_admin_alias, "xaRecoveryAuthAlias": self.conn_admin_alias,
              "containerAuthAlias": self.conn_admin_alias,
              "clientID": "external",
              "shareDurableSubscriptions": "InCluster",
              "durableSubscriptionHome": "REPLACE_WITH_ENGINE_NAME",
              "connectionPool": { "maxConnections": 75 },
              "nonPersistentMapping": "AsSIBDestination", "persistentMapping": "AsSIBDestination" },
            { "name": "Event Handlers TCF", "type": "topic",
              "scope": "CELL", "jndiName": "jms/connections/news/eventHandlers/tcf",
              "busName": "ConnectionsBus",
              "connectionPool": { "maxConnections": 100 },
              "clientID": "external",
              "readAhead": "Default",
              "durableSubscriptionHome": "REPLACE_WITH_ENGINE_NAME",
              "shareDurableSubscriptions": "InCluster",
              "targetType": "ME", "target": "REPLACE_WITH_ENGINE_NAME",
              "targetSignificance": "Required",
              "connectionProximity": "Bus",
              "mappingAlias": "DefaultPrincipalMapping",
              "containerAuthAlias": self.conn_admin_alias,
              "xaRecoveryAuthAlias": self.conn_admin_alias,
              "nonPersistentMapping": "AsSIBDestination",
              "persistentMapping": "AsSIBDestination"},
            { "name": "Mail-in TCF", "scope": "CELL", "type": "topic",
              "jndiName": "jms/connections/mailin/tcf",
              "authDataAlias": self.conn_admin_alias, "xaRecoveryAuthAlias": self.conn_admin_alias,
              "containerAuthAlias": self.conn_admin_alias,
              "clientID": "mailin",
              "shareDurableSubscriptions": "InCluster",
              "durableSubscriptionHome": "REPLACE_WITH_ENGINE_NAME",
              "connectionPool": { "maxConnections": 30 },
              "nonPersistentMapping": "AsSIBDestination", "persistentMapping": "AsSIBDestination" },
            { "name": "ActivityStreamSearch TCF", "scope": "CELL", "type": "topic",
              "jndiName": "jms/connections/news/search/tcf" }
              ]
        self.jms_queue_specs = [
            {"name":"Events Temp Queue",
             "dest":"connections.events.temp",
             "jndi":"jms/connections/news/events/temp/queue" },
            {"name":"Mail-in Exception Queue",
             "dest":"connections.mailin.exception",
             "jndi":"jms/connections/mailin/exception/queue"} ]
        self.jms_topic_specs = [
            {"name":"News Event Consumer Topic",
             "scope": "CELL",
             "topicSpace":"connections.events",
             "topicName":"//.",
             "jndi":"jms/connections/news/events/consumer/topic" },
            {"name":"Communities Event Consumer Topic",
             "scope": "CELL",
             "topicSpace":"connections.events",
             "topicName":"//.",
             "jndi":"jms/connections/communities/events/consumer/topic" },
            {"name":"Profiles Platform Commands Consumer Topic",
             "scope": "CELL",
             "topicSpace":"connections.platformCommands",
             "topicName":"//.",
             "jndi":"jms/connections/command/consumer/topic" },
            {"name":"Mail-in Forums Topic",
             "scope": "CELL",
             "topicSpace":"connections.mailin",
             "topicName":"forums",
             "jndi":"jms/connections/mailin/forums/topic" },
            {"name":"Mail-in Failed Topic",
             "scope": "CELL",
             "topicSpace":"connections.mailin",
             "topicName":"failed",
             "jndi":"jms/connections/mailin/failed/topic" },
            {"name":"ActivityStreamSearch FollowingService Topic",
             "scope": "CELL",
             "topicSpace":"activitystreamsearch.topic.space",
             "jndi":"jms/connections/news/search/followingservicetopic" },
            {"name":"ActivityStreamSearch Admin Topic",
             "scope": "CELL",
             "topicSpace":"activitystreamsearch.topic.space",
             "jndi":"jms/connections/news/search/admin/topic" },
            {"name":"ActivityStreamSearch System Topic",
             "scope": "CELL",
             "topicSpace":"activitystreamsearch.topic.space",
             "jndi":"jms/connections/news/search/system/topic" }
             ]
        self.activation_specs.append(self.notification_consumer_as())
        self.activation_specs.append(self.deleted_events_consumer_as())
        self.activation_specs.append(self.events_consumer_as())
        self.activation_specs.append(self.platform_cmd_consumer_as())
        self.activation_specs.append(self.mailin_consumer_as('Failed'))
        self.activation_specs.append(self.mailin_queue_as('Exception'))
        self.activation_specs.append(
            {"name": "ActivityStreamSearch Admin AS",
             "jndiName": "jms/connections/news/search/admin/as",
             "destinationJndiName": "jms/connections/news/search/admin/topic",
             "alwaysActivateAllMDBs": "true",
             "destinationType": "Topic" })
        self.activation_specs.append(
            {"name": "ActivityStreamSearch FollowingService AS",
             "jndiName": "jms/connections/news/search/followingserviceas",
             "destinationJndiName": "jms/connections/news/search/followingservicetopic",
             "alwaysActivateAllMDBs": "true",
             "destinationType": "Topic" })
        self.activation_specs.append(
            {"name": "ActivityStreamSearch System AS",
             "jndiName": "jms/connections/news/search/system/as",
             "destinationJndiName": "jms/connections/news/search/system/topic",
             "alwaysActivateAllMDBs": "true",
             "destinationType": "Topic" })
        self.has_new_3_0_events_publisher = 1
        self.has_event_subsriber_ejb = 1
        self.has_platform_command_consumer = 1
        self.has_following_ejb = 1
        self.has_scheduled_task_ejb = 1
        self.has_notification_sink_ejb = 1
        self.require_startup_beans_service = 1
        self.replication_domains = [{"name": "ConnectionsReplicationDomain"}]
        self.cache_specs += self.news_cache_specs()
        self.cache_specs += self.waltz_cache_specs()
        self.oauth_urls = [ '/news/oauth', '/news/follow/oauth' ]
        self.known_roles = ["person","everyone","reader","sharebox-reader","allAuthenticated","admin","search-admin","widget-admin","bss-provisioning-admin"]

    def news_cache_specs(self):
        specs = []
        for i in ["general", "ibatis", "story", "storycontent", "entry", "network", "follows", "person", "emailprefs"]:
            spec = {}
            spec["name"] = "News %s" % i
            spec["jndiName"] = "cache/news/%s" % i
            spec["enableCacheReplication"] = "true"
            spec["cacheSize"] = 5000
            spec["disableDependencyId"] = "false"
            spec["cacheReplication"] = [["messageBrokerDomainName", "ConnectionsReplicationDomain"]]
            specs.append(spec)
        return specs

    def waltz_cache_specs(self):
        specs = []
        waltz_cache_names = [ 'WaltzUnknownCache',
            'WaltzExactDNMatchCache',
            'WaltzExactIDMatchCache',
            'WaltzExactURLMatchCache',
            'WaltzExactURLMatchCache',
            'WaltzExactLoginUserIDMatchCache',
            'WaltzNestedGroupMembershipCache',
            'WaltzDirectMemberExpansionCache',
            'WaltzDirectGroupMembershipCache',
            'WaltzNestedMemberExpansionCache']
        for c in waltz_cache_names:
            specs.append( {"name": c, "scope": "CELL",
                 "jndiName": "com/ibm/connections/directory/services/cache/%s" % c,
                 "cacheSize": 2000, "defaultPriority": 1,
                 "enableCacheReplication": "true", "replicationType": "NONE",
                 "cacheReplication": [["messageBrokerDomainName", "ConnectionsReplicationDomain"]] })
        return specs

    def do_apply_install_options(self, opts):
        self.was_variables["MAILIN_ATTACHMENT_DIR"] = "%s/mailin/attachments" % self.data_dir
        self.was_variables["ACTIVITY_STREAM_SEARCH_REPLICATION_DIR"] = "%s/%s/search/indexReplication" % (self.data_dir, self.component_name)
        self.was_variables["ACTIVITY_STREAM_SEARCH_INDEX_DIR"] = "%s/%s/search/index" % (self.data_dir_local, self.component_name)
        self.was_variables["ACTIVITY_STREAM_SEARCH_INDEX_BACKUP_DIR"] = "%s/%s/search/index_backup" % (self.data_dir_local, self.component_name)
        self.roles_map["reader"] = ['No', 'Yes', '', '']
        admins = unique(self.administrators + [self.conn_admin_user])
        self.roles_map["admin"] = ['No', 'No', "|".join(admins), "|".join(self.administrator_groups)]
        self.roles_map["search-admin"] = ['No', 'No', "|".join(self.search_admin), "|".join(self.search_admin_groups)]
        if self.multitenant and self.bss_provisioning_admin_user:
            self.roles_map["bss-provisioning-admin"] = ['No', 'No', self.bss_provisioning_admin_user, '']

    def customize_datasource(self, data_source):
        if self.db_type == "DB2":
            data_source.properties["currentSchema"] = "HOMEPAGE"

    def ear_opts_BindJndiForEJBNonMessageBinding(self):
        opt  = LotusConnections.ear_opts_BindJndiForEJBNonMessageBinding(self)
        refs = opt[1]
        refs.append(
           ["Spring container launcher",
            "SpringContainerLauncherEjb",
            "news.spring.context.jar,META-INF/ejb-jar.xml",
            "ejb/connections/news/spring"])
        refs.append(
           ["NewsStoryBean",
            "NewsStoryEJBBean",
            "news.ejb.jar,META-INF/ejb-jar.xml",
            "ejb/connections/news/stories"])
        return opt

    def ear_opts_MapResRefToEJB(self):
        return []

    def update_additional_config_files(self, lcc, url, url_ssl):
        files = [ 'events-config.xsd', 'events-config.xml',
                  'XRDS.xml', 'XRDS.xsd', 'XRD.xsd',
                  'news-config.xsd', 'news-config.xml' ]
        for f in files:
            self.make_sure_config_file_in_place(f)

    def additional_post_install(self):
        self.create_as_index_dir()

    def create_as_index_dir(self):
        for d in ['index_backup']:
            dir_to_create = os.path.join(self.data_dir_local,self.component_name, "search", d)
            if not os.path.exists(dir_to_create):
                os.makedirs(dir_to_create)

class Communities(LotusConnections):
    def setup_names(self):
        self.starting_weight = 4
        self.ejb_webui_name = "Communities Web UI"
        self.ejb_webui_xml = "comm.web.war,WEB-INF/web.xml"
        self.data_source_spec['ref'] = "jdbc/sncomm"
        self.data_source_spec['jndi'] = "jdbc/sncomm"
        self.default_db_name['DB2'] = "SNCOMM"
        self.default_db_name['SQL Server'] = "SNCOMM"
        self.default_db_user["Oracle"] = "SNCOMMUSER"
        self.default_db_user["SQL Server"] = "SNCOMMUSER"
        self.si_bus_dest_specs = [
            { "name":"catalog.topic.space", "type":"TopicSpace" } ]
        self.mime_specs = { "image/png":"png;PNG" }
        self.scheduler_specs = [
           {"name":"CommunitiesScheduler",
            "description":"Communities Scheduler",
            "jndiName":"scheduler/communities",
            "datasourceJNDIName": self.data_source_spec['jndi'],
            "datasourceAlias":"communitiesJAASAuth",
            "pollInterval":"30",
            "tablePrefix":"SNCOMM.COMMUNITIESSCHEDULER",
            "useAdminRoles":"true",
            "workManagerInfoJNDIName":"wm/communities"} ]
        self.wm_specs = [
           {"name":"CommunitiesEventQueue",
            "category":"CommunitiesEventQueue",
            "description":"Communities Work Manager",
            "jndiName":"wm/communitiesEventQueue",
            "serviceNames":"security",
            "maxThreads":6,
            "minThreads":0,
            "numAlarmThreads":0,
            "isGrowable":"false",
            "threadPriority":5,
            "workReqQSize":0,
            "workReqQFullAction":0 },
           {"name":"CommunitiesWorkManager",
            "category":"CommunitiesWorkManager",
            "description":"Communities Work Manager",
            "jndiName":"wm/communities",
            "maxThreads":10,
            "minThreads":1,
            "numAlarmThreads":5,
            "isGrowable":"false",
            "threadPriority":5,
            "workReqQSize":10,
            "workReqQFullAction":1 } ]
        self.cf_specs = [{"name": "Catalog TCF", "type":"topic",
            "jndiName": "jms/connections/catalog/tcf"}]
        self.jms_topic_specs = [
            {"name":"Catalog Admin Topic",
             "scope": "CELL",
             "topicSpace":"catalog.topic.space",
             "jndi":"jms/connections/catalog/admin/topic" },
            {"name":"Catalog System Topic",
             "scope": "CELL",
             "topicSpace":"catalog.topic.space",
             "jndi": "jms/connections/catalog/system/topic"}]
        self.activation_specs = [
            {"name": "Catalog Admin AS",
             "jndiName": "jms/connections/catalog/admin/as",
             "destinationJndiName": "jms/connections/catalog/admin/topic",
             "alwaysActivateAllMDBs": "true",
             "destinationType": "Topic" },
            {"name": "Catalog System AS",
             "jndiName": "jms/connections/catalog/system/as",
             "destinationJndiName": "jms/connections/catalog/system/topic",
             "alwaysActivateAllMDBs": "true",
             "destinationType": "Topic" }]
        self.activation_specs.append(self.events_consumer_as())
        self.activation_specs.append(self.platform_cmd_consumer_as())
        self.has_platform_command_consumer = 1
        self.has_new_3_0_events_publisher = 1
        self.has_event_subscriber_ejb = 1
        self.has_following_ejb = 1
        self.has_scheduled_task_ejb = 1
        self.has_notification_sink_ejb = 1
        self.war_class_loader_policy = 'SINGLE'
        self.require_startup_beans_service = 1
        self.oauth_urls = [ '/communities/calendar/oauth', '/communities/service/atom/oauth',
                            '/communities/service/opensocial/oauth', '/communities/recomm/oauth' ]
        self.notification_sources = ['Communities']
        self.known_roles = ["everyone","reader","person","metrics-reader","community-creator",
                            "community-metrics-run","search-admin","global-moderator",
                            "admin","dsx-admin","widget-admin","bss-provisioning-admin"]

    def do_apply_install_options(self, opts):
        self.was_variables["COMMUNITIES_STATS_DIR"] = "%s/communities/statistic" % self.data_dir
        self.was_variables["COMMUNITIES_EVENT_CONTENT_DIR"] = "%s/communities/events" % self.data_dir
        if self.smartCloud:
            print 'Setting catalog indexreplication to: ' + self.data_dir + "/catalog/" + self.topologyName + "/indexReplication"
            self.was_variables["CATALOG_REPLICATION_DIR"] = self.data_dir + "/catalog/" + self.topologyName + "/indexReplication"
        else:
            self.was_variables["CATALOG_REPLICATION_DIR"] = "%s/catalog/indexReplication" % self.data_dir
        self.was_variables["CATALOG_INDEX_DIR"] = "%s/catalog/index" % self.data_dir_local
        self.was_variables["CATALOG_INDEX_BACKUP_DIR"] = "%s/catalog/index_backup" % self.data_dir_local
        self.roles_map["dsx-admin"] = ['No', 'No', self.conn_admin_user, '']
        self.roles_map["widget-admin"] = ['No', 'No', "|".join(self.widget_admin), "|".join(self.widget_admin_groups)]
        self.roles_map["search-admin"] = ['No', 'No', "|".join(self.search_admin), "|".join(self.search_admin_groups)]
        admins = unique(self.administrators + [self.conn_admin_user])
        self.roles_map["admin"] = ['No', 'No', "|".join(admins), "|".join(self.administrator_groups)]
        self.roles_map["global-moderator"] = ['No', 'No', "|".join(self.global_moderators), "|".join(self.global_mod_groups)]
        if self.bss_provisioning_admin_user:
            self.roles_map["bss-provisioning-admin"] = ['No', 'No', self.bss_provisioning_admin_user, '']
        if self.multitenant:
            self.roles_map["everyone"] = ['No','Yes', '','']
            self.roles_map["reader"] = ['No','Yes','','']

    def mdb_jndi_bindings(self):
        bindings = LotusConnections.mdb_jndi_bindings(self)
        info = AdminApp.taskInfo(self.ear_file, "BindJndiForEJBMessageBinding")
        if info.find("EJB: AdminTopicMessageDrivenBean") == -1:
            return bindings
        bindings.append( ['SearchAdminEJB',
             'AdminTopicMessageDrivenBean',
             'catalog.search.mdb.jar,META-INF/ejb-jar.xml',
             '',
             'jms/connections/catalog/admin/as',
             'jms/connections/catalog/admin/topic',
             self.conn_admin_alias ] )
        bindings.append( ['SearchAdminEJB',
             'SystemTopicMessageDrivenBean',
             'catalog.search.mdb.jar,META-INF/ejb-jar.xml',
             '',
             'jms/connections/catalog/system/as',
             'jms/connections/catalog/system/topic',
             self.conn_admin_alias ] )
        return bindings

    def ear_opts_MapResRefToEJB(self):
        options = LotusConnections.ear_opts_MapResRefToEJB(self)
        refs = options[1]
        refs.append(
            [self.ejb_webui_name,
             "",
             self.ejb_webui_xml,
             "wm/communitiesEventQueue",
             "com.ibm.websphere.asynchbeans.WorkManager",
             "wm/communitiesEventQueue",
             "",
             "" ])
        refs.append(
            [self.ejb_webui_name,
             "",
             self.ejb_webui_xml,
             "jms/catalogTCF",
             "javax.jms.TopicConnectionFactory",
             "jms/connections/catalog/tcf",
             "DefaultPrincipalMapping",
             self.conn_admin_alias ])
        return options

    def update_additional_config_files(self, lcc, url, url_ssl):
        # enable WCI
        sref_path = "/tns:config/sloc:serviceReference[@serviceName=\"directory\"]"
        if self.enable_wci:
            lcc.modify(sref_path + "/@communities_directory_service_extension_enabled", "true")
        lcc.modify(sref_path + "/@communities_directory_service_extension_href",
            "%s/communities/dsx/" % url)
        files = ['communities-config.xsd', 'communities-config.xml',
                 'calendar-config.xsd', 'calendar-config.xml',
                 'communities-policy.xsd', 'communities-policy.xml' ]
        for f in files:
            f_path = self.make_sure_config_file_in_place(f)
            if f == 'communities-config.xml' and self.multitenant:
                self.update_config_xml_for_multitenant(f_path)


    def additional_post_install(self):
        # add the media gallery Jaas
        auth = JAASAuthData('filenetAdmin')
        auth.uid = self.filenet_admin
        auth.password = self.filenet_admin_pswd
        auth.description = "JAAS Auth for Filenet"
        auth.create()
        for d in ['index_backup']:
            dir_to_create = os.path.join(self.data_dir_local, 'catlalog', d)
            if not os.path.exists(dir_to_create):
                os.makedirs(dir_to_create)

    def update_config_xml_for_multitenant(self,xml_file_path):
        f = XmlFile(xml_file_path)
        path_sref="/comm:config"
        f.modify(path_sref + "/comm:group/@enabled","false")
        f.modify(path_sref + "/comm:group/comm:membershipCache/@maximumAgeOnLoginInSeconds","120")
        f.modify(path_sref + "/comm:group/comm:membershipCache/@maximumAgeOnRequestInSeconds","120")
        f.modify(path_sref + "/comm:communityHandle/@enabled","false")
        f.insertComment(path_sref + "/comm:communityHandle","Enable Configuration for Mutli-Tenant")
        f.insertText(path_sref + "/comm:communityHandle","\n\n        ")
        f.insertSibling(path_sref + "/comm:communityHandle", "<comm:searchScope>\n            <comm:item name='allServices' value='disabled'/>\n        </comm:searchScope>")
        f.insertText(path_sref + "/comm:communityHandle","\n\n        ")
        f.insertSibling(path_sref + "/comm:communityHandle","<comm:communityBusinessOwner enabled='false'/>")
        f.insertText(path_sref + "/comm:communityHandle","\n\n        ")
        f.save()


class Wikis(LotusConnections):
    def setup_names(self):
        self.ejb_webui_name = "wikis.web.war"
        self.ejb_webui_xml = "wikis.web.war,WEB-INF/web.xml"
        self.cache_specs = [
            {"name": "wikis.freshness",
             "jndiName": "cache/wikis",
             "cacheSize": 2000, "defaultPriority": 1,
             "enableCacheReplication": "true",
             "replicationType": "NONE",
             "cacheReplication": [["messageBrokerDomainName", "ConnectionsReplicationDomain"]] }]
        self.wm_specs = [
           {"name":"WikisWorkManager", "jndiName":"workmanager/wikis",
            "category":"WikisWorkManager", "description":"Wikis Work Manager",
            "maxThreads":8, "minThreads":1, "numAlarmThreads":7, "isGrowable":"false",
            "serviceNames":"security",
            "threadPriority":5, "workReqQSize":100, "workReqQFullAction":0 } ]
        self.tm_specs = [
           {"name": "WikisTimerManager", "jndiName": "timermanager/wikis",
            "category": "WikisTimerManager", "description": "Wikis Timer Manager",
            "serviceNames": "", "numAlarmThreads": 7 } ]
        self.scheduler_specs = [
           {"name":"%sScheduler" % self.name,
            "description":"%s Scheduler" % self.name,
            "jndiName":"scheduler/%s" % self.component_name,
            "datasourceJNDIName": self.data_source_spec['jndi'],
            "datasourceAlias": "%sJAASAuth" % self.component_name,
            "pollInterval":"30",
            "tablePrefix":"WIKIS.SCHEDULER",
            "useAdminRoles":"true",
            "workManagerInfoJNDIName":"workmanager/%s" % self.component_name} ]
        self.activation_specs.append(self.platform_cmd_consumer_as())
        self.has_new_3_0_events_publisher = 1
        self.has_scheduled_task_ejb = 1
        self.has_notification_sink_ejb = 1
        self.require_startup_beans_service = 1
        self.oauth_urls = [ '/wikis/oauth' ]
        self.notification_sources = ['wikis']
        self.known_roles = ["everyone","person","reader","metrics-reader","everyone-authenticated",
                            "wiki-creator","admin","search-admin","widget-admin","bss-provisioning-admin"]

    def do_apply_install_options(self, opts):
        self.was_variables["WIKIS_CONTENT_DIR"] = "%s/wikis/upload" % self.data_dir
        self.roles_map['everyone-authenticated'] = ['No', 'Yes', '', '']
        admins = unique(self.administrators + [self.conn_admin_user])
        self.roles_map["admin"] = ['No', 'No', "|".join(admins), "|".join(self.administrator_groups)]
        self.roles_map["search-admin"] = ['No', 'No', "|".join(self.search_admin), "|".join(self.search_admin_groups)]
        self.roles_map["widget-admin"] = ['No', 'No', "|".join(self.widget_admin), "|".join(self.widget_admin_groups)]
        if self.multitenant:
            self.roles_map["everyone"] = ['No','Yes', '','']
            self.roles_map["reader"] = ['No','Yes','','']
        if self.bss_provisioning_admin_user:
            self.roles_map["bss-provisioning-admin"] = ['No', 'No', self.bss_provisioning_admin_user, '']

    def customize_datasource(self, data_source):
        if self.db_type == "DB2":
            data_source.properties["currentSchema"] = "WIKIS"

    def ear_opts_MapResRefToEJB(self):
        return []

    def update_additional_config_files(self, lcc, url, url_ssl):
        f_path = self.make_sure_config_file_in_place("wikis-config.xml")
        wikis_cfg = XmlFile(f_path)
        wikis_cfg.modify("/tns:config/tns:db/@dialect", self.db_type)
        wikis_cfg.save()
        files = ['wikis-config.xsd',
                 'mime-wikis-config.xsd', 'mime-wikis-config.xml']
        for f in files:
            self.make_sure_config_file_in_place(f)

    def additional_post_install(self):
        if self.multitenant:
            print "Update config property bootstrap[@productMode] for multitenancy"
            self.update_ear_properties_file(os.path.join('config',"com.ibm.lconn.share.platform.bootstrap.properties"),"bootstrap[@productMode]","cloud")

class Files(LotusConnections):
    def setup_names(self):
        self.ejb_webui_name = "files.web.war"
        self.ejb_webui_xml = "files.web.war,WEB-INF/web.xml"
        self.cache_specs = [
            {"name": "files.freshness",
             "jndiName": "cache/files",
             "cacheSize": 2000, "defaultPriority": 1,
             "enableCacheReplication": "true",
             "replicationType": "NONE",
             "cacheReplication": [["messageBrokerDomainName", "ConnectionsReplicationDomain"]] }]
        self.jms_topic_specs = [
            {"name":"Files Event Consumer Topic",
             "scope": "CELL",
             "topicSpace":"connections.events",
             "topicName":"//.",
             "jndi":"jms/connections/files/events/consumer/topic"} ]
        self.wm_specs = [
           {"name":"FilesWorkManager", "jndiName":"workmanager/files",
            "category":"FilesWorkManager", "description":"Files Work Manager",
            "maxThreads":8, "minThreads":1, "numAlarmThreads":7, "isGrowable":"false",
            "serviceNames":"security",
            "threadPriority":5, "workReqQSize":100, "workReqQFullAction":0 } ]
        self.tm_specs = [
           {"name": "FilesTimerManager", "jndiName": "timermanager/files",
            "category": "FilesTimerManager", "description": "Files Timer Manager",
            "serviceNames": "", "numAlarmThreads": 7 } ]
        self.scheduler_specs = [
           {"name":"%sScheduler" % self.name,
            "description":"%s Scheduler" % self.name,
            "jndiName":"scheduler/%s" % self.component_name,
            "datasourceJNDIName": self.data_source_spec['jndi'],
            "datasourceAlias": "%sJAASAuth" % self.component_name,
            "pollInterval":"30",
            "tablePrefix":"FILES.SCHEDULER",
            "useAdminRoles":"true",
            "workManagerInfoJNDIName":"workmanager/%s" % self.component_name} ]
        self.web_container_specs["enableServletCaching"] = "false"
        self.transaction_service_props = {'propogatedOrBMTTranLifetimeTimeout': 0}
        self.activation_specs.append(self.platform_cmd_consumer_as())
        self.activation_specs.append(self.events_consumer_as())
        self.has_new_3_0_events_publisher = 1
        self.has_scheduled_task_ejb = 1
        self.has_notification_sink_ejb = 1
        self.require_startup_beans_service = 1
        self.media_gallery_admin = None
        self.media_gallery_admin_pswd = ""
        self.oauth_urls = [ '/files/oauth' ]
        self.notification_sources = ['files']
        self.known_roles = ["everyone","person","reader","metrics-reader","everyone-authenticated",
                            "files-owner","admin","search-admin","widget-admin","app-connector",
                            "filesync-user","global-moderator","org-admin","bss-provisioning-admin"]

    def do_apply_install_options(self, opts):
        self.was_variables["FILES_CONTENT_DIR"] = "%s/files/upload" % self.data_dir
        self.was_variables["FILES_EVENT_CONTENT_DIR"] = "%s/files/upload" % self.data_dir
        self.roles_map["global-moderator"] = ['No', 'No', "|".join(self.global_moderators), "|".join(self.global_mod_groups)]
        self.roles_map['everyone-authenticated'] = ['No', 'Yes', '', '']
        self.roles_map['files-owner'] = ['No', 'Yes', '', '']
        self.roles_map["filesync-user"] = ['No', 'No', '', '']
        self.roles_map["search-admin"] = ['No', 'No', "|".join(self.search_admin), "|".join(self.search_admin_groups)]
        self.roles_map["widget-admin"] = ['No', 'No', "|".join(self.widget_admin), "|".join(self.widget_admin_groups)]
        # files needs connectionsAdmin user to have admin role, for media gallery
        self.media_gallery_admin = opts.get('mediaGalleryAdmin', self.conn_admin_user)
        self.media_gallery_admin_pswd = opts.get('mediaGalleryAdminPassword', self.conn_admin_password)
        admins = unique(self.administrators + [self.conn_admin_user])
        self.roles_map["admin"] = ['No', 'No', "|".join(admins), "|".join(self.administrator_groups)]
        if self.multitenant:
            self.roles_map['everyone'] = ['No', 'Yes', '', '']
            self.roles_map['reader'] = ['No', 'Yes', '', '']
            if self.bss_provisioning_admin_user:
                self.roles_map["bss-provisioning-admin"] = ['No', 'No', self.bss_provisioning_admin_user, '']

    def customize_datasource(self, data_source):
        if self.db_type == "DB2":
            data_source.properties["currentSchema"] = "FILES"

    def ear_opts_MapResRefToEJB(self):
        return []

    def update_additional_config_files(self, lcc, url, url_ssl):
        f_path = self.make_sure_config_file_in_place("files-config.xml")
        files_cfg = XmlFile(f_path)
        files_cfg.modify("/tns:config/tns:db/@dialect", self.db_type)
        files_cfg.save()
        files = ['files-config.xsd',
                 'files-url-config.xsd', 'files-url-config.xml',
                 'mime-files-config.xsd', 'mime-files-config.xml']
        for f in files:
            self.make_sure_config_file_in_place(f)
        if self.enable_moderation:
            self.update_content_review_cfg()

    def update_content_review_cfg(self):
        f_path = self.make_sure_config_file_in_place("contentreview-config.xml")
        f = XmlFile(f_path)
        xpaths = [
            '/config/serviceConfiguration/service[@id="files"]/contentApproval/@enabled',
            '/config/serviceConfiguration/service[@id="files"]/contentApproval/ownerModerate/@enabled',
            '/config/serviceConfiguration/service[@id="files"]/contentFlagging/@enabled',
            '/config/serviceConfiguration/service[@id="files"]/contentFlagging/ownerModerate/@enabled']
        for p in xpaths:
            f.modify(p, 'true')
        f.save()

    def additional_post_install(self):
        # add the media gallery Jaas
        auth = JAASAuthData('mediaGalleryAdmin')
        auth.uid = self.media_gallery_admin
        auth.password = self.media_gallery_admin_pswd
        auth.description = "JAAS Auth data for media gallery"
        auth.create()
        # update the events config for media gallery
        f_path = self.make_sure_config_file_in_place("events-config.xml")
        f = XmlFile(f_path)
        f.modify("/config/postHandlers/postHandler[@name='PhotoRenditionEventHandler']" +
                 "/properties/property[@name='j2calias']/text()",
                 "mediaGalleryAdmin")
        f.save()
        # put media gallery types xml file to cusotmization directory
        files = ['MediaPhoto.xml', 'MediaVideo.xml']
        objtypes_dir = os.path.join(self.customization_dir, "objecttypes")
        if not os.path.exists(objtypes_dir):
            os.makedirs(objtypes_dir)
        print "Copy media types definitions to [%s]" % objtypes_dir
        for f in files:
            f_path = os.path.join(self.kit_dir, "bin_lc_admin", f)
            if os.path.exists(f_path):
                print "  %s" % f_path
                shutil.copy2(f_path, objtypes_dir)
        if self.multitenant:
            print "Copy multi-tenant strings to customization directory"
            kit_strings = os.path.join(self.kit_dir, "customization", "strings")
            string_dir = os.path.join(self.customization_dir, "strings")
            if os.path.exists(kit_strings):
                if not os.path.exists(string_dir):
                    shutil.copytree(kit_strings,string_dir)
                else:
                    string_files = os.listdir(kit_strings)
                    for sf in string_files:
                        sf_path = os.path.join(kit_strings,sf)
                        shutil.copy2(sf_path,string_dir)
            
            print "Update config property bootstrap[@productMode] for multitenancy"
            self.update_ear_properties_file(os.path.join('config',"com.ibm.lconn.share.platform.bootstrap.properties"),"bootstrap[@productMode]","cloud")

class Mobile(LotusConnections):
    def setup_names(self):
        self.ejb_webui_name = "Mobile Web"
        self.ejb_webui_xml = "mobile.web.war,WEB-INF/web.xml"
        self.jms_topic_specs = [
            {"name":"Mobile Event Consumer Topic",
             "scope": "CELL",
             "topicSpace":"connections.events",
             "topicName":"//.",
             "jndi":"jms/connections/mobile/events/consumer/topic"} ]
        self.activation_specs.append(self.events_consumer_as())
        self.has_event_subscriber_ejb = 1
        self.known_roles = ["everyone","reader","person"]
        self.replication_domains = [{"name": "ConnectionsReplicationDomain"}]
        self.dynamic_cache_settings = {
            "enableCacheReplication": "true",
            "replicationType": "PUSH_PULL",
            "cacheReplication": [["messageBrokerDomainName", "ConnectionsReplicationDomain"]] }

    def do_apply_install_options(self, opts):
        self.was_variables["MOBILE_CONTENT_DIR"] = "%s/mobile/cache" % self.data_dir

    def ear_opts_default_datasource(self):
        return []

    def ear_opts_BindJndiForEJBNonMessageBinding(self):
        return []

    def ear_opts_MapEJBRefToEJB(self):
        return []

    def update_additional_config_files(self, lcc, url, url_ssl):
        files = ['mobile-config.xsd', 'mobile-config.xml']
        for f in files:
            self.make_sure_config_file_in_place(f)


class MobileAdmin(LotusConnections):
    def setup_names(self):
        self.name = "Mobile Administration"
        self.data_source_spec = None
        self.known_roles = ["administrator","tenantadmin","everyone"]

    def ear_opts_default_datasource(self):
        return []

    def do_apply_install_options(self, opts):
        del self.roles_map["reader"]
        del self.roles_map["person"]
        self.roles_map["administrator"] = ['No', 'No', "|".join(self.administrators), "|".join(self.administrator_groups)]

    def ear_opts_BindJndiForEJBNonMessageBinding(self):
        return []

    def ear_opts_MapEJBRefToEJB(self):
        return []


class Help(LotusConnections):
    def setup_names(self):
        self.data_source_spec = None
        self.known_roles = []

    def ear_opts_default_datasource(self):
        return []

    def ear_opts_BindJndiForEJBNonMessageBinding(self):
        return []
    def ear_opts_MapEJBRefToEJB(self):
        return []

class Forums(LotusConnections):
    def setup_names(self):
        self.ejb_webui_name = "Discussion Forum Web UI"
        self.ejb_webui_xml = "forum.web.war,WEB-INF/web.xml"
        self.data_source_spec['ref'] = "jdbc/forum"
        self.data_source_spec['jndi'] = "jdbc/forums"
        self.default_db_name['DB2'] = "FORUM"
        self.default_db_name['SQL Server'] = "FORUM"
        self.default_db_user['SQL Server'] = "DFUSER"
        self.default_db_user['Oracle'] = "DFUSER"
        self.activation_specs = []
        self.activation_specs.append(self.platform_cmd_consumer_as())
        self.activation_specs.append(self.mailin_consumer_as())
        self.mime_specs = { "image/png":"png;PNG" }
        self.scheduler_specs = [
           {"name":"ForumsScheduler",
            "description":"Forums Scheduler",
            "jndiName":"scheduler/%s" % self.component_name,
            "datasourceJNDIName": self.data_source_spec['jndi'],
            "datasourceAlias":"%sJAASAuth" % self.component_name,
            "pollInterval":"30",
            "tablePrefix":"FORUM.DF_SCHEDULER",
            "useAdminRoles":"true",
            "workManagerInfoJNDIName":"wm/%s" % self.component_name} ]
        self.wm_specs = [
           {"name":"ForumsWorkManager",
            "category":"ForumWorkManager",
            "description":"Forums Work Manager",
            "jndiName":"wm/%s" % self.component_name,
            "maxThreads":10,
            "minThreads":1,
            "numAlarmThreads":5,
            "isGrowable":"false",
            "threadPriority":5,
            "workReqQSize":10,
            "workReqQFullAction":1 } ]
        self.has_new_3_0_events_publisher = 1
        self.require_startup_beans_service = 1
        self.has_platform_command_consumer = 1
        self.has_mailin_subscriber_mdb = 1
        self.has_following_ejb = 1
        self.has_scheduled_task_ejb = 1
        self.has_notification_sink_ejb = 1
        self.oauth_urls = [ '/forums/oauth' ]
        self.notification_sources = ['Forums']
        self.known_roles = ["person","metrics-reader","reader","everyone","discussThis-user",
                            "search-admin","widget-admin","admin","global-moderator",
                            "bss-provisioning-admin","search-public-admin"]

    def do_apply_install_options(self, opts):
        self.was_variables["FORUM_STATS_DIR"] = "%s/%s/statistic" % (self.data_dir, self.component_name)
        self.was_variables["FORUM_CONTENT_DIR"] = "%s/%s/content" % (self.data_dir, self.component_name)
        self.roles_map["search-admin"] = ['No', 'No', "|".join(self.search_admin), "|".join(self.search_admin_groups)]
        self.roles_map["widget-admin"] = ['No', 'No', "|".join(self.widget_admin), "|".join(self.widget_admin_groups)]
        admins = unique(self.administrators + [self.conn_admin_user])
        self.roles_map["admin"] = ['No', 'No', "|".join(admins), "|".join(self.administrator_groups)]
        self.roles_map["global-moderator"] = ['No', 'No', "|".join(self.global_moderators), "|".join(self.global_mod_groups)]
        self.roles_map["metrics-reader"] = ['Yes', 'No', '', '']
        self.roles_map["discussThis-user"] = ['Yes', 'No', '', '']
        if self.bss_provisioning_admin_user and self.multitenant:
            self.roles_map["bss-provisioning-admin"] = ['No', 'No', self.bss_provisioning_admin_user, '']


    def customize_datasource(self, data_source):
        if self.db_type == "DB2":
            data_source.properties["currentSchema"] = "FORUM"

    def update_additional_config_files(self, lcc, url, url_ssl):
        files = ['forum-config.xsd', 'forum-config.xml',
                 'forum-policy.xsd', 'forum-policy.xml' ]
        for f in files:
            lcc_file = self.make_sure_config_file_in_place(f)
            if os.path.basename(lcc_file) == "forum-config.xml" and self.multitenant:
                self.update_config_xml_for_multitenant(lcc_file)
        if self.enable_moderation:
            self.update_content_review_cfg()

    def update_content_review_cfg(self):
        f_path = self.make_sure_config_file_in_place("contentreview-config.xml")
        f = XmlFile(f_path)
        xpaths = [
            '/config/serviceConfiguration/service[@id="forums"]/contentApproval/@enabled',
            '/config/serviceConfiguration/service[@id="forums"]/contentApproval/ownerModerate/@enabled',
            '/config/serviceConfiguration/service[@id="forums"]/contentFlagging/@enabled',
            '/config/serviceConfiguration/service[@id="forums"]/contentFlagging/ownerModerate/@enabled',
            '/config/serviceConfiguration/service[@id="forums"]/contentFlagging/issueCategorization/@enabled']
        for p in xpaths:
            f.modify(p, 'true')
        f.save()

    def update_config_xml_for_multitenant(self,xml_file_path):
        f = XmlFile(xml_file_path)
        path_sref="/config"
        f.modify(path_sref + "/deployment/@enableLotusLive","true")
        f.save()

class Moderation(LotusConnections):
    def setup_names(self):
        self.data_source_spec = None
        self.oauth_urls = [ '/moderation/oauth' ]
        self.notification_sources = ['moderation']
        self.known_roles = ["reader","everyone-authenticated","person","global-moderator"]

    def ear_opts_default_datasource(self):
        return []

    def do_apply_install_options(self, opts):
        del self.roles_map['everyone']
        self.roles_map["global-moderator"] = ['No', 'No', "|".join(self.global_moderators), "|".join(self.global_mod_groups)]

    def ear_opts_BindJndiForEJBNonMessageBinding(self):
        return []
    def ear_opts_MapEJBRefToEJB(self):
        return []

class WidgetContainer(LotusConnections):
    def setup_names(self):
        self.services_provides = ['opensocial', 'microblogging']
        self.ejb_webui_name = "IBM CRE OpenSocial Application"
        self.ejb_webui_xml = "lc.shindig.serverapi.war,WEB-INF/web.xml"
        self.starting_weight = 3
        self.default_db_name['DB2'] = "HOMEPAGE"
        self.default_db_name['SQL Server'] = "HOMEPAGE"
        self.default_db_user["Oracle"] = "HOMEPAGEUSER"
        self.default_db_user["SQL Server"] = "HOMEPAGEUSER"
        self.replication_domains = [{"name": "ConnectionsReplicationDomain"}]
        self.cache_specs = [
            {"name": "OAuth2 DB Tokens",
             "jndiName": "ic/services/cache/OAuth20DBTokenCache",
             "scope": "CELL",
             "cacheSize": 1000,
             "flushToDiskOnStop":"false",
             "enableCacheReplication":"true",
             "replicationType": "PUSH_PULL",
             "cacheReplication": [["messageBrokerDomainName", "ConnectionsReplicationDomain"]] },
            {"name": "OAuth2 DB Client",
             "jndiName":"ic/services/cache/OAuth20DBClientCache",
             "scope": "CELL",
             "cacheSize": 1000,
             "flushToDiskOnStop": "false",
             "enableCacheReplication":"true",
             "replicationType": "PUSH_PULL",
             "cacheReplication": [["messageBrokerDomainName", "ConnectionsReplicationDomain"]] }]
        self.cache_specs += self.cre_caches() + self.shindig_caches()
        self.dynamic_cache_settings = {
            "enableCacheReplication": "true",
            "replicationType": "PUSH_PULL",
            "cacheReplication": [["messageBrokerDomainName", "ConnectionsReplicationDomain"]] }
        self.has_new_3_0_events_publisher = "external"
        self.oauth_urls = [ '/connections/opensocial/oauth' ]
        self.known_roles = ["person","allAuthenticated","admin","everyone","reader","metrics-reader","global-moderator","mail-user","trustedExternalApplication"]

    def cre_caches(self):
        caches = [
            {"name": "CRE Gadget Cache",     "jndiName": "ic/services/cache/gadgetCache/creInstance",
                "cacheSize": 1000,
                "MemoryCacheEvictionPolicy": [["highThreshold", 5],["lowThreshold","4"]],
                "replicationType": "NONE",
                "enableCacheReplication": "false"},
            {"name": "CRE JSON Cache",       "jndiName": "ic/services/cache/json/creInstance",
                "cacheSize": 2000,
                "MemoryCacheEvictionPolicy": [["highThreshold", 5],["lowThreshold","4"]],
                "replicationType": "NONE",
                "enableCacheReplication": "false" },
            {"name": "CRE OAUTH2 accessors", "jndiName": "ic/services/cache/oauth2/accessors/creInstance",
                "cacheSize": 20000,
                "enableCacheReplication": "true",
                "replicationType": "PUSH_PULL" },
            {"name": "CRE OAUTH2 clients",   "jndiName": "ic/services/cache/oauth2/clients/creInstance",
                "replicationType": "NONE",
                "enableCacheReplication": "false",
                "cacheSize": 5000 },
            {"name": "CRE OAUTH2 Tokens",    "jndiName": "ic/services/cache/oauth2/tokens/creInstance",
                "replicationType": "NONE",
                "enableCacheReplication": "false",
                "cacheSize": 20000 },
            {"name": "CRE Resource Cache",   "jndiName": "ic/services/cache/resourceCache/creInstance",
                "replicationType": "NONE",
                "enableCacheReplication": "false",
                "MemoryCacheEvictionPolicy": [["highThreshold", 5],["lowThreshold","4"]],
                "cacheSize": 1000 },
            {"name": "CRE Sandboxed Widegt Cache",
                "jndiName": "ic/services/cache/sandboxedWidgetCache/creInstance",
                "replicationType": "NONE",
                "enableCacheReplication": "false",
                "cacheSize": 200 },
            {"name": "CRE Key Generator",
                "jndiName": "ic/services/cache/keyGenerator/creInstance",
                "cacheSize": 100,
                "enableCacheReplication": "true",
                "replicationType": "PUSH_PULL"},
            {"name": "CRE Security Token Cache",
                "jndiName": "ic/services/cache/stcache/creInstance",
                "cacheSize": 1000 }]
        for c in caches:
            c.update({"disableDependencyId": "false",
                      "cacheReplication": [["messageBrokerDomainName", "ConnectionsReplicationDomain"]] })
        return caches

    def shindig_caches(self):
        caches = [
            {"name": "Shindig Featured js cache",
             "jndiName": "ic/org/apache/shindig/gadgets/features/FeatureJsCache",
             "cacheSize": 1000,
             "replicationType": "NONE",
             "enableCacheReplication": "false" },
            {"name": "Shindig compiled js Cache",
             "jndiName": "ic/org/apache/shindig/gadgets/rewrite/js/CompiledJs",
             "cacheSize": 10000,
             "MemoryCacheEvictionPolicy": [["highThreshold", 5],["lowThreshold","4"]],
             "replicationType": "NONE",
             "enableCacheReplication": "false" },
            {"name": "Shindig gadget specs Cache",
             "jndiName": "ic/org/apache/shindig/gadgets/gadgetSpecs",
             "cacheSize": 1000,
             "MemoryCacheEvictionPolicy": [["highThreshold", 5],["lowThreshold","4"]],
             "replicationType": "NONE",
             "enableCacheReplication": "false" },
            {"name": "Shindig invalidated users Cache",
             "jndiName": "ic/org/apache/shindig/gadgets/http/invalidatedUsers",
             "cacheSize": 20000,
             "replicationType": "NONE",
             "enableCacheReplication": "false" },
            {"name": "Shindig message bundles Cache",
             "jndiName": "ic/org/apache/shindig/gadgets/messageBundles",
             "cacheSize": 5000,
             "MemoryCacheEvictionPolicy": [["highThreshold", 5],["lowThreshold","4"]],
             "replicationType": "NONE",
             "enableCacheReplication": "false" },
            {"name": "Shindig parsed xml Cache",
             "jndiName": "ic/org/apache/shindig/gadgets/templates/parsedXml",
             "cacheSize": 5000,
             "MemoryCacheEvictionPolicy": [["highThreshold", 5],["lowThreshold","4"]],
             "replicationType": "NONE",
             "enableCacheReplication": "false" },
            {"name": "Shindig parsedCSS Cache",
             "jndiName": "ic/org/apache/shindig/gadgets/parse/caja/parsedCss",
             "cacheSize": 5000,
             "MemoryCacheEvictionPolicy": [["highThreshold", 5],["lowThreshold","4"]],
             "replicationType": "NONE",
             "enableCacheReplication": "false" },
            {"name": "Shindig parsedDoc Cache",
             "jndiName": "ic/org/apache/shindig/gadgets/parse/parsedDocuments",
             "cacheSize": 5000,
             "MemoryCacheEvictionPolicy": [["highThreshold", 5],["lowThreshold","4"]],
             "replicationType": "NONE",
             "enableCacheReplication": "false" },
            {"name": "Shindig parsedFrags Cache",
             "jndiName": "ic/org/apache/shindig/gadgets/parse/parsedFragments",
             "cacheSize": 5000,
             "MemoryCacheEvictionPolicy": [["highThreshold", 5],["lowThreshold","4"]],
             "replicationType": "NONE",
             "enableCacheReplication": "false" },
            {"name": "Shindig Transitive Deps Cache",
             "jndiName": "ic/org/apache/shindig/gadgets/features/TransitiveDepsCache",
             "cacheSize": 1000}]
        for c in caches:
            c.update({"disableDependencyId": "false",
                      "cacheReplication": [["messageBrokerDomainName", "ConnectionsReplicationDomain"]] })
        return caches

    def do_apply_install_options(self, opts):
        opensocial_features_dir = self.customization_dir + "/OpenSocial/features"
        if not os.path.exists(opensocial_features_dir):
            os.makedirs(opensocial_features_dir)
        self.was_variables["CONNECTIONS_OPENSOCIAL_FEATURES"] = opensocial_features_dir
        self.roles_map["trustedExternalApplication "] = ['No', 'No', self.filenet_admin, '']
        admins = unique(self.administrators + [self.conn_admin_user])
        self.roles_map["admin"] = ['No', 'No', "|".join(admins), "|".join(self.administrator_groups)]

    def ear_opts_default_datasource(self):
        return []

    def ear_opts_MapResRefToEJB(self):
        return []

    def update_additional_config_files(self, lcc, url, url_ssl):
        files = [ 'opensocial-config.xsd', 'opensocial-config.xml',
                  'AS.Gadget.extension.cfg', 'AS.Gadget.oauth.cfg', 'AS.Gadget.proxy.cfg',
                  'EE.Gadget.extension.cfg', 'EE.Gadget.oauth.cfg', 'EE.Gadget.proxy.cfg' ]
        for f in files:
            self.make_sure_config_file_in_place(f)


class Common(LotusConnections):
    def setup_names(self):
        self.services_provides = ['webresources', 'bookmarklet', 'oauth', 'oauthprovider',
            'deploymentConfig']
        self.ejb_webui_name = "lc.oauth.provider.war"
        self.ejb_webui_xml = "lc.oauth.provider.war,WEB-INF/web.xml"
        self.data_source_spec['name'] = "oauth provider"
        self.data_source_spec['jndi'] = "jdbc/oauthp"
        self.starting_weight = 3
        self.default_db_name['DB2'] = "HOMEPAGE"
        self.default_db_name['SQL Server'] = "HOMEPAGE"
        self.default_db_user["Oracle"] = "HOMEPAGEUSER"
        self.default_db_user["SQL Server"] = "HOMEPAGEUSER"
        self.replication_domains = [{'name': 'ConnectionsReplicationDomain'}]
        self.cache_specs = [
            {"name": "Configuration sync",
             "scope": "CELL",
             "jndiName": "cache/mtconfig",
             "cacheSize": 2000, "defaultPriority": 1,
             "enableCacheReplication": "true",
             "replicationType": "PUSH",
             "useListenerContext": "true",
             "cacheReplication": [["messageBrokerDomainName", "ConnectionsReplicationDomain"]] }]
        self.cache_specs += self.highway_cache_specs()
        self.oauth_urls = [ '/connections/core/oauth' ]
        self.known_roles = ["person","allAuthenticated","admin","everyone","metrics-report-run","global-moderator","mail-user","reader","orgadmin"]

    def customize_datasource(self, data_source):
        if self.db_type == "DB2":
            data_source.properties["currentSchema"] = "HOMEPAGE"

    def highway_cache_specs(self):
        specs = []
        specs.append( {"name": "HighwayOrganizationCache", "scope": "CELL",
                 "jndiName": "ic/services/cache/HighwayOrganizationCache",
                 "cacheSize": 20000, "defaultPriority": 1,
                 "enableCacheReplication": "true", "replicationType": "PUSH_PULL", "disableDependencyId": "false",
                 "cacheReplication": [["messageBrokerDomainName", "ConnectionsReplicationDomain"]] })
        return specs

    def do_apply_install_options(self, opts):
        if self.smartCloud:
            self.was_variables["CONNECTIONS_PROVISION_PATH"] = self.data_dir_local + "/provision"
        else:
            self.was_variables["CONNECTIONS_PROVISION_PATH"] = self.data_dir + "/provision"
        self.was_variables["CONNECTIONS_CONFIGURATION_PATH"] = "%s/configuration" % self.data_dir
        self.roles_map['reader'] = [ 'No', 'Yes', '', '']
        self.roles_map["admin"] = ['No', 'No', "|".join(self.administrators), "|".join(self.administrator_groups)]
        self.roles_map["metrics-report-run"] = ['No', 'No', "|".join(self.administrators), "|".join(self.administrator_groups)]
        self.roles_map['mail-user'] = [ 'No', 'Yes', '', '']
        self.roles_map["global-moderator"] = ['No', 'No', "|".join(self.global_moderators), "|".join(self.global_mod_groups)]

    def ear_opts_MapResRefToEJB(self):
        refs = [["lc.oauth.provider.web.war", "", "lc.oauth.provider.web.war,WEB-INF/web.xml",
                 "com/ibm/lconn/oauth/datasource", "javax.sql.DataSource", "jdbc/oauthp",
                 "DefaultPrincipalMapping", self.data_source.auth.name ]]
        return [ '-MapResRefToEJB', refs ]

    def ear_opts_default_datasource(self):
        return []
    def ear_opts_BindJndiForEJBNonMessageBinding(self):
        return []
    def ear_opts_MapEJBRefToEJB(self):
        return []

    def update_additional_config_files(self, lcc, url, url_ssl):
        files = ['connectionsProvider.xml']
        oauth2_path = "cells/%s/oauth20" % self.cell.name
        for f in files:
            local_path = os.path.join(self.kit_dir, "oauth20", f)
            if not os.path.exists(local_path):
                print "WARNING: file [%s] not found, OAuth may not work, is the kit defective?!" % local_path
                continue
            was_path = oauth2_path + "/" + f
            print " Upload to WAS ... [%s]" % was_path
            if AdminConfig.existsDocument(was_path):
                AdminConfig.deleteDocument(was_path)
            AdminConfig.createDocument(was_path, local_path)
        # Here is something special we need to do for the oauthprovider servcie
        # since the was_oauth2.ear is not deployed by us, we have no control on
        # its HREFs and context root, we assume it is always "/oauth2"
        self.install_was_provided_app("WebSphereOauth20SP")
        path_sref = "/tns:config/sloc:serviceReference[@serviceName=\"oauthprovider\"]"
        lcc.modify(path_sref + "/sloc:href/sloc:hrefPathPrefix/text()", "/oauth2")
        files = ['file-preview-config.xsd', 'file-preview-config.xml']
        for f in files:
            self.make_sure_config_file_in_place(f)
        # Install the SAML service provider EAR from WAS if desired. Note that some deployment
        # would prefer not use the WAS provided solution, they may use TFIM for example.
        if self.use_websphere_saml_sp:
            self.install_was_provided_app("WebSphereSamlSP")

    def additional_post_install(self):
        src_dir = os.path.join(self.kit_dir, "configuration")
        dest_dir = os.path.join(self.data_dir, "configuration")
        if not os.path.exists(dest_dir): os.makedirs(dest_dir)
        try:
            copytree_replace(src_dir, dest_dir)
        except OSError, msg:
            raise LcError, "Failed copy Highway configuartion data to %s. Caused be OSError: %s" % (dest_dir, msg)

class Metrics(LotusConnections):
    def setup_names(self):
        self.jms_topic_specs = [
            {"name":"Metrics Event Consumer Topic",
             "scope": "CELL",
             "topicSpace":"connections.events",
             "topicName":"//.",
             "jndi":"jms/connections/metrics/events/consumer/topic"} ]
        self.activation_specs.append(self.events_consumer_as())
        self.activation_specs.append(self.platform_cmd_consumer_as())
        self.has_platform_command_consumer = 1
        self.has_event_subscriber_ejb = 1
        self.wm_specs = [
           {"name":"MetricsWorkManager",
            "category":"MetricsWorkManager",
            "description":"Metrics Work Manager",
            "jndiName":"workmanager/%s" % self.component_name,
            "serviceNames":"security;UserWorkArea;com.ibm.ws.i18n",
            "maxThreads":10,
            "minThreads":1,
            "numAlarmThreads":5,
            "isGrowable":"true",
            "threadPriority":5,
            "workReqQSize":10,
            "workReqQFullAction":1 } ]
        self.scheduler_specs = [
           {"name":"MetricsScheduler",
            "description":"Metrics Scheduler",
            "jndiName":"scheduler/%s" % self.component_name,
            "datasourceJNDIName": self.data_source_spec['jndi'],
            "datasourceAlias":"%sJAASAuth" % self.component_name,
            "pollInterval":"30",
            "tablePrefix":"METRICS.SCHEDULER",
            "useAdminRoles":"false",
            "workManagerInfoJNDIName":"workmanager/%s" % self.component_name} ]
        self.oauth_urls = [ '/metrics/service/oauth' ]
        self.known_roles = ["everyone","person","reader","everyone-authenticated","community-metrics-run","admin","metrics-report-run"]

    def customize_datasource(self, data_source):
        if self.db_type == "DB2":
            data_source.properties["currentSchema"] = "METRICS"

    def do_apply_install_options(self, opts):
        admins = unique(self.administrators + [self.conn_admin_user])
        self.roles_map["admin"] = ['No', 'No', "|".join(admins), "|".join(self.administrator_groups)]
        self.roles_map["metrics-report-run"] = ['No', 'No', "|".join(self.administrators), "|".join(self.administrator_groups)]
        self.cognos_admin_user = opts.get('cognosAdmin', self.conn_admin_user)
        self.cognos_admin_password = opts.get('cognosAdminPassword', self.conn_admin_password)

    def ear_opts_default_datasource(self):
        return []
    def ear_opts_BindJndiForEJBNonMessageBinding(self):
        return []
    def ear_opts_MapEJBRefToEJB(self):
        return []
    def ear_opts_MapResRefToEJB(self):
        return []

    def ear_opts_deployejb_opts(self):
        ejb_db_type = {
            "DB2": "DB2UDB_V95",
            "Oracle": "ORACLE_V10G",
            "SQL Server": "MSSQLSERVER_2005" }
        return [ "-deployejb.dbtype", ejb_db_type[self.db_type] ]

    def update_additional_config_files(self, lcc, url, url_ssl):
        f_path = self.make_sure_config_file_in_place("metrics-config.xml")
        "/tns:config/tns:db/@dialect"
        f = XmlFile(f_path)
        f.modify("/tns:config/tns:db/@dialect", self.db_type)
        f.save()
        files = ['metrics-config.xsd', 'cognos', 'metrics']
        for f in files:
            self.make_sure_config_file_in_place(f)

    def additional_post_install(self):
        auth = JAASAuthData('cognosAdminAlias')
        auth.uid = self.cognos_admin_user
        auth.password = self.cognos_admin_password
        auth.description = "JAAS Auth data for IBM Cognos Administrator"
        auth.create()

class Cognos(LotusConnections):
    def setup_names(self):
        self.name = "IBM Cognos"
        self.data_source_spec = None
        self.congnos_admin_user = None
        self.congnos_admin_pswd = ""
        self.known_roles = []

    def ear_opts_default_datasource(self):
        return []
    def ear_opts_BindJndiForEJBNonMessageBinding(self):
        return []
    def ear_opts_MapEJBRefToEJB(self):
        return []
    def ear_opts_MapResRefToEJB(self):
        return []

    def do_apply_install_options(self, opts):
        self.roles_map = {}

    def update_additional_config_files(self, lcc, url, url_ssl):
        files = ['cognos']
        for f in files:
            self.make_sure_config_file_in_place(f)

class Contacts(LotusConnections):
    def setup_names(self):
        self.data_source_spec['jndi'] = "jdbc/scdb-connection"
        self.data_source_spec['name'] = "scdb-connection"
        self.known_roles = []

    def ear_opts_MapResRefToEJB(self):
        return []

    def ear_opts_default_datasource(self):
        return []

    def do_apply_install_options(self, opts):
        self.default_db_name['DB2'] = opts.get("dbName")
        self.was_variables["CONTACTS_CONTENT_DIR"] = "%s/contacts/upload" % self.data_dir
        self.roles_map = {}

    def ear_opts_BindJndiForEJBNonMessageBinding(self):
        return []

    def ear_opts_MapEJBRefToEJB(self):
        return []

class ContactsWeb(LotusConnections):
    def setup_names(self):
        self.data_source_spec = None
        self.known_roles = []

    def ear_opts_default_datasource(self):
        return []

    def do_apply_install_options(self, opts):
        self.roles_map = {}

    def ear_opts_BindJndiForEJBNonMessageBinding(self):
        return []

    def ear_opts_MapEJBRefToEJB(self):
        return []


class ConnectionsProxy(LotusConnections):
    def setup_names(self):
        self.data_source_spec = None
        self.known_roles = []

    def ear_opts_default_datasource(self):
        return []
    def ear_opts_BindJndiForEJBNonMessageBinding(self):
        return []
    def ear_opts_MapEJBRefToEJB(self):
        return []
    def ear_opts_MapResRefToEJB(self):
        return []


class URLPreview(LotusConnections):
    def setup_names(self):
        self.services_provides = [ 'opengraph', 'thumbnail' ]
        self.data_source_spec['jndi'] = "jdbc/oembed"
        self.default_db_name['DB2'] = "HOMEPAGE"
        self.default_db_name['SQL Server'] = "HOMEPAGE"
        self.default_db_user["Oracle"] = "HOMEPAGEUSER"
        self.default_db_user["SQL Server"] = "HOMEPAGEUSER"
        self.wm_specs = [
            {"name":"OembedWorkManager",
             "maxThreads":10, "numAlarmThreads":9,
             "category":"URLPreviewWorkManager",
             "description":"URLPreview Work Manager",
             "jndiName":"wm/oembed"} ]
        self.scheduler_specs = [
            {"name":"URLPreviewScheduler",
             "description":"URLPreview Scheduler",
             "jndiName":"scheduler/oembed",
             "datasourceJNDIName":self.data_source_spec['jndi'],
             "datasourceAlias":"%sJAASAuth" % self.component_name,
             "pollInterval":30,
             "tablePrefix":"HOMEPAGE.OEMBED_SCHEDULER_",
             "useAdminRoles":"false",
             "workManagerInfoJNDIName":"wm/oembed"} ]
        self.known_roles = ["person","reader","everyone","admin","search-admin","widget-admin","everyone-authenticated","metrics-reader","bss-provisioning-admin"]

    def customize_datasource(self, data_source):
        if self.db_type == "DB2":
            data_source.properties["currentSchema"] = "HOMEPAGE"

    def do_apply_install_options(self, opts):
        self.was_variables["URL_PREVIEW_CONTENT_DIR"] = self.data_dir + "/common/urlPreview/shared"
        self.roles_map["search-admin"] = ['No', 'No', "|".join(self.search_admin), "|".join(self.search_admin_groups)]
        self.roles_map["widget-admin"] = ['No', 'No', "|".join(self.widget_admin), "|".join(self.widget_admin_groups)]
        self.roles_map["admin"] = ['No', 'No', "|".join(self.administrators), "|".join(self.administrator_groups)]
        if self.multitenant:
            self.roles_map["everyone"] = ['No','Yes', '','']
            self.roles_map["reader"] = ['No','Yes','','']
            if self.bss_provisioning_admin_user:
                self.roles_map["bss-provisioning-admin"] = ['No', 'No', self.bss_provisioning_admin_user, '']

    def ear_opts_BindJndiForEJBNonMessageBinding(self):
        return []
    def ear_opts_MapEJBRefToEJB(self):
        return []
    def ear_opts_MapResRefToEJB(self):
        return []

    def update_additional_config_files(self, lcc, url, url_ssl):
        files = ['og-config.xsd', 'og-config.xml']
        for f in files:
            self.make_sure_config_file_in_place(f)


class PushNotification(LotusConnections):
    def setup_names(self):
        #self.data_source_spec['jndi'] = "jdbc/oembed"
        self.default_db_name['DB2'] = "PNS"
        self.default_db_name['SQL Server'] = "PNS"
        self.default_db_user["Oracle"] = "PNSSUSER"
        self.default_db_user["SQL Server"] = "PNSSUSER"
        self.cache_specs = [
            {"name": "%s.freshness" % self.component_name,
             "jndiName": "cache/%s" % self.component_name,
             "cacheSize": 2000, "defaultPriority": 1,
             "enableCacheReplication": "true",
             "replicationType": "NONE",
             "cacheReplication": [["messageBrokerDomainName", "ConnectionsReplicationDomain"]] }]
        self.wm_specs = [
           {"name":"%sWorkManager" % self.name,
            "jndiName":"workmanager/%s" % self.component_name,
            "category":"%sWorkManager" % self.name,
            "description":"%s Work Manager" % self.name,
            "maxThreads":20, "minThreads":1,
            "numAlarmThreads":7,
            "isGrowable":"false",
            "serviceNames":"security",
            "threadPriority":5, "workReqQSize":200, "workReqQFullAction":0 } ]
        self.tm_specs = [
           {"name": "%sTimerManager" % self.name,
            "jndiName": "timermanager/%s" % self.component_name,
            "category": "%sTimerManager" % self.name,
            "description": "%s Timer Manager" % self.component_name,
            "serviceNames": "", "numAlarmThreads": 7 } ]
        self.scheduler_specs = [
           {"name":"%sScheduler" % self.name,
            "description":"%s Scheduler" % self.name,
            "jndiName":"scheduler/%s" % self.component_name,
            "datasourceJNDIName": self.data_source_spec['jndi'],
            "datasourceAlias": "%sJAASAuth" % self.component_name,
            "pollInterval":"30",
            "tablePrefix":"PNS.SCHEDULER",
            "useAdminRoles":"true",
            "workManagerInfoJNDIName":"workmanager/%s" % self.component_name} ]
        self.known_roles = []

    def customize_datasource(self, data_source):
        if self.db_type == "DB2":
            data_source.properties["currentSchema"] = "PNS"

    def ear_opts_BindJndiForEJBNonMessageBinding(self):
        return []
    def ear_opts_MapEJBRefToEJB(self):
        return []
    def ear_opts_MapResRefToEJB(self):
        return []

    def update_additional_config_files(self, lcc, url, url_ssl):
        self.make_sure_config_file_in_place("pushnotification-config.xsd")
        f_path = self.make_sure_config_file_in_place("pushnotification-config.xml")
        pn_cfg = XmlFile(f_path)
        pn_cfg.modify("/tns:config/tns:db/@dialect", self.db_type)
        pn_cfg.save()


class ExtensionsRegistry(LotusConnections):
    def setup_names(self):
        self.services_provides = ['extensionRegistry']
        self.ejb_webui_name = "sn.scee"
        self.ejb_webui_xml = "scee.war,WEB-INF/web.xml"
        self.known_roles = []
        self.data_source_spec['ref'] = "jdbc/db"
        self.data_source_spec['jndi'] = "jdbc/extensibility"
        self.default_db_name['DB2'] = "HOMEPAGE"
        self.default_db_name['SQL Server'] = "HOMEPAGE"
        self.default_db_user["Oracle"] = "HOMEPAGEUSER"
        self.default_db_user["SQL Server"] = "HOMEPAGEUSER"

    def customize_datasource(self, data_source):
        if self.db_type == "DB2":
            data_source.properties["currentSchema"] = "HOMEPAGE"

    def do_apply_install_options(self, opts):
        self.roles_map = {}

    def ear_opts_BindJndiForEJBNonMessageBinding(self):
        return []
    def ear_opts_MapEJBRefToEJB(self):
        return []


class Extensions(LotusConnections):
    def setup_names(self):
        self.ejb_webui_name = "Extensions"
        self.ejb_webui_xml = "extensions.war,WEB-INF/web.xml"

    def ear_opts_default_datasource(self):
        return []

    def ear_opts_BindJndiForEJBNonMessageBinding(self):
        bindings = []
        bindings.append(
            ["EventPublisher",
             "EventPublisher",
             "lc.events.publish.jar,META-INF/ejb-jar.xml",
             "ejb/connections/extensions/events/publisher"])
        return [ '-BindJndiForEJBNonMessageBinding', bindings ]

    def ear_opts_MapEJBRefToEJB(self):
        opts = [['Extensions',
            '',
            'extensions.war,WEB-INF/web.xml ',
            'ejb/EventPublisher',
            'com.ibm.lconn.events.internal.publish.impl.JMSPublisherLocal ',
            'ejb/connections/extensions/events/publisher']]
        return ['-MapEJBRefToEJB', opts]

    def ear_opts_MapResRefToEJB(self):
        return []

class CCM(LotusConnections):
   def set_ear(self, ear_file):
     self.ear_file = None
     self.kit_dir = ''

   def setup_names(self):
      self.data_source_spec = None
      self.services_provides = [ 'ecm_files' ]
      self.notification_sources = ['ecm_files']

   def do_apply_install_options (self, options):
      self.jdbcVars = {
         "DB2_JCC_DRIVER_PATH": options.get("dbDriverPath"),
         "DB2UNIVERSAL_JDBC_DRIVER_PATH": options.get("dbDriverPath") }

   def set_was_variables(self):
      LotusConnections.set_was_variables(self)
      # The JDBC vars must be set at the node scope
      if isinstance(self.install_to, ServerCluster):
          nodes = self.install_to.nodes()
      else:
          nodes = [self.node]
      for k,v in self.jdbcVars.items():
          for n in nodes:
             wv = WebSphereVariable(k, v)
             wv.parent = n
             wv.set()

   def do_install(self, options):
      self.apply_install_options(options)
      self.set_was_variables()
      self.install_to.modify_jvm_settings(self.jvm_settings)
      self.install_to.modify_trace_settings(self.trace_settings)
      self.additional_post_install()
      self.update_config_files()
      AdminConfig.save()

   def get_ce_app(self):
      return Application("FileNetEngine")

   def get_fncs_app(self):
      return Application("navigator")

   def startFilenetApps(self):
      print "Starting Filenet Applications"
      app_mgr = AdminControl.queryNames('type=ApplicationManager,*')
      try:
         AdminControl.invoke(app_mgr, "startApplication", "FileNetEngine")
      except:
         print "Could not start FileNetEngine. Could have been started already."

      try:
         AdminControl.invoke(app_mgr, "startApplication", "navigator")
      except:
         print "Could not start navigator. Could have been started already."

   def map_to_web_servers(self):
      ce = self.get_ce_app()
      fncs = self.get_fncs_app()
      AdminApp.edit(ce.name, self.ear_opts_MapModulesToServers())
      AdminApp.edit(fncs.name, self.ear_opts_MapModulesToServers())
      AdminConfig.save()


