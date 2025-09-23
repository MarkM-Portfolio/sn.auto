package com.ibm.conn.auto.appobjects.library;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.conn.auto.appobjects.library.scenes.DocSummary;
import com.ibm.conn.auto.appobjects.library.scenes.DocMain;
import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;

public class LibraryWidget {
	
	private Element widgetElement; // The Library Widget element can be persisted, as it is not destroyed between scene transitions.  Page transitions will require a new Library Widget to be created however.  Nav link transitions may work if you come back to the same widget (unteseted).
	private String id;
	private String widgetId;
	private String name;
	private DocMain docMain; // The DocMain scene object can be persisted, as it is not destroyed between scene transitions.  Other effects from the main Library Widget causing it to be invalid also apply.  This scene may not be present, even in a 'hidden' state, if the Library Widget loads a DocSummary scene first and has not transitioned to DocMain yet.
	private DocSummary docSummary; // The DocSummary scene object can be persisted, as it is not destroyed between scene transitions.  Other effects from the main Library Widget causing it to be invalid also apply.  This scene may not be present, even in a 'hidden' state, if the Library Widget loads a DocSummary scene first and has not transitioned to DocSummary yet.
	
	public LibraryWidget(Element widgetContainerElement) {
		this.widgetElement = widgetContainerElement.getSingleElement(CommunitiesUIConstants.LibraryWidgetRoot);
		this.id = widgetElement.getAttribute("id");
		this.widgetId = widgetElement.getAttribute("widgetid");
		this.name = widgetElement.getSingleElement(CommunitiesUIConstants.LibraryTitle).getText();
	}
	
	public DocMain getDocMain(){
      if(this.docMain == null)
         this.docMain = new DocMain(this.widgetElement, this.name);
      return this.docMain;
   }
   public DocSummary getDocSummary(){
      if(this.docSummary == null)
         this.docSummary = new DocSummary(this.widgetElement, this.name);
      return this.docSummary;
   }
}