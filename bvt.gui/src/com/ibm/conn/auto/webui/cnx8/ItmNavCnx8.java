package com.ibm.conn.auto.webui.cnx8;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import java.util.ArrayList;
import java.util.List;

import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.conn.auto.webui.constants.ItmNavUIConstants;
import com.ibm.conn.auto.webui.constants.OrientMeUIConstants;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.atmn.waffle.utils.Assert;

public class ItmNavCnx8 extends HCBaseUI {
	
	protected static Logger log = LoggerFactory.getLogger(FilesUICnx8.class);
	private Assert cnxAssert = new Assert(log);

	public ItmNavCnx8(RCLocationExecutor driver) {
		super(driver);
	}

	/**
	 * Add the target item to the Important to Me list
	 * @param targetItem
	 * @param exactMatchInTypeahead true if the item has to exactly match the target string
	 * @return element added
	 */
	public WebElement addImportantItem(String targetItem, boolean exactMatchInTypeahead) {
		if (!cfg.getUseNewUI()) {
			waitForElementVisibleWd(createByFromSizzle(OrientMeUIConstants.addImportantItem), 5);
			clickLinkWd(createByFromSizzle(OrientMeUIConstants.addImportantItem));
		} else {
			waitForElementVisibleWd(By.cssSelector(ItmNavUIConstants.addImportantItem), 5);
			clickLinkWd(By.cssSelector(ItmNavUIConstants.addImportantItem));
		}

		waitForElementVisibleWd(By.cssSelector(ItmNavUIConstants.addImportantBox), 5);
		typeWithDelayWd(targetItem, By.cssSelector(ItmNavUIConstants.addImportantBox));
		waitForTypeaheadDone();
		
		boolean isSelected = selectItemInTypeahead(ItmNavUIConstants.importantToMeTypeahead, targetItem, exactMatchInTypeahead);
		if (!isSelected && isElementVisibleWd(By.cssSelector(ItmNavUIConstants.importantToMeTypeaheadMore),3))  {
			// click More if exists then try find the item again
			try {
				clickLinkWd(By.cssSelector(ItmNavUIConstants.importantToMeTypeaheadMore));
				isSelected = selectItemInTypeahead(ItmNavUIConstants.importantToMeTypeahead, targetItem, exactMatchInTypeahead);
			} catch (AssertionError ae)  {
				cnxAssert.assertTrue(false, "Item not found in typeahead: " + targetItem);
			}
		}
		cnxAssert.assertTrue(isSelected, "Item is selected from typeahead!");
		
		// check the item is in the Important to Me list now
		WebElement item = getItemInImportantToMeList(targetItem, false);
		cnxAssert.assertNotNull(item, "Item is in the Important to Me list.");
		
		return item;
	}
	
	
	/**
	 * Select the target item in the given typeahead
	 * @param typeahead Top level selector of the typeahead
	 * @param targetItem
	 * @param exactMatch true if the item has to exactly match the target string
	 * @return
	 */
	private boolean selectItemInTypeahead(String typeahead, String targetItem, boolean exactMatch) {
		// uncomment sleep if we see timing issue selecting the entry again
	//	sleep(1000);
		waitForElementsVisibleWd(By.cssSelector(typeahead),10);
		List<WebElement> typeaheadItems = findElements(By.cssSelector(typeahead));

		log.info("Select item in typeahead: " + targetItem);
		boolean isSelected = false;
		for (WebElement item : typeaheadItems)  {
			if (exactMatch)  {
				// compare the entire string to avoid selecting 'Community for Bill User1' for 'Bill User1'
				waitForClickableElementWd(By.cssSelector("div.ui-typeahead-primary"), 4);
				WebElement entry1stLineText =  findElement(By.cssSelector("div.ui-typeahead-primary"));
				if (entry1stLineText.getText().equalsIgnoreCase(targetItem)) {
					isSelected = true;
				}
			} else {
				if (item.getText().toLowerCase().endsWith(targetItem.toLowerCase())) {
					isSelected = true;
				}
			}
			if (isSelected) {
				item.click();
				break;
			}
		}
		return isSelected;
	}
	
	
	/**
	 * Returns the target item in the Important to Me list
	 * @param item name of the target item (eg, user, community)
	 * @param includeSuggested whether it should attempt to find the item in the suggested list if it is not in the active list.
	 * @return Element of the target item, null if not found.
	 */
	public WebElement getItemInImportantToMeList(String targetItem, boolean includeSuggested) {
		String targetItemLocator = ItmNavUIConstants.importantToMeList +" [aria-label='"+targetItem+"']";
		List<WebElement> importantItem = findElements(By.cssSelector(targetItemLocator));

		if (importantItem.size() > 0) {
			return importantItem.get(0);
		}
		
		if (includeSuggested)  {
			targetItemLocator = ItmNavUIConstants.suggestedImportantList + " li[aria-label='"+targetItem+"']";
			List<WebElement> suggestedItems = findElements(By.cssSelector(targetItemLocator));
			if (suggestedItems.size() > 0) {
				return suggestedItems.get(0);
			}
		}
		
		log.info("Target item not found in the Important to Me list: " + targetItem);	
		return null;
	}
	
