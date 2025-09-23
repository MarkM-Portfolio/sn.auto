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

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.X509TrustManager;

/**
 * A trust manager that trust every certificate.
 * 
 * @author sharonk
 * 
 */
public class LenientTrustManager implements X509TrustManager {

	public void checkClientTrusted(X509Certificate[] arg0, String arg1)
			throws CertificateException {
	}

	public void checkServerTrusted(X509Certificate[] arg0, String arg1)
			throws CertificateException {
	}

	public X509Certificate[] getAcceptedIssuers() {
		return null;
	}

}
