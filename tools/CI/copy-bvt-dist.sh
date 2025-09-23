#!/bin/sh

DIR_SRC[1]=${BUILD_HOME}/${SRC}/${APPDIR}/build/bvt.dist/release
DIR_DST[1]=/local/opt/IBM/HTTPServer/htdocs/bvt-${buildDefinitionId}

DIR_SRC[2]=${BUILD_HOME}/${SRC}/${APPDIR}/build/bvt.dist/dist
DIR_DST[2]=/local/opt/IBM/HTTPServer/htdocs/bvt.dist-${buildDefinitionId}

COPY_OK="yes"
NUM_DIRS=${#DIR_SRC[@]}
for i in `seq 1 ${NUM_DIRS}`
do
	echo "Copying ${DIR_SRC[i]} to ${DIR_DST[i]}..."
	rsync -av --delete ${DIR_SRC[i]}/ ${DIR_DST[i]}
	if [ $? != 0 ]; then
		echo "Could not copy ${DIR_SRC[i]} to ${DIR_DST[i]}."
		COPY_OK="no"
		continue
	fi
done

if [ "${COPY_OK}" != "yes" ]; then
	exit 1
fi

exit 0
