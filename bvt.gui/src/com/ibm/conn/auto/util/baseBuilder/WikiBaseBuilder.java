package com.ibm.conn.auto.util.baseBuilder;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2016		                                     */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

import java.util.ArrayList;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseWiki;
import com.ibm.conn.auto.appobjects.base.BaseWikiPage;
import com.ibm.conn.auto.appobjects.base.BaseWiki.EditAccess;
import com.ibm.conn.auto.appobjects.base.BaseWiki.ReadAccess;
import com.ibm.conn.auto.appobjects.base.BaseWikiPage.PageType;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.WikiRole;

/**
 * Supporting static methods for building BaseWiki and BaseWikiPage objects
 * The objective of this class is to reduce the number of lines
 * of code in test cases by moving the building of standard
 * versions of these objects
 * 
 * @author Patrick Doherty
 *
 */
public class WikiBaseBuilder {
	
	/**
	 * 
	 * @param wikiName - The name of the wiki.  Ideally this should be created from the name of the test with
	 * a unique identifier, e.g. String testName = ui.startTest();
	 * 							 testName = testName + Helper.genStrongRand();
	 * @param editAccess - The edit access for users, i.e. EditAccess.AllLoggedIn for public wikis
	 *  and EditAccess.EditorsAndOwners for private wikis
	 * @param readAccess - The read access for users, i.e. ReadAccess.All for public wikis
	 *  and ReadAccess.WikiOnly for private wikis
	 * @return baseWiki - A BaseWiki object
	 */
	public static BaseWiki buildBaseWiki(String wikiName, EditAccess editAccess, ReadAccess readAccess){
		
		BaseWiki baseWiki = new BaseWiki.Builder(wikiName)
										.editAccess(editAccess)
										.readAccess(readAccess)
										.description(Data.getData().commonDescription + Helper.genStrongRand())
										.tags(Data.getData().commonTag + Helper.genStrongRand())
										.build();
		return baseWiki;	
	}
	
	/**
	 * Creates a BaseWiki instance of a wiki with a custom tag set
	 * 
	 * @param wikiName - The String content of the title to be given to the wiki
	 * @param editAccess - The EditAccess value for the edit access for any users of the wiki
	 * @param readAccess - The ReadAccess value for the read access for any users of the wiki
	 * @param customTag - The String content of the custom tag to be assigned to the wiki
	 * @return - The BaseWiki instance
	 */
	public static BaseWiki buildBaseWikiWithCustomTag(String wikiName, EditAccess editAccess, ReadAccess readAccess, String customTag) {
		
		BaseWiki baseWiki = buildBaseWiki(wikiName, editAccess, readAccess);
		baseWiki.setTags(customTag);
		
		return baseWiki;
	}
	
	/**
	 * Creates a BaseWiki instance which includes one member for the wiki
	 * 
	 * @param wikiName - The name of the wiki.  Ideally this should be created from the name of the test with a unique identifier
	 * @param editAccess - The edit access for users, i.e. EditAccess.AllLoggedIn for public wikis and EditAccess.EditorsAndOwners for private wikis
	 * @param readAccess - The read access for users, i.e. ReadAccess.All for public wikis and ReadAccess.WikiOnly for private wikis
	 * @param userToBeAMember - The User instance of the user to be added to the wiki as a member
	 * @param uuidOfUserToBeAMember - The UUID of the User to be added to the wiki as a member (obtained from using APIProfilesHandler.getUUID())
	 * @return - The BaseWiki instance
	 */
	public static BaseWiki buildBaseWikiWithOneMember(String wikiName, EditAccess editAccess, ReadAccess readAccess, WikiRole memberRole, User userToBeAMember, String uuidOfUserToBeAMember) {
		
		BaseWiki baseWiki = new BaseWiki.Builder(wikiName)
										.editAccess(editAccess)
										.readAccess(readAccess)
										.description(Data.getData().commonDescription + Helper.genStrongRand())
										.tags(Data.getData().commonTag + Helper.genStrongRand())
										.addMember(new Member(memberRole, userToBeAMember, uuidOfUserToBeAMember))
										.build();
		return baseWiki;
	}
	
