package com.ibm.conn.auto.webui.constants;

public final class BaseUIConstants {

	/** Login scene */
	public static String USERNAME_FIELD = "//input[@id='username'] | //input[@name='USER'] | //input[@id='j_username'] | //input[@name='username'] | //input[@name='j_username']";
	public static String Password_FIELD = "//input[@id='password'] | //input[@name='PASSWORD'] | //input[@id='j_password'] | //input[@name='password'] | //input[@name='j_password']";
	public static String SBS_Create_IBM_ID = "css=a[id='ng-mobile-signUp']";
	public static String SBS_remember_Me = "css=input[id='rememberYes']";
	public static String Login_Button = "//input[@value='Login'] | //input[@value='Log In'] | //input[@value='Log in'] | //a[@id='submitLink'] | //a[@id='signinbutton'] | //input[@value='Sign In']";
	public static String Login_Link = "link=Log In";
	public static String Mobile_USERNAME_FIELD = "css=input[id='j_username']";
	public static String Mobile_Password_FIELD = "css=input[id='j_password']";
	public static String Mobile_Login_Button = "id=submitLink";
	
	//New Changes during CNX8UI
	public static String loginTitle = "//h1[contains(text(),'Log In')]";
	public static String emailHeading = "//label[contains(text(),'Email/user name')]";
	public static String passwordHeading = "//label[contains(text(),'Password')]";
	public static String togglePasswordIcon = "//i[@id='togglePassword']";
	public static String resetGuestPasswordLink = "//a[@id='forgotPasswordLink']";
	
	/** Page Footer links */
	public static String PageFooterAbout = "link=About";
	public static String menuOption = "css=tbody[class='dijitReset'] td";
	/** Logout */
	public static String Logout_Username = "css=a#headerUserName";
	public static String Logout_Link = "css=a:contains(Log Out)";
	/** CKEditor */
	public static String CKEditor_iFrame = "css=iframe";
	public static String CKEditor_div = "css=#cke_ckeditor";
	public static String StatusUpdate_iFrame = "css=iframe[class^='cke_wysiwyg_frame']";
	public static String StatusUpdate_iFrameNew = "css=div[title='What do you want to share?'] iframe[class^='cke_wysiwyg_frame']";
	public static String StatusUpdate_Body = "css=body[class^='cke_editable']";
	/** Activities */
	public static String AddButton = "css=div > span > a.feedCapVisible > img.lotusArrow.lotusDropDownSprite";
	/** Files selectors */
	public static String CommunityFilesSidebar = "link=Files";
	public static String CommunityShareFiles = "css=*[id^='lconn_files_comm_actions_sharefiles_']";
	public static String CommunityMyComputer = "css=input[id*='option_mycomputer']";
	public static String BrowseFilesOnMyComputer = "link=Browse Files on My Computer...";
	public static String UploadFiles_Name = "//input[@class='lotusText lotusLtr']";
	public static String fileToUpload = "css=input[type='file']";
	public static String Share_Files_Button = "css=input[class='lotusFormButton'][value='Share Files']";
	public static String Upload_Button = "//input[@value='Upload']";
	public static String CancelButton = "//input[@value='Cancel']";
	public static String SaveButton = "//input[@value='Save']";
	public static String CreateButton = "//input[@value='Create']";
	public static String SaveButton1 = "//input[@value='Save'][1]";
	public static String SaveButton2 = "//input[@value='Save'])[2]";
	public static String RecentUploadsCheckbox = "css=input[type='checkbox']";
	public static String BrowseComputerForFiles = "//a[contains(text(),'Browse files on my computer')]";
	public static String FileInputField = "css=input[type='file'][id*='_contents_contents']";
	public static String FileInputField2 = "css=input[type='file'][id*='_contents_contents']";
	public static String FileInputField3 = "css=input[type='file'][id*='_contents']";
	public static String Browse_Button = "css=#lconn_btn_browse_files";
	public static String Wiki_File_Input = "css=tbody tr td input#file";
	public static String Chrome_Browse_Button = "css=span.lconnUploadContainer > button.lotusBtn";
	public static String Chrome_FileInputField = "css=input[type='file']";
	public static String IE_FileInputField = "css=input[type='file']";
	public static String ShareBoxBrowse_Button = "//div[@id='lconn_core_upload_ui_FileField_0']/span/button";
	public static String OKButton = "css=input[value='OK'][class*='lotusFormButton'][type='submit']";

