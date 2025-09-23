package com.ibm.conn.auto.webui;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.atmn.waffle.core.TestConfiguration;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseBlog;
import com.ibm.conn.auto.appobjects.base.BaseBlog.blogFields;
import com.ibm.conn.auto.appobjects.base.BaseBlogComment;
import com.ibm.conn.auto.appobjects.base.BaseBlogPost;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.webui.cloud.BlogsUICloud;
import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import com.ibm.conn.auto.webui.constants.BlogsUIConstants;
import com.ibm.conn.auto.webui.multi.BlogsUIMulti;
import com.ibm.conn.auto.webui.onprem.BlogsUIOnPrem;
import com.ibm.conn.auto.webui.production.BlogsUIProduction;

public abstract class BlogsUI extends ICBaseUI {

	public BlogsUI(RCLocationExecutor driver) {
		super(driver);
	}
	
	private static Logger log = LoggerFactory.getLogger(BlogsUI.class);

	public enum EditVia {
		MANAGEBLOG,
		WIDGETMENU;	
	}

	/**
	 * duplicateEntryTitle -
	 * @param newIdea
	 * @return
	 */
	public static String duplicateEntryTitle(BaseBlogPost newIdea){
		return "css=div#duplicatedEntryInfo h4:contains('" + newIdea.getTitle()+ "')";
	}
	
	/**
	 *  
	 * @param newIdea
	 * @return
	 */
	public static String duplicateEntryTag(BaseBlogPost newIdea){
		return "css=div#duplicatedEntryInfo a[title='" + newIdea.getTags().toLowerCase() +"']";
	}
	
	/**
	 * getFile -
	 * @return 
	 */
	public static String getFile(String fileName){
		return "css=td a:contains(" + fileName + ")";
	}
	
	/**
	 * getIcon -
	 * @param iconName
	 * @return
	 */
	public static String getIcon(String iconName){
		return "css=td img[class='" + iconName + "']";
	}
	
	
	/**
	 * getBlogMember - 
	 * @param user
	 * @return
	 */
	public static String getBlogMember(User user){
		return "css=a[title='" + user.getDisplayName() + "']";
	}
	
	/**
	 * getEntryContainer - Returns the container element for a Blog Entry
	 * @param entryTitle - Title of the Blog Entry
	 * @return xpath of the container of the Blog Entry
	 */
	public static String getEntryContainer(String entryTitle){
		return "xpath=//a[@name='" + entryTitle + "']//ancestor::td[@class='entryContentContainerTD blogsWrapText']";
	}
	
	/**
	 * getEntryCommentContainer - Returns the container element for the comment on a Blog Entry
	 * @param blogComment - Title of the comment
	 * @return xpath of the container for the comment
	 */
	public static String getEntryCommentContainer(String blogComment){
		return "xpath=//p[@dojoattachpoint='commentContentAP']/span[text()='" + blogComment + "']//ancestor::div[@dojoattachpoint='commentAP']";
	}

	/**
	 * getBlog -
	 * @param blog
	 * @return
	 */
	public static String getBlog(BaseBlog blog){
		return "css=a:contains(" + blog.getName() + ")";
	}
	
	/**
	 * getBlogPost
	 * @param blogPost
	 * @return
	 */
	public static String getBlogPost(BaseBlogPost blogPost){
		// make sure not to match posts in "featured blog entries"
		return "css=div.lotusContent a:contains(" + blogPost.getTitle() + ")";
	}
	
	/**
	 * newEntryBtnForBlog - Return the corresponding blog new entry button
	*/
	public static String getNewEntryBtnForBlog(BaseBlog blog) {
		return "css=a[href*=\"weblog=" + blog.getBlogAddress() + "\"]:contains(New Entry)";
	}
	
	/**
	 * 
	 * @param blog
	 * @return
	 */
	public static String getBlogDescription(BaseBlog blog) {
		return "css=div[class='blogsWrapText bidiAware']:contains(" + blog.getDescription() + ")";
	}
	
	/**
	 * 
	 * @param blog
	 * @return
	 */
	public static String newEntryForSpecificBlog(BaseBlog blog) {
		return "css=a[href*=\"weblog=" + blog.getBlogAddress() + "\"]:contains(New Entry)";
	}
	
	/**
	 * newEntryAnnouncements - 
	 * @param blogPost
	 * @return
	 */
	public static String newEntryAnnouncements(BaseBlogPost blogPost) {
		return "css=table[id='announcementBox'] a:contains(" + blogPost.getTitle() + ")";
	}
	

	
	/**
	 * createBlogComment
	 * @param comment
	 */
	public void createBlogComment(BaseBlogComment comment){

		//Click on the Add a comment link for entry
		log.info("INFO: Select the Add a comment link for entry");
		clickLinkWait(BlogsUIConstants.BlogsAddACommentLink);
		
		log.info("INFO: Verify Comment Headline");
		Assert.assertEquals(driver.getSingleElement(BlogsUIConstants.commentHeadline).getText(), "Comment on this Entry");

		//Fill in the comment form
		log.info("INFO: Fill in the comment form");
		typeCommentForm(comment.getContent());
		
		log.info("INFO: Verify 'Cancel' button");
		Assert.assertTrue(isElementPresent(BlogsUIConstants.commentCancelBtn));

		//Submit comment
		log.info("INFO: Submit the comment");
		Assert.assertTrue(isElementPresent(BlogsUIConstants.BlogsCommentSubmit));
		clickLinkWait(BlogsUIConstants.BlogsCommentSubmit);
		
	}
	
