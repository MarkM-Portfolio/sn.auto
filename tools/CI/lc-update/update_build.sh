#!/bin/sh

. $(dirname $0)/functions.sh
MYSELF=`abs_path $0`
MY_BIN_DIR=`dirname ${MYSELF}`
MY_HOME=`dirname ${MY_BIN_DIR}`
LOGFILE="${MY_HOME}/setup.log"

. ${HOME}/was.properties
if [ $? != 0 ]; then
    echo "Failed to load ${HOME}/was.properties."
    exit 1
fi

RESTART_COMMON_AFTER_APP_RESTART=true
while getopts b:dp: name
do
    case ${name} in
    b)  BUILD_LABEL="${OPTARG}";;
	d)	RESTART_COMMON_AFTER_APP_RESTART=false;;
    p)  WAS_HOST_PWD="${OPTARG}";;
    ?)  usage $0 ;;
    esac
done

if [ -z "${BUILD_LABEL}" ]; then
    echo "\${BUILD_LABEL} is null."
    exit 1
fi

fail_with_error() {
    echo "$@"
    exit 1
}

validate_schema_version() {
	APP=$1
	echo "Validating schema version for ${APP}..." 
	
	SCHEMA_VERSION_UPDATED=`sudo -i -u db2inst1 ${MY_HOME}/bin/create_db.sh -q ${APP}`
	[ $? -eq 0 ] || ( echo "Could not get updated schem version."; return 1	)
	echo "Updated schema version: ${SCHEMA_VERSION_UPDATED}"
	
	SCHEMA_VERSION_EXPECTED=`sudo -i -u db2inst1 ${MY_HOME}/bin/create_db.sh -p ${MY_HOME}/xkit/connections.sql -z ${APP}`
	[ $? -eq 0 ] || ( echo "Could not get expected schem version."; return 1 )
	echo "Expected schema version: ${SCHEMA_VERSION_EXPECTED}"
	
	if [ ! ${SCHEMA_VERSION_UPDATED} -eq ${SCHEMA_VERSION_EXPECTED} ]; then
		echo  "Updated schema version \"${SCHEMA_VERSION_UPDATED}\" doesn't match expected schema version \"${SCHEMA_VERSION_EXPECTED}\"."
		return 1
	fi

	return 0
}

BUILD_COMPONENT=`echo ${BUILD_LABEL} | sed 's/\(.*\)_/\1:/' | cut -d ':' -f1 | sed 's/\(.*\)_/\1:/' | cut -d ':' -f2`

APP_LIST_FOR_DB_FIXUP=
INSTALL_COMMON_AFTER_APP_INSTALL=true

case ${BUILD_COMPONENT} in
    Activities)
        APP_LIST_FOR_INSTALL=activities
        APP_LIST_FOR_DB_FIXUP=${APP_LIST_FOR_INSTALL}
        APP_LIST_FOR_RESTART="Activities"
		;;

    Blogs)
        APP_LIST_FOR_INSTALL=blogs
        APP_LIST_FOR_DB_FIXUP=${APP_LIST_FOR_INSTALL}
        APP_LIST_FOR_RESTART="Blogs"
		;;

    Bookmarks)
        APP_LIST_FOR_INSTALL=dogear
        APP_LIST_FOR_DB_FIXUP=${APP_LIST_FOR_INSTALL}
        APP_LIST_FOR_RESTART="Dogear"
        ;;

    Communities)
        APP_LIST_FOR_INSTALL=communities
        APP_LIST_FOR_DB_FIXUP=communities,calendar
        APP_LIST_FOR_RESTART="Communities"
        ;;

    Forums)
        APP_LIST_FOR_INSTALL=forums
        APP_LIST_FOR_DB_FIXUP=forum
        APP_LIST_FOR_RESTART="Forums"
        ;;

    HomepageNews)
        APP_LIST_FOR_INSTALL=homepage,news
        APP_LIST_FOR_DB_FIXUP=homepage
        APP_LIST_FOR_RESTART="Homepage News"
        ;;
    
	Homepage)
        APP_LIST_FOR_INSTALL=homepage
        APP_LIST_FOR_DB_FIXUP=${APP_LIST_FOR_INSTALL}
        APP_LIST_FOR_RESTART="Homepage"
        ;;

    Infra)
        APP_LIST_FOR_INSTALL=common,widgetcontainer
		INSTALL_COMMON_AFTER_APP_INSTALL=false
        APP_LIST_FOR_RESTART="WidgetContainer Common"
		RESTART_COMMON_AFTER_APP_RESTART=false
        POST_UPDATE_SCRIPT=samples/post-update_infra.sh
        POST_UPDATE_SCRIPT_ARGS="${WAS_HOME}/profiles/${WAS_PROFILE}/config/cells/${WAS_CELL}/LotusConnections-config"
        ;;

    Moderation)
        APP_LIST_FOR_INSTALL=moderation
        APP_LIST_FOR_RESTART="Moderation"
        POST_UPDATE_SCRIPT=samples/post-update_moderation.sh
        POST_UPDATE_SCRIPT_ARGS="${WAS_HOME}/profiles/${WAS_PROFILE}/config/cells/${WAS_CELL}/LotusConnections-config"
        RESTART_WAS="true"
        ;;

    News)
        APP_LIST_FOR_INSTALL=news
        APP_LIST_FOR_RESTART="News"
        ;;
    
    Profiles)
        APP_LIST_FOR_INSTALL=profiles
        APP_LIST_FOR_DB_FIXUP=${APP_LIST_FOR_INSTALL}
        APP_LIST_FOR_RESTART="Profiles"
        ;;

    Search)
        APP_LIST_FOR_INSTALL=search
        APP_LIST_FOR_RESTART="Search"
        ;;

    Share)
        APP_LIST_FOR_INSTALL=files,wikis
        APP_LIST_FOR_DB_FIXUP=${APP_LIST_FOR_INSTALL}
        APP_LIST_FOR_RESTART="Files Wikis"
        ;;

    UI)
        APP_LIST_FOR_INSTALL=common
		INSTALL_COMMON_AFTER_APP_INSTALL=false
        APP_LIST_FOR_RESTART="Common"
		RESTART_COMMON_AFTER_APP_RESTART=false
        POST_UPDATE_SCRIPT=samples/post-update_infra.sh
        POST_UPDATE_SCRIPT_ARGS="${WAS_HOME}/profiles/${WAS_PROFILE}/config/cells/${WAS_CELL}/LotusConnections-config"
        ;;

    *)
        fail_with_error "Unkown component: ${BUILD_COMPONENT}"
        ;;
