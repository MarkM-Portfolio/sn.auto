package com.ibm.lconn.automation.framework.services.search.data;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Set;

import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;

public class PeopleFinderAdditionalField {
	public enum AdditionalFieldConfidence { low, medium, high};
	public enum AdditionalFieldValues {name, userType, jobResponsibility, email, givenNames, workPhone, mobilePhone, tieLine, country, state, city, location, postalCode, tag};
	JSONObject additionalFieldParamValue  = new JSONObject();

	public void addFields (AdditionalFieldConfidence confidance, Set <AdditionalFieldValues> values){
		JSONArray arr = new JSONArray();
		for (AdditionalFieldValues additionalFieldValue : values) {
			arr.add(additionalFieldValue.toString());
		}	
		additionalFieldParamValue.put(confidance.toString(), arr);
	}
	
	public JSONArray getFieldsByConfidence (AdditionalFieldConfidence confidance){
		return (JSONArray)additionalFieldParamValue.get(confidance.toString());
	}
	
	@Override
	public String toString() {
		String s = null;
		try {
			s =  URLEncoder.encode(additionalFieldParamValue.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return s;
	}
}
