import sys, traceback
import os.environ
import re
import lcapp

applicationDirectory = sys.argv[0]
wasServer = sys.argv[1]
wasNode = sys.argv[2]

AdminApp.update("Profiles", "app",
    [ '-operation', 'update', '-contents', applicationDirectory + "/profiles.ear",
	'-server', wasServer, '-node', wasNode, '-defaultbinding.virtual.host', 'default_host',
	'-defaultbinding.datasource.jndi', 'jdbc/profiles', '-defaultbinding.datasource.username', 'profilesJAASAuth',
	'-BindJndiForEJBNonMessageBinding',
	[['EventPublisher', 'EventPublisher', 'lc.events.publish.jar,META-INF/ejb-jar.xml',
	'ejb/connections/profiles/events/publisher']],
	'-MapRolesToUsers',
	[['admin', 'No', 'No', 'ajones2|wasadmin|ajones1', ''],
	['search-admin', 'No', 'No', 'wasadmin', ''],
	['reader', 'Yes', 'No', '', ''],
	['person', 'No', 'Yes', '', ''],
	['everyone', 'Yes', 'No', '', ''],
	['dsx-admin', 'No', 'No', 'wasadmin', '']],
	'-MapEJBRefToEJB',
	[['Profiles', '', 'lc.profiles.app.war,WEB-INF/web.xml', 'ejb/EventPublisher',
	'com.ibm.lconn.events.internal.publish.impl.JMSPublisherLocal',
	'ejb/connections/profiles/events/publisher']],
	'-MapResRefToEJB',
	[['Profiles', '', 'lc.profiles.app.war,WEB-INF/web.xml', 'jdbc/profiles',
	'javax.sql.DataSource', 'jdbc/profiles', 'DefaultPrincipalMapping',
	'profilesJAASAuth']],
	'-usedefaultbindings'])

AdminConfig.save()

app = lcapp.Application("Profiles")
app.restart()

# Need to restart Common.
app = lcapp.Application("Common")
app.restart()
