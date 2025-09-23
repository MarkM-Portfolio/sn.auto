
package com.ibm.conn.auto.webui;

import java.util.Iterator;
import java.util.List;

import com.ibm.conn.auto.webui.constants.ActivitiesUIConstants;
import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import org.apache.commons.lang.StringEscapeUtils;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseActivity;
import com.ibm.conn.auto.appobjects.base.BaseActivityEntry;
import com.ibm.conn.auto.appobjects.base.BaseActivityTemplate;
import com.ibm.conn.auto.appobjects.base.BaseActivityToDo;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.member.ActivityCommunityMember;
import com.ibm.conn.auto.appobjects.member.ActivityMember;
import com.ibm.conn.auto.appobjects.role.CommunityRole;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
import com.ibm.conn.auto.webui.cloud.ActivitiesUICloud;
import com.ibm.conn.auto.webui.cnx8.HCBaseUI;
import com.ibm.conn.auto.webui.multi.ActivitiesUIMulti;
import com.ibm.conn.auto.webui.onprem.ActivitiesUIOnPrem;
import com.ibm.lconn.automation.framework.services.activities.nodes.Activity;
import com.ibm.lconn.automation.framework.services.activities.nodes.Todo;

public abstract class ActivitiesUI extends HCBaseUI {

	public ActivitiesUI(RCLocationExecutor driver) {
		super(driver);
	}
	
	protected static Logger log = LoggerFactory.getLogger(ActivitiesUI.class);

	// A FilesUI object is used when adding files to activity entries, this
	// field is to save time by only creating the object once.
	private static FilesUI filesUI = null;

	/**
	 * getMentionsLink - finds the mentions link the test needs to click on
	 * @param user - mentioned user whose link you are trying to click
	 */
	public String getMentionsLink(User user){
		return "css=a:contains(" + user.getDisplayName() + ")";
	}
	
	
	/**
	 * 
	 * @param title
	 * @return
	 */
	public String getAssignedTodoByName(BaseActivityToDo toDo) {
		return "css=h4[id$=miniTitle]:contains(" + toDo.getTitle()+ ")";
	}
	
	/**
	 * 
	 * @param locInList
	 * @return
	 */
	public String nameLocation(String locInList){
		return "css=div[id='" + locInList + "']";
	}
	
	/**
	 * getActivityLink
	 * @param activity
	 * @return
	 */
	public static String getActivityLink(BaseActivity activity){
		return "css=a:contains(" + activity.getName() + ")";
	}
	
	/**
	 * getCommunityActivityLink
	 * @param activity
	 * @return
	 */
	public static String getCommunityActivityLink(BaseActivity activity){
		return "css=#activitiesListTable a:contains(" + activity.getName() + ")";
	}
	
	/**
	 * getToDoUserLabel -
	 * @param entry
	 */
	public static String getToDoUserLabel(BaseActivityToDo toDo){
		return "css=label[title='" + toDo.getAssignTo().getDisplayName() +"']";
	}
	
	/**
	 * getToDoUserChkBox -
	 * @param userLabelID
	 */
	public static String getToDoUserChkBox(String userLabelId){
		return "css=input[id='" + userLabelId + "']";
	}
	
	/**
	 * @param uuid
	 * @return
	 */
	public static String getMoreDetailsSelector(String uuid) {
		return "css=tr[uuid='" + uuid + "'] td a.oaToggleDetailsNode";
	}
	
	/**
	 * @param uuid
	 * @return
	 */
	public static String getDeleteActivitySelector(String uuid){
		return "css=tr#activityList" + uuid + "detailsRow li a:contains('Delete')";
	}
	
	/**
	 * @param uuid
	 * @return
	 */
	public static String getEditActivitySelector(String uuid){
		return "css=tr#activityList" + uuid + "detailsRow li a:contains('Edit')";
	}
	
	public String getActivityExternalLabel(BaseActivity activity){
		return "css=h4[id^='" + getActivityUUID(activity) + "'] span[class='lconnIconSharedExternalText']:contains(Shared Externally)";
	}
	
	public String getActivitiesOption(){
		return ActivitiesUIConstants.activitiesOption;
	}
	
	public String getActivitiesToDoList(){
		return ActivitiesUIConstants.activitiesToDoList;
	}
	
	public String getActivitiesHighPriorityAct(){
		return ActivitiesUIConstants.activitiesHighPriorityAct;
	}
	
	/** End of the selector section*/
	
	public String getActivityUUID(BaseActivity activity) {		
		String href = driver.getFirstElement("css=.oaActivityListContentNode h4 a:contains('" + activity.getName() + "')").getAttribute("href");
		return href.substring(href.lastIndexOf(",") + 1);
	}
	
	public String getActivityTemplateUUID(BaseActivityTemplate activityTemplate) {		
		String href = driver.getFirstElement("css=.oaActivityListContentNode h4 a:contains('" + activityTemplate.getName() + "')").getAttribute("href");
		return href.substring(href.lastIndexOf(",") + 1);
	}

	public void createEntry(BaseActivityEntry entry){
		
		log.info("INFO: Create an entry for this activity");
		fluentWaitPresent(ActivitiesUIConstants.New_Entry);
		clickLinkWithJavascript(ActivitiesUIConstants.New_Entry);
		
		//start of form
		log.info("INFO: Add title to entry");
		clearText(ActivitiesUIConstants.New_Entry_InputText_Title);
		typeText(ActivitiesUIConstants.New_Entry_InputText_Title, entry.getTitle());

		//tags if have one
		if(entry.getTags() != null){
			log.info("INFO: Add tag to entry");
			typeText(ActivitiesUIConstants.New_Entry_InputText_Tags, entry.getTags());
		}

		//Shared Entry Option Menus
		sharedOptionMenus(entry);
		
		//add description
		if(entry.getDescription() != null) {
			log.info("INFO: Enter description");
			typeNativeInCkEditor(entry.getDescription());
		}

		//Switch to shared option
		sharedEntryOptions(entry);
		
		//Save	
		clickSaveButton();
		fluentWaitTextPresent(entry.getTitle());
		log.info("INFO: Created Entry: " + entry.getTitle());

	}
	
	public void createToDo(BaseActivityToDo toDo){
		log.info("INFO: Create an entry for this activity");
		waitForPageLoaded(driver);
		clickLinkWithJavascript(ActivitiesUIConstants.AddToDo);
		
		waitForSameTime();
		
		//start of form
		log.info("INFO: Add title to entry");
		clearText(ActivitiesUIConstants.ToDo_InputText_Title);
		typeText(ActivitiesUIConstants.ToDo_InputText_Title, toDo.getTitle());

		//more options
		clickLinkWait(ActivitiesUIConstants.ToDo_More_Options);
		
		//tags if have one
		if(toDo.getTags() != null){
			log.info("INFO: Add tag to entry");
			typeText(ActivitiesUIConstants.ToDo_InputText_Tags, toDo.getTags());
		}

		//Assign To
		if(toDo.getAssignTo() != null) {
			//Open Assigned To
			log.info("INFO: Assign to a person");
			getFirstVisibleElement(ActivitiesUIConstants.ToDo_AssignTo_Pulldown).useAsDropdown().selectOptionByVisibleText((Data.getData().ToDo_ChooseAPerson));

			//add user to the filter box
			log.info("INFO: Clear and add the user assigned to this todo");
			getFirstVisibleElement(ActivitiesUIConstants.ToDo_Assign_FilterBox).clear();
			getFirstVisibleElement(ActivitiesUIConstants.ToDo_Assign_FilterBox).type(toDo.getAssignTo().getDisplayName());

			//find the user we are looking for by using the checkbox label
			log.info("INFO: Select user we are looking for");
			String userLabelId = driver.getSingleElement(getToDoUserLabel(toDo)).getAttribute("id");
			
			//remove the last 5 letters from userLabelId to get id for checkbox
			clickLinkWait(getToDoUserChkBox(userLabelId.substring(0, userLabelId.length()-5)));
		}
		//Assign to multiple assignee
		if(!toDo.getMultipleAssignTo().isEmpty()){
			log.info("INFO: Assign to a person");
			getFirstVisibleElement(ActivitiesUIConstants.ToDo_AssignTo_Pulldown).useAsDropdown().selectOptionByVisibleText((Data.getData().ToDo_ChooseAPerson));
			
			for (User user: toDo.getMultipleAssignTo()){
				//add user to the filter box
				log.info("INFO: Clear and add the user assigned to this todo");
				getFirstVisibleElement(ActivitiesUIConstants.ToDo_Assign_FilterBox).clear();
				getFirstVisibleElement(ActivitiesUIConstants.ToDo_Assign_FilterBox).type(user.getDisplayName());

				//find the user we are looking for by using the checkbox label
				log.info("INFO: Select user we are looking for");
				String userLabelId = driver.getSingleElement("css=label[title='" + user.getDisplayName() +"']").getAttribute("id");
				
				//remove the last 5 letters from userLabelId to get id for checkbox
				clickLinkWait(getToDoUserChkBox(userLabelId.substring(0, userLabelId.length()-5)));				
			}
		}
		
		//Add due date
		if(toDo.getDueDateRandom() || toDo.getDueDate() != null) {
			if(toDo.getDueDateRandom()) {
				pickRandomDojoDate(ActivitiesUIConstants.ToDo_DueDate, toDo.getUseCalPick());
			} else {
				pickDojoDate(ActivitiesUIConstants.ToDo_DueDate, toDo.getDueDate(), toDo.getUseCalPick());
			}
		}
		
		//Switch to shared menu options
		sharedOptionMenus(toDo);
		

		//add description
		if(toDo.getDescription() != null) {
			log.info("INFO: Enter description");
			typeNativeInCkEditor(toDo.getDescription());
		}

		//Switch to shared option
		sharedEntryOptions(toDo);
				
		//Save	
		clickSaveButton();
		fluentWaitTextPresent(toDo.getTitle());
		log.info("INFO: Created Todo: " + toDo.getTitle());

	}	
	
