package com.ibm.conn.auto.webui;

import java.util.Map;

import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import com.ibm.conn.auto.webui.constants.ProfilesUIConstants;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseBlogPost;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseDogear;
import com.ibm.conn.auto.appobjects.base.BaseEvent;
import com.ibm.conn.auto.appobjects.base.BaseForumTopic;
import com.ibm.conn.auto.appobjects.base.BaseHighlights;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.CommunityRole;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIForumsHandler;
import com.ibm.conn.auto.util.Helper;
import com.ibm.lconn.automation.framework.services.common.nodes.ForumTopic;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.forums.nodes.Forum;

public class IcecUI extends ICBaseUI {
	
	private BaseCommunity.Access defaultAccess;
	
	public IcecUI(RCLocationExecutor driver) {
		super(driver);
		defaultAccess = CommunitiesUI.getDefaultAccess(cfg.getProductName());
	}
	private static Logger log = LoggerFactory.getLogger(IcecUI.class);
	
	public static String customizeButton = "xpath=//button[@aria-label='Adminpanel']";
	public static String myCommunitiesWidgetEditButton = "css=div[aria-label='my-communities'] i[class='wedit fa fa-bars']";

	public static String contentSourcesDropdown="xpath=//div[text()='Content sources']";
	public static String displaySettingDropdown="xpath=//div[text()='Display settings']";
	public static String advancedSettingDropdown="xpath=//div[text()='Advanced settings']";
	
	// xcc/admin
	public static String cachingForAdminsLabel = "css=span.input-group-addon.content.cachingforadmins";
	public static String cachingForAdminsCheckbox = "css=#cachingCheckbox";
	public static String openCommLinksInNewTabLabel = "css=span.input-group-addon.content.newtabforcommunitylinks";
	public static String openCommLinksInNewTabCheckbox = "css=#sourceLink";
	public static String allowUrlDebuggingLabel = "css=span.input-group-addon.content.allowurldebugging";
	public static String allowUrlDebuggingCheckbox = "css=#allowUrlDebugging";
	public static String enableNewIcecStylesLabel = "css=span.input-group-addon.content.enableNewStyles";
	public static String enableNewIcecStylesCheckbox = "css=#enableNewStyles";
	public static String externalImagesLabel = "css=span.input-group-addon.content.extimghandling";
	public static String enforceHttpsBtn = "css=label.btn.btn-default.harmonize";
	public static String useProxyBtn = "css=label.btn.btn-default.proxy";
	public static String allowHttpBtn = "css=label.btn.btn-default.nochange";
	public static String enableBackupCheckbox = "css=div.admp-backup #checkbox";
	public static String backupIntervalDropdown = "css=#intervall";
	public static String addCustomPropertyBtn = "css=button[rel=add]";
	public static String customPropertiesKeyLabel = "css=#page-wrapper > div > div > div > div.panel.panel-default > div.panel-body > div:nth-child(1) > label > span";
	public static String customPropertiesValueLabel = "css=#page-wrapper > div > div > div > div.panel.panel-default > div.panel-body > div:nth-child(2) > label > span";
	public static String languageFilesList = "css=div[rel=files-list] > div[class=list-group]";
	public static String allowIntegrationsSlider = "css=span.slider";
	public static String reInitLanguageFilesBtn = "css=button.reinitlanguagefiles";
	public static String saveBtn = "css=button.savebtn";
	public static String backToHomepage = "css=a.backButton:contains(Back to homepage)";

	// Admin Panel
	public static String widgetsTab = "css=#ui-id-1 > span";
	public static String pageSettingsTab = "css=#ui-id-3 > span";
	public static String pageMgmtTab = "css=#ui-id-4 > span";
	public static String icecSettingsTab = "css=#ui-id-4 > span";
	public static String createWidgetLink = "xpath=//span[text()= 'Widgets']";
	public static String typeDropdown = "css=select[name='type']";
	public static String idTextField = "css=label[class='input-group glue-next'] input[class='form-control xccEllipsis']";
	public static String widgetDialogCreateButton = "css=button[class='bx--btn bx--btn--primary']";
	public static String widgetCreateButton = "css=button[class='bx--btn  bx--btn--primary btn-primary']";
	public static String modalYesButton = "css=div[class='modal-footer'] button.btn-primary";
	public static String widgetTileDelete = "css=span.delete_icon";

	//Admin Panel - Page Settings
	public static String advancedSettingsButton = "css=button.advanced-settings-link";
	public static String restrictedButton = "//span[contains(text(), 'Restricted')]";
	public static String publicButton = "//span[contains(text(), 'Public')]";
	public static String anonymousButton = "//span[contains(text(), 'Anonymous')]";
	public static String pageReaderLabel = "css=label.are-page-readers span.cuteditoritem";
	public static String pageEditorTextField = "css=label.are-page-editors input.ui-autocomplete-input";
	public static String navigationCheckbox = "css=div.panel-heading.activate_NAV > label > input";
	public static String activityStreamCheckbox = "css=#actStreamBox";
	public static String rightColumnCheckbox = "css=#rightColumnBox";
	public static String isTemplateCheckbox = "css=input[aria-label='Is Template']";
	public static String activityStream = "css=div.actStream";
	public static String rightColumn = "css=#lotusColRight";
	public static String layoutDropdown = "css=label.layoutLabel > select";
	public static String gridLink = "css=span.gridText";
	public static String gridCancelButton = "css=div.modal-footer > button";
	public static String titleTextField = "css=input[name='title']";
	public static String settingsSave = "css=i.fa-save";
	public static String title = "css=span.lotusText";

	//Admin Panel - Page Management
	public static String searchTextField = "css=div.pageManagement.panel.panel-default > div.panel-heading > label > input";
	public static String showTemplatesCheckbox = "css=input[title='Show templates']";
	public static String createPageButton = "css=button.create-page";
	public static String createPageIdField = "css=label.input-group.has-success > input";
	public static String createPageTitleField = "css=label:nth-child(4) > input";
	public static String importPagesButton = "css=button[rel='import']";
	public static String exportAllPagesButton = "css=button.export_pages";
	public static String selectFilesDropzone = "css=span.xcc-fi-dropzone";
	public static String xButton = "css=i.close";

