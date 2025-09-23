/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2014                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
package com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.population;

import java.io.IOException;
import java.util.logging.Logger;

import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.json.simple.parser.ParseException;

import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.testsWithDate.ASCustomListAPIBVTTest;
import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.PopStringConstantsAS;
import com.ibm.lconn.automation.framework.activitiesStreamSearch.utils.nodes.FvtMasterLogsClass;
import com.ibm.lconn.automation.framework.services.common.LCServiceException;
import com.ibm.lconn.automation.framework.services.common.ProfileData;
import com.ibm.lconn.automation.framework.services.common.ProfileLoader;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Permissions;
import com.ibm.lconn.automation.framework.services.communities.CommunitiesService;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.opensocial.ASCustomListService;
import com.ibm.lconn.automation.framework.services.opensocial.ASCustomListServiceRequest;

/**
 * This helper class is used by API BVT Test class in setting up configuration,
 * it also interacts with service to perform the operations on custom
 * lists/items.
 * 
 * @author Raza Naqui
 * @version 5.0
 */
public class ASCustomListPopulationHelper extends PopulationHelper {

	protected static Logger LOGGER = FvtMasterLogsClass.LOGGER;
	private static ASCustomListService aSCustomListService;
	private static CommunitiesService communitiesService;
	private static String URLForRestRequest;
	private static ASCustomListPopulationHelper instance = null;
	private static Community sampleCommunity;
	public static String customListIdForPerson;
	public static String customListIdForCommunity;
	public static String customListIdForPersonAndCommunity;
	private static String communityId;
	public static final String TIMESTAMP = PopStringConstantsAS.eventIdent;

	private ASCustomListPopulationHelper() {
	}

	public static ASCustomListPopulationHelper getInstance() {
		if (instance == null) {
			return new ASCustomListPopulationHelper();
		}
		return instance;
	}

	private String createCustomList(String type) throws IOException,
			ParseException {
		String sourceMethod = "createCustomList";
		LOGGER.entering(ASCustomListAPIBVTTest.class.getName(), sourceMethod);
		String content = "[{\"summary\": \"Custom List for "
				+ type
				+ "\",\"objectType\": \"list\",\"displayName\": \"Custom List Display Name for "
				+ type + " - " + TIMESTAMP + "\"}]";

		ASCustomListServiceRequest request = new ASCustomListServiceRequest();
		request.setUrl(URLForRestRequest);
		request.setContent(content);

		String customListId = aSCustomListService.createCustomList(request);
		LOGGER.exiting(ASCustomListAPIBVTTest.class.getName(), sourceMethod);
		return customListId;
	}

	private String createCustomListItem(String listId, String type)
			throws Exception {
		String sourceMethod = "createCustomListItem";
		LOGGER.entering(ASCustomListAPIBVTTest.class.getName(), sourceMethod);
		String externalId;
		if (type.equals("person")) {
			ProfileData profData = ProfileLoader.getProfile(2);// ajones242
			externalId = profData.getUserId();
		} else {
			externalId = communityId;
		}

		String URLForCustomListItem = URLForRestRequest + "/" + listId;
		String content = "[{\"id\": \"urn:lsid:lconn.ibm.com:communities.community:"
				+ externalId
				+ "\", \"objectType\": \""
				+ type
				+ "\",\"displayName\": \"Custom List Item Display Name - "
				+ TIMESTAMP
				+ "\",\"name\":\"Custom List Item Name\",\"summary\":\"Custom List Item for Automation\"}]";

		ASCustomListServiceRequest serviceRequest = new ASCustomListServiceRequest();
		serviceRequest.setUrl(URLForCustomListItem);
		serviceRequest.setContent(content);

		String customListItem = aSCustomListService
				.createCustomListItem(serviceRequest);

		LOGGER.exiting(ASCustomListAPIBVTTest.class.getName(), sourceMethod);
		return customListItem;
	}

	public int getResponseCode() {
		return aSCustomListService.getRespStatus();
	}

	public void populate() {
		try {
			int userIndexToLogin = 3;
			setIndexForUserToLogin(userIndexToLogin);
			setup();
			customListIdForPerson = createCustomList("person");
			createCustomListItem(customListIdForPerson, "person");

			communityId = ASCommunitiesPopulationHelper.getPublicCommunity()
					.getUuid();

			customListIdForCommunity = createCustomList("community");
			createCustomListItem(customListIdForCommunity, "community");

			customListIdForPersonAndCommunity = createCustomList("person and community");
			createCustomListItem(customListIdForPersonAndCommunity, "person");
			createCustomListItem(customListIdForPersonAndCommunity, "community");

		} catch (Exception e) {
			LOGGER.fine("Exception in populate method: " + e.getMessage());
		}
	}

	
	private String createCommunity(final String title, final String content,
			final Permissions permission, final String tag) {
		Community newCommunity = new Community(title, content, permission, tag);

		Entry communityResult = (Entry) communitiesService
				.createCommunity(newCommunity);
		String communityEditLink = communityResult.getEditLinkResolvedHref()
				.toString();
		ExtensibleElement communityElement = communitiesService
				.getCommunity(communityEditLink);
		sampleCommunity = new Community((Entry) communityElement);
		assert (sampleCommunity != null);
		String communityId = sampleCommunity.getUuid();
		return communityId;
	}

	@SuppressWarnings("unused")
	private void deleteCommunity(Community createdCommunity) {
		assert (createdCommunity != null);
		boolean isDeleted = communitiesService.deleteCommunity(createdCommunity
				.getEditLink());
		assert (isDeleted);
	}

	protected void setUpServiceConfig() {

		ServiceEntry opensocial = config.getService("opensocial");
		assert (opensocial != null);
		aSCustomListService = new ASCustomListService(client, opensocial);
		//assert (aSCustomListService.isFoundService());

		ServiceEntry communities = config.getService("communities");
		assert (opensocial != null);

		try {
			communitiesService = new CommunitiesService(client, communities);
		} catch (LCServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assert (communitiesService.isFoundService());

		URLForRestRequest = opensocial.getSslHrefString() + "/rest/customlist/"
				+ "@me";
	}
}
