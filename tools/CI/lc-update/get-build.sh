#!/bin/sh
# ***************************************************************** 
#                                                                   
# IBM Confidential                                                  
#                                                                   
# OCO Source Materials                                              
#                                                                   
# Copyright IBM Corp. 2011, 2013                                    
#                                                                   
# The source code for this program is not published or otherwise    
# divested of its trade secrets, irrespective of what has been      
# deposited with the U.S. Copyright Office.                         
#                                                                   
# ***************************************************************** 

#
# Find the differences between 2 LotusConnections Builds
#

#BUILD_LOCATION="/net/mlsa2/ibm/releng/workplace/dailykits"
BUILD_LOCATION="${BUILD_LOCATION:-/net/mlsa2/ibm/releng/workplace/dailybuilds,/net/mlsa2.swg.usma.ibm.com/ibm/releng/workplace/dailybuilds}"
KNOWN_STREAMS="IC10.0 IC20.0 IC30.0 IC40.0"
FE_DOWNLOADED=""

# to trim blanks away from a shell variable
# my_var=`trim $my_var`
trim() {
    echo $1
}

build_location_exists() {
    BUILD_LOCATION=`echo ${BUILD_LOCATION} | sed s]',']' ']g`
    for location in ${BUILD_LOCATION}
    do
        echo "Trying ${location}..."
        ls ${location} >/dev/null 2>&1
        if [ -d "${location}" ] ; then
            BUILD_LOCATION=${location}
            return 0
        fi

        echo "Unable to access the build location: ${location}"
    done

    return 1
}

list_builds_of_stream() {
    local stream=$1
    stream=${stream:-LCI4.5}
    local ls_args="-d ${BUILD_LOCATION}/${stream}/${stream}_*"
    for bld in `ls $ls_args 2>/dev/null`; do
        # remove leading part of the path, keep only the build
        bld=${bld#${BUILD_LOCATION}}
        bld=${bld#/}
        if [ -z "${BUILDS_LIST}" ]; then
            BUILDS_LIST="${bld}"
        else
            BUILDS_LIST="${BUILDS_LIST} ${bld}"
        fi
    done
}

dump_builds_list() {
    echo "Available builds are:"
    for i in ${BUILDS_LIST} ; do
        echo "    ${i}"
    done
}

check_build() {
    echo "Check availability of stream [${STREAM}], build ${BUILD} ..."
    if [ ! -d "${BUILD_LOCATION}/${STREAM}/${BUILD}" ]; then
        echo "FAIL"
        echo "The build ${BUILD} is not available on ${BUILD_LOCATION}"
        dump_builds_list
        exit 1
    fi
    if [ ! -f "${BUILD_LOCATION}/${STREAM}/${BUILD}/MASTERED.sem" ]; then
        echo "WARNING"
        echo "The build ${BUILD} is not fully mastered yet"
    fi
    return 0
}

list_builds_of_all_known_streams() {
    local stream=""
    for stream in ${KNOWN_STREAMS} ; do
        list_builds_of_stream ${stream}
    done
}


get_current_build_name_of_stream() {
    local stream=$1
    local bld_label_file="${BUILD_LOCATION}/${stream}/currentBuildLabel.txt"
    echo Checking build label file: $bld_label_file
    if [ ! -f ${bld_label_file} ]; then
        BUILD=""
        return 1
    fi
    BUILD=`cat ${bld_label_file} | tr -d '[\n\r]'`
    BUILD=`trim $BUILD`
    BUILD="${stream}/${BUILD}"
}

get_latest_build_name_of_stream() {
    local stream=$1
    local latest_build=""
    local tmp=0
    list_builds_of_stream $stream
    for bld in ${BUILDS_LIST} ; do
        if [ -z "$latest_build" ]; then
            latest_build=$bld
        else
            if [ "1" = `expr $bld \> $latest_build` -a -f "${BUILD_LOCATION}/${bld}/MASTERED.sem" ]; then
                latest_build=$bld
            fi
        fi
    done
    BUILD=$latest_build
}


figure_out_webkit_file() {
    local bld=$1
    local name_prefix=$2 # IBM_Connections
    local name_suffix=$3 # _wizards

    # guess out the version number used in the file name
    #local num_in_file_name=`echo $bld | sed -e 's/^.*\/\([A-Z]\+\)\([0-9\|.]*\)_\(.*\)/\2/'`
    #echo "num used in file name = ${num_in_file_name}"
    #local web_kit_file="IBM_Connections_${num_in_file_name}"
    #local wizards_file="IBM_Connections_${num_in_file_name}"

    local pattern="${name_prefix}_[0-9\.]\+${name_suffix}"
    case `uname` in
    CYGWIN*)
        pattern="${pattern}_win.exe";;
    AIX)
        pattern="${pattern}_aix\.tar";;
    *)
        pattern="${pattern}_.lin\.tar";;
    esac
    local filename=""
    local n_files=0
    [ -d "${BUILD_LOCATION}/${bld}/WebKits/IBM_Connections" ] || return 1
    echo ls ${BUILD_LOCATION}/${bld}/WebKits/IBM_Connections/ | grep -e "^${pattern}\$"
    filename=`ls ${BUILD_LOCATION}/${bld}/WebKits/IBM_Connections/ | grep -e "^${pattern}\$"`
    n_files=`echo $filenames | wc -l`
    if [ ${n_files} -ne 1 ]; then
        echo "${n_files} files matches pattern ${web_kit_file}, crazy kit!!"
        return 1
    fi
    echo "${BUILD_LOCATION}/${bld}/WebKits/IBM_Connections/${filename}"
    exit 1
}

