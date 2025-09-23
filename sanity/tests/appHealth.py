from baseTest import BaseTest
import subprocess
import driver
import platform
import re
import os
import json
class appHealth(BaseTest):        
    def initialize(self, parameters):
        self.tests['Application Health Status'] = self.healthCheck
        
        #connection test results
        
        #no tests fail
        self.resultMessages[00000] = "PASS"
        
        #exactly one test fails
        self.resultMessages[00001] = "issue with web servers"
        self.resultMessages[00010] = "issue with nodes"
        self.resultMessages[00100] = "issue with clusters"
        self.resultMessages[01000] = "issue with application servers"
        self.resultMessages[10000] = "issue with applications (not started)"
        
        #exactly two tests fail
        self.resultMessages[00011] = "issue with web servers and nodes"
        self.resultMessages[00101] = "issue with web servers and clusters"
        self.resultMessages[01001] = "issue with web servers and application servers"
        self.resultMessages[10001] = "issue with web servers and applications (not started)"
        self.resultMessages[00110] = "issue with nodes and clusters"
        self.resultMessages[01010] = "issue with nodes and application servers"
        self.resultMessages[10010] = "issue with nodes and applications (not started)"
        self.resultMessages[01100] = "issue with clusters and application servers"
        self.resultMessages[10100] = "issue with clusters and applications (not started)"
        self.resultMessages[11000] = "issue with application servers and applications (not started)"
        
        #exactly three tests fail
        self.resultMessages[11100] = "issue with clusters, application servers, and applications (not started)"
        self.resultMessages[11010] = "issue with nodes, application servers, and applications (not started)"
        self.resultMessages[10110] = "issue with nodes, clusters, and applications (not started)"
        self.resultMessages[01110] = "issue with nodes, clusters, and application servers"
        self.resultMessages[11001] = "issue with web servers, application servers, and applications (not started)"
        self.resultMessages[10101] = "issue with web servers, clusters, and applications (not started)"
        self.resultMessages[01101] = "issue with web servers, clusters, and application servers"
        self.resultMessages[10011] = "issue with web servers, nodes, and applications (not started)"
        self.resultMessages[01011] = "issue with web servers, nodes, and application servers"
        self.resultMessages[00111] = "issue with web servers, nodes, and clusters"
        
        #exactly four tests fail
        self.resultMessages[01111] = "issue with web servers, nodes, clusters, and application servers"
        self.resultMessages[10111] = "issue with web servers, nodes, clusters, and applications (not started)"
        self.resultMessages[11011] = "issue with web servers, nodes, application servers, and applications (not started)"
        self.resultMessages[11101] = "issue with web servers, clusters, application servers, and applications (not started)"
        self.resultMessages[11110] = "issue with nodes, clusters, application servers, and applications (not started)"
        
        #all tests fail
        self.resultMessages[11111] = "issue with web servers, nodes, clusters, application servers, and applications (not started)"
        
        #unable to connect
        self.resultMessages[22222] = "unable to connect to wsadmin given the current credentials"
        
    def healthCheck(self):   
        
        directory = os.path.join(os.getcwd(),'tests','ping.py')    
        path = os.path.join(driver.getParameters('websphere.pathWAS'),'AppServer','bin','wsadmin.sh')
        user = driver.getParameters('wsadmin.user')
        passW = driver.getParameters('wsadmin.pass')
        
        output = subprocess.Popen([path,'-lang','jython','-user',user,'-password',passW,'-f',directory], stdout=subprocess.PIPE, stderr=subprocess.STDOUT).communicate()[0].splitlines()
        
        #ensure that a connection was made
        if (len(output)>1 and output[1]=='PING WEB SERVERS:'):
            #get the last element, then remove it form the array
            returnCode = output[-1]
            del output[-1]
            del output[0]
            for item in output:
                format = item.split(': ')
                if len(format)>1:
                    driver.outputMessage(driver.formatedString('    '+format[0],'',format[1],'')) 
                else:
                    driver.outputMessage(item)
        else:
            returnCode = '22222'          
        
        return int(returnCode)