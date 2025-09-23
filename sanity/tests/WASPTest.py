#WASPTest

from baseTest import BaseTest
import driver
import ctypes,os,platform,stat,sys

#lines marked with dual-pound (##) may be restored in a later version

class WASPTest(BaseTest):
	#initialization
	def initialize(self, parameters):
		#tests
		##self.tests['exist'] = self.exists
		self.tests['Determine if LotusConnections-config.xml has portless URIs'] = self.configured
		self.tests['Determine if WebSphere is configured to allow SSO'] = self.singleSignon
		self.tests['Determine if WAS is populated'] = self.populated
		self.tests['Determine if plugin-cfg.xml exists'] = self.WASPlugins
		self.tests['Determine if JVM Heaps have enough space allocated'] = self.JVMHeap
		
		#error messages, separated by test
		##self.resultMessages[101] = "EXIST ERROR: WebSphere is not installed"
		self.resultMessages[102] = "EXIST ERROR: Cannot find \'wsadmin.sh\'"
		self.resultMessages[201] = "CONFIGURE ERROR: Cannot open \'LotusConnections-config.xml\'"
		self.resultMessages[202] = "CONFIGURE ERROR: URIs should not have ports"
		self.resultMessages[301] = "Warning: SSO does not have a name or does not exist"
		self.resultMessages[302] = "Warning: SSO is not enabled"
		self.resultMessages[401] = "POPULATION ERROR: User list is empty or does not exist"
		self.resultMessages[501] = "WAS PLUGIN ERROR: Unable to find \'plugin-cfg.xml\'"
		self.resultMessages[601] = "Warning: One or more JVMs given less than recommended heap size (2506MB)"
		self.resultMessages[998] = "Warning: Windows version of this test is not implemented"
		self.resultMessages[999] = "ERROR: OS not supported by this program; Windows or Linux required"
		
		#parameters
		self.pathWAS = driver.getParameters('websphere.pathWAS')
		self.user = driver.getParameters('wsadmin.user')
		self.code = driver.getParameters('wsadmin.pass')
		self.server = driver.getParameters('websphere.server')
		self.srvName = driver.getParameters('websphere.srvName')
		self.profile = driver.getParameters('websphere.profile')
		self.cell = driver.getParameters('websphere.cell')
		#self.pathAdmin = driver.getParameters('wsadmin.path')
		#self.pathLotus = driver.getParameters('websphere.pathLotus')
	
	#determine if WebSphere exists
	##def exists(self):
		#Windows/Linux platform
		##if platform.system() == "Windows" or platform.system() == "Linux":
			#return [success] if success
			##if os.path.exists(self.pathWAS): return 0
			#return [websphere is not installed]
			##else: return 101
		#return [OS not supported by this program]
		##else: return 999
	
	#determine if LotusConnections-config.xml has port-less URIs
	def configured(self):
		#Windows/Linux platform
		if platform.system() == "Windows" or platform.system() == "Linux":
			#check if LotusConnections-config.xml can be opened, return [it can't] if not
			driver.outputMessage("Checking if LotusConnections-config.xml can be opened...")
			pathLotus = os.path.join(self.pathWAS,"AppServer","profiles",self.profile,"config",
															"cells",self.cell,"LotusConnections-config","LotusConnections-config.xml")
			try: inF = open(pathLotus, "r")
			except IOError: return 201
			driver.outputMessage("Success")
			search = self.server + ":"
			#if URIs followed by ':', assume they have a port associated, return [they still have ports]
			driver.outputMessage("Checking if ports appear for given server...")
			for line in inF:
				if search in line: return 202
			#if no ports found, return [success]
			driver.outputMessage("No ports found\nTest successful")
			return 0
		else: return 999
	
	#determine if SSO is configured
	def singleSignon(self):
		#Windows/Linux platform
		if platform.system() == "Windows" or platform.system() == "Linux":
			#check if wsadmin.sh exists, return [wsadmin doesn't exist] if not
			driver.outputMessage("Checking WebSphere for wsadmin.sh...")
			pathAdmin = os.path.join(self.pathWAS,"AppServer","bin","wsadmin.sh")
			if not os.path.exists(pathAdmin): return 102
			driver.outputMessage("Found wsadmin.sh")
			#get status of SSO for WebSphere
			driver.outputMessage("Getting WebSphere SSO status...")
			process = os.popen(pathAdmin + " -user " + self.user + " -password " + self.code + " -lang jython -f " 
												+ os.path.join("tests","JySec.py" + " 2")).readlines()[2].rstrip('\n')
			#if anything fails, return error message, else return [success]
			if process == "fail name": return 301
			elif process == "fail enabled": return 302
			else:
				driver.outputMessage("WebSphere is configured for SSO\nTest successful")
				return 0
		else: return 999

	#determine if WAS populated
	def populated(self):
		#Windows/Linux platform
		if platform.system() == "Windows" or platform.system() == "Linux":
			#check if wsadmin.sh exists, return [wsadmin doesn't exist] if not
			driver.outputMessage("Checking WebSphere for wsadmin.sh...")
			pathAdmin = os.path.join(self.pathWAS,"AppServer","bin","wsadmin.sh")
			if not os.path.exists(pathAdmin): return 102
			driver.outputMessage("Found wsadmin.sh")
			#get status of user population
			driver.outputMessage("Getting WebSphere server population status...")
			process = os.popen(pathAdmin + " -user " + self.user + " -password " + self.code + " -lang jython -f " 
												+ os.path.join("tests","JySec.py" + " 4")).readlines()
			#if users exist, return [success], else return [failure]
			if len(process)-2 > 0:
				driver.outputMessage("Server is populated\nTest successful")
				return 0
			else: return 401
		else: return 999
	
	#determine if plugin-cfg.xml exists
	def WASPlugins(self):
		#Windows/Linux platform
		if platform.system() == "Windows" or platform.system() == "Linux":
			#if plugin-cfg.xml exists, return [success], else return [failure]
			path = os.path.join(self.pathWAS,"Plugins","config",self.srvName,"plugin-cfg.xml")
			if os.path.exists(path): return 0
			else: return 501
		else: return 999
		
	#determine if JVM Heaps have enough space allocated
	def JVMHeap(self):
		#Windows/Linux platform
		if platform.system() == "Windows" or platform.system() == "Linux":
			#check if wsadmin.sh exists, return [wsadmin doesn't exist] if not
			driver.outputMessage("Checking WebSphere for wsadmin.sh...")
			pathAdmin = os.path.join(self.pathWAS,"AppServer","bin","wsadmin.sh")
			if not os.path.exists(pathAdmin): return 102
			driver.outputMessage("Found wsadmin.sh")
			#get information about JVM heaps and their sizes
			driver.outputMessage("Getting JVM heap status...")
			process = os.popen(pathAdmin + " -user " + self.user + " -password " + self.code + " -lang jython -f " 
												+ os.path.join("tests","JySec.py" + " 5")).readlines()[2].rstrip('\n')
			#return [success] if success, return warning if not
			if process == "pass":
				driver.outputMessage("JVM heap space is greater than recommended amount (2506MB)\nTest successful")
				return 0
			else: return 601
		else: return 999
		
	#config files correct on DM
		#(has the updated urls config file been updated in the DM and this match the files in app servers)