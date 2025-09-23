#  This is bvt_activities_ConfigService.py
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
# TEST TARGETS:     ActivitiesConfigService.checkOutConfig(tempDir,cellName); ActivitiesConfigService.showConfig(); 
#                   ActivitiesConfigService.updateConfig(parameter, value); ActivitiesConfigService.checkInConfig()


import sys, traceback, java
from types import *
from java import lang
from java.lang import System
from java.lang import String
from java.lang import Boolean
from java import util
from java.util import ArrayList
from java.util import Hashtable
from java import io
import os
from os import path
import com.ibm.janet.core.LineCompare
from com.ibm.janet.core.LineCompare import compareLines

#print sys.path

execfile('activitiesAdmin.py') # only do this once
execfile('bvt_common.py')
execfile('bvt_config_common.py')
execfile('ListServiceModule.py')

#Globals
SCRIPT_ERROR = 0
global UNIQUE_SCRIPT_ID
global RUNFILE
global GOLDFILE
global ERRORFILE
global PROPFILE
# denise added these next 4
global OACONFIGXML
global OACONFIGXML_BAK
global OACONFIGGOLD
global OAJOBSXML
global OAJOBSXML_BAK
global OAJOBSGOLD
global QETEMPDIR
global OACONFIGDIR
global OAJOBSDIR
global OACONFIGXML_MOD
global OAJOBSXML_MOD


global fErrorFile
#Set the following to "true" to set debug mode and allow detailed stack tracing
#Set the following to None to disable debug mode and allow standard error handling
#DEBUG_SCRIPT = "true"
DEBUG_SCRIPT = None

