package com.ibm.conn.auto.webui.constants;

public final class ItmNavUIConstants {

	public static String addImportantItem = "section.itm-section button.add-contacts";
	public static String addImportantBox = "input.typeaheadInput[type='text']";
	public static String genericTypeahead = " ul.ui-typeahead-ul li.ui-typeahead-li";
	public static String importantToMeTypeahead = "div.itmtypeahead " + genericTypeahead;
	public static String importantToMeTypeaheadMore = "li.more-results";
	public static String importantToMeListAll = "img[class='face']";
	public static String suggestedImportantListAll = "ul[class='suggested-sets']>li"; //ul.suggested-sets li.ic-bizcard";
	public static String importantToMeList = "ul[class='active-sets']";
	public static String suggestedImportantList = "ul[class='suggested-sets']";
	// importantToMeListAll may include the Add Entry icon if present while importantToMeListActual omits it
	public static String importantToMeListActual = importantToMeListAll + ":not(.add-action):not(.loading-indicator)";
	// suggestedImportantListAll may include other icon if present while suggestedImportantListActual omits it
	public static String suggestedImportantListActual = suggestedImportantListAll + ":not(.add-action):not(.loading-indicator)";
	
	public static String bizCardIconInPersonIcon = "li[aria-label='PLACEHOLDER'] button[title='Business card']";
	public static String filterIconInPersonIcon = "li[aria-label='PLACEHOLDER'] button[aria-label='filter ']";
	public static String viewCommunityInCommunityIcon = "//li[@aria-label='PLACEHOLDER']//button[@title='View Community']";
	public static String removeImportantItem = "section.itm-section button.remove-contacts";
	public static String xIconInItemBubble = "li[aria-label='PLACEHOLDER'] button[class='remove-entry']";
	public static String xIconsInItemBubble = "button[class='remove-entry']";
	public static String staticMenuSection = "ul[aria-label='Global Entries']>li>span";
	public static String addEntryIcon = "ul.active-sets li.add-action";
	public static String upArrowInITMCarousel = "button[class=\"scroll-button left itm-scroll scroll-button-visible\"]";
	public static String downArrowInITMCarousel = "button[class=\"scroll-button right itm-scroll scroll-button-visible\"]";
	public static String ItmBar = "//div[@id='itm-bar']";
	
	public static String addPersonComInput = "//input[@class='typeaheadInput']";
	//ITM SubBubble
	public static String composeSubBubble = "li[aria-label='PLACEHOLDER'] button[aria-label='Compose']";
	public static String filterSubBubble = "li[aria-label='PLACEHOLDER'] button[aria-label='Filter']";
	public static String chatSubBubble = "li[aria-label='PLACEHOLDER'] button[aria-label='Teams web chat ']";
	public static String itmEntryName = "li[aria-label='PLACEHOLDER'] span[class='setLabel']";
	
	public static String collapseIcon = "//button[@aria-label='Collapse']";
	public static String expandIcon = "//button[@aria-label='Expand']";
	
	private ItmNavUIConstants() {}

}
