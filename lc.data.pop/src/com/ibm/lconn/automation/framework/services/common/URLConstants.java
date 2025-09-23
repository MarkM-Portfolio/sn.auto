package com.ibm.lconn.automation.framework.services.common;

/**
 * Helper class that contains URLs that are used across the services.
 * 
 * @author Piyush K. Agarwal - pagarwal@us.ibm.com
 */
public class URLConstants {
	
	public static String SERVER_URL = "";
	public static String DMGR_URL = "";
	//SERVER_URL value used for run on eclipse moved to TestEnv.class

	public static void setServerURL(String url) {
		SERVER_URL = url;
	}
		
	// Service Base URLs
	public static final String ACTIVITIES_BASE = "/activities";
	public static final String BLOGS_BASE = "/blogs";
	public static final String COMMUNITIES_BASE = "/communities";
	public static final String DOGEAR_BASE = "/dogear";
	public static final String FILES_BASE = "/files";
	public static final String FORUMS_BASE = "/forums";
	public static final String HELP_BASE = "/help";
	public static final String HOMEPAGE_BASE = "/homepage";
	public static final String MEDIA_GALLERY_BASE = "/news/widgets/lw";
	public static final String MOBILE_BASE = "/mobile";
	public static final String MODERATION_BASE = "/moderation";
	public static final String NEWS_BASE = "/news";
	public static final String PERSON_TAG_BASE = "/profiles/ibm_semanticTagServlet";
	public static final String PROFILES_BASE = "/profiles";
	public static final String SAND_BASE = "/news/common/sand";
	public static final String SEARCH_BASE = "/search";
	public static final String WIKIS_BASE = "/wikis";
	

	public static final String NEWS_SERVICE = "/atom/service";
	public static final String COMMUNITIES_SERVICE = "/service/atom/service";
	public static final String PROFILES_SERVICE = "/atom/profileService.do";
	public static final String PROFILES_ADMIN_SERVICE = "/admin/atom/profileService.do";
	public static final String DOGEAR_SERVICE = "/api/app";
	public static final String WIKIS_SERVICE = "/basic/api/wikis/feed";
	public static final String WIKIS_MY_SERVICE = "/basic/api/mywikis/feed";
	//public static final String WIKIS_PUBLIC_SERVICE = "/basic/anonymous/api/wikis/feed";
	public static final String WIKI_PAGE_URL_PREFIX = "/basic/api/wiki";
	public static final String WIKIS_FEED = "/feed";
	public static final String WIKIS_ENTRY = "/entry";
	//public static final String WIKIS_FILTER = "/basic/anonymous/api/wikis";
	public static final String WIKIS_FILTER = "/basic/api/wikis";
	//public static final String PUBLIC_WIKIS_FILTER = "/basic/anonymous/api/wiki";
	public static final String PUBLIC_WIKIS_FILTER = "/basic/api/wiki";
	public static final String WIKI_FORM_FEED = "/form/api/wiki/";
	public static final String WIKIS_TAGS = "/basic/api/tags/feed"; 
	public static final String WIKIS_COMMUNITY_WIKI="/basic/api/communitywiki/"; //+{communityUUID}+/feed
	public static final String WIKIS_COMMUNITY = "/basic/api/community";
	public static final String WIKIS_PAGES_PERSON = "/basic/api/wikis/person/"; // + {userId} + /feed 
	
	public static final String ServiceConfigs = "/serviceconfigs";
	

	// Activities Specific URLs
	public static final String ACTIVITIES_SERVICE = "/service/atom2/service";
	public static final String ACTIVITIES_OAUTH_SERVICE = "/oauth/atom2/service";
	public static final String ACTIVITIES_MY = "/service/atom2/activities";
	public static final String ACTIVITIES_COMPLETED = "/service/atom2/completed";
	public static final String ACTIVITIES_ALL = "/service/atom2/everything";
	public static final String ACTIVITIES_TODO_LIST = "/service/atom2/todos";
	public static final String ACTIVITIES_TAGS = "/service/atom2/tags";
	public static final String ACTIVITIES_ENTRY_TEMPLATES = "/service/atom2/entrytemplates?activityUuid="; //specify Uuid of activity to get the templates
	public static final String ACTIVITIES_TRASH = "/service/atom2/trash";
	public static final String ACTIVITIES_TEMPLATES = "/service/atom2/entrytemplates?activityUuid=";
	public static final String ACTIVITIES_SERVER = "/service/atom2";
	public static final String ACTIVITIES_TODO_CONTENT = "/service/atom2/descendants"; //?nodeUuid={}
	public static final String ACTIVITIES_ATOM_MY = "/service/atom2/activities"; 
	