	/**
	 * Region that allows for adding a file, bookmark or custom field to an entry
	 * @param entry
	 */
	private void sharedOptionMenus(BaseActivityEntry entry) {

		//add file section  (attach, link to file, link to folder)
		if(entry.getFiles() != null){
			for(String fileName: entry.getFiles()) {
				uploadFileToActivity(fileName);
			}
		}
		
		//add link to file
		if(entry.getLinkToFiles() != null){
			for(String fileName: entry.getLinkToFiles()) {
				linkToFile(fileName);
			}
		}
		
		//add link to folder
		if(entry.getLinkToFolders() != null){
			for(String folderName: entry.getLinkToFolders()) {
				linkToFolder(folderName);
			}
		}
		
		//add bookmark
		if(entry.getBookmark() != null){
			int i = 0;
			for(String key: entry.getBookmark().keySet()) {
				clickLinkWait(ActivitiesUIConstants.New_Entry_Add_Bookmark);
				
				clearText(ActivitiesUIConstants.New_Entry_Bookmark1_Label_InputText_LableName + ":nth(" + i + ")");
				typeText(ActivitiesUIConstants.New_Entry_Bookmark1_Label_InputText_LableName + ":nth(" + i + ")", key);
				
				clearText(ActivitiesUIConstants.New_Entry_Bookmark1_InputText_LinkURL + ":nth(" + i + ")");
				typeText(ActivitiesUIConstants.New_Entry_Bookmark1_InputText_LinkURL + ":nth(" + i + ")",
						 entry.getBookmark().get(key));
				i++;
			}
		}
		
		//add custom fields
			//Add date
			if(entry.isDateRandom() || entry.getDate() != null) {
				clickLinkWithJavascript(ActivitiesUIConstants.New_Entry_AddCustomFieldsMenu);
				waitForPageLoaded(driver);
				String menuOptionSelector = BaseUIConstants.menuOption+":contains(Date Field)";
				List<Element> visibleDateMenu = driver.getVisibleElements(menuOptionSelector);
				Assert.assertTrue(visibleDateMenu.size() == 1, "ERROR: Wrong number of elements " +
						"visible for selector {" + menuOptionSelector + "} Expected: 1, actual: " +
						visibleDateMenu.size());
				visibleDateMenu.get(0).click();
				if(entry.isDateRandom()) {
					pickRandomDojoDate(ActivitiesUIConstants.New_Entry_Date_Label_InputText_LableName, entry.getUseCalPick());
				} else {
					pickDojoDate(ActivitiesUIConstants.New_Entry_Date_Label_InputText_LableName, entry.getDate(), entry.getUseCalPick());
				}
			}
			
			//Add Person
			if(entry.getPerson() != null) {
				clickLinkWait(ActivitiesUIConstants.New_Entry_AddCustomFieldsMenu);
				String menuOptionSelector = BaseUIConstants.menuOption+":contains(Person Field)";
				List<Element> visiblePersonMenu = driver.getVisibleElements(menuOptionSelector);
				Assert.assertTrue(visiblePersonMenu.size() == 1, "ERROR: Wrong number of elements " +
						"visible for selector {" + menuOptionSelector + "} Expected: 1, actual: " +
						visiblePersonMenu.size());
				visiblePersonMenu.get(0).click();
				addPersonField(entry);
			}
			
			//Add Text Field
			if(!entry.getCustomText().isEmpty()) {
				clickLinkWithJavascript(ActivitiesUIConstants.New_Entry_AddCustomFieldsMenu);
				String menuOptionSelector = BaseUIConstants.menuOption+":contains(Text Field)";
				List<Element> visibleTextMenu = driver.getVisibleElements(menuOptionSelector);
				Assert.assertTrue(visibleTextMenu.size() == 1, "ERROR: Wrong number of elements " +
						"visible for selector {" + menuOptionSelector + "} Expected: 1, actual: " +
						visibleTextMenu.size());
				visibleTextMenu.get(0).click();
				for(String key: entry.getCustomText().keySet()) {
					//title
					clearText(ActivitiesUIConstants.New_Entry_AddCustomFields_TextLabel);
					typeText(ActivitiesUIConstants.New_Entry_AddCustomFields_TextLabel, key);
					//value
					clearText(ActivitiesUIConstants.New_Entry_AddCustomFields_Text);
					typeText(ActivitiesUIConstants.New_Entry_AddCustomFields_Text, entry.getCustomText().get(key));
				}

			}

	}
	
	/**
	 * Region that allows for adding the entry to a section, marking as private and notifying others
	 * @param entry
	 */	
	private void sharedEntryOptions(BaseActivityEntry entry) {

		//section
		if(!entry.getSection().isEmpty()){
			log.info("INFO: Select drop down");
			driver.getSingleElement("css=select[id^='lconn_act_EntryForm_']").useAsDropdown().selectOptionByVisibleText(entry.getSection());
		}
		
		//Mark as Private
		if(entry.getMarkPrivate()){
			log.info("INFO: Marking entry private");
			clickLinkWait(ActivitiesUIConstants.New_Entry_Private_Checkbox);
			if(!isElementPresent(ActivitiesUIConstants.New_Entry_Private_Checkbox))
				clickLinkWithJavascript(ActivitiesUIConstants.New_Entry_Private_Checkbox);
		}
		
		//Notification
		if(entry.getNotifyPeople()) {
			clickLinkWait(ActivitiesUIConstants.New_Entry_Notify_Checkbox_Notify);
			fluentWaitTextPresent("All individual members of this activity");
			if(entry.getNotifyAll()) {
				clickLinkWait(ActivitiesUIConstants.New_Entry_Notify_Checkbox_NotifyAll);
			}else{
				for(User person: entry.getNotifyPeopleList()) {
					String labelId = driver.getSingleElement("css=label[id*='notifyCheckbox'][title='"+person.getDisplayName()+"']").getAttribute("id");
					String id = labelId.replace("Label", "");
					clickLinkWait("css=#" + id);
				}
			}
			
			if (entry.getNotifyMessage() != null)
				typeText(ActivitiesUIConstants.New_Entry_Notify_Message, entry.getNotifyMessage());
		}
	}
	
	/** Upload a file into an activity */
	public void uploadFileToActivity(String fileUploadName) {
		//Open "File Upload" dialog
		if (driver.isTextNotPresent("Add File")){
			clickLinkWait(ActivitiesUIConstants.AttachFileLink);
		}else if (driver.isTextPresent("Add File")){
			clickLinkWait(ActivitiesUIConstants.AddFileLink);
			clickLinkWithJavascript(ActivitiesUIConstants.AttachFileLinkFromAddButton);
		}
		
		//wait for the file upload dialog to appear
		fluentWaitPresent(ActivitiesUIConstants.AttachmentField);

		//get the path and file name of the file to upload
		if (filesUI == null) {
			filesUI = FilesUI.getGui(cfg.getProductName(), driver);
		}
		try {
			filesUI.fileToUpload(fileUploadName, ActivitiesUIConstants.AttachmentField);
			log.info("INFO: File Uploaded to Activity");
		} catch (Exception e) {
			Assert.fail("ERROR: File could not be uploaded to the activity: caught exception :" +
					e.getMessage());
		}
	}
		
	/** 
	 * Add a section to an activity 
	 * */
	public void addSection(String sectionTitle) {
		
		clickLinkWait(ActivitiesUIConstants.AddSection);
		fluentWaitPresent(ActivitiesUIConstants.Section_InputText_Title);
		clearText(ActivitiesUIConstants.Section_InputText_Title);
		typeText(ActivitiesUIConstants.Section_InputText_Title, sectionTitle);

		//save section
		clickButton("Save");
		fluentWaitTextPresent(sectionTitle);
		log.info("INFO: created a new section: "+sectionTitle);
	}

	/**
	 * Create an activity template
	 */
	public void createTemplate(BaseActivityTemplate template){
		
		if (!cfg.getUseNewUI()) {
			clickLinkWait(ActivitiesUIConstants.TemplateTab);
			clickLinkWait(ActivitiesUIConstants.createATemplate);
		} else {
			clickLinkWd(By.xpath(ActivitiesUIConstants.activityTemplatesTab));
			clickLinkWaitWd(By.xpath(ActivitiesUIConstants.createATemplateBtn), 6, "Click on 'Create a Template'");
		}
		
		fluentWaitPresent(ActivitiesUIConstants.Template_InputText_Title);
		clearText(ActivitiesUIConstants.Template_InputText_Title);
		typeText(ActivitiesUIConstants.Template_InputText_Title, template.getName());
		
		if (template.getTags() != null) {
			typeText(ActivitiesUIConstants.Template_InputText_Tags, template.getTags());
		}
		
		//add Members
		if(!template.getMembers().isEmpty()) {
			for(ActivityMember m: template.getMembers()) {
				addMember(m);
				//check member added and name appears
				Assert.assertTrue(driver.isTextPresent(m.getUser().getDisplayName()), m.getUser().getDisplayName() + " was not added to the activity");
			}
		}
		
		//Add description
		if (template.getDescription() != null) {
			typeText(ActivitiesUIConstants.Template_InputText_Description, template.getDescription());
		}
		
		clickSaveButton();
		if (!cfg.getUseNewUI()) {
			fluentWaitPresent(ActivitiesUIConstants.startAnActivityFromTemplate);
		} else {
			waitForElementVisibleWd(By.xpath(ActivitiesUIConstants.startAnActivityFromTemplateBtn), 5);
		}
		log.info("INFO: Created a template: " + template.getName());
	}
	
