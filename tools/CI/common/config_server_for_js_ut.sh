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

. ./server-info.properties || fail_with_error "Failed to load server-info.properties."

. ${CI_COMMON_HOME}/system.properties || fail_with_error "Failed to load ${CI_COMMON_HOME}/system.properties."

UPDATE_INFRA_BEFORE_CONFIG=no
index=1
case ${BUILD_COMPONENT} in

	Blogs)
		FE=sn.blogs
		
		# Test bundles
		JAR[${index}]=src/${FE}/lwp/build/blogs.web.resources/eclipse/plugins/com.ibm.lconn.blogs.web.resources.test_*.jar; index=`expr ${index} + 1`
		;;

	Contacts)
		FE=contacts
		
		# Test bundles
		JAR[${index}]=src/${FE}/lwp/build/contacts.web.resources/eclipse/plugins/com.ibm.ic.contacts.web.resources.test_*.jar; index=`expr ${index} + 1`
		;;
	
	Communities)
		FE=sn.comm

		# Test bundles
		JAR[${index}]=src/${FE}/lwp/build/comm.web.resources/eclipse/plugins/com.ibm.lconn.communities.web.resources.test_*.jar; index=`expr ${index} + 1`
		;;

	Forums)
		FE=sn.forum

		# Test bundles
		JAR[${index}]=src/${FE}/lwp/build/forum.web.resources/eclipse/plugins/com.ibm.lconn.forums.web.resources.test_*.jar; index=`expr ${index} + 1`
		;;

	Profiles)
		FE=sn.profiles
		
		# Test bundles
		JAR[${index}]=src/${FE}/lwp/build/profiles.web.resources.test/eclipse/plugins/com.ibm.lconn.profiles.web.resources.test_*.jar; index=`expr ${index} + 1`
		;;

	Share)
		FE=share
		
		# Test bundles
		JAR[${index}]=src/${FE}/lwp/build/files.web.resources/eclipse/plugins/com.ibm.ic.files.web.resources.test_*.jar; index=`expr ${index} + 1`
		JAR[${index}]=src/${FE}/lwp/build/wikis.web.resources/eclipse/plugins/com.ibm.lconn.wikis.web.resources.test_*.jar; index=`expr ${index} + 1`
		;;

	UI)
		FE=sn.infra.ui
		
		# Prereq bundles
		JAR[${index}]=src/${FE}/lwp/prereqs.web.resources/eclipse/plugins/org.junit_4.8.1.*; index=`expr ${index} + 1`
		JAR[${index}]=src/${FE}/lwp/prereqs.web.resources/eclipse/plugins/org.hamcrest.core_*.jar; index=`expr ${index} + 1`
		JAR[${index}]=src/${FE}/lwp/prereqs.web.resources/eclipse/plugins/org.mozilla.javascript_*.jar; index=`expr ${index} + 1`
		JAR[${index}]=src/${FE}/lwp/build/lc.web.resources.base/eclipse/plugins/net.jazz.ajax_*.jar; index=`expr ${index} + 1`
		JAR[${index}]=src/${FE}/lwp/build/lc.web.resources.base/eclipse/plugins/net.jazz.ajax.tests_*.jar; index=`expr ${index} + 1`
		JAR[${index}]=src/${FE}/lwp/build/dojo.web.resources/eclipse/plugins/org.dojotoolkit.d*.jar; index=`expr ${index} + 1`
		
		# Test bundles
		JAR[${index}]=src/${FE}/lwp/build/ic.web.resources/eclipse/plugins/com.ibm.ic.web.resources.test_*.jar; index=`expr ${index} + 1`
		JAR[${index}]=src/${FE}/lwp/build/ic.web.resources/eclipse/plugins/com.ibm.ic.share.web.resources.test_*.jar; index=`expr ${index} + 1`
		JAR[${index}]=src/${FE}/lwp/build/lc.web.resources/eclipse/plugins/com.ibm.lconn.core.styles.test_*.jar; index=`expr ${index} + 1`
		JAR[${index}]=src/${FE}/lwp/build/lc.web.resources/eclipse/plugins/com.ibm.lconn.web.resources.test_*.jar; index=`expr ${index} + 1`
		JAR[${index}]=src/${FE}/lwp/build/lc.web.resources/eclipse/plugins/com.ibm.lconn.share.web.resources.test_*.jar; index=`expr ${index} + 1`
		JAR[${index}]=src/${FE}/lwp/build/oneui.web.resources/eclipse/plugins/com.ibm.oneui.web.resources.test_*.jar; index=`expr ${index} + 1`
		JAR[${index}]=src/${FE}/lwp/build/social.web.resources/eclipse/plugins/com.ibm.social.test.web.resources_*.jar; index=`expr ${index} + 1`		
		JAR[${index}]=src/${FE}/lwp/build/ic.web.resources/eclipse/plugins/com.ibm.ic.gadget.web.resources.test_*.jar; index=`expr ${index} + 1`
		JAR[${index}]=src/${FE}/lwp/build/ic.web.resources/eclipse/plugins/com.ibm.ic.highway.web.resources.test_*.jar; index=`expr ${index} + 1`
		JAR[${index}]=src/${FE}/lwp/build/ic.web.resources/eclipse/plugins/com.ibm.ic.mm.web.resources.test_*.jar; index=`expr ${index} + 1`
		JAR[${index}]=src/${FE}/lwp/build/oauth.web.resources/eclipse/plugins/com.ibm.ic.oauth.web.resources.test_*.jar; index=`expr ${index} + 1`
		JAR[${index}]=src/${FE}/lwp/build/social.web.resources/eclipse/plugins/com.ibm.ic.social.web.resources.test_*.jar; index=`expr ${index} + 1`
		JAR[${index}]=src/${FE}/lwp/build/lc.web.resources/eclipse/plugins/com.ibm.lconn.search.web.resources.test_*.jar; index=`expr ${index} + 1`
		JAR[${index}]=src/${FE}/lwp/build/ic.web.resources/eclipse/plugins/com.ibm.ic.search.web.resources.test_*.jar; index=`expr ${index} + 1`
		
		# Core AMD bundles
		JAR[${index}]=src/${FE}/lwp/build/ic.web.resources/eclipse/plugins/com.ibm.ic.core.web.resources_*.jar; index=`expr ${index} + 1`
		JAR[${index}]=src/${FE}/lwp/build/ic.web.resources/eclipse/plugins/com.ibm.ic.ui.web.resources_*.jar; index=`expr ${index} + 1`
		JAR[${index}]=src/${FE}/lwp/build/ic.web.resources/eclipse/plugins/com.ibm.ic.share.web.resources_*.jar; index=`expr ${index} + 1`		
		JAR[${index}]=src/${FE}/lwp/build/ic.web.resources/eclipse/plugins/com.ibm.ic.gadget.web.resources_*.jar; index=`expr ${index} + 1`
		JAR[${index}]=src/${FE}/lwp/build/ic.web.resources/eclipse/plugins/com.ibm.ic.highway.web.resources_*.jar; index=`expr ${index} + 1`
		JAR[${index}]=src/${FE}/lwp/build/social.web.resources/eclipse/plugins/com.ibm.ic.mail.web.resources_*.jar; index=`expr ${index} + 1`
		JAR[${index}]=src/${FE}/lwp/build/ic.web.resources/eclipse/plugins/com.ibm.ic.mm.web.resources_*.jar; index=`expr ${index} + 1`
		JAR[${index}]=src/${FE}/lwp/build/oauth.web.resources/eclipse/plugins/com.ibm.ic.oauth.web.resources_*.jar; index=`expr ${index} + 1`
		JAR[${index}]=src/${FE}/lwp/build/ic.web.resources/eclipse/plugins/com.ibm.ic.personcard.web.resources_*.jar; index=`expr ${index} + 1`
		JAR[${index}]=src/${FE}/lwp/build/ic.web.resources/eclipse/plugins/com.ibm.ic.search.web.resources_*.jar; index=`expr ${index} + 1`
	
		# Core legacy bundles
		JAR[${index}]=src/${FE}/lwp/build/lc.web.resources/eclipse/plugins/com.ibm.lconn.core.styles_*.jar; index=`expr ${index} + 1`
		JAR[${index}]=src/${FE}/lwp/build/lc.web.resources/eclipse/plugins/com.ibm.lconn.core.web.resources_*.jar; index=`expr ${index} + 1`
		JAR[${index}]=src/${FE}/lwp/build/lc.web.resources/eclipse/plugins/com.ibm.lconn.share.web.resources_*.jar; index=`expr ${index} + 1`
		JAR[${index}]=src/${FE}/lwp/build/oneui.web.resources/eclipse/plugins/com.ibm.oneui.web.resources_*.jar; index=`expr ${index} + 1`
		JAR[${index}]=src/${FE}/lwp/build/lc.web.resources/eclipse/plugins/com.ibm.lconn.search.web.resources_*.jar; index=`expr ${index} + 1`
		;;

	WidgetsClib)
		FE=sn.widgets.clib
		UPDATE_INFRA_BEFORE_CONFIG=yes
		
		# Product jars
		JAR[${index}]=src/${FE}/lwp/build/librarywidget.web.resources/eclipse/plugins/com.ibm.lconn.ecmpicker.web.resources_*.jar; index=`expr ${index} + 1`
		JAR[${index}]=src/${FE}/lwp/build/librarywidget.web.resources/eclipse/plugins/com.ibm.lconn.gallery.config_*.jar; index=`expr ${index} + 1`
		JAR[${index}]=src/${FE}/lwp/build/librarywidget.web.resources/eclipse/plugins/com.ibm.lconn.gallery.web.resources_*.jar; index=`expr ${index} + 1`
		JAR[${index}]=src/${FE}/lwp/build/librarywidget.web.resources/eclipse/plugins/com.ibm.ic.gallery.config_*.jar; index=`expr ${index} + 1`
		JAR[${index}]=src/${FE}/lwp/build/librarywidget.web.resources/eclipse/plugins/com.ibm.ic.gallery.web.resources_*.jar; index=`expr ${index} + 1`
		JAR[${index}]=src/${FE}/lwp/build/librarywidget.web.resources/eclipse/plugins/com.ibm.lconn.librarywidget.web.resources_*.jar; index=`expr ${index} + 1`
		JAR[${index}]=src/${FE}/lwp/build/librarywidget.web.resources/eclipse/plugins/com.ibm.lconn.librarywidget.config_*.jar; index=`expr ${index} + 1`

		# Test bundles
		JAR[${index}]=src/${FE}/lwp/build/repackage_infra_tests/eclipse/plugins/com.ibm.lconn.web.resources.test_*.jar; index=`expr ${index} + 1`
		JAR[${index}]=src/${FE}/lwp/build/librarywidget.web.resources.tests/eclipse/plugins/com.ibm.lconn.librarywidget.tests.web.resources_*.jar; index=`expr ${index} + 1`
		JAR[${index}]=src/${FE}/lwp/build/librarywidget.web.resources.tests/eclipse/plugins/com.ibm.lconn.gallery.tests.web.resources_*.jar; index=`expr ${index} + 1`
		JAR[${index}]=src/${FE}/lwp/build/librarywidget.web.resources.tests/eclipse/plugins/com.ibm.ic.gallery.tests.web.resources_*.jar; index=`expr ${index} + 1`
		JAR[${index}]=src/${FE}/lwp/build/repackage_infra_tests/eclipse/plugins/org.mozilla.javascript_*.jar; index=`expr ${index} + 1`
		JAR[${index}]=src/${FE}/lwp/build/repackage_infra_tests/eclipse/plugins/net.jazz.ajax.tests_*.jar; index=`expr ${index} + 1`
		JAR[${index}]=src/${FE}/lwp/build/repackage_infra_tests/eclipse/plugins/org.junit_4.8.1.*; index=`expr ${index} + 1`
		JAR[${index}]=src/${FE}/lwp/build/repackage_infra_tests/eclipse/plugins/org.hamcrest.core_1.1.0.v20090501071000.jar; index=`expr ${index} + 1`
		;;

	*)	echo_info "No bundles to copy for component: ${BUILD_COMPONENT}"
		exit 0
		;;
