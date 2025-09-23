#!/bin/ksh

usage() {
    prog_name=`basename ${MYSELF}`
    cat <<EOF

Usage:
  $prog_name [options] <full pathname to configuration file> <full pathname to coverage XML directory> <full pathname to write summary report file>

Options:
  -p        file name pattern to match desired XML files in coverage XML directory
EOF
    exit 1
}

MYSELF=`readlink -f $0`

while getopts p: opt
do
    case $opt in
        p) PATTERN="${OPTARG}";;
        *) usage ;;
    esac
done
shift `expr $OPTIND - 1`

if [[ -n "${PATTERN}" ]]; then
	flag_use_pattern=true
else
	flag_use_pattern=false
	PATTERN=none
fi

if [[ -z "$1" ]]; then
    echo "ERROR:"
    echo "  You need to specify a configuration file"
    usage
fi

if [[ -z "$2" ]]; then
    echo "ERROR:"
    echo "  You need to specify a coverage XML directory"
    usage
fi

if [[ -z "$3" ]]; then
    echo "ERROR:"
    echo "  You need to specify a summary report file"
    usage
fi

CONFIG_FILE=$1
COVERAGE_XML_DIR=$2
SUMMARY_REPORT_TXT_FILE=$3

if [[ ! -f "${CONFIG_FILE}" ]]; then
    print "ERROR - unable to find configuration file ${CONFIG_FILE}."
    exit 1
fi

. "${CONFIG_FILE}"
if [[ $? -ne 0 ]]; then
    print "ERROR - failed to load configuration file ${CONFIG_FILE}."
    exit 1
fi

if [[ ! -d "${COVERAGE_XML_DIR}" ]]; then
    print "ERROR - unable to find coverage XML directory ${COVERAGE_XML_DIR}."
    exit 1
fi

cd ${DIR_EMMA_HOME}

${REMOTE_WAS_HOME}/bin/wsadmin.sh \
	-lang jython -conntype NONE \
	-javaoption "-Dpython.path=${REMOTE_SCRIPT_HOME}/lib" \
	-wsadmin_classpath "${REMOTE_SCRIPT_HOME}/lib/lccfg.jar" \
	-f "${DIR_EMMA_HOME}/scripts/gen_executive_summary.py" ${COVERAGE_XML_DIR} ${flag_use_pattern} ${PATTERN} ${SUMMARY_REPORT_TXT_FILE}

exit 0
