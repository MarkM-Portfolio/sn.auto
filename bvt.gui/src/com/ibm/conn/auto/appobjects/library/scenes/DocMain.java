package com.ibm.conn.auto.appobjects.library.scenes;

import java.util.List;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.conn.auto.appobjects.library.FileThumbnailWidget;
import com.ibm.conn.auto.appobjects.library.FolderThumbnailWidget;
import com.ibm.conn.auto.appobjects.library.SortArea;
import com.ibm.conn.auto.appobjects.library.ViewSelector;
import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;

public class DocMain extends Scene {
   
	private ViewSelector viewSelector; // The View Selector object can be persisted, as it is not destroyed between scene transitions.

   public DocMain(Element containerElement, String libraryName){
		super(containerElement, libraryName);
		this.viewSelector = new ViewSelector(this.sceneElement);
	}

	@Override
	protected String getSelector() {
		return CommunitiesUIConstants.LibraryDocMain;
	}

   public SortArea getSortArea(){
      return new SortArea(this.sceneElement); // The Sorting Header may not be persisted, as it is destroyed and re-created when loading container items or during scene transitions. 
   }
   
   public ViewSelector getViewSelector(){
      return this.viewSelector;
   }
   
   // TODO: Maybe when implementing the Grid View, have another object called "Content Area" or so that can give you all the views with the table.
   // An alternative to this entire approach could be to not return view objects, but have the content area perform functions like getting ThumbnailWidgets or Details rows, depending on the view.
   // For this alternative approach, it might not be cleanly possible have the Content Area monitor the current view set, so just have it like the other objects like Breadcrumbs.  If a given element cannot be parsed from the webpage, it just breaks =/, that work is on the test to know when each state is valid. 
   
   // TODO: When there is a separate Grid View class, make a getter method here that returns the object DocMain maintains, either with the same instance or a new instance each time.
   // When this all happens, add code to check if the current view in the ViewSelector is set to Grid View, and if not throw an exception.
   
   // TODO: Maybe extract these out to a Grid View class.
   public FolderThumbnailWidget getFolderThumbnailWidgetByName(String folderName){
      List<Element> tws = this.sceneElement.getElements(CommunitiesUIConstants.FolderThumbnailWidgetsByPartialId);
      for (Element element : tws) {
         FolderThumbnailWidget tw = new FolderThumbnailWidget(element);
         if(tw.getName().getText().equals(folderName)){
            return tw;
         }
      }
      
      return null;
   }
   public FileThumbnailWidget getFileThumbnailWidgetByName(String fileName){
      List<Element> tws = this.sceneElement.getElements(CommunitiesUIConstants.FileThumbnailWidgetsByPartialId);
      for (Element element : tws) {
         FileThumbnailWidget tw = new FileThumbnailWidget(element);
         if(tw.getName().getText().equals(fileName)){
            return tw;
         }
      }
      
      return null;
   }
}