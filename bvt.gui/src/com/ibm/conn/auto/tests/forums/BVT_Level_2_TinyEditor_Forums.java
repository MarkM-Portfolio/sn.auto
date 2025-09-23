/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential */
/*                                                                   */
/* OCO Source Materials */
/*                                                                   */
/* Copyright IBM Corp. 2010 */
/*                                                                   */
/* The source code for this program is not published or otherwise */
/* divested of its trade secrets, irrespective of what has been */
/* deposited with the U.S. Copyright Office. */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.conn.auto.tests.forums;


import com.ibm.conn.auto.webui.constants.ForumsUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseForum;
import com.ibm.conn.auto.appobjects.base.BaseForumTopic;
import com.ibm.conn.auto.appobjects.base.BaseFile.ShareLevel;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIFileHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.eventBuilder.files.FileEvents;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.FilesUI;
import com.ibm.conn.auto.webui.ForumsUI;
import com.ibm.conn.auto.webui.TinyEditorUI;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Role;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;

public class BVT_Level_2_TinyEditor_Forums extends SetUpMethods2 {
	
	private static Logger log = LoggerFactory.getLogger(BVT_Level_2_TinyEditor_Forums.class);
	private ForumsUI ui;
	private CommunitiesUI cUI;
	private TestConfigCustom cfg;
	private APICommunitiesHandler apiOwner;
	private APIFileHandler apiFileOwner;
	private User testUser;
	private FilesUI fui;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		cfg = TestConfigCustom.getInstance();
		
		//Load Users
		testUser = cfg.getUserAllocator().getUser();
		cfg.getUserAllocator().getUser();
				
		String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		apiFileOwner = new APIFileHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		new APIFileHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		
	  
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {
		
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = ForumsUI.getGui(cfg.getProductName(), driver);
		cUI = CommunitiesUI.getGui(cfg.getProductName(), driver);
		fui = FilesUI.getGui(cfg.getProductName(), driver);
		
		CommunitiesUI.getDefaultAccess(cfg.getProductName());
	}
		
	
	/**
	*<ul>
	*<li><B>Info: </B>Verify Paragraph functionality of tiny editor in create forum topic and reply</li>
	*<li><B>Step: </B>Go to Apps drop down menu and select Forums</li>
	*<li><B>Step: </B>Click the Forums tab</li>
	*<li><B>Step: </B>Click the Start a Forum button</li>
	*<li><B>Step: </B>Input information of Forum and save</li> 
	*<li><B>Step: </B>Click the Start a Topic button</li>
	*<li><B>Step: </B>Input all information of topic in tiny editor</li>
	*<li><B>Step: </B>Verify functionality for paragraph,indents of topic</li>
	*<li><B>Step: </B>Verify functionality for right left paragraph,alignment of tiny editor and save</li>
	*<li><B>Verify: </B>Text entered in tiny editor matches with forum topic description</li>
	*<li><B>Step: </B>Click the Reply to a Topic button</li>
	*<li><B>Step: </B>Input all information of topic in tiny editor</li>
	*<li><B>Step: </B>Verify functionality for paragraph,indents of topic</li>
	*<li><B>Step: </B>Verify functionality for right left paragraph,alignment of tiny editor and save</li>
    *<li><B>Verify: </B>Text entered in tiny editor matches with reply topic description</li>
	*</ul>
	* note: this test is not valid in smart cloud environment as smart cloud does not support forums natively
	*/
	@Test(groups = { "TinyEditor"})
	public void addForumTopicReply_TinyEditorParagraphFunctionality() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName=ui.startTest();
		
		BaseForum forum = new BaseForum.Builder(testName + Helper.genDateBasedRandVal())
									   .tags(Data.getData().commonTag)
									   .description(Data.getData().commonDescription).build();

		BaseForumTopic topic = new BaseForumTopic.Builder(testName + "_topic" + Helper.genDateBasedRandVal())
										   		 .tags(Data.getData().commonTag)
										   		 .description(Data.getData().commonDescription)
										   		 .tinyEditorFunctionalitytoRun("verifyParaInTinyEditor,verifyIndentsInTinyEditor,"
										   		 		+ "verifyRightLeftParagraphInTinyEditor,verifyAlignmentInTinyEditor,"
										   		 		+ "verifyLinkImageInTinyEditor")
										   		 .build();
		
