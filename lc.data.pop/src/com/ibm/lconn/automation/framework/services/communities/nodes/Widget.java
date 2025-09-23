package com.ibm.lconn.automation.framework.services.communities.nodes;

import java.util.List;

import org.apache.abdera.model.Category;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;

import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.nodes.LCEntry;

public class Widget extends LCEntry {
	
	private Category isWidget;

	private Element snx_communityUuid;			/** (Required for Community Event) Identifies the Event as a community Event and identifies the community to which it belongs by the communitys unique ID.. */

	private Element widgetHidden;
	private Element widgetInstanceId;
	private Element widgetLocation;
	private Element widgetDefId;
	//private Element widgetPreviousWidgetInstanceId;
	//private Boolean widgetMandated;
	//private Element widgetCustomTitle;


	public Widget(String widgetDef) {
		super();
		
		setIsWidget(true);
		setTitle(widgetDef);
		setWidgetHidden(false);
		setWidgetDefId(widgetDef);
		setWidgetLocation("col2");
	}
	
	public Widget(String widgetDef, String col) {
		super();
		
		setIsWidget(true);
		setTitle(widgetDef);
		setWidgetHidden(false);
		setWidgetDefId(widgetDef);
		setWidgetLocation(col);
	}

	public Widget(String widgetDef, String location, boolean hidden) {
		super();
		
		setIsWidget(true);
		setTitle(widgetDef);
		setWidgetHidden(hidden);
		setWidgetDefId(widgetDef);
		setWidgetLocation(location);
	}
	
	@Override
	public Entry toEntry() {
		Element[] extensions = {getWidgetDefId(), getWidgetLocation(), getWidgetHidden()};

		Category[] categories = {getIsWidgetCategory()};

		return createBasicEntry(extensions, categories);
	}
	
	public Element getWidgetDefId() {
		return widgetDefId;
	}

	public void setWidgetDefId(String widgetDefId) {
		this.widgetDefId = getFactory().newElement(StringConstants.SNX_WIDGET_DEFID);
		this.widgetDefId.setText(String.valueOf(widgetDefId));
	}
	
	public Element getWidgetInstanceId() {
		return widgetInstanceId;
	}


	public void setWidgetInstanceId(Element widgetInstanceId) {
		this.widgetInstanceId = widgetInstanceId;
	}


	public Element getWidgetLocation() {
		return widgetLocation;
	}


	public void setWidgetLocation(String widgetlocation) {
		this.widgetLocation = getFactory().newElement(StringConstants.SNX_WIDGET_LOCATION);
		this.widgetLocation.setText(widgetlocation);
	}



	public Widget(Entry entry) {
		super(entry);
		
		List<Category> types = entry.getCategories(StringConstants.SCHEME_TYPE);
		
		if(types.size() > 0) {
			String term = types.get(0).getTerm();
			if(term.equals(StringConstants.STRING_EVENT_LOWERCASE)) {
				setIsWidget(true);				
			}
		}
	}



	/**
	 * @return the Atom category object that contains the isEvent information.
	 */
	public Category getIsWidgetCategory() {
		return isWidget;
	}

	/**
	 * @param isWidget set the Atom category object that contains the isEvent information.
	 */
	public void setIsWidget(boolean isWidget) {
		Category isWidgetCategory = null;
		
		if(isWidget) {
			isWidgetCategory = getFactory().newCategory();
			isWidgetCategory.setScheme(StringConstants.SCHEME_TYPE);
			isWidgetCategory.setTerm(StringConstants.STRING_WIDGET_LOWERCASE);
			isWidgetCategory.setLabel(StringConstants.STRING_WIDGET_CAPITALIZED);
		}
		
		this.isWidget = isWidgetCategory;
	}
	
	public void setWidgetHidden(boolean hidden) {
		
		widgetHidden = getFactory().newElement(StringConstants.SNX_WIDGET_HIDDEN);
		if(hidden) {
			
			widgetHidden.setText(String.valueOf(hidden));
		} else {
			widgetHidden.setText("false");
		}	

	}
	
	public Element getWidgetHidden() {
		return widgetHidden;
	}

	
	/**
	 * @param isWidget a isWidget Atom Category object.
	 */
	public void setIsWidget(Category isWidget) {
		this.isWidget = isWidget;
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
	

	
}
