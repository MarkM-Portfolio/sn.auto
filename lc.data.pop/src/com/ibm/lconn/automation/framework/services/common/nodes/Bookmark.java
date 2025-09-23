package com.ibm.lconn.automation.framework.services.common.nodes;

import java.util.List;

import org.apache.abdera.model.Category;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;

import com.ibm.lconn.automation.framework.services.common.StringConstants;

public class Bookmark extends LCEntry {
	
	private Category isBookmark;	/** (Required) Identifies this object as a Bookmark. */
	private Category isImportant;   /** (Optional for Community Bookmarks) If present, identifies the bookmark as an important bookmark. Important bookmarks are listed on the community home page.*/
	private Category isPrivate;		/** (Optional) Identifies if this Bookmark is private */
	private Category isInternal;	/** (Ignored) Identifies a Bookmark that refers to a resource within a corporate intranet. If this flag is not present, the bookmark refers by default to a resource on the public Internet (World Wide web). The Bookmarks server uses IP ranges configured by the server administrator to determine whether bookmarks are internal or not. See "Administering IBM Connections" for more information. */
	
	private Element snx_clickcount; /** (Ignored) Number of times the Bookmark has been clicked. */
	private Element snx_link;		/** (Ignored) Unique ID of the Bookmark */
	private Element snx_linkcount;	/** (Ignored) Total count of Bookmarks to this web address, including this one. */
	
	public Bookmark(String title, String content, String linkHref, String tagsString) {
		super();
		
		setIsBookmark(true);
		setTitle(title);
		setContent(content);
		addLink("", "", linkHref);
		setTags(tagsString);
	}
	
	public Bookmark(Entry entry) {
		super(entry);

		List<Category> types = entry.getCategories(StringConstants.SCHEME_TYPE);
		
		if(types.size() > 0) {
			String term = types.get(0).getTerm();
			if(term.equals(StringConstants.STRING_BOOKMARK_LOWERCASE)) {
				setIsBookmark(true);
			}
		}
		
		List<Category> flags = entry.getCategories(StringConstants.SCHEME_FLAGS);
		for(Category flag : flags) {
			if(flag.getTerm().equals(StringConstants.STRING_IMPORTANT_LOWERCASE)) {
				setIsImportant(flag);
			} else if(flag.getTerm().equals(StringConstants.STRING_PRIVATE_LOWERCASE)) {
				setIsPrivate(flag);
			} else if(flag.getTerm().equals(StringConstants.STRING_INTERNAL_LOWERCASE)) {
				setIsInternal(flag);
			}
		}
		
		setClickCount(entry.getExtension(StringConstants.SNX_CLICK_COUNT));
		setLinkElement(entry.getExtension(StringConstants.SNX_LINK));
		setLinkCount(entry.getExtension(StringConstants.SNX_LINK_COUNT));
	}

	@Override
	public Entry toEntry() {
		Element[] extensions = { getClickCountElement(), getLinkElement(), getLinkCountElement() };

		Category[] categories = { getIsBookmarkCategory(), getIsImportantCategory(), getIsInternalCategory(), getIsPrivateCategory() };

		return createBasicEntry(extensions, categories);
	}
	
	public void setLink(String url) {
		addLink("", "", url);
	}
	
	/**
	 * @return the Atom category object that contains the isBookmark information.
	 */
	public Category getIsBookmarkCategory() {
		return isBookmark;
	}

	/**
	 * @param isBookmark set the Atom category object that contains the isBookmark information.
	 */
	public void setIsBookmark(boolean isBookmark) {
		Category isBookmarkCategory = null;
		
		if(isBookmark) {
			isBookmarkCategory = getFactory().newCategory();
			isBookmarkCategory.setScheme(StringConstants.SCHEME_TYPE);
			isBookmarkCategory.setTerm(StringConstants.STRING_BOOKMARK_LOWERCASE);
			isBookmarkCategory.setLabel(StringConstants.STRING_BOOKMARK_CAPITALIZED);
		}
		
		this.isBookmark = isBookmarkCategory;
	}
	
