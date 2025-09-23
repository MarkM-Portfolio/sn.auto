#!/bin/sh

. ./server-info.properties
if [ $? != 0 ]; then
	echo "Failed to load server-info.properties."
	exit 1
fi

echo "Stopping WAS ${WAS_SERVER} on ${WAS_HOST_FQDN}..."
ssh ${REMOTE_USER}@${WAS_HOST_FQDN} ${WAS_HOME}/profiles/${WAS_PROFILE}/bin/stopServer.sh ${WAS_SERVER} -username ${WAS_ADMIN} -password \${WAS_ADMIN_PASSWORD}

echo "Killing WAS ${WAS_SERVER} on ${WAS_HOST_FQDN}..."
CMD="${REMOTE_LCUPDATE_DIR}/bin/was kill"
ssh ${REMOTE_USER}@${WAS_HOST_FQDN} ${CMD}
	
echo "Starting WAS ${WAS_SERVER} on ${WAS_HOST_FQDN}..."
ssh ${REMOTE_USER}@${WAS_HOST_FQDN} ${WAS_HOME}/profiles/${WAS_PROFILE}/bin/startServer.sh ${WAS_SERVER}

exit 0
