#!/bin/sh
#
# do-ci.sh

#
# If RUN_WITH_EMMA is set, will run with code coverage instrumentation.
#

WAS_HOME=${WAS_HOME:-/opt/IBM/WebSphere/AppServer/profiles/AppSrv01}
#WAS_HOST=${WAS_HOST:-localhost}
WAS_PORT=${WAS_PORT:-8880}
WAS_USER=${WAS_USER:-wasadmin}
WAS_PSWD=${WAS_PSWD:-lcsecret}

export WAS_HOME
export WAS_HOST
export WAS_PORT
export WAS_USER
export WAS_PSWD

THIS_DIR=`pwd`
export CI_HOST_FQDN=`hostname -f`
export CI_USER_HOME=/local/home/icci
export CI_COMMON_HOME=/local/ci/common
export HTTP_SERVER_HOME=/local/opt/IBM/HTTPServer

#Display environment variables
dumpenv() {
    echo "===================================================================="
    env
    echo "===================================================================="
}

#Drop and recreate application database
recreate_db() {
    case $APPLICATION in
        forums) DBSQL_DIR="forum";;
        communities.emma) DBSQL_DIR="communities";;
        *)      DBSQL_DIR=$APPLICATION;;
    esac

    if [ -z "$DB2INSTANCE" -a -f $DB2INST_HOME/sqllib/db2profile ]; then
        . $DB2INST_HOME/sqllib/db2profile
    fi

    # collect the SQL scripts into connections.sql
    if [ -e "$CI_HOME/connections.sql/$DBSQL_DIR" ]; then
        rm -rf $CI_HOME/connections.sql/$DBSQL_DIR
    fi
    mkdir -p $CI_HOME/connections.sql/$DBSQL_DIR
    for i in db2 db.tokens oracle sqlserver ; do
        if [ -d "$CI_HOME/${SRC}/$SQLDIR/$i" ]; then
            cp -r $CI_HOME/${SRC}/$SQLDIR/$i $CI_HOME/connections.sql/$DBSQL_DIR/
        fi
    done
    find ${CI_HOME}/connections.sql/${DBSQL_dIR} -name "*.sql" -exec dos2unix {} \;
    cd $CI_HOME
    sudo su - $DB2INST -c "$CI_HOME/../common/create_db.sh -p $CI_HOME/connections.sql -d $DBSQL_DIR"
}

replace_db_cfg_file() {
    if [ -z "$DBCFG_FILE" ] ; then
        echo "WARNING: DBCFG_FILE is not set, skip copy db cfg for unit tests."
        return 1
    fi
    if [ ! -f "$CI_HOME/$DBCFG_FILE" ] ; then
        echo "WARNING: The specified ci db config file $CI_HOME/$DBCFG_FILE does not exist."
    fi
    echo copy from $CI_HOME/$DBCFG_FILE $CI_HOME/${SRC}/$APPDIR/$DBCFG_DIR/$DBCFG_FILE
    cp -r "${CI_HOME}/$DBCFG_FILE" "$CI_HOME/${SRC}/$APPDIR/$DBCFG_DIR/$DBCFG_FILE"
}

# workaround the issue of:
#
# [profiles.test/bvt/junit] WARNING: multiple versions of ant detected in path for junit 
#         jar:file:/local/ci/profiles/lwp04.tools/lwp/mantis/lib/ant.jar!/org/apache/tools/ant/Project.class
#     and jar:file:/local/ci/profiles/prereq_fes/sn.prereqs/lwp/prereqs.sn/was610/com.ibm.ws.runtime.dist.jar!/org/apache/tools/ant/Project.class
#junit.framework.AssertionFailedError: Forked Java VM exited abnormally. Please note the time in the report does not reflect the time until the VM exit.

hide_was_runtime_jar() {
  local dirs="was610 was7.0"
  local jar_files=""
  if [ "${HIDE_WAS_RUNTIME_JAR}" != "true" ]; then
    return
  fi
  for dir in $dirs; do
    jar_file="$CI_HOME/prereq_fes/sn.prereqs/lwp/prereqs.sn/${dir}/com.ibm.ws.runtime.dist.jar"
    if [ -f "${jar_file}" ]; then
        echo "Hide $jar_file"
        mv "${jar_file}" "${jar_file}_HIDE"
    fi
  done
}

