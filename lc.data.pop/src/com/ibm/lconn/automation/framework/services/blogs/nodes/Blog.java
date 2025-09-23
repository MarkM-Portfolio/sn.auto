package com.ibm.lconn.automation.framework.services.blogs.nodes;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TimeZone;

import org.apache.abdera.model.Category;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;

import com.ibm.lconn.automation.framework.services.activities.nodes.Activity;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.CommunityBlogPermissions;
import com.ibm.lconn.automation.framework.services.common.StringConstants.IdeationStatus;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Options;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Role;
import com.ibm.lconn.automation.framework.services.common.nodes.LCEntry;

/**
 * Blog object contains the elements that make up an Blog.
 * 
 * @author James Cunningham - jamcunni@ie.ibm.com
 */
public class Blog extends LCEntry{
	
	private Category isBlog;					/** (Required) Identifies an Blog. */
	private Category isCommunityBlog;			/** (Required for Community Blog) Identifies a community blog. */
	private Category isIdeationBlog;			/** (Required for Ideation Blog) Identifies a ideation blog. */
	private Category permissions;				/** (Required for Community Blog) Identifies the permissions of a community blog (private/moderated/public). */
	private Category ideationStatus;			/** (Required for Ideation Blog) Identifies the status for the ideation blog (closed/frozen/open). */
	
	private Element snx_handle;					/** (Required) A one-word keyword for the blog to be used in the web address of the blog. Note: This value must be unique across all of the blogs hosted by the Blogs server. */
	private Element snx_timezone;				/** (Optional) The time zone for this blog. */
	private Element snx_comments;				/** (Optional) Specifies whether commenting is on or off. Default is off */
	private Element snx_emailcomments;			/** (Optional) Indicates whether email notifications are sent when a comment is added. If email notifications are enabled for the entire blog, yes or no specifies whether or not emails are sent. The disabled option indicates that email notifications are disabled through a blog-wide setting. */
	private Element snx_commentmoderated;		/** (Optional) Specifies whether comments are moderated or not. */
	private Element snx_AllowCoedit;			/** (Optional) Indicates whether more than one person can edit the entry. */
	private Element snx_recommendations_rank;	/** (Optional) Number of recommendations for this blog. */
	private Element snx_hit_rank;
	private Element snx_locale;
	
	// Community Blog specific elements
	private Element snx_containertype;			/** (Required for Community Blog) */
	private Element snx_containerid;			/** (Required for Community Blog) */
	private Element snx_communityUuid;			/** (Required for Community Blog) Identifies the blog as a community blog and identifies the community to which it belongs by the communitys unique ID.. */
	private Element snx_maprole;				/** Membership Role based on Community Role (author, owner, reader) */
	
	// Ideation Blog specific elements
	private Element snx_votes_available_rank;
	private Element snx_voteLimit;
	
	private Element app_edited;
	
	public Blog(String title, String handle, String summary, String tagsString, boolean isCommunityBlog, boolean isIdeationBlog, CommunityBlogPermissions permissions, IdeationStatus ideationStatus, TimeZone timezone, boolean allowComments, int numDaysCommentsAllowed,  boolean emailComments, boolean commentModerated, boolean allowCoedit, int recommendationsRank, int containerType, String containerId, String communityUuid, Role mapRole, int votesAvailable){
		super();
		
		setTitle(title);
		setSummary(summary);
		setTags(tagsString);
		
		setCommunityBlogPermissions(permissions);
		setIdeationStatus(ideationStatus);
		
		setHandle(handle);
		setTimezone(timezone);
		setComments(allowComments, numDaysCommentsAllowed);
		setEmailComments(emailComments);
		setCommentModerated(commentModerated);
		setAllowCoedit(allowCoedit);
		
		setRecommendationsRank(recommendationsRank);
		setContainerType(containerType);
		setContainerId(containerId);
		setCommunityUuid(communityUuid);
		setMapRole(mapRole);
		
		setVotesAvailableRank(votesAvailable);
		
		if(isCommunityBlog) {
			setIsCommunityBlog(true);
		} else if(isIdeationBlog) {
			setIsIdeationBlog(true);
		} else {
			setIsBlog(true);
		}
	}
	
