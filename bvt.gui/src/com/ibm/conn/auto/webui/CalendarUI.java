package com.ibm.conn.auto.webui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseEvent;
import com.ibm.conn.auto.appobjects.base.BaseEvent.Days;
import com.ibm.conn.auto.appobjects.base.BaseEvent.RepeatEvery;
import com.ibm.conn.auto.appobjects.base.BaseEvent.RepeatType;
import com.ibm.conn.auto.webui.cloud.CalendarUICloud;
import com.ibm.conn.auto.webui.onprem.CalendarUIOnPrem;


public abstract class CalendarUI extends ICBaseUI {

	public CalendarUI(RCLocationExecutor driver) {
		super(driver);
	}
	
	private static Logger log = LoggerFactory.getLogger(CalendarUI.class);

	/** Calendar Widget */
	public static String CreateEvent = "css=a[dojoattachpoint='btnAddEvent']:contains(Create an Event),a:contains(^Create an Event$)";
	public static String CreateEventLink = "css=a[dojoattachpoint='btnAddEventLink']:contains(Create an Event)";
	public static String EventListTab = "css=#listTab";
	public static String EventGridTab = "css=#gridTab";
	public static String EventWidgetLoadingComplete = "css=div[id='gadgetLoading'][style='text-align: center; display: none;']";
	public static String fullPageZone = "css=span#widget-container-fullpage>div";
	public static String startTimeDropdownList = "css=#widget_calendar_event_editor-stime_dropdown div.dijitTimePickerItemInner";
	public static String endTimeDropdownList = "css=#widget_calendar_event_editor-etime_dropdown div.dijitTimePickerItemInner";
	public static String EventTime = "css=li[dojoattachpoint='timeAP']";
	
	/** Create Event Form  */
	public static String EventTitle = "css=#calendar_event_editor-subject";
	public static String EventTag = "css=#calendar_event_editor-tagsAsString";
	public static String EventLocation = "css=#calendar_event_editor-location";
	public static String EventAllDay = "css=input[id='calendar_event_editor-allDay']";
	public static String EventNotifyCommunity = "css=input[id='calendar_event_editor-notify']";
	public static String EventRepeats = "css=#calendar_event_editor_rec-recurrenceEnable_link";
	public static String EventSubmit = "css=input#calendar_event_editor-submit";
	public static String EventCancel = "css=input#calendar_event_editor-cancel";
	public static String EventRepeatFreq = "css=#calendar_event_editor_rec-rectype";
	public static String EventRepeatCheckbox = "css=#calendar_event_editor_rec-recurrenceDisable";
	public static String EventFollow = "css=a[title=Follow]";
	public static String EventStopFollowOption = "css=span[dojoattachpoint='followEvent'] a:contains('Stop Following')";
	public static String EventSubscribeToEvent = "css=a:contains(iCal feed)";
	public static String EventStartDate = "css=input[id='calendar_event_editor-sdate']";
	public static String EventStartTime = "css=input[id='calendar_event_editor-stime']";
	public static String EventEndDate = "css=input[id='calendar_event_editor-edate']";
	public static String EventEndTime = "css=input[id='calendar_event_editor-etime']";
	public static String EventRepeatUntil = "css=input[id='calendar_event_editor_rec-until']";
	public static String EventSave = "css=input[id='calendar_event_editor-submit']";
	public static String EventRepeatMenu = "css=select[id='calendar_event_editor_rec-rectype']";
	public static String EventRepeatWeeks = "css=select[id='calendar_event_editor_rec-interval']";
		
	/**event repeats monthly option**/
	public static String EventCheckMonthlyByDate ="css=#calendar_event_editor_rec-checkByDateMonthly";
	public static String EventMonthlyByDateValue ="css=#calendar_event_editor_rec-recByDateType";
	public static String EventMonthWillSkippedMsgDiv = "css=#com_ibm_oneui_controls_MessageBox_0";
	public static String EventCheckMonthlyByDay ="css=#calendar_event_editor_rec-checkByDayMonthly";
	public static String EventMonthlyByDateTypeWeek = "css=#calendar_event_editor_rec-recNthWeek";
	public static String EventMonthlyByDatetypeDay ="css=#calendar_event_editor_rec-recNthDay";
	
