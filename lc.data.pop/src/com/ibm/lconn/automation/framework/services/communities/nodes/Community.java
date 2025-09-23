package com.ibm.lconn.automation.framework.services.communities.nodes;

import java.util.List;

import org.apache.abdera.model.Category;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Link;

import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Permissions;
import com.ibm.lconn.automation.framework.services.common.nodes.LCEntry;

public class Community extends LCEntry {
	
	private Category isCommunity;
	private long lastVisitedDate;

	private Element snx_handle;			/** (Ignored) */
	private Element snx_membercount;	/** (Ignored) Contains the number of members of this community. */
	private Element snx_communityType;	/** (Required) Indicates whether the community is public or private. Options are: private, public, publicInviteOnly */
	private Element snx_listWhenPrivate;/** (Optional) Indicates whether the community summary is visisble to non-members in the org.  Default is false */
	private String Uuid = null;
	private Element snx_isExternal;		/** (optional) Used to allow external users.  Should only set to true for VModel deployments, private communities owned by users with extended.employee rights. */
	private Element snx_memberEmailPrivileges;	/** (optional) Used to block members from spamming a community.  Default is entireCommunity */
	private Element snx_copyFromCommunityUuid; /* (optional) Used to copy from an existing community UUID. Default is null. */
	private Element snx_startPage;      /** (optional) community start page */
	private Element snx_PreModeration;  /** (optional) community start page */
	
	public Community(String title, String content, Permissions communityType, String tagsString) {
		super();
		
		setIsCommunity(true);
		setTitle(title);
		setContent(content);
		setCommunityType(communityType);
		setTags(tagsString);
	}
	
	public Community(String title, String content, Permissions communityType, String tagsString, boolean isExternal) {
		super();
		
		setIsCommunity(true);
		setTitle(title);
		setContent(content);
		setCommunityType(communityType);
		setTags(tagsString);
		setIsExternal(isExternal);
	}

	public Community(String title, String content, Permissions communityType, String tagsString, boolean isExternal, String copyFromCommunityUuid) {
		this(title, content, communityType, tagsString, isExternal);
		setCopyFromCommunityUuid(copyFromCommunityUuid);
	}

	public Community(Entry entry) {
		super(entry);
		
		List<Category> types = entry.getCategories(StringConstants.SCHEME_TYPE);
		
		if(types.size() > 0) {
			String term = types.get(0).getTerm();
			if(term.equals(StringConstants.STRING_COMMUNITY_LOWERCASE)) {
				setIsCommunity(true);				
			}
		}
		
		List<Element> fieldElements = entry.getExtensions(StringConstants.FIELD_NAMESPACE);
		
		if (fieldElements != null) {
			for (Element fieldElement : fieldElements) {
				if (fieldElement.getAttributeValue("id").equals("FIELD_LAST_VISITED_DATE") ){
					setLastVisitedDate(Long.parseLong(fieldElement.getText()));
				}
			}
		}
		
		setHandle(entry.getExtension(StringConstants.SNX_HANDLE));
		setMemberCount(entry.getExtension(StringConstants.SNX_MEMBER_COUNT));
		setCommunityType((Element)entry.getExtension(StringConstants.SNX_COMMUNITY_TYPE));
		setListWhenPrivate(entry.getExtension(StringConstants.SNX_LIST_WHEN_RESTRICTED));
		setMemberEmailPrivileges(entry.getExtension(StringConstants.SNX_MEMBER_EMAIL_PRIVILEGES));
		// copyFromCommunityUuid not part of a GET response but used in a POST when copying from existing communities
		setCopyFromCommunityUuid((Element)entry.getExtension(StringConstants.SNX_COPY_FROM_COMMUNITY_UUID));
		
		Element commUuidExtension = entry.getExtension(StringConstants.SNX_COMMUNITY_UUID);
		if (commUuidExtension != null) {
			setUuid(commUuidExtension.getText());
		}
	}

	@Override
	public Entry toEntry() {
		Element[] extensions = { getHandleElement(), getMemberCountElement(), getCommunityTypeElement(), getListWhenPrivateElement(), getIsExternalElement(), getMemberEmailPrivileges(), getCopyFromCommunityUuid(), getStartPageElement(), getIsPreModerationElement()};

		Category[] categories = { getIsCommunityCategory() };

		return createBasicEntry(extensions, categories);
	}
	
