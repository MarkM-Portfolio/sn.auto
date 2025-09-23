package com.ibm.lconn.automation.framework.services.profiles.nodes;

import java.util.ArrayList;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Categories;
import org.apache.abdera.model.Category;

/**
 * TagsEntry object creates and stores Category objects for strings passed in.
 * The Category objects can then be put in an Atom Categories document and
 * posted to Connections to update a users profile tags.
 * 
 * @author Piyush K. Agarwal - pagarwal@us.ibm.com
 */
public class TagsEntry{
	
	private ArrayList<Category> tags;
	
	public TagsEntry(String tagsString) {

		String[] tagsArray = tagsString.split(" ");
		ArrayList<Category> newTags = new ArrayList<Category>();
		for(String tag : tagsArray) {
			Category tagCategory = Abdera.getNewFactory().newCategory();
			tagCategory.setScheme(null);
			tagCategory.setTerm(tag);
			newTags.add(tagCategory);
		}
		setTags(newTags);
	}
	
	public TagsEntry(Categories categories) {
		ArrayList<Category> newTags = new ArrayList<Category>();
		
		for(Category category : categories.getCategories()) {
			newTags.add(category);
		}
		setTags(newTags);
	}

	public ArrayList<Category> getTags() {
		return tags;
	}

	public void setTags(ArrayList<Category> tags) {
		this.tags = tags;
	}
	
	public Categories toCategories() {
		Categories categories =  Abdera.getNewFactory().newCategories();
		
		for(Category tag : tags) {
			categories.addCategory(tag);
		}
		return categories;
	}

	public void addTag(String newTag) {
		Category tagCategory = Abdera.getNewFactory().newCategory();
		tagCategory.setScheme(null);
		tagCategory.setTerm(newTag);
		tags.add(tagCategory);
	}
}
