package com.ibm.conn.auto.webui.constants;

public class ShareUIContants {

	public static String share = "div[id='share-wrapper']>div>svg";
	public static String shareDialog = "div[id='customized-dialog-title']";
	public static String searchTypeAhead = "input[id='memberList']";
	public static String searchTypeheadResult = "li[id='memberList-option-0']";
	public static String selectedUser = "span[class='MuiChip-label']";
	public static String confirmationMsg = "div[class='MuiAlert-message']";
	public static String shareButton = "button[id='share-button']";
	public static String sharePost = "//div[contains(string(), 'PLACEHOLDER')]";
	public static String shareMessage = "textarea[id='message']";
	public static String closeShareIcon = "div[id='customized-dialog-title']>button";
	public static String cancelShareButton = "button[id='cancel-button']";
	public static String userIcon = "div[class='MuiGrid-root MuiGrid-item MuiGrid-grid-xs-1']>div>img";
	public static String previewTitle = "span[class='MuiTypography-root MuiCardHeader-title MuiTypography-body2 MuiTypography-displayBlock']";
	public static String previewText = "span[class='MuiTypography-root MuiCardHeader-subheader MuiTypography-body2 MuiTypography-colorTextSecondary MuiTypography-displayBlock']>a";
	public static String recetUpdates = "li[id='RecentUpdates_navItem']";
	public static String communitySelect = "css=select[id$='shareWithDropdown']";
	public static String shareWithPeople= "div[class='sharewith-options']";
	
	// share icon dropdown options
	public static String shareDialogButtonWhenMSTeamsEnabled = "//div[@id=\"share-wrapper\"]";
	public static String shareWithMSTeamDropDownOption="//li[@class='MuiButtonBase-root MuiListItem-root MuiMenuItem-root teams-share-button MuiMenuItem-gutters MuiListItem-gutters MuiListItem-button']";
	public static String shareInConnectionsDropDownOption = "//ul[@class='MuiList-root MuiMenu-list MuiList-padding'][@role='menu']/li[contains(text(), 'Share in Connections')]";
	
	public static String shareInConnectionsInWiki= "div[id='shareWrapper_wiki']>div";
	public static String extension="//a[contains(text(),'Extensions')]";
	public static String msTeamsShareInAppReg="//div[@class='ic-app-name']/h3";
	public static String extensionType="//div[@class='bx--card__business-card__title']/p[@class='bx--about__title--name']";
	public static String backToAppsButton="//a[@id='appsTab']";
	public static String blogPostEntryDiv = "//h4/a[contains(text(), 'PLACEHOLDER')]";
	public static String blogShareLink = "div[id=PLACEHOLDER]>div>a";
	public static String shareInMSTeamsInWiki= "//div[@id=\"shareWrapper_wiki\"]/div/a";
	
	// ShareInConnections-MemberPicker
	public static String resultListPopup = "//span[starts-with(@class,'MuiTypography-root MuiListItemText-primary')][contains(text(),'PLACEHOLDER')]";
	public static String mUIChipAvatar = "div[class^='MuiChip-root'] div[class*='MuiChip-avatar']";
	public static String mUIChipRemoveIcon = "div[class^='MuiChip-root'] svg[class*='MuiChip-deleteIcon']";
	public static String mUIDropDownIcon = "button[class='MuiButtonBase-root MuiIconButton-root MuiIconButton-sizeSmall'] svg";
	public static String mUISearchIcon = "div[class$='MuiOutlinedInput-marginDense'] svg[data-mui-test='searchIcon']";
	public static String mUISuggestionMsgBox = "div[class='MuiAutocomplete-noOptions']";
	public static String mUIMemberAddIcon = "//li//span[contains(text(),'PLACEHOLDER')]/ancestor::li//*[local-name()='svg' and @data-mui-test='addIcon']";
	public static String mUIMemberCheckMarkIcon = "//li//span[contains(text(),'PLACEHOLDER')]/ancestor::li//*[local-name()='svg' and @data-mui-test='checkmarkIcon']";
	public static String mUIMemberList = "ul[id='memberList-popup']";
}
