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
package com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.ibm.lconn.automation.framework.activitiesStreamSearch.utils.json.JsonClient;
import com.ibm.lconn.automation.framework.activitiesStreamSearch.utils.json.JsonResponse;
import com.ibm.lconn.automation.framework.services.common.ProfileData;
import com.ibm.lconn.automation.framework.services.common.ProfileLoader;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.opensocial.ASCustomListPopulationRequest;

/**
 * This class constains utilities used by Activity Stream Search based Tests on
 * Custom List
 * 
 * @author Raza Naqui
 * @version 5.0
 */
public class ASCustomListUtils {
	private static JsonClient _jsonClient;

	static {
		try {
			ProfileData profData=null;
			
				profData = ProfileLoader.getProfile(2);
			
			
				_jsonClient = new JsonClient(profData.getEmail(),
					profData.getPassword(), URLConstants.SERVER_URL);
			}
		 catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method uses returns the events count for list filter and any other
	 * filter passed
	 * 
	 * @param listId
	 * @param otherFilterType
	 * @param otherFilterValue
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public static int getJsonResponseCount(ASCustomListPopulationRequest request)
			throws Exception {
		final String listId = request.getListId();
		final String context = request.getContext();
		final String otherFilterValue = request.getOtherFilterValue();
		final String otherFilterType = request.getOtherFilterType();
		int jsonResponseEntriesCount = getJsonResponseCount(buildURL(listId,
				otherFilterType, otherFilterValue, context));
		return jsonResponseEntriesCount;
	}

	/**
	 * Builds the URL for listId and other filters passed
	 * 
	 * @param listId
	 * @param otherFilterType
	 * @param otherFilterValue
	 * @param context
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String buildURL(final String listId,
			final String otherFilterType, final String otherFilterValue,
			final String context) throws UnsupportedEncodingException {
		final String URL = "/connections/opensocial/basic/rest/activitystreams";
		final boolean otherFilterExist = otherFilterType != null
				&& otherFilterValue != null;
		String filter = "[{'type':'list','values':['LIST_ID']}";
		StringBuilder urlToPost = new StringBuilder(URL);
		urlToPost.append(context);
		urlToPost.append(URLEncoder.encode(filter, "UTF-8"));

		if (otherFilterExist) {
			urlToPost
					.append(URLEncoder
							.encode(",{'type':'OTHER_FILTER_TYPE','values':['OTHER_FILTER_VALUE']}",
									"UTF-8"));
		}

		urlToPost.append(URLEncoder.encode("]", "UTF-8"));
		urlToPost.append(FVTUtilsWithDate.createDateFilterString(DateFilter
				.instance().getDateFilterParam()));

		String urlAsString = urlToPost.toString();
		urlAsString = urlAsString.replace("LIST_ID", listId);

		if (otherFilterExist) {
			urlAsString = urlAsString.replace("OTHER_FILTER_TYPE",
					otherFilterType);
			urlAsString = urlAsString.replace("OTHER_FILTER_VALUE",
					otherFilterValue);
		}

		return urlAsString;
	}

	/**
	 * This private method is used by other public overloaded methods in this
	 * class to return the no of events in response for a particular url
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	private static int getJsonResponseCount(String request) throws Exception {
		JsonResponse jResponse = _jsonClient.execute(request);
		int entriesCount = FVTUtilsWithDate
				.getJsonResponseEntriesCount(jResponse);
		return entriesCount;
	}
}
