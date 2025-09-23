import templateHelper
import driver
from Template import Template
import pageBuilder
import json

def generate(handler):
    templateHelper.sendHTMLHeaders(handler)
    template = Template()
    template.loadTemplate('web/templates/createRequest.pyt')
    
    replacements = {}
    replacements['title'] = 'Sanity Installation Tool - Run Tool'
    replacements['baseurl'] = templateHelper.baseUrl(handler)
    
    config = open('conf.json')
    data = json.load(config)
    config.close()
    tests = getTests(data['tests'])
    replacements['tests'] = pageBuilder.buildTable(tests)
    
    
    handler.wfile.write(template.parseTemplate(replacements))
    
def getTests(testsData, parentName = None):
    tests = []
    for test in testsData:
        if parentName:
            tests.append(parentName + '.' + test['name'])
        else:
            tests.append(test['name'])
        if 'children' in test:
            if parentName:
                childTests = getTests(test['children'], parentName + '.' + test['name'])
            else:
                childTests = getTests(test['children'], test['name'])
                tests.extend(childTests)
    return tests