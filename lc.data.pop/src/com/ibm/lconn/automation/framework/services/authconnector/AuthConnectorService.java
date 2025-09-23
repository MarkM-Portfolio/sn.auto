package com.ibm.lconn.automation.framework.services.authconnector;

import java.util.HashMap;
import java.util.Map;

import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;

import com.ibm.lconn.automation.framework.services.common.LCService;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.common.URLConstants;

/**
 * AuthConnector Service object handles getting/posting data.
 * 
 */
public class AuthConnectorService extends LCService {

	/**
	 * Constructor to create a new AuthConnector Service.
	 * 
	 * This object contains helper methods for all API calls that are supported
	 * by the GateKeeper.
	 * 
	 * @param client
	 *   the authenticated AbderaClient that is used to handle requests to/from server
	 * @param service
	 *   the ServiceEntry that contains information about the service
	 *   ( Use Profiles ServiceEntry to invoke AuthConnector )
	 */
	public AuthConnectorService(AbderaClient client, ServiceEntry service) {
		this(client, service, new HashMap<String, String>());
	}
	
	public AuthConnectorService(AbderaClient client, ServiceEntry service, Map<String, String> headers) {
		super(client, service);
		for(String key : headers.keySet()){			
			this.options.setHeader(key, headers.get(key));			
		}	
	}
	
	public ClientResponse getAuth() {
		return client.get(URLConstants.SERVER_URL + "/connections/auth");
	}
	
	public ClientResponse getCert() {
		return client.get(URLConstants.SERVER_URL + "/connections/cert");
	}
}