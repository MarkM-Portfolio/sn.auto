package com.ibm.lconn.automation.framework.services.search.request;

import java.util.Locale;

import com.ibm.lconn.automation.framework.services.search.data.CategoryConstraint;
import com.ibm.lconn.automation.framework.services.search.data.ContextPath;

public abstract class BaseSearchRequest{
	
	private Locale locale = null;
	private Integer start = null;
	private Integer page = null;
	private Integer pageSize = null;
	private CategoryConstraint[] categoryConstraints = null;
	private CategoryConstraint[] categoryNotConstraints = null;
	private Boolean evidence = null;
	private ContextPath contextPath = ContextPath.atom;
	
	public void setContextPath(ContextPath contextPath) {
		this.contextPath = contextPath;
	}
	public ContextPath getContextPath() {
		return contextPath;
	}
	public Integer getPage() {
		return page;
	}
	public  BaseSearchRequest () {
	 
	}
	public void setPage(Integer page) {
		this.page = page;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	
	public Integer getStart() {
		return start;
	}

	public void setStart(Integer start) {
		this.start = start;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}


	public CategoryConstraint[] getCategoryConstraints() {
		return categoryConstraints;
	}

	public void setCategoryConstraints(CategoryConstraint[] categoryConstraints) {
		this.categoryConstraints = categoryConstraints;
	}
	
	public CategoryConstraint[] getCategoryNotConstraints() {
		return categoryNotConstraints;
	}

	public void setCategoryNotConstraints(CategoryConstraint[] categoryNotConstraints) {
		this.categoryNotConstraints = categoryNotConstraints;
	}
	
	public Boolean getEvidence() {
		return evidence;
	}

	public void setEvidence(Boolean evidence) {
		this.evidence = evidence;
	}

}

