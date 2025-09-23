#!/bin/sh
#
# This script will be called by /etc/init.d/rc.local
#

# Start the IBM HTTP server
cd /local/ci/common
./start_ibm_http_server.sh

# Start all the DB2 instances.
cd /local/ci/common
./start_db2_instances.sh

# Start all the RTC CI build engines.
#cd /local/ci/common
#./start_build_engines.sh