	/**
	 * Creates an activity
	 * @param activity
	 */
	public void create(BaseActivity activity) {
		
		//Start an Activity
		log.info("INFO: Create an activity");
		waitForPageLoaded(driver);
		if(activity.getCommunity() != null){
			log.info("INFO: Activity is inside a community as a widget");
			if(driver.isElementPresent(ActivitiesUIConstants.Start_An_Activity_Community)) clickLink(ActivitiesUIConstants.Start_An_Activity_Community);
			else clickLink(ActivitiesUIConstants.Start_An_Activity);
		}else{
			clickLink(ActivitiesUIConstants.Start_An_Activity);
		}

		//switching to a form potential Sametime steal focus issue
		log.info("INFO: Checking to see if sametime is enabled");
		waitForSameTime();
		
		// add more waiting time for my activity page be loaded
		if (activity.getCommunity() == null) {
			fluentWaitPresent(ActivitiesUIConstants.ActivityListName);
		}
				
		//Activity Name
		fluentWaitPresent(ActivitiesUIConstants.Start_An_Activity_InputText_Name);
		clearText(ActivitiesUIConstants.Start_An_Activity_InputText_Name);
		typeText(ActivitiesUIConstants.Start_An_Activity_InputText_Name, activity.getName());
		
		//permissions
		log.info("INFO: Check permissions if we should share this activity externally");
		checkPermission(activity);
		
		
		//add Tag
		if(activity.getTags() != null) {
			typeText(ActivitiesUIConstants.Start_An_Activity_InputText_Tags, activity.getTags());
		}
		
		//add Members
		if(!activity.getMembers().isEmpty()) {
			for(ActivityMember m: activity.getMembers()) {
				addMember(m);
				if(!(driver.isTextPresent(m.getUser().getDisplayName()))){
					log.info("attempt #2. Add member "+ m.getUser().getDisplayName());
					getFirstVisibleElement(ActivitiesUIConstants.nameInputField).clear();
					addMember(m);
				}
				//check member added and name appears
				Assert.assertTrue(driver.isTextPresent(m.getUser().getDisplayName()), m.getUser().getDisplayName() + " was not added to the activity");
			}
		}
		
		//add external Members
		if(!activity.getExternalMembers().isEmpty()) {
			for(ActivityMember m: activity.getExternalMembers()) {
				this.addExternalMember(m);
				//check member added and name appears
				Assert.assertTrue(driver.isTextPresent(m.getUser().getEmail()), m.getUser().getEmail() + " was not added to the activity");
			}
		}
		//add community Members
		if(!activity.getCommunityMembers().isEmpty()) {
			for(ActivityCommunityMember m: activity.getCommunityMembers()) {
				this.addCommunityMember(m);
				//check member added and name appears
				Assert.assertTrue(driver.isTextPresent(m.getCommunity().getName()), m.getCommunity().getName() + " was not added to the activity");
			}
		}
		
		//add goal
		if(activity.getGoal() != null) {
			typeText(ActivitiesUIConstants.Start_An_Activity_Textarea_Activity_Goals, activity.getGoal());
		}
		
		//Enter due date

		if(activity.isDueDateRandom()) {
			pickRandomDojoDate(BaseUIConstants.DatePicker_InputField, activity.getUseCalPick());
		} else if(activity.getDueDate() != null) {
			pickDojoDate(BaseUIConstants.DatePicker_InputField, activity.getDueDate(), activity.getUseCalPick());
		}
		
		//Save activity
		clickSaveButton();
		fluentWaitTextPresent(Data.getData().feedsForTheseEntries);		
		log.info("INFO: Created Activity: " + activity.getName());
	}
	
	/**
	 * addMember - 
	 * @param member
	 */
	private void addMember(ActivityMember member) {
		log.info("INFO: Adding a member to the Activities");
		//Choose Type
		driver.getSingleElement(ActivitiesUIConstants.Start_An_Activity_InputSelect_MemberType).useAsDropdown().selectOptionByVisibleText(member.getMemberType().toString());
		
		//Choose Role
		driver.getSingleElement(ActivitiesUIConstants.Start_An_Activity_InputSelect_MemberRole).useAsDropdown().selectOptionByVisibleText(member.getRole().toString());
		
		//type the member name in the typeahead field and add member
		addMember(member.getUser());
		
		log.info("INFO: Added a member to the component successfully");
	}

	public Element getMemberElement(User testUser){
		
		List<Element> members = driver.getElements("css=div[class='MemberContainers lotusLeft'][role='listitem']");

		Element memberElement=null;		
		Iterator<Element> memberList = members.iterator();
		while(memberList.hasNext()){			
			memberElement = memberList.next();
		    if (memberElement.getText().contains(testUser.getDisplayName())){
				log.info("INFO: Found user " + testUser.getDisplayName());
		    	break;
		    }
		    memberElement = null;
		}
	
		return memberElement;
	}
	
	
	
	/**
	 * editGoal - 
	 * @param activity
	 */
	public void editGoal(BaseActivity activity) {
		//Click details
		String uuid = getActivityUUID(activity);
		clickLinkWait(getMoreDetailsSelector(uuid));
		
		//Edit activity
		clickLinkWait(getEditActivitySelector(uuid));
		
		//Edit the activity description
		clearText(ActivitiesUIConstants.Start_An_Activity_Textarea_Activity_Goals);
		typeText(ActivitiesUIConstants.Start_An_Activity_Textarea_Activity_Goals, activity.getGoal());
		
		//save
		clickSaveButton();
	}
	
	/**
	 * delete - 
	 * @param activity
	 */
	public void delete(BaseActivity activity) {
		log.info("INFO: Deleting activity");
		String uuid = getActivityUUID(activity);
		
		//Click details
		clickLinkWait(getMoreDetailsSelector(uuid));
		
		clickLinkWait(getDeleteActivitySelector(uuid));

		clickLinkWait(ActivitiesUIConstants.Delete_Activity_Dialogue_OkButton);
		log.info("INFO: Deleted the activity");
	}
	
	public void deleteTemplate(BaseActivityTemplate activityTemplate) {
		log.info("INFO: Deleting activity");
		
		String UUID = getActivityTemplateUUID(activityTemplate);
		
		//Click the "more" link for our activity
		clickLinkWait("css=tr[uuid='" + UUID + "'] td.lotusLastCell a:contains('More')");

		//Delete activity template
		clickLinkWait("css=tr[id$='" + UUID + "detailsRow'] a:contains('Delete')");
		clickLinkWait(ActivitiesUIConstants.Delete_Activity_Dialogue_OkButton);
		
		log.info("INFO: Activity template deleted");
	}
	

	
	/**
	 * gotoStartActivity -
	 */
	public void gotoStartActivity() {
		log.info("INFO: Go to Start Activity");
		clickLinkWait(ActivitiesUIConstants.Start_An_Activity);
		try{
			fluentWaitPresent(ActivitiesUIConstants.Start_An_Activity_InputText_Name);
		}catch (Exception e){
			log.warn("Click did not open the form as expected so using javascript to click");
			clickLinkWithJavascript(ActivitiesUIConstants.Start_An_Activity);
			fluentWaitPresent(ActivitiesUIConstants.Start_An_Activity_InputText_Name);
		}
	}
	
	/**
	 * searchActivities - 
	 * @param activity
	 */
	public void searchActivities(BaseActivity activity) {
		log.info("Search for " + activity.getName());
		driver.getSingleElement(ActivitiesUIConstants.SearchTextArea).type(activity.getName());
		driver.getSingleElement(ActivitiesUIConstants.SearchButton).click();
		log.info("Search preformed for " + activity.getName());
	}
	
	/**
	 * verifySearchActivities - 
	 * @param activity
	 */
	public void verifySearchActivities(BaseActivity activity) {
		log.info("Verifying search results for " + activity.getName());
		fluentWaitPresent("css=#activityList");
		Assert.assertTrue(driver.isElementPresent("css=tr[id^='activityList'] a:contains("+activity.getName()+")"), "Search did not find item: " + activity.getName());
		log.info("Search verified");
	}

	/**
	 * addMember - 
	 * @param name User
	 */
	protected abstract void addMember(User name);

	/**
	 * 
	 * @param activity
	 */
	public abstract void checkPermission(BaseActivity activity);
	
	/**
	 * 
	 * @param baseActivity name
	 */
	public abstract void searchActivities(String name);
	
	/**
	 * deleteCurrentActivity
	 */
	public abstract void deleteCurrentActivity();
	
