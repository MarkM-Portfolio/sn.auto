package com.ibm.lconn.automation.framework.activitiesStreamSearch.utils.nodes;

/*
 * Yakov Vilenchik 10/10/2012
 * This class is used to create one log file for all tests
 * Each test class call init() method of this class and if fh=null then it initialize fh, if fh != null then test class
 * should use already existing fh and logger.
 * Each tests class should start with "********class name*********"
 * Each tests class should extends this class in order to use it fh and logger  
 */

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import org.testng.annotations.BeforeClass;

public class FvtMasterLogsClass {

	public final static Logger LOGGER = Logger
			.getLogger(FvtMasterLogsClass.class.getName());

	static FileHandler fh = null;

	@BeforeClass
	public static void setUp() throws Exception {
		init();
	}

	public static void init() throws Exception {
		
		if (fh == null) {
			DateFormat formatter = new SimpleDateFormat("yyyyMMdd'T'hhmmss'Z'");
			fh = new FileHandler("logs/" + formatter.format(new Date())
					+ "_AS_Search_FVT_Test_Suite_Execution.xml", true);
			LOGGER.addHandler(fh);

			
			
		} 

	}

	public static void close() {
		fh.close();
	}

	public static void flush() {
		fh.flush();
	}

	// @AfterClass
	// public static void flushLog( ){
	// close();
	// }

}
