package com.ibm.smartcloud.metrics.dashboard.core;

import com.ibm.json.java.*;
import java.util.Formatter;

/**
 * Represents the services of IBM Connections and provides utility methods to retrieve information about the service.
 * The first name in each group will be used as the name of the group.
 */
public enum Service {
	// Other services -- listed first so that new additions can go on the end.
	/*OTHER(0, "#B0C4DE"),
	AKAMAI(0),
	ANNOUNCEMENTS(0),
	APPLE_TOUCH_ICON(0),
	CLOUD_COMPUTING(0),
	FAVICON(0),
	RPTWEB(0),
	STATUS(0),
	SUPPORT(0),*/// Explicitly removed by Jim Battle (Aug 7, 2014)
	// Common services -- Could be used by multiple applications.
	/*COMMON(1, "#9370DB"),
	BHC(1),
	CONNECTIONS(1),
	DOWNLOADS(1),
	INSTALL(1),
	EXT(1),
	NAVBAR(1),
	THEMING(1),*/// Explicitly removed by Jim Battle (Aug 7, 2014)
	// The IBM Connections Activities service
	ACTIVITIES(2, "query_string", "activityUuid=", "&",  "#20B2AA"),
	// The IBM Connections Blogs service
	BLOGS(3, "#82D1F5"),
	// The IBM SmartCloud BSS services
	/*BSS(4, "#008ABF"),
	API(4),
	DOJO(4),
	LOGIN(4),
	MANAGE(4),
	PRIVACY(4),*/// Explicitly removed by Jim Battle (Aug 7, 2014)
	// The IBM Connections Chat service
	/*SAMETIME(5, "#00648D"),
	SPS(5),*/// Explicitly removed by Jim Battle (Aug 7, 2014)
	// The IBM Connections Communities service
	COMMUNITIES(6, "query_string", "communityUuid=", "&", "#007670"),
	// The IBM SmartCloud Contacts services
	CONTACTS(7, "#17AF4B"),
	COM_IBM_SC_SERVER(7),
	CONTACTS_DOJO(7),
	LOTUSLIVE_SHINDIG_SERVER(7),
	MYCONTACTS(7),
	// The IBM Docs Viewer service
	IBM_DOCS(8, "#8CC63F"),
	COM_IBM_CONCORD_WAR(8),
	VIEWER(8),
	// The IBM Connections Files service
	FILES(9, "url_path", new String[] {"document/", "file/", "file!"}, "/", new String[] {"collection/", "collection!", "folder/", "library/"}, "/", "#A5A215"),
	MICROSOFT_SERVER_ACTIVESYNC(9, "request_referer", "DeviceType=", "&"),
	// The IBM Connections Forums service
	FORUMS(10, "query_string", "communityUuid=", "&", "#594F13"),
	// The IBM SmartCloud Help service
	/*HELP(11, "#FFE14F"),*/// Explicitly removed by Jim Battle (Aug 7, 2014)
	// The IBM Connections Home Page service
	HOMEPAGE(12, "#FFCF01"),
	// The IBM Connections Chat Meetings service
	/*MEETINGS(13, "#F19027"),
	DOJOMEETINGS(13),
	JOIN(13, "query_string", "id=", "&"),
	STMEETINGS(13),*/// Explicitly removed by Jim Battle (Aug 7, 2014)
	// The IBM SmartCloud Mobile services
	/*MOBILE(14, "#B8461B"),
	EAI(14),
	MOBILEADMIN(14),
	TRAVELER(14),*/// Explicitly removed by Jim Battle (Aug 7, 2014)
	// The IBM Connections News service
	NEWS(15, "#F04E37"),
	// The IBM Lotus Notes service
	/*NOTES(16, "#A91024"),
	LIVEMAIL(16),
	SMATTER(16),*/// Explicitly removed by Jim Battle (Aug 7, 2014)
	// The IBM Connections Profiles service
	PROFILES(17, "#F389AF"),
	// The IBM Connections Search service.
	SEARCH(18, "#EE3D96"),
	// The IBM Surveys service
	SURVEYS(19, "#AB1A86"),
	// The IBM Connections Wikis service
	WIKIS(20, "url_path", "wiki/", "/", "#3B0256")
	;
	
