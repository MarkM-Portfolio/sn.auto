package com.ibm.lconn.automation.framework.services.communities.nodes;

import java.util.List;

import org.apache.abdera.model.Category;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;

import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.nodes.LCEntry;

public class Event extends LCEntry {
	
	private Category isEvent;

	private Element snx_communityUuid;			/** (Required for Community Event) Identifies the Event as a community Event and identifies the community to which it belongs by the communitys unique ID.. */
	private Element snx_recurrence;
	//private Element snx_period;
	//private Element snx_startDate;
	//private Element snx_endDate;
	//private Element snx_location;
	private Element snx_allday;
	//private Element snx_until;

	public Event(String title, String custom, String frequency, String interval, String until, String start, String end, int allday, String monthlyType, String byDay) {
		super();
		
		setIsEvent(true);
		setTitle(title);
		if (frequency != null && frequency.equalsIgnoreCase(StringConstants.STRING_MONTHLY)) {
			if (monthlyType != null && monthlyType.equals(StringConstants.STRING_MONTHLY_BY_DAY)) {
				setMonthlyRecurrenceByDay(custom, interval, start, end, until);
			}
			if (monthlyType != null && monthlyType.equals(StringConstants.STRING_MONTHLY_BY_DAY_OF_WEEK)) {
				setMonthlyRecurrenceByDayOfWeek(custom, interval, start, end, until, byDay);
			}
		} else {
			setRecurrence(custom, frequency, interval, start, end, until);
		}
		setSnx_allday(allday);
	}
	
	public Event(String title, String custom, String frequency, String interval, String until, String start, String end, int allday, String monthlyType) {
		super();
		
		setIsEvent(true);
		setTitle(title);
		if (frequency != null && frequency.equalsIgnoreCase(StringConstants.STRING_MONTHLY)) {
			if (monthlyType != null && monthlyType.equals(StringConstants.STRING_MONTHLY_BY_DAY)) {
				setMonthlyRecurrenceByDay(custom, interval, start, end, until);
			}
			if (monthlyType != null && monthlyType.equals(StringConstants.STRING_MONTHLY_BY_DAY_OF_WEEK)) {
				setMonthlyRecurrenceByDayOfWeek(custom, interval, start, end, until);
			}
		} else {
			setRecurrence(custom, frequency, interval, start, end, until);
		}
		setSnx_allday(allday);
	}
	
	public Event(String title, String custom, String frequency, String interval, String until, String start, String end, int allday) {
		super();
		
		setIsEvent(true);
		setTitle(title);
		setRecurrence(custom, frequency, interval, start, end, until);
		setSnx_allday(allday);
	}
	
	public Event(String title, String custom, String frequency, String interval, String until, String start, String end, int allday, String timezone, String dayLightSavingsTime, String byDay) {
		super();
		
		setIsEvent(true);
		setTitle(title);
		setRecurrence(custom, frequency, interval, start, end, until, timezone, dayLightSavingsTime, byDay);
		setSnx_allday(allday);
	}	

	public Event(Entry entry) {
		super(entry);
		
		List<Category> types = entry.getCategories(StringConstants.SCHEME_TYPE);
		
		if(types.size() > 0) {
			String term = types.get(0).getTerm();
			if(term.equals(StringConstants.STRING_EVENT_LOWERCASE)) {
				setIsEvent(true);				
			}
		}
	}

	@Override
	public Entry toEntry() {
		Element[] extensions = {getRecurrenceElement(), getSnx_allday() };

		Category[] categories = {getIsEventCategory()};

		return createBasicEntry(extensions, categories);
	}


	/**
	 * @return the Atom category object that contains the isEvent information.
	 */
	public Category getIsEventCategory() {
		return isEvent;
	}

	/**
	 * @param isEvent set the Atom category object that contains the isEvent information.
	 */
	public void setIsEvent(boolean isEvent) {
		Category isEventCategory = null;
		
		if(isEvent) {
			isEventCategory = getFactory().newCategory();
			isEventCategory.setScheme(StringConstants.SCHEME_TYPE);
			isEventCategory.setTerm(StringConstants.STRING_EVENT_LOWERCASE);
			isEventCategory.setLabel(StringConstants.STRING_EVENT_CAPITALIZED);
		}
		
		this.isEvent = isEventCategory;
	}
	
	/**
	 * @param isEvent a isEvent Atom Category object.
	 */
	public void setIsEvent(Category isEvent) {
		this.isEvent = isEvent;
	}
	
	
	public String getUuid() {
		return getLinks().get(StringConstants.REL_SELF + ":" + StringConstants.MIME_NULL).getHref().toString().split("communityUuid=")[1];
	}

	
	
	public Element getCommunityUuidElement() {
		return snx_communityUuid;
	}

