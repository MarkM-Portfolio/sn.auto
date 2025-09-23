package com.ibm.lconn.automation.framework.services.files.nodes;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.abdera.model.Category;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;

import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Notification;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Permissions;
import com.ibm.lconn.automation.framework.services.common.StringConstants.SharePermission;
import com.ibm.lconn.automation.framework.services.common.nodes.LCEntry;

public class FileEntry extends LCEntry {
	
	private File file;
	private InputStream inputStream;
	private SharePermission sharePermission;
	private Permissions permission;
	private boolean notification;
	private Notification commentNotification;
	private Notification mediaNotification;
	private boolean includePath;
	private boolean propagate;
	private String shareSummary;
	private String shareWith;
	private String shareWhat = null;
	private String comment;
	private boolean isExternal;		// (optional) Used to allow external users. 
	private Date created;
	private Date modified;
	

	public FileEntry(File file) {
		super();
		setFile(file);
	}
	
	public FileEntry(File file, String title, String description, String tagsString, Permissions permission, boolean notification, Notification commentNotification, Notification mediaNotification, Date created, Date modified, boolean includePath, boolean propagate, SharePermission sharePermission, String shareSummary, String shareWith) {
		super();
		
		setTitle(title);
		setContent(description);
		setSummary(description);
		setPublished(created);
		setUpdated(modified);
		setCreated(created);
		setModified(modified);
		setFile(file);
		setPermission(permission);
		setNotification(notification);
		setCommentNotification(commentNotification);
		setMediaNotification(mediaNotification);
		setIncludePath(includePath);
		setPropagate(propagate);
		setSharePermission(sharePermission);
		setShareSummary(shareSummary);
		setShareWith(shareWith);
		
		String[] tagsArray = tagsString.split(" ");
		ArrayList<Category> newTags = new ArrayList<Category>();
		for(String tag : tagsArray) {
			Category tagCategory = this.getFactory().newCategory();
			tagCategory.setScheme(null);
			tagCategory.setTerm(tag);
			newTags.add(tagCategory);
		}
		setTags(newTags);
	}
	
	/*public FileEntry(File file, String title, String content, String description, String tagsString, Permissions permission, boolean notification, Notification commentNotification, Notification mediaNotification, Date created, Date modified, boolean includePath, boolean propagate, SharePermission sharePermission) {
		super();
		
		setTitle(title);
		setContent(content);
		setSummary(description);
		setPublished(created);
		setUpdated(modified);
		setCreated(created);
		setModified(modified);
		setFile(file);
		setPermission(permission);
		setNotification(notification);
		setCommentNotification(commentNotification);
		setMediaNotification(mediaNotification);
		setIncludePath(includePath);
		setPropagate(propagate);
		setSharePermission(sharePermission);
		setShareSummary(shareSummary);
		setShareWith(shareWith);
		
		String[] tagsArray = tagsString.split(" ");
		ArrayList<Category> newTags = new ArrayList<Category>();
		for(String tag : tagsArray) {
			Category tagCategory = this.getFactory().newCategory();
			tagCategory.setScheme(null);
			tagCategory.setTerm(tag);
			newTags.add(tagCategory);
		}
		setTags(newTags);
	}	*/
	
	public FileEntry(File file, String title, String description, String tagsString, Permissions permission, boolean notification, Notification commentNotification, Notification mediaNotification, Date created, Date modified, boolean includePath, boolean propagate, SharePermission sharePermission, String shareSummary, String shareWith,boolean isExternal) {
		super();
		
		setTitle(title);
		setContent(description);
		setSummary(description);
		setPublished(created);
		setUpdated(modified);
		setCreated(created);
		setModified(modified);
		setFile(file);
		setPermission(permission);
		setNotification(notification);
		setCommentNotification(commentNotification);
		setMediaNotification(mediaNotification);
		setIncludePath(includePath);
		setPropagate(propagate);
		setSharePermission(sharePermission);
		setShareSummary(shareSummary);
		setShareWith(shareWith);
		
		String[] tagsArray = tagsString.split(" ");
		ArrayList<Category> newTags = new ArrayList<Category>();
		for(String tag : tagsArray) {
			Category tagCategory = this.getFactory().newCategory();
			tagCategory.setScheme(null);
			tagCategory.setTerm(tag);
			newTags.add(tagCategory);
		}
		setTags(newTags);
		setExternal(isExternal);
	}	
	
