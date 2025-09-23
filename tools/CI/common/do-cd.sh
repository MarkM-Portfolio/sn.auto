#!/bin/sh
#
# do-cd.sh

fail_with_error() {
    echo "$@"
    exit 1
}

MYSELF=`readlink -f $0`
export CI_COMMON_HOME=`dirname $MYSELF`
export CI_ROOT_HOME=`dirname ${CI_COMMON_HOME}`
export CI_HOME="${PWD}"

echo "CI_ROOT_HOME: ${CI_ROOT_HOME}"
echo "CI_COMMON_HOME: ${CI_COMMON_HOME}"
echo "CI_HOME: ${CI_HOME}"

echo "Removing ${CI_HOME}/prereq_fes..."
rm -rf prereq_fes

# Create a workitem properties file
${CI_COMMON_HOME}/gen_workitem_params.sh assign_to_automation

# Create link to lwp04 tools directory
[ -d lwp04.tools ] || (ln -sv "${CI_COMMON_HOME}/lwp04.tools" lwp04.tools || fail_with_error "Link failed.")

# For now, set to need download the source tree, and each component should
# override this in their ci.properties file to indicate they don't need src.
# We are going to update each team's unit test scripts, so none of the tests
# should requires to download the source code tree to run.
SRC_REQUIRED="yes"

if [ -n "$1" ]; then
	APPLICATION_NAME=$1
	shift # Need to get rid of argument(s) passed to this script - they mess up boot.sh which is sourced in below.
	
	APP_PROP_FILE_HOME="${CI_ROOT_HOME}/${APPLICATION_NAME}"
	echo "APP_PROP_FILE_HOME: \"${APP_PROP_FILE_HOME}\""
	echo "PWD: \"${PWD}\""
	
	# Copy component's ci.properties file to workspace.
	[ "${APP_PROP_FILE_HOME}" != "${PWD}" ] && (cp -v "${APP_PROP_FILE_HOME}/ci.properties" . || fail_with_error "Copy failed.")
	
	# Need to load the ci properties file here so we can find out what the name of the DB properties file is.
	echo "Loading ${WORKSPACE}/ci.properties..."
	. ./ci.properties || fail_with_error "Failed to load ci.properties."

	# Copy DB properties file (if specified) to workspace."
	[ "${APP_PROP_FILE_HOME}" != "${PWD}" ] && [ -n "${DBCFG_FILE}" ] && (cp -v "${APP_PROP_FILE_HOME}/${DBCFG_FILE}" . || fail_with_error "Copy failed.")
else
   APPLICATION_NAME=`basename $PWD`
   	echo "Loading ${WORKSPACE}/ci.properties..."
	. ./ci.properties || fail_with_error "Failed to load ci.properties."
fi

export APPLICATION_NAME

echo "Loading ${CI_COMMON_HOME}/ci_functions.sh..."
. ${CI_COMMON_HOME}/ci_functions.sh || fail_with_error "Failed to load ${CI_COMMON_HOME}/ci_functions.sh."

echo "Loading ${CI_COMMON_HOME}/system.properties..."
. ${CI_COMMON_HOME}/system.properties || fail_with_error "Failed to load ${CI_COMMON_HOME}/system.properties."

#Display environment variables
dumpenv() {
    echo "===================================================================="
    env
    echo "===================================================================="
}

