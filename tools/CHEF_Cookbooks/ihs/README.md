# Description

The __ihs__ cookbook uses IBM's Install Manager to install IBM HTTP Server.

## Basic Use

The __ihs__ cookbook only requires an IBM repository that contains IHS. The attribute [:ihs][:install][:repositoryLocation] __must__ be set to he location of your repository.

If you do not know where to find a repository, you can start at this page: http://pic.dhe.ibm.com/infocenter/wasinfo/v8r5/index.jsp?topic=%2Fcom.ibm.websphere.installation.express.doc%2Fae%2Fcins_repositories.html


You may also need to set [:ihs][:install][:secureStorageFile] to the location of your secure storage file in order to access a repository, and if your secure storage file is password protected you will also need to point [:ihs][:install][:masterPasswordFile] to your master password file.

This cookbook also supports keyfiles, however keyfiles are depreciated and we do not recommend their use.

# Requirements

## Platform:

* Linux

## Cookbooks:

* iim

# Attributes

* `node[:ihs][:paths][:shared]` - The path to the shared directory for IBM products. Defaults to `"/opt/IBM/IMShared"`.
* `node[:ihs][:paths][:install]` - The path where IHS should be installed. Defaults to `"/opt/IBM/HTTPServer"`.
* `node[:ihs][:paths][:eclipse]` - The path where IHS internal eclipse tools should be installed. Defaults to `"node[:ihs][:paths][:install]"`.
* `node[:ihs][:paths][:plugins]` - The path where Web Server Plug-ins for IBM WebSphere Application Server should be installed. Defaults to `"/opt/IBM/WebSphere/Plugins"`.
* `node[:ihs][:settings][:arch]` - The archetecture being installed on. Defaults to `"x86"`.
* `node[:ihs][:settings][:port]` - The port for IHS. Defaults to `"80"`.
* `node[:ihs][:install][":bits"]` - Install a 64 or 32 bit version? Defaults to checking automatically. Defaults to `""`.
* `node[:ihs][:install][:repositoryLocation]` - The location of the repository to download IHS from, this attribute is REQUIRED. Defaults to `"nil"`.
* `node[:ihs][:install][:masterPasswordFile]` - The location of the master password file IIM should use to access the secure storage file, this attribute is optional. Defaults to `"nil"`.
* `node[:ihs][:install][:secureStorageFile]` - The location of the secure storage file IIM should use to access the repoistory, this attribute is optional. Defaults to `"nil"`.
* `node[:Robin_Config][:backEnd][:java][:toolsPath]` - The location of tools.jar on the liberty servers, this is needed to generate plugin-cfg.xml. As an alternative you can put a copy of tools.jar into the DMZIHSLiberty/files/defualt directory and leave this peramater as nil. Defaults to `"nil"`.
* `node[:Robin_Config]["backEnd"]["findGateway"]["pattern"]` - How the liberty servers should search for the front end IBM HTTP Server. This must be set on the back end. Examples of valid values for pattern: "hostname:<name>", "name:<chef-node-name>", "role:<chef-role>". If multiple results are returned this recipe will send the plugin-cfg.xml files to all of them. For more details see: https://docs.getchef.com/essentials_search.html#partial-search. Defaults to `"nil"`.
* `node[:Robin_Config]["backEnd"]["findGateway"]["networkInterface"]` -  Defaults to `"eth0"`.
* `node[:Robin_Config][:backEnd][:findGateway][:overrideIP]` - If this is set Chef will use the IPs provided instead of searching for the servers. This value takes an array of ips. Defaults to `"nil"`.
* `node[:Robin_Config][:backEnd][:SSH][:username]` - The back end liberty servers require the ability to SSH to the front end. !Warning, this recipy can use SSH usernames and passwords, this is NOT recommended. Defaults to `"nil"`.
* `node[:Robin_Config][:backEnd][:SSH][:password]` -  Defaults to `"nil"`.
* `node[:Robin_Config][:backEnd][:SSH][:supressUnkownHostsWarning]` -  Defaults to `"true"`.
* `node[:Robin_Config][:backEnd][:SSH][:override]` -  Defaults to `"nil # if not nil, a custom SSH string will be used."`.
* `node[:Robin_Config][:frontEnd][:IHS][:baseDir]` - Where to find the front end IBM HTTP Server. If left to nil then this will fall back to [:ihs][:paths][:install]. If you wish to run configihs One of the two variables __must__ be set. Defaults to `"nil"`.
* `node[:Robin_Config][:frontEnd][:IHS][:pluginBaseDir]` - Where to find the Web Server Plug-ins for IBM WebSphere Application Server V8.5. If left to nil then this will fall back to [:ihs][:paths][:plugins]. If you wish to run configihs one of the two variables __must__ be set. Defaults to `"nil"`.
* `node[:Robin_Config][:frontEnd][:IHS][:install][:responseFile]` - The reponse file and optionally a secure storage file for install manager to install IHS. Defaults to `"nil"`.
* `node[:Robin_Config][:frontEnd][:IHS][:install][:secureStorageFile]` -  Defaults to `"nil"`.
* `node[:Robin_Config][:frontEnd][:plugincfg][:logPath]` - Peramaters to override settings in the final config-xml these should be set on the front end server. Defaults to `"nil"`.
* `node[:Robin_Config][:frontEnd][:plugincfg][:hostname]` -  Defaults to `"*"`.
* `node[:Robin_Config][:frontEnd][:plugincfg][:ports]` -  Defaults to `"[ ... ]"`.

# Recipes

* [ihs::default](#ihsdefault)
* ihs::robin_collect
* ihs::robin_config

## ihs::default

This recipe calls IIM and uses it to install IHS

# License and Maintainer

Maintainer:: IBM (<>)

License:: Apache 2.0