	/**
	 * editBlogComment
	 * @param comment
	 * @param commentNumber 
	 */
	public void editBlogComment(BaseBlogComment comment, int commentNumber){

		//Click on More Action link and select Edit comment option
		log.info("INFO: Click on More Action link and select Edit comment option");
		clickLinkWithJavascript(getMoreActionForComment(commentNumber-1));
		clickLinkWait(BlogsUIConstants.BlogsCommentEdit);
		
		waitForCkEditorReady();
		WebDriver wd = (WebDriver) driver.getBackingObject();
		
		//Clear contents and edit the comment form
		log.info("INFO: Fill in the comment form");
		switchToFrameBySelector(BaseUIConstants.ckEditorFrame);
		wd.findElement(By.cssSelector(BlogsUIConstants.BlogsCommentEditContent)).clear();
		wd.findElement(By.cssSelector(BlogsUIConstants.BlogsCommentEditContent)).sendKeys(comment.getContent());;
		switchToTopFrame();
		
		//Submit comment
		log.info("INFO: Submit the comment");
		Assert.assertTrue(isElementPresent(BlogsUIConstants.BlogsCommentSubmit));
		clickLinkWait(BlogsUIConstants.BlogsCommentSubmit);
		
	}
	
	/**
	 *This method takes comment number as input and
	 *returns morea ction link to that comment
	 * @param int commentNumber
	 */
	public String getMoreActionForComment(int commentNumber) {
		return "css=div[widgetid='lconn_blogs_comment_Comment_"+commentNumber+"'] a[id*='moreActions-']";
	}
	
	/**
	 * createBlogReply
	 * @param reply
	 */
	public void createBlogReply(BaseBlogComment reply){

		log.info("INFO: Select the Reply link for the entry");
		clickLinkWait(BlogsUIConstants.ReplyLink);

		log.info("INFO: Fill in the reply form");
		typeCommentForm(reply.getContent());

		log.info("INFO: Submit the comment");
		clickLinkWait(BlogsUIConstants.BlogsCommentSubmit);
		
	}
	
	public void createBlogCommentAndAddAsNewEntry(BaseBlogComment comment){

		//Click on the Add a comment link for entry
		log.info("INFO: Select the Add a comment link for entry");
		clickLinkWait(BlogsUIConstants.BlogsAddACommentLink);

		//Fill in the comment form
		log.info("INFO: Fill in the comment form");
		typeCommentForm(comment.getContent());
		
		//Tick new entry/idea in my blog checkbox
		log.info("INFO: Tick new entry/blog checkbox");
		clickLinkWait(BlogsUIConstants.BlogCommentTrackbackCheckBox);
		
		//Submit comment
		log.info("INFO: Submit the comment");
		clickLinkWait(BlogsUIConstants.BlogsCommentSubmit);
		
	}
	
	/** 
	 * Create a blog entry
	 * @param blogName - widget to load 
	 * @param blogEntryName
	 * @author Conor Pelly 
	 */
	public void createBlogEntry(BaseBlogPost blogPost, boolean useUnifyInsertImage) {

		//Title
		clickLink(BlogsUIConstants.BlogsNewEntryTitle);
		typeText(BlogsUIConstants.BlogsNewEntryTitle, blogPost.getTitle());

		//Check if post has tag and add it if so
		if (!blogPost.getTags().isEmpty()){
			log.info("INFO: Adding Post Tag");
			clickLink(BlogsUIConstants.BlogsNewEntryAddTags);
			typeTextWithDelay(BlogsUIConstants.BlogsNewEntryAddTagsTextfield, blogPost.getTags());
			clickLink(BlogsUIConstants.BlogsNewEntryAddTagsOK);
		}
		
		//Check if post has Content and add it if so
		if (!blogPost.getContent().isEmpty()){
			log.info("INFO: Adding Post Content");
			typeNativeInCkEditor(blogPost.getContent());
		}
	
		//Check if post needs Advanced options 
		if (blogPost.getAdvanced()){
			log.info("INFO: Opening Advanced Post options");
			clickLink(BlogsUIConstants.BlogsNewEntryAdvancedSettings);
		
			if(blogPost.getDelay()){
				log.info("INFO: Selecting that this blog broadcast be delayed");
				//Check post as announcement checkbox
				clickLink(BlogsUIConstants.BlogsNewEntryAdvanDelayPub);
			}
			
			if(blogPost.getEnableEmoticons()){
				log.info("INFO: Enabling emoticons");
				//Enable emoticons
				clickLink(BlogsUIConstants.BlogsNewEntryAdvanEmoticons);
			}
					
			if(blogPost.getAnnouncement()){
				log.info("INFO: Selecting that this blog be broadcast as an announcement");
				//Check post as announcement checkbox
				clickLink(BlogsUIConstants.BlogsNewEntryAdvanPinnedToMain);

			}
			
		}

		//Check if post has an image and add it if so
		if (!blogPost.getImage().isEmpty()){
			log.info("INFO: Adding Post image file name " + blogPost.getImage());
			fluentWaitPresent(BlogsUIConstants.BlogsCKEInsertImageButton);
			clickLinkWithJavascript(BlogsUIConstants.BlogsCKEInsertImageButton);
			
			if (useUnifyInsertImage) {
				log.info("INFO: Use unify insert image dialog.");
				fluentWaitPresent(BlogsUIConstants.existingImageTabLink);
				clickLinkWithJavascript(BlogsUIConstants.existingImageTabLink);
				
				fluentWaitPresent(BlogsUIConstants.BlogsCKEUnifyChoosePhoto);
				clickLinkWithJavascript(BlogsUIConstants.BlogsCKEUnifyChoosePhoto);
				
				try{
					log.info("INFO: Attempting to Insert uploaded image " + blogPost.getImage());
					fluentWaitPresent(BlogsUIConstants.BlogsCKEUnifyInsertButton);
					clickLinkWithJavascript(BlogsUIConstants.BlogsCKEUnifyInsertButton);
				}
				catch (Exception e){
					log.warn("WARNING: Unable to use insert button attempting with Java script");
					log.info("INFO: will use java script to click on insert button");
					driver.executeScript(BlogsUIConstants.BlogsCKEUnifyInsertButton);
				}
				
			} else {
				fluentWaitPresent(BlogsUIConstants.BlogsCKEInsertFromRecent);
				clickLinkWithJavascript(BlogsUIConstants.BlogsCKEInsertFromRecent);
				
				fluentWaitPresent(BlogsUIConstants.BlogsCKEChoosePhoto);
				clickLinkWithJavascript(BlogsUIConstants.BlogsCKEChoosePhoto);
				
				try{
					log.info("INFO: Attempting to Insert uploaded image " + blogPost.getImage());
					fluentWaitPresent(BlogsUIConstants.BlogsCKEInsertButton);
					clickLinkWithJavascript(BlogsUIConstants.BlogsCKEInsertButton);
				}
				catch (Exception e){
					log.warn("WARNING: Unable to use insert button attempting with Java script");
					log.info("INFO: will use java script to click on insert button");
					driver.executeScript(BlogsUIConstants.BlogsCKEInsertButtonJS);
				}
			}
		}
			
		//Save the entry unless user requests it incomplete state
		if(blogPost.getComplete()){
			log.info("INFO: Posting Entry");
			fluentWaitPresent(BlogsUIConstants.BlogsNewEntryPost);
			clickLinkWithJavascript(BlogsUIConstants.BlogsNewEntryPost);
		}
		
		//check a second time to prevent transition timing issue with saving image
		if(!driver.isTextPresent(blogPost.getTitle())&& blogPost.getComplete()){
			log.warn("WARNING: Potential transition issue attempting a second time to Post Entry");
			fluentWaitPresent(BlogsUIConstants.BlogsNewEntryPost);
			clickLinkWithJavascript(BlogsUIConstants.BlogsNewEntryPost);
			
		}

	}
	
