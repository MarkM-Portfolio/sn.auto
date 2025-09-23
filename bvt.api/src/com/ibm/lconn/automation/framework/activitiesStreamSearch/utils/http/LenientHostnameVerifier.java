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
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

import org.apache.http.conn.ssl.X509HostnameVerifier;

/**
 * A hostname verifier that approves of every host.
 * 
 * @author sharonk
 * 
 */
public class LenientHostnameVerifier implements X509HostnameVerifier {

	public boolean verify(String arg0, SSLSession arg1) {
		return true;
	}

	public void verify(String arg0, SSLSocket arg1) throws IOException {
	}

	public void verify(String arg0, X509Certificate arg1) throws SSLException {
	}

	public void verify(String arg0, String[] arg1, String[] arg2)
			throws SSLException {
	}

}
