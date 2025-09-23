#!/bin/bash

. $(dirname $0)/functions.sh
MYSELF=`abs_path $0`
MY_BIN_DIR=`dirname ${MYSELF}`
MY_HOME=`dirname ${MY_BIN_DIR}`

# run java migration tool by calling the script name provided
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
    DB2_USER=${SCHEMAUSER} \
    DB2_PASSWORD=${SCHEMAPASS} \
    DB2_DB_NAME=${db_name} \
    sh $to_run
}

updateDB(){
if [ "${dbs}" == "0" ]; then
    app=$(${MY_BIN_DIR}/create_db.sh -p "${fixupDir}" -t "${1}")
else
    app="${databases[$2]}"; fi

#make sure we're working with a valid app
${MY_BIN_DIR}/create_db.sh -c ${1}
if [[ "${?}" == 1 ]]; then
    echo "unrecognized component name ""${1}"", skipping"
    return 0
fi

#find all fixups for a particular app
directory="${1}"
prepend=""
if [ "${1}" == "calendar" ]; then
    directory="communities"
    prepend="calendar-"; fi
fixup="${fixupDir}""/""${directory}""/db2/fixup/"

#get current version, then increment it by one
cv=$(${MY_BIN_DIR}/create_db.sh -p "${fixupDir}" -n "${app}" -q "${1}")
if [ ! $? -eq 0 ]; then
    echo "Error retrieving current version: ""${cv}"
    any_errors=0
    return 0
fi
version=$(( $cv + 1 ))
targetVersion=$(${MY_BIN_DIR}/create_db.sh -p "${fixupDir}" -z "${1}")
if [ ! $? -eq 0 ]; then
    echo "Error retrieving target version: ""${targetVersion}"
    any_errors=0
    return 0
fi
tvExists="false"

#check if the appropriate value exists
if [[ $( ls $fixup | grep -P -o '^('$prepend')(fixup)[0]*('$targetVersion')[j]?[s]?(.sql|.sh)$' ) != "" ]]; then
    tvExists="true"; fi

echo "${1}"" is currently on version ""${cv}"
echo "expected version for ""${1}"" after updates is ""${targetVersion}"

#continue only if the target version is greater than the current version and the target value exists
if [ "$targetVersion" -gt "$cv" ] && [ "${tvExists}" == "true" ]; then
    #check if a schema user and pass were provided. this changes the connect call
    if [[ -n "$SCHEMAUSER" ]] && [[ -n "$SCHEMAPASS" ]]; then
        db2 connect to "${app}" user "${SCHEMAUSER}" using "${SCHEMAPASS}"
    else
        db2 connect to "${app}"
    fi
    if [ $? -eq 0 ] ; then
      echo DB2 connected OK.
      for idx in `seq $version $targetVersion`; do
        run=""
        search=$( ls $fixup | grep -P -o '^('$prepend')(fixup)[0]*('$idx')[j]?(.sql|.sh)$' )
        scSearch=$( ls $fixup | grep -P -o '^('$prepend')(fixup)[0]*('$idx')(s.sql)$' )
        if [ "${smartCloud}" == "true" ] && [ "${scSearch}" != "" ]; then
            run="${scSearch}"
        elif [ "${search}" != "" ]; then
            run="${search}";
        fi

        #actually run the sql command
        if [ "${run}" != "" ]; then
            log_msg "running fixup ""$run"
            #check for "j"
            #remove any j's, check if the variable is the same
            #if it is not, then it must contain j's
            if [ "${run/j.sh/}" != "${run}" ]; then
                #run java file
                any_errors=0
                if ! run_java_migration_tool "${fixup}${run}" ${app} ; then
                    log_msg "Java migration ${run} failed."
                    any_errors=1
                    break
                fi
                log_msg "Java migration finished."
                continue
            fi
            containsConnect="false"
            db_command="${fixup}""${run}"
            default_db_name=$(${MY_BIN_DIR}/create_db.sh -p "${fixupDir}" -t "${1}")
            tempgrep1=`grep -i "CONNECT TO ${app}" ${db_command}`
            tempgrep2=`grep -i "CONNECT TO ${default_db_name}" ${db_command}`

            tmp_file=$(mktemp)
            file_content=$(cat "${db_command}")
            echo -e "${file_content}" > "${tmp_file}"

            if [[ "${tempgrep1}" != "" ]] || [[ "${tempgrep2}" != "" ]]; then
                containsConnect="true"
                db2 disconnect "${app}"
                #check if a schema user and pass were provided. this changes the connect call
                if [[ -n "$SCHEMAUSER" ]] && [[ -n "$SCHEMAPASS" ]]; then
                    #adjust to ignore case
                    #echo -e "${file_content//CONNECT TO ${default_db_name}/CONNECT TO ${app} USER ${SCHEMAUSER} USING ${SCHEMAPASS}}" > "${tmp_file}"
                    sed -i "s/CONNECT TO ${default_db_name}/CONNECT TO ${app} USER ${SCHEMAUSER} USING ${SCHEMAPASS}/i" "${tmp_file}" 
                else
                    #echo -e "${file_content//CONNECT TO ${default_db_name}/CONNECT TO ${app}}" > "${tmp_file}"; fi
                    sed -i "s/CONNECT TO ${default_db_name}/CONNECT TO ${app}/i" "${tmp_file}"; fi
            fi
            #check for disconnects
            tempgrep3=`grep -i "DISCONNECT ${default_db_name}" ${tmp_file}`
            if [[ "${tempgrep3}" != "" ]]; then
                #echo -e "${file_content//DISCONNECT ${default_db_name}/DISCONNECT ${app}}" > "${tmp_file}"
                sed -i "s/DISCONNECT ${default_db_name}/DISCONNECT ${app}/i" "${tmp_file}"
            fi
            tmp_output=$(mktemp)
            #pipe is causing a false positive return code
            #db2 -td@ -vf "${db_command}" | tee -a $tmp_output 2>&1
            db2 -td@ -vf "${tmp_file}" >> $tmp_output 2>&1
            ret_code=$?
            log_msg "finished running ${run}."
            cat $tmp_output
            if [ $ret_code -ne 0 ]; then
                if [ $ret_code -ge 4 ]; then #consider revision- ret_code of 2 can be a "DB21007E  End of file reached 
                                             #while reading the command.", whch we should stop at
                    log_msg "an error occured executing a sql command, exiting"
                    log_msg "error code $ret_code"
                    any_errors=1
                    break; fi
                #return code of 2 can contain: "DB21007E  End of file reached while reading the command."
                #check for this
                if [ $ret_code -eq 2 ]; then
                    check_for_db21007e=$(grep 'DB21007E' $tmp_output)
                    if [[ "${check_for_db21007e}" != "" ]]; then
                        log_msg "end of file reached while executing a sql command, exiting"
                        log_msg "error code $ret_code"
                        any_errors=1
                        break; fi
                fi
            fi
            #append tmp_output to the logfile
            cat $tmp_output
            if [ "${containsConnect}" == "false" ]; then
                db2 commit
            else
                if [[ -n "$SCHEMAUSER" ]] && [[ -n "$SCHEMAPASS" ]]; then
                    db2 connect to "${app}" USER ${SCHEMAUSER} USING ${SCHEMAPASS} 2>&1
                else
                    db2 connect to "${app}" 2>&1
                fi
            fi
        else
            log_msg "unable to locate fixup number ""${idx}"
        fi
      done

	run_sql_file "$fixupDir/$directory/db2/appGrants.sql"
	[ $? -ge 4 ] && any_errors=1

	run_sql_file "$fixupDir/$directory/db2/clearScheduler.sql"
	[ $? -ge 4 ] && any_errors=1

    db2 disconnect "${app}"
    else
        echo "unable to connect to \"${app}\""
        any_errors=0
        fi; fi
}

