package com.ibm.lconn.automation.framework.services.common;

import com.ibm.lconn.automation.framework.services.common.StringConstants.DeploymentType;

public class SetEnvironment {

	// Server specific configuration settings
	public static boolean VMODEL_ENABLED = false;

	public static boolean MODERATION_ENABLED = false;

	public static DeploymentType DEPLOYMENT_TYPE = DeploymentType.ON_PREMISE;

	public static String AUTHENTICATION = "basic";// {"basic", "form",

	// "webseal"};

	// Search Constants
	public static final String SEARCH_TERM = "Waldo";

	public static final String SEARCH_TAG_TERM = "Carmen";

	public static final String SEARCH_ACTIVITY_NAME = SEARCH_TERM + " A";

	public static final String SEARCH_BLOGS_NAME = SEARCH_TERM + " B";

	public static final String SEARCH_COMMUNITY_NAME = SEARCH_TERM + " C";

	public static final String SEARCH_DOGEAR_NAME = SEARCH_TERM + " D";

	public static final String SEARCH_FILES_NAME = SEARCH_TERM + " Fi";

	public static final String SEARCH_FORUM_NAME = SEARCH_TERM + " Fo";

	public static final String SEARCH_WIKI_NAME = SEARCH_TERM + " Wiki";

	public static final int SEARCH_ACTIVITY_INDEX = 0;

	public static final int SEARCH_BLOGS_INDEX = 1;

	public static final int SEARCH_COMMUNITY_INDEX = 2;

	public static final int SEARCH_DOGEAR_INDEX = 3;

	public static final int SEARCH_FILES_INDEX = 4;

	public static final int SEARCH_FORUM_INDEX = 5;

	public static final int SEARCH_WIKI_INDEX = 6;

}
