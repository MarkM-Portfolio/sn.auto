package com.ibm.conn.auto.appobjects.base;


import java.util.ArrayList;
import java.util.List;

import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import org.testng.Assert;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseStateObject;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIFileHandler;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Mentions;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.lconn.automation.framework.services.blogs.nodes.BlogPost;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class BaseCommunity implements BaseStateObject{

	public enum communityFields {
		NAME,
		TAGS,
		DESCRIPTION;	
	}
	
	public enum Access {
		PUBLIC(CommunitiesUIConstants.CommunityAccessPublic),
		MODERATED(CommunitiesUIConstants.CommunityAccessModerated),
		RESTRICTED(CommunitiesUIConstants.CommunityAccessPrivate);
		
	    public String commType;
	    private Access(String level){
	            this.commType = level;
	    }
	    
	    @Override
	    public String toString(){
	            return commType;
	    }
	}
	
	public enum Theme {
		
		DEFAULT(CommunitiesUIConstants.ThemeDefault),
		RED(CommunitiesUIConstants.ThemeRed),
		GREEN(CommunitiesUIConstants.ThemeGreen),
		COFFEE(CommunitiesUIConstants.ThemeCoffee),
		SILVER(CommunitiesUIConstants.ThemeSilver),
		BLUE(CommunitiesUIConstants.ThemeBlue),
		PINK(CommunitiesUIConstants.ThemePink),
		ONYX(CommunitiesUIConstants.ThemeOnyx),
		PURPLE(CommunitiesUIConstants.ThemePurple),
		ORANGE(CommunitiesUIConstants.ThemeOrange);
		
	    public String themeLink;
	    private Theme(String color){
	            this.themeLink = color;
	    }
	    
	    @Override
	    public String toString(){
	            return themeLink;
	    }
		
	}
	
	public enum StartPage {
		
		OVERVIEW("Overview"),
		BOOKMARKS("Bookmarks"),
		FORUMS("Forums"),
		RECENTUPDATES("Recent Updates"),
		STATUSUPDATES("Status Updates");
		
	    public String action;
	    
	    private StartPage(String action){
	            this.action = action;
	    }
	    
		public String getMenuItemText(){
			return this.action;
		}
	}
	
	public enum StartPageApi {

		OVERVIEW(""),
		RECENTUPDATES("RecentUpdates");

		public String action;

		private StartPageApi(String action){
			this.action = action;
		}
		
		public String toString() {
			return action;
		}
	}	
	
	private String name;
	private Access access;
	private String tags;
	private String commHandle;
	private List<Member> members = new ArrayList<Member>();
	private List<Member> exmembers = new ArrayList<Member>();
	private String webAddress;
	private String description;
	private String template;
	private StartPage startpage;
	private String communityUUID;
	private String tinyEditorFunctionalitytoRun;
	private boolean richContent;
	
	/**
	 * Cloud only check box that is only clickable if 
	 * the community is restricted 
	 */
	private boolean shareOutside = true;
	
	/**
	 * restricted but listed
	 */
	private boolean rbl = false;
	
	/**
	 * Path to the community image
	 */
	private String communityImage;
	private Theme theme;
	
	/**
	 * OnPrem only to determine if the check box 
	 * which is only clickable if the community is 
	 * resctricted and additionally.
	 * Only if the user has the permissions to create a restricted community with 
	 * external user access as not all users have this permission 
	 * within the OnPrem Connections deployments. 
	 */
	private boolean allowExternalUserAccess = false;
	
	/**
	 * Moderator must approve content (only for moderated servers)
	 */
	private boolean approvalRequired;
	
	/**
	 * Viewers can flag inappropriate content (only for moderated servers)
	 */
	private boolean enableContentFlagging;
	

	public static class Builder {
		private String name;
		private Access access = Access.PUBLIC;
		private String tags;
		private List<Member> members = new ArrayList<Member>();
		private List<Member> exmembers = new ArrayList<Member>();
		private String webAddress;
		private String description;
		private String commHandle;
		private String communityImage;
		private Theme theme = null;
		private boolean approvalRequired = false;
		private boolean rbl = false;
		private boolean enableContentFlagging = false;
		private StartPage startpage = StartPage.OVERVIEW;
		private boolean shareOutside = true;
		private boolean allowExternalUserAccess = false;
		private String tinyEditorFunctionalitytoRun;
		private boolean richContent=false;
		private String template;
		
		public Builder tinyEditorFunctionalitytoRun(String functionality)
		{
			this.tinyEditorFunctionalitytoRun= functionality;
			return this;
		}
		
		public Builder isRichContent(boolean flag)
		{
			this.richContent= flag;
			return this;
		}
		
		public Builder(String name){
			this.name = name;
		}
		
		public Builder access(Access access) {
			this.access = access;
			return this;
		}
		
		public Builder tags(String tags){
			this.tags = tags;
			return this;
		}
		
		public Builder addMembers(List<Member> members) {
			this.members.addAll(members);
			return this;
		}
		
		public Builder addMember(Member member) {
			this.members.add(member);
			return this;
		}
		
		public Builder addexMembers(List<Member> exmembers) {
			this.exmembers.addAll(exmembers);
			return this;
		}
		
		public Builder addexMember(Member exmembers) {
			this.exmembers.add(exmembers);
			return this;
		}
				
		public Builder webAddress(String webAddress) {
			this.webAddress = webAddress;
			return this;
		}
		
		public Builder description(String description) {
			this.description = description;
			return this;
		}	
		
		public Builder template(String template) {
			this.template = template;
			return this;
		}
		
		public Builder commHandle(String handle) {
			this.commHandle = handle;
			return this;
		}
		
		
		public Builder communityImage(String imagePath){
			this.communityImage = imagePath;
			return this;
		}
		
		public Builder theme(Theme theme) {
			this.theme = theme;
			return this;
		}
		
		public Builder approvalRequired(boolean approvalRequired) {
			this.approvalRequired = approvalRequired;
			return this;
		}
		
		public Builder enableContentFlagging(boolean enableContentFlagging) {
			this.enableContentFlagging = enableContentFlagging;
			return this;
		}
		
        // does not work on-premise
		public Builder startPage(StartPage startpage){
			this.startpage = startpage;
			return this;
		}
		public Builder shareOutside(boolean shareOutside){
			this.shareOutside = shareOutside;
			return this;
		}
		
		public Builder rbl(boolean rbl) {
			this.rbl = rbl;
			return this;
		}
		
		
		public BaseCommunity build() {
			return new BaseCommunity(this);
		}
		
		public Builder allowExternalUserAccess(boolean allowExternalUserAccess){
			this.allowExternalUserAccess = allowExternalUserAccess;
			return this;
		}
	}
	
	private BaseCommunity(Builder b) {
		this.setName(b.name);
		this.setAccess(b.access);
		this.setTags(b.tags);
		this.setMembers(b.members);
		this.setexMembers(b.exmembers);
		this.setWebAddress(b.webAddress);
		this.setDescription(b.description);
		this.setTemplate(b.template);
		this.setTinyEditorFunctionalitytoRun(b.tinyEditorFunctionalitytoRun);
		this.setRichContent(b.richContent);
		this.setHandle(b.commHandle);
		this.setCommunityImage(b.communityImage);
		this.setTheme(b.theme);
		this.setApprovalRequired(b.approvalRequired);
		this.setEnableContentFlagging(b.enableContentFlagging);
		this.setStartPage(b.startpage);
		this.setShareOutside(b.shareOutside);
		this.setExternalUserAccess(b.allowExternalUserAccess);
		this.setRbl(b.rbl);
	}

	public void setTinyEditorFunctionalitytoRun(String functionality)
	{
		this.tinyEditorFunctionalitytoRun = functionality;
	}

	public void setRichContent(boolean flag)
	{
		this.richContent= flag;

	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Access getAccess() {
		return access;
	}

	public void setAccess(Access access) {
		this.access = access;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public List<Member> getMembers() {
		return members;
	}

	public void setMembers(List<Member> members) {
		this.members = members;
	}
	
	public List<Member> getexMembers() {
		return exmembers;
	}

	public void setexMembers(List<Member> exmembers) {
		this.exmembers = exmembers;
	}
	
	public void setHandle(String commHandle) {
		this.commHandle = commHandle;
	}

	public String getHandle() {
		return commHandle;
	}
	
	public String getWebAddress() {
		return webAddress;
	}

	public void setWebAddress(String webAddress) {
		this.webAddress = webAddress;
	}

	public String getDescription() {
		return description;
	}
	
	public String getTemplate() {
		return template;
	}
	
	public String getTinyEditorFunctionalitytoRun() {
		return tinyEditorFunctionalitytoRun;
	}
	
	public boolean getRichContent() {
		return richContent;
	}	
	
	public void setDescription(String description) {
		this.description = description;
	}

	public void setTemplate(String template) {
		this.template = template;
	}
	
	public String getCommunityImage() {
		return communityImage;
	}

	public void setCommunityImage(String communityImage) {
		this.communityImage = communityImage;
	}

	public Theme getTheme() {
		return theme;
	}

	public void setTheme(Theme theme) {
		this.theme = theme;
	}

	public boolean isApprovalRequired() {
		return approvalRequired;
	}

	public void setApprovalRequired(boolean approvalRequired) {
		this.approvalRequired = approvalRequired;
	}

	public boolean isEnableContentFlagging() {
		return enableContentFlagging;
	}

	public void setEnableContentFlagging(boolean enableContentFlagging) {
		this.enableContentFlagging = enableContentFlagging;
	}

	public StartPage getStartPage() {
		return startpage;
	}

	public void setStartPage(StartPage startpage) {
		this.startpage = startpage;
	}

	public boolean getShareOutside(){
		return shareOutside;
	}
	
	public void setShareOutside(boolean shareOutside){
		this.shareOutside = shareOutside;
	}
	
	public boolean getExternalUserAccess(){
		return allowExternalUserAccess;
	}
		
	public boolean setExternalUserAccess(){
		return allowExternalUserAccess;
	}
		
	public void setExternalUserAccess(boolean allowExternalUserAccess){
		this.allowExternalUserAccess = allowExternalUserAccess;
	}
	
	public boolean getRbl() {
		return rbl;
	}
	
	public void setRbl(boolean rbl) {
		this.rbl = rbl;
	}

	public String getCommunityUUID() {
		return communityUUID;
	}

	public void setCommunityUUID(String communityUUID) {
		this.communityUUID = communityUUID;
	}
		
	public void create(CommunitiesUI ui) {
		ui.create(this);
	}
	
	public void createFromDropDown(CommunitiesUI ui) {
		ui.createFromDropDown(this);
	}
	
	/**
	 * This method is to create community from Tailored Experience Widget
	 * @param ui
	 */
	public void createCommunityFromTailoredExperienceWidget(CommunitiesUI ui, DefectLogger logger) {
		ui.createCommunityFromTailoredExperienceWidget(this, logger);
	}
	
	public String verifyTinyEditor(CommunitiesUI ui)  {
		String TEText = ui.verifyTinyEditor(this);
		return TEText;
	}
	
	public void createFromDropDownCardView(CommunitiesUI ui) {
		ui.createFromDropDownCardView(this);
	}

	public String getCommunityUUID_API(APICommunitiesHandler apiOwner, Community comAPI) {
		String UUID = apiOwner.getCommunityUUID(comAPI);
		this.setCommunityUUID(UUID);
		return UUID;
	}
	
	public Community createAPI(APICommunitiesHandler apiOwner) {
		Community community = apiOwner.createCommunity(this);
		
		Assert.assertTrue(community != null, "Failed to add community using API.");		
		return community;
	}
	
	public BlogPost createBlogEntryMentionsAPI(APICommunitiesHandler apiOwner, BaseBlogPost newBaseBlogPost, Community community, Mentions mentions){
		return apiOwner.createCommunityBlogEntryMentions(newBaseBlogPost, apiOwner.getCommunity(community), mentions);
	}

	public void delete(CommunitiesUI ui, User userDeleting) {	
		try {
			ui.delete(this, userDeleting);
		} catch (Exception e) {	
			e.printStackTrace();
		}
	}
	
	public void follow(CommunitiesUI ui) {
		ui.follow(this);
	}
	
	public void followAPI(Community community, APICommunitiesHandler apiFollower, APICommunitiesHandler apiOwner){
		apiFollower.followCommunity(apiOwner.getCommunity(community));
	}
	
	public void addFileAPI(Community community, BaseFile file1, APICommunitiesHandler apiOwner ,APIFileHandler fileHandler){	
		boolean uploaded =  apiOwner.uploadFile(apiOwner.getCommunity(community), file1, fileHandler.getService());
		Assert.assertTrue(uploaded, "Failed to upload file to community using API.");
		
	}
	
	public void addWidget(CommunitiesUI ui, BaseWidget widget){
		ui.addWidget(widget);
	}
	
	public void addWidgetAPI(Community community, APICommunitiesHandler apiOwner, BaseWidget widget){
			apiOwner.addWidget(community, widget);
		
	}

	public void navViaUUID(CommunitiesUI ui) {
		ui.navViaUUID(this);
	}
	
	public BlogPost createIdeaMentions(APICommunitiesHandler apiOwner, BaseBlogPost newPost, Community community, Mentions mentions){
		return apiOwner.createIdeaMentions(newPost, community, mentions);
	}
	
}
