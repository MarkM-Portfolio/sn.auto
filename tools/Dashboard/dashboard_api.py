import os
import sys
import tempfile
import subprocess

class Dashboard_API:
  def __init__(self, base_url="ilyavm.swg.usma.ibm.com:3000"):
    self.base_url = base_url
    self.cookies = tempfile.mktemp()
    self.error_message = None
    self.csrf_token = ""
    self.authed = 1 #0 = signed in, 1 = not signed in

  def auth(self, email="", password=""):
    cookieFile = self.cookies
    url = "\"" + self.base_url + "/users/sign_in\""
    cmd = "curl -k -s -b "+cookieFile+" -c "+cookieFile+" " + url + " | grep csrf-token | awk '{ print  substr($2,10,length($2)-10) }'"
    pipe = os.popen(cmd)
    self.csrf_token = pipe.readlines()[0].strip()
    cmd = "curl -k -s -b "+cookieFile+" -c "+cookieFile+" -H \"X-CSRF-Token:"+self.csrf_token+"\" -d user[email]="+email+" -d user[password]="+password+" -D - \""+self.base_url+"/users/sign_in\" -o /dev/null"
    headersPipe = os.popen(cmd)
    code = headersPipe.readlines()[0][9:12]
    if code != "302":
      self.error_message = "Authentication failed, return code: " + code
      return 1
    else:
      self.authed = 0
    try:
      cookie = open(cookieFile, "r")
      lines = cookie.readlines()
      cookie.close()
      self.cookies = cookieFile
    except:
      self.error_message = "Authentication failed"
      print "Authentication failed"
      return 1

  def reserve_server(self, os_password="", was_password=""):
    url = "\"" + self.base_url + "/server_pool/reserve\""
    cmd = "curl -k -s -b " + self.cookies + " -H \"X-CSRF-Token:"+self.csrf_token+"\" -H \"Content-Type: application/json\" -H \"Accept: application/json\" -X POST --data '{\"no_email\":true,\"os_password\":\""+os_password+"\",\"was_password\":\""+was_password+"\"}' " + url
    serverJsonPipe = os.popen(cmd)
    return serverJsonPipe.readlines()[0]

  def return_server(self, server_id):
    url = "\"" + self.base_url + "/server_pool/return_server?server=" + str(server_id) + "\""
    cmd = "curl -k -s -b " + self.cookies + " -H \"Content-Type: application/json\" -H \"Accept: application/json\" " + url
    responsePipe = os.popen(cmd)
    return responsePipe.readlines()[0]

  def get_ticket(self, ticket_id):
    url = "\"" + self.base_url + "/server_pool/get_ticket?ticket_id=" + str(ticket_id) + "\""
    cmd = "curl -k -s -b " + self.cookies + " -H \"Content-Type: application/json\" -H \"Accept: application/json\" " + url
    responsePipe = os.popen(cmd)
    return responsePipe.readlines()[0]

  def getPool(self):
    url = "\"" + self.base_url + "/server_pool\""
    cmd = "curl -k -s -b " + self.cookies + " -H \"Content-Type: application/json\" -H \"Accept: application/json\" " + url
    serverJsonPipe = os.popen(cmd)
    return serverJsonPipe.readlines()[0]

  def resetAllPoolServers(self):
    url = "\"" + self.base_url + "/server_pool/reset_all\""
    cmd = "curl -k -s -b " + self.cookies + " -H \"Content-Type: application/json\" -H \"Accept: application/json\" " + url
    response = os.popen(cmd)
    return response.readlines()[0]

  def cleanUp(self):
    if self.cookies != None:
      os.unlink(self.cookies)