	// Highlights
	public static String communityActionsLink = "css=#displayActionsBtn";
	public static String addAppsLink = "css=#communityMenu_CUSTOMIZE_text";
	public static String addHighlightsAppLink = "css=div.app_palette_modal a:contains(Highlights)";
	public static String closeAppsPalette = "css=img[aria-label='Close palette']";
	public static String highlightsTab = "css=li[widgetdefid='ICEC2'] a";
	public static String eventsTab = "css=li[widgetdefid='Calendar'] a";
	public static String moreDropdown = "css=#tabNavMoreBtn a";
	public static String highlightsDropdownLink = "css=tr[aria-label='Highlights ']";
	public static String eventsDropdownLink = "css=tr[aria-label='Events ']";
	public static String editViewIdType = "css=span[title='xccCommunityDescription']";
	public static String communityDescription = "css=div[aria-label='description']";
	public static String getWidgetEditRemove = "css=button.bx--overflow-menu-options__btn:contains(Remove Widget)";
	public static String getWidgetEditConfigure = "xpath=//button[contains(text(),'Edit')]";
	public static String searchCheckbox = "css=input[aria-label='Search']";
	public static String liveEditorForm = "div.live-editor_form";
	public static String editRichContentWidget ="xpath=//div[@class='wtitle'][text()='PLACEHOLDER']/..//div[@class='wedit bx--action-icon ']/button";
	public static String richContentWidgetText = "xpath=//div[text()='PLACEHOLDER']/../..//div[@class='wbody clearfix']/div/div";
	public static String editButtonRichContentWidget = "css=button:contains(Edit)";
	public static String saveSetting ="css=[class='bx--btn bx--btn--primary']";
	public static String editRichContentLink = "xpath=//button[@class='bx--overflow-menu-options__btn'][text()='Edit']";
	public static String saveTinyEditor="css=[class='bx--btn  bx--btn--primary btn-primary'], input[id=create_saveclose]" ;
	 
	// Highlights - Customize
	public static String highlightsWidgetsTab = "css=#ui-id-5";
	public static String highlightsPageSettingsTab = "css=div[class='topmenu'] li[role='tab'] a:contains('Page Settings')";
	public static String highlightsPageMgmtTab = "css=#ui-id-7";
	public static String highlightsIcecSettingsTab = "css=#ui-id-8";
	public static String actionsMenuRemove = "css=td[id^='dijit_MenuItem_']:contains(Remove)";
	public static String actionsMenuEdit = "xpath=//tr[not(contains(@style, 'user-select: none;'))]/td[contains(text(),'Edit')]";
	public static String actionsSaveAndClose = "css=input[value='Save and Close']";
	public static String deleteAppNameTextField = "css=input[name='communityName']";
	public static String deleteUserNameTextField = "css=input[name='signature']";
	public static String deleteOKButton = "css=button[dojoattachpoint='okButtonNode']";
	public static String showCommunitiesCheckbox = "css=input[title='Include community pages']";
	public static String pageSettingsSave ="css=div.submit-button button.bx--btn--primary";
	public static String pageMgmtFileClose = "css=button.bx--modal-close";
	public static String pageMgmtExportAllButton = "css=button.export_pages";

	public static String flyoutPageLabel = "css=div.flyoutPageConfigPanel label.checkbox-inline";
	public static String flyoutPanelPageLabel = "css=div.flyoutPageConfigPanel span[title='Page']";
	public static String flyoutPanelPageTextField = "css=div.flyoutPageConfigPanel input[name='flyoutPage']";
	public static String flyoutPanelHeightLabel = "css=div.flyoutPageConfigPanel span[title='Height']";
	public static String flyoutPanelHeightTextField = "css=div.flyoutPageConfigPanel input[name='flyoutHeight']";
	public static String flyoutPanelWidthLabel = "css=div.flyoutPageConfigPanel span[title='Width']";
	public static String flyoutPanelWidthTextField = "css=div.flyoutPageConfigPanel input[name='flyoutWidth']";
	public static String flyoutPanelSaveBtn = "css=div.flyoutPageConfigPanel button[rel='save']";
	public static String flyoutPanelDeleteBtn = "css=div.flyoutPageConfigPanel button[rel='delete']";
	public static String flyoutPanelCancelBtn = "css=div.flyoutPageConfigPanel button[rel='cancel']";

	public static String customizationFilesLabel = "css=div.customization label.checkbox-inline";
	public static String customCssFileLabel = "css=div.customization td[data-sv='custom.css']";
	public static String customJsFileLabel = "css=div.customization td[data-sv='custom.js']";
	public static String faviconIcoFileLabel = "css=div.customization td[data-sv='favicon.ico']";
	public static String customizationFilesSearchLabel = "css=div.customization span[title='Search:']";
	public static String customizationFilesSearchTextField = "css=div.customization input[rel='filter']";
	public static String customizationFilesUploadButton = "css=div.customization span.btn-fileupload";
	public static String customizationFilesUploadDialog ="css=div[aria-label='Import Custom Files'] span.xcc-fi-dropzone";

	public static String languageFilesLabel = "css=div.i18n label.checkbox-inline";
	public static String languageFilesSearchLabel = "css=div.i18n span[title='Search:']";
	public static String languageFilesSearchTextField = "css=div.i18n input[rel='filter']";
	public static String languageFilesUploadButton = "css=div.i18n span.btn-fileupload";
	public static String languageFilesUploadDialog ="css=div[aria-label='Import Language Files'] span.xcc-fi-dropzone";

	//Highlights - Events
	public static String prevButton = "css=button.fc-prev-button";
	public static String nextButton = "css=button.fc-next-button";
	public static String todayButton = "css=button.fc-today-button";
	public static String selectDateButton = "css=button.fc-datepicker-button";

	public static String dayButton = "css=button.fc-agendaDay-button";
	public static String dayListButton = "css=button.fc-basicDay-button";
	public static String weekButton = "css=button.fc-agendaWeek-button";
	public static String weekListButton = "css=button.fc-basicWeek-button";
	public static String monthButton = "css=button.fc-month-button";
	public static String upcomingButton = "css=button.fc-listYear-button";

	//Highlights - Forum
	public static String likeButton = "css=a.like-button";
	public static String noOfLikesLabel = "css=div.lotusLikeText";

