package com.ibm.lconn.automation.framework.services.common;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

/**
 * Helper class that contains Strings/QNames that are used across the services.
 * 
 * @author Piyush K. Agarwal - pagarwal@us.ibm.com
 */
public class StringConstants {
	
	// Helper Enum Types
	public enum Access { ANY, PRIVATE, PUBLIC }
	public enum Authentication { BASIC, FORM, OAUTH }
	public enum Logger { INFO, DEBUG, TRACE }
	public enum BlogsField { ALL }
	public enum BlogsType { BLOG, COMMENT, ENTRY }
	public enum CommunityBlogPermissions { PUBLIC, PRIVATE, MODERATED }
	public enum Component { ACTIVITIES, AUTHCONNECTOR, BLOGS, COMMUNITIES, CRE, DOGEAR, FILES, FORUMS, PROFILES, WIKIS, MICROBLOGS, MICROBLOGGING, ACTIVITYSTREAMS, OPENSOCIAL, ACTIVITYSTREAMSEARCH, PROFILESADMIN, SEARCH, CONTENTSEARCH, NEWS, COMMUNITIESCATALOG,SOLR,SWITCHBOX,METRICS,ORIENTME}
	public enum DefaultView { RECENT, OUTLINE, TODO }
	public enum FieldType { DATE, FILE, LINK, PERSON, TEXT }
	public enum Filter { ANSWEREDQUESTIONS, FORUMS, QUESTIONS, TOPICS, ALLQUESTIONS }
	public enum Format { ATOM, LI, TXT}
	public enum MailSubscription { ALL, SUBSCRIBE, UNSUBSCRIBE}
	public enum MemberType { PERSON, GROUP, COMMUNITY}
	public enum WikiMemberType { USER, VIRTUAL }
	public enum Network { ALL, INTRANET, INTERNET }
	public enum NodeType { ACTIVITIY, BOOKMARK, CHAT, COMMENT, COMMUNITY_ACTIVITY, EMAIL, ENTRY, ENTRYTEMPLATE, FILE, REPLY, SECTION, TODO }
	public enum Notification { ON, OFF }
	public enum Permissions { PRIVATE, PUBLIC, PUBLICINVITEONLY, SHARED }
	public enum ProfileFormat { LITE, FULL }
	public enum ProfileState { ACTIVE, INACTIVE }
	public enum ProfileOutput { VCARD, HCARD }
	public enum PopularBookmarksSort { POPULAR, VISITED }
	public enum Priority { MEDIUM, MEDIUMORHIGH, HIGH }
	public enum Role { ALL, AUTHOR, OWNER, MEMBER, READER, DRAFT }
	public enum SearchOperator { AND, OR }
	public enum SharePermission { EDIT, VIEW }
	public enum Sort { DATE, POPULARITY}
	public enum SortBy { COMMENTED, CREATEDBY, MODIFIED, POPULARITY, RECOMMENDED, TITLE, NAME}
	public enum SortField { COUNT, CREATED, CREATEDBY, DUEDATE, LASTMOD, NAME}
	public enum SortOrder { ASC, DESC }
	public enum Options { NO, ONLY, YES }
	public enum View { FOLLOW, MEMBER, OWNER }
	public enum WikiRole { READER, EDITOR, MANAGER }
	public enum IdeationStatus { CLOSED, FROZEN, OPEN }
	public enum WidgetID { Activities, IdeationBlog, Blog, Calendar, Feeds, Gallery, Library, RelatedCommunities, SubcommunityNav, Wiki }
	public enum DeploymentType {ON_PREMISE, SMARTCLOUD, MULTI_TENANT};
	public enum IndexNowOnCloudType {WAIT, WSADMIN};

	// Testing Users place hold with default value from BVT server lc45linux1
	// The value will replace from your ProfileData_<server>.properties
	//index 0 :  admin user 
	public static String ADMIN_USER_NAME = "ajones1";   
	public static String ADMIN_USER_PASSWORD = "jones1";
	public static String ADMIN_USER_REALNAME = "Amy Jones1";
	public static String ADMIN_USER_EMAIL = "ajones1@janet.iris.com";
	public static int ADMIN_USER = 0;	
	//index 1 : Optional -This user has employee.extended rights.   ajones480.
	public static int EMPLOYEE_EXTENDED_USER=1;  
	//index 2 : normal login/current user 
	public static String USER_NAME = "ajones242"; 
	public static String USER_PASSWORD = "jones242"; 
	public static String USER_REALNAME = "Amy Jones242";
	public static String USER_EMAIL = "ajones242@janet.iris.com";
	public static String USER_LOGIN_1 = "ajones242";
	public static String USER_LOGIN_2 = "amy%20jones242";
	public static int CURRENT_USER = 2;
	//index 3 :  moderator user 
	public static String MODERATOR_USER_NAME = "ajones2"; 
	public static String MODERATOR_USER_PASSWORD = "jones2";
	public static String MODERATOR_USER_REALNAME = "Amy Jones2";
	public static String MODERATOR_USER_EMAIL = "ajones2@janet.iris.com";
	public static int MODERATOR_USER = 3;
	// index 5 - 12 : random test users
	// index 5 :
	public static String RANDOM1_USER_NAME = "ajones100";   
	public static String RANDOM1_USER_PASSWORD = "jones100";
	public static String RANDOM1_USER_REALNAME = "Amy Jones100";
	public static String RANDOM1_USER_EMAIL = "ajones100@janet.iris.com";
	public static int RANDOM1_USER = 5;
	// index 12 :
	public static String RANDOM2_USER_NAME = "ajones108";  
	public static String RANDOM2_USER_PASSWORD = "jones108";
	public static String RANDOM2_USER_REALNAME = "Amy Jones108";
	public static String RANDOM2_USER_EMAIL = "ajones108@janet.iris.com";	
	public static int RANDOM2_USER = 12;
	// index 13 : Connection WAS admin - index 13 from properties
	public static String CONNECTIONS_ADMIN_USER_NAME = "wasadmin";  
	public static String CONNECTIONS_ADMIN_USER_PASSWORD = "wasadmin";
	public static int CONNECTIONS_ADMIN_USER = 13;
	// index 14 :
	public static String INACTIVE_USER_NAME = "ajones110";  
	public static String INACTIVE_USER_REALNAME = "Amy Jones110";
	public static String INACTIVE_USER_EMAIL = "ajones110@janet.iris.com";
	// index 15 :
	//This user is for smart cloud testing only.
	//jillwhite01@bluebox.lotus.com/passw0rd	Org=FVT Org One
	public static String EXTERNAL_USER_NAME = "jwhite01";  //  user - index 15 from properties
	public static String EXTERNAL_USER_PASSWORD = "passw0rd";
	public static String EXTERNAL_USER_REALNAME = "Jill White01";
	public static String EXTERNAL_USER_EMAIL = "jillwhite01@bluebox.lotus.com";
	public static int EXTERNAL_USER=15;
	
	//  non exist user - no index from properties
	public static String ADDED_USER_NAME = "ajones678";  
	public static String ADDED_USER_PASSWORD = "jones678";
	public static String ADDED_USER_REALNAME = "Amy Jones678";
	public static String ADDED_USER_EMAIL = "ajones678@janet.iris.com";	
	
