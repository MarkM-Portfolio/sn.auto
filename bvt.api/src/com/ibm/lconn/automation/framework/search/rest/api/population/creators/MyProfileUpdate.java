package com.ibm.lconn.automation.framework.search.rest.api.population.creators;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.List;
import java.util.logging.Logger;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Categories;
import org.apache.abdera.model.Category;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.protocol.client.ClientResponse;

import com.ibm.lconn.automation.framework.search.rest.api.RestAPIUser;
import com.ibm.lconn.automation.framework.search.rest.api.RestAPIUser.UserType;
import com.ibm.lconn.automation.framework.search.rest.api.population.Populator;
import com.ibm.lconn.automation.framework.services.common.LCServiceException;
import com.ibm.lconn.automation.framework.services.common.ProfileData;
import com.ibm.lconn.automation.framework.services.common.ProfileLoader;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.profiles.ProfilesService;
import com.ibm.lconn.automation.framework.services.profiles.admin.ProfilesAdminService;
import com.ibm.lconn.automation.framework.services.profiles.admin.nodes.Profile;
import com.ibm.lconn.automation.framework.services.profiles.admin.nodes.ProfileAttribute;
import com.ibm.lconn.automation.framework.services.profiles.nodes.TagsEntry;
import com.ibm.lconn.automation.framework.services.profiles.nodes.VCardEntry;

public class MyProfileUpdate {
	private RestAPIUser profileUser1;
	private RestAPIUser profileUser2;
	private RestAPIUser profileUser3;

	public static String realNameWithTag;

	final public String TEST_JOBRESP = "test PF job";

	final public String TEST_PREFFIRSTNAME = "test PF preferredFirstName";

	final public String TEST_PREFLASTNAME = "test PF preferredLastName";

	final public String TEST_PREFALTNAME = "test PF alternateLastname";

	final public String TEST_GROUPMAIL = "testPFgroupwareEmail@janet.iris.com";

	final public String TEST_PREFFIRSTNAME_UBAHN = "U-Bahn";

	final public String TEST_RESUME_FRENCH = "\u0072\u00e9\u0073\u0075\u006d\u00e9";

	final public String TEST_OCONNOR = "\u004f\u0027\u0043\u006f\u006e\u006e\u006f\u0072";

	final public String TEST_FRANCIAS = "\u0046\u0072\u0061\u006e\u00e7\u0061\u0069\u0073";

	final public String JOBRESP_CLASS = "com.ibm.snx_profiles.base.jobResp";

	final public String PREFFIRSTNAME_CLASS = "com.ibm.snx_profiles.base.preferredFirstName";

	final public String PREFLASTNAME_CLASS = "com.ibm.snx_profiles.base.preferredLastName";

	final public String ALTLASTNAME_CLASS = "com.ibm.snx_profiles.base.alternateLastname";

	final public String GROUPEMAIL_CLASS = "com.ibm.snx_profiles.base.groupwareEmail";

	final public String WORKLOC_CLASS = "com.ibm.snx_profiles.base.workLocationCode";

	final public String ORGID_CLASS = "com.ibm.snx_profiles.base.orgId";

	final public String COUNTRYCODE_CLASS = "com.ibm.snx_profiles.base.countryCode";

	final public String DEPTNUMBER_CLASS = "com.ibm.snx_profiles.base.deptNumber";

	final public String KEY_CLASS = "com.ibm.snx_profiles.base.key";

	final public String PHONETIC_TAG = "ami";

	final public String TEST_CJKPREFFIRSTNAME = "\u8303\u5411\u519b";

	private final static Logger LOGGER = Populator.LOGGER_POPUILATOR;
	private ProfilesService profilesService1;
	private ProfilesService profilesService2;
	private ProfilesService profilesService3;

	public MyProfileUpdate() throws FileNotFoundException, IOException, URISyntaxException {
		profileUser1 = new RestAPIUser(UserType.PROFILE, 5);
		profileUser2 = new RestAPIUser(UserType.PROFILE, 6);
		profileUser3 = new RestAPIUser(UserType.PROFILE, 7);
		realNameWithTag = profileUser3.getProfData().getRealName();
		ServiceEntry profilesServiceEntry2 = profileUser2.getService("profiles");

		ServiceEntry profilesServiceEntry1 = profileUser1.getService("profiles");
		ServiceEntry profilesServiceEntry3 = profileUser3.getService("profiles");

		try {
			profilesService1 = new ProfilesService(profileUser1.getAbderaClient(), profilesServiceEntry1);
			profilesService2 = new ProfilesService(profileUser2.getAbderaClient(), profilesServiceEntry2);
			profilesService3 = new ProfilesService(profileUser3.getAbderaClient(), profilesServiceEntry3);

		} catch (LCServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void populate() throws FileNotFoundException, IOException, URISyntaxException, UnsupportedEncodingException {

		LOGGER.fine("My Profile update started  ");

		if (profilesService1 != null) {
			VCardEntry vCard = profilesService1.getUserVCard();

			vCard.setJobResp(TEST_OCONNOR + " " + TEST_JOBRESP);

			if (!profilesService1.updateProfile(vCard)) {
				LOGGER.fine("My Profile update failed : " + vCard.toString());
			}

		} else {
			LOGGER.fine("Profile service not available");
		}

		if (profilesService2 != null) {
			VCardEntry vCard = profilesService2.getUserVCard();

			vCard.setJobResp(TEST_RESUME_FRENCH + " " + TEST_JOBRESP);

			if (!profilesService2.updateProfile(vCard)) {
				LOGGER.fine("My Profile update failed : " + vCard.toString());

			}

		} else {
			LOGGER.fine("Profile service not available");
		}

		if (profilesService3 != null) {

			VCardEntry vCard = profilesService3.getUserVCard();
			String profileKey = vCard.getVCardFields().get(StringConstants.VCARD_PROFILE_KEY);
			vCard.setJobResp(TEST_CJKPREFFIRSTNAME);

			if (!profilesService3.updateProfile(vCard)) {
				LOGGER.fine("My Profile update failed : " + vCard.toString());
			}
			String postPath = profilesService3.getServiceURLString() + "/atom/profileTags.do?targetKey=" + profileKey
					+ "&sourceKey=" + profileKey;
			TagsEntry tag = new TagsEntry(PHONETIC_TAG);
			ClientResponse cr = profileUser3.getAbderaClient().put(postPath, tag.toCategories(),
					profileUser3.getOptions());
			if (cr != null) {
				if (cr.getStatus() != 200) {
					LOGGER.fine("Add tag : " + PHONETIC_TAG + " status:" + cr.getStatusText());
				}
			} else {
				LOGGER.fine("Add tag : " + PHONETIC_TAG + " status: null");
			}

		} else {
			LOGGER.fine("Profile service not available");
		}

	}
}
