=begin
#<
This recipe calls IIM and uses it to install IHS
#>
=end

# Cookbook Name:: ihs
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
require 'tempfile'

#TODO politely exit if node[:ihs][:install][:repositoryLocation] is nil

include_recipe "iim"


bits = %x(uname -m | grep -o "^[^_]*" | sed -e 's/i6/i3/g')
if bits.start_with?('x') #as in x86_64
  node.default[:ihs][:install][":bits"] = '64'
elsif bits.start_with?('i') #as in i386 or i686
  node.default[:ihs][:install][":bits"] = '32'
end

responseFile = Tempfile.new('response.xml')
responseFilePath = responseFile.path

directory "/opt/IBM" do
  group node[:im][:group]
  owner node[:im][:user]
  mode "0755"
  recursive true
end

template responseFilePath do
   source "install_response_file.xml.erb"
   action :create
   group node[:im][:group]
   owner node[:im][:user]
   #all variables are set in the erb itself.
end        

iim_iim 'ihs' do 
   response_file responseFilePath
   master_password_file node[:ihs][:install][:masterPasswordFile] #all these default to null, and depending on your repositories security settings they might be able to remain that way. 
   secure_storage_file node[:ihs][:install][:secureStorageFile]
   action :install
end

template "/etc/init.d/ibm-http" do
   source "IBM-http.sh.erb"
   group "root"
   owner "root"
   mode "0755"
end

service "ibm-http" do
  supports :restart => true, :reload => true
  action [ :enable ]
  provider Chef::Provide::Service:AixInit if platform?('aix')
end

