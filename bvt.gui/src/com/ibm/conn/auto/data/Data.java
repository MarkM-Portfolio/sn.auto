package com.ibm.conn.auto.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.TestProperties;

public class Data {
	
	private static Data instance;
	public String file1;
	public String file2;
	public String file3;
	public String file4;
	public String file5;
	public String file6;
	public String file7;
	public TestConfigCustom cfg=TestConfigCustom.getInstance();
	
	public static Data getData() {
		if(instance == null) {
			instance = new Data();
		}
		return instance;
	}
	
	private Data() {
		// these files (including commented out ones) exist only as local resource
		file1 = "Desert.jpg";
		file2 = "Lighthouse.jpg";
		file3 = "Koala.jpg";
		file4 = "Test.mp4";
		file5 = "Hydrangeas.jpg";
		file6 = "Jellyfish.jpg";
		file7 = "Chrysanthemum.jpg";
	}

	// expandUserVars
	/**
	 * Big time ugly kludge to fill in strings in Data with details about the
	 * current user with substitutions in the style of Unix bash variables.
	 * The following substitutions are made:
	 * <dl>
	 * 	<dt>$EMAIL</dt><dd>the result of user.getEmail() for the user</dd>
	 * 	<dt>$PASSWORD</dt><dd>the result of user.getPassword() for the user</dd>
	 * 	<dt>$FIRSTNAME</dt><dd>the result of user.getFirstName() for the user</dd>
	 * 	<dt>$LASTNAME</dt><dd>the result of user.getLastName() for the user</dd>
	 * 	<dt>$DISPLAYNAME</dt><dd>the result of user.getDisplayName() for the user</dd>
	 * </dl>
	 * 
	 * @param source
	 * 			The source string to substitute variables in
	 * @param user
	 * 			The user whose details are used for the variables
	 * @return
	 * 			The source string with the variables substituted
	 * 
	 */
	
	public static String expandUserVars (String source, User user) {
		source = source.replace("$EMAIL", user.getEmail());
		source = source.replace("$PASSWORD", user.getPassword());
		source = source.replace("$FIRSTNAME", user.getFirstName());
		source = source.replace("$LASTNAME", user.getLastName());
		source = source.replace("$DISPLAYNAME", user.getDisplayName());
		return source;
	}
	
	private final Properties appProperties = TestConfigCustom.getInstance().getAppProperties();
	private final String WIDGETS_PROEPRTIES_PATH = "test_config/app/widgets.properties";
	private final String NODE_PROEPRTIES_PATH = "test_config/app/nodes.properties";
	private final Properties widgetProperties = TestProperties.getProperties(WIDGETS_PROEPRTIES_PATH);
	private final Properties nodeProperties = TestProperties.getProperties(NODE_PROEPRTIES_PATH);

	//URL's for connections components 
	public String ComponentActivities = appProperties.getProperty("test_component_activities");
	public String ComponentBlogs = appProperties.getProperty("test_component_blogs");
	public String ComponentCommunities = appProperties.getProperty("test_component_communities");
	public String ComponentTouchpoint = appProperties.getProperty("test_component_touchpoint");
	public String ComponentDogear = appProperties.getProperty("test_component_dogear");
	public String ComponentFiles = appProperties.getProperty("test_component_files");
	public String ComponentForums = appProperties.getProperty("test_component_forums");
	public String ComponentHomepage = appProperties.getProperty("test_component_homepage");
	public String ComponentIcec = appProperties.getProperty("test_component_icec");
	public String ComponentIcecAdmin = appProperties.getProperty("test_component_icec_admin");
	public String ComponentGlobalSearch = appProperties.getProperty("test_component_globalsearch");
	public String ComponentMobile = appProperties.getProperty("test_component_mobile");
	public String ComponentModeration = appProperties.getProperty("test_component_moderation");
	public String ComponentNews = appProperties.getProperty("test_component_news");
	public String ComponentProfiles = appProperties.getProperty("test_component_profiles");
	public String ComponentSettings = appProperties.getProperty("test_component_settings");
	public String ComponentSocialContacts = appProperties.getProperty("test_component_socialcontacts");
	public String ComponentNewSocialContact = appProperties.getProperty("test_component_newcontact");
	public String ComponentOrientMe = appProperties.getProperty("test_component_orientme");
	public String ComponentCustomizer = appProperties.getProperty("test_component_customizer");
	public String ComponentSocialSidebar = appProperties.getProperty("test_component_socialSidebar");
	
	public String ComponentWikis = appProperties.getProperty("test_component_wikis");
	public String ComponentMetrics = appProperties.getProperty("test_component_metrics");
	public String ComponentCRE = appProperties.getProperty("test_component_cre");
	public String CreateWikis = appProperties.getProperty("test_component_create_wiki");
	public String HomepageDiscover = appProperties.getProperty("homepage_discover_view");
	public String HomepageStatusUpdates = appProperties.getProperty("homepage_statusupdates_view");
	public String HomepageImFollowing = appProperties.getProperty("homepage_imfollowing_view");
	public String HomepageMentions = appProperties.getProperty("homepage_mentions_view");
	public String HomepageSaved = appProperties.getProperty("homepage_saved_view");
	public String HomepageActionRequired = appProperties.getProperty("homepage_actionrequired_view");
	public String HomepageMyNotifications = appProperties.getProperty("homepage_mynotifications_view");
	public String TailoredExperience_Admin = appProperties.getProperty("tailoredexperience_admin");
	// App Registry
	public final String appRegistry = appProperties.getProperty("appregistry");
	public final String addExtensionSuccess =  "'PLACEHOLDER' was successfully added.";
	public String specialCharacter = "Page 9 x!£$%+&()@_-y";
	public String specialCharacterForWiki = "Page 9 x!£$%#&()@_-y";
	
	public String publicCommunityURL = "communities/service/html/allcommunities";
	
	// URL for Smart Cloud - login scree
	public final String ComponentDashboard = appProperties.getProperty("test_component_dashboard");
	public final String createCommunity = "communities/service/html/communitycreate";
	public final String createActivity = "activities/service/html/mainpage#dashboard,myactivities";
	public final String scPeople = appProperties.getProperty("test_component_scPeople");
	public final String MailServer = appProperties.getProperty("test_mailbox");
	public final String MailDomain = appProperties.getProperty("test_maildomain");
	public final String ServerName = appProperties.getProperty("test_server_name");
	
	//Widgets 
	public final String onprem_widget_Array = widgetProperties.getProperty("onprem_widgets");
	public final String cloud_widget_Array = widgetProperties.getProperty("cloud_widgets");
	public final String prebvt_widget_Array = widgetProperties.getProperty("cloud_widgets");
	
	/** General Data */
	public String feedForTheseEntries = "Feed for these Entries";
	public String feedsForBlogEntries = "Feed for Blog Entries";
	public String feedsForIdeationBlogIdeas = "Feed for Ideation Blog Ideas";
	public String feedsForTheseEntries = "Feed for these entries";
	public String feedsForTheseActivities = "Feed for these activities";
	public String feedsForTheseBookmarks = "Feed for these Bookmarks";
	public String feedsForTheseCommunities = "Feed for these Communities";
	public String feedsForTheseSearchResults = "Feed for these Search Results";
	public String feedsForTheseIdeationBlogs = "Feed for these Ideation Blogs";
	public String feedsForTheseIdeationBlogsIdeas = "Feed for these Ideation Blogs Ideas";
	public String feedsForTheseTopics = "Feed for these topics";
	public String feedsForTheseForums = "Feed for these forums";
	public String feedForThisPage = "Feed for this page";
	public String postSuccessMessage = "The message was successfully posted.";
	public String EditedTestDescription = "This is a test description for the BVT testing - with additional text for editing";
	public String widgetinsidecommunity ="Test Widgets inside community";
	public String communitywidgettest = "Test Community widget"; 
	public String IBMKnowledgeCenter = "IBM Knowledge Center";
	public String imageSizeHelpIconTxt = "Large images shown at original size might cause viewers to scroll.";
	
	/** General form data */
	public String commonName = "Level 2 BVT test for ";
	public String commonTag = "BVTLevel2";
	public String commonHandle = "Handle";
	public String commonDescription = "This is a test description for the BVT testing";
	public String commonComment = "This is a test description for the BVT testing when adding comments";
	public String commonStatusUpdate = "This is a test status update for the BVT testing";
	public String commonLinkName = "Google Test";
	public String commonURL = "http://www.google.com";
	public String commonAddress = "commonAddress";
	public String editedData = "Edited_";
	
	
	/** Bookmark data for communities testing */
	public String BookmarkURL = "http://www.google.com";
	public String BookmarkName = "Google";
	public String BookmarkTag = "CommunityBookmarkTag";
	public String BookmarkDesc = "Bookmark description";
	public String EditBookmarkURL = "http://www.w3.ibm.com";
	public String EditBookmarkName = "IBM W3 page";
	public String EditBookmarkTag = "EditedCommunityBookmarkTag";
	public String ChangeBookmarkAppTitle = "BOOKMARKS - RENAMED";
	
	
	/** Forums data for communities testing */
	public String ForumTopicTitle = "Community BVT topic";
	public String ForumTopicTag = "CommunityForumTag";
	public String EditForumTopicTitle = "Topic Title EDITED";
	public String EditForumTopicContent = "Topic Content EDITED";
	public String ReplyToForumTopic = "Re: ";
	
	/** Messages	 */
	public String NoTopicMsg = "There are no topics to display.";
	public String accessDenied = "Access Denied";
	
	/** Feeds data for communities testing */
	public String FeedsURL = "communities/service/atom/communities/my";
	public String FeedsURL_API = "http://www.testfeedlink1.com/";
	public String EditedFeedsURL = "communities/service/atom/communities/all";
	public String FeedsTitle = "BVT Test Feed";
	public String EditedFeedsTitle = "BVT Test Feed Edited";
	public String FeedsTag = "CommunitiesFeedsTag";
	public String MultiFeedsTag = "news travel";
	public String MultiFeedsTag1 = "blog topstories";
	public String MultiFeedsTag2 = "sports nontravel";
	public String FeedSuccessMsg = "The feed has been added to your community.";
	public String DeleteFeedMsg = "Are you sure you want to delete this feed?";
	
	/** Homepage data */
	public String LeftNavHPActionRequiredText = "Action Required";
	public String ComponentHPActivities = "My Activities";
	public String ComponentHPPublActivities = "Public Activities";
	public String ComponentHPBlogs = "Blogs";
	public String ComponentHPBookmarks = "Bookmarks";
	public String ComponentHPBookmarksWatchlist = "Bookmarks - Watchlist for $DISPLAYNAME";
	public String ComponentHPBookmarksPopular = "Bookmarks - Popular";
	public String ComponentHPBookmarksRecent = "Bookmarks";
	public String ComponentHPCommunities = "My Communities";
	public String ComponentHPCommunitiesMy = "Communities I'm a Member";
	//public String ComponentHPCommunitiesPublic = "Public Communities"; NB - 12Aug2020 - UI Change observed during Extended BVT maintenance
	public String ComponentHPCommunitiesPublic = "My Organization Communities";
	//public String ComponentHPFiles = "My Files"; NB - 12Aug2020 - UI Change observed during Extended BVT maintenance
	public String ComponentHPFiles = "Welcome to Files";
	public String ComponentHPFilesShared = "Files Shared With Me";
	public String ComponentHPProfiles = "My Profile";
	public String ComponentHPProfilesNetwork = "My Network - Profiles";
	public String ComponentHPWikisLatest = "Public Wikis";
	public String ComponentHPWikisMy = "Wikis I Own";
	public String ComponentHPWikisPopular = "Public Wikis";
	public String ComponentHPHomepage = "Home";
	public String ComponentActivitiesKeyText = "Start an Activity";
	public String ComponentPublActivitiesKeyText = "Activities that have been shared with everyone";
	public String ComponentBlogsKeyText = "Start a Blog";
	public String ComponentBookmarksKeyText = "Add a Bookmark";
	public String ComponentBookmarksWatchlistKeyText = "Watchlist for $DISPLAYNAME";
	public String ComponentBookmarksPopularKeyText = "Most popular in the last 30 days";
	public String ComponentBookmarksRecentKeyText = "Add a Bookmark";
	//public String ComponentCommunitiesKeyText = "Start a Community"; NB - 12Aug2020 - Change in UI during Extended  BVT maintenance
	public String ComponentCommunitiesKeyText = "Create a Community";
	//public String ComponentCommunitiesMyKeyText = "Start a Community"; NB - 12Aug2020 - Change in UI during Extended  BVT maintenance
	public String ComponentCommunitiesMyKeyText = "Create a Community";
	//public String ComponentCommunitiesPublicKeyText = "Start a Community"; NB - 12Aug2020 - Change in UI during Extended  BVT maintenance
	public String ComponentCommunitiesPublicKeyText = "Create a Community";
	//public String ComponentFilesKeyText = "Files that you own"; NB - 12Aug2020 - Change in UI(popupBodayText) during Extended  BVT maintenance
	public String ComponentFilesKeyText = "Files shared with me";
	public String ComponentMyFilesKeyText = "Recently Visited";
	//public String ComponentFilesSharedKeyText = "Files that other people have shared with you"; NB - 12Aug2020 - Change in UI during Extended  BVT maintenance
	public String ComponentFilesSharedKeyText = "Files shared with me";
	public String ComponentProfilesKeyText = "My Profile";
	public String ComponentProfilesNetworkKeyText = "Network Contacts for $DISPLAYNAME";
	public String ComponentWikisLatestKeyText = "Start a Wiki";
	public String ComponentWikisMyKeyText = "Start a Wiki";
	public String ComponentWikisPopularKeyText = "Start a Wiki";
	public String ComponentHomepageKeyText = "My Notifications";
	public String HomepageHelp = "Home page Help";
	public String ActivitiesWidgetHelp = "Using the Activities app";
	public String MyActivitiesWidgetHelp= "Using the My Activities app";
	public String PublActivitiesWidgetHelp = "Using the Public Activities app";
	public String BlogsWidgetHelp = "Using the Blogs app";
	public String BookmarksWidgetHelp = "Using the Bookmarks app";
	public String MyBookmarksWidgetHelp = "Using the My Bookmarks app";
	public String BookmarksWidgetWatchlistHelp = "Using the My Watchlist app";
	public String BookmarksWidgetPopularHelp = "Using the Popular Bookmarks app";
	public String BookmarksWidgetRecentHelp = "Using the Recent Bookmarks app";
	public String CommunitiesWidgetHelp = "Using the Communities app";
	public String CommunitiesWidgetMyHelp = "Using the My Communities app";
	//public String CommunitiesWidgetPublicHelp = "Using the Public Communities app"; NB - 12Aug2020 - Change in UI during Extended  BVT maintenance
	public String CommunitiesWidgetPublicHelp = "Using the My Organization Communities app";
	public String FilesMyWidgetHelp = "Using the My Files app";
	public String FilesSharedWidgetHelp = "Using the Files Shared with Me app";
	public String ProfilesWidgetHelp = "Using the Profiles app";
	public String ProfilesWidgetMyHelp = "Using the My Profile app";
	public String ProfilesWidgetNetworkHelp = "Using the My Network app";
	public String WikisWidgetLatestHelp = "Using the Latest Wikis app";
	public String WikisWidgetMyHelp = "Using the My Wikis app";
	public String WikisWidgetPopularHelp = "Using the Popular Wikis app";
	public String HelpWindowTitle = "Using the";//Help System
	public String NewWidgetTitle = "BVT Open Social Widget";
	public String OpenSocialTitle = "BVT Test Gadget";
	public String OpenSocialContent = "Open Social gadget test text show up";
	public String newWidgetUrl = appProperties.getProperty("new_widget_url");
	public String AS_SearchToolTipText = "Open the search bar to Search the current view";
	public String AS_SearchShadowText = "Search this stream";
	public String IbmURL = "http://www.ibm.com";
	public String FacebookURL = "https://www.facebook.com";
	public String StumbleUponURL = "http://www.stumbleupon.com";
	public String UtvURL = "http://www.u.tv";
	public String Tv3URL = "http://www.tv3.ie";
	public String RteURL = "http://www.rte.ie";
	public String skyURL = "http://www.skysports.com";
	public String atlanticURL = "http://www.theatlantic.com";
	public String itvURL = "http://www.itv.com";
	public String bbcURL = "http://www.bbc.co.uk";
	public String ch4URL = "http://www.channel4.com";
	public String wsjURL = "http://uk.wsj.com/home-page";
	public String cnnURL = "http://edition.cnn.com";
	public String irTimesURL = "http://www.irishtimes.com";
	public String irIndURL = "http://www.independent.ie";
	public String guardianURL = "http://www.theguardian.com";
	public String utvIrlURL = "http://utv.ie";
	public String tg4URL = "http://www.tg4.ie";
	public String skyNewsURL = "http://news.sky.com";
	public String ebayIrlURL = "http://www.ebay.ie";
	public String ibmConnectionsVideo = "https://www.youtube.com/watch?v=OefC3prMuCE";
	public String ibmOneHundredYearsVideo = "https://www.youtube.com/watch?v=39jtNUGgmd4";
	public String ibmWatsonVideo = "https://www.youtube.com/watch?v=_Xcmh1LQB9I";
	public String ibmSmarterPlanetVideo = "https://www.youtube.com/watch?v=6oxO9ZHklBs";
	public String Chars1000 = "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890";
	public String Chars1001 = "98765432109876543210987654321098765432109876543210987654321098765432109876543210987654321098765432109876543210987654321098765432109876543210987654321098765432109876543210987654321098765432109876543210987654321098765432109876543210987654321098765432109876543210987654321098765432109876543210987654321098765432109876543210987654321098765432109876543210987654321098765432109876543210987654321098765432109876543210987654321098765432109876543210987654321098765432109876543210987654321098765432109876543210987654321098765432109876543210987654321098765432109876543210987654321098765432109876543210987654321098765432109876543210987654321098765432109876543210987654321098765432109876543210987654321098765432109876543210987654321098765432109876543210987654321098765432109876543210987654321098765432109876543210987654321098765432109876543210987654321098765432109876543210987654321098765432109876543210987654321098765432109876543210987654321098765432109876543210987654321098765432109876543210987654321098765432101";
	public String TooLongStatusMessage = "The message entered is too long to post. Change the text entered and try again";
	public String TooLongCommentMessage = "The comment is too long to be posted. Please amend the comment and try again.";
	public String FileMadePublicMessage = "Attaching the selected file with this message will make it public (visible to everyone).";
	public String FileMadePublicMessageSC = "Attaching the selected file with this message will make it visible to the entire organization.";
	public String NotificationCenter_MarkRead = "Mark read";
	public String NotificationCenter_MarkUnread = "Mark unread";
	public String htmlContent = "<script>alert('xss');</script>";
	public String tourWelcomePopupText = "Your home page is your command center";
	
