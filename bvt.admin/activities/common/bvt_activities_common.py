#  This is bvt_activities_common.py  
#  This file was modified by denise 12/21/07.  Stripped out alot of routines, & fixed others.
#  
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
from java import io

#from bvt_common import *

#execfile('bvt_common.py')

#import bvt_common

class BvtActivitiesCommon(BvtCommon):
   def __call__ (self, *args):
       return BvtActivitiesCommon(self, args)

   def initLibrary(self, logging):
      bvtCommon = BvtCommon()
      inited = BvtCommon.initLibrary(bvtCommon, logging)
      return inited

#   def fetchActivities(self, filter):
#      if (filter == "deleted"):
#         print ""
#         print "Fetching deleted activities..."
#         a = ActivityService.fetchDeletedActivities()
#      if (filter == "completed"):
#         print ""
#         print "Fetching completed activities..."
#         a = ActivityService.fetchCompletedActivities()
#      return a

#   def fetchActivitiesByDate(self, filter, start, end, lastUUID):
#      a = {}
#      if (filter != "created") & (filter != "modified"):
#          raise "INTERNAL ERROR: Sort Filter Must request either 'created' or 'modified'"
#      else:
#         a = ActivityService.fetchActivitiesByDate(filter, start, end, lastUUID)
#      return a

#   def fetchActivitiesCreatedByMember(self, member):
#      a = {}
#      if (member == "") | (member == None):
#          raise "INTERNAL ERROR: An Activities Creator must be specified!"
#      else:
#         a = ActivityService.fetchActivitiesCreatedByMember(member)
#      return a

#   def setActivitiesOwner(self, activityVector, member, bFailOnError):
#      global fErrorFile
#      a = {}
#      if member == None:
#          raise "ERROR: An Activities Owner must be specified!"
#      if (activityVector == None) | (activityVector.isEmpty() ):
#          raise "ERROR: No Activities were provided to change Activity Ownership!"
#      else:
#         a = AccessControlService.setOwnerAccess(activityVector, member)
#         if a.isEmpty() <= 0:
#            if bFailOnError:
#               errorMessage = "ERROR: One or more activities could not be set to the new Owner!"
#               print errorMessage
#               if fErrorFile != None:
#                  print >> fErrorFile, errorMessage
#               BvtActivitiesCommon.listAllActivities(self, a)               
#               raise errorMessage
#            else:
#               print "WARNING: One or more activities could not be set to the new Owner!"
#               BvtActivitiesCommon.listAllActivities(self, a)               
#      return a

#   def deleteActivitiesAccessForMembers(self, activityVector, membersVector, bFailOnError):
#      global fErrorFile
#      a = {}
#      if (membersVector == None) | (membersVector.isEmpty() ):
#          raise "ERROR: An Activities Member must be specified!"
#      if (activityVector == None) | (activityVector.isEmpty() ):
#          raise "ERROR: No Activities were provided to Delete Activity Access!"
#      else:
#         a = AccessControlService.deleteAccess(activityVector, membersVector)
#         if a.isEmpty() <= 0:
#            errorMessage = "ERROR: One or more activities could not have the specified members removed from them!"
#            if bFailOnError:
#               print errorMessage
#               if fErrorFile != None:
#                  print >> fErrorFile, errorMessage
#               BvtActivitiesCommon.listAllActivities(self, a)               
#               raise errorMessage
#            else:
#               print "WARNING: One or more activities could not have the specified members removed from them!"
#               BvtActivitiesCommon.listAllActivities(self, a)               
#      return a

   def listAllActivities(self, activityVector):
      global fErrorFile
      """ Iterate over vector of Activities and print it from start to finish. """
      count = 0
      try:
         for item in activityVector:
                count = count + 1
                print "[%d] " % count, item
                if fErrorFile != None:
                   print >> fErrorFile, "[%d] " % count, item
      except java.lang.Exception, e:
         print "Exception: " + e
         print ""
      return count

 #  def listFirstActivities(self, activityVector, max):
 #     """ Iterate over vector of Activities and print it from start to requested max. """
 #     count = 0
 #     try:
 #        for item in activityVector:
 #           if (count < max):
 #               count = count + 1
 #               print "[%d] " % count, item
 #           else:
 #               return count
 #     except java.lang.Exception, e:
 #        print "Exception: " + e
 #        print ""
 #     return count

