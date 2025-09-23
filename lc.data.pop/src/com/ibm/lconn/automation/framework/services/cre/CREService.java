package com.ibm.lconn.automation.framework.services.cre;

import java.util.HashMap;
import java.util.Map;

import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpClient;

import org.apache.wink.json4j.JSONObject;
import org.apache.wink.json4j.JSONException;

import com.ibm.lconn.automation.framework.services.common.HttpResponse;
import com.ibm.lconn.automation.framework.services.common.LCService;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.common.StringConstants;

/**
 * CRE Service object handles getting/posting data to the CRE service.
 * 
 */
public class CREService extends LCService {
	
	protected String _st = null; // Shindig SecurityToken

	/**
	 * Constructor to create a new CRE Service helper object.
	 * 
	 * This object contains helper methods for all API calls that are supported
	 * by the News service.
	 * 
	 * @param client
	 *            the authenticated AbderaClient that is used to handle requests
	 *            to/from server
	 * @param service
	 *            the ServiceEntry that contains information about the CRE
	 *            service
	 */
	public CREService(AbderaClient client, ServiceEntry service) {
		this(client, service, new HashMap<String, String>());
	}
	
	public CREService(AbderaClient client, ServiceEntry service, Map<String, String> headers) {
		super(client, service);
		for(String key : headers.keySet()){			
			this.options.setHeader(key, headers.get(key));			
		}	
	}

	public String getSecurityToken() {
		if (_st == null) {
			// need to establish a security context
			HttpClient http = new HttpClient();
			http.getState().clearCookies();
			Cookie[] cookies = executeLogin(http, StringConstants.USER_NAME, StringConstants.USER_PASSWORD);
			client.addCookies(cookies);

			for (Cookie cookie : cookies) {
				API_LOGGER.debug(cookie.toString());
			}
			final String url = service.getServiceURLString()
					+ "/common/tokenRefresh?type=container&token=";
			addRequestOption("X-Update-Nonce", "check");
			final String response = getResponseString(url);
			removeRequestOption("X-Update-Nonce");
			try {
				JSONObject json = new JSONObject(response);
				// expected response is of the form:
				//   {"widgetMetadata":[],
				//    "containerToken":{"expireTimeMs":1497720297,
				//                      "token":"default:jSDz7nLastbxjrfCeZQ8ZGXW0-EcuVDMCpdQy6xj32wysXXgng5u5U0zJCcjjiSVl0nS9mxE68aKMo6NriePDviXgEaN1s97AnE7M5HZv5Zk9yL1NywwnKM6344NehyQz65oYJX1CVbG_DklRz2qxKOP8N3V_Uy6yEGcltpzHBQ",
				//                      "needsTokenRefresh":true},
				//     "resourceList":[],
				//     "instanceData":[]}
				JSONObject containerToken = json.getJSONObject("containerToken");
				_st = containerToken.getString("token");
			} catch (JSONException je) {
				API_LOGGER.debug("JSONException (" + je.getMessage() + ") caught when parsing \n" + response);
			}	
		}
		
		return _st;
	}

	public String getConnectionsFeatures() {
		final String url = service.getServiceURLString()
				+ "/gadgets/js/cre.iruntime:cre.iwidget.event:cre.wire:cre.iwidget:cre.iwidget.itemset:cre.util.stringify:cre.service.event:cre.osgadget:cre.messages.en:core:container:rpc:pubsub-2:views:embedded-experiences:open-views:selection:actions:viewenhancements:shared-script-frame:cre.service.people:ibm.connections.sharedialog:com.ibm.connections.sharedialog:com.ibm.connections.ee:ibm.connections.ee:container.nongadget:shindig.sha1:open-views.js?c=1";
		return getResponseString(url);
	}

	public String getCREContainerFeature() {
		final String url = service.getServiceURLString()
				+ "/gadgets/js/cre.icontainer.js?debug=1&c=1&container=default";
		return getResponseString(url);
	}
	
	public String getProcessWidgets() {
		String baseURL = service.getServiceURLString();
		final String url = baseURL + "/common/processWidgets?format=json";
		addRequestOption("X-Shindig-ST", getSecurityToken());
		addRequestOption("container", "default");
		int slash = baseURL.lastIndexOf('/');
		if (slash != -1) {
			baseURL = baseURL.substring(0, slash); // drop last word ('opensocial') from URI as it is too far into the server
		}
		
		return postResponseJSONString(url, 
				  "{\"loadedFeatures\":[\"actions\",\"com.ibm.connections.ee\",\"com.ibm.connections.sharedialog\",\"container\",\"container.nongadget\",\"core\",\"cre.iruntime\",\"cre.iwidget\",\"cre.iwidget.event\",\"cre.iwidget.itemset\",\"cre.messages.en\",\"cre.osgadget\",\"cre.service.event\",\"cre.service.people\",\"cre.wire\",\"embedded-experiences\",\"ibm.connections.ee\",\"ibm.connections.sharedialog\",\"open-views\",\"pubsub-2\",\"rpc\",\"selection\",\"shared-script-frame\",\"shindig.sha1\",\"views\"],"
				+  "\"combineResources\":true,\"requireUserProfile\":false,\"metadataTimeout\":1000,"
				+  "\"widgets\":[{\"fields\":{\"instanceData\":true,\"metadata\":true},\"format\":\"json\",\"widget\":{\"componentType\":\"iWidget\",\"placement\":\"b9d\",\"id\":\"b9d\",\"definitionUrl\":\"" + baseURL + "resources/web/lconn.homepage/widgets/activities/activitiesTodoList.xml\"}},"
						     + "{\"fields\":{\"instanceData\":true,\"metadata\":true},\"format\":\"json\",\"widget\":{\"componentType\":\"gadget\",\"placement\":\"b50\",\"id\":\"b50\",\"definitionUrl\":\"" + baseURL + "resources/web/lconn.calendar/CalendarGadget.xml\"}}"
						     + "]}");
	}
}
