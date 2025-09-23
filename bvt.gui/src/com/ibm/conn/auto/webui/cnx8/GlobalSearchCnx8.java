package com.ibm.conn.auto.webui.cnx8;

import java.util.ArrayList;
import java.util.List;

import com.ibm.conn.auto.webui.constants.GlobalSearchUIConstants;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.atmn.waffle.core.RCLocationExecutor;

public class GlobalSearchCnx8 extends HCBaseUI {
	
	protected static Logger log = LoggerFactory.getLogger(GlobalSearchCnx8.class);
	
	public GlobalSearchCnx8(RCLocationExecutor driver) {
		super(driver);
	}

	/**
     * Returns List of Element texts from Search Suggestion
     *
     * @return arrayList<String>
     */
	public ArrayList<String> textsInSearchSuggestion()
	{
		ArrayList<String> textsInSearchSuggestion = new ArrayList<>();
		List<WebElement> searchedSuggestions= findElements(By.xpath(GlobalSearchUIConstants.searchedSuggestionList));
		for(int i=0;i<searchedSuggestions.size();i++)
		{
			textsInSearchSuggestion.add(searchedSuggestions.get(i).getText());
		}
		
		return textsInSearchSuggestion;
	}
	
	/**
     * Returns flag if searched community is displayed in Community padding under Community typography
     * @param communityName - community to be searched
     * @return Boolean flag - return true of community is displayed.
     */
	public Boolean isSearchedCommunityDisplayInCommunititesTypography(String communityName)
	{
		Boolean flag=false;
		waitForElementsVisibleWd(By.xpath(GlobalSearchUIConstants.communitiesTypographyInSearchSuggestion), 5);
		List<WebElement> communitiesName = findElements(By.xpath(GlobalSearchUIConstants.communitiesTypographyInSearchSuggestion));
		for(WebElement community : communitiesName)
		{
			if(community.getText().equals(communityName))
			{
				flag=true;
				break;
			}
		}
		return flag;
	}

	
	/**
     * Returns flag if searched User is displayed in People padding under People typography
     * @param userDisplayName - User to be searched
     * @return Boolean flag - return true of User is displayed.
     */
	public Boolean isSearchedPeopleDisplayInPeopleTypography(String userDisplayName)
	{
		Boolean flag=false;
		waitForElementsVisibleWd(By.xpath(GlobalSearchUIConstants.peopleTypographyInSearchSuggestion), 5);
		List<WebElement> peopleNames = findElements(By.xpath(GlobalSearchUIConstants.peopleTypographyInSearchSuggestion));
		for(WebElement peopleName : peopleNames)
		{
			if(peopleName.getText().equals(userDisplayName))
			{
				flag=true;
				break;
			}
		}
		return flag;
	}
	
	/**
     * Method to check if all results contain searched Community
     * @param CommunityName - user Name to be check
     * @return Boolean flag - true of all results contains searched Community and false if anyone from list of result does not contains searched Community
     */
	public Boolean isResultListContainsSearchedCommunity(String communityName)
	{
		Boolean flag=true;
		waitForElementsVisibleWd(By.xpath(GlobalSearchUIConstants.searchResultsForCommunity), 5);
		List<WebElement> searchResults = findElements(By.xpath(GlobalSearchUIConstants.searchResultsForCommunity));
		for(WebElement community : searchResults)
		{
			if(!community.getText().contains(communityName))
			{
				flag=false;
				break;
			}
		}
		return flag;
	}
	/**
	 * Method to check if all results contain searched People
	 * @param peopleName - user Name to be check
	 * @return Boolean flag - true if all results contains searched people
	 */
	public Boolean isResultListMatchesWithSearchedPeople(String peopleName)
	{
		Boolean flag=true;
		waitForElementsVisibleWd(By.xpath(GlobalSearchUIConstants.searchResultsForPeople), 5);
		List<WebElement> searchResults = findElements(By.xpath(GlobalSearchUIConstants.searchResultsForPeople));
		for(WebElement people : searchResults){
			if(!people.getText().contains(peopleName))
			{
				flag=false;
				log.info(people.getText() + " does not contains "+ peopleName );
				break;
			}
		}
		return flag;
	}
	
	/**
	 * Method to check if searched people name is  display in search result list
	 * @param peopleName - user Name to be check
	 * @return Boolean flag - true if exact match with people found
	 */
	public Boolean isSearchPeopleDisplayInResultList(String peopleName)
	{
		Boolean flag=false;
		waitForElementsVisibleWd(By.xpath(GlobalSearchUIConstants.searchResultsForPeople), 5);
		List<WebElement> searchResults = findElements(By.xpath(GlobalSearchUIConstants.searchResultsForPeople));
		for(WebElement people : searchResults){
			if(people.getText().equals(peopleName))
			{
				flag=true;
				break;
			}
		}
		return flag;
	}
	
