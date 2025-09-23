/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2012, 2013, 2015                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.lconn.automation.framework.services.profiles.connection.admin;

import java.util.ArrayList;
import java.util.List;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Service;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.UserPerspective;
import com.ibm.lconn.automation.framework.services.common.UsersEnvironment;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Component;
import com.ibm.lconn.automation.framework.services.common.StringConstants.DeploymentType;
import com.ibm.lconn.automation.framework.services.profiles.ProfilesService;
import com.ibm.lconn.automation.framework.services.profiles.base.AbstractTest;
import com.ibm.lconn.automation.framework.services.profiles.connection.ConnectionBase;
import com.ibm.lconn.automation.framework.services.profiles.connection.user.ColleagueTest;
import com.ibm.lconn.automation.framework.services.profiles.model.ColleagueConnection;
import com.ibm.lconn.automation.framework.services.profiles.model.ConnectionEntry;
import com.ibm.lconn.automation.framework.services.profiles.model.ConnectionEntry.ACTION;
import com.ibm.lconn.automation.framework.services.profiles.model.ConnectionEntry.STATUS;
import com.ibm.lconn.automation.framework.services.profiles.model.ProfileService;
//import com.ibm.lconn.automation.framework.services.profiles.user.messageboard.ColleaguesMessageBoardApiTest;
import com.ibm.lconn.automation.framework.services.profiles.util.ApiConstants;
import com.ibm.lconn.automation.framework.services.profiles.util.HTTPResponseValidator;
import com.ibm.lconn.automation.framework.services.profiles.util.ServiceDocUtil;
import com.ibm.lconn.automation.framework.services.profiles.util.Transport;
import com.ibm.lconn.automation.framework.services.profiles.util.URLBuilder;

/**
 * Network connections (colleagues) admin tests. Admin can manage connections on behalf of two other users.
 * 
 */
public class AdminColleagueTest extends ConnectionBase {

	static final long time = System.currentTimeMillis();
	ColleagueConnection existingConnectionUser1;
	ColleagueConnection existingConnectionUser2;
	ProfileService profilesServiceUser1;
	ProfileService profilesServiceUser2;
	ProfileService profilesServiceUser3;
	ProfileService profilesServiceAdmin = null;
	
	final static int ORGBUSER = 15; // OrgB user
	static UserPerspective orgBUser;
	ProfilesService orgBUserService;
	String orgBUserId = null;
	String orgBUserKey = null;
	String orgBUserEmail = null;

	protected static Transport orgBUserTransport;

	@BeforeClass
	public void setUpConnection() throws Exception {
		UsersEnvironment userEnv = new UsersEnvironment();
		orgBUser = userEnv.getLoginUserEnvironment(ORGBUSER, Component.PROFILES.toString());
		
		orgBUserService = orgBUser.getProfilesService();
		orgBUserId = orgBUserService.getUserId();
		orgBUserKey = orgBUserService.getUserKey();
		orgBUserEmail = orgBUser.getEmail();
		orgBUserTransport = new Transport(orgBUser, orgBUserService);
		
		// if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.ON_PREMISE) {

			profilesServiceUser1 = ServiceDocUtil.getUserServiceDocument(user1Transport);
			profilesServiceUser2 = ServiceDocUtil.getUserServiceDocument(user2Transport);
			profilesServiceUser3 = ServiceDocUtil.getUserServiceDocument(user3Transport);

			// make certain the users are distinct
			Assert.assertFalse(profilesServiceUser1.getUserId().equals(profilesServiceUser2.getUserId()));
			Assert.assertFalse(profilesServiceUser1.getUserId().equals(profilesServiceUser3.getUserId()));

			// and if the admin has a profile, make certain it is distinct from the user2
			Service rawAdminService = adminTransport.doAtomGet(Service.class, urlBuilder.getProfilesServiceDocument(), NO_HEADERS, null);
			if (null == rawAdminService) {
				// admin has no profile, continue
			}
			else {
				profilesServiceAdmin = ProfileService.parseFrom(rawAdminService);
				Assert.assertFalse(profilesServiceAdmin.getUserId().equals(profilesServiceUser2.getUserId()));
			}

			// save existing connection, if any, to restore later.
			existingConnectionUser1 = getColleagueConnection(user1Transport, user2Transport, ColleagueConnection.STATUS_ALL, false);
			existingConnectionUser2 = getColleagueConnection(user2Transport, user1Transport, ColleagueConnection.STATUS_ALL, false);

			if (null == existingConnectionUser1 && null == existingConnectionUser2) {
				; // users are not connected, so no-op
			}
			else if (null != existingConnectionUser1 && null != existingConnectionUser2) {
				// users are connectected, delete connection
				user1Transport.doAtomDelete(existingConnectionUser1.getEditLink(), NO_HEADERS, HTTPResponseValidator.OK);
			}
			else {
				// unexpected state, clean up
				deleteColleagueConnection(user1Transport, user2Transport, ColleagueConnection.STATUS_ALL, false);
				// make certain to skip restoration step
				existingConnectionUser1 = null;
				existingConnectionUser2 = null;
			}
		// }
	}

