package com.ibm.lconn.automation.framework.services.wikis.nodes;

import java.util.ArrayList;
import java.util.Date;

import org.apache.abdera.model.Category;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;

import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.nodes.LCEntry;

/**
 * Wiki object contains the elements that make up an Wiki.
 * 
 * @author James Cunningham - jamcunni@ie.ibm.com
 */

public class Wiki extends LCEntry{
	
	private String summary;
	double randNumber = 0;
	int randomInt = 0;
	ArrayList<ExtensibleElement> wikiMembers;
	ExtensibleElement sharedWith;
	ExtensibleElement sharedResourceType;
	boolean privateFlag;
	
	public Wiki(String title, String summary, String tagsString, ArrayList<WikiMember> members){
		super();
		
		setPrivateFlag(false);
		setTitle(title);
		
		if(members.size() > 0){
			
			sharedResourceType = getFactory().newElement(StringConstants.TD_WIKI_SHARED_RESOURCE_TYPE);
			sharedResourceType.setText(StringConstants.WIKIS_SHARED_RESOURCE);
			setSharedResourceType(sharedResourceType);
			sharedResourceType.setText(StringConstants.WIKIS_SHARED_RESOURCE);
			
			sharedWith = getFactory().newElement(StringConstants.TD_WIKI_SHARED_WITH);
			
			ArrayList<ExtensibleElement> newMembers = new ArrayList<ExtensibleElement>();
			for(WikiMember wikiMember : members){
				ExtensibleElement member = getFactory().newElement(StringConstants.CA_WIKI_MEMBER);
				member.setAttributeValue(StringConstants.CA_WIKI_MEMBER_ID, wikiMember.getUserid());
				member.setAttributeValue(StringConstants.CA_WIKI_MEMBER_TYPE, String.valueOf(wikiMember.getMemberType()).toLowerCase());
				member.setAttributeValue(StringConstants.CA_WIKI_MEMBER_ROLE, String.valueOf(wikiMember.getRole()).toLowerCase());
				sharedWith.addExtension(member);
				//System.out.println("member.toString(): " + member.toString());
				newMembers.add(member);
			}
			setMembers(newMembers);
			setSharedWith(sharedWith);
		}else{
			setPrivateFlag(true);
		}
		
		setSummary(summary);
		
		if (tagsString != null){
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
	}
	
	public Wiki(String title, String summary, String tagsString, ArrayList<WikiMember> members, boolean random){
		super();

		setPrivateFlag(false);
		if (random) {
			randNumber = Math.random() * 1000;
			randomInt = (int)randNumber;
			setTitle(title + randomInt);
		}
		else {
			setTitle(title);
		}
		
		if(members.size() > 0){
			
			sharedResourceType = getFactory().newElement(StringConstants.TD_WIKI_SHARED_RESOURCE_TYPE);
			sharedResourceType.setText(StringConstants.WIKIS_SHARED_RESOURCE);
			setSharedResourceType(sharedResourceType);
			sharedResourceType.setText(StringConstants.WIKIS_SHARED_RESOURCE);
			
			sharedWith = getFactory().newElement(StringConstants.TD_WIKI_SHARED_WITH);
			
			ArrayList<ExtensibleElement> newMembers = new ArrayList<ExtensibleElement>();
			for(WikiMember wikiMember : members){
				ExtensibleElement member = getFactory().newElement(StringConstants.CA_WIKI_MEMBER);
				member.setAttributeValue(StringConstants.CA_WIKI_MEMBER_ID, wikiMember.getUserid());
				member.setAttributeValue(StringConstants.CA_WIKI_MEMBER_TYPE, String.valueOf(wikiMember.getMemberType()).toLowerCase());
				member.setAttributeValue(StringConstants.CA_WIKI_MEMBER_ROLE, String.valueOf(wikiMember.getRole()).toLowerCase());
				sharedWith.addExtension(member);
				//System.out.println("member.toString(): " + member.toString());
				newMembers.add(member);
			}
			setMembers(newMembers);
			setSharedWith(sharedWith);
		}
		else{
			setPrivateFlag(true);
		}
		
		setSummary(summary);
		
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
	
	public Wiki(String title, String summary, String tagsString, ArrayList<WikiMember> members, boolean random, Date published, Date updated){
		super();

		setPrivateFlag(false);
		if (random) {
			randNumber = Math.random() * 1000;
			randomInt = (int)randNumber;
			setTitle(title + randomInt);
		}
		else {
			setTitle(title);
		}
		
		if(members.size() > 0){
			
			sharedResourceType = getFactory().newElement(StringConstants.TD_WIKI_SHARED_RESOURCE_TYPE);
			sharedResourceType.setText(StringConstants.WIKIS_SHARED_RESOURCE);
			setSharedResourceType(sharedResourceType);
			sharedResourceType.setText(StringConstants.WIKIS_SHARED_RESOURCE);
			
			sharedWith = getFactory().newElement(StringConstants.TD_WIKI_SHARED_WITH);
			
			ArrayList<ExtensibleElement> newMembers = new ArrayList<ExtensibleElement>();
			for(WikiMember wikiMember : members){
				ExtensibleElement member = getFactory().newElement(StringConstants.CA_WIKI_MEMBER);
				member.setAttributeValue(StringConstants.CA_WIKI_MEMBER_ID, wikiMember.getUserid());
				member.setAttributeValue(StringConstants.CA_WIKI_MEMBER_TYPE, String.valueOf(wikiMember.getMemberType()).toLowerCase());
				member.setAttributeValue(StringConstants.CA_WIKI_MEMBER_ROLE, String.valueOf(wikiMember.getRole()).toLowerCase());
				sharedWith.addExtension(member);
				//System.out.println("member.toString(): " + member.toString());
				newMembers.add(member);
			}
			setMembers(newMembers);
			setSharedWith(sharedWith);
		}
		else{
			setPrivateFlag(true);
		}
		
		setSummary(summary);
		
		String[] tagsArray = tagsString.split(" ");
		ArrayList<Category> newTags = new ArrayList<Category>();
		for(String tag : tagsArray) {
			Category tagCategory = this.getFactory().newCategory();
			tagCategory.setScheme(null);
			tagCategory.setTerm(tag);
			newTags.add(tagCategory);
		}
		
		setTags(newTags);
		
		if (published != null){
			setPublished(published);
			setUpdated(updated);
		}
	}	
	
	public Wiki(Entry entry) {
		super(entry);
	}
	
	//@Override
	public Entry toEntry() {
		Entry entry = getFactory().newEntry();
		
		Category isWiki = getFactory().newCategory();
		isWiki.setScheme(StringConstants.WIKIS_SCHEME_TYPE);
		isWiki.setTerm("wiki");
		isWiki.setLabel("wiki");
		entry.addCategory(isWiki);
		
		entry.setTitle(getTitle());
		if(getPrivateFlag() == false){
			entry.addExtension(getSharedResourceType());
			entry.addExtension(getSharedWith());
		}
			
		entry.setSummary(getSummary());	
		
		for(Category tag : getTags()) {
			entry.addCategory(tag);
		}
		
		if (getPublished() != null){
			entry.setPublished(getPublished());
			entry.setUpdated(getUpdated());
		}
		
		return entry;
	}
	
	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}
	
	public ArrayList<ExtensibleElement> getMembers() {
		return wikiMembers;
	}

	public void setMembers(ArrayList<ExtensibleElement> members) {
		this.wikiMembers = members;
	}
	
	public ExtensibleElement getSharedResourceType() {
		return sharedResourceType;
	}

	public void setSharedResourceType(ExtensibleElement sharedResourceType) {
		this.sharedResourceType = sharedResourceType;
	}
	
	public ExtensibleElement getSharedWith() {
		return sharedWith;
	}

	public void setSharedWith(ExtensibleElement sharedWith) {
		this.sharedWith = sharedWith;
	}
	
	public boolean getPrivateFlag() {
		return privateFlag;
	}

	public void setPrivateFlag(boolean privateFlag) {
		this.privateFlag = privateFlag;
	}
	
}
