#!/bin/sh

. ${CI_COMMON_HOME}/init_build_env.sh
if [ $? != 0 ]; then
	echo "Failed to load ${CI_COMMON_HOME}/init_build_env.sh."
	exit 1
fi
	
# Get and unzip the Metrics fe.zip file.
. ${CI_COMMON_HOME}/get-fe-zip.sh sn.metrics
if [ $? != 0 ]; then
	echo "Failed to load ${CI_COMMON_HOME}/get-fe-zip.sh."
	exit 1
fi

# Dowmload the FEs.
echo "Downloading FEs..."
cd ${LOCAL_DEST_DIR}/lwp
wsbld downloadFEs
if [ $? != 0 ]; then
	echo "Failed to download FEs."
	exit 1
fi

# Edit the test war build file to set the server url.
cd ${LOCAL_DEST_DIR}/lwp/lc.metrics.test.war
echo "Editing ${LOCAL_DEST_DIR}/lwp/lc.metrics.test.war/build.xml..."
sed -i -e "s]connciwas.swg.usma.ibm.com:9080]${WAS_HOST_FQDN}:${WAS_PORT_NORMAL}]g" build.xml

# Rebuild the test ear.
cd ${LOCAL_DEST_DIR}/lwp/lc.metrics.test.ear
echo "rebuilding test ear..."
wsbld
if [ $? != 0 ]; then
	echo "Failed to rebuild test ear."
	exit 1
fi
