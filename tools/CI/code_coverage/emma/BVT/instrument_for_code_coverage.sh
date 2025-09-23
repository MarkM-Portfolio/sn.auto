#!/bin/ksh

usage() {
    prog_name=`basename ${MYSELF}`
    cat <<EOF

Usage:
  $prog_name <full pathname to configuration file> <full pathname to log file>

EOF
    exit 1
}

echo2() {
    echo $* | tee -a "${LOG}"
}

MYSELF=`readlink -f $0`

if [[ -z "$1" ]]; then
    echo "ERROR:"
    echo "  You need to specify a configuration file"
    usage
fi

if [[ -z "$2" ]]; then
    echo "ERROR:"
    echo "  You need to specify a log file"
    usage
fi

CONFIG_FILE=$1
CONFIG_FILE_NAME=`basename ${CONFIG_FILE}`
LOG=$2

if [[ ! -f "${CONFIG_FILE}" ]]; then
    print "ERROR - unable to find configuration file ${CONFIG_FILE}."
    exit 1
fi

. "${CONFIG_FILE}"
if [[ $? -ne 0 ]]; then
    print "ERROR - failed to load configuration file ${CONFIG_FILE}."
    exit 1
fi

if [[ ! -f "${LOG}" ]]; then
    print "ERROR - unable to find log file ${LOG}."
    exit 1
fi

# Get the HOME directory on the remote host so we can create an EMMA directory there.
echo2 -n "Getting HOME directory on ${REMOTE_HOST}..."
REMOTE_HOME_DIR=`ssh ${REMOTE_USER}@${REMOTE_HOST} 'echo ${HOME}'`
if [ $? != 0 ] ; then
	echo2 "FAIL"
	return 1
else
	echo2 "OK"
fi
	
echo2 "REMOTE_HOME_DIR: ${REMOTE_HOME_DIR}"
	
	REMOTE_EMMA_HOME_DIR=${REMOTE_HOME_DIR}/emma
	LOCAL_EMMA_HOME_DIR=${HOME}/emma
	
	EMMA_SUBDIRECTORIES_TO_CREATE="scripts \
	coverage_metadata \
	coverage_runtimedata \
	reports/html_reports \
	reports/xml_reports \
	reports/summary_reports"
	
	for EMMA_SUBDIRECTORY in ${EMMA_SUBDIRECTORIES_TO_CREATE}
	do
		echo2 -n "Creating directory ${REMOTE_EMMA_HOME_DIR}/${EMMA_SUBDIRECTORY} on ${REMOTE_HOST}..."
		ssh ${REMOTE_USER}@${REMOTE_HOST} "mkdir -p ${REMOTE_EMMA_HOME_DIR}/${EMMA_SUBDIRECTORY}" >>${LOG} 2>&1
		if [ $? != 0 ] ; then
			echo2 "FAIL"
			return 1
		else
			echo2 "OK"
		fi
	done

	EMMA_SCRIPT_LIST="emma_instr.sh \
	emma_get_coverage_data.sh \
	emma_generate_reports.sh \
	emma_generate_all_reports.sh \
	dump_coverage_data.sh \
	emma_jar_lists.sh \
	gen_executive_summary.sh \
	gen_executive_summary.py"
		
	for EMMA_SCRIPT in ${EMMA_SCRIPT_LIST}
	do	
		echo2 -n "Copying code coverage file ${LOCAL_EMMA_HOME_DIR}/scripts/${EMMA_SCRIPT} to ${REMOTE_USER}@${REMOTE_HOST}:${REMOTE_EMMA_HOME_DIR}/scripts..."
		scp ${LOCAL_EMMA_HOME_DIR}/scripts/${EMMA_SCRIPT} ${REMOTE_USER}@${REMOTE_HOST}:${REMOTE_EMMA_HOME_DIR}/scripts >>${LOG} 2>&1
		if [ $? != 0 ] ; then
			echo2 "FAIL"
			return 1
		else
			echo2 "OK"
		fi
	done
	
	echo2 -n "Copying configuration file ${CONFIG_FILE} to ${REMOTE_USER}@${REMOTE_HOST}:${REMOTE_EMMA_HOME_DIR}/scripts..."
	scp ${CONFIG_FILE} ${REMOTE_USER}@${REMOTE_HOST}:${REMOTE_EMMA_HOME_DIR}/scripts >>${LOG} 2>&1
	if [ $? != 0 ] ; then
		echo2 "FAIL"
		return 1
	else
		echo2 "OK"
	fi
	
	echo2 -n "Copying EMMA jar file ${LOCAL_EMMA_HOME_DIR}/emma.jar to ${REMOTE_USER}@${REMOTE_HOST}:${DIR_WAS_JRE_LIB_EXT}..."
	scp ${LOCAL_EMMA_HOME_DIR}/emma.jar ${REMOTE_USER}@${REMOTE_HOST}:${DIR_WAS_JRE_LIB_EXT} >>${LOG} 2>&1
	if [ $? != 0 ] ; then
		echo2 "FAIL"
		return 1
	else
		echo2 "OK"
	fi
	
	EMMA_FILE_LIST="emma_filter.txt \
	emma.jar"
	
	for EMMA_FILE in ${EMMA_FILE_LIST}
	do
		echo2 -n "Copying EMMA file ${LOCAL_EMMA_HOME_DIR}/${EMMA_FILE} to ${REMOTE_USER}@${REMOTE_HOST}:${REMOTE_EMMA_HOME_DIR}..."
		scp ${LOCAL_EMMA_HOME_DIR}/${EMMA_FILE} ${REMOTE_USER}@${REMOTE_HOST}:${REMOTE_EMMA_HOME_DIR} >>${LOG} 2>&1
		if [ $? != 0 ] ; then
			echo2 "FAIL"
			return 1
		else
			echo2 "OK"
		fi
	done
	
	echo2 -n "Instrumenting jar files on ${REMOTE_HOST}..."
    ssh ${REMOTE_USER}@${REMOTE_HOST} "cd ${REMOTE_EMMA_HOME_DIR}/scripts && ./emma_instr.sh -x ./${CONFIG_FILE_NAME} ./emma_jar_lists.sh" >>${LOG} 2>&1
	if [ $? != 0 ] ; then
		echo2 "FAIL"
		return 1
	else
		echo2 "OK"
	fi

exit 0
