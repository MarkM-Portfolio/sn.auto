package com.ibm.conn.auto.webui;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import com.ibm.conn.auto.webui.constants.WikisUIConstants;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseWiki;
import com.ibm.conn.auto.appobjects.base.BaseWiki.EditAccess;
import com.ibm.conn.auto.appobjects.base.BaseWiki.ReadAccess;
import com.ibm.conn.auto.appobjects.base.BaseWiki.wikiField;
import com.ibm.conn.auto.appobjects.base.BaseWikiPage;
import com.ibm.conn.auto.appobjects.base.BaseWikiPage.wikiPageField;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.WikiRole;
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
import com.ibm.conn.auto.util.menu.Community_TabbedNav_Menu;
import com.ibm.conn.auto.util.menu.Wiki_Action_Menu;
import com.ibm.conn.auto.util.menu.Wiki_Page_Menu;
import com.ibm.conn.auto.webui.cloud.WikisUICloud;
import com.ibm.conn.auto.webui.onprem.WikisUIOnPrem;

public abstract class WikisUI extends ICBaseUI {

	public WikisUI(RCLocationExecutor driver) {
		super(driver);
	}
	
	private static Logger log = LoggerFactory.getLogger(WikisUI.class);
		
	/**
	 * getWikiActionMenuID
	 * @param menuItem
	 * @return
	 */
	public String getWikiActionMenuID(String menuItem){
		return driver.getSingleElement(menuItem).getAttribute("id");
	}
	
	/**
	 * getWikiPageLeftMenu -
	 * @param page
	 * @return
	 */
	public static String getWikiPageLeftMenu(BaseWikiPage page){
		return "css=a[title='" + page.getName() + "']";
	}
	
	/**
	 * getWiki - 
	 * @param wiki
	 * @return
	 */
	public static String getWiki(BaseWiki wiki){
		return "css=a[title^='" + wiki.getName() + "']";
	}
	
	/**
	 * getWikiPageTitle - 
	 * @param page
	 * @return
	 */
	public static String getWikiPageTitle(BaseWikiPage page){
		return "css=h1[id='wikiPageHeader'][title='" + page.getName() + "']";
	}
	
	/**
	 * getWikiLink - 
	 * @param wiki
	 * @return
	 */
	public String getWikiLink(BaseWiki wiki){
		return "css=a[class='lotusLink']:contains(" + wiki.getName() + ")";
	}
	
	/**
	 * getWikiLink - 
	 * @param wiki
	 * @return
	 */
	public String getWikiLink(String wiki){
		return "css=a[class='lotusLink']:contains(" + wiki + ")";
	}
	
	
	/**
	 * getMemberUUID - 
	 * @param member
	 * @return
	 */
	public String getMemberUUID(Member member) {

		log.info("INFO: Find members UUID");
		Element user = driver.getSingleElement("css=a.vcard.fn.lotusPerson.bidiAware:contains(" + member.getUser().getDisplayName() + ")");
		String href = user.getAttribute("href");
		if (href == null || href.equals("javascript:;")) {
			href = user.getAttribute("href_bc_");
		}
		String userID[] = href.split("userid=");
		return userID[1];
	}
	
	
	/**
	 * sTagLinkinTagCloud - 
	 * @param sTag
	 * @return
	 */
	public String sTagLinkinTagCloud(String sTag){
		return "css=#lconnTagWidget_tagCloudView li a[title^='Show the search results of tag " + sTag + ", count']";
	}
	
	/**
	 * sRemoveTagLinkinMain -
	 * @param sTag
	 * @return
	 */
	public String sRemoveTagLinkinMain(String sTag){
		return "css=a:contains(Tagged with '" + sTag + "')";
	}
		
	/**
	 * getWikiPage -
	 * @param wikiPage
	 * @return
	 */
	public static String getWikiPage(BaseWikiPage wikiPage){
		return "css=span:contains(" + wikiPage.getName() + ")";
	}
	
	/**
	 * getTagElement - 
	 * @param tagName
	 * @return
	 */
	public static String getTagElement(String tagName){
		return "css=li[class='lotusTag'] a:contains(" + tagName + ")";
	}
	
	/**
	 * getPageSelector -
	 * @param page
	 * @return
	 */
	public String getPageSelector(BaseWikiPage page){
		return "xpath=//a[contains(text(),'"+ page.getName() +"')]";
	}
	
	/**
	 * getPageSelector from tree item-
	 * @param page
	 * @return
	 */
	public String getPageSelectorFromTree(BaseWikiPage page){
		return "xpath=//a[@role='treeitem' and contains(text(),'"+ page.getName() +"')]";
	}
	
	/**
	 * getPageSelectorInListView
	 * @param page
	 * @return
	 */
	public String getPageSelectorinListView(BaseWikiPage page){
		return WikisUIConstants.list + "a:contains("+ page.getName() +")";
	}
	
	/**
	 * getTagDeleteSelector
	 * @param tag
	 * @return
	 */
	public String getTagDeleteSelector(String tag){
		return "css=a img[title='Remove tag " + tag + "']";
	}

	/**
	* getWikiPageLockingMessage
	* @param lockingMessage
	* @return
	*/
	public static String getWikiPageLockingMessage(String lockingMessage){
		return "xpath=//span[contains(text(),'" + lockingMessage + "')]";
	}

