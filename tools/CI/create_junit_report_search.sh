#!/bin/sh

REPORT_DIR=${CI_HOME}/${SRC}/${APPDIR}/build/search.tests/reports/jreport
JUNIT_RESULTS_FILE=${REPORT_DIR}/TESTS-TestSuites.xml 
TEST_RESULTS_FILE=${REPORT_DIR}/TEST-com.ibm.connections.search.tests.TestSearch.xml 

# See if the JUnit results file already exists.
echo "Check for existance of JUnit results file: ${JUNIT_RESULTS_FILE}..."
if [ -f ${JUNIT_RESULTS_FILE} ]; then
	echo "Found ${JUNIT_RESULTS_FILE}; nothing to do."
	exit 0
fi

echo "Did not find ${JUNIT_RESULTS_FILE}."

# Didn't find JUnit results file, try to generate one from the test results file.
if [ ! -f ${TEST_RESULTS_FILE} ]; then
	echo "Test results file ${TEST_RESULTS_FILE} does not exist."
	echo "Cannot generate JUnit results file."
	exit 0
fi

# Try to generate ${JUNIT_RESULTS_FILE}.
cd ${CI_HOME}
bld -buildfile ${CI_HOME}/build_junit_report_search.xml ci.build.report

exit 0
