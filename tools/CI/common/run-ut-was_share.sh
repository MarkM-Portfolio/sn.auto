#!/bin/sh

APPLICATION=$1
shift

# Create a workitem properties file
${CI_COMMON_HOME}/gen_workitem_params.sh assign_to_automation

. ${CI_COMMON_HOME}/init_build_env.sh
if [ $? != 0 ]; then
	echo "Failed to load ${CI_COMMON_HOME}/init_build_env.sh."
	exit 1
fi

case ${APPLICATION} in		
	
	Files)
		UT_URL[1]='https://${WAS_HOST_FQDN}:${WAS_PORT_SECURE}/files/test/JUnitServlet?asXml=true\&testSuite=com.ibm.lconn.share.platform.test.SharePlatformTestSuite\&runToLevel=basic'
		RESULTS_FILE[1]="TEST-Files-SharePlatformTestSuite.xml"
		
		UT_URL[2]='https://${WAS_HOST_FQDN}:${WAS_PORT_SECURE}/files/test/JUnitServlet?asXml=true\&testSuite=com.ibm.lconn.share.services.test.suite.FilesTestSuite\&runToLevel=basic\&serverProtocol=https\&serverHost=${WAS_HOST_FQDN}\&serverPort=${IHS_PORT_SECURE}'
		RESULTS_FILE[2]="TEST-Files-ShareServicesTestSuite.xml"

		UT_URL[3]='https://${WAS_HOST_FQDN}:${WAS_PORT_SECURE}/files/test/JUnitServlet?asXml=true\&testSuite=com.ibm.lconn.files.cmis.atompub.test.FvtTestSuite\&runToLevel=basic\&serverProtocol=https\&serverHost=${WAS_HOST_FQDN}\&serverPort=${IHS_PORT_SECURE}'
		RESULTS_FILE[3]="TEST-Files-CMISAtompubTestSuite.xml"
		;;

	Wikis)
		UT_URL[1]='https://${WAS_HOST_FQDN}:${WAS_PORT_SECURE}/wikis/test/JUnitServlet?asXml=true\&testSuite=com.ibm.lconn.share.platform.test.SharePlatformTestSuite\&runToLevel=basic'
		RESULTS_FILE[1]="TEST-Wikis-SharePlatformTestSuite.xml"
		
		UT_URL[2]='https://${WAS_HOST_FQDN}:${WAS_PORT_SECURE}/wikis/test/JUnitServlet?asXml=true\&testSuite=com.ibm.lconn.share.services.test.wiki.parser.WikiCreoleHtmlTestSuite\&runToLevel=basic'
		RESULTS_FILE[2]="TEST-Wikis-WikiCreoleHtmlTestSuite.xml"
		;;

	*) echo "Unknown application: ${APPLICATION}"
		;;
esac

# Create a directory for the xml results files. Make it mock a "src" tree so we can use the same JUnit reporting scripts as most other UTs use.
RESULTS_DIR="${WORKSPACE}/src/share/lwp/results"
if [ -d "${RESULTS_DIR}" ]; then
	echo "Removing ${RESULTS_DIR}..."
	rm -rf "${RESULTS_DIR}"
fi

echo "Creating ${RESULTS_DIR}..."
mkdir -p "${RESULTS_DIR}"

# Create a workitem properties file
${CI_COMMON_HOME}/gen_workitem_params.sh

URL_OK=true
NUM_URLS=${#UT_URL[@]}
for i in `seq 1 ${NUM_URLS}`
do
	eval echo "Hitting ${APPLICATION} UT URL: ${UT_URL[i]}..."
	eval curl --insecure "${UT_URL[i]}" --output "${RESULTS_DIR}/${RESULTS_FILE[i]}"
	if [ $? != 0 ]; then
		eval echo "Error trying to hit ${APPLICATION} UT URL: ${UT_URL[i]}."
		URL_OK=false
	fi

	grep "xml version" "${RESULTS_DIR}/${RESULTS_FILE[i]}" > /dev/null 2>&1
	if [ $? != 0 ]; then
		echo "Invalid test results xml file: ${RESULTS_DIR}/${RESULTS_FILE[i]}."
		URL_OK=false
	fi
done

cd ${CI_COMMON_HOME}
export CI_HOME="${WORKSPACE}"
export APPDIR_PRIMARY=share/lwp
export test_result_xml_dir=results
bld -f build_junit_report.xml ci.build.junitreport

# Capture the WAS login credentials and SystemOut logs.
${CI_COMMON_HOME}/get-was-info.sh

if [ "${URL_OK}" != "true" ]; then
	exit 1
fi

exit 0
