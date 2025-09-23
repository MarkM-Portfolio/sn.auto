#!/bin/sh

JAVA=${JAVA:-/opt/IBM/WebSphere/AppServer/java/bin/java}
LCC_DIR=$1

echo "Enable developer mode in opensocial-config"
$JAVA -jar lib/lccfg.jar -t ${LCC_DIR}/opensocial-config.xml \
        "/tns:config/tns:gadget-settings/tns:developer" \
        enabled true
$JAVA -jar lib/lccfg.jar -t ${LCC_DIR}/opensocial-config.xml \
        "/tns:config/tns:gadget-settings/tns:security" \
        whitelistEnabled false
