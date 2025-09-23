package com.ibm.conn.auto.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.core.Executor;
import com.ibm.atmn.waffle.core.RCLocationExecutor;

public class FormInputHandler {

	private ArrayList<String> assertList;
	//dojo date picker
	public static final String DatePicker_NextMonth = "css=th[dojoattachpoint='incrementMonth']";
	public static final String DatePicker_PreviousMonth = "css=th[dojoattachpoint='decrementMonth']";
	public static final String DatePicker_CurrentMonth_Dates = "css=td[class*=dijitCalendarCurrentMonth]";
	public static final String DatePicker_PreviousYear = "css=span[dojoAttachPoint='previousYearLabelNode']";
	public static final String DatePicker_NextYear = "css=span[data-dojo-Attach-Point='nextYearLabelNode']";
	public static final String DatePicker_SelectedYear = "css=span[data-dojo-attach-point='currentYearLabelNode']";
	public static final String DatePicker_MonthDropDown = "//div[contains(@id,'date_popup_mddb_mdd')] | //div[contains(@id,'until_popup_mddb_mdd')]";
	public static final String DatePicker_MonthLabelNode = "//span[contains(@id,'date_popup_mddb')] | //span[contains(@id,'until_popup_mddb')]";
		
	private RCLocationExecutor exec;

	@Deprecated
	public FormInputHandler(Executor exec) {

		this.exec = (RCLocationExecutor) exec;
		assertList = new ArrayList<String>();
	}

	public void type(String selector, String text) {

		type(selector, text, "sentence");
	}

	public void type(String selector, String text, String textType) {

		type(selector, text, textType, "type");
	}

	public void type(String selector, String text, String textType, String typingStrategy) {

		type(selector, text, textType, typingStrategy, null);
	}

	//textType == null means do not add to assert list. 
	//typingStrategy == null mean use default strategy (unless selector is null, in which case nativeType will be used).
	//fireStrategy == null means do not use a fireStrategy (e.g. use for nativeType)
	//if a selector is not applicable (e.g. nativeType), use selector = null. If selector is null, nativeType will be used. This applies whether typingStrategy is provided or not.
	public void type(String selector, String text, String textType, String typingStrategy, String fireStrategy) {

		if (selector == null) typingStrategy = "typeNative";
		if (text == null) text = "null";
		if (textType == null) ;
		if (typingStrategy == null) typingStrategy = "";
		if (fireStrategy == null) fireStrategy = "";

		if (typingStrategy.equalsIgnoreCase("typeKeys")) {
			cautiousType(selector, text, typingStrategy);
		}
		else if (typingStrategy.equalsIgnoreCase("typeNative")) {
			typeNative(text);
		}
		else {
			cautiousType(selector, text, "type");
		}

		if (fireStrategy.equalsIgnoreCase("event")) {
			fireByEvent(selector);
		}
		else if (fireStrategy.equalsIgnoreCase("typeKeysBackspace")) {
			fireByTypeKeysBackspace(selector);
		}
		else {
			//do nothing
		}

		//test if string should be stored
		if (textType != null) {
			//parse and add string to list for assert check
			if (textType.equalsIgnoreCase("tags")) {
				addTagsToAssertList(text);
				//} else if (textType.equalsIgnoreCase("date")) {
				//addDateToAssertList(text);
			}
			else {
				addBlobToAssertList(text);
			}
		}
	}

	//TODO: Move to PageActionMethods and change cautious strategy to repetitions as used in cautiousFocus?
	private void cautiousType(String selector, String text, String typingStrategy) {

		exec.getSingleElement(selector).type(text);
	}

	private void typeNative(String text) {

		exec.typeNative(text);
	}

	public void typeAndWait(String selector, String text) {

		typeAndWait(selector, text, "sentence");
	}

	public void typeAndWait(String selector, String text, String textType) {

		typeAndWait(selector, text, textType, "type", "typeKeysBackspace");
	}

	public void typeAndWait(String selector, String text, String textType, String typingStrategy) {

		typeAndWait(selector, text, textType, typingStrategy, "typekeysBackspace");
	}

	public void typeAndWait(String selector, String text, String textType, String typingStrategy, String fireStrategy) {

		//type the text
		type(selector, text, textType, typingStrategy, fireStrategy);

	}

	public void addBlobToAssertList(String text) {

		assertList.add(text);
	}

	public void addTagsToAssertList(String text) {

		String[] tags = text.split(" ");
		for (String tag : tags) {
			addBlobToAssertList(tag.toLowerCase());
		}
	}

