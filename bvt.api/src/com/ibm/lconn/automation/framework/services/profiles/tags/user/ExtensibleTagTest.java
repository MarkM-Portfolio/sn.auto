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
package com.ibm.lconn.automation.framework.services.profiles.tags.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.abdera.model.Categories;
import org.apache.abdera.model.Element;
import org.apache.commons.httpclient.methods.PutMethod;
import org.testng.Assert;
import org.testng.annotations.Test;
import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;
import com.ibm.lconn.automation.framework.services.profiles.base.AbstractTest;
import com.ibm.lconn.automation.framework.services.profiles.model.ProfileService;
import com.ibm.lconn.automation.framework.services.profiles.model.Tag;
import com.ibm.lconn.automation.framework.services.profiles.model.TagCloud;
import com.ibm.lconn.automation.framework.services.profiles.model.TagConfig;
import com.ibm.lconn.automation.framework.services.profiles.model.TagsConfig;
import com.ibm.lconn.automation.framework.services.profiles.util.ApiConstants;
import com.ibm.lconn.automation.framework.services.profiles.util.HTTPResponseValidator;
import com.ibm.lconn.automation.framework.services.profiles.util.ServiceDocUtil;
import com.ibm.lconn.automation.framework.services.profiles.util.Transport;
import com.ibm.lconn.automation.framework.services.profiles.util.URLBuilder;

public class ExtensibleTagTest extends AbstractTest {

	private String SOURCE_USERID = "sourceUserid";

	/**
	 * This test validates the API to introspect configuration of tags on the server.
	 * 
	 * @throws Exception
	 */
	
	@Test
	public void testConfiguration() throws Exception {
		TagsConfig tagsConfig = getTagsConfig(user1Transport);
		Assert.assertTrue(tagsConfig.getTagConfigs().size() > 0);

		// validate that we have a general type
		TagConfig generalTag = tagsConfig.getTagConfigs().get(ApiConstants.TagConfigConstants.GENERAL);
		Assert.assertNotNull(generalTag);
		generalTag.validate();

		// if (isOnPremise()) {
		// // validate that the seedlist definition has entries for each tag configuration type
		// String seedlistUrlNow = getSeedlistForNow(searchTransport);
		// SeedlistFeed seedlist = new SeedlistFeed(searchTransport.doAtomGet(Feed.class, seedlistUrlNow, NO_HEADERS,
		// HTTPResponseValidator.OK));
		//
		// // validate we have tag info in seedlist for across all tag types
		// SeedlistFieldInfo tagFieldInfo = seedlist.getSeedlistFieldInfoById().get("FIELD_TAG");
		// SeedlistFieldInfo taggerFieldInfo = seedlist.getSeedlistFieldInfoById().get("FIELD_TAGGER");
		// SeedlistFieldInfo taggerUidFieldInfo = seedlist.getSeedlistFieldInfoById().get("FIELD_TAGGER_UID");
		// Assert.assertNotNull(tagFieldInfo);
		// Assert.assertNotNull(taggerFieldInfo);
		// Assert.assertNotNull(taggerUidFieldInfo);
		//
		// // look for tag information specific to that tag type
		// for (TagConfig tagConfig : tagsConfig.getTagConfigs().values()) {
		// String fieldTag = IndexAttribute.getIndexFieldName(IndexAttribute.TAG, tagConfig.getType());
		// String fieldTaggerDisplayName = IndexAttribute.getIndexFieldName(IndexAttribute.TAGGER_DISPLAY_NAME, tagConfig.getType());
		// String fieldTaggerUid = IndexAttribute.getIndexFieldName(IndexAttribute.TAGGER_UID, tagConfig.getType());
		// tagFieldInfo = seedlist.getSeedlistFieldInfoById().get(fieldTag);
		// taggerFieldInfo = seedlist.getSeedlistFieldInfoById().get(fieldTaggerDisplayName);
		// taggerUidFieldInfo = seedlist.getSeedlistFieldInfoById().get(fieldTaggerUid);
		// Assert.assertNotNull(tagFieldInfo);
		// Assert.assertNotNull(taggerFieldInfo);
		// Assert.assertNotNull(taggerUidFieldInfo);
		// // TODO pending 91811, we need to validate that tagFieldInfo has information on if its a facet or not
		// }
		// }
	}

