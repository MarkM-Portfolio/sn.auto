import sys, traceback
import os.environ
import re
import lcapp
from com.ibm.lc.install import XmlFile

applicationName = sys.argv[0]
applicationEarFilePath = sys.argv[1]
wasServer = sys.argv[2]
wasNode = sys.argv[3]
wasCell = sys.argv[4]
wasProfile = sys.argv[5]
wasHome = sys.argv[6]

AdminApp.update(applicationName, "app",
    [ '-operation', 'update', '-contents', applicationEarFilePath,
	'-server', wasServer, '-node', wasNode, '-defaultbinding.virtual.host', 'default_host',
	'-MapRolesToUsers', 
	[['reader', 'Yes', 'No', '', ''],
	['person', 'No', 'Yes', '', ''],
	['global-moderator', 'No', 'No', 'fvt admin|ajones2|ajones1', '']],
	'-usedefaultbindings'])
AdminConfig.save()

xmlFilePath = wasHome + '/profiles/' + wasProfile + '/config/cells/' + wasCell + '/LotusConnections-config/contentreview-config.xml'
f = XmlFile(xmlFilePath)

print "Modifying %s to enable Moderation..." % (xmlFilePath)

xpaths = [
    '/config/serviceConfiguration/service[@id="blogs"]/contentApproval/@enabled',
    '/config/serviceConfiguration/service[@id="blogs"]/contentApproval/ownerModerate/@enabled',
    '/config/serviceConfiguration/service[@id="blogs"]/contentFlagging/@enabled',
    '/config/serviceConfiguration/service[@id="blogs"]/contentFlagging/issueCategorization/@enabled',
    '/config/serviceConfiguration/service[@id="files"]/contentApproval/@enabled',
    '/config/serviceConfiguration/service[@id="files"]/contentApproval/ownerModerate/@enabled',
    '/config/serviceConfiguration/service[@id="files"]/contentFlagging/@enabled',
    '/config/serviceConfiguration/service[@id="files"]/contentFlagging/ownerModerate/@enabled',
    '/config/serviceConfiguration/service[@id="forums"]/contentApproval/@enabled',
    '/config/serviceConfiguration/service[@id="forums"]/contentApproval/ownerModerate/@enabled',
    '/config/serviceConfiguration/service[@id="forums"]/contentFlagging/@enabled',
    '/config/serviceConfiguration/service[@id="forums"]/contentFlagging/ownerModerate/@enabled',
    '/config/serviceConfiguration/service[@id="forums"]/contentFlagging/issueCategorization/@enabled']
for p in xpaths:
    f.modify(p, 'true')
f.save()

#  Will be restarting WAS, so no need to restart the app here.
