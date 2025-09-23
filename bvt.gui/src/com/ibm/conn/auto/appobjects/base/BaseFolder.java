package com.ibm.conn.auto.appobjects.base;

import com.ibm.conn.auto.lcapi.APIFileHandler;
import com.ibm.conn.auto.webui.FilesUI;
import com.ibm.conn.auto.webui.constants.FilesUIConstants;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;


public class BaseFolder {

	public enum folderFields {
		NAME,
		DESCRIPTION;	
	}
	private String name;
	private String description;
	private Access access;
	private boolean performFromOverview;

	
	public enum Access {
		NO_ONE(FilesUIConstants.FolderAccessNoOne),
		PeoplGrpComm(FilesUIConstants.FolderAccessPeopleGrpComm),
		PUBLIC(FilesUIConstants.FolderAccessPublic);
		
	    public String commType;
	    private Access(String level){
	            this.commType = level;
	    }
	    
	    @Override
	    public String toString(){
	            return commType;
	    }
	}
	
	
	
	public static class Builder {
		private String name;
		private String description;
		private Access access;
		private boolean performFromOverview = false;
		
		public Builder(String name){
			this.name = name;
		}
	
		public Builder description(String description){
			this.description = description;
			return this;
		}
		
		public Builder access(Access access){
			this.access = access;
			return this;
		}
		
		public Builder performFromOverview(boolean performFromOverview) {
		   this.performFromOverview = performFromOverview;
		   return this;
		}
		
		public BaseFolder build() {
			return new BaseFolder(this);
		}
	}
	
	private BaseFolder(Builder b) {
		this.setName(b.name);
		this.setDescription(b.description);
		this.setAccess(b.access);
		this.setPerformFromOverview(b.performFromOverview);
	}
	
	public void setName(String name) {
		this.name = name;	
	}
	
	public String getName() {
		return name;
	}

	public void setDescription(String description) {
		this.description = description;	
	}

	public String getDescription() {
		return description;
	}

	public void setAccess(Access access) {
		this.access = access;	
	}

	public Access getAccess() {
		return access;
	}
	
	public void setPerformFromOverview(boolean performFromOverview) {
	   this.performFromOverview = performFromOverview;
	}
	
	public boolean getPerformFromOverview() {
	   return performFromOverview;
	}
	
	@Deprecated
	public void create(FilesUI ui){
		ui.create(this);
	}
	
	public void create(FilesUI ui, boolean waitForPageLoaded){
		ui.create(this, waitForPageLoaded);
	}
	
	public void add(FilesUI ui){
		ui.add(this);
	}
	
	public void share(FilesUI ui){
		ui.share(this);
	}
	
	public void delete(FilesUI ui){
		ui.delete(this);
	}
	
	public FileEntry createFolderAPI(APIFileHandler apiOwner, FileEntry folderEntry){
		FileEntry newFolderEntry = apiOwner.createFolder(folderEntry);
		return newFolderEntry;	
	}
	public FileEntry createFolderAPI(APIFileHandler apiOwner, FileEntry folderEntry, Community community){
		FileEntry newFolderEntry = apiOwner.createCommunityFolder(folderEntry, community);
		return newFolderEntry;	
	}

	public void pinFolderAPI(APIFileHandler apiOwner, FileEntry folderEntry) {
		apiOwner.pinFolder(folderEntry);
	}
}
