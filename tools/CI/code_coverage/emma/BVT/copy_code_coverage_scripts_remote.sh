#!/bin/ksh

. "${CI_COMMON_HOME}/system.properties"
if [[ $? -ne 0 ]]; then
    print "ERROR - failed to load properties file ${CI_COMMON_HOME}/system.properties."
    exit 1
fi

. ./server-info.properties
if [[ $? -ne 0 ]]; then
    print "ERROR - failed to load properties file server-info.properties."
    exit 1
fi

print -n "Creating directory ${REMOTE_EMMA_SCRIPT_DIR} on ${WAS_HOST_FQDN}..."
ssh ${REMOTE_USER}@${WAS_HOST_FQDN} "mkdir -p ${REMOTE_EMMA_SCRIPT_DIR}"
if [ $? != 0 ] ; then
	print "FAIL"
	exit 1
else
	print "OK"
fi

EMMA_SCRIPT_LIST="instrument_for_code_coverage_server.sh \
get_code_coverage_data_server.sh \
generate_code_coverage_report.sh \
dump_code_coverage_data_server.sh \
emma_jar_lists_server.sh"
		
	for EMMA_SCRIPT in ${EMMA_SCRIPT_LIST}
	do	
		print -n "Copying code coverage file ${EMMA_HOME}/scripts/${EMMA_SCRIPT} to ${REMOTE_USER}@${WAS_HOST_FQDN}:${REMOTE_EMMA_SCRIPT_DIR}..."
		scp ${EMMA_HOME}/scripts/${EMMA_SCRIPT} ${REMOTE_USER}@${WAS_HOST_FQDN}:${REMOTE_EMMA_SCRIPT_DIR}
		if [ $? != 0 ] ; then
			print "FAIL"
			return 1
		else
			print "OK"
		fi
	done
			
	EMMA_FILE_LIST="emma_filter.txt \
	emma.jar"
	
	for EMMA_FILE in ${EMMA_FILE_LIST}
	do
		print -n "Copying EMMA file ${EMMA_HOME}/${EMMA_FILE} to ${REMOTE_USER}@${WAS_HOST_FQDN}:${REMOTE_EMMA_HOME}..."
		scp ${EMMA_HOME}/${EMMA_FILE} ${REMOTE_USER}@${WAS_HOST_FQDN}:${REMOTE_EMMA_HOME}
		if [ $? != 0 ] ; then
			print "FAIL"
			return 1
		else
			print "OK"
		fi
	done
	
exit 0
