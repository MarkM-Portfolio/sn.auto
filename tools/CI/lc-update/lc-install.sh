#!/bin/sh
# ***************************************************************** 
#                                                                   
# IBM Confidential                                                  
#                                                                   
# OCO Source Materials                                              
#                                                                   
# Copyright IBM Corp. 2011, 2013                                    
#                                                                   
# The source code for this program is not published or otherwise    
# divested of its trade secrets, irrespective of what has been      
# deposited with the U.S. Copyright Office.                         
#                                                                   
# ***************************************************************** 

. $(dirname $0)/functions.sh

usage() {
    echo "Usage: ${MYSELF} [options]"
    exit 1
}

get_default_was_home() {
    OS_NAME=`uname`
    WAS_HOME="/opt/IBM/WebSphere/AppServer"
    if [ "$OS_NAME" = "AIX" -a -d "/usr/IBM/WebSphere/AppServer" ]; then
        WAS_HOME="/usr/IBM/WebSphere/AppServer"
    fi
}


ensure_was_exists() {
    if [ -z "${WAS_HOME}" ]; then
        get_default_was_home
    fi
    echo -n "Look for WAS at: ${WAS_HOME} ... "
    if [ -d "$WAS_HOME" ]; then
        WSADMIN_CMD="${WAS_HOME}/bin/wsadmin.sh"
        echo "[ FOUND ]"
        return 0
    fi
    echo "[ NO ]"

    cat <<EOM
ERROR: WAS not found at "${WAS_HOME}"
    If WebSphere Application Server(WAS) is not installed at the default location,
    please set WAS_HOME, for example:
        export WAS_HOME=/local/IBM/WebSphere/AppServer.

    If you did set WAS_HOME, please make sure it exists

EOM
    exit 1
}


ensure_profile_exists() {
    WAS_PROFILE=${WAS_PROFILE:-AppSrv01}
    local default_path="${WAS_HOME}/profiles/${WAS_PROFILE}"
    local profile_path="${WAS_PROF_PATH:-$default_path}"
    echo -n "Look for profile in ${profile_path} ... "
    if [ -d "${profile_path}" ] ; then
        WSADMIN_CMD="${profile_path}/bin/wsadmin.sh"
        echo "[ FOUND ]"
        return 0
    fi
    echo "[ NO ]"
    cat <<EOM
ERROR: profile was not found at "${profile_path}",
       please make sure your WAS_PROFILE variable is set to the correct
       name of the profile.

EOM
    exit 1
}


ensure_was_ownership() {
    WAS_OWNER=`ls -dlL ${WAS_HOME} | awk '{print $3}'`
    CURR_USER=`whoami`
    if [ "${WAS_OWNER}" = "${CURR_USER}" ]; then
        return 0
    fi
    cat << EOM
ERROR - user '${CURR_USER}' does not own WebSphere installation.
  The directory ${WAS_HOME} is owned by '${WAS_OWNER}', but the current user \
is '${CURR_USER}', you should switch to '${WAS_OWNER}' to run WebSphere command.
EOM
    exit 1
}

extract_kit() {
    local LC_KIT="${MY_HOME}/IBM_Connections_Install"
    local XKIT="${MY_HOME}/xkit"
    if [ -d ${XKIT} ] ; then
        if [ ! -d "${LC_KIT}/installableApps" -o ${XKIT} -nt "${LC_KIT}" ]; then
            echo "INFO - found directory \"${XKIT}\", it is newer than ${LC_KIT}, reuse it for installation."
            echo "       if you want to force re-extract the kit, remove xkit first and run again."
            return 0
        fi
    else
        if [ ! -d "$LC_KIT" ] ; then
            echo "ERROR - Unable to find ${LC_KIT}, you can download daily kit from files server:"
            echo "        mlsa1.swg.usma.ibm.com:/gpfs/workplace/dailykits/LC3.0"
            exit 1
        fi
    fi
    echo "Extracting EAR, XML and other files from ${LC_KIT} ... "
    ${WSADMIN_CMD} -lang jython -conntype NONE \
        -javaoption "-Dpython.path=${MY_HOME}/lib" \
        -wsadmin_classpath "${MY_HOME}/lib/lccfg.jar" \
        -f "${MY_HOME}/bin/xkit.py" $LC_KIT $XKIT | tee -a "${LOG_FILE}"
}


#
# start running from here
#

STARTED_AT=`date`
FINISHED_AT=

MYSELF=`abs_path $0`
MY_BIN_DIR=`dirname ${MYSELF}`
MY_HOME=`dirname ${MY_BIN_DIR}`
HAS_ERRORS=0
LOG_FILE=${MY_HOME}/install.log

#determine if this is an update or an install
action="install"
stripConnOption=""
idx=0
flaggedIDX=-1
for arg in ${@}; do

    if [[ "${arg}" == "update" ]]; then
        action="update"
        flaggedIDX=$(( idx + 1 ))
    elif [[ $idx == $flaggedIDX ]] && [[ "${action}" == "update" ]]; then
        appList="${arg}"
    else
        stripConnOption="${stripConnOption}"" ""${arg}"; fi;
    idx=$(( idx + 1 )); done

#Now appList should either be empty or full of apps
#Pass it on as an argument to update.py
#Update.py should now check for that argument and handle updates accordingly

# clear out log file
if [ -f "${LOG_FILE}" ] ; then rm "${LOG_FILE}" ; fi

ensure_was_exists
ensure_profile_exists
ensure_was_ownership
extract_kit

if [ $# -eq 0 ] ; then
  CONN_OPTION="-conntype NONE"
else
  CONN_OPTION="${stripConnOption}"
fi

${WSADMIN_CMD} -lang jython ${CONN_OPTION} \
    -javaoption "-Dpython.path=${MY_HOME}/lib" \
    -javaoption "-Xmx512m" \
    -wsadmin_classpath "${MY_HOME}/lib/lccfg.jar" \
    -f "${MY_HOME}/bin/${action}.py" ${MY_HOME}/cfg.py \
    ${MY_HOME}/xkit ${appList} 2>&1 | tee -a "${LOG_FILE}"

if [ $? -ne 0 ]; then
    echo "wsadmin command failed, exit."
    HAS_ERRORS=1
fi

FINISHED_AT=`date`
echo STARTED AT:  $STARTED_AT | tee -a "${LOG_FILE}"
echo FINISHED AT: $FINISHED_AT | tee -a "${LOG_FILE}"
exit ${HAS_ERRORS}

