#!/bin/sh

FE_NAME=sn.widgets.cal
APP=Calendar

# Create the workitem.properties file
${CI_COMMON_HOME}/gen_workitem_params.sh assign_to_automation

echo "`date +"%F %T,%3N"` [CI-CD] INFO Loading ${CI_COMMON_HOME}/ci_functions.sh..."
. ${CI_COMMON_HOME}/ci_functions.sh
if [ $? != 0 ]; then
	echo "`date +"%F %T,%3N"` [CI-CD] ERROR Failed to load ${CI_COMMON_HOME}/ci_functions.sh."
	exit 1
fi

fail_with_error() {
    echo_error "$@"
	${CI_COMMON_HOME}/get-was-info.sh
    exit 1
}

echo_info "Loading ${CI_COMMON_HOME}/system.properties..."
. ${CI_COMMON_HOME}/system.properties || fail_with_error "Failed to load ${CI_COMMON_HOME}/system.properties."

echo_info "Loading server-info.properties..."
. ./server-info.properties
[ $? -eq 0 ] || fail_with_error "Failed to load server-info.properties."

# Get and unzip the WidgetsCal fe.zip file.
[ -d src ] && ( echo_info "Deleting source tree: `pwd`/src..."; rm -rf src )
${LCUPDATE_DIR}/bin/get-build.sh -f -s src -b ${BUILD_LABEL} || fail_with_error "Failed to download or unzip fe.zip."

# Create a directory on the remote WAS to copy files.
echo_info "Creating directory \"${REMOTE_CI_HOME}/Calendar/jars_original\" on \"${WAS_HOST_FQDN}\"..."
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no ${REMOTE_USER}@${WAS_HOST_FQDN} mkdir -p ${REMOTE_CI_HOME}/${APP}/jars_original
[ $? -eq 0 ] || fail_with_error "Could not create directory \"${REMOTE_CI_HOME}\" on \"${WAS_HOST_FQDN}\"."

WAR_FILE_NAME=calendar.war
WAR_FILE_PATH="${WORKSPACE}/src/${FE_NAME}/lwp/build/calendar.web/lib/${WAR_FILE_NAME}"
DIR_WEB_RESOURCES=${WORKSPACE}/src/${FE_NAME}/lwp/build/calendar.web.resources/eclipse/plugins