	/** Activity Stream Data */
	public String LoadPeopleView = "homepage/web/updates/#discover/all";
	public String UpdateStatus = "This is the BVT Level 2 Status update message ";
	public String BlogNameForNewsBVT = "News Level 2 BVT ";
	public String BlogsAddressForNewsBVT = "NewsAddress";
	public String BlogsTagForNewsBVT = "BVTLevel2News";
	public String BlogsTimeZoneOptionForNewsBVT = "(GMT) Greenwich Mean Time : Dublin, Edinburgh, Lisbon, London";
	public String BlogsThemeOptionForNewsBVT = "Blog_with_Bookmarks";
	public String BlogsNewEntryTitleForNewsBVT = "Entry for Level 2 Test for news ";
	public String BlogsNewEntryTagForNewsBVT = "TestTagForNews";
	public String BlogsNewEntryEntryForNewsBVT = "This is a test entry for the level 2 blogs for the news bvt";
	public String BlogsCommentText = "This is the test for the comment to be added to the entry in blogs - part of the BVT level 2 test for news";
	public String Start_An_Activity_InputText_Name_Data = "BVT Level2 New Activity ";
	public String Start_Public_Activity_InputText_Name_Data = "BVT Level2 Public Activity ";
	public String Start_Private_Activity_InputText_Name_Data = "BVT Level2 Private Activity ";
	public String Start_An_Activity_InputText_Tags_Data = "BVT Level2 Automation New Activity ";
	public String Start_A_Entry_InputText_Title_Data = "BVT New Entry ";
	public String Start_A_Entry_InputText_Tags_Data = "entryTag";
	public String Public_ToDo_InputText_Title_Data = "BVT Level2 Public To-do ";
	public String Private_ToDo_InputText_Title_Data = "BVT Level2 Private To-do ";
	public String ToDo_InputText_Title_Data = "BVT Level2 Automated To-do";
	public String ToDo_InputText_Tags_Data = "todoTag";
	public String Section_InputText_Title_Data = "BVT Level2 Automated New Section";
	public String Start_An_Activity_InputText_Name_Data_For_Search = "Level 2 BVT Activity for Search";
	public String Start_An_Activity_InputText_Tags_Data_For_Search = "ActivitySearchTag";
	public String Start_An_Activity_Textarea_Description_Data_For_Search = "This is a test description field entry for the activity titled: Level 2 BVT Activity for Search ";
	public String FeedForEntries = "Feed for these entries";
	public String ImFollowingText = "View/Refresh updates for people and things you are following, and responses to your content";
	public String MyNotificationsText = "View/Refresh updates and comments related to your content and notifications you have received";
	public String ActionRequiredText = "View/Refresh items that require your response";
	public String SavedText = "View/Refresh the updates you have saved";
	public String RecommedationsText = "There are no current recommendations for you.";
	public String TagFollowedText = "was successfully added. You can now filter the feed with this tag.";
	public String RepostedUpdateMessage = "The update was successfully reposted to your followers.";
	public String ActivityEntryNotificationMessage = "Hi-  I thought you might be interested in this activity entry.";
	public String BlogEntryNotificationMessage = "Hi-  I thought you might be interested in this blog entry.";
	
	/** Wikis Data */
	public String PeerPageDescription = "This is the test description for a PEER wiki page";
	public String ChildPageDescription = "This is the test description for a CHILD wiki page";
	public String TagForWikiPages = "pagetag";
	public String New_Content_For_Public_Wiki = "New content for Public Wiki";
	public String New_Content_For_Public_Wiki_Peer = "New content for Public Wiki Peer";
	public String New_Content_For_Public_Wiki_Child = "New content for Public Wiki Child";
	public String New_Content_For_Private_Wiki_Peer = "New content for Private Wiki Peer";
	public String New_Content_For_Private_Wiki_Child = "New content for Private Wiki Child";
	public String Comment_For_Public_Wiki = "Comment 1 - Public Wiki";
	public String Tag_For_Public_Wiki = "publicwikitag";
	public String Community_For_wiki_tests = "Community for the wiki testing";
	public String New_Content_For_Private_Wiki = "New content for Private Wiki";
	public String New_Child_Page_For_Private_Wiki2 = "New_Child_for_Private_Wiki_on_CI_Box2";
	public String Comment_For_Private_Wiki = "Comment 1 - Private Wiki";
	public String TagForPrivateWiki = "privatewikitag";
	public String New_Editors_Peer_Page_For_Private_Wiki = "New_Editors_Peer_for_Private_Wiki_on_CI_Box";
	public String New_Content_For_Editors_Private_Wiki_Peer = "New content for Editors Private Wiki Peer";
	public String New_Editors_Child_Page_For_Private_Wiki = "New_Editors_Child_for_Private_Wiki_on_CI_Box";
	public String New_Content_For_Editors_Private_Wiki_Child = "New content for Editors Private Wiki Child";
	public String New_Editors_Child_Page_For_Private_Wiki2 = "New_Editors_Child_for_Private_Wiki_on_CI_Box2";
	public String New_Content_For_Editors_Private_Wiki = "New content for Editors Private Wiki";
	public String Comment_For_Editors_Private_Wiki = "Comment 1 - Editors Private Wiki";
	public String Tag_For_Editors_Private_Wiki = "editorsprivatewikitag";
	public String Comment_For_Readers_Private_Wiki = "Comment 1 - Readers Private Wiki";
	public String CI_Box_Private_Wiki2 = "BVT Level 2 Private Wiki 2";
	public String LDAP_User_Name = "ajones";
	public String LDAP_User_Password = "jones";
	public String Wiki_LDAP_Owner_Username = LDAP_User_Name + "77";
	public String Wiki_LDAP_Owner_Password = LDAP_User_Password + "77";
	public String Wiki_LDAP_Editor_Username = LDAP_User_Name + "88";
	public String Wiki_LDAP_Editor_Password = LDAP_User_Password + "88";
	public String Wiki_LDAP_Reader_Username = LDAP_User_Name + "99";
	public String Wiki_LDAP_Reader_Password = LDAP_User_Password + "99";
	public String New_Owners_Peer_Page_For_Private_Wiki = "New_Owners_Peer_for_Private_Wiki_on_CI_Box";
	public String New_Content_For_Owners_Private_Wiki_Peer = "New content for Owners Private Wiki Peer";
	public String New_Owners_Child_Page_For_Private_Wiki = "New_Owners_Child_for_Private_Wiki_on_CI_Box";
	public String New_Content_For_Owners_Private_Wiki_Child = "New content for Owners Private Wiki Child";
	public String New_Owners_Peer_Page_For_Private_Wiki2 = "New_Owners_Peer_for_Private_Wiki_on_CI_Box2";
	public String New_Content_For_Owners_Private_Wiki = "New content for Owners Private Wiki";
	public String Comment_For_Owners_Private_Wiki = "Comment 1 - Owners Private Wiki";
	public String Tag_For_Owners_Private_Wiki = "ownersprivatewikitag";
	public String Wikis_Owner = "I'm an Owner";
	public String Wikis_Editor = "I'm an Editor";
	public String Wikis_Reader = "I'm a Reader";
	public String Wikis_Following = "I'm Following";
	public String Wikis_Public = "Public Wikis";
	public String Expected_Like_Text = "You like this";
	public String DeletePriorVersionConfirmMsg = "Are you sure you want to delete all versions prior to ";
	public String VersionComparison = "Version Comparison";
	public String RestoreVersionMsg = "You are about to replace the current version of this page with version ";
	public String RestoreFromMsg = "Restored from version";
	public String EditedByUserNotExist = "No user was found. Try typing the name and selecting a user from the list provided.";
	public String NotCreatedMsg = "has not yet been created";
	public String NoPageMsg = "There are no pages in this wiki";
	public String RemoveTagmsg = "Are you sure you want to remove the tag ";
	public String CI_Box_Public_Wiki = "BVT Level 2 Public Wiki ";
	public String CI_Box_Private_Wiki = "BVT Level 2 Private Wiki ";
	public String New_Peer_Page_For_Public_Wiki = "New_Peer_for_Public_Wiki_on_CI_Box";
	public String New_Child_Page_For_Public_Wiki = "New_Child_for_Public_Wiki_on_CI_Box";
	public String Deletetag = "will_delete_this_tag";
	public String Searchtag = "use_this_tag_to_search_for";
	public String MaxPageName = "test to check how many charactors are allowed before you get the" +
	 							" message stating that you need to shorten the page title and if " +
	 							"you click the link it will shorten the page name for you and so I" +
	 							"would hope that this happens soon as I am runnin";
	
	public String specChar1 = "Page 1 <xyz>";
	public String specChar2 = "Page 2 [xyz]";
	public String specChar3 = "Page 3 x|y";
	public String specChar4 = "Page 4 x*y";
	public String specChar5 = "Page 5 x?y";
	public String specChar6 = "Page 6 x:y";
	public String specChar7 = "Page 7 a\\b";
	public String specChar8 = "Page 8 x//";
	public String WikiPage_Locking_Message = "This page stays locked while you are editing it.";

	/** Activities Data */
	public String ActivityEntry_NotificationSent_DialogBoxHeading = "Notify Message:";
	public String ActivityEntry_NotificationSent_DialogSuccessMessage = "The notification has been sent!";
	public String Activity_Name = "BVT Activity";
	public String Start_An_Activity_InputText_Name_Data_Level3 = "BVT Level3 New Activity ";
	public String Start_An_Activity_Section_Title = "Section";
	public String Start_An_Activity_ToDo_Title = "ToDo";
	public String Start_An_Activity_ToDo_Desc = "ToDoDesc";
	public String Start_An_Activity_ToDo_Tag1 = "ToDo1";
	public String Start_An_Activity_ToDo_Tag2 = "ToDo2";
	public String Start_An_Activity_ToDo_Tag3 = "ToDo3";
	public String Start_An_Activity_ToDo_Tags = Start_An_Activity_ToDo_Tag1 + " " + Start_An_Activity_ToDo_Tag2 + " " + Start_An_Activity_ToDo_Tag3 + " ";
	public String File_Name = "BVT Level 3 ";
	public String BVT_Level3_ToDo_InputText_Title_Data = "BVT Level3 Automated To-do";
	public String Start_An_Activity_With_Options = "BVT Level3 New Activity ";
	public String Start_An_Activity_With_Options_Two = "BVT Level3 New Activity Two";
	public String Start_An_Activity_Template = "BVT Level3 New Activity Template ";
	public String Start_An_Activity_Template_InputText_Tags_Data = "automation, level3, newactivity";
	public String PleaseCompleteForm = "You have a form that has not been saved. Discard your changes?";	
	public String Start_SpecialChar = "$1.00 Stocking Stuffers";
	public String Contains_SpecialChar = "Hello~World";
	public String Ends_SpecialChar = "The Dog #";
	public String All_SpecialChars = "~!@#$%^&*()/\\_+=-`{}|:<>?[];',.";
	public String Single_SpecialChar = "%";
	public String WelcomeText = "New to Activities?";
	public String EditActivity = "Edit Activity";
	public String CopyActivityEntry = "Copy this entry (and its responses) to another activity";
	public String AddRelatedActivity = "Add a shortcut to a related activity by selecting an item in this list.";
	public String ToDo_ChooseAPerson = "One or more activity members (50 max)";
	public String Activity_Followed_Success_Message = "You are following this activity.";
	public String Activity_Created_Message = "Activity PLACEHOLDER successfully created.";
	
	/** Blogs Data */
	public String BlogsAddress1 = "Test1Address";
	public String BlogsAddress2 = "Test2Address";
	public String BlogsTimeZoneOption = "(GMT) Greenwich Mean Time : Dublin, Edinburgh, Lisbon, London";
	public String BlogsThemeOption = "Blog_with_Bookmarks";
	public String BlogsNewEntryTitle = "Entry for Level 2 Test ";
	public String BlogsNewEntryTag = "TestTag";
	public String BlogsNewEntryEntry = "This is a test entry for the level 2 blogs";
	public String IdeaGraduatedMsg = "The idea has been marked as Graduated.";
	public String IdeaDuplicatedMsg = "The idea has been marked as duplicated.";
	public String SearchScopeEntries = "My Blog Entries";
	public String SearchScopeBlog = "This Blog";
	public String like_votePageTitle = "My Likes/Votes";
	public String BlogAddedTitle = "Blog added";
	public String BlogsWelcomeText ="New to Blogs?";
	public String BlogsSettingsChangesSaved = "Saved changes to blog settings";
	public String BlogEntry_NotificationSent_DialogSuccessMessage = "The notification has been sent.";
	public String followThisBlogMsg = "You are following this blog and will receive updates about it.";
	public String DragAndDropImagesToUploadMsg = "Drag and drop images from your desktop directly into this window to upload them.";
	public String ImageSizeMustNotExceedMsg = "* Image size must not exceed 1 MB";
	public String InsertImageWebURL = "https://img3.ibxk.com.br/2017/07/13/13160033479225.jpg?w=700";
	public String InsertImageInvalidWebURL = "http://abc:def/ghi";
	
    /** Dogear Data */
	public String PublicBookmarkTitle = "FVT Public Bookmark";
	public String PublicBookmarkTag = "PublicBookmarkTag";
	public String PublicBookmarkURL = "www.ibm.com";
	public String PrivateBookmarkTitle = "FVT Private Bookmark";
	public String PrivateBookmarkTag = "PrivateBookmarkTag";
	public String PrivateBookmarkURL = "www.google.com";
	public String NoBookmarks = "No Bookmarks";
	public String TagForMyBookmarks = "MyTag";
	public String DiscussThis_Firefox = "Drag the Discuss This button to your browser toolbar.";
	public String DiscussThis_IE1 = "1. Right-click the Discuss This button and choose Add to Favorites.";
	public String DiscussThis_IE2 = "2. Click Yes if you see a security alert.";
	public String DiscussThis_IE3 = "3. Choose Favorites -> Organize favorites and move the Discuss This link to the Links or Favorites Bar folder, depending on your browser version.";
	public String MultiplePublicBookmarksTitle1 = "Public Level 3 BVT test1";
	public String MultiplePublicBookmarksTitle2 = "Public Level 3 BVT test2";
	public String MultiplePublicBookmarksTitle3 = "Public Level 3 BVT test3";
	public String MultiplePublicBookmarksTag1 = "PublicBVTLevel3Bookmark1";
	public String MultiplePublicBookmarksTag2 = "PublicBVTLevel3Bookmark2";
	public String MultiplePublicBookmarksTag3 = "PublicBVTLevel3Bookmark3";
	public String MultiplePublicBookmarksUrl1 = "http://www.gmail.com";
	public String MultiplePublicBookmarksUrl2 = "http://www.yahoo.com";
	public String MultiplePublicBookmarksUrl3 = "http://www.bing.com";
	public String TagIsRequired = "Tag is required.";
	public String ReplaceSuccessMsg = "Your selected bookmarks tags have been updated.";
	public String partiallyReplaceSuccessMsg = "Some bookmarks don't have the tags to be replaced.";
	public String DeleteMsg = "Your selected bookmarks tags have been deleted.";
	public String NewTagIsRequired = "New Tag or Tags is required.";
	public String OldTagIsRequired = "Old Tag is required.";
	public String AddBookmarkDialogTitle = "Add Bookmark";
	
	/** Files Data */
	public String RootName = "Files";
	public String FolderName = "Level 2 BVT Folder";
	public String EditedFolderName = "BVT Folder New";
	public String FolderDescription = "This is a test folder for the level 2 BVT";
	public String SubFolderDescription = "This is a test Sub folder for the level 2 BVT";
	public String StopFollowingMessage = "You have stopped following this file";
	public String FollowingMessage = "You are now following this file";
	public String PinMessage = "was pinned successfully.";
	public String UnPinMessage = "was removed successfully.";
	public String NewUnPinMessage = "was removed from Pinned Files successfully.";
	public String LikeMessage = "You like this";
	public String DeleteFileMessage = " was moved to the trash.";
	public String AllFilesDeleted = "There are no files in your trash.";
	public String NoFoldersFound = "You have not created any folders.";
	public String NofilesFound = "You have not uploaded any files.";
	public String UploadMessage = "Successfully uploaded ";
	public String ShareMessage = " was successfully shared";
	public String MGUploadMessage = " uploaded successfully";
	public String MGUploadMessage1 = "Upload successful";
	public String CheckThatFileUploadExists = "Allow others to share these files";
	public String CheckThatMoveToTrashExists = "Are you sure you want to move these files to the trash?";
	public String MyFilesText = "Files that you own";
	public String FileListPrefix = "urn:lsid:ibm.com:td:";
	public String DownloadAllFiles = "Download All Files";
	public String DownloadAsCompressed = "Download as Compressed File";
	public String FolderAddSuccess = "%s was added to %s";
	public String CommFolderAddSuccess = "%s has been successfully added to %s";
	public String DragAndDropFilesToUpload = "Drag and drop files from your desktop directly into the browser to upload to Files";
	public String DuplicateFolderAddFailed = "There is already a folder named '%s' in this folder. Try another name.";
	public String CommFilesFullWidgetPageTitle = "All Community Files";
	public String editPropertiesDialogBoxTitle = "Edit Properties";
	public String editedFileName = "updatedFileName";
	public String jpgExtension = ".jpg";
	public String EditFilePropertyMsg = " was saved successfully.";
	public String foldersSharedWithMeOption = "Shared with Me";
	public String filesSharedWithMeOption = "Shared With Me";
	public String myFoldersOption = "My Folders";
   
	
	/**FiDO data */
	public String DescriptionTooLong = "The description is too long.";
	
	/**Gallery data */
	public String defaultGalleryWidgetText = "Galleries allow you to showcase files from a specific folder.";
	public String galleryIsNotSetupText = "Gallery showcases files in this community. This Gallery is not set up yet.";
	
	/** Media Gallery data */
	public String MediaGalleryViewCountOneUpload = "1-1 of 1";
	
	//Communities
	public String NewToCommunities_Heading = "New to Communities?";
	public String NewToCommunities_JoinCommHelpTitle = "Joining communities";
	public String NewToCommunities_AddForumsTopicHelpTitle = "Adding topics to a community forum";
	public String NewToCommunities_ShareResourcesHelpTitle = "Adding a bookmark from a web page to a community";
	public String NewToCommunities_CreateCommHelpTitle = "Creating communities";
	public String CommunityName = "BVT Community";
	public String NoSearchResult = "No results were found for that search";
	public String SearchIndexInfo = "Search index was last updated:  Today ";
	public String CommunitiesText = "Get together with people who share your interests.";
	public String noCommunitiesFollowed = "You are not following any communities.";
	public String inviteUserSuccess = "You have successfully invited the following people to this community";
	public String inviteRevokePrompt = "Are you sure you want to revoke the invitation for ";
	public String MembershipRequestSent = "Your membership request has been sent.";
	public String emptyColumnMessage = "Drag apps here.";
	public String deleteCommunityWarning = "Enter the information below to confirm you want to move this Community to the Trash";
	public String communitySearchResults = "Communities Search Results";
	public String searchAllConnectionsContentTab = "All Connections Content";
	public String WidgetHideMsg = "You can re-activate it at a later point simply by re-adding the application to your Community. All of the application content will be kept intact.";
	public String NoLongerMemberForCommunityMsg = "You have left this community and are no longer a member.";
	public String LastActiveOwnerMsgOnPrem = "The last active owner cannot leave a community.";
	public String LeftTheCommunitySuccessMsg = "You have successfully left the community. By leaving, you are no longer permitted to view that community or its resources.";
	public String FollowCommunityMsg = "You are following this community and will receive updates about community content.";
	public String StopFollowingCommunityMsg = "You have stopped following this community.";
	public String Restricted = "Restricted";
	public String Moderated = "Moderated"; 	
	public String LastActiveOwnerMsgOnSC = "The business owner must remain in the community.";
	public String makeThisASubcommOfMsg = "Make this a subcommunity of:";
	public String makeThisATopLevelCommMsg = "Make this a top level community";
	public String ImportMemberMsg = "You have successfully added the following members to this community: ";    
    public String moveCommSuccessMsgWithMemberChanges = "Community has been successfully moved. Please check membership of the community as it may have been adjusted by the move.";
	public String listedRestrictedCheckboxText = "Let anyone in my organization see this community's title, description, tags, and owners";
	public String listedRestrictedWarningMsg = "The Name, Description, Tags and Owners will be visible to people outside your community.";
	public String communityTestTag = "communityTestTag123";    
    public String businessOwnerPopupMsg = "Each community belongs to the organization of its business owner. The business owner must always have an active account.";
	public String internalCommMembersMsg = "This community cannot have members from outside your organization.";
	public String descriptionPublicComm = "This is a public community";
	public String descriptionModeratedComm = "This is a moderated community";
	public String descriptionInternalComm = "This is an internal restricted community";
	public String descriptionExternalComm = "This is an external restricted community";
	public String overview = "Overview";
	public String memberRole = "Member";
	public String ownerRole = "Owner";
	public String businessOwnerRole = "Business Owner";
	public String joinCommHelpOnprem = "c_com_join.dita";
	public String joinCommHelpCloud = "t_com_join.html";
	public String participateInCommForumHelpOnprem = "t_com_forum_topic_add.dita";
	public String participateInCommForumHelpCloud = "t_com_forum_topic_add.html";
	public String shareWebResourcesHelpOnprem = "community_bookmarks_frame.dita";
	public String shareWebResourcesHelpCloud = "t_com_bookmarks_add_web.html";
	public String createACommHelpOnprem = "t_com_create.dita";
	public String createACommHelpCloud = "t_com_create.html";
	public String communityNameFieldIsEmptyMsg = "The community name must not be empty.";
	public String richContentWidgetText = "Craft rich content for your community. Post text, links, images and more.";
	public String richContentWidgetErrorMsg = "The page could not be created due to an error.";
	
