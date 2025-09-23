#
#  Jython Script to test some Activity commands on IBM Activities Enterprise Application
#  (C) Copyright IBM Corporation, 2007
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

# AUTHOR:           tuohy@us.ibm.com
# INITIAL RELEASE:  LC1.02
# TEST TARGETS:     None (For Cleanup Usage Only)

import sys, traceback, java
from types import *
from java import lang
from java.lang import String
from java.lang import Boolean
from java import util
from java.util import ArrayList
from java.util import Hashtable
from java.util import Vector
from java import io
import os
import com.ibm.janet.core.LineCompare
from com.ibm.janet.core.LineCompare import compareLines
import com.ibm.janet.core.StringUtils
from com.ibm.janet.core.StringUtils import sortHashTableVectorByKey

#print sys.path

execfile('activitiesAdmin.py') # only do this once
execfile('bvt_common.py')
execfile('bvt_activities_common.py')
execfile('ListServiceModule.py')


#Globals
UNIQUE_SCRIPT_ID = "bvt_delete_all_activities_by_creator"
RUNFILE = UNIQUE_SCRIPT_ID + ".run"
GOLDFILE = UNIQUE_SCRIPT_ID + ".gld"
ERRORFILE = UNIQUE_SCRIPT_ID + ".err"
PROPFILE = UNIQUE_SCRIPT_ID + ".properties"
global fErrorFile
#Set the following to "true" to set debug mode and allow detailed stack tracing
#Set the following to None to disable debug mode and allow standard error handling
DEBUG_SCRIPT = None

def BvtDeleteAllActivitiesByCreator():
   print ""
   print "--------------------------------------------------------------------------------"
   print " BVT Test Script for deleting all activities on the system that were created by a specific user"
   print " (C) Copyright IBM Corporation, 2007 "
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

   #Get the activity sort key
   activitySortKey = BvtCommon.getProperty(bvtCommon, configProps, "activity_sort_key")

   activityOwner = BvtCommon.getProperty(bvtCommon, configProps, "fetch_activities_by_creator_email")
   print "Test is using Activity Owner: " + activityOwner

   actOwner = MemberService.fetchMemberByEmail(activityOwner)
   if actOwner != None:
      print "Successfully retrieved from LDAP Directory, specified activity owner: DisplayName = " + actOwner.get("displayName") + ", MemberID = " + actOwner.get("memberId") + " , email = " + actOwner.get("email")
   else:
      raise "ERROR: Could not retrieve specified Activity Owner: " + activityOwner 
   	
   print ""
   print "Fetching Activities created by Member: ..." + activityOwner
   print ""
   activities = BvtActivitiesCommon.fetchActivitiesCreatedByMember(bvtActivitiesCommon, actOwner)
   print ""

   print " Activities Unsorted as returned by Activities Server..."
   print " Activities Created by Member: " + activityOwner
   print " -------------------------------------------------------------------------------------------"
   count = BvtActivitiesCommon.listAllActivities(bvtActivitiesCommon, activities)
   print "%d Activities retrieved." % count

   #Sort the returned activities
   strUtil = com.ibm.janet.core.StringUtils(Boolean.TRUE)
   sortedActivities = strUtil.sortHashTableVectorByKey( activities, activitySortKey, 0)
   print "Activities Sorted Ascending Alphabetically by Key: " + activitySortKey + " ..."
   BvtActivitiesCommon.listAllActivities(bvtActivitiesCommon, sortedActivities)               

   #Delete the fetched Activities that were created by the specified member
   print "Deleting Sorted Activities Created by User: " + activityOwner + " ..."
   print ""
   ActivityService.deleteActivities(sortedActivities)
   
   #Purge Trash so no deleted activities remain on the server
   print "Purging all Trash on the Server..."
   print ""
   trash=TrashCollectionService.fetchTrash()
   TrashCollectionService.purgeTrash(trash)
   
   #Verify that there are no activities created by the specified user which remain on the system
   print ""
   print "Fetching Activities created by Member: " + activityOwner + " with ACL Entries ..."
   print ""
   activities = BvtActivitiesCommon.fetchActivitiesCreatedByMember(bvtActivitiesCommon, actOwner)
   print ""

   if activities.isEmpty() <= 0:
      errorMessage = "ERROR: One or more activities could not be removed from the system!"
      BvtActivitiesCommon.listAllActivities(bvtActivitiesCommon, activities)               
      if fErrorFile != None:
         print >> fErrorFile, errorMessage
         raise errorMessage
      else:
         raise errorMessage
   else:
      print "SUCCESS: All Activities created by User: " + activityOwner + " have been removed from the system!"

   flags = Hashtable()
   flags.put("fetchAccess", "true")
   BvtActivitiesCommon.writeActivitiesToFile(bvtActivitiesCommon, RUNFILE, activities, flags)

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
print "BVT Delete All Activities By Creator Test ... starting."

#-------------------------------------------------
# Main  - execute the Activity test function
#-------------------------------------------------
print "BVT Delete All Activities By Creator Test ... calling BvtDeleteAllActivitiesByCreator()"
if DEBUG_SCRIPT != None:
   print "WARNING: Script is in DEBUG MODE!  Set DEBUG_SCRIPT variable (at top of script) to None to disable debug mode!"
   BvtDeleteAllActivitiesByCreator()
   global fErrorFile
   fErrorFile.close()
   os.unlink(ERRORFILE)
else:	
   try:
      BvtDeleteAllActivitiesByCreator()
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

