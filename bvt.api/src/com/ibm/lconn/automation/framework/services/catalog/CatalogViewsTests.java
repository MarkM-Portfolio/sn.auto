package com.ibm.lconn.automation.framework.services.catalog;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.services.catalog.CatalogService;
import com.ibm.lconn.automation.framework.services.catalog.CatalogView;
import com.ibm.lconn.automation.framework.services.catalog.CatalogViewRequest;
import com.ibm.lconn.automation.framework.services.catalog.CatalogViewRequest.Location;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Component;
import com.ibm.lconn.automation.framework.services.common.UserPerspective;
import com.ibm.lconn.automation.framework.services.common.URLConstants;

public class CatalogViewsTests {

	private static UserPerspective internalUser = null;

	private static CatalogService internalUserService;

	private static UserPerspective externalUser = null;

	private static CatalogService externalUserService;

	private static boolean useSSL = true;

	protected final static Logger LOGGER = LoggerFactory
			.getLogger(CatalogViewsTests.class.getName());

	@BeforeClass
	public static void setUp() throws Exception {
//		following needed to run catalog test locally, point to server you wish to run tests on
//		URLConstants.setServerURL("https://lcauto186.swg.usma.ibm.com");
		externalUser = new UserPerspective(StringConstants.EXTERNAL_USER,
				Component.COMMUNITIES.toString(), useSSL);
		externalUserService = externalUser.getCatalogService();

		internalUser = new UserPerspective(StringConstants.CURRENT_USER,
				Component.COMMUNITIES.toString(), useSSL);
		internalUserService = internalUser.getCatalogService();

	}

	/**
	 * Test catalog views internal user
	 */
	@Test
	public void testGetCommunitiesCatalogViewsInternaUser() {
		LOGGER.debug("Beginning Test: catalog views internal user");
		CatalogViewRequest catalogViewRequest = new CatalogViewRequest();
		ArrayList<CatalogView> results = internalUserService
				.getCommunitiesCatalogViews(catalogViewRequest);
		Map<String, String> expectedViews = new HashMap<String, String>();
		expectedViews.put(CatalogService.CATALOG_VIEW_KEY_OWN,
				CatalogService.CATALOG_VIEW_PATH_POSTFIX_OWN);
		expectedViews.put(CatalogService.CATALOG_VIEW_KEY_MY,
				CatalogService.CATALOG_VIEW_PATH_POSTFIX_MY);
		expectedViews.put(CatalogService.CATALOG_VIEW_KEY_FOLLOW,
				CatalogService.CATALOG_VIEW_PATH_POSTFIX_FOLLOW);
		expectedViews.put(CatalogService.CATALOG_VIEW_KEY_INVITE,
				CatalogService.CATALOG_VIEW_PATH_POSTFIX_INVITE);
		expectedViews.put(CatalogService.CATALOG_VIEW_KEY_PUBLIC,
				CatalogService.CATALOG_VIEW_PATH_POSTFIX_PUBLIC);
		expectedViews.put(CatalogService.CATALOG_VIEW_KEY_TRASH,
				CatalogService.CATALOG_VIEW_PATH_POSTFIX_TRASH);
		expectedViews.put(CatalogService.CATALOG_VIEW_KEY_CREATE,
				CatalogService.CATALOG_VIEW_PATH_POSTFIX_CREATE);
		expectedViews.put(CatalogService.CATALOG_VIEW_KEY_ALLMY,
				CatalogService.CATALOG_VIEW_PATH_POSTFIX_ALLMY);

		if (results != null) {
			LOGGER.debug("Test Successful: 200 OK received");
			assertEquals(expectedViews.size(), results.size());

			int orderNum = 1;
			for (CatalogView catalogView : results) {
				String viewKey = catalogView.getKey();
				assertNotNull(viewKey);
				assertEquals(orderNum++, catalogView.getOrder());
				assertNotNull(catalogView.getPath());
				assertNotNull(catalogView.getTitle());
				assertNotNull(catalogView.getDescription());

				String viewPath = expectedViews.remove(viewKey);
				assertTrue(catalogView.getPath().endsWith(viewPath));
			}

			// make sure that all the expected view were received
			assertTrue(expectedViews.isEmpty());
		} else {
			LOGGER.debug("Test Failed: community catalog tag service issue");
			assertTrue(false);
		}
	}

