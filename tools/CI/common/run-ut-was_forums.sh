#!/bin/sh

# Create a workitem properties file
${CI_COMMON_HOME}/gen_workitem_params.sh assign_to_automation

. ${CI_COMMON_HOME}/init_build_env.sh
if [ $? != 0 ]; then
	echo "Failed to load ${CI_COMMON_HOME}/init_build_env.sh."
	exit 1
fi
	
# Now find and unzip the component's fe.zip.
FE_ZIP_FILE=${COMPONENT_DAILY_BUILD_ROOT_DIR}/${BUILD_LABEL}/repository/sn.forum/fe.zip
LOCAL_DEST_DIR="${WORKSPACE}/src/sn.forum"

echo "Deleting directory ${LOCAL_DEST_DIR}..."
rm -rf "${LOCAL_DEST_DIR}"
	
echo "Creating directory ${LOCAL_DEST_DIR} to unzip ${FE_ZIP_FILE}..."
mkdir -p "${LOCAL_DEST_DIR}"
if [ $? != 0 ]; then
	echo "Failed to create directory: ${LOCAL_DEST_DIR}"
	exit 1
fi

cd "${LOCAL_DEST_DIR}"

echo "Unzipping ${FE_ZIP_FILE}..."
unzip -o -q ${FE_ZIP_FILE}
if [ $? != 0 ]; then
	echo "Failed to unzip ${FE_ZIP_FILE}."
	exit 1
fi

# Dowmload the FEs.
echo "Downloading FEs..."
cd lwp
wsbld downloadFEs
if [ $? != 0 ]; then
	echo "Failed to download FEs."
	exit 1
fi

# Copy ServiceTest.xml into place and edit.
SRC_FILE=${CI_COMMON_HOME}/ServiceTestTemplate_Forums.xml
DST_DIR="${LOCAL_DEST_DIR}/lwp/forum.test/api"
DST_FILE="${DST_DIR}/ServiceTest.xml"

echo "Copying ${SRC_FILE} to ${DST_FILE}..."
cp "${SRC_FILE}" "${DST_FILE}"
if [ $? != 0 ]; then
	echo "Failed to copy ${SRC_FILE} to ${DST_FILE}"
	exit 1
fi

echo "Editing ${DST_FILE}..."
sed -i -e "s]CI_UT_SERVER_HTTPS_URL]https://${WAS_HOST_FQDN}:${WAS_PORT_SECURE}]g" ${DST_FILE}

# Create a workitem properties file
${CI_COMMON_HOME}/gen_workitem_params.sh

# Run the tests.
echo "Running WAS dependent unit tests..."
cd "${DST_DIR}"
wsbld bvt

# Build the JUnit report.
export CI_HOME="${WORKSPACE}"
export APPDIR_PRIMARY=sn.forum/lwp
python ${CI_COMMON_HOME}/filter_junit_report.py ${LOCAL_DEST_DIR}/lwp/build/forum.test/api/logs/TEST-com.ibm.lconn.forum.test.atom2.AtomServiceTest.xml
${CI_COMMON_HOME}/build_junit_report.sh build/forum.test/api/logs

# Capture the WAS login credentials and SystemOut logs.
${CI_COMMON_HOME}/get-was-info.sh
