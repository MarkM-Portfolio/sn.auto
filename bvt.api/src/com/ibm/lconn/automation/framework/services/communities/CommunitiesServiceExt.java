/**
 * 
 */
package com.ibm.lconn.automation.framework.services.communities;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Logger;

import org.apache.abdera.model.Entry;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;

import com.ibm.lconn.automation.framework.search.rest.api.population.Populator;
import com.ibm.lconn.automation.framework.services.common.LCServiceException;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

/**
 * @author reuven
 * 
 */
public class CommunitiesServiceExt extends CommunitiesService {
	private final static Logger LOGGER = Populator.LOGGER_POPUILATOR;

	public CommunitiesServiceExt(AbderaClient client, ServiceEntry service)
			throws IOException, LCServiceException {
		super(client, service);
	}

	public ClientResponse createCommunityExt(Community community)
			throws IOException {
		HashMap<String, String> commURLs = getCommunitiesURLs();
		String url = (String) commURLs.get(StringConstants.COMMUNITIES_MY);
		Entry entry = community.toEntry();
		LOGGER.fine("POST: " + url + "conent: " + entry);
		return client.post(url, entry, super.options);
	}
}
