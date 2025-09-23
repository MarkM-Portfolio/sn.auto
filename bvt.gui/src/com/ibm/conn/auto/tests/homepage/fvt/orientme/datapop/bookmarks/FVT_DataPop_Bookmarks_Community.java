package com.ibm.conn.auto.tests.homepage.fvt.orientme.datapop.bookmarks;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseDogear;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.tests.homepage.fvt.orientme.configs.DataPopSetup;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.DogearBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityBookmarkEvents;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.lconn.automation.framework.services.common.nodes.Bookmark;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2017                                          */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * @author 	Anthony Cox
 * Date:	27th February 2017
 */

public class FVT_DataPop_Bookmarks_Community extends DataPopSetup {

	private APICommunitiesHandler communitiesAPIUser1;
	private BaseCommunity baseCommunity;
	private Community publicCommunity;
	private User testUser1;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		setFilename(getClass().getSimpleName());
		
		setListOfStandardUsers(1);
		testUser1 = listOfStandardUsers.get(0);
		getTestCaseData().addUserAssignmentData(listOfStandardUsers);
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);
		
		// User 1 will now create a public community
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunity = CommunityEvents.createNewCommunity(baseCommunity, testUser1, communitiesAPIUser1);
		getTestCaseData().addCreateCommunityData(publicCommunity, baseCommunity);
	}
	
	@Test(groups = {"fvt_orientme_onprem", "fvt_orientme_cloud"})
	public void datapop_Bookmark_Create() {
		
		// User 1 will now create a public bookmark
		BaseDogear baseBookmark = DogearBaseBuilder.buildCommunityBaseDogear(getClass().getSimpleName() + Helper.genStrongRand(), Data.getData().Tv3URL, baseCommunity);
		CommunityBookmarkEvents.createBookmark(publicCommunity, baseBookmark, testUser1, communitiesAPIUser1);
	}
	
	@Test(groups = {"fvt_orientme_onprem", "fvt_orientme_cloud"})
	public void datapop_Bookmark_Update() {
		
		// User 1 will now create a public bookmark
		BaseDogear baseBookmark = DogearBaseBuilder.buildCommunityBaseDogear(getClass().getSimpleName() + Helper.genStrongRand(), Data.getData().skyURL, baseCommunity);
		Bookmark communityBookmark = CommunityBookmarkEvents.createBookmark(publicCommunity, baseBookmark, testUser1, communitiesAPIUser1);
		
		// User 1 will now edit the description of the bookmark
		String editedDescription = Data.getData().commonDescription + Helper.genStrongRand();
		CommunityBookmarkEvents.editBookmarkDescription(communityBookmark, editedDescription, testUser1, communitiesAPIUser1);
	}
}
