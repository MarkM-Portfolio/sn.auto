import driver

class BaseTest:
	
	def __init__(self):
		#This will contain the mapping of the group names to the group object
		self.subGroups = {}
		
		#This will contain the mapping of the test names to their methods
		#A test method is a test that takes no parameters and returns an int result code
		self.tests = {}
		
		#This maps the test names to their result codes(int)
		self.testResults = {}
		
		#This maps the result codes(int) to actual string messages that people can understand
		self.resultMessages = {0:'PASS'}
		
		#The name of the test group
		self.name = ''
		
		#The dependencies for this group
		self.dependencies = []
	
	#This method will be user defined and load all the tests and resultMessages
	def initialize(self, parameters):
		return
	
	#This runs either a test or hands of the request to a subGroup
	def runTest(self, testName):
		#If there is a . char like fu.bar then fu is a subgroup and bar is the name of the test
		#The top level subgroup (fu) should be stripped of and then run() on the subgroup should be called
		
		inSubGroup = testName.find('.')
		if inSubGroup == -1:
			driver.outputMessage('Starting a test in the test group: ' + self.name)
			driver.outputMessage('Starting test: ' + testName)
			self.testResults[testName] = self.tests[testName]()
			driver.outputMessage('Test finished with result: ' + self.getResultMessage(self.testResults[testName]) + '\n')
		else:
			split = testName.split('.', 1) #This split the string into subgroup and test name
			self.subGroups[split[0]].run(split[1]) #0 = subgroup, 1 = test name			
		return
	
	#This runs all the tests
	def runAll(self):
		driver.outputMessage('Starting test group: ' + self.name + '...')
		for testName, test in self.tests.iteritems():
			driver.outputMessage('Starting test: ' + testName)
			self.testResults[testName] = test()
			driver.outputMessage('Test finished with result: ' + self.getResultMessage(self.testResults[testName]) + '\n')
		driver.outputMessage('Finished running test group: ' + self.name + '\n')
		
		for subGroupName, subGroup in self.subGroups.iteritems():
			subGroup.runAll()
		return
	
	def getResultMessage(self, resultCode):
		if resultCode in self.resultMessages:
			return self.resultMessages[resultCode]
		return 'Invalid result code returned'
	
	def hasTest(self, testName):
		return testName in self.tests