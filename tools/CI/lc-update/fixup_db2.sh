#!/bin/bash

. $(dirname $0)/functions.sh
MYSELF=`abs_path $0`
MY_BIN_DIR=`dirname ${MYSELF}`
MY_HOME=`dirname ${MY_BIN_DIR}`


# run java migration tool by calling the script name provided
#  run_java_migration_tool script_name db_name
#
# returns: 0 on success and non-zero if failed.
#
run_java_migration_tool() {
    local to_run=$1
    local db_name=$2
    if [ ! -d ${DB2_HOME}/java ]; then
       echo "ERROR - can not find ${DB2_HOME}/java, db2 environment is not correct ?"
       return 1
    fi
    # figure out the db2 port
    local service_name=`db2 get dbm cfg | grep -i '(svcename)' | sed "s/.*SVCENAME) = //"`
    service_name=`echo $service_name | tr -d ' '`
    if [ -z "$service_name" ] ; then
        echo "**********************************************************************"
        echo
        echo "ERROR:"
        echo
        echo "Unable figure out DB2 service name (SVCENAME), the DB2 instance may not"
        echo "configured to suppoert TCPIP, try the following command as the instance"
        echo "to get more information."
        echo
        echo " $ db2 get dbm cfg | grep SVCENAME"
        echo
        echo "***********************************************************************"
        return 1
    fi
    local port_number=`grep "$service_name" /etc/services | awk '{print $2}'`
    port_number=${port_number%/tcp}
    if [ -z "$port_number" ] ; then
        echo "**********************************************************************"
        echo
        echo "ERROR:"
        echo
        echo "Unable find out TCP port number for service \"${service_name}\"."
        echo "Check your /etc/services file and make sure the DB2 instance is configured"
        echo "to use a service name that has a port number defined in /etc/services."
        echo
        echo "***********************************************************************"
        return 1
    fi

    echo "running ${to_run} from $PWD ..."
    DB2_JAVA_HOME=${DB2_HOME}/java \
    DB2_HOST=localhost \
    DB2_PORT=${port_number} \
    DB2_USER=${schema_user} \
    DB2_PASSWORD=${schema_pswd} \
    DB2_DB_NAME=${db_name} \
    sh $to_run
}

list_databases() {
    db2 list db directory | grep "Database name" | sed -e "s/.*= //"
}

fixup_all_dbs() {
    local db=""
    local errors=0
    if [ -z "${g_db_names}" ]; then
        echo "List databases available on this instance"
        for db in `list_databases` ; do
            g_db_names="${g_db_names}${g_db_names:+ }${db}"
        done
    fi
    echo "Fix up these databases: ${g_db_names}"
    for db in ${g_db_names}; do
        fixup_database $db || errors=1
    done
    return $errors
}

fixup_database() {
    local db=$1
    local c=""
    local db_ver=""
    local errors=0
    echo
    echo "***********************************************************************"
    echo "Database: [$db]"
    guess_db_appname $db
    echo "Schemas included: [${SCHEMA_NAMES}]"
    echo "For applications: [${APP_NAMES}]"
    for c in ${APP_NAMES} ; do
        get_db_schema_version "${db}" "$c"
        get_script_schema_version "$c"
        echo "$c:"
        echo "  Current schema: [${DB_SCHEMA_VERSION}]"
        echo "  Latest schema:  [${SCRIPT_SCHEMA_VERSION}]"
        if [ "${SCRIPT_SCHEMA_VERSION}" = "0" ] ; then
            echo "  Unable to figure out target schema for [${c}] in [${db}], skip."
            continue
        fi
        if [ "${DB_SCHEMA_VERSION}" = "${SCRIPT_SCHEMA_VERSION}" ]; then
            echo "  [${c}] schema in [${db}] is already up to date."
            continue
        fi
        run_fixups $db $c ${DB_SCHEMA_VERSION} ${SCRIPT_SCHEMA_VERSION} || errors=1
        get_db_schema_version "${db}" "$c"
        echo "  Schema version after fixup: [${DB_SCHEMA_VERSION}]"
        if [ "${DB_SCHEMA_VERSION}" != "${SCRIPT_SCHEMA_VERSION}" ]; then
            echo "  FAILED upgrade ${c} schema: expect [${SCRIPT_SCHEMA_VERSION}] but have [${DB_SCHEMA_VERSION}]"
            errors=1
        else
            echo "  SUCCESSFULLY upgraded ${c} to ${DB_SCHEMA_VERSION}."
        fi
        for f in appGrants.sql clearScheduler.sql ; do
            local f_path="${conn_sql_dir}/${c}/db2/${f}"
            [ "${c}" = "calendar" ] && f_path="${conn_sql_dir}/communities/db2/${c}-${f}"
            if [ -f "$f_path" ]; then
                run_sql_script "${f_path}" ${db} || errors=1
            else
                echo "[$f_path] not found, skipped."
            fi
        done
    done
    return ${errors}
}

