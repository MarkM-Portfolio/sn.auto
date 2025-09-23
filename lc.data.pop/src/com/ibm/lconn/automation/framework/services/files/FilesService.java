package com.ibm.lconn.automation.framework.services.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.lconn.automation.framework.services.common.LCService;
import com.ibm.lconn.automation.framework.services.common.LCServiceException;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.DeploymentType;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Notification;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Permissions;
import com.ibm.lconn.automation.framework.services.common.StringConstants.SharePermission;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.common.Utils;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.files.nodes.FileComment;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;

/**
 * Forums Service object handles getting/posting data to the Connections Forums service.
 * 
 * @author Piyush K. Agarwal - pagarwal@us.ibm.com
 */
public class FilesService extends LCService {

	//private HashMap<String, String> filesURLs;
	//private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
	protected final static Logger LOGGER = LoggerFactory.getLogger(FilesService.class.getName());
	
	public FilesService(AbderaClient client, ServiceEntry service) throws LCServiceException {
		super(client, service);
		
		//updateServiceDocument();
	}
	
	public FilesService(AbderaClient client, ServiceEntry service, Map<String, String> headers) throws LCServiceException {
		super(client, service);
		for(String key : headers.keySet()){			
			this.options.setHeader(key, headers.get(key));			
		}	
		//updateServiceDocument();
	}

	private void updateServiceDocument() throws LCServiceException {
		ExtensibleElement feed = getFeed(service.getServiceURLString() + URLConstants.FILES_SERVICE);
		
		if(feed != null) {
			if(getRespStatus() == 200){
				setFoundService(true);
				//filesURLs = getCollectionUrls((Service) feed); // Files url's from collection currently unused
			} else {
				setFoundService(false);
				throw new LCServiceException("Error : Can't get FilesService Feed, status: " + getRespStatus());
			}
		} else {
			setFoundService(false);
			throw new LCServiceException("Error : Can't get FilesService Feed, status: " + getRespStatus());
		}
	}
	
	/**
	 * Post a file to the File's service based on the given meta data.
	 * IJF 3/20/2014 NOTE: 	This method does not actually upload any file content! (file size will be 0)
	 * 					   	Use uploadFile(), which POSTs an InputStream (binary data), 
	 * 						if you want to actually upload a file
	 * 
	 * @param fileMetaData - information that describes the file and its contents.
	 * @return true if file was successfully posted, false otherwise.
	 * @throws FileNotFoundException - if a file was not found at the specified file path.
	 */
	@Deprecated
	public ExtensibleElement createFile(FileEntry fileMetaData) {
		String methodName = "createFile";
		LOGGER.debug(methodName + " : POST : postFeed ");
		
		ExtensibleElement result = null;
		ExtensibleElement metaDataResult = postFeed(service.getServiceURLString() + URLConstants.FILES_MY_LIBRARY + fileMetaData.getRequestParams(), fileMetaData.toEntry("document"));
		
		if(metaDataResult != null) {
			Entry entry = (Entry) metaDataResult;
			//String apierror = entry.getAttributeValue(StringConstants.API_ERROR);
			//boolean b = Boolean.parseBoolean(entry.getAttributeValue(StringConstants.API_ERROR))
			if((entry.getAttributeValue(StringConstants.API_ERROR) == null) && fileMetaData.getInputStream() != null 
					&& Boolean.parseBoolean(entry.getAttributeValue(StringConstants.API_ERROR))!= true ) {
				Link editLink = entry.getEditLink();
				ClientResponse response = client.put(editLink.getHref().toString(), fileMetaData.getInputStream());
				LOGGER.debug(methodName + " : PUT : "+editLink.getHref().toString());
				LOGGER.debug(methodName + " : Status : "+response.getStatus());
				result = (ExtensibleElement) response.getDocument().getRoot();
				///System.out.println(response.getStatus() + ":" + response.getStatusText());
				///System.out.println(result);
				assert(result != null);
			} else {
				result = entry;
			}
		}
		
		return result;
	}
	
	public ExtensibleElement createMyFile(FileEntry fileMetaData) {
		String url = service.getServiceURLString() + URLConstants.FILES_MY_LIBRARY;
		
		return createFile(url, fileMetaData);
	}
	
	public ExtensibleElement createFile(String url, FileEntry fileMetaData) {

		ExtensibleElement metaDataResult = null;
		if ( fileMetaData.getInputStream() != null ){
			metaDataResult = postFeed(url, fileMetaData.getInputStream());
		} else {
			metaDataResult = postFeed(url, fileMetaData.toEntry("document"));
		};
		
		LOGGER.debug("Response of createFile(): " + getRespStatus());
		
		return metaDataResult;
	}
	
	public ExtensibleElement postFileToMyUserLibrary(FileEntry fileMetaData) {
		
		ExtensibleElement result = null;
		if ( fileMetaData.getInputStream() != null ){
			result = postFeed(service.getServiceURLString() + URLConstants.FILES_MY_LIBRARY + fileMetaData.getRequestParams(), fileMetaData.getInputStream());
		} else {
			result = postFeed(service.getServiceURLString() + URLConstants.FILES_MY_LIBRARY + fileMetaData.getRequestParams(), fileMetaData.toEntry("document"));
		}
		return result;
	}
	
	
	public String getURLString(){
		return (service.getServiceURLString());
	}
	
	public ExtensibleElement createFileNoInputStream(FileEntry fileMetaData){
		return postFeed(service.getServiceURLString() + URLConstants.FILES_MY_LIBRARY + fileMetaData.getRequestParams(), fileMetaData.toEntry());
	}
		
	public ExtensibleElement downloadFile(String documentid) {
		return getFeed(service.getServiceURLString() + URLConstants.FILES_MY_DOCUMENT + documentid + "/media/fileName");
	}
	
	public ExtensibleElement downloadFileWithRedirect(String documentid) {
		return getFeedWithRedirect(service.getServiceURLString() + URLConstants.FILES_MY_DOCUMENT + documentid + "/media/fileName");
	}
	
	public ExtensibleElement getFileFromMyuserlibraryByUuid(String documentUUID) {
		// /basic/api/myuserlibrary/document/{document-id}/entry 
		return getFeed(service.getServiceURLString() + URLConstants.FILES_MY_DOCUMENT + documentUUID + "/entry");
	}
	
	public ExtensibleElement addFilesToFolder(String folderUUID, ArrayList<String> filesList) {
		String filesParm = "";
		Iterator<String> it = filesList.iterator();

		while(it.hasNext()) {
			filesParm = filesParm + "itemId=" + it.next() + "&";
		}
		
		Entry entry = Abdera.getInstance().newEntry();
		// /basic/api/collection/{collection-id}/feed?itemId=file1&itemId=file2
		return postFeed(service.getServiceURLString() + URLConstants.FILE_FOLDER_INFO + folderUUID + "/feed?" + filesParm, entry);
	}
	
	public ExtensibleElement addFileToFolders(ExtensibleElement bogusFileElement, String folderUUID, ArrayList<String> filesList) {
		String filesParm = "";
		Iterator<String> it = filesList.iterator();

		while(it.hasNext()) {
			filesParm = filesParm + "itemId=" + it.next() + "&";
		}
		
		// /basic/api/collection/{collection-id}/feed?itemId=file1&itemId=file2
		return postFeed(service.getServiceURLString() + URLConstants.FILE_FOLDER_INFO + folderUUID + "/feed?" + filesParm, bogusFileElement);
	}
	
