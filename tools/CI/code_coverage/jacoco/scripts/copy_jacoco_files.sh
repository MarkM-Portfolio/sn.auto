#!/bin/bash

if [ "${ENABLE_CODE_COVERAGE}" != "true" ]; then
	echo "Code coverage is not enabled, so skipping code coverage file copying..."
	exit 0
fi

. ./server-info.properties
if [ $? != 0 ]; then
	echo "Failed to load server-info.properties."
	exit 1
fi

. ${CI_COMMON_HOME}/system.properties
if [ $? != 0 ]; then
	echo "Failed to load ${CI_COMMON_HOME}/system.properties."
	exit 1
fi
	
# Copy Jacoco jar to the server.
echo "Copying ${JACOCO_JAR_DIR}/jacocoagent_server.jar to ${WAS_HOME}/java/jre/lib/ext/jacocoagent.jar on ${WAS_HOST_FQDN}..."
scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no ${JACOCO_JAR_DIR}/jacocoagent_server.jar ${REMOTE_USER}@${WAS_HOST_FQDN}:${WAS_HOME}/java/jre/lib/ext/jacocoagent.jar
if [ $? != 0 ]; then
	echo "Could not copy ${JACOCO_JAR_DIR}/jacocoagent_server.jar to ${WAS_HOME}/java/jre/lib/ext/jacocoagent.jar on ${WAS_HOST_FQDN}."
	exit 1
fi

exit 0
