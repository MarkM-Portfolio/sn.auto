#WASTest

from baseTest import BaseTest
import driver
import ctypes,os,platform,stat,sys

#lines marked with dual-pound (##) will be re-implemented later,
#once program-cooperation-related uncertainties are cleared up

class WASTest(BaseTest):
	#initialization
	def initialize(self, parameters):
		#tests
		self.tests['Determine if WebSphere exists'] = self.exists
		self.tests['Determine if wsadmin login is successful'] = self.login
		self.tests['Determine if security is enabled'] = self.security
		self.tests['Determine if application security is enabled'] = self.appSecurity
		
		#error messages, separated by test
		self.resultMessages[101] = "EXIST ERROR: WebSphere is not installed"
		self.resultMessages[201] = "EXIST ERROR: Cannot find \'wsadmin.sh\'"
		self.resultMessages[202] = "LOGIN ERROR: Cannot connect to wsadmin server; make sure login information is correct"
		self.resultMessages[203] = "LOGIN ERROR: Cannot interact with \'JySec.py\'; make sure \'JySec.py\' is located in ~/sanity/tests"
		self.resultMessages[301] = "SECURITY ERROR: Security is not enabled"
		self.resultMessages[401] = "APP ERROR: Application Security is not enabled"
		self.resultMessages[998] = "Warning: Windows version of this test is not implemented"
		self.resultMessages[999] = "ERROR: OS not supported by this program; Windows or Linux required"
		
		#parameters
		self.pathWAS = driver.getParameters('websphere.pathWAS')
		self.user = driver.getParameters('wsadmin.user')
		self.code = driver.getParameters('wsadmin.pass')
		#self.pathAdmin = driver.getParameters('wsadmin.path')
	
	#determine if WebSphere exists
	def exists(self):
		#Windows/Linux platform
		if platform.system() == "Windows" or platform.system() == "Linux":
			#return [success] if success
			if os.path.exists(self.pathWAS): return 0
			#return [websphere is not installed]
			else: return 101
		#return [OS not supported by this program]
		else: return 999
		
	#determine if possible to connect to wsadmin server
	def login(self):
		#Windows/Linux platform
		if platform.system() == "Windows" or platform.system() == "Linux":
			#check if wsadmin.sh exists, return [wsadmin doesn't exist] if not
			driver.outputMessage("Checking WebSphere for wsadmin.sh...")
			pathAdmin = os.path.join(self.pathWAS,"AppServer","bin","wsadmin.sh")
			if not os.path.exists(pathAdmin): return 201
			driver.outputMessage("Found wsadmin.sh")
			#check if current configuration can connect to wsadmin
			driver.outputMessage("Testing wsadmin connection with server and jython script...")
			process = os.popen(pathAdmin + " -user " + self.user 
												+ " -password " + self.code + " -lang jython -f " 
												+ os.path.join("tests","JySec.py" + " 0")).readlines()
			#return [cannot connect] if unable to connect to wsadmin
			if not "WASX7209I" in process[0]: return 202
			driver.outputMessage("Server connection complete")
			#return [cannot reach jython] if unable to reach jython file
			if process[2].rstrip('\n') != "login success": return 203
			driver.outputMessage("Jython script interaction complete\nTest successful")
			#return [success]
			return 0
		else: return 999

	#determine if security is enabled
	def security(self):
		#Windows/Linux platform
		if platform.system() == "Windows" or platform.system() == "Linux":
			#check if wsadmin.sh exists, return [wsadmin doesn't exist] if not
			driver.outputMessage("Checking WebSphere for wsadmin.sh...")
			pathAdmin = os.path.join(self.pathWAS,"AppServer","bin","wsadmin.sh")
			if not os.path.exists(pathAdmin): return 201
			driver.outputMessage("Found wsadmin.sh")
			#get status of security for WebSphere
			driver.outputMessage("Getting WebSphere security status...")
			process = os.popen(pathAdmin + " -user " + self.user 
												+ " -password " + self.code + " -lang jython -f " 
												+ os.path.join("tests","JySec.py" + " 1")).readlines()[2].rstrip('\n')
			#return [success] if enabled, else return [security is not enabled]
			if process == "pass":
				driver.outputMessage("Security enabled\nTest successful")
				return 0
			else: return 301
		else: return 999
	
	#determine if app security is enabled
	def appSecurity(self):
		#Windows/Linux platform
		if platform.system() == "Windows" or platform.system() == "Linux":
			#check if wsadmin.sh exists, return [wsadmin doesn't exist] if not
			driver.outputMessage("Checking WebSphere for wsadmin.sh...")
			pathAdmin = os.path.join(self.pathWAS,"AppServer","bin","wsadmin.sh")
			if not os.path.exists(pathAdmin): return 201
			driver.outputMessage("Found wsadmin.sh")
			#get status of security for all WebSphere apps
			driver.outputMessage("Getting WebSphere application security status...")
			process = os.popen(pathAdmin + " -user " + self.user 
												+ " -password " + self.code + " -lang jython -f " 
												+ os.path.join("tests","JySec.py" + " 3")).readlines()[2].rstrip('\n')
			#return [success] if enabled, else return [security is not enabled]
			if process == "true":
				driver.outputMessage("Application security enabled\nTest successful")
				return 0
			else: return 401
		else: return 999