#!/bin/sh
#
# Redeploy an (new) LC3.0 build onto a system.
#
# The system is an Unix type system such as Linux or AIX.
#
# Assumptions:
#   local user can SSH to remote host as lcuser without password
#   on remote host lcuser can run sudo without being prompt a password
#   svn or git is available on the system for ease of script update.
#   rsync is avaliable on remote host, for copy files over
#
MYSELF=`readlink -f $0`
MY_BIN_DIR=`dirname ${MYSELF}`
MY_HOME=`dirname ${MY_BIN_DIR}`

COMPONENTS="activities blogs dogear communities forum
           homepage files wikis news search mobile"
KIT_NAME="IBM_Connections_Install"
SQL_HOME="Wizards/connections.sql"

echo2() {
    echo $* | tee -a "${LOG}"
}

pull_new_build() {
    echo2 -n "Pulling New Build from Build Room Server ... "
    cmd="${BIN_DIR}/get-build.sh"
    if [ -n "${BUILD}" ]; then
        ${cmd} -b "$BUILD"
    else
        ${cmd} -B "$STREAM"
    fi
    if [ $? -eq 0 ] ; then
        echo2 "OK"
    else
        echo2 "FAIL"
        return 1
    fi
    return 0
}

extract_lc_kit() {
    echo2 -n "Extract EARs, XML, Jython and other files from LC kit ... "
    if [ "xkit" -nt "$KIT_NAME" ] ; then
        echo2 "Directory \"xkit\" is newer than \"$KIT_NAME\", skip"
        return 0
    fi
    # TODO, find a better way to run xkit
    bin/was xkit
}

push_build_to_remote() {
    echo2 -n "Push new build to remote host: [${REMOTE_HOST}] ... "
    rsync -rtz --copy-dirlinks --delete xkit $REMOTE_USER@$REMOTE_HOST:${REMOTE_SCRIPT_HOME} >>$LOG 2>&1
    if [ $? -eq 0 ]; then
        echo2 "OK"
    else
        echo2 "FAIL"
        return 1
    fi
    return 0
}

update_scripts_on_remote() {
    echo2 -n "Update scrips on: [${REMOTE_HOST}] ... "
    ssh $REMOTE_USER@$REMOTE_HOST "cd ${REMOTE_SCRIPT_HOME} && ${REMOTE_SCRIPT_UPDATE_CMD}" >>${LOG} 2>&1
    if [ $? -eq 0 ]; then
        echo2 "OK"
    else
        echo2 "FAIL"
        return 1
    fi
    return 0
}

clear_log() {
    if [ -f "$LOG" ] ; then
        mv $LOG $LOG.old
    fi
    echo2 `date`
}

