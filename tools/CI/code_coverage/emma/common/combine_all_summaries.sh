#!/bin/sh

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
    echo "ERROR - unable to find configuration file ${CONFIG_FILE}."
    exit 1
fi

. "${CONFIG_FILE}"
if [[ $? -ne 0 ]]; then
    echo "ERROR - failed to load configuration file ${CONFIG_FILE}."
    exit 1
fi

DATE_TIME_STAMP=`date +%Y%m%d_%H%M%S`
SUMMARY_REPORT_TXT_FILE=${DIR_EMMA_HOME}/reports/combined_results_${DATE_TIME_STAMP}.txt

rm ${LOCAL_BVT_DIR}/*.xml
rm -rf ${LOCAL_CI_DIR}/latest

COMPONENTS="Activities \
			ActivityStreamUI \
			Blogs \
			Communities \
			Dogear \
			Files \
			Forums \
			Homepage \
			Infra \
			LinkedLibrary-MediaGallery \
			Mail-In \
			Metrics \
			Mobile \
			News \
			Notifications \
			Profiles \
			Search \
			Sonata \
			Waltz \
			WidgetContainer \
			Wikis"

# Copy the latest BVT xml files from the BVT machine to this machine.
for COMPONENT in ${COMPONENTS}
do
	componentXMLFiles=`ssh ${REMOTE_BVT_USER}@${REMOTE_BVT_HOST} "cd ${REMOTE_BVT_EMMA_HOME}/reports/xml_reports; ls -1tm ${COMPONENT}*.xml"`
	if [[ $? != 0 ]]; then
		echo "EMMA: Did not find any XML files for component ${COMPONENT}."
		continue
	fi
	
	componentXMLFile=`echo ${componentXMLFiles} | cut -d ',' -f1`
	echo "EMMA: Copying ${componentXMLFile} from ${REMOTE_BVT_HOST}:${REMOTE_BVT_EMMA_HOME}/reports/xml_reports to ${LOCAL_BVT_DIR}..."
	scp ${REMOTE_BVT_USER}@${REMOTE_BVT_HOST}:${REMOTE_BVT_EMMA_HOME}/reports/xml_reports/${componentXMLFile} ${LOCAL_BVT_DIR}
	if [[ $? != 0 ]]; then
		echo "EMMA: Failed to Copy ${componentXMLFile} from ${REMOTE_BVT_HOST}:${REMOTE_BVT_EMMA_HOME}/reports/xml_reports to ${LOCAL_BVT_DIR}."
		exit 1
	fi
done

#Copy the latest CI xml files from the CI machine to this machine.
echo "EMMA: Copying ${REMOTE_CI_EMMA_HOME}/reports/xml_reports/scheduled-builds/latest directory tree from ${REMOTE_CI_HOST} to ${LOCAL_CI_DIR}..."
scp -r ${REMOTE_CI_USER}@${REMOTE_CI_HOST}:${REMOTE_CI_EMMA_HOME}/reports/xml_reports/scheduled-builds/latest ${LOCAL_CI_DIR}
if [[ $? != 0 ]]; then
	echo "EMMA: Failed to Copy ${componentXMLFile} from ${REMOTE_CI_HOST} to ${LOCAL_CI_DIR}."
	exit 1
fi

echo -n "" > ${FILEPATHS_FILE}
# For each component, calculate the combined summaries.
for COMPONENT in ${COMPONENTS}
do	
	# Get local BVT XML file
	BVT_XML_FILE_EXISTS="false"
	BVT_XML_FILE=`ls ${LOCAL_BVT_DIR}/${COMPONENT}*.xml`
	if [[ -n "${BVT_XML_FILE}" && -f ${BVT_XML_FILE} ]]; then
		BVT_XML_FILE_EXISTS="true"
	fi
	
	# Get local CI XML file
	if [[ "${COMPONENT}" == "Dogear" ]]; then
		COMPONENT="Bookmarks"
	fi
	COMPONENT=`echo ${COMPONENT} | tr '[:upper:]' '[:lower:]'`
	
	CI_XML_FILE_EXISTS="false"
	CI_XML_FILE=`find ${LOCAL_CI_DIR}/latest -type f -name "${COMPONENT}*.xml"`
	echo "COMPONENT: ${COMPONENT}"
	echo "CI_XML_FILE: ${CI_XML_FILE}"
	if [[ -n "${CI_XML_FILE}" && -f "${CI_XML_FILE}" ]]; then
		CI_XML_FILE_EXISTS="true"
	fi
		
	XML_FILE2=""
	if [[ "${BVT_XML_FILE_EXISTS}" == "true" ]]; then
		XML_FILE1=${BVT_XML_FILE}
		if [[ "${CI_XML_FILE_EXISTS}" == "true" ]]; then
			XML_FILE2=${CI_XML_FILE}
		fi
	elif [[ "${CI_XML_FILE_EXISTS}" == "true" ]]; then
		XML_FILE1=${CI_XML_FILE}
	else
		echo "EMMA: Could not find EMMA BVT and CI XML report files for ${COMPONENT}."
		continue
	fi
	
	echo "${COMPONENT},${XML_FILE1},${XML_FILE2}" >> ${FILEPATHS_FILE}
done

${EMMA_SCRIPT_DIR}/combine_summaries.sh ${CONFIG_FILE} ${FILEPATHS_FILE} ${SUMMARY_REPORT_TXT_FILE}

scp ${SUMMARY_REPORT_TXT_FILE} ${REMOTE_CI_USER}@${REMOTE_CI_HOST}:${REMOTE_CI_EMMA_HOME}/reports/combined_bvt_and_ci_reports

exit 0

