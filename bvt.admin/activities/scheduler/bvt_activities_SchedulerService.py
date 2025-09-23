#  This is bvt_activities_SchedulerService.py
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
# TEST TARGETS:     Scheduler.listJobs(schedName); Scheduler.getJobDetails(schedName, jobName); 
#                   Scheduler.pauseJob(schedName); and Scheduler.resumeJob(schedName)

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
execfile('bvt_scheduler_common.py')
execfile('ListServiceModule.py')

#Globals
UNIQUE_SCRIPT_ID = "bvt_activities_SchedulerService"
RUNFILE = UNIQUE_SCRIPT_ID + ".run"
GOLDFILE = UNIQUE_SCRIPT_ID + ".gld"
ERRORFILE = UNIQUE_SCRIPT_ID + ".err"
PROPFILE = UNIQUE_SCRIPT_ID + ".properties"
global fErrorFile
#Set the following to "true" to set debug mode and allow detailed stack tracing
#Set the following to None to disable debug mode and allow standard error handling
DEBUG_SCRIPT = None

def BvtActivitiesSchedulerService():
   print ""
   print "--------------------------------------------------------------------------------"
   print " BVT Test Script that will test the various SchedulerService MBean commands"
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

   bvtSchedulerCommon = BvtSchedulerCommon()
   inited = BvtSchedulerCommon.initLibrary(bvtSchedulerCommon, logging)
   if (inited != 0):
      return

   # retrieve this script's property settings
   strMakeGoldLog = BvtCommon.getProperty(bvtCommon, configProps, "make_gold_log")
   strMakeGoldLog.lower()
   if strMakeGoldLog == "true":
      bMakeGoldLog = Boolean(Boolean.TRUE)
   else:
      bMakeGoldLog = Boolean(Boolean.FALSE)

