package com.ibm.conn.auto.tests.bss;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.OrgConfig;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.bss.BssAPIHelper;
import com.ibm.conn.auto.webui.ActivitiesUI;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.FilesUI;
import com.ibm.conn.auto.webui.HomepageUI;

public class BVT_Level_2_BSS extends SetUpMethods2 {

	protected static Logger log = LoggerFactory.getLogger(BVT_Level_2_BSS.class);
	private TestConfigCustom cfg;

	private User adminUser;
	String server;
	Map<String, String> userIds;
	String csguser;
	String csgpassword;
	String testUsername;
	private BssAPIHelper bssHelper = new BssAPIHelper();
	private CommunitiesUI cui;
	private HomepageUI hui;
	private FilesUI fui;
	private ActivitiesUI aui;
	private String user_email_base;
	private String orgID="";
	private String subID="";
	private OrgConfig orgConfig;

	@BeforeClass(alwaysRun = true)
	public void setUp() throws Exception {

		cfg = TestConfigCustom.getInstance();
		adminUser = cfg.getUserAllocator().getAdminUser();
		getIDs();
		runProvisioning();
		cui = CommunitiesUI.getGui(cfg.getProductName(), driver);
		hui = HomepageUI.getGui(cfg.getProductName(), driver);
		fui = FilesUI.getGui(cfg.getProductName(), driver);
		aui = ActivitiesUI.getGui(cfg.getProductName(), driver);
	}

	@AfterClass(alwaysRun = true)
	public void afterClass() {
		for (Entry<String, String> e : userIds.entrySet()) {
			String id = e.getValue();
			String email = e.getKey();
			log.info("INFO: Attempting to delete user " + email);
			try {
				bssHelper.deleteSubscriber(id, server, csguser, csgpassword);
			} catch (Exception e1) {
				log.error(e1.getMessage());
			}
		}
	}

	public void runProvisioning() throws Exception {

		//Ensure the correct ID's are returned successfully
		Assert.assertTrue(checkIDs(),
				"ERROR: Org and Sub ID were returned empty. Please check ORG.XML");
		
		server = cfg.getServerURL();
		userIds = new HashMap<String, String>();
		String randOrgNum = Helper.genDateBasedRand();
		user_email_base = "user" + randOrgNum + "_";
		// String numParts = "10";
		csguser = adminUser.getEmail();
		csgpassword = adminUser.getPassword();
		String firstname = "DeleteMe";
		String lastname = "ProvisTest";
		Integer numUsers = 4;

		for (int i = 0; i < numUsers; i++) {
			String userID = bssHelper.addUser(orgID, user_email_base + i
					+ "@bluebox.lotus.com", server, csguser, csgpassword,
					firstname, lastname + i);
			log.info("INFO: User id of " + i + " user: " + userID);
			userIds.put(user_email_base + i + "@bluebox.lotus.com", userID);
			log.info("INFO Added " + user_email_base + i
					+ "@bluebox.lotus.com successfully." + "\n");

		}
		// Thread.sleep(15000);
		for (Entry<String, String> e : userIds.entrySet()) {
			String id = e.getValue();
            log.info("INFO: Value of id: " + id);
			log.info("INFO: Adding sub for user id : " + id
					+ " , and subscription id " + subID);
			bssHelper.addSubscriberSub(id, subID, server, csguser, csgpassword);

		}

		for (int i = 0; i < numUsers; i++) {
			log.info("INFO: Attempting to activate " + user_email_base + i
					+ "@bluebox.lotus.com");
			bssHelper.activateAccount(user_email_base + i
					+ "@bluebox.lotus.com");
		}
	
	}

	@Test
	public void testLoginCommunities() throws Exception {
		cui.startTest();
		String email = user_email_base + 0 + "@bluebox.lotus.com";
		log.info("INFO: Verify log in to Communities as " + email);
		cui.loadComponent(Data.getData().ComponentCommunities);
		cui.login(email, "passw0rd");
		Assert.assertTrue(driver.isTextPresent("Communities"),
				"Could not find the 'New to Communities' text on page.");
		cui.endTest();
	}

	@Test
	public void testLoginHomepage() throws Exception {
		hui.startTest();
		String email = user_email_base + 1 + "@bluebox.lotus.com";
		log.info("INFO: Verify log in to Homepage as " + email);
		hui.loadComponent(Data.getData().ComponentHomepage);
		hui.login(email, "passw0rd");
		Assert.assertTrue(driver.isTextPresent("I'm Following"),
				"Could not find the 'I'm Following' text on page.");
		hui.endTest();
	}

	@Test
	public void testLoginFiles() throws Exception {
		fui.startTest();
		String email = user_email_base + 2 + "@bluebox.lotus.com";
		log.info("INFO: Verify log in to Files as " + email);
		fui.loadComponent(Data.getData().ComponentFiles);
		fui.login(email, "passw0rd");
		Assert.assertTrue(driver.isTextPresent("My Drive"),
				"Could not find the 'Files that you own' text on page.");
		fui.endTest();
	}

	@Test
	public void testLoginActivities() throws Exception {
		aui.startTest();
		String email = user_email_base + 3 + "@bluebox.lotus.com";
		log.info("INFO: Verify log in to Activities as " + email);
		aui.loadComponent(Data.getData().ComponentActivities);
		aui.login(email, "passw0rd");
		Assert.assertTrue(driver.isTextPresent("My Activities"),
				"Could not find the 'New to Activities' text on page.");
		aui.endTest();
	}
	
	private Boolean checkIDs(){
		if (orgID.equals("") || subID.equals("")){
			return false;
		}
		return true;
	}
	
	private void getIDs(){
		Iterator<OrgConfig> orgIterator = orgs.iterator();
		while (orgIterator.hasNext()){
			orgConfig = orgIterator.next();
			 if (orgConfig.getGrpID().equals(adminUser.getLastName())){
				 orgID= orgConfig.getOrgID();
				 subID= orgConfig.getSubID();
			 }
		}
	}

}
