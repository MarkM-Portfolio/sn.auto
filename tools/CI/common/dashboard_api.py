#Version 1.0.7

import os
import sys
import tempfile
import subprocess
import json as jparser

def getServerId(json, name):
    json = jparser.loads(json)
    for serv in json:
        if serv['name'] == name:
            return serv['id']

def getPoolId(json, name):
    json = jparser.loads(json)
    for pool in json:
        if pool['name'] == name:
            return pool['id']

def getIterationName(json):
    json = jparser.loads(json)
    for itr in json:
        return itr['name']

def getReleaseId(json):
    json = jparser.loads(json)
    for itr in json:
        return itr['release_id']

def ticketComplete(json):
    json = jparser.loads(json)
    return json['complete']

def writeToFile(lines, file_path):
    xml = open(file_path, 'w')
    for line in lines:
        xml.write(line)
    xml.flush()

#jparser = jparse.JSON()

class Dashboard_API:
  def __init__(self, base_url="icautomation.cnx.cwp.pnp-hcl.com"):
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
      #get new csrf tocken
      url = "\"" + self.base_url + "\""
      cmd = "curl -k -s -b "+cookieFile+" -c "+cookieFile+" " + url + " | grep csrf-token | awk '{ print  substr($2,10,length($2)-10) }'"
      self.csrf_token = os.popen(cmd).readlines()[0].strip()
    try:
      cookie = open(cookieFile, "r")
      lines = cookie.readlines()
      cookie.close()
      self.cookies = cookieFile
    except:
      self.error_message = "Authentication failed"
      print "Authentication failed"
      return 1

  def reserve_server(self, pool="Development", os_password="", was_password=""):
    pools = self.getPools()
    poolId = getPoolId(pools, pool)
    if poolId == None:
      return "{\"error\":\"Pool not found\"}"
    url = "\"" + self.base_url + "/server_pool/reserve\""
    cmd = "curl -k -s -b " + self.cookies + " -H \"X-CSRF-Token:"+self.csrf_token+"\" -H \"Content-Type: application/json\" -H \"Accept: application/json\" -X POST --data '{\"no_email\":true,\"pool\":"+str(poolId)+",\"os_password\":\""+os_password+"\",\"was_password\":\""+was_password+"\"}' " + url
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

  def getPoolServers(self):
    url = "\"" + self.base_url + "/server_pool\""
    cmd = "curl -k -s -b " + self.cookies + " -H \"Content-Type: application/json\" -H \"Accept: application/json\" " + url
    serverJsonPipe = os.popen(cmd)
    return serverJsonPipe.readlines()[0]

  def resetAllPoolServers(self, pool="Development"):
    pools = self.getPools()
    poolId = getPoolId(pools, pool)
    if poolId == None:
      return "{\"error\":\"Pool not found\"}"
    url = "\"" + self.base_url + "/server_pool/reset_all?pool="+str(poolId)+"\""
    cmd = "curl -k -s -b " + self.cookies + " -H \"Content-Type: application/json\" -H \"Accept: application/json\" " + url
    response = os.popen(cmd)
    return response.readlines()[0]

  def run_api_test(self, server, components, ldap="tds62", makePublic="false", testTitle="", deploymentType="op"):
    comp = ','.join("\""+str(x)+"\"" for x in components)
    url = "\"" + self.base_url + "/ondemand_test/run_api\""
    cmd = "curl -k -s -b " + self.cookies + " -H \"X-CSRF-Token:"+self.csrf_token+"\" -H \"Content-Type: application/json\" -H \"Accept: application/json\" -X POST --data '{\"server\":\""+server+"\",\"components\":["+comp+"],\"ldap\":\""+ldap+"\",\"deploymentType\":\""+deploymentType+"\",\"makePublic\":\""+makePublic+"\",\"testTitle\":\""+testTitle+"\"}' " + url
    responsePipe = os.popen(cmd)
    return responsePipe.readlines()[0]

  def api_pass(self, title):
    title = title.replace(" ", "%20")
    cmd = "curl -k -s -b " + self.cookies + " " + self.base_url + "/ondemand_test/get_api_public_result?title=" + title
    return os.popen(cmd).readlines()[0]

  def run_gui_simple(self, server, components, deployment = "opc", stream = "http://connectionsci1.cnx.cwp.pnp-hcl.com/bvt-IC10.0_Automation", makePublic = "false", testTitle = "Default using API"):
    runOption = "simple"
    comp = ','.join("\""+str(x)+"\"" for x in components)
    url = "\"" + self.base_url + "/ondemand_test/run_gui\""
    cmd = "curl -k -s -b " + self.cookies + " -H \"X-CSRF-Token:"+self.csrf_token+"\" -H \"Content-Type: application/json\" -H \"Accept: application/json\" -X POST --data '{\"serverName\":\""+server+"\",\"simpleComp\":["+comp+"],\"runOption\":\""+runOption+"\",\"deploymentType\":\""+deployment+"\",\"stream\":\""+stream+"\",\"makePublic\":\""+makePublic+"\",\"testTitle\":\""+testTitle+"\"}' " + url
    responsePipe = os.popen(cmd)
    return responsePipe.readlines()[0]

  def createBuild(self, build, server, release="IC 10.0"):
    release = release.replace(" ", "%20")
    cmd = "curl -s " + self.base_url + "/servers.json"
    serversJson = os.popen(cmd).readlines()[0]
    serverId = getServerId(serversJson, server)
    if serverId == None:
      return "Server \""+server+"\" not found."

    cmd = "curl -s " + self.base_url + "/iterations.json?release="+release
    iterationJson = os.popen(cmd).readlines()[0]
    iterationName = getIterationName(iterationJson)
    if iterationName == None:
      return "Iteration not found."
    releaseId = getReleaseId(iterationJson)
    if releaseId == None:
      return "Release not found."
    cmd = "curl -k -s -b " + self.cookies + " -H \"X-CSRF-Token:"+self.csrf_token+"\" -H \"Content-Type: application/json\" -H \"Accept: application/json\" -X POST --data '{\"iteration_name\":\""+str(iterationName)+"\",\"name\":\""+build+"\",\"release\": "+str(releaseId)+",\"server_id\":"+str(serverId)+"}' "+self.base_url+"/builds"
    return os.popen(cmd).readlines()[0]

  def createTestResults(self, build_id, testType_name, components, sendReport, server_id = "", comment = ""):
    results = "{"
    for i in range(len(components)):
      results = results + "\"" + str(i) + "\":{\"name\":\"" + components[i] + "\",\"patch\":\"0\",\"result\":\"true\",\"details\":\"\"},"
    results = list(results)
    results[len(results)-1] = "}"
    results = "".join(results)
    url = self.base_url + "/test_results"
    test_results = "{\"test_result\":{\"testType_name\":\"" + testType_name + "\",\"build_id\":\"" + str(build_id) + "\",\"server_id\":\"\",\"comment\":\"\",\"components_attributes\":" + results +"},\"send_report\":\"" + sendReport + "\"}"
    cmd = "curl -k -s -b " + self.cookies + " -H \"X-CSRF-Token:"+self.csrf_token+"\" -H \"Content-Type: application/json\" -H \"Accept: application/json\" -X POST --data ' " + test_results + "' " + url
    return os.popen(cmd).readlines()[0]

  def checkpoint_gui_bvt(self, serverName, buildName, components, deployment = "opc", stream = "http://connectionsci1.cnx.cwp.pnp-hcl.com/bvt-IC10.0_Automation"):
    comp = ','.join("\""+str(x)+"\"" for x in components)
    url = "\"" + self.base_url + "/ondemand_test/checkpointBvt\""
    cmd = "curl -k -s -b " + self.cookies + " -H \"X-CSRF-Token:"+self.csrf_token+"\" -H \"Content-Type: application/json\" -H \"Accept: application/json\" -X POST --data '{\"serverName\":\""+serverName+"\",\"simpleComp\":["+comp+"],\"stream\":\""+stream+"\",\"testTitle\":\"Checkpoint " + buildName +"\",\"buildName\":\""+buildName+"\",\"deploymentType\":\""+deployment+"\"}' " + url
    responsePipe = os.popen(cmd)
    return responsePipe.readlines()[0]

  def isCheckpointComplete(self, ticket_id):
    url = "\"" + self.base_url + "/ondemand_test/getCheckpointStatus?ticket_id=" + str(ticket_id) + "\""
    cmd = "curl -k -s -b " + self.cookies + " -H \"Content-Type: application/json\" -H \"Accept: application/json\" " + url
    response = os.popen(cmd)
    return ticketComplete(response.readlines()[0])

  def saveCheckpointResultXml(self, ticket_id, full_file_path):
    url = "\"" + self.base_url + "/ondemand_test/getCheckpointResults?ticket_id=" + str(ticket_id) + "\""
    cmd = "curl -k -s -b " + self.cookies + " -H \"Content-Type: application/json\" -H \"Accept: application/json\" " + url
    response = os.popen(cmd)
    writeToFile(response.readlines(), full_file_path)

  def run_production_tests(self, serverName, env, node, components, nodeIp = "", stream = "http://connectionsci1.cnx.cwp.pnp-hcl.com/bvt-IC10.0_Automation"):
    comp = ','.join("\""+str(x)+"\"" for x in components)
    url = "\"" + self.base_url + "/ondemand_test/run_production\""
    cmd = "curl -k -s -b " + self.cookies + " -H \"X-CSRF-Token:"+self.csrf_token+"\" -H \"Content-Type: application/json\" -H \"Accept: application/json\" -X POST --data '{\"serverName\":\""+serverName+"\",\"comp\":["+comp+"],\"stream\":\""+stream+"\",\"env\":\""+str(env)+"\",\"node\":\""+str(node)+"\",\"nodeIp\":\""+str(nodeIp)+"\"}' " + url
    responsePipe = os.popen(cmd)
    return responsePipe.readlines()[0]

  def update_pool_build(self, master="lc45linux1.swg.usma.ibm.com", pool="Development", build=""):
    buildParam = ""
    if build != "":
      buildParam = "&build="+build
    pools = self.getPools()
    poolId = getPoolId(pools, pool)
    if poolId == None:
      return "{\"error\":\"Pool not found\"}"

    url = "\"" + self.base_url + "/server_pool/update_build?pool="+str(poolId)+"&master="+master+buildParam+"\""
    cmd = "curl -k -s -b " + self.cookies + " -H \"Content-Type: application/json\" -H \"Accept: application/json\" " + url
    response = os.popen(cmd)
    return response.readlines()[0]

  def getPools(self):
    poolUrl = "\"" + self.base_url + "/server_pool/get_pools\""
    cmd = "curl -k -s -b " + self.cookies + " -H \"Content-Type: application/json\" -H \"Accept: application/json\" " + poolUrl
    return os.popen(cmd).readlines()[0]

  def cleanUp(self):
    if self.cookies != None:
      os.unlink(self.cookies)

if __name__ == '__main__':
  if len(sys.argv) != 5:
    print "Usage: ", sys.argv[0], "url username password command"
    sys.exit(1)
  myself, url, user, pswd, cmd = sys.argv
  #t = Dashboard_API(base_url = "icautomation.cnx.cwp.pnp-hcl.com")
  t = Dashboard_API(base_url = url)
  a = t.auth(user, pswd)
  if a == 1:
    print t.error_message
    sys.exit(1)
  if cmd == 'reset':
    r = t.resetAllPoolServers()
  elif cmd == 'update':
    r = t.update_pool_build()
  else:
    print "You need to give me a command, [update|reset]"
    sys.exit(1)
  print "Dashboard returns: ", r