	public Blog(Entry entry) {
		super(entry);
		
		List<Category> types = entry.getCategories(StringConstants.SCHEME_TYPE);
		
		if(types.size() > 0) {
			String term = types.get(0).getTerm();
			if(term.equals(StringConstants.STRING_BLOG_LOWERCASE)) {
				setIsBlog(true);
			} else if(term.equals(StringConstants.STRING_COMMUNITY_BLOG_LOWERCASE)) {
				setIsCommunityBlog(true);
			} else if(term.equals(StringConstants.STRING_IDEATION_BLOG_LOWERCASE)) {
				setIsIdeationBlog(true);
			}
		}
		
		List<Category> flags = entry.getCategories(StringConstants.SCHEME_FLAGS);
		for(Category flag : flags) {
			if(flag.getTerm().equals(StringConstants.STRING_PRIVATE_LOWERCASE)) {
				setCommunityBlogPermissions(CommunityBlogPermissions.PRIVATE);
			} else if(flag.getTerm().equals(StringConstants.STRING_PUBLIC_LOWERCASE)) {
				setCommunityBlogPermissions(CommunityBlogPermissions.PUBLIC);
			} else if(flag.getTerm().equals(StringConstants.STRING_MODERATED_LOWERCASE)) {
				setCommunityBlogPermissions(CommunityBlogPermissions.MODERATED);
			} else if(flag.getTerm().equals(StringConstants.STRING_OPEN_LOWERCASE)) {
				setIdeationStatus(IdeationStatus.OPEN);
			} else if(flag.getTerm().equals(StringConstants.STRING_FROZEN_LOWERCASE)) {
				setIdeationStatus(IdeationStatus.FROZEN);
			} else if(flag.getTerm().equals(StringConstants.STRING_CLOSED_LOWERCASE))  {
				setIdeationStatus(IdeationStatus.CLOSED);
			}
		}
		setAppEdited(entry.getExtension(StringConstants.APP_EDITED));
		
		ExtensibleElement appControl = (entry.getExtension(StringConstants.APP_CONTROL));
		setHandle((Element)entry.getExtension(StringConstants.SNX_HANDLE));
		setTimezone((Element)entry.getExtension(StringConstants.SNX_TIMEZONE));
		if(appControl != null){
			setComments(appControl.getExtension(StringConstants.SNX_COMMENTS));
			setEmailComments(appControl.getExtension(StringConstants.SNX_EMAIL_COMMENTS));
			setCommentModerated(appControl.getExtension(StringConstants.SNX_COMMENT_MODERATED));
			setAllowCoedit(appControl.getExtension(StringConstants.SNX_ALLOW_COEDIT));
			setVoteLimit(appControl.getExtension(StringConstants.SNX_VOTE_LIMIT));
		}
		setContainerType(entry.getExtension(StringConstants.SNX_CONTAINER_TYPE));
		setContainerId((Element)entry.getExtension(StringConstants.SNX_CONTAINER_ID));
		setCommunityUuid((Element)entry.getExtension(StringConstants.SNX_COMMUNITY_UUID));
		setMapRole((Element)entry.getExtension(StringConstants.SNX_MAP_ROLE));
		setLocale(entry.getExtension(StringConstants.SNX_LOCALE));
		
		for(Element rankElement : entry.getExtensions(StringConstants.SNX_RANK)) {
			if(rankElement.getAttributeValue(StringConstants.ATTR_SCHEME).equals(StringConstants.SCHEME_RECOMMENDATIONS)) {
				setRecommendationsRank(rankElement);
			} else if(rankElement.getAttributeValue(StringConstants.ATTR_SCHEME).equals(StringConstants.SCHEME_VOTES_AVAILABLE)) {
				setVotesAvailableRank(rankElement);
			} else if(rankElement.getAttributeValue(StringConstants.ATTR_SCHEME).equals(StringConstants.SCHEME_HIT)) {
				setHitRank(rankElement);
			}
		}
	}

	private Element getAppControlElement() {
		ExtensibleElement appControlElement = getFactory().newElement(StringConstants.APP_CONTROL);
		if(getCommentsElement() != null)
			appControlElement.addExtension(getCommentsElement());
		
		if(getEmailCommentsElement() != null)	
			appControlElement.addExtension(getEmailCommentsElement());
		
		if(getCommentModeratedElement() != null)	
			appControlElement.addExtension(getCommentModeratedElement());
		
		if(getAllowCoeditElement() != null)	
			appControlElement.addExtension(getAllowCoeditElement());
		
		if(getVoteLimitElement() != null)
			appControlElement.addExtension(getVoteLimitElement());
		
		return appControlElement;
	}
	
