#  This is file:  bvt_stats_common.py
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

class BvtStatsCommon(BvtCommon):
   def __call__ (self, *args):
       return BvtStatsCommon(self, args)

   def initLibrary(self, logging):
      bvtCommon = BvtCommon()
      inited = BvtCommon.initLibrary(bvtCommon, logging)
      return inited

   
   def listOneStat(self, statHashtable):
       from java.lang import String
       invocations  =  statHashtable.get("invocations")
       total   = statHashtable.get("total")
       minimum = statHashtable.get("minimum")
       maximum = statHashtable.get("maximum")
       average = statHashtable.get("average")
       
       printBuffer = "[STATISTIC]::"
       printBuffer = printBuffer + "[INVOCATIONS] = " + str(invocations) + "::"
       printBuffer = printBuffer + "[TOTAL] = " + str(total) + "::"
       printBuffer = printBuffer + "[MINIMUM] = " + str(minimum) + "::"
       printBuffer = printBuffer + "[MAXIMUM] = " + str(maximum) + "::"
       printBuffer = printBuffer + "[AVERAGE] = " + str(average)
       printBuffer = printBuffer + "\n"

       print printBuffer
       return


   def writeOneStatToFile(self, fileName, statHashtable, flags):
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
       invocations  =  statHashtable.get("invocations")
       total   = statHashtable.get("total")
       minimum = statHashtable.get("minimum")
       maximum = statHashtable.get("maximum")
       average = statHashtable.get("average")

       fileBuffer = "[STATISTIC]::"
       fileBuffer = fileBuffer + "[INVOCATIONS] = " + str(invocations) + "::"
       fileBuffer = fileBuffer + "[TOTAL] = " + str(total) + "::"
       fileBuffer = fileBuffer + "[MINIMUM] = " + str(minimum) + "::"
       fileBuffer = fileBuffer + "[MAXIMUM] = " + str(maximum) + "::"
       fileBuffer = fileBuffer + "[AVERAGE] = " + str(average)
       fileBuffer = fileBuffer + "\n"

       f.write( fileBuffer ) # note - no CR/LF on write (only on print)

       f.flush() # or
       f.close()
       # If the program terminates here, the file has data
       return

#  ****************** Stuff below here needs work! ******************


#  This won't work - because it's not a vector! it's a hashtable
   def listAllStats(self, statsVector):
#      """ Iterate over vector of Statistics and print it from start to finish. """
      count = 0
      if statsVector != None:
         if len(statsVector) >0:
            try:
               for entry in statsVector:
                   count = count + 1
                   print "[%d] " % count, entry
#                  listOneMember( entry ):
            except java.lang.Exception, e:
               print "Exception: " + e
      print ""
      return count

 

# We need to figure out how to print all the stats to a file!!!  Need Tom's help.  It's a hashtable; not vector
   def writeMembersToFile(self, fileName, memberVector, flags):
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

          
              fileBuffer = "[MEMBER]::"
              fileBuffer = fileBuffer + "[ID] = " + memberId + "::"
              fileBuffer = fileBuffer + "[DISPLAYNAME] = " + displayName + "::"
              fileBuffer = fileBuffer + "[STATIC_PROFILE] = " + staticProfile + "::"
              fileBuffer = fileBuffer + "[EXTERNAL_ID] = " + externalId + "::"
              fileBuffer = fileBuffer + "[EMAIL] = " + email
              fileBuffer = fileBuffer + "[MEMBER_TYPE] = " + memberType + "::"
              fileBuffer = fileBuffer + "\n"

              f.write( fileBuffer ) # note - no CR/LF on write (only on print)
       finally:
          f.flush() # or
          f.close()
       # If the program terminates here, the file has data
       return


       

