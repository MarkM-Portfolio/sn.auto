package com.ibm.lconn.automation.framework.services.activities.nodes;

import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.abdera.model.Category;
import org.apache.abdera.model.Collection;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Link;

import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.Utils;
import com.ibm.lconn.automation.framework.services.common.StringConstants.DefaultView;
import com.ibm.lconn.automation.framework.services.common.nodes.LCEntry;

/**
 * Activity object contains the elements that make up a Connections Activity.
 * @see <a href="http://www-10.lotus.com/ldd/lcwiki.nsf/dx/Activity_content_lc3" > Activity Content </a>
 * 
 * @author Piyush K. Agarwal - pagarwal@us.ibm.com
 */
public class Activity extends LCEntry {
	
	private Category isActivity;			/** (Required) Identifies an activity. */
	private Category isCommunityActivity;	/** (Required for Community activity) Identifies a community activity. */
	private Category priority;				/** (Optional) Identifies the priority of the activity. Options are High, Medium, or Normal. Prioritization settings are not global, but are unique to each user; no other members can see these collections. */
	private Category isComplete;			/** (Optional) Flag that identifies a completed activity. To complete an activity, add this flag. If it is not present, the activity is not completed. */
	private Category isTemplate;			/** (Optional) Flag that is only present on an activity that is a template for creating other activities. Add this flag to make an activity appear as a template. The API does not provide any applications that use activity templates. */
	private Category isDeleted;				/** (Optional) Flag that is only present on an activity that is deleted, meaning it is in the Trash view and has not been removed from the system. */
	private Category defaultView;			/** (Optional) Specifies the starting page of a template. The term attribute identifies the default view to use. Options are: recent, outline (default), todo. Label attribute is optional. */
	
	private Collection app_collection;		/** (Unmodifiable) Atom element that contains the link to the collection of entries within the activity. */
	private Element snx_activity;			/** (Unmodifiable) Unique identifier of an activity. */
	private Element snx_depth;				/** (Unmodifiable) Only used by Connections Web UI */
	private Element snx_icon;				/** (Unmodifiable) Link to an icon that depicts the status of an activity. */
	private Element snx_permissions;		/** (Unmodifiable) Permissions for the current user */
	private Element snx_position;			/** (Unmodifiable) Only used by Connections Web UI  */
	private Element snx_duedate;			/** (Optional) Specifies the date on which the activity is due to be completed. */

	/**
	 * Constructor to create an Activity object with the required/optional content.
	 * 
	 * @param title					(Required) A descriptive title of the activity specified in text format.
	 * @param content				(Optional) The formatted content of an activity, as defined in the Atom specification; may be empty.
	 * @param tagsString			(Optional) Space delimited string of tags
	 * @param dueDate				(Optional) Specifies the date on which the activity is due to be completed.
	 * @param isComplete			(Optional) Flag that identifies a completed activity.
	 * @param isCommunityActivity	(Required if community activity) Flag that identifies the activity as a community activity.
	 */
	public Activity(String title, String content, String tagsString, Date dueDate, boolean isComplete,  boolean isCommunityActivity) {
		super();
		
		setTitle(title);
		setContent(content);
		setTags(tagsString);
		setIsComplete(isComplete);
		setDueDate(dueDate);
		
		if(isCommunityActivity) {
			setIsCommunityActivity(true);
		} else {
			setIsActivity(true);
		}
	}

