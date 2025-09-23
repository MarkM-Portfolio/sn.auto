
reserve_a_was(){
	SERVER_POOL=$1
	echo "Requesting a server from Server Pool \"${SERVER_POOL}\"..."
	RESERVATION_STRING=`python ${CI_COMMON_HOME}/reserve_a_was.py ${SERVER_POOL}`
	STATUS=$?
	export WAS_HOST_FQDN=`echo ${RESERVATION_STRING} | cut -d ',' -f 1`
	export WAS_HOST_ID=`echo ${RESERVATION_STRING} | cut -d ',' -f 2`
	export WAS_HOST_PWD=`echo ${RESERVATION_STRING} | cut -d ',' -f 3`
	if [ ${STATUS} != 0 -o -z "${WAS_HOST_FQDN}" -o "${WAS_HOST_FQDN}" == "None" ]; then
		echo "Failed to reserve a server from Server Pool \"${SERVER_POOL}\"."
		echo "Reservation call returned status code: ${STATUS}"
		echo "Reservation call returned message: ${RESERVATION_STRING}"
		WAS_HOST_ID=""
		WAS_HOST_FQDN=""
		WAS_HOST_PWD=""
		return 1
	fi

	echo "Successfully reserved server ${WAS_HOST_FQDN} with ID ${WAS_HOST_ID} from Server Pool \"${SERVER_POOL}\"."
	
	export WAS_HOME=/opt/IBM/WebSphere/AppServer
	export WAS_PROFILE=AppSrv01
	export WAS_SERVER=server1
	export WAS_NODE=lc45linux1Node01
	export WAS_CELL=lc45linux1Node01Cell
	export WAS_PORT_NORMAL=9080
	export WAS_PORT_SECURE=9443
	export WAS_ADMIN=wasadmin
	export WAS_ADMIN_PASSWORD=${WAS_HOST_PWD}
	export REMOTE_USER=lcuser
	export REMOTE_USER_HOME=/home/lcuser
	export REMOTE_LCUPDATE_DIR=${REMOTE_USER_HOME}/lc-update
	export REMOTE_JAVA_HOME=${WAS_HOME}/java/jre
	export REMOTE_CI_HOME=${REMOTE_USER_HOME}/ci
	export CONNECTIONS_SHARED_DATA_DIR=/data/Connections
	export SERVER_STATUS_FILENAME=ServerStatus.txt
	export IHS_PORT_SECURE=443

	# The following ssh command is to get by the "accept key" prompt" for new server pool machines that are put on-line.
	ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no ${REMOTE_USER}@${WAS_HOST_FQDN} echo "Accepting key if prompted..."

	return 0
}

return_a_was(){
	echo "Returning a Server Pool reservation..."
	if [ -z "${WAS_HOST_ID}" ]; then
		echo "There was no Server Pool reservation to return."
		return 0
	fi
	
	echo "WAS_HOST_ID: ${WAS_HOST_ID}"

	python ${CI_COMMON_HOME}/return_a_was.py ${WAS_HOST_ID}
	if [ $? != 0 ]; then
		echo "Error occurred trying to return a Server Pool reservation with host ID: ${WAS_HOST_ID}"
		return 1
	fi
	
	WAS_HOST_ID=""
	return 0
}
