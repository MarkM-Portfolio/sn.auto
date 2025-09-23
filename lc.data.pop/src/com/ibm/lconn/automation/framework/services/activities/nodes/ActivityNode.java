package com.ibm.lconn.automation.framework.services.activities.nodes;

import org.apache.abdera.model.Category;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;

import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.nodes.LCEntry;

public class ActivityNode extends LCEntry {	
	
	private Entry parent;					/** (Required) Identifies the parent activity of this entry. */
	
	private Category isPrivate;				/** (Optional) Identifies if this Entry is private */
	
	private Element snx_position;			/** (Optional) Position of the entry in the containing activity entry's array of fields. Use numbers separated by large increments to allow for repositioning. For example, use 1000, 2000, 3000, and so on. Choose a number that positions the entry relative to other entries in the activity. If a number is used more than once, the server renumbers the entries, while maintaining the relative positions of all entries. */
	private Element snx_activity;			/** (Unmodifiable) Unique identifier of an activity. */
	private Element snx_depth;				/** (Unmodifiable) Only used by Connections Web UI */
	private Element snx_permissions;		/** (Unmodifiable) Permissions for the current user */
	
	private Element thr_inReplyTo;			/** (Unmodifiable) Unique identifier of parent activity node */
	
	public ActivityNode(String title, String content, String tagsString, Entry parent) {
		super();
		
		setParent(parent);
		setActivityId(parent.getExtension(StringConstants.SNX_ACTIVITY));
		setTitle(title);
		setContent(content);
		setTags(tagsString);
		setInReplyTo(parent);
	}
	
	public ActivityNode(Entry entry) {
		super(entry);
		
		setPosition(entry.getExtension(StringConstants.SNX_POSITION));
		setActivityId(entry.getExtension(StringConstants.SNX_ACTIVITY));
		setDepth(entry.getExtension(StringConstants.SNX_DEPTH));
		setPermissions(entry.getExtension(StringConstants.SNX_PERMISSIONS));
		setInReplyTo((Element)entry.getExtension(StringConstants.THR_IN_REPLY_TO));
	}
	
	/**
	 * @param isPrivate a isPrivate Atom Category object.
	 */
	public void setIsPrivate(Category isPrivate) {
		this.isPrivate = isPrivate;
	}
	
	/**
	 * @return the Atom category object that contains the isPrivate information.
	 */
	public Category getIsPrivateCategory() {
		return isPrivate;
	}

	/**
	 * @param isPrivate set the Atom category object that contains the isEntry information.
	 */
	public void setIsPrivate(boolean isPrivate) {
		Category isPrivateCategory = null;
		
		if(isPrivate) {
			isPrivateCategory = getFactory().newCategory();
			isPrivateCategory.setScheme(StringConstants.SCHEME_FLAGS);
			isPrivateCategory.setTerm(StringConstants.STRING_PRIVATE_LOWERCASE);
			isPrivateCategory.setLabel(StringConstants.STRING_PRIVATE_CAPITALIZED);
		}
		
		this.isPrivate = isPrivateCategory;
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
	 * @return the href to the parent activity node of this entry.
	 */
	public String getInReplyTo() {
		if(thr_inReplyTo != null) {
			return thr_inReplyTo.getAttributeValue(StringConstants.ATTR_HREF);
		}
		
		return null;
	}

	/**
	 * Set the inReplyTo from an Atom element, only used when an entry is retrieved from the server.
	 * 
	 * @param activityId an Atom Element that contains the activity id information.
	 */
	public void setInReplyTo(Element thr_inReplyTo) {
		this.thr_inReplyTo = thr_inReplyTo;
	}
	
	/**
	 * Set the inReplyTo based on a parent Atom Entry
	 * 
	 * @param entry an Atom Entry that is an activity node that this entry is in reply to.
	 */
	public void setInReplyTo(Entry parentEntry) {
		Element replyElement = null;
		
		if(parentEntry != null) {
			replyElement = getFactory().newExtensionElement(StringConstants.THR_IN_REPLY_TO);
			replyElement.setAttributeValue(StringConstants.ATTR_REF, parent.getId().toString());
			replyElement.setAttributeValue(StringConstants.ATTR_HREF, parent.getLink(StringConstants.REL_EDIT).getHref().toString());
		}
		this.thr_inReplyTo = replyElement;
	}
	
	/**
	 * @return the inReplyTo Atom Element
	 */
	public Element getInReplyToElement() {
		return thr_inReplyTo;
	}
	
	
	public Entry getParent() {
		return parent;
	}

	public void setParent(Entry parent) {
		this.parent = parent;
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
	 * @param position the Atom Element node that has position information,  only used when an entry is retrieved from the server.
	 */
	public void setPosition(Element position) {
		this.snx_position = position;
	}
	
	/**
	 * @param position the integer position value for this entry
	 */
	public void setPosition(int position) {
		Element positionElement = getFactory().newElement(StringConstants.SNX_POSITION);
		positionElement.setText(String.valueOf(position));
	}
	
	/**
	 * @return the Atom Element node that has position information.
	 */
	public Element getPositionElement() {
		return snx_position;
	}
}