	public static String GROUP_NAME = "Amy Jones";
	public static String ORGID = "";
	public static String ORGADMINGK = "false";
	public static boolean QUICK_RESULTS_ENABLED = false;
	public static String MQ_SERVER = "";
	public static String DOWNLOAD_SERVER = ""; 
	
	
	// Server specific configuration settings
	public static boolean VMODEL_ENABLED = false;
	public static boolean MODERATION_ENABLED = false;
	public static String LOG_LEVEL = "info";       //{"info", "debug", "trace"};
	public static DeploymentType DEPLOYMENT_TYPE = DeploymentType.ON_PREMISE;
	public static String AUTHENTICATION = "basic";  //{"basic", "form", "webseal"};
	public static void setAuthentication(String auth) {
		AUTHENTICATION = auth;
	}
	public static boolean IMPERSONATION_ENABLED = true;
	public static boolean shouldServiceFeedLog = true;
	
	// HTML Attributes
	public static final String ATTR_NAME = "name";
	public static final String ATTR_FID = "fid";
	public static final String ATTR_HIDDEN = "hidden";
	public static final String ATTR_POSITION = "position";
	public static final String ATTR_TYPE = "type";
	public static final String ATTR_ENABLED = "enabled";
	public static final String ATTR_DAYS = "days";
	public static final String ATTR_SCHEME = "scheme";
	public static final String ATTR_MEMBERSHIP = "membership";
	public static final String ATTR_KEY = "key";
	public static final String ATTR_DATA = "data";
	public static final String ATTR_ACTION = "action";
	public static final String ATTR_CUSTOM = "custom";
	public static final String ATTR_FREQUENCY = "frequency";
	public static final String ATTR_INTERVAL = "interval";
	
	public static final QName ATTR_REL = new QName("rel");
	public static final QName ATTR_REF = new QName("ref");
	public static final QName ATTR_HREF = new QName("href");
	
	//Search Constants
	public static final String SEARCH_TERM = "Waldo";
	public static final String SEARCH_TAG_TERM = "Carmen";
	public static final String SEARCH_ACTIVITY_NAME = SEARCH_TERM + " A";
	public static final String SEARCH_BLOGS_NAME = SEARCH_TERM + " B";
	public static final String SEARCH_COMMUNITY_NAME = SEARCH_TERM + " C";
	public static final String SEARCH_DOGEAR_NAME = SEARCH_TERM + " D";
	public static final String SEARCH_FILES_NAME = SEARCH_TERM + " Fi";
	public static final String SEARCH_FORUM_NAME = SEARCH_TERM + " Fo";
	public static final String SEARCH_WIKI_NAME = SEARCH_TERM + " Wiki";
	public static final int SEARCH_ACTIVITY_INDEX = 0;
	public static final int SEARCH_BLOGS_INDEX = 1;
	public static final int SEARCH_COMMUNITY_INDEX = 2;
	public static final int SEARCH_DOGEAR_INDEX = 3;
	public static final int SEARCH_FILES_INDEX = 4;
	public static final int SEARCH_FORUM_INDEX = 5;
	public static final int SEARCH_WIKI_INDEX = 6;
	public static final int SEARCH_API_PAGE_SIZE_DEFAULT = 40;
	public static final String WIKI_PAGE_TITLE_PREFIX = "Welcome to ";
	public static IndexNowOnCloudType INDEX_NOW_ON_CLOUD_TYPE = IndexNowOnCloudType.WAIT;
	// Auth Types
	public static final String AUTH_BASIC = "basic";
	public static final String AUTH_REALM_FORCED = "lotus-connections";
	public static final String AUTH_REALM_ACTIVITIES = "Activities";
	public static final String AUTH_REALM_BLOGS = "Blogs";
	public static final String AUTH_REALM_COMMUNITIES = "Communities";
	public static final String AUTH_REALM_CRE = "CRE";
	public static final String AUTH_REALM_DOGEAR = "Dogear";
	public static final String AUTH_REALM_FILES = "Files";
	public static final String AUTH_REALM_FORUMS = "Forums";
	public static final String AUTH_REALM_PROFILES = "Profiles";
	public static final String AUTH_REALM_WIKIS = "Wikis";
	public static final String AUTH_REALM_NEWS = "News";
	
	// QNames;
	public static final QName API_IMAGE = new QName("http://www.w3.org/2005/Atom", "image");
	public static final QName CONTENT_TYPE = new QName("api", "Content-Type");
	public static final QName CONTENT_TYPE_LOWERCASE = new QName("api", "content-type");
	public static final QName CONTENT_LENGTH = new QName("api", "Content-Length");
	public static final QName CONTENT_LENGTH_LOWERCASE = new QName("api", "content-length");
	
	// Atom Namespace
	public static final QName ATOM_ENTRY = new QName("http://www.w3.org/2005/Atom", "entry");
	public static final QName ATOM_TITLE = new QName("http://www.w3.org/2005/Atom", "title");
	public static final QName ATOM_LINK = new QName("http://www.w3.org/2005/Atom", "link");
	public static final QName ATOM_CATEGORY = new QName("http://www.w3.org/2005/Atom", "category");
	public static final QName ATOM_GENERATOR = new QName("http://www.w3.org/2005/Atom","generator");

	// Widget
	public static final String NS_PREFIX = "atom";
    public static final QName WIDGET_DEFID = new QName("http://www.w3.org/2005/Atom", "widgetDefId", NS_PREFIX);
    public static final QName WIDGET_INSTANCID = new QName("http://www.w3.org/2005/Atom", "widgetInstanceId", NS_PREFIX);
    public static final QName WIDGET_HIDDEN = new QName("http://www.w3.org/2005/Atom", "hidden", NS_PREFIX);
    public static final QName WIDGET_LOCATION = new QName("http://www.w3.org/2005/Atom", "location", NS_PREFIX);
    public static final QName WIDGET_PREVIOUSWIDGETINSTANCEID = new QName("http://www.w3.org/2005/Atom", "previousWidgetInstanceId", NS_PREFIX);
    public static final QName WIDGET_MANDATED = new QName("http://www.w3.org/2005/Atom", "mandated", NS_PREFIX);
    public static final QName WIDGET_CUSTOMTITLE = new QName("http://www.w3.org/2005/Atom", "customTitle", NS_PREFIX);


	
	public static final QName OS_PERSON = new QName("http://ns.opensocial.org/2008/opensocial", "person");
	public static final QName OS_ENTRY = new QName("http://ns.opensocial.org/2008/opensocial", "entry");
	
	// App Namespace
	public static final QName APP_COLLECTION = new QName("http://www.w3.org/2007/app", "collection", "app");
	public static final QName APP_CONTROL = new QName("http://www.w3.org/2007/app", "control", "app");
	public static final QName APP_EDITED = new QName("http://www.w3.org/2007/app", "edited", "app");
	public static final QName APP_DRAFT = new QName("http://www.w3.org/2007/app", "draft", "app");
	
	public static final QName THR_IN_REPLY_TO = new QName("http://purl.org/syndication/thread/1.0", "in-reply-to", "thr");

