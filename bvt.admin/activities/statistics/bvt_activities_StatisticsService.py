#  This is bvt_activities_StatisticsService.py
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
# TEST TARGETS:     StatisticsService.fetchStatistic(statname)

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
execfile('bvt_stats_common.py')
execfile('ListServiceModule.py')

#Globals
UNIQUE_SCRIPT_ID = "bvt_activities_StatisticsService"
RUNFILE = UNIQUE_SCRIPT_ID + ".run"
GOLDFILE = UNIQUE_SCRIPT_ID + ".gld"
ERRORFILE = UNIQUE_SCRIPT_ID + ".err"
PROPFILE = UNIQUE_SCRIPT_ID + ".properties"
global fErrorFile
#Set the following to "true" to set debug mode and allow detailed stack tracing
#Set the following to None to disable debug mode and allow standard error handling
DEBUG_SCRIPT = None

def BvtActivitiesStatisticsService():
   print ""
   print "--------------------------------------------------------------------------------"
   print " BVT Test Script for fetching a single Statistic using the StatisticsService MBean"
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

   bvtStatsCommon = BvtStatsCommon()
   inited = BvtStatsCommon.initLibrary(bvtStatsCommon, logging)
   if (inited != 0):
      return

   # retrieve this script's property settings
   strMakeGoldLog = BvtCommon.getProperty(bvtCommon, configProps, "make_gold_log")
   strMakeGoldLog.lower()
   if strMakeGoldLog == "true":
      bMakeGoldLog = Boolean(Boolean.TRUE)
   else:
      bMakeGoldLog = Boolean(Boolean.FALSE)

   print ""
   print "StatisticsService.fetchStatistic(statname) cmd will fetch the info about this single stat..."
   print "Should return the following stats: invocations, total, min, max, & average."
   onestat = StatisticsService.fetchStatistic("activities.service.db.totals")   
   print ""
   
   BvtStatsCommon.listOneStat(bvtStatsCommon, onestat)
   
   
   # Will now send the results to the run log file; not appending to file
   flags = Hashtable()
   flags.put("append", "false")
   BvtStatsCommon.writeOneStatToFile(bvtStatsCommon, RUNFILE, onestat, flags)

#   print ""
#   print "This command will fetch all stats: StatisticsService.fetchStatistics()for Activities..."
#   print "Should return loads of stats."
#   allstat = StatisticsService.fetchStatistics()   
   print ""
   
#   count = BvtStatsCommon.listAllStats(bvtStatsCommon, allstat)
#   print "%d Statistics retrieved." % count
   
   # NEED TO WORK ON THIS  
   # Will now send the results to the run log file
   #BvtStatsCommon.writeStatsToFile(bvtStatsCommon, RUNFILE, onestat)

   

   #Compare the test run results with the Gold File Results
   #lc.compareLines( ",", RUNFILE, GOLDFILE, None)
   lc = com.ibm.janet.core.LineCompare(Boolean.TRUE, loadedProps, bMakeGoldLog)
   lc.compareLines( "::", RUNFILE, GOLDFILE)
   
   print ""
   return

# =========================================================

#global BvtCommon
global bvtStatsCommon

bvtStatsCommon=BvtStatsCommon()
print "BVT StatisticsService tests ... starting."

#-------------------------------------------------
# Main  - just execute the StatisticsService test function
#-------------------------------------------------
print "BVT StatisticsService Test ... calling BvtActivitiesStatisticsService()"
if DEBUG_SCRIPT != None:
   print "WARNING: Script is in DEBUG MODE!  Set DEBUG_SCRIPT variable (at top of script) to None to disable debug mode!"
   BvtActivitiesStatisticsService()
   global fErrorFile
   fErrorFile.close()
   os.unlink(ERRORFILE)
else:	
   try:
      BvtActivitiesStatisticsService()
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

