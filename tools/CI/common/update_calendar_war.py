import sys, traceback
import os.environ
import re
import lcapp

localPathToModule = sys.argv[0]

AdminApp.update('Communities','modulefile', ['-operation','update','-contents',localPathToModule,'-contenturi','calendar.war'])
AdminConfig.save()

app = lcapp.Application("Communities")
app.restart()
