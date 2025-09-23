package com.ibm.lconn.automation.framework.services.common.nodes;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Category;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Link;
import org.apache.abdera.model.Person;

import com.ibm.lconn.automation.framework.services.common.StringConstants;

/**
 * LCEntry represents the basic Atom entry for Connections.
 * LCEntry can be extended to implement specific fields/features for different services..
 * 
 * @author Piyush K. Agarwal - pagarwal@us.ibm.com
 */
public abstract class LCEntry {

	private Factory factory;
	private IRI id;
	private String title;
	private String content, contentType;
	private String summary;
	private Date updated;
	private Date published;
	private List<Person> authors;
	private List<Person> contributors;
	private HashMap<String, Link> links;
	private List<Category> tags;
	
	public LCEntry() {
		setFactory(Abdera.getNewFactory());
		authors = new ArrayList<Person>();
		contributors = new ArrayList<Person>();
		links = new HashMap<String, Link>();
		tags = new ArrayList<Category>();
	}
	
	public LCEntry(Entry entry) {
		this();
		setId(entry.getId());
		setTitle(entry.getTitle());
		setContent(entry.getContent());
		//setContentType(entry.getContentType().toString());
		setSummary(entry.getSummary());
		setUpdated(entry.getUpdated());
		setPublished(entry.getPublished());
		setAuthors(entry.getAuthors());
		setContributors(entry.getContributors());
		setTags(entry.getCategories(null));
		
		HashMap<String, Link> linkMap = new HashMap<String, Link>();
		for(Link link : entry.getLinks()) {
			linkMap.put(link.getRel() + ":" + link.getAttributeValue(StringConstants.ATTR_TYPE), link);
		}
		setLinks(linkMap);
	}
	
	public IRI getId() {
		return id;
	}

	public Factory getFactory() {
		return factory;
	}

	public void setFactory(Factory factory) {
		this.factory = factory;
	}

	public void setId(IRI id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
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

	public Date getPublished() {
		return published;
	}

	public void setPublished(Date published) {
		this.published = published;
	}
	
	public List<Person> getAuthors() {
		return authors;
	}

	public void setAuthors(List<Person> authors) {
		this.authors = authors;
	}
	
	public void addAuthor(Person author) {
		authors.add(author);
	}

	public List<Person> getContributors() {
		return contributors;
	}

	public void setContributors(List<Person> contributors) {
		this.contributors = contributors;
	}
	
	public void addContributor(Person contributor) {
		contributors.add(contributor);
	}
	
	public HashMap<String, Link> getLinks() {
		return links;
	}

	public void setLinks(HashMap<String, Link> linkMap) {
		this.links = linkMap;
	}
	
	public void addLink(String newLinkRel, String newLinkType, String newLinkHref) {
		Link link = getFactory().newLink();
		if(newLinkRel != null && newLinkRel.length() != 0) {
			link.setRel(newLinkRel);
		}
		link.setAttributeValue(StringConstants.ATTR_TYPE, newLinkType);
		link.setHref(newLinkHref);
		
		links.put(newLinkRel + ":" + newLinkType, link);
	}
	
	public List<Category> getTags() {
		return tags;
	}

	public void setTags(List<Category> tags) {
		this.tags = new ArrayList<Category>(tags);
	}
	
	public void setTags(String tagsString) {
		if(tagsString != null) {
			String[] tagsArray = tagsString.split(StringConstants.STRING_SPACE_SEPERATOR);
			ArrayList<Category> newTags = new ArrayList<Category>();
			
			for(String tag : tagsArray) {
				Category tagCategory = this.getFactory().newCategory();
				tagCategory.setScheme(null);
				tagCategory.setTerm(tag);
				newTags.add(tagCategory);
			}
			
			this.setTags(newTags);
		}
	}
	
	/* Useful if testing tag strings with spaces */
	public void setTagsNoParsing(String tagsString) {
		if(tagsString != null) {
			ArrayList<Category> newTag = new ArrayList<Category>();
			
			Category tagCategory = this.getFactory().newCategory();
			tagCategory.setScheme(null);
			tagCategory.setTerm(tagsString);
			newTag.add(tagCategory);
			
			this.setTags(newTag);
		}
	}
	
	public String toString() {
		return toEntry().toString();
	}
	
	public Element getGenerator() {
		return null;
	}
	

	public Entry toEntry() {
		return getFactory().newEntry();
	}
	
	public Entry createBasicEntry(Object[] objects, Category[] categories) {
		Entry entry = getFactory().newEntry();
		
		if(getId()!=null)
			entry.setId(getId().toString());
		
		entry.setTitle(getTitle());
		
		if(getSummary() != null && getSummary().length() > 0)
			entry.setSummary(getSummary());
		
		entry.setUpdated(getUpdated());
		entry.setPublished(getPublished());
		
		for(Person author : getAuthors()) {
			entry.addAuthor(author);
		}
		
		for(Person contributor : getContributors()) {
			entry.addContributor(contributor);
		}
		
		for(Category tag : getTags()) {
			if(tag != null)
				entry.addCategory(tag);
		}
		
		for(java.util.Map.Entry<String, Link> link : getLinks().entrySet()) {
			entry.addLink(link.getValue());
		}
		if(categories != null) {
			for(Category category : categories) {
				if(category != null)
					entry.addCategory(category);
			}
		}
		
		if(objects != null) {
			for(Object extension : objects) {
				if(extension != null)
				entry.addExtension((Element) extension);
			}
		}
		
		if(getContent() != null && getContent().length() > 0)
			entry.setContent(getContent());
		
		return entry;
	}
	
	public String getSelfLink() {
		Link link = getLinks().get(StringConstants.REL_SELF + ":" + StringConstants.MIME_NULL);
		return (link != null ? link.getHref().toString() : null);
	}

	public String getEditLink() {
		//application/atom+xml
		Link link = getLinks().get(StringConstants.REL_EDIT + ":" + StringConstants.MIME_ATOM_XML); // + StringConstants.MIME_NULL);
		return (link != null ? link.getHref().toString() : null);
	} 
	
	public String getAlternateLink() {
		Link link = getLinks().get(StringConstants.REL_ALTERNATE + ":" + StringConstants.MIME_TEXT_HTML); // + StringConstants.MIME_NULL);
		return (link != null ? link.getHref().toString() : null);
	} 
	
	public String getRecommendLink(){		
		return getLinks().get(StringConstants.REL_RECOMMENDATIONS +":" + StringConstants.MIME_ATOM_XML).getHref().toString();		
	}
	
	public String getRepliesLink() {
		Link link = getLinks().get(StringConstants.REL_REPLIES + ":" + StringConstants.MIME_ATOM_XML); 
		return (link != null ? link.getHref().toString() : null);		
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LCEntry other = (LCEntry) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}


}
