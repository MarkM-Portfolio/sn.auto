#!/bin/sh

STATUS=OK
cd "${WORKSPACE}"

if [ -f "${WORKSPACE}/server-info.properties" ]; then
	echo "Loading server-info.properties..."
	. "${WORKSPACE}/server-info.properties"
	[ $? -eq 0 ] || echo "WARNING: Failed to load server-info.properties."
fi

# Delete existing SystemOut logs from workspace.
echo "Deleting existing SystemOut logs from ${WORKSPACE}..."
rm -f SystemOut*.log

# Copy the server's SystemOut and SystemErr logs to this project's workspace.
WAS_LOG_PATH="${WAS_HOME}/profiles/${WAS_PROFILE}/logs/${WAS_SERVER}"

# The following ssh command is to get by the "accept key" prompt" for new server pool machines that are put on-line.
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no ${REMOTE_USER}@${WAS_HOST_FQDN} echo "Accepting key if prompted..."

LOGBASENAMES="SystemOut SystemErr"
for LOGBASENAME in ${LOGBASENAMES}
do
	echo "Copying ${WAS_LOG_PATH}/${LOGBASENAME}*.log on ${WAS_HOST_FQDN} to ${WORKSPACE}..."
	scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no "${REMOTE_USER}@${WAS_HOST_FQDN}:${WAS_LOG_PATH}/${LOGBASENAME}*.log" "${WORKSPACE}"
	if [ $? != 0 ]; then
		echo "Could not copy ${WAS_LOG_PATH}/${LOGBASENAME}*.log on ${WAS_HOST_FQDN} to ${WORKSPACE}"
		STATUS=FAIL
	fi

	# Delete existing zip files.
	echo "Deleting existing ${LOGBASENAME} zip file..."
	rm -f ${LOGBASENAME}.zip

	# Compress the logs to save disk space.
	echo "Compressing ${LOGBASENAME} logs in ${WORKSPACE}..."
	zip ${LOGBASENAME}.zip ${LOGBASENAME}*.log
	if [ $? != 0 ]; then
		echo "Could not compress ${LOGBASENAME} logs in ${WORKSPACE}"
		STATUS=FAIL
	fi

	# Delete original SystemOut logs from workspace.
	echo "Deleting original ${LOGBASENAME} logs from ${WORKSPACE}..."
	rm -f ${LOGBASENAME}*.log
done

if [ ${STATUS} != OK ]; then
	exit 1
fi

exit 0
