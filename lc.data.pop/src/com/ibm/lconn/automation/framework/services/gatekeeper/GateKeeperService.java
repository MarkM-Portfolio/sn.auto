package com.ibm.lconn.automation.framework.services.gatekeeper;

import java.util.HashMap;
import java.util.Map;

import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;

import com.ibm.lconn.automation.framework.services.common.HttpResponse;
import com.ibm.lconn.automation.framework.services.common.LCService;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.common.URLConstants;

/**
 * GateKeeper Service object handles getting/posting data.
 * 
 */
public class GateKeeperService extends LCService {

	/**
	 * Constructor to create a new GateKeeper Service.
	 * 
	 * This object contains helper methods for all API calls that are supported
	 * by the GateKeeper.
	 * 
	 * @param client
	 *   the authenticated AbderaClient that is used to handle requests to/from server
	 * @param service
	 *   the ServiceEntry that contains information about the service
	 *   ( Use Profiles ServiceEntry to invoke GateKeeper )
	 */
	public GateKeeperService(AbderaClient client, ServiceEntry service) {
		this(client, service, new HashMap<String, String>());
	}
	
	public GateKeeperService(AbderaClient client, ServiceEntry service, Map<String, String> headers) {
		super(client, service);
		for(String key : headers.keySet()){			
			this.options.setHeader(key, headers.get(key));			
		}	
	}

	public String getGateKeeperSetting( String id, String name){
		String url;
		if (name != null){
			url = URLConstants.SERVER_URL + "/connections/config/rest/gatekeeper/"+id+"/"+name;
		}else{
			url = URLConstants.SERVER_URL + "/connections/config/rest/gatekeeper/"+id;
		}		
		return getResponseString(url);
	}
	
	public String postGateKeeperSetting( String id, String settings ){
		String url = URLConstants.SERVER_URL + "/connections/config/rest/gatekeeper/"+id;
		return postResponseJSONString(url, settings);
	}
	
}
