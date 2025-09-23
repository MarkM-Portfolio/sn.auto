#!/bin/sh

if [ -z "${WAS_HOST_ID}" ]; then
	echo "\${WAS_HOST_ID} is null, so there is no server to return to the Server Pool."
	exit 0
fi

. ${CI_COMMON_HOME}/was_reservation_utils.sh
if [ $? != 0 ]; then
	echo "Failed to load ${CI_COMMON_HOME}/was_reservation_utils.sh."
	exit 1
fi

echo "Returning server ${WAS_HOST_FQDN} with ID ${WAS_HOST_ID} to the Server Pool..."
return_a_was
if [ $? != 0 ]; then
	echo "Failed to return server ${WAS_HOST_FQDN} with ID ${WAS_HOST_ID} to the Server Pool."
	exit 1
fi

exit 0
