#Cookbook Name:: install_db2_server_linux64
#Attributes:: default
#
#<> User and Group name under which the server will be installed and running.
default[:db2][:user] = 'root'
default[:db2][:group] = 'root'

#<> Base installation directory.
default[:db2][:base_dir] = '/opt/ibm/db2'

#<
## The db2_10.5 install tar.gz url. Set this if the installer is on a remote fileserver.
#>
default[:db2][:install_tar][:url] = 'http://yguobsd.swg.usma.ibm.com/software/DB2/db2_10.5/v10.5fp5_linuxx64_universal_fixpack.tar.gz'
