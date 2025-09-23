#!/bin/sh

APPLICATION_NAME=$1
FE_NAME=$2
EAR_FILE=$3
DEPLOY_SCRIPT_NAME=${4:-'updt-ear.py'}

EAR_FILENAME=`basename ${EAR_FILE}`

. ./server-info.properties
if [ $? != 0 ]; then
	echo "Failed to load server-info.properties."
	exit 1
fi

if [ -z "${BUILD_LABEL}" ]; then
	echo "\${BUILD_LABEL} is null."
	exit 1
fi

export BUILD_STREAM=`echo ${BUILD_LABEL} | cut -d '_' -f1`
export BUILD_COMPONENT=`echo ${BUILD_LABEL} | cut -d '_' -f2`

. ${CI_COMMON_HOME}/system.properties
if [ $? != 0 ]; then
	echo "Failed to load ${CI_COMMON_HOME}/system.properties."
	exit 1
fi
	
# Now get the EAR file to deploy.
FE_ZIP_FILE=${COMPONENT_DAILY_BUILD_ROOT_DIR}/${BUILD_LABEL}/repository/${FE_NAME}/fe.zip
LOCAL_DEST_DIR="${WORKSPACE}/ear_deploy"

echo "Deleting directory ${LOCAL_DEST_DIR}..."
rm -rf "${LOCAL_DEST_DIR}"
	
echo "Creating directory ${LOCAL_DEST_DIR} to unzip ${FE_ZIP_FILE}..."
mkdir "${LOCAL_DEST_DIR}"
if [ $? != 0 ]; then
	echo "Failed to create directory: ${LOCAL_DEST_DIR}"
	exit 1
fi

cd "${LOCAL_DEST_DIR}"

echo "Unzipping ${FE_ZIP_FILE}..."
unzip -o -q ${FE_ZIP_FILE}
if [ $? != 0 ]; then
	echo "Failed to unzip ${FE_ZIP_FILE}."
	exit 1
fi

# Get back to workspace directory
cd -

# Create a directory on the remote WAS to copy files.
echo "Creating directory \"${REMOTE_CI_HOME}\" on \"${WAS_HOST_FQDN}\"..."
ssh ${REMOTE_USER}@${WAS_HOST_FQDN} mkdir -p ${REMOTE_CI_HOME}
if [ $? != 0 ]; then
	echo "Could not create directory \"${REMOTE_CI_HOME}\" on \"${WAS_HOST_FQDN}\"."
	exit 1
fi

EAR_FILE_PATH="${LOCAL_DEST_DIR}/lwp/${EAR_FILE}"
DEPLOY_SCRIPT_PATH=${CI_COMMON_HOME}/${DEPLOY_SCRIPT_NAME}

if [ "${ENABLE_CODE_COVERAGE}" == "true" ]; then
	APPLICATIONS_TO_INSTRUMENT=${APPLICATIONS_TO_INSTRUMENT:-${APPLICATION_NAME}}
    echo "ENABLE_CODE_COVERAGE is set to \"true\", so will be running with code coverage instrumentation."
	echo "Applications to instrument: ${APPLICATIONS_TO_INSTRUMENT}"
	${EMMA_SCRIPT_DIR}/instrument_for_code_coverage_server.sh "${EAR_FILE_PATH}" ${APPLICATIONS_TO_INSTRUMENT}
	if [ $? != 0 ]; then
		echo "Failed to instrument jar files for code coverage."
		exit 1
	fi
fi

FILES_TO_COPY[1]="${EAR_FILE_PATH}"
FILES_TO_COPY[2]=${DEPLOY_SCRIPT_PATH}
NUM_FILES_TO_COPY=${#FILES_TO_COPY[@]}

# Copy the files to the remote WAS.
for i in `seq 1 $NUM_FILES_TO_COPY`
do
	echo "Copying ${FILES_TO_COPY[i]} to ${REMOTE_USER}@${WAS_HOST_FQDN}:${REMOTE_CI_HOME}..."
	scp "${FILES_TO_COPY[i]}" ${REMOTE_USER}@${WAS_HOST_FQDN}:${REMOTE_CI_HOME}
	if [ $? != 0 ]; then
		echo "Failed to copy file."
		exit 1
	fi
done

# Execute the wsadmin command on the remote WAS to deploy the EAR.
CMD="${WAS_HOME}/profiles/${WAS_PROFILE}/bin/wsadmin.sh -lang jython -javaoption -Dpython.path=${REMOTE_LCUPDATE_DIR}/lib -wsadmin_classpath ${REMOTE_LCUPDATE_DIR}/lib/lccfg.jar -host localhost -f ${REMOTE_CI_HOME}/${DEPLOY_SCRIPT_NAME} ${APPLICATION_NAME} ${REMOTE_CI_HOME}/${EAR_FILENAME} ${WAS_SERVER} ${WAS_NODE} ${WAS_CELL} ${WAS_PROFILE} ${WAS_HOME}"
date
echo "Executing wsadmin on ${WAS_HOST_FQDN}..."
ssh ${REMOTE_USER}@${WAS_HOST_FQDN} ${CMD}
if [ $? != 0 ]; then
	echo "Failed to execute wsadmin command."
	date
	exit 1
fi
date
