import os
import sys

class DJM_API:

	foo = None
	
	def __init__(self):
		print "In constructor."
		self.foo = "FOO"
	
	def __del__(self):
		print "In destructor."
	
	def cleanUp(self):
		print "in cleanUp."
		if self.foo != None:
			self.foo = None
	
	def myFunc(self, index):
		print "in myFunc, index: %d" %(index)
		self.cleanUp()

myObj = DJM_API()
print myObj.foo

for i in range(5):
	print "in loop"
	myObj.myFunc(i)
	print myObj.foo

