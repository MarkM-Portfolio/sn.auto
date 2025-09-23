/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2014                                          */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
package com.ibm.lconn.automation.framework.services.opensocial;

import static org.testng.AssertJUnit.fail;

import java.io.IOException;

import org.apache.abdera.protocol.Response.ResponseType;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.wink.json4j.OrderedJSONObject;

import com.ibm.lconn.automation.framework.services.common.LCService;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.ublogs.nodes.EventEntry;
import com.ibm.lconn.automation.framework.services.ublogs.nodes.JsonEntry;

/**
 * Profiles Service object handles getting/posting data to the Connections CustomList service.
 * 
 * @author Raza Naqui
 * @version 5.0
 */
public class ASCustomListService extends LCService {

	public ASCustomListService(final AbderaClient client, final ServiceEntry service) {
		super(client, service);
	}

	/**
	 * Used for making REST API calls to create CustomList and return the status code
	 * 
	 * @param url
	 * @param content
	 * @return
	 */
	public String createCustomList(ASCustomListServiceRequest request) {
		final String url = request.getUrl();
		final String content = request.getContent();
		options.setContentType("application/json");
		ClientResponse clientResponse = postResponse(url, content);
		String responseString = null;
		String id = null;
		if (clientResponse != null) {
			if (clientResponse.getType() == ResponseType.SUCCESS) {
				try {
					responseString = readResponse(clientResponse.getReader());
					if (responseString != null) {
						JsonEntry ub = new JsonEntry(responseString);
						OrderedJSONObject jsonEntry = ub.getJsonEntry();
						EventEntry ee = new EventEntry(jsonEntry);
						id = ee.getId();
					}
				} catch (IOException e) {
					fail(e.getLocalizedMessage());
				}
			}
		}
		return id;
	}

	/**
	 * Used for making REST API calls to create CustomListItem and return the status code. This method uses createCustomList method.
	 * 
	 * @param url
	 * @param content
	 * @return statusCode
	 */
	public String createCustomListItem(ASCustomListServiceRequest request) {
		return createCustomList(request);
	}
}