package com.ibm.lconn.automation.framework.services.ublogs.nodes;

import java.util.Iterator;
import java.util.Set;

import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.OrderedJSONObject;

/**
 * Get Entries represents activityStream/microblogging Entries.
 * 
 * @author Ping Wang - wangpin@us.ibm.com
 */
public class JsonEntries {

	private String totalResults;
	private JSONArray jsonEntryArray;

	public String getTotalResults() {
		return totalResults;
	}

	public void setTotalResults(String totalResults) {
		this.totalResults = totalResults;
	}


	public JSONArray getJsonEntryArray() {
		return jsonEntryArray;
	}

	public void setJsonEntryArray(JSONArray jsonEntryArray) {
		this.jsonEntryArray = jsonEntryArray;
	}

	public JsonEntries(String JSON) {
		try {

			OrderedJSONObject obj0 = new OrderedJSONObject(JSON);
			@SuppressWarnings("unchecked")
			Set<String> set0 = obj0.keySet();
			Iterator<String> it0 = set0.iterator();
			while (it0.hasNext()) {
				String key0 = it0.next().toString();
				if (key0.contains("totalResults")) {
					String value = obj0.getString(key0);
					setTotalResults(value);
				}
				if (key0.contains("list")) {
					JSONArray jsonEntryArray = obj0.getJSONArray(key0);

					setJsonEntryArray(jsonEntryArray);

				}
				if (key0.contains("LIST")) {
					JSONArray jsonEntryArray = obj0.getJSONArray(key0);

					setJsonEntryArray(jsonEntryArray);

				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			// assertTrue(false);
		}

	}

}
