package com.ibm.lconn.automation.framework.services.communities.impersonated;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import javax.xml.namespace.QName;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;
import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.services.blogs.BlogsService;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Component;
import com.ibm.lconn.automation.framework.services.common.StringConstants.DeploymentType;
import com.ibm.lconn.automation.framework.services.common.StringConstants.MemberType;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Notification;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Permissions;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Role;
import com.ibm.lconn.automation.framework.services.common.StringConstants.SharePermission;
import com.ibm.lconn.automation.framework.services.common.StringConstants.SortBy;
import com.ibm.lconn.automation.framework.services.common.StringConstants.SortField;
import com.ibm.lconn.automation.framework.services.common.StringConstants.SortOrder;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.common.UserPerspective;
import com.ibm.lconn.automation.framework.services.common.UsersEnvironment;
import com.ibm.lconn.automation.framework.services.common.Utils;
import com.ibm.lconn.automation.framework.services.common.nodes.Member;
import com.ibm.lconn.automation.framework.services.communities.CommunitiesService;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.communities.nodes.Widget;
import com.ibm.lconn.automation.framework.services.files.FilesService;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;

/**
 *  Connections API test
 * 
 * @author Ping - wangpin@us.ibm.com
 */
public class GDPRImpersonationTest {
	protected static Abdera abdera = new Abdera();
	
	//Users index in i1 ProfileData_apps.collabservintegration.properties
	static final int ORGADMIN = 0;
	static final int USER1 = 1;
	static final int AUDITOR = 2;
	
	private UserPerspective admin, user1, user2, auditor;    
	private BlogsService blogsService, AuditorService, blogsIMService;
	private CommunitiesService commService, commIMService;
	private FilesService filesService, filesIMService;
	protected final static Logger LOGGER = LoggerFactory.getLogger(GDPRImpersonationTest.class.getName());

