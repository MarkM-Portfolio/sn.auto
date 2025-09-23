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
package com.ibm.conn.auto.lcapi;

import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.wink.json4j.OrderedJSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.opensocial.ActivitystreamsService;
import com.ibm.lconn.automation.framework.services.ublogs.nodes.EventEntry;
import com.ibm.lconn.automation.framework.services.ublogs.nodes.JsonEntry;
import com.ibm.conn.auto.lcapi.common.APIHandler;

public class APIActivityStreamsHandler extends APIHandler<ActivitystreamsService>{

	private static final Logger log = LoggerFactory.getLogger(APIActivityStreamsHandler.class);

	public APIActivityStreamsHandler(String serverURL, String username, String password) {

		super("opensocial", serverURL, username, password);
	}

	@Override
	protected ActivitystreamsService getService(AbderaClient abderaClient, ServiceEntry generalService) {

		ActivitystreamsService service = null;
		try {
			service = new ActivitystreamsService(abderaClient, generalService);
		} catch (Exception e) {
			Assert.fail("Unable to create ActivityStreams service:" + e.getMessage());
		}
		return service;
	}

	public EventEntry postActivityStreamEvent(String title, String content, String srcUrl) {

		log.info("Post activitystreams event with target");

		String entry ="{\"generator\": {"
			+"\"id\": \"test id\"," 
			+"\"displayName\": \"test displayName\"," 
			+"\"url\": \""+srcUrl+"\"},"  
			+"\"actor\": {"
			+"\"id\": \"@me\"},"
			+"\"verb\": \"post\"," 
			+"\"title\": \""+ title +"\"," 
			+"\"content\": \""+ content +"\"," 
			+"\"object\": {"
			+"\"summary\": \"test summary\"," 
			+"\"objectType\": \"notea\"," 
			+"\"id\": \"test id\"," 
			+"\"displayName\": \"test displayName\"," 
			+"\"url\": \""+srcUrl+"\"},"
			+"\"target\": {"
			+"\"summary\": \"test summary\"," 
			+"\"objectType\": \"test objectType\"," 
			+"\"id\": \"test id\"," 
			+"\"displayName\": \"test displayName\"," 
			+"\"url\": \""+srcUrl+"\"}," 
			+"\"openSocial\": {"
			+"\"embed\": {"
			+"\"gadget\": \"" + service.getServiceURLString().replace("/connections/opensocial", "/connections/resources/web/com.ibm.social.ee.cloud/cloudee.xml") + "\", "
			+"\"context\": {"
			+"\"url\" : \""+srcUrl+"\"," 
			+"\"debug\" : true	}}}," 
			+"\"connections\": {"
			+"\"rollupid\": \"test rollupid\","
			+"\"broadcast\" : \"true\"}}";

		String post_uri = service.getServiceURLString()+"/basic/rest/activitystreams/@me/@all/@all";
		String JSON = service.createASEntry(post_uri,entry);
		JsonEntry je = new JsonEntry(JSON);
		OrderedJSONObject jsonObj = je.getJsonEntry();
		if(jsonObj.toString().indexOf("resp:error=\"true\">") == -1) {
			log.info("INFO: The Activity Stream event has been posted successfully");
			EventEntry ee = new EventEntry(jsonObj);
			log.info("Activity Stream Event successfully created , Event id is : "+ee.getId());
			return ee;
		} else {
			log.info("ERROR: The Activity Stream event could not be posted");
			log.info(jsonObj.toString());
			return null;
		}
	
	}

}
