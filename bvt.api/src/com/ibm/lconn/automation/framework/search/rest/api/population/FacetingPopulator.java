package com.ibm.lconn.automation.framework.search.rest.api.population;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils.Purpose;
import com.ibm.lconn.automation.framework.search.rest.api.population.creators.ActivityCreator;
import com.ibm.lconn.automation.framework.search.rest.api.population.creators.FileCreator;
import com.ibm.lconn.automation.framework.search.rest.api.tests.cloud.only.facets.SolrLegacyFacetingConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Permissions;
import com.ibm.lconn.automation.framework.services.common.Utils;

public class FacetingPopulator {
	public final static Logger LOGGER = Logger.getLogger(Populator.class.getName());

	FileHandler fh = null;

	public FacetingPopulator() throws IOException {
		DateFormat logDateFormatter = (DateFormat) Utils.logDateFormatter.clone();
		fh = new FileHandler("logs/" + logDateFormatter.format(new Date()) + "FacetingPopulator", false);
		LOGGER.addHandler(fh);
	}

	public static void populate() throws Exception {
		populateActivities();
		populateFiles();
	}

	private static void populateActivities() throws Exception {
		ActivityCreator activityCreator = new ActivityCreator();
		activityCreator.createActivity(Permissions.PRIVATE, Purpose.FACETING, SolrLegacyFacetingConstants.TAGS[0]);
		activityCreator.createActivity(Permissions.PRIVATE, Purpose.FACETING, SolrLegacyFacetingConstants.TAGS[1]);
		activityCreator.createActivity(Permissions.PRIVATE, Purpose.FACETING, SolrLegacyFacetingConstants.TAGS[0]);
		activityCreator.createActivity(Permissions.PRIVATE, Purpose.FACETING, SolrLegacyFacetingConstants.TAGS[1]);
		activityCreator.createActivity(Permissions.PRIVATE, Purpose.FACETING, SolrLegacyFacetingConstants.TAGS[0]);
		activityCreator.createActivity(Permissions.PRIVATE, Purpose.FACETING);
	}

	private static void populateFiles() throws Exception {
		FileCreator fileCreator = new FileCreator();
		fileCreator.createFile(Permissions.PRIVATE, Purpose.FACETING);		
	}

}