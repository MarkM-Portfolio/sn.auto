#!/bin/sh

COMPONENT=${1:-${APPLICATION}}
CI_PREBVT_TEST_NAME=${2:-${EAR_APP_NAME}}

echo "COMPONENT: $COMPONENT"
echo "CI_PREBVT_TEST_NAME: ${CI_PREBVT_TEST_NAME}"

CI_PREBVT_RESULTS_DIR=pre-bvt-ci-results/${CI_PREBVT_TEST_NAME}/${buildDefinitionId}-${buildLabel}
CI_PREBVT_HTTP_DEST_DIR=${HTTP_SERVER_HOME}/htdocs/${CI_PREBVT_RESULTS_DIR}

export NUM_TESTS_RUN=0
export NUM_TESTS_RUN_ORIG=0
export NUM_TESTS_RUN_RERUN=0
export NUM_FAILURES=0
export NUM_SKIPS=0
export PREBVT_RAN=true
OK=false

get_results(){
	NUM_TESTS_RUN=`grep 'Failures/Total Test Methods' api-bvt.log | awk -F: '{print $NF}' |  awk -F/ '{printf "%d\n", $2}'`
	NUM_FAILURES=`grep 'Failures/Total Test Methods' api-bvt.log | awk -F: '{print $NF}' |  awk -F/ '{printf "%d\n", $1}'`
	echo "NUM_TESTS_RUN: ${NUM_TESTS_RUN}"
	echo "NUM_FAILURES: ${NUM_FAILURES}"
}

finally(){
	${CI_COMMON_HOME}/send_prebvt_mail.sh api ${OK}
}

trap 'finally' EXIT

if [ -n "${errors_prebvt}" -a "${errors_prebvt}" != 0 ]; then
	echo "There were errors in setting up for the pre-bvt (API) tests, so not running them."
	PREBVT_RAN=false
	exit 0
fi

# Copy profiles property file into place.
cd ${CI_HOME}/pre-bvt/bvt.dist
echo "Copying ${CI_COMMON_HOME}/ProfileData_CI_PREBVT.properties to resources/ProfileData_${WAS_HOST}.properties..."
cp ${CI_COMMON_HOME}/ProfileData_CI_PREBVT.properties resources/ProfileData_${WAS_HOST}.properties

# Run the tests.
${JAVA_HOME}/bin/java -jar bvt.api.jar -server https://${WAS_HOST_FQDN}:${WAS_PORT_SECURE} -components ${COMPONENT} 2>&1 | tee api-bvt.log

#Check for failures.
# The end of the output will have a line like:
# INFO: *** End Test ==> Failures/Total Test Methods : 0/37

get_results
NUM_TESTS_RUN_ORIG=${NUM_TESTS_RUN}

# If there were any failures, rerun.
if [ "${NUM_FAILURES}" != 0 ]; then
	# There were failures, so try a re-run.
	echo "There were CI pre-bvt (API) failures"
	echo "Re-running pre-bvt (API) tests..."
	${JAVA_HOME}/bin/java -jar bvt.api.jar -server https://${WAS_HOST_FQDN}:${WAS_PORT_SECURE} -components ${COMPONENT} 2>&1 | tee api-bvt.log
	
	get_results
	NUM_TESTS_RUN_RERUN=${NUM_TESTS_RUN}
fi

if [ "${NUM_TESTS_RUN_ORIG}" != 0 -a "${NUM_FAILURES}" == 0 ]; then
	OK=true
fi

# Copy the log to the HTTP server area.
HTTP_DEST_DIR=${CI_PREBVT_HTTP_DEST_DIR}/api
RESULTS_DIR=${CI_PREBVT_RESULTS_DIR}/api

if [ ! -d ${HTTP_DEST_DIR} ]; then
	echo "Creating directory ${HTTP_DEST_DIR}..."
	mkdir -p ${HTTP_DEST_DIR}
fi

echo "Copying api-bvt.log to ${HTTP_DEST_DIR}/api-bvt.txt..."
cp api-bvt.log ${HTTP_DEST_DIR}/api-bvt.txt

exit 0