	/** 
	 * New a blog entry WITHOUT SAVE, please call saveBlogEntry to post and save
	 * @param blogPost
	 * @param unifyInsertImageDlg: Whether GK unify-insert-image-dialog is enabled
	 * @author Yan Peng GUO 
	 */
	public void newBlogEntry(BaseBlogPost blogPost, boolean unifyInsertImageDlg) {
		log.info("INFO: createBlogEntry");

		//Title
		clickLink(BlogsUIConstants.BlogsNewEntryTitle);
		typeText(BlogsUIConstants.BlogsNewEntryTitle, blogPost.getTitle());

		//Check if post has tag and add it if so
		if (!blogPost.getTags().isEmpty()) {
			log.info("INFO: Adding Post Tag");
			clickLink(BlogsUIConstants.BlogsNewEntryAddTags);
			typeTextWithDelay(BlogsUIConstants.BlogsNewEntryAddTagsTextfield, blogPost.getTags());
			clickLink(BlogsUIConstants.BlogsNewEntryAddTagsOK);
		}
		
		//Check if post has Content and add it if so
		if (!blogPost.getContent().isEmpty()) {
			log.info("INFO: Adding Post Content");
			typeNativeInCkEditor(blogPost.getContent());
		}
	
		//Check if post needs Advanced options 
		if (blogPost.getAdvanced()) {
			log.info("INFO: Opening Advanced Post options");
			clickLink(BlogsUIConstants.BlogsNewEntryAdvancedSettings);
		
			if (blogPost.getDelay()) {
				log.info("INFO: Selecting that this blog broadcast be delayed");
				//Check post as announcement checkbox
				clickLink(BlogsUIConstants.BlogsNewEntryAdvanDelayPub);
			}
			
			if (blogPost.getEnableEmoticons()) {
				log.info("INFO: Enabling emoticons");
				//Enable emoticons
				clickLink(BlogsUIConstants.BlogsNewEntryAdvanEmoticons);
			}
					
			if (blogPost.getAnnouncement()) {
				log.info("INFO: Selecting that this blog be broadcast as an announcement");
				//Check post as announcement checkbox
				clickLink(BlogsUIConstants.BlogsNewEntryAdvanPinnedToMain);
			}
		}

		//Check if post has an image and add it if so only when unified insert image dialog is not enabled
		if (unifyInsertImageDlg) {
			log.info("INFO: Verify that Insert Image Dialog is opened");
			fluentWaitPresent(BlogsUIConstants.BlogsCKEInsertImageButton);
			clickLinkWithJavascript(BlogsUIConstants.BlogsCKEInsertImageButton);

			//Click insert image button to open the insert image dialog
			log.info("INFO: Verify that Insert Image Dialog is opened");
			fluentWaitPresent(BlogsUIConstants.BlogsCKEImageDlgLocalFilesTab);
			
			//Just cancel the dialog
			clickLinkWithJavascript(BlogsUIConstants.BlogsCKECancelButton);
		}
	}
	