	//Gatekeeper settings
	public String commTabbedNav = "communities-tabbed-nav";
    public String gk_copycomm_flag = "communities-copy-community";
    public String gk_GuidedTourComm_flag ="guided-tour-communities";
    public String gk_hikariTheme_flag = "hikari-default-theme";
    public String gk_newWidgetLayouts_flag = "communities-new-widget-layouts";
    public String gk_hideWelcomeBox_flag = "ui-welcome-box";
    public String gk_showHiddenAppInTopNav_flag = "communities-show-hidden-in-navigator";
    public String gk_searchQuickResultsScope_flag = "search-quick-results-scope-param";
    public String gk_catalog_ui_updated_flag = "catalog-ui-updated";
    public String gk_unifyInsertImageDialog = "unify_insert_image_dialog";
    public String gk_filesEnableSimplifyLeftNav = "files-enable-simplify-left-nav";
    public String gk_catalog_card_view ="catalog-card-view";
    public String gk_catalog_card_updated ="catalog-card-updated";
    public String gk_communities_template ="communities-template";
    public String gk_communities_highlights_as_overview ="communities-highlights-as-overview";
    
    //Profiles
	public String People = "People";
	public String ProfileStatusUpdate = "BVT profile status update";
	public String profileTag = "tagsc";
	public String helpTextTagInputField = "Add tag(s) to this profile";
	public String searchProfileTitle = "Search - Profiles";
	public String tagsYouAddedView = "My Tags for this profile";
	public String tagsListView = "Tagged by ";
	public String UserPhotoAccSet = "Account Settings";
	public String OrgBannerHelpMsg = "Looking For an Expert?";
	public String OrgBannerHelpMsg1 = "Perform a quick search by entering a name in the field provided.";
	public String OrgBannerHelpMsg2 = "Learn how to use tags to find people.";
	public String Usetagslinkpage = "Understanding profile tags";
	public String MyOrgDirectoryUrl = "SERVER" + "profiles/html/searchProfiles.do#simpleSearch";
	public String MyLinksWidget = "My Links";
	public String MessageUnderMyLinks = "There are no links yet for this profile.";
	public String AddLinkURL = "www.ibm.com";
	public String AddLinksToYourProfile = "Add links to your profile";
	public String IBMWindowURL = "IBM Online Privacy Statement Highlights - United States";
	public String URL = "http://www.ibm.com/in-en/";
	public String HelpwindowBannerText = "Reasons to Update Your Profile";
	public String userProfilePageUrl = "SERVER" + "profiles/html/profileView.do?userid=" + "USERID" + "&lang=en-us"; 
	public String myProfileUrl = "SERVER" + "profiles/html/profileView.do?userid="+ "USERID" ;
	public String IBMConnectionsCloud = "IBM Connections";
	public String networkInvitationStatus = "Pending Invitation...";
	public String networkInvitationSent = "Network Invitation Sent";
	public String networkContactStatus = "Network Contact";
	public String removedNetworkMessage = " has been removed from your network contact list.";
	public String stopNetworkFollowingMessage = " has been removed from your following list";
	public String tagHovermsg = "Tag " + "TAGNAME" + " was tagged by 1 person.  See who added this tag";
	public String directorySearchWithTagUrl = "SERVER"+"profiles/html/simpleSearch.do?profileTags="+"TAGNAME"+"&isSimpleSearch=true";
	public String IBMConnectionPublicstories = "IBM Connections - Public stories";
	public String homepageUpdates = "Homepage - Updates";
	public String termsofusetab = "IBM LI docs and SaaS terms - tou";
	public String termsofuselinkURL = "www-03.ibm.com/software/sla/sladb.nsf/sla/tou/";
	public String privacytablink =  "IBM Online Privacy Statement Highlights - United States";
	public String privacytabURL =  "www.ibm.com/privacy/us/en/";
	public String systemStatustab =  "IBM Connections and Verse";
	public String systemStatusURL = "www.ibm.com/cloud-computing/social/us/en/maintenance/";
	public String whatsnewtab = "IBM Connections Cloud";
	public String whatsnewURL = "SERVER" + "help/index.jsp?topic=/com.ibm.cloud.whatsnew.doc/ll_wn_whats_new.html&lang=en";
	public String feedFooter = "Feed for these entries";
	public String userMyProfilePageUrl = "SERVER" + "profiles/html/profileView.do?userid=" + "USERID" + "&lang=en_us";
	public String userMyFilesPageUrl = "SERVER" + "files/app#/person/" + "USERID";
	public String userCreateActivityPageUrl = "SERVER" + "activities/service/html/createActivity#ll_user_id=" + "USERID";
	public String organizationPageUrl = "SERVER" + "contacts/orgprofiles/partnerPage/" + "ORGID";
	public String webChatPageTitle = "Chat with " + "USERNAME";
	public String ProfileWhoConnectsUs = "Who Connects Us?";
	public String ViewAllLinkUrl = "SERVER" + "mycontacts/home.html#/network/" + "NETWORKID";
	public String recentUpdatecomment = "Test recent update comment";
	public String addfilecomment = "Testing add file feature";
	public String shareAFileMsg = "You have successfully shared the file "+ '"' + "FILENAME" + '"' + " with " + "USERID" + ".";
	public String contactRecordPageUrl = "SERVER" + "mycontacts/home.html#/contact/";
	public String profilePhotoHoverText = "Edit profile photo";
	public String uploadPhotoDesc = "Upload a business-appropriate photo to enhance your profile.\n" +
				"The photo must be in JPEG, GIF, or PNG format and optimally 155x155 pixels in size.\n" +
				"Larger images will take longer to upload and lose definition when they are resized.\n" +
				"Updates to your photo may take a few minutes to show.";
	public String editProfileInfo = "Update information that you want to change in your profile.";
	public String updateSuccessMsg = "Profile data updated successfully.";
	public String wordCountExceedLimitMsg = "FIELD" + " cannot be greater than "+ "COUNT" +" characters.";
	public String localTimePattern = "[A-Za-z ]+[:][ ][0-9]{1,2}[:][0-9]{2}[ ][AP]M"; 
	public String easternTimeZone = "(GMT-05:00) Eastern Time (US & Canada)";
	public String profileChangedMsg = "Profile changed. Click the Save button to save your changes.";
	public String BlogLink = "User's Blog";
	public String networkUserUrl = "SERVER"+ "profiles/html/profileView.do";
	public String myProfileBusinessCardText = "Business card for ";
	public String networkViewAllLinkUrl = "SERVER" + "mycontacts/home.html#/network";
	public String networkUnderText = "No network contacts are associated with this profile";
	public String profiles = "profiles";
	public String welcomePage = "Welcome to IBM Connections";
	public String myNetworkUrl = "SERVER" + "profiles/html/networkView.do";
	public String myfilestab = "My Files";
    public String filesharedwithme  = "Files Shared With Me";
    public String anotheruserOrgDirectoryUrl = "SERVER" + "profiles/html/searchProfiles.do#q=" +"SEARCHUSER" + "&simpleSearch";
    public String aboutmetext = "testing about me text";
    public String backgroundtext = "testing about me and background text";
    public String dykHelpMsg = "Connections Cloud recommends to you";
    public String inviteDialogMsg = "I'd like to add you to my Connections network contacts list.";
    public String myfiletabdata = "My Files";
    public String myProfilePageUrl = "SERVER" + "profiles/html/profileView.do?key="+ "KEY"; 
    public String networkViewLinkUrl = "SERVER" + "profiles/html/networkView.do?";
    public String profileUrl = "SERVER" + "profiles/html/myProfileView.do#&tabinst=Updates";
	public String publicWikisSearchPageUrl = "SERVER" + "wikis/home?lang=en-us#!/search?uid=" + "UID" + "&name=" + "USERNAME";
	public String myOrganizationCommunitiesUrl = "SERVER" + "communities/service/html/allcommunities?userid=" + "UID";
	public String myBlogsUrl = "SERVER" + "blogs/roller-ui/allblogs?userid=" + "UID";
	public String userBookmarkUrl= "SERVER" + "dogear/html?userid=" + "UID";
	public String userWikiUrl= "SERVER" + "wikis/home?lang=en-us#!/search?uid=" + "UID";
	public String userForumsUrl = "SERVER" + "forums/html/search?userid=" + "UID" + "&name=" + "USERNAME";
	public String myActivitiesPageUrl = "SERVER" + "activities/service/html/mainpage#dashboard%2Cmyactivities%2Cuserid%3d" + "UID" + "%2Cname%3D"  + "USERNAME";
	public String downloadvCardWindow = "Download vCard";
	public String dykHelpMsgonPrem = "Connections recommends to you";
    public String helpIBMConnections = "Help - IBM Connections";
    public String helpIBMConnectionsURL = "SERVER" + "help/index.jsp?topic=%2Fcom.ibm.lotus.connections.homepage.help%2Fhframe.html";
    public String homePagetab = "IBM Connections Home Page - Getting started with IBM Connections";
    public String IBMSupportForums = "www-10.lotus.com/ldd/lcforum.nsf";
    public String BookmarktoolURL = "profiles/nav/toolbox?appName=profiles"; 
    public String Bookmarktooltab = "Tools - Profiles";
    public String Abouttab = "About Profiles";
    public String AboutlinkURL = "profiles/html/aboutView.do";
    public String IBMConnectionsonibmTab = "IBM Connections";
    public String IBMConnectionsonibmURL = "www-03.ibm.com/software/products/en/conn";
    public String Submitfeedbacktab = "Feedback";
    public String SubmitfeedbackURL = "www-12.lotus.com/ldd/doc/cct/nextgen.nsf/feedback_choice?OpenForm&Context=footer+ventura+NoTitle+4.5";
    public String ServerMetricstab = "Access to Metrics is Restricted";
    public String ServerMetricsURL = "metrics/app#/";
	public String searchProfileUrl = "SERVER" + "profiles/html/simpleSearch.do";
	public String directoryProfilesText = "Directory - Profiles";
	public String editMyProfileUrl = "SERVER" + "profiles/html/editMyProfileView.do";
	public String editMyProfileText = "Edit My Profile";
	public String statusUpdatesUrl = "SERVER" + "homepage/web/updates/#myStream/statusUpdates/all";
	public String statusUpdatesText = "IBM Connections Home Page - Updates";
	public String OrgDirectoryUrl = "SERVER" + "profiles/html/searchProfiles.do";
		
	//profile update
	public String profJobTitle = "President";
	public String profBuilding = "West Campus";
	public String profFloor = "3rd";
	public String profAddress = "122 Main st, Anytown USA";
	public String profOffice = "b1234";
	public String profOfficePhone = "617-555-1212";
	public String profIP_phone = "508-555-1212";
	public String profTelephone = "978-899-1234";
	public String profMobile = "800-555-1212";
	public String profPager = "201-555-1212";
	public String profFax = "403-555-1212";
	public String profWebSite = "http://mywebsite.com";
	public String profBlogLink ="http://myblog.com";
	public String profAssistant = "Ronald McDonald";
	public String profAltEmail = "altEmail@ibm.com";
	public String profJobDescription = "Preside over the company";

	//Button names
	public String buttonOK = "OK";
	public String buttonCancel = "Cancel";
	public String buttonSave = "Save";
	public String buttonSend = "Send";
	public String buttonRemove = "Remove";
	public String buttonUpload = "Upload";
	public String buttonPost = "Post";
	public String buttonDownload = "Download";
	public String buttonDelete = "Delete";
	
	//Files Data for uploading and download file/folders
	public String downloadsFolder = "C:\\SeleniumServer\\downloads";
	// docker selenium downloads dir
	public String downloadsFolderSelenoid = "/home/selenium/Downloads";
	public String outputFolder = "\\unzippedFilesFolder";
	public String localoutputFolder = "C:\\SeleniumServer\\downloads\\unzippedFilesFolder";
	
	public String file8 = "Penguins.jpg";
	public String file9 = "DesertCopy.jpg";
	public String file10 = "LighthouseCopy.jpg";
	public String file11 = "KoalaCopy.jpg";
	public String file12 = "HydrangeasCopy.jpg";
	public String file13 = "JellyfishCopy.jpg";
	public String file14 = "ChrysanthemumCopy.jpg";
	public String file15 = "testFile1.txt";
	public String file16 = "WebEditorsTestFile.docx";
	public String file17 = "WebEditorsTestFile.odp";
	public String file18 = "WebEditorsTestFile.ods";
	public String file19 = "WebEditorsTestFile.pdf";
	public String file20 = "WebEditorsTestFile.ppt";
	public String file21 = "WebEditorsTestFile.pptx";
	public String file22 = "WebEditorsTestFile.xls";
	public String file23 = "WebEditorsTestFileSheet.xlsx";
	public String file24 = "docTestFile.doc";
	public String file25 = "odtTestFile.odt";
	public String file26 = "SystemErr.zip";
	public String file27 = "build.xml";
	public String file28 = "FileUploadCancel.exe";
	public String file29 = "WebEditorsTestFile.potx";
	public String file30 = "dotxTestFile.dotx";
	public String file31 = "SymphonySpreadsheet.ots";
	public String fileVirus = "eicar.com";
	
	
	//Production
	public enum Side { A, B; }
	public String productionCommunityName = "ProductionTestCommunity";
	public String testNode = nodeProperties.getProperty("test_node");
	public String getNodeIp(Side side) {
		return nodeProperties.getProperty("node." + side + "." + String.valueOf(testNode));
	}
	/**
	 * Last number in each IP for side A and B
	 */
	public Map<Side, List<String>> nodeIps = loadNodes();
	
	private Map<Side, List<String>> loadNodes() {
		Map<Side, List<String>> map = new HashMap<Side, List<String>>();
		map.put(Side.A, new ArrayList<String>());
		map.put(Side.B, new ArrayList<String>());
		String ip;
		for(String node: nodeProperties.stringPropertyNames()) {
			if(node.startsWith("node.")) {
				ip = nodeProperties.getProperty(node);
				ip = ip.split("\\.")[ip.split("\\.").length-1];
				if(node.split("\\.")[1].equals("A")) {
					map.get(Side.A).add(ip);
				} else if(node.split("\\.")[1].equals("B")) {
					map.get(Side.B).add(ip);
				}
			}
		}
		return map;
	}
	
	//Community View names
	public String viewOwner = "I'm an Owner";
	public String viewMember = "I'm a Member";
	public String viewFollowing = "I'm Following";
	public String viewInvited = "I'm Invited";
	public String viewPublic = "css=div ul li a[id='toolbar_catalog_menu_allcommunities']";
	public String viewTrash = "Trash";
	
	//Community App/Widget names
	public String appCommunityDescription = "Community Description";
	public String appRecentUpdates = "Recent Updates";
	public String appStatusUpdates = "Status Updates";
	public String appMembersSummary = "Members";
	public String appTags = "Tags";
	public String appBookmarks = "Bookmarks";
	public String appFeeds = "Feeds";
	public String appImportantBookmarks = "Important Bookmarks";
	public String appForums = "Forums";
	public String appFiles = "Files";
	public String appBlog = "Blog";
	public String appIdeationBlog = "Ideation Blog"	;
	public String appActivities = "Activities";
	public String appWiki = "Wiki";
	public String appSubcommunities = "Subcommunities";
	public String appGallery = "Gallery";
	public String appEvents = "Events";
	public String appRelatedCommunities = "Related Communities";
	public String appLibrary = "Library";
	public String appLinkedLibrary = "Linked Library";
	public String appSurvey = "Surveys";
	public String appFeaturedSurvey = "Featured Survey";
	public String appMetrics = "Metrics";
	public String appHomepage = "Homepage";
	public String appCommunities = "Communities";
	public String appProfiles = "Profiles";
	public String viewMentions = "Mentions";
	public String homepageHelpContent = "Home page";
	
	
	//Event
	public String addToPersonalCalendar = "Add to Personal Calendar";
	public String stopAttendMsg = "You have stopped attending this event.";
	public String attendmsg = "You are attending this event. Subscribe it to your calendar application through";
	public String EventLocation = "This is the event location.";
	public String upcomingEvents = "Upcoming Events";
	public String stopFollowMsg = "You have stopped following this event.";
	public String followmsg = "You have followed this event. Subscribe it to your calendar application through";
	public String NotifySuccessMsg = "Notification sent successfully.";
	public String EmtpyCommentErrorMsg = "Please enter your comment and click 'Save.' If you no longer want to leave a comment click 'Cancel.'";
	public String NoCommentMsg = "There are no comments.";
	public String CustomFieldName = "Custom Field";
	public String MonthSkippedMsg = "Months that do not contain this date will be skipped";
	
	//mention
	public String mentionErrorMsg = "The following people mentioned will not be able to view the message because they are not members of the community";
	public String mentionErrorMsgGlobalSharebox = "The following people cannot view your message because they are not members of the community:";
	public String mentionErrorMsgVisitorModel = "The following people mentioned will not be able to see the message as they are in a different organization:";
	
	public String StatusComment = "This is the FVT Level 2 status comment ";
	public String ProfilesBoardEntry = "I am currently working on automating FVT testcases for Profiles ";
	public String statusSuccessMsg = "The message was successfully posted.";
	public String StatusHashMentionMsg = "This is new Status Message #Sales #Marketing";
	public String hashTag1 = "#Sales";
	public String hashTag2 = "#Marketing";
	
	//Getting Started page
	public String gettingStartedPageTitle = "Getting Started with IBM Connections";
	public String GettingStartedGreeting = "Welcome to IBM Connections";
	public String CollaborationToolsMsg = "Someone in this organization has invited you to join an IBM Connections community or has shared a file with you. IBM Connections gives you the tools you need to collaborate with others. When you join IBM Connections, you become an external user in this organization.";
	public String HomepageInfoMsg = "The Home page will help you quickly find out what's new, what requires your attention, and when people are mentioning you. To get the most out of this page, be sure to follow communities and files, so you can easily stay up-to-date on the latest changes. To open the home page, click the Home icon.";
	public String CommunitiesInfoMsg = "Use Communities to participate in a community of interest, where you can share files, track projects, ask and respond to questions, co-edit information, and brainstorm new ideas. To find the communities you belong to, click the Communities icon.";
	public String FilesInfoMsg = "Files lets you share files with others and see files that others have shared with you. Pin files that you want to get back to often, to find them quickly. Leave comments on files, download the latest version, or use the co-editing capabilities to make changes to files online.";
	public String MyNotActionReqInfoMsg = "To get going right away, click My Notifications or Action Required, to see if there are any messages waiting for you.";
	public String HelpInfoMsg = "Want to learn more? Click What can I do as an external user? to find out.";
	
