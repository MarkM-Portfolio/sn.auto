#  bvt_blogs_fetch_metrics.py
#
#  Jython Script to test some Blogs Metrics on IBM Blogs Enterprise Application
#  (C) Copyright IBM Corporation, 2008
#
#  DESCRIPTION:
#  This script retrieves all metrics associated with blogs on the target server.
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
# TEST TARGETS:     BlogsMetricsService.fetchMetrics()

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

execfile('blogsAdmin.py') # only do this once
execfile('bvt_common.py')
execfile('bvt_metrics_common.py')
execfile('ListServiceModule.py')

#Globals
UNIQUE_SCRIPT_ID = "bvt_blogs_fetch_metrics"
RUNFILE = UNIQUE_SCRIPT_ID + ".run"
GOLDFILE = UNIQUE_SCRIPT_ID + ".gld"
ERRORFILE = UNIQUE_SCRIPT_ID + ".err"
PROPFILE = UNIQUE_SCRIPT_ID + ".properties"
SNAPSHOTFILE = "blogsMetricsSnapshot.txt"
METRIC_NAME_PREFIX = "metric.keyname"
METRIC_UPDATED_NAME_PREFIX = "blogs.metric."

global fErrorFile
failuresList = []

#Set the following to "true" to set debug mode and allow detailed stack tracing
#Set the following to None to disable debug mode and allow standard error handling
DEBUG_SCRIPT = None

def BvtBlogsFetchMetrics():
   print ""
   print "--------------------------------------------------------------------------------"
   print " BVT Test Script for fetching all Blogs Metrics using the BlogsMetricsService MBean"
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

   bvtMetricsCommon = BvtMetricsCommon()
   inited = bvtMetricsCommon.initLibrary(logging)
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
   print "Setting Metrics Cache Interval to zero - Metrics information will be 'live' after each fetch"
   BlogsMetricsService.saveMetricToFile("", 0, "metrics.cache.timeout.in.minutes")

   # Read in the metrics names for this feature from our properties file
   metricsKeys = bvtMetricsCommon.getMetricsKeysFromHashtable(loadedProps, METRIC_NAME_PREFIX)

   #identifier = key[key.find(METRIC_UPDATED_NAME_PREFIX)+1:]

   # Read in the metrics value baseline updates for this feature from our properties file
   metricsUpdateKeys = bvtMetricsCommon.getMetricsKeysFromHashtable(loadedProps, METRIC_UPDATED_NAME_PREFIX)

   # The Number of Keys specified in props file for Metrics and Metric Update Values MUST BE THE SAME
   if( metricsKeys.size() != metricsUpdateKeys.size() ):
	raise "ERROR: The total number of Metrics Keys: %s does not match the total number of Metrics update values: %s  specified in properties File!" % (metricsKeys.size(), metricsUpdateKeys.size() )

   # Store The Baseline Metrics Values in a hashtable using the name of the metric for the keyname
   htBaselineMetrics = Hashtable()
   enumKeys = metricsUpdateKeys.keys()
   while enumKeys.hasMoreElements():
      key = enumKeys.nextElement()
      htBaselineMetrics.put( key, long(metricsUpdateKeys.get(key)))
      print "DEBUG: Assigned Baseline value of: %s for Metric: %s to Blogs Metrics Baseline hashtable..." % (metricsUpdateKeys.get(key), key)
   
   print ""
   print "Retrieving Current Blogs Metrics Snapshot from Snapshot File: " + SNAPSHOTFILE + " ..."
   fSnapshotFile = open(SNAPSHOTFILE, "r")
   metricSnapshotList = fSnapshotFile.readlines()
   htMetricSnapshot = Hashtable()
   
   for metric in metricSnapshotList:
   	key = metric[:metric.find("=")]
   	value = metric[metric.find("=")+1:]
	value = value.strip()
	print "Adding Metric: " + key + " with value: " + value + " to Blogs Metrics snapshot hashtable..."
   	if value.find(".") > 0:
   	   htMetricSnapshot.put(key, float(value))
	else:
   	   htMetricSnapshot.put(key, long(value))

   print ""
   print "Fetching Current Blogs Metrics on Target Server..."
   blogsMetrics = BlogsMetricsService.fetchMetrics()   
   print ""
   
   print ""
   print "Fetched the following current Blogs Metrics Data . . ."
   bvtMetricsCommon.listAllMetrics(blogsMetrics)
   print ""

   print ""
   print "Checking Retrieved Metrics For Errors . . ."
   metricsErrors = bvtMetricsCommon.checkMetricsForErrors(blogsMetrics)
   print ""

   # If any of the metrics had errors then raise an exception
   errorString = "ERROR: The following metrics had errors:\n\n"
   if( len(metricsErrors) > 0 ):
   	for error in metricsErrors:
   	   errorString = errorString + error + "\n"
	raise errorString
   
   # If the size of the fetched Metrics entries does not equal the size of the fetched snapshot metrics then fail
   if( htMetricSnapshot.size() != blogsMetrics.size() ):
	raise "ERROR: Metrics Snapshot Size: %d does not equal Fetch Metrics Size: %d" % ( htMetricSnapshot.size(), blogsMetrics.size() ) 

   # If the size of the fetched Metrics entries does not equal the size of our metrics entries specified in the props file then fail
   if( len(metricsKeys) != blogsMetrics.size() ):
	raise "ERROR: Metrics Key Size: %d read in from properties file does not equal Fetch Metrics Size: %d" % ( metricsKeys.size(), blogsMetrics.size() ) 
   
   # Compare the current metrics data to the snapshot taken before our test data was created
   # When we add the snapshot data to the baseline data (the metrics data we expect as a result of our test data,
   # then the total should equal the current metrics data. This verification method implies that NO external
   # data was generated outside of the scope of our testing between the time that this test started up
   # to the time this test ended.  Otherwise, results may be skewed/innacurate and this test will treat that
   # situation as a test failure.

   # Add the snapshot (pre-test) value to the baseline (test-generated) value
   htExpectedMetricsData = Hashtable()
   keys = blogsMetrics.keySet()
   keysIterator = keys.iterator()
   while keysIterator.hasNext():
      key = keysIterator.next()
      snapshotValue = str(htMetricSnapshot.get(key))
      baselineValue = str(htBaselineMetrics.get(key))
      if snapshotValue.find(".") > 0:
         #org.python.core.PyFloat
         htExpectedMetricsData.put(key, (float(baselineValue) + float(snapshotValue)) )
      else:
         htExpectedMetricsData.put(key, (long(baselineValue) + long(snapshotValue)) )
   
   # Now compare the actual results to expected results
   keys = blogsMetrics.keySet()
   keysIterator = keys.iterator()
   while keysIterator.hasNext():
      key = keysIterator.next()
      value = blogsMetrics.get(key)
      if type(value).__name__ == 'org.python.core.PyFloat':
         actualResult = float(blogsMetrics.get(key))
         expectedResult = float(htExpectedMetricsData.get(key))
         if actualResult != expectedResult:
            failuresList.append("ERROR: Metric: " + key + " - Actual Metrics Data was: %f but Expected Metrics Data was: %f\n" % (actualResult,expectedResult) )
      else:   
         actualResult = blogsMetrics.get(key)
         expectedResult = htExpectedMetricsData.get(key).longValue()
         if actualResult != expectedResult:
            failuresList.append("ERROR: Metric: " + key + " - Actual Metrics Data was: %s but Expected Metrics Data was: %s\n" % (actualResult,expectedResult) )
   return

