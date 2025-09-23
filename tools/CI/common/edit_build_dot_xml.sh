#!/bin/sh

COMPONENT=$1
BUILD_DOT_XML_FILE=$2

if [ "${COMPONENT}" == "homepage" ]; then
	echo "Replacing \"derby\" with \"db2\" in \"${BUILD_DOT_XML_FILE}\"..."
	sed -i -e "s]value=\"derby\"]value=\"db2\"]" ${BUILD_DOT_XML_FILE}

	grep 'value="db2"' ${BUILD_DOT_XML_FILE} > /dev/null 2>&1
	if [ $? != 0 ]; then
		echo "Failed to replace \"derby\" with \"db2\" in \"${BUILD_DOT_XML_FILE}\"."
		exit 1
	fi

	grep 'showoutput="true"' ${BUILD_DOT_XML_FILE} > /dev/null 2>&1
	if [ $? != 0 ]; then
		echo "Adding showoutput=\"true\" to junit tags in \"${BUILD_DOT_XML_FILE}\"..."
		sed -i -e "s]<junit ]<junit showoutput=\"true\" ]g" ${BUILD_DOT_XML_FILE}
	fi
fi

#if [ "${COMPONENT}" == "news" ]; then
#	# The log files get enormous and "bld junit-report" gets "out of memory" error if the following atttributes aren't changed.
#	echo "Removing showoutput=\"true\" from junit tags in \"${BUILD_DOT_XML_FILE}\"..."
#	sed -i -e "s]showoutput=\"true\"]]g" ${BUILD_DOT_XML_FILE}
#
#	echo "Changing printsummary=\"withOutAndErr\" in junit tags to printsummary=\"on\" in \"${BUILD_DOT_XML_FILE}\"..."
#	echo "Adding outputtoformatters=\"false\" to junit tags in \"${BUILD_DOT_XML_FILE}\"..."
#	sed -i -e "s]printsummary=\"withOutAndErr\"]printsummary=\"on\" outputtoformatters=\"false\"]g" ${BUILD_DOT_XML_FILE}
#fi

#echo "Removing \"fail\" tag from ${BUILD_DOT_XML_FILE}..."
#sed -i -e "s]<fail message.*>]]g" ${BUILD_DOT_XML_FILE}

exit 0
