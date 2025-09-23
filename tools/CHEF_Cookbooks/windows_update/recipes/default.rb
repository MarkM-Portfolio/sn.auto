#
# Cookbook Name:: windows_update
# Recipe:: default
#
# Copyright 2015, IBM
#
# All rights reserved - Do Not Redistribute
#

CACHE = Chef::Config[:file_cache_path]
UPDATE_SCRIPT = ::File.join(CACHE , "Get-WUInstall.ps1")
REBOOT_SCRIPT = ::File.join(CACHE, "Get-WURebootStatus.ps1")

template UPDATE_SCRIPT do
	source 'Get-WUInstall.ps1.erb'
end

#template REBOOT_SCRIPT do
#	source 'Get-WURebootStatus.ps1.erb'
#end

execute 'Set execution policy' do
	command "powershell \"set-ExecutionPolicy unrestricted\""
end

execute 'Windows update' do
	timeout 10800
	puts "Running windows update..."
	cwd CACHE
	command "powershell \"./Get-WUInstall.ps1 -AcceptAll -IgnoreReboot | Out-File UpdateOutput.txt\""
end

reboot 'Restart the node at the end of a chef-client run' do
	action :request_reboot
end
