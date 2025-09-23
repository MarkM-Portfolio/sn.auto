#!/bin/sh

fail_with_error() {
    echo "$@"
    exit 1
}

# Create the workitem.properties file
${CI_COMMON_HOME}/gen_workitem_params.sh assign_to_automation

echo "Loading ${CI_COMMON_HOME}/ci_functions.sh..."
. ${CI_COMMON_HOME}/ci_functions.sh || fail_with_error "Failed to load ${CI_COMMON_HOME}/ci_functions.sh."

. ./server-info.properties || fail_with_error "Failed to load server-info.properties."

. ${CI_COMMON_HOME}/system.properties || fail_with_error "Failed to load ${CI_COMMON_HOME}/system.properties."

# Create a directory on the remote server to copy files.
echo "Creating directory \"${REMOTE_CI_HOME}\" on \"${WAS_HOST_FQDN}\"..."
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no ${REMOTE_USER}@${WAS_HOST_FQDN} mkdir -p ${REMOTE_CI_HOME}
if [ $? != 0 ]; then
	fail_with_error "Could not create directory \"${REMOTE_CI_HOME}\" on \"${WAS_HOST_FQDN}\"."
fi

FE_ZIP_FILE=${COMPONENT_DAILY_BUILD_ROOT_DIR}/${BUILD_LABEL}/repository/share/fe.zip

FILES_TO_COPY[1]="${FE_ZIP_FILE}"
NUM_FILES_TO_COPY=${#FILES_TO_COPY[@]}

# Copy the files to the server.
for i in `seq 1 $NUM_FILES_TO_COPY`
do
	echo "Copying ${FILES_TO_COPY[i]} to ${REMOTE_USER}@${WAS_HOST_FQDN}:${REMOTE_CI_HOME}..."
	scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no "${FILES_TO_COPY[i]}" ${REMOTE_USER}@${WAS_HOST_FQDN}:${REMOTE_CI_HOME}
	if [ $? != 0 ]; then
		fail_with_error "Failed to copy file."
	fi
done

# Unzip the fe zip file on the server.
echo "Unzipping ${REMOTE_CI_HOME}/fe.zip on ${WAS_HOST_FQDN}..."
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no ${REMOTE_USER}@${WAS_HOST_FQDN} "cd ${REMOTE_CI_HOME}; unzip -o -q fe.zip"
if [ $? != 0 ]; then
	fail_with_error "Could not unzip ${REMOTE_CI_HOME}/fe.zip on ${WAS_HOST_FQDN}."
fi

SHARE_UPDATE_HOME=${REMOTE_CI_HOME}/lwp/build/share.platform/ci
SHARE_UPDATE_SCRIPT=share_ci_update.sh
echo "Adding execute permissions to all shell scripts in ${SHARE_UPDATE_HOME} on ${WAS_HOST_FQDN}..."
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no ${REMOTE_USER}@${WAS_HOST_FQDN} "chmod +x ${SHARE_UPDATE_HOME}/*.sh"
if [ $? != 0 ]; then
	fail_with_error "Could not add execute permissions to shell scripts in ${SHARE_UPDATE_HOME} on ${WAS_HOST_FQDN}."
fi

# Update the server.
echo "Running ${SHARE_UPDATE_HOME}/${SHARE_UPDATE_SCRIPT} on ${WAS_HOST_FQDN}..."
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no ${REMOTE_USER}@${WAS_HOST_FQDN} "cd ${SHARE_UPDATE_HOME}; export CI_SRC=${SHARE_UPDATE_HOME}; ./${SHARE_UPDATE_SCRIPT}"
if [ $? != 0 ]; then
	fail_with_error "Could not run ${SHARE_UPDATE_HOME}/${SHARE_UPDATE_SCRIPT} on ${WAS_HOST_FQDN}."
fi

# Script has passed, so delete the workitem properties file
echo "Removing workitem.properties..."
rm -fv workitem.properties

exit 0