	@BeforeClass
	public void setUp() throws Exception {

		LOGGER.debug("Start Initializing GDPRCommunityBlogs impersonate Test");

		UsersEnvironment userEnv = new UsersEnvironment();
		// Get User1 API test env
		user1 = userEnv.getLoginUserEnvironment(USER1,Component.COMMUNITIES.toString());
		commService = user1.getCommunitiesService();
		
		// Get User2 API test env, here user2 set as org-admin
		user2 = userEnv.getLoginUserEnvironment(ORGADMIN,Component.COMMUNITIES.toString());
		//comm2Service = user2.getCommunitiesService();
		
		// Get Org-admin act as User1 API test env
		userEnv.getImpersonateEnvironment(ORGADMIN, USER1, Component.COMMUNITIES.toString());
		admin = userEnv.getLoginUser();
		commIMService = admin.getCommunitiesService();
		
		
		
		user1 = userEnv.getLoginUserEnvironment(USER1,Component.BLOGS.toString());
		blogsService = user1.getBlogsService();
		
		auditor = userEnv.getLoginUserEnvironment(AUDITOR,Component.BLOGS.toString());
		AuditorService = auditor.getBlogsService();
		
		userEnv.getImpersonateEnvironment(ORGADMIN, USER1, Component.BLOGS.toString());
		admin = userEnv.getLoginUser();
		blogsIMService = admin.getBlogsService();
		
		
		user1 = userEnv.getLoginUserEnvironment(USER1,Component.FILES.toString());
		filesService = user1.getFilesService();

		userEnv.getImpersonateEnvironment(ORGADMIN, USER1, Component.FILES.toString());
		admin = userEnv.getLoginUser();
		filesIMService = admin.getFilesService();

		LOGGER.debug("Finished Initializing GDPR impersonate Test");
	}

	
	@Test
	public void blogsContentUpdate() throws Exception {
		/*
		 * Test process 
		 * 1. User1 create a community, add blogs widget and create a blogs (with SSN in blogs content)
		 * ---------------------------------------------------------------------------------------
		 * 2. Use impersonation APIs,  
		 *           GDPR admin act as User1 => add User2 as member to the community  (not used)
		 * 3. Auditor user search the SSN in blogs,  get list of blogs with SSN
		 * 4. For each blogs with SSN,  GDPR admin act as User1 => update the blogs ( remove SSN )
		 * 								GDPR admin act as User1 => get blogs and verify
		 */
		
		LOGGER.debug("BEGIN TEST: GDPR community blogs.");
		String SSN = "978-78-7800";
		String uniqueNameAddition = RandomStringUtils.randomAlphanumeric(4);
		String comName = "Comunity blogs test "+ uniqueNameAddition;

		LOGGER.debug("1.  User1 Create private community, add Blogs widget and create blogs ");
		Community newCommunity = new Community(
				comName, "Private community blogs test.", Permissions.PRIVATE, null);

		Entry communityResult = (Entry) commService.createCommunity(newCommunity);
		assertEquals("create comm faied", 201, commService.getRespStatus());
		assertTrue(communityResult != null);

		Community comm = new Community((Entry) commService.getCommunity(
				communityResult.getEditLinkResolvedHref().toString()));
				
		// Add blogs widget
		Feed widgetsInitialFeed = (Feed) commService.getCommunityWidgets(comm.getUuid());
		boolean blogFound = false;
		for (Entry e : widgetsInitialFeed.getEntries()) {
			if (e.getTitle().equals("Blog")) {
				blogFound = true;
				break;
			}
		}
		if (blogFound == false) {
			Widget widget = new Widget(StringConstants.WidgetID.Blog.toString());
			commService.postWidget(comm, widget.toEntry());
			assertEquals("add blogs widget failed", 201, commService.getRespStatus());
		}
		
		
		Feed remoteAppsFeed = (Feed) commService.getCommunityRemoteAPPs(
				comm.getRemoteAppsListHref(), true, null, 0, 50, null, null,
				SortBy.NAME, SortOrder.ASC, SortField.NAME, null);
		assertEquals("get commRemoteApps failed", 200, commService.getRespStatus());

		String blogUrl = null;
		for (Entry raEntry : remoteAppsFeed.getEntries()) {			
			if (raEntry.getTitle().equalsIgnoreCase("Blog")){
				for (Link link : raEntry.getLinks()) {
					if (link.getRel().contains("remote-application/publish")) {
						blogUrl = link.getHref().toString();
					}
				}
			}
		}

		String postBlogUrl = null;
		ExtensibleElement blogEE = null;
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			blogEE = commService.getAnyFeedWithRedirect(blogUrl);
		} else {
			blogEE = commService.getAnyFeed(blogUrl);
		}
		assertEquals("get blogs feed failed", 200, commService.getRespStatus());
		assertTrue(blogEE != null);
		for (Element ele : blogEE.getElements()) {
			if (ele.toString().startsWith("<workspace")) {
				for (Element ele2 : ele.getElements()) {
					if (ele2.toString().startsWith("<collection")) {
						for (QName atrb : ele2.getAttributes()) {
							if (atrb.toString().equalsIgnoreCase("href")
									&& ele2.getAttributeValue("href").contains("api/entries")) {
								postBlogUrl = ele2.getAttributeValue("href");
							}
						}
					}
				}
			}
		}

		Factory factory = abdera.getFactory();
		String blogTitle = "Blog Created in Communities";
		Entry blogEntry = factory.newEntry();
		blogEntry.setTitle(blogTitle);
		blogEntry.setContent("blog content with SSN "+SSN);

		blogEE = blogsService.postBlogsFeed(postBlogUrl, blogEntry);
		assertEquals("post commuinty blog failed", 201, blogsService.getRespStatus());
		
		
		LOGGER.debug("2. GDPR admin act as User1 add User2 as member to the communities,  not used");
		Member member = new Member(user2.getEmail(), user2.getUserId(),
				Component.COMMUNITIES, Role.MEMBER, MemberType.PERSON);

		commIMService.addMemberToCommunity(comm, member);
		assertEquals(" Add Community Member failed", 201, commIMService.getRespStatus());
		
		Feed membersFeed = (Feed) commService.getCommunityMembers(
				comm.getMembersListHref(), false, null, 1, 10, null, null, null, null);
		assertEquals("get comm member failed", 200, commService.getRespStatus());
		boolean foundMember = false;
		for (Entry entry : membersFeed.getEntries()) {
			if (entry.getTitle().equals(admin.getRealName()))
				foundMember = true;
		}
		assertTrue("member verify failed", foundMember);
		
		
		LOGGER.debug("3. Search SSN with Auditor User to get list of blogs with SSN ");
		String searchUrl = URLConstants.SERVER_URL+"/search/atom/mysearch?query="+SSN+"&scope=blogs";
		ExtensibleElement searchFeed = AuditorService.getBlogFeed(searchUrl);
		
