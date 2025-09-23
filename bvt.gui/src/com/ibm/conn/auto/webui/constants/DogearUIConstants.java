package com.ibm.conn.auto.webui.constants;

public final class DogearUIConstants {


	/** Bookmark Nav option selectors */
	public static final String Nav_MyBookmarks = "css=a[title='My saved bookmarks'] span";
	public static final String Nav_PublicBookmarks = "//a[@title='Public bookmarks, most recent first']//span[text()='Public Bookmarks']";
	public static final String Nav_Popular = "css=a[title='The most actively bookmarked URLs'] span:contains(Popular)";
	public static final String Nav_MyUpdates = "css=a[title='Collections of bookmarks in My Watchlist and notified about.'] span:contains(My Updates)";
	public static final String HeaderTextHolder = "css=#headerTextHolder";
	public static final String Bookmarks_Main_Content = "css=div#bookmarksMainContent";
	public static final String Nav_BookmarksHome_ExpandDetails_Bookmark1 = "css=a#showMore_1";
	public static final String Nav_BookmarksHome_HideDetails_Bookmark1 = "css=a#hideMore_1";
	public static final String Nav_HowToBookmark = "css=a:contains('Bookmarking Tools')";
	public static final String Bookmarklet_Bookmarklet1 = "css=a[title='Add Bookmark']";
	public static final String Bookmarklet_Bookmarklet2 = "css=a[title='Discuss This']";
	public static final String Bookmark_Link = "css=a[id^=bmlink]";
	/** Add a Bookmark selectors */
	public static final String AddBookmark = "css=#addBookmarkBtn";
	public static final String AddABookmark = "css=a:contains('Add a Bookmark')";
	public static final String Form_AddBookmark_Title = "css=input#title";
	public static final String Form_EditBookmark_Title = "css=tbody tr td input[name='name']";
	public static final String Form_AddBookmark_Url = "css=input#urlField";
	public static final String Form_AddBookmark_Tags = "css=input#ptags";
	public static final String Form_AddBookmark_Description = "css=textarea#rteDiv";
	public static final String Form_AddBookmark_Radio_Public = "css=input#publicDogear";
	public static final String Form_AddBookmark_Radio_Private = "css=input#privateDogear";
	public static final String Form_AddBookmark_AddToActivity = "css=select[id=activityUuid]";
	public static final String Form_AddBookmark_AddToCommunity = "css=select[id=communityUuid]";
	public static final String Form_AddBookmark_AddToBlog = "css=select[id=blogUuid]";
	public static final String BookmarksList_DetailsView = "css=a[id='bkdetailsLink']";
	public static final String BookmarksList_ListView = "css=a[id='bkListLink']";
	public static final String BookmarkletCommunityArea = "css=textarea#rteDivForCommunities";
	public static final String BookmarkletActivityArea = "css=textarea#rteDivForActivities";
	public static final String BookmarkletBlogArea = "css=textarea#rteDivForBlogs";
	public static final String ImportantBookmarkCheckbox = "css=input#comm_importantBookmark";
	public static final String PrivateActivityBookmarkCheckbox = "css=input#activity_private";
	public static final String CommunityList = "css=select#communityUuid option";
	public static final String ActivityList = "css=select#activityUuid option";
	public static final String BlogList = "css=select#blogUuid option";
	public static final String CommunityWidget = "css=select#communityUuid";
	public static final String ActivityWidget = "css=select#activityUuid";
	public static final String BlogWidget = "css=select#blogUuid";
	/** My Bookmarks (tab) view selectors */
	public static final String MyBookmarks_MyWatchList = "css=#subsAsynchLink";
	public static final String MyBookmarks_AddToWatchlist = "css=a#subscribe2";
	public static final String MyBookmarks_RemoveFromWatchlist = "css=a#subscribe1";
	public static final String MyBookmarks_UsersWatchlistedMeRegion = "css=div[role='region'][aria-label='Users that Watchlisted Me']";
	public static final String MyBookmarks_MyWatchListRegion = "css=div[role='region'][aria-label='My Watchlist']";
	public static final String MyBookmarks_DeleteBookmark1 = "css=input#t1.lotusCheckBox";
	public static final String MyBookmarks_Delete = "css=a:contains('Delete Selected')";
	public static final String ConfirmBookmarkDelete = "css=input.lotusFormButton.submit";
	public static final String MyBookmarks_SelectAll = "xpath=//a[contains(text(), 'Select All')]";
	public static final String MyBookmarks_DeselectAll = "xpath=//a[contains(text(), 'Deselect All')]";
	public static final String MyBookmarks_NoBookmarkSelectedMsg = "css=div[id='dialogContent']";
	public static final String MyBookmarks_NoBookmarkSelectedWhileDeleting = "css=div[class='lotusDialogContent'][role='presentation']";
	public static final String MyBookmarks_CloseNoBookmarkMsg = "css=input[value='OK'][type='button']";
	public static final String MyBookmarks_Notify = "xpath=//a[text()='Notify']";
	public static final String MyBookmarks_MoreActions = "css=a[id='moreactionsachor']";
	public static final String MyBookmarks_AddTags = "xpath=//td[contains(text(), 'Add Tag(s)')][1]/..";
	public static final String MyBookmarks_ReplaceTag = "xpath=//td[contains(text(), 'Replace Tag')][1]/..";
	public static final String MyBookmarks_MultiTags = "css=input[id='multiTags']";
	public static final String MyBookmarks_TagIsRequiredMsg = "css=div[id='dialogContent']";
	public static final String MyBookmarks_OldTagIsRequired = "xpath=//div[@id='dialogContent'][text()='Old Tag is required.']";
	public static final String MyBookmarks_OldTag = "xpath=//input[@id='multiOldTags']";
	public static final String MyBookmarks_NewTags = "xpath=//input[@id='multiNewTags']";
	public static final String MyBookmarks_NewTagsIsRequired = "xpath=//div[@id='dialogContent'][text()='New Tag or Tags is required.']";
	public static final String MyBookmarks_DeleteTags = "xpath=//td[contains(text(), 'Delete Tag(s)')][1]/..";
	public static final String MyBookmarks_DeleteTag = "css=input[id='delTags']";
	public static final String MyBookmarks_CloseAlertMsg = "css=input[value='OK'][type='button']";
	public static final String MyBookmarks_People_SearchLink = "css=a[id='peopleSearchLink']";
	public static final String MyBookmarks_People_SearchBox = "css=input[id='nameinput']";
	public static final String MyBookmarks_People_SearchBoxDropdown = "css=div[id='nameinput_dropdown'] li:nth-child(2)";
	public static final String SuccessMsgBox = "//div[@id='infoMessage1']/span";
	public static final String pagingBox = "//ul[@class='lotusLeft lotusInlinelist']/li[@class='lotusFirst'][contains(text(), 'of')]";
	public static final String AddtoCommunityLink = "css=tr:contains('Add to Community')";
	public static final String AddtoActivityLink = "css=tr:contains('Add to Activity')";
	public static final String AddtoBlogLink = "css=tr:contains('Add to Blog')";
	public static final String MoreActionsLink = "css=a:contains('More Actions')";
	/** My Updates (tab) view selectors */
	public static final String MyUpdates_MyAssociatedPeopleRegion = "css=div[id='dogearPeopleArea']";
	/** Mega Menu options */
	public static final String bookmarksOption = "css=a>strong:contains(Bookmarks)";
	public static final String bookmarksPopular = "css=td>a:contains(Popular)";
	public static final String bookmarksPublicBookmarks = "css=td>a:contains(Public Bookmarks)";
	/** Community bookmarks  */
	public static final String commAddFirstBookmark = "css=a:contains('Add Your First Bookmark')";
	public static final String commAddBookmark = "css=a:contains('Add a Bookmark')";
	public static final String commAddBookmark_Url = "css=textarea[id='addBookmarkUrl']";
	public static final String commAddBookmark_Title = "css=input[id='addBookmarkName']";
	public static final String commAddBookmark_Description = "css=textarea[id='addBookmarkDescription']";
	public static final String commAddBookmark_Tags = "css=input[id='autocompletetags2']";
	public static final String commAddBookmark_Save ="css=input[value='Save']";
	public static final String commAddBookmark_Cancel ="css=input[value='Cancel ']";
	public static final String leftNavBookmarks = "link=Bookmarks";
	public static final String EditBookmarkURL = "css=*[id^='editBookmarkUrl']";
	public static final String EditBookmarkName = "css=*[id^='editBookmarkName']";
	public static final String EditBookmarkDescription = "css=*[id^='editBookmarkDescription']";
	public static final String MoreLink = "//a[contains(text(),'More')]";
	public static final String First_More_Link = "css=a#showMore_1.lotusAction";
	public static final String Second_More_Link = "css=a#showMore_2.lotusAction";
	public static final String Third_More_Link = "css=a#showMore_3.lotusAction";
	public static final String EditLink = "css=tbody tr td a:contains('Edit')";
	public static final String EditSaveButton = "css=input[value='Save'][type='submit'][name='submit']";
	public static final String editBookmarkTitle = "css=input[id^='editBookmarkName']";
	public static final String editBookmarkURL = "css=textarea[id^='editBookmarkUrl']";
	public static final String editBookmarkTag = "css=input[id^='autocompletetags_edit']";
	public static final String editBookmarkDescription = "css=textarea[id^='editBookmarkDescription']";
	public static final String bookmarkLink = "css=a[id^='b_uri_']";
	public static final String SubmitSaveButton = "css=input[id='submitBtn']";
	public static final String Community_Bookmark_More_Link_Unique = "css=table[class*='lotusTable'] > tbody > tr[id='b_summary_PLACEHOLDER'] > td[class*='lotusAlignRight'] > a[aria-label='More']";
	public static final String Community_Bookmark_Edit_Link_Unique = "css=table[class*='lotusTable'] > tbody > tr[id='b_details_PLACEHOLDER'] > td > div > ul > li[class='lotusFirst'] > a:contains('Edit')";
	public static final String MyBookMarksTab = "//a[@title='My saved bookmarks']/../..";
	public static final String publicBookmarkCloudLink = "div[class=''] li[class='lotusFirst']>span";
	public static final String publicBookmarkListLink = "div[class=''] li>a[aria-controls='lconnTagWidget_tagListView']";
	public static final String publicBookmarkCloudViewTags = "div[id='lconnTagWidget_tagCloudView'] a";
	public static final String publicBookmarkListViewTags = "div[id='lconnTagWidget_tagListView'] a";
	public static final String publicBookmark_DetailsViewLink =  "table[class='lotusViewControl lotusRight'] span[class='bookmark_details_view']";
	public static final String publicBookmark_ListViewLink =  "table[class='lotusViewControl lotusRight'] span[class='bookmark_list_view']";
	public static final String BookmarkDetailedDesc ="//div[contains(@id,'description')]";
	
