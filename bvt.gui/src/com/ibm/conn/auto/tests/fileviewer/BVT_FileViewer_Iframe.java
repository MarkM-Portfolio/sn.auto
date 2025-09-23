package com.ibm.conn.auto.tests.fileviewer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.Assert;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.FileViewerUI;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;

public class BVT_FileViewer_Iframe extends SetUpMethods2 {
	
	private static Logger log = LoggerFactory.getLogger(BVT_FileViewer_Iframe.class);
	private TestConfigCustom cfg;
	private FileViewerUI fvUI;
	private User testUser;
	
	@BeforeClass(alwaysRun=true)
	public void SetUpClass() {
		cfg = TestConfigCustom.getInstance();
		//Load Users
		testUser = cfg.getUserAllocator().getUser();
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		fvUI = FileViewerUI.getGui(cfg.getProductName(), driver);	
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Validate File Viewer</li>
	 *<li><B>Step:</B> Upload file to Files</li>
	 *<li><B>Step:</B> Open file in FileViewer iFrame</li>
	 *<li><B>Verify:</B> Receive message that authentication is required because we are not yet logged in</li>
	 *<li><B>Step:</B> Log in</li>
	 *<li><B>Step:</B> Open file in FileViewer iFrame</li>
	 *<li><B>Verify:</B> File viewer successfully opens the file</li>
	 *<li><B>Verify:</B> File viewer functions correctly</li>
	 *<li><B>Step:</B> Close the file viewer</li>
	 *<li><B>Verify:</B> Message that the file viewer was closed is received</li>
	 *<li><B>Step:</B> Refresh the iframe page</li>
	 *<li><B>Step:</B> Move the file to the trash</li>
	 *<li><B>Verify:</B> Message that the file viewer was closed is received</li>
	 *<li><B>Step:</B> Refresh the iframe page</li>
	 *<li><B>Verify:</B> Message that there was an error opening the file is received</li>
	 *</ul>
	 */
	@Test(groups = {"level2", "bvt"})
	public void validateFileViewer() {
		
		BaseFile baseFile = new BaseFile.Builder(Data.getData().file1)
					.extension(".jpg")
					.rename(Helper.genDateBasedRand())
					.build();
		
		fvUI.startTest();

		FileEntry fileEntry = fvUI.upload(baseFile, testConfig, testUser);
		fvUI.navigateToIframePage(fileEntry, false);
		
		Assert.assertTrue(fvUI.hasMessage("ic-fileviewer/authenticationRequired"),
				"ERROR: Should receive and authenticationRequired message because we are not logged in.");
		
		fvUI.loadComponent(Data.getData().ComponentFiles, true);
		fvUI.login(testUser);
		
		log.info("INFO: Verify login was successful");
		
		fvUI.navigateToIframePage(fileEntry, true);
		
		Assert.assertFalse(fvUI.hasMessage("ic-fileviewer/authenticationRequired"),
				"ERROR: Should not have receive and authenticationRequired message because we are now logged in.");
		Assert.assertTrue(fvUI.hasMessage("ic-fileviewer/resourcesLoaded"),
				"ERROR: Should have received a message that resources are loaded.");
		Assert.assertTrue(fvUI.hasMessage("ic-fileviewer/open"),
				"ERROR: Should have received a message that the file viewer has opened.");
		
		log.info("INFO: About to execute fvUI.testViewer(baseFile)");
		log.info("INFO: fvUI = " + fvUI);
		log.info("INFO: baseFile = " + baseFile);
		if (baseFile != null) {
			log.info("INFO: baseFile = " + baseFile.getName());
		}
		
		final String msg = fvUI.testViewer(baseFile);

		log.info("INFO: Done executing fvUI.testViewer(baseFile)");
		log.info("INFO: " + msg);
		
		fvUI.tearOff();
		Assert.assertTrue(fvUI.hasMessage("ic-fileviewer/action/tearoff"),
				"ERROR: Should have recieved a tearoff message when clicking the tear off button.");
		
		fvUI.close();
		Assert.assertTrue(fvUI.hasMessage("ic-fileviewer/close"),
				"ERROR: Should have recieved a Close message when clicking the close button.");
		
		fvUI.refreshIframePage();
		
		fvUI.moveToTrash();
		Assert.assertTrue(fvUI.hasMessage("ic-fileviewer/close"),
				"ERROR: Should have recieved a Close message when the file was moved to the trash.");
		
		fvUI.refreshIframePage();
		Assert.assertTrue(fvUI.hasMessage("ic-fileviewer/openError"),
				"ERROR: Should have recieved an openError message because the file no longer exists.");
		
		fvUI.endTest();
	}
	

}