	/**
	 * selectAtMentionCKE - uses displayName to select and complete an at mention in CKE
	 * @param user - user you are creating an at mention for
	 */
	public void selectAtMentionCKE(String user) {	

		//Enter user name your sending to
		log.info("INFO: Typing text \"@" + user + "\" into CKEditor.");
		
		//If the whole string is typed at once, the dropdown will not reflect
		//what is typed, so we type the mention in two parts.
		String atMention = "@" + user;
		int atMentionLength = atMention.length();
		typeText(ActivitiesUIConstants.New_Entry_Description , atMention.substring(0, atMentionLength - 1));
		typeText(ActivitiesUIConstants.New_Entry_Description , atMention.substring(atMentionLength - 1));
		
		//Collect all the options
		List<Element> options = driver.getVisibleElements("css=div.dijitMenuItem[id^='lconn_core_PeopleTypeAheadMenu_']");
		
		//Iterate through the list and select the user from drop down
		Iterator<Element> iterator = options.iterator();
		while (iterator.hasNext()) {
			Element option = iterator.next();
			if (option.getText().contains(user + " ")){
				log.info("INFO: Found user " + user + ", clicking on the user.");
				option.click();
				
				if (!fluentWaitTextNotPresentWithoutRefresh("Person not listed?"))
					Assert.fail("ERROR: The @mention dropdown did not go away after clicking on a user!");
				return;
			}
		}
		Assert.fail("The user " + user + " was not found in the dropdown.");
	}

	/**
	 * collapseEntry - collapse the specified todo or entry
	 * @param entryUUID - the uuid of the todo or entry
	 */	
    public void collapseEntry(String entryUUID){
    	//collapse the specified entry
    	Element e = this.getFirstVisibleElement("css=h4[id^='activityPageNodeContainer"+entryUUID+"']");
    	Assert.assertTrue(e!=null);
    	String id = e.getAttribute("id");
    	if (id.endsWith("_nodeTitle")){
    		log.info("INFO: collapse entry with UUID: "+ entryUUID);
    		e.click();
    	}
    }

	/**
	 * expandEntry - expand the specified todo or entry
	 * @param entryUUID - the uuid of the todo or entry
	 */	    
    public void expandEntry(String entryUUID){
    	//expand the specified entry
    	Element e = this.getFirstVisibleElement("css=h4[id^='activityPageNodeContainer"+entryUUID+"']");
    	Assert.assertTrue(e!=null);
    	String id = e.getAttribute("id");
    	if (id.endsWith("_miniTitle")){
    		log.info("INFO: expand entry with UUID: "+ entryUUID);
    		e.click();
    	}
    }
    
	/**
	 * moreActionsForEntry -  click the more actions link for given todo or entry
	 * @param entryUUID - the uuid of the todo or entry
	 */	     
    public void moreActionsForEntry(String entryUUID){
    	//click the more actions link for given todo or entry
    	expandEntry(entryUUID);
    	log.info("INFO: click more actions for entry with UUID:"+ entryUUID);
    	driver.getSingleElement("css=a[aria-label='More Actions'][onclick*='"+entryUUID+"']").click();
    }
    
    /**
     * click edit link of an entry
     * @param entryUUID
     */
    public void editEntryLink(String entryUUID){
    	//click the Edit link for given todo or entry
    	expandEntry(entryUUID);
    	log.info("INFO: click Edit link for entry with UUID:"+ entryUUID);
    	driver.getSingleElement("css=a[id='activityPageNodeContainer"+entryUUID+"_editLink']").click();
    }
    
	/**
	 * getOverallCheckBoxStatus -  get a todo's overall checkbox status. note: the the overall checkbox status is not equal with the overall todo status 
	 * @param todoUUID - uuid of the todo
	 * @return true if the overall checkbox is checked, false if uncheck.
	 */	    
    public boolean getOverallCheckBoxStatus(String todoUUID){
    	//get a todo's overall checkbox status. note: the the overall checkbox status is not equal with the overall todo status
    	Element e = getFirstVisibleElement("css=input[id='activityPageNodeContainer"+todoUUID+"_checkBox']");
    	Assert.assertTrue(e!=null,"cannot find the todo overall checkbox");
    	log.info("INFO: get overall checkbox status of todo with UUID:"+ todoUUID);
    	return e.isSelected();
    }
	/**
	 * getOverallToDoStatus -  get a todo's overall complete status.
	 * @param todoUUID - uuid of the todo
	 * @return true if the todo is fully completed, false if not.
	 */	
    public boolean getOverallToDoStatus(String todoUUID){
    	//get a todo's overall complete status
    	Element e = getFirstVisibleElement("css=div[id='activityPageNodeContainer"+todoUUID+"_node']");	
       	Assert.assertTrue(e!=null,"cannot find the todo overall status");
       	log.info("INFO: get overall checkbox status of todo with UUID:"+ todoUUID);
       	return "Node completed".equals(e.getAttribute("class"));
    }
 
	/**
	 * getIndividualCheckBoxStatus -  get an individual assigee's checkbox status for a todo
	 * @param todoUUID - uuid of the todo
	 * @param userID - assignee's id
	 * @return true if the individual assignee's checkbox is check, false if not
	 */	  
    public boolean getIndividualCheckBoxStatus(String todoUUID, String userID){
    	//get an individual assigee's checkbox status for a todo
		Element e = getFirstVisibleElement("css=input[id$='"+todoUUID+"assignedTo"+userID+"']");
    	Assert.assertTrue(e!=null,"cannot find assignee individual checkbox");
       	log.info("INFO: get individual assigee(userID:"+ userID+")'s todo complete status for the todo with uuid:"+todoUUID);     	
    	return e.isSelected();
    }
    
    /**
     * check on the individual checkbox under the specified assignee
     * @param todoUUID
     * @param assigneeID
     */
	public void checkIndividualCheckBox(String todoUUID, String assigneeID){
		//check on the individual checkbox under the specified assignee
		this.expandEntry(todoUUID);
		Element e = getFirstVisibleElement("css=input[id$='"+todoUUID+"assignedTo"+assigneeID+"']");
    	Assert.assertTrue(e!=null,"cannot find assignee individual checkbox");
      	Assert.assertFalse(e.isSelected(),"the checkbox is already checked");
      	log.info("INFO: check on the individual assigee(userID:"+ assigneeID+")'s todo complete status for the todo with uuid:"+todoUUID);
      	e.click();
	} 
	
	/**
	 * uncheck the individual checkbox under the specified assignee
	 * @param todoUUID
	 * @param assigneeID
	 */
	public void uncheckIndividualCheckBox(String todoUUID, String assigneeID){
		//uncheck the individual checkbox under the specified assignee
		this.expandEntry(todoUUID);
		Element e = getFirstVisibleElement("css=input[id$='"+todoUUID+"assignedTo"+assigneeID+"']");
    	Assert.assertTrue(e!=null,"cannot find assignee individual checkbox");
      	Assert.assertTrue(e.isSelected(),"the checkbox is already unchecked");
      	log.info("INFO: uncheck the individual assigee(userID:"+ assigneeID+")'s todo complete status for the todo with uuid:"+todoUUID);      	
      	e.click();
	} 
	
	/**
	 * check the overall checkbox for a todo
	 * @param todoUUID
	 */
    public void checkOverallCheckBox(String todoUUID){
    	//check the overall checkbox for a todo
    	this.expandEntry(todoUUID);
		Element e = getFirstVisibleElement("css=input[id='activityPageNodeContainer"+todoUUID+"_checkBox']");
    	Assert.assertTrue(e!=null,"cannot find the todo overall checkbox");
     	Assert.assertFalse(e.isSelected(),"the checkbox is already checked");
     	log.info("INFO: checkon the overall checkbox of the todo with UUID:"+ todoUUID);
      	e.click();
    }
    
    /**
     * uncheck the overall checkbox for a todo
     * @param todoUUID
     */
    public void uncheckOverallCheckBox(String todoUUID){
    	//uncheck the overall checkbox for a todo
    	this.expandEntry(todoUUID);
		Element e = getFirstVisibleElement("css=input[id='activityPageNodeContainer"+todoUUID+"_checkBox']");
    	Assert.assertTrue(e!=null,"cannot find the todo overall checkbox");
     	Assert.assertTrue(e.isSelected(),"the checkbox is already unchecked"); 
     	log.info("INFO: uncheck the overall checkbox of the todo with UUID:"+ todoUUID);     	
      	e.click();
    }  
    
    /**
     * add a new assignee in the edit todo form
     * @param todoUUID
     * @param userName
     */
	public void addNewAssignee(String todoUUID, String userName){
		// add a new assignee in the edit todo form
		this.editEntryLink(todoUUID);
		clickLinkWait(ActivitiesUIConstants.ToDo_More_Options);
		getFirstVisibleElement(ActivitiesUIConstants.ToDo_AssignTo_Pulldown).useAsDropdown().selectOptionByVisibleText((Data.getData().ToDo_ChooseAPerson));

		//add user to the filter box
		log.info("INFO: Clear and add the user assigned to this todo");
		getFirstVisibleElement(ActivitiesUIConstants.ToDo_Assign_FilterBox).clear();
		getFirstVisibleElement(ActivitiesUIConstants.ToDo_Assign_FilterBox).type(userName);
		String userLabelId = driver.getSingleElement("css=label[title='" + userName +"']").getAttribute("id");
		
		//remove the last 5 letters from userLabelId to get id for checkbox
		clickLinkWait(getToDoUserChkBox(userLabelId.substring(0, userLabelId.length()-5)));
		this.clickSaveButton();
	};

	/**
	 * remove an assignee in the edit todo form
	 * @param todoUUID
	 * @param userID
	 */
	public void removeAssignee(String todoUUID, String userID){
		//remove an assignee in the edit todo form
		this.editEntryLink(todoUUID);
		clickLinkWait(ActivitiesUIConstants.ToDo_More_Options);
		log.info("INFO: remove assigee(user id:"+userID+") from todo with uuid:"+todoUUID);
		clickLinkWait("css=a[id^='lconn_core_PeopleFilterList_'][id$='-filter"+userID+"'] img");		
		this.clickSaveButton();
	};
	