	/** Event Objects */
	public static String locationText = "css=li[dojoattachpoint='location']";
	public static String EditLink = "css=a:contains(Edit)";	
	public static String lotusTag = "css=li[class='lotusTags'] span a";	
	public static String viewPickerImg = "css=img[dojoattachpoint='viewPickerImg']";
	public static String initialNavigatorText = "css=#dwa_cv_calendarView_0-navigator-current-inner";
	public static String CalendarViewEntry = "//div[contains(@id, 'dwa_cv_calendarView_0-entry')]";
	public static String CalendarViewEntryDay = "css=div[class='s-cv-entry-innerframe']";
	public static String CalendarViewEntryGrid = CalendarUI.CalendarViewEntry + "/../..";
	public static String CalendarViewEntrySelected = "css=div#dwa_cv_calendarView_0-entry-selected";
	public static String CalendarViewNextDay = "css=img[id^=dwa_cv_calendarView_0-image][alt='Next day']";
	public static String CalendarViewNextMonth = "css=img[id^=dwa_cv_calendarView_0-image][alt='Next month']";
	public static String CalendarViewPrevMonth = "css=img[id^=dwa_cv_calendarView_0-image][alt='Previous month']";
	public static String EditEntireSeries = "css=input#calendar_event_editor-series";
	public static String CalendarViewAllDay = "//div[@id='dwa_cv_calendarView_0-allday']";
	public static String CalendarViewheader = "css=div#dwa_cv_calendarView_0-header-date0";
	public static String CalendarView = "css=a[dojoattachpoint=viewPickerLink]";
	public static String CalendarView_OneDay = "css=td[id$='_gridView_menu_DAY_text']";
	public static String CalendarView_FiveDay = "css=td[id$='_gridView_menu_FIVEDAY_text']";
	public static String CalendarView_Week = "css=td[id$='_gridView_menu_WEEK_text']";
	public static String CalendarView_Timecloumn = "css=div[id^=dwa_cv_calendarView_0-timeslot-date]";
	public static String CalendarViewDelBtn = "css=a[title='Delete the selected event']";
	public static String DelConfirmPane = "css=div[id^='dijit_Dialog_'][class='dijitDialog dijitContentPane']";
	public static String DelConfirmMsg = "Are you sure you want to delete this event?";
	public static String DelBtn = "css=input[value=Delete]";
	public static String pastEventLink = "css=a:contains('Show Past Events')";	
	public static String CommentArea= "css=textarea[title='Enter your comment:']"; 	
	public static String CommentList = "css=ul[class='lotusCommentList'][dojoattachpoint='commentListAP']";
	public static String CommentContent = "css=div[id$=_commentList] div.lotusPostDetails";
	public static String EventList = "css=tr[id^='urn:lsid:ibm.com:calendar:event:'][id$='header']";
	public static String EventNameList = "css=tr[id^='urn:lsid:ibm.com:calendar:event:'][id$='header'] a:contains('PLACEHOLDER')";
	public static String LinkInEventDescription = "css=div[dojoattachpoint='description'] a";
	public static String OKBtn = "css=input[value=OK],a[title='OK']";
	public static String EventTabFocus = "css=li[id='listTabItem']";
	public static String CalendarTabFocus = "css=li[id='gridTabItem']";
	public static String Event_EventCreateButton = "css=div[id$='_listView'] div span a:contains(Create an Event)";
	public static String Cal_EventCreateButton = "css=div[id$='_gridView'] div span a:contains(Create an Event)";
	public static String WillNotAttend = "css=span[dojoattachpoint='rsvpEvent'] a:contains(Will Not Attend)";
	public static String WillAttend = "css=span[dojoattachpoint='rsvpEvent'] a:contains(Will Attend)";
	public static String Follow = "css=a[title=Follow]";
	public static String StopFollowing = "css=span[dojoattachpoint='followEvent'] a:contains('Stop Following')";
	public static String Success_Message_Text = "css=div[class='lotusMessage2 lotusSuccess'] div span span";
	public static String SuccessMsgBox = "css=div[class='lotusMessage2 lotusSuccess']";
	public static String MsgBoxcloseButton = "css=div[class='lotusMessage2 lotusSuccess'] a[title=Close]";
	public static String Edit = "css=span[dojoattachpoint='editEvent'] a:contains(Edit)";
	public static String EditEventLink = "css=a[dojoattachpoint='btnEditEventLink']:contains(Edit Event)";
	public static String MoreActions = "css=span[dojoattachpoint='btnMoreActions'] a:contains(More Actions)";
	public static String NotifyOtherPeopleDialog = "css=div.lotusDialogHeader h1 span:contains(Notify Other People)";
	public static String NotifyOtherPeopleText = "Notify Other People";
	public static String NotifyOtherPeopleMsg = "css=textarea[id=lconn_calendar_Notification_0-message-textarea]";
	public static String NotifyOtherPeopleInput = "css=input#lconn_calendar_FilteringCheckbox_0FilterTextbox";
	public static String NotifyOtherPeopleList = "css=div.peopleList.lconnReady div";
	public static String NotifyOtherPeopleListCheckbox = "css=div.lconnNotify.lotusLeft";
	public static String NotifyOtherPeopleOKBtn = "css=input#dialog_ok_btn";
	public static String NotifyOtherPeopleReceiver = "css=div[dojoattachpoint=receiverList_AP]";
	public static String AddAComment = "link=Add a comment...";
	public static String AddCommentTextField = "css=#lconn_search_searchPanel_SearchBar_1";
	public static String AddCommentSaveButton = "css=form[dojoattachpoint='addCommentFormAP'] input[value='Save']";
	public static String BackToCommunityEventsLink = "link=Back to community events";
	public static String EventEditorStartDate = "css=input#calendar_event_editor-sdate";
	public static String EventEditorEndDate = "css=input#calendar_event_editor-edate";
	public static String EventEditorStartTime = "css=input#calendar_event_editor-stime";
	public static String EventEditorEndTime = "css=input#calendar_event_editor-etime";
	public static String EventEditorUntilDate = "css=#calendar_event_editor_rec-until";
	public static String EventEditorSunCheckBox = "css=input[id$=SU]";
	public static String EventEditorSunCheckBox_Unchecked = CalendarUI.EventEditorSunCheckBox+"[aria-checked=false]";
	public static String EventEditorSunCheckBox_Checked = CalendarUI.EventEditorSunCheckBox+"[aria-checked=true]";
	public static String ConfirmDialogDeleteButton = "css=input[value='Delete']";
	public static String ConfirmDialogCancelButton = "css=input[value='Cancel']";
	public static String ConfirmDialogDeleteInstance = "css=span[class='title']:contains(Confirm)";
	public static String ConfirmDialogDeleteAll = "css=#deleteAll";
	public static String EventWidgetOP = "css=a[aria-label='Actions for: Events']";
	public static String EditWidgetOP = "Edit";
	public static String EditBlogsWidget = "css=td[id='dijit_MenuItem_23_text']";
	public static String HideWidgetOP = "Hide";
	public static String HideBlogsWidgetButton = "css=input[id='dialog1.button']";
	public static String HelpWidgetOP = "Help";
	public static String editMsg = "Edit Calendar Settings";
	public static String editWarnMsg = "You must save changes on each tab before moving to another tab.";
	public static String authorRoleRB = "css=input[id$='_prefs-author']";
	public static String readerRoleRB = "css=input[id$='_prefs-reader']";
	public static String savecloseBtn = "css=input[id$='_prefs-saveclose']";
	public static String removeEventMsg = "Warning: the following widget and all data associated with it will be permanently deleted!";
	public static String addToPersonalCalendar = "css=a:contains(Add to Personal Calendar)";
	public static String tagCloudWidget = "css=div#widget-tagcloud";
	public static String tagCloudListView = "css=ul#widget-tagcloud-tagcloudImpl_tagList";
	public static String tagCloudCloudView = "css=div#widget-tagcloud-tagcloudImpl_tagCloudView";
	public static String tagCloudRelatedTag = "css=ul[class='lotusList lotusTags lotusRelatedTags']";
	public static String tagTextBox = "css=input#widget-tagcloud-tagcloudImplcommonTagsTypeAhead";
	public static String tagTypeahead = "css=div#widget-tagcloud-tagcloudImplcommonTagsTypeAhead_popup";
	public static String tagLinksinTagTypeahead = "css=div[id^=widget-tagcloud-tagcloudImplcommonTagsTypeAhead_popup]";
	public static String tagLinkinTagTypeahead = "css=li#widget-tagcloud-tagcloudImplcommonTagsTypeAhead_popup0";
	public static String tagSearchBtn = "css=div[id='lconnTagCloudContent-widget-tagcloud']  input[alt='Search']";
	public static String CalendartagTypeahead = "css=div#calendar_event_editor-tagsAsString_popup";
	public static String CalendartagLinksinTypeahead = CalendartagTypeahead + " div[id^=calendar_event_editor-tagsAsString_popup]";
	public static String CalendartagLinkinTypeahead = "css=div#calendar_event_editor-tagsAsString_popup0";	
	public static String CalendarView_TwoDay = "css=td[id$='_gridView_menu_TWODAY_text']";
	public static String EventHeader = "css=h1[class='bidiAware']";
	public static String AddtoPersonalCalDia = "css=div[id^=dijit_Dialog_] h1:contains('Add to Personal Calendar')";
	public static String NotifyOtherpeopleReceiverLink = NotifyOtherPeopleReceiver + " a img[class='lotusDelete']";
	public static String SaveButton = "css=input[value=Save]";
	public static String CancelButton = "css=input[value='Cancel']";
	public static String RepeatingEvent = "css=a:contains('Repeating Event')";
	public static String DeleteComment = "css=div.lotusActions ul.lotusInlinelist li.lotusFirst a";
	public static String attendEntireSeriesRadioButton = "css=li input[id='rsvpAll']";
	public static String attendButtonOnConfirmDialog = "css=input[class='lotusFormButton submit'][value='Attend']";
	
