/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2013                                          */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.lconn.perf.jmeter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang3.StringUtils;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.types.CommandlineJava;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.ibm.lconn.perf.jmeter.beans.Sample;
import com.ibm.lconn.perf.jmeter.beans.TestResults;

/**
 * Runs one or more JMeter test plan sequentially </br> <b>Required:</b></br>
 * -Djmeter.home="D:\wspace\tools\apache-jmeter-2.9"</br>
 * -Djmeter.server.name="lc45linux2.swg.usma.ibm.com"</br> </br> <b>Optional
 * (+default value):</b></br> -Djmeter.result.dir="D:\wspace\!jmeter\news"</br>
 * -Dfailure.property.name="jmeter.test.plan.failed"</br>
 * -Djmeter.server.http.port="80"</br> -Djmeter.server.ssl.port=443</br>
 * 
 */
public class JMeterTestPlanRunner {
	
	private static String CLASS_NAME = JMeterTestPlanRunner.class.getName();
	private static Logger logger = Logger.getLogger(CLASS_NAME);
	

	
	private static final String KEY_JMETER_LOG_FILE_SUFFIX = ".log";
	private static final String KEY_JMETER_RESULT_FILE_SUFFIX = ".jtl";
	private static final String KEY_JMETER_PROPERTIES_FILE_SUFFIX = ".properties";
	private static final String KEY_JMETER_TESTPLAN_FILE_SUFFIX = ".jmx";
	private static final String KEY_JMETER_FILE_PREFIX = "jmeter";
	private static final String KEY_FAILURE_PROPERTY_NAME = "failure.property.name";
	private static final String KEY_JMETER_RESULT_DIR = "jmeter.result.dir";
	private static final String KEY_JMETER_TEST_PLAN_FILE = "com/ibm/lconn/perf/jmeter/int.properties";
	private static final String KEY_JMETER_PROPERTIES_FILE = "com/ibm/lconn/perf/jmeter/jmeter.properties";
	private static final String KEY_JMETER_HOME = "jmeter.home";
	Pattern p = Pattern.compile("^#\\s+.+$",Pattern.MULTILINE);

	/**
	 * The JMeter installation directory
	 */
	private File jmeterHome;

	/**
	 * The property file to use
	 */
	private File jmeterProperties;

	/**
	 * The test plan to execute.
	 */
	private File testPlan;

	/**
	 * The file to log results to
	 */
	private File resultLog;

	/**
	 * The jmeter log file
	 */
	private File jmeterLogFile;

	/**
	 * The directory need to save all result log files
	 */
	private File resultLogDir;

	/**
	 * The main JMeter jar
	 */
	private File jmeterJar;

	/**
	 * Indicate if build to be forcefully failed upon testcase failure
	 */
	private String failureProperty;

//	private String jmeterTestPlanServerName;
//
//	private String jmeterTestPlanServerHttpPort;
//
//	private String jmeterTestPlanServerSSLPort;

	/**
	 * List of result log files used during run.
	 */
	private ArrayList<File> resultLogFiles = new ArrayList<File>();
	
	

	@Before
	public void initProperties() throws Exception 
	{
		
		/** passed parameters **/
		jmeterHome = new File(getSystemProperty(KEY_JMETER_HOME, null));
		failureProperty = getSystemProperty(KEY_FAILURE_PROPERTY_NAME, "jmeter.test.plan.failed");
		jmeterLogFile = File.createTempFile(KEY_JMETER_FILE_PREFIX, KEY_JMETER_LOG_FILE_SUFFIX);
		resultLog = File.createTempFile(KEY_JMETER_FILE_PREFIX, KEY_JMETER_RESULT_FILE_SUFFIX);

//		jmeterTestPlanServerName = getSystemProperty("jmeter.server.name", "localhost");
//		jmeterTestPlanServerHttpPort = getSystemProperty("jmeter.server.http.port", "9082");
//		jmeterTestPlanServerSSLPort = getSystemProperty("jmeter.server.ssl.port", "9445");

		/** picking up properties file and test plan from classpath **/

		jmeterProperties = File.createTempFile(KEY_JMETER_FILE_PREFIX, KEY_JMETER_PROPERTIES_FILE_SUFFIX);		
		writeStringToFile(jmeterProperties, readToString(new ClassPathResource(KEY_JMETER_PROPERTIES_FILE)));		
		testPlan = File.createTempFile(KEY_JMETER_FILE_PREFIX, KEY_JMETER_TESTPLAN_FILE_SUFFIX);
		
		String jmxFile = readToString(new ClassPathResource(KEY_JMETER_TEST_PLAN_FILE));
		int startTestPlan = jmxFile.indexOf("<jmeterTestPlan");
		writeStringToFile(testPlan, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + jmxFile.substring(startTestPlan));
		
		verifyInit();
	}