	// Blogs Specific URLs
	public static final String BLOGS_SERVICE = "/api";
	public static final String BLOGS_ALL= "/feed/blogs/atom";
	public static final String BLOGS_ALL_LATEST_POSTS = "/feed/entries/atom";
	public static final String BLOGS_FEATURED_POSTS = "/feed/featured/atom";
	public static final String BLOGS_FEATURED_BLOGS = "/feed/featuredblogs/atom";
	public static final String BLOGS_ALL_LATEST_COMMENTS= "/feed/comments/atom";
	public static final String BLOGS_RECOMMENDED_POSTS = "/feed/recommended/atom";
	public static final String BLOGS_ALL_TAGS= "/feed/tags/atom";
	public static final String BLOGS_ISSUE_CATEGORIES = "/feed/issuecategories/atom";
	public static final String BLOGS_PERSON_RECENT_POSTS = "/roller-ui/feed/"; // user email-address;
	public static final String BLOGS_MEDIA_ENTRIES = "/feed/media/atom";
	public static final String BLOGS_MYVOTES_POSTS = "/feed/myvotes/atom";
	public static final String BLOGS_VERIFY_USER = "/seedlist/authverify/validateUser";
	public static final String BLOGS_ACL_TOKENS = "/seedlist/authverify/getACLTokens";
	public static final String BLOGS_ENTRY_RECOMMENDATIONS = "/feed/entryrecommendations/"; // <id>/atom
	public static final String BLOGS_COMMENT_RECOMMENDATIONS = "/feed/commentrecommendations/";// <id>/atom
	public static final String BLOGS_SERVICES = "/services/atom/";
	public static final String BLOGS_MODERATION_SERVICE = "/moderation/atomsvc";
	public static final String BLOGS_REPORTS_ENTRIES = "/api/reports/entries"; //  /{homepageHandle} must go before this
	public static final String BLOGS_REPORTS_COMMENTS = "/api/reports/comments"; //  /{homepageHandle} must go before this
	public static final String BLOGS_ENTRIES = "/api/entries"; //  /{homepageHandle} must go before this
	public static final String BLOGS_MEDIA = "/api/media";
	public static final String BLOGS_COMMENTS = "/api/comments";
	public static final String BLOGS_MODERATION_SERVICES = "/moderation/atomsvc";
	public static final String BLOGS_RENDERING_FEED = "/roller-ui/rendering/feed/";
	
	// Communities Specific URLs
	public static final String COMMUNITIES_ALL = "/service/atom/communities/all";
	public static final String COMMUNITIES_ORG = "/service/atom/communities/org";
	public static final String COMMUNITIES_MY = "/service/atom/communities/my";
	public static final String COMMUNITIES_ORG_TAG_CLOUD = "/service/atom/communities/org?outputType=categories";
	public static final String COMMUNITIES_RELATED = "/recomm/atom/relatedCommunity";
	public static final String COMMUNITIES_RELATED_ALL = "/recomm/atom/relatedCommunities";
	public static final String COMMUNITIES_RELATED_SERVICE = "/recomm/atom/service";
	public static final String COMMUNITIES_CATALOG_VIEWS = "/service/json/catalog/views";
	
	// DOGEAR Specific URLS 
	public static final String DOGEAR_POPULAR = "/atom/popular";
	public static final String DOGEAR_SEARCH = "/atom";
	public static final String DOGEAR_TAGS = "/tags";
	public static final String DOGEAR_MY_NOTIFICATIONS = "/atom/mynotifications";
	public static final String DOGEAR_SENT_NOTIFICATIONS = "/atom/mysentnotifications";

	// Forums Specific URLs
	public static final String FORUMS_SERVICE = "/atom/service";
	public static final String FORUMS_ALL = "/atom/forums";
	public static final String FORUMS_MY = "/atom/forums/my";
	public static final String FORUMS_TAG_COLLECTION = "/atom/tags/forums";
	public static final String FORUMS_SEARCH_ALL = "/atom/search";
	

	public static final String PROFILES_SEARCH_ALL = "/atom/search.do";
	public static final String PROFILES_SEARCH_PROFILES = "/atom/profile.do?"; // + one of the parameters: email, key, or userid
	public static final String PROFILES_SEARCH_REPORT_CHAIN = "/atom/reportingChain.do?"; // + one of the parameters: email, key, or userid
	public static final String PROFILES_SEARCH_DIRECT_REPORTS = "/atom/peopleManaged.do";
	public static final String PROFILES_SEARCH_CONNECTIONS_LIST = "/atom/connections.do?connectionType=colleague";
	public static final String PROFILES_SEARCH_ARE_COLLEAGUES = "/atom/connection.do?connectionType=colleague";
	public static final String PROFILES_PERMA_LINK = "/html/profileView.do";
	public static final String PROFILES_LEGACY_STATUS = "/atom/mv/theboard/entry/status.do"; // + parameter. ex: email, entryId

