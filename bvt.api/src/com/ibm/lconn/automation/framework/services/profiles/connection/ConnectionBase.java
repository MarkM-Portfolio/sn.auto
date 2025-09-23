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

package com.ibm.lconn.automation.framework.services.profiles.connection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.testng.Assert;
import com.ibm.lconn.automation.framework.services.profiles.base.AbstractTest;
import com.ibm.lconn.automation.framework.services.profiles.model.ColleagueConnection;
import com.ibm.lconn.automation.framework.services.profiles.model.ColleagueFeed;
import com.ibm.lconn.automation.framework.services.profiles.model.ConnectionEntry;
import com.ibm.lconn.automation.framework.services.profiles.model.ConnectionEntry.ACTION;
import com.ibm.lconn.automation.framework.services.profiles.model.ConnectionEntry.STATUS;
import com.ibm.lconn.automation.framework.services.profiles.model.ProfileService;
import com.ibm.lconn.automation.framework.services.profiles.util.ApiConstants;
import com.ibm.lconn.automation.framework.services.profiles.util.HTTPResponseValidator;
import com.ibm.lconn.automation.framework.services.profiles.util.ServiceDocUtil;
import com.ibm.lconn.automation.framework.services.profiles.util.Transport;
import com.ibm.lconn.automation.framework.services.profiles.util.URLBuilder;

/**
 * @author eedavis
 * 
 */
public class ConnectionBase extends AbstractTest {

	/**
	 * Cleanup method to delete connection between 2 users in the given statuses. If you're interested in preserving the existing state of
	 * the system, use <code>getColleagueConnection(...)</code> to capture invitation message and do manual delete/recreate operations.
	 * 
	 * @param t
	 * @param u
	 * @param statuses
	 * @param verbose
	 * @return existing connection from t, if any
	 * @throws Exception
	 */
	public static void deleteColleagueConnection(Transport t, Transport u, Collection<ColleagueConnection.STATUS> statuses, boolean verbose)
			throws Exception {
		// get service documents
		ProfileService profilesServiceT = ServiceDocUtil.getUserServiceDocument(t);
		ProfileService profilesServiceU = ServiceDocUtil.getUserServiceDocument(u);

		// link to colleague feeds for users T and U
		String colleagueLinkT = profilesServiceT.getLinkHref(ApiConstants.SocialNetworking.REL_COLLEAGUE);
		String colleagueLinkU = profilesServiceU.getLinkHref(ApiConstants.SocialNetworking.REL_COLLEAGUE);

		// userIds for IDing connections in responses
		String user1Id = profilesServiceT.getUserId();
		String user2Id = profilesServiceU.getUserId();

		for (ColleagueConnection.STATUS s : statuses) {
			Feed feed = t.doAtomGet(Feed.class, URLBuilder.addQueryParameter(colleagueLinkT, "status", s.name(), false),
					HTTPResponseValidator.OK, verbose);
			ColleagueFeed colleagueFeedT = new ColleagueFeed(feed).validate();

			// clear the connection from "t"
			for (ColleagueConnection e : colleagueFeedT.getEntries()) {
				if (user2Id.equals(e.getSource().getUserId()) || user2Id.equals(e.getTarget().getUserId())) {
					t.doAtomDelete(e.getEditLink(), AbstractTest.NO_HEADERS, HTTPResponseValidator.OK);
				}
			}

			feed = u.doAtomGet(Feed.class, URLBuilder.addQueryParameter(colleagueLinkU, "status", s.name(), false),
					HTTPResponseValidator.OK, verbose);
			ColleagueFeed colleagueFeedU = new ColleagueFeed(feed).validate();

			// and from "u"
			for (ColleagueConnection e : colleagueFeedU.getEntries()) {
				if (user1Id.equals(e.getSource().getUserId()) || user1Id.equals(e.getTarget().getUserId()))
					u.doAtomDelete(e.getEditLink(), AbstractTest.NO_HEADERS, HTTPResponseValidator.OK);
			}
		}
	}

