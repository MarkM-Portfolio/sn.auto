package com.ibm.conn.auto.appobjects.library;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;

public abstract class ThumbnailWidget {
	protected String id;
	protected Element widgetElement;
	protected ThumbnailWidget(Element element) {
		this.id = element.getAttribute("id");
		this.widgetElement = element;
	}
	
	public String getId() {
		return this.id;
	}
	public Element getWidgetElement() {
		return this.widgetElement;
	}
	public Element getName(){
		return this.widgetElement.getSingleElement(CommunitiesUIConstants.ThumbnailWidgetFrontName);
	}
	public Element getBackSideActionLink(){
		return this.widgetElement.getSingleElement(CommunitiesUIConstants.ThumbnailWidgetBackSideLink);
	}
}