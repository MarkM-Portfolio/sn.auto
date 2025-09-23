#!/bin/bash

APP_LIST="$1"
IS_PARTNERED_APP=$2

# Create a local tmp directory to keep the Jenkins Jacoco plug-in happy.
# It wants to know where your sources are and there is no way to tell it you
# don't have any. So I point it to an empty directory.
mkdir tmp
mkdir tmp_jacoco

# Remove any existing coverage data.
echo "Removing any existing coverage data..."
rm -fv *.exec
rm -fv jars_original/*.jar
mkdir jars_original

if [ "${ENABLE_CODE_COVERAGE}" != "true" ]; then
	echo "Code coverage is not enabled, so skipping code coverage dump..."
	exit 0
fi

. ${CI_COMMON_HOME}/system.properties
if [ $? != 0 ]; then
	echo "Failed to load ${CI_COMMON_HOME}/system.properties."
	exit 1
fi

if [ -f server-info.properties ]; then
	echo "Loading server-info.properties.."
	. ./server-info.properties
	if [ $? != 0 ]; then
		echo "Failed to load server-info.properties."
		exit 1
	fi
	export WAS_HOST_FQDN=${WAS_HOST_FQDN}
fi
	
if [ "${IS_PARTNERED_APP}" != "true" ]; then
	# Dump the code coverage data from the server and copy it back to the server
	# so it will be available for a partnered component (like Files/wikis and HP/News).
	# Need to do this since the Jacoco remote dump command will reset the run-time coverage data.
	# We will use the same .exec file for 2 components.
	echo "Dumping code coverage data from server..."
	${ANT_EXE} -v -lib ${JACOCO_JAR_DIR} -f ${JACOCO_BUILDFILE_DIR}/build_jacoco.xml dump
	

	echo "Copying remote.exec to ${REMOTE_CI_HOME} on ${WAS_HOST_FQDN}..."
	# The following ssh command is to get by the "accept key" prompt" for new server pool machines that are put on-line.
	ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no ${REMOTE_USER}@${WAS_HOST_FQDN} echo "Accepting key if prompted..."
	scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no remote.exec ${REMOTE_USER}@${WAS_HOST_FQDN}:${REMOTE_CI_HOME}
	if [ $? != 0 ]; then
		echo "Could not copy remote.exec to ${REMOTE_CI_HOME} on ${WAS_HOST_FQDN}."
		exit 1
	fi
else
	# Get the previously dumped code coverage data from the server.
	echo "Copying ${REMOTE_CI_HOME}/remote.exec on ${WAS_HOST_FQDN} to `pwd`..."
	scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no ${REMOTE_USER}@${WAS_HOST_FQDN}:${REMOTE_CI_HOME}/remote.exec .
	if [ $? != 0 ]; then
		echo "Could not copy ${REMOTE_CI_HOME}/remote.exec on ${WAS_HOST_FQDN} to `pwd`."
		exit 1
	fi
fi

# Get the original, uninstrumented jars from the server and generate an xml report.
for APP in ${APP_LIST}
do
	echo "Copying ${REMOTE_CI_HOME}/${APP}/jars_original on ${WAS_HOST_FQDN} to `pwd`/jars_original..."
	eval scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no ${REMOTE_USER}@${WAS_HOST_FQDN}:${REMOTE_CI_HOME}/${APP}/jars_original/*.jar jars_original
	if [ $? != 0 ]; then
		echo "Could not copy ${REMOTE_CI_HOME}/${APP}/jars_original on ${WAS_HOST_FQDN} to `pwd`/jars_original."
		exit 1
	fi
	
	export APPLICATION_NAME=${APP}
	echo "Generating code coverage XML report..."
	${ANT_EXE} -v -lib ${JACOCO_JAR_DIR} -f ${JACOCO_BUILDFILE_DIR}/build_jacoco.xml report
	python ${JACOCO_SCRIPT_DIR}/gen_coverage_summary.py "${WORKSPACE}/jacoco_report.xml" "${WORKSPACE}/jacoco_report.summary"
	${JACOCO_SCRIPT_DIR}/gen_coverage_deltas.sh "${WORKSPACE}/tmp_jacoco/jacoco_report.summary" "${WORKSPACE}/jacoco_report.summary"
done

exit 0
