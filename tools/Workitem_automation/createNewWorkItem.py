from rtc_api import RTC_API
import sys
import datetime

def usage():
    print "USAGE: createNewWorkItem.py <buildLabel>"
    sys.exit(1)

if len(sys.argv) < 2:
    usage()

VERSION = sys.argv[1]
date = datetime.datetime.now().day

if date % 2 == 1:
    server = "3"
    db = "(Linux / DB2 / ITDS )"
else:
    server = "4"
    db = "(Linux / ORACLE / SunONE )"

t = RTC_API(base_url="https://swgjazz.ibm.com:8001", projectName = "Lotus Connections")
t.auth("icci@us.ibm.com", "lc4Spr1ng")

title = "BVT Summary for build " + VERSION
buildId = t.getBuildId(buildLabel=VERSION)
if buildId == None:
    print "Could not find build " + VERSION
    t.cleanUp()
    sys.exit(1)
if t.isWorkItemCreated(title, buildId) == 1:
    print "Workitem already exists for build " + VERSION
    t.cleanUp()
    sys.exit(1)

description = "BVT Summary for build <b>"+VERSION+"</b> against server LC30LINUX"+server+".swg.usma.ibm.com "+db+"<br/><br/>Server logs - http://lc30linux"+server+".swg.usma.ibm.com/was-logs <br/><br/>API BVT - <br/>1. Activities <br/>2. Blogs <br/>3. Communities <br/>4. Dogear <br/>5. Profiles <br/>6. Homepage <br/>7. Wikis <br/>8. Files <br/>9. Search <br/>10. Forums <br/>11. News <br/>    a. MicroBlogs <br/>    b. ActivityStreams <br/>    c. AS Search  <br/>12. Moderation  <br/><br/>Admin BVT - <br/>This is a test of the WebSphere Admin interface.<br/>1. Activities <br/>2. Blogs <br/>3. Communities <br/>4. Dogear <br/>5. Profiles <br/>6. Homepage <br/>7. Wikis <br/>8. Files <br/>9. Search <br/>10. Forums <br/>11. News  <br/><br/><b>GUI BVT - </b>Level 1 - <br/>Application    Daily BVT    Details<br/>1. Activities    <br/>2. Blogs    <br/>3. Communities    <br/>4. Dogear    <br/>5. Profiles    <br/>6. Homepage    <br/>7. Wikis    <br/>8. Files    <br/>9. Search    <br/>10. Widgets    <br/>11. Forums    <br/>12. News    <br/>13. Mobile    <br/>14. Metrics    <br/>15. Media Gallery    <br/>16. Moderation    <br/>17. Open Social Gadget    <br/><br/><b>GUI BVT - </b>Level 2 - <br/>1. Activities    <br/>2. Blogs    <br/>3. Communities    <br/>4. Dogear    <br/>5. Profiles    <br/>6. Homepage    <br/>7. Wikis    <br/>8. Files    <br/>9. Search    <br/>10. Widgets    <br/>11. Forums    <br/>12. News    <br/>13. Mobile    <br/>14. Metrics   <br/>15. Media Gallery    <br/>16. Moderation    <br/>17. Open Social Gadget    <br/>"
archived = "false"
state = "1"
severity = "l3"
subject = "bvtresults"
#contextId = t.getContextId()
contextId = "_qXJMwLvDEd2lEeY97bvVQw"
#Don't think team area is necessary
teamArea = "_FmiaMOsYEd6wz9g_R4ybXA"
priority = "l07"
projectArea = "_qXJMwLvDEd2lEeY97bvVQw"
type = "task"
#filedAgainst = t.getFiledAgainst("Automation", projectId = "_qXJMwLvDEd2lEeY97bvVQw", itemType = "task")
filedAgainst = "_AfFiwOsZEd6wz9g_R4ybXA"
ownedBy = ""
plannedFor = t.getCurrentIteration(label = "IC 4.0 Automation Iteration")
if plannedFor == None:
	plannedFor = ""

Ruairi = "_fUIuML6CEeCWr6GeTyZcTQ"
Ping = "_yOTXgCA9EeGyualZ5IPA-Q"
Adrian = "_KKoiYMQGEd6QNKycKvWgLQ"
Ilya = "_kZzkMDyJEeGbr5CuKsMLkA"
Tom = "_QHe6sLo-Ed21hbXZhPk19w"
Connor = "_oTi2sMQGEd6JN4T9oBks4w"

subscribers = [Ruairi, Ping, Adrian, Ilya, Tom, Connor]


#create item
itemId = t.createWorkItem(title, description = description, archived = archived, state = state, severity = severity, subject = subject, contextId = contextId, teamArea = teamArea, priority = priority, projectArea = projectArea, type = type, filedAgainst = filedAgainst, ownedBy = ownedBy, plannedFor = plannedFor, subscribers = subscribers)
if itemId == None:
    "Work Item could not be created."
    t.cleanUp()
    sys.exit(1)
else:
    print itemId

#associate item with build
response = t.associateItemWithBuild(itemId, buildId)
if response.find("201") == -1:
    print "Could not associate item with build"
    print "Response: " + response

t.cleanUp()
