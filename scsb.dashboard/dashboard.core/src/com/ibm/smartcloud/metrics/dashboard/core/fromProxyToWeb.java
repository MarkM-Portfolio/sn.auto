package com.ibm.smartcloud.metrics.dashboard.core;
/* The view function on couchDB (as of 6/24/2014):

"map":
"function(doc) {
	for(var i in doc.viewList) {
		emit([doc.functionList[i], doc.endTime-doc.startTime, doc.startTime], doc.functionArray[i]);
	}
}"
"reduce": "function(keys, values)"
*/

import java.net.*;
import java.io.*;
import com.ibm.json.java.*;

public class fromProxyToWeb {
	public static void main (String args[]) {
		String function = args[0];
		long timeSegments[] = new long[args.length-1];
		for (int i=1; i<args.length; i++) {
			timeSegments[i-1] = Long.parseLong(args[i]);
		}
		for (int i=0; i<timeSegments.length; i++) {
		}
	}
	
	public static String getData(String function, int startTime, int endTime) {
		// Long unix times have milliseconds.
		return getData(function, startTime*1000, endTime*1000).toString();
	}
	public static String getData(String function, long startTime, long endTime) {
		String toReturn = getDataInternal(function, startTime, endTime).toString();
		JSONArray appNames = new JSONArray();//#
		int numApps = appNames.size();
		for (int i=0; i<numApps; i++) {
			toReturn.replaceAll((String)appNames.get(i), ""+i); // Replace names with indicies
		}
		return toReturn;
	}
	private static JSONArray getDataInternal(String function, long startTime, long endTime) {
		JSONArray data = new JSONArray();
		try {
			String query = "startkey=[\""+function+"\","+(endTime-startTime)+","+startTime+"]&endkey=[\""+function+"\","+(endTime-startTime)+","+endTime+"]";
			BufferedReader reader = new BufferedReader(new InputStreamReader((new URL("http://efdfa4be-13ea-4bc5-af0b-c47d2e97154f-bluemix.cloudant.com/proxy/_design/Get/_view/Get?"+query)).openStream()));
			String output = "";
			String readLine;
			while ((readLine = reader.readLine()) != null) {
				output += readLine;
			}
			JSONObject temp = JSONObject.parse(output);
			data = (JSONArray)temp.get("rows");
		} catch (Exception e) {e.printStackTrace();}
		if (data == null) {
			return new JSONArray();
		}
		return data;
	}
	public static String getDataWithApps(String function, int startTime, int endTime) {
		JSONArray appNames = new JSONArray();//#
		int numApps = appNames.size();
		JSONArray outputData[] = new JSONArray[numApps];
		JSONArray rows = getDataInternal(function, startTime, endTime);
		for (int i=0; i<rows.size(); i++) {
			JSONObject row = (JSONObject)rows.get(i);
			for (int j=0; j<numApps; j++) {
				JSONArray appData = (JSONArray)row.get((String)appNames.get(j));
				if (appData != null) {
					if (outputData[j] == null) {
						outputData[j] = appData;
					} else {
						outputData[j].addAll(appData);
					}
				}
			} // Done with the row
		} // Done with all the data
		String toReturn = "[";
		for (int i=0; i<numApps; i++) {
			String appData = outputData[i].toString();
			appData.replaceAll((String)appNames.get(i), ""+i); // Replace with indicies
			toReturn += appData;
		}
		toReturn += "]";
		System.out.println(toReturn);
		return toReturn;
	}
}