#   def listActivitiesRange(self, activityVector, min, max):
#      """ Iterate over vector of Activities and print it in chunks. """
#      skip_count = 0
#      item_count = 0
#      try:
#         if (min > max):
#      	   print "Invalid values: max value must be larger than min value."
#         else:
#            for item in activityVector:
#               if (skip_count < (min-1)):
#                   # skip it
#                   skip_count = skip_count + 1
#               else:
#                  # we are past 'min' item
#                  if (skip_count < max):
#                      skip_count = skip_count + 1
#                      item_count = item_count + 1
#                      print "[%d] " % skip_count, item
#                  else:
#                      return item_count
#      except java.lang.Exception, e:
#         print "Exception: " + e
#         print ""
#      return item_count

   def listOneActivity(self, activity):
       from java.lang import String
       activityId   = activity.get("activityId")
       name         = activity.get("name")
       createdBy    = activity.get("createdBy")
       createdDate  = activity.get("createdDate")
       modifiedBy   = activity.get("modifiedBy")
       modifiedDate = activity.get("modifiedDate")
       isCompleted  = activity.get("isCompleted")
       isDeleted    = activity.get("isDeleted")
       isTemplate   = activity.get("isTemplate")

       printBuffer = String()
       print type(name)
       print type(activityId)
       printBuffer= "[ACTIVITY]::"
       printBuffer= printBuffer+ "[ID] = " + activityId + "::"
       printBuffer= printBuffer+ "[NAME] = " + name + "::"
       printBuffer= printBuffer+ "[CREATED_BY] = " + createdBy + "::"
       printBuffer= printBuffer+ "[CREATED_DATE] = " + createdDate + "::"
       printBuffer= printBuffer+ "[MODIFIED_BY] = " + modifiedBy + "::"       
       printBuffer= printBuffer+ "[MODIFIED_DATE] = " + modifiedDate + "::"
       printBuffer= printBuffer+ "[IS_COMPLETED] = " + isCompleted + "::"
       printBuffer= printBuffer+ "[IS_DELETED] = " + isDeleted + "::"
       printBuffer= printBuffer+ "[IS_TEMPLATE] = " + isTemplate
       printBuffer= printBuffer+ "\n"
       print printBuffer
       return



   def writeActivitiesToFile(self, fileName, activityVector, flags):
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
          activityIterator = activityVector.iterator()
          while( activityIterator.hasNext( )):
              i = i + 1
              activityHashtable = activityIterator.next( )
             
              if (i > 12700):
                 print "[%d] " % i,
                 listOneActivity(activityHashtable)

              activityId   = activityHashtable.get("activityId")
              name         = activityHashtable.get("name")
              createdBy    = activityHashtable.get("createdBy")
              createdDate  = activityHashtable.get("createdDate")
              modifiedBy   = activityHashtable.get("modifiedBy")              
              modifiedDate = activityHashtable.get("modifiedDate")
              isCompleted  = activityHashtable.get("isCompleted")
              isDeleted    = activityHashtable.get("isDeleted")
              isTemplate   = activityHashtable.get("isTemplate")


              fileBuffer = "[ACTIVITY]::"
              fileBuffer = fileBuffer + "[ID] = " + activityId + "::"
              fileBuffer = fileBuffer + "[NAME] = " + name + "::"
              fileBuffer = fileBuffer + "[CREATED_BY] = " + createdBy + "::"
              fileBuffer = fileBuffer + "[CREATED_DATE] = " + createdDate + "::"
              fileBuffer = fileBuffer + "[MODIFIED_BY] = " + modifiedBy + "::"       
              fileBuffer = fileBuffer + "[MODIFIED_DATE] = " + modifiedDate + "::"
              fileBuffer = fileBuffer + "[IS_COMPLETED] = " + isCompleted + "::"
              fileBuffer = fileBuffer + "[IS_DELETED] = " + isDeleted + "::"
              fileBuffer = fileBuffer + "[IS_TEMPLATE] = " + isTemplate
              fileBuffer = fileBuffer + "\n"
              
              if flags != None:
                 if flags.get("fetchAccess") != None:   
                    activityAccessVector = AccessControlService.fetchAccess(activityHashtable)
                    if activityAccessVector == None:
                    	raise "ERROR: Activity: ID=" +  activityId + " NAME=" + name + " has no members defined for access!"
                    j = 0
                    activityAccessIterator = activityAccessVector.iterator()
                    while( activityAccessIterator.hasNext( )):
                       j = j + 1
                       activityAccessHashtable = activityAccessIterator.next( )
                       memberId = activityAccessHashtable.get("memberId")
                       memberRole = activityAccessHashtable.get("role")
                       fileBuffer = fileBuffer + "[ACL_ENTRY]::"
                       fileBuffer = fileBuffer + "[MEMBER_ID] = " + memberId + "::"
                       fileBuffer = fileBuffer + "[MEMBER_ROLE] = " + memberRole
                       fileBuffer = fileBuffer + "\n"
                           

              f.write( fileBuffer ) # note - no CR/LF on write (only on print)
       finally:
          f.flush() # or
          f.close()
       # If the program terminates here, the file has data
       return