	/**
	 * Remove all items from the Important to Me list
	 */
	public void removeAllItemsFromImportantToMeList()
	{
		goToExtremeEndInITM(ItmNavUIConstants.downArrowInITMCarousel);
		clickLinkWaitWd(By.cssSelector(ItmNavUIConstants.removeImportantItem), 5);
		List<WebElement> removeIcons = findElements(By.cssSelector(ItmNavUIConstants.xIconsInItemBubble));
		for(WebElement removeIcon : removeIcons)
		{
			clickLinkWithJavaScriptWd(removeIcon);
		}
	}
	
	/**
	 * Remove the target item from the Important to Me list
	 * @param target item
	 */
	public void removeItemFromImportantToMeList(String item)  {
		WebElement targetItem = getItemInImportantToMeList(item, false);
		scrollToElementWithJavaScriptWd(targetItem);
		if (targetItem.isDisplayed())  {
			if(cfg.getUseNewUI())
			{
				clickLinkWd(By.cssSelector(ItmNavUIConstants.removeImportantItem),"click on removeImportantItem");
			}
			else
			{
				clickLinkWd(By.cssSelector(ItmNavUIConstants.removeImportantItem),"click on removeImportantItem");
			}

			// get the X icon in the bubble
			clickLinkWd(By.cssSelector(ItmNavUIConstants.xIconInItemBubble.replace("PLACEHOLDER", item)), "click on xIconInItemBubble");

			waitForElementInvisibleWd(targetItem, 5);

			// exit ITM delete mode
			if(cfg.getUseNewUI())
			{
				clickLinkWd(By.cssSelector(ItmNavUIConstants.removeImportantItem),"click on removeImportantItem");
			}
			else
			{
				clickLinkWd(By.cssSelector(ItmNavUIConstants.removeImportantItem),"click on removeImportantItem");
			}
		} else {
			throw new RuntimeException("Target element is not displayed.");
		} 
	}
	