	// SNX Namespace
    public static final QName SNX_WIDGET_DEFID = new QName("http://www.ibm.com/xmlns/prod/sn", "widgetDefId", "snx");
    public static final QName SNX_WIDGET_INSTANCID = new QName("http://www.ibm.com/xmlns/prod/sn", "widgetInstanceId", "snx");
    public static final QName SNX_WIDGET_HIDDEN = new QName("http://www.ibm.com/xmlns/prod/sn", "hidden", "snx");
    public static final QName SNX_WIDGET_LOCATION = new QName("http://www.ibm.com/xmlns/prod/sn", "location", "snx");
    public static final QName SNX_WIDGET_PREVIOUSWIDGETINSTANCEID = new QName("http://www.ibm.com/xmlns/prod/sn", "previousWidgetInstanceId", "snx");
    public static final QName SNX_WIDGET_MANDATED = new QName("http://www.ibm.com/xmlns/prod/sn", "mandated", "snx");
    public static final QName SNX_WIDGET_CUSTOMTITLE = new QName("http://www.ibm.com/xmlns/prod/sn", "customTitle", "snx");

	public static final QName SNX_ACTIVITY = new QName("http://www.ibm.com/xmlns/prod/sn", "activity", "snx");
	public static final QName SNX_ASSIGNEDTO = new QName("http://www.ibm.com/xmlns/prod/sn", "assignedto", "snx");
	public static final QName SNX_ASSIGNEES = new QName("http://www.ibm.com/xmlns/prod/sn", "assignees", "snx");
	public static final QName SNX_COMMUNITY_TYPE = new QName("http://www.ibm.com/xmlns/prod/sn", "communityType", "snx");
	public static final QName SNX_LIST_WHEN_RESTRICTED = new QName("http://www.ibm.com/xmlns/prod/sn", "listWhenRestricted", "snx");
	public static final QName SNX_CONTENTMODIFIED = new QName("http://www.ibm.com/xmlns/prod/sn", "contentModifiedWhen", "snx");
	public static final QName SNX_DAYLIGHT = new QName("http://www.ibm.com/xmlns/prod/sn", "daylight", "snx");
	public static final QName SNX_DEPTH = new QName("http://www.ibm.com/xmlns/prod/sn", "depth", "snx");
	public static final QName SNX_DUEDATE = new QName("http://www.ibm.com/xmlns/prod/sn", "duedate", "snx");
	public static final QName SNX_FIELD = new QName("http://www.ibm.com/xmlns/prod/sn", "field", "snx");
	public static final QName SNX_TEMPLATE_ID = new QName("http://www.ibm.com/xmlns/prod/sn", "template-id", "snx");
	public static final QName SNX_COMMUNITY_UUID = new QName("http://www.ibm.com/xmlns/prod/sn", "communityUuid", "snx");
	public static final QName SNX_COMMUNITY_LAYOUT = new QName("http://www.ibm.com/xmlns/prod/sn", "communityLayout", "snx");
	public static final QName SNX_POSITION = new QName("http://www.ibm.com/xmlns/prod/sn", "position", "snx");
	public static final QName SNX_USERID = new QName("http://www.ibm.com/xmlns/prod/sn", "userid", "snx");
	public static final QName SNX_USER_STATE = new QName("http://www.ibm.com/xmlns/prod/sn", "userState", "snx");
	public static final QName SNX_ROLE = new QName("http://www.ibm.com/xmlns/prod/sn", "role", "snx");
	public static final QName SNX_ACLALIAS = new QName("http://www.ibm.com/xmlns/prod/sn", "aclAlias", "snx");
	public static final QName SNX_EDITABLE_FIELD = new QName("http://www.ibm.com/xmlns/prod/sn", "editableFields", "snx");
	public static final QName SNX_PERMISSIONS = new QName("http://www.ibm.com/xmlns/prod/sn", "permissions", "snx");
	public static final QName SNX_ICON = new QName("http://www.ibm.com/xmlns/prod/sn", "icon", "snx");
	public static final QName SNX_BLOGS_HOMEPAGE_HANDLE = new QName("http://www.ibm.com/xmlns/prod/sn", "BlogsHomepageHandle", "snx");
	public static final QName SNX_HANDLE = new QName("http://www.ibm.com/xmlns/prod/sn", "handle", "snx");
	public static final QName SNX_TIMEZONE = new QName("http://www.ibm.com/xmlns/prod/sn", "timezone", "snx");
	public static final QName SNX_COMMENTS = new QName("http://www.ibm.com/xmlns/prod/sn", "comments", "snx");
	public static final QName SNX_EMAIL_COMMENTS = new QName("http://www.ibm.com/xmlns/prod/sn", "emailcomments", "snx");
	public static final QName SNX_COMMENT_MODERATED = new QName("http://www.ibm.com/xmlns/prod/sn", "commentmoderated", "snx");
	public static final QName SNX_ALLOW_COEDIT = new QName("http://www.ibm.com/xmlns/prod/sn", "AllowCoedit", "snx");
	public static final QName SNX_RANK = new QName("http://www.ibm.com/xmlns/prod/sn", "rank", "snx");
	public static final QName SNX_HIT_RANK = new QName("http://www.ibm.com/xmlns/prod/sn/hit", "rank", "snx");
	public static final QName SNX_CONTAINER_TYPE = new QName("http://www.ibm.com/xmlns/prod/sn", "containertype", "snx");
	public static final QName SNX_CONTAINER_ID = new QName("http://www.ibm.com/xmlns/prod/sn", "containerid", "snx");
	public static final QName SNX_MAP_ROLE = new QName("http://www.ibm.com/xmlns/prod/sn", "maprole", "snx");
	public static final QName SNX_LOCALE = new QName("http://www.ibm.com/xmlns/prod/sn", "locale", "snx");
	public static final QName SNX_VOTE_LIMIT = new QName("http://www.ibm.com/xmlns/prod/sn", "voteLimit", "snx");
	public static final QName SNX_MODERATION = new QName("http://www.ibm.com/xmlns/prod/sn", "moderation", "snx");
	public static final QName SNX_CLICK_COUNT = new QName("http://www.ibm.com/xmlns/prod/sn", "clickcount", "snx");
	public static final QName SNX_LINK = new QName("http://www.ibm.com/xmlns/prod/sn", "link", "snx");
	public static final QName SNX_LINK_COUNT = new QName("http://www.ibm.com/xmlns/prod/sn", "linkcount", "snx");
	public static final QName SNX_MEMBER_COUNT = new QName("http://www.ibm.com/xmlns/prod/sn", "membercount", "snx");
	public static final QName SNX_FREQUENCY = new QName("http://www.ibm.com/xmlns/prod/sn", "frequency", "snx");
	public static final QName SNX_RECIPIENTS = new QName("http://www.ibm.com/xmlns/prod/sn", "recipients", "snx");
	public static final QName SNX_INREFTO = new QName("http://www.ibm.com/xmlns/prod/sn", "in-ref-to", "snx");
	public static final QName SNX_MAPROLE = new QName("http://www.ibm.com/xmlns/prod/sn", "maprole", "snx");
	public static final QName SNX_RECURRENCE = new QName("http://www.ibm.com/xmlns/prod/sn", "recurrence", "snx");
	public static final QName SNX_REPEATS = new QName("http://www.ibm.com/xmlns/prod/sn", "repeats", "snx");
	public static final QName SNX_LOCATION = new QName("http://www.ibm.com/xmlns/prod/sn", "location", "snx");
	public static final QName SNX_ALLDAY = new QName("http://www.ibm.com/xmlns/prod/sn", "allday", "snx");
	public static final QName SNX_PERIOD = new QName("http://www.ibm.com/xmlns/prod/sn", "period", "snx");
	public static final QName SNX_STARTDATE = new QName("http://www.ibm.com/xmlns/prod/sn", "startDate", "snx");
	public static final QName SNX_ENDDATE = new QName("http://www.ibm.com/xmlns/prod/sn", "endDate", "snx");
	public static final QName SNX_UNTIL = new QName("http://www.ibm.com/xmlns/prod/sn", "until", "snx");
	public static final QName SNX_BYDAY = new QName("http://www.ibm.com/xmlns/prod/sn", "byDay", "snx");
	public static final QName SNX_BYDAYOFWEEK = new QName("http://www.ibm.com/xmlns/prod/sn","byDayOfWeek","snx");
	public static final QName SNX_BYDATE = new QName("http://www.ibm.com/xmlns/prod/sn","byDate","snx");
	public static final QName SNX_FOLLOWED = new QName("http://www.ibm.com/xmlns/prod/sn", "followed", "snx");
	public static final QName SNX_ATTENDED = new QName("http://www.ibm.com/xmlns/prod/sn", "attended", "snx");
	public static final QName SNX_EVENTUUID = new QName("http://www.ibm.com/xmlns/prod/sn", "eventUuid", "snx");
	public static final QName SNX_EVENT_INST_UUID = new QName("http://www.ibm.com/xmlns/prod/sn", "eventInstUuid", "snx");
	public static final QName SNX_ISEXTERNAL = new QName("http://www.ibm.com/xmlns/prod/sn", "isExternal", "snx");
	public static final QName SNX_RECIPIENT_EMAIL = new QName("http://www.ibm.com/xmlns/prod/sn", "recipient", "snx");
	public static final QName SNX_STARTPAGE = new QName("http://www.ibm.com/xmlns/prod/sn", "communityStartPage", "snx");
	public static final QName SNX_MEMBER_EMAIL_PRIVILEGES = new QName("http://www.ibm.com/xmlns/prod/sn", "memberEmailPrivileges", "snx");
	public static final QName SNX_SUBSCRIPTION = new QName("http://www.ibm.com/xmlns/prod/sn", "mailSubscription", "snx");
	public static final QName SNX_COPY_FROM_COMMUNITY_UUID = new QName("http://www.ibm.com/xmlns/prod/sn", "copyFromCommunityUuid", "snx");
	public static final QName SNX_PREMODERATION = new QName("http://www.ibm.com/xmlns/prod/sn", "preModeration", "snx");
	