def BvtActivitiesConfigService():
   global OACONFIGDIR, OACONFIGXML, OACONFIGXML_BAK, OAJOBSDIR, OAJOBSXML, OAJOBSXML_BAK, OACONFIGXML_MOD, OAJOBSXML_MOD, OSNAME
   global UNIQUE_SCRIPT_ID, RUNFILE, GOLDFILE, ERRORFILE, PROPFILE
   UNIQUE_SCRIPT_ID = "bvt_activities_ConfigService"
   RUNFILE = UNIQUE_SCRIPT_ID + ".run"
   GOLDFILE = UNIQUE_SCRIPT_ID + ".gld"
   ERRORFILE = UNIQUE_SCRIPT_ID + ".err"
   PROPFILE = UNIQUE_SCRIPT_ID + ".properties"
   OSNAME = java.lang.System.getProperty("os.name")
   OSARCH = java.lang.System.getProperty("os.arch")
   OSVER = java.lang.System.getProperty("os.version")

   print " "
   print "--------------------------------------------------------------------------------"
   print " BVT Test Script that will test the various ActivitiesConfigService MBean commands"
   print " (C) Copyright IBM Corporation, 2008 "
   print " "
   print " Script Name:                  " + UNIQUE_SCRIPT_ID + ".py"
   print " Script Properties File:       " + PROPFILE
   print " Script Output File:           " + RUNFILE
   print " Script Baseline Compare File: " + GOLDFILE
   print " Script Error File:            " + ERRORFILE
   print " Script is Running on OS:      " + OSNAME + " (Version: " + OSVER + ")"
   print " Script Execution Directory    " + os.getcwd()
   print "--------------------------------------------------------------------------------"
   print " "

   # denise added these next 4
   OACONFIGXML = "oa-config.xml"
   OACONFIGXML_MOD = "oa-config.mod"
   OACONFIGXML_BAK = "oa-config.bak"
   OACONFIGGOLD = "oa-config.gld"
   OAJOBSXML = "oa-jobs.xml"
   OAJOBSXML_MOD = "oa-jobs.mod"
   OAJOBSXML_BAK = "oa-jobs.bak"
   OAJOBSGOLD = "oa-jobs.gld"
   QETEMPDIR = "QE-tempdir"

   #Create error file for logging any errors.  This file will be removed later if no errors occurred
   #This needs to be done at the start of every script. Use the script name as part of the filename
   global fErrorFile
   fErrorFile = open(ERRORFILE, "w+")

   #  Get the name of the cell using the WAS wsadmin cmd: AdminControl.getCell()...
   cellname=AdminControl.getCell()

   # Build the cofig files location from the cellname so that we can properly reference the OA config files in this test
   OACONFIGDIR = os.getcwd() + "/../config/cells/" + cellname + "/LotusConnections-config" + "/"
   OAJOBSDIR = os.getcwd() + "/../config/cells/" + cellname + "/LotusConnections-config/extern" + "/"
   QETEMPDIR = os.getcwd() + "/" + QETEMPDIR + "/"

   bvtCommon = BvtCommon()
   configProps = BvtCommon.loadProperties(bvtCommon, PROPFILE)
   loadedProps = BvtCommon.getLoadedProperties(bvtCommon, PROPFILE)
   debugLogging = BvtCommon.getProperty(bvtCommon, configProps, "debugLogging")
   if debugLogging == "true":
      logging = 1 # turn on logging
   else:
      logging = 0 # default no logging messages

   bvtConfigCommon = BvtConfigCommon()
   inited = BvtConfigCommon.initLibrary(bvtConfigCommon, logging)
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
   #activitySortKey = BvtCommon.getProperty(bvtCommon, configProps, "activity_sort_key")


   #Backup original oaconfig.xml if not already backed-up
   print " ******************************************"
   print " Backing up original Server Configuration File: " + OACONFIGDIR + OACONFIGXML + " To: " + OACONFIGDIR + OACONFIGXML_BAK 
   print " ******************************************"
   if( path.exists(OACONFIGDIR + OACONFIGXML_BAK) ):
      print "Server Configuration File: " + OACONFIGDIR + OACONFIGXML_BAK + " Already Exists! Aborting Backup of this file!"
   else:      
      stafCmd = "staf local FS COPY FILE " + OACONFIGDIR + OACONFIGXML + " TOFILE " + OACONFIGDIR + OACONFIGXML_BAK + " TOMACHINE local"
      #stafCmd = "C:/staf/bin/staf local FS COPY FILE " + "C:/IBM/webSphere/AppServer/profiles/AppSrv01/config/cells/SHIRE1Node01Cell/LotusConnections-config/" + OACONFIGXML + " TOFILE " + "C:/IBM/webSphere/AppServer/profiles/AppSrv01/config/cells/SHIRE1Node01Cell/LotusConnections-config/" + OACONFIGXML_BAK + " TOMACHINE local"
      status = os.system(stafCmd)
      if( status != 0):
         SCRIPT_ERROR = 1
         errorStr = "ERROR: STAF Command: [" + stafCmd + "] returned error status code: " + str(status) + "\n"
         print errorStr
         fErrorFile.write( errorStr) 
         fErrorFile.flush()
      else:
         print "Server Configuration File: " + OACONFIGDIR + OACONFIGXML_BAK + " Backup File Successfully Created!"


   #Backup original oajobs.xml if not already backed-up
   print " ******************************************"
   print " Backing up original Server Configuration File: " + OAJOBSDIR + OAJOBSXML + " To: " + OAJOBSDIR + OAJOBSXML_BAK 
   print " ******************************************"
   if( path.exists(OAJOBSDIR + OAJOBSXML_BAK) ):
      print "Server Configuration File: " + OAJOBSDIR + OAJOBSXML_BAK + " Already Exists! Aborting Backup of this file!"
   else:
      stafCmd = "staf local FS COPY FILE " + OAJOBSDIR + OAJOBSXML + " TOFILE " + OAJOBSDIR + OAJOBSXML_BAK + " TOMACHINE local"
      #stafCmd = "C:/staf/bin/staf local FS COPY FILE " + "C:/IBM/webSphere/AppServer/profiles/AppSrv01/config/cells/SHIRE1Node01Cell/LotusConnections-config/extern/" + OAJOBSXML + " TOFILE " + "C:/IBM/webSphere/AppServer/profiles/AppSrv01/config/cells/SHIRE1Node01Cell/LotusConnections-config/extern/" + OAJOBSXML_BAK + " TOMACHINE local"
      status = os.system(stafCmd)
      if( status != 0):
         SCRIPT_ERROR = 1
         errorStr = "ERROR: STAF Command: [" + stafCmd + "] returned error status code: " + str(status) + "\n"
         print errorStr
         fErrorFile.write( errorStr) 
         fErrorFile.flush() 
      else:
         print "Server Configuration File: " + OAJOBSDIR + OAJOBSXML + " Backup File Successfully Created!"


   #Create the QETEMPDIR if it does not already exist.  This directory is used to store temp changes to the XML config files
   print " ******************************************"
   print " Creating Temporary Config Work Directory: " + QETEMPDIR
   print " ******************************************"
   if( path.exists(QETEMPDIR)):
      print "Temp Config Directory: " + QETEMPDIR + " already exists - no need to create"
   else:
      print "Temp Config Directory: " + QETEMPDIR + " does not exist - creating it..."
      os.mkdir(QETEMPDIR)
      print "Successfully created Temp Config Directory: " + QETEMPDIR


   print " *******************************************"
   print "Checking out the Activities config files to folder: QE-tempdir under the AppSrv0#/bin dir "
   checkout = ActivitiesConfigService.checkOutConfig(QETEMPDIR, cellname)
   print "This is what is returned from the command"
   print checkout   
   print ""

   # Will now write the results to the RUN LOG file
   flags = Hashtable()
   flags.put("append", "false")
   BvtConfigCommon.writeStringToFile(bvtConfigCommon, RUNFILE, checkout, flags)

   
   print " ******************************************"
   print " Will display the current/original config settings for Activities, using the showConfig cmd..."
   showoriginal=ActivitiesConfigService.showConfig()
   print showoriginal
   print ""

   print " ******************************************"
   print " Updating the activeContentFilter.enabled parameter to false, using the updateConfig cmd..."
   updated=ActivitiesConfigService.updateConfig("activeContentFilter.enabled", "false")
   print updated
   print ""

   print " ******************************************"
   print " Updating the jobs.TrashAutoPurge.daysRetention parameter to 25, using the updateConfig cmd..."
   updated=ActivitiesConfigService.updateConfig("jobs.TrashAutoPurge.daysRetention", "25")
   print updated
   print ""

   print " ******************************************"
   print " Now we will re-display the updated config settings for Activities, using the showConfig cmd..."
   showupdated=ActivitiesConfigService.showConfig()
   print showupdated
   print ""


   print " ******************************************"
   print " Lastly, will now do a checkInConfig cmd to copy the updated xml files from the tempdir"
   print " into the true location where these files are stored - overwriting the originals..."
   ActivitiesConfigService.checkInConfig()
   print ""

   print " ******************************************"
   print "Will now do a line-by-line compare of oa-config.gld (gold file) to oa-config.xml(updated)"

   #  Compare the test run results updated oa-config.xml with the Gold file oa-config.gld
   #  NOTE:  the 1st input below None - means there's no col separator because we are using line-by-line
   #  comparison of the 2 files
   lc = com.ibm.janet.core.LineCompare(Boolean.FALSE, loadedProps, bMakeGoldLog)
   lc.compareLines( None, OACONFIGDIR + OACONFIGXML, OACONFIGDIR + OACONFIGGOLD)
   print ""
   


   print " ******************************************"
   print "Will now do a line-by-line compare of: oa-jobs.gld (gold file) to oa-jobs.xml(updated)"
   lc.compareLines( None, OAJOBSDIR + OAJOBSXML, OAJOBSDIR + OAJOBSGOLD)
   print ""

   return
