package com.ibm.lconn.automation.framework.services.search.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.commons.httpclient.methods.RequestEntity;

import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.search.request.quickresults.QuickResultsPostRequest;

public class QuickResultsPostService extends SearchService {

	public static String QUICK_RESULTS_POST_CONTEXT_PATH = "/eventTracker";
	public static String URL_PARAM_SEPARATOR = "?";

	protected final static Logger LOGGER = Logger
			.getLogger(QuickResultsPostService.class.getName());

	public QuickResultsPostService(AbderaClient client,
			ServiceEntry searchServiceEntry) {
		super(client, searchServiceEntry);
		addRequestOption("X-Update-Nonce", "true");
	}

	public ClientResponse postQuickResultsEvent(QuickResultsPostRequest postRequest){
		StringBuilder urlBuilder = new StringBuilder();
		urlBuilder.append(service.getServiceURLString());
		urlBuilder.append(QUICK_RESULTS_POST_CONTEXT_PATH);
		urlBuilder.append(URL_PARAM_SEPARATOR);		
		addParamsStringFromRequest(urlBuilder, postRequest);
	
		String url = urlBuilder.toString();
		LOGGER.fine("Send post request:  " + url);
		ClientResponse cr = client.post(url, (RequestEntity) null, this.options);
		return cr;

	}
	
	private void addParamsStringFromRequest(StringBuilder urlBuilder, QuickResultsPostRequest postRequest) {
		boolean isFirst = true;
		Map<String, String> params = postRequest.getParams();
		for (String paramName : params.keySet()){
			String paramValue = params.get(paramName);
			if (isFirst){
				isFirst = false;
			}else{
				urlBuilder.append("&");
			}
			urlBuilder.append(paramName);
			urlBuilder.append("=");
			try {
				urlBuilder.append(URLEncoder.encode(paramValue, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				LOGGER.severe("Exception while encoding paramValue: " + paramValue + " for paramName: " + paramName + "; url: " + urlBuilder.toString());
			}
		}
	}

}
