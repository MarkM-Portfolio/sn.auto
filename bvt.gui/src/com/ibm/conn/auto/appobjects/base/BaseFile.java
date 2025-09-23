package com.ibm.conn.auto.appobjects.base;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.testng.Assert;

import com.ibm.conn.auto.appobjects.BaseStateObject;
import com.ibm.conn.auto.appobjects.base.BaseFile.Builder;
import com.ibm.conn.auto.lcapi.APIFileHandler;
import com.ibm.conn.auto.util.GatekeeperConfig;
import com.ibm.conn.auto.webui.FilesUI;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.files.nodes.FileComment;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;

public class BaseFile implements BaseStateObject{
	
	public enum fileField{
		NAME,
		TAGS;	
	}
	
	public enum ShareLevel {
		NO_ONE("No one", "css=input[value='private']"),
		PEOPLE("People or Communities", "css=input[value='people']"),
		COMMUNITIEIS("People or Communities", "css=input[value='people']"),
		EVERYONE("Everyone in my organization", "css=input[value='public']");
		
	    private String shareLevel;
	    private String shareLink;
	    
	    private ShareLevel(String shareLevel, String shareLink){
	            this.shareLevel = shareLevel;
	            this.shareLink = shareLink;
	    }
	    
		public String getShareLevel(){
			return this.shareLevel;
		}
		
		public String getShareLink() {
			return this.shareLink;
		}
		
	}
	
	
	private String name;
	private String rename;
	private String tags;
	private String extension;
	private String folder;
	private ShareLevel shareLevel;
	private boolean encrypt;
	private boolean moderated;
	private boolean fileLocal;
	private boolean comFile;
	private boolean performFromOverview;
	private String sharedWith;
	private Set<fileField> edit_track = new HashSet<fileField>();
	private String description;
	
	public static class Builder {
		private String name;
		private String rename;
		private String tags;
		private String extension;
		private String folder = "";
		private ShareLevel shareLevel = ShareLevel.NO_ONE;
		private boolean encrypt = false;
		private boolean moderated = false;
		private boolean fileLocal = true;
		private boolean comFile = false;
		private boolean performFromOverview = false;
		private String sharedWith;
		private String description;
		
		
		public Builder(String name){
			this.name = name;
		}
		
		public Builder rename(String renameFile){
			this.rename = renameFile;
			return this;
		}
		
		public Builder tags(String tags){
			this.tags = tags;
			return this;
		}
		
		public Builder extension(String extension){
			this.extension = extension;
			return this;
		}
		
		public Builder encrypt(boolean encrypt) {
			this.encrypt = encrypt;
			return this;
		}
		
		public Builder moderated(boolean moderated) {
			this.moderated = moderated;
			return this;
		}
		
		public Builder fileLocal(boolean fileLocal){
			this.fileLocal = fileLocal;
			return this;
		}
		
		public Builder folder(String folder){
			this.folder = folder;
			return this;
		}
		
		public Builder comFile(boolean comFile){
			this.comFile = comFile;
			return this;
		}
		
		public Builder shareLevel(ShareLevel shareLevel) {
			this.shareLevel = shareLevel;
			return this;
		}
		
		public Builder performFromOverview(boolean performFromOverview){
			this.performFromOverview = performFromOverview;
			return this;
		}
		
		public Builder sharedWith(String sharedWith){
			this.sharedWith = sharedWith;
			return this;
		}
		
		public BaseFile build() {
			return new BaseFile(this);
		}
		
		public Builder description(String des) {
			this.description = des;
			return this;
		}
		
	}
	
	private BaseFile(Builder b) {
			this.setName(b.name);
			this.setRename(b.rename);
			this.setTags(b.tags);
			this.setExtension(b.extension);
			this.setEncrypt(b.encrypt);
			this.setModerated(b.moderated);
			this.setFileLocal(b.fileLocal);
			this.setFolder(b.folder);
			this.setComFile(b.comFile);
			this.setShareLevel(b.shareLevel);
			this.setPerformFromOverview(b.performFromOverview);
			this.setSharedWith(b.sharedWith);
			this.setDescription(b.description);
	}
	
	public void setDescription(String description) {
		this.description = description;		
	}
	
	public String getDescription() {		
		return description;		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;	
	}
	
	public String getRename() {
		return rename;
	}