	public FileEntry(File file, InputStream infile, String title, String description, String tagsString, Permissions permission, boolean notification, Notification commentNotification, Notification mediaNotification, Date created, Date modified, boolean includePath, boolean propagate, SharePermission sharePermission, String shareSummary, String shareWith) {
		super();
		
		setTitle(title);
		setContent(description);
		setSummary(description);
		setPublished(created);
		setUpdated(modified);
		setCreated(created);
		setModified(modified);
		setFile(file);
		setPermission(permission);
		setNotification(notification);
		setCommentNotification(commentNotification);
		setMediaNotification(mediaNotification);
		setIncludePath(includePath);
		setPropagate(propagate);
		setSharePermission(sharePermission);
		setShareSummary(shareSummary);
		setShareWith(shareWith);
		setInputStream(infile);
		
		String[] tagsArray = tagsString.split(" ");
		ArrayList<Category> newTags = new ArrayList<Category>();
		for(String tag : tagsArray) {
			Category tagCategory = this.getFactory().newCategory();
			tagCategory.setScheme(null);
			tagCategory.setTerm(tag);
			newTags.add(tagCategory);
		}
		setTags(newTags);
	}
	
	public FileEntry(File file, String title, String description, String tagsString, Permissions permission, boolean notification, Notification commentNotification, Notification mediaNotification, Date created, Date modified, boolean includePath, boolean propagate, SharePermission sharePermission, String shareSummary, String shareWith, String shareWhat, String comment) {
		super();
		
		setTitle(title);
		setContent(description);
		setSummary(description);
		setPublished(created);
		setUpdated(modified);
		setCreated(created);
		setModified(modified);
		setFile(file);
		setPermission(permission);
		setNotification(notification);
		setCommentNotification(commentNotification);
		setMediaNotification(mediaNotification);
		setIncludePath(includePath);
		setPropagate(propagate);
		setSharePermission(sharePermission);
		setShareSummary(shareSummary);
		setShareWith(shareWith);
		setSharedWhat(shareWhat);
		setComment(comment);
		
		String[] tagsArray = tagsString.split(" ");
		ArrayList<Category> newTags = new ArrayList<Category>();
		for(String tag : tagsArray) {
			Category tagCategory = this.getFactory().newCategory();
			tagCategory.setScheme(null);
			tagCategory.setTerm(tag);
			newTags.add(tagCategory);
		}
		setTags(newTags);
	}
	
	public FileEntry(File file, InputStream infile, String title, String description, String tagsString, Permissions permission, boolean notification, Notification commentNotification, Notification mediaNotification, Date created, Date modified, boolean includePath, boolean propagate, SharePermission sharePermission, String shareSummary, String shareWith, String shareWhat, String comment) {
		super();
		
		setTitle(title);
		setContent(description);
		setSummary(description);
		setPublished(created);
		setUpdated(modified);
		setCreated(created);
		setModified(modified);
		setFile(file);
		setPermission(permission);
		setNotification(notification);
		setCommentNotification(commentNotification);
		setMediaNotification(mediaNotification);
		setIncludePath(includePath);
		setPropagate(propagate);
		setSharePermission(sharePermission);
		setShareSummary(shareSummary);
		setShareWith(shareWith);
		setSharedWhat(shareWhat);
		setComment(comment);
		setInputStream(infile);
		
		String[] tagsArray = tagsString.split(" ");
		ArrayList<Category> newTags = new ArrayList<Category>();
		for(String tag : tagsArray) {
			Category tagCategory = this.getFactory().newCategory();
			tagCategory.setScheme(null);
			tagCategory.setTerm(tag);
			newTags.add(tagCategory);
		}
		setTags(newTags);
	}
	