	/**
	 * editWikiPage - 
	 * @param wikiPage
	 */
	public void editWikiPage(BaseWikiPage wikiPage){

		log.info("INFO: Select the Edit Button");
		waitForPageLoaded(driver); 
		clickLinkWait(WikisUIConstants.Edit_Button);

		//Iterate through list of edits
		log.info("INFO: Iterate through the changes");
       Iterator<wikiPageField> iterator = wikiPage.getEdits().iterator();
       waitForPageLoaded(driver);
       
        while(iterator.hasNext()){

            switch (iterator.next()) {
            case NAME:
            	//change name of page
            	log.info("INFO: Change name of wiki page");
            	fluentWaitPresent(WikisUIConstants.Page_Name_Textfield_In_Editor);
        		clearText(WikisUIConstants.Page_Name_Textfield_In_Editor);
        		typeText(WikisUIConstants.Page_Name_Textfield_In_Editor, wikiPage.getName());
                break;
                
            case TAG:
    			log.info("INFO: Click add tags link");
    			clickLinkWait(WikisUIConstants.wikiPageAddTagLink);

    			log.info("INFO: Add tags to text field");
    			fluentWaitPresent(WikisUIConstants.wikiPageAddTagText);
    			driver.getSingleElement(WikisUIConstants.wikiPageAddTagText).typeWithDelay(wikiPage.getTags());
    			
    			log.info("INFO: Click OK Button");
    			clickLinkWait(WikisUIConstants.OK_Button);
            	break;
            	
            case DESCRIPTION:
            	//change description
            	log.info("INFO: Change description in wiki page");
            	typeInCkEditor(wikiPage.getDescription());
            	break;
            	
            case PAGE_TYPE:
            	log.warn("WARNING: Page type can not be changed after page is made");
            	break;
            default: break;
            }

        }
        
        //Save the edit
        log.info("INFO: Save the edited wiki page");
        waitForPageLoaded(driver);
		scrollIntoViewElement(WikisUIConstants.Save_and_Close_Link);
		clickLinkWait(WikisUIConstants.Save_and_Close_Link);
               
	}	
	
	/**
	 * addComment - 
	 * @param CommentToBeAdded
	 */
	public void addComment(String CommentToBeAdded){
		
		//Open Comments Page
		log.info("INFO: Open Comments Page");
		clickLinkWait(WikisUIConstants.Comments_Tab);
		
		//Open the Comment Editor
		log.info("INFO: Select add comment link");
		clickLinkWait(WikisUIConstants.Add_Comment_Link);
		
		//Add a comment to the page
		log.info("INFO: Add comment to page");
		typeNativeInCkEditor(CommentToBeAdded);
		
		//save comment
		log.info("INFO: Save comment");
		clickLinkWait(BaseUIConstants.SaveButton);
		
		if (isTextPresent("Please enter your comment")) {
			log.info("INFO: Add comment to page again");
			typeNativeInCkEditor(CommentToBeAdded);
			
			//save comment
			log.info("INFO: Save comment");
			clickLinkWait(BaseUIConstants.SaveButton);
		}

	}

	/**
	 * collectComments -
	 * @return
	 */
	private List<Element> collectComments(){
		return driver.getVisibleElements("css=div[dojoattachpoint='streamNode'] div[class^='lotusPost lotusStatus lotusBoard']");
	}
	
	
	/**
	 * 
	 * @param before
	 * @param after
	 */
	public void editComment(String before, String after){

		//collect all of the comments
		Iterator<Element> comElement = collectComments().iterator();
		while(comElement.hasNext()){
		    Element comment = comElement.next();
		    if(comment.getText().contains(before)){
		    	log.info("INFO: Found Comment");
		    	WebElement allChildren = (WebElement)comment.getBackingObject();
		    	log.info("INFO: Selecting edit link");
		    	allChildren.findElement(By.cssSelector("li[class='lotusFirst'] a[role='button']")).click();
		    	log.info("INFO: Adding new comment");
		    	typeNativeInCkEditor(after);
		    	fluentWaitTextNotPresentWithoutRefresh(before);
		    	log.info("INFO: Select the Save Button");
		    	allChildren.findElement(By.cssSelector("input[value='Save']")).click();
		    	break;
		    }
		}
	}
	
	
	/**
	 * deleteComment - 
	 * @param sComment
	 */
	public void deleteComment(String sComment){
		
		//collect all of the comments
		Iterator<Element> comElement = collectComments().iterator();
		while (comElement.hasNext()) {
			Element comment = comElement.next();
			log.info("INFO: Check comment ");
			if(comment.getText().contains(sComment)){
				log.info("INFO: Found comment selecting delete");
				WebElement allChildren = (WebElement)comment.getBackingObject();
				log.info("INFO: Select delete link");
				List<WebElement> buttons = allChildren.findElements(By.cssSelector("a[role='button']"));
				boolean foundDelete = false;
				for(WebElement button : buttons) {
					if(button.getText().equalsIgnoreCase("delete")) {
						foundDelete = true;
						button.click();
						break;
					}
				}
				Assert.assertTrue(foundDelete, "ERROR: Could not find the link to delete a comment");
				log.info("INFO: Select OK button");
				clickButton("OK");
				break;
			}
		}
		
	}

