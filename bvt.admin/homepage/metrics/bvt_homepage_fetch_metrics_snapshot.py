#  bvt_homepage_fetch_metrics_snapshot.py
#
#  Jython Script to test some homepage commands on IBM homepage Enterprise Application
#  (C) Copyright IBM Corporation, 2008
#
#  DESCRIPTION:
#  This script takes a snapshot of homepage metrics and writes it to the file:
#  homepageMetricsSnapshot.txt.  The purpose of the snapshot file is to allow the 
#  test automation to determine the baseline metrics before a metrics test begins
#  The values contained in the snapshot file will be used to determine if test-generated
#  activity contributes to the correct metrics values when processed by the test validation.
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
# INITIAL RELEASE:  LC2.0
# TEST TARGETS:     HomepageMetricsService.fetchMetrics()

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

execfile('homepageAdmin.py') # only do this once
execfile('bvt_common.py')
execfile('bvt_metrics_common.py')
execfile('ListServiceModule.py')

#Globals
UNIQUE_SCRIPT_ID = "bvt_homepage_fetch_metrics_snapshot"
RUNFILE = UNIQUE_SCRIPT_ID + ".run"
GOLDFILE = UNIQUE_SCRIPT_ID + ".gld"
ERRORFILE = UNIQUE_SCRIPT_ID + ".err"
PROPFILE = UNIQUE_SCRIPT_ID + ".properties"
SNAPSHOTFILE = "homepageMetricsSnapshot.txt"
global fErrorFile
#Set the following to "true" to set debug mode and allow detailed stack tracing
#Set the following to None to disable debug mode and allow standard error handling
DEBUG_SCRIPT = None

def BvtHomepageFetchMetricsSnapshot():
   print ""
   print "--------------------------------------------------------------------------------"
   print " BVT Test Script for Producing an Homepage Metrics Snapshot at the time this script is invoked "
   print " (C) Copyright IBM Corporation, 2008 "
   print "--------------------------------------------------------------------------------"

   #Create error file for logging any errors.  This file will be removed later if no errors occurred
   #This needs to be done at the start of every script. Use the script name as part of the filename
   global fErrorFile
   fErrorFile = open(ERRORFILE, "w+")

   bvtCommon = BvtCommon()
   bvtMetricsCommon = BvtMetricsCommon()
   inited = BvtMetricsCommon.initLibrary(bvtMetricsCommon, None)
   if (inited != 0):
      return

   print ""
   print "Fetching Homepage Metrics Snapshot . . ."
   homepageMetricsSnapshot = HomepageMetricsService.fetchMetrics()   
   print ""

   print ""
   print "Homepage Metrics Snapshot Data . . ."
   bvtMetricsCommon.listAllMetrics(homepageMetricsSnapshot)
   print ""
   
   print ""
   print "Writing Homepage Metrics Snapshot Data to snapshot file: " + SNAPSHOTFILE + " . . ."
   bvtMetricsCommon.writeMetricsToFile(SNAPSHOTFILE, homepageMetricsSnapshot, None)
   print ""
   
   return

# =========================================================

#global BvtCommon
global bvtMetricsCommon

bvtMetricsCommon=BvtMetricsCommon()
print "BVT BvtHomepageFetchMetricsSnapshot ... starting."

#-------------------------------------------------
# Main  - just execute the BvtHomepageFetchMetricsSnapshot test function
#-------------------------------------------------
print "BVT BvtHomepageFetchMetricsSnapshot ... calling BvtHomepageFetchMetricsSnapshot()"
if DEBUG_SCRIPT != None:
   print "WARNING: Script is in DEBUG MODE!  Set DEBUG_SCRIPT variable (at top of script) to None to disable debug mode!"
   BvtHomepageFetchMetricsSnapshot()
   global fErrorFile
   fErrorFile.close()
   os.unlink(ERRORFILE)
else:	
   try:
      BvtHomepageFetchMetricsSnapshot()
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

