#
# Cookbook Name:: authentication_root_icchefstage1
# Recipe:: default
#
# Copyright 2015, YOUR_COMPANY_NAME
#
# All rights reserved - Do Not Redistribute
#
pub_key = "ssh-rsa AAAAB3NzaC1yc2EAAAABIwAAAQEA5cZ9+9YBCgw14m9GZIwMC3UV0sJs2jbsJHHQ5jKkDkjP6vuErMRAnFSTjm2jBHkFQzpgxZE5yz9SfztBMZ3XYqsYm77+UInfod0AzWZ8HNNt6LLxMwG4f2bw7dMByF+jjr7fxtvB8ZsHTLok6nkzzz/GOQwfKQCfho4KCIZz2sva4mhGgwWKM3FqNOc7XeHtsgzsSCp7otbv+WMe4mbbu8Gf1XlI8sP7i7SKqH/gGlXDATyV4PzW9y3JvflrKnOawF1cq5coqSmozcfoAP+X0OC29ZOwK1kYpn4gkvN1Hm1OQ4I1PBIi0wjBfXWNKtara4cC2twGhy4dl5amX3xXEQ== root@icchefstage1"
file "#{ENV['HOME']}/.ssh/authorized_keys" do
 
end

ruby_block "append_root_icchefstage1_pub_key" do
  block do
    file = Chef::Util::FileEdit.new("#{ENV['HOME']}/.ssh/authorized_keys")
    file.insert_line_if_no_match(/#{pub_key}/, pub_key)
    file.write_file
  end
end
