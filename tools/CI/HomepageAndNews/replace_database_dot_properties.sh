#!/bin/sh

# Extract jar file.
# Replace the database.properties file.
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
JAR_FILE_NAME="home.news.test.jar"
JAR_FILE_FULLPATH=${CI_HOME}/prereq_fes/sn.infra/lwp/build/home.news.test/lib/${JAR_FILE_NAME}
	
cd ${DIR_EXTRACTED_JAR}
echo "Unzipping ${JAR_FILE_FULLPATH}..."
unzip ${JAR_FILE_FULLPATH}
STATUS=$?
if [ ${STATUS} != 0 ]; then
	echo "Failed to extract ${JAR_FILE_FULLPATH}."
	echo "unzip returned: ${STATUS}"
	exit 1
fi

# Replace the database.properties file.
echo "Copying ${CI_HOME}/database.properties to ${DIR_EXTRACTED_JAR}/test/spring/home-news/database..."
cp ${CI_HOME}/database.properties ${DIR_EXTRACTED_JAR}/test/spring/home-news/database

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

exit 0
