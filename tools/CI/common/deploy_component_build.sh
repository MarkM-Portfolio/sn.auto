#!/bin/sh

# Create the workitem.properties file
${CI_COMMON_HOME}/gen_workitem_params.sh assign_to_automation

echo "`date +"%F %T,%3N"` [CI-CD] INFO Loading ${CI_COMMON_HOME}/ci_functions.sh..."
. ${CI_COMMON_HOME}/ci_functions.sh
if [ $? != 0 ]; then
	echo "`date +"%F %T,%3N"` [CI-CD] ERROR Failed to load ${CI_COMMON_HOME}/ci_functions.sh."
	exit 1
fi

fail_with_error() {
    echo_error "$@"
	${CI_COMMON_HOME}/get-was-info.sh
    exit 1
}

DEPLOY_EAR_ONLY=false
DEPLOY_JS_TEST_BUNDLES=false
case ${BUILD_COMPONENT} in
    Activities)
        APP_LIST_FOR_CODE_COVERAGE="Activities"
		;;

    Blogs)
        APP_LIST_FOR_CODE_COVERAGE="Blogs"
		;;

    Bookmarks)
        APP_LIST_FOR_CODE_COVERAGE="Dogear"
		INFRA_LIST_FOR_CODE_COVERAGE="Dogear_Infra"
		;;

    Communities)
        APP_LIST_FOR_CODE_COVERAGE="Communities"
		;;

    ExtensionsRegistry)
        APP_LIST_FOR_CODE_COVERAGE="ExtensionsRegistry"
		DEPLOY_EAR_ONLY=true
		APPLICATION_NAME=AppRegistry
		EAR_FILENAME=scee.ear
		;;

	Forums)
        APP_LIST_FOR_CODE_COVERAGE="Forums"
		;;

    HomepageNews)
        APP_LIST_FOR_CODE_COVERAGE="Homepage News"
		INFRA_LIST_FOR_CODE_COVERAGE="News_Infra"
		;;
    
	Homepage)
        APP_LIST_FOR_CODE_COVERAGE="Homepage"
		;;

    Infra)
        APP_LIST_FOR_CODE_COVERAGE=""
		;;

    Moderation)
        APP_LIST_FOR_CODE_COVERAGE="Moderation"
		;;
    
	News)
        APP_LIST_FOR_CODE_COVERAGE="News"
		INFRA_LIST_FOR_CODE_COVERAGE="News_Infra"
		;;
     
	PlacesCatalog)
        APP_LIST_FOR_CODE_COVERAGE="PlacesCatalog"
        ;;
                   
	Profiles)
        APP_LIST_FOR_CODE_COVERAGE="Profiles"
		DEPLOY_JS_TEST_BUNDLES=true
		;;

    RichTextEditors)
        APP_LIST_FOR_CODE_COVERAGE="RichTextEditors"
		DEPLOY_EAR_ONLY=true
		APPLICATION_NAME=RichTextEditors
		EAR_FILENAME=rte.ear
		;;

    Search)
        APP_LIST_FOR_CODE_COVERAGE="Search"
        ;;

    Share)
        APP_LIST_FOR_CODE_COVERAGE="Files Wikis"
		;;

    UI)
        APP_LIST_FOR_CODE_COVERAGE=""
		DEPLOY_JS_TEST_BUNDLES=true
		;;

    *)
        fail_with_error "Unkown component: ${BUILD_COMPONENT}"
		;;
esac

echo_info "Loading ${CI_COMMON_HOME}/system.properties..."
. ${CI_COMMON_HOME}/system.properties
[ $? -eq 0 ] || fail_with_error "Failed to load ${CI_COMMON_HOME}/system.properties."

echo_info "Loading server-info.properties..."
. ./server-info.properties
[ $? -eq 0 ] || fail_with_error "Failed to load server-info.properties."

# Create a directory on the remote WAS to copy files.
echo_info "Creating directory \"${REMOTE_CI_HOME}\" on \"${WAS_HOST_FQDN}\"..."
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no ${REMOTE_USER}@${WAS_HOST_FQDN} mkdir -p ${REMOTE_CI_HOME}
[ $? -eq 0 ] || fail_with_error "Could not create directory \"${REMOTE_CI_HOME}\" on \"${WAS_HOST_FQDN}\"."

echo_info "Updating lc-update scripts  on ${WAS_HOST_FQDN}..."
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no ${REMOTE_USER}@${WAS_HOST_FQDN} "cd ${REMOTE_LCUPDATE_DIR}; git pull"
[ $? -eq 0 ] || fail_with_error "Unable to update lc-update scripts on ${WAS_HOST_FQDN}."

echo_info "Downloading base Connections build to ${WAS_HOST_FQDN}..."
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no ${REMOTE_USER}@${WAS_HOST_FQDN} "cd ${REMOTE_LCUPDATE_DIR}; bin/get-build.sh -B IC10.0_Connections"
[ $? -eq 0 ] || fail_with_error "Failed to download base Connections build to ${WAS_HOST_FQDN}."