	//Help page
	public String HelpPageText = "Get the latest news and updates from your network and the wider organization.";
	
	public String ModeratedTxt = "Moderated (anyone in my organization can see content but must request to join)";
	public String RestrictedTxt = "Restricted (people must be invited to join)";	
	public String AddPublicTxt = "Moderated (anyone in my organization can see content but must request to join)";
	public String AllowExternalTxt = "Allow people from outside of my organization to become members of this community";	
	public String addPublicAccessTxt = "Open (anyone in my organization can join)";
		
	//Global Sharebox
	public String communityOption = "a Community";
	public String ReaderOption = "as Reader";
	public String SearchPanelFlyoutDefaultText_1 = "Once you visit pages you'll see them here. Think of it as a shortcut.";
	public String SearchPanelFlyoutDefaultText_2 = "Go ahead and search to get started.";
	public String SearchPanelFlyout_NoResultsText = "Sorry, there are no results for this search. Try a different word or more general term.";
	public String ReadersOption = "as Readers";
	public String EditorsOption = "as Editors";
	public String OwnersOption = "as Owners";
	
	//News Story Data
	//Activities Strings
	public static String ADDED_MEMBER_TO_ACTIVITY = "USER" + " added PLACEHOLDER to the REPLACE_THIS activity.";
	public static String CREATE_PUBLIC_ACTIVTY = "USER" + " created a public activity named PLACEHOLDER.";
	public static String CREATE_PRIVATE_ACTIVTY = "USER" + " created a private activity named PLACEHOLDER.";
	public static String CREATE_ACTIVITY = "USER" + " created an activity named PLACEHOLDER.";
	public static String CREATE_ACTIVITY_ENTRY = "USER" + " created the PLACEHOLDER entry in the REPLACE_THIS activity.";
	public static String COMMENT_ON_ACTIVITY = "USER" + " commented on the PLACEHOLDER entry thread in the REPLACE_THIS activity.";
	public static String COMMENT_ON_THEIR_OWN_ACTIVITY = "USER" + " commented on their own PLACEHOLDER entry thread in the REPLACE_THIS activity."; 
	public static String CREATE_TODO_ITEM = "USER" + " created a to-do item named PLACEHOLDER in the REPLACE_THIS activity.";
	public static String ASSIGNED_TODO_ITEM = "USER" + " assigned themselves a to-do item named PLACEHOLDER in the REPLACE_THIS activity.";
	public static String ASSIGNED_TODO_ITEM_YOU = "USER" + " assigned you a to-do item named PLACEHOLDER in the REPLACE_THIS activity.";
	public static String COMPLETE_TODO_ITEM = "USER" + " completed their own PLACEHOLDER to-do item in the REPLACE_THIS activity.";
	public static String COMPLETE_A_TODO_ITEM = "USER" + " completed the PLACEHOLDER to-do item in the REPLACE_THIS activity.";
	public static String CREATE_SECTION = "USER" + " created a section in the PLACEHOLDER.";
	public static String MAKE_ACTIVITY_PUBLIC = "USER" + " made the PLACEHOLDER activity public.";
	public static String MADE_AN_ACTIVITY_PUBLIC = "USER" + " made an activity public.";
	public static String UPDATE_ACTIVITY_ENTRY = "USER" + " updated the PLACEHOLDER entry in the REPLACE_THIS activity.";
	public static String UPDATE_THEIR_OWN_ACTIVITY_ENTRY = "USER" + " updated their own PLACEHOLDER entry in the REPLACE_THIS activity.";
	public static String UPDATE_ACTIVITY_TODO = "USER" + " updated the to-do item named PLACEHOLDER in the REPLACE_THIS activity.";
	public static String REOPEN_TODO_ITEM = "USER" + " reopened the PLACEHOLDER to-do item in the REPLACE_THIS activity.";
	public static String COMMENT_TODO_ITEM = "USER" + " commented on the PLACEHOLDER entry thread in the REPLACE_THIS activity.";
	public static String COMMENT_UPDATE_ACTIVITY_ENTRY = "USER" + " updated the PLACEHOLDER entry thread in the REPLACE_THIS activity.";
	public static String COMMENT_UPDATE_THEIR_OWN_ACTIVITY_ENTRY = "USER" + " updated their own PLACEHOLDER entry thread in the REPLACE_THIS activity.";
	public static String COMMENT_ON_THEIR_OWN_TODO = "USER" + " commented on their own PLACEHOLDER entry thread in the REPLACE_THIS activity."; 
	
	public static String MY_NOTIFICATIONS_ACTIVITY_MEMBER_ADDED_FROM_ME = "You notified " + "USER" + " that they were added to the activity named PLACEHOLDER.";
	public static String MY_NOTIFICATIONS_ACTIVITY_MEMBER_ADDED_FOR_ME = "USER" + " notified you that you were added to the activity named PLACEHOLDER.";
	public static String MY_NOTIFICATIONS_ACTIVITY_NOTIFY_ENTRY_FROM_ME = "You notified " + "USER" + " and 1 others about the activity entry named PLACEHOLDER.";
	public static String MY_NOTIFICATIONS_ACTIVITY_NOTIFY_ENTRY_FROM_ME_SingleUser = "You notified " + "USER" + " about the activity entry named PLACEHOLDER.";
	public static String MY_NOTIFICATIONS_ACTIVITY_NOTIFY_ENTRY_FOR_ME = "USER" + " notified you and 1 others about the activity entry named PLACEHOLDER.";
	public static String MY_NOTIFICATIONS_ACTIVITY_SAVED_ADDED_FOR_ME = "USER" + " notified PLACEHOLDER that they were added to the activity named REPLACE_THIS";
	public static String MY_NOTIFICATIONS_ACTIVITY_MEMBER_ADDED_FOR_ME_ENTRY = "USER" + " notified PLACEHOLDER that they were added to the activity named REPLACE_THIS.";
	public static String MY_NOTIFICATIONS_ACTIVITY_ENTRY_NOTIFICATION = "USER" + " notified you about the activity entry named PLACEHOLDER.";
	
	public static String COMMENT_ON_YOUR_ACTIVITY = "USER" + " commented on your PLACEHOLDER entry thread in the REPLACE_THIS activity.";
	public static String COMMENT_ON_YOUR_ENTRY = "USER" + " commented on the PLACEHOLDER entry thread added by you in the REPLACE_THIS activity.";
	public static String COMMENT_BY_YOU_ACTIVITY = "You commented on the PLACEHOLDER entry thread in the REPLACE_THIS activity.";
	public static String COMMENT_BY_YOU_YOUR_ACTIVITY = "You commented on your PLACEHOLDER entry thread in the REPLACE_THIS activity.";

	public static String COMMENT_ACTIVITY_TWO_COMMENTERS = "USER" + " and PLACEHOLDER commented on the REPLACE_THIS entry thread in the REPLACE_THIS_TOO activity.";
	public static String COMMENT_YOUR_ACTIVITY_TWO_COMMENTERS = "USER" + " and PLACEHOLDER commented on your REPLACE_THIS entry thread in the REPLACE_THIS_TOO activity.";
	public static String COMMENT_ACTIVITY_YOU_OTHER = "USER" + " and you commented on the PLACEHOLDER entry thread in the REPLACE_THIS activity.";
	public static String COMMENT_YOUR_ACTIVITY_YOU_OTHER = "USER" + " and you commented on your PLACEHOLDER entry thread in the REPLACE_THIS activity.";

	public static String COMMENT_ACTIVITY_MANY = "USER" + " and PLACEHOLDER others commented on the REPLACE_THIS entry thread in the REPLACE_THIS_TOO activity.";
	public static String COMMENT_YOUR_ACTIVITY_MANY = "USER" + " and PLACEHOLDER others commented on your REPLACE_THIS entry thread in the REPLACE_THIS_TOO activity.";
	public static String COMMENT_ACTIVITY_YOU_MANY = "You and PLACEHOLDER others commented on the REPLACE_THIS entry thread in the REPLACE_THIS_TOO activity.";
	public static String COMMENT_YOUR_ACTIVITY_YOU_MANY = "You and PLACEHOLDER others commented on your REPLACE_THIS entry thread in the REPLACE_THIS_TOO activity.";

	public static String COMMENT_UPDATE_YOUR_ACTIVITY_ENTRY = "USER" + " updated your PLACEHOLDER entry thread in the REPLACE_THIS activity.";
	public static String COMMENT_UPDATE_ACTIVITY_ENTRY_YOU = "You updated the PLACEHOLDER entry thread in the REPLACE_THIS activity.";
	public static String COMMENT_UPDATE_YOUR_ACTIVITY_ENTRY_YOU = "You updated your PLACEHOLDER entry thread in the REPLACE_THIS activity.";

	public static String COMMENT_UPDATE_ACTIVITY_ENTRY_TWO = "USER" + " and PLACEHOLDER updated the REPLACE_THIS entry thread in the REPLACE_THIS_TOO activity.";
	public static String COMMENT_UPDATE_YOUR_ACTIVITY_ENTRY_TWO = "USER" + " and PLACEHOLDER updated your REPLACE_THIS entry thread in the REPLACE_THIS_TOO activity.";
	public static String COMMENT_UPDATE_ACTIVITY_ENTRY_YOU_OTHER = "USER" + " and you updated the PLACEHOLDER entry thread in the REPLACE_THIS activity.";
	public static String COMMENT_UPDATE_YOUR_ACTIVITY_ENTRY_YOU_OTHER = "USER" + " and you updated your PLACEHOLDER entry thread in the REPLACE_THIS activity.";

	public static String COMMENT_UPDATE_ACTIVITY_ENTRY_MANY = "USER" + " and PLACEHOLDER updated the REPLACE_THIS entry thread in the REPLACE_THIS_TOO activity.";
	public static String COMMENT_UPDATE_YOUR_ACTIVITY_ENTRY_MANY = "USER" + " and PLACEHOLDER updated your REPLACE_THIS entry thread in the REPLACE_THIS_TOO activity.";
	public static String COMMENT_UPDATE_ACTIVITY_ENTRY_YOU_MANY = "You and PLACEHOLDER others updated the REPLACE_THIS entry thread in the REPLACE_THIS_TOO activity.";
	public static String COMMENT_UPDATE_YOUR_ACTIVITY_ENTRY_YOU_MANY = "You and PLACEHOLDER others updated the REPLACE_THIS entry thread in the REPLACE_THIS_TOO activity.";

	//Blogs Strings
	public static String MY_NOTIFICATIONS_BLOG_ENTRY_NOTIFY_FROM_ME = "You notified " + "USER" + " about the PLACEHOLDER blog entry in the REPLACE_THIS blog.";
	public static String MY_NOTIFICATIONS_BLOG_ENTRY_NOTIFY_FOR_ME = "USER" + " notified you about the PLACEHOLDER blog entry in the REPLACE_THIS blog.";
	public static String MY_NOTIFICATIONS_BLOG_ADD_AUTHOR_FROM_ME = "You added " + "USER" + " as an author of the PLACEHOLDER blog.";
	public static String MY_NOTIFICATIONS_BLOG_ADD_AUTHOR_FOR_ME = "USER" + " added you as an author of the PLACEHOLDER blog.";
	public static String MY_NOTIFICATIONS_BLOG_ADD_OWNER_FROM_ME = "You added " + "USER" + " as an owner of the PLACEHOLDER blog.";
	public static String MY_NOTIFICATIONS_BLOG_ADD_OWNER_FOR_ME = "USER" + " added you as an owner of the PLACEHOLDER blog.";
	public static String MY_NOTIFICATIONS_BLOG_ADD_DRAFT_FROM_ME = "You added " + "USER" + " as a draft contributor of the PLACEHOLDER blog.";
	public static String MY_NOTIFICATIONS_BLOG_ADD_DRAFT_FOR_ME = "USER" + " added you as a draft contributor of the PLACEHOLDER blog.";

	public static String CREATE_BLOG = "USER" + " created a blog named PLACEHOLDER.";
	public static String CREATE_BLOG_ENTRY = "USER" + " created a blog entry named PLACEHOLDER in the REPLACE_THIS blog.";
	public static String CREATE_IDEATION_BLOG = "USER" + " created the PLACEHOLDER community Ideation Blog.";
	public static String CREATE_IDEATION_BLOG_RECENTUPDATES = "USER" + " added the PLACEHOLDER community Ideation Blog.";
	public static String CREATE_IDEATION_BLOG_ENTRY = "USER" + " created the PLACEHOLDER entry in the REPLACE_THIS Ideation Blog.";
	public static String CREATE_IDEATION_BLOG_IDEA = "USER" + " created the PLACEHOLDER idea in the REPLACE_THIS Ideation Blog.";
	public static String UPDATE_IDEATION_BLOG_IDEA = "USER" + " updated the PLACEHOLDER idea in the REPLACE_THIS Ideation Blog.";
	public static String UPDATE_IDEATION_COMMENT = "USER" + " updated a comment on the PLACEHOLDER idea in the REPLACE_THIS Ideation Blog.";
	public static String UPDATE_THEIR_OWN_IDEATION_COMMENT = "USER" + " updated a comment on their own PLACEHOLDER idea in the REPLACE_THIS Ideation Blog.";
	public static String COMMENT_IDEATION_BLOG_IDEA = "USER" + " commented on their own PLACEHOLDER idea in the REPLACE_THIS Ideation Blog.";
	public static String COMMENT_ON_THEIR_OWN_IDEATION_BLOG_IDEA = "USER" + " commented on their own PLACEHOLDER idea in the REPLACE_THIS Ideation Blog.";
	public static String LIKE_A_COMMENT_THEIR_IDEA = "USER" + " liked a comment on their PLACEHOLDER idea.";
	public static String CREATE_TRACKBACK_IDEATION = "USER" + " left a trackback on their own PLACEHOLDER idea in the REPLACE_THIS Ideation Blog.";
	public static String CREATE_TRACKBACK_IDEATION_OTHER_USER = "USER" + " left a trackback on the PLACEHOLDER idea in the REPLACE_THIS Ideation Blog.";
	public static String GRADUATE_IDEA = "USER" + " graduated their own PLACEHOLDER idea in the REPLACE_THIS Ideation Blog.";
	public static String VOTE_FOR_BLOG = "USER" + " voted for the PLACEHOLDER idea in the REPLACE_THIS Ideation Blog."; 
	public static String CREATE_DRAFT_BLOG_ENTRY = "USER" + " created a draft blog entry named PLACEHOLDER in the REPLACE_THIS blog.";
	public static String UPDATE_BLOG_ENTRY = "USER" + " updated the PLACEHOLDER blog entry in the REPLACE_THIS blog.";
	public static String UPDATE_BLOG_COMMENT = "USER" + " updated a comment on the PLACEHOLDER blog entry in the REPLACE_THIS blog.";
	public static String UPDATE_THEIR_OWN_BLOG_COMMENT = "USER" + " updated their own comment on the PLACEHOLDER blog entry in the REPLACE_THIS blog.";
	public static String ADD_COMMENT_BLOG_ENTRY = "USER" + " commented on their own PLACEHOLDER blog entry in the REPLACE_THIS blog.";
	public static String ADD_TB_BLOG_ENTRY = "USER" + " left a trackback on their own PLACEHOLDER blog entry in the REPLACE_THIS blog.";
	public static String LIKE_BLOG_ENTRY = "USER" + " liked their own PLACEHOLDER blog entry in the REPLACE_THIS blog.";
	public static String LIKE_THEIR_OWN_BLOG_ENTRY = "USER" + " liked their own blog entry PLACEHOLDER in the REPLACE_THIS blog.";
	public static String LIKE_BLOG_COMMENT = "USER" + " liked their own comment on PLACEHOLDER.";
	public static String LIKE_A_COMMENT_THEIR_BLOG_ENTRY = "USER" + " liked a comment on their blog entry PLACEHOLDER.";
	public static String VOTE_FOR_OWN_IDEA = "USER" + " voted for their own PLACEHOLDER idea in the REPLACE_THIS Ideation Blog.";
	public static String DUPLICATE_IDEA = "USER" + " marked their PLACEHOLDER idea in the REPLACE_THIS Ideation Blog as a duplicate of idea REPLACE_THIS_TOO.";
	public static String EDIT_BLOG = "USER" + " updated the PLACEHOLDER blog.";
	public static String CREATE_COMM_BLOG = "USER" + " created the PLACEHOLDER community blog.";
	public static String CREATE_COMM_BLOG_RECENTUPDATES = "USER" + " added the PLACEHOLDER community blog.";

	public static String COMMENT_BLOG_ENTRY = "USER" + " commented on the PLACEHOLDER blog entry in the REPLACE_THIS blog.";
	public static String COMMENT_YOUR_BLOG_ENTRY = "USER" + " commented on your PLACEHOLDER blog entry in the REPLACE_THIS blog.";
	public static String COMMENT_BLOG_ENTRY_YOU = "You commented on the PLACEHOLDER blog entry in the REPLACE_THIS blog.";
	public static String COMMENT_YOUR_BLOG_ENTRY_YOU = "You commented on the PLACEHOLDER blog entry in the REPLACE_THIS blog.";
	public static String COMMENT_YOUR_BLOG_ENTRY_BY_YOU = "You commented on your PLACEHOLDER blog entry in the REPLACE_THIS blog.";
	
	public static String COMMENT_BLOG_ENTRY_TWO_COMMENTERS = "USER" + " and PLACEHOLDER commented on the REPLACE_THIS blog entry in the REPLACE_THIS_TOO blog.";
	public static String COMMENT_YOUR_BLOG_ENTRY_TWO_COMMENTERS = "USER" + " and PLACEHOLDER commented on your REPLACE_THIS blog entry in the REPLACE_THIS_TOO blog.";
	public static String COMMENT_BLOG_ENTRY_YOU_OTHER = "USER" + " and you commented on the PLACEHOLDER blog entry in the REPLACE_THIS blog.";
	public static String COMMENT_YOUR_BLOG_ENTRY_YOU_OTHER = "USER" + " and you commented on your PLACEHOLDER blog entry in the REPLACE_THIS blog.";

	public static String COMMENT_BLOG_ENTRY_MANY = "USER" + " and PLACEHOLDER others commented on the REPLACE_THIS blog entry in the REPLACE_THIS_TOO blog.";
	public static String COMMENT_YOUR_BLOG_ENTRY_MANY = "USER" + " and PLACEHOLDER others commented on your REPLACE_THIS blog entry in the REPLACE_THIS_TOO blog.";
	public static String COMMENT_BLOG_ENTRY_YOU_MANY = "You and PLACEHOLDER others commented on the REPLACE_THIS blog entry in the REPLACE_THIS_TOO blog.";
	public static String COMMENT_YOUR_BLOG_ENTRY_YOU_MANY = "You and PLACEHOLDER others commented on your REPLACE_THIS blog entry in the REPLACE_THIS_TOO blog.";

	public static String UPDATE_YOUR_BLOG_COMMENT = "USER" + " updated a comment on your PLACEHOLDER blog entry in the REPLACE_THIS blog.";
	public static String UPDATE_BLOG_COMMENT_YOU = "You updated a comment on the PLACEHOLDER blog entry in the REPLACE_THIS blog.";
	public static String UPDATE_BLOG_COMMENT_YOU_YOUR_ENTRY = "You updated a comment on your PLACEHOLDER blog entry in the REPLACE_THIS blog.";

