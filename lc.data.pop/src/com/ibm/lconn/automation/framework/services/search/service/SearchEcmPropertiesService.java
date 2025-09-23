package com.ibm.lconn.automation.framework.services.search.service;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;

import com.ibm.lconn.automation.framework.services.common.LCService;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.search.data.EcmProperty;

public class SearchEcmPropertiesService extends LCService {
	
	private static final String ECM_DOCUMENT_TYPE = "ecmDocumentType";

	public SearchEcmPropertiesService(AbderaClient client, ServiceEntry service) {
		super(client, service);
		
		if(service != null)
			this.setFoundService(true);
	}

	public ArrayList<EcmProperty> getAllEcmProperties(String ecmDocumentTypeId) {
		return getEcmProperties(ecmDocumentTypeId, null);
	}

	private ArrayList<EcmProperty> getEcmProperties(String ecmDocumentTypeId, String ecmPropertyName) {

		getApiLogger().debug("getEcmProperties");
		try {
			ClientResponse response = doSearch(service.getServiceURLString() + URLConstants.ECM_PROPERTIES + "?" + ECM_DOCUMENT_TYPE  + "=" + ecmDocumentTypeId);
			int status = response.getStatus();
			if (status != 200){
				getApiLogger().debug("Failed to get ECM properties - status : " + status);
				fail("Failed to get ECM properties - status : " + status);
				return null;
			}
			ArrayList<EcmProperty> results = new ArrayList<EcmProperty>();
			String responseStr;
		
			responseStr = readResponse(response.getReader());
			getApiLogger().debug(responseStr);
			JSONArray properties = new JSONArray(responseStr);
			for (int i=0; i< properties.length(); i++){
				JSONObject jsonObject = properties.getJSONObject(i);
				EcmProperty currEcmProperty = new EcmProperty(jsonObject);
				if (ecmPropertyName == null || ecmPropertyName.equalsIgnoreCase(currEcmProperty.getName())){
					results.add(currEcmProperty);
				}
			}
			
			return results;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}
	
}

