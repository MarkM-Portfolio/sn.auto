/**
 * 
 */
package com.ibm.lconn.automation.framework.services.cre;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.fail;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Component;
import com.ibm.lconn.automation.framework.services.common.UserPerspective;
import com.ibm.lconn.automation.framework.services.common.UsersEnvironment;

/**
 * JUnit Tests for CRE and WidgetContainer
 */
public class CREAPIValidationTests {
	private static UserPerspective user;

	private static CREService service;

	protected final static Logger LOGGER = LoggerFactory
			.getLogger(CREAPIValidationTests.class.getName());

	/**
	 * Set Users Test Environment
	 * 
	 * @throws IOException
	 */

	@BeforeClass
	public static void setUp() throws IOException {

		LOGGER.debug("Start Initializing CRE API Verification Test");

		UsersEnvironment userEnv = new UsersEnvironment();
		user = userEnv.getLoginUserEnvironment(StringConstants.CURRENT_USER,
				Component.OPENSOCIAL.toString());
		service = user.getCREService();

		LOGGER.debug("Finished Initializing CRE API Verification Test");
	}

	@Test
	public void validateCRELives() throws Exception {
		LOGGER.debug("Start validateCRELives()");
		// final HttpResponse response1 = service.getConnectionsFeatures();
		// assertEquals(response1.getStatusCode(), 200);
		final String rString1 = service.getConnectionsFeatures();
		assertEquals(service.getRespStatus(), 200);
		assertNotNull(rString1);
		assert (rString1.length() > 326000);
		assert (rString1.length() < 1000000);
		// final HttpResponse response2 =service.getCREContainerFeature();
		// assertEquals(response2.getStatusCode(), 200);
		final String rString2 = service.getCREContainerFeature();
		assertEquals(service.getRespStatus(), 200);
		assertNotNull(rString2);
		int length = rString2.length();
		assert (rString2.length() > 975000);
		assert (rString2.length() < 1500000);
		LOGGER.debug("Finished validateCRELives()");
	}

	@Test
	public void TestCORSprocessing() throws Exception {
		// CORS protection is provided by the CSRFFilter, which is placed in front of all WidgetContainer endpoints (/*)
		// This only tests 3 of those endpoints, but that should suffice for CORS testing
		// Fuller testing of the endpoints happens through the BVT suite, but that is not cross domain
		LOGGER.debug("Start TestCORSprocessing()");
		// stash original header information
		String origin = service.getRequestOption("ORIGIN");
		
		// set headers and do the positive tests
		LOGGER.debug("Start positive CORS testing");
		service.addRequestOption("ORIGIN", "http://fakedomain.com");
		String rString1 = service.getConnectionsFeatures();
		if (service.getRespStatus() != 200) {
			fail("CRE TestCORSprocessing: 200 expected (trusted domain), got " + service.getRespStatus() + " for getConnectionsFeatures()");
		}

		rString1 = service.getCREContainerFeature();
		if (service.getRespStatus() != 200) {
			fail("CRE TestCORSprocessing: 200 expected (trusted domain), got " + service.getRespStatus() + " for getCREContainerFeature()");
		}
		
		rString1 = service.getSecurityToken();
		if (service.getRespStatus() != 200) {
			fail("CRE TestCORSprocessing: 200 expected (trusted domain), got " + service.getRespStatus() + " for getSecurityToken()");
		} else {
			LOGGER.debug("getSecurityToken(): " + rString1);
		}
		
		rString1 = service.getProcessWidgets();
		if (service.getRespStatus() != 200) {
			fail("CRE TestCORSprocessing: 200 expected (trusted domain), got " + service.getRespStatus() + " for getProcessWidgets()");
		} else {
			LOGGER.debug("getProcessWidgets(): " + rString1);
		}

		// set headers and do the negative tests
		LOGGER.debug("Start negative CORS testing");
		service.addRequestOption("ORIGIN", "http://malicious.com");
		
		rString1 = service.getConnectionsFeatures();
		if (service.getRespStatus() != 403) {
			fail("CRE TestCORSprocessing: 403 expected (not a trusted domain), got " + service.getRespStatus() + " for getConnectionsFeatures()");
		}

		rString1 = service.getCREContainerFeature();
		if (service.getRespStatus() != 403) {
			fail("CRE TestCORSprocessing: 403 expected (not a trusted domain), got " + service.getRespStatus() + " for getCREContainerFeature()");
		}
		
		rString1 = service.getSecurityToken();
		if (service.getRespStatus() != 403) {
			fail("CRE TestCORSprocessing: 403 expected (not a trusted domain), got " + service.getRespStatus() + " for getSecurityToken()");
		} else {
			LOGGER.debug("getSecurityToken(): " + rString1);
		}
		
		rString1 = service.getProcessWidgets();
		if (service.getRespStatus() != 403) {
			fail("CRE TestCORSprocessing: 403 expected (not a trusted domain), got " + service.getRespStatus() + " for getProcessWidgets()");
		} else {
			LOGGER.debug("getProcessWidgets(): " + rString1);
		}

		// restore original headers for other tests
		setOrRemoveRequestOption("ORIGIN", origin);
		LOGGER.debug("Finished TestCORSprocessing()");
	}
	
	private void setOrRemoveRequestOption(String optionName, String value) {
		if (value != null && value.length() > 0) {
			LOGGER.debug("setOrRemoveRequestOption: " + optionName + "=" + value + ".");
			service.addRequestOption(optionName, value);
		} else {
			LOGGER.debug("setOrRemoveRequestOption: removing " + optionName);
			service.removeRequestOption(optionName);
		}	
	}
 
	@AfterClass
	public static void tearDown() {
		service.tearDown();
	}
}