	/**
	 * addWikiTag - 
	 * @param New_Tag
	 */
	public void addWikiTag(String New_Tag){
		log.info("INFO: add a tag to a wiki page");

		clickLinkWait(WikisUIConstants.Add_tags_Link);

		fluentWaitPresent(WikisUIConstants.TagEditorTextFieldInput);
		driver.getSingleElement(WikisUIConstants.TagEditorTextFieldInput).typeWithDelay(New_Tag);
		
		clickButton("OK");
	}
	
	/**
	 * likeUnlikePage - 
	 * @param LikeType
	 */
	public void likeUnlikePage(String LikeType){
		
		log.info("INFO: perform " + LikeType + " on the page");
		
		if (LikeType=="Like"){
			clickLinkWithJavascript(WikisUIConstants.likeLink);
			fluentWaitTextPresent("You like this");
		}else if (LikeType=="Unlike"){
			clickLinkWithJavascript(WikisUIConstants.unlikeLink);
		}
	}
	
	//TODO:
	/** Upload an attachment */
	public void uploadAttachment(String NameOfFile, String TypeOfFile, String FileUploadName) throws Exception {
		log.info("INFO: Upload an attachment to the page");
		//Click on the attachment tab

		clickLinkWait(WikisUIConstants.Attachments_Tab);
		//Click on the attachment link

		clickLinkWait(WikisUIConstants.AddAttachmentLink);
		//In File Upload dialog enter the name and path to the file to upload	
		FilesUI fUI = FilesUI.getGui(cfg.getProductName(), driver);
		fUI.setLocalFileDetector();
		driver.getSingleElement(BaseUIConstants.Wiki_File_Input).typeFilePath(FilesUI.getFileUploadPath(FileUploadName, cfg));

		//Upload the file
		String selector1 = WikisUIConstants.AttachmentOKButton;
		this.getFirstVisibleElement(selector1).click();

		//Check if the file is still uploading
		fluentWaitTextPresent(FileUploadName);
		log.info("INFO: verified that the attachment was added to the page");
	}

	//TODO:
	/** Open Wiki from Wikis View */
	public void openWikiFromView(String Wiki_Title, String ViewName) throws Exception {
		log.info("INFO: Opening the view: "+ViewName);
		//click on the appropriate view to test
		clickLink(ViewName);
				
		//In the view click on the wiki to open
		waitForPageLoaded(driver);
		clickLink("link=" + Wiki_Title);
		fluentWaitPresent(WikisUIConstants.Place_Bar_Title);
		
	}
	
	//TODO:
	/** verify that there is no wiki in a certain view */
	public void followWikiAction(String ActionToTake){
		//Wait for and then click on the Following link
		fluentWaitPresent(WikisUIConstants.Following_Actions_Button);
		clickLink(WikisUIConstants.Following_Actions_Button);
		if (ActionToTake.contains("Start")){
			fluentWaitPresent(WikisUIConstants.Start_Following_this_wiki);
			clickLink(WikisUIConstants.Start_Following_this_wiki);
			fluentWaitTextPresent("You are following this wiki");
		}else if (ActionToTake.contains("Stop")){
			fluentWaitPresent(WikisUIConstants.Stop_Following_this_wiki);
			clickLink(WikisUIConstants.Stop_Following_this_wiki);
			fluentWaitTextPresent("You stopped following this wiki");
		}
	}

	/**
	 * deleteTag
	 * @param NewTag
	 */
	public void deleteTag(String newTag){

		//open add remove
		log.info("INFO: Open add remove");
		clickLinkWait(WikisUIConstants.Add_or_RemoveTags_Link);
		
		//delete the tag
		log.info("INFO: Delete tag");
		clickLinkWait(getTagDeleteSelector(newTag));
		clickLinkWait("css=input[title='OK']");

		//close add remove
		log.info("INFO: Close add remove");
		clickButton("OK");
		
	}

	/**
	 * addPageTag -
	 * @param NewTag
	 */
	public void addPageTag(String NewTag) {
		log.info("INFO: Adding tag [" + NewTag + "]");
		//Edit the tag and save
		clickLinkWait(WikisUIConstants.Add_or_RemoveTags_Link);
		fluentWaitPresent(WikisUIConstants.TagEditorTextFieldInput);
		clearText(WikisUIConstants.TagEditorTextFieldInput);
		typeTextWithDelay(WikisUIConstants.TagEditorTextFieldInput, NewTag);
		clickButton("OK");

	}

	/**
	 * emptyTheTrashCan - 
	 * @param Wikiname
	 */
	public void emptyTheTrashCan(String Wikiname){
		log.info("INFO: Empty the trash can");
		//Click on the trash link
		clickLinkWait(WikisUIConstants.Trash_Link);
		
		//Click on the Delete button to delete the selected page
		clickLinkWait(WikisUIConstants.emptyTrashButton);
		
		//Click On the OK button to delete this page permanently
		clickOKButton();
		
		//Click on the trash link
		clickLinkWait(WikisUIConstants.Trash_Link);
		
		//Verify that there are no pages in the trash can
		Assert.assertTrue(driver.isTextPresent("There are no pages in the trash."));
		
		//return the to view that you were in 
		clickLink("css=div.lotusBreadcrumbs a:contains("+Wikiname+")");
		log.info("INFO: Trash can has being emptied");		
	}
	
