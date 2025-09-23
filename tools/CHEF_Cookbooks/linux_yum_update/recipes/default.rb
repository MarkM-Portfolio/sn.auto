#
# Cookbook Name:: linux_yum_update
# Recipe:: default
#
# Copyright 2015, IBM
#
# All rights reserved - Do Not Redistribute
#

execute "run yum update" do
  command "yum update -y"
end
