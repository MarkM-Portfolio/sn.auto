/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2012                                          */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.lconn.automation.framework.services.profiles.util;

public class GetBoardEntriesParameters extends AbstractParameters {

	public GetBoardEntriesParameters(){
		
	}
	
	public void setEmail(String value)
	{
	    put("email", value);
	}
	
	public void setPageSize(int value)
	{
		put("ps", "" + value);
	}
	public void setComments(String value){
		put("comments",value);
	}
	public void setMessageType(String value){
		put("messageTypes",value);
	}
	public void setSortOrder(String value){
		put("sortOrder",value);
	}
	public void setSortBy(String value){
		put("sortBy",value);
	}
}
