class Template:
    
    def __init__(self):
        self.content = ''
    
    '''
    Loads a template from a file
    '''
    def loadTemplate(self, filePath):
        file = open(filePath, 'r')
        self.content = file.read()
        file.close()
        
    '''
    Sets the content of this template to the string passed
    '''
    def setTemplate(self, templateContent):
        self.content = templateContent
    
    '''
    Parses the template content, replacing the fields in with values
    provided.
    The values parameter needs to be a dictionary with the key the fields
    to replace with out the % character, and the value the value to replace
    the field with.
    '''
    def parseTemplate(self, values):
        template = self.content
        for key, value in values.iteritems():
            template = template.replace('%' + key + '%', value)
        return template