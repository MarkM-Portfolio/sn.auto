package com.ibm.conn.auto.webui.constants;

public final class GlobalSearchUIConstants {
	
	public static String searchTextBox = "input[aria-label='search']";
	public static String searchButton = "span[class='MuiIconButton-label']>svg[data-mui-test='searchIcon']";
	public static String filterDropdownButton = "span[class='MuiIconButton-label']>svg[data-mui-test='filterIcon']";
	public static String allContentFromFilterDropdown = "//ul[@class='MuiList-root MuiList-padding']/li[contains(text(),'All content')]";
	public static String communitiesFromFilterDropdown = "//ul[@class='MuiList-root MuiList-padding']/li[contains(text(),'Communities')]";
	public static String peopleFromFilterDropdown = "//ul[@class='MuiList-root MuiList-padding']/li[contains(text(),'People')]";
	public static String peopleFilterButton = "//span[@class='MuiChip-label'][contains(text(),'People')]";
	public static String fileFromFilterDropdown = "//ul[@class='MuiList-root MuiList-padding']/li[contains(text(),'File')]";
	public static String optionsInFilterDropdown = "//ul[@class='MuiList-root MuiList-padding']/";
	public static String searchedSuggestionList ="//div[@class='MuiListItemText-root MuiListItemText-dense']//span[contains(text(),'in')]";
	public static String allContentInSearchSuggestion ="//div[@id='primary-label-All Content']//span[text()='PLACEHOLDER - in All Content']";
	public static String communitiesInSearchSuggestion ="//div[@id='primary-label-Communities']//span[text()='PLACEHOLDER - in Communities']";
	public static String customizedInFriendsSuggestion = "//span[text()='PLACEHOLDER - in Friends']";
	public static String customizedInAllConnectionSuggestion = "//span[text()='PLACEHOLDER - in All Connection']";

	public static String peopleInSearchSuggestion ="//div[@id='primary-label-People']//span[text()='PLACEHOLDER - in People']";
	public static String fileInSearchSuggestion ="//div[@id='primary-label-Files']//span[text()='PLACEHOLDER - in Files']";
	public static String communitiesTypographyInSearchSuggestion = "//div[text()='Communities']/following-sibling::ul[1]/a"; //"//div[text()='Communities']/../ul[2]//div[@class='MuiListItemText-root MuiListItemText-dense']/span";
	public static String peopleTypographyInSearchSuggestion = "//div[text()='People']/following::ul[1]//div/span";
	public static String advancePeopleSearchInSearchSuggestion ="div[id='fullsearch-label']";
	public static String peopleDisplayNameListUnderPeopleTypography ="//ul[@class='MuiList-root MuiList-dense MuiList-padding'][2]//div[@class='MuiListItemText-root MuiListItemText-dense']/span";
	public static String recentSearchInSearchDropdown = "//div[text()='Recent Searches']/following::ul[1]/a/div/span/span[contains(text(),'PLACEHOLDER')]";
	public static String recentSearchViewAllLinkInSearchDropdown = "//a[@id='searchRedirectViewAll']";
	public static String recentVisitedViewAllLinkInSearchDropdown = "//a[@id='visitedRedirectViewAll']";
	public static String recentSearchBackButtonInDropdown = "//div[text()='Recent Searches']//../button[@type='button']";
	public static String recentlyVisitedBackButtonInDropdown = "//div[text()='Recently visited']//../button[@type='button']";

	
	// Search Result Page Filters
	public static String noSearchResultFound = "//div[text()='No search result found for given search term']";
	public static String communitiesFilterButton = "//span[@class='MuiChip-label'][contains(text(),'Communities')]";
	public static String filterByButtonInSearchResult = "div[id='filter']";
	public static String filterByDropdownIcon = "div[id='filter']~svg";
	public static String filterByMyContentInSearchResult = "//ul[@aria-labelledby='filter']/li[@data-value='personalOnly']";
	public static String paginationBarInSearchResult = "nav[aria-label='pagination navigation']";
	public static String resultPerPageFilterInSearchResult = "div[id='perPage']";
	public static String perPageValue_100 = "ul[aria-labelledby='perPage']>li[data-value='100']";
	public static String perPageValue_25 = "ul[aria-labelledby='perPage']>li[data-value='25']";
	public static String footerAboutLink = "//a[contains(text(),'About')]";
	public static String resetAllFilterButtonInSearchResult = "//p[contains(text(),'Reset All Filters')]";
	public static String selectedFilterCounts = "div[class$='MuiGrid-grid-xs-10'] [aria-selected='true']";
	public static String blogsFilterButton = "//span[@class='MuiChip-label'][contains(text(),'Blogs')]";
	public static String forumFilterButton = "//span[@class='MuiChip-label'][contains(text(),'Forum')]";
	public static String sortByButtonInSearchResult = "div[id='sort']";
	public static String sortByDropdownIcon = "div[id='sort']~svg";
	public static String sortByDateInSearchResult = "ul[aria-labelledby='sort']>li[data-value='date']";
	public static String pagination_lastPage = "button[aria-label='Go to last page']";
	public static String pagination_firstPage = "button[aria-label='Go to first page']";
	public static String pagination_selectedNumber = "button[class$='Mui-selected']";
	public static String pagination_elementCount = "ul[class='MuiPagination-ul']>li";
	public static String pagination_thirdLastNum = "//ul[@class='MuiPagination-ul']/li[PLACEHOLDER]";
	public static String pagination_nextPage = "button[aria-label='Go to next page']";
	public static String pagination_previousPage = "button[aria-label='Go to previous page']";
	public static String tags_listLink = "//button[contains(text(),'List')]";
	public static String tags_cloudLink = "//button[contains(text(),'Cloud')]";
	public static String tags_listView = "//label[contains(text(),'Tags')]/ancestor::label/following-sibling::div//div[contains(@class,'MuiChip-outlined MuiChip-clickable')]";
	public static String tags_cloudView = "//label[contains(text(),'Tags')]/ancestor::label/following-sibling::div//div[contains(@class,'MuiChip-outlined MuiChip-sizeSmall MuiChip-clickable')]";
	public static String progressBar = "svg[class='MuiCircularProgress-svg']";
	public static String tagLink = "//label[contains(text(),'Tags')]/ancestor::label/following-sibling::div//span[contains(text(),'PLACEHOLDER')]";
	public static String selectedFilters = "div[class$='MuiGrid-grid-xs-10'] [aria-selected='true'] span[class='MuiChip-label']";
	public static String selectedTags = "//label[contains(text(),'Tags')]/ancestor::label/following-sibling::div//div[@aria-selected='true']//span[@class='MuiChip-label']";
	public static String allContentFilterButton = "//span[@class='MuiChip-label'][contains(text(),'All Content')]";
	public static String clearSelectionBtn = "//p[contains(text(),'Clear Selection')]";
	public static String bizCard = "#cardTable";
	public static String bizCardTitle = "#cardTable span[class='fn bidiAware']";
	public static String dateOptions = "div[id='dateOptions']";
	public static String dateOptions_dateRange = "li[data-value='range']";
	public static String dateOptions_last7Days = "li[data-value='7days']";
	public static String dateOptions_last30Days = "li[data-value='30days']";
	public static String dateRange_fromMonth = "#fromMonthText";
	public static String dateRange_toMonth = "#toMonthText";
	public static String dateRange_fromYear = "#fromYearText";
	public static String dateRange_toYear = "#toYearText";
	public static String errorMsg = "//div[contains(text(),'Date Range')]/../../p";
	public static String searchResults_date = "//li[@class='MuiListItem-root MuiListItem-gutters']/div[2]/span/p[1]";
	public static String selectedFilter_oldSearchPage = "a[title^='Selected. Show results']";
	public static String peopleResults_oldSearchPage = "//a[@class='icSearchMainAction fn lotusPerson bidiAware hasHover']";
	public static String communitiesFilter_oldSearchPage = "//a[@title='Show results from all of Connections'][contains(text(),'Communities')]";
	public static String profileFilter_oldSearchPage = "//a[@title='Show results from all of Connections'][contains(text(),'Profiles')]";