esac

if [ "${UPDATE_INFRA_BEFORE_CONFIG}" == "yes" ]; then
	# Get and update the latest "promoted" Infra and UI builds.
	INFRA_BUILD_LABEL=`cat ${DAILY_BUILD_ROOT_DIR}/${BUILD_STREAM}_Infra/BVTBuildLabel.txt`
	UI_BUILD_LABEL=`cat ${DAILY_BUILD_ROOT_DIR}/${BUILD_STREAM}_UI/BVTBuildLabel.txt`

	echo_info "Updating lc-update scripts  on ${WAS_HOST_FQDN}..."
	ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no ${REMOTE_USER}@${WAS_HOST_FQDN} "cd ${REMOTE_LCUPDATE_DIR}; git pull"
	[ $? -eq 0 ] || fail_with_error "Unable to update lc-update scripts on ${WAS_HOST_FQDN}."

	echo_info "Downloading build [${INFRA_BUILD_LABEL}] to ${WAS_HOST_FQDN}..."
	ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no ${REMOTE_USER}@${WAS_HOST_FQDN} "cd ${REMOTE_LCUPDATE_DIR}; bin/get-build.sh -f -x -b ${INFRA_BUILD_LABEL}"
	[ $? -eq 0 ] || fail_with_error "Failed download build [${INFRA_BUILD_LABEL}] to ${WAS_HOST_FQDN}."

	echo_info "Downloading build [${UI_BUILD_LABEL}] to ${WAS_HOST_FQDN}..."
	ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no ${REMOTE_USER}@${WAS_HOST_FQDN} "cd ${REMOTE_LCUPDATE_DIR}; bin/get-build.sh -f -x -b ${UI_BUILD_LABEL}"
	[ $? -eq 0 ] || fail_with_error "Failed download build [${UI_BUILD_LABEL}] to ${WAS_HOST_FQDN}."

	echo_info "Updating build ${INFRA_BUILD_LABEL} on ${WAS_HOST_FQDN}..."
	ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no ${REMOTE_USER}@${WAS_HOST_FQDN} "cd ${REMOTE_LCUPDATE_DIR}; bin/update_build.sh -d -b ${INFRA_BUILD_LABEL}"
	[ $? -eq 0 ] || fail_with_error "Unable to update build ${INFRA_BUILD_LABEL} on ${WAS_HOST_FQDN}."
