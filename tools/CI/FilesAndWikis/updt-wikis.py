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

set_was_variables({'ENABLE_WIKIS_UNITTEST': 'true'})

AdminApp.update("Wikis", "app",
	[ '-operation', 'update', '-contents', applicationDirectory + "/wikis.ear",
	'-server', wasServer, '-node', wasNode, '-defaultbinding.virtual.host', 'default_host', 
	'-defaultbinding.datasource.jndi', 'jdbc/wikis', '-defaultbinding.datasource.username', 'wikisJAASAuth',
	'-BindJndiForEJBNonMessageBinding',
	[['EventPublisher', 'EventPublisher', 'lc.events.publish.jar,META-INF/ejb-jar.xml', 'ejb/connections/wikis/events/publisher']],
	'-MapRolesToUsers', [['widget-admin', 'No', 'No', 'aalain|wasadmin', 'adminGroup'],
	['everyone-authenticated', 'No', 'Yes', '', ''],
	['wiki-creator', 'No', 'Yes', '', ''],
	['person', 'No', 'Yes', '', ''], 
	['reader', 'Yes', 'No', '', ''],
	['search-admin', 'No', 'No', 'aalain|wasadmin', 'adminGroup'],
	['admin', 'No', 'No', 'aalain|wasadmin', 'adminGroup'],
	['everyone', 'Yes', 'No', '', '']],
	'-MapEJBRefToEJB', [['wikis.web.war', '', 'wikis.web.war,WEB-INF/web.xml', 'ejb/EventPublisher',
	'com.ibm.lconn.events.internal.publish.impl.JMSPublisherLocal', 'ejb/connections/wikis/events/publisher']],
	'-usedefaultbindings'])

#AdminConfig.save()

app = lcapp.Application("Wikis")

#file = os.environ.get('EAR_FILE')
#if file:
#    app.update(file)

app.addFile(applicationDirectory + '/ant-junit.jar', 'ant-junit.jar')
app.addFile(applicationDirectory + '/junit.jar', 'junit.jar')
app.addFile(applicationDirectory + '/lc.rest.test.jar', 'lc.rest.test.jar')
app.addFile(applicationDirectory + '/share.platform.test.jar', 'share.platform.test.jar')
app.addFile(applicationDirectory + '/share.services.test.jar', 'share.services.test.jar')
app.addFile(applicationDirectory + '/share.services.test.fvt.jar', 'share.services.test.fvt.jar')
app.addFile(applicationDirectory + '/files.web.test.jar', 'files.web.test.jar')
app.addFile(applicationDirectory + '/wikis.web.test.jar', 'wikis.web.test.jar')
app.addModule(applicationDirectory + "/share.test.war", "share.test.war")
app.setContextRootForWebModules("share.test.war",
    "share.test.war,WEB-INF/web.xml", "/wikis/test")

AdminConfig.save()

app.restart()
