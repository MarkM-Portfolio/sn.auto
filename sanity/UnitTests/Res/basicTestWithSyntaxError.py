from baseTest import BaseTest
import driver
import random

'''
This is an example test the extends baseTest
'''
class BasicTest(BaseTest):		
	def initialize(self, parameters):
		#Mapping the test methods
		self.tests['testOne'] = self.testOne
		self.tests['testTwo'] = self.testOne
		self.tests['testThree'] = self.testOne
		self.test[youcantdothis] = really.notthis.either
		
		#Mapping the test result codes to messages
		self.resultMessages[1] = 'This test did not pass'
	
	def testOne(self):
		#print(driver.getParameters('testOne.param1'));
		return random.randint(0, 1)