	@AfterClass
	public void tearDownConnection() throws Exception {
		// if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.ON_PREMISE) {
			// restore old connection, if there was one. This isn't a "real restore" of the original connection, it only sets up a
			// connection
			// between the users similar to before the test.
			if (null != existingConnectionUser1 && null != existingConnectionUser2) {
				if (STATUS.pending.equals(existingConnectionUser1.getStatus())) {
					createColleagueConnection(user2Transport, user1Transport, existingConnectionUser2.getContent(),
							existingConnectionUser1.getContent(), STATUS.accepted.equals(existingConnectionUser1.getStatus()), false);
				}
				else if (STATUS.unconfirmed.equals(existingConnectionUser1.getStatus())) {
					createColleagueConnection(user1Transport, user2Transport, existingConnectionUser1.getContent(),
							existingConnectionUser2.getContent(), STATUS.accepted.equals(existingConnectionUser1.getStatus()), false);
				}
				// else {
				// // TODO: if needed, determine invite/accept order for this case
				// createColleagueConnection(user1Transport, user2Transport, existingConnectionUser1.getContent(),
				// existingConnectionUser2.getContent(), STATUS.accepted.equals(existingConnectionUser1.getStatus()), false);
				// }

				ColleagueConnection restoredConnectionMain = getColleagueConnection(user1Transport, user2Transport,
						ColleagueConnection.STATUS_ALL, false);
				ColleagueConnection restoredConnectionOther = getColleagueConnection(user2Transport, user1Transport,
						ColleagueConnection.STATUS_ALL, false);

				Assert.assertEquals(existingConnectionUser1.getStatus(), restoredConnectionMain.getStatus());
				Assert.assertEquals(existingConnectionUser2.getStatus(), restoredConnectionOther.getStatus());
			}
		// }
	}

	/**
	 * unlike <code>com.ibm.lconn.profiles.test.rest.junit.ConnectionTest.testAdminDeleteConnection()</code>, in this test we'll use
	 * /profiles/admin/atom URI to delete
	 * 
	 * @throws Exception
	 */

