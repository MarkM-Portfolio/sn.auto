package com.ibm.lconn.automation.framework.services.common;


import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import org.apache.abdera.protocol.client.util.NonOpTrustManager;
import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.protocol.SecureProtocolSocketFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provide a HTTPClient SecureProtocolSocketFactory that can be used with 
 * Abdera to create secure sockets using TLS (required for SSL 3.0 Protocol Vulnerability and POODLE Attack)
 *
 * This file was copied from Infra lc.util.web com.ibm.lconn.web.util
 */
public class TLSSocketFactory implements SecureProtocolSocketFactory {
	
	protected static Logger LOGGER = LoggerFactory.getLogger(TLSSocketFactory.class.getName());

	private SSLContext context = null;

	public TLSSocketFactory(TrustManager trustManager) throws KeyManagementException, NoSuchAlgorithmException {
		init(trustManager);
	}
	
	public TLSSocketFactory(TrustManager trustManager, String tlsVersion) throws KeyManagementException, NoSuchAlgorithmException {
		init(trustManager, tlsVersion);
	}

	public TLSSocketFactory() throws KeyManagementException, NoSuchAlgorithmException {
		this(new NonOpTrustManager());
	}
	
	public TLSSocketFactory(String tlsVersion) throws KeyManagementException, NoSuchAlgorithmException {
		this(new NonOpTrustManager(), tlsVersion);
	}
	
	private void init(TrustManager trustManager) throws NoSuchAlgorithmException, KeyManagementException {

		context = SSLContext.getInstance("TLS");
		context.init(null, new TrustManager[] {trustManager}, null);
	}

	private void init(TrustManager trustManager, String tlsVersion) throws NoSuchAlgorithmException, KeyManagementException {

		LOGGER.debug("TLS Version : "+tlsVersion);
		if (tlsVersion != null){
			context = SSLContext.getInstance(tlsVersion);
		} else {
			LOGGER.warn("WARNNING: TLS version parameter is null");
			context = SSLContext.getInstance("TLSv1.2");
		}
		
		context.init(null, new TrustManager[] {trustManager}, null);
	}

	public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
		return context.getSocketFactory().createSocket(host, port);
	}

	public Socket createSocket(String host, int port, InetAddress chost, int cport) throws IOException, UnknownHostException {
		return context.getSocketFactory().createSocket(host, port, chost, cport);
	}

	public Socket createSocket(String host, int port, InetAddress chost, int cport, HttpConnectionParams params) throws IOException, UnknownHostException, ConnectTimeoutException {
		return context.getSocketFactory().createSocket(host, port, chost, cport);
	}

	public Socket createSocket(Socket socket, String host, int port, boolean close) throws IOException, UnknownHostException {
		return context.getSocketFactory().createSocket(socket, host, port, close);
	}

}