	//Added by Wenbin 2016 1031
	public static String DeleteButton = "css=input[value='Delete'][class='lotusFormButton'][type='submit']";
	/** Communities selectors */
	public static String Im_Owner = "css=table a:contains(I'm an Owner)";
	public static String PublicCommunitiesView = "css=div ul li a[href='/communities/service/html/allcommunities']";
	public static String Community_Actions_Button = "css=#displayActionsBtn, a[id='displayActionsBtn']";
	public static String Menu_Item_CreateSub = "css=#communityMenu_CREATESUB_text";
	public static String Menu_Item_Edit = "css=#communityMenu_EDIT_text, #communityMenu_EDITURL_text";
	public static String Menu_Item_Delete = "css=#communityMenu_DELETE_text";
	public static String Menu_Item_Leave = "css=#communityMenu_LEAVE_text";
	public static String Menu_Item_Customize = "css=communityMenu_CUSTOMIZE_text";
	public static String Menu_Item_Moderate = "css=#communityMenu_MODERATE_text";
	public static String Menu_Item_MoveComunity = "css=#communityMenu_MOVE_text";
	public static String CommunityMembersTypeAhead = "css=input[id='addComMembersWidgetPeopleTypeAhead'], input[id='bhc_lconn_core_PeopleTypeAhead']";
	public static String fullUserSearchIdentifier = "css=*[id='addComMembersWidgetPeopleTypeAhead_popup'] *[dojoattachpoint='searchButton']";
	public static String FilterByComponentName = "css=select[dojoattachpoint='filterHolderNode']";
	public static String SelectEntryFromAS = "css=div.lotusPostContent div.lotusPostAction";
	public static String SelectEntryFromAS1 = "css=div.lotusPostDetails.lotusChunk";
	/** Wiki selectors */
	public static String MembershipRolesUsersDropdown = "css=tbody tr td input[aria-label='Add Users']";
	/** Global Search Bar Component **/
	public static String GlobalSearchBarTextBox = "css=input[title='Search']";
	public static String GlobalSearchBarDropdown = "css=a.lotusScope";
	public static String GlobalSearchBarDropdownSelection = "css=a.lotusScope #commonSearchControlDivsearchOpt";
	public static String GlobalSearchBarContainer = "css=td[id*=dijit_MenuItem_]";
	public static String continueAnnouncement = "css=#signMe";
	// Sametime Availability Dialog
	public static String STAvailability = "css=[dojoattachpoint='stproxy_dock_availability']";
	//Mega menu option
	public static String MegaMenuApps = "css=li[id='lotusBannerApps']";
	public static String VisitorMenu = "css=a#visitorMenu_btn";
	public static String VisitorOrgsList = "css=div#visitorMenu table td a";
	public static String MegaMenuMetricsLink = "css=li[id='lotusBannerHeaderMetrics'] a:contains(Metrics)";
	public static String cloudLoginContinueButton = "css=input[id='continue'][class='joinBtn'], a[id='continuebutton'], input[id='continue']";
	public static String DatePicker_InputField = "css=input[id='lconn_act_ActivityForm_0duedate']";
	public static String DatePicker_CurrentMonth_Dates = "css=td[class*=dijitCalendarCurrentMonth]";
	public static String DatePicker_MonthLabel = "css=div[class^='dijitCalendarMonthLabel']";
	public static String DatePicker_MonthOptions = "css=div[class='dijitCalendarMonthMenu dijitMenu'] div[class='dijitCalendarMonthLabel']";
	public static String DatePicker_CurrentSelected_Year = "css=span[data-dojo-attach-point='currentYearLabelNode']";
	public static String DatePicker_PreviousSelected_Year = "css=span[data-dojo-attach-point='previousYearLabelNode']";
	public static String DatePicker_FollowingSelected_Year = "css=span[data-dojo-attach-point='nextYearLabelNode']";
	public static String imagePreview = "css=body>img";
	// Insert Link ckEditor on Edit Community Description section
	public static String ckePanelFrame = "css=iframe[class='cke_panel_frame']";
	public static String urlLink = "css=div[title='Options'] span[class='cke_menubutton_inner'] span:contains('URL Link'),a[class='cke_button cke_button__menulink cke_button_link cke_button_off'] span[class='cke_button_icon cke_button__link_icon']";
	public static String linkToConnectionsFiles = "css=div[title='Options'] span[class='cke_menubutton_inner'] span:contains('Link to Connections Files')";
	public static String urlInputField = "xpath=//label[text()='*URL:']//following-sibling::div";
	public static String linkTextInputField = "xpath=//label[text()='Link Text:']//following-sibling::div//input[@class='cke_dialog_ui_input_text']";
	public static String openWindowCheckbox = "css=input[class='cke_dialog_ui_checkbox_input']";
	public static String okButtonURLForm = "css=a[title='OK']";
	public static String insertLink = "css=a[class='cke_button cke_button__menulink cke_button_link cke_button_off'] , a[class='cke_button cke_button__menulink cke_button_off cke_button_link']>span[class='cke_button_icon cke_button__link_icon'],a[data-title='Insert Link'] span[class='cke_button_icon cke_button__link_icon']";
	public static String ckEditorBodyPar = "css=body>p";
	// Bosch Automation
	public static String listTagView = "css=a:contains(List)";
	public static String cloudTagView = "css=div[id='lconnTagWidget_tagCloudView']";
	public static String searchlinkDropdown = "xpath=//div[text()='Person not listed? Use full search...']";
	public static String mentionLink = "css=a:contains('PLACEHOLDER')[role='button'][class='fn url']";
	/**
	 * Tag Cloud operation
	 */

