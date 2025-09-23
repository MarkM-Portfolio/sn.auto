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

import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.util.Helper;

/**
 * Supporting static methods for building BaseFile objects
 * The objective of this class is to reduce the number of lines
 * of code in test cases by moving the building of standard
 * versions of these objects
 * 
 * @author Patrick Doherty
 *
 */
public class FileBaseBuilder {
	
	/**
	 * 
	 * @param fileName - The name of the file, e.g. Data.getData().file1
	 * @param extension - The extension of the file, e.g. ".jpg"
	 * @param shareLevel - The share level of the file, e.g. ShareLevel.EVERYONE for a public file
	 * @return baseFile - A BaseFile object
	 */
	public static BaseFile buildBaseFile(String fileName, String extension, BaseFile.ShareLevel shareLevel){

		BaseFile baseFile = new BaseFile.Builder(fileName)
										.extension(extension)
										.rename(Helper.genStrongRand())
										.tags(Data.getData().commonTag + Helper.genStrongRand())
										.shareLevel(shareLevel)
										.build();
		return baseFile;
	}
	
	/**
	 * 
	 * @param fileName - The name of the file, e.g. Data.getData().file1
	 * @param extension - The extension of the file, e.g. ".jpg"
	 * @param shareLevel - The share level of the file, e.g. ShareLevel.EVERYONE for a public file
	 * @param userToShareWith - The APIProfilesHandler instance of the user to share the file with
	 * @return - A BaseFile object
	 */
	public static BaseFile buildBaseFile(String fileName, String extension, BaseFile.ShareLevel shareLevel, APIProfilesHandler userToShareWith) {
		
		BaseFile baseFile = buildBaseFile(fileName, extension, shareLevel);
		baseFile.setSharedWith(userToShareWith.getUUID());
		return baseFile;
	}
	
	/**
	 * Builds a BaseFile instance with custom tags
	 * 
	 * @param fileName - The name of the file, e.g. Data.getData().file1
	 * @param extension - The extension of the file, e.g. ".jpg"
	 * @param shareLevel - The share level of the file, e.g. ShareLevel.EVERYONE for a public file
	 * @param tag - The tag to be attached to the file
	 * @return - A BaseFile object
	 */
	public static BaseFile buildBaseFileWithCustomTag(String fileName, String extension, BaseFile.ShareLevel shareLevel, String tag) {
		
		BaseFile baseFile = buildBaseFile(fileName, extension, shareLevel);
		baseFile.setTags(tag);
		return baseFile;
	}
	
	/**
	 * Builds a BaseFile instance with multiple tags
	 * 
	 * @param fileName - The name of the file, e.g. Data.getData().file1
	 * @param extension - The extension of the file, e.g. ".jpg"
	 * @param shareLevel - The share level of the file, e.g. ShareLevel.EVERYONE for a public file
	 * @param numberOfTagsToInclude - The int number of multiple tags to be set to the file
	 * @return - A BaseFile object
	 */
	public static BaseFile buildBaseFileWithMultipleTags(String fileName, String extension, BaseFile.ShareLevel shareLevel, int numberOfTagsToInclude) {
		
		String multipleTagsString = "";
		for(int index = 0; index < numberOfTagsToInclude; index ++) {
			multipleTagsString += Helper.genStrongRand() + " ";
		}
		multipleTagsString = multipleTagsString.trim();
		
		BaseFile baseFile = buildBaseFile(fileName, extension, shareLevel);
		baseFile.setTags(multipleTagsString);
		return baseFile;
	}
}
