package com.ibm.lconn.automation.framework.services.common;

import javax.net.ssl.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.*;
import java.security.*;
import java.security.cert.X509Certificate;

public class SSLVersionTest {

	protected static Logger LOGGER = LoggerFactory
			.getLogger(SSLVersionTest.class.getName());

	public static String guessSSLVersion(String host, int port) {
		String[] known_versions = { "TLSv1.2", "TLSv1.1", "TLSv1", "TLS"};
		//,	"SSLv3", "SSLv2", "SSL" };  skip this test
		String result = null;
		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return new X509Certificate[0];
			}

			public void checkClientTrusted(
					java.security.cert.X509Certificate[] certs, String authType) {
			}

			public void checkServerTrusted(
					java.security.cert.X509Certificate[] certs, String authType) {
			}
		} };

		LOGGER.debug("Checking SSL handshake with " + host + ":" + port);
		for (int i = 0; i < known_versions.length; i++) {
			SSLContext sc;
			String ssl_ver = known_versions[i];
			try {
				sc = SSLContext.getInstance(ssl_ver);
				sc.init(null, trustAllCerts, new java.security.SecureRandom());
			} catch (NoSuchAlgorithmException e1) {
				LOGGER.debug("JRE does not support: " + ssl_ver);
				continue;
			} catch (KeyManagementException e) {
				LOGGER.debug("JRE does not support: " + ssl_ver);
				continue;
			}
			try {
				//LOGGER.debug("Trying: " + ssl_ver);
				SSLSocketFactory factory = sc.getSocketFactory();
				SSLSocket socket = (SSLSocket) factory.createSocket(host, port);
				socket.startHandshake();
				//LOGGER.debug("  Protocel in use: "	+ socket.getSession().getProtocol());
				socket.close();
				//LOGGER.debug("Success with: " + ssl_ver);
				if (result == null) {
					result = ssl_ver;
				}
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (javax.net.ssl.SSLException e) {
				LOGGER.debug("SSLException caught, " + ssl_ver
						+ " NOT going through.");
				// e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.err.println("IOException");
				e.printStackTrace();
			}
		}
		LOGGER.debug("The best to use should be: " + result);
		return result;
	}

	public static String getSSLVersion(String baseUrl)
			throws MalformedURLException {
		String host = null;
		int port = 443;
		String result = null;
		try {
			URL url = new URL(baseUrl);
			host = url.getHost();
			if (url.getPort() != -1) {
				port = url.getPort();
			}
			result = SSLVersionTest.guessSSLVersion(host, port);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw new MalformedURLException("Error : Can't reach Serve URL = "+baseUrl);
		}
		return result;
	}

	public static void main(String args[]) {
		String host = args[0];
		int port = Integer.parseInt(args[1]);

		guessSSLVersion(host, port);
	}

}
