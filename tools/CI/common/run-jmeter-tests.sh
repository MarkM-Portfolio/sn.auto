#!/bin/sh

fail_with_error() {
    echo "$@"
    exit 1
}

[ -n "${WAS_HOST_FQDN}" ] || fail_with_error "\${WAS_HOST_FQDN} is null."
. ${CI_COMMON_HOME}/ci_functions.sh || fail_with_error "Failed to load ${CI_COMMON_HOME}/ci_functions.sh."
. ${CI_COMMON_HOME}/system.properties || fail_with_error "Failed to load ${CI_COMMON_HOME}/system.properties."

echo "Creating clean directory tree ${WORKSPACE}/jmeter_workspace..."
rm -rf jmeter_workspace
mkdir -p jmeter_workspace/test_jar
mkdir -p jmeter_workspace/test_results/html

[ -d src ] && ( echo "Deleting source tree: `pwd`/src..."; rm -rf src )

case ${BUILD_COMPONENT} in
    News)
		echo "Downloading latest promoted Infra build..."
		${LCUPDATE_DIR}/bin/get-build.sh -f -s src -B ${BUILD_STREAM}_Infra || fail_with_error "Failed to download or unzip fe.zip."

		TEST_JAR_FILE=src/sn.infra/lwp/build/lc.integration.test/lib/lc.integration.test.jar
		echo "Unpacking test jar file ${TEST_JAR_FILE}..."
		unzip -o -q -d jmeter_workspace/test_jar ${TEST_JAR_FILE} || fail_with_error "Failed to unpack ${TEST_JAR_FILE}."

		TEST_CONFIG_FILE=jmeter_workspace/test_jar/META-INF/integration.suit.jmx
		TEST_REPORT_XSL_FILE=jmeter_workspace/test_jar/META-INF/report/jmeter-results-detail-report_21.xsl
		JMETER_RESULT_JTL_FILE=jmeter_workspace/test_results/integration.suit.jtl
		JMETER_RESULT_FILE=jmeter_workspace/test_results/integration.suit.result
		JMETER_RESULT_HTML_FILE=jmeter_workspace/test_results/html/integration.suit.html
		JMETER_LOG_FILE=jmeter_workspace/test_results/jmeter.log
		;;

	ExtensionsRegistry)
		echo "Downloading ${BUILD_LABEL}..."
		${LCUPDATE_DIR}/bin/get-build.sh -f -s src -b ${BUILD_LABEL} || fail_with_error "Failed to download or unzip fe.zip."

		TEST_CONFIG_FILE=src/sn.scee/lwp/build/ExtRegComp/lib/test/integration.suit.jmx
		TEST_REPORT_XSL_FILE=${CI_COMMON_HOME}/jmeter-results-detail-report_21.xsl
		JMETER_RESULT_JTL_FILE=jmeter_workspace/test_results/integration.suit.jtl
		JMETER_RESULT_FILE=jmeter_workspace/test_results/integration.suit.result
		JMETER_RESULT_HTML_FILE=jmeter_workspace/test_results/html/integration.suit.html
		JMETER_LOG_FILE=jmeter_workspace/test_results/jmeter.log
		;;
esac

# Create a workitem properties file
${CI_COMMON_HOME}/gen_workitem_params.sh

echo "Running the JMeter test suite..."
PATH=${JAVA_HOME}/bin:$PATH
WAS_ADMIN_PASSWORD=`ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no ${REMOTE_USER}@${WAS_HOST_FQDN} echo "\\${WAS_ADMIN_PASSWORD}"`
${JMETER_HOME}/bin/jmeter -n \
	-Jskip.jmeter.listeners=true \
	-Jserver.name=${WAS_HOST_FQDN} \
	-Jhttp.port=${WAS_PORT_NORMAL} \
	-Jhttps.port=${WAS_PORT_SECURE} \
	-Jsearch.admin.user=${WAS_ADMIN} \
	-Jsearch.admin.password=${WAS_ADMIN_PASSWORD} \
	-Jis.visitor.mode.enabled=false \
	-Jjmeter.result.file=${JMETER_RESULT_FILE} \
	-t ${TEST_CONFIG_FILE} \
	-l ${JMETER_RESULT_JTL_FILE} \
	-j ${JMETER_LOG_FILE} \
	|| fail_with_error "${JMETER_HOME}/bin/jmeter failed."

echo "Generating HTML report..."
xsltproc -o ${JMETER_RESULT_HTML_FILE} ${TEST_REPORT_XSL_FILE} ${JMETER_RESULT_JTL_FILE}

# Capture the WAS login credentials and SystemOut logs.
${CI_COMMON_HOME}/get-was-info.sh

exit 0
