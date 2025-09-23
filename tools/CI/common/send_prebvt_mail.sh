#!/bin/sh

CI_PREBVT_TYPE=$1
CI_PREBVT_TYPE_UPPER=`echo ${CI_PREBVT_TYPE} | tr a-z A-Z`
CI_PREBVT_RESULT=$2

if [ "${CI_PREBVT_RESULT}" != true ]; then
	echo "${CI_PREBVT_TEST_NAME} ${CI_PREBVT_TYPE_UPPER} pre-bvt failed."
	MAIL_SUBJECT_PHRASE="(ERROR) [s]"
else
	echo "${CI_PREBVT_TEST_NAME} ${CI_PREBVT_TYPE_UPPER} pre-bvt passed."
	MAIL_SUBJECT_PHRASE="(OK)"
fi

# Always send emails to some people, regardless of pass or fail.
CI_PRE_BVT_MAIL_TO=\
"\
icci@us.ibm.com,\
yguo@us.ibm.com,\
cindy_wu@us.ibm.com,\
PELLYCON@ie.ibm.com,\
wangpin@us.ibm.com,\
dmiller6@us.ibm.com,\
${CI_PRE_BVT_MAIL_TO}\
"

# Send mail to subcribers.
MAIL_FILE=${CI_HOME}/pre-bvt/bvt.dist/mail.txt

# The mail recepient list is kept in each component's ci.properties file,
# but if this is a personal build, only want to send mail to the build requester.
if [ "${personalBuild}" == "true" ]; then
	CI_PRE_BVT_MAIL_TO=${buildRequesterUserId}
fi

echo "To: ${CI_PRE_BVT_MAIL_TO}" > ${MAIL_FILE}
echo "Subject: CI pre-bvt build (${CI_PREBVT_TYPE_UPPER}) \"${buildDefinitionId} (${buildLabel})\" has completed ${MAIL_SUBJECT_PHRASE}" >> ${MAIL_FILE}
echo "" >> ${MAIL_FILE}

if [ "${scheduledBuild}" == "true" ]; then
	echo "Requested by: (scheduled build)" >> ${MAIL_FILE}
elif [ "${personalBuild}" == "true" ]; then
	echo "Requested by: (personal build)" >> ${MAIL_FILE}
fi

echo "" >> ${MAIL_FILE}
echo "Number of testcases run: ${NUM_TESTS_RUN_ORIG}" >> ${MAIL_FILE}
echo "Number of testcase failures: ${NUM_FAILURES}" >> ${MAIL_FILE}
echo "Number of testcases skipped: ${NUM_SKIPS}" >> ${MAIL_FILE}
	
if [ "${PREBVT_RAN}" == true ]; then
	echo "See results at: http://${CI_HOST_FQDN}/pre-bvt-ci/${CI_PREBVT_TEST_NAME}/${buildDefinitionId}-${buildLabel}/${CI_PREBVT_TYPE}" >> ${MAIL_FILE}
else
	echo "There were errors in setting up for the prebvt tests." >> ${MAIL_FILE}
fi
	
echo "Sending pre-bvt email to: ${CI_PRE_BVT_MAIL_TO}"
cat ${MAIL_FILE} | /usr/sbin/sendmail -tv
exit 0
