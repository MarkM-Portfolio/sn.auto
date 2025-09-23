#!/bin/sh

JAVA=/opt/IBM/WebSphere/AppServer/java/bin/java
LCC_DIR=$1
LCC_XML="${LCC_DIR}/LotusConnections-config.xml"
WAS_HOST_FQDN=connciwas.swg.usma.ibm.com
WAS_HOST_PORT_NORMAL=9080
WAS_HOST_PORT_SECURE=9443
#DIRECTORY_SERVICES_XML_FILE=/home/lcuser/lc-update/directory.services.xml

#echo "Adding properties to ${LCC_DIR}/LotusConnections-config.xml for Files tests..."
#SREF="/tns:config/sloc:serviceReference[@serviceName=\"directory\"]"
#$JAVA -jar lib/lccfg.jar -t ${LCC_XML} "${SREF}" custom_user_id_attribute uid
#$JAVA -jar lib/lccfg.jar -t ${LCC_XML} "${SREF}" custom_group_id_attribute cn

#echo "Adding port numbers to ${LCC_DIR}/LotusConnections-config.xml for Files tests..."
#sed -i -e "s]http://${WAS_HOST_FQDN}]http://${WAS_HOST_FQDN}:${WAS_HOST_PORT_NORMAL}]g" ${LCC_XML}
#sed -i -e "s]https://${WAS_HOST_FQDN}]https://${WAS_HOST_FQDN}:${WAS_HOST_PORT_SECURE}]g" ${LCC_XML}

#echo "Copying custom ${DIRECTORY_SERVICES_XML_FILE} to ${LCC_DIR} for Files tests..."
#cp ${DIRECTORY_SERVICES_XML_FILE} ${LCC_DIR}

echo "Enable developer mode in opensocial-config"
$JAVA -jar lib/lccfg.jar -t ${LCC_DIR}/opensocial-config.xml \
        "/tns:config/tns:gadget-settings/tns:developer" \
        enabled true
$JAVA -jar lib/lccfg.jar -t ${LCC_DIR}/opensocial-config.xml \
        "/tns:config/tns:gadget-settings/tns:security" \
        whitelistEnabled false
