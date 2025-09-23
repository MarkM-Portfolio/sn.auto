# ***************************************************************** 
#                                                                   
# IBM Confidential                                                  
#                                                                   
# OCO Source Materials                                              
#                                                                   
# Copyright IBM Corp. 2011, 2013                                    
#                                                                   
# The source code for this program is not published or otherwise    
# divested of its trade secrets, irrespective of what has been      
# deposited with the U.S. Copyright Office.                         
#                                                                   
# ***************************************************************** 

#
# this is the script that add the Photo and Video media type to Connections
# Files application.
#

import sys, traceback
import os.environ
import re
import lcapp

for app in sys.argv:
    lcapp.Application(app).restart()

