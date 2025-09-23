package com.ibm.lconn.automation.framework.services.ublogs.nodes;

import java.util.Iterator;
import java.util.Set;

import org.apache.wink.json4j.OrderedJSONObject;

/**
 * activitystreams entry represents activitystreams information.
 * 
 * @author Ping Wang - wangpin@us.ibm.com
 */
public class AccessControlEntry {
	
	private String acl, id;
	
	public String getAcl() {
		return acl;
	}

	public void setAcl(String acl) {
		this.acl = acl;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public AccessControlEntry(OrderedJSONObject obj1) {
		try {

			@SuppressWarnings("unchecked")
			Set<String> set1 = obj1.keySet();
			Iterator<String> it1 = set1.iterator();
			while (it1.hasNext()) {
				String key1 = it1.next().toString();
				if (key1.contains("acl")) {
					String value = obj1.getString(key1);
					setAcl(value);
				}
				if (key1.contains("id")) {
					String value = obj1.getString(key1);
					setId(value);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			// assertTrue(false);
		}
	}
}