#
# retrive_fe bld_path [fe_name [files_to_extract]]
#
# if fe_names is not specified, unzip all the FEs found in the build
# if files_to_extract is not specified, unzip all the files
#
retrieve_fe() {
    local bld_path=$1
    shift
    local fe_name=$1
    shift
    local files_to_extract="$*"
    local fe_list=""
    local fe_zip=""
    local has_errors=0

    if [ -n "$fe_name" ]; then
        fe_list="${fe_name}"
    else
        fe_list=`ls ${bld_path}/repository/`
    fi
    for fe_name in ${fe_list} ; do
        fe_zip="${bld_path}/repository/${fe_name}/fe.zip"
        if [ ! -f "${fe_zip}" ]; then
            echo "FE.zip file not found for [$fe_name]: [$fe_zip]."
            continue
        fi

        echo "unzip -o -q \"${fe_zip}\" -d \"${fe_name}\" ${files_to_extract}"
        unzip -o -q "${fe_zip}" -d "${fe_name}" ${files_to_extract}
        if [ $? -ne 0 ] ; then
            has_errors=1
        else
            FE_DOWNLOADED="${FE_DOWNLOADED}${FE_DOWNLOADED:+ }${fe_name}"
        fi
    done
    return ${has_errors}
}

retrieve_xkit_type_of_kit() {
    local bld_path=$1
    rm -rf sn.live lc.live.installer lwp
    retrieve_fe ${bld_path} sn.live
    [ -e xkit ] && rm -rf xkit
    if [ -d  "sn.live/lwp/build/lc.live.installer/lc.live.installer/xkit" ] ; then
        ln -sf sn.live/lwp/build/lc.live.installer/lc.live.installer/xkit xkit
    elif [ -d "sn.live/lwp/build/lc.live.installer/xkit" ] ; then
        ln -sf  sn.live/lwp/build/lc.live.installer/xkit xkit
    else
        echo "ERROR: can not find xkit in sn.live/lwp/build/lc.live.installer"
        return 1
    fi

    curDir=`pwd`
    if [ -d "sn.live/lwp/build/lc.mt.config/mtconfig" ] ; then
        cd xkit || return 1
        rm -fr LotusConnections-config.mt
        ln -sf ${curDir}/sn.live/lwp/build/lc.mt.config/mtconfig/LotusConnections-config.mt
        ln -sf ${curDir}/sn.live/lwp/build/lc.mt.config/mtconfig/bss
        cd connections.sql
        ln -s ${curDir}/sn.live/lwp/build/lc.mt.config/mtconfig/connections.sql/cloud
        cd ${curDir}
    elif [ -d "mtconfig" ] ; then
        cd xkit
        ln -sf ${curDir}/mtconfig/LotusConnections-config.mt
        ln -sf ${curDir}/mtconfig/bss
        cd connections.sql
        ln -sf ${curDir}/mtconfig/connections.sql/cloud
        cd ${curDir}
    fi

    # workaround the Push Notification database script location issue,
    # they should be in connections.sql/pushnotification rather then files
    if [ -f "xkit/connections.sql/files/db2/pns-createDb.sql" ] ; then
        echo "Found pns-createDb.sql under files, apply workaround to move them up"
        echo "to pushnotifications"
        for dbdir in db2 oracle sqlserver ; do
            mkdir -p "xkit/connections.sql/pushnotification/$dbdir"
            for f in xkit/connections.sql/files/$dbdir/pns-* ; do
                local new_name=`basename $f`
                new_name=${new_name#pns-}
                local new_file="xkit/connections.sql/pushnotification/$dbdir/$new_name"
                cp -r "$f" "xkit/connections.sql/pushnotification/$dbdir/$new_name"
            done
        done
    fi
    return 0
}

retrieve_rim_kit() {
    local errors=0
    local bld_path=$1
    local os_name=`uname`
    local tmp_path="${bld_path}/setup/IBM_Connections_Install_${os_name}"
    if [ -d "$tmp_path" ]; then
        echo "Copy kit from: ${tmp_path}"
        rsync -az --delete --chmod=u+rwx "${tmp_path}/" IBM_Connections_Install
    elif [ -d "${bld_path}/setup/IBM_Connections_Install" ]; then
        tmp_path="${bld_path}/setup/IBM_Connections_Install"
        rsync -az --delete --chmod=u+rwx "${tmp_path}/" IBM_Connections_Install
    else
        local web_kit_file=`figure_out_webkit_file ${bld} IBM_Connections`
        if [ $? -ne 0 -o -z "${web_kit_file}" ]; then
            errors=1
        else
            echo "Extracting: ${web_kit_file}"
            tar xf ${web_kit_file} || errors=1
        fi
    fi

    rm -rf Wizards
    local wizards_path=${BUILD_LOCATION}/${bld}/setup/Wizards
    if [ -d "${wizards_path}" ]; then
        echo "found Wizards in setup directory"
        #ln -sf "${wizards_path}" .
        rsync -az --delete --chmod=u+rwx "${wizards_path}/" Wizards
    else
        local wizards_file=`figure_out_webkit_file ${bld} IBM_Connections _wizards`
        if [ $? -ne 0 -o -z "${wizards_file}" ]; then
            errors=1
        else
            echo Extracting: ${wizards_file}
            tar xf ${wizards_file} || errors=1
        fi
    fi
    return $errors
}

retrieve_build() {
    local bld=$1
    local save_to="$2"
    local errors=0
    local curr_dir="$PWD"

    save_to="${save_to:-${curr_dir}}"
    local kit_version=""
    if [ -f "$save_to/.kitversion" ] ; then
        kit_version=`cat "$save_to/.kitversion"`
    fi
    if [ "$kit_version" = "$bld" ]; then
        echo "Already obtained kit for $bld, skip download"
        return 0
    fi

    # download the webkit
    #rsync ${BUILD_LOCATION}/${bld}/WebKits/IBM_Connections/${web_kit_file} .\
    # extract webkit
    if [ ! -d "$save_to" ]; then mkdir "$save_to" ; fi
    cd "$save_to"

    # remove existing files to avoid conflict
    rm -f .kitversion
    if [ -d "${BUILD_LOCATION}/${bld}/repository/sn.live" ] ; then
        echo "The build [${bld}] is Connections core installation kit, nice!"
        retrieve_xkit_type_of_kit ${BUILD_LOCATION}/${bld} || errors=1
    elif retrieve_rim_kit ${BUILD_LOCATION}/${bld} ; then
        errors=0
    elif [ ! -d "${BUILD_LOCATION}/${bld}/repository" -a -d "${BUILD_LOCATION}/${bld}/lwp04.tools"  ] ; then
        echo "Looks this is a tools build"
        rsync -az --delete --exclude Java60 --exclude JREs --delete-excluded --chmod=u+rwx \
          ${BUILD_LOCATION}/${bld}/lwp04.tools/ lwp04.tools
    else
        echo "Build [${bld}] not a installation kit build, will download all the FEs in it."
        retrieve_fe ${BUILD_LOCATION}/${bld} || errors=1
    fi
    if [ $errors -eq 0 ]; then
        echo ${bld} > .kitversion
    fi
    cd "$curr_dir"
    if [ "$f_updt_xkit_dir" = 1 ] ; then
        update_xkit_directory "$save_to"
        # Workaround: some FE's seem have their output file with permission 400, that prevents
        # db2 user to run scripts own by lcsuer, just to enusre the kit is world-wide readable
        chmod -R a+r xkit
    fi
    return $errors
}

#
# update_xkit_directory build_download_dir
#   collect files from component build download into the xkit directory
#
update_xkit_directory() {
    for fe in ${FE_DOWNLOADED}; do
        # Note: ${fe/./_} only works with Bash, not work in bsh.
        collector=collect_from_`echo $fe | tr '.' '_'`
        $collector "$*"
    done
}

collect_from_activities_impl() {
    local from_dir="$1/activities.impl/lwp"
    local dirs="installableApps activities.provision.web LotusConnections-config \
          connections.sql bin_lc_admin"
    for d in $dirs ; do [ -d "xkit/$d" ] || mkdir -p "xkit/$d" ; done
    cp -v "${from_dir}"/build/oa.ear/ear.prod/lib/oa.ear xkit/installableApps/

    rm -v xkit/activities.provision.web/com.ibm.lconn.activities.web.resources_*.jar
    cp -v "${from_dir}"/build/oa.web.resources/eclipse/plugins/com.ibm.lconn.activities.web.resources_*.jar xkit/activities.provision.web

    rsync -r "${from_dir}"/build/oa.install/framework/extensions/Product/db.sql/ \
       xkit/connections.sql/activities
    cp -v "${from_dir}"/build/oa.svc/core/conf/properties/oa-config.xml \
       xkit/LotusConnections-config/
    cp -v "${from_dir}"/build/oa.svc/core/conf/properties/oa-config.xsd \
       xkit/LotusConnections-config/
    cp -v "${from_dir}"/build/oa.install/was6/scripts/jython/activitiesAdmin.py \
       xkit/bin_lc_admin
}

collect_from_activities_api() {
    echo "Nothing to collect from activities.api"
}

collect_from_sn_appregui() {
    echo "Nothing to collect from sn.appregui"
}

collect_from_sn_blogs() {
    local from_dir="$1/sn.blogs/lwp"
    local dirs="installableApps blogs.provision.web LotusConnections-config \
          connections.sql bin_lc_admin"
    for d in $dirs ; do [ -d "xkit/$d" ] || mkdir -p "xkit/$d" ; done
    cp -v "${from_dir}"/build/blogs.ear/ear.prod/lib/blogs.ear xkit/installableApps/

    rm -v xkit/blogs.provision.web/com.ibm.lconn.blogs.web.resources_*.jar
    cp -v "${from_dir}"/build/blogs.web.resources/eclipse/plugins/com.ibm.lconn.blogs.web.resources_*.jar xkit/blogs.provision.web

    rm -v xkit/blogs.provision.web/com.ibm.lconn.communityblogs.web.resources_*.jar
    cp -v "${from_dir}"/build/blogs.web.resources/eclipse/plugins/com.ibm.lconn.communityblogs.web.resources_*.jar xkit/blogs.provision.web

    rsync -r "${from_dir}"/build/blogs.sn.install/db.scripts/blogs/ \
       xkit/connections.sql/blogs
    cp -v "${from_dir}"/blogs.sn.install/scripts/jython/blogsAdmin.py \
       xkit/bin_lc_admin
}

collect_from_sn_comm() {
    local from_dir="$1/sn.comm/lwp"
    local dirs="installableApps communities.provision.web LotusConnections-config \
          connections.sql bin_lc_admin"
    for d in $dirs ; do [ -d "xkit/$d" ] || mkdir -p "xkit/$d" ; done
    cp -v "${from_dir}"/build/comm.ear/lib/communities.ear xkit/installableApps/

    rm -v xkit/communities.provision.web/com.ibm.lconn.communities.web.resources_*.jar
    cp -v "${from_dir}"/build/comm.web.resources/eclipse/plugins/com.ibm.lconn.communities.web.resources_*.jar xkit/communities.provision.web

    rm -v xkit/communities.provision.web/com.ibm.lconn.recomm.web.resources_3.0.0.20131211-2211.jar
    cp -v "${from_dir}"/build/recomm.web.resources/eclipse/plugins/com.ibm.lconn.recomm.web.resources_*.jar xkit/communities.provision.web

    rsync -r "${from_dir}"/build/comm.sn.install/db.scripts/communities/ \
       xkit/connections.sql/communities
    cp -v "${from_dir}"/comm.install/install/communities-config.xml \
       xkit/LotusConnections-config/
    cp -v "${from_dir}"/comm.install/install/communities-config.xsd \
       xkit/LotusConnections-config/
    cp -v "${from_dir}"/comm.install/install/scripts/jython/communitiesAdmin.py \
       xkit/bin_lc_admin
}

collect_from_dogear() {
    local from_dir="$1/dogear/lwp"
    local dirs="installableApps dogear.provision.web LotusConnections-config \
          connections.sql bin_lc_admin"
    for d in $dirs ; do [ -d "xkit/$d" ] || mkdir -p "xkit/$d" ; done
    cp -v "${from_dir}"/build/dogear.ear/ear.prod/lib/dogear.ear xkit/installableApps/

    rm -v xkit/dogear.provision.web/com.ibm.lconn.dogear.web.resources_*.jar
    cp -v "${from_dir}"/build/dogear.web.resources/eclipse/plugins/com.ibm.lconn.dogear.web.resources_*.jar xkit/dogear.provision.web

    rsync -r "${from_dir}"/build/dogear.install/framework/dogear_install/database/production/ \
       xkit/connections.sql/dogear
    cp -v "${from_dir}"/build/dogear.install/framework/dogear_install/dogear-config-cell.xml \
       xkit/LotusConnections-config/
    cp -v "${from_dir}"/build/dogear.install/framework/dogear_install/dogear-config-cell.xsd \
       xkit/LotusConnections-config/
    cp -v "${from_dir}"/build/dogear.install/framework/dogear_install/scripts/jython/dogearAdmin.py \
       xkit/bin_lc_admin
}

collect_from_sn_forum() {
    local from_dir="$1/sn.forum/lwp"
    local dirs="installableApps forums.provision.web LotusConnections-config \
          connections.sql bin_lc_admin"
    for d in $dirs ; do [ -d "xkit/$d" ] || mkdir -p "xkit/$d" ; done
    cp -v "${from_dir}"/build/forum.ear/lib/forums.ear xkit/installableApps/

    rm -v xkit/forums.provision.web/com.ibm.lconn.forums.web.resources_*.jar
    cp -v "${from_dir}"/build/forum.web.resources/eclipse/plugins/com.ibm.lconn.forums.web.resources_*.jar xkit/forums.provision.web

    rsync -r "${from_dir}"/build/forum.install/framework/db.scripts/forum/ \
       xkit/connections.sql/forum
    cp -v "${from_dir}"/build/forum.conf/properties/forum-config.xml \
       xkit/LotusConnections-config/
    cp -v "${from_dir}"/build/forum.conf/properties/forum-config.xsd \
       xkit/LotusConnections-config/
    cp -v "${from_dir}"/build/forum.conf/properties/forum-policy.xml \
       xkit/LotusConnections-config/
    cp -v "${from_dir}"/build/forum.conf/properties/forum-policy.xsd \
       xkit/LotusConnections-config/
    cp -v "${from_dir}"/build/forum.conf/properties/forumsAdmin.py \
       xkit/bin_lc_admin
}

collect_from_sn_homepage() {
    local from_dir="$1/sn.homepage/lwp"
    local dirs="installableApps homepage.provision.web LotusConnections-config \
          connections.sql bin_lc_admin"
    for d in $dirs ; do [ -d "xkit/$d" ] || mkdir -p "xkit/$d" ; done
    cp -v "${from_dir}"/build/dboard.ear/ear.prod/lib/dboard.ear xkit/installableApps/

    rm -v xkit/homepage.provision.web/com.ibm.lconn.homepage.web.resources_*.jar
    cp -v "${from_dir}"/build/homepage.web.resources/eclipse/plugins/com.ibm.lconn.homepage.web.resources_*.jar xkit/homepage.provision.web

    rsync -r "${from_dir}"/build/dboard.sn.install/db.scripts/homepage/ \
       xkit/connections.sql/homepage
    cp -v "${from_dir}"/build/homepage.web.layer/core/config.files/gettingstarted-config.xml \
       xkit/LotusConnections-config/
    cp -v "${from_dir}"/build/homepage.web.layer/core/config.files/gettingstarted-config.xsd \
       xkit/LotusConnections-config/
    cp -v "${from_dir}"/dboard.sn.install/scripts/jython/homepageAdmin.py \
       xkit/bin_lc_admin
}

collect_from_sn_infra() {
    local from_dir="$1/sn.infra/lwp"
    local dirs="installableApps common.provision.web LotusConnections-config \
          connections.sql bin_lc_admin"
    for d in $dirs ; do [ -d "xkit/$d" ] || mkdir -p "xkit/$d" ; done
    cp -v "${from_dir}"/build/lc.shindig.serverapi/ear/lib/widget.container.ear xkit/installableApps/
    rsync -r "${from_dir}"/build/lc.common.db/db.scripts/common/ \
       xkit/connections.sql/common

    cp -v "${from_dir}"/build/lc.config.svc/config.files/opensocial-config.xml \
       xkit/LotusConnections-config/
    cp -v "${from_dir}"/build/lc.config.svc/config.files/opensocial-config.xsd \
       xkit/LotusConnections-config/
    cp -v "${from_dir}"/build/lc.config.svc/config.files/XRDS.xml \
       xkit/LotusConnections-config/
    cp -v "${from_dir}"/build/lc.config.svc/config.files/XRDS.xsd \
       xkit/LotusConnections-config/
    cp -v "${from_dir}"/build/lc.config.svc/config.files/XRD.xsd \
       xkit/LotusConnections-config/
    cp -v "${from_dir}"/build/lc.config.svc/config.files/LotusConnections-config.xml \
       xkit/LotusConnections-config/
    cp -v "${from_dir}"/build/lc.config.svc/config.files/LotusConnections-config.xsd \
       xkit/LotusConnections-config/
    cp -v "${from_dir}"/build/lc.config.svc/config.files/media-gallery-config.xml \
       xkit/LotusConnections-config/
    cp -v "${from_dir}"/build/lc.config.svc/config.files/media-gallery-config.xsd \
       xkit/LotusConnections-config/
    cp -v "${from_dir}"/build/lc.config.svc/config.files/widgets-config.xml \
       xkit/LotusConnections-config/
    cp -v "${from_dir}"/build/lc.config.svc/config.files/widgets-config.xsd \
       xkit/LotusConnections-config/
    cp -v "${from_dir}"/build/lc.config.svc/config.files/proxy-config.tpl \
       xkit/LotusConnections-config/
    cp -v "${from_dir}"/build/lc.config.svc/config.files/proxy-config.xsd \
       xkit/LotusConnections-config/
    cp -v "${from_dir}"/build/lc.config.svc/config.files/service-location.xsd \
       xkit/LotusConnections-config/
    cp -v "${from_dir}"/build/lc.config.svc/config.files/uiextensions-config.xml \
       xkit/LotusConnections-config/
    cp -v "${from_dir}"/build/lc.config.svc/config.files/uiextensions-config.xsd \
       xkit/LotusConnections-config/
    cp -v "${from_dir}"/build/lc.config.svc/config.files/contentreview-config.xml \
       xkit/LotusConnections-config/
    cp -v "${from_dir}"/build/lc.config.svc/config.files/contentreview-config.xsd \
       xkit/LotusConnections-config/
    cp -v "${from_dir}"/build/lc.config.svc/config.files/contentreview-config-i18n.xsd \
       xkit/LotusConnections-config/
    cp -v "${from_dir}"/build/lc.config.svc/config.files/library-config.xml \
       xkit/LotusConnections-config/
    cp -v "${from_dir}"/build/lc.config.svc/config.files/library-config.xsd \
       xkit/LotusConnections-config/
    cp -v "${from_dir}"/build/lc.config.svc/config.files/file-preview-config.xml \
       xkit/LotusConnections-config/
    cp -v "${from_dir}"/build/lc.config.svc/config.files/file-preview-config.xsd \
       xkit/LotusConnections-config/
    cp -v "${from_dir}"/build/lc.config.svc/config.files/AS.Gadget.extension.cfg \
       xkit/LotusConnections-config/
    cp -v "${from_dir}"/build/lc.config.svc/config.files/AS.Gadget.oauth.cfg \
       xkit/LotusConnections-config/
    cp -v "${from_dir}"/build/lc.config.svc/config.files/AS.Gadget.proxy.cfg \
       xkit/LotusConnections-config/
    cp -v "${from_dir}"/build/lc.config.svc/config.files/EE.Gadget.extension.cfg \
       xkit/LotusConnections-config/
    cp -v "${from_dir}"/build/lc.config.svc/config.files/EE.Gadget.oauth.cfg \
       xkit/LotusConnections-config/
    cp -v "${from_dir}"/build/lc.config.svc/config.files/EE.Gadget.proxy.cfg \
       xkit/LotusConnections-config/
    cp -v "${from_dir}"/build/lc.config.svc/config.files/hystrix-config.properties \
       xkit/LotusConnections-config/

    cp -v "${from_dir}"/lc.config.svc/config.scripts/jython/connectionsConfig.py \
       xkit/bin_lc_admin
    cp -v "${from_dir}"/lc.config.svc/config.scripts/jython/commonConnections.py \
       xkit/bin_lc_admin
    cp -v "${from_dir}"/lc.config.svc/config.scripts/jython/lotusConnectionsCommonAdmin.py \
       xkit/bin_lc_admin
    cp -v "${from_dir}"/lc.config.svc/config.scripts/jython/gadgetAdmin.py \
       xkit/bin_lc_admin
    cp -v -r "${from_dir}/build/lc.config.svc/config.files/00000000-0000-0000-0000-000000000000" \
       "xkit/configuration"
}

collect_from_sn_infra_ui() {
    local from_dir="$1/sn.infra.ui/lwp"
    local dirs="installableApps common.provision.web LotusConnections-config \
          connections.sql bin_lc_admin"
    for d in $dirs ; do [ -d "xkit/$d" ] || mkdir -p "xkit/$d" ; done
    cp -v "${from_dir}"/build/lc.common.ear/lib/connections.common.ear xkit/installableApps/

    rm -v xkit/common.provision.web/com.ibm.lconn.oauth.web.resources_*.jar
    cp -v "${from_dir}"/build/oauth.web.resources/eclipse/plugins/com.ibm.lconn.oauth.web.resources_*.jar xkit/common.provision.web

    rm -v xkit/common.provision.web/com.ibm.social.gen4.theme_*.jar
    cp -v "${from_dir}"/build/oneui.web.resources/eclipse/plugins/com.ibm.social.gen4.theme_*.jar xkit/common.provision.web

    ##### JS testing Prereq bundles
	rm -vr xkit/common.provision.web/org.junit_4.8.1.*
	cp -vr "${from_dir}"/prereqs.web.resources/eclipse/plugins/org.junit_4.8.1.* xkit/common.provision.web
		
	rm -v xkit/common.provision.web/org.hamcrest.core_*.jar
	cp -v "${from_dir}"/prereqs.web.resources/eclipse/plugins/org.hamcrest.core_*.jar xkit/common.provision.web
		
    rm -v xkit/common.provision.web/org.mozilla.javascript_*.jar
	cp -v "${from_dir}"/prereqs.web.resources/eclipse/plugins/org.mozilla.javascript_*.jar xkit/common.provision.web
		
    rm -v xkit/common.provision.web/net.jazz.ajax_*.jar
	cp -v "${from_dir}"/build/lc.web.resources.base/eclipse/plugins/net.jazz.ajax_*.jar xkit/common.provision.web
    
	rm -v xkit/common.provision.web/net.jazz.ajax.tests_*.jar
	cp -v "${from_dir}"/build/lc.web.resources.base/eclipse/plugins/net.jazz.ajax.tests_*.jar xkit/common.provision.web

    rm -v xkit/common.provision.web/org.dojotoolkit.d*.jar
	cp -v "${from_dir}"/build/dojo.web.resources/eclipse/plugins/org.dojotoolkit.d*.jar xkit/common.provision.web
		
	##### JS testing Core AMD bundles
    rm -v xkit/common.provision.web/com.ibm.ic.core.web.resources_*.jar
	cp -v "${from_dir}"/build/ic.web.resources/eclipse/plugins/com.ibm.ic.core.web.resources_*.jar xkit/common.provision.web
		
    rm -v xkit/common.provision.web/com.ibm.ic.ui.web.resources_*.jar
	cp -v "${from_dir}"/build/ic.web.resources/eclipse/plugins/com.ibm.ic.ui.web.resources_*.jar xkit/common.provision.web
		
    rm -v xkit/common.provision.web/com.ibm.ic.share.web.resources_*.jar
	cp -v "${from_dir}"/build/ic.web.resources/eclipse/plugins/com.ibm.ic.share.web.resources_*.jar xkit/common.provision.web

    rm -v xkit/common.provision.web/com.ibm.ic.gadget.web.resources_*.jar
	cp -v "${from_dir}"/build/ic.web.resources/eclipse/plugins/com.ibm.ic.gadget.web.resources_*.jar xkit/common.provision.web
		
    rm -v xkit/common.provision.web/com.ibm.ic.highway.web.resources_*.jar
	cp -v "${from_dir}"/build/ic.web.resources/eclipse/plugins/com.ibm.ic.highway.web.resources_*.jar xkit/common.provision.web
		
    rm -v xkit/common.provision.web/com.ibm.ic.mail.web.resources_*.jar
	cp -v "${from_dir}"/build/social.web.resources/eclipse/plugins/com.ibm.ic.mail.web.resources_*.jar xkit/common.provision.web
		
    rm -v xkit/common.provision.web/com.ibm.ic.mm.web.resources_*.jar
	cp -v "${from_dir}"/build/ic.web.resources/eclipse/plugins/com.ibm.ic.mm.web.resources_*.jar xkit/common.provision.web
		
    rm -v xkit/common.provision.web/com.ibm.ic.oauth.web.resources_*.jar
	cp -v "${from_dir}"/build/oauth.web.resources/eclipse/plugins/com.ibm.ic.oauth.web.resources_*.jar xkit/common.provision.web
		
    rm -v xkit/common.provision.web/com.ibm.ic.personcard.web.resources_*.jar
	cp -v "${from_dir}"/build/ic.web.resources/eclipse/plugins/com.ibm.ic.personcard.web.resources_*.jar xkit/common.provision.web
		
	##### JS testing Core legacy bundles
    rm -v xkit/common.provision.web/com.ibm.lconn.core.styles_*.jar
	cp -v "${from_dir}"/build/lc.web.resources/eclipse/plugins/com.ibm.lconn.core.styles_*.jar xkit/common.provision.web
	
    rm -v xkit/common.provision.web/com.ibm.lconn.core.web.resources_*.jar
	cp -v "${from_dir}"/build/lc.web.resources/eclipse/plugins/com.ibm.lconn.core.web.resources_*.jar xkit/common.provision.web
	
    rm -v xkit/common.provision.web/com.ibm.lconn.share.web.resources_*.jar
	cp -v "${from_dir}"/build/lc.web.resources/eclipse/plugins/com.ibm.lconn.share.web.resources_*.jar xkit/common.provision.web
	
    rm -v xkit/common.provision.web/com.ibm.oneui.web.resources_*.jar
	cp -v "${from_dir}"/build/oneui.web.resources/eclipse/plugins/com.ibm.oneui.web.resources_*.jar xkit/common.provision.web
}

collect_from_sn_moderation() {
    local from_dir="$1/sn.moderation/lwp"
    local dirs="installableApps moderation.provision.web LotusConnections-config \
          connections.sql bin_lc_admin"
    for d in $dirs ; do [ -d "xkit/$d" ] || mkdir -p "xkit/$d" ; done
    cp -v "${from_dir}"/build/sn.moderation.ear/lib/moderation.ear xkit/installableApps/

    rm -v xkit/moderation.provision.web/com.ibm.lconn.moderation.web.resources_*.jar
    cp -v "${from_dir}"/build/moderation.web.resources/eclipse/plugins/com.ibm.lconn.moderation.web.resources_*.jar xkit/moderation.provision.web
}

collect_from_sn_news() {
    local from_dir="$1/sn.news/lwp"
    local dirs="installableApps news.provision.web LotusConnections-config \
          connections.sql bin_lc_admin"
    for d in $dirs ; do [ -d "xkit/$d" ] || mkdir -p "xkit/$d" ; done
    cp -v "${from_dir}"/build/news.ear/ear.prod/lib/news.ear xkit/installableApps/

    rm -v xkit/news.provision.web/com.ibm.lconn.news.digest.web.resources_*.jar
    cp -v "${from_dir}"/build/news.web.resources/eclipse/plugins/com.ibm.lconn.news.digest.web.resources_*.jar xkit/news.provision.web

    rm -v xkit/news.provision.web/com.ibm.lconn.news.microblogging.sharebox.form_*.jar
    cp -v "${from_dir}"/build/news.web.resources/eclipse/plugins/com.ibm.lconn.news.microblogging.sharebox.form_*.jar xkit/news.provision.web

    cp -v "${from_dir}"/build/news.sn.install/stage1/news.config/news-config.xml \
       xkit/LotusConnections-config/
    cp -v "${from_dir}"/build/news.sn.install/stage1/news.config/news-config.xsd \
       xkit/LotusConnections-config/
    cp -v "${from_dir}"/build/news.sn.install/stage1/scripts/jython/newsAdmin.py \
       xkit/bin_lc_admin
    cp -v "${from_dir}"/build/news.sn.install/stage1/scripts/jython/configAdmin.py \
       xkit/bin_lc_admin
    cp -v "${from_dir}"/build/news.sn.install/stage1/scripts/jython/highwayAdmin.py \
       xkit/bin_lc_admin
}

collect_from_placecntr() {
    local from_dir="$1/placecntr/lwp"
    local dirs="installableApps placesCatalog.provision.web LotusConnections-config \
          connections.sql bin_lc_admin"
    for d in $dirs ; do [ -d "xkit/$d" ] || mkdir -p "xkit/$d" ; done

}

collect_from_sn_profiles() {
    local from_dir="$1/sn.profiles/lwp"
    local dirs="installableApps profiles.provision.web LotusConnections-config \
          connections.sql bin_lc_admin"
    for d in $dirs ; do [ -d "xkit/$d" ] || mkdir -p "xkit/$d" ; done
    cp -v "${from_dir}"/build/profiles.ear/lib/profiles.ear xkit/installableApps/

    rm -v xkit/profiles.provision.web/com.ibm.lconn.profiles.web.resources_*.jar
    cp -v "${from_dir}"/build/profiles.web.resources/eclipse/plugins/com.ibm.lconn.profiles.web.resources_*.jar xkit/profiles.provision.web

    rsync -r "${from_dir}"/build/profiles.sn.install/db.sql/profiles/ \
       xkit/connections.sql/profiles
    cp -v "${from_dir}"/profiles.config.files/config.files/profiles-config.xml \
       xkit/LotusConnections-config/
    cp -v "${from_dir}"/profiles.config.files/config.files/profiles-config.xsd \
       xkit/LotusConnections-config/
    cp -v "${from_dir}"/profiles.admin.scripts/scripts/jython/profilesAdmin.py \
       xkit/bin_lc_admin
}

collect_from_sn_rte() {
    local from_dir="$1/sn.rte/lwp"
    local dirs="installableApps rte.provision.web LotusConnections-config \
          connections.sql bin_lc_admin"
    for d in $dirs ; do [ -d "xkit/$d" ] || mkdir -p "xkit/$d" ; done
    cp -v "${from_dir}"/build/rte-ear/lib/rte.ear xkit/installableApps/
}

collect_from_sn_scee() {
    local from_dir="$1/sn.scee/lwp"
    local dirs="installableApps scee.provision.web LotusConnections-config \
          connections.sql bin_lc_admin"
    for d in $dirs ; do [ -d "xkit/$d" ] || mkdir -p "xkit/$d" ; done
    cp -v "${from_dir}"/build/ExtRegComp/lib/scee.ear xkit/installableApps/
}

collect_from_sn_search() {
    local from_dir="$1/sn.search/lwp"
    local dirs="installableApps search.provision.web LotusConnections-config \
          connections.sql bin_lc_admin"
    for d in $dirs ; do [ -d "xkit/$d" ] || mkdir -p "xkit/$d" ; done
    cp -v "${from_dir}"/build/search.ear/ear.prod/lib/search.ear xkit/installableApps/
    cp -v "${from_dir}"/search.base/common/config.files/search-config.xml \
       xkit/LotusConnections-config/
    cp -v "${from_dir}"/search.base/common/config.files/search-config.xsd \
       xkit/LotusConnections-config/
    cp -v "${from_dir}"/search.sn.install/scripts/jython/searchAdmin.py \
       xkit/bin_lc_admin
}

collect_from_sn_widgets_clib() {
    local from_dir="$1/sn.widgets.clib/lwp"
    [ -d "xkit/common.provision.web" ] || mkdir -p "xkit/common.provision.web"
    cp_web_res "${from_dir}"/build/librarywidget.web.resources/eclipse/plugins xkit/common.provision.web
}

collect_from_share() {
    local from_dir="$1/share/lwp"
    local d=""
    local f_path=""
    local f_name=""
    local dirs="installableApps wikis.provision.web files.provision.web LotusConnections-config \
          connections.sql bin_lc_admin"
    for d in $dirs ; do [ -d "xkit/$d" ] || mkdir -p "xkit/$d" ; done
    cp -v "${from_dir}"/build/files.ear/lib/files.ear xkit/installableApps/
    cp -v "${from_dir}"/build/wikis.ear/lib/wikis.ear xkit/installableApps/

    rm -v xkit/wikis.provision.web/com.ibm.lconn.communitywikis.web.resources_*.jar
    cp -v "${from_dir}"/build/wikis.web.resources/eclipse/plugins/com.ibm.lconn.communitywikis.web.resources_*.jar xkit/wikis.provision.web

    rm -v xkit/wikis.provision.web/com.ibm.lconn.core0.web.resources_*.jar
    cp -v "${from_dir}"/build/wikis.web.resources/eclipse/plugins/com.ibm.lconn.core0.web.resources_*.jar xkit/wikis.provision.web

    rm -v xkit/wikis.provision.web/com.ibm.lconn.share0.web.resources_*.jar
    cp -v "${from_dir}"/build/wikis.web.resources/eclipse/plugins/com.ibm.lconn.share0.web.resources_*.jar xkit/wikis.provision.web

    rm -v xkit/wikis.provision.web/com.ibm.lconn.wikis.web.resources_*.jar
    cp -v "${from_dir}"/build/wikis.web.resources/eclipse/plugins/com.ibm.lconn.wikis.web.resources_*.jar xkit/wikis.provision.web

    rm -v xkit/files.provision.web/com.ibm.lconn.communityfiles.web.resources_*.jar
    cp -v "${from_dir}"/build/files.web.resources/eclipse/plugins/com.ibm.lconn.communityfiles.web.resources_*.jar xkit/files.provision.web

    rm -v xkit/files.provision.web/com.ibm.lconn.filegadgets.web.resources_*.jar
    cp -v "${from_dir}"/build/files.web.resources/eclipse/plugins/com.ibm.lconn.filegadgets.web.resources_*.jar xkit/files.provision.web

    rm -v xkit/files.provision.web/com.ibm.lconn.files.web.resources_*.jar
    cp -v "${from_dir}"/build/files.web.resources/eclipse/plugins/com.ibm.lconn.files.web.resources_*.jar xkit/files.provision.web

    rsync -r "${from_dir}"/build/share.platform/db/connections.wikis/ \
       xkit/connections.sql/wikis
    rsync -r "${from_dir}"/build/share.platform/db/connections.files/ \
       xkit/connections.sql/files
    # config xml files
    cp -v "${from_dir}"/build/share.platform/config/connections.wikis.cloud/cell/wikis-config.xml \
       xkit/LotusConnections-config/
    cp -v "${from_dir}"/build/share.platform/config/connections.wikis.cloud/cell/wikis-config.xsd \
       xkit/LotusConnections-config/
    cp -v "${from_dir}"/build/share.platform/config/connections.files.cloud/cell/files-config.xml \
       xkit/LotusConnections-config/
    cp -v "${from_dir}"/build/share.platform/config/connections.files.cloud/cell/files-config.xsd \
       xkit/LotusConnections-config/
    # admin py files
    cp -v "${from_dir}"/build/share.platform/admin/jython/connections.files/filesAdmin.py \
       xkit/bin_lc_admin
    cp -v "${from_dir}"/build/share.platform/admin/jython/connections.wikis/wikisAdmin.py \
       xkit/bin_lc_admin
}

# Usage:
#   cp_web_res from_dir to_dir
#
#   copy the web resources jar files, remove the existing jar files with older timestamps
#   before copying over the new ones.
#
# Example:
#   cp_web_res share/lwp/build/wikis.provision.web/eclipse/plugins xkit/wikis.provision.web
#
cp_web_res() {
    local from_dir=$1
    local to_dir=$2
    local f_name=""
    for f_path in "${from_dir}"/* ; do
        f_name="${f_path##*/}"
        if [ "${f_name}" = '*' ]; then
            continue
        fi
        rm -f "${to_dir}"/${f_name%_*.????????-????.jar}_*.jar
        cp -v "$f_path" "${to_dir}/"
    done
}

