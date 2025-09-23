package com.ibm.conn.auto.util.bss;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BssAPIHelper {
	protected static Logger log = LoggerFactory.getLogger(BssAPIHelper.class);
	private static String OrgJsonFile = "resources/BssJsonFiles/addOrg.json";
	private static String SubJsonFile = "resources/BssJsonFiles/addsub.json";
	private static String UserJsonFile = "resources/BssJsonFiles/adduser.json";
	private static String[] OrgPlaceHolders = { "orgnameplaceholder",
			"email_placeholder" };
	private static String[] SubPlaceHolders = { "partplaceholder",
			"customeridholder", "quantity" };
	private static String[] UserPlaceHolders = { "orgnameplaceholder",
			"email_placeholder", "first_name_placeholder",
			"last_name_placeholder" };

	private static void createTrustMgr() {
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(X509Certificate[] certs,
					String authType) {
			}

			public void checkServerTrusted(X509Certificate[] certs,
					String authType) {
			}
		} };

		// Install the all-trusting trust manager
		try {
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, trustAllCerts, new SecureRandom());
			HttpsURLConnection
					.setDefaultSSLSocketFactory(sc.getSocketFactory());
			SSLContext.setDefault(sc);
		} catch (Exception e) {
			;
		}
	}

	public static String prepareJsonString(String jsonFile,
			String[] placeHolders, String[] replacementVals) throws Exception {
		StringBuilder text = new StringBuilder();
		BufferedReader br = null;
		String jsonStr;

		try {
			br = new BufferedReader(new FileReader(jsonFile));
			String line;

			while ((line = br.readLine()) != null) {
				text.append(line.trim());
			}
		} catch (IOException e) {
			log.error("ERROR: Unable to open JSON File");
			throw e;
		} finally {
			try {
				br.close();
			} catch (Exception e) {
				log.error("ERROR: Unable close BufferedReader");
				throw e;
			}
		}

		jsonStr = text.toString();
		for (int i = 0; i < placeHolders.length; i++) {
			jsonStr = jsonStr.replace(placeHolders[i], replacementVals[i]);
		}
		return jsonStr;
	}

	public static PostMethod preparePostMethod(String serverAddress,
			String urlTail, Credentials _credentials) {

		PostMethod method = new PostMethod(serverAddress + urlTail);
		HttpClient _http = new org.apache.commons.httpclient.HttpClient();
		AuthScope _aScope = new AuthScope(serverAddress, AuthScope.ANY_PORT,
				AuthScope.ANY_REALM, "ANY");

		_http.getState().setCredentials(_aScope, _credentials);
		String auth = _http.getState().getCredentials(_aScope).toString();
		String auth_encoded = new String(Base64.encodeBase64(auth.getBytes()));

		method.setFollowRedirects(false);
		method.setRequestHeader("Content-Type", "application/json");
		method.setRequestHeader("Authorization", "Basic " + auth_encoded);
		method.setRequestHeader("charset", "utf-8");

		return method;
	}

	public static void executePostMethod(PostMethod method, HttpClient _http)
			throws Exception {
		try {
			_http.executeMethod(method);
		} catch (HttpException e1) {
			log.error("API call was unsuccessful" + e1.getMessage());
			throw e1;
		}
	}

	public static String addOrg(String orgName, String email,
			String serverAddress, String csguser, String csgpassword)
			throws Exception {
		String custId = null;
		Credentials _credentials = new UsernamePasswordCredentials(csguser,
				csgpassword);
		PostMethod method = preparePostMethod(serverAddress,
				"/api/bss/resource/customer", _credentials);
		AuthScope _aScope = new AuthScope(serverAddress, AuthScope.ANY_PORT,
				AuthScope.ANY_REALM, "ANY");
		HttpClient _http = new org.apache.commons.httpclient.HttpClient();
		_http.getState().setCredentials(_aScope, _credentials);

		log.info("INFO: Adding org named " + orgName + " to " + serverAddress);

		String jsonStr = prepareJsonString(OrgJsonFile, OrgPlaceHolders,
				new String[] { orgName, email });

		createTrustMgr();
		method.setRequestBody(jsonStr);

		executePostMethod(method, _http);

		while (method.getStatusCode() == 302) {
			method.releaseConnection();
			method = preparePostMethod(serverAddress,
					"/api/bss/resource/customer", _credentials);
			executePostMethod(method, _http);
		}

		int responseCode = method.getStatusCode();
		String response = method.getResponseBodyAsString();
		method.releaseConnection();

		if (responseCode == 400) {
			log.error("ERROR: Received response code 400. Response: "
					+ response);
			throw new Exception("Error creating the org " + orgName
					+ ", received response code 400. Response: " + response);
		}
		if (responseCode == 200) {
			log.info("INFO: Response from creating org: " + response);
			int start = response.indexOf("Long") + 6;
			int end = start + 8;
			custId = response.substring(start, end);
			log.info("INFO: Customer ID is " + custId);
		}

		if (custId.isEmpty()) {
			log.error("ERROR: No customer ID returned - adding Org failed");
			throw new Exception("No customer ID returned - adding Org failed");
		}
		return custId;
	}

	public static String addPart(String partNum, String quantity,
			String customerID, String serverAddress, String csguser,
			String csgpassword) throws Exception {
		HttpClient _http = new org.apache.commons.httpclient.HttpClient();
		AuthScope _aScope = new AuthScope(serverAddress, AuthScope.ANY_PORT,
				AuthScope.ANY_REALM, "ANY");
		Credentials _credentials = new UsernamePasswordCredentials(csguser,
				csgpassword);
		;

		createTrustMgr();

		String jsonStr = prepareJsonString(SubJsonFile, SubPlaceHolders,
				new String[] { partNum, customerID, quantity });
		_http.getState().setCredentials(_aScope, _credentials);
		PostMethod method = preparePostMethod(serverAddress,
				"/api/bss/resource/subscription", _credentials);
		method.setRequestBody(jsonStr);

		// Execute
		executePostMethod(method, _http);

		while (method.getStatusCode() == 302) {
			method.releaseConnection();
			method = preparePostMethod(serverAddress,
					"/api/bss/resource/subscription", _credentials);
			executePostMethod(method, _http);
		}
		int code = method.getStatusCode();
		String response = method.getResponseBodyAsString();
		method.releaseConnection();

		if (code != 200) {
			throw new Exception("ERROR: Add subscription failure. Non 200 ("
					+ code + ") code returned. Response: " + response.trim());
		}

		String[] temp = response.split("\\[");
		String[] temp1 = temp[1].split(",");
		String[] temp2 = temp1[0].split(":");
		log.info("INFO: Subscription ID =" + temp2[1].trim());
		return temp2[1].trim();

	}

	public void activateAccount(String email) throws Exception {
		String response = "";
		int code = 0;
		HttpClient _http = new org.apache.commons.httpclient.HttpClient();
		email = email.toLowerCase();
		// Create a trust manager that does not validate certificate chains
		createTrustMgr();

		String twilString = "http://llnsvtsmtp.swg.usma.ibm.com/getTokenBlueboxPassPassword.php?mail="
				+ email + "&pass=passw0rd";
		GetMethod method = new GetMethod(twilString);
		method.setFollowRedirects(false);
		method.setRequestHeader("charset", "utf-8");

		// Execute
		try {
			_http.executeMethod(method);
		} catch (Exception e) {
			throw e;
		}

		try {
			code = method.getStatusCode();
			response = method.getResponseBodyAsString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		method.releaseConnection();

		if (response.contains(" has not received the email yet!")) {
			// TODO Create a more efficient loop for polling
			log.info("INFO: The email has not arrived yet, waiting a minute before checking again.");
			Thread.sleep(180000);
			method = new GetMethod(twilString);
			method.setFollowRedirects(false);
			method.setRequestHeader("charset", "utf-8");

			// Try checking the if the email has received again
			try {
				_http.executeMethod(method);
				code = method.getStatusCode();
				response = method.getResponseBodyAsString();
			} catch (Exception e) {
				throw e;
			}
			method.releaseConnection();
		}

		if (response.contains(" has not received the email yet!")) {
			throw (new Exception("ERROR: " + email
					+ " has not recieved the email - Provisioning failed"));
		}

		if (code == 200) {
			String shortResponse = response.substring(response
					.indexOf("User user"));
			log.info("INFO: Account activated!: " + shortResponse.trim() + "\n");
		} else
			log.warn("WARN: Non 200 resoponse code returned: " + code + "\n"
					+ response);
	}

	public String addUser(String orgId, String email, String serverAddress,
			String csguser, String csgpassword, String firstname,
			String lastname) throws Exception {

		log.info("INFO: Adding user " + email + " to " + serverAddress);

		HttpClient _http = new org.apache.commons.httpclient.HttpClient();
		AuthScope _aScope;
		Credentials _credentials;
		String jsonStr, auth, auth_encoded;
		jsonStr = prepareJsonString(UserJsonFile, UserPlaceHolders,
				new String[] { orgId, email, firstname, lastname });

		log.info("INFO: JSON string for request: " + jsonStr);

		// Create a trust manager that does not validate certificate chains
		createTrustMgr();

		_aScope = new AuthScope(serverAddress, AuthScope.ANY_PORT,
				AuthScope.ANY_REALM, "ANY");
		_credentials = new UsernamePasswordCredentials(csguser, csgpassword);

		_http.getState().setCredentials(_aScope, _credentials);
		auth = _http.getState().getCredentials(_aScope).toString();
		auth_encoded = new String(Base64.encodeBase64(auth.getBytes()));

		PostMethod method = new PostMethod(serverAddress
				+ "/api/bss/resource/subscriber");
		method.setFollowRedirects(false);
		method.setRequestBody(jsonStr);
		method.setRequestHeader("Content-Type", "application/json");
		method.setRequestHeader("Authorization", "Basic " + auth_encoded);
		method.setRequestHeader("charset", "utf-8");
		_http.executeMethod(method);

		int responseCode = method.getStatusCode();
		String responseBody = method.getResponseBodyAsString();

		if (responseCode == 302) {
			log.warn("WARN: First attempt at provisioning " + email
					+ " failed (" + responseCode + ": " + responseBody.trim()
					+ ") retrying request.");

			method.releaseConnection();
			method = new PostMethod(serverAddress
					+ "/api/bss/resource/subscriber");
			method.setFollowRedirects(false);
			method.setRequestBody(jsonStr);
			method.setRequestHeader("Content-Type", "application/json");
			method.setRequestHeader("Authorization", "Basic " + auth_encoded);
			method.setRequestHeader("charset", "utf-8");

			// Execute POST again.
			try {
				_http.executeMethod(method);
			} catch (Exception e) {
				log.error("ERROR: unable to retry provisioning request: " + e.getMessage());
				throw e;
			}

			responseCode = method.getStatusCode();
			responseBody = method.getResponseBodyAsString();
			
		}
		
		method.releaseConnection();
		
		if (responseCode != 201) {
			Exception e = new Exception("ERROR: Unable to add user "
					+ email + ". Response: " + responseCode + ", "
					+ responseBody.trim());
			log.error(e.getMessage());
			throw e;
		}

		int start = responseBody.indexOf("Long") + 6;
		int end = start + 8;
		String custId = responseBody.substring(start, end);
		log.info("INFO: AddUser successful. Response: " + responseBody.trim()
				+ ". Customer Subscription ID: " + custId);

		return custId;
	}

	/**
	 * 
	 * Add a subscription to a users account using the BSS REST API
	 * 
	 * @param subscriberId
	 *            - the id of the user
	 * @param subscriptionId
	 *            - the subscription id for the part number
	 * @param server
	 *            - deployment name (use https)
	 * @param csguser
	 *            - CSG email
	 * @param csgpassword
	 *            - CSG password
	 * @throws Exception
	 */
	public void addSubscriberSub(String subscriberId, String subscriptionId,
			String server, String csguser, String csgpassword) throws Exception {

		HttpClient _http = new org.apache.commons.httpclient.HttpClient();

		if (subscriptionId.isEmpty())
			throw new Exception("ERROR: No subscription id was provided");

		AuthScope _aScope;
		Credentials _credentials;

		// Create a trust manager that does not validate certificate chains
		createTrustMgr();
		_aScope = new AuthScope(server, AuthScope.ANY_PORT,
				AuthScope.ANY_REALM, "ANY");
		_credentials = new UsernamePasswordCredentials(csguser, csgpassword);
		_http.getState().setCredentials(_aScope, _credentials);
		String auth = _http.getState().getCredentials(_aScope).toString();
		String auth_encoded = new String(Base64.encodeBase64(auth.getBytes()));

		PostMethod method = new PostMethod(server
				+ "/api/bss/resource/subscriber/" + subscriberId
				+ "/subscription/" + subscriptionId);
		method.setFollowRedirects(false);
		method.setRequestHeader("Content-Type", "application/json");
		method.setRequestHeader("x-operation", "entitleSubscriber");
		method.setRequestHeader("Authorization", "Basic " + auth_encoded);
		method.setRequestHeader("charset", "utf-8");

		// Execute
		try {
			_http.executeMethod(method);
		} catch (Exception e) {
			log.error("ERROR: Unable to perform user subcription :  "
					+ subscriptionId);
			throw e;
		}

		String response = "";
		try {
			response = method.getResponseBodyAsString();
		} catch (Exception e) {
			log.error("ERROR: Unable to get response from subscribing user");
			throw e;
		}

		// Checking for 302 error code
		while (method.getStatusCode() == 302) {
			// Process the retry with the new URL, must also resend the request
			Header[] headers = method.getRequestHeaders();
			method.releaseConnection();
			method = new PostMethod(server + "/api/bss/resource/subscriber/"
					+ subscriberId + "/subscription/" + subscriptionId);
			for (int x = 0; x <= headers.length - 1; x++)
				method.setRequestHeader(headers[x]);

			// Execute POST again.
			try {
				_http.executeMethod(method);
			} catch (HttpException e) {
				log.error("ERROR: Unable to perform user subcription :  "
						+ subscriptionId);
				throw e;
			}
		}

		int code = method.getStatusCode();// != 204
		try {
			response = method.getResponseBodyAsString();
		} catch (IOException e) {
			log.error("ERROR: Unable to get response from subscribing user: "
					+ code);
			e.printStackTrace();
		}
		method.releaseConnection();

		if (code == 200) {
			log.info("INFO: Successfully subscribed user. Response: "
					+ response.trim() + "\n");
		} else {
			log.error("ERROR: Non 200 code returned (" + code + "). Response: "
					+ response.trim() + "\n");
			throw new Exception(
					"ERROR: Invalid response code subscribing user: " + code);
		}
	}

	public void deleteSubscriber(String subscriberId, String server,
			String csguser, String csgpassword) throws Exception {

		if (subscriberId.isEmpty())
			throw new Exception("Error: Subscriber ID is empty");

		HttpClient _http = new org.apache.commons.httpclient.HttpClient();
		AuthScope _aScope;
		Credentials _credentials;
		// Create a trust manager that does not validate certificate chains
		createTrustMgr();
		_aScope = new AuthScope(server, AuthScope.ANY_PORT,
				AuthScope.ANY_REALM, "ANY");
		_credentials = new UsernamePasswordCredentials(csguser, csgpassword);

		_http.getState().setCredentials(_aScope, _credentials);
		String auth = _http.getState().getCredentials(_aScope).toString();
		String auth_encoded = new String(Base64.encodeBase64(auth.getBytes()));
		String command = server + "/api/bss/resource/subscriber/"
				+ subscriberId;
		DeleteMethod method = new DeleteMethod(command);
		method.setFollowRedirects(false);
		method.setRequestHeader("Authorization", "Basic " + auth_encoded);
		method.setRequestHeader("charset", "utf-8");

		// Execute

		try {
			_http.executeMethod(method);
		} catch (HttpException e) {
			log.warn("Warn: Unable to send deletion request. Ensure the users were actually added");
			e.printStackTrace();
		}

		String response = "";
		int code = method.getStatusCode();
		try {
			response = method.getResponseBodyAsString();
		} catch (Exception e) {
			log.warn("WARN: Unable to get deletion response");
			e.printStackTrace();
		}

		method.releaseConnection();

		if (code == 204) {
			log.info("INFO: The server has reported the deletion was submitted");
		} else
			log.warn("WARN: Delete call returned unexpected response code ("
					+ code + "). Response: " + response.trim());
	}

}