#  Denise added this routine below 12/21/07.  
#  The only mbean that would use this routine is:  ActivityService.fetchActivitiesByID
   def writeOneActivityToFile(self, fileName, activityHashtable, flags):
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
       activityId   = activityHashtable.get("activityId")
       name         = activityHashtable.get("name")
       createdBy    = activityHashtable.get("createdBy")
       createdDate  = activityHashtable.get("createdDate")
       modifiedBy   = activityHashtable.get("modifiedBy")              
       modifiedDate = activityHashtable.get("modifiedDate")
       isCompleted  = activityHashtable.get("isCompleted")
       isDeleted    = activityHashtable.get("isDeleted")
       isTemplate   = activityHashtable.get("isTemplate")


       fileBuffer = "[ACTIVITY]::"
       fileBuffer = fileBuffer + "[ID] = " + activityId + "::"
       fileBuffer = fileBuffer + "[NAME] = " + name + "::"
       fileBuffer = fileBuffer + "[CREATED_BY] = " + createdBy + "::"
       fileBuffer = fileBuffer + "[CREATED_DATE] = " + createdDate + "::"
       fileBuffer = fileBuffer + "[MODIFIED_BY] = " + modifiedBy + "::"       
       fileBuffer = fileBuffer + "[MODIFIED_DATE] = " + modifiedDate + "::"
       fileBuffer = fileBuffer + "[IS_COMPLETED] = " + isCompleted + "::"
       fileBuffer = fileBuffer + "[IS_DELETED] = " + isDeleted + "::"
       fileBuffer = fileBuffer + "[IS_TEMPLATE] = " + isTemplate
       fileBuffer = fileBuffer + "\n"

       f.write( fileBuffer ) # note - no CR/LF on write (only on print)

       f.flush() # or
       f.close()
       # If the program terminates here, the file has data
       return


#  Denise added this new routine, 12/28/07
   def listAccessList(self, accessVector):
      global fErrorFile
      """ Iterate over vector of Access List members and print it from start to finish. """
      count = 0
      try:
         for item in accessVector:
                count = count + 1
                print "[%d] " % count, item
                if fErrorFile != None:
                   print >> fErrorFile, "[%d] " % count, item
      except java.lang.Exception, e:
         print "Exception: " + e
         print ""
      return count


#  Denise added this routine on 12/28/07
   def writeAccessListToFile(self, fileName, accessVector, flags):
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
          accessIterator = accessVector.iterator()
          while( accessIterator.hasNext( )):
              i = i + 1
              accessHashtable = accessIterator.next( )
             
              memberId     = accessHashtable.get("memberId")
              displayName  = accessHashtable.get("displayName")
              email        = accessHashtable.get("email")
              memberType   = accessHashtable.get("memberType")
              role         = accessHashtable.get("role")              
              
              fileBuffer = "[ACL_ENTRY]::"
              fileBuffer = fileBuffer + "[MEMBER_ID] = " + memberId + "::"
              fileBuffer = fileBuffer + "[DISPLAY_NAME] = " + displayName + "::"
              fileBuffer = fileBuffer + "[EMAIL] = " + email + "::"
              fileBuffer = fileBuffer + "[MEMBER_TYPE] = " + memberType + "::"
              fileBuffer = fileBuffer + "[ROLE] = " + role       
              fileBuffer = fileBuffer + "\n"

              f.write( fileBuffer ) # note - no CR/LF on write (only on print)
       finally:
           f.flush() # or
           f.close()
          # If the program terminates here, the file has data
       return