# Usage:
#   run_fixups PEOPLEDB1 profiles 48 51
#
run_fixups() {
    local db=$1
    local comp=$2
    local curr_version=$3
    local tgt_version=$4
    local suffixes=".sql j.sh"
    local errors=0
    [ "$flag_smartcloud" = 1 ] && suffixes="s.sql .sql j.sh"
    echo "  Bring up schema of [$comp] in [${db}], from ${curr_version} to ${tgt_version} ......"
    curr_version=`expr $curr_version + 1`
    for i in `seq ${curr_version} ${tgt_version}` ; do
        for suffix in $suffixes ; do
            local f_path="${conn_sql_dir}/${comp}/db2/fixup/fixup${i}${suffix}"
            [ "${c}" = "calendar" ] && f_path="${conn_sql_dir}/communities/db2/fixup/${c}-fixup${i}${suffix}"
            if [ -f ${f_path} ] ; then
                if [ "$suffix" = "j.sh" ]; then
                    run_java_migration_tool ${f_path} ${db} || errors=1
                elif [ "${suffix##*.}" = "sql" ]; then
                    run_sql_script ${f_path} ${db} || errors=1
                else
                    echo "unsupported fixup file type, only .sql and .sh are supported. (${f_path})"
                    errors=1
                fi
                break
            fi
        done
        [ "${errors}" -ne 0 ] && break
    done
    return ${errors}
}

# run_sql_script sql_file_path db_name
#
run_sql_script() {
    local sql_file=$1
    local db_name=$2
    local to_run=$sql_file
    local db2opts="-td@ -svf"
    local has_errors=0
    local eol_char=';'
    local sql_has_connect_to=false
    local is_default_db_name=true
    [ -z "$sql_file" ] && return 8
    if [ ! -f "${sql_file}" ]; then
       echo "ERROR: file not exist: $sql_file"
       return 8
    fi
    cat "$sql_file" | tr -d '\r' | grep -q -e "@$" || db2opts="-tsvf"
    if grep -i "connect to" "$sql_file" > /dev/null ; then
        sql_has_connect_to=true
        if ! grep -i "connect to $db_name" "$sql_file" > /dev/null ; then
            is_default_db_name=false
        fi
        if [ -n "${schema_user}" -o "${is_default_db_name}" = false ] ; then
            # need to use our own db2 connect for either customized db name or user id
            to_run=`mktemp "$LOG_PATH/${sql_file##*/}.XXXX"`
            perl -0777 -pe "s/connect to \\w+[ \\n\\r]*@//ig" ${sql_file} > $to_run | return 1
        fi
    fi
    if [ "${to_run}" != "sql_file" ]; then
        db2_connect_db "${db_name}" || return 1
    fi
    echo -n "Running: db2 ${db2opts} ${to_run} ... "
    db2 ${db2opts} ${to_run} > "${LOG_PATH}/${sql_file##*/}.log" 2>&1
    db2_ret=$?
    echo "-> EXIT: $db2_ret (${sql_file})"
    if [ $db2_ret -ge 4 ] ; then 
        has_errors=1
        echo "-------------------------------------------------------------------------"
        cat "${LOG_PATH}/${sql_file##*/}.log"
        echo "-------------------------------------------------------------------------"
    else
        [ "${to_run}" = "${sql_file}" ] ||  rm "${to_run}"
    fi
    db2 connect reset > /dev/null
    return $has_errors
}


