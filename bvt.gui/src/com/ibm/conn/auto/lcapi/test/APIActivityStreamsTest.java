/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2016                                        */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
package com.ibm.conn.auto.lcapi.test;

import org.apache.abdera.Abdera;
import org.apache.abdera.protocol.client.AbderaClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.lcapi.APIActivityStreamsHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.lconn.automation.framework.services.common.ServiceConfig;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.common.Utils;
import com.ibm.lconn.automation.framework.services.ublogs.nodes.EventEntry;

public class APIActivityStreamsTest extends SetUpMethods2{

	private static Logger log = LoggerFactory.getLogger(APIActivityStreamsTest.class);
	private TestConfigCustom cfg;	
	private User testUser;
	private String testURL;
	
	private static Abdera abdera;
	private static AbderaClient client;
	private static ServiceConfig config;
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {

		cfg = TestConfigCustom.getInstance();
		testUser = cfg.getUserAllocator().getUser(this);
		testURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());

		// Initialize Abdera
		abdera = new Abdera();
		client = new AbderaClient(abdera);
		
		// Register SSL / Add credentials for user
		AbderaClient.registerTrustManager();
		
		// Get service config for server, assert that it was retrieved and contains the activities service information
		config = new ServiceConfig(client, testURL, true);
		
		ServiceEntry activityStreams = config.getService("opensocial");
		assert(activityStreams != null);

		Utils.addServiceAdminCredentials(activityStreams, client);
		
				
	}

	@Test (groups = {"apitest"})
	public void postActivityStreamsEvent(){
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		//Instantiate APIHandler
		APIActivityStreamsHandler apiHandler = new APIActivityStreamsHandler(testURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		log.info("INFO: ------------Posting Activity Steam Event---------------------");
		String title = testName + Helper.genStrongRand();
		String content = testName + Helper.genStrongRand();
		EventEntry postedEvent = apiHandler.postActivityStreamEvent(title, content, "https://ibm.com");
		Assert.assertNotNull(postedEvent);
		if(postedEvent!=null)
			Assert.assertNotNull(postedEvent.getId());
	}
}