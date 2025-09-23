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
public class ApplicationEntry {
	
	private String id;
	private String displayName;
	private String url;
	private OrderedJSONObject image;
	
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public OrderedJSONObject getImage() {
		return image;
	}

	public void setImage(OrderedJSONObject image) {
		this.image = image;
	}

	public ApplicationEntry(OrderedJSONObject obj) {
		try {

			@SuppressWarnings("unchecked")
			Set<String> set = obj.keySet();
			Iterator<String> it = set.iterator();
			while (it.hasNext()){
				String key = it.next().toString();
				if (key.contains("generator")){
					OrderedJSONObject obj1 = (OrderedJSONObject) obj.get(key);
					@SuppressWarnings("unchecked")
					Set<String> set1 = obj1.keySet();
					Iterator<String> it1 = set1.iterator();
					while (it1.hasNext()){
						String key1 = it1.next().toString();
						
		            	if ( key1.contains("id")){
		            		String value = obj1.getString(key1);
		            		setId(value);
		            	}
		            	if ( key1.contains("displayName")){
		            		String value = obj1.getString(key1);
		            		setDisplayName(value);
		            	}
		            	if ( key1.contains("url")){
		            		String value = obj1.getString(key1);
		            		setUrl(value);
		            	}
						if ( key1.contains("image")){
							OrderedJSONObject obj2 = (OrderedJSONObject) obj1.getJSONObject(key1);
							setImage(obj2);
						}
					}
				}

			}



		} catch (Exception ex) {
			ex.printStackTrace();
			//assertTrue(false);
		}


	}
	

}