	/**
	 * restore -
	 * @param wikiPage
	 */
	public void restore(BaseWikiPage wikiPage) {

		boolean found = false;
		//Click on the trash link
		log.info("INFO: Click on the trash link");
		clickLinkWait(WikisUIConstants.Trash);
		
		//Click on the link to delete the first item
		
		List<Element> trashedPages = driver.getElements("css=tr[class^='hentry']");		
		log.info("INFO: Number of pages in trash " + trashedPages.size());

		//Log Each Widget visible for debug purposes, also remove Library from list if disabled in xml config
		for(Element trashedPage : trashedPages){
			String title[] = trashedPage.getText().split("Today");
			
			if(title[0].contains(wikiPage.getName())){
				log.info("INFO: Found page " + title[0]);
				WebElement allChildren = (WebElement)trashedPage.getBackingObject();
				allChildren.findElement(By.cssSelector("input[id^='checkbox_']")).click();

				//Click on the Delete button to delete the selected page
				log.info("INFO: Restore a page to the wiki");
				clickLinkWait(WikisUIConstants.restoreButton);

				//Click OK to restore the page to the left nav
				clickButton("OK");
			
				found = true;
				break;
			}
			Assert.assertTrue(found, "ERROR: Unable to find and or restore page properly");
		}
	
	}

	/**
	 * 
	 */
	public void gotoEditWiki() {
		clickLink(WikisUIConstants.Edit_Button);
	}

	/**
	 * 
	 * @param readAccess
	 */
	protected void addReadAccess(ReadAccess readAccess){
		
		log.info("INFO: Changing read access to " + readAccess.name());
		driver.getFirstElement(readAccess.toString()).click();
		
	}
	
	/**
	 * 
	 * @param editAccess
	 */
	protected void addEditAccess(EditAccess editAccess){
		
		log.info("INFO: Changing edit access to " + editAccess.name());
		driver.getFirstElement(editAccess.toString()).click();
		
	}

	/**
	 * choosePageType - Selects the type of wiki page
	 * @param wikiPage - Page such as: Peer or Child
	 */
	protected void choosePageType(BaseWikiPage wikiPage){

		switch (wikiPage.getPageType()){
        	case Community:
        		log.info("INFO: Select create a wiki page from a community");
        		CommunitiesUI comUI = CommunitiesUI.getGui(cfg.getProductName(), driver);
        		if(comUI.isHighlightDefaultCommunityLandingPage()) {
        			Community_TabbedNav_Menu.HIGHLIGHTS.select(this);
        		} else {
        			if(driver.isElementPresent("css=div#dropdownNavMenuSelection:contains(Overview)")){
        				Community_LeftNav_Menu.OVERVIEW.select(this);		
        			} else{
        				Community_TabbedNav_Menu.OVERVIEW.select(this);
        			}
        		}
        		//create wiki page
        		log.info("INFO: Select create a wiki page link");
        		clickLinkWait("css=a:contains(Create a Wiki Page)");
        		break;
        	case Peer:
        		log.info("INFO: Select create a Peer wiki page");				
        		Wiki_Page_Menu.CREATEPEER.select(this);
        		break;
        	case Child:
        		log.info("INFO: Select create a Child wiki page");
        		Wiki_Page_Menu.CREATECHILD.select(this);
        		break;
        	case NavPage: 
        		log.info("INFO: Select New Page from left nav menu");
        		clickLinkWait("css=a[class='lotusAction cnxPrimaryBtn']:contains(New Page)");
        		break;
        	case Context_Peer:
        		log.info("INFO: Select create a Peer wiki page by right click");
        		clickLinkWait("css=td[id^='dijit_MenuItem_']:contains(Create Peer)");
        		break;
        	case Context_Child:
        		log.info("INFO: Select create a Child wiki page by right click");
        		clickLinkWait("css=td[id^='dijit_MenuItem_']:contains(Create Child)");
        		break;
        	case About_Child:
        		log.info("INFO: Select about tab");
        		clickLinkWait("css=a[id='about_link']");
        		log.info("INFO: Click link Create new child page");
        		clickLinkWait("css=a:contains(Create new child page)");
        		break;
        	default:
        		log.error("ERROR: Must define a page type to create a wiki page");
        		break;
			

        }
		
		
	}
		
	/**
	 * createPage - method to create a page using a community base state object
	 * @throws Exception
	 */
	public void createPage(BaseWikiPage wikiPage){
		log.info("INFO: Creating a new Wiki Page");
		
		//choose method to create page
		log.info("INFO: Choosing type of page to create");
		choosePageType(wikiPage);
		
		waitForSameTime();
		
		//Enter name
		log.info("INFO: Enter name of Page");
		fluentWaitPresent("css=#name");
		driver.getSingleElement("css=#name").clear();
		driver.getSingleElement("css=#name").type(wikiPage.getName());

		//Enter tags if we have any
		log.info("INFO: Entering tags if we have any");
		if(wikiPage.getTags() != null) {

			log.info("INFO: Click add tags link");
			clickLinkWait(WikisUIConstants.wikiPageAddTagLink);

			log.info("INFO: Add tags to text field");
			fluentWaitPresent(WikisUIConstants.TagEditorTextFieldInput);
			driver.getSingleElement(WikisUIConstants.TagEditorTextFieldInput).typeWithDelay(wikiPage.getTags());
			
			log.info("INFO: Click OK Button");
			driver.getSingleElement(WikisUIConstants.OK_Button).click();
		}

		//Enter the content in CKEditor if we have one
		log.info("INFO: Entering a content if we have some");
		if(wikiPage.getDescription() != null) {
			typeInCkEditor(wikiPage.getDescription());
		}
		
		//save and close
		log.info("INFO: Save and close the wiki page.");
		waitForPageLoaded(driver);
		scrollIntoViewElement(WikisUIConstants.saveAndClose);
		driver.getSingleElement(WikisUIConstants.saveAndClose).click();

	}