	public static final QName API_ERROR = new QName("api", "error", "resp");
	public static final QName API_RESPONSE_CODE = new QName("api", "code", "resp");
	public static final QName API_RESPONSE_MSG = new QName("api", "msg", "resp");
	
	public static final String OPENSEARCH_NS = "http://a9.com/-/spec/opensearch/1.1/";
	public static final String TOTAL_RESULTS_LN  = "totalResults";
	public static final String OS_PREFIX = "os";
	public static final QName TOTAL_RESULTS  = new QName(OPENSEARCH_NS, TOTAL_RESULTS_LN, OS_PREFIX);
	public static final QName FIELD_NAMESPACE  = new QName("http://www.ibm.com/search/content/2010", "field", "ibmsc");
	// TD Namespace
	public static final QName TD_COMMENT_NOTIFICATION = new QName("urn:ibm.com/td", "commentNotification", "td");
	public static final QName TD_CREATED = new QName("urn:ibm.com/td", "created", "td");
	public static final QName TD_INCLUDE_PATH = new QName("urn:ibm.com/td", "includePath", "td");
	public static final QName TD_LABEL = new QName("urn:ibm.com/td", "label", "td");
	public static final QName TD_LIBRARY_SIZE = new QName("urn:ibm.com/td", "librarySize", "td");
	public static final QName TD_MEDIA_NOTIFICATION = new QName("urn:ibm.com/td", "mediaNotification", "td");
	public static final QName TD_MODIFIED = new QName("urn:ibm.com/td", "modified", "td");
	public static final QName TD_NOTIFICATION = new QName("urn:ibm.com/td", "notification", "td");
	public static final QName TD_PATH =  new QName("urn:ibm.com/td", "path", "td");
	public static final QName TD_PROPAGATE = new QName("urn:ibm.com/td", "propagate", "td");
	public static final QName TD_RECOMMENDATION = new QName("urn:ibm.com/td", "recommendation", "td");
	public static final QName TD_REMOVE_TAG = new QName("urn:ibm.com/td", "removeTag", "td");
	public static final QName TD_RESTRICTED_VISIBILITY = new QName("urn:ibm.com/td", "restrictedVisibility", "td");
	public static final QName TD_SEND_NOTIFICATION = new QName("urn:ibm.com/td", "sendNotification", "td");
	public static final QName TD_SHARE_PERMISSION = new QName("urn:ibm.com/td", "sharePermission", "td");
	public static final QName TD_SHARE_SUMMARY = new QName("urn:ibm.com/td", "shareSummary", "td");
	public static final QName TD_SHARE_WITH = new QName("urn:ibm.com/td", "shareWith", "td");
	public static final QName TD_SHARED_WITH = new QName("urn:ibm.com/td", "sharedWith", "td");
	public static final QName TD_SHARED_WHAT = new QName("urn:ibm.com/td", "sharedWhat", "td");
	public static final QName TD_TAG = new QName("urn:ibm.com/td", "tag", "td");
	public static final QName TD_TITLE = new QName("urn:ibm.com/td", "title", "td");
	public static final QName TD_VISIBILITY = new QName("urn:ibm.com/td", "visibility", "td");
	public static final QName TD_UUID = new QName("urn:ibm.com/td", "uuid", "td");
	public static final QName TD_LIBRARY_ID = new QName("urn:ibm.com/td", "libraryId", "td");
	public static final QName TD_LIBRARY = new QName("urn:ibm.com/td", "library", "td");
	public static final QName TD_LIBRARY_UUID = new QName("urn:ibm.com/td", "libraryUuid", "td");
	public static final QName TD_USER = new QName("urn:ibm.com/td", "user", "td");
	public static final QName TD_VERSIONUUID = new QName("urn:ibm.com/td", "versionUuid", "td");
	public static final QName TD_MEDIA_SIZE = new QName("urn:ibm.com/td", "totalMediaSize", "td");
	
	public static final QName TD_PERMISSIONS = new QName("urn:ibm.com/td", "permissions", "td"); 
	public static final QName TD_CHANGESUMMARY = new QName("urn:ibm.com/td", "changeSummary"); 
	//public static final QName TD_VERSIONUUID = new QName("urn:ibm.com/td", "versionUuid");
	
	//Blogs
	public static final QName SNX_BLOGS_HANDLE = new QName("http://www.ibm.com/xmlns/prod/sn", "handle", "snx");
	public static final QName BLOGS_COLLECTION = new QName("http://www.w3.org/2007/app", "weblog Entries");
	public static final QName SNX_EDIT_COMMENT = new QName("http://www.ibm.com/xmlns/prod/sn", "canEditComment", "snx");
	public static final QName SNX_DELETE_COMMENT = new QName("http://www.ibm.com/xmlns/prod/sn", "canDeleteComment", "snx");
	public static final QName SNX_BLOGS_RECOMMENDATION = new QName("http://www.ibm.com/xmlns/prod/sn", "rank", "snx");
	public static final QName OS_STARTINDEX = new QName("http://a9.com/-/spec/opensearch/1.1/", "startIndex", "os");
	public static final QName OS_ITEMS_PER_PAGE = new QName("http://a9.com/-/spec/opensearch/1.1/", "itemsPerPage", "os");
	
