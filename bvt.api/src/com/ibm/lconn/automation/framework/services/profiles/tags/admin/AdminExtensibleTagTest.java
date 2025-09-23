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
package com.ibm.lconn.automation.framework.services.profiles.tags.admin;

import org.apache.abdera.model.Categories;
import org.apache.abdera.model.Element;
import org.testng.Assert;
import org.testng.annotations.Test;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.UserPerspective;
import com.ibm.lconn.automation.framework.services.common.UsersEnvironment;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Component;
import com.ibm.lconn.automation.framework.services.common.StringConstants.DeploymentType;
import com.ibm.lconn.automation.framework.services.profiles.ProfilesService;
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

public class AdminExtensibleTagTest extends AbstractTest {
	final static int ORGBUSER = 15; // OrgB user
	UserPerspective orgBUser;
	ProfilesService orgBUserService;
	Transport orgBUserTransport;
	
	/**
	 * This test validates the API to introspect configuration of tags on the server.
	 * 
	 * @throws Exception
	 */

	@Test
	public void testTagCrudLifecycleAsAdmin() throws Exception {
		Assert.assertNotNull(adminTransport);
		// if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.ON_PREMISE) {
			tagCrudLifecycleAsAdmin(adminTransport, "testTagCrudLifecycleAsAdmin");
		// }
	}

	@Test
	public void testTagCrudLifecycleAsAdminCrossOrg() throws Exception {
		Assert.assertNotNull(adminTransport);
		if (StringConstants.DEPLOYMENT_TYPE != DeploymentType.SMARTCLOUD) {
			return;
		}

		UsersEnvironment userEnv = new UsersEnvironment();
		orgBUser = userEnv.getLoginUserEnvironment(ORGBUSER, Component.PROFILES.toString());
		orgBUserService = orgBUser.getProfilesService();
		orgBUserTransport = new Transport(orgBUser, orgBUserService);
		
		String tagCloudAdminUrlOrgBUser = urlBuilder.getProfileTagsUrl(null, orgBUserService.getUserId(), true, true);
		
		// Admin can not retrieve tags for a user from a different org
		adminTransport.doAtomGet(Categories.class, tagCloudAdminUrlOrgBUser, NO_HEADERS, HTTPResponseValidator.BAD_REQUEST);

		// now get the tags from user 1 via the admin user
		ProfileService user1ProfileService = ServiceDocUtil.getUserServiceDocument(user1Transport);
		String tagCloudAdminUrlUser1 = urlBuilder.getProfileTagsUrl(null, user1ProfileService.getUserId(), true, true);
		TagCloud tagCloud = new TagCloud(adminTransport.doAtomGet(Categories.class, tagCloudAdminUrlUser1, NO_HEADERS, HTTPResponseValidator.OK));

		// Use the tag cloud to do a put to orgUserB
		String tagCloudAdminUrlToPutUser1 = urlBuilder.getProfileTagsUrl(orgBUserService.getUserId(),
				orgBUserService.getUserId(), true, false);
		adminTransport.doAtomPut(null, tagCloudAdminUrlToPutUser1, tagCloud.toEntryXml(), TagCloud.CONTENT_TYPE, NO_HEADERS,
				HTTPResponseValidator.BAD_REQUEST);
		
	}
	
	public void tagCrudLifecycleAsAdmin(Transport adminUser, String prefix) throws Exception {
		// do a bad request to check for enforced arguments
		final String baseTagUrl = urlBuilder.getProfileTagsUrl(null, null, false, false);
		adminUser.doAtomGet(null, baseTagUrl, NO_HEADERS, HTTPResponseValidator.BAD_REQUEST);

		// verify that the admin user can get a user's tags via the admin endpoint, but that a non-admin user cannot get
		Transport user1 = user1Transport;
		ProfileService user1ProfileService = ServiceDocUtil.getUserServiceDocument(user1);

		// user1 can't get the admin tags
		String tagCloudAdminUrlUser1 = urlBuilder.getProfileTagsUrl(null, user1ProfileService.getUserId(), true, true);
		user1.doAtomGet(Categories.class, tagCloudAdminUrlUser1, NO_HEADERS, HTTPResponseValidator.FORBIDDEN);

		// now get the tags from user 1 via the admin user
		TagCloud tagCloud = new TagCloud(adminUser.doAtomGet(Categories.class, tagCloudAdminUrlUser1, NO_HEADERS, HTTPResponseValidator.OK));

		// create a new tag of each type
		TagsConfig tagsConfig = getTagsConfig(adminUser);
		for (TagConfig tagConfig : tagsConfig.getTagConfigs().values()) {
			String term = (prefix + System.currentTimeMillis()).toLowerCase();
			String scheme = tagConfig.getScheme();
			Tag aTag = new Tag(term, scheme);
			tagCloud.getTags().add(aTag);

			// validate a non-admin user cannot update via admin endpoint
			String tagCloudAdminUrlToPutUser1 = urlBuilder.getProfileTagsUrl(user1ProfileService.getUserId(),
					user1ProfileService.getUserId(), true, false);
			user1.doAtomPut(null, tagCloudAdminUrlToPutUser1, tagCloud.toEntryXml(), TagCloud.CONTENT_TYPE, NO_HEADERS,
					HTTPResponseValidator.FORBIDDEN);

			// do the creation now as the admin
			adminUser.doAtomPut(null, tagCloudAdminUrlToPutUser1, tagCloud.toEntryXml(), TagCloud.CONTENT_TYPE, NO_HEADERS,
					HTTPResponseValidator.OK);

			// get the tag cloud again
			TagCloud tagCloudAfter = new TagCloud(adminUser.doAtomGet(Categories.class, tagCloudAdminUrlUser1, NO_HEADERS,
					HTTPResponseValidator.OK));

			// look for the new tag
			Tag theTag = null;
			for (Tag tagObjects : tagCloudAfter.getTags()) {
				if (term.equals(tagObjects.getTerm()) && scheme.equals(tagObjects.getScheme())) {
					theTag = tagObjects;
					break;
				}
			}

			// make sure its there, and the user the admin worked on behalf of did the tag
			Assert.assertNotNull(theTag);
			Assert.assertEquals(user1ProfileService.getUserId(), theTag.getTaggers().get(0).getUserId());
			adminUser.doAtomPut(null, tagCloudAdminUrlToPutUser1, tagCloud.toEntryXml(), NO_HEADERS, HTTPResponseValidator.OK);
			tagCloud = new TagCloud(adminUser.doAtomGet(Categories.class, tagCloudAdminUrlUser1, NO_HEADERS, HTTPResponseValidator.OK));

		}
	}

	public static TagsConfig getTagsConfig(Transport t) throws Exception {
		TagsConfig result = new TagsConfig(t.doAtomGet(Element.class, urlBuilder.getTagsConfig(), NO_HEADERS,
				ApiConstants.TagConfigConstants.MEDIA_TYPE, HTTPResponseValidator.OK, false));
		return result;
	}

}
