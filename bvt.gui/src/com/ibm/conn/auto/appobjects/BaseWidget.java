package com.ibm.conn.auto.appobjects;

import com.ibm.conn.auto.util.menu.Widget_Action_Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;


public enum BaseWidget{

	
		ACTIVITIES("Activities", 
				"Activities",
				"Activities",
				"Working with community activities", 
				"Working with community activities",
				"col2",
				"Track community goals."),
		BLOG("Blog", 
				"Blog",
				"Blog",
				"Working with a community blog", 
				"Working with a community blog", 
				"col1",
				"Share your news and views."),
		IDEATION_BLOG("Ideation Blog",
				"Ideation Blog",
				"Ideation Blog",
				"Working with an Ideation Blog", 
				"Working with an Ideation Blog", 
				"col2",
				"Tap into the experience, knowledge and creativity of your community."),
		EVENTS("Events",
				"Calendar",
				"Upcoming Events",
				"Scheduling community events",
				"Scheduling community events", 
				"col3",
				"There are no upcoming events"),
		RELATED_COMMUNITIES("Related Communities", 
				"Related Communities",
				"Related Communities",
				"Sharing related communities with other members", 
				"Sharing related communities with other members",
				"col3",
				"Share links and see what's happening in related communities."),
		SUBCOMMUNITIES("SubcommunityNav", 
				"SubcommunityNav",
				"Subcommunities",
				"Creating subcommunities",
				"Creating subcommunities", 
				"col2",
				"Create subcommunities within this community."),
		FEEDS("Feeds",
				"Feeds",
				"Feeds",
				"Using feeds", 
				"Using feeds",
				"col2",
				"Stay current with up-to-minute information."),
		FILES("Files", 
				"Files",
				"Files",
				"Adding files to a community",
				"Working with community files", 
				"col2",
				"There are no files for this community."),
		FORUM("Forums", 
				"Forum",
				"Forums",
				"Working with community forums",
				"Working with community forums", 
				"col2",
				"Ask a question, brainstorm, or simply share your ideas."),
		BOOKMARKS("Bookmarks", 
				"Bookmarks",
				"Bookmarks",
				"Adding bookmarks to a community", 
				"Bookmarking important places",
				"col2",
				"Share Web resources with your community."),
		MEDIA_GALLERY("Media Gallery",
				"Media Gallery",
				"Media Gallery",
				"Using a media gallery", 
				"Using a media gallery",
				"col3",
				""),
		GALLERY("Gallery",
				"Gallery",
				"Gallery",
				"Working with galleries",
				"Working with galleries", 
				"col3",
				"Galleries allow you to showcase files from a specific folder."),
		WIKI("Wiki", 
				"Wiki",
				"Wiki",
				"Working with a community wiki",
				"Working with a community wiki", 
				"col1",
				"Create a Wiki Page"),
		LIBRARY("Library",
				"Library",
				"Library",
				"Using libraries",
				"Using libraries", 
				"col2",
				""),
		SURVEYS("Surveys", 
				"Surveys",
				"Surveys",
				"Conducting community surveys",
				"Conducting community surveys", 
				"col2",
				"There are no surveys currently available for this community."),
		MEMBERS("Members", 
				"Members", 
				"Members", 
				"Viewing members of a community",
				"How do I add community members?", 
				"col3",
				""),
		FEATUREDSURVEYS("Featured Survey", 
				"Featured Survey",
				"Featured Survey",
				"Conducting community surveys",
				"Conducting community surveys", 
				"col3",
				"Get started by creating a survey, or editing a draft."),
		RECOMENDATIONS("Recommendations",
				"Recommendations",
				"Recommendations",
				"Using the Recommendations widget in Communities",
				"Using the Recommendations widget in Communities",
				"col3",
				""),
		COMMUNITYDESCRIPTION("Community Description",
				"Community Description",
				"Community Description",
				"Editing communities",
				"Editing communities",
				"col2",
				""),
		IMPORTANTBOOKMARKS("Important Bookmarks",
				"Important Bookmarks",
				"Important Bookmarks",
				"Bookmarking important places",
				"Bookmarking important places",
				"col3",
				""),				
		TAGS("Tags",
			 "Tags",
			 "Tags",
		     "",
		     "",
		     "col1",
		     ""),
	    RICHCONTENT("Rich Content",
			    "Rich Content",
			    "Rich Content",
			    "Using the rich text editor",
			    "Using the rich text editor",
			    "banner",
			    "Craft rich content for your community. Post text, links, images and more."),
		SHAREPOINT_FILES(
				"unused widget title",
				"SharepointFiles",					// titleAPI, a.k.a. defId 		used when adding the Sharepoint Files widget to the test community
				"unused widget title on page",
				"unused widget help title onprem",
				"unused widget help title cloud",
				"col2",								// column, 						used when adding the Sharepoint Files widget to the test community
				"unused widget content"
				),
		HIGHLIGHTS(
				"Highlights",
				"Highlights",
				"Highlights",
				"",
				"",
				"col1",
				"Link to Highlights page"
				)		
		;
		
		
		String title = null;
		String titleAPI = null;
		String titleOnPage = null;
		String helpTitleOnprem = null;
		String helpTitleCloud = null;
		String content = null;

		String column = null;
	
		BaseWidget(String title, String titleAPI, String titleOnPage, String helpTitleOnprem, String helpTitleCloud, String column, String content){	
			this.title = title;
			this.titleAPI = titleAPI;
			this.titleOnPage = titleOnPage;
			this.helpTitleOnprem = helpTitleOnprem;
			this.helpTitleCloud = helpTitleCloud;
			this.column = column;
			this.content = content;
		}
		
		public String getTitle(){
			return this.title;
		}
		
		public String getTitleAPI(){
			return this.titleAPI;
		}
		
		public String getTitleOnPage() {
			return titleOnPage;
		}

		public String getHelpTitleOnprem(){
			return this.helpTitleOnprem;
		}

		public String getHelpTitleCloud() {
			return helpTitleCloud;
		}

		public String getColumn(){
			return this.column;
		}
		
		public String getContent(){
			return this.content;
		}
		
		public void preformMenuAction(Widget_Action_Menu action, CommunitiesUI ui){
			ui.performCommWidgetAction(this, action);
		}
		
		
};