	/**
	 * Test catalog views internal user + location parameter set to value
	 * "banner"
	 */
	@Test
	public void testGetCommunitiesCatalogViewsInternaUserLocationBanner() {
		LOGGER.debug("Beginning Test: catalog views internal user");
		CatalogViewRequest catalogViewRequest = new CatalogViewRequest();
		catalogViewRequest.setLocation(Location.banner);
		ArrayList<CatalogView> results = internalUserService
				.getCommunitiesCatalogViews(catalogViewRequest);
		Map<String, String> expectedViews = new HashMap<String, String>();
		expectedViews.put(CatalogService.CATALOG_VIEW_KEY_OWN,
				CatalogService.CATALOG_VIEW_PATH_POSTFIX_OWN);
		expectedViews.put(CatalogService.CATALOG_VIEW_KEY_MY,
				CatalogService.CATALOG_VIEW_PATH_POSTFIX_MY);
		expectedViews.put(CatalogService.CATALOG_VIEW_KEY_FOLLOW,
				CatalogService.CATALOG_VIEW_PATH_POSTFIX_FOLLOW);
		expectedViews.put(CatalogService.CATALOG_VIEW_KEY_INVITE,
				CatalogService.CATALOG_VIEW_PATH_POSTFIX_INVITE);
		expectedViews.put(CatalogService.CATALOG_VIEW_KEY_PUBLIC,
				CatalogService.CATALOG_VIEW_PATH_POSTFIX_PUBLIC);
		expectedViews.put(CatalogService.CATALOG_VIEW_KEY_CREATE,
				CatalogService.CATALOG_VIEW_PATH_POSTFIX_CREATE);
		expectedViews.put(CatalogService.CATALOG_VIEW_KEY_ALLMY,
				CatalogService.CATALOG_VIEW_PATH_POSTFIX_ALLMY);

		if (results != null) {
			LOGGER.debug("Test Successful: 200 OK received");
			assertEquals(expectedViews.size(), results.size());

			int orderNum = 1;
			for (CatalogView catalogView : results) {
				String viewKey = catalogView.getKey();
				assertNotNull(viewKey);
				assertEquals(orderNum++, catalogView.getOrder());
				assertNotNull(catalogView.getPath());
				assertNotNull(catalogView.getTitle());
				assertNotNull(catalogView.getDescription());

				String viewPath = expectedViews.remove(viewKey);
				assertTrue(catalogView.getPath().endsWith(viewPath));
			}

			// make sure that all the expected view were received
			assertTrue(expectedViews.isEmpty());
		} else {
			LOGGER.debug("Test Failed: community catalog tag service issue");
			assertTrue(false);
		}
	}

