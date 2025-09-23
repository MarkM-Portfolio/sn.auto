import templateHelper
from Template import Template
import pageBuilder

def generate(handler):
	templateHelper.sendHTMLHeaders(handler)
	template = Template()
	template.loadTemplate('web/templates/index.pyt')
	replacements = {}
	replacements['title'] = 'Sanity Installation Tool'
	replacements['baseUrl'] = templateHelper.baseUrl(handler)
	handler.wfile.write(template.parseTemplate(replacements))