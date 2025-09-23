# ***************************************************************** 
#                                                                   
# IBM Confidential                                                  
#                                                                   
# OCO Source Materials                                              
#                                                                   
# Copyright IBM Corp. 2011, 2012                                          
#                                                                   
# The source code for this program is not published or otherwise    
# divested of its trade secrets, irrespective of what has been      
# deposited with the U.S. Copyright Office.                         
#                                                                   
# ***************************************************************** 

#
# run this from wsadmin
#   sudo /opt/IBM/WebSphere/bin/wsadmin.py -lang jython \
#        -user xxx -password yyy -f install.py
#
import java
import sys, traceback
import re
import string
import com.ibm.websphere.management.cmdframework

# to workaround the issue that the os module in WAS 6.1 doesn't know
# Windows Server 2003 is a NT type OS
windows_os = ["Windows .*"]
for os_pattern in windows_os:
    regex_obj = re.compile(os_pattern)
    os_name = java.lang.System.getProperty("os.name")
    if regex_obj.match(os_name):
        sys.registry.setProperty('python.os', 'nt')
        break

from  com.ibm.ws.scripting import ScriptingException
import os.path
import tempfile
import shutil
from com.ibm.lc.install import XmlFile
import lcapp

# important, make the AdminX object available in imported lcapp module
lcapp.set_wsadmin_refs((AdminConfig, AdminControl, AdminTask, AdminApp))

def usage():
    print "Usage:"
    print "  wsadmin -javaoption \"-Dpython.path=lib\" -wsadmin_classpath lib/lccfg.jar \\"
    print "          -f bin/install.py options_file [kit_dir]"
    print ""
    print "  options_file -  is a piece of Python/Jython script contains the install"
    print "                  options, such as server names, passwords, etc."
    print "                  see examples in samples directory"
    print "  kit_dir      -  [optional] is the diretory contains the extracted kit,"
    print "                  that has the EAR files, LC XML configuration files, and"
    print "                  jython admin scripts."
    print ""
    sys.exit(1)

def make_deployment_id():
    uuid = java.util.UUID.randomUUID()
    return "com.ibm.lc.%s" % uuid

def set_deployment_id(lcc_xml):
    f = XmlFile(lcc_xml)
    f.modify("/tns:config/tns:deployment/@deployment_id", make_deployment_id());
    f.save()

def set_dynamic_host(lcc_xml, host):
    if not host:
        return
    f = XmlFile(lcc_xml)
    f.modify("/tns:config/tns:dynamicHosts/@enabled", "true")
    f.modify("/tns:config/tns:dynamicHosts/tns:host/@href", "http://" + host)
    f.modify("/tns:config/tns:dynamicHosts/tns:host/@ssl_href", "https://" + host)
    f.save()

def set_session_cookie_name(lcc_xml, cookie_name):
    if not cookie_name: return
    f = XmlFile(lcc_xml)
    f.modify("/tns:config/tns:sessionCookies/tns:cookieName/@key", cookie_name)
    f.save()

def checkin_files_to_was_dir(params, dir, names):
    was_dir = params[0]
    prefix_to_remove = os.path.join(params[1],"")
    for n in names:
        local_path = os.path.join(dir, n)
        if os.path.isdir(local_path):
            continue
        was_path = local_path
        if was_path.find(prefix_to_remove) == 0:
            was_path = was_path[len(prefix_to_remove):]
        was_path = was_dir + was_path
        #print "checking in file:", was_path
        if AdminConfig.existsDocument(was_path):
            AdminConfig.deleteDocument(was_path)
        AdminConfig.createDocument(was_path, local_path)

def copy_dir_contents(src_dir, dst_dir):
    names = os.listdir(src_dir)
    for name in names:
        src = os.path.join(src_dir, name)
        dst = os.path.join(dst_dir, name)
        shutil.copy2(src, dst)

# If connected to a node, the process type can be one of the following:
#    UnManagedProcess, DeploymentManager, ManagedProcess, NodeAgent
# If not connected, returns None
def get_process_type():
    p_type = None
    try:
        obj_name = AdminControl.queryNames("node="+AdminControl.getNode()+",type=Server,*")
        p_type = AdminControl.getAttribute(obj_name, "processType")
    except ScriptingException, ex:
        pass
    print "ProcessType is: ", p_type
    return p_type

