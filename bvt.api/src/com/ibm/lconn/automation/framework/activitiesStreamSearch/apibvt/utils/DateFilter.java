package com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class DateFilter {

	private static DateFilter _instance;

	private String dateFilter;

	private DateFilter() throws UnsupportedEncodingException {
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sss'Z'");
		formatter.setTimeZone(TimeZone.getTimeZone("GMT"));

		Calendar calStartTime = Calendar.getInstance();
		Calendar calEndTime = Calendar.getInstance();

		calEndTime.getTime();
		calStartTime.getTime();

		// System.out.println("Date: " + calStartTime.getTime());
		// System.out.println("Date: " + calEndTime.getTime());
		calEndTime.add(Calendar.HOUR, 1); // Adding 5 hours
		String endDate = formatter.format(calEndTime.getTime());
		// System.out.println("end time: " + endDate);
		calStartTime.add(Calendar.HOUR, -1); // Reducing 10 hours
		String startDate = formatter.format(calStartTime.getTime());
		// System.out.println("start time: " + startDate);

		String filterParam = "{'from':'startTime','to':'endTime','fromInclusive':true,'toInclusive':true}";
		filterParam = URLEncoder.encode(filterParam, "UTF-8");

		filterParam = filterParam.replaceAll("startTime", startDate);
		filterParam = filterParam.replaceAll("endTime", endDate);

		dateFilter = "&dateFilter=" + filterParam;
		System.out.println("dateFilter: " + dateFilter);

	}

	public static DateFilter instance() {
		if (_instance != null) {
			return _instance;
		}
		try {
			_instance = new DateFilter();
		} catch (UnsupportedEncodingException e) {
			System.out.println("failed to create date filter");
			e.printStackTrace();
		}
		return _instance;
	}

	public String getDateFilterParam() {
		return dateFilter;
	}

}