# put some walk around into connections sql to allow the metrics
# db to have some mock data for BVT purpose
hack_connections_sql() {
    local sql_dir=$1
    local db=db2
    cp -r etc/connections.sql/* $sql_dir/
}

upload_connections_sql() {
    local sql_dir="$SQL_HOME"
    local inst_name=$1
    if [ -d "xkit/connections.sql" ]; then
        sql_dir="xkit/connections.sql"
    fi
    if [ -z "$inst_name" ] ; then
        echo2 "ERROR, upload_connections_sql() needs a instance name"
        return 1
    fi
    hack_connections_sql $sql_dir
    echo2 -n "Upload ${sql_dir} to host [$DB_SERVER], for instance [$inst_name]:"
    rsync -az --delete "${sql_dir}" $inst_name@$DB_SERVER: >> ${LOG} 2>&1
    if [ $? -eq 0 ]; then
        echo2 "OK"
    else
        echo2 "FAIL"
        return 1
    fi
    return 0
}

push_cognos_config_jar_to_remote() {
    # copy Cognos/CognosConfig.tar
    if [ -z "${COGNOS_SERVER}" ] ; then
        echo2 "COGNOS_SERVER is not set, skip pushing CognosConfig.tar"
        return 0
    fi
    echo2 -n "Push new CognosConfig to remote host: [${COGNOS_SERVER}] ... "
    rsync ${KIT_NAME}/Cognos/CognosConfig.tar \
            ${COGNOS_SERVER}:CognosConfig.tar >>$LOG 2>&1
    scp ${ABS_CONFIG_FILE} ${COGNOS_SERVER}:bvt_server_cfg >>$LOG 2>&1
    if [ $? -eq 0 ]; then
        echo2 "OK"
    else
        echo2 "FAIL"
        return 1
    fi
}

update_databases_on_remote() {
    for ins in $DB_OWNER ; do
        if ! upload_connections_sql $ins ; then
            echo2 -n "Upload failed, can not recreate databases."
            return 1
        fi
        if [ -z "$flag_skip_db" ]; then
            recreate_databases_on_remote_instance $ins
        fi
    done
}

recreate_databases_on_remote_instance() {
    local inst_name=$1
    local errors=0
    local args="-d"
    [ -n "$flag_restart_db_server" ] && args="${args} -k"
    echo2 -n "Recreate databases on remote host [$DB_SERVER] instance [$ins] ... "
    ssh $inst_name@$DB_SERVER "\${HOME}/create_db.sh ${args}" >${DB_LOG}.${inst_name} 2>&1
    if [ $? -ne 0 ]; then
        echo2 "FAIL"
        errors=1
    else
        echo2 "OK"
    fi
    # FIXME: we don't know which database has been dropped and recreated,
    # hence don't know which component's data store need to be cleared.
    clear_remote_lc_data_dir || errors=1
    return ${errors}
}

clear_remote_was_profile_temp_dir() {
    echo2 "Remove remote WAS profile temp directory for ConnectionsCommon ..."
    echo2 -n "  Remove $REMOTE_WAS_HOME/profiles/$REMOTE_WAS_PROFILE/temp/ ... "
    run_over_ssh "rm -rf $REMOTE_WAS_HOME/profiles/$REMOTE_WAS_PROFILE/temp/" || return 1
    echo2 -n "  Verify $REMOTE_WAS_HOME/profiles/$REMOTE_WAS_PROFILE/temp/ ... "
    run_over_ssh "test ! -d $REMOTE_WAS_HOME/profiles/$REMOTE_WAS_PROFILE/temp" || return 1
    return 0
}

clear_remote_lc_data_dir() {
    # DANGER: if the remote was user is root, and in case we made a mistake
    # here in the rm command, could remove important files and eause serious
    # disaster!!!
    local errors=0
    if [ -z "$REMOTE_LC_DATA_DIR" ]; then
        echo2 "WARNING: remote LC data dir is not known, unable to clean."
        return 0
    fi
    # double check to make sure the directory looks like connections data
    echo2 -n "Check if ${REMOTE_LC_DATA_DIR} looks like a data directory ..."
    run_over_ssh "ls ${REMOTE_LC_DATA_DIR} | grep provision"
    if [ $? -ne 0 ]; then
        echo2 "ERROR: please check connections dat directory \"$REMOTE_LC_DATA_DIR\" is correct, it doesn't have provision in it?!"
        return 3
    fi
    echo2 -n "Remove LotusConnections data store: $REMOTE_LC_DATA_DIR/* ... "
    run_over_ssh sudo "rm -rf ${REMOTE_LC_DATA_DIR}" || return 1
    return 0
}

run_over_ssh() {
    local cmd="$*"
    if [ $REMOTE_WAS_OWN_BY_ROOT  = "yes" ]; then
        # in case the cmd itself is started with sudo ... do not add sudo again.
        if [ "${cmd#sudo}" = "${cmd}" ]; then
            cmd="sudo $cmd"
        fi
    fi
    #echo2 "RUN: ssh $REMOTE_USER@$REMOTE_HOST $cmd" >>${LOG} 2>&1
    ssh $REMOTE_USER@$REMOTE_HOST "$cmd" >> ${LOG} 2>&1
    if [ $? -eq 0 ]; then
        echo2 "OK"
    else
        echo2 "FAIL"
        return 1
    fi
    return 0
}

stop_remote_was() {
    echo2 -n "Stopping remote WAS server ... "
    local stop_srv=""
    if [ "$REMOTE_WAS_TYPE" = "nd" ] ; then
        stop_srv="${REMOTE_SCRIPT_HOME}/bin/was adm -f \
          ${REMOTE_SCRIPT_HOME}/bin/stop_cluster.py ${REMOTE_CLUSTERS}"
    else
        stop_srv="${REMOTE_SCRIPT_HOME}/bin/was stop"
    fi
    local was_cmd="${stop_srv} -user $REMOTE_WAS_USER -password $REMOTE_WAS_PSWD"
    run_over_ssh $was_cmd
    return $?
}

kill_remote_was() {
    echo2 -n "Kill remote WAS server ... "
    if [ "$REMOTE_WAS_TYPE" = "nd" ] ; then
        echo2 "Kill managed WAS server is not supported"
    fi
    run_over_ssh "${REMOTE_SCRIPT_HOME}/bin/was kill"
    return $?
}

start_remote_was() {
    echo2 -n "Starting remote WAS server ... "
    local start_srv=""
    if [ "$REMOTE_WAS_TYPE" = "nd" ] ; then
        start_srv="${REMOTE_SCRIPT_HOME}/bin/was adm -f \
          ${REMOTE_SCRIPT_HOME}/bin/start_cluster.py ${REMOTE_CLUSTERS}"
    else
        start_srv="$REMOTE_WAS_HOME/profiles/$REMOTE_WAS_PROFILE/bin/startServer.sh $REMOTE_WAS_SERVER"
    fi
    local was_cmd="${start_srv} -user $REMOTE_WAS_USER -password $REMOTE_WAS_PSWD"
    run_over_ssh $was_cmd
    return $?
}

run_remote_wsadmin() {
    echo2 -n "Run wsadmin on remote server ... "
    local wsadmin="$REMOTE_WAS_HOME/profiles/$REMOTE_WAS_PROFILE/bin/wsadmin.sh -host localhost"
    local was_cmd="${wsadmin} -conntype SOAP -user $REMOTE_WAS_USER -password $REMOTE_WAS_PSWD"
    [ -n "${REMOTE_WAS_SOAP_PORT}" ] && was_cmd="${was_cmd} -port ${REMOTE_WAS_SOAP_PORT}"
    run_over_ssh $was_cmd $*
    return $?
}

register_media_types() {
    run_remote_wsadmin -f ${REMOTE_SCRIPT_HOME}/bin/add_media_types.py
    return $?
}

build_search_index () {
    run_remote_wsadmin -f ${REMOTE_SCRIPT_HOME}/bin/indexNow.py
}

restore_was_backup() {
    echo2 -n "Restore WAS from backup ... "
    local rm_cmd="sudo rsync -a --delete ${REMOTE_WAS_HOME}.clean/ ${REMOTE_WAS_HOME}"
    #if [ "$REMOTE_WAS_OWN_BY_ROOT" = "yes" ] ; then rm_cmd="sudo ${rm_cmd}" ; fi
    echo "${rm_cmd}"
    ssh -t $REMOTE_USER@$REMOTE_HOST "${rm_cmd}" >> ${LOG} 2>&1
    if [ $? -eq 0 ]; then
        echo2 "OK"
        return 0
    else
        echo2 "FAIL"
        return 1
    fi
}

redeploy_on_was() {
    echo2 -n "Clear remote WAS log files ... "
    local log_path="${REMOTE_WAS_HOME}/profiles/${REMOTE_WAS_PROFILE}/logs/${REMOTE_WAS_SERVER}"
    local rm_cmd="rm"
    if [ "$REMOTE_WAS_OWN_BY_ROOT" = "yes" ] ; then rm_cmd="sudo ${rm_cmd}" ; fi
    ssh $REMOTE_USER@$REMOTE_HOST "${rm_cmd} ${log_path}/*.log" >> ${LOG} 2>&1
    if [ $? -eq 0 ]; then
        echo2 "OK"
    else
        echo2 "FAIL"
    fi

    echo2 -n "Redeploy new build on remote WAS server ... "
    rm_cmd="${REMOTE_SCRIPT_HOME}/bin/lc-install.sh"
    if [ "$REMOTE_WAS_OWN_BY_ROOT" = "yes" ] ; then rm_cmd="sudo ${rm_cmd}" ; fi
    if [ "$REMOTE_WAS_TYPE" = "nd" ] ; then
      rm_cmd="${rm_cmd} -host localhost -user $REMOTE_WAS_USER -password ${REMOTE_WAS_PSWD}"
    fi
    ssh $REMOTE_USER@$REMOTE_HOST "cd ${REMOTE_SCRIPT_HOME} && ${rm_cmd}" >> ${LOG} 2>&1
    grep "    FAILED:   \[\]" ${LOG} > /dev/null
    if [ $? -eq 0 ]; then
        echo2 "OK"
    else
        echo2 "FAIL"
        return 1
    fi
    return 0
}

fetch_remote_was_system_out_log() {
    echo2 -n "Fetch the SystemOut.log from remote WAS ... "
    local log_path="${REMOTE_WAS_HOME}/profiles/${REMOTE_WAS_PROFILE}/logs/${REMOTE_WAS_SERVER}/SystemOut.log"
    local sysout_log="logs/${CONFIG_FILE}_SystemOut.log"
    rsync -z $REMOTE_USER@$REMOTE_HOST:${log_path} ${sysout_log} >>$LOG 2>&1
    if [ $? -eq 0 ]; then
        echo2 "OK"
    else
        echo2 "FAIL"
        return 1
    fi
    echo2 "================= Errors from SystemOut.log Starts  ==================="
    ruby $BIN_DIR/filter_errors.rb ${sysout_log} | tee -a ${LOG}
    echo2 "=================  Errors from SystemOut.log Ends   ==================="
    return 0
}


run_data_population_tool() {
    local dp_download_url="icci@connectionsci1.cnx.cwp.pnp-hcl.com:/local/ci/automation/src/sn.auto/lwp/build/bvt.dist/dist/"
    if [ ! -x "$JAVA" ]; then
        echo2 "ERROR - enable to run Java using \$JAVA="$JAVA", please check configuration"
        return 2
    fi
    if [ -z "$DATA_POPULATION_URL" -o -z "${DATA_POPULATION_HOME}" ]; then
        echo2 "WARNING: data population URL is not provided, check configuration file"
        echo2 "  DATA_POPULATION_HOME=${DATA_POPULATION_HOME}"
        echo  "  DATA_POPULATION_URL=${DATA_POPULATION_URL}"
        return 2
    fi
    if [ ! -d "${DATA_POPULATION_HOME}" ]; then
        echo2 "WARNING: DATA_POPULATION_HOME(${DATA_POPULATION_HOME}) not exist, check configuration file"
        return 2
    fi
    echo2 "Running Connections_DP_Framework for data population"
    echo2 "  download data pop tool into ${DATA_POPULATION_HOME}"
    rsync -az "${dp_download_url}" "${DATA_POPULATION_HOME}"
    cd ${DATA_POPULATION_HOME}
    "${JAVA}" -jar bvt.api.jar -server ${DATA_POPULATION_URL} >$DP_LOG 2>&1
    cd -
    if [ $? -ne 0 ]; then
        echo2 "WARNING: data population failed!!"
        return 1
    fi
    return 0
}


restart_remote_ihs() {
    if [ -z "${REMOTE_IHS_HOME}" ]; then
        echo2 "No IHS directory specified, skip restarting IHS"
        return 0
    fi
    echo2 -n "Restart the IHS server ... "
    run_over_ssh sudo ${REMOTE_IHS_HOME}/bin/apachectl restart
}


install_sharepoint_widget() {
    echo2 " --- not yet implemented ---"
}


exit_with_message() {
    local exit_code=$1
    local msg=$2
    echo2 $msg
    exit $exit_code
}

usage() {
    local prog_name=`basename ${MYSELF}`
    cat <<EOF

Usage:
  $prog_name [options] config_file

Options:
  -D        skip recreate the databases, useful when database schema not changed.
            or plan to use fixup scripts to update db schema.
  -I        skip redeploy new Connections build on the system.
  -r        restore clean WAS backup if there is one saved.
  -X        use existing xkit directory as the build, do not download anything
  -b <bld>  use specified bld instead of the latest build.
  -B <strm> use the latest build from the specified stream.
  -2        stop and start WAS server twice to work around the 32bit DB2 out of
            memory problem.
  -p        populate profiles db. (make sure JDBC and LDAP info are provided)
  -P        do data population through connections API.
  -C        instrument jar files for code coverage.
  -c        collect code coverage data before redeployment.
  -K        kill DB2 and restart it.

Fully redeploy a build on a remote system specified in the config file, the remote
system needs a *LOT* setup work in order for the redeplyment to be successful.

* Remote OS, Linux with SSH enabled.
* User SSH key authentication, so can login to remote system without password.
  - \$HOME/.ssh/authorized_keys
* Remove user can use sudo to do work as root
  - the user is in sudoers list (/etc/sudoers)
  - sudo allows to run command without a TTY (/etc/sudoers)

EOF
    exit 1
}

MYSELF=`readlink -f $0`
BIN_DIR=`dirname ${MYSELF}`
BASEDIR=`dirname ${BIN_DIR}`

while getopts DIrb:B:2XpPCcK opt
do
    case $opt in
        b) BUILD="${OPTARG}";;
        B) STREAM="${OPTARG}";;
        X) flag_use_existing_xkit=1;;
        D) flag_skip_db=1 ;;
        I) flag_skip_install=1 ;;
        r) flag_restore_was_backup=1 ;;
        2) flag_start_was_twice=1 ;;
        p) flag_populate_profiles=1 ;;
        P) flag_populate_data=1 ;;
        C) flag_emma_instrument=1 ;;
        c) flag_emma_dump=1;;
        K) flag_restart_db_server=1;;
        *) usage ;;
    esac
done
shift `expr $OPTIND - 1`

# load additional enviroment setup if there is any
if [ -f "${HOME}/.redeploy" ]; then
    . "${HOME}/.redeploy"
fi

if [ -z "$1" ]; then
    echo "ERROR:"
    echo "  You need to specify a configuration file"
    usage
fi
CONFIG_FILE=$1

if [ -f "${CONFIG_FILE}" ]; then
    ABS_CONFIG_FILE=`readlink -f ${CONFIG_FILE}`
elif [ -f "${MY_HOME}/etc/${CONFIG_FILE}" ]; then
    ABS_CONFIG_FILE=`readlink -f ${MY_HOME}/etc/${CONFIG_FILE}`
else
    echo "ERROR - unable to find configuration file ${CONFIG_FILE} in ${PWD}"
    usage
fi

. "${ABS_CONFIG_FILE}"
if [ $? -ne 0 ] ; then
    exit_with_message 1 "ERROR - failed to load configuration from ${CONFIG_FILE}"
fi

# REMOTE_HOST="linux276.rtp.raleigh.ibm.com"
# REMOTE_WAS_OWN_BY_ROOT="yes"
# REMOTE_USER='lcuser'
# REMOTE_SCRIPT_HOME='/home/lcuser/lc-update'
# #REMOTE_SCRIPT_UPDATE_CMD="git pull"
# REMOTE_SCRIPT_UPDATE_CMD="svn update"
# DB_SERVER="${DB2_SERVER:-$REMOTE_HOST}"
# DB_OWNER="db2inst1"
# REMOTE_WAS_HOME="/opt/IBM/WebSphere/AppServer"
# REMOTE_WAS_TYPE="standalone"
# REMOTE_WAS_PROFILE="AppSrv01"
# REMOTE_WAS_SERVER="server1"
# REMOTE_WAS_USER="wasadmin"
# REMOTE_WAS_PSWD="secret"
# REMOTE_LC_DATA_DIR="/opt/IBM/LotusConnections/Data"
STREAM=${STREAM:-LCI3.0}

[ -d logs ] || mkdir logs
LOG=logs/${CONFIG_FILE}.log
DB_LOG=logs/${CONFIG_FILE}.db.log
DB_ERR=logs/${CONFIG_FILE}.db.err
DP_LOG=logs/${CONFIG_FILE}.dp.log

if ruby $BIN_DIR/update-server-doc.rb --reserved ${TEAMRM_SERVER_DOC} ; then
    exit_with_message 0 "SKIP: \"${TEAMRM_SERVER_DOC}\" is reserved in team room DB, skip."
fi

if [ -n "$flag_emma_dump" ] ; then
    echo "Collecting code coverage data..."
    ssh ${REMOTE_USER}@${REMOTE_HOST} "${EMMA_SCRIPT_DIR}/dump_coverage_data.sh ${EMMA_SCRIPT_DIR}/${CONFIG_FILE}"

    if [ $? != 0 ]; then
        echo2 "ERROR: Failed to collect code coverage data!!"
    fi
fi

clear_log
if [ -z "$flag_use_existing_xkit" ]; then
    if ! pull_new_build ; then
        exit_with_message 1 "STOP: unable to download build from server, see logs for details."
    fi
    extract_lc_kit
fi
if ! push_build_to_remote; then
    exit_with_message 1 "STOP: unable to push build to remote system, check the log for details."
fi

push_cognos_config_jar_to_remote

# remove remote semaphore file
ssh $REMOTE_USER@$REMOTE_HOST "rm -f /tmp/LC_temp/complete.txt"
update_scripts_on_remote

if [ -n "${TEAMRM_SERVER_DOC}" ]; then
    ruby $BIN_DIR/update-server-doc.rb -b "...INSTALLING..." -s 3 ${TEAMRM_SERVER_DOC}
fi

stop_remote_was || kill_remote_was

update_databases_on_remote

if [ -n "$flag_restore_was_backup" ]; then
    if ! restore_was_backup ; then
        echo "There is error restoring the WAS backup, a typical error could be"
        echo "someone run WAS as root user on a non-root installation, that will"
        echo "generates some files owned by root and can not be deleted by normal"
        echo "users, the log will have more details."
        exit 1
    fi
fi
if [ -z "$flag_skip_install" ]; then
    if ! redeploy_on_was; then
        echo2 "Failed deploy new build on WAS"
        exit 2
    fi
fi

if [ -n "$flag_emma_instrument" ] ; then
    echo "Instrumenting jars for code coverage..."
    if ! ${BIN_DIR}/instrument_for_code_coverage.sh ${ABS_CONFIG_FILE} ${LOG} ; then
        echo2 "ERROR: Failed to instrument jars for code coverage!!"
    fi
fi

if [ -n "$NEED_SHAREPOINT_WIDGET" ]; then
    install_sharepoint_widget
fi

clear_remote_was_profile_temp_dir

start_remote_was || exit_with_message 3 "failed to start WAS server"

if [ -n "$flag_start_was_twice" ] ; then
    echo2 "stop and start the WAS server again to workaround DB2 out of memory issue"
    stop_remote_was
    start_remote_was || exit_with_message 3 "failed to start WAS server"
fi

register_media_types

build_search_index

restart_remote_ihs

# put the semaphore file on remote system for BVT to start
#scp .kitversion $REMOTE_USER@$REMOTE_HOST:/tmp/LC_temp/complete.txt
BUILD=`cat .kitversion`
BUILD_STR=`basename $BUILD`
echo2 "Write $BUILD_STR to semaphore file"
ssh $REMOTE_USER@$REMOTE_HOST "[ -d /tmp/LC_temp ] && echo $BUILD_STR > /tmp/LC_temp/complete.txt"

if [ -n "$flag_populate_profiles" ]; then
    echo2 -n "Populate Profiles database ..."
    #$BIN_DIR/populate_profiles.sh ${ABS_CONFIG_FILE} | tee -a ${LOG}
    ssh ${REMOTE_USER}@${REMOTE_HOST} "${REMOTE_SCRIPT_HOME}/bin/populate_profiles.sh ${REMOTE_SCRIPT_HOME}/etc/${CONFIG_FILE}" | tee -a ${LOG}
fi

if [ -n "$flag_populate_data" ] ; then
    run_data_population_tool
fi

fetch_remote_was_system_out_log

# if needed, we update the Notes doc in team room db as well, note that
# this requires Ruby and also the Notes or Domino binaries and proper
# Notes user id, notes.ini, names.nsf etc. are properly configured.
if [ -n "${TEAMRM_SERVER_DOC}" ]; then
    ruby ${BIN_DIR}/update-server-doc.rb -b ${BUILD_STR} -s 0 ${TEAMRM_SERVER_DOC}
fi

if [ -n "${API_AUTOMATION_HOME}" -a -n "${API_AUTOMATION_ARGS}" ]; then
   echo2 "Submit API automation requests"
   (cd ${API_AUTOMATION_HOME} && nohup ./run_it.sh ${API_AUTOMATION_ARGS}) &
else
   echo2 "API Automation is skipped"
fi

if [ -n "${DASHBOARD_TOOL_HOME}" ] ; then
    if [ `date +%u` -gt 5 ] ; then
        echo2 "Weekend!!, Let's not create RTC work item for BVT."
    else
        echo2 "Create BVT work item for ${BUILD_STR##*/}"
        cd ${DASHBOARD_TOOL_HOME:-.} && ./createBuildDashboard.sh ${BUILD_STR##*/} ${REMOTE_HOST}
    fi
fi

if [ -n "${GUI_AUTOMATION_HOME}" ]; then
   (cd ${GUI_AUTOMATION_HOME} %% ./run_gui_bvt.sh ${GUI_AUTOMATION_HOST} http://lc30linux3.swg.usma.ibm.com) &
fi