# figure out which application this DB belongs to:
# example:
#   guess_db_appname PEOPLEDB
# returns:
#   set env varialbe SCHEMA_NAMES to the schema names found in db, (e.g "EMPINST")
#   set env varialbe APP_NAMES to the application name of the db, (e.g "profiles")
#
guess_db_appname() {
    SCHEMA_NAMES=""
    APP_NAMES=""
    local db=$1

    db2_connect_db "${db}" || return
    local db_apps="EMPINST:profiles SNCOMM:communities FORUM:forum BLOGS:blogs \
        DOGEAR:dogear  HOMEPAGE:homepage FILES:files WIKIS:wikis PNS:pushnotification\
        ACTIVITIES:activities METRICS:metrics MOBILE:mobile CALENDAR:calendar"
    for i in $db_apps; do
        local schema_name=${i%:*}
        local app_name=${i#*:}
        #echo "check if this db contains ${schema_name}"
        db2 'list tables for all' | awk '{print $2}' | grep ${schema_name} > /dev/null
        if [ $? -eq 0 ]; then
            SCHEMA_NAMES="${SCHEMA_NAMES}${SCHEMA_NAMES:+ }${schema_name}"
            APP_NAMES="${APP_NAMES}${APP_NAMES:+ }${app_name}"
        fi
    done
    db2 +o disconnect ${db} > /dev/null
    return
}

db2_connect_db() {
    local auth="${schema_user:+user }${schema_user} ${schema_pswd:+using }${schema_pswd}"
    local db=$1
    db2 +o connect to "$db" $auth  > /dev/null
}

# usage:
#    get_db_schema_version db_name schema_name
# return:
#    set DB_SCHEMA_VERSION to the vesrion
get_db_schema_version() {
    local db_name=$1
    local comp=$2
    local schema_version=""
    DB_SCHEMA_VERSION=""
    case ${comp} in
    "blogs") SQL="select value from BLOGS.roller_properties where name = 'database.schema.version'";;
    "mobile") SQL="select value from MOBILE.roller_properties where name = 'database.schema.version'";;
    "activities") SQL="select DBSCHEMAVER from ACTIVITIES.OA_SCHEMA";;
    "communities") SQL="select DBSCHEMAVER from SNCOMM.SNCOMM_SCHEMA";;
    "forum") SQL="select  DBSCHEMAVER from FORUM.DF_SCHEMA";;
    "dogear") SQL="select DBSCHEMAVER from DOGEAR.DOGEAR_SCHEMA";;
    "profiles") SQL="select DBSCHEMAVER from EMPINST.SNPROF_SCHEMA";;
    "homepage") SQL="select  DBSCHEMAVER from HOMEPAGE.HOMEPAGE_SCHEMA";;
    "files") SQL="select SCHEMA_VERSION from FILES.PRODUCT";;
    "pushnotification") SQL="select SCHEMA_VERSION from PNS.PRODUCT";;
    "wikis") SQL="select SCHEMA_VERSION from WIKIS.PRODUCT";;
    "metrics") SQL="select SCHEMA_VERSION from METRICS.PRODUCT";;
    "calendar" ) SQL="select DBSCHEMAVER from CALENDAR.CA_SCHEMA" ;;
    *) return 1;
    esac
    #check if a schema user and pass were provided. this changes the connect call
    db2_connect_db $db_name || return 1
    schema_version=`db2 -x +p "${SQL}"`
    db2 +o disconnect ${DBNAME}
    DB_SCHEMA_VERSION=$(echo "${schema_version}" | sed 's/ //g')
    any_errors=0
    case $DB_SCHEMA_VERSION in
        ''|*[!0-9]*)    DB_SCHEMA_VERSION="0"; any_errors=1;;
    esac
    return $any_errors
}



