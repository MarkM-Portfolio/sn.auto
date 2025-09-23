package com.ibm.lconn.automation.framework.search.rest.api.population.creators;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.apache.abdera.model.Element;
import org.apache.abdera.model.ExtensibleElement;

import com.ibm.lconn.automation.framework.search.rest.api.RestAPIUser;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils;
import com.ibm.lconn.automation.framework.search.rest.api.RestAPIUser.UserType;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils.Purpose;
import com.ibm.lconn.automation.framework.search.rest.api.population.PopulatedData;
import com.ibm.lconn.automation.framework.search.rest.api.population.Populator;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Permissions;
import com.ibm.lconn.automation.framework.services.communities.CommunitiesService;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class CommunitiesCreatorForQuickResults {
	private final static Logger LOGGER = Populator.LOGGER_POPUILATOR;

	private CommunitiesService commService;

	private static final String OPEN_HIGHLIGHT_TAG = "<b>";

	private static final String CLOSE_HIGHLIGHT_TAG = "</b>";

	private static String QR_EXEC_ID = SearchRestAPIUtils.getExecId(Purpose.QUICK_RESULTS);

	public static String TITLE_HIGHLIGHT_ON_JAPANISE_WORD = "\u65b0\u5e74\u304a\u3081\u3067\u3068\u3046\uff01" + " "
			+ "\u3042\u306a\u305f\u3082\uff01";

	public static String EXPECTED_TITLE_HIGHLIGHT_ON_JAPANISE_WORD = OPEN_HIGHLIGHT_TAG + "\u65b0" + CLOSE_HIGHLIGHT_TAG
			+ OPEN_HIGHLIGHT_TAG + "\u5e74" + CLOSE_HIGHLIGHT_TAG;

	public static String TITLE_HIGHLIGHT_MAX_LENGTH = "This design would have worked well for compatibility if application programs had only used MSDOS services to perform device IO and indeed the same design philosophy is embodied in Windows NT see Hardware Abstraction Layer However";

	public static String EXPECTED_TITLE_HIGHLIGHT_MAX_LENGTH = OPEN_HIGHLIGHT_TAG + "This" + CLOSE_HIGHLIGHT_TAG
			+ " design would have worked well for compatibility if application programs had only used MSDOS services to perform device IO and indeed the same design philosophy is embodied in Windows NT see Hardware Abstraction Layer However";

	public static String TITLE_HIGHLIGHT_ACCENT_CHARACTERS = "si\u00e8ge";

	public static String EXPECTED_TITLE_HIGHLIGHT_ACCENT_CHARACTERS = OPEN_HIGHLIGHT_TAG + "siege"
			+ CLOSE_HIGHLIGHT_TAG;

	public static String TITLE_HIGHLIGHT_UPPER_CASE = "WORLD";

	public static String EXPECTED_TITLE_HIGHLIGHT_UPPER_CASE = OPEN_HIGHLIGHT_TAG + "WORLD" + CLOSE_HIGHLIGHT_TAG;

	public static String TITLE_HIGHLIGHT_IN_THE_MIDDLE = "hello world connections";

	public static String EXPECTED_TITLE_HIGHLIGHT_IN_THE_MIDDLE = "hello " + OPEN_HIGHLIGHT_TAG + "world"
			+ OPEN_HIGHLIGHT_TAG + " connections";

	public CommunitiesCreatorForQuickResults() throws Exception {
		RestAPIUser restAPIUser = new RestAPIUser(UserType.QUICKRESULTS);

		ServiceEntry commServiceEntry = restAPIUser.getService("communities");
		commService = new CommunitiesService(restAPIUser.getAbderaClient(), commServiceEntry);
	}

	public void createCommunities() throws IOException {
		createJapaniseCommunity(QR_EXEC_ID + " " + TITLE_HIGHLIGHT_ON_JAPANISE_WORD);
		createJapaniseCommunity(QR_EXEC_ID + " " + TITLE_HIGHLIGHT_MAX_LENGTH);
		createJapaniseCommunity(QR_EXEC_ID + " " + TITLE_HIGHLIGHT_ACCENT_CHARACTERS);
		createJapaniseCommunity(QR_EXEC_ID + " " + TITLE_HIGHLIGHT_UPPER_CASE);
		createJapaniseCommunity(QR_EXEC_ID + " " + TITLE_HIGHLIGHT_IN_THE_MIDDLE);
	}

	private void createJapaniseCommunity(String title) throws IOException {

		String tag = SearchRestAPIUtils.generateTagValue(Purpose.QUICK_RESULTS);
		String commDescription = SearchRestAPIUtils.generateDescription(title);

		Community community = new Community(title, commDescription, Permissions.PUBLIC, tag);
		LOGGER.fine("Create community: " + community.toString());

		if (commService != null) {
			ExtensibleElement response = commService.createCommunity(community);
			Element codeElement = response.getExtension(new QName("api", "code"));
			if (codeElement != null) {
				LOGGER.log(Level.WARNING, "The community is not created");
			} else {
				LOGGER.fine("Community created: " + response.toString());
			}
		} else {
			LOGGER.log(Level.WARNING, "The Communities service is NULL.");
		}
	}
}
