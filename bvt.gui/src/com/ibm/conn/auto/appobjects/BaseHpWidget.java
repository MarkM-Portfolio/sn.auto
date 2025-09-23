package com.ibm.conn.auto.appobjects;

import com.ibm.conn.auto.data.Data;

public enum BaseHpWidget {

		MYFILES("My Files",
					Data.getData().ComponentHPFiles,
					Data.getData().ComponentFilesKeyText,
					Data.getData().FilesMyWidgetHelp),
		FILESSHAREDWITHME("Files Shared with Me",
					Data.getData().ComponentHPFilesShared,
					Data.getData().ComponentFilesSharedKeyText,
					Data.getData().FilesSharedWidgetHelp),
		ACTIVITIES("Activities",
					Data.getData().ComponentHPActivities,
					Data.getData().ComponentActivitiesKeyText,
					Data.getData().ActivitiesWidgetHelp),
		MYACTIVITIES("My Activities",
					Data.getData().ComponentHPActivities,
					Data.getData().ComponentActivitiesKeyText,
					Data.getData().MyActivitiesWidgetHelp),
		PUBLICACTIVITIES("Public Activities",
					Data.getData().ComponentHPPublActivities,
					Data.getData().ComponentPublActivitiesKeyText,
					Data.getData().PublActivitiesWidgetHelp),
		BLOGS("Blogs",
					Data.getData().ComponentHPBlogs,
					Data.getData().ComponentBlogsKeyText,
					Data.getData().BlogsWidgetHelp),
		COMMUNITIES("Communities", 
					Data.getData().ComponentHPCommunities, 
					Data.getData().ComponentCommunitiesKeyText,
					Data.getData().CommunitiesWidgetHelp),
		MYCOMMUNITIES("My Communities",
					Data.getData().ComponentHPCommunitiesMy,
					Data.getData().ComponentCommunitiesMyKeyText,
					Data.getData().CommunitiesWidgetMyHelp),
		PUBLICCOMMUNITIES("Public Communities",
					Data.getData().ComponentHPCommunitiesPublic,
					Data.getData().ComponentCommunitiesPublicKeyText,
					Data.getData().CommunitiesWidgetPublicHelp),
		PROFILES("Profiles",
					Data.getData().ComponentHPProfiles,
					Data.getData().ComponentProfilesKeyText,
					Data.getData().ProfilesWidgetHelp),
		MYPROFILE("My Profile",
					Data.getData().ComponentHPProfiles,
					Data.getData().ComponentProfilesKeyText,
					Data.getData().ProfilesWidgetMyHelp),
		BOOKMARKS("Bookmarks", 
					Data.getData().ComponentHPBookmarks, 
					Data.getData().ComponentBookmarksKeyText,
					Data.getData().BookmarksWidgetHelp),
		FILES("My Files",
				Data.getData().ComponentHPFiles,
				Data.getData().ComponentMyFilesKeyText,
				Data.getData().FilesMyWidgetHelp),
		MYBOOKMARKS("My Bookmarks",
					Data.getData().ComponentHPBookmarks,
					Data.getData().ComponentBookmarksKeyText,
					Data.getData().MyBookmarksWidgetHelp),
		POPULARBOOKMARKS("Popular Bookmarks",
					Data.getData().ComponentHPBookmarksPopular,
					Data.getData().ComponentBookmarksPopularKeyText,
					Data.getData().BookmarksWidgetPopularHelp),
		RECENTBOOKMARKS("Recent Bookmarks",
					Data.getData().ComponentHPBookmarksRecent,
					Data.getData().ComponentBookmarksRecentKeyText,
					Data.getData().BookmarksWidgetRecentHelp),
		MYWIKIS("My Wikis",
					Data.getData().ComponentHPWikisMy,
					Data.getData().ComponentWikisMyKeyText,
					Data.getData().WikisWidgetMyHelp),
		LATESTWIKIS("Latest Wikis",
					Data.getData().ComponentHPWikisLatest,
					Data.getData().ComponentWikisLatestKeyText,
					Data.getData().WikisWidgetLatestHelp),
		POPULARWIKIS("Popular Wikis",
					Data.getData().ComponentHPWikisPopular,
					Data.getData().ComponentWikisPopularKeyText,
					Data.getData().WikisWidgetPopularHelp),
		MYNETWORK("My Network",
					Data.getData().ComponentHPProfilesNetwork,
					Data.getData().ComponentProfilesNetworkKeyText,
					Data.getData().ProfilesWidgetNetworkHelp),
		MYWATCHLIST("My Watchlist",
					Data.getData().ComponentHPBookmarksWatchlist,
					Data.getData().ComponentBookmarksWatchlistKeyText,
					Data.getData().BookmarksWidgetWatchlistHelp);

		
		String title = null;
		String popupWindowTitle = null;
		String popupBodyText = null;
		String helpText = null;
	
		BaseHpWidget(String title, String popupWindowTitle, String popupBodyText, String helpText){	
			this.title = title;
			this.popupWindowTitle = popupWindowTitle;
			this.popupBodyText = popupBodyText;
			this.helpText = helpText;
		
		}
		
		public String getTitle(){
			return this.title;
		}
		
		public String getPopupWindowTitle(){
			return this.popupWindowTitle;
		}
		
		public void setPopupWindowTitle(String popupWindowTitle){
			this.popupWindowTitle = popupWindowTitle;
		}
		
		public String getPopupBodyText() {
			return popupBodyText;
		}

		public void setPopupBodyText(String popupBodyText){
			this.popupBodyText = popupBodyText;
		}
		
		public String getHelpText(){
			return this.helpText;
		}
		
		
}
