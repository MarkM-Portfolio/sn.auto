package com.ibm.conn.auto.data;

import com.ibm.lconn.automation.framework.services.common.StringGenerator;

public class metricsData {
	
	private boolean useSpecialChars;
	private StringGenerator sg = StringGenerator.getInstance();
	
	public metricsData(boolean useSpecialChars) {
		this.useSpecialChars = useSpecialChars;
		
		Loading = "Loading...";

		AccessDeniedMessage = "Access to Metrics is Restricted";
		MainView = "Connections";
		PeopleView = "People";
		ParticipationView = "Participation";
		ContentView = "Content";
		AllMetricsView = "All Metrics";
		
		ExportHeader = "Export Metrics";
		
		CommunityName = decorate("BVT Metrics Test Community");
		CommunityHandle = "Handle";
		
		GetStartedMessage = "To get started, Update Metrics to fetch the latest data.";
		UpdatingMessage = "Update request submitted. Your position in the queue is";
		NoMetricsMessage = "Metrics are captured each day and are not yet available for your community. Check back tomorrow to view metrics.";
		
		
		verifyPeopleText = "Number of unique authenticated visitors";
		verifyParticipationText = "Number of visits";
		verifyContentText = "Most followed content";
	}
	
	private String decorate(String text) {
		return sg.decorateSCAll(text, useSpecialChars);
	}
	
	public static String Loading = "Loading...";

	public static String AccessDeniedMessage;
	public static String MainView = "Connections";
	public static String PeopleView = "People";
	public static String ParticipationView = "Participation";
	public static String ContentView = "Content";
	public static String AllMetricsView = "All Metrics";
	public static String ParticipationViewPageTitle = "Participation Metrics";
	public static String PeopleViewPageTitle = "People Metrics";
	public static String ContentViewPageTitle = "Content Metrics";
	public static String OthersHeader = "Others";
	
