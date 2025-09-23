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

import com.ibm.conn.auto.appobjects.base.BaseFolder;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.Helper;

/**
 * Supporting static methods for building BaseFolder objects
 * The objective of this class is to reduce the number of lines
 * of code in test cases by moving the building of standard
 * versions of these objects
 * 
 * @author Patrick Doherty
 *
 */
public class FolderBaseBuilder {
	
	/**
	 * 
	 * @param folderName - The name of the folder.  Ideally this should be created from the name of the test with
	 * a unique identifier, e.g. String testName = ui.startTest();
	 * 							 testName = testName + Helper.genStrongRand();
	 * @param access - The access level of the folder, e.g. Access.PUBLIC for a public folder
	 * @return baseFolder - A BaseFolder object
	 */
	public static BaseFolder buildBaseFolder(String folderName, BaseFolder.Access access){
		
		BaseFolder baseFolder = new BaseFolder.Builder(folderName)
									.description(Data.getData().FolderDescription + Helper.genStrongRand())
									.access(access)
									.build();
		
		return baseFolder;
		
	}

}
