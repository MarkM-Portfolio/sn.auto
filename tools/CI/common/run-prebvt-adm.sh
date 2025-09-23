#!/bin/sh

# Create a workitem properties file
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

. ${CI_COMMON_HOME}/system.properties
[ $? == 0 ] || fail_with_error "Failed to load ${CI_COMMON_HOME}/system.properties."

case ${BUILD_COMPONENT} in

	Communities)
		TEST_TO_RUN[1]=communities
		TEST_TO_RUN[2]=communities_reparent
		;;	

	Infra)
		TEST_TO_RUN[1]=OAuthApplication
		TEST_TO_RUN[2]=scheduler
		;;

	News)
		TEST_TO_RUN[1]=news
		TEST_TO_RUN[2]=NewsOAuth
		;;

	Profiles)
		TEST_TO_RUN[1]=profiles
		;;

	*)	fail_with_error "Unknown component: ${BUILD_COMPONENT}"
		;;
esac

# Until we get the test code directory into a git repository on the pool servers, copy the directory to the server.
echo_info "Copying ${CI_COMMON_HOME}/bvt.adm2 to ${REMOTE_USER_HOME} on ${WAS_HOST_FQDN}..."
scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no -r ${CI_COMMON_HOME}/bvt.adm2 ${REMOTE_USER}@${WAS_HOST_FQDN}:${REMOTE_USER_HOME}
[ $? == 0 ] || fail_with_error "Failed to copy ${CI_COMMON_HOME}/bvt.adm2 to ${REMOTE_USER_HOME} on ${WAS_HOST_FQDN}."

# Run the tests.
echo_info "Updating ${REMOTE_USER_HOME}/bvt.adm2 on ${WAS_HOST_FQDN}..."
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no ${REMOTE_USER}@${WAS_HOST_FQDN} "cd bvt.adm2; git pull"
[ $? == 0 ] || fail_with_error "Failed to update ${REMOTE_USER_HOME}/bvt.adm2 on ${WAS_HOST_FQDN}."

# Create a workitem properties file
${CI_COMMON_HOME}/gen_workitem_params.sh

NUM_TESTS=${#TEST_TO_RUN[@]}
OK=yes
for i in `seq 1 ${NUM_TESTS}`
do
	echo_info "Running pre-bvt (ADM) test script ${TEST_TO_RUN[i]}..."
	ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no ${REMOTE_USER}@${WAS_HOST_FQDN} bash --login "${REMOTE_USER_HOME}/bvt.adm2/runtest.sh -h ${WAS_HOST_FQDN} -W ${WAS_HOME}/profiles/${WAS_PROFILE} ${TEST_TO_RUN[i]}"
	[ $? == 0 ] || OK=no
done

[ "${OK}" == "yes" ] || fail_with_error "One or more test suites failed."

# Capture the WAS login credentials and SystemOut logs.
${CI_COMMON_HOME}/get-was-info.sh

exit 0
