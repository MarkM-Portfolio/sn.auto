package com.ibm.smartcloud.metrics.dashboard.core;

import java.lang.String;
import com.ibm.json.java.*;

public class VitalsParser {
	/**
	 * A small, quick function to generate a unique number for a given string.
	 * @param String the string to be hashed
	 * @return int the hash
	 */
	private static int generateUUID(String hash) {
		int hashNum = 0;
		for (int i=0; i<hash.length(); i++) {
			hashNum += (int)hash.charAt(i);
			hashNum *= 3;
		}
		return hashNum;
	}
	/**
	 * Returns the segment of a string that is between the two query strings.
	 * For example, between("1234567890", "123", "789") returns "456".
	 * If the first string is not found, returns "".
	 * If the second string is not found, returns until EOL.
	 * @param String search string
	 * @param String prefix
	 * @param String postfix
	 * @return String sub string
	 */
	private static String between(String string, String before, String after) {
		if (string == null) {return null;}
		int start = string.indexOf(before);
		if (start == -1) {return "";}
		start += before.length();
		int end = string.indexOf(after, start);
		if (end == -1) {
			return string.substring(start);
		} else {
			return string.substring(start,end);
		}
	}
	/**
	 * Takes a user agent string and pulls Operating System (and version) along with Client (and version).
	 * In the case of webcrawlers, Client refers to the URL of the webcrawler.
	 * This is here rather than using a default library because IBM has some very weird user agents.
	 * @param String the user agent to be parsed
	 * @return String[] {Operating system, OS version, Client, Client version}
	 */
	private static String[] parseUserAgent(String userAgent) {
		String os = "";
		String client = "";
		String osVer = "";
		String clientVer = "";
		// Check bad defaults
		if (userAgent == null || userAgent.equals("-") || userAgent.equals("")) { // Some things forget to declare a user agent.
			userAgent = "Other/";
		}
		while (userAgent.contains("\\/")) {
			userAgent = userAgent.replaceAll("\\/","/"); // In case a \/ gets passed, remove it. It's just going to be confusing.
		
		}
		// Fill in operating system
		if (userAgent.contains("Android")) {
			os = "Android";
			osVer = between(userAgent, "Version/", " ");
		} else if (userAgent.contains("RIM-")) {
			os = "Blackberry";
			osVer = between(userAgent, "/", " ");
			client = "Blackberry";
			clientVer = between(userAgent, "RIM-", "-S");
			clientVer = between(clientVer, "", ".");
		} else if (userAgent.contains("Apple-iPad") || userAgent.contains("Apple-iPhone")) {
			os = "iOS";
			osVer = between(userAgent, " OS ", " like Mac");
			if (osVer.equals("")) {
				osVer = between(userAgent, "/", " ");
				if (osVer.equals("12A4265u")) {
					osVer = "1242.65";
				} else if (osVer.equals("12A4297e")) {
					osVer = "1242.97";
				} else if (osVer.equals("12A4318c")) {
					osVer = "1243.18";
				}
				int convertedVersion = 0;
				try {
					convertedVersion = (int)Math.floor(Double.parseDouble(osVer)*100);
				} catch (Exception e) {
					System.out.println("Error converting os version: "+osVer+" for user agent: "+userAgent);
				}
				// Credit to http://justworks.ca/blog/4c1-the-obscure-ios-user-agent-strings
				switch (convertedVersion) {
					case 124265: // 12A4265u
						osVer = "8.0beta1";
						break;
					case 124297: // 12A4297e
						osVer = "8.0beta2";
						break;
					case 124318: // 12A4318c
						osVer = "8.0beta3";
						break;
					case 70134:
						osVer = "3.0";
						break;
					case 70140:
						osVer = "3.0.1";
						break;
					case 70236:
						osVer = "3.2";
						break;
					case 70240:
						osVer = "3.2.1";
						break;
					case 70250: // 702.500
						osVer = "3.2.2";
						break;
					case 70314:
						osVer = "3.1";
						break;
					case 70411:
						osVer = "3.1.2";
						break;
					case 70518:
						osVer = "3.1.3";
						break;
					case 80129: 
						osVer = "4.0";
						break;
					case 80140:
						osVer = "4.0.2";
						break;
					case 80211:
						osVer = "4.1";
						break;
					case 80314:
						osVer = "4.2.1";
						break;
					case 80512:
						osVer = "4.2.5";
						break;
					case 80520:
						osVer = "4.2.6";
						break;
					case 80530:
						osVer = "4.2.7";
						break;
					case 80540:
						osVer = "4.2.8";
						break;
					case 80550:
						osVer = "4.2.9";
						break;
					case 80560:
						osVer = "4.2.10";
						break;
					case 80619:
						osVer = "4.3";
						break;
					case 80740:
						osVer = "4.3.1";
						break;
					case 80870:
						osVer = "4.3.2";
						break;
					case 81020: case 81030:
						osVer = "4.3.3";
						break;
					case 81120:
						osVer = "4.3.4";
						break;
					case 81210:
						osVer = "4.3.5";
						break;
					case 90133:
						osVer = "5.0";
						break;
					case 90140:
						osVer = "5.0.1";
						break;
					case 90217:
						osVer = "5.1";
						break;
					case 90220: // 902.206
						osVer = "5.1.1";
						break;
					case 100140: // 1001.40X
						osVer = "6.0";
						break;
					case 100152:
						osVer = "6.0.1";
						break;
					case 100155: case 100184: // 1001.8426
						osVer = "6.0.2";
						break;
					case 100185: // 1001.8550
					case 100214:
						if (osVer.equals("1002.146")) {
							osVer = "6.1.2";
						} else {
							osVer = "6.1";
						}
						break;
					case 100232: case 100235:
						osVer = "6.1.3";
						break;
					case 100250:
						osVer = "1002.500"; // NO IDEA
						break;
					case 110146:
						osVer = "7.0";
						break;
					case 110147:
						osVer = "7.0.1";
						break;
					case 110150:
						osVer = "7.0.2";
						break;
					case 110251: // 1102.511
						osVer = "7.0.3";
						break;
					case 110255: // 1102.55400001
						osVer = "7.0.4";
						break;
					case 110260:
						osVer = "7.0.5";
						break;
					case 110265: // 1102.651
						osVer = "7.0.6";
						break;
					case 110416: // 1104.167
						osVer = "7.1";
						break;
					case 110420: // 1104.201
						osVer = "7.1.1";
						break;
					case 110425:
						osVer = "7.1.2";
						break;
					case 0:
						osVer = "";
						break;
					default:
						osVer = "";
						System.out.println("Unknown apple UA version: "+userAgent+" Parsed as: "+convertedVersion);
						break;
				}
			}
			osVer = osVer.replaceAll("_", ".");
			int firstIndex = osVer.indexOf(".");
			int secondIndex = osVer.indexOf(".", firstIndex+1);
			if (secondIndex != -1) {
				osVer = osVer.substring(0, secondIndex);
			}
		} else if (userAgent.contains("Linux")) {
			os = "Linux";
			if (userAgent.contains("Ubuntu")) {
				osVer = "Ubuntu";
			}
		} else if (userAgent.contains("Mac")) {
			os = "Macintosh";
			osVer = between(userAgent, "X/", " ");
			if (osVer.equals("")) {
				osVer = between(userAgent, "OS X ", ")");
				osVer = between(osVer, "", ";");
			}
			osVer = osVer.replaceAll("_", ".");
			int firstIndex = osVer.indexOf(".");
			int secondIndex = osVer.indexOf(".", firstIndex+1);
			if (secondIndex != -1) {
				osVer = osVer.substring(0, secondIndex);
			}
		} else if (userAgent.contains("SunOS")) {
			os = "Solaris";
			osVer = between(userAgent, "SunOS ", ")");
			osVer = between(clientVer, "rv: ", ";");
		} else if (userAgent.contains("Windows Phone")) {
			os = "Windows Mobile";
			osVer = between(userAgent, "Phone ", ";");
			osVer = between(userAgent, "OS ", " ");
		} else if (userAgent.contains("Windows") && !userAgent.contains("IBM-LC")) { // IBM-LC strings contain a Windows keyword, but not a properly formed version
			os = "Windows";
			if (userAgent.contains("NT")) {
				osVer = between(userAgent, "NT ", ")");
				osVer = between(osVer, "", ";");
				int convertedVersion = 0;
				try {
					convertedVersion = (int)(Double.parseDouble(osVer)*10);  // Converts "6.1" to 61
				} catch (Exception e) {
					System.out.println("Error converting os version: "+osVer+" for user agent: "+userAgent);
				}
				// Credit to http://justworks.ca/blog/4c1-the-obscure-ios-user-agent-strings
				switch (convertedVersion) {
					case 63:
						osVer = "8.1";
						break;
					case 62:
						osVer = "8";
						break;
					case 61:
						osVer = "7";
						break;
					case 60:
						osVer = "Vista";
						break;
					case 52:
						osVer = "Server 2003";
						break;
					case 51:
						osVer = "XP";
						break;
					case 50:
						osVer = "2000";
						break;
					case 40: default:
						osVer = "NT";
						break;
					case 0:
						osVer = "";
						break;
				}
			} else { // Non-standard versioning
				osVer = between(userAgent, "Windows ", ")");
				osVer = between(osVer, "", " ");
			}
		}
		// Now analyze clients
		if (userAgent.contains("http://")) {
			os = "Webcrawler";
			client = between(userAgent, "http://", "/");
			client = between(client, "", ")");
		} else if (userAgent.contains("AddressBookSourceSync")) {
			client = "Address Book";
			clientVer = between(userAgent, "Sync/", " ");
			os = "Mac OS X";
			osVer = between(userAgent, "Mac OS X/", " ");
			int firstIndex = osVer.indexOf(".");
			int secondIndex = osVer.indexOf(".", firstIndex);
			if (secondIndex != -1) {
				osVer = osVer.substring(0, secondIndex);
			}
		} else if (userAgent.contains("Apple-iPad")) {
			client = "iPad";
			clientVer = between(userAgent, "iPad", "/");
			if (clientVer.equals("")) {
				clientVer = "1C0";
			}
			int convertedVersion = 0;
			try {
				convertedVersion = Integer.parseInt(clientVer.substring(0,1)+clientVer.substring(2));
			} catch (Exception e) {
				System.out.println("Error converting client version: "+clientVer+" for user agent: "+userAgent);
			}
			// Credit to http://justworks.ca/blog/4c1-the-obscure-ios-user-agent-strings
			switch (convertedVersion) {
				case 10: // 1C0, my own debug case
					clientVer = "Original";
					break;
				case 11: // 1C1
					clientVer = "1";
					break;
				case 21: case 22: case 23: // 2C1, 2C2, etc
					clientVer = "2";
					break;
				case 24: case 25:
					clientVer = "Mini";
					break;
				case 31: case 32:
					clientVer = "3";
					break;
				case 34: case 35: case 36: case 37:
					clientVer = "4";
					break;
				case 41: case 42:
					clientVer = "Air";
					break;
				case 44: case 45:
					clientVer = "Mini Retina";
					break;
				case 0:
					clientVer = "";
					break;
			}
		} else if (userAgent.contains("Apple-iPhone")) {
			client = "iPhone";
			clientVer = between(userAgent, "iPhone", "/");
			int convertedVersion = 0;
			try {
				convertedVersion = Integer.parseInt(clientVer.substring(0,1)+clientVer.substring(2));
			} catch (Exception e) {
				System.out.println("Error converting client version: "+clientVer+" for user agent: "+userAgent);
			}
			// Credit to http://justworks.ca/blog/4c1-the-obscure-ios-user-agent-strings
			switch (convertedVersion) {
				case 12: // 1C2
					clientVer = "3G";
					break;
				case 21: // 2C1
					clientVer = "3GS";
					break;
				case 31: case 33: // 3C1, etc
					clientVer = "4";
					break;
				case 41:
					clientVer = "4S";
					break;
				case 51: case 52:
					clientVer = "5";
					break;
				case 53: case 54:
					clientVer = "5C";
					break;
				case 61: case 62:
					clientVer = "5S";
					break;
				case 0:
					clientVer = "";
					break;
			}
		} else if (userAgent.contains("Camino")) {
			client = "Camino";
			clientVer = between(userAgent, "Camino/", " ");
		} else if (userAgent.contains("Firefox")) {
			client = "Firefox";
			clientVer = between(userAgent, "Firefox/", " ");
		} else if (userAgent.contains("Chrome")) {
			client = "Google Chrome";
			clientVer = between(userAgent, "Chrome/", " ");
		} else if (userAgent.contains("iCal")) {
			client = "iCal";
			clientVer = between(userAgent, "iCal/", " ");
			os = "Mac OS X";
			osVer = between(userAgent, "Mac OS X/", " ");
			int firstIndex = osVer.indexOf(".");
			int secondIndex = osVer.indexOf(".", firstIndex+1);
			if (secondIndex != -1) {
				osVer = osVer.substring(0, secondIndex);
			}
		} else if (userAgent.contains("IBM-LC-")) {
			userAgent = userAgent.replaceAll("\\+", "_");
			client = between(userAgent, "IBM-LC-", "_");
			client = between(client, "", " ");
			clientVer = between(userAgent, client, "(");
			os = between(clientVer, "-", " Java");
			clientVer = between(clientVer, "", "-");
			client = client.replaceAll("-", " ");
			clientVer = clientVer.replaceAll("_", ".");
			int firstIndex = clientVer.indexOf(".");
			int secondIndex = clientVer.indexOf(".", firstIndex+1);
			int thirdIndex = clientVer.indexOf(".", secondIndex+1);
			if (thirdIndex != -1) {
				clientVer = clientVer.substring(0, thirdIndex);
			}
		} else if (userAgent.contains("MSIE")) {
			if (os.equals("Windows Mobile")) {
				client = "Mobile Internet Explorer";
			} else {
				client = "Internet Explorer";
			}
			clientVer = between(userAgent, "MSIE ", ";");
		} else if (userAgent.contains("Java")) {
			client = "Java";
			clientVer = between(userAgent, "Java/1.", "."); // 1.7.0 -> 7.0
		} else if (userAgent.contains("TravelerToDo")) {
			client = "Lotus Traveler";
			clientVer = userAgent.substring(userAgent.indexOf("/")+1,userAgent.lastIndexOf("."));
		} else if (userAgent.contains("Lotus Traveler")) {
			client = "Lotus Traveler";
			os = between(userAgent, client+" ", " ");
			if (os.equals("WM")) {
				os = "Windows Mobile";
			}
			clientVer = between(userAgent, os+" ", " ");
		} else if (userAgent.contains("Lotus-Notes")) {
			client = "Lotus Notes";
			clientVer = between(userAgent, "Notes/", ";");
			os = between(userAgent, clientVer+"; ", ")");
		} else if (userAgent.contains("Mobile")) { // Similar to Safari, Mobile Safari occurs in user agent strings for other clients.
			client = "Mobile Safari";
			clientVer = between(userAgent, "Safari/", " ");
		} else if (userAgent.contains("Netscape")) {
			client = "Netscape";
			clientVer = between(userAgent, "Netscape/", " ");
		} else if (userAgent.contains("Opera Mini")) {
			client = "Opera Mini";
			clientVer = between(userAgent, "; Opera Mini/", "/");
		} else if (userAgent.contains("Opera")) {
			client = "Opera";
			clientVer = between(userAgent, "Opera ", "(");
		} else if (userAgent.contains("PhantomJS")) {
			client = "PhantomJS";
			clientVer = between(userAgent, "PhantomJS/", " ");
		} else if (userAgent.contains("Safari")) { // This key is occasionally in other user agent strings, so be sure to search last-ish
			client = "Safari";
			clientVer = between(userAgent, "Version/", " ");
		} else if (userAgent.contains("SametimeMeetingsMobile")) {
			client = "Sametime Meetings Mobile";
			clientVer = between(userAgent, "Mobile/", " ");
		} else if (userAgent.contains("Thunderbird")) {
			client = "Thunderbird";
			clientVer = between(userAgent, "Thunderbird/", " ");
		} else { // Default
			client = between(userAgent, "", "(").trim();
			if (client.contains("/")) {
				clientVer = between(client, "/", " ");
			} else if (client.contains(" ")) {
				clientVer = between(client, " ", " ");
			}
			if (client.equals("Mozilla")) {
				System.out.println("ERROR: Improperly parsed UA: "+userAgent);
			}
		}
		os = os.trim();
		osVer = osVer.replaceAll("_",".").trim();
		client = client.trim();
		clientVer = clientVer.replaceAll("_",".").trim();
		clientVer = between(clientVer, "", "."); // 25.0.0.1 -> 25
		return new String[] {os, osVer, client, clientVer};
	}
	/**
	 * Parses the data from VITALs and converts it into a format useful for our storage
	 * Date and time are converted to years, months, days, hours, and seconds since hour
	 * User agent is converted into os and client (and version)
	 * Unique file ID is created with timestamp, userid, and a hash of the client IP.
	 * This function also will add the parsed data into the cache queries
	 * @param JSONObject the visit/data point to be parsed
	 * @param int the segment number of the day
	 * @return JSONObject the parsed visit/data point
	 */
	public static JSONObject parseData(JSONObject data, int segmentNumber) {
		while (VitalsCrawler.activeCount() > 10) { // Was running into RAM issues. This prevents that, though not especially elegantly.
			try {
				Thread.sleep(60000); // Wait 1 minute
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		JSONObject thisData = new JSONObject();
		String appName = between((String)data.get("url_path"), "/", "/");
		long subscriberId = Long.parseLong((String)data.get("subscriber_id"))*2-1; // My very secure hashing method.
		thisData.put("subscriber_id", subscriberId); // Name of user
		long customerId = Long.parseLong((String)data.get("customer_id"))*2-1;
		thisData.put("customer_id", customerId); // Name of company
		String environment = (String)data.get("environment");
		thisData.put("environment", environment);
		String date = (String)data.get("request_date"); // YYYY-MM-DD
		try {
			if (appName.contains("apple-touch-icon")) {
				appName = "apple-touch-icon"; // There are a lot of resolutions for this icon.
			} else if (appName.contains("favicon")) {
				appName = "favicon"; // Ditto
			} else if ((appName.equals("dojo")) && ((String)data.get("request_referer")).contains("connections")) {
				appName = "dojoMeetings";
			}
			Service app = Service.valueOf(appName.replaceAll("[\\.-]", "_").toUpperCase()); // Converts things like com.ibm.sc to COM_IBM_SC
			int appId = app.getId();
			thisData.put("appId", appId);
			if (date != null && VitalsCrawler.verify(data)) { // Make sure data is from this month. Note: month is 0-indexed.
				VitalsCacher.addCustomerIdValue(segmentNumber, appId, customerId, customerId+""+subscriberId);
				VitalsCacher.addEnvironmentValue(segmentNumber, appId, environment, customerId+""+subscriberId);
				VitalsCacher.addUserVisit(Integer.parseInt(date.substring(8, 10)), appId, environment, customerId+""+subscriberId);
			}
			if (app.hasData()) {
				String dataSource = app.getDataSource();
				String prefix[] = app.getPrefix();
				String postfix = app.getPostfix();
				for (int i=0; i<prefix.length; i++) {
					String appData = between((String)data.get(dataSource), prefix[i], postfix);
					if (!appData.equals("")) {
						thisData.put("appData", appData+(appData.equals("")?"":appData.charAt(0)));
					}
				}
			}
			if (app.hasSecondaryData()) {
				String dataSource = app.getDataSource();
				String prefix[] = app.getSecondaryPrefix();
				String postfix = app.getSecondaryPostfix();
				for (int i=0; i<prefix.length; i++) {
					String appData = between((String)data.get(dataSource), prefix[i], postfix);
					if (!appData.equals("")) {
						thisData.put("appData2", appData+(appData.equals("")?"":appData.charAt(0)));
					}
				}
			}
			if (app.getId() == 0) {
				thisData.put("appData", appName);
			}
		} catch (IllegalArgumentException e) {
			thisData.put("appId", -2);
			VitalsCacher.addUnknownService(appName);
		}
		String userAgent = (String)data.get("user_agent");
		String userAgentData[] = parseUserAgent(userAgent);
		String os = userAgentData[0];
		String osVer = userAgentData[1];
		String client = userAgentData[2];
		String clientVer = userAgentData[3];
		thisData.put("client", client); // Client of requester / Host name of crawler
		thisData.put("clientVer", clientVer); // Client version
		thisData.put("os", os); // Operating system of requester
		thisData.put("osVer", osVer); // Operating system version
		//String date = (String)data.get("request_date"); // YYYY-MM-DD
		thisData.put("year", Integer.parseInt(date.substring(0,4)));
		thisData.put("month", Integer.parseInt(date.substring(5,7)));
		thisData.put("day", Integer.parseInt(date.substring(8,10)));
		String time = (String)data.get("request_time"); // HH:MM:SS
		thisData.put("hour", Integer.parseInt(time.substring(0,2)));
		thisData.put("secondsSinceHour", 60*Integer.parseInt(time.substring(3,5))+Integer.parseInt(time.substring(6,8)));
		String responseTimeString = (String)data.get("response_time");
		long responseTime = 0;
		if (responseTimeString != null && !responseTimeString.equals("")) {
			responseTime = Long.parseLong(responseTimeString); // Microseconds
		}
		thisData.put("responseTime", responseTime); // Time to get response from server
		thisData.put("responseSize", Integer.parseInt((String)data.get("response_size")));
		thisData.put("_id", ""+date+time+customerId+subscriberId+generateUUID((String)data.get("client_ip"))+generateUUID((String)data.get("url_path"))+responseTime);
		return thisData;
	}
}