	/** 
	 * post a blog entry
	 * @param blogPost
	 * @author Yan Peng GUO 
	 */
	public void postBlogEntry(BaseBlogPost blogPost) {
		//Save the entry unless user requests it incomplete state
		if (blogPost.getComplete()) {
			log.info("INFO: Posting Entry");
			fluentWaitPresent(BlogsUIConstants.BlogsNewEntryPost);
			clickLinkWithJavascript(BlogsUIConstants.BlogsNewEntryPost);
		}
		
		//check a second time to prevent transition timing issue with saving image
		if (!driver.isTextPresent(blogPost.getTitle())&& blogPost.getComplete()) {
			log.warn("WARNING: Potential transition issue attempting a second time to Post Entry");
			fluentWaitPresent(BlogsUIConstants.BlogsNewEntryPost);
			clickLinkWithJavascript(BlogsUIConstants.BlogsNewEntryPost);
		}
	}

	/**
	 * createBlogEntryWithMentions - Creates a blog entry with a mentions to a user
	 * @param blogPost The blog entry
	 * @param userName The name of the user who is to be mentioned
	 */
	public void createBlogEntryWithMentions(BaseBlogPost blogPost, String userName) {

		//Title
		clickLink(BlogsUIConstants.BlogsNewEntryTitle);
		typeText(BlogsUIConstants.BlogsNewEntryTitle, blogPost.getTitle());

		//Check if post has tag and add it if so
		if (!blogPost.getTags().isEmpty()){
			log.info("INFO: Adding Post Tag");
			clickLink(BlogsUIConstants.BlogsNewEntryAddTags);
			typeText(BlogsUIConstants.BlogsNewEntryAddTagsTextfield, blogPost.getTags());
			clickLink(BlogsUIConstants.BlogsNewEntryAddTagsOK);
		}
		
		//Check if post has Content and add it if so
		if (!blogPost.getContent().isEmpty()){
			log.info("INFO: Adding Post Content");
			typeNativeInCkEditor(blogPost.getContent() + " @" + userName);
			//focus on the typeahead
			driver.getSingleElement(HomepageUIConstants.typeAheadBox).hover();
			
			//click on the appropriate user
			driver.getSingleElement(HomepageUIConstants.typeAheadBox + HomepageUI.selectUserFromTypeAhead(userName)).click();
			
		}
	
		//Check if post needs Advanced options 
		if (blogPost.getAdvanced()){
			log.info("INFO: Opening Advanced Post options");
			clickLink(BlogsUIConstants.BlogsNewEntryAdvancedSettings);
		
			if(blogPost.getDelay()){
				log.info("INFO: Selecting that this blog broadcast be delayed");
				//Check post as announcement checkbox
				clickLink(BlogsUIConstants.BlogsNewEntryAdvanDelayPub);
			}
			
			if(blogPost.getEnableEmoticons()){
				log.info("INFO: Enabling emoticons");
				//Enable emoticons
				clickLink(BlogsUIConstants.BlogsNewEntryAdvanEmoticons);
			}
					
			if(blogPost.getAnnouncement()){
				log.info("INFO: Selecting that this blog be broadcast as an announcement");
				//Check post as announcement checkbox
				clickLink(BlogsUIConstants.BlogsNewEntryAdvanPinnedToMain);

			}
			
		}

		//Check if post has an image and add it if so
		if (!blogPost.getImage().isEmpty()){
			log.info("INFO: Adding Post image file name " + blogPost.getImage());
			fluentWaitPresent(BlogsUIConstants.BlogsCKEInsertImageButton);
			driver.getSingleElement(BlogsUIConstants.BlogsCKEInsertImageButton).click();

			fluentWaitPresent(BlogsUIConstants.BlogsCKEInsertFromRecent);
			driver.getSingleElement(BlogsUIConstants.BlogsCKEInsertFromRecent).doubleClick();
			
			fluentWaitPresent(BlogsUIConstants.BlogsCKEChoosePhoto);
			driver.getSingleElement(BlogsUIConstants.BlogsCKEChoosePhoto).doubleClick();
			
			try{
				log.info("INFO: Attempting to Insert uploaded image " + blogPost.getImage());
				fluentWaitPresent(BlogsUIConstants.BlogsCKEInsertButton);
				driver.getSingleElement(BlogsUIConstants.BlogsCKEInsertButton).doubleClick();
			}
			catch (Exception e){
				log.warn("WARNING: Unable to use insert button attempting with Java script");
				log.info("INFO: will use java script to click on insert button");
				driver.executeScript(BlogsUIConstants.BlogsCKEInsertButtonJS);
			}

		}
			
		//Save the entry unless user requests it incomplete state
		if(blogPost.getComplete()){
			log.info("INFO: Posting Entry");
			fluentWaitPresent(BlogsUIConstants.BlogsNewEntryPost);
			driver.getSingleElement(BlogsUIConstants.BlogsNewEntryPost).click();
		}
		
		//check a second time to prevent transition timing issue with saving image
		if(!driver.isTextPresent(blogPost.getTitle())&& blogPost.getComplete()){
			log.warn("WARNING: Potential transition issue attempting a second time to Post Entry");
			fluentWaitPresent(BlogsUIConstants.BlogsNewEntryPost);
			driver.getSingleElement(BlogsUIConstants.BlogsNewEntryPost).click();
			
		}
		
	}

