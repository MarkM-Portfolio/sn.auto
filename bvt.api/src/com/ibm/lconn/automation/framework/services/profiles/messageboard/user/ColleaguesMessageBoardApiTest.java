/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2012, 2013                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.lconn.automation.framework.services.profiles.messageboard.user;

import java.text.SimpleDateFormat;
import java.util.List;
import junit.framework.Assert;
import org.apache.abdera.model.Category;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Service;
import org.testng.annotations.Test;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.DeploymentType;
import com.ibm.lconn.automation.framework.services.profiles.base.AbstractTest;
import com.ibm.lconn.automation.framework.services.profiles.connection.user.ColleagueTest;
import com.ibm.lconn.automation.framework.services.profiles.model.BoardEntry;
import com.ibm.lconn.automation.framework.services.profiles.model.BoardFeed;
import com.ibm.lconn.automation.framework.services.profiles.model.ColleagueConnection;
import com.ibm.lconn.automation.framework.services.profiles.model.ProfileEntry;
import com.ibm.lconn.automation.framework.services.profiles.model.ConnectionEntry.STATUS;
import com.ibm.lconn.automation.framework.services.profiles.model.CommentEntry;
import com.ibm.lconn.automation.framework.services.profiles.model.CommentFeed;
import com.ibm.lconn.automation.framework.services.profiles.model.ProfileService;
import com.ibm.lconn.automation.framework.services.profiles.util.ApiConstants;
import com.ibm.lconn.automation.framework.services.profiles.util.GetColleaguesEntriesParameters;
import com.ibm.lconn.automation.framework.services.profiles.util.HTTPResponseValidator;
import com.ibm.lconn.automation.framework.services.profiles.util.ServiceDocUtil;
import com.ibm.lconn.automation.framework.services.profiles.util.URLBuilder;
import com.ibm.lconn.automation.framework.services.profiles.util.VerifyColleaguesParameters;

public class ColleaguesMessageBoardApiTest extends AbstractTest {