	public boolean removeFileFromFolder(String folderUUID, String fileUUID) {
		// 	/basic/api/collection/{collection-id}/feed?itemId=
		return deleteFeed(service.getServiceURLString() + URLConstants.FILE_FOLDER_INFO + folderUUID + "/feed?itemId=" + fileUUID);
	}
	
	public ExtensibleElement updateFileTag(ExtensibleElement fileElement, String documentUUID, String addTag) {
		InputStream fs = getFileInputStream(null);		
		return putFeed(service.getServiceURLString() + URLConstants.FILES_MY_DOCUMENT + documentUUID + "/entry?tag=" + addTag, fs);
	}

	
	public ExtensibleElement updateFileWithNameChange(String documentUUID, String newFileName) {	
		InputStream fs = getFileInputStream(null);		
		return putFeed(service.getServiceURLString() + URLConstants.FILES_MY_DOCUMENT + documentUUID + "/entry?label=" + newFileName + "&title=" + newFileName, fs);
	}

	public ExtensibleElement createFolder(FileEntry folderTest){
		return postFeed(service.getServiceURLString() + URLConstants.FILES_FOLDERS + folderTest.getRequestParams(), folderTest.toFolderEntry());
	}

	public ExtensibleElement createSubFolder(String parentFolderId, String subFolderLabel, String urlParametersString){
		Entry entry_subFolder_1 = Abdera.getInstance().newEntry();
	    entry_subFolder_1.addCategory(StringConstants.SCHEME_TD_TYPE, "collection", "collection");
	    entry_subFolder_1.setTitle(subFolderLabel);
	    
	    String url = service.getServiceURLString()  
				+ URLConstants.FILE_FOLDER_INFO + parentFolderId
				+ "/feed";
	    if(urlParametersString != null){
	    	url += urlParametersString;
	    }
	    
		return postFeed(url, entry_subFolder_1);
	}
	
	/*public ExtensibleElement createFolder(Entry folderTest) throws FileNotFoundException{
		return postFeed(service.getServiceURLString() + URLConstants.FILES_FOLDERS, folderTest);
	}*/
	
	public ExtensibleElement createCommunityFolder(FileEntry folderTest, String communityUUID){
		return postFeed(service.getServiceURLString() + URLConstants.FILES_COMMUNITY_LIBRARY + communityUUID + "/feed" + folderTest.getRequestParams(), folderTest.toEntry());
	}
	
	public ExtensibleElement createCommunityFolder(Entry folderEntry, String params, String communityUUID){
		String url = service.getServiceURLString() + URLConstants.FILES_COMMUNITY_COLLECTION + communityUUID + "/feed";
		if (params != null) {
			url += params;
		}
		return postFeed(url, folderEntry);
	}
	
	public ExtensibleElement retrieveFileFolder(String folderUUID) {
		// /basic/api/collection/{collection-id}/entry
		return getFeed(service.getServiceURLString() + URLConstants.FILE_FOLDER_INFO + folderUUID + "/entry");
	}
	
	public ExtensibleElement createFileShare(FileEntry fileMetaData) {
		// /basic/api/shares/feed
		ExtensibleElement metaDataResult = postFeed(service.getServiceURLString() + URLConstants.FILES_SHARES_3_0, fileMetaData.toEntry("share"));
		
		return metaDataResult;
	}
	
	public ExtensibleElement createFileShare(FileEntry fileMetaData, String fileUUID, String userUUID) {
		// /basic/api/shares/feed
		ExtensibleElement metaDataResult = postFeed(service.getServiceURLString() + URLConstants.FILES_PERSON_LIBRARY_AUTH + userUUID + "/document/" + fileUUID + "/feed", fileMetaData.toEntry("share"));
		
		return metaDataResult;
	}
	
	public ExtensibleElement createFolderShare(String folderId, String role, String memberType, String memberId) {
		// role: reader, editor, manager(for owner)
		// memberType: user, community
		// memberId: 20000038 or members@{communityUuid}
		/*
		 * <feed xmlns="http://www.w3.org/2005/Atom">
   <entry>
      <content xmlns:atom="http://www.w3.org/2005/Atom" atom:type="application/xml">
         <role xmlns="http://www.ibm.com/xmlns/prod/composite-applications/v1.0" 
         xmlns:ca="http://www.ibm.com/xmlns/prod/composite-applications/v1.0" 
         ca:type="reader">
            <member ca:id="20000038" ca:type="user"/>
         </role>
      </content>
   </entry>
   <entry>
      <content xmlns:atom="http://www.w3.org/2005/Atom" atom:type="application/xml">
         <role xmlns="http://www.ibm.com/xmlns/prod/composite-applications/v1.0" 
         xmlns:ca="http://www.ibm.com/xmlns/prod/composite-applications/v1.0" 
         ca:type="reader">
            <member ca:id="members@5733b17c-66e0-44da-a69e-a5ff9dabcac4" ca:type="community"/>
         </role>
      </content>
   </entry>
</feed>
		 */
		Feed feed = Abdera.getNewFactory().newFeed();
	    Entry entry = Abdera.getNewFactory().newEntry();

	    ExtensibleElement roleElement = Abdera.getNewFactory().newElement(StringConstants.CA_WIKI_MEMBER_ROLE);	    
	    roleElement.setAttributeValue(StringConstants.CA_WIKI_MEMBER_TYPE, role);

	    ExtensibleElement memberElement = (ExtensibleElement) roleElement.addExtension(StringConstants.CA_WIKI_MEMBER);
	    memberElement.setAttributeValue(StringConstants.CA_WIKI_MEMBER_TYPE, memberType);
	    memberElement.setAttributeValue(StringConstants.CA_WIKI_MEMBER_ID, memberId);

	    entry.setContent(roleElement);
	    feed.addEntry(entry);
	    
		// /files/form/api/collection/affeec29-e4d3-42aa-a394-22b77376242d/roles
		ExtensibleElement folderShareResult = postFeed(service.getServiceURLString() 
				+ URLConstants.FILE_FOLDER_INFO + folderId + "/roles", feed);
		
		return folderShareResult;
	}

	public ExtensibleElement retrieveFileShare(String shareUUID) {
		// /basic/api/share/{share-id}/entry 
		return getFeed(service.getServiceURLString() + URLConstants.FILES_GET_SHARE + shareUUID + "/entry");
	}
	
	public boolean deleteFileShare(String fileUUID) {
		// /basic/api/shares/feed?sharedWhat={document-id}
		return deleteFeed(service.getServiceURLString() + URLConstants.FILES_SHARES_3_0 + "?sharedWhat=" + fileUUID);
	}

	public ExtensibleElement createFileComment(FileEntry fileMetaData, String fileUUID, String userUUID) {
		// /basic/api/userlibrary/{userid}/document/{document-id}/feed
		ExtensibleElement metaDataResult = postFeed(service.getServiceURLString() + URLConstants.FILES_PERSON_LIBRARY_AUTH + userUUID + "/document/" + fileUUID + "/feed", fileMetaData.toEntry("comment"));

		return metaDataResult;
	}
	