	/**
	 * getEntryUUID - get an entry or todo's UUID
	 * @param baseEntry - the entry/todo object
	 * @return the uuid of the given entry/todo
	 */	    
    public String getEntryUUID(BaseActivityEntry baseEntry){
    	//get an entry or todo's UUID
       	Element e = this.getFirstVisibleElement("css=h4[id^='activityPageNodeContainer']:contains('"+baseEntry.getTitle()+"')");
    	Assert.assertTrue(e!=null,"ERROR: didn't find entry "+baseEntry.getTitle());
    	String id = e.getAttribute("id");
    	log.info("INFO: get entry's UUID of entry:" + baseEntry.getTitle());
    	return id.substring("activityPageNodeContainer".length(),id.length()-10);
    }

	/**
	 * isTextNotPresentWithinElement - check if the element doesn't contain the particular text
	 * @param container - the element as text container
	 * @param text - the searching text
	 * @return true if text cannot be found in the element, false if yes.
	 */	    
    public boolean isTextNotPresentWithinElement(Element container, String text){
		//check if the element doesn't contain the particular text
    	driver.turnOffImplicitWaits();
    	log.info("INFO: get inner text from the given container element");
		String context = container.getText();
		driver.turnOnImplicitWaits();
		Assert.assertTrue(context!=null,"No text in the given element");
		log.info("INFO: return if the inner text of the given element doesn't contain the string:" + text);
		return !context.contains(text);
    }
    
	/**
	 * isTextPresentWithinElement - check if the element contains the particular text
	 * @param container - the element as text container
	 * @param text - the searching text
	 * @return true if text can be found in the element, false if not.
	 */	    
    public boolean isTextPresentWithinElement(Element container, String text){
    	//check if the element contains the particular text
    	return !isTextNotPresentWithinElement(container,text);
    }
    
    /**
     * click force complete button
     */
    public void clickMarkCompleteButton(){
    	log.info("INFO: click mark Activities Complete button");
       	this.getFirstVisibleElement("xpath=//div[@class=\"lotusForm lotusFormTable ConfirmForm\"]/../following-sibling::div//input[@value=\"Mark Complete\"]").click();   	
    }; 
    
    /**
     * goto the activities main page through the header bar menu
     */
    public abstract void gotoActivitiesMainPage();
    
    /**
     * goto the todolist main page through the header bar menu
     */
    public abstract void gotoToDoListMainPage();
    
    /**
     * get section's uuid by given a section name
     * @param sectionName
     * @return
     */
	public String getSectionUUIDByName(String sectionName){
		//get section's uuid by given a section name
		log.info("INFO: get element of section:"+sectionName);
		Element section = driver.getSingleElement("css=span[id$='_section-titleNode']:contains('"+sectionName+"')");
		Assert.assertFalse(section == null, "No section found with name " + sectionName);
		String sectionID = section.getAttribute("id");
		sectionID = sectionID.substring("activityPageNodeContainer".length());
		sectionID = sectionID.substring(0,sectionID.indexOf("_section-titleNode"));
		return sectionID;
	}
	
	/**
	 * relogin an activity with specified user
	 * @param user
	 * @param activity
	 */
	public void reloginActivity(User user, BaseActivity activity){
		//relogin an activity with specified user
		reloginActivities(user);
		log.info("INFO: click the link of the activity:"+activity.getName());
		clickLinkWait(ActivitiesUI.getActivityLink(activity));
	}
	
	/**
	 * relogin activities main page with specified user
	 * @param user
	 */
	public void reloginActivities(User user){
		//relogin activities main page with specified user
		logout();
		login(user);
		log.info("INFO: relogin with user:"+ user.getDisplayName()+" and goto Activitis home page");
		gotoActivitiesHomepage();
	}
	
	/**
	 * jump to activity main page
	 */
	public void gotoActivitiesHomepage(){
		//jump to activity home page
		log.info("INFO: open Activities homepage through URL");
		driver.navigate().to(cfg.getTestConfig().getBrowserURL() + "activities");	
	}
	
	/**
	 * goto a community activity
	 * @param activity
	 */
	public void openCommunityActivity(BaseActivity activity){
		//open a community activity
		gotoActivitiesHomepage();
		log.info("INFO: open Community Activities link");		
		clickLinkWait(ActivitiesUIConstants.OverallCommunityActivities);
		log.info("INFO: click link of community activity:"+activity.getName());				
		clickLinkWait(ActivitiesUI.getActivityLink(activity));
	}
	
	/**
	 * relogin a community activity with the specified user
	 * @param user
	 * @param activity
	 */
	public void reloginCommunityActivity(User user, BaseActivity activity){
		//relogin a community activity with the specified user
		reloginCommunityActivies(user);
		log.info("INFO: click link of community activity:"+activity.getName());	
		driver.navigate().refresh();
		fluentWaitTextNotPresent(activity.getName());
		scrollIntoViewElement(ActivitiesUI.getActivityLink(activity));
		clickLinkWithJavascript((ActivitiesUI.getActivityLink(activity)));
	}
	
	/**
	 * relogin community activities view with specified user
	 * @param user
	 */
	public void reloginCommunityActivies(User user){
		//relogin community activities view with specified user
		reloginActivities(user);
		log.info("INFO: relogin Activities with user:"+user.getDisplayName()+" and goto Community Activities view");			
		clickLinkWait(ActivitiesUIConstants.OverallCommunityActivities);
	} 
	
	/**
	 * fill entry in edit mode
	 * @param entry
	 */
	public void fillEntry(BaseActivityEntry entry){
		
		//start of form
		log.info("INFO: Add title to entry");
		clearText(ActivitiesUIConstants.New_Entry_InputText_Title);
		typeText(ActivitiesUIConstants.New_Entry_InputText_Title, entry.getTitle());

		//tags if have one
		if(entry.getTags() != null){
			log.info("INFO: Add tag to entry");
			typeText(ActivitiesUIConstants.New_Entry_InputText_Tags, entry.getTags());
		}

		//Shared Entry Option Menus
		sharedOptionMenus(entry);
		
		//add description
		if(entry.getDescription() != null) {
			log.info("INFO: Enter description");
		//	typeNativeInCkEditor(entry.getDescription());
			String jsQuotedText = StringEscapeUtils.escapeJavaScript(entry.getDescription());
			driver.executeScript("for(var i in CKEDITOR.instances) { var x = CKEDITOR.instances[i]; " + " x.setData('" + jsQuotedText + "'); }");
		
		}

		//Switch to shared option
		sharedEntryOptions(entry);
		
		//Save	
		clickSaveButton();

	}

	/**
	 * fill todo in edit mode
	 * @param toDo
	 */
	public void fillToDo(BaseActivityToDo toDo){
		log.info("INFO: fill the toDo form in edit mode");
		
		waitForSameTime();
		
		//start of form
		log.info("INFO: Add title to entry");
		clearText(ActivitiesUIConstants.ToDo_InputText_Title);
		typeText(ActivitiesUIConstants.ToDo_InputText_Title, toDo.getTitle());

		//more options
		clickLinkWait(ActivitiesUIConstants.ToDo_More_Options);
		
		//tags if have one
		if(toDo.getTags() != null){
			log.info("INFO: Add tag to entry");
			typeText(ActivitiesUIConstants.ToDo_InputText_Tags, toDo.getTags());
		}

		//Assign To
		if(toDo.getAssignTo() != null) {
			//Open Assigned To
			log.info("INFO: Assign to a person");
			getFirstVisibleElement(ActivitiesUIConstants.ToDo_AssignTo_Pulldown).useAsDropdown().selectOptionByVisibleText((Data.getData().ToDo_ChooseAPerson));

			//add user to the filter box
			log.info("INFO: Clear and add the user assigned to this todo");
			getFirstVisibleElement(ActivitiesUIConstants.ToDo_Assign_FilterBox).clear();
			getFirstVisibleElement(ActivitiesUIConstants.ToDo_Assign_FilterBox).type(toDo.getAssignTo().getDisplayName());

			//find the user we are looking for by using the checkbox label
			log.info("INFO: Select user we are looking for");
			String userLabelId = driver.getSingleElement(getToDoUserLabel(toDo)).getAttribute("id");
			
			//remove the last 5 letters from userLabelId to get id for checkbox
			clickLinkWait(getToDoUserChkBox(userLabelId.substring(0, userLabelId.length()-5)));
		}
		
		if(!toDo.getMultipleAssignTo().isEmpty()){
			log.info("INFO: Assign to a person");
			getFirstVisibleElement(ActivitiesUIConstants.ToDo_AssignTo_Pulldown).useAsDropdown().selectOptionByVisibleText((Data.getData().ToDo_ChooseAPerson));
			
			for (User user: toDo.getMultipleAssignTo()){
				//add user to the filter box
				log.info("INFO: Clear and add the user assigned to this todo");
				getFirstVisibleElement(ActivitiesUIConstants.ToDo_Assign_FilterBox).clear();
				getFirstVisibleElement(ActivitiesUIConstants.ToDo_Assign_FilterBox).type(user.getDisplayName());

				//find the user we are looking for by using the checkbox label
				log.info("INFO: Select user we are looking for");
				String userLabelId = driver.getSingleElement("css=label[title='" + user.getDisplayName() +"']").getAttribute("id");
				
				//remove the last 5 letters from userLabelId to get id for checkbox
				clickLinkWait(getToDoUserChkBox(userLabelId.substring(0, userLabelId.length()-5)));				
			}
		}
		
		//Add due date
		if(toDo.getDueDateRandom() || toDo.getDueDate() != null) {
			if(toDo.getDueDateRandom()) {
				pickRandomDojoDate(ActivitiesUIConstants.ToDo_DueDate, toDo.getUseCalPick());
			} else {
				pickDojoDate(ActivitiesUIConstants.ToDo_DueDate, toDo.getDueDate(), toDo.getUseCalPick());
			}
		}
		
		//Switch to shared menu options
		sharedOptionMenus(toDo);
		

		//add description
		if(toDo.getDescription() != null) {
			log.info("INFO: Enter description");
			waitForCkEditorReady();
			String jsQuotedText = StringEscapeUtils.escapeJavaScript(toDo.getDescription());
			driver.executeScript("for(var i in CKEDITOR.instances) { var x = CKEDITOR.instances[i]; " + " x.setData('" + jsQuotedText + "'); }");		
		}

		//Switch to shared option
		sharedEntryOptions(toDo);
		
		waitForSameTime();
		//Save	
		clickSaveButton();		
	}
	
