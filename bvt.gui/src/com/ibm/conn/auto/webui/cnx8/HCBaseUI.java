package com.ibm.conn.auto.webui.cnx8;

import java.util.List;
import java.util.Set;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotSelectableException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.atmn.waffle.utils.Assert;
import com.ibm.conn.auto.webui.ICBaseUI;

public class HCBaseUI extends ICBaseUI {
	
	private static Logger log = LoggerFactory.getLogger(HCBaseUI.class);
	
	public HCBaseUI(RCLocationExecutor driver) {
		super(driver);
	}
	
	public WebElement findElement(By byLocator)  {
		log.info("INFO: findElement: " + byLocator.toString());
		driver.turnOffImplicitWaits();
		WebElement elment = ((WebDriver)driver.getBackingObject())
				.findElement(byLocator);
		driver.turnOnImplicitWaits();
		return elment;
	}
	
	public List<WebElement> findElements(By byLocator)  {
		log.info("INFO: findElements: " + byLocator.toString());
		driver.turnOffImplicitWaits();
		List<WebElement> elments = ((WebDriver)driver.getBackingObject())
				.findElements(byLocator);
		driver.turnOnImplicitWaits();
		return elments;
	}
	
	/**
	 * WebElement click with logging
	 * @param byLocator By locator
	 * @param msg optional element identification string to add to logging
	 */
	public void clickLinkWd(By byLocator, String... msg) {
		WebElement element = findElement(byLocator);
		clickLinkWd(element, msg);
	}
	
	public void clickLinkWd(WebElement element, String... msg) {
		String elementText;
		elementText = msg.length == 0 ? element.getText() : msg[0];
		log.info("INFO: click action will be performed on element: " + elementText);
		element.click();
		log.info("INFO: clickLink was performed on element: " + elementText);
	}
	

	/**
	 * WebElement click with custom wait time for the element to be visible
	 * @param byLocator
	 * @param timeoutInSec
	 * @param msg optional element identification string to add to logging
	 */
	public void clickLinkWaitWd(By byLocator, long timeoutInSec, String... msg)  {
		WebElement element = waitForElementVisibleWd(byLocator, timeoutInSec);
		clickLinkWd(element, msg);
	}
	
	/**
	 * Wait for the given element to be visible
	 * @param byLocator
	 * @param timeoutInSec
	 * @return
	 */
	public WebElement waitForElementVisibleWd(By byLocator, long timeoutInSec)  {
		log.info("INFO: Wait for element: " + byLocator.toString());
		driver.turnOffImplicitWaits();
		WebElement element = getWebDriverWait(timeoutInSec)
				.until(ExpectedConditions.visibilityOfElementLocated(byLocator));
		driver.turnOnImplicitWaits();
		return element;
	}
	
