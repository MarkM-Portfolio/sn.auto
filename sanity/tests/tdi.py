from baseTest import BaseTest
from remote import execRemoteCommand, checkServer
from driver import getParameters, outputMessage, loadPropertiesFile
import subprocess, os, re, platform, json

class tdiTest(BaseTest):

	def validation(self):
		'''
		Ensures all files are installed
		Code Prefix: 1
		Return Codes:
			0: Success
			1: Validation Failed
			2: Validation Script Not Found
		'''
		outputMessage("Validating TDI installation...")

		tdijson = open(os.path.join("resources","tdiFiles.json"), "r")
		files = json.load(tdijson)
		tdijson.close()

		if self.local == True:
			outputMessage("Loading TDI Registry")
			try:
				registry = open(os.path.join(self.loc,".registry")).read()
			except IOError as e:
				if e.errno == 13:
					outputMessage("Could not read TDI registry", "error")
					return 13
				else:
					outputMessage("Could not open TDI registry", "error")
					return 12

			outputMessage("Checking files...")
			serversuccess = True
			cesuccess = True
			amcsuccess = True
			for group, paths in files.iteritems():
				if "<"+group+">" in registry:
					for p in paths.rsplit(';'):
						if not os.path.exists(os.path.join(self.loc,p)):
							if group == "SERVER":
								outputMessage("TDI Server missing file " + p, "error")
								serversuccess = False
							elif group == "CE":
								outputMessage("TDI CE missing file " + p, "warning")
								cesuccess= False
							elif group == "AMC":
								outputMessage("TDI AMC missing file " + p, "warning")
								amcsuccess = False
			outputMessage("Finished checking files")
		else:
			commands = []
			outputMessage("Retrieving registry file on [" + self.address + "]")
			registry = execRemoteCommand(self.address, self.username, self.password,
										 ["cat " + os.path.join(self.loc,".registry")])
			if registry == -1:
				outputMessage("Error connecting to server","error")
				return 3
			registry = registry[0]
			if "Permission denied" in registry:
				outputMessage("Could not read TDI registry", "error")
				return 13
			elif "No such" in registry:
				outputMessage("Could not open TDI registry", "error")
				return 12

			filenames = []
			outputMessage("Parsing registry file")
			for group in files:
				if "<"+group+">" in registry:
					for path in files[group].rsplit(';'):
						commands.append("ls -ld " + os.path.join(self.loc,path))
						filenames.append((group, os.path.join(self.loc,path)))
			outputMessage("Searching for files on [" + self.address + "]")
			result = execRemoteCommand(self.address, self.username, self.password, commands)
			if result == -1:
				outputMessage("Error connecting to server","error")
				return 3
			serversuccess = True
			cesuccess = True
			amcsuccess = True
			outputMessage("Parsing results...")
			for r, f in zip(result, filenames):
				if "No such" in r:
					if f[0] == "SERVER":
						outputMessage("TDI Server missing file " + f[1], "error")
						serversuccess = False
					elif f[0] == "CE":
						outputMessage("TDI CE missing file " + f[1], "warning")
						cesuccess = False
					elif f[0] == "AMC":
						outputMessage("TDI AMC missing file " + f[1], "warning")
						amcsuccess = False
			outputMessage("Finished checking files")

		if serversuccess == True and cesuccess == True and amcsuccess == True:
			outputMessage("Validation test successful")
			return 0

		if serversuccess == False: self.resultMessages[11] += " ERROR: Server files missing. See log for details,"
		if cesuccess     == False: self.resultMessages[11] += " WARNING: CE files missing. See log for details,"
		if amcsuccess    == False: self.resultMessages[11] += " WARNING: AMC files missing. See log for details,"
	
		if serversuccess == False: outputMessage("Validation test failed", "error")
		else: outputMessage("Validation test finished with warnings", "warning")
		return 11

	def ibmdisrv(self):
		'''
		Ensures ibmdisrv is configured properly
		Code Prefix: 2
		Return Codes:
			0: Success
			1: Invocations not present in file
			2: Could not locate file
			3: Could not read file due to permissions
		'''
		
		ibmdisrv = "ibmdisrv.bat" if platform.system() == "Windows" else "ibmdisrv"

		outputMessage("Checking ibmdisrv for Java commands...")
		if self.local == True:
			if os.path.exists(os.path.join(self.loc,ibmdisrv)):
				try:
					outputMessage("Reading file...")
					text = open(os.path.join(self.loc,ibmdisrv), "r").read()
				except IOError:
					outputMessage("ibmdisrv does not have readable permissions", "error")
					return 23
			else:
				outputMessage("ibmdisrv file not found", "error")
				return 22
			outputMessage("Opened ibmdisrv")
		else:
			outputMessage("Opening ibmdisrv on [" + self.address + "]")
			text = execRemoteCommand(self.address, self.username, self.password,
									["cat " + os.path.join(self.loc,"ibmdisrv")])
			if text == -1:
				outputMessage("Error connecting to server","error")
				return 3
			text = text[0]
			if "No such" in text:
				outputMessage("ibmdisrv file not found")
				return 22
			if "Permission denied" in text:
				outputMessage("ibmdisrv does not have readable permissions", "error")
				return 23
		
		outputMessage("File retrieved. Searching for commands...")
		
		commands = {"-Xms": 1, "-Xmx": 1, "-Xnojit": 1}
		for line in text.split(os.linesep):
			for c in commands:
				if c in line and "=" not in line:
					commands[c] = 0
		success = True
		for c, val in commands.iteritems():
			if val == 1:
				success = False
				outputMessage(c + " flag not found in ibmdisrv", "warning")
				self.resultMessages[21] += " " + c + ","
		return 0 if success == True else 21
		
	def hostfile(self):
		'''
		Ensures hosts file contains localhost entry
		Code Prefix: 3
		Return Codes:
			0: Success
			1: No localhost entry
			2: Error opening file
			3: File does not give reading permissions
		'''	

		outputMessage("Checking hosts file for localhost entry...")

		if platform.system() == "Windows":
			outputMessage("Test not required for Windows systems")
			return 0

		if self.local == True:
			path = os.path.join("/","etc","hosts")
			try:
				outputMessage("Opening hosts file at " + path)
				text = open(path, "r").read()
			except IOError as e:
				outputMessage("Could not open hosts file: " + e.strerror, "error")
				return 33 if e.errno == 13 else 32
		else:
			outputMessage("Retrieving hosts file from [" + self.address + "]")
			text = execRemoteCommand(self.address, self.username, self.password, ["cat /etc/hosts"])
			if text == -1:
				outputMessage("Error connecting to server","error")
				return 3

			text = text[0]
			if "No such" in text:
				outputMessage("Hosts file not found", "error")
				return 32
			if "Permission denied" in text:
				outputMessage("Hosts file not readable", "error")
				return 33

		outputMessage("File retrieved; searching for localhost entry")
		if re.search("[0-9]+(?:\.[0-9]+){3}(\tlocalhost(.localdomain)?)+", text):
			outputMessage("Entry found; test successful")
			return 0
		outputMessage("Localhost entry not found. Test failed", "error")
		return 31

	def version(self):
		'''
		Checks that TDI is version 7 with fixpack 6
		Code Prefix: 4
		Return Codes:
			0: Success
			1: Error running applyUpdates.sh
			2: applyUpdates.sh not executable
			3: Not updated to version 7 or later
			4: Not all modules updated to FP0006
		'''
		
		if self.local == True:
			try:
				outputMessage("Running applyUpdates.sh -queryreg")
				registry = subprocess.Popen([os.path.join(self.loc,"bin","applyUpdates.sh"),"-queryreg"],
											stdout=subprocess.PIPE, stderr=subprocess.STDOUT).communicate()[0]
			except OSError as e:	
				if e.errno == 13:
					outputMessage("applyUpdates.sh is not executable", "error")
					return 42
				outputMessage("Couldn't run applyUpdates.sh", "error")
				return 41
		else:
			outputMessage("Running applyUpdate.sh -queryreg on [" + self.address + "]")
			registry = execRemoteCommand(self.address, self.username, self.password,
										 [os.path.join(self.loc,"bin","applyUpdates.sh")+" -queryreg"])
			if registry == -1:
				outputMessage("Error connecting to server","error")
				return 3
			registry = registry[0]
			if "No such" in registry and len(registry.split(os.linesep)) == 1:
				outputMessage("Couldn't run applyUpdates.sh", "error")
				return 41
			if "Permission denied" in registry:
				outputMessage("applyUpdates.sh is not executable", "error")
				return 42
			
		level = re.findall("Level: \\d(?:\\.(?:\\d+))*", registry)[0]
		if int(level.split(" ")[-1][0]) < 7:
			outputMessage("TDI Version too early; update to version 7", "error")
			return 43
		if "FP0006" not in re.findall("Fixes Applied\n(?:\\=\\-){6}\\=\\n.*\\n", registry)[0].split(os.linesep)[2]:
			outputMessage("TDI not updated to fixpack 6", "error")
			return 44
		return 0

	def serverError(self):
		'''Called if error connecting to remote server''' 
		return self.status

	def initialize(self, parameters):

		self.name = "TDI"
		self.address = getParameters("tdi.location")
		self.username = getParameters("tdi.username")
		self.password = getParameters("tdi.password")

		if getParameters("tdi.location") != "local" and platform.system() == "Linux":
			self.local = False
			self.status = checkServer(self.address, self.username, self.password)
		else:
			self.local = True
			self.status = 0

		self.loc = getParameters("tdi.path")

		if self.status == 0:
			self.tests['Validate Installed Files'] = self.validation
			self.tests['Check for JVM flags in ibmdisrv file'] = self.ibmdisrv
			self.tests['Ensure Host File Has Localhost Entry'] = self.hostfile
			self.tests['Check TDI Version is Up to Date'] = self.version
		else:
			self.tests['Server Connectivity'] = self.serverError

		for code, message in loadPropertiesFile(os.path.join("resources","tdierrors.properties")).iteritems():
			self.resultMessages[int(code)] = message

		for code, message in loadPropertiesFile(os.path.join("resources","servererrors.properties")).iteritems():
			self.resultMessages[int(code)] = message
	
