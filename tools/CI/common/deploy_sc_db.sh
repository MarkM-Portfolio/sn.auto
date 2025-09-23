#!/bin/sh

APPS="$1"
OK=true

SC_CREATED_BACKUP_ROOTDIR=/local/ci/original_sc_backups
LOCALLY_CREATED_DB_BACKUP_ROOTDIR=/local/ci/db2_backups
LOG_ROOTDIR=/local/ci/db2_logs

for APP in ${APPS}
do
	case ${APP} in
		activities)
			DB_NAME=CON_ACT
			DB2INST=db2inst2
			;;
	
		blogs)
			DB_NAME=BLOGS
			DB2INST=db2inst2
			;;
	
		communities)
			DB_NAME=COMMUN
			DB2INST=db2inst3
			;;
	
		files)
			DB_NAME=CON_FILE
			DB2INST=db2inst2
			;;
		
		forums)
			DB_NAME=CON_FOR
			DB2INST=db2inst2
			;;
		
		homepage)
			DB_NAME=CON_HOME
			DB2INST=db2inst2
			;;
		
		profiles)
			DB_NAME=PROFILES
			DB2INST=db2inst5
			;;

		wikis)
			DB_NAME=WIKIS
			DB2INST=db2inst2
			;;

		*)
			echo "Unknown app: ${APP}"
			OK=false
			continue
			;;
	esac
			
	DB2INST_HOME=/local/home/db2s/${DB2INST}
	ERROR_LOG_FILE=${DB2INST_HOME}/db2_error.log
	sudo rm -f ${ERROR_LOG_FILE} > /dev/null 2>&1
	
	for DIR in ${SC_CREATED_BACKUP_ROOTDIR}/${APP} ${LOCALLY_CREATED_DB_BACKUP_ROOTDIR}/${APP} ${LOG_ROOTDIR}/${APP}
	do
		if [ "${DIR}" == "/" ]; then
			echo "Directory is root dir; skipping..."
			continue
		fi

		echo "Creating directory ${DIR}..."
		sudo rm -rf ${DIR} > /dev/null 2>&1
		sudo mkdir -p ${DIR}
		sudo chmod -R 777 ${DIR}
	done

	echo "Copying original SC ${DB_NAME} DB backup from jenksub3 to ${SC_CREATED_BACKUP_ROOTDIR}/${APP}..."
	scp icci@jenksub3.cnx.cwp.pnp-hcl.com:/local/ci/daily_db/${DB_NAME}.* ${SC_CREATED_BACKUP_ROOTDIR}/${APP}
	if [ $? != 0 ]; then
		 echo "Unable to copy /local/ci/daily_db/${DB_NAME}.* on jenksub3 to ${SC_CREATED_BACKUP_ROOTDIR}/${APP}."
		 OK=false
		 continue
	fi
		
	echo "Dropping database ${DB_NAME}..."
	sudo su - ${DB2INST} -c "db2 drop db ${DB_NAME}"
	
	echo "Restoring database ${DB_NAME} from ${SC_CREATED_BACKUP_ROOTDIR}/${APP}..."
	sudo su - ${DB2INST} -c "db2 restore db ${DB_NAME} from ${SC_CREATED_BACKUP_ROOTDIR}/${APP} ON ${DB2INST_HOME} into ${DB_NAME} logtarget ${LOG_ROOTDIR}/${APP} replace existing"
	if [ $? -gt 3 ]; then
		echo  "Unable to restore ${DB_NAME} database from ${SC_CREATED_BACKUP_ROOTDIR}/${APP}."
		OK=false
		continue
	fi
	
	echo "Rolling the ${DB_NAME} database forward..."
	sudo su - ${DB2INST} -c "db2 rollforward db ${DB_NAME} to end of logs and stop overflow log path \(${LOG_ROOTDIR}/${APP}\) noretrieve"
	if [ $? -gt 3 ]; then
		echo "Unable to rollforward ${DB_NAME} database."
		OK=false
		continue
	fi
	
	echo "Making a backup of the restored ${DB_NAME} database..."
	sudo su - ${DB2INST} <<HERE	
		echo "Connecting to ${DB_NAME}..."
		db2 connect to ${DB_NAME}
		[ $? -lt 4 ] || echo "Could not connect to ${DB_NAME}." | tee ${ERROR_LOG_FILE}
		
		echo "Quiescing ${DB_NAME}..."
		db2 QUIESCE DATABASE IMMEDIATE FORCE CONNECTIONS
		[ $? -lt 4 ] || echo "Could not quiesce ${DB_NAME}." | tee -a ${ERROR_LOG_FILE}
		
		echo "Unquiescing ${DB_NAME}..."
		db2 UNQUIESCE DATABASE
		[ $? -lt 4 ] || echo "Could not unquiesce ${DB_NAME}." | tee -a ${ERROR_LOG_FILE}
		
		echo "Terminating..."
		db2 terminate
		[ $? -lt 4 ] || echo "Could not terminate." | tee -a ${ERROR_LOG_FILE}
		
		echo "Deactivating ${DB_NAME}..."
		db2 deactivate database ${DB_NAME}
		[ $? -lt 4 ] || echo "Could not deactivate ${DB_NAME}." | tee -a ${ERROR_LOG_FILE}
		
		echo "Backing up ${DB_NAME} to ${LOCALLY_CREATED_DB_BACKUP_ROOTDIR}/${APP}..."
		db2 backup database ${DB_NAME} to ${LOCALLY_CREATED_DB_BACKUP_ROOTDIR}/${APP}
		[ $? -lt 4 ] || echo "Could not backup ${DB_NAME}." | tee -a ${ERROR_LOG_FILE}

		[ -f ${ERROR_LOG_FILE} ] && chmod 664 ${ERROR_LOG_FILE}
HERE

	if [ -f ${ERROR_LOG_FILE} ]; then
		cat ${ERROR_LOG_FILE}
		echo "Failed to backup ${DB_NAME}."
		OK=false
		continue
	fi

	echo "Changing permissions of backup file in ${LOCALLY_CREATED_DB_BACKUP_ROOTDIR}/${APP}..."
	sudo chown icci:swg ${LOCALLY_CREATED_DB_BACKUP_ROOTDIR}/${APP}/*
	sudo chmod 755 ${LOCALLY_CREATED_DB_BACKUP_ROOTDIR}/${APP}/*
	
	echo "Creating directory /local/ci/db2_backups_sc/${APP} on jenksub3 for backup file..."
	ssh icci@jenksub3.cnx.cwp.pnp-hcl.com mkdir -p /local/ci/db2_backups_sc/${APP}
	if [ $? != 0 ]; then
		echo "Unable to create directory /local/ci/db2_backups_sc/${APP} on jenksub3 for backup file."
		OK=false
		continue
	fi

	echo "Copying ${LOCALLY_CREATED_DB_BACKUP_ROOTDIR}/${APP}/* to /local/ci/db2_backups_sc/${APP} onjenksub3..."
	scp ${LOCALLY_CREATED_DB_BACKUP_ROOTDIR}/${APP}/* icci@jenksub3.cnx.cwp.pnp-hcl.com:/local/ci/db2_backups_sc/${APP}
	if [ $? != 0 ]; then
		echo "Unable to copy ${LOCALLY_CREATED_DB_BACKUP_ROOTDIR}/${APP}/* to /local/ci/db2_backups_sc/${APP} onjenksub3."
		OK=false
		continue
	fi

	echo "Successfully created backup for ${DB_NAME} on jenksub3."

done
	
if [ ${OK} != true ]; then
	echo "There were errors."
	exit 1
fi

exit 0
