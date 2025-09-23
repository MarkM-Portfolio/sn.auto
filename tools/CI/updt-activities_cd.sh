#!/bin/sh

if [ -z "${BUILD_LABEL}" ]; then
	echo "\${BUILD_LABEL} is null."
	exit 1
fi

export BUILD_STREAM=`echo ${BUILD_LABEL} | cut -d '_' -f1`
export BUILD_COMPONENT=`echo ${BUILD_LABEL} | cut -d '_' -f2`

. ./ci.properties
if [ $? != 0 ]; then
	echo "Failed to load ci.properties."
	exit 1
fi

FE_ZIP_FILE=${COMPONENT_DAILY_BUILD_ROOT_DIR}/${BUILD_LABEL}/repository/${FE_NAME[0]}/fe.zip
echo "Uzipping ${FE_ZIP_FILE}..."
cd ${CI_HOME}/ear-deploy
unzip -o ${FE_ZIP_FILE}
if [ $? != 0 ]; then
	echo "Failed to unzip ${FE_ZIP_FILE}."
	exit 1
fi

EAR_FILE_PATH=${CI_HOME}/ear-deploy/lwp/${EAR_FILE}

DEPLOY_SCRIPT_NAME=updt-activities.py
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
