package com.ibm.lconn.automation.framework.services.ublogs.nodes;

import java.util.Iterator;
import java.util.Set;

import org.apache.wink.json4j.OrderedJSONObject;

/**
 * Ublog object represents a microblogging information.
 * 
 * @author Ping Wang - wangpin@us.ibm.com
 */
public class UblogEntry {
	
	private String content;
	private String published;
	private OrderedJSONObject actor, object, target;
		
	
	public OrderedJSONObject getTarget() {
		return target;
	}

	public void setTarget(OrderedJSONObject target) {
		this.target = target;
	}

	public OrderedJSONObject getActor() {
		return actor;
	}

	public OrderedJSONObject getObject() {
		return object;
	}

	public void setObject(OrderedJSONObject object) {
		this.object = object;
	}

	public void setActor(OrderedJSONObject actor) {
		this.actor = actor;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getPublished() {
		return published;
	}

	public void setPublished(String published) {
		this.published = published;
	}


	public UblogEntry(OrderedJSONObject obj1) {
		try {

			@SuppressWarnings("unchecked")
			Set<String> set1 = obj1.keySet();
			Iterator<String> it1 = set1.iterator();
			while (it1.hasNext()){
				String key1 = it1.next().toString();
            	if ( key1.contains("content")){
            		String value = obj1.getString(key1);
            		setContent(value);
            	}
            	if ( key1.contains("published")){
            		String value = obj1.getString(key1);
            		setPublished(value);
            	}
            	if ( key1.contains("actor")){
            		OrderedJSONObject obj2 = (OrderedJSONObject) obj1.getJSONObject(key1);
            		setActor(obj2);
            	}
				if ( key1.contains("object")){
					OrderedJSONObject obj2 = (OrderedJSONObject) obj1.getJSONObject(key1);
					setObject(obj2);
				}

			}



		} catch (Exception ex) {
			ex.printStackTrace();
			//assertTrue(false);
		}


	}
	

}
