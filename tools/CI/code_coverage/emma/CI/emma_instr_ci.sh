#!/bin/ksh

usage() {
    prog_name=`basename ${MYSELF}`
    cat <<EOF

Usage:
  $prog_name [options] <full pathname to configuration file> <full pathname to jar list file>

Options:
  -x		Use the EMMA_FILTER_FILE as the EMMA filter
			file. (optional)
EOF
    exit 1
}

MYSELF=`readlink -f $0`

while getopts x opt
do
    case $opt in
        x) flag_use_filter_file=1;;
        *) usage ;;
    esac
done
shift `expr $OPTIND - 1`

if [[ -z "$1" ]]; then
    echo "ERROR:"
    echo "  You need to specify a configuration file"
    usage
fi

if [[ -z "$2" ]]; then
    echo "ERROR:"
    echo "  You need to specify a jar list file"
    usage
fi

CONFIG_FILE=$1
JAR_LIST_FILE=$2

if [[ ! -f "${CONFIG_FILE}" ]]; then
    print "ERROR - unable to find configuration file ${CONFIG_FILE}."
    exit 1
fi

. "${CONFIG_FILE}"
if [[ $? -ne 0 ]]; then
    print "ERROR - failed to load configuration file ${CONFIG_FILE}."
    exit 1
fi

if [[ ! -f "${JAR_LIST_FILE}" ]]; then
    print "ERROR - unable to find jar list file ${JAR_LIST_FILE}."
    exit 1
fi

. "${JAR_LIST_FILE}"
if [[ $? -ne 0 ]]; then
    print "ERROR - failed to load jar list file ${JAR_LIST_FILE}."
    exit 1
fi

if [[ -n "${flag_use_filter_file}" && ! -f "${EMMA_FILTER_FILE}" ]]; then
	print "ERROR - unable to find EMMA filter file ${EMMA_FILTER_FILE}"
	exit 1
fi

cd ${DIR_EMMA_HOME}

print "NUM_DIRS_WITH_JARS_TO_INSTRUMENT: ${NUM_DIRS_WITH_JARS_TO_INSTRUMENT}"

if [[ "${NUM_DIRS_WITH_JARS_TO_INSTRUMENT}" == "0" ]]; then
	print "WARNING: There are no jar files to instrument."
	exit 2
fi

index=0
while [[ "${index}" -lt "${NUM_DIRS_WITH_JARS_TO_INSTRUMENT}" ]]
do
	print "Component: ${COMPONENT}"
	print "Directory with jars to instrument: ${DIR_WITH_JARS_TO_INSTRUMENT[${index}]}"
	print "\tJars to instrument: ${JARS_TO_INSTRUMENT[${index}]}\n"
	index=`expr ${index} + 1`
done

# Create needed directories.
# DIR_JARS_INSTRUMENTED is where the jars will be instrumented.
# DIR_EXTRACTED_EAR is where an ear file will be extracted to.
# DIR_INSTRUMENTED_EAR is where an ear file will be repackaged.
# DIR_EXTRACTED_WAR is where an war file will be extracted to.
# DIR_INSTRUMENTED_WAR is where an war file will be repackaged.
for DIR_TO_CREATE in ${DIR_JARS_INSTRUMENTED} ${DIR_EXTRACTED_EAR} ${DIR_EXTRACTED_WAR} ${DIR_INSTRUMENTED_EAR} ${DIR_INSTRUMENTED_WAR}
do
	if [[ -z "${DIR_TO_CREATE}" || "${DIR_TO_CREATE}" == "/" ]]; then
		print "DIR_TO_CREATE is invalid: \"${DIR_TO_CREATE}\""
		exit 1
	fi

	if [[ -d ${DIR_TO_CREATE} ]]; then
		print "Deleting directory: ${DIR_TO_CREATE}"
		rm -rf ${DIR_TO_CREATE}
		if [[ $? != 0 ]]; then
			print "Could not delete directory: ${DIR_TO_CREATE}"
			exit 1
		fi
	fi
	
	print "Creating directory: ${DIR_TO_CREATE}..."
	mkdir ${DIR_TO_CREATE}
	if [[ $? != 0 ]]; then
		print "Could not create directory: ${DIR_TO_CREATE}"
		exit 1
	fi
done

# If EAR_FILE is defined, then extract it to DIR_EXTRACTED_EAR.
if [[ -n "${EAR_FILE}" && -n "${EAR_APP_NAME}" && "${INSTRUMENT_THE_EAR}" == true ]]; then		
	cd ${DIR_EXTRACTED_EAR}
	unzip ${EAR_FILE_FULLPATH}
	if [[ $? != 0 ]]; then
		print "Failed to extract ${EAR_FILE_FULLPATH}."
		exit 1
	fi
	
	# If war jars were specified, get the war file from DIR_EXTRACTED_EAR
	# and extract it to DIR_EXTRACTED_WAR
	
	if [[ -n "${WAR_FILENAME}" && "${WAR_FILENAME}" != none ]]; then
		cd ${DIR_EXTRACTED_WAR}
		unzip ${DIR_EXTRACTED_EAR}/${WAR_FILENAME}
		if [[ $? != 0 ]]; then
			print "Failed to extract ${WAR_FILENAME}."
			exit 1
		fi
	fi
fi

# Clean out any old coverage metadata file
ls ${EMMA_METADATA_DIR}/${COMPONENT}.em > /dev/null 2>&1
if [[ $? == 0 ]]; then
	print "Removing old coverage metadata file ${EMMA_METADATA_DIR}/${COMPONENT}.em..."
	rm ${EMMA_METADATA_DIR}/${COMPONENT}.em
	if [[ $? != 0 ]]; then
		print "Could not remove coverage metadata file ${EMMA_METADATA_DIR}/${COMPONENT}.em"
		exit 1
	fi
