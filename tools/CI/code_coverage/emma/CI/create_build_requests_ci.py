from rtc_api import RTC_API
import time, sys

#create build dictionary
buildDict = {}
#buildDict['activities'] = {'buildDef':'ci-activities-IC10.0-db2','stream':'Activities Development - IC10.0'}
buildDict['blogs'] = {'buildDef':'ci-blogs-IC10.0-db2','stream':'Blogs Development - IC10.0'}
buildDict['bookmarks'] = {'buildDef':'ci-bookmarks-IC10.0-db2','stream':'Bookmarks Development - IC10.0'}
#buildDict['communities'] = {'buildDef':'ci-communities-IC10.0-db2','stream':'Communities Development - IC10.0'}
buildDict['files'] = {'buildDef':'ci-files-IC10.0-db2-littleton','stream':'Share Development - IC10.0'}
buildDict['forums'] = {'buildDef':'ci-forums-IC10.0-db2','stream':'Forums Development - IC10.0'}
#buildDict['homepage'] = {'buildDef':'ci-homepage-IC10.0-db2','stream':'Homepage-news Development - IC10.0'}
#buildDict['infra'] = {'buildDef':'ci-infra-IC10.0','stream':'Infra Development - IC10.0'}
buildDict['metrics'] = {'buildDef':'ci-metrics-IC10.0','stream':'Metrics Development - IC10.0'}
buildDict['news'] = {'buildDef':'ci-news-IC10.0-db2','stream':'Homepage-news Development - IC10.0'}
#buildDict['profiles'] = {'buildDef':'ci-profiles-IC10.0-db2','stream':'Profiles Development - IC10.0'}
#buildDict['search'] = {'buildDef':'ci-search-IC10.0-db2','stream':'Search Development - IC10.0'}
buildDict['wikis'] = {'buildDef':'ci-wikis-IC10.0-db2','stream':'Share Development - IC10.0'}

#create properties dictionary
properties = {"RUN_WITH_EMMA":"true", "TRACK_EMMA_RESULTS":"true"}
#properties = {"RUN_WITH_EMMA":"true"}

#default is correct base_url and projectName, so don't need parameters
t = RTC_API()#(base_url = "https://jazz01.swg.usma.ibm.com:9443", projectName = "sn.auto.sandbox")

#create the build requests for each component for which there is a dictionary entry.
keyList = buildDict.keys()
keyList.sort()
requestsOK = 'true'
for k in keyList:
	buildDef = buildDict[k].get('buildDef')
	stream = buildDict[k].get('stream')
		
	print "EMMA: Submitting build request for buildDef %s using stream %s" %(buildDef, stream)
	t.auth("icci@us.ibm.com", "lcSummer13")
	buildId = t.createBuildRequest(buildDefName = buildDef, personal = "true", streamName = stream, properties = properties)
	if buildId == None:
		print "EMMA: Could not create build request for buildDef %s using stream %s." %(buildDef, stream)
		requestsOK = 'false'
		continue
		
	buildCompleted = 'false'
	for i in range(36):
		t.auth("icci@us.ibm.com", "lcSummer13")
		buildCompletedStatus = t.isBuildCompleted(buildId)
		if buildCompletedStatus == 1:
			print "EMMA: Build for %s completed." %(buildDef)
			buildCompleted = 'true'
			break
		elif buildCompletedStatus == -1:
			print "EMMA: WARNING: Could not get build status for %s." %(buildDef)
			print "EMMA: Will try again in 10 seconds."
			for j in range(6):
				time.sleep(10)
				buildCompletedStatus = t.isBuildCompleted(buildId)
				if buildCompletedStatus == 1:
					print "EMMA: Build for %s completed." %(buildDef)
					buildCompleted = 'true'
					break
				
				if buildCompletedStatus == 0:
					print "EMMA: Build for %s not yet completed." %(buildDef)
					break
		
				elif buildCompletedStatus == -1:
					print "EMMA: WARNING: Could not get build status for %s." %(buildDef)
					print "EMMA: Will try again in 10 seconds."
					continue
			
			if buildCompleted == 'true':
					break
			else:
				continue
			
		print "EMMA: Build for %s not yet completed." %(buildDef)
		print "EMMA: Waiting 5 minutes before checking again..."
		time.sleep(300)
		
	if buildCompleted == 'false':
		print "EMMA: Build for %s did not complete in 3 hours." %(buildDef)
		requestsOK = 'false'
		continue

#delete cookies
t.cleanUp()

if requestsOK == 'false':
	sys.exit(1)
	
sys.exit(0)