	/** 
	 * Upload a file(s) Max of Five files can be loaded at a time
	 * @param FilesToUpload - List of the names of the file to uploaded
	 * @author Conor Pelly
	 */
	public void blogsAddFileToUpload(List<String> FilesToUpload) {

		String BlogsFileUploadsField;

		//Assert that the list only contains a max of five files
		Assert.assertTrue(FilesToUpload.size()<5, 
						   "ERROR: Blogs may only add five a max of five files at a timeS");

		//Open the File Upload Link
		clickLink(BlogsUIConstants.BlogsFileUploadsLink);
		
		fluentWaitPresent("css=input[id='uploadFile0']");

		FilesUI fUI = FilesUI.getGui(cfg.getProductName(), driver);
		fUI.setLocalFileDetector();
		for(int i = 0; i < FilesToUpload.size(); i++) {
			String fileFullPath = FilesUI.getFileUploadPath(FilesToUpload.get(i), cfg);
			File file = new File(fileFullPath);
			
			TestConfiguration testConfig = cfg.getTestConfig();
			if (testConfig.getServerHost().contains("localhost") && !testConfig.serverIsBrowserStack()) {
				log.info("INFO: It's running locally.");
				Assert.assertTrue(file.exists(), "The file '" + fileFullPath + "' does not exist! Please check your configuration and retry!");
			} else {
				log.info("INFO: It's running in a grid or BrowserStack, so assume the grid machine has the test resource file.");
			}
			
		    // Create CSS to locate the correct button
			BlogsFileUploadsField = "css=input[id='uploadFile" + i + "']";
			driver.getSingleElement(BlogsFileUploadsField).typeFilePath(fileFullPath);
		}
		
		//save
		clickLink(BlogsUIConstants.BlogsFileUploadButton);
		fluentWaitTextPresent("Uploaded file(s)");

	}
	
	public void gotoNewEntry() {
		clickLink(BlogsUIConstants.BlogsNewEntry);
	}
	
	public void gotoNewIdea() {
		clickLink(BlogsUIConstants.NewIdea);
	}

	/**
	 * create a blog
	 * @param commUI
	 * @param blog
	 * @param community
	 * @throws Exception
	 */
	public void create(BaseBlog blog){
		//Click on the My Blogs tab
		log.info("INFO: Select My Blogs tab");
		if(cfg.getUseNewUI()) {
			clickLinkWait(BlogsUIConstants.myBlogsTab);
		}else {
			clickLinkWait(BlogsUIConstants.MyBlogs);
		}

		//Click on the Start a Blog button
		log.info("INFO: Select Start a blog button");
		clickLinkWait(BlogsUIConstants.StartABlog);

		//fill in the name of the blog
		log.info("INFO: Enter name of blog");
		fluentWaitPresent(BlogsUIConstants.BlogsNameField);
		typeText(BlogsUIConstants.BlogsNameField, blog.getName());
		
		//fill in the blog address
		log.info("INFO: Enter blog address");
		typeText(BlogsUIConstants.BlogsAddressObject, blog.getBlogAddress());
		
		//If blogObj has a tag add it
		if (blog.getTags() != null){
			log.info("INFO: Add blog tags");
			typeText(BlogsUIConstants.BlogsTags, blog.getTags());
		}		
		
		//If blogObj has a description add it
		if (blog.getDescription() != null){
			log.info("INFO: add blog description");
			typeText(BlogsUIConstants.BlogsDescription, blog.getDescription());
		}
		
		//select blog time zone default is "(GMT-05:00) Bogota, Lima, Quito"
		log.info("INFO: Select the Blog time zone");
		driver.getSingleElement(BlogsUIConstants.BlogsTimeZone).useAsDropdown().selectOptionByVisibleText(blog.getTimeZone().toString());
		
		//select blog theme	default is blog	
		log.info("INFO: Select the blog theme");
		if(!cfg.getUseNewUI()) {
			driver.getSingleElement(BlogsUIConstants.BlogsTheme).useAsDropdown().selectOptionByVisibleText(blog.getTheme().toString());
		}

		// Save the form
		log.info("INFO: Select Save button");
		driver.executeScript("arguments[0].scrollIntoView(true);",
				driver.getElements(BaseUIConstants.SaveButton).get(0).getWebElement());
		driver.getSingleElement(BlogsUIConstants.SaveButton).click();

	}
	
	
	
	public void delete(BaseBlog blog){
		
		log.info("INFO: Select manage blog link");
		clickLinkWait(BlogsUIConstants.blogManage);
		
		log.info("INFO: Select delete blog button");
		scrollIntoViewElement(BlogsUIConstants.blogDelete);
		clickLinkWait(BlogsUIConstants.blogDelete);
		
		log.info("INFO: Confirm delete blog check box");
		//clickLinkWait(blogDeleteConfirmChkBox);
		this.clickLinkWithJavascript(BlogsUIConstants.blogDeleteConfirmChkBox);
		
		log.info("INFO: Select the Delete button");
		clickLinkWait(BlogsUIConstants.blogDeleteConfirm);
		
	}
	
