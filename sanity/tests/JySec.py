import sys, java

lineSep = java.lang.System.getProperty('line.separator')

global AdminApp
global AdminConfig
global AdminControl
global AdminTask

def getLogin():
	print "login success"
	return 0

def getSec():
	cells = AdminConfig.list('Cell').split(lineSep)
	for cell in cells:
		sec = AdminConfig.list('Security', cell)
		if AdminConfig.showAttribute(sec, 'enabled') == "false":
			print("fail")
			return 0
	print("pass")
	return 0
	
def getSSO():
	cells = AdminConfig.list('Cell').split(lineSep)
	for cell in cells:
		SSO = AdminConfig.list('SingleSignon', cell)
		if AdminConfig.showAttribute(SSO, 'domainName') == "":
			print("fail name")
			return 0
		if AdminConfig.showAttribute(SSO, 'enabled') == "false":
			print("fail enabled")
			return 0
		#print AdminConfig.showAttribute(SSO, 'domainName')
		#print AdminConfig.showAttribute(SSO, 'enabled')
		#print AdminConfig.showAttribute(SSO, 'requiresSSL')
	print("pass")
	return 0
		
def getAppSec():
	print AdminTask.isAppSecurityEnabled()
	return 0
	
def getUsers():
	print AdminTask.searchUsers('-cn *')
	return 0
	
def getJVMHeap():
	cells = AdminConfig.list('Cell').split(lineSep)
	for cell in cells:
		server = AdminConfig.getid('/Cell:' + AdminConfig.showAttribute(cell,'name'))
		procDef = AdminConfig.list('ProcessDef', server).split(lineSep)
		for proc in procDef:
			jvm = AdminConfig.list('JavaVirtualMachine', proc).split(lineSep)
			for j in jvm:
				if int(AdminConfig.showAttribute(j, 'maximumHeapSize')) < 2506:
					print "fail"
					return 0
	print "pass"
	return 0
		
if len(sys.argv) == 1:
	if sys.argv[0] == '0': getLogin()
	elif sys.argv[0] == '1': getSec()
	elif sys.argv[0] == '2': getSSO()
	elif sys.argv[0] == '3': getAppSec()
	elif sys.argv[0] == '4': getUsers()
	elif sys.argv[0] == '5': getJVMHeap()