	//Below are the Qname objects needed to construct an entry which will allow a user to share a file with another user through the API
	//Implementation in APIFileHandler shareFile()
	public static final QName SHARE_PERMISSION = new QName("urn:ibm.com/td", "sharePermission", "");
	public static final QName SHARE_WITH = new QName("urn:ibm.com/td", "shareWith", "");
	public static final QName SHARED_WITH = new QName("urn:ibm.com/td", "sharedWith", "");
	public static final QName SHARED_WHAT = new QName("urn:ibm.com/td", "sharedWhat", "");
	public static final QName USERID = new QName("http://www.ibm.com/xmlns/prod/sn", "userid", "");
	public static final QName USER = new QName("urn:ibm.com/td", "user", ""); 
		
	//Wikis
	public static final String WIKIS_NAMESPACE = ":ibm.com/td";

	public static final QName TD_WIKI_LABEL = new QName("urn:ibm.com", "label", "td");
	public static final QName TD_WIKI_PERMISSIONS = new QName("urn:ibm.com", "permissions", "td");
	public static final QName TD_WIKI_SHARED_RESOURCE_TYPE = new QName("urn:ibm.com/td", "sharedResourceType");
	public static final QName TD_WIKI_SHARED_WITH = new QName("urn:ibm.com/td", "sharedWith");
	public static final QName CA_WIKI_MEMBER = new QName("http://www.ibm.com/xmlns/prod/composite-applications/v1.0", "member");
	public static final QName CA_WIKI_MEMBER_ID = new QName("http://www.ibm.com/xmlns/prod/composite-applications/v1.0", "id", "ca");
	public static final QName CA_WIKI_MEMBER_TYPE = new QName("http://www.ibm.com/xmlns/prod/composite-applications/v1.0", "type", "ca");
	public static final QName CA_WIKI_MEMBER_ROLE = new QName("http://www.ibm.com/xmlns/prod/composite-applications/v1.0", "role", "ca");
	public static final String WIKIS_SCHEME_TYPE = "tag:ibm.com,2006:td/type";
	public static final String WIKIS_SHARED_RESOURCE = "wiki";
	
	// Activities specific constants
	public static final String ACTIVITY_DASHBOARD = "Activity Dashboard";
	public static final String ACTIVITIES_FOR = "Activities for";
	public static final String ACTIVITIES_OVERVIEW = "Overview";
	public static final String ACTIVITIES_COMPLETED= "Completed";
	public static final String ACTIVITIES_TUNED_OUT = "Tuned out";
	public static final String ACTIVITIES_TRASH = "Trash";
	public static final String ACTIVITIES_PUBLIC = "Public";
	public static final String ACTIVITIES_EVERYTHING = "Everything";
	public static final String ACTIVITIES_TO_DO_LIST = "To Do List";
	
	//Activities Header
	public static final QName HEADER_VERSION = new QName("api", "X-APIVersion");
	public static final QName HEADER_VERSION_SC = new QName("api", "x-apiversion");
	
	// News specific constants
	public static final String NEWS_SAVED = "saved-news";				// saved updates by user who auth'd the request
	public static final String NEWS_DISCOVERY= "discovery-news";		// all public updates
	public static final String NEWS_PERSON_TOP = "person-top-news";		// top news
	public static final String NEWS_PERSON_FEED= "person-news-feed";	// news feed
	public static final String NEWS_PERSON_STATUS_UPDATES= "person-status-updates";	// status updates
	
	
	// Dogear specific constants
	public static final String DOGEAR_ENTRIES = "Entries";
	public static final String DOGEAR_SEND_NOTIFICATIONS = "Send Notifications";

	// HTML Scheme
	public static final String SCHEME_TYPE = "http://www.ibm.com/xmlns/prod/sn/type";
	public static final String SCHEME_TD_TYPE = "tag:ibm.com,2006:td/type";
	public static final String SCHEME_PRIORITY = "http://www.ibm.com/xmlns/prod/sn/priority";
	public static final String SCHEME_FLAGS = "http://www.ibm.com/xmlns/prod/sn/flags";
	public static final String SCHEME_MESSAGE_TYPE = "http://www.ibm.com/xmlns/prod/sn/message-type";
	public static final String SCHEME_CONNECTION_TYPE = "http://www.ibm.com/xmlns/prod/sn/connection/type";
	public static final String SCHEME_STATUS = "http://www.ibm.com/xmlns/prod/sn/status";
	public static final String SCHEME_RECOMMENDATIONS = "http://www.ibm.com/xmlns/prod/sn/recommendations";
	public static final String SCHEME_HIT = "http://www.ibm.com/xmlns/prod/sn/hit";
	public static final String SCHEME_DEFAULT_VIEW = "http://www.ibm.com/xmlns/prod/sn/default-view";
	public static final String SCHEME_VOTES_AVAILABLE = "http://www.ibm.com/xmlns/prod/sn/votes-available";
	public static final String SCHEME_COLLECTION = "http://www.ibm.com/xmlns/prod/sn/collection";
	public static final String SCHEME_COMPONENT = "http://www.ibm.com/xmlns/prod/sn/component";
	public static final String SCHEME_ACCESS = "http://www.ibm.com/xmlns/prod/sn/accesscontrolled";
	public static final String SCHEME_DOC_TYPE = "http://www.ibm.com/xmlns/prod/sn/doctype";
	public static final String SCHEME_SOURCE = "http://www.ibm.com/xmlns/prod/sn/source";
	public static final String SCHEME_RESOURCE_TYPE = "http://www.ibm.com/xmlns/prod/sn/resource-type";
	public static final String SCHEME_RESOURCE_ID = "http://www.ibm.com/xmlns/prod/sn/resource-id";
	
