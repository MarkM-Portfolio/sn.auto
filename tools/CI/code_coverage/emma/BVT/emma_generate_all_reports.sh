#!/bin/ksh

usage() {
    prog_name=`basename ${MYSELF}`
    cat <<EOF

Usage:
  $prog_name [options] <full pathname to configuration file> <full file pathname to coverage data>

Options:
  -u        identifier to include in the report file paths/names
EOF
    exit 1
}

MYSELF=`readlink -f $0`

index=0
while getopts u: opt
do
    case $opt in
        u) IDENTIFIER="${OPTARG}";;
        *) usage ;;
    esac
done
shift `expr $OPTIND - 1`

echo "IDENTIFIER: ${IDENTIFIER}"

if [[ -z "$1" ]]; then
    echo "ERROR:"
    echo "  You need to specify a configuration file"
    usage
fi

if [[ -z "$2" ]]; then
    echo "ERROR:"
    echo "  You need to specify a coverage data file"
    usage
fi

CONFIG_FILE=$1
COVERAGE_DATA_FILE=$2

if [[ ! -f "${CONFIG_FILE}" ]]; then
    print "ERROR - unable to find configuration file ${CONFIG_FILE}."
    exit 1
fi

. "${CONFIG_FILE}"
if [[ $? -ne 0 ]] ; then
    print "ERROR - failed to load configuration file ${CONFIG_FILE}."
    exit 1
fi

if [[ ! -f "${COVERAGE_DATA_FILE}" ]]; then
    print "ERROR - unable to find coverage data file ${COVERAGE_DATA_FILE}."
    exit 1
fi

cd ${DIR_EMMA_HOME}

# Generate html and xml reports for each component's metadata file.
RET=0
ls -1 ${EMMA_METADATA_DIR}/*.em | while read METADATA_FILE
do
	COMPONENT=`basename ${METADATA_FILE} .em`
	print "COMPONENT=${COMPONENT}"
	
	if [[ -n "${IDENTIFIER}" ]]; then
		PATH_TAG="${COMPONENT}_${IDENTIFIER}"
	else
		PATH_TAG="${COMPONENT}"
	fi

	${DIR_EMMA_HOME}/scripts/emma_generate_reports.sh -i ${METADATA_FILE},${COVERAGE_DATA_FILE} -h ${EMMA_REPORTS_HTML_DIR}/${PATH_TAG}/index.html -x ${EMMA_REPORTS_XML_DIR}/${PATH_TAG}.xml ${CONFIG_FILE}
	if [[ $? -ne 0 ]]; then
		print "ERROR - failed to generate html and/or xml coverage reports for ${COMPONENT}."
		RET=1
	fi	
done

exit ${RET}