	@Test
	public void testTagTypeAhead() throws Exception {
		Transport user1 = user1Transport;
		ProfileService user1ProfileService = ServiceDocUtil.getUserServiceDocument(user1);
		String tagCloudUrlMain = user1ProfileService.getLinkHref(ApiConstants.SocialNetworking.REL_TAG_CLOUD);
		String tagCloudUrlMainWithSourceUser1Id = URLBuilder.addQueryParameter(tagCloudUrlMain, SOURCE_USERID,
				user1ProfileService.getUserId(), false);
		TagsConfig tagsConfig = getTagsConfig(user1);

		String tagBase = "testTagTypeAhead" + System.currentTimeMillis();

		List<Tag> tagsToCheckInTypeAhead = new ArrayList<Tag>();
		Map<String, Tag> tagsToCheckByType = new HashMap<String, Tag>();

		for (TagConfig tagConfig : tagsConfig.getTagConfigs().values()) {

			// as user1, get tags cloud, and add 1 new tag with the tag base
			TagCloud tagCloud = new TagCloud(user1.doAtomGet(Categories.class, tagCloudUrlMain, NO_HEADERS, HTTPResponseValidator.OK));

			// user1 adds a tag of this type
			String tagTerm = (tagBase + tagConfig.getType()).toLowerCase();
			String tagScheme = tagConfig.getScheme();
			String tagType = tagConfig.getType();
			Tag tag = new Tag(tagTerm, tagScheme);
			tag.setType(tagType);
			
			//add the tag to the tagCloud
			tagCloud.getTags().add(tag);
			tagsToCheckInTypeAhead.add(tag);
			tagsToCheckByType.put(tagType, tag);
			System.out.println(tagCloud.toEntryXml());
			user1.doAtomPut(null, tagCloudUrlMainWithSourceUser1Id, tagCloud.toEntryXml(), NO_HEADERS, HTTPResponseValidator.OK);
		}

		// the type-ahead url without a filter should return all the tags we just created
		String typeAheadUrl = urlBuilder.getTagTypeAhead(tagBase, null);
		JSONArray items = user1.doJSONArrayGet(typeAheadUrl, NO_HEADERS, HTTPResponseValidator.OK);
		Assert.assertEquals(items.size(), tagsConfig.getTagConfigs().keySet().size());
		for (int i = 0; i < items.size(); i++) {
			// validate that
			JSONObject object = (JSONObject) items.get(i);
			String tag = (String) object.get("tag");
			String type = (String) object.get("type");
			boolean foundMatch = false;
			for (Tag aTag : tagsToCheckInTypeAhead) {
				if (aTag.getTerm().equals(tag) && aTag.getType().equals(type)) {
					foundMatch = true;
				}
			}
			Assert.assertTrue(foundMatch);
		}

		// now do type-ahead by filter on type
		for (TagConfig tagConfig : tagsConfig.getTagConfigs().values()) {
			typeAheadUrl = urlBuilder.getTagTypeAhead(tagBase, tagConfig.getType());
			items = user1.doJSONArrayGet(typeAheadUrl, NO_HEADERS, HTTPResponseValidator.OK);
			Assert.assertEquals(items.size(), 1);
			JSONObject object = (JSONObject) items.get(0);
			String tag = (String) object.get("tag");
			String type = (String) object.get("type");
			Tag tagToCheck = tagsToCheckByType.get(tagConfig.getType());
			Assert.assertEquals(tagToCheck.getTerm(), tag);
			Assert.assertEquals(tagToCheck.getType(), type);
		}
	}

