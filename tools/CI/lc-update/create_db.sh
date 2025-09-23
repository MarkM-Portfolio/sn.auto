#!/bin/sh
# ***************************************************************** 
#                                                                   
# IBM Confidential                                                  
#                                                                   
# OCO Source Materials                                              
#                                                                   
# Copyright IBM Corp. 2012, 2013                                    
#                                                                   
# The source code for this program is not published or otherwise    
# divested of its trade secrets, irrespective of what has been      
# deposited with the U.S. Copyright Office.                         
#                                                                   
# ***************************************************************** 
. $(dirname $0)/functions.sh

WORKDIR=$PWD
DBSCRIPT_HOME="$HOME/connections.sql"
COMPONENTS="activities blogs dogear communities forum homepage files wikis profiles metrics calendar mobile library.os library.gcd pushnotification"
COMPONENTS_WITH_SCHEMA_CHANGED=
DBLIST=""
DB_ERRORS_DROP=
DB_ERRORS_CREATE=
DB_SCHEMA_VERSION=
LOG_FILE=create_db.log

verify_component(){
    case $1 in
        activities|blogs|dogear|communities|forum|homepage|files|wikis|profiles|metrics|calendar|mobile|pushnotification) return 0;;
        library.os|library.gcd) return 0;;
        *) return 1;;
    esac
}

prereqs_check() {
    DB2=`which db2 2>/dev/null`
    if [ "${DB2}" = "" -a -f "$HOME/sqllib/db2profile" ] ; then
        echo "Found $HOME/sqllib/db2profile, load it ..."
        . $HOME/sqllib/db2profile
    fi
    DB2=`which db2 2>/dev/null`
    if [ "${DB2}" = "" ]; then
        echo "Cannot find command 'db2', is db2 installed and is instance ready"
        echo "to use? Make sure db2 is in your PATH"
        exit 1
    fi
    ARCH=`uname -m`
    if [ "${ARCH}" = "s390x" ]; then
        echo "Running on S390, need to disable NO FILE SYSTEM CACHING"
    fi
}

comp_name_to_db_name() {
    if [ -n "${FORCED_DB_NAME}" ]; then
       DBNAME=${FORCED_DB_NAME}
       return 0
    fi
    case $1 in
    activities) DBNAME=OPNACT;;
    blogs) DBNAME=BLOGS;;
    calendar) DBNAME=SNCOMM;;
    dogear) DBNAME=DOGEAR;;
    bookmarks) DBNAME=DOGEAR;;
    communities) DBNAME=SNCOMM;;
    forum) DBNAME=FORUM;;
    homepage) DBNAME=HOMEPAGE;;
    profiles) DBNAME=PEOPLEDB;;
    files) DBNAME=FILES;;
    pushnotification) DBNAME=PNS;;
    wikis) DBNAME=WIKIS;;
    metrics) DBNAME=METRICS;;
    library.os) DBNAME=FNOS;;
    library.gcd) DBNAME=FNGCD;;
    mobile) DBNAME=MOBILE;;
    *)
        echo "ERROR - unknown component name '$1'"
        usage
    esac
    return 0
}

database_exists() {
    local db=$1
    db2 list db directory | grep $db
    return $?
}

# return "-tvf" or "-td@ -vf" based on the give SQL script is
# ending with @ or just the normal \n char
run_db2_sql_script() {
   local sql_file=$1
   local db2opts="-tvf"
   local has_errors=0
   [ -z "$sql_file" ] && return 8
   if [ ! -f "${sql_file}" ]; then
       echo "ERROR: file not exist: $sql_file"
       return 8
   fi
   cat "$sql_file" | tr -d '\r' | grep -q -e "@$" && db2opts="-td@ -vf"
   echo "RUNNING: db2 ${db2opts} ${sql_file}"
   db2 ${db2opts} ${sql_file} 2>&1
   db2_ret=$?
   echo "EXIT CODE: ($db2_ret)"
   [ $db2_ret -ge 4 ] && has_errors=1
   return $has_errors
}

