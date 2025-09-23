#!/bin/ksh

usage() {
    prog_name=`basename ${MYSELF}`
    cat <<EOF

Usage:
  $prog_name <full pathname to configuration file> <full pathname to ci properties file>

EOF
    exit 1
}

MYSELF=`readlink -f $0`

if [[ -z "$1" ]]; then
    echo "ERROR:"
    echo "  You need to specify a configuration file"
    usage
fi

if [[ -z "$2" ]]; then
    echo "ERROR:"
    echo "  You need to specify a ci properties file"
    usage
fi

CONFIG_FILE=$1
PROPERTIES_FILE=$2

if [[ ! -f "${CONFIG_FILE}" ]]; then
    print "ERROR - unable to find configuration file ${CONFIG_FILE}."
    exit 1
fi

. "${CONFIG_FILE}"
if [[ $? -ne 0 ]]; then
    print "ERROR - failed to load configuration file ${CONFIG_FILE}."
    exit 1
fi

if [[ ! -f "${PROPERTIES_FILE}" ]]; then
    print "ERROR - unable to find properties file ${PROPERTIES_FILE}."
    exit 1
fi

. "${PROPERTIES_FILE}"
if [[ $? -ne 0 ]]; then
    print "ERROR - failed to load configuration file ${CONFIG_FILE}."
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

# For debugging purposes, list all instances of local code coverage *.ec files.
echo 'EMMA: Listing all instances of local code coverage *.ec files:'
find ${CI_HOME} -name "*.ec"
echo 'EMMA: End of *.ec list'

# Get comma separated list of all local coverage data files.
for i in `seq 1 ${#TEST_DIR[@]}`
do
	COVERAGE_DATA_FILE=$CI_HOME/${SRC}/$APPDIR/${TEST_DIR[$i]}/coverage.ec
	if [ -f ${COVERAGE_DATA_FILE} ]; then
		echo "EMMA: Collecting code coverage data from ${COVERAGE_DATA_FILE}..."
		if [ -n "${COVERAGE_DATA_FILE_LIST}" ]; then
			COVERAGE_DATA_FILE_LIST="${COVERAGE_DATA_FILE_LIST},${COVERAGE_DATA_FILE}"
		else
			COVERAGE_DATA_FILE_LIST="${COVERAGE_DATA_FILE}"
		fi
	else
		echo "EMMA: WARNING - Local coverage data file not found: ${COVERAGE_DATA_FILE}"
		echo "EMMA: This is OK if there were no local jar files instrumented for code coverage."
	fi		
done

for i in `seq 1 ${#POST_TEST_DIR[@]}`
do
	COVERAGE_DATA_FILE=$CI_HOME/${SRC}/$APPDIR/${POST_TEST_DIR[$i]}/coverage.ec
	if [ -f ${COVERAGE_DATA_FILE} ]; then
		echo "EMMA: Collecting code coverage data from ${COVERAGE_DATA_FILE}..."
		if [ -n "${COVERAGE_DATA_FILE_LIST}" ]; then
			COVERAGE_DATA_FILE_LIST="${COVERAGE_DATA_FILE_LIST},${COVERAGE_DATA_FILE}"
		else
			COVERAGE_DATA_FILE_LIST="${COVERAGE_DATA_FILE}"
		fi
	else
		echo "EMMA: WARNING - Local coverage data file not found: ${COVERAGE_DATA_FILE}"
		echo "EMMA: This is OK if there were no local jar files instrumented for code coverage."
	fi		
done
	
# If there were tests that ran against WAS, get the WAS coverage data.
if [ -n "${EAR_FILE}" -a -n "${EAR_APP_NAME}" -a "${INSTRUMENT_THE_EAR}" == true ]; then
	COVERAGE_DATA_FILE=${EMMA_RUNTIMEDATA_DIR}/${APPLICATION_DIRNAME}_was.ec
	echo "EMMA: Dumping code coverage data from ${WAS_HOST}..."
	${JAVA_EXE} -cp ${EMMA_JAR} emma ctl -connect ${WAS_HOST}.`hostname -d`:47653 -command coverage.get,${COVERAGE_DATA_FILE} -Dverbosity.level=verbose
	if [ $? != 0 ]; then
		echo "EMMA: ERROR - Could not get coverage data from ${WAS_HOST}."
		exit 1
	fi
	
	# If there was a WAS coverage data file, add it to the list.
	if [ -f ${COVERAGE_DATA_FILE} ]; then
		echo "EMMA: Collecting code coverage data from ${COVERAGE_DATA_FILE}..."
		if [ -n "${COVERAGE_DATA_FILE_LIST}" ]; then
			COVERAGE_DATA_FILE_LIST="${COVERAGE_DATA_FILE_LIST},${COVERAGE_DATA_FILE}"
		else
			COVERAGE_DATA_FILE_LIST="${COVERAGE_DATA_FILE}"
		fi
	else
		echo "EMMA: ERROR - Coverage data file not found: ${COVERAGE_DATA_FILE}"
		exit 1
	fi
fi
	
echo "EMMA: COVERAGE_DATA_FILE_LIST: ${COVERAGE_DATA_FILE_LIST}"

