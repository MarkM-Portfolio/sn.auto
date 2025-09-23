package com.ibm.conn.auto.webui.constants;

public final class TouchpointUIConstants {


	// Welcome screen
	public static String welcomeMessage = "css=#content-welcome #welcome-content-home header>p";
	public static String createYourProfile = "css=#content-welcome #welcome-content-home>div div:nth(1)>p:nth(0)";
	public static String followPeopleAndCommunities = "css=#content-welcome #welcome-content-home>div div:nth(1)>p:nth(1)";
	public static String welcomeScreenText = "css=#content-welcome #welcome-content-home div:nth(1)>p";
	public static String buttonLetsGo = "css=#navigation-wrapper #button-start";
	public static String informationMsg = "In a few moments, you'll be empowered to collaborate on documents, brainstorm with your team, manage tasks, build a knowledge base, and share updates on what you and your team are working on. Together let's:";
	public static String step1Text = "Step 1: Create your Profile";
	public static String step2Text = "Step 2: Follow People and Communities";
	public static String acceptPolicyLink = "css=div#acceptTC";
	public static String acceptPolicyCheckBox = "css=input[type= 'checkbox' ]";
	public static String networkExtral = "css=div[id='networkExternal']";
	// Update your profile screen
	public static String updateProfilePageHeader = "css=#content-editProfile h1:contains('Update Your Profile')";
	public static String UpdateProfilePageSecondHeader = "css=#content-editProfile h2";
	public static String ProfileDefaultPicture = "css=#profilePhoto-upload #profilePhoto-imgCircle";
	public static String ProfilePicture = "css=#profilePhoto-imgCircle #profilePhoto-input";
	public static String ProfilePicSaveBtn = "css=#save_thumb";
	public static String CropDialogBoxHeader = "xpath=//h2[contains(text(),'Crop your profile photo')]";
	public static String CropElement = "css=body:nth-child(2) div:nth-child(7) > div.imgareaselect-border4";
	public static String displayedUserName = "xpath=//input[@id='userName']";
	public static String defaultJobtitle = "xpath=//input[@id='jobTitle']";
	public static String editjobTitle = "xpath=//img[@id='editJobTitle']";
	public static String workPhoneNum = "xpath=//input[@name='telephoneNumber']";
	public static String mobilePhoneNum = "css=input[name='mobileNumber']";
	public static String nextBtnUpdateMyProfile = "xpath=//span[contains(text(),'Next')]";
	// Add your interests screen
	public static String addYourInterestPageHeader = "css=#content-profileTags h1:contains('Add Your Interests')";
	public static String nextButton = "css=#navigation-wrapper div[class^='navigation-next']>span:contains('Next')";
	public static String backButton = "css=#navigation-wrapper div[class^='navigation-back']>span:contains('Back')";
	public static String searchBox = "css=#tagTypeahead #tagTypeahead-input";
	public static String searchTypeaheadResult = "css=#tagTypeahead div[class='tagTypeahead-option']";
	public static String myInterestSection = "css=#tags-collection-wrapper #tags-collection";
	public static String tagsInMyInterest = "css=#tags-collection-wrapper #tags-collection>li";
	public static String suggestedInterestSection = "css=#content-profileTags #tags-from-colleagues";
	public static String visibleScreen =  "css=#content-wrapper div[style='display: block;']";
	public static String suggestedInterestsText = "css=#content-profileTags #tags-from-colleagues-heading:contains('Suggested Interests')";
	public static String getFirstSuggestedInterest = "css=#tags-collection-wrapper #tags-from-colleagues li span:nth(0)";
	public static String getVisibleSuggestedInterests = "css=#tags-collection-wrapper #tags-from-colleagues li img";
	// Follow your colleagues screen
	public static String followColleaguesPageHeader = "css=#content-findColleagues h1:contains('Follow Colleagues')";
	public static String followedPeopleCounter = "css=#followedExperts-counter";
	public static String followColleaguesPageHeader2 = "css=#content-findColleagues h2:contains('Select people relevant to you')";
	public static String suggestionText = "css=#content-findColleagues div[class='sidebar-info'] div:contains('We've added some suggestions ')";
	public static String youCanAddorRemoveText = "css=#content-findColleagues div[class='sidebar-info'] div:contains('You can add or remove people at anytime.')";
	public static String searchForColleagues = "css=#nameTypeahead #nameTypeahead-input";
	public static String colleaguesYouFollow = "css=#content-findColleagues div[class='sidebar-info'] div>h3";
	public static String suggestedpeolpeToFollow = "css=#profile-colleagues #recommended-colleagues li";
	public static String suggestedPeolpeName = "css=#profile-colleagues #recommended-colleagues li div>span[class='colleague-line1']";
	public static String nameInSearchResults = "css=#profile-colleagues #nameTypeahead-result li div>span[class='colleague-line1']";
	public static String getSuggestedResults = "//div[@id='profile-colleagues']//ul[@id='recommended-colleagues']";
	public static String getSearchedResults = "//div[@id='profile-colleagues']//ul[@id='nameTypeahead-result']";
	public static String getPeopleAvailableToFollow = "//li//span[text()='Follow']//ancestor::div[contains(@class,'follow-button')]//preceding-sibling::div/span[@class='colleague-line1']";
	// Follow Community Screen
	public static String followCommunityPageHeader = "css=#content-followCommunities h1:contains('Follow Communities')";
	public static String followCommunitiesPageHeader2 = "css=#content-followCommunities h2:contains('Your colleagues use communities to share ideas the')";
	public static String searchForCommunities = "css=#communityTypeahead-input";
	public static String suggestionTextfolCom1 = "css=#content-followCommunities div[class='sidebar-info'] div:contains('Follow suggested Communities, or search for ones t')";
	public static String suggestionTextfolCom2 = "css=#content-followCommunities div[class='sidebar-info'] div:contains('You can add or remove Communities at anytime.')";
	public static String communitiesYouFollow = "css=#content-followCommunities div[class='sidebar-info'] div>h3";
	public static String communitiesFollowers = "css=#communities-counter";
	public static String suggestedcommunitiesToFollow = "css=#content-followCommunities";
	public static String suggestedCommunitiesName = "css=#communityTypeahead-result";
	public static String followedCommunitiesCounter = "css=#communities-counter";
	public static String getCommunitySearchedResults = "css=#communities-collection #communityTypeahead-result li";
	public static String communityInSearchResults = "css=#communities-collection #communityTypeahead-result li div>span[class='colleague-line1']";
	public static String getAvailableCommunityToFollow = "//div[@id='content-followCommunities']//ul[@id='communityTypeahead-result']//li//span[text()='Follow']//ancestor::div[contains(@class,'follow-button')]//preceding-sibling::div/span[@class='colleague-line1']";
	public static String doneButton = "css=#navigation-wrapper div[class^='navigation-submit']>span:contains('Done')";
	public static String messageBox = "xpath=//p[contains(text(),'We're preparing your Connections Experience.')]";
	public static String searchedCommunityView = "//div[@id='content-followCommunities']//ul[@id='communityTypeahead-result']";
	public static String updatedMessage = "//div[@id='content-followCommunities']//ul[@id='communityTypeahead-result']//li//div/span[@class='colleague-line2']";

	private TouchpointUIConstants() {}

}