	public static String UPDATE_BLOG_COMMENT_TWO_COMMENTERS = "USER" + " and PLACEHOLDER updated a comment on the REPLACE_THIS blog entry in the REPLACE_THIS_TOO blog.";
	public static String UPDATE_YOUR_BLOG_COMMENT_TWO_COMMENTERS = "USER" + " and PLACEHOLDER updated a comment on your REPLACE_THIS blog entry in the REPLACE_THIS_TOO blog.";
	public static String UPDATE_BLOG_COMMENT_YOU_OTHER = "USER" + " and you updated a comment on the PLACEHOLDER blog entry in the REPLACE_THIS blog.";
	public static String UPDATE_YOUR_BLOG_COMMENT_YOU_OTHER = "USER" + " and you updated a comment on your PLACEHOLDER blog entry in the REPLACE_THIS blog.";

	public static String UPDATE_BLOG_COMMENT_MANY = "USER" + " and PLACEHOLDER others updated a comment on the REPLACE_THIS blog entry in the REPLACE_THIS_TOO blog.";
	public static String UPDATE_YOUR_BLOG_COMMENT_MANY = "USER" + " and PLACEHOLDER others updated a comment on your REPLACE_THIS blog entry in the REPLACE_THIS_TOO blog.";
	public static String UPDATE_BLOG_COMMENT_YOU_MANY = "You and PLACEHOLDER others updated a comment on the REPLACE_THIS blog entry in the REPLACE_THIS_TOO blog.";
	public static String UPDATE_YOUR_BLOG_COMMENT_YOU_MANY = "You and PLACEHOLDER others updated a comment on your REPLACE_THIS blog entry in the REPLACE_THIS_TOO blog.";

	public static String LIKE_BLOG_ENTRY_OTHER = "USER" + " liked PLACEHOLDER's blog entry REPLACE_THIS in the REPLACE_THIS_TOO blog.";
	public static String LIKE_YOUR_BLOG_ENTRY = "USER" + " liked your blog entry PLACEHOLDER in the REPLACE_THIS blog.";
	public static String LIKE_BLOG_ENTRY_YOU = "You liked PLACEHOLDER's blog entry REPLACE_THIS in the REPLACE_THIS_TOO blog.";
	public static String LIKE_YOUR_BLOG_ENTRY_YOU = "You liked your blog entry PLACEHOLDER in the REPLACE_THIS blog.";

	public static String LIKE_BLOG_ENTRY_TWO_LIKES = "USER" + " and PLACEHOLDER liked REPLACE_THIS's blog entry REPLACE_THIS_TOO in the REPLACE_THIS_ALSO blog.";
	public static String LIKE_YOUR_BLOG_ENTRY_TWO = "USER" + " and PLACEHOLDER liked your blog entry REPLACE_THIS in the REPLACE_THIS_TOO blog.";
	public static String LIKE_BLOG_ENTRY_YOU_OTHER = "USER" + " and you liked PLACEHOLDER's blog entry REPLACE_THIS in the REPLACE_THIS_TOO blog.";
	public static String LIKE_YOUR_BLOG_ENTRY_YOU_OTHER = "USER" + " and you liked your blog entry PLACEHOLDER in the REPLACE_THIS blog.";

	public static String LIKE_BLOG_ENTRY_MANY = "USER" + " and PLACEHOLDER others liked REPLACE_THIS's blog entry REPLACE_THIS_TOO in the REPLACE_THIS_ALSO blog.";
	public static String LIKE_YOUR_BLOG_ENTRY_MANY = "USER" + " and PLACEHOLDER others liked your blog entry REPLACE_THIS in the REPLACE_THIS_TOO blog.";
	public static String LIKE_BLOG_ENTRY_YOU_MANY = "You and PLACEHOLDER others liked REPLACE_THIS's blog entry REPLACE_THIS_TOO in the REPLACE_THIS_ALSO blog.";
	public static String LIKE_YOUR_BLOG_ENTRY_YOU_MANY = "You and PLACEHOLDER others liked your blog entry REPLACE_THIS in the REPLACE_THIS_TOO blog.";

	public static String LIKE_BLOG_COMMENT_OTHER = "USER" + " liked PLACEHOLDER's comment on REPLACE_THIS.";
	public static String LIKE_YOUR_BLOG_COMMENT = "USER" + " liked your comment on PLACEHOLDER.";
	public static String LIKE_A_COMMENT_YOUR_BLOG_ENTRY_OTHER = "USER" + " liked a comment on your blog entry PLACEHOLDER.";
	public static String LIKE_BLOG_COMMENT_YOU = "You liked PLACEHOLDER's comment on REPLACE_THIS.";
	public static String LIKE_YOUR_BLOG_COMMENT_YOU = "You liked your comment on PLACEHOLDER.";
	public static String LIKE_A_COMMENT_YOUR_BLOG_ENTRY_YOU = "You liked a comment on your blog entry PLACEHOLDER.";

	public static String LIKE_BLOG_COMMENT_TWO_LIKES = "USER" + " and PLACEHOLDER liked REPLACE_THIS's comment on REPLACE_THIS_TOO.";
	public static String LIKE_YOUR_BLOG_COMMENT_TWO_LIKES = "USER" + " and PLACEHOLDER liked your comment on REPLACE_THIS.";
	public static String LIKE_A_COMMENT_YOUR_BLOG_ENTRY_TWO_LIKES = "USER" + " and PLACEHOLDER liked a comment on your blog entry REPLACE_THIS.";
	public static String LIKE_BLOG_COMMENT_YOU_OTHER = "USER" + " and you liked PLACEHOLDER's comment on REPLACE_THIS.";
	public static String LIKE_YOUR_BLOG_COMMENT_YOU_OTHER = "USER" + " and you liked your comment on PLACEHOLDER.";
	public static String LIKE_A_COMMENT_YOUR_BLOG_ENTRY_YOU_OTHER = "USER" + " and you liked a comment on your blog entry PLACEHOLDER.";

	public static String LIKE_BLOG_COMMENT_MANY = "USER" + " and PLACEHOLDER others liked REPLACE_THIS's comment on REPLACE_THIS_TOO.";
	public static String LIKE_YOUR_BLOG_COMMENT_MANY = "USER" + " and PLACEHOLDER others liked your comment on REPLACE_THIS.";
	public static String LIKE_A_COMMENT_YOUR_BLOG_ENTRY_MANY = "USER" + " and PLACEHOLDER others liked a comment on your blog entry REPLACE_THIS.";
	public static String LIKE_BLOG_COMMENT_YOU_MANY = "You and PLACEHOLDER others liked REPLACE_THIS's comment on REPLACE_THIS_TOO.";
	public static String LIKE_YOUR_BLOG_COMMENT_YOU_MANY = "You and PLACEHOLDER others liked your comment on REPLACE_THIS.";
	public static String LIKE_A_COMMENT_YOUR_BLOG_ENTRY_YOU_MANY = "You and PLACEHOLDER others liked a comment on your blog entry REPLACE_THIS.";

	public static String COMMENT_IDEA = "USER" + " commented on the PLACEHOLDER idea in the REPLACE_THIS Ideation Blog.";
	public static String COMMENT_YOUR_IDEA = "USER" + " commented on your PLACEHOLDER idea in the REPLACE_THIS Ideation Blog.";
	public static String COMMENT_IDEA_YOU = "You commented on the PLACEHOLDER idea in the REPLACE_THIS Ideation Blog.";
	public static String COMMENT_YOUR_IDEA_YOU = "You commented on your PLACEHOLDER idea in the REPLACE_THIS Ideation Blog.";

	public static String COMMENT_IDEA_TWO_COMMENTERS = "USER" + " and PLACEHOLDER commented on the REPLACE_THIS idea in the REPLACE_THIS_TOO Ideation Blog.";
	public static String COMMENT_YOUR_IDEA_TWO_COMMENTERS = "USER" + " and PLACEHOLDER commented on your REPLACE_THIS idea in the REPLACE_THIS_TOO Ideation Blog.";
	public static String COMMENT_IDEA_YOU_OTHER = "USER" + " and you commented on the PLACEHOLDER idea in the REPLACE_THIS Ideation Blog.";
	public static String COMMENT_YOUR_IDEA_YOU_OTHER = "USER" + " and you commented on your PLACEHOLDER idea in the REPLACE_THIS Ideation Blog.";

	public static String COMMENT_IDEA_MANY = "USER" + " and PLACEHOLDER others commented on the REPLACE_THIS idea in the REPLACE_THIS_TOO Ideation Blog.";
	public static String COMMENT_YOUR_IDEA_MANY = "USER" + " and PLACEHOLDER others commented on your REPLACE_THIS idea in the REPLACE_THIS_TOO Ideation Blog.";
	public static String COMMENT_IDEA_YOU_MANY = "You and PLACEHOLDER others commented on the REPLACE_THIS idea in the REPLACE_THIS_TOO Ideation Blog.";
	public static String COMMENT_YOUR_IDEA_YOU_MANY = "You and PLACEHOLDER others commented on your REPLACE_THIS idea in the REPLACE_THIS_TOO Ideation Blog.";

	public static String UPDATE_COMMENT_YOUR_IDEA = "USER" + " updated a comment on your PLACEHOLDER idea in the REPLACE_THIS Ideation Blog.";
	public static String UPDATE_COMMENT_IDEA_YOU = "You updated a comment on the PLACEHOLDER idea in the REPLACE_THIS Ideation Blog.";
	public static String UPDATE_COMMENT_YOUR_IDEA_YOU = "You updated a comment on your PLACEHOLDER idea in the REPLACE_THIS Ideation Blog.";

	public static String UPDATE_COMMENT_IDEA_TWO_COMMENTERS = "USER" + " and PLACEHOLDER updated a comment on the REPLACE_THIS idea in the REPLACE_THIS_TOO Ideation Blog.";
	public static String UPDATE_COMMENT_YOUR_IDEA_TWO_COMMENTERS = "USER" + " and PLACEHOLDER updated a comment on your REPLACE_THIS idea in the REPLACE_THIS_TOO Ideation Blog.";
	public static String UPDATE_COMMENT_IDEA_YOU_OTHER = "USER" + " and you updated a comment on the PLACEHOLDER idea in the REPLACE_THIS Ideation Blog.";
	public static String UPDATE_COMMENT_YOUR_IDEA_YOU_OTHER = "USER" + " and you updated a comment on your PLACEHOLDER idea in the REPLACE_THIS Ideation Blog.";

	public static String UPDATE_COMMENT_IDEA_MANY = "USER" + " and PLACEHOLDER others updated a comment on the REPLACE_THIS idea in the REPLACE_THIS_TOO Ideation Blog.";
	public static String UPDATE_COMMENT_YOUR_IDEA_MANY = "USER" + " and PLACEHOLDER others updated your comment on the REPLACE_THIS idea in the REPLACE_THIS_TOO Ideation Blog.";
	public static String UPDATE_COMMENT_IDEA_YOU_MANY = "You and PLACEHOLDER others updated a comment on the REPLACE_THIS idea in the REPLACE_THIS_TOO Ideation Blog.";
	public static String UPDATE_COMMENT_YOUR_IDEA_YOU_MANY = "You and PLACEHOLDER others updated a comment on your REPLACE_THIS idea in the REPLACE_THIS_TOO Ideation Blog.";

	public static String VOTED_YOUR_IDEA = "USER" + " voted for your PLACEHOLDER idea in the REPLACE_THIS Ideation Blog.";
	public static String VOTED_IDEA_YOU = "You voted for the PLACEHOLDER idea in the REPLACE_THIS Ideation Blog.";
	public static String VOTED_YOUR_IDEA_YOU = "You voted for your PLACEHOLDER idea in the REPLACE_THIS Ideation Blog.";

	public static String VOTED_IDEA_TWO_VOTERS = "USER" + " and PLACEHOLDER voted for the REPLACE_THIS idea in the REPLACE_THIS_TOO Ideation Blog.";
	public static String VOTED_YOUR_IDEA_TWO_VOTERS = "USER" + " and PLACEHOLDER voted for your REPLACE_THIS idea in the REPLACE_THIS_TOO Ideation Blog.";
	public static String VOTED_IDEA_YOU_OTHER = "USER" + " and you voted for the PLACEHOLDER idea in the REPLACE_THIS Ideation Blog.";
	public static String VOTED_YOUR_IDEA_YOU_OTHER = "USER" + " and you voted for your PLACEHOLDER idea in the REPLACE_THIS Ideation Blog.";

	public static String VOTED_IDEA_MANY = "USER" + " and PLACEHOLDER others voted for the REPLACE_THIS idea in the REPLACE_THIS_TOO Ideation Blog.";
	public static String VOTED_YOUR_IDEA_MANY = "USER" + " and PLACEHOLDER others voted for your REPLACE_THIS idea in the REPLACE_THIS_TOO Ideation Blog.";
	public static String VOTED_IDEA_YOU_MANY = "You and PLACEHOLDER others voted for the REPLACE_THIS idea in the REPLACE_THIS_TOO Ideation Blog.";
	public static String VOTED_YOUR_IDEA_YOU_MANY = "You and PLACEHOLDER others voted for your REPLACE_THIS idea in the REPLACE_THIS_TOO Ideation Blog.";

	public static String LIKE_COMMENT_IDEA = "USER" + " liked a comment on PLACEHOLDER.";
	public static String LIKE_COMMENT_IDEA_MADE_BY = "USER" + " liked a comment on PLACEHOLDER made by REPLACE_THIS";
	public static String LIKE_YOUR_COMMENT_IDEA = "USER" + " liked your comment on PLACEHOLDER.";
	public static String LIKE_A_COMMENT_YOUR_IDEA = "USER" + " liked a comment on your PLACEHOLDER idea.";
	public static String LIKE_COMMENT_IDEA_YOU = "You liked a comment on PLACEHOLDER.";
	public static String LIKE_YOUR_COMMENT_IDEA_YOU = "You liked your comment on PLACEHOLDER.";
	public static String LIKE_COMMENT_YOUR_IDEA_YOU = "You liked the comment on your PLACEHOLDER idea.";

	public static String LIKE_COMMENT_IDEA_TWO_LIKES = "USER" + " and PLACEHOLDER liked a comment on REPLACE_THIS.";
	public static String LIKE_YOUR_COMMENT_IDEA_TWO_LIKES = "USER" + " and PLACEHOLDER liked your comment on REPLACE_THIS.";
	public static String LIKE_A_COMMENT_YOUR_IDEA_TWO_LIKES = "USER" + " and PLACEHOLDER liked a comment on your REPLACE_THIS idea.";
	public static String LIKE_COMMENT_IDEA_YOU_OTHER = "USER" + " and you liked a comment on PLACEHOLDER.";
	public static String LIKE_YOUR_COMMENT_IDEA_YOU_OTHER = "USER" + " and you liked your comment on PLACEHOLDER.";
	public static String LIKE_COMMENT_YOUR_IDEA_YOU_OTHER = "USER" + " and you liked a comment on your PLACEHOLDER idea.";

	public static String LIKE_COMMENT_IDEA_MANY = "USER" + " and PLACEHOLDER others liked a comment on REPLACE_THIS.";
	public static String LIKE_YOUR_COMMENT_IDEA_MANY = "USER" + " and PLACEHOLDER others liked your comment on REPLACE_THIS.";
	public static String LIKE_A_COMMENT_YOUR_IDEA_MANY = "USER" + " and PLACEHOLDER others liked a comment on your REPLACE_THIS idea.";
	public static String LIKE_COMMENT_IDEA_YOU_MANY = "You and PLACEHOLDER others liked a comment on REPLACE_THIS.";
	public static String LIKE_YOUR_COMMENT_IDEA_YOU_MANY = "You and PLACEHOLDER others liked your comment on REPLACE_THIS.";
	public static String LIKE_COMMENT_YOUR_IDEA_YOU_MANY = "You and PLACEHOLDER others liked a comment on your REPLACE_THIS idea.";

	//Bookmarks
	public static String MY_NOTIFICATIONS_BOOKMARK_NOTIFY_FROM_ME = "You notified " + "USER" + " about the following bookmarks:";
	public static String MY_NOTIFICATIONS_BOOKMARK_NOTIFY_FOR_ME = "USER" + " notified you about the following bookmarks:";
	public static String MY_NOTIFICATIONS_BOOKMARK_BROKEN_FROM_ME = "You notified " + "USER" + " that the URL for the bookmark named PLACEHOLDER is broken.";
	public static String MY_NOTIFICATIONS_BOOKMARK_BROKEN_FOR_ME = "USER" + " notified you that the URL for the bookmark named PLACEHOLDER is broken.";
	public static String MY_NOTIFICATIONS_SAVED_BOOKMARK_NOTIFY_FOR_ME = "USER" + " notified PLACEHOLDER about the following bookmarks:";
	
	//Communities
	public static String MY_NOTIFICATIONS_COMMUNITY_INVITE_FROM_ME = "You invited " + "USER" + " to join the PLACEHOLDER community.";
	public static String MY_NOTIFICATIONS_COMMUNITY_INVITE_FOR_ME = "USER" + " invited you to join the PLACEHOLDER community.";
	public static String MY_NOTIFICATIONS_COMMUNITY_REQUEST_FROM_ME = "USER" + " has requested to join the PLACEHOLDER community.";
	public static String MY_NOTIFICATIONS_COMMUNITY_REQUEST_FOR_ME = "USER" + " has requested to join your PLACEHOLDER community.";
	public static String MY_NOTIFICATIONS_COMMUNITY_ADD_MEMBER_FROM_ME = "You added " + "USER" + " to the PLACEHOLDER community.";
	public static String MY_NOTIFICATIONS_COMMUNITY_ADD_MEMBER_FOR_ME = "USER" + " added you to the PLACEHOLDER community.";
	public static String MY_NOTIFICATIONS_COMMUNITY_REMOVE_MEMBER_FROM_ME = "You removed " + "USER" + " from the PLACEHOLDER community.";
	public static String MY_NOTIFICATIONS_COMMUNITY_REMOVE_MEMBER_FOR_ME = "USER" + " removed you from the PLACEHOLDER community.";
	public static String MY_NOTIFICATIONS_COMMUNITY_EVENT_INVITE_FROM_ME = "You notified " + "USER" + " about the community event PLACEHOLDER in the REPLACE_THIS community.";
	public static String MY_NOTIFICATIONS_COMMUNITY_EVENT_INVITE_FOR_ME = "USER" + " notified you about community event PLACEHOLDER in the REPLACE_THIS community.";
	public static String SAVED_EVENT_ADD_MEMBER = "USER" + " added PLACEHOLDER to the REPLACE_THIS community.";
	
	public static String MY_NOTIFICATIONS_COMMUNITY_INVITE_JOIN_LINK_TEXT = "Join this community";
	public static String MY_NOTIFICATIONS_COMMUNITY_INVITE_DECLINE_LINK_TEXT = "Decline this invitation";
	public static String MY_NOTIFICATIONS_COMMUNITY_INVITE_SAVE_LINK_TEXT = "Save this";
	public static String MY_NOTIFICATIONS_COMMUNITY_INVITE_STOPFOLLOWING_LINK_TEXT = "Stop Following";
	
	public static String CREATE_COMMUNITY = "USER" + " created a community named PLACEHOLDER";
	public static String CREATE_COMMUNITY_RECENTUPDATES = "USER" + " created the community.";
	public static String ADD_COMMUNITY_BOOKMARK = "USER" + " added the PLACEHOLDER bookmark to the REPLACE_THIS community.";
	public static String ADD_COMMUNITY_FEED = "USER" + " added the PLACEHOLDER feed to the REPLACE_THIS community.";
	public static String UPDATE_COMMUNITY_BOOKMARK = "USER" + " updated the PLACEHOLDER bookmark in the REPLACE_THIS community.";
	public static String COMMUNITY_ADD_BLOG = "%s added a blog to the %s community.";
	public static String COMMUNITY_ADD_MEMBER = "USER" + " added PLACEHOLDER to the REPLACE_THIS community.";
	