	/**
	 * add a todo under the specified entry
	 * @param entryUUID
	 * @param newToDo
	 */
	public void createTodoUnderEntry(String entryUUID,BaseActivityToDo newToDo){
		//add a todo under the specified entry
		expandEntry(entryUUID);
		log.info("INFO: click Add To Do Item link under entry with UUID:"+entryUUID);
		clickLinkWait("css=div[id='activityPageNodeContainer"+entryUUID+ "_node'] a:contains('Add To Do Item')");
		fillToDo(newToDo);
	}
	
	/**
	 * add a comment for the specified entry
	 * @param entryUUID
	 * @param comment
	 */
	public void createCommentUnderEntry(String entryUUID,String comment){
		//add a comment for the specified entry
		expandEntry(entryUUID);
		log.info("INFO: click Add Comment link under entry with UUID:"+entryUUID);
		clickLinkWait("css=div[id='activityPageNodeContainer"+entryUUID+ "_node'] a:contains('Add Comment')");		
		typeNativeInCkEditor(comment);
		clickSaveButton();		
	}


	/**
	 * add a link to file into an entry or todo
	 * @param fileName
	 */
	public void linkToFile(String fileName) {
		//add a link to file into an entry or todo
		log.info("INFO: click Add File->Link To File link");
		clickLinkWait(ActivitiesUIConstants.AddFileLink);
		this.getFirstVisibleElement(ActivitiesUIConstants.LinkToFileFromAddButton).click();
		
		Assert.assertTrue(fluentWaitTextPresent("Add Links to Files"),"link to file dialog doesn't appear");
		log.info("INFO: select file in file picker by name:"+fileName);
		clickLinkWait("css=div[title='"+ fileName +"']>span");
		String OKButtonSelector="css=input[class='lotusFormButton'][type='submit'][aria-disabled='false']"; 
		this.fluentWaitElementVisible(OKButtonSelector);
		clickLinkWait(OKButtonSelector);
	}
	
	/**
	 * add a link to folder into an entry or todo
	 * @param folderName
	 */
	public void linkToFolder(String folderName) {
		//add a link to folder into an entry or todo
		log.info("INFO: click Add File->Link To Folder link");
		clickLinkWait(ActivitiesUIConstants.AddFileLink);
		clickLinkWait(ActivitiesUIConstants.LinkToFolderFromAddButton);
		
		Assert.assertTrue(fluentWaitTextPresent("Add Links to Folders"),"link to folder dialog doesn't appear");
		getFirstVisibleElement(ActivitiesUIConstants.linkToFolderDropdown).useAsDropdown().selectOptionByValue("myCollections");
		log.info("INFO: select folder in folder picker by name:"+folderName);
		clickLinkWait("xpath=//div[@title='"+folderName+"']/span/../../div[@class='checkbox']");
		String OKButtonSelector="css=input[class='lotusFormButton'][type='submit'][aria-disabled='false']"; 
		this.fluentWaitElementVisible(OKButtonSelector);
		clickLinkWait(OKButtonSelector);
	}

	/**
	 * create an activity in a community 
	 * @param activity
	 * @param memberType indicate the creator is the owner or the member of the community
	 */
	public void createInCommunity(BaseActivity activity, CommunityRole memberType) {
		//create an activity in a community
		BaseCommunity community = activity.getCommunity();
		Assert.assertTrue(community!=null, "Error: No community to create Activity");		
		
		//Click on Communities link
		log.info("INFO: Clicking on the communities link");
		driver.navigate().to(cfg.getTestConfig().getBrowserURL() + "communities");

		//Open Community to leave it
		log.info("INFO: Open community");

		if (memberType == CommunityRole.MEMBERS){
			clickLinkWithJavascript(CommunitiesUIConstants.topNavMyCommunitiesCardView);
		}else if(memberType == CommunityRole.OWNERS){

		}
		this.fluentWaitPresentWithRefresh(CommunitiesUI.getCommunityLink(community));
		clickLink(CommunitiesUI.getCommunityLink(community));

		//click activity link
		log.info("INFO: Open community");		
		Community_LeftNav_Menu.ACTIVITIES.select(this);
		
		log.info("INFO: Create an activity");
		clickLinkWait("css=div[id='startActivity'] span[class='lotusBtn'] a:contains('Activity')");
		
		//switching to a form potential Sametime steal focus issue
		log.info("INFO: Checking to see if sametime is enabled");
		waitForSameTime();
		
		//Activity Name
		fluentWaitPresent(ActivitiesUIConstants.Start_An_Activity_InputText_Name);
		clearText(ActivitiesUIConstants.Start_An_Activity_InputText_Name);
		typeText(ActivitiesUIConstants.Start_An_Activity_InputText_Name, activity.getName());
		
		//add Tag
		if(activity.getTags() != null) {
			typeText(ActivitiesUIConstants.Start_An_Activity_InputText_Tags, activity.getTags());
		}
		
		//set implicit role
		if(activity.getImplicitRole()!=null){
			//Choose Role
			driver.getSingleElement(ActivitiesUIConstants.StartCommunityActivityImplicitRole).useAsDropdown().selectOptionByVisibleText(activity.getImplicitRole().toString());
		}else if(!activity.getMembers().isEmpty()) {
			
		    clickLinkWait(ActivitiesUIConstants.CommunityActivityExplicitRoleOption);
			for(ActivityMember m: activity.getMembers()) {
				addMemberFromCommunity(m);
			}
		}
		

		//add goal
		if(activity.getGoal() != null) {
			typeText(ActivitiesUIConstants.Start_An_Activity_Textarea_Activity_Goals, activity.getGoal());
		}
		
		//Enter due date

		if(activity.isDueDateRandom()) {
			pickRandomDojoDate(BaseUIConstants.DatePicker_InputField, activity.getUseCalPick());
		} else if(activity.getDueDate() != null) {
			pickDojoDate(BaseUIConstants.DatePicker_InputField, activity.getDueDate(), activity.getUseCalPick());
		}
		
		//Save activity
		clickSaveButton();
		fluentWaitTextPresent(Data.getData().feedsForTheseEntries);
		
		log.info("INFO: Created an activity: " + activity.getName());
	}
	
	/**
	 * Re-opens a to-do item using a unique CSS selector
	 * 
	 * @param todo - The Todo instance of the to-do item to be re-opened
	 */
	public void reopenToDoItemUsingUI(Todo todo) {
		
		log.info("INFO: Now re-opening the to-do item with title: " + todo.getTitle());
		
		// Retreive the ID for the to-do item to be re-opened
		String todoItemId = todo.getId().toString().substring(todo.getId().toString().lastIndexOf(':') + 1).trim();
		log.info("INFO: The ID of the to-do item to be re-opened has been retrieved: " + todoItemId);
		
		// Create the CSS selector for the checkbox to be clicked
		String checkboxCSSSelector = ActivitiesUIConstants.Todo_CompleteCheckBox_Unique;
		checkboxCSSSelector = checkboxCSSSelector.replaceAll("PLACEHOLDER", todoItemId);
		checkboxCSSSelector = checkboxCSSSelector.replaceAll("REPLACE_THIS", todo.getTitle().trim());
		log.info("INFO: The CSS selector for the checkbox has been created: " + checkboxCSSSelector);
		
		// Retrieve the elements for the checkbox
		List<Element> listOfCheckboxElements = driver.getElements(checkboxCSSSelector);
		
		// Only one of the checkbox elements found will be displayed - this element must be used
		Element checkboxElement = null;
		int index = 0;
		boolean foundVisibleElement = false;
		while(index < listOfCheckboxElements.size() && foundVisibleElement == false) {
			
			if(listOfCheckboxElements.get(index).isDisplayed()) {
				log.info("INFO: Found a visible checkbox element with ID: " + listOfCheckboxElements.get(index).getAttribute("id"));
				checkboxElement = listOfCheckboxElements.get(index);
				foundVisibleElement = true;
			}
			index ++;
		}
		
		log.info("INFO: Verify that a visible checkbox for the to-do item is displayed in the UI");
		Assert.assertTrue(foundVisibleElement, 
							"ERROR: A visible checkbox element could NOT be found for the to-do item with title: " + todo.getTitle());
		
		log.info("INFO: Verify that the checkbox is checked (ie. the to-do item is currently marked as completed)");
		Assert.assertTrue(checkboxElement.isSelected(), 
							"ERROR: The to-do item could not be re-opened using the UI - the checkbox was NOT checked (ie. the to-do item was NOT marked as completed)");
		
		// Re-open the item by unchecking the checkbox
		checkboxElement.click();
		
		log.info("INFO: Verify that the checkbox is now unchecked (ie. the to-do item has been re-opened)");
		Assert.assertFalse(checkboxElement.isSelected(), 
				"ERROR: The to-do item was not re-opened successfully using the UI - the checkbox was still checked after clicking (ie. the to-do item was still marked as completed)");
	}