#Find and download a new build
get_new_build() {
    local src_dir="${CI_HOME}/${SRC}"
    if [ -d ${src_dir} ]; then
        echo "Deleting source tree: ${src_dir}..."
        rm -rf "${src_dir}"
    fi
    echo "Creating source directory: ${src_dir}..."
    mkdir "${src_dir}"

    if [ "${SRC_REQUIRED}" = "yes" ] ; then
        echo "Downloading source code for ${BUILD_LABEL}..."
        export BUILD_DIR=/local/ci/common
        cd ${CI_COMMON_HOME}/wplctools
        
		local cnt=0
		local downloadOK=false
		while [ ${cnt} -lt 3 ]
		do
			perl JazzGetSourceFromBuildLabel.pl \
			-buildLabel ${BUILD_LABEL} -url ${REPOSITORY_URL} \
			-ru ${REPOSITORY_USER} -rpass ${CI_COMMON_HOME}/.icci_rtc \
			-loadDir ${src_dir} -NoClean
			if [ $? == 0 ]; then
				echo "Successfully downloaded sources."
				downloadOK=true
				break
			fi
				
			cnt=`expr ${cnt} + 1`
			if [ ${cnt} -lt 3 ]; then
				echo "Failed to download sources; will retry `expr 3 - ${cnt}` times..."
				echo "Waiting 60 seconds to retry..."
				sleep 60
			fi
		done

		if [ ${downloadOK} != true ]; then
			echo "Failed to download source after ${cnt} attempts."
			echo "Giving up..."
			return 1
		fi
    fi

	${LCUPDATE_DIR}/bin/get-build.sh -f -s "${WORKSPACE}/${SRC}" -b ${BUILD_LABEL} || return 1
	if [ "${BUILD_COMPONENT}" == "News" ]; then
		${LCUPDATE_DIR}/bin/get-build.sh -f -s "${WORKSPACE}/${SRC}" -B ${BUILD_STREAM}_Homepage || return 1
	fi

	return 0
}

capture_create_db_log() {
	echo "Removing any old create_db.log files..."
	rm -f "${WORKSPACE}/create_db.log"
	DB2INST_HOME=`sudo -u $DB2INST -H -i pwd`
	echo "Copying ${DB2INST_HOME}/create_db.log to ${WORKSPACE}..."
	sudo cp ${DB2INST_HOME}/create_db.log "${WORKSPACE}"
	sudo chown icci "${WORKSPACE}/create_db.log"
}

#Drop and recreate application database
recreate_db() {
    local dbsql_dir=${APPLICATION_NAME}
    local component2=""
	local dbname1=$dbsql_dir
	local dbname2=""
    case $dbsql_dir in
		bookmarks)
			dbname1=dogear
			;;
        calendar)
            dbsql_dir="communities"
            component2="calendar"
			dbname1=$dbsql_dir
			dbname2=$component2
			;;
        forums) 
			dbsql_dir="forum"
			dbname1=$dbsql_dir
			;;
        news) 
			dbsql_dir="homepage"
			dbname1=$dbsql_dir
			;;
    esac

    if [ -z "$DB2INSTANCE" -a -f $DB2INST_HOME/sqllib/db2profile ]; then
        . $DB2INST_HOME/sqllib/db2profile
    fi
    # collect the SQL scripts into connections.sql
    if [ -e "$CI_HOME/connections.sql/$dbsql_dir" ]; then
        rm -rf $CI_HOME/connections.sql/$dbsql_dir
    fi
    mkdir -p $CI_HOME/connections.sql/$dbsql_dir
    for i in db2 db.tokens oracle sqlserver ; do
        if [ -d "$CI_HOME/${SRC}/$SQLDIR/$i" ]; then
            cp -r $CI_HOME/${SRC}/$SQLDIR/$i $CI_HOME/connections.sql/$dbsql_dir/
        else
            echo "WARNING: not such directory? [$CI_HOME/${SRC}/$SQLDIR/$i]"
        fi
    done
    echo "Converting SQL script from DOS to Unix format"
    find ${CI_HOME}/connections.sql/${DBSQL_dIR} -name "*.sql" -exec dos2unix -q {} \;
    cd ${CI_HOME}
	
	echo "Removing tmp files left by dos2unix..."
	rm -fv ${CI_HOME}/d2utmp*
    
	echo "Run create_db.sh to create database"
    chmod -R go+rX $CI_HOME/connections.sql
    sudo -u $DB2INST -H -i ${CI_COMMON_HOME}/create_db.sh -p $CI_HOME/connections.sql -d ${dbsql_dir} ${component2}
	
	local app_list="$dbname1 $dbname2"
	for app in $app_list
	do
		echo "Getting DB schema version for ${app}..."
		SCHEMA_VERSION=`sudo -i -u $DB2INST ${CI_COMMON_HOME}/create_db.sh -q ${app}`
		if [ $? != 0 ]; then
			echo "Could not get schema version for ${app}."
			continue
		fi
		
		echo "Schema version for ${app}: ${SCHEMA_VERSION}"
	done
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

