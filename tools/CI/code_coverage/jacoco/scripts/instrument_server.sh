#!/bin/bash

APP_LIST_FOR_CODE_COVERAGE="$1"

echo "`date +"%F %T,%3N"` [CI-CD] INFO Loading ${CI_COMMON_HOME}/ci_functions.sh..."
. ${CI_COMMON_HOME}/ci_functions.sh
if [ $? != 0 ]; then
	echo "`date +"%F %T,%3N"` [CI-CD] ERROR Failed to load ${CI_COMMON_HOME}/ci_functions.sh."
	exit 1
fi

if [ -z "${APP_LIST_FOR_CODE_COVERAGE}" ]; then
    echo_error "\${APP_LIST_FOR_CODE_COVERAGE} is null."
    exit 1
fi

. ./server-info.properties
if [ $? != 0 ]; then
	echo_error "Failed to load server-info.properties."
	exit 1
fi

. ${CI_COMMON_HOME}/system.properties
if [ $? != 0 ]; then
	echo_error "Failed to load ${CI_COMMON_HOME}/system.properties."
	exit 1
fi
	
# DIR_EAR_ORIGINAL is where the original, uninstrumented ear files from the server will be kept.
# DIR_JARS_ORIGINAL is where a copy of the original, uninstrumented jar files will be kept (needed for Jacoco report generation).
# DIR_JARS_INSTRUMENTED is where the jars will be instrumented.
# DIR_EAR_EXTRACTED is where the ear file will be extracted to.
# DIR_EAR_INSTRUMENTED is where the instrumented ear file will be repackaged.
# DIR_WAR_EXTRACTED is where the war file will be extracted to.
# DIR_WAR_INSTRUMENTED is where the instrumented war file will be repackaged.
DIR_EAR_ORIGINAL="${WORKSPACE}/ear_original"
DIR_JARS_ORIGINAL="${WORKSPACE}/jars_original"
DIR_JARS_INSTRUMENTED="${WORKSPACE}/jars_instrumented"
DIR_EAR_EXTRACTED="${WORKSPACE}/ear_extracted"
DIR_EAR_INSTRUMENTED="${WORKSPACE}/ear_instrumented"
DIR_WAR_EXTRACTED="${WORKSPACE}/war_extracted"
DIR_WAR_INSTRUMENTED="${WORKSPACE}/war_instrumented"
DIR_WEB_RESOURCES_INSTRUMENTED="${WORKSPACE}/web_resources_app_instrumented"

