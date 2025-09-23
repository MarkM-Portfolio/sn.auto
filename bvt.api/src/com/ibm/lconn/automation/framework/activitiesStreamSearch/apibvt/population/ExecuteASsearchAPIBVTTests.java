/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2011, 2014                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
package com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.population;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeTest;

import com.ibm.lconn.automation.framework.services.common.TestNGEnv;
import com.ibm.lconn.automation.framework.services.common.URLConstants;

//@Suite.SuiteClasses({ AllTests.class, ASCustomListAPIBVTTest.class })
public class ExecuteASsearchAPIBVTTests {
	protected final static Logger LOGGER = LoggerFactory
			.getLogger(ExecuteASsearchAPIBVTTests.class.getName());
	@BeforeTest
	public static void setUp() throws IOException, Exception {
		LOGGER.debug("ASsearchAPIBVTTests setup");
		// if SERVER_URL not set, use default value in testEnv
		if (URLConstants.SERVER_URL.equalsIgnoreCase("")) {
			TestNGEnv.setTestEnv();
		}
		SyncCrawlerBySleeping30Sec.run();
		RunASSearchFvtPopulation.populate();
		SyncCrawlerBySleeping30Sec.run();
		System.out.println("STOP");
	}
}