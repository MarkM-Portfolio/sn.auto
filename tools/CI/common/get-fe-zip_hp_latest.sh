#!/bin/sh

FE_NAME="sn.homepage"
SRC=${SRC:-src}

fail_with_error() {
    echo "$@"
    exit 1
}

echo "Loading ${CI_COMMON_HOME}/ci_functions.sh..."
. ${CI_COMMON_HOME}/ci_functions.sh || fail_with_error "Failed to load ${CI_COMMON_HOME}/ci_functions.sh."

echo "Loading ${CI_COMMON_HOME}/system.properties..."
. ${CI_COMMON_HOME}/system.properties || fail_with_error "Failed to load ${CI_COMMON_HOME}/system.properties."

# Now find and unzip the latest "promoted" Homepage fe.zip.
HP_BUILD_LABEL=`cat ${DAILY_BUILD_ROOT_DIR}/${BUILD_STREAM}_Homepage/BVTBuildLabel.txt`
HP_DAILY_BUILD_ROOT_DIR=/net/mlsa2/ibm/releng/workplace/dailybuilds/${BUILD_STREAM}_Homepage
FE_ZIP_FILE="${HP_DAILY_BUILD_ROOT_DIR}/${HP_BUILD_LABEL}/repository/${FE_NAME}/fe.zip"

LOCAL_DEST_DIR="${WORKSPACE}/${SRC}/${FE_NAME}"
echo "Deleting ${LOCAL_DEST_DIR}..."
rm -rf "${LOCAL_DEST_DIR}"
	
[ -d "${LOCAL_DEST_DIR}" ] || (mkdir -pv "${LOCAL_DEST_DIR}" || fail_with_error "Failed to create directory: ${LOCAL_DEST_DIR}.")

echo "Unzipping ${FE_ZIP_FILE} to ${LOCAL_DEST_DIR}..."
unzip -o -q -d "${LOCAL_DEST_DIR}" "${FE_ZIP_FILE}" || fail_with_error "Failed to unzip ${FE_ZIP_FILE}."

exit 0