for APP in ${APP_LIST_FOR_CODE_COVERAGE}
do
	# Create a directory on the remote WAS to copy files.
	echo_info "Creating directory \"${REMOTE_CI_HOME}/${APP}/jars_original\" on \"${WAS_HOST_FQDN}\"..."
	ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no ${REMOTE_USER}@${WAS_HOST_FQDN} mkdir -p ${REMOTE_CI_HOME}/${APP}/jars_original
	if [ $? != 0 ]; then
		echo_error "Could not create directory \"${REMOTE_CI_HOME}/${APP}/jars_original\" on \"${WAS_HOST_FQDN}\"."
		exit 1
	fi

	. "${JACOCO_SCRIPT_DIR}/jar_lists_server.sh"
	if [[ $? -ne 0 ]]; then
		echo_error "Failed to load ${JACOCO_SCRIPT_DIR}/jar_lists_server.sh."
		exit 1
	fi

	EAR_FILE_PATH="${DIR_EAR_ORIGINAL}/${EAR_FILE_NAME}"

	cd "${WORKSPACE}"

	echo_info "NUM_DIRS_WITH_JARS_TO_INSTRUMENT: ${NUM_DIRS_WITH_JARS_TO_INSTRUMENT}"

	if [[ "${NUM_DIRS_WITH_JARS_TO_INSTRUMENT}" == "0" ]]; then
		echo_warn "There are no jar files to instrument."
		continue
	fi

	index=0
	while [[ "${index}" -lt "${NUM_DIRS_WITH_JARS_TO_INSTRUMENT}" ]]
	do
		echo_info "Application: ${APP}"
		echo_info "Directory with jars to instrument: ${DIR_WITH_JARS_TO_INSTRUMENT[${index}]}"
		echo_info "\tJars to instrument: ${JARS_TO_INSTRUMENT[${index}]}\n"
		index=`expr ${index} + 1`
	done

	# Create needed directories each time to ensure they're clean.
	for DIR_TO_CREATE in "${DIR_EAR_ORIGINAL}" "${DIR_JARS_ORIGINAL}" "${DIR_JARS_INSTRUMENTED}" "${DIR_EAR_EXTRACTED}" "${DIR_EAR_INSTRUMENTED}" "${DIR_WAR_EXTRACTED}" "${DIR_WAR_INSTRUMENTED}" "${DIR_WEB_RESOURCES_INSTRUMENTED}"
	do
		if [[ -z "${DIR_TO_CREATE}" || "${DIR_TO_CREATE}" == "/" ]]; then
			echo_error "DIR_TO_CREATE is invalid: \"${DIR_TO_CREATE}\""
			exit 1
		fi

		if [[ -d "${DIR_TO_CREATE}" ]]; then
			echo_info "Deleting directory: ${DIR_TO_CREATE}"
			rm -rf "${DIR_TO_CREATE}"
			if [[ $? != 0 ]]; then
				echo_error "Could not delete directory: ${DIR_TO_CREATE}"
				exit 1
			fi
		fi
	
		echo_info "Creating directory: ${DIR_TO_CREATE}..."
		mkdir -p "${DIR_TO_CREATE}"
		if [[ $? != 0 ]]; then
			echo_error "Could not create directory: ${DIR_TO_CREATE}"
			exit 1
		fi
	done

	# Get web resource jars from server.
	echo_info "Looking for app web resource jars to copy..."
	if [[ -n "${DIR_WEB_RESOURCES}" && "${DIR_WEB_RESOURCES}" != none ]]; then
		echo_info "Copying ${REMOTE_LCUPDATE_DIR}/xkit/${DIR_WEB_RESOURCES}/*.jar on ${WAS_HOST_FQDN} to ${DIR_WEB_RESOURCES_INSTRUMENTED}..."
		scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no ${REMOTE_USER}@${WAS_HOST_FQDN}:${REMOTE_LCUPDATE_DIR}/xkit/${DIR_WEB_RESOURCES}/*.jar "${DIR_WEB_RESOURCES_INSTRUMENTED}"
		if [[ $? != 0 ]]; then
			echo_error "Could not copy ${REMOTE_LCUPDATE_DIR}/xkit/${DIR_WEB_RESOURCES}/*.jar on ${WAS_HOST_FQDN} to ${DIR_WEB_RESOURCES_INSTRUMENTED}."
			exit 1
		fi
	fi
	
	# If an EAR file was specified, get it from server.
	echo_info "Looking for EAR file to copy..."
	if [[ -n "${EAR_FILE_NAME}" && "${EAR_FILE_NAME}" != none ]]; then
		echo_info "Copying ${REMOTE_LCUPDATE_DIR}/xkit/installableApps/${EAR_FILE_NAME} on ${WAS_HOST_FQDN} to ${DIR_EAR_ORIGINAL}..."
		scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no ${REMOTE_USER}@${WAS_HOST_FQDN}:${REMOTE_LCUPDATE_DIR}/xkit/installableApps/${EAR_FILE_NAME} "${DIR_EAR_ORIGINAL}"
		if [[ $? != 0 ]]; then
			echo_error "Could not copy ${REMOTE_LCUPDATE_DIR}/xkit/installableApps/${EAR_FILE_NAME} on ${WAS_HOST_FQDN} to ${DIR_EAR_ORIGINAL}."
			exit 1
		fi
	
		# Extract the EAR.
		echo_info "Extracting ${EAR_FILE_PATH} to ${DIR_EAR_EXTRACTED}..."
		cd "${DIR_EAR_EXTRACTED}"
		unzip -q "${EAR_FILE_PATH}"
		if [[ $? != 0 ]]; then
			echo_error "Failed to extract ${EAR_FILE_PATH}."
			exit 1
		fi
	
		# If war jars were specified, get the war file from DIR_EAR_EXTRACTED.
		# and extract it to DIR_WAR_EXTRACTED	
		if [[ -n "${WAR_FILE_NAME}" && "${WAR_FILE_NAME}" != none ]]; then
			echo_info "Extracting ${DIR_EAR_EXTRACTED}/${WAR_FILE_NAME} to ${DIR_WAR_EXTRACTED}..."
			cd "${DIR_WAR_EXTRACTED}"
			unzip -q "${DIR_EAR_EXTRACTED}/${WAR_FILE_NAME}"
			if [[ $? != 0 ]]; then
				echo_error "Failed to extract ${WAR_FILE_NAME}."
				exit 1
			fi
		fi
	fi

	# Now instrument all the specified jars.
	index=0
	while [[ "${index}" -lt "${NUM_DIRS_WITH_JARS_TO_INSTRUMENT}" ]]
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
		eval cp "${DIR_WITH_JARS_TO_INSTRUMENT[${index}]}/${JARS_TO_INSTRUMENT[${index}]}"  "${DIR_JARS_ORIGINAL}"
		if [ $? != 0 ]; then
			echo_error "Could not copy ${DIR_WITH_JARS_TO_INSTRUMENT[${index}]}/${JARS_TO_INSTRUMENT[${index}]} to ${DIR_JARS_ORIGINAL}."
			exit 1
		fi

		# Copy the original jars back to the server so they will be available to other Jenkins jobs.
		echo_info "Copying ${DIR_JARS_ORIGINAL}/*.jar to ${REMOTE_CI_HOME}/${APP}/jars_original on ${WAS_HOST_FQDN}..."
		eval scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no "${DIR_JARS_ORIGINAL}/*.jar" ${REMOTE_USER}@${WAS_HOST_FQDN}:${REMOTE_CI_HOME}/${APP}/jars_original
		if [ $? != 0 ]; then
			echo_error "Could not copy ${DIR_JARS_ORIGINAL}/*.jar to ${REMOTE_CI_HOME}/${APP}/jars_original on ${WAS_HOST_FQDN}."
			exit 1
		fi

		# Make sure instrumented jar directory is clean each time through this loop.
		if [[ -n "${DIR_JARS_INSTRUMENTED}" && "${DIR_JARS_INSTRUMENTED}" != "/" ]]; then
			echo_info "Cleaning directory: ${DIR_JARS_INSTRUMENTED}..."
			rm "${DIR_JARS_INSTRUMENTED}"/*.jar
		else
			echo_error "DIR_JARS_INSTRUMENTED is invalid: \"${DIR_JARS_INSTRUMENTED}\""
			exit 1
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
		cp "${DIR_JARS_INSTRUMENTED}"/*.jar "${DIR_WITH_JARS_TO_INSTRUMENT[${index}]}"
		if [[ $? != 0 ]]; then
			echo_error "Could not copy jars back to ${DIR_WITH_JARS_TO_INSTRUMENT[${index}]}."
			exit 1
		fi

		index=`expr ${index} + 1`
	done


	# Repackage the instrumented EAR.
	if [[ -n "${EAR_FILE_NAME}" && "${EAR_FILE_NAME}" != none ]]; then
		# If war jars were specified, repackage the war first	
		if [[ -n "${WAR_FILE_NAME}" && "${WAR_FILE_NAME}" != none ]]; then

			echo_info "Packaging ${DIR_WAR_EXTRACTED} into ${DIR_WAR_INSTRUMENTED}/${WAR_FILE_NAME}..."
			cd "${DIR_WAR_EXTRACTED}"
			zip -r -q "${DIR_WAR_INSTRUMENTED}/${WAR_FILE_NAME}" . 
			if [[ $? != 0 ]]; then
				echo_error "Failed to package ${DIR_WAR_INSTRUMENTED}/${WAR_FILE_NAME}."
				exit 1
			fi
		
			# Copy repackaged war back to extracted ear.
			echo_info "Copying ${DIR_WAR_INSTRUMENTED}/${WAR_FILE_NAME} to ${DIR_EAR_EXTRACTED}..."
			cp "${DIR_WAR_INSTRUMENTED}/${WAR_FILE_NAME}" "${DIR_EAR_EXTRACTED}"
		fi
	
		# Package up the ear.
		echo_info "Packaging ${DIR_EAR_EXTRACTED} into ${DIR_EAR_INSTRUMENTED}/${EAR_FILE_NAME}..."
		cd "${DIR_EAR_EXTRACTED}"
		zip -r -q "${DIR_EAR_INSTRUMENTED}/${EAR_FILE_NAME}" .
		if [[ $? != 0 ]]; then
			echo_error "Failed to package ${DIR_EAR_INSTRUMENTED}/${EAR_FILE_NAME}."
			exit 1
		fi
	
		# Copy instrumented ear back to server.
		echo_info "Copying ${DIR_EAR_INSTRUMENTED}/${EAR_FILE_NAME} to ${REMOTE_LCUPDATE_DIR}/xkit/installableApps on ${WAS_HOST_FQDN}..."
		scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no "${DIR_EAR_INSTRUMENTED}/${EAR_FILE_NAME}" ${REMOTE_USER}@${WAS_HOST_FQDN}:${REMOTE_LCUPDATE_DIR}/xkit/installableApps
		if [[ $? != 0 ]]; then
			echo_error "Failed to copy ${DIR_EAR_INSTRUMENTED}/${EAR_FILE_NAME} to ${REMOTE_LCUPDATE_DIR}/xkit/installableApps on ${WAS_HOST_FQDN}."
			exit 1
		fi
	fi
	
	# Copy instrumented web resource jars back to server.
	if [[ -n "${DIR_WEB_RESOURCES}" && "${DIR_WEB_RESOURCES}" != none ]]; then
		echo_info "Copying ${DIR_WEB_RESOURCES_INSTRUMENTED}/*.jar to ${REMOTE_LCUPDATE_DIR}/xkit/${DIR_WEB_RESOURCES} on ${WAS_HOST_FQDN}..."
		eval scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no "${DIR_WEB_RESOURCES_INSTRUMENTED}/*.jar" ${REMOTE_USER}@${WAS_HOST_FQDN}:${REMOTE_LCUPDATE_DIR}/xkit/${DIR_WEB_RESOURCES}
		if [[ $? != 0 ]]; then
			echo_error "Failed to copy ${DIR_WEB_RESOURCES_INSTRUMENTED}/*.jar to ${REMOTE_LCUPDATE_DIR}/xkit/${DIR_WEB_RESOURCES} on ${WAS_HOST_FQDN}."
			exit 1
		fi
	fi
done

exit 0
