#!/bin/sh

MYSELF=`readlink -f $0`
MYHOME=`dirname ${MYSELF}`

WAS_HOME=${WAS_HOME:-/opt/IBM/WebSphere/AppServer}
TARGET_SERVER=${TARGET_SERVER:-icbvtlinux1.swg.usma.ibm.com}
SERVER_SOAP=${SERVER_PORT:-8880}
ADMIN_USER=${ADMIN_USER:-wasadmin}
ADMIN_PSWD=${ADMIN_PSWD:-lcsecret}
IC_ADMIN_DIR=${IC_ADMIN_DIR:-${MYHOME}/bin_lc_admin}

TEST_TO_RUN=all

if [ ! -f ${IC_ADMIN_DIR}/commonConnections.py ]; then
    echo "ERROR: unable to find IBM Connections admin scripts."
    echo "  You can copy IC admin scripts to here: [${IC_ADMIN_DIR}] "
    echo "  Or, set env variable IC_ADMIN_DIR to where admin scripts exists."
    exit 1
fi

[ -n "$1" ] && TEST_TO_RUN=$1

# clear the temp directory
if [ -z "$MYHOME" ] ; then
    echo "Please don't run me from the / direcrory."
    exit 1
fi
[ -d "${MYHOME}/tmp" ] || mkdir -p ${MYHOME}/tmp
rm -rf ${MYHOME}/tmp/*

cd ${MYHOME}

if [ "none" = "${TEST_TO_RUN}" ] ; then
    WS_F_ARG=""
else
    WS_F_ARG="-f ${MYHOME}/test/test_${TEST_TO_RUN}.py"
fi
export IC_ADMIN_DIR
$WAS_HOME/bin/wsadmin.sh  -lang jython \
    ${TARGET_SERVER:+-host ${TARGET_SERVER}} ${SERVER_SOAP:+-port ${SERVER_SOAP}} \
    -username ${ADMIN_USER} -password ${ADMIN_PSWD} \
    -javaoption "-Dpython.path=${MYHOME}/test" \
    ${WS_F_ARG}
passed=$?
echo Exit Code is: $passed
#stty sane
exit $passed

