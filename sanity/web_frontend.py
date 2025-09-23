from BaseHTTPServer import BaseHTTPRequestHandler, HTTPServer
import os
import imp
import sys
from time import time
sys.path.insert(0, os.path.join(os.getcwd(),'web'))
sys.path.insert(1, os.path.join(os.getcwd(),'bin'))

class FrontendRequestHandler(BaseHTTPRequestHandler):
    def do_GET(self):
        start = time()
        
        pathParts = self.path.split('/')
        templatePath = 'web/scripts/'
        if pathParts[1] == 'stop_server':
            self.server.shutdown()        
        elif pathParts[1] == 'view_results':
            templatePath = os.path.join(templatePath, 'viewResults.py')
        elif pathParts[1].startswith('view_result'):
            templatePath = os.path.join(templatePath, 'viewResult.py')
        elif pathParts[1] == 'create_request':
            templatePath = os.path.join(templatePath, 'createRequest.py')
        elif pathParts[1].startswith('start_tool'):
            templatePath = os.path.join(templatePath, 'startTool.py')
        elif pathParts[1] ==  '':
            templatePath = os.path.join(templatePath, 'index.py')
        elif self.path.endswith('.js') or self.path.endswith('.css'):
            filePath = os.path.join('web', self.path.lstrip('/'))
            if not os.path.exists(filePath):
                self.send_error(404, 'The resource could not be found')
                return
            file = open(filePath)
            self.send_response(200)
            
            if self.path.endswith('.js'):
                self.send_header('Content-type', 'application/javascript')
            else:
                self.send_header('Content-type', 'text/css')
                
            self.end_headers()
            self.wfile.write(file.read())
            file.close()
            return
        elif self.path.endswith('.json') or self.path.endswith('.log'):
            filePath = os.path.join('web/', self.path.lstrip('/'))
            if not os.path.exists(filePath):
                self.send_error(404, 'The resource could not be found')
                return
            file = open(filePath)
            self.send_response(200)
            self.send_header('Content-type', 'text/plain')
            self.end_headers()
            self.wfile.write(file.read())
            file.close()
            return
        else:
            templatePath = os.path.join(templatePath, 'index.py')
        template = imp.load_source('template', templatePath)
        template.generate(self)
        elapse = time() - start
        self.log_message('Time required to process request: ' + str(elapse) + 's')
        
def start():
    address = ('0.0.0.0', 8000)
    httpd = HTTPServer(address, FrontendRequestHandler)
    print('Starting Server')
    print('Press ctrl+c to stop the server')
    try:
        httpd.serve_forever()
    except KeyboardInterrupt:
        print('Stopping Server')
        httpd.shutdown()
        print('Server Stopped')
    
start()
