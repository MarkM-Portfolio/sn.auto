#!/bin/sh

BUILD_STREAM=$1
BUILD_COMPONENT=$2

REPOSITORY_URL=https://swgjazz.ibm.com:8001/jazz/
REPOSITORY_USER=icci@us.ibm.com

CI_COMMON_HOME=/local/ci/common

. ./ci.properties
if [ $? != 0 ]; then
	echo "Failed to load ci.properties."
	exit 1
fi

if [ -z "${CI_HOME}" ]; then
	echo "\${CI_HOME} is null."
	exit 1
fi

export SRC=${CI_SRC_DIR:-src}
export CI_SRC_HOME=${CI_HOME}/${SRC}
export COMPONENT_DAILY_BUILD_ROOT_DIR=/net/mlsa2/ibm/releng/workplace/dailybuilds/${BUILD_STREAM}_${BUILD_COMPONENT}
export CURRENT_BUILD_FILE=${COMPONENT_DAILY_BUILD_ROOT_DIR}/currentBuildLabel.txt
export BVT_BUILD_FILE=${COMPONENT_DAILY_BUILD_ROOT_DIR}/BVTBuildLabel.txt
export UNDER_TEST_BUILD_FILE=${CI_HOME}/underTestBuildLabel.txt

echo "CURRENT_BUILD_FILE: ${CURRENT_BUILD_FILE}"
echo "UNDER_TEST_BUILD_FILE: ${UNDER_TEST_BUILD_FILE}"
echo "BVT_BUILD_FILE: ${BVT_BUILD_FILE}"

if [ ! -f ${CURRENT_BUILD_FILE} ]; then
	echo "${CURRENT_BUILD_FILE} does not exist."
	exit 1
fi

if [ ! -f ${UNDER_TEST_BUILD_FILE} ]; then
	echo "${UNDER_TEST_BUILD_FILE} does not exist; creating one..."
	echo "This is the initial version of the file." > ${UNDER_TEST_BUILD_FILE}
fi

while :
do
	while :
	do
		echo "Looking for new build..."
		echo "Contents of ${CURRENT_BUILD_FILE}: `cat ${CURRENT_BUILD_FILE}`"
		echo "Contents of ${UNDER_TEST_BUILD_FILE}: `cat ${UNDER_TEST_BUILD_FILE}`"

		diff ${CURRENT_BUILD_FILE} ${UNDER_TEST_BUILD_FILE} > /dev/null 2>&1
		if [ $? != 0 ]; then
			echo "Found new build: `cat ${CURRENT_BUILD_FILE}`"
			cp ${CURRENT_BUILD_FILE} ${UNDER_TEST_BUILD_FILE}
			break
		fi
		
		echo "No new build, waiting 15 seconds..."
		sleep 15
	done

	export UNDER_TEST_BUILD=`cat ${UNDER_TEST_BUILD_FILE}`
	
	cd ${CI_COMMON_HOME}
	./create_build_request_cd.sh ${BUILD_STREAM} ${BUILD_COMPONENT}
done
