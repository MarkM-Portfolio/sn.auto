#!/bin/sh

fail_with_error() {
    echo "$@"
    exit 1
}

[ -n "${BUILD_LABEL}" ] || fail_with_error "\${BUILD_LABEL} is null."

# Get jenkins job parameters from the workitem.properties file.
# Need to replace each space with a %20 for the curl command to work.
echo "Editing workitem.properties..."
sed -ie "s/ /%20/g" workitem.properties

echo "Loading workitem.properties..."
. ./workitem.properties
[ $? == 0 ] || fail_with_error "Could not load workitems.properties."

echo "tag: ${tag}"
echo "category: ${category}"        
echo "subscribers: ${subscribers}"
echo "query: ${query}"

JENKINS_UID=C-CF8L
JENKINS_PASSWORD=`cat /local/ci/common/.icci`
JENKINS_URL=jenkinspoc.cnx.cwp.pnp-hcl.com/jenkins
JENKINS_TOKEN=YWN0aXZpdG11cwo
JENKINS_JOB="Work%20Item%20Creator"
#JENKINS_JOB="DJM%20WIC%20JOB"

echo "Triggering Work Item Creator job via curl..."
curl --fail http://${JENKINS_UID}:${JENKINS_PASSWORD}@${JENKINS_URL}/job/${JENKINS_JOB}/buildWithParameters?token=${JENKINS_TOKEN}\&BUILD_LABEL=${BUILD_LABEL}\&tags="${tags}"\&category="${category}"\&subscribers="${subscribers}"\&query="${query}"
[ $? == 0 ] || fail_with_error "Curl returned an error."