	public static String mentionSelUserSearch = "css=div[id^='lconn_core_PeopleTypeAheadMenu_'][id$='_searchDir']";
	public static String mentionSelUsers_Onprem = "css=div[class^='dijitMenuItem'][id^='lconn_core_PeopleTypeAheadMenu_']";
	public static String mentionSelUsers_OnCloud = "css=div[id^='bhc_PeopleTypeAheadMenu_']";
	public static String bizCardLink = "css=a#semtagmenuBox";
	public static String bizCard = "css=div#semtagmenuCard";
	
	
	public static String TagListinListView = "css=ul#widget-tagcloud-tagcloudImpl_tagList li span";
	public String sTagLinkinTagCloud(String sTag){
		return "css=#widget-tagcloud-tagcloudImpl_tagCloudView li a[title^='Show the search results of tag " + sTag + ", count']";
	}	
	
	public static String mentionErrorMsgDiv = "css=div[dojoattachpoint=permissionWarningMessage]";
	public abstract void mention_addMember(String identifier);
	public abstract String mentionTypeahead();
	public abstract void verifyBizCard();
	
	@SuppressWarnings("serial")
	private List<Days> listdays = new ArrayList<Days>() {
	    {
	        add(Days.Sun);
	        add(Days.Mon);
	        add(Days.Tue);
	        add(Days.Wed);
	        add(Days.Thu);
	        add(Days.Fri);
	        add(Days.Sat);		        
	    }
	 };

