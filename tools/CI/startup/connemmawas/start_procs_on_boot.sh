#!/bin/sh
#
# This script will be called by /etc/init.d/rc.local
#

# Start the IBM HTTP server
cd /local/home/lcuser/ci-startup
./start_ibm_http_server.sh

# Start all the DB2 instances.
cd /local/home/lcuser/ci-startup
./start_db2_instances.sh

# Start WAS.
cd /local/home/lcuser/ci-startup
./start_was.sh
