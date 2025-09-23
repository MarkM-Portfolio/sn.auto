package com.ibm.lconn.automation.framework.search.rest.api.tests;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeTest;

import com.ibm.lconn.automation.framework.search.rest.api.OrientMeConstants;
import com.ibm.lconn.automation.framework.search.rest.api.population.PopulatorOrientMe;
import com.ibm.lconn.automation.framework.services.common.TestNGEnv;
import com.ibm.lconn.automation.framework.services.common.URLConstants;

public class OrientMeRunOnCloud {
	protected final static Logger LOGGER = LoggerFactory.getLogger("OrientMe");

	@BeforeTest
	public void setUp() throws Exception {
		LOGGER.debug("OrientMe setup");

		if (URLConstants.SERVER_URL.equalsIgnoreCase("")) {
			TestNGEnv.setTestEnv();
		}

		OrientMeConstants.loadOrientMeProperties();

		PopulatorOrientMe.populationOrientMe();

	}

}
