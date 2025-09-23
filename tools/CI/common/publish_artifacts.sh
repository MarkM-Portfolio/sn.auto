#!/bin/sh

COMPONENT=$1
BUILD_DIR=$2
TEST_RESULTS_DIR=${CI_HOME}/${SRC}/${APPDIR}/${BUILD_DIR}
ZIP_FILE=${CI_HOME}/test-results.zip

if [ -f ${ZIP_FILE} ]; then
	echo "Removing existing zip file: ${ZIP_FILE}..."
	rm ${ZIP_FILE}
fi

cd ${TEST_RESULTS_DIR}
if [ "${COMPONENT}" == files -o "${COMPONENT}" == wikis ]; then
	echo "Zipping ${COMPONENT} test results files testresult_*.* in ${TEST_RESULTS_DIR}..."
	zip ${ZIP_FILE} original_testresult_*.*
	cd ${CI_HOME}
	zip ${ZIP_FILE} testExcludes_*.txt
else
	echo "Zipping ${COMPONENT} test results files TEST-*.* in ${TEST_RESULTS_DIR}..."
	zip ${ZIP_FILE} TEST-*.*
fi

cd ${CI_HOME}
echo "Publishing ${COMPONENT} artifacts to RTC..."
bld -buildfile ${CI_COMMON_HOME}/build_publish_artifacts.xml -lib /local/opt/IBM/jazz/buildsystem/buildtoolkit publish-artifacts

exit 0
