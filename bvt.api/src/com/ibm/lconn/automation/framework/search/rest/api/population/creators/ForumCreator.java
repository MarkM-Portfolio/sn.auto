package com.ibm.lconn.automation.framework.search.rest.api.population.creators;

import static org.testng.AssertJUnit.assertTrue;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.apache.abdera.model.Element;
import org.apache.abdera.model.ExtensibleElement;

import com.ibm.lconn.automation.framework.search.rest.api.RestAPIUser;
import com.ibm.lconn.automation.framework.search.rest.api.RestAPIUser.UserType;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils.Purpose;
import com.ibm.lconn.automation.framework.search.rest.api.population.PopulatedData;
import com.ibm.lconn.automation.framework.search.rest.api.population.Populator;
import com.ibm.lconn.automation.framework.services.common.LCServiceException;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Permissions;
import com.ibm.lconn.automation.framework.services.forums.ForumsService;
import com.ibm.lconn.automation.framework.services.forums.nodes.Forum;
import com.ibm.lconn.automation.framework.services.search.data.Application;

public class ForumCreator {
	private final static Logger LOGGER = Populator.LOGGER_POPUILATOR;

	private ForumsService forumService;

	public ForumCreator() throws Exception {
		RestAPIUser restAPIUser = new RestAPIUser(UserType.LOGIN);
		ServiceEntry forumsEntry = restAPIUser.getService("forums");
		try {
			forumService = new ForumsService(restAPIUser.getAbderaClient(), forumsEntry);
		} catch (LCServiceException e) {

			LOGGER.log(Level.WARNING, "The forum is not created" + " LCServiceException: " + e.toString());
		}
	}

	public void createForum(Permissions permission) throws IOException {
		String title = SearchRestAPIUtils.generateTitle(permission, Application.forum);
		String tag = SearchRestAPIUtils.generateTagValue(Purpose.SEARCH); // tag
		// is
		// inverted
		// execId
		String forumDescription = SearchRestAPIUtils.generateDescription(title);// Content
		// is
		// inverted
		// title

		Forum forum = new Forum(title.toString(), forumDescription);
		forum.setTags(tag);
		LOGGER.fine("Create forum: " + forum.toString());
		if (forumService != null) {
			ExtensibleElement response = forumService.createForum(forum);
			Element codeElement = response.getExtension(new QName("api", "code"));
			if (codeElement != null) {
				LOGGER.log(Level.WARNING, "The forum is not created");
				assertTrue("Forums service problem, forum is not created", false);
			} else {
				PopulatedData.getInstance().setPopulatedLcEntry(forum, permission);
				LOGGER.fine("Forum created: " + response.toString());
			}
		} else {
			LOGGER.log(Level.WARNING, "The Forums service is NULL.");
			assertTrue("Forums service problem, forum is not created", false);
		}
	}
}