usage() {
    printf "Usage: %s: [-b build] [-B stream]\n" $0
    printf "\n"
    printf "Options:\n"
    printf "  -b build     retrieve the specified build\n"
    printf "  -B stream    retrieve the latest build of stream\n"
    printf "  -f           force re-download the build even already have it\n"
    printf "  -l           list builds for given stream\n"
    printf "  -s dir       save the downloaded build to given dir\n"
    printf "  -x           update the Connections component into given xkit dir\n"
    printf "               only for Connections build such as activities, blogs, bookmarks, ..."
    printf "\n"
    printf "Examples:\n"
    printf "  %s -b IC10.0_Activities_20131122.1600\n" $0
    printf "  %s -b LCI4.5/LCI4.5_20130122.1600\n" $0
    printf "  %s -B LCI4.0_CI\n" $0
    printf "  %s -B IC10.0_Activities -x xkit\n" $0
    printf "  %s -l -B LCI4.5\n" $0
    exit 2
}


while getopts vfb:B:l:s:x name
do
    case ${name} in
    b)  BUILD="${OPTARG}";;
    B)  STREAM="${OPTARG}";;
    s)  SAVETO="${OPTARG}";;
    v)  f_verbose=1;;
    f)  f_forced=1;;
    l)  f_list_builds=1;;
    x)  f_updt_xkit_dir=1 ;;
    ?)  usage $0 ;;
    esac