esac

[ ${INSTALL_COMMON_AFTER_APP_INSTALL} == true ] && APP_LIST_FOR_INSTALL="${APP_LIST_FOR_INSTALL},common,widgetcontainer"
[ ${RESTART_COMMON_AFTER_APP_RESTART} == true ] && APP_LIST_FOR_RESTART="${APP_LIST_FOR_RESTART} Common"

[ -n ${MY_HOME} ] && cd ${MY_HOME}

echo "Creating cfg.py for update build..."
sed -e "s/DB_PASSWORD/$WAS_HOST_PWD/g" samples/standalone.py > cfg.py
[ $? -eq 0 ] || fail_with_error "Failed to create cfg.py for update build"

sed -i -e "s/WAS_PASSWORD/$WAS_HOST_PWD/g" cfg.py
[ $? -eq 0 ] || fail_with_error "Failed to edit cfg.py for update build"

if [ -n "${APP_LIST_FOR_DB_FIXUP}" ] ; then
    echo "Updating database..."
    sudo -i -u db2inst1 ${MY_HOME}/bin/fixDB.sh ${APP_LIST_FOR_DB_FIXUP}
    [ $? -eq 0 ] || fail_with_error "Failed to update database(s)"
	
	echo "Validating schema version(s)..."
	APP_LIST_FOR_DB_VALIDATE=`echo ${APP_LIST_FOR_DB_FIXUP} | sed "s/,/ /g"`
	for APP in ${APP_LIST_FOR_DB_VALIDATE}
	do
		validate_schema_version ${APP} || fail_with_error "Schema version validation failed."
	done
else
    echo "No databases need to be updated."
fi

echo "Updating build for ${APP_LIST_FOR_INSTALL} on WAS..."
${MY_HOME}/bin/lc-install.sh update ${APP_LIST_FOR_INSTALL}
[ $? -eq 0 ] || fail_with_error "Failed update builds into WAS"

if [ -n "${POST_UPDATE_SCRIPT}" ]; then
    echo "Executing: ${MY_HOME}/${POST_UPDATE_SCRIPT} ${POST_UPDATE_SCRIPT_ARGS} on WAS..."
    ${MY_HOME}/${POST_UPDATE_SCRIPT} ${POST_UPDATE_SCRIPT_ARGS}
    [ $? -eq 0 ] || fail_with_error "Failed to execute ${MY_HOME}/${POST_UPDATE_SCRIPT} ${POST_UPDATE_SCRIPT_ARGS} on WAS"
fi

echo "Removing Common temp directory..."
rm -rf /opt/IBM/WebSphere/AppServer/profiles/AppSrv01/temp/lc45linux1Node01/server1/Common

if [ "${RESTART_WAS}" == "true" ]; then
    echo "Restarting WAS..."
    ${MY_HOME}/bin/was stop
    [ $? -eq 0 ] || fail_with_error "Failed to stop WAS"

    ${MY_HOME}/bin/was start
    [ $? -eq 0 ] || fail_with_error "Failed to start WAS"
else
    echo "Restarting ${APP_LIST_FOR_RESTART}..."
    ${MY_HOME}/bin/was adm -f ${MY_HOME}/bin/restart_app.py ${APP_LIST_FOR_RESTART}
    [ $? -eq 0 ] || fail_with_error "Failed restart applications"
fi