unhide_was_runtime_jar() {
  local dirs="was610 was7.0"
  local jar_files=""
  for dir in $dirs; do
    jar_file="$CI_HOME/prereq_fes/sn.prereqs/lwp/prereqs.sn/${dir}/com.ibm.ws.runtime.dist.jar"
    if [ -f "${jar_file}_HIDE" ]; then
        echo "un-Hide ${jar_file}"
        mv "${jar_file}_HIDE" "${jar_file}"
    fi
  done
}

update_ear_file_on_was_server() {
    cd ${CI_HOME}
    echo "Debug: EARFILE=\"${EAR_FILE}\""
    if [ -z "${EAR_FILE}" ]; then
        echo "EAR_FILE is not set, skip to update EAR"
        return 0
    fi
    if [ -z "${EAR_APP_NAME}" ]; then
        echo "EAR_APP_NAME is not set, unable to update EAR"
        return 0
    fi
    if [ ! -f "${CI_HOME}/${SRC}/${APPDIR}/${EAR_FILE}" ] ; then
        echo "ERROR: file \"${CI_HOME}/${SRC}/${APPDIR}/${EAR_FILE}\" does not exist, build failed??"
	    return 1
    fi
    echo "Update App [${EAR_APP_NAME}] with file [${EAR_FILE}] on WAS server"
    echo "  WAS_HOST=${WAS_HOST}"
    echo "  WAS_PORT=${WAS_PORT}"
    echo "  WAS_USER=${WAS_USER}"
    echo "  WAS_PSWD=******"
    cat >update_ear.py <<EOF
AdminApp.update("${EAR_APP_NAME}", "app",
    [ '-operation', 'update', '-contents', "${CI_HOME}/${SRC}/${APPDIR}/${EAR_FILE}" ])
AdminConfig.save()
EOF
    echo ${WAS_HOME}/bin/wsadmin.sh
    ${WAS_HOME}/bin/wsadmin.sh -conntype SOAP -lang jython \
        -host ${WAS_HOST} -port ${WAS_PORT} -user ${WAS_USER} -password ${WAS_PSWD} \
        -f update_ear.py
}

instrument_for_code_coverage() {
	echo "EMMA: Instrumenting jars for code coverage..."
	${EMMA_SCRIPT_DIR}/emma_instr_ci.sh ${CONFIG_FILE} ${EMMA_SCRIPT_DIR}/emma_jar_lists_ci.sh
	if [ $? != 0 ]; then
		echo "EMMA: Failed to instrument jar files."
		return 1
	fi

	echo "EMMA: Removing any existing local code coverage data..."
	for i in `seq 1 ${#TEST_CMD[@]}`
	do
		COVERAGE_DATA_FILE=${CI_HOME}/${SRC}/${APPDIR}/${TEST_DIR[$i]}/coverage.ec
		if [ -f "${COVERAGE_DATA_FILE}" ]; then
			echo "EMMA: Removing code coverage data file ${COVERAGE_DATA_FILE}..."
			rm -f ${COVERAGE_DATA_FILE}
			if [ $? != 0 ]; then
				echo "EMMA: Could not remove code coverage data file ${COVERAGE_DATA_FILE}."
				return 1
			fi
		fi
	done

	for i in `seq 1 ${#POST_TEST_CMD[@]}`
	do
		COVERAGE_DATA_FILE=${CI_HOME}/${SRC}/${APPDIR}/${POST_TEST_DIR[$i]}/coverage.ec
		if [ -f "${COVERAGE_DATA_FILE}" ]; then
			echo "EMMA: Removing code coverage data file ${COVERAGE_DATA_FILE}..."
			rm -f ${COVERAGE_DATA_FILE}
			if [ $? != 0 ]; then
				echo "EMMA: Could not remove code coverage data file ${COVERAGE_DATA_FILE}."
				return 1
			fi
		fi
	done
	
	echo "EMMA: Removing any existing WAS code coverage data..."	
	COVERAGE_DATA_FILE=${EMMA_RUNTIMEDATA_DIR}/${APPLICATION}_was.ec	
	if [ -f ${COVERAGE_DATA_FILE} ]; then
		rm ${COVERAGE_DATA_FILE}
		if [ $? != 0 ]; then
			echo "EMMA: Could not remove file: ${COVERAGE_DATA_FILE}"
			return 1
		fi
	fi
	
	return 0
}

