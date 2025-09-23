import json
from optparse import OptionParser
import os
import sys
sys.path.insert(0, os.path.join(os.getcwd(),'bin'))
import driver

def main():
	usage = 'usage: %prog [options]'
	parser = OptionParser(usage)
	
	parser.add_option('-c', '--config', dest='config', default='', help=
'''The location of the configuration file to load. The default value is conf.json.''')
	
	parser.add_option('-p', '--param', dest='parameters', default='', help=
'''A list of user defined parameters. They're are formated like key=value 
for a single parameter or you can add multiple parameters by adding a comma
in between like key1=value1,key2=value2.''')
	
	parser.add_option('-t', '--tests', dest='tests', default='', help=
'''A list of tests and test groups to run. The formating is as followed.
group.subGroup,groupA.testName. The last value can either be a group or
test name. If it is a group then all the tests in the group are ran,
including the sub groups of the groups''')
	
	parser.add_option('-l', '--log', dest='logPath', default='output.log', help=
'''Sets the location that the log file will be saved to. If this file already exists
then the file will be appended with the new log information. The default value is output.log.''')
	
	(options, args) = parser.parse_args()
	
	print("Welcome to the Sanity installation validation tool.")
	
	userParameters = {}
	for kvp in options.parameters.split(','):#kvp = key value pair
		if '=' in kvp:
			split = kvp.split('=')#key=value -> [key, value]
			userParameters[split[0]] = split[1]
	
	tests = []
	if options.tests != '':
		for test in options.tests.split(','):
			tests.append(test)
	
	confLoc = ''
	if options.config == '':
		confLoc = raw_input("Configuration File Location (default is conf.json):")
		if confLoc == '':
			confLoc = 'conf.json'
	else:
		confLoc = options.config
	
	rawResults = driver.run(driver.INTERACTIVE_MODE, driver.defaultDependancyChallangeFunc,
							driver.defaultHandleTestModuleNotFoundFunc, driver.defaultHandleBadConfigPathFunc,
							driver.defaultHandleBadJsonFunc, driver.defaultHandleSyntaxErrorFunc,
							confLoc, userParameters, tests, options.logPath)
	if not rawResults:
		print('There was an error while running the driver. Please fix this problem and then try again.')
		return
	results = json.loads(rawResults)
	
	print('Results:')
	if len(results) == 0:
		print('No tests were executed.')
		return
	
	(failureCount, warningCount, passCount) = countFailures(results)
	print(str(failureCount + warningCount + passCount) + ' test(s) ran.')
	if not failureCount and not warningCount:
		print('All tests passed.')
	else:
		print(str(passCount) + ' test(s) passed.')
		print(str(warningCount) + ' test(s) have warnings.')
		print(str(failureCount) + ' test(s) failed.')
	
	printResults(results)
	return

'''
Takes a dictionary representing group results with tests and children groups embedded and
returns the number of failed and tests pass.
(failCount, passCount)
'''
def countFailures(group):
	failCount = 0
	passCount = 0
	warningCount = 0
	
	for testName, testResult in group.items():
		if type(testResult) == type({}):
			(failC, warnC, passC) = countFailures(testResult)
			failCount += failC
			passCount += passC
			warningCount += warnC
		elif testResult.lower() == 'pass':
			passCount += 1
		elif testResult.startswith(('WARNING', 'Warning', 'warning')):
			warningCount += 1
		else:
			failCount += 1
	return (failCount, warningCount, passCount)

def printResults(group, groupName = ''):
	childrenFound = {}
	passFound = []
	warningFound = []
	
	print(groupName)
	#Traverse the results storing the passed tests and children found
	#and then printing out the failed tests
	for testName, testResult in group.iteritems():
		if type(testResult) == type({}):
			childrenFound[testName] = testResult
		elif testResult.lower() == 'pass':
			passFound.append(testName)
		elif testResult.startswith(('WARNING', 'Warning', 'warning')):
			warningFound.append((testName, testResult))
		else:
			print('\t' + testName + ': ' + testResult.replace("\\n", "\n") + '\n')
	
	#Print all the warning tests with each other
	for testName, testResult in warningFound:
		print('\t' + testName + ': ' + testResult + '\n')
	
	#Print all the passed tests with each other
	for testName in passFound:
		print('\t' + testName + ': pass')
		
	#Print all the children groups results
	for childName, child in childrenFound.iteritems():
		printResults(child, childName)

	print '\n'
	return
	
main()