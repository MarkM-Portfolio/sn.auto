package com.ibm.lconn.automation.framework.services.ublogs.nodes;

import java.util.Iterator;
import java.util.Set;

import org.apache.wink.json4j.OrderedJSONObject;

/**
 * Ublog object represents a microblogging information.
 * 
 * @author Ping Wang - wangpin@us.ibm.com
 */
public class UblogObject {
	
	private String summary; // not for like
	private String objectType;  // note, comment, like
	private String id;
	private OrderedJSONObject replies, likes;
	private String author;  // author for comment, like  object
	private String published;  // published for comment, like  object
	
	
	public OrderedJSONObject getReplies() {
		return replies;
	}

	public void setReplies(OrderedJSONObject replies) {
		this.replies = replies;
	}

	public OrderedJSONObject getLikes() {
		return likes;
	}

	public void setLikes(OrderedJSONObject likes) {
		this.likes = likes;
	}
	
	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getPublished() {
		return published;
	}

	public void setPublished(String published) {
		this.published = published;
	}



	public UblogObject(OrderedJSONObject obj1) {
		try {

			@SuppressWarnings("unchecked")
			Set<String> set1 = obj1.keySet();
			Iterator<String> it1 = set1.iterator();
			while (it1.hasNext()){
				String key1 = it1.next().toString();
            	if ( key1.contains("summary")){
            		String value = obj1.getString(key1);
            		setSummary(value);
            	}
            	if ( key1.contains("objectType")){
            		String value = obj1.getString(key1);
            		setObjectType(value);
            	}
            	if ( key1.contains("id")){
            		String value = obj1.getString(key1);
            		setId(value);
            	}
            	if ( key1.contains("published")){
            		String value = obj1.getString(key1);
            		setPublished(value);
            	}
				if ( key1.contains("replies")){
					OrderedJSONObject obj2 = (OrderedJSONObject) obj1.getJSONObject(key1);
					setReplies(obj2);
				}
				if ( key1.contains("likes")){
					OrderedJSONObject obj2 = (OrderedJSONObject) obj1.getJSONObject(key1);
					setLikes(obj2);
				}
			
			}



		} catch (Exception ex) {
			ex.printStackTrace();
			//assertTrue(false);
		}


	}
	

}
