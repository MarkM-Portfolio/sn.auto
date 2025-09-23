package com.ibm.conn.auto.webui;

import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.atmn.waffle.core.TestConfiguration;
import com.ibm.atmn.waffle.core.TestConfiguration.BrowserType;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.atmn.waffle.utils.Utils;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.StartPageApi;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseFolder;
import com.ibm.conn.auto.appobjects.base.BaseSubCommunity;
import com.ibm.conn.auto.appobjects.base.BaseSurvey;
import com.ibm.conn.auto.appobjects.base.BaseSurveyQuestion;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.GatekeeperConfig;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.menu.Com_Action_Menu;
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
import com.ibm.conn.auto.util.menu.Community_View_Menu;
import com.ibm.conn.auto.util.menu.Widget_Action_Menu;
import com.ibm.conn.auto.webui.cloud.CommunitiesUICloud;
import com.ibm.conn.auto.webui.cnx8.HCBaseUI;
import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import com.ibm.conn.auto.webui.constants.BlogsUIConstants;
import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import com.ibm.conn.auto.webui.constants.FilesUIConstants;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import com.ibm.conn.auto.webui.multi.CommunitiesUIMulti;
import com.ibm.conn.auto.webui.onprem.CommunitiesUIOnPrem;
import com.ibm.conn.auto.webui.production.CommunitiesUIProduction;
import com.ibm.conn.auto.webui.vmodel.CommunitiesUIVmodel;
import com.ibm.lconn.automation.framework.services.blogs.nodes.BlogPost;
import com.ibm.lconn.automation.framework.services.catalog.CatalogService;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public abstract class CommunitiesUI extends HCBaseUI {

	public CommunitiesUI(RCLocationExecutor driver) {
		super(driver);
	}

	private static Logger log = LoggerFactory.getLogger(CommunitiesUI.class);
	private APICommunitiesHandler apiOwner;


	/**
	 * communityFileOrFolderLink
	 *
	 * @param file
	 * @return
	 */
	public static String communityFileOrFolderLink(BaseFile fileOrFolder) {
		return "css=div[dndelementtitle='" + fileOrFolder.getName() + "']";
	}

	public static String searchResult(String name) {
		return "css=a[class='icSearchMainAction']:contains(" + name + ")";
	}

	public static String imgInRichContent(BaseFile file) {
		return "css=p img[src*='" + file.getName() + "']";
	}

	public static String getMoreAction(String appName) {
		return "css=div h2 a[title='Actions for: "+appName+"']";
	}

	public static String forumPositionInCol(String appName, int column) {
		return "css=#col"+column+"DropZone div h2 span:contains('"+appName+"')";
	}

	public static String appRenameTitle(String renamedTo) {
		return "css=div h2 span:contains('"+renamedTo+"')";
	}

	/**
	 * cancelAddMemberUsingX
	 * @param member
	 * @return
	 */
	public static String cancelAddMemberUsingX(Member member){
		return "css=a[title='Remove " + member.getUser().getDisplayName() + "'] img[class='lotusDelete'], img[class='fieldDel lotusDelete']";
	}

	/**
	 * getMemberEditBoxId
	 * @param member
	 * @return
	 */
	public String getMemberEditBoxId(Member member){
		Element editBox = this.getFirstVisibleElement("css=form[id^='formid']:contains(" + member.getUser().getDisplayName() +")" );
		return editBox.getAttribute("id");
	}

	/**
	 * getMemberEditBoxOwner
	 * @param editBoxID
	 * @return
	 */
	public static String getMemberEditBoxOwner(String editBoxID){
		return "css=form[id='" +editBoxID + "'] input[id^='ownerradioid'][type='radio']";
	}

	/**
	 * getMemberEditBoxMember
	 * @param editBoxID
	 * @return
	 */
	public static String getMemberEditBoxMember(String editBoxID){
		return "css=form[id='" +editBoxID + "'] input[id^='memberradioid'][type='radio']";
	}

	/**
	 * getInviteSuccessMsg
	 * @param user
	 * @return
	 */
	public static String getInviteSuccessMsg(User user){
		return 	"css=span[data-dojo-attach-point='msgBody']:contains("+ Data.getData().CommunityInviteMessage + user.getDisplayName() +".)";
	}

	/**
	 * getFileThumbnail -
	 * @param file
	 * @return
	 */
	public static String getFileThumbnail(BaseFile file) {
		return "css=a[title='Open preview of " + file.getName() + "']";
	}

	/**
	 * getViewAllLink -
	 *
	 * @param int
	 * @return
	 */
	public static String getViewAllLink(int numberOfFiles) {
		return "//a[contains(text(),'View All (" + numberOfFiles + ")')]";
	}

	/**
	 * getFileThumbnail -
	 *
	 * @param file
	 * @return
	 */
	public static String getGalleryLightbox(BaseFile file) {
		return "css=div.galleryLightBox img[title='" + file.getName() + "']";
	}

	/**
	 * getFolderHeading -
	 * @param folderName
	 * @return
	 */
	public static String getFolderHeading(String folderName){
		return "css=h1[id='scene-title']:contains('"+ folderName + "')";
	}

	/**
	 *
	 * @param widgetId
	 * @return
	 */
	public static String getWidgetTitle(String widgetId){
		return "css=span[id^='" + widgetId +"']";
	}

	/**
	 *
	 * @param widgetName
	 * @return
	 */
	public String getLeftNavWidget (String widgetName){
		return "css=li[id$='_navItem'] a:contains(" + widgetName + ")";
	}

	/**
	 *
	 * @param locInList
	 * @return
	 */
	public String getUserInList(String locInList){
		return "css=div[id='" + locInList + "']";
	}

	 /**
	  * getCommunityView
	  * @param viewName
	  * @return
	  */
	 public static String getCommunityView(Community_View_Menu viewName){
		 	return "css=div ul li a:contains(" + viewName.getMenuItemText() + ")";
	 }

	/**
	 * getCommunityLink
	 * @param community
	 * @return
	 */
	public static String getCommunityLink(BaseCommunity community){
		return  "xpath=//a[text()='"+community.getName()+"']";
	}	

	/**
	 * 
	 * @param getcommunityName
	 * @return
	 */
	public static String getRelatedCommunityNameLink(String communityName){
		return "css=tr[name='"+communityName+"']";
	}

	// get a link to a card using the community name
	public static String getCommunityCardByNameLink(String communityName) {
		return "css=.bx--cardv2.community-card[aria-label=\"" + communityName + "\"]";
	}

	// get a link to a Tag in the Filter panel
	public static String getCommunityFilterTagLink(String tagName) {
		return "css=.tags-filter .bx--tag[data-value=\"" + tagName + "\"]";
	}

	// get a link to a Tag in the bread crumb panel
	public static String getCommunityFilterBreadcrumbsTagLink(String tagName) {
		return "css=.filter-breadcrumbs .bx--tag[data-value=\"" + tagName + "\"]";
	}

	// get the time of visit for a community card using the community name
    public static String getTimeOfVisitForCommunityCard(String communityName) {
        return getCommunityCardByNameLink(communityName) + " p.recently-visited";
    }

	public static String getCommunityLinkCardView(BaseCommunity community) {
		// community name is truncated in 6.0 CR4 Card View, use p id='title-UUID' selector instead
		String UUID_PREFIX = "communityUuid=";
		String commUuid = community.getCommunityUUID();
		if (commUuid != null) {
			String uuid = community.getCommunityUUID().replace(UUID_PREFIX, "");
			return "css=a#title-" + uuid + ", p#title-" + uuid ;
		}
		return null;
	}

	// get link for a sub community
	public static String getSubCommunityLinkCardView(BaseSubCommunity subCommunity) {

		String UUID_PREFIX = "communityUuid=";
		String commUuid = subCommunity.getCommunityUUID();
		if (commUuid != null) {
			return "css=p#title-" + subCommunity.getCommunityUUID().replace(UUID_PREFIX, "");
		}
		return null;
	}

	public static String getStreamCommunityLink(BaseCommunity community) {
		return "css=div.activityStreamNewsItemContainer a:contains('" + community.getName() + "')";
	}

	public static String getCommunityIdeationBlogSelector(String blogName){
		return "css=h4[class='lotusBreakWord'] a:contains(" + blogName + ")";
	}

	public static String getCommunitySelector(String name) {
		return "link=" + name;
	}

	public void gotoCommunities(String bannerSelector) {
		driver.getFirstElement(bannerSelector).hover();
		Element e = driver.getFirstElement(BaseUIConstants.Im_Owner);
		String url = e.getAttribute("href");
		driver.navigate().to(url);
	}

	public String getCommunitiesMegaMenu(){
		return CommunitiesUIConstants.communitiesMegaMenu;
	}

	public String getCommunitiesMegaMenuMyCommunities() {
		return CommunitiesUIConstants.communitiesMegaMenuMyCommunities;
	}

	public String getCommunitiesMegaMenuOwner(){
		return CommunitiesUIConstants.communitiesMegaMenuOwner;
	}

	public String getTemplateName(String templateName){
		return "css=div[class^='MuiCardContent-root'] p[title='"+templateName+"']";
	}

	public String getCategoryName(String categoryName){
		return "xpath=//td[starts-with(@class, 'MuiTableCell-root')][text()='"+categoryName+"']";
	}

	public String getCategoryLocator(String categoryName){
		return "xpath=//span[contains(@class, 'MuiTypography-root')][text()='"+categoryName+"']";
	}

	/**
	* checkImportantBkmksApp() method checks to see if the specified bookmark appears in the Important Bookmarks widget
	* @param String bookmarkName
	* returns 'true' if found; otherwise, false
	*/
	public boolean checkImportantBkmksApp(String bookmarkName){
	boolean returnedValue=false;

		List<Element> visibleImportantBkmks = driver.getElements("css=div[id='importantBookmarks'] a[class='action bidiAware']");
		log.info("INFO: Number of Important Bookmarks listed: " + visibleImportantBkmks.size());

		//Loops thru visible bookmarks in the Important Bookmarks widget & checks to see if it exists
		for(Element visImpBookmarks : visibleImportantBkmks){
			String title = visImpBookmarks.getText();
			log.info("INFO: Important Bookmark listed: " + title );
			if(title.equalsIgnoreCase(bookmarkName)){
				log.info("INFO: Bookmark " + bookmarkName + " appears in the Important Bookmarks widget");
				returnedValue=true;
				break;
				}
			}
		if (!returnedValue){
				log.info("INFO: Bookmark " + bookmarkName + " does not appear ");
	}
		return returnedValue;
	}

	/**
	 * AddWidget() Method to add widget to community if not present
	 * @param BaseWidget widget
	 * <p>
	 * @author Ralph LeBlanc
	 */
	public void addWidget(BaseWidget widget){

		log.info("INFO: Widget " + widget.getTitle() +" is not present");
		//Make selection
		Com_Action_Menu.ADDAPP.select(this);

		log.info("INFO: Select Widget");
		selectWidget(widget);

		log.info("INFO: Check is Adding Application Loading screen visible");
		if(this.fluentWaitTextNotPresentWithoutRefresh("Adding Application"))
			log.info("INFO: Adding Application is no longer present...Loading finished");
		else
			{
			log.info("INFO: Adding Application is still present...Loading issue");
			//Check if the widget has been added
			log.info("INFO: Check to see if widget " + widget.getTitle() + " is enabled");
			// wait for element to be added
			fluentWaitPresent("css=h4.lotusTitle.lotusAdded span[title=\""+widget.getTitle()+" added\"]");

			if(driver.isElementPresent("css=h4.lotusTitle.lotusAdded span[title=\""+widget.getTitle()+" added\"]"))
				log.info("INFO: Widget" + widget.getTitle() +" is enabled");
			else
				log.error("ERROR: Widget is not found and not enabled");

			}
		//close the disabled widget palette
		this.clickLinkWithJavascript(CommunitiesUIConstants.WidgetSectionClose);

		Assert.assertTrue(this.fluentWaitTextNotPresentWithoutRefresh("Add Apps"), "ERROR: Unable to dismiss Add Apps dialog");
	}

	public void selectWidget(BaseWidget widget){

		fluentWaitElementVisible("css=div[widgetid=app_palette_dialog_modal]");

		Element widgetElement=null;
		widgetElement = iterateThroughWidgetPalette(widget);

		//Iterate through more pages if they exist
		while (widgetElement == null && isElementPresent(CommunitiesUIConstants.widgetPaletteNextButton)) {
			log.info("INFO: More than one page of apps found, clicking to next page");
			clickLink(CommunitiesUIConstants.widgetPaletteNextButton);
			widgetElement = iterateThroughWidgetPalette(widget);

		}

		//If widget is not in Palette check to see if the widget is already enabled
		if(widgetElement == null){
			log.info("INFO: Check to see if Community widget " + widget.getTitle() + " is already enabled");
			if(driver.isElementPresent("css=h2[class='ibmDndDragHandle']>span:contains("+widget.getTitle()+")")){
				log.info("INFO: Widget" + widget.getTitle() +" is already enabled");

			}else{
				log.error("ERROR: Widget is not found and not already enabled");
			}
		}

	}

	/**
	 * iterateThroughWidgetPalette() - method to add widget from list palette
	 * @param BaseWidget widget - widget to add
	 * @Return widgetElement if the widget is found and added, null otherwise
	 */
	public Element iterateThroughWidgetPalette(BaseWidget widget) {
		Element widgetElement = null;
		// collect the visible widget web elements
		List<Element> visibleWidgets = driver
				.getElements("css=div[class='lotusWidgetTitle'] h4[class='lotusTitle'] a[role='button']");

		// check to see if the widget is in list
		Iterator<Element> widgetList = visibleWidgets.iterator();
		while (widgetList.hasNext()) {
			widgetElement = widgetList.next();
			if (widgetElement.getText().equals(widget.getTitle())) {
				log.info("INFO: Widget " + widgetElement.getText() + " is found and disabled");
				log.info("INFO: Adding widget " + widgetElement.getText());
				WebDriver wd = (WebDriver) driver.getBackingObject();
				WebElement element = (WebElement) widgetElement.getBackingObject();
				scrolltoViewElement(element, wd);
				element.click();
				break;
			}
			widgetElement = null;
		}
		return widgetElement;
	}

	public void scrollToWidget(BaseWidget widget) {
		int bannerHieght = 90;
		Point widgetLocation = driver.getSingleElement(getWidgetByTitle(widget)).getLocation();
		driver.executeScript("window.scroll(0,"+(widgetLocation.y-bannerHieght)+")");
	}

	/**
	 * collectDisabledCommWidgets() - collect disabled widgets inside of a community with customization open
	 * @return List<Element>
	 * <p>
	 * @author Ralph LeBlanc
	 */
	public List<Element> collectDisabledCommWidgets() {

		fluentWaitElementVisible(CommunitiesUIConstants.WidgetSectionClose);
		//collect the visible widget web elements
		List<Element> visibleWidgets = driver.getElements("css=div[class='lotusWidgetTitle'] h4[class='lotusTitle'] a[role='button']");
		log.info("INFO: Disabled Widgets = " + visibleWidgets.size());

		Element toRemove = null;
		//Log each Widget visible for debug purposes, also remove Library from list if disabled in xml config
		for(Element visWidget : visibleWidgets){
			String title = visWidget.getText();
			log.info("INFO: Widget " + title + " is disabled");
			if(title.equalsIgnoreCase("Library")){
				if(!cfg.isFilenetEnabled()){
					log.info("INFO: Ignore Library widget as Filenet is not enabled");
					toRemove = visWidget;
				}
			}
		}
		visibleWidgets.remove(toRemove);

		return visibleWidgets;
	}

	/**
	 * getWidgetLocation - this method collects a widget that is part of the content areas current
	 * location in the list
	 * @param widget
	 * @return int representing location, if widget is found on the page, return its location, if not found, return 0;
	 */
	public int getWidgetLocation(BaseWidget widget){
		List<Element> enabledWidgets = driver.getElements(CommunitiesUIConstants.contentEnabledWidgets );
		int location=0;
		String title = widget.getTitleOnPage();

		log.info("INFO: size of center content area " + enabledWidgets.size());

		for(Element ewidget : enabledWidgets){
			location++;
			String widgetId = ewidget.getAttribute("widgetid");
			log.info("INFO: widget id : " + widgetId);
			log.info(driver.getSingleElement(getWidgetTitle(widgetId)).getText());
			if(driver.getSingleElement(getWidgetTitle(widgetId)).getText().contentEquals(title)){
				log.info("INFO: Found Widget in center column at location " +location);
                return location;
			}
		}

		// widget is not in center content area, find it in right column

		location = 0;
		List<Element> rightColumnWidget = driver
				.getElements(CommunitiesUIConstants.rightColumnWidgets);
		log.info("INFO: size of right column area " + rightColumnWidget.size());
		for (Element rwidget : rightColumnWidget) {
			location++;
			String widgetId = rwidget.getAttribute("widgetid");
			log.info("INFO: widget id : " + widgetId);
			log.info(driver.getSingleElement(getWidgetTitle(widgetId)).getText());
			if (driver.getSingleElement(getWidgetTitle(widgetId)).getText().contentEquals(title)) {
				log.info("INFO: Found Widget in right column at location "+ location);
				return location;
			}
		}

		// widget is not in center and right content area, find it in left column

		location = 0;
		List<Element> leftColumnWidget = driver.getElements(CommunitiesUIConstants.leftColumnWidgets);
		log.info("INFO: size of left column area " + leftColumnWidget.size());
		for (Element lwidget : leftColumnWidget) {
			location++;
			String widgetId = lwidget.getAttribute("widgetid");
			log.info("INFO: widget id : " + widgetId);
			log.info(driver.getSingleElement(getWidgetTitle(widgetId)).getText());
			if (driver.getSingleElement(getWidgetTitle(widgetId)).getText().contentEquals(title)) {
				log.info("INFO: Found Widget in right column at location "+ location);
				return location;
			}
		}

		return 0;
	}

	/**
	 * getWidgetLocationInfo - this method locates the widget and returns either the column the widget is located in
	 * or the widget position within the column
	 * @param widget
	 * @param returnColumn
	 *      true = return the column number
	 *      false = return widget location in the column
	 * @return int
	 *
	 * return values for column number
	 *   0 = not found
	 *   1 = left column
	 *   2 = middle column
	 *   3 = right column
	 *   4 = banner area
	 *
	 * return value for widget location
	 *   0 = not found
	 *   1 thru x = widget location
	 */



	public int getWidgetLocationInfo(BaseWidget widget,Boolean returnColumn ){
		List<Element> enabledWidgets = driver.getElements(CommunitiesUIConstants.contentEnabledWidgets );
		int location = 0;
		String title = widget.getTitleOnPage();

		log.info("INFO: size of center content area " + enabledWidgets.size());

		for(Element ewidget : enabledWidgets){
			location++;
			String widgetId = ewidget.getAttribute("widgetid");
			log.info("INFO: widget id : " + widgetId);
			log.info(driver.getSingleElement(CommunitiesUI.getWidgetTitle(widgetId)).getText());
			if(driver.getSingleElement(CommunitiesUI.getWidgetTitle(widgetId)).getText().contentEquals(title)){
				log.info("INFO: Found Widget in the center column at location " + location);
				if (returnColumn)
					return 2;
				else
					return location;
			}
		}
		// widget is not in center content area, find it in right column

		location = 0;
		List<Element> rightColumnWidget = driver.getElements(CommunitiesUIConstants.rightColumnWidgets);
		log.info("INFO: size of right column area " + rightColumnWidget.size());
		for (Element rwidget : rightColumnWidget) {
			location++;
			String widgetId = rwidget.getAttribute("widgetid");
			log.info("INFO: widget id : " + widgetId);
			log.info(driver.getSingleElement(CommunitiesUI.getWidgetTitle(widgetId)).getText());
			if (driver.getSingleElement(CommunitiesUI.getWidgetTitle(widgetId)).getText().contentEquals(title)) {
				log.info("INFO: Found Widget in the right column at location "+ location);
				if (returnColumn)
					return 3;
				else
					return location;
			}
		}
		// widget is not in right column, find it in the left column
		location = 0;
		List<Element>leftColumnWidget = driver.getElements(CommunitiesUIConstants.leftColumnWidgets);
		log.info("INFO: size of left column area " + leftColumnWidget.size());
		for (Element lwidget : leftColumnWidget) {
			location++;
			String widgetId = lwidget.getAttribute("widgetid");
			log.info("INFO: widget id : " + widgetId);
			log.info(driver.getSingleElement(CommunitiesUI.getWidgetTitle(widgetId)).getText());
			if(driver.getSingleElement(CommunitiesUI.getWidgetTitle(widgetId)).getText().contentEquals(title)){
				log.info("INFO: Found widget in the left column at location "+ location);
				if (returnColumn)
					return 1;
				else
					return location;
			}
		}
        // widget is not in the left column, find it in the banner area
		location = 0;
		List<Element>bannerAreaWidget = driver.getElements(CommunitiesUIConstants.bannerAreaWidgets);
		log.info("INFO: size of banner area " + bannerAreaWidget.size());
		for (Element bwidget : bannerAreaWidget) {
			location++;
			String widgetId = bwidget.getAttribute("widgetid");
			log.info("INFO: widget id : " + widgetId);
			log.info(driver.getSingleElement(CommunitiesUI.getWidgetTitle(widgetId)).getText());
	        if(driver.getSingleElement(CommunitiesUI.getWidgetTitle(widgetId)).getText().contentEquals(title)){
				log.info("INFO: Found widget in the banner area at location "+ location);
				if (returnColumn)
					return 4;
				else
					return location;
			}
		}

		return 0;
	}

	/**
	 * getTopNavItems(): This method will get the apps on the tabbed/top navigation.
	 * @param: true or false, enter true to sort the top nav items in alphabetical order;otherwise, enter false for no sorting
	 * @return: List<string> apps listed on the tabbed/top navigation
	 */

	 public List<String> getTopNavItems(Boolean doSort) {
			List<String> topNavItems = new ArrayList<String>();
			List<String> temp = new ArrayList<String>();

			//Add the menu items to List
			List<Element> leftNavMenuElem = driver.getVisibleElements(CommunitiesUIConstants.leftNavMenuItems);
			for(Element widget : leftNavMenuElem) {
				topNavItems.add(widget.getText().trim().toLowerCase());

				log.info("INFO: top nav tab found: " + widget.getText().trim().toLowerCase());
			}

			if((driver.getVisibleElements(CommunitiesUIConstants.tabbedNavMoreTab).size())!=0){
				clickLinkWait(CommunitiesUIConstants.tabbedNavMoreTab);
				List<Element> topNavMoreElem = driver.getElements(CommunitiesUIConstants.topNavMoreMenuItems);
				for(Element topWidget : topNavMoreElem) {
					topNavItems.add(topWidget.getText().trim().toLowerCase());

					log.info("INFO: top nav MORE menu item found: " + topWidget.getText().trim().toLowerCase());
				}
			}
			//Sort the menu order and add Overview as first element
			topNavItems.remove("overview");
			if(doSort){
				Collections.sort(topNavItems);
			}
			temp.add("overview");
			temp.addAll(topNavItems);
			topNavItems=temp;

			return topNavItems;
	 }

	/**
	 * checkWidgetMenuPresent(): This method will check whether the action specified in to sAction present in
	 * the widget action menu to the widget specified in sWidgetName
	 * <p>
	 * @param sWidgetName String - name of Community widget
	 * @param sAction String - name of the action from the pull down menu.
	 * @author
	 */
	public boolean checkWidgetMenuPresent(BaseWidget widget, Widget_Action_Menu action) {

		Element menu = getFirstVisibleElement(getWidgetByTitle(widget));
		String id = menu.getAttribute("id");

		//Select Widget Action Menu option
		log.info("INFO: Opening Widget Action Menu");
		navigateMenuByID(id);

		//Make menu selection
		List<Element> options = driver.getElements("css=table[id^='" + id.substring(17) + "'] tbody>tr");
		for (Element option : options){
			log.info("INFO: "+ option.getText());
			if(option.getText().contains(action.getMenuItemText())){
				log.info("INFO: Found menu " + action.getMenuItemText());
				return true;
			}
		}
        return false;
	}

	/**
	 * checkWidgetMenuPresentClick(): This method will check whether the action specified (sAction) is present on the widget action
	 * menu for the specified widget (sWidgetName).  If action is found the action will be clicked/selected.
	 *
	 * @param sWidgetName String - name of Community widget
	 * @param sAction String - name of the action from the pull down menu.
	 * @author
	 */
	public boolean checkWidgetMenuPresentClick( String widgetTitle, Widget_Action_Menu action, boolean doClick) {

		Element menu = this.getFirstVisibleElement("css=a[title='Actions for: " + widgetTitle + "']");
		String id = menu.getAttribute("id");

		//Select Widget Action Menu option
		log.info("INFO: Opening Widget Action Menu");
		this.navigateMenuByID(id);

		//Make menu selection
		List<Element> options = driver.getElements("css=table[id^='" + id.substring(17) + "'] tbody>tr");
		for (Element option : options){
			log.info("INFO: "+ option.getText());
			if(option.getText().contains(action.getMenuItemText())){
				log.info("INFO: Found menu " + action.getMenuItemText());
				if (doClick)
				{
					option.click();
					log.info("INFO: Clicking " + action.getMenuItemText());
				}
				return true;
		}
		}
        return false;
	}

	/**
	 * getWidgetActionMenuLinks - this gets a list of links from the widget action menu
	 * @param - name of widget
	 * @return - text of menu items in a vector
	 */
	//get the Tags widget id
	public Vector<String> getWidgetActionMenuLinks(BaseWidget widget){
			Element menu;
			String id;
			Vector<String> widgetMenuText=new Vector<String>();

			log.info("INFO: Get Tags widget id");
			menu = this.getFirstVisibleElement(this.getWidgetByTitle(widget));
			id = menu.getAttribute("id");

			//click on the Tags widget action menu
			log.info("INFO: Click on Tags widget action menu icon");
			this.navigateMenuByID(id);

			//Get the list of action menu links & list them out
			log.info("INFO: Get the list of widget actions menu links");
			List<Element> options = driver.getElements("css=table[id^='" + id.substring(17) + "'] tbody>tr");

			for (Element option : options){
				log.info("INFO: "+ option.getText());
				widgetMenuText.add(option.getText());
				}
			return widgetMenuText;
	}


	/**
	 * preformCommWidgetAction(): This method will execute the action specified in to sAction from
	 * the widget action menu to the widget specified in sWidgetName
	 * <p>
	 * @param sWidgetName String - name of Community widget to perform the task on.
	 * @param sAction String - name of the action from the pull down menu.
	 * @author Ralph LeBlanc
	 */
	public void performCommWidgetAction(BaseWidget widget, Widget_Action_Menu action) {
		if(cfg.getUseNewUI()) {
			waitForElementVisibleWd(createByFromSizzle(getWidgetByTitle(widget)), 5);
			scrollIntoViewElement(getWidgetByTitle(widget));
		}
		

		Element menu = getFirstVisibleElement(getWidgetByTitle(widget));
		String id = menu.getAttribute("id");

		//Select Widget Action Menu option
		log.info("INFO: Opening Widget Action Menu");
		navigateMenuByID(id);

		//Make menu selection
		List<Element> options = driver.getElements("css=table[id^='" + id.substring(17) + "'] tbody>tr");
		for (Element option : options){
			log.info("INFO: "+ option.getText());
			if(option.getText().contains(action.getMenuItemText())){
				option.click();
				log.info("INFO: Clicking " + action.getMenuItemText());
				break;
			}
		}


	}

	/**
	 * getCenterColumnSize - this method return the movable widget number in the center column of overview page
	 * @return int the number of widget in that column
	 */
	public int getCenterColumnSize(){
		int size=0;
		List<Element> widgetList = driver.getElements(CommunitiesUIConstants.contentEnabledWidgets );
	    size = widgetList.size();
		return size;
	}

	/**
	 *  getRightColumnSize - this method return the movable widget number in the right column of overview page
	 * @return int the number of widget in that column
	 */
	public int getRightColumnSize(){
		int size=0;
		List<Element> widgetList = driver.getElements(CommunitiesUIConstants.rightColumnWidgets);
	    size = widgetList.size();
		return size;
	}

	/**
	 *  getLeftColumnSize - this method return the movable widget number in the left column of overview page
	 * @return int the number of widget in that column
	 */
	public int getLeftColumnSize(){
		int size=0;
		List<Element> widgetList = driver.getElements(CommunitiesUIConstants.leftColumnWidgets);
	    size = widgetList.size();
		return size;
	}

	/**
	 * getSummaryWidgetTitle - this method will get the widget title
	 *
	 */
	public String getSummaryWidgetTitle (BaseWidget widget){
		Element menu = this.getFirstVisibleElement(getWidgetByTitle(widget));
		String id = menu.getAttribute("id");
		String title;

		id = id.replace("widgetActionsMenu", "");

		id = id + "Id";
		title = this.getElementText("css=span[id=" + id + "]");
		log.info("INFO: widget title name: " + title);
		return title;
	}

	/**
	 * addHiddenWidget() Method to add widget from hidden area to community overview if not present
	 * @param BaseWidget widget
	 * <p>
	 * @author Ralph LeBlanc
	 */
	public void addHiddenWidget(BaseWidget widget){

		log.info("INFO: Widget" + widget.getTitle() +" is not present");
		//Make selection
		Com_Action_Menu.ADDAPP.select(this);
		this.fluentWaitElementVisible(CommunitiesUIConstants.HiddenArea);
		clickLinkWait(CommunitiesUIConstants.HiddenArea);

		//collect the visible widget web elements
		List<Element> visibleWidgets = driver.getElements("css=div[class='lotusWidgetTitle'] h4[class='lotusTitle'] a[role='button']");
		log.info("INFO: Disabled Widgets " + visibleWidgets.size());

		Element toRemove = null;
		//Log Each Widget visible for debug purposes, also remove Library from list if disabled in xml config
		for(Element visWidget : visibleWidgets){
			String title = visWidget.getAttribute("title");
			log.info("INFO: Widget " + title + " is disabled");
			if(title.equalsIgnoreCase("Library")){
				if(!cfg.isFilenetEnabled()){
					log.info("INFO: Filenet is not enabled ignore library widget.");
					toRemove = visWidget;
				}
			}
		}
		visibleWidgets.remove(toRemove);

		//check to see if the widget is in list
		Element widgetElement=null;
		Iterator<Element> widgetList = visibleWidgets.iterator();
		while(widgetList.hasNext()){
			widgetElement = widgetList.next();
		    if (widgetElement.getText().equals(widget.getTitle())){
		    	log.info("INFO: Widget " + widgetElement.getText() + " is found and disabled");
				log.info("INFO: Adding widget " + widgetElement.getText());
				widgetElement.click();
	    		break;
		    	}
		    widgetElement=null;
			}

		//If widget is not in Palette check to see if the widget is already enabled
		if(widgetElement == null){
			log.info("INFO: Check to see if Community widget " + widget.getTitle() + " is already enabled");
			if(driver.isElementPresent("css=h2[class='ibmDndDragHandle']>span:contains("+widget.getTitle()+")")){
				log.info("INFO: Widget" + widget.getTitle() +" is already enabled");

			}else{
				log.error("ERROR: Widget is not found and not already enabled");
			}
		}

		//close the disabled widget palette
		driver.getSingleElement(CommunitiesUIConstants.WidgetSectionClose).click();


	}

	/**
	 * addAllEnabledWigdetsToCommunity - enables all communitiy widgets it finds and returns a list for validation.
	 * Must be inside a community to work
	 * @return List<String> containing the widgets that it enables
	 * @Author Ralph LeBlanc
	 */
	public List<String> addAllEnabledWigdetsToCommunity()throws Exception{
		String widgetName;
		List<String> widgetList = new ArrayList<String>();

		//Chose customize from Community Actions
		Com_Action_Menu.CUSTOMIZE.select(this);
		this.waitForPageLoaded(driver);

		//collect all the disabled widget elements
		List<Element> widgets = this.collectDisabledCommWidgets();

		log.info("INFO: Widgets to enable " + widgets.size());
		//add the element text to String list
		Iterator<Element> elementList = widgets.iterator();
		while(elementList.hasNext()){
		    widgetList.add(elementList.next().getText());
		}

		//select the widgets from above
		for (Element widget : widgets){
			widgetName=widget.getText();

			//When Featured Survey is added the 'Add Surveys Widget' pop-up displays. Clicking OK automatically
			//adds Surveys. Surveys can only be added once.  Since it has already been added,
			//Surveys will no longer display as a link & can no longer be selected.
			if(widgetName.equals(""))
				continue;

			log.info("INFO: Enabling " + widgetName);
			widget.click();
			this.waitForPageLoaded(driver);
			log.info("INFO: Validate that " + widgetName + " is enabled.  If not try again.");
			if(!widget.isVisible()){
				log.info("INFO: Widget " + widgetName + " enabled properly");
			}else if(widget.isVisible()){
				this.waitForPageLoaded(driver);
				log.info("INFO: Widget " + widgetName + " not enabled attempting one more time");
				try{
					widget.click();
				}catch(ElementNotVisibleException e){
					log.info("INFO: Widget became not visible during double check");

				}
			}
			//Click on OK button on the 'Add Surveys Widget' pop-up dialog
			if(widgetName.contains("Featured Survey") && this.fluentWaitPresent(CommunitiesUIConstants.RevokeInviteOK))
				this.clickLinkWait(CommunitiesUIConstants.RevokeInviteOK);
		}


		//Close the widget
		this.clickLink(CommunitiesUIConstants.CloseWidget);

		return widgetList;
	}

	/**
	*<ul>
	*<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	*<li><B>Step: Create a community, then customize by adding all the available widgets to the community</B> </li>
	*<li><B>Verify: All widgets are added to the community without any errors in the UI </B> </li>
	*</ul>
	*@throws Exception
	*/
	public void addAllWidgets() {
		List<String> widgetsEnabled = null;
		if (!checkGKSetting(Data.getData().commTabbedNav)){

			//Add the widgets to the community
			try {
				widgetsEnabled = addAllEnabledWigdetsToCommunity();
			} catch (Exception e) {
				e.printStackTrace();
			}

			//validate newly created widgets links appear in the left nav
			//Open Overview menu
			this.clickLinkWithJavascript(CommunitiesUIConstants.OpenOverViewMenu);
			for(String widget: widgetsEnabled) {
				log.info("INFO: Checking left navigation bar for " + widget);
				//Subcommunities and Gallery are not listed in left nav bar so validate separately
				if(widget.contentEquals("Gallery") || widget.equals("Featured Survey") || widget.equals("Rich Content")){
					boolean found = false;
					List<Element> elements = driver.getElements(CommunitiesUIConstants.rightsideGalleryWidget);
					for (Element element : elements){
						log.info("INFO: Right side Widget" + element.getText());
						if(element.getText().contentEquals("Gallery") || element.getText().contentEquals("Featured Survey")){
							found = true;
							break;
						}
					}
					Assert.assertTrue(found,
							"ERROR: Unable find 'Gallery' widget on right side widget container");
				}else if(widget.contentEquals("Subcommunities")){
					log.info("INFO: " + widget + " exception note: there is not a left navigation bar link.");
					Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.subCommunitiesWidgetTitle),
							"ERROR: Unable to find Subcommunities widget in list of widgets");
				}else{
					//remaining widgets validate against left menu item
					Assert.assertTrue(driver.getFirstElement(this.getLeftNavWidget(widget)).getText().contentEquals(widget));
				}
			}

			//Close Overview menu
			this.clickLinkWithJavascript(CommunitiesUIConstants.OpenOverViewMenu);
		}
		else {

			//Tabbed Nav GK setting is enabled - Add the widgets to the community
			try {
				widgetsEnabled = addAllEnabledWigdetsToCommunity();
			} catch (Exception e) {
				e.printStackTrace();
			}
			List<String> topNavElements = getTopNavItems(false);
			for(String widget: widgetsEnabled) {
				log.info("INFO: Checking top navigation for added widget : " + widget);

				//Subcommunities, Rich Content, Gallery & Featured Survey do not appear on the
				//top navigation.  To verify they get added will check for the summary widget title
				//on the Overview page
				if(widget.contentEquals("Subcommunities")){
					log.info("INFO: Verify " + widget + " appears on the Overview page.");
					Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.subCommunitiesWidgetTitle),
							"ERROR: Unable to find " + widget + " summary widget on the Overview page.");
				}
				else if(widget.contentEquals("Rich Content")){
					//Rich Content will appear twice because it gets added by default & then added as part of this test
					log.info("INFO: Verify " + widget + " appears twice on the Overview page.");
					Assert.assertTrue(driver.getElements(CommunitiesUIConstants.richContentWidgetTitle).size()==2,
							"ERROR: " + widget + " does not appear twice on the Overview page.");
				}
				else if(widget.contentEquals("Gallery")){
					log.info("INFO: Verify " + widget + " appears on the Overview page.");
					Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.rightsideGalleryWidget),
							"ERROR: Unable to find " + widget + " summary widget on the Overview page.");
				}
				else if(widget.contentEquals("Featured Survey")){
					log.info("INFO: Verify " + widget + " appears on the Overview page.");
					Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.featuredSurveyWidgetTitle),
							"ERROR: Unable to find " + widget + " summary widget on the Overview page.");
				}
				else{
					//remaining widgets get validate against top navigation tabs
					log.info("INFO: Verify " + widget + " appears on the top navigation menu");
					Assert.assertTrue(topNavElements.contains(widget.toLowerCase()),
							"ERROR: Widget " + widget + " not found on top navigation menu");
				}
			}
		}


	}

	/**
	 * Remove a widget from overview page
	 * @param widget community widget to delete
	 * @param userDelete user who is deleting the widget
	 */
	public void removeWidget(BaseWidget widget, User userDelete){

		log.info("INFO: remove " + widget.getTitle() + " widget");

		log.info("INFO: Enter the name of the widget to delete");
		this.typeText(CommunitiesUIConstants.WidgetNameInput, widget.getTitle());

		// Enter User that is deleting this widget
		log.info("INFO: Enter the name of the user who is deleting the widget");
		log.info("user to delete " + userDelete.getDisplayName());
		this.typeText(CommunitiesUIConstants.SignatureInput, userDelete.getDisplayName());

		// Ensure we wait a moment for the delete button to by visible
		log.info("INFO: Ensure that delete button is visible");
		this.fluentWaitElementVisible(CommunitiesUIConstants.OkButtonOnRemoveWidget);

		// Select the Delete button
		log.info("INFO: Clicking ok button to delete the widget");
		this.clickLink(CommunitiesUIConstants.OkButtonOnRemoveWidget);
		this.fluentWaitTextNotPresent(CommunitiesUIConstants.OkButtonOnRemoveWidget);
	}

	/**
	 * Enter text into the catalog filter bar
	 * @param text
	 */
	public void applyCatalogFilter(String text, boolean isCardView) {
		String selector = isCardView ? CommunitiesUIConstants.catalogFilterCardView : CommunitiesUIConstants.catalogFilter;
		this.clearText(selector);
		this.typeText(selector, text);
	}

	/**
	 * Remove a widget from overview page
	 * @param widget community widget whose remove dialog doesn't require signature. SubCommunities, Gallery widget
	 * @param userDelete user who is deleting the widget
	 */
	public void removeWidget(){
		// Select the Ok button
		log.info("INFO: Clicking ok button to delete the widget");
		fluentWaitElementVisible(CommunitiesUIConstants.OkButtonOnRemoveWidgetWithoutSignature);
		this.clickLink(CommunitiesUIConstants.OkButtonOnRemoveWidgetWithoutSignature);
		this.fluentWaitTextNotPresent(CommunitiesUIConstants.OkButtonOnRemoveWidgetWithoutSignature);
	}

	/**
	 * getWidgetByTitle - get html element of widget
	 * @param Widget widget to be retrieved from page
	 * @return
	 */
	public String getWidgetByTitle(BaseWidget widget){
		return "css=a[title='Actions for: " + widget.getTitleOnPage() + "']";
	}

	/*
	 * HTML element of widget help page
	 */
	public abstract String getWidgetHelpFrame();

	/*
	 *  HTML element of widget help page
	 */
	public abstract String getWidgetHelpTitle(BaseWidget widget);


	/**
	 * Read title of widget help page
	 *
	 * @return
	 */
	public String readWidgetHelpPageTitle(){
		driver.switchToFirstMatchingWindowByPageTitle(this.getWidgetHelpFrame());
		driver.switchToFrame().selectSingleFrameBySelector("//frame[@name='HelpFrame']").switchToFrame().selectSingleFrameBySelector("//frame[@name='ContentFrame']").switchToFrame().selectSingleFrameBySelector("//frame[@name='ContentViewFrame']");
		String helpTitle = driver.getSingleElement(CommunitiesUIConstants.WidgetHelpPageTitle).getText();
		log.info("INFO: Title of help page is: " + helpTitle);
		return helpTitle;

	}

	/**
	 * Method to add an Invited user to a community
	 * @param addMember
	 * @author - Aditya Bhatia5/India/IBM
	 */
	public void inviteMemberCommunity(Member member) throws Exception {

		//Click Add Invited Members Button

		log.info("INFO: Clicking on Invite Member button");
		clickLink(CommunitiesUIConstants.InviteMemberButton);

		//add member
		addMember(member);

	}

	 /**
	  * selectUser
	  * @param testUser
	  * @return
	  */
	 public void selectInvitedMember(Member member){

			Element user = driver.getSingleElement("link=" + member.getUser().getDisplayName());
			String[] userId = user.getAttribute("id").split("_");
			log.info("INFO: Select the user checkbox");
			clickLinkWait("css=div[id$='" + userId[1] + "'] div[class='lotusFloat'] input");
	 }


		public void addMembers(List<Member> members) {
			//check to see if we need to open advanced options
			log.info("INFO: Check to see if we need to open advanced options");
			if(!driver.getSingleElement(CommunitiesUIConstants.CommunityMembersDropdown).isVisible()){
				log.info("INFO: Open advanced options");
				openAdvancedOptions();
			}

			for(Member member: members) {
				log.info("INFO: Adding a member to the component " + member.getUser().getDisplayName());
				driver.getSingleElement(CommunitiesUIConstants.CommunityMembersDropdown).useAsDropdown().selectOptionByVisibleText(member.getRole().toString());
				addMember(member);
			}
		}

		public void addexMembers(List<Member> exmembers) {
			for(Member exmember: exmembers) {
				log.info("INFO: Adding an external member to the component " + exmember.getUser().getEmail());
				driver.getSingleElement(CommunitiesUIConstants.CommunityMembersDropdown).useAsDropdown().selectOptionByVisibleText(exmember.getRole().toString());
				typeText(BaseUIConstants.CommunityMembersTypeAhead, exmember.getUser().getEmail());
				driver.getSingleElement(CommunitiesUIConstants.addExMemBtn).click();
				log.info("INFO: Added an external member to the component successfully");
			}
		}

		public void importMembersViaFile(BaseFile file) {

			log.info("INFO: Select Use csv file or email addresses");
			clickLinkWait(CommunitiesUIConstants.ImportMemberCSV_File);

			log.info("INFO: Now we will import the file " + file.getName());

			//start uploading file
			TestConfiguration testConfig = cfg.getTestConfig();
			if (testConfig.getBrowserEnvironment().isLocal() && !testConfig.serverIsBrowserStack() && testConfig.serverIsLegacyGrid()){
				//FileCancelCancelGrid.exe must be running on the grid machines
				try {
					Runtime.getRuntime().exec(cfg.getTestConfig().getBrowserEnvironment().getAbsoluteFilePath(cfg.getUploadFilesDir(), "FileUploadCancel.exe"));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			Element inputField = driver.getSingleElement(CommunitiesUIConstants.ImportMemberFile);

			log.info("INFO: Click on input field");
			inputField.click();

			//remove class attribute so input field becomes visible
			log.info("INFO: Execute script to make input field become visible");
			driver.executeScript("arguments[0].setAttribute('class', '');", (WebElement) inputField.getBackingObject());
			if (testConfig.serverIsLegacyGrid())  {
				inputField.typeFilePath(Data.getData().downloadsFolder + "\\" + file.getName()+ file.getExtension());
			} else {
				inputField.typeFilePath(Data.getData().downloadsFolderSelenoid + "/" +
						Utils.getThreadLocalUniqueTestName().replace(".", "_") + "/" + file.getName() + file.getExtension());
			}

			//Click on Import
			log.info("INFO: Select import");
			clickLinkWait(CommunitiesUIConstants.MemberImportButton);

		}

	public void addMemberCommunity(Member member)throws Exception {
		//Choose members from the left nav
		if (driver.isTextNotPresent("Build your Community")){
			//Then click on the Add Members button
			clickLink(CommunitiesUIConstants.AddMembersToExistingCommunity);
		}else {
			Community_LeftNav_Menu.MEMBERS.select(this);
			//Then click on the Add Members button
			clickLink(CommunitiesUIConstants.AddMembersToExistingCommunity);
		}
		/** change to using the generic add members method */
		log.info("INFO: Adding a member to the component");

		//Chose the member type and role
		log.info("INFO: Adding user role");
		driver.getSingleElement(CommunitiesUIConstants.CommunityMembersDropdown).useAsDropdown().selectOptionByVisibleText(member.getRole().toString());

		//add member
		addMember(member);

	}

	/**
	 * getMemberText
	 * @param member
	 * @return String containing the raw text of the member
	 */
	public Element getMemberElement(Member member){

		Element memberInfo = null;
		List<Element> members = driver.getElements(CommunitiesUIConstants.GenericMemberElement);

		Iterator<Element> membersList = members.iterator();
		while(membersList.hasNext()){
			memberInfo = membersList.next();
			if(memberInfo.getText().contains(member.getUser().getDisplayName())){
				log.info("INFO: Found user " + member.getUser().getDisplayName());
				break;
			}
		}
		return memberInfo;
	}

	/**
	 * removeMemberCommunity -
	 * @param member
	 */
	public void removeMemberCommunity(Member member){

		log.info("INFO: Will remove the user: " + member.getUser().getDisplayName());

		log.info("INFO: Find the member in the list of members");
		Element memberElement = getMemberElement(member);

		log.info("INFO: Select remove link of user");
		clickLinkWait("css=span[id='editremove" + memberElement.getAttribute("id") + "'] a[title='Remove']");

		log.info("INFO: Select the Ok to confirm the remove");
		clickLink(CommunitiesUIConstants.okButton);
	}

	/**
	 *
	 * @param TypeOfFile
	 * @param FileUploadName
	 * @throws Exception
	 */
	public void uploadFileFromMediaGalleryWidget(String TypeOfFile, String FileUploadName) throws Exception {

		//Click on the Upload link in the Widget
		fluentWaitPresent(CommunitiesUIConstants.MediaGalleryUploadLink);
		clickLink(CommunitiesUIConstants.MediaGalleryUploadLink);

		//Choose to add a photo or media file
		if (TypeOfFile=="New Photo"){
			clickLink(CommunitiesUIConstants.MediaGalleryUploadPhoto);
		}else if (TypeOfFile=="New Video"){
			clickLink(CommunitiesUIConstants.MediaGalleryUploadVideo);
		}

		fluentWaitPresent(CommunitiesUIConstants.MediaGalleryFileUploadFilePath);

		FilesUI fUI = FilesUI.getGui(cfg.getProductName(), driver);
		fUI.setLocalFileDetector();
		String uploadFilePath = FilesUI.getFileUploadPath(FileUploadName, cfg);
		log.info("INFO: Adding file with path" + uploadFilePath);
		driver.getSingleElement(CommunitiesUIConstants.MediaGalleryFileUploadFilePath).typeFilePath(uploadFilePath);

		//Add a Tag and Description
		typeText(CommunitiesUIConstants.MediaGalleryFileUploadTags, "tagfor" + FileUploadName);

		if(cfg.getTestConfig().browserIs(BrowserType.IE)){
			typeText(CommunitiesUIConstants.MediaGalleryFileUploadDescriptionIE, "this is a test description");
		}
		else {
			typeText(CommunitiesUIConstants.MediaGalleryFileUploadDescription, "this is a test description");
		}

		//Upload file now
		if(cfg.getTestConfig().browserIs(BrowserType.IE)) {
			clickLink("css=input[value='Upload'][class='lotusFormButton'][type='submit']");
		}else {
			clickLink(BaseUIConstants.Upload_Button);
		}
		waitForPageLoaded(driver);
		fluentWaitTextPresent(Data.getData().MGUploadMessage1);
		log.info("INFO: uploaded a file from the Upload link");

	}

	public void uploadFileFromMediaGallery(String TypeOfFile, String FileUploadName) throws Exception {
		String typeOfUpload = "css=div span a:contains("+TypeOfFile+")";
		//Click on the Upload link in the Widget
		log.info("INFO: Select Media from left navigation menu");
		Community_LeftNav_Menu.MEDIA.select(this);

		//Choose to add a photo or media file
		clickLink(typeOfUpload);
		//enter the file to upload
		fluentWaitPresent(CommunitiesUIConstants.MediaGalleryFileUploadFilePath);
		FilesUI fUI = FilesUI.getGui(cfg.getProductName(), driver);
		fUI.setLocalFileDetector();
		String uploadFilePath = FilesUI.getFileUploadPath(FileUploadName, cfg);
		driver.getSingleElement(CommunitiesUIConstants.MediaGalleryFileUploadFilePath).typeFilePath(uploadFilePath);

		//Add a Tag and Description
		typeText(CommunitiesUIConstants.MediaGalleryFileUploadTags, "tagfor" + FileUploadName);

		if(cfg.getTestConfig().browserIs(BrowserType.IE)){
			typeText(CommunitiesUIConstants.MediaGalleryFileUploadDescriptionIE, "this is a test description");
		}
		else {
			typeText(CommunitiesUIConstants.MediaGalleryFileUploadDescription, "this is a test description");
		}

		//Upload file now
		if(cfg.getTestConfig().browserIs(BrowserType.IE)) {
			clickLink("css=input[value='Upload'][class='lotusFormButton'][type='submit']");
		}else {
			clickLink(BaseUIConstants.Upload_Button);
		}
		fluentWaitTextPresent(FileUploadName+Data.getData().MGUploadMessage);
		log.info("INFO: uploaded a file from the Media Gallery widget");

	}

	public void loadUploadedFile(String fileName)throws Exception{
		clickLink("css=h4 span a.entry-title:contains("+fileName+")");
		waitForPageLoaded(driver);
		log.info("INFO: loading the Media Gallery widget");
	}

	/** Add a comment - part of media gallery test */
	public void mediaGalleryAddAComment(String fileName, String Comment)throws Exception{
		loadUploadedFile(fileName);
		clickLink("css=div ul li a:contains(Comments)");
		clickLink("css=a[dojoattachpoint='addCommentLink']");
		typeText("css=textarea[name='description']", Comment);
		clickSaveButton();
		fluentWaitTextPresent(Comment);
		log.info("INFO: comment added");
	}

	/** Edit an existing comment - part of media gallery test */
	public void mediaGalleryEditComment(String Comment)throws Exception{
		clickLink("css=div ul li a.editBtn");
		clearText("css=tbody tr td textarea[name='description']");
		typeText("css=tbody tr td textarea[name='description']", Comment);
		clickSaveButton();
		fluentWaitTextPresent(Comment);
		log.info("INFO: the comment was edited");
	}

	/** Delete an existing comment - part of media gallery test */
	public void mediaGalleryDeleteComment(String Comment)throws Exception{
		clickLink("css=div ul li a.deleteBtn");
		clickOKButton();
		waitForPageLoaded(driver);
		driver.isTextNotPresent(Comment);
		log.info("INFO: deleted the comment successfully");
	}

	/** Like the filename - part of media gallery test */
	public void mediaGalleryLike(String fileName)throws Exception{
		clickLink("css=h4 span a.entry-title:contains("+fileName+")");
		waitForPageLoaded(driver);
		clickLink("css=div span a[aria-label='Like this']");
		fluentWaitTextPresent("You like this");
		fluentWaitPresent("css=div span a[aria-label='Unlike']");
		log.info("INFO: user has liked this image");

	}

	/**
	 *  deleteMediaGalleryUpload - remove an existing file that is part of media gallery then validate that
	 *  							the file is no longer present and that you can find the file in the trash
	 * @param fileName - file you wish to remove
	 */
	public void deleteMediaGalleryUpload(String fileName)throws Exception{
		clickLink("css=h4 span a.entry-title:contains("+fileName+")");
		waitForPageLoaded(driver);
		clickLink("css=div span a[aria-label='Move to Trash']");
		clickOKButton();

		//Potential timing issue here so we check to see if the move to trash successfully is present.
		//If the message is not present validate that the file is not there.
		log.info("INFO: Checking to see if moved to the trash successfully is still present");
		if(!driver.isElementPresent("css=div[id^='quickr_lw_widget_MessageContainer']")){
			log.info("INFO: Validating " + fileName + " no longer appears in the list");
			fluentWaitTextNotPresent(fileName);
		}

		log.info("INFO: Checking the trash for " + fileName);
		clickLink("css=a[title='View the Media Gallery and Files Community Trash']");
		fluentWaitTextPresent(fileName);
	}

	/** remove the Media Gallery widget from the community - part of media gallery test */
	public void removeMediaGalleryWidget()throws Exception{
		clickLink("css=div h2 a[aria-label='Actions for: Media Gallery']");
		clickLink(CommunitiesUIConstants.menuOption+":contains(Remove)");
		clickButton("Remove");
		waitForPageLoaded(driver);
		driver.isTextNotPresent("Media Gallery");
		log.info("INFO: Media Gallery widget has being removed from the community");
	}

	public void editMediaGalleryItemProperties(String newFilename, String newTag, String newDesc)throws Exception{
		String editProperties = "css=tbody tr td ";
		clickLink("css=div span a[aria-label='Edit Properties']");
		clearText(editProperties+"input[id^='qkrLW_label_']");
		typeText(editProperties+"input[id^='qkrLW_label_']", newFilename);
		clearText(editProperties+"input[name='uploadFileTaggerTypeAhead']");
		typeText(editProperties+"input[name='uploadFileTaggerTypeAhead']", newTag);
		clearText(editProperties+"textarea[name='_description']");
		typeText(editProperties+"textarea[name='_description']", newDesc+" edited text");
		clickOKButton();
		fluentWaitTextPresent(""+newFilename+".jpg was saved successfully.");
		log.info("INFO: Edited the file properties successfully");
		fluentWaitTextPresent("Tags: "+newTag);
		fluentWaitTextPresent("Description: "+newDesc+" edited text");
		fluentWaitTextPresent(newFilename+".jpg");
		log.info("INFO: verified the edited file properties successfully");
	}

	public void replaceFile(String newFilename, String oldFilename)throws Exception{
		clickLink("css=div span a[aria-label='Replace']");
		fluentWaitPresent(CommunitiesUIConstants.MediaGalleryFileUploadFilePath);
		FilesUI fUI = FilesUI.getGui(cfg.getProductName(), driver);
		fUI.setLocalFileDetector();
		String uploadFilePath = FilesUI.getFileUploadPath(newFilename, cfg);
		driver.getSingleElement(CommunitiesUIConstants.MediaGalleryFileUploadFilePath).typeFilePath(uploadFilePath);
		clickOKButton();
		fluentWaitTextPresent(""+oldFilename+" was replaced successfully.");
		log.info("INFO: replaced the file successfully");

	}

	public void checkInCheckOutFile(int MoreLink1, int MoreLink2)throws Exception{
		String CheckIn = "css=a[title=\"Check in document\"]";
		String CheckOut = "css=a[title=\"Check out this file\"]";
		log.info("INFO: Will check out the file, verify, check in the file and verify");
		if (driver.isElementPresent(CheckOut)){
			Assert.assertTrue(driver.isElementPresent(CheckOut));
			clickLink(CheckOut);
			fluentWaitPresent("css=span.qkrDraft");
			Assert.assertTrue(driver.isElementPresent("css=span.qkrDraft"));
		}
		//expand with more link
		clickLink(CommunitiesUIConstants.LibraryMoreLink+MoreLink1);
		if (driver.isElementPresent(CheckIn)){
			fluentWaitPresent(CheckIn);
			Assert.assertTrue(driver.isElementPresent(CheckIn));
			clickLink(CheckIn);
		}
		//expand with more link
		clickLink(CommunitiesUIConstants.LibraryMoreLink+MoreLink2);
		fluentWaitPresent(CheckOut);
		Assert.assertTrue(driver.isElementPresent(CheckOut));
		log.info("INFO: Check out and check in worked as expected");
	}

	public void uploadNewVersionOfFile(String filename1, String filename2)throws Exception{
		clickLink(CommunitiesUIConstants.UploadNewVerionLink);
		FilesUI fUI = FilesUI.getGui(cfg.getProductName(), driver);
		fUI.setLocalFileDetector();
		String uploadFilePath = FilesUI.getFileUploadPath(filename2, cfg);
		driver.getFirstElement(CommunitiesUIConstants.FileUploadBrowse).typeFilePath(uploadFilePath);
		clickButton("Check In");
		fluentWaitTextPresent(filename1+" updated to version");
	}

	public void typeMessageInShareBox(String statusMessage, boolean inCommunity){
		//Enter a status update
		if(driver.isElementPresent(CommunitiesUIConstants.ShareBoxTextArea)){
			typeText(CommunitiesUIConstants.ShareBoxTextArea, statusMessage);
		}else{
			List<Element> frames = driver.getVisibleElements(BaseUIConstants.StatusUpdate_iFrame);
			int frameCount = 0;
			for(Element frame : frames){
				frameCount++;
				log.info("INFO: Frame toString: " + frame.toString());
				log.info("INFO: Frame location: " + frame.getLocation());
				//The first CK Editor iframe will be for the embedded sharebox
				if(frameCount == 1){
					log.info("INFO: Switching to Frame: " + frameCount);
					driver.switchToFrame().selectFrameByElement(frame);
				}
			}
			driver.getSingleElement(BaseUIConstants.StatusUpdate_Body).click();
			driver.getSingleElement(BaseUIConstants.StatusUpdate_Body).type(statusMessage);

			log.info("INFO: Returning to parent frame to click 'Post' button");
			driver.switchToFrame().returnToTopFrame();
			if (!inCommunity) {
				driver.switchToFrame().selectSingleFrameBySelector(CommunitiesUIConstants.ShareBoxStatusFrameIdentifer);
			}
		}
	}

	public void postStatusFromShareBox(String statusMessage){

		log.info("Posting status using the Sharebox");

		//Switch to the Sharebox frame
		log.info("INFO: Switch to Sharebox frame");
		fluentWaitPresent(CommunitiesUIConstants.ShareBoxStatusFrameIdentifer);
		driver.switchToFrame().selectSingleFrameBySelector(CommunitiesUIConstants.ShareBoxStatusFrameIdentifer);

		typeMessageInShareBox(statusMessage, false);

		clickButton(Data.getData().buttonPost);
		log.info("Posted status update '"+statusMessage+"'");

	}


	/**
	 * openCommunityLink - opens community from table community name link
	 * @param commObj
	 */
	public void openCommunityLink(BaseCommunity commObj){

		boolean found = false;
		int count=0;

		//while loop to go through a couple of times in case community has not updated from cache
		while(found==false && count <5){

			//Collect community elements from the table
			List<Element> tableElements = driver.getElements("css=table[class='lotusTable'] td[class='lotusFirstCell'] h4 a[dojoattachpoint='placeTitleLink']");

			//Iterate through list of community elements and click on the community that matches the community title
			Iterator<Element> it = tableElements.iterator();
			while(it.hasNext()){
				//get next element
			    Element tableElementUnderTest = it.next();
			    //check to see if the element matches the community title we are looking for and if so click it
				if(commObj.getName().contentEquals(tableElementUnderTest.getText())){
					tableElementUnderTest.click();
					found = true;
					break;
				}

			}
			//increment count and refresh browser
			count++;
			driver.navigate().refresh();
		}


	}

	/**
	 * Request to join a community (moderated)
	 * @param communityName
	 * @throws Exception
	 */
	public void joinCommunity(String communityName)throws Exception{
		//click on the Join a community link
		fluentWaitPresent(CommunitiesUIConstants.Join_the_Community);
		driver.executeScript("arguments[0].scrollIntoView(true);",driver.getElements(GlobalsearchUI.OpenSearchPanel).get(0).getWebElement());
		clickLinkWithJavascript(CommunitiesUIConstants.Join_the_Community);
		// Commenting out following two lines due to https://jira.cwp.pnp-hcl.com/browse/CNXSERV-9214 this known issue.
		// Jira ticket https://jira.cwp.pnp-hcl.com/browse/CNXTEST-1479 is raised to revert the changes
		/*fluentWaitTextPresent("You have joined the community and can now post content");
		log.info("INFO: request to join community has being successful");*/

	}

	/**
	 *
	 * @param ApprovalType
	 * @param option
	 * @param Text
	 */
	public void selectTreeOption(String ApprovalType, String option){


			List<Element>  appType = driver.getElements("css=div[id^='lconn_moderation_scenes__NavigationTreeNode_']");

			log.info("INFO: here " + appType.size());

			Element type = getTreeMenuContains(appType, ApprovalType);

		    List<Element>  app = driver.getElements("css=div[id='"+ type.getAttribute("id")+"'] div[id^='lconn_moderation_scenes__NavigationTreeNode_']");

		    log.info("INFO: here " + app.size());

		    Element selection = getTreeMenuContentEquals(app , option);

	    	clickLinkWait("css=div[id='"+ selection.getAttribute("id") + "'] div span span:contains(" + option + ")");


	}

	private Element getTreeMenuContains(List<Element> treeMenus, String sMenuName) {

		Element lookingFor = null;

		//Iterate through list of elements
		Iterator<Element> it = treeMenus.iterator();
		while(it.hasNext()){
			//get next element
		    Element tableElementUnderTest = it.next();
		    //check to see if the element matches the community title we are looking for and if so click it
		    if(driver.getSingleElement("css=div[id='" + tableElementUnderTest.getAttribute("id")+"']").getText().contains(sMenuName)){
			    log.info("INFO: id  "+ tableElementUnderTest.getAttribute("id"));
		    	lookingFor = tableElementUnderTest;
		    	break;
		    }
		}

		return lookingFor;
	}

	private Element getTreeMenuContentEquals(List<Element> treeMenus, String sMenuName) {

		Element lookingFor = null;

		//Iterate through list of elements
		Iterator<Element> it = treeMenus.iterator();
		while(it.hasNext()){
			//get next element
		    Element tableElementUnderTest = it.next();
		    //check to see if the element matches the community title we are looking for and if so click it
		    if(driver.getSingleElement("css=div[id='" + tableElementUnderTest.getAttribute("id")+"']").getText().contentEquals(sMenuName)){
			    log.info("INFO: id  "+ tableElementUnderTest.getAttribute("id"));
		    	lookingFor = tableElementUnderTest;
		    	break;
		    }
		}

		return lookingFor;
	}

	/**
	 * approveItems - Approve moderated items
	 */
	public void approveItems(){

		// Approve the items
		log.info("INFO: Select all entries");
		clickLinkWait(CommunitiesUIConstants.SelectAllEntries);

		// Click Approve
		log.info("INFO: Select approve button");
		clickLinkWait(CommunitiesUIConstants.Approve);

		// click Yes to warning message
		log.info("INFO: Select ok button");
		clickLinkWait(CommunitiesUIConstants.OKButton);

		log.info("INFO: Validate items have been approved");
		fluentWaitTextPresent(Data.getData().ApproveSuccessMsg);
	}

	public void createNewFolderForLibrary(String Foldername, String Description)throws Exception{
		log.info("INFO: Create a new folder in the library");
		clickLink(CommunitiesUIConstants.NewFolderButton);
		fluentWaitTextPresent("*Folder name:");
		driver.getSingleElement("css=input#qkrLW_label_0").clear();
		driver.getSingleElement("css=input#qkrLW_label_0").type(Foldername);
		driver.getSingleElement("css=textarea#qkrLW_label_1").type(Description);
		clickButton("OK");
		fluentWaitTextPresent(Foldername+" created successfully.");
		log.info("INFO: New folder has being added");
	}

	/**
	 * create() - method to create an community using a community base state object
	 * @param BaseCommunity community
	 */
	public void create(BaseCommunity community){

		waitForSameTime();

		//Click Start A Community
		log.info("INFO: Create a new community using Start A Community button");
		this.driver.getSingleElement(CommunitiesUIConstants.StartACommunity).click();

		createCommunity(community);
	}

	/**
	 * create() - method to create an community using a community base state object
	 * @param BaseCommunity community
	 */
	public void createFromDropDown(BaseCommunity community){

		waitForSameTime();

		//Click Start A Community
		log.info("INFO: Create a new community using the Start A Community Drop Down");
		this.driver.getSingleElement(CommunitiesUIConstants.StartACommunityDropDown).click();
		this.driver.getSingleElement(CommunitiesUIConstants.StartACommunityFromDropDown).click();

		createCommunity(community);
	}

	public void createFromDropDownCardView(BaseCommunity community) {

		waitForSameTime();

		// Click Start A Community (card view)
		log.info("INFO: Create a new community using the Start A Community Drop Down in the Card View");
		this.driver.getSingleElement(CommunitiesUIConstants.StartACommunityDropDownCardView).click();
		this.driver.getSingleElement(CommunitiesUIConstants.StartACommunityFromDropDownCardView).click();

		createCommunity(community);
	}

	public void createCommunity(BaseCommunity community){

		//Wait for Community page to load
		fluentWaitPresent(CommunitiesUIConstants.CommunityName);

		//Enter Name of Community
		log.info("INFO: Entering community name " + community.getName());
		this.driver.getSingleElement(CommunitiesUIConstants.CommunityName).type(community.getName());

		//Choose Type of Community default public
		log.info("INFO: Selecting the type of Community "+ community.getAccess().toString());
		this.driver.getSingleElement(community.getAccess().toString()).click();

		//If access level restricted, check restricted but listed
		if(community.getAccess() == Access.RESTRICTED && community.getRbl()) {
			this.driver.getSingleElement(CommunitiesUIConstants.ListRestrictedCommunity).click();
		}

		//Check to see if community shared externally **cloud only option**
		log.info("INFO: Check to see if community is to be shared Externally");
		allowShareExternal(community);

		if((community.getAccess() == Access.RESTRICTED) && community.getExternalUserAccess())
		{
			clickLink(CommunitiesUIConstants.CommunityallowExternalBox);
		}

		//Enter the description in CKEditor
		log.info("INFO: Entering a description if we have one");
		if(community.getDescription() != null) {
			typeInCkEditor(community.getDescription());
		}

		//Enter Community Tag if one was provided
		if(community.getTags() != null){
			log.info("INFO: Entering any community tags");
			this.driver.getSingleElement(CommunitiesUIConstants.CommunityTag).type(community.getTags());
		}

		//Adding Members if we have any to add
		log.info("INFO: Adding members if we have any to add");
		addMembers(community.getMembers());

		//Adding Members if we have any to add
		log.info("INFO: Adding external members if we have any to add");
		addexMembers(community.getexMembers());

		//Enter Community Handle if one was provided
		if(community.getHandle() != null){
			addHandle(community.getHandle());
		}

		//If product is onprem check to see if a theme was provided
		log.info("INFO: Entering a theme for the community if selected");
		if (community.getTheme() !=null){
			addTheme(community);
		}

		//If community is moderated select moderation to be enabled
		log.info("INFO: Check if community should be moderated");
		if (community.isApprovalRequired() == true){
			this.driver.getSingleElement(CommunitiesUIConstants.ContentApproval_Checkbox).click();
		}

		//Save the community
		log.info("INFO: Saving the community " + community.getName());
		Element saveBtn = driver.getSingleElement(CommunitiesUIConstants.SaveButton);
		driver.executeScript("arguments[0].scrollIntoView(true)", saveBtn.getWebElement());
		saveBtn.click();

		fluentWaitTextPresent(community.getName());

		// Get url with params after UUID removed
		String webUrl = this.driver.getCurrentUrl().split("&")[0];
		community.setWebAddress(webUrl);
		// Strip the UUID out of the weburl, left communityUuid= for legacy
		community.setCommunityUUID(webUrl.split("\\?")[1].split("#")[0]);

		log.info("INFO: " + community.getName() + " was created successfully");

	}


	/**
	 * Method to create a community using a community base state object
	 * @param community
	 * @param logger
	 */
	public void createCommunityFromTailoredExperienceWidget(BaseCommunity community, DefectLogger logger){

		waitForSameTime();

		//Click Start A Community
		log.info("INFO: Create a new community using the Start A Community Drop Down");
		this.driver.getSingleElement(CommunitiesUIConstants.StartACommunityDropDown).click();
		this.driver.getSingleElement(CommunitiesUIConstants.StartACommunityFromDropDown).click();
		verifyBlankCommunityNameValidation(logger);
		String communityName=community.getName();
		String communityDescription=community.getDescription();
		String communityType=community.getAccess().toString(); //public or moderate or restricted or restrictedHidden
		String testTag=community.getTags();
		String commWebAddress=community.getWebAddress();
		String fileUploadPath= community.getCommunityImage();
		List<Member> communityMemberList = community.getMembers();
		String templateName = community.getTemplate();
		String communityCatagory = community.getClass().getName();
		createCommunityWidgetTE(logger, communityName, communityDescription, communityType, testTag,
				commWebAddress, communityMemberList, fileUploadPath, communityCatagory, templateName);
	}

	/**
	 * Method to create a subcommunity using a community base state object
	 * @param subCommunity
	 * @param logger
	 */
	public void createSubCommunityFromTailoredExperienceWidget(BaseSubCommunity subCommunity, BaseCommunity community, DefectLogger logger){

		waitForSameTime();

		//Choose the method to create SubCommunity default actions menu
		logger.strongStep("INFO: Create SubCommunity " + subCommunity.getName());
		log.info("INFO: Create SubCommunity " + subCommunity.getName());
		if(subCommunity.getUseActionMenu()){
			logger.strongStep("INFO: Using action menu to create the SubCommunity");
			log.info("INFO: Using action menu to create the SubCommunity");
			Com_Action_Menu.CREATESUB.select(this);

		}
		else{
			logger.strongStep("INFO: Click Close link to close the Add Apps dialog");
			log.info("INFO: Click Close link to close the Add Apps dialog");
			this.driver.getSingleElement(CommunitiesUIConstants.CloseWidget).click();

			logger.strongStep("INFO: Using link from widget to create the SubCommunity");
			log.info("INFO: Using link from widget to create the SubCommunity");
			clickLinkWait(CommunitiesUIConstants.SubcommunityLinkAfterAddApp);
		}

		//Wait for Community page to load - Tailored Experience Create community widget
		fluentWaitPresent(CommunitiesUIConstants.communityNameTE);
		String communityName=subCommunity.getName();
		String communityDescription=subCommunity.getDescription();
		String communityType=subCommunity.getAccess().toString(); //public or moderate or restricted or restrictedHidden
		String testTag=subCommunity.getTags();
		String commWebAddress=subCommunity.getWebAddress();
		List<Member> communityMemberList = community.getMembers();
		String fileUploadPath=subCommunity.getCommunityImage();
		String communityCatagory = subCommunity.getClass().getName();
		String templateName = subCommunity.getTemplate();
		createCommunityWidgetTE(logger, communityName, communityDescription, communityType, testTag, commWebAddress,
				communityMemberList, fileUploadPath, communityCatagory, templateName);
	}

	/**
	 * This method takes input to create/update a community using community widget
	 * @param communityMemberList
	 * @param commWebAddress
	 * @param testTag
	 * @param communityType
	 * @param communityDescriptionTE
	 * @param communityNameTE
	 * @param logger
	 * @param fileUploadPath
	 */
	public void createCommunityWidgetTE(DefectLogger logger, String commuName, String commuDescription,
			String communityType, String testTag, String commWebAddress,
			List<Member> communityMemberList, String fileUploadPath, String communityCatagory, String templateName){

		logger.strongStep("INFO: Creating a community with name: "+ commuName);
		log.info("INFO: Creating a public community with name: "+ commuName);
		getFirstVisibleElement(CommunitiesUIConstants.communityNameTE).type("");
		getFirstVisibleElement(CommunitiesUIConstants.communityNameTE).type(commuName);

		if(commuDescription!=null){
			logger.strongStep("INFO: Add description: " + commuDescription);
			log.info("INFO: Add description: " + commuDescription);
			getFirstVisibleElement(CommunitiesUIConstants.communityDescriptionTE).type("");
			getFirstVisibleElement(CommunitiesUIConstants.communityDescriptionTE).type(commuDescription);
		}

		if(communityType!=null){
            logger.strongStep("INFO: Creating this community with type: "+ communityType);
            log.info("INFO: Creating this community with type: "+ communityType);
            if(communityType.equals(BaseCommunity.Access.PUBLIC.commType)||communityType.equals(BaseSubCommunity.Access.PUBLIC.commType))
                getFirstVisibleElement(CommunitiesUIConstants.communityTypePublic).click();
            else if(communityType.equals(BaseCommunity.Access.MODERATED.commType)||communityType.equals(BaseSubCommunity.Access.MODERATED.commType))
                getFirstVisibleElement(CommunitiesUIConstants.communityTypeModerate).click();
            else if(communityType.equals(BaseCommunity.Access.RESTRICTED.commType)||communityType.equals(BaseSubCommunity.Access.RESTRICTED.commType))
                getFirstVisibleElement(CommunitiesUIConstants.communityTypeRestricted).click();
            else if(communityType.equals(BaseSubCommunity.Access.RESTRICTEDHIDDEN.commType)){
                getFirstVisibleElement(CommunitiesUIConstants.communityTypeRestricted).click();
                getFirstVisibleElement(CommunitiesUIConstants.communityTypeRestrictedHidden).click();
            }

        }

		if(fileUploadPath!=null){
			logger.strongStep("INFO: Upload image for Community Logo");
			log.info("INFO: Upload image for Community Logo");
			clickLinkWait(CommunitiesUIConstants.uploadCommunityLogo);
			FilesUI fUI = FilesUI.getGui(cfg.getProductName(), driver);
			fUI.setLocalFileDetector();
			driver.getSingleElement(CommunitiesUIConstants.chooseFile).typeFilePath(fileUploadPath);
		}

		getFirstVisibleElement(CommunitiesUIConstants.proceedToNextStep).click();

		if (templateName.equalsIgnoreCase("")) {
			fluentWaitElementVisible(CommunitiesUIConstants.defaultCommunityTemplate);
			logger.strongStep("INFO: Select default template and click NEXT");
			log.info("INFO: Select default template and click NEXT");
			clickLinkWait(CommunitiesUIConstants.defaultCommunityTemplate);
		}else{
			logger.strongStep("INFO: Search for community template, select it and click NEXT");
			log.info("INFO: Search for community template, select it and click NEXT");
			clickLinkWait(CommunitiesUIConstants.searchForCommunityTemplate);
			getFirstVisibleElement(CommunitiesUIConstants.searchForCommunityTemplate).type(templateName);
			clickLinkWait(CommunitiesUIConstants.searchTemplateIcon);
			clickLinkWait(getTemplateName(templateName));
		}

		clickLinkWait(CommunitiesUIConstants.proceedToNextStep);

		if(communityMemberList!=null && !communityMemberList.isEmpty()){
				if(!communityCatagory.contains("BaseSubCommunity")){
					fluentWaitElementVisible(CommunitiesUIConstants.searchMemberToAdd);
					logger.strongStep("INFO: Adding members to the community");
					log.info("INFO: Adding members to the community");
					List<String> addedMembersList = new ArrayList<String>();	//To store user display names
					for(Member addedMember : communityMemberList){
						Assert.assertTrue(selectMemberForCommunity(addedMember.getUser().getDisplayName()), "Unable to select "
								+ "user " + addedMember.getUser().getDisplayName() + " from listbox");
						addedMembersList.add(addedMember.getUser().getDisplayName().toLowerCase());
					}

					clickLinkWait(CommunitiesUIConstants.addMember);

					logger.strongStep("Verify all members are added successfully to the community");
					log.info("Verify all members are added successfully to the community");
					Assert.assertTrue(verifySelectedMembersForCommunity(addedMembersList),
							"Added members are not matching with the selected members");
				}
				else{
					fluentWaitElementVisible(CommunitiesUIConstants.searchMemberFromParentCommunity);
					logger.strongStep("Add members from parent community");
					for(Member item : communityMemberList){
						String userDisplayName=item.getUser().getDisplayName();
						//fix user name - convert it to camel case
						userDisplayName=userDisplayName.replace(userDisplayName.charAt(0), Character.toUpperCase(userDisplayName.charAt(0)));
						userDisplayName=userDisplayName.replace(userDisplayName.charAt(userDisplayName.indexOf(" ")+1), Character.toUpperCase(userDisplayName.charAt(userDisplayName.indexOf(" ")+1))).trim();
						log.info("Select member " + userDisplayName + " from parent community to be added to the subcommunity");
						driver.getSingleElement("xpath=//td[text()='"+ userDisplayName + "']//parent::tr/td[1]//input").click();
			        }
				}
		}
		clickLinkWithJavascript(CommunitiesUIConstants.proceedToNextStep);

		if(testTag!=null){
			logger.strongStep("INFO: Add tag " + testTag);
			log.info("INFO: Add tag " + testTag);
			getFirstVisibleElement(CommunitiesUIConstants.communityTags).type(testTag);
			driver.typeNative(Keys.ENTER);

			fluentWaitElementVisible(CommunitiesUIConstants.addedTag);
			Assert.assertEquals(getElementText(CommunitiesUIConstants.addedTag), testTag, testTag + "is not added to the community");
		}

		if(commWebAddress!=null && !communityCatagory.contains("BaseSubCommunity")){
			logger.strongStep("INFO: Add community web address " + commWebAddress);
			log.info("INFO: Add community web address " + commWebAddress);
			getFirstVisibleElement(CommunitiesUIConstants.communityWebAddress).type(commWebAddress);
		}

		clickLinkWait(CommunitiesUIConstants.ftCreateCommunity);
		logger.strongStep("INFO: Click on continue editing on create community alert and verify user is redirected to the Optional Settings tab");
		log.info("INFO: Click on continue editing on create community alert and verify user is redirected to the "
				+ "Optional Settings tab");
		fluentWaitElementVisible(CommunitiesUIConstants.createCommunityAlert);
		clickLinkWait(CommunitiesUIConstants.continueEditing);
		fluentWaitElementVisible(CommunitiesUIConstants.communityTags);
		clickLinkWait(CommunitiesUIConstants.ftCreateCommunity);
		fluentWaitElementVisible(CommunitiesUIConstants.createCommunityAlert);
		logger.strongStep("INFO: Click create community and verify success message");
		log.info("INFO: Click create community and verify success message");
		clickLinkWait(CommunitiesUIConstants.createCommunity);

		log.info("Wait up to " + TimeUnit.MILLISECONDS.toSeconds(cfg.getTestConfig().getImplicitWait()) + " secs for the loading icon to disppear.");
		// need to turn off driver implicit wait to let WebDriverWait control the wait time
		driver.turnOffImplicitWaits();
		WebDriverWait wait = new WebDriverWait((WebDriver)driver.getBackingObject(), cfg.getTestConfig().getImplicitWait());
		wait.until(ExpectedConditions.invisibilityOfElementLocated(
				By.cssSelector("svg.MuiCircularProgress-svg")));
		driver.turnOnImplicitWaits();
	}

	/**
	 * @param logger
	 */
	public void verifyBlankCommunityNameValidation(DefectLogger logger){

		fluentWaitPresent(CommunitiesUIConstants.communityNameTE);

		logger.strongStep("INFO: Click on 'NEXT' without entering community name");
		log.info("INFO: Click on 'NEXT' without entering community name");
		clickLinkWait(CommunitiesUIConstants.proceedToNextStep);
		Assert.assertTrue(isElementPresent(CommunitiesUIConstants.missingCommunityWarning),"A warning icon is not displayed on the vertical"
				+ " grid next to Community Details label");

		logger.strongStep("INFO: Click on 'BACK' link");
		log.info("INFO: Click on 'BACK' link");
		clickLinkWait(CommunitiesUIConstants.backToPrevStep);

		Assert.assertTrue(isElementPresent(CommunitiesUIConstants.emptyCommunityNameErrorText),"Error message 'Community name can't be empty' is "
				+ " not displayed below community name input field");
	}

	/**
	 * Get list of all members user are selected to add to the community and compare with selected members
	 * @param addedMembers
	 * @return true/false
	 */
	public boolean verifySelectedMembersForCommunity(List<String> addedMembers) {
		List<String> membersList = new ArrayList<String>();
		List<Element> items = driver.getElements(CommunitiesUIConstants.addedMemberListTable + ">tbody>tr");
        for(Element item : items.subList(1, items.size())){
        	List<Element> cols = item.getElements("css=td");
	        // get cell text with getText() from 2nd column(1st index) as first column(0th index) is dedicated to the profile picture
	        membersList.add(cols.get(1).getText().toLowerCase());
        }
        if(membersList.equals(addedMembers))
        	return true;
        else
        	return false;
	}

	/**
	 * Select given member from listbox/member search dropdown. If not found return false
	 * @param logger
	 * @param testUser
	 * @return true/false
	 */
	public boolean selectMemberForCommunity(String testUser) {
		//logger.strongStep("INFO: Add member " + testUser + " to the community");
		log.info("INFO: Add member " + testUser + " to the community");
		getFirstVisibleElement(CommunitiesUIConstants.searchMemberToAdd).type(testUser);
		driver.changeImplicitWaits(5);
		this.fluentWaitElementVisible(CommunitiesUIConstants.memberListPopup);
		List<Element> items = driver.getElements(CommunitiesUIConstants.memberListPopup + ">li");
		if(items.size()==0){
			driver.getFirstElement(CommunitiesUIConstants.memberListPopup + ">div>span").click();
			this.fluentWaitElementVisible(CommunitiesUIConstants.memberListPopup);
			items = driver.getElements(CommunitiesUIConstants.memberListPopup + ">li");
		}
		driver.turnOnImplicitWaits();
        for(Element item : items){
        	List<Element> cols = item.getElements("css=span>div");
        	// get cell text with getText()
	        if(cols.get(0).getText().toLowerCase().trim().equals(testUser.toLowerCase().trim())){
	        	cols.get(0).click();
	        	return true;
	        }
        }
        return false;
	}

	/**
	 * create() - method to create an community using a community base state object
	 * @param BaseCommunity community
	 * @throws Exception
	 */
	public void createSubCommunity(BaseSubCommunity subCommunity){

		//Choose the method to create SubCommunity default actions menu
		log.info("INFO: Create SubCommunity " + subCommunity.getName());
		if(subCommunity.getUseActionMenu()){
			log.info("INFO: Using action menu to create the SubCommunity");
			Com_Action_Menu.CREATESUB.select(this);
		}else{

			log.info("INFO: Click Close link to close the Add Apps dialog");
			this.driver.getSingleElement(CommunitiesUIConstants.CloseWidget).click();

			log.info("INFO: Using link from widget to create the SubCommunity");
			clickLinkWait(CommunitiesUIConstants.SubcommunityLinkAfterAddApp);
		}

		//Wait for Community page to load
		fluentWaitElementVisible(CommunitiesUIConstants.CommunityName);

		//Enter Name of Community
		log.info("INFO: Entering community name " + subCommunity.getName());
		this.clickLink(CommunitiesUIConstants.CommunityName);
		this.driver.getSingleElement(CommunitiesUIConstants.CommunityName).clear();
		this.driver.getSingleElement(CommunitiesUIConstants.CommunityName).type(subCommunity.getName());

		if(this.driver.isElementPresent(CommunitiesUIConstants.AccessRequired)) {
			//Choose Type of Community default public
			log.info("INFO: Selecting the type of Community "+ subCommunity.getAccess().toString());
			this.driver.getSingleElement(subCommunity.getAccess().toString()).click();
		}

		if(subCommunity.getRbl()) {
			this.driver.getSingleElement(CommunitiesUIConstants.ListRestrictedSubCommunity).click();
		}

		//Enter Community Tag if one was provided
		if (subCommunity.getTags() != null){
			log.info("INFO: Entering any community tags");
			this.driver.getSingleElement(CommunitiesUIConstants.SubCommunityTag).type(subCommunity.getTags());
		}

		//Adding Members if we have any to add
		log.info("INFO: Adding members if we have any to add");
		addMembers(subCommunity.getMembers());

		//Adding Members through CheckBox
		if(subCommunity.isUseParentMembers()) {
			this.driver.getFirstElement(CommunitiesUIConstants.AddMemberscheckbox).click();
		}

		//Adding Members if we have any to add
		log.info("INFO: Adding external members if we have any to add");
		addexMembers(subCommunity.getexMembers());

		//Enter Community Handle if one was provided
		if(subCommunity.getHandle() != null){
			addSubHandle(subCommunity.getHandle());
		}

		//Enter the description in CKEditor
		log.info("INFO: Entering a description if we have one");
		if(subCommunity.getDescription() != null) {
			typeInCkEditor(subCommunity.getDescription());
		}

		//Save the sub community
		log.info("INFO: Saving the sub community " + subCommunity.getName());
		this.driver.getSingleElement(CommunitiesUIConstants.SaveButton).click();

		//
        // needed for CR4, add the uuid to the subCommunity, this is needed to locate
	    // the card in the Catalog view
		//
		if(checkGKSetting(Data.getData().gk_catalog_card_view)){

           fluentWaitTextPresent(subCommunity.getName());

		   // Get url with params after UUID removed
		   String webUrl = this.driver.getCurrentUrl().split("&")[0];

		   // Strip the UUID out of the weburl, left communityUuid= for legacy
		   subCommunity.setCommunityUUID(webUrl.split("\\?")[1]);

		   log.info("INFO: " + subCommunity.getName() + " was created successfully");
		}
	}

	/**
	 * Delete an open community
	 * @param community community deleting
	 * @param userDelete user who is deleting the community
	 */
	public void delete(BaseCommunity community, User userDelete){

		//Select Delete from communities Menu
		log.info("INFO: Selecting the delete community option from menu");
		try {
			scrollIntoViewElement(BaseUIConstants.Community_Actions_Button);
			Com_Action_Menu.DELETE.select(this);
		} catch (Exception e) {
			log.info("ERROR: Unable to use the community action menu properly");
			e.printStackTrace();
		}

		//Enter the community name
		log.info("INFO: Enter the name of the community to delete");
		fluentWaitElementVisible(CommunitiesUIConstants.commDeleteName);
		clickLinkWithJavascript(CommunitiesUIConstants.commDeleteName);
		this.typeText(CommunitiesUIConstants.commDeleteName, community.getName());

		//Enter User that is deleting the community
		log.info("INFO: Enter the name of the user who is deleting the community");
		fluentWaitElementVisible(CommunitiesUIConstants.commDeleteUser);
		this.typeText(CommunitiesUIConstants.commDeleteUser, userDelete.getDisplayName());

		//Ensure we wait a moment for the delete button to by visible
		log.info("INFO: Ensure that delete button is visible");
		this.fluentWaitElementVisible(CommunitiesUIConstants.commDeleteButton);

		//Select the Delete button
		log.info("INFO: Clicking on the delete button to delete the community");
		//Verify the button is enabled before clicking with JavaScript
		Assert.assertTrue(driver.getFirstElement(CommunitiesUIConstants.commDeleteButton).isEnabled(),
						  "ERROR: The button to delete a community is not enabled");
		//Use JS click to work around the button being off screen in FF 32
		this.clickLinkWithJavascript(CommunitiesUIConstants.commDeleteButton);
		waitForPageLoaded(driver);

		//Wait for popup to disappear
		log.info("INFO: Waiting for popup to disappear");
		fluentWaitTextNotPresent(Data.getData().deleteCommunityWarning);
	}

	/**
	 * Delete a copy community
	 * @param copyCommunityName community deleting
	 * @param userDelete user who is deleting the community
	 */
	public void deleteCopyCommunity(String copyCommunityName, User userDelete){

		//Select Delete from communities Menu
		log.info("INFO: Selecting the Delete Community option from Community Actions menu");
		try {
			Com_Action_Menu.DELETE.select(this);
		} catch (Exception e) {
			log.info("ERROR: Unable to use the community action menu properly");
			e.printStackTrace();
		}

		//Enter the community name
		log.info("INFO: Enter the name of the community to delete");
		fluentWaitElementVisible(CommunitiesUIConstants.commDeleteName);
		this.typeText(CommunitiesUIConstants.commDeleteName, copyCommunityName);

		//Enter User that is deleting the community
		log.info("INFO: Enter the name of the user who is deleting the community");
		this.typeText(CommunitiesUIConstants.commDeleteUser, userDelete.getDisplayName());

		//Ensure we wait a moment for the delete button to be visible
		log.info("INFO: Ensure that delete button is visible");
		this.fluentWaitElementVisible(CommunitiesUIConstants.commDeleteButton);

		//Select the Delete button
		log.info("INFO: Clicking on the Delete button to delete the community");
		//Verify the button is enabled before clicking with JavaScript
		Assert.assertTrue(driver.getFirstElement(CommunitiesUIConstants.commDeleteButton).isEnabled(),
						  "ERROR: The button to delete a community is not enabled");
		//Use JS click to work around the button being off screen in FF 32
		this.clickLinkWithJavascript(CommunitiesUIConstants.commDeleteButton);
		waitForPageLoaded(driver);
		//Wait for popup to disappear
		log.info("INFO: Waiting for Move to Trash popup to disappear");
		fluentWaitTextNotPresent(Data.getData().deleteCommunityWarning);

	}

	/**
	 * Delete an open community
	 * @param community community deleting
	 * @param userDelete user who is deleting the community
	 */
	public void deleteSubCommunity(BaseSubCommunity community, User userDelete){

		log.info("INFO: Delete a sub community " + community.getName());

		//Select Delete from communities Menu
		log.info("INFO: Selecting the delete community option from menu");
		try {
			Com_Action_Menu.DELETE.select(this);
		} catch (Exception e) {
			log.info("ERROR: Unable to use the community action menu properly");
			e.printStackTrace();
		}

		//Enter the community name
		log.info("INFO: Enter the name of the sub community to delete");
		this.typeText(CommunitiesUIConstants.commDeleteName, community.getName());

		//Enter User that is deleting the community
		log.info("INFO: Enter the name of the user who is deleting the community");
		this.typeText(CommunitiesUIConstants.commDeleteUser, userDelete.getDisplayName());

		//Ensure we wait a moment for the delete button to by visible
		log.info("INFO: Ensure that delete button is visible");
		this.fluentWaitElementVisible(CommunitiesUIConstants.commDeleteButton);

		//Select the Delete button
		log.info("INFO: Clicking on the delete button to delete the community");
		this.clickLink(CommunitiesUIConstants.commDeleteButton);

	}

	public void gotoStartACommunity() {
		String loadCreateFormURL = cfg.getTestConfig().getBrowserURL() + Data.getData().createCommunity;
		log.info("INFO: Click Start a community");
		clickLink(CommunitiesUIConstants.StartACommunity);

		try{
			fluentWaitPresent(CommunitiesUIConstants.CommunityName);
		}catch (Exception e){
			log.warn("Click did not open the form as expected so loading the form using the url");
			driver.navigate().to(loadCreateFormURL);
			fluentWaitPresent(CommunitiesUIConstants.CommunityName);
		}
	}

	public boolean communityExist(String name) {
		return driver.isElementPresent(getCommunitySelector(name));
	}

	public void openCommunity(String name) {

		clickLink(getCommunitySelector(name));
		try{
			fluentWaitTextPresent("Community Description");
		}catch (Exception e){
			log.warn("Click did not open the community as expected so trying javascript click");
			clickLinkWithJavascript(getCommunitySelector(name));
			fluentWaitTextPresent("Community Description");
		}
	}

	public void gotoMediaGallery() {
		clickLinkWithJavascript(CommunitiesUIConstants.leftNavMediaGallery);
	}

	public void gotoUploadPhoto() {
		clickLinkWithJavascript(CommunitiesUIConstants.UploadPhoto);
	}

	public void gotoMembers() {
		Community_LeftNav_Menu.MEMBERS.select(this);
	}

	public boolean viewGallery(String name) {

		log.info("INFO: View phote file " + name);

		try{
			// verify for it
			log.info("INFO: Select View all");
			clickLinkWait("//a[contains(text(),'View All (1)')]");
			return driver.isTextPresent(name);

		}catch (Exception e){
			log.warn("Click did not open the community as expected so trying javascript click");
			clickLinkWithJavascript(getCommunitySelector(name));

			return false;
		}
	}

	public void clickCreateCommunityActivityButton() {
		clickLink(CommunitiesUIConstants.createCommunityActivityButton);
	}

	public void gotoCommunityFromSubcommunity(String communityName) {
		fluentWaitTextPresent("Build your Community");
		clickLink("div.lotusMenuSection h3 div.communityLink a:contains("+communityName+")");
	}

	public void gotoActivity(String activityName) {
		clickLink("div h4 a:contains("+activityName+")");
	}

	public void clickActivityMenu() {
		fluentWaitTextPresent("Mark Activity Complete");
		clickLink(CommunitiesUIConstants.CommActivityActionsMenu);
	}

	public void clickActivityMenuMoveActivity() {
		clickActivityMenu();
		fluentWaitTextPresent("Move Activity");
		clickLink(CommunitiesUIConstants.CommActivityActionsMenu_MoveActivity);
	}

	public void clickMoveActivitySelectCommunity(String communityName) {
		// move activity dialog: select sub community to move activity to
		fluentWaitTextPresent("where you want to move the Activity");
		clickLink("div.lotusDialog form li:contains("+communityName+")");
	}

	public void clickMoveActivitySelectAllMembers() {
		// move activity dialog: select add all members to the sub community
		fluentWaitTextPresent("Select All");
		clickLink(CommunitiesUIConstants.CommActivity_moveActivity_selectAll);
	}

	public void clickMoveActivityOkButton() {
		// move activity dialog: click ok (move the activity)
		clickLink(CommunitiesUIConstants.CommActivity_moveActivity_OkButton);
	}

	public void gotoSubCommunity(String subcommunityName) {
		clickLink("div.lotusMenuSection a:contains("+subcommunityName+")");
	}

	public void gotoSubCommunityAllMembers() {
		// click view all members
		fluentWaitTextPresent("View all");
		clickLink(CommunitiesUIConstants.CommunityMembersViewAll); // note, members in sub-community are not accessed from the navigation bar
	}

	public void searchCommunities(BaseCommunity community) {
		searchCommunities(community.getName());
	}

	public void searchCommunities(String communityName) {
		log.info("Search for " + communityName);
		String gk_flag = "search-history-view-ui";

		//GateKeeper check for new search panel vs old search control
		log.info("INFO: Check to see if the Gatekeeper " + gk_flag + " setting is enabled");
		GatekeeperConfig gkc = GatekeeperConfig.getInstance(driver);
		boolean value = gkc.getSetting(gk_flag);
		log.info("INFO: Gatekeeper flag " + gk_flag + " is " + value );
		
		if(value){			
			log.info("Open common search panel");
			clickLinkWait(GlobalsearchUI.OpenSearchPanel);
			fluentWaitPresent(GlobalsearchUI.TextAreaInPanel);
			typeText(GlobalsearchUI.TextAreaInPanel, communityName);
			clickLink(GlobalsearchUI.SearchButtonInPanel);

		}else{
		driver.getSingleElement(CommunitiesUIConstants.SearchTextArea).type(communityName);
		driver.getSingleElement(CommunitiesUIConstants.SearchButton).click();
		log.info("Search preformed for " + communityName);
		}
	}

	/**
	 * getCommSearchResultsTabName - this method will return widget tab selector name from the communities search results page
	 */
	public String getCommSearchResultsTabName (String tabName){
		return "css=li[id$='_TabItem'] a:contains(" + tabName + ")";
	}

	public String getNavOrgName() {
		return getOrganizationName(driver);
	}

	/**
	 * getOrganizationName - Will return either organization name for cloud or My Organization for onPrem.
	 * 	This helps with /allcommunities view name displayed in navigation panel.
	 * @return String
	 * @author - Ralph LeBlanc
	 */
	public static String getOrganizationName(RCLocationExecutor driver){
		String orgName="";
		//NOTE: As of 6/11/15 onPrem changed to div#lotusLogo for IBM Connections but cloud still uses selector below for company
		if(driver.getElements("css=div.lotusRightCorner div.lotusInner ul.lotusInlinelist li a").size()!=0){
			 orgName = driver.getFirstElement("css=div.lotusRightCorner div.lotusInner ul.lotusInlinelist li a").getText();
		} else {
			//NOTE: Adding logic to deal with new Nav bar
			orgName = driver.getSingleElement("css=a[class='org _myorg']").getText();
		}

		if (orgName.contentEquals(""))
			orgName = "My Organization";
		return orgName;
	}

	public void setCommunityApproveContent(BaseCommunity community)
	{

		Com_Action_Menu.EDIT.select(this);

		fluentWaitPresent(CommunitiesUIConstants.ModerateApproveOption);
		clickLink(CommunitiesUIConstants.ModerateApproveOption);

		//Save the community
		log.info("INFO: Saving the community " + community.getName());
		this.driver.getSingleElement(CommunitiesUIConstants.SaveButton).click();

	}

	/**
	 * @author Patrick Doherty - DOHERTYP@ie.ibm.com
	 * @param community The community to which the update will be posted
	 * @param newMessage The message to be posted
	 */
	public void postCommunityStausUpdate (BaseCommunity community, String newMessage){

		clickLinkWait(BaseUIConstants.Im_Owner);
		waitForPageLoaded(driver);

		if(!driver.isElementPresent("link=" + community.getName())){
			clickLinkWait(BaseUIConstants.Im_Owner);
			waitForPageLoaded(driver);
		}
		//Go to the community just created
		clickLinkWait("link=" + community.getName());
		waitForPageLoaded(driver);

		//Add a status update using the left nav bar
		log.info("INFO: Add status update to community using left nav bar");
		clickLinkWait(CommunitiesUIConstants.leftNavOverview);
		clickLinkWait(CommunitiesUIConstants.leftNavStatusUpdates);

		//Enter and Save a status update
		if(driver.isElementPresent(HomepageUIConstants.EnterMentionsStatusUpdate)){
			driver.getSingleElement(HomepageUIConstants.EnterMentionsStatusUpdate).type(newMessage);
		}else{
			driver.getSingleElement(HomepageUIConstants.EnterStatusUpdate).type(newMessage);
		}
		clickLinkWait(HomepageUIConstants.PostStatus);
		log.info("INFO: Status update was successful");

		//Verify that the update posted correctly
		log.info("INFO: Verify that the update posted correctly");
		fluentWaitTextPresent(Data.getData().postSuccessMessage);

	}

	/*
	 * Update 12/17/18: split at /communities/service/html/, then replace everything after it w/ communitystart?communityUuid=<UUID>
	 * Post 6.0 CR3, the default URI for the catalog (for auth'd users) is "allmycommunities", not "ownedcommunities".
	 * This implementation should be more general now.
	 */
	public void navViaUUID(BaseCommunity community){
		String communitiesURI = "/communities/service/html";
		String communityStart = "/communitystart?" + community.getCommunityUUID();
		String currentUrl = driver.getCurrentUrl();
		String targetUrl = "";

		if (currentUrl.indexOf(communitiesURI) > 0) {
			// also strips away any location.hash content that may have been left over in the URL
			targetUrl = currentUrl.substring(0, currentUrl.indexOf(communitiesURI) + communitiesURI.length()) + communityStart;
			log.info("INFO: CommunitiesUI#navViaUUID(), navigating to URL: " + targetUrl);
			driver.navigate().to(targetUrl);
		} else {
			// if we're mistakenly not in communities service, just navigate to the same page.
			driver.navigate().to(currentUrl);
		}

		//wait for sametime if enabled
		waitForSameTime();
		waitForPageLoaded(driver);
		waitForJQueryToLoad(driver);
	}

	public void checkEnableWidget(BaseWidget widget){

		boolean found = false;
		List<Element> enabledWidget = driver.getVisibleElements("css=h2[class='ibmDndDragHandle']");

		for (Element element : enabledWidget) {

			if(widget.getTitle().contentEquals(element.getText())){
				found=true;
				log.info("INFO: Widget is enabled: " + widget.getTitle());
			}
		}

		if(!found){
			log.info("INFO: Widget not enabled");
			log.info("INFO: Enabling widget " + widget.getTitle());
			addWidget(widget);
		}
	}

	public void changeFolderName(String folderName, String newName, boolean clickMoreActionArrow) {
		clickLink(CommunitiesUIConstants.foldersTab);
		clickLink("link=" + folderName);

		changeFolderNameInFileWidget(newName, clickMoreActionArrow);
	}

	public void changeFolderNameInFileWidget(String newName, boolean clickMoreActionArrow) {
		if (clickMoreActionArrow) {
			log.info("INFO: Select More Actions Dropdown");
			clickLinkWait(CommunitiesUIConstants.filesMoreActionsArrow);
		} else {
			log.info("INFO: Select More Actions button");
			clickLinkWait(CommunitiesUIConstants.filesMoreActionsBtn);
		}

		log.info("INFO: Select the Edit Properties option");
		clickLinkWait(CommunitiesUIConstants.filesEditPropertiesOption);

		fluentWaitTextPresent("Edit Folder Properties");
		clearText(FilesUIConstants.editPropertiesName);
		typeText(FilesUIConstants.editPropertiesName, newName);
		clickButton(Data.getData().buttonSave);
	}

	public void deleteFolder(String folderName, boolean clickMoreActionArrow) {
		clickLink(CommunitiesUIConstants.foldersTab);
		clickLink("link=" + folderName);

		deleteFolderInFileWidget(folderName, clickMoreActionArrow);
	}

	public void deleteFolderInFileWidget(String folderName, boolean clickMoreActionArrow) {
		if (clickMoreActionArrow) {
			log.info("INFO: Select More Actions Dropdown");
			clickLinkWait(CommunitiesUIConstants.filesMoreActionsArrow);
		} else {
			log.info("INFO: Select More Actions button");
			clickLinkWait(CommunitiesUIConstants.filesMoreActionsBtn);
		}

		log.info("INFO: Select the Delete option");
		clickLinkWait(CommunitiesUIConstants.filesDeleteOption);

		//driver.getSingleElement(CommunitiesUIConstants.DeleteButton).click();
		// Added javascript click as test is failing in Share pipeline
		clickLinkWithJavascript(CommunitiesUIConstants.DeleteButton);
	}


	public void createSurvey(BaseSurvey survey){

		fluentWaitElementVisible(CommunitiesUIConstants.CreateaSurvey);

		log.info("INFO: Select create survey link");
		clickLinkWait(CommunitiesUIConstants.CreateaSurvey);

		log.info("INFO: Add Survey name");
		driver.getSingleElement(CommunitiesUIConstants.NameField).type(survey.getName());


		log.info("INFO: Add Survey descriptions");
		driver.getSingleElement(CommunitiesUIConstants.DescriptionField).type(survey.getDescription());

		log.info("INFO: Select continue button");
		driver.getSingleElement(CommunitiesUIConstants.ContinueBtn).click();

		log.info("INFO: Add question");
		addSurveyQuestion(survey.getQuestions().get(0));

	}

	public void addSurveyQuestion(BaseSurveyQuestion question){

		log.info("INFO: Select add a question button");
		driver.getSingleElement(CommunitiesUIConstants.AddQuestion).click();

		log.info("INFO: Switch to the question popup");
		driver.switchToFrame().selectSingleFrameBySelector(CommunitiesUIConstants.QuestionFrame);

		log.info("INFO: Type the question");
		driver.getSingleElement(CommunitiesUIConstants.QuestionDescriptionField).type(question.getQuestion());

		log.info("INFO: Open the select the menu for question type");
		driver.getSingleElement(CommunitiesUIConstants.SelectOne).click();

		log.info("INFO: Select option" + question.getQuestionType().getOption());
		driver.getSingleElement(CommunitiesUIConstants.SelectOne).useAsDropdown().selectOptionByVisibleText(question.getQuestionType().getOption());
		driver.typeNative(Keys.ENTER);

		log.info("INFO: Select option button");
		driver.getSingleElement(CommunitiesUIConstants.OKBtn).click();

		log.info("INFO: Switch back to main frame");
		driver.switchToFrame().returnToTopFrame();

		log.info("INFO: Select the save button");
		driver.getSingleElement(CommunitiesUIConstants.SaveBtn).click();

	}

	public void startSurvey(BaseSurvey survey){


		log.info("INFO: Select the start button from the survey");
		driver.getSingleElement(CommunitiesUIConstants.StartBtn).click();

		log.info("INFO:");

		log.info("INFO: ");
		clickLinkWithJavascript("css=input[dojoattachpoint='okButton']");

	}


	/**
	 * @author Patrick Doherty
	 * This method enables the user to post a status update to a community
	 * from the 'Status Updates' widget
	 * @param statusUpdate - The status update which is to be posted to the community
	 */
	public void postCommunityUpdate(String statusUpdate){

		log.info("INFO: Posting the community status update: " + statusUpdate);
		List<Element> frames = driver.getVisibleElements(BaseUIConstants.StatusUpdate_iFrame);
		int frameCount = 0;
		for(Element frame : frames){
			frameCount++;
			log.info("INFO: Frame toString: " + frame.toString());
			log.info("INFO: Frame location: " + frame.getLocation());
			//The first CK Editor iframe will be for the embedded sharebox
			if(frameCount == 1){
				log.info("INFO: Switching to Frame: " + frameCount);
				driver.switchToFrame().selectFrameByElement(frame);
			}
		}

		driver.getSingleElement(BaseUIConstants.StatusUpdate_Body).click();
		driver.getSingleElement(BaseUIConstants.StatusUpdate_Body).type(statusUpdate);

		log.info("INFO: Returning to top Frame to click 'Post' button");
		driver.switchToFrame().returnToTopFrame();

		clickLinkWait(HomepageUIConstants.PostStatusOld);

		log.info("INFO: Verify that the update posted correctly");
		fluentWaitTextPresent(Data.getData().postSuccessMessage);
	}

	/**
	 * @author Patrick Doherty
	 * This method enables the user to add a comment to a community status update
	 * from the 'Status Updates' widget
	 * @param statusUpdate - The status update to which the comment will added
	 * @param statusComment - The comment which will be added
	 */
	public void postCommunityUpdateComment(String statusUpdate, String statusComment){
		HomepageUI ui = HomepageUI.getGui(cfg.getProductName(),driver);
		ui.moveToClick(HomepageUI.getStatusUpdateMesage(statusUpdate), HomepageUIConstants.StatusCommentLink);

		//Type and post a comment
		if(driver.isElementPresent(HomepageUIConstants.StatusCommentTextArea)){
			log.info("INFO: Post the comment: " + statusComment);
			driver.getFirstElement(HomepageUIConstants.StatusCommentTextArea).type(statusComment);
		}
		else{
			List<Element> frames = driver.getVisibleElements(BaseUIConstants.StatusUpdate_iFrame);
			int frameCount = 0;
			for(Element frame : frames){
				frameCount++;
				log.info("INFO: Frame toString: " + frame.toString());
				log.info("INFO: Frame location: " + frame.getLocation());
				//The first CK Editor iframe will be for the embedded sharebox and the second for the open comment text box
				if(frameCount > 1){
					log.info("INFO: Switching to Frame: " + frameCount);
					driver.switchToFrame().selectFrameByElement(frame);
				}
			}

				log.info("INFO: Typing the comment in the comment text area");
				driver.getSingleElement(BaseUIConstants.StatusUpdate_Body).click();
				driver.getSingleElement(BaseUIConstants.StatusUpdate_Body).type(statusComment);

				log.info("INFO: Return to the top frame to click the 'Post' button");
				driver.switchToFrame().returnToTopFrame();
		}

		clickLinkWait(HomepageUIConstants.PostComment);

	}


	/**
	 * Follow community expects UI to be at any one of the Community views level.
	 * When called it will open the community and select Follow This Community
	 *
	 * @param community - The community to start following
	 */
	public void follow(BaseCommunity community){

		//Find and open the community
		fluentWaitPresent("link=" + community.getName());
		log.info("INFO: Selecting "+ community.getName());
		clickLink("link=" + community.getName());


		//Follow this community
		log.info("INFO: Choose to follow " + community.getName());
		fluentWaitPresent(CommunitiesUIConstants.FollowThisCommunity);
		clickLink(CommunitiesUIConstants.FollowThisCommunity);
		fluentWaitTextPresent(Data.getData().FollowCommunityMsg);

	}

	public abstract String getCommunitiesBanner();

	/** Open the Help window and verify */
	public abstract void communitiesHelpAndAbout() throws Exception;

	protected abstract void addHandle(String handle);

	protected abstract void addTheme(BaseCommunity community);

	protected abstract void addMember(Member member);

	public abstract void checkInternal(BaseCommunity community);

	public abstract void openAdvancedOptions();

	public abstract void allowShareExternal(BaseCommunity community);

	public abstract void inviteUser(User Guest);

	public abstract void inviteExternalUser(User Guest);

	public abstract void openAPICommunity(String communityName, User myUser);

	public abstract void addSurveyWidget();

	public abstract void verifyWidgetPage();

	public void restrictedNotInPublic(BaseCommunity community, boolean isCardView) {
		//Validate restricted community is not list in public communities
		log.info("INFO: Validate restricted community is not in Public Communities view");
		if (isCardView) {
			// select "Discover" in nav bar for 6.0 CR4 Card View
			log.info("INFO: Navigate to the 'Discover' view");
			waitForPageLoaded(driver);
			goToDiscoverNameAscCardView();

			Assert.assertFalse(driver.isElementPresent("css=div[aria-label='"+community.getName()+"']"),
					"ERROR: Restricted community is present in public view");
		} else {
			log.info("INFO: Navigate to Public Communities view");
			waitForPageLoaded(driver);
			Community_View_Menu.PUBLIC_COMMUNITIES.select(this);
			Assert.assertFalse(driver.isElementPresent(CommunitiesUI.getCommunityLink(community)),
					   "ERROR: Restricted community is present in public view");
		}
	}

	public void deletedNotInPublic(BaseCommunity community, boolean isCardView) {
		log.info("Verify deletion in Public Communities view");
		if (isCardView) {
			// select "Discover" in nav bar for 6.0 CR4 Card View
			log.info("INFO: Navigate to the 'Discover' view");
			waitForPageLoaded(driver);
			goToDiscoverNameAscCardView();
			driver.changeImplicitWaits(5);
			Assert.assertFalse(driver.isElementPresent("css=div[aria-label='" + community.getName() + "']"),
					"ERROR: Community '" + community.getName() + "' shows up in Public Communities view after deletion.");
			driver.turnOnImplicitWaits();

		} else {
			log.info("INFO: Navigate to Public Communities view");
			Community_View_Menu.PUBLIC_COMMUNITIES.select(this);
			waitForPageLoaded(driver);

			Assert.assertTrue(fluentWaitTextNotPresent(community.getName()),
						  	  "Community '" + community.getName() + "' shows up in Public Communities view after deletion.");
		}
	}

	public abstract void clickSaveAddMember();

	public abstract String lastOwnerLeaveCommunityMsg();

	public abstract boolean presenceOfDefaultWidgetsForCommunity();

	public abstract boolean presenceOfWidgetsInAddAppsPalette();

	public abstract boolean presenceOfWidgetsInOverviewPage();

    public abstract void businessOwnerDescription();

	public abstract void  frameEntry(HomepageUI hUI );

	public abstract String getMemberFilterDropdown(BaseCommunity community);

	public abstract void switchToNewToCommHelpWindow();

	public abstract boolean presenceOfDefaultWidgetsOnTopNav();





	public static BaseCommunity.Access getDefaultAccess(String product){
		if(product.toLowerCase().equals("cloud")){
			return BaseCommunity.Access.RESTRICTED;
		} else if(product.toLowerCase().equals("vmodel")){
			return BaseCommunity.Access.RESTRICTED;
		} else
			return BaseCommunity.Access.PUBLIC;
	}


	public static CommunitiesUI getGui(String product, RCLocationExecutor driver){
		if(product.toLowerCase().equals("cloud")){
			return new  CommunitiesUICloud(driver);
		} else if(product.toLowerCase().equals("onprem")) {
			return new  CommunitiesUIOnPrem(driver);
		} else if(product.toLowerCase().equals("production")) {
			return new  CommunitiesUIProduction(driver);
		} else if(product.toLowerCase().equals("vmodel")) {
			return new  CommunitiesUIVmodel(driver);
		} else if(product.toLowerCase().equals("multi")) {
			return new  CommunitiesUIMulti(driver);
		}else {
			throw new RuntimeException("Unknown product name: " + product);
		}
	}

	/**
	 * Get the community widget in view's page
	 * @param communityName - Name of the community
	 * @return community row
	 */
	public Element getCommunityWidget(String communityName, boolean isCardView){

		if (isCardView) {

			Element communitycard = null;
			String cardString = "css=.bx--cardv2.community-card[aria-label=\"" + communityName + "\"]";
			communitycard = driver.getSingleElement(cardString);
			return communitycard;
		}
		else {
			return getCommunityWidget(communityName);
		}
	}


	public Element getCommunityWidget(String communityName){

		log.info("INFO: To get the row of the community");

		//collect all the visible communities as web elements
		List<Element> visibleWidgets = driver.getElements("css=tr[class^='placeRow'][aria-labelledby^='lconn_communities_catalog_widgets_PlaceDisplayWidget_']");
		log.info("INFO: visible communities are " + visibleWidgets.size());

		Element communityRow = null;
		//Search for the community and break the loop
		for(Element visWidget : visibleWidgets){
			String commName = visWidget.getSingleElement("css=td h4 span[id^='lconn_communities_catalog_widgets_PlaceDisplayWidget_'] > a").getText();
			log.info("INFO: Compare the community Name");
			if(commName.equalsIgnoreCase(communityName)){
				log.info("INFO: Found community - " + commName);
				communityRow = visWidget;
				break;
			}
		}

		return communityRow;
	}

	/**
	 * Adds handle to SubCommunity
	 * @param handle - enter handle name
	 * @return none
	 */
	protected void addSubHandle(String handle) {
		log.info("INFO: Entering a SubCommunity handle");
		driver.getSingleElement(CommunitiesUIConstants.SubCommunityHandle).clear();
		driver.getSingleElement(CommunitiesUIConstants.SubCommunityHandle).type(handle);
	}

	/**
	 * Method to click on Accept link under Invited view
	 * @param community Description
	 * @return String
	 */
	public static String getAcceptLink(String CommunityDesc){
		return "css=div p:contains('" + CommunityDesc + "') + p + ul > li > a[dojoattachevent='onclick: acceptInvite']";
	}

	/**
	 * Method to click on Decline link under Invited view
	 * @param community Description
	 * @return String
	 */
	public static String getDeclineLink(String CommunityDesc){
		return "css=div p:contains('" + CommunityDesc + "') + p + ul > li > a[dojoattachevent='onclick: declineInvite']";
	}

	/**
	*Info: To get the left Nav. menu items into a list</li>
	*@param: None
	*@return: List<String> containing menu items of community card
	*/
	public List<String> getLeftNavMenu() {
		List<String> leftNavMenu = new ArrayList<String>();
		List<String> temp = new ArrayList<String>();

		//Open the overview menu
		this.clickLinkWithJavascript(CommunitiesUIConstants.OpenOverViewMenu);

		//Add the menu items to List
		List<Element> leftNavMenuElem = driver.getElements(CommunitiesUIConstants.leftNavMenuItems);
		for(Element widget : leftNavMenuElem) {
			leftNavMenu.add(widget.getText().trim().toLowerCase());
		}

		//Close the overview menu
		this.clickLinkWithJavascript(CommunitiesUIConstants.OpenOverViewMenu);

		//Sort the menu order and add Overview as first element
		leftNavMenu.remove("overview");
		Collections.sort(leftNavMenu);
		temp.add("overview");
		temp.addAll(leftNavMenu);
		leftNavMenu=temp;

		return leftNavMenu;
	}
	/**
	*Get the user's business card
	*@param: User
	*@return: String  containing business card
	*/
	public String getbusinesscard(User testUser){
		return driver.getSingleElement("css=span[id='"+ testUser.getEmail()+"vcardNameElem']").getText();
	}

	/**
	*Info: To view the user's business card
	*@param: User
	*
	*/
	public void viewBusinesscard(User testUser){
		fluentWaitElementVisible("link="+testUser.getDisplayName());
		driver.getSingleElement("link="+testUser.getDisplayName()).hover();
	}

	/**
	* Get the MemberName using the list of Element
	* @param: all members entries
	* @return: array of String  containing MemberName
	* Author :Rajeev
	*/
	public String[] getnameFromMemberList(List<Element> InElement){
		String[] MemberName = new String[InElement.size()];
		for(int i=0 ;i < InElement.size();i++)
		{
			MemberName[i] = InElement.get(i).getText();
		}
		return MemberName;
	}


	/**
	 * Sort the members list (given input list by name)
	 * @param: unsorted input list
	 * @return: sorted names
	 * Author :Rajeev
	 */
	public String[] getsortMemberList(List<Element> unsortList){

		String[] SortName= getnameFromMemberList(unsortList);

		Arrays.sort(SortName);

        return SortName ;
	}
	/**
	 * Compares the array of String(containing Names of members),to the names of members from the Element List
	 * @param : array of string
	 * @param : sorted input list
	 * Author : Rajeev
	 */
	public void CheckName(String[] actual , List<Element> Expected ){
		boolean FlagCheck = false;
		for (int num=0;num<Expected.size();num++){
			FlagCheck = actual[num].equals(Expected.get(num).getText());
				Assert.assertTrue(FlagCheck,
						  "ERROR: Sorting order of members are not as expected");
				FlagCheck = false;
		}
	}

	/**
	 *Compares the array of String(containing Names of members),to the names of members from the Element List in the reverse order
	 * @param : array of string
	 * @param : sorted input list
	 * Author : Rajeev
	 */
	 public void CheckReverse(String[] actual , List<Element> Expected ){
		boolean FlagCheck = false;
		for (int num=0;num<Expected.size();num++){
			FlagCheck = actual[num].equals(Expected.get(Expected.size() -(num+1)).getText());
			Assert.assertTrue(FlagCheck,
					  "ERROR: Sorting order of members are not as expected");
			FlagCheck = false;
			}
		}

	/**
	*Export members to a csv file
	*@param: BaseFile file
	*@param: Memberslist
	*/
    public void exportMembersViaFile(BaseFile file,List<Member> Memberslist) {

		log.info("INFO: Now we will export to the file " + file.getName());
		//Click on Export Members to export the data
		log.info("INFO: Click on Export member button");
		this.fluentWaitElementVisible(CommunitiesUIConstants.ExportMembersButton);
		this.clickLinkWithJavascript(CommunitiesUIConstants.ExportMembersButton);

		log.info("INFO: Select Export member submit");
		this.clickLinkWait(CommunitiesUIConstants.MemberImportSubmitButton);
		}


	/**
	 * Selects first match item (community or user) from typeahead list
	 * @param itemName - The user/community whose name will be selected from the typeahead
	 * @param typeaheadList - The typeahead from which the selection will be made
	 *
	 */
	public void typeaheadSelection(String itemName, String typeaheadList){

		//Collect all the options
		List<Element> options = driver.getVisibleElements(typeaheadList);

		//Iterate through the list and select the user/community from drop down
		Iterator<Element> iterator = options.iterator();
		while (iterator.hasNext()) {
			Element option = iterator.next();
			if (option.getText().contains(itemName + " ") || option.getText().endsWith(itemName)){
				log.info("INFO: Found " + itemName);
				option.click();
				break;
				}
			}
		}

	/**
	 * Edits a comment posted to a community blog by clearing the existing comment and
	 * replacing it with the comment provided as a parameter
	 *
	 * This UI method works with comments posted to a blog entry as well as comments posted to
	 * an idea in an ideation blog
	 *
	 * @param commentEdit - The comment to be posted as part of the update / edit of the existing comment
	 */
	public void editCommunityBlogComment(String commentEdit) {

		log.info("INFO: Retrieving all visible iframe elements");
		List<Element> frames = driver.getVisibleElements(CommunitiesUIConstants.BlogsComment_iFrame);

		int index = 0;
		boolean foundFrame = false;
		while(index < frames.size() && foundFrame == false) {
			Element currentFrame = frames.get(index);
			log.info("INFO: Frame toString: " + currentFrame.toString());
			log.info("INFO: Frame location: " + currentFrame.getLocation());

			if(index == 0) {
				log.info("INFO: Switching to community blog comment edit iframe");
				driver.switchToFrame().selectFrameByElement(currentFrame);
				foundFrame = true;
			}
			index ++;
		}

		log.info("INFO: Retrieving the element for the blog comment text area and editing the comment");
		Element blogsCommentBody = driver.getSingleElement(CommunitiesUIConstants.BlogsComment_Body);
		blogsCommentBody.clear();
		blogsCommentBody.type(commentEdit);

		log.info("INFO: Returning to main (top) frame");
		driver.switchToFrame().returnToTopFrame();

		log.info("INFO: Clicking on the 'Submit' button to finish updating the comment");
		clickLinkWait(CommunitiesUIConstants.BlogsComment_Submit);
	}

	/**
	 * Posts a trackback comment on a community blog
	 * PLEASE NOTE: This method also works for posting trackbacks to Ideation Blog ideas
	 *
	 * @param blogEntry - The BlogPost instance of the blog entry / idea to which the trackback is to be posted
	 * @param trackbackComment - The comment to be posted to the blog entry / idea as a trackback
	 */
	public void postTrackbackOnCommunityBlog(BlogPost blogEntry, String trackbackComment) {

		log.info("INFO: Now posting a trackback comment on the community blog with title: " + blogEntry.getTitle());

		clickLinkWait(BlogsUIConstants.BlogsAddACommentLink);

		List<Element> frames = driver.getVisibleElements(BaseUIConstants.StatusUpdate_iFrame);
		int frameCount = 0;
		for(Element frame : frames){
			frameCount++;
			log.info("INFO: Frame toString: " + frame.toString());
			log.info("INFO: Frame location: " + frame.getLocation());
			//The first CK Editor iframe will be for the embedded sharebox
			if(frameCount == 1){
				log.info("INFO: Switching to Frame: " + frameCount);
				driver.switchToFrame().selectFrameByElement(frame);
			}
		}
		driver.getSingleElement(BaseUIConstants.StatusUpdate_Body).click();
		driver.getSingleElement(BaseUIConstants.StatusUpdate_Body).type(trackbackComment);

		log.info("INFO: Returning to parent frame");
		driver.switchToFrame().returnToTopFrame();

		log.info("INFO: Click the checkbox for making this comment a trackback comment");
		clickLinkWait(BlogsUIConstants.BlogCommentTrackbackCheckBox);

		log.info("INFO: Click 'Post' to post the comment");
		clickLinkWait(BlogsUIConstants.BlogsCommentSubmit);

		log.info("INFO: Verify that the comment has been posted and is now visible in the UI");
		fluentWaitTextPresent(trackbackComment);
	}

	/**
	 * This method checks to see if the Guided Tour pop-up appears.  If yes, it will close the pop-up box.
	 */

	public void closeGuidedTourPopup(){

		log.info("INFO: Check to see if the guided tour pop-up appears");
		if(driver.isElementPresent(CommunitiesUIConstants.closeCommGuidedTour)){
			log.info("INFO: Close the guided tour pop-up box");
			this.clickLinkWait(CommunitiesUIConstants.closeCommGuidedTour);
		}
	}


	/**
	 * This method will collect a list of the subcommunities that appear on the subcommunity drop-down menu
	 *
	 * @return - list of subcommunities on the drop-down menu
     */
	public List<Element> collectListOfSubcommunities() {

		// collect list of subcommunities
		List<Element> subcommList = driver
				.getElements(CommunitiesUIConstants.tabbedNavSubcommMenuItem);
		log.info("INFO: Subcommunities = " + subcommList.size());

		// Log each subcommunity for debug purposes
		for (Element listedSubcomms : subcommList) {
			String title = listedSubcomms.getText();
			log.info("INFO: Subcommunity " + title + " is listed");
		}
		return subcommList;

	}

	/**
	 * This method will collect a list of subcommunities on the drop-down menu and locate the specific subcommunity.
	 * If the 'doclick' param is set to true the subcommunity will be selected.
	 *
	 *
	 * @param subcomm - list of subcommunities on the drop-down menu
	 * @param subcommName - subcommunity to select
	 * @param doclick - input true if you want to click on the subcommunity; otherwise, just return the element
	 * @return - selected subcommunity or null if subcommunity is not found
	 */
	public Element selectSubCommunity(List<Element> subcomm, String subcommName, boolean doclick) {
		Element returnedsubcomm = null;
		for (int i = 0; i < subcomm.size(); i++) {
			if (subcomm.get(i).getText().equals(subcommName)) {
				returnedsubcomm = subcomm.get(i);
				if (doclick) returnedsubcomm.click();
				break;
			}

		}

		return returnedsubcomm;
	}

	public static String getStatusUpdatesComment (String statusComment){
		return "css=div[class^='asLinkContainer']:contains(" + statusComment + ")";
	}


	/**
	 * This method enables the user to add a comment and an at mentions to a community status update
	 * from the 'Status Updates' widget
	 * @param statusUpdate - The status update to which the comment will added
	 * @param statusComment - The comment which will be added
	 * @param user - The user to be at mentioned
	 */
	public void postCommunityUpdateCommentWithAtMentions(String statusUpdate, String statusComment, User user){
		String mention = Character.toString('@');
		HomepageUI ui = HomepageUI.getGui(cfg.getProductName(),driver);
		ui.moveToClick(HomepageUI.getStatusUpdateMesage(statusUpdate), HomepageUIConstants.StatusCommentLink);

		//Type and post a comment
		if(driver.isElementPresent(HomepageUIConstants.StatusCommentTextArea)){
			log.info("INFO: Post the comment: " + statusComment);
			driver.getFirstElement(HomepageUIConstants.StatusCommentTextArea).type(statusComment);
		}
		else{
			List<Element> frames = driver.getVisibleElements(BaseUIConstants.StatusUpdate_iFrame);
			int frameCount = 0;
			for(Element frame : frames){
				frameCount++;
				log.info("INFO: Frame toString: " + frame.toString());
				log.info("INFO: Frame location: " + frame.getLocation());
				//The first CK Editor iframe will be for the embedded sharebox and the second for the open comment text box
				if(frameCount > 1){
					log.info("INFO: Switching to Frame: " + frameCount);
					driver.switchToFrame().selectFrameByElement(frame);
				}
			}

				log.info("INFO: Typing the comment in the comment text area");
				driver.getSingleElement(BaseUIConstants.StatusUpdate_Body).click();
				driver.getSingleElement(BaseUIConstants.StatusUpdate_Body).type(statusComment);

				log.info("INFO: Typing the user to @mention in the comment text area");
				driver.getSingleElement(BaseUIConstants.StatusUpdate_Body).typeWithDelay(mention + user.getDisplayName());
				driver.switchToFrame().returnToTopFrame();

				log.info("INFO: Select the user from the mentions typeahead suggestion list");
				driver.getFirstElement((HomepageUIConstants.MentionsTypeaheadSelection)).click();

				log.info("INFO: Return to the top frame to click the 'Post' button");
				driver.switchToFrame().returnToTopFrame();
		}

		ui.clickLinkWait(HomepageUIConstants.PostComment);

	}

	/**
	 * This method checks to see if the Guided Tour gatekeeper flag is enabled.  If yes, it will close guided tour.
	 * The method then checks to see if the Copy Community gatekeeper flag is enabled.  If yes, it will click on Start
	 * a Community button and then the Start from New link to bring up the Start a Community form.
	 */

	public void closeGuidedTourSelectStartFromNew(){
		log.info("If GateKeeper setting for Guided Tour is enabled, close the Community Guided Tour popup window to unblock the 'Start from New' menu item");
		if(checkGKSetting(Data.getData().gk_GuidedTourComm_flag)){

			log.info("INFO: If the guided tour pop-up box appears, close it");
			this.closeGuidedTourPopup();
		}

		boolean isCardView = checkGKSetting(Data.getData().gk_catalog_card_view);

		log.info("If GateKeeper setting for Copy Existing Community is enabled, click Start a Community menu");
		if(checkGKSetting(Data.getData().gk_copycomm_flag)){

			if (isCardView){
				// Click Start A Community (card view)
				log.info("INFO: Create a new community using the Start A Community Drop Down in the Card View");
				this.driver.getSingleElement(CommunitiesUIConstants.StartACommunityDropDownCardView).click();
				this.driver.getSingleElement(CommunitiesUIConstants.StartACommunityFromDropDownCardView).click();

			}
			else {
				log.info("INFO: Click on Start a Community to expand the dropdown menu");
				clickLinkWait(CommunitiesUIConstants.StartACommunityMenu);

				log.info("INFO: Click on Start from New");
				clickLinkWait(CommunitiesUIConstants.StartFromNewOption);
			}

		}else{

			if (isCardView){
				// Click Start A Community (card view)
				log.info("INFO: Create a new community using the Start A Community Drop Down in the Card View");
				this.driver.getSingleElement(CommunitiesUIConstants.StartACommunityDropDownCardView).click();

			}
			else {
				log.info("INFO: Click on the Start a Community button");
				getFirstVisibleElement(CommunitiesUIConstants.StartACommunity).click();
			}
		}
	}


	/**
	 * This method will take the user to the default Catalog View, I'm a Member or My Communities (CR3)
	 */

	public void goToDefaultCatalogView(){

		boolean isCardView = checkGKSetting(Data.getData().gk_catalog_card_view);

		if (checkGKSetting(Data.getData().gk_catalog_ui_updated_flag)){

			if (isCardView) {
				// go to My Communities view
			   log.info("INFO: Clicking on the My Communites view");
			   clickLinkWait(CommunitiesUIConstants.topNavMyCommunitiesCardView); // "My Communities" top-nav link
			}
			else {
				// go to My Communities view
				log.info("INFO: Clicking on the My Communites link from the LeftNavigation");
				Community_View_Menu.MY_COMMUNITIES.select(this);
			}
		}
		else {
	       //go to I'm Member view
		   log.info("INFO: Clicking on the I'm Member link from the LeftNavigation");
		   Community_View_Menu.IM_A_MEMBER.select(this);
	   }
	}

	/**
	 * This method will take the user to the I'm an Owner catalog view via the Communities link on the mega-menu/top navigation menu
	 */

	public void goToImAnOwnerViewUsingCommLinkOnMegamenu(){

		log.info("INFO: The environment is on-premise");

		if (checkGKSetting(Data.getData().gk_catalog_ui_updated_flag)){

			boolean isCardView = checkGKSetting(Data.getData().gk_catalog_card_view);

		    log.info("INFO: Click on the Communities navbar button");
			clickLinkWait(CommunitiesUIConstants.communitiesMegaMenu);

			log.info("INFO: Click on the My Communities link to return to catalog view");
			clickLinkWait(CommunitiesUIConstants.communitiesMegaMenuMyCommunities);

			log.info("INFO: Click on the I'm an Owner catalog view");
			goToOwnerView(isCardView);
		}
		else {
		   log.info("INFO: Click on the Communities link to return to the catalog views");
		   clickLinkWait(CommunitiesUIConstants.communitiesMegaMenu);

		   log.info("INFO: Click on the I'm an Owner link from the Communities drop-down menu on the mega menu");
		   clickLinkWait(CommunitiesUIConstants.communitiesMegaMenuOwner);
		}
	}

	/**
	* check to make sure the community name field is not empty.
	* if the name field is empty, re-enter the subcommunity name
	*/

	public void checkCommunityNameFieldEmptyMsg(BaseSubCommunity subCommunity) {
		log.info("INFO: Check for the message that the community name should not be empty");
		if (driver.isTextPresent(Data.getData().communityNameFieldIsEmptyMsg)){
			log.info("INFO: Name field is empty, entering community name again " + subCommunity.getName());
			this.driver.getSingleElement(CommunitiesUIConstants.CommunityName).clear();
			this.driver.getSingleElement(CommunitiesUIConstants.CommunityName).type(subCommunity.getName());

			log.info("INFO: Click on the Access Advanced Features link to expand the section");
			driver.getFirstElement(CommunitiesUIConstants.comAdvancedLink).click();

			if(subCommunity.isUseParentMembers()) {
				log.info("INFO: Select the checkbox to add members from the parent community to the subcommunity");
				this.driver.getFirstElement(CommunitiesUIConstants.AddMemberscheckbox).click();
			}

			log.info("INFO: Saving the sub community " + subCommunity.getName());
			this.driver.getSingleElement(CommunitiesUIConstants.SaveButton).click();
		}
	}

	/**
	 * Clicks the Edit link for the member
	 * @param member - The user to be edited
	 */

	public void clickEditMemberLink(Member member) {
		log.info("INFO: Locate the user to be edited");
		Element editLink = this.getFirstVisibleElement(CommunitiesUIConstants.userLinkOnMembersPage.replace("PLACEHOLDER",member.getUser().getDisplayName().toString()));
		String id=editLink.getAttribute("id");

		id=id.replace("name_","");

		log.info("INFO: Click the Edit link for user: " + member.getUser().getDisplayName());
		this.clickLinkWait(CommunitiesUIConstants.editMemberLink.replace("REPLACE_THIS", id));
	}

	public static String selectFolder(BaseFolder folder){
		return "css=div[id^='lconn_share_widget_Node'][title='" + folder.getName() + "']";
	}

	//
	// This will take you to the I'm an Owner view in Communities.
	//
	// It will check the GK flags and take you to the CR4 card view or older list view.
	//
	public void goToDefaultIamOwnerView(boolean isCardView) {

		//Navigate to the I'm an Owner view

		// if CR4, go to the Owner Card View.
		if(isCardView){
		    log.info("INFO: Card View, got to I am owner view.");
			goToOwnerCardView();
		}
		else {
		   //I'm Owner View
		   log.info("INFO: Clicking on the I'm an Owner link from the LeftNavigation");
		   clickLinkWait(CommunitiesUIConstants.OwnerCommunitiesView);
		}
	}

	//
	// this method is used by BVT test cases
	//
    public void goToDefaultIamMemberView(boolean isCardView) {

		//Navigate to the I'm a Member view

		// if CR4, go to the I'm a Member Card View.
		if(isCardView){
		    log.info("INFO: Card View, got to I'm a Member view.");
		    goToMemberCardView();
		}
		else {
		   //I'm a Member View
		   log.info("INFO: Clicking on the I'm a Member view");
		   Community_View_Menu.IM_A_MEMBER.select(this);
		}
	}

    //
    // These methods are used by the Communities Regression code to navigate to
    // different views in the Community Catalog
    //

    public void goToOwnerView(boolean isCardView) {

  	    //Navigate to the I'm an Owner view
  		if(isCardView){
  		    log.info("INFO: Card View, got to I am owner view.");
  			goToOwnerCardView();
  		}
  		else {
  		   //I'm Owner View
  		   log.info("INFO: Clicking on the I'm an Owner link from the LeftNavigation");
  		   Community_View_Menu.IM_AN_OWNER.select(this);
  		}
  	}

  	public void goToMyCommunitiesView(boolean isCardView) {

  	    // go to My Communities view
  		if (isCardView) {
  		   goToMyCommunitiesCardView();
  		}
  		else {
  		   // go to My Communities view
  		   log.info("INFO: Clicking on the My Communites link from the LeftNavigation");
  		   Community_View_Menu.MY_COMMUNITIES.select(this);

  		   log.info("INFO: Click on sort by 'Date' option");
  		   this.clickLinkWait(CommunitiesUIConstants.catalogViewSortByDateTab);
  		}
  	}

  	public void goToIamFollowingView(boolean isCardView, boolean isOnPremise) {

  	   if(isCardView){
           log.info("INFO: Card View, go to I'm Following view.");
  		   goToIamFollowingCardView();
  	   }
  	   else {

  	       //I'm Following view
  	       log.info("INFO: Clicking on the I'm Following link from the LeftNavigation");
  	       Community_View_Menu.IM_FOLLOWING.select(this);

  	       if (!isOnPremise){
  	           //Step added so community could be easily found in the view
  	           log.info("INFO: If the catalog UI GK flag is enabled, click sort by 'Date'");
  	           if (checkGKSetting(Data.getData().gk_catalog_ui_updated_flag)){
  		          clickLinkWait(CommunitiesUIConstants.catalogViewSortByDateTab);
  	           }
  	       }
  	   }
  	}

  	public void goToCreatedView(boolean isCardView) {

  		if (isCardView) {
  			log.info("INFO: Card View, go to I Created view.");
  			goToCreatedCardView();
  		}
  		else {
  		   // go to I'v Created view
  		   log.info("INFO: Clicking on the I'v Created link from the LeftNavigation");
  		   Community_View_Menu.IVE_CREATED.select(this);

  		}
  	}

  	public void goToInvitedView(boolean isCardView) {


  	    if(isCardView){
            log.info("INFO: Card View, got to Invited View.");
  		    goToInvitedCardView();
  	    }
  	    else {

  	       //I'm Invited view
  	       log.info("INFO: Clicking on the I'm Invited link from the LeftNavigation");
  	       Community_View_Menu.IM_INVITED.select(this);
  	    }
  	}

  	public void goToMemberView(boolean isCardView) {

  	    if(isCardView){
              log.info("INFO: Card View, go to I'm a Member View.");
              goToMemberCardView();
  	    }
  	    else {

  	       //I'm a Member view
  	       log.info("INFO: Clicking on the I'm a Member link from the LeftNavigation");
  	       Community_View_Menu.IM_A_MEMBER.select(this);
  	    }
  	}

  	public void goToPublicView(boolean isCardView) {

  		if (isCardView) {
  		    log.info("INFO: Go to Discovery View");
  			goToDiscoverCardView();
  		}
  		else {
  		   //Public Communities
  		   log.info("INFO: Clicking on the Public community link from the LeftNavigation");
  		   clickLinkWait(CommunitiesUIConstants.leftNavPublicCommunities);
  		}
  	}

  	public void goToTrashView(boolean isCardView) {

  		if (isCardView) {
  			log.info("INFO: Navigate to Trash View");
  			goToTrashCardView();
  		} else {
  			//Click on Trash link
  			log.info("INFO: Click on Trash link");
  			clickLinkWait(CommunitiesUIConstants.TrashLink);
  		}
  	}

	public void goToOwnerCardView() {
		// "My Communities" top-nav link
		fluentWaitElementVisible(CommunitiesUIConstants.topNavMyCommunitiesCardView);
		clickLinkWait(CommunitiesUIConstants.topNavMyCommunitiesCardView);

		// current product behavior shows that clicking My Communities always reset the filter but
		// still needs to check whether the filter section on the left is visible
		Element filterIcon = getFirstVisibleElement(CommunitiesUIConstants.filterSideBar);
		if (!filterIcon.getAttribute("class").contains("expanded")) {
			log.info("INFO: Expand filter and select I'm an Owner view");
			clickLinkWait(CommunitiesUIConstants.filterSideBarExpandCardView);
		}

		log.info("INFO: Select I'm an Owner view");
		clickLinkWait(CommunitiesUIConstants.filterSideBarOwnerCardView);
		fluentWaitElementVisible(CommunitiesUIConstants.filterSideBarOwnerCardViewSelected);

		// Expand View dropdown then select "Recently Updated" entry
		log.info("INFO: Select Recently Updated");
		clickLinkWait(CommunitiesUIConstants.viewSelectorDropDownCardView);
		clickLinkWait(CommunitiesUIConstants.viewSelectorDropDownCardViewRecentlyUpdated);
	}

	public void goToMemberCardView() {
		clickLinkWait(CommunitiesUIConstants.topNavMyCommunitiesCardView); // "My Communities" top-nav link
		driver.changeImplicitWaits(10);
		if (isElementVisible(CommunitiesUIConstants.filterSideBarNotExpanded)) {
			clickLinkWait(CommunitiesUIConstants.filterSideBarExpandCardView); // filter side-bar expand
		}
		if (!isElementVisible(CommunitiesUIConstants.filterSideBarMemberCardViewSelected)) {
			clickLinkWait(CommunitiesUIConstants.filterSideBarMemberCardView); // filter side-bar member
		}
		driver.turnOnImplicitWaits();
		clickLinkWait(CommunitiesUIConstants.viewSelectorDropDownCardView); // Expand View Selector
		clickLinkWithJavascript(CommunitiesUIConstants.viewSelectorDropDownCardViewRecentlyUpdated); // "Recently Updated" entry
	}

	public void goToDiscoverNameAscCardView() {
		clickLinkWait(CommunitiesUIConstants.topNavDiscoverCardView); // "Discover" top-nav link
		clickLinkWait(CommunitiesUIConstants.viewSelectorDropDownCardView); // Expand View Selector
		clickLinkWait(CommunitiesUIConstants.viewSelectorDropDownCardViewNameAscending); // "Name Asc" entry
	}

	public void goToDiscoverCardView() {
		clickLinkWait(CommunitiesUIConstants.topNavDiscoverCardView); // "Discover" top-nav link
		clickLinkWait(CommunitiesUIConstants.viewSelectorDropDownCardView); // Expand View Selector
		clickLinkWait(CommunitiesUIConstants.viewSelectorDropDownCardViewRecentlyUpdated); // "Recently Updated" entry
	}

	public void goToTrashCardView() {
		clickLinkWait(CommunitiesUIConstants.topNavMyCommunitiesCardView); // "My Communities"" top-nav link
		clickLinkWait(CommunitiesUIConstants.viewSelectorDropDownCardView); // Expand View Selector
		clickLinkWait(CommunitiesUIConstants.viewSelectorDropDownCardViewTrash); // "Name (ascending)" entry
	}

	public void goToTrendingCardView() {
		clickLinkWait(CommunitiesUIConstants.topNavMyCommunitiesCardView); // "My Communities"" top-nav link
		clickLinkWait(CommunitiesUIConstants.viewSelectorDropDownCardView); // Expand View Selector
		clickLinkWait(CommunitiesUIConstants.viewSelectorDropDownCardViewTrending); // "Name (ascending)" entry
	}

	public void goToMyCommunitiesCardView() {
		clickLinkWait(CommunitiesUIConstants.topNavMyCommunitiesCardView); // "My Communities"" top-nav link
		clickLinkWait(CommunitiesUIConstants.viewSelectorDropDownCardView); // Expand View Selector
		clickLinkWait(CommunitiesUIConstants.viewSelectorDropDownCardViewRecentlyUpdated); // "Recently Updated" entry
	}

	public void goToInvitedCardView() {
		clickLinkWait(CommunitiesUIConstants.topNavInvitedCardView); // "Invited" top-nav link
	}

	public void goToIamFollowingCardView() {
		clickLinkWait(CommunitiesUIConstants.topNavMyCommunitiesCardView); // "My Communities" top-nav link
		if (isElementVisible(CommunitiesUIConstants.filterSideBarNotExpanded)) {
			clickLinkWait(CommunitiesUIConstants.filterSideBarExpandCardView); // filter side-bar expand
		}
		if (!isElementVisible(CommunitiesUIConstants.filterSideBarFollowingCardViewSelected)) {
			clickLinkWait(CommunitiesUIConstants.filterSideBarFollowingCardView); // filter side-bar following
		}
	}

	public void goToCreatedCardView() {
		clickLinkWait(CommunitiesUIConstants.topNavMyCommunitiesCardView); // "My Communities" top-nav link
		if (isElementVisible(CommunitiesUIConstants.filterSideBarNotExpanded)) {
			clickLinkWait(CommunitiesUIConstants.filterSideBarExpandCardView); // filter side-bar expand
		}
		if (!isElementVisible(CommunitiesUIConstants.filterSideBarCreatedCardViewSelected)) {
			clickLinkWait(CommunitiesUIConstants.filterSideBarCreatedCardView); // filter side-bar created
		}
		clickLinkWait(CommunitiesUIConstants.viewSelectorDropDownCardView); // Expand View Selector
		clickLinkWait(CommunitiesUIConstants.viewSelectorDropDownCardViewRecentlyUpdated); // "Recently Updated" entry
	}

	public String getCommunityCardSelector(BaseCommunity community) {
		String UUID_PREFIX = "communityUuid=";
		if (community.getCommunityUUID() != null && community.getCommunityUUID().length() > 0) {
		    return "css=#community-card-" + community.getCommunityUUID().replace(UUID_PREFIX, "");
		}
		return null;
	}
	//  Catalog Card Restore button
	public String getCommunityRestoreButtonLink(BaseCommunity community) {

		String cardselector = getCommunityCardSelector(community);
		return (cardselector != null) ? cardselector + " .community-restore" : null;
	}

    //  Catalog Card Accept button
	public String getCommunityAcceptButtonLink(BaseCommunity community) {

		String cardselector = getCommunityCardSelector(community);
		return (cardselector != null) ? cardselector + " .community-action.accept-invite" : null;
	}

	//  Catalog Card Decline button
	public String getCommunityDeclineButtonLink(BaseCommunity community) {
		String cardselector = getCommunityCardSelector(community);
		return (cardselector != null) ? cardselector + " .community-action.decline-invite" : null;
	}

	// Check the state of MORE menu if clicked or not on community page
	public void checkMoreisSelected() {
		driver.turnOffImplicitWaits();
		if (((driver.getElements(CommunitiesUIConstants.topNavMoreMenuItems).size()) == 0)) {
			log.info("size is: " + driver.getElements(CommunitiesUIConstants.topNavMoreMenuItems).size());
			clickLinkWithJavascript(CommunitiesUIConstants.tabbedNavMoreTab);
		}
		driver.turnOnImplicitWaits();
	}

	/**
	 * Switch URL to orgb for widgets displayed on top navigation bar under community page
	 * <p>
	 * @param widget String - name of Community widget to switch to orgB url
	 * @param orgBurl String - orgB URL to switch to
	 * @param selector String - widget page heading locator
	 * @param widgetPageHeading String - widget page heading
	 */
	public void validateSwitchURLCommWidget(String widget, String orgBurl, String selector,String widgetPageHeading) {

		fluentWaitPresent(CommunitiesUIConstants.topNavBar);

		// Get the list of element from top nav bar
		driver.turnOffImplicitWaits();
		List<Element> menu = driver.getElements(CommunitiesUIConstants.navmenu);
		driver.turnOnImplicitWaits();

		log.info("INFO: Names in list: " + menu.size());
		for (int i = 0; i < menu.size(); i++) {

			fluentWaitPresent(CommunitiesUIConstants.topNavBar);
			menu = driver.getElements(CommunitiesUIConstants.navmenu);
			log.info("INFO: number of Widget in sub community: " + menu.size());

			// Get single element from list
			Element ele = menu.get(i);
			String menuName = ele.getAttribute("innerText").trim().toLowerCase();
			log.info("INFO: Menu is: " + menuName);
			if (menuName.equals(widget)) {

				// Check whether element is under MORE menu
				if (!ele.isDisplayed()) {
					validateSwitchURLMoreMenuItems(menuName, orgBurl,selector,widgetPageHeading);
					break;
				} else {
					// Select top nav menu
					log.info("INFO: Selecting " + menuName + " from top nav bar");
					ele.click();

					// Wait until respective community widget page gets loaded
					waitForTextToBePresentInElementWd(createByFromSizzle(selector), widgetPageHeading, 10);
					
					// switch URL to orgB
					log.info("Switching URL: " + driver.getCurrentUrl() + " to orgB.");
					switchToOrgBURL(orgBurl);

					// Validate error message
					validateAccessDenied("Access Denied", "You do not have permission to access this page.");
					driver.navigate().back();
					break;
				}
			} else {
				log.info("Skipping the " + menuName + " selection");
			}
		}
	}

	/**
	 * Switch URL to orgb for widgets displayed under MORE menu on top navigation bar under community page
	 * <p>
	 * @param menuItemName String - name of Community widget under More menu
	 * @param orgBurl String - orgB URL to switch to
	 * @param selector String - selector to widget page heading
	 * @param widgetPageHeading String - widget page heading
	 */
	public void validateSwitchURLMoreMenuItems(String menuItemName, String orgBurl, String selector,String widgetPageHeading) {

		fluentWaitPresent(CommunitiesUIConstants.tabbedNavMoreTab);

		// Select MORE item view is if not expanded
		checkMoreisSelected();

		// Get all elements form MORE menu
		List<Element> moremenu = driver.getElements(CommunitiesUIConstants.topNavMoreMenuItems);
		log.info("INFO: Names in more list: " + moremenu.size());

		for (int j = 0; j < moremenu.size(); j++) {
			fluentWaitPresent(CommunitiesUIConstants.tabbedNavMoreTab);
			// Select MORE item view is if not expanded
			checkMoreisSelected();
			log.info("size is: " + driver.getElements(CommunitiesUIConstants.topNavMoreMenuItems).size());

			moremenu = driver.getElements(CommunitiesUIConstants.topNavMoreMenuItems);
			log.info("More menu number  is: " + moremenu.size());

			Element elemore = moremenu.get(j);
			fluentWaitPresent(CommunitiesUIConstants.tabbedNavMoreTab);
			String moremenuName = elemore.getAttribute("innerText").trim().toLowerCase();
			log.info("More menu name is: " + moremenuName);

			if (moremenuName.equals(menuItemName)) {

				// Select element under MORE menu
				elemore.click();
				waitForTextToBePresentInElementWd(createByFromSizzle(selector), widgetPageHeading, 10);

				// switch URL to orgB
				log.info("Switching URL: " + driver.getCurrentUrl() + " to orgB.");
				switchToOrgBURL(orgBurl);

				// Validate error message
				validateAccessDenied("Access Denied", "You do not have permission to access this page.");
				driver.navigate().back();
				break;

			} else {
				log.info("Skipping the " + moremenuName + " selection");
			}
		}
	}

	/**
	 * Navigate to Create community and verify Tiny Editor functionality
	 *
	 * @param Base community object
	 * @return String Text present in Description of Tiny Editor.
	 */
	public String verifyTinyEditor(BaseCommunity community) {
		TinyEditorUI tui = new TinyEditorUI(driver);

		if(community.getDescription().contains("RichCon"))
		{
			log.info("INFO: clicked on rich content link");
			this.getFirstVisibleElement(CommunitiesUIConstants.rteAddContent).click();
		}
		else if(community.getDescription().contains("Subcom"))
		{
			log.info("INFO: Navigate to Sub Community");
			clickLinkWithJavascript(BaseUIConstants.Community_Actions_Button);
			fluentWaitPresent(CommunitiesUIConstants.createSubCommunityFromDropdown);
			clickLinkWithJavascript(CommunitiesUIConstants.createSubCommunityFromDropdown);

			String subComTitle=community.getName()+ Helper.genDateBasedRandVal();
			this.waitForPageLoaded(driver);
			this.fluentWaitPresent(CommunitiesUIConstants.CommunityName);
			this.fluentWaitElementVisible(CommunitiesUIConstants.CommunityName);
            log.info("INFO: Entering subcommunity name " + subComTitle);
            this.driver.getSingleElement(CommunitiesUIConstants.CommunityName).type(subComTitle);
            tui.clickOnMoreLink();
		}
		else
		{
		log.info("INFO: Navigate to create community page by using the Start A Community Drop Down in the Card View");
		this.driver.getSingleElement(CommunitiesUIConstants.StartACommunityDropDownCardView).click();
		this.driver.getSingleElement(CommunitiesUIConstants.StartACommunityFromDropDownCardView).click();

		this.waitForPageLoaded(driver);
		// Wait for Community page to load
		fluentWaitPresent(CommunitiesUIConstants.CommunityName);

		log.info("INFO: Entering community name " + community.getName());
		this.driver.getSingleElement(CommunitiesUIConstants.CommunityName).type(community.getName());
		tui.clickOnMoreLink();

		}

		log.info("INFO: Entering a description and validating the functionality of Tiny Editor");
		if (community.getDescription() != null) {

			String TE_Functionality[] = community.getTinyEditorFunctionalitytoRun().split(",");

			for (String functionality : TE_Functionality) {
				switch (functionality) {
				case "verifyParaInTinyEditor":
					log.info("INFO: Validate Paragragh and header functionality of Tiny Editor");
					tui.verifyParaInTinyEditor(community.getDescription());
					break;
				case "verifyAttributesInTinyEditor":
					log.info("INFO: Validate Attributes functionality of Tiny Editor");
					tui.verifyAttributesInTinyEditor(community.getDescription());
					break;
				case "verifyPermanentPenInTinyEditor":
					log.info("INFO: Validate Permanent Pen functionality of Tiny Editor");
					tui.verifyPermanentPenInTinyEditor(community.getDescription());
					break;
				case "verifyUndoRedoInTinyEditor":
					log.info("INFO: Validate Undo and Redo functionality of Tiny Editor");
					tui.verifyUndoRedoInTinyEditor(community.getDescription());
					break;
				case "verifyAlignmentInTinyEditor":
					log.info("INFO: Validate Alignment functionality of Tiny Editor");
					tui.verifyAlignmentInTinyEditor(community.getDescription());
					break;
				case "verifyIndentsInTinyEditor":
					log.info("INFO: Validate Indents functionality of Tiny Editor");
					tui.verifyIndentsInTinyEditor(community.getDescription());
					break;
				case "verifyBulletsAndNumbersInTinyEditor":
					log.info("INFO: Validate Bullets and Numbers functionality of Tiny Editor");
					tui.verifyBulletsAndNumbersInTinyEditor(community.getDescription());
					break;
				case "verifyHorizontalLineInTinyEditor":
					log.info("INFO: Validate Horizontal Line functionality of Tiny Editor");
					tui.verifyHorizontalLineInTinyEditor(community.getDescription());
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
					tui.verifySpellCheckInTinyEditor(community.getDescription());
					break;
				case "verifyRowsCoulmnOfTableInTinyEditor":
					log.info("INFO: Validate Rows and Columns of Table in Tiny Editor");
					tui.verifyRowsCoulmnOfTableInTinyEditor(community.getDescription());
					break;
				case "verifyFormatPainterInTinyEditor":
					log.info("INFO: Validate Format Painter in Tiny Editor");
					tui.verifyFormatPainterInTinyEditor(community.getDescription());
					break;
				case "verifyFontInTinyEditor":
					log.info("INFO: Validate font functionality of Tiny Editor");
					tui.verifyFontInTinyEditor(community.getDescription());
					break;
				case "verifyFontSizeInTinyEditor":
					log.info("INFO: Validate font Size functionality of Tiny Editor");
					tui.verifyFontSizeInTinyEditor(community.getDescription());
					break;
				case "verifyLinkImageInTinyEditor":
					log.info("INFO: Validate Link Image functionality of Tiny Editor");
					tui.verifyLinkImageInTinyEditor(community.getDescription());
					break;
				case "verifyRightLeftParagraphInTinyEditor":
					log.info("INFO: Validate Left to Right paragraph functionality of Tiny Editor");
					tui.verifyRightLeftParagraphInTinyEditor(community.getDescription());
					break;
				case "verifyOtherTextAttributesAndFullScreenInTinyEditor":
					log.info("INFO: Validate other text attributes functionality of Tiny Editor");
					tui.verifyOtherTextAttributesAndFullScreenInTinyEditor(community.getDescription());
					break;
				case "verifyFindReplaceInTinyEditor":
					log.info("INFO: Validate Find and Replace functionality of Tiny Editor");
					tui.verifyFindReplaceInTinyEditor(community.getDescription());
					break;
				case "verifyInsertLinkImageInTinyEditor":
					log.info("INFO: Validate Link Image functionality of Tiny Editor");
					tui.verifyInsertLinkImageInTinyEditor(community.getDescription());
					break;
				case "verifyTextColorInTinyEditor":
					log.info("INFO: Validate Font Text Color functionality of Tiny Editor");
					tui.verifyTextColorInTinyEditor(community.getDescription());
					break;
				case "verifyBackGroundColorInTinyEditor":
					log.info("INFO: Validate Font BackGround Color functionality of Tiny Editor");
					tui.verifyBackGroundColorInTinyEditor(community.getDescription());
					break;
				case "verifyWordCountInTinyEditor":
					log.info("INFO: Validate Word Count functionality of Tiny Editor");
					tui.verifyWordCountInTinyEditor(community.getDescription());
					break;
				case "verifyUploadImageFromDiskInTinyEditor":
					log.info("INFO: Validate Upload image from Disk functionality of Tiny Editor");
					tui.verifyUploadImageFromDiskInTinyEditor();
					break;
				case "verifyBlockQuoteInTinyEditor":
					log.info("INFO: Validate Block quote functionality of Tiny Editor");
					tui.verifyBlockQuoteInTinyEditor(community.getDescription());
					break;
				case "verifyInsertMediaInTinyEditor":
					log.info("INFO: Validate Insert Media functionality of Tiny Editor");
					tui.verifyInsertMediaInTinyEditor(community.getDescription());
					break;
				case "verifyLinkToConnectionsFilesInTinyEditor":
					log.info("INFO: Validate Link to connections files from files in Tiny Editor");
					tui.addLinkToConnectionsFilesInTinyEditor(community.getDescription());
					break;
				case "verifyCodeSampleIntinyEditor":
					log.info("INFO: Validate Code Sample functionality of Tiny Editor");
					tui.verifyCodeSampleIntinyEditor(community.getDescription());
					break;
				case "verifyInsertiFrameInTinyEditor":
					log.info("INFO: Validate Insert iFrame functionality of Tiny Editor");
					tui.verifyInsertiFrameInTinyEditor(community.getDescription());
					break;
				}
			}
		}

		String TEText = tui.getTextFromTinyEditor();
		log.info("INFO: Get the text from Tiny Editor body" + TEText);

		if(community.getDescription().contains("RichCon"))
		{
			log.info("INFO: Saving community widget rich content" + community.getName());
			this.driver.getSingleElement(CommunitiesUIConstants.rteSave).click();
		}
		else
		{
		// Save the community
		log.info("INFO: Saving the community " + community.getName());
		this.driver.getSingleElement(CommunitiesUIConstants.SaveButton).click();

		fluentWaitTextPresent(community.getName());

		// Get url with params after UUID removed
		String webUrl = this.driver.getCurrentUrl().split("&")[0];
		community.setWebAddress(webUrl);
		// Strip the UUID out of the weburl, left communityUuid= for legacy
		community.setCommunityUUID(webUrl.split("\\?")[1]);

		log.info("INFO: " + community.getName() + " was created successfully");
		}
		return TEText;
	}

	public boolean isMemberOfCommunity(User userToVerified) {
		Boolean found = false;
		List<Element> Members = driver.getVisibleElements(CommunitiesUIConstants.getCommunityMembers);
		String[] OrgData = getnameFromMemberList(Members);

		for (int i = 0; i < OrgData.length; i++) {
			log.info("Member name is: " + OrgData[i].trim());
			if (OrgData[i].equals(userToVerified.getDisplayName())) {
				found = true;
				break;
			}
		}

		return found;
	}

	public String getCommunityText() {
		return this.getFirstVisibleElement(CommunitiesUIConstants.communityDescription).getText();
	}

	public String getRichContentText() {

		fluentWaitElementVisible(CommunitiesUIConstants.richContentDescription);
		return this.getFirstVisibleElement(CommunitiesUIConstants.richContentDescription).getText();
	}

	public void verifyInsertedLink(BaseCommunity community)
    {
        TinyEditorUI tui = new TinyEditorUI(driver);
        tui.verifyInsertedLinkinCommunityDescription(community.getRichContent());
    }

	public void verifyRichContentAndOpen() {

		Assert.assertTrue(driver.getSingleElement(CommunitiesUIConstants.richContentWidgetTitleTE).getText().equalsIgnoreCase("Rich Content"),
				"ERROR: Rich content header is not visible");
		driver.getSingleElement(CommunitiesUIConstants.rteAddContent).click();
	}

	public String editDescriptionInTinyEditor(BaseCommunity community, String ediDesc) {
		TinyEditorUI tui = new TinyEditorUI(driver);
		String editedDesc;

		if (community.getRichContent()) {
			log.info("Adding content rich ");
			verifyRichContentAndOpen();
			tui.addDescriptionInrichContent(community.getDescription());
			driver.getSingleElement(CommunitiesUIConstants.rteSave).click();
			driver.getSingleElement(CommunitiesUIConstants.richContentActions).click();
		} 
		else if(ediDesc.contains("Subcom"))
		{
			fluentWaitElementVisible(CommunitiesUIConstants.editSubCommunityDescription);
			driver.getSingleElement(CommunitiesUIConstants.editSubCommunityDescription).click();
		}
		else {

			fluentWaitElementVisible(CommunitiesUIConstants.editCommunityDescription);
			driver.getSingleElement(CommunitiesUIConstants.editCommunityDescription).click();

		}

		driver.typeNative(Keys.ARROW_DOWN);
		driver.typeNative(Keys.ARROW_DOWN);
		driver.typeNative(Keys.ENTER);
		tui.verifyEditDescription(ediDesc);

		if (community.getRichContent()) {
			driver.getSingleElement(CommunitiesUIConstants.rteSave).click();
			editedDesc = driver.getFirstElement(TinyEditorUI.tinyEditorRichContentDOMPara).getText();
		} else {
			driver.getSingleElement(CommunitiesUIConstants.editCommunitySaveAndClose).click();
			editedDesc = driver.getFirstElement(CommunitiesUIConstants.communityDescription).getText();
		}
		return editedDesc;

	}

	/**
	 * This method is used to verify the image preview
	 * @param Base community object
	 * @param image selected from Files/This community before saving
	 */
	public void verifyInsertedImageLinkFromRichContent(String imageName)
	{
		//this.getFirstVisibleElement(richContentDescription+ " a:contains(View Details)").click();
		this.clickLinkWithJavascript(CommunitiesUIConstants.richContentDescription+ " a:contains(View Details)");
		WebDriver wd = (WebDriver) driver.getBackingObject();
		Set<String> whandleset = wd.getWindowHandles();
		for(String s:whandleset)
		{
			wd.switchTo().window(s);
		}
		Assert.assertTrue(driver.getFirstElement(CommunitiesUIConstants.imageNameInPreview).isDisplayed());
	}

	/**
	 * This method is used to add Rich content Tiny Editor
	 * @param Text to be added in Rich content Tiny Editor body
	 */
	public void addRichContent(String text)
	{
		TinyEditorUI tui = new TinyEditorUI(driver);
		fluentWaitPresent(CommunitiesUIConstants.rteAddContent);
		clickLinkWithJavascript(CommunitiesUIConstants.rteAddContent);
		tui.typeInTinyEditor(text);
		driver.getFirstElement(CommunitiesUIConstants.rteSave).click();
	}

	/**
	 * This method is verify that the community created via api is displayed in My Community page.
	 * @param Text to be added in Rich content Tiny Editor body
	 */
	public Boolean isCreatedCommunityDisplayed(BaseCommunity community)
	{
		CommunitiesUIConstants.communitiesNames = CommunitiesUIConstants.communitiesNames.replaceAll("PLACEHOLDER", community.getName());
		fluentWaitPresentWithRefresh(CommunitiesUIConstants.communitiesNames);
		return driver.isElementPresent(CommunitiesUIConstants.communitiesNames);

	}

	public Boolean isHighlightDefaultCommunityLandingPage() {
		try {
			String gk_flag = Data.getData().gk_communities_highlights_as_overview;
			log.info("INFO: Check to see if the Gatekeeper " + gk_flag + " setting is enabled");
			boolean value = checkGKSetting(gk_flag);
			return value;
		} catch (NullPointerException npe)  {
			// for CNX server before 7.0
			return false;
		}
	}

	/**
	 * This method will create a template.
	 * @param commUrl Url of community to create template
	 * @param templateName Name of the template to be created
	 * @param User Admin User required for template creation
	 */
	public void createTemplate(String commUrl, String templateName, User adminUser) {

		Supplier<Void> adminActions = () -> {
			log.info("Creating "+templateName+" from Tailored Experience admin page");
			clickLinkWait(CommunitiesUIConstants.templateAdmin);
			clickLinkWait(CommunitiesUIConstants.templateBtn);
			getFirstVisibleElement(CommunitiesUIConstants.commURL).type(commUrl);
			getFirstVisibleElement(CommunitiesUIConstants.commName).type(templateName);
			getFirstVisibleElement(CommunitiesUIConstants.tempDes).type(templateName + " Description");
			clickLinkWait(CommunitiesUIConstants.createBtn);

			isTextPresent(templateName + " Description");
			// need to turn off driver implicit wait to let WebDriverWait control the wait time
			driver.turnOffImplicitWaits();
			log.info("Wait up to " + TimeUnit.MILLISECONDS.toSeconds(cfg.getTestConfig().getImplicitWait()) + " secs for the loading icon to disppear.");
			WebDriverWait wait = new WebDriverWait((WebDriver)driver.getBackingObject(), cfg.getTestConfig().getImplicitWait());
			wait.until(ExpectedConditions.invisibilityOfElementLocated(
					By.cssSelector("svg.MuiCircularProgress-svg")));
			driver.turnOnImplicitWaits();

			return null;
		};

		performTEAdminActions(adminActions, adminUser, this);
	}

	/**
	 * This method will delete a template.
	 * @param templateName Name of the template to be created
	 * @param User Admin User required for template creation
	 */
	public void deleteTemplate(String templateName, User adminUser) {
		log.info("Deleting template "+templateName);
		getFirstVisibleElement(CommunitiesUIConstants.searchBoxAdminPage).clear();
		getFirstVisibleElement(CommunitiesUIConstants.searchBoxAdminPage).type(templateName);
		driver.changeImplicitWaits(5);
		getFirstVisibleElement(getTemplateName(templateName)).click();
		getFirstVisibleElement(CommunitiesUIConstants.deleteTemplateButton).click();
		getFirstVisibleElement(CommunitiesUIConstants.yesButtonDeleteConfrimation).click();
		fluentWaitElementVisible(CommunitiesUIConstants.searchBoxAdminPage);
		getFirstVisibleElement(CommunitiesUIConstants.searchBoxAdminPage).clear();
		getFirstVisibleElement(CommunitiesUIConstants.searchBoxAdminPage).type(templateName);
		Assert.assertTrue(fluentWaitTextNotPresent(templateName),
				"ERROR: '" + templateName + "' shows up in template list view after deletion.");
		log.info("Template "+templateName+" has been deleted successfully");
		driver.turnOnImplicitWaits();
	}

	public List<String> deleteTemplates(List<String> templateNames, User adminUser) {

		Supplier<List<String>> adminActions = () -> {
			List<String> notDeleted = new ArrayList<String>();

			if (!templateNames.isEmpty())  {
				clickLinkWait(CommunitiesUIConstants.templateAdmin);
				clickLinkWait(CommunitiesUIConstants.searchBoxAdminPage);

				for (String template : templateNames) {
					try {
						deleteTemplate(template, adminUser);
					} catch (org.openqa.selenium.TimeoutException | AssertionError te)  {
						log.error("Cannot delete template: " + template + ". Error: " + te.getMessage());
						notDeleted.add(template);
					}
				}
			}
			return notDeleted;
		};

		return performTEAdminActions(adminActions, adminUser, this);
	}

	/**
	 * Perform given actions in the TE admin page
	 * @param <T>
	 * @param adminActions - actions to perform
	 * @param User Admin User required for template admin actions
	 */
	public static synchronized <T> T performTEAdminActions(Supplier<T> adminActions, 
			User adminUser, ICBaseUI ui) {
		// TE admin page requires user to login to other component first then switch to admin
		log.info("Loading connection url and login with admin user "+adminUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities, true);
		ui.login(adminUser);
		log.info("Loading Tailored Experience admin page");
		ui.loadComponent(Data.getData().TailoredExperience_Admin, true);

		// start given actions
		T results = adminActions.get();

		log.info("Loading connection page again");
		ui.loadComponent(ui.extractComponent(Data.getData().ComponentCommunities), true);
		ui.waitForPageLoaded(ui.getDriver());
		ui.logout();

		return results;
	}



	/** This method will change the Landing Page of Community as Overview, if default is Highlights.
	 * @param APICommunitiesHandler, Community 
	 */
	public void changingCommunityLandingPage(APICommunitiesHandler apiOwner, Community comAPI)
	{
		log.info("Changing the Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = isHighlightDefaultCommunityLandingPage();
		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
	}

	/** This method will click on Home from Tailored experience admin page.
	 */
	public void clickHomePageButton() {
		log.info("Loading connection page again");
		switchToFrameByTitle("Homepage");
		fluentWaitElementVisible(CommunitiesUIConstants.homePage);
		clickLink(CommunitiesUIConstants.homePage);
		switchToTopFrame();

	}

	/** This method will create a category within tailored experience admin page.
	 * @param categoryName
	 */
	public void createCategory(String categoryName) {
		log.info("Creating New Category");
		clickLinkWait(CommunitiesUIConstants.templateAdmin);
		clickLinkWait(CommunitiesUIConstants.templateCategory);
		clickLinkWait(CommunitiesUIConstants.templateCategoryNew);
		clickLinkWait(CommunitiesUIConstants.templateCategoryName);
		getFirstVisibleElement(CommunitiesUIConstants.templateCategoryName).type(categoryName);
		clickLinkWait(CommunitiesUIConstants.templateCategoryDesc);
		getFirstVisibleElement(CommunitiesUIConstants.templateCategoryDesc).type("Test Description");
		clickLinkWait(CommunitiesUIConstants.templateCategoryAdd);

		log.info("Search New Category");
		clickLinkWait(CommunitiesUIConstants.templateCategorySearch);
		getFirstVisibleElement(CommunitiesUIConstants.templateCategorySearch).type(categoryName);

		log.info("Verify New Category Added");
		isTextPresent(categoryName);
		String categoryNameText = getElementText(getCategoryName(categoryName));
		log.info("Category with name " + categoryNameText + "is successfully created");

	}

	/** This method will edit category within tailored experience Admin page.
	 * @param categoryName
	 */
	public void editCategory(String categoryNameEdited) {

		log.info("Editing New Category");
		getFirstVisibleElement(CommunitiesUIConstants.editCategory).click();

		log.info("Editing Category Name");
		clickLinkWait(CommunitiesUIConstants.templateCategoryName);
		// observed that .clear doesn't work on Chrome for this input box
		getFirstVisibleElement(CommunitiesUIConstants.templateCategoryName).getWebElement().sendKeys(Keys.chord(Keys.CONTROL,"a", Keys.DELETE));

		getFirstVisibleElement(CommunitiesUIConstants.templateCategoryName).type(categoryNameEdited);

		log.info("Editing Category Description");
		clickLinkWait(CommunitiesUIConstants.templateCategoryDesc);
		String categoryDescEdited = getFirstVisibleElement(CommunitiesUIConstants.templateCategoryDesc).getText()+"edited";
		// observed that .clear doesn't work on Chrome for this input box
		getFirstVisibleElement(CommunitiesUIConstants.templateCategoryDesc).getWebElement().sendKeys(Keys.chord(Keys.CONTROL,"a", Keys.DELETE));
		getFirstVisibleElement(CommunitiesUIConstants.templateCategoryDesc).type(categoryDescEdited);

		getFirstVisibleElement(CommunitiesUIConstants.saveEditedCategory).click();

		log.info("Verify Edited Category");
		isTextPresent(categoryNameEdited);
		String editedCategoryNameText = getElementText(getCategoryName(categoryNameEdited));
		log.info("Template with category name " + editedCategoryNameText + "is successfully edited");

	}

	/** This method will delete category from tailored experience Admin page.
	 * @param categoryName
	 */
	public void deleteCategory(String categoryNameEdited) {
		clickLinkWait(CommunitiesUIConstants.templateAdmin);
		clickLinkWait(CommunitiesUIConstants.templateCategory);

		fluentWaitElementVisible(CommunitiesUIConstants.templateCategorySearch);
		getFirstVisibleElement(CommunitiesUIConstants.templateCategorySearch).clear();
		getFirstVisibleElement(CommunitiesUIConstants.templateCategorySearch).type(categoryNameEdited);

		log.info("Deleting Category");
		List<Element> elements = driver.getVisibleElements(CommunitiesUIConstants.editCategory);
		Element deleteCategory = elements.get(1);

		deleteCategory.click();

		clickLinkWait(CommunitiesUIConstants.yesButtonDeleteConfrimation);

		fluentWaitElementVisible(CommunitiesUIConstants.templateCategorySearch);
		getFirstVisibleElement(CommunitiesUIConstants.templateCategorySearch).clear();
		getFirstVisibleElement(CommunitiesUIConstants.templateCategorySearch).type(categoryNameEdited);
		Assert.assertTrue(fluentWaitTextNotPresent(categoryNameEdited),
				"ERROR: '" + categoryNameEdited + "' shows up in template list view after deletion.");
		log.info("Template "+categoryNameEdited+" has been deleted successfully");

	}

	/** This method will edit existing template from tailored experience Admin page.
	 * @param templateName, categoryName, templateNameEdited, templatedDescriptionEdited, fileA, fileB
	 */
	public void editTemplate(String templateName, String categoryName, String templateNameEdited, String templateDesEdited, BaseFile fileA, BaseFile fileB) {
		selectTemplateToEdit(templateName);

		driver.getSingleElement(CommunitiesUIConstants.uploadLogoBtn).typeFilePath(FilesUI.getFileUploadPath(fileA.getName(), cfg));
		driver.getSingleElement(CommunitiesUIConstants.uploadPreviewBtn).typeFilePath(FilesUI.getFileUploadPath(fileB.getName(), cfg));

		getFirstVisibleElement(CommunitiesUIConstants.templateNameBox).click();
		getFirstVisibleElement(CommunitiesUIConstants.templateNameBox).clear();
		getFirstVisibleElement(CommunitiesUIConstants.templateNameBox).type(templateNameEdited);
		getFirstVisibleElement(CommunitiesUIConstants.templateDescriptionBox).click();
		getFirstVisibleElement(CommunitiesUIConstants.templateDescriptionBox).clear();
		getFirstVisibleElement(CommunitiesUIConstants.templateDescriptionBox).type(templateDesEdited);
		scrollIntoViewElement(CommunitiesUIConstants.selectCategory);
		clickLinkWait(CommunitiesUIConstants.selectCategory);
		clickLinkWait(getCategoryLocator(categoryName));
		// observed ElementClickInterceptedException in the next 2 clicks if clickLinkWait is used
		clickLinkWithJavascript(CommunitiesUIConstants.editTemplateLabel);
		clickLinkWithJavascript(CommunitiesUIConstants.saveEditedCategory);
		Assert.assertTrue(fluentWaitTextNotPresentWithoutRefresh("Preview Template"), 
				"ERROR: '" + templateName + "' edit is not saved.");
	}

	/** This method will search and edit template from tailored experience Admin page.
	 * @param templateName
	 */
	public void selectTemplateToEdit(String templateName) {
		clickLinkWait(CommunitiesUIConstants.templateAdmin);
		clickLinkWait(CommunitiesUIConstants.searchBoxAdminPage);
		log.info("Search template "+templateName);
		getFirstVisibleElement(CommunitiesUIConstants.searchBoxAdminPage).type(templateName);
		getFirstVisibleElement(getTemplateName(templateName)).click();
		getFirstVisibleElement(CommunitiesUIConstants.editTemplateButton).click();

	}

	/** This method will set a category to a TE template.
	 * @param categoryName, templateName
	 */
	public void setCategoryToTemplate(String categoryName, String templateName) {
		selectTemplateToEdit(templateName);
		scrollIntoViewElement(CommunitiesUIConstants.selectCategory);
		clickLinkWait(CommunitiesUIConstants.selectCategory);
		clickLinkWait(getCategoryLocator(categoryName));
		// observed ElementClickInterceptedException in the next 2 clicks if clickLinkWait is used
		clickLinkWithJavascript(CommunitiesUIConstants.editTemplateLabel);
		clickLinkWithJavascript(CommunitiesUIConstants.saveEditedCategory);
	}

	/** This method will search a template which is having category.
	 * @param categoryName, templateName
	 */
	public void searchTemplateWithCategory(String categoryName, String templateName){
		clickLinkWait(CommunitiesUIConstants.templateAdmin);
		clickLinkWait(CommunitiesUIConstants.templateLabel);
		fluentWaitElementVisible(CommunitiesUIConstants.searchBoxAdminPage);
		getFirstVisibleElement(CommunitiesUIConstants.searchBoxAdminPage).clear();
		clickLinkWait(CommunitiesUIConstants.selectCategoryToSearch);
		clickLinkWait(getCategoryLocator(categoryName));
		clickLinkWait(CommunitiesUIConstants.selectCategoryToSearch);

		isTextPresent(templateName + " Description");
		String templateText = getElementText(getTemplateName(templateName));
		log.info("Template with name " + templateText + "is set to category "+categoryName);
	}

	/** This method will search and select a template.
	 * @param templateName
	 */
	public void selectTemplate(String templateName) {
		clickLinkWait(CommunitiesUIConstants.templateAdmin);
		clickLinkWait(CommunitiesUIConstants.searchBoxAdminPage);
		getFirstVisibleElement(CommunitiesUIConstants.searchBoxAdminPage).type(templateName);
		getFirstVisibleElement(getTemplateName(templateName)).click();
	}

	/** This method will search and verify a template if it is deleted.
	 * @param templateName
	 */
	public void verifyTemplateDeleted(String templateName) {
		loadComponent(Data.getData().TailoredExperience_Admin, true);
		clickLinkWait(CommunitiesUIConstants.templateAdmin);
		clickLinkWait(CommunitiesUIConstants.searchBoxAdminPage);
		getFirstVisibleElement(CommunitiesUIConstants.searchBoxAdminPage).type(templateName);
		Assert.assertTrue(fluentWaitTextNotPresent(templateName),"ERROR: '" + templateName + "' shows up in template list view after deletion.");
		log.info("Template "+templateName+" has been deleted successfully");
	}

	/** This method will return a locator having username.
	 * @param User
	 */
	public String getUserName(User user) {
		return "css=div.lotusFloatContent.commFocusPT > span > a:contains(" + user.getDisplayName() + ")";
	}

	/** This method will return a Forum Topic Link.
	 * @param String
	 */
	public String getForumTopic(String title) {
		return "xpath=//div[@class='lotusHeader']//h1//span[contains(text(),'"+ title + "')]/ancestor::div[@class='lotusPostContent']//a[text()='Like']";
	}
		
	/** This method will return a Topic Link.
	 * @param String
	 */

	public String getTopic(String title) {
		return "xpath=//div[@class='lotusHeader']//h1//span[contains(text(),'" + title + "')]/ancestor::div[@class='lotusPostContent']//div[@class='lotusLikeText']";
	}
	 
	/** This method will return a Forum Topic Link.
	 * @param String, String
	 */

	public String getAdminUserForumTopic(String title, String adminUser) {
		return "xpath=//span[contains(text(),'" + title + "')]//ancestor::div[@class='lotusPostContent']//preceding-sibling::div[@class='lotusPostAuthorInfo']//a[text()='" + adminUser + "']";
	}

	/** This method will return a User reply link.
	 * @param String
	 */
	
	public String adminUserReply(String displayName) {
		return "xpath=//*[contains(text(),'Topic Reply')]/ancestor::div[@class='lotusPostContent']//preceding-sibling::div[@class='lotusPostAuthorInfo']//a[text()='" + displayName + "']";
	}
	
	/** This method will return adminUser link.
	 * @param String, User, Int
	 */
	public String getLinkOwner(String commName, User adminUser, int i) {
		return "xpath=//a[contains(text(),'" + commName + "')]/ancestor::tr/following-sibling::tr/td["+i+"]//a[text()='" + adminUser.getDisplayName() + "']";
	}

	/** This method will return bookmark title link.
	 * @param String
	 */
	public String getBookmarkTitle(String title) {
		return "css=a.commFocusML > span.bidiAware:contains(" + title + ")";
	}

	/** This method will return Bookmark author link.
	 * @param String, String
	 */
	public String getBookmarkAuthor(String title, String displayName) {
		return "xpath=//*[text()='" + title + "']/ancestor::tr//div[@class='lotusMeta']//a[@title='" + displayName + "']";
	}

	/** This method will return Bookmark link.
	 * @param String, String
	 */
	public String getBookmarkLink(String url, String title) {
		return "css=div#importantBookmarks a[href='" + url + "']:contains(" + title + ")";
	}

	/** This method will return subcommunity name link.
	 * @param BaseSubCommunity
	 */
	public String getSubCommunityName(BaseSubCommunity subcommunity) {
		return "css=tr.dijitReset.dijitMenuItem[title='" + subcommunity.getName() + "']";
	}

	/** This method will return wiki like and author link.
	 * @param String, String
	 */
	public String getWikiLikeAuthorlink(String name, String text) {
		return "xpath=//h1[@id='wikiPageHeader'][contains(text(),'" + name + "')]/following-sibling::div[@class='lotusMeta']" + text + "";
	}

	/** This method will return file author link.
	 * @param User,
	 */
	public String getFileAuthorLink(User adminUser) {
		return "css=span.header1 > span.versionNumber:contains(1) + span.vcard > a:contains(" + adminUser.getDisplayName() + ")";
	}

	/**
	 * This is to workaround browser freeze issue when clicking top nav or the
	 * Community Actions link too fast before the page is fully loaded.
	 * If this is used, no need to call waitForPageLoaded and waitForJQueryToLoad 
	 * since this is covering their wait already.  Otherwise this method will potentially add additional wait time.
	 */
	public void waitForCommunityLoaded() {
		Element introspection = driver.getSingleElement("css=link[rel='introspection']");
		String origValue = ((introspection.getAttribute("hastooltip") == null) ? "" : introspection.getAttribute("hastooltip"));
		if (origValue == null)  {
			origValue = "";
		}
	    long start = System.currentTimeMillis();
	    String newValue;
	    int count = 0;
	    
	    // wait until the hastooltip attribute for introspection stays unchanged for at least 1 secs
	    while (System.currentTimeMillis() - start < cfg.getTestConfig().getImplicitWait()) {
	    	newValue = ((introspection.getAttribute("hastooltip") == null) ? "" : introspection.getAttribute("hastooltip"));
	    	if (origValue.equalsIgnoreCase(newValue)) {
	    		count++;
	    		if (count >= 2) {
	    			break;
	    		}
	    	} else {
	    		count = 0;
	    	}
	    	
	    	origValue = newValue;
	    	sleep(500);
	    }
	}
	
	/** This method will return add comment link
	 * @param uuid,
	 */
	public String getComment(String uuid) {
		return "xpath=//div[@uuid='"+uuid+"']//a[text()='Add Comment']";
	}
	
	/** This method will return community widget header 
	 *  @param selector - selector for community widget header
	 */
	public String getCommWidgetPageHeader(String selector) {
		return "css=#lotusContent "+ selector +" h1";
	}
}
