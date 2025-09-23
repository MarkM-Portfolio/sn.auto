from baseTest import BaseTest
from driver import getParameters, outputMessage, loadPropertiesFile
from remote import execRemoteCommand, checkServer
import subprocess, os, re, platform

class db2Test(BaseTest):

	def validation(self, name):
		'''
		Runs db2val to ensure that files are installed correctly
		Codes Prefix: 1
		Return Codes (returned in order of tests executed):
			0: Success (or test skipped)
			2: Validation script not found
			else:
				0: Individual Test Success
				1: Individual Test Failed
		'''

		code = ""
		outputMessage("Validating DB2 installation. This may take several minutes...", "critical")
		
		if self.servers[name].local == True:
			if not os.path.exists(os.path.join(self.servers[name].loc,"bin","db2val")):
				outputMessage("db2 validation script not found", "error")
				return 12
			outputMessage("Beginning validation process")
			results = (subprocess.Popen([os.path.join(self.servers[name].loc,"bin","db2val"), '-o'], stdout=subprocess.PIPE, 
					  stderr=subprocess.STDOUT).communicate()[0].rsplit(os.linesep + os.linesep))
			outputMessage("Validation complete")
		else:
			outputMessage("Beginning validation process on [" + self.servers[name].address + "] ([" + name + "] Server)")
			results = (execRemoteCommand(self.servers[name].address, self.servers[name].username, 
					self.servers[name].password, ["cd " + self.servers[name].loc + "/bin && db2val"]))
			if results == -1:
				return 3
			results = results[0]
			if "No such file or directory" in results or "command not found" in results:
				outputMessage("db2 validation script not found", "error")
				return 12
			outputMessage("Validation complete")
			results = results.rsplit(os.linesep + os.linesep)

		outputMessage("Parsing results...")
		if "successful" in results[1]:
			outputMessage("DB2 installation valid; test successful")
			return 0
		outputMessage("Invalid DB2 installation; consult documentation", "error")
		return 11
		
	def drivers(self, name):
		'''
		Checks that drivers are in directory and have readable permissions
		Code Prefix: 2
		Return Codes (returned in order db2jcc.jar, db2jcc_license_cu.jar):
			0: Success
			else:
				0: Individual driver success
				1: Driver not readable
				2: Driver not found
		'''
		
		code = ''
		
		outputMessage("Checking drivers...")

		if self.servers[name].local == True:
			output = []

			outputMessage("Checking db2jcc.jar permissions...")
			if os.path.exists(os.path.join(self.servers[name].loc,"java","db2jcc.jar")):
				if os.access(os.path.join(self.servers[name].loc,"java","db2jcc.jar"), os.R_OK):
					outputMessage("db2jcc.jar located successfully with correct permissions")
					code += "0"
				else:
					outputMessage("db2jcc.jar not readable", "error")
					code += "1"
			else:
				outputMessage("[" + os.path.join(self.servers[name].loc,"java","db2jcc.jar") + "] not found", "error")
				code += "2"

			outputMessage("Checking db2jcc_license_cu.jar permissions...")
			if os.path.exists(os.path.join(self.servers[name].loc,"java","db2jcc_license_cu.jar")):
				if os.access(os.path.join(self.servers[name].loc,"java","db2jcc_license_cu.jar"), os.R_OK):
					outputMessage("db2jcc_license_cu.jar located successfully with correct permissions")
					code += "0"
				else:
					outputMessage("db2jcc_license_cu.jar not readable", "error")
					code += "1"
			else:
				outputMessage("[" + os.path.join(self.servers[name].loc,"java","db2jcc_license_cu.jar") + "] not found", "error")
				code += "2"
		else:
			outputMessage("Checking driver permissions on [" + self.servers[name].address + "] ([" + name + "] Server)")
			output = execRemoteCommand(self.servers[name].address, self.servers[name].username, self.servers[name].password,
						["test -e "+self.servers[name].loc+"/java/db2jcc.jar",
						 "echo $?",
						 "test -r "+self.servers[name].loc+"/java/db2jcc.jar",
						 "echo $?",
						 "test -e "+self.servers[name].loc+"/java/db2jcc_license_cu.jar",
						 "echo $?",
						 "test -r "+self.servers[name].loc+"/java/db2jcc_license_cu.jar",
						 "echo $?"], True)
			if output == -1:
				return 3
			output = output[1::2]

			outputMessage("Parsing results...")

			#db2jcc.jar check
			if output[0] == "1":
				outputMessage("db2jcc.jar not found", "error")
				code += "2"
			elif output[1] == "1":
				outputMessage("db2jcc.jar is not readable", "error")
				code += "1"
			else:
				outputMessage("db2jcc.jar has correct permissions")
				code += "0"
					
			#db2jcc_license_cu.jar check
			if output[2] == "1":
				outputMessage("db2jcc_license_cu.jar not found", "error")
				code += "2"
			elif output[3] == "1":
				outputMessage("db2jcc_license_cu.jar is not readable", "error")
				code += "1"
			else:
				outputMessage("db2jcc_license_cu.jar has correct permissions")
				code += "0"
		
		if int(code) == 0:
			outputMessage("Drivers test successful")
			return 0
		else:
			outputMessage("Drivers test failed: " + self.resultMessages[int("2" + code)], "error")
			return int("2" + code)

	def wizards(self, name):
		'''
		Checks that Wizards directory exists in correct 
		location and ensures it has the correct permissions
		Code Prefix: 3
		Return Codes:
			0: Success
			1: Directory not executable
			2: Directory not found
		'''

		outputMessage("Checking Wizards directory status...")

		wizardloc = self.servers[name].wizardloc
		outputMessage("Looking for Wizards directory in [" + wizardloc+"]")

		if self.servers[name].local == True:
			if os.path.exists(os.path.join(self.servers[name].loc,wizardloc)):
				if os.access(os.path.join(self.servers[name].loc,wizardloc), os.X_OK):
					outputMessage("Wizards directory located successfully with correct permissions")
					return 0
				else:
					outputMessage("Wizards directory not executable", "error")
					return 31
			else:
				outputMessage("["+os.path.join(self.servers[name].loc,wizardloc) + "] not found", "error")
				return 32
		else:
			outputMessage("Checking Wizards permissions on [" + self.servers[name].address + "] ([" + name + "] Server)")
			result = execRemoteCommand(self.servers[name].address, self.servers[name].username, 
					self.servers[name].password, ["test -e "+wizardloc,"echo $?","test -x "+wizardloc,"echo$?"], True)

			outputMessage("Parsing results")

			if result == -1:
				return 3
			result = result[1::2]
			if result[0] == "1":
				outputMessage("Wizards directory not found", "error")
				return 32
			if result[1] == "1":
				outputMessage("Wizards directory is not executable", "error")
				return 31
			outputMessage("Wizards directory is exectable; test successful")
			return 0

	def unicode(self, name):
		'''
		Checks that db2 is configured for unicode
		Code Prefix: 4
		Return Codes:
			0: Success
			1: Unicode not set
			2: Command not run as db administrator
		'''

		outputMessage("Checking if DB2 is configured for Unicode...")

		if self.servers[name].local == True:
			try:
				outputMessage("Running 'db2set'")
				output = subprocess.Popen(['db2set'], stdout=subprocess.PIPE, 
					stderr=subprocess.STDOUT).communicate()[0]
			except OSError:
				outputMessage("test not run as DB administrator", "error")
				return 42
		else:
			outputMessage("Running 'dbset' on [" + self.servers[name].address + "] ([" + name + "] Server)")
			output = execRemoteCommand(self.servers[name].address, self.servers[name].username,
					self.servers[name].password, ["db2set"])
			if output == -1:
				return 3
			output = output[0]

		outputMessage("Parsing results")
		if "DB2CODEPAGE=1208" in output:
			outputMessage("DB2 configured for unicode; test successful")
			return 0
		elif "command not found" in output:
			outputMessage("test not run as DB administrator", "error")
			return 42
		outputMessage("DB2 not configured for unicode. Test failed.", "error")
		return 41
	
	def cognos(self, name):
		'''
		Checks that, if metrics schema is installed, cognos is as well
		Code Prefix: 5
		Return Codes:
			0: Success
			1: Metrics configured without Cognos
			2: db2 shell not found
		'''

		outputMessage("Checking Cognos schema (N/A if Metrics schema not installed)...")

		if self.servers[name].local == True:
			outputMessage("Running 'db2 list db directory'")
			try:
				output = (subprocess.Popen(["db2", "list db directory"], shell=False,
						stdout=subprocess.PIPE, stderr=subprocess.STDOUT)).communicate()[0]
			except OSError:
				outputMessage("test not run as DB administrator", "error")
				return 52
		else:
			outputMessage("Running 'db2 list db directory' on [" + self.servers[name].address + "] ([" + name + "] Server)")
			output = execRemoteCommand(self.servers[name].address, self.servers[name].username,
										self.servers[name].password, ["db2 list db directory"])
			if output == -1:
				return 3
			output = output[0]
			if "not found" in output:
				outputMessage("test not run as DB administrator", "error")
				return 52
			
		outputMessage("Parsing results")
		if "METRICS" in output:
			outputMessage("Metrics installed...")
			if "COGNOS" in output:
				outputMessage("Cognos installed; test successful")
				return 0
			else:
				outputMessage("Cognos not installed; test failed", "error")
				return 51
		outputMessage("Metrics not installed; ignoring test")
		return 0

	def ded_user(self, name):
		'''
		Checks that dedicated db2 user exists
		Code Prefix: 6
		Return Codes:
			0: Success
			1: No user
			2: Not run as DB administrator
			3: Server not found
		'''

		outputMessage("Checking existence of dedicated db2 user...")

		if self.servers[name].local == True:
			outputMessage("Attempting to connect to database as [" + self.servers[name].dedUser + "]")
			try:
				output = subprocess.Popen(["db2","connect","to",name,"user",self.servers[name].dedUser,"using",self.servers[name].password],
								stdout=subprocess.PIPE, stderr=subprocess.STDOUT).communicate()[0]
			except OSError:
				outputMessage("Test not run as db administrator", "error")
				return 62
		else:
			outputMessage("Attempting to connect to database as [" + self.servers[name].dedUser + "] on [" + self.servers[name].address + "]")
			output = execRemoteCommand(self.servers[name].address, self.servers[name].username, self.servers[name].password,
						["db2 connect to " + name + " user " + self.servers[name].dedUser + " using " + self.servers[name].password], True)
			if output == -1:
				return 3
			outputMessage("Parsing results")
			output = output[0]

		if "not found" in output:
			outputMessage("Server name not found. Check configuration file", "error")
			return 63
		if "Security processing failed" in output:
			outputMessage("Server credentials incorrect; check dedicated username and password", "error")
			return 61
		outputMessage("Dedicated user found; test successful")
		return 0

	def db2comm(self, name):
		'''
		Checks that db2comm is set to TCPIP
		Code Prefix: 7
		Return Codes:
			0: Success
			1: Not TCPIP
			2: Not run as DB administrator
		'''

		outputMessage("Checking db2comm")

		if self.servers[name].local == True:
			try:
				result = subprocess.Popen(["db2set", "db2comm"], stdout=subprocess.PIPE,
										stderr=subprocess.STDOUT).communicate()[0]
			except OSError:
				outputMessage("Test must be run as DB administrator", "error")
				return 72
				
		else:
			outputMessage("Parsing db2comm command on [" + self.servers[name].address + "] ([" + name + "] Server)")
			result = execRemoteCommand(self.servers[name].address, self.servers[name].username, 
									   self.servers[name].password, ["db2set db2comm"])
			if result == -1:
				return 3
			result = result[0]

			if "not found" in result:
				outputMessage("Test must be run as DB administrator", "error")
				return 72
			result = result.split(os.linesep)[0]

		if result == "TCPIP":
			outputMessage("db2comm is set to TCPIP. Test successful")
			return 0
		outputMessage("db2comm not set to TCPIP", "error")
		return 71

	def svcename(self, name):
		'''
		Checks that specified config port and /etc/services port all match
		Code Prefix: 8
		Return Codes:
			0: Success
			1: Ports do not match
			2: Not DB administrator
			3: Error reading dbm cfg
			4: Services file not readable
			5: Error reading services file
			6: SVCENAME not found in cfg
			7: No port assignment in services file
		'''

		if self.servers[name].local == True:
			try:
				outputMessage("Reading dbm cfg file")
				cfg = subprocess.Popen(["db2", "get", "dbm", "cfg"], stdout=subprocess.PIPE, 
										stderr=subprocess.STDOUT).communicate()[0]
				if "not found" in cfg:
					return 82
				outputMessage("Searching for SVCENAME")
				SVCENAME = -1
				for line in cfg.split(os.linesep):
					if line.find("(SVCENAME)") != -1:
						SVCENAME = line.split(os.linesep)[0].split(" ")[-1]
						outputMessage("found SVCENAME in config: [" + SVCENAME + "]")
						break
			except OSError:
				outputMessage("Could not run db2 shell. Make sure you are running tests as db administrator", "error")
				return 83
			if SVCENAME == -1:
				return 86

			try:
				if platform.system() == "Linux":
					path = os.path.join("/","etc","services")
				elif platform.system() == "Windows":
					path = os.path.join("c:","Windows","System32","drivers","etc","services")
				services = open(path, "r").read()
			except IOError as e:
				if e.errno == 13:
					outputMessage("Could not read services file", "error")
					return 84
				outputMessage("Error reading services file: " + e.strerror, "error")
				return 85

			port = ""
			for line in services.split(os.linesep):
				if SVCENAME in line:
					port = line.split("\t")[-1]
					break
		else:
			outputMessage("Searching for SVCENAME in dbm cfg on [" + self.servers[name].address + "] ([" + name + "] Server)")
			SVCENAME = execRemoteCommand(self.servers[name].address, self.servers[name].username, self.servers[name].password,
										["db2 get dbm cfg | grep \(SVCENAME\)"])
			if SVCENAME == -1:
				return 3
			SVCENAME = SVCENAME[0]
			if "not found" in SVCENAME:
				outputMessage("Test is not being run as DB administrator", "error")
				return 82
			SVCENAME = SVCENAME.split(os.linesep)[0].split(" ")[-1]
			if SVCENAME == "":
				outputMessage("SVCENAME not found in config", "error")
				return 86
			outputMessage("Searching for SVCENAME value in /etc/services")
			port = execRemoteCommand(self.servers[name].address, self.servers[name].username, self.servers[name].password,
										["cat /etc/services | grep " + SVCENAME])
			if port == -1:
				return 3
			port = port[0]
			if "No such" in port:
				outputMessage("Couldn't find /etc/services", "error")
				return 84
			port = port.split("\t")[-1]

		if port == "":
			outputMessage("services file does not contain DB2 port assignment", "error")
			return 87
		if self.servers[name].port in port:
			outputMessage("Configuration port matches DB2 port. Test Successful")
			return 0
		outputMessage("Configuration port does not match DB2 port. Test failed", "error")
		return 81

	def serverError(self, name):
		'''Called if error connecting to remote server'''
		return self.servers[name].status

	'''
	The two following methods override the ones found in BaseTest in order to
	account for arguments that must be passed to each test for each server
	'''

	def run(self, testName):
		inSubGroup = testName.find('.')
		if inSubGroup == -1:
			outputMessage('Starting test: ' + testName)
			self.testResults[testName] = self.tests[testName](self, testName.rsplit(" SERVER:",1)[0])
			outputMessage('Test finished with result: ' + self.getResultMessage(self.testResults[testName]) + "\n")
		else:
			split = testName.split('.', 1)
			self.subGroups[split[0]].run(split[1])
		return

	def runAll(self):
		outputMessage('Starting test group: ' + self.name + '...')
		for testName, test in self.tests.iteritems():
			outputMessage('Starting test: ' + testName)
			self.testResults[testName] = test(testName.rsplit(" SERVER:",1)[0])
			outputMessage('Test finished with result: ' + self.getResultMessage(self.testResults[testName]) + "\n")
		outputMessage('Finished running test group: ' + self.name)

		for subGroupName, subGroup in self.subGroups.iteritems():
			subGroup.runAll()
		return
	
	def initialize(self, parameters):

		self.servers = {}

		for s in parameters['db2.servers']:
			server = Server(s)
			if server.status == 0:
				self.tests[server.name + " SERVER: Validate Installed Files"] = self.validation
				self.tests[server.name + " SERVER: Check Drivers Status"] = self.drivers
				self.tests[server.name + " SERVER: Verify Wizards Directory Permissions"] = self.wizards
				self.tests[server.name + " SERVER: Verify DB2 Unicode Configuration"] = self.unicode
				self.tests[server.name + " SERVER: Check Cognos Installation"] = self.cognos
				self.tests[server.name + " SERVER: Confirm Existence of Dedicated User"] = self.ded_user
				self.tests[server.name + " SERVER: Ensure DB2 is Configured for TCPIP"] = self.db2comm
				self.tests[server.name + " SERVER: Verify DB2 is Configured for Correct Port"] = self.svcename
			else:
				self.tests[server.name + " SERVER: Server Connectivity"] = self.serverError

			self.servers[server.name] = server

		for code, message in loadPropertiesFile(os.path.join("resources","db2errors.properties")).iteritems():
			self.resultMessages[int(code)] = message

		for code, message in loadPropertiesFile(os.path.join("resources","servererrors.properties")).iteritems():
			self.resultMessages[int(code)] = message

class Server:
	
	def __init__(self, server):
		self.name = server['name'].upper()
		self.address = server['location']
		self.username = server['username']
		self.password = server['password']

		if self.address != "local" and platform.system() == "Linux":
			self.local = False
			self.status = checkServer(self.address, self.username, self.password)
		else:
			self.local = True
			self.status = 0

		self.port = server['port']
		self.loc = server['path']
		self.dedUser = server['dedicatedUser']
		self.wizardloc = server['wizardsLocation']

