package com.ibm.conn.auto.appobjects.base;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.testng.Assert;

import com.ibm.conn.auto.appobjects.BaseStateObject;
import com.ibm.conn.auto.lcapi.APICalendarHandler;
import com.ibm.conn.auto.webui.CalendarUI;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.communities.nodes.Event;

public class BaseEvent implements BaseStateObject {

	public enum eventFields {
		NAME,
		TAGS,
		DESCRIPTION;	
	}
	
	public enum RepeatType {
		DAILY("Daily"),
		WEEKLY("Weekly");
		
	    public String repeatType;
	    private RepeatType(String type){
            this.repeatType = type;
	    }
	    	    
	    @Override
	    public String toString(){
	            return repeatType;
	    }
	}

	public enum Days {
	    Sun ("css=tr[id='calendar_event_editor_rec-recDaily'] input[weekday='SU'][role='checkbox']",
	         "css=tr[id='calendar_event_editor_rec-recWeekly'] input[weekday='SU'][role='checkbox']"),
	    Mon ("css=tr[id='calendar_event_editor_rec-recDaily'] input[weekday='MO'][role='checkbox']", 
	    	 "css=tr[id='calendar_event_editor_rec-recWeekly'] input[weekday='MO'][role='checkbox']"),
	    Tue ("css=tr[id='calendar_event_editor_rec-recDaily'] input[weekday='TU'][role='checkbox']", 
	    	 "css=tr[id='calendar_event_editor_rec-recWeekly'] input[weekday='TU'][role='checkbox']"),
	    Wed ("css=tr[id='calendar_event_editor_rec-recDaily'] input[weekday='WE'][role='checkbox']", 
	    	 "css=tr[id='calendar_event_editor_rec-recWeekly'] input[weekday='WE'][role='checkbox']"),
	    Thu ("css=tr[id='calendar_event_editor_rec-recDaily'] input[weekday='TH'][role='checkbox']", 
	    	 "css=tr[id='calendar_event_editor_rec-recWeekly'] input[weekday='TH'][role='checkbox']"),
	    Fri ("css=tr[id='calendar_event_editor_rec-recDaily'] input[weekday='FR'][role='checkbox']", 
	    	 "css=tr[id='calendar_event_editor_rec-recWeekly'] input[weekday='FR'][role='checkbox']"),
	    Sat ("css=tr[id='calendar_event_editor_rec-recDaily'] input[weekday='SA'][role='checkbox']", 
	    	 "css=tr[id='calendar_event_editor_rec-recWeekly'] input[weekday='SA'][role='checkbox']");

	    private String linkDay;
	    private String linkWeek;
	    
	    Days (String linkDay, String linkWeek) {
	        this.setLinkDay(linkDay);
	        this.setLinkWeek(linkWeek);
	    }

		public void setLinkDay(String linkDay) {
			this.linkDay = linkDay;
		}

		public String getLinkDay() {
			return linkDay;
		}

		public void setLinkWeek(String linkWeek) {
			this.linkWeek = linkWeek;
		}

		public String getLinkWeek() {
			return linkWeek;
		}

	
	}
	
	public enum RepeatEvery {
		WEEKLY("Weekly"),
		TWO_WEEKS("2 Weeks"),
		THREE_WEEKS("3 Weeks"),
		FOUR_WEEKS("4 Weeks"),
		FIVE_WEEKS("5 Weeks");
		
	    private String weeks;
	    
	    RepeatEvery (String howMany) {
	        this.setWeeks(howMany);
	    }
	    	
		public void setWeeks(String howMany) {
			this.weeks = howMany;
		}

		public String getWeeks() {
			return weeks;
		}
		
	}
	
	private String name;
	private String tags;
	private String description;
	private String location;
	private Calendar startDate;
	private Calendar endDate;
	private String startTime;
	private String endTime;
	private boolean repeat;
	private RepeatType repeatType;
	private List<Days> repeatDays = new ArrayList<Days>();
	private Calendar repeatUntil;	
	private RepeatEvery repeatEvery;
	private boolean allDayEvent;
	private boolean notifyCommunity;
	private boolean useCalPick;
	



	public static class Builder {
		
		private String name; 
		private String tags = "";
		private boolean allDayEvent = false;
		private boolean repeat = false;
		private RepeatType repeatType = RepeatType.DAILY;
		private List<Days> repeatDays = new ArrayList<Days>();
		private Calendar repeatUntil = null;
		private RepeatEvery repeatEvery = RepeatEvery.WEEKLY;
		private String location = "";
		private String description = "";
		private Calendar startDate = null;
		private Calendar endDate = null;
		private String startTime = "";
		private String endTime = "";		
		private boolean notifyCommunity = false;	
		private boolean useCalPick = true;
		
		public Builder(String name){
			this.name = name;
		}
		
		public Builder tags(String tags){
			this.tags = tags;
			return this;
		}

		/**  use format MM/dd/YYYY */
		public Builder startDate(Calendar startDate){	
			this.startDate = startDate;
			return this;
		}

		/**  use format HH:mm AM or HH:mm PM */
		public Builder startTime(String startTime){
			this.startTime = startTime;
			return this;
		}
		
