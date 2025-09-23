package com.ibm.lconn.automation.framework.search.rest.api;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.OrderedJSONObject;

public class OrientMeResponse {
	protected final static Logger logger = Logger
			.getLogger(OrientMeResponse.class.getName());
	private String orientMeResponseStr = "";
	
	private int status =-1;
	
    private int timeBoxesCount;
    
    private List<TimeBox> timeBoxes;
	private static final List<String> stackldsList = Arrays.asList("stackType",
			"stackId", "totalEventsNum", "stackScore");
    
    
    public int getStatus() {
		return status;
	}
	public int getTimeBoxesCount() {
		return timeBoxesCount;
	}

	public void setTimeBoxesCount(int timeBoxesCount) {
		this.timeBoxesCount = timeBoxesCount;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
	

	public OrientMeResponse(ClientResponse response) throws JSONException {
		status = response.getStatus();
		if (status != 200) {
			logger.fine("OrientMe response status : " + status);
		}
		try {
			orientMeResponseStr = readResponse(response.getReader(),response.toString());
			
			

		} catch (IOException e) {
			logger.fine("Can not read the response: " + e.getLocalizedMessage());
			e.printStackTrace();
		}
		if (orientMeResponseStr != ""){
		buildJsonResponse(response.toString());
		}
	}

	private String readResponse(Reader responseReader,String responseToString) {
		if (responseReader == null) {
			logger.fine("responseReader is NULL in response: "
					+ responseToString);
			return "";
		}
		StringBuffer sb = new StringBuffer();
		try {
			int charValue = 0;
			while ((charValue = responseReader.read()) != -1) {
				sb.append((char) charValue);
			}
		} catch (IOException e) {
			logger.fine("Read response to buffer error: "
					+ e.getLocalizedMessage());
			e.printStackTrace();
		}
		return sb.toString();
	}

	private void buildJsonResponse(String responseToString) throws JSONException {
		OrderedJSONObject jsonResponse;
		JSONArray jsonTimeBoxesArr = null;
		try {
			jsonResponse = new OrderedJSONObject(orientMeResponseStr);
						
			jsonTimeBoxesArr = jsonResponse.getJSONObject("connections").getJSONArray("timeBoxes");
		} catch (JSONException e) {
			logger.fine("buildJsonResponse error: "
					+ e.getLocalizedMessage()+" response: "+ responseToString );
			e.printStackTrace();
		}
		assertTrue("jsontimeBoxesArr is NULL in response: "+responseToString,jsonTimeBoxesArr != null);
		
		setTimeBoxesCount(jsonTimeBoxesArr.size());
		timeBoxes= convertJsonToListOfTimeBoxes(jsonTimeBoxesArr);
		
	}
	private List<TimeBox> convertJsonToListOfTimeBoxes(JSONArray jsonTimeBoxesArr) throws JSONException{
		List<TimeBox> timeBoxes = new ArrayList<TimeBox>();
		for (Object object : jsonTimeBoxesArr) {
			OrderedJSONObject jsonTimeBox = (OrderedJSONObject) object;
			
			String jsonTimeBoxStart =  jsonTimeBox.get("start") != null ? jsonTimeBox.get("start").toString() : "";
			JSONArray timeBoxStacksArr=jsonTimeBox.getJSONArray("stacks");
			List <Stack> timeBoxStacks = convertJsonToListOfStacks(timeBoxStacksArr);
		
		TimeBox current = new TimeBox(jsonTimeBoxStart);
		current.setTimeBoxStacks(timeBoxStacks);
		timeBoxes.add(current);
		}
		return timeBoxes;
		}
	private List<Stack> convertJsonToListOfStacks(JSONArray jsonStacksArr) throws JSONException {
		List<Stack> stacks = new ArrayList<Stack>();
		for (Object object : jsonStacksArr) {
			OrderedJSONObject jsonStack = (OrderedJSONObject) object;
			Map<String, Object> stackDetails = new HashMap<String, Object>();
			JSONArray events = jsonStack.getJSONArray("events");
			String eventsValue= events.toString(1);
			for (Object field : stackldsList) {
				String fieldValue = jsonStack.get(field) != null ? jsonStack.get(field).toString() : "";
				
				stackDetails.put(field.toString(), fieldValue);
			}
            Stack current = new Stack(stackDetails);
            
            current.setStackEvents(eventsValue);
			stacks.add(current);

		}
		return stacks;
	}

	public String getOrientMeResponseStr() {
		return orientMeResponseStr;
	}

	

	

	
	
	public List<TimeBox> getTimeBoxes() {
		return timeBoxes;
	}
	
	
	public class TimeBox {
		private String  timeBoxStart;
		private List<Stack> timeBoxStacks;
		TimeBox(String StartString) {
			this.timeBoxStart = StartString;
		}
			public  void setTimeBoxStacks (List<Stack> stacks){
				this.timeBoxStacks = stacks;
		}
			public  List<Stack> getTimeBoxStacks (){
				return this.timeBoxStacks;
		}
			public  String getTimeBoxStart (){
				return this.timeBoxStart;
		}
		
	}
		public class Stack {
			private Map<String, Object> stackDetails;
			private String stackEvents;
			Stack(Map<String, Object> stackDetails) {
				this.stackDetails = stackDetails;

			}
			public  void setStackEvents (String events){
				this.stackEvents = events;
		}
			public  String getStackEvents (){
				return this.stackEvents;
		}
			public String getStackName (){
				return  this.stackDetails.get("stackType").toString();
			}
			@Override
			public String toString() {
				StringBuilder sb = new StringBuilder("Stack - ");
				for (Map.Entry<String, Object> entry : stackDetails.entrySet()) {
					sb.append(entry.getKey()).append(": ").append(entry.getValue())
							.append(", ");
				}
				return sb.toString();
			}
}
	}
