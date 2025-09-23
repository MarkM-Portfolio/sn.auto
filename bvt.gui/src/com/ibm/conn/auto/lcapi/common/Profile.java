package com.ibm.conn.auto.lcapi.common;

import java.util.ArrayList;
import java.util.Arrays;

import com.ibm.lconn.automation.framework.services.profiles.nodes.VCardEntry;

public class Profile {

	private ArrayList<String> tags;
	private VCardEntry vCard;
	
	public Profile(VCardEntry vCard, String tags){
		
		this.vCard = vCard;
		this.tags = new ArrayList<String>(Arrays.asList(tags.split(" ")));
	}
	
	public ArrayList<String> getTags(){
	
		return this.tags;
	}
	
	public VCardEntry getVCard(){
		
		return this.vCard;
	}
	
	
}
