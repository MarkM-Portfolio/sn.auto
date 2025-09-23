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

package com.ibm.lconn.automation.framework.services.profiles.tags.user;

import junit.framework.Assert;
import org.apache.abdera.model.Entry;
import org.testng.annotations.Test;
import com.ibm.lconn.automation.framework.services.profiles.base.AbstractTest;
import com.ibm.lconn.automation.framework.services.profiles.model.ProfileEntry;
import com.ibm.lconn.automation.framework.services.profiles.model.Tag;
import com.ibm.lconn.automation.framework.services.profiles.util.ApiConstants;
import com.ibm.lconn.automation.framework.services.profiles.util.HTTPResponseValidator;
import com.ibm.lconn.automation.framework.services.profiles.util.ServiceDocUtil;

public class ProfilesTagsTest extends AbstractTest {

	@Test
	public void testUpdateTags() throws Exception {

		LOGGER.debug("Inside Tags test");

		ProfileEntry profileEntry = ServiceDocUtil.getUserProfileEntry(user1Transport);
		String editLink = profileEntry.getLinkHref(ApiConstants.Atom.REL_EDIT);

		// get the profile entry again using the edit link (add a tag to the profile)
		ProfileEntry profileEntryToUpdate = new ProfileEntry(user1Transport.doAtomGet(Entry.class, editLink, NO_HEADERS,
				HTTPResponseValidator.OK));

		// add a tag
		String newTag = ("testUpdateProfileEntry" + System.currentTimeMillis()).toLowerCase();
		Tag aTag = new Tag(newTag);
		profileEntryToUpdate.getTags().add(aTag);
		user1Transport.doAtomPut(null, editLink, profileEntryToUpdate.toEntry(), NO_HEADERS, HTTPResponseValidator.OK);

		// fetch the profile again, and validate the tag is set
		ProfileEntry result = new ProfileEntry(user1Transport.doAtomGet(Entry.class, editLink, NO_HEADERS, HTTPResponseValidator.OK))
				.validate();

		// validate the new tag is there
		System.out.println("All available Tags: "+result.getTags());
		System.out.println("Tag added: "+aTag);
		System.out.println("Tag added successfully?: "+result.getTags().contains(aTag));
		Assert.assertTrue(result.getTags().contains(aTag));
		
		//Removing the added tags.
		LOGGER.debug("Removing the added Tags");
		profileEntryToUpdate.getTags().remove(aTag);
		user1Transport.doAtomPut(null, editLink, profileEntryToUpdate.toEntry(), NO_HEADERS, HTTPResponseValidator.OK);

	}

}