	//Highlights - Static Content Blogs
	public static String latestBtn = "css=label[class='btn btn-default']";
	public static String selectedBtn = "css=label.input-group.glue-prev fieldset label:nth-child(3)";
	public static String plusBtn = "css=button.btn.btn-default";
	public static String selectPostTextField = "css=div[rel='postsContainer'] input.form-control.xccEllipsis.ui-autocomplete-input";
	public static String editBlogPostLink = "css=a.lotusAction:contains(Edit)";
	public static String postBtn = "css=#postEntryID";
	public static String selectComSourceField = "css=input.ui-autocomplete-input";
	
	//Highlights - Links
	public static String numberOfItemsPerPage = "css=input[placeholder='Number of Items per Page']";
	
	//Hightlights - Ideation Blogs
	public static String ideaVotingButton = "css=div.ideaVotingButtonDiv";
	public static String ideaVotingNumber = "css=div.ideaVotingNumber";
	public static String blogPopup = "css=div.lotusFlyout";
	public static String blogPopupTitle = "css=div.lotusFlyout h2 a";

	//Highlights - News
	public static String likesAndCommentsCheckbox = "css=input[name='likesAndComments']";
	public static String allowContentCreationCheckbox ="css=input[name='allowContentCreation']";
	public static String autoSlideCheckbox ="css=input[name='autosliding']";
	public static String slidingSpeedTextField ="css=input[name='slidingspeed']";
	public static String previousButton = "css=div.xccNewsSlider button.slick-prev";
	public static String currentSliderPostTitle = "css=div.xccNewsSlider div.slick-current h3 a";
	public static String currentSliderPostContent = "css=div.xccNewsSlider div.slick-current p.newsChannelTeaser";
	public static String socialAndContentSettingText = "xpath=//div[text()='Social and content creation settings']/..";
	//Highlights - Media Gallery
	public static String nextLinkMediaGallery = "css=div.xccMediaGallery a[aria-label='Show next items']";
	
	//Highlights - Navigation
	public static String wikiEditButton = "css=span.lotusBtn a:contains(Edit)";
	public static String wikiEditSaveClose = "css=#edit_saveclose";
	
	//Highlight - Rich Content close dialog
	public static String richContentCloseMsg = "css=a[title='Close']";
	

	// UI Strings - Page Management
	public static String FILTER_ZERO_RESULTS = "0 results found.";
	public static String HTML_HEADER = "Testing HTML Widget";
	
	public enum LiveEditorSelections {
		ADVANCED_SETTINGS("Advanced settings"),
		CONTENT_SOURCES("Content sources"),
		SOCIAL_AND_CONTENT_CREATION("Social and content creation settings"),
		DISPLAY_SETTINGS("Display settings"),
		GENERAL_SETTINGS("General settings");
		
		private final String name;
		
		private LiveEditorSelections(String s) {
			this.name = s;
		}
		
		public String toString() {
			return this.name;
		}
	}

	public static IcecUI getGui(String product, RCLocationExecutor driver) {

		return new IcecUI(driver);
		// TODO Implement this when IcecUICloud, IcecUIOnPrem etc are
		// created
		/**
		 * if(product.toLowerCase().equals("cloud")){ return new
		 * CommunitiesUICloud(driver); } else
		 * if(product.toLowerCase().equals("onprem")) { return new
		 * CommunitiesUIOnPrem(driver); } else
		 * if(product.toLowerCase().equals("production")) { return new
		 * CommunitiesUIProduction(driver); } else
		 * if(product.toLowerCase().equals("vmodel")) { return new
		 * CommunitiesUIVmodel(driver); } else
		 * if(product.toLowerCase().equals("multi")) { return new
		 * CommunitiesUIMulti(driver); }else { throw new
		 * RuntimeException("Unknown product name: " + product); }
		 */
	}
	
	public static String getSampleHTML(User user) {
		return "<body>" +
				  "<h1>" + HTML_HEADER + "</h1>" +
				  "<p>User: " + user.getDisplayName() + "</p>" +
				  "<br>" +
				  "<label for=\"fName\">First name:</label>" +
				  "<input id=\"fName\" type=\"text\" name=\"firstname\" value=\"" + user.getFirstName() + "\">" +
				  "<br>" +
				  "<label for=\"lName\">Last name:</label>" +
				  "<input id=\"lName\" type=\"text\" name=\"lastname\" value=\"" + user.getLastName() + "\">" +
				  "<br><br>" +
				  "<button>Click</button>" +
				  "</body>";
	}
	
	public static String getCommunitySourceInput(String widgetId) {
		return "css=div[data-wtype='" + widgetId + "'], input.ui-autocomplete-input";
	}
	
	public static String getCommunityDescriptionText(String widgetTitle) {
		return "css=div[aria-label='" + widgetTitle + "'] div.wbody.clearfix > div > div > div > p";
	}

	public static String getAdminDashLabel(String label) {
		return "css=span.input-group-addon." + label;
	}

	public static String getPageSettingsLabel(String label) {
		return "css=span[title='" + label + "']";
	}
	
	public static String getAppFromHiddenApps(String type) {
		return "css=div[title='" + type + "']";
	}

	public static String getWidgetTypeElement(String type) {
		return "css=div#" + type + ".widgetBox";
	}

	public static String getWidget(String widgetTitle) {
		return "css=div[aria-label^='" + widgetTitle + "']";
	}

	public static String getWidgetEditDropdown(String widgetTitle) {
		return getWidget(widgetTitle) + " button.dropdown-toggle";
	}

	public static String getWidgetEditSave(String widgetTitle) {
		return getWidget(widgetTitle) + " button.bx--btn--primary, "
				+ liveEditorForm + " button.bx--btn--primary";
	}
	
	public static String getWidgetEditCancel(String widgetTitle) {
		return getWidget(widgetTitle) + " button.bx--btn--secondary, "
				+ liveEditorForm +" button.bx--btn--secondary";
	}

	public static String getWidgetEditRemove(String widgetTitle) {
		return getWidget(widgetTitle) + " :contains(Remove)";
	}

	public static String getEditWidgetBtn(String widgetTitle) {
		return getWidget(widgetTitle) + " div.wedit button";
	}

	public static String getWidgetTile(String widgetTitle) {
		return "css=a[title*='" + widgetTitle + "']";
	}

	public static String getLayoutDropdownOption(String option) {
		return layoutDropdown + " > option[value='"+ option +"']";
	}

	public static String getPageMgmtEditPage(String pageId) {
		return "css=span.fa-wrench[data-name='" + pageId + "']";
	}

