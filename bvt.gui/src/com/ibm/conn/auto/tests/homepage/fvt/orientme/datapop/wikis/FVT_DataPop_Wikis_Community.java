package com.ibm.conn.auto.tests.homepage.fvt.orientme.datapop.wikis;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseWikiPage;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.lcapi.APIWikisHandler;
import com.ibm.conn.auto.tests.homepage.fvt.orientme.configs.DataPopSetup;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.Mentions;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.MentionsBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.WikiBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityWikiEvents;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.wikis.nodes.Wiki;

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

public class FVT_DataPop_Wikis_Community extends DataPopSetup {

	private APICommunitiesHandler communitiesAPIUser1;
	private APIWikisHandler wikisAPIUser1;
	private APIProfilesHandler profilesAPIUser2;
	private BaseCommunity baseCommunity;
	private Community publicCommunity;
	private User testUser1, testUser2;
	private Wiki communityWiki;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		setFilename(getClass().getSimpleName());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		getTestCaseData().addUserAssignmentData(listOfStandardUsers);
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);
		
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
		
		wikisAPIUser1 = initialiseAPIWikisHandlerUser(testUser1);
		
		// User 1 will now create a public community with the Wikis widget added
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunity = CommunityEvents.createNewCommunityAndAddWidget(baseCommunity, BaseWidget.WIKI, isOnPremise, testUser1, communitiesAPIUser1);
		getTestCaseData().addCreateCommunityData(publicCommunity, baseCommunity);
		
		// Retrieve the Wiki instance of the community wiki that has just been created
		communityWiki = CommunityWikiEvents.getCommunityWiki(publicCommunity, wikisAPIUser1);
	}
	
	@Test(groups = {"fvt_orientme_onprem", "fvt_orientme_cloud"})
	public void datapop_Community_Wiki_CreateWikiPage() {
		
		// User 1 will now add a wiki page to the community wiki
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(getClass().getSimpleName() + Helper.genStrongRand());
		CommunityWikiEvents.createWikiPage(communityWiki, baseWikiPage, testUser1, wikisAPIUser1);
	}
	
	@Test(groups = {"fvt_orientme_onprem", "fvt_orientme_cloud"})
	public void datapop_Community_Wiki_UpdateWikiPage() {
		
		// User 1 will now add a wiki page to the community wiki and will then edit the wiki page
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(getClass().getSimpleName() + Helper.genStrongRand());
		CommunityWikiEvents.createWikiPageAndEditWikiPage(communityWiki, baseWikiPage, testUser1, wikisAPIUser1);
	}
	
	@Test(groups = {"fvt_orientme_onprem", "fvt_orientme_cloud"})
	public void datapop_Community_Wiki_LikeWikiPage() {
		
		// User 1 will now add a wiki page to the community wiki and will then like / recommend the wiki page
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(getClass().getSimpleName() + Helper.genStrongRand());
		CommunityWikiEvents.createWikiPageAndLikeWikiPage(communityWiki, baseWikiPage, testUser1, wikisAPIUser1);
	}
	
	@Test(groups = {"fvt_orientme_onprem", "fvt_orientme_cloud"})
	public void datapop_Community_Wiki_WikiPageWithMentions() {
		
		// User 1 will now add a wiki page with mentions to User 2 to the community wiki
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser2, profilesAPIUser2, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(getClass().getSimpleName() + Helper.genStrongRand());
		CommunityWikiEvents.createWikiPageWithMentions(communityWiki, baseWikiPage, mentions, testUser1, wikisAPIUser1);
	}
	
	@Test(groups = {"fvt_orientme_onprem", "fvt_orientme_cloud"})
	public void datapop_Community_Wiki_CommentOnWikiPage() {
		
		// User 1 will now add a wiki page to the community wiki and will comment on the wiki page
		String user1Comment = Data.getData().commonComment + Helper.genStrongRand();
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(getClass().getSimpleName() + Helper.genStrongRand());
		CommunityWikiEvents.createWikiPageAndAddComment(communityWiki, baseWikiPage, testUser1, wikisAPIUser1, user1Comment);
	}
	
	@Test(groups = {"fvt_orientme_onprem", "fvt_orientme_cloud"})
	public void datapop_Community_Wiki_UpdateCommentOnWikiPage() {
		
		// User 1 will now add a wiki page to the community wiki, will comment on the wiki page and will then update the comment 
		String user1Comment = Data.getData().commonComment + Helper.genStrongRand();
		String user1CommentEdit = Data.getData().commonComment + Helper.genStrongRand();
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(getClass().getSimpleName() + Helper.genStrongRand());
		CommunityWikiEvents.createWikiPageAndAddCommentAndEditComment(communityWiki, baseWikiPage, testUser1, wikisAPIUser1, user1Comment, user1CommentEdit);
	}
	
	@Test(groups = {"fvt_orientme_onprem", "fvt_orientme_cloud"})
	public void datapop_Community_Wiki_CommentWithMentionsOnWikiPage() {
		
		// User 1 will now add a wiki page to the community wiki and will comment with mentions to User 2 on the wiki page
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser2, profilesAPIUser2, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(getClass().getSimpleName() + Helper.genStrongRand());
		CommunityWikiEvents.createWikiPageAndAddCommentWithMentions(communityWiki, baseWikiPage, mentions, testUser1, wikisAPIUser1);
	}
}
