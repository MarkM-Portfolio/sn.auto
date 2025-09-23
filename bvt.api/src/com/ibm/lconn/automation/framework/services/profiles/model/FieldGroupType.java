/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2015                                          */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.lconn.automation.framework.services.profiles.model;

public enum FieldGroupType {
	BASE("base"), SYS("sys"), EXT("ext");

	private String value;

	private FieldGroupType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static FieldGroupType lookup(String value) {
		for (FieldGroupType fgt : FieldGroupType.values()) {
			if (value.equals(fgt.getValue())) return fgt;
		}
		return null;
	}
}