	public void addDateToAssertList(String text) {

		//Parse date string
		SimpleDateFormat dmy = new SimpleDateFormat("dd MMMM yyyy");
		SimpleDateFormat mdy = new SimpleDateFormat("MMMM dd, yyyy");
		boolean mdyIsFormat = true;
		Date chosenDate;
		dmy.setLenient(false);
		mdy.setLenient(false);
		try {
			chosenDate = mdy.parse(text);
		} catch (ParseException e) {
			mdyIsFormat = false;
			try {
				chosenDate = dmy.parse(text);
			} catch (ParseException e1) {
				System.out.println("ERROR: This date could not be parsed: " + text + ". It will not be verified. \n" + e1);
				return;
			}
		}
		Calendar chosenDateCal = Calendar.getInstance();
		chosenDateCal.setTime(chosenDate);
		System.out.println("******* Chosen Date Time: " + chosenDateCal.getTime());
		
		//get current date
		Calendar currentDateCal = getBrowserLocalCurrentDate();
		System.out.println("******* Current Date Time: " + currentDateCal.getTime());
		
		//Work out difference in days
		long days = getDateDelta(currentDateCal, chosenDateCal, Calendar.DAY_OF_MONTH);
		System.out.println("******* Days Difference: " + days);
		
		//Format date
		String expected;
		SimpleDateFormat yearOnly = new SimpleDateFormat("yyyy");
		//TODO: This will not be the same for every app, take format as parameter?
		if (days == 0)
			expected = "Today";
		else if (days == 1)
			expected = "Tomorrow";
		else if (days == -1)
			expected = "Yesterday";
		else if (days == 2 || days == -2)
			expected = new SimpleDateFormat("EEEE").format(chosenDateCal.getTime()); //e.g. Due Tuesday
		else if (yearOnly.format(currentDateCal.getTime()).equalsIgnoreCase(yearOnly.format(chosenDateCal.getTime()))) {
			SimpleDateFormat withoutYear;
			if (mdyIsFormat)
				withoutYear = new SimpleDateFormat("MMM d");
			else withoutYear = new SimpleDateFormat("d MMM");//e.g. due Jul 12 or 12 Jul
			expected = withoutYear.format(chosenDateCal.getTime());
		}
		else {
			SimpleDateFormat withYear;
			if (mdyIsFormat)
				withYear = new SimpleDateFormat("MMM d, yyyy");
			else withYear = new SimpleDateFormat("d MMM yyyy");//e.g. due Jul 12, 2013 or 2 Jul 2013
			expected = withYear.format(chosenDateCal.getTime());
		}

		//Add appropriate string to assert list
		addBlobToAssertList(expected);
	}
	
	public Calendar getBrowserLocalCurrentDate(){
		
		return exec.getBrowserDatetime();
	}
	
	/**
	 * Resets dates to midnight. Does not support all fields.
	 * @param date1
	 * @param date2
	 * @param field Calendar.MONTH or Calendar.DAY_OF_YEAR
	 * @return The signed number of days that added to date 1 would give the same date as date2.
	 */
	private long getDateDelta(Calendar cal1, Calendar cal2, int field) {

		long delta = 0;
		cal1 = DateUtils.truncate(cal1, field);
		cal2 = DateUtils.truncate(cal2, field);

		//Equal means 0 days difference
		if (cal1.equals(cal2)){
			return delta;
		}
		else if (cal1.before(cal2)) {
			while (cal1.before(cal2)) {
				cal1.add(field, 1);
				delta++;
			}
		}
		else if (cal1.after(cal2)) {
			while (cal1.after(cal2)) {
				cal1.add(field, -1);
				delta--;
			}
		}
		return delta;
	}

	public String popLastFromAssertList() {

		return assertList.remove(assertList.size() - 1);
	}

	public void dumpList() {

		assertList = new ArrayList<String>();
	}

	public ArrayList<String> getListCopy() {

		ArrayList<String> temp = new ArrayList<String>();
		for (String text : assertList) {
			temp.add(text);
		}
		return temp;
	}

	public ArrayList<String> getAssertList() {

		return assertList;
	}

	public void pickARandomDojoDate(String locator) {

		pickARandomDojoDate(locator, true);
	}

