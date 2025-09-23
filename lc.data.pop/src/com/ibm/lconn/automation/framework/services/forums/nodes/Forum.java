package com.ibm.lconn.automation.framework.services.forums.nodes;

import java.util.List;

import org.apache.abdera.model.Category;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;

import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.nodes.LCEntry;

/**
 * ForumTopic object contains the elements that make up a new topic in a forum.
 * 
 * @author Piyush K. Agarwal - pagarwal@us.ibm.com
 */
public class Forum extends LCEntry {

	private Category isForum;
	private Category isLocked;
	
	public Forum(String title, String content) {
		super();
		
		setIsForum(true);
		setTitle(title);
		setContent(content);
	}

	public Forum(Entry entry) {
		super(entry);
		
		List<Category> types = entry.getCategories(StringConstants.SCHEME_TYPE);
		
		if(types.size() > 0) {
			String term = types.get(0).getTerm();
			if(term.equals(StringConstants.STRING_FORUM_FORUM_LOWERCASE)) {
				setIsForum(true);				
			}
		}
	}
	
	@Override
	public Entry toEntry() {
		Element[] extensions = { };

		Category[] categories = { getIsForumCategory(), getIsLockedCategory() };

		return createBasicEntry(extensions, categories);	
	}
	
	/**
	 * @return the Atom category object that contains the isForum information.
	 */
	public Category getIsForumCategory() {
		return isForum;
	}

	/**
	 * @param isForum set the Atom category object that contains the isForum information.
	 */
	public void setIsForum(boolean isForum) {
		Category isForumCategory = null;
		
		if(isForum) {
			isForumCategory = getFactory().newCategory();
			isForumCategory.setScheme(StringConstants.SCHEME_TYPE);
			isForumCategory.setTerm(StringConstants.STRING_FORUM_FORUM_LOWERCASE);
		}
		
		this.isForum = isForumCategory;
	}
	
	/**
	 * @param isForum a isForum Atom Category object.
	 */
	public void setIsForum(Category isForum) {
		this.isForum = isForum;
	}
	
	
	/**
	 * @return the Atom category object that contains the isLocked information.
	 */
	public Category getIsLockedCategory() {
		return isLocked;
	}

	/**
	 * @param isLocked set the Atom category object that contains the isLocked information.
	 */
	public void setIsLocked(boolean isLocked) {
		Category isLockedCategory = null;
		
		if(isLocked) {
			isLockedCategory = getFactory().newCategory();
			isLockedCategory.setScheme(StringConstants.SCHEME_FLAGS);
			isLockedCategory.setTerm(StringConstants.STRING_LOCKED_LOWERCASE);
		}
		
		this.isLocked = isLockedCategory;
	}
	
	/**
	 * @param isLocked a isLocked Atom Category object.
	 */
	public void setIsLocked(Category isLocked) {
		this.isLocked = isLocked;
	}
	
	public String getRepliesLink() {
		return getLinks().get(StringConstants.REL_REPLIES + ":" + StringConstants.MIME_ATOM_XML).getHref().toString();
	}

	public String getEditLink() {
		return getLinks().get(StringConstants.REL_EDIT + ":" + StringConstants.MIME_ATOM_XML).getHref().toString();
	}

	public String getForumTopicsLink() {
		return getLinks().get(StringConstants.REL_FORUM_TOPICS + ":" + StringConstants.MIME_ATOM_XML).getHref().toString();
	}
	
	

}