		//Load the component
		logger.strongStep("Load Forums and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentForums);
		ui.login(testUser);
		
		//Navigate to owned Forms
		logger.strongStep("Navigate to the 'Owned Forums' view");
		log.info("INFO: Navigate to the 'Owned Fourms' view");
		ui.clickLinkWait(ForumsUIConstants.Im_An_Owner);
		
		//Create a forum
		logger.strongStep("Create a new Forum");
		log.info("INFO: Create a new Forum");
		forum.create(ui);

		log.info("INFO: Open Start Forum Topic Page and validate Tiny Editor fucntionality.");
		logger.strongStep("Open Start Forum Topic Page and validate Tiny Editor fucntionality.");
		String TEText = topic.verifyTinyEditor(ui).trim();
		String ComText = ui.getForumText().trim();
		log.info("INFO: Text in  saved forum topic " + ComText);
		log.info(TEText + " : " + ComText);
		Assert.assertEquals(TEText, ComText);
		
		topic.setDescription(topic.getDescription()+"Reply");
		
		log.info("INFO: Open Start Forum Topic Page and validate Tiny Editor fucntionality.");
		logger.strongStep("Open Start Forum Topic Page and validate Tiny Editor fucntionality.");
		String TEText_Reply = topic.verifyTinyEditor(ui).trim();
		String ComText_Reply =ui.getForumReplyText().trim();
		log.info("INFO: Text in  saved forum topic reply " + ComText_Reply);
		log.info(TEText_Reply + " : " + ComText_Reply);
		Assert.assertEquals(TEText, ComText);
	
		ui.endTest();
	}	
	
	/**
	*<ul>
	*<li><B>Info: </B>Verify font attribute functionality of tiny editor in create forum topic and reply</li>
	*<li><B>Step: </B>Go to Apps drop down menu and select Forums</li>
	*<li><B>Step: </B>Click the Forums tab</li>
	*<li><B>Step: </B>Click the Start a Forum button</li>
	*<li><B>Step: </B>Input information of Forum and save</li> 
	*<li><B>Step: </B>Click the Start a Topic button</li>
	*<li><B>Step: </B>Input all information of topic in tiny editor</li>
	*<li><B>Step: </B>Verify font attribute functionality of tiny editor and save</li>
	*<li><B>Verify: </B>Verify Text entered in tiny editor matches with forum topic text</li>
	*<li><B>Step: </B>Click reply to a Topic button</li>
	*<li><B>Step: </B>Input all information of topic in tiny editor</li>
	*<li><B>Step: </B>Verify font attribute functionality of tiny editor and save</li>
	*<li><B>Verify: </B>Verify Text entered in tiny editor matches with forum reply text</li>
	*</ul>
	* note: this test is not valid in smart cloud environment as smart cloud does not support forums natively
	*/
	@Test(groups = { "TinyEditor"})
public void addForumTopicReply_TinyEditorFontAttributeFunctionality() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName=ui.startTest();
		
		BaseForum forum = new BaseForum.Builder(testName + Helper.genDateBasedRandVal())
									   .tags(Data.getData().commonTag)
									   .description(Data.getData().commonDescription).build();

		BaseForumTopic topic = new BaseForumTopic.Builder(testName + "_topic" + Helper.genDateBasedRandVal())
										   		 .tags(Data.getData().commonTag)
										   		 .description("thisisTestdescriptionfortestcase")
										   		 .tinyEditorFunctionalitytoRun("verifyPermanentPenInTinyEditor,verifyAttributesInTinyEditor,"
									                        + "verifyFontSizeInTinyEditor,verifyFontInTinyEditor,verifyOtherTextAttributesAndFullScreenInTinyEditor,"
									                        + "verifyTextColorInTinyEditor,verifyBackGroundColorInTinyEditor,verifyInsertMediaInTinyEditor")
										   		 .build();
		
		//Load the component
		logger.strongStep("Load Forums and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentForums);
		ui.login(testUser);
		
		//Navigate to owned Forums
		logger.strongStep("Navigate to the 'Owned Forums' view");
		log.info("INFO: Navigate to the 'Owned Fourms' view");
		ui.clickLinkWait(ForumsUIConstants.Im_An_Owner);
		
		//Create a forum
		logger.strongStep("Create a new Forum");
		log.info("INFO: Create a new Forum");
		forum.create(ui);

		log.info("INFO: Open Start Forum Topic Page and validate Tiny Editor fucntionality.");
		logger.strongStep("Open Start Forum Topic Page and validate Tiny Editor fucntionality.");
		String TEText = topic.verifyTinyEditor(ui).trim();
		String ComText = ui.getForumText().trim();
		log.info("INFO: Text in  saved forum topic " + ComText);
		log.info(TEText + " : " + ComText);
		Assert.assertEquals(TEText, ComText);
				
		topic.setDescription(topic.getDescription()+"Reply");
		
		log.info("INFO: Open Start Forum Topic Reply Page and validate Tiny Editor fucntionality.");
		logger.strongStep("Open Start Forum Topic Reply Page and validate Tiny Editor fucntionality.");
		String TEText_Reply = topic.verifyTinyEditor(ui).trim();
		String ComText_Reply =ui.getForumReplyText().trim();
		log.info("INFO: Text in  saved forum topic reply" + ComText_Reply);
		log.info(TEText_Reply + " : " + ComText_Reply);
		Assert.assertEquals(TEText, ComText);
		
		ui.endTest();
	}	
			
	
	/**
	*<ul>
	*<li><B>Info: </B>Verify bullet table functionality of tiny editor in create forum topic and reply</li>
	*<li><B>Step: </B>Go to Apps drop down menu and select Forums</li>
	*<li><B>Step: </B>Click the Forums tab</li>
	*<li><B>Step: </B>Click the Start a Forum button</li>
	*<li><B>Step: </B>Input information of Forum and save</li> 
	*<li><B>Step: </B>Click the Start a Topic button</li>
	*<li><B>Step: </B>Input all information of topic in tiny editor</li>
	*<li><B>Step: </B>Verify functionality for LineBulletTable of tiny editor and save</li>
	*<li><B>Verify: </B>Text entered in tiny editor matches with forum  topic text</li>
	*<li><B>Step: </B>Click the Reply to a Topic button</li>
	*<li><B>Step: </B>Input all information of topic in tiny editor</li>
	*<li><B>Step: </B>Verify functionality for LineBulletTable of tiny editor and save</li>
	*<li><B>Verify: </B>Text entered in tiny editor matches with forum  topic text</li>
	*</ul>
	* note: this test is not valid in smart cloud environment as smart cloud does not support forums natively
	*/
	@Test(groups = { "TinyEditor"})
	public void addForumTopicReply_TinyEditorLineBulletTableFunctionality() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName=ui.startTest();
		
