#!/bin/sh
#
# Start all the DB2 instances.
# Will be called by /etc/rc.d/rc.local.
#
for DB2INST in db2inst1
do
	LOGFILE="start_${DB2INST}.log"
	
	if [ -f ${LOGFILE} ]; then
		mv ${LOGFILE} ${LOGFILE}.old
	fi
	
	sudo su - ${DB2INST} -c "db2start" 1>${LOGFILE} 2>&1
	sleep 10
done
