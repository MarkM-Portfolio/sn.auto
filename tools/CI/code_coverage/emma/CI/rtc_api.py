import os
import sys
import tempfile
import org.apache.wink.json4j as jparse
import subprocess

jparser = jparse.JSON()

def printUsage():
    print '''Here is how to use:
    Call auth(username, password)

    Constructor: RTC_API(base_url, projectName) : defult base_url="https://swgjazz.ibm.com:8001", projectName="Lotus Connections"

    Methods that return ids can also return urls if "wantUrl" parm is 0
    Available methods:
    getPersonId(name, [wantUrl])
    getBuildDef(buildDefName)
    getBuildId(buildLabel, [buildDefinition, [wantUrl]]) : buildDefinition default is "LCI4.0".
    getWorkItemId(buildId, title, [wantUrl]
    getCurrentIteration([label, [wantUrl]]) : label default is "IC 4.0 It"
    getContextId() : gets project id specified by project name
    getFiledAgainst(name, projectId, itemType)
    createWorkItem(title, [description, [archived, [state, [severity, [subject, [contextId, [teamArea, [priority, [projectArea, [type, [filedAgainst, [ownedBy, [plannedFor, [subscribers]]]]]]]]]]]]]])
    associateItemWithBuild(itemId, buildId)
    createBuildRequest(buildDefName, personal, streamName, properties) : properties is a dictionary of string properties
    cleanUp() - deletes cookies
    '''

def getPersonId(userJson):
    jobj = jparser.parse(userJson)
    return jobj['soapenv:Body']['response']['returnValue']['value']['elements'][0]['itemId']

def urlEncode(name):
    return name.replace(" ", "%20")

def getBuildDefId(buildDefsJson, buildDefinition):
    jobj = jparser.parse(buildDefsJson)
    for bDef in jobj:
        buildDef=bDef['buildDefinition']
        if buildDef['id']==buildDefinition:
            return buildDef['itemId']
        
def getBuildId(buildsJson, buildLabel):
    jobj = jparser.parse(buildsJson)
    for build in jobj:
        if build['label'] == buildLabel:
            return build['itemId']

def getItemUrl(buildJson, title):
    jobj = jparser.parse(buildJson)
    try:
        for contribution in jobj['contributions']:
            contr = contribution['contribution']
            if contr['label'].find(title) != -1:
                return contr['extendedContributionProperties'][0]['value']
    except:
        return None
    return None

def getItemUrlBySearch(json, title):
    jobj = jparser.parse(json)
    try:
        return jobj['soapenv:Body']['response']['returnValue']['value']['workItemSummaryDTOs'][0]['locationUri']
    except:
        return None

def getIterationId(iterationJson, label):
    jobj = jparser.parse(iterationJson)
    for itr in jobj['soapenv:Body']['response']['returnValue']['value']['referencedItems']:
        if itr['label'].find(label) != -1:
            return itr['iteration'].split(";")[1]
    return None

def getContextId2(contextXml, projectName):
    foundProject = 0
    for line in contextXml:
        if foundProject == 1:
            if line.find("<oslc_disc:details") != -1:
                url = line.split("\"")[1]
                sUrl = url.split("/")
                return sUrl[len(sUrl)-1]
        if line.find("<dc:title>" + projectName) != -1:
            foundProject = 1

def getFiledAgainstId(filedAgainstJson, name):
    jobj = jparser.parse(filedAgainstJson)
    try:
        values = jobj['soapenv:Body']['response']['returnValue']['value']['allValues']
        for value in values:
            if value['attributeName'] == "category":
                for item in value['uiItems']:
                    if item['label'].strip() == name:
                        return item['id']
    except:
        return None

def checkItemCreated(json, name):
    jobj = jparser.parse(json)
    itemExists = 0
    for contribution in jobj['contributions']:
        contr = contribution['contribution']
        if contr['label'].find(name) != -1:
            itemExists = 1
            return itemExists
    return itemExists

