package com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.testsWithDate;

/*
 * 03.04.2012 - Yakov Vilenchik remove rollup=false/true& from each request
 * 03.04.2012 - due to lack of Community Wikis (Community Wiki creation entry does not exists in JSON response)
 * set expoctedResults1/3 to 0 and search only for Public/Private standalone wiki page event and title  
 * 05.04.2012 - Yakov Vilenchik due to problems with query=Fifty remove this param from query list and remove expected results for these requests
 * 25.04.2012 - Yakov Vilenchik changed according to Ina's new test case details. need to talk to ina about removing Community Wikis test
 * as they doesn't exist.
 */

import static org.testng.AssertJUnit.assertTrue;

import java.util.ArrayList;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.FVTUtilsWithDate;
import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.PopStringConstantsAS;

public class FvtWikisPageCreatedWordQueryTestWithDate extends ActivityStreamSearchTest {

	private static String searchString = "/@public/@all/wikis?query=";
	private static String filterWord = "Chapter";

	@Test
	public void fvtWikisPageCreatedWordQueryTest() throws Exception {
		LOGGER.fine("ASS: fvtWikisPageCreatedWordQueryTest ");
		requestToExecute = FVTUtilsWithDate.createRequestURL(urlPrefix, searchString, filterWord);

		LOGGER.fine("URL: " + requestToExecute);

		int requestResult1 = FVTUtilsWithDate.sendRequestAndGetResult(requestToExecute,
				PopStringConstantsAS.WIKI_PAGE_CREATION_EVENT_TYPE, PopStringConstantsAS.EVENT_TYPE_LOCATION,
				PopStringConstantsAS.EVENT_TITLE_LOCATION, PopStringConstantsAS.PUBLIC_STANDALONE_WIKI_PAGE_TITLE);

		assertTrue("Request not found", (requestResult1 > 0));
	}

}
