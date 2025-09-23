import sys, traceback
import os.environ
import re
import lcapp

applicationDirectory = sys.argv[0]
wasServer = sys.argv[1]
wasNode = sys.argv[2]

AdminApp.update("Activities", "app",
    [ '-operation', 'update', '-contents', applicationDirectory + "/oa.ear",
	'-server', wasServer, '-node', wasNode, '-defaultbinding.virtual.host', 'default_host',
	'-defaultbinding.datasource.jndi', 'jdbc/activities', '-defaultbinding.datasource.username', 'activitiesJAASAuth',
	'-BindJndiForEJBNonMessageBinding',
	[['EventPublisher', 'EventPublisher', 'lc.events.publish.jar,META-INF/ejb-jar.xml',
	'ejb/connections/activities/events/publisher'],
	['FollowingEJB', 'Following', 'lc.following.ejb.jar,META-INF/ejb-jar.xml', 'ejb/connections/activities/following']],
	'-BindJndiForEJBMessageBinding',
	[['Platform Command Consumer', 'PlatformCommandConsumerMDB', 'platformCommand.consumer.jar,META-INF/ejb-jar.xml', '',
	'jms/connections/activities/command/consumer/as', 'jms/connections/command/consumer/topic', 'connectionsAdmin']],
	'-MapRolesToUsers',
	[['search-admin', 'No', 'No', 'wasadmin', ''], ['reader', 'Yes', 'No', '', ''], ['person', 'No', 'Yes', '', ''],
	['everyone', 'Yes', 'No', '', ''], ['widget-admin', 'No', 'No', 'wasadmin', '']],
	'-MapEJBRefToEJB', [['Activities Web UI', '', 'oawebui.war,WEB-INF/web.xml', 'ejb/EventPublisher',
	'com.ibm.lconn.events.internal.publish.impl.JMSPublisherLocal', 'ejb/connections/activities/events/publisher']],
	'-MapResRefToEJB',
	[['Activities Web UI', '', 'oawebui.war,WEB-INF/web.xml', 'jdbc/activities', 'javax.sql.DataSource', 'jdbc/activities',
	'DefaultPrincipalMapping', 'activitiesJAASAuth']], '-usedefaultbindings'])

AdminConfig.save()

app = lcapp.Application("Activities")
app.restart()

# Need to restart Common.
app = lcapp.Application("Common")
app.restart()