instrument_for_code_coverage() {
    echo "JACOCO: Instrumenting jars for code coverage..."
    ${JACOCO_SCRIPT_DIR}/instrument_local.sh ${JACOCO_SCRIPT_DIR}/jar_lists_local.sh
    if [ $? != 0 ]; then
        echo "JACOCO: Failed to instrument jar files."
        return 1
    fi
		
	echo "JACOCO: Removing any existing local code coverage data..."
	find ${WORKSPACE} -name "*.exec" -exec rm -fv {} \;

	echo "JACOCO: Removing any existing local code coverage reports..."
	rm -fv ${WORKSPACE}/jacoco_report.*

    return 0
}

generate_code_coverage_reports() {
	echo "JACOCO: Generating code coverage report..."
	${ANT_EXE} -v -lib ${JACOCO_JAR_DIR} -f ${JACOCO_BUILDFILE_DIR}/build_jacoco.xml report
	python ${JACOCO_SCRIPT_DIR}/gen_coverage_summary.py "${WORKSPACE}/jacoco_report.xml" "${WORKSPACE}/jacoco_report.summary"
	${JACOCO_SCRIPT_DIR}/gen_coverage_deltas.sh "${WORKSPACE}/tmp_jacoco/jacoco_report.summary" "${WORKSPACE}/jacoco_report.summary"
	[ $? == 10 ] && return 1
	return 0
}

umask 022

# default location of sources
export SRC=${SRC:-src}

export REPOSITORY_URL=https://swgjazz.ibm.com:8001/jazz/
export REPOSITORY_USER=icci@us.ibm.com

if [ "${ENABLE_CODE_COVERAGE}" == "true" ]; then
    echo "ENABLE_CODE_COVERAGE is set to \"true\", so will be running with code coverage instrumentation."
    flag_instrument_for_code_coverage=1
else
    echo "ENABLE_CODE_COVERAGE is NOT set to \"true\", so will NOT be running with code coverage instrumentation."
    flag_instrument_for_code_coverage=0
fi

dumpenv

cat <<EOF > wplclocal.sh
export APPLICATION_NAME
export APPDIR
export APPDIR_PRIMARY=${APPDIR}
export JAVA50_ROOT=${JAVA50_ROOT:-/local/ci/common/ibm-java2-i386-50}
export JAVA60_ROOT=${JAVA60_ROOT:-/local/ci/common/ibm-java-x86_64-60}
export ECLIPSE_HOME=${ECLIPSE_HOME:-/local/ci/common/eclipse}
export ECLIPSE34_HOME=${ECLIPSE34_HOME:-/local/ci/common/eclipse34}
export BINSDAILY=${BINSDAILY:-/net/mlsa2/ibm/releng/workplace/dailykits}
export MSTRDAILY=${MSTRDAILY:-/net/mlsa2/ibm/releng/workplace/dailybuilds/}
export FE_DOWNLOAD_DIR="\$BINSDAILY,\$MSTRDAILY"
EOF
chmod +x wplclocal.sh
# FIXME temp workaround hanging issue when access ndunix on lc30linux7
sed -i -e 's/\/ndunix\//\/_ndunix_\//' $CI_HOME/lwp04.tools/lwp/boot.sh
export BUILD_HOME=${BUILD_HOME:-$PWD}
. $CI_HOME/lwp04.tools/lwp/boot.sh
echo "APPDIR_PRIMARY: ${APPDIR_PRIMARY}"
# set proper LANG for Ubuntu
#export LANG=en_US.utf8
#export LANG=en_US.UTF-8

if [ -n "$CLASSPATH" ]; then
   export CLASSPATH=$CLASSPATH:${JACOCO_JAR_DIR}/jacocoagent.jar
else
    export CLASSPATH=${JACOCO_JAR_DIR}/jacocoagent.jar
fi

echo "JACOCO: CLASSPATH: ${CLASSPATH}"

# Create a local tmp directory to keep the Jenkins Jacoco plug-in happy.
# It wants to know where your sources are and there is no way to tell it you
# don't have any. So I point it to an empty directory.
echo "Creating directory ${WORKSPACE}/tmp_jacoco for Jenkins Jacoco plug-in...."
mkdir "${WORKSPACE}/tmp_jacoco"

