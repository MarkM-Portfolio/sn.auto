package com.ibm.conn.auto.appobjects.base;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseStateObject;
import com.ibm.conn.auto.appobjects.base.BaseBlog.Builder;
import com.ibm.conn.auto.lcapi.APIActivitiesHandler;
import com.ibm.conn.auto.webui.ActivitiesUI;
import com.ibm.lconn.automation.framework.services.activities.nodes.Activity;
import com.ibm.lconn.automation.framework.services.activities.nodes.ActivityEntry;

public class BaseActivityEntry implements BaseStateObject {

	public enum activityEntryFields {
		NAME,
		TAGS,
		DESCRIPTION;	
	}
	
	private String title;
	private String tags;
	private String description;
	private String notifyMessage;
	private String sections;
	private List<String> files = new ArrayList<String>();
	private List<String> linkToFiles = new ArrayList<String>();
	private List<String> linkToFolders = new ArrayList<String>();	
	private Map<String, String> bookmark = new HashMap<String, String>();
	private Map<String, String> customText = new HashMap<String, String>();
	private Calendar date;
	private List<User> notifyPeopleList;
	private User person;
	private boolean dateRandom = false;
	private boolean markPrivate = false;
	private boolean notifyPeople = false;
	private boolean notifyAll = false;
	private boolean useCalPick;
	private Activity parent;
	private String tinyEditorFunctionalitytoRun;
	
	public static abstract class Builder<T extends Builder<T>> {
		private String title;
		protected String tags;
		protected String description;
		protected String notifyMessage;
		private String sections = "";
		protected List<String> files = new ArrayList<String>();
		protected List<String> linkToFiles = new ArrayList<String>();
		protected List<String> linkToFolders = new ArrayList<String>();		
		protected Map<String,String> bookmark = new HashMap<String, String>();
		protected Map<String,String> customText = new HashMap<String, String>();
		protected Calendar date;
		protected List<User> notifyPeopleList = new ArrayList<User>();
		protected User person;
		protected boolean markPrivate = false;
		protected boolean notifyPeople = false;
		protected boolean notifyAll = false;
		protected boolean dateRandom = false;
		private boolean useCalPick = false;
		private Activity parent;
		private String tinyEditorFunctionalitytoRun;
		
		protected abstract T self();
		
		public Builder(String title){
			this.title = title;
		}
		public T tinyEditorFunctionalitytoRun(String functionality)
		{
			this.tinyEditorFunctionalitytoRun= functionality;
			return self();
		}
		public T tags(String tags) {
			this.tags = tags;
			return self();
		}
		
		public T description(String description) {
			this.description = description;
			return self();
		}
		
		public T addFile(String filePath) {
			this.files.add(filePath);
			return self();
		}
		
		public T addFiles(List<String> filePaths) {
			this.files.addAll(filePaths);
			return self();
		}
		
		public T addLinkToFile(String fileName) {
			this.linkToFiles.add(fileName);
			return self();
		}
		
		public T addLinkToFiles(List<String> fileNames) {
			this.linkToFiles.addAll(fileNames);
			return self();
		}		

		public T addLinkToFolder(String folderName) {
			this.linkToFolders.add(folderName);
			return self();
		}
		
		public T addLinkToFolders(List<String> folderNames) {
			this.linkToFolders.addAll(folderNames);
			return self();
		}	
		
		public T bookmark(String title, String url) {
			this.bookmark.put(title, url);
			return self();
		}
		
		public T dateRandom() {
			this.dateRandom = true;
			return self();
		}
		
		public T date(Calendar date) {
			this.date = date;
			return self();
		}
		
		public T addPerson(User person) {
			this.person = person;
			return self();
		}
		
		public T customText(String title, String text) {
			this.customText.put(title, text);
			return self();
		}
		
		public T markPrivate() {
			this.markPrivate = true;
			return self();
		}
		
		public T notifyPeople() {
			this.notifyPeople = true;
			return self();
		}
		
		public T notifyAllPeople() {
			this.notifyAll = true;
			return self();
		}
		
		public T addPersonToNotify(User person) {
			this.notifyPeopleList.add(person);
			return self();
		}
		
		public T addPeopleToNotify(List<User> people) {
			this.notifyPeopleList.addAll(people);
			return self();
		}
		
		public T notifyMessage(String text) {
			this.notifyMessage = text;
			return self();
		}
		
		public T useCalPick(boolean value){
			this.useCalPick = value;
			return self();
		}
		
		public T sections(String sections) {
			this.sections = sections;
			return self();
		}
		
		public BaseActivityEntry build() {
			return new BaseActivityEntry(this);
		}
	}
	