	public static String getPageMgmtCopyPage(String pageId) {
		return "css=span.fa-copy[data-name='" + pageId + "']";
	}

	public static String getPageMgmtPageIdLabel(String pageId) {
		return "css=td[rel='name'][data-sv='" + pageId + "']";
	}

	public static String getOverviewActionsMenu(String appName) {
		return "css=a[aria-label='Actions for: " + appName + "']";
	}

	public static String getPageEditorDropdownEntry(String userDisplayName) {
		return "css=div.ui-menu-item-wrapper:contains(\"^" + userDisplayName + "$\")";
	}

	public static String getToolsEditButton(String fileName) {
		return "css=td[data-name='"+ fileName +"'] span.edit";
	}

	public static String getToolsDeleteButton(String fileName) {
		return "css=td[data-name='"+ fileName +"'] span.edit";
	}

	public static String getToolsDownloadButton(String fileName) {
		return "css=td[data-name='"+ fileName +"'] span.edit";
	}

	public static String getToolsVersionButton(String fileName) {
		return "css=td[data-name='"+ fileName +"'] span.edit";
	}

	public static String getLanguageFileLabel(String fileName) {
		return "css=div.i18n td[data-sv='"+ fileName +"']";
	}

	public static String getMemberTileToggle(String widgetId) {
		return getWidget(widgetId) + " div.xcc-tile-toggle";
	}

	public static String getMemberTileNameLink(String widgetId, String user) {
		return getWidget(widgetId) + " span.xcc-tile-title:contains(" + user + ")";
	}
	
	public static String getMemberTileNameLinkXpath(String widgetId, String user) {
		return "//div[@aria-label='" + widgetId + "']/div/div/span/a/div/span[contains(@class, 'xcc-tile-title') and text()='" + user + "']";
	}

	public static String getMemberTileLink(String widgetId, String user) {
		return getWidget(widgetId) + " img[alt='Profile picture of " + user + "']";
	}

	public static String getMemberTitle(String widgetId, String user) {
		return getWidget(widgetId) + " div[style=''] span.xcc-tile-summary-title";
	}

	public static String getMemberTypeLabel(String widgetId, String user) {
		return getWidget(widgetId) + " div[style=''] span.xcc-tile-summary-body";
	}

	public static String getMemberContactImage(String widgetId, User user) {
		return getWidget(widgetId) + " img[alt='" + user.getDisplayName() + "']";
	}

	public static String getForumTopicLink(String serverURL, BaseForumTopic topic){
		return "css=div.xccForum a[data-mobile-href='"+ serverURL +"/forums/html/topic?id="+ topic.getUUID() +"']";
	}

	public static String getForumTopicDesc(String serverURL, BaseForumTopic topic){
		return "css=div[data-anchor='"+ serverURL +"/forums/atom/topic?topicUuid="+ topic.getUUID() +"'] div.abstract";
	}

	public static String getTopicFlyoutTitle(String serverURL, BaseForumTopic topic){
		return "css=div.lotusFlyout h2 a[href='"+ serverURL +"/forums/html/topic?id="+ topic.getUUID() +"']";
	}

	public static String getNewsOverviewLink(BaseBlogPost blogPost) {
		return "css=div.xccNewsOverview a:contains(" + blogPost.getTitle() + ")";
	}

	public static String getNewsOverviewContent(BaseBlogPost blogPost) {
		return "css=div.xccNewsOverview div.newsOverviewTeaser:contains(" + blogPost.getContent() + ")";
	}

	public static String getNewsContent(BaseBlogPost blogPost) {
		return "css=div.xccNews div.xccTeaser div.newsContent:contains(" + blogPost.getContent() + ")";
	}

	public static String getNewsLink(BaseBlogPost blogPost) {
		return "css=div.xccNews a:contains(" + blogPost.getTitle() + ")";
	}

	public static String getNewsListLink(BaseBlogPost blogPost) {
		return "css=div.xccNewsList a:contains(" + blogPost.getTitle() + ")";
	}
	
	public static String getTopNewsLink(BaseBlogPost blogPost) {
		return "css=div.xccTopNews a:contains(" + blogPost.getTitle() + ")";
	}

	public static String getBlogFlyoutTitle(BaseBlogPost blogPost){
		return "css=div.lotusFlyout h2 a:contains(" + blogPost.getTitle() + ")";
	}

	public static String getIdeaLink(BaseBlogPost idea){
		return "css=div.xccIdeationBlog a:contains(" + idea.getTitle() + ")";
	}

	public static String getSelectedBlogEntry(String blogTitle){
		return "css=div.ui-menu-item-wrapper:contains(" + blogTitle + ")";
	}
	
	public static String getChannelPreviousLink(int channel){
		return "css=div.xccNewsChannel div.slick-container:nth-child(" + channel + ") button.slick-prev";
	}
	
	public static String getCurrentChannelPostTitle(int channel){
		return "css=div.xccNewsChannel div.slick-container:nth-child(" + channel + ") div.slick-current h3 a";
	}
	
	public static String getCurrentChannelPostContent(int channel){
		return "css=div.xccNewsChannel div.slick-container:nth-child(" + channel + ") div.slick-current p.newsChannelTeaser";
	}
	
	public static String getCurrentChannelImage(int channel){
		return "css=div.xccNewsChannel div.slick-container:nth-child(" + channel + ") div.slick-current img";
	}
	
	public static String getOverviewWikiLink(String wikiName) {
		return "css=a.entry-title:contains(" + wikiName + ")";
	}
	
	public static String getActiveOverviewMenuAction(String activeMenuId, String sAction){
		return "css=table[id='" + activeMenuId + "'] tbody[class='dijitReset'] tr[id^='dijit_MenuItem_'] td:contains(" + sAction + ")";
	}
	
	public static String getMediaGalleryImage(int row, int column) {
		return "css=div.xccMediaGallery div:nth-child(" + row + ") > div.xccEntry:nth-child(" + column + ") a img";
	}
	
	public static String getMyCommunitiesImage(int index) {
		return "css=div.xccMyCommunities span:nth-child(" + index + ") a div.xcc-tile-slide-1";
	}
	
	public static String getMyCommunitiesTitle(BaseCommunity community) {
		return "css=div.xccMyCommunities span.xcc-tile-title:contains(" + community.getName() + ")";
	}
	