def createWorkItemJson(base_url, title, description, archived, state, severity, subject, contextId, teamArea, priority, projectArea, type, filedAgainst, ownedBy, plannedFor, subscribers):
    jsonFile = tempfile.mktemp()
    obj = jparse.JSONObject()
    obj.put("dc:title", title)
    if description != None and description != "":
        obj.put("dc:description", description)
    if archived != None and archived != "":
        obj.put("rtc_cm:archived", archived)
    if state != None and state != "":
        state = base_url + "/jazz/oslc/workflows/" + contextId + "/states/com.ibm.team.workitem.taskWorkflow/" + state
        stateMap = {"rdf:resource":state}
        obj.put("rtc_cm:state", stateMap)
    if severity != None and severity != "":
        severity = base_url + "/jazz/oslc/enumerations/" + contextId + "/severity/severity.literal." + severity
        severityMap = {"rdf:resource":severity}
        obj.put("oslc_cm:severity", severityMap)
    if subject != None and subject != "":
        obj.put("dc:subject", subject)
    if contextId != None and contextId != "":
        obj.put("rtc_cm:contextId", contextId)
    if teamArea != None and teamArea != "":
        teamArea = base_url + "/jazz/oslc/teamareas/" + teamArea
        teamAreaMap = {"rdf:resource":teamArea}
        obj.put("rtc_cm:teamArea", teamAreaMap)
    if priority != None and priority != "":
        priority = base_url + "/jazz/oslc/enumerations/" + contextId + "/priority/priority.literal." + priority
        piorityMap = {"rdf:resource":priority}
        obj.put("oslc_cm:priority", piorityMap)
    if projectArea != None and projectArea != "":
        projectArea = base_url + "/jazz/oslc/projectareas/" + projectArea
        projectAreaMap = {"rdf:resource":projectArea}
        obj.put("rtc_cm:projectArea", projectAreaMap)
    if type != None and type != "":
        type = base_url + "/jazz/oslc/types/" + contextId + "/" + type
        typeMap = {"rdf:resource":type}
        obj.put("dc:type", typeMap)
    if filedAgainst != None and filedAgainst != "":
        filedAgainst = base_url + "/jazz/resource/itemOid/com.ibm.team.workitem.Category/" + filedAgainst
        filedAgainstMap = {"rdf:resource":filedAgainst}
        obj.put("rtc_cm:filedAgainst", filedAgainstMap)
    if ownedBy != None and ownedBy != "":
        ownedBy =  base_url + "/jazz/oslc/users/" + ownedBy
        ownedByMap = {"rdf:resource":ownedBy}
        obj.put("rtc_cm:ownedBy", ownedByMap)
    if plannedFor != None and plannedFor != "":
        plannedFor = base_url + "/jazz/oslc/iterations/" + plannedFor
        plannedForMap = {"rdf:resource":plannedFor}
        obj.put("rtc_cm:plannedFor", plannedForMap)
    subscrs = []
    for subscriber in subscribers:
        subscriber = base_url + "/jazz/oslc/users/" + subscriber
        subscriberMap = {"rdf:resource":subscriber}
        subscrs.append(subscriberMap)
    obj.put("rtc_cm:subscribers", subscrs)
    json = obj.toString()
    
    file = open(jsonFile, "w")
    file.write(json)
    file.close()
    return file

def getNewItemId(newItem):
    jobj = jparser.parse(newItem)
    return jobj['dc:identifier']

def createFiledAgainstJson(buildId):
    jsonFile = tempfile.mktemp()
    obj = jparse.JSONObject()
    obj.put("rdf:resource", "itemOid/com.ibm.team.build.BuildResult/" + buildId)
    file = open(jsonFile, "w")
    file.write(obj.toString())
    file.close()
    return file

def getStreamId(streamJson, streamName):
    jobj = jparser.parse(streamJson)
    for stream in jobj['jazz_scm:results']:
        if stream['dcterms:name'] == streamName:
            return stream['jazz_scm:itemId']

def getInternalId(json):
    jobj = jparser.parse(json)
    properties = jobj['properties']
    for property in properties:
        if property['kind'] == "com.ibm.team.scm.property.workspace":
            return property['internalId']

def createBuildRequestJson(defenitionName, personal, properties, streamId, internalId):
    jsonFile = tempfile.mktemp()
    obj = jparse.JSONObject()
    obj.put("definition", defenitionName)
    obj.put("allowDuplicateRequests", "true")
    allProperties = []
    if personal == "true":
        obj.put("personalBuild", "true")
        map = {"value":streamId, "kind":"com.ibm.team.scm.property.workspace", "genericEditAllowed":"false", "internalId":internalId, "name":"team.scm.workspaceUUID", "description":"", "required":"true"}
        allProperties.append(map)
    pKeys = properties.keys()
    for key in pKeys:
        pMap = {"kind":"com.ibm.team.build.property.string", "name":key, "value":properties[key], "description":"", "isRequired":"false", "isGenericEditAllowed":"true"}
        allProperties.append(pMap)
    obj.put("newOrModifiedProperties", allProperties)
    json = obj.toString()

    file = open(jsonFile, "w")
    file.write(json)
    file.close()
    return file

