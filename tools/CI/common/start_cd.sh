#!/bin/sh

export BUILD_STREAM=$1
export BUILD_COMPONENT=$2

export CI_COMMON_HOME=/local/ci/common

while :
do	
	echo "Starting CI for ${BUILD_STREAM}_${BUILD_COMPONENT}..."
	${CI_COMMON_HOME}/do-cd.sh
	if [ $? != 0 ]; then
		echo "CI failed for ${BUILD_STREAM}_${BUILD_COMPONENT}."
		continue
	fi
done
