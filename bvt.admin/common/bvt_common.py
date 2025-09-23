#
#  Jython Script library for common admin (base class) for IBM Activities Enterprise Application
#  (C) Copyright IBM Corporation, 2007
#
#  The library creates the WebSphere server objects required for the Activities database connectivity,
#  verifies this database connectivity, and then runs some test against the Activities Enterprise Application scheduler
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

import sys, java
import os
from os import path
from types import *
from java import lang
from java import util
from java.util import ArrayList
from java import io
import com.ibm.janet.core.StringUtils
from com.ibm.janet.core.StringUtils import tokensToArrayList


#from activitiesAdmin import AdminControl
#from activitiesAdmin import *

# import activitiesAdmin

# global serverName

if (not globals().has_key('bvtCommon')):
   print "!= defined"
   # so, define it !
   bvtCommon = "defined"

#   execfile('activitiesAdmin.py') # only do this once
#   import activitiesAdmin

   class BvtCommon:
#
#     BvtCommon contains the base class & commonly used methods
#
      logging = 1 # default is false - no logging messages
#
      def __init__(self, name='common'):
          """
          Special method __init__() is called first (acts as Constructor).
          It brings in data from outside the class like the variable 'name'.
          (in this case 'name' is also set to a default value of 'common')
          The first parameter of any method/function in the class is always self,
          the name self is used by convention.  Assigning name to self.name allows it
          to be passed to all methods within the class.
          The variable 'logging' is assigned a default value in the class, but outside of the methods.
          You can access logging in a method using self.logging
          """
          self.name = name

      def __call__ (self, *args):
          return BvtCommon(self, args)

      def setLogging(self, logState):
          self.logging = logState

#     test code
      def printLogState(self):
          self.logging = 1
          if (self.logging == 1):
             state = 'on'
          else:
             state = 'off'
          msg="%s's logging state is %s" % (self.name, state)
          print msg
          return

      def printName(self):
          print "My name is :", self.name

      def Hello1(self):
          print "Hello from Class BvtCommon!"

      def HelloCommon(self):
          print "HelloCommon from Class BvtCommon!"
          self.printLogState()

      def __localHello(self):
#
          # A variable or function with a double underline prefix and no or max. single
          # underline postfix is considered private to the class and is not inherited or
          # accessible outside the class.
#
          print "A hardy Hello only used within the class!"

      def initLibrary(self, logging):
          if (logging == 1):
             print ""
             print "------------------------------------------------------------------------------------------"
             print " Initialization script for the Activities Enterprise Application (%s)" % self.name
             print " (C) Copyright IBM Corporation, 2007"
             print "------------------------------------------------------------------------------------------"
          return 0  # init is OK !

#     helper methods
      def loadProperties(self, source):
          """ Load a Java properties file into a Dictionary. """
          result = {}
          try:
             if type(source) == type(''): # name provided, use file
                source = io.FileInputStream(source)
             bis = io.BufferedInputStream(source)
             props = util.Properties()
             props.load(bis)
             bis.close()
             for key in props.keySet().iterator():
                 result[key] = props.get(key)
          except java.io.FileNotFoundException, e:
            print "File not found: " + source
            print ""
          return result

      def getLoadedProperties(self, source):
          try:
             if type(source) == type(''): # name provided, use file
                source = io.FileInputStream(source)
             bis = io.BufferedInputStream(source)
             props = util.Properties()
             props.load(bis)
             bis.close()
          except java.io.FileNotFoundException, e:
            print "File not found: " + source
            print ""
          return props


      def getProperty(self, props, key):
          val = props.get(key)
          if val == None:
             print "Error, the property '" + key + "' can not be null"
             print ""
          return val
          
#     Method to setup and return the exclude column list when comparing results
      def GetColumnCompareExcludeList(self, props):
          key = "exclude_columns_enabled"
          val = props.get(key)
          if val == None:
             raise "Property: " + key + " was Null or not set in the script's property file! (values: true | false)"
          val.lower()
          if val == "true":
             key = "exclude_columns"
             val = props.get(key)
	     if val == None:
                print "INFO:GetColumnCompareExcludList(): No columns were specified for compare exclusion!"
	        return None
	     strUtil = com.ibm.janet.core.StringUtils(Boolean.TRUE)
	     result = strUtil.tokensToArrayList( val, ",")
          else:
             print "INFO:GetColumnCompareExcludList(): Property: exclude_columns_enabled was set to: false - Column exclusion feature disabled!"
             return None
          return result
             		

#     Method to restore a backed-up file. Overwrites existing file if any
      def restoreFile(self, sourceFile, targetFile):
          print "INFO:BvtCommon.RestoreFile(): Restoring File: " + sourceFile + " to: " + targetFile + "..."

          #Parameter Validation
          if sourceFile == None:
             raise "Cannot Restore File!  Source File is Null!"
          if targetFile == None:
             raise "Cannot Restore File!  Target File is Null!"

          if sourceFile == None:
             raise "Cannot Restore File!  Source File is Null!"

          #Make certain the source file exists at its specified location
	  if( path.exists(sourceFile)):
             print "INFO:BvtCommon.RestoreFile(): Source File: " + sourceFile + " was found to exist - Restoring it to: " + targetFile + "..."
             if( path.exists(targetFile)):
                print "INFO:BvtCommon.RestoreFile(): Target File: " + targetFile + " was found to exist - This file will be overwritten by source file: " + sourceFile
                # Remove the target file
                os.unlink(targetFile)
                os.rename(sourceFile, targetFile)
                print "INFO:BvtCommon.RestoreFile(): Successfully restored source File: " + sourceFile + " to target file: " + targetFile + "..."
             else:             	
                print "INFO:BvtCommon.RestoreFile(): Target File: " + targetFile + " was NOT found to previously exist!"
	  else:
             raise "Cannot Restore File! Source File: " + sourceFile + " does not exist at its specified location!"
      
      
else:
   print "== defined"
