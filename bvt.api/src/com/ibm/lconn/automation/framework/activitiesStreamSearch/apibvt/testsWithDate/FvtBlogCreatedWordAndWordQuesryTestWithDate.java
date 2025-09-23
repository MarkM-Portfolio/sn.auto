package com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.testsWithDate;

import static org.testng.AssertJUnit.assertTrue;

import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.FVTUtilsWithDate;
import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.PopStringConstantsAS;

public class FvtBlogCreatedWordAndWordQuesryTestWithDate extends ActivityStreamSearchTest {

	private static String searchString = "/@public/@all/blogs?query=";
	private static String filterWord = "Manager%20AND%20tiger";

	@Test
	public void fvtBlogCreatedWordAndWordQueryTest() throws Exception {

		requestToExecute = FVTUtilsWithDate.createRequestURL(urlPrefix, searchString, filterWord);

		LOGGER.fine("ASS: fvtBlogCreatedWordAndWordQuesryTest");
		LOGGER.fine("URL: " + requestToExecute);

		int requestResult1 = FVTUtilsWithDate.sendRequestAndGetResult(requestToExecute,
				PopStringConstantsAS.BLOG_ENTRY_CREATION_EVENT_TYPE, PopStringConstantsAS.EVENT_TYPE_LOCATION,
				PopStringConstantsAS.EVENT_TITLE_LOCATION, PopStringConstantsAS.MODERATED_COMMUNITY_BLOG_ENTRY_TITLE);

		assertTrue("Request not found", (requestResult1 > 0));

	}

}
