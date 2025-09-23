package com.ibm.conn.auto.util;

public class Video {
	
	private String url;
	
	private String session;
	
	public Video(String gridURL, String sessionID){
		url = gridURL;
		session = sessionID;
	}
	
	public String getURL(){
		return url;
	}
	
	public String getSession(){
		return session;
	}
}
