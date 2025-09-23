#!/bin/sh
#
# Start all the DB2 instances.
# Will be called by /etc/rc.d/rc.local.
#
for DB2INST in db2inst1 db2inst2 db2inst3 db2inst4 db2inst5
do
	LOGFILE="start_${DB2INST}.log"
	
	if [ -f ${LOGFILE} ]; then
		mv ${LOGFILE} ${LOGFILE}.old
	fi
	
	su - ${DB2INST} -c "db2start" 1>${LOGFILE} 2>&1
	sleep 10
done
