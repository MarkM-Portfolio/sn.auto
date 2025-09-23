package com.ibm.lconn.automation.framework.search.rest.api.population.creators;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.logging.Logger;

import org.apache.abdera.model.Categories;

import com.ibm.lconn.automation.framework.search.rest.api.RestAPIUser;
import com.ibm.lconn.automation.framework.search.rest.api.RestAPIUser.UserType;
import com.ibm.lconn.automation.framework.search.rest.api.population.Populator;
import com.ibm.lconn.automation.framework.services.common.LCServiceException;
import com.ibm.lconn.automation.framework.services.common.ProfileData;
import com.ibm.lconn.automation.framework.services.common.ProfileLoader;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.profiles.ProfilesService;
import com.ibm.lconn.automation.framework.services.profiles.admin.ProfilesAdminService;
import com.ibm.lconn.automation.framework.services.profiles.admin.nodes.Profile;
import com.ibm.lconn.automation.framework.services.profiles.admin.nodes.ProfileAttribute;
import com.ibm.lconn.automation.framework.services.profiles.nodes.TagsEntry;

public class ProfileUpdate {
	private RestAPIUser profileAdminUser;

	private ProfilesAdminService profilesAdminService;

	private ProfilesService profilesService;

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

	public ProfileUpdate() throws FileNotFoundException, IOException, URISyntaxException {
		profileAdminUser = new RestAPIUser(UserType.ADMIN);

		ServiceEntry profilesServiceEntry = profileAdminUser.getService("profiles");

		profileAdminUser.addCredentials(profilesServiceEntry);

		ProfilesService profilesService;
		try {
			profilesService = new ProfilesService(profileAdminUser.getAbderaClient(), profilesServiceEntry);
			profilesAdminService = new ProfilesAdminService(profileAdminUser.getAbderaClient(), profilesServiceEntry);
		} catch (LCServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void populate() throws FileNotFoundException, IOException, URISyntaxException, UnsupportedEncodingException {

		LOGGER.fine("Profile update started  ");
		ProfileData profData1 = ProfileLoader.getProfile(5);
		ProfileData profData2 = ProfileLoader.getProfile(6);
		ProfileData profData3 = ProfileLoader.getProfile(7);

		String targetEmail1 = URLEncoder.encode(profData1.getEmail(), "UTF-8");
		String targetEmail2 = URLEncoder.encode(profData2.getEmail(), "UTF-8");
		String targetEmail3 = URLEncoder.encode(profData3.getEmail(), "UTF-8");

		Profile profilePF1 = profilesAdminService.getFirstProfile(targetEmail1, null, null, null);
		ProfileAttribute jobRespPF = null;
		ProfileAttribute attrKeyPF = null;

		if ((jobRespPF = profilePF1.getAttribute(JOBRESP_CLASS)) != null) {
			if (!jobRespPF.getData().contains(TEST_JOBRESP)) {
				profilePF1.setAttribute(JOBRESP_CLASS, new ProfileAttribute(JOBRESP_CLASS,
						StringConstants.FieldType.TEXT.toString(), jobRespPF.getData() + " " + TEST_JOBRESP));
			}
		} else {
			profilePF1.setAttribute(JOBRESP_CLASS,
					new ProfileAttribute(JOBRESP_CLASS, StringConstants.FieldType.TEXT.toString(), TEST_JOBRESP));
		}
		profilePF1.setAttribute(PREFFIRSTNAME_CLASS, new ProfileAttribute(PREFFIRSTNAME_CLASS,
				StringConstants.FieldType.TEXT.toString(), TEST_PREFFIRSTNAME));
		profilePF1.setAttribute(PREFLASTNAME_CLASS,
				new ProfileAttribute(PREFLASTNAME_CLASS, StringConstants.FieldType.TEXT.toString(), TEST_PREFLASTNAME));
		profilePF1.setAttribute(ALTLASTNAME_CLASS,
				new ProfileAttribute(ALTLASTNAME_CLASS, StringConstants.FieldType.TEXT.toString(), TEST_PREFALTNAME));
		profilePF1.setAttribute(GROUPEMAIL_CLASS,
				new ProfileAttribute(GROUPEMAIL_CLASS, StringConstants.FieldType.TEXT.toString(), TEST_GROUPMAIL));
		profilePF1.setAttribute(WORKLOC_CLASS,
				new ProfileAttribute(WORKLOC_CLASS, StringConstants.FieldType.TEXT.toString(), "RN"));
		profilePF1.setAttribute(ORGID_CLASS,
				new ProfileAttribute(ORGID_CLASS, StringConstants.FieldType.TEXT.toString(), "WPLC"));
		profilePF1.setAttribute(COUNTRYCODE_CLASS,
				new ProfileAttribute(COUNTRYCODE_CLASS, StringConstants.FieldType.TEXT.toString(), "au"));
		profilePF1.setAttribute(DEPTNUMBER_CLASS,
				new ProfileAttribute(DEPTNUMBER_CLASS, StringConstants.FieldType.TEXT.toString(), "SM"));

		if (profilesAdminService.updateProfile(profilePF1, targetEmail1, null, null, null) == 200) {
			LOGGER.fine("Profile updated : " + targetEmail1.toString()
					+ profilesAdminService.getFirstProfile(targetEmail1, null, null, null).getContent());
		}

		Profile profilePF2 = profilesAdminService.getFirstProfile(targetEmail2, null, null, null);
		jobRespPF = null;
		if ((jobRespPF = profilePF2.getAttribute(JOBRESP_CLASS)) != null) {
			if (!jobRespPF.getData().contains(TEST_RESUME_FRENCH)) {
				profilePF2.setAttribute(JOBRESP_CLASS, new ProfileAttribute(JOBRESP_CLASS,
						StringConstants.FieldType.TEXT.toString(), jobRespPF.getData() + " " + TEST_RESUME_FRENCH));
			}
		} else {
			profilePF2.setAttribute(JOBRESP_CLASS, new ProfileAttribute(JOBRESP_CLASS,
					StringConstants.FieldType.TEXT.toString(), TEST_JOBRESP + " " + TEST_RESUME_FRENCH));
		}
		profilePF2.setAttribute(PREFFIRSTNAME_CLASS, new ProfileAttribute(PREFFIRSTNAME_CLASS,
				StringConstants.FieldType.TEXT.toString(), TEST_PREFFIRSTNAME_UBAHN));
		profilePF2.setAttribute(PREFLASTNAME_CLASS, new ProfileAttribute(PREFLASTNAME_CLASS,
				StringConstants.FieldType.TEXT.toString(), TEST_OCONNOR + " " + TEST_FRANCIAS));

		if (profilesAdminService.updateProfile(profilePF2, targetEmail2, null, null, null) == 200) {
			LOGGER.fine("Profile updated : " + targetEmail2.toString()
					+ profilesAdminService.getFirstProfile(targetEmail2, null, null, null).getContent());
		}
		Profile profilePF3 = profilesAdminService.getFirstProfile(targetEmail3, null, null, null);
		jobRespPF = null;

		profilePF3.setAttribute(PREFFIRSTNAME_CLASS, new ProfileAttribute(PREFFIRSTNAME_CLASS,
				StringConstants.FieldType.TEXT.toString(), TEST_CJKPREFFIRSTNAME));

		if (profilesAdminService.updateProfile(profilePF3, targetEmail3, null, null, null) == 200) {
			LOGGER.fine("Profile updated : " + targetEmail3.toString()
					+ profilesAdminService.getFirstProfile(targetEmail3, null, null, null).getContent());
		}

		Categories tags = null;
		if ((attrKeyPF = profilePF3.getAttribute(KEY_CLASS)) != null) {
			if ((tags = profilesAdminService.getProfileTags(targetEmail3, attrKeyPF.getData(), false, targetEmail3,
					attrKeyPF.getData())) != null) {
				TagsEntry tagsBefore = new TagsEntry(tags);
				String tagsString = tagsBefore.toString();
				if (!tagsString.contains(PHONETIC_TAG)) {
					tagsBefore.addTag(PHONETIC_TAG);
					profilesAdminService.setProfileTags(tagsBefore, targetEmail3, attrKeyPF.getData(), targetEmail3,
							attrKeyPF.getData());
				}
			} else {
				TagsEntry tagsNew = new TagsEntry(PHONETIC_TAG);
				profilesAdminService.setProfileTags(tagsNew, targetEmail3, attrKeyPF.getData(), targetEmail3,
						attrKeyPF.getData());
			}
		}
	}
}
