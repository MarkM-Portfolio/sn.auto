#<> Where to find the front end IBM HTTP Server. If left to nil then this will fall back to [:ihs][:paths][:install]. If you wish to run configihs One of the two variables __must__ be set. 
default[:Robin_Config][:frontEnd][:IHS][:baseDir] = nil

#<> Where to find the Web Server Plug-ins for IBM WebSphere Application Server V8.5. If left to nil then this will fall back to [:ihs][:paths][:plugins]. If you wish to run configihs one of the two variables __must__ be set. 
default[:Robin_Config][:frontEnd][:IHS][:pluginBaseDir] = nil

#<> The reponse file and optionally a secure storage file for install manager to install IHS

default[:Robin_Config][:frontEnd][:IHS][:install][:responseFile] = nil
default[:Robin_Config][:frontEnd][:IHS][:install][:secureStorageFile] = nil

#<> Peramaters to override settings in the final config-xml these should be set on the front end server
default[:Robin_Config][:frontEnd][:plugincfg][:logPath] = nil
default[:Robin_Config][:frontEnd][:plugincfg][:hostname] = "*"
default[:Robin_Config][:frontEnd][:plugincfg][:ports] = ["80", "443"]

