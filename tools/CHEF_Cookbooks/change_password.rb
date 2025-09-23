require 'rbconfig'
require 'date'
new_password = 'asdfe'
user = 'jcblackm'
warning_time = 14 # 2 weeks


warning_date = Time.new().to_date + warning_time 
case RbConfig::CONFIG['host_os']
when /linux/ # RHEL
	expiry = `passwd "#{user}" -S`
	expiry = expiry.split(' ')
	creation = Date.parse(expiry[2])
	expiry = creation + expiry[4].to_i
	`echo #{new_password} | passwd #{user} --stdin`
when /mswin|msys|mingw|cygwin|bccwin|wince|emc/ # Windows
	expiry = `net user "#{user}"`
	expiry = expiry.partition('Password expires'+' '*13)[2]
	expiry = expiry.partition(' ')[0]
	expiry = Date.strptime(expiry, '%m/%d/%Y')
	`net user #{user} #{new_password}`
end
