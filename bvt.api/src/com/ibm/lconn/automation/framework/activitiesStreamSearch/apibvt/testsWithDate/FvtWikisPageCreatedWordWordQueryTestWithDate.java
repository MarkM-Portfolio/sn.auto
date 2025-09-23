package com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.testsWithDate;

import static org.testng.AssertJUnit.assertTrue;

import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.FVTUtilsWithDate;
import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.PopStringConstantsAS;

public class FvtWikisPageCreatedWordWordQueryTestWithDate extends ActivityStreamSearchTest {

	private static String searchString = "/@public/@all/@all?query=";
	private static String filterWord = "Fifty%20tiger";

	@Test
	public void fvtWikisPageCreatedWordWordQueryTest() throws Exception {

		requestToExecute = FVTUtilsWithDate.createRequestURL(urlPrefix, searchString, filterWord);

		LOGGER.fine("ASS: fvtWikisPageCreatedWordWordQueryTest ");
		LOGGER.fine("URL: " + requestToExecute);

		int requestResult1 = FVTUtilsWithDate.sendRequestAndGetResult(requestToExecute,
				PopStringConstantsAS.WIKI_CREATION_EVENT_TYPE, PopStringConstantsAS.EVENT_TYPE_LOCATION,
				PopStringConstantsAS.EVENT_TITLE_LOCATION, PopStringConstantsAS.PUBLIC_STANDALONE_WIKI_TITLE);

		assertTrue("Request not found", (requestResult1 > 0));
	}

}
