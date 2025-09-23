#IHSPTest

from baseTest import BaseTest
import driver
import ctypes,os,platform,stat,sys

#lines marked with dual-pound (##) will be re-implemented later,
#once program-cooperation-related uncertainties are cleared up

class IHSPTest(BaseTest):
	#initialization
	def initialize(self, parameters):
		#tests
		self.tests['Determine if IHS exists'] = self.exists
		self.tests['Determine if httpd.conf is configured for WebSphere plugins'] = self.configured
		self.tests['Determine if httpd.conf is configured for files, wikis, and libraries'] = self.plugins
		self.tests['Determine if httpd.conf is configured to support SSL'] = self.SSLConf
		
		#error messages, separated by test
		self.resultMessages[101] = "EXIST ERROR: IHS is not installed"
		
		self.resultMessages[201] = "CONFIGURE ERROR: Cannot open \'httpd.conf\'"
		self.resultMessages[202] = "Warning: \'LoadModule\' line exists more than once in \'httpd.conf\'"
		self.resultMessages[203] = "Warning: \'WebSpherePluginConfig\' line exists more than once in \'httpd.conf\'"
		self.resultMessages[204] = "CONFIGURE ERROR: \'LoadModule\' and \'WebSpherePluginConfig\' lines do not exist"
		self.resultMessages[205] = "CONFIGURE ERROR: \'WebSpherePluginConfig\' line does not exist"
		self.resultMessages[206] = "CONFIGURE ERROR: \'LoadModule\' line does not exist"
		self.resultMessages[207] = "CONFIGURE ERROR: An unexpected error occurred"
		
		self.resultMessages[301] = "PLUGIN ERROR: Unable to find \'plugin-cfg.xml\'"
		self.resultMessages[302] = "Warning: Unable to find \'mod_ibm_local_redirect.so\'"
		self.resultMessages[303] = "PLUGIN ERROR: Cannot open \'httpd.conf\'"
		self.resultMessages[304] = "Warning: \'httpd.conf\' is not configured for files, wikis, and libraries"
		self.resultMessages[305] = "PLUGIN ERROR: \'httpd.conf\' is missing one or more lines of code"
		self.resultMessages[310] = "PLUGIN ERROR: Misplaced line \'LoadModule\'"
		self.resultMessages[311] = "PLUGIN ERROR: Misplaced line \'Alias\'"
		self.resultMessages[312] = "PLUGIN ERROR: Misplaced line \'<Directory>\'"
		self.resultMessages[313] = "PLUGIN ERROR: Misplaced line \'Order Deny,Allow\'"
		self.resultMessages[314] = "PLUGIN ERROR: Misplaced line \'Deny from all\'"
		self.resultMessages[315] = "PLUGIN ERROR: Misplaced line \'Allow from env\'"
		self.resultMessages[316] = "PLUGIN ERROR: Misplaced line \'</Directory>\'"
		self.resultMessages[317] = "PLUGIN ERROR: Misplaced line \'<Location>\'"
		self.resultMessages[318] = "PLUGIN ERROR: Misplaced line \'IBMLocalRedirect\'"
		self.resultMessages[319] = "PLUGIN ERROR: Misplaced line \'IBMLocalRedirectKeepHeaders\'"
		self.resultMessages[320] = "PLUGIN ERROR: Misplaced line \'Setenv\'"
		self.resultMessages[321] = "PLUGIN ERROR: Misplaced line \'</Location>\'"
		
		self.resultMessages[401] = "SSL ERROR: Cannot open \'httpd.conf\'"
		self.resultMessages[402] = "Warning: \'httpd.conf\' is not configured for SSL support"
		self.resultMessages[403] = "SSL ERROR: \'httpd.conf\' is missing one or more lines of code"
		self.resultMessages[410] = "SSL ERROR: Duplicated line \'Loadmodule\'"
		self.resultMessages[411] = "SSL ERROR: Duplicated line \'<IfModule>\'"
		self.resultMessages[412] = "SSL ERROR: Duplicated line \'Listen\'"
		self.resultMessages[413] = "SSL ERROR: Duplicated line \'<VirtualHost>\'"
		self.resultMessages[414] = "SSL ERROR: Duplicated line \'ServerName\'"
		self.resultMessages[415] = "SSL ERROR: Duplicated line \'SSLEnable\'"
		self.resultMessages[416] = "SSL ERROR: Duplicated line \'</VirtualHost>\'"
		self.resultMessages[417] = "SSL ERROR: Duplicated line \'</IfModule>\'"
		self.resultMessages[418] = "SSL ERROR: Duplicated line \'SSLDisable\'"
		self.resultMessages[419] = "SSL ERROR: Duplicated line \'Keyfile\'"
		self.resultMessages[420] = "SSL ERROR: Duplicated line \'SSLStashFile\'"
		self.resultMessages[430] = "SSL ERROR: Misplaced line \'Loadmodule\'"
		self.resultMessages[431] = "SSL ERROR: Misplaced line \'<IfModule>\'"
		self.resultMessages[432] = "SSL ERROR: Misplaced line \'Listen\'"
		self.resultMessages[433] = "SSL ERROR: Misplaced line \'<VirtualHost>\'"
		self.resultMessages[434] = "SSL ERROR: Misplaced line \'ServerName\'"
		self.resultMessages[435] = "SSL ERROR: Misplaced line \'SSLEnable\'"
		self.resultMessages[436] = "SSL ERROR: Misplaced line \'</VirtualHost>\'"
		self.resultMessages[437] = "SSL ERROR: Misplaced line \'</IfModule>\'"
		self.resultMessages[438] = "SSL ERROR: Misplaced line \'SSLDisable\'"
		self.resultMessages[439] = "SSL ERROR: Misplaced line \'Keyfile\'"
		self.resultMessages[440] = "SSL ERROR: Misplaced line \'SSLStashFile\'"
		
		self.resultMessages[998] = "Warning: Windows version of this test is not implemented"
		self.resultMessages[999] = "ERROR: OS not supported by this program; Windows or Linux required"
		
		#parameters
		self.pathCon = driver.getParameters('connections.pathCon')
		self.pathIHS = driver.getParameters('IHS.pathIHS')
		self.pathWAS = driver.getParameters('websphere.pathWAS')
		self.server = driver.getParameters('websphere.server')
		self.srvName = driver.getParameters('websphere.srvName')
		self.pathKey = driver.getParameters('IHS.pathKey')
		self.pathStash= driver.getParameters('IHS.pathStash')
	
	#determine if IHS exists
	def exists(self):
		#Windows/Linux platform
		if platform.system() == "Windows" or platform.system() == "Linux":
			#return [success] if success
			if os.path.exists(self.pathIHS): return 0
			#return [IHS is not installed]
			else: return 101
		#return [OS not supported by this program]
		else: return 999

	#determine if httpd.conf is configured for plugins
	def configured(self):
		#Windows/Linux platform
		if platform.system() == "Windows" or platform.system() == "Linux":
			#check if httpd.conf can be opened, return [httpd can't be opened] if not
			driver.outputMessage("Checking if httpd.conf can be opened...")
			pathConf = os.path.join(self.pathIHS,"conf","httpd.conf")
			try: inF = open(pathConf, "r")
			except IOError: return 201
			driver.outputMessage("Success")
			#paths for plugins
			pathPlug1 = os.path.join(self.pathWAS,"Plugins","bin","64bits","mod_was_ap22_http.so")
			pathPlug2 = os.path.join(self.pathWAS,"Plugins","config",self.srvName,"plugin-cfg.xml")
			#the full lines of code
			line1 = "LoadModule was_ap22_module " + pathPlug1
			line2 = "WebSpherePluginConfig " + pathPlug2
			isThere = 0 #0: --, 1: 1-, 2: -2, 3: 12
			#determine whether or not both lines appear in httpd.conf
			driver.outputMessage("Checking if httpd.conf is configured for WebSphere plugins")
			for line in inF:
				if line1 in line:
					driver.outputMessage("Line found: " + line1)
					if isThere != 1 and isThere != 3:
						isThere += 1
					else: return 202 #if line appears more than once, return warning
				if line2 in line:
					driver.outputMessage("Line found: " + line2)
					if isThere != 2 and isThere != 3: isThere += 2
					else: return 203 #if line appears more than once, return warning
			#return finalized outcome based on search
			if isThere == 0: return 204
			elif isThere == 1: return 205
			elif isThere == 2: return 206
			elif isThere == 3:
				driver.outputMessage("httpd.conf is configured for WebSphere.\nTest successful")
				return 0
			else: return 207
		else: return 999
	
	#determine if file, wiki, library plugins are set up properly
	def plugins(self):
		#Windows/Linux platform
		if platform.system() == "Windows" or platform.system() == "Linux":
			#check if plugin-cfg.xml and mod_ibm_local_redirect.so exist, return [doesn't exist] if not
			driver.outputMessage("Checking if plugin-cfg.xml exists...")
			if not os.path.exists(os.path.join
														(self.pathWAS,"Plugins","config",self.srvName,"plugin-cfg.xml")): return 301
			driver.outputMessage("Found plugin-cfg.xml")
			driver.outputMessage("Checking if mod_ibm_local_redirect.so exists...")
			if not os.path.exists(os.path.join(self.pathIHS,"modules","mod_ibm_local_redirect.so")): return 302
			driver.outputMessage("Found mod_ibm_local_redirect.so")
			#check if httpd.conf can be opened, return [httpd can't be opened] if not
			driver.outputMessage("Checking if httpd.conf can be opened...")
			pathConf = os.path.join(self.pathIHS,"conf","httpd.conf")
			try: inF = open(pathConf, 'r')
			except IOError: return 303
			driver.outputMessage("Success")
			path = os.path.join(self.pathCon,"data","shared")
			#generalized code
			code = ["LoadModule ibm_local_redirect_module " + os.path.join("modules","mod_ibm_local_redirect.so"),
							"Alias ",
							"<Directory ",
							"Order Deny,Allow",
							"Deny from all",
							"Allow from env",
							"</Directory>",
							"<Location ",
							"IBMLocalRedirect On",
							("IBMLocalRedirectKeepHeaders X-LConn-Auth,Cache-Control,Content-Type," 
							+ "Content-Disposition,Last-Modified,ETag,Content-Language,Set-Cookie"),
							"SetEnv ",
							"</Location>"]
			#plugin-specific code
			specCode = ["Alias /files_content " + os.path.join(path,"files"),
									"<Directory \"" + os.path.join(path,"files") + "\">",
									"Allow from env=REDIRECT_FILES_CONTENT",
									"<Location /files>",
									"SetEnv FILES_CONTENT true",
									"Alias /wikis_content " + os.path.join(path,"wikis"),
									"<Directory \"" + os.path.join(path,"wikis") + "\">",
									"Allow from env=REDIRECT_WIKIS_CONTENT",
									"<Location /wikis>",
									"SetEnv WIKIS_CONTENT true",
									"Alias /library_content_cache " + os.path.join(path,"ccmcache"),
									"<Directory \"" + os.path.join(path,"ccmcache") + "\">",
									"Allow from env=REDIRECT_LIBRARIES_CONTENT",
									"<Location /dm>",
									"SetEnv LIBRARIES_CONTENT true"]
			isThere = [-1,0,0,0]
			#search for each line of code in httpd.conf, checking to 
			#make sure given lines appear in an order that makes sense
			driver.outputMessage("Checking if code is formatted properly...")
			for line in inF:
				if line[0] != '#':
					for x in range(0,len(code)):
						spec = -1
						if code[x] in line:
							driver.outputMessage("Found line: " + code[x])
							if x == 0:
								if isThere[0] == -1: isThere[0] = 0
								else: return 310+x
							elif x == 1: spec = 0
							elif x == 2: spec = 1
							elif x == 5: spec = 2
							elif x == 7: spec = 3
							elif x == 10: spec = 4
							elif x == 11:
								if isThere[1] == 10:
									driver.outputMessage("\'Files\' is formatted properly")
									isThere[1] = 11
									isThere[0] += 1
								elif isThere[2] == 10:
									driver.outputMessage("\'Wikis\' is formatted properly")
									isThere[2] = 11
									isThere[0] += 1
								elif isThere[3] == 10:
									driver.outputMessage("\'Libraries\' is formatted properly")
									isThere[3] = 11
									isThere[0] += 1
								elif isThere[0] != -1 and isThere[0] != 3: return 310+x
							else:
								if isThere[1] == x-1: isThere[1] = x
								elif isThere[2] == x-1: isThere[2] = x
								elif isThere[3] == x-1: isThere[3] = x
								elif isThere[0] != -1 and isThere[0] != 3: return 310+x
							if spec != -1:
								if specCode[spec] in line:
									if ((isThere[2] <= 1 or isThere[2] == 6 or isThere[2] == 11)
											and (isThere[3] <= 1 or isThere[3] == 6 or isThere[3] == 11)
											and isThere[1] == x-1): isThere[1] = x
									else: return 310+x
								elif specCode[spec+5] in line:
									if ((isThere[1] <= 1 or isThere[1] == 6 or isThere[1] == 11)
											and (isThere[3] <= 1 or isThere[3] == 6 or isThere[3] == 11)
											and isThere[2] == x-1): isThere[2] = x
									else: return 310+x
								elif specCode[spec+10] in line:
									if ((isThere[1] <= 1 or isThere[1] == 6 or isThere[1] == 11)
											and (isThere[2] <= 1 or isThere[2] == 6 or isThere[2] == 11)
											and isThere[3] == x-1): isThere[3] = x
									else: return 310+x
								elif isThere[0] != -1 and isThere[0] != 3: return 310+x
			inF.close()
			#if all lines are there, return [success], else return [failure]
			if isThere[0] == 3:
				driver.outputMessage("Code is formatted for files, wikis, and libraries\nTest successful")
				return 0
			elif isThere[0] == -1: return 304
			else: return 305
		else: return 999
	
	#determine if httpd.conf is configured to support SSL
	def SSLConf(self):
		#Windows/Linux platforms
		if platform.system() == "Windows" or platform.system() == "Linux":
			#make sure httpd.conf can be opened, return [it can't] if failure
			driver.outputMessage("Checking if httpd.conf can be opened...")
			pathConf = os.path.join(self.pathIHS,"conf","httpd.conf")
			try: inF = open(pathConf, 'r')
			except IOError: return 401
			driver.outputMessage("Success")
			#code lines to be searched
			code = ["LoadModule ibm_ssl_module " + os.path.join("modules","mod_ibm_ssl.so"),
							"<IfModule mod_ibm_ssl.c>",
							"Listen 0.0.0.0:443",
							"<VirtualHost *:443>",
							"ServerName " + self.server,
							"SSLEnable",
							"</VirtualHost>",
							"</IfModule>",
							"SSLDisable",
							"KeyFile \"" + self.pathKey + "\"",
							"SSLStashFile \"" + self.pathStash + "\""]
			isThere = -1
			#search for each line of code in httpd.conf, checking to 
			#make sure each SSL-based line appears in the right order
			driver.outputMessage("Checking if code is formatted properly...")
			for line in inF:
				if line[0] != '#':
					for x in range(0,11):
						if code[x] in line:
							driver.outputMessage("Found line: " + code[x])
							if isThere == x-1: isThere = x
							elif isThere != -1 and isThere != 10:
								if isThere >= x: return 410+x
								else: return 430+x
			inF.close()
			#if all lines are there, return [success], else return [failure]
			if isThere == 10:
				driver.outputMessage("Code is formatted for SSL\nTest successful")
				return 0
			elif isThere == -1: return 402
			else: return 403
		else: return 999