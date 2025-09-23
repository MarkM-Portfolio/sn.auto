/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2012                                          */
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
import com.ibm.lconn.automation.framework.services.profiles.model.BoardEntry;
import com.ibm.lconn.automation.framework.services.profiles.model.BoardFeed;
import com.ibm.lconn.automation.framework.services.profiles.model.CommentEntry;
import com.ibm.lconn.automation.framework.services.profiles.model.CommentFeed;
import com.ibm.lconn.automation.framework.services.profiles.model.ProfileService;
import com.ibm.lconn.automation.framework.services.profiles.model.StatusEntry;
import com.ibm.lconn.automation.framework.services.profiles.util.ApiConstants;
import com.ibm.lconn.automation.framework.services.profiles.util.GetAllStatusMessagesParameters;
import com.ibm.lconn.automation.framework.services.profiles.util.HTTPResponseValidator;
import com.ibm.lconn.automation.framework.services.profiles.util.URLBuilder;

public class StatusMessagesApiTest extends AbstractTest {

	@Test
	public void testGetAllStatusMessagesUsingPageSize() throws Exception {

		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.ON_PREMISE) {
			// get the authenticated users profile service document
			ProfileService profilesService = ProfileService.parseFrom(user1Transport.doAtomGet(Service.class,
					urlBuilder.getProfilesServiceDocument(), NO_HEADERS, HTTPResponseValidator.OK));
			String boardLink = profilesService.getLinkHref(ApiConstants.SocialNetworking.REL_BOARD);

			Thread.sleep(sleepTime);
			// ADD Board Entry
			String testMessage1 = "testStatusMessage1_" + System.currentTimeMillis() + "_1";
			BoardEntry message1 = new BoardEntry();
			message1.setContent(testMessage1);
			Entry entry1 = message1.toEntry();
			user1Transport.doAtomPost(Entry.class, boardLink, entry1, NO_HEADERS, HTTPResponseValidator.CREATED);

			Thread.sleep(sleepTime);
			// ADD Board Entry
			String testMessage2 = "testStatusMessage2_" + System.currentTimeMillis() + "_1";
			BoardEntry message2 = new BoardEntry();
			message2.setContent(testMessage2);
			Entry entry2 = message2.toEntry();
			user1Transport.doAtomPost(Entry.class, boardLink, entry2, NO_HEADERS, HTTPResponseValidator.CREATED);

			Thread.sleep(sleepTime);
			// ADD Board Entry
			String testMessage3 = "testStatusMessage3_" + System.currentTimeMillis() + "_1";
			BoardEntry message3 = new BoardEntry();
			message3.setContent(testMessage3);
			Entry entry3 = message3.toEntry();
			user1Transport.doAtomPost(Entry.class, boardLink, entry3, NO_HEADERS, HTTPResponseValidator.CREATED);

			Thread.sleep(sleepTime);
			// ADD Board Entry
			String testMessage4 = "testStatusMessage4_" + System.currentTimeMillis() + "_1";
			BoardEntry message4 = new BoardEntry();
			message4.setContent(testMessage4);
			Entry entry4 = message4.toEntry();
			user1Transport.doAtomPost(Entry.class, boardLink, entry4, NO_HEADERS, HTTPResponseValidator.CREATED);

			Thread.sleep(sleepTime);
			// ADD Board Entry
			String testMessage5 = "testStatusMessage5_" + System.currentTimeMillis() + "_1";
			BoardEntry message5 = new BoardEntry();
			message5.setContent(testMessage5);
			Entry entry5 = message5.toEntry();
			user1Transport.doAtomPost(Entry.class, boardLink, entry5, NO_HEADERS, HTTPResponseValidator.CREATED);

			// GET Board Entry
			BoardFeed boardFeed = new BoardFeed(user1Transport.doAtomGet(Feed.class, boardLink, NO_HEADERS, HTTPResponseValidator.OK));
			BoardEntry boardMessage5 = boardFeed.getEntries().get(4);
			BoardEntry boardMessage4 = boardFeed.getEntries().get(3);
			BoardEntry boardMessage3 = boardFeed.getEntries().get(2);
			BoardEntry boardMessage2 = boardFeed.getEntries().get(1);
			BoardEntry boardMessage1 = boardFeed.getEntries().get(0);

			Thread.sleep(sleepTime);
			// ADD comment to Board Entry
			String commentsLink5 = boardMessage5.getLinkHref(ApiConstants.SocialNetworking.TERM_REPLIES);
			CommentFeed commentsFeed5 = new CommentFeed(user1Transport.doAtomGet(Feed.class, commentsLink5, NO_HEADERS,
					HTTPResponseValidator.OK));
			CommentEntry comment5 = new CommentEntry();
			comment5.setContent("testCommentStatusMessage5_" + System.currentTimeMillis() + "_1");
			Entry commentEntry5 = comment5.toEntry();
			user1Transport.doAtomPost(Entry.class, commentsLink5, commentEntry5, NO_HEADERS, HTTPResponseValidator.CREATED);

			Thread.sleep(sleepTime);
			// ADD comment to Board Entry
			String commentsLink1 = boardMessage1.getLinkHref(ApiConstants.SocialNetworking.TERM_REPLIES);
			CommentFeed commentsFeed1 = new CommentFeed(user1Transport.doAtomGet(Feed.class, commentsLink1, NO_HEADERS,
					HTTPResponseValidator.OK));
			CommentEntry comment1 = new CommentEntry();
			comment1.setContent("testCommentStatusMessage1_" + System.currentTimeMillis() + "_1");
			Entry commentEntry1 = comment1.toEntry();
			user1Transport.doAtomPost(Entry.class, commentsLink1, commentEntry1, NO_HEADERS, HTTPResponseValidator.CREATED);

			Thread.sleep(sleepTime);
			// ADD comment to Board Entry
			String commentsLink2 = boardMessage2.getLinkHref(ApiConstants.SocialNetworking.TERM_REPLIES);
			CommentFeed commentsFeed2 = new CommentFeed(user1Transport.doAtomGet(Feed.class, commentsLink2, NO_HEADERS,
					HTTPResponseValidator.OK));
			CommentEntry comment2 = new CommentEntry();
			comment2.setContent("testCommentStatusMessage2_" + System.currentTimeMillis() + "_1");
			Entry commentEntry2 = comment2.toEntry();
			user1Transport.doAtomPost(Entry.class, commentsLink2, commentEntry2, NO_HEADERS, HTTPResponseValidator.CREATED);

			Thread.sleep(sleepTime);
			// ADD comment to Board Entry
			String commentsLink4 = boardMessage4.getLinkHref(ApiConstants.SocialNetworking.TERM_REPLIES);
			CommentFeed commentsFeed4 = new CommentFeed(user1Transport.doAtomGet(Feed.class, commentsLink4, NO_HEADERS,
					HTTPResponseValidator.OK));
			CommentEntry comment4 = new CommentEntry();
			comment4.setContent("testCommentStatusMessage4_" + System.currentTimeMillis() + "_1");
			Entry commentEntry4 = comment4.toEntry();
			user1Transport.doAtomPost(Entry.class, commentsLink4, commentEntry4, NO_HEADERS, HTTPResponseValidator.CREATED);

			Thread.sleep(sleepTime);
			// ADD comment to Board Entry
			String commentsLink3 = boardMessage3.getLinkHref(ApiConstants.SocialNetworking.TERM_REPLIES);
			CommentFeed commentsFeed3 = new CommentFeed(user1Transport.doAtomGet(Feed.class, commentsLink3, NO_HEADERS,
					HTTPResponseValidator.OK));
			CommentEntry comment3 = new CommentEntry();
			comment3.setContent("testCommentStatusMessage3_" + System.currentTimeMillis() + "_1");
			Entry commentEntry3 = comment3.toEntry();
			user1Transport.doAtomPost(Entry.class, commentsLink3, commentEntry3, NO_HEADERS, HTTPResponseValidator.CREATED);

			StringBuilder url = new StringBuilder(urlBuilder.getAllStatusMessagesUrl());
			GetAllStatusMessagesParameters params = new GetAllStatusMessagesParameters();
			int pageSize = 5;
			params.setPageSize(pageSize);
			URLBuilder.addQueryParameters(url, params);

			// GET Board Entry Feed
			BoardFeed boardEntriesFeed = new BoardFeed(user1Transport.doAtomGet(Feed.class, url.toString(), NO_HEADERS,
					HTTPResponseValidator.OK));
			Assert.assertEquals(pageSize, boardEntriesFeed.getPageSize());
		}
	}

	@Test
	public void testGetAllStatusMessagesUsingSortOrderDescSortByPublished() throws Exception {

		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.ON_PREMISE) {
			StringBuilder url = new StringBuilder(urlBuilder.getAllStatusMessagesUrl());
			GetAllStatusMessagesParameters params = new GetAllStatusMessagesParameters();
			params.setSortOrder("desc");
			params.setSortBy("published");
			URLBuilder.addQueryParameters(url, params);

			// GET Board Entry Feed
			BoardFeed boardEntriesFeed = new BoardFeed(user1Transport.doAtomGet(Feed.class, url.toString(), NO_HEADERS,
					HTTPResponseValidator.OK));
			List<BoardEntry> entries = boardEntriesFeed.getEntries();
			if (entries.size() > 1) {
				for (int i = 0; i < entries.size() - 1; i++) {
					Assert.assertEquals(true, entries.get(i + 1).getCreated().before(entries.get(i).getCreated()));
				}
			}
		}
	}

	@Test
	public void testGetAllStatusMessagesUsingSortOrderAscSortByPublished() throws Exception {

		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.ON_PREMISE) {
			StringBuilder url = new StringBuilder(urlBuilder.getAllStatusMessagesUrl());
			GetAllStatusMessagesParameters params = new GetAllStatusMessagesParameters();
			params.setSortOrder("asc");
			params.setSortBy("published");
			URLBuilder.addQueryParameters(url, params);

			// GET Board Entry Feed
			BoardFeed boardEntriesFeed = new BoardFeed(user1Transport.doAtomGet(Feed.class, url.toString(), NO_HEADERS,
					HTTPResponseValidator.OK));
			List<BoardEntry> entries = boardEntriesFeed.getEntries();
			if (entries.size() > 1) {
				for (int i = 0; i < entries.size() - 1; i++) {
					Assert.assertEquals(true, entries.get(i + 1).getCreated().after(entries.get(i).getCreated()));
				}
			}
		}
	}

	@Test
	public void testGetAllStatusMessagesUsingSortOrderDescSortByLastMod() throws Exception {

		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.ON_PREMISE) {
			StringBuilder url = new StringBuilder(urlBuilder.getAllStatusMessagesUrl());
			GetAllStatusMessagesParameters params = new GetAllStatusMessagesParameters();
			params.setSortOrder("desc");
			params.setSortBy("lastMod");
			URLBuilder.addQueryParameters(url, params);

			// GET Board Entry Feed
			BoardFeed boardEntriesFeed = new BoardFeed(user1Transport.doAtomGet(Feed.class, url.toString(), NO_HEADERS,
					HTTPResponseValidator.OK));
			List<BoardEntry> entries = boardEntriesFeed.getEntries();
			if (entries.size() > 1) {
				for (int i = 0; i < entries.size() - 1; i++) {
					Assert.assertEquals(true, entries.get(i + 1).getUpdated().before(entries.get(i).getUpdated()));
				}
			}
		}
	}

	@Test
	public void testGetAllStatusMessagesUsingSortOrderAscSortByLastMod() throws Exception {

		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.ON_PREMISE) {
			StringBuilder url = new StringBuilder(urlBuilder.getAllStatusMessagesUrl());
			GetAllStatusMessagesParameters params = new GetAllStatusMessagesParameters();
			params.setSortOrder("asc");
			params.setSortBy("lastMod");
			URLBuilder.addQueryParameters(url, params);

			// GET Board Entry Feed
			BoardFeed boardEntriesFeed = new BoardFeed(user1Transport.doAtomGet(Feed.class, url.toString(), NO_HEADERS,
					HTTPResponseValidator.OK));
			List<BoardEntry> entries = boardEntriesFeed.getEntries();
			if (entries.size() > 1) {
				for (int i = 0; i < entries.size() - 1; i++) {
					Assert.assertEquals(true, entries.get(i + 1).getUpdated().after(entries.get(i).getUpdated()));
				}
			}
		}
	}

	@Test
	public void testGetAllStatusMessagesUsingCommentsNone() throws Exception {

		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.ON_PREMISE) {
			StringBuilder url = new StringBuilder(urlBuilder.getAllStatusMessagesUrl());
			GetAllStatusMessagesParameters params = new GetAllStatusMessagesParameters();
			params.setComments("none");
			URLBuilder.addQueryParameters(url, params);

			// GET Board Entry Feed
			Feed boardEntriesFeed = user1Transport.doAtomGet(Feed.class, url.toString(), NO_HEADERS, HTTPResponseValidator.OK);
			boolean hasComments = false;
			for (Entry boardEntry : boardEntriesFeed.getEntries()) {
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
	public void testGetAllStatusMessagesUsingCommentsAll() throws Exception {

		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.ON_PREMISE) {
			StringBuilder url = new StringBuilder(urlBuilder.getAllStatusMessagesUrl());
			GetAllStatusMessagesParameters params = new GetAllStatusMessagesParameters();
			params.setComments("all");
			URLBuilder.addQueryParameters(url, params);

			// GET Board Entry Feed
			Feed boardEntriesFeed = user1Transport.doAtomGet(Feed.class, url.toString(), NO_HEADERS, HTTPResponseValidator.OK);
			boolean hasComments = false;
			for (Entry boardEntry : boardEntriesFeed.getEntries()) {
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
	public void testGetAllStatusMessagesUsingCommentsInline() throws Exception {

		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.ON_PREMISE) {
			StringBuilder url = new StringBuilder(urlBuilder.getAllStatusMessagesUrl());
			GetAllStatusMessagesParameters params = new GetAllStatusMessagesParameters();
			params.setComments("inline");
			URLBuilder.addQueryParameters(url, params);

			// GET Board Entry Feed
			BoardFeed boardEntriesFeed = new BoardFeed(user1Transport.doAtomGet(Feed.class, url.toString(), NO_HEADERS,
					HTTPResponseValidator.OK));
			boolean hasComments = false;
			for (BoardEntry be : boardEntriesFeed.getEntries()) {
				if (be.getComments() != null && be.getComments().size() > 0) {
					hasComments = true;
				}
			}
			Assert.assertEquals(true, hasComments);
		}
	}

	@Test
	public void testGetAllStatusMessagesUsingSinceAndSinceEntryIdDesc() throws Exception {

		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.ON_PREMISE) {
			ProfileService profilesService = ProfileService.parseFrom(user1Transport.doAtomGet(Service.class,
					urlBuilder.getProfilesServiceDocument(), NO_HEADERS, HTTPResponseValidator.OK));

			String profileStatusLink = profilesService.getLinkHref(ApiConstants.SocialNetworking.REL_STATUS);
			String testStatusMessage = "testStatusEntryMessage1_" + System.currentTimeMillis() + "_1";

			Thread.sleep(sleepTime);
			// ADD new status message
			StatusEntry statusEntry = new StatusEntry();
			statusEntry.setContent(testStatusMessage);
			Entry entry = statusEntry.toEntry();
			user1Transport.doAtomPut(null, profileStatusLink, entry, NO_HEADERS, HTTPResponseValidator.OK);

			StringBuilder url = new StringBuilder(urlBuilder.getAllStatusMessagesUrl());
			GetAllStatusMessagesParameters params = new GetAllStatusMessagesParameters();
			params.setSortBy("published");
			params.setSortOrder("desc");
			URLBuilder.addQueryParameters(url, params);

			BoardFeed boardFeed = new BoardFeed(user1Transport.doAtomGet(Feed.class, url.toString(), NO_HEADERS, HTTPResponseValidator.OK));
			BoardEntry boardMessage3 = boardFeed.getEntries().get(2);

			SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd,H:mm:ss.S");
			String tempDate = formater.format(boardMessage3.getCreated());

			String offset = "" + boardMessage3.getCreated().getTimezoneOffset();
			String since = "" + tempDate.split(",")[0] + "T" + tempDate.split(",")[1] + offset;
			String sinceEntryId = boardMessage3.getAtomId().split("entry:")[1];

			StringBuilder url2 = new StringBuilder(urlBuilder.getAllStatusMessagesUrl());
			GetAllStatusMessagesParameters params2 = new GetAllStatusMessagesParameters();
			params2.setSince(since);
			params2.setSinceEntryId(sinceEntryId);
			params2.setSortBy("published");
			params2.setSortOrder("desc");
			URLBuilder.addQueryParameters(url2, params2);

			// GET Board Entry Feed
			BoardFeed boardEntriesFeed = new BoardFeed(user1Transport.doAtomGet(Feed.class, url2.toString(), NO_HEADERS,
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
	public void testGetAllStatusMessagesUsingSinceAndSinceEntryIdAsc() throws Exception {

		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.ON_PREMISE) {
			ProfileService profilesService = ProfileService.parseFrom(user1Transport.doAtomGet(Service.class,
					urlBuilder.getProfilesServiceDocument(), NO_HEADERS, HTTPResponseValidator.OK));

			String profileStatusLink = profilesService.getLinkHref(ApiConstants.SocialNetworking.REL_STATUS);
			String testStatusMessage = "testStatusEntryMessage1_" + System.currentTimeMillis() + "_1";

			Thread.sleep(sleepTime);
			// ADD new status message
			StatusEntry statusEntry = new StatusEntry();
			statusEntry.setContent(testStatusMessage);
			Entry entry = statusEntry.toEntry();
			user1Transport.doAtomPut(null, profileStatusLink, entry, NO_HEADERS, HTTPResponseValidator.OK);

			StringBuilder url = new StringBuilder(urlBuilder.getAllStatusMessagesUrl());
			GetAllStatusMessagesParameters params = new GetAllStatusMessagesParameters();
			params.setSortBy("published");
			params.setSortOrder("desc");
			URLBuilder.addQueryParameters(url, params);

			BoardFeed boardFeed = new BoardFeed(user1Transport.doAtomGet(Feed.class, url.toString(), NO_HEADERS, HTTPResponseValidator.OK));
			BoardEntry boardMessage3 = boardFeed.getEntries().get(2);

			SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd,HH:mm:ss.S");
			String tempDate = formater.format(boardMessage3.getCreated());
			String offset = "" + boardMessage3.getCreated().getTimezoneOffset();
			String since = "" + tempDate.split(",")[0] + "T" + tempDate.split(",")[1] + offset;
			String sinceEntryId = boardMessage3.getAtomId().split("entry:")[1];

			StringBuilder url2 = new StringBuilder(urlBuilder.getAllStatusMessagesUrl());
			GetAllStatusMessagesParameters params2 = new GetAllStatusMessagesParameters();
			params2.setSince(since);
			params2.setSinceEntryId(sinceEntryId);
			params2.setSortBy("published");
			params2.setSortOrder("asc");
			URLBuilder.addQueryParameters(url2, params2);

			// GET Board Entry Feed
			BoardFeed boardEntriesFeed = new BoardFeed(user1Transport.doAtomGet(Feed.class, url2.toString(), NO_HEADERS,
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

}
