package com.ibm.lconn.automation.framework.services.switchbox;

import java.util.HashMap;
import java.util.Map;

import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;

import com.ibm.lconn.automation.framework.services.common.HttpResponse;
import com.ibm.lconn.automation.framework.services.common.LCService;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.URLConstants;

/**
 * SwitchBox Service object handles switchbox API.
 * 
 */
public class SwitchBoxService extends LCService {

	/**
	 * Constructor to create a new GateKeeper Service.
	 * 
	 * This object contains helper methods for SwitchBox API calls.
	 * 
	 * @param client
	 *   the authenticated AbderaClient that is used to handle requests to/from server
	 * @param service
	 *   the ServiceEntry that contains information about the service
	 *   ( Use Profiles ServiceEntry to invoke GateKeeper )
	 */
	public SwitchBoxService(AbderaClient client, ServiceEntry service) {
		this(client, service, new HashMap<String, String>());
	}
	
	public SwitchBoxService(AbderaClient client, ServiceEntry service, Map<String, String> headers) {
		super(client, service);
		for(String key : headers.keySet()){			
			this.options.setHeader(key, headers.get(key));			
		}	
	}
	
	public String switchboxBVT(){
		
		// For BVT
		String url = URLConstants.DMGR_URL + "/switchboxtest/bvt?topic="+StringConstants.ORGID+
			"&user="+StringConstants.ADMIN_USER_EMAIL+"&pw="+StringConstants.ADMIN_USER_PASSWORD;
		
		return getResponseString(url);
	}

	/*public String switchboxBVT(){
		
		// For BVT
		String url = URLConstants.DMGR_URL + "/switchboxtest/bvt?mqserver="+StringConstants.MQ_SERVER+
			"&user="+StringConstants.ADMIN_USER_EMAIL+"&pw="+StringConstants.ADMIN_USER_PASSWORD+
			"&topic="+StringConstants.ORGID;
		
		return getResponseString(url);
	}
	public String switchboxUnit(){
		// For unit
		String url = URLConstants.DMGR_URL + "/switchboxtest/bvt?mqserver="+StringConstants.MQ_SERVER+
			"&user="+StringConstants.ADMIN_USER_EMAIL+"&pw="+StringConstants.ADMIN_USER_PASSWORD+
			"&topic="+StringConstants.ORGID+"&downloadserver="+StringConstants.DOWNLOAD_SERVER;
				
		return getResponseString(url);
	}*/
	
}
