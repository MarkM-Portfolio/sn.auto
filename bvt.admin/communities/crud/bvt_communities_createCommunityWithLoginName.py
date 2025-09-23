#  This is:  bvt_communities_createCommunityWithLoginName.py
#
#  Jython Script to test some Communities commands on IBM Activities Enterprise Application
#  (C) Copyright IBM Corporation, 2008
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
# TEST TARGETS:     Run the CommunitiesService.createCommunityWithLoginName command

import sys, traceback, java
from types import *
from java.lang import Boolean
from java.util import Hashtable
from java.text import DateFormat
from java.util import Date
from java.lang import RuntimeException
from java import io
import os


#print sys.path

execfile('communitiesAdmin.py') # only do this once
execfile('bvt_common.py')
execfile('bvt_communities_common.py')
execfile('bvt_comm_members_common.py')

#Globals
UNIQUE_SCRIPT_ID = "bvt_communities_createCommunityWithLoginName"
ERRORFILE = UNIQUE_SCRIPT_ID + ".err"
PROPFILE = "bvt_comm_common.properties"
global fErrorFile
#Set the following to "true" to set debug mode and allow detailed stack tracing
#Set the following to None to disable debug mode and allow standard error handling
DEBUG_SCRIPT = None



def BvtCreateCommunityByLoginNameInit():
   print ""
   print "--------------------------------------------------------------------------------"
   print " BVT Test Script for createCommunityWithLoginName, fetchCommByName and fetchMember MBean commands."
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

   bvtCommunitiesCommon = BvtCommunitiesCommon()
   inited = BvtCommunitiesCommon.initLibrary(bvtCommunitiesCommon, logging)
   if (inited != 0):
      return


   dsmlFile = BvtCommon.getProperty(bvtCommon, configProps, "communities1.dsml.filename")
   ownerLoginName = BvtCommon.getProperty(bvtCommon, configProps, "communities1.owner.loginname")
   oldCommName1 = BvtCommon.getProperty(bvtCommon, configProps, "communities1.old.name")
   currentDate = Date()
   oldCommName1 = oldCommName1 + " " + DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG).format(currentDate);
   print ""
   createCommCmd1 = "CommunitiesService.createCommunityWithLoginName(\"" + oldCommName1 + "\", \"" + ownerLoginName + "\", 0, \"" + dsmlFile + "\")"   
   print "Issuing the command - " + createCommCmd1
   exec createCommCmd1

   fetchCommCmd1 = "comm1 = CommunitiesService.fetchCommByName(\"" + oldCommName1 + "\")"   
   print "Now fetch the community just created by name - " + fetchCommCmd1   
   exec fetchCommCmd1
   if comm1.isEmpty():
       printErrorMsg("==>Error! - fetchCommByName did not return any communities!")

   fetchMemberCmd1 = "comm1 = CommunitiesService.fetchMember(comm1)"
   print "Now fetchMember for this community - " + fetchMemberCmd1
   exec fetchMemberCmd1

   # Load the users and descriptions to be tested into a hashtable. 
   goldMemberTable = bvtCommunitiesCommon.loadPropSetsIntoHashTable("communities1", "member.display.", "member.role.")

   print "Checking memberList for this community (names and roles)..."
   comm1MemberList = comm1.get(0).get("memberList")
   compareMembers(goldMemberTable, comm1MemberList)
   
   print ""
   return

# =========================================================

#global BvtCommon
global bvtCommCommon
bvtCommCommon=BvtCommunitiesCommon()
print "BVT CommunitiesService createCommunityByLoginName Tests ... starting."

#-------------------------------------------------
# Main  - just execute the createCommunity test function
#-------------------------------------------------
print "BVT createCommunityByLoginName, fetchCommByName and fetchMember Tests..."

if DEBUG_SCRIPT != None:
   print "WARNING: Script is in DEBUG MODE!  Set DEBUG_SCRIPT variable (at top of script) to None to disable debug mode!"
   BvtCreateCommunityByLoginNameInit()
   global fErrorFile
   fErrorFile.close()
   os.unlink(ERRORFILE)
else:	
   try:
      BvtCreateCommunityByLoginNameInit()
   except:
      global fErrorFile
      traceback.print_exception(sys.exc_info()[0], sys.exc_info()[1], sys.exc_info()[2])
      traceback.print_exception(sys.exc_info()[0], sys.exc_info()[1], sys.exc_info()[2], limit = None, file = fErrorFile)
      fErrorFile.close()
      print '*****************************************************************'
      print 'FAIL FAIL FAIL FAIL FAIL FAIL FAIL FAIL FAIL FAIL FAIL FAIL FAIL'
      print '*****************************************************************'     
   else:
      global fErrorFile
      fErrorFile.close()
      os.unlink(ERRORFILE)
      print '*****************************************************************'
      print 'PASS PASS PASS PASS PASS PASS PASS PASS PASS PASS PASS PASS PASS'
      print '*****************************************************************'
      print ""


  