drop_databases() {
    DB_ERRORS_DROP=""
    local has_errors=0
    echo "Here are the current database connections:"
    db2 list applications
    echo "Put instance [${DB2INSTANCE}] into quiesce mode."
    db2 quiesce instance ${DB2INSTANCE} immediate force connections
    # force close all db connections
    #db2 force applications all
    for c in ${COMPONENTS}; do
        comp_name_to_db_name $c
        if ! database_exists $DBNAME ; then
            echo "Database $DBNAME is not found, no need to drop it."
            continue
        fi
        local db_script_base="${DBSCRIPT_HOME}/${c}/db2/"
        [ "$c" = "calendar" ] && db_script_base="${DBSCRIPT_HOME}/communities/db2/${c}-"
        local db_script="${db_script_base}dropDb.sql"
        if ! run_db2_sql_script ${db_script} ; then
            has_errors=1
            DB_ERRORS_DROP="${DB_ERRORS_DROP} $c"
        fi
    done
    echo "Unquiesce the instance [${DB2INSTANCE}]"
    db2 unquiesce instance ${DB2INSTANCE}
    return $has_errors
}

backup_databases() {
    for db in $DBLIST; do
        local backup_dir=${WORKDIR}/db-backups
        if [ ! -d ${backup_dir} ]; then
            mkdir -p ${backup_dir}
        fi
        echo "Remove old  backup of [${db}] from [${backup_dir}] ..."
        rm -f ${backup_dir}/${db}*
        echo "db2 force application all"
        db2 force application all
        echo  "db2 backup db ${db} to ${backup_dir} compress"
        db2 backup db ${db} to ${backup_dir} compress
    done
}

restore_databases() {
    #db2stop force; db2start;
    for db in $DBLIST; do
        local backup_dir=${WORKDIR}/db-backups
        if [ -d ${backup_dir} ]; then
            cd ${backup_dir}
            echo "db2 force application all"
            db2 force application all
            echo "restore db ${db} without prompting"
            db2 restore db ${db} without prompting
        fi
    done
}

create_databases() {
    local has_errors=0
    for c in ${COMPONENTS}; do
        has_errors=0
        local db_script_base="${DBSCRIPT_HOME}/${c}/db2/"
        [ "${c}" = "calendar" ] && db_script_base="${DBSCRIPT_HOME}/communities/db2/${c}-"
        local create_db_script="${db_script_base}createDb.sql"
        local app_grants_script="${db_script_base}appGrants.sql"
        local init_data_script="${db_script_base}initData.sql"

        if [ "${ARCH}" = "s390x" ] ; then
            sed -i -e 's/NO FILE SYSTEM CACHING/FILE SYSTEM CACHING/g' \
                ${create_db_script}
        fi

        run_db2_sql_script ${create_db_script} || has_errors=1
        if [ -f "${init_data_script}" ] ; then
            run_db2_sql_script ${init_data_script} || has_errors=1
        fi

        # pause a little time hope the db2 backend process terminates completely
        sleep 5

        run_db2_sql_script ${app_grants_script} || has_errors=1

        # pause a little time hope the db2 backend process terminates completely
        sleep 5

        if [ $has_errors -ne 0 ]; then
            DB_ERRORS_CREATE="${DB_ERRORS_CREATE} $c"
        fi
    done
    test -z "${DB_ERRORS_CREATE}"
}

run_pre_xfer_scripts() {
    for c in ${COMPONENTS}; do
        run_db2_sql_script ${DBSCRIPT_HOME}/${c}/db2/predbxfer.sql
    done
}

load_data_to_databases() {
    for db in $DBLIST; do
        DBDIR="${WORKDIR}/db_backup/${db}"
        if [ -d "${DBDIR}" ]; then
            cd "${DBDIR}"
            db2move $db load
            cd ${WORKDIR}
        fi
    done
}

run_post_xfer_scripts() {
    for c in ${COMPONENTS}; do
        run_db2_sql_script ${DBSCRIPT_HOME}/${c}/db2/postdbxfer.sql
        run_db2_sql_script ${DBSCRIPT_HOME}/${c}/db2/appGrants.sql
        if [ -f ${DBSCRIPT_HOME}/${c}/db2/clearScheduler.sql ]; then
            run_db2_sql_script ${DBSCRIPT_HOME}/${c}/db2/clearScheduler.sql
        fi
    done
}

