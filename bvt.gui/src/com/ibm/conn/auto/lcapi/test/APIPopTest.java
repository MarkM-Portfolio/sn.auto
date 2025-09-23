package com.ibm.conn.auto.lcapi.test;

import org.apache.abdera.Abdera;
import org.apache.abdera.protocol.client.AbderaClient;
import org.junit.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.conn.auto.appobjects.base.BaseActivity;
import com.ibm.conn.auto.appobjects.base.BaseBlog;
import com.ibm.conn.auto.appobjects.base.BaseBlog.Theme;
import com.ibm.conn.auto.appobjects.base.BaseBlog.Time_Zone;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseDogear;
import com.ibm.conn.auto.lcapi.APIActivitiesHandler;
import com.ibm.conn.auto.lcapi.APIBlogsHandler;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIDogearHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.lcapi.common.Profile;
import com.ibm.conn.auto.util.Helper;
import com.ibm.lconn.automation.framework.services.activities.nodes.Activity;
import com.ibm.lconn.automation.framework.services.blogs.nodes.Blog;
import com.ibm.lconn.automation.framework.services.common.ServiceConfig;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.common.Utils;
import com.ibm.lconn.automation.framework.services.common.nodes.Bookmark;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.profiles.admin.ProfilesAdminService;

public class APIPopTest {
	
	private String testURL = "https://lc45linux2.swg.usma.ibm.com";
	private String testURLSC = "https://apps.collabservdaily.swg.usma.ibm.com";
	private String userNameSC = "conorpelly03@bluebox.lotus.com";	
	private String passWordSC = "Pa88w0rd";
	private String userName = "ajones333";
	private String passWord = "jones333";
	
	private static Abdera abdera;
	private static AbderaClient client;
	private static ServiceConfig config;
	private static ProfilesAdminService service;
	
	
	@BeforeClass
	public static void setUp() throws Exception {
		
		// Initialize Abdera
		abdera = new Abdera();
		client = new AbderaClient(abdera);
		
		// Register SSL / Add credentials for user
		AbderaClient.registerTrustManager();
		
		// Get service config for server, assert that it was retrieved and contains the activities service information
		config = new ServiceConfig(client, URLConstants.SERVER_URL, true);
		
		ServiceEntry profiles = config.getService("profiles");
		assert(profiles != null);

		Utils.addServiceAdminCredentials(profiles, client);
		
		// Retrieve the profiles service document and assert that it exists
		service = new ProfilesAdminService(client, profiles);
		assert(service.isFoundService());
		
	}
	
	
	
	@Test(groups={"apitest"})
	public void profilesAPITagTest(){
		
		APIProfilesHandler apiHandler = new APIProfilesHandler(testURL, userName, passWord);
		
		Profile result = apiHandler.setProfileTags("profiletag " + Helper.genDateBasedRand());
		
		assert result != null : "Add tags to profile failed";
	}
	
	
	@Test(groups={"apitest"})
	public void profilesAPITagTestCloud(){
		
		APIProfilesHandler apiHandler = new APIProfilesHandler(testURLSC, userNameSC, passWordSC);
		
		Profile result = apiHandler.setProfileTags("profiletag " + Helper.genDateBasedRand());
		
		assert result != null : "Add tags to profile failed";
	}
	
	@Test(groups={"apitest"})
	public void activitiesAPIPopTest(){
		
		String testName = new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName();
		BaseActivity activity = new BaseActivity.Builder(testName + Helper.genDateBasedRand())
												.tags("tag" + Helper.genDateBasedRand())
												.goal("Description" + testName)
												.build();
		
		
		APIActivitiesHandler apiHandler = new APIActivitiesHandler("cloud", testURL, userName, passWord);
		
		Activity result = apiHandler.createActivity(activity);
		
		assert result!=null : "Create activity failed";
	}
	
	
	@Test(groups={"apitest"})
	public void communitiesAPITagTest(){
		
		String testName = new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
													.tags("Tag" + Helper.genDateBasedRandVal())
													.access(Access.PUBLIC)
													.description("Test description for testcase " + testName)
													.build();
		
		APICommunitiesHandler apiHandler = new APICommunitiesHandler(testURL, userName, passWord);

		//RESTRICTED
		Community result = apiHandler.createCommunity(community);
		
		assert result!=null : "Create community failed";
		
		
	}
	
	@Test(groups={"apitest"})
	public void dogearAPITagTest(){
		
		String testName = new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName();
		String url = "http://www.google.com";
		
		BaseDogear bookmark = new BaseDogear.Builder(testName , url)
													.tags("Tag" + Helper.genDateBasedRand())
													.description("Description" + testName)
													.build();
		
		APIDogearHandler apiHandler = new APIDogearHandler(testURL, userName, passWord);
		
		Bookmark result = apiHandler.createBookmark(bookmark);
		
		assert result!=null : "Create bookmark failed";
	}
	
	@Test(groups={"apitest"})
	public void blogsAPITagTest(){
		
		String testName = new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName();
		String randval = Helper.genDateBasedRandVal();
		
		BaseBlog blog = new BaseBlog.Builder(testName + randval, "Test1Address" + randval)
									.tags("Tag for "+testName  + randval)
									.description("Test description for testcase " + testName)
									.timeZone(Time_Zone.Europe_London)
									.theme(Theme.Blog_with_Bookmarks)
									.build();
		
		APIBlogsHandler apiHandler = new APIBlogsHandler(testURL, userName, passWord);
		
		Blog result = apiHandler.createBlog(blog);
		
		assert result!=null : "Create blog failed";
	}
}
