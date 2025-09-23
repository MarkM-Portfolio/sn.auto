package com.ibm.conn.auto.tests.homepage.fvt.testcases.mentions.files.communities;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2010, 2014                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

import java.io.File;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIFileHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.Mentions;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;

/**
 * @author Patrick Doherty
 */

public class FVT_Mentions_FileComment_Communities_DiffOrg extends SetUpMethods2{
	private static Logger log = LoggerFactory.getLogger(FVT_Mentions_FileComment_Communities_DiffOrg.class);

	private HomepageUI ui;
	private TestConfigCustom cfg;
	private APICommunitiesHandler apiOwner;
	private APIFileHandler apiFileOwner;
	private APIProfilesHandler profilesAPI;
	private User testUser1, testUser2, testUser3;
	private String serverURL;
	private String filePath;
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() {
	
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = HomepageUI.getGui(cfg.getProductName(),driver);
		
		testUser1 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA, this);
		testUser2 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgB, this);
		testUser3 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA, this);
		if(testUser1.getDisplayName().equalsIgnoreCase(testUser3.getDisplayName())){
			testUser3 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA, this);
		}
		
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		apiFileOwner = new APIFileHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		profilesAPI = new APIProfilesHandler(serverURL, testUser3.getAttribute(cfg.getLoginPreference()), testUser3.getPassword());

		filePath = cfg.getTestConfig().getBrowserEnvironment().getAbsoluteFilePath(cfg.getUploadFilesDir(), Data.getData().file1);
	}
		
	/**
	* fileComment_publicCommunity_diffOrg() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 log into Communities</B></li>
	*<li><B>Step: testUser1 go to a community with public access for which you have owner access</B></li>
	*<li><B>Step: testUser1 upload a file with public access to the community</B></li>
	*<li><B>Step: testUser1 add a comment with a mentions to this file</B></li>
	*<li><B>Step: testUser2 log into Homepage as a user in a different organisation</B></li>
	*<li><B>Step: testUser2 go to Homepage / Updates / Discover / All, Communities & Files</B></li>
	*<li><B>Verify: Verify that there is NOT a mentions event in the views</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/8E2464F6F670D4EA85257C70003B4833">TTT - DISCOVER - FILES - 00122 - FILE COMMENT WITH A MENTIONS - PUBLIC COMMUNITY - DIFFERENT ORGANISATION</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtcloud"})
	public void fileComment_publicCommunity_diffOrg() {
				
		String testName = ui.startTest();
		
		BaseCommunity communityPub = new BaseCommunity.Builder(testName + Helper.genStrongRand())
														.access(Access.PUBLIC)
														.tags(Data.getData().commonTag + Helper.genStrongRand())
														.description(Data.getData().commonDescription + Helper.genStrongRand())
														.build();

		BaseFile baseFile = new BaseFile.Builder(Data.getData().file1)
										.extension(".jpg")
										.rename(Helper.genStrongRand())
										.build();
				
		log.info("INFO: Create a community using API method");	
		Community community = communityPub.createAPI(apiOwner);
		
		log.info("INFO: Create a file object");
		File file = new File(filePath);

		log.info("INFO: Create a file for the community using API method");
		FileEntry fileEntry = apiFileOwner.CreateFile(baseFile, file, community);

		log.info("INFO: Add a comment with a mentions to the community file");
		String beforeMentionsText = Data.getData().buttonCancel + Helper.genStrongRand();
		String afterMentionsText = Data.getData().buttonDownload + Helper.genStrongRand();
		
		Mentions mentions = new Mentions.Builder(testUser3, profilesAPI.getUUID())
										.browserURL(serverURL)
										.beforeMentionText(beforeMentionsText)
										.afterMentionText(afterMentionsText)
										.build();
		apiFileOwner.addMentionFileCommentAPI(fileEntry, community, mentions);
			
		/*
		 * Login testUser2 who will verify that the @mentions to testUser3
		 * does NOT appear in the "Discover" view
		 */
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(testUser2);
		ui.gotoDiscover();
		ui.clickIfVisible(HomepageUIConstants.ShowMore);

		//Create the news story
		String newsStory = ui.replaceNewsStory(Data.FILE_COMMENTED, null, null, testUser1.getDisplayName());
		String mentionsComment = beforeMentionsText + " @" + testUser3.getDisplayName() + " " + afterMentionsText;

		log.info("INFO: Verify the news story is NOT displayed");
		Assert.assertTrue(driver.isTextNotPresent(newsStory),
							"ERROR: News story is displayed");
		
		log.info("INFO: Verify the @mentions is NOT displayed");
		Assert.assertTrue(driver.isTextNotPresent(mentionsComment),
							"ERROR: @mentions is displayed");
		
		log.info("INFO: Filtering the Activity Stream by 'Files'");
		ui.filterBy(HomepageUIConstants.FilterFiles);
		
		log.info("INFO: Wait for the Activity Stream to load");
		ui.fluentWaitTextPresent(Data.getData().feedsForTheseEntries);
		ui.clickIfVisible(HomepageUIConstants.ShowMore);
		
		log.info("INFO: Verify the news story is NOT displayed");
		Assert.assertTrue(driver.isTextNotPresent(newsStory),
							"ERROR: News story is displayed");
		
		log.info("INFO: Verify the @mentions is NOT displayed");
		Assert.assertTrue(driver.isTextNotPresent(mentionsComment),
							"ERROR: @mentions is displayed");

		log.info("INFO: Filtering the Activity Stream by 'Communities'");
		ui.filterBy(HomepageUIConstants.FilterCommunities);
		
		log.info("INFO: Wait for the Activity Stream to load");
		ui.fluentWaitTextPresent(Data.getData().feedsForTheseEntries);
		ui.clickIfVisible(HomepageUIConstants.ShowMore);
		
		log.info("INFO: Verify the news story is NOT displayed");
		Assert.assertTrue(driver.isTextNotPresent(newsStory),
							"ERROR: News story is displayed");
		
		log.info("INFO: Verify the @mentions is NOT displayed");
		Assert.assertTrue(driver.isTextNotPresent(mentionsComment),
							"ERROR: @mentions is displayed");
		
		log.info("INFO: Perform clean up now that the test has completed");
		apiOwner.deleteCommunity(community);
		ui.endTest();
	}

	/**
	* fileComment_modCommunity_diffOrg() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 log into Communities</B></li>
	*<li><B>Step: testUser1 go to a community with moderated access for which you have owner access</B></li>
	*<li><B>Step: testUser1 upload a file with public access to the community</B></li>
	*<li><B>Step: testUser1 add a comment with a mentions to this file</B></li>
	*<li><B>Step: testUser2 log into Homepage as a user in a different organisation</B></li>
	*<li><B>Step: testUser2 go to Homepage / Updates / Discover / All, Communities & Files</B></li>
	*<li><B>Verify: Verify that there is NOT a mentions event in the views</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/987486251A84D8B585257C70003B4834">TTT - DISCOVER - FILES - 00123 - FILE COMMENT WITH A MENTIONS - MODERATED COMMUNITY - DIFFERENT ORGANISATION</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtcloud"})
	public void fileComment_modCommunity_diffOrg() {
				
		String testName = ui.startTest();
		
		BaseCommunity communityMod = new BaseCommunity.Builder(testName + Helper.genStrongRand())
														.access(Access.MODERATED)
														.tags(Data.getData().commonTag + Helper.genStrongRand())
														.description(Data.getData().commonDescription + Helper.genStrongRand())
														.build();
		
		BaseFile baseFile = new BaseFile.Builder(Data.getData().file1)
										.extension(".jpg")
										.rename(Helper.genStrongRand())
										.build();

		log.info("INFO: Create a community using API method");
		Community community = communityMod.createAPI(apiOwner);
		
		log.info("INFO: Create a file object");
		File file = new File(filePath);

		log.info("INFO: Create a file for the community using API method");
		FileEntry fileEntry = apiFileOwner.CreateFile(baseFile, file, community);

		log.info("INFO: Add a comment with a mentions to the community file");
		String beforeMentionsText = Data.getData().buttonOK + Helper.genStrongRand();
		String afterMentionsText = Data.getData().buttonPost + Helper.genStrongRand();
		
		Mentions mentions = new Mentions.Builder(testUser3, profilesAPI.getUUID())
										.browserURL(serverURL)
										.beforeMentionText(beforeMentionsText)
										.afterMentionText(afterMentionsText)
										.build();
		apiFileOwner.addMentionFileCommentAPI(fileEntry, community, mentions);
		
		/*
		* Login testUser2 who will verify that the @mentions to testUser3
		* does NOT appear in the "Discover" view
		*/
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(testUser2);
		ui.gotoDiscover();
		ui.clickIfVisible(HomepageUIConstants.ShowMore);
		
		//Create the news story
		String newsStory = ui.replaceNewsStory(Data.FILE_COMMENTED, null, null, testUser1.getDisplayName());
		String mentionsComment = beforeMentionsText + " @" + testUser3.getDisplayName() + " " + afterMentionsText;
		
		log.info("INFO: Verify the news story is NOT displayed");
		Assert.assertTrue(driver.isTextNotPresent(newsStory),
							"ERROR: News story is displayed");
		
		log.info("INFO: Verify the @mentions is NOT displayed");
		Assert.assertTrue(driver.isTextNotPresent(mentionsComment),
							"ERROR: @mentions is displayed");
		
		log.info("INFO: Filtering the Activity Stream by 'Files'");
		ui.filterBy(HomepageUIConstants.FilterFiles);
		
		log.info("INFO: Wait for the Activity Stream to load");
		ui.fluentWaitTextPresent(Data.getData().feedsForTheseEntries);
		ui.clickIfVisible(HomepageUIConstants.ShowMore);
		
		log.info("INFO: Verify the news story is NOT displayed");
		Assert.assertTrue(driver.isTextNotPresent(newsStory),
							"ERROR: News story is displayed");
		
		log.info("INFO: Verify the @mentions is NOT displayed");
		Assert.assertTrue(driver.isTextNotPresent(mentionsComment),
							"ERROR: @mentions is displayed");

		log.info("INFO: Filtering the Activity Stream by 'Communities'");
		ui.filterBy(HomepageUIConstants.FilterCommunities);
		
		log.info("INFO: Wait for the Activity Stream to load");
		ui.fluentWaitTextPresent(Data.getData().feedsForTheseEntries);
		ui.clickIfVisible(HomepageUIConstants.ShowMore);
		
		log.info("INFO: Verify the news story is NOT displayed");
		Assert.assertTrue(driver.isTextNotPresent(newsStory),
							"ERROR: News story is displayed");
		
		log.info("INFO: Verify the @mentions is NOT displayed");
		Assert.assertTrue(driver.isTextNotPresent(mentionsComment),
							"ERROR: @mentions is displayed");
		
		log.info("INFO: Perform clean up now that the test has completed");
		apiOwner.deleteCommunity(community);
		ui.endTest();
	}

	/**
	* fileComment_privateCommunity_diffOrg() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 log into Communities</B></li>
	*<li><B>Step: testUser1 go to a community with private access for which you have owner access</B></li>
	*<li><B>Step: testUser1 upload a file with public access to the community</B></li>
	*<li><B>Step: testUser1 add a comment with a mentions to this file</B></li>
	*<li><B>Step: testUser2 log into Homepage as a user in a different organisation</B></li>
	*<li><B>Step: testUser2 go to Homepage / Updates / Discover / All, Communities & Files</B></li>
	*<li><B>Verify: Verify that there is NOT a mentions event in the views</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/69AE111E184C840985257C70003B4835">TTT - DISCOVER - FILES - 00124 - FILE COMMENT WITH A MENTIONS - PRIVATE COMMUNITY - DIFFERENT ORGANISATION</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtcloud"})
	public void fileComment_privateCommunity_diffOrg() {
				
		String testName = ui.startTest();
		
		BaseCommunity communityPriv = new BaseCommunity.Builder(testName + Helper.genStrongRand())
														.access(Access.RESTRICTED)
														.tags(Data.getData().commonTag + Helper.genStrongRand())
														.description(Data.getData().commonDescription + Helper.genStrongRand())
														.shareOutside(false)
														.build();
		
		BaseFile baseFile = new BaseFile.Builder(Data.getData().file1)
										.extension(".jpg")
										.rename(Helper.genStrongRand())
										.build();

		log.info("INFO: Create a community using API method");
		Community community = communityPriv.createAPI(apiOwner);
		
		log.info("INFO: Create a file object");
		File file = new File(filePath);

		log.info("INFO: Create a file for the community using API method");
		FileEntry fileEntry = apiFileOwner.CreateFile(baseFile, file, community);

		log.info("INFO: Add a comment with a mentions to the community file");
		String beforeMentionsText = Data.getData().buttonRemove + Helper.genStrongRand();
		String afterMentionsText = Data.getData().buttonSave + Helper.genStrongRand();
		
		Mentions mentions = new Mentions.Builder(testUser3, profilesAPI.getUUID())
										.browserURL(serverURL)
										.beforeMentionText(beforeMentionsText)
										.afterMentionText(afterMentionsText)
										.build();
		apiFileOwner.addMentionFileCommentAPI(fileEntry, community, mentions);
		
		/*
		* Login testUser2 who will verify that the @mentions to testUser3
		* does NOT appear in the "Discover" view
		*/
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(testUser2);
		ui.gotoDiscover();
		ui.clickIfVisible(HomepageUIConstants.ShowMore);
		
		//Create the news story
		String newsStory = ui.replaceNewsStory(Data.FILE_COMMENTED, null, null, testUser1.getDisplayName());
		String mentionsComment = beforeMentionsText + " @" + testUser3.getDisplayName() + " " + afterMentionsText;
		
		log.info("INFO: Verify the news story is NOT displayed");
		Assert.assertTrue(driver.isTextNotPresent(newsStory),
							"ERROR: News story is displayed");
		
		log.info("INFO: Verify the @mentions is NOT displayed");
		Assert.assertTrue(driver.isTextNotPresent(mentionsComment),
							"ERROR: @mentions is displayed");
		
		log.info("INFO: Filtering the Activity Stream by 'Files'");
		ui.filterBy(HomepageUIConstants.FilterFiles);
		
		log.info("INFO: Wait for the Activity Stream to load");
		ui.fluentWaitTextPresent(Data.getData().feedsForTheseEntries);
		ui.clickIfVisible(HomepageUIConstants.ShowMore);
		
		log.info("INFO: Verify the news story is NOT displayed");
		Assert.assertTrue(driver.isTextNotPresent(newsStory),
							"ERROR: News story is displayed");
		
		log.info("INFO: Verify the @mentions is NOT displayed");
		Assert.assertTrue(driver.isTextNotPresent(mentionsComment),
							"ERROR: @mentions is displayed");

		log.info("INFO: Filtering the Activity Stream by 'Communities'");
		ui.filterBy(HomepageUIConstants.FilterCommunities);
		
		log.info("INFO: Wait for the Activity Stream to load");
		ui.fluentWaitTextPresent(Data.getData().feedsForTheseEntries);
		ui.clickIfVisible(HomepageUIConstants.ShowMore);
		
		log.info("INFO: Verify the news story is NOT displayed");
		Assert.assertTrue(driver.isTextNotPresent(newsStory),
							"ERROR: News story is displayed");
		
		log.info("INFO: Verify the @mentions is NOT displayed");
		Assert.assertTrue(driver.isTextNotPresent(mentionsComment),
							"ERROR: @mentions is displayed");
		
		log.info("INFO: Perform clean up now that the test has completed");
		apiOwner.deleteCommunity(community);
		ui.endTest();
	}
}
