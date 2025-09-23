#!/bin/sh

CURRENT_DIR=`pwd`
echo "CURRENT_DIR: ${CURRENT_DIR}"

echo "${CURRENT_DIR}" | grep 'activities.impl' > /dev/null 2>&1
if [ $? != 0 ]; then
	echo "This is not the activities.impl build, so don't need to edit felement.xml."
	exit 0
fi

echo "This is the activities.impl build, so removing Activities build label value from felement.xml..."
sed -i -e "s/<build-label.*value=\".*_Activities_.*>//" felement.xml

exit 0
