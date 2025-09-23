#!/bin/sh
#
# Start all the DB2 instances.
# Will be called by /etc/rc.d/rc.local.
#
DB2_HOME=/opt/ibm/db2/V10.5
DB2_DIAGLOG=sqllib/db2dump/db2diag.log

for DB2INST in `${DB2_HOME}/instance/db2ilist`
do
	LOGFILE="start_${DB2INST}.log"
	su - ${DB2INST} -c "[ -f ${LOGFILE} ] && mv ${LOGFILE} ${LOGFILE}.old"
	su - ${DB2INST} -c "[ -f ${DB2_DIAGLOG} ] && cat /dev/null > ${DB2_DIAGLOG}"
	su - ${DB2INST} -c "db2start 1>${LOGFILE} 2>&1"
	sleep 10
done