	private static class Builder2 extends Builder<Builder2> {
        public Builder2(String title) {
			super(title);
		}

		@Override
        protected Builder2 self() {
            return this;
        }
    }

    public static Builder<?> builder(String title) {
        return new Builder2(title);
    }
	
	protected BaseActivityEntry(Builder<?> b) {
		this.setTitle(b.title);
		this.setTags(b.tags);
		this.setDescription(b.description);
		this.setFiles(b.files);
		this.setLinkToFiles(b.linkToFiles);
		this.setLinkToFolders(b.linkToFolders);
		this.setBookmark(b.bookmark);
		this.setDateRandom(b.dateRandom);
		this.setDate(b.date);
		this.setPerson(b.person);
		this.setCustomText(b.customText);
		this.setMarkPrivate(b.markPrivate);
		this.setNotifyPeople(b.notifyPeople);
		this.setNotifyAll(b.notifyAll);
		this.setNotifyPeopleList(b.notifyPeopleList);
		this.setNotifyMessage(b.notifyMessage);
		this.setUseCalPick(b.useCalPick);
		this.setSection(b.sections);
		this.setParent(b.parent);
		this.setTinyEditorFunctionalitytoRun(b.tinyEditorFunctionalitytoRun);
	}

	public void setTinyEditorFunctionalitytoRun(String functionality)
	{
		this.tinyEditorFunctionalitytoRun = functionality;
	}
	
	public String getTinyEditorFunctionalitytoRun() {
		return tinyEditorFunctionalitytoRun;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<String> getFiles() {
		return files;
	}

	public void setFiles(List<String> files) {
		this.files = files;
	}

	public Map<String, String> getBookmark() {
		return bookmark;
	}

	public void setBookmark(Map<String, String> bookmark) {
		this.bookmark = bookmark;
	}

	public boolean isDateRandom() {
		return dateRandom;
	}

	public void setDateRandom(boolean dateRandom) {
		this.dateRandom = dateRandom;
	}

	public Calendar getDate() {
		return date;
	}

	public void setDate(Calendar date) {
		this.date = date;
	}

	public User getPerson() {
		return person;
	}

	public void setPerson(User person) {
		this.person = person;
	}

	public Map<String, String> getCustomText() {
		return customText;
	}

	public void setCustomText(Map<String, String> customText) {
		this.customText = customText;
	}

	public boolean getMarkPrivate() {
		return markPrivate;
	}

	public void setMarkPrivate(boolean markPrivate) {
		this.markPrivate = markPrivate;
	}

	public boolean getNotifyPeople() {
		return notifyPeople;
	}

	public void setNotifyPeople(boolean notifyPeople) {
		this.notifyPeople = notifyPeople;
	}

	public boolean getNotifyAll() {
		return notifyAll;
	}

	public void setNotifyAll(boolean notifyAll) {
		this.notifyAll = notifyAll;
	}

	public List<User> getNotifyPeopleList() {
		return notifyPeopleList;
	}

	public void setNotifyPeopleList(List<User> notifyPeopleList) {
		this.notifyPeopleList = notifyPeopleList;
	}

	public String getNotifyMessage() {
		return notifyMessage;
	}

	public void setNotifyMessage(String notifyMessage) {
		this.notifyMessage = notifyMessage;
	}
	public void setParent(Activity parent){
		
		this.parent=parent;
		
	}
	public Activity getParent(){
		
		return parent;
		
	}
	
	public boolean getUseCalPick() {
		return useCalPick;
	}

	public void setUseCalPick(boolean useCalPick) {
		this.useCalPick = useCalPick;
	}
	
	public String getSection() {
		return sections;
	}

	public void setSection(String sections) {
		this.sections = sections;
	}
	
	public void create(ActivitiesUI ui) {
		ui.createEntry(this);
	}
	
	public List<String> getLinkToFiles() {
		return linkToFiles;
	}

	public void setLinkToFiles(List<String> linkToFiles) {
		this.linkToFiles = linkToFiles;
	}

	public List<String> getLinkToFolders() {
		return linkToFolders;
	}

	public void setLinkToFolders(List<String> linkToFolders) {
		this.linkToFolders = linkToFolders;
	}

	public ActivityEntry createEntryAPI(APIActivitiesHandler activitiesAPI){
		ActivityEntry newActEntry = activitiesAPI.createActivityEntry(this.getTitle(), this.getDescription(), this.getTags(), this.getParent(), markPrivate);
		//ActivityEntry newActEntry = activitiesAPI.createActivityEntry(this);
		return newActEntry;
	}

}