	/**
	 * Remove all tags from profile, add new tags, get seedlist, ensure its in proper field
	 * 
	 * @throws Exception
	 */
	@Test
	public void testTagSeedlistIntegration() throws Exception {

		Transport user1 = user1Transport;
		ProfileService user1ProfileService = ServiceDocUtil.getUserServiceDocument(user1);
		String tagCloudUrlMain = user1ProfileService.getLinkHref(ApiConstants.SocialNetworking.REL_TAG_CLOUD);
		String tagCloudUrlMainWithSourceUser1Id = URLBuilder.addQueryParameter(tagCloudUrlMain, SOURCE_USERID,
				user1ProfileService.getUserId(), false);
		TagsConfig tagsConfig = getTagsConfig(user1);

		// remove all tags from profile based on configuration
		for (TagConfig tagConfig : tagsConfig.getTagConfigs().values()) {
			// get current tags, remove all (source + others), so we start empty
			TagCloud tagCloud = new TagCloud(user1.doAtomGet(Categories.class, tagCloudUrlMain, NO_HEADERS, HTTPResponseValidator.OK));
			for (Tag tag : tagCloud.getTags()) {
				// DELETE profileTags.do?targetKey=...&tag=hi&type=...
				String tagToDelete = URLBuilder.addQueryParameter(tagCloudUrlMain, "tag", tag.getTerm(), false);
				tagToDelete = URLBuilder.addQueryParameter(tagToDelete, "type", tag.getType(), false);
				user1.doAtomDelete(tagToDelete, NO_HEADERS, HTTPResponseValidator.OK);
			}
		}

		// make sure we have no tags
		TagCloud tagCloud = new TagCloud(user1.doAtomGet(Categories.class, tagCloudUrlMain, NO_HEADERS, HTTPResponseValidator.OK));
		Assert.assertEquals(tagCloud.getTags().size(), 0);

		// String seedlistUrlNow = null;
		// if (isOnPremise()) {
		// // crawl to latest point in seedlist
		// seedlistUrlNow = getSeedlistForNow(searchTransport);
		// }
		// add a tag to profile for each type
		Map<String, Tag> tagByType = new HashMap<String, Tag>();
		for (TagConfig tagConfig : tagsConfig.getTagConfigs().values()) {
			String tagTerm = ("testSeedlist_" + tagConfig.getType()).toLowerCase();
			String tagScheme = tagConfig.getScheme();
			Tag tag = new Tag(tagTerm, tagScheme);
			tagCloud.getTags().add(tag);
			tagByType.put(tagConfig.getType(), tag);
		}
		user1.doAtomPut(null, tagCloudUrlMainWithSourceUser1Id, tagCloud.toEntryXml(), NO_HEADERS, HTTPResponseValidator.OK);

		Thread.sleep(1000);

		// if (isOnPremise()) {
		// // get seedlist field, and ensure that each entry is populated as expected
		// // fetch the seedlist update
		// SeedlistFeed seedlist = new SeedlistFeed(searchTransport.doAtomGet(Feed.class, seedlistUrlNow, NO_HEADERS,
		// HTTPResponseValidator.OK));
		// Assert.assertEquals(1, seedlist.getEntries().size());
		// SeedlistEntry seedlistEntry = seedlist.getEntries().get(0);
		//
		// // the field names that have all tags across types
		// final String ALL_TAGS_FIELD = "FIELD_TAG";
		// final String ALL_TAGS_TAGGER = "FIELD_TAGGER";
		// final String ALL_TAGS_TAGGER_UID = "FIELD_TAGGER_UID";
		//
		// // does the field contain all the tags across types
		// List<String> allTags = seedlistEntry.getFieldValues(ALL_TAGS_FIELD);
		// List<String> allTaggers = seedlistEntry.getFieldValues(ALL_TAGS_TAGGER);
		// List<String> allTaggersUid = seedlistEntry.getFieldValues(ALL_TAGS_TAGGER_UID);
		// for (Tag tag : tagCloud.getTags()) {
		// Assert.assertTrue(allTags.contains(tag.getTerm()));
		// }
		// allTaggers.contains(user1ProfileService.getTitle());
		// allTaggersUid.contains(user1ProfileService.getUserId());
		//
		// // now validate that each tag specific field only has tags of that type
		// for (TagConfig tagConfig : tagsConfig.getTagConfigs().values()) {
		//
		// // the field names in seedlist scoped to this type
		// String TAG = IndexAttribute.getIndexFieldName(IndexAttribute.TAG, tagConfig.getType());
		// String TAGGER = IndexAttribute.getIndexFieldName(IndexAttribute.TAGGER_DISPLAY_NAME, tagConfig.getType());
		// String TAGGER_UID = IndexAttribute.getIndexFieldName(IndexAttribute.TAGGER_UID, tagConfig.getType());
		//
		// List<String> tags = seedlistEntry.getFieldValues(TAG);
		// List<String> taggers = seedlistEntry.getFieldValues(TAGGER);
		// List<String> taggerUid = seedlistEntry.getFieldValues(TAGGER_UID);
		// Assert.assertEquals(tags.size(), 1);
		// Assert.assertEquals(taggers.size(), 1);
		// Assert.assertEquals(taggerUid.size(), 1);
		//
		// Tag toCheck = tagByType.get(tagConfig.getType());
		// Assert.assertTrue(tags.contains(toCheck.getTerm()));
		// Assert.assertTrue(taggers.contains(user1ProfileService.getTitle()));
		// Assert.assertTrue(taggerUid.contains(user1ProfileService.getUserId()));
		// }
		// }
	}