	public void edit(BaseBlog blog, EditVia editType){

		//Iterate through list of edits
		log.info("INFO: Iterate through the changes");
		Iterator<blogFields> iterator = blog.getEdits().iterator();
        while(iterator.hasNext()){

            switch (iterator.next()) {
 
            case NAME:
            	 log.info("INFO: Change name of Blog");
            	 fluentWaitPresent(BlogsUIConstants.BlogsNameField);
        		 clearText(BlogsUIConstants.BlogsNameField);
        		 typeText(BlogsUIConstants.BlogsNameField, blog.getName());
                 break;

            case TAGS:
    			 log.info("INFO: Add tags to text field");
    			 clearText(BlogsUIConstants.BlogsTags);
    			 typeText(BlogsUIConstants.BlogsTags, blog.getTags());
            	 break;
            	
            case DESCRIPTION:
    			 log.info("INFO: Clearing old blog description.");
    			 clearText(BlogsUIConstants.BlogsDescription);
    			 log.info("INFO: Entering new blog description.");
    			 typeText(BlogsUIConstants.BlogsDescription, blog.getDescription());
            	 break;

            case TIMEZONE:
        		 log.info("INFO: Select the new Blog time zone");
        		 driver.getSingleElement(BlogsUIConstants.BlogsTimeZone).useAsDropdown().selectOptionByVisibleText(blog.getTimeZone().toString());
            	 break;
            
            case ISACTIVE:
            	 log.info("INFO: Change blog active to " + blog.getIsActive());
            	 clickLinkWait("css=input[id='active']");
            	 break;

            case USE_EMOTICONS:
           	     log.info("INFO: Change use emoticons to " + blog.getIsActive());
        	     clickLinkWait("css=input[id='Emoticons']");
            	 break;
            
            case ALLOWCOMMENTS:
          	     log.info("INFO: Change allow comments to " + blog.getIsActive());
        	     clickLinkWait("css=input[id='allowComments']");
            	 break;
            	 
            case MODERATECOMMENTS:
         	     log.info("INFO: Change moderate comments to " + blog.getIsActive());
        	     clickLinkWait("css=input[id='moderateComments']");
            	 break;
            
            case COMMENTSTIME:
            	 log.info("INFO: Change Comment time to " + blog.getCommentsTime().commentDays);
            	 driver.getSingleElement("css=select[id='defaultCommentDays']").useAsDropdown().selectOptionByVisibleText(blog.getCommentsTime().commentDays);
            	 break;
            	 
            case APPLYEXISTENTRY:
            	 log.info("INFO: Change Apply to existing entries (this time only)");
         		 clickLinkWait(BlogsUIConstants.BlogsSettingsApplyCommentDefaultsToExisting);
         		 break;
         		 
            case EDITOTHERCOMMENTS:
            	 log.info("INFO: Change allow each other to edit comments");
         		 clickLinkWait("css=input[id='allowCoEdit'][type='checkbox']");
         		 break;
         	
            case COMMEMBERROLE:
            	 log.info("INFO: Change community member role");
            	 clickLinkWait("css=input[id='" + blog.getComMemberRole().id + "']");
            	 //sometimes this goes to fast added check here to ensure the roles is selected before moving on
            	 if (!driver.getSingleElement("css=input[id='" + blog.getComMemberRole().id + "']").isSelected())
            		 clickLinkWait("css=input[id='" + blog.getComMemberRole().id + "']");
            	 break;
         		 
            default: break;
            }

        }

		log.info("INFO: Save changes");
		if(editType == EditVia.MANAGEBLOG){
			fluentWaitElementVisible(BlogsUIConstants.blogsUpdateSettings);
			clickLinkWait(BlogsUIConstants.blogsUpdateSettings);
		}else if(editType == EditVia.WIDGETMENU){
			clickLinkWait("css=input[name='save']");
		}
			
	}

	/**
	 * addMember - add a new member to the blog
	 * @param member
	 */
	public void addMember(Member member){

		// set and author and a drafter for newly created blog
		log.info("INFO: Click on Add members link");
		clickLinkWait("css=a:contains(Add members)");
		
		//select role
		log.info("INFO: Select role");
		driver.getSingleElement("css=select[dojoattachpoint='aclLevel_AP']").useAsDropdown().selectOptionByVisibleText(member.getRole().toString());

		//type in user
		log.info("INFO: Type user name");
		driver.getFirstElement("css=input[id^='lconn_core_PeopleTypeAhead']").typeWithDelay(member.getUser().getDisplayName());

		//search
		log.info("INFO: Select the search link");
		clickLinkWait("css=div[id='lconn_core_PeopleTypeAhead_0_popup_searchDir']");

		//select user from list
		log.info("Select the user from the list");
		clickLinkWait("css=div[id^='lconn_core_PeopleTypeAhead_'][role='option']:contains(" + member.getUser().getDisplayName()
								+ " <" + member.getUser().getEmail() + ">)");

		// save the changes
		log.info("INFO: Save the changes");
		clickLinkWait("css=input[class='lotusFormButton'][name='save']");
		
	}
	
	/** 
	 * Set a blog as the Primary
	 * @param BlogsName - blog to be set as primary
	 * 
	 * @author Conor Pelly
	 */
	public abstract void setBlogAsPrimary(String BlogsName);

	/**
	 * 
	 */
	protected abstract void selectBlogsDropdown();
	
	/** 
	 * Change the Admin settings and verify that they were saved
	 * 
	 * @author Conor Pelly
	 */
	public abstract void changeAdminSettings();

	
	/**
	 * 
	 * @param comment
	 */
	protected abstract void typeCommentForm(String comment);

	/**
	 * getCommIdeationBlogLink -
	 * @param community
	 * @return
	 */
	public abstract String getCommIdeationBlogLink(BaseCommunity community);

	
	
