#!/bin/sh
#
# This script will be called by /etc/init.d/rc.local
#

# Start all the DB2 instances.
cd /home/lcuser/ci-startup
./start_db2_instances.sh

# Start WAS.
cd /home/lcuser/ci-startup
./start_was.sh
