package com.ibm.lconn.automation.tdi.tests;

import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeClass;
import org.testng.Assert;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import com.ibm.lconn.automation.tdi.framework.Util.PropertiesInstance;

public class BaseTest {
	
	protected String tdiSolHome;
	protected String dbProperties;
	protected String repoMapProperties;
	protected String ldapProperties;
	private String tdiProperties;

	@BeforeClass
	public void setUpBeforeClass() throws Exception {
		loadConfig("config.properties");
		
		loadDatabaseMap();
		
		LoadDBandLdap();
		
		if(tdiProperties != null)
			loadTdiProp();
	}

	@AfterClass
	public void tearDownAfterClass() throws Exception {
	}

	@BeforeMethod
	public void setUp() throws Exception {
//		loadConfig("config.properties");
//		
//		loadDatabaseMap();
//		
//		LoadDBandLdap();
//		
//		if(tdiProperties != null)
//			loadTdiProp();
	}

	@AfterMethod
	public void tearDown() throws Exception {
	}
	
	private void loadConfig(String mainConfig) {
		Properties mainProperties = PropertiesInstance.getInstance("config/" + mainConfig);
		if(mainProperties == null)
			Assert.fail("Could not load config.properties");
		
		tdiSolHome = mainProperties.getProperty("tdisol_path");
		dbProperties = mainProperties.getProperty("database");
		repoMapProperties = mainProperties.getProperty("db_repo_map");
		ldapProperties = mainProperties.getProperty("ldap");
		tdiProperties = mainProperties.getProperty("tdi");
		
		
		if(tdiSolHome == null)
			Assert.fail("TDI SOL path not set. Set \"tdisol_path\" in config.properties");
		if(dbProperties == null)
			Assert.fail("Database properties file not set. Set \"database\" in config.properties");
		else
			dbProperties = "config/database/" + dbProperties;
		if(repoMapProperties == null)
			Assert.fail("Database repo map properties file not set. Set \"db_repo_map\" in config.properties");
		else
			repoMapProperties = "config/db_repo_map/" + repoMapProperties;
		if(ldapProperties == null)
			Assert.fail("LDAP properties file not set. Set \"ldap\" in config.properties");
		else
			ldapProperties = "config/ldap/" + ldapProperties;
		if(tdiProperties != null)
			tdiProperties = "config/tdi/" + tdiProperties;
	}
	
	private void loadDatabaseMap() throws IOException {
		String mapPath = tdiSolHome + getRepoMap();
		Properties tdiMapProp = PropertiesInstance.getInstance(mapPath);
		
		Properties newMapProp = PropertiesInstance.getInstance(repoMapProperties);
		
		for(String key: newMapProp.stringPropertyNames()){
			if(tdiMapProp.getProperty(key) == null){
				Assert.fail(key + " exists in the new map properties, but not in the current tdi sol");
			}
			else {
				tdiMapProp.setProperty(key, newMapProp.getProperty(key));
			}
		}
		
		storeProperties(tdiMapProp, mapPath);
	}
	
