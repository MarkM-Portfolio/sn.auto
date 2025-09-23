#
# Cookbook Name:: nfsaccess
# Recipe:: default
#
# Copyright 2015, YOUR_COMPANY_NAME
#
# All rights reserved - Do Not Redistribute
#
#This cookbook install nfs and run the script to validate the username/uid and groupname/gid mapping between a NFS client and the NFS server.
#

#Install NFS client and server
package("nfs-utils")
package("ftp")

directory "/tmp/nfsaccess" do
end

template "/tmp/nfsaccess/irischeckuidgid" do
    source "irischeckuidgid.erb"
    owner "root"
    group "root"
    mode "0755"
end

execute "run the script to validate uid and gid" do
    command "./tmp/nfsaccess/irischeckuidgid"
end

cron "run the script" do
    minute '1'
    #hour '6'
    #day '1'
    command "./tmp/nfsaccess/irischeckuidgid"
end
