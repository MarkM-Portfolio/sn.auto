package com.ibm.lconn.automation.framework.services.common;

import static org.testng.AssertJUnit.assertEquals;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.activation.MimetypesFileTypeMap;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.namespace.QName;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Base;
import org.apache.abdera.model.Category;
import org.apache.abdera.model.Collection;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.model.Service;
import org.apache.abdera.model.Workspace;
import org.apache.abdera.protocol.Response.ResponseType;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.abdera.protocol.client.RequestOptions;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.EntityEnclosingMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.OptionsMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.lconn.automation.framework.services.search.data.FeedWithCacheControlHeader;

public abstract class LCService {
	private static Logger log = LoggerFactory.getLogger(LCService.class);
	
	public static final String CACHE_CONTROL_HEADER_NAME = "Cache-Control";
	protected AbderaClient client;
	protected ServiceEntry service;
	protected boolean foundService;
	protected RequestOptions options;

	protected static Logger API_LOGGER = LoggerFactory.getLogger(LCService.class.getName());
	protected static HttpClient _http = new HttpClient();
	private static String impersonationHeaderKey = "X-LConn-RunAs";

	private int respStatus = -1;
	private String respStatusText = null;
	private String respLocation = null;
	private String respErrorMsg = null;
	private String method_url = null;
	// private String requestheaders = null;
	private String responseheaders = null;
	private ResponseType respType = null;

	public static final String NEWLINE = "\n";

	public LCService(AbderaClient client, ServiceEntry service) {
		this(client, service, API_LOGGER);
	}

	public LCService(AbderaClient client, ServiceEntry service, Logger logger) {

		API_LOGGER = logger;
		this.client = client;
		this.service = service;
		this.options = client.getDefaultRequestOptions();
		this.options.setFollowRedirects(false);
		this.options.setUseExpectContinue(false);
		this.client.setSocketTimeout(300000);
		this.client.setConnectionTimeout(300000);
		if (Boolean.valueOf(System.getProperties().getProperty("useFiddler"))) {
			this.client.setProxy("localhost", 8888);
		}

	}

	public AbderaClient getAbderaClient() {
		return this.client;
	}

	public RequestOptions setRequestOptions(Map<String, String> requestHeaders) {

		this.options = client.getDefaultRequestOptions();
		this.options.setFollowRedirects(false);
		this.options.setUseExpectContinue(false);

		if (requestHeaders != null) {
			for (String header : requestHeaders.keySet()) {
				String value = requestHeaders.get(header);
				if (StringUtils.isNotEmpty(header) && StringUtils.isNotEmpty(value)) {
					this.options.setHeader(header, value);
				}
			}
		}
		return this.options;

	}

	protected ExtensibleElement getFeed(String url) {
		getApiLogger().debug("GET:  " + url);

		ClientResponse cr = client.get(url, this.options);
		parseClientResponse(cr);

		return handleResponse(cr, null);
	}

	protected FeedWithCacheControlHeader getFeedWithCacheControlHeader(String url) {
		getApiLogger().debug("GET:  " + url);

		ClientResponse cr = client.get(url, this.options);
		parseClientResponse(cr);

		String cacheControl = cr.getHeader(CACHE_CONTROL_HEADER_NAME);
		return new FeedWithCacheControlHeader(cacheControl, handleResponse(cr, null));
	}

	protected ExtensibleElement getFeed(AbderaClient client, String url) {
		getApiLogger().debug("GET:  " + url);

		ClientResponse cr = client.get(url, this.options);
		parseClientResponse(cr);

		return handleResponse(cr, null);
	}

	protected ExtensibleElement getFeedWithRedirect(String url) {
		getApiLogger().debug("GET:  " + url);

		options.setFollowRedirects(true);

		ClientResponse cr = client.get(url, this.options);
		parseClientResponse(cr);

		return handleResponse(cr, null);
	}

	protected ExtensibleElement getNotesFeed(String url) {
		getApiLogger().debug("GET:  " + url);

		this.options.setHeader("User-Agent", "Notes");
		ClientResponse cr = client.get(url, this.options);
		parseClientResponse(cr);

		return handleResponse(cr, null);
	}

	protected ExtensibleElement postFeed(String url, ExtensibleElement entry) {
		getApiLogger().debug("POST: " + url);
		getApiLogger().debug("ENTRY: " + (entry == null ? "null" : entry.toString()));

		if (entry == null) {
			ClientResponse cr = client.post(url, (RequestEntity) null, this.options);
			parseClientResponse(cr);
			return handleResponse(cr, entry);
		}

		ClientResponse cr = client.post(url, entry, this.options);
		parseClientResponse(cr);
		return handleResponse(cr, entry);
	}

	protected ExtensibleElement postFeed(String url, InputStream input) {
		getApiLogger().debug("POST: " + url);

		ClientResponse cr = client.post(url, input, this.options);
		ExtensibleElement ee = null;
		parseClientResponse(cr);
		return handleResponse(cr, ee);
	}

	protected ExtensibleElement postFeed(String url, InputStream input, Map<String, String> requestHeaders) {
		getApiLogger().debug("POST: " + url);
		getApiLogger().debug("requestHeaders: " + requestHeaders.toString());

		ClientResponse cr = client.post(url, input, addRequestOptions(requestHeaders));
		// remove customized headers that are added in this.options by
		// addRequestOptions().
		removeRequestOptions(requestHeaders);

		ExtensibleElement ee = null;
		parseClientResponse(cr);
		return handleResponse(cr, ee);
	}

	protected ExtensibleElement putFeed(String url, ExtensibleElement entry) {
		getApiLogger().debug("PUT:  " + url);
		getApiLogger().debug("ENTRY: " + (entry == null ? "null" : entry.toString()));

		ClientResponse cr = client.put(url, entry, this.options);
		// ExtensibleElement eEle = null;
		parseClientResponse(cr);
		return handleResponse(cr, entry);
	}

