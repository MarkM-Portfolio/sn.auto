require 'itsas_query'
require 'thread'

Windows_update_text = 'This update is available via Windows Update'
Linux_update_text =  %q(
Before applying this update, make sure all previously released errata
relevant to your system have been applied

For details on how to apply this update, refer to:

https://access.redhat.com/articles/11258
)

never_installed = [
=begin
	'Tivoli',
	'Flash',
	'ClearQuest',
	'Domino',
	'Sametime',
	'Jazz',
	'Quickr',
	'php',
	'AppScan',
	'Sterling',
	'Notes',
	'CICS',
	'IBM Systems Director',
	'Communications Server',
	'Cognos',
	'Security',
	'Endpoint Manager',
	'Websphere Portal',
	'Rational ClearCase',
	'WebSphere MQ',
	'Directory Server',
	'Process Server',
	'Message Broker',
	'Process Manager',
=end
]

always_installed = [
	'Internet Explorer',
	'Tivoli Directory Integrator',
	'python',
	'IBM HTTP Server',
	'WebSphere Application Server',
	'IBM SDK, Java Technology Edition',
]

if ARGV[0] == nil or ARGV[0] == ''
	puts 'Username: '
	STDOUT.flush
	username = STDIN.gets.chomp
else
	username = ARGV[0].chomp
end
if ARGV[1] == nil or ARGV[1] == ''
	puts 'Password: '
	STDOUT.flush
	password = STDIN.gets.chomp
else
	password = ARGV[1].chomp
end
if ARGV[2] == nil or ARGV[2] == ''
	puts 'Role (Delegate, Person Responsible): '
	STDOUT.flush
	role = STDIN.gets.chomp
else
	if ARGV[2].chomp == 'Person' and ARGV[3].chomp == 'Responsible'
		role = 'Person Responsible'
	else
		role = ARGV[2].chomp
	end
end

if !authenticate(username, password)
	puts 'Incorrect username/password.'
	exit
end

hostnames = get_all_hostnames(role, false)
apars = get_apars(hostnames, role)

threads = []
thread_mutex = Mutex.new
i = 0

apars.length.times do
	threads << Thread.new do
		apar = nil
		thread_mutex.synchronize do
			apar = apars.values[i]
			i += 1
		end
		
		#run through keywords comparing apar descriptions to update text
		for keyword in always_installed
			if apar['Title'].downcase.include? keyword.downcase
				puts "Apar '#{apar['Title']}' matched filter '#{keyword}'..."
				if apar['Description'].include? Windows_update_text
					puts "Apar '#{apar['Title']}'s Description matched windows update text, auto-updating..."
					exit
					#call chef script to resolve apar issue
					for hostname in apar['Applicable Machines'] do
						# Call chef script
						resolve_apar(hostname, apar['id'], role, 'PATCH_APPLIED')
					end
				elsif apar['Description'].include? Linux_update_text
					puts "Apar '#{apar['Title']}'s Description matched yum update text, auto-updating..."
					exit
					for hostname in apar['Applicable Machines'] do
						# Call chef script
						resolve_apar(hostname, apar['id'], role, 'PATCH_APPLIED')
					end
				end
			end
		end
		#run through list of software that will not be installed and remove the apars, they are N/A
		for keyword in never_installed
			if apar['Title'].downcase.include? keyword.downcase
				puts "Apar '#{apar['Title']}' matched filter '#{keyword}', resolving as not installed..."
				exit
				for hostname in apar['Applicable Machines'] do
					puts hostname
					resolve_apar(hostname, apar['id'], role, 'N/A')
				end
				exit
			end
		end		
	end
end
threads.each(&:join)