	/**
	 * deletePage -
	 * @param wikiPage
	 */
	public void deletePage(BaseWikiPage wikiPage){

		log.info("INFO: Moving page into trash");
		Wiki_Page_Menu.MOVETOTRASH.select(this);
		
		log.info("INFO: click ok button");
		clickLink("css=input[title='OK']");  

		}


	/**
	 * expandPage
	 * @param page
	 */
	public void expandPage(BaseWikiPage page){
		String treeNode = "css=div[id^=lconn_wikis_util__NavigationTreeNode_]";
		String id = "";
		for(int i=0;i<driver.getElements(treeNode).size();i++){
			if(driver.getElements(treeNode).get(i).getText().contains(page.getName())){
				id = driver.getElements(treeNode).get(i).getAttribute("id");
				break;
			}
		}
		driver.getSingleElement("css=div#" + id + " img[class='dijitTreeExpando dijitTreeExpandoClosed']").click();
	}

	/**
	 * openWikiPageNav - 
	 * @param wikiPage
	 */
	public void openWikiPageNav(BaseWikiPage wikiPage){
        fluentWaitElementVisible("css=a[class^='dijitTreeLabel'][title='" + wikiPage.getName() + "']");
		clickLinkWithJavascript("css=a[class^='dijitTreeLabel'][title='" + wikiPage.getName() + "']");			

	}
	
	/**
	 * addMember - 
	 * @param member
	 * @param wiki
	 */
	public void addMember(Member member, BaseWiki wiki) {
		
		log.info("INFO: Select Members Link");
		clickLinkWait(WikisUIConstants.Members_Link);
		
		log.info("INFO: Select Add Members button");
		fluentWaitElementVisible(WikisUIConstants.Add_Members_Button);
		clickLinkWait(WikisUIConstants.Add_Members_Button);
		
		log.info("INFO: Add member");
		addMember(member);

		//Click ok button
		log.info("INFO: Click ok button");
		clickButton("OK");
		
		//click on the breadcrumb to return to the homepage	
		clickLink("css=div.lotusBreadcrumbs a:contains"+"(" + wiki.getName() + ")");
	}
	
	/**
	 * removeMember -
	 * @param member
	 * @param wiki
	 */
	public void removeMember(Member member, BaseWiki wiki) {
		
		log.info("INFO: Select Members Link");
		clickLink(WikisUIConstants.Members_Link);
			
		log.info("INFO: Select Members checkbox");
		clickLinkWait("css=input[id='checkbox_" + getMemberUUID(member) + "']");
			
		//Click Remove Members Wiki button
		log.info("INFO: Select user check box");
		clickLinkWait("css=a[role='button']:contains(Remove Members)");
				
		//Click ok button
		log.info("INFO: Click ok button");
		clickButton("OK");
		
		//click on the breadcrumb to return to the homepage	
		clickLink("css=div.lotusBreadcrumbs a:contains"+"(" + wiki.getName() + ")");
	}

	/**
	 * changeMemberRole -
	 * @param member
	 * @param newRole
	 * @return
	 */
	public boolean changeMemberRole(Member member, WikiRole newRole){

		boolean found = false;
		log.info("INFO: Change Member role to " + newRole.toString());
		List<Element> memberList = driver.getVisibleElements("css=table[class$='lconnWikisMembersList'] tbody tr td");

		log.info("INFO: Looking for " + member.getUser().getDisplayName());
		for (ListIterator<Element> iter = memberList.listIterator(); iter.hasNext(); ) {
			Element element = iter.next();
			if(element.getText().contains(member.getUser().getDisplayName() + "\n")){
				log.info("INFO: Found User changing role");
				WebElement allChildren = (WebElement)element.getBackingObject();
				allChildren.findElement(By.cssSelector("ul a[role='button']")).click();
				
				List<Element> choiceLabel = driver.getVisibleElements("css=td[id='roles_list'] label:contains(" + newRole.toString() + ")");
				String choiceID = choiceLabel.get(0).getAttribute("for");					
				clickLinkWait("css=input[id='" + choiceID + "']");
				found = true;

				//User was found break out
				break;
			}
		}

		if(found){
			clickButton("Save");
		}
		
		return found;
	}
	