	@Override
	public String getEditLink() {
		Link link = getLinks().get(StringConstants.REL_EDIT + ":" + StringConstants.MIME_NULL);
		return (link != null ? link.getHref().toString() : null);
	}

	public String getBookmarkHref() {
		Link link = getLinks().get(StringConstants.REL_BOOKMARKS + ":" + StringConstants.MIME_ATOM_XML);
		return (link != null ? link.getHref().toString() : null);
	}
	
	public String getWidgetHref() {
		Link link = getLinks().get(StringConstants.REL_WIDGETS + ":" + StringConstants.MIME_ATOM_XML);
		return (link != null ? link.getHref().toString() : null);
	}

	public String getFeedLinksHref() {
		Link link =  getLinks().get(StringConstants.REL_FEEDS + ":" + StringConstants.MIME_ATOM_XML);
		return (link != null ? link.getHref().toString() : null);
	}
	
	public String getSubcommunitiesHref() {
		Link link = getLinks().get(StringConstants.REL_SUBCOMMUNITIES + ":" + StringConstants.MIME_ATOM_XML);
		return (link != null ? link.getHref().toString() : null);
	}
	
	public String getParentcommunityHref() {
		Link link = getLinks().get(StringConstants.REL_PARENTCOMMUNITY + ":" + StringConstants.MIME_ATOM_XML);
		return (link != null ? link.getHref().toString() : null);
	}

	public String getMembersListHref() {
		Link link = getLinks().get(StringConstants.REL_MEMBERS + ":" + StringConstants.MIME_ATOM_XML);
		return (link != null ? link.getHref().toString() : null);
	}

	public String getForumTopicsLink() {
		Link link = getLinks().get(StringConstants.REL_FORUM_TOPICS + ":" + StringConstants.MIME_ATOM_XML);
		return (link != null ? link.getHref().toString() : null);
	}

	public String getInvitationsListLink() {
		Link link = getLinks().get(StringConstants.REL_INVITATIONS_LIST + ":" + StringConstants.MIME_ATOM_XML);
		return (link != null ? link.getHref().toString() : null);
	}
	
	public String getAlternateLink() {
		Link link = getLinks().get(StringConstants.REL_ALTERNATE + ":" + StringConstants.MIME_TEXT_HTML);
		return (link != null ? link.getHref().toString() : null);
	}	
	
	public String getRemoteAppsListHref() {
		Link link = getLinks().get(StringConstants.REL_REMOTEAPPLICATIONS + ":" + StringConstants.MIME_ATOM_XML);
		return (link != null ? link.getHref().toString() : null);
	}	
	
	/**
	 * @return the Atom category object that contains the isCommunity information.
	 */
	public Category getIsCommunityCategory() {
		return isCommunity;
	}
	
	public long getLastVisitedDate() {
		return lastVisitedDate;
	}

	public void setLastVisitedDate(long lastVisitedDate) {
		this.lastVisitedDate = lastVisitedDate;
	}

	/**
	 * @param isCommunity set the Atom category object that contains the isCommunity information.
	 */
	public void setIsCommunity(boolean isCommunity) {
		Category isCommunityCategory = null;
		
		if(isCommunity) {
			isCommunityCategory = getFactory().newCategory();
			isCommunityCategory.setScheme(StringConstants.SCHEME_TYPE);
			isCommunityCategory.setTerm(StringConstants.STRING_COMMUNITY_LOWERCASE);
			isCommunityCategory.setLabel(StringConstants.STRING_COMMUNITY_CAPITALIZED);
		}
		
		this.isCommunity = isCommunityCategory;
	}
	
	/**
	 * @param isCommunity a isCommunity Atom Category object.
	 */
	public void setIsCommunity(Category isCommunity) {
		this.isCommunity = isCommunity;
	}
	
	/**
	 * @param handle the Atom Element node that has handle information,  only used when an entry is retrieved from the server.
	 */
	public void setHandle(Element handle) {
		this.snx_handle = handle;
	}
	
	/**
	 * @return the Atom Element node that has handle information.
	 */
	public Element getHandleElement() {
		return snx_handle;
	}
	
	/**
	 * @param membercount the Atom Element node that has member count information,  only used when an entry is retrieved from the server.
	 */
	public void setMemberCount(Element membercount) {
		this.snx_membercount = membercount;
	}
	