	/**
	 * addMember - add external member in the new activity form
	 * @param member activity member type
	 */
	private void addExternalMember(ActivityMember member) {
		//add external member in the new activity form
		log.info("INFO: Adding a member to the Activities");
		//Choose Type
		driver.getSingleElement(ActivitiesUIConstants.Start_An_Activity_InputSelect_MemberType).useAsDropdown().selectOptionByVisibleText(member.getMemberType().toString());
		
		//Choose Role
		driver.getSingleElement(ActivitiesUIConstants.Start_An_Activity_InputSelect_MemberRole).useAsDropdown().selectOptionByVisibleText(member.getRole().toString());
		
		//type the member name in the typeahead field and add member
		addExternalMember(member.getUser());
		
		log.info("INFO: Added a member:"+member.getUser().getDisplayName()+" to the component successfully");
	}	
	
	/**
	 * addMember - add community as member in the create activity form
	 * @param member
	 */
	private void addCommunityMember(ActivityCommunityMember member) {
		//add community as member in the create activity form
		log.info("INFO: Adding a member to the Activities");
		//Choose Type
		driver.getSingleElement(ActivitiesUIConstants.Start_An_Activity_InputSelect_MemberType).useAsDropdown().selectOptionByVisibleText(member.getMemberType().toString());
		
		//Choose Role
		driver.getSingleElement(ActivitiesUIConstants.Start_An_Activity_InputSelect_MemberRole).useAsDropdown().selectOptionByVisibleText(member.getRole().toString());
		this.waitForSameTime();
		log.info("Entering the users display name (" + member.getCommunity().getName() + ")");
		typeText(ActivitiesUIConstants.Start_An_Activity_InputText_Members_CommunityTypeAhead, member.getCommunity().getName());
		clickLinkWait("css=div[class='dijitMenuItem']:contains('"+member.getCommunity().getName()+"')");
	}
	
	/**
	 * addMemberFromCommunity - add an individual member from community to the new activity
	 * @param member
	 */
	public void addMemberFromCommunity(ActivityMember member){
		//add an individual member from community to the new activity
		log.info("INFO: Select Explicit Role option");
		getFirstVisibleElement(ActivitiesUIConstants.StartCommunityActivityExplicitRole).useAsDropdown().selectOptionByVisibleText(member.getRole().toString());
		log.info("INFO: Select user:"+member.getUser().getDisplayName()+" from community member list");
		Element memberLabel = getFirstVisibleElement("css=div[class='lconnNotify lotusLeft'] label[title='"+ member.getUser().getDisplayName()+"']");
		Assert.assertTrue(memberLabel!=null,"Error: cannot find the user " + member.getUser().getDisplayName());
		String labelID = memberLabel.getAttribute("id");
		String checkboxID = labelID.substring(0,labelID.length()-5);
		Element checkbox = 	getFirstVisibleElement("id="+checkboxID);
		checkbox.click();
		
	}

	/**
	 * Navigate to the given activity by loading the URL directly.
	 * @param activity activity created by API
	 */
	public void navViaUUID(Activity activity){
		String activityURI = "/activities/service/html";
		String activityStart = "/mainpage#activitypage," + activity.getActivityId();
		String currentUrl = driver.getCurrentUrl();
		String targetUrl = "";
		
		if (currentUrl.indexOf(activityURI) > 0) {
			// also strips away any location.hash content that may have been left over in the URL
			targetUrl = currentUrl.substring(0, currentUrl.indexOf(activityURI) + activityURI.length()) + activityStart;
			log.info("INFO: ActivitiesUI#navViaUUID(), navigating to URL: " + targetUrl);
			driver.navigate().to(targetUrl);
		} else {
			// if we're mistakenly not in activities service, just navigate to the same page.
			driver.navigate().to(currentUrl);
		}
		
		//wait for page to load
		waitForSameTime();
		waitForPageLoaded(driver);
		waitForJQueryToLoad(driver);
	}
	
	
	/**
	 * addExternalMember - add external member to activity
	 * @param name
	 */
	protected abstract void addExternalMember(User name);
	
	/**
	 * add person file in entry or todo
	 * @param entry
	 */
	protected abstract void addPersonField(BaseActivityEntry entry);
	
	/**
	 * getGui -
	 * @param product
	 * @param driver
	 * @return ActivitiesUI
	 */
	public static ActivitiesUI getGui(String product, RCLocationExecutor driver){
		if(product.toLowerCase().equals("cloud")){
			return new  ActivitiesUICloud(driver);
		} else if(product.toLowerCase().equals("onprem")) {
			return new  ActivitiesUIOnPrem(driver);
		} else if(product.toLowerCase().equals("production")) {
			return new  ActivitiesUICloud(driver);
		} else if(product.toLowerCase().equals("multi")) {
			return new  ActivitiesUIMulti(driver);
		} else if(product.toLowerCase().equals("vmodel")) {
			return new  ActivitiesUIOnPrem(driver);
		} else {
			throw new RuntimeException("Unknown product name: " + product);
		}
	}


	/**
	 * Navigate to Created Activity , create new Entry and new To Do section  and verify Tiny Editor functionality
	 * @param Community Description
	 * @param Community Title
	 * @param Tiny Editor Scenario List
	 * @param User testUser1 to specify user in mention test case
	 * @return String Text present in Description of Tiny Editor.
	 */
	public String verifyTinyEditorInActivity(String  desc , String title, String scenariosList, User testUser1) {
		TinyEditorUI tui = new TinyEditorUI(driver);
		//String EntryTitle = "Entry"+baseActivity.getName();

		tui.clickOnMoreLink();
		
		log.info("INFO: Entering a description and validating the functionality of Tiny Editor");
		if (desc != null) {

			String TE_Functionality[] = scenariosList.split(",");

			for (String functionality : TE_Functionality) {
				switch (functionality) {
				case "verifyParaInTinyEditor":
					log.info("INFO: Validate Paragragh and header functionality of Tiny Editor");
					tui.verifyParaInTinyEditor(desc);
					break;
				case "verifyAttributesInTinyEditor":
					log.info("INFO: Validate Attributes functionality of Tiny Editor");
					tui.verifyAttributesInTinyEditor(desc);
					break;
				case "verifyPermanentPenInTinyEditor":
					log.info("INFO: Validate Permanent Pen functionality of Tiny Editor");
					tui.verifyPermanentPenInTinyEditor(desc);
					break;
				case "verifyUndoRedoInTinyEditor":
					log.info("INFO: Validate Undo and Redo functionality of Tiny Editor");
					tui.verifyUndoRedoInTinyEditor(desc);
					break;
				case "verifyAlignmentInTinyEditor":
					log.info("INFO: Validate Alignment functionality of Tiny Editor");
					tui.verifyAlignmentInTinyEditor(desc);
					break;
				case "verifyIndentsInTinyEditor":
					log.info("INFO: Validate Indents functionality of Tiny Editor");
					tui.verifyIndentsInTinyEditor(desc);
					break;
				case "verifyBulletsAndNumbersInTinyEditor":
					log.info("INFO: Validate Bullets and Numbers functionality of Tiny Editor");
					tui.verifyBulletsAndNumbersInTinyEditor(desc);
					break;
				case "verifyHorizontalLineInTinyEditor":
					log.info("INFO: Validate Horizontal Line functionality of Tiny Editor");
					tui.verifyHorizontalLineInTinyEditor(desc);
					break;
				case "verifySpecialCharacterInTinyEditor":
					log.info("INFO: Validate Special character functionality of Tiny Editor");
					tui.verifySpecialSymbolsInTinyEditor("SpecialChar");
					break;
				case "verifyEmotionsInTinyEditor":
					log.info("INFO: Validate Emoticons functionality of Tiny Editor");
					tui.verifySpecialSymbolsInTinyEditor("Emotions");
					break;
				case "verifySpellCheckInTinyEditor":
					log.info("INFO: Validate Horizontal Line functionality of Tiny Editor");
					tui.verifySpellCheckInTinyEditor(desc);
					break;
				case "verifyRowsCoulmnOfTableInTinyEditor":
					log.info("INFO: Validate Rows and Columns of Table in Tiny Editor");
					tui.verifyRowsCoulmnOfTableInTinyEditor(desc);
					break;
				case "verifyFormatPainterInTinyEditor":
					log.info("INFO: Validate Format Painter in Tiny Editor");
					tui.verifyFormatPainterInTinyEditor(desc);
					break;
				case "verifyFontInTinyEditor":
					log.info("INFO: Validate font functionality of Tiny Editor");
					tui.verifyFontInTinyEditor(desc);
					break;
				case "verifyFontSizeInTinyEditor":
					log.info("INFO: Validate font Size functionality of Tiny Editor");
					tui.verifyFontSizeInTinyEditor(desc);
					break;
				case "verifyLinkImageInTinyEditor":
					log.info("INFO: Validate Link Image functionality of Tiny Editor");
					tui.verifyLinkImageInTinyEditor(desc);
					break;
				case "verifyRightLeftParagraphInTinyEditor":
					log.info("INFO: Validate Left to Right paragraph functionality of Tiny Editor");
					tui.verifyRightLeftParagraphInTinyEditor(desc);
					break;
				case "verifyOtherTextAttributesAndFullScreenInTinyEditor":
					log.info("INFO: Validate other text attributes functionality of Tiny Editor");
					tui.verifyOtherTextAttributesAndFullScreenInTinyEditor(desc);
					break;
				case "verifyFindReplaceInTinyEditor":
					log.info("INFO: Validate Find and Replace functionality of Tiny Editor");
					tui.verifyFindReplaceInTinyEditor(desc);
					break;
				case "verifyInsertLinkImageInTinyEditor":
					log.info("INFO: Validate Link Image functionality of Tiny Editor");
					tui.verifyInsertLinkImageInTinyEditor(title);
					break;
				case "verifyTextColorInTinyEditor":
					log.info("INFO: Validate Font Text Color functionality of Tiny Editor");
					tui.verifyTextColorInTinyEditor(desc);
					break;
				case "verifyBackGroundColorInTinyEditor":
					log.info("INFO: Validate Font BackGround Color functionality of Tiny Editor");
					tui.verifyBackGroundColorInTinyEditor(desc);
					break;
				case "verifyWordCountInTinyEditor":
					log.info("INFO: Validate Word Count functionality of Tiny Editor");
					tui.verifyWordCountInTinyEditor(desc);
					break;
				case "verifyUploadImageFromDiskInTinyEditor":
					log.info("INFO: Validate Upload image from Disk functionality of Tiny Editor");
					tui.verifyUploadImageFromDiskInTinyEditor();
					break;
				case "verifyBlockQuoteInTinyEditor":
					log.info("INFO: Validate Block quote functionality of Tiny Editor");
					tui.verifyBlockQuoteInTinyEditor(desc);
					break;
				case "verifyInsertMediaInTinyEditor":
					log.info("INFO: Validate Insert Media functionality of Tiny Editor");
					tui.verifyInsertMediaInTinyEditor(desc);
					break;
				case "verifyLinkToConnectionsFilesInTinyEditor":
					log.info("INFO: Validate Link to connections files from files in Tiny Editor");
					tui.addLinkToConnectionsFilesInTinyEditor(desc);
					break;
				case "verifyCodeSampleIntinyEditor":
					log.info("INFO: Validate Code Sample functionality of Tiny Editor");
					tui.verifyCodeSampleIntinyEditor(desc);
					break;
				case "verifyInsertiFrameInTinyEditor":
					log.info("INFO: Validate Insert iFrame functionality of Tiny Editor");
					tui.verifyInsertiFrameInTinyEditor(desc);
					break;
				case "verifyExistingImagekInTinyEditor":
					log.info("INFO: Validate existing image functionality of Tiny Editor");
					tui.verifyExistingImagekInTinyEditor();
					break;	
				case "verifyMentionUserInTinyEditor":
					log.info("INFO: Validate Insert iFrame functionality of Tiny Editor");
					tui.verifyMentionUserInTinyEditor(desc,testUser1.getDisplayName());
					break;
				case "verifyEditDescriptionInTinyEditor":
					log.info("INFO: Validate Insert iFrame functionality of Tiny Editor");
					tui.verifyDefaultCaseInTinyEditor(desc);
					break;	
				}
			}
		}

		String TEText = tui.getTextFromTinyEditor();
		log.info("INFO: Get the text from Tiny Editor body" + TEText);
		
		// Save the Activity Entry
		clickSaveButton();

		return TEText;
	}
	