	public void setCommunityUuid(String communityUuid) {
		Element communityUuidElement = null;
		
		if(communityUuid != null) {
			communityUuidElement = getFactory().newElement(StringConstants.SNX_COMMUNITY_UUID);
			communityUuidElement.setText(communityUuid);
		}
		this.snx_communityUuid = communityUuidElement;
	}
	
	/*private void setCommunityUuid(Element communityUuid) {
		snx_communityUuid = communityUuid;
	}*/
	
	public Element getRecurrenceElement() {
		return snx_recurrence;
	}

	public void setMonthlyRecurrenceByDay(String custom, String interval, String start, String end, String until) {
		Element recurrenceElement = null;
		Element periodElement = null;
		Element startDateElement = null;
		Element endDateElement = null;
		Element untilElement = null;
		Element byDayElement = null;
		
		recurrenceElement = getFactory().newElement(StringConstants.SNX_RECURRENCE);
		recurrenceElement.setAttributeValue(StringConstants.ATTR_CUSTOM, custom);
		
		if (custom.equalsIgnoreCase(StringConstants.STRING_YES_LOWERCASE)) {
			
			periodElement = getFactory().newElement(StringConstants.SNX_PERIOD, recurrenceElement);
			startDateElement = getFactory().newElement(StringConstants.SNX_STARTDATE, periodElement);
			endDateElement = getFactory().newElement(StringConstants.SNX_ENDDATE, periodElement);
			
		}
		else if (custom.equalsIgnoreCase(StringConstants.STRING_NO_LOWERCASE)){

			untilElement = getFactory().newElement(StringConstants.SNX_UNTIL, recurrenceElement);
			startDateElement = getFactory().newElement(StringConstants.SNX_STARTDATE, recurrenceElement);
			endDateElement = getFactory().newElement(StringConstants.SNX_ENDDATE, recurrenceElement);
			byDayElement = getFactory().newElement(StringConstants.SNX_BYDATE, recurrenceElement);
			recurrenceElement.setAttributeValue(StringConstants.ATTR_FREQUENCY, StringConstants.STRING_MONTHLY);
			if (interval != null){
				recurrenceElement.setAttributeValue(StringConstants.ATTR_INTERVAL, interval);
			}
			untilElement.setText(until);
			byDayElement.setText("31");
		}
		startDateElement.setText(start);
		endDateElement.setText(end);
		
		this.snx_recurrence = recurrenceElement;
	}

	public void setMonthlyRecurrenceByDayOfWeek(String custom, String interval, String start, String end, String until, String byDay) {
		Element recurrenceElement = null;
		Element periodElement = null;
		Element startDateElement = null;
		Element endDateElement = null;
		Element untilElement = null;
		Element byDayElement = null;
		
		recurrenceElement = getFactory().newElement(StringConstants.SNX_RECURRENCE);
		recurrenceElement.setAttributeValue(StringConstants.ATTR_CUSTOM, custom);
		
		if (custom.equalsIgnoreCase(StringConstants.STRING_YES_LOWERCASE)) {
			
			periodElement = getFactory().newElement(StringConstants.SNX_PERIOD, recurrenceElement);
			startDateElement = getFactory().newElement(StringConstants.SNX_STARTDATE, periodElement);
			endDateElement = getFactory().newElement(StringConstants.SNX_ENDDATE, periodElement);
			
		}
		else if (custom.equalsIgnoreCase(StringConstants.STRING_NO_LOWERCASE)){

			untilElement = getFactory().newElement(StringConstants.SNX_UNTIL, recurrenceElement);
			startDateElement = getFactory().newElement(StringConstants.SNX_STARTDATE, recurrenceElement);
			endDateElement = getFactory().newElement(StringConstants.SNX_ENDDATE, recurrenceElement);
			byDayElement = getFactory().newElement(StringConstants.SNX_BYDAYOFWEEK, recurrenceElement);
			recurrenceElement.setAttributeValue(StringConstants.ATTR_FREQUENCY, StringConstants.STRING_MONTHLY);
			if (interval != null){
				recurrenceElement.setAttributeValue(StringConstants.ATTR_INTERVAL, interval);
			}
			untilElement.setText(until);
			//byDayElement.setText("-1,SA");
			byDayElement.setText(byDay);
		}
		startDateElement.setText(start);
		endDateElement.setText(end);
		
		this.snx_recurrence = recurrenceElement;
	}
	
	public void setMonthlyRecurrenceByDayOfWeek(String custom, String interval, String start, String end, String until) {
		setMonthlyRecurrenceByDayOfWeek(custom, interval, start, end, until, "-1,SA");
	}
	