	public ExtensibleElement retrieveFileComment(String documentUUID, String commentUUID) {
		// 	/basic/api/myuserlibrary/document/{document-id}/comment/{comment-id}/entry
		return getFeed(service.getServiceURLString() + URLConstants.FILES_MY_DOCUMENT + documentUUID + "/comment/" + commentUUID + "/entry");
	}
	
	public boolean deleteFileComment(String documentUUID, String commentUUID) {
		// /basic/api/myuserlibrary/document/{document-id}/comment/{comment-id}/entry
		return deleteFeed(service.getServiceURLString() + URLConstants.FILES_MY_DOCUMENT + documentUUID + "/comment/" + commentUUID + "/entry");
	}
	
	public ExtensibleElement updateFileComment(ExtensibleElement fileCommentElement, String documentUUID, String commentUUID) {
		// /basic/api/myuserlibrary/document/{document-id}/comment/{comment-id}/entry
		return putFeed(service.getServiceURLString() + URLConstants.FILES_MY_DOCUMENT + documentUUID + "/comment/" + commentUUID + "/entry", fileCommentElement);
	}
	
	public ExtensibleElement updateCommunityFileComment(ExtensibleElement fileCommentElement,String communityUUID, String documentUUID, String commentUUID) {
		// /basic/api/communitylibrary/{community-id}/document/{document-id}/comment/{comment-id}/entry
		return putFeed(service.getServiceURLString() + URLConstants.FILES_COMMUNITY_LIBRARY + communityUUID + "/document/" + documentUUID + "/comment/" + commentUUID + "/entry", fileCommentElement);
	}

	public ExtensibleElement retrieveFileFromTrash(String documentUUID) {
		// /basic/api/myuserlibrary/view/recyclebin/{document-id}/entry
		return getFeed(service.getServiceURLString() + URLConstants.FILES_RETRIEVE_FROM_RECYCLE_BIN + documentUUID + "/entry");
	}

	public boolean purgeFileFromTrash(String documentUUID) {
		// /basic/api/myuserlibrary/view/recyclebin/{document-id}/entry
		return deleteFeed(service.getServiceURLString() + URLConstants.FILES_PURGE_FROM_TRASH + documentUUID + "/entry");
	}
	
	@Deprecated
	public ExtensibleElement createFile(FileEntry fileMetaData, boolean createVersion) {
		ExtensibleElement result = null;
		ExtensibleElement metaDataResult = postFeed(service.getServiceURLString() + URLConstants.FILES_MY_LIBRARY + fileMetaData.getRequestParams() + "createVersion=" + createVersion, fileMetaData.toEntry());
		if(metaDataResult != null) {
			Entry entry = (Entry) metaDataResult;
			if((entry.getAttributeValue(StringConstants.API_ERROR) != null) && Boolean.parseBoolean(entry.getAttributeValue(StringConstants.API_ERROR)) != true) {
	//			Link editLink = entry.getEditLink();
	//			ClientResponse response = client.put(editLink.getHref().toString(), new FileInputStream(fileMetaData.getFile()));
//				result = (ExtensibleElement) response.getDocument().getRoot();
//				Reporter.log(response.getStatus() + ":" + response.getStatusText());
//				System.out.println(response.getStatus() + ":" + response.getStatusText());
				assert(result != null);
			} else {
				result = entry;
			}
		}
		
		return result;
	}
	
	public ExtensibleElement retrieveVersionOfFile(String documentUUID, String versionUUID) {
		// /basic/api/myuserlibrary/document/{document-id}/version/{version-id}/entry 
		return getFeed(service.getServiceURLString() + URLConstants.FILES_VERSION + documentUUID + "/version/" + versionUUID + "/entry");
	}

	public ExtensibleElement downloadVersionOfFile(String documentUUID, String versionUUID) {
		// /basic/api/myuserlibrary/document/{document-id}/version/{version-id}/media 
		return getFeed(service.getServiceURLString() + URLConstants.FILES_MY_DOCUMENT + documentUUID + "/version/" + versionUUID + "/media");
	}
	
	public boolean deleteAllVersionsOfFile(String documentUUID) {
		// /basic/api/myuserlibrary/document/{document-id}/feed
//		return deleteFeed(service.getServiceURLString() + URLConstants.FILES_MY_DOCUMENT + documentUUID + "/feed?category=version");
		return deleteFeed(service.getServiceURLString() + URLConstants.FILES_MY_DOCUMENT + documentUUID + "/feed?category=version&deleteFrom=1");
	}
	
	public ExtensibleElement getAllVersionsOfFile(String documentUUID){
		return getFeed(service.getServiceURLString() + URLConstants.FILES_MY_DOCUMENT + documentUUID + "/feed?category=version");
	}
	
	public boolean deleteVersionOfFile(String libId, String docId, String versionId){
		///basic/api/library/{library-id}/document/{document-id}/version/{version-label}/entry 
		return deleteFeed(service.getServiceURLString() + URLConstants.FILES_LIBRARY + libId + "/document/" + docId + "/version/" + versionId + "/entry");
	}
	
	public boolean deleteVersionOfFileUser(String docId, String versionId){
		///basic/api/library/{library-id}/document/{document-id}/version/{version-label}/entry 
		return deleteFeed(service.getServiceURLString() + URLConstants.FILES_VERSION + docId + "/version/" + versionId + "/entry");
	}

	public boolean deleteVersionOfFileUserId(String userId, String docId, String versionId){
		///basic/api/userlibrary/{user-id}/document/{document-idOrLabel}/version/{version-id}/entry
		return deleteFeed(service.getServiceURLString() + URLConstants.FILES_PERSON_LIBRARY_AUTH + userId + "/document/" + docId + "/version/" + versionId + "/entry");
	}
	
	public ExtensibleElement pinningFile(String fileDocumentUUID, ExtensibleElement bogusFileElement) {
		// 	/basic/api/myfavorites/documents/feed
		return postFeed(service.getServiceURLString() + URLConstants.FILES_PINNED + "?itemId=" + fileDocumentUUID, bogusFileElement);
	}
	
	public boolean unPinningFile(String fileDocumentUUID) {
		// 	/basic/api/myfavorites/documents/feed
		return deleteFeed(service.getServiceURLString() + URLConstants.FILES_PINNED + "?itemId=" + fileDocumentUUID);
	}
	
	public ExtensibleElement pinningFolder(String folderUUID, ExtensibleElement bogusFileElement) {
		// /basic/api/myfavorites/collections/feed
		return postFeed(service.getServiceURLString() + URLConstants.FILES_PINNED_FOLDERS + "?itemId=" + folderUUID, bogusFileElement);
	}
	
	public boolean unPinningFolder(String folderUUID) {
		// /basic/api/myfavorites/collections/feed
		return deleteFeed(service.getServiceURLString() + URLConstants.FILES_PINNED_FOLDERS + "?itemId=" + folderUUID);
	}

	//Get feed from Url
	public ExtensibleElement getUrlFeed(String url){
		return getFeed(url);
	}
	
	public ExtensibleElement getUrlFeedWithRedirect(String url){
		return getFeedWithRedirect(url);
	}
	
