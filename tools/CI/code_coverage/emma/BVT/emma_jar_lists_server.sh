
# Be careful with quoting. If the jar list has wild card characters (* or ?),
# then use single quote. Otherwise, with double quotes, will have to escape those characters and I forget
# whether it will have to be multiply escaped so that the consumer of the arrays does the right thing.

index=0

case $APPLICATION_NAME in		
	Profiles)
		WAR_FILENAME=lc.profiles.app.war
		
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EXTRACTED_WAR}/WEB-INF/lib
		JARS_TO_INSTRUMENT[${index}]='lc.profiles.web.app.jar'
		index=`expr ${index} + 1`
		
		DIR_WITH_JARS_TO_INSTRUMENT[${index}]=${DIR_EXTRACTED_EAR}
		JARS_TO_INSTRUMENT[${index}]='lc.profiles.core.service.*.jar lc.profiles.web.*.jar'
		index=`expr ${index} + 1`;;
		
	*) echo "Unknown application: $APPLICATION_NAME";;
esac

NUM_DIRS_WITH_JARS_TO_INSTRUMENT=${#DIR_WITH_JARS_TO_INSTRUMENT[*]}
echo "NUM_DIRS_WITH_JARS_TO_INSTRUMENT: ${NUM_DIRS_WITH_JARS_TO_INSTRUMENT}"

index=0
while [[ "${index}" -lt "${NUM_DIRS_WITH_JARS_TO_INSTRUMENT}" ]]
do
	echo "DIR_WITH_JARS_TO_INSTRUMENT[${index}]: ${DIR_WITH_JARS_TO_INSTRUMENT[${index}]}"
	echo "JARS_TO_INSTRUMENT[${index}]: ${JARS_TO_INSTRUMENT[${index}]}"
	index=`expr ${index} + 1`
done
