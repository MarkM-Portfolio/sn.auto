package com.ibm.conn.auto.appobjects.library.scenes;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.conn.auto.appobjects.library.Breadcrumbs;

public abstract class Scene {
	protected Element sceneElement;
	private Breadcrumbs breadcrumbs; // The Breadcrumbs object may be persisted, as it is not destroyed between scene transitions (not carefully tested).
	
	protected Scene(Element containerElement, String libraryName) {
		this.sceneElement = containerElement.getSingleElement(this.getSelector());
		this.breadcrumbs = new Breadcrumbs(this.sceneElement, libraryName);
	}
	
	protected abstract String getSelector();
	
	public Breadcrumbs getBreadcrumbs() {
		return breadcrumbs;
	}
}