	@Test
	public void testAdminDeleteConnectionEntry() throws Exception {

		// if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.ON_PREMISE) {
			// create test connection
			String messageInvite = time + " " + this.getClass().getSimpleName() + ".testAdminDeleteConnectionEntry() invitation message";
			String messageAccept = time + " " + this.getClass().getSimpleName() + ".testAdminDeleteConnectionEntry() accept message";

			ColleagueConnection testConnection = createColleagueConnection(user1Transport, user2Transport, messageInvite, messageAccept,
					false, false);

			// set up admin/edit link to connection
			String testConnectionAdminEditUrl = urlBuilder.getProfilesAdminConnectionEditUrl(testConnection.getId());

			// TEST: non-admin cannot delete on admin URI
			user1Transport.doAtomDelete(testConnectionAdminEditUrl, AbstractTest.NO_HEADERS, HTTPResponseValidator.FORBIDDEN);

			// TEST: admin not permitted to PUT/POST to edit URL
			// this will likely be deprecated soon, leave in for now to demonstrate function
			adminTransport.doAtomPut(null, testConnectionAdminEditUrl, ColleagueConnection.getRequestEntry("accepted", STATUS.accepted),
					NO_HEADERS, HTTPResponseValidator.METHOD_NOT_ALLOWED);

			// TEST: admin delete test connection
			adminTransport.doAtomDelete(testConnectionAdminEditUrl, AbstractTest.NO_HEADERS, HTTPResponseValidator.OK);

			// set up for new connection ... preserve existing connection if anyone ever complains
			deleteColleagueConnection(user3Transport, user2Transport, ColleagueConnection.STATUS_ALL, false);
			// create a test connection that a non-admin user is NOT involved in
			testConnection = ColleagueTest.createColleagueConnection(user3Transport, user2Transport, messageInvite, messageAccept, true,
					false);
			// set up admin/edit link to connection
			testConnectionAdminEditUrl = urlBuilder.getProfilesAdminConnectionEditUrl(testConnection.getId());

			// TEST: non-involved user should not be able to delete test connection on admin URI
			user1Transport.doAtomDelete(testConnectionAdminEditUrl, AbstractTest.NO_HEADERS, HTTPResponseValidator.FORBIDDEN);

			// admin can delete test connection
			adminTransport.doAtomDelete(testConnectionAdminEditUrl, AbstractTest.NO_HEADERS, HTTPResponseValidator.OK);
		// }
	}

	/**
	 * connection operations by admin on admin API endpoint
	 * 
	 * @throws Exception
	 */

