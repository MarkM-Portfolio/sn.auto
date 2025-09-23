#!/bin/bash

# Refresh a connections deployment with a new build.
# There are some requirements and assumptions with this script:
#   All IBM software installed on /opt/IBM
#     /opt/IBM/WebSphere/AppServer
#     /opt/IBM/WebSphere/AppServer/profiles/AppSrv01
#     /opt/IBM/WebSphere/Plugins
#     /opt/IBM/HTTPServer
#     /opt/IBM/TDI/V7.1
#     /opt/ibm/db2/V10.1
#     /opt/IBM/Connections
#   WebSphere is properly configured with LDAP
#   Standalone deployment
#   DB2 instance db2inst1
#   The User 'lcuser'
#     owns and runs WebSphere AppServer
#     can sudo as db2inst1 without a password (/etc/sudoers)
#     can sudo without a tty (/etc/sudoers)
#     has access to build room file server
#        (/net/mlsa2/ibm/releng/workplace/dailybuilds)

usage() {
    echo "redeploy-local.sh [-D] build"
    echo ""
    echo "redeploy the build specified onto this server"
    echo "  -D     force drop and recreate database"
    echo "  -o     database is oracle"
    echo ""
    exit 1
}

recreate_all_databases() {
    local db_user=db2inst1
    local db_cmd=create_db.sh
    if [ "$flag_oracle" = "1" ] ; then
        db_user=oracle
        db_cmd=create_db_o.sh
    fi
    sudo -u "$db_user" -i ${LC_UPDATE_HOME}/bin/${db_cmd} -d
    echo "`date "+[%Y-%m-%d %H:%M:%S]"` Populate Profiles databases"
    bin/populate_profiles.sh etc/${HOSTNAME_S}
    if [ -d ~db2inst1/db-backups -a "${flag_oracle}" != "1" ] ; then
        echo "`date "+[%Y-%m-%d %H:%M:%S]"` Found ~db2inst1/db-backups directory, back up the clean databases"
        sudo -u "${db_user}" -i ${LC_UPDATE_HOME}/bin/${db_cmd} -b -C
    fi
    # need to clear the data store since we recreated the databases
    clear_data_store
}

clear_data_store() {
    echo "`date "+[%Y-%m-%d %H:%M:%S]"` Clear Connections data store at /data/Connections"
    rm -rf /data/Connections
}

while getopts Do opt
do
    case $opt in
        D) flag_recreate_db=1 ;;
        o) flag_oracle=1 ;;
        *) usage ;;
    esac
done
shift `expr $OPTIND - 1`

LC_UPDATE_HOME=${HOME}/lc-update
BUILD=$1
STREAM=${BUILD%_20*}

WAS_HOME=${WAS_HOME:-/opt/IBM/WebSphere/AppServer}
WAS_PROFILE=${WAS_PROFILE:-AppSrv01}

if [ -z "${BUILD}" -o -z "${STREAM}" ] ; then
    echo "ERROR - no build specified, exit"
    exit 1
fi

if [ "AIX" = `uname` ]; then
    FULL_HOSTNAME=`hostname`
else
    FULL_HOSTNAME=`hostname -f`
fi
HOSTNAME_S=`hostname -s`
cd ${LC_UPDATE_HOME}
if [ -f .kitversion ] ; then
    OLDBUILD=`cat .kitversion`
else
    OLDBUILD=""
fi

echo "`date "+[%Y-%m-%d %H:%M:%S]"` Start deploy [${BUILD}]"
if [ "${BUILD}" = "${STREAM}" ] ; then
    echo "`date "+[%Y-%m-%d %H:%M:%S]"` Start download latest build of stream [${STREAM}]"
    bin/get-build.sh -B ${STREAM}
else
    echo "`date "+[%Y-%m-%d %H:%M:%S]"` Start download build [${BUILD}]"
    bin/get-build.sh -b ${STREAM}/${BUILD}
fi
if [ "$?" != "0" ] ; then
    echo "`date "+[%Y-%m-%d %H:%M:%S]"` Download failed, exit."
    exit 1
fi
echo "`date "+[%Y-%m-%d %H:%M:%S]"` Download finished."

BUILD=`cat .kitversion`
OLDBUILD=${OLDBUILD#*/}
BUILD=${BUILD#*/}
if [ "$OLDBUILD" = "$BUILD" ]; then
    echo "Last build is ${OLDBUILD}, new build is "${BUILD}", same"
    echo "skip deployment and simply run tests"
    exit 0
fi
if [ ! -e xkit ] ; then
    echo "`date "+[%Y-%m-%d %H:%M:%S]"` [xkit] not found, this could be a IM kit, try extract it."
    bin/was xkit
fi

echo "`date "+[%Y-%m-%d %H:%M:%S]"` Stopping WAS server."
bin/was stop
bin/was kill
echo "`date "+[%Y-%m-%d %H:%M:%S]"` WAS stopped"

echo "`date "+[%Y-%m-%d %H:%M:%S]"` Kill java processes from \"${WAS_HOME}/profiles/${WAS_PROFILE}\""
pids=`ps -ef | grep java | grep "${WAS_HOME}/profiles/${WAS_PROFILE}" | awk '{print $2}'`
[ -z "$pids" ] || kill -9 ${pids}

echo "`date "+[%Y-%m-%d %H:%M:%S]"` Check for clean WAS backup at: ${WAS_HOME}.clean"
if [ -d ${WAS_HOME}.clean ] ; then
    echo "`date "+[%Y-%m-%d %H:%M:%S]"` Revert WAS from backup"
    rsync -a --delete ${WAS_HOME}.clean/ ${WAS_HOME}
fi

if [ -n "${flag_recreate_db}" ]; then
    # force to re-create db, no matter schema changed or not
    echo "`date "+[%Y-%m-%d %H:%M:%S]"` drop and recreate databases based on command line args"
    recreate_all_databases
else
    # automatically detect db schema changed or not
    echo "`date "+[%Y-%m-%d %H:%M:%S]"` Check schema versions of databases"
    sudo -u db2inst1 -i ${LC_UPDATE_HOME}/bin/create_db.sh -s -C
    if [ "$?" -ne 0 ] ; then
        db_schema_change=yes
        echo "`date "+[%Y-%m-%d %H:%M:%S]"` Schema changed, need to recreate database"
        recreate_all_databases
    else
        echo "`date "+[%Y-%m-%d %H:%M:%S]"` Schema version not changed, reuse old databases"
    fi
fi

echo "`date "+[%Y-%m-%d %H:%M:%S]"` Start deploying build ${BUILD}"
bin/lc-install.sh | tail -20
grep "    FAILED:   \[\]" install.log > /dev/null
echo "There are errors when deploy the build !!!"

echo "`date "+[%Y-%m-%d %H:%M:%S]"` Remove files from WAS temp directories"
rm -rf  ${WAS_HOME}/profiles/${WAS_PROFILE}/temp/*

echo "`date "+[%Y-%m-%d %H:%M:%S]"` Starting WAS server."
bin/was start
echo "`date "+[%Y-%m-%d %H:%M:%S]"` WAS server started."
echo "`date "+[%Y-%m-%d %H:%M:%S]"` Issue indexNow to server"
bin/was adm -f bin/indexNow.py
cp .kitversion "/tmp/LC_temp/complete.txt"
chmod a+r /tmp/LC_temp/complete.txt
echo "`date "+[%Y-%m-%d %H:%M:%S]"` Finished."


