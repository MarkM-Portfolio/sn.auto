#!/bin/sh

. ${CI_COMMON_HOME}/system.properties
if [ $? != 0 ]; then
	echo "Failed to load ${CI_COMMON_HOME}/system.properties."
	exit 1
fi

# Download the BVT zip file from distribution area.
# Retry if the download fails, since that could be the result
# of the distribution area being updated while we're downloading.
# Make sure there's a clean directory to download the bvt zip file.
if [ -d "${CI_BVT_DIST_DIR}" ]; then
	echo "Deleting directory "${CI_BVT_DIST_DIR}"..."
	rm -rf "${CI_BVT_DIST_DIR}"
	if [ $? != 0 ]; then
		"Could not delete directory "${CI_BVT_DIST_DIR}"."
		exit 1
	fi
fi

echo "Creating directory ${CI_BVT_DIST_DIR}..."
mkdir -p ${CI_BVT_DIST_DIR}
if [ $? != 0 ]; then
	"Could not create directory ${CI_BVT_DIST_DIR}."
	exit 1
fi


index=0
OK=false
while true
do
	echo "Downloading BVT zip file from ${BUILD_STREAM}_Automation stream to ${CI_BVT_DIST_DIR}..."
	curl http://connectionsci1.cnx.cwp.pnp-hcl.com/bvt-${BUILD_STREAM}_Automation/bvt.zip -o ${CI_BVT_DIST_DIR}/bvt.zip
	if [ $? == 0 ]; then
		echo "Download complete."
		OK=true
		break
	fi
		
	index=`expr ${index} + 1`
	if [ ${index} -lt 6 ]; then
		echo "Download failed, will retry in 10 seconds..."
		sleep 10
		continue
	fi

	echo "Download failed, giving up..."
	break
done

if [ ${OK} != true ]; then
	exit 1
fi

cd ${CI_BVT_DIST_DIR}

# Unzip the BVT zip file.
echo "Unzipping ${CI_BVT_DIST_DIR}/bvt.zip..."
unzip -q bvt.zip
if [ $? != 0 ]; then
	echo "Could not unzip ${CI_BVT_DIST_DIR}/bvt.zip."
	exit 1
fi

# Remove the zip file.
echo "Removing ${CI_BVT_DIST_DIR}/bvt.zip..."
rm -f bvt.zip

cd -