	public void setRename(String rename) {
		this.rename = rename;	
	}
	
	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;	
	}
	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;	
	}
		
	public boolean getEncrypt() {
		return encrypt;
	}

	public void setEncrypt(boolean encrypt) {
		this.encrypt = encrypt;	
	}
	
	public boolean getModerated() {
		return moderated;
	}

	public void setModerated(boolean moderated) {
		this.moderated = moderated;	
	}
	
	public boolean getFileLocal() {
		return fileLocal;
	}

	public void setFileLocal(boolean fileLocal) {
		this.fileLocal = fileLocal;	
	}
	
	public String getFolder() {
		return folder;
	}

	public void setComFile(boolean comFile) {
		this.comFile = comFile;	
	}
	
	public boolean getComFile() {
		return comFile;
	}

	public void setShareLevel(ShareLevel shareLevel) {
		this.shareLevel = shareLevel;	
	}
	
	public ShareLevel getShareLevel() {
		return shareLevel;
	}
	
	public void setPerformFromOverview(boolean performFromOverview) {
      this.performFromOverview = performFromOverview; 
   }
   
	public boolean getPerformFromOverview() {
		return performFromOverview;
	}
	
	public void setFolder(String folder) {
		this.folder = folder;	
	}
	
	public void setSharedWith(String sharedWith) {
		this.sharedWith = sharedWith;
	}
	
	public String getSharedWith() {
		return sharedWith;
	}
	
	public void upload(FilesUI ui) {
		ui.upload(this);
	}
	
	public void upload(FilesUI ui, GatekeeperConfig gkc) {
		ui.upload(this, gkc);
	}
	
	public void addToFolder(FilesUI ui, BaseFolder folder){
		if(!this.getComFile()){
			ui.addToFolder(this, folder);
		}else{
			ui.addToComFolder(this,folder);
		}
	}
	public void share(FilesUI ui){
		ui.share(this);
	}
	
	public void trash(FilesUI ui){
		ui.trash(this);
	}
	
	public void download(FilesUI ui){
		ui.download(this);
	}
	
	public void delete(FilesUI ui){
		ui.delete(this);
	}

	public void restore(FilesUI ui){
		ui.restore(this);
	}
	
	public void pin(FilesUI ui) {
		ui.pin(this);
	}
	
	public void like(FilesUI ui) {
		ui.like(this);
	}
	
	public void comment(FilesUI ui, String comment) {
		ui.comment(comment);
	}

	public void following(FilesUI ui) {
		ui.following(this);
	}
	
	public FileEntry createFolderAPI(APIFileHandler apiOwner, FileEntry folderEntry){
		FileEntry newFolderEntry = apiOwner.createFolder(folderEntry);
		return newFolderEntry;	
	}
	
	public FileEntry createAPI(APIFileHandler apiOwner, File file){			
		Assert.assertNotNull(file, "The file parameter can't be null!");
		FileEntry fileEntry = apiOwner.CreateFile(this, file);
		Assert.assertNotNull(fileEntry, "The returned FileEntry for file '" + file.getAbsolutePath() + "' is null!");
		Assert.assertNotNull(fileEntry.getInputStream(), "The API returned a FileEntry with null InputStream! File '" + file.getAbsolutePath() + "' may be missing!");
		return fileEntry;			
	}
	public FileEntry createAPI(APIFileHandler apiOwner, File file, Community community){			
		FileEntry fileEntry = apiOwner.CreateFile(this, file, community);
		Assert.assertTrue(fileEntry != null, "Failed to upload file using API.");
		Assert.assertNotNull(fileEntry, "The returned FileEntry for file '" + file.getAbsolutePath() + "' is null!");
		Assert.assertNotNull(fileEntry.getInputStream(), "The API returned a FileEntry with null InputStream! File '" + file.getAbsolutePath() + "' may be missing!");
		return fileEntry;			
	}
	
	public FileComment commentAPI(APIFileHandler apiOwner, FileEntry fileEntry, FileComment comment) {
		FileComment fileComment = apiOwner.CreateFileComment(fileEntry, comment);
		Assert.assertTrue(fileComment != null, "Failed to add comment to file using API.");
		return fileComment;
	}
	public FileComment commentAPI(APIFileHandler apiOwner, FileEntry fileEntry, FileComment comment, Community community) {
		FileComment fileComment = apiOwner.CreateFileComment(fileEntry, comment, community);
		Assert.assertTrue(fileComment != null, "Failed to add comment to community file using API.");
		return fileComment;
	}
	
	public FileComment updateCommentAPI(APIFileHandler apiOwner, FileComment fileComment, String updatedFileCommentContent){
		FileComment newfileComment = apiOwner.updateFileComment(fileComment, updatedFileCommentContent);
		Assert.assertTrue(newfileComment != null, "Failed to update comment to community file using API.");
		return newfileComment;
	}
	
	public FileComment updateCommunityCommentAPI(APIFileHandler apiOwner, FileEntry fileEntry, FileComment fileComment, Community community){
		FileComment newfileComment = apiOwner.updateCommunityFileComment(community, fileEntry, fileComment);
		Assert.assertTrue(fileComment != null, "Failed to update comment to community file using API.");
		return newfileComment;
	}
	
	public FileEntry updateFileAPI(APIFileHandler apiOwner, FileEntry fileEntry, String newFileName) {
		fileEntry = apiOwner.updateFile(fileEntry, newFileName);
		return fileEntry;
	}
	
	public FileEntry updateCommunityFileAPI(APIFileHandler apiOwner, FileEntry fileEntry, Community community, String newFileName) {
		fileEntry = apiOwner.updateCommunityFile(fileEntry, community, newFileName);
		return fileEntry;
	}
	
	public FileEntry shareFileAPI(APIFileHandler apiOwner, FileEntry fileEntry){
		return apiOwner.changePermissions(this, fileEntry);
	}
	
	public FileEntry addFileToFolderAPI(APIFileHandler apiOwner,FileEntry folderEntry, ArrayList<String> filesList){
		FileEntry fileEntry = apiOwner.addFilestoFolder( folderEntry, filesList);
		return fileEntry;
	}
	
	public String likeFileAPI(APIFileHandler apiOwner, FileEntry fileEntry) {
		return apiOwner.likeFile(fileEntry);
	}
	
	public FileEntry likeCommunityFileAPI(APIFileHandler apiOwner,FileEntry fileEntry, Community community) {
		return apiOwner.likeCommunityFile(fileEntry, community);
	}
	
	public void pinFileAPI(APIFileHandler apiOwner, FileEntry fileEntry) {
		apiOwner.pinFile(fileEntry);
	}
	
	public void RemoveFromFolder(APIFileHandler apiOwner, FileEntry fileEntry, FileEntry folderEntry) {
		apiOwner.removeFileFromFolder(fileEntry, folderEntry);
	}
	
	public Set<fileField> getEdits(){
		return edit_track;
	}

	public boolean trashNotPresent(FilesUI ui) {
		return ui.trashNotPresent(this);
		
	}
}