run_sql_file() {
    local sql_file=$1

    #fix to replace dbname in appgrants -> CONNECT TO ...
    tempgrep1=`grep -i "CONNECT TO ${app}" ${sql_file}`
    tempgrep2=`grep -i "CONNECT TO ${default_db_name}" ${sql_file}`

    tmp_file=$(mktemp)
    file_content=$(cat ${sql_file})
    echo -e "${file_content}" > "${tmp_file}"

    if [[ "${tempgrep1}" != "" ]] || [[ "${tempgrep2}" != "" ]]; then
        containsConnect="true"
        db2 disconnect "${app}"
        if [[ -n "$SCHEMAUSER" ]] && [[ -n "$SCHEMAPASS" ]]; then
            #echo -e "${file_content//CONNECT TO ${default_db_name}/CONNECT TO ${app} USER ${SCHEMAUSER} USING ${SCHEMAPASS}}" > "${tmp_file}"
            sed -i "s/CONNECT TO ${default_db_name}/CONNECT TO ${app} USER ${SCHEMAUSER} USING ${SCHEMAPASS}/i" "${tmp_file}"
        else
            #echo -e "${file_content//CONNECT TO ${default_db_name}/CONNECT TO ${app}}" > "${tmp_file}"; fi
            sed -i "s/CONNECT TO ${default_db_name}/CONNECT TO ${app}/i" "${tmp_file}"; fi
    fi
    #check for disconnects
    tempgrep3=`grep -i "DISCONNECT ${default_db_name}" ${tmp_file}`
    if [[ "${tempgrep3}" != "" ]]; then
        #echo -e "${file_content//DISCONNECT ${default_db_name}/DISCONNECT ${app}}" > "${tmp_file}"
        sed -i "s/DISCONNECT ${default_db_name}/DISCONNECT ${app}/i" "${tmp_file}"
    fi
    db2 -td@ -vf ${tmp_file} 2>&1
    return $?
}

