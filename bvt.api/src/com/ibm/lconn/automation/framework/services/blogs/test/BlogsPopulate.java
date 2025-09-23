package com.ibm.lconn.automation.framework.services.blogs.test;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.xpath.XPathAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.services.blogs.BlogsTestBase;
import com.ibm.lconn.automation.framework.services.blogs.nodes.Blog;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.BlogsType;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Component;
import com.ibm.lconn.automation.framework.services.common.UsersEnvironment;
import com.ibm.lconn.automation.framework.services.common.Utils;

/**
 * JUnit Tests via Connections API for Blogs Service
 * 
 * @author James Cunningham - jamcunni@ie.ibm.com
 */

public class BlogsPopulate extends BlogsTestBase {

	protected final static Logger LOGGER = LoggerFactory
			.getLogger(BlogsPopulate.class.getName());

	private static final String METAWEBLOG_URL = "/services/xmlrpc";

	private static final String XML_DECLARATION = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n";

	private static final String METAWEBLOG_TEMPLATE_NEWPOST = XML_DECLARATION
			+ "<methodCall><methodName><![CDATA[metaWeblog.newPost]]></methodName><params><param><value><string><![CDATA[${blogId}]]></string></value></param><param><value><string><![CDATA[${userId}]]></string></value></param><param><value><string><![CDATA[${password}]]></string></value></param><param><value><struct><member><name><![CDATA[title]]></name><value><string><![CDATA[${title}]]></string></value></member><member><name><![CDATA[description]]></name><value><string><![CDATA[${description}]]></string></value></member><member><name><![CDATA[categories]]></name><value><array><data></data></array></value></member><member><name><![CDATA[mt_keywords]]></name><value><string><![CDATA[]]></string></value></member><member><name><![CDATA[custom_fields]]></name><value><array><data></data></array></value></member></struct></value></param><param><value><boolean>1</boolean></value></param></params></methodCall>";

	private static final String METAWEBLOG_TEMPLATE_GETRECENTPOSTS = XML_DECLARATION
			+ "<methodCall><methodName><![CDATA[metaWeblog.getRecentPosts]]></methodName><params><param><value><string><![CDATA[${blogId}]]></string></value></param><param><value><string><![CDATA[${userId}]]></string></value></param><param><value><string><![CDATA[${password}]]></string></value></param><param><value><int>100</int></value></param></params></methodCall>";

	private static final String METAWEBLOG_TEMPLATE_EDITPOST = XML_DECLARATION
			+ "<methodCall><methodName><![CDATA[metaWeblog.editPost]]></methodName><params><param><value><string><![CDATA[${entryId}]]></string></value></param><param><value><string><![CDATA[${userId}]]></string></value></param><param><value><string><![CDATA[${password}]]></string></value></param><param><value><struct><member><name><![CDATA[title]]></name><value><string><![CDATA[${title}]]></string></value></member><member><name><![CDATA[description]]></name><value><string><![CDATA[${description}]]></string></value></member><member><name><![CDATA[categories]]></name><value><array><data></data></array></value></member><member><name><![CDATA[mt_keywords]]></name><value><string><![CDATA[]]></string></value></member><member><name><![CDATA[date_created_gmt]]></name><value><dateTime.iso8601>20150104T03:09:49Z</dateTime.iso8601></value></member><member><name><![CDATA[custom_fields]]></name><value><array><data></data></array></value></member></struct></value></param><param><value><boolean>1</boolean></value></param></params></methodCall>";

	private static final String METAWEBLOG_TEMPLATE_DELETEPOST = XML_DECLARATION
			+ "<methodCall><methodName><![CDATA[blogger.deletePost]]></methodName><params><param><value><string><![CDATA[0123456789ABCDEF]]></string></value></param><param><value><string><![CDATA[${entryId}]]></string></value></param><param><value><string><![CDATA[${userId}]]></string></value></param><param><value><string><![CDATA[${password}]]></string></value></param><param><value><boolean>1</boolean></value></param></params></methodCall>";