	public static String COMMUNITY_COMMENT_MESSAGE = "USER" + " commented on PLACEHOLDER's message in the REPLACE_THIS community.";
	public static String COMMUNITY_COMMENT_YOUR_MESSAGE = "USER" + " commented on your message in the PLACEHOLDER community.";
	public static String COMMUNITY_COMMENT_MESSAGE_YOU = "You commented on PLACEHOLDER's message in the REPLACE_THIS community.";
	public static String COMMUNITY_COMMENT_YOUR_MESSAGE_YOU = "You commented on your message in the PLACEHOLDER community.";

	public static String COMMUNITY_COMMENT_MESSAGE_TWO_COMMENTERS = "USER" + " and PLACEHOLDER commented on REPLACE_THIS's message in the REPLACE_THIS_TOO community.";
	public static String COMMUNITY_COMMENT_YOUR_MESSAGE_TWO_COMMENTERS = "USER" + " and PLACEHOLDER commented on your message in the REPLACE_THIS community.";
	public static String COMMUNITY_COMMENT_MESSAGE_YOU_OTHER = "USER" + " and you commented on PLACEHOLDER's message in the REPLACE_THIS community.";
	public static String COMMUNITY_COMMENT_YOUR_MESSAGE_YOU_OTHER = "USER" + " and you commented on your message in the PLACEHOLDER community.";

	public static String COMMUNITY_COMMENT_MESSAGE_MANY = "USER" + " and PLACEHOLDER others commented on REPLACE_THIS's message in the REPLACE_THIS_TOO community.";
	public static String COMMUNITY_COMMENT_YOUR_MESSAGE_MANY = "USER" + " and PLACEHOLDER others commented on your message in the REPLACE_THIS community.";
	public static String COMMUNITY_COMMENT_MESSAGE_YOU_MANY = "You and PLACEHOLDER others commented on REPLACE_THIS's message in the REPLACE_THIS_TOO community.";
	public static String COMMUNITY_COMMENT_YOUR_MESSAGE_YOU_MANY = "You and PLACEHOLDER others commented on your message in the REPLACE_THIS community.";

	public static String COMMUNITY_LIKE_MESSAGE = "USER" + " liked PLACEHOLDER's message in the REPLACE_THIS community.";
	public static String COMMUNITY_LIKE_YOUR_MESSAGE = "USER" + " liked your message in the PLACEHOLDER community.";
	public static String COMMUNITY_LIKE_MESSAGE_YOU = "You liked PLACEHOLDER's message in the REPLACE_THIS community.";
	public static String COMMUNITY_LIKE_YOUR_MESSAGE_YOU = "You liked your message in the PLACEHOLDER community.";

	public static String COMMUNITY_LIKE_MESSAGE_TWO_LIKES = "USER" + " and PLACEHOLDER liked REPLACE_THIS's message in the REPLACE_THIS_TOO community.";
	public static String COMMUNITY_LIKE_YOUR_MESSAGE_TWO_LIKES = "USER" + " and PLACEHOLDER liked your message in the REPLACE_THIS community.";
	public static String COMMUNITY_LIKE_MESSAGE_YOU_OTHER = "USER" + " and you liked PLACEHOLDER's message in the REPLACE_THIS community.";
	public static String COMMUNITY_LIKE_YOUR_MESSAGE_YOU_OTHER = "USER" + " and you liked your message in the PLACEHOLDER community.";

	public static String COMMUNITY_LIKE_MESSAGE_MANY = "USER" + " and PLACEHOLDER others liked REPLACE_THIS's message in the REPLACE_THIS_TOO community.";
	public static String COMMUNITY_LIKE_YOUR_MESSAGE_MANY = "USER" + " and PLACEHOLDER others liked your message in the REPLACE_THIS community.";
	public static String COMMUNITY_LIKE_MESSAGE_YOU_MANY = "You and PLACEHOLDER others liked REPLACE_THIS's message in the REPLACE_THIS_TOO community.";
	public static String COMMUNITY_LIKE_YOUR_MESSAGE_YOU_MANY = "You and PLACEHOLDER others liked your message in the REPLACE_THIS community.";

	public static String COMMUNITY_LIKE_COMMENT_MESSAGE = "USER" + " liked PLACEHOLDER's comment in the REPLACE_THIS community.";
	public static String COMMUNITY_LIKE_YOUR_COMMENT_MESSAGE = "USER" + " liked your comment in the PLACEHOLDER community.";
	public static String COMMUNITY_LIKE_COMMENT_MESSAGE_YOU = "You liked PLACEHOLDER's comment in the REPLACE_THIS community.";
	public static String COMMUNITY_LIKE_YOUR_COMMENT_MESSAGE_YOU = "You liked your comment in the PLACEHOLDER community.";
	public static String COMMUNITY_LIKE_YOUR_COMMENT_MESSAGE_YOU_SAVED = "USER" + " liked their own comment in the PLACEHOLDER community.";

	public static String COMMUNITY_LIKE_COMMENT_MESSAGE_TWO_LIKES = "USER" + " and PLACEHOLDER liked REPLACE_THIS's comment in the REPLACE_THIS_TOO community.";
	public static String COMMUNITY_LIKE_YOUR_COMMENT_MESSAGE_TWO_LIKES = "USER" + " and PLACEHOLDER liked your comment in the REPLACE_THIS community.";
	public static String COMMUNITY_LIKE_COMMENT_MESSAGE_YOU_OTHER = "USER" + " and you liked PLACEHOLDER's comment in the REPLACE_THIS community.";
	public static String COMMUNITY_LIKE_YOUR_COMMENT_MESSAGE_YOU_OTHER = "USER" + " and you liked your comment in the PLACEHOLDER community.";

	public static String COMMUNITY_LIKE_COMMENT_MESSAGE_MANY = "USER" + " and PLACEHOLDER others liked REPLACE_THIS's comment in the REPLACE_THIS_TOO community.";
	public static String COMMUNITY_LIKE_YOUR_COMMENT_MESSAGE_MANY = "USER" + " and PLACEHOLDER others liked your comment in the REPLACE_THIS community.";
	public static String COMMUNITY_LIKE_COMMENT_MESSAGE_YOU_MANY = "You and PLACEHOLDER others liked REPLACE_THIS's comment in the REPLACE_THIS_TOO community.";
	public static String COMMUNITY_LIKE_YOUR_COMMENT_MESSAGE_YOU_MANY = "You and PLACEHOLDER others liked your comment in the REPLACE_THIS community.";

	//Community Calendar
	public static String CREATE_COMMUNITY_CALENDAR_EVENT = "USER" + " created the event PLACEHOLDER in the REPLACE_THIS community";
	public static String UPDATE_COMMUNITY_CALENDAR_EVENT = "USER" + " updated the event PLACEHOLDER in the REPLACE_THIS community.";
	public static String COMMENT_COMMUNITY_CALENDAR_EVENT = "USER" + " commented on the event PLACEHOLDER in the REPLACE_THIS community.";
	public static String COMMENT_ON_THEIR_OWN_EVENT = "USER" + " commented on their own event PLACEHOLDER in the REPLACE_THIS community.";
	public static String CREATE_COMMUNITY_CALENDAR_REPEATING_EVENT = "USER" + " created a repeating event PLACEHOLDER in the REPLACE_THIS community";
	public static String UPDATE_COMMUNITY_CALENDAR_REPEATING_EVENT = "USER" + " updated the repeating event PLACEHOLDER in the REPLACE_THIS community.";
	public static String UPDATE_COMMUNITY_CALENDAR_EVENT_INSTANCE = "USER" + " updated an instance of the repeating event PLACEHOLDER in the REPLACE_THIS community.";

	public static String CALENDAR_ENTRY_COMMENT = "USER" + " commented on the event PLACEHOLDER in the REPLACE_THIS community.";
	public static String CALENDAR_YOUR_ENTRY_COMMENT = "USER" + " commented on your event PLACEHOLDER in the REPLACE_THIS community.";
	public static String CALENDAR_ENTRY_COMMENT_YOU = "You commented on the event PLACEHOLDER in the REPLACE_THIS community.";
	public static String CALENDAR_YOUR_ENTRY_COMMENT_YOU = "You commented on your event PLACEHOLDER in the REPLACE_THIS community.";

	public static String CALENDAR_ENTRY_COMMENT_TWO_COMMENTERS = "USER" + " and PLACEHOLDER commented on the event REPLACE_THIS in the REPLACE_THIS_TOO community.";
	public static String CALENDAR_YOUR_ENTRY_COMMENT_TWO_COMMENTERS = "USER" + " and PLACEHOLDER commented on your event REPLACE_THIS in the REPLACE_THIS_TOO community.";
	public static String CALENDAR_ENTRY_COMMENT_YOU_OTHER = "USER" + " and you commented on the event PLACEHOLDER in the REPLACE_THIS community.";
	public static String CALENDAR_YOUR_ENTRY_COMMENT_YOU_OTHER = "USER" + " and you commented on your event PLACEHOLDER in the REPLACE_THIS community.";

	public static String CALENDAR_ENTRY_COMMENT_MANY = "USER" + " and PLACEHOLDER others commented on the event REPLACE_THIS in the REPLACE_THIS_TOO community.";
	public static String CALENDAR_YOUR_ENTRY_COMMENT_MANY = "USER" + " and PLACEHOLDER others commented on your event REPLACE_THIS in the REPLACE_THIS_TOO community.";
	public static String CALENDAR_ENTRY_COMMENT_YOU_MANY = "You and PLACEHOLDER others commented on the event REPLACE_THIS in the REPLACE_THIS_TOO community.";
	public static String CALENDAR_YOUR_ENTRY_COMMENT_YOU_MANY = "You and PLACEHOLDER others commented on your event REPLACE_THIS in the REPLACE_THIS_TOO community.";

	//Dogear
	public static String CREATE_BOOKMARK = "USER" + " created a bookmark named PLACEHOLDER.";
	public static String UPDATE_BOOKMARK = "USER" + " updated the bookmark named PLACEHOLDER.";
	public static String ADD_BOOKMARK_WATCHLIST = "USER" + " added to their Bookmarks watchlist.";
	public static String ADD_TAG = "USER" + " added the following tag to their Bookmarks watchlist: PLACEHOLDER.";
	public static String TAG_NAME = "fvttesttag";
	public static String Form_EditBookmark_Window = "Edit Bookmark";
	
	//Forums Strings
	public static String CREATE_FORUM = "USER" + " created the PLACEHOLDER forum.";
	public static String UPDATE_FORUM = "USER" + " updated the PLACEHOLDER forum.";
	public static String CREATE_TOPIC = "USER" + " created a topic named PLACEHOLDER in the REPLACE_THIS forum.";
	public static String UPDATE_TOPIC = "USER" + " updated the PLACEHOLDER topic in the REPLACE_THIS forum.";
	public static String CREATE_REPLY = "USER" + " replied to the PLACEHOLDER topic thread in the REPLACE_THIS forum.";
	public static String CREATE_THEIR_OWN_REPLY = "USER" + " replied to their own PLACEHOLDER topic thread in the REPLACE_THIS forum.";
	public static String UPDATE_REPLY = "USER" + " updated the PLACEHOLDER topic in the REPLACE_THIS forum.";
	public static String UPDATE_REPLY_THREAD = "USER" + " updated the PLACEHOLDER topic thread in the REPLACE_THIS forum.";
	public static String UPDATE_REPLY_THEIR_OWN_THREAD = "USER" + " updated their own PLACEHOLDER topic thread in the REPLACE_THIS forum.";
	public static String UPDATE_REPLY_RE = "USER" + " updated the Re: PLACEHOLDER topic in the REPLACE_THIS forum.";
	public static String FORUM_LIKE_TOPIC = "USER" + " liked the topic named PLACEHOLDER in the REPLACE THIS forum.";
	public static String FORUM_LIKE_RESPONSE = "USER" + " liked a reply to the PLACEHOLDER topic thread in the REPLACE_THIS forum.";
	public static String FORUM_LIKE_RESPONSE_OWNTOPIC = "USER" + " liked a reply to their PLACEHOLDER topic thread in the REPLACE_THIS forum.";
	public static String FORUM_LIKE_RESPONSE_THEIR_OWN_TOPIC = "USER" + " liked a reply to their own PLACEHOLDER topic thread in the REPLACE_THIS forum.";
	public static String FORUM_LIKE_OWNTOPIC = "USER" + " liked their own topic named PLACEHOLDER in the REPLACE_THIS forum.";
	public static String FORUM_LIKE_THEIRTOPIC = "USER" + " liked their topic named PLACEHOLDER in the REPLACE_THIS forum.";
	public static String FORUM_LIKE_OWNRESPONSE = "USER" + " liked their own reply to the PLACEHOLDER topic thread"; 
	public static String FORUM_STOP_FOLLOWING = "Stop Following this Forum";
	public static String FORUM_START_FOLLOWING = "Follow this Forum";
	public static String FORUM_TOPIC_MARKED_REGULAR = "This question has been marked as a regular topic.";

	public static String CREATE_REPLY_YOUR_TOPIC = "USER" + " replied to your PLACEHOLDER topic thread in the REPLACE_THIS forum.";
	public static String CREATE_REPLY_TOPIC_YOU = "You replied to the PLACEHOLDER topic thread in the REPLACE_THIS forum.";
	public static String CREATE_REPLY_YOUR_TOPIC_YOU = "You replied to your PLACEHOLDER topic thread in the REPLACE_THIS forum.";

	public static String CREATE_REPLY_TOPIC_TWO_REPLIES = "USER" + " and PLACEHOLDER replied to the REPLACE_THIS topic thread in the REPLACE_THIS_TOO forum.";
	public static String CREATE_REPLY_YOUR_TOPIC_TWO_REPLIES = "USER" + " and PLACEHOLDER replied to your REPLACE_THIS topic thread in the REPLACE_THIS_TOO forum.";
	public static String CREATE_REPLY_TOPIC_YOU_OTHER = "USER" + " and you replied to the PLACEHOLDER topic thread in the REPLACE_THIS forum.";
	public static String CREATE_REPLY_YOUR_TOPIC_YOU_OTHER = "USER" + " and you replied to your PLACEHOLDER topic thread in the REPLACE_THIS forum.";

	public static String CREATE_REPLY_TOPIC_MANY = "USER" + " and PLACEHOLDER others replied to the REPLACE_THIS topic thread in the REPLACE_THIS_TOO forum.";
	public static String CREATE_REPLY_YOUR_TOPIC_MANY = "USER" + " and PLACEHOLDER others replied to your REPLACE_THIS topic thread in the REPLACE_THIS_TOO forum.";
	public static String CREATE_REPLY_TOPIC_YOU_MANY = "You and PLACEHOLDER others replied to the REPLACE_THIS topic thread in the REPLACE_THIS_TOO forum.";
	public static String CREATE_REPLY_YOUR_TOPIC_YOU_MANY = "You and PLACEHOLDER others replied to your REPLACE_THIS topic thread in the REPLACE_THIS_TOO forum.";

	public static String UPDATE_REPLY_YOUR_TOPIC = "USER" + " updated your PLACEHOLDER topic thread in the REPLACE_THIS forum.";
	public static String UPDATE_REPLY_TOPIC_YOU = "You updated the PLACEHOLDER topic thread in the REPLACE_THIS forum.";
	public static String UPDATE_REPLY_YOUR_TOPIC_YOU = "You updated your PLACEHOLDER topic thread in the REPLACE_THIS forum.";

	public static String UPDATE_REPLY_TOPIC_TWO_UPDATES = "USER" + " and PLACEHOLDER updated the REPLACE_THIS topic thread in the REPLACE_THIS_TOO forum.";
	public static String UPDATE_REPLY_YOUR_TOPIC_TWO_UPDATES = "USER" + " and PLACEHOLDER updated your REPLACE_THIS topic thread in the REPLACE_THIS_TOO forum.";
	public static String UPDATE_REPLY_TOPIC_YOU_OTHER = "USER" + " and you updated the PLACEHOLDER topic thread in the REPLACE_THIS forum.";
	public static String UPDATE_REPLY_YOUR_TOPIC_YOU_OTHER = "USER" + " and you updated your PLACEHOLDER topic thread in the REPLACE_THIS forum.";

	public static String UPDATE_REPLY_TOPIC_MANY = "USER" + "  and PLACEHOLDER others updated the REPLACE_THIS topic thread in the REPLACE_THIS_TOO forum.";
	public static String UPDATE_REPLY_YOUR_TOPIC_MANY = "USER" + "  and PLACEHOLDER others updated your REPLACE_THIS topic thread in the REPLACE_THIS_TOO forum.";
	public static String UPDATE_REPLY_TOPIC_YOU_MANY = "You and PLACEHOLDER others updated the REPLACE_THIS topic thread in the REPLACE_THIS_TOO forum.";
	public static String UPDATE_REPLY_YOUR_TOPIC_YOU_MANY = "You and PLACEHOLDER others updated your REPLACE_THIS topic thread in the REPLACE_THIS_TOO forum.";

	public static String FORUM_LIKE_YOUR_TOPIC = "USER" + " liked your topic named PLACEHOLDER in the REPLACE_THIS forum.";
	public static String FORUM_LIKE_TOPIC_YOU = "You liked the topic named PLACEHOLDER in the REPLACE_THIS forum.";
	public static String FORUM_LIKE_YOUR_TOPIC_YOU = "You liked your topic named PLACEHOLDER in the REPLACE_THIS forum.";

	public static String FORUM_LIKE_TOPIC_TWO_LIKES = "USER" + " and PLACEHOLDER liked the topic named REPLACE_THIS in the REPLACE_THIS_TOO forum.";
	public static String FORUM_LIKE_YOUR_TOPIC_TWO_LIKES = "USER" + " and PLACEHOLDER liked your topic named REPLACE_THIS in the REPLACE_THIS_TOO forum.";
	public static String FORUM_LIKE_TOPIC_YOU_OTHER = "USER" + " and you liked the topic named PLACEHOLDER in the REPLACE_THIS forum.";
	public static String FORUM_LIKE_YOUR_TOPIC_YOU_OTHER = "USER" + " and you liked your topic named PLACEHOLDER in the REPLACE_THIS forum.";

	public static String FORUM_LIKE_TOPIC_MANY = "USER" + " and PLACEHOLDER others liked the topic named REPLACE_THIS in the REPLACE_THIS_TOO forum.";
	public static String FORUM_LIKE_YOUR_TOPIC_MANY = "USER" + " and PLACEHOLDER others liked your topic named REPLACE_THIS in the REPLACE_THIS_TOO forum.";
	public static String FORUM_LIKE_TOPIC_YOU_MANY = "You and PLACEHOLDER others liked the topic named REPLACE_THIS in the REPLACE_THIS_TOO forum.";
	public static String FORUM_LIKE_YOUR_TOPIC_YOU_MANY = "You and PLACEHOLDER others liked your topic named REPLACE_THIS in the REPLACE_THIS_TOO forum.";

	public static String FORUM_LIKE_YOUR_THREAD_RESPONSE = "USER" + " liked a reply to your PLACEHOLDER topic thread in the REPLACE_THIS forum.";
	public static String FORUM_LIKE_RESPONSE_YOU = "You liked a reply to the PLACEHOLDER topic thread in the REPLACE_THIS forum.";
	public static String FORUM_LIKE_YOUR_THREAD_RESPONSE_YOU = "You liked a reply to your PLACEHOLDER topic thread in the REPLACE_THIS forum.";

	public static String FORUM_LIKE_RESPONSE_TWO_LIKES = "USER" + " and PLACEHOLDER liked a reply to the REPLACE_THIS topic thread in the REPLACE_THIS_TOO forum.";
	public static String FORUM_LIKE_YOUR_RESPONSE_TWO_LIKES = "USER" + " and PLACEHOLDER liked your reply to the REPLACE_THIS topic thread in the REPLACE_THIS_TOO forum.";
	public static String FORUM_LIKE_RESPONSE_YOU_OTHER = "USER" + " and you liked a reply to the PLACEHOLDER topic thread in the REPLACE_THIS forum.";
	public static String FORUM_LIKE_YOUR_RESPONSE_YOU_OTHER = "USER" + " and you liked your reply to the PLACEHOLDER topic thread in the REPLACE_THIS forum.";

