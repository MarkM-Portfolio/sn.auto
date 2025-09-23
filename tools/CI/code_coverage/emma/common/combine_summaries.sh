#!/bin/sh

usage() {
    prog_name=`basename ${MYSELF}`
    cat <<EOF

Usage:
  $prog_name <full pathname to configuration file> <full pathname to EMMA XML filepaths file> <full pathname to write summary report file>

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
    echo "  You need to specify an EMMA XML filepaths file"
    usage
fi

if [[ -z "$3" ]]; then
    echo "ERROR:"
    echo "  You need to specify a summary report file"
    usage
fi

CONFIG_FILE=$1
FILEPATHS_FILE=$2
SUMMARY_REPORT_TXT_FILE=$3

if [[ ! -f "${CONFIG_FILE}" ]]; then
    echo "ERROR - unable to find configuration file ${CONFIG_FILE}."
    exit 1
fi

. "${CONFIG_FILE}"
if [[ $? -ne 0 ]]; then
    echo "ERROR - failed to load configuration file ${CONFIG_FILE}."
    exit 1
fi

if [[ ! -f "${FILEPATHS_FILE}" ]]; then
    echo "ERROR - unable to find XML file ${FILEPATHS_FILE}."
    exit 1
fi

${REMOTE_WAS_PROFILE_HOME}/bin/wsadmin.sh \
	-lang jython -conntype NONE \
	-javaoption "-Dpython.path=${REMOTE_SCRIPT_HOME}/lib" \
	-wsadmin_classpath "${REMOTE_SCRIPT_HOME}/lib/lccfg.jar" \
	-f ${DIR_EMMA_HOME}/scripts/combine_summaries.py ${FILEPATHS_FILE} ${SUMMARY_REPORT_TXT_FILE}
if [[ $? -ne 0 ]]; then
    echo "ERROR - failed to run combine_summaries.py."
    exit 1
fi
	
exit 0
