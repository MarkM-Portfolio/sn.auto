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

package com.ibm.lconn.automation.framework.services.profiles.pronunciation.user;

import java.io.InputStream;
import junit.framework.Assert;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Service;
import org.testng.annotations.Test;
import com.ibm.lconn.automation.framework.services.profiles.base.AbstractTest;
import com.ibm.lconn.automation.framework.services.profiles.model.ProfileEntry;
import com.ibm.lconn.automation.framework.services.profiles.model.ProfileFeed;
import com.ibm.lconn.automation.framework.services.profiles.model.ProfileService;
import com.ibm.lconn.automation.framework.services.profiles.util.ApiConstants;
import com.ibm.lconn.automation.framework.services.profiles.util.HTTPResponseValidator;
import com.ibm.lconn.automation.framework.services.profiles.util.IoUtils;
import com.ibm.lconn.automation.framework.services.profiles.util.URLBuilder;


public class ProfilePronunciationTest extends AbstractTest {

	@Test
	public void testCRUD() throws Exception {
		InputStream audioFile = null;
		try {
			// get the authenticated user's profile service document
			ProfileService profilesService = ProfileService.parseFrom(user1Transport.doAtomGet(Service.class,
					urlBuilder.getProfilesServiceDocument(), NO_HEADERS, HTTPResponseValidator.OK));

			// get their profile feed and validate the data
			Feed rawFeed = user1Transport.doAtomGet(Feed.class, profilesService.getProfileFeedUrl(), NO_HEADERS, HTTPResponseValidator.OK);
			// prettyPrint(rawFeed);
			ProfileFeed profileFeed = new ProfileFeed(rawFeed);
			profileFeed.validate();
			Assert.assertEquals("There must be a single entry for the current user profile", 1, profileFeed.getEntries().size());

			// check the documented/default pronunciation URL
			ProfileEntry profileEntry = profileFeed.getEntries().get(0);
			// prettyPrint(profileEntry.toEntryXml());
			String url = profileEntry.getLinkHref(ApiConstants.SocialNetworking.REL_PRONOUNCE);

			int status = user1Transport.doStatusGet(url, NO_HEADERS);
			if (204 == status) {
				// OK, user has no pronunciation
			}
			else if (200 == status) {
				// delete existing pronunciation so we can begin from the "no pronunciation" state
				user1Transport.doAtomDelete(url, NO_HEADERS, HTTPResponseValidator.OK);

				// now there should be no pronunciation, and the response is predictable
				user1Transport.doAtomGet(null, URLBuilder.updateLastMod(url), NO_HEADERS, HTTPResponseValidator.NO_CONTENT);
			}
			else {
				Assert.fail("unhandled status: " + status);
			}

			//audioFile = this.getClass().getResourceAsStream("7A7276897.wav");
			//byte[] audioFileBytes = getBytes(audioFile);
			byte[] audioFileBytes = IoUtils.readFileAsByteArray(this.getClass(),"7A7276897.wav");

			// verify server rejects incorrect contentType
			audioFile = this.getClass().getResourceAsStream("7A7276897.wav");
			this.user1Transport.doAtomPut(null, URLBuilder.updateLastMod(url), audioFile, "image/jpeg", NO_HEADERS,
					HTTPResponseValidator.BAD_REQUEST);

			// submit with acceptable contentType this time
			audioFile = this.getClass().getResourceAsStream("7A7276897.wav");
			this.user1Transport.doAtomPut(null, URLBuilder.updateLastMod(url), audioFile, "audio/wav", NO_HEADERS,
					HTTPResponseValidator.OK);

			// verify the pronunciation exists
			byte[] afterBytes = user1Transport.doBytesGet(URLBuilder.updateLastMod(url), NO_HEADERS, HTTPResponseValidator.OK);

			// can't test for length ... the server sends back more bytes than we uploaded
			//assertEquals(audioFileBytes.length, afterBytes.length);
			
			// however the first audioFileBytes.length bytes of the response does match
			for (int i = 0; i < audioFileBytes.length; i++)
				Assert.assertEquals("mismatch at " + i + ", expected " + audioFileBytes[i] + " but found " + afterBytes[i], audioFileBytes[i], afterBytes[i]);

			// delete pronunciation to be certain we excersise DELETE
			user1Transport.doAtomDelete(url, NO_HEADERS, HTTPResponseValidator.OK);

			// verify
			user1Transport.doAtomGet(null, URLBuilder.updateLastMod(url), NO_HEADERS, HTTPResponseValidator.NO_CONTENT);
		}
		finally {
			if (null != audioFile) audioFile.close();
		}
	}
	
	//private static byte[] getBytes(InputStream is) throws Exception {
	//	if (is == null) return null;
	//	try {
	//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
	//		byte[] buf = new byte[1024];
	//		int n;
	//
	//		while ((n = is.read(buf, 0, buf.length)) > -1) {
	//			baos.write(buf, 0, n);
	//		}
	//
	//		baos.flush();
	//
	//		return baos.toByteArray();
	//	}
	//	finally {
	//		is.close();
	//	}
	//}
}
