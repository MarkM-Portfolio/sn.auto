# Cookbook Name:: ci.chef.ihs.config_start
# Attributes:: default
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

#Step one, install prerequisites: 


include_recipe "apt"
include_recipe "java"

#Step two, install liberty for the backend and two servers

include_recipe "wlp"



wlp_server "testone" do
  config ({
            "featureManager" => {
              "feature" => [ "jsp-2.2" ]
            },
            "httpEndpoint" => {
              "id" => "defaultHttpEndpoint",
              "host" => "*",
              "httpPort" => "9080",
              "httpsPort" => "9443"
            }
          })
  action :create
end

directory "#{node['wlp']['base_dir']}/wlp/usr/servers/testone/dropins/test.war" do
   recursive true
end

cookbook_file "#{node['wlp']['base_dir']}/wlp/usr/servers/testone/dropins/test.war/testone.jsp" do
  backup false
  source "testone.jsp"
  action :create_if_missing
end

wlp_server "testone" do
  action :start
end



wlp_server "testtwo" do
  config ({
            "featureManager" => {
              "feature" => [ "jsp-2.2" ]
            },
            "httpEndpoint" => {
              "id" => "defaultHttpEndpoint",
              "host" => "*",
              "httpPort" => "9081",
              "httpsPort" => "9444"
            }
          })
  action :create
end

directory "#{node['wlp']['base_dir']}/wlp/usr/servers/testtwo/dropins/test.war" do
   recursive true
end

cookbook_file "#{node['wlp']['base_dir']}/wlp/usr/servers/testtwo/dropins/test.war/testtwo.jsp" do
  backup false
  source "testtwo.jsp"
  action :create_if_missing
end

wlp_server "testtwo" do
  action :start
end


#Step three, create a frontend server

include_recipe "ihs" # note, also installs IM

#Step four, "transfer" the config to the server and then apply it

include_recipe "ihs::robin_collect"
include_recipe "ihs::robin_config"


#Step five, download some pages to /tmp so that you can test for their existance later. 

#in my experience it suceeds twice then fails twice - since I have been bad and made round robin urls that only work on one server
bash "download from server one through the proxy" do
  user "root"
  ignore_failure true
  cwd "/tmp"
  code <<-EOH
  wget localhost/test/testone.jsp || wget localhost/test/testone.jsp || wget localhost/test/testone.jsp || wget localhost/test/testone.jsp || wget localhost/test/testone.jsp || wget localhost/test/testone.jsp || wget localhost/test/testone.jsp || wget localhost/test/testone.jsp || wget localhost/test/testone.jsp || wget localhost/test/testone.jsp
  EOH
end


bash "download from server two through the proxy" do
  user "root"
  ignore_failure true
  cwd "/tmp"
  code <<-EOH
  wget localhost/test/testtwo.jsp || wget localhost/test/testtwo.jsp || wget localhost/test/testtwo.jsp || wget localhost/test/testtwo.jsp || wget localhost/test/testtwo.jsp || wget localhost/test/testtwo.jsp || wget localhost/test/testtwo.jsp || wget localhost/test/testtwo.jsp || wget localhost/test/testtwo.jsp || wget localhost/test/testtwo.jsp
  EOH
end 