if [ "${ENABLE_CODE_COVERAGE}" == "true" ]; then
    echo_info "ENABLE_CODE_COVERAGE is set to \"true\", instrumenting ${BUILD_COMPONENT} jars..."

	DIR_JARS_ORIGINAL="${WORKSPACE}/jars_original"
	DIR_JARS_INSTRUMENTED="${WORKSPACE}/jars_instrumented"
	DIR_WAR_EXTRACTED="${WORKSPACE}/war_extracted"
	DIR_WAR_INSTRUMENTED="${WORKSPACE}/war_instrumented"
	DIR_WEB_RESOURCES_INSTRUMENTED="${WORKSPACE}/web_resources_app_instrumented"
	
	. "${JACOCO_SCRIPT_DIR}/jar_lists_server.sh"
	[ $? -eq 0 ] || fail_with_error "ERROR - failed to load ${JACOCO_SCRIPT_DIR}/jar_lists_server.sh."

	# Create needed directories each time to ensure they're clean.
	for DIR_TO_CREATE in "${DIR_JARS_ORIGINAL}" "${DIR_JARS_INSTRUMENTED}" "${DIR_WAR_EXTRACTED}" "${DIR_WAR_INSTRUMENTED}" "${DIR_WEB_RESOURCES_INSTRUMENTED}"
	do
		[[ -z "${DIR_TO_CREATE}" || "${DIR_TO_CREATE}" == "/" ]] && fail_with_error "DIR_TO_CREATE is invalid: \"${DIR_TO_CREATE}\""

		if [ -d "${DIR_TO_CREATE}" ]; then
			echo_info "Deleting directory: ${DIR_TO_CREATE}"
			rm -rf "${DIR_TO_CREATE}"
			[ $? == 0 ] || fail_with_error "Could not delete directory: ${DIR_TO_CREATE}"
		fi
	
		echo_info "Creating directory: ${DIR_TO_CREATE}..."
		mkdir -p "${DIR_TO_CREATE}"
		[ $? == 0 ] || fail_with_error "Could not create directory: ${DIR_TO_CREATE}"
	done

	# Copy the web resources jars to the location where they will be instrumented.
	echo_info "Copying ${DIR_WEB_RESOURCES}/*.jar to ${DIR_WEB_RESOURCES_INSTRUMENTED}..."
	cp -v "${DIR_WEB_RESOURCES}"/*.jar "${DIR_WEB_RESOURCES_INSTRUMENTED}"
	[ $? == 0 ] || fail_with_error "Could not copy ${DIR_WEB_RESOURCES}/*.jar to ${DIR_WEB_RESOURCES_INSTRUMENTED}."
	
	# Extract the war file to get at the jar(s) to instrument.
	echo_info "Extracting ${WAR_FILE_PATH} to ${DIR_WAR_EXTRACTED}..."
	cd "${DIR_WAR_EXTRACTED}"
	unzip -q "${WAR_FILE_PATH}"
	[ $? == 0 ] || fail_with_error "Failed to extract ${WAR_FILE_PATH}."

	# Now instrument all the specified jars.
	index=0
	while [ "${index}" -lt "${NUM_DIRS_WITH_JARS_TO_INSTRUMENT}" ]
	do
		if [[ ! -d "${DIR_WITH_JARS_TO_INSTRUMENT[${index}]}" ]]; then
			echo_warn "${DIR_WITH_JARS_TO_INSTRUMENT[${index}]} does not exist."
			echo_warn "Skipping..."
			index=`expr ${index} + 1`
			continue
		fi
					
		# Need to save a copy of the original jars for Jacoco coverage reporting.
		echo_info "Copying ${DIR_WITH_JARS_TO_INSTRUMENT[${index}]}/${JARS_TO_INSTRUMENT[${index}]} to ${DIR_JARS_ORIGINAL}"
		# Need the eval to handle wildcarding in jar file names.
		eval cp -v "${DIR_WITH_JARS_TO_INSTRUMENT[${index}]}/${JARS_TO_INSTRUMENT[${index}]}"  "${DIR_JARS_ORIGINAL}"
		[ $? == 0 ] || fail_with_error "Could not copy ${DIR_WITH_JARS_TO_INSTRUMENT[${index}]}/${JARS_TO_INSTRUMENT[${index}]} to ${DIR_JARS_ORIGINAL}."

		# Copy the original jars back to the server so they will be available to other Jenkins jobs.
		echo_info "Copying ${DIR_JARS_ORIGINAL}/*.jar to ${REMOTE_CI_HOME}/${APP}/jars_original on ${WAS_HOST_FQDN}..."
		eval scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no "${DIR_JARS_ORIGINAL}/*.jar" ${REMOTE_USER}@${WAS_HOST_FQDN}:${REMOTE_CI_HOME}/${APP}/jars_original
		[ $? == 0 ] || fail_with_error "Could not copy ${DIR_JARS_ORIGINAL}/*.jar to ${REMOTE_CI_HOME}/${APP}/jars_original on ${WAS_HOST_FQDN}."

		# Make sure instrumented jar directory is clean each time through this loop.
		if [[ -n "${DIR_JARS_INSTRUMENTED}" && "${DIR_JARS_INSTRUMENTED}" != "/" ]]; then
			echo_info "Cleaning directory: ${DIR_JARS_INSTRUMENTED}..."
			rm "${DIR_JARS_INSTRUMENTED}"/*.jar
		else
			fail_with_error "DIR_JARS_INSTRUMENTED is invalid: \"${DIR_JARS_INSTRUMENTED}\""
		fi
		
		echo_info "Instrumenting jars in ${DIR_JARS_INSTRUMENTED}..."
		export DIR_TO_INCLUDE=${DIR_WITH_JARS_TO_INSTRUMENT[${index}]}
		export JARS_TO_INCLUDE=${JARS_TO_INSTRUMENT[${index}]}
		${ANT_EXE} -v -lib ${JACOCO_JAR_DIR} -f ${JACOCO_BUILDFILE_DIR}/build_jacoco.xml instr
		if [ $? != 0 ]; then
			echo_warn "Could not instrument jars in ${DIR_TO_INCLUDE}."
			continue
		fi

		echo_info "Copying instrumented jars back to ${DIR_WITH_JARS_TO_INSTRUMENT[${index}]}..."
		cp -v "${DIR_JARS_INSTRUMENTED}"/*.jar "${DIR_WITH_JARS_TO_INSTRUMENT[${index}]}"
		[ $? == 0 ] || fail_with_error "Error trying to copy jars back to ${DIR_WITH_JARS_TO_INSTRUMENT[${index}]}."

		index=`expr ${index} + 1`
	done

	echo_info "Packaging ${DIR_WAR_EXTRACTED} into ${DIR_WAR_INSTRUMENTED}/${WAR_FILE_NAME}..."
	cd "${DIR_WAR_EXTRACTED}"
	zip -r -q "${DIR_WAR_INSTRUMENTED}/${WAR_FILE_NAME}" . 
	[ $? == 0 ] || fail_with_error "Failed to package ${DIR_WAR_INSTRUMENTED}/${WAR_FILE_NAME}."

	echo_info "Copying ${DIR_WAR_INSTRUMENTED}/${WAR_FILE_NAME} to ${WAR_FILE_PATH}..."
	cp -v "${DIR_WAR_INSTRUMENTED}/${WAR_FILE_NAME}" "${WAR_FILE_PATH}"
	[ $? == 0 ] || fail_with_error "Failed to copy ${DIR_WAR_INSTRUMENTED}/${WAR_FILE_NAME} to ${WAR_FILE_PATH}."

	echo_info "Copying ${DIR_WEB_RESOURCES_INSTRUMENTED}/*.jar to ${DIR_WEB_RESOURCES}..."
	cp -v "${DIR_WEB_RESOURCES_INSTRUMENTED}"/*.jar "${DIR_WEB_RESOURCES}"
	[ $? == 0 ] || fail_with_error "Failed to copy ${DIR_WEB_RESOURCES_INSTRUMENTED}/*.jar to ${DIR_WEB_RESOURCES}."
fi

DEPLOY_SCRIPT_NAME=update_calendar_war.py
DEPLOY_SCRIPT_PATH=${CI_COMMON_HOME}/${DEPLOY_SCRIPT_NAME}

FILES_TO_COPY[1]=${WAR_FILE_PATH}
FILES_TO_COPY[2]=${DEPLOY_SCRIPT_PATH}
NUM_FILES_TO_COPY=${#FILES_TO_COPY[@]}

# Copy the files to the remote WAS.
for i in `seq 1 $NUM_FILES_TO_COPY`
do
	echo_info "Copying ${FILES_TO_COPY[i]} to ${REMOTE_USER}@${WAS_HOST_FQDN}:${REMOTE_CI_HOME}..."
	scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no ${FILES_TO_COPY[i]} ${REMOTE_USER}@${WAS_HOST_FQDN}:${REMOTE_CI_HOME}
	[ $? -eq 0 ] || fail_with_error "Failed to copy file."
done

# Copy the Calendar DB fixup scripts to the remote WAS.
echo_info "Copying ${WORKSPACE}/src/${FE_NAME}/lwp/build/calendar.sn.install/db.scripts/calendar/db2 to ${REMOTE_LCUPDATE_DIR}/xkit/connections.sql/communities on ${WAS_HOST_FQDN}..."
scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no -r "${WORKSPACE}/src/${FE_NAME}/lwp/build/calendar.sn.install/db.scripts/calendar/db2/" ${REMOTE_USER}@${WAS_HOST_FQDN}:${REMOTE_LCUPDATE_DIR}/xkit/connections.sql/communities
[ $? -eq 0 ] || fail_with_error "Failed to copy Calendar DB fixup scripts."

# Run the Calendar fixup scripts.
echo_info "Updating Calendar database..."
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no ${REMOTE_USER}@${WAS_HOST_FQDN} sudo -i -u db2inst1 ${REMOTE_LCUPDATE_DIR}/bin/fixDB.sh -p ${REMOTE_LCUPDATE_DIR}/xkit/connections.sql calendar
[ $? -eq 0 ] || fail_with_error "Failed to update Calendar database"

# Get the updated schema version.
SCHEMA_VERSION_UPDATED=`ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no ${REMOTE_USER}@${WAS_HOST_FQDN} sudo -i -u db2inst1 ${REMOTE_LCUPDATE_DIR}/bin/create_db.sh -q calendar`
[ $? -eq 0 ] || echo_warn "Could not get updated schema version."
echo_info "Updated schema version: ${SCHEMA_VERSION_UPDATED}"

# Execute the wsadmin command on the remote WAS to deploy the WAR.
echo_info "Executing wsadmin on ${WAS_HOST_FQDN}..."
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no ${REMOTE_USER}@${WAS_HOST_FQDN} ${REMOTE_LCUPDATE_DIR}/bin/was adm -f ${REMOTE_CI_HOME}/${DEPLOY_SCRIPT_NAME} ${REMOTE_CI_HOME}/${WAR_FILE_NAME}
[ $? -eq 0 ] || fail_with_error "Failed to execute wsadmin command."

# Deploy calendar web resources files.
echo_info "Removing existing Calendar web resources from the WAS..."
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no ${REMOTE_USER}@${WAS_HOST_FQDN} rm -v ${CONNECTIONS_SHARED_DATA_DIR}/provision/webresources/com.ibm.dwa.web.resources_*.jar
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no ${REMOTE_USER}@${WAS_HOST_FQDN} rm -v ${CONNECTIONS_SHARED_DATA_DIR}/provision/webresources/com.ibm.lconn.calendar.web.resources_*.jar

echo_info "Copying new Calendar web resources to the WAS..."
eval scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no "${DIR_WEB_RESOURCES}/com.ibm.dwa.web.resources_*.jar" ${REMOTE_USER}@${WAS_HOST_FQDN}:${CONNECTIONS_SHARED_DATA_DIR}/provision/webresources
[ $? -eq 0 ] || fail_with_error "Failed to copy web resource."

eval scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no "${DIR_WEB_RESOURCES}/com.ibm.lconn.calendar.web.resources_*.jar" ${REMOTE_USER}@${WAS_HOST_FQDN}:${CONNECTIONS_SHARED_DATA_DIR}/provision/webresources
[ $? -eq 0 ] || fail_with_error "Failed to copy web resource."

echo_info "Removing Common temp directory..."
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no ${REMOTE_USER}@${WAS_HOST_FQDN} rm -rf ${WAS_HOME}/profiles/${WAS_PROFILE}/temp/${WAS_NODE}/${WAS_SERVER}/Common
[ $? -eq 0 ] || fail_with_error "Failed to remove Common temp directory."

echo_info "Restarting Common..."
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no ${REMOTE_USER}@${WAS_HOST_FQDN} ${REMOTE_LCUPDATE_DIR}/bin/was adm -f ${REMOTE_LCUPDATE_DIR}/bin/restart_app.py Common
[ $? -eq 0 ] || fail_with_error "Failed to execute wsadmin command."

# Get the current build versions for all Connections components.
# Copy the file back to the server so it will be available for each test job.
echo_info "Deleting existing server status file ${SERVER_STATUS_FILENAME} from ${WORKSPACE}..."
rm -fv ${SERVER_STATUS_FILENAME}

echo_info "Getting current server status from ${WAS_HOST_FQDN}..."
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no ${REMOTE_USER}@${WAS_HOST_FQDN} ${WAS_HOME}/profiles/${WAS_PROFILE}/bin/wsadmin.sh -lang jython -f ${REMOTE_LCUPDATE_DIR}/bin/server_status.py | tee ${SERVER_STATUS_FILENAME}
if [ ${PIPESTATUS[0]} != 0 ]; then
	echo_warn "Could not get server status from ${WAS_HOST_FQDN}."
else
	echo_info "Copying ${WORKSPACE}/${SERVER_STATUS_FILENAME} back to ${WAS_HOST_FQDN}..."
	scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no ${SERVER_STATUS_FILENAME} ${REMOTE_USER}@${WAS_HOST_FQDN}:${REMOTE_CI_HOME}
	[ $? -eq 0 ] || echo_warn "Could not copy ${WORKSPACE}/${SERVER_STATUS_FILENAME} back to ${WAS_HOST_FQDN}:${REMOTE_CI_HOME}."
fi

# Dump the run-time code coverage data so as to reset the data to zero in prep of running the BVT suites.
if [ "${ENABLE_CODE_COVERAGE}" == "true" ]; then
	echo_info "ENABLE_CODE_COVERAGE is set to \"true\", resetting coverage data..."
	echo_info "Dumping code coverage data from server in order to reset it to zero..."
	export WAS_HOST_FQDN=${WAS_HOST_FQDN}
	${ANT_EXE} -v -lib ${JACOCO_JAR_DIR} -f ${JACOCO_BUILDFILE_DIR}/build_jacoco.xml dump
	[ $? -eq 0 ] || fail_with_error "Unable to dump code coverage data from ${WAS_HOST_FQDN}."
fi

# Script has passed, so delete the workitem properties file
echo_info "Removing workitem.properties..."
rm -fv workitem.properties

exit 0
