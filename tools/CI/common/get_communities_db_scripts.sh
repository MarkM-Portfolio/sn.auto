#!/bin/sh

COMMUNITIES_BUILD_LABEL_FILE=$1
COMMUNITIES_BUILD_LABEL=`cat ${COMMUNITIES_BUILD_LABEL_FILE}`
COMMUNITIES_BUILD_STREAM=`echo ${COMMUNITIES_BUILD_LABEL} | cut -d '_' -f1`
COMMUNITIES_BUILD_COMPONENT=`echo ${COMMUNITIES_BUILD_LABEL} | cut -d '_' -f2`

COMMUNITIES_DAILY_BUILD_ROOT_DIR=${DAILY_BUILD_ROOT_DIR}/${COMMUNITIES_BUILD_STREAM}_${COMMUNITIES_BUILD_COMPONENT}

echo "Downloading FEs from [$COMMUNITIES_DAILY_BUILD_ROOT_DIR/${COMMUNITIES_BUILD_LABEL}}..."
fe_path=""
fe_name=""
for fe_path in ${COMMUNITIES_DAILY_BUILD_ROOT_DIR}/${COMMUNITIES_BUILD_LABEL}/repository/* ; do
	fe_name=`basename ${fe_path}`
	
	if [ -d "${TMP}/${fe_name}" ]; then
		echo "deleting directory ${TMP}/${fe_name}..."
		rm -rf "${TMP}/${fe_name}"
	fi
		
	echo "Creating directory ${TMP}/${fe_name}..."
	mkdir ${TMP}/${fe_name}
    
	echo "Unzipping ${fe_path}/fe.zip into ${TMP}/${fe_name}"
    unzip -q -o ${fe_path}/fe.zip -d ${TMP}/${fe_name}
done

COMMUNITIES_DB2_SQLDIR=${TMP}/${fe_name}/lwp/build/comm.sn.install/db.scripts/communities/db2

echo "Copying ${TMP}/${fe_name}/lwp/build/comm.sn.install/db.scripts/communities/db2/*.sql to ${CI_HOME}/${SRC}/${SQLDIR}/db2..."
cp ${TMP}/${fe_name}/lwp/build/comm.sn.install/db.scripts/communities/db2/*.sql  ${CI_HOME}/${SRC}/${SQLDIR}/db2

exit 0
