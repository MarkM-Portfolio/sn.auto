package com.ibm.lconn.automation.framework.services.communities.nodes;

import java.util.List;

import org.apache.abdera.model.Category;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;

import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Role;
import com.ibm.lconn.automation.framework.services.common.nodes.LCEntry;

public class Calendar extends LCEntry {
	
	private Category isCalendar;

	private Element snx_communityUuid;			/** (Required for Community Calendar) Identifies the calendar as a community calendar and identifies the community to which it belongs by the communitys unique ID.. */
	private Element snx_maprole;				/** Membership Role based on Community Role (author, owner, reader) */
	
	
	public Calendar(String title, Role role, String tagsString) {
		super();
		
		setIsCalendar(true);
		setTitle(title);
		setMapRole(role);
		setTags(tagsString);
	}

	public Calendar(Entry entry) {
		super(entry);
		
		List<Category> types = entry.getCategories(StringConstants.SCHEME_TYPE);
		
		if(types.size() > 0) {
			String term = types.get(0).getTerm();
			if(term.equals(StringConstants.STRING_CALENDAR_LOWERCASE)) {
				setIsCalendar(true);				
			}
		}
		
		setMapRole((Element)entry.getExtension(StringConstants.SNX_MAP_ROLE));

	}

	@Override
	public Entry toEntry() {
		Element[] extensions = {getMapRoleElement(), getCommunityUuidElement()};

		Category[] categories = {getIsCalendarCategory()};

		return createBasicEntry(extensions, categories);
	}


	/**
	 * @return the Atom category object that contains the isCalendar information.
	 */
	public Category getIsCalendarCategory() {
		return isCalendar;
	}

	/**
	 * @param isCalendar set the Atom category object that contains the isCalendar information.
	 */
	public void setIsCalendar(boolean isCalendar) {
		Category isCalendarCategory = null;
		
		if(isCalendar) {
			isCalendarCategory = getFactory().newCategory();
			isCalendarCategory.setScheme(StringConstants.SCHEME_TYPE);
			isCalendarCategory.setTerm(StringConstants.STRING_CALENDAR_LOWERCASE);
			isCalendarCategory.setLabel(StringConstants.STRING_CALENDAR_CAPITALIZED);
		}
		
		this.isCalendar = isCalendarCategory;
	}
	
	/**
	 * @param isCalendar a isCalendar Atom Category object.
	 */
	public void setIsCalendar(Category isCalendar) {
		this.isCalendar = isCalendar;
	}
	
	
	public String getUuid() {
		return getLinks().get(StringConstants.REL_SELF + ":" + StringConstants.MIME_NULL).getHref().toString().split("communityUuid=")[1];
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
	
	/*private void setCommunityUuid(Element communityUuid) {
		snx_communityUuid = communityUuid;
	}*/
	
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
	

}
