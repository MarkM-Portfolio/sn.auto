/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2012, 2015                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.lconn.automation.framework.services.profiles.connection.user;

import junit.framework.Assert;
import org.testng.annotations.Test;
import com.ibm.lconn.automation.framework.services.profiles.connection.ConnectionBase;
import com.ibm.lconn.automation.framework.services.profiles.model.ColleagueConnection;
import com.ibm.lconn.automation.framework.services.profiles.model.ConnectionEntry.STATUS;
import com.ibm.lconn.automation.framework.services.profiles.util.HTTPResponseValidator;

public class ColleagueTest extends ConnectionBase {
	static final long time = System.currentTimeMillis();

	// basic test that creates a connection and completes it.

	@Test
	public void testConnections() throws Exception {
		// save existing connection, if any, to restore later.
		// TEST: Gets feeds of connections in all states by sending ColleagueConnection.STATUS_ALL.
		ColleagueConnection existingConnectionUser1 = getColleagueConnection(user1Transport, user2Transport,
				ColleagueConnection.STATUS_ALL, false);
		ColleagueConnection existingConnectionUser2 = getColleagueConnection(user2Transport, user1Transport,
				ColleagueConnection.STATUS_ALL, false);

		if (null == existingConnectionUser1 && null == existingConnectionUser2) {
			; // users are not connected, so no-op
		}
		else if (null != existingConnectionUser1 && null != existingConnectionUser2) {
			// users are connected, delete connection
			user1Transport.doAtomDelete(existingConnectionUser1.getEditLink(), NO_HEADERS, HTTPResponseValidator.OK);
		}
		else {
			// unexpected state, clean up
			deleteColleagueConnection(user1Transport, user2Transport, ColleagueConnection.STATUS_ALL, false);
			// make certain to skip restoration step
			existingConnectionUser1 = null;
			existingConnectionUser2 = null;
		}

		// TEST: create test connection
		String messageInvite = time + " " + this.getClass().getSimpleName() + ".testConnections() invitation message";
		String messageAccept = time + " " + this.getClass().getSimpleName() + ".testConnections() accept message";

		// verify that we can create only one instance of a connection between any two people and cannot create one where an existing
		// connection exists
		int i = 1;
		ColleagueConnection colleague = null;
		while (i < 3) {
			try {
				if (i > 1) {
					// expect a NULL object (HTTP 400 response)
					colleague = createColleagueConnection400(user1Transport, user2Transport, messageInvite, messageAccept, true, true);
					Assert.assertNull(colleague);
				}
				else {
					// expect a valid object (HTTP 201 response)
					colleague = createColleagueConnection(user1Transport, user2Transport, messageInvite, messageAccept, true, true);
					Assert.assertNotNull(colleague);
				}
				i++;
			}
			catch (Exception e) {
				System.out.println("already connected : [" + i + "] " + e.getMessage());
				e.printStackTrace();
			}
		}

		// clean up test connection
		deleteColleagueConnection(user1Transport, user2Transport, ColleagueConnection.STATUS_ALL, false);

		// restore old connection, if there was one. This isn't a "real restore" of the original connection, it only sets up a connection
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
			else {
				// TODO: if needed, determine invite/accept order for this case
				createColleagueConnection(user1Transport, user2Transport, existingConnectionUser1.getContent(),
						existingConnectionUser2.getContent(), STATUS.accepted.equals(existingConnectionUser1.getStatus()), false);
			}

			ColleagueConnection restoredConnectionUser1 = getColleagueConnection(user1Transport, user2Transport,
					ColleagueConnection.STATUS_ALL, false);
			ColleagueConnection restoredConnectionUser2 = getColleagueConnection(user2Transport, user1Transport,
					ColleagueConnection.STATUS_ALL, false);

			Assert.assertEquals(existingConnectionUser1.getStatus(), restoredConnectionUser1.getStatus());
			Assert.assertEquals(existingConnectionUser2.getStatus(), restoredConnectionUser2.getStatus());
		}
	}

	// admin delete on public API URIs is not documented, but tested here to monitor for inadvertent changes in behavior
	// This test requires that the 'admin' account is a valid Profiles user

}
