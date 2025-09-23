#!/bin/sh


# Modify the property file.
PROPERTY_FILE_NAME="djm.properties"
PROPERTY_FILE_PATH="/tmp/djm/${PROPERTY_FILE_NAME}"

ENTRY_KEY[1]="file.restrictionType\[@enabled\]"
ENTRY_VALUE_NEW[1]="true"

ENTRY_KEY[2]="development\[@enabled\]"
ENTRY_VALUE_NEW[2]="true"

ENTRY_KEY[3]="development.leakDetection\[@enabled\]"
ENTRY_VALUE_NEW[3]="true"

ENTRY_KEY[4]="security.allowInlineDownload\[@enabled\]"
ENTRY_VALUE_NEW[4]="true"

ENTRY_KEY[5]="directory.directoryServiceImpl"
ENTRY_VALUE_NEW[5]="com.ibm.lconn.share.platform.test.MockDirectoryServiceImpl"

NUM_ENTRIES=${#ENTRY_KEY[@]}
for i in `seq 1 ${NUM_ENTRIES}`
do
	ENTRY=`grep "${ENTRY_KEY[i]}=" ${PROPERTY_FILE_PATH}`
	if [ $? == 0 ]; then
		# Entry exists; get its value.
		ENTRY_VALUE_OLD=`echo ${ENTRY} | cut -d '=' -f2`
		echo "ENTRY_VALUE_OLD: ${ENTRY_VALUE_OLD}"
		ENTRY_MOD="${ENTRY_KEY[i]}=${ENTRY_VALUE_OLD}"
		echo "Removing ${ENTRY_MOD} from ${PROPERTY_FILE_PATH}..."
		sed -i -e "s/${ENTRY_MOD}//" ${PROPERTY_FILE_PATH}
	fi

	# Add the new entry.
	NEW_ENTRY="${ENTRY_KEY[i]}=${ENTRY_VALUE_NEW[i]}"
	eval echo "Adding ${NEW_ENTRY} to ${PROPERTY_FILE_PATH}..."
	eval echo "${NEW_ENTRY}" >> ${PROPERTY_FILE_PATH}
done

exit 0
