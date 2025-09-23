#  This is bvt_activities_ArchiveService.py
#
#  Jython Script to test some Activity commands on IBM Activities Enterprise Application
#  (C) Copyright IBM Corporation, 2008
#
#  It creates the WebSphere server objects required for the Activities database connectivity,
#  verifies this database connectivity, and then runs some test against the Activities Enterprise Application Activity
#  using these WebSphere server objects to run on WebSphere Portal under wp_profile profile.
#
#  This script can be run from a .bat from the Windows command prompt, or a .sh on Linux shell.
#
#  The default profile_root directory for a profile named profile_name is as following:
#   - C:\Program Files\IBM\WebSphere\AppServer\profiles\profile_name (on Windows)
#   - /opt/IBM/WebSphere/AppServer/profiles/profile_name (on Linux)
#
#  The default profile name on WebSphere Application Server is 'default'.
#  The default profile name on WebSphere Portal Server is 'wp_profile'.
#

# AUTHOR:           mckennad@us.ibm.com
# INITIAL RELEASE:  LC1.02
# TEST TARGETS:     ArchiveService.exportActivities(subdir, activities); ArchiveService.fetchActivities(subdir); and ArchiveService.importActivities(subdir, activities)

import sys, traceback, java
from types import *
from java import lang
from java.lang import String
from java.lang import Boolean
from java import util
from java.util import ArrayList
from java.util import Hashtable
from java import io
import os
import com.ibm.janet.core.LineCompare
from com.ibm.janet.core.LineCompare import compareLines

#print sys.path

execfile('activitiesAdmin.py') # only do this once
execfile('bvt_common.py')
execfile('bvt_activities_common.py')
execfile('ListServiceModule.py')

#Globals
UNIQUE_SCRIPT_ID = "bvt_activities_ArchiveService"
RUNFILE = UNIQUE_SCRIPT_ID + ".run"
GOLDFILE = UNIQUE_SCRIPT_ID + ".gld"
ERRORFILE = UNIQUE_SCRIPT_ID + ".err"
PROPFILE = UNIQUE_SCRIPT_ID + ".properties"
global fErrorFile
#Set the following to "true" to set debug mode and allow detailed stack tracing
#Set the following to None to disable debug mode and allow standard error handling
DEBUG_SCRIPT = None

def BvtActivitiesArchiveService():
   print ""
   print "--------------------------------------------------------------------------------"
   print " BVT Test Script for Exporting & Importing activities created by a specified member using the ArchiveService MBean"
   print " (C) Copyright IBM Corporation, 2008 "
   print "--------------------------------------------------------------------------------"

   #Create error file for logging any errors.  This file will be removed later if no errors occurred
   #This needs to be done at the start of every script. Use the script name as part of the filename
   global fErrorFile
   fErrorFile = open(ERRORFILE, "w+")

   bvtCommon = BvtCommon()
   configProps = BvtCommon.loadProperties(bvtCommon, PROPFILE)
   loadedProps = BvtCommon.getLoadedProperties(bvtCommon, PROPFILE)
   debugLogging = BvtCommon.getProperty(bvtCommon, configProps, "debugLogging")
   if debugLogging == "true":
      logging = 1 # turn on logging
   else:
      logging = 0 # default no logging messages

   bvtActivitiesCommon = BvtActivitiesCommon()
   inited = BvtActivitiesCommon.initLibrary(bvtActivitiesCommon, logging)
   if (inited != 0):
      return

   # retrieve this script's property settings
   strMakeGoldLog = BvtCommon.getProperty(bvtCommon, configProps, "make_gold_log")
   strMakeGoldLog.lower()
   if strMakeGoldLog == "true":
      bMakeGoldLog = Boolean(Boolean.TRUE)
   else:
      bMakeGoldLog = Boolean(Boolean.FALSE)

   # Get the activity sort key - defined in the bvt_activities_ArchiveService.properties file
   activitySortKey = BvtCommon.getProperty(bvtCommon, configProps, "activity_sort_key")



   print ""
   print "ActivityService.fetchActivitiesCreatedByMember(amy jones70) fetches activities created by the specified member..."
   print ""
   print "List all Activities created by user Amy Jones70, should return 2 activities"
   aj70 = MemberService.fetchMemberByName("amy jones70")   
   aj70act = ActivityService.fetchActivitiesCreatedByMember(aj70)
   print ""
   