	public List<WebElement> waitForElementsVisibleWd(By byLocator, long timeoutInSec)  {
		log.info("INFO: Wait for element: " + byLocator.toString());
		driver.turnOffImplicitWaits();
		List<WebElement> elements = getWebDriverWait(timeoutInSec)
				.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(byLocator));
		driver.turnOnImplicitWaits();
		return elements;
	}


	public Boolean waitForElementInvisibleWd(By byLocator, long timeoutInSec)  {
		log.info("INFO: Wait for element: " + byLocator.toString());
		Boolean flag=false;
		driver.turnOffImplicitWaits();
		try
		{
			flag = getWebDriverWait(timeoutInSec)
					.until(ExpectedConditions.invisibilityOfElementLocated(byLocator));
			driver.turnOnImplicitWaits();
		}
		catch(Exception e)
		{
			log.warn("element is visible "+ e.getStackTrace());
		}
		return flag;
	}
	
	public Boolean waitForElementInvisibleWd(WebElement element, long timeoutInSec)  {
		Boolean flag=false;
		driver.turnOffImplicitWaits();
		try
		{
			flag = getWebDriverWait(timeoutInSec)
					.until(ExpectedConditions.invisibilityOf(element));
			driver.turnOnImplicitWaits();
		}
		catch(Exception e)
		{
			log.warn("element is visible "+ e.getStackTrace());
		}
		return flag;
	}
	
	public Boolean waitForElementVisibleWd(WebElement element, long timeoutInSec)  {
		Boolean flag=false;
		driver.turnOffImplicitWaits();
		try
		{
			getWebDriverWait(timeoutInSec)
					.until(ExpectedConditions.visibilityOf(element));
			flag=true;
			driver.turnOnImplicitWaits();
		}
		catch(Exception e)
		{
			log.warn("element is invisible "+ e.getStackTrace());
		}
		return flag;
	}
	
	public WebElement waitForClickableElementWd(By byLocator, long timeoutInSec)  {
		log.info("INFO: Wait for element: " + byLocator.toString());
		driver.turnOffImplicitWaits();
		WebElement element = getWebDriverWait(timeoutInSec)
				.until(ExpectedConditions.elementToBeClickable(byLocator));
		driver.turnOnImplicitWaits();
		return element;
	}
	
	public WebDriverWait getWebDriverWait(long timeoutInSec) {
		return new WebDriverWait(
				(WebDriver)driver.getBackingObject(), timeoutInSec);
	}
	
	
	public boolean isElementPresentWd(By byLocator) {
		try {
			findElement(byLocator);
			return true;
		} catch (org.openqa.selenium.NoSuchElementException nse)  {
			return false;
		}
	}
	
	public boolean isElementVisibleWd(By byLocator, long timeoutInSec) {
		try {
			waitForElementVisibleWd(byLocator, timeoutInSec);
			return true;
		} catch (org.openqa.selenium.TimeoutException te)  {
			return false;
		}
	}

	 public boolean isElementDisplayedWd(By byLocator) {
		 WebElement element = findElement(byLocator);
	        try {
	            return element.isDisplayed();
	        } catch (org.openqa.selenium.NoSuchElementException nse) {
	            return false;
	        }
	    }
	
	public void typeWithDelayWd(String text,By byLocator) {
		WebElement element = findElement(byLocator);
		char[] charsText = text.toCharArray();
		sleep(1000);
	//	element.click();
		waitForElementVisibleWd(byLocator,5);
		for (char ct : charsText){
			element.sendKeys(String.valueOf(ct));
			sleep(700);
		}
	}
	
	public String getElementTextWd(By byLocator){
		waitForElementVisibleWd(byLocator, 3);		
		WebElement element = findElement(byLocator);
		log.info("INFO: Get text of " + element.getText());
		return element.getText();
	}
	
	public void clearTexWithJavascriptWd(By byLocator) {
		waitForElementVisibleWd(byLocator, 3);		
		WebElement element = findElement(byLocator);
		JavascriptExecutor js = (JavascriptExecutor)((WebDriver)driver.getBackingObject());
		js.executeScript("arguments[0].value = '';", element);
		log.info("INFO: textfield or area has been cleared");
	}
	
	public boolean isTextPresentWd(String text) {

		Boolean isTextPresent = false;
		if(((WebDriver)driver.getBackingObject()).getPageSource().contains(text))
		{
			isTextPresent=true;
		}
		return isTextPresent;
	}	
	
	public void clickLinkWithJavaScriptWd(WebElement element) {
		JavascriptExecutor js = (JavascriptExecutor)((WebDriver)driver.getBackingObject());
		js.executeScript("arguments[0].click();", element);  

	}
	
	public void scrollToElementWithJavaScriptWd(WebElement element) {
		JavascriptExecutor js = (JavascriptExecutor)((WebDriver)driver.getBackingObject());
		js.executeScript("arguments[0].scrollIntoView(true);", element);  
	}
	
	public void scrollToElementWithJavaScriptWd(By locator) {
		WebElement element = findElement(locator);
		JavascriptExecutor js = (JavascriptExecutor)((WebDriver)driver.getBackingObject());
		js.executeScript("arguments[0].scrollIntoView(true);", element);  
	}
	
	public void acceptPopupWd() {
		try
		{
			Alert alert = ((WebDriver)driver.getBackingObject()).switchTo().alert();
			alert.accept();
		}
		catch (Exception e) {

			System.out.println("no alert displayed");
		}
	}

	public void mouseHoverAndClickWd(WebElement element) {
		Actions action = new Actions((WebDriver)driver.getBackingObject());
		action.moveToElement(element).doubleClick().build().perform();
	}

	public void mouseHoverWd(WebElement element) {
		Actions action = new Actions((WebDriver)driver.getBackingObject());
		action.moveToElement(element).build().perform();
	}
	
	public void switchToNextWindowWd(String childUrl)   {
		
		Set<String> allWindowHandles = ((WebDriver)driver.getBackingObject()).getWindowHandles();
		getWebDriverWait(5)
				.until(ExpectedConditions.numberOfWindowsToBe(2));
		String window = (String) allWindowHandles.toArray()[1];
		((WebDriver)driver.getBackingObject()).switchTo().window(window);
		getWebDriverWait(5)
		.until(ExpectedConditions.urlContains(childUrl));
	}
	
	/**
	 * Method to create By selector based on sizzle locator (i.e. old locators in the project)
	 * Note: it will not work properly if the locator provided is not supported by native Selenium 
	 * or CSS 3 like comma separated selector or pseudo class :contains.
	 * @param locator
	 * @return
	 */
	public By createByFromSizzle(String locator) {	
		String convertedLocator;
		
		if (locator.contains("xpath=")) {
			convertedLocator = locator.replace("xpath=", "");
			return By.xpath(convertedLocator);
		} else if (locator.contains("css=")) {
			convertedLocator = locator.replace("css=", "");
			return By.cssSelector(convertedLocator);
		}
		throw new IllegalArgumentException("Sizzle locator prefix not found, "
				+ "maybe you don't need to call this method.");		
	}
	
	/**
     * Method to switch to particular frame.
     * @param element - WebElement of frame where we need to switch
     */
    public void switchToFrame(WebElement element)
    {
        ((WebDriver)driver.getBackingObject()).switchTo().frame(element);
    }
    

	/**
     * Method to close current window and switch to particular window 
     * @param element - WebElement of frame where we need to switch
     */

	public void closeCurrentWindowAndMoveToParentWindowWd(String parentWindow) {
		String windowHandle = ((WebDriver)driver.getBackingObject()).getWindowHandle();
		((WebDriver)driver.getBackingObject()).switchTo().window(windowHandle).close();
		((WebDriver)driver.getBackingObject()).switchTo().window(parentWindow);
	}

  public void refreshPage()
    {
    	((WebDriver)driver.getBackingObject()).navigate().refresh();
    }
  
	/**
	 * Method to select a dropdown value
	 * 
	 * @param By     byLocator - Locator of webelement
	 * @param String text - text to select from a dropdown
	 */
	public void selectElementByText(By byLocator, String text) {
		log.info("INFO: findElement: " + byLocator.toString());
		driver.turnOffImplicitWaits();
		WebElement element = ((WebDriver) driver.getBackingObject()).findElement(byLocator);
		driver.turnOnImplicitWaits();
		try {
			Select select = new Select(element);
			log.info("INFO: Selecting element: " + byLocator.toString() + " with text " + text);
			select.selectByVisibleText(text);
		} catch (ElementNotSelectableException e) {
			log.warn("element is selectable " + e.getStackTrace());
		}

	}
	

	/**
	 * Wait for text of given element to be true
	 * @param byLocator - locator of WebElement 
	 * @param timeoutInSec - time until browser waits
	 * @param text - element text to be verified
	 * @return
	 */
	
	public boolean waitForTextToBePresentInElementWd(By byLocator, String text, long timeoutInSec) {
		driver.turnOffImplicitWaits();

		Boolean textPresentInElement = false;
		try {
			textPresentInElement = getWebDriverWait(timeoutInSec).until(ExpectedConditions.textToBe(byLocator, text));
			driver.turnOnImplicitWaits();
		} catch (Exception e) {

			log.warn("text is not present in element  " + e.getStackTrace());
		}

		return textPresentInElement;
	}
	
	/**
	 * Wait for text of given element to be true
	 * @param element - WebElement 
	 * @param timeoutInSec - time until browser waits
	 * @param text - element text to be verified
	 * @return
	 */
	public boolean waitForTextToBePresentInElementWd(WebElement element, String text, long timeoutInSec) {
		driver.turnOffImplicitWaits();
		Boolean textPresentInElement = false;
		try {
			textPresentInElement = getWebDriverWait(timeoutInSec).until(ExpectedConditions.textToBePresentInElement(element, text));
			driver.turnOnImplicitWaits();
		} catch (Exception e) {

			log.warn("text is not present in element  " + e.getStackTrace());
		}
		return textPresentInElement;
	}

	/**
	 * Wait for page title to be title
	 * @param title
	 * @param timeoutInSec
	 * @return
	 */
	public boolean waitForTitleIsPresentWd(String title, long timeoutInSec) {
		driver.turnOffImplicitWaits();
		Boolean titleIsPresent = false;
		try {
			titleIsPresent = getWebDriverWait(timeoutInSec).until(ExpectedConditions.titleIs(title));
			driver.turnOnImplicitWaits();
		} catch (Exception e) {
			log.warn("Page title is not present:  " + e.getStackTrace());
		}
		return titleIsPresent;
	}

	/**
	 * Wait for number of elements to be available
	 * @param expNumberOfElements
	 * @param timeoutInSec
	 * @return
	 */
	public boolean waitForNumberOfElementsToBe(By byLocator,int expNumberOfElements, long timeoutInSec) {
		driver.turnOffImplicitWaits();
		Boolean expNumberOfElementsReturned = false;
		List<WebElement> elementsReturned = null;
		try {
			elementsReturned = getWebDriverWait(timeoutInSec).until(ExpectedConditions.numberOfElementsToBe(byLocator, expNumberOfElements));
			if (elementsReturned.size() == expNumberOfElements) {
				expNumberOfElementsReturned = true;
			}
			driver.turnOnImplicitWaits();
		} catch (Exception e) {
			log.warn("Expected number of elements are not present" + e.getStackTrace());
		}
		return expNumberOfElementsReturned;

	}

	/**
	 * Dragging element from source to target
	 * 
	 * @param source
	 * @param target
	 */
	public void dragAndDropWd(WebElement source, WebElement target) throws Exception {
		Actions acts = new Actions((WebDriver) driver.getBackingObject());
		Action action;
		acts.clickAndHold(source).moveByOffset(30, 30);
		action = acts.build();
		action.perform();
		acts.moveToElement(target).build().perform();
		acts.release().perform();
	}
	
    /**
     * Toggle to old UI
     */
    public void toggleToOldUI() {
		
		if (isElementVisibleWd(By.id("top-navigation"), 5)) {
			log.info("INFO: Intentionally toggle after login");
			waitForElementVisibleWd(By.id("theme-switcher-wrapper"), 4);
			clickLinkWd(By.id("theme-switcher-wrapper"), "new UI toggle switch");
			clickLinkWithJavaScriptWd(findElement(By.cssSelector("#theme_switcher_options_modal_switch input")));
			findElement(By.id("options_modal_save_button")).click();
		}
	}
/**
 * This method verifies font color and background color of specified element
 * @param ele
 * @param expTextColor
 * @param expBGcolor
 */
	public void verifyCssProperty(WebElement ele, String expTextColor, String expBGcolor) {
		String actTextColor, actBgColor;
		actTextColor = ele.getCssValue("color");
		actBgColor = ele.getCssValue("background-color");
		Assert cnxAssert = new Assert(log);
		cnxAssert.assertEquals(actTextColor, expTextColor, "Text color matches");
		cnxAssert.assertEquals(actBgColor, expBGcolor, "Background color matches");
	}

}
