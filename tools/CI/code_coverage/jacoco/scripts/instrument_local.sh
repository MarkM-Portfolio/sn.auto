#!/bin/bash

usage() {
    prog_name=`basename ${MYSELF}`
    cat <<EOF

Usage:
  $prog_name [options] <full pathname to jar list file>

EOF
    exit 1
}

MYSELF=`readlink -f $0`

if [[ -z "$1" ]]; then
    echo "ERROR:"
    echo "  You need to specify a jar list file"
    usage
fi

JAR_LIST_FILE=$1

if [[ ! -f "${JAR_LIST_FILE}" ]]; then
    echo "ERROR - unable to find jar list file ${JAR_LIST_FILE}."
    exit 1
fi

. "${JAR_LIST_FILE}"
if [[ $? -ne 0 ]]; then
    echo "ERROR - failed to load jar list file ${JAR_LIST_FILE}."
    exit 1
fi

echo "NUM_DIRS_WITH_JARS_TO_INSTRUMENT: ${NUM_DIRS_WITH_JARS_TO_INSTRUMENT}"

if [[ "${NUM_DIRS_WITH_JARS_TO_INSTRUMENT}" == "0" ]]; then
	echo "WARNING: There are no jar files to instrument."
	exit 2
fi

index=0
while [[ "${index}" -lt "${NUM_DIRS_WITH_JARS_TO_INSTRUMENT}" ]]
do
	echo "Component: ${COMPONENT}"
	echo "Directory with jars to instrument: ${DIR_WITH_JARS_TO_INSTRUMENT[${index}]}"
	echo -e "\tJars to instrument: ${JARS_TO_INSTRUMENT[${index}]}\n"
	index=`expr ${index} + 1`
done

# Create some clean directories.
# DIR_JARS_INSTRUMENTED is where the jars will be instrumented.
# DIR_JARS_ORIGINAL is where a copy of the uninstrumented jars will be kept. Needed for report generation.
DIR_JARS_INSTRUMENTED="${WORKSPACE}/jars_instrumented"
DIR_JARS_ORIGINAL="${WORKSPACE}/jars_original"
for DIR_TO_CREATE in "${DIR_JARS_INSTRUMENTED}" "${DIR_JARS_ORIGINAL}"
do
	if [[ -z "${DIR_TO_CREATE}" || "${DIR_TO_CREATE}" == "/" ]]; then
		echo "DIR_TO_CREATE is invalid: \"${DIR_TO_CREATE}\""
		exit 1
	fi

	if [[ -d "${DIR_TO_CREATE}" ]]; then
		echo "Deleting directory:${DIR_TO_CREATE}"
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

index=0
while [[ "${index}" -lt "${NUM_DIRS_WITH_JARS_TO_INSTRUMENT}" ]]
do
	if [[ ! -d ${DIR_WITH_JARS_TO_INSTRUMENT[${index}]} ]]; then
		echo "WARNING - ${DIR_WITH_JARS_TO_INSTRUMENT[${index}]} does not exist."
		echo "Skipping..."
		index=`expr ${index} + 1`
		continue
	fi
	
	echo "Copying ${DIR_WITH_JARS_TO_INSTRUMENT[${index}]}/${JARS_TO_INSTRUMENT[${index}]} to ${DIR_JARS_ORIGINAL}"
	# Need the eval to handle wildcarding in jar file names.
	eval cp "${DIR_WITH_JARS_TO_INSTRUMENT[${index}]}/${JARS_TO_INSTRUMENT[${index}]}"  "${DIR_JARS_ORIGINAL}"
	if [ $? != 0 ]; then
		echo "Could not copy ${DIR_WITH_JARS_TO_INSTRUMENT[${index}]}/${JARS_TO_INSTRUMENT[${index}]} to ${DIR_JARS_ORIGINAL}."
		exit 1
	fi
	
	if [[ -n "${DIR_JARS_INSTRUMENTED}" && "${DIR_JARS_INSTRUMENTED}" != "/" ]]; then
		echo "Cleaning directory: ${DIR_JARS_INSTRUMENTED}..."
		rm ${DIR_JARS_INSTRUMENTED}/*.jar
	else
		echo "DIR_JARS_INSTRUMENTED is invalid: \"${DIR_JARS_INSTRUMENTED}\""
		exit 1
	fi
						
	echo "Instrumenting jars in ${DIR_JARS_INSTRUMENTED}..."	
	
	export DIR_TO_INCLUDE=${DIR_WITH_JARS_TO_INSTRUMENT[${index}]}
	export JARS_TO_INCLUDE=${JARS_TO_INSTRUMENT[${index}]}
	${ANT_EXE} -v -lib ${JACOCO_JAR_DIR} -f ${JACOCO_BUILDFILE_DIR}/build_jacoco.xml instr
	if [ $? != 0 ]; then
		echo "WARNING - Could not instrument jars in ${DIR_TO_INCLUDE}."
		echo "Skipping..."
		index=`expr ${index} + 1`
		continue
	fi
	
	echo "Copying instrumented jars back to ${DIR_WITH_JARS_TO_INSTRUMENT[${index}]}..."
	cp ${DIR_JARS_INSTRUMENTED}/*.jar ${DIR_WITH_JARS_TO_INSTRUMENT[${index}]}
	if [[ $? != 0 ]]; then
		echo "Error trying to copy jars back to ${DIR_WITH_JARS_TO_INSTRUMENT[${index}]}."
		exit 1
	fi

	index=`expr ${index} + 1`
done

exit 0
