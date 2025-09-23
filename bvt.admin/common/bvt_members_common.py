#  This is file:  bvt_members_common.py
#
#  Jython Script library for the Activity service running on IBM Activities Enterprise Application
#  (C) Copyright IBM Corporation, 2007
#
#  This script can be run from a .bat from the Windows command prompt, or a .sh on Linux shell.
#
#  This is:  bvt_member_common.py

import sys, java
from types import *
from java import lang
from java import util
from java import io
from java.util import Collections

#from bvt_common import *

#execfile('bvt_common.py')

#import bvt_common

class BvtMembersCommon(BvtCommon):
   def __call__ (self, *args):
       return BvtMembersCommon(self, args)

   def initLibrary(self, logging):
      bvtCommon = BvtCommon()
      inited = BvtCommon.initLibrary(bvtCommon, logging)
      return inited

   
   def listOneMember(self, memberHashtable):
       from java.lang import String

       memberId     =  memberHashtable.get("memberId")
       displayName  =  memberHashtable.get("displayName")
       staticProfile = memberHashtable.get("staticProfile")
       externalId    = memberHashtable.get("externalId")
       email         = memberHashtable.get("email")
       memberType    = memberHashtable.get("memberType")
       loginNames    = memberHashtable.get("loginNames")
       Collections.sort(loginNames)

       printBuffer = "[MEMBER]::"
       printBuffer = printBuffer + "[ID] = " + memberId + "::"
       printBuffer = printBuffer + "[DISPLAYNAME] = " + displayName + "::"
       printBuffer = printBuffer + "[STATIC_PROFILE] = " + staticProfile + "::"
       printBuffer = printBuffer + "[EXTERNAL_ID] = " + externalId + "::"
       printBuffer = printBuffer + "[EMAIL] = " + email
       printBuffer = printBuffer + "[MEMBER_TYPE] = " + memberType + "::"
       printBuffer = printBuffer + "[LOGINNAMES] = "
       for i in range(loginNames.size()):
           printBuffer = printBuffer + loginNames[i] + ","

       printBuffer = printBuffer[0:len(printBuffer)-1]
       printBuffer = printBuffer + "::"
       printBuffer = printBuffer + "\n"

       print printBuffer
       return



   def listAllMembers(self, memberVector):
#      """ Iterate over vector of Members and print it from start to finish. """
      count = 0
      if memberVector != None:
         if len(memberVector) >0:
            try:
               for member in memberVector:
                   count = count + 1
                   print "[%d] " % count, member
#                  listOneMember( member ):
            except java.lang.Exception, e:
               print "Exception: " + e
      print ""
      return count

 

   def writeMembersToFile(self, fileName, memberVector, flags ):
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
          i = 0
          memberIterator = memberVector.iterator()
          while( memberIterator.hasNext( )):
              i = i + 1
              memberHashtable = memberIterator.next( )
             
              memberId     = memberHashtable.get("memberId")
              displayName  = memberHashtable.get("displayName")
              staticProfile = memberHashtable.get("staticProfile")
              externalId    = memberHashtable.get("externalId")
              email         = memberHashtable.get("email")
              memberType    = memberHashtable.get("memberType")
              loginNames    = memberHashtable.get("loginNames")
              Collections.sort(loginNames)

          
              fileBuffer = "[MEMBER]::"
              fileBuffer = fileBuffer + "[ID] = " + memberId + "::"
              fileBuffer = fileBuffer + "[DISPLAYNAME] = " + displayName + "::"
              fileBuffer = fileBuffer + "[STATIC_PROFILE] = " + staticProfile + "::"
              fileBuffer = fileBuffer + "[EXTERNAL_ID] = " + externalId + "::"
              fileBuffer = fileBuffer + "[EMAIL] = " + email
              fileBuffer = fileBuffer + "[MEMBER_TYPE] = " + memberType + "::"
              fileBuffer = fileBuffer + "[LOGINNAMES] = "
              for i in range(loginNames.size()):
                  fileBuffer = fileBuffer + loginNames[i] + ","
              
              fileBuffer = fileBuffer[0:len(fileBuffer)-1]
              fileBuffer = fileBuffer + "::"
              fileBuffer = fileBuffer + "\n"

              f.write( fileBuffer ) # note - no CR/LF on write (only on print)
       finally:
          f.flush() # or
          f.close()
       # If the program terminates here, the file has data
       return

#  Denise added this routine below 12/20/07
   def writeOneMemberToFile(self, fileName, memberHashtable, flags):
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

       from java.lang import String
       memberId     =  memberHashtable.get("memberId")
       displayName  =  memberHashtable.get("displayName")
       staticProfile = memberHashtable.get("staticProfile")
       externalId    = memberHashtable.get("externalId")
       email         = memberHashtable.get("email")
       memberType    = memberHashtable.get("memberType")
       loginNames    = memberHashtable.get("loginNames")
       Collections.sort(loginNames)

       fileBuffer = "[MEMBER]::"
       fileBuffer = fileBuffer + "[ID] = " + memberId + "::"
       fileBuffer = fileBuffer + "[DISPLAYNAME] = " + displayName + "::"
       fileBuffer = fileBuffer + "[STATIC_PROFILE] = " + staticProfile + "::"
       fileBuffer = fileBuffer + "[EXTERNAL_ID] = " + externalId + "::"
       fileBuffer = fileBuffer + "[EMAIL] = " + email
       fileBuffer = fileBuffer + "[MEMBER_TYPE] = " + memberType + "::"
       fileBuffer = fileBuffer + "[LOGINNAMES] = "
       for i in range(loginNames.size()):
           fileBuffer = fileBuffer + loginNames[i] + ","
       
       fileBuffer = fileBuffer[0:len(fileBuffer)-1]
       fileBuffer = fileBuffer + "::"
       fileBuffer = fileBuffer + "\n"

       f.write( fileBuffer ) # note - no CR/LF on write (only on print)

       f.flush() # or
       f.close()
       # If the program terminates here, the file has data
       return