	@Test
	public void testAdminCreateConnections() throws Exception {

		// if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.ON_PREMISE) {
			// TEST: bogus action param value
			String url = urlBuilder.getProfilesAdminConnectionsUrl(null, "bogus", profilesServiceUser1.getUserId(),
					profilesServiceUser2.getUserId());
			adminTransport.doAtomPut(Entry.class, url, ABDERA.newEntry(), NO_HEADERS, HTTPResponseValidator.BAD_REQUEST);

			// TEST: missing action param
			url = urlBuilder.getProfilesAdminConnectionsUrl(null, null, profilesServiceUser1.getUserId(), profilesServiceUser2.getUserId());
			adminTransport.doAtomPut(Entry.class, url, ABDERA.newEntry(), NO_HEADERS, HTTPResponseValidator.BAD_REQUEST);

			// TEST: empty action param
			url = urlBuilder.getProfilesAdminConnectionsUrl(null, "", profilesServiceUser1.getUserId(), profilesServiceUser2.getUserId());
			adminTransport.doAtomPut(Entry.class, url, ABDERA.newEntry(), NO_HEADERS, HTTPResponseValidator.BAD_REQUEST);

			// TEST: verify "accept" action is not implemented
			url = urlBuilder.getProfilesAdminConnectionsUrl(null, ACTION.accept.name(), profilesServiceUser1.getUserId(),
					profilesServiceUser2.getUserId());
			adminTransport.doAtomPut(Entry.class, url, ABDERA.newEntry(), NO_HEADERS, HTTPResponseValidator.BAD_REQUEST);

			// TEST: verify "reject" action is not implemented
			url = urlBuilder.getProfilesAdminConnectionsUrl(null, ACTION.reject.name(), profilesServiceUser1.getUserId(),
					profilesServiceUser2.getUserId());
			adminTransport.doAtomPut(Entry.class, url, ABDERA.newEntry(), NO_HEADERS, HTTPResponseValidator.BAD_REQUEST);

			// TEST: bogus inviter param value
			url = urlBuilder.getProfilesAdminConnectionsUrl(null, ACTION.complete.name(), "bogus", profilesServiceUser2.getUserId());
			adminTransport.doAtomPut(Entry.class, url, ABDERA.newEntry(), NO_HEADERS, HTTPResponseValidator.BAD_REQUEST);

			// TEST: missing inviter param
			url = urlBuilder.getProfilesAdminConnectionsUrl(null, ACTION.complete.name(), null, profilesServiceUser2.getUserId());
			adminTransport.doAtomPut(Entry.class, url, ABDERA.newEntry(), NO_HEADERS, HTTPResponseValidator.BAD_REQUEST);

			// TEST: bogus invitee param value
			url = urlBuilder.getProfilesAdminConnectionsUrl(null, ACTION.complete.name(), profilesServiceUser2.getUserId(), "bogus");
			adminTransport.doAtomPut(Entry.class, url, ABDERA.newEntry(), NO_HEADERS, HTTPResponseValidator.BAD_REQUEST);

			// TEST: missing invitee param
			url = urlBuilder.getProfilesAdminConnectionsUrl(null, ACTION.complete.name(), profilesServiceUser2.getUserId(), null);
			adminTransport.doAtomPut(Entry.class, url, ABDERA.newEntry(), NO_HEADERS, HTTPResponseValidator.BAD_REQUEST);

			// TEST: missing all params
			url = urlBuilder.getProfilesAdminConnectionsUrl(null, null, null, null);
			adminTransport.doAtomPut(Entry.class, url, ABDERA.newEntry(), NO_HEADERS, HTTPResponseValidator.BAD_REQUEST);

			// TEST: verify POST method is not implemented
			url = urlBuilder.getProfilesAdminConnectionsUrl(null, ACTION.complete.name(), profilesServiceUser1.getUserId(),
					profilesServiceUser2.getUserId());
			adminTransport.doAtomPost(Entry.class, url, ABDERA.newEntry(), NO_HEADERS, HTTPResponseValidator.METHOD_NOT_ALLOWED);

			// TEST: verify non-admin cannot access admin url
			adminImpersonateColleagueConnection(user1Transport, null, user1Transport, user2Transport, HTTPResponseValidator.FORBIDDEN,
					null, ACTION.complete, true);

			// TEST: verify a user cannot connect a user to itself
			adminImpersonateColleagueConnection(adminTransport, null, user1Transport, user1Transport, HTTPResponseValidator.BAD_REQUEST,
					null, ACTION.complete, true);

			// TEST: admin sends an Invitation from User1 to User2
			ColleagueConnection testConnection = adminImpersonateColleagueConnection(adminTransport, null, user1Transport, user2Transport,
					HTTPResponseValidator.CREATED, null, ACTION.invite, true);

			// TEST: admin tries to resend an Invitation from User1 to User2
			adminImpersonateColleagueConnection(adminTransport, null, user1Transport, user2Transport, HTTPResponseValidator.BAD_REQUEST,
					null, ACTION.invite, true);

			// TEST: admin creates completed connection between 2 users which is currently in pending state
			testConnection = adminImpersonateColleagueConnection(adminTransport, null, user1Transport, user2Transport,
					HTTPResponseValidator.CREATED, null, ACTION.complete, true);

			// TEST: admin tries to change connection to Pending from Completed state - Bad request
			adminImpersonateColleagueConnection(adminTransport, null, user1Transport, user2Transport, HTTPResponseValidator.BAD_REQUEST,
					null, ACTION.invite, true);

			// cleanup: "source" user deletes test connection
			adminTransport.doAtomDelete(testConnection.getEditLink(), AbstractTest.NO_HEADERS, HTTPResponseValidator.OK);
			testConnection = null;
		// }
	}

	/**
	 * connection operations by admin on admin API endpoint
	 * 
	 * @throws Exception
	 */

