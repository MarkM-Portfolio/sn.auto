#!/bin/sh

# Extract ear file.
# grep for entry in property file.
# If it exists, delete entry from property file.
# Append entry to property file.
# HAD PROBLEMS WITH (so not doing): Update ear file with modified property file.
# Package up ear.
# Replace original ear with newly packaged up ear.


# Make directories to work in.
DIR_EXTRACTED_EAR=${BUILD_HOME}/extracted_ear
DIR_MODIFIED_EAR=${BUILD_HOME}/modified_ear

for DIR_TO_CREATE in ${DIR_EXTRACTED_EAR} ${DIR_MODIFIED_EAR}
do
	if [ -z "${DIR_TO_CREATE}" -o "${DIR_TO_CREATE}" == "/" ]; then
		echo "DIR_TO_CREATE is invalid: \"${DIR_TO_CREATE}\""
		exit 1
	fi
	if [[ -d ${DIR_TO_CREATE} ]]; then
		echo "Deleting directory: ${DIR_TO_CREATE}"
		rm -rf ${DIR_TO_CREATE}
		if [[ $? != 0 ]]; then
			echo "Could not delete directory: ${DIR_TO_CREATE}"
			exit 1
		fi
	fi
	
	echo "Creating directory: ${DIR_TO_CREATE}..."
	mkdir ${DIR_TO_CREATE}
	if [[ $? != 0 ]]; then
		echo "Could not create directory: ${DIR_TO_CREATE}"
		exit 1
	fi
done

# Extract EAR file, but first save off a copy to use when we want
# to deploy an EAR without all the unit test stuff for prebvt testing.
EAR_FILE_NAME=`basename ${EAR_FILE}`
EAR_FILE_FULLPATH=${BUILD_HOME}/${SRC}/${APPDIR}/${EAR_FILE}

cp ${EAR_FILE_FULLPATH} ${TMP}

cd ${DIR_EXTRACTED_EAR}
echo "Unzipping ${EAR_FILE_FULLPATH}..."
unzip ${EAR_FILE_FULLPATH}
STATUS=$?
if [ ${STATUS} != 0 ]; then
	echo "Failed to extract ${EAR_FILE_FULLPATH}."
	echo "unzip returned: ${STATUS}"
	exit 1
fi

# Modify the property file.
PROPERTY_FILE_NAME="com.ibm.lconn.share.platform.bootstrap.properties"
PROPERTY_FILE_PATH=${DIR_EXTRACTED_EAR}/config/${PROPERTY_FILE_NAME}

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
		#Need the quoted version of the key in order for sed to work.
		ENTRY_MOD="${ENTRY_KEY[i]}=${ENTRY_VALUE_OLD}"
		echo "Removing ${ENTRY_MOD} from ${PROPERTY_FILE_PATH}..."
		sed -i -e "s/${ENTRY_MOD}//" ${PROPERTY_FILE_PATH}
		if [ $? != 0 ]; then
			echo "Could not remove ${ENTRY_MOD} from ${PROPERTY_FILE_PATH}."
			exit 1
		fi
	fi

	# Add the new entry.
	NEW_ENTRY="${ENTRY_KEY[i]}=${ENTRY_VALUE_NEW[i]}"
	eval echo "Adding ${NEW_ENTRY} to ${PROPERTY_FILE_PATH}..."
	eval echo "${NEW_ENTRY}" >> ${PROPERTY_FILE_PATH}
done

# Package up the EAR.
cd ${DIR_EXTRACTED_EAR}
echo "Zipping up ear file with modified property file..."
zip -r ${DIR_MODIFIED_EAR}/${EAR_FILE_NAME} .
STATUS=$?
if [ ${STATUS} != 0 ]; then
	echo "Failed to package up ear file ${DIR_MODIFIED_EAR}/${EAR_FILE_NAME} from ${DIR_EXTRACTED_EAR}."
	echo "zip -r returned: ${STATUS}"
	exit 1
fi

# Replace original EAR with the newly modified one.
echo "Replacing ${EAR_FILE_FULLPATH} with ${DIR_MODIFIED_EAR}/${EAR_FILE_NAME}..."
cp ${DIR_MODIFIED_EAR}/${EAR_FILE_NAME} ${EAR_FILE_FULLPATH}

#####

# Extract jar file.
# grep for entries in property files.
# If they exists, delete entries from property file.
# Append entries to property file.
# Package up jar.
# Replace original jar with newly packaged up jar.

# Make directories to work in.
DIR_EXTRACTED_JAR=${BUILD_HOME}/extracted_jar
DIR_MODIFIED_JAR=${BUILD_HOME}/modified_jar

for DIR_TO_CREATE in ${DIR_EXTRACTED_JAR} ${DIR_MODIFIED_JAR}
do
	if [ -z "${DIR_TO_CREATE}" -o "${DIR_TO_CREATE}" == "/" ]; then
		echo "DIR_TO_CREATE is invalid: \"${DIR_TO_CREATE}\""
		exit 1
	fi
	if [[ -d ${DIR_TO_CREATE} ]]; then
		echo "Deleting directory: ${DIR_TO_CREATE}"
		rm -rf ${DIR_TO_CREATE}
		if [[ $? != 0 ]]; then
			echo "Could not delete directory: ${DIR_TO_CREATE}"
			exit 1
		fi
	fi
	
	echo "Creating directory: ${DIR_TO_CREATE}..."
	mkdir ${DIR_TO_CREATE}
	if [[ $? != 0 ]]; then
		echo "Could not create directory: ${DIR_TO_CREATE}"
		exit 1
	fi
done

