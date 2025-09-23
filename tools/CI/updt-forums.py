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

set_was_variables({'ENABLE_FORUMS_UNITTEST': 'true'})

app = lcapp.Application(os.environ.get('EAR_APP_NAME', "Forums"))

#app.update(os.environ.get('EAR_FILE', ''))

AdminApp.update("Forums", "app",
    [ '-operation', 'update', '-contents', applicationDirectory + "/forums.ear",
      '-server', wasServer, '-node', wasNode, '-defaultbinding.virtual.host', 'default_host', '-defaultbinding.datasource.jndi', 'jdbc/forum', '-defaultbinding.datasource.username', 'forumsJAASAuth', '-BindJndiForEJBNonMessageBinding', [['EventPublisher', 'EventPublisher', 'lc.events.publish.jar,META-INF/ejb-jar.xml', 'ejb/connections/forums/events/publisher'], ['FollowingEJB', 'Following', 'lc.following.ejb.jar,META-INF/ejb-jar.xml', 'ejb/connections/forums/following']], '-BindJndiForEJBMessageBinding', [['Platform Command Consumer', 'PlatformCommandConsumerMDB', 'platformCommand.consumer.jar,META-INF/ejb-jar.xml', '', 'jms/connections/forums/command/consumer/as', 'jms/connections/command/consumer/topic', 'connectionsAdmin']], '-MapRolesToUsers', [['widget-admin', 'No', 'No', 'wasadmin', ''], ['global-moderator', 'No', 'No', 'ajones2|wasadmin', ''], ['discussThis-user', 'Yes', 'No', '', ''], ['metrics-reader', 'Yes', 'No', '', ''], ['person', 'No', 'Yes', '', ''], ['reader', 'Yes', 'No', '', ''], ['search-admin', 'No', 'No', 'wasadmin', ''], ['admin', 'No', 'No', 'ajones2|ajones1', ''], ['everyone', 'Yes', 'No', '', '']], '-MapEJBRefToEJB', [['Discussion Forum Web UI', '', 'forum.web.war,WEB-INF/web.xml', 'ejb/EventPublisher', 'com.ibm.lconn.events.internal.publish.impl.JMSPublisherLocal', 'ejb/connections/forums/events/publisher']], '-MapResRefToEJB', [['Discussion Forum Web UI', '', 'forum.web.war,WEB-INF/web.xml', 'jdbc/forum', 'javax.sql.DataSource', 'jdbc/forum', 'DefaultPrincipalMapping', 'forumsJAASAuth']], '-usedefaultbindings'])

AdminConfig.save()

app.restart()

