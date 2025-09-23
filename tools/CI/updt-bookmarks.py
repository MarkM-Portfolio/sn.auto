import sys, traceback
import os.environ
import re
import lcapp

applicationDirectory = sys.argv[0]
wasServer = sys.argv[1]
wasNode = sys.argv[2]

AdminApp.update("Dogear", "app",
    [ '-operation', 'update', '-contents', applicationDirectory + "/dogear.ear",
	'-server', wasServer, '-node', wasNode,
	'-defaultbinding.virtual.host', 'default_host',
	'-defaultbinding.datasource.jndi', 'jdbc/dogear',
	'-defaultbinding.datasource.username', 'dogearJAASAuth',
	'-BindJndiForEJBNonMessageBinding',
	[['EventPublisher', 'EventPublisher', 'lc.events.publish.jar,META-INF/ejb-jar.xml',
	'ejb/connections/dogear/events/publisher']],
	'-BindJndiForEJBMessageBinding',
	[['Platform Command Consumer', 'PlatformCommandConsumerMDB',
	'platformCommand.consumer.jar,META-INF/ejb-jar.xml', '',
	'jms/connections/bookmarks/command/consumer/as',
	'jms/connections/command/consumer/topic', 'connectionsAdmin']],
	'-MapRolesToUsers',
	[['search-admin', 'No', 'No', 'wasadmin', ''],
	['reader', 'Yes', 'No', '', ''],
	['person', 'No', 'Yes', '', ''],
	['everyone', 'Yes', 'No', '', '']],
	'-MapEJBRefToEJB',
	[['Dogear Application', '', 'dogear.webui.war,WEB-INF/web.xml', 'ejb/EventPublisher',
	'com.ibm.lconn.events.internal.publish.impl.JMSPublisherLocal',
	'ejb/connections/dogear/events/publisher']],
	'-MapResRefToEJB',
	[['Dogear Application', '', 'dogear.webui.war,WEB-INF/web.xml', 'jdbc/dogear',
	'javax.sql.DataSource', 'jdbc/dogear', 'DefaultPrincipalMapping', 'dogearJAASAuth']],
	'-usedefaultbindings'])

AdminConfig.save()

app = lcapp.Application("Dogear")
app.restart()

# Need to restart Common.
app = lcapp.Application("Common")
app.restart()

