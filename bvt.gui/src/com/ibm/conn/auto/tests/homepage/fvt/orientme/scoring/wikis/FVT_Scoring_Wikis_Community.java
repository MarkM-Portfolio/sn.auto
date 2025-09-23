package com.ibm.conn.auto.tests.homepage.fvt.orientme.scoring.wikis;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.conn.auto.tests.homepage.fvt.orientme.configs.DataPopSetup;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseWikiPage;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIWikisHandler;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.WikiBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityWikiEvents;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.wikis.nodes.Wiki;
import com.ibm.lconn.automation.framework.services.wikis.nodes.WikiComment;
import com.ibm.lconn.automation.framework.services.wikis.nodes.WikiPage;

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
 * Date:	3rd March 2017
 */

public class FVT_Scoring_Wikis_Community extends DataPopSetup {

	private APICommunitiesHandler communitiesAPIUser1;
	private APIWikisHandler wikisAPIUser1, wikisAPIUser2, wikisAPIUser3;
	private User testUser1, testUser2, testUser3;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		setFilename(getClass().getSimpleName());
		
		setListOfStandardUsers(3);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		testUser3 = listOfStandardUsers.get(2);
		getTestCaseData().addUserAssignmentData(listOfStandardUsers);
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);
		
		wikisAPIUser1 = initialiseAPIWikisHandlerUser(testUser1);
		wikisAPIUser2 = initialiseAPIWikisHandlerUser(testUser2);
		wikisAPIUser3 = initialiseAPIWikisHandlerUser(testUser3);
	}
	
	/**
	 * populateData_Wiki_Scoring_Test() 
	 *<ul>
	 *<li><B>1: User 1 creates a public community</B></li>
	 *<li><B>2: User 1 adds the wiki widget to the community and then adds a wiki page to the community wiki</B></li>
	 *<li><B>3: User 1 adds a second wiki page to the community wiki</B></li>
	 *<li><B>4: User 1 adds Users 2 and 3 to the community as members</B></li>
	 *<li><B>5: User 2 comments on the first community wiki page.</B></li>
	 *<li><B>6: User 3 likes the second community wiki page.</B></li>
	 *</ul>
	 */
	@Test
	public void scoring_Community_Wiki() {
		
		// User 1 will now create a public community with the Wikis widget added
		BaseCommunity baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		Community publicCommunity = CommunityEvents.createNewCommunityAndAddWidget(baseCommunity, BaseWidget.WIKI, isOnPremise, testUser1, communitiesAPIUser1);
		getTestCaseData().addCreateCommunityData(publicCommunity, baseCommunity);
		
		// Retrieve the Wiki instance of the community wiki that has just been created
		Wiki communityWiki = CommunityWikiEvents.getCommunityWiki(publicCommunity, wikisAPIUser1);
		getTestCaseData().addCreateWikiData(communityWiki, publicCommunity, null, testUser1);
		
		// User 1 will now add the first wiki page to the community wiki
		BaseWikiPage baseWikiPage1 = WikiBaseBuilder.buildBaseWikiPage(getClass().getSimpleName() + Helper.genStrongRand());
		WikiPage communityWikiPage1 = CommunityWikiEvents.createWikiPage(communityWiki, baseWikiPage1, testUser1, wikisAPIUser1);
		
		// User 1 will now add the second wiki page to the community wiki
		BaseWikiPage baseWikiPage2 = WikiBaseBuilder.buildBaseWikiPage(getClass().getSimpleName() + Helper.genStrongRand());
		WikiPage communityWikiPage2 = CommunityWikiEvents.createWikiPage(communityWiki, baseWikiPage2, testUser1, wikisAPIUser1);
		
		// User 1 will now add User 2 and User 3 to the community as members
		User[] membersToBeAdded = { testUser2, testUser3 };
		CommunityEvents.addMemberMultipleUsers(publicCommunity, testUser1, communitiesAPIUser1, membersToBeAdded);
		
		// User 2 will now post a comment to the first community wiki page posted by User 1
		String user2Comment = Data.getData().commonComment + Helper.genStrongRand();
		WikiComment user2WikiComment = CommunityWikiEvents.addCommentToWikiPage(communityWikiPage1, testUser2, wikisAPIUser2, user2Comment);
		getTestCaseData().addCommentOnWikiPageData(user2WikiComment, communityWikiPage1, testUser2);
		
		// User 3 will now like / recommend the second community wiki page posted by User 1
		CommunityWikiEvents.likeWikiPage(communityWikiPage2, testUser3, wikisAPIUser3);
		getTestCaseData().addLikeWikiPageData(communityWiki, communityWikiPage2, baseWikiPage2, testUser3);
	}
}