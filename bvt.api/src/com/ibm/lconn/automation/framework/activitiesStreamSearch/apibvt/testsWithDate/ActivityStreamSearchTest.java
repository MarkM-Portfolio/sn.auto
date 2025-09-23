package com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.testsWithDate;

import java.util.LinkedHashMap;
import java.util.logging.Logger;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.FVTUtilsWithDate;
import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.PopStringConstantsAS;
import com.ibm.lconn.automation.framework.activitiesStreamSearch.utils.json.JsonClient;
import com.ibm.lconn.automation.framework.activitiesStreamSearch.utils.nodes.FvtMasterLogsClass;
import com.ibm.lconn.automation.framework.services.common.ProfileData;
import com.ibm.lconn.automation.framework.services.common.ProfileLoader;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.profiles.nodes.VCardEntry;

public class ActivityStreamSearchTest {
	
	
	protected static Logger LOGGER = FvtMasterLogsClass.LOGGER;
	protected static String userIDLong = "";
	protected static String userIDShort = "";		
	protected static String urlPrefix = "/connections/opensocial/basic/rest/activitystreams";
	protected String requestToExecute;
	protected static final String REQUEST_ME_ALL_ALL = "/@me/@all/@all";
	protected static final String REQUEST_ME_ALL_STATUS = "/@me/@all/@status";
	protected static final String REQUEST_ME_ALL_COMMUNITIES = "/@me/@all/@communities";
	protected static final String REQUEST_ME_ALL_PEOPLE = "/@me/@all/@people";
	protected static final String REQUEST_ME_ALL_TAGS = "/@me/@all/@tags";
	protected static final String REQUEST_ME_ALL_ACTIVITIES = "/@me/@all/activities";
	protected static final String REQUEST_ME_ALL_BOOKMARKS = "/@me/@all/bookmarks";
	protected static final String REQUEST_ME_ALL_BLOGS = "/@me/@all/blogs";
	protected static final String REQUEST_ME_ALL_FILES = "/@me/@all/files";
	protected static final String REQUEST_ME_ALL_FORUMS = "/@me/@all/forums";
	protected static final String REQUEST_ME_ALL_WIKIS = "/@me/@all/wikis";
	protected static final String REQUEST_PUBLIC_ALL_ALL = "/@public/@all/@all";
	protected static final String REQUEST_PUBLIC_ALL_STATUS = "/@public/@all/@status";
	protected static final String REQUEST_PUBLIC_ALL_COMMUNITIES = "/@public/@all/@communities";
	protected static final String REQUEST_PUBLIC_ALL_PROFILES = "/@public/@all/profiles";
	protected static final String REQUEST_PUBLIC_ALL_ACTIVITIES = "/@public/@all/activities";
	protected static final String REQUEST_PUBLIC_ALL_BOOKMARKS = "/@public/@all/bookmarks";
	protected static final String REQUEST_PUBLIC_ALL_BLOGS = "/@public/@all/blogs";
	protected static final String REQUEST_PUBLIC_ALL_FILES = "/@public/@all/files";
	protected static final String REQUEST_PUBLIC_ALL_FORUMS = "/@public/@all/forums";
	protected static final String REQUEST_PUBLIC_ALL_WIKIS = "/@public/@all/wikis";
	protected static final String REQUEST_ME_FRIENDS_STATUS = "/@me/@friends/@status";
	protected static final String REQUEST_ME_SELF_STATUS = "/@me/@self/@status";
	
	protected static final String REQUEST_INVOLVED_ALL = "/@involved/@all";
	protected static final String REQUEST_INVOLVED_COMMUNITIES = "/@involved/@communities";
	protected static final String REQUEST_INVOLVED_PROFILES = "/@involved/profiles";
	protected static final String REQUEST_INVOLVED_ACTIVITIES = "/@involved/activities";
	protected static final String REQUEST_INVOLVED_BOOKMARKS = "/@involved/bookmarks";
	protected static final String REQUEST_INVOLVED_BLOGS = "/@involved/blogs";
	protected static final String REQUEST_INVOLVED_FILES = "/@involved/files";
	protected static final String REQUEST_INVOLVED_FORUMS = "/@involved/forums";
	protected static final String REQUEST_INVOLVED_WIKIS = "/@involved/wikis";
	
	
	@BeforeClass
	public static void setUp() throws Exception {
		
		
		
		

			userIDLong = FVTUtilsWithDate
					.getUserId(PopStringConstantsAS.USER_DISPLAY_NAME);
		

		String[] user1IdPart = userIDLong.split(":");
		userIDShort = user1IdPart[user1IdPart.length - 1];
		
        

		

	
	
	}
	
	@AfterClass
	public static void flushLog() {
		
	}

}
