package com.ibm.lconn.automation.framework.services.search.nodes;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.apache.abdera.model.Category;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Person;

import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Permissions;
import com.ibm.lconn.automation.framework.services.common.nodes.LCEntry;
import com.ibm.lconn.automation.framework.services.search.data.Application;

/**
 * Activity object contains the elements that make up a Connections Search Result.
 * 
 * @author Piyush K. Agarwal - pagarwal@us.ibm.com
 */
/**
 * @author nogat
 *
 */
public class SearchResult extends LCEntry {

	private Category isSearch;				/** (Required) Identifies the Atom entry as a search result */
	private List<Category> components;				/** (Optional) Identifies the IBM Connections application in which the result was found. */
	private String fieldDueDate ;
	private Element relevenceScore;
	private Element communityUUID;
	
	
	


	private Category accessControl;
	private Category documentType;
	private Application application;
	
	/**
	 * Constructor to create an Activity based object based on valid Activity Atom Entry object.
	 * 
	 * @param entry an Atom entry that represents a Activity.
	 */
	public SearchResult(Entry entry) {
		super(entry);
		
		List<Category> types = entry.getCategories(StringConstants.SCHEME_TYPE);
		
		if(types.size() > 0) {
			String term = types.get(0).getTerm();
			if(term.equals(StringConstants.STRING_SEARCH_LOWERCASE)) {
				setIsSearch(true);
			}
		}
		
		List<Category> componentList = entry.getCategories(StringConstants.SCHEME_COMPONENT);
		setComponents(componentList);
		
		setRelevenceScore(entry.getExtension(StringConstants.RELEVENCE_SCORE));
		setDueDateField(entry);
		List<Category> accessControlCategories = entry.getCategories(StringConstants.SCHEME_ACCESS);
		assertEquals("Category with " + StringConstants.SCHEME_ACCESS + " scheme should appear only once per entry", 1, accessControlCategories.size());
		setAccessControl(accessControlCategories.get(0));
		
		List<Category> documentTypeCategories = entry.getCategories(StringConstants.SCHEME_DOC_TYPE);
		assertEquals("Category with " + StringConstants.SCHEME_DOC_TYPE + " scheme should appear only once per entry", 1, documentTypeCategories.size());
		setDocumentType(documentTypeCategories.get(0));
		Element communityUUID = entry.getExtension(StringConstants.SNX_COMMUNITY_UUID);
		setCommunityUUID(communityUUID);
	}

	private void determineApp() {
		for (Category component : components){
			if(StringConstants.SCHEME_COMPONENT_TERM_COMMUNITIES.equals(component.getTerm())){
				application = Application.community;
				
			} 
			if(StringConstants.SCHEME_COMPONENT_TERM_ACTIVITIES.equals(component.getTerm())){
				application = Application.activity;
				return;
			} 
			if(StringConstants.SCHEME_COMPONENT_TERM_FILES.equals(component.getTerm())){
				application = Application.file;
				return;
			} 
			if(StringConstants.SCHEME_COMPONENT_TERM_BLOGS.equals(component.getTerm())){
				application = Application.blog;
				return;
			} 
			if(StringConstants.SCHEME_COMPONENT_TERM_FORUMS.equals(component.getTerm())){
				application = Application.forum;
				return;
			} 
			if(StringConstants.SCHEME_COMPONENT_TERM_WIKI.equals(component.getTerm())){
				application = Application.wiki;
				return;
			} 
			if(StringConstants.SCHEME_COMPONENT_TERM_BOOKMARKS.equals(component.getTerm())){
				application = Application.bookmark;
				return;
			} 
			if(StringConstants.SCHEME_COMPONENT_TERM_STATUS_UPDATE.equals(component.getTerm())){
				application = Application.status_update;
				return;
			} 
			if(StringConstants.SCHEME_COMPONENT_TERM_PROFILES.equals(component.getTerm())){
				application = Application.profile;
				return;
			} 
		}
	}
	private void setDueDateField(Entry entry) {	
		String stringDueDate ="";
		
		List<Element>  fieldElements = entry.getExtensions(StringConstants.FIELD_NAMESPACE);
		
		if (fieldElements != null) {
			for (Element fieldElement : fieldElements) {
				
				if (fieldElement.getAttributeValue("id").equals("due_date") ){
					
					stringDueDate = fieldElement.getText();
				}
			}
		}
		
		this.fieldDueDate= stringDueDate;
	}
	/**
	 * Returns a Atom Entry representation of an Activity.
	 * 
	 * @see com.ibm.lconn.automation.framework.services.common.nodes.LCEntry#toEntry()
	 * @return the Atom Entry representation of an Activity.
	 */
	@Override
	public Entry toEntry() {
		
		Element[] extensions = { getRelevenceScoreElement() };
		
		Category[] categories = { getSearchCategory(), getComponents().get(0) };
		
		return createBasicEntry(extensions, categories);
	}
	
