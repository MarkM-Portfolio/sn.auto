package com.ibm.lconn.automation.framework.services.search.service;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.OrderedJSONObject;

import com.ibm.lconn.automation.framework.services.common.LCService;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.search.data.EcmDocumentType;

public class SearchEcmDocumentTypeService extends LCService {
	
	public SearchEcmDocumentTypeService(AbderaClient client, ServiceEntry service) {
		super(client, service);
		
		if(service != null)
			this.setFoundService(true);
	}

	public ArrayList<EcmDocumentType> getAllEcmDocumentTypes() {
		return getEcmDocumentTypes(null);
	}

	private ArrayList<EcmDocumentType> getEcmDocumentTypes(String documentTypeId) {

		getApiLogger().debug("getEcmDocumentTypes");
		try {
			ClientResponse response = doSearch(service.getServiceURLString() + URLConstants.ECM_DOCUMENT_TYPE);
			int status = response.getStatus();
			if (status != 200){
				getApiLogger().debug("Failed to get ECM document types - status : " + status);
				fail("Failed to get ECM document types - status : " + status);
				return null;
			}
			ArrayList<EcmDocumentType> results = new ArrayList<EcmDocumentType>();
			String responseStr;
		
			responseStr = readResponse(response.getReader());
			getApiLogger().debug(responseStr);
			OrderedJSONObject jsonResponse = new OrderedJSONObject(responseStr);
			for (Object keyObject : jsonResponse.keySet()){
				String key = (String)keyObject;
				EcmDocumentType currEcmDocumentType = new EcmDocumentType(key,jsonResponse.getString(key));
				if (documentTypeId == null || documentTypeId.equalsIgnoreCase(currEcmDocumentType.getId())){
					results.add(currEcmDocumentType);
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

