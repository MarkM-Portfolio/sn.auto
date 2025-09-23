package com.ibm.conn.auto.tests.homepage.fvt.orientme.wikis;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseWikiPage;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
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
 * Date:	19th April 2017
 */

public class FVT_DataPop_Wiki_Scoring extends SetUpMethodsFVT {

	private APICommunitiesHandler communitiesAPIUser1;
	private APIWikisHandler wikisAPIUser1, wikisAPIUser2, wikisAPIUser3;
	private User testUser1, testUser2, testUser3;
	
	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
		
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(3);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		testUser3 = listOfStandardUsers.get(2);
		
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
	public void populateData_Wiki_Scoring_Test() {
		
		// User 1 will now create a public community
		BaseCommunity baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		Community publicCommunity = CommunityEvents.createNewCommunity(baseCommunity, testUser1, communitiesAPIUser1);
		
		// User 1 will now add the wiki widget to the community
		CommunityEvents.addWikiWidget(publicCommunity, testUser1, communitiesAPIUser1, isOnPremise);
		
		// Retrieve the Wiki instance of the community wiki that has just been created
		Wiki communityWiki = CommunityWikiEvents.getCommunityWiki(publicCommunity, wikisAPIUser1);
		
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
		CommunityWikiEvents.addCommentToWikiPage(communityWikiPage1, testUser2, wikisAPIUser2, user2Comment);
		
		// User 3 will now like / recommend the second community wiki page posted by User 1
		CommunityWikiEvents.likeWikiPage(communityWikiPage2, testUser3, wikisAPIUser3);
	}
}