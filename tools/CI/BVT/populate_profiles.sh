#!/bin/sh
#
# Populate Profiles database with the settings provided on
# command line
#

. $(dirname $0)/functions.sh

#DB_TYPE=db2
#DB_HOST=yguo00.swg.usma.ibm.com
#DB_PORT=50000
#DB_NAME=peopledb

#DB_TYPE=oracle
#DB_HOST=lc30linux5.swg.usma.ibm.com
#DB_PORT=1521
#DB_NAME=lsconn
#DB_PASSWORD=lcsecret

#LDAP_SERVER=sunldap63.swg.usma.ibm.com
#LDAP_PORT=389
#LDAP_USER="uid=fvtbind,cn=users,l=WestfordFVT,st=Massachusetts,c=US,ou=Lotus,o=Software Group,dc=ibm,dc=com"
#LDAP_PASSWORD="fvtbind"
#LDAP_BASE="cn=users,l=WestfordFVT,st=Massachusetts,c=US,ou=Lotus,o=Software Group,dc=ibm,dc=com"

#LDAP_SERVER=tds62ldap.swg.usma.ibm.com
#LDAP_PORT=389
#LDAP_USER="uid=Fvt Admin,cn=users,l=WestfordFVT,st=Massachusetts,c=US,ou=Lotus,o=Software Group,dc=ibm,dc=com"
#LDAP_PASSWORD=fvtadmin
#LDAP_BASE="cn=users,l=WestfordFVT,st=Massachusetts,c=US,ou=Lotus,o=Software Group,dc=ibm,dc=com"

usage()
{
    cat << EOM
Usage: populate_profiles.sh config_file

EOM
}

mk_db_url_sqlserver() {
    db_url="jdbc:sqlserver:\\/\\/${DB_HOST}:${DB_PORT:-1433};DatabaseName=PEOPLEDB"
    db_driver="com.microsoft.sqlserver.jdbc.SQLServerDriver"
    db_user="${DB_USER:-PROFUSER}"
}

mk_db_url_oracle() {
    db_url="jdbc:oracle:thin:@${DB_HOST}:${DB_PORT:-1432}:lsconn"
    db_driver="oracle.jdbc.pool.OracleConnectionPoolDataSource"
    db_user="${DB_USER:-PROFUSER}"
}

mk_db_url_db2() {
    db_url="jdbc:db2:\\/\\/${DB_HOST}:${DB_PORT}\\/peopledb"
    db_driver="com.ibm.db2.jcc.DB2Driver"
    db_user="${DB_USER:-LCUSER}"
}

set_db_and_ldap_info() {
    case $DB_TYPE in
    db2*) mk_db_url_db2 ;;
    oracle*) mk_db_url_oracle ;; 
    sqlserver*) mk_db_url_sqlserver ;;
    esac

    db_password=${DB_PASSWORD:-password}

    echo "Set DB and LDAP info in: ${PROF_TDI_PROPERTIES}"

    local ldap_url="ldap:\\/\\/${LDAP_SERVER}:${LDAP_PORT}"
    local ldap_filter="${LDAP_FILTER:-(\\&(uid=*)(objectClass=inetOrgPerson))}"
    echo "FILTER: $ldap_filter"

    cat > tdi.$$.sed <<EOF
