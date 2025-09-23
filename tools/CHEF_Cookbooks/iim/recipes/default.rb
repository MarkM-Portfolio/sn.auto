#
# Cookbook Name:: iim
# Provider:: default
#
# (C) Copyright IBM Corporation 2013.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

# TODO: some way to check if IIM is already installed, and don't do anything if it is.

im_user = node[:im][:user]
im_group = node[:im][:group]
im_base_dir = node[:im][:base_dir]
im_data_dir = node[:im][:data_dir]
im_home_dir = node[:im][:user_home_dir]
scratch_dir = "#{Chef::Config[:file_cache_path]}/iim"

group im_group do
  not_if { im_group == node['root_group'] }
end

user im_user do
  comment 'IBM Installation Manager'
  gid im_group
  home im_home_dir
  shell '/bin/sh'
  manage_home true
  system true
  not_if { im_user == 'root' }
end

[im_base_dir, im_data_dir, scratch_dir].each do |dirname|
  directory dirname do
  group im_group
  owner im_user
    mode '0755'
  recursive true
  end
end



zip_file = node[:im][:install_zip][:file]

if zip_file.nil?
  zip_uri = ::URI.parse(node[:im][:install_zip][:url])
  zip_filename = ::File.basename(zip_uri.path)
  zip_file = "#{Chef::Config[:file_cache_path]}/#{zip_filename}" 
  remote_file zip_file do
    source node[:im][:install_zip][:url]
    user im_user
    group im_group
  end
else
  zip_filename = ::File.basename(zip_file)
end

package 'unzip' unless platform?('aix')

execute "unpack #{zip_filename}" do
  cwd scratch_dir
  command "unzip #{zip_file}" 
  user im_user
  group im_group
  creates "#{scratch_dir}/userinstc"
end

if im_user == 'root'
  execute 'imcl install' do
    command "#{scratch_dir}/tools/imcl install com.ibm.cic.agent -repositories #{scratch_dir}/repository.config -installationDirectory #{im_base_dir}/eclipse -dataLocation #{im_data_dir} -accessRights admin -acceptLicense"
    user im_user
    im_group im_group
  end
else
  execute 'imcl install' do
    command "#{scratch_dir}/tools/imcl install com.ibm.cic.agent -repositories #{scratch_dir}/repository.config -installationDirectory #{im_base_dir}/eclipse -dataLocation #{im_data_dir} -accessRights nonAdmin -acceptLicense"
    user im_user
    group im_group
    # allow to create executable files and allow to read and write for others in the same group but not execution, read for others
    # if this is not set the installer will fail because it cannot lock files below /opt/IBM/IM/installationLocation/configuration
    # see https://www-304.ibm.com/support/docview.wss?uid=swg21455334
    umask '013'
  end
end
