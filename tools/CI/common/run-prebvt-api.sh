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

[ -n "${WAS_HOST_FQDN}" ] || fail_with_error "\${WAS_HOST_FQDN} is null."

WAS_HOST=`echo ${WAS_HOST_FQDN} | cut -d '.' -f 1`

COMPONENT=$1

echo_info "COMPONENT: ${COMPONENT}"

. ${CI_COMMON_HOME}/system.properties || fail_with_error "Failed to load ${CI_COMMON_HOME}/system.properties."

# Download and unzip the latest Automation fe.zip
[ -d sn.auto ] && ( echo_info "Deleting source tree: `pwd`/sn.auto..."; rm -rf sn.auto )
${LCUPDATE_DIR}/bin/get-build.sh -f -B ${BUILD_STREAM}_Automation || fail_with_error "Could not download or unzip the Automation fe.zip"

cd "${CI_BVT_DIST_DIR}"

# Create a directory to extract the BVT API jar so we can get at the properties files.
echo_info "Creating directory extracted_bvt_api_jar..."
mkdir -p extracted_bvt_api_jar || fail_with_error "Could not create directory extracted_bvt_api_jar."

echo_info "Unzipping bvt.api.jar..."
unzip -o -q -d extracted_bvt_api_jar bvt.api.jar ||	fail_with_error "Could not unzip bvt.api.jar."

# Copy profiles property file into place.
cp -v extracted_bvt_api_jar/resources/ProfileData_lc45linux1.properties resources/ProfileData_${WAS_HOST}.properties ||	fail_with_error "Could not copy extracted_bvt_api_jar/resources/ProfileData_lc45linux1.properties to resources/ProfileData_${WAS_HOST}.properties."

# Need to replace the wasadmin pwd entry with the wasadmin pwd for the server under test.
WAS_ADMIN_PWD=$(ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no ${REMOTE_USER}@${WAS_HOST_FQDN} "echo \${WAS_HOST_PWD}")
echo_info "Setting connectionsAdmin.userpassword to ${WAS_ADMIN_PWD}..."
sed -i -e "s]connectionsAdmin.userpassword=bvtsecret]connectionsAdmin.userpassword=${WAS_ADMIN_PWD}]" resources/ProfileData_${WAS_HOST}.properties || fail_with_error "Could not edit resources/ProfileData_${WAS_HOST}.properties."

# Create a workitem properties file
${CI_COMMON_HOME}/gen_workitem_params.sh no ${COMPONENT}

# Run the tests.
echo_info "Running pre-bvt (API) tests..."
${JAVA_HOME}/bin/java -jar bvt.api.jar -server https://${WAS_HOST_FQDN} -components ${COMPONENT} 2>&1 | tee api-bvt.log

# Capture the WAS login credentials and SystemOut logs.
${CI_COMMON_HOME}/get-was-info.sh

exit 0
