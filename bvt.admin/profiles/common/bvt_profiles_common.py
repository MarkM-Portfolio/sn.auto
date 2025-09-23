#  This is file:  bvt_profiles_common.py
#
#  Jython Script library for the Profiles service running on IBM Profiles Enterprise Application
#  (C) Copyright IBM Corporation, 2009
#
#
# AUTHOR:                yees@us.ibm.com
# INITIAL RELEASE:  LC2.5
# COMMENTS:            Support routines to prepare data set and process results.

import sys, java
from types import *
from java.lang import Integer
from java import util
from java import io


class BvtProfilesCommon(BvtCommon):
   def __call__ (self, *args):
       return BvtProfilesCommon(self, args)

   def initLibrary(self, logging, configProps):
      bvtCommon = BvtCommon()
      inited = BvtCommon.initLibrary(bvtCommon, logging)
      return inited

   # This method loads a set of value pairs into a hashtable.  The values to load are based on the property names given as parameters. 
   # Input: propLable - property prefix, propName1 - key, propName2 - value
   # Output: Hashtable with properties loaded.
   def loadPropSetsIntoHashTable(self, propLable, propName1, propName2):
      bvtCommon = BvtCommon()
      configProps = BvtCommon.loadProperties(bvtCommon, PROPFILE)

      resultTable = Hashtable()
      # sizeProp = value of descTest.size for example.
      sizeProp = Integer.valueOf(BvtCommon.getProperty(bvtCommon, configProps, propLable + ".size"))
      
      for i in range(sizeProp):
          # propKey = value of descTest.descTestUser1 for example.
          propKey = propLable + "." + propName1 + String.valueOf(i + 1)
          propValue = propLable + "." + propName2 + String.valueOf(i + 1) 
          key = String(BvtCommon.getProperty(bvtCommon, configProps, propKey)).trim()
          value = String(BvtCommon.getProperty(bvtCommon, configProps, propValue)).trim()
          resultTable.put(key, value)

      return resultTable


   # This method loads a set of 2 values into a 2d vector.  The values to load are based on the property names given as parameters. 
   # Input: propLable - property prefix, propsVector - vector containing all property names.  eg. [descTestUser, descTestDesc] for example.
   # Output: Vector with all properties loaded.
   def loadPropSetsIntoVector(self, propLable, propsVector):
      bvtCommon = BvtCommon()
      configProps = BvtCommon.loadProperties(bvtCommon, PROPFILE)

      resultVector = Vector()  
      # numOfItems = value of descTest.size for example.
      numOfItems = Integer.valueOf(BvtCommon.getProperty(bvtCommon, configProps, propLable + ".size"))
      numOfPropsPerItem = propsVector.size()
  
      for i in range(numOfItems):
          items = Vector()
          for k in range(numOfPropsPerItem):
              propName = propLable + "." + propsVector[k] + String.valueOf(i + 1)
              items.add(String(BvtCommon.getProperty(bvtCommon, configProps, propName)).trim())             
          resultVector.add(items)

      return resultVector       


   def printErrorMsg(self, err):
      print "**************************************************************"
      print err
      print "**************************************************************"
