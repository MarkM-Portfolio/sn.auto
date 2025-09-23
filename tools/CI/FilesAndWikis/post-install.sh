#!/bin/sh

JAVA=/local/opt/IBM/WebSphere/AppServer/java/bin/java
LCC_DIR=$1
LCC_XML="${LCC_DIR}/LotusConnections-config.xml"
WAS_HOST_FQDN=connemmawas.swg.usma.ibm.com
WAS_HOST_PORT_NORMAL=9080
WAS_HOST_PORT_SECURE=9443
DIRECTORY_SERVICES_XML_FILE=/local/home/lcuser/lc-update/directory.services.xml
EVENTS_CONFIG_XML_FILE=/local/home/lcuser/lc-update/events-config.xml
FILES_URL_CONFIG_XML_FILE=/local/home/lcuser/lc-update/files-url-config.xml

echo "Adding properties to ${LCC_DIR}/LotusConnections-config.xml for Files tests..."
SREF="/tns:config/sloc:serviceReference[@serviceName=\"directory\"]"
$JAVA -jar lib/lccfg.jar -t ${LCC_XML} "${SREF}" custom_user_id_attribute uid
$JAVA -jar lib/lccfg.jar -t ${LCC_XML} "${SREF}" custom_group_id_attribute cn

echo "Adding port numbers to ${LCC_DIR}/LotusConnections-config.xml for Files tests..."
sed -i -e "s]http://${WAS_HOST_FQDN}]http://${WAS_HOST_FQDN}:${WAS_HOST_PORT_NORMAL}]g" ${LCC_XML}
sed -i -e "s]https://${WAS_HOST_FQDN}]https://${WAS_HOST_FQDN}:${WAS_HOST_PORT_SECURE}]g" ${LCC_XML}

echo "Copying custom ${DIRECTORY_SERVICES_XML_FILE} to ${LCC_DIR} for Files tests..."
cp ${DIRECTORY_SERVICES_XML_FILE} ${LCC_DIR}

echo "Copying custom ${EVENTS_CONFIG_XML_FILE} to ${LCC_DIR} for Files tests..."
cp ${EVENTS_CONFIG_XML_FILE} ${LCC_DIR}

echo "Copying custom ${FILES_URL_CONFIG_XML_FILE} to ${LCC_DIR} for Files tests..."
cp ${FILES_URL_CONFIG_XML_FILE} ${LCC_DIR}
