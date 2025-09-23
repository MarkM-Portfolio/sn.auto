import templateHelper
import os
import subprocess
import threading
import urllib
import sys
sys.path.insert(0, os.path.join(os.getcwd(),'bin'))
import driver

def generate(handler):    
    
    params = {}
    rawParams = handler.path.split('/')[1].split('?', 1)
    rawParams = urllib.unquote_plus(rawParams[1]).split('&')
    tests = []
    for item in rawParams:
    	if item.startswith("tests="):
    		tests.append(item[6:])
    
    for rawParam in rawParams:
        pair = rawParam.split('=', 1)
        if len(pair) == 2:
            params[pair[0]] = pair[1]
    
    time = templateHelper.getFormatedTime()
    resultsFile = 'web/results/' + time + '_results.json'
    logFile = 'web/results/' + time + '_output.log'
    args = ['python', os.path.abspath('bin/driver.py'),'-l', logFile, '-q']
            
    
    #Driver params will be formated like param1:value1,param2:value2
    driverParams = 'driver.saveResultsPath=' + resultsFile
    if 'params' in params:
        for driverParam in params['params'].split(','):
            pair = driverParam.split(':', 1)
            if len(pair) != 2: continue
            driverParams += ',' + pair[0] + '=' + pair[1]
    args.extend(['-p', driverParams])
    
    if len(tests) > 0: args.extend(['-t', ','.join(tests)])

    	 
    print "Running command: "+' '.join(args)
    
    handler.log_message('Starting tool')
    
    try:

    	driver.run(driver.INTERACTIVE_MODE,driver.defaultDependancyChallangeFunc,
               driver.defaultHandleTestModuleNotFoundFunc, driver.defaultHandleBadConfigPathFunc,
               driver.defaultHandleBadJsonFunc, driver.defaultHandleSyntaxErrorFunc,'conf.json', params['params'], tests, args[3], False)
    except:
    #if subprocess.Popen(args).wait() == 1:
        handler.send_response(301)
        handler.send_header('Location' , '/view_results/?error=true')
        handler.end_headers()
        return
        
    handler.send_response(301)
    handler.send_header('Location' , '/view_results/?success=true')
    handler.end_headers()

    return

        
