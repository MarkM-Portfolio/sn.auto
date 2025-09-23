package com.ibm.conn.auto.tests.commonui.regression;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseBlog;
import com.ibm.conn.auto.appobjects.base.BaseBlogPost;
import com.ibm.conn.auto.appobjects.base.BaseBlog.Theme;
import com.ibm.conn.auto.appobjects.base.BaseBlog.Time_Zone;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.BlogsUI;

public class Blogs extends SetUpMethods2{

	private static Logger log = LoggerFactory.getLogger(Blogs.class);
	private TestConfigCustom cfg;
	private BlogsUI ui;
	private User testUser;
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() {
		
		cfg = TestConfigCustom.getInstance();
		ui = BlogsUI.getGui(cfg.getProductName(), driver);
		testUser = cfg.getUserAllocator().getUser();	
	
	}

	
	/**
	* addBlogEntry() 
	*<ul>
	*<li><B>Info: </B>Creates a blog and adds an entry to the blog</li>
	*<li><B>Step: </B>Create a blog</li>
	*<li><B>Step: </B>Go to My Blogs</li>
	*<li><B>Step: </B>Click on the "Add Entry" link</li> 
	*<li><B>Step: </B>Add an entry</li>
	*<li><B>Step: </B>Go back to My Blogs</li>
	*<li><B>Step: </B>Open the blog</li>
	*<li><B>Verify: </B>Blog entry displayed in the view</li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B568243C8AE5645C85257F38005A7B2D">TTT - Common UI - Cross Application Regression/Blogs</a></li>
	*</ul>
	*Note: On Prem only
	*/
	@Test (groups = {"regression"} )
	public void addBlogEntry() throws Exception{

		String testName = ui.startTest();
		String rand = Helper.genDateBasedRand();
		
		//Create a blog base state object
		BaseBlog blog = new BaseBlog.Builder(testName + rand, testName + rand)
											.tags("blogtag")
											.description("This is description for " + testName)
											.timeZone(Time_Zone.Europe_London)
											.theme(Theme.Blog_with_Bookmarks)
											.build();	
		
		//Create a blog entry base state object
		BaseBlogPost blogPost = new BaseBlogPost.Builder("My blogEntry" + rand)
												.blogParent(blog)
												.tags(Data.getData().commonAddress + rand)
												.content("This is description for blog entry")
												.build();

		
		//Load component and login
		log.info("INFO: Load Blogs component and login");
		ui.loadComponent(Data.getData().ComponentBlogs);
		ui.login(testUser);

		//Create a blog
		log.info("INFO: Create a new blog");
		blog.create(ui);
						
		//Click on the Add Entry link 
		log.info("INFO: Click on the Add Entry link");
		ui.clickLinkWait(BlogsUI.newEntryForSpecificBlog(blog));
		
		//Add an Entry
		log.info("INFO: Add an Entry");
		blogPost.create(ui);
		
		//Verify blog entry is displayed in the view
		log.info("INFO: Verify blog entry is displayed in the view");
		Assert.assertTrue(driver.isTextPresent(blogPost.getTitle()), 
						  "ERROR: entry with name [" + blogPost.getTitle() + "] was not found in blog [" + blog.getName() + "] where it was posted");

		//Clean Up: Delete the blog
		log.info("INFO: Delete the blog");
		blog.delete(ui);
		
		ui.endTest();	
	}

}