	public static final String TOPICS_MY= "/atom/topics/my";
	public static final String TOPICS_TAG_COLLECTION = "/atom/tags/topics";
	public static final String TOPICS_FORUM = "/atom/topics?forumUuid="; // + {forumUuid}
	// Files Specific URLs
	public static final String FILES_SERVICE = "/basic/api/introspection";
	public static final String FILES_ALL_ANONY = "/basic/anonymous/api/documents/feed?visibility=public"; // all public
	public static final String FILES_ALL_SC = "/basic/api/documents/feed?visibility=public"; // all public
	public static final String FILES_PINNED = "/basic/api/myfavorites/documents/feed";
	public static final String FILES_FOLDERS = "/basic/api/collections/feed";
	public static final String FILES_IN_FOLDER = "/basic/api/collection";
	public static final String FILE_FOLDER_INFO = "/basic/api/collection/";
	public static final String FILES_PINNED_FOLDERS = "/basic/api/myfavorites/collections/feed";
	public static final String FILES_RECENT_ADDEDTO_FOLDERS = "/basic/api/collections/addedto/feed";
	public static final String FILES_FOLDER_LIST = "/basic/api/connection/"; // + {collection-id}/feed;
	public static final String FILES_PUBLIC_FOLDERS = "/basic/anonymous/api/collections/feed";
	public static final String FILES_PUBLIC_FOLDERS_SC = "/basic/api/collections/feed";
	public static final String FILES_PERSON_LIBRARY_UNAUTH = "/basic/anonymous/api/userlibrary/"; // + {userid}/feed;
	public static final String FILES_PERSON_LIBRARY_AUTH = "/basic/api/userlibrary/"; // + {userid}/feed;
	public static final String FILES_MY_LIBRARY = "/basic/api/myuserlibrary/feed";
	public static final String FILES_SHARES_3_0 = "/basic/api/shares/feed";
	public static final String FILES_GET_SHARE = "/basic/api/share/";
	public static final String FILES_SHARES = "/basic/api/documents/shared/feed";
	public static final String FILES_MYSHARES = "/basic/api/myshares/feed";
	public static final String FILES_COMMENTS_CATEGORY = "/feed?category=comment";
	public static final String FILES_COMMENTS_PUBLIC = "/basic/anonymous/api/userlibrary"; // + /{userid}/document/{document-id}/feed?category=comment"
	public static final String FILES_COMMENTS_ACCESS = "/basic/api/userlibrary"; // + /{userid}/document/{document-id}/feed?category=comment"
	public static final String FILES_MY_DOCUMENT = "/basic/api/myuserlibrary/document/"; // + {document-id}/feed?category=comment"
	public static final String FILES_RECYCLE_BIN = "/basic/api/myuserlibrary/view/recyclebin/feed";
	public static final String FILES_RETRIEVE_FROM_RECYCLE_BIN = "/basic/api/myuserlibrary/view/recyclebin/";
	public static final String FILES_PURGE_FROM_TRASH = "/basic/api/myuserlibrary/view/recyclebin/";
	public static final String FILES_VERSION = "/basic/api/myuserlibrary/document/";
	public static final String FILES_USER_SEARCH = "/basic/api/people/feed";
	public static final String FILES_DOCUMENT = "/basic/api/document/";
	public static final String FILES_TAGS = "/basic/api/tags/feed";
	public static final String FILES_LIBRARY = "/basic/api/library/";
	public static final String FILES_COMMUNITY_LIBRARY = "/basic/api/communitylibrary/"; // + {communityUuid}/feed
	public static final String FILES_COMMUNITY_COLLECTION = "/basic/api/communitycollection/"; // + {communityUuid}/feed
	public static final String FILES_SERVICE_DOCUMENT="/my/servicedoc";
	public static final String FILES_CMIS ="/basic/cmis"; //to get repository, add "/repository" to this
	public static final String FILES_MODERATION_SERVICES = "/basic/api/moderation/atomsvc";
	 public static final String FILES_REPORT = "/basic/api/reports";
	
