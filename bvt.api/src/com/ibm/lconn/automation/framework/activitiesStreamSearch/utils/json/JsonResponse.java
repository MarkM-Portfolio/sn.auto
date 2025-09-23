package com.ibm.lconn.automation.framework.activitiesStreamSearch.utils.json;

import java.util.ArrayList;

import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;
import com.jayway.jsonpath.JsonPath;

public class JsonResponse {
	JSONObject jObj;

	public JsonResponse(JSONObject jObj) {
		this.jObj = jObj;
	}

	public Object find(String path) throws Exception {
		// System.out.println("The path is: "+path);
		Object jsonValue = JsonPath.read(jObj, path);
		return jsonValue;
	}

	public int count(String path) {
		JSONArray entryArr = (JSONArray) jObj.get(path);
		int entry_count = entryArr.size();// gets the entries number
		return entry_count;
	}

	@Override
	public String toString() {
		return "JsonResponse [jObj=" + jObj + "]";
	}

	public ArrayList<String> getAllValues(String path) {
		ArrayList<String> listOfValues = JsonPath.read(jObj, path);
		return listOfValues;
	}

	// Checks for a value
	public String findValue(String path) throws Exception {
		// System.out.println("The path is: "+path);
		String returned_str = JsonPath.read(jObj, path);
		return returned_str;
	}

	// Checks first at original path for a value, if returned null then check at
	// alternate path
	public Object findValueWithAltPath(String path, String altPath)
			throws Exception {
		// System.out.println("The path is: "+path);
		Object jsonValue = JsonPath.read(jObj, path);
		if (jsonValue == null) {
			System.out.println("Original path for returned null");
			System.out.println("Performing value find with alternate path");
			jsonValue = JsonPath.read(jObj, altPath);
		}
		return jsonValue;
	}

	public Double getDoubleValue(String path) {
		Double returnedValue = JsonPath.read(jObj, path);
		return returnedValue;
	}

	public Integer getIntValue(String path) {
		Integer returnedValue = JsonPath.read(jObj, path);
		return returnedValue;
	}

	public ArrayList<Double> getAllValuesDouble(String path) {
		ArrayList<Double> listOfValues = JsonPath.read(jObj, path);
		return listOfValues;
	}

	public ArrayList<Integer> getAllValuesInteger(String path) {
		ArrayList<Integer> listOfValues = JsonPath.read(jObj, path);
		return listOfValues;
	}

}