	 /** Computed Strings */
	 
	public String AttendeeList(String sDisplayName){
		return "css=ul[dojoattachpoint='attendeeListAP'] a:contains(" + sDisplayName + ")";
		}	

	public String sRemoveTagLinkinmain(String sTag){
		return "css=a[title='Remove the tag " + sTag + " from the selected filter tags'].lotusFilter";
	}

	public String getEventSelector(BaseEvent event){
		return "css=a:contains("+ event.getName() +")";
	}
	
	public String getEventSelectoronCommPage(BaseEvent event){
		return "css=a:contains(" + event.getName().substring(0,25) + ")";
	}
	
	public String getEventSelectoronHomePage(BaseEvent event){
		return "css=" + HomepageUIConstants.eventWidget + "a:contains(" + event.getName().substring(0,25) + ")";
	}
	
	public String getMentionPersonLink(String sName){
		return "link=" + "@" + sName;
	}

	/********************* end of defines  **************/

	public void gotoBackCommunityEvents() {
		log.info("INFO: Select back to community events link");
		clickLinkWait(BackToCommunityEventsLink);
		try {
			fluentWaitPresent(EventHeader);
		} catch (Exception e){
			log.warn("Click did not open the form as expected so using javascript to click");
			clickLinkWithJavascript(BackToCommunityEventsLink);
			fluentWaitPresent(EventHeader);
		}
	}

	public void addCommentToEvent(String comment) {
		log.info("INFO: Adding a comment to an Event");
		if (driver.isElementPresent(AddCommentTextField)) {
			driver.typeNative(comment);
		} else
			typeNativeInCkEditor(comment);

		log.info("INFO: Save comment");
		clickLinkWait(AddCommentSaveButton);
	}
	
