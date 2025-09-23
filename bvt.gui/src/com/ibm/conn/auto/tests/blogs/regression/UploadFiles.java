package com.ibm.conn.auto.tests.blogs.regression;

import java.io.File;
import java.util.HashMap;
import java.util.Random;

import com.ibm.conn.auto.webui.constants.BlogsUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseBlog;
import com.ibm.conn.auto.appobjects.base.BaseBlog.Theme;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIBlogsHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.BlogsUI;

public class UploadFiles extends SetUpMethods2{

	private static Logger log = LoggerFactory.getLogger(GeneralUI.class);
	private TestConfigCustom cfg;
	private BlogsUI ui;
	private User testAdmin;
	private APIBlogsHandler apiOwner;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		testAdmin = cfg.getUserAllocator().getAdminUser();
		String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APIBlogsHandler(serverURL, testAdmin.getAttribute(cfg.getLoginPreference()), testAdmin.getPassword());
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() {
		cfg = TestConfigCustom.getInstance();
		ui = BlogsUI.getGui(cfg.getProductName(), driver);
	}

	
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Test to see if a file appears in the server after upload</li>
	 *<li><B>Step: </B>Go to My Blogs</li>
	 *<li><B>Step: </B>Go to Blog Settings</li>
	 *<li><B>Step: </B>Click File Uploads</li>
	 *<li><B>Step: </B>Choose a random file from a map</li>
	 *<li><B>Step: </B>Upload the file</li>
	 *<li><B>Verify: </B>Verify that the page contains the file name</li>
	 *<li><B>Verify: </B>Verify that the page contains the icon</li>
	 *</ul>
	 */
// This test assumes the file system being accessed is always the local file
// system and is fundamentally incompatible with any kind of grid setup. Use
// the test addComBlogEntryWithImage in BVT_Level_2_Blogs instead.
//	@Test(groups = {"regression"})
	@Deprecated
	public void uploadFiles() throws Exception {
		
		String testName = ui.startTest();
		String rand = Helper.genDateBasedRand();
		
		BaseBlog blog = new BaseBlog.Builder(testName + rand, testName + rand)
											.tags(Data.getData().commonTag + rand)
											.description(Data.getData().commonDescription)
											.theme(Theme.Blog_with_Bookmarks)
											.build();

		log.info("INFO: Create blog using API");
		blog.createAPI(apiOwner);
		
		//GUI
		//Load the component
		ui.loadComponent(Data.getData().ComponentBlogs);
		ui.login(testAdmin);

		log.info("INFO: Select Administration link");
		ui.clickLinkWait(BlogsUIConstants.Administration);
		
		log.info("INFO: Type the allowed extensions");
		driver.getSingleElement(BlogsUIConstants.allowedExtensionsInput).clear();
		driver.getSingleElement(BlogsUIConstants.allowedExtensionsInput).type(BlogsUIConstants.allowedExtensions);

		log.info("INFO: Save changes to the blog");
		ui.clickLinkWait(BlogsUIConstants.BlogsSiteSettingsSave);
		
		//Go back to My Blogs
		log.info("INFO: Navigate to My Blogs");
		ui.clickLinkWait(BlogsUIConstants.MyBlogs);

		log.info("INFO: Select blog " + blog.getName());
		ui.clickLinkWait("link=" + blog.getName());
		
		//Go to the 
		log.info("INFO: Navigate to Manage Blogs");
		ui.clickLinkWait(BlogsUIConstants.blogsSettings);
		
		//click File Uploads
		log.info("INFO: Select file uploads");
		ui.clickLinkWait(BlogsUIConstants.BlogsFileUploadsLink);
		
		//create the map between descriptor strings and arrays of extensions
		HashMap<String, String[]> extensionsMap = new HashMap<String, String[]>();		
		extensionsMap.put("text", BlogsUIConstants.textExtensions);
		extensionsMap.put("document", BlogsUIConstants.documentExtensions);
		extensionsMap.put("data", BlogsUIConstants.dataExtensions);
		extensionsMap.put("presentation", BlogsUIConstants.presentationExtensions);
		extensionsMap.put("pdf", BlogsUIConstants.pdfExtensions);
		extensionsMap.put("flash", BlogsUIConstants.flashExtensions);
		extensionsMap.put("code", BlogsUIConstants.codeExtensions);
		extensionsMap.put("graphic", BlogsUIConstants.graphicExtensions);
		extensionsMap.put("audio", BlogsUIConstants.audioExtensions);
		extensionsMap.put("video", BlogsUIConstants.videoExtensions);

		String fileNameBeginning = "test.";
		String iconNameBeginning = "lconn-ftype16 lconn-ftype16-";	
		String expectedFileName;
		String iconName;
		
		Random r = new Random();
		
		String fileUploadDirectory = cfg.getUploadFilesDir() + "\\" + BlogsUIConstants.fileUploadDirectoryName;
		File downloadFolder = new File(fileUploadDirectory);
		if(!downloadFolder.exists())
			downloadFolder.mkdir();
		
		//for every file type
		for(String s : extensionsMap.keySet()){
			String[] extensions = extensionsMap.get(s);
			
			//choose a random element from the list
			String randomExtension = extensions[r.nextInt(extensions.length)];
			
			//determine the file name
			String fileName = cfg.getUploadFilesDir() + "\\" + BlogsUIConstants.fileUploadDirectoryName + fileNameBeginning + randomExtension;

			File f = new File(fileName);
			
			//if the file doesn't exist
			if(!f.exists()){
				try{
					f.createNewFile();			// try to create it
				}
				catch(Exception e){				// if unable to create the file...
					Assert.fail("unable to create file [" + fileName + "]");
				}
			}

			//upload the file
			log.info("INFO: Upload the file");
			driver.getSingleElement(BlogsUIConstants.BlogsFileUploadInput).typeFilePath(fileName);
			ui.clickLinkWait(BlogsUIConstants.BlogsFileUploadButton);

			
			iconName = iconNameBeginning + randomExtension;
			expectedFileName = fileNameBeginning + randomExtension;
			
			//verify that the page contains the file name
			log.info("INFO: Validate that the page contains the file name");
			Assert.assertTrue(driver.isElementPresent(BlogsUI.getFile(expectedFileName)), 
							 "ERROR: could not find file name string after uploading file [" + fileName + "]");
			
			//verify that the page contains the icon
			log.info("INFO: Validate that the page contains the icon");
			Assert.assertTrue(driver.isElementPresent(BlogsUI.getIcon(iconName)), 
							  "ERROR: icon with name[" + iconName + "] could not be found after uploading file [" + fileName + "]");
		}

		ui.endTest();
		
	}
	
}