	/**
	 * create -
	 * @param wiki
	 */
	public void create(BaseWiki wiki){

		log.info("INFO: Select new wiki button");
		waitForPageLoaded(driver);
		clickLinkWait(WikisUIConstants.Start_New_Wiki_Button);

		//enter name of wiki
		log.info("INFO: Entering any Wikis name.");
		driver.getSingleElement(WikisUIConstants.Wiki_form_title).type(wiki.getName());
		
		//enter tags for wiki
		if(wiki.getTags() != null){
			log.info("INFO: Entering any Wikis tags.");
			driver.getSingleElement(WikisUIConstants.Wiki_form_tag).type(wiki.getTags());
		}

		//Enter Read Access if one was provided	
		if(wiki.getReadAccess() != ReadAccess.All){
			log.info("INFO: Changing Read Access.");
			addReadAccess(wiki.getReadAccess());
		}
		
		//Enter Editor Access if one was provided	
		if(wiki.getEditAccess() != EditAccess.AllLoggedIn){
			log.info("INFO: Changing Edit Access.");
			addEditAccess(wiki.getEditAccess());
		}
		
		//add Members
		if(!wiki.getMembers().isEmpty()) {
			for(Member m: wiki.getMembers()) {
				addMember(m);
				//check member added and name appears
				Assert.assertTrue(driver.isTextPresent(m.getUser().getDisplayName()), 
								m.getUser().getDisplayName() + " was not added to the wiki");
			}
		}
		
		//Add groups link
		
		//description
		if(wiki.getDescription() != null){
			log.info("INFO: Entering Wiki description.");
			scrollIntoViewElement("css=td textarea#createWiki_description");
			typeText("css=td textarea#createWiki_description", wiki.getDescription());	
		}	
		
		//Click Save button
		log.info("INFO: Saving wiki.");
		clickSaveButton();
		
	}

	/**
	 * delete
	 * @param wiki
	 * @param testUser
	 */
	public void delete(BaseWiki wiki, User testUser){

		//Select Delete from wikis Menu
		log.info("INFO: Selecting the delete wiki option from menu");

		try {
			Wiki_Action_Menu.DELETE.select(this);
		} catch (Exception e) {
			log.info("ERROR: Unable to use the wiki action menu properly");
			e.printStackTrace();
		}

		//Enter the Users name
		log.info("INFO: Enter the signatore name of the user deleting the wiki");
		this.typeText(WikisUIConstants.Permanently_Delete_This_Wiki_Signature , testUser.getDisplayName());
		
		//Select Check box to confirm choice to delete
		log.info("INFO: Check the check box to confirm the delete");
		this.clickLink(WikisUIConstants.Permanently_Delete_This_Wiki);
							
		//Select the Delete button
		log.info("INFO: Clicking on the delete button to delete the community");
		this.clickLinkWait(WikisUIConstants.Delete_Button);

	}

	/**
	 * 
	 * @param wiki
	 */
	public void edit(BaseWiki wiki){
		
		//Select edit from wikis Menu
		log.info("INFO: Selecting the edit wiki option from menu");
		Wiki_Action_Menu.EDIT.select(this);
		
		//Iterate through list of edits
		log.info("INFO: Iterate through the changes");
		Iterator<wikiField> iterator = wiki.getEdits().iterator();
        while(iterator.hasNext()){

            switch (iterator.next()) {
            case NAME:
            	//change name of page
            	log.info("INFO: Change name of wiki");
            	fluentWaitPresent(WikisUIConstants.wikiTitleEdit);
        		clearText(WikisUIConstants.wikiTitleEdit);
        		typeText(WikisUIConstants.wikiTitleEdit, wiki.getName());
                break;
                
            case TAG:
    			log.info("INFO: Add tags to text field");
    			fluentWaitPresent(WikisUIConstants.wikiTagEdit);
    			driver.getSingleElement(WikisUIConstants.wikiTagEdit).type(wiki.getTags());

            	break;
            	
            case DESCRIPTION:
            	//change description
    			log.info("INFO: Clearing old Wiki description.");
    			driver.getSingleElement(WikisUIConstants.wikiDescriptionEdit).clear();
    			log.info("INFO: Entering Wiki description.");
    			typeText(WikisUIConstants.wikiDescriptionEdit, wiki.getDescription());
            	break;

            default: break;
            }

        }

		log.info("INFO: Save changes");
		fluentWaitElementVisible(WikisUIConstants.wikiEditSave);
		clickLinkWait(WikisUIConstants.wikiEditSave);
	}
	
	
	/**
	 * addMember - 
	 * @param member
	 */
	public abstract void addMember(Member member);
			
	/**
	 * changeAccess -
	 * @param wiki
	 */
	public abstract void changeAccess(BaseWiki wiki);

	/**
	 * getGui -
	 * @param product
	 * @param driver
	 * @return
	 */
	public static WikisUI getGui(String product, RCLocationExecutor driver){
		if(product.toLowerCase().equals("cloud")){
			return new  WikisUICloud(driver);
		} else if(product.toLowerCase().equals("onprem")) {
			return new  WikisUIOnPrem(driver);
		} else if(product.toLowerCase().equals("production")) {
			return new  WikisUICloud(driver);
		} else if(product.toLowerCase().equals("vmodel")) {
			return new  WikisUIOnPrem(driver);
		}  else if(product.toLowerCase().equals("multi")) {
			return new  WikisUIOnPrem(driver);
		}  else {
			throw new RuntimeException("Unknown product name: " + product);
		}
	}
	
