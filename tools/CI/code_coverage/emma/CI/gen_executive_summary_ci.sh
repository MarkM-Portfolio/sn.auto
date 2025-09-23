#!/bin/ksh

usage() {
    prog_name=`basename ${MYSELF}`
    cat <<EOF

Usage:
  $prog_name <full pathname to configuration file>

EOF
    exit 1
}

MYSELF=`readlink -f $0`

if [[ -z "$1" ]]; then
    echo "ERROR:"
    echo "  You need to specify a configuration file"
    usage
fi

CONFIG_FILE=$1

if [[ ! -f "${CONFIG_FILE}" ]]; then
    print "ERROR - unable to find configuration file ${CONFIG_FILE}."
    exit 1
fi

. "${CONFIG_FILE}"
if [[ $? -ne 0 ]]; then
    print "ERROR - failed to load configuration file ${CONFIG_FILE}."
    exit 1
fi

DATE_TIME_STAMP=`date +%Y%m%d_%H%M%S`
COVERAGE_XML_LATEST_TMP_DIR=${EMMA_REPORTS_DIR}/tmp
		
# Generate an executive summary report for the "latest" reports.
# Need to operate on all the XML report files in the "latest" tree,
# so will consolidate them into one dir for ease of processing.
# First clean tmp dir.

if [ -d "${COVERAGE_XML_LATEST_TMP_DIR}" ]; then				
	echo "EMMA: Deleting directory: ${COVERAGE_XML_LATEST_TMP_DIR}"
	rm -rf ${COVERAGE_XML_LATEST_TMP_DIR}
	if [ $? != 0 ]; then
		echo "EMMA: ERROR - Could not delete directory: ${COVERAGE_XML_LATEST_TMP_DIR}"
		exit 1
	fi
fi
			
echo "EMMA: Creating directory: ${COVERAGE_XML_LATEST_TMP_DIR}"
mkdir -p ${COVERAGE_XML_LATEST_TMP_DIR}
if [ $? != 0 ]; then
	echo "EMMA: ERROR - Could not create directory: ${COVERAGE_XML_LATEST_TMP_DIR}"
	exit 1
fi

if [ ! -d "${COVERAGE_HTML_ALL_SUMMARIES_DIR}" ]; then
	echo "EMMA: Creating directory: ${COVERAGE_HTML_ALL_SUMMARIES_DIR}"
	mkdir -p ${COVERAGE_HTML_ALL_SUMMARIES_DIR}
	if [ $? != 0 ]; then
		echo "EMMA: ERROR - Could not create directory: ${COVERAGE_HTML_ALL_SUMMARIES_DIR}"
		exit 1
	fi
fi

echo "EMMA: Generating executive summary report..."
find ${COVERAGE_XML_LATEST_DIR} -name "*.xml" -exec cp {} ${COVERAGE_XML_LATEST_TMP_DIR} \;
if [ $? != 0 ]; then
	echo "EMMA: ERROR - Failed to copy XML report files to ${COVERAGE_XML_LATEST_TMP_DIR}."
	exit 1
fi
		
${REMOTE_WAS_HOME}/bin/wsadmin.sh \
	-lang jython -conntype NONE \
	-javaoption "-Dpython.path=${REMOTE_SCRIPT_HOME}/lib" \
	-wsadmin_classpath "${REMOTE_SCRIPT_HOME}/lib/lccfg.jar" \
	-f "${EMMA_SCRIPT_DIR}/gen_executive_summary.py" ${COVERAGE_XML_LATEST_TMP_DIR} false none ${COVERAGE_HTML_ALL_SUMMARIES_DIR}/latest_summary_${DATE_TIME_STAMP}.txt
if [ $? != 0 ]; then
	echo "EMMA: ERROR - Failed to generate executive summary report."
	exit 1
fi

echo "EMMA: Copying ${COVERAGE_HTML_ALL_SUMMARIES_DIR}/latest_summary_${DATE_TIME_STAMP}.txt to ${EMMA_REPORTS_HTML_DIR}..."
cp ${COVERAGE_HTML_ALL_SUMMARIES_DIR}/latest_summary_${DATE_TIME_STAMP}.txt ${EMMA_REPORTS_HTML_DIR}/latest_summary.txt
if [ $? != 0 ]; then
	echo "EMMA: ERROR - Failed to copy ${COVERAGE_HTML_ALL_SUMMARIES_DIR}/latest_summary_${DATE_TIME_STAMP}.txt to ${EMMA_REPORTS_HTML_DIR}."
	exit 1
fi
	
exit 0
