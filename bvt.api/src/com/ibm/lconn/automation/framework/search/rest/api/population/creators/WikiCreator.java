package com.ibm.lconn.automation.framework.search.rest.api.population.creators;

import static org.testng.AssertJUnit.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
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
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Permissions;
import com.ibm.lconn.automation.framework.services.common.StringConstants.WikiMemberType;
import com.ibm.lconn.automation.framework.services.common.StringConstants.WikiRole;
import com.ibm.lconn.automation.framework.services.common.Utils;
import com.ibm.lconn.automation.framework.services.search.data.Application;
import com.ibm.lconn.automation.framework.services.wikis.WikisService;
import com.ibm.lconn.automation.framework.services.wikis.nodes.Wiki;
import com.ibm.lconn.automation.framework.services.wikis.nodes.WikiMember;

public class WikiCreator {
	private final static Logger LOGGER = Populator.LOGGER_POPUILATOR;

	private WikisService wikiService;

	public WikiCreator() throws Exception {
		RestAPIUser restAPIUser = new RestAPIUser(UserType.LOGIN);
		ServiceEntry wikisEntry = restAPIUser.getService("wikis");
		wikiService = new WikisService(restAPIUser.getAbderaClient(), wikisEntry);

	}

	public void createWiki(Permissions permission) throws IOException {
		String title = SearchRestAPIUtils.generateTitle(permission, Application.wiki);
		String tag = SearchRestAPIUtils.generateTagValue(Purpose.SEARCH); // tag
		// is
		// inverted
		// execId
		String wikiDescription = SearchRestAPIUtils.generateDescription(title);// Content
		// is
		// inverted
		// title

		ArrayList<WikiMember> wikiMembers = new ArrayList<WikiMember>();
		if (permission.equals(Permissions.PUBLIC)) {
			WikiMember virtualReader = new WikiMember("anonymous-user", WikiRole.READER, WikiMemberType.VIRTUAL);
			WikiMember virtualEditor = new WikiMember("all-authenticated-users", WikiRole.EDITOR,
					WikiMemberType.VIRTUAL);
			wikiMembers.add(virtualReader);
			wikiMembers.add(virtualEditor);
		}

		Wiki newWiki = new Wiki(title.toString(), wikiDescription, tag, wikiMembers, false);
		LOGGER.fine("Create wiki: " + newWiki.toString());
		if (wikiService != null) {
			ExtensibleElement response = wikiService.createWiki(newWiki);
			Element codeElement = response.getExtension(new QName("api", "code"));
			if (codeElement != null) {
				LOGGER.log(Level.WARNING, "The wiki is not created");
				assertTrue("Wikis service problem, wiki is not created", false);
			} else {
				PopulatedData.getInstance().setPopulatedLcEntry(newWiki, permission);
				LOGGER.fine("Wiki created: " + response.toString());
			}
		} else {
			LOGGER.log(Level.WARNING, "The Wikis service is NULL.");
			assertTrue("Wikis service problem, wiki is not created", false);
		}
	}
}
