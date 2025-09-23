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
    echo "EMMA: ERROR:"
    echo "EMMA:  You need to specify a configuration file"
    usage
fi

CONFIG_FILE=$1

if [[ ! -f "${CONFIG_FILE}" ]]; then
    print "EMMA: ERROR - unable to find configuration file ${CONFIG_FILE}."
    exit 1
fi

. "${CONFIG_FILE}"
if [[ $? -ne 0 ]]; then
    print "EMMA: ERROR - failed to load configuration file ${CONFIG_FILE}."
    exit 1
fi

$HOME/jython-2.5.2/jython -Dpython.path="${DIR_EMMA_HOME}/wink-json4j-1.1.3-incubating.jar" create_build_requests_ci.py
if [[ $? -ne 0 ]]; then
    print "EMMA: Warning - failed to submit build requests."
fi

stty sane

${EMMA_SCRIPT_DIR}/gen_executive_summary_ci.sh ${CONFIG_FILE}
if [[ $? -ne 0 ]]; then
    print "EMMA: ERROR - failed to generate executive summary report."
    exit 1
fi
	
exit 0