#  Sort the returned activities
   strUtil = com.ibm.janet.core.StringUtils(Boolean.TRUE)
   sortedActivities = strUtil.sortHashTableVectorByKey( aj70act, activitySortKey, 0)
   print "Activities Sorted Ascending Alphabetically by Key: " + activitySortKey + " ..."
   
   count = BvtActivitiesCommon.listAllActivities(bvtActivitiesCommon, sortedActivities)               
   print "%d Activities retrieved." % count

   
   
   print "Will now export the activities created by Amy Jones70 to a subdirectory called BVTExports"
   ArchiveService.exportActivities("BVTExports", aj70act)

   print "We will now do a fetch of the activities exported to BVTExports, to prove the export command worked..."
   allexp=ArchiveService.fetchActivities("BVTExports")

   #Sort the exported activities
   strUtil = com.ibm.janet.core.StringUtils(Boolean.TRUE)
   sortedExportedAct = strUtil.sortHashTableVectorByKey( allexp, activitySortKey, 0)
   
   print "Listing the activities exported to BVTExports, sorted by name...."
   count = BvtActivitiesCommon.listAllActivities(bvtActivitiesCommon, sortedExportedAct)               
   print "%d Activities retrieved." % count

      
   flags = Hashtable()
   flags.put("append", "false")
   # writing results to run log; not appending to the file
   BvtActivitiesCommon.writeActivitiesToFile(bvtActivitiesCommon, RUNFILE, sortedExportedAct, flags )


   print  "Now will delete the activities we exported from the server's db - this will put them into the Trash folder."
   ActivityService.deleteActivities(aj70act)

   print "Will now fetch all Activities in the Trash (should be 3 entries) and purge them..."
   alltrash = TrashCollectionService.fetchTrash()
   TrashCollectionService.purgeTrash(alltrash)

   print "We will now do a fetchActivities command again to confirm they are really purged."
   print "Should get back zero activities."
   zero=ActivityService.fetchActivitiesCreatedByMember(aj70)
   
   count = BvtActivitiesCommon.listAllActivities(bvtActivitiesCommon, zero)
   print "%d Activities retrieved." % count

   print "Now will import all activities (2) that we previously exported.  The import will re-create the 2 acts."
   allimp=ArchiveService.fetchActivities("BVTExports")
   ArchiveService.importActivities("BVTExports", allimp)

   print "Now will confirm that the activities have been imported into the server's db."
   print "Doing an ActivityService.fetchActivitiesCreatedByMember(aj70)cmd.  Should return 2 activities."
   importedActs=ActivityService.fetchActivitiesCreatedByMember(aj70)

   #Sort the activities we just fetched from the server....
   strUtil = com.ibm.janet.core.StringUtils(Boolean.TRUE)
   sortedImportedActs = strUtil.sortHashTableVectorByKey( importedActs, activitySortKey, 0)

   print "Listing the activities imported from BVTExports...."
   count = BvtActivitiesCommon.listAllActivities(bvtActivitiesCommon, sortedImportedActs)
   print "%d Activities retrieved." % count
   
   flags = Hashtable()
   flags.put("append", "true")
   # writing results to run log; appending to the end of the file
   BvtActivitiesCommon.writeActivitiesToFile(bvtActivitiesCommon, RUNFILE, sortedImportedActs, flags )

   #  CLEAN UP!  Will delete the activities from the export subdirectory (BVTExports)
   print "As a clean up - now deleting the exported activities from BVTExports subdirectory."
   ArchiveService.deleteActivities("BVTExports", allimp)

   #Compare the test run results with the Gold File Results
   #lc.compareLines( ",", RUNFILE, GOLDFILE, None)
   lc = com.ibm.janet.core.LineCompare(Boolean.TRUE, loadedProps, bMakeGoldLog)
   lc.compareLines( "::", RUNFILE, GOLDFILE)
   
   print ""
   return

# =========================================================

#global BvtCommon
global bvtActivitiesCommon

bvtActivitiesCommon=BvtActivitiesCommon()
print "BVT ArchiveService - import/export Activities test ... starting."

#-------------------------------------------------
# Main  - just execute the ArchiveService test function
#-------------------------------------------------
print "BVT ArchiveService Test ... calling BvtActivitiesArchiveService()"
if DEBUG_SCRIPT != None:
   print "WARNING: Script is in DEBUG MODE!  Set DEBUG_SCRIPT variable (at top of script) to None to disable debug mode!"
   BvtActivitiesArchiveService()
   global fErrorFile
   fErrorFile.close()
   os.unlink(ERRORFILE)
else:	
   try:
      BvtActivitiesArchiveService()
   except:
      global fErrorFile
      print '*****************************************************************'
      print 'FAIL FAIL FAIL FAIL FAIL FAIL FAIL FAIL FAIL FAIL FAIL FAIL FAIL'
      print '*****************************************************************'
      traceback.print_exception(sys.exc_info()[0], sys.exc_info()[1], sys.exc_info()[2])
      traceback.print_exception(sys.exc_info()[0], sys.exc_info()[1], sys.exc_info()[2], limit = None, file = fErrorFile)
      fErrorFile.close()
   else:
      global fErrorFile
      fErrorFile.close()
      os.unlink(ERRORFILE)
      print '*****************************************************************'
      print 'PASS PASS PASS PASS PASS PASS PASS PASS PASS PASS PASS PASS PASS'
      print '*****************************************************************'
      print ""

