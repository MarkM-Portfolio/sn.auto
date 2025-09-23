package com.ibm.conn.auto.appobjects.library;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;

public class FileThumbnailWidget extends ThumbnailWidget {
	public static final String idPrefix = "lconn_share_widget_CCMFileThumbnail_";
	
	public FileThumbnailWidget(Element element){
		super(element);
	}
	
	public Element getDownloadActionLink(){
		return this.widgetElement.getSingleElement(CommunitiesUIConstants.ThumbnailWidgetDownloadLink);
	}
	public Element getSummaryActionLink(){
		return this.widgetElement.getSingleElement(CommunitiesUIConstants.ThumbnailWidgetSummaryLink);
	}
	public Element getPreviewActionLink(){
		return this.widgetElement.getSingleElement(CommunitiesUIConstants.ThumbnailWidgetPreviewLink);
	}
}