# returns the expected schema version from the createDB.sql files inside connections.sql.
#
get_script_schema_version() {
    local comp=$1
    local db_script_base="${conn_sql_dir}/${comp}/db2/"
    [ "${comp}" = "calendar" ] && db_script_base="${conn_sql_dir}/communities/db2/${comp}-"
    local create_db_script="${db_script_base}createDb.sql"
    local for_grep=""
    local for_sed=""
    local schema_version=""

    case ${comp} in
    "activities") for_grep="INSERT INTO ACTIVITIES.OA_SCHEMA \\( COMPKEY, DBSCHEMAVER, RELEASEVER, PRESCHEMAVER, POSTSCHEMAVER \\) VALUES \\( 'OA', "
         for_sed="s/INSERT INTO ACTIVITIES.OA_SCHEMA \\( COMPKEY, DBSCHEMAVER, RELEASEVER, PRESCHEMAVER, POSTSCHEMAVER \\) VALUES \\( 'OA', ([0-9]+), .*/\1/" ;;
    "blogs") for_grep="insert into BLOGS.roller_properties \\(name, value\\) values \\('database.schema.version', "
         for_sed="s/.*'database\.schema\.version', '([0-9]+)'.*/\1/" ;;
    "communities") for_grep="\\('Communities', '[0-9.]{9}', [0-9]+\\)@"
         for_sed="s/.*\\('Communities', '[0-9.]{9}', ([0-9]+)\\)@/\1/";;
    "calendar") for_grep="INSERT INTO CALENDAR.CA_SCHEMA \\(COMPKEY, DBSCHEMAVER, RELEASEVER\\) VALUES \\('CALENDAR',"
         for_sed="s/INSERT INTO CALENDAR.CA_SCHEMA \\(COMPKEY, DBSCHEMAVER, RELEASEVER\\) VALUES \\('CALENDAR', '([0-9]+)'.*/\1/";;
    "dogear") for_grep="\\( 'DOGEAR', [0-9]+, '[0-9].[0-9].[0-9].[0-9]'\\)@"
         for_sed="s/.*\\( 'DOGEAR', ([0-9]+), '[0-9.]{7}'\\)@/\1/";;
    "forum") for_grep=" *\\( 'DF', [0-9]+, '[0-9.]{9}'([0-9, ]*)\\)"
         for_sed="s/.*\\( 'DF', ([0-9]+), '[0-9.]{9}', ([0-9]+), ([0-9]+)\\)@/\1/";;
    "profiles") for_grep="INSERT INTO EMPINST.SNPROF_SCHEMA \\(COMPKEY, DBSCHEMAVER, RELEASEVER\\) VALUES \\('Profiles'"
         for_sed="s/INSERT INTO EMPINST.SNPROF_SCHEMA \\(COMPKEY, DBSCHEMAVER, RELEASEVER\\) VALUES \\('Profiles', ([0-9]+).*/\1/";;
    "homepage") for_grep="VALUES \\('HOMEPAGE', [0-9]+, '.+'\\)"
         for_sed="s/.*VALUES \\('HOMEPAGE', ([0-9]+), '.+'\\)@/\1/";;
    "files") for_grep=".*VALUES \\('connections.files', [0-9], [0-9], [0-9], [0-9], '[0-9]{8}-[0-9]{4}', '[0-9]+', 'on-premise'\\)"
         for_sed="s/.*VALUES \\('connections.files', [0-9], [0-9], [0-9], [0-9], '[0-9]{8}-[0-9]{4}', '([0-9]+)', 'on-premise'\\).*/\1/";;
    "pushnotification") for_grep=".*VALUES \\('pushnotification', [0-9], [0-9], [0-9], [0-9], '[0-9]{8}-[0-9]{4}', '[0-9]+'\\)"
         for_sed="s/.*VALUES \\('pushnotification', [0-9], [0-9], [0-9], [0-9], '[0-9]{8}-[0-9]{4}', '([0-9]+)'\\).*/\1/";;
    "wikis") for_grep=".*VALUES \\('connections.wikis', [0-9], [0-9], [0-9], [0-9], '[0-9]{8}-[0-9]{4}', '[0-9]+', 'on-premise'\\)"
         for_sed="s/.*VALUES \\('connections.wikis', [0-9], [0-9], [0-9], [0-9], '[0-9]{8}-[0-9]{4}', '([0-9]+)', 'on-premise'\\).*/\1/";;
    "metrics") for_grep="VALUES \\('connections.metrics', [0-9], [0-9], [0-9], [0-9], '', '[0-9]+'\\)"
         for_sed="s/.*VALUES \\('connections.metrics', [0-9], [0-9], [0-9], [0-9], '', '([0-9]+)'\\).*/\1/";;
    "mobile") for_grep="INSERT INTO MOBILE.ROLLER_PROPERTIES \\(NAME, VALUE\\) VALUES \\('database.schema.version', "
         for_sed="s/.*'database\.schema\.version', '([0-9]+)'.*/\1/" ;;
    "library.os") for_grep="NO_SUCH_LINE_AT_ALL"
         for_sed="s/.*'database\.schema\.version', '([0-9]+)'.*/\1/" ;;
    "library.gcd") for_grep="NO_SUCH_LINE_AT_ALL"
         for_sed="s/.*'database\.schema\.version', '([0-9]+)'.*/\1/" ;;
    esac

    schema_version=`grep -E "${for_grep}" "${create_db_script}" | sed -r "${for_sed}"`
    SCRIPT_SCHEMA_VERSION=$(echo "${schema_version}" | sed 's/ //g')
    any_errors=0
    case $SCRIPT_SCHEMA_VERSION in
        ''|*[!0-9]*)    SCRIPT_SCHEMA_VERSION="0"; any_errors=1;;
    esac
    return $any_errors
}

