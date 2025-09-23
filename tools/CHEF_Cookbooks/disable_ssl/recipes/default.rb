#
# Cookbook Name:: disable_ssl
# Recipe:: default
#
# Copyright 2015, YOUR_COMPANY_NAME
#
# All rights reserved - Do Not Redistribute
#
file_name = "/opt/IBM/HTTPServer/conf/httpd.conf"
#serverName = /ServerName\s+lcauto100.swg.usma.ibm.com/
#customLog = /CustomLog logs\/+ssl_access_log\s+combined/
newline = "SSLEnable\nSSLProtocolDisable SSLv2 SSLv3\nSSLAttributeSet 471 1"

ruby_block "insert SSLEnable lines" do
  block do
    rc = Chef::Util::FileEdit.new(sshd_config)
    rc.insert_line_after_match(serverName, newline)
    rc.write_file
  end
  only_if {::File.readlines(file_name).grep(serverName).any? }
end

