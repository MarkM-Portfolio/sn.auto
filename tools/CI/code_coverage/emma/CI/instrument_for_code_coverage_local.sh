#!/bin/bash

usage() {
    prog_name=`basename ${MYSELF}`
    cat <<EOF

Usage:
  $prog_name [options] <full pathname to jar list file>

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

if [[ -n "${flag_use_filter_file}" && ! -f "${EMMA_FILTER_FILE}" ]]; then
	echo "ERROR - unable to find EMMA filter file ${EMMA_FILTER_FILE}"
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
	echo "\tJars to instrument: ${JARS_TO_INSTRUMENT[${index}]}\n"
	index=`expr ${index} + 1`
done

# Create some clean directories.
# DIR_JARS_INSTRUMENTED is where the jars will be instrumented.
DIR_JARS_INSTRUMENTED="${WORKSPACE}/jars_instrumented"
for DIR_TO_CREATE in "${DIR_JARS_INSTRUMENTED}"
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

# The EMMA instrumentation command errors out when I try to instrument the
# jars in their original locations, so I copy them to another directory and
# instrument them there.

EMMA_CMD_OPTS="-m overwrite -merge yes -exit"
if [[ -n "${flag_use_filter_file}" ]]; then
	EMMA_CMD_OPTS="${EMMA_CMD_OPTS} -ix @${EMMA_FILTER_FILE}"
fi

echo "EMMA_CMD_OPTS: ${EMMA_CMD_OPTS}"

index=0
while [[ "${index}" -lt "${NUM_DIRS_WITH_JARS_TO_INSTRUMENT}" ]]
do
	if [[ ! -d ${DIR_WITH_JARS_TO_INSTRUMENT[${index}]} ]]; then
		echo "${DIR_WITH_JARS_TO_INSTRUMENT[${index}]} does not exist."
		echo "Skipping..."
		index=`expr ${index} + 1`
		continue
	fi
	
	if [[ -n "${DIR_JARS_INSTRUMENTED}" && "${DIR_JARS_INSTRUMENTED}" != "/" ]]; then
		echo "Cleaning directory: ${DIR_JARS_INSTRUMENTED}..."
		rm ${DIR_JARS_INSTRUMENTED}/*.jar
	else
		echo "DIR_JARS_INSTRUMENTED is invalid: \"${DIR_JARS_INSTRUMENTED}\""
		exit 1
	fi
				
	echo "Copying jars from ${DIR_WITH_JARS_TO_INSTRUMENT[${index}]} to ${DIR_JARS_INSTRUMENTED}..."
	cd ${DIR_WITH_JARS_TO_INSTRUMENT[${index}]}
	JARLIST=""
	ls -1 ${JARS_TO_INSTRUMENT[${index}]} | while read JAR
	do
		cp ${JAR}  "${DIR_JARS_INSTRUMENTED}"
		JARLIST="${JARLIST}:./${JAR}"
		echo -n "${JARLIST}" > ${TMP}/jarlist.txt
	done
	
	JARLIST=`cat ${TMP}/jarlist.txt`

	if [ -z "${JARLIST}" ]; then
		echo "JARLIST is empty."
		echo "Skipping..."
		index=`expr ${index} + 1`
		continue
	fi
		
	echo "JARLIST: ${JARLIST}"
	
	cd "${DIR_JARS_INSTRUMENTED}"
	
	echo "Instrumenting jars in ${DIR_JARS_INSTRUMENTED}..."
	echo "Coverage metadata will be merged to coverage.em"
	
	
	${JAVA_EXE} -cp ${EMMA_JAR} emma instr -ip ${JARLIST} ${EMMA_CMD_OPTS} -out "${WORKSPACE}/coverage.em"
	if [[ $? != 0 ]]; then
		echo "Error trying to instrument jars in ${DIR_JARS_INSTRUMENTED}."
		exit 1
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
