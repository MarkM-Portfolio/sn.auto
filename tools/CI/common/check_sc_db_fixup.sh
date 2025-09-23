#!/bin/sh

APP="$1"

fail_with_error() {
    echo "$@"
    exit 1
}

send_mail() {
	local MAIL_FILE=mail.txt
	echo "Sending email notification..."
	echo "To: CONN-Automation-CI@notesdev.ibm.com" > ${MAIL_FILE}
	echo "Subject: ${DB2INST} was restarted on ${NODE_NAME}!" >> ${MAIL_FILE}
	echo "" >> ${MAIL_FILE}
	echo "Found ${DB2INST} not runnning on ${NODE_NAME} for database ${DB_NAME}." >> ${MAIL_FILE}
	echo "Restarted it." >> ${MAIL_FILE}
	echo "" >> ${MAIL_FILE}
	echo "Build: ${BUILD_LABEL}" >> ${MAIL_FILE}
	echo "Job: ${BUILD_URL}" >> ${MAIL_FILE}

	cat ${MAIL_FILE} | /usr/sbin/sendmail -tv
}

# Create a workitem properties file
${CI_COMMON_HOME}/gen_workitem_params.sh assign_to_automation

[ -n "${BUILD_LABEL}" ] || fail_with_error "\${BUILD_LABEL} is null."

SC_DB_OWNER_NAME=db2admin
SC_DB_OWNER_PWD=widget@1bm

echo "Loading ${CI_COMMON_HOME}/system.properties..."
. ${CI_COMMON_HOME}/system.properties
if [ $? != 0 ]; then
	echo "Failed to load ${CI_COMMON_HOME}/system.properties."
	exit 1
fi

COMPONENT_NAME=${APP}

case ${APP} in
	activities)
		FE_LIST="activities.api activities.impl"
		DB_NAME=CON_ACT
		DB2INST=db2inst1
		;;
	
	blogs)
		FE_LIST="sn.blogs"
		DB_NAME=BLOGS
		DB2INST=db2inst2
		;;
	
	communities)
		FE_LIST="sn.comm"
		DB_NAME=COMMUN
		DB2INST=db2inst3
		;;

	files)
		FE_LIST="share"
		DB_NAME=CON_FILE
		DB2INST=db2inst4
		;;

	forums)
		FE_LIST="sn.forum"
		COMPONENT_NAME=forum
		DB_NAME=CON_FOR
		DB2INST=db2inst5
		;;

	homepage)
		FE_LIST="sn.homepage"
		DB_NAME=CON_HOME
		DB2INST=db2inst6
		;;

	profiles)
		FE_LIST="sn.profiles"
		DB_NAME=PROFILES
		DB2INST=db2inst7
		;;

	wikis)
		FE_LIST="share"
		DB_NAME=WIKIS
		DB2INST=db2inst8
		;;		
	
	*)
		echo "Unknown app: ${APP}"
		exit 1
		;;
esac
			
DB2INST_HOME=${DB2INST_HOME_ROOT}/${DB2INST}

echo "APP: ${APP}"
echo "COMPONENT_NAME: ${COMPONENT_NAME}"
echo "FE_LIST: ${FE_LIST}"
echo "DB_NAME: ${DB_NAME}"
echo "DB2INST: ${DB2INST}"
echo "DB2INST_HOME: ${DB2INST_HOME}"

ERROR_LOG_FILE=${DB2INST_HOME}/db2_error.log
sudo rm -f ${ERROR_LOG_FILE} > /dev/null 2>&1

NEW_LOG_PATH=/tmp/db2logs/${APP}
sudo rm -rf /tmp/db2logs/${APP} > /dev/null 2>&1
sudo mkdir -p /tmp/db2logs/${APP}
sudo chmod -R 777 /tmp/db2logs
	
echo "Updating ${CI_COMMON_HOME}/lc-update..."
cd ${CI_COMMON_HOME}/lc-update
git pull
cd "${WORKSPACE}"

# Clean out any old builds.
for FE_NAME in ${FE_LIST}
do
	echo "Deleting ${FE_NAME}..."
	rm -rf ${FE_NAME}
done

echo "Deleting xkit..."
rm -rf xkit

# Pull down the build.
echo "Downloading build [${BUILD_LABEL}]..."
${CI_COMMON_HOME}/lc-update/bin/get-build.sh -f -x -b ${BUILD_LABEL}
[ $? -eq 0 ] || fail_with_error "Failed to download build [${BUILD_LABEL}]."

echo "Changing permissions for ${WORKSPACE}/xkit directory tree..."
chmod -R +rX "${WORKSPACE}/xkit"

# Clean out old logs.
OLD_LOG_ROOT_DIR=/db2_databases
OLD_LOG_DIR_NAME=`echo ${DB_NAME} | tr '[:upper:]' '[:lower:]'`
echo "Deleting ${OLD_LOG_ROOT_DIR}/${OLD_LOG_DIR_NAME}..."
sudo su - root -c "cd ${OLD_LOG_ROOT_DIR}; rm -rf ${OLD_LOG_DIR_NAME}"

# Make sure the DB2 instance is running; if not, try to restart it.
echo "Checking that ${DB2INST} is running..."
sudo su - ${DB2INST} -c "db2 list applications"
if [ $? -ge 4 ]; then
	echo "WARNING: Could not list applications for ${DB2INST}; will try to start ${DB2INST}."
	sudo su - ${DB2INST} -c "db2start"
	[ $? -lt 4 ] || fail_with_error "Could not start ${DB2INST}."
	echo "Successfully started ${DB2INST}."
	send_mail