	public ExtensibleElement putFeed(String url, InputStream input) {
		getApiLogger().debug("PUT:  " + url);

		ClientResponse cr = client.put(url, input, this.options);
		// ExtensibleElement eEle = null;
		parseClientResponse(cr);
		return handleResponse(cr, null);
	}

	protected ExtensibleElement putFile(String url, File file) throws FileNotFoundException {
		getApiLogger().debug("PUT:  " + url);

		RequestOptions fileOptions = client.getDefaultRequestOptions();
		fileOptions.setHeader("Content-Type", Utils.getMimeType(file));
		fileOptions.setFollowRedirects(false);
		return handleResponse(client.put(url, new FileInputStream(file), fileOptions), null);
	}

	// TJB 4/16/14 - Jenkins Pipeline doesn't seem to be able to access image files
	// in .jars. Using InputStream instead.
	protected ExtensibleElement putFileWithStream(String url, InputStream input, String mimeType)
			throws FileNotFoundException {
		getApiLogger().debug("PUT:  " + url);

		RequestOptions fileOptions = client.getDefaultRequestOptions();
		fileOptions.setHeader("Content-Type", mimeType);
		fileOptions.setFollowRedirects(false);
		return handleResponse(client.put(url, input, fileOptions), null);
	}

	protected ExtensibleElement postMultipartFeed(String url, Entry entry, File media) {
		getApiLogger().debug("POST: " + url);

		RequestOptions multiPartOptions = client.getDefaultRequestOptions();
		// RequestOptions multiPartOptions = this.options;

		multiPartOptions.setHeader("Content-Type", "multipart/related;type=\"application/atom+xml\"");
		multiPartOptions.setFollowRedirects(false);

		try {
			StringPart entryPart = new StringPart("entry", entry.toString());
			entryPart.setContentType("application/atom+xml");

			FilePart filePart = new FilePart("file", media);

			RequestEntity request = new MultipartRequestEntity(new Part[] { entryPart, filePart },
					client.getHttpClientParams());

			ClientResponse cr = client.post(url, request, multiPartOptions);
			parseClientResponse(cr);
			return handleResponse(cr, entry);

		} catch (Exception e) {
			getApiLogger().error(e.getMessage());
			getApiLogger().error(e.getStackTrace().toString());
			return null;
		}
	}

	protected ExtensibleElement postMultipartFeedWithBase64(String url, Entry entry, File media) {
		getApiLogger().debug("POST: " + url);

		RequestOptions multiPartOptions = client.getDefaultRequestOptions();
		multiPartOptions.setHeader("Content-Type", "multipart/related;type=\"application/atom+xml\"");
		multiPartOptions.setFollowRedirects(false);

		try {
			MimetypesFileTypeMap map = new MimetypesFileTypeMap();
			String contentType = map.getContentType(media);
			InputStream is = new FileInputStream(media);
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			byte[] buf = new byte[4096];
			int count = -1;
			while ((count = is.read(buf)) != -1) {
				os.write(buf, 0, count);
			}
			is.close();
			os.close();
			String encodedString = Base64.encodeBase64String(os.toByteArray()).trim();
			String multipartString = "--boundary" + NEWLINE + NEWLINE + entry.toString() + NEWLINE + "--boundary"
					+ NEWLINE + "Content-Type: " + contentType + NEWLINE
					+ "Content-Disposition: attachment; filename=\"" + media.getName() + "\"" + NEWLINE
					+ "Content-Transfer-Encoding:base64" + NEWLINE + NEWLINE + encodedString + NEWLINE + "--boundary--";

			RequestEntity request = new StringRequestEntity(multipartString,
					"multipart/related;type=\"application/atom+xml\"", null);
			ClientResponse cr = client.post(url, request, multiPartOptions);

			parseClientResponse(cr);
			return handleResponse(cr, entry);

		} catch (Exception e) {
			getApiLogger().error(e.getMessage());
			getApiLogger().error(e.getStackTrace().toString());
			return null;
		}
	}

	protected ExtensibleElement postMultipartFeedImpersonated(String url, Entry entry, File media) {
		getApiLogger().debug("POST: " + url);

		RequestOptions multiPartOptions = client.getDefaultRequestOptions();
		multiPartOptions.setHeader("Content-Type", "multipart/related;type=\"application/atom+xml\"");
		if (this.options.getHeader(impersonationHeaderKey) != null) {
			multiPartOptions.setHeader(impersonationHeaderKey, this.options.getHeader(impersonationHeaderKey));
		}
		multiPartOptions.setFollowRedirects(false);

		try {
			StringPart entryPart = new StringPart("entry", entry.toString());
			entryPart.setContentType("application/atom+xml");

			FilePart filePart = new FilePart("file", media);

			RequestEntity request = new MultipartRequestEntity(new Part[] { entryPart, filePart },
					client.getHttpClientParams());

			ClientResponse cr = client.post(url, request, multiPartOptions);
			parseClientResponse(cr);
			return handleResponse(cr, entry);

		} catch (Exception e) {
			getApiLogger().error(e.getMessage());
			getApiLogger().error(e.getStackTrace().toString());
			return null;
		}
	}

	protected ExtensibleElement putMultipartFeed(String url, Entry entry, File media) {
		getApiLogger().debug("PUT:  " + url);

		RequestOptions multiPartOptions = client.getDefaultRequestOptions();
		multiPartOptions.setHeader("Content-Type", "multipart/related;type=\"application/atom+xml\"");

		try {
			StringPart entryPart = new StringPart("entry", entry.toString());
			entryPart.setContentType("application/atom+xml");

			FilePart filePart = new FilePart("file", media);
			filePart.setContentType(Utils.getMimeType(media));
			RequestEntity request = new MultipartRequestEntity(new Part[] { entryPart, filePart },
					client.getHttpClientParams());

			return handleResponse(client.put(url, request, multiPartOptions), entry);

		} catch (Exception e) {
			getApiLogger().error(e.getMessage());
			getApiLogger().error(e.getStackTrace().toString());
			return null;
		}
	}

