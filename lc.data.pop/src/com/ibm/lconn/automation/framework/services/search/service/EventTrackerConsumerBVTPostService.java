package com.ibm.lconn.automation.framework.services.search.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.commons.httpclient.methods.RequestEntity;

import com.ibm.lconn.automation.framework.services.common.LCService;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.search.request.EventTrackerConsumerPostRequest;

public class EventTrackerConsumerBVTPostService extends LCService {
	
	public static final String EVENT_TRACKER_CONTEXT_PATH = "/eventTracker";
	public static final String URL_PARAM_SEPARATOR = "?";
	protected final static Logger LOGGER = Logger.getLogger(EventTrackerConsumerBVTPostService.class.getName());
	
	public EventTrackerConsumerBVTPostService(AbderaClient client, ServiceEntry service) {
		super(client, service);
		addRequestOption("X-Update-Nonce", "true");
	}
	
	public ClientResponse sendPost(EventTrackerConsumerPostRequest request) throws UnsupportedEncodingException{
		StringBuilder urlBuilder = new StringBuilder();
		urlBuilder.append(service.getServiceURLString());
		urlBuilder.append(EVENT_TRACKER_CONTEXT_PATH);
		urlBuilder.append(URL_PARAM_SEPARATOR);		
		addParamsStringFromRequest(urlBuilder, request);
		String url = urlBuilder.toString();
		LOGGER.fine("Send post request:  " + url);
		ClientResponse cr = client.post(url, (RequestEntity) null, this.options);
		
		return cr;
	}
	
	private void addParamsStringFromRequest(StringBuilder urlBuilder, EventTrackerConsumerPostRequest request) throws UnsupportedEncodingException {
		boolean isFirst = true;
		Map<String, String> params = request.getParams();
		for (String paramName : params.keySet()){
			String paramValue = params.get(paramName);
			if (isFirst){
				isFirst = false;
			}else{
				urlBuilder.append("&");
			}
			urlBuilder.append(paramName);
			urlBuilder.append("=");
			urlBuilder.append(URLEncoder.encode(paramValue, "UTF-8"));
		}
	}
	
	
}