	// News 
	public static final String NEWS_PUBLIC_UPDATES = "/atom/stories/public";
	public static final String NEWS_SAVED_UPDATES = "/atom/stories/saved";
	public static final String NEWS_TOP_UPDATES = "/atom/stories/top";
	public static final String NEWS_PERSON_UPDATES = "/atom/stories/public/person";
	public static final String NEWS_COMMUNITY_UPDATES = "/atom/stories/community";
	public static final String NEWS_FEED_UPDATES = "/atom/stories/newsfeed";
	public static final String NEWS_STATUS_UPDATES = "/atom/stories/statusupdates";
	//OpenSocial
	public static final String OPENSOCIAL_BASIC = "/connections/opensocial/basic";
	
	// Search
	public static final String SEARCH_PUBLIC = "/search";
	public static final String SEARCH_PUBLIC_PRIVATE = "/mysearch";
	public static final String SEARCH_PEOPLE_PUBLIC = "/search/facets/people";
	public static final String SEARCH_PEOPLE_PUBLIC_PRIVATE = "/mysearch/facets/people";
	public static final String SEARCH_TAGS_PUBLIC = "/search/facets/tags";
	public static final String SEARCH_TAGS_PUBLIC_PRIVATE = "/mysearch/facets/tags";
	public static final String SEARCH_SOURCE_PUBLIC = "/search/facets/source";
	public static final String SEARCH_SOURCE_PUBLIC_PRIVATE = "/mysearch/facets/source";
	public static final String SEARCH_DATE_PUBLIC = "/search/facets/date";
	public static final String SEARCH_DATE_PUBLIC_PRIVATE = "/mysearch/facets/date";
	public static final String SEARCH_SOCIAL_RECOMMENDATIONS = "/social/recommend";
	// social graph
	public static final String SEARCH_SOCIAL_GRAPH = "/social/graph/path";
	// social network
	public static final String SEARCH_SOCIAL_NETWORK = "/social/graph/list";

	//scopes
	public static final String SCOPES = "/atom/scopes";
	
	//Tags typeahead
	public static final String TAGS_TYPEAHEAD_PUBLIC = "/json/tag"; 
	public static final String TAGS_TYPEAHEAD_PRIVATE = "/json/mytag"; 
	
	//ECM Properties Labels API
	public static final String ECM_PROPERTIES = "/json/labels/properties";
	public static final String ECM_DOCUMENT_TYPE = "/json/labels";
	
	// Catalog
	public static final String BASE_CATALOG_URL = COMMUNITIES_BASE + "/service/atom/catalog";
	public static final String CATALOG_PUBLIC = "/public";
	public static final String CATALOG_MY = "/my";
	public static final String CATALOG_ALLMY = "/allmy";
	public static final String CATALOG_SEARCH = "/search";
	public static final String CATALOG_FOLLOWING = "/followed";
	public static final String CATALOG_INVITED = "/invites";
	public static final String CATALOG_OWNED = "/owned";
	public static final String CATALOG_CREATED = "/created";
	public static final String CATALOG_RESTRICTED = "/restricted";
	public static final String CATALOG_TRASHED = "/trashed";
	public static final String CATALOG_COMPLETION = "/completion";
	public static final String CATALOG_PUBLIC_COMPLETION = "/public/completion";
	public static final String CATALOG_TAGS = "/tags";
	public static final String CATALOG_PUBLIC_TAGS = "/public/tags";
	public static final String CATALOG_MY_TAGS = "/my/tags";
	public static final String CATALOG_TAGS_COMPLETION = "/tags/completion";
	public static final String CATALOG_ADMIN = "/admin/get";
	public static final String CATALOG_ADMIN_GET = CATALOG_ADMIN + "/get";
	public static final String CATALOG_ADMIN_START = CATALOG_ADMIN + "/start";
	public static final String CATALOG_COLLECTION_ID_PARAM = "collectionId=Places";
	public static final String CATALOG_CRAWLER_ID_PARAM = "crawlerId=LocalCrawler";

	public static final String CATALOG_VIEWS_URL = COMMUNITIES_BASE + "/service/json/catalog/views";
	
	// Metrics
	public static final String METRICS_BASE = "/metricssc";
	public static final String METRICS_SC_QUERY_SERVICE = "/metricssc/service/rest/queryservice";
	public static final String METRICS_ONPREMISE_QUERY_SERVICE = "/metrics/service/rest/queryservice";
	public static final String METRICS_ONPRIMISE_METRICS_SERVICE = "/metrics/service/rest/metricsservice";
	public static final String METRICS_SC_METRICS_SERVICE = "/metricssc/service/rest/metricsservice";
}
