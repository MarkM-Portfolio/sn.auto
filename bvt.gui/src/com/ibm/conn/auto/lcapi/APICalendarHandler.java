package com.ibm.conn.auto.lcapi;

import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.SimpleTimeZone;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Category;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import com.ibm.conn.auto.appobjects.base.BaseEvent;
import com.ibm.conn.auto.util.Mentions;
import com.ibm.lconn.automation.framework.services.common.LCServiceException;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.SortBy;
import com.ibm.lconn.automation.framework.services.common.StringConstants.SortField;
import com.ibm.lconn.automation.framework.services.common.StringConstants.SortOrder;
import com.ibm.lconn.automation.framework.services.communities.CommunitiesService;
import com.ibm.lconn.automation.framework.services.communities.nodes.Calendar;
import com.ibm.lconn.automation.framework.services.communities.nodes.CommentToEvent;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.communities.nodes.Event;

public class APICalendarHandler extends APICommunitiesHandler {

	private static final Logger log = LoggerFactory.getLogger(APICalendarHandler.class);

	public APICalendarHandler(String serverURL, String username, String password) {

		super(serverURL, username, password);
	}

	@Override
	protected CommunitiesService getService(AbderaClient abderaClient, ServiceEntry generalService) {
		try {
			return new CommunitiesService(abderaClient, generalService);
		} catch (LCServiceException e) {
			Assert.fail("Unable create Communities service: " + e.getMessage());
			return null;
		}
	}
	