	public void create(BaseEvent event) throws Exception {
		
		log.info(driver.getSingleElement(EventTabFocus).getAttribute("class"));

		//find the view you are currently using (Event or Calendar)	
		if(driver.getSingleElement(EventTabFocus).getAttribute("class").contentEquals("lotusSelected")){						
			//User is currently in Event view
			log.info("INFO: User is using the Event Tab view");
			driver.getFirstElement(Event_EventCreateButton).click();
		}else if(driver.getSingleElement(CalendarTabFocus).getAttribute("class").contentEquals("lotusSelected")){
			//User is currently in Calendar view
			log.info("INFO: User is using the Calendar Tab view");
			driver.getFirstElement(Cal_EventCreateButton).click();			
		}else{
			//Unable to determine view attempting generic CreateEvent click
			log.info("WARNING: Unable to detect which Event view is being used");
			log.info("INFO: Attempting to click on Generic CreateEvent button");
			driver.getFirstElement(CreateEvent).click();
		}
		
		log.info("INFO: Adding Title of Event");
		driver.getSingleElement(EventTitle).clear();
		driver.getSingleElement(EventTitle).type(event.getName());
		
		/**If we have tags add them */
		if(!event.getTags().isEmpty()){
			log.info("INFO: Adding Tags");
			driver.getSingleElement(EventTag).clear();
			driver.getSingleElement(EventTag).type(event.getTags());
		}
				
		/** If we are changing end date and it is greater then 24 hours from start date this will fail*/
		if(!event.getStartDateText().isEmpty()){
			//change start time subject to 24 hour limitation
			log.info("INFO: Changing Start date to " + event.getStartDateText());
			pickDojoDate(EventStartDate, event.getStartDate(), event.getUseCalPick());
		}
		
		/** If we are changing end date and it is greater then 24 hours from start date this will fail*/
		if(!event.getStartTime().isEmpty()){
			//change start time subject to 24 hour limitation
			log.info("INFO: Changing Start time to " + event.getStartTime());
			driver.getSingleElement(EventStartTime).clear();
			driver.getSingleElement(EventStartTime).type(event.getStartTime());			
		}
		
		/** If we are changing end date and it is greater then 24 hours from start date this will fail*/
		if(!event.getEndDateText().isEmpty()){
			//change end date subject to 24 hour limitation
			log.info("INFO: Changing end date to " + event.getEndDateText());	
			pickDojoDate(EventEndDate,  event.getEndDate(), event.getUseCalPick());
		}

		/** If we are changing end date and it is greater then 24 hours from start date this will fail*/
		if(!event.getEndTime().isEmpty()){
			//change end time subject to 24 hour limitation
			log.info("INFO: Changing end time to " + event.getEndTime());
			driver.getSingleElement(EventEndTime).clear();
			driver.getSingleElement(EventEndTime).type(event.getEndTime());			
		}		
		
		/**If event is an all day event check box  */
		if(event.getAllDayEvent()){
			//make event all day event
			log.info("INFO: Making event and all day event");
			clickLinkWait(CalendarUI.EventAllDay);
		}
		
		/**If event repeats select it*/
		if(event.getRepeat()){
			//select the Repeats link
			log.info("INFO: Event repeats selecting Repeat option");
			clickLinkWait(EventRepeats);
			
			//Select repeat Type
			log.info("INFO: Selecting Repeat type " + event.getRepeatType().repeatType);
			driver.getSingleElement(EventRepeatMenu).useAsDropdown().selectOptionByVisibleText(event.getRepeatType().toString());

		/**Repeats ON section */
			/** check to see if user is requesting non default if repeatDays = "" then use pre populated default*/
			if(!event.getRepeatDays().isEmpty()){
				if(event.getRepeatType().equals(RepeatType.DAILY)){
					//Clear existing checks
					log.info("INFO: Clearing existing Day Checks");
					checkDayRepeat(listdays, event.getRepeatType());
					//Add new user selected days
					log.info("INFO: Add user selected Days");
					checkDayRepeat(event.getRepeatDays(), event.getRepeatType());
				}else{
					List<Days> today = new ArrayList<Days>();
					Calendar calendar = Calendar.getInstance();
					int day = calendar.get(Calendar.DAY_OF_WEEK); 
							
					switch (day) {
					case Calendar.SUNDAY:
						today.add(Days.Sun);
						break;
					case Calendar.MONDAY:
						today.add(Days.Mon);
						break;
					case Calendar.TUESDAY:
						today.add(Days.Tue);
						break;
					case Calendar.WEDNESDAY:
						today.add(Days.Wed);
						break;
					case Calendar.THURSDAY:
						today.add(Days.Thu);
						break;
					case Calendar.FRIDAY:
			        	today.add(Days.Fri);
			        	break;
			    	case Calendar.SATURDAY:
			        	today.add(Days.Sat);
			        	break;
					}
					//Clear existing checks
					log.info("INFO: Clearing existing Day Checks");
					checkDayRepeat(today, event.getRepeatType());
					//Add new user selected days
					log.info("INFO: Add user selected Days");
					checkDayRepeat(event.getRepeatDays(), event.getRepeatType());
					
					/**Check to see if user changed Repeat Every Amount of Weeks by comparing to default of weekly*/
					if(!event.getRepeatEvery().equals(RepeatEvery.WEEKLY)){
						log.info("INFO: Selecting weeks to repeat " + event.getRepeatEvery().getWeeks());
						driver.getSingleElement(EventRepeatWeeks).useAsDropdown().selectOptionByVisibleText(event.getRepeatEvery().getWeeks());
					}
				}
			}
				
			
		/** Repeats UNTIL Section */
			/** NOTE: If user wants to change the repeats UNTIL date */
			/** NOTE: If repeatUntil is empty it will use default date provided by connections*/
			if(!event.getRepeatUntilText().isEmpty()){
				log.info("INFO: Changing until date to " + event.getRepeatUntilText());	
				pickDojoDate(EventRepeatUntil,  event.getRepeatUntil(), event.getUseCalPick());
			}
			
		}

		/**If we have a location add it */
		if(!event.getLocation().isEmpty()){
			log.info("INFO: Adding location");
			driver.getSingleElement(EventLocation).clear();
			driver.getSingleElement(EventLocation).type(event.getLocation());
		}
		
		//if we have a description add it
		if(!event.getDescription().isEmpty()){
			log.info("INFO: Adding Description");
			typeNativeInCkEditor(event.getDescription());
		}
		
		//If we are communicate the event to the whole community
		if(event.getNotifyCommunity()){
			log.info("INFO: Selecting notify community");
			clickLinkWait(CalendarUI.EventNotifyCommunity);
		}
		
		//Save the new Event
		clickLinkWait(EventSave);
	}
	
	
	public void checkDayRepeat(List<Days> repeatDays, RepeatType type) throws Exception{
	
		/** Iterate through provided list */
		Iterator<Days> it = repeatDays.iterator();
		while(it.hasNext())
		{
		    Days day = it.next();
		    log.info("INFO: Selecting Day " + day.toString());
		    if(type.equals(RepeatType.DAILY)){				    	
		    	clickLinkWait(day.getLinkDay());
		    	log.info("INFO: Repeat type is Daily");
		    }
		    else if(type.equals(RepeatType.WEEKLY)){
		    	clickLinkWait(day.getLinkWeek());
		    	log.info("INFO: Repeat type is Weekly");
		    }else
		    	throw new Exception("ERROR: Must have Repeat Type");
		}
		
		
	}
		