	@Test
	public void testTagOperationsForLegacyClients() throws Exception {
		// test works with user 1 and user 2
		Transport user1 = user1Transport;

		// get the users profile documents
		ProfileService user1ProfileService = ServiceDocUtil.getUserServiceDocument(user1);

		// URL to fetch tag cloud on user1
		String tagCloudUrlMain = user1ProfileService.getLinkHref(ApiConstants.SocialNetworking.REL_TAG_CLOUD);
		String tagCloudUrlMainWithSourceUser1IdExtensionAware = urlBuilder.getProfileTagsUrl(user1ProfileService,
				user1ProfileService.getUserId(), true);
		String tagCloudUrlMainWithSourceUser1IdNotExtensionAware = urlBuilder.getProfileTagsUrl(user1ProfileService,
				user1ProfileService.getUserId(), false);

		// get the tag config and iterate over each type, and add a tag of that type
		TagsConfig tagsConfig = getTagsConfig(user1);
		for (TagConfig tagConfig : tagsConfig.getTagConfigs().values()) {

			// get current tags, remove all (source + others), so we start empty
			TagCloud tagCloud = new TagCloud(user1.doAtomGet(Categories.class, tagCloudUrlMain, NO_HEADERS, HTTPResponseValidator.OK));
			for (Tag tag : tagCloud.getTags()) {
				// DELETE profileTags.do?targetKey=...&tag=hi&type=...
				String tagToDelete = URLBuilder.addQueryParameter(tagCloudUrlMain, "tag", tag.getTerm(), false);
				tagToDelete = URLBuilder.addQueryParameter(tagToDelete, "type", tag.getType(), false);
				user1.doAtomDelete(tagToDelete, NO_HEADERS, HTTPResponseValidator.OK);
			}

			// as user1, get tags again, confirm we have 0 tags
			tagCloud = new TagCloud(user1.doAtomGet(Categories.class, tagCloudUrlMain, NO_HEADERS, HTTPResponseValidator.OK));
			Assert.assertEquals(0, tagCloud.getTags().size());

			// user1 adds a tag of this type using the extension aware endpoint
			String tagTerm = ("testTagCrudLifecycle_" + tagConfig.getType()).toLowerCase();
			String tagScheme = tagConfig.getScheme();
			Tag tag = new Tag(tagTerm, tagScheme);
			tagCloud.getTags().add(tag);
			user1.doAtomPut(null, tagCloudUrlMainWithSourceUser1IdExtensionAware, tagCloud.toEntryXml(), NO_HEADERS,
					HTTPResponseValidator.OK);

			// ensure the tag is there
			tagCloud = new TagCloud(user1.doAtomGet(Categories.class, tagCloudUrlMain, NO_HEADERS, HTTPResponseValidator.OK));
			Assert.assertEquals(1, tagCloud.getTags().size());

			// user1 adds a tag of type "general" using the not extension aware end-point (therefore, the extension tag is not sent to
			// server)
			tagCloud.getTags().clear();
			String otherTagTerm = ("testTagCrudLifecycle_" + tagConfig.getType()).toLowerCase() + "_general";
			Tag otherTag = new Tag(otherTagTerm);
			tagCloud.getTags().add(otherTag);
			user1.doAtomPut(null, tagCloudUrlMainWithSourceUser1IdNotExtensionAware, tagCloud.toEntryXml(), NO_HEADERS,
					HTTPResponseValidator.OK);

			// if we are sending up base tags back and forth, then base tags are always synched, so we would have 1 item instead of 2
			int numExpected = tagConfig.getType().equals("general") ? 1 : 2;
			tagCloud = new TagCloud(user1.doAtomGet(Categories.class, tagCloudUrlMain, NO_HEADERS, HTTPResponseValidator.OK));
			Assert.assertEquals(tagCloud.getTags().size(), numExpected);

			Set<Tag> tagsToCheck = new HashSet<Tag>();
			tagsToCheck.add(tag);
			tagsToCheck.add(otherTag);

			for (Tag aTag : tagCloud.getTags()) {
				Assert.assertNotNull(tagsToCheck.remove(aTag));
			}

			// now send up the original tag cloud to the extension aware point
			tagCloud.getTags().clear();
			tagCloud.getTags().add(tag);
			user1.doAtomPut(null, tagCloudUrlMainWithSourceUser1IdExtensionAware, tagCloud.toEntryXml(), NO_HEADERS,
					HTTPResponseValidator.OK);

			// ensure the tag is the only 1
			tagCloud = new TagCloud(user1.doAtomGet(Categories.class, tagCloudUrlMain, NO_HEADERS, HTTPResponseValidator.OK));
			Assert.assertEquals(tagCloud.getTags().size(), 1);

			tagsToCheck = new HashSet<Tag>();
			tagsToCheck.add(tag);

			for (Tag aTag : tagCloud.getTags()) {
				Assert.assertNotNull(tagsToCheck.remove(aTag));
			}

		}
	}