	/**
	 * Warning: The alternative method "postRecommendation(url)" uses deprecated code, use this instead
	 * @param url to file; format: {ServerBaseURL}/files/basic/api/library/{Library-ID}/document/{Document-ID}/feed
	 * @param recommendationEntry is an entry formatted as a recommendation.
	 * @see FilesPopulate getRecommendations()
	 * @return response in the form of ExtensibleElement
	 */
	public ExtensibleElement postRecommendationToFile(String url, Entry recommendationEntry){
		return postFeed(url, recommendationEntry);
	}
	
	public ExtensibleElement postUrlFeed(String url, ExtensibleElement file){
		return postFeed(url, file);
	}

	public ExtensibleElement postAttachmentToFile(String fileUUID, String title, String filePath){
		Map<String, String> requestHeaders = new HashMap<String, String>();
		requestHeaders.put("Slug", title);
		requestHeaders.put("X-Title", title);
		requestHeaders.put("X-Update-Nonce", "true");
		
		String fileFeedUrl = service.getServiceURLString() + URLConstants.FILES_DOCUMENT + fileUUID + "/feed" + "?category=attachment";
		return postFeed(fileFeedUrl, getFileInputStream(filePath), requestHeaders);
	}

	public ExtensibleElement createVersionToFile(String fileUUID){
		Entry entry = Abdera.getNewFactory().newEntry();
	    entry.setContent("new version content");
		
		String url = service.getServiceURLString()  
				+  URLConstants.FILES_DOCUMENT  + fileUUID 
				+ "/entry"
				+ "?createVersion=true&opId=replace&X-Method-Override=PUT&format=xml"; 
		return postFeed(url, entry);
	}
	
	//Get feed from public Files filtered by date
	public ExtensibleElement getPublicDateFilteredFeed(String parameter){	
		long timePastMS = (new Date()).getTime() - 24*60*60*1000; //subtract a day of ms
		String parm = "&" + parameter + "=" + timePastMS;			
		
		//if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD || 
		//		StringConstants.DEPLOYMENT_TYPE == DeploymentType.MULTI_TENANT ){
			return getFeed(service.getServiceURLString() + URLConstants.FILES_ALL_SC + parm);
		//} else {
		//	return getFeed(service.getServiceURLString() + URLConstants.FILES_ALL + parm);
		//}
	}
	
	public ExtensibleElement getPublicFeed(){
		/*if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD ||
				StringConstants.DEPLOYMENT_TYPE == DeploymentType.MULTI_TENANT ){
			return getFeed(service.getServiceURLString() + URLConstants.FILES_ALL_SC);
		} else {
			return getFeed(service.getServiceURLString() + URLConstants.FILES_ALL);
		}*/
		return getFeed(service.getServiceURLString() + URLConstants.FILES_ALL_SC);
	}
	
	public ExtensibleElement getPublicFeedInDepth(String para){
		/*if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD){
	        Document<Feed> feed_doc = client.get(service.getServiceURLString() + URLConstants.FILES_ALL_SC + para).getDocument();
	        return feed_doc.getRoot();
		}else {
			return getFeed(service.getServiceURLString() + URLConstants.FILES_ALL + para);
		}*/
		return getFeed(service.getServiceURLString() + URLConstants.FILES_ALL_SC + para);
	}
	
	//Get feed from your pinned files
	public ExtensibleElement getMyPinnedFilesFeed(){
		return getFeed(service.getServiceURLString() + URLConstants.FILES_PINNED);
	}
	
	//Get feed from your pinned folders
	public ExtensibleElement getMyPinnedFoldersFeed(){
		return getFeed(service.getServiceURLString() + URLConstants.FILES_PINNED_FOLDERS);
	}
	
	//get feed from public folders
	public ExtensibleElement getPublicFolderFeed(){
		/*if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD ||
				StringConstants.DEPLOYMENT_TYPE == DeploymentType.MULTI_TENANT ) {
			return getFeed(service.getServiceURLString() + URLConstants.FILES_PUBLIC_FOLDERS_SC );			
		} else {
			return getFeed(service.getServiceURLString() + URLConstants.FILES_PUBLIC_FOLDERS);
		}*/
		return getFeed(service.getServiceURLString() + URLConstants.FILES_PUBLIC_FOLDERS_SC );
	}
	
	//get feed from my library
	public ExtensibleElement getMyLibraryFeed(){
		return getFeed(service.getServiceURLString() + URLConstants.FILES_MY_LIBRARY);
	}
	
	public ExtensibleElement getMyLibraryFeed(String param){
		return getFeed(service.getServiceURLString() + URLConstants.FILES_MY_LIBRARY + "?" + param);
	}
	
	public ExtensibleElement getMyFeed(String string){
		return getFeed(string);
	}
	
	/*public HttpResponse doHttpGet(String url) throws Exception{
		return super.doHttpGet(url);
	}*/
	
	//get feed from tags
	public String getTagsFeed() {
		// /files/basic/api/tags/feed
		return getResponseString(service.getServiceURLString() + URLConstants.FILES_TAGS + "?tag=cool&scope=document");
	}
	
	public String getTagsFeed(String tag) {
		// /files/basic/api/tags/feed
		return getResponseString(service.getServiceURLString() + URLConstants.FILES_TAGS + "?tag="+tag+"&scope=document");
	}
	
	//get feed from folders that recently had files added to them
	public ExtensibleElement getRecentFolderFeed(){
		return getFeed(service.getServiceURLString() + URLConstants.FILES_RECENT_ADDEDTO_FOLDERS);
	}
	
	public ExtensibleElement getFilesInFolderFeed(String folderID) {
		return getFeed(service.getServiceURLString() + URLConstants.FILES_IN_FOLDER + "/" + folderID + "/feed");
	}
	
	public ExtensibleElement getFoldersFeed(){
		return getFeed(service.getServiceURLString() + URLConstants.FILES_FOLDERS + "?ps=100");
	}
	
	public ExtensibleElement getFoldersFeed(String params){
		return getFeed(service.getServiceURLString() + URLConstants.FILES_FOLDERS + "?" + params);
	}
	
	public ExtensibleElement getUserLibraryFeed(String userID) {
		return getFeed(service.getServiceURLString() + URLConstants.FILES_COMMENTS_ACCESS + "/" + userID + "/feed");

	}
	
	public ExtensibleElement getUserLibraryEntry(String userID) {
		return getFeed(service.getServiceURLString() + URLConstants.FILES_COMMENTS_ACCESS + "/" + userID + "/entry");

	}
	
	//get feed for my file shares
	public ExtensibleElement getFileSharesFeed(){
		return getFeed(service.getServiceURLString() + URLConstants.FILES_SHARES);
	}
	
	public ExtensibleElement getFilesCommentsFeed(String userID, String documentID) {
		/* /basic/api/userlibrary/{userid}/document/{document-id}/feed?category=comment */
		return getFeed(service.getServiceURLString() + URLConstants.FILES_COMMENTS_ACCESS + "/" + userID + "/document/" + documentID + "/feed?catagory=comment");
	}
	
	public ExtensibleElement getMyFilesCommentsFeed(String documentID) {
		/* /basic/api/myuserlibrary/document/{document-id}/feed?category=comment */
		return getFeed(service.getServiceURLString() + URLConstants.FILES_MY_DOCUMENT + documentID + "/feed?category=comment");
	}
	
