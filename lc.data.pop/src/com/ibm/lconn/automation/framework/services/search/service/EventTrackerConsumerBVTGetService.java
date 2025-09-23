package com.ibm.lconn.automation.framework.services.search.service;

import java.io.IOException;
import java.util.logging.Logger;

import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.wink.json4j.JSONException;

import com.ibm.json.java.JSONObject;
import com.ibm.lconn.automation.framework.services.common.LCService;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;

public class EventTrackerConsumerBVTGetService extends LCService {
	
	public static final String EVENT_TRACKER_CONSUMER_POST_CONTEXT_PATH = "/eventTrackerConsumerBVTServlet/?query=";
	protected final static Logger LOGGER = Logger.getLogger(EventTrackerConsumerBVTGetService.class.getName());
	
	public EventTrackerConsumerBVTGetService(AbderaClient client, ServiceEntry service) {
		super(client, service);
	}
	
//	public JSONObject sendGet() throws IOException, JSONException{
//		JSONObject response = new JSONObject();
//		StringBuilder urlBuilder = new StringBuilder();
//		urlBuilder.append(service.getServiceURLString());
//		urlBuilder.append(EVENT_TRACKER_CONSUMER_POST_CONTEXT_PATH);
//		String getUrl = urlBuilder.toString();
//		LOGGER.debug("Send get request:  " + getUrl);
//		ClientResponse cr = client.get(getUrl);
//		if(cr.getStatus() == 200){
//			String responseStr = readResponse(cr.getReader());
//			getApiLogger().debug(responseStr);
//			response = JSONObject.parse(responseStr);
//		}
//		return response;
//
//	}
	
	public ClientResponse sendGet() throws IOException, JSONException{
		StringBuilder urlBuilder = new StringBuilder();
		urlBuilder.append(service.getServiceURLString());
		urlBuilder.append(EVENT_TRACKER_CONSUMER_POST_CONTEXT_PATH);
		String getUrl = urlBuilder.toString();
		LOGGER.fine("Send get request:  " + getUrl);
		ClientResponse cr = client.get(getUrl);
		return cr;

	}
	
	public JSONObject readResponse(ClientResponse clientResponse) throws IOException, JSONException{
		JSONObject response = new JSONObject();
		String responseStr = readResponse(clientResponse.getReader());
		getApiLogger().debug(responseStr);
		response = JSONObject.parse(responseStr);
		return response;
	}
	
	
}

