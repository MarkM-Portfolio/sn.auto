#!/bin/sh

WORKDIR=$PWD
COMPONENTS="activities activities_cd blogs dogear communities forum homepage files wikis profiles metrics calendar news"
#DBLIST="OPNACT BLOGS DOGEAR SNCOMM FORUM HOMEPAGE FILES WIKIS PEOPLEDB"
DBLIST=""
DBSCRIPT_HOME="$HOME/connections.sql"
DB_ERRORS_DROP=
DB_ERRORS_CREATE=

prereqs_check() {
    DB2=`which db2 2>/dev/null`
    if [ "${DB2}" = "" ]; then
        echo "Can not find command 'db2', is db2 is installed and instance is ready"
        echo "to use? Make sure db2 is in your PATH"
        exit 1
    fi
    ARCH=`uname -m`
    if [ "${ARCH}" = "s390x" ]; then
        echo "Running on S390, need to disable NO FILE SYSTEM CACHING"
    fi
}

comp_name_to_db_name() {
    case $1 in
    activities) DBNAME=OPNACT;;
    activities_cd) DBNAME=OPNACT;;
    blogs) DBNAME=BLOGS;;
    dogear) DBNAME=DOGEAR;;
    bookmarks) DBNAME=DOGEAR;;
    communities) DBNAME=SNCOMM;;
    calendar) DBNAME=SNCOMM;;
    forum) DBNAME=FORUM;;
    homepage) DBNAME=HOMEPAGE;;
    profiles) DBNAME=PEOPLEDB;;
    files) DBNAME=FILES;;
    wikis) DBNAME=WIKIS;;
    metrics) DBNAME=METRICS;;
    news) DBNAME=HOMEPAGE;;
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

drop_databases() {
    local has_errors=0
    for c in ${COMPONENTS}; do
        comp_name_to_db_name $c
        if ! database_exists $DBNAME ; then
            echo "Database $DBNAME is not found, no need to drop it."
            continue
        fi
        local db_script_base="${DBSCRIPT_HOME}/${c}/db2/"
        if [ "$c" = "calendar" ]; then
            db_script_base="${DBSCRIPT_HOME}/communities/db2/${c}-"
        fi
        local drop_db_script="${db_script_base}dropDb.sql"
        local db2_opts="-td@ -vf"
        if [ "${c}" = "profiles" -o "${c}" = "homepage" -o "${c}" = "news" ]; then
            local db2_opts="-tvf"
        fi
        db2 ${db2_opts} ${drop_db_script} || has_errors=1
        if [ $has_errors -ne 0 ]; then
            DB_ERRORS_DROP="${DB_ERRORS_DROP} $c"
        fi
    done
    return $has_errors
}