	public static String getMyCommunitiesSummaryTitle(BaseCommunity community) {
		return "css=div.xccMyCommunities span.xcc-tile-summary-title:contains(" + community.getName() + ")";
	}
	
	public static String getMyCommunitiesDescription(BaseCommunity community) {
		return "css=div.xccMyCommunities span.xcc-tile-summary-body:contains(" + community.getName() + ")";
	}
	
	public static String getMyCommunitiesTileToggle(int index) {
		return "css=div.xccMyCommunities span:nth-child(" + index + ") div.xcc-tile-toggle";
	}
	
	public static String getWikiPageLink(String wikiPageName) {
		return "css=a[title='" + wikiPageName + "']";
	}
	
	public boolean isCECErrorPresent (){
		return fluentWaitElementVisible("css=div.xccErrorText > h1:contains(Error occurred)") &&
				fluentWaitElementVisible("css=div.xccErrorText > p:contains(You do not have the entitlement to access Connections Engagement Center)");
	}
	
	public void selectType(String type) {
		fluentWaitElementVisible(createWidgetLink);
		clickLinkWithJavascript(createWidgetLink);
		String selector = getWidgetTypeElement(type);
		fluentWaitElementVisible(selector);
		scrollIntoViewElement(selector);
		clickLinkWait(selector);
		driver.typeNative(Keys.ENTER);
	}
	
	public void addWidgetsFromHiddenApps(String type) {
		clickLinkWithJavascript(getWidgetTile("Hidden Apps"));
		String selector = getAppFromHiddenApps(type);
		clickLinkWithJavascript(selector);
		driver.typeNative(Keys.ENTER);
	}

	public void removeWidget(String widgetTitle) {
		try {
			Element editButton = driver.getSingleElement(getEditWidgetBtn(widgetTitle));
			driver.executeScript("arguments[0].scrollIntoView(true);", editButton.getWebElement());
			clickLinkWithJavascript(getEditWidgetBtn(widgetTitle));
			clickLink(getWidgetEditRemove);
		} catch (AssertionError ae)  {
			log.info("INFO: " + widgetTitle + " widget is not present.");
		}
	}

	public void deleteWidgetTileFromPanel(String widgetTitle) {
		clickLinkWithJavascript(customizeButton);
		clickLinkWithJavascript(getWidgetTile("Hidden Widgets"));
		String hiddenWidget = getAppFromHiddenApps(widgetTitle);
		scrollIntoViewElement(hiddenWidget);
		driver.getSingleElement(getAppFromHiddenApps(widgetTitle)).hover();
		clickLink(widgetTileDelete);
		clickLink(modalYesButton);
		clickLinkWait(customizeButton);
	}

	public void selectCheckbox(String selector){
		Element checkbox = getFirstVisibleElement(selector);
		if(!checkbox.isSelected()){
			clickLink(selector);
		}
	}

	public void unselectCheckbox(String selector){
		Element checkbox = getFirstVisibleElement(selector);
		if(checkbox.isSelected()){
			clickLink(selector);
		}
	}

	public void deletePageMgmtTemplate(String pageId) {
		String selector = "css=span.fa-times[data-name='" + pageId + "']";
		WebElement deleteIcon = (WebElement) this.getFirstVisibleElement(selector).getBackingObject();
		driver.executeScript("arguments[0].scrollIntoView()", deleteIcon);
		clickLink(selector);
		clickLink(modalYesButton);
	}

	public void clearAndTypeText(String selector, String text) {
		clearText(selector);
		typeText(selector, text);
	}

	public void editWidget(String widgetTitle) {
		editWidget(true, widgetTitle);
	}
	
	public String editWidget(Boolean isWidget, String widgetTitle) {
		fluentWaitElementVisible(getWidget(widgetTitle));
		WebElement widgetElement = (WebElement) this.getFirstVisibleElement(getWidget(widgetTitle)).getBackingObject();
		String title=widgetElement.getAttribute("aria-label");
		driver.executeScript("arguments[0].scrollIntoView()", widgetElement);
		driver.executeScript("scroll(0,-50);");
		//driver.getSingleElement(getWidget(widgetTitle)).hover();
		clickLinkWithJavascript(getEditWidgetBtn(widgetTitle));
		clickLinkWithJavascript(getWidgetEditConfigure);
		if (isWidget)
			fluentWaitPresent("css=div[class*='xccWidget'][class*='isEditing'], div[class='live-editor_title']");
		return title;
	}
	
	public String getWidgetTitle(String widgetTitle) {
		WebElement widgetElement = (WebElement) this.getFirstVisibleElement(getWidget(widgetTitle)).getBackingObject();
		String title=widgetElement.getAttribute("aria-label");
		return title;
	}

	public void saveWidget(String widgetTitle) {
		clickLink(getWidgetEditSave(widgetTitle));
		WebElement widgetElement = (WebElement) this.getFirstVisibleElement(getWidget(widgetTitle)).getBackingObject();
		driver.executeScript("arguments[0].scrollIntoView()", widgetElement);
		driver.executeScript("scroll(0,-50);");
	}
	
	public void cancelWidget(String widgetTitle) {
		clickLink(getWidgetEditCancel(widgetTitle));
	}

	public void loginAndLoadCurrentUrlWithHttps(User user) {
		login(user);
		String url = driver.getCurrentUrl();
		loadUrlWithHttps(url);
	}

	public void loadUrlWithHttps(String url) {
		url = replaceHttpWithHttps(url);
		driver.navigate().to(url);
	}

	public String replaceHttpWithHttps(String url) {
		if(url.startsWith("http:")){
			url = url.replace("http", "https");
		}
		return url;
	}

	public void addHighlightsToCommunity() {
		clickLink(communityActionsLink);
		clickLink(addAppsLink);
		isHighlightsInAppPalette();
		clickLink(addHighlightsAppLink);
		clickLink(closeAppsPalette);
	}