		// skip if index is not ready
		int responseCode =  AuditorService.getRespStatus();
		if ( responseCode == 503) return;
		
		assertEquals("Search blogs failed", 200, AuditorService.getRespStatus());
		
		
		LOGGER.debug("4. For each blogs with SSN, update and verify with impersonation");
		for (Entry bEntry : ((Feed)searchFeed).getEntries()){
			for (Link link : bEntry.getLinks()) {
				if (link.getRel().contains("via")) {
					blogUrl = link.getHref().toString();
				}
			}
			
			blogEE = blogsIMService.getBlogFeed(blogUrl);
			assertEquals("Get blogs failed", 200, blogsIMService.getRespStatus());
			
			LOGGER.debug("Blogs content original :" +((Entry)blogEE).getContent());
			
			String blogEditUrl=((Entry)blogEE).getEditLinkResolvedHref().toString();
			
			//update blogs content
			Entry blogsEntry = (Entry)blogEE;
			String updated = blogsEntry.getContent().replaceAll(SSN, "");
			blogsEntry.setContent(updated);
						
			blogEE = blogsIMService.putBlogsFeed(blogEditUrl, blogsEntry);
			assertEquals("Update blogs content failed", 200, blogsIMService.getRespStatus());
			
			LOGGER.debug("Blogs content after update :" +((Entry)blogEE).getContent());
		}
	}
	
	@Test
	public void fileContentUpdate() throws FileNotFoundException, IOException, InterruptedException {
		/*
		 * Tests file content update with impersonation
		 * Step 1: Create file with password_string in content 
		 * ---------------------------------------------------------------
		 * Step 2: Auditor user search the password and get list of files with password
		 * Step 3: update file content ( remove password_string ) and verify
		 * 
		 */
		
		LOGGER.debug("BEGINNING TEST: File Content Update");
		String PASSWORD="password_string";
		String timeStamp = Utils.logDateFormatter.format(new Date());
		String filename = "File for GDPR" + timeStamp;
		String fileContent = "myfiles with "+PASSWORD;

		LOGGER.debug("Step 1: Create file");
		FileEntry fileMetaData = new FileEntry(null, filename,
				"This is one cool car!", "cool car lp640 private",
				Permissions.PRIVATE, true, Notification.ON, Notification.ON,
				null, null, true, true, SharePermission.VIEW,
				"Hello world, this is my new private share!", null, null,
				fileContent);
		filesService.createFile(fileMetaData);

		LOGGER.debug("Step 2. Search password with Auditor User to get list of files with password ");
		String searchUrl = URLConstants.SERVER_URL+"/search/atom/mysearch?query="+PASSWORD+"&scope=files";
		ExtensibleElement searchFeed = AuditorService.getBlogFeed(searchUrl);
		
		// skip if index is not ready
		int responseCode =  AuditorService.getRespStatus();
		if ( responseCode == 503) return;
		
		assertEquals("Search failed", 200, AuditorService.getRespStatus());
		
		
		LOGGER.debug("Step 3: update file content and verify");
		String fileUrl = null;
		for (Entry fEntry : ((Feed)searchFeed).getEntries()){
			for (Link link : fEntry.getLinks()) {
				if (link.getRel() != null && link.getRel().contains("via")) {
					fileUrl = link.getHref().toString();
				}
			}
			String fileMediaUrl = fileUrl.replace("entry", "media");
			String content = filesService.getResponseStringWithRedirect(fileMediaUrl);
			LOGGER.debug("File content before:" + content);
			
			String updatedContent = content.replaceAll(PASSWORD, "");
			InputStream in = new ByteArrayInputStream(updatedContent.getBytes());
		
			filesIMService.putResponse(fileUrl, in);
			
			Thread.sleep(1000);
			String updatedFileContent = filesService.getResponseStringWithRedirect(fileMediaUrl);
			LOGGER.debug("File content After update:" + updatedFileContent);
			
		}

		LOGGER.debug("COMPLETED TEST: File Content update");
	}


	@AfterClass
	public void tearDown() {
		commService.tearDown();
		commIMService.tearDown();
		blogsService.tearDown();
		blogsIMService.tearDown();
		AuditorService.tearDown();
		filesIMService.tearDown();
		filesService.tearDown();
	}

}