	public static String[] ViewRangeList = {"Last 7 days", "Last 4 weeks", "Last quarter", "Last 12 months", "All years", "Custom"};
	public static String[] AppPeopleFiltersList = {"Activities", "Blogs", "Bookmarks", "Communities", "Files", "Forums", "Homepage", "Profiles", "Wikis"};
	public static String[] AppParticipationFiltersList = {"Activities", "Blogs", "Bookmarks", "Communities", "Files", "Forums", "Homepage", "Profiles", "Wikis"};
	public static String[] AppContentFiltersList = {"Activities", "Blogs", "Bookmarks", "Communities", "Files", "Forums", "Wikis"};
	public static String[] OtherFiltersList = {"All people", "Geography", "Role", "Department"};
	public static String[] TimeLineOptions_ES = {"Last 7 days", "Last 4 weeks", "Last quarter", "Last 12 months"};
	public static String[] LineChartHeaders = {"Total", "Average", "Change"};
	public static String[] LineChartDefaultAppFilterText ={"Community Overall Total", "Bookmarks", "Files", "Forums"};
	public static String[] LineChartDefaultAppFilterText_SC ={"Community Overall Total", "Bookmarks", "Files", "Forums", "Wikis"};
	public static String[] LineChartAppFilterTextAllAppsAdded = {"Community Overall Total", "Activities", "Blogs", "Bookmarks", "Files", "Forums","Wikis", "Ideation Blog"};
	public static String[] ViewAllMetricsDefaultSections = {"Bookmarks", "Files", "Forums", "Others"};
	public static String[] ViewAllMetricsDefaultSections_SC = {"Bookmarks", "Files", "Forums", "Wikis", "Others"};
	public static String[] ViewAllMetricsSectionsAllAppsAdded = {"Activities","Blogs","Bookmarks","Files","Forums","Ideation Blog","Wikis","Others"};
	public static String[] ParticipationDefaultViewChartHeaders_GlobSC = {"Organization Overall Total","Activities","Blogs","Communities","Files","Forums","Wikis","Ideation Blog"};
	public static String[] ParticipationDefaultViewChartHeaders_GlobOP = {"Organization Overall Total","Activities","Blogs","Bookmarks","Communities","Files","Forums","Profiles","Wikis","Ideation Blog"};
	public static String[] PeopleDefaultViewChartHeaders_GlobSC = {"Organization Overall Total","Activities","Blogs","Communities","Files","Forums","Homepage","Wikis","Ideation Blog"};
	public static String[] PeopleDefaultViewChartHeaders_GlobOP = {"Organization Overall Total","Activities","Blogs","Bookmarks","Communities","Files","Forums","Homepage","Profiles","Wikis","Ideation Blog"};
	public static String[] BookmarksLinks = {"Number of new bookmarks","Number of new updates","Number of unique authenticated visitors","Number of visits"};
	public static String[] BookmarksLinks_Glob = {"Most active applications","Number of new bookmarks","Number of new updates","Number of unique authenticated visitors","Number of unique contributors","Number of visits"};
	public static String[] FilesLinks ={"Most active content","Number of new files","Number of new updates","Number of unique authenticated visitors","Number of unique contributors","Number of unique downloaded files","Number of unique people who downloaded files","Number of unique people who shared files","Number of visits"}; 
	public static String[] FilesLinks_Glob = {"Most active applications","Most active file libraries","Most active files","Most followed content","Number of new files","Number of new rejected items","Number of new updates","Number of unique authenticated visitors","Number of unique contributors","Number of unique downloaded files","Number of unique people who downloaded files","Number of unique people who shared files","Number of visits"};
	public static String[] ForumsLinks ={"Most active content","Number of new forum topics","Number of new topic replies","Number of new updates","Number of unique authenticated visitors","Number of unique contributors","Number of visits"};
	public static String[] ForumsLinks_Glob = {"Most active applications","Most active forums","Most followed content","Number of new forum topics","Number of new forums","Number of new rejected items","Number of new topic replies","Number of new updates","Number of unique authenticated visitors","Number of unique contributors","Number of visits"};
	public static String[] WikisLinks = {"Most active content","Number of new updates","Number of new wiki pages","Number of unique authenticated visitors","Number of unique contributors","Number of visits"};
	public static String[] WikisLinks_Glob = {"Most active applications","Most active wikis","Most followed content","Number of new updates","Number of new wiki pages","Number of new wikis","Number of unique authenticated visitors","Number of unique contributors","Number of visits"};
	public static String[] ActivitiesLinks = {"Most active content","Number of new activities","Number of new updates","Number of unique authenticated visitors","Number of unique contributors","Number of visits"};
	public static String[] ActivitiesLinks_Glob = {"Most active activities","Most active applications","Most followed content","Number of new activities","Number of new updates","Number of unique authenticated visitors","Number of unique contributors","Number of visits"};
	public static String[] BlogsLinks = {"Most active content","Number of new entries","Number of new entry comments","Number of new updates","Number of unique authenticated visitors","Number of unique contributors","Number of visits"};
	public static String[] BlogsLinks_Glob = {"Most active applications","Most active blogs","Most followed content","Number of new blogs","Number of new entries","Number of new entry comments","Number of new rejected items","Number of new updates","Number of unique authenticated visitors","Number of unique contributors","Number of visits"};
	public static String[] IdeationBlogLinks = {"Most active content","Number of new graduated ideas","Number of new ideas","Number of new updates","Number of unique authenticated visitors","Number of unique contributors","Number of visits"};
	public static String[] IdeationBlogLinks_Glob = {"Most active applications","Number of new graduated ideas","Number of new ideas","Number of new updates","Number of unique authenticated visitors","Number of unique contributors","Number of visits"};
	public static String[] OthersLinks={"Top Contributors"};
	public static String[] OthersLinks_Glob = {"Top Followed People"};
	public static String[] HomepageLinks_Glob = {"Number of unique authenticated visitors","Number of visits"};
	public static String[] CommunitiesLinks_Glob = {"Most active applications","Most active communities","Most followed content","Number of new status updates","Number of new updates","Number of unique authenticated visitors","Number of unique contributors","Number of visits"};
	public static String[] ProfilesLinks_Glob = {"Number of new status updates","Number of new updates","Number of unique authenticated visitors","Number of unique contributors","Number of unique followed people","Number of unique people who updated their profile","Number of visits"};
	public static String[] ContentView3ColTableHeaders = {"Title","Count","Author"};
	public static String[] ContentView2ColTableHeaders = {"Title","Count"};
	
