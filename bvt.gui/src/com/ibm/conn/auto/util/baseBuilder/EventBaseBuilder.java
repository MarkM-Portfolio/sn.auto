package com.ibm.conn.auto.util.baseBuilder;

import java.text.ParseException;

import com.ibm.conn.auto.appobjects.base.BaseEvent;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.Helper;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2016		                                     */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

/**
 * Supporting static methods for building BaseEvent objects
 * The objective of this class is to reduce the number of lines
 * of code in test cases by moving the building of standard
 * versions of these objects
 * 
 * @author Anthony Cox
 */

public class EventBaseBuilder {

	/**
	 * Creates a BaseEvent instance of a calendar event
	 * 
	 * @param calendarEventName - The name to be assigned to the calendar event (ie. testName)
	 * @param repeatingEvent - True if the calendar event is to be a repeating event
	 * 						   False if the calendar event is not a repeating event
	 * @return - The BaseEvent instance of the calendar event
	 */
	public static BaseEvent buildBaseCalendarEvent(String calendarEventName, boolean repeatingEvent) {
		BaseEvent baseEvent;
		try {
			baseEvent = new BaseEvent.Builder(calendarEventName + Helper.genStrongRand())
									 .tags(Data.getData().commonTag + Helper.genStrongRand())
									 .description(Data.getData().commonDescription + Helper.genStrongRand())
									 .repeat(repeatingEvent)
									 .build();
		} catch(ParseException pe) {
			pe.printStackTrace();
			baseEvent = null;
		}
		return baseEvent;
	}
}