echo_info "Downloading latest promoted Infra build to ${WAS_HOST_FQDN}..."
INFRA_BUILD_LABEL=`cat ${DAILY_BUILD_ROOT_DIR}/${BUILD_STREAM}_Infra/BVTBuildLabel.txt`
echo_info "Downloading build [${INFRA_BUILD_LABEL}] to ${WAS_HOST_FQDN}..."
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no ${REMOTE_USER}@${WAS_HOST_FQDN} "cd ${REMOTE_LCUPDATE_DIR}; bin/get-build.sh -f -x -b ${INFRA_BUILD_LABEL}"
[ $? -eq 0 ] || fail_with_error "Failed to download build [${INFRA_BUILD_LABEL}] to ${WAS_HOST_FQDN}."

echo_info "Downloading latest promoted UI build to ${WAS_HOST_FQDN}..."
UI_BUILD_LABEL=`cat ${DAILY_BUILD_ROOT_DIR}/${BUILD_STREAM}_UI/BVTBuildLabel.txt`
echo_info "Downloading build [${UI_BUILD_LABEL}] to ${WAS_HOST_FQDN}..."
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no ${REMOTE_USER}@${WAS_HOST_FQDN} "cd ${REMOTE_LCUPDATE_DIR}; bin/get-build.sh -f -x -b ${UI_BUILD_LABEL}"
[ $? -eq 0 ] || fail_with_error "Failed to download build [${UI_BUILD_LABEL}] to ${WAS_HOST_FQDN}."

echo_info "Downloading component build [${BUILD_LABEL}] to ${WAS_HOST_FQDN}..."
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no ${REMOTE_USER}@${WAS_HOST_FQDN} "cd ${REMOTE_LCUPDATE_DIR}; bin/get-build.sh -f -x -b ${BUILD_LABEL}"
[ $? -eq 0 ] || fail_with_error "Failed to download build [${BUILD_LABEL}] to ${WAS_HOST_FQDN}."

if [ "${ENABLE_CODE_COVERAGE}" == "true" -a -n "${APP_LIST_FOR_CODE_COVERAGE}" ]; then
    echo_info "ENABLE_CODE_COVERAGE is set to \"true\""
    echo_info "Check to see is Jacoco jar available on [${WAS_HOST_FQDN}]."
    ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no ${REMOTE_USER}@${WAS_HOST_FQDN} \
        "ls -l ${REMOTE_JAVA_HOME}/lib/ext/jacocoagent.jar"
	if [ $? -ne 0 ] ; then
        echo_warn "*******************************************************************"
        echo_warn "[$WAS_HOST_FQDN] does not have Jacoco, we can not do code coverage!"
        echo_warn "*******************************************************************"
    else
        echo_info "instrumenting ${BUILD_COMPONENT} jars..."
        ${JACOCO_SCRIPT_DIR}/instrument_server.sh "${APP_LIST_FOR_CODE_COVERAGE}"
        [ $? -eq 0 ] || echo_error "Failed to instrument EAR file(s)."

        if [ -n "${INFRA_LIST_FOR_CODE_COVERAGE}" ]; then
            echo_info "ENABLE_CODE_COVERAGE is set to \"true\", instrumenting Infra jars..."
            ${JACOCO_SCRIPT_DIR}/instrument_server.sh "${INFRA_LIST_FOR_CODE_COVERAGE}"
            [ $? -eq 0 ] || echo_error "Failed to instrument Infra jars in EAR file(s)."
        fi
    fi
fi

# For debug, print out webresources line from LotusConnections-config.xml
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no ${REMOTE_USER}@${WAS_HOST_FQDN} "grep webresources ${WAS_HOME}/profiles/${WAS_PROFILE}/config/cells/${WAS_CELL}/LotusConnections-config/LotusConnections-config.xml"

if [ ${DEPLOY_EAR_ONLY} != true ]; then
	echo_info "Updating build ${BUILD_LABEL} on ${WAS_HOST_FQDN}..."
	ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no ${REMOTE_USER}@${WAS_HOST_FQDN} "cd ${REMOTE_LCUPDATE_DIR}; bin/update_build.sh -b ${BUILD_LABEL}"
	[ $? -eq 0 ] || fail_with_error "Unable to update build ${BUILD_LABEL} on ${WAS_HOST_FQDN}."
