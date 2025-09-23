import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;

public class SCBuilds {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String deployment = args[0];

		createTrustMgr();

		GetMethod method = new GetMethod(
				"https://cloud.swg.usma.ibm.com/lotusLiveDeployment/Request_deployedBuilds.action?name="
						+ deployment);
		method.setRequestHeader("charset", "utf-8");
		HttpClient _http = new org.apache.commons.httpclient.HttpClient();
		// Execute
		try {
			_http.executeMethod(method);
		} catch (HttpException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		String response = "";
		try {
			System.out.println(method.getResponseBodyAsString());
			response = method.getResponseBodyAsString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		response = response.replace("{", "");
		response = response.replace("}", "");
		response = response.replace("[", "");
		response = response.replace("]", "");
		response = response.replace("json:", "");
		response = response.replace("content: ''", "");
		response = response.replace("errors: null", "");
		response = response.replace("\"", "");
		response = response.replace(",", "\n");
		response = response.trim();
		System.out.println(response);

		try {
			generateBuildLogFile(response, deployment);
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	public static void generateBuildLogFile(String builds, String deployment)
			throws IOException {
		File file = new File(deployment + "_builds.log");
		if (file.exists()) {
			file.delete();
			file.createNewFile();

		} else {
			file.createNewFile();
		}

		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(builds);
		bw.close();
	}

	public static void createTrustMgr() {
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

}
