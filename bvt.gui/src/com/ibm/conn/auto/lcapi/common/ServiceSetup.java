package com.ibm.conn.auto.lcapi.common;

import org.apache.abdera.Abdera;
import org.apache.abdera.protocol.client.AbderaClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.lconn.automation.framework.services.common.ServiceConfig;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.DeploymentType;
import com.ibm.lconn.automation.framework.services.common.URLConstants;

public class ServiceSetup {

	private static final Logger log = LoggerFactory.getLogger(ServiceSetup.class);

	private AbderaClient client;
	private ServiceConfig config;
	public static DeploymentType DEPLOYMENT_TYPE = DeploymentType.ON_PREMISE;
	public static String AUTHENTICATION = "basic";
	protected TestConfigCustom cfg  = TestConfigCustom.getInstance();
	
	public ServiceEntry getGeneralService(String product, String serviceName, String serverURL, String username, String password, boolean useSSL) throws Exception {

		log.info("Start Initializing General Data Population Service");

		// Initialize Abdera
		this.client = initAbdera();

		if(product.toLowerCase().equals("cloud")){
			DEPLOYMENT_TYPE=DeploymentType.SMARTCLOUD;
			AUTHENTICATION = "form";
		} else if(product.toLowerCase().equals("onprem")) {
			DEPLOYMENT_TYPE=DeploymentType.ON_PREMISE;
			AUTHENTICATION = "basic";
		} else if(product.toLowerCase().equals("production")) {

		} else if(product.toLowerCase().equals("multi")) {
			DEPLOYMENT_TYPE=DeploymentType.MULTI_TENANT;
			AUTHENTICATION = "basic";
		} else if(product.toLowerCase().equals("vmodel")) {

		} else {
			throw new RuntimeException("Unknown product name: " + product);
		}
		
		// Get service config for server, assert that it was retrieved and contains the activities service information
		this.config = new ServiceConfig(client, serverURL, useSSL, username, password);

		ServiceEntry generalService = config.getService(serviceName);

		APIUtils.addServiceCredentials(generalService, client, username, password);

		log.info("Finished Initializing General Data Population Service");

		return generalService;
	}

	public ServiceEntry getGeneralService(String serviceName, String serverURL, String username, String password, boolean useSSL) throws Exception {

		log.info("Start Initializing General Data Population Service");
		URLConstants.setServerURL(serverURL);	
		StringConstants.setAuthentication(cfg.getTestConfig().AuthenticationType());
		
		// Initialize Abdera
		Abdera abdera = new Abdera();
		this.client = new AbderaClient(abdera);
		log.info("AbderaClient : "+this.client);
		
		// Get service config for server, assert that it was retrieved and contains the activities service information
		this.config = new ServiceConfig(client, serverURL, useSSL, username, password);

		ServiceEntry generalService = config.getService(serviceName);

		APIUtils.addServiceCredentials(generalService, client, username, password);

		log.info("Finished Initializing General Data Population Service");

		return generalService;
	}
	
	
	public AbderaClient getClient() {

		return this.client;
	}

	public ServiceConfig getServiceConfig() {

		return this.config;
	}
	
	public DeploymentType getDeploymentType(){
		return DEPLOYMENT_TYPE;
	}
	
	public void setDeploymentType(DeploymentType deployment_type){
			DEPLOYMENT_TYPE=deployment_type;
	}
	
	public String getAuthentication(){
		return AUTHENTICATION;
	}
	
	public void setAuthentication(String authentication){
		AUTHENTICATION = authentication;
	}
	
	
	public AbderaClient initAbdera(){
		return new AbderaClient();
	}
	
	
}
