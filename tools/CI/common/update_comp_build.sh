#!/bin/sh

if [ -f ./was.properties ]; then
  . ./was.properties
fi

BUILD_LABEL=${BUILD_LABEL:-${1}}
TARGET_SERVER=${WAS_HOST_FQDN:-${2}}
WAS_HOST_PWD=${WAS_HOST_PWD:-${3}}

if [ -z "${BUILD_LABEL}" ]; then
	echo "\${BUILD_LABEL} is null."
	exit 1
fi

fail_with_error() {
    echo "$@"
    exit 1
}

BUILD_COMPONENT=`echo ${BUILD_LABEL} | cut -d '_' -f2`

case ${BUILD_COMPONENT} in
	Activities)
		APP_LIST_FOR_INSTALL=activities
		APP_LIST_FOR_RESTART="Activities Common";;

	Blogs)
		APP_LIST_FOR_INSTALL=blogs
		APP_LIST_FOR_RESTART="Blogs Common";;
		
	Bookmarks)
		APP_LIST_FOR_INSTALL=dogear
		APP_LIST_FOR_RESTART="Dogear Common"
		;;
		
	Communities)
		APP_LIST_FOR_INSTALL=communities
		APP_LIST_FOR_RESTART="Communities Common"
		;;
	
	Forums)
		APP_LIST_FOR_INSTALL=forums
		APP_LIST_FOR_RESTART="Forums Common"
		;;

	HomepageNews)
		APP_LIST_FOR_INSTALL=homepage,news
		APP_LIST_FOR_RESTART="Homepage News Common"
		;;
	
	Infra)
		APP_LIST_FOR_INSTALL=common,widgetcontainer
		APP_LIST_FOR_RESTART="Common WidgetContainer"
		POST_UPDATE_SCRIPT=post-update_infra.sh
		POST_UPDATE_SCRIPT_ARGS="${WAS_HOME}/profiles/${WAS_PROFILE}/config/cells/${WAS_CELL}/LotusConnections-config"
		;;

	Moderation)
		APP_LIST_FOR_INSTALL=moderation
		APP_LIST_FOR_RESTART="Moderation Common"
		;;

	Profiles)
		APP_LIST_FOR_INSTALL=profiles
		APP_LIST_FOR_RESTART="Profiles Common"
		;;

	Search)
		APP_LIST_FOR_INSTALL=search
		APP_LIST_FOR_RESTART="Search Common"
		;;

	Share)
		APP_LIST_FOR_INSTALL=files,wikis
		APP_LIST_FOR_RESTART="Files Wikis Common"
		;;

	*)
		fail_with_error "Unkown component: ${BUILD_COMPONENT}"
		;;
esac

echo "Updating lc-update scripts..."
ssh lcuser@${TARGET_SERVER} "cd lc-update; git pull"
[ $? -eq 0 ] || fail_with_error "Unable to update lc-update scripts"

echo "Creating cfg.py for update build..."
ssh lcuser@${TARGET_SERVER} "cd lc-update; cp samples/standalone.py cfg.py; sed -i -e \"s/DB_PASSWORD/$WAS_HOST_PWD/\" cfg.py"
[ $? -eq 0 ] || fail_with_error "Failed to create cfg.py for update build"

echo "Downloading base Connections build..."
ssh lcuser@${TARGET_SERVER} "cd lc-update; bin/get-build.sh -B IC10.0_Connections"
[ $? -eq 0 ] || fail_with_error "Failed download base Connections build"

echo "Downloading build [${BUILD_LABEL}]..."
ssh lcuser@${TARGET_SERVER} "cd lc-update; bin/get-build.sh -f -x -b ${BUILD_LABEL}"
[ $? -eq 0 ] || fail_with_error "Failed download build [${BUILD_LABEL}]"

echo "Updating database..."
ssh lcuser@${TARGET_SERVER} "sudo -i -u db2inst1 ~lcuser/lc-update/bin/fixDB.sh"
[ $? -eq 0 ] || fail_with_error "Failed update database"

echo "Updating build for ${APP_LIST_FOR_INSTALL} on WAS..."
ssh lcuser@${TARGET_SERVER} "cd lc-update; bin/lc-install.sh update ${APP_LIST_FOR_INSTALL}"
[ $? -eq 0 ] || fail_with_error "Failed update builds into WAS"

if [ -n "${POST_UPDATE_SCRIPT}" ]; then
	echo "Copying ${POST_UPDATE_SCRIPT} to WAS..."
	scp ${CI_COMMON_HOME}/${POST_UPDATE_SCRIPT} lcuser@${TARGET_SERVER}:~/lc-update
	[ $? -eq 0 ] || fail_with_error "Failed to copy ${POST_UPDATE_SCRIPT} to WAS"

	echo "Executing: ${POST_UPDATE_SCRIPT} ${POST_UPDATE_SCRIPT_ARGS} on WAS..."
	ssh lcuser@${TARGET_SERVER} "cd lc-update; ./${POST_UPDATE_SCRIPT} ${POST_UPDATE_SCRIPT_ARGS}"
	[ $? -eq 0 ] || fail_with_error "Failed to execute ${POST_UPDATE_SCRIPT} ${POST_UPDATE_SCRIPT_ARGS} on WAS"
fi

echo "Removing Common temp directory..."
ssh lcuser@${TARGET_SERVER} "rm -rf /opt/IBM/WebSphere/AppServer/profiles/AppSrv01/temp/lc45linux1Node01/server1/Common"

echo "Restarting ${APP_LIST_FOR_RESTART}..."
ssh lcuser@${TARGET_SERVER} "cd lc-update; bin/was adm -f bin/restart_app.py ${APP_LIST_FOR_RESTART}"
[ $? -eq 0 ] || fail_with_error "Failed restart applications"
