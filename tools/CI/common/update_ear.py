import sys, traceback
import os.environ
import re
import lcapp

applicationName = sys.argv[0]
applicationEarFilePath = sys.argv[1]
wasServer = sys.argv[2]
wasNode = sys.argv[3]

AdminApp.update(applicationName, "app",
    [ '-operation', 'update', '-contents', applicationEarFilePath,
	'-server', wasServer, '-node', wasNode ])
AdminConfig.save()


app = lcapp.Application(applicationName)
app.restart()