	public boolean isHighlightsInAppPalette() {
		for(int i = 0; i < 5; i++){
			if (!driver.isElementPresent(addHighlightsAppLink)
					&& driver.getSingleElement("css=div.app_palette_modal li[data-dojo-attach-point='nextNode'] a").isDisplayed()) {
				clickLink("css=div.app_palette_modal li[data-dojo-attach-point='nextNode'] a");
				if(driver.isElementPresent(addHighlightsAppLink)) {
					return true;
				}
			} else if(driver.isElementPresent(addHighlightsAppLink)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isIcecLight() {
		loadComponent(Data.getData().ComponentCommunities);
		try {
			loginAndLoadCurrentUrlWithHttps(cfg.getUserAllocator().getUser());
		}
		catch (NullPointerException n) {
			log.info("INFO: NullPointerException caught as the login() method was invoked from BeforeClass, continuing");
		}
		waitForJQueryToLoad(driver);
		return (Boolean) driver.executeScript("return lconn.core.config.properties['icec.light'].toLowerCase() === 'true'");
	}
	
	private Community createCommunity(User testUser, String serverURL, String communityName, String description, BaseCommunity community, BaseWidget[] widgetsToAdd){
		APICommunitiesHandler apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());

		//Create community
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);

		//Add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		if (widgetsToAdd != null) {
			addWidgets(apiOwner, community, comAPI, widgetsToAdd);
		}

		return comAPI;
	}
	
	public void addWidgets(APICommunitiesHandler apiOwner, BaseCommunity community, Community comAPI, BaseWidget[] widgetsToAdd) {
		for (BaseWidget widget : widgetsToAdd) {
			log.info("INFO: Adding the " + widget.getTitle() +
					" widget to community " + community.getName() + " using API");
			community.addWidgetAPI(comAPI, apiOwner, widget);
		}
	}
	
	public Community createLoginAndNavigateToCommunity(User testUser, String serverURL, String communityName, String description) {
		return createLoginAndNavigateToCommunity(testUser, serverURL, communityName, description, null, null);
	}

	public Community createLoginAndNavigateToCommunity(User testUser, String serverURL, String communityName, String description, BaseCommunity community, BaseWidget[] widgetsToAdd) {
		if(community == null) {
			community = new BaseCommunity.Builder(communityName)
					   .access(defaultAccess)
					   .tags("commTag")
					   .addMember(new Member(CommunityRole.MEMBERS, testUser))
					   .description(description).build();
		}
		Community comAPI = createCommunity(testUser, serverURL, communityName, description, community, widgetsToAdd);
		
		//GUI
		//Login
		loadComponent(Data.getData().ComponentCommunities);
		loginAndLoadCurrentUrlWithHttps(testUser);

		//Navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(CommunitiesUI.getGui(cfg.getProductName(), driver));

		return comAPI;
	}
	
	public BaseDogear[] createBookmarks(User testUser, String serverURL,BaseCommunity community, int noOfBookmarks) {
		BaseDogear[] bookmarks = new BaseDogear[noOfBookmarks];
		for(int i = 0; i < noOfBookmarks; i++) {
			boolean isImportant = (i % 2 == 0) ? true : false;
			String url = "http://www.ibm" + Helper.genDateBasedRand() + ".com";
			String name = "BOOKMARK" + Helper.genDateBasedRand();
			String tag = "bmTag" + Helper.genDateBasedRand();
			String description = "Sample bookmark description " + Helper.genDateBasedRand();
			BaseDogear bookmark = new BaseDogear.Builder(name, url)
					.community(community)
					.tags(tag)
					.description(description)
					.isImportant(isImportant)
					.build();

			//Create bookmark
			log.info("INFO: Create bookmarks using API");
			bookmark.createAPI(new APICommunitiesHandler(serverURL, testUser.getUid(), testUser.getPassword()));
			bookmarks[i] = bookmark;
		}
		return bookmarks;
	}
	
	public BaseForumTopic[] createForumTopics(User testUser, String serverURL, BaseCommunity community, int noOfTopics) {
		return createForumTopics(testUser, serverURL, community, noOfTopics, null);
	}

	public BaseForumTopic[] createForumTopics(User testUser, String serverURL, BaseCommunity community, int noOfTopics, String[] tags) {
		APIForumsHandler forumsHandler = new APIForumsHandler(serverURL, testUser.getEmail(), testUser.getPassword());
		String commUUID = community.getCommunityUUID().replaceAll("communityUuid=", "");

		Forum forum = forumsHandler.getDefaultCommForum(commUUID, community.getName());

		BaseForumTopic[] topics = new BaseForumTopic[noOfTopics];
		for(int i = 0; i < noOfTopics; i++) {
			String tag;
			if(tags != null && tags.length != 0 && tags.length >= noOfTopics) {
				tag = tags[i];
			} else {
				tag = Data.getData().ForumTopicTag;
			}
			BaseForumTopic forumTopic = new BaseForumTopic.Builder("Forum Post" + Helper.genDateBasedRandVal())
						.parentForum(forum)
					    .tags(tag)
					    .description("Sample forum description " + Helper.genDateBasedRand())
					    .partOfCommunity(community)
					    .build();

			//Create bookmark
			log.info("INFO: Create bookmarks using API");
			ForumTopic topicObj = forumTopic.createAPI(forumsHandler);
			forumTopic.setUUID(topicObj.getId().toString().replaceAll("urn:lsid:ibm.com:forum:",""));
			topics[i] = forumTopic;
			// Sleep to add delay before next api call to avoid creation in wrong order
			sleep(1000);
		}
		return topics;
	}
	
	public void createEvent(String eventName, String startTime, String endTime, CalendarUI calUI) throws Exception {
		//Create an event base state object
		BaseEvent event = new BaseEvent.Builder(eventName)
				.tags(Data.getData().commonTag)
				.description(Data.getData().commonDescription)
				.startTime(startTime)
				.endTime(endTime)
				.build();

		event.create(calUI);
	}
	
	public BaseBlogPost[] createIdeas(User testUser, String serverURL, Community comAPI, BaseCommunity community, int noOfIdeas) {
		BaseBlogPost[] ideaEntries = new BaseBlogPost[noOfIdeas];
		APICommunitiesHandler apiCommunityOwner = new APICommunitiesHandler(serverURL,
				testUser.getUid(), testUser.getPassword());
		community.addWidgetAPI(comAPI, apiCommunityOwner,
				BaseWidget.IDEATION_BLOG);
		for(int i = 0; i < noOfIdeas; i++) {
			BaseBlogPost blogEntry = new BaseBlogPost.Builder("Idea" + Helper.genDateBasedRand())
					.tags(Data.getData().commonAddress + Helper.genDateBasedRand())
					.content("Test description for Idea " + Helper.genDateBasedRand()).build();
			log.info("Creating: " + blogEntry.getTitle());
			apiCommunityOwner.createIdea(blogEntry,
					comAPI);

			ideaEntries[i] = blogEntry;
		}
		return ideaEntries;
	}
	