#Get the activity sort key - commented out - because not using it in this test document
#   activitySortKey = BvtCommon.getProperty(bvtCommon, configProps, "activity_sort_key")


   print " *******************************************"
   print "Scheduler.listJobs(local) should list 2 jobs (30MinStats & HourlyStats)..."
   localjobs = Scheduler.listJobs("local")
   print ""
   
   count=BvtSchedulerCommon.listStringArray(bvtSchedulerCommon, localjobs)
   print "%d Total Jobs Scheduled on the LOCAL Scheduler:" % count
 
   # Will now send the results to the run log file
   flags = Hashtable()
   flags.put("append", "false")
   BvtSchedulerCommon.writeStringArrayToFile(bvtSchedulerCommon, RUNFILE, localjobs, flags)


   print " ******************************************"
   print "Scheduler.listJobs(cluster) should list 3 jobs..."
   print "(DatabaseRuntimeStats, ActivityAutoCompleteJob, and TrashAutoPurgeJob)..."
   clusterjobs = Scheduler.listJobs("cluster")
   print ""
   
   count=BvtSchedulerCommon.listStringArray(bvtSchedulerCommon, clusterjobs)
   print "%d Total Jobs Scheduled on the CLUSTER Scheduler:" % count
      
   # Will now send the results to the run log file; appending to the file
   flags = Hashtable()
   flags.put("append", "true")
   # writing results to run log; not appending
   BvtSchedulerCommon.writeStringArrayToFile(bvtSchedulerCommon, RUNFILE, clusterjobs, flags)

   
   print " ******************************************"
   print "Scheduler.getJobDetails(local, 30MinStats) should list detailed info for this job..."
   thirtyMin= Scheduler.getJobDetails("local", "30MinStats")
   print ""
   #  Print the job details to the screen
   BvtSchedulerCommon.listOneJobHashtable(bvtSchedulerCommon, thirtyMin)
    
   # Will now send the results to the run log file; appending to file
   flags = Hashtable()
   flags.put("append", "true")
   BvtSchedulerCommon.writeOneJobHashtableToFile(bvtSchedulerCommon, RUNFILE, thirtyMin, flags)

   print " *******************************************"
   print "Scheduler.getJobDetails(cluster, TrashAutoPurgeJob) should list detailed info for this job..."
   trash = Scheduler.getJobDetails("cluster", "TrashAutoPurgeJob")
   print ""
   #  Print the job details to the screen
   BvtSchedulerCommon.listOneJobHashtable(bvtSchedulerCommon, trash)
    
   # Will now send the results to the run log file; appending
   flags = Hashtable()
   flags.put("append", "true")
   BvtSchedulerCommon.writeOneJobHashtableToFile(bvtSchedulerCommon, RUNFILE, trash, flags)


   print " *******************************************"
   print "Scheduler.pauseJob(cluster, TrashAutoPurgeJob) should pause this job..."
   pause = Scheduler.pauseJob("cluster", "TrashAutoPurgeJob")
   print "This is what is returned from the command"
   print pause
       
   # Will now send the results to the run log file
   flags = Hashtable()
   flags.put("append", "true")
   BvtSchedulerCommon.writeStringToFile(bvtSchedulerCommon, RUNFILE, pause, flags)


   print " *******************************************"
   print "Scheduler.getJobDetails(cluster, TrashAutoPurgeJob) should now show that" 
   print "the job has been paused."
   trashpaused = Scheduler.getJobDetails("cluster", "TrashAutoPurgeJob")
   print ""
   #  Print the job details to the screen
   BvtSchedulerCommon.listOneJobHashtable(bvtSchedulerCommon, trashpaused)
    
   # Will now send the results to the run log file
   flags = Hashtable()
   flags.put("append", "true")
   BvtSchedulerCommon.writeOneJobHashtableToFile(bvtSchedulerCommon, RUNFILE, trashpaused, flags)
       
   print " *******************************************"
   print "Will now resume the paused job: Scheduler.resumeJob(cluster, TrashAutoPurgeJob)"
   resume = Scheduler.resumeJob("cluster", "TrashAutoPurgeJob")
   print "This is what is returned from the command"
   print resume
       
   # Will now send the results to the run log file
   flags = Hashtable()
   flags.put("append", "true")
   BvtSchedulerCommon.writeStringToFile(bvtSchedulerCommon, RUNFILE, resume, flags)


   print " *******************************************"
   print "Do Scheduler.getJobDetails again on the resumed job. Should once again show the" 
   print "the details about the TrashAutoPurgeJob."
   trashresumed = Scheduler.getJobDetails("cluster", "TrashAutoPurgeJob")
   print ""
   #  Print the job details to the screen
   BvtSchedulerCommon.listOneJobHashtable(bvtSchedulerCommon, trashresumed)
    
   # Will now send the results to the run log file
   flags = Hashtable()
   flags.put("append", "true")
   BvtSchedulerCommon.writeOneJobHashtableToFile(bvtSchedulerCommon, RUNFILE, trashresumed, flags)
       


   #Compare the test run results with the Gold File Results
   #lc.compareLines( ",", RUNFILE, GOLDFILE, None)
   lc = com.ibm.janet.core.LineCompare(Boolean.TRUE, loadedProps, bMakeGoldLog)
   lc.compareLines( "::", RUNFILE, GOLDFILE)
   
   print ""
   return

# =========================================================

#global BvtCommon
global bvtSchedulerCommon

bvtSchedulerCommon=BvtSchedulerCommon()
print "BVT testing of SchedulerService commands ... starting."

#-------------------------------------------------
# Main  - just execute the SchedulerService test function
#-------------------------------------------------
print "BVT SchedulerService Test ... calling BvtActivitiesSchedulerService()"
if DEBUG_SCRIPT != None:
   print "WARNING: Script is in DEBUG MODE!  Set DEBUG_SCRIPT variable (at top of script) to None to disable debug mode!"
   BvtActivitiesSchedulerService()
   global fErrorFile
   fErrorFile.close()
   os.unlink(ERRORFILE)
else:	
   try:
      BvtActivitiesSchedulerService()
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

