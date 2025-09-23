package com.ibm.lconn.automation.framework.services.ublogs.nodes;

import java.util.Iterator;
import java.util.Set;

import org.apache.wink.json4j.OrderedJSONObject;

/**
 * Ublog Entry represents a microblogging Entry.
 * 
 * @author Ping Wang - wangpin@us.ibm.com
 */
public class JsonEntry {

	private OrderedJSONObject jsonEntry;


	public OrderedJSONObject getJsonEntry() {
		return jsonEntry;
	}

	public void setJsonEntry(OrderedJSONObject jsonEntry) {
		this.jsonEntry = jsonEntry;
	}

	public JsonEntry(String JSON) {
		try {

			OrderedJSONObject obj0 = new OrderedJSONObject(JSON);
			@SuppressWarnings("unchecked")
			Set<String> set0 = obj0.keySet();
			Iterator<String> it0 = set0.iterator();
			while (it0.hasNext()) {
				String key0 = it0.next().toString();
				if (key0.contains("entry")) {
					
					OrderedJSONObject jsonEntry = (OrderedJSONObject) obj0.getJSONObject(key0);
					setJsonEntry(jsonEntry);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			// assertTrue(false);
		}

	}

}