	@Test
	public void testAdminCreateConnectionsCrossOrg() throws Exception {

		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {

			// TEST: admin can not send an Invitation from User1 to orgBUser in a different org
			ColleagueConnection testConnection = adminImpersonateColleagueConnection(adminTransport, null, user1Transport, orgBUserTransport,
					HTTPResponseValidator.BAD_REQUEST, null, ACTION.invite, true);

			testConnection = null;
		}
	}

	/**
	 * connection operations by admin on admin API endpoint
	 * 
	 * @throws Exception
	 */

	@Test
	public void testAdminDeleteConnections() throws Exception {

		// if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.ON_PREMISE) {
			// setup: admin creates completed connection between user1 and user2
			ColleagueConnection testConnection = adminImpersonateColleagueConnection(adminTransport, null, user1Transport, user2Transport,
					HTTPResponseValidator.CREATED, null, ACTION.complete, true);

			// TEST: admin deletes the connection between user1 and user2
			String url = urlBuilder.getProfilesAdminConnectionsUrl(testConnection.getId(), ACTION.complete.name(),
					profilesServiceUser1.getUserId(), profilesServiceUser2.getUserId());
			adminTransport.doAtomDelete(url, AbstractTest.NO_HEADERS, HTTPResponseValidator.OK);

			// TEST: repeat the deletion to confirm server response
			adminTransport.doAtomDelete(url, AbstractTest.NO_HEADERS, HTTPResponseValidator.NOT_FOUND);

			// TEST: admin deletes all connections by BOGUS userid
			url = urlBuilder.getProfilesAdminConnectionsUrl(null, null, null, null);
			url = URLBuilder.addQueryParameter(url, ApiConstants.SocialNetworking.USER_ID.getLocalPart(), "BOGUS_USER_ID", true);
			adminTransport.doAtomDelete(url, AbstractTest.NO_HEADERS, HTTPResponseValidator.NOT_FOUND);
		// }
	}

	// test that the admin can act as a regular user and create connections via public api
	// This test requires that the 'admin' has to be a valid Profiles user.
	@Test
	public void testPublicConnectionAsAdmin() throws Exception {
		// if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.ON_PREMISE) {
			// save existing connection, if any, to restore later.
			// TEST: Gets feeds of connections in all states by sending ColleagueConnection.STATUS_ALL.
			ColleagueConnection existingConnectionAdmin = getColleagueConnection(adminTransport, user2Transport,
					ColleagueConnection.STATUS_ALL, false);
			ColleagueConnection existingConnectionUser2 = getColleagueConnection(user2Transport, adminTransport,
					ColleagueConnection.STATUS_ALL, false);

			if (null == existingConnectionAdmin && null == existingConnectionUser2) {
				; // users are not connected, so no-op
			}
			else if (null != existingConnectionAdmin && null != existingConnectionUser2) {
				// users are connected, delete connection
				adminTransport.doAtomDelete(existingConnectionAdmin.getEditLink(), NO_HEADERS, HTTPResponseValidator.OK);
			}
			else {
				// unexpected state, clean up
				deleteColleagueConnection(adminTransport, user2Transport, ColleagueConnection.STATUS_ALL, false);
				// make certain to skip restoration step
				existingConnectionAdmin = null;
				existingConnectionUser2 = null;
			}

			String messageInvite = time + " " + this.getClass().getSimpleName() + ".testPublicConnectionAsAdmin() invitation message";
			String messageAccept = time + " " + this.getClass().getSimpleName() + ".testPublicConnectionAsAdmin() accept message";
			createColleagueConnection(adminTransport, user2Transport, messageInvite, messageAccept, true, true);

			// restore old connection, if there was one. This isn't a "real restore" of the original connection, it only sets up a
			// connection
			// between the users similar to before the test.
			if (null != existingConnectionAdmin && null != existingConnectionUser2) {
				if (STATUS.pending.equals(existingConnectionAdmin.getStatus())) {
					createColleagueConnection(user2Transport, user1Transport, existingConnectionUser2.getContent(),
							existingConnectionAdmin.getContent(), STATUS.accepted.equals(existingConnectionAdmin.getStatus()), false);
				}
				else if (STATUS.unconfirmed.equals(existingConnectionAdmin.getStatus())) {
					createColleagueConnection(user1Transport, user2Transport, existingConnectionAdmin.getContent(),
							existingConnectionUser2.getContent(), STATUS.accepted.equals(existingConnectionAdmin.getStatus()), false);
				}
				else {
					// TODO: if needed, determine invite/accept order for this case
					createColleagueConnection(user1Transport, user2Transport, existingConnectionAdmin.getContent(),
							existingConnectionUser2.getContent(), STATUS.accepted.equals(existingConnectionAdmin.getStatus()), false);
				}

				ColleagueConnection restoredConnectionMain = getColleagueConnection(adminTransport, user2Transport,
						ColleagueConnection.STATUS_ALL, false);
				ColleagueConnection restoredConnectionOther = getColleagueConnection(user2Transport, adminTransport,
						ColleagueConnection.STATUS_ALL, false);

				Assert.assertEquals(existingConnectionAdmin.getStatus(), restoredConnectionMain.getStatus());
				Assert.assertEquals(existingConnectionUser2.getStatus(), restoredConnectionOther.getStatus());
			}
		// }
	}

