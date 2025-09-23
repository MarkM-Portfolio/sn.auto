/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2014                                          */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
package com.ibm.lconn.automation.framework.services.opensocial;

public class ASCustomListPopulationRequest {
	private String listId;
	private String context;
	private String otherFilterType;
	private String otherFilterValue;
	private String url;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getListId() {
		return listId;
	}

	public void setListId(String listId) {
		this.listId = listId;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public String getOtherFilterType() {
		return otherFilterType;
	}

	public void setOtherFilterType(String otherFilterType) {
		this.otherFilterType = otherFilterType;
	}

	public String getOtherFilterValue() {
		return otherFilterValue;
	}

	public void setOtherFilterValue(String otherFilterValue) {
		this.otherFilterValue = otherFilterValue;
	};
}