	public BaseBlogPost[] createBlogPosts(User testUser, String serverURL, Community comAPI, BaseCommunity community, int noOfPosts) {
		BaseBlogPost[] blogPosts = new BaseBlogPost[noOfPosts];
		APICommunitiesHandler apiCommunityOwner = new APICommunitiesHandler(serverURL,
				testUser.getUid(), testUser.getPassword());
		if (apiCommunityOwner.getWidgetID(comAPI.getUuid(), "Blog").isEmpty()) {
        	
			community.addWidgetAPI(comAPI, apiCommunityOwner,
					BaseWidget.BLOG);

		}
		for(int i = 0; i < noOfPosts; i++) {
			BaseBlogPost blogEntry = new BaseBlogPost.Builder("BlogPost" + Helper.genDateBasedRand())
					.tags(Data.getData().commonAddress + Helper.genDateBasedRand())
					.content("Test description for BlogPost " + Helper.genDateBasedRand()).build();
			log.info("Creating: " + blogEntry.getTitle());
			apiCommunityOwner.createBlogEntry(blogEntry,
					comAPI);

			blogPosts[i] = blogEntry;
			// Sleep to add delay before next api call to avoid creation in wrong order
			sleep(1000);
		}
		return blogPosts;
	}
	
	public void checkElementsArePresent(String[] selectors) {
		for (String selector : selectors) {
			log.info("INFO: Check " + selector + " is present");
			Assert.assertTrue(fluentWaitPresent(selector),
					selector + " not found");
		}
	}
	
	public void selectEventLayoutInEditView(String optionValue) {
		clickLink("css=select[name='event2']");
		clickLink("css=option[value='"+ optionValue +"']");
		driver.typeNative(Keys.ENTER);
	}
	
	public void deleteMyLinks(String serverURL, Map<String, String> links) {
		loadUrlWithHttps(serverURL + "/profiles/html/myProfileView.do");
		for (int i = 0; i < links.size(); i++) {
			driver.getFirstElement(ProfilesUIConstants.DeleteLink).click();
			fluentWaitTextPresent("The link has been removed");
		}
	}
	
	public void expandLiveEditorSectionIfPresent(String section) {
		if (isElementPresent("css=div[class='live-editor_heading-text']:contains("+ section +")")) {
			clickLink("css=div[class='live-editor_heading-text']:contains("+ section +")");
		} 
	}
	
	/**
	 * Method to set content creation as True for Rich Content widget in Highlights page
	 * 
	 * @param text is rich content widget title value
	 */
	public void editRichContentWidgetSetting(String locator)
	{
		
		fluentWaitElementVisible(editRichContentWidget.replace("PLACEHOLDER", locator));
		clickLinkWithJavascript(editRichContentWidget.replace("PLACEHOLDER", locator));
		clickLinkWithJavascript(editButtonRichContentWidget);
		fluentWaitElementVisible(socialAndContentSettingText);
		clickLinkWait(socialAndContentSettingText);
		clickLinkWithJavascript(allowContentCreationCheckbox);
		this.getFirstVisibleElement(saveSetting).click();
	}
	
	/**
	 * Method to add Rich Content widget in Highlights page
	 * 
	 * @param text is rich content widget title value
	 */
	public void createHighlightsWidget(String widgetType, String widgetTitle, String defaultWidget) {
		driver.executeScript("scroll(0,-250);");
		log.info("INFO: Create " + widgetType + " widget");
		clickLinkWithJavascript(IcecUI.customizeButton);
		clickLinkWait(IcecUI.createWidgetLink);
		selectType(widgetType);
		fluentWaitTextNotPresent("has been created.");
		waitForPageLoaded(driver);
		editWidget(defaultWidget);
		log.info("INFO: Enter ID");
		clearAndTypeText(IcecUI.idTextField, widgetTitle);
		clickLink(IcecUI.widgetDialogCreateButton);
	}
	