	public static ColleagueConnection getColleagueConnection(Transport source, Transport target,
			Collection<ColleagueConnection.STATUS> statuses, boolean verbose) throws Exception {
		ColleagueConnection retVal = null;

		// get service documents
		ProfileService profilesServiceSource = ServiceDocUtil.getUserServiceDocument(source);
		ProfileService profilesServiceTarget = ServiceDocUtil.getUserServiceDocument(target);

		// userIds for IDing connections in responses
		String userIdSource = profilesServiceSource.getUserId();
		String userIdTarget = profilesServiceTarget.getUserId();

		String url = urlBuilder.getVerifyColleaguesUrl(userIdSource, userIdTarget);

		Entry rawEntry = source.doAtomGet(Entry.class, url, null, verbose);
		if (verbose) prettyPrint(rawEntry);

		if (null != rawEntry) retVal = new ColleagueConnection(rawEntry).validate();

		return retVal;
	}

	/**
	 * t invites t, u accepts if doComplete==true
	 * 
	 * @param inviter
	 * @param invitee
	 * @param messageInvite
	 * @param messageAccept
	 * @param doComplete
	 * @param verbose
	 * @return - the new connection
	 * @throws Exception
	 */
	public static ColleagueConnection createColleagueConnection(Transport inviter, Transport invitee, String messageInvite,
			String messageAccept, boolean doComplete, boolean verbose) throws Exception {
		return createColleagueConnection(null, null, inviter, invitee, messageInvite, messageAccept, HTTPResponseValidator.CREATED,
				HTTPResponseValidator.OK, doComplete, verbose);
	}

	public static ColleagueConnection createColleagueConnection400(Transport inviter, Transport invitee, String messageInvite,
			String messageAccept, boolean doComplete, boolean verbose) throws Exception {
		return createColleagueConnection(null, null, inviter, invitee, messageInvite, messageAccept, HTTPResponseValidator.BAD_REQUEST,
				HTTPResponseValidator.BAD_REQUEST, doComplete, verbose);
	}

	public static ColleagueConnection createColleagueConnection(Transport inviterImpersonator, Transport inviteeImpersonator,
			Transport inviter, Transport invitee, String messageInvite, String messageAccept, HTTPResponseValidator validatorCreate,
			HTTPResponseValidator validatorAccept, boolean doComplete, boolean verbose) throws Exception {
		ColleagueConnection retVal = null;

		ProfileService profilesServiceInvitee = ServiceDocUtil.getUserServiceDocument(invitee);
		String url = profilesServiceInvitee.getLinkHref(ApiConstants.SocialNetworking.REL_COLLEAGUE);

		// add colleague connection
		Entry colleagueRequestEntry = ColleagueConnection.getRequestEntry(messageInvite, STATUS.pending);
		if (verbose) prettyPrint(colleagueRequestEntry);
		Entry response;
		if (null == inviterImpersonator)
			response = inviter.doAtomPost(Entry.class, url, colleagueRequestEntry, NO_HEADERS, validatorCreate);
		else
			response = inviterImpersonator.doAtomPost(Entry.class, url, colleagueRequestEntry, NO_HEADERS, validatorCreate);

		if (verbose && (validatorCreate != HTTPResponseValidator.BAD_REQUEST)) prettyPrint(response);

		if (!validatorCreate.isErrorExpected()) {
			retVal = new ColleagueConnection(response).validate();

			if (doComplete) {
				colleagueRequestEntry = ColleagueConnection.getRequestEntry(messageAccept, STATUS.accepted);
				// there is no response body on this call
				if (null == inviteeImpersonator)
					invitee.doAtomPut(null, retVal.getEditLink(), colleagueRequestEntry, NO_HEADERS, validatorAccept);
				else
					inviteeImpersonator.doAtomPut(null, retVal.getEditLink(), colleagueRequestEntry, NO_HEADERS, validatorAccept);

				response = invitee.doAtomGet(Entry.class, retVal.getEditLink(), HTTPResponseValidator.OK, verbose);

				retVal = new ColleagueConnection(response).validate();
			}
		}
		return retVal;
	}

