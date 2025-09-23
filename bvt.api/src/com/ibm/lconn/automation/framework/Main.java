package com.ibm.lconn.automation.framework;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.parsers.ParserConfigurationException;

import org.testng.TestNG;
import org.testng.xml.Parser;
import org.testng.xml.XmlSuite;
import org.xml.sax.SAXException;

import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Component;
import com.ibm.lconn.automation.framework.services.common.StringConstants.DeploymentType;
import com.ibm.lconn.automation.framework.services.common.StringConstants.IndexNowOnCloudType;
import com.ibm.lconn.automation.framework.services.common.URLConstants;

/**
 * BVT API
 * 
 * @author Ping Wang - wangpin@us.ibm.com
 */
public class Main {

	public static boolean RUNNING_ALL_COMPONENTS = true;

	public static String RUN_COMPONENTS = "ACTIVITIES,AUTHCONNECTOR,BLOGS,COMMUNITIES,CRE,DOGEAR,FILES,FORUMS,PROFILES,PROFILESADMIN,WIKIS,MICROBLOGS,ACTIVITYSTREAMS,ACTIVITYSTREAMSEARCH,COMMUNITIESCATALOG,METRICS";

	/*public static boolean ACTIVITIES, BLOGS, COMMUNITIES, CRE, DOGEAR, FILES,
			FORUMS, PROFILES, PROFILESADMIN, WIKIS, MICROBLOGS,
			ACTIVITYSTREAMS, ACTIVITYSTREAMSEARCH, SEARCH, CONTENTSEARCH,
			COMMUNITIESCATALOG;*/

	static boolean parseError = false;

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		for (int nIndex = 0; nIndex < args.length; nIndex += 2) {

			// all parameters are key=value pairs
			if ((nIndex + 2) > args.length) {
				parseError = true;
				System.err.println("Missing value for parameter: "
						+ args[nIndex]);
				System.err.println("");
				break;
			} else if (args[nIndex].equals("-server")) {
				URLConstants.setServerURL(args[nIndex + 1]);
			} else if (args[nIndex].equals("-components")) {
				RUN_COMPONENTS = args[nIndex + 1];
				RUNNING_ALL_COMPONENTS = false;
			} else if (args[nIndex].equals("-moderation_enabled")) {
				StringConstants.MODERATION_ENABLED = args[nIndex + 1]
						.equalsIgnoreCase("true");
			} else if (args[nIndex].equals("-impersonation_enabled")) {
				StringConstants.IMPERSONATION_ENABLED = args[nIndex + 1]
						.equalsIgnoreCase("true");
			} else if (args[nIndex].equals("-vmodel_enabled")) {
				StringConstants.VMODEL_ENABLED = args[nIndex + 1]
						.equalsIgnoreCase("true");
			} else if (args[nIndex].equals("-deployment_type")) {
				String deployType = args[nIndex + 1];
				if (deployType.equalsIgnoreCase("smartcloud")) {
					StringConstants.DEPLOYMENT_TYPE = DeploymentType.SMARTCLOUD;

				} else if (deployType.equalsIgnoreCase("on_premise")) {
					StringConstants.DEPLOYMENT_TYPE = DeploymentType.ON_PREMISE;

				} else if (deployType.equalsIgnoreCase("multi_tenant")) {
					StringConstants.DEPLOYMENT_TYPE = DeploymentType.MULTI_TENANT;

				} else {
					System.err.println("Unknown deployment type value: "
							+ args[nIndex + 1]);
					System.err
							.println("Accepted values are: smartcloud, on_premise or multi_tenant");
					System.err.println("");
					break;
				}
			} else if (args[nIndex].equals("-loggerlevel")) {
				StringConstants.LOG_LEVEL = args[nIndex + 1];
				if (!StringConstants.LOG_LEVEL
						.equalsIgnoreCase(StringConstants.Logger.INFO
								.toString())
						&& !StringConstants.LOG_LEVEL
								.equalsIgnoreCase(StringConstants.Logger.DEBUG
										.toString())
						&& !StringConstants.LOG_LEVEL
								.equalsIgnoreCase(StringConstants.Logger.TRACE
										.toString())) {
					parseError = true;
					System.err.println("Unknown Logger parameter: "
							+ args[nIndex]);
					System.err.println("should be info/debug");
					System.err.println("");
					break;
				}
			} else if (args[nIndex].equals("-index_now_type")) {
				String indexType = args[nIndex + 1];
				if (indexType.equalsIgnoreCase("wsadmin")) {
					StringConstants.INDEX_NOW_ON_CLOUD_TYPE = IndexNowOnCloudType.WSADMIN;

				}
				System.out.println(" IndexNowType: "
						+ StringConstants.INDEX_NOW_ON_CLOUD_TYPE);
			} else if (args[nIndex].equals("-authentication")) {
				StringConstants.AUTHENTICATION = args[nIndex + 1];
				if (!StringConstants.AUTHENTICATION
						.equalsIgnoreCase(StringConstants.Authentication.BASIC
								.toString())
						&& !StringConstants.AUTHENTICATION
								.equalsIgnoreCase(StringConstants.Authentication.FORM
										.toString())
						&& !StringConstants.AUTHENTICATION
								.equalsIgnoreCase(StringConstants.Authentication.OAUTH
										.toString())) {
					parseError = true;
					System.err.println("Unknown Authentication parameter: "
							+ args[nIndex]);
					System.err.println("should be basic/form");
					System.err.println("");
					break;
				}
			} else {
				parseError = true;
				System.err.println("Unknown parameter: " + args[nIndex]);
				System.err.println("");
				break;
			}
		}

