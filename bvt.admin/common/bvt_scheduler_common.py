#  This is file:  bvt_scheduler_common.py
#
#  Jython Script library for the Activity service running on IBM Activities Enterprise Application
#  (C) Copyright IBM Corporation, 2008
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

class BvtSchedulerCommon(BvtCommon):
   def __call__ (self, *args):
       return BvtSchedulerCommon(self, args)

   def initLibrary(self, logging):
      bvtCommon = BvtCommon()
      inited = BvtCommon.initLibrary(bvtCommon, logging)
      return inited

  
   def listStringArray(self, schedStringArray):
  #  """ Iterate over array of Scheduled jobs and print it from start to finish. """
      count = 0
      if schedStringArray != None:
         if len(schedStringArray) >0:
            try:
               for entry in schedStringArray:
                   count = count + 1
                   print "[%d] " % count, entry
            except java.lang.Exception, e:
               print "Exception: " + e
      print ""
      return count

   def writeStringArrayToFile(self, fileName, schedStringArray, flags):
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

       if schedStringArray != None:
         if len(schedStringArray) >0:

       # if the program terminates here, the file is empty
           try:
               for entry in schedStringArray:
                   fileBuffer = "[SCHEDULED_JOB]::"
                   fileBuffer = fileBuffer + "[JOB_NAME] = " + entry
                   fileBuffer = fileBuffer + "\n"

                   f.write( fileBuffer ) # note - no CR/LF on write (only on print)
           finally:
              f.flush() # or
              f.close()
       # If the program terminates here, the file has data
       return


   def listOneJobHashtable(self, jobHashtable):
       from java.lang import String

       printBuffer = "[JOB_DETAILS]::"

       if jobHashtable.containsKey("name"):
          nextFireTime = jobHashtable.get("trigger0.nextFireTime")
          name         = jobHashtable.get("name")
          prevFireTime = jobHashtable.get("trigger0.previousFireTime")
          startTime    = jobHashtable.get("trigger0.startTime")
          triggerName  = jobHashtable.get("trigger0.triggerName")
              
          printBuffer = printBuffer + "[NAME] = " + (name) + "::"
          printBuffer = printBuffer + "[NEXT_FIRE_TIME] = " + (nextFireTime) + "::"
          printBuffer = printBuffer + "[START_TIME] = " + (startTime) + "::"
          printBuffer = printBuffer + "[TRIGGER_NAME] = " + (triggerName) + "::"
          
          if jobHashtable.containsKey("trigger0.previousFireTime"): 
             printBuffer = printBuffer + "[PREV_FIRE_TIME] = " + (prevFireTime)
          else: 
             printBuffer = printBuffer + "[PREV_FIRE_TIME] = "
          printBuffer = printBuffer + "\n"
      #else if jobHashtable.containsKey("CLFRA0327I: Status"):
       elif jobHashtable.containsKey("CLFRA0327I: Status"):
          status         = jobHashtable.get("CLFRA0327I: Status")
          printBuffer = printBuffer + "[STATUS] = " + (status)
          printBuffer = printBuffer + "\n"
       else:
          raise Exception, "Unexpected Status returned from scheduler MBean command!"

       print printBuffer
       return


   def writeOneJobHashtableToFile(self, fileName, jobHashtable, flags):
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

       fileBuffer = "[JOB_DETAILS]::"

       if jobHashtable.containsKey("name"):
          name         = jobHashtable.get("name")
          nextFireTime = jobHashtable.get("trigger0.nextFireTime")
          prevFireTime = jobHashtable.get("trigger0.previousFireTime")
          startTime    = jobHashtable.get("trigger0.startTime")
          triggerName  = jobHashtable.get("trigger0.triggerName")
   
          fileBuffer = fileBuffer + "[NAME] = " + (name) + "::"
          fileBuffer = fileBuffer + "[NEXT_FIRE_TIME] = " + (nextFireTime) + "::"
          fileBuffer = fileBuffer + "[START_TIME] = " + (startTime) + "::"
          fileBuffer = fileBuffer + "[TRIGGER_NAME] = " + (triggerName) + "::"
          if jobHashtable.containsKey("trigger0.previousFireTime"): 
             fileBuffer = fileBuffer + "[PREV_FIRE_TIME] = " + (prevFireTime)
          else: 
             fileBuffer = fileBuffer + "[PREV_FIRE_TIME] = "

          fileBuffer = fileBuffer + "\n"

       #else if jobHashtable.containsKey("CLFRA0327I: Status"):
       elif jobHashtable.containsKey("CLFRA0327I: Status"):
          status         = jobHashtable.get("CLFRA0327I: Status")
          fileBuffer = fileBuffer + "[STATUS] = " + (status)
          fileBuffer = fileBuffer + "\n"
       else:
          raise Exception, "Unexpected Status returned from scheduler MBean command!"

       f.write( fileBuffer ) # note - no CR/LF on write (only on print)

       f.flush() # or
       f.close()
       # If the program terminates here, the file has data
       return


   def writeStringToFile(self, fileName, schedString, flags):
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
          
       if schedString != None:
         if len(schedString) >0:

       # if the program terminates here, the file is empty
           try:
               # for entry in schedString:
                   fileBuffer = "[STRING RETURNED]::"
                   fileBuffer = fileBuffer + "[CMD_OUTPUT] = " + schedString
                   fileBuffer = fileBuffer + "\n"

                   f.write( fileBuffer ) # note - no CR/LF on write (only on print)
           finally:
              f.flush() # or
              f.close()
       # If the program terminates here, the file has data
       return