def getBuildIdByRequest(buildsJson, requestId):
    jobj = jparser.parse(buildsJson)
    for build in jobj:
        if build['buildRequests'][0]['itemId'].strip() == requestId.strip():
            return build['buildResult']['itemId'].strip()
    return None

def getPipeOutput(pipe):
	while True:
		#out = pipe.stdout.read()
		out = pipe.communicate()[0]
		if out != '':
			sys.stdout.flush()
			break
	return out

def isComplete(buildJson):
    jobj = jparser.parse(buildJson)
    if jobj['buildResult']['buildState'] == "COMPLETED":
        return 1
    else:
        return 0

class RTC_API:
    def __init__(self, base_url="https://swgjazz.ibm.com:8001", projectName = "Lotus Connections"):
        self.base_url = base_url
        self.projectName = projectName
        self.cookies = None
        
    def usage(self):
        printUsage()

    def authRequired(self):
        print "Please authenticate by calling auth method first."
        sys.exit(1)

    def auth(self, username="", password=""):
        self.cleanUp()
        cookieFile = tempfile.mktemp()
        cmd = "curl -k -L -b "+cookieFile+" -c "+cookieFile+" -d j_username="+username+" -d j_password="+password+" \""+self.base_url+"/jazz/authenticated/j_security_check\" > /dev/null 2>&1"
        os.system(cmd)
        try:
            cookie = open(cookieFile, "r")
            lines = cookie.readlines()
            cookie.close()
            self.cookies = cookieFile
        except:
            print "Authentication failed"
            sys.exit(1)

    def getPersonId(self, name = "", wantUrl = 0):
        if self.cookies == None:
            self.authRequired()
        name = urlEncode(name)
        url = "\"" + self.base_url + "/jazz/service/com.ibm.team.process.internal.service.web.IProcessWebUIService/contributors?sortBy=name&searchTerm=%25" + name + "%25&searchField=name&pageSize=250&hideAdminGuest=false&hideUnassigned=true&hideArchivedUsers=true\""

        cmd = "curl -k -s -b " + self.cookies + " -H \"Content-Type: text/json\" -H \"Accept: text/json\" " + url

        usersJsonPipe = os.popen(cmd)
        userJson = usersJsonPipe.readlines()[0]
        try:
            id = getPersonId(userJson)
        except:
            return None
        if wantUrl != 0:
            return self.base_url + "/jazz/oslc/users/" + id
        else:
            return id

    def getBuildDef(self, buildDefName = "LCI4.0"):
        if self.cookies == None:
            self.authRequired()
        projectId = self.getContextId()
        if projectId == None:
            print "Project Name was not found"
            return None
        buildDefUrl = "\"" + self.base_url + "/jazz/resource/virtual/build/definitionstatusrecords?projectAreaUUID="+projectId+"&includeChildTeamAreas=true&profile=REDUCED\""
        cmd = "curl -k -s -b " + self.cookies + " " + buildDefUrl
        buildDefsJsonPipe = os.popen(cmd)
        buildDefsJson = buildDefsJsonPipe.readlines()[0]
        return getBuildDefId(buildDefsJson, buildDefName)

    def getBuildId(self, buildLabel = "", buildDefinition = "LCI4.0", wantUrl = 0):
        if self.cookies == None:
            self.authRequired()
        buildDefId = self.getBuildDef(buildDefinition)
        if buildDefId == None:
            print "Build Definition not found"
            return None

        buildUrl = "\"" + self.base_url + "/jazz/resource/virtual/build/results?definitionUUID=" + buildDefId + "&profile=REDUCED\""
        cmd = "curl -k -s -b " + self.cookies + " " + buildUrl
        buildJsonPipe = os.popen(cmd)
        buildJson = buildJsonPipe.readlines()[0]
        buildId = getBuildId(buildJson, buildLabel)
        if buildId == None:
            return None
        if wantUrl != 0:
            return self.base_url + "/jazz/resource/virtual/build/resultpresentation/" + buildId
        else:
            return buildId

    def getWorkItemId(self, buildId = "", title = "", wantUrl = 0):
        if self.cookies == None:
            self.authRequired()
        if title == "":
            return None
        if buildId != "":
            url = self.base_url + "/jazz/resource/virtual/build/resultpresentation/" + buildId
        else:
            title = urlEncode(title)
            url = "\"" + self.base_url + "/jazz/service/com.ibm.team.workitem.common.internal.rest.IQueryRestService/results?maxResults=100&fullText="+title+"&projectAreaItemId=_qXJMwLvDEd2lEeY97bvVQw\""
        cmd = "curl -k -s -b " + self.cookies + " -H \"Content-Type: text/json\" -H \"Accept: text/json\" " + url
        buildJsonPipe =  os.popen(cmd)
        buildJson = buildJsonPipe.readlines()[0]
        if buildId != "":
            itemUrl = getItemUrl(buildJson, title)
        else:
            itemUrl = getItemUrlBySearch(buildJson, title)
        if itemUrl == None:
            return None
        if wantUrl != 0:
            return itemUrl
        else:
            urlSplit = itemUrl.split("/")
            return urlSplit[len(urlSplit)-1]

    def getCurrentIteration(self, label = "Automation iteration", wantUrl = 0):
        if self.cookies == None:
            self.authRequired()
        url = "\"" + self.base_url + "/jazz/service/com.ibm.team.apt.internal.service.rest.IPlanRestService/planSearchResults?projectAreaId=_qXJMwLvDEd2lEeY97bvVQw&currentOnly=true&includeBacklog=true&maxResults=200\""
        cmd = "curl -k -s -b " + self.cookies + " -H \"Content-Type: text/json\" -H \"Accept: text/json\" " + url
        iterationJsonPipe =  os.popen(cmd)
        iterationJson = iterationJsonPipe.readlines()[0]

        iterationId = getIterationId(iterationJson, label)
        if iterationId == None:
            return None
        if wantUrl != 0:
            return "https://swgjazz.ibm.com:8001/jazz/oslc/iterations/" + iterationId
        else:
            return iterationId

    def getContextId(self):
        if self.cookies == None:
            self.authRequired()
        url = "\"" + self.base_url + "/jazz/oslc/workitems/catalog\""
        cmd = "curl -k -s -b " + self.cookies + " " + url
        contextPipe = os.popen(cmd)
        contextXml = contextPipe.readlines()
        return getContextId2(contextXml, self.projectName)

    def getFiledAgainst(self, name, projectId = "_qXJMwLvDEd2lEeY97bvVQw", itemType = "defect"):
        if self.cookies == None:
            self.authRequired()
        url = "\"" + self.base_url + "/jazz/service/com.ibm.team.workitem.common.internal.rest.IWorkItemRestService/workItemEditableProperties?newWorkItem=true&typeId="+itemType+"&projectAreaItemId=" + projectId + "\""
        cmd = "curl -k -s -b " + self.cookies + " -H \"Content-Type: text/json\" -H \"Accept: text/json\" " + url
        filedAgainstPipe = os.popen(cmd)
        filedAgainstJson = filedAgainstPipe.readlines()[0]
        return getFiledAgainstId(filedAgainstJson, name)

    def isWorkItemCreated(self, name, buildId):
        if self.cookies == None:
            self.authRequired()
        url = "\"" + self.base_url + "/jazz/resource/virtual/build/resultpresentation/" + buildId + "\""
        cmd = "curl -k -s -b " + self.cookies + " -H \"Content-Type: text/json\" -H \"Accept: text/json\" " + url
        pipe = os.popen(cmd)
        return checkItemCreated(pipe.readlines()[0], name)

    def createWorkItem(self, title, description = "", archived = "false", state = "1", severity = "l3", subject = "", contextId = "_qXJMwLvDEd2lEeY97bvVQw", teamArea = "", priority = "l07", projectArea = "_qXJMwLvDEd2lEeY97bvVQw", type = "task", filedAgainst = "", ownedBy = "", plannedFor = "", subscribers = []):
        if self.cookies == None:
            self.authRequired()
        newWorkItemJson = createWorkItemJson(self.base_url, title, description, archived, state, severity, subject, contextId, teamArea, priority, projectArea, type, filedAgainst, ownedBy, plannedFor, subscribers)
        url = "\"" + self.base_url + "/jazz/oslc/contexts/"+contextId+"/workitems\""
        cmd = "curl -k -s -b " + self.cookies + " -H \"Content-Type: application/x-oslc-cm-change-request+json\" -H \"Accept: text/json\" -X POST --data-binary @"+newWorkItemJson.name+" " + url
        newItemPipe = os.popen(cmd)
        newItemLines = newItemPipe.readlines()
        os.unlink(newWorkItemJson.name)
        if len(newItemLines) == 1:
            itemId = getNewItemId(newItemLines[0])
            return itemId
        else:
            print newItemLines[1]
            return None

    def associateItemWithBuild(self, itemId, buildId):
        if self.cookies == None:
            self.authRequired()
        if type(itemId) == int:
            sItemId = str(itemId)
        else:
            sItemId = itemId.strip()
        filedAgainstJson = createFiledAgainstJson(buildId)
        url = "\"" + self.base_url + "/jazz/oslc/workitems/" + sItemId + "/rtc_cm:com.ibm.team.build.linktype.reportedWorkItems.com.ibm.team.build.common.link.reportedAgainstBuilds\""
        cmd = "curl -k -s -D - -b " + self.cookies + " -H \"Content-Type: text/json\" -H \"Accept: text/json\" -X POST --data-binary @" + filedAgainstJson.name + " " + url
        for i in range(20): 
            pipe = os.popen(cmd)
            response = pipe.readlines()[0]
            if response.find("201"):
                os.unlink(filedAgainstJson.name)
                return response
        os.unlink(filedAgainstJson.name)
        return response

    def createBuildRequest(self, buildDefName, personal = "true", streamName = "", properties = {}):
        if self.cookies == None:
            self.authRequired()
        streamId = ""
        internalId = ""
        if personal == "true":
            url = "\"" + self.base_url + "/jazz/oslc-scm/stream?dcterms:name="+streamName+"&jazz_scm:nameKind=jazz_scm:partialIgnorecase&jazz_scm:maxResults=25\""
            cmd = "curl -k -s -b " + self.cookies + " -H \"Content-Type: text/json\" -H \"Accept: text/json\" -H \"X-Method-Override: REPORT\" -X POST " + url
            pipe = os.popen(cmd)
            streamId = getStreamId(pipe.readlines()[0], streamName)
            if streamId == None:
                print "Stream not found"
                return None

            buildDefId = self.getBuildDef(buildDefName)
            url = "\"" + self.base_url + "/jazz/resource/virtual/build/definition/" + buildDefId + "\""
            cmd = "curl -k -s -b " + self.cookies + " -H \"Content-Type: text/json\" -H \"Accept: text/json\" " + url
            pipe = os.popen(cmd)
            internalId = getInternalId(pipe.readlines()[0])
            if internalId == None:
                print "build definition not found."
                return None
        buildRequestJson = createBuildRequestJson(buildDefName, personal, properties, streamId, internalId)
        url = "\"" + self.base_url + "/jazz/resource/virtual/build/requests\""
        cmd = "curl -k -s -D - -b " + self.cookies + " -H \"Content-Type: text/json\" -H \"Accept: text/json\" -X POST --data-binary @" + buildRequestJson.name + " " + url
        for i in range(20):
            pipe = os.popen(cmd)
            lines = pipe.readlines()
            if lines[0].find("201"):
                print lines[0]
                for l in lines:
                    if l.find("https") != -1: 
                        requestUrl = l.split("/")
                requestId = requestUrl[len(requestUrl)-1]
                os.unlink(buildRequestJson.name)

                projectId = self.getContextId()
                if projectId == None:
                    print "Project Name was not found"
                    return None
                queueUrl = "\"" + self.base_url + "/jazz/resource/virtual/build/queue?projectAreaUUID="+projectId+"&profile=REDUCED\""
                cmd = "curl -k -s -b " + self.cookies + " " + queueUrl
                buildJsonPipe = os.popen(cmd)
                buildJson = buildJsonPipe.readlines()[0]
                bId = getBuildIdByRequest(buildJson, requestId)
                if bId == None:
                    print "Couldn't find build id of the build request"
                return bId
        os.unlink(buildRequestJson.name)
        print "Could not create request"
        return None

    def isBuildCompleted(self, buildId):
        if self.cookies == None:
            self.authRequired()
        url = "\"" + self.base_url + "/jazz/resource/virtual/build/resultpresentation/" + buildId + "\""
        cmd = "curl -k -s -b " + self.cookies + " -H \"Content-Type: text/json\" -H \"Accept: text/json\" " + url
        buildJsonPipe = subprocess.Popen(cmd,stdout=subprocess.PIPE, stderr=subprocess.PIPE, shell=True)
        try:
            buildJson = getPipeOutput(buildJsonPipe)
            buildJsonPipe.stdout.close()
        except:
            return -1
        return isComplete(buildJson)

    def cleanUp(self):
        if self.cookies != None:
            os.unlink(self.cookies)