# =========================================================

#global BvtCommon
global bvtConfigCommon

bvtConfigCommon=BvtConfigCommon()
print "BVT testing of ActivitiesConfigService commands ... starting."

#-------------------------------------------------
# Main  - just execute the ActivitiesConfigService test function
#-------------------------------------------------
print "BVT ActivitiesConfigService Test ... calling BvtActivitiesConfigService()"
try:
   if DEBUG_SCRIPT != None:
      print "WARNING: Script is in DEBUG MODE!  Set DEBUG_SCRIPT variable (at top of script) to None to disable debug mode!"
      BvtActivitiesConfigService()
   else:	
      try:
         BvtActivitiesConfigService()
      except:
         SCRIPT_ERROR = 1
         global fErrorFile
         print '*****************************************************************'
         print 'FAIL FAIL FAIL FAIL FAIL FAIL FAIL FAIL FAIL FAIL FAIL FAIL FAIL'
         print ' '
         print "EXCEPTION: " + str(sys.exc_info()[1])
         print ' '
         print 'FAIL FAIL FAIL FAIL FAIL FAIL FAIL FAIL FAIL FAIL FAIL FAIL FAIL'
         print '*****************************************************************'
         traceback.print_exception(sys.exc_info()[0], sys.exc_info()[1], sys.exc_info()[2])
         traceback.print_exception(sys.exc_info()[0], sys.exc_info()[1], sys.exc_info()[2], limit = None, file = fErrorFile)
         fErrorFile.flush() 
      else:
         print '*****************************************************************'
         print 'PASS PASS PASS PASS PASS PASS PASS PASS PASS PASS PASS PASS PASS'
         print '*****************************************************************'
         print ""
