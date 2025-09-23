#!/bin/ksh

usage() {
    prog_name=`basename ${MYSELF}`
    cat <<EOF

Usage:
  $prog_name [options] <full pathname to configuration file>

Options:
  -i        comma separated list of full pathnames to metadata and coverage data files
  -h		full pathname to write report html file
  -x		full pathname to write report xml file
EOF
    exit 1
}

MYSELF=`readlink -f $0`

while getopts h:x:i: opt
do
    case $opt in
        h) reportHtmlFile="${OPTARG}";;
        x) reportXmlFile="${OPTARG}";;
        i) coverageDataFiles="${OPTARG}";;
        *) usage ;;
    esac
done
shift `expr $OPTIND - 1`

echo "reportHtmlFile: ${reportHtmlFile}"
echo "reportXmlFile: ${reportXmlFile}"
echo "coverageDataFiles: ${coverageDataFiles}"

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
if [[ $? -ne 0 ]] ; then
    print "ERROR - failed to load configuration file ${CONFIG_FILE}."
    exit 1
fi

cd ${DIR_EMMA_HOME}
${JAVA_EXE} -cp ${EMMA_JAR} emma report -r html,xml -in ${coverageDataFiles} -Dreport.html.out.file=${reportHtmlFile} -Dreport.xml.out.file=${reportXmlFile}
if [[ $? -ne 0 ]] ; then
    print "ERROR - failed to generate html and/or coverage reports."
    exit 1
fi