	public void verifyInit() throws Exception 
	{
		if (!jmeterProperties.exists() || jmeterProperties.length() <= 0)
			throw new RuntimeException("JMeter property file was not copied properly, please check java log and file system permissions");

		if (!testPlan.exists() || testPlan.length() <= 0)
			throw new RuntimeException("JMeter test plan file was not copied properly, please check java log and file system permissions");
	}
	
	//@After
	public void cleanUp() throws Exception {
		deleteQuietly(jmeterProperties);
		deleteQuietly(testPlan);
	}
	

	public static void writeStringToFile(File file, String data) throws IOException {
		writeStringToFile(file, data, "UTF-8");
	}

	public static void writeStringToFile(File file, String data, String encoding) throws IOException {
		OutputStream out = null;
		try {
			out = openOutputStream(file);
			write(data, out, encoding);
		} finally {
			closeQuietly(out);
		}
	}

	public static void write(String data, OutputStream output, String encoding) throws IOException {
		if (data != null)
			if (encoding == null)
				write(data, output);
			else
				output.write(data.getBytes(encoding));
	}

	public static void write(String data, OutputStream output) throws IOException {
		if (data != null)
			output.write(data.getBytes());
	}

	private static FileOutputStream openOutputStream(File file) throws IOException {
		if (file.exists()) {
			if (file.isDirectory()) {
				throw new IOException("File '" + file + "' exists but is a directory");
			}
			if (!(file.canWrite()))
				throw new IOException("File '" + file + "' cannot be written to");
		} else {
			File parent = file.getParentFile();
			if ((parent != null) && (!(parent.exists())) && (!(parent.mkdirs()))) {
				throw new IOException("File '" + file + "' could not be created");
			}
		}
		return new FileOutputStream(file);
	}

	private static boolean deleteQuietly(File file) {
		if (file == null)
			return false;
		try {
			return file.delete();
		} catch (Exception e) {
		}
		return false;
	}

	private static String readToString(Resource r) throws IOException {
		InputStream is = r.getInputStream();
		String ts = toString(r.getInputStream());
		closeQuietly(is);
		return ts;
	}
	
	private static String readToString(InputStream is) throws IOException {
		String ts = toString(is);
		closeQuietly(is);
		return ts;
	}

	private static void closeQuietly(InputStream input) {
		try {
			if (input != null)
				input.close();
		} catch (IOException ioe) {
		}
	}

	public static void closeQuietly(OutputStream output) {
		try {
			if (output != null)
				output.close();
		} catch (IOException ioe) {
		}
	}

	private static String toString(InputStream is) throws IOException {
		StringWriter sw = new StringWriter();
		copy(is, sw);
		return sw.toString();
	}

	private static int copy(Reader input, Writer output) throws IOException {
		long count = copyLarge(input, output);
		if (count > 2147483647L) {
			return -1;
		}
		return (int) count;
	}

	public static long copyLarge(Reader input, Writer output) throws IOException {
		char[] buffer = new char[4096];
		long count = 0L;
		int n = 0;
		while (-1 != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
			count += n;
		}
		return count;
	}

	public static void copy(InputStream input, Writer output) throws IOException {
		InputStreamReader in = new InputStreamReader(input);
		copy(in, output);
	}

	public static String getSystemProperty(String key, String defaultValue) {
		String prop = System.getProperty(key);

		if (StringUtils.isEmpty(prop))
			if (StringUtils.isEmpty(defaultValue))
				throw new RuntimeException("System property [" + key + "] is not defined");
			else
				prop = defaultValue;

		return prop;
	}

	@Test
	public void execute() throws Exception {
		if (jmeterHome == null || !jmeterHome.isDirectory()) {
			throw new RuntimeException("You must set jmeterhome to your JMeter install directory.");
		}

		jmeterJar = new File(jmeterHome.getAbsolutePath() + File.separator + "bin" + File.separator
				+ "ApacheJMeter.jar");

		validate();

		// execute the single test plan if specified
		if (testPlan != null) {
			File resultLogFile = resultLog;
/*			if (resultLogDir != null) {
				String testPlanFileName = testPlan.getName();
				String resultLogFilePath = this.resultLogDir + File.separator
						+ testPlanFileName.replaceFirst("\\.jmx", "\\.jtl");
				resultLogFile = new File(resultLogFilePath);
			}*/
			executeTestPlan(testPlan, resultLogFile);
		}

		checkForFailures();
	}

