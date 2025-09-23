package com.ibm.conn.auto.lcapi.common;

import org.apache.abdera.protocol.client.AbderaClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.lconn.automation.framework.services.common.LCService;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;

public abstract class APIHandler<T extends LCService> {
	
	private static final Logger log = LoggerFactory.getLogger(APIHandler.class);
	protected String serviceType = "";
	protected T service;
	private boolean useSSL = true;
	public ServiceSetup setup = null;
	
	protected APIHandler(String component, String serverURL, String username, String password) {
		int count = 0;
		int maxTries = 3;
		
		while (true) {
			try {
				log.info("INFO: Test " + component + " user: " + username);
				this.service = createService(component, serverURL, username, password);
				break;
			} catch (Exception | AssertionError ae) {
				// retry 3 times
				if (++count >= maxTries) {
					log.error("Attempt " + count + ": Error creating " + component + " api service for " + username + ":\n" + ae.getMessage());
					throw new RuntimeException("Error creating " + component + " api service: " + ae.getMessage(), ae);						
				} else {
					log.warn("Attempt " + count + ": Error creating " + component + " api service for " + username + ":\n" + ae.getMessage());
				}
			}
		}
	}
	
	private T createService(String component, String serverURL, String username, String password) throws Exception {

		log.info("API: Start Initializing " + component + " Data Population");

		setup = new ServiceSetup();
		ServiceEntry generalService = setup.getGeneralService(component, serverURL, username, password, useSSL);

		assert (generalService != null) : "Failure creating ServiceEntry for " + component + ".";

		// Retrieve the service document and assert that it exists
		T service = getService(setup.getClient(), generalService);

		log.info("API: Finished Initializing " + component + " Data Population");

		return service;
	}
	
	protected abstract T getService(AbderaClient abderaClient, ServiceEntry generalService);
	
	public T getService() { return service; }

}