	/**
	 * @return the Atom Element node that has member count information.
	 */
	public Element getMemberCountElement() {
		return snx_membercount;
	}
	
	/**
	 * @param communityType the Atom Element node that has community type information,  only used when an entry is retrieved from the server.
	 */
	public void setCommunityType(Element communityType) {
		this.snx_communityType = communityType;
	}

	public void setCommunityType(Permissions communityType) {
		Element communityTypeElement = null;
		
		if(communityType != null) {
			communityTypeElement = getFactory().newElement(StringConstants.SNX_COMMUNITY_TYPE);
			
			if(communityType != Permissions.PUBLICINVITEONLY) {
				communityTypeElement.setText(String.valueOf(communityType).toLowerCase());
			} else {
				communityTypeElement.setText(StringConstants.STRING_PUBLIC_INVITE_ONLY);
			}
		}
		this.snx_communityType = communityTypeElement;
	}
	
	public void setListWhenPrivate(Element listWhenPrivate) {
		this.snx_listWhenPrivate = listWhenPrivate;
	}
	
	public void setListWhenPrivate(boolean value) {
		Element listWhenPrivateElement = getFactory().newElement(StringConstants.SNX_LIST_WHEN_RESTRICTED);
		listWhenPrivateElement.setText(Boolean.toString(value));

		this.snx_listWhenPrivate = listWhenPrivateElement;
	}
	
	public Element getListWhenPrivateElement() {
		return snx_listWhenPrivate;
	}
	
	/**
	 * @return the Atom Element node that has community type information.
	 */
	public Element getCommunityTypeElement() {
		return snx_communityType;
	}
	
	public void setIsExternal(boolean isExtValue) {
		Element isExternalElement = null;

		isExternalElement = getFactory().newElement(StringConstants.SNX_ISEXTERNAL);
			
		if(isExtValue) {
			isExternalElement.setText("true");
		} else {
			isExternalElement.setText("false");
		}
		this.snx_isExternal = isExternalElement;
	}
	
	public Element getIsExternalElement() {
		return snx_isExternal;
	}
	
	public String getUuid() {
		if (this.Uuid == null){
		return getLinks().get(StringConstants.REL_SELF + ":" + StringConstants.MIME_NULL).getHref().toString().split("communityUuid=")[1];
		}
		else return Uuid;
	}
	public void setUuid(String uuid){
		this.Uuid = uuid ;
	}

	public Element getMemberEmailPrivileges() {
		return snx_memberEmailPrivileges;
	}

	public void setMemberEmailPrivileges(Element element) {
		this.snx_memberEmailPrivileges = element;
	}

	public void setMemberEmailPrivilegesValue(String value) {
		Element memberEmailPrivileges = getFactory().newElement(StringConstants.SNX_MEMBER_EMAIL_PRIVILEGES);
		memberEmailPrivileges.setText(value);
		
		this.snx_memberEmailPrivileges = memberEmailPrivileges;
	}

	public Element getCopyFromCommunityUuid() {
		return snx_copyFromCommunityUuid;
	}

	public void setCopyFromCommunityUuid(Element element) {
		this.snx_copyFromCommunityUuid = element;
	}

	public void setCopyFromCommunityUuid(String copyFromCommunityUuid) {
		Element copyFromCommunityUuidElement = getFactory().newElement(StringConstants.SNX_COPY_FROM_COMMUNITY_UUID);
		copyFromCommunityUuidElement.setText(copyFromCommunityUuid);
		this.snx_copyFromCommunityUuid = copyFromCommunityUuidElement;
	}
	
	public Element getStartPageElement() {
		return snx_startPage;
	}
	
	public void setStartPage(String startPage) {
		Element startPageElement = getFactory().newElement(StringConstants.SNX_STARTPAGE);
		startPageElement.setText(startPage);
		this.snx_startPage = startPageElement;
	}
	
	public void setIsPreModeration(boolean isPreModeration) {
		Element isPreModerationElement = null;

		isPreModerationElement = getFactory().newElement(StringConstants.SNX_PREMODERATION);
			
		if(isPreModeration) {
			isPreModerationElement.setText("true");
		} else {
			isPreModerationElement.setText("false");
		}
		this.snx_PreModeration = isPreModerationElement;
	}
	
	public Element getIsPreModerationElement() {
		return snx_PreModeration;
	}
}
