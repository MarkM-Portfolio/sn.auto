package com.ibm.conn.auto.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ibm.atmn.waffle.core.Executor;
import com.ibm.atmn.waffle.extensions.user.User;

/**
 * Config read from Gatekeeper/Highway
 * 
 * @author Ilya
 *
 */
public class GatekeeperConfig {
	private static volatile Map<String, GatekeeperConfig> instance = new HashMap<String, GatekeeperConfig>();
	
	private String organization = null;
	private Map<String, Boolean> settings = new HashMap<String, Boolean>();
	private boolean errorLoading = false;
	
	private static Logger log = LoggerFactory.getLogger(GatekeeperConfig.class);
	
	/**
	 * Loads config using REST API call for On-Premise environment
	 * Loads config using Javascript for Cloud environment
	 * 
	 * NOTE: Method will be deleted when Cloud can use REST API call to load config
	 * 
	 * @param product
	 * @param url
	 * @param wd
	 * @param user - Admin user
	 * @return
	 */
	@Deprecated
	private static synchronized GatekeeperConfig getInstance(String product, String url, Executor driver, User user) {
		String org = "DafaultOrg";
		if(instance.get(org) == null) {
			if(product.equalsIgnoreCase("onprem")) {
				instance.put(org, new GatekeeperConfig(url, user, org));
			} else {
				instance.put(org, new GatekeeperConfig(driver));
     		}
		}
		return instance.get(org);
	}
	
	/**
	 * Loads config using REST API call for On-Premise environment
	 * Loads config using Javascript for Cloud environment
	 * 
	 * NOTE: Method will be deleted when Cloud can use REST API call to load config
	 *
	 * @param driver
	 * @return
	 */
	@Deprecated
	public static synchronized GatekeeperConfig getInstance(Executor driver) {
		return GatekeeperConfig.getInstance("cloud", null, driver, null);
	}
	
	
	
	/**
	 * Calls getInstance(url, user, "DefaultOrg")
	 * 
	 * @param url
	 * @param user -Admin user
	 * @return
	 */
	public static synchronized GatekeeperConfig getInstance(String url, User user) {
		return GatekeeperConfig.getInstance(url, user, "DefaultOrg");
	}
	
	/**
	 * Loads config using REST API call
	 * 
	 * @param url
	 * @param user - Admin user
	 * @param org
	 * @return
	 */
	public static synchronized GatekeeperConfig getInstance(String url, User user, String org) {
		if(instance.get(org) == null) {
			instance.put(org, new GatekeeperConfig(url, user, org));
		}
		return instance.get(org);
	}
	
	/**
	 * Calls getInstance(driver, gkflag)
	 * @param driver
	 * @param gkflag
	 * @return
	 */
	
		
	private GatekeeperConfig(String url, User user, String org) { 
		this.organization = org;
		JsonObject json = this.getGatekeeperSettings(url, user, org);
		if(json == null) {
			this.errorLoading = true;
			log.error("Cannot get GateKeeper json!");
			return;
		}
		try {
			JsonArray settings = json.getAsJsonArray("settings");
			for(int i = 0; i < settings.size(); i++) {
				this.settings.put(settings.get(i).getAsJsonObject().get("name").getAsString(), settings.get(i).getAsJsonObject().get("value").getAsBoolean());
			}
		} catch (Exception e) {
			this.errorLoading = true;
			log.error("Cannot get settings from GateKeeper json: " + e.getMessage());
		}
	}
	
	private GatekeeperConfig(Executor wd) {
		try {
			String json = wd.executeScript("return window.gatekeeperConfig").toString();
			JsonObject jObj = Helper.parseJson(json.trim());
			Iterator<Entry<String, JsonElement> > entryItr = jObj.entrySet().iterator();
			while(entryItr.hasNext()) {
				Entry<String, JsonElement> entry = entryItr.next();
				this.settings.put(entry.getKey(), entry.getValue().getAsBoolean());
			}
		} catch(Exception ex) {
			this.errorLoading = true;
			log.error("Cannot get GateKeeper settings from javascript: " + ex.getMessage());
		}
	}
			
	public static String getFoundationValue(Executor driver, String gkflag){
		return driver.executeScript("return window.navbarData.hasGatekeeper('"+ gkflag+"')").toString();
	}
	
	public Map<String, Boolean> getAllSettings() {
		return this.settings;
	}
	
	/**
	 * Returns value of the given GK key.
	 * @return null if not found
	 */
	public boolean getSetting(String setting) {
		if (!settings.containsKey(setting)) {
			log.info(setting + " is not found in GateKeeper. GateKeeper size = " + this.settings.size());
		} 
		return this.settings.get(setting);
	}
	
	
	public String getOrganization() {
		return this.organization;
	}
	
	public boolean isErrorLoading() {
		return this.errorLoading;
	}
	
	public void printSettings() {
		Set<String> keys = this.settings.keySet();
		for(String key: keys) {
			System.out.println(key + " : " + this.settings.get(key));
		}
	}
	
	private JsonObject getGatekeeperSettings(String serverUrl, User user, String org) {
		String url = serverUrl + "/connections/config/rest/gatekeeper/" + org;
		
		//check to determine if the deployment is tam related or not.
		String settingsString = "";
		String security = TestConfigCustom.getInstance().getSecurityType();
		boolean SITEMINDER = security.equalsIgnoreCase("SITEMINDER");
		boolean TAMSPNEGO = TestConfigCustom.getInstance().getSecurityType().equalsIgnoreCase("TAM_SPNEGO");
		boolean SITEMIDNERSPNEGO = security.equalsIgnoreCase("SITEMINDER_SPNEGO");
		boolean TAM = security.equalsIgnoreCase("TAM");

		if(SITEMINDER||TAMSPNEGO||SITEMIDNERSPNEGO||TAM)
			settingsString = Helper.getRequestStringSecurityJSON(url,user.getUid(),user.getPassword());
		else
			settingsString = Helper.getRequestString(url, user.getUid(), user.getPassword());
		
		return Helper.parseJson(settingsString);
	}

}
