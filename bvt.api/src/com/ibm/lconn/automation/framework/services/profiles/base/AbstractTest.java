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

package com.ibm.lconn.automation.framework.services.profiles.base;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.abdera.Abdera;
import org.apache.abdera.model.Base;
import org.apache.abdera.writer.Writer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import com.ibm.lconn.automation.framework.services.common.ServiceConfig;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.DeploymentType;
import com.ibm.lconn.automation.framework.services.common.UserPerspective;
import com.ibm.lconn.automation.framework.services.common.UsersEnvironment;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Component;
import com.ibm.lconn.automation.framework.services.profiles.ProfilesService;
import com.ibm.lconn.automation.framework.services.profiles.admin.ProfilesAdminService;
import com.ibm.lconn.automation.framework.services.profiles.util.ServiceDocUtil;
import com.ibm.lconn.automation.framework.services.profiles.util.Transport;
import com.ibm.lconn.automation.framework.services.profiles.util.URLBuilder;

public class AbstractTest {

	public static final Map<String, String> NO_HEADERS = new HashMap<String, String>(0);

	public static final Map<String, String> CACHE_CONTROL_PUBLIC = new HashMap<String, String>(1);

	public static final Map<String, String> CACHE_CONTROL_PRIVATE = new HashMap<String, String>(1);

	public static final Map<String, String> CONTENT_TYPE_SERVICE = new HashMap<String, String>(1);
	static {
		CACHE_CONTROL_PRIVATE.put("Cache-Control", "private,must-revalidate,max-age=0");
		CACHE_CONTROL_PUBLIC.put("Cache-Control", "public,must-revalidate,max-age=0");
		// CONTENT_TYPE_SERVICE.put("Content-Type", ApiConstants.Atom.MEDIA_TYPE_ATOM_SERVICE_DOCUMENT);
	}

	public static boolean isOnCloud() {
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD)
			return true;
		else
			return false;
	}

	public static boolean isOnPremise() {
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.ON_PREMISE)
			return true;
		else
			return false;
	}

	protected static URLBuilder urlBuilder;
	protected static ServiceDocUtil serviceDocUtil;

	protected final static Logger LOGGER = LoggerFactory.getLogger(AbstractTest.class.getName());

	static ServiceConfig config;

	protected static UserPerspective user1, user2, user3, adminUser, adminNoProfile;

	public static ProfilesService userProfilesService, user2ProfilesService, user3ProfilesService;
	public static ProfilesAdminService adminProfilesService, adminNoProfilesService;

	protected static Transport user1Transport, user2Transport, user3Transport, adminTransport;
	protected static Transport adminNoProfileTransport = null;

	protected final int sleepTime = 100;

	@BeforeClass
	public void setUp() throws Exception {

		// creating user perspective for the user1
		UsersEnvironment usersEnv = new UsersEnvironment();
		user1 = usersEnv.getLoginUserEnvironment(StringConstants.CURRENT_USER, Component.PROFILES.toString());
		userProfilesService = user1.getProfilesService();
		user1Transport = new Transport(user1, userProfilesService);

		// creating user perspective for the user2
		UsersEnvironment users2Env = new UsersEnvironment();
		user2 = users2Env.getLoginUserEnvironment(StringConstants.RANDOM1_USER, Component.PROFILES.toString());
		user2ProfilesService = user2.getProfilesService();
		// creating transport object for the main user
		user2Transport = new Transport(user2, user2ProfilesService);

		// creating user perspective for the user3
		UsersEnvironment users3Env = new UsersEnvironment();
		user3 = users3Env.getLoginUserEnvironment(StringConstants.RANDOM2_USER, Component.PROFILES.toString());
		user3ProfilesService = user3.getProfilesService();
		// creating transport object for the main user
		user3Transport = new Transport(user3, user3ProfilesService);

		// creating user perspective for the admin user
		UsersEnvironment adminUsersEnv = new UsersEnvironment();
		adminUser = adminUsersEnv.getLoginUserEnvironment(StringConstants.ADMIN_USER, Component.PROFILES.toString());
		adminProfilesService = adminUser.getProfilesAdminService();
		// creating transport object for the admin user
		adminTransport = new Transport(adminUser, adminProfilesService);

		// creating user perspective for the wasadmin/adminWithNoProfile
		// UsersEnvironment adminNoProfileEnv = new UsersEnvironment();
		// adminNoProfile = adminNoProfileEnv.getLoginUserEnvironment(StringConstants.ADMIN_NO_USER,
		// Component.PROFILES.toString());
		// adminNoProfilesService = adminNoProfile.getProfilesAdminService();
		// //creating transport object for the admin user
		// adminNoProfileTransport = new Transport();
		// adminNoProfileTransport.setup(adminProfilesService.getClient(), adminProfilesService.get_http(),
		// adminNoProfile.getUserId(), adminNoProfile.getPassword());

		urlBuilder = new URLBuilder();
	}

	protected static final Abdera ABDERA = new Abdera();

	public static Writer WRITER = ABDERA.getWriterFactory().getWriter("prettyxml");

	public static void prettyPrint(Base base) throws Exception {
		// if (null != base) {
		// WRITER.writeTo(base, System.out);
		// System.out.println();
		// }
		// else {
		// System.out.println("prettyPrint(): NULL");
		// }
		prettyPrint(base, System.out);
	}

	public static void prettyPrint(Base base, OutputStream out) throws Exception {
		if (null != base) {
			WRITER.writeTo(base, out);
			out.write('\n');
			// System.out.println();
		}
		else {
			System.out.println("prettyPrint(): NULL");
		}
	}
}