	public List<Element> getCommentList(String selector){
		List<Element> list = driver.getElements(selector);
		return list;
	}


	
	public String EventSubscribeWebcalLink(){
		String surl = driver.getCurrentUrl();
		String sWebcal = "webcal://";
		if(surl.substring(0, surl.indexOf("//")).equals("https:")) sWebcal = "webcals://";
		
		return "css=a:contains(" + sWebcal + ")";
	}

	public String getMentionUserInList(String locInList){
		return "css=div[id='" + locInList + "']";
	}
	
	
	
	public abstract void verifyPublicURL(String sPublicCalendarHandle, String sPublicEventHandle, String snormaleventName, User testUser);
	
	
	public static CalendarUI getGui(String product, RCLocationExecutor driver){
		if(product.toLowerCase().equals("cloud")){
			return new  CalendarUICloud(driver);
		} else if(product.toLowerCase().equals("onprem")) {
			return new  CalendarUIOnPrem(driver);
		} else if(product.toLowerCase().equals("vmodel")) {
			return new  CalendarUIOnPrem(driver);
		}  else if(product.toLowerCase().equals("multi")) {
			return new  CalendarUIOnPrem(driver);
		}  else {
			throw new RuntimeException("Unknown product name: " + product);
		}
	}
	

	public String getRepeatFrequentOption() {
		Select typeselectBox = new Select((WebElement) driver.getSingleElement(CalendarUI.EventRepeatFreq).getBackingObject());
		return typeselectBox.getFirstSelectedOption().getText();
	}
	
}
