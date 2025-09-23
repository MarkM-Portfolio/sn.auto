import sys, traceback
import os.environ
import re
import lcapp

applicationDirectory = sys.argv[0]
wasServer = sys.argv[1]
wasNode = sys.argv[2]

lcapp.set_wsadmin_refs((AdminConfig, AdminControl, AdminTask, AdminApp))


def set_was_variables(vars):
    cell = lcapp.Cell()
    for k,v in vars.items():
        wv = lcapp.WebSphereVariable(k, v)
        wv.parent = cell
        wv.set()

AdminApp.update("Files", "app",
	[ '-operation', 'update', '-contents', applicationDirectory + "/files.ear",
	'-server', wasServer, '-node', wasNode, '-defaultbinding.virtual.host', 'default_host',
	'-defaultbinding.datasource.jndi', 'jdbc/files', '-defaultbinding.datasource.username', 'filesJAASAuth',
	'-BindJndiForEJBNonMessageBinding',
	[['EventPublisher', 'EventPublisher', 'lc.events.publish.jar,META-INF/ejb-jar.xml', 'ejb/connections/files/events/publisher']],
	'-MapRolesToUsers', [['widget-admin', 'No', 'No', 'wasadmin', ''],
	['global-moderator', 'No', 'No', 'ajones2|wasadmin', ''],
	['everyone-authenticated', 'No', 'Yes', '', ''],
	['files-owner', 'No', 'Yes', '', ''],
	['person', 'No', 'Yes', '', ''],
	['reader', 'Yes', 'No', '', ''],
	['search-admin', 'No', 'No', 'wasadmin', ''],
	['admin', 'No', 'No', 'wasadmin|ajones1', ''],
	['everyone', 'Yes', 'No', '', '']],
	'-MapEJBRefToEJB', [['files.web.war', '', 'files.web.war,WEB-INF/web.xml', 'ejb/EventPublisher',
	'com.ibm.lconn.events.internal.publish.impl.JMSPublisherLocal', 'ejb/connections/files/events/publisher']],
	'-usedefaultbindings'])

app = lcapp.Application("Files")

AdminConfig.save()

app.restart()
