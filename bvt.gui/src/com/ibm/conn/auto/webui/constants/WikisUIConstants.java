package com.ibm.conn.auto.webui.constants;

public final class WikisUIConstants {


	/** verify wiki page objects */
	public static String About_Tab = "css=a[id='about_link']";
	public static String Versions_Tab = "css=a[id='versionHistory_link']";
	public static String Wiki_Actions_Button = "css=a[aria-label='Wiki Actions']";
	public static String Follow_Button = "css=a[id^='lconn_core_FollowMenu_'], li[class='lotusFirst lotusNowrap'] a[id^='lconn_core_FollowMenu']";
	public static String Feed_For_This_Page_Link = "css=div[class*='lotusFeeds'] > a[class*='lotusFeed'][aria-label='Follow changes to this page through your feed reader']";
	/** User views */
	public static String SelectAPageInTheNav = "css=span.dijitTreeContent a";
	public static String LeftNavLink = "css=div span a";
	public static String Members_Link = "css=a#lconn_wikis_scenes_SiteTools_members.lotusLink";
	public static String Public_Wikis_Filter = "link=Public Wikis";
	public static String MyWikis_Editor_Filter = "link=I'm an Editor";
	public static String MyWikis_Owner_Filter = "link=I'm an Owner";
	public static String MyWikis_Reader_Filter = "link=I'm a Reader";
	public static String MyWikis_Follow_Filter = "link=I'm Following";
	public static String MyWikis_Following_Filter_Text = "I'm following";
	public static String MyWikis_Owner_Filter_Text = "I'm an Owner";
	public static String MyWikis_Editor_Filter_Text = "I'm an Editor";
	public static String MyWikis_Reader_Filter_Text = "I'm a Reader";
	public static String Public_Wikis_List_Header = "Public Wikis";
	public static String WikisLink = "css=a:contains(Wikis)";
	/** Creating/Editing pages */
	public static String Edit_Button = "xpath=//div//span//a[@title='Click to edit this page']";
	public static String Page_Actions_Button = "css=a[aria-label='Page Actions']";
	public static String Menu_Create_Peer = "xpath=//td[contains(text(), 'Create Peer')]";
	public static String Menu_Create_Child = "xpath=//td[contains(text(), 'Create Child')]";
	public static String Menu_Delete_Page = "xpath=//td[contains(text(), 'Move to Trash')]";
	public static String Menu_Edit_Wiki = "xpath=//td[contains(text(), 'Edit Wiki')]";
	public static String Menu_Edit_Wiki1 = "css=tbody[class='dijitReset dijitMenuItemLabel'] tr:nth-child(1)";
	public static String Menu_Move_To_Trash = "xpath=//td[contains(text(), 'Move to Trash')]";
	public static String Menu_PrintPage = "xpath=//td[contains(text(), 'Print Page')]";
	public static String Menu_MovePage = "xpath=//td[contains(text(), 'Move Page')]";
	public static String Menu_DownloadPage = "xpath=//td[contains(text(), 'Download Page')]";
	public static String Edit_Wiki_Name_Textfield = "css=[id='editWiki_title']";
	public static String New_Page_Title_Textfield = "css=input[id='name']";
	public static String Save_and_Close_Link = "css=input[value='Save and Close']";
	public static String Page_Name_Textfield_In_Editor = "css=*[id='title']";
	public static String WikisLinkInHeader = "css=h2.lotusHeading a";
	public static String AllUsers_RadioButton = "css=input[id$='_publicViewingAllowed']";
	public static String WikiMembersOnly_RadioButton = "css=input[id$='_publicViewingDisabled']";
	public static String AllLoggedInUsers_RadioButton = "css=input[id$='_authenticatedEditingAllowed')]";
	public static String WikiEditorsAndOwnersOnly_RadioButton = "css=input[id$='_authenticatedEditingDisabled']";
	public static String Verify_Wiki_Title_After_Edit = "css=#lotusPlaceBar > div.lotusRightCorner > div.lotusInner > h2 > a";
	public static String MembersRole = "css=#qkrMemRoleSel";
	/** Comments */
	public static String Comments_Tab = "css=a[id='comments_link']";
	public static String Add_Comment_Link = "link=Add a comment";
	public static String Add_Comment_Editor = "css=[id='addCommentBody']";
	public static String Add_Comment_Text = "css=div[id^='mentionstextAreaNode_']";
	public static String Edit_Comment_Editor = "css=form[class='lotusForm qkrAddComment'] textarea[id^='commentBody_']";
	public static String Comment_Added_Message = "The comment was added.";
	/** Tagging */
	public static String Add_tags_Link = "css=div span a.qkrAdd";
	public static String Add_or_RemoveTags_Link = "css=a:contains('Add or remove tags')";
	public static String Add_or_RemoveTags_Text = "Add or remove tags";
	public static String OK_Button = "css=input[value='OK']";
	public static String Dialog_OK_Button = "css=input.lotusFormButton[value='OK']";
	public static String TagEditorTextFieldInput = "css=input[id$='_selectTag']";
	public static String TagSearchBtn = "css=div[id='lconnTagCloudFilter']  input[alt='Search']";
	public static String tagCloudWidget = "css=div#lconnTagCloudFilter";
	public static String tagCloudListView = "css=div#lconnTagWidget_tagListView";
	public static String tagCloudCloudView = "css=div#lconnTagWidget_tagCloudView";
	public static String TagListinListView = "css=ul#lconnTagWidget_tagList li span";
	public static String tagTextBox = "css=input#lconnTagWidgetcommonTagsTypeAhead";
	public static String tagTypeahead = "css=div#lconnTagWidgetcommonTagsTypeAhead_popup";
	public static String tagLinksinTagTypeahead = "css=div[id^=lconnTagWidgetcommonTagsTypeAhead_popup]";
	public static String tagLinkinTagTypeahead = "css=div#lconnTagWidgetcommonTagsTypeAhead_popup0";
	/** Like/Unlike */
	public static String likeLink = "xpath=//a[@aria-label='Like this']";
	public static String unlikeLink = "css=a[aria-label='Unlike']";
	public static String LikeUnLikeLink = "link=";
	public static String LikeDescription = "css=div#lconn_wikis_widget_WikiLike_0.lotusLike span span.lotusLikeDescription";
	public static String LikeCount = "css=a.lotusLikeCount div.lotusLikeText";
	public static String LikeImage = "css=img.lotusIconLike";
	public static String ListOfUsersWhoLikeAPage = "xpath=//div[2]/a";
	public static String CloseListOfUsersWhoLikeAPage = "css=a[aria-label='Close']";
	/** Attachments */
	public static String Attachments_Tab = "css=a[id='attachments_link']";
	public static String AddAttachmentLink = "css=a.lotusAction:contains('Add an attachment')";
	public static String AttachmentOKButton = "css=input[value='OK'][class='lotusFormButton cnxPrimaryBtn'][type='submit']";
	/** CK Editor */
	public static String CKEditor_iFrame_Wikis = "css=iframe[title='Rich text editor, editor1, press ALT 0 for help.']";
	public static String OK_Button_In_Popup = "css=div[class='lotusDialogFooter'] [value='OK']";
	/** Delete wiki/page */
	public static String Index_Link = "link=Index";
	public static String Trash_Link = "link=Trash";
	public static String Menu_Delete_Wiki = "css=td:contains('Delete Wiki')";
	public static String Permanently_Delete_This_Wiki_Signature = "css=input[id='lconn_wikis_action_DeleteWiki_signature']";
	public static String Permanently_Delete_This_Wiki = "css=input[id='lconn_wikis_action_DeleteWiki_confirm']";
	public static String Delete_Button = "css=input[value='Delete']";
	public static String New_Page_Link_in_Nav = "//div[@id='sideNav']/div/div[2]/a";
	public static String Add_Tag_Link_During_Page_Create = "css=div span a.qkrAdd";
	public static String Add_Tag_Textfield_During_Page_Create = "css=div span#pageTags.lotusTags form input#pageTags_selectTag.lotusText";
	public static String Edit_Page_Title_Textfield = "css=tbody tr td input[name='Edit title']";
	public static String Place_Bar_Title = "css=[id='lotusPlaceBar'] *[class='lotusInner'] h2:nth-child(1)";
	public static String CommentCountinTab = "css=a#comments_link";
	public static String UsernameInCommentSection = "css=div.lotusPost div.lotusPostContent div.lotusMeta a.lotusPerson";
	public static String CreateThisPageBtn = "css=input[value='Create This Page']";
	public static String NoThanksBtn = "css=input[value='Get Me Out of Here']";
	public static String emptyTrashButton = "css=button[aria-describedby^='describedBy_lconn_wikis_action_PurgeAllPages']";
	public static String restoreButton = "css=button[aria-describedby^='describedBy_lconn_wikis_action_RestorePages']";
	/** Members */
	public static String Remove_Members_Button = "link=Remove Members";
	public static String Add_Members_Button = "link=Add Members";
	public static String Manage_Access_Button = "link=Manage Access";
	public static String Reader_Role_Option_onEditMemberLightBox = "css=input[id$='_role_reader'][class='lotusCheckbox']";
	public static String Editor_Role_Option_onEditMemberLightBox = "css=input[id$='_role_editor'][class='lotusCheckbox']";//_role_editor
	public static String Owner_Role_Option_onEditMemberLightBox = "css=input[id$='_role_manager'][class='lotusCheckbox']";
	public static String ManageAccess ="css=a:contains(Manage Access)";
	public static String Member_List_Table = "css=table.lconnWikisMembersList";
	/** Following */
	public static String Following_Actions_Button = "css=a:contains('Following Actions')";
	public static String Stop_Following_this_wiki = "css=tbody[class='dijitReset'] tr:contains(Stop Following this Wiki)";
	public static String Start_Following_this_wiki = "css=tbody[class='dijitReset'] tr:contains(Follow this Wiki)";
	public static String Follow_Wiki_Message = "You are following this wiki.";
	public static String Follow_This_Page = "css=tbody[class='dijitReset'] > tr:contains('Follow this Page')";
	public static String UndoRecommendation = "css=a:contains('Unlike')";
	public static String Rich_Text_Tab = "css=ul#qkrEditorTabs a:contains(Rich Text)";
	public static String HTML_Source_Tab = "css=ul#qkrEditorTabs a:contains(HTML Source)";
	public static String HTMLTextArea = "css=#htmlSourceEditor";
	public static String Preview_Tab = "css=ul#qkrEditorTabs a:contains(Preview)";
	public static String NoTags = "css=span[id^='lconn_share0_widget_Tagger_'] [class='qkrEmpty']";
	public static String All_Breadcrumb_Text = "css=div[id^='lconn_share0_widget_Breadcrumbs_']";
	public static String Recommendations_Info_Alternate = "css=a#TOGGLE_lconn_wikis_widget_WikiLike_0.lotusLikeAction";
	public static String Recommendations_Info = "css=a#TOGGLE_lconn_wikis_widget_WikiLike_0.lotusLikeAction";
	public static String Menu_Item_1 = "css=tbody[class='dijitReset'] tr:nth-child(1)";
	/** Create a wiki */
	public static String Start_New_Wiki_Button = "css=span[class='lotusBtn lotusBtnAction lotusLeft cnxPrimaryBtn'] > a:contains('Start a Wiki')";
	public static String Wiki_form_title = "css=input[id='createWiki_title']";
	public static String Wiki_form_tag = "css=tbody tr td input#lconn_share0_widget_TagTypeAhead_0.lotusText";
	public static String Wiki_form_desc = "css=tbody tr td textarea#createWiki_description";
	public static String Wiki_description = "css=td textarea#createWiki_description";
	public static String Wiki_Member_Dropdown = "css=tbody tr td select#qkrMemRoleSel.wikiRoles";
	public static String MembershipRolesUsersDropdown = "css=tbody tr td input[aria-label='Add Users']";
	public static String selectedUserIdentifierWikis = "css=*[id$='_selectUser_popup'] li:nth-child(2)";
	public static String fullUserSearchIdentifierWikis = "css=*[id$='_selectUser_popup'] *[dojoattachpoint='searchButton']";
	public static String LikeMessage = "css=span[class='lotusLikeDescription']:contains(You like this)";
	public static String CreateWikiLink = "link=Create a Wiki Page";
	public static String wikiPageAddTagLink = "css=a[class='qkrAdd']";
	public static String wikiPageAddTagText = "css=input[title='Add tags']";
	/** Mega Menu options */
	public static String wikisOption = "css=a>strong:contains(Wikis)";
	public static String wikisImAnOwner = "css=tr:contains(Wikis)>td>a:contains(I'm an Owner)";
	public static String wikisPublicWikis = "css=td>a:contains(Public Wikis)";
	/** Version */
	public static String ShowComparisonLink = "link=Show comparison";
	public static String ViewLink = "link=View";
	public static String DeleteLink = "link=Delete";
	public static String RestoreLink = "link=Restore";
	/** Index page	 */
	public static String IndexLink = "link=Index";
	public static String updateLink = "css=a#link_qkrSortupdated";
	public static String PageViewLink = "css=a#link_qkrSortmostpopular";
	public static String SizeLink = "css=a#link_qkrSortsize";
	public static String list = "css=div#list ";
	public static String firstWikiEntry = "css=div#list tr[class='hentry lotusFirst'] h4";
	public static String firstWikiEntryUpdateCell = "css=div#list tr[class='hentry lotusFirst'] td[class=updated] span";
	public static String firstWikiEntryPageCell = "css=div#list tr[class='hentry lotusFirst'] td[title$=views] span";
	public static String moreLink = "link=More";
	public static String GotoPageLink = "link=Go to Page...";
	public static String IEditedLink = "link=I Edited";
	public static String EditedByLink = "link=Edited by...";
	public static String PagesSection = "css=span[id^='span_filter_wiki.filter.allpages']";
	public static String PageSectionInput = "css=#pagenavsearchbox";
	public static String PageSectionSearchBtn = "css=#allPagesList input[name='submit']";
	public static String PageSectionTypeahead = "css=#pagenavsearchbox_dropdown";
	public static String PageSectionTypeaheadLink = "css=div[id^=pagenavsearchbox_popup]";
	public static String EditedByPersonInput = "css=#personalpagesearchbox";
	public static String EditedByPersonTypeahead = "css=#personalpagesearchbox_popup";
	public static String EditedByPersonTypeaheadLink = "css=#personalpagesearchbox_popup0";
	/** Move Page	 */
	public static String MovePageDig = "css=div[id^=lconn_share][role=dialog]";
	public static String PageLinksinSelBox = "css=div[id^=lconn_share0_widget_SelectableList_]";
	public static String MarkAsTopCheckbox = "css=input[id$=_maketoplevel_box]";
	public static String BreadCrumb = "css=div[id^=lconn_share0_widget_Breadcrumbs_]";
	public static String SelectedPageinSelBox = "css=div.liItemSelected";
	public static String PageNameTypeBox = "css=input[id$=mysearchbox]";
	public static String PageNameDropdown = "css=div[id$=mysearchbox_dropdown]";
	public static String PageNameDropdownLink = "css=div[id$=mysearchbox_popup0]";
	public static String SelectedPageNameinSortBox = "css=div[id^=lconn_share0_widget_SortableList_].liItemSelected";
	public static String MoveUpLink = "css=a[title='Move Up']";
	public static String MoveUpImg = "css=img[alt='Move Up']";
	public static String MoveDownImg = "css=img[alt='Move Down']";
	public static String Trash = "css=div h1:contains(Trash)";
	public static String shortenTag = "link=Shorten tag?";
	public static String memberWebElement = "css=table[class$='lconnWikisMembersList'] tbody tr td";
	public static String pageCreated = "css=span:contains(The page was created.)";
	public static String replaceInvalidCharacters = "css=tbody tr td a:contains(Replace invalid characters)";
	public static String saveAndClose = "css=input[title='Save and Close']";
	public static String welcomeTo = "css=a[title^='Welcome to ']";
	public static String wikiPageHeader = "css=h1[id='wikiPageHeader']";
	public static String pageTooLongLink = "css=td[class='lotusFormError']:contains(The page name is too long.)";
	public static String recommendationCount0 = "css=div[dojoattachpoint='inlineLikeCount']:contains('0')";
	public static String recommendationCount1 = "css=div[dojoattachpoint='inlineLikeCount']:contains('1')";
	public static String recommendationCount2 = "css=div[dojoattachpoint='inlineLikeCount']:contains('2')";
	public static String wikiDescriptionEdit = "css=td textarea#editWiki_description";
	public static String wikiTagEdit = "css=input[id='qkrTagTextbox']";
	public static String wikiTitleEdit = "css=input[id='editWiki_title']";
	public static String wikiEditSave = "css=input[value='Save']";
	public static String wikiManageAccess = "css=a[role='button']:contains(Manage Access)";
	public static String wikiEditAccessEditDisable = "css=fieldset[id='editAccessNode'] input[id^='lconn_wikis_widget_WikiAccessOptions_'][id$='_authenticatedEditingDisabled']";
	public static String WikisActionMenu = "css=a[id='wikiActionMenuLink']";
	public static String TwistyTopLevelCollapse = "css=img[class='dijitTreeExpando dijitTreeExpandoOpened']";
	public static String wikiNameInBreadcrumb = "css=div[class='lotusBreadcrumbs'] a";
	public static String childPageName = "css=h1[class='qkrWideInner bidiAware']";
	public static String StartWikiBtn = "xpath=//a[text()='Start a Wiki']";
	//Tiny Editor
	public static String wikisNewPageDesc = "css=div#wikiContentDiv";
	public static String wikisHeadlineLink = "xpath=//a[text()='Wikis' ]";
	
