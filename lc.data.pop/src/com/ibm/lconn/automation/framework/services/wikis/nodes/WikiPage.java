package com.ibm.lconn.automation.framework.services.wikis.nodes;

import java.util.ArrayList;
import java.util.Date;

import org.apache.abdera.model.Category;
import org.apache.abdera.model.Entry;

import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.nodes.LCEntry;

/**
 * Wiki Page object contains the elements that make up an Wiki Page.
 * 
 * @author James Cunningham - jamcunni@ie.ibm.com
 */

public class WikiPage extends LCEntry{

	double randNumber = 0;
	int randomInt = 0;
	
	Date created = null;
	Date modified = null;
	
	public WikiPage(String title, String content, String tagsString){
		super();
		
		randNumber = Math.random() * 1000;
		randomInt = (int)randNumber;
		
		setTitle(title + randomInt);
		setContent(content);
		
		String[] tagsArray = tagsString.split(" ");
		ArrayList<Category> newTags = new ArrayList<Category>();
		for(String tag : tagsArray) {
			Category tagCategory = this.getFactory().newCategory();
			tagCategory.setScheme(null);
			tagCategory.setTerm(tag);
			newTags.add(tagCategory);
		}
		
		setTags(newTags);
		
	}
	
	public WikiPage(String title, String content, String tagsString, boolean random){
		super();
		
		setTitle(title);
		setContent(content);
		
		String[] tagsArray = tagsString.split(" ");
		ArrayList<Category> newTags = new ArrayList<Category>();
		for(String tag : tagsArray) {
			Category tagCategory = this.getFactory().newCategory();
			tagCategory.setScheme(null);
			tagCategory.setTerm(tag);
			newTags.add(tagCategory);
		}
		
		setTags(newTags);
		
	}
	
	public WikiPage(String title, String content, String tagsString, Date created, Date modified){
		super();
		
		randNumber = Math.random() * 1000;
		randomInt = (int)randNumber;
		
		setTitle(title + randomInt);
		setContent(content);
		
		String[] tagsArray = tagsString.split(" ");
		ArrayList<Category> newTags = new ArrayList<Category>();
		for(String tag : tagsArray) {
			Category tagCategory = this.getFactory().newCategory();
			tagCategory.setScheme(null);
			tagCategory.setTerm(tag);
			newTags.add(tagCategory);
		}
		
		setTags(newTags);
		
		setCreated(created);
		setModified(modified);
		
	}
	
	public WikiPage(Entry entry) {
		super(entry);
	}
	
	@Override
	public Entry toEntry() {
		Entry entry = getFactory().newEntry();
		
		entry.setTitle(getTitle());
		entry.setContent(getContent());	
		
		if(getCreated() != null)
			entry.addSimpleExtension(StringConstants.TD_CREATED, String.valueOf(getCreated().getTime()));
		
		if(getModified() != null)
			entry.addSimpleExtension(StringConstants.TD_MODIFIED, String.valueOf(getModified().getTime()));
		
		//entry.addSimpleExtension(StringConstants.TD_WIKI_PERMISSIONS, permissions.toString().toLowerCase());
		
		for(Category tag : getTags()) {
			entry.addCategory(tag);
		}
		
		Category isWiki = getFactory().newCategory();
		isWiki.setScheme(StringConstants.WIKIS_SCHEME_TYPE);
		isWiki.setTerm("page");
		isWiki.setLabel("page");
		entry.addCategory(isWiki);
		
		return entry;
	}
	
	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}
	
	public Date getModified() {
		return modified;
	}

	public void setModified(Date modified) {
		this.modified = modified;
	}
}