		BaseForum forum = new BaseForum.Builder(testName + Helper.genDateBasedRandVal())
									   .tags(Data.getData().commonTag)
									   .description("this is Test description with url and Browse").build();

		BaseForumTopic topic = new BaseForumTopic.Builder(testName + "_topic" + Helper.genDateBasedRandVal())
				.tags(Data.getData().commonTag).description("Forum this is Test description")
				.tinyEditorFunctionalitytoRun("verifyHorizontalLineInTinyEditor,verifyRowsCoulmnOfTableInTinyEditor,"
						+ "verifyBulletsAndNumbersInTinyEditor,verifyBlockQuoteInTinyEditor,verifyLinkImageInTinyEditor,verifyInsertiFrameInTinyEditor").build();
		
		//Load the component
		logger.strongStep("Load Forums and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentForums);
		ui.login(testUser);
		
		//Navigate to owned Forms
		logger.strongStep("Navigate to the 'Owned Forums' view");
		log.info("INFO: Navigate to the 'Owned Fourms' view");
		ui.clickLinkWait(ForumsUIConstants.Im_An_Owner);
		
		//Create a forum
		logger.strongStep("Create a new Forum");
		log.info("INFO: Create a new Forum");
		forum.create(ui);

		//Create Topic
		log.info("INFO: Open Start Forum Topic Page and validate Tiny Editor fucntionality.");
		logger.strongStep("Open Start Forum Topic Page and validate Tiny Editor fucntionality.");
		String TEText = topic.verifyTinyEditor(ui).trim();
		String ComText = ui.getForumText().trim();
		log.info("INFO: Text in  saved Forum topic " + ComText);
		log.info(TEText + " : " + ComText);
		Assert.assertEquals(TEText, ComText);
		
		topic.setDescription(topic.getDescription()+"Reply");
		
		log.info("INFO: Open Start Forum Topic Reply Page and validate Tiny Editor fucntionality.");
		logger.strongStep("Open Start Forum Topic Reply Page and validate Tiny Editor fucntionality.");
		String TEText_Reply = topic.verifyTinyEditor(ui).trim();
		String ComText_Reply =ui.getForumReplyText().trim();
		log.info("INFO: Text in  saved forum topic reply" + ComText_Reply);
		log.info(TEText_Reply + " : " + ComText_Reply);
		Assert.assertEquals(TEText, ComText);
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Verify find and replace functionality of tiny editor
	*<li><B>Info: </B>in create forum topic and reply</li>
	*<li><B>Step: </B>Go to Apps drop down menu and select Forums</li>
	*<li><B>Step: </B>Click the Forums tab</li>
	*<li><B>Step: </B>Click the Start a Forum button</li>
	*<li><B>Step: </B>Input information of Forum and save</li> 
	*<li><B>Step: </B>Click the Start a Topic button</li>
	*<li><B>Step: </B>Input all information of topic in tiny editor</li>
	*<li><B>Step: </B>Verify functionality for LineBulletTable of tiny editor and save</li>
	*<li><B>Verify: </B>Text entered in tiny editor matches with forum text</li>
	*<li><B>Step: </B>Click reply to a Topic button</li>
	*<li><B>Step: </B>Input all information of topic in tiny editor</li>
	*<li><B>Step: </B>Verify functionality for LineBulletTable of tiny editor and save</li>
	*<li><B>Verify: </B>Text entered in tiny editor matches with forum text</li>
	*</ul>
	* note: this test is not valid in smart cloud environment as smart cloud does not support forums natively
	*/
	@Test(groups = { "TinyEditor"})
	public void addForumTopicReply_FindReplaceSpellcheckUndoRedoSpecialCharLinkImageFunctionality() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName=ui.startTest();
		
		BaseForum forum = new BaseForum.Builder(testName + Helper.genDateBasedRandVal())
									   .tags(Data.getData().commonTag)
									   .description(Data.getData().commonDescription).build();

		BaseForumTopic topic = new BaseForumTopic.Builder(testName + "_topic" + Helper.genDateBasedRandVal())
										   		 .tags(Data.getData().commonTag)
										   		 .description("this is Test description with url and Browse")
										   		 .tinyEditorFunctionalitytoRun(
															"verifyFindReplaceInTinyEditor,verifySpellCheckInTinyEditor,"
															+ "verifyUndoRedoInTinyEditor,verifySpecialCharacterInTinyEditor,"
															+ "verifyEmotionsInTinyEditor,verifyWordCountInTinyEditor")
										   		 .build();
		
		//Load the component
		logger.strongStep("Load Forums and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentForums);
		ui.login(testUser);
		
		//Navigate to owned Forms
		logger.strongStep("Navigate to the 'Owned Forums' view");
		log.info("INFO: Navigate to the 'Owned Fourms' view");
		ui.clickLinkWait(ForumsUIConstants.Im_An_Owner);
		
		//Create a forum
		logger.strongStep("Create a new Forum");
		log.info("INFO: Create a new Forum");
		forum.create(ui);

		//Create Topic
		log.info("INFO: Open Start Forum Topic Page and validate Tiny Editor fucntionality.");
		logger.strongStep("Open Start Forum Topic Page and validate Tiny Editor fucntionality.");
		String TEText = topic.verifyTinyEditor(ui).trim();
		String ComText = ui.getForumText().trim();
		log.info("INFO: Text in Forum Topic" + ComText);
		log.info(TEText + " : " + ComText);
		Assert.assertEquals(TEText, ComText);
		
		topic.setDescription(topic.getDescription()+"Reply");
		
		log.info("INFO: Open Start Forum Topic Reply Page and validate Tiny Editor fucntionality.");
		logger.strongStep("Open Start Forum Topic Reply Page and validate Tiny Editor fucntionality.");
		String TEText_Reply = topic.verifyTinyEditor(ui).trim();
		String ComText_Reply =ui.getForumReplyText().trim();
		log.info("INFO: Text in  saved forum topic reply" + ComText_Reply);
		log.info(TEText_Reply + " : " + ComText_Reply);
		Assert.assertEquals(TEText, ComText);
	
		ui.endTest();
	}	
	
	
	/**
	*<ul>
	*<li><B>Info: </B>Verify Insert Link functionality of tiny editor in create forum topic</li>
	*<li><B>Step: </B>Go to Apps drop down menu and select Forums</li>
	*<li><B>Step: </B>Click the Forums tab</li>
	*<li><B>Step: </B>Click the Start a Forum button</li>
	*<li><B>Step: </B>Input information of Forum and save</li> 
	*<li><B>Step: </B>Click the Start a Topic button</li>
	*<li><B>Step: </B>Input all information of topic in tiny editor</li>
	*<li><B>Step: </B>Verify functionality for InsertLink image of tiny editor and save</li>
	*<li><B>Verify: </B>Verify inserted link with current window and new window</li>
	*<li><B>Step: </B>Click the Reply to a Topic button</li>
	*<li><B>Step: </B>Input all information of topic in tiny editor</li>
	*<li><B>Step: </B>Verify functionality for InsertLink image of tiny editor and save</li>
	*<li><B>Verify: </B>Verify inserted link with current window and new window</li>
	*</ul>
	* note: this test is not valid in smart cloud environment as smart cloud does not support forums natively
	*/
	@Test(groups = {"TinyEditor"})
	public void addForumTopicReply_verifyTinyEditorInsertLink() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName=ui.startTest();
		
		BaseForum forum = new BaseForum.Builder(testName + Helper.genDateBasedRandVal())
									   .tags(Data.getData().commonTag)
									   .description(Data.getData().commonDescription).build();

		BaseForumTopic topic = new BaseForumTopic.Builder(testName + "_topic" + Helper.genDateBasedRandVal())
										   		 .tags(Data.getData().commonTag)
										   		 .description("Insert Image: Dialog default UI")
										   		 .tinyEditorFunctionalitytoRun("verifyInsertLinkImageInTinyEditor")
										   		 .build();
		
		//Load the component
		logger.strongStep("Load Forums and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentForums);
		ui.login(testUser);
		
		//Navigate to owned Forms
		logger.strongStep("Navigate to the 'Owned Forums' view");
		log.info("INFO: Navigate to the 'Owned Fourms' view");
		ui.clickLinkWait(ForumsUIConstants.Im_An_Owner);
		
		//Create a forum
		logger.strongStep("Create a new Forum");
		log.info("INFO: Create a new Forum");
		forum.create(ui);

		log.info("INFO: Open Start Forum Topic Page and validate Tiny Editor fucntionality.");
		logger.strongStep("Open Start Forum Topic Page and validate Tiny Editor fucntionality.");
			
		String ExtectedValue = topic.verifyTinyEditor(ui).trim();

		String ActualValue = ui.getForumDescText().trim();
		
		Assert.assertEquals(ActualValue, ExtectedValue);
		
		ui.verifyInsertedLink("CurrentWindow_"+topic.getDescription()+"~NewWindow_"+topic.getDescription());
		
		topic.setDescription("Forum Reply "+topic.getDescription());
		
		log.info("INFO: Open Start Forum Topic Reply Page and validate Tiny Editor fucntionality.");
		logger.strongStep("Open Start Forum Topic Reply Page and validate Tiny Editor fucntionality.");
			
		String ExtectedValueReply = topic.verifyTinyEditor(ui).trim();

		String ActualValueReply = ui.getForumReplyText().trim();
		
		Assert.assertEquals(ActualValueReply, ExtectedValueReply);
		
		ui.verifyInsertedLink("CurrentWindow_"+topic.getDescription()+"~NewWindow_"+topic.getDescription());
				
		ui.endTest();
	}	
	
	/**
	*<ul>
	*<li><B>Info: </B>Verify LinkToConnectionsFilesFromFiles functionality of TinyEditor in create topic and reply</li> 
	*<li><B>Info: </B>tiny editor in create forum topic</li>
	*<li><B>Step: </B>Go to Apps drop down menu and select Forums</li>
	*<li><B>Step: </B>Click the Forums tab</li>
	*<li><B>Step: </B>Click the Start a Forum button</li>
	*<li><B>Step: </B>Input information of Forum and save</li> 
	*<li><B>Step: </B>Click the Start a Topic button</li>
	*<li><B>Step: </B>Input all information of topic in tiny editor</li>
	*<li><B>Step: </B>Verify functionality for link to connection files from files in tiny editor and save</li>
	*<li><B>Verify: </B>Verify download file in File Download section</li>
	*<li><B>Step: </B>Click the Reply to a Topic button</li>
	*<li><B>Step: </B>Input all information of topic in tiny editor</li>
	*<li><B>Step: </B>Verify functionality for link to connection files from files in tiny editor and save</li>
	*<li><B>Verify: </B>Verify download file in File Download section</li>
	*</ul>
	* note: this test is not valid in smart cloud environment as smart cloud does not support forums natively
	*/
	@Test(groups = {"TinyEditor"})
	public void addForumTopicReply_verifyLinkToConnectionsFilesFromFilesInTinyEditor() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName=ui.startTest();

		BaseFile file = new BaseFile.Builder(Data.getData().file1).extension(".jpg")
				.tags(testName + "_" + Helper.genDateBasedRand())
				.rename(testName + Helper.genDateBasedRand())
				.build();

		Assert.assertNotNull(apiFileOwner);

		logger.strongStep("Upoad public image via API ");
		log.info("INFO: Upoad public image via API ");
		FileEvents.addFile(file, testUser, apiFileOwner);
		file.setName(file.getRename() + file.getExtension());
		
		BaseForum forum = new BaseForum.Builder(testName + Helper.genDateBasedRandVal())
									   .tags(Data.getData().commonTag)
									   .description(Data.getData().commonDescription).build();

		BaseForumTopic topic = new BaseForumTopic.Builder(testName + "_topic" + Helper.genDateBasedRandVal())
										   		 .tags(Data.getData().commonTag)
										   		 .description("Forums Insert Image: Dialog default UI")
										   		 .tinyEditorFunctionalitytoRun("verifyLinkToConnectionsFilesInTinyEditor")
										   		 .build();
		
		//Load the component
		logger.strongStep("Load Forums and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentForums);
		ui.login(testUser);
		
		//Navigate to owned Forms
		logger.strongStep("Navigate to the 'Owned Forums' view");
		log.info("INFO: Navigate to the 'Owned Fourms' view");
		ui.clickLinkWait(ForumsUIConstants.Im_An_Owner);
		
		//Create a forum
		logger.strongStep("Create a new Forum");
		log.info("INFO: Create a new Forum");
		forum.create(ui);
		
		log.info(" image file name is " + file.getName());
		TinyEditorUI.setImageName(topic.getDescription(),file.getName());

		logger.strongStep("Verify link to connections files from files in tiny editor componenet");
		log.info("Verify link to connections files from files in tiny editor componenet");
		topic.verifyTinyEditor(ui).trim();
		
		log.info("INFO: Validate image download from Rich content");
		logger.strongStep("Validate image download from Rich content");
		driver.getFirstElement(ForumsUIConstants.ForumDesc+ " a:nth-of-type(1)").click();
		fui.verifyFileDownloaded(file.getName());
		
		topic.setDescription("Reply "+topic.getDescription());
		
		log.info("INFO: Click Reply to Forum Topic Page and validate Tiny Editor fucntionality.");
		
		logger.strongStep("Verify link to connections files from files in tiny editor componenet");
		log.info("Verify link to connections files from files in tiny editor componenet");
		topic.verifyTinyEditor(ui);
		
		log.info("INFO: Validate image download");
		logger.strongStep("Validate image download");
		driver.getFirstElement(ForumsUIConstants.forumReplyDesc+ " a:nth-of-type(1)").click();
		fui.verifyFileDownloaded(file.getName());
		
    	ui.endTest();
	}	
	
	/**
	*<ul>
	*<li><B>Info: </B>Verify LinkToConnections with upload functionality of TinyEditor in create topic and reply</li>
	*<li><B>Step: </B>Go to Apps drop down menu and select Forums</li>
	*<li><B>Step: </B>Click the Forums tab</li>
	*<li><B>Step: </B>Click the Start a Forum button</li>
	*<li><B>Step: </B>Input information of Forum and save</li> 
	*<li><B>Step: </B>Click the Start a Topic button</li>
	*<li><B>Step: </B>Input all information of topic in tiny editor</li>
	*<li><B>Step: </B>Verify functionality for link to connection files from my computer and save</li>
	*<li><B>Verify: </B>Verify download file in File Download section</li>
	*<li><B>Step: </B>Click the Reply to a Topic button</li>
	*<li><B>Step: </B>Input all information of topic in tiny editor</li>
	*<li><B>Step: </B>Verify functionality for link to connection files from my computer and save</li>
	*<li><B>Verify: </B>Verify download file in File Download section</li>
	*</ul>
	* note: this test is not valid in smart cloud environment as smart cloud does not support forums natively
	*/
	@Test(groups = {"TinyEditor"})
	public void addForumTopicReply_verifyLinkToConnectionsFiles_UploadInTinyEditor() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName=ui.startTest();

		BaseFile file = new BaseFile.Builder(Data.getData().file1).extension(".jpg")
				.tags(testName + "_" + Helper.genDateBasedRand())
				.rename(testName + Helper.genDateBasedRand())
				.build();

		Assert.assertNotNull(apiFileOwner);

		logger.strongStep("Upoad public image via API ");
		log.info("INFO: Upoad public image via API ");
		FileEvents.addFile(file, testUser, apiFileOwner);
		file.setName(file.getName());
		
		BaseForum forum = new BaseForum.Builder(testName + Helper.genDateBasedRandVal())
									   .tags(Data.getData().commonTag)
									   .description(Data.getData().commonDescription).build();

		BaseForumTopic topic = new BaseForumTopic.Builder(testName + "_topic" + Helper.genDateBasedRandVal())
										   		 .tags(Data.getData().commonTag)
										   		 .description("Upload Image: Dialog default UI")
										   		 .tinyEditorFunctionalitytoRun("verifyLinkToConnectionsFilesInTinyEditor")
										   		 .build();
		
		//Load the component
		logger.strongStep("Load Forums and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentForums);
		ui.login(testUser);
		
		//Navigate to owned Forms
		logger.strongStep("Navigate to the 'Owned Forums' view");
		log.info("INFO: Navigate to the 'Owned Fourms' view");
		ui.clickLinkWait(ForumsUIConstants.Im_An_Owner);
		
		//Create a forum
		logger.strongStep("Create a new Forum");
		log.info("INFO: Create a new Forum");
		forum.create(ui);
		
		log.info(" image file name is " + file.getName());
		TinyEditorUI.setImageName(topic.getDescription(),file.getName());

		logger.strongStep("Verify link to connections files from files in tiny editor componenet");
		log.info("Verify link to connections files from files in tiny editor componenet");
		topic.verifyTinyEditor(ui).trim();
		
		log.info("INFO: Validate image download from Rich content");
		logger.strongStep("Validate image download from Rich content");
		
		TinyEditorUI tui = new TinyEditorUI(driver);
		tui.ImageDownload(ForumsUIConstants.ForumDesc);
		ui.waitForPageLoaded(driver);
		ui.fluentWaitPresent(ForumsUIConstants.ForumDesc);
		ui.fluentWaitElementVisible(ForumsUIConstants.ForumDesc);
		
		if(TinyEditorUI.picturecount==1)
			fui.verifyFileDownloaded(Data.getData().file1);
		else
			fui.verifyFileDownloaded(Data.getData().file2);
		
		topic.setDescription("Reply "+topic.getDescription());
		
		log.info("INFO: Click Reply to Forum Topic Page and validate Tiny Editor fucntionality.");
		
		logger.strongStep("Verify link to connections files from files in tiny editor componenet");
		log.info("Verify link to connections files from files in tiny editor componenet");
		topic.verifyTinyEditor(ui);
		
		log.info("INFO: Validate image download from Forum topic");
		logger.strongStep("Validate image download from Forum Topic");
		tui.ImageDownload(ForumsUIConstants.forumReplyDesc);
		//driver.getFirstElement(ForumsUI.forumReplyDesc+ " a:nth-of-type(1)").click();
		if(TinyEditorUI.picturecount==1)
			fui.verifyFileDownloaded(Data.getData().file1);
		else
			fui.verifyFileDownloaded(Data.getData().file2);
	
		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Info:</B>Verify LinkToConnectionsFiles from this community functionality</li>
	 * <li><B>Info:</B>functionality of TinyEditor in create topic</li>
	 * <li><B>Step:</B>Created Images form API</li>
	 * <li><B>Step:</B>Create community from API and Navigate to community dash-board page</li>
	 * <li><B>Step:</B>Create forum topic from community dash-board page</li>
	 * <li><B>Verify:</B>Verify Insert Link to connections files from This Community functionality in TinyEditor</li>* 
	 * <li></B>Verify link Images is added Content Widget of Community</li>
	 * <li></B>Verify link Images preview and image download in Forum content Widget of Community</li>
	 * <li><B>Step:</B>Reply to forum topic</li>
	 * <li><B>Verify:</B>Verify Insert Link to connections files from This Community functionality in TinyEditor</li>* 
	 * <li></B>Verify link Images is added Content Widget of Community</li>
	 * <li></B>Verify link Images preview and image download in Forum content Widget of Community</li>
	 * <li><B>Step:</B>Delete Image from API</li>
	 * <li><B>Step:</B>Delete community</li>
	 * </ul>
	 */
	@Test(groups = { "TinyEditor" })
	public void addForumTopicReply_verifyLinkToConnectionsFilesFromThisCommunityInTinyEditor() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		log.info("INFO: " + testUser.getDisplayName() + " creating a new community using the API");

		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
				.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal())
				.build();
		
		Community publicCommunity = community.createAPI(apiOwner);
		
		// Add the UUID to community
		log.info("INFO: Set UUID of community");
		community.setCommunityUUID(community.getCommunityUUID_API(apiOwner, publicCommunity));
		
		new BaseForum.Builder(testName + Helper.genDateBasedRandVal()).tags(Data.getData().commonTag)
				.description(Data.getData().commonDescription).build();

		BaseForumTopic topic = new BaseForumTopic.Builder(testName + "_topic" + Helper.genDateBasedRandVal())
				.tags(Data.getData().commonTag).description("Forums this is Test description for testcase Community")
				.tinyEditorFunctionalitytoRun("verifyLinkToConnectionsFilesInTinyEditor").partOfCommunity(community)
				.build();

		log.info("INFO: " + testUser.getDisplayName() + " creating a public file");
		BaseFile file = new BaseFile.Builder(Data.getData().file2).extension(".jpg").shareLevel(ShareLevel.EVERYONE)
				.rename(testName + "_" + Helper.genDateBasedRand()).tags(testName + Helper.genStrongRand()).build();

		Assert.assertNotNull(apiFileOwner);

		log.info("INFO: " + testUser.getDisplayName() + " sharing file with community using API method");
		FileEntry imageFile = FileEvents.addFile(file, testUser, apiFileOwner);
		file.setName(file.getRename() + file.getExtension());

		log.info("INFO: Change permissions to public");
		apiFileOwner.changePermissions(file, imageFile);

		log.info("INFO: Share file with the community");
		apiFileOwner.shareFileWithCommunity(imageFile, publicCommunity, Role.OWNER);

		logger.strongStep("Check to see if the Forum widget is enabled. If it is not enabled, then enable it");
		log.info("INFO: Checking to see if the Forum widget is enabled. If it is not enabled, then enable it");
		if (!apiOwner.hasWidget(publicCommunity, BaseWidget.FORUM)) {
			log.info("INFO: Add the Forum widget to the Community using API");
			community.addWidgetAPI(publicCommunity, apiOwner, BaseWidget.FORUM);
		}

		// GUI
		// Load component and login
		logger.strongStep("Load Forums and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		// navigate to the API community
		logger.strongStep("Navigate to the Community");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(cUI);

		log.info(" image file name is " + file.getName());
		TinyEditorUI.setImageName(topic.getDescription(), file.getName());

		logger.strongStep("Verify link to connections files from files in tiny editor componenet");
		log.info("Verify link to connections files from files in tiny editor componenet");
		topic.verifyTinyEditor(ui).trim();

		log.info("INFO: Validate image download from Rich content");
		logger.strongStep("Validate image download from Rich content");
		driver.getFirstElement(ForumsUIConstants.ForumDesc + " a:nth-of-type(1)").click();
		fui.verifyFileDownloaded(file.getName());
		
		topic.setDescription("Reply "+topic.getDescription());
		
		log.info("INFO: Click Reply to Forum Topic Page and validate Tiny Editor fucntionality.");
		
		log.info(" image file name is " + file.getName());
		TinyEditorUI.setImageName(topic.getDescription(), file.getName());

		logger.strongStep("Verify link to connections files from files in tiny editor componenet");
		log.info("Verify link to connections files from files in tiny editor componenet");
		topic.verifyTinyEditor(ui).trim();

		log.info("INFO: Validate image download from Rich content");
		logger.strongStep("Validate image download from Rich content");
		driver.getFirstElement(ForumsUIConstants.forumReplyDesc + " a:nth-of-type(1)").click();
		fui.verifyFileDownloaded(file.getName());

		log.info("INFO: Delete the Community.");
		logger.strongStep("Delete the Community");
		apiOwner.deleteCommunity(apiOwner.getCommunity(community.getCommunityUUID()));

		logger.weakStep("Delete uploaded file via API");
		log.info("INFO: Delete uploaded file via API");
		apiFileOwner.deleteFile(imageFile);

		ui.endTest();

	}
	
	
	/**
	*<ul>
	*<li><B>Info: </B>Verify edit description functionality of tiny editor in create topic and reply</li>
	*<li><B>Step: </B>Go to Apps drop down menu and select Forums</li>
	*<li><B>Step: </B>Click the Forums tab</li>
	*<li><B>Step: </B>Click the Start a Forum button</li>
	*<li><B>Step: </B>Input information of Forum and save</li> 
	*<li><B>Step: </B>Click the Start a Topic button</li>
	*<li><B>Step: </B>Input all information of topic in tiny editor and create topic</li>
	*<li><B>Step: </B>Edit topic modify description and save</li>
	*<li><B>Step: </B>Verify functionality for edit description of tiny editor and save</li>
	*<li><B>Verify: </B>Edited description gets displayed Forum's description</li>
	*<li><B>Step: </B>Click the Reply to a Topic link</li>
	*<li><B>Step: </B>Input all information of topic in tiny editor and create topic</li>
	*<li><B>Step: </B>Edit reply modify description and save</li>
	*<li><B>Step: </B>Verify functionality for edit description of reply with tiny editor and save</li>
	*<li><B>Verify: </B>Edited description gets displayed Forum's Reply description</li>
	*</ul>
	* note: this test is not valid in smart cloud environment as smart cloud does not support forums natively
	*/
	@Test(groups = { "TinyEditor"})
	public void addForumTopicReply_EditDescription_TinyEditor() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		BaseForum forum = new BaseForum.Builder(testName + Helper.genDateBasedRandVal()).tags(Data.getData().commonTag)
				.description(Data.getData().commonDescription).build();

		BaseForumTopic topic = new BaseForumTopic.Builder(testName + "_topic" + Helper.genDateBasedRandVal())
				.tags(Data.getData().commonTag).description("This is test description for Forum")
				.tinyEditorEnabled(true).build();

		// Load the component
		logger.strongStep("Load Forums and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentForums);
		ui.login(testUser);

		// Navigate to owned Forms
		logger.strongStep("Navigate to the 'Owned Forums' view");
		log.info("INFO: Navigate to the 'Owned Fourms' view");
		ui.clickLinkWait(ForumsUIConstants.Im_An_Owner);

		// Create a forum
		logger.strongStep("Create a new Forum");
		log.info("INFO: Create a new Forum");
		forum.create(ui);

		// Create a new topic inside the Forum
		logger.strongStep("Create a new Forum topic");
		log.info("INFO: Create a new Forum topic");
		topic.create(ui);

		// Verify tiny editor functionality
		logger.strongStep("Verify edit functionality of tiny editor componenet");
		log.info("Verify edit functionality of tiny editor componenet");

		String EditedDescripton = topic.getDescription().concat("Edited");

		String DescAfterEdit = ui.editDescriptionInTinyEditor(topic, EditedDescripton);

		Assert.assertEquals(DescAfterEdit, EditedDescripton);
		
		// Reply to topic
		logger.strongStep("Reply to the Forum topic");
		log.info("INFO: Reply to the Forum topic");
		ui.replyToTopic(topic);
		
		// Verify tiny editor functionality
		logger.strongStep("Verify edit functionality of tiny editor componenet");
		log.info("Verify edit functionality of tiny editor componenet");

		String EditedDescriptonReply = topic.getDescription().concat("Edited Reply");

		String DescAfterEditReply = ui.editDescriptionInTinyEditor(topic, EditedDescriptonReply);

		Assert.assertEquals(DescAfterEditReply, EditedDescriptonReply);

		ui.endTest();
	}	

		

}