package com.ibm.conn.auto.util.webeditors.fvt;

import static org.seleniumhq.jetty9.http.HttpScheme.HTTPS;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import javax.net.ssl.SSLSession;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.SystemDefaultCredentialsProvider;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

public class RestClient {

	private static final boolean DEBUGGING = false, RETRY_ON_LOCKED_FILE = false;

	private static Logger log = LoggerFactory.getLogger(RestClient.class);

	private CloseableHttpClient httpClient;
	protected final String urlScheme, serverHostName, userName, userPass;
	protected final int serverHostPort;
	private CookieStore cookieStore;


	protected void addCookie(Cookie arg0) { cookieStore.addCookie(arg0); }
	protected void clear() { cookieStore.clear(); }
	protected boolean clearExpired(Date arg0) { return cookieStore.clearExpired(arg0); }
	protected List<Cookie> getCookies() { return cookieStore.getCookies(); }

	protected String getUserName() { return userName; }
	protected String getUserPass() { return userPass; }

	public void closeClient() throws IOException { httpClient.close(); }


	public RestClient(String urlScheme, String serverHostName, int serverHostPort,
					boolean trustAnyCertificate, String userName, String userPass ) 
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, UnknownHostException {
		
		this.urlScheme = urlScheme;
		this.serverHostName = serverHostName;
		this.serverHostPort = serverHostPort;
		
		this.userName = userName;
		this.userPass = userPass;
		
		this.cookieStore = new BasicCookieStore();
		HttpClientBuilder httpClientBuilder = HttpClients.custom()
													.setDefaultCredentialsProvider(getBasicCredentials(userName, userPass))
													.setDefaultCookieStore(cookieStore)
													;
		
		if (urlScheme.toLowerCase().startsWith(HTTPS.toString())) {
			if (trustAnyCertificate) {
	            httpClientBuilder.setSSLSocketFactory(getTLS_CSF_TrustSelfSigned_AnyHostname());
	        }
	        else {
	        	// for the time being there is no need to do a proper verification of the server's certificates  
	        }
		}
        
		this.httpClient = httpClientBuilder.build();
	}

	// http://stackoverflow.com/questions/13626965/how-to-ignore-pkix-path-building-failed-sun-security-provider-certpath-suncertp
	private org.apache.http.conn.ssl.SSLConnectionSocketFactory tlsConnectionSocketFactory = null;
	
	private org.apache.http.conn.ssl.SSLConnectionSocketFactory getTLS_CSF_TrustSelfSigned_AnyHostname() 
			throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
		
