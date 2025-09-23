package com.ibm.conn.auto.tests.commonui.regression;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseWiki;
import com.ibm.conn.auto.appobjects.base.BaseWikiPage;
import com.ibm.conn.auto.appobjects.base.BaseWikiPage.PageType;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.WikisUI;

public class Wiki extends SetUpMethods2{

	private static Logger log = LoggerFactory.getLogger(Wiki.class);
	private WikisUI ui;
	private TestConfigCustom cfg;
	
	@BeforeClass(alwaysRun=true)
	public void SetUpClass() {
		
		cfg = TestConfigCustom.getInstance();
		ui = WikisUI.getGui(cfg.getProductName(), driver);
		
	}
	
	
	/**
	* createWikiPage()
	* <ul>
	* <li><B>Info: </B>Create a public wiki and add a child wiki page</li>
	* <li><B>Step: </B>Create a new public wiki</li>
	* <li><B>Verify: </B>Verify public wiki is created successfully</li>
	* <li><B>Step: </B>Create a child wiki page
	* <li><B>Verify: </B>Verify child wiki page is created successfully</li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B568243C8AE5645C85257F38005A7B2D">TTT - Common UI - Cross Application Regression/Wiki</a></li>
	*</ul>
	*Note: On Prem only
	*/
	@Test(groups = {"regression"})
	public void createWikiPage() throws Exception {
		
		String testName = ui.startTest();

		//Allocate user
		User testUser = cfg.getUserAllocator().getUser();
		
		//Create a wiki base state object
		BaseWiki wiki = new BaseWiki.Builder(testName + Helper.genDateBasedRand())
									.tags("tag" + Helper.genDateBasedRand())
									.description("Description for test " + testName)
									.build();
	
		//Create a wiki page base state object
		BaseWikiPage wikiPage = new BaseWikiPage.Builder(testName + Helper.genDateBasedRand(), PageType.Child)
												.tags("tag1, tag2")
												.description("this is a test description for creating a child wiki page")
												.build();
		
		//Load the component and login
		log.info("INFO: Load Wiki and login");
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.login(testUser);

		//Create a public wiki
		log.info("INFO: Create a public wiki");
		wiki.create(ui);

		//Verify public wiki is created
		log.info("INFO: Verify public wiki is created");
		Assert.assertTrue(ui.fluentWaitTextPresent(wiki.getName()),
				  "ERROR: The public wiki can not be found");
		
		//Create a child wiki page
		log.info("INFO: Create a child wiki page");
		wikiPage.create(ui);
		
		//Verify that the wiki page is created successfully
		log.info("Verify that the wiki page is created successfully");
		Assert.assertTrue(ui.fluentWaitTextPresent(wikiPage.getName()),
						  "ERROR: Wiki page can not be found");

		//Clean Up: Delete the wiki
		log.info("INFO: Delete the wiki");
		wiki.delete(ui, testUser);
		
		//Logout of Wiki
		ui.endTest();
	}

}
