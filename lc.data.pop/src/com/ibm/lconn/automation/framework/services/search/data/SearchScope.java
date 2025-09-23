package com.ibm.lconn.automation.framework.services.search.data;

import java.util.Date;

import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Link;

public class SearchScope {
	String id;
	String title;
	String summary;
	Date updated;
	Link link;
	
	public SearchScope(String id, String title, String summary, Date updated,
			Link link) {
		super();
		this.id = id;
		this.title = title;
		this.summary = summary;
		this.updated = updated;
		this.link = link;
	}
	
	public SearchScope(Entry entry) {
		this.id = entry.getId().toString();
		this.title = entry.getTitle();
		this.summary = entry.getSummary();
		this.updated = entry.getUpdated();
		this.link = entry.getLink("alternate");
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public Date getUpdated() {
		return updated;
	}
	public void setUpdated(Date updated) {
		this.updated = updated;
	}
	public Link getLink() {
		return link;
	}
	public void setLink(Link link) {
		this.link = link;
	}

	@Override
	public String toString() {
		return "SearchScope [id=" + id + ", title=" + title + ", summary="
				+ summary + ", updated=" + updated + ", link=" + link + "]";
	}

	
	
}
