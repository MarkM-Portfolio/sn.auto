#!/bin/bash

SERVER_POOL=${1:-Pipeline}

# Create the workitem.properties file
#
# Decision was made not to create a defect for server pool issues, so commenting out the following line:
#${CI_COMMON_HOME}/gen_workitem_params.sh assign_to_automation

echo "`date +"%F %T,%3N"` [CI-CD] INFO Loading ${CI_COMMON_HOME}/ci_functions.sh..."
. ${CI_COMMON_HOME}/ci_functions.sh
if [ $? != 0 ]; then
	echo "`date +"%F %T,%3N"` [CI-CD] ERROR Failed to load ${CI_COMMON_HOME}/ci_functions.sh."
	exit 1
fi

fail_with_error() {
    echo_error "$@"
    exit 1
}

send_mail() {
	local MAIL_FILE=mail.txt
	echo_with_timestamp "Sending email notification..."
	echo "To: CONN-Automation-CI@notesdev.ibm.com" > ${MAIL_FILE}
	echo "Subject: Pool Server ${WAS_HOST_FQDN} Sanity Check Failure!" >> ${MAIL_FILE}
	echo "" >> ${MAIL_FILE}
	echo "${WAS_HOST_FQDN} failed the post-reservation sanity check." >> ${MAIL_FILE}
	echo "" >> ${MAIL_FILE}
	echo "Build: ${BUILD_LABEL}" >> ${MAIL_FILE}
	echo "Job: ${BUILD_URL}" >> ${MAIL_FILE}

	cat ${MAIL_FILE} | /usr/sbin/sendmail -tv
}

create_server_properties_file() {
	# Create a properties file so that Jenkins can pass the server's
	# parameters to other jobs.
	echo_info "Creating server properties file: ${SERVER_INFO_PROPERTIES_FILE}..."
	cat <<EOF > ${SERVER_INFO_PROPERTIES_FILE}	
WAS_HOST_FQDN=${WAS_HOST_FQDN}
WAS_HOST_ID=${WAS_HOST_ID}
WAS_HOME=${WAS_HOME}
WAS_PROFILE=${WAS_PROFILE}
WAS_SERVER=${WAS_SERVER}
WAS_NODE=${WAS_NODE}
WAS_CELL=${WAS_CELL}
WAS_PORT_NORMAL=${WAS_PORT_NORMAL}
WAS_PORT_SECURE=${WAS_PORT_SECURE}
WAS_ADMIN=${WAS_ADMIN}
REMOTE_USER=${REMOTE_USER}
REMOTE_USER_HOME=${REMOTE_USER_HOME}
REMOTE_LCUPDATE_DIR=${REMOTE_LCUPDATE_DIR}
REMOTE_JAVA_HOME=${REMOTE_JAVA_HOME}
REMOTE_CI_HOME=${REMOTE_CI_HOME}
CONNECTIONS_SHARED_DATA_DIR=${CONNECTIONS_SHARED_DATA_DIR}
SERVER_STATUS_FILENAME=${SERVER_STATUS_FILENAME}
IHS_PORT_SECURE=${IHS_PORT_SECURE}

EOF
	
	echo ""
	cat ${SERVER_INFO_PROPERTIES_FILE}
	echo ""
}

SERVER_INFO_PROPERTIES_FILE="server-info.properties"
WAS_PROPERTIES_FILE="was.properties"
echo_info "Cleaning out any existing server info properties file: ${SERVER_INFO_PROPERTIES_FILE}..."
rm -f ${SERVER_INFO_PROPERTIES_FILE}
echo_info "Cleaning out any existing was properties file: ${WAS_PROPERTIES_FILE}..."
rm -f ${WAS_PROPERTIES_FILE}

. ${CI_COMMON_HOME}/was_reservation_utils.sh
if [ $? != 0 ]; then
	echo_error "Failed to load ${CI_COMMON_HOME}/was_reservation_utils.sh."
	create_server_properties_file
	fail_with_error ""
fi

MAX_NUM_TRIES=2
NUM_SERVERS_TRIED=0
FOUND_GOOD_SERVER=false
while [ ${NUM_SERVERS_TRIED} -lt ${MAX_NUM_TRIES} ]
do	
	OK=true
	index=0
	while true
	do
		echo_info "PATH: ${PATH}"
		echo_info "python: `which python`"
		reserve_a_was ${SERVER_POOL}
		if [ $? == 0 ]; then
			break
		fi
	
		index=`expr ${index} + 1`
		if [ ${index} -lt 2 ]; then
			echo_warn "Will retry Server Pool \"${SERVER_POOL}\" in 5 minutes."
			sleep 300
			continue
		fi

		echo_error "Giving up..."
		OK=false
		break
	done

	create_server_properties_file
	
	if [ ${OK} != true ]; then
		fail_with_error ""
	fi

	# Create a properties file to copy to server.
	cp ${SERVER_INFO_PROPERTIES_FILE} ${WAS_PROPERTIES_FILE}
	echo "WAS_HOST_PWD=${WAS_HOST_PWD}" >> ${WAS_PROPERTIES_FILE}
	echo "WAS_ADMIN_PASSWORD=${WAS_ADMIN_PASSWORD}" >> ${WAS_PROPERTIES_FILE}

	echo_info "Copying ${WAS_PROPERTIES_FILE} to ${REMOTE_USER}@${WAS_HOST_FQDN}:${REMOTE_USER_HOME}..."
	scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no ${WAS_PROPERTIES_FILE} ${REMOTE_USER}@${WAS_HOST_FQDN}:${REMOTE_USER_HOME}
	if [ $? != 0 ]; then
		echo_error "Could not copy ${WAS_PROPERTIES_FILE} to ${REMOTE_USER_HOME} on ${WAS_HOST_FQDN}."
		fail_with_error ""
	fi

	echo_info "Adding WAS property entries to ${REMOTE_USER_HOME}/.bashrc on ${WAS_HOST_FQDN}..."
	ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no ${REMOTE_USER}@${WAS_HOST_FQDN} "echo \". ./${WAS_PROPERTIES_FILE}\" >> ${REMOTE_USER_HOME}/.bashrc"
	if [ $? != 0 ]; then
		echo_error "Could not add entry to ${REMOTE_USER_HOME}/.bashrc on ${WAS_HOST_FQDN}."
		fail_with_error ""
	fi

	# Sanity check the Connections apps.
	echo_info "Sanity checking Connection apps post-reservation..."
	${CI_COMMON_HOME}/check_a_was.sh ${WAS_HOST_FQDN} check_for_code_coverage_jar
	if [ $? == 0 ]; then
		FOUND_GOOD_SERVER=true
		break
	fi
	
	echo_info "${WAS_HOST_FQDN} failed the sanity check."
	send_mail
	
	NUM_SERVERS_TRIED=`expr ${NUM_SERVERS_TRIED} + 1`
	[ ${NUM_SERVERS_TRIED} -lt ${MAX_NUM_TRIES} ] && echo_warn "Trying to reserve another server..."
done

[ ${FOUND_GOOD_SERVER} == true ] || fail_with_error "Could not reserve a good server from Server Pool."

# Script has passed, so delete the workitem properties file
echo_info "Removing workitem.properties..."
rm -fv workitem.properties

exit 0
