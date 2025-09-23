#!/bin/sh

exit 0

APP_LIST_FOR_CODE_COVERAGE="$1"

if [ -z "${BUILD_LABEL}" ]; then
	echo "\${BUILD_LABEL} is null."
	exit 1
fi

BUILD_STREAM=`echo ${BUILD_LABEL} | cut -d '_' -f1`
BUILD_COMPONENT=Infra

. ${CI_COMMON_HOME}/system.properties
if [ $? != 0 ]; then
	echo "Failed to load ${CI_COMMON_HOME}/system.properties."
	exit 1
fi

. ./server-info.properties
if [ $? != 0 ]; then
	echo "Failed to load server-info.properties."
	exit 1
fi

fail_with_error() {
    echo "$@"
    exit 1
}

INFRA_BUILD_LABEL=`cat ${COMPONENT_DAILY_BUILD_ROOT_DIR}/BVTBuildLabel.txt`

echo "Updating lc-update scripts  on ${WAS_HOST_FQDN}..."
ssh ${REMOTE_USER}@${WAS_HOST_FQDN} "cd lc-update; git pull"
[ $? -eq 0 ] || fail_with_error "Unable to update lc-update scripts  on ${WAS_HOST_FQDN}"

echo "Downloading base Connections build to ${WAS_HOST_FQDN}..."
ssh ${REMOTE_USER}@${WAS_HOST_FQDN} "cd ${REMOTE_LCUPDATE_DIR}; bin/get-build.sh -B IC10.0_Connections"
[ $? -eq 0 ] || fail_with_error "Failed download base Connections build to ${WAS_HOST_FQDN}."

echo "Downloading build [${INFRA_BUILD_LABEL}] to ${WAS_HOST_FQDN}..."
ssh ${REMOTE_USER}@${WAS_HOST_FQDN} "cd ${REMOTE_LCUPDATE_DIR}; bin/get-build.sh -f -x -b ${INFRA_BUILD_LABEL}"
[ $? -eq 0 ] || fail_with_error "Failed download build [${INFRA_BUILD_LABEL}] to ${WAS_HOST_FQDN}."

if [ "${ENABLE_CODE_COVERAGE}" == "true" -a -n "${APP_LIST_FOR_CODE_COVERAGE}" ]; then
    echo "ENABLE_CODE_COVERAGE is set to \"true\", instrumenting EAR file(s)..."
	${JACOCO_SCRIPT_DIR}/instrument_server.sh "${APP_LIST_FOR_CODE_COVERAGE}"
	[ $? -eq 0 ] || echo "Failed to instrument EAR file(s)."
fi

echo "Updating build ${INFRA_BUILD_LABEL} on ${WAS_HOST_FQDN}..."
ssh ${REMOTE_USER}@${WAS_HOST_FQDN} "cd ${REMOTE_LCUPDATE_DIR}; bin/update_build.sh -b ${INFRA_BUILD_LABEL}"
[ $? -eq 0 ] || fail_with_error "Unable to update build ${INFRA_BUILD_LABEL} on ${WAS_HOST_FQDN}."