verify(){

if [ "${dbs}" == "0" ]; then
    app=$(${MY_BIN_DIR}/create_db.sh -p "${fixupDir}" -t "${1}")
else
    app="${databases[$2]}"; fi

#make sure we're working with a valid app
${MY_BIN_DIR}/create_db.sh -c ${1}
if [[ "${?}" == 1 ]]; then
    errorMessage="unrecognized component name ""${1}"", skipping"
    echo "${errorMessage}"
    saveResult "${app}" fail "${errorMessage}"
    return 1
fi

#get current version, then increment it by one
cv=$(${MY_BIN_DIR}/create_db.sh -p "${fixupDir}" -n "${app}" -q "${1}")
if [ ! $? -eq 0 ]; then
    errorMessage="Error retrieving current version: ""${cv}"
    echo "${errorMessage}"
    saveResult "${app}" fail "${errorMessage}"
    return 1
fi

targetVersion=$(${MY_BIN_DIR}/create_db.sh -p "${fixupDir}" -z "${1}")
if [ ! $? -eq 0 ]; then
    errorMessage="Error retrieving target version: ""${targetVersion}"
    echo "${errorMessage}"
    saveResult "${app}" fail "${errorMessage}"
    return 1
fi

if [ ! $cv -eq $targetVersion ]; then
    errorMessage="Error: expected version [$targetVersion] and current version [$cv] of $app do not match"
    echo "${errorMessage}"
    saveResult "${app}" fail "${errorMessage}"
    return 1
else
    saveResult "${app}" success
    return 0
fi
}

#pass in 'app' and 'result'
#so, either: 'blogs success' or 'blogs fail'
saveResult(){

#remove existing hidden success/fail file
baseFileName="${fixupDir}""/""${1}""/db2/."
if [ -f "${baseFileName}""success" ]; then
    rm -f "${baseFileName}""success"
fi
if [ -f "${baseFileName}""fail" ]; then
    rm -f "${baseFileName}""fail"
fi

#create result file
#echo `date` > "${baseFileName}""${2}"

#if there is an error message, include it as well
#if [ ${#} -gt 2 ]; then
    #echo "${3}" >> "${baseFileName}""${2}"
#fi
}

####################
#    start here    #
####################

#define smartcloud mode
smartCloud="false"
#create log file
mkdir -p logs_fixDB
logFile="logs_fixDB/"`date +%Y%m%d`"_"`date +%H%M%S%Z`".txt"
setup_log_file "${logFile}"
out=$(echo "log for execution of: ""$*" >> $logFile)
any_errors=0