	//@Test
	public void testGetMessagesOfColleagues() throws Exception {
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.ON_PREMISE) {
			setUpColleagueConnection();

			// ProfileService profilesService = ProfileService.parseFrom(user1Transport.doAtomGet(Service.class,
			// urlBuilder.getProfilesServiceDocument(TestProperties.getInstance().getOtherEmail(),true), NO_HEADERS,
			// HTTPResponseValidator.OK));
			ProfileService profilesService = ServiceDocUtil.getUserServiceDocument(user2Transport);
			String boardLink = profilesService.getLinkHref(ApiConstants.SocialNetworking.REL_BOARD);
			String testMessage = "testOthersBoardMessage" + System.currentTimeMillis() + "_1";

			Thread.sleep(sleepTime);
			// ADD Board Entry
			BoardEntry message = new BoardEntry();
			message.setContent(testMessage);
			Entry entry = message.toEntry();
			user1Transport.doAtomPost(Entry.class, boardLink, entry, NO_HEADERS, HTTPResponseValidator.CREATED);

			// profilesService = ProfileService.parseFrom(user1Transport.doAtomGet(Service.class,
			// urlBuilder.getProfilesServiceDocument(TestProperties.getInstance().getOtherEmail(),true), NO_HEADERS,
			// HTTPResponseValidator.OK));
			boardLink = profilesService.getLinkHref(ApiConstants.SocialNetworking.REL_BOARD);
			BoardFeed boardFeed = new BoardFeed(user1Transport.doAtomGet(Feed.class, boardLink, NO_HEADERS, HTTPResponseValidator.OK));
			BoardEntry boardMessage = boardFeed.getEntries().get(0);

			Thread.sleep(sleepTime);
			String commentsLink1 = boardMessage.getLinkHref(ApiConstants.SocialNetworking.TERM_REPLIES);
			CommentFeed commentsFeed1 = new CommentFeed(user1Transport.doAtomGet(Feed.class, commentsLink1, NO_HEADERS,
					HTTPResponseValidator.OK));
			CommentEntry comment1 = new CommentEntry();
			comment1.setContent("testCommentMessage1_" + System.currentTimeMillis() + "_1");
			Entry commentEntry1 = comment1.toEntry();
			user1Transport.doAtomPost(Entry.class, commentsLink1, commentEntry1, NO_HEADERS, HTTPResponseValidator.CREATED);

			StringBuilder url = new StringBuilder(urlBuilder.getColleagueEntriesUrl());
			GetColleaguesEntriesParameters params = new GetColleaguesEntriesParameters();
			String userEmail = ServiceDocUtil.getUserProfileEntry(user2Transport).getEmail();
			params.setEmail(userEmail);
			URLBuilder.addQueryParameters(url, params);

			// GET Comment Entry Feed
			Feed entryFeed = user1Transport.doAtomGet(Feed.class, url.toString(), NO_HEADERS, HTTPResponseValidator.OK);
			for (Entry ent : entryFeed.getEntries()) {
				String colleagueEmail = ent.getAuthor().getEmail();
				if (!userEmail.equals(colleagueEmail)) {
					VerifyColleaguesParameters parameters = new VerifyColleaguesParameters();
					parameters.setSourceEmail(userEmail);
					parameters.setTargetEmail(colleagueEmail);
					StringBuilder url2 = new StringBuilder(urlBuilder.getVerifyColleaguesUrl());
					URLBuilder.addQueryParameters(url2, parameters);
					Entry e = user1Transport.doAtomGet(Entry.class, url2.toString(), NO_HEADERS, HTTPResponseValidator.OK);
					boolean connected = false;
					for (Category category : e.getCategories()) {
						if (category.getTerm().equals("accepted")) {
							connected = true;
							break;
						}
					}
					Assert.assertEquals(true, connected);
				}
			}

			tearDownColleagueConnection();
		}
	}

	@Test
	public void testGetMessagesOfColleaguesUsingPageSize() throws Exception {
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.ON_PREMISE) {
			StringBuilder url = new StringBuilder(urlBuilder.getColleagueEntriesUrl());
			GetColleaguesEntriesParameters params = new GetColleaguesEntriesParameters();
			String userEmail = ServiceDocUtil.getUserProfileEntry(user2Transport).getEmail();
			int pageSize = 2;
			params.setEmail(userEmail);
			params.setPageSize(pageSize);
			URLBuilder.addQueryParameters(url, params);

			BoardFeed entryFeed = new BoardFeed(user1Transport.doAtomGet(Feed.class, url.toString(), NO_HEADERS, HTTPResponseValidator.OK));
			Assert.assertEquals(entryFeed.getPageSize(), pageSize);
		}
	}

	@Test
	public void testGetMessagesOfColleaguesUsingCommentsAll() throws Exception {

		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.ON_PREMISE) {
			StringBuilder url = new StringBuilder(urlBuilder.getColleagueEntriesUrl());
			GetColleaguesEntriesParameters params = new GetColleaguesEntriesParameters();
			String userEmail = ServiceDocUtil.getUserProfileEntry(user2Transport).getEmail();
			params.setEmail(userEmail);
			params.setComments("all");
			URLBuilder.addQueryParameters(url, params);

			Feed entryFeed = user1Transport.doAtomGet(Feed.class, url.toString(), NO_HEADERS, HTTPResponseValidator.OK);
			boolean hasComments = false;
			for (Entry boardEntry : entryFeed.getEntries()) {
				for (Category c : boardEntry.getCategories()) {
					String term = c.getTerm();
					if (term.equals("comment")) {
						hasComments = true;
						break;
					}
				}
			}
			Assert.assertEquals(true, hasComments);
		}
	}

	@Test
	public void testGetMessagesOfColleaguesUsingCommentsNone() throws Exception {

		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.ON_PREMISE) {
			StringBuilder url = new StringBuilder(urlBuilder.getColleagueEntriesUrl());
			GetColleaguesEntriesParameters params = new GetColleaguesEntriesParameters();
			String userEmail = ServiceDocUtil.getUserProfileEntry(user2Transport).getEmail();
			params.setEmail(userEmail);
			params.setComments("none");
			URLBuilder.addQueryParameters(url, params);

			Feed entryFeed = user1Transport.doAtomGet(Feed.class, url.toString(), NO_HEADERS, HTTPResponseValidator.OK);
			boolean hasComments = false;
			for (Entry boardEntry : entryFeed.getEntries()) {
				for (Category c : boardEntry.getCategories()) {
					String term = c.getTerm();
					if (term.equals("comment")) {
						hasComments = true;
						break;
					}
				}
			}
			Assert.assertEquals(false, hasComments);
		}
	}

	@Test
	public void testGetMessagesOfColleaguesUsingMessageTypeStatus() throws Exception {
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.ON_PREMISE) {
			StringBuilder url = new StringBuilder(urlBuilder.getColleagueEntriesUrl());
			GetColleaguesEntriesParameters params = new GetColleaguesEntriesParameters();
			String userEmail = ServiceDocUtil.getUserProfileEntry(user2Transport).getEmail();
			params.setEmail(userEmail);
			params.setMessageType("status");
			URLBuilder.addQueryParameters(url, params);

			Feed entryFeed = user1Transport.doAtomGet(Feed.class, url.toString(), NO_HEADERS, HTTPResponseValidator.OK);
			boolean onlyStatusEntries = true;
			for (Entry boardEntry : entryFeed.getEntries()) {
				for (Category c : boardEntry.getCategories()) {
					String term = c.getTerm();
					String sch = c.getScheme().toString();
					if (term.equals("simpleEntry") && sch.equals(ApiConstants.SocialNetworking.SCHEME_MESSAGE_TYPE)) {
						onlyStatusEntries = false;
						break;
					}
				}
			}
			Assert.assertEquals(true, onlyStatusEntries);
		}
	}

	@Test
	public void testGetMessagesOfColleaguesUsingMessageTypeSimpleEntry() throws Exception {
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.ON_PREMISE) {
			StringBuilder url = new StringBuilder(urlBuilder.getColleagueEntriesUrl());
			GetColleaguesEntriesParameters params = new GetColleaguesEntriesParameters();
			String userEmail = ServiceDocUtil.getUserProfileEntry(user2Transport).getEmail();
			params.setEmail(userEmail);
			params.setMessageType("simpleEntry");
			URLBuilder.addQueryParameters(url, params);

			Feed entryFeed = user1Transport.doAtomGet(Feed.class, url.toString(), NO_HEADERS, HTTPResponseValidator.OK);
			boolean onlySimpleEntries = true;
			for (Entry boardEntry : entryFeed.getEntries()) {
				for (Category c : boardEntry.getCategories()) {
					String term = c.getTerm();
					String sch = c.getScheme().toString();
					if (term.equals("status") && sch.equals(ApiConstants.SocialNetworking.SCHEME_MESSAGE_TYPE)) {
						onlySimpleEntries = false;
						break;
					}
				}
			}
			Assert.assertEquals(true, onlySimpleEntries);
		}
	}

	@Test
	public void testGetMessagesOfColleaguesUsingSortOrderDescSortByPublished() throws Exception {
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.ON_PREMISE) {
			// get other users profile service document
			StringBuilder url = new StringBuilder(urlBuilder.getColleagueEntriesUrl());
			GetColleaguesEntriesParameters params = new GetColleaguesEntriesParameters();
			String userEmail = ServiceDocUtil.getUserProfileEntry(user2Transport).getEmail();
			params.setEmail(userEmail);
			params.setSortBy("published");
			params.setSortOrder("desc");
			URLBuilder.addQueryParameters(url, params);

			BoardFeed entryFeed = new BoardFeed(user1Transport.doAtomGet(Feed.class, url.toString(), NO_HEADERS, HTTPResponseValidator.OK));
			List<BoardEntry> entries = entryFeed.getEntries();
			if (entries.size() > 1) {
				for (int i = 0; i < entries.size() - 1; i++) {
					Assert.assertEquals(true, entries.get(i + 1).getCreated().before(entries.get(i).getCreated()));
				}
			}
		}
	}

	@Test
	public void testGetMessagesOfColleaguesUsingSortOrderAscSortByPublished() throws Exception {
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.ON_PREMISE) {
			// get other users profile service document
			StringBuilder url = new StringBuilder(urlBuilder.getColleagueEntriesUrl());
			GetColleaguesEntriesParameters params = new GetColleaguesEntriesParameters();
			String userEmail = ServiceDocUtil.getUserProfileEntry(user2Transport).getEmail();
			params.setEmail(userEmail);
			params.setSortBy("published");
			params.setSortOrder("asc");
			URLBuilder.addQueryParameters(url, params);

			BoardFeed entryFeed = new BoardFeed(user1Transport.doAtomGet(Feed.class, url.toString(), NO_HEADERS, HTTPResponseValidator.OK));
			List<BoardEntry> entries = entryFeed.getEntries();
			if (entries.size() > 1) {
				for (int i = 0; i < entries.size() - 1; i++) {
					Assert.assertEquals(true, entries.get(i + 1).getCreated().after(entries.get(i).getCreated()));
				}
			}
		}
	}

	@Test
	public void testGetMessagesOfColleaguesUsingSortOrderDescSortByLastMod() throws Exception {
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.ON_PREMISE) {
			// get other users profile service document
			StringBuilder url = new StringBuilder(urlBuilder.getColleagueEntriesUrl());
			GetColleaguesEntriesParameters params = new GetColleaguesEntriesParameters();
			String userEmail = ServiceDocUtil.getUserProfileEntry(user2Transport).getEmail();
			params.setEmail(userEmail);
			params.setSortBy("lastMod");
			params.setSortOrder("desc");
			URLBuilder.addQueryParameters(url, params);

			BoardFeed entryFeed = new BoardFeed(user1Transport.doAtomGet(Feed.class, url.toString(), NO_HEADERS, HTTPResponseValidator.OK));
			List<BoardEntry> entries = entryFeed.getEntries();
			if (entries.size() > 1) {
				for (int i = 0; i < entries.size() - 1; i++) {
					Assert.assertEquals(true, entries.get(i + 1).getUpdated().before(entries.get(i).getUpdated()));
				}
			}
		}
	}

	@Test
	public void testGetMessagesOfColleaguesUsingSortOrderAscSortByLastMod() throws Exception {
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.ON_PREMISE) {
			// get other users profile service document
			StringBuilder url = new StringBuilder(urlBuilder.getColleagueEntriesUrl());
			GetColleaguesEntriesParameters params = new GetColleaguesEntriesParameters();
			String userEmail = ServiceDocUtil.getUserProfileEntry(user2Transport).getEmail();
			params.setEmail(userEmail);
			params.setSortBy("lastMod");
			params.setSortOrder("asc");
			URLBuilder.addQueryParameters(url, params);

			BoardFeed entryFeed = new BoardFeed(user1Transport.doAtomGet(Feed.class, url.toString(), NO_HEADERS, HTTPResponseValidator.OK));
			List<BoardEntry> entries = entryFeed.getEntries();
			if (entries.size() > 1) {
				for (int i = 0; i < entries.size() - 1; i++) {
					Assert.assertEquals(true, entries.get(i + 1).getUpdated().after(entries.get(i).getUpdated()));
				}
			}
		}
	}

	@Test
	public void testGetMessagesOfColleaguesUsingSinceAndSinceEntryIdDesc() throws Exception {
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.ON_PREMISE) {
			StringBuilder url = new StringBuilder(urlBuilder.getColleagueEntriesUrl());
			GetColleaguesEntriesParameters params = new GetColleaguesEntriesParameters();
			String userEmail = ServiceDocUtil.getUserProfileEntry(user2Transport).getEmail();
			params.setEmail(userEmail);
			params.setPageSize(10);
			URLBuilder.addQueryParameters(url, params);

			BoardFeed entryFeed = new BoardFeed(user1Transport.doAtomGet(Feed.class, url.toString(), NO_HEADERS, HTTPResponseValidator.OK));
			BoardEntry boardMessage1 = entryFeed.getEntries().get(0);
			//BoardEntry boardMessage2 = entryFeed.getEntries().get(1);
			//BoardEntry boardMessage3 = entryFeed.getEntries().get(3);

			SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd,HH:mm:ss.S");
			String tempDate = formater.format(boardMessage1.getCreated());
			String offset = "" + boardMessage1.getCreated().getTimezoneOffset();
			String since = "" + tempDate.split(",")[0] + "T" + tempDate.split(",")[1] + offset;

			String sinceEntryId = boardMessage1.getAtomId().split("entry:")[1];

			url = new StringBuilder(urlBuilder.getColleagueEntriesUrl());
			params.setSince(since);
			params.setSinceEntryId(sinceEntryId);
			params.setSortBy("published");
			params.setSortOrder("desc");
			params.setPageSize(10);
			URLBuilder.addQueryParameters(url, params);

			// GET Board Entry Feed
			BoardFeed boardEntriesFeed = new BoardFeed(user1Transport.doAtomGet(Feed.class, url.toString(), NO_HEADERS,
					HTTPResponseValidator.OK));
			if (boardEntriesFeed.getEntries().size() > 1) {
				for (int i = 0; i < boardEntriesFeed.getEntries().size() - 1; i++) {
					Assert.assertEquals(true,
							(boardEntriesFeed.getEntries().get(i + 1).getCreated()
									.before(boardEntriesFeed.getEntries().get(i).getCreated()) || boardEntriesFeed.getEntries().get(i + 1)
									.getCreated().equals(boardEntriesFeed.getEntries().get(i).getCreated())));
				}
			}
		}
	}

	@Test
	public void testGetMessagesOfColleaguesUsingSinceAndSinceEntryIdAsc() throws Exception {
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.ON_PREMISE) {
			StringBuilder url = new StringBuilder(urlBuilder.getColleagueEntriesUrl());
			GetColleaguesEntriesParameters params = new GetColleaguesEntriesParameters();
			String userEmail = ServiceDocUtil.getUserProfileEntry(user2Transport).getEmail();
			params.setEmail(userEmail);
			params.setPageSize(10);
			URLBuilder.addQueryParameters(url, params);

			BoardFeed entryFeed = new BoardFeed(user1Transport.doAtomGet(Feed.class, url.toString(), NO_HEADERS, HTTPResponseValidator.OK));
			BoardEntry boardMessage1 = entryFeed.getEntries().get(0);
			//BoardEntry boardMessage2 = entryFeed.getEntries().get(1);
			//BoardEntry boardMessage3 = entryFeed.getEntries().get(3);

			SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd,HH:mm:ss.S");
			String tempDate = formater.format(boardMessage1.getCreated());
			String offset = "" + boardMessage1.getCreated().getTimezoneOffset();
			String since = "" + tempDate.split(",")[0] + "T" + tempDate.split(",")[1] + offset;
			String sinceEntryId = boardMessage1.getAtomId().split("entry:")[1];

			url = new StringBuilder(urlBuilder.getColleagueEntriesUrl());
			params.setSince(since);
			params.setSinceEntryId(sinceEntryId);
			params.setSortBy("published");
			params.setSortOrder("asc");
			params.setPageSize(10);
			URLBuilder.addQueryParameters(url, params);

			// GET Board Entry Feed
			BoardFeed boardEntriesFeed = new BoardFeed(user1Transport.doAtomGet(Feed.class, url.toString(), NO_HEADERS,
					HTTPResponseValidator.OK));
			if (boardEntriesFeed.getEntries().size() > 1) {
				for (int i = 0; i < boardEntriesFeed.getEntries().size() - 1; i++) {
					Assert.assertEquals(
							true,
							(boardEntriesFeed.getEntries().get(i + 1).getCreated().after(boardEntriesFeed.getEntries().get(i).getCreated()) || boardEntriesFeed
									.getEntries().get(i + 1).getCreated().equals(boardEntriesFeed.getEntries().get(i).getCreated())));
				}
			}
		}
	}

	ColleagueConnection existingConnectionUser1;
	ColleagueConnection existingConnectionUser2;

	// override setUp()/tearDown() later if needed. At this time only one test requires the connection, so we only do this cycle once for
	// that test
	private void setUpColleagueConnection() throws Exception {
		boolean doConnect = false;
		boolean doDelete = false;
		// save existing connection, if any, to restore later.
		existingConnectionUser1 = ColleagueTest.getColleagueConnection(user1Transport, user2Transport, ColleagueConnection.STATUS_ALL,
				false);
		existingConnectionUser2 = ColleagueTest.getColleagueConnection(user2Transport, user1Transport, ColleagueConnection.STATUS_ALL,
				false);

		if (null == existingConnectionUser1 && null == existingConnectionUser2) {
			// users are not connected, need to connect them
			doConnect = true;
		}
		else if (null != existingConnectionUser1 && null != existingConnectionUser2) {
			// users are connected, if the connection is not confirmed delete+create
			if (!STATUS.accepted.equals(existingConnectionUser1.getStatus())) {
				doDelete = true;
				doConnect = true;
			}
		}
		else {
			// unexpected state, clean up
			doDelete = true;
			// after delete, users are not connected, need to connect them
			doConnect = true;
			// set up for tearDown()
			existingConnectionUser1 = null;
			existingConnectionUser2 = null;
		}

		if (doDelete) {
			ColleagueTest.deleteColleagueConnection(user1Transport, user2Transport, ColleagueConnection.STATUS_ALL, false);
		}

		if (doConnect) {
			String msg = System.currentTimeMillis() + " " + this.getClass().getSimpleName();
			ColleagueTest.createColleagueConnection(user1Transport, user2Transport, msg + " invitation message", msg + " accept message",
					true, false);
		}
	}

	/**
	 * restore connection to pre-test state
	 * 
	 * @throws Exception
	 */
	private void tearDownColleagueConnection() throws Exception {
		// restore old connection, if there was one. This isn't a "real restore" of the original connection, it only sets up a connection
		// between the users similar to before the test.
		if (null != existingConnectionUser1 && null != existingConnectionUser2) {
			// first clean up test connection
			ColleagueTest.deleteColleagueConnection(user1Transport, user2Transport, ColleagueConnection.STATUS_ALL, false);

			if (STATUS.pending.equals(existingConnectionUser1.getStatus())) {
				ColleagueTest.createColleagueConnection(user2Transport, user1Transport, existingConnectionUser2.getContent(),
						existingConnectionUser1.getContent(), STATUS.accepted.equals(existingConnectionUser1.getStatus()), false);
			}
			else if (STATUS.unconfirmed.equals(existingConnectionUser1.getStatus())) {
				ColleagueTest.createColleagueConnection(user1Transport, user2Transport, existingConnectionUser1.getContent(),
						existingConnectionUser2.getContent(), STATUS.accepted.equals(existingConnectionUser1.getStatus()), true);
			}
			else {
				// TODO: if needed, determine invite/accept order for this case
				ColleagueTest.createColleagueConnection(user1Transport, user2Transport, existingConnectionUser1.getContent(),
						existingConnectionUser2.getContent(), STATUS.accepted.equals(existingConnectionUser1.getStatus()), false);
			}

			ColleagueConnection restoredConnectionMain = ColleagueTest.getColleagueConnection(user1Transport, user2Transport,
					ColleagueConnection.STATUS_ALL, false);
			ColleagueConnection restoredConnectionOther = ColleagueTest.getColleagueConnection(user2Transport, user1Transport,
					ColleagueConnection.STATUS_ALL, false);

			Assert.assertEquals(existingConnectionUser1.getStatus(), restoredConnectionMain.getStatus());
			Assert.assertEquals(existingConnectionUser2.getStatus(), restoredConnectionOther.getStatus());
		}
		else {
			// no valid prior connection, simply clean up test connection
			ColleagueTest.deleteColleagueConnection(user1Transport, user2Transport, ColleagueConnection.STATUS_ALL, false);
		}
	}
}