fi

# Get and unzip the fe.zip file.
[ -d src ] && ( echo_info "Deleting source tree: `pwd`/src..."; rm -rf src )
${LCUPDATE_DIR}/bin/get-build.sh -f -s src -b ${BUILD_LABEL} || fail_with_error "Failed to download or unzip fe.zip."

# Copy the test jars to the server
DST_DIR=${CONNECTIONS_SHARED_DATA_DIR}/provision/webresources

NUM_JARS=${#JAR[@]}
for i in `seq 1 ${NUM_JARS}`
do
	echo_info "Deleting ${JAR[i]} from ${REMOTE_USER}@${WAS_HOST_FQDN}:${DST_DIR}..."
	ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no ${REMOTE_USER}@${WAS_HOST_FQDN} "rm -rf ${DST_DIR}/${JAR[i]}"
	if [ $? != 0 ]; then
		fail_with_error "Could not remove ${JAR[i]} from ${REMOTE_USER}@${WAS_HOST_FQDN}:${DST_DIR}."
	fi

	echo_info "Copying ${JAR[i]} to ${REMOTE_USER}@${WAS_HOST_FQDN}:${DST_DIR}..."
	scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no -r ${JAR[i]} ${REMOTE_USER}@${WAS_HOST_FQDN}:${DST_DIR}
	if [ $? != 0 ]; then
		fail_with_error "Could not copy ${JAR[i]} to ${REMOTE_USER}@${WAS_HOST_FQDN}:${DST_DIR}."
	fi
done

# Create a directory on the remote WAS to copy files.
echo_info "Creating directory \"${REMOTE_CI_HOME}\" on \"${WAS_HOST_FQDN}\"..."
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no ${REMOTE_USER}@${WAS_HOST_FQDN} mkdir -p ${REMOTE_CI_HOME}
if [ $? != 0 ]; then
	fail_with_error "Could not create directory \"${REMOTE_CI_HOME}\" on \"${WAS_HOST_FQDN}\"."
fi

# Copy the app restart script to the server
echo_info "Copying ${CI_COMMON_HOME}/restart-app.py to ${REMOTE_CI_HOME} on ${WAS_HOST_FQDN}..."
scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no ${CI_COMMON_HOME}/restart-app.py ${REMOTE_USER}@${WAS_HOST_FQDN}:${REMOTE_CI_HOME}
if [ $? != 0 ]; then
	fail_with_error "Could not copy ${CI_COMMON_HOME}/restart-app.py to ${REMOTE_CI_HOME} on ${WAS_HOST_FQDN}."
fi

# Remove the Common temp directory on the server.
echo_info "Removing ${WAS_HOME}/profiles/${WAS_PROFILE}/temp/${WAS_NODE}/${WAS_SERVER}/Common on ${WAS_HOST_FQDN}..."
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no ${REMOTE_USER}@${WAS_HOST_FQDN} rm -rf ${WAS_HOME}/profiles/${WAS_PROFILE}/temp/${WAS_NODE}/${WAS_SERVER}/Common
if [ $? != 0 ]; then
	fail_with_error "Could not remove ${WAS_HOME}/profiles/${WAS_PROFILE}/temp/${WAS_NODE}/${WAS_SERVER}/Common."
fi

# Restart Common
echo_info "Restarting Common on ${WAS_HOST_FQDN}..."
CMD="${WAS_HOME}/profiles/${WAS_PROFILE}/bin/wsadmin.sh -lang jython -javaoption -Dpython.path=${REMOTE_LCUPDATE_DIR}/lib -wsadmin_classpath ${REMOTE_LCUPDATE_DIR}/lib/lccfg.jar -host localhost -f ${REMOTE_CI_HOME}/restart-app.py Common"
echo_info "Executing wsadmin on ${WAS_HOST_FQDN}..."
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no ${REMOTE_USER}@${WAS_HOST_FQDN} ${CMD}
if [ $? != 0 ]; then
	fail_with_error "Failed to execute wsadmin command."
fi

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
	[ $? -eq 0 ] || echo_warn "Could not copy ${WORKSPACE}/${SERVER_STATUS_FILENAME} back to ${WAS_HOST_FQDN}:${REMOTE_CI_HOME}."
fi

# Script has passed, so delete the workitem properties file
echo_info "Removing workitem.properties..."
rm -fv workitem.properties

exit 0