	@Override
	public Entry toEntry() {
		Entry entry = getFactory().newEntry();
		
		entry.setTitle(getTitle());
		entry.addSimpleExtension(StringConstants.TD_LABEL, getTitle());
		//entry.setContent("", Utils.getMimeType(getFile()));
		entry.setContent(getContent());
		if ( getSummary() != null){
			entry.setSummary(getSummary());
		}else {
			entry.setSummary(getContent());
		}
		entry.setPublished(getPublished());
		entry.setUpdated(getUpdated());
		//entry.setExternal(isExternal());
		
		if(getCreated() != null)
			entry.addSimpleExtension(StringConstants.TD_CREATED, String.valueOf(getCreated().getTime()));
		
		if(getModified() != null)
			entry.addSimpleExtension(StringConstants.TD_MODIFIED, String.valueOf(getModified().getTime()));
		
		if(getPermission() != null)
			entry.addSimpleExtension(StringConstants.TD_VISIBILITY, getPermission().toString().toLowerCase());
		
		if(isNotification())
			entry.addSimpleExtension(StringConstants.TD_NOTIFICATION, String.valueOf(isNotification()));
		
		if(isPropagate())
			entry.addSimpleExtension(StringConstants.TD_PROPAGATE, String.valueOf(isPropagate()));
		
		if(isExternal())
			//extension.addSimpleExtension(StringConstants.SNX_USERID, getShareWith());
			entry.addSimpleExtension(StringConstants.SNX_ISEXTERNAL, String.valueOf(isExternal()));
		
		for(Category tag : getTags()) {
			entry.addCategory(tag);
		}
		
		Category isDocumentCategory = getFactory().newCategory();
		isDocumentCategory.setScheme(StringConstants.SCHEME_TD_TYPE);
		isDocumentCategory.setTerm("document");
		isDocumentCategory.setLabel("document");
		entry.addCategory(isDocumentCategory);
		
		return entry;
	}
	
	public Entry toFolderEntry() {
		Entry entry = getFactory().newEntry();
		
		entry.setTitle(getTitle());
		entry.addSimpleExtension(StringConstants.TD_LABEL, getTitle());
		//entry.setContent("", Utils.getMimeType(getFile()));
		entry.setContent(getContent());
		if ( getSummary() != null){
			entry.setSummary(getSummary());
		}else {
			entry.setSummary(getContent());
		}
		entry.setPublished(getPublished());
		entry.setUpdated(getUpdated());
		//entry.setExternal(isExternal());
		
		if(getCreated() != null)
			entry.addSimpleExtension(StringConstants.TD_CREATED, String.valueOf(getCreated().getTime()));
		
		if(getModified() != null)
			entry.addSimpleExtension(StringConstants.TD_MODIFIED, String.valueOf(getModified().getTime()));
		
		if(getPermission() != null)
			entry.addSimpleExtension(StringConstants.TD_VISIBILITY, getPermission().toString().toLowerCase());
		
		if(isNotification())
			entry.addSimpleExtension(StringConstants.TD_NOTIFICATION, String.valueOf(isNotification()));
		
		if(isPropagate())
			entry.addSimpleExtension(StringConstants.TD_PROPAGATE, String.valueOf(isPropagate()));
		
		if(isExternal())
			//extension.addSimpleExtension(StringConstants.SNX_USERID, getShareWith());
			entry.addSimpleExtension(StringConstants.SNX_ISEXTERNAL, String.valueOf(isExternal()));
		
		for(Category tag : getTags()) {
			entry.addCategory(tag);
		}
		
		Category isDocumentCategory = getFactory().newCategory();
		isDocumentCategory.setScheme(StringConstants.SCHEME_TD_TYPE);
		isDocumentCategory.setTerm("collection");
		isDocumentCategory.setLabel("collection");
		entry.addCategory(isDocumentCategory);
		
		return entry;
	}
	
