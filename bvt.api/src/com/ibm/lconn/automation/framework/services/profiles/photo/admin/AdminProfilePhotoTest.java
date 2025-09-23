/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2015                                          */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.lconn.automation.framework.services.profiles.photo.admin;

import java.io.InputStream;
import java.util.Map;
import org.apache.abdera.model.Feed;
import org.testng.Assert;
import org.testng.annotations.Test;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.UserPerspective;
import com.ibm.lconn.automation.framework.services.common.UsersEnvironment;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Component;
import com.ibm.lconn.automation.framework.services.common.StringConstants.DeploymentType;
import com.ibm.lconn.automation.framework.services.profiles.ProfilesService;
import com.ibm.lconn.automation.framework.services.profiles.model.ProfileEntry;
import com.ibm.lconn.automation.framework.services.profiles.model.ProfileFeed;
import com.ibm.lconn.automation.framework.services.profiles.model.ProfileService;
import com.ibm.lconn.automation.framework.services.profiles.photo.PhotoBase;
import com.ibm.lconn.automation.framework.services.profiles.util.ApiConstants;
import com.ibm.lconn.automation.framework.services.profiles.util.HTTPResponseValidator;
import com.ibm.lconn.automation.framework.services.profiles.util.ServiceDocUtil;
import com.ibm.lconn.automation.framework.services.profiles.util.Sha256Encoder;
import com.ibm.lconn.automation.framework.services.profiles.util.Transport;
import com.ibm.lconn.automation.framework.services.profiles.util.URLBuilder;

public class AdminProfilePhotoTest extends PhotoBase {
	final static int ORGBUSER = 15; // OrgB user
	UserPerspective orgBUser;
	ProfilesService orgBUserService;
	Transport orgBUserTransport;
	
	@Test
	public void testGetServiceDocumentLinks() throws Exception {

		// if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.ON_PREMISE) {
			ProfileService profilesServiceAdmin = ServiceDocUtil.getAdminServiceDocument(adminTransport);

			Assert.assertNotNull(profilesServiceAdmin);
			if (null != profilesServiceAdmin) {
				Assert.assertNotNull(profilesServiceAdmin);
				Map<String, String> apiLinks = profilesServiceAdmin.getLinkHrefs();
				System.out.println("Profiles Admin Service document has links to " + apiLinks.size() + " APIs");

				int i = 1;
				for (String k : apiLinks.keySet()) {
					String s = apiLinks.get(k);
					System.out.println("[" + i + "] " + k + " URL : " + s);
					if ((k).equalsIgnoreCase(ApiConstants.SocialNetworking.REL_PROFILES_SERVICE)) {
						System.out.println("Calling GET Profiles Admin API : profiles.do");
						// verify link URL via HTTP GET
						Feed profilesResponseBody = adminTransport.doAtomGet(Feed.class, s, NO_HEADERS, HTTPResponseValidator.OK);
						// prettyPrint(profilesResponseBody);
						ProfileFeed profileFeed = new ProfileFeed(profilesResponseBody);
						Assert.assertNotNull(profileFeed, "profiles.do feed is null");
						profileFeed.validate();
						System.out.println("Profiles feed has " + profileFeed.getNumItems() + " entries");
					}
					i++;
				}
			}
		// }
	}

