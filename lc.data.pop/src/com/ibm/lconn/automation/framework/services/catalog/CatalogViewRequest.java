package com.ibm.lconn.automation.framework.services.catalog;

import java.util.Locale;


public class CatalogViewRequest{
	
	public enum Location{banner,menu};
	
	private static final String PARAM_NAME_LOCALE = "locale";
	private static final String PARAM_NAME_LOCATION = "location";
	
	private Location location = null;
	private Locale locale = null;
	
	public CatalogViewRequest() {
	}
	

	public Location getLocation() {
		return location;
	}


	public void setLocation(Location location) {
		this.location = location;
	}


	public Locale getLocale() {
		return locale;
	}


	public void setLocale(Locale locale) {
		this.locale = locale;
	}


	public static String buildPath(String url,
			CatalogViewRequest catalogViewRequest) {
		StringBuffer catalogPath = new StringBuffer();
		catalogPath.append(url);
		
		if(catalogPath.toString().lastIndexOf("?") == -1){
			catalogPath.append("?");
		}else{
			catalogPath.append("&");
		}
		addLocale(catalogPath ,catalogViewRequest.getLocale());
		addLocation(catalogPath, catalogViewRequest.getLocation());
		catalogPath.deleteCharAt(catalogPath.length() - 1);
		return catalogPath.toString();
	}
	
	private static void addLocale(StringBuffer catalogPath ,Locale locale){
		addParameter(catalogPath, PARAM_NAME_LOCALE, locale);
	}
	
	private static void addLocation(StringBuffer catalogPath, Location location){
		addParameter(catalogPath, PARAM_NAME_LOCATION, location);
	}
	
	private static void addParameter(StringBuffer catalogPath ,String paramName, Object paramValue){
		if(paramValue != null){
			catalogPath.append(paramName + "=");
			catalogPath.append(paramValue);
			catalogPath.append("&");
		}	
	}

}

