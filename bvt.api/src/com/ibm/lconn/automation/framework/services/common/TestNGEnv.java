package com.ibm.lconn.automation.framework.services.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import ch.qos.logback.classic.Level;

/**
 * Helper class that set Eclipse TestNG Environment (all optional value in one
 * place).
 * 
 * @author Ping Wang - wangpin@us.ibm.com
 */
public class TestNGEnv {

	/* BVT Server URL */
	 public static String SERVER_URL = "https://bvtdb2.cnx.cwp.pnp-hcl.com";
	// public static String SERVER_URL = "https://bvtsql.cnx.cwp.pnp-hcl.com";
	// public static String SERVER_URL = "https://bvtoracle.cnx.cwp.pnp-hcl.com";
	// public static String SERVER_URL = "https://bvt1.cnx.cwp.pnp-hcl.com";
	// public static String SERVER_URL = "https://bvtdmgr.cnx.cwp.pnp-hcl.com";
	/* Pool */
	// public static String SERVER_URL = "https://lcauto200.cnx.cwp.pnp-hcl.com";
	 
	private final static Logger LOGGER = LoggerFactory.getLogger(TestNGEnv.class.getName());
	private final static java.util.logging.Logger LOG = java.util.logging.Logger.getLogger(TestNGEnv.class.getName());
	static public boolean instance_flag = false;

	public static void setTestEnv() {

		URLConstants.SERVER_URL = SERVER_URL;
		StringConstants.DEPLOYMENT_TYPE = StringConstants.DeploymentType.ON_PREMISE;
		//StringConstants.DEPLOYMENT_TYPE = StringConstants.DeploymentType.SMARTCLOUD;
		
		StringConstants.LOG_LEVEL = StringConstants.Logger.INFO.toString();
		//StringConstants.LOG_LEVEL = StringConstants.Logger.DEBUG.toString();

		StringConstants.MODERATION_ENABLED = false;// true;
		StringConstants.VMODEL_ENABLED = false;
		StringConstants.IMPERSONATION_ENABLED = true;
		// StringConstants.AUTHENTICATION = "basic"; //always
		SetUsersId.usersNumber = 18;
		
		setLog();
		
	}

	@Parameters({ "SERVER_URL", "DEPLOYMENT_TYPE", "MODERATION_ENABLED",
			"IMPERSONATION_ENABLED", "VMODEL_ENABLED", "AUTHENTICATION" })
	@BeforeSuite
	public void testNGParameters(@Optional String url, String deployType,
			String moderation, String impersonation, String vmode, String authentication) {
		
		if (instance_flag) {
			return;
		}

		System.out.println(" -- --- Read Parameters in config xml----------- ");
		System.out.println(" --           SERVER_URL is : " + url);

		if (!(url == null || url.isEmpty() || url
				.equalsIgnoreCase("CI_PREBVT_BROWSER_URL"))) {
			URLConstants.SERVER_URL = url;
			if (deployType.equalsIgnoreCase("ON_PREMISE")) {
				StringConstants.DEPLOYMENT_TYPE = StringConstants.DeploymentType.ON_PREMISE;
			} else if (deployType.equalsIgnoreCase("SMARTCLOUD")) {
				StringConstants.DEPLOYMENT_TYPE = StringConstants.DeploymentType.SMARTCLOUD;
			}

			if (moderation.equalsIgnoreCase("false")) {
				StringConstants.MODERATION_ENABLED = false;
			} else {
				StringConstants.MODERATION_ENABLED = true;
			}

			if (impersonation.equalsIgnoreCase("true")) {
				StringConstants.IMPERSONATION_ENABLED = true;
			} else {
				StringConstants.IMPERSONATION_ENABLED = false;
			}

			if (vmode.equalsIgnoreCase("true")) {
				StringConstants.VMODEL_ENABLED = true;
			} else {
				StringConstants.VMODEL_ENABLED = false;
			}
			
			StringConstants.setAuthentication(authentication);
			setLog();
			
			System.out.println(" --       VMODEL_ENABLED is : "
					+ StringConstants.VMODEL_ENABLED);
			System.out.println(" --      DEPLOYMENT_TYPE is : "
					+ StringConstants.DEPLOYMENT_TYPE.toString());
			System.out.println(" --   MODERATION_ENABLED is : "
					+ StringConstants.MODERATION_ENABLED);
			System.out.println(" --IMPERSONATION_ENABLED is : "
					+ StringConstants.IMPERSONATION_ENABLED);
			System.out.println(" --            LOG_LEVEL is : "
					+ StringConstants.LOG_LEVEL);
			System.out.println(" -- ---- ----------------------------------- ");
		} else {
			setTestEnv();
			System.out.println(" -- --- From setTestEnv -------------------- ");
			System.out.println(" --           SERVER_URL is : " + SERVER_URL);
			System.out.println(" --      DEPLOYMENT_TYPE is : "
					+ StringConstants.DEPLOYMENT_TYPE.toString());
			System.out.println(" --   MODERATION_ENABLED is : "
					+ StringConstants.MODERATION_ENABLED);
			System.out.println(" --IMPERSONATION_ENABLED is : "
					+ StringConstants.IMPERSONATION_ENABLED);
			System.out.println(" --       VMODEL_ENABLED is : "
					+ StringConstants.VMODEL_ENABLED);
			System.out.println(" --            LOG_LEVEL is : "
					+ StringConstants.LOG_LEVEL);
			System.out.println(" -----------------------------------------");

		}

		instance_flag = true;
	}

	private static void setLog() {
		ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger)LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
		if (StringConstants.LOG_LEVEL.equalsIgnoreCase(StringConstants.Logger.DEBUG.toString())){
			rootLogger.setLevel(Level.DEBUG);
			LOG.setLevel(java.util.logging.Level.FINE);
		} else if (StringConstants.LOG_LEVEL.equalsIgnoreCase(StringConstants.Logger.TRACE.toString())){
			rootLogger.setLevel(Level.TRACE);
			LOG.setLevel(java.util.logging.Level.FINER);
		} else {
			rootLogger.setLevel(Level.INFO);
			LOG.setLevel(java.util.logging.Level.INFO);
		}
	}
}
