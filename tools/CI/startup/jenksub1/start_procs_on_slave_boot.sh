#!/bin/sh
#
# This script will be called by /etc/init.d/rc.local
#

# Start all the DB2 instances.
cd /local/ci/common
./start_db2_instances_on_slave_boot.sh