	public static final String SCHEME_COMPONENT_TERM_FILES = "files"; 
	public static final String SCHEME_COMPONENT_TERM_FORUMS = "forums";
	public static final String SCHEME_COMPONENT_TERM_WIKI = "wikis";
	public static final String SCHEME_COMPONENT_TERM_COMMUNITIES = "communities";
	public static final String SCHEME_COMPONENT_TERM_ACTIVITIES = "activities";
	public static final String SCHEME_COMPONENT_TERM_ACTIVITY_ATTACHMENT = "activities:attachment";
	public static final String SCHEME_COMPONENT_TERM_BOOKMARKS = "dogear";
	public static final String SCHEME_COMPONENT_TERM_STATUS_UPDATE = "status_updates";
	public static final String SCHEME_COMPONENT_TERM_BLOGS = "blogs";
	public static final String SCHEME_COMPONENT_TERM_PROFILES = "profiles";
	
	
	public static final String CATEGORY_TERM_COMMUNITY_APP_GROUP_COMMUNITY = "Group/Community";
	public static final String CATEGORY_TERM_ACTIVITY_APP_GROUP_ACTIVITY = "Group/Activity";
	public static final String CATEGORY_TERM_FILE_APP_DOCUMENT_FILE = "Document/File";
	public static final String CATEGORY_TERM_BLOG_APP_DOCUMENT_BLOG ="Document/Blog";
	public static final String CATEGORY_TERM_FORUM_APP_DOCUMENT_FORUM ="Document/ForumThread";
	public static final String CATEGORY_TERM_WIKI_APP_DOCUMENT_WIKI ="Document/Wiki";
	public static final String CATEGORY_TERM_BOOKMARK_APP_UTIL_BOOKMARK ="Util/Bookmark";
	public static final String CATEGORY_TERM_STATUS_UPDATE_APP_DOCUMENT_STATUS_UPDATE  = "Document/StatusUpdate";
	
	
	// Link Rels
	public static final String REL_REMOTEAPPLICATIONS = "http://www.ibm.com/xmlns/prod/sn/remote-applications";
	public static final String REL_REMOTEAPPLICATION_PUBLISH = "http://www.ibm.com/xmlns/prod/sn/remote-application/publish";
	public static final String REL_REMOTEAPPLICATION_FEED = "http://www.ibm.com/xmlns/prod/sn/remote-application/feed";
	public static final String REL_CONTAINER = "http://www.ibm.com/xmlns/prod/sn/container";
	public static final String REL_SUBCOMMUNITIES = "http://www.ibm.com/xmlns/prod/sn/subcommunities";
	public static final String REL_PARENTCOMMUNITY = "http://www.ibm.com/xmlns/prod/sn/parentcommunity";
	public static final String REL_MEMBERS = "http://www.ibm.com/xmlns/prod/sn/member-list";
	public static final String REL_BOOKMARKS = "http://www.ibm.com/xmlns/prod/sn/bookmarks";
	public static final String REL_FEEDS = "http://www.ibm.com/xmlns/prod/sn/feeds";
	public static final String REL_FORUM_TOPICS = "http://www.ibm.com/xmlns/prod/sn/forum-topics";
	public static final String REL_INVITATIONS_LIST = "http://www.ibm.com/xmlns/prod/sn/invitations-list";
	public static final String REL_LOGO = "http://www.ibm.com/xmlns/prod/sn/logo";
	public static final String REL_ALTERNATE = "alternate";
	public static final String REL_REPLIES = "replies";
	public static final String REL_CHILDREN = "children";
	public static final String REL_EDIT = "edit";
	public static final String REL_SELF = "self";
	public static final String REL_VCARD = "vcard";
	public static final String REL_NAME = "name";
	public static final String REL_HISTORY = "http://www.ibm.com/xmlns/prod/sn/history";
	public static final String REL_RELATED = "related";
	public static final String REL_PUBLISH = "http://www.ibm.com/xmlns/prod/sn/remote-application/publish";
	public static final String REL_E_ATTEND = "http://www.ibm.com/xmlns/prod/sn/calendar/event/attend";
	public static final String REL_E_FOLLOW = "http://www.ibm.com/xmlns/prod/sn/calendar/event/follow";
	public static final String REL_E_INSTANCES = "http://www.ibm.com/xmlns/prod/sn/calendar/event/instances";
	public static final String REL_E_PARENTEVENT = "http://www.ibm.com/xmlns/prod/sn/calendar/event/parentevent";
	public static final String REL_WIDGETS = "http://www.ibm.com/xmlns/prod/sn/widgets";
	public static final String REL_REPORT_ITEM = "http://www.ibm.com/xmlns/prod/sn/report-item";
	public static final String REL_ISSUE = "http://www.ibm.com/xmlns/prod/sn/issue";
	public static final String REL_IMAGE = "http://www.ibm.com/xmlns/prod/sn/image";
	public static final String REL_PRONUNCIATION = "http://www.ibm.com/xmlns/prod/sn/pronunciation";
	public static final String REL_RECOMMENDATIONS = "recommendations";
	
	public static final String MIME_ATOM_XML = "application/atom+xml";
	public static final String MIME_TEXT_HTML = "text/html";
	public static final String MIME_NULL = "null";
	public static final String MIME_ATOMSVC_XML = "application/atomsvc+xml";
	public static final String LINK_TYPE_ATOM_XML = "application/atom+xml";
	//Activities
	public static final String REL_TAG_CLOUD = "http://www.ibm.com/xmlns/prod/sn/tag-cloud";

	
	//Blogs
	public static final String BLOGS = "Blogs";
	public static final String MY_BLOGS = "My Blogs";
	public static final String WEBLOG_ENTRIES = "Weblog Entries";
	public static final String MEDIA_ENTRIES = "Media Entries";
	public static final String COMMENT_ENTRIES = "Comment Entries";
	public static final String BLOG_RECOMMENDATIONS = "Recommendations";
	
	// Profiles constants
	public static final String PROFILES_SOURCE = "profiles";
	public static final String PROFILE_RESOURCE_TYPE = "profile";

	public static final String ALTERNATE_LASTNAME = "alternateLastname";
	public static final String BLDGID = "bldgId";
	public static final String BLOG_URL = "blogUrl";
	public static final String COUNTRY_CODE = "countryCode";
	public static final String COURTESY_TITLE = "courtesyTitle";
	public static final String DEPT_NUMBER = "deptNumber";
	public static final String DESCRIPTION = "description";
	public static final String DISPLAY_NAME = "displayName";
	public static final String EMAIL = "email";
	public static final String EMPLOYEE_NUMBER = "employeeNumber";
	public static final String EMPLOYEE_TYPE_CODE = "employeeTypeCode";
	public static final String EMPLOYEE_TYPE_DESC = "employeeTypeDesc";
	public static final String EXPERIENCE = "experience";
	public static final String FAX_NUMBER = "faxNumber";
	public static final String FLOOR = "floor";
	public static final String GROUPWARE_EMAIL = "groupwareEmail";
	public static final String GUID = "guid";
	public static final String IP_TELEPHONE_NUMBER = "ipTelephoneNumber";
	public static final String IS_MANAGER = "isManager";
	public static final String JOB_RESP = "jobResp";
	public static final String LAST_UPDATE = "lastUpdate";
	public static final String MANAGER_UID = "managerUid";
	public static final String MOBILE_NUMBER = "mobileNumber";
	public static final String NATIVE_FIRST_NAME = "nativeFirstName";
	public static final String NATIVE_LAST_NAME = "nativeLastName";
	public static final String OFFICE_NAME = "officeName";
	public static final String ORGANIZATION_TITLE = "organizationTitle";
	public static final String ORG_ID = "orgId";
	public static final String PAGER_ID = "pagerId";
	public static final String PAGER_SERVICE_PROVIDER = "pagerServiceProvider";
	public static final String PAGER_TYPE = "pagerType";
	public static final String PREFERRED_FIRST_NAME = "preferredFirstName";
	public static final String PREFERRED_LANGUAGE = "preferredLanguage";
	public static final String PREFERRED_LAST_NAME = "preferredLastName";
	public static final String TELEPHONE_NUMBER = "telephoneNumber";
	public static final String TIMEZONE = "timezone";
	public static final String UID = "uid";
	public static final String URL = "URL*";
	public static final String WORK_LOCATION = "workLocation";
	public static final String WORK_LOCATION_CODE = "workLocationCode";
	
	public static final QName  OPENSEARCH_TOTALRESULTS = new QName("http://a9.com/-/spec/opensearch/1.1/","totalResults");

