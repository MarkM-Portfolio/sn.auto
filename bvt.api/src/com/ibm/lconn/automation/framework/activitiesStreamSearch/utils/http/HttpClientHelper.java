package com.ibm.lconn.automation.framework.activitiesStreamSearch.utils.http;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import org.apache.http.HttpVersion;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.protocol.ResponseProcessCookies;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

/*
 import org.apache.http.HttpVersion;
 import org.apache.http.auth.AuthScope;
 import org.apache.http.auth.UsernamePasswordCredentials;
 import org.apache.http.client.CredentialsProvider;
 import org.apache.http.client.HttpClient;
 import org.apache.http.client.protocol.ResponseProcessCookies;
 import org.apache.http.conn.ClientConnectionManager;
 import org.apache.http.conn.params.ConnManagerParams;
 import org.apache.http.conn.scheme.PlainSocketFactory;
 import org.apache.http.conn.scheme.Scheme;
 import org.apache.http.conn.scheme.SchemeRegistry;
 import org.apache.http.impl.client.BasicCredentialsProvider;
 import org.apache.http.impl.client.DefaultHttpClient;
 import org.apache.http.impl.conn.SingleClientConnManager;
 import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
 import org.apache.http.params.BasicHttpParams;
 import org.apache.http.params.HttpConnectionParams;
 import org.apache.http.params.HttpParams;
 import org.apache.http.params.HttpProtocolParams;
 import org.apache.http.protocol.HTTP;
 */
public class HttpClientHelper {
	private static int CONNECTION_TIMEOUT_MILLISECONDS = 10000;

	private static int SOCKET_DATA_TIMEOUT_MILLISECONDS = 600000;

	/**
	 * Create a default http client, that supports http and https.
	 * 
	 * @return A new http client instance
	 * @throws KeyManagementException
	 * @throws NoSuchAlgorithmException
	 */
	public static HttpClient createHttpClient(String userName,
			String userPassword) throws KeyManagementException,
			NoSuchAlgorithmException {
		return createHttpClient(userName, userPassword, false,
				CONNECTION_TIMEOUT_MILLISECONDS,
				SOCKET_DATA_TIMEOUT_MILLISECONDS, true);
	}

	public static HttpClient createHttpClient(String userName,
			String userPassword, int connectionTimeoutMilliseconds,
			int socketDataTimeoutMilliseconds) throws KeyManagementException,
			NoSuchAlgorithmException {
		return createHttpClient(userName, userPassword, false,
				connectionTimeoutMilliseconds, socketDataTimeoutMilliseconds,
				true);
	}

	/**
	 * Create a default thread safe (i.e., can be used from multiple threads
	 * concurrently) http client, that supports http and https.
	 * 
	 * @return A new http client instance
	 * @throws KeyManagementException
	 * @throws NoSuchAlgorithmException
	 */
	public static HttpClient createThreadSafeHttpClient(String userName,
			String userPassword) throws KeyManagementException,
			NoSuchAlgorithmException {
		return createHttpClient(userName, userPassword, true,
				CONNECTION_TIMEOUT_MILLISECONDS,
				SOCKET_DATA_TIMEOUT_MILLISECONDS, true);
	}

	public static HttpClient createThreadSafeHttpClientWithNoCookieStore(
			String userName, String userPassword)
			throws KeyManagementException, NoSuchAlgorithmException {
		return createHttpClient(userName, userPassword, true,
				CONNECTION_TIMEOUT_MILLISECONDS,
				SOCKET_DATA_TIMEOUT_MILLISECONDS, false);
	}

	public static HttpClient createThreadSafeHttpClient(String userName,
			String userPassword, int connectionTimeoutMilliseconds,
			int socketDataTimeoutMilliseconds) throws KeyManagementException,
			NoSuchAlgorithmException {
		return createHttpClient(userName, userPassword, true,
				connectionTimeoutMilliseconds, socketDataTimeoutMilliseconds,
				true);
	}

	private static HttpClient createHttpClient(String userName,
			String userPassword, boolean threadSafe,
			int connectionTimeoutMilliseconds,
			int socketDataTimeoutMilliseconds, boolean storeCookies)
			throws KeyManagementException, NoSuchAlgorithmException {
		// Create and initialize HTTP parameters
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setContentCharset(params,
				HTTP.DEFAULT_CONTENT_CHARSET);
		HttpProtocolParams.setUseExpectContinue(params, true);
		ConnManagerParams.setMaxTotalConnections(params, 100);
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpConnectionParams.setConnectionTimeout(params,
				connectionTimeoutMilliseconds);
		HttpConnectionParams
				.setSoTimeout(params, socketDataTimeoutMilliseconds);

		// Create and initialize scheme registry
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));

		LenientSSLSocketFactory sslSocketFactory = new LenientSSLSocketFactory();
		schemeRegistry.register(new Scheme("https", sslSocketFactory, 443));

		ClientConnectionManager clientConnectionManager;
		if (threadSafe) {
			// Use ThreadSafeClientConnManager as the connection manager.
			// This connection manager is needed for using the http client from
			// multiple threads.
			clientConnectionManager = new ThreadSafeClientConnManager(params,
					schemeRegistry);
		} else {
			clientConnectionManager = new SingleClientConnManager(params,
					schemeRegistry);
		}

		DefaultHttpClient httpClient = new DefaultHttpClient(
				clientConnectionManager, params);

		CredentialsProvider credProv = new BasicCredentialsProvider();

		String host = AuthScope.ANY_HOST;
		credProv.setCredentials(new AuthScope(host, AuthScope.ANY_PORT),
				new UsernamePasswordCredentials(userName, userPassword));

		httpClient.setCredentialsProvider(credProv);

		if (!storeCookies) {
			httpClient
					.removeResponseInterceptorByClass(ResponseProcessCookies.class);
		}

		return httpClient;
	}

}