	public static String pagination = "//div[contains(@class,'lotusPaging')]";
	public static String recordsOnPage = "//td[starts-with(@id,'icon')]/parent::tr";
	public static String nextPageIcon = "//a[@class='next']";
	public static String lastPageIcon = "//a[@class='page-last']";
	public static String prevPageIcon = "//a[@class='prev']";
	public static String firstPageIcon = "//a[@class='page-first']";
	public static String paginationEle = "//ul[@class='lotusInlinelist pagination']/li";
	public static String paginationLastEle = "//ul[@class='lotusInlinelist pagination']/li[PLACEHOLDER]";

	public static final String notifyOtherPeopleLink = "//a[contains(text(),'Notify Other People')]";
	public static final String notifyToProfileSearch = "//input[@id='notifyEmails']";
	public static final String notifyButton = "//div[@class='lotusDialogFooter']//input[@type='button' and @value= 'Notify']";
	public static final String notifyMessage = "//textarea[@id='notifyDesc']";
	public static String typeAheadFullSearch = "//div[@id='notifyEmails_popup_searchDir']";
	public static String successfullNotificationMsg = "//span[contains(text(),'Your notifications have been sent')]";
	public static String mostPopularTitle = "//span[@id='mbTitle']";
	public static String mostPopularTwistyIconOpen = "//a[@id='mbImage' and @class='lotusSprite lotusArrow lotusTwistyOpen']";
	public static String mostPopularTwistyIconClosed = "//a[@id='mbImage' and @class='lotusSprite lotusArrow lotusTwistyClosed']";
	public static String myWatchlistSecondNavAtMyUpdatesTab = "//a[contains(text(),'My Watchlist')]";
	public static String notificationReceivedSecondNavAtMyUpdatesTab = "//a[contains(text(),'Notifications Received')]";
	public static String notificationSentSecondNavAtMyUpdatesTab = "//a[contains(text(),'Notifications Sent')]";
	
	public static String markPrivateOptionFrompageActionDropdwon = "//td[@id='dijit_MenuItem_4_text']";
	public static String markPrivateMessageInfo = "//span[contains(text(),'Your selected bookmarks are now private.')]";
	public static String addedTag = "//a[@id='bm1_tag1']";
	
	private DogearUIConstants() {}
	
}
