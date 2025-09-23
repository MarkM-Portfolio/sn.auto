package com.ibm.conn.auto.tests.commonui.regression;

import static org.testng.Assert.assertTrue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseDogear;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.DogearUI;
import com.ibm.conn.auto.webui.DogearUI.SelectBookmarkViews;

public class Bookmarks extends SetUpMethods2 {
	
	private static Logger log = LoggerFactory.getLogger(Bookmarks.class);
	private DogearUI ui;
	private TestConfigCustom cfg;
	private User testUser1;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		
		cfg = TestConfigCustom.getInstance();
		ui = DogearUI.getGui(cfg.getProductName(), driver);
		
		testUser1 = cfg.getUserAllocator().getUser();
			
	}
	
	
	/**
	* createBookmark()
	*<ul>
	*<li><B>Info:</B>Create a public bookmark and view it from Public Bookmarks</li>
	*<li><B>Step:</B>Create a public bookmark for Google from My Bookmarks view</li>
	*<li><B>Step:</B>Switch to Public Bookmarks view</li>
	*<li><B>Verify:</B>Bookmark displayed in Public Bookmarks view</li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B568243C8AE5645C85257F38005A7B2D">TTT - Common UI - Cross Application Regression/Bookmarks</a></li>
	*</ul>
	*Note: On Prem only
	*/
	@Test(groups = {"regression"})
	public void createBookmark() throws Exception {
		
		String testName = ui.startTest() + Helper.genDateBasedRandVal();
		String url = Data.getData().commonURL;
		
		BaseDogear bookmark = new BaseDogear.Builder(testName , url)
													.tags("google_tag")
													.description("This is the description for " + testName).build();

		// Load the component and login
		log.info("INFO: Load Bookmarks component and login as " +testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentDogear);
		ui.login(testUser1);
		
		//Create public bookmark for Google with tags and description from My Bookmarks view
		log.info("INFO: Create public bookmark for google from 'My Bookmarks' view");
		ui.create(bookmark);
		
		//Switch to Public Bookmarks view 
		log.info("INFO: Switch to 'Public Bookmarks' view");
		ui.selectBookmarkView(SelectBookmarkViews.PublicBookmarks);
		
		//Reliability check
		log.info("INFO: If the bookmark is not present refresh the browser");
		if(driver.isTextNotPresent(testName))
		{
			log.info("INFO:Bookmark not detected, ...refreshing browser ");
			driver.navigate().refresh();
		}
		
		//Validate bookmark displays in Public Bookmarks view
		log.info("INFO: Validate bookmark: " +testName+ " displays in Public Bookmarks view");
		assertTrue(ui.fluentWaitTextPresent(bookmark.getTitle()), 
				   "ERROR: Bookmark: " + bookmark.getTitle() + " not found");

		//Clean Up: Delete the bookmark
		log.info("INFO: Delete the bookmark");
		ui.selectBookmarkView(SelectBookmarkViews.MyBookmarks);
		ui.delete(bookmark);
		
		ui.endTest();	
	}
}
