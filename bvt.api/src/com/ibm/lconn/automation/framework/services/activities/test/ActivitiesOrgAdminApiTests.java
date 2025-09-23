/**
 * 
 */
package com.ibm.lconn.automation.framework.services.activities.test;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Categories;
import org.apache.abdera.model.Category;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.model.Feed;
import org.apache.abdera.protocol.client.ClientResponse;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.services.activities.ActivitiesService;
import com.ibm.lconn.automation.framework.services.activities.nodes.Activity;
import com.ibm.lconn.automation.framework.services.activities.nodes.ActivityEntry;
import com.ibm.lconn.automation.framework.services.activities.nodes.FieldElement;
import com.ibm.lconn.automation.framework.services.activities.nodes.Section;
import com.ibm.lconn.automation.framework.services.activities.nodes.Todo;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Component;
import com.ibm.lconn.automation.framework.services.common.StringConstants.DeploymentType;
import com.ibm.lconn.automation.framework.services.common.StringConstants.FieldType;
import com.ibm.lconn.automation.framework.services.common.StringConstants.MemberType;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Permissions;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Role;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.common.UserPerspective;
import com.ibm.lconn.automation.framework.services.common.UsersEnvironment;
import com.ibm.lconn.automation.framework.services.common.nodes.Member;
import com.ibm.lconn.automation.framework.services.communities.CommunitiesService;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.communities.nodes.Widget;
import com.ibm.lconn.automation.framework.services.gatekeeper.GateKeeperService;

/**
 * 
 * 
 *  Activities API unit test cases about org-admin.
 * 
 */
public class ActivitiesOrgAdminApiTests {

	private static ActivitiesService activityService, activityService2, adminActService;
	private static ActivitiesService actServiceUserA, actServiceUserB, actServiceOrgBAdmin;
	private static CommunitiesService comServiceUserA, comServiceUserB;
	private static Factory _factory = Abdera.getNewFactory();
	private static GateKeeperService gateKeeperService;

	private final static Logger LOGGER = Logger
			.getLogger(ActivitiesOrgAdminApiTests.class.getName());

	

	static UserPerspective user, user2, adminUser, actUserA, actUserB, comUserA, comUserB, profilesAdminUser, orgBAdminUser;

	static List<UserPerspective> userList = new ArrayList<UserPerspective>();
	