		if (parseError) {
			System.err.println("Usage example: all parameters are optional");
			System.err
					.println(">dp.sh [-server Servername -username userID -password userPwd -components component1,component2,...,wikis] -moderation_enabled true|false ");
			System.err.println(" ");
			System.err
					.println("default server : https://lc45linux1.swg.usma.ibm.com ");
			System.err
					.println("default components : all supported components ");
			System.err.println("default moderation_enabled : false ");
			System.exit(345);
		}

		// SetProfileData.SetProfileDataOnce();

		System.out
				.println(" ----- From Input & Default -------------------------------------------------------");
		System.out.println(" --  Running on Server : "
				+ URLConstants.SERVER_URL);
		System.out.println(" --  Deployment Type : "
				+ StringConstants.DEPLOYMENT_TYPE.toString());
		System.out.println(" --  VModel Enabled : "
				+ StringConstants.VMODEL_ENABLED);
		System.out.println(" --  Moderation Enabled : "
				+ StringConstants.MODERATION_ENABLED);
		System.out.println(" --  Impersonation Enabled : "
				+ StringConstants.IMPERSONATION_ENABLED);

		System.out.println(" --  Running Components : " + RUN_COMPONENTS);
		System.out
				.println(" -----------------------------------------------------------------");

		// Create temp dir for config xml files
		String tempDir = "temp";

		File tempFolder = new File(tempDir);
		if (!tempFolder.isDirectory()) {
			if (tempFolder.exists())
				tempFolder.delete();
			tempFolder.mkdir();
		}
		cleanDirectory(tempFolder);

		// Copy xml files from test_config to temp
		File test_config = new File("test_config");
		String srcFile = "";
		String destFile = "";
		if (test_config.isDirectory()) {
			File[] files = test_config.listFiles();
			for (File file : files) {

				srcFile = file.getPath();
				if (file.isFile() && srcFile.contains(".xml")) {
					destFile = srcFile.replace(
							srcFile.substring(0,
									srcFile.indexOf(File.separator)), tempDir);

					copyfile(srcFile, destFile);
					UpdateConfigXML(destFile);
				}
			}
		} else {
			System.err.println("Couldn't find config xml files");
			System.exit(1);
		}

		String configXml = "";

