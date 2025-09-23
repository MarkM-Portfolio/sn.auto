package com.ibm.lconn.automation.framework.services.metrics;

import static org.testng.AssertJUnit.assertEquals;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.URLEncoder;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.abdera.Abdera;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;

import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;

import com.ibm.lconn.automation.framework.services.common.LCService;
import com.ibm.lconn.automation.framework.services.common.ProfileData;
import com.ibm.lconn.automation.framework.services.common.ServiceConfig;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.DeploymentType;

public class MetricsHttpClient {

	private ProfileData _profile;
	private final static Logger LOGGER = Logger.getLogger(MetricsHttpClient.class.getName());
	private AbderaClient _client;

	MetricsHttpClient(ProfileData profile) {
		this(profile, null);
	}
	
	MetricsHttpClient(ProfileData profile, LCService lcService) {
		_profile = profile;
		if(lcService != null && lcService.getAbderaClient() != null){
			_client = lcService.getAbderaClient();
		}else{
			setupClient();
		}
	}
	

	/**
	 * Trying to use a common well defined logical to construct AbderaClient
	 */
	private void setupClient() {
		try {
			Abdera abdera = new Abdera();
			_client = new AbderaClient(abdera);
			AbderaClient.registerTrustManager();
			new ServiceConfig(_client, URLConstants.SERVER_URL, true, _profile.getEmail(), _profile.getPassword());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getMetricsServiceContext(){
		String context = URLConstants.METRICS_ONPRIMISE_METRICS_SERVICE;
		if(StringConstants.DEPLOYMENT_TYPE != DeploymentType.ON_PREMISE) {
			context = URLConstants.METRICS_SC_METRICS_SERVICE;
		}
		return context;
	}
	
	private String getQeuryServiceContext(){
		String context = URLConstants.METRICS_ONPREMISE_QUERY_SERVICE;
		if(StringConstants.DEPLOYMENT_TYPE != DeploymentType.ON_PREMISE) {
			context = URLConstants.METRICS_SC_QUERY_SERVICE;
		}
		return context;
	}
	
	public String getQueryService(HashMap<String, String> map, int expectedStatus) throws Exception {

		LOGGER.fine("Begin to send the get request ");
		String url = URLConstants.SERVER_URL + getQeuryServiceContext();
		String params = "";
		if (!map.isEmpty())
			params = "?";
		for (Map.Entry<String, String> entry : map.entrySet()) {
			params += URLEncoder.encode(entry.getKey(), "UTF-8") + "=" + URLEncoder.encode(entry.getValue(), "UTF-8")
					+ "&";
		}
		url += params;
		LOGGER.fine("get : " + url);
		ClientResponse cr = _client.get(url);
		int status = cr.getStatus();
		LOGGER.fine("get status:" + status);
		assertEquals(expectedStatus, status);
		InputStream instream = cr.getInputStream();
		StringBuffer jsonStr = new StringBuffer();
		try {

			BufferedReader reader = new BufferedReader(new InputStreamReader(instream));

			String line = reader.readLine();
			jsonStr.append(line);

		} finally {
			instream.close();
		}
		return jsonStr.toString();
	}
	
	public String postQueryService(Map<String, String> map, int expectedStatus) throws Exception {

		LOGGER.fine("Begin to send the post query service request ");
		String url = URLConstants.SERVER_URL + getQeuryServiceContext();
		
		if(map.get("communityUuid") != null){
			url += "?communityUuid=" + map.get("communityUuid");
		}

		InputStream out = null;
		StringBuffer jsonStr = new StringBuffer();
		try {
			StringBuffer param = new StringBuffer("");
			if (map != null && map.size() > 0) {
				for (String key : map.keySet()) {
					param.append(key).append("=" + map.get(key)).append("&");
				}
			}

			RequestEntity requestEntry = new StringRequestEntity(param.toString(), "application/x-www-form-urlencoded",
					"UTF-8");
			ClientResponse rest = _client.post(url, requestEntry);
			int status = rest.getStatus();
			assertEquals(expectedStatus, status);
			out = rest.getInputStream();
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(out));
				String line = reader.readLine();
				jsonStr.append(line);
			} finally {
				out.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return jsonStr.toString();
	}


	public String postMetricsService(String uri, Map<String, String> map, int expectedStatus) throws Exception {

		LOGGER.fine("Begin to send the post request ");
		String url = URLConstants.SERVER_URL + getMetricsServiceContext() + uri;

		InputStream out = null;
		StringBuffer jsonStr = new StringBuffer();
		try {
			StringBuffer param = new StringBuffer("");
			if (map != null && map.size() > 0) {
				for (String key : map.keySet()) {
					param.append(key).append("=" + map.get(key)).append("&");
				}
			}

			RequestEntity requestEntry = new StringRequestEntity(param.toString(), "application/x-www-form-urlencoded",
					"UTF-8");
			ClientResponse rest = _client.post(url, requestEntry);
			int status = rest.getStatus();
			assertEquals(expectedStatus, status);
			out = rest.getInputStream();
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(out));
				String line = reader.readLine();
				jsonStr.append(line);
			} finally {
				out.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return jsonStr.toString();
	}

}
