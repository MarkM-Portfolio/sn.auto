package com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.testsWithDate;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Entry;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.abdera.protocol.client.RequestOptions;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;

import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.FVTUtilsWithDate;
import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.PopStringConstantsAS;
import com.ibm.lconn.automation.framework.services.common.ServiceConfig;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.news.NewsService;

public class FvtASSearchCrawlerEnable {

	protected final static Logger LOGGER = Logger.getLogger(FvtASSearchCrawlerEnable.class.getName());

	private static Abdera abdera;
	private static AbderaClient client;
	private static ServiceConfig config;
	private static NewsService newsService;
	private static UsernamePasswordCredentials credentials;
	private static String requestUri = "/service/atom/search/admin/update";
	private static String queryToSend = "?collectionId=ActivityStream&crawlerId=LocalCrawler&enabled=true";
	private static String crawlerActivationParameter = "&X-Update-Nonce=";
	private static String crawlerActivationParameterValue = "ABC";
	private static String cookiePath = "/news";
	private static String serviceURLStr;
	// private static String domain = "cn.ibm.com";
	protected static HttpClient _http = new HttpClient();
	// private static Properties _profileProperties = new Properties();
	// private static String PROPERTIES_FILE_PATH =
	// "/resources/ASSearch_crawler.properties";
	protected static FileHandler fh;

	private static void setUp() throws Exception {
		DateFormat formatter = new SimpleDateFormat("yyyyMMdd'T'hhmmss'Z'");

		fh = new FileHandler("logs/" + formatter.format(new Date()) + "_ASSearch_Crawler_Enable_Execution.xml", false);

		String serverDomain = FVTUtilsWithDate.getServerDomain(PopStringConstantsAS.SERVER_URL);
		LOGGER.fine("Domain name for addCoockie method: " + serverDomain);

		LOGGER.addHandler(fh);
		abdera = new Abdera();
		client = new AbderaClient(abdera);

		// Register SSL / Add credentials for user
		AbderaClient.registerTrustManager();

		// Get service config for server, assert that it was retrieved and
		// contains the activities service information
		config = new ServiceConfig(client, PopStringConstantsAS.SERVER_URL, true);
		ServiceEntry assearch = config.getService("news");
		if (assearch != null) {
			serviceURLStr = assearch.getServiceURLString();

			// client.addCookie(domain, "X-Update-Nonce",
			// crawlerActivationParameterValue, cookiePath, 5, true);
			client.addCookie(serverDomain, "X-Update-Nonce", crawlerActivationParameterValue, cookiePath, 5, true);
			credentials = new UsernamePasswordCredentials(StringConstants.CONNECTIONS_ADMIN_USER_NAME,
					StringConstants.CONNECTIONS_ADMIN_USER_PASSWORD);
			client.addCredentials(URLConstants.SERVER_URL, StringConstants.AUTH_REALM_FORCED,
					StringConstants.AUTH_BASIC, credentials);

			LOGGER.fine("User credentials: " + StringConstants.CONNECTIONS_ADMIN_USER_NAME + " / "
					+ StringConstants.CONNECTIONS_ADMIN_USER_PASSWORD);
			LOGGER.fine("Server: " + PopStringConstantsAS.SERVER_URL);

			client.addCredentials(assearch.getSslHrefString(), StringConstants.AUTH_REALM_NEWS,
					StringConstants.AUTH_BASIC, credentials);
			client.addCredentials(assearch.getSslHrefString(), StringConstants.AUTH_REALM_FORCED,
					StringConstants.AUTH_BASIC, credentials);
			// Retrieve the service document and assert that it exists
			newsService = new NewsService(client, assearch);
		} else {
			LOGGER.fine("Get News service failed");
		}
	}

	static public void enableASSearchCrawler() throws Exception {
		setUp();
		String startCrawlerUrl = serviceURLStr + requestUri + queryToSend + crawlerActivationParameter
				+ crawlerActivationParameterValue;
		LOGGER.fine(startCrawlerUrl);
		RequestOptions options = client.getDefaultRequestOptions();
		options.setFollowRedirects(false);
		Entry entry = Abdera.getNewFactory().newEntry();
		ClientResponse cResponse = sendRequestToEnableASSearchCrawler(client, startCrawlerUrl, entry, options);
		LOGGER.fine("Request response code:" + cResponse.getStatus() + " Request responce message: "
				+ cResponse.getStatusText());
		if (cResponse.getStatus() != 200) {
			LOGGER.fine("AS Search crawler enable/disable operation failed");
			LOGGER.fine("Received message: " + cResponse.getStatusText());
			ClientResponse cResponse1 = sendRequestToEnableASSearchCrawler(client, startCrawlerUrl, entry, options);
			LOGGER.fine("Request response code:" + cResponse1.getStatus() + " Request responce message: "
					+ cResponse1.getStatusText());
		} else {
			LOGGER.fine("AS Search crawler enable/disable operation successfully finished");
		}
	}

	// Method to send request to enable AS Search crawler, it receive
	// parameters.
	// This method allows to use it several times.
	static public ClientResponse sendRequestToEnableASSearchCrawler(AbderaClient clientToSend, String urlToSend,
			Entry entryToSend, RequestOptions optionsToSend) {
		LOGGER.fine("Starting AS Search crawler enable operation");
		ClientResponse cResponse = FVTUtilsWithDate.sendRequestToServerAndReturnResponse(clientToSend, urlToSend,
				entryToSend, optionsToSend);
		return cResponse;
	}

}
