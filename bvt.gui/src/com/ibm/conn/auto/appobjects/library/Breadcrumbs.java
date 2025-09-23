package com.ibm.conn.auto.appobjects.library;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;

public class Breadcrumbs {
	private Element breadcrumbsElement;
   private String libraryName;
	
	public Breadcrumbs(Element containerElement, String libraryName) {
		this.breadcrumbsElement = containerElement.getSingleElement(CommunitiesUIConstants.LibraryBreadcrumbs);
		this.libraryName = libraryName; 
	}
	
	public void goBackToLibrary() {
		//TODO: Might need to add detection logic later on when we arrive on a page (like through a bookmark or refreshing), where the path to the Library (the root parent) cannot be determined, and the link is now "Back to <LIBRARY_NAME>". 
		this.breadcrumbsElement.getSingleElement(CommunitiesUIConstants.LibraryBreadcrumbLibraryWithNameSuffix + this.libraryName).click();
	}
}