	protected ExtensibleElement postFile(String url, File file) throws FileNotFoundException {
		getApiLogger().debug("POST: " + url);
		RequestOptions fileOptions = client.getDefaultRequestOptions();
		fileOptions.setHeader("Content-Type", Utils.getMimeType(file));
		fileOptions.setFollowRedirects(false);
		return handleResponse(client.post(url, new FileInputStream(file), fileOptions), null);

	}

	protected ExtensibleElement postFileStream(String url, File file, InputStream infile) throws FileNotFoundException {
		getApiLogger().debug("POST: " + url);

		RequestOptions fileOptions = client.getDefaultRequestOptions();
		fileOptions.setHeader("Content-Type", Utils.getMimeType(file));
		fileOptions.setFollowRedirects(false);
		// return handleResponse(client.post(url, new FileInputStream(file),
		// fileOptions), null);
		return handleResponse(client.post(url, infile, fileOptions), null);

	}

	protected boolean deleteFeed(String url) {
		getApiLogger().debug("DELETE: " + url);

		boolean success = false;
		ClientResponse response = client.delete(url, this.options);
		getApiLogger().debug("STATUS : " + response.getStatus());

		parseClientResponse(response);
		ResponseType responseType = response.getType();

		if (responseType == ResponseType.SUCCESS) {
			success = true;
			getApiLogger().debug(
					response.getMethod() + " SUCCESS - " + response.getStatus() + ": " + response.getStatusText());
		} else if (responseType == ResponseType.REDIRECTION) {
			success = deleteFeed(getRedirectLocation(response));
			getApiLogger().warn(
					response.getMethod() + " REDIRECTION - " + response.getStatus() + ": " + response.getStatusText());
		} else if ((responseType == ResponseType.CLIENT_ERROR) && (response.getStatus() == 403)) {
			success = false;
			getApiLogger().debug(
					response.getMethod() + " Failure - " + response.getStatus() + ": " + response.getStatusText());
		} else {
			success = false;
			getApiLogger().debug(response.getMethod() + " ERROR - " + response.getStatus() + ": "
					+ response.getStatusText() + "\n" + url + "\n" + response.getDocument().toString());

		}

		response.release();

		return success;
	}

	/*
	 * TJB 8/6/15 Copy of deleteFeed(String url) with an extra cookie called
	 * "enableTrash" This cookie is used to bypass the Trash view when deleting a
	 * community. Instead the community is directly purged from the database. This
	 * is important as we want to limit the amount of data accumulated in test
	 * environments. Therefore, this is a hard delete.
	 * 
	 * This method is for Communities only. I don't know if other applications
	 * support this same cookie (it would be great if they did).
	 * 
	 */
	protected boolean deleteWithTrashCookie(String url) {
		getApiLogger().debug("DELETE: " + url);
		// This cookie allows a hard delete.
		// client.addCookie("swg.usma.ibm.com", "enableTrash", "false", "/communities",
		// null, true);
		client.addCookie(".com", "enableTrash", "false", "/communities", null, true);

		boolean success = false;
		ClientResponse response = client.delete(url, this.options);
		getApiLogger().debug("STATUS : " + response.getStatus());

		parseClientResponse(response);
		ResponseType responseType = response.getType();

		if (responseType == ResponseType.SUCCESS) {
			success = true;
			getApiLogger().debug(
					response.getMethod() + " SUCCESS - " + response.getStatus() + ": " + response.getStatusText());
		} else if (responseType == ResponseType.REDIRECTION) {
			success = deleteFeed(getRedirectLocation(response));
			getApiLogger().debug(
					response.getMethod() + " REDIRECTION - " + response.getStatus() + ": " + response.getStatusText());
		} else {
			success = false;
			getApiLogger().debug(response.getMethod() + " ERROR - " + response.getStatus() + ": "
					+ response.getStatusText() + "\n" + url + "\n" + response.getDocument().toString());

		}

		response.release();

		return success;
	}

	public void deleteFeedWithBody(String url, String contentBody, String userCredentials) throws Exception {
		getApiLogger().debug("DELETE WITH REUQEST BODY: " + url);
		excuteDeleteWithRequestBody(url, contentBody, userCredentials);
	}

	public void deleteFeedWithBody(String url, String contentBody) throws Exception {
		getApiLogger().debug("DELETE WITH REQUEST BODY: " + url);
		excuteDeleteWithRequestBody(url, contentBody);
	}

	protected int deleteWithResponseStatus(String url) {
		getApiLogger().debug("DELETE: " + url);

		ClientResponse response = client.delete(url, this.options);

		response.release();
		return response.getStatus();
	}

	public ClientResponse deleteWithResponse(String url) {
		getApiLogger().debug("DELETE: " + url);

		ClientResponse response = client.delete(url, this.options);

		response.release();
		return response;
	}

	public String getRedirectLocation(ClientResponse response) {
		String locationHeader = response.getHeader("Location");
		if (locationHeader == null) {
			locationHeader = response.getHeader("location");
		}

		return locationHeader;
	}

