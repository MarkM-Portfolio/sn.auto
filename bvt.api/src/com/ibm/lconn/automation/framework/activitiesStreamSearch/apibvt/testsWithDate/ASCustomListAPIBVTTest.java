/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2014                                          */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
package com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.testsWithDate;

import static org.testng.AssertJUnit.assertEquals;

import java.io.IOException;
import java.util.logging.Logger;

import org.json.simple.parser.ParseException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.population.ASCustomListPopulationHelper;
import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.ASCustomListUtils;
import com.ibm.lconn.automation.framework.activitiesStreamSearch.utils.nodes.FvtMasterLogsClass;
import com.ibm.lconn.automation.framework.services.opensocial.ASCustomListPopulationRequest;

/**
 * To test Activity Stream Search based on Custom List
 * 
 * @author Raza Naqui
 * @version 5.0
 */
@SuppressWarnings("unused")
public class ASCustomListAPIBVTTest {

	protected static Logger LOGGER = FvtMasterLogsClass.LOGGER;

	private static ASCustomListPopulationHelper asCustomListPopulationHelper;

	private static String requestURLToExecute;

	private static final String MY_ALL_EVENTS = "/@me/@all/@all?filters=";

	private static final String ALL_EVENTS = "/@all/@all/@all?filters=";

	private static final String PUBLIC_ALL_EVENTS = "/@public/@all/@all?filters=";

	private static final String PUBLIC_STATUS_UPDATES = "/@public/@all/@status?filters=";

	public static final String INVALID_FILTER = "invalid_filter";

	public static final String INVALID_FILTER_VALUE = "some_value";

	public static final String OTHER_FILTER = "source";

	public static final String OTHER_FILTER_VALUE = "a";

	/**
	 * Used by Test Runner to setup configuration and initialisation
	 * 
	 * @throws Exception
	 */
	@BeforeClass
	public static void setUp() throws Exception {
		String sourceMethod = "setUp";
		LOGGER.entering(ASCustomListAPIBVTTest.class.getName(), sourceMethod);
		FvtMasterLogsClass.init();
		/*
		 * asCustomListPopulationHelper = ASCustomListPopulationHelper.getInstance();
		 * asCustomListPopulationHelper.populate();
		 */

		LOGGER.exiting(ASCustomListAPIBVTTest.class.getName(), sourceMethod);
	}

	/**
	 * Tests my all events with respect to person in the custom list
	 * 
	 * @throws Exception
	 */
	@Test
	public void testMyAllEventsWithValidListId() throws Exception {
		String sourceMethod = "testMyAllEventsWithValidListId";
		LOGGER.fine(ASCustomListAPIBVTTest.class.getName() + ":" + sourceMethod);

		ASCustomListPopulationRequest request = new ASCustomListPopulationRequest();
		request.setListId(ASCustomListPopulationHelper.customListIdForPerson);
		request.setContext(MY_ALL_EVENTS);

		int actualCount = ASCustomListUtils.getJsonResponseCount(request);
		int expectedCount = 20; // Get this programmatically
		assertEquals("No of Entries", expectedCount, actualCount);
		LOGGER.exiting(ASCustomListAPIBVTTest.class.getName(), sourceMethod);
	}

	/**
	 * Tests all public events with respect to person in the custom list
	 * 
	 * @throws Exception
	 */
	@Test
	public void testPublicAllEventsWithValidListId() throws Exception {
		String sourceMethod = "testPublicAllEventsWithValidListId";
		LOGGER.fine(ASCustomListAPIBVTTest.class.getName() + ":" + sourceMethod);

		ASCustomListPopulationRequest request = new ASCustomListPopulationRequest();
		request.setListId(ASCustomListPopulationHelper.customListIdForPerson);
		request.setContext(PUBLIC_ALL_EVENTS);

		int entriesCount = ASCustomListUtils.getJsonResponseCount(request);
		assertEquals("No of Entries", 20, entriesCount);
		LOGGER.exiting(ASCustomListAPIBVTTest.class.getName(), sourceMethod);
	}

	/**
	 * Tests all public status updates of person in the custom list
	 * 
	 * @throws Exception
	 */
	@Test(enabled = false)
	public void testPublicStatusUpdatesWithValidListId() throws Exception {
		String sourceMethod = "testPublicStatusUpdatesWithValidListId";
		LOGGER.fine(ASCustomListAPIBVTTest.class.getName() + ":" + sourceMethod);

		ASCustomListPopulationRequest request = new ASCustomListPopulationRequest();
		request.setListId(ASCustomListPopulationHelper.customListIdForPerson);
		request.setContext(PUBLIC_STATUS_UPDATES);

		int entriesCount = ASCustomListUtils.getJsonResponseCount(request);
		assertEquals("No of Entries", 4, entriesCount);
		LOGGER.exiting(ASCustomListAPIBVTTest.class.getName(), sourceMethod);
	}

