package com.ibm.lconn.automation.framework.services.catalog;


public class CatalogView {

	public final static String CATALOG_VIEW_KEY = "key";
	public final static String CATALOG_VIEW_TITLE = "title";
	public final static String CATALOG_VIEW_DESCRIPTION = "description";
	public final static String CATALOG_VIEW_PATH = "path";
	public final static String CATALOG_VIEW_ORDER = "order";

	private String key;
	private String title;
	private String description;
	private String path;
	private int order;

	public CatalogView(String key, String title, String description, String path, int order) {
		super();
		this.key = key;
		this.title = title;
		this.description = description;
		this.path = path;
		this.order = order;
	}

	public String getKey() {
		return key;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public String getPath() {
		return path;
	}

	public int getOrder() {
		return order;
	}

}