	/**
	 * Clicks at a wiki pages title element in Wikis UI
	 * 
	 * @param wikiPageTitle - The String content of the wiki page title to be clicked
	 * @return - True if all actions are successful, False otherwise
	 */
	public boolean clickWikiPageTitleElement(HomepageUI ui, String wikiPageTitle) {
		
		// Ensure that the view is reset to the top of the screen before attempting to click on the wiki page title
		ui.resetASToTop();
		
		// Set the CSS selector for the wiki page title element
		String cssWikiPageTitle = HomepageUIConstants.leftNavWikiPageTitleCSSSelector.replaceAll("PLACEHOLDER", wikiPageTitle);
		
		log.info("INFO: Ensuring that the wiki page title element is displayed with CSS selector: " + cssWikiPageTitle);
		if(ui.isElementVisible(cssWikiPageTitle)) {
			log.info("INFO: Now retrieving the wiki page title element corresponding to the CSS selector: " + cssWikiPageTitle);
			Element wikiPageTitleElement = driver.getFirstElement(cssWikiPageTitle);

			log.info("INFO: Ensuring a robust click is made at this element by clicking a location of ((X = 25% length), (Y = 50% height))");
			int quarterOfLength = (int) (wikiPageTitleElement.getSize().width / 4);
			int centreOfHeight = (int) (wikiPageTitleElement.getSize().height / 2);
			
			log.info("INFO: Now clicking on the web page title element");
			WebDriver webDriver = (WebDriver) driver.getBackingObject();
			Actions actions = new Actions(webDriver);
			actions.moveToElement((WebElement) wikiPageTitleElement.getBackingObject(), 0, 0).moveByOffset(quarterOfLength, centreOfHeight).click().perform();
			
			return true;
		} else {
			log.info("ERROR: Could NOT retrieve the wiki page title element corresponding to the CSS selector: " + cssWikiPageTitle);
			return false;
		}
	}
	

