package com.ibm.lconn.automation.tdi.tests;

import java.util.ArrayList;

import javax.naming.directory.Attribute;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.ibm.lconn.automation.tdi.framework.database.dao.ProfileDAO;
import com.ibm.lconn.automation.tdi.framework.database.model.Profile;
import com.ibm.lconn.automation.tdi.framework.ldap.LdapService;
import com.ibm.lconn.automation.tdi.framework.tdi.Tdi;

public class AllTests extends BaseTest{
	
  @Test
  public void addUser() {
	  Tdi tdi = new Tdi(tdiSolHome);
	
	//
	//run populate script
	//
	System.out.println("Run Collect Dns...");
	tdi.runCollectDns();
	System.out.println("Run Populate...");
	String popRun1 = tdi.runPopulate();
	if(popRun1 == null) {
		Assert.fail("Populate script failed");
	}
	
	//
	//Add user to ldap
	//
	System.out.println("Add user to LDAP...");
	LdapService ls = new LdapService(ldapProperties);
	
	ArrayList<Attribute> attr = new ArrayList<Attribute>();
	attr.add(new BasicAttribute("c","us"));
	attr.add(new BasicAttribute("departmentNumber","SM"));
	attr.add(new BasicAttribute("displayname","Temp1"));
	attr.add(new BasicAttribute("description","user added for tdi test"));
	attr.add(new BasicAttribute("givenname","Temp1"));
	attr.add(new BasicAttribute("l","LM"));
	attr.add(new BasicAttribute("ou","WPLC"));
	attr.add(new BasicAttribute("sn","Temp1"));
	attr.add(new BasicAttribute("userPassword","passw0rd"));
	attr.add(new BasicAttribute("mail","utemp1@janet.iris.com"));
	attr.add(new BasicAttribute("cn","Temp1"));
	
	DirContext user = ls.addUser("utemp1", attr);
	if(user == null){
		Assert.fail("User not added to LDAP");
	}
	
	//
	//run populate script
	//
	System.out.println("Run Collect Dns...");
	tdi.runCollectDns();
	System.out.println("Run Populate...");
	String popRun2 = tdi.runPopulate();
	if(popRun2 == null) {
		Assert.fail("Populate script failed");
	}
	
	//
	//check db for new user
	//
	System.out.println("Check database for user...");
	ProfileDAO pDao = new ProfileDAO(dbProperties);
	Profile p = null;
	try {
		p = pDao.getPerson("utemp1");
	} catch (Exception e) {
		Assert.fail(e.getMessage());
	}
	
	Assert.assertTrue(p != null, "User not added to the database");
  }
  
  @Test(dependsOnMethods={"addUser"})
  public void updateUser() {
	  String userUid = "utemp1";
	  
	  System.out.println("Change givenName in LDAP...");
	  LdapService ls = new LdapService(ldapProperties);
	  ArrayList<Attribute> attbs = new ArrayList<Attribute>();
	  attbs.add(new BasicAttribute("givenname","changedName"));
	  ls.modifyUser(userUid, attbs);
	  
	  System.out.println("Run Sync...");
	  Tdi tdi = new Tdi(tdiSolHome);
	  String syncResult = tdi.runSyncAllDns();
	  if(syncResult == null) {
		  Assert.fail("Sync script failed");
	  }
	  
	  System.out.println("Getting user from database...");
	  ProfileDAO pDao = new ProfileDAO(dbProperties);
	  Profile p = pDao.getPerson(userUid);
	  
	  Assert.assertEquals(p.getGivenName(), "changedName", "Updated name is: " + p.getGivenName() + ", expected: changedName");
  }
  
  @Test(dependsOnMethods={"updateUser"})
  public void removeUser(){
	//
	//delete user from ldap
	//
	LdapService ls = new LdapService(ldapProperties);
	ls.deleteUser("utemp1");
	  
	Tdi tdi = new Tdi(tdiSolHome);
	
	System.out.println("Run Sync...");
	String popResult = tdi.runSyncAllDns();
	if(popResult == null) {
		Assert.fail("Populate script failed");
	}
	
	//
	//check db for user
	//
	System.out.println("Check database for user inactivated...");
	ProfileDAO pDao = new ProfileDAO(dbProperties);
	Profile p = null;
	try {
		p = pDao.getPerson("utemp1");
	} catch (Exception e) {
		Assert.fail(e.getMessage());
	}
	
	if(p != null){
		if(p.getState() != 1){
			Assert.fail("User not inactivated the database, state: " + p.getState());
		}
	}
	else{Assert.fail("User not found in the database");}
	
  }
  
  @Test
  public void addFiveUsers() {
	  	//
		//Add 5 users to ldap
		//
		System.out.println("Add 5 users to LDAP...");
		LdapService ls = new LdapService(ldapProperties);
		
		ArrayList<ArrayList<Attribute>> users = getFiveUsers();
		for(int i = 0; i < 5; i++){
			DirContext user = ls.addUser("utemp10" + i, users.get(i));
			if(user == null){
				Assert.fail("User utemp10" + i + " not added to LDAP");
			}
		}
		
		//
		//Run Sync
		//
		Tdi tdi = new Tdi(tdiSolHome);
		
		System.out.println("Run Sync...");
		String popResult = tdi.runSyncAllDns();
		if(popResult == null) {
			Assert.fail("Populate script failed");
		}
		
		//
		//check db for new users
		//
		System.out.println("Check database for users...");
		ProfileDAO pDao = new ProfileDAO(dbProperties);
		Profile p;
		for(int i = 0; i < 5; i++){
			p = null;
			try {
				p = pDao.getPerson("utemp10" + i);
			} catch (Exception e) {
				Assert.fail(e.getMessage());
			}
			
			Assert.assertTrue(p != null, "User utemp10" + i + " not added to the database");
		}
  }
  