	public static String FORUM_LIKE_RESPONSE_MANY = "USER" + " and PLACEHOLDER others liked a reply to the REPLACE_THIS topic thread in the REPLACE_THIS_TOO forum.";
	public static String FORUM_LIKE_YOUR_RESPONSE_MANY = "USER" + " and PLACEHOLDER others liked your reply to the REPLACE_THIS topic thread in the REPLACE_THIS_TOO forum.";
	public static String FORUM_LIKE_RESPONSE_YOU_MANY = "You and PLACEHOLDER others liked a reply to the REPLACE_THIS topic thread in the REPLACE_THIS_TOO forum.";
	public static String FORUM_LIKE_YOUR_RESPONSE_YOU_MANY = "You and PLACEHOLDER others liked your reply to the REPLACE_THIS topic thread in the REPLACE_THIS_TOO forum.";
	
	public static String MY_NOTIFICATIONS_CREATE_REPLY = "USER" + " replied to the Re: PLACEHOLDER topic thread started by you in the REPLACE_THIS forum.";
	public String Successful_Pin_Msg = "This topic has been successfully pinned. It will stay at the top of this forum's topic list.";
	public String Successful_UnPin_Msg = "This topic has been successfully unpinned. It will no longer stay at the top of this forum's topic list.";

	public static String Communities_Joined_NoFollow = "You have joined the community and can now post content. Follow the community to get updates about community content.";
	public static String SuccessfulDelFromSavedList = "Entry has been successfully removed from your Saved list.";
	public static String SuccessfulDelFromActionRequiredList = "Entry has been successfully removed from your Action Required list.";
	public static String SuccessfulAddRCMsg = "Successfully add a new related community.";
	public static String SuccessfulEditRCMsg = "Successfully edited the related community.";
	public static String SuccessfulDelRCMsg = "Successfully deleted the related community.";
	
	public static String OnePersonLikesThis = "1 person likes this";
	public static String NoPeopleLikeThis = "0 people like this";
	public static String PeopleWhoLikeThis = "People who like this...";
	
	//Profiles
	public static String MY_NOTIFICATIONS_NETWORK_INVITE_FROM_ME = "You invited " + "USER" + " to join your network.";
	public static String MY_NOTIFICATIONS_NETWORK_INVITE_FOR_ME = "USER" + " invited you to become a network contact.";
	public static String PROFILE_INFO_CHANGED = "USER" + "'s profile information changed.";
	public static String MULTI_PROFILE_CHANGE = "The following people updated their profile information:";
	public static String PROFILES_SEARCH_RESULTS = "Profile search results for Name:";
	public static String LONG_INVITE_MESSAGE = "123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 223456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 323456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 423456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 523456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 6";
	public static String INVITE_MESSAGE_TOO_LONG = "Your invite message is too long. It can be a maximum of 500 characters";
	public static String MY_NOTIFICATIONS_NETWORK_INVITE_ACCEPTED = "USER" + " accepted your network invitation.";
	public static String NETWORK_INVITATION_MESSAGE = "Please accept this invitation to join my network.";
	
	public static String MY_NOTIFICATIONS_NETWORK_INVITE_ACCEPTED_TWO = "USER" + " and PLACEHOLDER accepted your network invitation.";
	public static String MY_NOTIFICATIONS_NETWORK_INVITE_ACCEPTED_MANY = "USER" + " and PLACEHOLDER others accepted your network invitation.";

	public static String FOLLOWING_YOU = "USER" + " followed you.";
	public static String FOLLOWING_YOU_TWO_FOLLOWERS = "USER" + " and PLACEHOLDER followed you.";
	public static String FOLLOWING_YOU_MANY = "USER" + " and PLACEHOLDER others followed you.";

	public static String REPOSTED_UPDATE = "USER" + " reposted:";
	public static String MESSAGE_ORIGINALLY_POSTED_TO = "USER's message originally posted to PLACEHOLDER";
	public static String COMMENT_YOUR_STATUSUPDATE = "USER" + " commented on your message.";
	public static String COMMENT_ON_THEIR_OWN_MESSAGE = "USER" + " commented on their own message.";
	public static String COMMENT_STATUSUPDATE_OTHER_USER_YOU = "You commented on PLACEHOLDER's message.";
	public static String COMMENT_YOUR_STATUSUPDATE_YOU = "You commented on your message.";
	public static String COMMENT_YOUR_STATUSUPDATE_USER_THEIR_OWN = "USER" + " commented on their own message.";

	public static String COMMENT_STATUSUPDATE_OTHER_USER_TWO_COMMENTERS = "USER" + " and PLACEHOLDER commented on PLACEHOLDER's message.";
	public static String COMMENT_YOUR_STATUSUPDATE_TWO_COMMENTERS = "USER" + " and PLACEHOLDER commented on your message.";
	public static String COMMENT_STATUSUPDATE_OTHER_USER_YOU_OTHER = "USER" + " and you commented on PLACEHOLDER's message.";
	public static String COMMENT_YOUR_STATUSUPDATE_OTHER_USER_YOU_OTHER = "USER" + " and you commented on your message.";
	public static String COMMENT_USER_THEIR_OWN_MESSAGE_TO_OTHER_USER = "USER" + " commented on their own message posted to PLACEHOLDER.";
	public static String COMMENT_YOU_YOUR_OWN_MESSAGE_TO_OTHER_USER = "You commented on your message posted to PLACEHOLDER.";
	
	public static String COMMENT_STATUSUPDATE_OTHER_USER_MANY = "USER" + " and PLACEHOLDER others commented on REPLACE_THIS's message.";
	public static String COMMENT_YOUR_STATUSUPDATE_MANY = "USER" + " and PLACEHOLDER others commented on your message.";
	public static String COMMENT_STATUSUPDATE_OTHER_USER_YOU_MANY = "You and PLACEHOLDER others commented on REPLACE_THIS's message.";
	public static String COMMENT_YOUR_STATUSUPDATE_YOU_MANY = "You and PLACEHOLDER others commented on your message.";

	public static String LIKE_STATUSUPDATE_OTHER_USER = "USER" + " liked PLACEHOLDER's message.";
	public static String LIKE_YOUR_STATUSUPDATE = "USER" + " liked your message.";
	public static String LIKE_STATUSUPDATE_OTHER_USER_YOU = "You liked PLACEHOLDER's message.";
	public static String LIKE_YOUR_STATUSUPDATE_YOU = "You liked your message.";

	public static String LIKE_STATUSUPDATE_OTHER_USER_TWO_LIKES = "USER" + " and PLACEHOLDER liked REPLACE_THIS's message.";
	public static String LIKE_YOUR_STATUSUPDATE_TWO_LIKES = "USER" + " and PLACEHOLDER liked your message.";
	public static String LIKE_STATUSUPDATE_OTHER_USER_YOU_OTHER = "USER" + " and you liked PLACEHOLDER's message.";
	public static String LIKE_YOUR_STATUSUPDATE_YOU_OTHER = "USER" + " and you liked your message.";
	public static String LIKE_YOUR_STATUSUPDATE_YOU_THEIR_OWN = "USER" + " liked their own message.";

	public static String LIKE_STATUSUPDATE_MANY = "USER" + " and PLACEHOLDER others liked REPLACE_THIS's message.";
	public static String LIKE_YOUR_STATUSUPDATE_MANY = "USER" + " and PLACEHOLDER others liked your message.";
	public static String LIKE_STATUSUPDATE_YOU_MANY = "You and PLACEHOLDER others liked REPLACE_THIS's message.";
	public static String LIKE_YOUR_STATUSUPDATE_YOU_MANY = "You and PLACEHOLDER others liked your message.";

	public static String LIKE_SU_COMMENT = "USER" + " liked PLACEHOLDER's comment.";
	public static String LIKE_SU_YOUR_COMMENT = "USER" + " liked your comment on a message.";
	public static String LIKE_SU_YOUR_COMMENT_ON_MESSAGE = "You liked your comment on a message.";
	public static String LIKE_SU_COMMENT_YOU = "You liked PLACEHOLDER's comment.";
	public static String LIKE_SU_YOUR_COMMENT_YOU = "You liked your comment.";
	public static String LIKE_SU_YOUR_COMMENT_YOU_USER = "USER" + " liked their own comment.";

	public static String LIKE_SU_COMMENT_TWO_LIKES = "USER" + " and PLACEHOLDER liked REPLACE_THIS's comment.";
	public static String LIKE_SU_YOUR_COMMENT_TWO_LIKES = "USER" + " and PLACEHOLDER liked your comment on a message.";
	public static String LIKE_SU_COMMENT_YOU_OTHER = "USER" + " and you liked PLACEHOLDER's comment.";
	public static String LIKE_SU_YOUR_COMMENT_YOU_OTHER = "USER" + " and you liked your comment on a message.";

	public static String LIKE_SU_COMMENT_MANY = "USER" + " and PLACEHOLDER others liked REPLACE_THIS's comment.";
	public static String LIKE_SU_YOUR_COMMENT_MANY = "USER" + " and PLACEHOLDER others liked your comment on a message.";
	public static String LIKE_SU_COMMENT_YOU_MANY = "You and PLACEHOLDER others liked REPLACE_THIS's comment.";
	public static String LIKE_SU_YOUR_COMMENT_YOU_MANY = "You and PLACEHOLDER others liked your comment on a message.";
	public static String LIKE_SU_THEIR_OWN_COMMENT_YOU_USERS_BOARD_MESSAGE = "You liked your comment on a message posted to PLACEHOLDER";
	public static String LIKE_SU_THEIR_OWN_COMMENT_SAVED_VIEW = "USER" + " liked their own comment on a message posted to PLACEHOLDER";

	//Wikis Strings
	public static String CREATE_WIKI = "USER" + " created a wiki named PLACEHOLDER.";
	public static String CREATE_WIKI_PAGE = "USER" + " created a wiki page named PLACEHOLDER in the REPLACE_THIS wiki.";
	public static String WIKI_WELCOME_PAGE_CREATED = "USER" + " created a wiki page named Welcome to PLACEHOLDER in the REPLACE_THIS wiki.";
	public static String EDIT_WIKI_PAGE = "USER" + " edited the wiki page PLACEHOLDER in the REPLACE_THIS wiki.";
	public static String TAG_WIKI_PAGE = "USER" + " tagged the wiki page Welcome to PLACEHOLDER with REPLACE_THIS in the REPLACE_THIS_TOO wiki.";
	public static String LIKE_WIKI_PAGE = "USER" + " liked the wiki page PLACEHOLDER in the REPLACE_THIS wiki.";
	public static String LIKE_WIKI_WELCOME = "USER" + " liked the wiki page Welcome to PLACEHOLDER in the REPLACE_THIS wiki.";
	public static String COMMENT_WIKI_PAGE = "USER" + " commented on the wiki page PLACEHOLDER in the REPLACE_THIS wiki.";
	public static String COMMENT_WIKI_WELCOME = "USER" + " commented on the wiki page Welcome to PLACEHOLDER in the REPLACE_THIS wiki.";
	public static String CREATE_WIKI_COMMUNITY = "A PLACEHOLDER community wiki was created.";
	public static String UPDATE_WIKI = "USER" + " edited the wiki page PLACEHOLDER in the REPLACE_THIS wiki.";
	public static String UPDATE_WIKI_PAGE = "USER" + " updated the wiki page Re: Welcome to PLACEHOLDER in the REPLACE_THIS wiki.";
	public static String UPDATE_WIKI_PAGE_NO_WELCOME = "USER" + " updated the wiki page Re: PLACEHOLDER in the REPLACE_THIS wiki.";
	public static String ADDED_YOU_AS_EDITOR = "USER" + " added you as an editor of the PLACEHOLDER wiki.";
	
	public static String MY_NOTIFICATIONS_WIKI_MEMBER_ADDED_FROM_ME = "You notified " + "USER" + " that they have been added to the PLACEHOLDER wiki.";
	public static String MY_NOTIFICATIONS_WIKI_MEMBER_ADDED_FOR_ME = "USER" + " notified you that you have been added to the PLACEHOLDER wiki.";
	public String LongTagMessage = "is too long.";
	public String LongTag = "test2_to_check_how_many_characters_are_allowed_before_you_get_the_message_stating_that_you_need_to_shorten_the_page_title_and_if_you_click_the_link_it_will_shorten_the_page_name_for_you_and_so_I_would_hope_that_this_happens_soon_as_I_am_runnin";
	public String MaxLengthTag = "test1_to_check_how_max_characters_are_allowed_before_you_get_the_message_stating_that_you_need_to_sh";
	public static String Follow_Wiki_Page_Message = "You are following this page.";

	public static String LIKE_YOUR_WIKI_PAGE = "USER" + " liked your wiki page PLACEHOLDER in the REPLACE_THIS wiki.";
	public static String LIKE_WIKI_PAGE_YOU = "You liked the wiki page PLACEHOLDER in the REPLACE_THIS wiki.";
	public static String LIKE_YOUR_WIKI_PAGE_YOU = "You liked the wiki page PLACEHOLDER in your REPLACE_THIS wiki.";

	public static String LIKE_WIKI_PAGE_TWO_LIKES = "USER" + " and PLACEHOLDER liked the wiki page REPLACE_THIS in the REPLACE_THIS_TOO wiki.";
	public static String LIKE_YOUR_WIKI_PAGE_TWO_LIKES = "USER" + " and PLACEHOLDER liked your wiki page REPLACE_THIS in the REPLACE_THIS_TOO wiki.";
	public static String LIKE_WIKI_PAGE_YOU_OTHER = "USER" + " and you liked the wiki page PLACEHOLDER in the REPLACE_THIS wiki.";
	public static String LIKE_YOUR_WIKI_PAGE_YOU_OTHER = "USER" + " and you liked your wiki page PLACEHOLDER in the REPLACE_THIS wiki.";

	public static String LIKE_WIKI_PAGE_MANY = "USER" + " and PLACEHOLDER others liked the wiki page REPLACE_THIS in the REPLACE_THIS_TOO wiki.";
	public static String LIKE_YOUR_WIKI_PAGE_MANY = "USER" + " and PLACEHOLDER others liked your wiki page REPLACE_THIS in the REPLACE_THIS_TOO wiki.";
	public static String LIKE_WIKI_PAGE_YOU_MANY = "You and PLACEHOLDER others liked the wiki page REPLACE_THIS in the REPLACE_THIS_TOO wiki.";
	public static String LIKE_YOUR_WIKI_PAGE_YOU_MANY = "You and PLACEHOLDER others liked your wiki page REPLACE_THIS in the REPLACE_THIS_TOO wiki.";
	public static String LIKE_THEIR_OWN_WIKI_PAGE = "USER" + " liked their own wiki page PLACEHOLDER in the REPLACE_THIS wiki.";
	
	public static String COMMENT_YOUR_WIKI_PAGE = "USER" + " commented on your wiki page PLACEHOLDER in the REPLACE_THIS wiki.";
	public static String COMMENT_WIKI_PAGE_YOU = "You commented on the wiki page PLACEHOLDER in the REPLACE_THIS wiki.";
	public static String COMMENT_YOUR_WIKI_PAGE_YOU = "You commented on your wiki page PLACEHOLDER in the REPLACE_THIS wiki.";
	public static String COMMENT_BY_USER_THEIR_OWN_WIKI_PAGE = "USER" + " commented on their own wiki page PLACEHOLDER in the REPLACE_THIS wiki.";
	
	public static String COMMENT_WIKI_PAGE_TWO_COMMENTERS = "USER" + " and PLACEHOLDER commented on the wiki page REPLACE_THIS in the REPLACE_THIS_TOO wiki.";
	public static String COMMENT_YOUR_WIKI_PAGE_TWO_COMMENTERS = "USER" + " and PLACEHOLDER commented on your wiki page REPLACE_THIS in the REPLACE_THIS_TOO wiki.";
	public static String COMMENT_WIKI_PAGE_YOU_OTHER = "USER" + " and you commented on the wiki page PLACEHOLDER in the REPLACE_THIS wiki.";
	public static String COMMENT_YOUR_WIKI_PAGE_YOU_OTHER = "USER" + " and you commented on your wiki page PLACEHOLDER in the REPLACE_THIS wiki.";

	public static String COMMENT_WIKI_PAGE_MANY = "USER" + " and PLACEHOLDER others commented on the wiki page REPLACE_THIS in the REPLACE_THIS_TOO wiki.";
	public static String COMMENT_YOUR_WIKI_PAGE_MANY = "USER" + " and PLACEHOLDER others commented on your wiki page REPLACE_THIS in the REPLACE_THIS_TOO wiki.";
	public static String COMMENT_WIKI_PAGE_YOU_MANY = "You and PLACEHOLDER others commented on the wiki page REPLACE_THIS in the REPLACE_THIS_TOO wiki.";
	public static String COMMENT_YOUR_WIKI_PAGE_YOU_MANY = "You and PLACEHOLDER others commented on your wiki page REPLACE_THIS in the REPLACE_THIS_TOO wiki.";
	
	public static String UPDATE_THEIR_COMMENT_ON_THE_WIKIPAGE = "USER" + " updated their comment on the wiki page Re: PLACEHOLDER in the REPLACE_THIS wiki.";
	public static String UPDATE_THEIR_COMMENT_ON_THEIR_OWN_WIKIPAGE = "USER" + " updated their comment on their own wiki page Re: PLACEHOLDER in the REPLACE_THIS wiki.";
	public static String UPDATE_YOUR_WIKI_PAGE_COMMENT = "USER" + " updated your wiki page Re: PLACEHOLDER in the REPLACE_THIS wiki.";
	public static String UPDATE_THEIR_COMMENT_YOUR_WIKI_PAGE = "USER" + " updated their comment on your wiki page Re: PLACEHOLDER in the REPLACE_THIS wiki.";
	public static String UPDATE_YOUR_WIKI_PAGE_THEIR_COMMENT = "USER" + " updated their comment on their own wiki page Re: PLACEHOLDER in the REPLACE_THIS wiki.";
	public static String UPDATE_WIKI_PAGE_COMMENT_YOU = "You updated the wiki page Re: PLACEHOLDER in the REPLACE_THIS wiki.";
	public static String UPDATE_YOUR_WIKI_PAGE_COMMENT_YOU = "You updated your wiki page Re: PLACEHOLDER in the REPLACE_THIS wiki.";
	public static String UPDATE_YOUR_WIKI_PAGE_YOUR_COMMENT_YOU = "You updated your comment on your wiki page Re: PLACEHOLDER in the REPLACE_THIS wiki.";
	
	public static String UPDATE_WIKI_PAGE_COMMENT_TWO_EDITORS = "USER" + " and PLACEHOLDER updated the wiki page Re: REPLACE_THIS in the REPLACE_THIS_TOO wiki.";
	public static String UPDATE_YOUR_WIKI_PAGE_COMMENT_TWO_EDITORS = "USER" + " and PLACEHOLDER updated your wiki page Re: REPLACE_THIS in the REPLACE_THIS_TOO wiki.";
	public static String UPDATE_WIKI_PAGE_COMMENT_YOU_OTHER = "USER" + " and you updated the wiki page Re: PLACEHOLDER in the REPLACE_THIS wiki.";
	public static String UPDATE_YOUR_WIKI_PAGE_COMMENT_YOU_OTHER = "USER" + " and you updated your wiki page Re: PLACEHOLDER in the REPLACE_THIS wiki.";

