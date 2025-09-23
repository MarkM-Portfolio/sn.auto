import sys, traceback
import os.environ
import re
import lcapp

applicationDirectory = sys.argv[0]
wasServer = sys.argv[1]
wasNode = sys.argv[2]

AdminApp.update("Metrics", "app",
    [ '-operation', 'update', '-contents', applicationDirectory + '/metricsTest.ear',
    '-server', wasServer, '-node', wasNode, '-defaultbinding.virtual.host', 'default_host',
    '-BindJndiForEJBMessageBinding',
    [['ConsumerEJB', 'EventSubscriber', 'lc.events.subscribe.jar,META-INF/ejb-jar.xml', '',
    'jms/connections/metrics/events/consumer/as', 'jms/connections/metrics/events/consumer/topic', 'connectionsAdmin'],
    ['Platform Command Consumer', 'PlatformCommandConsumerMDB', 'platformCommand.consumer.jar,META-INF/ejb-jar.xml', '',
    'jms/connections/metrics/command/consumer/as', 'jms/connections/command/consumer/topic', 'connectionsAdmin']],
    '-MapRolesToUsers',
    [['metrics-report-run', 'No', 'No', 'ajones1', ''], ['admin', 'No', 'No', 'ajones1', ''], ['reader', 'Yes', 'No', 'ajones1', ''],
    ['person', 'No', 'Yes', '', ''], ['everyone', 'Yes', 'No', '', '']],
	'-MapModulesToServers',
	[[ 'lc.metrics.test.war', 'lc.metrics.test.war,WEB-INF/web.xml', 'WebSphere:cell=connciwasNode01Cell,node=connciwasNode01,server=server1' ]],
    '-deployejb.dbtype', 'DB2UDB_V95', '-usedefaultbindings'])
 
app_id = AdminConfig.getid("/Deployment:Metrics/")
deployed_obj = AdminConfig.showAttribute(app_id, 'deployedObject')
attrs = []
attrs.append(['classloader', [['mode', 'PARENT_LAST']]])
AdminConfig.modify(deployed_obj, attrs)

AdminConfig.save()

app = lcapp.Application("Metrics")
app.restart()

# Need to restart Common.
app = lcapp.Application("Common")
app.restart()
