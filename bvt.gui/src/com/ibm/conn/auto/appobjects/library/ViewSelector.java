package com.ibm.conn.auto.appobjects.library;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;

public class ViewSelector {
   private static final View DEFAULT_VIEW = View.GRID_VIEW;
   
   public enum View {
      GRID_VIEW(CommunitiesUIConstants.LibraryGridViewSelectorLinkOn, CommunitiesUIConstants.LibraryGridViewSelectorLinkOff),
      DETAILS_VIEW(CommunitiesUIConstants.LibraryDetailsViewSelectorLinkOn, CommunitiesUIConstants.LibraryDetailsViewSelectorLinkOff),
      SUMMARY_VIEW(CommunitiesUIConstants.LibrarySummaryViewSelectorLinkOn, CommunitiesUIConstants.LibrarySummaryViewSelectorLinkOff);
      
      private String activeSelector;
      private String inactiveSelector;
      
      private View(String activeSelector, String inactiveSelector){
         this.activeSelector = activeSelector;
         this.inactiveSelector = inactiveSelector;
      }
      
      public String getActiveSelector(){
         return this.activeSelector;
      }
      public String getInactiveSelector(){
         return this.inactiveSelector;
      }
   }

   private Element viewSelectorElement;
   private View currentView = DEFAULT_VIEW;
   
   public ViewSelector(Element containerElement){
      this.viewSelectorElement = containerElement.getSingleElement(CommunitiesUIConstants.LibraryViewSelector);
   }
   
   public boolean isCurrentView(View view){
      return this.currentView == view && this.viewSelectorElement.isElementPresent(this.currentView.getActiveSelector());
   }
   public void switchToView(View newView) {
      this.currentView = newView;
      this.viewSelectorElement.getSingleElement(newView.getInactiveSelector()).click();
   }
}