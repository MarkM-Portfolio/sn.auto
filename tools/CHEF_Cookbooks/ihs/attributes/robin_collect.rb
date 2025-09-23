
#<> The location of tools.jar on the liberty servers, this is needed to generate plugin-cfg.xml. As an alternative you can put a copy of tools.jar into the DMZIHSLiberty/files/defualt directory and leave this peramater as nil. 
default[:Robin_Config][:backEnd][:java][:toolsPath] = nil

#<> How the liberty servers should search for the front end IBM HTTP Server. This must be set on the back end. Examples of valid values for pattern: "hostname:<name>", "name:<chef-node-name>", "role:<chef-role>". If multiple results are returned this recipe will send the plugin-cfg.xml files to all of them. For more details see: https://docs.getchef.com/essentials_search.html#partial-search
default[:Robin_Config]["backEnd"]["findGateway"]["pattern"] = nil
default[:Robin_Config]["backEnd"]["findGateway"]["networkInterface"] = "eth0"

#<> If this is set Chef will use the IPs provided instead of searching for the servers. This value takes an array of ips.
default[:Robin_Config][:backEnd][:findGateway][:overrideIP] = nil


#<> The back end liberty servers require the ability to SSH to the front end. !Warning, this recipy can use SSH usernames and passwords, this is NOT recommended. 

default[:Robin_Config][:backEnd][:SSH][:username] = nil
default[:Robin_Config][:backEnd][:SSH][:password] = nil
default[:Robin_Config][:backEnd][:SSH][:supressUnkownHostsWarning] = "true"

default[:Robin_Config][:backEnd][:SSH][:override] = nil # if not nil, a custom SSH string will be used.


