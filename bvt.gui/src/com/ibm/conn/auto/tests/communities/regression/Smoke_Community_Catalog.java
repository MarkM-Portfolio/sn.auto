package com.ibm.conn.auto.tests.communities.regression;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.CommunitiesUI;

public class Smoke_Community_Catalog extends SetUpMethods2{

	private static Logger log = LoggerFactory.getLogger(Smoke_Community_Catalog.class);
	private CommunitiesUI ui;
	private TestConfigCustom cfg;	
	private User testUser;
	String ownerViewURL = "https://apps.collabservdaily.swg.usma.ibm.com/communities/service/html/ownedcommunities";
	String memberViewURL = "https://apps.collabservdaily.swg.usma.ibm.com/communities/service/html/mycommunities";
	String followingViewURL = "https://apps.collabservdaily.swg.usma.ibm.com/communities/service/html/followedcommunities";
	String invitedViewURL = "https://apps.collabservdaily.swg.usma.ibm.com/communities/service/html/communityinvites";
	String publicViewURL = "https://apps.collabservdaily.swg.usma.ibm.com/communities/service/html/allcommunities";
	String trashViewURL = "https://apps.collabservdaily.swg.usma.ibm.com/communities/service/html/trashedcommunities";
		
	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {
		
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = CommunitiesUI.getGui(cfg.getProductName(), driver);

	}	
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() 
	{
		//Load Users
		cfg = TestConfigCustom.getInstance();

		testUser = cfg.getUserAllocator().getUser();
		log.info("INFO: Using test user: " + testUser.getDisplayName());
		
		}
	
	/**
	*<ul>
	*<li><B>Step: </B>Select from the Mega Menu: I'm an Owner and verify that the view is loaded</li>
	*<li><B>Step: </B>Select from the Mega Menu: I'm a Member and verify that the view is loaded</li>
	*<li><B>Step: </B>Select from the Mega Menu: I'm Following and verify that the view is loaded</li>
	*<li><B>Step: </B>Select from the Mega Menu: I'm Invited and verify that the view is loaded</li>
	*</ul>
	*@throws Exception
	*/
	@Test(groups = {"smoke"})
	public void smokeCommunityCatalogCheckViewsFromMegaMenu() throws Exception {
		String communitiesMegaMenu = "css=a#communitiesMenu_btn.bss_banner_menu_btn";
		String selectView = "css=div table tbody tr:contains";
		//Start of test
		ui.startTest();
		
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		ui.waitForPageLoaded(driver);

		//Now load from the mega menu - I'm an Owner view and verify that the view is loaded
		ui.clickLink(communitiesMegaMenu);
		ui.clickLink(selectView+"(I'm an Owner)");
		String actualURLForView = driver.getCurrentUrl();
		Assert.assertEquals(actualURLForView, ownerViewURL);
		
		//Now load from the mega menu - I'm a Member view and verify that the view is loaded
		ui.clickLink(communitiesMegaMenu);
		ui.clickLink("css=div table tbody tr:contains(I'm a Member)");
		actualURLForView = driver.getCurrentUrl();
		Assert.assertEquals(actualURLForView, memberViewURL);
		
		//Now load from the mega menu - I'm Following view and verify that the view is loaded
		ui.clickLink(communitiesMegaMenu);
		ui.clickLink("css=div table tbody tr:contains(I'm Following)");
		actualURLForView = driver.getCurrentUrl();
		Assert.assertEquals(actualURLForView, followingViewURL);
		
		//Now load from the mega menu - I'm Invited view and verify that the view is loaded
		ui.clickLink(communitiesMegaMenu);
		ui.clickLink("css=div table tbody tr:contains(I'm Invited)");
		actualURLForView = driver.getCurrentUrl();
		Assert.assertEquals(actualURLForView, invitedViewURL);
		
		//End of test
		ui.endTest();
	}	
	
	/**
	*<ul>
	*<li><B>Step: </B>Select from the leftNav Menu: I'm an Owner and verify that the view is loaded</li>
	*<li><B>Step: </B>Select from the leftNav Menu: I'm a Member and verify that the view is loaded</li>
	*<li><B>Step: </B>Select from the leftNav Menu: I'm Following and verify that the view is loaded</li>
	*<li><B>Step: </B>Select from the leftNav Menu: I'm Invited and verify that the view is loaded</li>
	*<li><B>Step: </B>Select from the leftNav Menu: public communities and verify that the view is loaded</li>
	*</ul>
	*@throws Exception
	*/
	@Test(groups = {"smoke"})
	public void smokeCommunityCatalogCheckViewsFromLeftNav() throws Exception {
		String selectView = "css=div ul li a:contains";
		
		//Start of test
		ui.startTest();
		
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		ui.waitForPageLoaded(driver);
		
		//Now load from the leftnav menu - I'm an Owner view and verify that the view is loaded
		ui.clickLink(selectView+"("+Data.getData().viewOwner+")");
		String actualURLForView = driver.getCurrentUrl();
		Assert.assertEquals(actualURLForView, ownerViewURL);
		
		//Now load from the leftnav menu - I'm a Member view and verify that the view is loaded
		ui.clickLink(selectView+"("+Data.getData().viewMember+")");
		actualURLForView = driver.getCurrentUrl();
		Assert.assertEquals(actualURLForView, memberViewURL);
		
		//Now load from the leftnav menu - I'm Following view and verify that the view is loaded
		ui.clickLink(selectView+"("+Data.getData().viewFollowing+")");
		actualURLForView = driver.getCurrentUrl();
		Assert.assertEquals(actualURLForView, followingViewURL);
		
		//Now load from the leftnav menu - I'm Invited view and verify that the view is loaded
		ui.clickLink(selectView+"("+Data.getData().viewInvited+")");
		actualURLForView = driver.getCurrentUrl();
		Assert.assertEquals(actualURLForView, invitedViewURL);
		
		//Now load from the leftnav menu - Public view and verify that the view is loaded
		ui.clickLink(Data.getData().viewPublic);
		actualURLForView = driver.getCurrentUrl();
		Assert.assertEquals(actualURLForView, publicViewURL);
		
		//Now load from the leftnav menu - Trash view and verify that the view is loaded
		ui.clickLink(selectView+"("+Data.getData().viewTrash+")");
		actualURLForView = driver.getCurrentUrl();
		Assert.assertEquals(actualURLForView, trashViewURL);
		
		//End of test
		ui.endTest();
	}
	
}