		if (RUNNING_ALL_COMPONENTS) {
			if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.ON_PREMISE) {
				// srcFile = "test_config/BVTOnPremise.xml";
				configXml = "temp/BVTOnPremise.xml";
			} else if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
				// srcFile = "test_config/BVTOnCloud.xml";
				configXml = "temp/BVTOnCloud.xml";
			}

		} else {// get all running xml files from Suites.xml

			String destSuitesFile = "temp/Suites.xml";

			StringTokenizer st = new StringTokenizer(RUN_COMPONENTS, ",");
						
			while (st.hasMoreElements()) {
				String compName = st.nextElement().toString();
				if (compName.equalsIgnoreCase(Component.ACTIVITIES.toString())) {

					UpdateSuitesXML(destSuitesFile,
							Component.ACTIVITIES.toString());
				} else if (compName.equalsIgnoreCase(Component.ACTIVITYSTREAMS
						.toString())) {

					UpdateSuitesXML(destSuitesFile,
							Component.ACTIVITYSTREAMS.toString());
				} else if (compName
						.equalsIgnoreCase(Component.ACTIVITYSTREAMSEARCH
								.toString())) {

					UpdateSuitesXML(destSuitesFile,
							Component.ACTIVITYSTREAMSEARCH.toString());
				} else if (compName.equalsIgnoreCase(Component.AUTHCONNECTOR
						.toString())) {

					UpdateSuitesXML(destSuitesFile,
							Component.AUTHCONNECTOR.toString());
				} else if (compName
						.equalsIgnoreCase(Component.BLOGS.toString())) {

					UpdateSuitesXML(destSuitesFile, Component.BLOGS.toString());
				} else if (compName.equalsIgnoreCase(Component.COMMUNITIES
						.toString())) {

					UpdateSuitesXML(destSuitesFile,
							Component.COMMUNITIES.toString());
				} else if (compName
						.equalsIgnoreCase(Component.COMMUNITIESCATALOG
								.toString())) {

					UpdateSuitesXML(destSuitesFile,
							Component.COMMUNITIESCATALOG.toString());
				} else if (compName.equalsIgnoreCase(Component.CRE.toString())) {

					UpdateSuitesXML(destSuitesFile, Component.CRE.toString());
				} else if (compName.equalsIgnoreCase(Component.DOGEAR
						.toString())) {

					UpdateSuitesXML(destSuitesFile, Component.DOGEAR.toString());
				} else if (compName
						.equalsIgnoreCase(Component.FILES.toString())) {

					UpdateSuitesXML(destSuitesFile, Component.FILES.toString());
				} else if (compName.equalsIgnoreCase(Component.FORUMS
						.toString())) {

					UpdateSuitesXML(destSuitesFile, Component.FORUMS.toString());
				} else if (compName.equalsIgnoreCase(Component.MICROBLOGS
						.toString())) {

					UpdateSuitesXML(destSuitesFile,
							Component.MICROBLOGS.toString());
				} else if (compName.equalsIgnoreCase(Component.PROFILES
						.toString())) {

					UpdateSuitesXML(destSuitesFile,
							Component.PROFILES.toString());
				} else if (compName.equalsIgnoreCase(Component.PROFILESADMIN
						.toString())) {

					UpdateSuitesXML(destSuitesFile,
							Component.PROFILESADMIN.toString());
				} else if (compName.equalsIgnoreCase(Component.SEARCH
						.toString())) {

					UpdateSuitesXML(destSuitesFile, Component.SEARCH.toString());
				} else if (compName.equalsIgnoreCase(Component.SOLR.toString())) {

					UpdateSuitesXML(destSuitesFile, Component.SOLR.toString());
				} else if (compName
						.equalsIgnoreCase(Component.SWITCHBOX.toString())) {

					UpdateSuitesXML(destSuitesFile, Component.SWITCHBOX.toString());
				} else if (compName
						.equalsIgnoreCase(Component.WIKIS.toString())) {

					UpdateSuitesXML(destSuitesFile, Component.WIKIS.toString());
				} else if (compName
						.equalsIgnoreCase(Component.METRICS.toString())) {

					UpdateSuitesXML(destSuitesFile, Component.METRICS.toString());
				}else if (compName
						.equalsIgnoreCase(Component.ORIENTME.toString())) {

					UpdateSuitesXML(destSuitesFile, Component.ORIENTME.toString());
				}   
				else {
					System.err.println("Wrong Component : " + compName);
				}
			}
			configXml = destSuitesFile;

		}

		TestNG testng = new TestNG();
		List<XmlSuite> suite;
		try {
			suite = (List<XmlSuite>) (new Parser(configXml).parse());
			testng.setXmlSuites(suite);
			//testng.setSuiteThreadPoolSize(new StringTokenizer(RUN_COMPONENTS, ",").countTokens());
			testng.run();
			
			System.exit(0);
			
//		} catch (ParserConfigurationException e) {
//			e.printStackTrace();
//		} catch (SAXException e) {
//			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}

	}

	private static void UpdateConfigXML(String fileName) {
		// UpdateXML parameters and group value

		ReplaceString("CI_PREBVT_BROWSER_URL", URLConstants.SERVER_URL,
				fileName);
		ReplaceString("ON_PREMISE", StringConstants.DEPLOYMENT_TYPE.toString(),
				fileName);
		ReplaceString(
				"VMODEL_ENABLED\" value=\"false",
				"VMODEL_ENABLED\" value=\""
						+ String.valueOf(StringConstants.VMODEL_ENABLED),
				fileName);
		ReplaceString(
				"MODERATION_ENABLED\" value=\"false",
				"MODERATION_ENABLED\" value=\""
						+ String.valueOf(StringConstants.MODERATION_ENABLED),
				fileName);
		ReplaceString(
				"IMPERSONATION_ENABLED\" value=\"true",
				"IMPERSONATION_ENABLED\" value=\""
						+ String.valueOf(StringConstants.IMPERSONATION_ENABLED),
				fileName);
		ReplaceString("<!--<include name=\"moderation\" />-->",
				"<include name=\"moderation\" />", fileName);
		
		if ( !StringConstants.IMPERSONATION_ENABLED ){
			ReplaceString("<!-- IMPERSONATEDTEST START -->",
					"<!-- IMPERSONATEDTEST START ", fileName);
			ReplaceString("<!-- IMPERSONATEDTEST END -->",
					" IMPERSONATEDTEST END -->", fileName);
		}
		

	}

	private static void UpdateSuitesXML(String fileName, String component) {
		// UpdateXML to activate the test
		if (component.equalsIgnoreCase(StringConstants.Component.ACTIVITIES
				.toString()))
			component = "Activities";
		else if (component
				.equalsIgnoreCase(StringConstants.Component.ACTIVITYSTREAMS
						.toString()))
			component = "ActivityStreams";
		else if (component
				.equalsIgnoreCase(StringConstants.Component.ACTIVITYSTREAMSEARCH
						.toString())) {
			if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.ON_PREMISE) {
				component = "ASS";
			} else if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
				component = "ASSOnCloud";
			}
		} else if (component
				.equalsIgnoreCase(StringConstants.Component.AUTHCONNECTOR
						.toString()))
			component = "AuthConnector";
		else if (component.equalsIgnoreCase(StringConstants.Component.BLOGS
				.toString()))
			component = "Blogs";
		else if (component
				.equalsIgnoreCase(StringConstants.Component.COMMUNITIES
						.toString()))
			component = "Communities";
		else if (component
				.equalsIgnoreCase(StringConstants.Component.COMMUNITIESCATALOG
						.toString()))
			component = "Catalog";
		else if (component.equalsIgnoreCase(StringConstants.Component.CRE
				.toString()))
			component = "Cre";
		else if (component.equalsIgnoreCase(StringConstants.Component.DOGEAR
				.toString()))
			component = "Dogear";
		else if (component.equalsIgnoreCase(StringConstants.Component.FILES
				.toString()))
			component = "Files";
		else if (component.equalsIgnoreCase(StringConstants.Component.FORUMS
				.toString()))
			component = "Forums";
		else if (component
				.equalsIgnoreCase(StringConstants.Component.MICROBLOGS
						.toString()))
			component = "UBlogs";
		else if (component.equalsIgnoreCase(StringConstants.Component.PROFILES
				.toString()))
			component = "Profiles";
		else if (component
				.equalsIgnoreCase(StringConstants.Component.PROFILESADMIN
						.toString()))
			component = "ProfilesAdmin";
		else if (component.equalsIgnoreCase(StringConstants.Component.SEARCH
				.toString())) {
			if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.ON_PREMISE) {
				component = "SearchOnPremise";
			} else if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
				component = "SearchOnCloud";
			}
		} else if (component.equalsIgnoreCase(StringConstants.Component.SOLR
				.toString()))
			component = "SearchSolr";
		else if (component.equalsIgnoreCase(StringConstants.Component.SWITCHBOX
				.toString()))
			component = "Switchbox";
		else if (component.equalsIgnoreCase(StringConstants.Component.WIKIS
				.toString()))
			component = "Wikis";
		else if (component.equalsIgnoreCase(StringConstants.Component.METRICS
				.toString()))
			component = "Metrics";
		else if (component.equalsIgnoreCase(StringConstants.Component.ORIENTME
				.toString()))
			component = "OrientMe";
		ReplaceString("<!--<suite-file path=\"" + component + ".xml\" />-->",
				"<suite-file path=\"" + component + ".xml\" />", fileName);

	}

	static void ReplaceString(String old, String replacement,
			String readFileName, String newDir, String writeFileName) {
		try {
			File file = new File(readFileName);
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = "", oldtext = "";
			while ((line = reader.readLine()) != null) {
				oldtext += line + "\r\n";
			}
			reader.close();

			// replace a word in a file
			// String newtext = oldtext.replaceAll("drink", "Love");

			// To replace a line in a file
			String newtext = oldtext.replaceAll(old, replacement);

			FileWriter writer = new FileWriter(newDir + "/" + writeFileName);
			writer.write(newtext);
			writer.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	static void ReplaceString(String old, String replacement, String fileName) {
		try {
			File file = new File(fileName);
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = "", oldtext = "";
			while ((line = reader.readLine()) != null) {
				oldtext += line + "\r\n";
			}
			reader.close();
			// replace a word in a file
			// String newtext = oldtext.replaceAll("drink", "Love");

			// To replace a line in a file
			String newtext = oldtext.replaceAll(old, replacement);

			FileWriter writer = new FileWriter(fileName);
			writer.write(newtext);
			writer.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	static void cleanDirectory(File dir) {
		for (File file : dir.listFiles()) {
			if (file.isDirectory())
				cleanDirectory(file);
			file.delete();
		}
	}

	private static void copyfile(String srFile, String dtFile) {
		try {
			File f1 = new File(srFile);
			File f2 = new File(dtFile);
			InputStream in = new FileInputStream(f1);

			// For Append the file.
			// OutputStream out = new FileOutputStream(f2,true);

			// For Overwrite the file.
			OutputStream out = new FileOutputStream(f2);

			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();

		} catch (FileNotFoundException ex) {
			System.out
					.println(ex.getMessage() + " in the specified directory.");
			System.exit(0);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
}
