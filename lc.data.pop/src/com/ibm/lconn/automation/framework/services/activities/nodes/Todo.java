package com.ibm.lconn.automation.framework.services.activities.nodes;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.abdera.model.Category;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.commons.lang.ArrayUtils;

import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.UserPerspective;
import com.ibm.lconn.automation.framework.services.common.Utils;

/**
 * Todo object contains the elements that make up a To-do.
 * Todo's can be posted to an Activity directly, or to Sections within an Activity.
 * 
 * @author Piyush K. Agarwal - pagarwal@us.ibm.com
 */
public class Todo extends ActivityNode {

	private Category isTodo;		/** (Required) Identifies this object as a Todo. */
	private Category isComplete;
	
	private List<Element> snx_assignedto;
	private Element snx_duedate;	/** (Optional) Specifies the date on which the activity is due to be completed. */

	
	public Todo(String title, String content, String tagsString, int position, boolean isComplete, boolean isPrivate, Entry parent, String assignedToUserName, String assignedToUserID) {
		super(title, content, tagsString, parent);
		setIsTodo(true);
		setIsComplete(isComplete);
		setPosition(position);
		setIsPrivate(isPrivate);
		setAssignedTo(assignedToUserName, assignedToUserID);
	}
	public Todo(String title, String content, String tagsString, int position, boolean isComplete, boolean isPrivate, Entry parent, List<UserPerspective> assignedUsers) {
		super(title, content, tagsString, parent);
		setIsTodo(true);
		setIsComplete(isComplete);
		setPosition(position);
		setIsPrivate(isPrivate);
		setAssignedToMultiple(assignedUsers);
	}

	public Todo(Entry entry) {
		super(entry);
		List<Category> types = entry.getCategories(StringConstants.SCHEME_TYPE);
		if(types.size() > 0) {
			String term = types.get(0).getTerm();
			if(term.equals(StringConstants.STRING_TODO_LOWERCASE)) {
				setIsTodo(true);
			}
		}
		for(Category flag : entry.getCategories(StringConstants.SCHEME_FLAGS)) {
			if(flag.getTerm().equals(StringConstants.STRING_PRIVATE_LOWERCASE)) {
				setIsPrivate(flag);
			} else if(flag.getTerm().equals(StringConstants.STRING_COMPLETED)) {
				setIsComplete(flag);
			}
		}
		List<Element> assignedTo = new ArrayList<Element>();
		assignedTo=(entry.getExtensions(StringConstants.SNX_ASSIGNEDTO));
		this.setAssignedTo(assignedTo);
	}

	@Override
	public Entry toEntry() {
		
		Element[] extensions = { getInReplyToElement(), getActivityIdElement(), getDepthElement(), getDueDateElement(), 
				 getPermissionsElement(), getPositionElement() 
			   };
		for(Element assignedTo : getAssignedToElement()){
			Element[] extensionsMore  =(Element[]) ArrayUtils.add(extensions, assignedTo);
			extensions = extensionsMore;
		}  
		Category[] categories = { getIsTodoCategory(), getIsCompleteCategory(), getIsPrivateCategory() };
		return createBasicEntry(extensions, categories);
	}
	
	/**
	 * @return the Atom category object that contains the isTodo information.
	 */
	public Category getIsTodoCategory() {
		return isTodo;
	}

	/**
	 * @param isTodo set the Atom category object that contains the isTodo information.
	 */
	public void setIsTodo(boolean isTodo) {
		Category isEntryCategory = null;
		
		if(isTodo) {
			isEntryCategory = getFactory().newCategory();
			isEntryCategory.setScheme(StringConstants.SCHEME_TYPE);
			isEntryCategory.setTerm(StringConstants.STRING_TODO_LOWERCASE);
			isEntryCategory.setLabel(StringConstants.STRING_TODO_CAPITALIZED);
		}
		
		this.isTodo = isEntryCategory;
	}
	