unhide_was_runtime_jar

get_new_build || exit 1

# in case some component need to replace the db config before build tests
# projects, so far we don't have any
#replace_db_cfg_file

# The following code coverage instrumenation is commented out and put later on since
# building a test directory results in everything being rebuilt, which would overwrite
# the instrumented jars.
#if [ "$flag_instrument_for_code_coverage" -eq "1" ]; then
#    instrument_for_code_coverage
#    if [ $? != 0 ]; then
#        echo "Code coverage instrumention failure."
#        flag_instrument_for_code_coverage=0
#    fi
#fi

# Create a workitem properties file
${CI_COMMON_HOME}/gen_workitem_params.sh

#
# JUnit Tests
#
echo "PREPARING TO RUN UNIT TESTS..."
NUM_DIRS=${#TEST_DIR[@]}
errors=0
for i in `seq 1 $NUM_DIRS`
do
    cd $CI_HOME/${SRC}/$APPDIR/${TEST_DIR[$i]}

    if [ -z "${TEST_CMD[$i]}" ]; then
        if [ -n "${TEST_CMD}" ]; then
            echo "====================================================================="
            echo "`date "+[%Y-%m-%d %H:%M:%S]"` [${TEST_DIR[$i]}] $ ${TEST_CMD}"
            echo "====================================================================="
            ${TEST_CMD} || errors=1
        else
            echo "ERROR: no test command provided for test dir ${TEST_DIR[$i]}"
            echo "       check ci.properties file"
            exit 2
        fi
    else
        echo "====================================================================="
        echo "`date "+[%Y-%m-%d %H:%M:%S]"` [${TEST_DIR[$i]}] $ ${TEST_CMD[$i]}"
        echo "====================================================================="
        ${TEST_CMD[$i]} || errors=1
    fi
done

if [ ${errors} != 0 ]; then
    exit 1
fi

cd $CI_HOME

if [ "$flag_instrument_for_code_coverage" -eq "1" ]; then
    instrument_for_code_coverage
    if [ $? != 0 ]; then
        echo "Code coverage instrumention failure."
        flag_instrument_for_code_coverage=0
    fi
fi

#
# Database Operations
#
if [ -n "$RECREATEDB" -a "$RECREATEDB" = "true" ]
then
    echo "====================================================================="
    echo "`date "+[%Y-%m-%d %H:%M:%S]"` recreate database"
    echo "====================================================================="
    recreate_db
	status=$?
	capture_create_db_log
	[ ${status} -eq 0 ] || exit 5 
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
echo "RUNNING UNIT TESTS..."
NUM_DIRS=${#POST_TEST_DIR[@]}
errors=0
for i in `seq 1 $NUM_DIRS`
do
    cd $CI_HOME/${SRC}/$APPDIR/${POST_TEST_DIR[$i]}

    if [ -z "${POST_TEST_CMD[$i]}" ]; then
        if [ -n "${POST_TEST_CMD}" ]; then
            echo "====================================================================="
            echo "`date "+[%Y-%m-%d %H:%M:%S]"` [${POST_TEST_DIR[$i]}] $ ${POST_TEST_CMD}"
            echo "====================================================================="
            ${POST_TEST_CMD} || errors=1
        else
            echo "ERROR: no test command provided for test dir ${POST_TEST_DIR[$i]}"
            echo "       check ci.properties file"
            exit 2
        fi
    else
        echo "====================================================================="
        echo "`date "+[%Y-%m-%d %H:%M:%S]"` [${POST_TEST_DIR[$i]}] $ ${POST_TEST_CMD[$i]}"
        echo "====================================================================="
        ${POST_TEST_CMD[$i]} || errors=1
    fi
done

if [ "$flag_instrument_for_code_coverage" -eq "1" ]; then
    generate_code_coverage_reports
    if [ $? != 0 ]; then
        echo "Code coverage threshold violation!"
    fi
fi

unhide_was_runtime_jar

cd $CI_HOME

if [ ${errors} != 0 ]; then
    exit 1
fi

echo "CI passed."

echo "Removing ${CI_HOME}/prereq_fes..."
rm -rf prereq_fes

exit 0
