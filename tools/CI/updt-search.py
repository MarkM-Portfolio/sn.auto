import sys, traceback
import os.environ
import re
import lcapp

applicationDirectory = sys.argv[0]
wasServer = sys.argv[1]
wasNode = sys.argv[2]

AdminApp.update("Search", "app",
    [ '-operation', 'update', '-contents', applicationDirectory + "/search.ear",
	'-server', wasServer, '-node', wasNode, '-defaultbinding.virtual.host', 'default_host',
	'-defaultbinding.datasource.jndi', 'jdbc/search',
	'-defaultbinding.datasource.username', 'searchJAASAuth',
	'-BindJndiForEJBNonMessageBinding',
	[['EventPublisher', 'EventPublisher', 'lc.events.publish.jar,META-INF/ejb-jar.xml',
	'ejb/connections/search/events/publisher']],
	'-BindJndiForEJBMessageBinding',
	[['search.indexer', 'IndexTopicMDB', 'dboard.search.ejb.jar,META-INF/ejb.jar.xml', '',
	'jms/connections/search/as', 'jms/connections/search/topic', 'connectionsAdmin']],
	'-MapRolesToUsers',
	[['admin', 'No', 'No', 'ajones2|wasadmin|ajones1', ''],
	['reader', 'Yes', 'No', '', ''],
	['person', 'No', 'Yes', '', ''],
	['everyone', 'Yes', 'No', '', '']],
	'-MapRunAsRolesToUsers',
	[['admin', 'wasadmin', 'lcsecret']],
	'-MapEJBRefToEJB',
	[['Search', '', 'search.war,WEB-INF/web.xml', 'ejb/EventPublisher',
	'com.ibm.lconn.events.internal.publish.impl.JMSPublisherLocal',
	'ejb/connections/search/events/publisher']],
	'-MapResEnvRefToRes',
	[['Search', '', 'search.war,WEB-INF/web.xml', 'jdbc/search', 'javax.sql.DataSource',
	'jdbc/search', 'searchJAASAuth']],
	'-MapResRefToEJB',
	[['search.indexer', 'ScheduleStartup', 'dboard.search.ejb.jar,META-INF/ejb.jar.xml',
	'jdbc/search', 'javax.sql.DataSource', 'jdbc/search', 'DefaultPrincipalMapping',
	'searchJAASAuth'], ['search.indexer', 'IndexTopicMDB',
	'dboard.search.ejb.jar,META-INF/ejb.jar.xml', 'jms/connectionsSearchTopic',
	'javax.jms.Topic', 'jms/connections/search/topic'],
	['search.indexer', 'IndexTopicMDB', 'dboard.search.ejb.jar, META-INF/ejb.jar.xml',
	'jms/connectionsSearchTCF', 'javax.jms.TopicConnectionFactory',
	'jms/connections/search/tcf', 'DefaultPrincipalMapping', 'searchJAASAuth']],
	'-usedefaultbindings'])

AdminConfig.save()

app = lcapp.Application("Search")
app.restart()

# Need to restart Common.
app = lcapp.Application("Common")
app.restart()
