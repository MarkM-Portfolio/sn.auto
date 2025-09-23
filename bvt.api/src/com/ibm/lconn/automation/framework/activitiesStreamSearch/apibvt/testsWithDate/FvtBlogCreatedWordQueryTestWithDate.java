package com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.testsWithDate;

/*
 * 23.04.2012 - Due to existance of private community blog at discover list, set expected results for private community blog
 * expextedResults2 at place 4 and 5 to 1  
 */

import static org.testng.AssertJUnit.assertTrue;

import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.FVTUtilsWithDate;
import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.PopStringConstantsAS;

public class FvtBlogCreatedWordQueryTestWithDate extends ActivityStreamSearchTest {

	private static String searchString = "/@public/@all/blogs?query=";
	private static String filterWord = "Manager";

	@Test
	public void fvtBlogCreatedSingleWordQueryTest() throws Exception {

		requestToExecute = FVTUtilsWithDate.createRequestURL(urlPrefix, searchString, filterWord);

		LOGGER.fine("ASS: fvtBlogCreatedSingleWordQueryTest ");
		LOGGER.fine("URL: " + requestToExecute);

		int requestResult1 = FVTUtilsWithDate.sendRequestAndGetResult(requestToExecute,
				PopStringConstantsAS.BLOG_CREATION_EVENT_TYPE, PopStringConstantsAS.EVENT_TYPE_LOCATION,
				PopStringConstantsAS.EVENT_TITLE_LOCATION, PopStringConstantsAS.MODERATED_COMMUNITY_BLOG_TITLE);

		assertTrue("Request not found", (requestResult1 > 0));
	}

}
