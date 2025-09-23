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

import java.util.List;
import junit.framework.Assert;
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
import com.ibm.lconn.automation.framework.services.profiles.util.ApiConstants;
import com.ibm.lconn.automation.framework.services.profiles.util.GetEntryCommentsParameters;
import com.ibm.lconn.automation.framework.services.profiles.util.HTTPResponseValidator;
import com.ibm.lconn.automation.framework.services.profiles.util.URLBuilder;

public class MessageBoardCommentsApiTest extends AbstractTest {

	@Test
	public void testGetEnrtyCommentsUsingPageSize() throws Exception {

		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.ON_PREMISE) {
			// get the authenticated users profile service document
			ProfileService profilesService = ProfileService.parseFrom(user1Transport.doAtomGet(Service.class,
					urlBuilder.getProfilesServiceDocument(), NO_HEADERS, HTTPResponseValidator.OK));
			String boardLink = profilesService.getLinkHref(ApiConstants.SocialNetworking.REL_BOARD);
			String testMessage = "testStatusMessage" + System.currentTimeMillis();

			// ADD Board Entry
			BoardEntry message = new BoardEntry();
			message.setContent(testMessage);
			Entry entry = message.toEntry();
			user1Transport.doAtomPost(Entry.class, boardLink, entry, NO_HEADERS, HTTPResponseValidator.CREATED);

			// GET Board Entry
			profilesService = ProfileService.parseFrom(user1Transport.doAtomGet(Service.class, urlBuilder.getProfilesServiceDocument(),
					NO_HEADERS, HTTPResponseValidator.OK));
			boardLink = profilesService.getLinkHref(ApiConstants.SocialNetworking.REL_BOARD);
			BoardFeed boardFeed = new BoardFeed(user1Transport.doAtomGet(Feed.class, boardLink, NO_HEADERS, HTTPResponseValidator.OK));
			BoardEntry boardMessage = boardFeed.getEntries().get(0);
			String entryId = boardMessage.getAtomId().split("entry:")[1];

			String commentsLink = boardMessage.getLinkHref(ApiConstants.SocialNetworking.TERM_REPLIES);
			// ADD comment to Board Entry
			String testComment1 = "testComment1_" + System.currentTimeMillis();
			CommentEntry comment1 = new CommentEntry();
			comment1.setContent(testComment1);
			Entry commentEntry1 = comment1.toEntry();
			user1Transport.doAtomPost(Entry.class, commentsLink, commentEntry1, NO_HEADERS, HTTPResponseValidator.CREATED);

			Thread.sleep(sleepTime);
			// ADD comment to Board Entry
			String testComment2 = "testComment2_" + System.currentTimeMillis();
			CommentEntry comment2 = new CommentEntry();
			comment2.setContent(testComment2);
			Entry commentEntry2 = comment2.toEntry();
			user1Transport.doAtomPost(Entry.class, commentsLink, commentEntry2, NO_HEADERS, HTTPResponseValidator.CREATED);

			Thread.sleep(sleepTime);
			// ADD comment to Board Entry
			String testComment3 = "testComment3_" + System.currentTimeMillis();
			CommentEntry comment3 = new CommentEntry();
			comment3.setContent(testComment3);
			Entry commentEntry3 = comment3.toEntry();
			user1Transport.doAtomPost(Entry.class, commentsLink, commentEntry3, NO_HEADERS, HTTPResponseValidator.CREATED);

			Thread.sleep(sleepTime);
			// ADD comment to Board Entry
			String testComment4 = "testComment4_" + System.currentTimeMillis();
			CommentEntry comment4 = new CommentEntry();
			comment4.setContent(testComment4);
			Entry commentEntry4 = comment4.toEntry();
			user1Transport.doAtomPost(Entry.class, commentsLink, commentEntry4, NO_HEADERS, HTTPResponseValidator.CREATED);

			Thread.sleep(sleepTime);
			// ADD comment to Board Entry
			String testComment5 = "testComment5_" + System.currentTimeMillis();
			CommentEntry comment5 = new CommentEntry();
			comment5.setContent(testComment5);
			Entry commentEntry5 = comment5.toEntry();
			user1Transport.doAtomPost(Entry.class, commentsLink, commentEntry5, NO_HEADERS, HTTPResponseValidator.CREATED);

			StringBuilder url = new StringBuilder(urlBuilder.getEntryCommentsUrl());
			GetEntryCommentsParameters params = new GetEntryCommentsParameters();
			params.setEntryId(entryId);
			int pageSize = 2;
			params.setPageSize(pageSize);
			URLBuilder.addQueryParameters(url, params);

			// GET Board Entry Feed
			CommentFeed cFeed = new CommentFeed(user1Transport.doAtomGet(Feed.class, url.toString(), NO_HEADERS, HTTPResponseValidator.OK));
			Assert.assertEquals(pageSize, cFeed.getPageSize());

		}
	}

	@Test
	public void testGetEntryCommentsUsingPage() throws Exception {

		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.ON_PREMISE) {
			ProfileService profilesService = ProfileService.parseFrom(user1Transport.doAtomGet(Service.class,
					urlBuilder.getProfilesServiceDocument(), NO_HEADERS, HTTPResponseValidator.OK));
			String boardLink = profilesService.getLinkHref(ApiConstants.SocialNetworking.REL_BOARD);

			BoardFeed boardFeed = new BoardFeed(user1Transport.doAtomGet(Feed.class, boardLink, NO_HEADERS, HTTPResponseValidator.OK));
			BoardEntry boardMessage = boardFeed.getEntries().get(0);
			String entryId = boardMessage.getAtomId().split("entry:")[1];

			StringBuilder url = new StringBuilder(urlBuilder.getEntryCommentsUrl());
			GetEntryCommentsParameters params = new GetEntryCommentsParameters();
			params.setEntryId(entryId);
			int pageSize = 1;
			params.setPageSize(pageSize);
			params.setPage("2");
			URLBuilder.addQueryParameters(url, params);

			// GET Comment Entry Feed
			Feed cFeed = user1Transport.doAtomGet(Feed.class, url.toString(), NO_HEADERS, HTTPResponseValidator.OK);
			Assert.assertEquals(2, Integer.parseInt(cFeed.getExtension(ApiConstants.OpenSearch.QN_START_INDEX).getText()));
		}

	}

	@Test
	public void testGetEntryCommentsUsingSortOrderDescSortByPublished() throws Exception {

		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.ON_PREMISE) {
			ProfileService profilesService = ProfileService.parseFrom(user1Transport.doAtomGet(Service.class,
					urlBuilder.getProfilesServiceDocument(), NO_HEADERS, HTTPResponseValidator.OK));
			String boardLink = profilesService.getLinkHref(ApiConstants.SocialNetworking.REL_BOARD);

			BoardFeed boardFeed = new BoardFeed(user1Transport.doAtomGet(Feed.class, boardLink, NO_HEADERS, HTTPResponseValidator.OK));
			BoardEntry boardMessage = boardFeed.getEntries().get(0);
			String entryId = boardMessage.getAtomId().split("entry:")[1];

			StringBuilder url = new StringBuilder(urlBuilder.getEntryCommentsUrl());
			GetEntryCommentsParameters params = new GetEntryCommentsParameters();
			params.setEntryId(entryId);
			params.setSortOrder("desc");
			params.setSortBy("published");
			URLBuilder.addQueryParameters(url, params);

			// GET Board Entry Feed
			CommentFeed cFeed = new CommentFeed(user1Transport.doAtomGet(Feed.class, url.toString(), NO_HEADERS, HTTPResponseValidator.OK));
			List<CommentEntry> entries = cFeed.getEntries();
			if (entries.size() > 1) {
				for (int i = 0; i < entries.size() - 1; i++) {
					Assert.assertEquals(true, entries.get(i + 1).getCreated().before(entries.get(i).getCreated()));
				}
			}
		}
	}

	@Test
	public void testGetEntryCommentsUsingSortOrderAscSortByPublished() throws Exception {

		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.ON_PREMISE) {
			ProfileService profilesService = ProfileService.parseFrom(user1Transport.doAtomGet(Service.class,
					urlBuilder.getProfilesServiceDocument(), NO_HEADERS, HTTPResponseValidator.OK));
			String boardLink = profilesService.getLinkHref(ApiConstants.SocialNetworking.REL_BOARD);

			BoardFeed boardFeed = new BoardFeed(user1Transport.doAtomGet(Feed.class, boardLink, NO_HEADERS, HTTPResponseValidator.OK));
			BoardEntry boardMessage = boardFeed.getEntries().get(0);
			String entryId = boardMessage.getAtomId().split("entry:")[1];

			StringBuilder url = new StringBuilder(urlBuilder.getEntryCommentsUrl());
			GetEntryCommentsParameters params = new GetEntryCommentsParameters();
			params.setEntryId(entryId);
			params.setSortOrder("asc");
			params.setSortBy("published");
			URLBuilder.addQueryParameters(url, params);

			// GET Board Entry Feed
			CommentFeed cFeed = new CommentFeed(user1Transport.doAtomGet(Feed.class, url.toString(), NO_HEADERS, HTTPResponseValidator.OK));
			List<CommentEntry> entries = cFeed.getEntries();
			if (entries.size() > 1) {
				for (int i = 0; i < entries.size() - 1; i++) {
					Assert.assertEquals(true, entries.get(i + 1).getCreated().after(entries.get(i).getCreated()));
				}
			}
		}
	}

	@Test
	public void testGetEntryCommentsUsingSortOrderDescSortByLastMod() throws Exception {

		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.ON_PREMISE) {
			ProfileService profilesService = ProfileService.parseFrom(user1Transport.doAtomGet(Service.class,
					urlBuilder.getProfilesServiceDocument(), NO_HEADERS, HTTPResponseValidator.OK));
			String boardLink = profilesService.getLinkHref(ApiConstants.SocialNetworking.REL_BOARD);

			BoardFeed boardFeed = new BoardFeed(user1Transport.doAtomGet(Feed.class, boardLink, NO_HEADERS, HTTPResponseValidator.OK));
			BoardEntry boardMessage = boardFeed.getEntries().get(0);
			String entryId = boardMessage.getAtomId().split("entry:")[1];

			StringBuilder url = new StringBuilder(urlBuilder.getEntryCommentsUrl());
			GetEntryCommentsParameters params = new GetEntryCommentsParameters();
			params.setEntryId(entryId);
			params.setSortOrder("desc");
			params.setSortBy("lastMod");
			URLBuilder.addQueryParameters(url, params);

			// GET Board Entry Feed
			CommentFeed cFeed = new CommentFeed(user1Transport.doAtomGet(Feed.class, url.toString(), NO_HEADERS, HTTPResponseValidator.OK));
			List<CommentEntry> entries = cFeed.getEntries();
			if (entries.size() > 1) {
				for (int i = 0; i < entries.size() - 1; i++) {
					Assert.assertEquals(true, entries.get(i + 1).getUpdated().before(entries.get(i).getUpdated()));
				}
			}
		}
	}

	@Test
	public void testGetEntryCommentsUsingSortOrderAscSortByLastMod() throws Exception {

		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.ON_PREMISE) {
			ProfileService profilesService = ProfileService.parseFrom(user1Transport.doAtomGet(Service.class,
					urlBuilder.getProfilesServiceDocument(), NO_HEADERS, HTTPResponseValidator.OK));
			String boardLink = profilesService.getLinkHref(ApiConstants.SocialNetworking.REL_BOARD);

			BoardFeed boardFeed = new BoardFeed(user1Transport.doAtomGet(Feed.class, boardLink, NO_HEADERS, HTTPResponseValidator.OK));
			BoardEntry boardMessage = boardFeed.getEntries().get(0);
			String entryId = boardMessage.getAtomId().split("entry:")[1];

			StringBuilder url = new StringBuilder(urlBuilder.getEntryCommentsUrl());
			GetEntryCommentsParameters params = new GetEntryCommentsParameters();
			params.setEntryId(entryId);
			params.setSortOrder("asc");
			params.setSortBy("lastMod");
			URLBuilder.addQueryParameters(url, params);

			// GET Board Entry Feed
			CommentFeed cFeed = new CommentFeed(user1Transport.doAtomGet(Feed.class, url.toString(), NO_HEADERS, HTTPResponseValidator.OK));
			List<CommentEntry> entries = cFeed.getEntries();
			if (entries.size() > 1) {
				for (int i = 0; i < entries.size() - 1; i++) {
					Assert.assertEquals(true, entries.get(i + 1).getUpdated().after(entries.get(i).getUpdated()));
				}
			}
		}
	}

}