	@Test
	public void testTagChangeTagType() throws Exception {
		// test works with user1 and user 2
		Transport user1 = user1Transport;
		Transport user2 = user2Transport;

		// get the users profile documents
		ProfileService user1ProfileService = ServiceDocUtil.getUserServiceDocument(user1);
		ProfileService user2ProfileService = ServiceDocUtil.getUserServiceDocument(user2);

		// URL to fetch tag cloud on user1
		String tagCloudUrlMain = user1ProfileService.getLinkHref(ApiConstants.SocialNetworking.REL_TAG_CLOUD);
		String tagCloudUrlMainWithSourceUser1Id = urlBuilder.getProfileTagsUrl(user1ProfileService, user1ProfileService.getUserId(), true);
		String tagCloudUrlMainWithSourceUser2Id = urlBuilder.getProfileTagsUrl(user1ProfileService, user2ProfileService.getUserId(), true);

		// get the tag config and iterate over each type and add a tag of that type
		TagsConfig tagsConfig = getTagsConfig(user1);
		// if there is only 1 type of tag defined, then we exit the test
		if (tagsConfig.getTagConfigs().keySet().size() == 1) {
			return;
		}

		// iterate over the tag types supported
		for (TagConfig tagConfig : tagsConfig.getTagConfigs().values()) {

			// get current tags, remove all (source + others), so we start empty
			TagCloud tagCloud = new TagCloud(user1.doAtomGet(Categories.class, tagCloudUrlMain, NO_HEADERS, HTTPResponseValidator.OK));
			for (Tag tag : tagCloud.getTags()) {
				// DELETE profileTags.do?targetKey=...&tag=hi&type=...
				String tagToDelete = URLBuilder.addQueryParameter(tagCloudUrlMain, "tag", tag.getTerm(), false);
				tagToDelete = URLBuilder.addQueryParameter(tagToDelete, "type", tag.getType(), false);
				user1.doAtomDelete(tagToDelete, NO_HEADERS, HTTPResponseValidator.OK);
			}

			// as user1, get tags again, confirm we have 0 tags
			tagCloud = new TagCloud(user1.doAtomGet(Categories.class, tagCloudUrlMain, NO_HEADERS, HTTPResponseValidator.OK));
			Assert.assertEquals(tagCloud.getTags().size(), 0);

			// user1 adds a tag of this type
			String tagTerm = ("testTagChangeTagType" + tagConfig.getType()).toLowerCase();
			String tagScheme = tagConfig.getScheme();
			Tag tag = new Tag(tagTerm, tagScheme);
			tagCloud.getTags().add(tag);
			user1.doAtomPut(null, tagCloudUrlMainWithSourceUser1Id, tagCloud.toEntryXml(), NO_HEADERS, HTTPResponseValidator.OK);

			// as user1, get tags again, confirm we have 1 tag, and it matches
			tagCloud = new TagCloud(user1.doAtomGet(Categories.class, tagCloudUrlMain, NO_HEADERS, HTTPResponseValidator.OK));
			Assert.assertEquals(tagCloud.getTags().size(), 1);
			Assert.assertEquals(tag, tagCloud.getTags().get(0));

			// as user2, get user1 tag cloud, and add the same tag
			tagCloud = new TagCloud(user2.doAtomGet(Categories.class, tagCloudUrlMainWithSourceUser2Id, NO_HEADERS,
					HTTPResponseValidator.OK));
			tagCloud.getTags().add(tag);
			user2.doAtomPut(null, tagCloudUrlMainWithSourceUser2Id, tagCloud.toEntryXml(), NO_HEADERS, HTTPResponseValidator.OK);

			// get tag cloud, ensure 1 tag, but multiple sources for same tags
			tagCloud = new TagCloud(user1.doAtomGet(Categories.class, tagCloudUrlMain, NO_HEADERS, HTTPResponseValidator.OK));
			Assert.assertEquals(tagCloud.getTags().size(), 1);
			Tag tagToCompare = tagCloud.getTags().get(0);
			Assert.assertEquals(tag, tagToCompare);
			Assert.assertEquals(tagToCompare.getFrequency(), 2);

			// as user1, get tags again
			tagCloud = new TagCloud(user1.doAtomGet(Categories.class, tagCloudUrlMain, NO_HEADERS, HTTPResponseValidator.OK));

			// iterate over the tag categories available, and move the tag to the new type
			TagConfig fromTagConfig = tagConfig;
			for (TagConfig toTagConfig : tagsConfig.getTagConfigs().values()) {

				// skip moving to your own current type
				if (toTagConfig.getType().equals(tagConfig.getType())) continue;

				// move url operation
				// String tagMoveUrl = urlBuilder.getProfileMoveTagsToNewTypeUrl(user1ProfileService, tagTerm, fromTagConfig.getType(),
				// toTagConfig.getType());
				PutMethod p = new PutMethod();
				// p.setURI(new URI(tagMoveUrl));
				// user1.doAtomPut(null, tagMoveUrl, null, NO_HEADERS, HTTPResponseValidator.OK);
				user1.doHttpPutMethod(null, p, NO_HEADERS, HTTPResponseValidator.OK);
				// re-fetch the tag cloud for the user and ensure it has the new type for both taggers
				tagCloud = new TagCloud(user1.doAtomGet(Categories.class, tagCloudUrlMain, NO_HEADERS, HTTPResponseValidator.OK));
				Assert.assertEquals(tagCloud.getTags().size(), 1);
				Tag moveTagToCompare = tagCloud.getTags().get(0);
				Assert.assertEquals(moveTagToCompare.getTerm(), tagTerm);
				Assert.assertEquals(toTagConfig.getType(), moveTagToCompare.getType());
				Assert.assertEquals(2, moveTagToCompare.getFrequency());
				// so we can update again
				fromTagConfig = toTagConfig;
			}
		}
	}