	public static String communitiesResult_oldSearchPage = "a.icSearchMainAction";
	public static String pagination_elementCount_oldSearchPage = "ul[id='contentContainer_results_View_CenterPaging'] li";
	public static String pageNum_oldSearchPage = "//ul[@id='contentContainer_results_View_CenterPaging'] /li[PLACEHOLDER]";
	public static String loadingIcon_oldSearchPage = "//div[text()='Loading...']";
	public static String selectedPage_oldSearchPage = "//ul[@id='contentContainer_results_View_CenterPaging'] /li[starts-with(@class,'icSelected')]";
	public static String rankMyContentHigherCheckbox = "input[id='chkMyContent']";
	
	// Recently Visited
	public static String recentlyVisitedItemsInSearchDropdown = "//div[text()='Recently visited']/following::ul[1]//div[@class='MuiListItemText-root MuiListItemText-dense MuiListItemText-multiline']/span";
	public static String recentlyVisitedTextInSearchDropdown = "//div[text()='Recently visited']";
	public static String searchResultsForCommunity = "//li[@class='MuiListItem-root MuiListItem-gutters']/div[2]/span/p[2]";
	public static String searchResultForCommunity = "//label[contains(text(),'entry')]/../../../p[2]/a";
	public static String searchResultsForPeople = "//li[@class='MuiListItem-root MuiListItem-gutters']/div[2]/span/p[1]/a";
	public static String searchResultForPeople = "//li[@class='MuiListItem-root MuiListItem-gutters']/div[2]/span/p[1]/a/b[text()='PLACEHOLDER1']/following::b[text()='PLACEHOLDER2']/..";
	public static String searchResultsForFiles = "//li[@class='MuiListItem-root MuiListItem-gutters']/div[2]/span/p[2]/a";
	public static String tags = "//label[text()='Tags']";
	public static String peopleSlider ="//span[@role='slider']";
	public static String peopleFilters ="//span[@id='peopleSlider']/parent::div//ul//li";
	
	public static String closeIconOnSearchResult = "//button[starts-with(@class, 'MuiButtonBase-root MuiIconButton-root') and @aria-label='close']";

	private GlobalSearchUIConstants() {}

}
