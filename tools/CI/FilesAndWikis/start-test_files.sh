#!/bin/sh
ALL_PASSED=true

echo "Hitting Share Platform Test Suite URL..."
curl --insecure https://${WAS_HOST_FQDN}:${WAS_PORT_SECURE}/files/test/JUnitServlet?asXml=true\&testSuite=com.ibm.lconn.share.platform.test.SharePlatformTestSuite --output testresult_SharePlatformTestSuite.xml
if [ $? != 0 ]; then
	echo "Error trying to hit Share Platform Test Suite URL."
	ALL_PASSED=false
fi

grep "xml version" testresult_SharePlatformTestSuite.xml > /dev/null 2>&1
if [ $? != 0 ]; then
	echo "Invalid test results xml file: testresult_SharePlatformTestSuite.xml."
	ALL_PASSED=false
fi

cp testresult_SharePlatformTestSuite.xml original_testresult_SharePlatformTestSuite.xml
${CI_HOME}/exclude-tests.sh ${CI_HOME}/testExcludes_Files_SharePlatformTestSuite.txt testresult_SharePlatformTestSuite.xml ${TMP}/testresult_SharePlatformTestSuite_modified.xml

echo "Hitting Files API Test Suite URL..."
curl --insecure https://${WAS_HOST_FQDN}:${WAS_PORT_SECURE}/files/test/JUnitServlet?asXml=true\&testSuite=com.ibm.lconn.share.services.test.suite.FilesTestSuite --output testresult_FilesAPITestSuite.xml
if [ $? != 0 ]; then
	echo "Error trying to hit Files API Test Suite URL."
	ALL_PASSED=false
fi

grep "xml version" testresult_FilesAPITestSuite.xml > /dev/null 2>&1
if [ $? != 0 ]; then
	echo "Invalid test results xml file: testresult_FilesAPITestSuite.xml."
	ALL_PASSED=false
fi

cp testresult_FilesAPITestSuite.xml original_testresult_FilesAPITestSuite.xml
${CI_HOME}/exclude-tests.sh ${CI_HOME}/testExcludes_Files_APITestSuite.txt testresult_FilesAPITestSuite.xml ${TMP}/testresult_FilesAPITestSuite_modified.xml

echo "Hitting CMIS Test Suite URL..."
curl --insecure https://${WAS_HOST_FQDN}:${WAS_PORT_SECURE}/files/test/JUnitServlet?asXml=true\&testSuite=com.ibm.lconn.files.cmis.atompub.test.FvtTestSuite --output testresult_CMISTestSuite.xml
if [ $? != 0 ]; then
	echo "Error trying to hit CMIS Test Suite URL."
	ALL_PASSED=false
fi

grep "xml version" testresult_CMISTestSuite.xml > /dev/null 2>&1
if [ $? != 0 ]; then
	echo "Invalid test results xml file: testresult_CMISTestSuite.xml."
	ALL_PASSED=false
fi

cp testresult_CMISTestSuite.xml original_testresult_CMISTestSuite.xml
${CI_HOME}/exclude-tests.sh ${CI_HOME}/testExcludes_Files_CMISTestSuite.txt testresult_CMISTestSuite.xml ${TMP}/testresult_CMISTestSuite_modified.xml

cd ${CI_HOME}
bld ci.build.report

if [ "${ALL_PASSED}" != "true" ]; then
	exit 1
fi

exit 0