	/**
	 * @param isBookmark a isBookmark Atom Category object.
	 */
	public void setIsBookmark(Category isBookmark) {
		this.isBookmark = isBookmark;
	}
	
	/**
	 * @return the Atom category object that contains the isImportant information.
	 */
	public Category getIsImportantCategory() {
		return isImportant;
	}

	/**
	 * @param isImportant set the Atom category object that contains the isImportant information.
	 */
	public void setIsImportant(boolean isImportant) {
		Category isImportantCategory = null;
		
		if(isImportant) {
			isImportantCategory = getFactory().newCategory();
			isImportantCategory.setScheme(StringConstants.SCHEME_FLAGS);
			isImportantCategory.setTerm(StringConstants.STRING_IMPORTANT_LOWERCASE);
		}
		
		this.isImportant = isImportantCategory;
	}
	
	/**
	 * @param isImportant a isImportant Atom Category object.
	 */
	public void setIsImportant(Category isImportant) {
		this.isImportant = isImportant;
	}
	
	/**
	 * @param isPrivate a isPrivate Atom Category object.
	 */
	public void setIsPrivate(Category isPrivate) {
		this.isPrivate = isPrivate;
	}

	/**
	 * @param isPrivate set the Atom category object that contains the isPrivate information.
	 */
	public void setIsPrivate(boolean isPrivate) {
		Category isPrivateCategory = null;
		
		if(isPrivate) {
			isPrivateCategory = getFactory().newCategory();
			isPrivateCategory.setScheme(StringConstants.SCHEME_FLAGS);
			isPrivateCategory.setTerm(StringConstants.STRING_PRIVATE_LOWERCASE);
			isPrivateCategory.setLabel(StringConstants.STRING_PRIVATE_CAPITALIZED);
		}
		
		this.isPrivate = isPrivateCategory;
	}
	
	/**
	 * @return the Atom category object that contains the isPrivate information.
	 */
	public Category getIsPrivateCategory() {
		return isPrivate;
	}
	
	/**
	 * @param isInternal a isInternal Atom Category object.
	 */
	public void setIsInternal(Category isInternal) {
		this.isInternal = isInternal;
	}

	/**
	 * @param isInternal set the Atom category object that contains the isInternal information.
	 */
	public void setIsInternal(boolean isInternal) {
		Category isInternalCategory = null;
		
		if(isInternal) {
			isInternalCategory = getFactory().newCategory();
			isInternalCategory.setScheme(StringConstants.SCHEME_FLAGS);
			isInternalCategory.setTerm(StringConstants.STRING_INTERNAL_LOWERCASE);
			isInternalCategory.setLabel(StringConstants.STRING_INTERNAL_CAPITALIZED);
		}
		
		this.isInternal = isInternalCategory;
	}
	
	/**
	 * @return the Atom category object that contains the isInternal information.
	 */
	public Category getIsInternalCategory() {
		return isInternal;
	}
	
	/**
	 * @param clickcount the Atom Element node that has click count information,  only used when an entry is retrieved from the server.
	 */
	public void setClickCount(Element clickcount) {
		this.snx_clickcount = clickcount;
	}
	
	/**
	 * @return the Atom Element node that has click count information.
	 */
	public Element getClickCountElement() {
		return snx_clickcount;
	}
	
	/**
	 * @param link the Atom Element node that has click count information,  only used when an entry is retrieved from the server.
	 */
	public void setLinkElement(Element link) {
		this.snx_link = link;
	}
	
	/**
	 * @return the Atom Element node that has link id information.
	 */
	public Element getLinkElement() {
		return snx_link;
	}
	
	/**
	 * @param linkcount the Atom Element node that has link count information,  only used when an entry is retrieved from the server.
	 */
	public void setLinkCount(Element linkcount) {
		this.snx_linkcount = linkcount;
	}
	
	/**
	 * @return the Atom Element node that has link count information.
	 */
	public Element getLinkCountElement() {
		return snx_linkcount;
	}
	
}
