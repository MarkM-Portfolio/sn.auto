package com.ibm.lconn.automation.framework.services.common;

import java.util.Date;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Link;

import com.ibm.lconn.automation.framework.services.common.StringConstants.Component;

/**
 * Service object represents a Connections service.
 * Contains the URLs to access services that are enabled on the base server.
 * 
 * @author Piyush K. Agarwal - pagarwal@us.ibm.com
 */
public class ServiceEntry {
	
	private String title;
	private IRI id;
	private Link link;
	private Link sslLink;
	private Date updated;
	private boolean emailHidden;
	private boolean useSSL;
	private Entry entry;

	public ServiceEntry(Entry entry, boolean emailHidden, boolean useSSL) {
		setTitle(entry.getTitle());
		setId(entry.getId());
		setLink(entry.getLink("alternate"));
		setSslLink(entry.getLink("http://www.ibm.com/xmlns/prod/sn/alternate-ssl"));
		setUpdated(entry.getUpdated());
		setEmailHidden(emailHidden);
		setUseSSL(useSSL);
		setEntry(entry);
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public IRI getId() {
		return id;
	}

	public void setId(IRI iri) {
		this.id = iri;
	}

	public Link getLink() {
		return link;
	}

	public void setLink(Link link) {
		this.link = link;
	}
	
	public String getHrefString() {
		return link.getHref().toString();
	}

	public Link getSslLink() {
		return sslLink;
	}

	public void setSslLink(Link link) {
		this.sslLink = link;
	}
	
	public String getSslHrefString() {
		return sslLink.getHref().toString();
	}
	
	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date date) {
		this.updated = date;
	}

	public boolean isEmailHidden() {
		return emailHidden;
	}

	public void setEmailHidden(boolean emailHidden) {
		this.emailHidden = emailHidden;
	}

	public boolean isUseSSL() {
		return useSSL;
	}

	public void setUseSSL(boolean useSSL) {
		this.useSSL = useSSL;
	}
	
	public String getServiceURLString() {
		if(isUseSSL()) {
			return getSslHrefString();
		}
		
		return getHrefString();
	}
	
	public String toString() {
		return getTitle() + " - " + getId() + 
					"\n\tLink: " + getLink() + 
					"\n\tSSL: " + getSslLink();
	}

	public void setEntry(Entry entry) {
		this.entry = entry;
	}

	public Entry getEntry() {
		return entry;
	}

	public Component getComponent() {
		return Enum.valueOf(Component.class, this.getTitle().toUpperCase());
	}
}