	/**
	 * Test catalog views internal user + location parameter set to value "menu"
	 */
	@Test
	public void testGetCommunitiesCatalogViewsInternaUserLocationMenu() {
		LOGGER.debug("Beginning Test: catalog views internal user");
		CatalogViewRequest catalogViewRequest = new CatalogViewRequest();
		catalogViewRequest.setLocation(Location.menu);
		ArrayList<CatalogView> results = internalUserService
				.getCommunitiesCatalogViews(catalogViewRequest);
		Map<String, String> expectedViews = new HashMap<String, String>();
		expectedViews.put(CatalogService.CATALOG_VIEW_KEY_OWN,
				CatalogService.CATALOG_VIEW_PATH_POSTFIX_OWN);
		expectedViews.put(CatalogService.CATALOG_VIEW_KEY_MY,
				CatalogService.CATALOG_VIEW_PATH_POSTFIX_MY);
		expectedViews.put(CatalogService.CATALOG_VIEW_KEY_FOLLOW,
				CatalogService.CATALOG_VIEW_PATH_POSTFIX_FOLLOW);
		expectedViews.put(CatalogService.CATALOG_VIEW_KEY_INVITE,
				CatalogService.CATALOG_VIEW_PATH_POSTFIX_INVITE);
		expectedViews.put(CatalogService.CATALOG_VIEW_KEY_PUBLIC,
				CatalogService.CATALOG_VIEW_PATH_POSTFIX_PUBLIC);
		expectedViews.put(CatalogService.CATALOG_VIEW_KEY_TRASH,
				CatalogService.CATALOG_VIEW_PATH_POSTFIX_TRASH);
		expectedViews.put(CatalogService.CATALOG_VIEW_KEY_CREATE,
				CatalogService.CATALOG_VIEW_PATH_POSTFIX_CREATE);
		expectedViews.put(CatalogService.CATALOG_VIEW_KEY_ALLMY,
				CatalogService.CATALOG_VIEW_PATH_POSTFIX_ALLMY);

		if (results != null) {
			LOGGER.debug("Test Successful: 200 OK received");
			assertEquals(expectedViews.size(), results.size());

			int orderNum = 1;
			for (CatalogView catalogView : results) {
				String viewKey = catalogView.getKey();
				assertNotNull(viewKey);
				assertEquals(orderNum++, catalogView.getOrder());
				assertNotNull(catalogView.getPath());
				assertNotNull(catalogView.getTitle());
				assertNotNull(catalogView.getDescription());

				String viewPath = expectedViews.remove(viewKey);
				assertTrue(catalogView.getPath().endsWith(viewPath));
			}

			// make sure that all the expected view were received
			assertTrue(expectedViews.isEmpty());
		} else {
			LOGGER.debug("Test Failed: community catalog tag service issue");
			assertTrue(false);
		}
	}

	/**
	 * Test catalog views external user
	 */
	@Test
	public void testGetCommunitiesCatalogViewsExternalUser() {
		if (StringConstants.VMODEL_ENABLED) {
			LOGGER.debug("Beginning Test: catalog views external user");
			CatalogViewRequest catalogViewRequest = new CatalogViewRequest();
			ArrayList<CatalogView> results = externalUserService
					.getCommunitiesCatalogViews(catalogViewRequest);
			Map<String, String> expectedViews = new HashMap<String, String>();
			expectedViews.put(CatalogService.CATALOG_VIEW_KEY_FOLLOW,
					CatalogService.CATALOG_VIEW_PATH_POSTFIX_FOLLOW);
			expectedViews.put(CatalogService.CATALOG_VIEW_KEY_MY,
					CatalogService.CATALOG_VIEW_PATH_POSTFIX_MY);
			expectedViews.put(CatalogService.CATALOG_VIEW_KEY_INVITE,
					CatalogService.CATALOG_VIEW_PATH_POSTFIX_INVITE);

			if (results != null) {
				LOGGER.debug("Test Successful: 200 OK received");
				assertEquals(expectedViews.size(), results.size());

				int orderNum = 1;

				for (CatalogView catalogView : results) {
					String viewKey = catalogView.getKey();
					assertNotNull(viewKey);
					assertNotNull(catalogView.getOrder());
					assertEquals(orderNum++, catalogView.getOrder());
					assertNotNull(catalogView.getPath());
					assertNotNull(catalogView.getTitle());
					assertNotNull(catalogView.getDescription());

					String viewPath = expectedViews.remove(viewKey);
					assertTrue(catalogView.getPath().endsWith(viewPath));
				}

				// make sure that all the expected view were received
				assertTrue(expectedViews.isEmpty());
			} else {
				LOGGER.debug("Test Failed: community catalog tag service issue");
				assertTrue(false);
			}
		}
	}

}
