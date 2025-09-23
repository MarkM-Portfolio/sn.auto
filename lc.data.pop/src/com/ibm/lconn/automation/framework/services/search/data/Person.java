package com.ibm.lconn.automation.framework.services.search.data;

import com.ibm.json.java.JSONObject;

public class Person{
	
	public static final String TYPE = "type";
	public static final String ID = "id";
	
	private PersonIdType personIdType;
	private String personId;
	
	public Person(PersonIdType personType, String personId) {
		this.personIdType = personType;
		this.personId = personId;
	}
	
	public String toString(){
		JSONObject obj = new JSONObject();
		obj.put(TYPE, personIdType.toString());
		obj.put(ID, personId);
		return obj.toString();
	}

}