COVERAGE_HTML_ALL_BUILD_DEF_DIR=${COVERAGE_HTML_ALL_DIR}/${BUILD_STREAM}_${BUILD_COMPONENT}
COVERAGE_XML_ALL_BUILD_DEF_DIR=${COVERAGE_XML_ALL_DIR}/${BUILD_STREAM}_${BUILD_COMPONENT}
		
for DIR in ${COVERAGE_HTML_ALL_BUILD_DEF_DIR} ${COVERAGE_XML_ALL_BUILD_DEF_DIR} ${COVERAGE_HTML_ALL_SUMMARIES_DIR}
do
	if [ ! -d ${DIR} ]; then
		echo "EMMA: Creating directory: ${DIR}"
		mkdir -p ${DIR}
		if [ $? != 0 ]; then
			echo "EMMA: ERROR - Could not create directory: ${DIR}"
			exit 1
		fi
	fi
done
			
COVERAGE_HTML_BUILD_DEF_DIR=${COVERAGE_HTML_ALL_DIR}/${BUILD_STREAM}_${BUILD_COMPONENT}
COVERAGE_XML_BUILD_DEF_DIR=${COVERAGE_XML_ALL_DIR}/${BUILD_STREAM}_${BUILD_COMPONENT}
	
COVERAGE_HTML_COMPONENT_ROOT_DIR=${COVERAGE_HTML_BUILD_DEF_DIR}/${BUILD_LABEL}
COVERAGE_HTML_FILE=${COVERAGE_HTML_COMPONENT_ROOT_DIR}/index.html
COVERAGE_XML_FILE=${COVERAGE_XML_BUILD_DEF_DIR}/${BUILD_LABEL}.xml

mkdir emma
COVERAGE_XML_FILE="`pwd`/emma/coverage.xml"

		
echo "EMMA: Generating HTML (${COVERAGE_HTML_FILE}) and XML (${COVERAGE_XML_FILE}) reports..."
$EMMA_SCRIPT_DIR/emma_generate_reports.sh -i ${EMMA_METADATA_DIR}/${APPLICATION_DIRNAME}.em,${COVERAGE_DATA_FILE_LIST} -h ${COVERAGE_HTML_FILE} -x ${COVERAGE_XML_FILE} ${CONFIG_FILE}
if [ $? != 0 ]; then
	echo "EMMA: ERROR - Failed to generate HTML and/or XML reports."
	exit 1
fi

# Need to copy the results of the current run to the "latest" area.
# First will make sure "latest" area is clean.
COVERAGE_HTML_LATEST_DIR=${EMMA_REPORTS_HTML_DIR}/scheduled-builds/latest
COVERAGE_HTML_LATEST_BUILD_DEF_DIR=${COVERAGE_HTML_LATEST_DIR}/${BUILD_STREAM}_${BUILD_COMPONENT}
		
COVERAGE_XML_LATEST_DIR=${EMMA_REPORTS_XML_DIR}/scheduled-builds/latest
COVERAGE_XML_LATEST_BUILD_DEF_DIR=${COVERAGE_XML_LATEST_DIR}/${BUILD_STREAM}_${BUILD_COMPONENT}

# Clean up the "latest" dirs.
for DIR in ${COVERAGE_HTML_LATEST_BUILD_DEF_DIR} ${COVERAGE_XML_LATEST_BUILD_DEF_DIR}
do
	if [ -d "${DIR}" ]; then				
		echo "EMMA: Deleting directory: ${DIR}"
		rm -rf ${DIR}
		if [ $? != 0 ]; then
			echo "EMMA: ERROR - Could not delete directory: ${DIR}"
			exit 1
		fi
	fi
			
	echo "EMMA: Creating directory: ${DIR}"
	mkdir -p ${DIR}
	if [ $? != 0 ]; then
		echo "EMMA: ERROR - Could not create directory: ${DIR}"
		exit 1
	fi
done
	
# Now copy the current reports to the "latest" dir.
echo "EMMA: Copying ${COVERAGE_HTML_COMPONENT_ROOT_DIR} to ${COVERAGE_HTML_LATEST_BUILD_DEF_DIR}..."
cp -R ${COVERAGE_HTML_COMPONENT_ROOT_DIR} ${COVERAGE_HTML_LATEST_BUILD_DEF_DIR}
if [ $? != 0 ]; then
	echo "EMMA: ERROR - Could not copy ${COVERAGE_HTML_COMPONENT_ROOT_DIR} to ${COVERAGE_HTML_LATEST_BUILD_DEF_DIR}."
	exit 1
fi

echo "EMMA: Copying ${COVERAGE_XML_FILE} to ${COVERAGE_XML_LATEST_BUILD_DEF_DIR}..."
cp ${COVERAGE_XML_FILE} ${COVERAGE_XML_LATEST_BUILD_DEF_DIR}
if [ $? != 0 ]; then
	echo "EMMA: ERROR - Could not copy ${COVERAGE_XML_FILE} to ${COVERAGE_XML_LATEST_BUILD_DEF_DIR}."
	exit 1
fi

exit 0