# =========================================================

#global BvtCommon
global bvtMetricsCommon

bvtMetricsCommon=BvtMetricsCommon()
print "BVT BvtBlogsFetchMetrics tests ... starting."

#-------------------------------------------------
# Main  - just execute the BvtBlogsFetchMetrics test function
#-------------------------------------------------
print "BVT BvtBlogsFetchMetrics Test ... calling BvtBlogsStatisticsService()"
if DEBUG_SCRIPT != None:
   print "WARNING: Script is in DEBUG MODE!  Set DEBUG_SCRIPT variable (at top of script) to None to disable debug mode!"
   BvtBlogsFetchMetrics()
   global fErrorFile
   fErrorFile.close()
   os.unlink(ERRORFILE)
else:	
   try:
      BvtBlogsFetchMetrics()
   except:
      global fErrorFile
      print '*****************************************************************'
      print 'FAIL FAIL FAIL FAIL FAIL FAIL FAIL FAIL FAIL FAIL FAIL FAIL FAIL'
      print '*****************************************************************'
      traceback.print_exception(sys.exc_info()[0], sys.exc_info()[1], sys.exc_info()[2])
      traceback.print_exception(sys.exc_info()[0], sys.exc_info()[1], sys.exc_info()[2], limit = None, file = fErrorFile)
      fErrorFile.close()
      print ""
      print "Setting Metrics Cache Interval to 60 seconds (default)"
      BlogsMetricsService.saveMetricToFile("", 60, "metrics.cache.timeout.in.minutes")
   else:
      global fErrorFile
      if( len(failuresList) > 0):
        print '*****************************************************************'
        print 'FAIL FAIL FAIL FAIL FAIL FAIL FAIL FAIL FAIL FAIL FAIL FAIL FAIL'
        print '*****************************************************************'
	print ""
      	for failure in failuresList:
           print failure
      	   fErrorFile.write(failure)
	fErrorFile.close()
      else:
        print '*****************************************************************'
        print 'PASS PASS PASS PASS PASS PASS PASS PASS PASS PASS PASS PASS PASS'
        print '*****************************************************************'
        print ""
	fErrorFile.close()
      	os.unlink(ERRORFILE)
      print ""
      print "Setting Metrics Cache Interval to 60 seconds (default)"
      BlogsMetricsService.saveMetricToFile("", 60, "metrics.cache.timeout.in.minutes")