def dump_install_options(opts):
    for key,value in opts.items():
        if key.endswith("Password"):
            value = "*" * 8
        print "%25s : %s" % (key, value)

def install_license_files(common_options):
    lafiles_to = os.path.join(common_options['lcHome'], 'lafiles')
    lafiles_from = os.path.join(common_options['kit_dir'], 'lafiles')
    if os.path.exists(lafiles_from):
	if os.path.exists(lafiles_to): shutil.rmtree(lafiles_to)
        shutil.copytree(lafiles_from, lafiles_to)

def validate_install_options(common_options, apps_options):
    errors = []
    warnings = []
    required_options = [ 'installType', 'connectionsAdminUser', 'connectionsAdminPassword',
        'dbType', 'dbServer', 'dbPassword', 'dbDriverPath',
        'dataDirectory', 'lcHome' ]
    for o in required_options:
        if not common_options.get(o):
            errors.append("Option [%s] is required" % o)
    if not common_options.has_key("dataDirectoryLocal"):
        common_options["dataDirectoryLocal"] = common_options["dataDirectory"]
    errors += validate_cell_name(common_options)
    errors += validate_install_type(common_options)
    errors += validate_kit_directory(common_options)
    errors += validate_permissions(common_options)
    app_errors, app_warnings = validate_apps_selection(apps_options)
    errors += app_errors
    warnings += app_warnings
    for e in errors: print "ERROR:", e
    for w in warnings: print "WARNING:", w
    if errors: usage()

def validate_component_options(app_name, options):
    if app_name == "blogs" and options.has_key("blogsAdmin"):
        print "options 'blogsAdmin' is deprecated, please set 'admin' in Blogs"
        options['admin'] = options['blogsAdmin']

def validate_cell_name(common_options):
    errors = []
    cell = lcapp.Cell()
    if common_options.get('cell') is not None:
        cell_specified = lcapp.Cell(common_options['cell'])
        if cell_specified.exists():
            cell = cell_specified
        else:
            print "WARNING - cell name is NOT valid, use default cell [%s]" % cell.name
    common_options['cell'] = cell
    return errors

# make sure we are connected to the DeploymentManager when working on a ND type deployment
def validate_install_type(common_options):
    errors = []
    if common_options['installType'] == 'nd':
        conn_type = get_process_type()
        if conn_type != "DeploymentManager":
            errors.append("For Network Deployment installation, you must connect to the deployment manager." \
                "To do that, use -conntype, -host, -port, -user, -password options to connect to the" \
                "deployment manager through SOAP protocol.")
    return errors

def validate_kit_directory(common_options):
    errors = []
    if len(sys.argv) == 2:
        kit_dir = sys.argv[1]
        if not os.path.isdir(kit_dir):
            errors.append("The kit directory [%s] does not exist: " % kit_dir )
    else:
        kit_dir = os.getcwd()
    for tmpd in ["installableApps", "LotusConnections-config", "bin_lc_admin"]:
        if not os.path.isdir(os.path.join(kit_dir, tmpd)):
            errors.append("Invalid kit, unable to find \"%s\" in \"%s\"" % (tmpd, kit_dir))
    common_options['kit_dir'] = os.path.abspath(kit_dir)
    return errors

def validate_permissions(common_options):
    # TODO: finish this permission validation here
    errors = []
    conn_home = common_options['lcHome']
    try:
        if not os.path.exists(conn_home):
            os.mkdir(conn_home)
        elif os.path.isdir(conn_home):
            dummy = os.path.join(conn_home, "dummy.txt")
            if not os.path.exists(dummy):
                f = open(dummy, 'w')
                #f.write('')
                f.close()
            os.remove(dummy)
    except IOError, e:
        errors.append("Can not write to %s, check lcHome setting in cfg.py. (%s)" % (conn_home,e))
    return errors

