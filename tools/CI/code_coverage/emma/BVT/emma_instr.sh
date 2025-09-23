#!/bin/ksh

usage() {
    prog_name=`basename ${MYSELF}`
    cat <<EOF

Usage:
  $prog_name [options] <full pathname to configuration file> <full pathname to jar list file>

Options:
  -c        Copy EMMA jar file to WAS java/jre/lib/ext directory.
            If this script was launched via re-deploy.sh, that
            script will have copied the jar file, so use this option only
            executing this script locally and there is an EMMA jar file
            in SRC_EMMA_JAR. (optional)
            
  -x		Use the EMMA_FILTER_FILE as the EMMA filter
			file. (optional)
			
  -s		Stop WAS before instrumentation if it's running. (optional)
  
  -S		Start WAS after instrumentation if it's not running. (optional)
EOF
    exit 1
}

MYSELF=`readlink -f $0`

while getopts cxsS opt
do
    case $opt in
        c) flag_copy_emma_jar=1;;
        x) flag_use_filter_file=1;;
        s) flag_stop_WAS=1;;
        S) flag_start_WAS=1;;
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
	print "Component: ${COMPONENT[${index}]}"
	print "Directory with jars to instrument: ${DIR_WITH_JARS_TO_INSTRUMENT[${index}]}"
	print "\tJars to instrument: ${JARS_TO_INSTRUMENT[${index}]}\n"
	index=`expr ${index} + 1`
done

if [[ -n "${flag_stop_WAS}" ]]; then
	print "Request to shut down WAS."
	ps -ef | grep -i ${REMOTE_WAS_HOME} | grep -v grep > /dev/null 2>&1
	if [[ $? == 0 ]]
	then
		print "Found WAS running; will shut it down..."
		${DIR_WAS_BIN}/stopServer.sh ${REMOTE_WAS_SERVER} -username ${REMOTE_WAS_USER} -password ${REMOTE_WAS_PSWD}
		if [[ $? != 0 ]]
		then
			print 'Could not stop WAS.'
			exit 1
		fi
	else
		print "WAS already stopped."
	fi
fi

if [[ -n "${flag_copy_emma_jar}" ]]; then
	print "Copying ${SRC_EMMA_JAR} to ${EMMA_JAR}..."
	cp ${SRC_EMMA_JAR} ${EMMA_JAR}
	if [[ $? != 0 ]]
	then
		print "Could not copy ${SRC_EMMA_JAR} to ${EMMA_JAR}"
		exit 1
	fi
fi

# DIR_JARS_INSTRUMENTED is where the jars will be instrumented.
if [[ ! -d ${DIR_JARS_INSTRUMENTED} ]]
then
	print "Creating directory: ${DIR_JARS_INSTRUMENTED}..."
	mkdir ${DIR_JARS_INSTRUMENTED}
	if [[ $? != 0 ]]
	then
		print "Could not create directory: ${DIR_JARS_INSTRUMENTED}"
		exit 1
	fi
fi

# Clean out any old coverage metadata files
ls ${EMMA_METADATA_DIR}/*.em > /dev/null 2>&1
if [[ $? == 0 ]]
then
	print "Removing old coverage metadata files in ${EMMA_METADATA_DIR}..."
	rm ${EMMA_METADATA_DIR}/*.em
	if [[ $? != 0 ]]
	then
		print "Could not remove coverage metadata files in ${EMMA_METADATA_DIR}"
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
	if [[ ! -d ${DIR_WITH_JARS_TO_INSTRUMENT[${index}]} ]]
	then
		print "${DIR_WITH_JARS_TO_INSTRUMENT[${index}]} does not exist."
		print "Skipping..."
		index=`expr ${index} + 1`
		continue
	fi
	
	print "Cleaning directory: ${DIR_JARS_INSTRUMENTED}..."
	rm ${DIR_JARS_INSTRUMENTED}/*.jar
	
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
	print "Coverage metadata will be merged to ${EMMA_METADATA_DIR}/${COMPONENT[${index}]}.em"
	cd ${DIR_JARS_INSTRUMENTED}
	
	${JAVA_EXE} -cp ${EMMA_JAR} emma instr -ip ${JARLIST} ${EMMA_CMD_OPTS} -out ${EMMA_METADATA_DIR}/${COMPONENT[${index}]}.em
	if [[ $? != 0 ]]
	then
		print "Error trying to instrument jars in ${DIR_JARS_INSTRUMENTED}."
		exit 1
	fi

	cd ${DIR_EMMA_HOME}

	print "Copying instrumented jars back to ${DIR_WITH_JARS_TO_INSTRUMENT[${index}]}..."
	cp ${DIR_JARS_INSTRUMENTED}/*.jar ${DIR_WITH_JARS_TO_INSTRUMENT[${index}]}
	if [[ $? != 0 ]]
	then
		print "Error trying to copy jars back to ${DIR_WITH_JARS_TO_INSTRUMENT[${index}]}."
		exit 1
	fi

	index=`expr ${index} + 1`
done

if [[ -n "${flag_start_WAS}" ]]; then
	print "Request to start up WAS."
	ps -ef | grep -i ${REMOTE_WAS_HOME} | grep -v grep > /dev/null 2>&1
	if [[ $? != 0 ]]
	then
		print "Found WAS stopped; will start it up..."
		${DIR_WAS_BIN}/startServer.sh ${REMOTE_WAS_SERVER}
		if [[ $? != 0 ]]
		then
			print 'Could not start WAS.'
			exit 1
		fi
	else
		print "WAS already running."
	fi
fi


exit 0