	@Override
	public Entry toEntry() {
		Element allowCoedit = null;
		
		if(snx_AllowCoedit != null) {
			allowCoedit = (Element) getAllowCoeditElement().clone();
		}
		Element[] extensions = { getHandleElement(), getTimezoneElement(), getAppEditedElement(), getAppControlElement(), allowCoedit, 
								 getContainerTypeElement(), getContainerIdElement(), getCommunityUuidElement(),
								 getMapRoleElement(), getRecommendationsRankElement(), getVotesAvailableElement(), getLocaleElement()
							   };

		Category[] categories = { getBlogCategory(), getCommunityBlogCategory(), getIdeationBlogCategory(), getCommunityBlogPermissionsCategory(), getIdeationStatusCategory() };

		
		return createBasicEntry(extensions, categories);
	}
	
	private Element getAppEditedElement() {
		return app_edited;
	}
	
	private void setAppEdited(Element appEdited) {
		this.app_edited = appEdited;
	}
	
	private Element getLocaleElement() {
		return snx_locale;
	}
	
	private void setLocale(Element locale) {
		this.snx_locale = locale;
	}

	/**
	 * @return <code>true</code> if this object represents a blog; <code>false</code> otherwise.
	 */
	public boolean isBlog() {
		return (getBlogCategory() != null ? true : false);
	}
	
	/**
	 * @return the Atom category object that states that this entry is a blog.
	 */
	public Category getBlogCategory() {
		return isBlog;
	}

	/**
	 * Set whether this object represents a blog.
	 * @param isBlog	set <code>true</code> if this object represents a blog; <code>false</code> otherwise.
	 */
	public void setIsBlog(boolean isBlog) {
		Category isBlogCategory = null;
		
		if(isBlog) {
			isBlogCategory = getFactory().newCategory();
			isBlogCategory.setScheme(StringConstants.SCHEME_TYPE);
			isBlogCategory.setTerm(StringConstants.STRING_BLOG_LOWERCASE);
			isBlogCategory.setLabel(StringConstants.STRING_BLOG_CAPITALIZED);
		}
		
		this.isBlog = isBlogCategory;
	}
	
	/**
	 * @return <code>true</code> if this object represents a community blog; <code>false</code> otherwise.
	 */
	public boolean isCommunityBlog() {
		return (getCommunityBlogCategory() != null ? true : false);
	}
	
	/**
	 * @return the Atom category object that states that this entry is a community blog.
	 */
	public Category getCommunityBlogCategory() {
		return isCommunityBlog;
	}

	/**
	 * Set whether this object represents a community blog.
	 * @param isCommunityBlog	set <code>true</code> if this object represents a community blog; <code>false</code> otherwise.
	 */
	public void setIsCommunityBlog(boolean isCommunityBlog) {
		Category isCommunityBlogCategory = null;
		
		if(isCommunityBlog) {
			isCommunityBlogCategory = getFactory().newCategory();
			isCommunityBlogCategory.setScheme(StringConstants.SCHEME_TYPE);
			isCommunityBlogCategory.setTerm(StringConstants.STRING_COMMUNITY_BLOG_LOWERCASE);
			isCommunityBlogCategory.setLabel(StringConstants.STRING_COMMUNITY_BLOG_CAPITALIZED);
		}
		
		this.isCommunityBlog = isCommunityBlogCategory;
	}
	
	/**
	 * @return <code>true</code> if this object represents a ideation blog; <code>false</code> otherwise.
	 */
	public boolean isIdeationBlog() {
		return (getIdeationBlogCategory() != null ? true : false);
	}
	
	/**
	 * @return the Atom category object that states that this entry is a ideation blog.
	 */
	public Category getIdeationBlogCategory() {
		return isIdeationBlog;
	}

	/**
	 * Set whether this object represents a ideation blog.
	 * @param isIdeationBlog	set <code>true</code> if this object represents a ideation blog; <code>false</code> otherwise.
	 */
	public void setIsIdeationBlog(boolean isIdeationBlog) {
		Category isIdeationBlogCategory = null;
		
		if(isIdeationBlog) {
			isIdeationBlogCategory = getFactory().newCategory();
			isIdeationBlogCategory.setScheme(StringConstants.SCHEME_TYPE);
			isIdeationBlogCategory.setTerm(StringConstants.STRING_IDEATION_BLOG_LOWERCASE);
			isIdeationBlogCategory.setLabel(StringConstants.STRING_IDEATION_BLOG_CAPITALIZED);
		}
		
		this.isIdeationBlog = isIdeationBlogCategory;
	}
	
