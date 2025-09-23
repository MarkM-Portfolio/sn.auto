from rtc_api import RTC_API
import time, sys

build_stream = sys.argv[1]
build_component = sys.argv[2]
print "build_component: %s" %(build_component)
#create build dictionary
buildDict = {}
buildDict['Activities'] = {'buildDef':'cd-activities-IC10.0-db2','stream':'Activities Development - IC10.0'}

#create properties dictionary
#properties = {"RUN_WITH_EMMA":"true", "TRACK_EMMA_RESULTS":"true"}
#properties = {"RUN_WITH_EMMA":"true"}
properties = {"BUILD_STREAM":build_stream, "BUILD_COMPONENT":build_component}

#default is correct base_url and projectName, so don't need parameters
t = RTC_API()#(base_url = "https://jazz01.swg.usma.ibm.com:9443", projectName = "sn.auto.sandbox")

#create the build request.
requestsOK = 'true'

buildDef = buildDict[build_component].get('buildDef')
stream = buildDict[build_component].get('stream')
		
print "Submitting build request for buildDef %s using stream %s" %(buildDef, stream)
t.auth("icci@us.ibm.com", "lcSpring13")
buildId = t.createBuildRequest(buildDefName = buildDef, personal = "true", streamName = stream, properties = properties)
if buildId == None:
	print "Could not create build request for buildDef %s using stream %s." %(buildDef, stream)
	t.cleanUp()
	sys.exit(1)
		
buildCompleted = 'false'
for i in range(36):
	t.auth("icci@us.ibm.com", "lcSpring13")
	buildCompletedStatus = t.isBuildCompleted(buildId)
	if buildCompletedStatus == 1:
		print "Build for %s completed." %(buildDef)
		buildCompleted = 'true'
		break
	elif buildCompletedStatus == -1:
		print "WARNING: Could not get build status for %s." %(buildDef)
		print "Will try again in 10 seconds."
		for j in range(6):
			time.sleep(10)
			buildCompletedStatus = t.isBuildCompleted(buildId)
			if buildCompletedStatus == 1:
				print "Build for %s completed." %(buildDef)
				buildCompleted = 'true'
				break
				
			if buildCompletedStatus == 0:
				print "Build for %s not yet completed." %(buildDef)
				break
		
			elif buildCompletedStatus == -1:
				print "WARNING: Could not get build status for %s." %(buildDef)
				print "Will try again in 10 seconds."
				continue
			
		if buildCompleted == 'true':
				break
		else:
			continue
			
	print "Build for %s not yet completed." %(buildDef)
	print "Waiting 5 minutes before checking again..."
	time.sleep(300)
		
if buildCompleted == 'false':
	print "Build for %s did not complete in 3 hours." %(buildDef)
	t.cleanUp()
	sys.exit(1)

t.cleanUp()	
sys.exit(0)