  @Test(dependsOnMethods={"addFiveUsers"})
  public void deleteFiveUsers() {
	  //
	  //Delete 5 users
	  //
	  LdapService ls = new LdapService(ldapProperties);
	  for(int i = 0; i < 5; i++){
		  ls.deleteUser("utemp10"+i);
	  }
	  
	  //
	  //Sync
	  //
	  Tdi tdi = new Tdi(tdiSolHome);
	  System.out.println("Run Sync...");
	  String popResult = tdi.runSyncAllDns();
	  if(popResult == null) {
		  Assert.fail("Populate script failed");
	  }
		
	  //
	  //check db for user
	  //
	  System.out.println("Check database for user inactivated...");
	  ProfileDAO pDao = new ProfileDAO(dbProperties);
	  
	  for(int i = 0; i < 5; i++){
		  Profile p = null;
		  try {
			  p = pDao.getPerson("utemp10"+i);
		  } catch (Exception e) {
			  Assert.fail(e.getMessage());
		  }
			
		  if(p != null){
			  if(p.getState() != 1){
				  Assert.fail("User utemp"+i+" not inactivated the database, state: " + p.getState());
			  }
		  } else{Assert.fail("User utemp"+i+" not found in the database");}
	  }
  }
  
  private ArrayList<ArrayList<Attribute>> getFiveUsers() {
	  ArrayList<ArrayList<Attribute>> users = new ArrayList<ArrayList<Attribute>>();
	  
	  	ArrayList<Attribute> user1 = new ArrayList<Attribute>();
		user1.add(new BasicAttribute("c","us"));
		user1.add(new BasicAttribute("departmentNumber","SM"));
		user1.add(new BasicAttribute("displayname","Temp100"));
		user1.add(new BasicAttribute("description","user added for tdi test"));
		user1.add(new BasicAttribute("givenname","Temp100"));
		user1.add(new BasicAttribute("l","LM"));
		user1.add(new BasicAttribute("ou","WPLC"));
		user1.add(new BasicAttribute("sn","Temp100"));
		user1.add(new BasicAttribute("userPassword","passw0rd"));
		user1.add(new BasicAttribute("mail","utemp100@janet.iris.com"));
		user1.add(new BasicAttribute("cn","Temp100"));
		
		ArrayList<Attribute> user2 = new ArrayList<Attribute>();
		user2.add(new BasicAttribute("c","us"));
		user2.add(new BasicAttribute("departmentNumber","SM"));
		user2.add(new BasicAttribute("displayname","Temp101"));
		user2.add(new BasicAttribute("description","user added for tdi test"));
		user2.add(new BasicAttribute("givenname","Temp101"));
		user2.add(new BasicAttribute("l","LM"));
		user2.add(new BasicAttribute("ou","WPLC"));
		user2.add(new BasicAttribute("sn","Temp101"));
		user2.add(new BasicAttribute("userPassword","passw0rd"));
		user2.add(new BasicAttribute("mail","utemp101@janet.iris.com"));
		user2.add(new BasicAttribute("cn","Temp101"));
		
		ArrayList<Attribute> user3 = new ArrayList<Attribute>();
		user3.add(new BasicAttribute("c","us"));
		user3.add(new BasicAttribute("departmentNumber","SM"));
		user3.add(new BasicAttribute("displayname","Temp102"));
		user3.add(new BasicAttribute("description","user added for tdi test"));
		user3.add(new BasicAttribute("givenname","Temp102"));
		user3.add(new BasicAttribute("l","LM"));
		user3.add(new BasicAttribute("ou","WPLC"));
		user3.add(new BasicAttribute("sn","Temp102"));
		user3.add(new BasicAttribute("userPassword","passw0rd"));
		user3.add(new BasicAttribute("mail","utemp102@janet.iris.com"));
		user3.add(new BasicAttribute("cn","Temp102"));
		
		ArrayList<Attribute> user4 = new ArrayList<Attribute>();
		user4.add(new BasicAttribute("c","us"));
		user4.add(new BasicAttribute("departmentNumber","SM"));
		user4.add(new BasicAttribute("displayname","Temp103"));
		user4.add(new BasicAttribute("description","user added for tdi test"));
		user4.add(new BasicAttribute("givenname","Temp103"));
		user4.add(new BasicAttribute("l","LM"));
		user4.add(new BasicAttribute("ou","WPLC"));
		user4.add(new BasicAttribute("sn","Temp103"));
		user4.add(new BasicAttribute("userPassword","passw0rd"));
		user4.add(new BasicAttribute("mail","utemp103@janet.iris.com"));
		user4.add(new BasicAttribute("cn","Temp103"));
		
		ArrayList<Attribute> user5 = new ArrayList<Attribute>();
		user5.add(new BasicAttribute("c","us"));
		user5.add(new BasicAttribute("departmentNumber","SM"));
		user5.add(new BasicAttribute("displayname","Temp104"));
		user5.add(new BasicAttribute("description","user added for tdi test"));
		user5.add(new BasicAttribute("givenname","Temp104"));
		user5.add(new BasicAttribute("l","LM"));
		user5.add(new BasicAttribute("ou","WPLC"));
		user5.add(new BasicAttribute("sn","Temp104"));
		user5.add(new BasicAttribute("userPassword","passw0rd"));
		user5.add(new BasicAttribute("mail","utemp104@janet.iris.com"));
		user5.add(new BasicAttribute("cn","Temp104"));
		
		users.add(user1);
		users.add(user2);
		users.add(user3);
		users.add(user4);
		users.add(user5);
		
		return users;
  }
  
}
