#
# Cookbook Name:: trust_host_key
# Recipe:: default
#
# Copyright 2015, YOUR_COMPANY_NAME
#
# All rights reserved - Do Not Redistribute
#

execute 'add icautomation.cnx.cwp.pnp-hcl.com pub key to known_hosts' do
  command "ssh-keyscan icautomation,9.32.151.149 >> ~/.ssh/known_hosts"
end