	/**
	 * @param isEntry a isTodo Atom Category object.
	 */
	public void setIsTodo(Category isTodo) {
		this.isTodo = isTodo;
	}
	
	/**
	 * @return <code>true</code> if this activity is complete; <code>false</code> otherwise.
	 */
	public boolean isComplete() {
		return (isComplete != null);
	}
	
	/**
	 * @return the Atom category object that contains the is complete information.
	 */
	public Category getIsCompleteCategory() {
		return isComplete;
	}

	/**
	 * @param isComplete set the Atom category object that contains the is complete information.
	 */
	public void setIsComplete(boolean isComplete) {
		Category isCompleteCategory = null;
		
		if(isComplete) {
			isCompleteCategory = getFactory().newCategory();
			isCompleteCategory.setScheme(StringConstants.SCHEME_FLAGS);
			isCompleteCategory.setTerm(StringConstants.STRING_COMPLETED);
		}
		
		this.isComplete = isCompleteCategory;
	}
	
	/**
	 * @param isComplete	a isComplete Atom Category object.
	 */
	public void setIsComplete(Category isComplete) {
		this.isComplete = isComplete;
	}
	
	/**
	 * @return a Date object with the time parsed from the snx:duedate element.
	 * @throws ParseException if the date String is not in the expected format.
	 */
	public Date getDueDate() throws ParseException {
		if(snx_duedate != null) {
			return Utils.dateFormatter.parse(snx_duedate.getText());
		}
		
		return null;
	}

	public String getDueDateInText() throws ParseException {
		if(snx_duedate != null) {
			return snx_duedate.getText();
		}
		
		return null;
	}
	
	/**
	 * Set the due date of this activity to the provided date.
	 * 
	 * @param dueDate	the Date this activity is due.
	 */
	public void setDueDate(Date dueDate) {
		Element dueDateElement = null;
		
		if(dueDate != null) {
			dueDateElement = getFactory().newElement(StringConstants.SNX_DUEDATE);
			dueDateElement.setText(Utils.dateFormatter.format(dueDate));
		}
		
		this.snx_duedate = dueDateElement;
	}
	
	/**
	 * @return an Atom Element object with the duedate content
	 */
	public Element getDueDateElement() {
		return snx_duedate;
	}
	
	/**
	 * @return the Atom Element object that contains the assignedTo information.
	 */
	public List<Element> getAssignedToElement() {
		return snx_assignedto;
	}


	public void setAssignedTo(String assignedToUserName, String assignedToUserID) {
		 List<Element> assignedToElementList = new ArrayList<Element>();
		 Element assignedToElement = null;
		
		if(assignedToUserName != null && assignedToUserID != null) {
			assignedToElement = getFactory().newExtensionElement(StringConstants.SNX_ASSIGNEDTO);
			assignedToElement.setAttributeValue("name", assignedToUserName);
			assignedToElement.setAttributeValue("userid", assignedToUserID);
		}
		assignedToElementList.add(assignedToElement);
		this.snx_assignedto = assignedToElementList;
	}
	
	public void setAssignedToMultiple(List<UserPerspective> assignedUsers){
        List<Element> assignedToElementList = new ArrayList<Element>();
		for(UserPerspective assignedUser : assignedUsers)
		{
			if(assignedUser.getUserName() != null && assignedUser.getUserId() != null) {
				Element  assignedToElement = null;
				assignedToElement = getFactory().newExtensionElement(StringConstants.SNX_ASSIGNEDTO);
				assignedToElement.setAttributeValue("id",assignedUser.getUserId());
				assignedToElement.setAttributeValue("name", assignedUser.getUserName());
				assignedToElement.setAttributeValue("userid", assignedUser.getUserId());
				assignedToElementList.add(assignedToElement);
			}
			
		}
		this.setAssignedTo(assignedToElementList);
	}
	
	private void setAssignedTo(List<Element> assignedToElement) {
		snx_assignedto = assignedToElement;
	}
}
