package com.ibm.lconn.automation.framework.services.ublogs.nodes;

import java.util.Iterator;
import java.util.Set;

import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.OrderedJSONObject;

/**
 * Ublog object represents a microblogging information.
 * 
 * @author Ping Wang - wangpin@us.ibm.com
 */
public class UblogLikes {
	
	private JSONArray jsonItemsArray;
	private String totalItems;
	private String url;
		

	public JSONArray getJsonItemsArray() {
		return jsonItemsArray;
	}

	public void setJsonItemsArray(JSONArray jsonItemsArray) {
		this.jsonItemsArray = jsonItemsArray;
	}

	public String getTotalItems() {
		return totalItems;
	}

	public void setTotalItems(String totalItems) {
		this.totalItems = totalItems;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}


	public UblogLikes(OrderedJSONObject obj1) {
		try {

			@SuppressWarnings("unchecked")
			Set<String> set1 = obj1.keySet();
			Iterator<String> it1 = set1.iterator();
			while (it1.hasNext()){
				String key1 = it1.next().toString();
            	if ( key1.contains("totalItems")){
            		String value = obj1.getString(key1);
            		setTotalItems(value);
            	}
            	if ( key1.contains("url")){
            		String value = obj1.getString(key1);
            		setUrl(value);
            	}
				if ( key1.contains("items")){
					JSONArray jsonItemsArray = obj1.getJSONArray(key1);
					setJsonItemsArray(jsonItemsArray);
				}

			}



		} catch (Exception ex) {
			ex.printStackTrace();
			//assertTrue(false);
		}


	}
	

}