gather_code_coverage_data() {
	echo "EMMA: Gathering code coverage data..."
	${EMMA_SCRIPT_DIR}/dump_coverage_data_ci.sh ${CONFIG_FILE} ${THIS_DIR}/ci.properties
	if [ $? != 0 ]; then
		echo "EMMA: Failed to get code coverage data."
		return 1
	fi
		
	return 0
}

reserve_a_was(){
	echo "Reserving a WAS host..."
	RESERVATION_STRING=`${CI_USER_HOME}/jython-2.5.2/jython -Dpython.path="${CI_USER_HOME}/emma/wink-json4j-1.1.3-incubating.jar" ${CI_COMMON_HOME}/reserve_a_was.py`
	STATUS=$?
	WAS_HOST_FQDN_TMP=`echo ${RESERVATION_STRING} | cut -d ',' -f 1`
	WAS_HOST_ID=`echo ${RESERVATION_STRING} | cut -d ',' -f 2`
	if [ ${STATUS} != 0 -o -z "${WAS_HOST_FQDN_TMP}" -o "${WAS_HOST_FQDN_TMP}" == "None" ]; then
		echo "Could not reserve a WAS host."
		echo "Reservation call returned status code: ${STATUS}"
		echo "Reservation call returned message: ${RESERVATION_STRING}"
		RESERVED_A_WAS=false
		return 1
	fi		
	
	RESERVED_A_WAS=true
	
	WAS_HOST_TMP=`echo ${WAS_HOST_FQDN_TMP} | cut -d '.' -f 1`
	WAS_SERVER_TMP=server1
#	WAS_NODE_TMP=${WAS_HOST_TMP}Node01
	WAS_NODE_TMP=lc45linux1Node01
	WAS_PORT_NORMAL_TMP=9080
	WAS_PORT_SECURE_TMP=9443
	REMOTE_USER_TMP=lcuser
	REMOTE_USER_HOME_TMP=/home/lcuser

	# Create a directory on the reserved server to copy files.
	echo "Creating directory \"${REMOTE_USER_HOME_TMP}/ci/${APPLICATION}\" on \"${WAS_HOST_FQDN_TMP}\"."
	ssh ${REMOTE_USER_TMP}@${WAS_HOST_FQDN_TMP} mkdir -p ${REMOTE_USER_HOME_TMP}/ci/${APPLICATION}
	if [ $? != 0 ]; then
		echo "Could not create directory \"${REMOTE_USER_HOME_TMP}/ci/${APPLICATION}\" on \"${WAS_HOST_FQDN_TMP}\"."
		return_a_was
		return 1
	fi
	
	# If we get here, we have successfully reserved a WAS, so set
	# the env variables so the ci.properties files will pick them up.
	WAS_HOST_FQDN=${WAS_HOST_FQDN_TMP}
	WAS_HOST=${WAS_HOST_TMP}
	WAS_SERVER=${WAS_SERVER_TMP}
	WAS_NODE=${WAS_NODE_TMP}
	WAS_PORT_NORMAL=${WAS_PORT_NORMAL_TMP}
	WAS_PORT_SECURE=${WAS_PORT_SECURE_TMP}
	REMOTE_USER=${REMOTE_USER_TMP}
	REMOTE_USER_HOME=${REMOTE_USER_HOME_TMP}
		
	echo "Reserved a WAS host with the following paramaters:"
	echo "WAS_HOST: ${WAS_HOST}"
	echo "WAS_HOST_FQDN: ${WAS_HOST_FQDN}"
	echo "WAS_HOST_ID: ${WAS_HOST_ID}"
	echo "WAS_NODE: ${WAS_NODE}"
	echo "WAS_PORT_NORMAL: ${WAS_PORT_NORMAL}"
	echo "WAS_PORT_SECURE: ${WAS_PORT_SECURE}"
	echo "REMOTE_USER: ${REMOTE_USER}"
	echo "REMOTE_USER_HOME: ${REMOTE_USER_HOME}"
	
	return 0
}

return_a_was(){
	echo "Returning a WAS host reservation..."
	if [ "${RESERVED_A_WAS}" != "true" ]; then
		echo "There was no reservation to return."
		return 0
	fi
	
	echo "WAS_HOST_ID: ${WAS_HOST_ID}"

	${CI_USER_HOME}/jython-2.5.2/jython -Dpython.path="${CI_USER_HOME}/emma/wink-json4j-1.1.3-incubating.jar" ${CI_COMMON_HOME}/return_a_was.py ${WAS_HOST_ID}
	if [ $? != 0 ]; then
		echo "Error occurred trying to return a WAS host reservation with ID: ${WAS_HOST_ID}"
		return 1
	fi
	
	RESERVED_A_WAS=false
	return 0
}

