package com.ibm.lconn.automation.framework.services.search.data;

import com.ibm.json.java.JSONObject;

public class SocialConstraint{
	
	private String socialConstraintId;
	private IdentificationType identificationType;
	
	public static final String TYPE = "type";
	public static final String ID = "id";
	
	public SocialConstraint(IdentificationType identificationType, String socialConstraintId) {
		this.identificationType = identificationType;
		this.socialConstraintId = socialConstraintId;
	}
	
	
	public String toString(){
		JSONObject obj = new JSONObject();
		obj.put(TYPE, identificationType.toString());
		obj.put(ID, socialConstraintId);
		return obj.toString();
	}

}

