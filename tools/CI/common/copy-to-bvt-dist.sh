#!/bin/sh

echo "Loading ${CI_COMMON_HOME}/ci_functions.sh..."
. ${CI_COMMON_HOME}/ci_functions.sh
if [ $? != 0 ]; then
	echo "Failed to load ${CI_COMMON_HOME}/ci_functions.sh."
	exit 1
fi

echo "Loading ${CI_COMMON_HOME}/system.properties..."
. ${CI_COMMON_HOME}/system.properties
if [ $? != 0 ]; then
	echo "Failed to load ${CI_COMMON_HOME}/system.properties."
	exit 1
fi

# Get and unzip the Automation fe.zip file.
[ -d src ] && ( echo "Deleting source tree: `pwd`/src..."; rm -rf src )
${LCUPDATE_DIR}/bin/get-build.sh -f -s src -b ${BUILD_LABEL}
if [ $? != 0 ]; then
	echo "Failed to download or unzip fe-zip.sh."
	exit 1
fi

DIR_SRC[1]="${WORKSPACE}/src/sn.auto/lwp/build/bvt.dist/dist"
DIR_DST[1]=/local/opt/IBM/HTTPServer/htdocs/bvt.dist-${BUILD_STREAM}_${BUILD_COMPONENT}

DIR_SRC[2]="${WORKSPACE}/src/sn.auto/lwp/build/bvt.dist/release"
DIR_DST[2]=/local/opt/IBM/HTTPServer/htdocs/bvt-${BUILD_STREAM}_${BUILD_COMPONENT}

# Create bvt zip file.
mkdir "${DIR_SRC[2]}"
cd "${DIR_SRC[1]}"
zip -r -q "${DIR_SRC[2]}/bvt.zip" .
if [ $? != 0 ]; then
	echo "Could not create bvt.zip."
	exit 1
fi

DIST_AREA_HOST=connectionsci1.cnx.cwp.pnp-hcl.com
DIST_AREA_USER=icci
COPY_OK="yes"
NUM_DIRS=${#DIR_SRC[@]}
for i in `seq 1 ${NUM_DIRS}`
do
	echo "Copying ${DIR_SRC[i]} to ${DIR_DST[i]} on ${DIST_AREA_HOST}..."
	rsync -av --delete "${DIR_SRC[i]}/" ${DIST_AREA_USER}@${DIST_AREA_HOST}:${DIR_DST[i]}
	if [ $? != 0 ]; then
		echo "Could not copy ${DIR_SRC[i]} to ${DIST_AREA_USER}@${DIST_AREA_HOST}:${DIR_DST[i]}."
		COPY_OK="no"
		continue
	fi
done

if [ "${COPY_OK}" != "yes" ]; then
	exit 1
fi

exit 0