	public static BlogsUI getGui(String product, RCLocationExecutor driver){
		if(product.toLowerCase().equals("cloud")){
			return new  BlogsUICloud(driver);
		} else if(product.toLowerCase().equals("onprem")) {
			return new  BlogsUIOnPrem(driver);
		} else if(product.toLowerCase().equals("production")) {
			return new  BlogsUIProduction(driver);
		} else if(product.toLowerCase().equals("vmodel")) {
			return new  BlogsUIOnPrem(driver);
		}  else if(product.toLowerCase().equals("multi")) {
			return new  BlogsUIMulti(driver);
		} else {
			throw new RuntimeException("Unknown product name: " + product);
		}
	}

	/**
	 * Navigate to Created Blog , create new Entry and verify Tiny Editor functionality
	 * @param testUser1 used in verifyMentionUserInTinyEditor to parameterize user display name
	 * @param Base community object
	 * @return String Text present in Description of Tiny Editor.
	 */
	public String verifyTinyEditorInBlog(BaseBlog baseBlog, User testUser1) {
		TinyEditorUI tui = new TinyEditorUI(driver);
		
		String EntryTitle = "Entry"+baseBlog.getName();
		log.info("INFO: Entering Entry Title " + EntryTitle);
		this.waitForPageLoaded(driver);
		this.fluentWaitElementVisible(BlogsUIConstants.BlogsNewEntryTitle);
		this.waitForPageLoaded(driver);
		this.getFirstVisibleElement(BlogsUIConstants.BlogsNewEntryTitle).type(EntryTitle);
		//this.driver.getSingleElement(BlogsNewEntryTitle).type(EntryTitle);
		tui.clickOnMoreLink();
		
		

		log.info("INFO: Entering a description and validating the functionality of Tiny Editor");
		if (baseBlog.getDescription() != null) {

			String TE_Functionality[] = baseBlog.getTinyEditorFunctionalitytoRun().split(",");
			
			for (String functionality : TE_Functionality) {
				switch (functionality) {
				case "verifyParaInTinyEditor":
					log.info("INFO: Validate Paragragh and header functionality of Tiny Editor");
					tui.verifyParaInTinyEditor(baseBlog.getDescription());
					break;
				case "verifyAttributesInTinyEditor":
					log.info("INFO: Validate Attributes functionality of Tiny Editor");
					tui.verifyAttributesInTinyEditor(baseBlog.getDescription());
					break;
				case "verifyPermanentPenInTinyEditor":
					log.info("INFO: Validate Permanent Pen functionality of Tiny Editor");
					tui.verifyPermanentPenInTinyEditor(baseBlog.getDescription());
					break;
				case "verifyUndoRedoInTinyEditor":
					log.info("INFO: Validate Undo and Redo functionality of Tiny Editor");
					tui.verifyUndoRedoInTinyEditor(baseBlog.getDescription());
					break;
				case "verifyAlignmentInTinyEditor":
					log.info("INFO: Validate Alignment functionality of Tiny Editor");
					tui.verifyAlignmentInTinyEditor(baseBlog.getDescription());
					break;
				case "verifyIndentsInTinyEditor":
					log.info("INFO: Validate Indents functionality of Tiny Editor");
					tui.verifyIndentsInTinyEditor(baseBlog.getDescription());
					break;
				case "verifyBulletsAndNumbersInTinyEditor":
					log.info("INFO: Validate Bullets and Numbers functionality of Tiny Editor");
					tui.verifyBulletsAndNumbersInTinyEditor(baseBlog.getDescription());
					break;
				case "verifyHorizontalLineInTinyEditor":
					log.info("INFO: Validate Horizontal Line functionality of Tiny Editor");
					tui.verifyHorizontalLineInTinyEditor(baseBlog.getDescription());
					break;
				case "verifySpecialCharacterInTinyEditor":
					log.info("INFO: Validate Special character functionality of Tiny Editor");
					tui.verifySpecialSymbolsInTinyEditor("SpecialChar");
					break;
				case "verifyEmotionsInTinyEditor":
					log.info("INFO: Validate Emoticons functionality of Tiny Editor");
					tui.verifySpecialSymbolsInTinyEditor("Emotions");
					break;
				case "verifySpellCheckInTinyEditor":
					log.info("INFO: Validate Horizontal Line functionality of Tiny Editor");
					tui.verifySpellCheckInTinyEditor(baseBlog.getDescription());
					break;
				case "verifyRowsCoulmnOfTableInTinyEditor":
					log.info("INFO: Validate Rows and Columns of Table in Tiny Editor");
					tui.verifyRowsCoulmnOfTableInTinyEditor(baseBlog.getDescription());
					break;
				case "verifyFormatPainterInTinyEditor":
					log.info("INFO: Validate Format Painter in Tiny Editor");
					tui.verifyFormatPainterInTinyEditor(baseBlog.getDescription());
					break;
				case "verifyFontInTinyEditor":
					log.info("INFO: Validate font functionality of Tiny Editor");
					tui.verifyFontInTinyEditor(baseBlog.getDescription());
					break;
				case "verifyFontSizeInTinyEditor":
					log.info("INFO: Validate font Size functionality of Tiny Editor");
					tui.verifyFontSizeInTinyEditor(baseBlog.getDescription());
					break;
				case "verifyLinkImageInTinyEditor":
					log.info("INFO: Validate Link Image functionality of Tiny Editor");
					tui.verifyLinkImageInTinyEditor(baseBlog.getDescription());
					break;
				case "verifyRightLeftParagraphInTinyEditor":
					log.info("INFO: Validate Left to Right paragraph functionality of Tiny Editor");
					tui.verifyRightLeftParagraphInTinyEditor(baseBlog.getDescription());
					break;
				case "verifyOtherTextAttributesAndFullScreenInTinyEditor":
					log.info("INFO: Validate other text attributes functionality of Tiny Editor");
					tui.verifyOtherTextAttributesAndFullScreenInTinyEditor(baseBlog.getDescription());
					break;
				case "verifyFindReplaceInTinyEditor":
					log.info("INFO: Validate Find and Replace functionality of Tiny Editor");
					tui.verifyFindReplaceInTinyEditor(baseBlog.getDescription());
					break;
				case "verifyInsertLinkImageInTinyEditor":
					log.info("INFO: Validate Link Image functionality of Tiny Editor");
					tui.verifyInsertLinkImageInTinyEditor(EntryTitle);
					break;
				case "verifyTextColorInTinyEditor":
					log.info("INFO: Validate Font Text Color functionality of Tiny Editor");
					tui.verifyTextColorInTinyEditor(baseBlog.getDescription());
					break;
				case "verifyBackGroundColorInTinyEditor":
					log.info("INFO: Validate Font BackGround Color functionality of Tiny Editor");
					tui.verifyBackGroundColorInTinyEditor(baseBlog.getDescription());
					break;
				case "verifyWordCountInTinyEditor":
					log.info("INFO: Validate Word Count functionality of Tiny Editor");
					tui.verifyWordCountInTinyEditor(baseBlog.getDescription());
					break;
				case "verifyUploadImageFromDiskInTinyEditor":
					log.info("INFO: Validate Upload image from Disk functionality of Tiny Editor");
					tui.verifyUploadImageFromDiskInTinyEditor();
					break;
				case "verifyBlockQuoteInTinyEditor":
					log.info("INFO: Validate Block quote functionality of Tiny Editor");
					tui.verifyBlockQuoteInTinyEditor(baseBlog.getDescription());
					break;
				case "verifyInsertMediaInTinyEditor":
					log.info("INFO: Validate Insert Media functionality of Tiny Editor");
					tui.verifyInsertMediaInTinyEditor(baseBlog.getDescription());
					break;
				case "verifyLinkToConnectionsFilesInTinyEditor":
					log.info("INFO: Validate Link to connections files from files in Tiny Editor");
					tui.addLinkToConnectionsFilesInTinyEditor(baseBlog.getDescription());
					break;
				case "verifyCodeSampleIntinyEditor":
					log.info("INFO: Validate Code Sample functionality of Tiny Editor");
					tui.verifyCodeSampleIntinyEditor(baseBlog.getDescription());
					break;
				case "verifyInsertiFrameInTinyEditor":
					log.info("INFO: Validate Insert iFrame functionality of Tiny Editor");
					tui.verifyInsertiFrameInTinyEditor(baseBlog.getDescription());
					break;
				case "verifyExistingImagekInTinyEditor":
					log.info("INFO: Validate existing image functionality of Tiny Editor");
					tui.verifyExistingImagekInTinyEditor();
					break;	
				case "verifyMentionUserInTinyEditor":
					log.info("INFO: Validate Insert iFrame functionality of Tiny Editor");
					tui.verifyMentionUserInTinyEditor(baseBlog.getDescription(),testUser1.getDisplayName());
					break;
				case "verifyEditDescriptionInTinyEditor":
					log.info("INFO: Validate Insert iFrame functionality of Tiny Editor");
					tui.verifyDefaultCaseInTinyEditor(baseBlog.getDescription());
					break;	
				}
			}
		}

		String TEText = tui.getTextFromTinyEditor();
		log.info("INFO: Get the text from Tiny Editor body" + TEText);
		
		// Save the Blog Entry
		log.info("INFO: Saving the Entry " + EntryTitle);
		this.driver.getSingleElement(BlogsUIConstants.BlogsNewEntryPost).click();

		fluentWaitTextPresent(EntryTitle);

		return TEText;
	}
	