	//gets the feed of files in the current users recycle bin
	public ExtensibleElement getFilesInRecycleBinFeed(){
		return getFeed(service.getServiceURLString() + URLConstants.FILES_RECYCLE_BIN);
	}

	
	//restores an item from the trash
	public ExtensibleElement restoreFileFromTrash(String documentId, Entry fileEntry){
		return putFeed(service.getServiceURLString() + "/basic/api/myuserlibrary/view/recyclebin/" + documentId + "/entry?undelete=true", fileEntry);
	}
	
	public ExtensibleElement getDownloadFeed(String documentId){
		String feedString =  "";
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD  || 
				StringConstants.DEPLOYMENT_TYPE == DeploymentType.MULTI_TENANT ){
			feedString = service.getServiceURLString() + URLConstants.FILES_DOCUMENT+ documentId+ "/media";
			return getFeedWithRedirect(feedString);
		} else {
			//feedString = service.getServiceURLString() + "/basic/anonymous/api/document/"+ documentId+ "/media";
			feedString = service.getServiceURLString() + URLConstants.FILES_DOCUMENT+ documentId+ "/media";
			return getFeed(feedString);
		}
	
	}
	
	public ExtensibleElement getFileMetaDataFeed(String documentId){
		
		//if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD ||
		//		StringConstants.DEPLOYMENT_TYPE == DeploymentType.MULTI_TENANT ){
			return getFeed(service.getServiceURLString() + URLConstants.FILES_DOCUMENT + documentId + "/entry?includeTags=true");
		//} else {
		//	return getFeed(service.getServiceURLString() + "/basic/anonymous/api/document/" + documentId + "/entry?includeTags=true");
		//}
		
	}
	
	public ExtensibleElement updateFileMetaData(String documentId, FileEntry fileMetaData){
		return putFeed(service.getServiceURLString() + URLConstants.FILES_MY_DOCUMENT + documentId + "/entry", fileMetaData.toEntry());
	}
	
	public ExtensibleElement updateFolderMetaData(String documentId, FileEntry folderMetaData){
		return putFeed(service.getServiceURLString() + URLConstants.FILES_IN_FOLDER + "/" + documentId + "/entry", folderMetaData.toEntry());
	}
	
	public ExtensibleElement updateItemMetaData(String url, ExtensibleElement libMetaData){
		return putFeed(url, libMetaData);
	}
	
	public boolean purgeAllFilesFromTrash(){
		return deleteFeed(service.getServiceURLString() + URLConstants.FILES_RECYCLE_BIN);
	}
	
	/*public ExtensibleElement getFileFolder(String documentId){
		return getFeed(service.getServiceURLString() + "/basic/api/collection/" + documentId + "/entry");
	}*/
	
	public boolean deleteFileFolder(String documentId){
		return deleteFeed(service.getServiceURLString() + "/basic/api/collection/" + documentId + "/entry");
	}
	
	public ExtensibleElement updateFileFolder(String documentId, FileEntry updateFolder){
		return putFeed(service.getServiceURLString() + "/basic/api/collection/" + documentId + "/entry", updateFolder.toEntry());
	}
	
	
	/**
	 * Delete a file based on the given document id.
	 * @param documentid - value of <td:uuid> or <td:label> elements in a file's Atom entry document
	 * @return true if successfully deleted, false otherwise.
	 */
	public boolean deleteFile(String documentid) {
		return deleteFeed(service.getServiceURLString() + URLConstants.FILES_MY_DOCUMENT + documentid + "/entry");
	}
	
	public boolean deleteFileFullUrl(String fullUrlToFile) {
		return deleteFeed(fullUrlToFile);
	}
	
	public boolean deleteCMISFile(String fullURLToTheFile){
		return deleteFeed(fullURLToTheFile);
	}
	
	// communityElement can be Entry or Feed (feed can contain multiple community entries)
	// NOTE: Entries must be formatted like this (a share permission can be included as well):
	// <entry>
	// 	 <category term="community" label="community" scheme="tag:ibm.com,2006:td/type"></category>
	//   <itemId xmlns="urn:ibm.com/td">{communityUuid}</itemId>
	// </entry>
	public ExtensibleElement shareFileWithCommunity(String fileDocumentID, ExtensibleElement communityElement) {
		return postFeed(service.getServiceURLString() + URLConstants.FILES_MY_DOCUMENT + fileDocumentID + "/feed", communityElement);
	}
	
	public ExtensibleElement getFilesServiceDocument(){
		return getFeed(service.getServiceURLString() + "/basic/api/introspection");
	}
	
	public ExtensibleElement getMyServiceDocument(){
		return getFeed(service.getServiceURLString() + "/basic/cmis/my/servicedoc");
	}
	
	public ExtensibleElement createCommunityFileNoInputStream(String publishURL,FileEntry fileMetaData){
		return postFeed(publishURL, fileMetaData.toEntry());
	}
	
	public ExtensibleElement createCommunityFile(String publishURL,FileEntry fileMetaData, File file){
		return postMultipartFeed(publishURL, fileMetaData.toEntry(), file);
	}
	
	public ExtensibleElement updateCommunityFileNoInputStream(String editURL,FileEntry fileMetaData){
		return putFeed(editURL, fileMetaData.toEntry());
	}
	
	public ExtensibleElement restoreCommunityFile(String editURL,Entry entry){
		String restoreUrl = editURL.replace(editURL.substring(editURL.indexOf("/library/")), editURL.substring(editURL.indexOf("/document/")))+"?submit=true&createVersion=true";
		return postFeed(restoreUrl, entry);
	}
	
	public ExtensibleElement postFileComment(String commentLink,FileComment comment) {
		if (commentLink.startsWith("http"))
			return postFeed(commentLink, comment.toEntry());
		else
			return postFeed(service.getServiceURLString() + commentLink, comment.toEntry());
	}
	
	public ExtensibleElement getModSerDoc(){
		return getFeed(service.getServiceURLString() + "/basic/api/moderation/atomsvc");
	}	
	
	public ExtensibleElement getInfoByUsersEmail(String emailID) {
		// /files/basic/api/people/feed?email=dummy%40janet.iris.com 
		return getFeed(service.getServiceURLString() + URLConstants.FILES_USER_SEARCH + "?email=" + emailID);
	}
	
	public ExtensibleElement getPeopleSelfFeed() { 
		return getFeed(service.getServiceURLString() + URLConstants.FILES_USER_SEARCH + "?self=true&format=xml");
	}
	
	public String getPeopleSelfJSON() {
		return getResponseString(service.getServiceURLString() + URLConstants.FILES_USER_SEARCH + "?self=true");
	}

	public int unlockFile(String fileUUID) {
		// /files/basic/api/document/fileuuid/lock
		return deleteWithResponseStatus(service.getServiceURLString() + URLConstants.FILES_DOCUMENT + fileUUID + "/lock");
	}
	
	public ExtensibleElement updateFileMetaDataMultiPartPut(String documentId, String parametersToUpdate, FileEntry fileMetaData){
		String feedString = service.getServiceURLString() + URLConstants.FILES_MY_DOCUMENT + documentId + "/entry" + parametersToUpdate;
		return putFeed(feedString,fileMetaData.toEntry());
	}
	
	public ExtensibleElement createFileMultiPartPost(String params, FileEntry metaData){
		return postFeed(service.getServiceURLString() + URLConstants.FILES_MY_LIBRARY + params, metaData.toEntry("document"));
	}
	
	public String getNonce(){
		return getResponseString(URLConstants.SERVER_URL + URLConstants.FILES_BASE + "/basic/api/nonce");
	}
	
	public ExtensibleElement sharedByMeCompress(){
		return getFeed(service.getServiceURLString() + "/basic/api/documents/shared/media");
	}
	
	public ExtensibleElement sharedByMeCompress(String fileName){
		return getFeed(service.getServiceURLString() + "/basic/api/documents/shared/media/" + fileName);
	}
	
	public ExtensibleElement filesInLibCompressLibId(String libId){
		return getFeed(service.getServiceURLString() + URLConstants.FILES_LIBRARY + libId + "/media");
	}

	public ExtensibleElement filesInLibCompressLibId(String libId, String fileName, String titlePrefix){
		if (fileName == null) {
			return getFeed(service.getServiceURLString() + URLConstants.FILES_LIBRARY + libId + "/media?title=" + titlePrefix);
		} else {
			return getFeed(service.getServiceURLString() + URLConstants.FILES_LIBRARY + libId + "/media/" + fileName + "?title=" + titlePrefix);
		}
	}
	
	public ExtensibleElement filesInLibCompressUserId(String userId, String titlePrefix){
		return getFeed(service.getServiceURLString() + URLConstants.FILES_PERSON_LIBRARY_AUTH + userId + "/media" + "?title=" + titlePrefix);
	}

	public ExtensibleElement filesInLibCompressUserId(String userId, String fileName, String titlePrefix){
		return getFeed(service.getServiceURLString() + URLConstants.FILES_PERSON_LIBRARY_AUTH + userId + "/media/" + fileName + "?title=" + titlePrefix);
	}
	
	public ExtensibleElement filesInLibCompressMyUserLib(String titlePrefix){
		return getFeed(service.getServiceURLString() + "/basic/api/myuserlibrary/media" + "?title=" + titlePrefix);
	}

	public ExtensibleElement filesInLibCompressMyUserLib(String fileName, String titlePrefix){
		return getFeed(service.getServiceURLString() + "/basic/api/myuserlibrary/media/" + fileName + "?title=" + titlePrefix);
	}
	
	public ExtensibleElement filesInLibCompressFolder(String folderId){
		return getFeed(service.getServiceURLString() + "/basic/api/collection/" + folderId + "/media");
	}
	
	public ExtensibleElement filesInLibCompressFolder(String folderId, String fileName){
		return getFeed(service.getServiceURLString() + "/basic/api/collection/" + folderId + "/media/" + fileName);
	}
	
	public ExtensibleElement specifiedFilesCompressFile(){
		return getFeed(service.getServiceURLString() + "/basic/api/documents/media");
	}
	
	public ExtensibleElement specifiedFilesCompressFile(String folderId, String fileName){
		return getFeed(service.getServiceURLString() + "/basic/api/documents/media/" + fileName);
	}
	
	public String getUserListLibId(String libId, String docId) {
		return getResponseString(service.getServiceURLString() + URLConstants.FILES_LIBRARY + libId + "/document/" + docId + "/users/feed");
	}
	
	public String getUserListUserId(String userId, String docId) {
		return getResponseString(service.getServiceURLString() + URLConstants.FILES_PERSON_LIBRARY_AUTH + userId + "/document/" + docId + "/users/feed");
	}
	
	public ExtensibleElement getDocumentEntry(String libId, String docId){
		return getFeed(service.getServiceURLString() + URLConstants.FILES_LIBRARY + libId + "/document/" + docId + "/entry");
	}
	
	public ExtensibleElement getUsersShared(){
		return getFeed(service.getServiceURLString() + URLConstants.FILES_MYSHARES);
	}
	
	public ExtensibleElement getVersionUsingLibId(String libId, String docId, String versionId){
		return getFeed(service.getServiceURLString() + URLConstants.FILES_LIBRARY + libId + "/document/" + docId + "/rendition/" + versionId);
	}
	
	public ExtensibleElement getCollectionRoles(String collectionId){
		return getFeed(service.getServiceURLString() + URLConstants.FILE_FOLDER_INFO + collectionId + "/roles");
	}

	public String retrieveSingleCollectionRole(String collectionId, String roleType){
		return getResponseString(service.getServiceURLString() + URLConstants.FILE_FOLDER_INFO + collectionId + "/roles/"+roleType);
	}

	public ExtensibleElement getDocumentThumbnail(String docId){
		return getFeed(service.getServiceURLString() + URLConstants.FILES_DOCUMENT + docId + "/thumbnail");
	}

	public ExtensibleElement getCollectionsContainingFile(String fileUUID){
		return getFeed(service.getServiceURLString() + URLConstants.FILES_DOCUMENT + fileUUID + "/collections/feed");
	}

	public ExtensibleElement getCollectionsContainingFileInSpecifiedLibrary(String libUUID, String fileUUID){
		return getFeed(service.getServiceURLString() + URLConstants.FILES_LIBRARY + libUUID + "/document/" + fileUUID + "/collections/feed");
	}

	public ExtensibleElement getCollectionsContainingFileByUser(String userUUID, String fileUUID){
		return getFeed(service.getServiceURLString() + URLConstants.FILES_PERSON_LIBRARY_AUTH + userUUID + "/document/" + fileUUID + "/collections/feed");
	}

	public ExtensibleElement getCollectionsContainingFileMyUserLibrary(String fileUUID){
		return getFeed(service.getServiceURLString() + URLConstants.FILES_MY_DOCUMENT + fileUUID + "/collections/feed");
	}
	
	private InputStream getFileInputStream(String filePath) {
		
		if (filePath == null){
			filePath = "/resources/lamborghini_murcielago_lp640.jpg";
		}
		//convert file to InputStream 
		InputStream fs = null;
		fs = this.getClass().getResourceAsStream(filePath);
		if(fs == null){
			try {
				fs = new FileInputStream(filePath);
			} catch (FileNotFoundException e) {
				LOGGER.debug("ERROR: FILE NOT FOUND");
				e.printStackTrace();
			}
		}
		return fs;
	}

	public ExtensibleElement getCommunityLibraryFeed(String communityUuid) {
		return getFeed(service.getServiceURLString()+URLConstants.FILES_COMMUNITY_LIBRARY+communityUuid+"/feed");
	}

	public ExtensibleElement getCommunityCollectionFeed(String communityUuid){
		return getFeed(service.getServiceURLString()+URLConstants.FILES_COMMUNITY_COLLECTION+communityUuid+"/feed");
	}

	public boolean removeCommunityFileShare(String communityUuid, String fileDocumentId){
		return deleteFeed(service.getServiceURLString()+URLConstants.FILES_COMMUNITY_COLLECTION+communityUuid+"/feed?itemId="+fileDocumentId);
	}

	// Returns an entry
	public ExtensibleElement getCommunityLibraryInfo(String communityUuid){
		return getFeed(service.getServiceURLString()+URLConstants.FILES_COMMUNITY_LIBRARY+communityUuid+"/entry");
	}
	
	/**
	 * Gets a feed of my shares, either shared with me or shared by me
	 * @param shareDirection "inbound" = files shared with me,
	 * 					"outbound" = files shared by me
	 * @return HTTP response
	 */
	public ExtensibleElement getMySharesFeed(String shareDirection){
		return getFeed(service.getServiceURLString()+URLConstants.FILES_MYSHARES +"?direction="+shareDirection);
	}

	public ExtensibleElement getLibraryFeed(String libraryID){
		return getFeed(service.getServiceURLString()+URLConstants.FILES_LIBRARY+libraryID+"/feed");
	}
	
	public boolean deleteLibraryFile(String libraryID, String documentID){
		return deleteFeed(service.getServiceURLString()+URLConstants.FILES_LIBRARY+libraryID+"/document/"+documentID+"/entry");
	}
	
	public boolean deleteItem(String url){
		return deleteFeed(url);
	}
	
	public ExtensibleElement getLibraryTrashFeed(String libraryID){
		return getFeed(service.getServiceURLString()+URLConstants.FILES_LIBRARY+libraryID+"/view/recyclebin/feed");
	}
	
	public boolean purgeFileFromLibraryTrash(String libraryID, String documentID){
		return deleteFeed(service.getServiceURLString()+URLConstants.FILES_LIBRARY+libraryID+"/view/recyclebin/"+documentID+"/entry");
	}
	
	public boolean purgeAllFilesFromLibraryTrash(String libraryID){
		return deleteFeed(service.getServiceURLString()+URLConstants.FILES_LIBRARY+libraryID+"/view/recyclebin/feed");
	}
	
	public ExtensibleElement updateLibraryFileInfo(String libraryID, String documentID, FileEntry updatedFileInfo){
		return putFeed(service.getServiceURLString()+URLConstants.FILES_LIBRARY+libraryID+"/document/"+documentID+"/entry", updatedFileInfo.toEntry());
	}
	
	public ExtensibleElement getLibraryFileInfo(String libraryID, String documentID){
		return getFeed(service.getServiceURLString()+URLConstants.FILES_LIBRARY+libraryID+"/document/"+documentID+"/entry");
	}
	
	public ExtensibleElement getLibraryFileInTrash(String libraryID, String documentID){
		return getFeed(service.getServiceURLString()+URLConstants.FILES_LIBRARY+libraryID+"/view/recyclebin/"+documentID+"/entry");
	}
	
	public ExtensibleElement restoreLibraryFileFromTrash(String libraryID, String documentID, Entry fileEntry){
		return putFeed(service.getServiceURLString()+URLConstants.FILES_LIBRARY+libraryID+"/view/recyclebin/"+documentID+"/entry?undelete=true", fileEntry);
	}
	
	public ExtensibleElement getLibraryTags(String libraryID){
		return getFeed(service.getServiceURLString()+URLConstants.FILES_LIBRARY+libraryID+"/tags/feed?format=xml&pageSize=50");
	}
	
	public ExtensibleElement getLibraryTags(String libraryID, int pagesize, int page){
		return getFeed(service.getServiceURLString()+URLConstants.FILES_LIBRARY+libraryID+"/tags/feed?format=xml&pageSize="+pagesize+"&page="+page);
	}
	
	/**
	 * Method to actually upload file content by POSTing the file's InputStream
	 * @param fileEntry      FileEntry to upload containing inputstream
	 * @return server response EE
	 */
	public ExtensibleElement uploadFile(FileEntry fileEntry){
		return postFeed(service.getServiceURLString() + URLConstants.FILES_MY_LIBRARY + "?label="+fileEntry.getTitle(), fileEntry.getInputStream());
	}
	
	/**
	 * Method to actually upload file content by POSTing the file's InputStream
	 * This method supports tags and public / shared / private files in full
	 * 
	 * @param fileEntry - The FileEntry instance of the file to be uploaded to the server (must include the input stream of the file)
	 * @return - An ExtensibleElement instance of the server response
	 */
	public ExtensibleElement uploadNewFile(FileEntry fileEntry) {
		
		// Create the URL for uploading the file
		String uploadFileURL = service.getServiceURLString() + URLConstants.FILES_MY_LIBRARY 
								+ "?label=" + fileEntry.getTitle();
		
		if(fileEntry.getPermission().equals(Permissions.PUBLIC)) {
			uploadFileURL += "&visibility=public";
		} else {
			uploadFileURL += "&visibility=private";
		}
		
		if(fileEntry.getPermission().equals(Permissions.SHARED) && fileEntry.getShareWith() != null && fileEntry.getShareWith().length() != 0) {
			/**
			 * Please Note: Currently only supports sharing the file with one user per file upload
			 */
			uploadFileURL += "&shareWithEditors=" + fileEntry.getShareWith();
		}
		
		if(fileEntry.getTags() != null && fileEntry.getTags().size() != 0) {
			/**
			 * Please Note: Now supports the use of any number of tags per file upload
			 */
			for(int index = 0; index < fileEntry.getTags().size(); index ++) {
				uploadFileURL += "&tag=" + fileEntry.getTags().get(index).getTerm();
			}
		}
		
		// POST the file to the server
		return postFeed(uploadFileURL, fileEntry.getInputStream());
	}

	/**
	 * Method to actually upload file content by POSTing the file's InputStream
	 * @param fileTitle			desired file title
	 * @param fileInputStream	file binary data as an InputStream object
	 * @return
	 */
	public ExtensibleElement uploadFile(String fileTitle, InputStream fileInputStream){
		return postFeed(service.getServiceURLString() + URLConstants.FILES_MY_LIBRARY + "?label="+fileTitle, fileInputStream);
	}
	/**
	 * Method to actually upload file content by POSTing the file's InputStream
	 * @param fileTitle			desired file title
	 * @param fileInputStream	file binary data as an InputStream object
	 * @return
	 */
	/*public ExtensibleElement uploadFileToCommunity(FileEntry fileEntry, Community community){
		return postFeed(service.getServiceURLString() + URLConstants.FILES_COMMUNITY_LIBRARY + community.getUuid()+ "/feed?label="
				+fileEntry.getTitle(), fileEntry.getInputStream());
	}*/
	
	/**
	 * Method to actually upload file content by POSTing the file's InputStream
	 * @param fileTitle			desired file title
	 * @param fileInputStream	file binary data as an InputStream object
	 * @return
	 */
	public ExtensibleElement uploadFileToCommunity(FileEntry fileEntry, Community community){
		return postFeed(service.getServiceURLString() + URLConstants.FILES_COMMUNITY_LIBRARY + community.getUuid()+ "/feed" + fileEntry.getRequestParams(), fileEntry.getInputStream());
	}
	
	/**
	 * Method to flag one community file with Moderation of Files is enabled
	 * @param fileID		 the file ID to be flagged
	 * @return
	 */
	public ExtensibleElement FlagCommunityFile(String fileID){
			
		String url = service.getServiceURLString() + URLConstants.FILES_REPORT;
		
		Abdera abdera = new Abdera();
		Factory factory = abdera.getFactory();
		Entry reportEntry = factory.newEntry();
		
		reportEntry.declareNS("http://www.ibm.com/xmlns/prod/sn", "snx");
		QName extensionQName = new QName("snx:in-ref-to");
		Element element = reportEntry.addExtension(extensionQName);
		element.setAttributeValue("rel", "http://www.ibm.com/xmlns/prod/sn/report-item");
		element.setAttributeValue("ref", fileID);
		element.setAttributeValue("ref-item-type", "document");
		reportEntry.setContent("Crazy serious issue here.");

		return postFeed(url, reportEntry);
	}
	
	/**
	 * Method to flag one community file comment with Moderation of Files is enabled
	 * @param commentID		the comment ID to be flagged
	 * @return
	 */
	public ExtensibleElement FlagCommunityFileComment(String commentID){
		
		String url = service.getServiceURLString() + URLConstants.FILES_REPORT;
		
		Abdera abdera = new Abdera();
		Factory factory = abdera.getFactory();
		Entry reportEntry = factory.newEntry();
		
		reportEntry.declareNS("http://www.ibm.com/xmlns/prod/sn", "snx");
		QName extensionQName = new QName("snx:in-ref-to");
		Element element = reportEntry.addExtension(extensionQName);
		element.setAttributeValue("rel", "http://www.ibm.com/xmlns/prod/sn/report-item");
		element.setAttributeValue("ref", commentID);
		element.setAttributeValue("ref-item-type", "comment");
		reportEntry.setContent("Crazy serious issue here.");
		
		return postFeed(url, reportEntry);
	}
	
	public ExtensibleElement createFile(String url, String params, FileEntry fileMetaData) {
		return postFeed(url + params, fileMetaData.toEntry("document"));

	}

	public ExtensibleElement lockFile(String fileUUID){
		ExtensibleElement eEle = null;
		return postFeed(service.getServiceURLString() + URLConstants.FILES_DOCUMENT + fileUUID + "/lock?type=hard", eEle);
	}

	public boolean isFileEditable(String fileUUID){
		
		String timeStamp = Utils.logDateFormatter.format(new Date());
		String filename = "File_test_" + timeStamp;
		String fileContent = "file update test";
		FileEntry fileMetaData = new FileEntry(null, filename,
				"This is one description", "tag0String",
				Permissions.PRIVATE, true, Notification.ON, Notification.ON,
				null, null, true, true, SharePermission.VIEW,
				"Hello world, this is my private share!", null, null, fileContent);
		putFeed(service.getServiceURLString() + URLConstants.FILES_DOCUMENT + fileUUID + "/entry", fileMetaData.toEntry());
		if (getRespStatus() == 200) 
			return true;				
		else 
			return false;
	}			

	public ExtensibleElement moveItem(String itemCategory, String itemUUID, String sourceParentCatergory, String sourceParentUUID, 
			String targetParentCatergory, String targetParentUUID)
	{
		/*
		 <entry xmlns:td="urn:ibm.com/td" xmlns="http://www.w3.org/2005/Atom">
     		<category term="document" scheme="tag:ibm.com,2006:td/type" label="document"></category>
     		<td:itemId xmlns:td="urn:ibm.com/td">25badd1b-7d33-4ada-9ac9-1e126f0b47a3</td:itemId>
     		<td:source xmlns:td="urn:ibm.com/td">
          		<td:type xmlns:td="urn:ibm.com/td">collection</td:type>
          		<td:itemId xmlns:td="urn:ibm.com/td">b3d1b2d0-eeff-463c-8759-423b9971dd2e</td:itemId>
     		</td:source>
		</entry>
		 */
		// create entry
		Entry entry = Abdera.getNewFactory().newEntry();
	    ExtensibleElement categoryElement =(ExtensibleElement) entry.addExtension(StringConstants.ATOM_CATEGORY);
    	categoryElement.setAttributeValue("scheme", "tag:ibm.com,2006:td/type");
	    if("document".equalsIgnoreCase(itemCategory)) {
	    	categoryElement.setAttributeValue("term", "document");
	    	categoryElement.setAttributeValue("label", "document");
	    }
	    if("collection".equalsIgnoreCase(itemCategory)) {
	    	categoryElement.setAttributeValue("term", "collection");
	    	categoryElement.setAttributeValue("label", "collection");	    
	    }
	    
	    QName ITEM_ID = new QName("urn:ibm.com/td", "itemId", "td");
	    ExtensibleElement itemIdElement = (ExtensibleElement) entry.addExtension(ITEM_ID);
	    itemIdElement.setText(itemUUID);
	    
	    QName SOURCE = new QName("urn:ibm.com/td", "source", "td");
	    ExtensibleElement sourceElement = (ExtensibleElement) entry.addExtension(SOURCE);
	    ExtensibleElement itemInSourceElement = (ExtensibleElement) sourceElement.addExtension(ITEM_ID);
	    QName TYPE = new QName("urn:ibm.com/td", "type", "td");
	    ExtensibleElement typeElement = (ExtensibleElement) sourceElement.addExtension(TYPE);
	    if("collection".equalsIgnoreCase(sourceParentCatergory)) {
	    	typeElement.setText("collection");	
		    itemInSourceElement.setText(sourceParentUUID);    
	    }
	    if("communityroot".equalsIgnoreCase(sourceParentCatergory)) {
	    	typeElement.setText("communityroot");
	    }
	    if("personalroot".equalsIgnoreCase(sourceParentCatergory)) {
	    	typeElement.setText("personalroot");
	    }	 
	    
	    
	    // set url
	    String url = null;
	    if("collection".equalsIgnoreCase(targetParentCatergory)){
	    	url = service.getServiceURLString()
					+ URLConstants.FILE_FOLDER_INFO + targetParentUUID + "/feed";
	    }
	    if("communitycollection".equalsIgnoreCase(targetParentCatergory)){
	    	url = service.getServiceURLString()
					+ URLConstants.FILES_COMMUNITY_COLLECTION + targetParentUUID + "/feed";
	    }
	    if("collections".equalsIgnoreCase(targetParentCatergory)){
	    	url = service.getServiceURLString()
					+ URLConstants.FILES_PUBLIC_FOLDERS_SC;
	    }
	    
	    // send request
		return postFeed(url, entry);
	}
	
	public ExtensibleElement createFileComment(String libraryId, String documentId, FileEntry fileMetaData) {
		// /library/{library-id}/document/{document-id}/feed 
		ExtensibleElement metaDataResult = postFeed( service.getServiceURLString() + URLConstants.FILES_LIBRARY + libraryId + "/document/" + documentId + "/feed", fileMetaData.toEntry("comment"));

		return metaDataResult;
	}

	public ExtensibleElement retrieveFileComment(String libraryId, String documentId, String commentUUID) {
		// 	/library/{library-id}/document/{document-id}/comment/{comment-id}/entry
		return getFeed(service.getServiceURLString() + URLConstants.FILES_LIBRARY + libraryId + "/document/" + documentId + "/comment/" + commentUUID + "/entry");
	}

	public ExtensibleElement updateFileComment(String libraryId, String documentId, String commentUUID, ExtensibleElement fileCommentElement) {
		// /library/{library-id}/document/{document-id}/comment/{comment-id}/entry 
		return putFeed(service.getServiceURLString() + URLConstants.FILES_LIBRARY + libraryId + "/document/" + documentId  + "/comment/" + commentUUID + "/entry", fileCommentElement);
	}

	public boolean deleteFileComment(String libraryId, String documentId, String commentUUID) {
		// /library/{library-id}/document/{document-id}/comment/{comment-id}/entry
		return deleteFeed(service.getServiceURLString() + URLConstants.FILES_LIBRARY + libraryId + "/document/" + documentId  + "/comment/" + commentUUID + "/entry");
	}
	
}
