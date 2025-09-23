package com.ibm.conn.auto.webui.cnx8;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import com.ibm.conn.auto.webui.constants.ProfilesUIConstants;
import com.ibm.conn.auto.webui.onprem.ProfilesUIOnPrem;

public class ProfilesUICnx8 extends HCBaseUI {
	
	protected static Logger log = LoggerFactory.getLogger(FilesUICnx8.class);
	HomepageUICnx8 homepageCnx8ui = new HomepageUICnx8(driver);
	
	public ProfilesUICnx8(RCLocationExecutor driver) {
		super(driver);
	}
	

	public static String getLabel(String label) {
		return "//span[@id='label_"+label+"']";
	}
	
	public static String getDropdown(String label) {
		return "select_"+label;
	}
	
	/**
	 * locate the element tag link
	 * @param tag 
	 */
	public static String getTagLink(String tag) {
		return "//div[@id='tagCloud']//a[text()='"+tag+" ']";
	}
	
	/**
	 * locate the element tag filter
	 * @param tag 
	 */
	public static String getFilterOfTag(String tag) {
		return "//span[text()='"+tag+"']";
	}
	
	/**
	 * locate the element search grid
	 * @param email 
	 */
	public static String getSearchGrid(String email) {
		return "//a[text()='"+email+"']/ancestor::div[@class='cnx8ui-result-item-grid']";
	}
	
	/**
	 * locate the element user link 
	 * @param userName 
	 */
	public static String getUserLinkFromAllPeople(String userName) {
		return "//a[text()='"+userName+"']";
	}
	
	/**
	 * locate the element user link 
	 * @param userName 
	 */
	public static String getActionMenu(String menu) {
		return "//table[contains(@class,'dijitMenuSelectedFocused')]//tr//td[text()='"+menu+"']";
	}
	
	/**
	 * getUserLinkFromDirSearchResult
	 * @param userName 
	 */
	public static String getUserLinkFromDirSearchResult(String userName) {
		return "//a[@title='"+userName+"']";
	}
	
	/**
	 * getUserFromMyNetworkUserList
	 * @param userName 
	 */
	public static String getUserFromMyNetworkUserList(String userName) {
		return "//table[@id='friends_mainContentTable']//tr//span[@class='vcard']//a[text()='"+userName+"']";
	}
	
	/**
	 * getUserFromMyNetworkUserList
	 * @param userName 
	 */
	public static String getUserFromInvitationTab(String userName) {
		return "//table[contains(@class,'lotusTable cnx8Invitetbl')]//tr//span[@class='vcard']//a[text()='"+userName+"']";
	}
		
	/**
	 * Type user name  in directory search and check exact user is displayed
	 * @param User
	 * @return boolean
	 */
	public boolean isDirectorySearchResultExactMatching(User userName)
	{

		String SearchUser = userName.getDisplayName();
		waitForElementsVisibleWd(createByFromSizzle(ProfilesUIConstants.DirectorySearch), 8);
		typeWithDelayWd(SearchUser, createByFromSizzle(ProfilesUIConstants.DirectorySearch));
		boolean check  = false;
		List<WebElement> userFullName = findElements(createByFromSizzle(HomepageUIConstants.userNamelink));

		for (WebElement uName : userFullName) {
			if(uName.getText().equalsIgnoreCase(SearchUser)) {
				check = true;
				break; 
			}
		}
        return check;
	} 
	
	/**
	 * Send Invite To My Network to User
	 */
	public void sendInvite()
	{
		homepageCnx8ui.clickLinkWaitWd(By.cssSelector(HomepageUIConstants.bizCardVerticalEllipsisIcon), 4);
		homepageCnx8ui.scrollToElementWithJavaScriptWd(homepageCnx8ui.createByFromSizzle(HomepageUIConstants.bizCardInviteToMyNetwork));
		homepageCnx8ui.clickLinkWaitWd(homepageCnx8ui.createByFromSizzle(HomepageUIConstants.bizCardInviteToMyNetwork),4,"Click on Invite to my network");
		homepageCnx8ui.waitForElementVisibleWd(homepageCnx8ui.createByFromSizzle(ProfilesUIConstants.SendInvite), 3);
		homepageCnx8ui.clickLinkWaitWd(homepageCnx8ui.createByFromSizzle(ProfilesUIConstants.SendInvite),3,"Send Invite");
	
	}
	
	/**
	 * Click on three dots drop-dwon <Ellipsis Icon> on Biz Card
	 */
	public void clickThreeDotsDropDownIcon()
	{
		if(cfg.getUseNewUI()) {
		homepageCnx8ui.waitForElementVisibleWd(By.cssSelector(HomepageUIConstants.bizCardVerticalEllipsisIcon), 3);
		homepageCnx8ui.clickLinkWaitWd(By.cssSelector(HomepageUIConstants.bizCardVerticalEllipsisIcon),3,"Ellipsis Icon with three vertical dots");
		}

	}

	/**
	 * this methods clicks on three dots on another user profile and select the
	 * specified action menu
	 * 
	 * @param actionMenu
	 */

	public void selectActionMenufromTable(String actionMenu) {

		log.info("INFO: Click on three dots icon(more action)");
		waitForElementVisibleWd(By.xpath(ProfilesUIConstants.threeDotsActionIcon), 4);
		clickLinkWaitWd(By.xpath(ProfilesUIConstants.threeDotsActionIcon), 4, "Click on three dots icon(more action) ");

		log.info("INFO: Select" + actionMenu + " from the menus");
		waitForElementVisibleWd(By.xpath(ProfilesUIConstants.anotherUserProfActionMenuTable), 4);
		clickLinkWaitWd(By.xpath(ProfilesUICnx8.getActionMenu(actionMenu)), 4,"Click " + actionMenu + " from the menus");

	}
	
	/**
	 * locate component from Ellipsis Drop Down
	 * @param  compName
	 */
	public static String getEllipsisDropDownValue(String compName) {
		return "//div[@id='myDropdown']/a[@title='"+compName+"']";
	}
	
	/* Save&Close at Edit Profile
	 * 
	 */
	
	public void saveAndcloseAtEditProfile() {		
		String saveButton="";
		if (cfg.getUseNewUI()) {
			saveButton = "Save & Close";
		} else {
			saveButton = "Save and Close";

		}
		homepageCnx8ui.clickButton(saveButton);
		
		
	}
}
