import templateHelper
import os
import json
import types
from Template import Template
import pageBuilder

def generate(handler):
    rawParams = handler.path.split('/')[1].split('?', 1)
    if len(rawParams) != 2:
        handler.send_error(400, 'No result passed')
        return
    
    params = {}
    rawParams = rawParams[1].split('&')
    for rawParam in rawParams:
        pair = rawParam.split('=', 1)
        if len(pair) == 2:
            params[pair[0]] = pair[1]

    if not 'result' in params:
        handler.send_error(400, 'No result passed')
        return
    
    result = params['result']
    if not result[:8].isdigit() or not result[9:].isdigit() or len(result) != 15:
        handler.send_error(400, 'Malformatted result')
        return
    
    resultPath = os.path.join('web/results/', result + '_results.json')
    if not os.path.exists(resultPath):
        handler.send_error(404, 'No result found')
        return
    
    resultFile = open(resultPath, 'r')
    resultData = json.load(resultFile)
    resultFile.close()
    
    templateHelper.sendHTMLHeaders(handler)
    template = Template()
    template.loadTemplate('web/templates/viewResult.pyt')
    
    replacements = {}
    replacements['title'] = 'Sanity Installation Tool - View Result'
    
    year = result[0:4]
    month = result[4:6]
    day = result[6:8]
    hour = result[9:11]
    min = result[11:13]
    sec = result[13:15]
    
    replacements['result'] = month+"/"+day+"/"+year+" "+hour+":"+min+":"+sec
      
    summaryDiv = []
    if len(resultData) == 0:
        resultsDiv.append('No tests were executed.<br />')
        return
    
    (failureCount, passCount) = countFailures(resultData)
    summaryDiv.append(str(failureCount + passCount) + ' test(s) ran | ')
    if not failureCount:
        summaryDiv.append('<span class="textSuccess">All tests passed &#10004;</span>')
    else:
        summaryDiv.append('<span class="textSuccess">'+str(passCount) + ' test(s) passed</span> | ')
        summaryDiv.append('<span class="textFailure">'+str(failureCount) + ' test(s) failed</span>')
    replacements['summaryValues'] = pageBuilder.buildDiv(summaryDiv)
    
    for group, testResults in resultData.iteritems():
        success = True
        for key, value in testResults.iteritems():
            if type(value) is types.DictType:
                for dictkey, dictvalue in value.iteritems():
                    if dictvalue.startswith("Warning:"):
                        success = False
                        resultData[group][key][dictKey] = '<span class="textWarning">'+dictValue+'</span>'
                    elif dictvalue != "PASS":
                        success = False
                        resultData[group][key][dictkey] = '<span class="textFailure">'+dictvalue+'</span>'
                    else:
                        resultData[group][key][dictkey] = '<span class="textSuccess">&#10004;</span>'
            else:
                if value.startswith("Warning:"):
                    success = False
                    resultData[group][key] = '<span class="textWarning">'+value+'</span>'
                elif value != "PASS":
                	success = False
                	resultData[group][key] = '<span class="textFailure">'+value+'</span>'
                else:
                    resultData[group][key] = '<span class="textSuccess">&#10004;</span>'
 
    replacements['resultsList'] = buildResultsList(resultData)
        
    baseUrl = templateHelper.baseUrl(handler)
    replacements['jsonLink'] = pageBuilder.buildLink('/results/' + result + '_results.json', 'Download Json Result File')
    replacements['logLink'] = pageBuilder.buildLink('/results/' + result + '_output.log', 'Download Log File')
    
    handler.wfile.write(template.parseTemplate(replacements))
    return

'''
Takes a dictionary representing group results with tests and children groups embedded and
returns the number of failed and tests pass.
(failCount, passCount)
'''
def countFailures(group):
    failCount = 0
    passCount = 0
    for testName, testResult in group.iteritems():
        if type(testResult) == type({}):
            (failC, passC) = countFailures(testResult)
            failCount += failC
            passCount += passC
        elif testResult.lower() == 'pass':
            passCount += 1
        else:
            failCount += 1
    return (failCount, passCount)

def buildResultsList(group, groupName = ''):
    childrenFound = {}
    passFound = []
    resultsList = []
    #Traverse the results storing the passed tests and children found
    #and then printing out the failed tests
    for testName, testResult in group.iteritems():
        if type(testResult) == type({}):
            childrenFound[testName] = testResult
        elif testResult.lower() == 'pass':
            passFound.append(testName)
        else:
            resultsList.append(testName + ': ' + testResult)
    
    #Print all the passed tests with each other
    for testName in passFound:
        resultsList.append(testName + ': pass')
    #Print all the children groups results
    for childName, child in childrenFound.iteritems():
        resultsList.append(buildResultsList(child, childName))
    
    list = pageBuilder.buildList(resultsList)
    if groupName !='':
        return pageBuilder.buildDiv(['<strong>' + groupName + '</strong>', list])
    return list