def validate_apps_selection(app_options):
    errors = []
    warnings = []
    required_apps = ['common', 'widgetcontainer', 'proxy']
    for app_name in required_apps:
        if not apps_options.has_key(app_name):
            warnings.append("[%s] is required but not specified to install, force added" % app_name)
            apps_options[app_name] = {}
    return (errors, warnings)

def remove_obsolete_apps():
    print "Removing applications from old releases:"
    for app in ['ConnectionsCommon', 'connectionsProxy']:
        app_exists = AdminConfig.getid("/Deployment:%s/" % app) != ''
        if app_exists:
            print "  Remove obsoleted EAR \"%s\"" % app
            AdminApp.uninstall(app)
            AdminConfig.save()
        else:
            print "  Obsoleted EAR \"%s\" not found, good." % app

def create_connections_bus(inst_options):
    # in LC 2.5, the bus name is something like "Connections_server1_Bus" with standalone
    # and is "ConnectionsBus" in case it is ND deployment, let just use Connections_Bus
    old_bus_names = ['Connections_Bus', 'Connections_server1_Bus']
    try:
        bus_name = inst_options.get("SIBusName", "ConnectionsBus")
        for n in old_bus_names:
            if n == bus_name: continue
            old_bus = lcapp.SIBus(n)
            if old_bus.exists():
                print "  Found obsoleted SIBus \"%s\", removing ..." % n
                old_bus.remove()
        bus = lcapp.SIBus(bus_name)
        if inst_options.get("replaceSIBus", "false") == 'true':
            bus.remove()
        bus.create()
        bus.add_bus_connector_role(inst_options['connectionsAdminUser'])
        inst_options['connectionsBus'] = bus
    except ScriptingException, msg:
        print "ERROR - unable to create Connections bus"
        print msg
        sys.exit(1)

def configure_was_security(inst_options, oauth_filter):
    # Set the LC required WAS security settings
    print "Update WAS security settings:"
    was_security = lcapp.Security()
    print " - Enable WAS global security"
    was_security.enable_global_security()
    print " - Enable WAS Applications security"
    was_security.enable_application_security()
    print " - Set Web Authentication Behavior to PERSISTING"
    was_security.set_webauth("persisting")
    print " - Enable WebSphere Single Sign-On"
    was_security.sso("true")
    sso_domain = inst_options.get('ssoDomain')
    if sso_domain:
        print " - Set Single Sign-On domain to [%s]" % inst_options['ssoDomain']
        was_security.sso_domain(sso_domain)
    if was_security.sso_domain() == "":
        print "WARNING: I found your WebSphere Signle Sign On Domain is left blank, you can set"
        print "         it in cfg.py. Or, if you prefer to do this manually, see LC InfoCenter."
    if hasattr(AdminTask, 'enableOAuthTAI'):
        print " - Enable OAuth TAI"
        AdminTask.enableOAuthTAI()
        oauth_filters = ( "[-interceptor com.ibm.ws.security.oauth20.tai.OAuthTAI "
            "-customProperties [\"provider_1.name=connectionsProvider\","
            "\"provider_1.characterEncoding=utf-8\","
            "\"provider_1.filter=request-url^=" + string.join(oauth_filter, "|") + "\"] ]")
        AdminTask.configureInterceptor(oauth_filters)
    else:
        print "WARNING: there is no OAuth 2.0 libs with your WAS"
    if inst_options.get("realmName"):
        print " - Updating WAS Security Realm name"
        was_security.updateRealm(inst_options.get("realmName"))
    if inst_options.get('trustedRealm'):
        print " - Adding Trusted Realm"
        was_security.addTrustedRealm(inst_options.get('trustedRealm'))
    AdminConfig.save()
    print "WAS security settings saved"

# update the mail domain from "example.com" to the one specified
# in options
def update_notification_xml_mail_domain(common_options, tmp_lcc_dir):
    email_domain = common_options.get('notificationEmailDomain')
    if email_domain is None:
        print "No E-mail notification domain provided, skip update notification-config.xml"
        return
    print "Update notification-config.xml, set email domain to [%s]" % email_domain
    fi = open(os.path.join(common_options['kit_dir'], "LotusConnections-config", "notification-config.xml"))
    fo = open(os.path.join(tmp_lcc_dir, "notification-config.xml"), 'w')
    while 1:
        line = fi.readline()
        if not line: break
        line = line.replace("@example.com", "@" + email_domain)
        fo.write(line)
    fi.close()
    fo.close()