	public static String publicWikisearchResult = "//h1[contains(text(),'Search Results within Public Wikis')]";
	
	public static String wikiLinkOnnewUI = "//li[@widgetdefid='Wiki']";
	
	//Community Wikis
	public static String wikisLink = "table[class='lotusTable lotusFixedTable'] a[title='PLACEHOLDER']";
	public static String communityWikiHeader = "//div[@id='tabNavBar']//div[contains(text(),'PLACEHOLDER')]";
	public static String communityWikiActionsBtn = "//div[@class='lotusTitleBarExt tabNavActionsMenu']//a[contains(text(),'PLACEHOLDER')]";
	public static String communityWikiTags = "div[id='lotusColRight'] span[title='Tags']";
	public static String followingActionsDropDownnewUI = "//div[contains(text(),'Following Actions')]";
	public static String followThisPageLinknewUI = "//a[contains(text(),'Follow this Page')]";
	public static String followingThisPageMessagenewUI = "//span[contains(text(),'You are following this page.')]";
	public static String stopFollowThisWikiLinknewUI = "//a[contains(text(),'Stop Following this Wiki')]";
	public static String wikiActionsDropDownnewUI = "//div[contains(text(),'Wiki Actions')]";
	public static String editWikiLinknewUI = "//a[contains(text(),'Edit Wiki')]";
	public static String editWikiTitleTextnewUI = "//h2[contains(text(),'Edit Wiki')]";
	public static String deleteWikiLinknewUI = "//a[contains(text(),'Delete Wiki')]";
	public static String memberDropDownnewUI = "//span[@id='span_filter_members.summary']";
	
