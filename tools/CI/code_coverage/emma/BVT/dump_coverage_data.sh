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

DATE_TIME_STAMP=`date +%Y%m%d_%H%M%S`

CONFIG_FILE=$1

if [[ ! -f "${CONFIG_FILE}" ]]; then
    print "ERROR - unable to find configuration file ${CONFIG_FILE}." | tee -a ${LOG_FILE}
    exit 1
fi

. "${CONFIG_FILE}"
if [[ $? -ne 0 ]]; then
    print "ERROR - failed to load configuration file ${CONFIG_FILE}." | tee -a ${LOG_FILE}
    exit 1
fi

LOG_FILE=${DIR_EMMA_HOME}/dump.log
if [[ -f ${LOG_FILE} ]]; then
	mv ${LOG_FILE} ${LOG_FILE}.old
fi

BUILD_STR=""
if [[ -f /tmp/LC_temp/complete.txt ]]; then
	BUILD_STR=`cat /tmp/LC_temp/complete.txt`
fi

if [[ -n "${BUILD_STR}" ]]; then
	RUN_IDENTIFIER=${BUILD_STR}_${DATE_TIME_STAMP}
else
	RUN_IDENTIFIER=${DATE_TIME_STAMP}
fi

echo "RUN_IDENTIFIER: ${RUN_IDENTIFIER}"

COVERAGE_DATA_FILE=${EMMA_RUNTIMEDATA_DIR}/coverage_${RUN_IDENTIFIER}.ec

cd ${EMMA_SCRIPT_DIR}

print "Dumping coverage data to ${COVERAGE_DATA_FILE}..." | tee -a ${LOG_FILE}
./emma_get_coverage_data.sh ${CONFIG_FILE} ${COVERAGE_DATA_FILE} >> ${LOG_FILE} 2>&1
if [ $? != 0 ]; then
	print "Failed to dump coverage data."
	exit 1
fi

print "Generating HTML and XML reports..." | tee -a ${LOG_FILE}
./emma_generate_all_reports.sh -u ${RUN_IDENTIFIER} ${CONFIG_FILE} ${COVERAGE_DATA_FILE} >> ${LOG_FILE} 2>&1
if [ $? != 0 ]; then
	print "Failed to generate HTML and/or XML reports." | tee -a ${LOG_FILE}
	exit 1
fi

print "Generating component summary report ${COVERAGE_SUMMARY_FILE}..." | tee -a ${LOG_FILE}
./gen_executive_summary.sh -p ${RUN_IDENTIFIER} ${CONFIG_FILE} ${EMMA_REPORTS_XML_DIR} ${EMMA_REPORTS_HTML_DIR}/summary_report_${RUN_IDENTIFIER}.txt >> ${LOG_FILE} 2>&1
if [ $? != 0 ]; then
	print "Failed to generate executive summary report." | tee -a ${LOG_FILE}
	exit 1
fi

if [ -n "${REMOTE_HTTP_DOC_DIR}" -a -d "${REMOTE_HTTP_DOC_DIR}" ]; then
	print "Creating HTTP links to report files..." | tee -a ${LOG_FILE}
	cd ${REMOTE_HTTP_DOC_DIR}
	if [ ! -d coverage-reports ]; then
		sudo ln -s ${EMMA_REPORTS_DIR} coverage-reports
	fi
else
	print "HTTP doc directory \"${REMOTE_HTTP_DOC_DIR}\" does not exist" | tee -a ${LOG_FILE}
fi

print "Copying code coverage reports to CI system..." | tee -a ${LOG_FILE}
rsync -rt ${EMMA_REPORTS_HTML_DIR}/ icci@connectionsci1.cnx.cwp.pnp-hcl.com:/local/home/icci/emma/reports_bvt/html_reports
rsync -rt ${EMMA_REPORTS_XML_DIR}/ icci@connectionsci1.cnx.cwp.pnp-hcl.com:/local/home/icci/emma/reports_bvt/xml_reports

exit 0
