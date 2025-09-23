package com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.testsWithDate;

import static org.testng.AssertJUnit.assertTrue;

import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.FVTUtilsWithDate;
import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.PopStringConstantsAS;

//@RunWith(Parameterized.class)
public class FvtTopicCreatedWordQueryTestWithDate extends ActivityStreamSearchTest {

	private static String searchString = "/@public/@all/forums?query=";
	private static String filterWord = "Manager";

	@Test
	public void fvtTopicCreatedSingleWordQueryTest() throws Exception {
		LOGGER.fine("ASS:fvtTopicCreatedSingleWordQueryTest ");
		requestToExecute = FVTUtilsWithDate.createRequestURL(urlPrefix, searchString, filterWord);

		LOGGER.fine("URL: " + requestToExecute);

		int requestResult1 = FVTUtilsWithDate.sendRequestAndGetResult(requestToExecute,
				PopStringConstantsAS.FORUM_TOPIC_CREATION_EVENT_TYPE, PopStringConstantsAS.EVENT_TYPE_LOCATION,
				PopStringConstantsAS.EVENT_TITLE_LOCATION, PopStringConstantsAS.MODERATED_COMMUNITY_TOPIC_TITLE);

		assertTrue("Request not found", (requestResult1 > 0));

	}

}
