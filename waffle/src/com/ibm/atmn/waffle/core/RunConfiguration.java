package com.ibm.atmn.waffle.core;

import java.io.File;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.atmn.waffle.log.LogManager;
import com.ibm.atmn.waffle.utils.FileIOHandler;

/**
 * Responsible for all configuration above the Test level (and therefore not suitable for {@link TestConfiguration}.
 * 
 * @author Ruairi Pidgeon/Ireland/IBM
 *
 */
public class RunConfiguration {
	
	private static final Logger log = LoggerFactory.getLogger(RunConfiguration.class);

	/*
	 * Files identify purposed directories and files on local machine. These files/folders must be common to all suites.
	 */
	private File testOutputFolder;
	private File logOutputFolder; //should be used in logback.xml also but these are not connected

	private static final String TEST_OUTPUT_FOLDER_NAME = "test-output";
	private static final String LOG_OUTPUT_FOLDER_NAME = "logs";
	
	private Environment localEnvironment;

	//properties
	private static final String CORE_PROPERTIES_PATH = "test_config/core/waffle.properties";
	private static Properties coreProperties = FileIOHandler.loadExternalProperties(CORE_PROPERTIES_PATH);

	private static final String SYSTEM_PROPERTIES_PATH = "test_config/core/system.properties";
	private static Properties systemProperties = FileIOHandler.loadExternalProperties(SYSTEM_PROPERTIES_PATH);
	
	private RunConfiguration() {

		this.testOutputFolder = FileIOHandler.createFolderFromPath(TEST_OUTPUT_FOLDER_NAME, false);
		//this.runOutputFolder = new File(this.testOutputFolder, RUN_FOLDER_TIMESTAMP);
		this.logOutputFolder = FileIOHandler.createFolderFromPath( new File(this.testOutputFolder, LOG_OUTPUT_FOLDER_NAME).getAbsolutePath(), false);
		verifyFilesAndDirectories();
		this.localEnvironment = new Environment();
		printProps();
	}

	private static class RunConfigurationHolder {

		private static final RunConfiguration instance = new RunConfiguration();
	}

	/**
	 * Returns singleton instance
	 * 
	 * @return RunConfiguration
	 */
	public static RunConfiguration getInstance() {

		return RunConfigurationHolder.instance;
	}

	//TODO: This must be moved and changed so that it will cleanup files as necessary and verify existence of all required files.
	private void verifyFilesAndDirectories() {

		//stop a run without folder early
		File testConfigFolder = new File("test_config");
		if (!testConfigFolder.exists()) {
			log.error("Required file or directory missing at " + testConfigFolder.getAbsolutePath());
			throw new RuntimeException("The required test_config folder can not be found at: " + testConfigFolder.getAbsolutePath());
		}
		
	}

	public Environment getLocalEnvironment() {

		return this.localEnvironment;
	}
	
	public File getTestOutputFolder() {
		return this.testOutputFolder;
	}
	
	public File getLogOutputFolder() {
		return logOutputFolder;
	}
	
	/**
	 * Prints out all of the environment properties
	 */
	@SuppressWarnings("unchecked")
	private void printProps() {

		// sort the properties
		Map<String, String> sortedCore = new TreeMap<String, String>((Map) coreProperties);
		Map<String, String> sortedSystem = new TreeMap<String, String>((Map) coreProperties);
		LogManager.printPropertyMap(sortedCore, "CORE PROPERTIES");
		LogManager.printPropertyMap(sortedSystem, "SYSTEM PROPERTIES");
	}

	public static Properties getSystemProperties() {
		return systemProperties;
	}
	
	public static Properties getCoreProperties() {
		return coreProperties;
	}
}