	private synchronized ExtensibleElement handleResponse(ClientResponse response, ExtensibleElement requestEntry) {

		respStatus = response.getStatus();
		respStatusText = response.getStatusText();
		if (getRedirectLocation(response) != null)
			respLocation = getRedirectLocation(response);
		respErrorMsg = null;
		respType = response.getType();

		Document<?> feedDoc = null;
		ExtensibleElement feed = null;

		ResponseType responseType = response.getType();
		getApiLogger().debug("STATUS: " + response.getStatus() + " " + response.getStatusText());
		if (responseType == ResponseType.SUCCESS) {
			if (response.getStatus() != 204) {
				try {
					feedDoc = (Document<?>) response.getDocument().clone();
					feed = (ExtensibleElement) feedDoc.getRoot();
				} catch (Exception e) {
					// Have to handle case where response back could possibly be empty
					// At this point we know that the post/put/get occurred successfully
					feed = Abdera.getNewFactory().newEntry();
					feed.setText("empty response");
					for (String name : response.getHeaderNames()) {
						String msg = response.getHeader(name);
						feed.addSimpleExtension(new QName("api", name, "header"), msg);
					}
					if (getRedirectLocation(response) != null)
						((Entry) feed).addLink(getRedirectLocation(response), StringConstants.REL_EDIT);
				}
				// getApiLogger().fine(response.getMethod() + " SUCCESS - " +
				// response.getStatus() + ": " + response.getStatusText());
			} else {
				feed = Abdera.getNewFactory().newEntry();
				((Entry) feed).setContent("204: No content");

				String location = getRedirectLocation(response);
				if (location != null) {
					((Entry) feed).addLink(location);
					getApiLogger().warn(response.getMethod() + " WARNING - " + response.getStatus() + ": "
							+ response.getStatusText() + "\n" + location);
				} else {
					getApiLogger().warn(response.getMethod() + " WARNING - " + response.getStatus() + ": "
							+ response.getStatusText() + "\nNo location returned.");
				}

			}
		} else if (responseType == ResponseType.REDIRECTION) {
			String location = getRedirectLocation(response);
			if (location != null) {
				response.release();
				if (response.getMethod().equals("GET"))
					return getFeed(location);
				else if (response.getMethod().equals("POST"))
					return postFeed(location, requestEntry);
				else if (response.getMethod().equals("PUT"))
					return putFeed(location, requestEntry);
				return null;
			}
			getApiLogger().warn(
					response.getMethod() + " REDIRECTION - " + response.getStatus() + ": " + response.getStatusText());
		} else {
			String sRootString = "";
			respErrorMsg = "";
			String downloadAnywayUrl = null;
			try {
				// This sometimes throws if getDocument() fails the parse, don't need an error
				// within the error
				Element errorElem = response.getDocument().getRoot();
				sRootString = errorElem.toString();
				try { // Try to retrieve error message text
					respErrorMsg = errorElem.getElements().get(1).getText().trim();
				} catch (Exception e) {
					respErrorMsg = "";
				}

				// Try to retrieve error downloadAnywayUrl
				try {
					if (response.getStatus() == 403 && errorElem.getElements().size() > 3) {
						String errorCode = errorElem.getElements().get(0).getText().trim();
						if ("UnscannedFileDownloadWarning".equalsIgnoreCase(errorCode)) {
							downloadAnywayUrl = errorElem.getElements().get(2).getText().trim();
						}
					}
				} catch (Exception e) {
					downloadAnywayUrl = null;
				}
			} catch (Exception e) {
				sRootString = "";
			}

			getApiLogger().debug(response.getMethod() + " ERROR - " + response.getStatus() + ": "
					+ response.getStatusText() + "\n" + response.getUri() + "\n" + sRootString);

			feed = Utils.createErrorEntry(response.getStatus(), response.getStatusText());

			if (downloadAnywayUrl != null) {
				final QName API_RESPONSE_DOWNLOADANYWAYURL = new QName("api", "downloadAnywayUrl", "resp");
				Factory factory = Abdera.getNewFactory();
				ExtensibleElement responseDownloadAnywayUrl = factory
						.newExtensionElement(API_RESPONSE_DOWNLOADANYWAYURL);
				responseDownloadAnywayUrl.setText(downloadAnywayUrl);
				feed.addExtension(responseDownloadAnywayUrl);
			}
		}

		response.release();

		return feed;
	}