# Configure Multitenancy
def config_multitenancy(lcc_xml):
    f = XmlFile(lcc_xml)
    if not f.get("/tns:config/tns:properties"):
        print "Enable Multi-tenancy"
        mt_config = '    <properties>\r' + \
            '        <genericProperty name="LotusLive">true</genericProperty>\r' + \
            '        <genericProperty name="usersCanFollowContent">false</genericProperty>\r' + \
            '    </properties>\r'
        f.appendChild("/tns:config", mt_config)
        f.save()

# Configure the config engine
def config_ConfigEngine(common_options, lcc_xml):
    configEngine = common_options.get("configEngine") or "com.ibm.connections.mtconfig.provider.DbConfigProvider"
    f = XmlFile(lcc_xml)
    if not f.get("/tns:config/tns:configEngine"):
        print " - Enable Config Engine with provider: ", configEngine
        mt_config = '<configEngine configProvider="' + configEngine + '" />'
        f.appendChild("/tns:config", mt_config)
        f.save()

def disableWidgets(common_options, tmp_lcc_dir):
    w_xml = os.path.join(tmp_lcc_dir, "widgets-config.xml")
    w_path = "/tns:config/tns:resource[@type='community']/tns:widgets/tns:definitions/tns:widgetDef"
    f = XmlFile(w_xml)
    for w in common_options.get("disableWidgets", []):
        print "Disabling widget %s from Communities" % w
        f.setAttr(w_path + "[@defId='" + w.strip() + "']", "showInPalette", "false")
    f.save()

def config_jvms(jvm_opts):
    errors = []
    cell = lcapp.Cell()
    for n,v in jvm_opts.items():
        if n == "dmgr":
            node = AdminControl.getNode()
        else:
            node = n
        nodes = cell.nodes(node)
        if len(nodes) == 0:
            print "ERROR: Unable to determine node %s" % node
            continue
        servers = nodes[0].servers()
        if len(servers) == 0:
            print "ERROR: Unable to find servers on node %s" % nodes[0]
        for s in servers:
            if s.name == n or s.name == 'nodeagent':
                s.modify_jvm_settings(v)
    AdminConfig.save()

def config_coregroups():
    cell = lcapp.Cell()
    cg_property = [["name", "IBM_CS_WIRE_FORMAT_VERSION"], ["value","6.1.0"]]
    coreGroup = AdminConfig.getid('/Cell:' + cell.name + '/CoreGroup:DefaultCoreGroup/')
    existing_props = AdminConfig.list('Property', coreGroup).splitlines()
    for i in existing_props:
        prop_key = AdminConfig.showAttribute(i, 'name')
        if prop_key == "IBM_CS_WIRE_FORMAT_VERSION":
            print "Option IBM_CS_WIRE_FORMAT_VERSION exists. Removing old value."
            AdminConfig.remove(i)
    print "Option IBM_CS_WIRE_FORMAT_VERSION doesn't exist, creating"
    AdminConfig.create('Property', coreGroup, cg_property)
    AdminConfig.save()

def config_dynacache(common_options):
    dc_props = common_options.get('jvm_custom')
    prop_keys = []
    app_servers = AdminTask.listServers('[-serverType APPLICATION_SERVER ]').splitlines()
    for m in app_servers:
        server_id = m 
        jvm_id = AdminConfig.list("JavaVirtualMachine", server_id)
        existing_props = AdminConfig.list('Property', jvm_id).splitlines()
        for i in existing_props:
            prop_key = AdminConfig.showAttribute(i, 'name')
            if dc_props.has_key(prop_key):
                print "Option " + prop_key + " exists. Removing old value." 
                AdminConfig.remove(i)
        for k,v in dc_props.items(): 
            print "Creating " + k + " with value " + v
            AdminConfig.create('Property', jvm_id, [["name", k], ["value", v]])
    AdminConfig.save()    

