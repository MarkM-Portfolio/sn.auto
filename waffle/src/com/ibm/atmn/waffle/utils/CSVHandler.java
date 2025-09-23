package com.ibm.atmn.waffle.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class CSVHandler.
 */
public class CSVHandler {

	private static final Logger log = LoggerFactory.getLogger(CSVHandler.class);

	private static String getNextLine(BufferedReader br) {

		String out = null;
		try {
			out = br.readLine();
		} catch (IOException e) {
			log.warn("IOException while reading CSV. Exception: " + e.getMessage());
		}
		return out;
	}

	private static ArrayList<String> readFileByLines(String filePath) {

		ArrayList<String> lines = new ArrayList<String>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(filePath));

			// loop through all lines until end of file
			for (String line = getNextLine(br); line != null; line = getNextLine(br)) {

				lines.add(line);
			}
			
		} catch (FileNotFoundException e) {
			log.warn("FileNotFoundException trying to load CSV: '" + filePath + "'. Exception: " + e.getMessage());
			e.printStackTrace();
			throw new RuntimeException("CSV file could not be loaded. See log.");
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					log.warn("Stream failed to close");
					e.printStackTrace();
				}
			}
		}
		return lines;
	}

	/**
	 * 
	 * @param filePath
	 * @param separator
	 * @param headerIncluded
	 * @return List of maps where each map represents a table row with header,value pairs.
	 */
	public static ArrayList<HashMap<String, String>> loadCSV(String filePath, String separator, boolean headerIncluded) {

		if(filePath == null || separator == null ){
			throw new IllegalArgumentException("filePath or separator not specified (null). Can't load CSV.");
		}
		// The map that will be returned. Will be returned empty if there is no data.
		ArrayList<HashMap<String, String>> listOfHeaderToValueMaps = new ArrayList<HashMap<String, String>>();

		ArrayList<String> lines = readFileByLines(filePath);

		if (lines.size() > 0) {

			// Create list of the headers, or {"0","1","2",...} as default if no headers included
			ArrayList<String> headers;
			if (headerIncluded) {
				String headerLine = lines.remove(0);
				headers = new ArrayList<String>(Arrays.asList(headerLine.split(separator)));
			} else {
				headers = generateDefaultHeaders(lines.get(0).split(separator).length);
			}
			validateHeaders(headers);

			for (int j = 0; j < lines.size(); j++) {

				String line = lines.get(j);
				// split line to values
				ArrayList<String> entry = new ArrayList<String>(Arrays.asList(line.split(separator)));

				// skip line if the number of elements don't match the number of headings
				if (headers.size() != entry.size()) {
					int lineNumber = j;
					if(headerIncluded) lineNumber++;
					log.warn("CSV row (line "+lineNumber+") does not contain the same number of elements as headers. Line skipped.");
				} else {
					HashMap<String, String> headerToValueMap = new HashMap<String, String>();
					for (int i = 0; i < headers.size(); i++) {
						headerToValueMap.put(headers.get(i), entry.get(i));
					}
					// All OK, add map to list
					listOfHeaderToValueMaps.add(headerToValueMap);
				}
			}
		}
		return listOfHeaderToValueMaps;

	}

	private static int countInstances(ArrayList<String> haystack, String needle) {

		int count = 0;
		for (String straw : haystack) {
			if (straw.equals(needle))
				count++;
		}
		return count;
	}

	private static void validateHeaders(ArrayList<String> headers) {

		// check there's at least one
		if (!(headers.size() > 0))
			throw new RuntimeException("Error parsing CSV, there must be at least one column.");

		// check the headers are unique
		for (String header : headers) {
			if (countInstances(headers, header) > 1)
				throw new RuntimeException("Error parsing CSV, the header '" + header + "' is not unique.");
		}
	}

	private static ArrayList<String> generateDefaultHeaders(int length) {

		ArrayList<String> headers = new ArrayList<String>();
		for (int i = 0; i < length; i++) {
			headers.add(String.valueOf(i));
		}
		return headers;
	}

}