done

SAVETO="${SAVETO:-${PWD}}"

if ! build_location_exists ; then
    exit 2
fi

if [ -n "${f_list_builds}" ]; then
    if [ -n "${STREAM}" ] ; then
        list_builds_of_stream ${STREAM}
    else
        list_builds_of_all_known_streams
    fi
    dump_builds_list
    exit 0
fi

if [ -n "${STREAM}" -a -z "${BUILD}" ]; then
    get_current_build_name_of_stream ${STREAM} || get_latest_build_name_of_stream ${STREAM}
fi

echo BUILD=$BUILD

if [ -z "${BUILD}" ]; then
    echo "No build specified, use either -b or -B option, or use -? for help."
    exit 1
fi

if [ "${BUILD#*/}" = "${BUILD}" ] ; then
    # build is just a build label, like IC10.0_Activities_20131123-1100
    STREAM=${BUILD%_20*}
else
    # build stream contains a '/', like IC10.0_Activities/IC10.0_Activities_20131123-1100
    STREAM=${BUILD%/*}
    BUILD=${BUILD#*/}
fi

check_build

if [ "$f_forced" = 1 -a -f "${SAVETO}/.kitversion" ] ; then
    rm "${SAVETO}/.kitversion"
fi

if ! retrieve_build $STREAM/$BUILD "$SAVETO" ; then
    echo "Failed to get build: $BUILD in to [${SAVETO}]"
    exit 2
fi
echo "Build [$BUILD] is available at [${SAVETO}]"