finally(){
	return_a_was
}

trap 'finally' EXIT

. ./ci.properties
if [ $? != 0 ]; then
	echo "Failed to load ci.properties."
	exit 1
fi

if [ -z "${CI_HOME}" ]; then
	echo "\${CI_HOME} is null."
	exit 1
fi

export SRC=${CI_SRC_DIR:-src}
export CI_PREBVT_RESULTS_DIR=pre-bvt-ci-results/${CI_PREBVT_TEST_NAME}/${buildDefinitionId}-${buildLabel}
export CI_PREBVT_HTTP_DEST_DIR=${HTTP_SERVER_HOME}/htdocs/${CI_PREBVT_RESULTS_DIR}

if [ -n "${RUN_WITH_EMMA}" ]; then
	echo "RUN_WITH_EMMA is set, so will be running with code coverage instrumentation."
	if [ -n "${EAR_FILE}" -a "${INSTRUMENT_THE_EAR}" == true ]; then
		echo "EAR_FILE and INSTRUMENT_THE_EAR are set, so will be instrumenting ${EAR_FILE}."
	else
		echo "EAR_FILE and/or INSTRUMENT_THE_EAR are NOT set, so will be NOT be instrumenting any ear files."
	fi
	
	if [ "${TRACK_EMMA_RESULTS}" == "true" ]; then
		echo "TRACK_EMMA_RESULTS is set to \"true\"."
	fi
	
	flag_instrument_for_code_coverage=1	
else
	echo "RUN_WITH_EMMA is NOT set, so will NOT be running with code coverage instrumentation."
	flag_instrument_for_code_coverage=0
fi

dumpenv
cd $CI_HOME

cat <<EOF > wplclocal.sh
export BUILD_HOME=${BUILD_HOME:-/local/ci/$APPLICATION}
export APPLICATION
export APPDIR
export JAVA50_ROOT=${JAVA50_ROOT:-/local/ci/common/ibm-java2-i386-50}
export JAVA60_ROOT=${JAVA60_ROOT:-/local/ci/common/ibm-java-x86_64-60}
export ECLIPSE_HOME=${ECLIPSE_HOME:-/local/ci/common/eclipse}
export ECLIPSE34_HOME=${ECLIPSE34_HOME:-/local/ci/common/eclipse34}
export BINSDAILY=${BINSDAILY:-/net/mlsa2/ibm/releng/workplace/dailykits}
export MSTRDAILY=${MSTRDAILY:-/net/mlsa2/ibm/releng/workplace/dailybuilds/}
export FE_DOWNLOAD_DIR="\$BINSDAILY,\$MSTRDAILY"
export WAS_HOME=/local/opt/IBM/WebSphere/AppServer
export TMP=${CI_HOME}/tmp
export CI_HOME
export CI_PREBVT_TEST_NAME
export CI_PREBVT_TEST_CLASS
EOF
chmod +x wplclocal.sh

# FIXME temp workaround hanging issue when access ndunix on lc30linux7
sed -i -e 's/\/ndunix\//\/_ndunix_\//' $CI_HOME/lwp04.tools/lwp/boot.sh
. $CI_HOME/lwp04.tools/lwp/boot.sh

# set proper LANG for Ubuntu 
#export LANG=en_US.utf8
#export LANG=en_US.UTF-8

EMMA_HOME=/local/home/icci/emma

if [ -n "$CLASSPATH" ]; then
	export CLASSPATH=$CLASSPATH:${EMMA_HOME}/emma.jar
else
	export CLASSPATH=${EMMA_HOME}/emma.jar
fi

echo "EMMA: CLASSPATH: ${CLASSPATH}"

if [ "$flag_instrument_for_code_coverage" -eq "1" ]; then	
	# Need to source in emma config file after JAVA_HOME is set.
	CONFIG_FILE=${EMMA_HOME}/scripts/connectionsci1_emma
	echo "EMMA: Sourcing in config file: ${CONFIG_FILE}..."
	. ${CONFIG_FILE}
	if [ $? != 0 ]; then
		echo "EMMA: Could not source in config file: ${CONFIG_FILE}."
		flag_instrument_for_code_coverage=0
	fi
