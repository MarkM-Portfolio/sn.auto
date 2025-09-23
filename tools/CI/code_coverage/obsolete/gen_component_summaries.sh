#!/bin/ksh

usage() {
    prog_name=`basename ${MYSELF}`
    cat <<EOF

Usage:
  $prog_name <full pathname to configuration file> <full pathname to component package XML file> <full pathname to coverage report XML file> <full pathname to write summary report file>

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
    echo "  You need to specify a component package XML file"
    usage
fi

if [[ -z "$3" ]]; then
    echo "ERROR:"
    echo "  You need to specify a coverage report XML file"
    usage
fi

if [[ -z "$4" ]]; then
    echo "ERROR:"
    echo "  You need to specify a summary report file"
    usage
fi

CONFIG_FILE=$1
COMPONENT_PACKAGE_XML_FILE=$2
COVERAGE_REPORT_XML_FILE=$3
SUMMARY_REPORT_TXT_FILE=$4

if [[ ! -f "${CONFIG_FILE}" ]]; then
    print "ERROR - unable to find configuration file ${CONFIG_FILE}."
    exit 1
fi

. "${CONFIG_FILE}"
if [[ $? -ne 0 ]]; then
    print "ERROR - failed to load configuration file ${CONFIG_FILE}."
    exit 1
fi

if [[ ! -f "${COMPONENT_PACKAGE_XML_FILE}" ]]; then
    print "ERROR - unable to find component package XML file ${COMPONENT_PACKAGE_XML_FILE}."
    exit 1
fi

if [[ ! -f "${COVERAGE_REPORT_XML_FILE}" ]]; then
    print "ERROR - unable to find coverage report XML file ${COVERAGE_REPORT_XML_FILE}."
    exit 1
fi

cd ${DIR_EMMA_HOME}

${REMOTE_WAS_HOME}/bin/wsadmin.sh \
	-lang jython -conntype NONE \
	-javaoption "-Dpython.path=${REMOTE_SCRIPT_HOME}/lib" \
	-wsadmin_classpath "${REMOTE_SCRIPT_HOME}/lib/lccfg.jar" \
	-f "${DIR_EMMA_HOME}/scripts/gen_component_summaries.py" ${COMPONENT_PACKAGE_XML_FILE} ${COVERAGE_REPORT_XML_FILE} ${SUMMARY_REPORT_TXT_FILE}

exit 0