	/**
	 * Navigate to Create community and verify Tiny Editor functionality 
	 * @param Base Highlights object
	 * @return String Text present in Description of Tiny Editor.
	 */
	public String verifyTinyEditor(BaseHighlights highlights) {
		TinyEditorUI tui = new TinyEditorUI(driver);
		driver.executeScript("scroll(0,-250);");
		this.fluentWaitElementVisible(editRichContentWidget.replace("PLACEHOLDER", "Rich Content"));
		this.clickLink(editRichContentWidget.replace("PLACEHOLDER", "Rich Content"));
		
		fluentWaitElementVisible(editRichContentLink);
		clickLinkWithJavascript(editRichContentLink);
		tui.clickOnMoreLink();

		log.info("INFO: Entering a description and validating the functionality of Tiny Editor");
		if (highlights.getDescription() != null) {

			String TE_Functionality[] = highlights.getTinyEditorFunctionalitytoRun().split(",");
			
			for (String functionality : TE_Functionality) {
				switch (functionality) {
				case "verifyParaInTinyEditor":
					log.info("INFO: Validate Paragragh and header functionality of Tiny Editor");
					tui.verifyParaInTinyEditor(highlights.getDescription());
					break;
				case "verifyAttributesInTinyEditor":
					log.info("INFO: Validate Attributes functionality of Tiny Editor");
					tui.verifyAttributesInTinyEditor(highlights.getDescription());
					break;
				case "verifyPermanentPenInTinyEditor":
					log.info("INFO: Validate Permanent Pen functionality of Tiny Editor");
					tui.verifyPermanentPenInTinyEditor(highlights.getDescription());
					break;
				case "verifyUndoRedoInTinyEditor":
					log.info("INFO: Validate Undo and Redo functionality of Tiny Editor");
					tui.verifyUndoRedoInTinyEditor(highlights.getDescription());
					break;
				case "verifyAlignmentInTinyEditor":
					log.info("INFO: Validate Alignment functionality of Tiny Editor");
					tui.verifyAlignmentInTinyEditor(highlights.getDescription());
					break;
				case "verifyIndentsInTinyEditor":
					log.info("INFO: Validate Indents functionality of Tiny Editor");
					tui.verifyIndentsInTinyEditor(highlights.getDescription());
					break;
				case "verifyBulletsAndNumbersInTinyEditor":
					log.info("INFO: Validate Bullets and Numbers functionality of Tiny Editor");
					tui.verifyBulletsAndNumbersInTinyEditor(highlights.getDescription());
					break;
				case "verifyHorizontalLineInTinyEditor":
					log.info("INFO: Validate Horizontal Line functionality of Tiny Editor");
					tui.verifyHorizontalLineInTinyEditor(highlights.getDescription());
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
					tui.verifySpellCheckInTinyEditor(highlights.getDescription());
					break;
				case "verifyRowsCoulmnOfTableInTinyEditor":
					log.info("INFO: Validate Rows and Columns of Table in Tiny Editor");
					tui.verifyRowsCoulmnOfTableInTinyEditor(highlights.getDescription());
					break;
				case "verifyFormatPainterInTinyEditor":
					log.info("INFO: Validate Format Painter in Tiny Editor");
					tui.verifyFormatPainterInTinyEditor(highlights.getDescription());
					break;
				case "verifyFontInTinyEditor":
					log.info("INFO: Validate font functionality of Tiny Editor");
					tui.verifyFontInTinyEditor(highlights.getDescription());
					break;
				case "verifyFontSizeInTinyEditor":
					log.info("INFO: Validate font Size functionality of Tiny Editor");
					tui.verifyFontSizeInTinyEditor(highlights.getDescription());
					break;
				case "verifyLinkImageInTinyEditor":
					log.info("INFO: Validate Link Image functionality of Tiny Editor");
					tui.verifyLinkImageInTinyEditor(highlights.getDescription());
					break;
				case "verifyRightLeftParagraphInTinyEditor":
					log.info("INFO: Validate Left to Right paragraph functionality of Tiny Editor");
					tui.verifyRightLeftParagraphInTinyEditor(highlights.getDescription());
					break;
				case "verifyOtherTextAttributesAndFullScreenInTinyEditor":
					log.info("INFO: Validate other text attributes functionality of Tiny Editor");
					tui.verifyOtherTextAttributesAndFullScreenInTinyEditor(highlights.getDescription());
					break;
				case "verifyFindReplaceInTinyEditor":
					log.info("INFO: Validate Find and Replace functionality of Tiny Editor");
					tui.verifyFindReplaceInTinyEditor(highlights.getDescription());
					break;
				case "verifyInsertLinkImageInTinyEditor":
					log.info("INFO: Validate Link Image functionality of Tiny Editor");
					tui.verifyInsertLinkImageInTinyEditor(highlights.getDescription());
					break;
				case "verifyTextColorInTinyEditor":
					log.info("INFO: Validate Font Text Color functionality of Tiny Editor");
					tui.verifyTextColorInTinyEditor(highlights.getDescription());
					break;
				case "verifyBackGroundColorInTinyEditor":
					log.info("INFO: Validate Font BackGround Color functionality of Tiny Editor");
					tui.verifyBackGroundColorInTinyEditor(highlights.getDescription());
					break;
				case "verifyWordCountInTinyEditor":
					log.info("INFO: Validate Word Count functionality of Tiny Editor");
					tui.verifyWordCountInTinyEditor(highlights.getDescription());
					break;
				case "verifyUploadImageFromDiskInTinyEditor":
					log.info("INFO: Validate Upload image from Disk functionality of Tiny Editor");
					tui.verifyUploadImageFromDiskInTinyEditor();
					break;
				case "verifyBlockQuoteInTinyEditor":
					log.info("INFO: Validate Block quote functionality of Tiny Editor");
					tui.verifyBlockQuoteInTinyEditor(highlights.getDescription());
					break;
				case "verifyInsertMediaInTinyEditor":
					log.info("INFO: Validate Insert Media functionality of Tiny Editor");
					tui.verifyInsertMediaInTinyEditor(highlights.getDescription());
					break;
				case "verifyLinkToConnectionsFilesInTinyEditor":
					log.info("INFO: Validate Link to connections files from files in Tiny Editor");
					tui.addLinkToConnectionsFilesInTinyEditor(highlights.getDescription());
					break;
				case "verifyCodeSampleIntinyEditor":
					log.info("INFO: Validate Code Sample functionality of Tiny Editor");
					tui.verifyCodeSampleIntinyEditor(highlights.getDescription());
					break;
				case "verifyInsertiFrameInTinyEditor":
					log.info("INFO: Validate Insert iFrame functionality of Tiny Editor");
					tui.verifyInsertiFrameInTinyEditor(highlights.getDescription());
					break;
				case "verifyEditDescriptionInTinyEditor":
					log.info("INFO: Validate Insert iFrame functionality of Tiny Editor");
					tui.verifyDefaultCaseInTinyEditor(highlights.getDescription());
					break;	
				}
			}
		}

		String TEText = tui.getTextFromTinyEditor();
		log.info("INFO: Get the text from Tiny Editor body" + TEText);
		
		// Save the community
		log.info("INFO: Saving the highlights " + highlights.getName());
		this.driver.getSingleElement(CommunitiesUIConstants.rteSave).click();
		
		return TEText;
	}
	
	public String getHighlightsRichContentText(String locator) {
		return this.getFirstVisibleElement(richContentWidgetText.replace("PLACEHOLDER", locator)).getText();
	}
	
	public void verifyInsertedLink(BaseHighlights highlights,String communityName)
    {
        TinyEditorUI tui = new TinyEditorUI(driver);
        tui.verifyInsertedLinkinHighlightsDescription(highlights,communityName);
    }


	public String editDescriptionInTinyEditor(BaseHighlights highlights, String ediDesc,String communityName) 
	{
		TinyEditorUI tui = new TinyEditorUI(driver);
		String editedDesc;
		
		this.fluentWaitElementVisible(editRichContentWidget.replace("PLACEHOLDER", communityName));
		this.clickLink(editRichContentWidget.replace("PLACEHOLDER", communityName));
		
		fluentWaitElementVisible(editRichContentLink);
		clickLinkWait(editRichContentLink);
		tui.clickOnMoreLink();
		tui.verifyDefaultCaseInTinyEditor(ediDesc);
		editedDesc = tui.getTextFromTinyEditor();
		log.info("INFO: Saving the highlights rich content widget" + highlights.getName());
		this.driver.getSingleElement(saveTinyEditor).click();
		return editedDesc;
	}
	}