	/**
	 * @return CommunityBlogPermissions term value (valid values: private, public, moderated - defined in CommunityBlogPermissions enum)
	 */
	public CommunityBlogPermissions getCommunityPermissions() {
		if (permissions != null) {
			return Enum.valueOf(CommunityBlogPermissions.class, permissions.getTerm().toUpperCase());
		}
		
		return null;
	}
	
	/**
	 * @return the Atom category object that contains the community blog permissions information.
	 */
	public Category getCommunityBlogPermissionsCategory() {
		return permissions;
	}

	/**
	 * @param permissions	one of the permission options in the CommunityBlogPermissions enum.
	 */
	public void setCommunityBlogPermissions(CommunityBlogPermissions permissions) {
		Category communityBlogPermissionsCategory = null;
		
		if(permissions != null) {
			communityBlogPermissionsCategory = getFactory().newCategory();
			communityBlogPermissionsCategory.setScheme(StringConstants.SCHEME_FLAGS);
			communityBlogPermissionsCategory.setTerm(String.valueOf(permissions).toLowerCase());
		}
		
		this.permissions = communityBlogPermissionsCategory;
	}
	
	/**
	 * @param permissions	a CommunityBlogPermissions Atom Category object.
	 */
	public void setCommunityBlogPermissions(Category permissions) {
		this.permissions = permissions;
	}
	
	/**
	 * @return IdeationStatus term value (valid values: closed, frozen, open- defined in IdeationStatus enum)
	 */
	public IdeationStatus getIdeationStatus() {
		if (ideationStatus != null) {
			return Enum.valueOf(IdeationStatus.class, ideationStatus.getTerm().toUpperCase());
		}
		
		return null;
	}
	
	/**
	 * @return the Atom category object that contains the ideation status information.
	 */
	public Category getIdeationStatusCategory() {
		return ideationStatus;
	}

	/**
	 * @param permissions	one of the ideation status options in the IdeationStatus enum.
	 */
	public void setIdeationStatus(IdeationStatus status) {
		Category ideationStatusCategory = null;
		
		if(status != null) {
			ideationStatusCategory = getFactory().newCategory();
			ideationStatusCategory.setScheme(StringConstants.SCHEME_FLAGS);
			ideationStatusCategory.setTerm(String.valueOf(status).toLowerCase());
			ideationStatusCategory.setLabel("Users can contribute new ideas or comment on or vote for existing ideas in this Ideation Blog.");
		}
		
		this.ideationStatus = ideationStatusCategory;
	}
	
	/**
	 * @param permissions	a CommunityBlogPermissions Atom Category object.
	 */
	public void setIdeationStatus(Category ideationStatus) {
		this.ideationStatus = ideationStatus;
	}
	
	public Element getHandleElement() {
		return snx_handle;
	}

	public void setHandle(String handle) {
		Element handleElement = null;
		
		if(handle != null) {
			handleElement = getFactory().newElement(StringConstants.SNX_HANDLE);
			handleElement.setText(handle);
		}
		this.snx_handle = handleElement;
	}
	
	private void setHandle(Element handleElement) {
		snx_handle = handleElement;
	}
	
	public Element getTimezoneElement() {
		return snx_timezone;
	}

	public void setTimezone(TimeZone timezone) {
		Element timezoneElement = null;
		
		if(timezone != null) {
			timezoneElement = getFactory().newElement(StringConstants.SNX_TIMEZONE);
			timezoneElement.setText(timezone.getDisplayName());
		}
		this.snx_timezone = timezoneElement;
	}
	
	private void setTimezone(Element timezone) {
		snx_timezone = timezone;
	}
	
	public Element getCommentsElement() {
		return snx_comments;
	}

	public void setComments(boolean enabled, int days) {
		Element commentsElement = null;
		commentsElement = getFactory().newElement(StringConstants.SNX_COMMENTS);
		
		if(enabled) {
			commentsElement.setAttributeValue(StringConstants.ATTR_ENABLED, String.valueOf(Options.YES).toLowerCase());
		} else {
			commentsElement.setAttributeValue(StringConstants.ATTR_ENABLED, String.valueOf(Options.NO).toLowerCase());
		}
		commentsElement.setAttributeValue(StringConstants.ATTR_DAYS, String.valueOf(days));
		
		this.snx_comments = commentsElement;
	}
	