	/**
	 * @return <code>true</code> if this object represents an activity; <code>false</code> otherwise (community activity, or invalid entry).
	 */
	public boolean isSearch() {
		return isSearch.getTerm().equals(StringConstants.STRING_ACTIVITY_LOWERCASE);
	}
	
	/**
	 * @return the Atom category object that states that this entry is a search result.
	 */
	public Category getSearchCategory() {
		return isSearch;
	}

	/**
	 * Set whether this object represents a search result
	 * @param isSearch	set <code>true</code> if this object represents a search result; <code>false</code> otherwise
	 */
	public void setIsSearch(boolean isSearch) {
		Category isSearchCategory = null;
		
		if(isSearch) {
			isSearchCategory = getFactory().newCategory();
			isSearchCategory.setScheme(StringConstants.SCHEME_TYPE);
			isSearchCategory.setTerm(StringConstants.STRING_SEARCH_LOWERCASE);
			isSearchCategory.setLabel(StringConstants.STRING_SEARCH_CAPITALIZED);
		}
		
		this.isSearch = isSearchCategory;
	}

	
	
	public List<Category> getComponents() {
		return components;
	}

	public void setComponents(List<Category> components) {
		this.components = components;
		determineApp();
	}

	public Category getAccessControl() {
		return accessControl;
	}

	public void setAccessControl(Category accessControl) {
		this.accessControl = accessControl;
	}
	
	public Permissions getPermissions(){
		if ("private".equalsIgnoreCase(getAccessControlString())){
			return Permissions.PRIVATE;
		}
		if ("public".equalsIgnoreCase(getAccessControlString())){
			return Permissions.PUBLIC;
		}
		return null;
	}

	public String getAccessControlString(){
		if (accessControl == null){
			return null;
		}
		return accessControl.getTerm().toLowerCase().trim();
	}
	
	public Category getDocumentType() {
		return documentType;
	}

	public void setDocumentType(Category documentType) {
		this.documentType = documentType;
	}

	public String getDocumentTypeString(){
		if (documentType == null){
			return null;
		}
		return documentType.getTerm().toLowerCase().trim();
	}
	
	/**
	 * @return search relevence score
	 */
	public double getRelevenceScore() {
		if(relevenceScore != null) {
			return Double.parseDouble(relevenceScore.getText());
		}
		
		return 0;
	}
	public String getDueDateString() {
		
			return fieldDueDate;
		}
		
		
	/**
	 * @param relevenceScore	the Atom Element node that has search result relevence score
	 */
	public void setRelevenceScore(Element relevenceScore) {
		this.relevenceScore = relevenceScore;
	}
	
	/**
	 * @return the Atom Element node that has position information.
	 */
	public Element getRelevenceScoreElement() {
		return relevenceScore;
	}

	public Person getAuthor() {
		return getAuthors().get(0);
	}

	public Application getApplication() {
		return application;
	}

	public void setApplication(Application application) {
		this.application = application;
	}
	public Element getCommunityUUID() {
		return communityUUID;
	}

	public void setCommunityUUID(Element communityUUID) {
		if (communityUUID != null){
		this.communityUUID = communityUUID;
		}
	}

	@Override
	public String toString() {
		return "SearchResult [" + 
		super.toString() + 
		" + isSearch=" + isSearch + 
		", components=" + getComponents() + 
		", relevenceScore=" + getRelevenceScore() +
		", accessControl=" + getAccessControlString() + 
		", documentType=" + getDocumentTypeString() + 
		", application=" + getApplication() 
		+ "]";
	}


}
