/**
 * 
 */
package com.ibm.lconn.automation.framework.services.activities;

import java.io.IOException;
import java.util.HashMap;

import org.apache.abdera.model.Entry;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.lconn.automation.framework.services.activities.nodes.Activity;
import com.ibm.lconn.automation.framework.services.common.LCServiceException;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.common.StringConstants;

/**
 * @author reuven
 * 
 */
public class ActivitiesServiceExt extends ActivitiesService {
	private final static Logger LOGGER = LoggerFactory
			.getLogger(ActivitiesServiceExt.class.getName());

	public ActivitiesServiceExt(AbderaClient client, ServiceEntry service)
			throws IOException, LCServiceException {
		super(client, service, true);
	}

	public ClientResponse createActivityExt(Activity activity)
			throws IOException {
		HashMap<String, String> activityDashbiardUrls = getActivityDashboardURLs();
		String url = activityDashbiardUrls
				.get(StringConstants.ACTIVITIES_OVERVIEW);
		Entry entry = activity.toEntry();
		LOGGER.debug("POST: " + url + "conent: " + entry);
		return client.post(url, entry, super.options);
	}
}