	@Test
	public void testUpdateProfilePhoto() throws Exception {

		HTTPResponseValidator expectedHTTPResponse = HTTPResponseValidator.OK;

		// Retreive the user's profile entry.
		ProfileEntry userProfileEntry = ServiceDocUtil.getUserProfileEntry(user1Transport);

		String[] photos = { "bird150_admin.jpg", "bird165_admin.jpg", "Easter_Island_admin.jpg", };
		String imageURL = null;

		// as Admin update another user's photo - this method also validates sizes
		imageURL = checkPhotoUploadAndUpdate("Admin", adminTransport, userProfileEntry, photos);

		// now, attempt to update it as some other non-admin user - should fail with HTTP 403
		// need to use another valid user or we get HTTP 400

		System.out.println("\n" + "As other user (" + user2Transport.getUserId() + ") update " + userProfileEntry.getName()
				+ "'s photo - should fail");

		InputStream bird150 = readPhoto("bird150_admin.jpg", 150, 150);
		Assert.assertNotNull(bird150, "bird150_admin.jpg not found");
		InputStream bird165 = readPhoto("bird165_admin.jpg", 165, 165);
		Assert.assertNotNull(bird165, "bird165_admin.jpg not found");
		InputStream easterIsland = readPhoto("Easter_Island_admin.jpg", 165, 165, false); // image is bigger than 165 X 165
		Assert.assertNotNull(easterIsland, "Easter_Island_admin.jpg not found");

		// attempting to update it as other user - should fail with HTTP 403
		expectedHTTPResponse = HTTPResponseValidator.FORBIDDEN;
		verifyPhotoPut(user2Transport, userProfileEntry, URLBuilder.Query.IMAGE, bird165, expectedHTTPResponse);

		ProfileEntry adminProfileEntry = ServiceDocUtil.getUserProfileEntry(adminTransport);
		String adminEmail = adminProfileEntry.getEmail();
		System.out.println("\n" + "As admin (" + adminEmail + ") update own photo");
		// need to re-read JPG streams since they have been consumed above.
		InputStream adminPhoto = null;
		adminPhoto = readPhoto("bird165_admin.jpg", 165, 165);

		// As admin, upload self photo.
		expectedHTTPResponse = HTTPResponseValidator.OK;
		verifyPhotoPut(adminTransport, adminProfileEntry, URLBuilder.Query.IMAGE, adminPhoto, expectedHTTPResponse);

		// retrieve the current image (as Admin). it should remain the default size (155x155)
		imageURL = adminProfileEntry.getLinkHref(ApiConstants.SocialNetworking.REL_IMAGE);
		validateImageDimensions(URLBuilder.updateLastMod(imageURL), adminTransport, PHOTO_DEFAULT_HEIGHT, PHOTO_DEFAULT_WIDTH);
	}

	@Test
	public void testUpdateProfilePhotoCrossOrg() throws Exception {

		if (StringConstants.DEPLOYMENT_TYPE != DeploymentType.SMARTCLOUD) {
			// If it is not for the cloud, do nothing.
			return;
		}
		HTTPResponseValidator expectedHTTPResponse = HTTPResponseValidator.BAD_REQUEST;
		
		UsersEnvironment userEnv = new UsersEnvironment();
		orgBUser = userEnv.getLoginUserEnvironment(ORGBUSER, Component.PROFILES.toString());
		orgBUserService = orgBUser.getProfilesService();
		orgBUserTransport = new Transport(orgBUser, orgBUserService);
		
		// Retreive the user's profile entry.
		ProfileEntry userProfileEntry = ServiceDocUtil.getUserProfileEntry(orgBUserTransport);

		String[] photos = { "bird150_admin.jpg", "bird165_admin.jpg", "Easter_Island_admin.jpg", };
		InputStream photo = readPhoto(photos[0], 150, 150);
		
		// Even as admin, they still can't update user's photo from a different org
		verifyPhotoPut(adminTransport, userProfileEntry, URLBuilder.Query.IMAGE, photo, expectedHTTPResponse);
		
	}

	private void verifyPhotoGet(Transport transport, ProfileEntry profileEntry, String queryType, HTTPResponseValidator expectedHTTPResponse)
			throws Exception {
		String photoUrl = null;
		String queryValue = null;
		if (queryType.equalsIgnoreCase(URLBuilder.Query.IMAGE)) {
			photoUrl = profileEntry.getLinkHref(ApiConstants.SocialNetworking.REL_IMAGE);
		}
		else {
			if (queryType.equalsIgnoreCase(URLBuilder.Query.EMAIL)) {
				queryValue = profileEntry.getEmail();
			}
			else if (queryType.equalsIgnoreCase(URLBuilder.Query.MCODE)) {
				String email = profileEntry.getEmail();
				queryValue = Sha256Encoder.hashLowercaseStringUTF8(email, true);
			}
			else if (queryType.equalsIgnoreCase(URLBuilder.Query.USER_ID)) {
				queryValue = profileEntry.getUserId();
			}
			// StringBuilder sb = urlBuilder.getImageUrl(queryType, queryValue);
			photoUrl = urlBuilder.getImageUrl(queryType, queryValue).toString();
		}
		System.out.println("photoUrl : " + photoUrl);
		transport.doAtomGet(null, URLBuilder.updateLastMod(photoUrl), NO_HEADERS, expectedHTTPResponse);
	}

