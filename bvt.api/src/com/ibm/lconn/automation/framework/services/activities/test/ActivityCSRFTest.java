/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2008, 2012                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.lconn.automation.framework.services.activities.test;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import java.util.Date;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Collection;
import org.apache.abdera.model.Entry;
import org.apache.abdera.protocol.client.AbderaClient;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.services.activities.ActivitiesService;
import com.ibm.lconn.automation.framework.services.activities.nodes.Activity;
import com.ibm.lconn.automation.framework.services.activities.nodes.ActivityEntry;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Component;
import com.ibm.lconn.automation.framework.services.common.StringConstants.DeploymentType;
import com.ibm.lconn.automation.framework.services.common.StringConstants.MemberType;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Role;
import com.ibm.lconn.automation.framework.services.common.UserPerspective;
import com.ibm.lconn.automation.framework.services.common.UsersEnvironment;
import com.ibm.lconn.automation.framework.services.common.nodes.Member;

public class ActivityCSRFTest {

	private static Factory _factory = Abdera.getNewFactory();;

	private static String activityColUrl;

	private static ActivitiesService as;

	private static String activityUrl, activityNodeUrl, aclUrl, memberEditUrl,
			entryEditUrl, activityNodeEditUrl;

	static UserPerspective user, user2;

	@BeforeClass
	public void setUp() throws Exception {
		UsersEnvironment userEnv = new UsersEnvironment();
		user = userEnv.getLoginUserEnvironment(9,
				Component.ACTIVITIES.toString());
		as = user.getActivitiesService();

		testCreateActivity();
	}

	@AfterClass
	public void tearDown() throws Exception {
		testDeleteActivity();
		as.tearDown();
	}

	// @Test
	public void testCreateActivity() throws Exception {
		// TODO SC enable
		if (!(StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD)) {

			/************** Activity ****************************************************************/
			/* start: Create a test activity */
			Entry actEntry = _factory.newEntry();

			actEntry.setTitle("CSRF");
			actEntry.setContent("Save Dulcinea from evil giants");
			actEntry.setUpdated(new Date());
			assert (as != null);
			assert (as.isFoundService());

			as.addRequestOption("Origin", "something");

			// add an activity for test
			Activity simpleActivity = new Activity(actEntry);
			as.createActivity(simpleActivity);
			assertEquals(403, as.getRespStatus());

			as.removeRequestOption("Origin");

			// add an activity for test
			Entry activityResult = (Entry) as.createActivity(simpleActivity);
			assertTrue(activityResult != null);
			assertEquals(201, as.getRespStatus());

			activityNodeUrl = as.getRespLocation();
			System.out.println("activity location = " + activityNodeUrl);
			activityUrl = activityResult.getSelfLinkResolvedHref().toURL()
					.toString();
			System.out.println("activity self url = " + activityUrl);
			activityColUrl = ((Collection) activityResult
					.getExtension(StringConstants.APP_COLLECTION)).getHref()
					.toString();
			System.out.println("activity collection url = " + activityColUrl);
			aclUrl = activityResult
					.getLink("http://www.ibm.com/xmlns/prod/sn/member-list")
					.getHref().toURL().toString();
			System.out.println("activity acl url = " + aclUrl);
			activityNodeEditUrl = activityResult.getEditLinkResolvedHref()
					.toURL().toString();

			assertTrue(activityNodeUrl != null && activityUrl != null
					&& aclUrl != null);
			assertTrue(activityNodeUrl.equalsIgnoreCase(activityResult
					.getEditLinkResolvedHref().toURL().toString()));
		}
	}

	@Test
	public void testAddActivityEntry() throws Exception {

		// TODO SC enable
		if (!(StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD)) {

			/************** Message *****************************************************************/
			/* start: Create a test message */
			Entry actEntry = _factory.newEntry();

			actEntry.setTitle("Don Quixote - Message");
			actEntry.setContent("Don Quixote begins his adventure to save Dulcinea del Toboso from the evil windmills.");
			actEntry.setUpdated(new Date());

			as.addRequestOption("Origin", "something");
			ActivityEntry simpleEntry = new ActivityEntry(actEntry);
			as.addNodeToActivity(activityColUrl, simpleEntry);
			assertEquals(403, as.getRespStatus());

			as.removeRequestOption("Origin");
			Entry activityResult = (Entry) as.addNodeToActivity(activityColUrl,
					simpleEntry);
			assertTrue(activityResult != null);
			assertEquals(201, as.getRespStatus());

			String messageEditUrl = as.getRespLocation();
			System.out.println("messageEditUrl = " + messageEditUrl);
			String entryEditUrl = activityResult.getEditLink().getHref()
					.toURL().toString();
			System.out.println("message entry edit url = " + entryEditUrl);
			assertTrue(messageEditUrl.equalsIgnoreCase(entryEditUrl));
			/* end: Create a test message */
		}
	}

	@Test
	public void testAddMember() throws Exception {

		// TODO SC enable
		if (!(StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD)) {
			/* Add members */

			String user = "ajones334@janet.iris.com";
			Member newMember = new Member(user, null, Component.ACTIVITIES,
					Role.MEMBER, MemberType.PERSON);

			as.addRequestOption("Origin", "something");
			as.addMemberToActivity(aclUrl, newMember);
			assertEquals(403, as.getRespStatus());

			as.removeRequestOption("Origin");
			as.addMemberToActivity(aclUrl, newMember);
			assertEquals(201, as.getRespStatus());

			// entry = _client.post(aclUrl, entry);
			// assertTrue(_client.getStatusCode() == 201 && entry != null);
			// assertTrue(_client.getLocation() != null);

		}

	}

	@Test
	public void testAddActivityEntryWithTrustedWebsite() throws Exception {

		// TODO SC enable
		if (!(StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD)) {

			/************** Message *****************************************************************/
			/* start: Create a test message */
			Entry actEntry = _factory.newEntry();

			actEntry.setTitle("Test Entry with trusted Origin");
			actEntry.setContent("Test Entry with trusted Origin");
			actEntry.setUpdated(new Date());

			as.addRequestOption("Origin", "http://fakedomain.com");
			ActivityEntry simpleEntry = new ActivityEntry(actEntry);
			
			Entry activityResult = (Entry) as.addNodeToActivity(activityColUrl,
					simpleEntry);
			assertTrue(activityResult != null);
			assertEquals(201, as.getRespStatus());

			String messageEditUrl = as.getRespLocation();
			String entryEditUrl = activityResult.getEditLink().getHref()
					.toURL().toString();
			assertTrue(messageEditUrl.equalsIgnoreCase(entryEditUrl));
			/* end: Create a test message */
		}
	}
	
	@Test
	public void testDeleteActivity() throws Exception {
		// TODO SC enable
		if (!(StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD)) {
			/* Delete the test activity */
			System.out.println("delete the activity: " + activityNodeEditUrl);
			boolean done = as.deleteActivity(activityNodeEditUrl);
			assertTrue(done);
			assertEquals(204, as.getRespStatus());
		}
	}
}