	/**
	 * Validate the results.
	 */
	private void checkForFailures() throws Exception {
		final List<String> failures = new LinkedList<String>();

		if (failureProperty != null && failureProperty.trim().length() > 0) {
			for (Iterator<File> i = resultLogFiles.iterator(); i.hasNext();) {
				File resultLogFile = i.next();

				JAXBContext jaxbContext = JAXBContext.newInstance(TestResults.class);
				Unmarshaller jaxbUnMarshaller = jaxbContext.createUnmarshaller();

				TestResults results = (TestResults) jaxbUnMarshaller.unmarshal(resultLogFile);

				if (results.getSamplers() != null && results.getSamplers().size() > 0) {
					for (Sample s : results.getSamplers()) {
						String formattedString = s.toCustomizedString();
						if (s.isFail())
							failures.add(formattedString);

						System.out.println(formattedString);
					}
				}
				/**
				 * JMeterSummary summary = new JMeterSummary(resultLogFile,
				 * 500); summary.run();
				 **/
			}

			if (failures.size() > 0) {
				setFailure(getFailureProperty());
				throw new RuntimeException("Execution of integration tests failed! \n"
						+ StringUtils.join(failures, "\n"));
			}

		}
	}

	/**
	 * Validate the task attributes.
	 */
	private void validate() throws Exception {
		if (!(jmeterJar.exists() && jmeterJar.isFile())) {
			throw new Exception("jmeter jar file not found or not a valid file: " + jmeterJar.getAbsolutePath());
		}

		if (resultLog == null && resultLogDir == null) {
			throw new Exception("You must set resultLog or resultLogDir.");
		}

		if (resultLogDir != null && !(resultLogDir.exists() && resultLogDir.isDirectory())) {
			throw new Exception("resultLogDir directory not found or not a valid directory: "
					+ resultLog.getAbsolutePath());
		}
	}

	/**
	 * Execute a JMeter test plan.
	 */
	private void executeTestPlan(File testPlanFile, File resultLogFile) {

		resultLogFiles.add(resultLogFile);

		CommandlineJava cmd = new CommandlineJava();

		cmd.setJar(jmeterJar.getAbsolutePath());

		// non-gui mode
		cmd.createArgument().setValue("-n");
		// the properties file
		if (jmeterProperties != null) {
			cmd.createArgument().setValue("-p");
			cmd.createArgument().setValue(jmeterProperties.getAbsolutePath());
		}
		// the jmeter log file
		if (jmeterLogFile != null) {
			cmd.createArgument().setValue("-j");
			cmd.createArgument().setValue(jmeterLogFile.getAbsolutePath());
		}

		// the test plan file
		cmd.createArgument().setValue("-t");
		cmd.createArgument().setValue(testPlanFile.getAbsolutePath());

		// the result log file
		cmd.createArgument().setValue("-l");
		cmd.createArgument().setValue(resultLogFile.getAbsolutePath());
//
//		/** override server name **/
//		if (StringUtils.isNotEmpty(jmeterTestPlanServerName)) {
//			cmd.createArgument().setValue("-J");
//			cmd.createArgument().setValue("server.name=" + jmeterTestPlanServerName);
//		}
//
//		/** override server port **/
//		if (StringUtils.isNotEmpty(jmeterTestPlanServerHttpPort)) {
//			cmd.createArgument().setValue("-J");
//			cmd.createArgument().setValue("http.port=" + jmeterTestPlanServerHttpPort);
//		}
//
//		/** override server SSL port **/
//		if (StringUtils.isNotEmpty(jmeterTestPlanServerSSLPort)) {
//			cmd.createArgument().setValue("-J");
//			cmd.createArgument().setValue("https.port=" + jmeterTestPlanServerSSLPort);
//		}

		Execute execute = new Execute();
		execute.setCommandline(cmd.getCommandline());

		execute.setWorkingDirectory(new File(jmeterHome.getAbsolutePath() + File.separator + "bin"));

		try {
			execute.execute();
		} catch (IOException e) {
			throw new RuntimeException("JMeter execution failed", e);
		}
	}

	public void setJmeterHome(File jmeterHome) {
		this.jmeterHome = jmeterHome;
	}

	public File getJmeterHome() {
		return jmeterHome;
	}

	public void setJmeterProperties(File jmeterProperties) {
		this.jmeterProperties = jmeterProperties;
	}

	public File getJmeterProperties() {
		return jmeterProperties;
	}

	public void setTestPlan(File testPlan) {
		this.testPlan = testPlan;
	}

	public File getTestPlan() {
		return testPlan;
	}

	public void setResultLog(File resultLog) {
		this.resultLog = resultLog;
	}

	public File getResultLog() {
		return resultLog;
	}

	public File getJmeterLogFile() {
		return jmeterLogFile;
	}

	public void setJmeterLogFile(File jmeterLogFile) {
		this.jmeterLogFile = jmeterLogFile;
	}

	public void setResultLogDir(File resultLogDir) {
		this.resultLogDir = resultLogDir;
	}

	public File getResultLogDir() {
		return this.resultLogDir;
	}

	public void setFailureProperty(String failureProperty) {
		this.failureProperty = failureProperty;
	}

	public String getFailureProperty() {
		return failureProperty;
	}

	public void setFailure(String failureProperty) {
		System.setProperty(failureProperty, "true");
	}

}
