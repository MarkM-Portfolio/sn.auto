import templateHelper
import os
from Template import Template
import pageBuilder

def generate(handler):
    templateHelper.sendHTMLHeaders(handler)
    template = Template()
    template.loadTemplate('web/templates/viewResults.pyt')
    replacements = {}
    replacements['title'] = 'Sanity Installation Tool - View Results'
    
    rawParams = handler.path.split('?')
    replacements['error'] = ''
    if len(rawParams) > 1:
        if rawParams[1] == "error=true":
            replacements['error'] = '<div class="lotusMessage" role="alert"><img src="http://www-12.lotus.com/ldd/doc/oneuidoc/docResources/icons/iconError16.png" alt="error" /><p><strong>Error: Sanity could not complete the tests.</strong> Please check your configuration file or see console output for more information.</p></div>'
        elif rawParams[1] == "success=true":
            replacements['error'] = '<div class="lotusMessage lotusConfirm" role="status"><img src="http://www-12.lotus.com/ldd/doc/oneuidoc/docResources/icons/iconConfirmation16.png" alt="success" /><p><strong>Tests completed successfully.</strong></p></div>'
    
    baseUrl = templateHelper.baseUrl(handler)
    results = []
    for path in sorted(os.listdir('web/results/')):
        if path.endswith('json'):
            year = path[0:4]
            month = path[4:6]
            day = path[6:8]
            hour = path[9:11]
            min = path[11:13]
            sec = path[13:15]
            results.append(pageBuilder.buildLink('/view_result?result=' + path.rstrip('_results.json'), month+"/"+day+"/"+year+" "+hour+":"+min+":"+sec))
              
    results.reverse()                                      
    if len(results):
        replacements['resultsList'] = pageBuilder.buildList(results)
    else:
        replacements['resultsList'] = 'No results found'
    handler.wfile.write(template.parseTemplate(replacements))
