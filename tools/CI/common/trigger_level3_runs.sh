#!/bin/sh

JENKINS_UID=C-CF8L
JENKINS_PASSWORD=`cat /local/ci/common/.icci`
JENKINS_URL=http://jenkinspoc.cnx.cwp.pnp-hcl.com/jenkins
JENKINS_TOKEN=YWN0aXZpdG11cwo
JENKINS_JOB=IC10.0_Level3
JENKINS_JOB_PARAMS="{\"parameter\": [{\"name\": \"ENABLE_CODE_COVERAGE\", \"value\": \"true\"}], \"\": \"\"}"

curl --fail -X POST $JENKINS_URL/job/${JENKINS_JOB}/build \
     --user "${JENKINS_UID}:${JENKINS_PASSWORD}" \
     -d token=${JENKINS_TOKEN} --data-urlencode json="${JENKINS_JOB_PARAMS}"
