package com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils;

/*
 * 16.04.2012 - Yakov Vilenchik
 * Add possibility to use server name with https/http - To allow users to use the same server name as for population. 
 * The parameter https/http will be stripped as for the tests should be used server name as: lc4yakov401.haifa.ibm.com
 */

import static org.testng.AssertJUnit.assertTrue;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.apache.abdera.model.Entry;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.abdera.protocol.client.RequestOptions;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.ibm.lconn.automation.framework.activitiesStreamSearch.utils.json.JsonClient;
import com.ibm.lconn.automation.framework.activitiesStreamSearch.utils.json.JsonResponse;
import com.ibm.lconn.automation.framework.services.common.ProfileData;
import com.ibm.lconn.automation.framework.services.common.ProfileLoader;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.profiles.admin.GetUserID;

//FvtUtils
public class FVTUtilsWithDate {
	private static JsonClient _jsonClient;
	protected static String urlPrefix = "/connections/opensocial/basic/rest/activitystreams";
	protected static String requestToExecute;
	protected final static Logger LOGGER = Logger.getLogger("Test Logger");
	static int maxEntriesCount = 10;
	
	
	static {
		

			ProfileData profData=null;
			try {
				profData = ProfileLoader.getProfile(2);
			
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			PopStringConstantsAS.setLoginUserName(profData.getEmail());
			PopStringConstantsAS.setLoginUserPwd(profData.getPassword());
			PopStringConstantsAS.setLoginUserRealName(profData.getRealName());
			PopStringConstantsAS.setTestUserName(profData.getUserName());
			PopStringConstantsAS.setTestUserPwd(profData.getPassword());
			PopStringConstantsAS.SERVER_URL = URLConstants.SERVER_URL;
		
		
		try {
			
				_jsonClient = new JsonClient(profData.getEmail(),
						profData.getPassword(), URLConstants.SERVER_URL);
			
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	
	public static String createRequestURL(String requestURL,
			String searchString, String filterWord)  {
		String newRequestURL = null;
		
		
		newRequestURL = requestURL
				+ searchString
				+ filterWord
				+ "&count="
				+ PopStringConstantsAS.RECEIVED_PAGE_SIZE
				+ createDateFilterString(DateFilter.instance()
						.getDateFilterParam());
		
		
		return newRequestURL;
	}

	
	public static String createRequestURLWithIndex(String requestURL,
			String newStartIndex) {
		String newRequestURL = null;
		if (newStartIndex != null) {
			newRequestURL = requestURL + newStartIndex;
			
		}
		return newRequestURL;
	}

	
	public static String createRequestToSend(String requestURL,
			String queryString) {
		String newRequestURL = null;
		newRequestURL = requestURL
				+ queryString
				+ "&count="
				+ PopStringConstantsAS.RECEIVED_PAGE_SIZE
				+ createDateFilterString(DateFilter.instance()
						.getDateFilterParam());
		
		return newRequestURL;
	}

	
	public static String createRequestToSendWithParameter(String requestURL,
			String queryString, String parameter) {
		String newRequestURL = null;
		newRequestURL = requestURL
				+ queryString
				+ parameter
				+ "&count="
				+ PopStringConstantsAS.RECEIVED_PAGE_SIZE
				+ createDateFilterString(DateFilter.instance()
						.getDateFilterParam());
		
		return newRequestURL;
	}

	
	public static String createRequestToSendWithFilter(String requestURL,
			String queryString, String filterQuery)
			throws UnsupportedEncodingException {
		String newRequestURL = null;
		newRequestURL = requestURL
				+ queryString
				+ URLEncoder.encode(filterQuery, "UTF-8")
				+ "&count="
				+ PopStringConstantsAS.RECEIVED_PAGE_SIZE
				+ createDateFilterString(DateFilter.instance()
						.getDateFilterParam());
		return newRequestURL;
	}

	
	public static String createRequestToSendWithFilter1(String requestURL,
			String queryString, String filterString, String filterQuery)
			throws UnsupportedEncodingException {
		String newRequestURL = null;
		newRequestURL = requestURL
				+ queryString
				+ filterString
				+ URLEncoder.encode(filterQuery, "UTF-8")
				+ "&count="
				+ PopStringConstantsAS.RECEIVED_PAGE_SIZE
				+ createDateFilterString(DateFilter.instance()
						.getDateFilterParam());
		return newRequestURL;
	}

	
	public static String createRequestToSendWithFacet(String requestURL,
			String queryString, String facetQuery, String facetParam)
			throws UnsupportedEncodingException {
		String newRequestURL = null;
		newRequestURL = requestURL
				+ queryString
				+ facetQuery
				+ URLEncoder.encode(facetParam, "UTF-8")
				+ "&count="
				+ PopStringConstantsAS.RECEIVED_PAGE_SIZE
				+ "&query="
				+ PopStringConstantsAS.ASSEARCH_SUFFIX
				+ createDateFilterString(DateFilter.instance()
						.getDateFilterParam());
		return newRequestURL;
	}

	// **********************************************************************************************************************
	// **************************Methods for requestURL list
	// creation*************************************************************
	// **********************************************************************************************************************
// Method to create all requests URL list for further execution
	public static ArrayList<String> createRequestsList(String requestURL,
			String[] requestsStringArray, String[] filtersArray)  {
		
		ArrayList<String> totalRequestsArrayList = new ArrayList<String>();

		
		for (int i = 0; i < filtersArray.length; i++) {
			for (int j = 0; j < requestsStringArray.length; j++) {
				totalRequestsArrayList.add(createRequestURL(requestURL,
						requestsStringArray[j], filtersArray[i]));
			}
		}
		return totalRequestsArrayList;
	}

	// *************************************************************************************************************************
	// *************************************************************************************************************************
	// *************************************************************************************************************************

	
	public static int sendRequestAndGetResult(String requestURLToExecute,
			String eventType, String eventTypeLoc, String eventTitleLoc,
			String title) throws Exception {
		
		int totalResult = 0;
		JsonResponse js;
		
		js = getJsonResponse(requestURLToExecute);
		int entriesNumber = 0;
		if (js != null){
		entriesNumber = getJsonResponseEntriesCount(js);
		if (entriesNumber != 0){
			
		int receviedResult = checkJsonResponseForEventExistance(js, eventType,
				eventTypeLoc, eventTitleLoc, title);
		totalResult += receviedResult;
		}
		}else {
			return totalResult;
		}
		
		int index = 2;
		int nextReceviedResult;
		while (entriesNumber == PopStringConstantsAS.RECEIVED_PAGE_SIZE) {
			
			String startIndex = "&startIndex=" + index;
			
			String newRequestURL = createRequestURLWithIndex(
					requestURLToExecute, startIndex);
			js = null;
			nextReceviedResult=0;
			js = getJsonResponse(newRequestURL);
			if (js != null){
			entriesNumber = getJsonResponseEntriesCount(js);
			if (entriesNumber != 0){
			nextReceviedResult = checkJsonResponseForEventExistance(js,
					eventType, eventTypeLoc, eventTitleLoc, title);
			totalResult += nextReceviedResult;
			index++;
			}
			}else {
				break;
			}
		}
		
		return totalResult;
	}

	
	public static int sendRequestAndGetResultMultiEventTypes(
			String requestURLToExecute, String eventType, String eventType2,
			String eventTypeLoc, String eventTitleLoc, String title)
			throws Exception {
		
		int totalResult = 0;
		JsonResponse js;
		
		js = getJsonResponse(requestURLToExecute);
		int entriesNumber = 0;
		if (js != null){
		entriesNumber = getJsonResponseEntriesCount(js);
		if (entriesNumber != 0){
			
		int receviedResult = checkJsonResponseForEventExistanceMultiEventTypes(
				js, eventType, eventType2, eventTypeLoc, eventTitleLoc, title);
		totalResult += receviedResult;
		}
		}else {
			return totalResult;
		}
		
		int index = 2;
		int nextReceviedResult;
		while (entriesNumber == PopStringConstantsAS.RECEIVED_PAGE_SIZE) {
			
			String startIndex = "&startIndex=" + index;
			
			String newRequestURL = createRequestURLWithIndex(
					requestURLToExecute, startIndex);
			js = null;
			nextReceviedResult=0;
			js = getJsonResponse(newRequestURL);
			if (js != null){
			entriesNumber = getJsonResponseEntriesCount(js);
			if (entriesNumber != 0){
			 nextReceviedResult = checkJsonResponseForEventExistanceMultiEventTypes(
					js, eventType, eventType2, eventTypeLoc, eventTitleLoc,
					title);
			 totalResult += nextReceviedResult;
				index++;
				}
				}else {
					break;
				}
			}
			
			return totalResult;
	}

	
	static int checkJsonResponseForEventExistance(JsonResponse jResponse,
			String eventType, String eventTypeLoc, String eventTitleLoc,
			String title) throws Exception {
		
		int returnedResultFromRequest = 0;
		int entriesNumber = getJsonResponseEntriesCount(jResponse);
		

		if (entriesNumber > 0) {
			returnedResultFromRequest = checkEventExistance(jResponse,
					eventType, eventTypeLoc, eventTitleLoc, title);
			
		} 
		return returnedResultFromRequest;
	}

	
	 static int checkJsonResponseForEventExistanceMultiEventTypes(
			JsonResponse jResponse, String eventType, String eventType2,
			String eventTypeLoc, String eventTitleLoc, String title)
			throws Exception {
		
		int returnedResultFromRequest = 0;
		int entriesNumber = getJsonResponseEntriesCount(jResponse);
		

		if (entriesNumber > 0) {
			returnedResultFromRequest = checkEventExistanceMultiEventTypes(
					jResponse, eventType, eventType2, eventTypeLoc,
					eventTitleLoc, title);
			
		} 
		return returnedResultFromRequest;
	}

	
	 static int checkEventExistance(JsonResponse jResponse,
			String eventType, String eventTypeLoc, String eventTitleLoc,
			String stringToCheck) throws Exception {
		int entriesCount = getJsonResponseEntriesCount(jResponse);
		
		int titleFound = 0;
		for (int i = 0; i < entriesCount; i++) {
			String receivedEventType = jResponse
					.findValue(PopStringConstantsAS.JSON_ROOT_ELEMENT
							+ PopStringConstantsAS.JSON_ROOT_ENTRY + "[" + i
							+ "]." + eventTypeLoc);
			
			if (receivedEventType.equals(eventType)) {
				String receivedEventTitle = jResponse
						.findValue(PopStringConstantsAS.JSON_ROOT_ELEMENT
								+ PopStringConstantsAS.JSON_ROOT_ENTRY + "["
								+ i + "]." + eventTitleLoc);
				
				
				if (receivedEventTitle.contains(stringToCheck)) {
					
					titleFound++;
				} 

			}
		}

		
		return titleFound;
	}

	
	private static int checkEventExistanceMultiEventTypes(
			JsonResponse jResponse, String eventType, String eventType2,
			String eventTypeLoc, String eventTitleLoc, String stringToCheck)
			throws Exception {
		
		int titleFound = 0;
		int entriesCount = getJsonResponseEntriesCount(jResponse);
		
		for (int i = 0; i < entriesCount; i++) {
			String receivedEventType = jResponse
					.findValue(PopStringConstantsAS.JSON_ROOT_ELEMENT
							+ PopStringConstantsAS.JSON_ROOT_ENTRY + "[" + i
							+ "]." + eventTypeLoc);
			
			if ((receivedEventType.equals(eventType))
					|| (receivedEventType.equals(eventType2))) {
				String receivedEventTitle = jResponse
						.findValue(PopStringConstantsAS.JSON_ROOT_ELEMENT
								+ PopStringConstantsAS.JSON_ROOT_ENTRY + "["
								+ i + "]." + eventTitleLoc);
				
				
				if (receivedEventTitle.contains(stringToCheck)) {
					
					titleFound++;
				} 

			}
		}

		
		return titleFound;
	}

	public static int checkMentionsEventExistanceByEventTypeAndMentionedPersonExtID(
			JsonResponse jResponse, String eventType1, String eventType2,
			String eventType3, String eventTypeLoc, String eventLoc,
			String stringToCheck, String searchedObject) throws Exception {
		int entriesCount = getJsonResponseEntriesCount(jResponse);
		
		int externalIdFound = 0;
		for (int i = 0; i < entriesCount; i++) {
			String receivedEventType = jResponse
					.findValue(PopStringConstantsAS.JSON_ROOT_ELEMENT
							+ PopStringConstantsAS.JSON_ROOT_ENTRY + "[" + i
							+ "]." + eventTypeLoc);
			
			if ((receivedEventType.equals(eventType1))
					|| (receivedEventType.equals(eventType2))
					|| (receivedEventType.equals(eventType3))) {
				String receivedEvent = jResponse
						.findValue(PopStringConstantsAS.JSON_ROOT_ELEMENT
								+ PopStringConstantsAS.JSON_ROOT_ENTRY + "["
								+ i + "]." + eventLoc);
				
				if (receivedEvent.contains(stringToCheck)) {
					
					externalIdFound++;
				} 

			}
		}
		return externalIdFound;
	}

	public static void reportToLogTestResults(int expectedResult,
			int receivedResult, String searchPlace) {
		
			LOGGER.fine("expectedResult: " +expectedResult+" receivedResult: "+receivedResult+" in  searchPlace:" +searchPlace );
		
	}

	
	public static String getUserID(String urlToExecute, String usernameToSearch)
			throws Exception {
		int index = 2;
		JsonResponse js;
		int numberOfEntries = 0;
		String returnUserID = "";
		
		js = getJsonResponse(urlToExecute + "&count="
				+ PopStringConstantsAS.RECEIVED_PAGE_SIZE);
		if (js != null){
			numberOfEntries = getJsonResponseEntriesCount(js);
			for (int i = 0; i < numberOfEntries; i++) {
				String actorDNstr = js
						.findValue(PopStringConstantsAS.JSON_ROOT_ELEMENT
								+ PopStringConstantsAS.JSON_ROOT_ENTRY
								+ "["
								+ i
								+ "]."
								+ PopStringConstantsAS.ENTRY_USER_DISPLAY_NAME_LOCATION);
				if (actorDNstr.equals(usernameToSearch)) {
					returnUserID = js
							.findValue(PopStringConstantsAS.JSON_ROOT_ELEMENT
									+ PopStringConstantsAS.JSON_ROOT_ENTRY
									+ "["
									+ i
									+ "]."
									+ PopStringConstantsAS.ENTRY_USER_USER_ID_LOCATION);
					
					return returnUserID;
				}
			}
		
		}
		
			while ((numberOfEntries == PopStringConstantsAS.RECEIVED_PAGE_SIZE)
					&& (index <= maxEntriesCount)) {
				js = null;
				String startIndex = "&startIndex=" + index;
				String newRequestURL = createRequestURLWithIndex(urlToExecute,
						startIndex);
				
				js = getJsonResponse(newRequestURL
						+ "&count=" + PopStringConstantsAS.RECEIVED_PAGE_SIZE);
				if (js != null){
				numberOfEntries = getJsonResponseEntriesCount(js);
				for (int i = 0; i < numberOfEntries; i++) {
					String actorDNstr = js
							.findValue(PopStringConstantsAS.JSON_ROOT_ELEMENT
									+ PopStringConstantsAS.JSON_ROOT_ENTRY
									+ "["
									+ i
									+ "]."
									+ PopStringConstantsAS.ENTRY_USER_DISPLAY_NAME_LOCATION);
					if (actorDNstr.equals(usernameToSearch)) {
						returnUserID = js
								.findValue(PopStringConstantsAS.JSON_ROOT_ELEMENT
										+ PopStringConstantsAS.JSON_ROOT_ENTRY
										+ "["
										+ i
										+ "]."
										+ PopStringConstantsAS.ENTRY_USER_USER_ID_LOCATION);
						
						return returnUserID;
					}
				}
				}
				index++;
			}
	
			
	
		return returnUserID;
	}

	public static String getUserId(String userName) {
		boolean useSSL = true;
		String userID = "";
		String userIDPrefix = "urn:lsid:lconn.ibm.com:profiles.person:";
		if (StringConstants.DEPLOYMENT_TYPE != StringConstants.DeploymentType.SMARTCLOUD) {

			userID = GetUserID.getUserID(userName, useSSL);
		} else {
			userID =  _jsonClient.GetUserIdOnCloud();
		}
		

		
		
		
		return userIDPrefix + userID;
	}

	// **********************************************************************************************************************
	// **************************New general methods for basic work with
	// JSON************************************************
	// **********************************************************************************************************************

	// return JSON response according to the request URL
	public static JsonResponse getJsonResponse(String requestURLToExecute)
			throws Exception {
		
		JsonResponse js = _jsonClient.execute(requestURLToExecute);
		return js;
	}

	
	public static Boolean checkJsonValidity(JsonResponse jResponse) {
		
		
		if (jResponse != null) {
			
			return true;
		} 
			
			return false;
		
	}

	
	public static int getJsonResponseEntriesCount(JsonResponse jResponse) {
		
		int entriesNumber = jResponse
				.count(PopStringConstantsAS.JSON_ROOT_ENTRY);
		return entriesNumber;
	}

	public static int getTotalEntriesCount(String requestURLToExecute)
			throws Exception {
		int totalCount = 0;
		JsonResponse js;
		js = getJsonResponse(requestURLToExecute);
		if (js == null){
			return totalCount;
		}
		int entriesNumber = getJsonResponseEntriesCount(js);
		totalCount += entriesNumber;
		int index = 2;
		while (entriesNumber == PopStringConstantsAS.RECEIVED_PAGE_SIZE) {
			js = null;
			String startIndex = "&startIndex=" + index;
			
			String newRequestURL = createRequestURLWithIndex(
					requestURLToExecute, startIndex);
			js = getJsonResponse(newRequestURL);
			if (js == null){
				return totalCount;
			}
			entriesNumber = getJsonResponseEntriesCount(js);
			
			totalCount += entriesNumber;
			index++;
		}
		
		return totalCount;
	}

	
	public static ArrayList<String> getJsonResponseValuesList(
			JsonResponse jResponse, String checkString) {
		
		ArrayList<String> listOfValues = jResponse.getAllValues(checkString);
		return listOfValues;
	}

	
	@SuppressWarnings("unused")
	public static ArrayList<String> getJsonResponseEntriesList(String request)
			throws Exception {
		ArrayList<String> entryContentList = new ArrayList<String>();
		ArrayList<String> totalEntriesContentlist = new ArrayList<String>();
		String entryContent;
		int index = 2;
		JsonResponse jResponse;
		jResponse = _jsonClient.execute(request);
		if (jResponse == null){
			return totalEntriesContentlist;
		}
		int entriesCount = getJsonResponseEntriesCount(jResponse);

		
		
			entryContentList.clear();
			for (int i = 0; i < entriesCount; i++) {
				entryContent = jResponse.find(
						PopStringConstantsAS.JSON_ROOT_ELEMENT
								+ PopStringConstantsAS.JSON_ROOT_ENTRY + "["
								+ i + "]").toString();
				if (entryContent.contains(PopStringConstantsAS.ASSEARCH_SUFFIX)) {
					
					entryContentList.add(entryContent);
				} 
			}
			totalEntriesContentlist.addAll(entryContentList);
			while (entriesCount == PopStringConstantsAS.RECEIVED_PAGE_SIZE) {
				jResponse = null;
				String startIndex = "&startIndex=" + index;
				String newRequestURL = createRequestURLWithIndex(request,
						startIndex);
				jResponse = getJsonResponse(newRequestURL);
				if (jResponse == null){
					return totalEntriesContentlist;
				}
				entriesCount = getJsonResponseEntriesCount(jResponse);
				
				entryContentList.clear();
				for (int i = 0; i < entriesCount; i++) {
					entryContent = jResponse.find(
							PopStringConstantsAS.JSON_ROOT_ELEMENT
									+ PopStringConstantsAS.JSON_ROOT_ENTRY
									+ "[" + i + "]").toString();
					if (entryContent
							.contains(PopStringConstantsAS.ASSEARCH_SUFFIX)) {
						
						entryContentList.add(entryContent);
					} 
				}
				totalEntriesContentlist.addAll(entryContentList);
				index++;
			}
		

		
		return totalEntriesContentlist;

	}

	
	@SuppressWarnings("unused")
	public static ArrayList<String> getJsonResponseEntriesListNotContainWord(
			String request, String wordNotToContain) throws Exception {
		ArrayList<String> entryContentList = new ArrayList<String>();
		ArrayList<String> totalEntriesContentlist = new ArrayList<String>();
		String entryContent;
		int index = 2;
		JsonResponse jResponse;
		
		jResponse = _jsonClient.execute(request);
		
		if (jResponse == null){
			return totalEntriesContentlist;
		}
		int entriesCount = getJsonResponseEntriesCount(jResponse);

		
		
			entryContentList.clear();
			for (int i = 0; i < entriesCount; i++) {
				entryContent = jResponse.find(
						PopStringConstantsAS.JSON_ROOT_ELEMENT
								+ PopStringConstantsAS.JSON_ROOT_ENTRY + "["
								+ i + "]").toString();
				if ((entryContent.contains(PopStringConstantsAS.ASSEARCH_SUFFIX)) && (!entryContent.contains(wordNotToContain))){
					
						entryContentList.add(entryContent);
					
				} 
			}
			totalEntriesContentlist.addAll(entryContentList);
			while (entriesCount == PopStringConstantsAS.RECEIVED_PAGE_SIZE) {
				jResponse = null;
				String startIndex = "&startIndex=" + index;
				String newRequestURL = createRequestURLWithIndex(request,
						startIndex);
				jResponse = getJsonResponse(newRequestURL);
				if (jResponse == null){
					return totalEntriesContentlist;
				}
				entriesCount = getJsonResponseEntriesCount(jResponse);
				
				entryContentList.clear();
				for (int i = 0; i < entriesCount; i++) {
					entryContent = jResponse.find(
							PopStringConstantsAS.JSON_ROOT_ELEMENT
									+ PopStringConstantsAS.JSON_ROOT_ENTRY
									+ "[" + i + "]").toString();
					if ((entryContent.contains(PopStringConstantsAS.ASSEARCH_SUFFIX)) && (!entryContent.contains(wordNotToContain))){
						
						entryContentList.add(entryContent);
					
				} 
				}
				totalEntriesContentlist.addAll(entryContentList);
				index++;
			}
		
		
		return totalEntriesContentlist;

	}

	
	@SuppressWarnings("unused")
	public static ArrayList<String> getJsonResponseEntriesElementList(
			String request, String elementToSearch) throws Exception {
		ArrayList<String> entryContentList = new ArrayList<String>();
		ArrayList<String> totalEntriesContentlist = new ArrayList<String>();
		String entryTitle;
		String entryContent;
		int index = 2;
		JsonResponse jResponse;
		jResponse = _jsonClient.execute(request);
		if (jResponse == null){
			return totalEntriesContentlist;
		}
		int entriesCount = getJsonResponseEntriesCount(jResponse);

		
		
			entryContentList.clear();
			for (int i = 0; i < entriesCount; i++) {
				entryTitle = jResponse
						.findValue(
								PopStringConstantsAS.JSON_ROOT_ELEMENT
										+ PopStringConstantsAS.JSON_ROOT_ENTRY
										+ "["
										+ i
										+ "]."
										+ PopStringConstantsAS.TITLE_LOCATION_FOR_SEARCH)
						.toString();
				if (entryTitle.contains(PopStringConstantsAS.ASSEARCH_SUFFIX)) {
					entryContent = jResponse.find(
							PopStringConstantsAS.JSON_ROOT_ELEMENT
									+ PopStringConstantsAS.JSON_ROOT_ENTRY
									+ "[" + i + "]" + elementToSearch)
							.toString();
					
					entryContentList.add(entryContent);
				} 
			}
			totalEntriesContentlist.addAll(entryContentList);
			while (entriesCount == PopStringConstantsAS.RECEIVED_PAGE_SIZE) {
				jResponse = null;
				String startIndex = "&startIndex=" + index;
				String newRequestURL = createRequestURLWithIndex(request,
						startIndex);
				jResponse = getJsonResponse(newRequestURL);
				if (jResponse == null){
					return totalEntriesContentlist;
				}
				entriesCount = getJsonResponseEntriesCount(jResponse);
				
				entryContentList.clear();
				for (int i = 0; i < entriesCount; i++) {
					entryTitle = jResponse
							.findValue(
									PopStringConstantsAS.JSON_ROOT_ELEMENT
											+ PopStringConstantsAS.JSON_ROOT_ENTRY
											+ "["
											+ i
											+ "]."
											+ PopStringConstantsAS.TITLE_LOCATION_FOR_SEARCH)
							.toString();
					if (entryTitle
							.contains(PopStringConstantsAS.ASSEARCH_SUFFIX)) {
						entryContent = jResponse.find(
								PopStringConstantsAS.JSON_ROOT_ELEMENT
										+ PopStringConstantsAS.JSON_ROOT_ENTRY
										+ "[" + i + "]" + elementToSearch)
								.toString();
						
						entryContentList.add(entryContent);
					} 
				}
				totalEntriesContentlist.addAll(entryContentList);
				index++;
			}
		
		
		return totalEntriesContentlist;

	}

	
	@SuppressWarnings("unused")
	public static ArrayList<String> getJsonResponseEntriesTargetPersonList(
			String request, String elementToSearch, String elementToSearch2,
			String elementToSearch3, String element3Value) throws Exception {
		ArrayList<String> entryContentList = new ArrayList<String>();
		ArrayList<String> totalEntriesContentlist = new ArrayList<String>();
		String entryTitle;
		String entryContent;
		int index = 2;
		JsonResponse jResponse;
		jResponse = _jsonClient.execute(request);
		if (jResponse == null){
			return totalEntriesContentlist;
		}
		int entriesCount = getJsonResponseEntriesCount(jResponse);

		
		
			entryContentList.clear();
			for (int i = 0; i < entriesCount; i++) {
				if (jResponse.find(PopStringConstantsAS.JSON_ROOT_ELEMENT
						+ PopStringConstantsAS.JSON_ROOT_ENTRY + "[" + i + "]."
						+ elementToSearch3) != null) {
					if (jResponse
							.find(PopStringConstantsAS.JSON_ROOT_ELEMENT
									+ PopStringConstantsAS.JSON_ROOT_ENTRY
									+ "[" + i + "]." + elementToSearch3)
							.toString().equals(element3Value)) {
						entryTitle = jResponse.findValue(
								PopStringConstantsAS.JSON_ROOT_ELEMENT
										+ PopStringConstantsAS.JSON_ROOT_ENTRY
										+ "[" + i + "]." + elementToSearch)
								.toString();
						if (entryTitle
								.contains(PopStringConstantsAS.ASSEARCH_SUFFIX)) {
							entryContent = jResponse
									.find(PopStringConstantsAS.JSON_ROOT_ELEMENT
											+ PopStringConstantsAS.JSON_ROOT_ENTRY
											+ "[" + i + "]." + elementToSearch2)
									.toString();
							
							entryContentList.add(entryContent);
						} 
					} 
				} 
			}
			totalEntriesContentlist.addAll(entryContentList);
			while (entriesCount == PopStringConstantsAS.RECEIVED_PAGE_SIZE) {
				jResponse = null;
				String startIndex = "&startIndex=" + index;
				String newRequestURL = createRequestURLWithIndex(request,
						startIndex);
				jResponse = getJsonResponse(newRequestURL);
				if (jResponse == null){
					return totalEntriesContentlist;
				}
				entriesCount = getJsonResponseEntriesCount(jResponse);
				
				entryContentList.clear();
				for (int i = 0; i < entriesCount; i++) {
					if (jResponse
							.find(PopStringConstantsAS.JSON_ROOT_ELEMENT
									+ PopStringConstantsAS.JSON_ROOT_ENTRY
									+ "[" + i + "]." + elementToSearch3) != null) {
						if (jResponse
								.find(PopStringConstantsAS.JSON_ROOT_ELEMENT
										+ PopStringConstantsAS.JSON_ROOT_ENTRY
										+ "[" + i + "]." + elementToSearch3)
								.toString().equals(element3Value)) {
							entryTitle = jResponse
									.findValue(
											PopStringConstantsAS.JSON_ROOT_ELEMENT
													+ PopStringConstantsAS.JSON_ROOT_ENTRY
													+ "["
													+ i
													+ "]."
													+ elementToSearch)
									.toString();
							if (entryTitle
									.contains(PopStringConstantsAS.ASSEARCH_SUFFIX)) {
								entryContent = jResponse
										.find(PopStringConstantsAS.JSON_ROOT_ELEMENT
												+ PopStringConstantsAS.JSON_ROOT_ENTRY
												+ "["
												+ i
												+ "]."
												+ elementToSearch2).toString();
								
								entryContent.replaceAll("[\"\"]", "");
								entryContentList.add(entryContent);
							} 
						} 
					} 
				}
				totalEntriesContentlist.addAll(entryContentList);
				index++;
			}
		
		

		
		return totalEntriesContentlist;
	}

	
	public static ArrayList<String> getJsonResponseEntriesElementListForAccentWords(
			String request, String elementToSearch) throws Exception {
		ArrayList<String> entryContentList = new ArrayList<String>();
		ArrayList<String> totalEntriesContentlist = new ArrayList<String>();
		int index = 2;
		JsonResponse jResponse;
		jResponse = _jsonClient.execute(request);
		if (jResponse == null){
			return totalEntriesContentlist;
		}
		int entriesCount = getJsonResponseEntriesCount(jResponse);

		
		entryContentList.clear();
		for (int i = 0; i < entriesCount; i++) {
			String entryContent = jResponse.find(
					PopStringConstantsAS.JSON_ROOT_ELEMENT
							+ PopStringConstantsAS.JSON_ROOT_ENTRY + "[" + i
							+ "]" + elementToSearch).toString();
			entryContentList.add(entryContent);
		}
		totalEntriesContentlist.addAll(entryContentList);
		while (entriesCount == PopStringConstantsAS.RECEIVED_PAGE_SIZE) {
			jResponse = null;
			String startIndex = "&startIndex=" + index;
			String newRequestURL = createRequestURLWithIndex(request,
					startIndex);
			jResponse = getJsonResponse(newRequestURL);
			if (jResponse == null){
				return totalEntriesContentlist;
			}
			entriesCount = getJsonResponseEntriesCount(jResponse);
			
			entryContentList.clear();
			for (int i = 0; i < entriesCount; i++) {
				String entryContent = jResponse.find(
						PopStringConstantsAS.JSON_ROOT_ELEMENT
								+ PopStringConstantsAS.JSON_ROOT_ENTRY + "["
								+ i + "]" + elementToSearch).toString();
				entryContentList.add(entryContent);
			}
			totalEntriesContentlist.addAll(entryContentList);
			index++;
		}
		return totalEntriesContentlist;

	}

	
	public static Boolean checkTestResult(int expectedResult,
			int receivedResult, String requestToExecute, String eventName) {
		boolean testResult = false;
		if (((expectedResult == 0) && (receivedResult == 0))
				|| ((expectedResult == 1) && (receivedResult >= 1))) {
			testResult = true;
		} 
		
			assertTrue("eventName: " + eventName + " expectedResult: "
					+ expectedResult + " receivedResult: " + receivedResult
					+ "\n" + "requestToExecute: " + requestToExecute,testResult);
		
		return testResult;
	}

	
	public static Boolean checkTestResult1(int expectedResult,
			int receivedResult) {
		boolean testResult = false;
		 if (expectedResult <= receivedResult) {
			testResult = true;
		}
		return testResult;
	}

	// ###############################################################################################
	// Method to search word in entry
	// ###############################################################################################

	public static Boolean searchWordInEntry(String entryToSearch,
			String wordToSearch) {
		
		if (entryToSearch.toLowerCase().toString()
				.contains(wordToSearch.toLowerCase())) {
			
			return true;
		} 
		return false;
	}

	// ##########################################################################################
	// Method to strip server name from http/https for the tests
	// ##########################################################################################
	public static String checkServerUrl(String serverUrl) {
		
		if (serverUrl.startsWith("http://")) {
			
			return serverUrl.substring(7);
			
		} 
		if (serverUrl.startsWith("https://")) {
			
			return serverUrl.substring(8);
			
		} 

			
		return serverUrl;
		
	}

	// method to get server domain name for AS Search crawler addCoockie method
	public static String getServerDomain(String serverUrl) throws Exception {
		String domain = null;
		URL url = new URL(serverUrl);
		String serverName = url.getHost();
		
		String hostName = serverUrl.substring(serverUrl.indexOf("://") + 3,
				serverUrl.indexOf('.'));
		
		int serverNameLength = hostName.length();
		
		domain = serverName.substring(serverNameLength + 1);
		return domain;
	}

	
	public static Boolean isUrlSecured(String serverUrl) throws Exception {
		
		URL url = new URL(serverUrl);
		
		if (url.getProtocol().equals("https")) {
			return  true;
		} 
		return false;
	}

	
	public static Integer getUrlPort(String serverUrl) throws Exception {
		
		URL url = new URL(serverUrl);
		return url.getPort();
		
	}

	
	public static String getServerHost(String serverUrl) throws Exception {
		
		URL url = new URL(serverUrl);
		return url.getHost();
		
	}

	
	public static String getServerHostName(String serverUrl) throws IOException {
		
		
		return serverUrl.substring(serverUrl.indexOf("://") + 3,
				serverUrl.indexOf('.'));
		
		
	}

	// ##########################################################################################
	// Method to check if entries are equals
	// ##########################################################################################

	public static Boolean compareEntriesFromTwoRequests(
			ArrayList<String> entriesList1, ArrayList<String> entriesList2) {
		int passedResult = 0;
		int failedResult = 0;
		boolean testResult;
		if (entriesList1.size() == entriesList2.size()) {
			
			for (int i = 0; i < entriesList1.size(); i++) {
				if (entriesList1.get(i).equals(entriesList2.get(i))) {
					passedResult++;
					
				} else {
					failedResult++;
					
				}
			}

			if (passedResult == entriesList1.size()) {
				testResult = true;
				
			} else {
				testResult = false;
				
			}
		} else {
			
			testResult = false;
		}
		return testResult;
	}

	// #########################################################################################
	// Method to send request to server and return clientresponse
	// #########################################################################################
	public static ClientResponse sendRequestToServerAndReturnResponse(
			AbderaClient client, String requestUrl, Entry entry,
			RequestOptions options) {
		ClientResponse clientResponse = client.post(requestUrl, entry, options);
		return clientResponse;
	}

	public static String createDateFilterString(String dateFilter) {
		String dateFilterString = null;
		if (dateFilter.startsWith("&dateFilter=%7B")) {
			
			dateFilterString = dateFilter;
		} else {
			try {
				
				dateFilter = URLDecoder.decode(dateFilter, "UTF-8");
				dateFilterString = dateFilter;
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return dateFilterString;

	}

	
	public static String createRequestUrl(String requestPrefix,
			String requestParts) {
		
		String request = requestPrefix
				+ requestParts
				+ PopStringConstantsAS.INDEX_SEARCH_PARAMETER
				+ "&count="
				+ PopStringConstantsAS.RECEIVED_PAGE_SIZE
				+ FVTUtilsWithDate.createDateFilterString(DateFilter.instance()
						.getDateFilterParam());
		
		return request;
	}

	
	public static String createRequestUrlWithIndex(String requestURL,
			String startIndex) {
		String request = requestURL + startIndex;
		
		return request;
	}

	
	public static String createRequestUrlWithFilter(String requestURL,
			String requestParts, String advancedFilter) {
		
		String request = requestURL
				+ requestParts
				+ PopStringConstantsAS.INDEX_SEARCH_PARAMETER
				+ advancedFilter
				+ "&count="
				+ PopStringConstantsAS.RECEIVED_PAGE_SIZE
				+ FVTUtilsWithDate.createDateFilterString(DateFilter.instance()
						.getDateFilterParam());
		
		return request;
	}

	
	public static String createRequestUrlWithUid(String requestURL,
			String UID, String requestParts) {
		String request = requestURL
				+ "/"
				+ UID
				+ requestParts
				+ PopStringConstantsAS.INDEX_SEARCH_PARAMETER
				+ "&count="
				+ PopStringConstantsAS.RECEIVED_PAGE_SIZE
				+ FVTUtilsWithDate.createDateFilterString(DateFilter.instance()
						.getDateFilterParam());
		
		return request;
	}

	
	public static String createRequestUrlWithUidWithFilter(String requestURL,
			String UID, String requestParts, String advancedFilter) {
		String request = requestURL
				+ "/"
				+ UID
				+ requestParts
				+ PopStringConstantsAS.INDEX_SEARCH_PARAMETER
				+ advancedFilter
				+ "&count="
				+ PopStringConstantsAS.RECEIVED_PAGE_SIZE
				+ FVTUtilsWithDate.createDateFilterString(DateFilter.instance()
						.getDateFilterParam());
		
		return request;
	}
	
	public static String readJSONTitle(String data) {
		String title="";
		try {
			JSONParser parse = new JSONParser();		
			JSONObject jObj = (JSONObject) parse.parse(data);
			
			title = jObj.get("title").toString();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return title;
	}

	


	

	

	
	
}