	/**
	 * Navigate to Created Blog , create new Entry and verify Tiny Editor functionality
	 * @param Base wiki object
	 * @param testUser used in verifyMentionUserInTinyEditor for user mention
	 * @return String Text present in Description of Tiny Editor.
	 */
	public String verifyTinyEditorInWikis(BaseWikiPage basewikipage, User testUser) {
		
		this.choosePageType(basewikipage);
		
		fluentWaitElementVisible(WikisUIConstants.New_Page_Title_Textfield);
		getFirstVisibleElement(WikisUIConstants.New_Page_Title_Textfield).clear();
		getFirstVisibleElement(WikisUIConstants.New_Page_Title_Textfield).type(basewikipage.getName());
		
		TinyEditorUI tui = new TinyEditorUI(driver);
		
		String EntryTitle = "Entry"+basewikipage.getName();
		log.info("INFO: Entering Entry Title " + EntryTitle);
		tui.clickOnMoreLink();
		
		log.info("INFO: Entering a description and validating the functionality of Tiny Editor");
		if (basewikipage.getDescription() != null) {

			String TE_Functionality[] = basewikipage.getTinyEditorFunctionalitytoRun().split(",");
			
			for (String functionality : TE_Functionality) {
				switch (functionality) {
				case "verifyParaInTinyEditor":
					log.info("INFO: Validate Paragragh and header functionality of Tiny Editor");
					tui.verifyParaInTinyEditor(basewikipage.getDescription());
					break;
				case "verifyAttributesInTinyEditor":
					log.info("INFO: Validate Attributes functionality of Tiny Editor");
					tui.verifyAttributesInTinyEditor(basewikipage.getDescription());
					break;
				case "verifyPermanentPenInTinyEditor":
					log.info("INFO: Validate Permanent Pen functionality of Tiny Editor");
					tui.verifyPermanentPenInTinyEditor(basewikipage.getDescription());
					break;
				case "verifyUndoRedoInTinyEditor":
					log.info("INFO: Validate Undo and Redo functionality of Tiny Editor");
					tui.verifyUndoRedoInTinyEditor(basewikipage.getDescription());
					break;
				case "verifyAlignmentInTinyEditor":
					log.info("INFO: Validate Alignment functionality of Tiny Editor");
					tui.verifyAlignmentInTinyEditor(basewikipage.getDescription());
					break;
				case "verifyIndentsInTinyEditor":
					log.info("INFO: Validate Indents functionality of Tiny Editor");
					tui.verifyIndentsInTinyEditor(basewikipage.getDescription());
					break;
				case "verifyBulletsAndNumbersInTinyEditor":
					log.info("INFO: Validate Bullets and Numbers functionality of Tiny Editor");
					tui.verifyBulletsAndNumbersInTinyEditor(basewikipage.getDescription());
					break;
				case "verifyHorizontalLineInTinyEditor":
					log.info("INFO: Validate Horizontal Line functionality of Tiny Editor");
					tui.verifyHorizontalLineInTinyEditor(basewikipage.getDescription());
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
					tui.verifySpellCheckInTinyEditor(basewikipage.getDescription());
					break;
				case "verifyRowsCoulmnOfTableInTinyEditor":
					log.info("INFO: Validate Rows and Columns of Table in Tiny Editor");
					tui.verifyRowsCoulmnOfTableInTinyEditor(basewikipage.getDescription());
					break;
				case "verifyFormatPainterInTinyEditor":
					log.info("INFO: Validate Format Painter in Tiny Editor");
					tui.verifyFormatPainterInTinyEditor(basewikipage.getDescription());
					break;
				case "verifyFontInTinyEditor":
					log.info("INFO: Validate font functionality of Tiny Editor");
					tui.verifyFontInTinyEditor(basewikipage.getDescription());
					break;
				case "verifyFontSizeInTinyEditor":
					log.info("INFO: Validate font Size functionality of Tiny Editor");
					tui.verifyFontSizeInTinyEditor(basewikipage.getDescription());
					break;
				case "verifyLinkImageInTinyEditor":
					log.info("INFO: Validate Link Image functionality of Tiny Editor");
					tui.verifyLinkImageInTinyEditor(basewikipage.getDescription());
					break;
				case "verifyRightLeftParagraphInTinyEditor":
					log.info("INFO: Validate Left to Right paragraph functionality of Tiny Editor");
					tui.verifyRightLeftParagraphInTinyEditor(basewikipage.getDescription());
					break;
				case "verifyOtherTextAttributesAndFullScreenInTinyEditor":
					log.info("INFO: Validate other text attributes functionality of Tiny Editor");
					tui.verifyOtherTextAttributesAndFullScreenInTinyEditor(basewikipage.getDescription());
					break;
				case "verifyFindReplaceInTinyEditor":
					log.info("INFO: Validate Find and Replace functionality of Tiny Editor");
					tui.verifyFindReplaceInTinyEditor(basewikipage.getDescription());
					break;
				case "verifyInsertLinkImageInTinyEditor":
					log.info("INFO: Validate Link Image functionality of Tiny Editor");
					tui.verifyInsertLinkImageInTinyEditor(basewikipage.getName());
					break;
				case "verifyTextColorInTinyEditor":
					log.info("INFO: Validate Font Text Color functionality of Tiny Editor");
					tui.verifyTextColorInTinyEditor(basewikipage.getDescription());
					break;
				case "verifyBackGroundColorInTinyEditor":
					log.info("INFO: Validate Font BackGround Color functionality of Tiny Editor");
					tui.verifyBackGroundColorInTinyEditor(basewikipage.getDescription());
					break;
				case "verifyWordCountInTinyEditor":
					log.info("INFO: Validate Word Count functionality of Tiny Editor");
					tui.verifyWordCountInTinyEditor(basewikipage.getDescription());
					break;
				case "verifyUploadImageFromDiskInTinyEditor":
					log.info("INFO: Validate Upload image from Disk functionality of Tiny Editor");
					tui.verifyUploadImageFromDiskInTinyEditor();
					break;
				case "verifyBlockQuoteInTinyEditor":
					log.info("INFO: Validate Block quote functionality of Tiny Editor");
					tui.verifyBlockQuoteInTinyEditor(basewikipage.getDescription());
					break;
				case "verifyInsertMediaInTinyEditor":
					log.info("INFO: Validate Insert Media functionality of Tiny Editor");
					tui.verifyInsertMediaInTinyEditor(basewikipage.getDescription());
					break;
				case "verifyLinkToConnectionsFilesInTinyEditor":
					log.info("INFO: Validate Link to connections files from files in Tiny Editor");
					tui.addLinkToConnectionsFilesInTinyEditor(basewikipage.getDescription());
					break;
				case "verifyCodeSampleIntinyEditor":
					log.info("INFO: Validate Code Sample functionality of Tiny Editor");
					tui.verifyCodeSampleIntinyEditor(basewikipage.getDescription());
					break;
				case "verifyInsertiFrameInTinyEditor":
					log.info("INFO: Validate Insert iFrame functionality of Tiny Editor");
					tui.verifyInsertiFrameInTinyEditor(basewikipage.getDescription());
					break;
				case "verifyExistingImagekInTinyEditor":
					log.info("INFO: Validate existing image functionality of Tiny Editor");
					tui.verifyExistingImagekInTinyEditor();
					break;	
				case "verifyMentionUserInTinyEditor":
					log.info("INFO: Validate Insert iFrame functionality of Tiny Editor");
					tui.verifyMentionUserInTinyEditor(basewikipage.getDescription(),testUser.getDisplayName());
					break;
				case "verifyEditDescriptionInTinyEditor":
					log.info("INFO: Validate Insert iFrame functionality of Tiny Editor");
					tui.verifyDefaultCaseInTinyEditor(basewikipage.getDescription());
					break;	
				}
			}
		}

		String TEText = tui.getTextFromTinyEditor();
		log.info("INFO: Get the text from Tiny Editor body" + TEText);
		
		// Save the Wikis New Page
		log.info("INFO: Saving the Entry " + EntryTitle);
		this.driver.getSingleElement(WikisUIConstants.saveAndClose).click();

		fluentWaitTextPresent(basewikipage.getName());

		return TEText;
	}
	
	public void verifyInsertedLink(String LinkName)
    {
        TinyEditorUI tui = new TinyEditorUI(driver);
        tui.verifyInsertedLinkinDescription(LinkName);
    }

	public String getWikisEntryDescText() {
		return this.getFirstVisibleElement(WikisUIConstants.wikisNewPageDesc).getText();
	}
	
	public String editDescriptionInTinyEditor(BaseWikiPage Blogs, String ediDesc) 
	{
		TinyEditorUI tui = new TinyEditorUI(driver);
		String editedDesc;
		this.getFirstVisibleElement(WikisUIConstants.Edit_Button).click();
		tui.verifyDefaultCaseInTinyEditor(ediDesc);
		editedDesc = tui.getTextFromTinyEditor();
		log.info("INFO: Get the text from Tiny Editor body" + editedDesc);
		this.driver.getSingleElement(WikisUIConstants.saveAndClose).click();

		fluentWaitTextPresent(ediDesc);
		return editedDesc;
	}

	/**
	 * getUserLinkInPopUp - Will return a link of User in Pop-Up List
	 * @param User TestUser
	 */
	public static String getUserLinkInPopUp(User TestUser) {
		return "css=li div a:contains(" + TestUser.getDisplayName() + ")";
	}
}
