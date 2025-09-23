#!/bin/sh

JAVA=${JAVA:-/opt/IBM/WebSphere/AppServer/java/bin/java}
LCC_DIR=$1

echo "Enable moderation in contentreview-config.xml"
$JAVA -jar lib/lccfg.jar -t ${LCC_DIR}/contentreview-config.xml \
        "/config/serviceConfiguration/service[@id=\"blogs\"]/contentApproval" \
		enabled true
$JAVA -jar lib/lccfg.jar -t ${LCC_DIR}/contentreview-config.xml \
        "/config/serviceConfiguration/service[@id=\"blogs\"]/contentApproval/ownerModerate" \
		 enabled true
$JAVA -jar lib/lccfg.jar -t ${LCC_DIR}/contentreview-config.xml \
        "/config/serviceConfiguration/service[@id=\"blogs\"]/contentFlagging" \
		enabled true
$JAVA -jar lib/lccfg.jar -t ${LCC_DIR}/contentreview-config.xml \
        "/config/serviceConfiguration/service[@id=\"blogs\"]/contentFlagging/issueCategorization" \
		 enabled true

$JAVA -jar lib/lccfg.jar -t ${LCC_DIR}/contentreview-config.xml \
        "/config/serviceConfiguration/service[@id=\"files\"]/contentApproval" \
		enabled true
$JAVA -jar lib/lccfg.jar -t ${LCC_DIR}/contentreview-config.xml \
        "/config/serviceConfiguration/service[@id=\"files\"]/contentApproval/ownerModerate" \
		 enabled true
$JAVA -jar lib/lccfg.jar -t ${LCC_DIR}/contentreview-config.xml \
        "/config/serviceConfiguration/service[@id=\"files\"]/contentFlagging" \
		enabled true
$JAVA -jar lib/lccfg.jar -t ${LCC_DIR}/contentreview-config.xml \
        "/config/serviceConfiguration/service[@id=\"files\"]/contentFlagging/ownerModerate" \
		 enabled true

$JAVA -jar lib/lccfg.jar -t ${LCC_DIR}/contentreview-config.xml \
        "/config/serviceConfiguration/service[@id=\"forums\"]/contentApproval" \
		enabled true
$JAVA -jar lib/lccfg.jar -t ${LCC_DIR}/contentreview-config.xml \
        "/config/serviceConfiguration/service[@id=\"forums\"]/contentApproval/ownerModerate" \
		 enabled true
$JAVA -jar lib/lccfg.jar -t ${LCC_DIR}/contentreview-config.xml \
        "/config/serviceConfiguration/service[@id=\"forums\"]/contentFlagging" \
		enabled true
$JAVA -jar lib/lccfg.jar -t ${LCC_DIR}/contentreview-config.xml \
        "/config/serviceConfiguration/service[@id=\"forums\"]/contentFlagging/ownerModerate" \
		 enabled true
$JAVA -jar lib/lccfg.jar -t ${LCC_DIR}/contentreview-config.xml \
        "/config/serviceConfiguration/service[@id=\"forums\"]/contentFlagging/issueCategorization" \
		 enabled true