		if(tlsConnectionSocketFactory == null) {
		    org.apache.http.ssl.SSLContextBuilder sslContextBuilder = org.apache.http.ssl.SSLContexts.custom().loadTrustMaterial(null, new TrustSelfSignedStrategy());
		    sslContextBuilder.useProtocol("TLSv1.2"); // as seen in http://opendj.forgerock.org/opendj-core/apidocs/constant-values.html#org.forgerock.opendj.ldap.SSLContextBuilder.PROTOCOL_TLS1_2; hopefully these strings are standard
		    tlsConnectionSocketFactory = new org.apache.http.conn.ssl.SSLConnectionSocketFactory( sslContextBuilder.build(), new javax.net.ssl.HostnameVerifier() {
				@Override
				public boolean verify(String hostname, SSLSession session) {
					return true; // I could verify that hostname is the dubxpcvm194.mul.ie.ibm.com VM, but that's not a given
				}
		    });
		}
		return tlsConnectionSocketFactory;
	}

	// http://stackoverflow.com/questions/29570383/basic-authentication-from-java-to-sharepoint-2013-rest-api
	private CredentialsProvider getBasicCredentials(String userName, String userPass) {
		
		CredentialsProvider credentialsProvider = new BasicCredentialsProvider(); // DefaultCredentialsProvider
		
		credentialsProvider.setCredentials(
		        new AuthScope( AuthScope.ANY ), // intendedServer.getHost(), intendedServer.getPort()
		        new UsernamePasswordCredentials( userName, userPass )
		        );
		
		return credentialsProvider;
	}

	@SuppressWarnings("unused") // for now...
	private CredentialsProvider getNTCredentials(String userName, String userPass) throws UnknownHostException {
		
		CredentialsProvider credentialsProvider = new SystemDefaultCredentialsProvider();
		
		credentialsProvider.setCredentials(
		        new AuthScope( AuthScope.ANY ), // (intendedServer.getHost(), intendedServer.getPort()),
		        new NTCredentials(userName, userPass, java.net.InetAddress.getLocalHost().getHostName(), "TESTENV") // targeting user TESTENV\test 
		        );
		
		return credentialsProvider;
	}


	// uses Apache HttpCore 4
	protected String doRequest(HttpEntityEnclosingRequestBase request, List<Header> headerList, String jsonEntity)
			throws KeyManagementException, ClientProtocolException, NoSuchAlgorithmException, IOException {

		final String httpClientLogMsg = request.getClass().getSimpleName() + "ing '" + request.getURI() + "'";
		Assert.assertNotNull( jsonEntity, "Content data parameter cannot be null. Got a null content parameter while " + httpClientLogMsg); 

	    request.setEntity(new StringEntity(jsonEntity, ContentType.APPLICATION_JSON));
	    
        return doRequest((HttpRequestBase)request, headerList);
	}

	// uses Apache HttpCore 4
	protected String doRequest(HttpEntityEnclosingRequestBase request, List<Header> headerList, byte[] entity) 
			throws KeyManagementException, ClientProtocolException, NoSuchAlgorithmException, IOException {

		final String httpClientLogMsg = request.getClass().getSimpleName() + "ing '" + request.getURI() + "'";
		Assert.assertNotNull( entity, "Content data parameter cannot be null. Got a null content parameter while " + httpClientLogMsg); 

	    request.setEntity(new ByteArrayEntity(entity, ContentType.DEFAULT_BINARY)); 
	    
        return doRequest((HttpRequestBase)request, headerList);
	}
	
	// uses Apache HttpCore 4
	protected String doRequest(HttpEntityEnclosingRequestBase request, List<Header> headerList, List<NameValuePair> formParameterList) 
			throws ClientProtocolException, IOException, KeyManagementException, NoSuchAlgorithmException {

		final String httpClientLogMsg = request.getClass().getSimpleName() + "ing '" + request.getURI() + "'";
		Assert.assertNotNull( formParameterList, "Form parameter list cannot be null. Got a null parameter while " + httpClientLogMsg); 
		Assert.assertTrue( !formParameterList.isEmpty(), "Form parameter list cannot be empty. Got an invalid parameter while " + httpClientLogMsg); 

		request.setEntity(new UrlEncodedFormEntity(formParameterList));
	    
        return doRequest((HttpRequestBase)request, headerList);
	}
	
	// uses Apache HttpCore 4
	protected String doRequest(HttpRequestBase request, List<Header> headerList)
			throws ClientProtocolException, IOException, KeyManagementException, NoSuchAlgorithmException {

	    if( headerList != null && 0 < headerList.size()) {
	    	for(Header postHeader : headerList)
	    		request.addHeader(postHeader); 
	    }
	    
	    HttpEntity responseEntity = null; String responseEntityStr = ""; 
	    try {
	        boolean retry = false;
	        int retryCount = 3;
	        
        	String httpClientLogMsg = request.getClass().getSimpleName() + "ing '" + request.getURI() + "'";
        	String responseStatusLine;
	        do {
		    	log.info(httpClientLogMsg);
			    CloseableHttpResponse response = httpClient.execute(request);

		    	log.info(response.getStatusLine().toString());
		    	if(DEBUGGING)
			        for(Header header : response.getAllHeaders())
			        	log.info("Header: "+header.toString());
		    	
		    	responseStatusLine = response.getStatusLine().toString();
		        switch(response.getStatusLine().getStatusCode()) {
		        
			        case java.net.HttpURLConnection.HTTP_OK: // http 200
				        responseEntity = response.getEntity();
			        	if( responseEntity != null && 0 != responseEntity.getContentLength()) { // getContentLength may return a negative number if Length is unknown
					        ContentType contentType = ContentType.parse( responseEntity.getContentType().getValue());	        
					        responseEntityStr = IOUtils.toString(responseEntity.getContent());
			        	}
			        	retry=false;
			        	break;
			        	
			        case 423: // file is locked
			        	log.warn("Resource referenced by '" + request.getURI() + "' is currently locked.");
						try { Thread.sleep(5000); }
						catch (InterruptedException e) {} // standard Thread.sleep catch
			        	--retryCount;
			        	retry=true;
						break;
			        
			        default:
			        	String contentErrorMsg;
			        	try {
			        		ContentType contentType = ContentType.parse(response.getEntity().getContentType().getValue());	        
			        		contentErrorMsg = " // " + IOUtils.toString(response.getEntity().getContent());
			        	}
			        	catch(Exception ex) {
			        		contentErrorMsg = "";
			        	}
			        	throw new RuntimeException(httpClientLogMsg + " resulted in a http status code that was not OK: " + response.getStatusLine() + contentErrorMsg);
		        }
	        }
	        while(retry && 0 < retryCount && RETRY_ON_LOCKED_FILE); // the constant goes last to avoid the dead code warning
	        
	        if(retry && 0 == retryCount) // this covers the use case in which the file is reported as 'locked' multiple times in a row  
	        	throw new RuntimeException(httpClientLogMsg + " resulted in a http status code that was not OK: " + responseStatusLine);
	        	
	    }
	    finally {
	        EntityUtils.consume(responseEntity);
	    }

        return responseEntityStr;
	}

	
	/**
	 * This method builds new URI's based on the information passed in the constructor's parameters. It takes a single parameter which will be 
	 * included in the URI as the URI's path. The reason for using URI instead of URL is that it's necessary to properly encode URL's. 
	 * The <a href="http://stackoverflow.com/questions/4737841/urlencoder-not-able-to-translate-space-character">common practice</a> of replacing caracters is incorrect:<br/>
	 * {@code
	 * contentBaseFolder = URLEncoder.encode(contentBaseFolder, "UTF-8").replace("+", "%20");
	 * startOfFileName = URLEncoder.encode(startOfFileName, "UTF-8").replace("+", "%20");
	 * }
	 * <p/>
	 * URLEncoder.encode is designed to encode form parameter values *only*.
	 * The remaining part of the URL must be encoded according to RFC2396. Class 'URL' will not do this for you. From the {@linkplain java.net.URL Javadoc}:
	 * 'The URL class does not itself encode or decode any URL components according to the escaping mechanism defined in RFC2396.'
	 * However, URL encoding</a> can be done with the 'URI' class.
	 * 
	 * @param path to be included in the new URI 
	 * @return a URI the includes {@code restServicePath} as it's path.
	 * @throws MalformedURLException
	 * @throws URISyntaxException
	 */
	protected URI buildURI(String restServicePath) throws URISyntaxException {
		final String userInfo, query, fragment;
		userInfo = query = fragment = null;
		return new URI(	urlScheme,			userInfo,
						serverHostName,		serverHostPort,
						restServicePath,	query,		fragment
				);
	}

	/* http://stackoverflow.com/questions/724043/http-url-address-encoding-in-java
	 * Notice how p & q will yield diferent results
	 * URL p = new URI("Http", "Authority:444", "/Path for This & That.html",
	 *					String.format("QueryKey#1=%s&QueryKey#2=%s",
	 *								URLEncoder.encode("some value", "UTF-8"),
	 *								URLEncoder.encode("Yet+Another\\Value?", "UTF-8")),
	 *					"Fragment").toURL();
	 * URL q = new URL("Http", "Authority", 444, "/Path for This & That.html");
	 */


	
}