	private int appId;
	private String color;
	private String dataSource;
	private String prefix1[];
	private String prefix2[];
	private String postfix1;
	private String postfix2;
	private boolean hasData = false;
	private boolean hasSecondaryData = false;
	/**
	 * For a secondary name and no associated data
	 * @param int index
	 */
	private Service(int appId) {
		this.appId = appId;
	}
	/**
	 * For a primary name and no associated data
	 * @param int index
	 * @param String color
	 */
	private Service(int appId, String color) {
		this.appId = appId;
		this.color = color;
	}	
	/**
	 * For a secondary name and one associated datum
	 * Prefix and postfix will be used with {@link VitalsParser.between()}.
	 * @param int index
	 * @param String data source (url_path, e.g.)
	 * @param String prefix
	 * @param String postfix
	 */
	private Service(int appId, String dataSource, String prefix, String postfix) {
		this.appId = appId;
		this.hasData = true;
		this.dataSource = dataSource;
		this.prefix1 = new String[] {prefix};
		this.postfix1 = postfix;
	}
	/**
	 * For a primary name and one associated datum
	 * Prefix and postfix will be used with {@link VitalsParser.between()}.
	 * @param int index
	 * @param String data source (url_path, e.g.)
	 * @param String prefix
	 * @param String postfix
	 * @param String color
	 */
	private Service(int appId, String dataSource, String prefix, String postfix, String color) {
		this.appId = appId;
		this.color = color;
		this.hasData = true;
		this.dataSource = dataSource;
		this.prefix1 = new String[] {prefix};
		this.postfix1 = postfix;
	}
	/**
	 * For a secondary name and two associated datum
	 * Prefix and postfix will be used with {@link VitalsParser.between()}.
	 * @param int index
	 * @param String data source (url_path, e.g.)
	 * @param String[] list of prefixes for the first datum
	 * @param String postfix for the first datum
	 * @param String[] list of prefixes for the second datum
	 * @param Sring postfix for the second datum
	 */
	private Service(int appId, String dataSource, String[] prefix1, String postfix1, String[] prefix2, String postfix2) {
		this.appId = appId;
		this.hasData = true;
		this.hasSecondaryData = true;
		this.dataSource = dataSource;
		this.prefix1 = prefix1;
		this.prefix2 = prefix2;
		this.postfix1 = postfix1;
		this.postfix2 = postfix2;
	}
	/**
	 * For a primary name and two associated datum
	 * Prefix and postfix will be used with {@link VitalsParser.between()}.
	 * @param int index
	 * @param String data source (url_path, e.g.)
	 * @param String[] list of prefixes for the first datum
	 * @param String postfix for the first datum
	 * @param String[] list of prefixes for the second datum
	 * @param Sring postfix for the second datum
	 * @param String color
	 */
	private Service(int appId, String dataSource, String[] prefix1, String postfix1, String[] prefix2, String postfix2, String color) {
		this.appId = appId;
		this.color = color;
		this.hasData = true;
		this.hasSecondaryData = true;
		this.dataSource = dataSource;
		this.prefix1 = prefix1;
		this.prefix2 = prefix2;
		this.postfix1 = postfix1;
		this.postfix2 = postfix2;
	}
	/**
	 * Returns the service index. New services will be added on to the end of the index list. Other is index 0, Filtered out is index -1, Unknown is index -2.
	 * @return int the index
	 */
	public int getId() {
		return this.appId;
	}
	/**
	 * Returns the Service data source. For example, "url_path" means the data is in the url_path.
	 * @return String the service data source
	 */
	public String getDataSource() {
		if (this.hasData) {
			return this.dataSource;
		} else {
			return null;
		}
	}
	/**
	 * Returns the Service string marker(s) for determining data
	 * @return String[] the service prefix marker(s)
	 */
	public String[] getPrefix() {
		if (this.hasData) {
			return this.prefix1;
		} else {
			return null;
		}
	}
	/**
	 * Returns the Service string marker(s) for determining secondary data
	 * @return String[] the service prefix marker(s) for secondary data
	 */
	public String[] getSecondaryPrefix() {
		if (this.hasSecondaryData) {
			return this.prefix2;
		} else {
			return null;
		}
	}
	/**
	 * Returns the Service string marker(s) for determining data
	 * @return String[] the service postfix marker(s)
	 */
	public String getPostfix() {
		if (this.hasData) {
			return this.postfix1;
		} else {
			return null;
		}
	}
	/**
	 * Returns the Service string marker(s) for determining secondary data
	 * @return String[] the service postfix marker(s) for secondary data
	 */
	public String getSecondaryPostfix() {
		if (this.hasSecondaryData) {
			return this.postfix1;
		} else {
			return null;
		}
	}
	/**
	 * Returns whether or not the Service has associated data
	 * @return boolean if the service has data
	 */
	public boolean hasData() {
		return this.hasData;
	}
	/**
	 * Returns whether or not the Service has secondary data. Should be false if hasData() is false.
	 * @return boolean if the service has secondary data
	 */
	public boolean hasSecondaryData() {
		return this.hasSecondaryData;
	}
	/**
	 * Returns the largest index. Useful for generating an upper bound for the number of Services
	 * @return int the maximum index
	 */
	public static int maxAppNumber() {
		int maxValue = 0;
		for (Service application : Service.values()) {
			if (application.getId() > maxValue) {maxValue = application.getId();}
		}
		return maxValue;
	}
	/**
	 * Returns the list of Service names and some associated data:
	 * The name is UCfirst
	 * The color for the service
	 * The available graph types (pie, bubble)
	 * @return JSONArray the Service names and associated info
	 */
	public static JSONArray getApplicationNames() {
		String appNumbers[] = new String[Service.maxAppNumber()+1];
		for (Service application : Service.values()) {
			if (appNumbers[application.getId()] == null) {
				appNumbers[application.getId()] = application.toString();
			}
		}
		JSONArray appList = new JSONArray();
		for (int i=0; i<appNumbers.length; i++) { // This is handled as a separate for loop to allow for blank applications in the above list.
			if (appNumbers[i] == null) {
				appList.add(null);
				continue;
			}
			Service application = Service.valueOf(appNumbers[i]);
			JSONObject appData = new JSONObject();
			String convertedAppName = application.toString().toLowerCase().replaceAll("_", " ");
			convertedAppName = Character.toUpperCase(convertedAppName.charAt(0))+convertedAppName.substring(1);
			appData.put("name", convertedAppName);
			JSONArray graphTypes = new JSONArray();
			if (application.getId() == 0) { // Other is the only graph that supports pie charts.
				graphTypes.add("Pie");
			}
			if (application.hasData()) {
				graphTypes.add("Bubble");
			}
			appData.put("graphTypes", graphTypes);
			appData.put("color", application.color);
			appList.add(appData);
		}
		return appList;
	}
}