	private void setComments(Element comments) {
		snx_comments = comments;
	}
	
	public Element getEmailCommentsElement() {
		return snx_emailcomments;
	}

	public void setEmailComments(boolean enabled) {
		Element emailCommentsElement = null;
		emailCommentsElement = getFactory().newElement(StringConstants.SNX_EMAIL_COMMENTS);
		
		if(enabled) {
			emailCommentsElement.setAttributeValue(StringConstants.ATTR_ENABLED, String.valueOf(Options.YES).toLowerCase());
		} else {
			emailCommentsElement.setAttributeValue(StringConstants.ATTR_ENABLED, String.valueOf(Options.NO).toLowerCase());
		}
		this.snx_emailcomments = emailCommentsElement;
	}
	
	private void setEmailComments(Element emailComments) {
		snx_emailcomments = emailComments;
	}
	
	public Element getCommentModeratedElement() {
		return snx_commentmoderated;
	}

	public void setCommentModerated(boolean enabled) {
		Element commentModeratedElement = null;
		commentModeratedElement = getFactory().newElement(StringConstants.SNX_COMMENT_MODERATED);
		
		if(enabled) {
			commentModeratedElement.setAttributeValue(StringConstants.ATTR_ENABLED, String.valueOf(Options.YES).toLowerCase());
		} else {
			commentModeratedElement.setAttributeValue(StringConstants.ATTR_ENABLED, String.valueOf(Options.NO).toLowerCase());
		}
		this.snx_commentmoderated = commentModeratedElement;
	}
	
	private void setCommentModerated(Element commentModerated) {
		snx_commentmoderated = commentModerated;
	}
	
	public Element getAllowCoeditElement() {
		return snx_AllowCoedit;
	}

	public void setAllowCoedit(boolean enabled) {
		Element allowCoeditElement = null;
		allowCoeditElement = getFactory().newElement(StringConstants.SNX_ALLOW_COEDIT);
		
		if(enabled) {
			allowCoeditElement.setAttributeValue(StringConstants.ATTR_ENABLED, String.valueOf(Options.YES).toLowerCase());
		} else {
			allowCoeditElement.setAttributeValue(StringConstants.ATTR_ENABLED, String.valueOf(Options.NO).toLowerCase());
		}
		this.snx_AllowCoedit = allowCoeditElement;
	}
	
	private void setAllowCoedit(Element allowCoedit) {
		snx_AllowCoedit = allowCoedit;
	}
	
	public Element getRecommendationsRankElement() {
		return snx_recommendations_rank;
	}

	public void setRecommendationsRank(int rank) {
		Element recommendationsRankElement = null;
		
		recommendationsRankElement = getFactory().newElement(StringConstants.SNX_RANK);
		recommendationsRankElement.setAttributeValue(StringConstants.ATTR_SCHEME, StringConstants.SCHEME_RECOMMENDATIONS);
		recommendationsRankElement.setText(String.valueOf(rank));

		this.snx_recommendations_rank = recommendationsRankElement;
	}
	
	private void setRecommendationsRank(Element recommendationsRank) {
		snx_recommendations_rank = recommendationsRank;
	}
	
	private void setHitRank(Element hitRank) {
		snx_hit_rank = hitRank;
	}
	
	public Element getHitRank() {
		return snx_hit_rank;
	}
	
	public Element getContainerTypeElement() {
		return snx_containertype;
	}

	public void setContainerType(int containerType) {
		Element containerTypeElement = null;
		
		if(containerType > -1) {
			containerTypeElement = getFactory().newElement(StringConstants.SNX_CONTAINER_TYPE);
			containerTypeElement.setText(String.valueOf(containerType));
		}
		
		this.snx_containertype = containerTypeElement;
	}
	
	private void setContainerType(Element containerType) {
		snx_containertype = containerType;
	}
	
	public Element getContainerIdElement() {
		return snx_containerid;
	}

	public void setContainerId(String containerId) {
		Element containerIdElement = null;
		
		if(containerId != null) {
			containerIdElement = getFactory().newElement(StringConstants.SNX_CONTAINER_ID);
			containerIdElement.setText(containerId);
		}
		this.snx_containerid = containerIdElement;
	}
	