	private static String shareScopeUrl = "/activities/service/atom2/acl?contentUuid=";

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUp() throws Exception {

		UsersEnvironment userEnv = new UsersEnvironment();
		user = userEnv.getLoginUserEnvironment(StringConstants.CURRENT_USER,
				Component.ACTIVITIES.toString());
		activityService = user.getActivitiesService();

		user2 = userEnv.getLoginUserEnvironment(10,
				Component.ACTIVITIES.toString());
		activityService2 = user2.getActivitiesService();

		for (int i = 5; i <= 10; i++) {
			UserPerspective userCurrent = userEnv.getLoginUserEnvironment(i,
					Component.ACTIVITIES.toString());
			userList.add(userCurrent);
		}

		// get admin User, 0 is the admin user
		adminUser = userEnv.getLoginUserEnvironment(0,
				Component.ACTIVITIES.toString());
		profilesAdminUser = userEnv.getLoginUserEnvironment(StringConstants.ADMIN_USER,
		        Component.PROFILES.toString());
	    adminActService = adminUser.getActivitiesService();
	    gateKeeperService = profilesAdminUser.getGateKeeperService();
		
		actUserA = userEnv.getLoginUserEnvironment(12,
				Component.ACTIVITIES.toString());
		actServiceUserA = actUserA.getActivitiesService();

		actUserB = userEnv.getLoginUserEnvironment(11,
				Component.ACTIVITIES.toString());
		actServiceUserB = actUserB.getActivitiesService();

		comUserA = userEnv.getLoginUserEnvironment(12,
				Component.COMMUNITIES.toString());
		comServiceUserA = comUserA.getCommunitiesService();
		
		comUserB = userEnv.getLoginUserEnvironment(11,
				Component.COMMUNITIES.toString());
		comServiceUserB = comUserB.getCommunitiesService();
		
		orgBAdminUser = userEnv.getLoginUserEnvironment(15,
				Component.ACTIVITIES.toString());
		
		actServiceOrgBAdmin = orgBAdminUser.getActivitiesService();
		
		shareScopeUrl = URLConstants.SERVER_URL + shareScopeUrl;

	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDown() throws Exception {
		activityService.tearDown();
		activityService2.tearDown();
		adminActService.tearDown();
		gateKeeperService.tearDown();
	}
	/**
	 * to check the gatekeeper is enabled or not, this function can work for on-prem and smartcloud envs
	 * Note: testAndEnableFeature is only used for on-prem envs, 
	 * @param orgId
	 * @param gkName
	 * @return
	 */
	public boolean isGKenabled(String orgId, String gkName){
		String gkSetting = gateKeeperService.getGateKeeperSetting(orgId, gkName);
	    return gkSetting.contains("\"value\": true");
	}
	
    @Test
    public void testAclActionForOrgAdmin()throws Exception{
    	// Step1 create an activity for test
    	Activity simpleActivity = new Activity("activity_acl_test_for_orgadmin",
    	  "content for test acl activity", "acl_add", null, false, false);
    	Entry activityResult = (Entry) activityService.createActivity(simpleActivity);
    	
    	// Step2 get members of activity with orgAdmin
    	String acl_url = activityResult.getLink(StringConstants.REL_MEMBERS).getHref().toURL().toString();
    	Feed myMembers = (Feed) adminActService.getMemberFromActivity(acl_url);
    	assertEquals("member size error "+adminActService.getDetail(),1, myMembers.getEntries().size());
    	
    	// Step3  add member(user2) to this activity as owner with orgAdmin
    	Member user2Member = new Member(user2.getEmail(), null, Component.ACTIVITIES, Role.OWNER, MemberType.PERSON);
    	adminActService.addMemberToActivity(acl_url, user2Member);
    	myMembers = (Feed) adminActService.getMemberFromActivity(acl_url);
    	List<Entry> members = myMembers.getEntries();
    	assertEquals("member size error "+adminActService.getDetail(),2, members.size());
    	
    	//Step3 update member(user2) to reader with orgAdmin
    	String editLink="";
    	for(Entry member : members){
    		String useridUser2=member.getContributors().get(0).getExtension(new QName("http://www.ibm.com/xmlns/prod/sn", "userid","snx")).getText();
    		if(user2.getUserId().equalsIgnoreCase(useridUser2)){
    			editLink = member.getEditLinkResolvedHref().toString();
    			break;
    		}
    	}
    	user2Member.setRole(Role.READER);
    	ExtensibleElement updateResult = adminActService.updateMemberInActivity(editLink,user2Member);
    	assertNotNull(updateResult);
    	assertEquals("update member error "+adminActService.getDetail(),200,adminActService.getRespStatus() );
    	
    	//Step4 delete member(user2) with orgAdmin
    	adminActService.removeMemberFromActivity(editLink);
    	assertEquals("delete member error "+adminActService.getDetail(),200,adminActService.getRespStatus() );
    	
    	//successfully
    	String editActivityUrl = activityResult.getEditLink().getHref().toURL()
				.toString();
    	boolean deleteActivityResult = activityService
				.deleteActivity(editActivityUrl);
		assertTrue("Activity can be deleted"+activityService.getDetail(), deleteActivityResult);
    	
    }
    
    @Test
    public void testAclActionCrossOrg()throws Exception{
      if(StringConstants.DEPLOYMENT_TYPE.equals(DeploymentType.SMARTCLOUD)){
    	// Step1 create an activity and add one member for test 
    	Activity simpleActivity = new Activity("activity_acl_test_for_orgbdmin",
    	  "content for test acl activity", "acl_add", null, false, false);
    	Entry activityResult = (Entry) activityService.createActivity(simpleActivity);
    	String acl_url = activityResult.getLink(StringConstants.REL_MEMBERS).getHref().toURL().toString();
    	Member memberA = new Member(actUserA.getEmail(), null, Component.ACTIVITIES, Role.OWNER, MemberType.PERSON);
    	Entry memberAResult=(Entry)activityService.addMemberToActivity(acl_url, memberA);
    	assertTrue(memberAResult != null);
		assertEquals("add menember error", 201, activityService.getRespStatus());
    	
    	// Step2 get members of activity with orgBAdmin
    	actServiceOrgBAdmin.getMemberFromActivity(acl_url);
    	assertEquals("get member size error "+actServiceOrgBAdmin.getDetail(),403, actServiceOrgBAdmin.getRespStatus());
    	
    	// Step3  add member(user2) to this activity as owner with orgBAdmin
    	Member user2Member = new Member(user2.getEmail(), null, Component.ACTIVITIES, Role.OWNER, MemberType.PERSON);
    	actServiceOrgBAdmin.addMemberToActivity(acl_url, user2Member);
    	assertTrue("add member error "+actServiceOrgBAdmin.getDetail(),201!=actServiceOrgBAdmin.getRespStatus());
    	
    	//Step3 update member(user2) to reader with orgBAdmin
    	String memberAEditLink = memberAResult.getEditLinkResolvedHref().toString();
    	memberA.setRole(Role.READER);
    	actServiceOrgBAdmin.updateMemberInActivity(memberAEditLink,user2Member);
    	assertEquals("update member error "+actServiceOrgBAdmin.getDetail(),403,actServiceOrgBAdmin.getRespStatus() );
    	
    	//Step4 delete member(user2) with orgBAdmin
    	boolean removeResult = actServiceOrgBAdmin.removeMemberFromActivity(memberAEditLink);
    	assertTrue(removeResult);
    	Entry memberEntry =(Entry) activityService.getMemberFromActivity(memberAEditLink);
    	//check the userA is still existing in ,not removed.
    	assertEquals("delete member error",memberEntry.getTitle(),actUserA.getRealName());
    	assertEquals("delete member error "+actServiceOrgBAdmin.getDetail(),200,actServiceOrgBAdmin.getRespStatus() );
    	
    	//successfully
    	String editActivityUrl = activityResult.getEditLink().getHref().toURL()
				.toString();
    	boolean deleteActivityResult = activityService
				.deleteActivity(editActivityUrl);
		assertTrue("Activity can be deleted"+activityService.getDetail(), deleteActivityResult);
      }
    }
     /*
	 *  Test the org-admin can create/get/put/delete activity node
	 *  POST /atom2/activity 
	 *  GET PUT DELETE /atom2/activitynode 
	 */
      @Test
      public void testActivityNode() throws Exception{
      	// Step1 create an standalone activity for test
      	Activity standaloneActivity = new Activity("activity_activityNode_test_for_orgadmin",
      	  "content for test activityNode activity", "activityNode`", null, false, false);
      	Entry standaloneActivityResult = (Entry) actServiceUserA.createActivity(standaloneActivity);
      	String editStandaloneActivityUrl = standaloneActivityResult.getEditLink().getHref().toURL()
  				.toString();
      	ActivityEntry entry = new ActivityEntry("Test Entry",
				"This is a test entry", "tag1 tag2 tag3", 0, true, null,
				standaloneActivityResult, false);
      	// Step2 create entry to this activity with orgAdmin | POST atom2/activity
		Entry entryResult = (Entry) adminActService.addNodeToActivity(new Activity(standaloneActivityResult)
				.getAppCollection().getHref().toString(), entry);
		assertEquals("create activity node error", 201,adminActService.getRespStatus());
		assert (entryResult != null);
		String nodeEditUrl = entryResult.getEditLinkResolvedHref().toString();
		
		// Step3 get entry of this activity with orgAdmin | GET atom2/activityNode
		Entry entryNode = (Entry)adminActService.getFeed(nodeEditUrl);
		assertEquals("get activity node error", 200,adminActService.getRespStatus());
		assert(entryNode != null);
		
		// Step4 update entry of this activity with orgAdmin | PUT atom2/activityNode
      	entryNode.setContent("Updated entry content");
      	entryNode = (Entry)adminActService.editNodeInActivity(nodeEditUrl, entryNode);
      	assertEquals("update activity node error", 200,adminActService.getRespStatus());
      	assert(entryNode != null);
      	
      	// Step5 delete entry of this activity with orgAdmin | DELETE atom2/activityNode
      	boolean deleted = adminActService.removeNodeFromActivity(nodeEditUrl);
      	assertTrue("Activity Node can be deleted"+adminActService.getDetail(), deleted);
      	assertEquals("delete activity node error", 204,adminActService.getRespStatus());
      	
      	//Step6 create an community activity for test
      	Community community = new Community(" Test community for API /activityNode", "test community for API /activityNode",
				Permissions.PRIVATE, "tag_community_API_activityNode");
		LOGGER.fine("Create community: " + community.toString());
		Entry response = (Entry) comServiceUserA.createCommunity(community);
	    Community returnCommunity = new Community ((Entry) comServiceUserA.getCommunity(response.getEditLinkResolvedHref().toString()));
		Widget widget = new Widget(
				StringConstants.WidgetID.Activities.toString());
		comServiceUserA.postWidget(returnCommunity, widget.toEntry());
		assertEquals(201, comServiceUserA.getRespStatus());
		
	    //Step7 add member to community 
		Member member = new Member(comUserB.getEmail(), comUserB.getUserId(),
				Component.COMMUNITIES, Role.MEMBER, MemberType.PERSON);
		Entry memberEntry = (Entry) comServiceUserA.addMemberToCommunity(returnCommunity, member);
		LOGGER.fine("Create community member: " + memberEntry.toString());
		assertEquals(" Add Community Member ", 201, comServiceUserA.getRespStatus());
		
		//Step8  create one implicit community activity
		String activitiesURL = actServiceUserA.getServiceURLString() + URLConstants.ACTIVITIES_MY;
		Activity communityActivity = new Activity("implicit_community_activity_for_API_activityNode",
				  "content for API /activityNode", null, null, false, true);
		Entry implicitCommunityActivityResult = (Entry)  actServiceUserA.createCommunityActivity(activitiesURL,communityActivity, returnCommunity.getUuid(), "");
		String editCommnubityActivityUrl = implicitCommunityActivityResult.getEditLink().getHref().toURL()
  				.toString();
		
		// Step9 create entry to this activity with orgAdmin | POST atom2/activity
		Entry comEntryResult = (Entry) adminActService.addNodeToActivity(new Activity(implicitCommunityActivityResult)
						.getAppCollection().getHref().toString(), entry);
		assertEquals("create community activity node error", 201,adminActService.getRespStatus());
		assert (comEntryResult != null);
		nodeEditUrl = comEntryResult.getEditLinkResolvedHref().toString();
				
		// Step10 get entry of this activity with orgAdmin | GET atom2/activityNode
		Entry comEntryNode = (Entry)adminActService.getFeed(nodeEditUrl);
		assertEquals("get community activity node error", 200,adminActService.getRespStatus());
		assert(comEntryNode != null);
				
		// Step11 update entry of this activity with orgAdmin | PUT atom2/activityNode
		comEntryNode.setContent("Updated entry content for community activity");
		comEntryNode = (Entry)adminActService.editNodeInActivity(nodeEditUrl, comEntryNode);
		assertEquals("update community activity node error", 200,adminActService.getRespStatus());
		assert(comEntryNode != null);
		      	
		// Step12 delete entry of this activity with orgAdmin | DELETE atom2/activityNode
		boolean deletedResponse = adminActService.removeNodeFromActivity(nodeEditUrl);
		assertTrue("Community Activity Node can be deleted"+adminActService.getDetail(), deletedResponse);
		assertEquals("delete community activity node error", 204,adminActService.getRespStatus());
		
      	//successfully
      	boolean deleteActivityResult = actServiceUserA
  				.deleteActivity(editStandaloneActivityUrl);
      	boolean deleteCommActivityResult = actServiceUserA
  				.deleteActivity(editCommnubityActivityUrl);
      	assertTrue("Activity can be deleted"+activityService.getDetail(), deleteActivityResult);
  		assertTrue("Community Activity can be deleted"+activityService.getDetail(), deleteCommActivityResult);
  		
      }
      /*
  	  *  Test the org-admin can not  create/get/put/delete activity node when crossing org
  	  *  POST /atom2/activity 
  	  *  GET PUT DELETE /atom2/activitynode 
  	  */
      @Test
      public void testActivityNodeCrossOrg() throws Exception{
    	if(StringConstants.DEPLOYMENT_TYPE.equals(DeploymentType.SMARTCLOUD)){
    	  // Step1 create an standalone activity for test
      	  Activity standaloneActivity = new Activity("activity_activityNode_test_for_orgBadmin",
      	    "content for test activityNode activity", "activityNode`", null, false, false);
      	  Entry standaloneActivityResult = (Entry) actServiceUserA.createActivity(standaloneActivity);
      	  String editStandaloneActivityUrl = standaloneActivityResult.getEditLink().getHref().toURL()
  				.toString();
      	  ActivityEntry entry = new ActivityEntry("Test Entry",
				"This is a test entry", "tag1 tag2 tag3", 0, true, null,
				standaloneActivityResult, false);
      	  // Step2 create entry to this activity with orgBAdmin | POST atom2/activity
		  actServiceOrgBAdmin.addNodeToActivity(new Activity(standaloneActivityResult)
				.getAppCollection().getHref().toString(), entry);
		  assertTrue("Org-B-Admin create activity node error", 201 != actServiceOrgBAdmin.getRespStatus());
		
		  // orgBAdmin can not create activity node, then use org-A-user to create one for test GET/PUT/DELETE
		  Entry entryResult = (Entry) actServiceUserA.addNodeToActivity(new Activity(standaloneActivityResult)
		  .getAppCollection().getHref().toString(), entry);
		  assertEquals("create activity node error", 201,actServiceUserA.getRespStatus());
		  assert (entryResult != null);
		  String nodeEditUrl = entryResult.getEditLinkResolvedHref().toString();
		  Entry entryNode = (Entry)actServiceUserA.getFeed(nodeEditUrl);
		  assert(entryNode != null);
		
		  // Step3 get entry of this activity with orgAdmin | GET atom2/activityNode
		  actServiceOrgBAdmin.getFeed(nodeEditUrl);
		  assertEquals("Org-B-Admin get activity node error", 404,actServiceOrgBAdmin.getRespStatus());
		
		  // Step4 update entry of this activity with orgAdmin | PUT atom2/activityNode
      	  entryNode.setContent("Updated entry content");
      	  actServiceOrgBAdmin.editNodeInActivity(nodeEditUrl, entryNode);
      	  assertEquals("Org-B-Admin update activity node error", 404,actServiceOrgBAdmin.getRespStatus());
      	  assert(entryNode != null);
      	
      	  // Step5 delete entry of this activity with orgAdmin | DELETE atom2/activityNode
      	  boolean deleted = actServiceOrgBAdmin.removeNodeFromActivity(nodeEditUrl);
      	  assertEquals("Activity Node can be deleted"+actServiceOrgBAdmin.getDetail(),false, deleted);
      	  assertEquals("Org-B-Admin delete activity node error", 404,actServiceOrgBAdmin.getRespStatus());
      	
      	  //Step6 create an community activity for test
      	  Community community = new Community(" Test community for API /activityNode", "test community for API /activityNode",
				Permissions.PRIVATE, "tag_community_API_activityNode");
		  LOGGER.fine("Create community: " + community.toString());
		  Entry response = (Entry) comServiceUserA.createCommunity(community);
	      Community returnCommunity = new Community ((Entry) comServiceUserA.getCommunity(response.getEditLinkResolvedHref().toString()));
		  Widget widget = new Widget(
				StringConstants.WidgetID.Activities.toString());
		  comServiceUserA.postWidget(returnCommunity, widget.toEntry());
		  assertEquals(201, comServiceUserA.getRespStatus());
		
	      //Step7 add member to community 
		  Member member = new Member(comUserB.getEmail(), comUserB.getUserId(),
				Component.COMMUNITIES, Role.MEMBER, MemberType.PERSON);
		  Entry memberEntry = (Entry) comServiceUserA.addMemberToCommunity(returnCommunity, member);
		  LOGGER.fine("Create community member: " + memberEntry.toString());
		  assertEquals(" Add Community Member ", 201, comServiceUserA.getRespStatus());
		
		  //Step8  create one implicit community activity
		  String activitiesURL = actServiceUserA.getServiceURLString() + URLConstants.ACTIVITIES_MY;
		  Activity communityActivity = new Activity("implicit_community_activity_for_API_activityNode",
				  "content for API /activityNode", null, null, false, true);
		  Entry implicitCommunityActivityResult = (Entry)  actServiceUserA.createCommunityActivity(activitiesURL,communityActivity, returnCommunity.getUuid(), "");
		  String editCommnubityActivityUrl = implicitCommunityActivityResult.getEditLink().getHref().toURL()
  				.toString();
		
		  // Step9 create entry to this activity with orgAdmin | POST atom2/activity
		  actServiceOrgBAdmin.addNodeToActivity(new Activity(implicitCommunityActivityResult)
						.getAppCollection().getHref().toString(), entry);
		  assertTrue("Org-B-Admin create community activity node error", 201!=actServiceOrgBAdmin.getRespStatus());
		
		  // orgBAdmin can not create activity node, then use org-A-user to create one for test GET/PUT/DELETE
		  Entry comEntryResult = (Entry) actServiceUserA.addNodeToActivity(new Activity(implicitCommunityActivityResult)
		  .getAppCollection().getHref().toString(), entry);
		  assert (comEntryResult != null);
		  nodeEditUrl = comEntryResult.getEditLinkResolvedHref().toString();
		  Entry comEntryNode = (Entry)actServiceUserA.getFeed(nodeEditUrl);
		  assert(comEntryNode != null);
				
		  // Step10 get entry of this activity with orgAdmin | GET atom2/activityNode
		  actServiceOrgBAdmin.getFeed(nodeEditUrl);
		  assertEquals("Org-B-Admin get community activity node error", 404,actServiceOrgBAdmin.getRespStatus());
				
		  // Step11 update entry of this activity with orgAdmin | PUT atom2/activityNode
		  comEntryNode.setContent("Updated entry content for community activity");
		  actServiceOrgBAdmin.editNodeInActivity(nodeEditUrl, comEntryNode);
		  assertEquals("Org-B-Admin update community activity node error", 404,actServiceOrgBAdmin.getRespStatus());
		      	
		  // Step12 delete entry of this activity with orgAdmin | DELETE atom2/activityNode
		  boolean deletedResponse = actServiceOrgBAdmin.removeNodeFromActivity(nodeEditUrl);
		  assertEquals("Community Activity Node can be deleted"+actServiceOrgBAdmin.getDetail(),false, deletedResponse);
		  assertEquals("Org-B-Admin delete community activity node error", 404,actServiceOrgBAdmin.getRespStatus());
		
      	  //successfully
      	  boolean deleteActivityResult = actServiceUserA
  				.deleteActivity(editStandaloneActivityUrl);
      	  boolean deleteCommActivityResult = actServiceUserA
  				.deleteActivity(editCommnubityActivityUrl);
      	  assertTrue("Activity can be deleted"+activityService.getDetail(), deleteActivityResult);
  		  assertTrue("Community Activity can be deleted"+activityService.getDetail(), deleteCommActivityResult);
    	}	
      }
    
	/**
	 * For standalone activity
	 * Test create/get/update activities/service/atom2/tags with orgAdmin and external orgAdmin, delete isn't supported
	 * POST Step2
	 * GET  Step3
	 * PUT  Step4
	 */
	@Test
	public void testActivityTags() throws InterruptedException,
			URISyntaxException, FileNotFoundException, IOException {
		Entry activityResult = null;
		try {
			//Step1 create a standalone activity
			Activity simpleActivity = new Activity("activity_tags_standalone", "Test tags",
					null, null, false, false);
			activityResult = (Entry) activityService.createActivity(simpleActivity);
			
			//Step2.1 try POST with user2's orgAdmin-a, check status is 200
			String activityUuid = activityResult.getId().toString().trim().substring(20);
			String tagsUrl = activityService.getServiceURLString() + URLConstants.ACTIVITIES_TAGS + 
					"?activityNodeUuid=" + activityUuid;
			String tagsString = "tags1 tags2";
			String[] tagsArray = tagsString.split(StringConstants.STRING_SPACE_SEPERATOR);
			Entry entry = _factory.newEntry();
			for(String tag : tagsArray) {
				Category tagCategory = _factory.newCategory();
				tagCategory.setScheme(null);
				tagCategory.setTerm(tag);
				entry.addCategory(tagCategory);
			}
			adminActService.postFeed(tagsUrl, entry);
			assertEquals("the status error 200" + adminActService.getDetail(), 200, adminActService.getRespStatus());			
			
			//Step2.2 try POST with external orgAdmin-b, check status is 404
			if(StringConstants.DEPLOYMENT_TYPE.equals(DeploymentType.SMARTCLOUD)){
				actServiceOrgBAdmin.postFeed(tagsUrl, entry);
				assertEquals("the status error 404" + actServiceOrgBAdmin.getDetail(), 404, actServiceOrgBAdmin.getRespStatus());			
			}
			
			//Step3.1 try GET with user2's orgAdmin-a, check status is 200, it should respond tags created by step3.
			Categories categoryDocument = (Categories) adminActService.getFeed(tagsUrl);			
			assertEquals("status error 200"+adminActService.getDetail(),200, adminActService.getRespStatus());
			List<Category> tagsA = categoryDocument.getCategories();
			ArrayList<String> tagNames = new ArrayList<String>();
			for (Category cat : tagsA) {
				tagNames.add(cat.getTerm());
			}
			for (String tag : tagsString.split(" ")) {
				assertEquals(true, tagNames.contains(tag.toLowerCase()));
			}
			
			//Step3.2 try GET with external orgAdmin-b, check status is 200, it should respond empty tags.
			if(StringConstants.DEPLOYMENT_TYPE.equals(DeploymentType.SMARTCLOUD)){
				categoryDocument = (Categories) actServiceOrgBAdmin.getFeed(tagsUrl);			
				assertEquals("status error 200" + actServiceOrgBAdmin.getDetail(), 200, actServiceOrgBAdmin.getRespStatus());
				List<Category> tagsB = categoryDocument.getCategories();
				assertEquals(" orgAdmin-b GET an empty tags array ", 0, tagsB.size());
			}
			
			//Step4.1 try PUT with user2's orgAdmin-a, check status is 200.
			entry = _factory.newEntry();
			for (Category cat : tagsA) {
				cat.setTerm(cat.getTerm() + "_activity");
				entry.addCategory(cat);
			}
			adminActService.editNodeInActivity(tagsUrl, entry);
			assertEquals("the status error 200" + adminActService.getDetail(), 200, adminActService.getRespStatus());
					
			//Step4.2 try PUT with external orgAdmin-b, check status is 404.
			if(StringConstants.DEPLOYMENT_TYPE.equals(DeploymentType.SMARTCLOUD)){
				actServiceOrgBAdmin.editNodeInActivity(tagsUrl, entry);
				assertEquals("the status error 404" + actServiceOrgBAdmin.getDetail(), 404, actServiceOrgBAdmin.getRespStatus());
			}			
		} finally {
			if (null != activityResult) {
				String activityNodeEditUrl = activityResult.getEditLinkResolvedHref().toURL().toString();
				boolean done = activityService.deleteActivity(activityNodeEditUrl);
				assertTrue("done error" + activityService.getDetail(), done);
			}
		}
	}
	
	
	/**
	 * For community activity
	 * Test create/get/update activities/service/atom2/tags with orgAdmin and external orgAdmin, delete isn't supported
	 * POST Step3
	 * GET  Step4
	 * PUT  Step5
	 */
	@Test
	public void testCommunityActivityTags() throws InterruptedException,
			URISyntaxException, FileNotFoundException, IOException {		
		Entry communityResponse = null;
		try {
			//Step1 create a community by userA and add widget ACTIVITIES			
			Community community = new Community("Test community for tags", "test community for tags",
					Permissions.PRIVATE, "tag_community");
			communityResponse = (Entry) comServiceUserA.createCommunity(community);
			Community returnCommunity = new Community(
					(Entry) comServiceUserA.getCommunity(communityResponse.getEditLinkResolvedHref().toString()));
			Widget widget = new Widget(StringConstants.WidgetID.Activities.toString());
			comServiceUserA.postWidget(returnCommunity, widget.toEntry());
			assertEquals("the status error 201 "+comServiceUserA.getDetail(), 201, comServiceUserA.getRespStatus());					
			
			//Step2 create an activity in the community
			String activitiesURL = actServiceUserA.getServiceURLString() + URLConstants.ACTIVITIES_MY;
			Activity simpleActivity = new Activity("activity_tags_community",
					"Test tags", "tag1 tag2", null, false, true);
			Entry activityResult = (Entry)  actServiceUserA.createCommunityActivity(activitiesURL, simpleActivity, returnCommunity.getUuid(), "");
			assertEquals("the status error 201 "+comServiceUserA.getDetail(), 201, comServiceUserA.getRespStatus());
			
			//Step3.1 try POST with user2's orgAdmin-a, check status is 200
			String activityUuid = activityResult.getId().toString().trim().substring(20);
			String tagsUrl = activityService.getServiceURLString() + URLConstants.ACTIVITIES_TAGS + 
					"?activityNodeUuid=" + activityUuid;
			String tagsString = "tags3 tags4";
			String[] tagsArray = tagsString.split(StringConstants.STRING_SPACE_SEPERATOR);
			Entry entry = _factory.newEntry();
			for(String tag : tagsArray) {
				Category tagCategory = _factory.newCategory();
				tagCategory.setScheme(null);
				tagCategory.setTerm(tag);
				entry.addCategory(tagCategory);
			}
			adminActService.postFeed(tagsUrl, entry);
			assertEquals("the status error 200" + adminActService.getDetail(), 200, adminActService.getRespStatus());			
			
			//Step3.2 try POST with external orgAdmin-b, check status is 404
			if(StringConstants.DEPLOYMENT_TYPE.equals(DeploymentType.SMARTCLOUD)){
				actServiceOrgBAdmin.postFeed(tagsUrl, entry);
				assertEquals("the status error 404" + actServiceOrgBAdmin.getDetail(), 404, actServiceOrgBAdmin.getRespStatus());
			}
		
			//Step4.1 try GET with user2's orgAdmin-a, check status is 200, it should respond tags created by step3.
			Categories categoryDocument = (Categories) adminActService.getFeed(tagsUrl);			
			assertEquals("status error 200" + adminActService.getDetail(), 200, adminActService.getRespStatus());
			List<Category> tagsA = categoryDocument.getCategories();
			ArrayList<String> tagNames = new ArrayList<String>();
			for (Category cat : tagsA) {
				tagNames.add(cat.getTerm());
			}
			for (String tag : tagsString.split(" ")) {
				assertEquals(true, tagNames.contains(tag.toLowerCase()));
			}
			
			//Step4.2 try GET with external orgAdmin-b, check status is 200, it should respond empty tags.
			if(StringConstants.DEPLOYMENT_TYPE.equals(DeploymentType.SMARTCLOUD)){
				categoryDocument = (Categories) actServiceOrgBAdmin.getFeed(tagsUrl);			
				assertEquals("status error 200" + actServiceOrgBAdmin.getDetail(), 200, actServiceOrgBAdmin.getRespStatus());
				List<Category> tagsB = categoryDocument.getCategories();
				assertEquals(" orgAdmin-b GET an empty tags array ", 0, tagsB.size());
			}
			
			//Step5.1 try PUT with user2's orgAdmin-a, check status is 200.
			entry = _factory.newEntry();
			for (Category cat : tagsA) {
				cat.setTerm(cat.getTerm() + "_community");
				entry.addCategory(cat);
			}
			adminActService.editNodeInActivity(tagsUrl, entry);
			assertEquals("the status error 200" + adminActService.getDetail(), 200, adminActService.getRespStatus());
					
			//Step5.2 try PUT with external orgAdmin-b, check status is 404.
			if(StringConstants.DEPLOYMENT_TYPE.equals(DeploymentType.SMARTCLOUD)){
				actServiceOrgBAdmin.editNodeInActivity(tagsUrl, entry);
				assertEquals("the status error 404" + actServiceOrgBAdmin.getDetail(), 404, actServiceOrgBAdmin.getRespStatus());
			}
		} finally {
			if (null != communityResponse) {
				String activityNodeEditUrl = communityResponse.getEditLinkResolvedHref().toURL().toString();
				boolean done = comServiceUserA.deleteCommunity(activityNodeEditUrl);
				assertTrue("done error" + comServiceUserA.getDetail(), done);
			}
		}
	}
	 /*
  	  *  Test the org-admin can get activity descendants/children node 
  	  *  Test the org-admin can not get activity descendants/children node when cross org
  	  *  GET /atom2/descendants
  	  *  GET /atom2/children
  	  */
     @Test
     public void testDescendantsAndChildren() throws Exception{
       Entry standaloneActivityResult = null;
   	   Entry implicitCommunityActivityResult = null;
   	   String editStandaloneActivityUrl = null;
   	   String editCommnunityUrl = null;
   	   try{
   	      // Step1 create an standalone activity for test
     	  Activity standaloneActivity = new Activity("activity_standalone_test_for_descendants_and_children",
     	    "content for test activity descendants and children", "descendants", null, false, false);
     	  standaloneActivityResult = (Entry) actServiceUserA.createActivity(standaloneActivity);
     	  String acl_url = standaloneActivityResult.getLink(StringConstants.REL_MEMBERS).getHref().toURL().toString();
     	  Member userBMember = new Member(actUserB.getEmail(), null, Component.ACTIVITIES, Role.OWNER, MemberType.PERSON);
 	      actServiceUserA.addMemberToActivity(acl_url, userBMember);
     	  editStandaloneActivityUrl = standaloneActivityResult.getEditLink().getHref().toURL()
 				.toString();
     	  Activity returnedActivity = new Activity(standaloneActivityResult);
     	  String appCollectionLink = returnedActivity.getAppCollection().getHref().toString();
     	  
     	  //Step2 create an community activity for test
     	  Community community = new Community("Test community for API_descendants and children", "test community for API_descendants_and_children",
				Permissions.PRIVATE, "tag_community_API_descendants_and_children");
		  LOGGER.fine("Create community: " + community.toString());
		  Entry response = (Entry) comServiceUserA.createCommunity(community);
	      Community returnCommunity = new Community ((Entry) comServiceUserA.getCommunity(response.getEditLinkResolvedHref().toString()));
		  Widget widget = new Widget(
				StringConstants.WidgetID.Activities.toString());
		  comServiceUserA.postWidget(returnCommunity, widget.toEntry()); 
		  assertEquals(201, comServiceUserA.getRespStatus());
		  Member member = new Member(comUserB.getEmail(), comUserB.getUserId(),
				Component.COMMUNITIES, Role.MEMBER, MemberType.PERSON);
		  Entry memberEntry = (Entry) comServiceUserA.addMemberToCommunity(returnCommunity, member);
		  LOGGER.fine("Create community member: " + memberEntry.toString());
		  assertEquals("Add Community Member ", 201, comServiceUserA.getRespStatus());
		  String activitiesURL = actServiceUserA.getServiceURLString() + URLConstants.ACTIVITIES_MY;
		  Activity communityActivity = new Activity("implicit_community_activity_for_API_descendants_and_children",
				  "content for API_descendants", null, null, false, true);
		  implicitCommunityActivityResult = (Entry)  actServiceUserA.createCommunityActivity(activitiesURL,communityActivity, returnCommunity.getUuid(), "");
		  editCommnunityUrl = response.getEditLink().getHref().toURL().toString();
		  Activity returnedCommActivity = new Activity(implicitCommunityActivityResult);
		  String appCommCollectionLink = returnedCommActivity.getAppCollection().getHref().toString();
   	  
   	      // Step3 create entry to standalone activity and community activity 
     	  ActivityEntry entry = new ActivityEntry("Test Entry for descendants and children",
				"This is a test entry", "tag1 tag2 tag3", 0, true, null,
				standaloneActivityResult, false);
     	  Entry entryResult =(Entry) actServiceUserA.addNodeToActivity(appCollectionLink, entry);
     	  assertEquals("create activity entry error", 201, actServiceUserA.getRespStatus());
     	  ActivityEntry returnedEntry = new ActivityEntry(entryResult);
     	  entry.setTitle(entry.getTitle()+"_community");
     	  entry.setParent(implicitCommunityActivityResult);
     	  entry.setInReplyTo(implicitCommunityActivityResult);
     	  entry.setActivityId(returnedCommActivity.getActivityIdElement());
     	  Entry entryCommResult = (Entry)actServiceUserA.addNodeToActivity(appCommCollectionLink, entry);
     	  assertEquals("create activity entry error", 201, actServiceUserA.getRespStatus());
     	  ActivityEntry returnedCommEntry = new ActivityEntry(entryCommResult);
     	  String returnedEntryId = returnedEntry.getId().toString();
    	  returnedEntryId = returnedEntryId.substring(returnedEntryId.lastIndexOf(":") + 1);
    	  String returnedCommEntryId = returnedCommEntry.getId().toString();
    	  returnedCommEntryId = returnedCommEntryId.substring(returnedCommEntryId.lastIndexOf(":") + 1);
     	
		  Todo singleTodo = new Todo("Test_single_assignee_Todo_for_API_descendants_and_children",
					"content_of_single_assignee_Todo", "single Todo", 1, false,
					false, standaloneActivityResult, actUserB.getUserName(),
					actUserB.getUserId());
		  actServiceUserA.addNodeToActivity(appCollectionLink, singleTodo);
		  assertEquals("create activity todo error", 201, actServiceUserA.getRespStatus());
		  
		  singleTodo.setTitle(singleTodo.getTitle()+"_community");
		  singleTodo.setParent(implicitCommunityActivityResult);
		  singleTodo.setInReplyTo(implicitCommunityActivityResult);
		  singleTodo.setActivityId(returnedCommActivity.getActivityIdElement());
		  actServiceUserA.addNodeToActivity(appCommCollectionLink, singleTodo);
     	  assertEquals("create activity todo error", 201, actServiceUserA.getRespStatus());
		  
		  // Step4 create section to this activity
		  Section section = new Section("TestSection_for_API_descendants" , 0, standaloneActivityResult);
		  Entry sectionResult = (Entry)actServiceUserA.addNodeToActivity(appCollectionLink, section);
		  assertEquals("create activity section error", 201, actServiceUserA.getRespStatus());
		  Section returnedSection = new Section(sectionResult);
		  
		  section.setTitle(section.getTitle()+"_community");
		  section.setParent(implicitCommunityActivityResult);
		  section.setInReplyTo(implicitCommunityActivityResult);
		  section.setActivityId(returnedCommActivity.getActivityIdElement());
		  Entry sectionCommResult = (Entry)actServiceUserA.addNodeToActivity(appCommCollectionLink, section);
     	  assertEquals("create activity section error", 201, actServiceUserA.getRespStatus());
     	  Section returnedCommSection = new Section(sectionCommResult);
     	  String returnedSectionId = returnedSection.getId().toString();
     	  returnedSectionId = returnedSectionId.substring(returnedSectionId.lastIndexOf(":") + 1);
  	      String returnedCommSectionId = returnedCommSection.getId().toString();
  	      returnedCommSectionId = returnedCommSectionId.substring(returnedCommSectionId.lastIndexOf(":") + 1);
     	  
     	  // Step5 move entry to section
  	      actServiceUserA.moveNode(returnedEntryId,returnedSectionId);
  	      actServiceUserA.moveNode(returnedCommEntryId,returnedCommSectionId);
     	  
		  /** Step6
  	       * call API GET /atom2/descendants to get activity nodes with org-admin
     	   * call API GET /atom2/children to get activity nodes with org-admin
     	   */
		  String descendantsUrl = URLConstants.SERVER_URL + URLConstants.ACTIVITIES_BASE + 
				  URLConstants.ACTIVITIES_SERVER + "/descendants?nodeUuid=";
		  String childrenUrl = URLConstants.SERVER_URL + URLConstants.ACTIVITIES_BASE + 
				  URLConstants.ACTIVITIES_SERVER + "/children?nodeUuid=";
		  Feed descendantsFeed = (Feed)adminActService.getFeed(descendantsUrl + returnedActivity.getActivityId());
		  assertEquals("get activity descendants nodes error", 200, adminActService.getRespStatus());
		  assertEquals("returned activity descendants nodes size error", 3, descendantsFeed.getEntries().size());
		  
		  Feed descendantsCommFeed = (Feed)adminActService.getFeed(descendantsUrl + returnedCommActivity.getActivityId());
		  assertEquals("get community activity descendants nodes error", 200, adminActService.getRespStatus());
		  assertEquals("returned community activity descendants nodes size error", 3, descendantsCommFeed.getEntries().size());
		  
		  Feed childrenFeed = (Feed)adminActService.getFeed(childrenUrl + returnedActivity.getActivityId());
		  assertEquals("get activity children nodes error", 200, adminActService.getRespStatus());
		  assertEquals("returned activity children nodes size error", 2, childrenFeed.getEntries().size());
		  
		  Feed childrenCommFeed = (Feed)adminActService.getFeed(childrenUrl + returnedCommActivity.getActivityId());
		  assertEquals("get community activity children nodes error", 200, adminActService.getRespStatus());
		  assertEquals("returned community activity children nodes size error", 2, childrenCommFeed.getEntries().size());
		  /**
		   * Step7
		   * call API GET /atom2/descendants to get activity nodes with crossed org org-admin
		   * call API GET /atom2/children to get activity nodes with crossed org org-admin
		   */
		  if(StringConstants.DEPLOYMENT_TYPE.equals(DeploymentType.SMARTCLOUD)){
		    actServiceOrgBAdmin.getFeed(descendantsUrl + returnedActivity.getActivityId());
		    assertEquals("org-B-Admin get standalone activity descendants error", 404, actServiceOrgBAdmin.getRespStatus());
		    actServiceOrgBAdmin.getFeed(descendantsUrl + returnedCommActivity.getActivityId());
		    assertEquals("org-B-Admin get community activity descendants error", 404, actServiceOrgBAdmin.getRespStatus());
		    
		    Feed childrenF = (Feed)actServiceOrgBAdmin.getFeed(childrenUrl + returnedActivity.getActivityId());
		    assertEquals("org-B-Admin get standalone activity children error", 200, actServiceOrgBAdmin.getRespStatus());
		    assertEquals("returned activity children nodes size error", 0, childrenF.getEntries().size());
		    
		    Feed childrenCommF = (Feed)actServiceOrgBAdmin.getFeed(childrenUrl + returnedCommActivity.getActivityId());
		    assertEquals("org-B-Admin get community activity children error", 200, actServiceOrgBAdmin.getRespStatus());
		    assertEquals("returned community activity children nodes size error", 0, childrenCommF.getEntries().size());
		  }
		  
       }finally{
   	     if(editStandaloneActivityUrl != null){
 		    boolean deleteActivityResult = actServiceUserA.deleteActivity(editStandaloneActivityUrl);
 		    assertTrue("Activity can be deleted"+actServiceUserA.getDetail(), deleteActivityResult);
   	     }
   	     if(editCommnunityUrl != null){
 		    boolean deleteCommResult = comServiceUserA.deleteCommunity(editCommnunityUrl);
 	        assertTrue("Community can be deleted"+comServiceUserA.getDetail(), deleteCommResult);
   	     }
       }
     }
     
 	/**
 	 * For standalone activity 
 	 * Test activities/service/download/ with orgAdmin and external orgAdmin 
 	 * try to download attachment in Step3
 	 */
 	@Test
 	public void testActivityDownload()
 			throws InterruptedException, URISyntaxException, FileNotFoundException, IOException {
 		Entry activityResult = null;
 		try {
 			// Step1 create a standalone activity
 			Activity simpleActivity = new Activity("activity_download_standalone", "Test download", null, null, false, false);
 			activityResult = (Entry) activityService.createActivity(simpleActivity);
 			Activity activity = new Activity(activityResult);
 			
 			// Step2. add entry with attachment to the activity
 			ArrayList<FieldElement> fields = new ArrayList<FieldElement>();
 			FieldElement element = new FieldElement(null, false, "Attachment", 0, FieldType.FILE, null, null);
 			fields.add(element);
 			ActivityEntry simpleEntry = new ActivityEntry("Test Entry", "This is a test entry for testing download",
 					null, 0, false, fields, activityResult, false);
 		    //create a new file, and write something  
 			String contentStr = "This is the first line output to the standalone activity download file!";
 			String fileName= "fileName.txt";
 		    File file = new File(fileName);
 		    BufferedWriter output = new BufferedWriter(new FileWriter(file));
 		    output.write(contentStr);
 		    output.flush();
 		    output.close();
 		    Entry entryResult = (Entry) activityService.addMultipartNodeToActivity(
 					activity.getAppCollection().getHref().toString(), simpleEntry, file);
 			
 			// Step3.1 try to download the attachment with user1's orgAdmin-a,
 			// check status is 200, it should respond content of the uploaded file 
 			// get attachment uuid
 			String contentUuid = "";
 			List<Element> fieldElements = entryResult.getExtension(StringConstants.SNX_FIELD).getElements();
 			for (Element e : fieldElements) {
 				if ("enclosure".equalsIgnoreCase(e.getAttributeValue("rel"))) {
 					String enclosureUrl = e.getAttributeValue("href");
 					contentUuid = enclosureUrl.substring(enclosureUrl.indexOf("/download/")+10, enclosureUrl.indexOf("/" + fileName));
 					break;
 				}
 			}
 			assertFalse("error message invalid attachment uuid" , "".equals(contentUuid));
 			String downloadUrl = activityService.getServiceURLString() + "/service/download/" + contentUuid;
 			String responseString = adminActService.getResponseString(downloadUrl);
 			assertEquals("the status error 200" + adminActService.getDetail(), 200, adminActService.getRespStatus());
 			assertTrue("verify respond with file's content", responseString.contains(contentStr));
 			
 			// Step3.2 try to download the attachment with external orgAdmin-b, check status is 404
 			if(StringConstants.DEPLOYMENT_TYPE.equals(DeploymentType.SMARTCLOUD)){
 				responseString = actServiceOrgBAdmin.getResponseString(downloadUrl);
 				assertEquals("the status error 404" + actServiceOrgBAdmin.getDetail(), 404, actServiceOrgBAdmin.getRespStatus());
 			}
 		} finally {
 			if (null != activityResult) {
 				String activityNodeEditUrl = activityResult.getEditLinkResolvedHref().toURL().toString();
 				boolean done = activityService.deleteActivity(activityNodeEditUrl);
 				assertTrue("done error" + activityService.getDetail(), done);
 			}
 		}
 	}
 	
 	/**
 	 * For community activity 
 	 * Test activities/service/download/ with orgAdmin and external orgAdmin
 	 * try to download attachment in Step4
 	 */
 	@Test
 	public void testCommunityActivityDownload()
 			throws InterruptedException, URISyntaxException, FileNotFoundException, IOException {
 		Entry communityResponse = null;
 		try {
 			//Step1 create a community by userA and add widget ACTIVITIES			
 			Community community = new Community("Test community for download", "test community for download",
 					Permissions.PRIVATE, "download_community");
 			communityResponse = (Entry) comServiceUserA.createCommunity(community);
 			Community returnCommunity = new Community(
 					(Entry) comServiceUserA.getCommunity(communityResponse.getEditLinkResolvedHref().toString()));
 			Widget widget = new Widget(StringConstants.WidgetID.Activities.toString());
 			comServiceUserA.postWidget(returnCommunity, widget.toEntry());
 			assertEquals("the status error 201 "+comServiceUserA.getDetail(), 201, comServiceUserA.getRespStatus());					
 			
 			//Step2 create an activity in the community
 			String activitiesURL = actServiceUserA.getServiceURLString() + URLConstants.ACTIVITIES_MY;
 			Activity simpleActivity = new Activity("activity_download_community",
 					"Test download", null, null, false, true);
 			Entry activityResult = (Entry)  actServiceUserA.createCommunityActivity(activitiesURL, simpleActivity, returnCommunity.getUuid(), "");
 			assertEquals("the status error 201 "+comServiceUserA.getDetail(), 201, comServiceUserA.getRespStatus());
 			Activity activity = new Activity(activityResult);

 			// Step3. add entry with attachment to the activity
 			ArrayList<FieldElement> fields = new ArrayList<FieldElement>();
 			FieldElement element = new FieldElement(null, false, "Attachment", 0, FieldType.FILE, null, null);
 			fields.add(element);
 			ActivityEntry simpleEntry = new ActivityEntry("Test Entry", "This is a test entry for testing download",
 					null, 0, false, fields, activityResult, false);
 		    //create a new file, and write something  
 			String contentStr = "This is the first line output to the community activity download file!";
 			String fileName= "fileName.txt";
 		    File file = new File(fileName);
 		    BufferedWriter output = new BufferedWriter(new FileWriter(file));
 		    output.write(contentStr);
 		    output.flush();
 		    output.close();
 		    Entry entryResult = (Entry) actServiceUserA.addMultipartNodeToActivity(
 		    		activity.getAppCollection().getHref().toString(), simpleEntry, file);
 		    
 			// Step4.1 try to download the attachment with user1's orgAdmin-a,
 			// check status is 200, it should respond content of the uploaded file 
 			// get attachment uuid
 			String contentUuid = "";
 			List<Element> fieldElements = entryResult.getExtension(StringConstants.SNX_FIELD).getElements();
 			for (Element e : fieldElements) {
 				if ("enclosure".equalsIgnoreCase(e.getAttributeValue("rel"))) {
 					String enclosureUrl = e.getAttributeValue("href");
 					contentUuid = enclosureUrl.substring(enclosureUrl.indexOf("/download/")+10, enclosureUrl.indexOf("/" + fileName));
 					break;
 				}
 			}
 			assertFalse("error message invalid attachment uuid" , "".equals(contentUuid));
 			String downloadUrl = actServiceUserA.getServiceURLString() + "/service/download/" + contentUuid;
 			String responseString = adminActService.getResponseString(downloadUrl);
 			assertEquals("the status error 200" + adminActService.getDetail(), 200, adminActService.getRespStatus());
 			assertTrue("verify respond with file's content", responseString.contains(contentStr));
 		    
 			// Step4.2 try to download the attachment with external orgAdmin-b, check status is 404
 			if(StringConstants.DEPLOYMENT_TYPE.equals(DeploymentType.SMARTCLOUD)){
 				responseString = actServiceOrgBAdmin.getResponseString(downloadUrl);
 				assertEquals("the status error 404" + actServiceOrgBAdmin.getDetail(), 404, actServiceOrgBAdmin.getRespStatus());
 			}
 		} finally {
 			if (null != communityResponse) {
 				String activityNodeEditUrl = communityResponse.getEditLinkResolvedHref().toURL().toString();
 				boolean done = comServiceUserA.deleteCommunity(activityNodeEditUrl);
 				assertTrue("done error" + comServiceUserA.getDetail(), done);
 			}
 		}
 	}
 	
 	/**
 	 * For standalone activity 
 	 * Test activities/service/downloadExtended/ with orgAdmin and external orgAdmin 
 	 * try to download extended attachment in Step3
 	 */
    @Test
 	public void testActivityDownloadExtended()
 			throws InterruptedException, URISyntaxException, FileNotFoundException, IOException {
 		Entry activityResult = null;
 		try {
 			// Step1 create a standalone activity
 			Activity simpleActivity = new Activity("activity_downloadExtended_standalone", "Test downloadExtended", null, null, false, false);
 			activityResult = (Entry) activityService.createActivity(simpleActivity);
 			Activity activity = new Activity(activityResult);
 			
 			// Step2. add an entry with long description to the activity
 			ActivityEntry simpleEntry = new ActivityEntry("Test Entry", "This is a test entry for testing downloadExtended",
 					null, 0, false, null, activityResult, false);
 		    //make a long string as entry's description  
 			String contentStr = "if(typeof define!==\"undefined\"&&typeof define._packages!==\"undefined\")define._packages[\"activity\"]=true;\n";
 			StringBuffer buf = new StringBuffer(contentStr);
 		    for(int loop=0; loop<20; loop++){
 		    	buf.append(contentStr);
 		    }
 		    simpleEntry.setSummary(buf.toString());
 		    Entry entryResult = (Entry) activityService.addNodeToActivity(activity.getAppCollection().getHref().toString(), simpleEntry);
 			// Step3.1 try to download the attachment with user1's orgAdmin-a,
 			// check status is 200, it should respond content of contentStr
 			// get extended attachment uuid
 			String contentUuid = "";
 		    String enclosureUrl= entryResult.getEnclosureLink().getAttributeValue("href");
 		    contentUuid = enclosureUrl.substring(enclosureUrl.indexOf("/downloadExtended/")+18, enclosureUrl.indexOf("/activitiesExtendedDescription"));
 			assertFalse("error message invalid extended attachment uuid" , "".equals(contentUuid));
 			String downloadUrl = activityService.getServiceURLString() + "/service/downloadExtended/" + contentUuid;
 			String responseString = adminActService.getResponseString(downloadUrl);
 			assertEquals("the status error 200" + adminActService.getDetail(), 200, adminActService.getRespStatus());
 			assertTrue("verify respond with file's content", responseString.contains(contentStr));
 			
 			// Step3.2 try to download the extended attachment with external orgAdmin-b, check status is 404
 			if(StringConstants.DEPLOYMENT_TYPE.equals(DeploymentType.SMARTCLOUD)){
 				responseString = actServiceOrgBAdmin.getResponseString(downloadUrl);
 				assertEquals("the status error 404" + actServiceOrgBAdmin.getDetail(), 404, actServiceOrgBAdmin.getRespStatus());
 			}
 		} finally {
 			if (null != activityResult) {
 				String activityNodeEditUrl = activityResult.getEditLinkResolvedHref().toURL().toString();
 				boolean done = activityService.deleteActivity(activityNodeEditUrl);
 				assertTrue("done error" + activityService.getDetail(), done);
 			}
 		}
 	}
 	
 	/**
 	 * For community activity
 	 * Test activities/service/downloadExtended/ with orgAdmin and external orgAdmin
 	 * try to download extended attachment in Step4
 	 */
 	@Test
 	public void testCommunityActivityDownloadExtended()
 			throws InterruptedException, URISyntaxException, FileNotFoundException, IOException {
		if(StringConstants.DEPLOYMENT_TYPE.equals(DeploymentType.SMARTCLOUD)){
	 		Entry communityResponse = null;
	 		try {
	 			//Step1 create a community by userA and add widget ACTIVITIES			
	 			Community community = new Community("Test community for downloadExtended", "test community for downloadExtended",
	 					Permissions.PRIVATE, "downloadExtended_community");
	 			communityResponse = (Entry) comServiceUserA.createCommunity(community);
	 			Community returnCommunity = new Community(
	 					(Entry) comServiceUserA.getCommunity(communityResponse.getEditLinkResolvedHref().toString()));
	 			Widget widget = new Widget(StringConstants.WidgetID.Activities.toString());
	 			comServiceUserA.postWidget(returnCommunity, widget.toEntry());
	 			assertEquals("the status error 201 "+comServiceUserA.getDetail(), 201, comServiceUserA.getRespStatus());					
	 			
	 			//Step2 create an activity in the community
	 			String activitiesURL = actServiceUserA.getServiceURLString() + URLConstants.ACTIVITIES_MY;
	 			Activity simpleActivity = new Activity("activity_downloadExtended_community",
	 					"Test downloadExtended", null, null, false, true);
	 			Entry activityResult = (Entry)  actServiceUserA.createCommunityActivity(activitiesURL, simpleActivity, returnCommunity.getUuid(), "");
	 			assertEquals("the status error 201 "+comServiceUserA.getDetail(), 201, comServiceUserA.getRespStatus());
	 			Activity activity = new Activity(activityResult);

	 			// Step3. add entry with extended attachment to the activity
	 			ActivityEntry simpleEntry = new ActivityEntry("Test Entry", "This is a test entry for testing downloadExtended",
	 					null, 0, false, null, activityResult, false);
	 		    //make a long string as entry's description  
	 			String contentStr = "if(typeof define!==\"undefined\"&&typeof define._packages!==\"undefined\")define._packages[\"community\"]=true;\n";
	 			StringBuffer buf = new StringBuffer(contentStr);
	 		    for(int loop=0; loop<20; loop++){
	 		    	buf.append(contentStr);
	 		    }
	 		    simpleEntry.setSummary(buf.toString());
	 		    Entry entryResult = (Entry) actServiceUserA.addNodeToActivity(activity.getAppCollection().getHref().toString(), simpleEntry);
	 		    
	 			// Step4.1 try to download the attachment with user1's orgAdmin-a,
	 			// check status is 200, it should respond content of contentStr  
	 			// get extended attachment uuid
	 			String contentUuid = "";
	 		    String enclosureUrl= entryResult.getEnclosureLink().getAttributeValue("href");
	 		    contentUuid = enclosureUrl.substring(enclosureUrl.indexOf("/downloadExtended/")+18, enclosureUrl.indexOf("/activitiesExtendedDescription"));
	 			assertFalse("error message invalid extended attachment uuid" , "".equals(contentUuid));
	 			String downloadUrl = actServiceUserA.getServiceURLString() + "/service/downloadExtended/" + contentUuid;
	 			String responseString = adminActService.getResponseString(downloadUrl);
	 			assertEquals("the status error 200" + adminActService.getDetail(), 200, adminActService.getRespStatus());
	 			assertTrue("verify respond with file's content", responseString.contains(contentStr));
	 		    
	 			// Step4.2 try to download the extended attachment with external orgAdmin-b, check status is 404
				responseString = actServiceOrgBAdmin.getResponseString(downloadUrl);
				assertEquals("the status error 404" + actServiceOrgBAdmin.getDetail(), 404, actServiceOrgBAdmin.getRespStatus());
	 		} finally {
	 			if (null != communityResponse) {
	 				String activityNodeEditUrl = communityResponse.getEditLinkResolvedHref().toURL().toString();
	 				boolean done = comServiceUserA.deleteCommunity(activityNodeEditUrl);
	 				assertTrue("done error" + comServiceUserA.getDetail(), done);
	 			}
	 		}
		}
 	}
 	
 	/*
     *  Test the org-admin can get activity trash nodes / trashed node / restore node 
     *  Test the org-admin can not  get trash nodes /trashed node /restore node when cross org
     *  GET /atom2/trash
     *  GET PUT /atom2/trashednode
     */
    @Test
    public void testTrashAndTrashNode() throws Exception{
   	  Entry standaloneActivityResult = null;
   	  Entry implicitCommunityActivityResult = null;
   	  String editStandaloneActivityUrl = null;
   	  String editCommnunityUrl = null;
   	  try{
   	    // Step1 create an standalone activity, then delete it for test
        Activity standaloneActivity = new Activity("activity_standalone_test_for_trash_and_trashednode",
          "content for test activity trash and trashednode", "trash_trashnode", null, false, false);
        standaloneActivityResult = (Entry) actServiceUserA.createActivity(standaloneActivity);
        Activity returnedActivity = new Activity(standaloneActivityResult);
        String acl_url = standaloneActivityResult.getLink(StringConstants.REL_MEMBERS).getHref().toURL().toString();
        Member userBMember = new Member(actUserB.getEmail(), null, Component.ACTIVITIES, Role.OWNER, MemberType.PERSON);
  	     actServiceUserA.addMemberToActivity(acl_url, userBMember);
        editStandaloneActivityUrl = standaloneActivityResult.getEditLink().getHref().toURL().toString();
        actServiceUserA.deleteActivity(editStandaloneActivityUrl);
      	  
        //Step2 create an community activity, then delete it for test
        Community community = new Community("Test community for API_trash and trashednode", "test community for API_trash_and_trashednode",
				Permissions.PRIVATE, "tag_community_API_trash_and_trashednode");
		LOGGER.fine("Create community: " + community.toString());
		Entry response = (Entry) comServiceUserA.createCommunity(community);
	    Community returnCommunity = new Community ((Entry) comServiceUserA.getCommunity(response.getEditLinkResolvedHref().toString()));
	    Widget widget = new Widget(
				StringConstants.WidgetID.Activities.toString());
	    comServiceUserA.postWidget(returnCommunity, widget.toEntry()); 
		assertEquals(201, comServiceUserA.getRespStatus());
		Member member = new Member(comUserB.getEmail(), comUserB.getUserId(),
				Component.COMMUNITIES, Role.MEMBER, MemberType.PERSON);
		Entry memberEntry = (Entry) comServiceUserA.addMemberToCommunity(returnCommunity, member);
		LOGGER.fine("Create community member: " + memberEntry.toString());
		assertEquals("Add Community Member ", 201, comServiceUserA.getRespStatus());
		String activitiesURL = actServiceUserA.getServiceURLString() + URLConstants.ACTIVITIES_MY;
		Activity communityActivity = new Activity("implicit_community_activity_for_API_trash_and_trashednode",
				  "content for API_trash_and_trashednode", null, null, false, true);
		implicitCommunityActivityResult = (Entry)  actServiceUserA.createCommunityActivity(activitiesURL,communityActivity, returnCommunity.getUuid(), "");
		editCommnunityUrl = response.getEditLink().getHref().toURL().toString();
		String editCommunityActivityUrl = implicitCommunityActivityResult.getEditLink().getHref().toURL().toString();
		Activity returnedCommActivity = new Activity(implicitCommunityActivityResult);
		actServiceUserA.deleteActivity(editCommunityActivityUrl);
		  
		//Step3 org-admin call GET /service/atom2/trash to get trash nodes for standalone activity
		String trashUrl = URLConstants.SERVER_URL + URLConstants.ACTIVITIES_BASE + URLConstants.ACTIVITIES_TRASH ;
        Feed trashFeed = (Feed)adminActService.getFeed(trashUrl);
        boolean foundActivity = false;
		for (Entry activityEntry : trashFeed.getEntries()) {
		  if (activityEntry.getId().equals(standaloneActivityResult.getId()))
		    foundActivity = true;
		}
		assertTrue(foundActivity);
		//Step4 org-admin call GET /service/atom2/trashednode to get trashed node for standalone activity
		Entry trashedStandaloneEntry = (Entry) adminActService.getFeed(URLConstants.SERVER_URL + URLConstants.ACTIVITIES_BASE 
						+ URLConstants.ACTIVITIES_SERVER + "/trashednode?activityNodeUuid="
						+ returnedActivity.getActivityId());
		assertTrue(trashedStandaloneEntry.getId().equals(standaloneActivityResult.getId()));
		 
		//Step5 org-admin call GET /service/atom2/trash to get trash nodes for community activity
        trashFeed = (Feed)adminActService.getFeed(trashUrl);
        foundActivity = false;
		for (Entry activityEntry : trashFeed.getEntries()) {
		  if (activityEntry.getId().equals(standaloneActivityResult.getId()))
		    foundActivity = true;
		}
		assertTrue(foundActivity);
		//Step6 org-admin call GET /service/atom2/trashednode to get trashed node for community activity
		Entry trashedCommunityEntry = (Entry) adminActService.getFeed(URLConstants.SERVER_URL + URLConstants.ACTIVITIES_BASE 
						+ URLConstants.ACTIVITIES_SERVER + "/trashednode?activityNodeUuid="
						+ returnedCommActivity.getActivityId());
		assertTrue(trashedCommunityEntry.getId().equals(implicitCommunityActivityResult.getId()));
		trashedStandaloneEntry =  removeDeletedCategory(trashedStandaloneEntry);
		trashedCommunityEntry = removeDeletedCategory(trashedCommunityEntry);
		/**
		  * Step7
		  * call API GET /atom2/trash to get trash nodes with crossed org org-admin
		  * call API GET /atom2/trashednode to get trashed node with crossed org org-admin
		  * call API PUT /atom2/trashednode to restore node with crossed org org-admin
		  */
		if(StringConstants.DEPLOYMENT_TYPE.equals(DeploymentType.SMARTCLOUD)){
		   Feed tFeed = (Feed)actServiceOrgBAdmin.getFeed(trashUrl);
		   assertEquals("get trash nodes error", 200, actServiceOrgBAdmin.getRespStatus());
		   assertEquals("returned trash nodes size error", 0, tFeed.getEntries().size());
			
		   actServiceOrgBAdmin.getFeed(URLConstants.SERVER_URL + URLConstants.ACTIVITIES_BASE 
					+ URLConstants.ACTIVITIES_SERVER + "/trashednode?activityNodeUuid="
					+ returnedActivity.getActivityId());
		   assertEquals("get trashed standalone activity node error", 404, actServiceOrgBAdmin.getRespStatus());
		    
		   actServiceOrgBAdmin.getFeed(URLConstants.SERVER_URL + URLConstants.ACTIVITIES_BASE 
					+ URLConstants.ACTIVITIES_SERVER + "/trashednode?activityNodeUuid="
					+ returnedCommActivity.getActivityId());
		   assertEquals("get trashed community activity node error", 404, actServiceOrgBAdmin.getRespStatus());
		    
		   actServiceOrgBAdmin.updateActivity(URLConstants.SERVER_URL + URLConstants.ACTIVITIES_BASE 
					+ URLConstants.ACTIVITIES_SERVER + "/trashednode?activityNodeUuid="
					+ returnedActivity.getActivityId(),trashedStandaloneEntry);
		    assertEquals("restore standalone activity node error", 404, actServiceOrgBAdmin.getRespStatus());
		    
		   actServiceOrgBAdmin.updateActivity(URLConstants.SERVER_URL + URLConstants.ACTIVITIES_BASE 
					+ URLConstants.ACTIVITIES_SERVER + "/trashednode?activityNodeUuid="
					+ returnedCommActivity.getActivityId(),trashedCommunityEntry);
		   assertEquals("restore community activity node error", 404, actServiceOrgBAdmin.getRespStatus());
		 }
		  
		 //Step8 org-admin call PUT /service/atom2/trashednode to restore standalone activity
		 adminActService.updateActivity(URLConstants.SERVER_URL + URLConstants.ACTIVITIES_BASE 
					+ URLConstants.ACTIVITIES_SERVER + "/trashednode?activityNodeUuid="
					+ returnedActivity.getActivityId(),trashedStandaloneEntry);
		 assertEquals("restore standalone activity node error", 204, adminActService.getRespStatus());
		  
		 //Step9 org-admin call PUT /service/atom2/trashednode to restore community activity
		 adminActService.updateActivity(URLConstants.SERVER_URL + URLConstants.ACTIVITIES_BASE 
					+ URLConstants.ACTIVITIES_SERVER + "/trashednode?activityNodeUuid="
					+ returnedCommActivity.getActivityId(),trashedCommunityEntry);
		 assertEquals("restore community activity node error", 204, adminActService.getRespStatus());
   	   }finally{
   	    if(editStandaloneActivityUrl != null){
    	   boolean deleteActivityResult = actServiceUserA.deleteActivity(editStandaloneActivityUrl);
    	   assertTrue("Activity can be deleted"+actServiceUserA.getDetail(), deleteActivityResult);
        }
        if(editCommnunityUrl != null){
    	  boolean deleteCommResult = comServiceUserA.deleteCommunity(editCommnunityUrl);
    	  assertTrue("Community can be deleted"+comServiceUserA.getDetail(), deleteCommResult);
        }
   	  }
    }
    protected  Entry removeDeletedCategory(Entry trashedEntry){
      List<Category> categories = trashedEntry.getCategories();
	  for (Category category : categories) {
	    if ("deleted".equalsIgnoreCase(category.getTerm()))
		  category.discard();
		}
	 return trashedEntry;
   }
}
