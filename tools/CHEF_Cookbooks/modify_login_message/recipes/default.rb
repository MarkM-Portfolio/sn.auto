#
# Cookbook Name: modify_login_message
# Recipe:: default
#
# Copyright 2015, YOUR_COMPANY_NAME
#
# All rights reserved - Do Not Redistribute
#
#

sshd_config = "/etc/ssh/sshd_config"
printMotd = /^#+PrintMotd\s+yes/

ruby_block "uncomment PrintMotd" do
  block do
    rc = Chef::Util::FileEdit.new(sshd_config)
    rc.search_file_replace(printMotd, 'PrintMotd yes')
    rc.write_file
  end
  only_if {::File.readlines(sshd_config).grep(printMotd).any? }
end
etc_motd = "/etc/motd"
login_message = "Welcome to the new login system, which was modifed by Jing"
#login_message_pattern = "/"+login_message+"/"
ruby_block "insert lines of login message" do
  block do
    file = Chef::Util::FileEdit.new(etc_motd)
    file.insert_line_if_no_match(/#{login_message}/, login_message)
    file.write_file
  end
end