	private void setContainerId(Element containerId) {
		snx_containerid = containerId;
	}
	
	public Element getCommunityUuidElement() {
		return snx_communityUuid;
	}

	public void setCommunityUuid(String communityUuid) {
		Element communityUuidElement = null;
		
		if(communityUuid != null) {
			communityUuidElement = getFactory().newElement(StringConstants.SNX_COMMUNITY_UUID);
			communityUuidElement.setText(communityUuid);
		}
		this.snx_communityUuid = communityUuidElement;
	}
	
	private void setCommunityUuid(Element communityUuid) {
		snx_communityUuid = communityUuid;
	}
	
	public Element getMapRoleElement() {
		return snx_maprole;
	}

	public void setMapRole(Role role) {
		Element mapRoleElement = null;
		
		if(role != null) {
			mapRoleElement = getFactory().newElement(StringConstants.SNX_MAP_ROLE);
			mapRoleElement.setAttributeValue(StringConstants.ATTR_MEMBERSHIP, StringConstants.STRING_MEMBER);
			mapRoleElement.setText(String.valueOf(role).toLowerCase());
		}
		this.snx_maprole = mapRoleElement;
	}
	
	private void setMapRole(Element mapRole) {
		snx_maprole = mapRole;
	}
	
	public Element getVotesAvailableElement() {
		return snx_votes_available_rank;
	}

	public void setVotesAvailableRank(int votesAvailable) {
		Element votesAvailableElement = null;
		
		if(votesAvailable > 0) {
			votesAvailableElement = getFactory().newElement(StringConstants.SNX_RANK);
			votesAvailableElement.setAttributeValue(StringConstants.ATTR_SCHEME, StringConstants.SCHEME_VOTES_AVAILABLE);
			votesAvailableElement.setText(String.valueOf(votesAvailable));
		}
		
		this.snx_votes_available_rank = votesAvailableElement;
	}
	
	private void setVotesAvailableRank(Element ideationRank) {
		snx_votes_available_rank = ideationRank;
	}
	
	public Element getVoteLimitElement() {
		return snx_voteLimit;
	}
	
	private void setVoteLimit(Element voteLimit) {
		snx_voteLimit = voteLimit;
	}

	public String getBlogType() {
		if(isBlog())
			return StringConstants.STRING_BLOG_LOWERCASE;
		else if(isCommunityBlog())
			return StringConstants.STRING_COMMUNITY_BLOG_LOWERCASE;
		else if(isIdeationBlog())
			return StringConstants.STRING_IDEATION_BLOG_LOWERCASE;
		
		return null;
	}

	Comparator<Category> categoryComparator = new Comparator<Category>() {

        // Java 5 doesn't allow this, should reenable when we move to Java 6
		//@Override
		public int compare(Category o1, Category o2) {
			int term = o1.getTerm().compareTo(o2.getTerm());
			
			if(term == 0) {
				return o1.getText().compareTo(o2.getText());
			}
			
			return term;
		}
	};
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null){
			return false;
		}
		
		if (!(obj instanceof Blog)){
			return false;
		}
			
		Blog other = (Blog)obj;

		if(other.getId() != null && this.getId() != null) {
			if(other.getId().equals(this.getId())) {
				return true;
			}
		} else {
			Collections.sort(other.getTags(), categoryComparator);
			Collections.sort(this.getTags(), categoryComparator);
			
			if(other.getTitle().trim().equals(this.getTitle().trim()) &&
			   other.isBlog() == this.isBlog() &&
			   other.isCommunityBlog() == this.isCommunityBlog() &&
			   other.isIdeationBlog() == this.isIdeationBlog() &&
			   other.getHandleElement().getText().equals(this.getHandleElement().getText())) {
			   
				if(other.getTags().size() == this.getTags().size()) {
					for(int i = 0; i < other.getTags().size(); i++) {
						if(categoryComparator.compare(this.getTags().get(i), other.getTags().get(i)) != 0) {
							return false;
						}
					}
				}
				return true;
		   }
		}
		return false;
	}
	
	public String getEditHref() {
		return super.getEditLink();
	}
	
	public String getSelfHref() {
		return super.getSelfLink();
	}
	
	public String getAlternateHref() {
		return super.getAlternateLink();
	}
	
}
