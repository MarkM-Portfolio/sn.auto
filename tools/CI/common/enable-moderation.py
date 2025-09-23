import sys, traceback
import os.environ
import re
from com.ibm.lc.install import XmlFile

wasServer = sys.argv[0]
wasNode = sys.argv[1]
wasCell = sys.argv[2]
wasProfile = sys.argv[3]
wasHome = sys.argv[4]

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
