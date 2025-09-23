package com.ibm.lconn.automation.framework.services.wikis.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Component;
import com.ibm.lconn.automation.framework.services.common.UsersEnvironment;
import com.ibm.lconn.automation.framework.services.wikis.WikisTestBase;

/**
 * JUnit Tests via Connections API for Wikis Service
 * 
 * @author James Cunningham - jamcunni@ie.ibm.com
 */
public class WikisPopulate extends WikisTestBase {

	protected final static Logger LOGGER = LoggerFactory
			.getLogger(WikisPopulate.class.getName());

	@BeforeClass
	public static void setUp() throws Exception {
		LOGGER.debug("Start Initializing Wikis Data Population Test");

		UsersEnvironment userEnv = new UsersEnvironment();
		user = userEnv.getLoginUserEnvironment(StringConstants.CURRENT_USER,
				Component.WIKIS.toString());
		service = user.getWikisService();
		imUser = user;

		otherUser = userEnv.getLoginUserEnvironment(
				StringConstants.RANDOM1_USER, Component.FILES.toString());

		LOGGER.debug("Finished Initializing Wikis Data Population Test");

	}

	@Test
	public void updatePage() throws FileNotFoundException, IOException {
		super.updatePage();
	}

	@Test
	// tests parameters sortby, sortorder, and since
	// does not test search fully because cache does not update correctly right
	// now and if it did the test would take an extra 15minutes
	public void wikiPagesPara() throws InterruptedException {
		super.wikiPagesPara();
	}

	@Test
	public void deleteMyWikis() throws Exception {
		super.deleteMyWikis();
	}

	@Test
	public void retrieveWikiWithLabel() throws UnsupportedEncodingException {
		super.retrieveWikiWithLabel();
	}

	@Test
	public void testWikiComment() throws Exception {
		super.testWikiComment();
	}
	
	@Test
	public void deleteTrashWikiPage() throws Exception {
		super.deleteTrashWikiPage();
	}
	
	@Test
	public void restoreWikiPage() throws Exception {
		super.restoreWikiPage();
	}
	
	@Test
	public void getPagesEdited() {
		super.getPagesEdited();
	}
	
	@Test
	public void getWikiPageResourcesInDepth() {
		super.getWikiPageResourcesInDepth();
	}
	
	@Test
	public void getWikiMembersOfRole() throws FileNotFoundException,
			IOException, InterruptedException {
		super.getWikiMembersOfRole();
	}
	
	@Test
	public void wikiRecyclePagesPara() {
		super.wikiRecyclePagesPara();
	}
	
	@Test
	public void visitorModelTests() throws FileNotFoundException, IOException {
		super.visitorModelTests();
	}
	
	@Test
	public void getWikiMembersInDepth() throws FileNotFoundException,
			IOException {
		super.getWikiMembersInDepth();
	}

	@AfterClass
	public static void tearDown() {
		service.tearDown();
	}
}