	public String getRequestParams() {
		String requestParams = "?";
		
		if(getTitle() != null)
		{
			try {
				requestParams += "label=" + URLEncoder.encode(getTitle(), "UTF-8")+ "&";
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(getSummary() != null)
		{
			try {
				requestParams += "description=" + URLEncoder.encode(getSummary(), "UTF-8")+ "&";
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(getCreated() != null)
		{
			requestParams += "created=" + String.valueOf(getCreated().getTime()) + "&";
		}
		
		if(getModified() != null)
		{
			requestParams += "modified=" + String.valueOf(getModified().getTime()) + "&";
		}
		
		if(getCommentNotification() != null)
			requestParams += "commentNotification=" + String.valueOf(getCommentNotification()).toLowerCase() + "&";
		
		if(isIncludePath())
			requestParams += "includePath=" + String.valueOf(isIncludePath()) + "&";

		if(getMediaNotification() != null)
			requestParams += "mediaNotification=" + String.valueOf(getMediaNotification()).toLowerCase() + "&";
		
		//if(isPropagate())
			requestParams += "propagate=" + String.valueOf(isPropagate()) + "&";
		
		if(getSharePermission() != null){
			try{
				requestParams += "sharePermission=" + String.valueOf(getSharePermission()).substring(0, 1).toUpperCase() + String.valueOf(getSharePermission()).toLowerCase().substring(1) + "&";
			}catch(Exception e){
				e.printStackTrace();
				requestParams += "sharePermission=" + String.valueOf(getSharePermission()).toLowerCase() + "&";
			}
		}
		
		if(getShareSummary() != null && getShareSummary().length() != 0) {
			try {
				requestParams += "shareSummary=" +  URLEncoder.encode(getShareSummary(), "UTF-8") + "&";
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	
		if(getShareWith() != null && getShareWith().length() != 0) {
			try {
				String[] userids = getShareWith().split(" ");
				for(String id : userids) {
					requestParams += "shareWith=" + URLEncoder.encode(id, "UTF-8") + "&";
				}
			} catch (UnsupportedEncodingException e) {
				// Should not occur.
			}
		}
		
		if(getTags() != null && getTags().size() != 0) {
			/**
			 * Please note that this currently only supports one tag per file
			 */
			List<Category> listOfTags = getTags();
			try {
				String fileTag = listOfTags.get(0).getTerm();
				requestParams += "tag=" + URLEncoder.encode(fileTag, "UTF-8") + "&";
			} catch(UnsupportedEncodingException e) {
				// Should not occur
			}
		}
		return requestParams;
	}
	
	public Entry toEntry(String categoryType) {
		Entry entry = getFactory().newEntry();
		
		entry.setTitle(getTitle());
		entry.addSimpleExtension(StringConstants.TD_LABEL, getTitle());
		if (getComment() != null) {
			entry.setContent(getComment());
		} else entry.setContent(getContent());

		entry.setSummary(getContent());
		entry.setPublished(getPublished());
		entry.setUpdated(getUpdated());
		
		if(getCreated() != null)
			entry.addSimpleExtension(StringConstants.TD_CREATED, String.valueOf(getCreated().getTime()));
		
		if(getModified() != null)
			entry.addSimpleExtension(StringConstants.TD_MODIFIED, String.valueOf(getModified().getTime()));
		
		if(getPermission() != null)
			entry.addSimpleExtension(StringConstants.TD_VISIBILITY, getPermission().toString().toLowerCase());
		
		if(isNotification())
			entry.addSimpleExtension(StringConstants.TD_NOTIFICATION, String.valueOf(isNotification()));
		
		if(isPropagate())
			entry.addSimpleExtension(StringConstants.TD_PROPAGATE, String.valueOf(isPropagate()));
		
		if(getShareWith() != null) {
			ExtensibleElement extension = entry.addExtension(StringConstants.TD_SHARED_WITH);
		    extension = extension.addExtension(StringConstants.TD_USER);
//		    extension.addSimpleExtension(QNAME, value); 
//			entry.addSimpleExtension(StringConstants.TD_USER, getShareWith());
			extension.addSimpleExtension(StringConstants.SNX_USERID, getShareWith());
//			extension.addSimpleExtension(new QName("", "name", ""), "ajones10");
//			entry.addSimpleExtension(StringConstants.SNX_USERID, getShareWith());
		}
		
		if(getSharedWhat() != null)
			entry.addSimpleExtension(StringConstants.TD_SHARED_WHAT, getSharedWhat());
		
//		if (getComment() != null) {
//	//		ExtensibleElement extension = entry.addExtension("", "content", "").setAttributeValue("type", "text");
//
//			QName qname = new QName("", "content", "");
//			ExtensibleElement extension = entry.addSimpleExtension(qname, getComment()).setAttributeValue("type", "text");
//		//	entry.addSimpleExtension(arg0, getComment());
//		}
		
		for(Category tag : getTags()) {
			entry.addCategory(tag);
		}
		
		Category isDocumentCategory = getFactory().newCategory();
		isDocumentCategory.setScheme(StringConstants.SCHEME_TD_TYPE);
		isDocumentCategory.setTerm(categoryType);
		isDocumentCategory.setLabel(categoryType);
//		isDocumentCategory.setTerm("document");
//		isDocumentCategory.setLabel("document");
		entry.addCategory(isDocumentCategory);
		
		return entry;
	}
	
	public Entry toShareEntry() {
		Entry entry = getFactory().newEntry();
		
//		entry.setTitle(getTitle());
		
//		if (getComment() != null) {
//			entry.setContent(getComment());
//		} else entry.setContent("", Utils.getMimeType(getFile()));

		entry.setSummary(getContent());
//		entry.setPublished(getPublished());
//		entry.setUpdated(getUpdated());
		
//		if(getPermission() != null)
//			entry.addSimpleExtension(StringConstants.TD_VISIBILITY, getPermission().toString().toLowerCase());
		
//		if(isNotification())
//			entry.addSimpleExtension(StringConstants.TD_NOTIFICATION, String.valueOf(isNotification()));
		
//		if(isPropagate())
//			entry.addSimpleExtension(StringConstants.TD_PROPAGATE, String.valueOf(isPropagate()));
		
		if(getShareWith() != null) {
			ExtensibleElement extension = entry.addExtension(StringConstants.TD_SHARED_WITH);
		    extension = extension.addExtension(StringConstants.TD_USER);
//		    extension.addSimpleExtension(QNAME, value); 
//			entry.addSimpleExtension(StringConstants.TD_USER, getShareWith());
			extension.addSimpleExtension(StringConstants.SNX_USERID, getShareWith());
//			extension.addSimpleExtension(new QName("", "name", ""), "ajones10");
//			entry.addSimpleExtension(StringConstants.SNX_USERID, getShareWith());
		}
		
		if(getSharedWhat() != null)
			entry.addSimpleExtension(StringConstants.TD_SHARED_WHAT, getSharedWhat());
		
		if(getCreated() != null)
			entry.addSimpleExtension(StringConstants.TD_CREATED, String.valueOf(getCreated().getTime()));
		
		if(getModified() != null)
			entry.addSimpleExtension(StringConstants.TD_MODIFIED, String.valueOf(getModified().getTime()));
//		if (getComment() != null) {
//	//		ExtensibleElement extension = entry.addExtension("", "content", "").setAttributeValue("type", "text");
//
//			QName qname = new QName("", "content", "");
//			ExtensibleElement extension = entry.addSimpleExtension(qname, getComment()).setAttributeValue("type", "text");
//		//	entry.addSimpleExtension(arg0, getComment());
//		}
		
//		for(Category tag : getTags()) {
//			entry.addCategory(tag);
//		}
		
		Category isDocumentCategory = getFactory().newCategory();
		isDocumentCategory.setScheme(StringConstants.SCHEME_TD_TYPE);
		isDocumentCategory.setTerm("share");
		isDocumentCategory.setLabel("share");
//		isDocumentCategory.setTerm("document");
//		isDocumentCategory.setLabel("document");
		entry.addCategory(isDocumentCategory);
		
		return entry;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public SharePermission getSharePermission() {
		return sharePermission;
	}

	public void setSharePermission(SharePermission sharePermission) {
		this.sharePermission = sharePermission;
	}

	public Permissions getPermission() {
		return permission;
	}

	public void setPermission(Permissions permission) {
		this.permission = permission;
	}

	public boolean isNotification() {
		return notification;
	}

	public void setNotification(boolean notification) {
		this.notification = notification;
	}

	public Notification getCommentNotification() {
		return commentNotification;
	}

	public void setCommentNotification(Notification commentNotification2) {
		this.commentNotification = commentNotification2;
	}

	public Notification getMediaNotification() {
		return mediaNotification;
	}

	public void setMediaNotification(Notification mediaNotification) {
		this.mediaNotification = mediaNotification;
	}

	public boolean isIncludePath() {
		return includePath;
	}

	public void setIncludePath(boolean includePath) {
		this.includePath = includePath;
	}

	public boolean isPropagate() {
		return propagate;
	}

	public void setPropagate(boolean propagate) {
		this.propagate = propagate;
	}

	public String getShareSummary() {
		return shareSummary;
	}

	public void setShareSummary(String shareSummary) {
		this.shareSummary = shareSummary;
	}

	public String getShareWith() {
		return shareWith;
	}

	public void setShareWith(String shareWith) {
		this.shareWith = shareWith;
	}
	
	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}
	
	public void setSharedWhat(String shareWhat) {
		this.shareWhat = shareWhat;
	}
	
	public String getSharedWhat() {
		return shareWhat;
	}
	
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public String getComment() {
		return comment;
	}
	
	public boolean isExternal() {
		return isExternal;
	}

	public void setExternal(boolean isExternal) {
		this.isExternal = isExternal;
	}
	
	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}
	
	public Date getModified() {
		return modified;
	}

	public void setModified(Date modified) {
		this.modified = modified;
	}
}