	/**
	 * Method to check if results count is less than or equals to the Per page value
	 * @param perPageValue - Per page value to check
	 * @return Boolean flag - true if count is less or equals to the per page value
	 */
	public Boolean isCountOfResultsLessThanOrEqualsToPerPageValueForSearchPeople(int perPageValue)
	{
		Boolean flag=false;
		waitForPageLoaded(driver);
		List<WebElement> searchResults = findElements(By.xpath(GlobalSearchUIConstants.searchResultsForPeople));
		if(searchResults.size() <= perPageValue){
			flag=true;
		}
		return flag;
	}
	
	/**
     * Click on specific item under Recently visited section of global Search dropdown
     * @param itemToBeSearch - item to be searched under Recently visited section of Global search dropdown
     * @return Boolean flag - return true of User is displayed.
     */
	public void clickItemUnderRecentlyVisited(String itemToBeSearch)
	{
		Boolean flag = false;
		List<WebElement> recentlyVisitedItems = waitForElementsVisibleWd(By.xpath(GlobalSearchUIConstants.recentlyVisitedItemsInSearchDropdown),8);

		for(WebElement recentlyVisitedItem : recentlyVisitedItems )
		{
			if(recentlyVisitedItem.getText().contains(itemToBeSearch))
			{
				scrollToElementWithJavaScriptWd(recentlyVisitedItem);
				mouseHoverAndClickWd(recentlyVisitedItem);
				flag=true;
				break;
			}
		}
		if(flag.equals(false))
		{
			log.warn("WARNING: Utem to be searched is not displayed in Recently visited section of Global Search dropdown");
		}
	}
	
	/**
     * Click on specific item under Recently visited section of global Search dropdown
     * @param itemToBeSearch - item to be searched under Recently visited section of Global search dropdown
     */
	public void clickItemUnderRecentlySearch(String itemToBeSearch)
	{
		WebElement recentlySearchedItem = waitForElementVisibleWd(By.xpath(GlobalSearchUIConstants.recentSearchInSearchDropdown
				.replace("PLACEHOLDER", itemToBeSearch)),8);
		scrollToElementWithJavaScriptWd(recentlySearchedItem);
		clickLinkWd(recentlySearchedItem, "click on recentlySearchedItem");
	}
	
	
	/**
	 * Method to check if all results contain searched People
	 * @param peopleName - user Name to be check
	 * @return Boolean flag - true if all results contains searched people
	 */
	public Boolean isResultsContainSearchedPeople(String peopleName)
	{
		Boolean flag=false;
		waitForElementsVisibleWd(By.xpath(GlobalSearchUIConstants.searchResultsForPeople), 5);
		List<WebElement> searchResults = findElements(By.xpath(GlobalSearchUIConstants.searchResultsForPeople));
		for(WebElement people : searchResults){
			if(people.getText().equals(peopleName))
			{
				flag=true;
				log.info(people.getText() + " does not contains "+ peopleName );
				break;
			}
		}
		return flag;
	}
	
	/**
     * Method to check if all results contain current date
     * @param currentdate - date to check
     * @return Boolean flag - true if all results contains current date
     */
	public Boolean isSearchListContainsCurrentDate(String currentdate)
	{
		Boolean flag=true;
		waitForElementsVisibleWd(By.xpath(GlobalSearchUIConstants.searchResults_date), 5);
		List<WebElement> searchResults = findElements(By.xpath(GlobalSearchUIConstants.searchResults_date));
		for(WebElement date : searchResults)
		{
			log.info("INFO: Checking if search result creation date: " + date.getText() + " contains current date: " + currentdate);
			if(!date.getText().contains(currentdate))
			{
				flag=false;
				break;
			}
		}
		return flag;
	}
	
	/**
     * Method to select different option in Dates filters
     * @param dateOptions - different options to select
     * @param string - Message to display when performing click action
     */
	public void selectDatesFilterOption(String dateOptions, String string) {
		log.info("Click on Dates filter");
		waitForElementVisibleWd(By.cssSelector(GlobalSearchUIConstants.dateOptions), 8);
		clickLinkWaitWd(By.cssSelector(GlobalSearchUIConstants.dateOptions), 7, "Dates Dropdown");	
		waitForElementVisibleWd(By.cssSelector(dateOptions), 7);
		log.info("Select:" + string);
		clickLinkWaitWd(By.cssSelector(dateOptions), 7, string);
		waitForElementInvisibleWd(By.cssSelector(GlobalSearchUIConstants.progressBar), 7);		
	}
}
