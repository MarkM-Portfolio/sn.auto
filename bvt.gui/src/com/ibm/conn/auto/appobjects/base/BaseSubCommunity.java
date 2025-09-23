package com.ibm.conn.auto.appobjects.base;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseStateObject;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;

public class BaseSubCommunity implements BaseStateObject{
		
	public enum subCommunityField{
		NAME,
		TAGS,
		DESCRIPTION;	
	}
	
	public enum Access {
		PUBLIC(CommunitiesUIConstants.SubCommunityAccessPublic),
		MODERATED(CommunitiesUIConstants.SubCommunityAccessModerated),
		RESTRICTED(CommunitiesUIConstants.SubCommunityAccessPrivate),
		RESTRICTEDHIDDEN(CommunitiesUIConstants.communityTypeRestrictedHidden);
		
	    public String commType;
	    private Access(String level){
	            this.commType = level;
	    }
	    
	    @Override
	    public String toString(){
	            return commType;
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
	
	private String name;
	private String tags;
	private String commHandle;
	private List<Member> members = new ArrayList<Member>();
	private List<Member> exmembers = new ArrayList<Member>();
	private String webAddress;
	private String description;
	private String template;
	private StartPage startpage;
	private Access access;
	private boolean useActionMenu;
	private boolean useParentmembers;
	private String communityUUID;
	
	/**
	 * restricted but listed
	 */
	private boolean rbl = false;
	
	/**
	 * Path to the community image
	 */
	private String communityImage;
	
	/**
	 * Moderator must approve content (only for moderated servers)
	 */
	private boolean approvalRequired;
	
	/**
	 * Viewers can flag inappropriate content (only for moderated servers)
	 */
	private boolean enableContentFlagging;
	
	private Set<subCommunityField> edit_track = new HashSet<subCommunityField>();
	
	public static class Builder {
		private String name;
		private String tags;
		private List<Member> members = new ArrayList<Member>();
		private List<Member> exmembers = new ArrayList<Member>();
		private String webAddress;
		private String description;
		private String template;
		private String commHandle;
		private String communityImage;
		private boolean approvalRequired = false;
		private boolean enableContentFlagging = false;
		private StartPage startpage = StartPage.OVERVIEW;
		private Access access = Access.PUBLIC;
		private boolean rbl = false;
		private boolean useActionMenu = true;
		private boolean useParentMembers = false;
		
		public Builder(String name){
			this.name = name;
		}
		
		public Builder tags(String tags){
			this.tags = tags;
			return this;
		}
		
		public Builder access(Access access) {
			this.access = access;
			return this;
		}
		
		public Builder addMembers(List<Member> members) {
			this.members.addAll(members);
			return this;
		}
		
		public Builder addMember(Member members) {
			this.members.add(members);
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
		
		public Builder approvalRequired(boolean approvalRequired) {
			this.approvalRequired = approvalRequired;
			return this;
		}
		
		public Builder enableContentFlagging(boolean enableContentFlagging) {
			this.enableContentFlagging = enableContentFlagging;
			return this;
		}
		
		public Builder startPage(StartPage startpage){
			this.startpage = startpage;
			return this;
		}
		public Builder useActionMenu(boolean useActionMenu){
			this.useActionMenu = useActionMenu;
			return this;
		}
		
		public Builder UseParentmembers(boolean choice){
			this.useParentMembers = choice;
			return this;
		}
		
		public Builder rbl(boolean rbl) {
			this.rbl = rbl;
			return this;
		}
		
		public BaseSubCommunity build() {
			return new BaseSubCommunity(this);
		}
	}
	
	private BaseSubCommunity(Builder b) {
		this.setName(b.name);
		this.setTags(b.tags);
		this.setMembers(b.members);
		this.setexMembers(b.exmembers);
		this.setWebAddress(b.webAddress);
		this.setDescription(b.description);
		this.setTemplate(b.template);
		this.setHandle(b.commHandle);
		this.setCommunityImage(b.communityImage);
		this.setApprovalRequired(b.approvalRequired);
		this.setEnableContentFlagging(b.enableContentFlagging);
		this.setStartPage(b.startpage);
		this.setAccess(b.access);
		this.setUseActionMenu(b.useActionMenu);
		this.setUseParentmembers(b.useParentMembers);
		this.setRbl(b.rbl);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public String getCommunityUUID() {
		return communityUUID;
	}

	public void setCommunityUUID(String communityUUID) {
		this.communityUUID = communityUUID;
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
	
	public Access getAccess() {
		return access;
	}

	public void setAccess(Access access) {
		this.access = access;
	}

	public boolean getUseActionMenu(){
		return useActionMenu;
	}
	
	public void setUseActionMenu(boolean choice){
		this.useActionMenu = choice;
	}
	
	public boolean isUseParentMembers(){
		return useParentmembers;
	}
	
	public void setUseParentmembers(boolean choice){
		this.useParentmembers = choice;
	}
	
	public boolean getRbl() {
		return rbl;
	}
	
	public void setRbl(boolean rbl) {
		this.rbl = rbl;
	}

	public void create(CommunitiesUI ui) {
		ui.createSubCommunity(this);
	} 

	public void delete(CommunitiesUI ui, User userDeleting) {	
		try {
			ui.deleteSubCommunity(this, userDeleting);
		} catch (Exception e) {	
			e.printStackTrace();
		}
	}  

	public Set<subCommunityField> getEdits(){
		return edit_track;
	}
	
	/**
	 * This method is to create subcommunity from Tailored Experience Widget
	 * @param ui
	 */
	public void createSubCommunityFromTailoredExperienceWidget(CommunitiesUI ui, BaseCommunity community, DefectLogger logger) {
		ui.waitForCommunityLoaded();
		ui.createSubCommunityFromTailoredExperienceWidget(this, community, logger);
	}
	
}
