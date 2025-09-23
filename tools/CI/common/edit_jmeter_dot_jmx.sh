#!/bin/sh

JMX_FILE=${1}

# Disable thread group parallelism
echo "Editing ${JMX_FILE} to disable thread group parallelism..."
sed -i -e "s]<boolProp name=\"TestPlan\.serialize_threadgroups\">false]<boolProp name=\"TestPlan\.serialize_threadgroups\">true]" ${JMX_FILE}

# Change all component concurrency levels to 1
index=0
COMPONENTS[${index}]=profiles;			index=`expr ${index} + 1`
COMPONENTS[${index}]=community;			index=`expr ${index} + 1`
COMPONENTS[${index}]=files;				index=`expr ${index} + 1`
COMPONENTS[${index}]=wikis;				index=`expr ${index} + 1`
COMPONENTS[${index}]=microblogging;		index=`expr ${index} + 1`
COMPONENTS[${index}]=activitystreams;	index=`expr ${index} + 1`
COMPONENTS[${index}]=customlist;		index=`expr ${index} + 1`
COMPONENTS[${index}]=following.api;		index=`expr ${index} + 1`
COMPONENTS[${index}]=multitenancy;		index=`expr ${index} + 1`
COMPONENTS[${index}]=oembed;			index=`expr ${index} + 1`
COMPONENTS[${index}]=visitor.model;		index=`expr ${index} + 1`
COMPONENTS[${index}]=news.seedlist;		index=`expr ${index} + 1`

NUM_COMPONENTS=${#COMPONENTS[*]}
index=0
while [ "${index}" -lt "${NUM_COMPONENTS}" ]
do
	LINE_NUM=`grep -n "<stringProp name=\"Argument.name\">default.concurrency.level.${COMPONENTS[${index}]}</stringProp>" ${JMX_FILE} | cut -f1 -d':'`
	LINE_NUM=`expr ${LINE_NUM} + 1`
	echo "Editing line ${LINE_NUM} to set concurrency level to 1 for ${COMPONENTS[${index}]}..."
	sed -i -e "${LINE_NUM} s]Argument\.value\">.*<]Argument\.value\">1<]" ${JMX_FILE}
	index=`expr ${index} + 1`
done

exit 0