	/**
	 * Constructor to create an Activity based object based on valid Activity Atom Entry object.
	 * 
	 * @param entry an Atom entry that represents a Activity.
	 */
	public Activity(Entry entry) {
		super(entry);
		
		List<Category> types = entry.getCategories(StringConstants.SCHEME_TYPE);
		
		if(types.size() > 0) {
			String term = types.get(0).getTerm();
			if(term.equals(StringConstants.STRING_ACTIVITY_LOWERCASE)) {
				setIsActivity(true);
			} else if(term.equals(StringConstants.STRING_COMMUNITY_ACTIVITY_LOWERCASE)) {
				setIsCommunityActivity(true);
			}
		}
		
		List<Category> schemes = entry.getCategories(StringConstants.SCHEME_PRIORITY);
		if(schemes.size() > 0) {
			setPriority(schemes.get(0));
		}
		
		for(Category category : entry.getCategories(StringConstants.SCHEME_FLAGS)) {
			if(category.getTerm().equals(StringConstants.STRING_COMPLETED))
				setIsComplete(category);
			else if(category.getTerm().equals(StringConstants.STRING_TEMPLATE_LOWERCASE))
				setIsTemplate(category);
			else if(category.getTerm().equals(StringConstants.STRING_DELETED))
				setIsDeleted(category);
			else if(category.getScheme().equals(StringConstants.SCHEME_DEFAULT_VIEW))
				setDefaultView(category);
		}
		
		try {
			String dueDateString = entry.getSimpleExtension(StringConstants.SNX_DUEDATE);
			
			if(dueDateString != null && dueDateString.length() != 0) {
				setDueDate(Utils.tLdateFormatter.get().parse(dueDateString));
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		setAppCollection((Collection) entry.getExtension(StringConstants.APP_COLLECTION));
		setPosition(entry.getExtension(StringConstants.SNX_POSITION));
		setDepth(entry.getExtension(StringConstants.SNX_DEPTH));
		setPermissions(entry.getExtension(StringConstants.SNX_PERMISSIONS));
		setIcon(entry.getExtension(StringConstants.SNX_ICON));
		setActivityId(entry.getExtension(StringConstants.SNX_ACTIVITY));
	}

	/**
	 * Returns a Atom Entry representation of an Activity.
	 * 
	 * @see com.ibm.lconn.automation.framework.services.common.nodes.LCEntry#toEntry()
	 * @return the Atom Entry representation of an Activity.
	 */
	@Override
	public Entry toEntry() {
		
		Element[] extensions = { getAppCollection(), getActivityIdElement(), getDepthElement(), getDueDateElement(), 
								 getIconElement(), getPermissionsElement(), getPositionElement() 
							   };
		
		Category[] categories = { getActivityCategory(), getCommunityActivityCategory(), getPriority(), 
								  getIsCompleteCategory(), getIsTemplateCategory(), getIsDeletedCategory(), 
								  getDefaultViewCategory()
								};
		
		Entry entry = createBasicEntry(extensions, categories);
		entry.setContentAsHtml(getContent());
		
		return entry;
	}
	
	/**
	 * Returns a Atom Entry representation of a Community Activity.
	 * 
	 * @return the Atom Entry representation of a Community Activity.
	 */
	public Entry toCommunityEntry(String communityUuid, String communityURL) {
		
		Entry entry = this.toEntry();
		
		// Add Community Specific Nodes
		if(isCommunityActivity()) {
			entry.addSimpleExtension(StringConstants.SNX_COMMUNITY_UUID, communityUuid);
			
			Link communityLink = getFactory().newLink();
			communityLink.setHref(communityURL);
			communityLink.setRel(StringConstants.REL_CONTAINER);
			communityLink.setMimeType(StringConstants.LINK_TYPE_ATOM_XML);
			entry.addLink(communityLink);
		}
		
		return entry;
	}

	/**
	 * @return <code>true</code> if this object represents an activity; <code>false</code> otherwise (community activity, or invalid entry).
	 */
	public boolean isActivity() {
		return isActivity.getTerm().equals(StringConstants.STRING_ACTIVITY_LOWERCASE);
	}
	
	/**
	 * @return the Atom category object that states that this entry is an activity.
	 */
	public Category getActivityCategory() {
		return isActivity;
	}

	/**
	 * Set whether this object represents an activity (vs. community activity).
	 * @param isActivity	set <code>true</code> if this object represents an activity; <code>false</code> otherwise (community activity, or invalid entry).
	 */
	public void setIsActivity(boolean isActivity) {
		Category isActivityCategory = null;
		
		if(isActivity) {
			isActivityCategory = getFactory().newCategory();
			isActivityCategory.setScheme(StringConstants.SCHEME_TYPE);
			isActivityCategory.setTerm(StringConstants.STRING_ACTIVITY_LOWERCASE);
			isActivityCategory.setLabel(StringConstants.STRING_ACTIVITY_CAPITALIZED);
		}
		
		this.isActivity = isActivityCategory;
	}
	
	/**
	 * @return <code>true</code> if this object represents a community activity; <code>false</code> otherwise (regular activity, or invalid entry).
	 */
	public boolean isCommunityActivity() {
		return (isCommunityActivity != null);
	}
	
	/**
	 * @return the Atom category object that states that this entry is a community activity.
	 */
	public Category getCommunityActivityCategory() {
		return isCommunityActivity;
	}
	
	/**
	 * Set whether this object represents a community activity (vs. regular activity).
	 * @param isCommunityActivity	set <code>true</code> if this object represents a community activity; <code>false</code> otherwise (regular activity, or invalid entry).
	 */
	public void setIsCommunityActivity(boolean isCommunityActivity) {
		Category isCommunityActivityCategory = null;
		
		if(isCommunityActivity) {
			isCommunityActivityCategory = getFactory().newCategory();
			isCommunityActivityCategory.setScheme(StringConstants.SCHEME_TYPE);
			isCommunityActivityCategory.setTerm(StringConstants.STRING_COMMUNITY_ACTIVITY_LOWERCASE);
			isCommunityActivityCategory.setLabel(StringConstants.STRING_COMMUNITY_ACTIVITY_CAPITALIZED);
		}
		
		this.isCommunityActivity = isCommunityActivityCategory;
	}
	
	/**
	 * @return the Atom category object that contains the priority.
	 */
	public Category getPriority() {
		return priority;
	}

	
	/**
	 * @param priority	the Atom category object that contains the priority.
	 */
	public void setPriority(Category priority) {
		this.priority = priority;
	}
	
	/**
	 * @return <code>true</code> if this activity is complete; <code>false</code> otherwise.
	 */
	public boolean isComplete() {
		return (isComplete != null);
	}
	
	/**
	 * @return the Atom category object that contains the is complete information.
	 */
	public Category getIsCompleteCategory() {
		return isComplete;
	}

	/**
	 * @param isComplete set the Atom category object that contains the is complete information.
	 */
	public void setIsComplete(boolean isComplete) {
		Category isCompleteCategory = null;
		
		if(isComplete) {
			isCompleteCategory = getFactory().newCategory();
			isCompleteCategory.setScheme(StringConstants.SCHEME_FLAGS);
			isCompleteCategory.setTerm(StringConstants.STRING_COMPLETED);
		}
		
		this.isComplete = isCompleteCategory;
	}
	
	/**
	 * @param isComplete	a isComplete Atom Category object.
	 */
	public void setIsComplete(Category isComplete) {
		this.isComplete = isComplete;
	}
	
	/**
	 * @return <code>true</code> if this activity is a template; <code>false</code> otherwise.
	 */
	public boolean isTemplate() {
		return (isTemplate != null);
	}
	
	/**
	 * @return the Atom category object that contains the is template information.
	 */
	public Category getIsTemplateCategory() {
		return isTemplate;
	}

	/**
	 * @param isTemplate set <code>true</code> if this activity is a template; <code>false</code> otherwise.
	 */
	public void setIsTemplate(boolean isTemplate) {
		Category isTemplateCategory = null;
		
		if(isTemplate) {
			isTemplateCategory = getFactory().newCategory();
			isTemplateCategory.setScheme(StringConstants.SCHEME_FLAGS);
			isTemplateCategory.setTerm(StringConstants.STRING_TEMPLATE_LOWERCASE);
			isTemplateCategory.setLabel(StringConstants.STRING_TEMPLATE_CAPITALIZED);
		}
		
		this.isTemplate = isTemplateCategory;
	}
	
	/**
	 * @param isTemplate	a isTemplate Atom Category object.
	 */
	public void setIsTemplate(Category isTemplate) {
		this.isTemplate = isTemplate;
	}
	
	/**
	 * @return <code>true</code> if this activity is deleted; <code>false</code> otherwise.
	 */
	public boolean isDeleted() {
		return (isDeleted != null);
	}
	
	/**
	 * @return the Atom category object that contains the is deleted information.
	 */
	public Category getIsDeletedCategory() {
		return isDeleted;
	}

	/**
	 * @param  isDeleted set <code>true</code> if this activity is deleted; <code>false</code> otherwise.
	 */
	public void setIsDeleted(boolean isDeleted) {
		Category isDeletedCategory = null;
		
		if(isDeleted) {
			isDeletedCategory = getFactory().newCategory();
			isDeletedCategory.setScheme(StringConstants.SCHEME_FLAGS);
			isDeletedCategory.setTerm(StringConstants.STRING_DELETED);
		}
		
		this.isDeleted = isDeletedCategory;
	}
	
	/**
	 * @param isDeleted	a isDeleted Atom Category object.
	 */
	public void setIsDeleted(Category isDeleted) {
		this.isDeleted = isDeleted;
	}
	
	/**
	 * @return DefaultView term value (valid values: recent, outline, todo - defined in DefaultView enum)
	 */
	public DefaultView getDefaultViewValue() {
		if(defaultView != null) {
			return Enum.valueOf(DefaultView.class, defaultView.getTerm().toUpperCase());
		}
	
		return null;
	}
	
	/**
	 * @return the Atom category object that contains the default view information.
	 */
	public Category getDefaultViewCategory() {
		return isDeleted;
	}

	/**
	 * @param defaultView	one of the default view options in the DefaultView enum.
	 */
	public void setDefaultView(DefaultView defaultView) {
		Category defaultViewCategory = null;
		
		if(defaultView != null) {
			defaultViewCategory = getFactory().newCategory();
			defaultViewCategory.setScheme(StringConstants.SCHEME_DEFAULT_VIEW);
			defaultViewCategory.setTerm(String.valueOf(defaultView).toLowerCase());
		}
		
		this.defaultView = defaultViewCategory;
	}
	
	/**
	 * @param defaultView	a defaultView Atom Category object.
	 */
	public void setDefaultView(Category defaultView) {
		this.defaultView = defaultView;
	}
	
	/**
	 * @param appCollection	the Atom Element that describes an app:collection xml node, only used when an entry is retrieved from the server.
	 */
	public void setAppCollection(Collection appCollection) {
		this.app_collection = appCollection;
	}

	/**
	 * @return the Atom Element that describes an app:collection xml node.
	 */
	public Collection getAppCollection() {
		return app_collection;
	}
	
	/**
	 * @return the activities unique id String.
	 */
	public String getActivityId() {
		if(snx_activity != null) {
			return snx_activity.getText();
		}
		
		return null;
	}

	/**
	 * Set the activity id from an Atom element, only used when an entry is retrieved from the server.
	 * 
	 * @param activityId	an Atom Element that contains the activity id information.
	 */
	public void setActivityId(Element activityId) {
		this.snx_activity = activityId;
	}
	
	/**
	 * @return the activities unique id Atom Element.
	 */
	public Element getActivityIdElement() {
		return snx_activity;
	}
	
	/**
	 * @return value of the snx:depth Atom Element node.
	 */
	public int getDepth() {
		if (snx_depth != null) {
			return Integer.parseInt(snx_depth.getText());
		}
		
		return 0;
	}

	/**
	 * @param depth the Atom Element node that has activity depth information, only used when an entry is retrieved from the server.
	 */
	public void setDepth(Element depth) {
		this.snx_depth = depth;
	}
	
	/**
	 * @return the Atom Element node that has activity depth information.
	 */
	public Element getDepthElement() {
		return snx_depth;
	}
	

	/**
	 * @return the url of the icon that depicts the status of this activity.
	 */
	public String getIconUrl() {
		if(snx_icon != null) {
			return snx_icon.getText();
		}
		
		return null;
	}

	/**
	 * @param icon the Atom Element node that has activity icon information,  only used when an entry is retrieved from the server.
	 */
	public void setIcon(Element icon) {
		this.snx_icon = icon;
	}
	
	/**
	 * @return  the Atom Element node that has activity icon information.
	 */
	public Element getIconElement() {
		return snx_icon;
	}
	
	/**
	 * @return String containing permission information for currently authenticated user.
	 */
	public String getPermissions() {
		if(snx_permissions != null) {
			return snx_permissions.getText();
		}
		
		return null;
	}

	/**
	 * @param permissions the Atom Element node that has user permissions information,  only used when an entry is retrieved from the server.
	 */
	public void setPermissions(Element permissions) {
		this.snx_permissions = permissions;
	}
	
	/**
	 * @return the Atom Element node that has user permissions information.
	 */
	public Element getPermissionsElement() {
		return snx_permissions;
	}
	
	/**
	 * @return position of the activity, currently used only by UI.
	 */
	public int getPosition() {
		if(snx_position != null) {
			return Integer.parseInt(snx_position.getText());
		}
			return 0;
	}

	/**
	 * @param position	the Atom Element node that has position information,  only used when an entry is retrieved from the server.
	 */
	public void setPosition(Element position) {
		this.snx_position = position;
	}
	
	/**
	 * @return the Atom Element node that has position information.
	 */
	public Element getPositionElement() {
		return snx_position;
	}
	
	/**
	 * @return a Date object with the time parsed from the snx:duedate element.
	 * @throws ParseException if the date String is not in the expected format.
	 */
	public Date getDueDate() throws ParseException {
		if(snx_duedate != null) {
			return Utils.dateFormatter.parse(snx_duedate.getText());
		}
		
		return null;
	}

	/**
	 * Set the due date of this activity to the provided date.
	 * 
	 * @param dueDate	the Date this activity is due.
	 */
	public void setDueDate(Date dueDate) {
		Element dueDateElement = null;
		
		if(dueDate != null) {
			dueDateElement = getFactory().newElement(StringConstants.SNX_DUEDATE);
			dueDateElement.setText(Utils.dateFormatter.format(dueDate));
		}
		
		this.snx_duedate = dueDateElement;
	}
	
	/**
	 * @return an Atom Element object with the duedate content
	 */
	public Element getDueDateElement() {
		return snx_duedate;
	}

	/**
	 * @return the edit href for this activity
	 */
	public String getEditHref() {
		return getLinks().get(StringConstants.REL_EDIT + ":" + StringConstants.MIME_ATOM_XML).getHref().toString();
	}
	
	/**
	 * @return the tag-cloud href for this activity
	 */
	public String getTagCloudHref() {
		return getLinks().get(StringConstants.REL_TAG_CLOUD + ":" + StringConstants.MIME_ATOM_XML).getHref().toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null){
			return false;
		}
		
		if (!(obj instanceof Activity)){
			return false;
		}
			
		Activity other = (Activity)obj;

		if(other.getId() != null && this.getId() != null) {
			if(other.getId().equals(this.getId())) {
				return true;
			}
		} else {
			Collections.sort(other.getTags(), Utils.categoryComparator);
			Collections.sort(this.getTags(), Utils.categoryComparator);
			
			if(other.getTitle().trim().equals(this.getTitle().trim()) &&
			   other.getContent().trim().equals(this.getContent().trim()) &&
			   other.isComplete() == this.isComplete() &&
			   other.isDeleted() == this.isDeleted() &&
			   other.isTemplate() == this.isTemplate() &&
			   other.isCommunityActivity() == this.isCommunityActivity()) {
				
				if(other.getTags().size() == this.getTags().size()) {
					for(int i = 0; i < other.getTags().size(); i++) {
						if(Utils.categoryComparator.compare(this.getTags().get(i), other.getTags().get(i)) != 0) {
							return false;
						}
					}
				}
				
				try {
					if(other.getDueDate() != null && this.getDueDate() != null) {
						if(other.getDueDate().equals(this.getDueDate())) {
							return true;
						}
						return false;
					}
					return true;
					
				} catch (ParseException e) {
					e.printStackTrace();
				}
		   }
		}
		return false;
	}
}
