package com.ibm.conn.auto.tests.commonui.regression;

import com.ibm.conn.auto.webui.constants.ForumsUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseForum;
import com.ibm.conn.auto.appobjects.base.BaseForumTopic;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.ForumsUI;

public class Forums extends SetUpMethods2 {
	
	private static Logger log = LoggerFactory.getLogger(Forums.class);
	private ForumsUI ui;
	private TestConfigCustom cfg;
	private User testUser;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		
		cfg = TestConfigCustom.getInstance();
		ui = ForumsUI.getGui(cfg.getProductName(), driver);
		
		//Load Users
		testUser = cfg.getUserAllocator().getUser();
		
	}
	

	/**
	* createForumTopic()
	*<ul>
	*<li><B>Info: </B>Create a forum and topic</li>
	*<li><B>Step: </B>Go to the Apps drop down menu and Click Forums</li>
	*<li><B>Step: </B>Click the Forums Tab</li>
	*<li><B>Step: </B>Click the Start a Forum button</li>
	*<li><B>Step: </B>Fill out the forum and save</li>
	*<li><B>Step: </B>Click the Start a Topic button</li>
	*<li><B>Step: </B>Input title, tag and description and save</li>
	*<li><B>Verify: </B>Forum and topic are displayed in the view</li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B568243C8AE5645C85257F38005A7B2D">TTT - Common UI - Cross Application Regression/Forums</a></li>
	*</ul>
	*Note: On Prem only
	*/
	@Test(groups = {"regression"} , enabled=false )
	public void createForumTopic() throws Exception {
	
		String testName=ui.startTest();
	
		BaseForum forum = new BaseForum.Builder(testName + "_forum" + Helper.genDateBasedRandVal())
									   .tags("forum_tag")
									   .description("This is the description for creating forum").build();

		BaseForumTopic topic = new BaseForumTopic.Builder(testName + "_topic" + Helper.genDateBasedRandVal())
												 .tags("topic_tag")
												 .description("This is the description for creating topic").build();
	
		//Load the component
		log.info("INFO: Load Forums component and login");
		ui.loadComponent(Data.getData().ComponentForums);
		ui.login(testUser);
	
		//Navigate to owned Forums view
		log.info("INFO: Navigate to the owned Forums view");
		ui.clickLinkWait(ForumsUIConstants.Im_An_Owner);
	
		//Create a forum
		log.info("INFO: Create a forum");
		forum.create(ui);

		//Start a topic in the Forum created above
		log.info("INFO: Create a topic");
		topic.create(ui);
	
		//select Forums to return to the main page
		log.info("INFO: Select Forums link in top left corner");
		ui.clickLinkWait(ForumsUIConstants.topComponentForumLink);
		
		//Select I'm An Owner from left menu option and then select FORUMS tab
		log.info("INFO: Select I'm An Owner from left menu option and then select FORUMS tab");
		ui.clickLinkWait(ForumsUIConstants.Im_An_Owner);
		ui.clickLinkWait(ForumsUIConstants.Centre_Content_Filter_Tabs_Tab1);

		//Verify the forum exists
		log.info("INFO: Validate that the forum exists");
		Assert.assertTrue(ui.fluentWaitTextPresent(forum.getName()), "ERROR: Failed to find the forum");
		
		// Click the forum created above
		log.info("INFO: Select the forum created above");
		ui.clickLinkWait("link=" + forum.getName());

		//Verify the topic exists by clicking it
		log.info("INFO: Validate that the topic exists");
		ForumsUI.selectForumTopic(topic);
		Assert.assertTrue(ui.fluentWaitTextPresent(topic.getTitle()), "ERROR: Failed to find the topic");

		//Clean Up: Delete the forum
		log.info("INFO: Delete the forum");
		forum.delete(ui);
		
		ui.endTest();
	}

}