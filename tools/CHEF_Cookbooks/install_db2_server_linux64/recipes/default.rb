#
# Cookbook Name:: install_db2_server_linux64
# Recipe:: default
#
# Copyright 2015, IBM
#
# All rights reserved - Do Not Redistribute
#

db2_user = node[:db2][:user]
db2_group = node[:db2][:group]
scratch_dir = "#{Chef::Config[:file_cache_path]}/db2"

tar_url = ::URI.parse(node[:db2][:install_tar][:url]) 
tar_filename = ::File.basename(tar_url.path)
tar_file = "#{Chef::Config[:file_cache_path]}/#{tar_filename}" 

#Create scratch_dir
directory scratch_dir do
  group db2_group
  owner db2_user
  mode '0755'
end

#Transfer tar.gz from the remote location to the tar_file location
remote_file tar_file do
  source node[:db2][:install_tar][:url]
  user db2_user
  group db2_group
end

execute "unpack #{tar_filename}" do
  cwd scratch_dir
  command "tar -zxvf #{tar_file}"
  user db2_user
  group db2_group
end

execute "db2_install" do
  cwd scratch_dir
  command "#{scratch_dir}/universal/db2_install -b /opt/ibm/db2/V10.5 -p SERVER -n"
  user db2_user
  group db2_group
end

