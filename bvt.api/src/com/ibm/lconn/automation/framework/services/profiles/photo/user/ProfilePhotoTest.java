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

package com.ibm.lconn.automation.framework.services.profiles.photo.user;

import java.io.InputStream;
import org.testng.annotations.Test;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.DeploymentType;
import com.ibm.lconn.automation.framework.services.profiles.model.ProfileEntry;
import com.ibm.lconn.automation.framework.services.profiles.photo.PhotoBase;
import com.ibm.lconn.automation.framework.services.profiles.util.ApiConstants;
import com.ibm.lconn.automation.framework.services.profiles.util.HTTPResponseValidator;
import com.ibm.lconn.automation.framework.services.profiles.util.IoUtils;
import com.ibm.lconn.automation.framework.services.profiles.util.ServiceDocUtil;
import com.ibm.lconn.automation.framework.services.profiles.util.URLBuilder;

public class ProfilePhotoTest extends PhotoBase { // AbstractTest {

	@Test
	public void testGetProfilePhoto() throws Exception {

		// check the documented/default image URL
		ProfileEntry profileEntry = ServiceDocUtil.getUserProfileEntry(user1Transport);
		String imageUrl = profileEntry.getLinkHref(ApiConstants.SocialNetworking.REL_IMAGE);
		user1Transport.doAtomGet(null, imageUrl, NO_HEADERS, HTTPResponseValidator.OK);
    }

	@Test
	public void testGetProfilePhotoMcode() throws Exception {
		InputStream bird165 = null;

		try {
			bird165 = ProfilePhotoTest.class.getResourceAsStream("bird165.jpg");
			validateImageDimensions(bird165, 165, 165);
			bird165 = ProfilePhotoTest.class.getResourceAsStream("bird165.jpg");

			// get the authenticated users profile service document
			// ProfileService profilesService = ServiceDocUtil.getUserServiceDocument(user1Transport);
			// check the documented/default image URL
			ProfileEntry profileEntry = ServiceDocUtil.getUserProfileEntry(user1Transport);
			String imageUrl = profileEntry.getLinkHref(ApiConstants.SocialNetworking.REL_IMAGE);
			user1Transport.doAtomGet(null, imageUrl, NO_HEADERS, HTTPResponseValidator.OK);

			// update the image with the usual url
			user1Transport.doAtomPut(null, imageUrl, bird165, "image/jpeg", NO_HEADERS, HTTPResponseValidator.OK);

			// now retrieve with the mcode id
			String email = profileEntry.getEmail();
			String mcodeImageUrl = getMcodePhotoUrl(imageUrl, email);

			// retrieve the current image. it should be scaled down
			validateImageDimensions(URLBuilder.updateLastMod(mcodeImageUrl), user1Transport, 155, 155);
		}
		finally {
			IoUtils.closeQuietly(bird165);
		}
	}

	@Test
	public void testUpdateProfilePhoto() throws Exception {
		InputStream bird150 = null;
		InputStream bird165 = null;

		try {
			bird150 = ProfilePhotoTest.class.getResourceAsStream("bird150.jpg");
			validateImageDimensions(bird150, 150, 150);
			bird150 = ProfilePhotoTest.class.getResourceAsStream("bird150.jpg");

			bird165 = ProfilePhotoTest.class.getResourceAsStream("bird165.jpg");
			validateImageDimensions(bird165, 165, 165);
			bird165 = ProfilePhotoTest.class.getResourceAsStream("bird165.jpg");

			// check the documented/default image URL
			ProfileEntry profileEntry = ServiceDocUtil.getUserProfileEntry(user1Transport);
			String imageUrl = profileEntry.getLinkHref(ApiConstants.SocialNetworking.REL_IMAGE);
			user1Transport.doAtomGet(null, imageUrl, NO_HEADERS, HTTPResponseValidator.OK);
			// System.out.println(imageUrl);

			// run an update to bird150
			user1Transport.doAtomPut(null, imageUrl, bird150, "image/jpeg", NO_HEADERS, HTTPResponseValidator.OK);

			// retrieve the current image. it should remain 150x150
			validateImageDimensions(URLBuilder.updateLastMod(imageUrl), user1Transport, PHOTO_DEFAULT_HEIGHT, PHOTO_DEFAULT_WIDTH);

			// update to larger image.
			user1Transport.doAtomPut(null, imageUrl, bird165, "image/jpeg", NO_HEADERS, HTTPResponseValidator.OK);

			// retrieve the current image. it should be scaled down
			validateImageDimensions(URLBuilder.updateLastMod(imageUrl), user1Transport, 155, 155);
		}
		finally {
			IoUtils.closeQuietly(bird150);
			IoUtils.closeQuietly(bird165);
		}
	}