	//Selects a date from a subset of dates in the next or previous 18 months
	public void pickARandomDojoDate(String locator, boolean addToAssertList) {

		//pick forward or backward 18 months at random
		int offsetMonths = ((int) (Math.floor(Math.random() * 2))) == 1 ? 18 : -18;
		
		//Generate random future month and date of month
		Calendar target = getBrowserLocalCurrentDate();
		long cts = target.getTimeInMillis();
		target.add(Calendar.MONTH, offsetMonths);
		long ots = target.getTimeInMillis();
		long randomTime = ots > cts ? ((long)(Math.random()*((ots - cts) + 1)+cts)) : ((long)(Math.random()*((cts - ots) + 1)+ots));	
		target.setTime(new Date(randomTime));
		
		System.out.println("******* Random Date: " + new SimpleDateFormat("d MMM yyyy").format(target.getTime()));
		pickADojoDate(locator, true, target);
	}

	public void pickADojoDate(String locator, boolean addToAssertList, Calendar targetDate) {

		int dayOfMonth = targetDate.get(Calendar.DAY_OF_MONTH);
		System.out.println("******* dayOfMonth: " + dayOfMonth);
		Calendar currentDate = getBrowserLocalCurrentDate();
		System.out.println("******* Current Date: " + new SimpleDateFormat("d MMM yyyy").format(currentDate.getTime()));
		
		long moveMonths = getDateDelta(currentDate, targetDate, Calendar.MONTH);
		System.out.println("******* Move Months: " + moveMonths);
		//activate dropdown datepicker
		exec.getSingleElement(locator).click();

		//Select month
		while (moveMonths != 0) {
			if (moveMonths > 0) {
				exec.getSingleElement(DatePicker_NextMonth).click();
				moveMonths--;
			}
			else {
				exec.getSingleElement(DatePicker_PreviousMonth).click();
				moveMonths++;
			}
		}

		//Select date
		List<Element> temp2;
		temp2 = exec.getElements(DatePicker_CurrentMonth_Dates);
		temp2.get(dayOfMonth - 1).click();

		if (addToAssertList) {
			//get string, parse it, and add to list for assert check
			addDateToAssertList(exec.getSingleElement(locator).getAttribute("value"));
		}
	}
	/**
	 * This will pick the date that is passed in as a parameter.
	 * @param locator the id of the date picker.
	 * @param date
	 */
	public void pickTheDojoDate(String locator, Date date) {
		
		//activate drop down date picker
		exec.getSingleElement(locator).click();
		
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int d = c.get(Calendar.DAY_OF_MONTH);
		int m = c.get(Calendar.MONTH);
		int y = c.get(Calendar.YEAR);
		
		//Picking the date involves scrolling the years and months. When the Calendar opens, it is the current date.
		//pick the year first.
		boolean sY = false;
		while (!sY) {
			String sT = exec.getVisibleElements(FormInputHandler.DatePicker_SelectedYear).get(0).getText();
			int st = Integer.parseInt(sT);
			if (st < y) {
				exec.getVisibleElements(FormInputHandler.DatePicker_NextYear).get(0).click();
			} else if (st > y) {
				exec.getVisibleElements(FormInputHandler.DatePicker_PreviousYear).get(0).click();
			} else {
				sY = true;
			}
		}
		
		//take the modulus of months and move back or forward
		exec.getVisibleElements(FormInputHandler.DatePicker_MonthLabelNode).get(0).click();
		@SuppressWarnings("unused")
		Element elem = exec.getVisibleElements(FormInputHandler.DatePicker_MonthDropDown).get(0);
		List<Element> monthElements = exec.getElements("css=div[month='"+m+"']");
		for (Element monthDropDown : monthElements) {
			if (monthDropDown.isVisible()) {
				monthDropDown.hover();
				monthDropDown.click();
			}
		}
		
		//pick the day of month
		String dS = String.valueOf(d);
		List<Element> temp2 = exec.getElements("css=td[class*=dijitCalendarCurrentMonth]");
		
		for (Element dayOfMonth : temp2) {
			String t = dayOfMonth.getText();
			if (dS.equals(t)) {
				dayOfMonth.click();
				break;
			}
		}
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	private void fireByEvent(String selector, String... events) {

		/*
		 * String[] eventList; //confirm list of events to fire if (events.length < 1) { eventList = new String[] {
		 * "keydown", "keyup", "keypress", "change", "blur" }; } else { eventList = events; } for (String event :
		 * eventList) { exec.sel.fireEvent(selector, event); SetUpMethods.sleep(100); }
		 */
	}

	private void fireByTypeKeysBackspace(String selector) {

		/*
		 * exec.sel.typeKeys(selector, " "); exec.sel.typeKeys(selector, "\b");
		 */
	}
}
