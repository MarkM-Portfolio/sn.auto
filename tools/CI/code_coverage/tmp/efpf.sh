#!/bin/sh


# Modify the property file.
PROPERTY_FILE_NAME="djm.properties"
PROPERTY_FILE_PATH=/tmp/djm/${PROPERTY_FILE_NAME}

OLD_ENTRY[1]="file.restrictionType\[@enabled\]=false"
NEW_ENTRY[1]="file.restrictionType[@enabled]=true"

OLD_ENTRY[2]="development\[@enabled\]=false"
NEW_ENTRY[2]="development[@enabled]=true"

OLD_ENTRY[3]="development.leakDetection\[@enabled\]=false"
NEW_ENTRY[3]="development.leakDetection[@enabled]=true"

OLD_ENTRY[4]="security.allowInlineDownload\[@enabled\]=false"
NEW_ENTRY[4]="security.allowInlineDownload[@enabled]=true"

NUM_ENTRIES=${#OLD_ENTRY[@]}
for i in `seq 1 ${NUM_ENTRIES}`
do
	echo "Replacing ${OLD_ENTRY[i]} with ${NEW_ENTRY[i]}"
	sed -i -e "s/${OLD_ENTRY[i]}/${NEW_ENTRY[i]}/" ${PROPERTY_FILE_PATH}
done

# Add the new entry.
ENTRY="directory.directoryServiceImpl=com.ibm.lconn.share.platform.test.MockDirectoryServiceImpl"
echo "Adding ${ENTRY} to ${PROPERTY_FILE_PATH}..."
echo "${ENTRY}" >> ${PROPERTY_FILE_PATH}


exit 0
