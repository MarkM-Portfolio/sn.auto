package com.ibm.conn.auto.tests.homepage.fvt.orientme.data;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ibm.conn.auto.tests.homepage.fvt.orientme.objects.TestCaseData.Key;
import com.ibm.conn.auto.tests.homepage.fvt.orientme.objects.TestCaseData.Value;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2017                                          */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * @author 	Anthony Cox
 * Date:	23rd February 2017
 */

public class JSONData {

	protected static Logger log = LoggerFactory.getLogger(JSONData.class);
	
	private static final String ATTRIBUTES_SEPARATOR = ", ";
	private static final String CLOSE_PARENTHESIS = " }";
	private static final String DOUBLE_QUOTE = "\"";
	private static final String KEY_VALUE_SEPARATOR = ": ";
	private static final String OPEN_PARENTHESIS = "{ ";
	
	/**
	 * Outputs all contents of the specified HashMap instance to a JSON String
	 * 
	 * @return - The JSON String content of the data
	 */
	public static String convertHashMapToJSONString(HashMap<String, String> hashMapOfData) {
		
		String jsonString = OPEN_PARENTHESIS;
		
		Set<String> setOfTestCaseIndexes = hashMapOfData.keySet();
		for(String testCaseIndex : setOfTestCaseIndexes) {
			jsonString += testCaseIndex + KEY_VALUE_SEPARATOR + OPEN_PARENTHESIS;
			jsonString += hashMapOfData.get(testCaseIndex);
			jsonString += CLOSE_PARENTHESIS + ATTRIBUTES_SEPARATOR;
		}
		jsonString = jsonString.substring(0, jsonString.lastIndexOf(ATTRIBUTES_SEPARATOR));
		jsonString += CLOSE_PARENTHESIS;
		
		return jsonString;
	}
	
	/**
	 * Creates a JSON String for identifying a JSON object with the specified index position set
	 * 
	 * @param objectIndexPosition - The Integer value of the index position for the current object
	 * @return - The JSON String which identifies this object
	 */
	public static String createJSONObjectIdentifierString(int objectIndexPosition) {
		return JSONData.DOUBLE_QUOTE + objectIndexPosition + JSONData.DOUBLE_QUOTE;
	}
	
	/**
	 * Creates a JSON String with the specified Key: Value pairs
	 * 
	 * @param key - The Key instance of the key to be set in the JSON String
	 * @param value - The String instance of the value to be set in the JSON String
	 * @return - The JSON String
	 */
	public static String createKeyValuePairAsString(Key key, String value) {
		return DOUBLE_QUOTE + key.getKey() + DOUBLE_QUOTE + KEY_VALUE_SEPARATOR + DOUBLE_QUOTE + value.trim() + DOUBLE_QUOTE;
	}
	
	/**
	 * Creates a JSON String with the specified Key: Value pairs
	 * 
	 * @param key - The Key instance of the key to be set in the JSON String
	 * @param value - The Value instance of the value to be set in the JSON String
	 * @return - The JSON String
	 */
	public static String createKeyValuePairAsString(Key key, Value value) {
		return DOUBLE_QUOTE + key.getKey() + DOUBLE_QUOTE + KEY_VALUE_SEPARATOR + DOUBLE_QUOTE + value.getValue().trim() + DOUBLE_QUOTE;
	}
	
	/**
	 * Creates a String which includes all key / value pairs for the specified JSON object data
	 * 
	 * @param listOfData - The List<String> instance of all data to be included in the JSON String
	 * @return - The String of all JSON data in key / value pairs
	 */
	public static String createStringOfAllJSONObjectKeyValuePairs(List<String> listOfData) {
		int index = 0;
		String allDataString = "";
		while(index < listOfData.size()) {
			allDataString += listOfData.get(index);	
			if(index < listOfData.size() - 1) {
				allDataString += ATTRIBUTES_SEPARATOR;
			}
			index ++;
		}
		return allDataString;
	}
	
	/**
	 * Retrieves the JSON object at the specified index position in the specified JSON content
	 * 
	 * @param indexPosition - The Integer value of the objects index position to retrieve from the JSON content
	 * @param jsonContent - The JSON content to retrieve the object from
	 * @return - The String content of the JSON object if found in the JSON content - null otherwise
	 */
	public static String getObjectFromJsonContent(int indexPosition, String jsonContent) {
		
		log.info("INFO: Now retrieving the JSON object at index position " + indexPosition + " from the JSON content: " + jsonContent);
		
		String currentIndexPosition = DOUBLE_QUOTE + indexPosition + DOUBLE_QUOTE;
		int startIndexOfObject = jsonContent.indexOf(currentIndexPosition);
		
		if(startIndexOfObject > -1) {
			log.info("INFO: Successfully found the JSON object at index position: " + indexPosition);
			String objectContent = jsonContent.substring(startIndexOfObject);
			
			int endIndexOfObject = objectContent.indexOf(CLOSE_PARENTHESIS);
			objectContent = objectContent.substring(0, endIndexOfObject + CLOSE_PARENTHESIS.length()).trim();
			
			log.info("INFO: The JSON object at index position " + indexPosition + " has been retrieved: " + objectContent);
			return objectContent;
		}
		log.info("INFO: The JSON object at index position " + indexPosition + " could NOT be retrieved");
		log.info("INFO: The end of the JSON content file has been reached");
		return null;
	}
	
	/**
	 * Retrieves the value for the specified key from the JSON object content String
	 * 
	 * @param jsonObjectContent - The String content of the JSON object
	 * @param key - The String content of the key whose value is to be retrieved
	 * @return - The String content of the attribute value if the attribute is found in the JSON object, null otherwise
	 */
	public static String getValueForSpecifiedKey(String jsonObjectContent, String key) {
		
		log.info("INFO: Now searching for the '" + key + "' key in the JSON object content: " + jsonObjectContent);
		
		String attributeInJson = DOUBLE_QUOTE + key + DOUBLE_QUOTE + KEY_VALUE_SEPARATOR + DOUBLE_QUOTE;
		int startIndexOfAttribute = jsonObjectContent.indexOf(attributeInJson);
		
		if(startIndexOfAttribute > -1) {
			log.info("INFO: The key '" + key + "' was successfully found in the JSON content");
			startIndexOfAttribute += attributeInJson.length();
			String attributeValue = jsonObjectContent.substring(startIndexOfAttribute);
			
			// Loop through and find the end of the attribute value (ie. the point at which the first instance of the double quote is found)
			int endIndexOfAttribute = 0;
			int index = 0;
			boolean foundEndOfAttributeValue = false;
			while(index < attributeValue.length() && foundEndOfAttributeValue == false) {
				if(attributeValue.charAt(index) == '\"') {
					endIndexOfAttribute = index;
					foundEndOfAttributeValue = true;
				}
				index ++;
			}
			attributeValue = attributeValue.substring(0, endIndexOfAttribute).trim();
			
			log.info("INFO: The value for the key '" + key + "' has been retrieved as: " + attributeValue);
			return attributeValue;
		}
		log.info("ERROR: The key '" + key + "' could NOT be found in the JSON content");
		return null;
	}
}