	@BeforeMethod
	public static void setUp() throws Exception {

		LOGGER.debug("Start Initializing Blogs Data Population Test");

		UsersEnvironment userEnv = new UsersEnvironment();
		user = userEnv.getLoginUserEnvironment(StringConstants.CURRENT_USER,
				Component.BLOGS.toString());
		service = user.getBlogsService();
		imUser = user;
		impersonateService = service;

		otherUser = userEnv.getLoginUserEnvironment(
				StringConstants.RANDOM1_USER, Component.BLOGS.toString());
		
		//clean up
		/*ArrayList<Blog> myBlogs = service.getMyBlogs(null, null, 0, 100, null,
				null, null, null, BlogsType.BLOG, null, null);
		for (Blog blog : myBlogs) {
			service.deleteBlog(blog.getEditHref());
			LOGGER.debug("Deleted blog " + blog.getTitle());
		}*/

		LOGGER.debug("Finished Initializing Blogs Data Population Test");
	}

	@Test
	public void createBlog() throws Exception {
		super.createBlog();
	}

	// TODO: following 7 methods run error on impersonate, skipped there
	@Test
	public void getMyBlogs() {
		super.getMyBlogs();
	}

	@Test
	public void getMedia() throws IOException {
		super.getMedia();
	}

	@Test
	public void publishingAPISortOrder() throws MalformedURLException,
			URISyntaxException, InterruptedException {
		super.publishingAPISortOrder();
	}

	@Test
	public void enableDisableBlogEdits() throws FileNotFoundException,
			IOException {
		super.enableDisableBlogEdits();
	}

	@Test
	public void startIndexAndItemsPerPage() throws MalformedURLException,
			URISyntaxException {
		super.startIndexAndItemsPerPage();
	}

	@Test
	public void getAtomBlogPages() throws Exception {
		super.getAtomBlogPages();
	}

	@Test
	public void blogsModerationQuarantine() throws FileNotFoundException,
			IOException {
		super.blogsModerationQuarantine();
	}

	@Test
	public void getFeaturedBlogPosts() {
		super.getFeaturedBlogPosts();
	}

	@Test
	public void getAllBlogs() {
		super.getAllBlogs();
	}

	@Test
	public void blogsFollowsService() throws FileNotFoundException, IOException {
		super.blogsFollowsService();
	}

	@Test
	public void blogsSortOrder() {
		super.blogsSortOrder();
	}

