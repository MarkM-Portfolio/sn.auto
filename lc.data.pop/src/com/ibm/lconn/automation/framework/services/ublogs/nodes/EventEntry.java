package com.ibm.lconn.automation.framework.services.ublogs.nodes;

import java.util.Iterator;
import java.util.Set;

import org.apache.wink.json4j.OrderedJSONObject;

/**
 * Application object represents activity stream applications information.
 * https://<connection-server>/connections/opensocial/rest/activitystreams/@me/@applications/
 * 
 * @author Ping Wang - wangpin@us.ibm.com
 */
public class EventEntry {
	
	private String id;
	
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public EventEntry(OrderedJSONObject obj) {
		try {

			@SuppressWarnings("unchecked")
			Set<String> set = obj.keySet();
			Iterator<String> it = set.iterator();
			while (it.hasNext()){
				String key = it.next().toString();
				if (key.contains("id")){
            		String value = obj.getString(key);
            		setId(value);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			//assertTrue(false);
		}


	}
	

}
