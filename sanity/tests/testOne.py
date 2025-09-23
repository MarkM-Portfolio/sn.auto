from baseTest import BaseTest
import driver
import random

'''
This is an example test the extends baseTest
'''
class TestOne(BaseTest):		
	def initialize(self, parameters):
		#Mapping the test methods
		self.tests['testOne'] = self.testOne
		self.tests['testTwo'] = self.testOne
		self.tests['testThree'] = self.testOne
		
		#Mapping the test result codes to messages
		self.resultMessages[1] = 'This test did not pass'
		self.resultMessages[2] = 'Warning: This test has a warning'
		self.resultMessages[3] = 'warning: This test has a warning'
		self.resultMessages[4] = 'WARNING: This test has a warning'
	
	def testOne(self):
		#print(driver.getParameters('driver.configPath'))
		driver.outputMessage(driver.getParameters('myparam'))
		return random.randint(0, 4)