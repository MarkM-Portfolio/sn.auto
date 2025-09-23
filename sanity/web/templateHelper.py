import time

'''
Sends a 200 response code with content-typ set to text/htmls
'''
def sendHTMLHeaders(handler):
    handler.send_response(200)
    handler.send_header('Content-type', 'text/html')
    handler.end_headers()

def getFormatedTime():
    return time.strftime('%Y%m%d_%H%M%S')

def baseUrl(handler):
    (address, port) = handler.server.server_address
    addressString = handler.address_string()
    if port == 80:
        return 'http://' + addressString + '/'
    return 'http://' + addressString + ':' + str(port) + '/'
        
        