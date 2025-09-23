#!/bin/sh

if [ -z "${BUILD_LABEL}" ]; then
	echo "\${BUILD_LABEL} is null."
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

echo "Updating lc-update scripts  on ${WAS_HOST_FQDN}..."
ssh ${REMOTE_USER}@${WAS_HOST_FQDN} "cd lc-update; git pull"
[ $? -eq 0 ] || fail_with_error "Unable to update lc-update scripts  on ${WAS_HOST_FQDN}"

echo "Updating build ${BUILD_LABEL} on ${WAS_HOST_FQDN}..."
ssh ${REMOTE_USER}@${WAS_HOST_FQDN} "cd lc-update; bin/update_component.sh -b ${BUILD_LABEL}"
[ $? -eq 0 ] || fail_with_error "Unable to update build ${BUILD_LABEL} on WAS."
