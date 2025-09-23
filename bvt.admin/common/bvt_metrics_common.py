#  This is file:  bvt_metrics_common.py
#
#  Jython Script library for the Activity service running on IBM Activities Enterprise Application
#  (C) Copyright IBM Corporation, 2007
#
#  This script can be run from a .bat from the Windows command prompt, or a .sh on Linux shell.
#

import sys, java
from types import *
from java import lang
from java import util
from java.util import *
from java import io

#from bvt_common import *

#execfile('bvt_common.py')

#import bvt_common

class BvtMetricsCommon(BvtCommon):
   def __call__ (self, *args):
       return BvtMetricsCommon(self, args)

   def initLibrary(self, logging):
      bvtCommon = BvtCommon()
      inited = BvtCommon.initLibrary(bvtCommon, logging)
      return inited

   # Print all metrics in the hashtable to console, one metric per line
   def listAllMetrics(self, metricHashmap):
    	# Iterate over Hashmap of Metrics and print it from start to finish
    	if metricHashmap != None:
    	    if metricHashmap.size() > 0:
		keys = metricHashmap.keySet()
    	    	keysIterator = keys.iterator()
    	    	while keysIterator.hasNext():
		   key = keysIterator.next()
		   self.listOneMetric( metricHashmap, key)
	return

   # Print one metric to console
   def listOneMetric(self, metricHashmap, key):
    	if metricHashmap != None:
    	    if metricHashmap.size() > 0:
    	    	if key != None:
    	    	   print key + "=%s" % metricHashmap.get(key)
	return
 

   # Write the metrics in the hashtable to a file, one metric per line
   def writeMetricsToFile(self, fileName, metricHashmap, flags):
       f = None
       if flags != None:
          append = flags.get("append")
          if( append != None):
             if( append.lower() == "true"):
                f = open(fileName, "a")
             else:
                f = open(fileName, "w")
          else:
             f = open(fileName, "w")
       else:
          f = open(fileName, "w")
          
       try:
           metricsList = []
           keys = metricHashmap.keySet()
           keysIterator = keys.iterator()
           while keysIterator.hasNext():
                key = keysIterator.next()
           	metricsList.append( key + "=%s\n" % metricHashmap.get(key))
	   for metric in metricsList:
              f.write( metric) # note - no CR/LF on write (only on print)
       finally:
          f.flush()
          f.close()
       return

   # Get metrics keys starting with "keyPrefix" from "hashtable"
   # Return a hashtable of only those metrics keys in the table that begin with the key prefix
   def getMetricsKeysFromHashtable(self, hashtable, keyPrefix):
	print "Retrieving Metrics Keys starting with Key Prefix: " + keyPrefix + " ..."
	metricsKeys = Hashtable()
	enumKeys = hashtable.keys()
	while enumKeys.hasMoreElements():
	   key = enumKeys.nextElement()
	   if key.startswith(keyPrefix):
		metricsKeys.put(key, hashtable.get(key))
	return metricsKeys

   # Get metrics keys starting with "keyPrefix" from "hashmap"
   # Return a (sorted) list of only those metrics keys in the table that begin with the key prefix
   def getMetricsKeysFromHashmap(self, hashmap, keyPrefix):
	print "Retrieving Metrics Keys starting with Key Prefix: " + keyPrefix + " ..."
	metricsKeys = []
        keys = hashmap.keySet()
        keysIterator = keys.iterator()
        while keysIterator.hasNext():
	   key = keysIterator.next()
	   if key.startswith(keyPrefix):
              metricsKeys.append(hashmap.get(key))
	metricsKeys.sort()
	return metricsKeys

   # Check Metrics Values for -1 which indicates an error retrieving the metric from the DB
   # Return a list of metrics that match this condition
   def checkMetricsForErrors(self, hashmap):
	print "Checking Metrics for error conditions ..."
	metricsErrors = []
        keys = hashmap.keySet()
        keysIterator = keys.iterator()
        while keysIterator.hasNext():
	   key = keysIterator.next()
	   value = hashmap.get(key)
	   if value < 0:
	      print "ERROR: Metric: %s contains error condition: %s" % (key, value)
              metricsErrors.append("ERROR: %s = %s") % (key, value)
	return metricsErrors

       
       