#schema user and pass will be empty unless defined in command line
SCHEMAUSER=
SCHEMAPASS=
#look for -p flag
fixupDir=${MY_HOME}/xkit/connections.sql
flaggedIDX="0"
dbs="0"
schemaUFlag="0"
schemaPFlag="0"
databases=()
for arg in ${@}; do
    if [[ "${flaggedIDX}" == "1" ]]; then
        fixupDir="${arg}"
        if [[ "${fixupDir: -1:1}" = '/' ]]; then
            fixupDir="${fixupDir:0: ( ${#fixupDir} - 1 )}"; fi
        flaggedIDX="0"
    elif [[ "${dbs}" == "1" ]]; then
        IFS=',' read -a databases <<< "${arg}"
        dbs="0"
    elif [[ "${schemaUFlag}" == "1" ]]; then
        SCHEMAUSER="${arg}"
        schemaUFlag="0"
    elif [[ "${schemaPFlag}" == "1" ]]; then
        SCHEMAPASS="${arg}"
        schemaPFlag="0"
    elif [[ "${arg}" == "-SCHEMAUSER" ]]; then
        schemaUFlag="1"
    elif [[ "${arg}" == "-SCHEMAPASS" ]]; then
        schemaPFlag="1"
    elif [[ "${arg}" == "-p" ]]; then
        flaggedIDX="1"
    elif [[ "${arg}" == "-s" ]]; then
        smartCloud="true"
    elif [[ "${arg}" == "-d" ]]; then
        dbs="1"
    elif [[ "${arg}" == "-h" ]] || [[ "${arg}" == "-help" ]]; then
        echo ""
        echo "  Usage:"
        echo ""
        echo "  Valid Flags:                                                           "
        echo "      -p             Full path to connections.sql                        "
        echo "                     ex.) -p /home/someUser/xkit/connections.sql         "
        echo "                                                                         "
        echo "      -s             Enables SmartCloud Mode                             "
        echo "                                                                         "
        echo "      -d             Enables Unique Database Name Mode                   "
        echo "                     Flag must precede comma separated list of DB names  "
        echo "                     Must be used in conjunction with comma separated    "
        echo "                     list of app names, where order is used to match DB  "
        echo "                     and app values                                      "
        echo "                     ex.) -d BLOGS2,NEWS2 blogs,news                     "
        echo "                                                                         "
        echo "      -SCHEMAUSER    For use with a schema user who is                   "
        echo "                     different from the instance user                    "
        echo "                     ex.) -SCHEMAUSER scUser                             "
        echo "                                                                         "
        echo "      -SCHEMAPASS    Provide password for unique schema user             "
        echo "                     ex.) -SCHEMAPASS mySecretPass                       "
        echo "                                                                         "
        echo "      Comma Separated Application List                                   "
        echo "                     ex.) blogs,communities,news                         "
        echo "                     empty list defaults to all applications             "
        echo "                                                                         "
        echo "      Note: target and current values of applications are                "
        echo "            compared before each transaction, up-to-date                 "
        echo "            applications will be skipped over automatically              "
        echo ""
        exit 0
    else
        #the only remaining argument is the application names
        #create an array of apps
        IFS=',' read -a applications <<< "${arg}"; fi; done

export SCHEMAUSER
export SCHEMAPASS
if [ ${#applications[@]} == 0 ]; then
    IFS=' ' read -a applications <<< "$(${MY_BIN_DIR}/create_db.sh -p "${fixupDir}" -x)"; fi

if [ ${#databases[@]} == 0 ] || [ ${#applications[@]} == ${#databases[@]} ]; then

    if [ ${#databases[@]} -gt 0 ]; then
        dbs="1"; fi
    echo "attempting to apply fixes to ""${#applications[@]}"" applications"

    #run fixups
    k=0
    for application in ${applications[@]}; do
        updateDB "${application}" $k
        (( k++ )); done
    #check if fixups worked, provide output
    k=0
    success=""
    fail=""
    for application in ${applications[@]}; do
        verify "${application}" $k
        if [ $? -eq 0 ]; then
            success="${success}"" ""${application}"
        else
            fail="${fail}"" ""${application}"
        fi
        if [[ "${fail}" == "" ]]; then
            fail="NONE"
        fi
        (( k++ ))
    done
    #print summary
    echo ""
    echo "SUMMARY:"
    echo "Successful: ""${success}"
    echo "Failed:     ""${fail}"
    echo""
#moved hidden files to each app folder so instances will (likely) have permission to save there
#instances are extremely unlikely to have permission to save in this directory
#
#    #keep track of summary in hidden files for external use elsewhere
#    if [ -f ${MY_BIN_DIR}/.success ]; then
#        echo "${success}" >> ${MY_BIN_DIR}/.success
#    fi
#
#    if [ -f ${MY_BIN_DIR}/.fail ]; then
#        echo "${fail}" >> ${MY_BIN_DIR}/.fail
#    fi
    exit "${any_errors}"
else
    echo "number of database names does not match number of applications provided"
    echo "no fixups were attempted"; fi
    exit 1

