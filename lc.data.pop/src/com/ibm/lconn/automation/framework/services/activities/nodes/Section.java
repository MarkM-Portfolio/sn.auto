package com.ibm.lconn.automation.framework.services.activities.nodes;

import java.util.List;

import org.apache.abdera.model.Category;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;

import com.ibm.lconn.automation.framework.services.common.StringConstants;

/**
 * Section object contains the elements that make up a Section.
 * Sections can only be posted to an Activity directly.
 * Sections may not be created inside another section.
 * 
 * @author Piyush K. Agarwal - pagarwal@us.ibm.com
 */
public class Section extends ActivityNode {
	
	private Category isSection;		/** (Required) Identifies this object as a Section. */
	
	public Section(String title, int position, Entry parent) {
		super(title, "", "", parent);
		
		setIsSection(true);
		setPosition(position);
	}

	public Section(Entry entry) {
		super(entry);
		
		List<Category> types = entry.getCategories(StringConstants.SCHEME_TYPE);
		
		if(types.size() > 0) {
			String term = types.get(0).getTerm();
			if(term.equals(StringConstants.STRING_SECTION_LOWERCASE)) {
				setIsSection(true);
			}
		}
	}
	
	@Override
	public Entry toEntry() {
		Element[] extensions = { getInReplyToElement(), getActivityIdElement(), getPositionElement(), getDepthElement(), getPermissionsElement()};

		Category[] categories = { getIsSectionCategory() };

		return createBasicEntry(extensions, categories);
	}
	
	/**
	 * @return the Atom category object that contains the isSection information.
	 */
	public Category getIsSectionCategory() {
		return isSection;
	}

	/**
	 * @param isSection set the Atom category object that contains the isSection information.
	 */
	public void setIsSection(boolean isSection) {
		Category isSectionCategory = null;
		
		if(isSection) {
			isSectionCategory = getFactory().newCategory();
			isSectionCategory.setScheme(StringConstants.SCHEME_TYPE);
			isSectionCategory.setTerm(StringConstants.STRING_SECTION_LOWERCASE);
			isSectionCategory.setLabel(StringConstants.STRING_SECTION_CAPITALIZED);
		}
		
		this.isSection = isSectionCategory;
	}
	
	/**
	 * @param isSection a isSection Atom Category object.
	 */
	public void setIsSection(Category isSection) {
		this.isSection = isSection;
	}
}