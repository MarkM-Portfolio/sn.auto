package com.ibm.atmn.waffle.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.security.InvalidParameterException;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Helpers for file IO needed by waffle.
 * 
 * @author Ruairi Pidgeon/Ireland/IBM
 *
 */
public class FileIOHandler {
	
	private static final Logger log = LoggerFactory.getLogger(FileIOHandler.class);

	public FileIOHandler() {

	}

	public static Properties loadClasspathProperties(String filePath) {

		Properties properties = new Properties();
		try {
			properties.load(FileIOHandler.class.getResourceAsStream(filePath));
		} catch (IOException e) {
			log.error("File IO Exception attempting to load file [" + filePath + "] Exception Message: '" +e.getMessage()+"'.");
			e.printStackTrace();
		}
		return properties;
	}

	public static Properties loadExternalProperties(String filePath) {

		FileReader fileReader = null;
		try {
			File confFile = new File(filePath);
			fileReader = new FileReader(confFile);
		} catch (FileNotFoundException e) {
			log.error("FileNotFound Exception attempting to load file [" + filePath + "] Exception Message: '" +e.getMessage()+"'.");
			e.printStackTrace();
		}
		Properties conf = new Properties();
		try {
			conf.load(fileReader);
		} catch (IOException e) {
			log.error("File IO Exception attempting to load file [" + filePath + "] Exception Message: '" +e.getMessage()+"'.");
			e.printStackTrace();
		}
		finally{
			if(fileReader != null){
				try {
					fileReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return conf;
	}

	public static void writeRawDataToFile(String filepath, byte[] data) {

		if(filepath.length() == 0 || filepath == null) throw new InvalidParameterException("No file name");
		writeRawDataToFile(new File(filepath), data);
	}

	public static void writeRawDataToFile(File file, byte[] data) {

		//make folders and file
		File targetFile = file;
		targetFile.getParentFile().mkdirs();
		if (!targetFile.exists()) {
			try {
				targetFile.createNewFile();
			} catch (IOException e) {
				log.warn("File IO Exception attempting to load file [" + file + "] Exception Message: '" +e.getMessage()+"'.");
				e.printStackTrace();
			}
		}

		//write
		FileOutputStream fos = null;
		if (targetFile.exists() && targetFile.isFile() && targetFile.canWrite()) {
			try {
				fos = new FileOutputStream(targetFile);
				fos.write(data);

			} catch (Exception e) {
				log.warn("File IO Exception attempting to write file [" + file + "] Exception Message: '" +e.getMessage()+"'.");
				e.printStackTrace();
				throw new RuntimeException("Failure to write file: " + targetFile.getAbsolutePath());
			} finally {
				if (fos != null) {
					try {
						fos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		else {
			throw new RuntimeException("This file could not be created or is not writable: " + targetFile.getAbsolutePath());
		}

	}

	public static File createFolderFromPath(String folderPath) {

		if(folderPath.length() == 0 || folderPath == null) throw new InvalidParameterException("No folder name");
		return createFolderFromPath(folderPath, false);
	}

	public static File createFolderFromPath(String folderPath, boolean overwrite) {

		if(folderPath.length() == 0 || folderPath == null) throw new InvalidParameterException("No folder name");
		File targetDir = new File(folderPath);
		if (!targetDir.exists()) {
			targetDir.mkdir();
		}
		else {
			if (overwrite) {
				deleteDir(targetDir);
				targetDir.mkdir();
			}
		}
		return targetDir;
	}

	public static Document getXMLConfig(File file) {

		if (!file.exists()) {
			throw new InvalidParameterException("No such file: " + file.getAbsolutePath());
		}
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
			builder = factory.newDocumentBuilder();
			/*
			 * Override the default behavior of reading dtd file in xml file
			 * Reason - http://testng.org/testng-1.0.dtd webside went down and broke tests.
			 */
			builder.setEntityResolver(new EntityResolver() {
				@Override
				public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
					return  new InputSource(new StringReader(""));
				}
			});
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		Document doc = null;
		try {
			doc = builder.parse(file);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		doc.getDocumentElement().normalize();

		return doc;
	}

	public static void writeXmlFile(Document doc, File file) {

		try {
			// Prepare the DOM document for writing
			Source source = new DOMSource(doc);

			// Prepare the output file
			Result result = new StreamResult(file.getPath());

			// Write the DOM document to the file
			Transformer xformer = TransformerFactory.newInstance().newTransformer();
			xformer.transform(source, result);
		} catch (TransformerConfigurationException e) {
			throw new RuntimeException("There was an error writing XML file: " + e.getMessage());
		} catch (TransformerException e) {
			throw new RuntimeException("There was an error writing XML file: " + e.getMessage());
		}
	}

	// Deletes dir, files and subdirs under dir.
	private static boolean deleteDir(File dir) {

		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (String child : children) {
				boolean success = deleteDir(new File(dir, child));
				if (!success) {
					return false;
				}
			}
		}
		return dir.delete();
	}

	public static String readFileToString(File file) {

		if(file==null || !file.exists()) throw new InvalidParameterException("No such file: " + file);
		Reader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			StringBuilder builder = new StringBuilder();
			char[] buffer = new char[4096];
			int charCount;
			//read until end of file
			while ((charCount = reader.read(buffer)) != -1) {
				char[] blob = new char[charCount];
				System.arraycopy(buffer, 0, blob, 0, charCount);
				builder.append(blob);
			}

			return builder.toString();
		} catch (IOException e) {
			log.error("File IO Exception attempting to read file [" + file + "] Exception Message: '" +e.getMessage()+"'.");
			e.printStackTrace();
			throw new RuntimeException("Fatal IOException: " + e.getMessage());
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
		}
	}
}
