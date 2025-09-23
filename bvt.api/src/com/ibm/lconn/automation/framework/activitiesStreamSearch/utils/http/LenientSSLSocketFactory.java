///////////////////////////////////////////////////////////////////////////
// 
// IBM Confidential
// OCO Source Materials
// 
// 5724Z67 
// 
// (c) Copyright IBM Corp. 2007, 2010
// 
// The source code for this program is not published or otherwise divested of 
// its trade secrets, irrespective of what has been deposited with
// the U.S. Copyright Office.
// 
///////////////////////////////////////////////////////////////////////////

package com.ibm.lconn.automation.framework.activitiesStreamSearch.utils.http;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;

import org.apache.http.conn.scheme.LayeredSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

/**
 * An SSL socket factory that doesn't validate host certificates. This is needed
 * in order to connect to hosts whose certificates are self-signed.
 * 
 * @author sharonk
 * 
 */
public class LenientSSLSocketFactory implements LayeredSocketFactory {

	public static final String TLS = "TLS";

	public static final String SSL = "SSL";

	public static final String SSLV2 = "SSLv2";

	public static final String SSLV3 = "SSLv3";

	private final SSLContext sslcontext;

	private final javax.net.ssl.SSLSocketFactory socketfactory;

	private X509HostnameVerifier hostnameVerifier = new LenientHostnameVerifier();

	public LenientSSLSocketFactory() throws NoSuchAlgorithmException,
			KeyManagementException {
		this.sslcontext = SSLContext.getInstance(TLS);
		this.sslcontext.init(null,
				new TrustManager[] { new LenientTrustManager() }, null);
		this.socketfactory = this.sslcontext.getSocketFactory();
	}

	// non-javadoc, see interface org.apache.http.conn.SocketFactory
	public Socket createSocket() throws IOException {
		Socket socket = this.socketfactory.createSocket();
		SSLSocket sslSocket = (SSLSocket) socket;

		setEnabledProtocols(sslSocket);

		return socket;
	}

	/**
	 * Add SSL v3 to the list of enabled protocols, as we need to support it and
	 * apparently it is not enabled by default
	 * 
	 * @param sslSocket
	 */
	private void setEnabledProtocols(SSLSocket sslSocket) {
		String[] protocols = null;
		String[] currentlyEnabledProtocols = sslSocket.getEnabledProtocols();
		if (currentlyEnabledProtocols == null) {
			protocols = new String[] { SSLV3 };
		} else {
			for (String protocol : currentlyEnabledProtocols) {
				if (protocol.equals(SSLV3)) {
					return;
				}
			}
			protocols = new String[currentlyEnabledProtocols.length + 1];
			for (int i = 0; i < currentlyEnabledProtocols.length; i++) {
				protocols[i] = currentlyEnabledProtocols[i];
			}
			protocols[currentlyEnabledProtocols.length] = SSLV3;
		}
		try {
			sslSocket.setEnabledProtocols(protocols);
		} catch (IllegalArgumentException e) {
			// In FIPS mode, SSLv3 cannot be enabled, and we will get this
			// exception
			sslSocket.setEnabledProtocols(currentlyEnabledProtocols);
		}
	}

	// non-javadoc, see interface org.apache.http.conn.SocketFactory
	public Socket connectSocket(final Socket sock, final String host,
			final int port, final InetAddress localAddress, int localPort,
			final HttpParams params) throws IOException {
		if (host == null) {
			throw new IllegalArgumentException("Target host may not be null.");
		}
		if (params == null) {
			throw new IllegalArgumentException("Parameters may not be null.");
		}

		SSLSocket sslsock = (SSLSocket) ((sock != null) ? sock : createSocket());

		if ((localAddress != null) || (localPort > 0)) {

			// we need to bind explicitly
			if (localPort < 0)
				localPort = 0; // indicates "any"

			InetSocketAddress isa = new InetSocketAddress(localAddress,
					localPort);
			sslsock.bind(isa);
		}

		int connTimeout = HttpConnectionParams.getConnectionTimeout(params);
		int soTimeout = HttpConnectionParams.getSoTimeout(params);

		sslsock.connect(new InetSocketAddress(host, port), connTimeout);

		sslsock.setSoTimeout(soTimeout);
		try {
			hostnameVerifier.verify(host, sslsock);
			// verifyHostName() didn't blowup - good!
		} catch (IOException iox) {
			// close the socket before re-throwing the exception
			try {
				sslsock.close();
			} catch (Exception x) { /* ignore */
			}
			throw iox;
		}

		return sslsock;
	}

	/**
	 * Checks whether a socket connection is secure. This factory creates
	 * TLS/SSL socket connections which, by default, are considered secure. <br/>
	 * Derived classes may override this method to perform runtime checks, for
	 * example based on the cypher suite.
	 * 
	 * @param sock
	 *            the connected socket
	 * 
	 * @return <code>true</code>
	 * 
	 * @throws IllegalArgumentException
	 *             if the argument is invalid
	 */
	public boolean isSecure(Socket sock) throws IllegalArgumentException {

		if (sock == null) {
			throw new IllegalArgumentException("Socket may not be null.");
		}
		// This instanceof check is in line with createSocket() above.
		if (!(sock instanceof SSLSocket)) {
			throw new IllegalArgumentException(
					"Socket not created by this factory.");
		}
		// This check is performed last since it calls the argument object.
		if (sock.isClosed()) {
			throw new IllegalArgumentException("Socket is closed.");
		}

		return true;

	} // isSecure

	// non-javadoc, see interface LayeredSocketFactory
	public Socket createSocket(final Socket socket, final String host,
			final int port, final boolean autoClose) throws IOException,
			UnknownHostException {
		SSLSocket sslSocket = (SSLSocket) this.socketfactory.createSocket(
				socket, host, port, autoClose);
		setEnabledProtocols(sslSocket);
		hostnameVerifier.verify(host, sslSocket);
		// verifyHostName() didn't blowup - good!
		return sslSocket;
	}

	public void setHostnameVerifier(X509HostnameVerifier hostnameVerifier) {
		if (hostnameVerifier == null) {
			throw new IllegalArgumentException(
					"Hostname verifier may not be null");
		}
		this.hostnameVerifier = hostnameVerifier;
	}

	public X509HostnameVerifier getHostnameVerifier() {
		return hostnameVerifier;
	}

}
