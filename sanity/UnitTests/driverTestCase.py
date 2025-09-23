import unittest
import imp
import sys
import os


modPath = os.path.abspath('..')
sys.path.append(modPath)
import driver

class DriverTestCase(unittest.TestCase):
    
    badConfPathCalled = False
    malformedJsonCalled = False
    
    testModuleNotFound = False
    testCountainsSyntaxError = False
        
    def testLoadExistingConfigFile(self):
        data = driver.loadConfFile('Res/conf.json', self.handleBadConfigPath, self.handleBadJson)
        unittest.TestCase.assertTrue(self, data, 'Could not load an existing config file')
        
    def testLoadNonExistingConfigFile(self):
        data = driver.loadConfFile('Res/fooBarConf.json', self.handleBadConfigPath, self.handleBadJson)
        unittest.TestCase.assertFalse(self, data, 'Error not raised when file could be found')
        unittest.TestCase.assertTrue(self, self.badConfPathCalled, 'Bad Config Path was not processed')
        
    def testLoadMalfFormedConfigFile(self):
        self.malformedJsonCalled = False
        data = driver.loadConfFile('Res/malformedConf.json', self.handleBadConfigPath, self.handleBadJson)
        unittest.TestCase.assertFalse(self, data, 'Error not raised when the file was malformed')
        unittest.TestCase.assertTrue(self, self.malformedJsonCalled, 'Malformated JSON file was not processed')
    
    def testLoadTests(self):
        data = driver.loadConfFile('Res/conf.json', self.handleBadConfigPath, self.handleBadJson)
        tests = driver.loadTests(data, self.handleTestModuleNotFoundFunc, self.handleSyntaxError)
        unittest.TestCase.assertTrue(self, tests, 'Tests were not loaded')
        
    def testLoadTestsWithSyntaxEror(self):
        data = driver.loadConfFile('Res/loadTestWithSyntaxErrorConf.json', self.handleBadConfigPath, self.handleBadJson)
        tests = driver.loadTests(data, self.handleTestModuleNotFoundFunc, self.handleSyntaxError)
        unittest.TestCase.assertFalse(self, tests, 'Tests didn\'t fail when loading trying to load a test with a syntax error')
        unittest.TestCase.assertTrue(self, self.testCountainsSyntaxError, 'Syntax Error in file was not processed')
        
    def testLoadTestsWithWrongTestPath(self):
        self.testModuleNotFound = False
        unittest.TestCase.assertTrue(self, False, 'Failed')
        unittest.TestCase.assertTrue(self, self.testModuleNotFound, 'File not found error was not processed')
    
    def testLoadTestsWithNoTestClassFoundError(self):
        self.testModuleNotFound = False
        unittest.TestCase.assertTrue(self, False, 'Failed')
        unittest.TestCase.assertTrue(self, self.testModuleNotFound, 'Class not found in the file was not processed')
        
    '''
    Helper functions
    '''
    def handleBadConfigPath(self, path):
        self.badConfPathCalled = True
    
    def handleBadJson(self, path, details):
        self.malformedJsonCalled = True
    
    def handleTestModuleNotFoundFunc(self, name, relPath):
        print('Mod')
        self.testModuleNotFound = True
        
    def handleSyntaxError(self, name, path, details):
        self.testCountainsSyntaxError = True
    
def main():
    unittest.main()

if __name__ == '__main__':
    main()