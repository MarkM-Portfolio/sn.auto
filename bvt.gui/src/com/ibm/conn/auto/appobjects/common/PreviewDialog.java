package com.ibm.conn.auto.appobjects.common;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;

public class PreviewDialog {
   private String closeSelector;
   private Element dialogElement;
   
   public PreviewDialog(RCLocationExecutor driver) {
	  String dialogSelector;
      
	  if (driver.isElementPresent(CommunitiesUIConstants.fileViewer)) {
          dialogSelector = CommunitiesUIConstants.fileViewer;
          closeSelector = CommunitiesUIConstants.closeViewer;
      } else {
          dialogSelector = CommunitiesUIConstants.previewDialog;
          closeSelector = CommunitiesUIConstants.closeThumbnail;
      }
      
      this.dialogElement = driver.getSingleElement(dialogSelector);
   }
   
   public void close() {
       this.dialogElement.getSingleElement(closeSelector).click();
   }
}
