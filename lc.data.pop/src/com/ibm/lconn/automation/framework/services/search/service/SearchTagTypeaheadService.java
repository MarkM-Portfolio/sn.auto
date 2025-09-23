package com.ibm.lconn.automation.framework.services.search.service;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;

import com.ibm.lconn.automation.framework.services.common.LCService;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.common.URLConstants;

public class SearchTagTypeaheadService extends LCService {
	
	private static final String TAG_PREFIX = "tag";

	public SearchTagTypeaheadService(AbderaClient client, ServiceEntry service) {
		super(client, service);
		
		if(service != null)
			this.setFoundService(true);
	}
	

	public ArrayList<String> getPersonalTags(String tagPrefix) {
		getApiLogger().debug("getPersonalTags");
		return getTags(URLConstants.TAGS_TYPEAHEAD_PRIVATE, tagPrefix);
	}
	public ArrayList<String> getPublicTags(String tagPrefix) {
		getApiLogger().debug("getPublicTags");
		return getTags(URLConstants.TAGS_TYPEAHEAD_PUBLIC, tagPrefix);
	}
	
	private ArrayList<String> getTags(String tagsTypeaheadPath, String tagPrefix) {
		getApiLogger().debug("getTags");
		ArrayList<String> results = new ArrayList<String>();
		
		String tagsPath = service.getServiceURLString() + tagsTypeaheadPath +  "?" + TAG_PREFIX + "=" + tagPrefix;
		try {
			ClientResponse response = doSearch(tagsPath);
			int status = response.getStatus();
			if (status != 200){
				getApiLogger().debug("Failed to get tags - status : " + status);
				fail("Failed to get tags - status : " + status);
				return null;
			}
			String responseStr;
		
			responseStr = readResponse(response.getReader());
			getApiLogger().debug(responseStr);
			JSONArray tagResults = new JSONArray(responseStr);
			for (int i=0; i<tagResults.length(); i++){
				String tag = tagResults.getString(i);
				results.add(tag);
			}
			
			return results;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return results;
		
	}
	
}

