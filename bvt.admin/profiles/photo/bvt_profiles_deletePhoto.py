#  This is:  bvt_profiles_deletePhoto.py
#
#  Jython Script to test some Communities commands on IBM Profiles Enterprise Application
#  (C) Copyright IBM Corporation, 2009
#
#  This script can be run from a .bat from the Windows command prompt, or a .sh on Linux shell.
#
#  The default profile_root directory for a profile named profile_name is as following:
#   - C:\Program Files\IBM\WebSphere\AppServer\profiles\profile_name (on Windows)
#   - /opt/IBM/WebSphere/AppServer/profiles/profile_name (on Linux)
#
#  The default profile name on WebSphere Application Server is 'default'.
#

# AUTHOR:           yees@us.ibm.com
# INITIAL RELEASE:  LC2.5
# TEST TARGETS:     Test the ProfilesService.deletePhoto command

import sys, traceback, java
from types import *
from java.lang import Boolean
from java.util import Vector
from java.sql import Clob
from java.lang import RuntimeException
from java import io
import os

#print sys.path
execfile('profilesAdmin.py') # only do this once
execfile('bvt_common.py')
execfile('bvt_profiles_common.py')
execfile('DbUtils.py')

#Globals
UNIQUE_SCRIPT_ID = "bvt_profiles_deletePhoto"
ERRORFILE = UNIQUE_SCRIPT_ID + ".err"
PROPFILE = "bvt_profiles_common.properties"
global fErrorFile
#Set the following to "true" to set debug mode and allow detailed stack tracing
#Set the following to None to disable debug mode and allow standard error handling
DEBUG_SCRIPT = None



def BvtProfilesDeletePhotoInit():
   print ""
   print "--------------------------------------------------------------------------------"
   print " BVT Test Script for ProfilesService.deletePhoto command."
   print " (C) Copyright IBM Corporation, 2009 "
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

   inited = bvtProfilesCommon.initLibrary(logging, configProps)
   if (inited != 0):
      return

   # Load the e-mail to be tested into a vector. 
   photoPropsVector = Vector()
   photoPropsVector.add("email.")
   photoPropsVector = bvtProfilesCommon.loadPropSetsIntoVector("photo.user", photoPropsVector)

   dbType = java.lang.System.getProperty("dbType")   
   pplDbUtils = DBUtils("people.dbname")
   pplDbUtils.dbConnect(Boolean.TRUE)

   if dbType == "db2":
	   sql = "SELECT PROF_FILE_TYPE FROM EMPINST.EMPLOYEE AS EMPLOYEE JOIN EMPINST.PHOTO AS PHOTO ON EMPLOYEE.PROF_KEY = PHOTO.PROF_KEY WHERE PROF_MAIL_LOWER = ?"
   elif dbType == "sqlserver":
	   sql = "SELECT PROF_FILE_TYPE FROM PHOTO WHERE PROF_KEY = (SELECT PROF_KEY FROM EMPLOYEE WHERE PROF_MAIL_LOWER = ?)"   
   elif dbType == "oracle":
	   sql = "SELECT PROF_FILE_TYPE FROM PHOTO WHERE PROF_KEY = (SELECT PROF_KEY FROM EMPLOYEE WHERE PROF_MAIL_LOWER = ?)"
   
   prepStmt = pplDbUtils.prepareQuery(Boolean.TRUE, sql)

   # Delete the photo for the test profiles.
   for i in range(photoPropsVector.size()):
       user = photoPropsVector[i][0]
       # Make sure the profile does have a photo before the test.
       try:
          prepStmt.setString(1, user)    
          rs = pplDbUtils.runPreparedQuery(prepStmt)
       except:
          pplDbUtils.dbDisconnect()
          traceback.print_exception(sys.exc_info()[0], sys.exc_info()[1], sys.exc_info()[2])
          raise RuntimeException("==>Error! - Cannot retrieve data from the Profiles database!")

       # Ready to run the test
       if rs.next():
          print "\nExisting profile photo found for " + user + ".  Ready to run the test."
          deletePhotoCmd = "ProfilesService.deletePhoto(\"" + user + "\")"    
          print "\nIssuing the command - " + deletePhotoCmd
          exec deletePhotoCmd

          # Verify if the row for the photo no longer exist in the database.
          try:
             rs = pplDbUtils.runPreparedQuery(prepStmt)
          except:
             pplDbUtils.dbDisconnect()
             traceback.print_exception(sys.exc_info()[0], sys.exc_info()[1], sys.exc_info()[2])
             raise RuntimeException("==>Error! - Cannot retrieve data from the Profiles database!")

          if rs.next():
             # Photo still exists - error
             bvtProfilesCommon.printErrorMsg("==>Error! - Cannot delete photo for " + user)
             pplDbUtils.dbDisconnect() 
             traceback.print_exception(sys.exc_info()[0], sys.exc_info()[1], sys.exc_info()[2])
             raise RuntimeException("deletePhoto Error!")                
          else:
             print "deletePhoto for " + user + " passed!"
             rs.close()
       else:
          bvtProfilesCommon.printErrorMsg("==>Error! - there is no photo for " + user + " to begin the test.  Insert a photo and run again.")
          pplDbUtils.dbDisconnect()
          traceback.print_exception(sys.exc_info()[0], sys.exc_info()[1], sys.exc_info()[2])
          raise RuntimeException("==>Error! - test user data error!")
   
       rs.close()

   pplDbUtils.dbDisconnect()

   print ""
   return

# =========================================================
#-------------------------------------------------
# Main  - just execute the deletePhoto test function
#-------------------------------------------------
global bvtProfilesCommon
bvtProfilesCommon = BvtProfilesCommon()
print "BVT ProfilesService.deletePhoto Test..."

if DEBUG_SCRIPT != None:
   print "WARNING: Script is in DEBUG MODE!  Set DEBUG_SCRIPT variable (at top of script) to None to disable debug mode!"
   BvtProfilesUpdateExpInit()
   global fErrorFile
   fErrorFile.close()
   os.unlink(ERRORFILE)
else:	
   try:
      BvtProfilesDeletePhotoInit()
   except:
      global fErrorFile
      traceback.print_exception(sys.exc_info()[0], sys.exc_info()[1], sys.exc_info()[2])
      traceback.print_exception(sys.exc_info()[0], sys.exc_info()[1], sys.exc_info()[2], limit = None, file = fErrorFile)
      print '*****************************************************************'
      print 'FAIL FAIL FAIL FAIL FAIL FAIL FAIL FAIL FAIL FAIL FAIL FAIL FAIL'
      print '*****************************************************************'
      fErrorFile.close()
   else:
      global fErrorFile
      fErrorFile.close()
      os.unlink(ERRORFILE)
      print '*****************************************************************'
      print 'PASS PASS PASS PASS PASS PASS PASS PASS PASS PASS PASS PASS PASS'
      print '*****************************************************************'
      print ""