fi

unhide_was_runtime_jar

#
# Assumption is that all dependent FE directory entries in the
# APPDIR array follow the entry for this FE, i.e., the first entry
# in the array should always be the directory for this FE. So will
# iterate through the array in reverse order. Done this way so that
# ${APPDIR} refers to this FE's directory.
#
echo "Number of FEs to build: ${#APPDIR[@]}"
NUM_APPDIRS_MINUS_ONE=`expr ${#APPDIR[@]} - 1`
for i in `seq ${NUM_APPDIRS_MINUS_ONE} -1 0` 
do
	APPDIR_FULLPATH=${CI_HOME}/${SRC}/${APPDIR[${i}]}
	echo "APPDIR_FULLPATH: ${APPDIR_FULLPATH}"
	if [ ! -d ${APPDIR_FULLPATH} ]; then
		echo "WARNING: The directory \"${APPDIR_FULLPATH}\" does not exist."
		continue
	fi

	cd ${APPDIR_FULLPATH}

	echo "====================================================================="
	if [ -n "${BLD_CLEAN}" ] ; then
		echo "Executing \"wsbld clean\"..."
		echo "====================================================================="
		wsbld clean
		echo "====================================================================="
	fi

	if [ -n "${CALL_SCRIPT_BEFORE_FE_DOWNLOAD}" -a "${CALL_SCRIPT_BEFORE_FE_DOWNLOAD}" != "false" ]; then
		echo "Calling component specific script before FE download: ${CALL_SCRIPT_BEFORE_FE_DOWNLOAD}"
		echo "====================================================================="
		${CALL_SCRIPT_BEFORE_FE_DOWNLOAD} || exit 4
		echo "====================================================================="
	fi

	echo " wsbld downloadFEs"
	echo "====================================================================="
	wsbld downloadFEs || exit 1
	echo "====================================================================="
	
	if [ "${DBCFG_REPLACE_BEFORE_BUILD}" == "true" ]; then
		echo "====================================================================="
		echo "replace_db_cfg_file"
		replace_db_cfg_file || exit 3
		echo "====================================================================="
	fi
	
	if [ -n "${CALL_SCRIPT_BEFORE_BUILD}" -a "${CALL_SCRIPT_BEFORE_BUILD}" != "false" ]; then
		echo "====================================================================="
		echo "Calling component specific script before build: ${CALL_SCRIPT_BEFORE_BUILD}"
		${CALL_SCRIPT_BEFORE_BUILD} || exit 4
		echo "====================================================================="
	fi
		
	echo " wsbld $BUILD_TYPE"
	echo "====================================================================="
	wsbld ${BUILD_TYPE} || exit 2
done

# in case some component need to replace the db config before build tests
# projects, so far we don't have any
#replace_db_cfg_file

if [ "$flag_instrument_for_code_coverage" -eq "1" ]; then
	instrument_for_code_coverage
	if [ $? != 0 ]; then
		echo "Code coverage instrumention failure."
		flag_instrument_for_code_coverage=0
	fi
fi

# At this point the component sources have been built. If the component's
# tests are going to use WAS in any way, the code from here on out needs
# to be protected against WAS redeploys.
# Not going to check for lock timeouts, since the component
# could have unit tests that don't depend on WAS. The TEST_CMD and POST_TEST_CMD
# arrays could have anything.
if [ "${CHECK_FOR_WAS_REDEPLOY_LOCK}" == "true" ]; then
	exec 8>/local/ci/common/was_redeploy_${WAS_HOST}.lock
	flock -s -w 7200 8
fi

