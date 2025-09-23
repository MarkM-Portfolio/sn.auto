package com.ibm.conn.auto.tests.communities;

import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.data.Data.Side;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.CommunitiesUI;

public class Smoke_RO_MediaGallery extends SetUpMethods2 {
	
	private static Logger log = LoggerFactory.getLogger(Smoke_RO_MediaGallery.class);

	private CommunitiesUI ui;
	private TestConfigCustom cfg;
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() {
		cfg = TestConfigCustom.getInstance();
		ui = CommunitiesUI.getGui(cfg.getProductName(), driver);
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B>Media Gallery Files Uploaded</li>
	 *<li><B>Step:</B>Create a community</li>
	 *<li><B>Step:</B>Click Community Actions > Add Apps</li>
	 *<li><B>Step:</B> Click Media Gallery in the right nav pane</li>
	 *<li><B>Step:</B>Set up media gallery</li>
	 *<li><B>Step:</B>Under files click Add Files</li>
	 *<li><B>Step:</B>Select the file and click Share File</li>
	 *<li><B>Step:</B>Click Gallery and select a file</li>
	 *<li><B>Verify:</B>File name is present</li>
	 *<li><B>Verify:</B>File field present</li>
	 *</ul>
	 */
	@Test(groups = {"smoke"})
	public void testUILoad() throws Exception{
		User testUser = cfg.getUserAllocator().getUser();
		
		ui.startTest();
		
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		Side side = ui.replaceProductionCookies();
		ui.login(testUser);
		ui.validateSelectedNode(APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL()), side);
		
		boolean communityExists = ui.communityExist(Data.getData().productionCommunityName);
		if(!communityExists) 
			Assert.fail("Prerequisite community '" + Data.getData().productionCommunityName + "' does not exits.");
		
		ui.openCommunity(Data.getData().productionCommunityName);
		
		ui.gotoMediaGallery();
		
		ui.fluentWaitPresent(CommunitiesUIConstants.UploadPhoto);
		
		log.info("INFO: Goto Upload a photo");
		ui.gotoUploadPhoto();
		
		ui.fluentWaitTextPresent("File name");
		
		//Check File path Field
		ui.fluentWaitPresent(CommunitiesUIConstants.MediaGalleryFileUploadFilePath);
		
		ui.endTest();
		
	}

}
