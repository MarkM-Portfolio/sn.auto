#!/bin/sh
#
# Start all the RTC CI build engines.
# Will be called by /etc/rc.d/rc.local.

LOGFILE="start_build_engine.log"
for DIR in automation
do
	cd /local/ci/${DIR}
	if [ $? != 0 ]; then
		continue
	fi
	
	if [ -f ${LOGFILE} ]; then
		mv ${LOGFILE} ${LOGFILE}.old
	fi
	
	sudo -u icci nohup ./start_build_engine.sh 1>${LOGFILE} 2>&1 &
	sleep 10
done
