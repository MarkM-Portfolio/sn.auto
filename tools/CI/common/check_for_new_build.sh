#!/bin/sh

STREAM=$1
COMPONENT=$2

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
CI_SRC_HOME=${CI_HOME}/${SRC}
COMPONENT_DAILY_BUILD_ROOT_DIR=/net/mlsa2/ibm/releng/workplace/dailybuilds/${STREAM}_${COMPONENT}
CURRENT_BUILD_FILE=${COMPONENT_DAILY_BUILD_ROOT_DIR}/currentBuildLabel.txt
BVT_BUILD_FILE=${COMPONENT_DAILY_BUILD_ROOT_DIR}/BVTBuildLabel.txt
UNDER_TEST_BUILD_FILE=${CI_HOME}/underTestBuildLabel.txt

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

	UNDER_TEST_BUILD=`cat ${UNDER_TEST_BUILD_FILE}`

	if [ -d ${CI_SRC_HOME} ]; then
		echo "Deleting source tree: ${CI_SRC_HOME}..."
		rm -rf ${CI_SRC_HOME}
	fi
	
	echo "Creating source directory: ${CI_SRC_HOME}..."
	mkdir ${CI_SRC_HOME}
	
	echo "Downloading build sources for ${UNDER_TEST_BUILD}..."
	export BUILD_DIR=/local/ci/common
	cd ${CI_COMMON_HOME}/wplctools
	perl JazzGetSourceFromBuildLabel.pl -buildLabel ${UNDER_TEST_BUILD} -url ${REPOSITORY_URL} -ru ${REPOSITORY_USER} -rpass ${CI_COMMON_HOME}/.vf -loadDir ${CI_SRC_HOME} -NoClean

	echo "Downloading FE zip file(s)..."
	# Need to get all the FEs for this component.
	echo "CI_SRC_HOME: ${CI_SRC_HOME}"
	for FE_PATH in ${CI_SRC_HOME}/*
	do
		echo "FE_PATH: $FE_PATH"
		FE_NAME=`basename ${FE_PATH}`
		echo "FE: ${FE_NAME}"
		FE_ZIP_FILE=${COMPONENT_DAILY_BUILD_ROOT_DIR}/${UNDER_TEST_BUILD}/repository/${FE_NAME}/fe.zip
		echo "Copying ${FE_ZIP_FILE} to ${CI_SRC_HOME}/${FE_NAME}..."
		cp ${FE_ZIP_FILE} ${CI_SRC_HOME}/${FE_NAME}
		cd ${CI_SRC_HOME}/${FE_NAME}
		unzip -o fe.zip
	done
	
	echo "Running CI..."
	cd ${CI_HOME}
	${CI_COMMON_HOME}/do-cd.sh
	if [ $? != 0 ]; then
		echo "CI failed."
		continue
	fi

	echo "CI passed; updating ${BVT_BUILD_FILE}..."
	cp ${UNDER_TEST_BUILD_FILE} ${BVT_BUILD_FILE}
done
