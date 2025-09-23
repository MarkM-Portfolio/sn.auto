package com.ibm.conn.auto.util.baseBuilder;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.util.Mentions;

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

/**
 * Supporting static methods for building Mentions objects
 * The objective of this class is to reduce the number of lines
 * of code in test cases by moving the building of standard
 * versions of these objects
 * 
 * @author Anthony Cox
 */

public class MentionsBaseBuilder {

	/**
	 * Creates a base class for Mentions
	 * 
	 * @param userToBeMentioned - The User instance of the user to be mentioned
	 * @param mentionedUserProfile - The APIProfilesHandler instance of the user to be mentioned
	 * @param browserURL - The URL of the browser (usually the serverURL variable in test cases)
	 * @param beforeMentionsText - The text to appear before the user is mentioned
	 * @param afterMentionsText - The text to appear after the user is mentioned
	 * @return - The complete Mentions object with all attributes set
	 */
	public static Mentions buildBaseMentions(User userToBeMentioned, APIProfilesHandler mentionedUserProfile, String browserURL,
											String beforeMentionsText, String afterMentionsText) {
		
		Mentions mentions = new Mentions.Builder(userToBeMentioned, mentionedUserProfile.getUUID())
										.browserURL(browserURL)
										.beforeMentionText(beforeMentionsText)
										.afterMentionText(afterMentionsText)
										.build();
		return mentions;
	}
}
