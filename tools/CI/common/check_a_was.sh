#!/bin/bash

WAS_HOST_FQDN=$1
CHECK_FOR_CODE_COVERAGE_JAR=${2:-no}

echo -e "Sanity checking Connection apps on server ${WAS_HOST_FQDN}...\n"

ALL_PASSED=true
HEADER_FILE="${WORKSPACE:-.}/header_file.txt"

index=0

APP[${index}]=activities; index=`expr ${index} + 1`
APP[${index}]=blogs; index=`expr ${index} + 1`
APP[${index}]=communities; index=`expr ${index} + 1`
APP[${index}]=dogear; index=`expr ${index} + 1`
APP[${index}]=files; index=`expr ${index} + 1`
APP[${index}]=forums; index=`expr ${index} + 1`
APP[${index}]=homepage; index=`expr ${index} + 1`
APP[${index}]=metrics; index=`expr ${index} + 1`
#APP[${index}]=mobile; index=`expr ${index} + 1`
APP[${index}]=moderation; index=`expr ${index} + 1`
APP[${index}]=news; index=`expr ${index} + 1`
APP[${index}]=profiles; index=`expr ${index} + 1`
APP[${index}]=search; index=`expr ${index} + 1`
APP[${index}]=wikis; index=`expr ${index} + 1`

NUM_APPS=${#APP[*]}

index=-1
while [[ `expr ${index} + 1` -lt "${NUM_APPS}" ]]
do
	index=`expr ${index} + 1`
	rm -f ${HEADER_FILE}
	echo -en "Hitting URL https://${WAS_HOST_FQDN}/${APP[${index}]}... "
	curl -D ${HEADER_FILE} --insecure -sS https://${WAS_HOST_FQDN}/${APP[${index}]}
	if [ $? != 0 ]; then
		echo -e "\nError trying to hit URL https://${WAS_HOST_FQDN}/${APP[${index}]}.\n"
		ALL_PASSED=false
		continue
	fi

	grep -iq "302 Found" ${HEADER_FILE}
	if [ $? != 0 ]; then
		echo -e "\nDid not find URL https://${WAS_HOST_FQDN}/${APP[${index}]}:\n"
		cat ${HEADER_FILE}
		ALL_PASSED=false
		continue
	fi

	echo -e "OK\n"
		
done

if [ ${CHECK_FOR_CODE_COVERAGE_JAR} == check_for_code_coverage_jar ]; then
	JAR=${REMOTE_JAVA_HOME}/lib/ext/jacocoagent.jar
	echo "Checking for ${JAR} on ${WAS_HOST_FQDN}..."
	ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no ${REMOTE_USER}@${WAS_HOST_FQDN} "ls -l ${JAR}"
	if [ $? -ne 0 ] ; then
		echo "[$WAS_HOST_FQDN] does not have ${JAR} installed."
		ALL_PASSED=false
	fi
fi

[ ${ALL_PASSED} == true ] || exit 1

exit 0