	@Test
	public void metaWeblogAPI() throws Exception {
		final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		String handle = "MetaWebLogAPI-" + System.currentTimeMillis();
		Blog regBlog = new Blog("Test Meta Weblog API", handle,
				"This blog is for testing and verifying MetaWeblog API",
				"tagBlogs_" + Utils.logDateFormatter.format(new Date()), false,
				false, null, null, TimeZone.getDefault(), true, 13, true, true,
				true, 0, -1, null, null, null, 0);
		service.createBlog(regBlog);
		assertEquals("Create Blog", 201, service.getRespStatus());

		final String apiUrl = service.getServiceURLString() + METAWEBLOG_URL;
		LOGGER.debug("meta weblog API url: " + apiUrl);

		HttpClient client = new HttpClient();
		if (Boolean.parseBoolean(System.getProperty("useFiddler"))) {
			client.getHostConfiguration().setProxy("localhost", 8888);
		}
		LOGGER.debug("create blog entry");
		PostMethod method = new PostMethod(apiUrl);
		String content = METAWEBLOG_TEMPLATE_NEWPOST
				.replace("${blogId}", handle)
				.replace("${userId}", user.getUserName())
				.replace("${password}", user.getPassword())
				.replace("${title}", "entry1 via metaweblog API")
				.replace("${description}",
						"this is an entry created via metaweblog API");
		method.setRequestEntity(new StringRequestEntity(content, "text/xml",
				"utf-8"));
		client.executeMethod(method);
		assertEquals("response code", 200, method.getStatusCode());
		org.w3c.dom.Document doc = dbf.newDocumentBuilder().parse(
				method.getResponseBodyAsStream());
		org.w3c.dom.Node n = XPathAPI.selectSingleNode(
				doc.getDocumentElement(), "params/param/value");
		assertNotNull(n);
		String postId1 = n.getTextContent();
		LOGGER.debug("blog entry id is " + postId1);

		LOGGER.debug("create blog entry");
		method = new PostMethod(apiUrl);
		method.setRequestEntity(new StringRequestEntity(content, "text/xml",
				"utf-8"));
		client.executeMethod(method);
		assertEquals("response code", 200, method.getStatusCode());
		doc = dbf.newDocumentBuilder().parse(method.getResponseBodyAsStream());
		n = XPathAPI.selectSingleNode(doc.getDocumentElement(),
				"params/param/value");
		assertNotNull(n);
		String postId2 = n.getTextContent();
		LOGGER.debug("blog entry id is " + postId2);

		LOGGER.debug("get recent blog entry");
		method = new PostMethod(apiUrl);
		content = METAWEBLOG_TEMPLATE_GETRECENTPOSTS
				.replace("${blogId}", handle)
				.replace("${userId}", user.getUserName())
				.replace("${password}", user.getPassword());
		method.setRequestEntity(new StringRequestEntity(content, "text/xml",
				"utf-8"));
		client.executeMethod(method);
		assertEquals("response code", 200, method.getStatusCode());
		doc = dbf.newDocumentBuilder().parse(method.getResponseBodyAsStream());
		org.w3c.dom.NodeList nodeList = XPathAPI.selectNodeList(
				doc.getDocumentElement(),
				"//value/struct/member[name/text()='postid']/value");
		assertEquals("number of recent post", 2, nodeList.getLength());
		assertEquals("post id", postId2, nodeList.item(0).getTextContent());
		assertEquals("post id", postId1, nodeList.item(1).getTextContent());

		LOGGER.debug("edit blog entry");
		method = new PostMethod(apiUrl);
		content = METAWEBLOG_TEMPLATE_EDITPOST.replace("${entryId}", postId2)
				.replace("${userId}", user.getUserName())
				.replace("${password}", user.getPassword())
				.replace("${title}", "new title")
				.replace("${description}", "new description");
		method.setRequestEntity(new StringRequestEntity(content, "text/xml",
				"utf-8"));
		client.executeMethod(method);
		assertEquals("response code", 200, method.getStatusCode());
		doc = dbf.newDocumentBuilder().parse(method.getResponseBodyAsStream());
		n = XPathAPI.selectSingleNode(doc.getDocumentElement(),
				"params/param/value/boolean");
		assertNotNull(n);
		assertEquals("success code", "1", n.getTextContent());

		LOGGER.debug("delete blog entry");
		method = new PostMethod(apiUrl);
		content = METAWEBLOG_TEMPLATE_DELETEPOST.replace("${entryId}", postId1)
				.replace("${userId}", user.getUserName())
				.replace("${password}", user.getPassword());
		method.setRequestEntity(new StringRequestEntity(content, "text/xml",
				"utf-8"));
		client.executeMethod(method);
		assertEquals("response code", 200, method.getStatusCode());
		doc = dbf.newDocumentBuilder().parse(method.getResponseBodyAsStream());
		n = XPathAPI.selectSingleNode(doc.getDocumentElement(),
				"params/param/value/boolean");
		assertNotNull(n);
		assertEquals("success code", "1", n.getTextContent());
	}
	
	@Test
	public void getEntryRecommendations() {
		super.getEntryRecommendations();
	}

	@Test
	public void verifyBlogPageReturn() {
		super.verifyBlogPageReturn();
	}
	
	@Test
	public void testHitCounts() {
		super.testHitCounts();
	}
	
	@Test
	public void addBlogEntryComments() {
		super.addBlogEntryComments();
	}
	
	@Test
	public void blogsEntryModeration() throws FileNotFoundException,
			IOException {
		super.blogsEntryModeration();
	}
	
	@Test
	public void getSubscriptionAllBlogsPage() throws Exception {
		super.getSubscriptionAllBlogsPage();
	}
	
	@AfterMethod
	public static void tearDown() {
		service.tearDown();
		// otherUserService.tearDown();
	}

}