	@Test
	public void testAdminGetConnectionsByStatus() throws Exception {

		// if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.ON_PREMISE) {

			// Trying to retreive all connections of a user using a non-admin user - should fail
			String colleagueLinkT = profilesServiceUser1.getLinkHref(ApiConstants.SocialNetworking.REL_COLLEAGUE);
			for (ColleagueConnection.STATUS s : ColleagueConnection.STATUS_ALL) {
				if (!s.equals(ColleagueConnection.STATUS.accepted)) {
					user2Transport.doAtomGet(Feed.class, URLBuilder.addQueryParameter(colleagueLinkT, "status", s.name(), false),
							HTTPResponseValidator.BAD_REQUEST, true);
				}
			}

			ColleagueConnection testConnection = adminImpersonateColleagueConnection(adminTransport, null, user1Transport, user2Transport,
					HTTPResponseValidator.CREATED, null, ACTION.invite, true);

			// Retrieving all the connections
			List<ColleagueConnection> allConnections = adminGetAllConnections(user1Transport, true);

			// Retrieving all the pending connections
			List<ColleagueConnection> pendingConnections = adminGetAllConnectionsByStatus(user1Transport, ConnectionEntry.STATUS.pending,
					true);

			// Retrieving all the unconfirmed connections
			List<ColleagueConnection> unconfirmedConnections = adminGetAllConnectionsByStatus(user1Transport,
					ConnectionEntry.STATUS.unconfirmed, true);

			// Retrieving all the pending connections
			List<ColleagueConnection> acceptedConnections = adminGetAllConnectionsByStatus(user1Transport, ConnectionEntry.STATUS.accepted,
					true);

			// Checking if the unconfirmed connections are from user1 and are in pending state
			for (ColleagueConnection connection : unconfirmedConnections) {
				Assert.assertEquals(connection.getSource().getUserId(), profilesServiceUser1.getUserId());
				Assert.assertEquals(connection.getStatus(), STATUS.unconfirmed);
			}

			// Check if unconfirmed connections count is valid
			if (acceptedConnections.isEmpty() && pendingConnections.isEmpty()) {
				Assert.assertTrue(allConnections.size() == unconfirmedConnections.size());
			}

			// cleanup: "source" user deletes test connection
			adminTransport.doAtomDelete(testConnection.getEditLink(), AbstractTest.NO_HEADERS, HTTPResponseValidator.OK);
		}

	// }
}
