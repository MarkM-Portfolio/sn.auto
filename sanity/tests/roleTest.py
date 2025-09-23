from baseTest import BaseTest
import subprocess
import driver
import platform
import re
import os
import stat
import json
class roleTest(BaseTest):        
    def initialize(self, parameters):
        self.tests['Role Mapping Assignments'] = self.roleCheck
        
        #connection test results
        
        #no tests fail
        self.resultMessages[00] = "PASS"
        
        #fails connections admin test
        self.resultMessages[01] = "no connections admin defined"
        
        #fails mapping test
        self.resultMessages[10] = "review mappings for connections admin (specific applications listed above)"
        
        #unable to connect
        self.resultMessages[22] = "unable to connect to wsadmin given the current credentials"
                
    def roleCheck(self):   
        
        directory = os.path.join(os.getcwd(),'tests','role.py')   
        path = os.path.join(driver.getParameters('websphere.pathWAS'),'AppServer','bin','wsadmin.sh')
        user = driver.getParameters('wsadmin.user')
        passW = driver.getParameters('wsadmin.pass')
                   
        output = subprocess.Popen([path,'-lang','jython','-user',user,'-password',passW,'-f',directory], stdout=subprocess.PIPE, stderr=subprocess.STDOUT).communicate()[0].splitlines()
        #ensure that a connection was made
        if (len(output)>1 and output[1]=='CHECKING FOR COGNOS ADMIN:'):
            #get the last element, then remove it from the array
            returnCode = output[-1]
            del output[-1]
            del output[0]
            for item in output:
                format = item.split(': ')
                if len(format)>1:
                    driver.outputMessage(driver.formatedString('    '+format[0],format[1],'','')) 
                else:
                    driver.outputMessage(item)
        else:
            returnCode = '22'
        
        return int(returnCode)
