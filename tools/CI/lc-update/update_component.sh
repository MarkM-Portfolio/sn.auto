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

while getopts b:p: name
do
    case ${name} in
    b)  BUILD_LABEL="${OPTARG}";;
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

BUILD_COMPONENT=`echo ${BUILD_LABEL} | cut -d '_' -f2`

APP_LIST_FOR_DB_FIXUP=

case ${BUILD_COMPONENT} in
    Activities)
        APP_LIST_FOR_INSTALL=activities
        APP_LIST_FOR_DB_FIXUP=${APP_LIST_FOR_INSTALL}
        APP_LIST_FOR_RESTART="Activities Common";;

    Blogs)
        APP_LIST_FOR_INSTALL=blogs
        APP_LIST_FOR_DB_FIXUP=${APP_LIST_FOR_INSTALL}
        APP_LIST_FOR_RESTART="Blogs Common";;

    Bookmarks)
        APP_LIST_FOR_INSTALL=dogear
        APP_LIST_FOR_DB_FIXUP=${APP_LIST_FOR_INSTALL}
        APP_LIST_FOR_RESTART="Dogear Common"
        ;;

    Communities)
        APP_LIST_FOR_INSTALL=communities
        APP_LIST_FOR_DB_FIXUP=communities,calendar
        APP_LIST_FOR_RESTART="Communities Common"
        ;;

    Forums)
        APP_LIST_FOR_INSTALL=forums
        APP_LIST_FOR_DB_FIXUP=forum
        APP_LIST_FOR_RESTART="Forums Common"
        ;;

    HomepageNews)
        APP_LIST_FOR_INSTALL=homepage,news
        APP_LIST_FOR_DB_FIXUP=homepage
        APP_LIST_FOR_RESTART="Homepage News Common"
        ;;

    Infra)
        APP_LIST_FOR_INSTALL=common,widgetcontainer
        APP_LIST_FOR_RESTART="Common WidgetContainer"
        POST_UPDATE_SCRIPT=samples/post-update_infra.sh
        POST_UPDATE_SCRIPT_ARGS="${WAS_HOME}/profiles/${WAS_PROFILE}/config/cells/${WAS_CELL}/LotusConnections-config"
        ;;

    Moderation)
        APP_LIST_FOR_INSTALL=moderation
        APP_LIST_FOR_RESTART="Moderation Common"
        POST_UPDATE_SCRIPT=samples/post-update_moderation.sh
        POST_UPDATE_SCRIPT_ARGS="${WAS_HOME}/profiles/${WAS_PROFILE}/config/cells/${WAS_CELL}/LotusConnections-config"
        RESTART_WAS="true"
        ;;

    Profiles)
        APP_LIST_FOR_INSTALL=profiles
        APP_LIST_FOR_DB_FIXUP=${APP_LIST_FOR_INSTALL}
        APP_LIST_FOR_RESTART="Profiles Common"
        ;;

    Search)
        APP_LIST_FOR_INSTALL=search
        APP_LIST_FOR_RESTART="Search Common"
        ;;

    Share)
        APP_LIST_FOR_INSTALL=files,wikis
        APP_LIST_FOR_DB_FIXUP=${APP_LIST_FOR_INSTALL}
        APP_LIST_FOR_RESTART="Files Wikis Common"
        ;;

    *)
        fail_with_error "Unkown component: ${BUILD_COMPONENT}"
        ;;
esac

[ -n ${MY_HOME} ] && cd ${MY_HOME}

echo "Creating cfg.py for update build..."
sed -e "s/DB_PASSWORD/$WAS_HOST_PWD/" samples/standalone.py > cfg.py
[ $? -eq 0 ] || fail_with_error "Failed to create cfg.py for update build"

echo "Downloading base Connections build..."
${MY_HOME}/bin/get-build.sh -B IC10.0_Connections
[ $? -eq 0 ] || fail_with_error "Failed download base Connections build"

echo "Downloading build [${BUILD_LABEL}]..."
${MY_HOME}/bin/get-build.sh -f -x -b ${BUILD_LABEL}
[ $? -eq 0 ] || fail_with_error "Failed download build [${BUILD_LABEL}]"

if [ -n "${APP_LIST_FOR_DB_FIXUP}" ] ; then
    echo "Updating database..."
    sudo -i -u db2inst1 ${MY_HOME}/bin/fixDB.sh ${APP_LIST_FOR_DB_FIXUP}
    [ $? -eq 0 ] || fail_with_error "Failed update database"
else
    echo "No database need to be updated."
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