# Extract JAR file.
JAR_FILE_NAME="share.services.test.jar"
JAR_FILE_FULLPATH=${BUILD_HOME}/${SRC}/${APPDIR}/build/share.services.test/lib/${JAR_FILE_NAME}
	
cd ${DIR_EXTRACTED_JAR}
echo "Unzipping ${JAR_FILE_FULLPATH}..."
unzip ${JAR_FILE_FULLPATH}
STATUS=$?
if [ ${STATUS} != 0 ]; then
	echo "Failed to extract ${JAR_FILE_FULLPATH}."
	echo "unzip returned: ${STATUS}"
	exit 1
fi

# Modify the property files.
PROPERTY_FILE1=${DIR_EXTRACTED_JAR}/com/ibm/lconn/files/cmis/atompub/test/test-basic.properties
PROPERTY_FILE2=${DIR_EXTRACTED_JAR}/com/ibm/lconn/share/services/test/files.properties
PROPERTY_FILE3=${DIR_EXTRACTED_JAR}/com/ibm/lconn/share/services/test/server.properties
PROPERTY_FILE4=${DIR_EXTRACTED_JAR}/com/ibm/lconn/share/services/test/wikis.properties

for PROPERTY_FILE in ${PROPERTY_FILE1} ${PROPERTY_FILE2} ${PROPERTY_FILE3} ${PROPERTY_FILE4}
do
	sed -i -e "s/localhost/${WAS_HOST}.swg.usma.ibm.com/g" ${PROPERTY_FILE}
	if [ $? != 0 ]; then
		echo "Could not edit ${PROPERTY_FILE}."
		exit 1
	fi
done

# Package up the JAR.
cd ${DIR_EXTRACTED_JAR}
echo "Zipping up jar file with modified property files..."
zip -r ${DIR_MODIFIED_JAR}/${JAR_FILE_NAME} .
STATUS=$?
if [ ${STATUS} != 0 ]; then
	echo "Failed to package up jar file ${DIR_MODIFIED_JAR}/${JAR_FILE_NAME} from ${DIR_EXTRACTED_JAR}."
	echo "zip -r returned: ${STATUS}"
	exit 1
fi

# Replace original JAR with the newly modified one.
echo "Replacing ${JAR_FILE_FULLPATH} with ${DIR_MODIFIED_JAR}/${JAR_FILE_NAME}..."
cp ${DIR_MODIFIED_JAR}/${JAR_FILE_NAME} ${JAR_FILE_FULLPATH}

#####

# Extract test war file.
# Add ant-junit.jar and junit.jar to manifest file.
# Package up war.
# Replace original war with newly packaged up war.

# Make directories to work in.
DIR_EXTRACTED_WAR=${BUILD_HOME}/extracted_war
DIR_MODIFIED_WAR=${BUILD_HOME}/modified_war

for DIR_TO_CREATE in ${DIR_EXTRACTED_WAR} ${DIR_MODIFIED_WAR}
do
	if [ -z "${DIR_TO_CREATE}" -o "${DIR_TO_CREATE}" == "/" ]; then
		echo "DIR_TO_CREATE is invalid: \"${DIR_TO_CREATE}\""
		exit 1
	fi
	if [[ -d ${DIR_TO_CREATE} ]]; then
		echo "Deleting directory: ${DIR_TO_CREATE}"
		rm -rf ${DIR_TO_CREATE}
		if [[ $? != 0 ]]; then
			echo "Could not delete directory: ${DIR_TO_CREATE}"
			exit 1
		fi
	fi
	
	echo "Creating directory: ${DIR_TO_CREATE}..."
	mkdir ${DIR_TO_CREATE}
	if [[ $? != 0 ]]; then
		echo "Could not create directory: ${DIR_TO_CREATE}"
		exit 1
	fi
done

# Extract WAR file.
WAR_FILE_NAME="share.test.war"
WAR_FILE_FULLPATH=${BUILD_HOME}/${SRC}/${APPDIR}/build/share.test.war/lib/${WAR_FILE_NAME}
	
cd ${DIR_EXTRACTED_WAR}
echo "Unzipping ${WAR_FILE_FULLPATH}..."
unzip ${WAR_FILE_FULLPATH}
STATUS=$?
if [ ${STATUS} != 0 ]; then
	echo "Failed to extract ${WAR_FILE_FULLPATH}."
	echo "unzip returned: ${STATUS}"
	exit 1
fi

# Modify the manifest file.
MANIFEST_FILE=${DIR_EXTRACTED_WAR}/META-INF/MANIFEST.MF

sed -i -e "s/lc.rest.api.jar/lc.rest.api.jar ant-junit.jar junit.jar/" ${MANIFEST_FILE}
if [ $? != 0 ]; then
	echo "Could not edit ${MANIFEST_FILE}."
	exit 1
fi

# Package up the WAR.
cd ${DIR_EXTRACTED_WAR}
echo "Zipping up war file with modified property files..."
zip -r ${DIR_MODIFIED_WAR}/${WAR_FILE_NAME} .
STATUS=$?
if [ ${STATUS} != 0 ]; then
	echo "Failed to package up war file ${DIR_MODIFIED_WAR}/${WAR_FILE_NAME} from ${DIR_EXTRACTED_WAR}."
	echo "zip -r returned: ${STATUS}"
	exit 1
fi

# Replace original WAR with the newly modified one.
echo "Replacing ${WAR_FILE_FULLPATH} with ${DIR_MODIFIED_WAR}/${WAR_FILE_NAME}..."
cp ${DIR_MODIFIED_WAR}/${WAR_FILE_NAME} ${WAR_FILE_FULLPATH}

exit 0