def config_esi(esi_enable):
    webServers = AdminTask.listServers('[-serverType WEB_SERVER ]').splitlines()
    for ws in webServers:
        pluginProps = AdminConfig.list("PluginProperties", ws)
        ESIEnable = AdminConfig.showAttribute(pluginProps,"ESIEnable")
        print "ESIEnable attribute before change: " + ESIEnable
        AdminConfig.modify(pluginProps, [["ESIEnable", esi_enable]])
        ESIEnable2 = AdminConfig.showAttribute(pluginProps,"ESIEnable")
        print "ESIEnable attribute after change: " + ESIEnable2
    AdminConfig.save()

def config_ldap_nested_group():
    print "Disabling nested groups for repositories..."
    try:
        AdminTask.configureAdminWIMUserRegistry('[-customProperties [ "com.ibm.ws.wim.registry.grouplevel=1" ] ]')
    except com.ibm.websphere.management.cmdframework.CommandException:
        print "Unable to disable nested groups in wim config"

# we need to update the PATH and LD_LIBRARY_PATH or LIBPATH in the
# setupCmdLine script of the WAS profiles Search is running on, for
# stellent native binaries
def update_ws_profile_env():
    #TODO implement this
    pass

def get_application(name, kit_dir):
    classes = {
        "activities": lcapp.Activities, "communities": lcapp.Communities, "forums": lcapp.Forums,
        "profiles": lcapp.Profiles, "blogs" : lcapp.Blogs, "dogear": lcapp.Dogear,
        "homepage": lcapp.Homepage, "news": lcapp.News, "search": lcapp.Search,
        "files": lcapp.Files, "wikis": lcapp.Wikis, "mobile": lcapp.Mobile,
        "mobile.admin": lcapp.MobileAdmin, "metrics": lcapp.Metrics,
        "cognos": lcapp.Cognos,
        "moderation": lcapp.Moderation,
        "widgetcontainer": lcapp.WidgetContainer,
        "common": lcapp.Common,
        "proxy": lcapp.ConnectionsProxy,
        "help": lcapp.Help,
        "contacts": lcapp.Contacts,
        "urlpreview": lcapp.URLPreview,
        "contactsWeb": lcapp.ContactsWeb}
    ears = {
        "activities": os.path.join(kit_dir, "installableApps","oa.ear"),
        "blogs" :     os.path.join(kit_dir, "installableApps","blogs.ear"),
        "communities":os.path.join(kit_dir, "installableApps","communities.ear"),
        "forums":     os.path.join(kit_dir, "installableApps","forums.ear"),
        "dogear":     os.path.join(kit_dir, "installableApps","dogear.ear"),
        "homepage":   os.path.join(kit_dir, "installableApps","dboard.ear"),
        "profiles":   os.path.join(kit_dir, "installableApps","profiles.ear"),
        "news":       os.path.join(kit_dir, "installableApps","news.ear"),
        "search":     os.path.join(kit_dir, "installableApps","search.ear"),
        "files":      os.path.join(kit_dir, "installableApps","files.ear"),
        "wikis":      os.path.join(kit_dir, "installableApps","wikis.ear"),
        "metrics":    os.path.join(kit_dir, "installableApps","metrics.ear"),
        "cognos":     os.path.join(kit_dir, "installableApps","p2pd.ear"),
        "mobile":     os.path.join(kit_dir, "installableApps","mobile.ear"),
        "mobile.admin": os.path.join(kit_dir, "installableApps","mobile.admin.ear"),
        "moderation": os.path.join(kit_dir, "installableApps","moderation.ear"),
        "widgetcontainer":     os.path.join(kit_dir, "installableApps","widget.container.ear"),
        "common":     os.path.join(kit_dir, "installableApps","connections.common.ear"),
        "proxy":      os.path.join(kit_dir, "installableApps","connections.proxy.ear"),
        "help":       os.path.join(kit_dir, "installableApps","help.ear"),
        "urlpreview": os.path.join(kit_dir, "installableApps","oembed.ear"),
        "contacts":       os.path.join(kit_dir, "installableApps","contacts.service.ear"),
        "contactsWeb":       os.path.join(kit_dir, "installableApps","contacts.app.ear") }

    return classes[name](ears[name])


