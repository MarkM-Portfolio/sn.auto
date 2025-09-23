import sys, traceback
import os.environ
import re
import lcapp

applicationDirectory = sys.argv[0]
wasServer = sys.argv[1]
wasNode = sys.argv[2]

AdminApp.update("Homepage", "app",
    [ '-operation', 'update', '-contents', applicationDirectory + "/dboard.ear",
	'-server', wasServer, '-node', wasNode, '-defaultbinding.virtual.host', 'default_host',
	'-defaultbinding.datasource.jndi', 'jdbc/homepage', '-defaultbinding.datasource.username',
	'homepageJAASAuth', '-BindJndiForEJBNonMessageBinding',
	[['EventPublisher', 'EventPublisher', 'lc.events.publish.jar,META-INF/ejb-jar.xml',
	'ejb/connections/homepage/events/publisher']],
	'-BindJndiForEJBMessageBinding',
	[['ConsumerEJB', 'EventRecordConsumer', 'lc.events.consumer.jar,META-INF/ejb-jar.xml',
	'', 'jms/connections/homepage/events/inbound/as',
	'jms/connections/homepage/events/inbound/queue', 'connectionsAdmin']],
	'-MapRolesToUsers',
	[['admin', 'No', 'No', 'ajones1', ''], ['reader', 'No', 'Yes', '', ''], 
	['person', 'No', 'Yes', '', ''], ['everyone', 'Yes', 'No', '', '']],
	'-MapEJBRefToEJB',
	[['Homepage', '', 'homepage.war,WEB-INF/web.xml', 'ejb/EventPublisher',
	'com.ibm.lconn.events.internal.publish.impl.JMSPublisherLocal',
	'ejb/connections/homepage/events/publisher'],
	['Homepage', '', 'homepage.war, WEB-INF/web.xml', 'NewsStoryBean',
	'com.ibm.lconn.news.ejb.client.NewsStoryEJBBean', 'ejb/connections/news/stories']],
	'-MapResRefToEJB',
	[['Homepage', '', 'homepage.war,WEB-INF/web.xml', 'jdbc/homepage', 'javax.sql.DataSource',
	'jdbc/homepage', 'DefaultPrincipalMapping', 'homepageJAASAuth']], '-usedefaultbindings'])

AdminConfig.save()

app = lcapp.Application("Homepage")
app.restart()

# Need to restart Common.
app = lcapp.Application("Common")
app.restart()