	/**
	 * Tests all events of person in the custom list
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAllEventsWithValidListId() throws Exception {
		String sourceMethod = "testAllEventsWithValidListId";
		LOGGER.fine(ASCustomListAPIBVTTest.class.getName() + ":" + sourceMethod);

		ASCustomListPopulationRequest request = new ASCustomListPopulationRequest();
		request.setListId(ASCustomListPopulationHelper.customListIdForPerson);
		request.setContext(ALL_EVENTS);

		int entriesCount = ASCustomListUtils.getJsonResponseCount(request);
		assertEquals("No of Entries", 20, entriesCount);
		LOGGER.exiting(ASCustomListAPIBVTTest.class.getName(), sourceMethod);
	}

	/**
	 * Tests all events of communities in the custom list
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAllEventsForCommunities() throws Exception {
		String sourceMethod = "testAllEventsForCommunities";
		LOGGER.fine(ASCustomListAPIBVTTest.class.getName() + ":" + sourceMethod);

		ASCustomListPopulationRequest request = new ASCustomListPopulationRequest();
		request.setListId(ASCustomListPopulationHelper.customListIdForCommunity);
		request.setContext(ALL_EVENTS);

		int entriesCount = ASCustomListUtils.getJsonResponseCount(request);
		assertEquals("No of Entries", 16, entriesCount);
		LOGGER.exiting(ASCustomListAPIBVTTest.class.getName(), sourceMethod);
	}
	
	@Test
	public void testAllEventsForPersonAndCommunities() throws Exception {
		String sourceMethod = "testAllEventsForPersonAndCommunities";
		LOGGER.fine(ASCustomListAPIBVTTest.class.getName() + ":" + sourceMethod);

		ASCustomListPopulationRequest request = new ASCustomListPopulationRequest();
		request.setListId(ASCustomListPopulationHelper.customListIdForPersonAndCommunity);
		request.setContext(ALL_EVENTS);

		int entriesCount = ASCustomListUtils.getJsonResponseCount(request);
		assertEquals("No of Entries", 20, entriesCount);
		LOGGER.exiting(ASCustomListAPIBVTTest.class.getName(), sourceMethod);
	}

	/**
	 * This test expects an Exception for passing an invalid list id
	 * 
	 * @throws Exception
	 */
	@Test(expectedExceptions = RuntimeException.class)
	public void testInValidListId() throws Exception {
		String sourceMethod = "testInValidListId";
		LOGGER.fine(ASCustomListAPIBVTTest.class.getName() + ":" + sourceMethod);

		ASCustomListPopulationRequest request = new ASCustomListPopulationRequest();
		request.setListId("11111111-1111-1111-1111-111111111111");
		request.setContext(PUBLIC_ALL_EVENTS);

		int entriesCount = ASCustomListUtils.getJsonResponseCount(request);
		LOGGER.exiting(ASCustomListAPIBVTTest.class.getName(), sourceMethod);
	}

	/**
	 * This test expects an Exception for passing an invalid filter
	 * 
	 * @throws Exception
	 */
	@Test(expectedExceptions = RuntimeException.class)
	public void testInvalidFilterType() throws Exception {
		String sourceMethod = "testInvalidFilterType";
		LOGGER.fine(ASCustomListAPIBVTTest.class.getName() + ":" + sourceMethod);

		ASCustomListPopulationRequest request = new ASCustomListPopulationRequest();
		request.setListId(ASCustomListPopulationHelper.customListIdForPerson);
		request.setOtherFilterType(INVALID_FILTER);
		request.setOtherFilterValue(INVALID_FILTER_VALUE);
		request.setContext(PUBLIC_ALL_EVENTS);

		int entriesCount = ASCustomListUtils.getJsonResponseCount(request);
		assertEquals("No of Entries", 1, entriesCount);
		LOGGER.exiting(ASCustomListAPIBVTTest.class.getName(), sourceMethod);
	}

	/**
	 * Tests multiple filters. E.g: Custom List and as well source
	 * 
	 * @throws Exception
	 */
	@Test
	public void testMultipleFilters() throws Exception {
		String sourceMethod = "testMultipleFilters";
		LOGGER.fine(ASCustomListAPIBVTTest.class.getName() + ":" + sourceMethod);

		ASCustomListPopulationRequest request = new ASCustomListPopulationRequest();
		request.setListId(ASCustomListPopulationHelper.customListIdForPerson);
		request.setOtherFilterType(OTHER_FILTER);
		request.setOtherFilterValue(OTHER_FILTER_VALUE);
		request.setContext(PUBLIC_ALL_EVENTS);

		int entriesCount = ASCustomListUtils.getJsonResponseCount(request);
		assertEquals("No of Entries", 0, entriesCount);
		LOGGER.exiting(ASCustomListAPIBVTTest.class.getName(), sourceMethod);
	}

	/**
	 * Used to depopulate the custom lists and also flushes the log
	 * 
	 * @throws Exception
	 */
	@AfterClass
	public static void flushLog() throws IOException, ParseException {
		FvtMasterLogsClass.flush();
	}
}