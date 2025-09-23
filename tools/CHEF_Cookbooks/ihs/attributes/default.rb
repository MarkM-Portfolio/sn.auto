#<> The path to the shared directory for IBM products
default[:ihs][:paths][:shared] = "/opt/IBM/IMShared"
#<> The path where IHS should be installed
default[:ihs][:paths][:install] = "/opt/IBM/HTTPServer"
#<> The path where IHS internal eclipse tools should be installed
default[:ihs][:paths][:eclipse] = node[:ihs][:paths][:install]
#<> The path where Web Server Plug-ins for IBM WebSphere Application Server should be installed
default[:ihs][:paths][:plugins] = "/opt/IBM/WebSphere/Plugins"
#<> The archetecture being installed on
default[:ihs][:settings][:arch] = "x86"
#<> The port for IHS 
default[:ihs][:settings][:port] = "80"
#<> Install a 64 or 32 bit version? Defaults to checking automatically. 
default[:ihs][:install][":bits"] = ""

#<> The location of the repository to download IHS from, this attribute is REQUIRED
default[:ihs][:install][:repositoryLocation] = nil
#<> The location of the master password file IIM should use to access the secure storage file, this attribute is optional
default[:ihs][:install][:masterPasswordFile] = nil
#<> The location of the secure storage file IIM should use to access the repoistory, this attribute is optional
default[:ihs][:install][:secureStorageFile] = nil
#<> The version to install. If left as an empty string it will default to the latest available version. Must be in the following format: version='VERSION_NUMBER'
default[:ihs][:install][:versionString] = ""
