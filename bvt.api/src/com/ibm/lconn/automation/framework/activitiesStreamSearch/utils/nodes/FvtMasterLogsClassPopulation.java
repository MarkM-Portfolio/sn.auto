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

public class FvtMasterLogsClassPopulation {

	public final static Logger LOGGER = Logger
			.getLogger(FvtMasterLogsClassPopulation.class.getName());

	static FileHandler fh = null;

	@BeforeClass
	public static void setUp() throws Exception {
		init();
	}

	public static void init() throws Exception {
		System.out.println("FH status: " + fh);
		if (fh == null) {
			DateFormat formatter = new SimpleDateFormat("yyyyMMdd'T'hhmmss'Z'");
			fh = new FileHandler("logs/" + formatter.format(new Date())
					+ "_AS_Search_Events_Population.xml", true);
			LOGGER.addHandler(fh);

			LOGGER.fine("Starting AS Search Events Population....");
			System.out.println("FH status after initiation: " + fh);
		} else {
			System.out.println("Logger already initialized");
			System.out.println("FH status: " + fh);
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