	public void verifyInsertedLink(String community)
    {
        TinyEditorUI tui = new TinyEditorUI(driver);
        tui.verifyInsertedLinkinDescription(community);
    }

	public String getBlogEntryDescText() {
		return this.getFirstVisibleElement(BlogsUIConstants.BlogsEntryDescDOM).getText();
	}
	
	public String editDescriptionInTinyEditor(BaseBlog Blogs, String ediDesc) 
	{
		TinyEditorUI tui = new TinyEditorUI(driver);
		String editedDesc;
		this.getFirstVisibleElement(BlogsUIConstants.BlogsEditEntry).click();
		tui.verifyDefaultCaseInTinyEditor(ediDesc);
		editedDesc = tui.getTextFromTinyEditor();
		log.info("INFO: Get the text from Tiny Editor body" + editedDesc);
		this.driver.getSingleElement(BlogsUIConstants.BlogsNewEntryPost).click();

		fluentWaitTextPresent(ediDesc);
		return editedDesc;
	}
	
	/**
	 * createIdeaComment: This method will create a comment in Blog idea
	 * @param comment
	 */
	public void createBlogIdeaComment(BaseBlogComment comment){

		//Click on the Add a comment link for entry
		log.info("INFO: Select the Add a comment link for entry");
		clickLinkWait(BlogsUIConstants.BlogsAddACommentLink);

		//Fill in the comment form
		log.info("INFO: Fill in the comment form");
		typeCommentForm(comment.getContent());
		
		//Submit comment
		log.info("INFO: Submit the comment");
		clickLinkWait(BlogsUIConstants.BlogsCommentSubmit);
		
	}
	
}