	@Test
	public void testTagCRUDLifecycle() throws Exception {
		// test works with user 1 and user 2
		Transport user1 = user1Transport;
		Transport user2 = user2Transport;

		// get the users profile documents
		ProfileService user1ProfileService = ServiceDocUtil.getUserServiceDocument(user1);
		ProfileService user2ProfileService = ServiceDocUtil.getUserServiceDocument(user2);

		// URL to fetch tag cloud on user1
		String tagCloudUrlMain = user1ProfileService.getLinkHref(ApiConstants.SocialNetworking.REL_TAG_CLOUD);
		String tagCloudUrlMainWithSourceUser1Id = urlBuilder.getProfileTagsUrl(user1ProfileService, user1ProfileService.getUserId(), true);
		String tagCloudUrlMainWithSourceUser2Id = urlBuilder.getProfileTagsUrl(user1ProfileService, user2ProfileService.getUserId(), true);

		// get the tag config and iterate over each type and add a tag of that type
		TagsConfig tagsConfig = getTagsConfig(user1);
		for (TagConfig tagConfig : tagsConfig.getTagConfigs().values()) {

			// get current tags, remove all (source + others), so we start empty
			TagCloud tagCloud = new TagCloud(user1.doAtomGet(Categories.class, tagCloudUrlMain, NO_HEADERS, HTTPResponseValidator.OK));
			for (Tag tag : tagCloud.getTags()) {
				// DELETE profileTags.do?targetKey=...&tag=hi&type=...
				String tagToDelete = URLBuilder.addQueryParameter(tagCloudUrlMain, "tag", tag.getTerm(), false);
				tagToDelete = URLBuilder.addQueryParameter(tagToDelete, "type", tag.getType(), false);
				user1.doAtomDelete(tagToDelete, NO_HEADERS, HTTPResponseValidator.OK);
			}

			// as user1, get tags again, confirm we have 0 tags
			tagCloud = new TagCloud(user1.doAtomGet(Categories.class, tagCloudUrlMain, NO_HEADERS, HTTPResponseValidator.OK));
			Assert.assertEquals(tagCloud.getTags().size(), 0);

			// user1 adds a tag of this type
			String tagTerm = ("testTagCrudLifecycle_" + tagConfig.getType()).toLowerCase();
			String tagScheme = tagConfig.getScheme();
			Tag tag = new Tag(tagTerm, tagScheme);
			tagCloud.getTags().add(tag);
			user1.doAtomPut(null, tagCloudUrlMainWithSourceUser1Id, tagCloud.toEntryXml(), NO_HEADERS, HTTPResponseValidator.OK);

			// as user1, get tags again, confirm we have 1 tag, and it matches
			tagCloud = new TagCloud(user1.doAtomGet(Categories.class, tagCloudUrlMain, NO_HEADERS, HTTPResponseValidator.OK));
			Assert.assertEquals(tagCloud.getTags().size(), 1);
			Assert.assertEquals(tag, tagCloud.getTags().get(0));

			// as user2, get user1 tag cloud, and add the same tag
			tagCloud = new TagCloud(user2.doAtomGet(Categories.class, tagCloudUrlMainWithSourceUser2Id, NO_HEADERS,
					HTTPResponseValidator.OK));
			tagCloud.getTags().add(tag);
			user2.doAtomPut(null, tagCloudUrlMainWithSourceUser2Id, tagCloud.toEntryXml(), NO_HEADERS, HTTPResponseValidator.OK);

			// get tag cloud, ensure 1 tag, but multiple sources for same tags
			tagCloud = new TagCloud(user1.doAtomGet(Categories.class, tagCloudUrlMain, NO_HEADERS, HTTPResponseValidator.OK));
			Assert.assertEquals(tagCloud.getTags().size(), 1);
			Tag tagToCompare = tagCloud.getTags().get(0);
			Assert.assertEquals(tag, tagToCompare);
			Assert.assertEquals(tagToCompare.getFrequency(), 2);

			// as user1, get tags again
			tagCloud = new TagCloud(user1.doAtomGet(Categories.class, tagCloudUrlMain, NO_HEADERS, HTTPResponseValidator.OK));

			// user1 adds new tags
			tagCloud.getTags().clear();
			tagTerm = ("testTagCrudLifecycle1_" + tagConfig.getType() + System.currentTimeMillis()).toLowerCase();
			tagScheme = tagConfig.getScheme();
			Tag tag1 = new Tag(tagTerm, tagScheme);
			tagTerm = ("testTagCrudLifecycle2_" + tagConfig.getType() + System.currentTimeMillis()).toLowerCase();
			Tag tag2 = new Tag(tagTerm, tagScheme);
			tagCloud.getTags().add(tag1);
			tagCloud.getTags().add(tag2);
			user1.doAtomPut(null, tagCloudUrlMainWithSourceUser1Id, tagCloud.toEntryXml(), NO_HEADERS, HTTPResponseValidator.OK);
			tagCloud = new TagCloud(user1.doAtomGet(Categories.class, tagCloudUrlMain, NO_HEADERS, HTTPResponseValidator.OK));
			Assert.assertEquals(tagCloud.getTags().size(), 3);

			// get current tags, remove all (source + others), so we start empty
			tagCloud = new TagCloud(user1.doAtomGet(Categories.class, tagCloudUrlMain, NO_HEADERS, HTTPResponseValidator.OK));
			for (Tag o : tagCloud.getTags()) {
				// DELETE profileTags.do?targetKey=...&tag=hi&type=...
				String tagToDelete = URLBuilder.addQueryParameter(tagCloudUrlMain, "tag", o.getTerm(), false);
				tagToDelete = URLBuilder.addQueryParameter(tagToDelete, "type", o.getType(), false);
				user1.doAtomDelete(tagToDelete, NO_HEADERS, HTTPResponseValidator.OK);
			}

			// user1 adds 4 new new tags
			for (int i = 0; i < 4; i++) {
				tagTerm = "testAddasdas_" + i + System.currentTimeMillis();
				tagCloud.getTags().add(new Tag(tagTerm, tagScheme));
			}
			user1.doAtomPut(null, tagCloudUrlMainWithSourceUser1Id, tagCloud.toEntryXml(), NO_HEADERS, HTTPResponseValidator.OK);

			// user clears out tags, and adds 3 new ones
			tagCloud.getTags().clear();
			for (int i = 0; i < 3; i++) {
				tagTerm = "testBAdasdasd_" + i + System.currentTimeMillis();
				tagCloud.getTags().add(new Tag(tagTerm, tagScheme));
			}
			user1.doAtomPut(null, tagCloudUrlMainWithSourceUser1Id, tagCloud.toEntryXml(), NO_HEADERS, HTTPResponseValidator.OK);
			tagCloud = new TagCloud(user1.doAtomGet(Categories.class, tagCloudUrlMain, NO_HEADERS, HTTPResponseValidator.OK));
			Assert.assertEquals(tagCloud.getTags().size(), 3);

			for (Tag o : tagCloud.getTags()) {
				// DELETE profileTags.do?targetKey=...&tag=hi&type=...
				String tagToDelete = URLBuilder.addQueryParameter(tagCloudUrlMain, "tag", o.getTerm(), false);
				tagToDelete = URLBuilder.addQueryParameter(tagToDelete, "type", o.getType(), false);
				user1.doAtomDelete(tagToDelete, NO_HEADERS, HTTPResponseValidator.OK);
			}

			// if tag config supports phrases, add multi-word tag
			if (tagConfig.isPhraseSupported()) {

				// add multi-word tag
				tagCloud = new TagCloud(user1.doAtomGet(Categories.class, tagCloudUrlMainWithSourceUser1Id, NO_HEADERS,
						HTTPResponseValidator.OK));
				tag = new Tag("this is a test " + tagConfig.getType().toLowerCase(), tagConfig.getScheme());
				tagCloud.getTags().add(tag);
				user1.doAtomPut(null, tagCloudUrlMainWithSourceUser1Id, tagCloud.toEntryXml(), NO_HEADERS, HTTPResponseValidator.OK);

				// validate
				tagCloud = new TagCloud(user1.doAtomGet(Categories.class, tagCloudUrlMain, NO_HEADERS, HTTPResponseValidator.OK));
				Assert.assertEquals(tagCloud.getTags().size(), 1);
				System.out.println(tagCloud.toEntryXml());
				Assert.assertEquals(tag, tagCloud.getTags().get(0));

			}
			else {

				// add multi-word tag, and ensure that its broken into terms
				tagCloud = new TagCloud(user1.doAtomGet(Categories.class, tagCloudUrlMainWithSourceUser1Id, NO_HEADERS,
						HTTPResponseValidator.OK));
				tag = new Tag("this is a test " + tagConfig.getType().toLowerCase(), tagConfig.getScheme());
				tagCloud.getTags().add(tag);
				user1.doAtomPut(null, tagCloudUrlMainWithSourceUser1Id, tagCloud.toEntryXml(), NO_HEADERS, HTTPResponseValidator.OK);

				String[] termArray = tag.getTerm().split(" ");

				// validate
				tagCloud = new TagCloud(user1.doAtomGet(Categories.class, tagCloudUrlMain, NO_HEADERS, HTTPResponseValidator.OK));
				Assert.assertEquals(tagCloud.getTags().size(), termArray.length);
				for (String termToCheck : termArray) {
					Tag toCheck = new Tag(termToCheck, tag.getScheme());
					Assert.assertTrue(tagCloud.getTags().contains(toCheck));
				}
			}

			// clean-up all tags again
			tagCloud = new TagCloud(user1.doAtomGet(Categories.class, tagCloudUrlMain, NO_HEADERS, HTTPResponseValidator.OK));
			for (Tag o : tagCloud.getTags()) {
				// DELETE profileTags.do?targetKey=...&tag=hi&type=...
				String tagToDelete = URLBuilder.addQueryParameter(tagCloudUrlMain, "tag", o.getTerm(), false);
				tagToDelete = URLBuilder.addQueryParameter(tagToDelete, "type", o.getType(), false);
				user1.doAtomDelete(tagToDelete, NO_HEADERS, HTTPResponseValidator.OK);
			}
		}
	}