check_prereqs() {
    if [ -z ${conn_sql_dir} ] ; then
        if [ -e "connections.sql" ] ; then
            conn_sql_dir=connections.sql
        elif [ -e "${MY_HOME}/xkit/connections.sql" ] ; then
            conn_sql_dir="${MY_HOME}/xkit/connections.sql"
        fi
    fi
    if [ ! -d "${conn_sql_dir}" ] ; then
        echo "Error: connections.sql \"${conn_sql_dir}\" does not exit."
        return 1
    else
        echo "Use connections.sql on \"${conn_sql_dir}\"."
    fi

    which perl || return 1
}

usage() {
    cat << EOM
Usage:
   fixup_db2.sh [options]"

Options:
   -s             SmartCloud mode
   -d db_name     specify database name to fixup, this option can repeat
                  multiple times for different databases.
   -a app_name    specify the app name (profiles, blogs, etc), this option
                  must be used together with -d option above.
   -p dir         set the connections.sql directory.
   -u user        the schema user who will be used to connect to the db
   -w password    the password of schema user
   -m mode        specify th mode, "pre"|"outage"|"post"
EOM
    exit 1
}

while getopts sa:d:p:u:w:m: opt
do
    case $opt in
    s) flag_smartcloud=1;;
    d) g_db_names="${g_db_names}${g_db_names:+ }${OPTARG}";;
    a) g_app_names="${g_app_names}${g_app_names:+ }${OPTARG}";;
    p) conn_sql_dir=${OPTARG};;
    u) schema_user=${OPTARG};;
    w) schema_pswd=${OPTARG};;
    m) running_mode=${OPTARG};;
    *) usage ;;
    esac
done
shift `expr $OPTIND - 1`

setup_log_file "${MYSELF##*/}.log"
LOG_PATH=$PWD/fixup_logs
[ -d "$LOG_PATH" ] || mkdir -p "$LOG_PATH" || exit 1
check_prereqs || exit 1
fixup_all_dbs

