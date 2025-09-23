# ***************************************************************** 
#                                                                   
# IBM Confidential                                                  
#                                                                   
# OCO Source Materials                                              
#                                                                   
# Copyright IBM Corp. 2011, 2013                                          
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
import os.system
import re

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

# If connected to a node, the process type can be one of the following:
#    UnManagedProcess, DeploymentManager, ManagedProcess, NodeAgent
# If not connected, returns None

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
    #app_errors, app_warnings = validate_apps_selection(apps_options)
    #errors += app_errors
    #warnings += app_warnings
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
    if (len(sys.argv) == 3) or (len(sys.argv) == 2):
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
    for app in ['ConnectionsCommon', 'connectionsProxy', 'MobileAdmin']:
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
if (len(sys.argv) < 1) or (len(sys.argv) > 3) :
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

#get applications from the third argument
if str(sys.argv[2]) != "all":#checking to see if there is a third argument
    appList=sys.argv[2]
    appArray=appList.split(',')
#if sys.argv[3] is empty then we want to get the keys from apps_options
else:
    appArray=apps_options.keys()

install_lcc_uploaded = "NO"
install_data_dir_created = "NO"
ws_security_updated = "NO"
oauth_filter = []
#alreadyUpdated has a somewhat misleading name, it is an array that is used only in smartUpdate mode
#that keeps track of apps that the user asked to update, but were already up to date (and as a result, were skipped over)
alreadyUpdated = []
#create an array of services provided by each app specified, fixes a one-to-many mapping problem later
appsAndServicesArray = []
#for app_name,opts in apps_options.items():
for app_name in appArray:
    
    current_version=""
    if app_name == "widgetcontainer":
		current_version=AdminApp.view('WidgetContainer','-buildVersion')
    else:
		current_version=AdminApp.view(app_name.title(),'-buildVersion') 
    
    opts=apps_options[app_name]

	
    if app_name == "forum":
        print 'WARNING: please use "forums" instead of "forum" in your apps_options'
        app_name = "forums"
    options = common_options.copy()
    options.update(opts)
    options['LotusConnections-config'] = tmp_lcc_dir
    print "Installation options for [%s]:" % app_name
    #validate_component_options(app_name, options)
    #dump_install_options(options)
    try:
        app = get_application(app_name, options['kit_dir'])
        appsAndServicesArray = appsAndServicesArray + app.services_provides

        new_version="0"
        if options.get('smartUpdate') == "true":
            #get versions of both ears
            earFile=str(app.ear_file)
            os.system("unzip -u " + earFile + " META-INF/MANIFEST.MF")
            manifest=open("META-INF/MANIFEST.MF","r").read()
            regex=re.compile("([0-9]{8})(-)([0-9]{4})(D)?")
            match=regex.search(manifest)
            new_version=match.group()
            match=regex.search(current_version)
            current_version=match.group()
            current_version=current_version.replace('-','')
            new_version=new_version.replace('-','')        
    except:
        print "ERROR - unable to get app definition of:", app_name
        traceback.print_exc()
        update_failed_list.append(app_name)
        continue
    
    if (options.get('smartUpdate') != "true") or (new_version != current_version):
        app.apply_install_options(options)
        app.update()
        app.update_config_files()
        app.laydown_web_resources()
        #check the above to figure out the mapping
        #look in lcapp
    #if we don't update it, we should remove it from the list so the xml will get updated properly later
    else:
        alreadyUpdated = alreadyUpdate + app.services_provides
        #now, when the xml is generated, it will grab the old version from the existing xml instead of
        #skipping over it, thinking it already called an update xml function for this app

print "Saving to WAS master repository ..."
AdminConfig.save()
print "WAS configuration saved"

#Changes to retain previously enabled applications

if common_options.get("updateLCC", "true") == "true":

    of=XmlFile(os.path.join(java.lang.System.getProperty('user.install.root'),"config","cells",common_options["cell"].name,"LotusConnections-config","LotusConnections-config.xml"))

    services=of.get("/tns:config/sloc:serviceReference[@enabled=\"true\"]/@serviceName")

    nf=XmlFile(os.path.join(tmp_lcc_dir,"LotusConnections-config.xml"))

    #copy over deployment value
    oldDepVal=str(of.get("/tns:config/tns:deployment/@deployment_id")[0])
    nf.modify("/tns:config/tns:deployment/@deployment_id", oldDepVal)

    #update the cookie name
    cookie=str(common_options.get("sessionCookieName"))
    nf.modify("/tns:config/tns:sessionCookies/tns:cookieName/@key", cookie)

    #update the WPI value
    WPI=str(common_options.get("enableWPI"))
    nf.modify("/tns:config/sloc:serviceReference[@serviceName=\"directory\"]/@profiles_directory_service_extension_enabled", WPI)

    for service in services:
        
        if (service in alreadyUpdated) or (not service in appsAndServicesArray):

            path_sref = "/tns:config/sloc:serviceReference[@serviceName=\"%s\"]" % service

            #check for bootstrapHost
            if (of.get(path_sref + "/@bootstrapHost")):
                bsHost=str(of.get(path_sref + "/@bootstrapHost")[0])
                nf.modify(path_sref + "/@bootstrapHost", bsHost)

            #check for bootstrapPort
            if (of.get(path_sref + "/@bootstrapPort")):
                bsPort=str(of.get(path_sref + "/@bootstrapPort")[0])
                nf.modify(path_sref + "/@bootstrapPort", bsPort)

            #check for clusterName
            if (of.get(path_sref + "/@clusterName")):
                cName=str(of.get(path_sref + "/@clusterName")[0])
                nf.modify(path_sref + "/@clusterName", cName)

            #handle urls
            url = of.get(path_sref + "/sloc:href/sloc:static/@href")
            if str(url) != "None":
                url = str(url[0])
                nf.modify(path_sref + "/sloc:href/sloc:static/@href", url)

                url_ssl = of.get(path_sref + "/sloc:href/sloc:static/@ssl_href")
                url_ssl = str(url_ssl[0])
                nf.modify(path_sref + "/sloc:href/sloc:static/@ssl_href", url_ssl)

                url_ssl = of.get(path_sref + "/sloc:href/sloc:interService/@href")
                url_ssl = str(url_ssl[0])
                nf.modify(path_sref + "/sloc:href/sloc:interService/@href", url_ssl)
        
            #We should set ssl_enabled either way
            nf.modify(path_sref + "/@ssl_enabled", "true")
            #We should make sure it's enabled
            nf.modify(path_sref + "/@enabled", "true")

    #Save the files
    of.save()
    nf.save()

#End of changes

lc_admin_dir = os.path.join(common_options['kit_dir'], "bin_lc_admin")

try:
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

if common_options['installType'] == 'nd':
    print "Fully resychronize all nodes in cell ..."
    common_options['cell'].resync_all_nodes()