	private String handleStringResponse(ClientResponse response, String in) {
		String responseStr = null;

		respStatus = response.getStatus();
		respStatusText = response.getStatusText();
		if (getRedirectLocation(response) != null)
			respLocation = getRedirectLocation(response);
		respType = response.getType();

		ResponseType responseType = response.getType();
		getApiLogger().debug("STATUS: " + response.getStatus() + " " + response.getStatusText() + " " + responseType);

		if (responseType == ResponseType.SUCCESS) {
			if (response.getStatus() != 204) {

				try {
					responseStr = readResponse(response.getReader());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				getApiLogger().debug(responseStr);
				// OrderedJSONObject jsonResponse = new OrderedJSONObject(responseStr);
				getApiLogger().debug(
						response.getMethod() + " SUCCESS - " + response.getStatus() + ": " + response.getStatusText());
			} else {
				String location = getRedirectLocation(response);
				if (location != null) {
					getApiLogger().warn(response.getMethod() + " WARNING - " + response.getStatus() + ": "
							+ response.getStatusText() + "\n" + location);
				} else {
					getApiLogger().warn(response.getMethod() + " WARNING - " + response.getStatus() + ": "
							+ response.getStatusText() + "\nNo location returned.");
				}

			}
		} else if (responseType == ResponseType.REDIRECTION) {
			String location = getRedirectLocation(response);
			if (location != null) {
				response.release();
				if (response.getMethod().equals("GET"))
					responseStr = handleStringResponse(getResponse(location), null);
				else if (response.getMethod().equals("POST"))
					responseStr = handleStringResponse(postResponse(location, in), in);
				else if (response.getMethod().equals("PUT"))
					responseStr = handleStringResponse(putResponse(location, in), in);
			}
			getApiLogger().warn(
					response.getMethod() + " REDIRECTION - " + response.getStatus() + ": " + response.getStatusText());
		} else {
			try {
				responseStr = readResponse(response.getReader());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// getApiLogger().fine(responseStr);
			getApiLogger()
					.debug(response.getMethod() + " ERROR - " + response.getStatus() + ": " + response.getStatusText());

		}

		response.release();

		return responseStr;
	}

	protected HashMap<String, String> getCollectionUrls(Service service) {
		HashMap<String, String> collectionUrls = new HashMap<String, String>();

		for (Workspace workspace : service.getWorkspaces()) {
			for (Collection collection : workspace.getCollections()) {
				String st = collection.getTitle();
				if (st != null) {
					collectionUrls.put(st.trim(), collection.getHref().toString());
				}

				getApiLogger().debug("getCollectionUrls : " + collectionUrls.get(collection.getTitle()));
			}
		}

		return collectionUrls;
	}

	public boolean isFoundService() {
		return foundService;
	}

	public void setFoundService(boolean foundService) {
		this.foundService = foundService;
	}

	public String getServiceName() {
		return service.getTitle();
	}

	public ClientResponse getResponse(String url) {
		getApiLogger().debug("GET:  " + url);

		ClientResponse cr = client.get(url, this.options);
		parseClientResponse(cr);

		return cr;
	}

	public String getResponseString(String url) {
		return handleStringResponse(getResponse(url), null);
	}

	public String getResponseStringWithRedirect(String url) {
		options.setFollowRedirects(true);
		return handleStringResponse(getResponse(url), null);
	}

	public ClientResponse postResponse(String url, String st) {
		getApiLogger().debug("POST:  " + url);
		getApiLogger().debug("BODY:  " + st);

		// options.setContentType("application/json");
		InputStream in;
		ClientResponse clientResponse = null;
		if (st != null) {
			try {
				in = new ByteArrayInputStream(st.getBytes("UTF-8"));
				clientResponse = client.post(url, in, options);
				parseClientResponse(clientResponse);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			clientResponse = client.post(url, Abdera.getNewFactory().newEntry(), options);
			parseClientResponse(clientResponse);
		}
		getApiLogger().debug("Response:  type - " + clientResponse.getType() + ", status - " + clientResponse.getStatus()
				+ " " + clientResponse.getStatusText() + ", uri - " + clientResponse.getUri());
		return clientResponse;
	}

	public ClientResponse postResponse(String url, Base base) {
		getApiLogger().debug("POST:  " + url);

		ClientResponse clientResponse = null;
		if (base != null) {
			clientResponse = client.post(url, base, options);
			parseClientResponse(clientResponse);
		} else {
			clientResponse = client.post(url, Abdera.getNewFactory().newEntry(), options);
			parseClientResponse(clientResponse);
		}
		getApiLogger().debug("Response:  type - " + clientResponse.getType() + ", status - " + clientResponse.getStatus()
				+ " " + clientResponse.getStatusText() + ", uri - " + clientResponse.getUri());
		return clientResponse;
	}

	public ClientResponse postResponse(String url, InputStream in) {
		getApiLogger().debug("POST:  " + url);

		ClientResponse clientResponse = null;
		if (in != null) {
			clientResponse = client.post(url, in, options);
			parseClientResponse(clientResponse);
		} else {
			clientResponse = client.post(url, Abdera.getNewFactory().newEntry(), options);
			parseClientResponse(clientResponse);
		}
		getApiLogger().debug("Response:  type - " + clientResponse.getType() + ", status - " + clientResponse.getStatus()
				+ " " + clientResponse.getStatusText() + ", uri - " + clientResponse.getUri());
		return clientResponse;
	}

	public String postResponseJSONString(String url, String st) {
		options.setContentType("application/json");
		return handleStringResponse(postResponse(url, st), st);
	}

	public String postResponseString(String url, String st) {
		return handleStringResponse(postResponse(url, st), st);
	}

	public ClientResponse putResponse(String url, String st) {
		getApiLogger().debug("PUT:  " + url);
		getApiLogger().debug("BODY:  " + st);

		options.setContentType("application/json");
		InputStream in;
		ClientResponse clientResponse = null;
		if (st != null) {
			try {
				in = new ByteArrayInputStream(st.getBytes("UTF-8"));
				clientResponse = client.put(url, in, options);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			clientResponse = client.post(url, Abdera.getNewFactory().newEntry(), options);

		}
		getApiLogger().debug("Response:  type - " + clientResponse.getType() + ", status - " + clientResponse.getStatus()
				+ " " + clientResponse.getStatusText() + ", uri - " + clientResponse.getUri());
		return clientResponse;
	}

	public ClientResponse putResponse(String url, Base base) {
		getApiLogger().debug("PUT:  " + url);

		ClientResponse clientResponse = null;
		if (base != null) {
			clientResponse = client.put(url, base, options);
			parseClientResponse(clientResponse);
		} else {
			clientResponse = client.put(url, Abdera.getNewFactory().newEntry(), options);
			parseClientResponse(clientResponse);
		}
		getApiLogger().debug("Response:  type - " + clientResponse.getType() + ", status - " + clientResponse.getStatus()
				+ " " + clientResponse.getStatusText() + ", uri - " + clientResponse.getUri());
		return clientResponse;
	}

	public ClientResponse putResponse(String url, InputStream in) {
		getApiLogger().debug("PUT:  " + url);

		ClientResponse clientResponse = null;
		if (in != null) {
			clientResponse = client.put(url, in, options);
			parseClientResponse(clientResponse);
		} else {
			clientResponse = client.put(url, Abdera.getNewFactory().newEntry(), options);
			parseClientResponse(clientResponse);
		}
		getApiLogger().debug("Response:  type - " + clientResponse.getType() + ", status - " + clientResponse.getStatus()
				+ " " + clientResponse.getStatusText() + ", uri - " + clientResponse.getUri());
		return clientResponse;
	}

	/*
	 * public ClientResponse putResponseWithAuthHeader(String url, String st) {
	 * getApiLogger().fine("PUT:  " + url); getApiLogger().fine("BODY:  " + st);
	 * 
	 * String auth_encoded = getBasicCredentialsAuth();
	 * options.setContentType("application/json");
	 * options.setHeader("Authorization", "Basic "+auth_encoded); InputStream in;
	 * 
	 * ClientResponse clientResponse = null; if (st!= null){ try { in = new
	 * ByteArrayInputStream(st.getBytes("UTF-8")); clientResponse = client.put(url,
	 * in, options); } catch (UnsupportedEncodingException e) { // TODO
	 * Auto-generated catch block e.printStackTrace(); } } else{ clientResponse =
	 * client.post(url, Abdera.getNewFactory().newEntry(), options);
	 * 
	 * } getApiLogger().fine("Response:  type - " + clientResponse.getType() +
	 * ", status - " + clientResponse.getStatus()+ " " +
	 * clientResponse.getStatusText() + ", uri - " + clientResponse.getUri() );
	 * return clientResponse; }
	 */

	public String putResponseString(String url, String st) {
		return handleStringResponse(putResponse(url, st), st);
	}

	/*
	 * public String putResponseStringWithAuthHeader(String url, String st) { return
	 * handleStringResponse(putResponseWithAuthHeader( url, st), st); }
	 */

	public ClientResponse patchResponse(String url, Base base, RequestOptions customizedOptions) {
		getApiLogger().debug("PATCH:  " + url);
		getApiLogger().debug("ENTRY: " + (base == null ? "null" : base.toString()));

		ClientResponse clientResponse = null;

		if (customizedOptions == null) {
			customizedOptions = client.getDefaultRequestOptions();
		}
		customizedOptions.setHeader("X-Method-Override", "PATCH");
		clientResponse = client.post(url, base, customizedOptions);
		parseClientResponse(clientResponse);

		getApiLogger().debug("Response:  type - " + clientResponse.getType() + ", status - " + clientResponse.getStatus()
				+ " " + clientResponse.getStatusText() + ", uri - " + clientResponse.getUri());

		String responseStr = null;
		try {
			responseStr = readResponse(clientResponse.getReader());
		} catch (IOException e) {
			e.printStackTrace();
		}
		getApiLogger().debug("Response:  body - " + responseStr);

		return clientResponse;
	}

	public ClientResponse executeResponse(String method, String url, Base base) {
		getApiLogger().debug(method + ":" + url);

		ClientResponse clientResponse = null;
		if (base != null) {
			clientResponse = client.execute(method, url, base, options);
			parseClientResponse(clientResponse);
		} else {
			clientResponse = client.execute(method, url, Abdera.getNewFactory().newEntry(), options);
			parseClientResponse(clientResponse);
		}
		getApiLogger().debug("Response:  type - " + clientResponse.getType() + ", status - " + clientResponse.getStatus()
				+ " " + clientResponse.getStatusText() + ", uri - " + clientResponse.getUri());
		return clientResponse;
	}

	public int executeHttp(PutMethod method) throws HttpException, IOException {

		int status = _http.executeMethod(method);
		return status;

	}

	public int executeHttp(PostMethod method) throws HttpException, IOException {

		int status = _http.executeMethod(method);
		return status;

	}

	public int executeHttp(GetMethod method) throws HttpException, IOException {

		int status = _http.executeMethod(method);
		return status;

	}

	public int executeHttp(DeleteMethod method) throws HttpException, IOException {

		int status = _http.executeMethod(method);
		return status;

	}

	public ClientResponse doSearch(AbderaClient client, String url) {
		getApiLogger().debug("GET:  " + url);
		ClientResponse clientResponse = client.get(url, options);
		getApiLogger().debug("Response:  type - " + clientResponse.getType() + ", status - " + clientResponse.getStatus()
				+ " " + clientResponse.getStatusText() + ", uri - " + clientResponse.getUri());
		return clientResponse;
	}

	public ClientResponse doSearch(String url) {
		return doSearch(this.client, url);
	}

	public String readResponse(Reader responseReader) {
		if (responseReader == null) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		try {
			int charValue = 0;
			while ((charValue = responseReader.read()) != -1) {
				sb.append((char) charValue);
			}
		} catch (IOException e) {
		}
		return sb.toString();
	}

	public static void setAbderaClientCookies(AbderaClient client, String userEmail, String pw) {

		getApiLogger().debug("SET AbderaClient Cookies");

		Cookie[] cookies = getConnectionCookies(userEmail, pw);
		client.addCookies(cookies);

	}

	private static Cookie[] getConnectionCookies(String userEmail, String pw) {
		_http.getState().clearCookies();

		Cookie[] cookies = executeLogin(_http, userEmail, pw);
		return cookies;
	}

	/*
	 * Performs Abdera Http Get..FORM Based
	 * 
	 * @Returns ClientResponse url - correctly formed url
	 * 
	 */
	static ClientResponse getServiceConfigFeed(AbderaClient client, String url, String userEmail, String pw) {

		getApiLogger().debug("GET Service config:  " + url);
		_http.getState().clearCookies();
		Cookie[] cookies;
		if (StringConstants.AUTHENTICATION.equals(StringConstants.Authentication.FORM.toString().toLowerCase())) {
			cookies = executeJLogin(_http, userEmail, pw);
		} else
			cookies = executeLogin(_http, userEmail, pw);
		client.addCookies(cookies);
		return client.get(url);
	}

	// get form based authentication
	public static Cookie[] executeJLogin(HttpClient http) {
		return executeJLogin(http, StringConstants.USER_EMAIL, StringConstants.USER_PASSWORD);
	}

	// get form based authentication
	public static Cookie[] executeJLogin(HttpClient http, String user, String pw) {
		log.info("executeJLogin: logging in as " + user);

		http = new HttpClient(new MultiThreadedHttpConnectionManager());
		String url = URLConstants.SERVER_URL + URLConstants.NEWS_BASE + "/j_security_check";
		String body = "j_username=" + user + "&" + "j_password=" + pw;
		getApiLogger().debug("POST : " + url);
		PostMethod method = new PostMethod(url);

		try {
			((EntityEnclosingMethod) method)
					.setRequestEntity(new StringRequestEntity(body, "application/x-www-form-urlencoded", "utf-8"));

			http.executeMethod(method);
			getApiLogger().debug("STATUS Line : " + method.getStatusLine());
			method.releaseConnection();
			return http.getState().getCookies();

		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	// get web seal authentication for smartcloud
	public static Cookie[] executeLogin(HttpClient http, String userMail, String pw) {
		log.info("executeLogin: logging in as " + userMail);

		String loginUrl = URLConstants.SERVER_URL + "/pkmslogin.form";
		String body = "login-form-type=pwd&error-code=&username=" + userMail + "&password=" + pw;

		PostMethod method = new PostMethod(loginUrl);

		try {
			((EntityEnclosingMethod) method)
					.setRequestEntity(new StringRequestEntity(body, "application/x-www-form-urlencoded", "utf-8"));

			// method.setRequestHeader("Referer", url);
			http.executeMethod(method);
			getApiLogger().debug("STATUS Line : " + method.getStatusLine());
			method.releaseConnection();
			return http.getState().getCookies();

		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public static Cookie[] executeLogin(HttpClient http) {

		return executeLogin(http, StringConstants.USER_EMAIL, StringConstants.USER_PASSWORD);
	}

	// Variation on getCollectionUrls that uses terms to categorize for cases
	// like moderation where titles are the same in different categories
	protected HashMap<String, String> getCollectionUrlsUsingTerm(Service service) {
		HashMap<String, String> collectionUrls = new HashMap<String, String>();

		for (Workspace workspace : service.getWorkspaces()) {
			Category c = (Category) workspace.getFirstChild(StringConstants.ATOM_CATEGORY);
			String term = c.getTerm();
			// keep it generic between blogs + files
			term.replace("documents", "entries");
			for (Collection collection : workspace.getCollections()) {
				Category c2 = (Category) collection.getFirstChild(StringConstants.ATOM_CATEGORY);
				String term2 = c2.getTerm();
				collectionUrls.put(term + "-" + term2, collection.getHref().toString());
				getApiLogger().debug(collectionUrls.get(term + "-" + term2).toString());
			}
		}

		return collectionUrls;
	}

	// Method to allow to enable AS Search crawler.
	// This operation is required for AS Search testing.
	// By default AS Search crawler is disabled
	public ExtensibleElement postUrlToAdminASSearchCrawler(String url) {
		RequestOptions options = client.getDefaultRequestOptions();
		options.setFollowRedirects(false);
		Entry entry = Abdera.getNewFactory().newEntry();
		ClientResponse cr = client.post(url, entry, options);
		parseClientResponse(cr);
		return handleResponse(cr, null);
	}

	private void parseClientResponse(ClientResponse cr) {
		respStatus = cr.getStatus();
		respStatusText = cr.getStatusText();
		if (cr.getLocation() != null)
			respLocation = cr.getLocation().toString();
		respErrorMsg = null;
		respType = cr.getType();
		// getRequestHeaders();
		getResponseHeaders(cr);
	}

	public synchronized int getRespStatus() {
		return respStatus;
	}

	public String getRespStatusText() {
		return respStatusText;
	}

	public String getRespLocation() {
		return respLocation;
	}

	public String getRespErrorMsg() {
		return respErrorMsg;
	}

	public ResponseType getRespType() {
		return respType;
	}

	public String getDetail() {
		return "\n" + method_url + "\nStatus: " + respStatus + "\nRequestheaders:\n" + getRequestHeaders()
				+ "\nResponseheaders:\n" + responseheaders + "\n";
	}

	public void addRequestOption(String header, String value) {
		this.options.setHeader(header, value);
	}

	public RequestOptions addRequestOptions(Map<String, String> requestHeaders) {
		if (requestHeaders != null) {
			for (String header : requestHeaders.keySet()) {
				String value = requestHeaders.get(header);
				if (StringUtils.isNotEmpty(header) && StringUtils.isNotEmpty(value)) {
					this.options.setHeader(header, value);
				}
			}
		}
		return this.options;
	}

	public String getRequestOption(String header) {
		return this.options.getHeader(header);
	}

	public void removeRequestOption(String header) {
		this.options.removeHeaders(header);
	}

	public RequestOptions removeRequestOptions(Map<String, String> requestHeaders) {
		if (requestHeaders != null) {
			for (String header : requestHeaders.keySet()) {
				String value = requestHeaders.get(header);
				if (StringUtils.isNotEmpty(header) && StringUtils.isNotEmpty(value)) {
					this.options.removeHeaders(header);
				}
			}
		}
		return this.options;
	}

	protected int postFeedWithCRX(String url, ExtensibleElement entry) {
		getApiLogger().debug("POST: " + url);

		RequestOptions requestOptions = client.getDefaultRequestOptions();
		// For defect 78808 and 87008 adding the request header "Origin" is required.
		requestOptions.setHeader("Origin", "any_value");

		ClientResponse cr = client.post(url, entry, requestOptions);
		return cr.getStatus();
	}

	private static String getHostName() {

		// For http://xxx.xxx.xxx:000/xxx
		String host = URLConstants.SERVER_URL.substring(URLConstants.SERVER_URL.indexOf(":") + 3);
		if (host.indexOf(":") > 0) {
			host = host.substring(0, host.indexOf(":"));
		} else if (host.indexOf("/") > 0) {
			host = host.substring(0, host.indexOf("/"));
		}
		return host;
	}

	public static void printCookies(Cookie[] cookies) {
		System.out.println("---------------------");
		for (int i = 0; i < cookies.length; i++) {
			Cookie cookie = cookies[i];
			System.out.println("Cookie: " + cookie.getName() + ", Value: " + cookie.getValue() + ", IsPersistent?: "
					+ cookie.isPersistent() + ", Expiry Date: " + cookie.getExpiryDate() + ", Comment: "
					+ cookie.getComment());
		}
	}

	public void postRecommendation(String url) {

		postFeed(url, Abdera.getNewFactory().newEntry());

	}

	public String getServiceURLString() {
		return service.getServiceURLString();
	}

	public static Logger getApiLogger() {
		return API_LOGGER;
	}

	// Special DELETE methods - not use abdera client
	public static String excuteDeleteWithRequestBody(String targetURL, String contentBody) throws Exception {
		return excuteDeleteWithRequestBody(targetURL, contentBody,
				StringConstants.USER_EMAIL + ":" + StringConstants.USER_PASSWORD);
	}

	public static String excuteDeleteWithRequestBody(String targetURL, String contentBody, String userCredentials)
			throws Exception {
		Security.setProperty("ssl.SocketFactory.provider", "com.ibm.jsse2.SSLSocketFactoryImpl");
		Security.setProperty("ssl.ServerSocketFactory.provider", "com.ibm.jsse2.SSLServerSocketFactoryImpl");

		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			@Override
			public void checkClientTrusted(X509Certificate[] certs, String authType) {
			}

			@Override
			public void checkServerTrusted(X509Certificate[] certs, String authType) {
			}

		} };

		String ssl_ver = SSLVersionTest.getSSLVersion(URLConstants.SERVER_URL);
		getApiLogger().debug("excuteDeleteWithRequestBody getSSLVersion : " + ssl_ver);
		SSLContext sc = SSLContext.getInstance(ssl_ver);
		sc.init(null, trustAllCerts, new java.security.SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

		// Create all-trusting host name verifier
		HostnameVerifier allHostsValid = new HostnameVerifier() {

			@Override
			public boolean verify(String arg0, SSLSession arg1) {
				// TODO Auto-generated method stub
				return true;
			}
		};
		// Install the all-trusting host verifier
		HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
		URL url;
		HttpsURLConnection connection = null;
		try {
			// Create connection
			url = new URL(targetURL);
			connection = (HttpsURLConnection) url.openConnection();
			connection.setRequestMethod("DELETE");
			connection.setRequestProperty("Content-Type", "application/atom+xml");

			connection.setRequestProperty("Content-Language", "en-US");
			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);

			String basicAuth = new Base64().encodeToString(userCredentials.getBytes());
			connection.setRequestProperty("Authorization", "Basic " + basicAuth.trim());

			// Send request
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			wr.writeBytes(contentBody);
			wr.flush();
			wr.close();

			// Get Response
			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line;
			StringBuffer response = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				System.out.println(line);
				response.append(line);
				response.append('\r');
			}
			rd.close();
			return response.toString();

		} catch (Exception e) {

			e.printStackTrace();
			return null;

		} finally {

			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	private String getRequestHeaders() {
		String requestheaders = null;
		String[] headerNames = client.getDefaultRequestOptions().getHeaderNames();
		for (int i = 0; i < headerNames.length; i++) {
			String headerName = headerNames[i];
			if (requestheaders == null) {
				requestheaders = headerName + ":" + client.getDefaultRequestOptions().getHeader(headerName);
			} else {
				requestheaders = requestheaders + "; " + headerName + ":"
						+ client.getDefaultRequestOptions().getHeader(headerName);
			}
		}
		return requestheaders;
	}

	private void getResponseHeaders(ClientResponse cr) {
		responseheaders = null;
		method_url = cr.getMethod() + ": " + cr.getUri();

		String[] headerNames = cr.getHeaderNames();
		for (int i = 0; i < headerNames.length; i++) {
			String headerName = headerNames[i];
			if (responseheaders == null) {
				responseheaders = headerName + ":" + cr.getHeader(headerName);
			} else {
				responseheaders = responseheaders + "; " + headerName + ":" + cr.getHeader(headerName);
			}
		}
		// return responseheaders;
	}

	public void makeCorsPreflightRequestsVerifyResponses(String url) throws IOException {

		int[] statuses = makeCorsPreflightRequests(url);

		assertEquals("Untrusted CORS request should fail", 403, statuses[0]);
		assertEquals("Trusted CORS request should succeed", 200, statuses[1]);
		assertEquals("Trusted subdomain CORS request should succeed", 200, statuses[2]);
		assertEquals("Untrusted variant CORS request should fail", 403, statuses[3]);
	}

	public int[] makeCorsPreflightRequests(String url) throws IOException {
		String UNTRUSTED_DOMAIN = "http://untrusted.org";
		String TRUSTED_DOMAIN = "http://fakedomain.com";
		String TRUSTED_SUBDOMAIN = "https://apps.fakedomain.com";
		String UNTRUSTED_DOMAIN_VARIANT = "http://sofakedomain.com";

		String[] originUrls = { UNTRUSTED_DOMAIN, TRUSTED_DOMAIN, TRUSTED_SUBDOMAIN, UNTRUSTED_DOMAIN_VARIANT };
		return makeCorsPreflightRequests(url, originUrls);
	}

	public int[] makeCorsPreflightRequests(String url, String[] originUrls) throws IOException {

		OptionsMethod optionsMethod = new OptionsMethod(url);

		optionsMethod.setRequestHeader("Host", "localhost");
		optionsMethod.setRequestHeader("User-Agent", "Mozilla/5.0");

		int[] results = new int[originUrls.length];

		try {
			int i = 0;
			for (String originUrl : originUrls) {
				optionsMethod.setRequestHeader("Origin", originUrl);
				int status = _http.executeMethod(optionsMethod);
				results[i++] = status;
				API_LOGGER.debug(
						"CORS preflight request to " + url + " ostensibly from " + originUrl + " returned: " + status);
			}
		} finally {
			optionsMethod.releaseConnection();
		}

		return results;
	}

	public void tearDown() {
		this.client.teardown();
	}
}
