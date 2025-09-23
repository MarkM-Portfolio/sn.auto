package com.ibm.lconn.automation.framework.services.ublogs.nodes;

import java.util.Iterator;
import java.util.Set;

import org.apache.wink.json4j.OrderedJSONObject;

/**
 * UblogConfig object represents activityStream/microblogging config information.
 * https://<Connections-Server>/connections/opensocial/rest/ublog/@config/settings
 * 
 * @author Ping Wang - wangpin@us.ibm.com
 */
public class ConfigEntry {
	
	private String activityStreamCommentDisplayMaxChars;
	private String microblogCommentMaxChars;
	private String microblogEntryMaxChars;
	private String activityStreamEntryDisplayMaxChars;
	

	public String getActivityStreamCommentDisplayMaxChars() {
		return activityStreamCommentDisplayMaxChars;
	}


	public void setActivityStreamCommentDisplayMaxChars(
			String activityStreamCommentDisplayMaxChars) {
		this.activityStreamCommentDisplayMaxChars = activityStreamCommentDisplayMaxChars;
	}


	public String getMicroblogCommentMaxChars() {
		return microblogCommentMaxChars;
	}


	public void setMicroblogCommentMaxChars(String microblogCommentMaxChars) {
		this.microblogCommentMaxChars = microblogCommentMaxChars;
	}


	public String getMicroblogEntryMaxChars() {
		return microblogEntryMaxChars;
	}


	public void setMicroblogEntryMaxChars(String microblogEntryMaxChars) {
		this.microblogEntryMaxChars = microblogEntryMaxChars;
	}


	public String getActivityStreamEntryDisplayMaxChars() {
		return activityStreamEntryDisplayMaxChars;
	}


	public void setActivityStreamEntryDisplayMaxChars(
			String activityStreamEntryDisplayMaxChars) {
		this.activityStreamEntryDisplayMaxChars = activityStreamEntryDisplayMaxChars;
	}


	public ConfigEntry(OrderedJSONObject obj1) {
		try {

			@SuppressWarnings("unchecked")
			Set<String> set1 = obj1.keySet();
			Iterator<String> it1 = set1.iterator();
			while (it1.hasNext()){
				String key1 = it1.next().toString();
            	if ( key1.contains("activityStreamEntryDisplayMaxChars")){
            		String value = obj1.getString(key1);
            		setActivityStreamEntryDisplayMaxChars(value);
            	}
            	if ( key1.contains("activityStreamCommentDisplayMaxChars")){
            		String value = obj1.getString(key1);
            		setActivityStreamCommentDisplayMaxChars(value);
            	}
            	if ( key1.contains("microblogEntryMaxChars")){
            		String value = obj1.getString(key1);
            		setMicroblogEntryMaxChars(value);
            	}
            	if ( key1.contains("microblogCommentMaxChars")){
            		String value = obj1.getString(key1);
            		setMicroblogCommentMaxChars(value);
            	}

			}

		} catch (Exception ex) {
			ex.printStackTrace();
			//assertTrue(false);
		}


	}
	

}
