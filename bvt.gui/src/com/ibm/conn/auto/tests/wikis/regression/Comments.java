package com.ibm.conn.auto.tests.wikis.regression;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseWiki;
import com.ibm.conn.auto.appobjects.base.BaseWiki.EditAccess;
import com.ibm.conn.auto.appobjects.base.BaseWiki.ReadAccess;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.WikiRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIWikisHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.menu.Wiki_LeftNav_Menu;
import com.ibm.conn.auto.webui.WikisUI;

public class Comments extends SetUpMethods2{
	
	private static Logger log = LoggerFactory.getLogger(Comments.class);
	private WikisUI ui;
	private TestConfigCustom cfg;	
	private List<Member> members;
	private User testUser1, testUser2, testUser3, testUser4;
	private APIWikisHandler apiOwner;
	private String serverURL;
	/*
	 * This test case is to verify that comments service functions as expected
	 * Created By: Conor Pelly
	 * Date: 20/10/2010
	 */	
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass(){
		
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		
		//Load Users
		testUser1 = cfg.getUserAllocator().getUser();
		testUser2 = cfg.getUserAllocator().getUser();
		testUser3 = cfg.getUserAllocator().getUser();
		testUser4 = cfg.getUserAllocator().getUser();		
		
		//initialize API
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());

	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUp(){
		cfg = TestConfigCustom.getInstance();
		ui = WikisUI.getGui(cfg.getProductName(), driver);
		
		apiOwner = new APIWikisHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
				
		//create member list
		members = new ArrayList<Member>();	
		members.add(new Member(WikiRole.OWNER, testUser2, apiOwner.getUserUUID(serverURL, testUser2)));
		members.add(new Member(WikiRole.EDITOR, testUser3, apiOwner.getUserUUID(serverURL, testUser3)));
		members.add(new Member(WikiRole.READER, testUser4, apiOwner.getUserUUID(serverURL, testUser4)));

	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Tests the addition of comment using the 'Add a comment' link under the Comments tab when login as the owner of the wiki.</li>
	*<li><B>Step: </B>Create a wiki using API.</li>
	*<li><B>Step: </B>Load component and Login as the owner </li>
	*<li><B>Step: </B>Select the ' I'm an Owner ' view from the left navigation menu.</li>
	*<li><B>Step: </B>Open the wiki.</li>
	*<li><B>Step: </B>Click on the 'Add a comment' link under Comments tab.</li>
	*<li><B>Step: </B>Enter a comment in the text area for comments and click on the Save button.</li>
	*<li><B>Verify: </B>The message: 'The comment was added.' appears.</li>
	*<li><B>Verify: </B>The comment appears on the wiki's Welcome page.</li>
	*</ul>
	*/
	@Test(groups = {"regression"})
	public void addWikiCommentOwner()throws Exception{		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest();
		
		String comment = "Add Comment for user " + testUser2.getDisplayName() + 
						  " and the time and date stamp is: " + Helper.genDateBasedRand();

		BaseWiki wiki = new BaseWiki.Builder(testName + Helper.genDateBasedRand())
									.editAccess(EditAccess.EditorsAndOwners)
									.readAccess(ReadAccess.WikiOnly)
									.addMembers(members)
									.tags("tag" + Helper.genDateBasedRand())
									.description("Description for test " + testName)
									.build();
				
		logger.strongStep("Create wiki: " + wiki.getName() + " using API");
		log.info("INFO: Create wiki: " + wiki.getName() + " using API");
		wiki.createAPI(apiOwner);
			
		//Load Wikis component and login
		logger.strongStep("Open browser and login to Wikis as: " + testUser2.getDisplayName());
		log.info("INFO: Open browser and login to Wikis as owner");
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.login(testUser2);

		//view the wiki
		logger.strongStep("Select the ' I'm an Owner ' view from the left navigation menu");
		log.info("INFO: Select the 'I'm an Owner' view from the left navigation menu");
		Wiki_LeftNav_Menu.OWNER.select(ui);
		
		//Open Wiki created earlier
		logger.strongStep("Open Wiki created via API");
		log.info("INFO: Open Wiki created via API");
		ui.clickLinkWait(WikisUI.getWiki(wiki));
				
		//Add the comments to the page
		logger.strongStep("Click on the 'Add a comment' link under Comments tab, then enter the comment: " + comment + " in the text area and click on the Save button");
		log.info("INFO: Add the comment: " + comment + " to the page");
		ui.addComment(comment);

		//Verify 'The comment was added.' text message
		logger.strongStep("Verify the message: 'The comment was added.' appears");
		log.info("INFO: Verify the message: 'The comment was added.' appears");
		Assert.assertTrue(ui.fluentWaitTextPresent("The comment was added"),
						  "ERROR: 'The comment was added.' text is not present");

		//Verify comment shows up on the page
		logger.strongStep("Verify the comment appears on the wiki's Welcome page");
		log.info("INFO: Verify the comment appears on the wiki's Welcome page");
		Assert.assertTrue(ui.fluentWaitTextPresent(comment),
		  			     "ERROR:Comment was not added correctly");	
		
		ui.endTest();	
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Tests the addition of comment using the 'Add a comment' link under the Comments tab when login as the editor of the wiki.</li>
	*<li><B>Step: </B>Create a wiki using API.</li>
	*<li><B>Step: </B>Load component and Login as the editor </li>
	*<li><B>Step: </B>Select the ' I'm an Editor ' view from the left navigation menu.</li>
	*<li><B>Step: </B>Open the wiki.</li>
	*<li><B>Step: </B>Click on the 'Add a comment' link under Comments tab.</li>
	*<li><B>Step: </B>Enter a comment in the text area for comments and click on the Save button.</li>
	*<li><B>Verify: </B>The message: 'The comment was added.' appears.</li>
	*<li><B>Verify: </B>The comment appears on the wiki's Welcome page.</li>
	*</ul>
	*/
	@Test(groups = {"regression"})
	public void addWikiCommentEditor()throws Exception{		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest();
		
		String comment = "Add Comment for user " + testUser3.getDisplayName() + 
						  " and the time and date stamp is: " + Helper.genDateBasedRand();

		BaseWiki wiki = new BaseWiki.Builder(testName + Helper.genDateBasedRand())
									.editAccess(EditAccess.EditorsAndOwners)
									.readAccess(ReadAccess.WikiOnly)
									.addMembers(members)
									.tags("tag" + Helper.genDateBasedRand())
									.description("Description for test " + testName)
									.build();
		
		logger.strongStep("Create wiki: " + wiki.getName() + " using API");
		log.info("INFO: Create wiki: " + wiki.getName() + " using API");
		wiki.createAPI(apiOwner);
			
		//Load Wikis component and login
		logger.strongStep("Open browser and login to Wikis as: " + testUser3.getDisplayName());
		log.info("INFO: Open browser and login to Wikis as editor");
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.login(testUser3);

		//view the wiki
		logger.strongStep("Select the ' I'm an Editor ' view from the left navigation menu");
		log.info("INFO: Select the ' I'm an Editor ' view from the left navigation menu");
		Wiki_LeftNav_Menu.EDITOR.select(ui);
			
		//Open Wiki created earlier
		logger.strongStep("Open Wiki created via API");
		log.info("INFO: Open Wiki created via API");
		ui.clickLinkWait(WikisUI.getWiki(wiki));
				
		//Add the comments to the page
		logger.strongStep("Click on the 'Add a comment' link under Comments tab, then enter the comment: " + comment + " in the text area and click on the Save button");
		log.info("INFO: Add the comment: " + comment + " to the page");
		ui.addComment(comment);
		
		//Verify 'The comment was added' text message
		logger.strongStep("Verify the message: 'The comment was added.' appears");
		log.info("INFO: Verify the message: 'The comment was added.' appears");
		Assert.assertTrue(ui.fluentWaitTextPresent("The comment was added"),
						  "ERROR: 'The comment was added' text is not present");

		//Verify comment shows up on the page
		logger.strongStep("Verify the comment appears on the wiki's Welcome page");
		log.info("INFO: Verify the comment appears on the wiki's Welcome page");
		Assert.assertTrue(ui.fluentWaitTextPresent(comment),
		  			     "ERROR:Comment was not added correctly");	
		
		ui.endTest();	
	}	
	
	/**
	*<ul>
	*<li><B>Info: </B>Tests the addition of comment using the 'Add a comment' link under the Comments tab when login as the reader of the wiki.</li>
	*<li><B>Step: </B>Create a wiki using API.</li>
	*<li><B>Step: </B>Load component and Login as the reader </li>
	*<li><B>Step: </B>Select the ' I'm an Reader ' view from the left navigation menu.</li>
	*<li><B>Step: </B>Open the wiki.</li>
	*<li><B>Step: </B>Click on the 'Add a comment' link under Comments tab.</li>
	*<li><B>Step: </B>Enter a comment in the text area for comments and click on the Save button.</li>
	*<li><B>Verify: </B>The message: 'The comment was added.' appears.</li>
	*<li><B>Verify: </B>The comment appears on the wiki's Welcome page.</li>
	*</ul>
	*/
	@Test(groups = {"regression"})
	public void addWikiCommentReader()throws Exception{		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest();
		
		String comment = "Add Comment for user " + testUser4.getDisplayName() + 
						  " and the time and date stamp is: " + Helper.genDateBasedRand();

		BaseWiki wiki = new BaseWiki.Builder(testName + Helper.genDateBasedRand())
									.editAccess(EditAccess.EditorsAndOwners)
									.readAccess(ReadAccess.WikiOnly)
									.addMembers(members)
									.tags("tag" + Helper.genDateBasedRand())
									.description("Description for test " + testName)
									.build();

		logger.strongStep("Create wiki: " + wiki.getName() + " using API");
		log.info("INFO: Create wiki: " + wiki.getName() + " using API");
		wiki.createAPI(apiOwner);
			
		//Load Wikis component and login
		logger.strongStep("Open browser and login to Wikis as: " + testUser4.getDisplayName());
		log.info("INFO: Open browser and login to Wikis as reader");
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.login(testUser4);

		//view the wiki
		logger.strongStep("Select the 'I'm a Reader' view from the left navigation menu");
		log.info("INFO: Select the 'I'm a Reader' view from the left navigation menu");
		Wiki_LeftNav_Menu.READER.select(ui);
		
		//Open Wiki created earlier
		logger.strongStep("Open Wiki created via API");
		log.info("INFO: Open Wiki created via API");
		ui.clickLinkWait(WikisUI.getWiki(wiki));
				
		//Add the comments to the page
		logger.strongStep("Click on the 'Add a comment' link under Comments tab, then enter the comment: " + comment + " in the text area and click on the Save button");
		log.info("INFO: Add the comment: " + comment + " to the page");
		ui.addComment(comment);
		
		//Verify 'The comment was added' text message
		logger.strongStep("Verify the message: 'The comment was added.' appears");
		log.info("INFO: Verify the message: 'The comment was added.' appears");
		Assert.assertTrue(ui.fluentWaitTextPresent("The comment was added"),
						  "ERROR: 'The comment was added' text is not present");

		//Verify comment shows up on the page
		logger.strongStep("Verify the comment appears on the wiki's Welcome page");
		log.info("INFO: Verify the comment appears on the wiki's Welcome page");
		Assert.assertTrue(ui.fluentWaitTextPresent(comment),
		  			     "ERROR:Comment was not added correctly");	
		
		ui.endTest();	
	}	
	
	/**
	*<ul>
	*<li><B>Info: </B>Tests the addition of comment using the 'Add a comment' link under the Comments tab when login as different user of the wiki.</li>
	*<li><B>Step: </B>Create a wiki using API.</li>
	*<li><B>Step: </B>Load component and Login as the owner </li>
	*<li><B>Step: </B>Select the ' I'm an Owner ' view from the left navigation menu.</li>
	*<li><B>Step: </B>Open the wiki.</li>
	*<li><B>Step: </B>Add comment to the page as an owner</li>
	*<li><B>Verify: </B>The message: 'The comment was added.' appears.</li>
	*<li><B>Step: </B>Logout and Login as a editor</li>
	*<li><B>Step: </B>Select the ' I'm an Editor ' view from the left navigation menu.</li>
	*<li><B>Step: </B>Open the wiki.</li>
	*<li><B>Step: </B>Add comment to the page as an editor</li>
	*<li><B>Verify: </B>The message: 'The comment was added.' appears.</li>
	*<li><B>Step: </B>Logout and Login as a reader</li>
	*<li><B>Step: </B>Select the ' I'm an Reader ' view from the left navigation menu.</li>
	*<li><B>Step: </B>Open the wiki.</li>
	*<li><B>Step: </B>Add comment to the page as an reader</li>
	*<li><B>Verify: </B>The message: 'The comment was added.' appears.</li>
	*</ul>
	*/
	public void editComment()throws Exception{		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest();
		
		String comment1 = "Comment1 for user " + testUser2.getDisplayName() + 
						  " and the time and date stamp is: " + Helper.genDateBasedRand();

		String comment2 = "Comment2 for user " + testUser2.getDisplayName() + 
						  " and the time and date stamp is: " + Helper.genDateBasedRand();
		
		String newComment = "Edited comment with out a time stamp";
		
		BaseWiki wiki = new BaseWiki.Builder(testName + Helper.genDateBasedRand())
									.editAccess(EditAccess.EditorsAndOwners)
									.readAccess(ReadAccess.WikiOnly)
									.addMembers(members)
									.tags("tag" + Helper.genDateBasedRand())
									.description("Description for test " + testName)
									.build();

		logger.strongStep("Create wiki: " + wiki.getName() + " using API");
		log.info("INFO: Create wiki: " + wiki.getName() + " using API");
		wiki.createAPI(apiOwner);
			
		//Load Wikis component and login
		logger.strongStep("Open browser and login to Wikis as: " + testUser2.getDisplayName());
		log.info("INFO: Open browser and login to Wikis as owner");
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.login(testUser2);

		//Open Wiki created earlier
		logger.strongStep("Open Wiki created via API");
		log.info("INFO: Open Wiki created via API");
		ui.clickLinkWait(WikisUI.getWiki(wiki));
				
		//Add a comment to the page
		logger.strongStep("Add a comment to the page");
		log.info("INFO: Add a comment to the page");
		ui.addComment(comment1);

		//Add second comment to the page
		logger.strongStep("Add second comment to the page");
		log.info("INFO: Add second comment to the page");
		ui.addComment(comment2);
		
		//Edit the second comment
		logger.strongStep("Edit the second comment");
		log.info("INFO: Edit the second comment");
		ui.editComment(comment2, newComment);
		
		//Verify that the expected message appears
		logger.strongStep("Verify the message: 'Edited today at' appears");
		log.info("INFO: Verify the message: 'Edited today at' appears");
		Assert.assertTrue(ui.fluentWaitTextPresent("Edited today at"),
						  "ERROR: Did not find 'Edited today at' message");
		
		//Verify that the edited text appears
		logger.strongStep("Verify the edited text appears");
		log.info("INFO: Verify the edited text appears");
		Assert.assertTrue(ui.fluentWaitTextPresent(newComment),
						  "ERROR: Edited text does not appear");
		
		//Verify the old text disappears
		logger.strongStep("Verify the old text no longer appears");
		log.info("INFO: Verify the old text no longer appears");
		Assert.assertTrue(ui.fluentWaitTextNotPresent(comment2),
		  				  "ERROR: Old text appears");
		
		ui.endTest();	
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Tests the comment is deleted from the Comments tab.</li>
	*<li><B>Step: </B>Create a wiki using API.</li>
	*<li><B>Step: </B>Load component and Login as the owner </li>
	*<li><B>Step: </B>Open the wiki.</li>
	*<li><B>Step: </B>Add a comment to the page.</li>
	*<li><B>Step: </B>Add second comment to the page.</li>
	*<li><B>Step: </B>Delete the first comment.</li>
	*<li><B>Verify: </B>The comment was deleted from the comment tab.</li>
	*</ul>
	*/
	@Test (groups = {"regression"})
	public void AddDeleteComment() throws Exception{		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		
		String comment1 = "Delete Comment1 for user " + testUser2.getDisplayName() + 
	       				  " and the time and date stamp is: " + Helper.genDateBasedRand();

		String comment2 = "Delete Comment2 for user " + testUser2.getDisplayName() + 
			  			  " and the time and date stamp is: " + Helper.genDateBasedRand();

		
		BaseWiki wiki = new BaseWiki.Builder(testName + Helper.genDateBasedRand())
									.editAccess(EditAccess.EditorsAndOwners)
									.readAccess(ReadAccess.WikiOnly)
									.addMembers(members)
									.tags("tag" + Helper.genDateBasedRand())
									.description("Description for test " + testName)
									.build();
		
		logger.strongStep("Create wiki: " + wiki.getName() + " using API");
		log.info("INFO: Create wiki: " + wiki.getName() + " using API");
		wiki.createAPI(apiOwner);
			
		//Load Wikis component and login
		logger.strongStep("Open browser and login to Wikis as: " + testUser2.getDisplayName());
		log.info("INFO: Open browser and login to Wikis as owner");
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.login(testUser2);

		//Open Wiki created earlier
		logger.strongStep("Open Wiki created via API");
		log.info("INFO: Open Wiki created via API");
		ui.clickLinkWait(WikisUI.getWiki(wiki));
		
		//Add first comment to the page
		logger.strongStep("Add first comment to the page");
		log.info("INFO: Add first comment to the page");
		ui.addComment(comment1);
		
		//Add second comment to the page
		logger.strongStep("Add second comment to the page");
		log.info("INFO: Add second comment to the page");
		ui.addComment(comment2);
		
		//Delete the first comment
		logger.strongStep("Delete the first comment");
		log.info("INFO: Delete the first comment");
		ui.deleteComment(comment1);

		//Verify the comment was removed
		logger.strongStep("Verify the comment was deleted");
		log.info("INFO: Verify the comment was deleted");
		Assert.assertTrue(ui.fluentWaitTextNotPresent(comment1),
						  "ERROR: Comment was not removed");
		
		ui.endTest();			
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Tests the addition of multiple comments using the 'Add a comment' link under the Comments tab in the wiki.</li>
	*<li><B>Step: </B>Create a wiki using API.</li>
	*<li><B>Step: </B>Load component and Login as the owner </li>
	*<li><B>Step: </B>Open the wiki.</li>
	*<li><B>Step: </B>Add 12 comments to the page.</li>
	*<li><B>Step: </B>Select to show 25 comments.</li>
	*<li><B>Verify: </B>The comment count is correct on the comment tab.</li>
	*</ul>
	*/
	@Test (groups = {"regression"})
	public void AddMultipleCommentsToWiki()throws Exception{		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		String comment = "Here is Comment number ";
		
		BaseWiki wiki = new BaseWiki.Builder(testName + Helper.genDateBasedRand())
									.editAccess(EditAccess.EditorsAndOwners)
									.readAccess(ReadAccess.WikiOnly)
									.addMembers(members)
									.tags("tag" + Helper.genDateBasedRand())
									.description("Description for test " + testName)
									.build();
		
		logger.strongStep("Create wiki: " + wiki.getName() + " using API");
		log.info("INFO: Create wiki: " + wiki.getName() + " using API");
		wiki.createAPI(apiOwner);
			
		//Load Wikis component and login
		logger.strongStep("Open browser and login to Wikis as: " + testUser1.getDisplayName());
		log.info("INFO: Open browser and login to Wikis as owner");
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.login(testUser1);

		//Open Wiki created earlier
		logger.strongStep("Open Wiki created via API");
		log.info("INFO: Open Wiki created via API");
		ui.clickLinkWait(WikisUI.getWiki(wiki));
		
		//Add 12 comments to the page
		for (int i=1; i<13; i++){
			
			//Add the comments to the page
			logger.strongStep("Add comment number " + i + " to the page");
			log.info("INFO: Add comment number " + i + " to the page");
			ui.addComment(comment + i);
			
		}

		//If show 25 comments is not active select to show 25
		if(driver.getSingleElement("css=a[id='s25L']").isVisible()){
			logger.strongStep("Select to show 25 comments");
			log.info("INFO: Select to show 25 comments");
			ui.clickLinkWait("css=a[id='s25L']");
		}
		
		//Validate the comments count
		logger.strongStep("Verify the comments count is correct");
		log.info("INFO: Verify the comments count is correct");
		Assert.assertTrue(driver.getSingleElement("css=div[dojoattachpoint='pageInfoNode']").getText().contentEquals("1-12 of 12"),
						 "ERROR: comment count does not equal what was expected");

		ui.endTest();	
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Tests the comments are added and edited when login as the different user of the wiki.</li>
	*<li><B>Step: </B>Create a wiki using API.</li>
	*<li><B>Step: </B>Load component and Login as the owner </li>
	*<li><B>Step: </B>Open the wiki.</li>
	*<li><B>Step: </B>Add a comments to the page.</li>
	*<li><B>Verify: </B>The comment appears on comment tab.</li>
	*<li><B>Step: </B>Edit the comment just added to the page.</li>
	*<li><B>Verify: </B>The old comment is disappears from the comment tab.</li>
	*<li><B>Step: </B>Logout and Login as an owner.</li>
	*<li><B>Step: </B>Select the 'I'm an Owner' view from the left navigation menu.</li>
	*<li><B>Step: </B>Open the wiki.</li>
	*<li><B>Step: </B>Add a comment to the page.</li>
	*<li><B>Verify: </B>The comment is present.</li>
	*<li><B>Step: </B>Edit the 2nd comment.</li>
	*<li><B>Verify: </B>The old comment is not present.</li>
	*<li><B>Step: </B>Logout and Login as editor.</li>
	*<li><B>Step: </B>Select the 'I'm an Editor' view from the left navigation menu.</li>
	*<li><B>Step: </B>Open the wiki.</li>
	*<li><B>Step: </B>Add a comment to the page.</li>
	*<li><B>Verify: </B>The comment is present.</li>
	*<li><B>Step: </B>Edit the comment just added.</li>
	*<li><B>Verify: </B>Verify the old comment is not present.</li>
	*<li><B>Step: </B>Logout and Login as reader.</li>
	*<li><B>Step: </B>Select the 'I'm an Reader' view from the left navigation menu.</li>
	*<li><B>Step: </B>Open the wiki.</li>
	*<li><B>Step: </B>Add a comment to the page.</li>
	*<li><B>Verify: </B>The comment is present.</li>
	*<li><B>Step: </B>Edit the comment just added.</li>
	*<li><B>Verify: </B>Verify the old comment is not present.</li>
	*<li><B>Verify: </B>Verify the comment count is correct.</li>
	*</ul>
	*/
	@Test (groups = {"regression"})
	public void AddAndEditCommentsMultipleUsers()throws Exception{		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest();
		String initialComment = "Test Comment for Multiple Users";
		String newComment = "New comment for Multiple Users";
		
		BaseWiki wiki = new BaseWiki.Builder(testName + Helper.genDateBasedRand())
									.editAccess(EditAccess.EditorsAndOwners)
									.readAccess(ReadAccess.WikiOnly)
									.addMembers(members)
									.tags("tag" + Helper.genDateBasedRand())
									.description("Description for test " + testName)
									.build();
		
		logger.strongStep("Create wiki: " + wiki.getName() + " using API");
		log.info("INFO: Create wiki: " + wiki.getName() + " using API");
		wiki.createAPI(apiOwner);
			
		//Login again as the creator
		logger.strongStep("Open browser and login to Wikis as: " + testUser1.getDisplayName());
		log.info("INFO: Open browser and login to Wikis as owner");
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.login(testUser1);

		//Open Wiki created earlier
		logger.strongStep("Open Wiki created via API");
		log.info("INFO: Open Wiki created via API");
		ui.clickLinkWait(WikisUI.getWiki(wiki));
		
		//Add the comment to the page
		logger.strongStep("Add a comment to the page");
		log.info("INFO: Add a comment to the page");
		ui.addComment(initialComment);
		
		logger.strongStep("Verify the comment is present");
		log.info("INFO: Verify the comment is present");
		Assert.assertTrue(ui.fluentWaitTextPresent(initialComment), "ERROR: Unable to find comment text");
		
		//Edit the comment just created
		logger.strongStep("Edit the comment");
		log.info("INFO: edit the comment");
		ui.editComment(initialComment, newComment);

		//Validate the old comment is not present
		logger.strongStep("Verify the old comment is not present");
		log.info("INFO: Verify the old comment is not present");
		Assert.assertTrue(ui.fluentWaitTextNotPresent(initialComment),"ERROR: Found original comment text");
				
		//Logout of Wiki
		logger.strongStep("Logout of Wiki");
		log.info("INFO: Logout of Wiki");
		ui.logout();
		
		//Login again as an Owner
		logger.strongStep("Open browser and login to Wikis as: " + testUser2.getDisplayName());
		log.info("INFO: Open browser and login to Wikis as owner");
		ui.loadComponent(Data.getData().ComponentWikis, true);
		ui.login(testUser2);
		
		//view the wiki
		logger.strongStep("Select the 'I'm an Owner' view from the left navigation menu");
		log.info("INFO: Select the 'I'm an Owner' view from the left navigation menu");
		Wiki_LeftNav_Menu.OWNER.select(ui);
		
		//Open Wiki created earlier
		logger.strongStep("Open Wiki created via API");
		log.info("INFO: Open Wiki created via API");
		ui.clickLinkWait(WikisUI.getWiki(wiki));
		
		//Add a comment to the page
		logger.strongStep("Add a comment to the page");
		log.info("INFO: Add a comment to the page");
		ui.addComment(initialComment);
		
		logger.strongStep("Verify the comment is present");
		log.info("INFO: Verify the comment is present");
		Assert.assertTrue(ui.fluentWaitTextPresent(initialComment),
						  "ERROR: Unable to find comment text");
		
		//Edit the comment just created
		logger.strongStep("Edit the second comment");
		log.info("INFO: edit the second comment");
		ui.editComment(initialComment, newComment);

		//Validate the old comment is not present
		logger.strongStep("Verify the old comment is not present");
		log.info("INFO: Verify the old comment is not present");
		Assert.assertTrue(ui.fluentWaitTextNotPresent(initialComment),
		  				  "ERROR: Found original comment text");
		
		//Logout of Wiki
		logger.strongStep("Logout of Wiki");
		log.info("INFO: Logout of Wiki");
		ui.logout();
		
		//Login again as a Editor
		logger.strongStep("Open browser and login to Wikis as: " + testUser3.getDisplayName());
		log.info("INFO: Open browser and login to Wikis as editor");
		ui.loadComponent(Data.getData().ComponentWikis, true);
		ui.login(testUser3);
		
		//view the wiki
		logger.strongStep("Select the 'I'm an Editor' view from the left navigation menu");
		log.info("INFO: Select the 'I'm an Editor' view from the left navigation menu");
		Wiki_LeftNav_Menu.EDITOR.select(ui);
		
		//Open Wiki created earlier
		logger.strongStep("Open Wiki created via API");
		log.info("INFO: Open Wiki created via API");
		ui.clickLinkWait(WikisUI.getWiki(wiki));
		
		//Add the comment to the page
		logger.strongStep("Add a comment to the page");
		log.info("INFO: Add a comment to the page");
		ui.addComment(initialComment);
		
		logger.strongStep("Verify the comment is present");
		log.info("INFO: Verify the comment is present");
		Assert.assertTrue(ui.fluentWaitTextPresent(initialComment),
						  "ERROR: Unable to find comment text");
		
		//Edit the comment just created
		logger.strongStep("Edit the comment just added");
		log.info("INFO: edit the comment just added");
		ui.editComment(initialComment, newComment);

		//Validate the old comment is not present
		logger.strongStep("Verify the old comment is not present");
		log.info("INFO: Verify the old comment is not present");
		Assert.assertTrue(ui.fluentWaitTextNotPresent(initialComment),
		  				  "ERROR: Found original comment text");
		
		//Logout of Wiki
		logger.strongStep("Logout of Wiki");
		log.info("INFO: Logout of Wiki");
		ui.logout();
		
		//Login again as a Reader
		logger.strongStep("Open browser and login to Wikis as: " + testUser4.getDisplayName());
		log.info("INFO: Open browser and login to Wikis as reader");
		ui.loadComponent(Data.getData().ComponentWikis, true);
		ui.login(testUser4);
		
		//View the wiki
		logger.strongStep("Select the 'I'm an Reader' view from the left navigation menu");
		log.info("INFO: Select the 'I'm an Reader' view from the left navigation menu");
		Wiki_LeftNav_Menu.READER.select(ui);
		
		//Open Wiki created earlier
		logger.strongStep("Open Wiki created via API");
		log.info("INFO: Open Wiki created via API");
		ui.clickLinkWait(WikisUI.getWiki(wiki));
		
		//Add the comments to the page
		logger.strongStep("Add a comment to the page");
		log.info("INFO: Add a comment to the page");
		ui.addComment(initialComment);
		
		logger.strongStep("Verify the comment is present");
		log.info("INFO: Verify the comment is present");
		Assert.assertTrue(ui.fluentWaitTextPresent(initialComment),
						  "ERROR: Unable to find comment text");
		
		//Edit the comment just created
		logger.strongStep("Edit the second comment");
		log.info("INFO: edit the second comment");
		ui.editComment(initialComment, newComment);

		//Validate the old comment is not present
		logger.strongStep("Verify the old comment is not present");
		log.info("INFO: Verify the old comment is not present");
		Assert.assertTrue(ui.fluentWaitTextNotPresent(initialComment),
		  				  "ERROR: Found original comment text");
		
		//Validate comment count
		logger.strongStep("Verify the comment count is correct");
		log.info("INFO: Verify the comment count is correct");
		Assert.assertTrue(driver.getSingleElement("css=div[dojoattachpoint='pageInfoNode']").getText().contentEquals("1-4 of 4"),
						 "ERROR: comment count does not equal what was expected");
		
		ui.endTest();	
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Tests the comments are added and deleted when login as the different user of the wiki.</li>
	*<li><B>Step: </B>Create a wiki using API.</li>
	*<li><B>Step: </B>Load component and Login as the owner </li>
	*<li><B>Step: </B>Open the wiki.</li>
	*<li><B>Step: </B>Add a comment to the page.</li>
	*<li><B>Step: </B>Delete the comment from the page.</li>
	*<li><B>Step: </B>Logout and Login as an owner.</li>
	*<li><B>Step: </B>Select the 'I'm an Owner' view from the left navigation menu.</li>
	*<li><B>Step: </B>Open the wiki.</li>
	*<li><B>Step: </B>Add a comment to the page.</li>
	*<li><B>Step: </B>Delete the comment from the page.</li>
	*<li><B>Step: </B>Logout and Login as editor.</li>
	*<li><B>Step: </B>Select the 'I'm an Editor' view from the left navigation menu.</li>
	*<li><B>Step: </B>Open the wiki.</li>
	*<li><B>Step: </B>Add a comment to the page.</li>
	*<li><B>Step: </B>Delete the comment just added.</li>
	*<li><B>Step: </B>Logout and Login as reader.</li>
	*<li><B>Step: </B>Select the 'I'm an Reader' view from the left navigation menu.</li>
	*<li><B>Step: </B>Open the wiki.</li>
	*<li><B>Step: </B>Add a comment to the page.</li>
	*<li><B>Step: </B>Delete the comment just added.</li>
	*<li><B>Verify: </B>Verify the comment count is correct.</li>
	*</ul>
	*/
	@Test (groups = {"regression"})
	public void AddDeleteCommentsMultipleUsers()throws Exception{		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		
		String comment1 = "Delete Comment1 for user " + testUser1.getDisplayName() + 
		  				  " and the time and date stamp is: " + Helper.genDateBasedRand();
		
		String comment2 = "Delete Comment1 for user " + testUser2.getDisplayName() + 
		  				  " and the time and date stamp is: " + Helper.genDateBasedRand();
		
		String comment3 = "Delete Comment1 for user " + testUser3.getDisplayName() + 
		  				  " and the time and date stamp is: " + Helper.genDateBasedRand();
		
		String comment4 = "Delete Comment1 for user " + testUser4.getDisplayName() + 
		  				  " and the time and date stamp is: " + Helper.genDateBasedRand();
		
		BaseWiki wiki = new BaseWiki.Builder(testName + Helper.genDateBasedRand())
									.editAccess(EditAccess.EditorsAndOwners)
									.readAccess(ReadAccess.WikiOnly)
									.addMembers(members)
									.tags("tag" + Helper.genDateBasedRand())
									.description("Description for test " + testName)
									.build();
		
		logger.strongStep("Create wiki: " + wiki.getName() + " using API");
		log.info("INFO: Create wiki: " + wiki.getName() + " using API");
		wiki.createAPI(apiOwner);
			
		//Login again as the Creator
		logger.strongStep("Open browser and login to Wikis as: " + testUser1.getDisplayName());
		log.info("INFO: Open browser and login to Wikis as owner");
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.login(testUser1);

		//Open Wiki created earlier
		logger.strongStep("Open Wiki created via API");
		log.info("INFO: Open Wiki created via API");
		ui.clickLinkWait(WikisUI.getWiki(wiki));
		
		//Add the comments to the page
		logger.strongStep("Add a comment to the page");
		log.info("INFO: Add a comment to the page");
		ui.addComment(comment1);
		
		//Delete the comment just created
		logger.strongStep("Delete the comment just created from the page");
		log.info("INFO: Delete the comment just created from the page");
		ui.deleteComment(comment1);
		
		//Logout of Wiki
		logger.strongStep("Logout of Wiki");
		log.info("INFO: Logout of Wiki");
		ui.logout();
		
		//Login again as an owner
		logger.strongStep("Open browser and login to Wikis as: " + testUser2.getDisplayName());
		log.info("INFO: Open browser and login to Wikis as owner");
		ui.loadComponent(Data.getData().ComponentWikis, true);
		ui.login(testUser2);
		
		//View the wiki
		logger.strongStep("Select the 'I'm a Owner' view from the left navigation menu");
		log.info("INFO: Select the 'I'm a Owner' view from the left navigation menu");
		Wiki_LeftNav_Menu.OWNER.select(ui);
		
		//Open Wiki created earlier
		logger.strongStep("Open Wiki created via API");
		log.info("INFO: Open Wiki created via API");
		ui.clickLinkWait(WikisUI.getWiki(wiki));
		
		//Add the comment to the page
		logger.strongStep("Add a comment to the page");
		log.info("INFO: Add a comment to the page");
		ui.addComment(comment2);
		
		//Delete the comment just created
		logger.strongStep("Delete the comment just created from the page");
		log.info("INFO: Delete the comment just created from the page");
		ui.deleteComment(comment2);
		
		//Logout of Wiki
		logger.strongStep("Logout of Wiki");
		log.info("INFO: Logout of Wiki");
		ui.logout();
		
		//Login again as an Editor
		logger.strongStep("Open browser and login to Wikis as: " + testUser3.getDisplayName());
		log.info("INFO: Open browser and login to Wikis as editor");
		ui.loadComponent(Data.getData().ComponentWikis, true);
		ui.login(testUser3);
		
		//View the wiki
		logger.strongStep("Select the 'I'm a Editor' view from the left navigation menu");
		log.info("INFO: Select the 'I'm a Editor' view from the left navigation menu");
		Wiki_LeftNav_Menu.EDITOR.select(ui);
		
		//Open Wiki created earlier
		logger.strongStep("Open Wiki created via API");
		log.info("INFO: Open Wiki created via API");
		ui.clickLinkWait(WikisUI.getWiki(wiki));
		
		//Add the comments to the page
		logger.strongStep("Add a comment to the page");
		log.info("INFO: Add a comment to the page");
		ui.addComment(comment3);
		
		//Delete the comment just created
		logger.strongStep("Delete the comment just created from the page");
		log.info("INFO: Delete the comment just created from the page");
		ui.deleteComment(comment3);

		//Logout of Wiki
		logger.strongStep("Logout of Wiki");
		log.info("INFO: Logout of Wiki");
		ui.logout();
		
		//Login again as a Reader
		logger.strongStep("Open browser and login to Wikis as: " + testUser4.getDisplayName());
		log.info("INFO: Open browser and login to Wikis as reader");
		ui.loadComponent(Data.getData().ComponentWikis, true);
		ui.login(testUser4);
	
		//View the wiki
		logger.strongStep("Select the 'I'm a Reader' view from the left navigation menu");
		log.info("INFO: Select the 'I'm a Reader' view from the left navigation menu");
		Wiki_LeftNav_Menu.READER.select(ui);
		
		//Open Wiki created earlier
		logger.strongStep("Open Wiki created via API");
		log.info("INFO: Open Wiki created via API");
		ui.clickLinkWait(WikisUI.getWiki(wiki));
		
		//Add the comment to the page
		logger.strongStep("Add a comment to the page");
		log.info("INFO: Add a comment to the page");
		ui.addComment(comment4);
		
		//Delete the comment just created
		logger.strongStep("Delete the comment just created from the page");
		log.info("INFO: Delete the comment just created from the page");
		ui.deleteComment(comment4);		
		
		//Validate comment count
		logger.strongStep("Verify the comment count is correct");
		log.info("INFO: Verify the comment count is correct");
		Assert.assertTrue(ui.fluentWaitTextPresent("There are no comments."),
						 "ERROR: There are comments still present");
		
		ui.endTest();	
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Tests the addition of long comment using the 'Add a comment' link under the Comments tab when login as the owner of the wiki.</li>
	*<li><B>Step: </B>Create a wiki using API.</li>
	*<li><B>Step: </B>Load component and Login as the owner </li>
	*<li><B>Step: </B>Open the wiki.</li>
	*<li><B>Step: </B>Add comment to the page as an owner.</li>
	**<li><B>Verify: </B>The message: 'The comment was added.' appears.</li>
	*<li><B>Step: </B>Add second comment to the page as an owner.</li>
	**<li><B>Verify: </B>The message: 'The comment was added.' appears.</li>
	*</ul>
	*/
	@Test (groups = {"regression"})
	public void AddLongComment()throws Exception{		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		
		BaseWiki wiki = new BaseWiki.Builder(testName + Helper.genDateBasedRand())
									.editAccess(EditAccess.EditorsAndOwners)
									.readAccess(ReadAccess.All)
									.addMembers(members)
									.tags("tag" + Helper.genDateBasedRand())
									.description("Description for test " + testName)
									.build();
		
		logger.strongStep("Create wiki: " + wiki.getName() + " using API");
		log.info("INFO: Create wiki: " + wiki.getName() + " using API");
		wiki.createAPI(apiOwner);
			
		//Load Wikis component and login
		logger.strongStep("Open browser and login to Wikis as: " + testUser1.getDisplayName());
		log.info("INFO: Open browser and login to Wikis as owner");
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.login(testUser1);

		//Open Wiki created earlier
		logger.strongStep("Open Wiki created via API");
		log.info("INFO: Open Wiki created via API");
		ui.clickLinkWait(WikisUI.getWiki(wiki));		
		
		//Add a long comment to the page
		logger.strongStep("Add a long comment to the page as an owner");
		log.info("INFO: Add comment to the page as an owner");
		ui.addComment("Test Comment for user " + testUser1.getDisplayName() + 
					" and the time and date stamp is: " + Helper.genDateBasedRand() + 
					" and then the rest of this comment " + Data.getData().LongComment + 
					" and maybe a bit more " + Data.getData().LongComment + "");
		
		//Verify 'The comment was added.' text message
		logger.strongStep("Verify the message: 'The comment was added.' appears");
		log.info("INFO: Verify the message: 'The comment was added.' appears");
		Assert.assertTrue(ui.fluentWaitTextPresent("The comment was added"),
						  "ERROR: 'The comment was added.' text is not present");
		
		//Add second very long comment to the page
		logger.strongStep("Add second very long comment to the page as an owner");
		log.info("INFO: Add second very long comment to the page as an owner");
		ui.addComment("Test Comment for user " + testUser1.getDisplayName() + 
					   " and the time and date stamp is: " + Helper.genDateBasedRand() + 
					   " and then the rest of this comment " + Data.getData().ReallyLongComment + 
					   " and maybe a bit more " + Data.getData().ReallyLongComment+"");
		
		//Verify 'The comment was added.' text message
		logger.strongStep("Verify the message: 'The comment was added.' appears");
		log.info("INFO: Verify the message: 'The comment was added.' appears");
		Assert.assertTrue(ui.fluentWaitTextPresent("The comment was added"),
						  "ERROR: 'The comment was added.' text is not present");
		
		ui.endTest();	
	}
	
}