	// VCard constants
	public static final String VCARD_ALTERNATE_LASTNAME = "X_ALTERNATE_LAST_NAME";
	public static final String VCARD_BLDGID = "X_BUILDING";
	public static final String VCARD_BLOG_URL = "X_BLOG_URL;VALUE";
	public static final String VCARD_COUNTRY_CODE = "X_COUNTRY_CODE";
	public static final String VCARD_COURTESY_TITLE = "HONORIFIC_PREFIX";
	public static final String VCARD_DEPT_NUMBER = "X_DEPARTMENT_NUMBER";
	public static final String VCARD_DESCRIPTION = "X_DESCRIPTION";
	public static final String VCARD_DISPLAY_NAME = "FN";
	public static final String VCARD_EMAIL = "EMAIL;INTERNET";
	public static final String VCARD_EMPLOYEE_NUMBER = "X_EMPLOYEE_NUMBER";
	public static final String VCARD_EMPLOYEE_TYPE_CODE = "X_EMPTYPE";
	public static final String VCARD_EMPLOYEE_TYPE_DESC = "ROLE";
	public static final String VCARD_EXPERIENCE = "X_EXPERIENCE";
	public static final String VCARD_FAX_NUMBER = "TEL;FAX";
	public static final String VCARD_FLOOR = "X_FLOOR";
	public static final String VCARD_FULL_NAME = "FN";
	public static final String VCARD_GROUPWARE_EMAIL = "EMAIL;X_GROUPWARE_MAIL";
	public static final String VCARD_GUID = "UID";
	public static final String VCARD_IP_TELEPHONE_NUMBER = "TEL;X_IP";
	public static final String VCARD_IS_MANAGER = "X_IS_MANAGER";
	public static final String VCARD_JOB_RESP = "TITLE";
	public static final String VCARD_LAST_UPDATE = "REV";
	public static final String VCARD_MANAGER_UID = "X_MANAGER_UID";
	public static final String VCARD_MOBILE_NUMBER = "TEL;CELL";
	public static final String VCARD_NATIVE_FIRST_NAME = "X_NATIVE_FIRST_NAME";
	public static final String VCARD_NATIVE_LAST_NAME = "X_NATIVE_LAST_NAME";
	public static final String VCARD_OFFICE_NAME = "X_OFFICE_NUMBER";
	public static final String VCARD_ORGANIZATION_TITLE = "ORG";
	public static final String VCARD_ORG_ID = "X_ORGANIZATION_CODE";
	public static final String VCARD_PAGER_ID = "X_PAGER_ID";
	public static final String VCARD_PAGER_SERVICE_PROVIDER = "X_PAGER_PROVIDER";
	public static final String VCARD_PAGER_TYPE = "X_PAGER_TYPE";
	public static final String VCARD_PREFERRED_FIRST_NAME = "NICKNAME";
	public static final String VCARD_PREFERRED_LANGUAGE = "X_PREFERRED_LANGUAGE";
	public static final String VCARD_PREFERRED_LAST_NAME = "X_PREFERRED_LAST_NAME";
	public static final String VCARD_PROFILE_KEY = "X_PROFILE_KEY";
	public static final String VCARD_TELEPHONE_NUMBER = "TEL;WORK";
	public static final String VCARD_TIMEZONE = "TZ";
	public static final String VCARD_UID = "X_PROFILE_UID";
	public static final String VCARD_URL = "X_BLOG_URL;VALUE";
	public static final String VCARD_WORK_LOCATION = "ADR;WORK";
	public static final String VCARD_WORK_LOCATION_CODE = "X_WORKLOCATION_CODE";
	
	// Activity Test Strings
	public static final String ACTIVITY_TITLE = "API Activity Test";
	
	// Community Specific Strings
	public static final String COMMUNITIES_PUBLIC = "Public Communities";
	public static final String COMMUNITIES_MY = "My Communities";
	public static final String COMMUNITIES_MY_INVITATIONS = "My Invitations";
	public static final String RECOMMENDATIONS = "Recommendations";
	
	public static final String MEMBERS_EMAIL_PRIVILEGES_ENTIRE_COMMUNITY = "canEmailEntireCommunity";
	public static final String MEMBERS_EMAIL_PRIVILEGES_OWNERS_ONLY = "canEmailOwnersOnly";
	public static final String MEMBERS_EMAIL_PRIVILEGES_NO_ONE = "emailNotAllowed";
	
	public static final String COMMUNITIES_SOURCE = "communities";
	public static final String COMMUNITY_RESOURCE_TYPE = "community";
	
	// include leading space here for cleaner string concatenation in test assertions
	public static final String COMMUNITY_COPY_SUFFIX = " Copy";

	// Forums Specific Strings
	public static final String FORUMS = "Forums";
	public static final String FORUMS_PUBLIC = "Public Forums";
	public static final String FORUMS_MY = "My Forums";
	public static final String FORUMS_TOPICS = "My Topics";
	
	public static final String PRE_MODERATED_LIST = "Listing of pre-moderated forum content";
	public static final String PRE_MODERATED_EDIT = "Change pre-moderated forum content approval status";
	public static final String POST_MODERATED_LIST = "Listing of post-moderated forum content";
	public static final String POST_MODERATED_EDIT = "Change post-moderated forum content review status";
	
	public static final String REF_ITEM_TYPE = "ref-item-type";
	
	public static final String[] LOREM_1 = {"Lorem ipsum dolor sit amet, consectetur adipiscing elit. ",
			"Vestibulum ac nisi non nibh placerat volutpat et in lacus. ",
			"Fusce ac quam vehicula turpis vulputate auctor. ",
			"Donec a nunc ac ligula ornare feugiat a non massa. ",
			"Cras id augue velit. Lorem ipsum dolor sit amet, consectetur adipiscing elit. ",
			"Vestibulum malesuada eleifend ipsum, cursus condimentum neque fermentum sit amet. ",
			"Cras euismod nisl eget purus facilisis ac tincidunt elit malesuada. ",
			"Quisque dui odio, malesuada non euismod nec, vulputate ac tellus.\n\n"};
	
	public static final String[] LOREM_2 = {"Morbi urna erat, eleifend et egestas non, mattis eu metus. ",
			"Quisque mollis congue ipsum at euismod. ",
			"Etiam non imperdiet tortor. ",
			"Duis id leo non dui scelerisque luctus. ",
			"Etiam sit amet nisl sem, ac sagittis ante. ",
			"Sed tristique blandit semper. ",
			"Phasellus euismod tempus sagittis. ",
			"Pellentesque blandit accumsan nisi, a suscipit neque lacinia ac. ",
			"Nullam sapien tortor, ultricies ac suscipit at, adipiscing a mauris. ",
			"Proin mattis arcu at risus bibendum vitae condimentum magna volutpat. ",
			"Aenean a risus ac felis tristique porttitor. ",
			"In euismod elit magna. ",
			"Maecenas congue lobortis lacinia. ",
			"Vestibulum tortor enim, laoreet in lacinia vitae, dapibus at neque. ",
			"Ut pharetra, mi vel gravida feugiat, purus ante suscipit mauris, id laoreet orci risus quis lacus. ",
			"Ut adipiscing rhoncus dictum.\n\n" };
			
	public static final String STRING_SPACE_SEPERATOR = " ";
	
	public static final String STRING_COMPLETED = "completed";
	public static final String STRING_DELETED = "deleted";
	public static final String STRING_RECENT_UPDATES = "Recent Updates";
	public static final String STRING_TEMPLATE_LOWERCASE = "template";
	public static final String STRING_TEMPLATE_CAPITALIZED = "Template";
	
	public static final String STRING_ACTIVITY_LOWERCASE = "activity";
	public static final String STRING_ACTIVITY_CAPITALIZED = "Activity";
	
	public static final String STRING_COMMUNITY_ACTIVITY_LOWERCASE = "community_activity";
	public static final String STRING_COMMUNITY_ACTIVITY_CAPITALIZED = "Community Activity";
	