	@Test
	public void testConfirmUserCannotImpersonateOtherUser() throws Exception {
		Transport user1 = user1Transport;
		Transport user2 = user2Transport;

		ProfileService user1ProfileService = ServiceDocUtil.getUserServiceDocument(user1);

		String tagCloudUrlMain = user1ProfileService.getLinkHref(ApiConstants.SocialNetworking.REL_TAG_CLOUD);
		String tagCloudUrlMainWithSourceUser1Id = URLBuilder.addQueryParameter(tagCloudUrlMain, SOURCE_USERID,
				user1ProfileService.getUserId(), false);
		//Get the tags from User1
		TagCloud tagCloud = new TagCloud(user1.doAtomGet(Categories.class, tagCloudUrlMainWithSourceUser1Id, NO_HEADERS,
				HTTPResponseValidator.OK));
		Tag tag = new Tag("testConfirmUserCannotImpersonateOtherUser_" + System.currentTimeMillis());
		tagCloud.getTags().add(tag);

		// confirm user2 cannot impersonate user1
		user2.doAtomPut(null, tagCloudUrlMainWithSourceUser1Id, tagCloud.toEntryXml(), TagCloud.CONTENT_TYPE, NO_HEADERS,
				HTTPResponseValidator.FORBIDDEN);

		// confirm admin user cannot impersonate user1
		adminTransport.doAtomPut(null, tagCloudUrlMainWithSourceUser1Id, tagCloud.toEntryXml(), TagCloud.CONTENT_TYPE, NO_HEADERS,
				HTTPResponseValidator.FORBIDDEN);
	}

//	static String test1 = "t%E2%80%8Bt";
//	static String test2 = "_\u200B_"; // zero-width space
//	static String test3 = "_\u2003_"; // EM space
//	static String test4 = "_\u202F_"; // narrow no-break space
//	static final String[] nonPrintChars = { test1, test2, test3, test4 };

	public static TagsConfig getTagsConfig(Transport t) throws Exception {
		TagsConfig result = new TagsConfig(t.doAtomGet(Element.class, urlBuilder.getTagsConfig(), NO_HEADERS,
				ApiConstants.TagConfigConstants.MEDIA_TYPE, HTTPResponseValidator.OK, false));
		return result;
	}

}
