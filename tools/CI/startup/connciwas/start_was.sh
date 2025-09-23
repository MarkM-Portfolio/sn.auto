#!/bin/sh
#
# Start WAS.
# Will be called by /etc/rc.d/rc.local.

LOGFILE="start_was.log"
	
sudo su - lcuser -c "/opt/IBM/WebSphere/AppServer/bin/startServer.sh server1" 1>${LOGFILE} 2>&1
