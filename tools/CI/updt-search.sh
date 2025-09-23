#!/bin/sh

EAR_FILE_NAME=search.ear
EAR_FILE_PATH=${CI_HOME}/${SRC}/${APPDIR}/build/search.ear/ear.prod/lib/${EAR_FILE_NAME}

DEPLOY_SCRIPT_NAME=updt-search.py
DEPLOY_SCRIPT_PATH=${CI_HOME}/${DEPLOY_SCRIPT_NAME}

FILES_TO_COPY[1]=${EAR_FILE_PATH}
FILES_TO_COPY[2]=${DEPLOY_SCRIPT_PATH}
NUM_FILES_TO_COPY=${#FILES_TO_COPY[@]}

# Copy the files to the remote WAS.
for i in `seq 1 $NUM_FILES_TO_COPY`
do
	echo "Copying ${FILES_TO_COPY[i]} to ${REMOTE_USER}@${WAS_HOST_FQDN}:${REMOTE_USER_HOME}/ci/${APPLICATION}..."
	scp ${FILES_TO_COPY[i]} ${REMOTE_USER}@${WAS_HOST_FQDN}:${REMOTE_USER_HOME}/ci/${APPLICATION}
	if [ $? != 0 ]; then
		echo "Failed to copy file."
		exit 1
	fi
done

# Execute the wsadmin command on the remote WAS to deploy the EAR.
echo "Executing wsadmin on ${WAS_HOST_FQDN}..."
ssh ${REMOTE_USER}@${WAS_HOST_FQDN} ${REMOTE_USER_HOME}/lc-update/bin/was adm -f ${REMOTE_USER_HOME}/ci/${APPLICATION}/${DEPLOY_SCRIPT_NAME} ${REMOTE_USER_HOME}/ci/${APPLICATION} ${WAS_SERVER} ${WAS_NODE}
if [ $? != 0 ]; then
	echo "Failed to execute wsadmin command."
	exit 1
fi
