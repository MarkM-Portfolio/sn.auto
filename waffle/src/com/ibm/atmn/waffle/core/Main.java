package com.ibm.atmn.waffle.core;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.testng.TestNG;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ibm.atmn.waffle.utils.FileIOHandler;

/**
 * To be set as main class of executable jar to allow for custom arguments.
 * Do not use Logger here.
 * 
 * @author Ruairi Pidgeon/Ireland/IBM
 */
public class Main {

	private static final String TEMPLATE_FILE_ARG = "-IBMtemplate";
	private static final String TEMPLATE_FILE_ARG_HCL = "-HCLtemplate";
	private static final String BROWSER_START_COMMAND_DELIMITER = ";";
	private static final String RUN_FOLDER_TIMESTAMP = new SimpleDateFormat("yy.MM.dd-HH.mm.ss").format(new Date());

	/**
	 * To be used instead of TestNG main method for executable jar only. Testng Ant task runner and plug-in do not use
	 * this.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		System.setProperty("logback.configurationFile", "test_config/log/logback.xml");
		ArrayList<String> suites = null;
		ArrayList<String> testngArgs = new ArrayList<String>();

		testngArgs.add("-listener");
		testngArgs.add("com.ibm.atmn.waffle.base.BaseTestListener");
		
		//loop through all args and process custom arguments
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			if (arg.equalsIgnoreCase(TEMPLATE_FILE_ARG) || arg.equalsIgnoreCase(TEMPLATE_FILE_ARG_HCL)) {
				suites = createSuiteFiles(args[i + 1]);
				i++;
			}
			else { //not a custom argument: belongs to testng
				testngArgs.add(arg);
			}
		}

		//set testng -suitethreadpoolsize argument automatically to run all suites in parallel
		if (suites != null && suites.size() > 0) {
			if (testngArgs.indexOf("-suitethreadpoolsize") > -1) {
				testngArgs.set(testngArgs.indexOf("-suitethreadpoolsize") + 1, String.valueOf(suites.size()));
			}
			else {
				testngArgs.add("-suitethreadpoolsize");
				testngArgs.add(String.valueOf(suites.size()));
			}
			for (String suite : suites) {
				testngArgs.add(suite);
			}
		}

		String[] modifiedArgs = new String[testngArgs.size()];
		testngArgs.toArray(modifiedArgs);

		for (String finalarg : modifiedArgs) {
			
			System.out.println("Arg: " + finalarg);
		}
		
		try {
			TestNG testNG = TestNG.privateMain(modifiedArgs, null);
			
			//RunConfiguration rConfig = RunConfiguration.getInstance();
			//File logDirectory = rConfig.getLogOutputFolder();
			File testngOutputDirectory = new File(testNG.getOutputDirectory());	
			testngOutputDirectory.renameTo(new File(testngOutputDirectory, RUN_FOLDER_TIMESTAMP));
			//logDirectory.renameTo(new File(testngOutputDirectory, logDirectory.getName()));
			System.exit(testNG.getStatus());
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * Creates new testng suite xml files from a template.xml
	 * 
	 * @param templateName
	 * @return ArrayList<String> of testng suite.xml file names
	 */
	private static ArrayList<String> createSuiteFiles(String templateName) {

		ArrayList<String> suiteFilenames = new ArrayList<String>();
		String[] startCommands = null;

		//read xml
		File readFile = new File(templateName);
		if (readFile.exists() != true || readFile.canRead() != true) {
			//TODO:something
		}
		Document template = FileIOHandler.getXMLConfig(readFile);

		//get browser start command parameter
		NodeList parameters = template.getElementsByTagName("parameter");
		Node browserStartCommandParameter = findNodeByAttributeValue(parameters, "name", "browser_start_command");

		//get value attribute
		String parameterValue = getAttributeValue(browserStartCommandParameter, "value");

		//Get start commands
		if (parameterValue != null && parameterValue.length() > 0) {
			startCommands = parameterValue.split(BROWSER_START_COMMAND_DELIMITER);
		}
		else {
			//TODO: something
		}

		//modify template with commands, 1 per browser start command, write to file
		if (startCommands != null && startCommands.length > 0) {
			int i = 0;
			for (String browserStartCommand : startCommands) {
				setAttributeValue(browserStartCommandParameter, "value", browserStartCommand);
				Node suite = template.getElementsByTagName("suite").item(0);
				setAttributeValue(suite, "name", browserStartCommand + "-" + String.valueOf(i));
				RunConfiguration rConfig = RunConfiguration.getInstance();
				String fileName = browserStartCommand + "-" + String.valueOf(i) + ".xml";
				File targetFile = new File(rConfig.getLogOutputFolder(), fileName);
				boolean fileCreated = false;
				if (targetFile.exists()) {
					targetFile.delete();
				}
				try {
					fileCreated = targetFile.createNewFile();
				} catch (IOException e2) {
					System.out.println(targetFile);
					e2.printStackTrace();
				}
				
				if (fileCreated) {
					suiteFilenames.add(rConfig.getLogOutputFolder().getPath() + File.separator + fileName);
					FileIOHandler.writeXmlFile(template, targetFile);
				}
				i++;
			}
		}
		//return file names
		return suiteFilenames;

	}

	private static Node findNodeByAttributeValue(NodeList elements, String attributeName, String attributeValue) {

		for (int i = 0; i < elements.getLength(); i++) {
			Node element = elements.item(i);
			if (element.hasAttributes()) {
				String attValue = getAttributeValue(element, attributeName);
				if (attValue != null && attValue.equalsIgnoreCase(attributeValue)) {
					return element;
				}
			}

		}
		return null;
	}

	private static String getAttributeValue(Node element, String attributeName) {

		return getAttribute(element, attributeName).getNodeValue();
	}

	private static Node getAttribute(Node element, String attributeName) {

		NamedNodeMap attributes = element.getAttributes();
		for (int k = 0; k < attributes.getLength(); k++) {
			Node current = attributes.item(k);
			if (current.getNodeName().equalsIgnoreCase(attributeName)) {
				return current;
			}
		}
		return null;
	}

	private static void setAttributeValue(Node element, String attributeName, String newValue) {

		getAttribute(element, attributeName).setNodeValue(newValue);
	}

}
