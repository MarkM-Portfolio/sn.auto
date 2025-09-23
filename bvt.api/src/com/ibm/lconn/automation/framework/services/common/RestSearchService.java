package com.ibm.lconn.automation.framework.services.common;

import java.io.IOException;
import java.util.logging.Logger;

import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.abdera.protocol.client.RequestOptions;

import com.ibm.lconn.automation.framework.search.rest.api.RestAPIUser;

public class RestSearchService {
	private AbderaClient _client;

	protected static Logger LOGGER;

	private RequestOptions _options;

	public RestSearchService(RestAPIUser restApiClient, ServiceEntry service,
			Logger LOGGER) throws IOException {

		this.LOGGER = LOGGER;
		_client = restApiClient.getAbderaClient();
		_options = restApiClient.getDefaultRequestOptions();

	}

	public ClientResponse doSearch(String url) {
		LOGGER.fine("GET:  " + url);
		ClientResponse clientResponse = _client.get(url, _options);
		LOGGER.fine("Response:  type - " + clientResponse.getType()
				+ ", status - " + clientResponse.getStatus() + " "
				+ clientResponse.getStatusText() + ", uri - "
				+ clientResponse.getUri());
		return clientResponse;
	}

	public void release(ClientResponse response) {
		response.release();
	}

}
