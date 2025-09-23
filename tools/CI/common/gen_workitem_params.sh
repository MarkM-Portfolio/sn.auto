#!/bin/sh

ASSIGN_TO_AUTOMATION=${1:-no}
COMPONENT=$2

fail_with_error() {
    echo "$@"
    exit 1
}

echo "Loading ${CI_COMMON_HOME}/ci_functions.sh..."
. ${CI_COMMON_HOME}/ci_functions.sh || fail_with_error "Failed to load ${CI_COMMON_HOME}/ci_functions.sh."

[ "${COMPONENT}" == "activitystreamsearch" ] && BUILD_COMPONENT=Search

case ${BUILD_COMPONENT} in
    Activities)
        tags="bvt_blocker_activities"
		category="Web/Activities"
		subscribers="moyingbj@cn.ibm.com,niushuh@cn.ibm.com"
		query="Activities BVT Blocker"
		ownedBy="moyingbj@cn.ibm.com"
		;;

    Blogs)
        tags="bvt_blocker_blogs"
		category="Web/Blogs"
		subscribers="xiaming@cn.ibm.com"
		query="Blogs BVT Blocker"
		ownedBy="yuxiaof@cn.ibm.com"
		;;

    Bookmarks)
        tags="bvt_blocker_bookmarks"
		category="Web/Bookmarks"
		subscribers="xiaming@cn.ibm.com,daizhoul@cn.ibm.com,jinyifan@cn.ibm.com,zhangqiw@cn.ibm.com,zhihongy@cn.ibm.com,zhling@cn.ibm.com"
		query="Bookmarks BVT Blocker"
		ownedBy="yuxiaof@cn.ibm.com"
		;;

    Communities)
        tags="bvt_blocker_communities"
		category="Web/Communities"
		subscribers="davidab@us.ibm.com,jonathan_hewitt@us.ibm.com,guym@il.ibm.com,brawntj@us.ibm.com"
		query="Communities BVT Blocker"
		ownedBy="robertsb@us.ibm.com"
		;;

	ExtensionsRegistry)
        tags="bvt_blocker_ext"
		category="Web/Cross Functional/Extension Registry"
		subscribers="vfrancis@us.ibm.com michael_blackstock@us.ibm.com,vincent.burckhardt@ie.ibm.com"
		query="Extensibility BVT Blocker"
		ownedBy="michael_blackstock@us.ibm.com"
		;;

    Forums)
        tags="bvt_blocker_forums"
		category="Web/Forums"
		subscribers="xiaming@cn.ibm.com,jwiss@us.ibm.com,wanghek@cn.ibm.com,yuxiaof@cn.ibm.com,zhling@cn.ibm.com"
		query="Forums BVT Blocker"
		ownedBy="yuxiaof@cn.ibm.com"
		;;
    
	Homepage)
        tags="bvt_blocker_homepage"
		category="Web/Home Page"
		subscribers="stephen.crawford@ie.ibm.com,bill_looby@ie.ibm.com,vincent.burckhardt@ie.ibm.com"
		query="Homepage BVT Blocker"
		ownedBy="stephen.crawford@ie.ibm.com"
		;;

    Infra)
        tags="bvt_blocker_infra"
		category="Web/Cross Functional"
		subscribers="jay_boyd@us.ibm.com,michael_cross@us.ibm.com,guym@il.ibm.com,JORGEMAN@ie.ibm.com,raozw@cn.ibm.com,stephen.crawford@ie.ibm.com,stephen_wills@ie.ibm.com,brawntj@us.ibm.com,zhling@cn.ibm.com"
		query="Infra BVT Blocker"
		ownedBy="JORGEMAN@ie.ibm.com"
		;;

	Metrics)
		tags="bvt_blocker_metrics"
		category="Web/Metrics"
		subscribers="davidab@us.ibm.com,hukuang@cn.ibm.com,lichunl@cn.ibm.com,linzhig@cn.ibm.com,niushuh@cn.ibm.com"
		query="Metrics BVT Blocker"
		ownedBy="lichunl@cn.ibm.com"
		;;

	Mobile)
		tags="bvt_blocker_mobile"
		category="Web/Mobile"
		subscribers="davek@us.ibm.com,drshock@us.ibm.com,gausingh@us.ibm.com"
		query="Mobile BVT Blocker"
		ownedBy="davek@us.ibm.com"
		;;

    Moderation)
	    tags="bvt_blocker_moderation"
		category="Web/Moderation"
		subscribers="CONN-MODERATION"
		query="Moderation BVT Blocker"
		ownedBy="changpbj@cn.ibm.com"
		;;
    
	News)
        tags="bvt_blocker_news"
		category="Web/News"
		subscribers="stephen.crawford@ie.ibm.com,bill_looby@ie.ibm.com,vincent.burckhardt@ie.ibm.com,tonycal3@ie.ibm.com"
		query="News BVT Blocker"
		ownedBy="tonycal3@ie.ibm.com"
		;;

    PlacesCatalog)
        tags="bvt_blocker_placesCatalog"
        category="Web/Search"
        subscribers="EITANS@il.ibm.com,mark_levins@ie.ibm.com,guym@il.ibm.com,SHARONK@il.ibm.com,stephen_wills@ie.ibm.com"
        query="PlacesCatalog BVT Blocker"
 		ownedBy="stephen_wills@ie.ibm.com"
       ;;
                                           
    Profiles)
        tags="bvt_blocker_profiles"
		category="Web/Profiles"
		subscribers="arbuckle@us.ibm.com,zhouwen_lu@us.ibm.com"
		query="Profiles BVT Blocker"
 		ownedBy="jwiss@us.ibm.com"
		;;

	RichTextEditors)
        tags="bvt_blocker_rte"
		category="Web/Cross Functional/Rich Text Editors"
		subscribers="CHRISGUI@ie.ibm.com"
		query="RichTextEditors BVT Blocker"
		ownedBy="CHRISGUI@ie.ibm.com"
		;;

    Search)
        tags="bvt_blocker_search"
		category="Web/Search"
		subscribers="EITANS@il.ibm.com,mark_levins@ie.ibm.com,guym@il.ibm.com,SHARONK@il.ibm.com,stephen_wills@ie.ibm.com"
		query="Search BVT Blocker"
 		ownedBy="INA@il.ibm.com"
        ;;

    Share)
        tags="bvt_blocker_fileswikis"
		category="Web/Files"
		subscribers="husbj@cn.ibm.com,zhaoyis@cn.ibm.com,jodriscoll@ie.ibm.com,JORGEMAN@ie.ibm.com,raozw@cn.ibm.com"
		query="Files-Wikis BVT Blocker"
 		ownedBy="husbj@cn.ibm.com"
		;;

    UI)
        tags="bvt_blocker_common_ui"
		category="Web/Cross Functional"
		subscribers="jay_boyd@us.ibm.com,guym@il.ibm.com,JORGEMAN@ie.ibm.com,raozw@cn.ibm.com,stephen.crawford@ie.ibm.com,stephen_wills@ie.ibm.com,brawntj@us.ibm.com,zhling@cn.ibm.com,jgirata2@us.ibm.com"
		query="Common UI BVT Blocker"
 		ownedBy="JORGEMAN@ie.ibm.com"
		;;

	WidgetsCal)
		tags="bvt_blocker_widgets-cal"
		category="Web/Communities/Calendar"
		subscribers="xiaming@cn.ibm.com,daizhoul@cn.ibm.com,jinyifan@cn.ibm.com,zhangqiw@cn.ibm.com"
		query="Widgets-cal BVT Blocker"
		ownedBy="yuxiaof@cn.ibm.com"
		;;

	WidgetsClib)
		tags="bvt_blocker_widgets-clib"
		category="Web/Communities/Custom Library"
		subscribers="arclarke@us.ibm.com,jgirata2@us.ibm.com,brawntj@us.ibm.com"
		query="Widgets-clib BVT Blocker"
		ownedBy="arclarke@us.ibm.com"
		;;

    *)
        fail_with_error "Unkown component: ${BUILD_COMPONENT}"
		;;
esac

[ "${ASSIGN_TO_AUTOMATION}" != "no" ] && category="Web/Cross Functional/Automation"

cd "${WORKSPACE}"

# Create a properties file for passing parameters when triggering the Work Item Creator job.
echo "Deleting any existing workitem.properties file..."
rm -f workitem.properties

# Decision was made not to create a defect for automation related problems
# since those kinds of problems will usually impact all pipelines causing
# tons of defects to be generated for the same issue.
[ "${ASSIGN_TO_AUTOMATION}" != "no" ] && exit 0

echo "Creating workitem.properties file..."
echo "tags=${tags}"						 > workitem.properties
echo "category=${category}"				>> workitem.properties
echo "subscribers=${subscribers}"		>> workitem.properties
echo "query=${query}"					>> workitem.properties
echo "ownedBy=${ownedBy}"				>> workitem.properties