	@Test
	public void testDeletePhoto() throws Exception {
		InputStream bird150 = null;
		try {
			// read the image file
			bird150 = ProfilePhotoTest.class.getResourceAsStream("bird150.jpg");

			ProfileEntry profileEntry = ServiceDocUtil.getUserProfileEntry(user1Transport);
			String imageUrl = profileEntry.getLinkHref(ApiConstants.SocialNetworking.REL_IMAGE);
			user1Transport.doAtomPut(null, imageUrl, bird150, "image/jpeg", NO_HEADERS, HTTPResponseValidator.OK);
			validateImageDimensions(URLBuilder.updateLastMod(imageUrl), user1Transport, PHOTO_DEFAULT_HEIGHT, PHOTO_DEFAULT_WIDTH);

			// delete the photo
			user1Transport.doAtomDelete(imageUrl, null, HTTPResponseValidator.OK);

			// retrieve the current image. it should be the unknown image
			// this is not foolproof (if the image changes) but it is currently 128x128
			// which is not one of our test dimensions.
			validateImageDimensions(URLBuilder.updateLastMod(imageUrl), user1Transport, 128, 128);
		}
		finally {
			IoUtils.closeQuietly(bird150);
		}
	}

	@Test
	public void testGetExtProfilePhoto() throws Exception {
		// The URL /ext/photo was introduced in 4.0 (via ifix) to an external way to retrieve photos
		// as opposed to retrieving the service document.
		InputStream bird150 = null;
		try {
			// read the image file
			bird150 = ProfilePhotoTest.class.getResourceAsStream("bird150.jpg");
			validateImageDimensions(bird150, 150, 150);
			bird150 = ProfilePhotoTest.class.getResourceAsStream("bird150.jpg");

			ProfileEntry profileEntry = ServiceDocUtil.getUserProfileEntry(user1Transport);
			String imageUrl = profileEntry.getLinkHref(ApiConstants.SocialNetworking.REL_IMAGE);

			// update the photo to ensure one exists
			user1Transport.doAtomPut(null, imageUrl, bird150, "image/jpeg", NO_HEADERS, HTTPResponseValidator.OK);

			// get the service doc image URL and convert to the 'ext' version
			StringBuffer extImageUrl = new StringBuffer(imageUrl);
			int index = extImageUrl.indexOf("/photo");
			if (index > 0) { // should always be the case
				extImageUrl.insert(index, "/ext");
			}

			// the /profiles/ext/photo.do URL pattern is blocked on SC by not being explicitly exposed in
			// /opt/IBM/WebSphere/AppServer/lib/ext/authentication-mappings.json
			// TODO: we need to decide if we are exposing this API on Cloud
			if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.ON_PREMISE) {
				// retrieve the current image. it should remain 155x155
				validateImageDimensions(URLBuilder.updateLastMod(extImageUrl.toString()), user1Transport, PHOTO_DEFAULT_HEIGHT,
						PHOTO_DEFAULT_WIDTH);
			}
			validateImageDimensions(URLBuilder.updateLastMod(imageUrl), user1Transport, PHOTO_DEFAULT_HEIGHT, PHOTO_DEFAULT_WIDTH);
		}
		finally {
			IoUtils.closeQuietly(bird150);
		}
	}

	/**
	 * test that one user cannot alter another's photo.
	 */
	@Test
	public void testUpdateAnotherUserPhoto() throws Exception {
		InputStream bird150 = null;
		try {
			bird150 = ProfilePhotoTest.class.getResourceAsStream("bird150.jpg");
			// get user2's photo url.
			// ProfileService profilesServiceTwo = serviceDocUtil.getUserServiceDocument(user2Transport);
			ProfileEntry profileEntry = ServiceDocUtil.getUserProfileEntry(user2Transport);
			String imageUrlTwo = profileEntry.getLinkHref(ApiConstants.SocialNetworking.REL_IMAGE);
			// delete any photo that might exist for user2
			user2Transport.doAtomDelete(imageUrlTwo, null, HTTPResponseValidator.OK);

			// try a put as user1 with user2's photo url. should be forbidden.
			user1Transport.doAtomPut(null, imageUrlTwo, bird150, "image/jpeg", NO_HEADERS, HTTPResponseValidator.FORBIDDEN);

			// retrieve user2's photo and make sure it is empty
			// this is not foolproof (if the image changes) but it is currently 128x128
			// which is not one of our test dimensions.
			validateImageDimensions(URLBuilder.updateLastMod(imageUrlTwo), user2Transport, 128, 128);
		}
		finally {
			IoUtils.closeQuietly(bird150);
		}
	}
}