	/**
	 * Creates a BaseWiki instance which includes multiple members for the wiki
	 * 
	 * @param wikiName - The name of the wiki.  Ideally this should be created from the name of the test with a unique identifier
	 * @param editAccess - The edit access for users, i.e. EditAccess.AllLoggedIn for public wikis and EditAccess.EditorsAndOwners for private wikis
	 * @param readAccess - The read access for users, i.e. ReadAccess.All for public wikis and ReadAccess.WikiOnly for private wikis
	 * @param memberRoles - An array of WikiRole instances representing the member role for each member
	 * @param userMembers - An array of User instances representing each member to be added to the wiki
	 * @param uuidsOfMembers - An array of Strings representing the UUID's of each member to be added to the wiki
	 * @return - The BaseWiki instance
	 */
	public static BaseWiki buildBaseWikiWithMultipleMembers(String wikiName, EditAccess editAccess, ReadAccess readAccess, WikiRole[] memberRoles, User[] userMembers, String[] uuidsOfMembers) {
		
		// Create the list of members with their specified roles
		ArrayList<Member> listOfMembers = new ArrayList<Member>();
		
		for(int index = 0; index < userMembers.length; index ++) {
			Member newMember = new Member(memberRoles[index], userMembers[index], uuidsOfMembers[index]);
			listOfMembers.add(newMember);
		}
		
		// Now build the baseWiki template with the list of members
		BaseWiki baseWiki = new BaseWiki.Builder(wikiName)
										.editAccess(editAccess)
										.readAccess(readAccess)
										.description(Data.getData().commonDescription + Helper.genStrongRand())
										.tags(Data.getData().commonTag + Helper.genStrongRand())
										.addMembers(listOfMembers)
										.build();
		return baseWiki;
	}
	
	/**
	 * Creates a BaseWikiPage instance of a wiki page
	 * 
	 * @param wikiPageName - The name of the wiki page.  Ideally this should be created from the name of the test with
	 * a unique identifier, e.g. String testName = ui.startTest();
	 * 							 testName = testName + Helper.genStrongRand();
	 * @return baseWikiPage - A BaseWikiPage object
	 */
	public static BaseWikiPage buildBaseWikiPage(String wikiPageName){
		
		BaseWikiPage baseWikiPage = new BaseWikiPage.Builder(wikiPageName, PageType.Peer)
													.description(Data.getData().commonDescription + Helper.genStrongRand())
													.tags(Data.getData().commonTag + Helper.genStrongRand())
													.build();
		return baseWikiPage;
	}
	
	/**
	 * Creates a BaseWikiPage instance of a wiki page and allows a custom description to be set for that wiki page
	 * 
	 * @param wikiPageName - The name of the wiki page. Ideally this should be created from the name of the test with a unique identifier, e.g. String testName = ui.startTest();
	 * @param description - The description that the wiki page will have when it's created
	 * @return baseWikiPage - A BaseWikiPage object
	 */
	public static BaseWikiPage buildBaseWikiPageWithCustomDescription(String wikiPageName, String description) {
		
		BaseWikiPage baseWikiPage = buildBaseWikiPage(wikiPageName);
		baseWikiPage.setDescription(description);
		
		return baseWikiPage;
	}
	
	/**
	 * Creates a BaseWikiPage instance of a wiki page and allows a custom tag to be set for that wiki page
	 * 
	 * @param wikiPageName - The name of the wiki page. Ideally this should be created from the name of the test with a unique identifier, e.g. String testName = ui.startTest();
	 * @param customTag - The String content of the tag to be set to the wiki page
	 * @return baseWikiPage - A BaseWikiPage object
	 */
	public static BaseWikiPage buildBaseWikiPageWithCustomTag(String wikiPageName, String customTag) {
		
		BaseWikiPage baseWikiPage = buildBaseWikiPage(wikiPageName);
		baseWikiPage.setTags(customTag);
		
		return baseWikiPage;
	}
}