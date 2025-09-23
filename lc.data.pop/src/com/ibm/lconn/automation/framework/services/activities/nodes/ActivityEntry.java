package com.ibm.lconn.automation.framework.services.activities.nodes;

import java.util.ArrayList;
import java.util.List;

import org.apache.abdera.model.Category;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.commons.lang.ArrayUtils;

import com.ibm.lconn.automation.framework.services.common.StringConstants;

/**
 * ActivityEntry object contains the elements that make up an Entry.
 * Entries can be posted to an Activity directly, or to Sections within an Activity.
 * 
 * @author Piyush K. Agarwal - pagarwal@us.ibm.com
 */
public class ActivityEntry extends ActivityNode {

	private Category isEntry;					/** (Required) Identifies this object as an Entry. */
	private Category isEntryTemplate;			/** (Required for Entry Template) Identifies this object as an Entry Template */
	
	private ArrayList<FieldElement> fields;		/** (Optional) List of fields, where fields may represent a bookmark, file, or custom field. See Activities field element for more information. */

	public ActivityEntry(String title, String content, String tagsString, int position, boolean isPrivate, ArrayList<FieldElement> fields, Entry parent, boolean isTemplate) {
		super(title, content, tagsString, parent);
		
		this.fields = new ArrayList<FieldElement>();

		if(!isTemplate)
			setIsEntry(true);
		else
			setIsEntryTemplate(true);
		
		setIsPrivate(isPrivate);
		
		setFields(fields);
		setPosition(position);
		// setActivityId(null); 	// Assigned by server
		// setDepth(0); 			// Assigned by server
		// setPermissions(null);    // Assigned by server
	}

	public ActivityEntry(Entry entry) {
		super(entry);
		
		this.fields = new ArrayList<FieldElement>();
		setFieldElements(entry.getExtensions(StringConstants.SNX_FIELD));
		
		List<Category> types = entry.getCategories(StringConstants.SCHEME_TYPE);
		
		if(types.size() > 0) {
			String term = types.get(0).getTerm();
			if(term.equals(StringConstants.STRING_ENTRY_LOWERCASE)) {
				setIsEntry(true);
			} else if(term.equals(StringConstants.STRING_ENTRY_TEMPLATE_LOWERCASE)) {
				setIsEntryTemplate(true);
			}
		}
		
		List<Category> flags = entry.getCategories(StringConstants.SCHEME_FLAGS);
		for(Category flag : flags) {
			if(flag.getTerm().equals(StringConstants.STRING_PRIVATE_LOWERCASE)) {
				setIsPrivate(flag);
			}
		}
	}
	
	@Override
	public Entry toEntry() {
		Element[] extensions = { getInReplyToElement(), getActivityIdElement(), getPositionElement(), getDepthElement(), getPermissionsElement()};

		Category[] categories = { getIsEntryCategory(), getIsEntryTemplateCategory(), getIsPrivateCategory() };

		Entry entry = createBasicEntry(ArrayUtils.addAll(getFieldElements().toArray(), extensions), categories);
		entry.setContentAsHtml(getContent());
		
		return entry;
	}
	
	/**
	 * @return the Atom category object that contains the isEntry information.
	 */
	public Category getIsEntryCategory() {
		return isEntry;
	}

	/**
	 * @param isEntry set the Atom category object that contains the isEntry information.
	 */
	public void setIsEntry(boolean isEntry) {
		Category isEntryCategory = null;
		
		if(isEntry) {
			isEntryCategory = getFactory().newCategory();
			isEntryCategory.setScheme(StringConstants.SCHEME_TYPE);
			isEntryCategory.setTerm(StringConstants.STRING_ENTRY_LOWERCASE);
			isEntryCategory.setLabel(StringConstants.STRING_ENTRY_CAPITALIZED);
		}
		
		this.isEntry = isEntryCategory;
	}
	
	/**
	 * @param isEntry a isEntry Atom Category object.
	 */
	public void setIsEntry(Category isEntry) {
		this.isEntry = isEntry;
	}

	/**
	 * @return the Atom category object that contains the isEntryTemplate information.
	 */
	public Category getIsEntryTemplateCategory() {
		return isEntryTemplate;
	}

	/**
	 * @param isEntryTemplate set the Atom category object that contains the isEntryTemplate information.
	 */
	public void setIsEntryTemplate(boolean isEntryTemplate) {
		Category isEntryTemplateCategory = null;
		
		if(isEntryTemplate) {
			isEntryTemplateCategory = getFactory().newCategory();
			isEntryTemplateCategory.setScheme(StringConstants.SCHEME_TYPE);
			isEntryTemplateCategory.setTerm(StringConstants.STRING_ENTRY_TEMPLATE_LOWERCASE);
		}
		
		this.isEntryTemplate = isEntryTemplateCategory;
	}
	
	/**
	 * @param isEntryTemplate a isEntryTemplate Atom Category object.
	 */
	public void setIsEntryTemplate(Category isEntryTemplate) {
		this.isEntryTemplate = isEntryTemplate;
	}
	
	/**
	 * @param field the Atom Element node that has position information,  only used when an entry is retrieved from the server.
	 */
	public void setFields(ArrayList<FieldElement> fields) {
		this.fields = fields;
	}
	
	/**
	 * @param position the integer position value for this entry
	 */
	public void setFieldElements(List<Element> fieldElements) {
		for(Element field : fieldElements) {
			this.fields.add(new FieldElement(field));
		}
	}
	
	/**
	 * @return the FieldElement object that represents this field
	 */
	public List<FieldElement> getFields() {
		return fields;
	}
	
	/**
	 * @return the Atom Element node that has position information.
	 */
	public ArrayList<Element> getFieldElements() {
		ArrayList<Element> fieldElements = new ArrayList<Element>();
		
		if(fields != null) {
			for(FieldElement field: fields) {
				fieldElements.add(field.toElement());
			}
		}
		
		return fieldElements;
	}
}