finally:
   #Restore orginal Server Config State - This finally block will be executed under all (normal completion or exceptions) conditions
   global fErrorFile
   global ERRORFILE

   global OACONFIGDIR, OACONFIGXML, OACONFIGXML_BAK, OACONFIGXML_MOD, OAJOBSDIR, OAJOBSXML, OAJOBSXML_BAK, OAJOBSXML_MOD
   #Make copy of modified oaconfigxml config file so that we can keep track of modifications - named oa-config.mod
   stafCmd = "staf local FS COPY FILE " + OACONFIGDIR + OACONFIGXML + " TOFILE " + OACONFIGDIR + OACONFIGXML_MOD + " TOMACHINE local"
   #stafCmd = "C:/staf/bin/staf local FS COPY FILE " + "C:/IBM/webSphere/AppServer/profiles/AppSrv01/config/cells/SHIRE1Node01Cell/LotusConnections-config/" + OACONFIGXML + " TOFILE " + "C:/IBM/webSphere/AppServer/profiles/AppSrv01/config/cells/SHIRE1Node01Cell/LotusConnections-config/" + OACONFIGXML_MOD + " TOMACHINE local"
   status = os.system(stafCmd)
   if( status != 0):
      SCRIPT_ERROR = 1
      errorStr = "ERROR: STAF Command: [" + stafCmd + "] returned error status code: " + str(status) + "\n"
      print errorStr
      fErrorFile.write( errorStr) 
      fErrorFile.flush() 

   #Make copy of modified oajobsxml config file so that we can keep track of modifications - named oa-jobs.mod
   stafCmd = "staf local FS COPY FILE " + OAJOBSDIR + OAJOBSXML + " TOFILE " + OAJOBSDIR + OAJOBSXML_MOD + " TOMACHINE local"
   #stafCmd = "C:/staf/bin/staf local FS COPY FILE " + "C:/IBM/webSphere/AppServer/profiles/AppSrv01/config/cells/SHIRE1Node01Cell/LotusConnections-config/extern/" + OAJOBSXML + " TOFILE " + "C:/IBM/webSphere/AppServer/profiles/AppSrv01/config/cells/SHIRE1Node01Cell/LotusConnections-config/extern/" + OAJOBSXML_MOD + " TOMACHINE local"
   status = os.system(stafCmd)
   if( status != 0):
      SCRIPT_ERROR = 1
      errorStr = "ERROR: STAF Command: [" + stafCmd + "] returned error status code: " + str(status) + "\n"
      print errorStr
      fErrorFile.write( errorStr) 
      fErrorFile.flush() 


   #Restore original oa-config.xml from oa-config.bak
   print " ******************************************"
   print " Restoring original Server Configuration File: " + OACONFIGDIR + OACONFIGXML_BAK + " To: " + OACONFIGDIR + OACONFIGXML
   print " ******************************************"
   stafCmd = "staf local FS COPY FILE " + OACONFIGDIR + OACONFIGXML_BAK + " TOFILE " + OACONFIGDIR + OACONFIGXML + " TOMACHINE local"
   #stafCmd = "C:/staf/bin/staf local FS COPY FILE " + "C:/IBM/webSphere/AppServer/profiles/AppSrv01/config/cells/SHIRE1Node01Cell/LotusConnections-config/" + OACONFIGXML_BAK + " TOFILE " + "C:/IBM/webSphere/AppServer/profiles/AppSrv01/config/cells/SHIRE1Node01Cell/LotusConnections-config/" + OACONFIGXML + " TOMACHINE local"
   status = os.system(stafCmd)
   if( status != 0):
      SCRIPT_ERROR = 1
      errorStr = "ERROR: STAF Command: [" + stafCmd + "] returned error status code: " + str(status) + "\n"
      print errorStr
      fErrorFile.write( errorStr) 
      fErrorFile.flush() 

   #Restore original oa-jobs.xml from oa-config.bak
   print " ******************************************"
   print " Restoring original Server Configuration File: " + OAJOBSDIR + OAJOBSXML_BAK + " To: " + OAJOBSDIR + OAJOBSXML 
   print " ******************************************"
   stafCmd = "staf local FS COPY FILE " + OAJOBSDIR + OAJOBSXML_BAK + " TOFILE " + OAJOBSDIR + OAJOBSXML + " TOMACHINE local"
   #stafCmd = "C:/staf/bin/staf local FS COPY FILE " + "C:/IBM/webSphere/AppServer/profiles/AppSrv01/config/cells/SHIRE1Node01Cell/LotusConnections-config/extern/" + OAJOBSXML_BAK + " TOFILE " + "C:/IBM/webSphere/AppServer/profiles/AppSrv01/config/cells/SHIRE1Node01Cell/LotusConnections-config/extern/" + OAJOBSXML + " TOMACHINE local"
   status = os.system(stafCmd)
   if( status != 0):
      SCRIPT_ERROR = 1
      errorStr = "ERROR: STAF Command: [" + stafCmd + "] returned error status code: " + str(status) + "\n"
      print errorStr
      fErrorFile.write( errorStr) 
      fErrorFile.flush() 
   
   #If SCRIPT_ERROR is non-zero then there was a problem so don't delete the script error file
   if( SCRIPT_ERROR == 0):
      fErrorFile.close()
      os.unlink(ERRORFILE)
   else:
      fErrorFile.close()


