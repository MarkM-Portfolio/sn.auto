package com.ibm.smartcloud.metrics.dashboard.core;
import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.net.*;
import java.io.*;
import java.sql.*;
import com.ibm.json.java.*;

class fromVitalsToProxy {
	private static JSONObject createMultiLevelGraph(JSONArray inputData, String groupType, String elementType, boolean multiApp) {
		JSONObject toReturnMultiApp = new JSONObject();
		JSONObject toReturn = new JSONObject();
		JSONArray dataArray = new JSONArray();
		JSONArray listOfEverything = new JSONArray();
		JSONArray groupList = new JSONArray();
		JSONObject inputLine = (JSONObject)inputData.get(0);
		for (int j=0; j<inputData.size();) {
			String groupName = (String)inputLine.get(groupType);
			JSONArray elementList = new JSONArray();
			JSONArray group = new JSONArray(); // Constructing two-level data (group)
			String appName = (String)inputLine.get("appName"); // Null if not multi-app, but also unused
			if (multiApp) while (appName.equals(inputLine.get("appName"))) { // If multi-app, we need an additional loop so we can group by application
			while (groupName.equals(inputLine.get(groupType))) {
				String elementName = (String)inputLine.get(elementType);
				JSONArray element = new JSONArray(); // Constructing onClick data (element)
				element.add(listOfEverything.size());
				listOfEverything.add(elementName);
				element.add(groupList.size());
				addUserCounts(element, inputLine.get("numUniqueUsers"), inputLine.get("totalHits"));
				dataArray.add(element); // Done with onClick data (element)
				element = new JSONArray(); // Constructing two-level data (element)
				element.add(elementList.size());
				elementList.add(elementName);
				addUserCounts(element, inputLine.get("numUniqueUsers"), inputLine.get("totalHits"));
				group.add(element); // Done with two-level data (element)
				j++;
				inputLine = (JSONObject)inputData.get(j);
			} // Ends while loop
			{
				JSONObject temp = new JSONObject();
				temp.put("categories", elementList);
				temp.put("data", group);
				toReturn.put(groupName, temp);
			} // Done with onClick data (group)
			group = new JSONArray(); // Constructing two-level data (group)
			group.add(listOfEverything.size());
			listOfEverything.add(groupName);
			group.add(null);
			addUserCounts(group, 0, 0); // Size is specified by the files
			dataArray.add(group); // Done with two-level data (group)
		} // Ends for loop if single app, ends first while loop if multi-app
		toReturn.put("categories", listOfEverything);
		toReturn.put("categoriesParent", groupList);
		toReturn.put("data", dataArray);
		if (!multiApp) return toReturn;
		if (multiApp) toReturnMultiApp.put(appName, toReturn); } // Ends for loop if multi-app
		return toReturnMultiApp;
	}
	private static JSONArray addUserCounts(JSONArray entry, Object numUniqueUsers, Object totalHits) {
		int unique = 0;
		int nonUnique = 0;
		try {
			unique = Integer.parseInt((String)numUniqueUsers);
			nonUnique = Integer.parseInt((String)numUniqueUsers);
		} catch (Exception e) {e.printStackTrace();}
		double uniqueSort = 1000 + unique / 1000.0;
		double nonUniqueSort = 1000 + nonUnique / 1000.0;
		entry.add(unique);
		entry.add(uniqueSort);
		entry.add(nonUnique);
		entry.add(nonUniqueSort);
		return entry;
	}
	private static JSONArray getInputData(String query) {
		JSONObject inputData = new JSONObject();
		try {
			URL url = new URL("http://scvitals.swg.usma.ibm.com/sqlview-scvitals/json.php?sql="+query);
			url = url.toURI().toURL();
			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
			String readLine;
			String output = "";
			while ((readLine = reader.readLine()) != null) {
				output += readLine;
			}
			reader.close();
			inputData = JSONObject.parse(output);
		} catch (Exception e) {e.printStackTrace();}
		if (inputData == null) {
			return new JSONArray();
		}
		JSONArray toReturn = (JSONArray)inputData.get("rows");
		return toReturn;
	}
	public static void main(String args[]) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(new Date());
		switch(args.length) {
			default:
			case 4:
				cal.set(Calendar.YEAR, Integer.parseInt(args[3]));
			case 3:
				cal.set(Calendar.MONTH, Integer.parseInt(args[2]));
			case 2:
				cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(args[1]));
			case 1: // Specified epoch date
				int args0 = Integer.parseInt(args[0]);
				if (args0 <= Integer.MAX_VALUE) {
					args0 *= 1000;
				}
				cal.setTimeInMillis(args0);
			case 0:
				cal.set(Calendar.HOUR, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
		}
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int dayOfYear = cal.get(Calendar.DAY_OF_YEAR);
		long epoch = cal.getTimeInMillis();
		// Epoch is the beginning of the current day
		JSONArray bulkDocs = new JSONArray();
		for (int i=0; i<24+7+1; i++) { // 24 hours + 7 days + 1 month.
			long startTime = 0;
			long endTime = 0;
			if (i < 24) {
				startTime = epoch + 3600*1000*i;
				endTime = startTime + 3600*1000; // One hour
			} else if (i < 24+7) {
				startTime = epoch - 24*3600*1000*(i-24); // From 1-7 days as (i-24) goes 0-6.
				endTime = epoch + 24*3600*1000; // One day
			} else if (i == 24+7+1 && day == 1) {
				endTime = epoch;
				Calendar temp = (Calendar)cal.clone();
				temp.add(Calendar.MONTH, -1);
				startTime = temp.getTimeInMillis();
			} // Nothing beyond a month is saved in vitals
			JSONObject toReturn = new JSONObject(); // The file we are returning
			JSONArray functionList = new JSONArray(); // The list of function names
			JSONArray functionArray = new JSONArray(); // The actual function contents
			JSONArray inputData; // Data pulled from a SQL query 
			JSONArray functionData; // Temporary constructor for functions
			String users = " COUNT(DISTINCT customer_id, subscriber_id) AS numUniqueUsers, COUNT(*) as totalHits";
			String timeSegment = " WHERE time BETWEEN "+startTime+" AND "+endTime;
			String database = " FROM collabservsvt2accesslog"+day;
			//Since data has two components (unique, nonunique), let rave sort the data.

				// For All Applications
			// Usage both unique and nonUnique
			inputData = getInputData("SELECT app AS appName,"+users+timeSegment+database+" GROUP BY appName");
			functionData = new JSONArray();
			for (int j=0; j<inputData.size(); j++) {
				JSONObject appData = (JSONObject)inputData.get(j);
				JSONArray entry = new JSONArray();
				entry.add(appData.get("appName"));
				entry = addUserCounts(entry, appData.get("numUniqueUsers"), appData.get("totalHits"));
				functionData.add(entry);
			}
			functionList.add("PerAppTotals");
			functionArray.add(functionData);
			
			// Latency data
			inputData = getInputData("SELECT app AS appName, 1.0*AVERAGE(response_size)/AVERAGE(response_time) AS downloadRate"+timeSegment+database+" GROUP BY appName");
			functionData = new JSONArray();
			for (int j=0; j<inputData.size(); j++) {
				JSONObject appData = (JSONObject)inputData.get(j);
				JSONArray entry = new JSONArray();
				entry.add(appData.get("appName"));
				entry.add(startTime);
				entry.add(appData.get("downloadRate"));
				functionData.add(entry);
			}
			functionList.add("PerAppLatency");
			functionArray.add(functionData);
			
			// Object list data (not files)
			inputData = getInputData("SELECT appData AS appData, app as appName"+users+timeSegment+database+" GROUP BY appName, appData SORT BY appName");
			JSONObject entryData = new JSONObject();
			JSONObject appData = (JSONObject)inputData.get(0); // Prime the data
			for (int j=0; j<inputData.size();) {
				String appName = (String)appData.get("appName"); // Pull the app name for the next application
				JSONArray categories = new JSONArray();
				JSONArray dataPerApp = new JSONArray();
				while (appName.equals((String)appData.get("appName"))) { // For all entries under this app name
					JSONArray dataPoint = new JSONArray();
					j++; // Increment to the next entry in the overall list
					appData = (JSONObject)inputData.get(j); // Pull the next entry
					dataPoint.add(categories.size()); // Index
					categories.add(appData.get("appName"));
					addUserCounts(dataPoint, appData.get("numUniqueUsers"), appData.get("totalHits"));
					dataPerApp.add(dataPoint);
				}
				JSONObject entry = new JSONObject();
				entry.put("categories", categories);
				entry.put("data", dataPerApp);
				entryData.put(appName, entry);
			}
			functionList.add("UsageData");
			functionArray.add(entryData);
			
			// Per App User Agents (OS)
			inputData = getInputData("SELECT app AS appName, os AS os, osVer AS version,"+users+timeSegment+database+" GROUP BY appName, os, version");
			functionList.add("PerAppUserAgents_OS");
			functionArray.add(createMultiLevelGraph(inputData, "os", "version", true));
			
			// Per App User Agents (Client)
			inputData = getInputData("SELECT app as appName, client AS client, clientVer AS version,"+users+timeSegment+database+" GROUP BY appName, client, version");
			functionList.add("PerAppUserAgents_Client");
			functionArray.add(createMultiLevelGraph(inputData, "client", "version", true));
			
				// Totals
			// Usage both unique and nonUnique
			inputData = getInputData("SELECT"+users+timeSegment+database);
			functionData = new JSONArray();
			JSONArray entry = new JSONArray();
			{
				JSONObject temp = (JSONObject)inputData.get(0);
				entry.add(startTime);
				entry = addUserCounts(entry, appData.get("numUniqueUsers"), appData.get("totalHits"));
			}
			functionData.add(entry);
			functionList.add("TotalCounts");
			functionArray.add(functionData);
			
			// Latency data
			inputData = getInputData("SELECT 1.0*AVERAGE(response_size)/AVERAGE(response_time) AS downloadRate"+timeSegment+database);
			functionData = new JSONArray();
			entry = new JSONArray();
			{
				JSONObject temp = (JSONObject)inputData.get(0);
				entry.add(startTime);
				entry.add(temp.get("downloadRate"));
			}
			functionData.add(entry);
			functionList.add("TotalLatency");
			functionArray.add(functionData);
			
			// User Agents (OS)
			inputData = getInputData("SELECT os AS os, osVer AS version,"+users+timeSegment+database+" GROUP BY os, version");
			functionList.add("UserAgents_OS");
			functionArray.add(createMultiLevelGraph(inputData, "os", "version", false));
			
			// User Agents (Client)
			inputData = getInputData("SELECT client AS client, clientVer AS version,"+users+timeSegment+database+" GROUP BY client, version");
			functionList.add("UserAgents_Client");
			functionArray.add(createMultiLevelGraph(inputData, "client", "version", false));
			
				// App Specific
			// File list data
			inputData = getInputData("SELECT library AS libraryName, file AS fileName,"+users+timeSegment+" WHERE app=\"Files\""+database+" GROUP BY libraryName, fileName");
			functionList.add("FilesData");
			functionArray.add(createMultiLevelGraph(inputData, "libraryName", "fileName", false));

			toReturn.put("_id", startTime+" "+endTime);
			toReturn.put("functionList", functionList);
			toReturn.put("functionArray", functionArray);
			bulkDocs.add(toReturn);
		}
		String output = "";
		JSONArray failed = new JSONArray();
		int returnCounts[] = new int[3];
		try {
			JSONObject toUpload = new JSONObject();
			toUpload.put("docs", bulkDocs);
			// Code heavily adapted from http://www.javatute.com/javatute/faces/post/couchdb/2014/java-curl-request-using-httpurlconntection.xhtml
			HttpURLConnection HTTP = (HttpURLConnection)(new URL("http://efdfa4be-13ea-4bc5-af0b-c47d2e97154f-bluemix.cloudant.com/proxy/_bulk_docs")).openConnection();
			HTTP.setRequestMethod("POST");
			HTTP.setRequestProperty("Content-Type", "application/json");
			HTTP.setDoOutput(true);
			OutputStreamWriter out = new OutputStreamWriter(HTTP.getOutputStream(), "UTF-8");
			out.write(toUpload.toString());
			out.close();
			System.out.println("Response code: "+HTTP.getResponseCode());
			output = (new BufferedReader(new InputStreamReader(HTTP.getInputStream()))).readLine();
			JSONArray confirm = JSONArray.parse(output);
			int numFiles = confirm.size();
			for (int i=0; i<numFiles; i++) {
				JSONObject file = (JSONObject)confirm.get(i);
				if (file.get("error") == null) {
					returnCounts[0]++;
				} else {
					if (file.get("error").equals("conflict")) {
						returnCounts[1]++;
					} else {
						returnCounts[2]++;
					}
					failed.add(file);
				}
			}
		} catch (Exception e) {e.printStackTrace();}
		System.out.println("Completed: "+returnCounts[0]);
		System.out.println("Conflict: "+returnCounts[1]);
		System.out.println("Other Error: "+returnCounts[2]);
	}
}
