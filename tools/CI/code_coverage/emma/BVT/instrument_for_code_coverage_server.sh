#!/bin/bash

usage() {
    prog_name=`basename ${MYSELF}`
    cat <<EOF

Usage:
  $prog_name  <full pathname to ear file> <comma separated list of applications to instrument>
EOF
    exit 1
}

MYSELF=`readlink -f $0`

if [[ -z "$1" ]]; then
    echo "ERROR:"
	echo "  You need to specify an ear file"
    usage
fi

if [[ -z "$2" ]]; then
    echo "ERROR:"
    echo "  You need to specify one or more applications to instrument"
    usage
fi

EAR_FILE_FULLPATH=$1
EAR_FILE_NAME=`basename "${EAR_FILE_FULLPATH}"`
APPLICATION_NAMES=$2
APPLICATION_NAMES=`echo ${APPLICATION_NAMES} | sed -e "s/,/ /g"`
echo "APPLICATION_NAMES: ${APPLICATION_NAMES}"

if [[ ! -f "${EAR_FILE_FULLPATH}" ]]; then
    echo "ERROR - unable to find ear file ${EAR_FILE_FULLPATH}."
    exit 1
fi

. ${CI_COMMON_HOME}/system.properties
if [[ $? -ne 0 ]]; then
    echo "ERROR - failed to load ${CI_COMMON_HOME}/system.properties."
    exit 1
fi

. ./server-info.properties
if [[ $? -ne 0 ]]; then
    echo "ERROR - failed to load server-info.properties."
    exit 1
fi

# DIR_INSTRUMENTED_JARS is where the jars will be instrumented.
# DIR_EXTRACTED_EAR is where an ear file will be extracted to.
# DIR_INSTRUMENTED_EAR is where an instrumented ear file will be repackaged.
# DIR_EXTRACTED_WAR is where an war file will be extracted to.
# DIR_INSTRUMENTED_WAR is where an instrumented war file will be repackaged.
DIR_INSTRUMENTED_JARS="${WORKSPACE}/instrumented_jars"
DIR_EXTRACTED_EAR="${WORKSPACE}/extracted_ear"
DIR_INSTRUMENTED_EAR="${WORKSPACE}/instrumented_ear"
DIR_EXTRACTED_WAR="${WORKSPACE}/extracted_war"
DIR_INSTRUMENTED_WAR="${WORKSPACE}/instrumented_war"