	public void setRecurrence(String custom, String frequency, String interval, String start, String end, String until) {
		Element recurrenceElement = null;
		Element periodElement = null;
		Element startDateElement = null;
		Element endDateElement = null;
		Element untilElement = null;
		Element byDayElement = null;
		
		recurrenceElement = getFactory().newElement(StringConstants.SNX_RECURRENCE);
		recurrenceElement.setAttributeValue(StringConstants.ATTR_CUSTOM, custom);
		
		if(custom.equalsIgnoreCase(StringConstants.STRING_YES_LOWERCASE)) {
			
			periodElement = getFactory().newElement(StringConstants.SNX_PERIOD, recurrenceElement);
			startDateElement = getFactory().newElement(StringConstants.SNX_STARTDATE, periodElement);
			endDateElement = getFactory().newElement(StringConstants.SNX_ENDDATE, periodElement);
			
		}
		else if (custom.equalsIgnoreCase(StringConstants.STRING_NO_LOWERCASE)){

			untilElement = getFactory().newElement(StringConstants.SNX_UNTIL, recurrenceElement);
			startDateElement = getFactory().newElement(StringConstants.SNX_STARTDATE, recurrenceElement);
			endDateElement = getFactory().newElement(StringConstants.SNX_ENDDATE, recurrenceElement);
			byDayElement = getFactory().newElement(StringConstants.SNX_BYDAY, recurrenceElement);
			
			if (frequency != null){
				recurrenceElement.setAttributeValue(StringConstants.ATTR_FREQUENCY, frequency);
			}
			if (interval != null){
				recurrenceElement.setAttributeValue(StringConstants.ATTR_INTERVAL, interval);
			}
			untilElement.setText(until);
			byDayElement.setText("TU");
		}
		startDateElement.setText(start);
		endDateElement.setText(end);
		
		this.snx_recurrence = recurrenceElement;
	}
	
	public void setRecurrence(String custom, String frequency, String interval, String start, String end, String until, String timezone, String dayLightSavingsTime, String byDay) {
		Element recurrenceElement = null;
		Element periodElement = null;
		Element startDateElement = null;
		Element endDateElement = null;
		Element untilElement = null;
		Element byDayElement = null;
		Element timezoneElement = null;
		Element dayLightSavingsTimeElement = null;
		
		recurrenceElement = getFactory().newElement(StringConstants.SNX_RECURRENCE);
		recurrenceElement.setAttributeValue(StringConstants.ATTR_CUSTOM, custom);
		
		if(custom.equalsIgnoreCase(StringConstants.STRING_YES_LOWERCASE)) {
			
			periodElement = getFactory().newElement(StringConstants.SNX_PERIOD, recurrenceElement);
			startDateElement = getFactory().newElement(StringConstants.SNX_STARTDATE, periodElement);
			endDateElement = getFactory().newElement(StringConstants.SNX_ENDDATE, periodElement);
			
		}
		else if (custom.equalsIgnoreCase(StringConstants.STRING_NO_LOWERCASE)){

			untilElement = getFactory().newElement(StringConstants.SNX_UNTIL, recurrenceElement);
			startDateElement = getFactory().newElement(StringConstants.SNX_STARTDATE, recurrenceElement);
			endDateElement = getFactory().newElement(StringConstants.SNX_ENDDATE, recurrenceElement);
			byDayElement = getFactory().newElement(StringConstants.SNX_BYDAY, recurrenceElement);
			
			if (frequency != null){
				recurrenceElement.setAttributeValue(StringConstants.ATTR_FREQUENCY, frequency);
			}
			if (interval != null){
				recurrenceElement.setAttributeValue(StringConstants.ATTR_INTERVAL, interval);
			}
			untilElement.setText(until);
			if (byDay == "") {
				byDayElement.setText("TU");
			} else {
				byDayElement.setText(byDay);
			}						
		}
		
		startDateElement.setText(start);
		endDateElement.setText(end);
		
		//New extensions for Calendar
		if (timezone != ""){
			timezoneElement = getFactory().newElement(StringConstants.SNX_TIMEZONE, recurrenceElement);
			timezoneElement.setText(timezone);	
		}
		if (dayLightSavingsTime != "") {
			dayLightSavingsTimeElement = getFactory().newElement(StringConstants.SNX_DAYLIGHT, recurrenceElement);
			dayLightSavingsTimeElement.setText(dayLightSavingsTime);
		}


		
		this.snx_recurrence = recurrenceElement;
	}	
	
	/*private void setRecurrence(Element recurrence) {
		snx_recurrence = recurrence;
	}*/
	
	public Element getSnx_allday() {
		return snx_allday;
	}

	public void setSnx_allday(Element snx_allday) {
		this.snx_allday = snx_allday;
	}
	
	public void setSnx_allday(int allday) {
		Element alldayElement = null;
		
		alldayElement = getFactory().newElement(StringConstants.SNX_ALLDAY);
		alldayElement.setText(String.valueOf(allday));

		this.snx_allday = alldayElement;
	}
	
}