	//CNX8UI_Wikis
	public static String publicWikisTable = "table[aria-label='my wikis table']";
	public static String mostLiked = "//div[@id='lotusColRight']//h2[contains(text(),'Most Liked')]";
	public static String mostVisits = "//div[@id='lotusColRight']//h2[contains(text(),'Most Visits')]";
	public static String publicTags = "//div[@id='lotusColRight']//span[@title='Public tags']";
	public static String selected_ternaryNav = "div[id='tertiary_level_nav'] a[aria-label^='Selected']";
	public static String wikis_pagination = "div[class='perpage_dropdown']";
	public static String wikisName = "table[class^='lotusTable'] a[title='PLACEHOLDER']";
	public static String wikisFeed = "a[class='lotusFeed lotusAction']";
	public static String tag_Section = "span[title='Tags']";
	public static String view_Summary = "table[id='qkrViewControl'] a[aria-label='Summary']";
	public static String view_Details = "table[id='qkrViewControl'] a[aria-label='Details']";
	public static String wikisLinkNewUI = "//a[contains(text(),'Wikis')]";
	public static String wikisArrowLink = "//span[@id='top_nav_action']//img[@class='icon-chevron-left']";
	public static String indexLinkTopNav = "//a[@id='lconn_wikis_scenes_SiteTools_index']";
	public static String membersLinkTopNav = "//a[@id='lconn_wikis_scenes_SiteTools_members']";
	public static String trashLinkTopNav = "//a[@id='lconn_wikis_scenes_SiteTools_trash']";
	public static String exportPDFLink = "//span[@class='pdfexportBtn']";
	public static String maximizefullScreenToggleLink = "//*[local-name()='svg' and @data-mui-test='maximizeIcon']";
	public static String minimizefullScreenToggleLink = "//*[local-name()='svg' and @data-mui-test='minimizeIcon']";
	public static String selectedTab_TopNavBar = "div[id='tertiary_level_nav'] li[class='lotusSelected'] a";
	public static String newPageBtn = "div[id='new_page_btn'] a";
	public static String followingActionsLink = "//div[@id='actionMenuWidgetWrapper']//div[contains(text(),'Following Actions')]";
	public static String wikiActionsLink = "//div[@id='actionMenuWidgetWrapper']//div[contains(text(),'Wiki Actions')]";
	public static String wikiMembers = "div[id='filter_members.summary']";
	public static String wikiTags = "div[id='lconnTagCloudFilter']";
	public static String wikiDates = "div[id='filter_files.filter.date']";
	public static String wikiRole = "div[id='filter_members.filter.role']";
	public static String wikiKind = "div[id='filter_members.filter.type']";
	public static String wikiPages = "div[id='filter_wiki.filter.allpages.onpremise']";
	public static String sideNavWikiName = "//div[@id='sideNav']//a[contains(text(),'PLACEHOLDER')]";
	public static String wikiList = "//table[@summary='List of wiki pages']//a[contains(text(),'PLACEHOLDER')]";
	public static String backToWikiLink = "//div[@class='lotusBreadcrumbs']//a[contains(text(),'Back to PLACEHOLDER Wiki')]";
	public static String memberList = "//table[@class='lotusTable lotusClear lconnWikisMembersList']//a[contains(text(),'PLACEHOLDER')]";
	public static String actionBtn = "//div[@class='lotusBtnContainer lotusActionBar']//a[contains(text(),'PLACEHOLDER')]";
	public static String trashActionBtn = "//div[@class='lotusBtnContainer']//button[contains(text(),'PLACEHOLDER')]";

	private WikisUIConstants() {}

}