# note that the wsadmin Jython environment is different with the normal
# Jython, it does not include the script itself, so the sys.argv[0] is
# the first argument rather the name of our script itself.
if (len(sys.argv) < 1) or (len(sys.argv) > 2) :
    usage()

print "Loading options from file: ", sys.argv[0]
common_options = {}
apps_options = {}
if os.path.isfile(sys.argv[0]):
    execfile(sys.argv[0])
else:
    print "Error - %s not exist or is not a file." % sys.argv[0]
    usage()
print "options loaded"
print "common options loaded from file:", common_options
print "application specific options:", apps_options


# validate the installation options first
validate_install_options(common_options, apps_options)

# clean up stuff from older releases that could cause trouble
remove_obsolete_apps()

# create a tempory directory to hold those XML files under
# LotusConnections-config before they get checked into WAS
tmp_lcc_dir = tempfile.mktemp("LotusConnections-config")

# to avoid conflict between concurrent mulitiple administrative actions
AdminConfig.reset()

# we create connections bus here, since it is shared across all the components
create_connections_bus(common_options)

install_successful_list = []
install_failed_list = []
install_lcc_uploaded = "NO"
install_data_dir_created = "NO"
ws_security_updated = "NO"
oauth_filter = []
for app_name,opts in apps_options.items():
    if app_name == "forum":
        print 'WARNING: please use "forums" instead of "forum" in your apps_options'
        app_name = "forums"
    options = common_options.copy()
    options.update(opts)
    options['LotusConnections-config'] = tmp_lcc_dir
    print "Installation options for [%s]:" % app_name
    validate_component_options(app_name, options)
    dump_install_options(options)
    try:
        app = get_application(app_name, options['kit_dir'])
    except:
        print "ERROR - unable to get app definition of:", app_name
        traceback.print_exc()
        install_failed_list.append(app_name)
        continue
    if app.install(options):
        install_successful_list.append(app_name)
        if app.oauth_urls:
            oauth_filter.extend(app.oauth_urls)
        print "LotusConnections Component [%s] install is SUCCESS" % app_name
    else:
        install_failed_list.append(app_name)
        print "LotusConnections Component [%s] install is FAILED" % app_name

print "Saving to WAS master repository ..."
AdminConfig.save()
print "WAS configuration saved"

try:
    configure_was_security(common_options, oauth_filter)
    ws_security_updated = "YES"
except:
    print "ERROR occurred when configure WAS security, can be manually corrected after install"
    print "-" * 60
    traceback.print_exc()
    print "-" * 60

print "Update Coregroup settings"
config_coregroups()

if common_options.get('jvm_custom'):
    print "Update Dynacache Settings..."
    config_dynacache(common_options)

if common_options.get('jvm_options'):
    print "Update JVM Settings..."
    config_jvms(common_options.get('jvm_options'))

if common_options.get('ESIEnable'):
    print "Updating ESI Settings..."
    config_esi(common_options.get('ESIEnable'))

config_ldap_nested_group()

AdminConfig.save()