list_schema_version() {
    local current_schema=
    local new_schema=
    local schema_changed=
    for c in ${COMPONENTS} ; do
        get_schema_version $c
        current_schema=$DB_SCHEMA_VERSION
        get_script_schema_version $c
        new_schema=$DB_SCHEMA_VERSION
        echo "   $c: ${current_schema:-N/A}, ${new_schema:-N/A}"
        if [ "${current_schema}" != "${new_schema}" ]; then
            schema_changed="$c $schema_changed"
        fi
    done
    COMPONENTS_WITH_SCHEMA_CHANGED=${schema_changed}
    [ -z "${schema_changed}" ]
}

list_script_schema(){
    local current_schema=
    for c in ${COMPONENTS} ; do
        get_script_schema_version $c
        new_schema=$DB_SCHEMA_VERSION
        echo "   $c: ${new_schema:-N/A}"
    done
}

get_schema_version() {
    local schema_version=""
    DB_SCHEMA_VERSION=""
    local comp=$1
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
    comp_name_to_db_name $comp
    #check if a schema user and pass were provided. this changes the connect call
    if [[ -n "$SCHEMAUSER" ]] && [[ -n "$SCHEMAPASS" ]]; then
        db2 +o connect to "${DBNAME}" user "${SCHEMAUSER}" using "${SCHEMAPASS}"
    else
        db2 +o connect to "${DBNAME}";
    fi

    #db2 +o connect to ${DBNAME}
    schema_version=`db2 -x +p "${SQL}"`
    db2 +o disconnect ${DBNAME}
    DB_SCHEMA_VERSION=$(echo "${schema_version}" | sed 's/ //g')
    any_errors=0
    case $DB_SCHEMA_VERSION in
        ''|*[!0-9]*)    DB_SCHEMA_VERSION="ERROR- non-numeric value returned: ""${schema_version}"; any_errors=1;;
    esac
}

get_script_schema_version() {
    local comp=$1
    local db_script_base="${DBSCRIPT_HOME}/${comp}/db2/"
    [ "${comp}" = "calendar" ] && db_script_base="${DBSCRIPT_HOME}/communities/db2/${comp}-"
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
    DB_SCHEMA_VERSION=$(echo "${schema_version}" | sed 's/ //g')
    any_errors=0
    case $DB_SCHEMA_VERSION in
        ''|*[!0-9]*)    DB_SCHEMA_VERSION="ERROR- non-numeric value returned: ""${schema_version}"; any_errors=1;;
    esac
}

usage()
{
    cat << EOF

Usage: create_db.sh [opts] [component]

Options
    -a           automatically drop and recreate db that has schema change
    -b           backup data of existing db using db2backup
    -d           drop existing db
    -r           restore database from backup
    -s           list the schema version of databases
    -C           do not create databases
    -p <path>    specify the path of connections.sql
    -q comp      query schema version of the component
    -t comp      print the DB name of component
    -z comp      show the schema version of createDb.sql for component
    -L log_file  use the log_file instead create_db.log in current dir
    -x           list avaliable components

Components:
    The known components are:
      activities, blogs, communities, dogear, forum, files, wikis, homepage, profiles, library.os, library.gcd

EOF
    exit 1
}


load_components_list() {
    if [ -f ${WORKDIR}/components ]; then
        . ${WORKDIR}/components
    fi
    echo Components: ${COMPONENTS}
}

valiate_components_list() {
    local valid_components=""
    local db_script_base=""
    local db_script=""
    for c in $COMPONENTS ; do
        db_script_base="${DBSCRIPT_HOME}/${c}/db2/"
        [ "$c" = "calendar" ] && db_script_base="${DBSCRIPT_HOME}/communities/db2/${c}-"
        db_script="${db_script_base}createDb.sql"
        if [ -f "${db_script}" ] ; then
            valid_components="${valid_components}${valid_components:+ }${c}"
        else
            echo "WARNING: Unable to find createDb.sql in for [$c], skip."
        fi
    done
    COMPONENTS=${valid_components}
}

