#!/bin/sh
ALL_PASSED=true

echo "Hitting Share Platform Test Suite URL..."
curl --insecure https://${WAS_HOST_FQDN}:${WAS_PORT_SECURE}/wikis/test/JUnitServlet?asXml=true\&testSuite=com.ibm.lconn.share.platform.test.SharePlatformTestSuite --output testresult_SharePlatformTestSuite.xml
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
${CI_HOME}/exclude-tests.sh ${CI_HOME}/testExcludes_Wikis_SharePlatformTestSuite.txt testresult_SharePlatformTestSuite.xml ${TMP}/testresult_SharePlatformTestSuite_modified.xml

echo "Hitting All Creole Test Suite URL..."
curl --insecure https://${WAS_HOST_FQDN}:${WAS_PORT_SECURE}/wikis/test/JUnitServlet?asXml=true\&testSuite=com.ibm.lconn.share.services.test.wiki.parser.AllCreoleTests --output testresult_AllCreoleTests.xml
if [ $? != 0 ]; then
	echo "Error trying to hit All Creole Test Suite URL."
	ALL_PASSED=false
fi

grep "xml version" testresult_AllCreoleTests.xml > /dev/null 2>&1
if [ $? != 0 ]; then
	echo "Invalid test results xml file: testresult_AllCreoleTests.xml."
	ALL_PASSED=false
fi

cp testresult_AllCreoleTests.xml original_testresult_AllCreoleTests.xml
${CI_HOME}/exclude-tests.sh ${CI_HOME}/testExcludes_Wikis_AllCreoleTests.txt testresult_AllCreoleTests.xml ${TMP}/testresult_AllCreoleTests_modified.xml

echo "Hitting Wikis Test Suite URL..."
curl --insecure https://${WAS_HOST_FQDN}:${WAS_PORT_SECURE}/wikis/test/JUnitServlet?asXml=true\&testSuite=com.ibm.lconn.share.services.test.suite.WikisTestsSuite --output testresult_WikisTestsSuite.xml
if [ $? != 0 ]; then
	echo "Error trying to hit Wikis Test Suite URL."
	ALL_PASSED=false
fi

grep "xml version" testresult_WikisTestsSuite.xml > /dev/null 2>&1
if [ $? != 0 ]; then
	echo "Invalid test results xml file: testresult_WikisTestsSuite.xml."
	ALL_PASSED=false
fi

cp testresult_WikisTestsSuite.xml original_testresult_WikisTestsSuite.xml
${CI_HOME}/exclude-tests.sh ${CI_HOME}/testExcludes_Wikis_WikisTestsSuite.txt testresult_WikisTestsSuite.xml ${TMP}/testresult_WikisTestsSuite_modified.xml

cd ${CI_HOME}
bld ci.build.report

if [ "${ALL_PASSED}" != "true" ]; then
	exit 1
fi

exit 0