	/**
	 * Wait until the typeahead loading skeleton UI is gone.
	 */
	private void waitForTypeaheadDone()  {
		// Need to temporary turn off implicit wait otherwise WebDriverWait timeout will not be used.
		// https://github.com/SeleniumHQ/selenium/issues/718
		driver.turnOffImplicitWaits();
		WebDriverWait wait = new WebDriverWait((WebDriver)driver.getBackingObject(), 7);
		wait.until(ExpectedConditions.not(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("div.ui-people-loading-item"))));
		driver.turnOnImplicitWaits();
	}
	
	/**
	 * Get Important to Me icon label text
	 * @param icon element
	 * @return label text
	 */
	public String getItmIconLabel(WebElement iconElem) {
		return iconElem.getAttribute("aria-label");
	}
	

	/**
	 * Click the target item from Important to Me list
	 * @param targetItem
	 */
	public boolean clickImportantItem(String targetItem) {
		WebElement item = getItemInImportantToMeList(targetItem, false);
		if (item != null) {
			item.click();
			return true;			
		}
	
		log.info("Target item not found in the Important to Me list: " + targetItem);	
		return false;
	}
	
	/**
	 * Click the target item from Important to Me list based on selected UI
	 * @param String title - Title of the ITM item
	 * @param WebElement elementInITM - main bubble like community icon, people icon on ITM
	 * @param HomepageUI hUI -  Homepage UI object to get the control of the element of UI
	 * @param String mainBrowserWindowHandle - main browser  window handle element help in switching on new windowW
	 */
	public void clickOnCommImportantItems(String title,WebElement elementInITM, HomepageUI hUI, String mainBrowserWindowHandle) throws Exception
    {
        ItmNavCnx8 itmNavCnx8 = new ItmNavCnx8(driver);
        if(cfg.getUseNewUI()) {
            itmNavCnx8.clickImportantItem(title);             
        }
        else {   
            itmNavCnx8.clickSubItemfromITM(elementInITM, By.xpath(ItmNavUIConstants.viewCommunityInCommunityIcon.replace("PLACEHOLDER", title)));
            UIEvents.switchToNextOpenBrowserWindowByHandle(hUI, mainBrowserWindowHandle);
            hUI.switchTabs(2, 1);
        }
    }
	
	
	/**
	 * This method will click on required subItem 
	 * @param WebElement item - main bubble like community icon, people icon on ITM
	 * @param By locator - Bylocator of subItem in main bubble like "view community", "business card" ,"compose" 
	 */
	public void clickSubItemfromITM(WebElement item,By locator)
	{
		// observed that doing scrollToElementWithJavaScriptWd will accidentally click 
		// the top nav bar afterwards so only scroll in new UX
		if (cfg.getUseNewUI())  {
			scrollToElementWithJavaScriptWd(item);
		}
		
		mouseHoverWd(item);
		WebElement viewedItem=waitForElementVisibleWd(locator,5); 
		mouseHoverWd(viewedItem);
		clickLinkWithJavaScriptWd(viewedItem);
	}
	
	/**
	 * This method will check the list of visible element after clicking on down and up arrow in ITM.
	 * @return Boolean - true if list of element is different after click down/up arrow.
	 * false if list of element is same after click down/up arrow.
	 */
	public Boolean verifyArrowButtons()
	{  
		Boolean flag=false;
		waitForPageLoaded(driver);
		List<String> impToMeVisibleElementsBefore = new ArrayList<String>();
		List<String> impToMeVisibleElementsAfter = new ArrayList<String>();;
		waitForElementVisibleWd(By.cssSelector(ItmNavUIConstants.addImportantItem),6);
		List<WebElement> impToMeElements = findElements(By.cssSelector(ItmNavUIConstants.importantToMeListAll));
		
		log.info("Add items if count is less than 10");
		for(int i = impToMeElements.size();i<=10;i++)
		{
			User testUser = cfg.getUserAllocator().getUser();
			addImportantItem(testUser.getDisplayName(), true);
		}

		log.info("Move to the top of ITM bar");
		while(isElementPresentWd(By.cssSelector(ItmNavUIConstants.upArrowInITMCarousel)))
		{
			clickLinkWd(By.cssSelector(ItmNavUIConstants.upArrowInITMCarousel), "click on up arrow in ITM");
		}
		
		log.info("Get list of visible entries before click on down arrow");
		for(int i=1;i<=impToMeElements.size();i++)
		{
			if(isElementVisibleWd(By.xpath("//ul[@class='active-sets']/li["+i+"]/span/.."),1))
			{
				impToMeVisibleElementsBefore.add(findElement(By.xpath("//ul[@class='active-sets']/li["+i+"]/span/..")).getAttribute("aria-label"));
			}
		}
		
		log.info("Click on down arrow of ITM bar");
		clickLinkWd(By.cssSelector(ItmNavUIConstants.downArrowInITMCarousel));
		waitForSameTime();
		
		log.info("Get list of visible entries after click on down arrow");
		for(int i=1;i<=impToMeElements.size();i++)
		{
			if(isElementVisibleWd(By.xpath("//ul[@class='active-sets']/li["+i+"]/span/.."),1))
			{
				impToMeVisibleElementsAfter.add(findElement(By.xpath("//ul[@class='active-sets']/li["+i+"]/span/..")).getAttribute("aria-label"));
			}
		}
		
		if(!impToMeVisibleElementsBefore.equals(impToMeVisibleElementsAfter))
		{
			flag=true;
			cnxAssert.assertTrue(flag, "List of bubble elements are different after clicking on Down arrow");

		}

		if(flag)
		{
			log.info("Click on up arrow of ITM bar");
			clickLinkWd(By.cssSelector(ItmNavUIConstants.upArrowInITMCarousel));
			
			log.info("Get list of visible entries after click on up arrow");
			for(int i=1;i<=impToMeElements.size();i++)
			{
				if(isElementVisibleWd(By.xpath("//ul[@class='active-sets']/li["+i+"]/span/.."),1))
				{
					impToMeVisibleElementsAfter.add(findElement(By.xpath("//ul[@class='active-sets']/li["+i+"]/span/..")).getAttribute("aria-label"));
				}
			}
			if(impToMeVisibleElementsBefore.equals(impToMeVisibleElementsAfter)) {
				flag=false;
			}
			cnxAssert.assertTrue(flag, "List of bubble elements are same after clicking on Up arrow");

		}
		return flag;
	}
	
	/**
	 * This method is used to scroll down from up till the element is visible in ITM.
	 * @param element - WebElement, which needs to be visible 
	 */
	public void scrollTillElementIsVisible(WebElement element)
	{
		log.info("Move to the top of ITM bar");
		while(isElementPresentWd(By.cssSelector(ItmNavUIConstants.upArrowInITMCarousel)))
		{
			clickLinkWd(By.cssSelector(ItmNavUIConstants.upArrowInITMCarousel), "click on up arrow in ITM");
		}
		while(waitForElementInvisibleWd(element, 4))
		{	
			clickLinkWd(By.cssSelector(ItmNavUIConstants.downArrowInITMCarousel), "click on down arrow in ITM");
		}
	}
	
	/**
	 * This method is used to go to extreme end in ITM, based on parameter.
	 * @param String - end where we want to go.
	 */
	public void goToExtremeEndInITM(String value)
	{
		while(isElementPresentWd(By.cssSelector(value)))
		{
			clickLinkWd(By.cssSelector(value), "click on up arrow in ITM");
		}
	}
	/**
	 * This returns locator for entry specific remove icon 
	 * @param userName
	 * @return
	 */
	public static String getFilterIcon(String userName)
	{
			return "li[aria-label='"+userName+"'] div[class='ic-bizcard-actions'] button[aria-label='Filter']";
		}
	
	/**
	 * This method validates the presence of Collapse and Expand Icon
	 */
	public boolean validateCollapseAndExpandInITM() {
		boolean flag = false;
		
		log.info("Validate the presence of Collpase and Expand Icon and Click");
		if (isElementPresentWd(By.xpath(ItmNavUIConstants.collapseIcon))) {
			clickLinkWd(By.xpath(ItmNavUIConstants.collapseIcon));
			clickLinkWd(By.xpath(ItmNavUIConstants.expandIcon));
			flag = true;
		}
		return flag;
	}

	/**
	 * This adds user to ITM bar and clicks on person filter icon
	 * @param userAddedToITM - user being added to ITM bar
	 */
	public void addUserToITMAndClickFilterIcon(User userAddedToITM) {

		log.info("Add " + userAddedToITM.getDisplayName());
		WebElement user = getItemInImportantToMeList(userAddedToITM.getDisplayName(), false);
		if (user == null) {
			log.info("Add " + userAddedToITM.getDisplayName() + " to the Important to Me list.");
			addImportantItem(userAddedToITM.getDisplayName(), true);
		} else {
			log.info(userAddedToITM.getDisplayName() + " is already in the Important to Me list.");
		}
		log.info("Click on filter icon associated with " + userAddedToITM.getDisplayName());
		WebElement item = getItemInImportantToMeList(userAddedToITM.getDisplayName(), false);
		waitForElementVisibleWd(item, 5);
		scrollToElementWithJavaScriptWd(item);
		mouseHoverWd(item);
		clickLinkWaitWd(By.cssSelector(ItmNavCnx8.getFilterIcon(userAddedToITM.getDisplayName())), 6, "");
	}

}