generate_db_list() {
    for c in ${COMPONENTS}; do
        comp_name_to_db_name $c
        DBLIST="${DBLIST} ${DBNAME}"
    done
    echo Databases: ${DBLIST}
}


restart_db_server() {
    echo "Killing DB2 with db2_kill ..."
    db2_kill
    echo "Starting DB2 ..."
    db2start
}

FLAG_CREATE=1
HAS_ERRORS=0
while getopts c:q:n:abdrslL:Cp:kt:z:x opt
do
    case $opt in
        a) FLAG_AUTO_RECREAT=1;;
        b) FLAG_BACKUP=1 ;;
        d) FLAG_DROPDB=1 ;;
        r) FLAG_RESTOREDB=1 ;;
        s) FLAG_LISTSCHEMA=1;;
        l) FLAG_SCRIPT_SCHEMA=1;;
        L) LOG_FILE="$OPTARG";;
        C) FLAG_CREATE=0;;
        p) DBSCRIPT_HOME=$OPTARG;;
        k) FLAG_RESTART_SERVER=1;;
        n) FORCED_DB_NAME=$OPTARG;;
        q) get_schema_version $OPTARG ; echo $DB_SCHEMA_VERSION ; exit $any_errors;;
        t) comp_name_to_db_name $OPTARG ; echo $DBNAME; exit;;
        z) get_script_schema_version $OPTARG; echo $DB_SCHEMA_VERSION; exit $any_errors;;
        x) echo $COMPONENTS; exit;;
        c) verify_component $OPTARG; exit $?;;
        \?) usage ;;
    esac
done
shift `expr $OPTIND - 1`

setup_log_file "$LOG_FILE"

if [ $# -gt 0 ] ; then
    COMPONENTS=$*
else
    load_components_list
fi

# validate options
if [ "${FLAG_RESTOREDB}" = "1" -a "${FLAG_CREATE}" = "1" ] ; then
    echo "ERROR - can not restore and create database, please use -C opion to skip create."
    exit 1
fi

valiate_components_list

generate_db_list

if [ "${FLAG_SCRIPT_SCHEMA}" = "1" ] ; then
    list_script_schema
fi

prereqs_check

if [ "${FLAG_BACKUP}" = "1" ]; then
    backup_databases
fi


if [ "${FLAG_RESTOREDB}" = "1" ]; then
    restore_databases
fi

if [ "${FLAG_LISTSCHEMA}" = "1" -o "${FLAG_AUTO_RECREAT}" = "1" ]; then
    list_schema_version
    if [ "${FLAG_LISTSCHEMA}" = "1" ]; then
        if [ -n "${COMPONENTS_WITH_SCHEMA_CHANGED}" ] ; then
            exit 1
        else
            exit 0
        fi
    fi
fi

if [ "${FLAG_AUTO_RECREAT}" = "1" ]; then
    echo "Will automatically recreate databases following components:"
    echo "  $COMPONENTS_WITH_SCHEMA_CHANGED"
    COMPONENTS=$COMPONENTS_WITH_SCHEMA_CHANGED
    FLAG_DROPDB=${FLAG_CREATE}
fi

[ "${FLAG_RESTART_SERVER}" = "1" ] && restart_db_server

if [ "${FLAG_DROPDB}" = "1" ]; then
    drop_databases
fi
if [ "${FLAG_CREATE}" = "1" ]; then
    create_databases
    if [ "$?" -ne 0 ] ; then
        echo "**********************************"
        echo "*   ERROR WHEN CREATE DATABASE   *"
        echo "**********************************"
        HAS_ERRORS=1
    fi
fi

echo "Exiting with: ${HAS_ERRORS:-0}"
echo "  when create database: ${DB_ERRORS_CREATE}"
echo "  when drop database: ${DB_ERRORS_DROP}"
exit ${HAS_ERRORS:-0}

