package com.ibm.atmn.waffle.log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.TreeSet;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * For per-test logging and logging helpers.
 * 
 * @author Ruairi Pidgeon/Ireland/IBM
 *
 */
public class LogManager {

	private static final Logger log = LoggerFactory.getLogger(LogManager.class);

	public static final String MDC_TEST_IDENTIFIER = "testname";

	public static void startTestLogging(String name) {
		MDC.put(MDC_TEST_IDENTIFIER, name);
		//log.info("Started test logging for test: " + name);
	}

	public static String stopTestLogging() {
		String name = MDC.get(MDC_TEST_IDENTIFIER);
		MDC.remove(MDC_TEST_IDENTIFIER);
		//log.info("Stopped test logging for test: " + name);
		return name;
	}

	public static void printPropertyMap(Map<?, ?> map, String title) {

		TreeSet<String> props = new TreeSet<String>();
		for (Entry<?, ?> prop : map.entrySet()) {
			props.add(String.format("%1$s", String.format("%1$35s = %2$s", prop.getKey(), prop.getValue())));
		}
		printBlock(title, props.toArray(new String[0]));
	}

	public static void printBlock(String title, String... block) {

		if (block.length > 0) {

			StringBuilder output = new StringBuilder("\n");
			output.append(getBlockHeader(title) + "\n");
			for (String str : block) {
				if(str.contains("BROWSERSTACK_KEY") || str.contains("BROWSERSTACK_USERNAME")){
					str=str.replace(str.substring(str.indexOf("=")+1, str.length()),"***********");
				}
				output.append(str + "\n");
			}
			output.append(getDec(getBlockHeader(title).length()) + "\n");
			log.info(output.toString());
			File destination = new File("GUIBVTLog.txt");
			try {
				FileWriter fw = new FileWriter(destination,true); //the true will append the new data
			    fw.write(output.toString());//appends the string to the file
			    fw.write("\r\n");//adds a carriage return after each line
			    fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static String getBlockHeader(String title) {

		return getDec(20) + " " + title.toUpperCase().trim() + " " + getDec(20);
	}

	private static String getDec(int length) {

		String footer = "";
		for (int i = 0; i < length; i++)
			footer = footer + "*";
		return footer;
	}
}