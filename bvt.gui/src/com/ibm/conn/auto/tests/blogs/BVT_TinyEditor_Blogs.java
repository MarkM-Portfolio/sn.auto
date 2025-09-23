package com.ibm.conn.auto.tests.blogs;

import com.ibm.conn.auto.webui.constants.BlogsUIConstants;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseBlog;
import com.ibm.conn.auto.appobjects.base.BaseBlog.Theme;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIBlogsHandler;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.BlogsUI;
import com.ibm.conn.auto.webui.CommunitiesUI;

public class BVT_TinyEditor_Blogs extends SetUpMethods2 {

	private static Logger log = LoggerFactory.getLogger(BVT_TinyEditor_Blogs.class);
	private TestConfigCustom cfg;
	private BlogsUI ui;
	private static User testUser1;
	private APIBlogsHandler apiOwner;
	private String serverURL;

	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
		cfg = TestConfigCustom.getInstance();
	}

	@BeforeMethod(alwaysRun = true)
	public void setUp() {
		cfg = TestConfigCustom.getInstance();
		ui = BlogsUI.getGui(cfg.getProductName(), driver);
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		testUser1 = cfg.getUserAllocator().getUser();

		new APICommunitiesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		apiOwner = new APIBlogsHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()),
				testUser1.getPassword());
		CommunitiesUI.getDefaultAccess(cfg.getProductName());
	}

	/**
	 * <ul>
	 * <li><B>Info:</B>Validate tiny editor features on creating New Entry in Blog</li>
	 * <li><B>Step:</B>Create a Blogs with API.</li>
	 * <li><B>Step:</B>Navigate to the  same Blog </li>
	 * <li><B>Step:</B>Click on New Entry Button for the Created Blog</li>
	 * <li><B>Step:</B>Enter the Name of the Entry</li>
	 * <li><B>Verify:</B>Verify Paragraph and Header functionality in TinyEditor
	 * <li><B>Verify:</B>Verify Right to Left Paragraph functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify Alignment functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify verifyIndentsInTinyEditor functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify verifyUploadImageFromDiskInTinyEditor functionality in TinyEditor</li>
	 * <li><B>Step:</B>Post the Entry</li>
	 * <li><B>Verify:</B>Verify tiny Editor text on Description of Entry in Blog Page</li>
	 * <li><B>Step:</B>Delete Blog</li>
	 * </ul>
	 */
	@Test(groups = { "TinyEditor" })
	public void verifyTinyEditorParagraphFunctionalityInBlog() throws Exception {
		String testName = ui.startTest();
		String rand = Helper.genDateBasedRand();

		BaseBlog blog = new BaseBlog.Builder(testName + rand, testName + rand).tags(Data.getData().commonTag + rand)
				.description("Insert Image: Dialog default UI").theme(Theme.Blog_with_Bookmarks)
				.tinyEditorFunctionalitytoRun("verifyParaInTinyEditor,verifyRightLeftParagraphInTinyEditor,"
				+ "verifyAlignmentInTinyEditor,verifyIndentsInTinyEditor,verifyUploadImageFromDiskInTinyEditor,verifyMentionUserInTinyEditor")
				.build();

		log.info("INFO: Create blog using API");
		blog.createAPI(apiOwner);

		log.info("INFO: Log into Blogs as " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentBlogs);
		ui.login(testUser1);

		log.info("INFO: Navigate to My Blogs");
		ui.clickLinkWithJavascript(BlogsUIConstants.MyBlogs);

		log.info("INFO: Navigate to the blog & click New Entry");
		ui.clickLinkWithJavascript(BlogsUI.getNewEntryBtnForBlog(blog));

		log.info("INFO: Verify Tiny Editor functionality");
		String ExtectedValue = blog.verifyTinyEditor(ui,testUser1).trim();

		String ActualValue = ui.getBlogEntryDescText().trim();

		Assert.assertEquals(ActualValue, ExtectedValue);

		log.info("INFO: Delete the blog");
		blog.delete(ui);

		ui.endTest();
	}

	/**
	 * <ul>
	 * <li><B>Info:</B>Validate tiny editor features on creating New Entry in Blog</li>
	 * <li><B>Step:</B>Create a Blogs with API.</li>
	 * <li><B>Step:</B>Navigate to the  same Blog </li>
	 * <li><B>Step:</B>Click on New Entry Button for the Created Blog</li>
	 * <li><B>Step:</B>Enter the Name of the Entry</li>
	 * <li><B>Verify:</B>Verify Permanent Pen functionality in TinyEditor
	 * <li><B>Verify:</B>Verify Font attributes functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify Font Size in TinyEditor</li>
	 * <li><B>Verify:</B>Verify Font functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify Other Text attributes and full screen functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify Text Color functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify Back Ground functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify verifyInsertMediaInTinyEditor functionality in TinyEditor</li>
	 * <li><B>Step:</B>Post the Entry</li>
	 * <li><B>Verify:</B>Verify tiny Editor text on Description of Entry in Blog Page</li>
	 * <li><B>Step:</B>Delete Blog</li>
	 * </ul>
	 */
	@Test(groups = { "TinyEditor" })
	public void verifyTinyEditorFontAttributeFunctionalityInBlog() throws Exception {
		String testName = ui.startTest();
		String rand = Helper.genDateBasedRand();

		BaseBlog blog = new BaseBlog.Builder(testName + rand, testName + rand).tags(Data.getData().commonTag + rand)
				.description("thisisTestdescriptionfortestcasehighlights").theme(Theme.Blog_with_Bookmarks)
				.tinyEditorFunctionalitytoRun("verifyPermanentPenInTinyEditor,verifyAttributesInTinyEditor,"
						+ "verifyFontSizeInTinyEditor,verifyFontInTinyEditor,verifyOtherTextAttributesAndFullScreenInTinyEditor,"
						+ "verifyTextColorInTinyEditor,verifyBackGroundColorInTinyEditor,verifyInsertMediaInTinyEditor")
				.build();

		log.info("INFO: Create blog using API");
		blog.createAPI(apiOwner);

		log.info("INFO: Log into Blogs as " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentBlogs);
		ui.login(testUser1);

		log.info("INFO: Navigate to My Blogs");
		ui.clickLinkWithJavascript(BlogsUIConstants.MyBlogs);

		log.info("INFO: Navigate to the blog & click New Entry");
		ui.clickLinkWithJavascript(BlogsUI.getNewEntryBtnForBlog(blog));

		log.info("INFO: Verify Tiny Editor functionality");
		String ExtectedValue = blog.verifyTinyEditor(ui,testUser1).trim();

		String ActualValue = ui.getBlogEntryDescText().trim();

		Assert.assertEquals(ActualValue, ExtectedValue);

		log.info("INFO: Delete the blog");
		blog.delete(ui);

		ui.endTest();
	}

	/**
	 * <ul>
	 * <li><B>Info:</B>Validate tiny editor features on creating New Entry in Blog</li>
	 * <li><B>Step:</B>Create a Blogs with API.</li>
	 * <li><B>Step:</B>Navigate to the  same Blog </li>
	 * <li><B>Step:</B>Click on New Entry Button for the Created Blog</li>
	 * <li><B>Step:</B>Enter the Name of the Entry</li>
	 * <li><B>Verify:</B>Verify Horizontal Line functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify Rows and Columns, images, texts and nested table in Table of TinyEditor</li>
	 * <li><B>Verify:</B>Verify Bullets and Numbers functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify verifyBlockQuoteInTinyEditor in TinyEditor</li>
	 * <li><B>Verify:</B>Verify verifyLinkImageInTinyEditor in TinyEditor</li>
	 * <li><B>Verify:</B>Verify uverifyInsertiFrameInTinyEditor in TinyEditor</li>
	 * <li><B>Step:</B>Post the Entry</li>
	 * <li><B>Verify:</B>Verify tiny Editor text on Description of Entry in Blog Page</li>
	 * <li><B>Step:</B>Delete Blog</li>
	 * </ul>
	 */
	@Test(groups = { "TinyEditor" })
	public void verifyTinyEditorLineBulletTableImageIFrameFunctionalityInBlog() throws Exception {
		String testName = ui.startTest();
		String rand = Helper.genDateBasedRand();

		BaseBlog blog = new BaseBlog.Builder(testName + rand, testName + rand).tags(Data.getData().commonTag + rand)
				.description("this is Test description with url and Browse").theme(Theme.Blog_with_Bookmarks)
				.tinyEditorFunctionalitytoRun("verifyHorizontalLineInTinyEditor,verifyRowsCoulmnOfTableInTinyEditor,"
						+ "verifyBulletsAndNumbersInTinyEditor,verifyBlockQuoteInTinyEditor,verifyLinkImageInTinyEditor,verifyInsertiFrameInTinyEditor")
				.build();

		log.info("INFO: Create blog using API");
		blog.createAPI(apiOwner);

		log.info("INFO: Log into Blogs as " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentBlogs);
		ui.login(testUser1);

		log.info("INFO: Navigate to My Blogs");
		ui.clickLinkWithJavascript(BlogsUIConstants.MyBlogs);

		log.info("INFO: Navigate to the blog & click New Entry");
		ui.clickLinkWithJavascript(BlogsUI.getNewEntryBtnForBlog(blog));

		log.info("INFO: Verify Tiny Editor functionality");
		String ExtectedValue = blog.verifyTinyEditor(ui,testUser1).trim();

		String ActualValue = ui.getBlogEntryDescText().trim();

		Assert.assertEquals(ActualValue, ExtectedValue);

		log.info("INFO: Delete the blog");
		blog.delete(ui);

		ui.endTest();
	}

	/**
	 * <ul>
	 * <li><B>Info:</B>Validate tiny editor features on creating New Entry in Blog</li>
	 * <li><B>Step:</B>Create a Blogs with API.</li>
	 * <li><B>Step:</B>Navigate to the  same Blog </li>
	 * <li><B>Step:</B>Click on New Entry Button for the Created Blog</li>
	 * <li><B>Step:</B>Enter the Name of the Entry</li>
	 * <li><B>Verify:</B>Verify Find and Replace functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify Special Character functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify Link Image functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify Spell check functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify Undo Redo functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify Emotions functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify Word Count functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify verifyCodeSampleIntinyEditor functionality in TinyEditor</li>
	 * <li><B>Step:</B>Post the Entry</li>
	 * <li><B>Verify:</B>Verify tiny Editor text on Description of Entry in Blog Page</li>
	 * <li><B>Step:</B>Delete Blog</li>
	 * </ul>
	 */
	@Test(groups = { "TinyEditor" })
	public void verifyTinyEditorFindReplaceSpellcheckUndoRedoSpecialCharFunctionalityInBlog() throws Exception {
		String testName = ui.startTest();
		String rand = Helper.genDateBasedRand();

		BaseBlog blog = new BaseBlog.Builder(testName + rand, testName + rand).tags(Data.getData().commonTag + rand)
				.description("this is Test description with url and Browse").theme(Theme.Blog_with_Bookmarks)
				.tinyEditorFunctionalitytoRun("verifyFindReplaceInTinyEditor,verifySpellCheckInTinyEditor,"
						+ "verifySpecialCharacterInTinyEditor,verifyUndoRedoInTinyEditor,verifyEmotionsInTinyEditor,"
						+"verifyWordCountInTinyEditor,verifyCodeSampleIntinyEditor")
				.build();

		log.info("INFO: Create blog using API");
		blog.createAPI(apiOwner);

		log.info("INFO: Log into Blogs as " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentBlogs);
		ui.login(testUser1);

		ui.waitForPageLoaded(driver);
		
		log.info("INFO: Navigate to My Blogs");
		ui.clickLinkWithJavascript(BlogsUIConstants.MyBlogs);

		ui.fluentWaitPresent(BlogsUI.getNewEntryBtnForBlog(blog));
		log.info("INFO: Navigate to the blog & click New Entry");
		ui.clickLinkWithJavascript(BlogsUI.getNewEntryBtnForBlog(blog));

		log.info("INFO: Verify Tiny Editor functionality");
		String ExtectedValue = blog.verifyTinyEditor(ui,testUser1).trim();

		String ActualValue = ui.getBlogEntryDescText().trim();

		Assert.assertEquals(ActualValue, ExtectedValue);

		log.info("INFO: Delete the blog");
		blog.delete(ui);

		ui.endTest();

	}

	/**
	 * <ul>
	 * <li><B>Info:</B>Validate tiny editor features on creating New Entry in Blog</li>
	 * <li><B>Step:</B>Create a Blogs with API.</li>
	 * <li><B>Step:</B>Navigate to the  same Blog </li>
	 * <li><B>Step:</B>Click on New Entry Button for the Created Blog</li>
	 * <li><B>Step:</B>Enter the Name of the Entry</li>
	 * <li><B>Verify:</B>Verify Insert Link functionality in TinyEditor</li>
	 * <li><B>Step:</B>Post the Entry</li>
	 * <li><B>Verify:</B>Verify tiny Editor text on Description of Entry in Blog Page</li>
	 * <li><B>Step:</B>Delete Blog</li>
	 * </ul>
	 */
	@Test(groups = { "TinyEditor" })
	public void verifyTinyEditorInsertLinkInBlog() throws Exception {
		String testName = ui.startTest();
		String rand = Helper.genDateBasedRand();

		BaseBlog blog = new BaseBlog.Builder(testName + rand, testName + rand).tags(Data.getData().commonTag + rand)
				.description("Insert Image: Dialog default UI").theme(Theme.Blog_with_Bookmarks)
				.tinyEditorFunctionalitytoRun("verifyInsertLinkImageInTinyEditor").build();

		log.info("INFO: Create blog using API");
		blog.createAPI(apiOwner);

		log.info("INFO: Log into Blogs as " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentBlogs);
		ui.login(testUser1);

		log.info("INFO: Navigate to My Blogs");
		ui.clickLinkWithJavascript(BlogsUIConstants.MyBlogs);

		log.info("INFO: Navigate to the blog & click New Entry");
		ui.clickLinkWithJavascript(BlogsUI.getNewEntryBtnForBlog(blog));

		log.info("INFO: Verify Tiny Editor functionality");
		String ExtectedValue = blog.verifyTinyEditor(ui,testUser1).trim();

		String ActualValue = ui.getBlogEntryDescText().trim();
		ui.verifyInsertedLink("CurrentWindow_Entry"+blog.getName()+"~NewWindow_Entry"+blog.getName());
		Assert.assertEquals(ActualValue, ExtectedValue);

		log.info("INFO: Delete the blog");
		blog.delete(ui);

		ui.endTest();
	}

	/**
	 * <ul>
	* <li><B>Info:</B>Validate tiny editor features on creating New Entry in Blog</li>
	 * <li><B>Step:</B>Create a Blogs with API.</li>
	 * <li><B>Step:</B>Navigate to the  same Blog </li>
	 * <li><B>Step:</B>Click on New Entry Button for the Created Blog</li>
	 * <li><B>Step:</B>Enter the Name and Description of the Entry</li>
	 * <li><B>Step:</B>Select edit description and edit the rich content description</li>
	 * <li><B>Step:</B>Post the Entry</li>
	 * <li><B>Verify:</B>Verify tiny Editor text on Description of Entry in Blog Page</li>
	 * <li><B>Step:</B>Select Edit for the created Entry </li>
	 * <li><B>Step:</B>Enter the Edit description message</li> 
	 * <li><B>Step:</B>Post the Entry</li>
	 * <li><B>Verify:</B>Verify tiny Editor Description of Entry is successfully Edited in Blog Page</li>
	 * <li><B>Step:</B>Delete Blog</li>
	 * </ul>
	 */

	@Test(groups = { "TinyEditor" })
	public void verifyTinyEditorEditFunctionalityInBlog() throws Exception {
		String testName = ui.startTest();
		
		String rand = Helper.genDateBasedRand();
		BaseBlog blog = new BaseBlog.Builder(testName + rand, testName + rand).tags(Data.getData().commonTag + rand)
				.tinyEditorFunctionalitytoRun("verifyEditDescriptionInTinyEditor")
				.description("Insert Image: Dialog default UI").theme(Theme.Blog_with_Bookmarks).build();

		log.info("INFO: Create blog using API");
		blog.createAPI(apiOwner);

		log.info("INFO: Log into Blogs as " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentBlogs);
		ui.login(testUser1);

		log.info("INFO: Navigate to My Blogs");
		ui.clickLinkWithJavascript(BlogsUIConstants.MyBlogs);
		
		log.info("INFO: Navigate to the blog & click New Entry");
		ui.clickLinkWithJavascript(BlogsUI.getNewEntryBtnForBlog(blog));

		log.info("INFO: Verify Tiny Editor functionality");
		String ExtectedValue = blog.verifyTinyEditor(ui,testUser1).trim();
		String ActualValue = ui.getBlogEntryDescText().trim();
		Assert.assertEquals(ActualValue, ExtectedValue);
		
		String ediDesc = "Edited Description of Entry in Blog" + testName + rand;
		String value = ui.editDescriptionInTinyEditor(blog, ediDesc);
		Assert.assertEquals(value, ediDesc);
		log.info("INFO: Delete the blog");
		blog.delete(ui);

		ui.endTest();

	}
	
	/**
	 * <ul>
	* <li><B>Info:</B>Validate tiny editor features on creating New Entry in Blog</li>
	 * <li><B>Step:</B>Create a Blogs with API.</li>
	 * <li><B>Step:</B>Navigate to the  same Blog </li>
	 * <li><B>Step:</B>Click on New Entry Button for the Created Blog</li>
	 * <li><B>Step:</B>Enter the Name of the Entry</li>
	 * <li><B>Step:</B>Upload Image in Tiny Editor from Local Disk</li>
	 * <li><B>Step:</B>Post the Entry</li>
	 * <li><B>Step:</B>Click on New Entry Button for the same Blog</li>
	 * <li><B>Step:</B>Enter the Name of the Entry</li>
	 * <li><B>Verify:</B>Verify Previously uploaded Image is Displayed in 'Upload Existing Image' Section and select.</li>
	 * <li><B>Step:</B>Post the Entry</li>
	 * <li><B>Verify:</B>Verify tiny Editor Description of Entry is successfully Edited in Blog Page</li>
	 * <li><B>Step:</B>Delete Blog</li>
	 * </ul>
	 */
	@Test(groups = { "TinyEditor" })
	public void verifyTinyEditorExistingImageFunctionalityInBlog() throws Exception {
		String testName = ui.startTest();
		String rand = Helper.genDateBasedRand();

		BaseBlog blog = new BaseBlog.Builder(testName + rand, testName + rand).tags(Data.getData().commonTag + rand)
				.description("Insert Image: Dialog default UI").theme(Theme.Blog_with_Bookmarks)
				.tinyEditorFunctionalitytoRun("verifyUploadImageFromDiskInTinyEditor")
				.build();

		log.info("INFO: Create blog using API");
		blog.createAPI(apiOwner);

		log.info("INFO: Log into Blogs as " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentBlogs);
		ui.login(testUser1);

		ui.waitForPageLoaded(driver);
		log.info("INFO: Navigate to My Blogs");
		ui.clickLinkWithJavascript(BlogsUIConstants.MyBlogs);

		ui.fluentWaitPresent(BlogsUI.getNewEntryBtnForBlog(blog));
		log.info("INFO: Navigate to the blog & click New Entry");
		ui.clickLinkWithJavascript(BlogsUI.getNewEntryBtnForBlog(blog));

		log.info("INFO: Verify Tiny Editor functionality");
		String ExtectedValue = blog.verifyTinyEditor(ui,testUser1).trim();

		String ActualValue = ui.getBlogEntryDescText().trim();

		Assert.assertEquals(ActualValue, ExtectedValue);
		
		blog = new BaseBlog.Builder(testName + rand, testName + rand).tags(Data.getData().commonTag + rand)
				.description("Insert Image: Dialog default UI").theme(Theme.Blog_with_Bookmarks)
				.tinyEditorFunctionalitytoRun("verifyExistingImagekInTinyEditor")
				.build();
		
		ui.clickLinkWait(BlogsUIConstants.BlogsNewEntryButton.replaceAll("PLACEHOLDER", blog.getName()));
		
		log.info("INFO: Verify Tiny Editor functionality");
		blog.verifyTinyEditor(ui,testUser1).trim();


		log.info("INFO: Delete the blog");
		blog.delete(ui);

		ui.endTest();
	}
	
}
