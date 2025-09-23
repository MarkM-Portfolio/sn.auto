package com.ibm.conn.auto.util.userBuilder;

import java.util.ArrayList;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2017                                    		 */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * 	Author:		Anthony Cox
 * 	Date:		11th April 2017
 */

public class UserSelector {
	
	private static final String ADMIN_USER = "Admin User";
	
	/**
	 * Selects the specified number of admin users from the user pool
	 * 
	 * @param cfg - The TestConfigCustom instance to invoke all relevant methods
	 * @param checkoutToken - The String instance of the checkout token to be assigned to the users
	 * @param numberOfUsersToSelect - The Integer value of how many unique users are to be selected from the user pool
	 * @return - An ArrayList containing the specified number of admin users
	 */
	public static ArrayList<User> selectUniqueUsers_Admin(TestConfigCustom cfg, String checkoutToken, int numberOfUsersToSelect) {
		
		return selectUniqueUsersFromUserPool(cfg, ADMIN_USER, checkoutToken, numberOfUsersToSelect);
	}
	
	/**
	 * Selects the specified number of OrgA users from the user pool
	 * 
	 * @param cfg - The TestConfigCustom instance to invoke all relevant methods
	 * @param checkoutToken - The String instance of the checkout token to be assigned to the users
	 * @param numberOfUsersToSelect - The Integer value of how many unique users are to be selected from the user pool
	 * @return - An ArrayList containing the specified number of OrgA users
	 */
	public static ArrayList<User> selectUniqueUsers_OrgA(TestConfigCustom cfg, String checkoutToken, int numberOfUsersToSelect) {
		
		return selectUniqueUsersFromUserPool(cfg, HomepageUIConstants.OrgA, checkoutToken, numberOfUsersToSelect);
	}
	
	/**
	 * Selects the specified number of OrgB users from the user pool
	 * 
	 * @param cfg - The TestConfigCustom instance to invoke all relevant methods
	 * @param checkoutToken - The String instance of the checkout token to be assigned to the users
	 * @param numberOfUsersToSelect - The Integer value of how many unique users are to be selected from the user pool
	 * @return - An ArrayList containing the specified number of OrgB users
	 */
	public static ArrayList<User> selectUniqueUsers_OrgB(TestConfigCustom cfg, String checkoutToken, int numberOfUsersToSelect) {
		
		return selectUniqueUsersFromUserPool(cfg, HomepageUIConstants.OrgB, checkoutToken, numberOfUsersToSelect);
	}
	
	/**
	 * Selects the specified number of standard users from the user pool
	 * 
	 * @param cfg - The TestConfigCustom instance to invoke all relevant methods
	 * @param checkoutToken - The String instance of the checkout token to be assigned to the users
	 * @param numberOfUsersToSelect - The Integer value of how many unique users are to be selected from the user pool
	 * @return - An ArrayList containing the specified number of standard test users
	 */
	public static ArrayList<User> selectUniqueUsers_Standard(TestConfigCustom cfg, String checkoutToken, int numberOfUsersToSelect) {
		
		return selectUniqueUsersFromUserPool(cfg, null, checkoutToken, numberOfUsersToSelect);
	}
	
	/**
	 * Chooses a user from the user pool, verifies they are unique and, if so, adds them to the ArrayList of User instances until the required amount of users is reached
	 * 
	 * @param cfg - The TestConfigCustom instance to invoke all relevant methods
	 * @param userGroup - The String content of the group to which the user to be selected belongs
	 * @param checkoutToken - The String instance of the checkout token to be assigned to the users
	 * @param numberOfUsersToSelect - The Integer value of how many unique users are to be selected from the user pool
	 * @return - An ArrayList containing the specified number of unique User instances
	 */
	private static ArrayList<User> selectUniqueUsersFromUserPool(TestConfigCustom cfg, String userGroup, String checkoutToken, int numberOfUsersToSelect) {
		
		// Create a new array list of users in which to store all required users
		ArrayList<User> listOfUniqueUsers = new ArrayList<User>();
		
		while(listOfUniqueUsers.size() < numberOfUsersToSelect) {
			// Choose a user from the user pool
			User currentUser;
			if(userGroup == null) {
				currentUser = cfg.getUserAllocator().getUser(checkoutToken);
			} else if(userGroup.equals(ADMIN_USER)) {
				currentUser = cfg.getUserAllocator().getAdminUser(checkoutToken);
		    } else {
				currentUser = cfg.getUserAllocator().getGroupUser(userGroup, checkoutToken);
			}
			// Verify if the current user is unique
			int index = 0;
			boolean userIsUnique = true;
			while(index < listOfUniqueUsers.size() && userIsUnique == true) {
				if(listOfUniqueUsers.get(index).getDisplayName().trim().equals(currentUser.getDisplayName().trim())) {
					userIsUnique = false;
				}
				index ++;
			}
			// Add the user to the list of users only if they are unique
			if(userIsUnique) {
				listOfUniqueUsers.add(currentUser);
			}
		}
		return listOfUniqueUsers;
	}
}