s/^dbrepos_jdbc_url=.*/dbrepos_jdbc_url=${db_url}/
s/^dbrepos_jdbc_driver=.*/dbrepos_jdbc_driver=${db_driver}/
s/^dbrepos_username=.*/dbrepos_username=${db_user}/
s/^{protect}-dbrepos_password=.*/{protect}-dbrepos_password=${db_password}/
s/^source_ldap_url=.*/source_ldap_url=${ldap_url}/
s/^source_ldap_user_login=.*/source_ldap_user_login=${LDAP_USER}/
s/^source_ldap_search_base=.*/source_ldap_search_base=${LDAP_BASE}/
s/{protect}-source_ldap_user_password=.*/{protect}-source_ldap_user_password=${LDAP_PASSWORD}/
s/^source_ldap_search_filter=.*/source_ldap_search_filter=${ldap_filter}/
EOF

    mv ${PROF_TDI_PROPERTIES} ${PROF_TDI_PROPERTIES}.orig
    sed -f tdi.$$.sed ${PROF_TDI_PROPERTIES}.orig > ${PROF_TDI_PROPERTIES}
    rm tdi.$$.sed
    if [ -d ${MY_HOME}/etc/ldap/${LDAP_SERVER} ] ; then
        cp ${MY_HOME}/etc/ldap/${LDAP_SERVER}/* ${MY_HOME}/tdi/TDI/
    else
        echo "--- WARNING ---"
        echo Unknown LDAP server, missing mapping info may lead to population failure
    fi
}

# example:
#   extract_tdisol /home/yguo/src/lc-update/xkit/tdisol.tar
extract_tdisol() {
    local tdisol_tar=$1
    if [ ! -d "${MY_HOME}/tdi" ] ; then
        mkdir "${MY_HOME}/tdi"
    fi
    rm -rf "${MY_HOME}/tdi/TDI"
    cd ${MY_HOME}/tdi && tar xf ${tdisol_tar}
    chmod +x TDI/*.sh TDI/netstore
    PROF_TDI_PROPERTIES="${MY_HOME}/tdi/TDI/profiles_tdi.properties"
    if [ ! -f "${PROF_TDI_PROPERTIES}" ]; then
        echo "ERROR - unable to find file \"${PROF_TDI_PROPERTIES}\""
        exit 1
    fi
}

run_tdi_population() {
    export TDIPATH=${TDIPATH:-/opt/IBM/TDI/V7.1}
    echo "Running Profiles TDISOL with ${TDIPATH}"
    cd "${MY_HOME}/tdi/TDI"
    echo "Call collect_dns.sh ..."
    ./collect_dns.sh
    echo "Call populate_from_dn_file.sh ..."
    ./populate_from_dn_file.sh
    echo "Call makr_managers.sh ..."
    ./mark_managers.sh
    for i in fill_*.sh; do
        echo "Call $i ..."
        ./$i
    done
}

usage() {
    exit 1
}

verify_config_info_provided() {
    if [ -z "${DB_TYPE}" -o \
         -z "${DB_HOST}" -o \
         -z "${DB_PORT}" -o \
         -z "${DB_NAME}" -o \
         -z "${DB_PASSWORD}" -o \
         -z "${LDAP_SERVER}" -o \
         -z "${LDAP_PORT}" -o \
         -z "${LDAP_USER}" -o \
         -z "${LDAP_PASSWORD}" -o \
         -z "${LDAP_BASE}" ]; then
         cat << EOM
ERROR - all of these envrionment variable need to set
Required:
    DB_TYPE=${DB_TYPE}
    DB_HOST=${DB_HOST}
    DB_PORT=${DB_PORT}
    DB_NAME=${DB_NAME}
    DB_PASSWORD=${DB_PASSWORD}
    LDAP_SERVER=${LDAP_SERVER}
    LDAP_PORT=${LDAP_PORT}
    LDAP_USER=${LDAP_USER}
    LDAP_PASSWORD=${LDAP_PASSWORD}
    LDAP_BASE=${LDAP_BASE}
Optionally:
    DB_USER=${DB_USER}
EOM
        return 1
    fi
    return 0
}

MYSELF=`abs_path $0`
MY_BIN_DIR=`dirname ${MYSELF}`
MY_HOME=`dirname ${MY_BIN_DIR}`

CONFIG_FILE=$1
if [ -n "${CONFIG_FILE}" ] ; then
    if [ ! -f "${CONFIG_FILE}" ]; then
        echo "ERROR - unable to find configuration file ${CONFIG_FILE} in ${PWD}"
        usage
    fi
    ABS_CONFIG_FILE=`abs_path ${CONFIG_FILE}`
    echo "Loading configuration from ${ABS_CONFIG_FILE}"
    . "${ABS_CONFIG_FILE}"
    if [ $? -ne 0 ] ; then
        echo "ERROR - failed to load configuration from ${CONFIG_FILE}"
        exit 1
    fi
fi

verify_config_info_provided || exit 1
extract_tdisol ${MY_HOME}/xkit/tdisol.tar
set_db_and_ldap_info
run_tdi_population