	public static final String STRING_PRIVATE_LOWERCASE = "private";
	public static final String STRING_PRIVATE_CAPITALIZED = "Private";
	
	public static final String STRING_PUBLIC_LOWERCASE = "public";
	public static final String STRING_PUBLIC_CAPITALIZED = "Public";

	public static final String STRING_MODERATED_LOWERCASE = "moderated";
	public static final String STRING_MODERATED_CAPITALIZED = "Moderated";
	
	public static final String STRING_ENTRY_LOWERCASE = "entry";
	public static final String STRING_ENTRY_CAPITALIZED = "Entry";
	
	public static final String STRING_ATTACHMENT = "Attachment";
	public static final String STRING_LINK_TO_FILE = "Link to File";
	public static final String STRING_LINK_TO_FOLDER = "Link to Folder";
	public static final String STRING_BOOKMARK = "Bookmark";
	public static final String STRING_DATE = "Date";
	
	public static final String STRING_CHAT_LOWERCASE = "chat";
	public static final String STRING_CHAT_CAPITALIZED = "Chat";
	
	public static final String STRING_EMAIL_LOWERCASE = "email";
	public static final String STRING_EMAIL_CAPITALIZED = "Email";
	
	public static final String STRING_REPLY_LOWERCASE = "reply";
	public static final String STRING_REPLY_CAPITALIZED = "Reply";
	
	public static final String STRING_SECTION_LOWERCASE = "section";
	public static final String STRING_SECTION_CAPITALIZED = "Section";
	
	public static final String STRING_TODO_LOWERCASE = "todo";
	public static final String STRING_TODO_CAPITALIZED = "Todo";
	
	public static final String STRING_BLOG_LOWERCASE = "blog";
	public static final String STRING_BLOG_CAPITALIZED = "Blog";
	
	public static final String STRING_COMMUNITY_BLOG_LOWERCASE = "communityblog";
	public static final String STRING_COMMUNITY_BLOG_CAPITALIZED = "Community Blog";
	
	public static final String STRING_IDEATION_BLOG_LOWERCASE = "ideationblog";
	public static final String STRING_IDEATION_BLOG_CAPITALIZED = "Ideation Blog";
	
	public static final String STRING_OPEN_LOWERCASE = "open";
	public static final String STRING_OPEN_CAPITALIZED = "Open";
	
	public static final String STRING_FROZEN_LOWERCASE = "frozen";
	public static final String STRING_FROZEN_CAPITALIZED = "Frozen";
	
	public static final String STRING_CLOSED_LOWERCASE = "closed";
	public static final String STRING_CLOSED_CAPITALIZED = "Closed";
	
	public static final String STRING_MEMBER = "member";
	public static final String STRING_IMPORTANT_LOWERCASE = "important";
	
	public static final String STRING_BOOKMARK_LOWERCASE = "bookmark";
	public static final String STRING_BOOKMARK_CAPITALIZED = "Bookmark";
	
	public static final String STRING_INTERNAL_LOWERCASE = "internal";
	public static final String STRING_INTERNAL_CAPITALIZED = "Internal";
	
	public static final String STRING_COMMUNITY_LOWERCASE = "community";
	public static final String STRING_COMMUNITY_CAPITALIZED = "Community";
	
	public static final String STRING_PUBLIC_INVITE_ONLY = "publicInviteOnly";
	
	public static final String STRING_FEED_LINK_LOWERCASE = "feed";
	public static final String STRING_FEED_LINK_CAPITALIZED = "Feed";
	
	public static final String STRING_INVITE_LOWERCASE = "invite";
	public static final String STRING_INVITE_CAPITALIZED = "Invite";
	
	public static final String STRING_FORUM_FORUM_LOWERCASE = "forum-forum";
	public static final String STRING_LOCKED_LOWERCASE = "locked";
	
	public static final String STRING_ENTRY_TEMPLATE_LOWERCASE = "entrytemplate";
	public static final String STRING_RELATED_ACTIVITY_LOWERCASE = "link";
	
	public static final String STRING_SEARCH_LOWERCASE = "search";
	public static final String STRING_SEARCH_CAPITALIZED = "Search";
	
	public static final String STRING_CALENDAR_LOWERCASE = "calendar";
	public static final String STRING_CALENDAR_CAPITALIZED = "Calendar";
	
	public static final String STRING_EVENT_LOWERCASE = "event";
	public static final String STRING_EVENT_CAPITALIZED = "Event";
	
	public static final String STRING_WIDGET_LOWERCASE = "widget";
	public static final String STRING_WIDGET_CAPITALIZED = "WIDGET";
	
	public static final String STRING_EVENT_COMMENT = "comment";
	
	public static final String STRING_WEEKLY = "weekly";
	public static final String STRING_MONTHLY = "monthly";
	public static final String STRING_MONTHLY_BY_DAY = "monthlyByDay";
	public static final String STRING_MONTHLY_BY_DAY_OF_WEEK = "monthlyByDayOfWeek";
	public static final String STRING_YES_LOWERCASE = "yes";
	public static final String STRING_NO_LOWERCASE = "no";
	
	public static final QName RELEVENCE_SCORE = new QName("http://a9.com/-/opensearch/extensions/relevance/1.0/", "score", "relevence");
	
	public static final String PROFILES_ADMIN_ALL_USERS = "All User Profiles";
	
	static final Map<Component, String> componentRealmMap;
	
	public static final String PERMISSIONS_DELETE_ACTIVITY = "delete_activity";
	
	static {
        Map<Component, String> tempMap = new HashMap<Component, String>();
        tempMap.put(Component.ACTIVITIES, "Activities");
        tempMap.put(Component.AUTHCONNECTOR, "AuthConnector");
        tempMap.put(Component.BLOGS, "Blogs");
        tempMap.put(Component.COMMUNITIES, "Communities");
        tempMap.put(Component.CRE, "CRE");
        tempMap.put(Component.DOGEAR, "Dogear");
        tempMap.put(Component.FILES, "Files");
        tempMap.put(Component.FORUMS, "Forums");
        tempMap.put(Component.PROFILES, "Profiles");
        tempMap.put(Component.WIKIS, "Wikis");
        tempMap.put(Component.SEARCH, "Search");
        componentRealmMap = Collections.unmodifiableMap(tempMap);
    }
	
	public static final String[] SCFULLLIST = {"/", "?", ">", "<", "'", "\"", ";", ":", "\\", "|", "{", "}", "[", "]", "=", "+", "-", 
												"_", ")", "(", "&", "*", "^", "%", "$", "#", "@", "!", "`", "~"};
	public static final String[] SCFILESVALIDLIST = {"'", ";", "{", "}", "[", "]", "=", "+", "-", "_", ")", "(", 
												"&", "^", "%", "$", "#", "@", "!", "`", "~"};
	public static final String[] SCFILESINVALIDLIST = {"*", "<", ">", "\"", "/", "?", ":", "\\", "|"};
	public static final String[] SCWIKISVALIDLIST = {"'", ";", "{", "}", "=", "+", "-", "_", ")", "(", 
		"&", "^", "%", "$", "#", "@", "!", "`", "~"};
	public static final String[] SCWIKISINVALIDLIST = {"*", "<", ">", "\"", "/", "?", ":", "\\", "|", "[", "]"};
	public static final String[] SCCOMBOLIST = {"@._", "kat*$", "()*", ".@", "/_\\"};
	
	
}
