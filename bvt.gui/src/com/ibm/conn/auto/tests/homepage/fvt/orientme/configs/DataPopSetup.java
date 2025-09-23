package com.ibm.conn.auto.tests.homepage.fvt.orientme.configs;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.tests.homepage.fvt.orientme.data.JSONData;
import com.ibm.conn.auto.tests.homepage.fvt.orientme.objects.TestCaseData;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2017                                          */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * @author 	Anthony Cox
 * Date:	22nd February 2017
 */

public abstract class DataPopSetup extends SetUpMethodsFVT {

	protected static Logger log = LoggerFactory.getLogger(DataPopSetup.class);
	
	protected final String FILE_ABSOLUTE_PATH = "/";
	protected final String FILE_EXTENSION = ".json";
	
	protected String filename;
	private TestCaseData testCaseData;
	
	@BeforeClass(alwaysRun=true)
	@Override
	public void beforeClass(ITestContext context) {

		super.beforeClass(context);
		
		setFilename(null);
		setTestCaseData(new TestCaseData());
	}
	
	@AfterClass(alwaysRun = true)
	public void afterClass() {
		
		// Output the final state of the test case data to the specified file name
		outputTestCaseDataToJSFile();
	}
	
	/**
	 * Returns the value for the 'filename' attribute
	 * 
	 * @return - The String content of the 'filename' attribute
	 */
	protected String getFilename() {
		return filename;
	}
	
	/**
	 * Retrieves the current TestCaseData instance of the 'testCaseData' attribute
	 * 
	 * @return - The TestCaseData instance of the 'testCaseData' attribute
	 */
	protected TestCaseData getTestCaseData() {
		return testCaseData;
	}
	
	/**
	 * Outputs the current contents of the 'testCaseData' attribute to the file specified in 'filename' in JSON format
	 */
	private void outputTestCaseDataToJSFile() {
		
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(new File(getFilename()));
			fileWriter.write(JSONData.convertHashMapToJSONString(getTestCaseData()));
		} catch(IOException ioex) {
			log.info("ERROR: Could not create a new file with path and name: " + getFilename());
			ioex.printStackTrace();
			Assert.fail("ERROR: Failing currently executing class - new file could NOT be created");
		} finally {
			if(fileWriter != null) {
				try {
					fileWriter.close();
				} catch(IOException ioex) {
					log.info("ERROR: Could not close the FileWriter instance cleanly");
					ioex.printStackTrace();
					Assert.fail("ERROR: Failing currently executing class - FileWriter could NOT be closed cleanly");
				}
			}
		}
	}
	
	/**
	 * Sets the value for the 'filename' attribute with the absolute path to the file and the file extension being added by default
	 * 
	 * @param filenameValue - The String content to be set as the value for the 'filename' attribute
	 */
	protected void setFilename(String filenameValue) {
		if(filenameValue == null) {
			filename = null;
		} else {
			filename = FILE_ABSOLUTE_PATH + filenameValue + FILE_EXTENSION;
			log.info("INFO: The file name and path to the JSON script to be imported have now been set to: " + filename);
		}
	}
	
	/**
	 * Sets the value for the 'testCaseData' attribute
	 * 
	 * @param dataValue - The TestCaseData instance to which the 'testCaseValue' attribute will be set
	 */
	private void setTestCaseData(TestCaseData dataValue) {
		testCaseData = dataValue;
	}
}