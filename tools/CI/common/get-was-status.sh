#!/bin/sh

cd "${WORKSPACE}"

if [ -f "${WORKSPACE}/server-info.properties" ]; then
	echo "Loading server-info.properties..."
	. "${WORKSPACE}/server-info.properties"
	[ $? -eq 0 ] || echo "WARNING: Failed to load server-info.properties."
fi

SERVER_STATUS_FILE=ServerStatus.txt

# Delete existing server status file ${SERVER_STATUS_FILENAME} from workspace.
echo "Deleting existing server status file ${SERVER_STATUS_FILENAME} from ${WORKSPACE}..."
rm -fv ${SERVER_STATUS_FILENAME}

# Get the server status file from server.
echo "Getting server status file ${REMOTE_CI_HOME}/${SERVER_STATUS_FILENAME} from ${WAS_HOST_FQDN}..."
scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no ${REMOTE_USER}@${WAS_HOST_FQDN}:${REMOTE_CI_HOME}/${SERVER_STATUS_FILENAME} "${WORKSPACE}"
if [ $? != 0 ]; then
	echo "Could not get server status file ${REMOTE_CI_HOME}/${SERVER_STATUS_FILENAME} from ${WAS_HOST_FQDN}."
	exit 1
fi

# Output the contents of the server status file so that it gets into the job's console log.
cat "${WORKSPACE}/${SERVER_STATUS_FILENAME}"

exit 0
