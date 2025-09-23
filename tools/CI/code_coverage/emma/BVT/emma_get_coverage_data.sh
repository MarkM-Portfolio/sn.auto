#!/bin/ksh

usage() {
    prog_name=`basename ${MYSELF}`
    cat <<EOF

Usage:
  $prog_name [options] <full pathname to configuration file> <full file pathname to write coverage data>

Options:
  -r        Reset coverage data after it is written.

EOF
    exit 1
}

MYSELF=`readlink -f $0`

while getopts r opt
do
    case $opt in
        r) flag_reset_coverage_data=1;;
        *) usage ;;
    esac
done
shift `expr $OPTIND - 1`

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

cd ${DIR_EMMA_HOME}
${JAVA_EXE} -cp ${DIR_EMMA_HOME}/emma.jar emma ctl -connect localhost:47653 -command coverage.get,${COVERAGE_DATA_FILE}
if [[ $? -ne 0 ]] ; then
    print "ERROR - failed to get configuration data."
    exit 1
fi

if [[ -n "${flag_reset_coverage_data}" ]]
then
	${JAVA_EXE} -cp ${EMMA_JAR} emma ctl -connect localhost:47653 -command coverage.reset
	if [[ $? -ne 0 ]] ; then
		print "ERROR - failed to reset configuration data."
		exit 1
	fi
fi

exit 0
