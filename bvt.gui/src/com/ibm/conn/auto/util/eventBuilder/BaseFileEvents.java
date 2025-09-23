package com.ibm.conn.auto.util.eventBuilder;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2016			                                 */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/*
 * Author:	Anthony Cox
 * Date:	25th October 2016
 */

public abstract class BaseFileEvents {
	
	private static final String RESOURCES_DIR_ABSOLUTE_PATH = "resources/";
	
	/**
	 * Retrieves the value for the resources folders absolute path
	 * 
	 * @return - The path to the resources folder
	 */
	protected static String getResourcesDirAbsolutePath() {
		return RESOURCES_DIR_ABSOLUTE_PATH;
	}
}