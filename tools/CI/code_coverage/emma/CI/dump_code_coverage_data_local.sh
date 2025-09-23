#!/bin/bash

usage() {
    prog_name=`basename ${MYSELF}`
    cat <<EOF

Usage:
  $prog_name <full pathname to ci properties file>

EOF
    exit 1
}

MYSELF=`readlink -f $0`

if [[ -z "$1" ]]; then
    echo "ERROR:"
    echo "  You need to specify a ci properties file"
    usage
fi

PROPERTIES_FILE=$1

if [[ ! -f "${PROPERTIES_FILE}" ]]; then
    echo "ERROR - unable to find properties file ${PROPERTIES_FILE}."
    exit 1
fi

. "${PROPERTIES_FILE}"
if [[ $? -ne 0 ]]; then
    echo "ERROR - failed to load configuration file ${PROPERTIES_FILE}."
    exit 1
fi

if [ -z "${BUILD_LABEL}" ]; then
	echo "EMMA: ERROR - BUILD_LABEL not defined."
	exit 1
fi

if [ -z "${BUILD_STREAM}" ]; then
	echo "EMMA: ERROR - BUILD_STREAM not defined."
	exit 1
fi

if [ -z "${BUILD_COMPONENT}" ]; then
	echo "EMMA: ERROR - BUILD_COMPONENT not defined."
	exit 1
fi
		
echo "EMMA: BUILD_LABEL: ${BUILD_LABEL}"
echo "EMMA: BUILD_STREAM: ${BUILD_STREAM}"
echo "EMMA: BUILD_COMPONENT: ${BUILD_COMPONENT}"

# Get all instances of local code coverage *.ec files.
echo 'EMMA: Listing all instances of local code coverage *.ec files:'
find ${CI_HOME} -name "*.ec" > ${CI_HOME}/runtime_coverage_data_files.txt

while read COVERAGE_DATA_FILE
do
	COVERAGE_DATA_FILE_LIST="${COVERAGE_DATA_FILE_LIST},${COVERAGE_DATA_FILE}"
done < ${CI_HOME}/runtime_coverage_data_files.txt

COVERAGE_DATA_FILES="${WORKSPACE}/coverage.em${COVERAGE_DATA_FILE_LIST}"
echo "Generating XML coverage report ${WORKSPACE}/coverage.xml from ${COVERAGE_DATA_FILES}..."
${JAVA_EXE} -cp ${EMMA_JAR} emma report -r xml -in "${COVERAGE_DATA_FILES}" -Dreport.xml.out.file="${WORKSPACE}/coverage.xml"
if [[ $? -ne 0 ]] ; then
    echo "ERROR - failed to generate XML coverage report."
    exit 1
fi

exit 0