	public static ColleagueConnection adminImpersonateColleagueConnection(Transport inviterImpersonator, Transport inviteeImpersonator,
			Transport inviter, Transport invitee, HTTPResponseValidator validatorCreate, HTTPResponseValidator validatorAccept,
			ACTION action, boolean verbose) throws Exception {
		ColleagueConnection retVal = null;

		ProfileService profilesServiceInvitee = ServiceDocUtil.getUserServiceDocument(invitee);
		ProfileService profilesServiceInviter = ServiceDocUtil.getUserServiceDocument(inviter);

		String url = urlBuilder.getProfilesAdminConnectionsUrl(null, action.name(), profilesServiceInviter.getUserId(),
				profilesServiceInvitee.getUserId());

		// set up request body -- unused in v4.0
		// Entry colleagueRequestEntry = ColleagueConnection.getRequestEntry(messageInvite, STATUS.accepted);
		// if (verbose) prettyPrint(colleagueRequestEntry);

		// add colleague connection
		Entry rawEntry = inviterImpersonator.doAtomPut(Entry.class, url, AbstractTest.ABDERA.newEntry(), NO_HEADERS, validatorCreate);

		if (verbose) prettyPrint(rawEntry);

		if (!validatorCreate.isErrorExpected()) {
			retVal = new ColleagueConnection(rawEntry).validate();

			// VERIFY: "source" user is "Invitee"
			Assert.assertEquals(profilesServiceInvitee.getUserId(), retVal.getSource().getUserId());
			// VERIFY: "target" user is "Inviter"
			Assert.assertEquals(profilesServiceInviter.getUserId(), retVal.getTarget().getUserId());
		}

		return retVal;
	}

	public void getAllConnections(Transport source, Transport target, boolean verbose) throws Exception {
		// service document
		ProfileService profilesServiceT = ServiceDocUtil.getUserServiceDocument(target);
		// link to colleague feed
		String colleagueLinkT = profilesServiceT.getLinkHref(ApiConstants.SocialNetworking.REL_COLLEAGUE);

		ArrayList<ColleagueConnection> retVal = new ArrayList<ColleagueConnection>();

		for (ColleagueConnection.STATUS s : ColleagueConnection.STATUS_ALL) {
			source.doAtomGet(Feed.class,
					URLBuilder.addQueryParameter(colleagueLinkT, "status", s.name(), false), HTTPResponseValidator.BAD_REQUEST, verbose);
			//ColleagueFeed colleagueFeedT = new ColleagueFeed(f).validate();
			//retVal.addAll(colleagueFeedT.getEntries());
		}
		//return retVal;
	}
	public List<ColleagueConnection> adminGetAllConnections(Transport t, boolean verbose) throws Exception {
		// service document
		ProfileService profilesServiceT = ServiceDocUtil.getUserServiceDocument(t);
		// link to colleague feed
		String colleagueLinkT = profilesServiceT.getLinkHref(ApiConstants.SocialNetworking.REL_COLLEAGUE);
		String adminColleagueLinkT = URLBuilder.addAdmin(colleagueLinkT);

		ArrayList<ColleagueConnection> retVal = new ArrayList<ColleagueConnection>();

		for (ColleagueConnection.STATUS s : ColleagueConnection.STATUS_ALL) {
			ColleagueFeed colleagueFeedT = new ColleagueFeed(adminTransport.doAtomGet(Feed.class,
					URLBuilder.addQueryParameter(adminColleagueLinkT, "status", s.name(), false), HTTPResponseValidator.OK, verbose))
					.validate();
			retVal.addAll(colleagueFeedT.getEntries());
		}
		return retVal;
	}

	public List<ColleagueConnection> adminGetAllConnectionsByStatus(Transport t, ConnectionEntry.STATUS status, boolean verbose)
			throws Exception {
		// service document
		ProfileService profilesServiceT = ServiceDocUtil.getUserServiceDocument(t);
		// link to colleague feed
		String colleagueLinkT = profilesServiceT.getLinkHref(ApiConstants.SocialNetworking.REL_COLLEAGUE);
		String adminColleagueLinkT = URLBuilder.addAdmin(colleagueLinkT);

		ArrayList<ColleagueConnection> retVal = new ArrayList<ColleagueConnection>();

		ColleagueFeed colleagueFeedT = new ColleagueFeed(adminTransport.doAtomGet(Feed.class,
				URLBuilder.addQueryParameter(adminColleagueLinkT, "status", status.name(), false), HTTPResponseValidator.OK, verbose))
				.validate();
		retVal.addAll(colleagueFeedT.getEntries());

		return retVal;
	}
}
