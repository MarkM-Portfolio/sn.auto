from baseTest import BaseTest
import subprocess
import driver
import platform
import re
import os
import json
class ldapTest(BaseTest):		
	def initialize(self, parameters):

		self.tests['Connection Confirmation'] = self.validateLdapConnection
		self.tests['Populated Portion Selected'] = self.ldapIsPopulated
		
		#connection test results
		self.resultMessages[10]	  = "PASS"
		self.resultMessages[11]   = "LDAP CONNECTION ERROR: unable to connect with provided credentials"
		
		#population test results
		self.resultMessages[20]	  = "PASS"
		self.resultMessages[21]   = "LDAP FILTER ERROR: unpopulated portion of ldap has been selected"
	
	def validateLdapConnection(self):
		
		directory = os.path.join(os.getcwd(),'lib','ldapTest.jar')
		print "PARAMS IN VALIDATE:" + str(driver.getParameters('wsadmin.user'))	
		#java_home = os.path.join(os.getenv('JAVA_HOME'), 'jre', 'bin', 'java')
		java_home = driver.getParameters('ldap.java')
	
		server = driver.getParameters('ldap.server')
		port = driver.getParameters('ldap.port')
		user = driver.getParameters('ldap.user')

		driver.outputMessage("LDAPTEST")
		driver.outputMessage(server)
		driver.outputMessage(port)
		driver.outputMessage(user)
		
		driver.outputMessage('    Attempting to connect to '+server+' on port '+port+' with user '+user)
		
		return int(subprocess.Popen([java_home,'-jar',directory,'validateLdapConnection',driver.getParameters('driver.configPath')], stdout=subprocess.PIPE, stderr=subprocess.STDOUT).communicate()[0])
		
	def ldapIsPopulated(self):
	
		directory = os.path.join(os.getcwd(),'lib','ldapTest.jar')
		#java_home = os.path.join(os.getenv('JAVA_HOME'), 'jre', 'bin', 'java')
		java_home = driver.getParameters('ldap.java')
	
		filter = driver.getParameters('ldap.filter')
		
		driver.outputMessage('    Looking for populated values with filter '+filter)
		
		output = subprocess.Popen([java_home,'-jar',directory,'ldapIsPopulated',driver.getParameters('driver.configPath')], stdout=subprocess.PIPE, stderr=subprocess.STDOUT).communicate()[0]
		if len(output.rstrip())>2:
			driver.outputMessage('    At least ' + output[2:].rstrip() + ' entries were returned')
		
		return int(output[:2])