	public Event createEvent(BaseEvent eventObj, Community community){
		
		log.info("Creating Event in Community: " + community.getTitle());
		
		String custom = StringConstants.STRING_YES_LOWERCASE;
		String frequency = null;
		String interval = "";
		String until = null;
		int allday = 0;
		String event_url=null;
		String timezone = "";
		String dayLightSavingsTime = "";
		String byDay = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf1 = new SimpleDateFormat("MM/dd/yyyy");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdf3 = new SimpleDateFormat("hh:mm");
		SimpleDateFormat sdf4 = new SimpleDateFormat("HH:mm:ss");
		java.util.Calendar cal = java.util.Calendar.getInstance(new SimpleTimeZone(0,"GMT"));
		sdf2.setCalendar(cal);
		sdf.setCalendar(cal);
		sdf4.setCalendar(cal);
		
		if(eventObj.getRepeat()) {
			custom = StringConstants.STRING_NO_LOWERCASE;
			frequency = eventObj.getRepeatType().toString();
			if(frequency.equalsIgnoreCase(StringConstants.STRING_WEEKLY)){
				frequency = StringConstants.STRING_WEEKLY;
				if(eventObj.getRepeatEvery().equals(BaseEvent.RepeatEvery.WEEKLY)) interval = "1";
				else if(eventObj.getRepeatEvery().equals(BaseEvent.RepeatEvery.TWO_WEEKS)) interval = "2";
				else if(eventObj.getRepeatEvery().equals(BaseEvent.RepeatEvery.THREE_WEEKS)) interval = "3";
				else if(eventObj.getRepeatEvery().equals(BaseEvent.RepeatEvery.FOUR_WEEKS)) interval = "4";
				else if(eventObj.getRepeatEvery().equals(BaseEvent.RepeatEvery.FIVE_WEEKS)) interval = "5";
			}
			else{
				frequency = "daily";
				if(eventObj.getRepeatDays().size() == 0)
					byDay = "SU,MO,TU,WE,TH,FR,SA";
				else
					byDay = eventObj.getRepeatDays().toString();						
			}
			until = eventObj.getRepeatUntilText();
			try {
				until = sdf.format(sdf1.parse(until));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(eventObj.getAllDayEvent()) allday = 1;		
		
		
		//Set the date and time using GMT format 
		String sStartTime = "";
		String sEndTime = "";
		
		
		String sDate="";
		String eDate="";
		String sTime="";
		String eTime="";
		
		if(eventObj.getStartDateText() == ""){
			log.info("INFO: Calculate the default date and time");
			sStartTime = sdf2.format(cal.getTime());
			cal.add(java.util.Calendar.HOUR_OF_DAY, 1);
			sEndTime = sdf2.format(cal.getTime());
			
			sDate = sStartTime.substring(0,10);
			eDate = sEndTime.substring(0,10);
			sTime = sStartTime.substring(11,19);
			eTime = sEndTime.substring(11,19);						
		}
		else{
			log.info("INFO: Calculate the specific date");			
			try {
				sDate = sdf.format(sdf1.parse(eventObj.getStartDateText()));
				eDate = sdf.format(sdf1.parse(eventObj.getEndDateText()));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(eventObj.getStartTime() != ""){
			log.info("INFO: Calculate the specific time");
			sTime = eventObj.getStartTime().substring(0,eventObj.getStartTime().indexOf(" "));
			eTime =  eventObj.getEndTime().substring(0,eventObj.getEndTime().indexOf(" "));			
			try {
				sTime = sdf4.format(sdf3.parse(sTime));
				eTime = sdf4.format(sdf3.parse(eTime));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		sStartTime = sDate + "T" + sTime + "Z";
		sEndTime = eDate + "T" + eTime + "Z";
		
		
		Event event = new Event(eventObj.getName(), custom, frequency, interval, until, sStartTime, sEndTime, allday, timezone, dayLightSavingsTime, byDay); 		
				
		Feed remoteAppsFeed = (Feed) service.getCommunityRemoteAPPs(community.getRemoteAppsListHref(), true, null, 0, 50, null, null, SortBy.NAME, SortOrder.ASC, SortField.NAME, null) ;
		assertTrue(remoteAppsFeed != null);				
		for (Entry entry : remoteAppsFeed.getEntries()){
			for (Category category : entry.getCategories()){
				if (category.getTerm().equalsIgnoreCase("calendar")){	
					for(Link link : entry.getLinks()) {
						if ( link.getRel().contains(StringConstants.REL_REMOTEAPPLICATION_FEED) ){
							event_url = link.getHref().toString();
						}
					}						
					log.info("Event URL : "+event_url);
					break;
				}
			}
		}
		
		String eventUrl = event_url.replace("type=event&calendarUuid", "calendarUuid");
		ExtensibleElement eventFeed = service.postCalendarEvent(eventUrl, event);
		assertTrue(eventFeed != null);
		
		return event;						
	}
	
	/**
	 * Adds a calendar event to a community
	 * 
	 * @param community - The community in which the calendar event is to be added
	 * @param event	- The event to be added to the calendar
	 * @return - Returns the Calendar instance for the calendar event that has been added (returns null if the process fails)
	 */
	public Calendar addCalendarEvent(Community community, BaseEvent event) {
		
		log.info("INFO: Adding a calendar event for the " + community.getTitle() + " community");
		
		// Create the Event instance of the calendar event
		Event calendarEvent = createCalendarEventInstance(event.getName().trim(), event.getRepeat(), event.getAllDayEvent());
		
		// Set the content and tags for the calendar event
		calendarEvent.setContent(event.getDescription().trim());
		calendarEvent.setTags(event.getTags());
		
		log.info("INFO: The calendar event instance has been created: " + calendarEvent.toEntry().toString());
		
		return executeCreateCalendarEvent(community, calendarEvent);
	}

	/**
	 * Creates an Event instance of a calendar event based on the BaseEvent instance
	 * Private access only since there is no requirement to use this method externally
	 * 
	 * @param eventTitle - The title of the calendar event
	 * @param isRepeatingEvent - True if the event is a repeating event, false otherwise
	 * @param isAllDayEvent - True if the event is an all day event, false otherwise
	 * @return - The Event instance of the calendar event
	 */
	private Event createCalendarEventInstance(String eventTitle, boolean isRepeatingEvent, boolean isAllDayEvent) {
		
		log.info("INFO: Creating the Event instance of the new calendar event to be created");
		
		String title = eventTitle;
		String custom = StringConstants.STRING_YES_LOWERCASE;
		String frequency = null;
		String interval = null;
		String until = null;
		String start = "2016-01-17T17:00:00+00:00";
		String end = "2016-01-17T22:00:00+00:00";
		String daylight = "2016-03-27T11:34:10+01:00/2016-10-30T10:34:10+00:00";
		String byDay = null;
		int allDay = 0;
		if(isRepeatingEvent) {
			custom = StringConstants.STRING_NO_LOWERCASE;
			frequency = "daily";
			interval = "";
			until = "2016-01-24T00:00:00+00:00";
			byDay = "SU,MO,TU,WE,TH,FR,SA";
		}
		if(isAllDayEvent) {
			allDay = 1;
		}
		
		log.info("INFO: The Event instance of the new calendar event has been created successfully");
		return new Event(title, custom, frequency, interval, until, start, end, allDay, null, daylight, byDay);
	}
	
	/**
	 * POSTs the Event instance of the calendar event to the URL to create the calendar event
	 * Private access only since there is no requirement to use this method externally
	 * 
	 * @param community - The community in which the calendar event is to be created
	 * @param calendarEvent - The Event instance of the calendar event to be created
	 * @return - The new Calendar instance of the calendar event if the operation is successful, null otherwise
	 */
	private Calendar executeCreateCalendarEvent(Community community, Event calendarEvent) {
		
		log.info("INFO: Performing the actions to create the calendar event");
		
		// Create the URL to which the calendar event will be posted
		String calendarEventURL = service.getServiceURLString() + "/calendar/atom_form/calendar/event?calendarUuid="
																   + community.getUuid();
		log.info("INFO: The URL to which the calendar event entry will be POSTed has been created: " + calendarEventURL);
		
		Entry postedEventEntry = (Entry) service.postEntry(calendarEventURL, calendarEvent.toEntry());
		
		if(postedEventEntry.toString().indexOf("resp:error=\"true\"") == -1) {
			log.info("INFO: Calendar event successfully posted");
			Calendar calendar = new Calendar(postedEventEntry);
			calendar.setId(postedEventEntry.getId());
			calendar.setLinks(getEntryLinks(postedEventEntry));
			calendar.setTags(calendarEvent.getTags());
			return calendar;
		} else {
			log.info("INFO: Calendar event could not be posted");
			log.info(postedEventEntry.toString());
			return null;
		}
	}
	
	/**
	 * Retrieves all relevant links from the Entry response for any action and returns them in the correct format
	 * Private access only since there is no requirement to use this method externally
	 * 
	 * @param entry - The Entry whose links are to be retrieved
	 * @return - The HashMap<String Link> instance of the links in the correct format
	 */
	private HashMap<String, Link> getEntryLinks(Entry entry) {
		
		log.info("INFO: Retrieving the links from the entry (self link, edit link etc)");
		
		HashMap<String, Link> entryLinks = new HashMap<String, Link>();
		
		// Set the common links for every created object such as events and comments
		entryLinks.put(StringConstants.REL_SELF + ":" + StringConstants.MIME_NULL, entry.getSelfLink());
		entryLinks.put(StringConstants.REL_EDIT + ":" + StringConstants.MIME_ATOM_XML, entry.getEditLink());
		
		// The following links are specific to calendar events themselves
		entryLinks.put("attend", entry.getLink("http://www.ibm.com/xmlns/prod/sn/calendar/event/attend"));
		entryLinks.put("follow", entry.getLink("http://www.ibm.com/xmlns/prod/sn/calendar/event/follow"));
		entryLinks.put("instances", entry.getLink("http://www.ibm.com/xmlns/prod/sn/calendar/event/instances"));
		return entryLinks;
	}
	
	/**
	 * Adds a calendar event to a community with the specified user mentioned in the calendar event description
	 * 
	 * @param community - The community in which the calendar event is to be added
	 * @param event	- The event to be added to the calendar
	 * @return - Returns the Calendar instance for the calendar event that has been added (returns null if the process fails)
	 */
	public Calendar addCalendarEventWithMentions(Community community, BaseEvent event, Mentions mentions) {
		
		log.info("INFO: Adding a calendar event with mentions for the " + community.getTitle() + " community");
		
		// Create the Event instance of the calendar event and set the content
		Event calendarEvent = createCalendarEventInstance(event.getName().trim(), event.getRepeat(), event.getAllDayEvent());
		
		String hrefBC = service.getServiceURLString().replace("/communities", "/profiles")
						+ "/html/profileView.do?userid=" + mentions.getUserUUID();
		log.info("INFO: The profile URL for " + mentions.getUserToMention().getDisplayName() + " has been created: " + hrefBC);
		
		String contentWithMentions = "<p dir=\"ltr\">" + event.getDescription().trim() + " "
									+ mentions.getBeforeMentionText().trim() + " "
									+ "<span class=\"vcard\"><a class=\"fn url\" href=\"javascript:;\" href_bc_=\""
									+ hrefBC + "\" role=\"button\">@" + mentions.getUserToMention().getDisplayName() + "</a>"
									+ "<span class=\"x-lconn-userid\" style=\"display: none\">"
									+ mentions.getUserUUID() + "</span></span> " + mentions.getAfterMentionText() + "</p>";
		log.info("INFO: The content with mentions has been set: " + contentWithMentions);
		
		// Set the content and tags for the calendar event
		calendarEvent.setContent(contentWithMentions);
		calendarEvent.setTags(event.getTags());
		log.info("INFO: The calendar event instance has been created: " + calendarEvent.toEntry().toString());
		
		return executeCreateCalendarEvent(community, calendarEvent);
	}
	
	/**
	 * Edits the description for any calendar event - also acts as updating the entire series of a repeating calendar event
	 * 
	 * @param calendarEvent - The Calendar instance of the calendar event whose description is to be edited
	 * @param newEventDescription - The description to which the current event description is to be changed
	 * @return - Returns a Calendar instance of the updated calendar event if the update operation is successful, null otherwise
	 */
	public Calendar editCalendarEventDescription(Calendar calendarEvent, String newEventDescription) {
		
		log.info("INFO: Updating / editing the description for the calendar event with title: " + calendarEvent.getTitle());
		
		// Retrieve the full entry instance of the calendar event to be updated
		Entry calendarEventEntry = (Entry) service.getCalendarFeedWithRedirect(calendarEvent.getSelfLink());
		log.info("INFO: The calendar event entry has been retrieved: " + calendarEventEntry.toString().replaceAll("\n", "").trim());
		
		// Analyse the calendar event entry to determine if this is an all day event and/or a repeating event
		List<Element> listOfExtensions = calendarEventEntry.getExtensions();
		boolean isAllDayEvent = false;
		boolean isRepeatingEvent = false;
		for(Element currentElement : listOfExtensions) {
			if(currentElement.getQName().getPrefix().equals("snx") &&
					currentElement.getQName().getLocalPart().equals("allday")) {
				if(Integer.parseInt(currentElement.getText()) == 1) {
					isAllDayEvent = true;
				}
			}
			if(currentElement.getQName().getPrefix().equals("snx") &&
					currentElement.getQName().getLocalPart().equals("recurrence")) {
				String elementToString = currentElement.toString();
				if(elementToString.indexOf("snx:until") > -1) {
					isRepeatingEvent = true;
				}
			}
		}
		log.info("INFO: The repeating event attribute of this calendar event has been determined to be '" + isRepeatingEvent + "'");
		log.info("INFO: The all day event attribute of this calendar event has been determined to be '" + isAllDayEvent + "'");
		
		// Set the parameters which will make up the updated Event instance for this calendar event
		Event updatedEvent = createCalendarEventInstance(calendarEvent.getTitle().trim(), isRepeatingEvent, isAllDayEvent);
		updatedEvent.setId(calendarEvent.getId());
		updatedEvent.setContent(newEventDescription);
		updatedEvent.setTags(calendarEvent.getTags());
		log.info("INFO: The entry to represent the updated calendar event has been created: " + updatedEvent.toEntry().toString());
		
		// Create the URL to which the updated event will be PUT
		String updateEventURL = calendarEvent.getEditLink();
		log.info("INFO: The URL to which the PUT request will be sent has been retrieved: " + updateEventURL);
		
		Entry updatedCalendarEvent = (Entry) service.putEntry(updateEventURL, updatedEvent.toEntry());
		
		if(updatedCalendarEvent.toString().indexOf("resp:error=\"true\"") == -1) {
			log.info("INFO: The calendar event description was updated successfuly");
			calendarEvent.setContent(newEventDescription);
			return calendarEvent;
		} else {
			log.info("ERROR: The calendar event description could not be updated");
			log.info(updatedCalendarEvent.toString());
			return null;
		}
	}
	
	/**
	 * Adds a comment to a calendar entry
	 * 
	 * PLEASE NOTE:
	 * In order for any user other than the creator of the community to post a comment in a Calendar Event,
	 * that user must be following the community or else their comment will return a response code 200 
	 * but will not be posted in the UI and/or posted to the calendar event.
	 * 
	 * @param calendarEvent - The Calendar instance of the calendar event to which the comment is to be posted
	 * @param commentToBePosted - A String containing the comment to be posted to the calendar event
	 * @return - Returns a CommentToEvent instance of the comment if the operation is successful, null otherwise
	 */
	public CommentToEvent commentOnCalendarEvent(Calendar calendarEvent, String commentToBePosted) {
		
		log.info("INFO: Posting a comment to the calendar event with title: " + calendarEvent.getTitle());
		
		String commentEventURL = getURLToPostCommentsToCalendarEvent(calendarEvent);
		
		if(commentEventURL != null) {
			log.info("INFO: The URL for posting a comment to the calendar feed has been found: " + commentEventURL);
			
			// Create the entry which represents the comment to be posted
			Entry newCommentEntry = Abdera.getNewFactory().newEntry();
			newCommentEntry.addCategory(StringConstants.SCHEME_TYPE, "comment", "Comment");
			newCommentEntry.setContent(commentToBePosted);
			log.info("INFO: The entry to represent the comment has been created: " + newCommentEntry.toString());
			
			Entry commentEvent = (Entry) service.postEntry(commentEventURL, newCommentEntry);
			
			if(commentEvent.toString().indexOf("resp:error=\"true\"") == -1) {
				log.info("INFO: Comment successfully added to the calendar event");
				CommentToEvent commentToEvent = new CommentToEvent(commentEvent);
				commentToEvent.setLinks(getEntryLinks(commentEvent));
				return commentToEvent;
			} else {
				log.info("INFO: Comment could not be added to the calendar event");
				log.info(commentEvent.toString());
				return null;
			}
		} else {
			return null;
		}
	}
	
	/**
	 * Retrieves the URL to which comment events can be POSTed to calendar events
	 * Private access only since there is no requirement to use this method externally
	 * 
	 * @param calendarEvent - The calendar event whose comment event URL is to be retrieved
	 * @return - The String containing the URL if the URL is successfully retrieved, null otherwise
	 */
	private String getURLToPostCommentsToCalendarEvent(Calendar calendarEvent) {
		
		log.info("INFO: Retrieving the URL to which comment entries will be posted for calendar event with title: " + calendarEvent.getTitle());
		String commentEventURL = null;
		
		// Retrieve the feed for this calendar event and determine the list of extensions from the feed
		String calendarEventEditLink = calendarEvent.getEditLink();
		Entry eventFeed = (Entry) service.getCalendarFeedWithRedirect(calendarEventEditLink);
		List<Element> eventExtensions = eventFeed.getExtensions();
		
		log.info("INFO: Attempting to retrieve the URL for posting a comment to the calendar event");
		int index = 0;
		boolean foundCommentURL = false;
		while(index < eventExtensions.size() && foundCommentURL == false) {
			Element currentElement = eventExtensions.get(index);
			String currentElementString = currentElement.toString();
			
			if(currentElementString.indexOf("<collection") > -1
					&& currentElementString.indexOf("/calendar/event/comment?eventInstUuid=") > -1) {
				commentEventURL = currentElement.getAttributeValue("href");
				foundCommentURL = true;
			}
			index ++;
		}
		
		if(commentEventURL != null) {
			log.info("INFO: Found the URL for posting comments to the calendar event as: " + commentEventURL);
			return commentEventURL;
		} else {
			log.info("ERROR: The URL for posting a comment to the calendar event could not be found");
			log.info(eventFeed.toString());
			return null;
		}
	}
	
	/**
	 * Adds a comment with mentions to another user to a calendar entry
	 * 
	 * PLEASE NOTE:
	 * In order for any user other than the creator of the community to post a comment in a Calendar Event,
	 * that user must be following the community or else their comment will return a response code 200 
	 * but will not be posted in the UI and/or posted to the calendar event.
	 * 
	 * @param calendarEvent - The Calendar instance of the calendar event to which the comment is to be posted
	 * @param mentions - The Mentions instance of the user to be mentioned in the comment
	 * @return - Returns a CommentToEvent instance of the comment if the operation is successful, null otherwise
	 */
	public CommentToEvent commentWithMentionsOnCalendarEvent(Calendar calendarEvent, Mentions mentions) {
		
		log.info("INFO: Posting a comment with mentions to " + mentions.getUserToMention().getDisplayName() + " to the calendar event with title: " + calendarEvent.getTitle());
		
		String commentEventURL = getURLToPostCommentsToCalendarEvent(calendarEvent);
		
		if(commentEventURL != null) {
			// Create the comment with mentions to be posted
			String commentWithMentions = mentions.getBeforeMentionText() + " <span class=\"vcard\">"
										+ "<span class=\"fn\">@" + mentions.getUserToMention().getDisplayName() + "</span>"
										+ "<span class=\"x-lconn-userid\">" + mentions.getUserUUID() + "</span></span>"
										+ " " + mentions.getAfterMentionText();
			log.info("INFO: The comment with mentions has been created: " + commentWithMentions);
			
			// Create the entry which represents the comment to be posted
			Entry newCommentEntry = Abdera.getNewFactory().newEntry();
			newCommentEntry.addCategory(StringConstants.SCHEME_TYPE, "comment", "Comment");
			newCommentEntry.setContent(commentWithMentions);
			log.info("INFO: The entry to represent the comment has been created: " + newCommentEntry.toString());
			
			Entry commentEvent = (Entry) service.postEntry(commentEventURL, newCommentEntry);
			
			if(commentEvent.toString().indexOf("resp:error=\"true\"") == -1) {
				log.info("INFO: Comment with mentions successfully added to the calendar event");
				CommentToEvent commentToEvent = new CommentToEvent(commentEvent);
				commentToEvent.setLinks(getEntryLinks(commentEvent));
				return commentToEvent;
			} else {
				log.info("INFO: Comment could not be added to the calendar event");
				log.info(commentEvent.toString());
				return null;
			}
		} else {
			return null;
		}
	}
	
	/**
	 * Edits the description for the first single instance of a calendar event
	 * 
	 * @param calendarEvent - The Calendar instance of the calendar event whose first single instance description is to be edited
	 * @param newEventDescription - The description to which the first single instance event description is to be changed
	 * @return - Returns true if the update operation is successful, false otherwise
	 */
	public boolean editCalendarEventDescriptionFirstSingleInstance(Calendar calendarEvent, String newEventDescription) {
		
		log.info("INFO: The description for the first instance of the calendar event will now be edited / updated to: " + newEventDescription);
		
		// Retrieve the first instance event from the calendar event
		Entry firstInstanceEntry = getCalendarEventFirstSingleInstance(calendarEvent);
		
		if(firstInstanceEntry == null) {
			return false;
		}
		
		// Create the entry to update the first instance event - setting the updated content required
		Entry updateEntry = Abdera.getNewFactory().newEntry();
		updateEntry.setId(firstInstanceEntry.getId().toString());
		updateEntry.setContentAsHtml(newEventDescription);
		updateEntry.addCategory("http://www.ibm.com/xmlns/prod/sn/type", "event-instance", null);
		log.info("INFO: The entry required to update the calendar event instance has been created: " + updateEntry.toString());
		
		// Retrieve the required URL to PUT the update request to
		String updateRequestURL = firstInstanceEntry.getEditLink().getHref().toString();
		log.info("INFO: The URL to which the update request will be PUT has been retrieved: " + updateRequestURL);
		
		Entry updatedFirstInstance = (Entry) service.putEntry(updateRequestURL, updateEntry);
		
		if(updatedFirstInstance.toString().indexOf("resp:error=\"true\"") == -1) {
			log.info("INFO: The description for the first instance of the calendar event was successfully updated");
			return true;
		} else {
			log.info("ERROR: The description for the first instance of the calendar event could not be updated");
			log.info(updatedFirstInstance.toString());
		}	return false;		
	}
	
	/**
	 * Retrieves the first single instance event for a calendar event
	 * Private access for now since there is no current requirement to use this method externally
	 * 
	 * @param calendarEvent - The Calendar instance of the calendar event whose first single instance event is to be retrieved
	 * @return - The Entry instance of the first single instance if the retrieval operation is successful, null otherwise
	 */
	private Entry getCalendarEventFirstSingleInstance(Calendar calendarEvent) {
		
		log.info("INFO: Retrieving the first instance event from the calendar event with title: " + calendarEvent.getTitle());
		
		// Retrieve the feed for this calendar event and from that retrieve the list of instances
		Feed calendarEventFeed = (Feed) service.getCalendarFeedWithRedirect(calendarEvent.getLinks().get("instances").getHref().toString());
		List<Entry> listOfInstances = calendarEventFeed.getEntries();
		
		if(listOfInstances.size() == 0) {
			log.info("ERROR: There were no single event instances found in the feed for this calendar event");
			log.info(calendarEventFeed.toString());
			return null;
		}
		return listOfInstances.get(0);
	}
	
	/**
	 * Deletes a comment posted to a calendar event
	 * 
	 * @param commentToBeDeleted - The CommentToEvent instance of the comment to be deleted
	 * @return - True if the deletion operation is successful, false otherwise
	 */
	public boolean deleteCommentOnCalendarEvent(CommentToEvent commentToBeDeleted) {
		
		log.info("INFO: The comment will be deleted with ID: " + commentToBeDeleted.getId().toString());
		
		ClientResponse deleteResponse = service.deleteWithResponse(commentToBeDeleted.getEditLink());
		
		if(deleteResponse.getStatus() >= 200 && deleteResponse.getStatus() <= 204) {
			log.info("INFO: The comment was successfully deleted from the calendar event");
			return true;
		} else {
			log.info("ERROR: The comment could not be deleted from the calendar event");
			log.info("ERROR: Reply status = " + deleteResponse.getStatus());
			log.info("ERROR: Reply text = " + deleteResponse.getStatusText());
			return false;
		}
	}
	
	/**
	 * Deletes the first single instance event from a calendar event
	 * 
	 * @param calendarEvent - The Calendar instance of the calendar event whose first single instance event is to be deleted
	 * @return - True if the delete operation is successful, false otherwise
	 */
	public boolean deleteCalendarEventFirstSingleInstance(Calendar calendarEvent) {
		
		log.info("INFO: Deleting the first instance event from the calendar event with title: " + calendarEvent.getTitle());
		
		// Retrieve the first instance event from the calendar event
		Entry firstInstanceEntry = getCalendarEventFirstSingleInstance(calendarEvent);
		
		if(firstInstanceEntry == null) {
			return false;
		}
		
		boolean deleted = service.deleteFeedLink(firstInstanceEntry.getEditLink().getHref().toString());
		if(deleted) {
			log.info("INFO: The first instance event was successfully deleted from the calendar event with title: " + calendarEvent.getTitle());
		} else {
			log.info("ERROR: The first instance event could not be deleted from the calendar event with title: " + calendarEvent.getTitle());
		}
		return deleted;
	}
	
	/**
	 * Deletes a calendar event from a community - also acts as deleting the entire series of a repeating calendar event from a community
	 * 
	 * @param calendarEvent - The Calendar instance of the calendar event to be deleted from the community
	 * @return - True if the delete operation is successful, false otherwise
	 */
	public boolean deleteCalendarEvent(Calendar calendarEvent) {
		
		log.info("INFO: Deleting the calendar event with title: " + calendarEvent.getTitle());
		
		boolean deleted = service.deleteFeedLink(calendarEvent.getEditLink());
		if(deleted) {
			log.info("INFO: The calendar event was successfully deleted");
		} else {
			log.info("ERROR: The calendar event could not be deleted");
		}
		return deleted;
	}
}