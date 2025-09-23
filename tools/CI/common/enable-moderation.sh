#!/bin/sh

. ./server-info.properties
if [ $? != 0 ]; then
	echo "Failed to load server-info.properties."
	exit 1
fi

. ${CI_COMMON_HOME}/system.properties
if [ $? != 0 ]; then
	echo "Failed to load ${CI_COMMON_HOME}/system.properties."
	exit 1
fi
	
# Create a directory on the remote WAS to copy files.
echo "Creating directory \"${REMOTE_CI_HOME}\" on \"${WAS_HOST_FQDN}\"..."
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no ${REMOTE_USER}@${WAS_HOST_FQDN} mkdir -p ${REMOTE_CI_HOME}
if [ $? != 0 ]; then
	echo "Could not create directory \"${REMOTE_CI_HOME}\" on \"${WAS_HOST_FQDN}\"."
	exit 1
fi

# Copy the moderation enable script to the remote WAS.
echo "Copying ${CI_COMMON_HOME}/enable-moderation.py to ${REMOTE_CI_HOME} on ${WAS_HOST_FQDN}..."
scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no "${CI_COMMON_HOME}/enable-moderation.py" ${REMOTE_USER}@${WAS_HOST_FQDN}:${REMOTE_CI_HOME}
if [ $? != 0 ]; then
	echo "Failed to copy ${CI_COMMON_HOME}/enable-moderation.py to ${REMOTE_CI_HOME} on ${WAS_HOST_FQDN}."
	exit 1
fi

# Execute commands on the remote WAS to enable moderation and restart WAS.
echo "Executing commands on remote WAS to enable Moderation and restart WAS..."
CMD[1]="${WAS_HOME}/profiles/${WAS_PROFILE}/bin/wsadmin.sh -lang jython -javaoption -Dpython.path=${REMOTE_LCUPDATE_DIR}/lib -wsadmin_classpath ${REMOTE_LCUPDATE_DIR}/lib/lccfg.jar -host localhost -f ${REMOTE_CI_HOME}/enable-moderation.py ${WAS_SERVER} ${WAS_NODE} ${WAS_CELL} ${WAS_PROFILE} ${WAS_HOME}"
CMD[2]="${REMOTE_LCUPDATE_DIR}/bin/was stop"
CMD[3]="${REMOTE_LCUPDATE_DIR}/bin/was start"

NUM_CMDS=${#CMD[@]}

for i in `seq 1 $NUM_CMDS`
do
	echo "Executing ${CMD[$i]} on ${WAS_HOST_FQDN}..."
	ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no ${REMOTE_USER}@${WAS_HOST_FQDN} ${CMD[$i]}
	if [ $? != 0 ]; then
		echo "Failed to execute ${CMD[$i]} on ${WAS_HOST_FQDN}."
		exit 1
	fi
done
