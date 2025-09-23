#sysTest - system-based tests for Connections install

from baseTest import BaseTest
import driver
import ctypes,os,platform,stat,sys

class sysTest(BaseTest):
	#initialization
	def initialize(self, parameters):
		#tests
		self.tests['Determine if enough disk space exists to install Connections (114GB recommended)'] = self.diskSpace
		self.tests['Determine if enough memory exists to fulfill the recommended amount (8GB recommended)'] = self.memory
		self.tests['Determine if all required Linux libraries have been installed'] = self.libraries
		self.tests['Determine if permissions to required directories are set properly'] = self.permissions
		self.tests['Determine if uLimit is set to at least the recommended value 8200'] = self.uLim
		
		#error messages, separated by test
		self.resultMessages[101] = "Warning: Free space is less than 114GB;  problems may occur when installing Connections"
		##self.resultMessages[102] = "SPACE ERROR: Free space is less than the minimum required to install Connections (26GB)"
		
		self.resultMessages[200] = "Warning: Total memory is less than 8GB; problems may occur when running Connections"
		
		self.resultMessages[300] = "LIBRARY ERROR: Missing library package \'compat-libstdc++-33.x86_64\'"
		self.resultMessages[301] = "LIBRARY ERROR: Missing library package \'compat-libstdc++-33.i686\'"
		self.resultMessages[302] = "LIBRARY ERROR: Missing library package \'compat-libstdc++-296-2.96\'"
		self.resultMessages[303] = "LIBRARY ERROR: Missing library package \'libcanberra-gtk2.i686\'"
		self.resultMessages[304] = "LIBRARY ERROR: Missing library package \'PackageKit-gtk-module.i686\'"
		self.resultMessages[305] = "LIBRARY ERROR: Missing library package \'gtk2.i686\'"
		self.resultMessages[306] = "LIBRARY ERROR: Missing library package \'libXtst.i686\'"
		self.resultMessages[307] = "LIBRARY ERROR: Missing library \'libpam.so.0\'"
		
		self.resultMessages[401] = "PERMISSION ERROR: Admin denied read permission for \'Installation Manager\'"
		self.resultMessages[402] = "PERMISSION ERROR: Admin denied write permission for \'Installation Manager\'"
		self.resultMessages[403] = "PERMISSION ERROR: Admin denied execute permission for \'Installation Manager\'"
		self.resultMessages[404] = "PERMISSION ERROR: Nonroot user denied read permission for \'Installation Manager\'"
		self.resultMessages[405] = "PERMISSION ERROR: Nonroot user denied write permission for \'Installation Manager\'"
		self.resultMessages[406] = "PERMISSION ERROR: Nonroot user denied execute permission for \'Installation Manager\'"
		self.resultMessages[407] = "PERMISSION ERROR: \'Installation Manager\' does not exist"
		self.resultMessages[411] = "PERMISSION ERROR: Admin denied read permission for \'Shared Data Directory\'"
		self.resultMessages[412] = "PERMISSION ERROR: Admin denied write permission for \'Shared Data Directory\'"
		self.resultMessages[413] = "PERMISSION ERROR: Admin denied execute permission for \'Shared Data Directory\'"
		self.resultMessages[414] = "PERMISSION ERROR: Nonroot user denied read permission for \'Shared Data Directory\'"
		self.resultMessages[415] = "PERMISSION ERROR: Nonroot user denied write permission for \'Shared Data Directory\'"
		self.resultMessages[416] = "PERMISSION ERROR: Nonroot user denied execute permission for \'Shared Data Directory\'"
		self.resultMessages[417] = "Warning: Connections is not installed; run this test again after installing Connections"
		self.resultMessages[421] = "PERMISSION ERROR: Admin denied read permission for \'Connections\'"
		self.resultMessages[422] = "PERMISSION ERROR: Admin denied write permission for \'Connections\'"
		self.resultMessages[423] = "PERMISSION ERROR: Admin denied execute permission for \'Connections\'"
		self.resultMessages[424] = "PERMISSION ERROR: Nonroot user denied read permission for \'Connections\'"
		self.resultMessages[425] = "PERMISSION ERROR: Nonroot user denied write permission for \'Connections\'"
		self.resultMessages[426] = "PERMISSION ERROR: Nonroot user denied execute permission for \'Connections\'"
		self.resultMessages[431] = "PERMISSION ERROR: Admin denied read permission for \'WebSphere AppSever\'"
		self.resultMessages[432] = "PERMISSION ERROR: Admin denied write permission for \'WebSphere AppSever\'"
		self.resultMessages[433] = "PERMISSION ERROR: Admin denied execute permission for \'WebSphere AppSever\'"
		self.resultMessages[434] = "PERMISSION ERROR: Nonroot user denied read permission for \'WebSphere AppSever\'"
		self.resultMessages[435] = "PERMISSION ERROR: Nonroot user denied write permission for \'WebSphere AppSever\'"
		self.resultMessages[436] = "PERMISSION ERROR: Nonroot user denied execute permission for \'WebSphere AppSever\'"
		self.resultMessages[437] = "Warning: WebSphere is not installed; run this test again after installing WebSphere"
		self.resultMessages[441] = "Warning: Admin denied read permission for \'Connections Installer\'"
		self.resultMessages[442] = "Warning: Admin denied write permission for \'Connections Installer\'"
		self.resultMessages[443] = "Warning: Admin denied execute permission for \'Connections Installer\'"
		self.resultMessages[444] = "Warning: Nonroot user denied read permission for \'Connections Installer\'"
		self.resultMessages[445] = "Warning: Nonroot user denied write permission for \'Connections Installer\'"
		self.resultMessages[446] = "Warning: Nonroot user denied execute permission for \'Connections Installer\'"
		self.resultMessages[447] = "Warning: \'Connections Installer\' does not exist"
		self.resultMessages[451] = "PERMISSION ERROR: Access rights denied for nonroot users in \'install.ini\'"
		self.resultMessages[452] = "PERMISSION ERROR: Invalid value for access rights in \'install.ini\'"
		self.resultMessages[457] = "PERMISSION ERROR: \'install.ini\' does not exist"
		
		self.resultMessages[500] = "Warning: uLimit is less than 8200; problems may occur when running Connections"
		
		self.resultMessages[999] = "ERROR: OS not supported by this program; Windows or Linux required"
		
		#parameters
		self.pathCon = driver.getParameters('connections.pathCon')
		self.pathShr = driver.getParameters('connections.pathShare')
		#self.pathDB2 = driver.getParameters('system.pathDB2')
		self.pathWAS = driver.getParameters('websphere.pathWAS')
		self.pathIns = driver.getParameters('connections.pathConInst')
		self.pathMan = driver.getParameters('system.pathInstMan')
		
	#determine if enough disk space exists to install Connections
	def diskSpace(self):
		if platform.system() != "Windows" and platform.system() != "Linux": return 999
		GB = 1024*1024*1024
		req = 114*GB
		#add 2 GB to requirements if DB2 not installed
		##if not os.path.exists(self.pathDB2):
		##	req += 2*GB
		#add 3 GB to requirements if WebSphere not installed
		##if not os.path.exists(self.pathWAS):
		##	req += 3*GB
		#get free space, in bytes
		driver.outputMessage("Getting disk space...")
		if platform.system() == "Windows":
			st = ctypes.c_ulonglong(0)
			ctypes.windll.kernel32.GetDiskFreeSpaceExW(ctypes.c_wchar_p("C:\\"), None, None, ctypes.pointer(st))
			du = st.value
		elif platform.system() == "Linux":
			st = os.statvfs("/")
			du = st.f_bsize * st.f_bavail
		driver.outputMessage ("Total free disk space: " + str(du/GB) + "GB")
		#if enough space, return [success], else return [not enough space]
		if du >= req:
			driver.outputMessage("Total space is greater than recommended amount (114GB)\nTest successful")
			return 0
		else: return 101
		##elif du >= GB*26: return 101
		##else: return 102
		
	#determine if enough memory exists to run Connections
	def memory(self):
		if platform.system() != "Windows" and platform.system() != "Linux": return 999
		#get memory through command line-based code, in GB
		driver.outputMessage ("Getting total memory...")
		if platform.system() == "Windows":
			tm = int(os.popen("systeminfo").readlines()[24].split()[3].translate(None,','))/1024
		elif platform.system() == "Linux":
			tm = int(os.popen("free -m").readlines()[1].split()[1])/1024
		driver.outputMessage ("Total memory: " + str(tm) + "GB")
		#if greater than 8 GB, return [success], else return [not enough memory]
		if tm >= 8:
			driver.outputMessage("Total memory is greater than recommended amount (8GB)\nTest successful")
			return 0
		return 200
	
	#determine if required libraries are installed (Linux only)
	def libraries(self):
		#skip windows platform
		if platform.system() == "Windows":
			driver.outputMessage("Windows platform in use; no libraries required")
			return 0
		#Linux platform
		elif platform.system() == "Linux":
			#list of required libs/packages
			libs = ["compat-libstdc++-33.x86_64",
							"compat-libstdc++-33.i686",
							"compat-libstdc++-296-2.96",
							"libcanberra-gtk2.i686",
							"PackageKit-gtk-module.i686",
							"gtk2.i686",
							"libXtst.i686",
							"libpam.so.0"]
			#for each package...
			driver.outputMessage ("Searching for library packages...")
			for x in range(0,7):
				#if it doesn't exist, return [package ___ doesn't exist]
				process = os.popen("rpm -q %s" % libs[x]).readline().rstrip('\n')
				if process == "": return x+300
				else: driver.outputMessage("Found: " + libs[x])
			#for the lib, search /lib directory for it
			for root, dirs, files, in os.walk("/lib"):
				#if it exists, return [success]
				if libs[7] in files:
					driver.outputMessage("Found: " + libs[7] + "\nAll libraries found\nTest successful")
					return 0
			#return [libpam.so.0 doesn't exist]
			return 307
		#return [OS not supported by this program]
		return 999
		
	#checks permissions of given path
	def PMStatus(self,path):
		#check if admin has permissions to path
		driver.outputMessage("Checking admin permissions for " + path + "...")
		st = os.stat(path)
		if not bool(st.st_mode & stat.S_IRUSR): return 1
		if not bool(st.st_mode & stat.S_IWUSR): return 2
		if not bool(st.st_mode & stat.S_IXUSR): return 3
		driver.outputMessage("Admin permissions enabled")
		#Linux platform should have group permissions as well
		if platform.system() == "Linux":
			driver.outputMessage("Checking group permissions for " + path + "...")
			if not bool(st.st_mode & stat.S_IRGRP): return 4
			if not bool(st.st_mode & stat.S_IWGRP): return 5
			if not bool(st.st_mode & stat.S_IXGRP): return 6
			driver.outputMessage("Group permissions enabled")
		return 0
		
	#checks if permissions to required directories are set properly
	#run again after WebSphere is installed
	#run as both pre and post-install procedure
	def permissions(self):
		if platform.system() != "Windows" and platform.system() != "Linux": return 999
		install = 0
		#list of directories
		path = [self.pathMan,
						self.pathShr,
						self.pathCon,
						self.pathWAS,
						self.pathIns]
		if platform.system() == "Linux":
			#add the install.ini file
			path.append(os.path.join(self.pathIns,"IM","linux","install.ini"))
		#for all directories...
		for x in range(0,6):
			#if path exists, check permissions
			driver.outputMessage("Checking if path exists: " + path[x])
			if os.path.exists(path[x]):
				driver.outputMessage("Path exists")
				if x == 5:
					if platform.system() == "Linux":
						#open the .ini file
						driver.outputMessage("Checking access rights for install.ini...")
						with open(path[x], 'r') as f:
							rights = f.readline()[3].rstrip('\n')
							#if 4th line is "admin", return [access rights denied for nonroot users]
							if rights == "admin": return 451
							#else if line isn't "nonadmin", return [invalid value for access rights]
							elif rights != "nonadmin": return 452
						f.closed
						driver.outputMessage("Access rights are correct")
				else:
					driver.outputMessage("Checking access rights for " + path[x] + "...")
					y = self.PMStatus(path[x])
					#if non-success, return [admin denied ___ permission for ___]
					if y != 0: return 400+(x*10)+y
					driver.outputMessage("Access rights are correct")
			else:
				#if Connections not installed, get warning
				if x == 1 or x == 2: install = 1
				#else if WebSphere not installed, get other warning
				elif x == 3: install = 2
				#else, return [___ does not exist]
				else: return 400+(x*10)+7
		#print [success]
		if install == 0:
			driver.outputMessage("All access rights are correct\nTest successful")
			return 0
		#print warning
		elif install == 1: return 417
		#print other warning
		elif install == 2: return 437
	
	#determine if uLimit is set properly (Linux only)
	def uLim(self):
		#skip Windows platform
		if platform.system() == "Windows":
			driver.outputMessage("Windows platform in use; no libraries required")
			return 0
		#Linux platform
		elif platform.system() == "Linux":
			driver.outputMessage ("Getting uLimit...")
			#get uLimit through command line-based code
			process = os.popen("ulimit -s").readline().rstrip('\n')
			driver.outputMessage ("uLimit: " + process)
			#if greater than/equal to 8200, return [success], else return [uLimit is too low]
			if int(process) >= 8200:
				driver.outputMessage("Total memory is greater than recommended amount (8200)\nTest successful")
				return 0
			else: return 500
		#return [OS not supported by this program]
		return 999