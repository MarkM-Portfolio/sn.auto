package com.ibm.lconn.automation.framework.search.rest.api.population.creators;

import static org.testng.AssertJUnit.assertTrue;

import java.io.IOException;
import java.util.TimeZone;
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
import com.ibm.lconn.automation.framework.services.blogs.BlogsService;
import com.ibm.lconn.automation.framework.services.blogs.nodes.Blog;
import com.ibm.lconn.automation.framework.services.common.LCServiceException;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.CommunityBlogPermissions;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Permissions;
import com.ibm.lconn.automation.framework.services.common.Utils;
import com.ibm.lconn.automation.framework.services.communities.CommunitiesService;
import com.ibm.lconn.automation.framework.services.search.data.Application;

public class BlogsCreator {
	private final static Logger LOGGER = Populator.LOGGER_POPUILATOR;

	private BlogsService blogService;

	public BlogsCreator() throws Exception {
		RestAPIUser restAPIUser = new RestAPIUser(UserType.LOGIN);
		ServiceEntry blogsServiceEntry = restAPIUser.getService("blogs");
		try {
			blogService = new BlogsService(restAPIUser.getAbderaClient(), blogsServiceEntry);
		} catch (LCServiceException e) {

			LOGGER.log(Level.WARNING, "The blog is not created" + " LCServiceException: " + e.toString());
			assertTrue("Blogs service problem, blog is not created", false);
		}

	}

	public void createBlog() throws IOException {
		String title = SearchRestAPIUtils.generateTitle(Permissions.PUBLIC, Application.blog);
		String tag = SearchRestAPIUtils.generateTagValue(Purpose.SEARCH); // tag
		// is
		// inverted
		// execId
		String blogContent = SearchRestAPIUtils.generateDescription(title);// Content
		// is
		// inverted
		// title

		Blog newBlog = new Blog(title.toString(), blogContent, blogContent, tag, false, false,
				CommunityBlogPermissions.PUBLIC, null, TimeZone.getDefault(), true, 13, true, true, true, 0, -1, null,
				null, null, 0);
		LOGGER.fine("Create blog: " + newBlog.toString());
		if (blogService != null) {
			ExtensibleElement response = blogService.createBlog(newBlog);
			Element codeElement = response.getExtension(new QName("api", "code"));
			if (codeElement != null) {
				LOGGER.log(Level.WARNING, "The blog is not created");
				assertTrue("Blogs service problem, blog is not created", false);
			} else {
				PopulatedData.getInstance().setPopulatedLcEntry(newBlog, Permissions.PUBLIC);
				LOGGER.fine("Blog created: " + response.toString());
			}
		} else {
			LOGGER.log(Level.WARNING, "Blog service is NULL");
			assertTrue("Blogs service problem, blog is not created", false);
		}
	}

}
