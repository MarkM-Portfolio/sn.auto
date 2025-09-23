#  This is:  bvt_profiles_updateDescExp.py
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
# TEST TARGETS:     Test the ProfilesService.updateDescription and updateExperience command

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
UNIQUE_SCRIPT_ID = "bvt_profiles_updateDescExp"
ERRORFILE = UNIQUE_SCRIPT_ID + ".err"
PROPFILE = "bvt_profiles_common.properties"
global fErrorFile
#Set the following to "true" to set debug mode and allow detailed stack tracing
#Set the following to None to disable debug mode and allow standard error handling
DEBUG_SCRIPT = None



def BvtProfilesUpdateExpInit():
   print ""
   print "--------------------------------------------------------------------------------"
   print " BVT Test Script for ProfilesService.updateDescription and updateExperience commands."
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

   # Load the users and descriptions to be tested into a vector. 
   userDescExpProps = Vector()
   userDescExpProps.add("name.")
   userDescExpProps.add("description.")
   userDescExpProps.add("experience.")
   userDescVector = bvtProfilesCommon.loadPropSetsIntoVector("update.user", userDescExpProps)

   pplDbUtils = DBUtils("peopleDBName")
   pplDbUtils.dbConnect(Boolean.TRUE)

   # Update description and compare with the resulting data in the database.
   for i in range(userDescVector.size()):
       user = userDescVector[i][0]
       desc = userDescVector[i][1]
       updateDescCmd = "ProfilesService.updateDescription(\"" + user + "\", \"" + desc + "\")"    
       print "\nIssuing the command - " + updateDescCmd
       exec updateDescCmd

       exp = userDescVector[i][2]
       updateExpCmd = "ProfilesService.updateExperience(\"" + user + "\", \"" + exp + "\")"    
       print "\nIssuing the command - " + updateExpCmd
       exec updateExpCmd
       
       print "\nChecking updated values in database..."
       try:
          sql = "SELECT PROF_DESCRIPTION, PROF_EXPERIENCE FROM EMPINST.EMPLOYEE WHERE PROF_MAIL_LOWER = '" + user + "'"
          rs = pplDbUtils.runQuery(sql)
       except:
          raise RuntimeException("==>Error! - Cannot retrieve data from the Profiles database!")
          pplDbUtils.dbDisconnect()

       # Compare the data retrieved from the database
       if rs.next():
	       descClob = rs.getClob("PROF_DESCRIPTION")
	       expClob = rs.getClob("PROF_EXPERIENCE")

	       if descClob == None:
	          bvtProfilesCommon.printErrorMsg("==>Error! - Description of " + user + " is still empty in the database.")
	          raise "updateDescription Error!"
	       else:
	          descInDb = String(descClob.getSubString(1, descClob.length())).trim()
	          if descInDb == desc:
	            print "updateDescription for " + user + " passed!"
	          else:
	            bvtProfilesCommon.printErrorMsg("==>Error! - Description of " + user + " in database doesn't match the gold string!\nGold: " + desc + "\nDatabase: " + descInDb)
	            raise "updateDescription Error!"

	       if expClob == None:
	          bvtProfilesCommon.printErrorMsg("==>Error! - Experience of " + user + " is still empty in the database.")
	          raise "updateExperience Error!"
	       else:
	          expInDb = String(expClob.getSubString(1, expClob.length())).trim()
	          if expInDb == exp:
	            print "updateExperience for " + user + " passed!"
	          else:
	            bvtProfilesCommon.printErrorMsg("==>Error! - Experience of " + user + " in database doesn't match the gold string!\nGold: " + desc + "\nDatabase: " + expInDb)
	            raise "updateExperience Error!"
       else:
	       bvtProfilesCommon.printErrorMsg("==>Error! - there is no row in the database for " + user)

       rs.close()

   pplDbUtils.dbDisconnect()




          
   
   print ""
   return

# =========================================================
#-------------------------------------------------
# Main  - just execute the updateDescription test function
#-------------------------------------------------
global bvtProfilesCommon
bvtProfilesCommon = BvtProfilesCommon()
print "BVT ProfilesService.updateDescription Test..."

if DEBUG_SCRIPT != None:
   print "WARNING: Script is in DEBUG MODE!  Set DEBUG_SCRIPT variable (at top of script) to None to disable debug mode!"
   BvtProfilesUpdateExpInit()
   global fErrorFile
   fErrorFile.close()
   os.unlink(ERRORFILE)
else:	
   try:
      BvtProfilesUpdateExpInit()
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