	private void verifyPhotoPut(Transport transport, ProfileEntry profileEntry, String queryType, InputStream image,
			HTTPResponseValidator expectedHTTPResponse) throws Exception {
		String photoUrl = null;
		String queryValue = null;
		if (queryType.equalsIgnoreCase(URLBuilder.Query.IMAGE)) {
			photoUrl = profileEntry.getLinkHref(ApiConstants.SocialNetworking.REL_IMAGE);
		}
		else {
			if (queryType.equalsIgnoreCase(URLBuilder.Query.EMAIL)) {
				queryValue = profileEntry.getEmail();
			}
			else if (queryType.equalsIgnoreCase(URLBuilder.Query.MCODE)) {
				String email = profileEntry.getEmail();
				queryValue = Sha256Encoder.hashLowercaseStringUTF8(email, true);
			}
			else if (queryType.equalsIgnoreCase(URLBuilder.Query.USER_ID)) {
				queryValue = profileEntry.getUserId();
			}
			photoUrl = urlBuilder.getImageUrl(queryType, queryValue).toString();
		}
		transport.doAtomPut(null, photoUrl, image, "image/jpeg", NO_HEADERS, expectedHTTPResponse);
	}

	private String checkPhotoUploadAndUpdate(String asUser, Transport transport, ProfileEntry profileEntry, String[] photos)
			throws Exception {
		HTTPResponseValidator expectedHTTPResponse = HTTPResponseValidator.OK;
		// as self update the target user's photo
		System.out.println("\n" + "As " + asUser + " (" + transport.getUserId() + ") update " + profileEntry.getName() + "'s photo");

		InputStream photo = readPhoto(photos[0], 150, 150); // bird150
		InputStream photo1 = readPhoto(photos[1], 165, 165); // bird165
		InputStream photo2 = readPhoto(photos[2], 165, 165, false); // EasterIsland image is bigger than 165 X 165

		// verify that PUT with the 150 X 150 image
		expectedHTTPResponse = HTTPResponseValidator.OK;
		verifyPhotoPut(transport, profileEntry, URLBuilder.Query.IMAGE, photo, expectedHTTPResponse);

		// retrieve the current image (as self). it should remain the default size (155x155)
		String imageURL = profileEntry.getLinkHref(ApiConstants.SocialNetworking.REL_IMAGE);

		validateImageDimensions(URLBuilder.updateLastMod(imageURL), transport, PHOTO_DEFAULT_HEIGHT, PHOTO_DEFAULT_WIDTH);

		// verify that PUT with URL param mcode= is NOT blocked on Cloud
		expectedHTTPResponse = HTTPResponseValidator.OK;
		verifyPhotoPut(transport, profileEntry, URLBuilder.Query.MCODE, photo1, expectedHTTPResponse);

		// retrieve the current image (as self). it should remain the default size (155x155)
		// String imageURL = profileEntry.getLinkHref(ApiConstants.SocialNetworking.REL_IMAGE);

		validateImageDimensions(URLBuilder.updateLastMod(imageURL), transport, PHOTO_DEFAULT_HEIGHT, PHOTO_DEFAULT_WIDTH);

		// update to larger image (as self) using MCODE URL param
		verifyPhotoPut(transport, profileEntry, URLBuilder.Query.MCODE, photo2, expectedHTTPResponse);

		// retrieve the current image (as self). it should be scaled down
		validateImageDimensions(URLBuilder.updateLastMod(imageURL), transport, PHOTO_DEFAULT_HEIGHT, PHOTO_DEFAULT_WIDTH);

		return imageURL;
	}

	private InputStream readPhoto(String fileName, int height, int width) throws Exception {
		return readPhoto(fileName, height, width, true); // checkDimensions : true
	}

	private InputStream readPhoto(String fileName, int height, int width, boolean checkDimensions) throws Exception {
		InputStream photo = getResourceAsStream(AdminProfilePhotoTest.class, fileName);
		if (checkDimensions) {
			validateImageDimensions(photo, height, width);
			photo = getResourceAsStream(AdminProfilePhotoTest.class, fileName);
		}
		return photo;
	}

	// private ProfileEntry getAdminProfile(String orgAdminEmail) throws Exception {
	// ProfileEntry adminProfile = null;
	// String mcode = Sha256Encoder.hashLowercaseStringUTF8(orgAdminEmail, true);
	// String profileEntryUrl = urlBuilder.getProfileEntryUrl(URLBuilder.Query.MCODE, mcode);
	// Entry serverResponseBody = adminTransport.doAtomGet(Entry.class, profileEntryUrl, NO_HEADERS, HTTPResponseValidator.OK);
	// adminProfile = new ProfileEntry(serverResponseBody);
	// // testProfile.validate(); // missing LINK_HREFS for "self" & "edit"
	// return adminProfile;
	// }
}