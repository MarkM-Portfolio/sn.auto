#!/bin/sh

WAS_HOST_CONFIG_FILE_PATH=$1
echo "Loading WAS host config file: ${WAS_HOST_CONFIG_FILE_PATH}..."
. ${WAS_HOST_CONFIG_FILE_PATH}

echo "Stopping WAS ${WAS_SERVER_NAME} on ${HOST}..."
CMD="${REMOTE_SCRIPT_HOME}/bin/was stop -user ${WAS_ADMIN} -password ${WAS_ADMIN_PASSWORD}"
sudo su - icci -c "ssh ${USER}@${HOST} ${CMD}"

echo "Killing WAS ${WAS_SERVER_NAME} on ${HOST}..."
CMD="${REMOTE_SCRIPT_HOME}/bin/was kill"
sudo su - icci -c "ssh ${USER}@${HOST} ${CMD}"
	
echo "Starting WAS ${WAS_SERVER_NAME} on ${HOST}..."
CMD="${WAS_BIN_DIR}/startServer.sh ${WAS_SERVER_NAME}"
sudo su - icci -c "ssh ${USER}@${HOST} ${CMD}"

exit 0
