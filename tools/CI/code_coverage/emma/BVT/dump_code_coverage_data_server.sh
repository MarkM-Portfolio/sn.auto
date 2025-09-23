#!/bin/bash


if [[ "${ENABLE_CODE_COVERAGE}" != "true" ]]; then
	echo "ENABLE_CODE_COVERAGE not set to "true", so not gathering code coverage data."
	exit 0
fi

. "${CI_COMMON_HOME}/system.properties"
if [[ $? -ne 0 ]] ; then
    echo "ERROR - failed to load ${CI_COMMON_HOME}/system.properties."
    exit 1
fi

# Dump the runtime coverage data
echo "Dumping runtime code coverage data from ${WAS_HOST_FQDN}..."
COVERAGE_RUNTIME_DATA_FILE=coverage.ec
${JAVA_EXE} -cp ${EMMA_HOME}/emma.jar emma ctl -connect ${WAS_HOST_FQDN}:47653 -command coverage.get,${COVERAGE_RUNTIME_DATA_FILE}
if [[ $? -ne 0 ]] ; then
    echo "ERROR - failed to get runtime code coverage data from ${WAS_HOST_FQDN}."
    exit 1
fi

# Look for code coverage metadata files on the server.
echo "Getting code coverage metadata files from ${WAS_HOST_FQDN}..."
scp ${REMOTE_USER}@${WAS_HOST_FQDN}:${REMOTE_CI_HOME}/*.em .
if [[ $? -ne 0 ]] ; then
    echo "ERROR - failed to get code coverage metadata from ${WAS_HOST_FQDN}."
    exit 1
fi

# Generate code coverage xml report files.
ls -1 *.em | while read COVERAGE_METADATA_FILE
do
    COVERAGE_DATA_FILES="${COVERAGE_METADATA_FILE},${COVERAGE_RUNTIME_DATA_FILE}"
	APPLICATION_NAME=`echo ${COVERAGE_METADATA_FILE} | cut -d '.' -f1`
    echo "Application: ${APPLICATION_NAME}"
    ${JAVA_EXE} -cp ${EMMA_HOME}/emma.jar emma report -r xml -in "${COVERAGE_DATA_FILES}" -Dreport.xml.out.file=coverage_${APPLICATION_NAME}.xml
	if [[ $? -ne 0 ]] ; then
		echo "ERROR - failed to generate xml code coverage report."
		exit 1
	fi
done
exit 0
