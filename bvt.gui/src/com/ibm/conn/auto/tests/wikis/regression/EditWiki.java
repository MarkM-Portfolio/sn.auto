package com.ibm.conn.auto.tests.wikis.regression;

import java.util.ArrayList;
import java.util.List;

import com.ibm.conn.auto.webui.constants.WikisUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseWiki;
import com.ibm.conn.auto.appobjects.base.BaseWiki.EditAccess;
import com.ibm.conn.auto.appobjects.base.BaseWiki.ReadAccess;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.WikiRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.WikisUI;

public class EditWiki extends SetUpMethods2{
	
	private static Logger log = LoggerFactory.getLogger(EditWiki.class);
	private WikisUI ui;
	private TestConfigCustom cfg;	
	private List<Member> members;
	private User testUser1, testUser2, testUser3, testUser4;
	
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

	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUp(){
		
		cfg = TestConfigCustom.getInstance();
		ui = WikisUI.getGui(cfg.getProductName(), driver);
		
		//create member list
		members = new ArrayList<Member>();	
		members.add(new Member(WikiRole.OWNER, testUser2));
		members.add(new Member(WikiRole.EDITOR, testUser3));
		members.add(new Member(WikiRole.READER, testUser4));

	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Tests the Start a Wiki form that appears when a user clicks on the Start a Wiki button.
	*<li><B>Step: </B>Open Wikis component and login.
	*<li><B>Step: </B>Hit the Start a Wiki button and add all parts of the wiki but do not create it.
	*<li><B>Step: </B>Enter wiki's name in the Name field.
	*<li><B>Step: </B>Enter wiki's tags in the Tags field.
	*<li><B>Step: </B>Enter wiki's description in the Description field.
	*<li><B>Step: </B>Click on the Cancel button to nix the wiki creation.
	*<li><B>Step: </B>Click on the OK button in the Confirm dialog box.
	*<li><B>Verify: </B>The wiki is not created and does not appear on My Wikis page.
	*</ul> 
	*/
	@Test (groups = {"regression"})
	public void VerifyPublicWikiForm()throws Exception{

		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		
		BaseWiki wiki = new BaseWiki.Builder(testName + Helper.genDateBasedRand())
									.editAccess(EditAccess.EditorsAndOwners)
									.readAccess(ReadAccess.All)
									.addMembers(members)
									.tags("tag" + Helper.genDateBasedRand())
									.description("Description for test " + testName)
									.build();
		
		//Load component and login
		logger.strongStep("Open Wikis component and login as: " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.login(testUser1);

		logger.strongStep("Hit the Start a Wiki button and add all parts of the wiki but do not create it");
		log.info("INFO: Click on Start a Wiki button and add all parts of the wiki but do not create it");
		ui.clickLinkWait(WikisUIConstants.Start_New_Wiki_Button);

		//enter name of wiki
		logger.strongStep("Input '" + wiki.getName() + "' in the Name field");
		log.info("INFO: Entering wiki's name as: " + wiki.getName());
		driver.getSingleElement(WikisUIConstants.Wiki_form_title).type(wiki.getName());
				
		//enter tags for wiki
		logger.strongStep("Input '" + wiki.getTags() + "' in the Tags field");
		log.info("INFO: Entering wiki's tags as: " + wiki.getTags());
		driver.getSingleElement(WikisUIConstants.Wiki_form_tag).type(wiki.getTags());
	
		//enter description for wiki
		logger.strongStep("Input '" + wiki.getDescription() + "' in the Description field");
		log.info("INFO: Entering wiki's description as: " + wiki.getDescription());
		ui.typeText(WikisUIConstants.Wiki_description, wiki.getDescription());

		//Click Cancel button
		logger.strongStep("Click on the Cancel button to nix the wiki creation");
		log.info("INFO: Cancel creating the wiki");
		ui.clickCancelButton();

		//select ok button
		logger.strongStep("Click on the OK button in the Confirm dialog box");
		log.info("INFO: Select OK button in the Confirm dialog box");
		ui.clickButton("OK");
		
		//Validate wiki is not created
		logger.strongStep("Verify the wiki is not created and does not appear on My Wikis page");
		log.info("INFO: Validate that the wiki does not appear on My Wikis page because it is not created");
		Assert.assertTrue(ui.fluentWaitTextNotPresent(wiki.getName()),
						"ERROR: Wiki name is visible");
		
		ui.endTest();
		
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Tests that a public wiki's access can be changed after it has been created.
	*<li><B>Step: </B>Open Wikis component and login.
	*<li><B>Step: </B>Click on the Start a Wiki button and create a public wiki.
	*<li><B>Step: </B>Click on the Members link in the left navigation menu.
	*<li><B>Step: </B>Click on Manage Access button and change the Read access to 'Wiki members only'.
	*<li><B>Verify: </B>The message 'The access settings were changed.' appears.
	*<li><B>Step: </B>Click on the Members link in the left navigation menu.
	*<li><B>Step: </B>Click on the Manage Access button.
	*<li><B>Verify: </B>The option 'Wiki members only' is selected under Read Access.
	*</ul> 
	*/
	@Test (groups = {"regression"})
	public void CreateAPublicWikiAndEditWiki() throws Exception {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		
		BaseWiki wiki = new BaseWiki.Builder(testName + Helper.genDateBasedRand())
									.editAccess(EditAccess.EditorsAndOwners)
									.readAccess(ReadAccess.All)
									.tags("tag" + Helper.genDateBasedRand())
									.description("Description for test " + testName)
									.build();
		
		//Load component and login
		logger.strongStep("Open Wikis component and login as: " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.login(testUser1);
		
		//Create a public wiki 
		logger.strongStep("Click on the Start a Wiki button and create a public wiki");
		log.info("INFO: Create a public wiki");
		wiki.create(ui);
		
		//change wiki access to private
		logger.strongStep("Click on the Members link in the left navigation menu, then click on Manage Access button and change the Read access to 'Wiki members only'");
		log.info("INFO: Select the Members link from the left navigation menu, then click on Manage Access button and change the Read access to 'Wiki members only'");
		wiki.setReadAccess(ReadAccess.WikiOnly);
		wiki.changeAccess(ui, wiki);

		//ensure wiki was edited
		logger.strongStep("Verify the message 'The access settings were changed.' appears");
		log.info("INFO: Validate that the message 'The access settings were changed.' appears");
		ui.fluentWaitTextPresent("The access settings were changed.");

		logger.strongStep("Click on the Members link in the left navigation menu");
		log.info("INFO: Select the Members link from the left navigation menu");
		ui.clickLinkWait(WikisUIConstants.Members_Link);
    	
		logger.strongStep("Click on the Manage Access button");
		log.info("INFO: Select the Manage Access button");
		ui.clickLinkWait(WikisUIConstants.wikiManageAccess);
		
		//Validate that read access has changed to Wiki members only
		logger.strongStep("Verify the option 'Wiki members only' is selected under Read Access");
		log.info("INFO: Validate that read access has changed to 'Wiki members only'");
		List<Element> readAccess = driver.getVisibleElements(WikisUIConstants.wikiEditAccessEditDisable);
		Assert.assertTrue(readAccess.get(0).getAttribute("aria-checked").contentEquals("true"),
						  "ERROR: Read access not set to 'Wiki members only'");

		ui.endTest();
		
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Tests that the access for a public wiki whose name contains special characters can be changed after it has been created.
	*<li><B>Step: </B>Open Wikis component and login.
	*<li><B>Step: </B>Click on the Start a Wiki button and create a public wiki with a name that contains special characters.
	*<li><B>Step: </B>Click on the Members link in the left navigation menu.
	*<li><B>Step: </B>Click on Manage Access button and change the Read access to 'Wiki members only'.
	*<li><B>Verify: </B>The message 'The access settings were changed.' appears.
	*<li><B>Step: </B>Click on the Members link in the left navigation menu.
	*<li><B>Step: </B>Click on the Manage Access button.
	*<li><B>Verify: </B>The option 'Wiki members only' is selected under Read Access.
	*</ul> 
	*/
	@Test (groups = {"regression"})
	public void CreatePublicWikiAndEditWithSpecialCharactors()throws Exception{

		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		
		BaseWiki wiki = new BaseWiki.Builder(Data.getData().specialCharacterForWiki + Helper.genDateBasedRand())
									.editAccess(EditAccess.EditorsAndOwners)
									.readAccess(ReadAccess.All)
									.addMembers(members)
									.tags("tag" + Helper.genDateBasedRand())
									.description("Description for test " + testName)
									.build();
		
		//Load component and login
		logger.strongStep("Open Wikis component and login as: " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.login(testUser1);
		
		//Create a public wiki 
		logger.strongStep("Click on the Start a Wiki button and create a public wiki with a name that contains special characters");
		log.info("INFO: Create a public wiki with a name containing special characters");
		wiki.create(ui);
		
		//change wiki access to private
		logger.strongStep("Click on the Members link in the left navigation menu, then click on Manage Access button and change the Read access to 'Wiki members only'");
		log.info("INFO: Select the Members link from the left navigation menu, then click on Manage Access button and change the Read access to 'Wiki members only'");
		wiki.setReadAccess(ReadAccess.WikiOnly);
		wiki.changeAccess(ui, wiki);

		//ensure wiki was edited
		logger.strongStep("Verify the message 'The access settings were changed.' appears");
		log.info("INFO: Validate that the message 'The access settings were changed.' appears");
		ui.fluentWaitTextPresent("The access settings were changed.");

		logger.strongStep("Click on the Members link in the left navigation menu");
		log.info("INFO: Select the Members link from the left navigation menu");
		ui.clickLinkWait(WikisUIConstants.Members_Link);
    	
		logger.strongStep("Click on the Manage Access button");
		log.info("INFO: Select the Manage Access button");
		ui.clickLinkWait(WikisUIConstants.wikiManageAccess);
		
		//Validate that read access has changed to Wiki members only
		logger.strongStep("Verify the option 'Wiki members only' is selected under Read Access");
		log.info("INFO: Validate that read access has changed to 'Wiki members only'");
		List<Element> readAccess = driver.getVisibleElements(WikisUIConstants.wikiEditAccessEditDisable);
		Assert.assertTrue(readAccess.get(0).getAttribute("aria-checked").contentEquals("true"),
						  "ERROR: Read access not set to 'Wiki members only'");
		
		ui.endTest();
		
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Tests that the access for a private wiki whose name contains special characters can be changed after it has been created.
	*<li><B>Step: </B>Open Wikis component and login.
	*<li><B>Step: </B>Click on the Start a Wiki button and create a private wiki with a name that contains special characters.
	*<li><B>Step: </B>Click on the Members link in the left navigation menu.
	*<li><B>Step: </B>Click on Manage Access button and change the Read access to 'Wiki members only'.
	*<li><B>Verify: </B>The message 'The access settings were changed.' appears.
	*<li><B>Step: </B>Click on the Members link in the left navigation menu.
	*<li><B>Step: </B>Click on the Manage Access button.
	*<li><B>Verify: </B>The option 'Wiki members only' is selected under Read Access.
	*</ul> 
	*/
	@Test (groups = {"regression"})
	public void CreatePrivateWikiAndEditWithSpecialCharactors()throws Exception{

		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		
		BaseWiki wiki = new BaseWiki.Builder(Data.getData().specialCharacterForWiki + Helper.genDateBasedRand())
									.editAccess(EditAccess.EditorsAndOwners)
									.readAccess(ReadAccess.WikiOnly)
									.addMembers(members)
									.tags("tag" + Helper.genDateBasedRand())
									.description("Description for test " + testName)
									.build();
		
		//Load component and login
		logger.strongStep("Open Wikis component and login as: " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.login(testUser1);
		
		//Create a public wiki 
		logger.strongStep("Click on the Start a Wiki button and create a private wiki with a name that contains special characters");
		log.info("INFO: Create a private wiki with a name containing special characters");
		wiki.create(ui);
		
		//change wiki access to private
		logger.strongStep("Click on the Members link in the left navigation menu, then click on Manage Access button and change the Read access to 'Wiki members only'");
		log.info("INFO: Select the Members link from the left navigation menu, then click on Manage Access button and change the Read access to 'Wiki members only'");
		wiki.setReadAccess(ReadAccess.WikiOnly);
		wiki.changeAccess(ui, wiki);

		//ensure wiki was edited
		logger.strongStep("Verify the message 'The access settings were changed.' appears");
		log.info("INFO: Validate that the message 'The access settings were changed.' appears");
		ui.fluentWaitTextPresent("The access settings were changed.");

		logger.strongStep("Click on the Members link in the left navigation menu");
		log.info("INFO: Select the Members link from the left navigation menu");
		ui.clickLinkWait(WikisUIConstants.Members_Link);
    	
		logger.strongStep("Click on the Manage Access button");
		log.info("INFO: Select the Manage Access button");
		ui.clickLinkWait(WikisUIConstants.wikiManageAccess);
		
		//Validate that read access has changed to Wiki members only
		logger.strongStep("Verify the option 'Wiki members only' is selected under Read Access");
		log.info("INFO: Validate that read access has changed to Wiki members only");
		List<Element> readAccess = driver.getVisibleElements(WikisUIConstants.wikiEditAccessEditDisable);
		Assert.assertTrue(readAccess.get(0).getAttribute("aria-checked").contentEquals("true"),
						  "ERROR: Read access not set to 'Wiki members only'");
		
		ui.endTest();
		
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Tests that a user who creates a public wiki can also delete it.
	*<li><B>Step: </B>Open Wikis component and login.
	*<li><B>Step: </B>Click on the Start a Wiki button and create a public wiki.
	*<li><B>Step: </B>Click on the 'Wiki Actions' menu and then click on 'Delete Wiki' option.
	*<li><B>Step: </B>Enter the logged in user's name as the signature in the Delete Wiki dialog box.
	*<li><B>Step: </B>Select the checkbox in the dialog box and click on the Delete button.
	*<li><B>Verify: </B>The public wiki does not appear on My Wikis page anymore.
	*</ul> 
	*/
	@Test (groups = {"regression"})
	public void CreatePublicWikiAndDelete()throws Exception{

		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		
		BaseWiki wiki = new BaseWiki.Builder(testName + Helper.genDateBasedRand())
								.editAccess(EditAccess.EditorsAndOwners)
								.readAccess(ReadAccess.All)
								.addMembers(members)
								.tags("tag" + Helper.genDateBasedRand())
								.description("Description for test " + testName)
								.build();
		
		//Load component and login
		logger.strongStep("Open Wikis component and login as: " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.login(testUser1);
		
		//Create a public wiki 
		logger.strongStep("Click on the Start a Wiki button and create a public wiki");
		log.info("INFO: Create a public wiki");
		wiki.create(ui);
		
		//Delete the public wiki
		logger.strongStep("Click on the 'Wiki Actions' menu, then click on 'Delete Wiki' option followed by entering: " + testUser1.getDisplayName() + ""
				+ " as the signature in the Delete Wiki dialog box, selecting the checkbox and finally clicking on the Delete button");
		log.info("INFO: Delete the public wiki");
		wiki.delete(ui, testUser1);
		
		//Verify that the public wiki is no longer in the view
		logger.strongStep("Verify the public wiki does not appear on My Wikis page anymore");
		log.info("INFO: Validate that the public wiki does not appear on My Wikis page anymore");
		Assert.assertTrue(ui.fluentWaitTextNotPresent(wiki.getName()),
						  "ERROR: Wiki is still present");
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Tests that a user who creates a private wiki can also delete it.
	*<li><B>Step: </B>Open Wikis component and login.
	*<li><B>Step: </B>Click on the Start a Wiki button and create a private wiki.
	*<li><B>Step: </B>Click on the 'Wiki Actions' menu and then click on 'Delete Wiki' option.
	*<li><B>Step: </B>Enter the logged in user's name as the signature in the Delete Wiki dialog box.
	*<li><B>Step: </B>Select the checkbox in the dialog box and click on the Delete button.
	*<li><B>Verify: </B>The private wiki does not appear on My Wikis page anymore.
	*</ul> 
	*/
	@Test (groups = {"regression"})
	public void CreatePrivateWikiAndDelete()throws Exception{

		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		
		BaseWiki wiki = new BaseWiki.Builder(testName + Helper.genDateBasedRand())
									.editAccess(EditAccess.EditorsAndOwners)
									.readAccess(ReadAccess.WikiOnly)
									.addMembers(members)
									.tags("tag" + Helper.genDateBasedRand())
									.description("Description for test " + testName)
									.build();
			
		//Load component and login
		logger.strongStep("Open Wikis component and login as: " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.login(testUser1);
		
		//Create a private wiki 
		logger.strongStep("Click on the Start a Wiki button and create a private wiki");
		log.info("INFO: Create a private wiki");
		wiki.create(ui);
		
		//Delete the private wiki
		logger.strongStep("Click on the 'Wiki Actions' menu, then click on 'Delete Wiki' option followed by entering: " + testUser1.getDisplayName() + ""
				+ " as the signature in the Delete Wiki dialog box, selecting the checkbox and finally clicking on the Delete button");
		log.info("INFO: Delete the private wiki");
		wiki.delete(ui, testUser1);
		
		//Verify that the private wiki is no longer in the view
		logger.strongStep("Verify the private wiki does not appear on My Wikis page anymore");
		log.info("INFO: Validate that the private wiki does not appear on My Wikis page anymore");
		Assert.assertTrue(ui.fluentWaitTextNotPresent(wiki.getName()),
						  "ERROR: Wiki still present");
		
		ui.endTest();
	}
	
}
