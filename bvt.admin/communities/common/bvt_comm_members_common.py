#  This is file:  bvt_comm_members_common.py
#
#  Jython Script library for the Communities service running on IBM Activities Enterprise Application
#  (C) Copyright IBM Corporation, 2007
#
#  This script can be run from a .bat from the Windows command prompt, or a .sh on Linux shell.
#
# AUTHOR:                yees@us.ibm.com
# INITIAL RELEASE:  LC2.5
# COMMENTS:            Support routines to process results.

import sys, java
from types import *
from java import lang
from java import util
from java import io


class BvtCommMembersCommon(BvtCommon):
   def __call__ (self, *args):
       return BvtMembersCommon(self, args)

   def initLibrary(self, logging):
      bvtCommon = BvtCommon()
      inited = BvtCommon.initLibrary(bvtCommon, logging)
      return inited

def compareMembers(goldMembers, comm1Members):
   print "Gold memberList = " + goldMembers.toString()
   print "Run memberList = " + comm1Members.toString()
   
   for i in range(comm1Members.size()):
      member = comm1Members[i]
      commMemberName = member[0]
      if goldMembers.containsKey(commMemberName):
          if (comm1Members[i][2] == goldMembers.get(commMemberName)):
             print commMemberName + " verified."
          else:
             printErrorMsg("==>Error! - " + commMemberName + "'s role is incorrect! " + goldMembers.get(commMemberName) + " in gold list but " + comm1Members[i][2] + " in actual community.")
             raise RuntimeException("memberList error!")
          goldMembers.remove(commMemberName)			 
      else:
          printErrorMsg("==>Error! - " + commMemberName + " is not in the gold list!")
          raise RuntimeException("memberList error!")

   if goldMembers.size() != 0:
      printErrorMsg("==>Error! - Gold list has more members than the actual community!  These are the member(s) not found:\n" + goldMembers.toString())
      raise RuntimeException("memberList error!")
   else:
      print "memberList check PASS."


