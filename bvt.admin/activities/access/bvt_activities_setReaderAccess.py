#  This is file bvt_activities_setReaderAccess.py
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
# TEST TARGETS:     Run the AccessControlService.setReaderAccess mbean command

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
UNIQUE_SCRIPT_ID = "bvt_activities_setReaderAccess"
RUNFILE = UNIQUE_SCRIPT_ID + ".run"
GOLDFILE = UNIQUE_SCRIPT_ID + ".gld"
ERRORFILE = UNIQUE_SCRIPT_ID + ".err"
PROPFILE = UNIQUE_SCRIPT_ID + ".properties"
global fErrorFile
#Set the following to "true" to set debug mode and allow detailed stack tracing
#Set the following to None to disable debug mode and allow standard error handling
DEBUG_SCRIPT = None

def BvtSetReaderAccess():
   print ""
   print "--------------------------------------------------------------------------------"
   print " BVT Test Script for AccessControlService.setReaderAccess(activity)MBean command."
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

   # Get the activity sort key
   activitySortKey = BvtCommon.getProperty(bvtCommon, configProps, "activity_sort_key")


   print ""
   print "Issuing the command MemberService.fetchMemberByName('Amy Jones70')..."
   print "Will use the results of this command as input for fetchActivitiesCreatedByMember command..."
   aj70=MemberService.fetchMemberByName("Amy Jones70")
   print "Now fetching all activities created by Amy Jones70.  Should be 2 of them"
   aj70act=ActivityService.fetchActivitiesCreatedByMember(aj70)
   
   
#  Sort the returned activities
   strUtil = com.ibm.janet.core.StringUtils(Boolean.TRUE)
   aj70actsorted = strUtil.sortHashTableVectorByKey( aj70act, activitySortKey, 0)
   
   print "Activities Sorted Ascending Alphabetically by Key: " + activitySortKey + " ..."
   count = BvtActivitiesCommon.listAllActivities(bvtActivitiesCommon, aj70actsorted)               
   
   print "%d Activities retrieved." % count

   # the following will take the 2 activities returned in prev cmd & assign to hashtable entries
   aj70hash0=aj70actsorted[0]
   aj70hash1=aj70actsorted[1]


   print "This command will list the access list for the 1st Activity, which should be..."
   print "Amy Jones70=owner, Amy Jones71=author, Amy Jones72=reader"
   act1=AccessControlService.fetchAccess(aj70hash0)

   count = BvtActivitiesCommon.listAccessList(bvtActivitiesCommon, act1)
   print "%d Access List entries retrieved." % count

   flags = Hashtable()
   flags.put("append", "false")
   print "Now writing the access list for 1st activity to run log file..."
   BvtActivitiesCommon.writeAccessListToFile(bvtActivitiesCommon, RUNFILE, act1, flags)

   

   print "This command will list the access list for the 2nd Activity, which should be..."
   print "Amy Jones70=owner, Amy Jones73=author"
   act2=AccessControlService.fetchAccess(aj70hash1)

   count = BvtActivitiesCommon.listAccessList(bvtActivitiesCommon, act2)
   print "%d Access List entries retrieved." % count


   flags = Hashtable()
   flags.put("append", "true")
   print "Now writing the access list for 2nd activity to run log file..."
   BvtActivitiesCommon.writeAccessListToFile(bvtActivitiesCommon, RUNFILE, act2, flags)
   

   print "We will now fetch info about member Amy Jones20, so that we can add her to the access list..."
   aj20=MemberService.fetchMembers("amy jones20")
   
   print "Now adding Amy Jones20 as a Reader to the access list of the 2 activities created by AJ70..."
   print "Using the AccessControlService.setReaderAccess mbean command to do this."
   AccessControlService.setReaderAccess(aj70act, aj20)

   print "Will now confirm that the prev cmd worked, will list out the access list of those 2 activities..."
   print "This command will list the updated ACL for the 1st Activity, which should be..."
   print "Amy Jones70=owner, Amy Jones71=author, Amy Jones72 and Amy Jones20=reader"
   act1updated=AccessControlService.fetchAccess(aj70hash0)

   count = BvtActivitiesCommon.listAccessList(bvtActivitiesCommon, act1updated)
   print "%d Access List entries retrieved." % count

   flags = Hashtable()
   flags.put("append", "true")
   print "Now writing the updated access list for 1st activity to run log file..."
   BvtActivitiesCommon.writeAccessListToFile(bvtActivitiesCommon, RUNFILE, act1updated, flags)   

   

   print "This command will list the updated ACL for the 2nd Activity, which should be..."
   print "Amy Jones70=owner, Amy Jones73=author, Amy Jones20=reader"
   act2updated=AccessControlService.fetchAccess(aj70hash1)

   count = BvtActivitiesCommon.listAccessList(bvtActivitiesCommon, act2updated)
   print "%d Access List entries retrieved." % count

   print "Now writing the access list for 2nd activity to run log file..."
   flags = Hashtable()
   flags.put("append", "true")
   BvtActivitiesCommon.writeAccessListToFile(bvtActivitiesCommon, RUNFILE, act2updated, flags)

   # CLEAN UP!  Will now remove user Amy Jones20 from the access list as part of cleaning up.
   print ""
   print "Removing Amy Jones20 from access lists - as part of clean up!"
   AccessControlService.deleteAccess(aj70act, aj20)
   print "Listing activity1's access list - Amy Jones should no longer appear."
   removedact1=AccessControlService.fetchAccess(aj70hash0)
   BvtActivitiesCommon.listAccessList(bvtActivitiesCommon, removedact1)
   print "Listing activity2's access list - Amy Jones should no longer appear."
   removedact2=AccessControlService.fetchAccess(aj70hash1)
   BvtActivitiesCommon.listAccessList(bvtActivitiesCommon, removedact2)

   #Compare the test run results with the Gold File Results
   lc = com.ibm.janet.core.LineCompare(Boolean.TRUE, loadedProps, bMakeGoldLog)
   lc.compareLines( "::", RUNFILE, GOLDFILE)
   
   print ""
   return

# =========================================================

#global BvtCommon
global bvtActivitiesCommon

bvtActivitiesCommon=BvtActivitiesCommon()
print "BVT setReaderAccess List test ... starting."

#-------------------------------------------------
# Main  - just execute the Activity test function
#-------------------------------------------------
print "BVT setReaderAccess List Test ... calling BvtSetReaderAccess()"
if DEBUG_SCRIPT != None:
   print "WARNING: Script is in DEBUG MODE!  Set DEBUG_SCRIPT variable (at top of script) to None to disable debug mode!"
   BvtSetReaderAccess()
   global fErrorFile
   fErrorFile.close()
   os.unlink(ERRORFILE)
else:	
   try:
      BvtSetReaderAccess()
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
