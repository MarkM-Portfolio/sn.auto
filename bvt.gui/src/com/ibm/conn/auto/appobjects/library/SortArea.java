package com.ibm.conn.auto.appobjects.library;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;

// TODO: May need to create subclasses in the future for the different headers (Details/Grid and Summary views have some different columns)
// They can pull from a grab-bag master list of all possible columns from the super class.
// When that is done, might also want to have them instantiated as part of their associated view instead of just generally held in DocMain
public class SortArea { 
	private static final SortKey DEFAULT_SORT_KEY = SortKey.UPDATED;
	private static final SortOrder DEFAULT_SORT_ORDER = SortOrder.DESC;
	
	public enum SortKey {
		NAME(CommunitiesUIConstants.LibrarySortKeyNameLinkByText),
		UPDATED(CommunitiesUIConstants.LibrarySortKeyUpdatedLinkByText),
		DOWNLOADS(CommunitiesUIConstants.LibrarySortKeyDownloadsLinkByText),
		COMMENTS(CommunitiesUIConstants.LibrarySortKeyCommentsLinkByText),
		LIKES(CommunitiesUIConstants.LibrarySortKeyLikesLinkByText);
		
		private String selector;

      private SortKey(String selector) {
			this.selector = selector;
		}
		
		public String getSelector() {
		   return this.selector;
		}
	}
	public enum SortOrder {
		ASC("lotusAscending"),
		DESC("lotusDescending");
		
		private String className;

      private SortOrder(String className) {
		   this.className = className;
		}
      
      public String getClassName() {
         return this.className;
      }
	}

	private Element sortAreaElement;
	private SortKey key;
	private SortOrder order;
	
	public SortArea(Element containerElement) {
		this(DEFAULT_SORT_KEY, DEFAULT_SORT_ORDER);
		this.sortAreaElement = containerElement.getSingleElement(CommunitiesUIConstants.LibrarySortArea);
	}
	
	public SortArea(SortKey key, SortOrder order) {
		this.key = key;
		this.order = order;
	}
	
	// Getters and Setters
	
	public Element getSortArea() {
	   return this.sortAreaElement;
	}

	public SortKey getKey() {
		return key;
	}

	public SortOrder getOrder() {
		return order;
	}
	
	// Internal Sort Handling Methods
	
	private void resetOrder() {
	   this.order = DEFAULT_SORT_ORDER;
	}
	
	private void toggleOrder() {
	   this.order = SortOrder.values()[(SortOrder.values().length-1)-this.order.ordinal()];
	}
	
	private void clickSortKey(SortKey key) {
	   this.key = key;
	   this.getSortElement(key).click();
	}
	
	// Interaction Methods
	
	public void updateSort(SortKey key) {
	   if(this.key != key)
	      this.resetOrder();
	   else
	      this.toggleOrder();

	   this.clickSortKey(key);
	}
	
	public Element getSortElement(SortKey key) {
	   return this.sortAreaElement.getSingleElement(key.getSelector());
	}
	
	public boolean isDescending(SortKey key) {
	   return this.getSortElement(key).getAttribute("class").contains(SortOrder.DESC.getClassName());
	}
	public boolean isAscending(SortKey key) {
      return this.getSortElement(key).getAttribute("class").contains(SortOrder.ASC.getClassName());
   }
}