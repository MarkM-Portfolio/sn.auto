import sys, traceback
import os.environ
import re
import lcapp

applicationDirectory = sys.argv[0]
wasServer = sys.argv[1]
wasNode = sys.argv[2]

AdminApp.update("Communities", "app",
    [ '-operation', 'update', '-contents', applicationDirectory + "/communities.ear",
	'-server', wasServer, '-node', wasNode, '-defaultbinding.virtual.host', 'default_host',
	'-defaultbinding.datasource.jndi', 'jdbc/sncomm', '-defaultbinding.datasource.username', 'communitiesJAASAuth',
	'-BindJndiForEJBNonMessageBinding', 
	[['EventPublisher', 'EventPublisher', 'lc.events.publish.jar,META-INF/ejb-jar.xml',
	'ejb/connections/communities/events/publisher'],
	['FollowingEJB', 'Following', 'lc.following.ejb.jar,META-INF/ejb-jar.xml',  'ejb/connections/communities/following']],
	'-BindJndiForEJBMessageBinding',
	[['ConsumerEJB', 'EventSubscriber', 'lc.events.subscribe.jar,META-INF/ejb-jar.xml', '',
	'jms/connections/communities/events/consumer/as', 'jms/connections/communities/events/consumer/topic', 'connectionsAdmin'], 
	['Platform Command Consumer', 'PlatformCommandConsumerMDB', 'platformCommand.consumer.jar,META-INF/ejb-jar.xml', '',
	'jms/connections/communities/command/consumer/as', 'jms/connections/command/consumer/topic', 'connectionsAdmin']],
	'-MapRolesToUsers',
	[['admin', 'No', 'No', 'ajones2|ajones1', ''],
	['search-admin', 'No', 'No', 'wasadmin', ''], ['reader', 'Yes', 'No', '', ''], ['person', 'No', 'Yes', '', ''],
	['everyone', 'Yes', 'No', '', ''], ['dsx-admin', 'No', 'No', 'wasadmin', ''], ['widget-admin', 'No', 'No', 'wasadmin', ''],
	['global-moderator', 'No', 'No', 'wasadmin|ajones2', '']],
	'-MapEJBRefToEJB',
	[['Communities Web UI', '', 'comm.web.war,WEB-INF/web.xml', 'ejb/EventPublisher',
	'com.ibm.lconn.events.internal.publish.impl.JMSPublisherLocal', 'ejb/connections/communities/events/publisher']],
	'-MapResRefToEJB',
	[['Communities Web UI', '', 'comm.web.war,WEB-INF/web.xml', 'jdbc/sncomm', 'javax.sql.DataSource', 'jdbc/sncomm',
	'DefaultPrincipalMapping', 'communitiesJAASAuth'],
	['Communities Web UI', '', 'comm.web.war,WEB-INF/web.xml', 'wm/communitiesEventQueue',
	'com.ibm.websphere.asynchbeans.WorkManager', 'wm/communitiesEventQueue', '', ''],
	['Communities Web UI', '', 'comm.web.war,WEB-INF/web.xml', 'jms/catalogTCF', 'javax.jms.TopicConnectionFactory',
	'jms/connections/catalog/tcf', 'DefaultPrincipalMapping', 'connectionsAdmin']],
	'-usedefaultbindings'])

AdminConfig.save()

app = lcapp.Application("Communities")
app.restart()

# Need to restart Common.
app = lcapp.Application("Common")
app.restart()
