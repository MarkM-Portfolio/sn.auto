#
#  Jython Script library for the List service module
#  (C) Copyright IBM Corporation, 2007
#
#  This script can be run from a .bat from the Windows command prompt, or a .sh on Linux shell.
#

import sys, java
from types import *
from java import lang
from java import util
from java import io


class ListServiceModule:
   def HelloList(self):
       print "Hello from Class ListServiceModule!"

   def filterActivitiesByField(self, input, fieldName, toMatch):
       from java.util.regex import Pattern
       from java.util.regex import Matcher
       from java.lang import String
       from java.util import Vector
       result = Vector()
       pat = Pattern.compile(toMatch)
       len = input.size();
       for i in range(len):
           act = input.elementAt(i)
           name = act.get(fieldName)
           nameStr = String(name)
           mat = pat.matcher(nameStr)
           if (mat.matches()):
               result.add(act)
       return result

   def filterActivitiesByCreator(self, input, toMatch):
       return self.filterActivitiesByField(input, "createdBy", toMatch)

   def filterActivitiesByName(self, input, toMatch):
       return self.filterActivitiesByField(input, "name", toMatch)

   def listActivitiesByName(self, input):
       len = input.size();
       for i in range(len):
           act = input.elementAt(i)
           name = act.get("name")
           print i, name
       return

   def listHashTable(self, table, depth):
       try:
          keys     = table.keys()
          elements = table.elements()
          i = 1
          while (elements.hasMoreElements()):
             key = keys.nextElement()
             val = elements.nextElement()
             for j in range(depth):
                 print "  ",
             if hasattr(val, "keys"):                  # then it's a Hashtable too, recurse on it
                print "Item [%d] Key : %s" % (i, key), # but only if it's not too deep
                if (depth < 3):
                   print "" # start a new line and recurse on the sub element
                   self.listHashTable(val, depth+1)
                else:
                   print "Value : %s" % (val)
             else:
                print "Item [%d] Key : %s  Value : %s" % (i, key, val)
             i = i + 1

       except KeyError:
          print "got KeyError exception"
       return

   def listOneMember(self, memberVector):
       print memberVector
#      {memberId=E9FG0A0A0A0A51C99CC54513C2179C00000B, displayName=William J. Arbuckle, staticProfile=false, externalId=91ae7240-8f0a-1028-82ca-db07163b51b2, email=arbuckle@us.ibm.com, memberType=person}
       try:
          elements = memberVector.elements()
          i = 1
          while (elements.hasMoreElements()):
             val = elements.nextElement()
             print "Item [%d] Value : %s" % (i, val)
             i = i + 1
       except :
          print "got Error exception"
       return

   def listVectorOfHashtables(self, theVector):
       try:
          elements = theVector.elements()
          i = 0
          while (elements.hasMoreElements()):
             val = elements.nextElement()
             ListService.listHashTable(val, 0)
             i = i + 1
       except :
          print "got Error exception"
       return


ListService=ListServiceModule()