	private void LoadDBandLdap() throws IOException {
		String filePath = tdiSolHome + getTdiProfileProperties();
		Properties tdiProfileProp = PropertiesInstance.getInstance(filePath);
		
		Properties dbProp = PropertiesInstance.getInstance(dbProperties);
		Properties ldapProp = PropertiesInstance.getInstance(ldapProperties);
		
		//db set up
		String[] dbKeys = {"DB_TYPE", "DB_HOST", "DB_PORT", "DB_NAME", "DB_USER", "DB_PASSWORD", "DB_URL", "DB_DRIVER"};
		checkValuesSet(dbProp, dbKeys);
		
		tdiProfileProp.setProperty("dbrepos_jdbc_url", dbProp.getProperty("DB_URL"));
		tdiProfileProp.setProperty("dbrepos_jdbc_driver", dbProp.getProperty("DB_DRIVER"));
		tdiProfileProp.setProperty("dbrepos_username", dbProp.getProperty("DB_USER"));
		tdiProfileProp.setProperty("{protect}-dbrepos_password", dbProp.getProperty("DB_PASSWORD"));
		
		//ldap set up
		String[] ldapKeys = {"LDAP_SERVER", "LDAP_PORT", "LDAP_USER", "LDAP_PASSWORD", "LDAP_BASE", "LDAP_FILTER"};
		checkValuesSet(ldapProp, ldapKeys);
		
		String ldapUrl = "ldap://" + ldapProp.getProperty("LDAP_SERVER") + ":" + ldapProp.getProperty("LDAP_PORT");
		
		tdiProfileProp.setProperty("source_ldap_url", ldapUrl);
		tdiProfileProp.setProperty("source_ldap_user_login", ldapProp.getProperty("LDAP_USER"));
		tdiProfileProp.setProperty("{protect}-source_ldap_user_password", ldapProp.getProperty("LDAP_PASSWORD"));
		tdiProfileProp.setProperty("source_ldap_search_base", ldapProp.getProperty("LDAP_BASE"));
		tdiProfileProp.setProperty("source_ldap_search_filter", ldapProp.getProperty("LDAP_FILTER"));
		
		storeProperties(tdiProfileProp, filePath);
	}
	
	private void loadTdiProp() throws IOException {
		Properties tdiProfileProp;
		String filePath;
		if(System.getProperty("os.name").contains("Windows")){
			filePath = tdiSolHome + "\\profiles_tdi.properties";
			tdiProfileProp = PropertiesInstance.getInstance(filePath);
		}
		else {
			filePath = tdiSolHome + "/profiles_tdi.properties";
			tdiProfileProp = PropertiesInstance.getInstance(filePath);
		}
		
		Properties tdiProp = PropertiesInstance.getInstance(tdiProperties);
		
		for(String key: tdiProp.stringPropertyNames()) {
			if(tdiProfileProp.getProperty(key) != null){
				tdiProfileProp.setProperty(key, tdiProp.getProperty(key));
			}
			else {
				Assert.fail(tdiProfileProp.getProperty(key) + " properties doesn't exist in profiles_tdi.properties");
			}
		}
		
		storeProperties(tdiProfileProp, filePath);
	}
	
	private void checkValuesSet(Properties p, String[] keys){
		for(String key: keys){
			if(p.getProperty(key) == null)
				Assert.fail(key + " is not set.");
		}
	}
	
	private void storeProperties(Properties p, String path) {
		try{
			FileWriter fstream = new FileWriter(path);
			BufferedWriter out = new BufferedWriter(fstream);
			for(String key: p.stringPropertyNames()){
				out.write(key + "=" + p.getProperty(key));
				out.newLine();
			}
			out.close();
		}
		catch(Exception e){
			Assert.fail(e.getMessage());
		}
	}
	
	private String getTdiProfileProperties() {
		if(System.getProperty("os.name").contains("Windows")){
			return "\\profiles_tdi.properties";
		}
		else {
			return "/profiles_tdi.properties";
		}
	}
	
	private String getRepoMap() {
		if(System.getProperty("os.name").contains("Windows")){
			return "\\map_dbrepos_from_source.properties";
		}
		else {
			return "/map_dbrepos_from_source.properties";
		}
	}
	
	protected void updateTdiProfileProperties(Map<String, String> map) throws IOException {
		String filePath = tdiSolHome + getTdiProfileProperties();
		Properties tdiProfileProp = PropertiesInstance.getInstance(filePath);
		for(String key: map.keySet()){
			if(tdiProfileProp.getProperty(key) != null){
				tdiProfileProp.setProperty(key, map.get(key));
			}
			else{
				Assert.fail(tdiProfileProp.getProperty(key) + " properties doesn't exist in profiles_tdi.properties");
			}
		}
		storeProperties(tdiProfileProp, filePath);
	}

}