	public static String[] componentsList = {"Wikis", "Communities", "Profiles", "Activities", "Homepage", "Blogs", "Forums", "Files", "Bookmarks"};
	
	public static String ExportHeader;
	
	public static String CommunityName;
	public static String CommunityHandle;
	
	public static String GetStartedMessage;
	public static String UpdatingMessage;
	public static String NoMetricsMessage = "Metrics are captured each day and are not yet available for your community. Check back tomorrow to view metrics.";;
	public static String verifyPeopleText = "Number of unique authenticated visitors";
	public static String verifyParticipationText = "Number of visits";
	public static String verifyContentText = "Most followed content";	
	public static String numberOfMembersWhoLeftCommText = "Number of members who left the community";	
	public static String dateRangeText = "(All dates and times in GMT)";
	public static String noMetricsDataMsg = "Metrics data may not be available for the time period.";
	public static String viewAllMetricsViewText = "This is a complete list of all the community metrics.";
	public static String viewAllMetricsViewText_Glob = "This is a complete list of all the Connections metrics.";
	public static String contentValueText = "Content Value is a rank of usage based on creates, reads, visits, likes, updates, follows. Showing top 20 results.";
	
	//View Names: 
	public static String numOfUniquePeopleSharedFiles = "Number of unique people who shared files";
	public static String numOfNewFiles = "Number of new files";
	public static String numOfPeopleFollowingComm = "Number of unique people following the community";
	public static String numOfNewForumTopics = "Number of new forum topics";
	public static String numOfNewUpdates = "Number of new updates";
	public static String numOfNewBookmarks = "Number of new bookmarks";
	public static String numOfUniqueContributors = "Number of unique contributors";
	public static String numOfUniquePeopleDownloadFiles = "Number of unique people who downloaded files";
	public static String numOfUniqueDownloadedFiles = "Number of unique downloaded files";
	public static String numOfNewTopicReplies = "Number of new topic replies";
	public static String numOfNewWikiPages = "Number of new wiki pages";
	public static String numOfNewMembers = "Number of new members";
	public static String numOfNewEntryComments = "Number of new entry comments";
	public static String numOfNewActivities = "Number of new activities";
	public static String numOfNewGraduatedIdeas = "Number of new graduated ideas";
	public static String numOfUniquePeopleWhoSharedFiles = "Number of unique people who shared files";
	public static String numOfNewEntries = "Number of new entries";
	public static String numOfNewIdeas = "Number of new ideas";
	public static String numOfNewRejectedItems = "Number of new rejected items";
	public static String numOfNewForums = "Number of new forums";
	public static String numOfNewStatusUpdates = "Number of new status updates";
	public static String numOfUniqueFollowedPeople = "Number of unique followed people";
	public static String numOfNewBlogs = "Number of new blogs";
	public static String numOfUniquePeopleWhoUpdatedProfile = "Number of unique people who updated their profile";
	public static String numOfNewWikis = "Number of new wikis";
	public static String mostActiveContent = "Most active content";
	public static String mostActiveCommunities = "Most active communities";
	public static String mostActiveApplications = "Most active applications";
	public static String mostActiveFiles = "Most active files";
	public static String mostActiveFileLibraries = "Most active file libraries";
	public static String mostActiveWikis = "Most active wikis";
	public static String mostFollowedContent = "Most followed content";
	public static String mostActiveForums = "Most active forums";
	public static String mostActiveBlogs = "Most active blogs";
	public static String mostActiveActivities = "Most active activities";
	public static String topContributors = "Top Contributors";

}
