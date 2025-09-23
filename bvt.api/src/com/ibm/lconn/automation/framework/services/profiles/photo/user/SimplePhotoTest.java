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
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import org.apache.abdera.protocol.client.ClientResponse;
import org.testng.annotations.Test;
import com.ibm.lconn.automation.framework.services.profiles.model.ProfileEntry;
import com.ibm.lconn.automation.framework.services.profiles.model.ProfileService;
import com.ibm.lconn.automation.framework.services.profiles.photo.PhotoBase;
import com.ibm.lconn.automation.framework.services.profiles.util.ApiConstants;
import com.ibm.lconn.automation.framework.services.profiles.util.HTTPResponseValidator;
import com.ibm.lconn.automation.framework.services.profiles.util.IoUtils;
import com.ibm.lconn.automation.framework.services.profiles.util.ServiceDocUtil;
import com.ibm.lconn.automation.framework.services.profiles.util.Transport;
import com.ibm.lconn.automation.framework.services.profiles.util.URLBuilder;

/**
 * Stand alone test not intended to be part of the test suite. It will allow us to quickly run a photo update via the api when investigating
 * issues. Copy your photo into this directory and set the name as noted below.
 */
public class SimplePhotoTest extends PhotoBase {

	@Test(groups = { "diagnostic" })
	public void testGetServiceDocument() throws Exception {
		// get the authenticated users profile service document
		@SuppressWarnings("unused")
		ProfileService profilesService = ServiceDocUtil.getUserServiceDocument(user1Transport);
	}

	@Test(groups = { "diagnostic" })
	public void testUpdateProfilePhoto() throws Exception {
		InputStream thephoto = null;

		// copy your photo into this directory and set photoName. add width and height if desired.
		String photoName = "bird150.jpg";
		int width = -99;
		int height = -99;

		try {
			if (height > 0 && width > 0) {
				thephoto = SimplePhotoTest.class.getResourceAsStream(photoName);
				validateImageDimensions(thephoto, height, width);
			}
			thephoto = SimplePhotoTest.class.getResourceAsStream(photoName);

			// check the documented/default image URL
			ProfileEntry profileEntry = ServiceDocUtil.getUserProfileEntry(user1Transport);
			String imageUrl = profileEntry.getLinkHref(ApiConstants.SocialNetworking.REL_IMAGE);
			user1Transport.doAtomGet(null, imageUrl, NO_HEADERS, HTTPResponseValidator.OK);
			// System.out.println(imageUrl);

			// update the photo
			user1Transport.doAtomPut(null, imageUrl, thephoto, "image/jpeg", NO_HEADERS, HTTPResponseValidator.OK);

			// retrieve the current image and print dimensions
			printImageDimensions(URLBuilder.updateLastMod(imageUrl), user1Transport);
		}
		finally {
			IoUtils.closeQuietly(thephoto);
		}
	}

	// not converted yet.
	// public void testDeletePhoto() throws Exception {
	// InputStream bird150 = null;
	// try {
	// // read the image file
	// bird150 = QuickPhotoTest.class.getResourceAsStream("bird150.jpg");
	//
	// // get the authenticated users profile service document
	// ProfileService profilesService = ProfileService.parseFrom(user1Transport.doAtomGet(Service.class,
	// urlBuilder.getProfilesServiceDocument(), NO_HEADERS, HTTPResponseValidator.OK));
	//
	// // get their profile feed and validate the data
	// Feed rawFeed = user1Transport.doAtomGet(Feed.class, profilesService.getProfileFeedUrl(), NO_HEADERS, HTTPResponseValidator.OK);
	// // prettyPrint(rawFeed);
	// ProfileFeed profileFeed = new ProfileFeed(rawFeed);
	// profileFeed.validate();
	// Assert.assertEquals("There must be a single entry for the current user profile", 1, profileFeed.getEntries().size());
	//
	// // add a photo and then retrieve to see that is is of expected dimension
	// ProfileEntry profileEntry = profileFeed.getEntries().get(0);
	// String imageUrl = profileEntry.getLinkHref(ApiConstants.SocialNetworking.REL_IMAGE);
	// user1Transport.doAtomPut(null, imageUrl, bird150, "image/jpeg", NO_HEADERS, HTTPResponseValidator.OK);
	// validateImageDimensions(URLBuilder.updateLastMod(imageUrl), user1Transport, PHOTO_DEFAULT_HEIGHT, PHOTO_DEFAULT_WIDTH);
	//
	// // delete the photo
	// user1Transport.doAtomDelete(imageUrl,null,HTTPResponseValidator.OK);
	//
	// // retrieve the current image. it should be the unknown image
	// // this is not foolproof (if the image changes) but it is currently 128x128
	// // which is not one of our test dimensions.
	// validateImageDimensions(URLBuilder.updateLastMod(imageUrl), user1Transport, 128, 128);
	// }
	// finally {
	// IoUtils.closeQuietly(bird150);
	// }
	// }

	protected void printImageDimensions(String imageUrl, Transport transport) throws Exception {
		ClientResponse response = null;
		InputStream is = null;

		try {
			response = transport.doResponseGet(imageUrl, NO_HEADERS);
			is = response.getInputStream();
			printImageDimensions(is);
		}
		finally {
			IoUtils.closeQuietly(is);
			response.release();
		}
	}

	protected void printImageDimensions(InputStream is) throws Exception {
		ImageReader ir = null;
		ImageInputStream iis = null;

		try {
			iis = ImageIO.createImageInputStream(is);

			Iterator<?> imageReaderIterator = ImageIO.getImageReaders(iis);
			ir = (ImageReader) imageReaderIterator.next();

			ir.setInput(iis);

			System.out.println("image width: " + ir.getWidth(0));
			System.out.println("image height: " + ir.getHeight(0));
		}
		finally {
			ir = null;
			iis.close();
			IoUtils.closeQuietly(is);
		}
	}
}