fi

# The EMMA instrumentation command errors out when I try to instrument the
# jars in their original locations, so I copy them to another directory and
# instrument them there.

EMMA_CMD_OPTS="-m overwrite -merge yes -exit"
if [[ -n "${flag_use_filter_file}" ]]; then
	EMMA_CMD_OPTS="${EMMA_CMD_OPTS} -ix @${EMMA_FILTER_FILE}"
fi

print "EMMA_CMD_OPTS: ${EMMA_CMD_OPTS}"

index=0
while [[ "${index}" -lt "${NUM_DIRS_WITH_JARS_TO_INSTRUMENT}" ]]
do
	if [[ ! -d ${DIR_WITH_JARS_TO_INSTRUMENT[${index}]} ]]; then
		print "${DIR_WITH_JARS_TO_INSTRUMENT[${index}]} does not exist."
		print "Skipping..."
		index=`expr ${index} + 1`
		continue
	fi
	
	cd /tmp
	if [[ -n "${DIR_JARS_INSTRUMENTED}" && "${DIR_JARS_INSTRUMENTED}" != "/" ]]; then
		print "Cleaning directory: ${DIR_JARS_INSTRUMENTED}..."
		rm ${DIR_JARS_INSTRUMENTED}/*.jar
	else
		print "DIR_JARS_INSTRUMENTED is invalid: \"${DIR_JARS_INSTRUMENTED}\""
		exit 1
	fi
				
	print "Copying jars from ${DIR_WITH_JARS_TO_INSTRUMENT[${index}]} to ${DIR_JARS_INSTRUMENTED}..."
	cd ${DIR_WITH_JARS_TO_INSTRUMENT[${index}]}
	JARLIST=""
	ls -1 ${JARS_TO_INSTRUMENT[${index}]} | while read JAR
	do
		cp ${JAR} ${DIR_JARS_INSTRUMENTED}
		JARLIST="${JARLIST}:./${JAR}"
	done
	
	if [ -z "${JARLIST}" ]; then
		print "JARLIST is empty."
		print "Skipping..."
		index=`expr ${index} + 1`
		continue
	fi
		
	print "JARLIST: ${JARLIST}"

	print "Instrumenting jars in ${DIR_JARS_INSTRUMENTED}..."
	print "Coverage metadata will be merged to ${EMMA_METADATA_DIR}/${COMPONENT}.em"
	cd ${DIR_JARS_INSTRUMENTED}
	
	${JAVA_EXE} -cp ${EMMA_JAR} emma instr -ip ${JARLIST} ${EMMA_CMD_OPTS} -out ${EMMA_METADATA_DIR}/${COMPONENT}.em
	if [[ $? != 0 ]]; then
		print "Error trying to instrument jars in ${DIR_JARS_INSTRUMENTED}."
		exit 1
	fi

	cd ${DIR_EMMA_HOME}

	print "Copying instrumented jars back to ${DIR_WITH_JARS_TO_INSTRUMENT[${index}]}..."
	cp ${DIR_JARS_INSTRUMENTED}/*.jar ${DIR_WITH_JARS_TO_INSTRUMENT[${index}]}
	if [[ $? != 0 ]]; then
		print "Error trying to copy jars back to ${DIR_WITH_JARS_TO_INSTRUMENT[${index}]}."
		exit 1
	fi

	index=`expr ${index} + 1`
done

# If EAR_FILE is defined, then repackage it up.
if [[ -n "${EAR_FILE}" && -n "${EAR_APP_NAME}" && "${INSTRUMENT_THE_EAR}" == true ]]; then		
	# If war jars were specified, repackage the war first	
	if [[ -n "${WAR_FILENAME}" && "${WAR_FILENAME}" != none ]]; then

		cd ${DIR_EXTRACTED_WAR}
		zip -r ${DIR_INSTRUMENTED_WAR}/${WAR_FILENAME} . 
		if [[ $? != 0 ]]; then
			print "Failed to package ${DIR_INSTRUMENTED_WAR}/${WAR_FILENAME}."
			exit 1
		fi
		
		# Copy repackaged war back to extracted ear.
		print "Copying ${DIR_INSTRUMENTED_WAR}/${WAR_FILENAME} to ${DIR_EXTRACTED_EAR}..."
		cp ${DIR_INSTRUMENTED_WAR}/${WAR_FILENAME} ${DIR_EXTRACTED_EAR}
	fi
	
	# Package up the ear.
	cd ${DIR_EXTRACTED_EAR}
	zip -r ${DIR_INSTRUMENTED_EAR}/${EAR_FILE_NAME} .
	if [[ $? != 0 ]]; then
		print "Failed to package ${DIR_INSTRUMENTED_EAR}/${EAR_FILE_NAME}."
		exit 1
	fi
	
	# Copy instrumented ear back to original
	print "Copying ${DIR_INSTRUMENTED_EAR}/${EAR_FILE_NAME} to ${EAR_FILE_FULLPATH}..."
	cp ${DIR_INSTRUMENTED_EAR}/${EAR_FILE_NAME} ${EAR_FILE_FULLPATH}
	if [[ $? != 0 ]]; then
		print "Failed to copy ${DIR_INSTRUMENTED_EAR}/${EAR_FILE_NAME} to ${EAR_FILE_FULLPATH}."
		exit 1
	fi
fi

exit 0