fi
echo "${DB2INST} is running..."

# Drop the DB just to be safe. Don't really need to since we'll be using a "replace existing" restore.
echo "Dropping database ${DB_NAME}..."
sudo su - ${DB2INST} -c "db2 drop db ${DB_NAME}"
	
# Restore the DB.
echo "restoring database ${DB_NAME} from backup in directory ${DB2_BACKUP_ROOT_DIR}/${APP}..."
sudo su - ${DB2INST} -c "db2 restore db ${DB_NAME} from ${DB2_BACKUP_ROOT_DIR}/${APP} ON ${DB2INST_HOME} into ${DB_NAME} newlogpath ${NEW_LOG_PATH} replace existing"
[ $? -lt 4 ] || fail_with_error "Unable to restore db ${DB_NAME} from ${DB2_BACKUP_ROOT_DIR}/${APP}."

# Grant the DB2 instance owner rights to the restored DB.
echo "Granting rights to database ${DB_NAME} to ${DB2INST}..."
sudo su - ${DB2INST} <<HERE
	echo "Connecting to ${DB_NAME}..."
	db2 connect to ${DB_NAME} user ${SC_DB_OWNER_NAME} using ${SC_DB_OWNER_PWD}
	[ $? -lt 4 ] || echo "Could not connect to ${DB_NAME} using user ${SC_DB_OWNER_NAME} with password ${SC_DB_OWNER_PWD}." | tee ${ERROR_LOG_FILE}
	
	echo "Granting secadm rights to ${DB2INST}..."
	db2 grant secadm on database to user ${DB2INST}
	[ $? -lt 4 ] || echo "Could not grant secadm rights to ${DB2INST}." | tee -a  ${ERROR_LOG_FILE}
	
	echo "Granting dbadm rights to ${DB2INST}..."
	db2 grant dbadm on database to user ${DB2INST}
	[ $? -lt 4 ] || echo "Could not grant dbadm rights to ${DB2INST}." | tee -a  ${ERROR_LOG_FILE}
	
	echo "Resetting connection..."
	db2 connect reset
	[ $? -lt 4 ] || echo "Could not reset connection." | tee -a  ${ERROR_LOG_FILE}

	[ -f ${ERROR_LOG_FILE} ] && chmod 664 ${ERROR_LOG_FILE}
HERE
	
if [ -f ${ERROR_LOG_FILE} ]; then
	cat ${ERROR_LOG_FILE}
	fail_with_error "Failed to grant rights to ${DB_NAME} database to ${DB2INST}."
fi

# For debugging, print out the database config information.
echo "Printing out database config information for ${DB_NAME} before disabling archive logging..."
sudo su - ${DB2INST} -c "db2 get db cfg for ${DB_NAME}"

# Turn off archive logging - that's only needed if we needed the restored database to be rollforward recoverable.
echo "Turning off archive logging for ${DB_NAME}..."
sudo su - ${DB2INST} -c "db2 update db cfg for ${DB_NAME} using logarchmeth1 off"
sudo su - ${DB2INST} -c "db2 update db cfg for ${DB_NAME} using logarchmeth2 off"
# increase the number of secondary logs to 50
sudo su - ${DB2INST} -c "db2 update db cfg for ${DB_NAME} using logsecond 50"

# For debugging, print out the database config information.
echo "Printing out database config information for ${DB_NAME} after disabling archive logging..."
sudo su - ${DB2INST} -c "db2 get db cfg for ${DB_NAME}"

# Create a workitem properties file
${CI_COMMON_HOME}/gen_workitem_params.sh

# Run the fixup scripts to update the DB..."
echo "Updating database..."
sudo -i -u ${DB2INST} ${CI_COMMON_HOME}/lc-update/bin/fixup_db2.sh -s -d ${DB_NAME} -p "${WORKSPACE}/xkit/connections.sql" \
   -u ${SC_DB_OWNER_NAME} -w ${SC_DB_OWNER_PWD}
[ $? -eq 0 ] || fail_with_error "There were errors updating database ${DB_NAME}."	


# Validate that the DB was really updated
echo "Getting updated DB schema version..."
SCHEMA_VERSION_UPDATED=`sudo -i -u ${DB2INST} ${CI_COMMON_HOME}/lc-update/bin/create_db.sh -n ${DB_NAME}  -q ${COMPONENT_NAME}`
[ $? -eq 0 ] || echo "Could not get updated DB schema version for component ${COMPONENT_NAME} for database ${DB_NAME}."	
echo "Updated schema version: ${SCHEMA_VERSION_UPDATED}"

echo "Getting expected DB schema version..."
SCHEMA_VERSION_EXPECTED=`sudo -i -u ${DB2INST} ${CI_COMMON_HOME}/lc-update/bin/create_db.sh -p "${WORKSPACE}/xkit/connections.sql" -z ${COMPONENT_NAME}`
[ $? -eq 0 ] || fail_with_error "Could not get expected DB schema version for component ${COMPONENT_NAME}."	
echo "Expected schema version: ${SCHEMA_VERSION_EXPECTED}"

echo "Checking that updated database schema version ${SCHEMA_VERSION_UPDATED} matches expected version ${SCHEMA_VERSION_EXPECTED}..."
[ ${SCHEMA_VERSION_UPDATED} -eq ${SCHEMA_VERSION_EXPECTED} ]  || fail_with_error "Updated schema version doesn't match expected schema version."	

exit 0
