package com.ibm.conn.auto.appobjects.library.scenes;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;

public class DocSummary extends Scene {

	public DocSummary(Element containerElement, String libraryName){
		super(containerElement, libraryName);
	}

	@Override
	protected String getSelector() {
		return CommunitiesUIConstants.LibraryDocSummary;
	}
}