else
	echo_info "Updating build ${UI_BUILD_LABEL} on ${WAS_HOST_FQDN}..."
	ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no ${REMOTE_USER}@${WAS_HOST_FQDN} "cd ${REMOTE_LCUPDATE_DIR}; bin/update_build.sh -b ${UI_BUILD_LABEL}"
	[ $? -eq 0 ] || fail_with_error "Unable to update build ${UI_BUILD_LABEL} on ${WAS_HOST_FQDN}."

	echo_info "Copying ${CI_COMMON_HOME}/update_ear.py to ${REMOTE_USER}@${WAS_HOST_FQDN}:${REMOTE_CI_HOME}..."
	scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no "${CI_COMMON_HOME}/update_ear.py" ${REMOTE_USER}@${WAS_HOST_FQDN}:${REMOTE_CI_HOME}
	[ $? -eq 0 ] || fail_with_error "Failed to copy ${CI_COMMON_HOME}/update_ear.py to ${REMOTE_USER}@${WAS_HOST_FQDN}:${REMOTE_CI_HOME}."

	CMD="${WAS_HOME}/profiles/${WAS_PROFILE}/bin/wsadmin.sh -lang jython -javaoption -Dpython.path=${REMOTE_LCUPDATE_DIR}/lib -wsadmin_classpath ${REMOTE_LCUPDATE_DIR}/lib/lccfg.jar -host localhost -f ${REMOTE_CI_HOME}/update_ear.py ${APPLICATION_NAME} ${REMOTE_LCUPDATE_DIR}/xkit/installableApps/${EAR_FILENAME} ${WAS_SERVER} ${WAS_NODE} ${WAS_CELL} ${WAS_PROFILE} ${WAS_HOME}"
	echo_info "Executing wsadmin command to update ${EAR_FILENAME} on ${WAS_HOST_FQDN}..."
	ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no ${REMOTE_USER}@${WAS_HOST_FQDN} ${CMD}
	[ $? -eq 0 ] || fail_with_error "Failed to update ${EAR_FILENAME} on ${WAS_HOST_FQDN}."
fi

# For debug, print out webresources line from LotusConnections-config.xml
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no ${REMOTE_USER}@${WAS_HOST_FQDN} "grep webresources ${WAS_HOME}/profiles/${WAS_PROFILE}/config/cells/${WAS_CELL}/LotusConnections-config/LotusConnections-config.xml"

# Get the current build versions for all Connections components.
# Copy the file back to the server so it will be available for each test job.
echo_info "Deleting existing server status file ${SERVER_STATUS_FILENAME} from ${WORKSPACE}..."
rm -fv ${SERVER_STATUS_FILENAME}

echo_info "Getting current server status from ${WAS_HOST_FQDN}..."
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no ${REMOTE_USER}@${WAS_HOST_FQDN} ${WAS_HOME}/profiles/${WAS_PROFILE}/bin/wsadmin.sh -lang jython -f ${REMOTE_LCUPDATE_DIR}/bin/server_status.py | tee ${SERVER_STATUS_FILENAME}
if [ ${PIPESTATUS[0]} != 0 ]; then
	echo_warn "Could not get server status from ${WAS_HOST_FQDN}."
else
	echo_info "Copying ${WORKSPACE}/${SERVER_STATUS_FILENAME} back to ${WAS_HOST_FQDN}..."
	scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no ${SERVER_STATUS_FILENAME} ${REMOTE_USER}@${WAS_HOST_FQDN}:${REMOTE_CI_HOME}
	[ $? -eq 0 ] || echo_error "Could not copy ${WORKSPACE}/${SERVER_STATUS_FILENAME} back to ${WAS_HOST_FQDN}:${REMOTE_CI_HOME}."
fi

# Deploy test bundles for JS UTs.
if [ ${DEPLOY_JS_TEST_BUNDLES} == true ]; then
	${CI_COMMON_HOME}/config_server_for_js_ut.sh 
	[ $? -eq 0 ] || fail_with_error "Failure while copying JS UT bundles to ${WAS_HOST_FQDN}."
fi

# Sanity check the Connections apps.
echo_info "Sanity checking Connection apps post-deploy..."
${CI_COMMON_HOME}/check_a_was.sh ${WAS_HOST_FQDN}
if [ $? != 0 ]; then
	${CI_COMMON_HOME}/gen_workitem_params.sh
	fail_with_error "One or more Connection apps are not running on ${WAS_HOST_FQDN}."
fi

# Dump the run-time code coverage data so as to reset the data to zero in prep of running the BVT suites.
if [ "${ENABLE_CODE_COVERAGE}" == "true" -a -n "${APP_LIST_FOR_CODE_COVERAGE}" ]; then
	echo_info "ENABLE_CODE_COVERAGE is set to \"true\", resetting coverage data..."
	echo_info "Dumping code coverage data from server in order to reset it to zero..."
	export WAS_HOST_FQDN=${WAS_HOST_FQDN}
	${ANT_EXE} -v -lib ${JACOCO_JAR_DIR} -f ${JACOCO_BUILDFILE_DIR}/build_jacoco.xml dump
	[ $? -eq 0 ] || fail_with_error "Unable to dump code coverage data from ${WAS_HOST_FQDN}."
fi

# Script has passed, so delete the workitem properties file
echo_info "Removing workitem.properties..."
rm -fv workitem.properties

exit 0