for APPLICATION_NAME in ${APPLICATION_NAMES}
do
	. "${EMMA_SCRIPT_DIR}/emma_jar_lists_server.sh"
	if [[ $? -ne 0 ]]; then
		echo "ERROR - failed to load jar list file ${JAR_LIST_FILE}."
		exit 1
	fi

	cd ${EMMA_HOME}

	echo "NUM_DIRS_WITH_JARS_TO_INSTRUMENT: ${NUM_DIRS_WITH_JARS_TO_INSTRUMENT}"

	if [[ "${NUM_DIRS_WITH_JARS_TO_INSTRUMENT}" == "0" ]]; then
		echo "WARNING: There are no jar files to instrument."
		continue
	fi

	index=0
	while [[ "${index}" -lt "${NUM_DIRS_WITH_JARS_TO_INSTRUMENT}" ]]
	do
		echo "Application: ${APPLICATION_NAME}"
		echo "Directory with jars to instrument: ${DIR_WITH_JARS_TO_INSTRUMENT[${index}]}"
		echo "\tJars to instrument: ${JARS_TO_INSTRUMENT[${index}]}\n"
		index=`expr ${index} + 1`
	done

	# Create needed directories each time to ensure they're clean.
	for DIR_TO_CREATE in "${DIR_INSTRUMENTED_JARS}" "${DIR_EXTRACTED_EAR}" "${DIR_EXTRACTED_WAR}" "${DIR_INSTRUMENTED_EAR}" "${DIR_INSTRUMENTED_WAR}"
	do
		if [[ -z "${DIR_TO_CREATE}" || "${DIR_TO_CREATE}" == "/" ]]; then
			echo "DIR_TO_CREATE is invalid: \"${DIR_TO_CREATE}\""
			exit 1
		fi

		if [[ -d "${DIR_TO_CREATE}" ]]; then
			echo "Deleting directory: ${DIR_TO_CREATE}"
			rm -rf "${DIR_TO_CREATE}"
			if [[ $? != 0 ]]; then
				echo "Could not delete directory: ${DIR_TO_CREATE}"
				exit 1
			fi
		fi
	
		echo "Creating directory: ${DIR_TO_CREATE}..."
		mkdir "${DIR_TO_CREATE}"
		if [[ $? != 0 ]]; then
			echo "Could not create directory: ${DIR_TO_CREATE}"
			exit 1
		fi
	done

	# Extract EAR file.
	cd "${DIR_EXTRACTED_EAR}"
	unzip -q "${EAR_FILE_FULLPATH}"
	if [[ $? != 0 ]]; then
		echo "Failed to extract ${EAR_FILE_FULLPATH}."
		exit 1
	fi
	
	# If war jars were specified, get the war file from DIR_EXTRACTED_EAR
	# and extract it to DIR_EXTRACTED_WAR
	
	if [[ -n "${WAR_FILENAME}" && "${WAR_FILENAME}" != none ]]; then
		cd "${DIR_EXTRACTED_WAR}"
		unzip -q "${DIR_EXTRACTED_EAR}/${WAR_FILENAME}"
		if [[ $? != 0 ]]; then
			echo "Failed to extract ${WAR_FILENAME}."
			exit 1
		fi
	fi

	# Clean out any old coverage metadata file
	ls "${WORKSPACE}/${APPLICATION_NAME}.em" > /dev/null 2>&1
	if [[ $? == 0 ]]; then
		echo "Removing old coverage metadata file ${WORKSPACE}/${APPLICATION_NAME}.em..."
		rm "${WORKSPACE}/${APPLICATION_NAME}.em"
		if [[ $? != 0 ]]; then
			echo "Could not remove coverage metadata file ${EMMA_METADATA_DIR}/${APPLICATION_NAME}.em"
			exit 1
		fi
	fi

	# The EMMA instrumentation command errors out when I try to instrument the
	# jars in their original locations, so I copy them to another directory and
	# instrument them there.

	EMMA_CMD_OPTS="-m overwrite -merge yes -exit -ix @${EMMA_FILTER_FILE}"

	index=0
	while [[ "${index}" -lt "${NUM_DIRS_WITH_JARS_TO_INSTRUMENT}" ]]
	do
		if [[ ! -d "${DIR_WITH_JARS_TO_INSTRUMENT[${index}]}" ]]; then
			echo "${DIR_WITH_JARS_TO_INSTRUMENT[${index}]} does not exist."
			echo "Skipping..."
			index=`expr ${index} + 1`
			continue
		fi
	
		cd /tmp
		if [[ -n "${DIR_INSTRUMENTED_JARS}" && "${DIR_INSTRUMENTED_JARS}" != "/" ]]; then
			echo "Cleaning directory: ${DIR_INSTRUMENTED_JARS}..."
			rm "${DIR_INSTRUMENTED_JARS}"/*.jar
		else
			echo "DIR_INSTRUMENTED_JARS is invalid: \"${DIR_INSTRUMENTED_JARS}\""
			exit 1
		fi
				
		echo "Copying jars from ${DIR_WITH_JARS_TO_INSTRUMENT[${index}]} to ${DIR_INSTRUMENTED_JARS}..."
		cd "${DIR_WITH_JARS_TO_INSTRUMENT[${index}]}"
		JARLIST=""
		ls -1 ${JARS_TO_INSTRUMENT[${index}]} | while read JAR
		do
			cp ${JAR} "${DIR_INSTRUMENTED_JARS}"
			JARLIST="${JARLIST}:./${JAR}"
		done
	
		if [ -z "${JARLIST}" ]; then
			echo "JARLIST is empty."
			echo "Skipping..."
			index=`expr ${index} + 1`
			continue
		fi
		
		echo "JARLIST: ${JARLIST}"

		echo "Instrumenting jars in ${DIR_INSTRUMENTED_JARS}..."
		echo "Coverage metadata will be merged to ${WORKSPACE}/${APPLICATION_NAME}.em"
		cd "${DIR_INSTRUMENTED_JARS}"
	
		${JAVA_EXE} -cp ${EMMA_JAR} emma instr -ip ${JARLIST} ${EMMA_CMD_OPTS} -out "${WORKSPACE}/${APPLICATION_NAME}.em"
		if [[ $? != 0 ]]; then
			echo "Error trying to instrument jars in ${DIR_INSTRUMENTED_JARS}."
			exit 1
		fi

		# Need to copy coverage metadata file to server so it will be available to other Jenkins jobs.
		echo "Copying coverage metadata file ${WORKSPACE}/${APPLICATION_NAME}.em to ${REMOTE_CI_HOME} on ${WAS_HOST_FQDN}..."
		scp "${WORKSPACE}/${APPLICATION_NAME}.em" ${REMOTE_USER}@${WAS_HOST_FQDN}:${REMOTE_CI_HOME}
		if [[ $? != 0 ]]; then
			echo "Error trying to copy ${WORKSPACE}/${APPLICATION_NAME}.em to ${REMOTE_CI_HOME} on ${WAS_HOST_FQDN}."
			exit 1
		fi

		cd ${DIR_EMMA_HOME}

		echo "Copying instrumented jars back to ${DIR_WITH_JARS_TO_INSTRUMENT[${index}]}..."
		cp "${DIR_INSTRUMENTED_JARS}"/*.jar "${DIR_WITH_JARS_TO_INSTRUMENT[${index}]}"
		if [[ $? != 0 ]]; then
			echo "Error trying to copy jars back to ${DIR_WITH_JARS_TO_INSTRUMENT[${index}]}."
			exit 1
		fi

		index=`expr ${index} + 1`
	done

	# Repackage the EAR_FILE.
	# If war jars were specified, repackage the war first	
	if [[ -n "${WAR_FILENAME}" && "${WAR_FILENAME}" != none ]]; then

		cd "${DIR_EXTRACTED_WAR}"
		zip -r -q "${DIR_INSTRUMENTED_WAR}/${WAR_FILENAME}" . 
		if [[ $? != 0 ]]; then
			echo "Failed to package ${DIR_INSTRUMENTED_WAR}/${WAR_FILENAME}."
			exit 1
		fi
		
		# Copy repackaged war back to extracted ear.
		echo "Copying ${DIR_INSTRUMENTED_WAR}/${WAR_FILENAME} to ${DIR_EXTRACTED_EAR}..."
		cp "${DIR_INSTRUMENTED_WAR}/${WAR_FILENAME}" "${DIR_EXTRACTED_EAR}"
	fi
	
	# Package up the ear.
	cd "${DIR_EXTRACTED_EAR}"
	zip -r -q "${DIR_INSTRUMENTED_EAR}/${EAR_FILE_NAME}" .
	if [[ $? != 0 ]]; then
		echo "Failed to package ${DIR_INSTRUMENTED_EAR}/${EAR_FILE_NAME}."
		exit 1
	fi
	
	# Copy instrumented ear back to original
	echo "Copying ${DIR_INSTRUMENTED_EAR}/${EAR_FILE_NAME} to ${EAR_FILE_FULLPATH}..."
	cp "${DIR_INSTRUMENTED_EAR}/${EAR_FILE_NAME}" "${EAR_FILE_FULLPATH}"
	if [[ $? != 0 ]]; then
		echo "Failed to copy ${DIR_INSTRUMENTED_EAR}/${EAR_FILE_NAME} to ${EAR_FILE_FULLPATH}."
		exit 1
	fi
done

exit 0