lc_admin_dir = os.path.join(common_options['kit_dir'], "bin_lc_admin")
try:
    print "Set the unique deployment_id in Lotus Connections config"
    lccfg_xml = os.path.join(tmp_lcc_dir, "LotusConnections-config.xml")
    set_deployment_id(lccfg_xml)
    set_dynamic_host(lccfg_xml, common_options.get("reverseProxyHost"))
    set_session_cookie_name(lccfg_xml, common_options.get("sessionCookieName"))
    update_notification_xml_mail_domain(common_options, tmp_lcc_dir)

    if common_options.get('multi-tenant'):
        config_multitenancy(lccfg_xml)
        config_ConfigEngine(common_options, lccfg_xml)

    if common_options.get('disableWidgets', []):
        disableWidgets(common_options, tmp_lcc_dir)

    if common_options.get('post-install-script'):
        post_install_script = os.path.abspath(common_options.get('post-install-script'))
        if os.path.isfile(post_install_script):
            if 0 != os.system(post_install_script + " " + tmp_lcc_dir):
                print 'WARNING - error running post install script: %s' % post_install_script

    if common_options.get("updateLCC", "true") == "false":
        print "Skip send LotusConnections-config to WebSphere"
        print "  As required, LotusConnections-config XML files are NOT copied to WebSphere."
        print "  You need manually update those files in WAS before start LotusConnections."
        print "  For your reference, new files are saved in [%s]." % tmp_lcc_dir
    else:
        print "Checkin LotusConnections-config files to WAS"
        os.path.walk(tmp_lcc_dir, checkin_files_to_was_dir,
            ("cells/%s/LotusConnections-config/" % common_options["cell"].name, tmp_lcc_dir))
        print "Remove temporay LotusConnections-config in", tmp_lcc_dir
        shutil.rmtree(tmp_lcc_dir)

    print "Checkin bin_lc_admin Jython scripts to WAS"
    os.path.walk(lc_admin_dir, checkin_files_to_was_dir, ("bin_lc_admin/", lc_admin_dir))

    was_install_bin = os.path.join(java.lang.System.getProperty('user.install.root'), 'bin')
    print "Copy admin jython script to:", was_install_bin
    copy_dir_contents(lc_admin_dir, was_install_bin)

    install_lcc_uploaded = "YES"
except:
    print "ERROR occurred during upload LotusConnections-config or Admin Jython script to WAS."
    print "      These directories can be manually copied to the WAS Deployment Manager."
    print "      1)", tmp_lcc_dir
    print "      2)", lc_admin_dir
    print "-" * 60
    traceback.print_exc()
    print "-" * 60


try:
    cust_dir = common_options.get('customizationDir',
            os.path.join(common_options['dataDirectory'], "customization"))
    print "Create Connections data and customization directories"
    print " - shared data directories are under [%s]" % common_options['dataDirectory']
    print " - local  data directories are under [%s]" % common_options['dataDirectoryLocal']
    print " - customization directories are under [%s]" % cust_dir
    for d in apps_options.keys():
        data_dir = os.path.join(common_options['dataDirectory'], d)
        if not os.path.exists(data_dir):
            os.makedirs(data_dir)
        cust_dir_comp = os.path.join(cust_dir, d)
        if not os.path.exists(cust_dir_comp):
            os.makedirs(cust_dir_comp)
    for d in ["common", "javascript", "themes"]:
        cust_p = os.path.join(cust_dir, d)
        if not os.path.exists(cust_p):
            print "  Create directory [%s]" % cust_p
            os.makedirs(cust_p)
    for d in ["catalog", "news", "search"]:
        data_dir = os.path.join(common_options['dataDirectoryLocal'], d)
        if not os.path.exists(data_dir):
            print "  Create directory [%s]" % data_dir
            os.makedirs(data_dir)
    install_data_dir_created = "YES"
except:
    print "ERROR occurred during create Data and customization directories."
    print "-" * 60
    traceback.print_exc()
    print "-" * 60

print "Install license files:"
try:
    install_license_files(common_options)
except:
    print "ERROR occurred install the license agreement files"
    print "-" * 60
    traceback.print_exc()
    print "-" * 60

print ""
print "Installation finished, here is the summary."
print "J2EE Applications deployed to WebSphere:"
print "    SUCCESS: ", install_successful_list
print "    FAILED:  ", install_failed_list
print "Upload LotusConnections-config configurations files to WebSphere: [%s]" % install_lcc_uploaded
print "Create LotusConnections data directories:                         [%s]" % install_data_dir_created
print "WebSphere security settings updated:                              [%s]" % ws_security_updated

if common_options['installType'] == 'nd':
    print "Fully resychronize all nodes in cell ..."
    common_options['cell'].resync_all_nodes()

if len(install_failed_list) > 0:
    print "There are components FAILED deploy, please check the log for errors"
    sys.exit(2)