	public static String CloudLink = "link=Cloud";
	public static String ListLink = "link=List";
	public static String FindTag = "css=a[title='Find a Tag']";
	public static String taglinks = "css=div[class='lotusTagCloud'] ul li a";
	public static String emptyTags = "css=div[data-dojo-attach-point='_noTags']:contains(No tags yet)";
	public static String tagsHelp = "css=div[id='tagsHelp']>a>img[alt='Tags help']";
	public static String tagsHelpLauncher = "css=div[title='Tags help'] p";
	public static String tagsHelpMessage = "A tag is a keyword associated with community content to categorize it and make it easier to find. " +
			"Type or click a tag to see all community content associated with the tag. " +
			"Popular tags appear in larger text in the tag cloud.";
	// Access denied page
	public static String errorBox = "css=div[id='lotusFrame'] div[class='lotusErrorBox']";
	public static String AccessDenied = "css=div[class='lotusErrorForm'] h1";
	public static String NoPermissionToAccess = "css=div[class='lotusErrorForm'] p";
	public static String ckEditorFrame ="css=iframe[class='cke_wysiwyg_frame cke_reset']";

	//Language Selector
	public static String publicbuttonForStandaloneComp = "//span[contains(@class,'lotusBtn lotusBtnAction lotusLeft')]/a[@role='button']";
	public static String hclConnectionsLogo = "//span[contains(text(),'HCL Connections')]";
	public static String arabicLang = "//td[@id='dijit_MenuItem_16_text']";

	private BaseUIConstants() {}

}