	public static String UPDATE_WIKI_PAGE_COMMENT_MANY = "USER" + " and PLACEHOLDER others updated the wiki page Re: REPLACE_THIS in the REPLACE_THIS_TOO wiki.";
	public static String UPDATE_YOUR_WIKI_PAGE_COMMENT_MANY = "USER" + " and PLACEHOLDER others updated your wiki page Re: REPLACE_THIS in the REPLACE_THIS_TOO wiki.";
	public static String UPDATE_WIKI_PAGE_COMMENT_YOU_MANY = "You and PLACEHOLDER others updated the wiki page Re: REPLACE_THIS in the REPLACE_THIS_TOO wiki.";
	public static String UPDATE_YOUR_WIKI_PAGE_COMMENT_YOU_MANY = "You and PLACEHOLDER others updated your wiki page Re: REPLACE_THIS in the REPLACE_THIS_TOO wiki.";

	//Comments
	public String LongComment = "this is the rest of the long comment and then there will kjdsflkdsj kldsj flkdsjflkd jlkdjf lkdjflkdsjf lkdjsf lkjdslkfj lkdsjf lkdsjfl kdjflkdsj flkdsjf lkdsj flkjdslkfj dslkfjldskjf lkdjsflkjdsf lkdjsflkdsjf lkjds flkdsjflksjdflkdsj flkj dsflkj sdlkf jlkds jflkdsjf lkdsjf lkdsjf lks jdflkdsj flkjds and then even more text now and this should be nearly eno hdskjfh dsakj hflkjdsa h hsadlkjf haslkjd hflkjsad hflkja hflkj flkjhadsfklj ha hfklj hdsfkljflkjsad hfkjads hkjdsa hfkj hflkjugh now ajkhdakjhfakjhf adkjads hkjfa flkdsj flksjdflksdjf lksjd flksdj flksjdflkdsjf";
	public String ReallyLongComment = "Although the default exception handler provided by the Java run-time system is useful for debugging, you will usually want to handle an exception yourself. Doing so provides two benefits. First, it allows you to fix the error. Second, it prevents the program from automatically terminating. Most users would be confused (to say the least) if your program stopped running and printed a stack trace whenever an error occurred! Fortunately, it is quite easy to prevent this. To guard against and handle a run-time error, simply enclose the code that you want to monitor inside a try block. Immediately following the try block, include a catch clause that specifies the exception type that you wish to catch. To illustrate how easily this can be done, the following program includes a try block and a catch clause which processes the ArithmeticException generated by the division-by-zero error: This program generates the following output: Division by zero. After catch statement. Notice that the call to println( ) inside the try block is never executed. Once an exception is thrown, program control transfers out of the try block into the catch block. Put differently, catch is not 'called,' so execution never 'returns' to the try block from a catch. Thus, the line 'This will not be printed.' is not displayed. Once the catch statement has executed, program control continues with the next line in the program following the entire try/catch mechanism. A try and its catch statement form a unit. The scope of the catch clause is restricted to those statements specified by the immediately preceding try statement. A catch statement cannot catch an exception thrown by another try statement (except in the case of nested try statements, described shortly). The statements that are protected by try must be surrounded by curly braces. (That is, they must be within a block.) You cannot use try on a single statement. The goal of most well-constructed catch clauses should be to resolve the exceptional condition and then continue on as if the error had never happened. For example, in the next program each iteration of the for loop obtains two random integers. Those two integers are divided by each other, and the result is used to divide the value 12345. The final result is put into a. If either division operation causes a divide-by-zero error, it is caught, the value of a is set to zero, and the program continues.";
	public String Verify_Comments_Functionality_Wiki = "Verify_Comments_Functionality";
	
	public String CommunityInviteMessage = "You have successfully invited the following people to this community: ";
	
	//Files
	public static String FILE_SHARED_WITH_COMM = "USER" + " shared a file with the community PLACEHOLDER.";
	public static String FILE_SHARED_WITH_COMM_ENTRY = "USER" + " shared the file PLACEHOLDER with the community REPLACE_THIS";
	public static String FILE_SHARED = "USER" + " shared a file with you.";
	public static String FILE_SHARED_BASIC = "USER" + " shared a file.";
	public static String FILE_SHARED_WITH_YOU_NOTIFICATION_CENTER = "USER" + " shared the file PLACEHOLDER with you.";
	public static String FILE_COMMENTED = "USER" + " commented on a file.";
	public static String FILE_COMMENTED_YOU = "You commented on a file.";
	public static String FILE_COMMENTED_OWN_FILE = "USER" + " commented on their own file.";
	public static String FILE_COMMENTED_YOUR_FILE = "USER" + " commented on your file.";
	public static String FILE_UPLOADED = "USER" + " uploaded a file.";
	public static String FILE_UPLOADED_THE_FILE = "USER" + " uploaded the file PLACEHOLDER";
	public static String FILE_EDITED = "USER" + " edited a file.";
	public static String FILE_EDITED_ENTRY = "USER" + " edited the file PLACEHOLDER.";
	public static String FILE_LIKE = "USER" + " liked a file.";
	public static String FILE_ADDED_TO_FOLDER = "USER" + " added a file to the folder PLACEHOLDER";
	public static String FILE_UPDATED = "USER" + " updated a file.";
	public static String FILE_UPDATED_THEIR_OWN_FILE = "USER" + " updated their own PLACEHOLDER file.";
	public static String FILE_PINNED = "USER" + " pinned a file.";
	public static String FOLDER_CREATED = "USER" + " created the folder PLACEHOLDER";
	public static String FILE_LIKE_THEIR_OWN_FILE = "USER" + " liked their own file.";
	public static String FOLDER_MADE_EDITOR_FOR_ME = "You were made an editor of a folder.";
	public static String FOLDER_MADE_EDITOR_FROM_ME = "USER" + " was made an editor of a folder.";
	public static String FOLDER_MADE_READER_FOR_ME = "You were made reader of a folder.";
	public static String FOLDER_MADE_READER_FROM_ME = "USER" + " was made a reader of a folder";
	public static String FOLDER_MADE_OWNER_FOR_ME = "You were made owner of a folder.";
	public static String FOLDER_MADE_OWNER_FROM_ME = "USER" + " was made owner of a folder.";

	public static String RECOMMENDED_FILE = "USER" + " liked the file REPLACE_THIS.";
	public static String RECOMMENDED_YOUR_FILE = "USER" + " liked your file.";
	public static String RECOMMENDED_FILE_YOU = "You liked the file REPLACE_THIS.";
	public static String RECOMMENDED_YOUR_FILE_YOU = "You liked your file.";

	public static String RECOMMENDED_FILE_TWO_LIKES = "USER" + " and PLACEHOLDER liked the file REPLACE_THIS.";
	public static String RECOMMENDED_YOUR_FILE_TWO_LIKES = "USER" + " and PLACEHOLDER liked your file.";
	public static String RECOMMENDED_FILE_YOU_OTHER = "USER" + " and you liked the file REPLACE_THIS.";
	public static String RECOMMENDED_YOUR_FILE_YOU_OTHER = "USER" + " and you liked your file.";

	public static String RECOMMENDED_FILE_MANY = "USER" + " and PLACEHOLDER others liked the file REPLACE_THIS.";
	public static String RECOMMENDED_YOUR_FILE_MANY = "USER" + " and PLACEHOLDER others liked your file.";
	public static String RECOMMENDED_FILE_YOU_MANY = "You and PLACEHOLDER others liked the file REPLACE_THIS.";
	public static String RECOMMENDED_YOUR_FILE_YOU_MANY = "You and PLACEHOLDER others liked your file.";

	public static String COMMENT_FILE = "USER" + " commented on the file PLACEHOLDER.";
	public static String COMMENT_YOUR_FILE = "USER" + " commented on your file.";
	public static String COMMENT_FILE_YOU = "You commented on the file PLACEHOLDER.";
	public static String COMMENT_YOUR_FILE_YOU = "You commented on your file PLACEHOLDER.";
	public static String COMMENT_YOUR_FILE_YOU_NO_FILENAME = "You commented on your file.";
	public static String COMMENT_OWN_FILE_WITH_FILENAME = "USER" + " commented on their own file PLACEHOLDER.";

	public static String COMMENT_FILE_TWO_COMMENTERS = "USER" + " and PLACEHOLDER commented on the file REPLACE_THIS.";
	public static String COMMENT_YOUR_FILE_TWO_COMMENTERS = "USER" + " and PLACEHOLDER commented on your file.";
	public static String COMMENT_FILE_YOU_OTHER = "USER" + " and you commented on the file REPLACE_THIS.";
	public static String COMMENT_YOUR_FILE_YOU_OTHER = "USER" + " and you commented on your file.";

	public static String COMMENT_FILE_MANY = "USER" + " and PLACEHOLDER others commented on the file REPLACE_THIS.";
	public static String COMMENT_YOUR_FILE_MANY = "USER" + " and PLACEHOLDER others commented on your file.";
	public static String COMMENT_FILE_YOU_MANY = "You and PLACEHOLDER others commented on the file REPLACE_THIS.";
	public static String COMMENT_YOUR_FILE_YOU_MANY = "You and PLACEHOLDER others commented on your file.";

	public static String UPDATED_COMMENT_FILE = "USER" + " updated the PLACEHOLDER file.";
	public static String UPDATED_COMMENT_YOUR_FILE = "USER" + " updated your PLACEHOLDER file.";
	public static String UPDATED_COMMENT_FILE_YOU = "You updated the PLACEHOLDER file.";
	public static String UPDATED_COMMENT_YOUR_FILE_YOU = "You updated your PLACEHOLDER file.";

	public static String UPDATED_COMMENT_FILE_TWO_UPDATERS = "USER" + " and PLACEHOLDER updated the PLACEHOLDER file.";
	public static String UPDATED_COMMENT_YOUR_FILE_TWO_UPDATERS = "USER" + " and PLACEHOLDER updated your PLACEHOLDER file.";
	public static String UPDATED_COMMENT_FILE_YOU_OTHER = "USER" + " and you updated the PLACEHOLDER file.";
	public static String UPDATED_COMMENT_YOUR_FILE_YOU_OTHER = "USER" + " and you updated your PLACEHOLDER file.";

	public static String UPDATED_COMMENT_FILE_MANY = "USER" + " and PLACEHOLDER others updated the REPLACE_THIS file.";
	public static String UPDATED_COMMENT_YOUR_FILE_MANY = "USER" + " and PLACEHOLDER others updated your REPLACE_THIS file.";
	public static String UPDATED_COMMENT_FILE_YOU_MANY = "You and PLACEHOLDER others updated the REPLACE_THIS file.";
	public static String UPDATED_COMMENT_YOUR_FILE_YOU_MANY = "You and PLACEHOLDER others updated your REPLACE_THIS file.";
	
	public static String FILE_PREVIEW_NOT_AVAILABLE = "Preview currently not available";

	//File Viewer
	public static String FILEVIEWER_ABOUTTHISFILE = "About This File";
	public static String FILEVIEWER_COMMENTS = "Comments";
	public static String FILEVIEWER_SHARED_WITH = "Shared With";
	public static String FILEVIEWER_VERSIONS = "Versions";
	public static String FILEVIEWER_SIZE = "File Size";
	public static String FILEVIEWER_CREATED = "Created";
	public static String FILEVIEWER_CURRENTVERSION = "Current Version" ;
	public static String FILEVIEWER_COMMENTITEM = "CommentItem" ;
	public static String TypeAheadSelectorValueEveryone = "everyone";
	public static String TypeAheadSelectorValueUser = "user";
	public static String TypeAheadSelectorValueCommunity = "community";
	public static String DisplayTextEveryoneInYourOrganization = "Everyone in your organization";
	

	
	//@Mentions
	public static String MENTIONED_YOU = "USER" + " mentioned you in a message.";
	public static String MENTIONED_YOU_COMMENT = "USER" + " mentioned you in a comment on USER's message."; 
	public static String MENTIONED_YOU_COMMENT_COMM = "USER" + " mentioned you in a comment on USER's message posted to the PLACEHOLDER community."; 
	public static String MENTIONED_YOU_MESSAGE_BOARD = "USER" + " mentioned you in a message posted to PLACEHOLDER.";
	public static String MENTIONED_YOU_COMMUNITY = "USER" + " mentioned you in a message posted to the PLACEHOLDER community.";
	public static String MENTIONED_YOU_BOARD_MESSAGE_COMMENT = "USER" + " mentioned you in a comment on USER's message posted to PLACEHOLDER";
	public static String MENTIONED_YOU_ACTIVITY_ENTRY = "USER" + " mentioned you in the PLACEHOLDER entry in the REPLACE_THIS activity.";
	public static String MENTIONED_YOU_ACTIVITY_ENTRY_COMMENT = "USER" + " mentioned you in a comment on the PLACEHOLDER entry thread in the REPLACE_THIS activity.";
	public static String MENTIONED_YOU_TODO_ITEM = "USER" + " mentioned you in the to-do item named PLACEHOLDER in the REPLACE_THIS activity.";
	public static String MENTIONED_YOU_BLOG_ENTRY = "USER" + " mentioned you in a blog entry named PLACEHOLDER in the REPLACE_THIS blog.";
	public static String MENTIONED_YOU_BLOG_ENTRY_COMMENT = "USER" + " mentioned you in a comment on PLACEHOLDER in the REPLACE_THIS blog.";
	public static String MENTIONED_YOU_FORUM_TOPIC = "USER" + " mentioned you in a topic named PLACEHOLDER in the REPLACE_THIS forum.";
	public static String MENTIONED_YOU_FORUM_TOPIC_REPLY = "USER" + " mentioned you in a reply to a topic named PLACEHOLDER in the REPLACE_THIS forum.";
	public static String MENTIONED_YOU_EVENT = "USER" + " mentioned you in the event PLACEHOLDER in the REPLACE_THIS community.";
	public static String MENTIONED_YOU_EVENT_COMMENT = "USER" + " mentioned you in a comment on the event PLACEHOLDER in the REPLACE_THIS community.";
	public static String MENTIONED_YOU_FILE_COMMENT = "USER" + " mentioned you in a comment on a file.";
	public static String MENTIONED_YOU_IDEA = "USER" + " mentioned you in the PLACEHOLDER idea in the REPLACE_THIS Ideation Blog."; 
	public static String MENTIONED_YOU_IDEA_COMMENT = "USER" + " mentioned you in a comment on PLACEHOLDER in the REPLACE_THIS Ideation Blog.";
	public static String MENTIONED_YOU_WIKIPAGE = "USER" + " mentioned you in the wiki page PLACEHOLDER in the REPLACE_THIS wiki."; 
	public static String MENTIONED_YOU_WIKIPAGE_COMMENT = "USER" + " mentioned you in a comment on the wiki page Re: PLACEHOLDER in the REPLACE_THIS wiki.";
	public static String SAVEDVIEW_MENTION_EVENT = "USER" + " mentioned PLACEHOLDER in a message.";
	public static String SAVED_EVENT_COMMNITY_INVITE = "USER" + " invited PLACEHOLDER to join the REPLACE_THIS community.";
	
	//Microblogging
	public static String POSTED_A_MESSAGE_GENERIC = "USER" + " posted a message.";
	public static String ADD_COMMUNITY_STATUS_UPDATE = "USER" + " posted a message to the PLACEHOLDER community.";
	public static String COMMUNITY_STATUS_UPDATE_COMMENT_SAME_USER = "USER" + " commented on their own message in the PLACEHOLDER community.";
	public static String BOARD_MESSAGE_OTHER_USER = "USER" + " posted a message to PLACEHOLDER.";
	public static String BOARD_MESSAGE_TO_YOU = "USER" + " posted a message to you.";
	public static String COMMENT_BOARD_MESSAGE_OTHER_USER = "USER" + " commented on the message posted to PLACEHOLDER.";
	public static String COMMENT_STATUSUPDATE_OTHER_USER = "USER" + " commented on PLACEHOLDER's message.";
	public static String COMMENT_BOARD_MESSAGE_RECIPIENT_USER_COMMENT = "USER" + " commented on PLACEHOLDER's message posted to REPLACE_THIS";
	//EE
	public static String EE_ADD_BLOG = "USER" + " added a blog to the PLACEHOLDER community.";
	public static String EE_CREATE_ENTRY = "USER" + " created a blog entry.";
	public static String EE_CREATE_FORUM = "USER" + " created a forum.";
	public static String EE_ADD_BOOKMARK = "USER" + " added a bookmark.";
	public static String EE_ADD_IDEATION_BLOG = "USER" + " added an Ideation Blog to a community.";
	public static String EE_CREATE_ACTIVITY = "USER" + " created an activity.";
	public static String EE_CREATE_TODO = "USER" + " created a to-do item.";
	public static String EE_UPDATE_TODO = "USER" + " updated a to-do item.";
	public static String EE_COMPLETE_TODO = "USER" + " completed a to-do item.";
	public static String EE_COMMENT_TODO = "USER" + " commented on an activity entry";
	public static String EE_UPDATE_COMMENT_ACTIVITY_ENTRY = "USER" + " updated an activity entry";
	public static String EE_CREATE_ACTIVITY_ENTRY = "USER" + " created an activity entry.";
	public static String EE_UPDATE_ACTIVITY_ENTRY = "USER" + " updated an activity entry.";
	public static String EE_COMMENT_ACTIVITY_ENTRY = "USER" + " commented on an activity entry.";
	
	//Visitor Model
	public static String SharedExternallyMsg = "Shared externally";
	public static String VisitorModel_CommentWarningMsg = "Comments might be seen by people external to your organization.";
	
	//URL navigation
	public static String Community_StatusUpdates_URLSuffix = "&filter=status";
	public static String CommunityURL_startText = "start";
	public static String CommunityURL_updatesText = "/updates";
	public static String User_profilePage = "profiles/html/profileView.do?userid=";
	
	//WidgetFramework
    public String WF_CommunityName = "WF Test Community";
    public String SearchResultsPageTitle = "Search Results - All Content";
    
    public String ApproveSuccessMsg = "approved successfully";
    public String HomepageMyNotificationsNewTab_OnPrem = "IBM Connections Home Page - My Notifications";
    public String HomepageMyNotificationsNewTab_SC = "Homepage - My Notifications"; 
    
    //BSS admin page
    public String ComponentBSS = "/manage/account/user/input";    
	public String JabberOptionText = "Jabber Chat";
	public String WebExOptionText = "WebEx Meetings";
	public String SparkOptionText = "Spark Chat";
	public String ChatAndMeetingsSaveSuccessAckText = "Changes saved successfully.";
	
	//BSS Integrated Third-party Apps page
	public String IntegratedThirdPartyAppsDescription = "View the list of available integrated applications and enable them for your organization's account.";
	public String IntegratedThirdPartyAppsEnabledAck = "The application is enabled for your organization.";
	public String IntegratedThirdPartyAppsDisabledAck = "The application has been cancelled for all users in your organization.";
	
	//Chat and meetings page
	public String CiscoJabberEnabledAck = "Jabber Chat is enabled.";
	public String CiscoJabberSaveToDisableMsg = "Choose save to finish disabling Jabber Chat.";
	public String CiscoJabberDisabledAck = "Jabber Chat is disabled.";
	public String CiscoSparkEnabledAck = "Spark Chat is enabled.";

	//Jabber Chat App Registry
	public String CiscoJabberAppId = "com.cisco.jabber";
	public String CiscoJabberBizcardExtensionId = "com.cisco.jabber.bizcard.chat";

	//Stop Survey view from Recent Updates
	public static String STOP_SURVEY = "USER" + " stopped the survey PLACEHOLDER in REPLACE_THIS.";
	
	//Check Responses tab for Top-updates
	public static String INVITE_TEXT_RESPONSES = "User" + " invited you to join their network";	
}
