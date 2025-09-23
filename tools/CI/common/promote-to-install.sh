#!/bin/sh

echo "Loading ${CI_COMMON_HOME}/ci_functions.sh..."
. ${CI_COMMON_HOME}/ci_functions.sh
if [ $? != 0 ]; then
	echo "Failed to load ${CI_COMMON_HOME}/ci_functions.sh."
	exit 1
fi

. ${CI_COMMON_HOME}/system.properties
if [ $? != 0 ]; then
	echo "Failed to load ${CI_COMMON_HOME}/system.properties."
	exit 1
fi

echo "Writing ${BUILD_LABEL} to ${COMPONENT_DAILY_BUILD_ROOT_DIR}/BVTBuildLabel.txt..."
echo ${BUILD_LABEL} > ${COMPONENT_DAILY_BUILD_ROOT_DIR}/BVTBuildLabel.txt

exit 0
