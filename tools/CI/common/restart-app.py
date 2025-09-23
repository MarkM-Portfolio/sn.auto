import sys, traceback
import os.environ
import re
import lcapp

applicationName = sys.argv[0]

app = lcapp.Application(applicationName)
app.restart()
