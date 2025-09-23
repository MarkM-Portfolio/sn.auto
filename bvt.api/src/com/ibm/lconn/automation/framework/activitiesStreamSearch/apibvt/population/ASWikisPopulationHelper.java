package com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.population;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;

import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.PopStringConstantsAS;
import com.ibm.lconn.automation.framework.activitiesStreamSearch.utils.nodes.FvtMasterLogsClassPopulation;
import com.ibm.lconn.automation.framework.search.rest.api.RestAPIUser;
import com.ibm.lconn.automation.framework.search.rest.api.RestAPIUser.UserType;
import com.ibm.lconn.automation.framework.services.common.ServiceConfig;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.common.StringConstants.WikiMemberType;
import com.ibm.lconn.automation.framework.services.common.StringConstants.WikiRole;
import com.ibm.lconn.automation.framework.services.wikis.WikisService;
import com.ibm.lconn.automation.framework.services.wikis.nodes.Wiki;
import com.ibm.lconn.automation.framework.services.wikis.nodes.WikiMember;
import com.ibm.lconn.automation.framework.services.wikis.nodes.WikiPage;

public class ASWikisPopulationHelper {

	private static Abdera abdera;
	private static AbderaClient client;
	private static UsernamePasswordCredentials credentials;
	private static ServiceConfig config;
	private static WikisService service;

	private static boolean useSSL = true;
	protected static Logger LOGGER = FvtMasterLogsClassPopulation.LOGGER;

	
	
	public ASWikisPopulationHelper() throws Exception {
		RestAPIUser restAPIUser = new RestAPIUser(UserType.LOGIN);
		ServiceEntry wikisEntry = restAPIUser.getService("wikis");
		restAPIUser.addCredentials(wikisEntry);
		service = new WikisService(restAPIUser.getAbderaClient(),
				wikisEntry);
		
	}

	

	public void createPrivateWikiWithPage() {

		createWikiWithPages("Private",
				PopStringConstantsAS.PRIVATE_STANDALONE_WIKI_TITLE + " "
						+ PopStringConstantsAS.eventIdent,
				PopStringConstantsAS.PRIVATE_STANDALONE_WIKI_CONTENT,
				PopStringConstantsAS.PRIVATE_STANDALONE_WIKI_PAGE_TITLE,
				PopStringConstantsAS.PRIVATE_STANDALONE_WIKI_PAGE_CONTENT,
				PopStringConstantsAS.PRIVATE_STANDALONE_WIKI_TAG);

	}

	public void createPublicWikiWithPage() {

		createWikiWithPages("Public",
				PopStringConstantsAS.PUBLIC_STANDALONE_WIKI_TITLE + " "
						+ PopStringConstantsAS.eventIdent,
				PopStringConstantsAS.PUBLIC_STANDALONE_WIKI_CONTENT,
				PopStringConstantsAS.PUBLIC_STANDALONE_WIKI_PAGE_TITLE,
				PopStringConstantsAS.PUBLIC_STANDALONE_WIKI_PAGE_CONTENT,
				PopStringConstantsAS.PUBLIC_STANDALONE_WIKI_TAG);

	}

	public void populate() {
		try {
			
			createPrivateWikiWithPage();
			createPublicWikiWithPage();
		} catch (Exception e) {
			LOGGER.fine("Exception in communities population: "
					+ e.getMessage());
		}
	}

	// *******************************************************************************************************************
	// *******************************************************************************************************************
	// Working functions
	// *******************************************************************************************************************
	// *******************************************************************************************************************

	public void createWikiWithPages(String wikiPrivacy, String wikiTitle,
			String wikiContent, String wikiPageTitle, String wikiPageContent,
			String wikiTags) {

		ArrayList<WikiMember> wikiMembers = new ArrayList<WikiMember>();

		if (service != null) {
			if (wikiPrivacy.equals("Public")) {
				WikiMember virtualReader = new WikiMember("anonymous-user",
						WikiRole.READER, WikiMemberType.VIRTUAL);
				WikiMember virtualEditor = new WikiMember(
						"all-authenticated-users", WikiRole.EDITOR,
						WikiMemberType.VIRTUAL);
				wikiMembers.add(virtualReader);
				wikiMembers.add(virtualEditor);
			}

			Wiki newWiki = new Wiki(wikiTitle, wikiContent, wikiTags,
					wikiMembers, false);
			Entry wikiEntry = (Entry) service.createWiki(newWiki);

			if (wikiEntry != null) {

				WikiPage newWikiPage = new WikiPage(wikiPageTitle,
						wikiPageContent,
						PopStringConstantsAS.PRIVATE_STANDALONE_WIKI_PAGE_TAG,
						false);
				ExtensibleElement wikiPageCreationResult = service
						.createWikiPage(wikiEntry, newWikiPage);
			} else {
				LOGGER.fine("Wiki creation failed. Maybe the wiki name already exists");
			}
		}
	}

}