backup_databases() {
    for db in $DBLIST; do
        DBDIR=${WORKDIR}/db_backup/${db}
        if [ ! -d ${DBDIR} ]; then
            mkdir -p ${DBDIR}
        fi
        rm -f ${DBDIR}/*
        cd ${DBDIR}
        db2move ${db} export
    done
    cd ${WORKDIR}
}

create_databases() {
    local has_errors=0
    for c in ${COMPONENTS}; do
        local db_script_base="${DBSCRIPT_HOME}/${c}/db2/"
        if [ "$c" = "calendar" ]; then
            db_script_base="${DBSCRIPT_HOME}/communities/db2/${c}-"
        fi
        create_db_script="${db_script_base}createDb.sql"
        app_grants_script="${db_script_base}appGrants.sql"
        init_data_script="${db_script_base}initData.sql"
        create_db_options="-td@ -vf"
        app_grants_options="-td@ -vf"
        init_data_options="-tvf"

        if [ "${ARCH}" = "s390x" ] ; then
            sed -i -e 's/NO FILE SYSTEM CACHING/FILE SYSTEM CACHING/g' \
                ${create_db_script}
        fi

        case $c in
        homepage | profiles | news )
            create_db_options="-tvf"
            app_grants_options="-tvf"
            ;;
        esac
        echo "START to run: db2 ${create_db_options} ${create_db_script}"
        db2 ${create_db_options} ${create_db_script}
        local ret_code=$?
        if [ "$ret_code" -ge 4 ]; then
            echo "END of command: db2 ${create_db_options} ${create_db_script}"
            echo "     Exit code: $ret_code"
            has_errors=1
        fi
        if [ -f "${init_data_script}" ] ; then
            echo "START to run: db2 ${init_data_options} ${init_data_script}"
            db2 ${init_data_options} ${init_data_script}
            ret_code=$?
            if [ "$ret_code" -ne 0 ]; then
                echo "END of Command: db2 ${init_data_options} ${init_data_script}"
                echo "     Exit code: $ret_code"
                has_errors=1
            fi
        fi
        echo "START to run: db2 ${app_grants_options} ${app_grants_script}"
        db2 ${app_grants_options} ${app_grants_script}
        ret_code=$?
        if [ "$ret_code" -ne 0 ]; then
            echo "End of command: db2 ${app_grants_options} ${app_grants_script}"
            echo "     Exit code: $ret_code"
            has_errors=1
        fi
        # pause a little time hope the db2 backend process terminates completely
        sleep 5
    done
    return $has_errors
}

run_pre_xfer_scripts() {
    for c in ${COMPONENTS}; do
        case $c in
        activities | activities_cd | blogs | dogear | homepage | profiles | forum | communities | news )
            db2 -tvf ${DBSCRIPT_HOME}/${c}/db2/predbxfer25.sql
            ;;
        files | wikis )
            db2 -td@ -vf  ${DBSCRIPT_HOME}/${c}/db2/predbxfer30.sql
            ;;
        esac
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
        case $c in
        activities | activities_cd | blogs | dogear | homepage | profiles | forum | news )
            db2 -tvf ${DBSCRIPT_HOME}/${c}/db2/postdbxfer25.sql
            db2 -tvf ${DBSCRIPT_HOME}/${c}/db2/appGrants.sql
            if [ -f ${DBSCRIPT_HOME}/${c}/db2/clearScheduler.sql ]; then
                db2 -tvf ${DBSCRIPT_HOME}/${c}/db2/clearScheduler.sql
            fi
            ;;
        files | wikis )
            db2 -td@ -vf ${DBSCRIPT_HOME}/${c}/db2/postdbxfer30.sql
            db2 -td@ -vf ${DBSCRIPT_HOME}/${c}/db2/appGrants.sql
            ;;
        communities )
            db2 -td@ -vf ${DBSCRIPT_HOME}/${c}/db2/postdbxfer25.sql
            db2 -tvf ${DBSCRIPT_HOME}/${c}/db2/postdbxfer25_forum.sql
            db2 -tvf ${DBSCRIPT_HOME}/${c}/db2/appGrants.sql
            db2 -tvf ${DBSCRIPT_HOME}/${c}/db2/clearScheduler.sql
            ;;
        esac
    done
}

dump_db_schema_version() {
    db2 <<EOF
connect to BLOGS
select name, value from BLOGS.roller_properties where name='database.version' or name = 'database.schema.version'
disconnect BLOGS

connect to OPNACT
select * from ACTIVITIES.OA_SCHEMA
disconnect OPNACT

connect to SNCOMM
select * from SNCOMM.SNCOMM_SCHEMA
disconnect SNCOMM

connect to FORUM
select * from FORUM.DF_SCHEMA
disconnect FORUM

connect to DOGEAR
select * from DOGEAR.DOGEAR_SCHEMA
disconnect DOGEAR

connect to HOMEPAGE
select * from HOMEPAGE.HOMEPAGE_SCHEMA
disconnect HOMEPAGE

connect to FILES
select BUILD_NUMBER, SCHEMA_VERSION from FILES.PRODUCT
disconnect FILES

connect to WIKIS
select BUILD_NUMBER, SCHEMA_VERSION from WIKIS.PRODUCT
disconnect WIKIS

connect to PEOPLEDB
select * from EMPINST.SNPROF_SCHEMA
disconnect PEOPLEDB

EOF
}

usage()
{
    cat << EOF

Usage: create_db.sh [opts] [component]

Options
    -b backup data of existing db using db2move
    -d drop existing db
    -r restore data from backup
    -s list the schema version of databases
    -C do not create databases

Components:
    The known components are:
      activities, activities_cd, blogs, communities, dogear, forum, files, wikis, homepage, profiles, news

EOF
    exit 1
}


load_components_list() {
    if [ -f ${WORKDIR}/components ]; then
        . ${WORKDIR}/components
    fi

    echo Components: ${COMPONENTS}
}

generate_db_list() {
    for c in ${COMPONENTS}; do
        comp_name_to_db_name $c
        DBLIST="${DBLIST} ${DBNAME}"
    done
    echo Databases: ${DBLIST}
}

FLAG_CREATE=1
HAS_ERRORS=0
while getopts bdrsCp: opt
do
    case $opt in
        b) FLAG_BACKUP=1 ;;
        d) FLAG_DROPDB=1 ;;
        r) FLAG_RESTOREDB=1 ;;
        s) FLAG_LISTSCHEMA=1;;
        C) FLAG_CREATE=0;;
        p) DBSCRIPT_HOME=$OPTARG;;
        \?) usage ;;
    esac
done
shift `expr $OPTIND - 1`
if [ $# -gt 0 ] ; then
    COMPONENTS=$*
else
    load_components_list
fi
generate_db_list

prereqs_check

if [ "${FLAG_BACKUP}" = "1" ]; then
    backup_databases
fi
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
if [ "${FLAG_RESTOREDB}" = "1" ]; then
    run_pre_xfer_scripts
    load_data_to_databases
    run_post_xfer_scripts
fi
if [ "${FLAG_LISTSCHEMA}" = "1" ]; then
    dump_db_schema_version
fi

echo "Exiting with: ${HAS_ERRORS:-0}"
echo "  when drop database: ${DB_ERRORS_DROP}"
exit ${HAS_ERRORS:-0}
