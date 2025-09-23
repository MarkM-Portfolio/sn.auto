#!/bin/sh
ALL_PASSED=true

echo "Hitting Share Platform Test Suite URL..."
curl --insecure https://${WAS_HOST_FQDN}:${WAS_PORT_SECURE}/files/test/JUnitServlet?asXml=true\&testSuite=com.ibm.lconn.share.platform.test.SharePlatformTestSuite --output testresult_SharePlatformTestSuite.xml
if [ $? != 0 ]; then
	echo "Error trying to hit Share Platform Test Suite URL."
	ALL_PASSED=false
fi

echo "Hitting Files API Test Suite URL..."
curl --insecure https://${WAS_HOST_FQDN}:${WAS_PORT_SECURE}/files/test/JUnitServlet?asXml=true\&testSuite=com.ibm.lconn.share.services.test.suite.FilesTestSuite --output testresult_FilesAPITestSuite.xml
if [ $? != 0 ]; then
	echo "Error trying to hit Files API Test Suite URL."
	ALL_PASSED=false
fi

echo "Hitting CMIS Test Suite URL..."
curl --insecure https://${WAS_HOST_FQDN}:${WAS_PORT_SECURE}/files/test/JUnitServlet?asXml=true\&testSuite=com.ibm.lconn.files.cmis.atompub.test.FvtTestSuite --output testresult_CMISTestSuite.xml
if [ $? != 0 ]; then
	echo "Error trying to hit CMIS Test Suite URL."
	ALL_PASSED=false
fi

cd ${CI_HOME}
bld ci.build.report

if [ "${ALL_PASSED}" != "true" ]; then
	exit 1
fi

exit 0
