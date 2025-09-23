package com.ibm.lconn.automation.framework.services.authconnector;

import java.io.IOException;
import java.io.BufferedReader;

import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.fail;
import static org.junit.Assume.assumeTrue;

import org.apache.abdera.Abdera;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Component;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.common.UserPerspective;
import com.ibm.lconn.automation.framework.services.common.UsersEnvironment;

public class AuthConnector {

	private static AbderaClient client;
	static UserPerspective user;
	private static AuthConnectorService service;

	@BeforeClass
	public static void setUp() throws Exception {
		// create local client instance
		Abdera abdera = new Abdera();
		client = new AbderaClient(abdera);
		
		// get service instance
		UsersEnvironment userEnv = new UsersEnvironment();
		user = userEnv.getLoginUserEnvironment(StringConstants.ADMIN_USER,
				Component.AUTHCONNECTOR.toString());
		service = user.getAuthConnectorService();
	}

	@Test
	public void testTrue() {
		assertTrue(true);
	}
	
	@Test
	public void testAuth() {
		// this is an on premise test
		assumeTrue(StringConstants.DEPLOYMENT_TYPE == StringConstants.DeploymentType.ON_PREMISE);
		
		String expected = "Hello authConnector!";
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(service.getAuth().getReader());
		} catch (IOException e) {
			fail(e.getMessage());
		}
		String response = service.readResponse(reader);
		assertTrue(response.contains(expected));
	}
}