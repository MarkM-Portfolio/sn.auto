import sys, traceback
import os.environ
import re
import lcapp

applicationDirectory = sys.argv[0]
wasServer = sys.argv[1]
wasNode = sys.argv[2]

AdminApp.update("News", "app",
    [ '-operation', 'update', '-contents', applicationDirectory + "/news.ear",
	'-server', wasServer, '-node', wasNode, '-defaultbinding.virtual.host', 'default_host',
	'-defaultbinding.datasource.jndi', 'jdbc/news', '-defaultbinding.datasource.username',
	'newsJAASAuth',
	'-BindJndiForEJBNonMessageBinding',
	[['EventPublisher', 'EventPublisher', 'lc.events.publish.jar,META-INF/ejb-jar.xml',
	'ejb/connections/news/events/publisher'],
	['FollowingEJB', 'Following', 'lc.following.ejb.jar,META-INF/ejb-jar.xml',
	'ejb/connections/news/following'],
	['Spring container launcher', 'SpringContainerLauncherEjb',
	'news.spring.context.jar,META-INF/ejb-jar.xml', 'ejb/connections/news/spring'],
	['NewsStoryBean', 'NewsStoryEJBBean', 'news.ejb.jar,META-INF/ejb-jar.xml',
	'ejb/connections/news/stories']],
	'-BindJndiForEJBMessageBinding',
	[['Platform Command Consumer', 'PlatformCommandConsumerMDB',
	'platformCommand.consumer.jar,META-INF/ejb-jar.xml', '',
	'jms/connections/news/command/consumer/as', 'jms/connections/command/consumer/topic',
	'connectionsAdmin']],
	'-MapRolesToUsers',
	[['search-admin', 'No', 'No', 'wasadmin', ''], ['admin', 'No', 'No', 'ajones2|ajones1', ''],
	['reader', 'No', 'Yes', '', ''], ['person', 'No', 'Yes', '', ''],
	['everyone', 'Yes', 'No', '', '']],
	'-MapEJBRefToEJB',
	[['News Aggregation service', '', 'news.web.war,WEB-INF/web.xml',
	'ejb/EventPublisher', 'com.ibm.lconn.events.internal.publish.impl.JMSPublisherLocal',
	'ejb/connections/news/events/publisher']], '-usedefaultbindings'])

AdminConfig.save()

app = lcapp.Application("News")
app.restart()

# Need to restart Common.
app = lcapp.Application("Common")
app.restart()