	public void verifyInsertedLink(String community)
    {
        TinyEditorUI tui = new TinyEditorUI(driver);
        tui.verifyInsertedLinkinDescription(community);
    }

	public String getActivityEntryDescText() {
		return this.getFirstVisibleElement(ActivitiesUIConstants.activityEntryDescDOM).getText();
	}
	
	public String getActivityToDoItemDescText(String toDoTitle){
		return this.getFirstVisibleElement(ActivitiesUIConstants.activityToDoItemDOM.replace("PLACEHOLDER", toDoTitle)).getText();
	}
	
	public void editDescriptionInTinyEditorNewEntry(String ediDesc) 
	{
		TinyEditorUI tui = new TinyEditorUI(driver);
		driver.getFirstElement(ActivitiesUIConstants.editFirstEntryNewEntry).click();
		tui.clearInTinyEditor();
		tui.typeInTinyEditor(ediDesc);
		clickSaveButton();
	}
	
	public void editDescriptionInTinyEditorToDoSection(String ediDesc,String title) 
	{
		TinyEditorUI tui = new TinyEditorUI(driver);
		driver.getFirstElement(ActivitiesUIConstants.activityToDoItemLink.replace("PLACEHOLDER", title)).click();
		driver.getFirstElement(ActivitiesUIConstants.activityToDoItemEditLink.replace("PLACEHOLDER", title)).click();
		driver.getFirstElement(ActivitiesUIConstants.ToDo_More_Options).click();
		tui.clearInTinyEditor();
		tui.typeInTinyEditor(ediDesc);
		clickSaveButton();
	}
	
	public String verifyTinyEditorInActivityEntry(BaseActivityEntry baseActivityEntry, User testUser1) 
	{
		return verifyTinyEditorInActivity(baseActivityEntry.getDescription(),baseActivityEntry.getTitle(),baseActivityEntry.getTinyEditorFunctionalitytoRun(),testUser1);
	}
	
	public String verifyTinyEditorInActivityToDo(BaseActivityToDo baseActivityToDo, User testUser1) 
	{
		return verifyTinyEditorInActivity(baseActivityToDo.getDescription(),baseActivityToDo.getTitle(),baseActivityToDo.getTinyEditorFunctionalitytoRun(),testUser1);
	}
	
	/**
	 * verifyActivityInfo -
	 * This function will verify Activity info like name, description and goal
	 * @param activity
	 */
	public void verifyActivityInfo(DefectLogger logger, BaseActivity activity) {

		// Go to the main page of the activity
		log.info("INFO: Go to the main page of the activity");
		logger.weakStep("Go to the main page of the activity");
		clickLinkWait("link=" + activity.getName());

		// Verify Activity goal is present on the Activity page
		log.info("INFO: Looking for activity goal '" + activity.getGoal() + "'");
		logger.weakStep("Looking for activity goal '" + activity.getGoal() + "'");

		if (activity.getGoal() != null)
			Assert.assertTrue(driver.isTextPresent(activity.getGoal()), "Goal for Activity is missing");

		// expand activity description
		if (driver.isElementPresent(ActivitiesUIConstants.ActivityOutline_More_ExpandDescription)) {
			log.info("INFO: Expand the activity");
			logger.weakStep("INFO: Expand the activity");
			clickLinkWait(ActivitiesUIConstants.ActivityOutline_More_ExpandDescription);
		}

		log.info("INFO: Validate that the goal is not empty");
		logger.weakStep("Validate that the goal is not empty");
		if (activity.getGoal() != null) {
			Assert.assertTrue(driver.isTextPresent(activity.getGoal()),
					"Goal for Activity is missing. Looking for Goal: " + activity.getGoal());
		}
	}

	/**
	 * verifyEntryInfo -
	 * This function will verify Activity Entry details
	 * @param entry
	 */
	public void validatePageInfo(DefectLogger logger , BaseActivityEntry entry) {

		log.info("INFO: Validate Entry title ");
		logger.weakStep("Validate Entry title ");
		Assert.assertTrue(driver.isTextPresent(entry.getTitle()), "ERROR: Title for Entry is missing");
		log.info("INFO: Validate Entry tags");
		logger.weakStep("Validate Entry tags");
		if (entry.getTags() != null)
			Assert.assertTrue(driver.isTextPresent(entry.getTags()), "ERROR: Tags for Entry is missing");
		log.info("INFO: Validate Entry description");
		logger.weakStep("Validate Entry description");
		if (entry.getDescription() != null)
		{
			if(cfg.getUseNewUI())
			{
				HCBaseUI hc = new HCBaseUI(driver);
				hc.scrollToElementWithJavaScriptWd(By.xpath(ActivitiesUIConstants.activityDetailDisplayOn));
				hc.clickLinkWaitWd(By.xpath(ActivitiesUIConstants.activityDetailDisplayOn), 4, "Click on Activity displayOn icon");
			}
			Assert.assertTrue(driver.isTextPresent(entry.getDescription()), "ERROR: Description for Entry is missing");
		}
		if (!entry.getFiles().isEmpty()) {
			for (String fileName : entry.getFiles()) {
				log.info("INFO: Validate file was entered");
				logger.weakStep("Validate file was entered");
				Assert.assertTrue(driver.isTextPresent(fileName), "ERROR: " + fileName + " file for Entry is missing");
			}
		}
	}

	/**
	 * verifyToDOInfo -
	 * This function will verify activity todo details
	 * @param ToDo
	 */
	public void validateToDoInfo(DefectLogger logger , BaseActivityToDo toDo) {

		log.info("INFO: Validate the todo is present");
		logger.weakStep("Validate the todo is present");
		Assert.assertTrue(driver.isTextPresent(toDo.getTitle()), "ERROR: Todo was not found on page");
    	log.info("Validate that the tags for the ToDo are present");
		logger.weakStep("Validate that the tags for the ToDo are present");
		Assert.assertTrue(driver.isTextPresent(toDo.getTags()), "ERROR: Tags for the ToDo is missing");
		log.info("Validate that the Description for the ToDo is present");
		logger.weakStep("Validate that the Description for the ToDo is present");
		Assert.assertTrue(driver.isTextPresent(toDo.getDescription()), "ERROR: Description for the ToDo is missing");
	}
}