#
# JUnit Tests
#
echo "RUNNING UNIT TESTS"
NUM_DIRS=${#TEST_DIR[@]}
errors=0
for i in `seq 1 $NUM_DIRS`
do
    cd $CI_HOME/${SRC}/$APPDIR/${TEST_DIR[$i]}

    if [ -z "${TEST_CMD[$i]}" ]; then
        if [ -n "${TEST_CMD}" ]; then
			echo "Date/Time: `date`"
            echo "====================================================================="
            echo " [${TEST_DIR[$i]}] $ ${TEST_CMD}"
            echo "====================================================================="
            ${TEST_CMD} || errors=1
        else
            echo "ERROR: no test command provided for test dir ${TEST_DIR[$i]}"
            echo "       check ci.properties file"
            exit 2
        fi
    else
		echo "Date/Time: `date`"
        echo "====================================================================="
        echo " [${TEST_DIR[$i]}] $ ${TEST_CMD[$i]}"
        echo "====================================================================="
        ${TEST_CMD[$i]} || errors=1
    fi
done

if [ ${errors} != 0 ]; then
	exit 1
fi

cd $CI_HOME

#
# Database Operations
#
if [ -n "$RECREATEDB" -a "$RECREATEDB" = "true" ]
then
    echo "Date/Time: `date`"
    echo "====================================================================="
    echo " recreate database"
    echo "====================================================================="
    recreate_db || exit 5
fi


# replace db configuration file after db is created, this normally tells the test
# code to use JDBC drivers instead of find the from JNDI (which requires WAS),
# and also the db port, and user id, pw etc.
if [ "${DBCFG_REPLACE_BEFORE_BUILD}" != "true" ]; then
	replace_db_cfg_file
fi

hide_was_runtime_jar

#
# Commands Post Database Operations 
#
NUM_DIRS=${#POST_TEST_DIR[@]}
errors=0
for i in `seq 1 $NUM_DIRS`
do
    cd $CI_HOME/${SRC}/$APPDIR/${POST_TEST_DIR[$i]}
    
    if [ -z "${POST_TEST_CMD[$i]}" ]; then
        if [ -n "${POST_TEST_CMD}" ]; then
			echo "Date/Time: `date`"
		    echo "====================================================================="
		    echo " [${POST_TEST_DIR[$i]}] $ ${POST_TEST_CMD}"
		    echo "====================================================================="
            ${POST_TEST_CMD} || errors=1
        else
            echo "ERROR: no test command provided for test dir ${POST_TEST_DIR[$i]}"
            echo "       check ci.properties file"
            exit 2
        fi
    else
		echo "Date/Time: `date`"
        echo "====================================================================="
        echo " [${POST_TEST_DIR[$i]}] $ ${POST_TEST_CMD[$i]}"
        echo "====================================================================="
        ${POST_TEST_CMD[$i]} || errors=1
    fi
done

NUM_DIRS=${#PREBVT_DIR[@]}

if [ -n "${RUN_WITH_EMMA}" ]; then
	echo "This is a code coverage run."
	echo "SKIPPING PreBVT TESTS..."
	if [ "$flag_instrument_for_code_coverage" -eq "1" ]; then	
		gather_code_coverage_data
	fi

elif [ -f ${CI_COMMON_HOME}/SKIP_PREBVT ]; then
	echo "File \"${CI_COMMON_HOME}/SKIP_PREBVT\" exists."
	echo "SKIPPING PreBVT TESTS..."

elif [ -z "${NUM_DIRS}" -o "${NUM_DIRS}" -lt 1 ]; then
	echo "No PreBVT tests specified."

elif [ "${WAS_HOST}" == "None" ]; then
	echo "No available WAS."
	echo "SKIPPING PreBVT TESTS..."

else
	# Copy bvt files from distribution area.
	# This operation needs to be semaphored locked with
	# the file copying in the LCAUTO CI build.
	echo "Copying ${HTTP_SERVER_HOME}/htdocs/bvt.dist to ${CI_HOME}/pre-bvt..."
	flock -w 120 ${CI_COMMON_HOME}/copy-bvt-dist.lock -c "rsync -avL --delete ${HTTP_SERVER_HOME}/htdocs/bvt.dist ${CI_HOME}/pre-bvt"

	# Run the tests.
	echo "RUNNING PreBVT TESTS..."
	export errors_prebvt=0
	for i in `seq 1 $NUM_DIRS`
	do
		cd $CI_HOME/${SRC}/$APPDIR/${PREBVT_DIR[$i]}
    
		echo "Date/Time: `date`"
		echo "====================================================================="
		echo " [${PREBVT_DIR[$i]}] $ ${PREBVT_CMD[$i]}"
		echo "====================================================================="
		${PREBVT_CMD[$i]} || export errors_prebvt=1
	done
fi

unhide_was_runtime_jar

# This is the phase of deploy the EAR onto WAS
cd $CI_HOME

if [ "${UPDATE_EAR_ON_EXIT}" == "true" ]; then
	update_ear_file_on_was_server
fi

if [ ${errors} != 0 ]; then
	exit 1
fi

exit 0