		/**  use format MM/dd/YYYY */
		public Builder endDate(Calendar endDate){	
			this.endDate = endDate;
			return this;
		}
		
		/**  use format HH:mm AM or HH:mm PM */
		public Builder endTime(String endTime){
			this.endTime = endTime;
			return this;
		}
		
		public Builder allDayEvent(boolean allDayEvent){
			this.allDayEvent = allDayEvent;
			return this;
		}
		
		public Builder repeat(boolean repeat){
			this.repeat = repeat;
			return this;
		}
		
		public Builder repeatType(RepeatType repeatType){
			this.repeatType = repeatType;
			return this;
		}
		
		/** use format S,M,T,W, */
		public Builder repeatDays(List<Days> repeatDays){
			this.repeatDays = repeatDays;
			return this;
		}

		/**  use format MM/dd/YYYY */
		public Builder repeatUntil(Calendar repeatUntil){	
			this.repeatUntil = repeatUntil;
			return this;
		}

		public Builder repeatEvery(RepeatEvery repeatEvery){
			this.repeatEvery = repeatEvery;
			return this;
		}
		
		public Builder location(String location) {
			this.location = location;
			return this;
		}
		
		public Builder description(String description) {
			this.description = description;
			return this;
		}
		
		public Builder notifyCommunity(boolean notifyCommunity){
			this.notifyCommunity = notifyCommunity;
			return this;
		}
		
		public Builder useCalPick(boolean useCalPick) {
			this.useCalPick = useCalPick;
			return this;
		}
		
		public BaseEvent build() throws ParseException {
			return new BaseEvent(this);
		}
	}
	
	private BaseEvent(Builder b) throws ParseException {


		this.setName(b.name);
		this.setTags(b.tags);
		this.setStartDate(b.startDate);
		this.setStartTime(b.startTime);
		this.setEndDate(b.endDate);
		this.setEndTime(b.endTime);
		this.setAllDayEvent(b.allDayEvent);
		this.setRepeat(b.repeat);
		this.setRepeatType(b.repeatType);
		this.setRepeatDays(b.repeatDays);
		this.setRepeatUntil(b.repeatUntil);
		this.setRepeatEvery(b.repeatEvery);
		this.setLocation(b.location);
		this.setDescription(b.description);
		this.setNotifyCommunity(b.notifyCommunity);
		this.setUseCalPick(b.useCalPick);
		
	}
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}	

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public Calendar getStartDate() { 		
		return startDate;
	}	
	
	public String getStartDateText() {
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");	
		if(this.startDate == null)
			return "";
		else
			return sdf.format(getStartDate().getTime());
	}
	
	public void setStartDate(Calendar startDate){	
		this.startDate = startDate;
	}
	
	public String getStartTime() { 		
		return startTime;
	}	
	
	public void setStartTime(String startTime){	
		this.startTime = startTime;
	}	
	
	public Calendar getEndDate() {
		return endDate;
	}

	public String getEndDateText() {
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		if(this.endDate == null)
			return "";
		else
			return sdf.format(getEndDate().getTime());
	}
	
	public void setEndDate(Calendar endDate){
		this.endDate = endDate;
	}
	
	public String getEndTime() { 		
		return endTime;
	}	
	
	public void setEndTime(String endTime){	
		this.endTime = endTime;
	}
	
	public boolean getAllDayEvent() {
		return allDayEvent;
	}

	public void setAllDayEvent(Boolean allDayEvent) {
		this.allDayEvent = allDayEvent;
	}
	
	public boolean getRepeat() {
		return repeat;
	}

	public void setRepeat(Boolean repeat) {
		this.repeat = repeat;
	}
	
	public RepeatType getRepeatType(){
		return repeatType;
	}
	
	public void setRepeatType(RepeatType repeatType){
		this.repeatType = repeatType;
	}
	
	public List<Days> getRepeatDays() {
		return repeatDays;
	}

	public void setRepeatDays(List<Days> repeatDays) {
		this.repeatDays = repeatDays;
	}
	
	public Calendar getRepeatUntil() {
		return repeatUntil;
	}

	public String getRepeatUntilText() {
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");	
		if(this.repeatUntil == null)
			return "";
		else
			return sdf.format(getRepeatUntil().getTime());
	}
	
	public void setRepeatUntil(Calendar repeatUntil) {
		this.repeatUntil = repeatUntil;
	}
	
	public RepeatEvery getRepeatEvery() {
		return repeatEvery;
	}

	public void setRepeatEvery(RepeatEvery repeatEvery) {
		this.repeatEvery = repeatEvery;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}	
	
	public boolean getNotifyCommunity() {
		return notifyCommunity;
	}

	public void setNotifyCommunity(Boolean notifyCommunity) {
		this.notifyCommunity = notifyCommunity;
	}
	
	public boolean getUseCalPick() {
		return useCalPick;
	}

	public void setUseCalPick(Boolean useCalPick) {
		this.useCalPick = useCalPick;
	}
	
	public void create(CalendarUI ui) throws Exception {
		ui.create(this);
	}

	public Event createAPI(APICalendarHandler apiOwner, Community community) {
		Event event = apiOwner.createEvent(this, community);		
		Assert.assertTrue(event != null, 
						  "ERROR: Failed to create event using API.");		
		return event;
	}
	
}
