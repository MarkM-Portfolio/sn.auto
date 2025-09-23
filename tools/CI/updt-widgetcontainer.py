import sys, traceback
import os.environ
import re
import lcapp

applicationDirectory = sys.argv[0]
wasServer = sys.argv[1]
wasNode = sys.argv[2]

try:
	AdminApp.update("WidgetContainer", "app",
		[ '-operation', 'update',
		'-contents', applicationDirectory + "/widget.container.ear",
		'-server', wasServer, '-node', wasNode,
		'-defaultbinding.virtual.host', 'default_host',
		'-MapRolesToUsers', [['person', 'No', 'Yes', '', ''], ['everyone', 'Yes', 'No', '', '']],
		'-usedefaultbindings'])
	AdminConfig.save()

	app = lcapp.Application("WidgetContainer")
	app.restart()

	# Need to restart Common.
	app = lcapp.Application("Common")
	app.restart()
except Exception, errMsg:
	print "Exception received trying to update WidgetContainer ear file: %s\n" % (str(err))

sys.exit(0)
