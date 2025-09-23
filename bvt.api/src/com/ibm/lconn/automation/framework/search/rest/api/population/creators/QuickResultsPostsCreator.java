package com.ibm.lconn.automation.framework.search.rest.api.population.creators;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.apache.abdera.model.Element;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.protocol.client.ClientResponse;

import com.ibm.lconn.automation.framework.search.rest.api.RestAPIUser;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils;
import com.ibm.lconn.automation.framework.search.rest.api.RestAPIUser.UserType;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils.Purpose;
import com.ibm.lconn.automation.framework.search.rest.api.population.PopulatedData;
import com.ibm.lconn.automation.framework.search.rest.api.population.Populator;
import com.ibm.lconn.automation.framework.services.common.LCServiceException;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Notification;
import com.ibm.lconn.automation.framework.services.common.StringConstants.SharePermission;
import com.ibm.lconn.automation.framework.services.files.FilesService;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;
import com.ibm.lconn.automation.framework.services.search.request.quickresults.QuickResultsActivityEntryViewedPostRequest;
import com.ibm.lconn.automation.framework.services.search.request.quickresults.QuickResultsActivityViewedPostRequest;
import com.ibm.lconn.automation.framework.services.search.request.quickresults.QuickResultsFileFolderViewedPostRequest;
import com.ibm.lconn.automation.framework.services.search.request.quickresults.QuickResultsFileViewedPostRequest;
import com.ibm.lconn.automation.framework.services.search.request.quickresults.QuickResultsPostRequest;
import com.ibm.lconn.automation.framework.services.search.service.QuickResultsPostService;

public class QuickResultsPostsCreator {

	private final static Logger logger = Populator.LOGGER_POPUILATOR;

	public static String PPREFIX_NOW = Long
			.toString(System.currentTimeMillis());

	public static final String ACTIVITY_STANDALONE_TITLE_TODAY = PPREFIX_NOW
			+ " BVT fake population Test Quick Result activity only viewed today";
	public static final String ACTIVITY_ENTRY_STANDALONE_TITLE_TODAY = PPREFIX_NOW
			+ " BVT fake population Test Quick Result activity entry viewed today";
	
	public static final String ACTIVITY_STANDALONE_TITLE_YESTERDAY = PPREFIX_NOW
			+ " BVT fake population Test Quick Result activity viewed yesterday";

	public static final String ACTIVITY_STANDALONE_ID_TODAY = "261719b1-b1b6-4338-b422-0aef7f7382eb";
	public static final String ACTIVITY_ENTRY_STANDALONE_ID_TODAY = "261719b1-b1b6-4338-b422-40c8f4bf9c34";
	public static final String ACTIVITY_STANDALONE_ID_YESTERDAY = "e4261100-23b7-4343-95ec-40c8f4bf9c34";

	public static final String ACTIVITY_IN_COMMUNITY_TITLE_TODAY = PPREFIX_NOW
			+ " BVT fake population Test Quick Result activity in community  viewed today";

	public static final String ACTIVITY_IN_COMMUNITY_TITLE_YESTERDAY = PPREFIX_NOW
			+ " BVT fake population Test Quick Result activity in community viewed yesterday";

	public static final String ACTIVITY_IN_COMMUNITY_ID_TODAY = "371719b1-b1b6-4338-b422-0aef7f7382eb";

	public static final String ACTIVITY_IN_COMMUNITY_ID_YESTERDAY = "f5261100-23b7-4343-95ec-40c8f4bf9c34";

	public static final String FILE_STANDALONE_TITLE_TODAY = PPREFIX_NOW
			+ " BVT fake population Test Quick Result file only viewed today";
	public static final String FILE_FOLDER_STANDALONE_TITLE_TODAY = PPREFIX_NOW
			+ " BVT fake population Test Quick Result file folder viewed today";
	
	public static final String FILE_STANDALONE_TITLE_YESTERDAY = PPREFIX_NOW
			+ " BVT fake population Test Quick Result file viewed yesterday";

	public static final String FILE_STANDALONE_ID_TODAY = "261719b1-b1b6-4338-b422-0aef7f7382fc";
	public static final String FILE_FOLDER_STANDALONE_ID_TODAY = "7d7f0b35-1fb4-4d4a-a517-b9315e8e9d8b";

	public static final String FILE_STANDALONE_ID_YESTERDAY = "e4261100-23b7-4343-95ec-40c8f4bf9c45";

	public static final String FILE_IN_COMMUNITY_TITLE_TODAY = PPREFIX_NOW
			+ " BVT fake population Test Quick Result file in community  viewed today";

	public static final String FILE_IN_COMMUNITY_TITLE_YESTERDAY = PPREFIX_NOW
			+ " BVT fake population Test Quick Result file in community viewed yesterday";

	public static final String FILE_IN_COMMUNITY_ID_TODAY = "481719b1-b1b6-4338-b422-0aef7f7382eb";

	public static final String FILE_IN_COMMUNITY_ID_YESTERDAY = "f5261100-23b7-4343-95ec-40c8f4bf9c45";

	public static final String FILE_STANDALONE_TITLE_JMS = PPREFIX_NOW
			+ " JMS population Test Quick Result";

	public static final String COMMUNITY_ID = "f569fc58-5485-40fc-98bd-3b16c640eb68";

	public static final String QR_POPULATION_PREFIX = PPREFIX_NOW
			+ " fak pop Tes Qui Res";

	private long QUICKRESULTS_INDEXING_INTERVAL_MILLISEC = 30000;

	private String contentCreatorId;

	private RestAPIUser quickResultsUser;

	private ServiceEntry search;

	private QuickResultsPostService postService;

	private FilesService fileService;

	public QuickResultsPostsCreator() throws FileNotFoundException, IOException {

		

		sendPost(new QuickResultsActivityViewedPostRequest(
				ACTIVITY_STANDALONE_ID_TODAY, ACTIVITY_STANDALONE_TITLE_TODAY,
				contentCreatorId, null, null, null, null, null));
		sendPost(new QuickResultsActivityEntryViewedPostRequest(
				ACTIVITY_ENTRY_STANDALONE_ID_TODAY, ACTIVITY_ENTRY_STANDALONE_TITLE_TODAY,
				contentCreatorId, null, ACTIVITY_STANDALONE_ID_TODAY, ACTIVITY_STANDALONE_TITLE_TODAY, null, null));
		sendPost(new QuickResultsActivityViewedPostRequest(
				ACTIVITY_STANDALONE_ID_YESTERDAY,
				ACTIVITY_STANDALONE_TITLE_YESTERDAY, contentCreatorId, null,
				null, null, null, null));
		sendPost(new QuickResultsActivityViewedPostRequest(
				ACTIVITY_IN_COMMUNITY_ID_TODAY,
				ACTIVITY_IN_COMMUNITY_TITLE_TODAY, contentCreatorId, null,
				null, COMMUNITY_ID, null, null));
		sendPost(new QuickResultsActivityViewedPostRequest(
				ACTIVITY_IN_COMMUNITY_ID_YESTERDAY,
				ACTIVITY_IN_COMMUNITY_TITLE_YESTERDAY, contentCreatorId, null,
				null, COMMUNITY_ID, null, null));
		sendPost(new QuickResultsFileViewedPostRequest(
				FILE_STANDALONE_ID_TODAY, FILE_STANDALONE_TITLE_TODAY,
				contentCreatorId, null, null, null, null, null));
		sendPost(new QuickResultsFileViewedPostRequest(
				FILE_STANDALONE_ID_YESTERDAY, FILE_STANDALONE_TITLE_YESTERDAY,
				contentCreatorId, null, null, null, null, null));
		sendPost(new QuickResultsFileFolderViewedPostRequest(
				FILE_FOLDER_STANDALONE_ID_TODAY, FILE_FOLDER_STANDALONE_TITLE_TODAY,
				contentCreatorId, null, null, null, null, null));
		sendPost(new QuickResultsFileViewedPostRequest(
				FILE_IN_COMMUNITY_ID_TODAY, FILE_IN_COMMUNITY_TITLE_TODAY,
				contentCreatorId, null, null, COMMUNITY_ID, null, null));
		sendPost(new QuickResultsFileViewedPostRequest(
				FILE_IN_COMMUNITY_ID_YESTERDAY,
				FILE_IN_COMMUNITY_TITLE_YESTERDAY, contentCreatorId, null,
				null, COMMUNITY_ID, null, null));
		createFileJMS(FILE_STANDALONE_TITLE_JMS);
		logger.fine("QuickResultsPosts:Waiting for indexing "
				+ QUICKRESULTS_INDEXING_INTERVAL_MILLISEC / 1000 + " sec");
		try {
			Thread.sleep(QUICKRESULTS_INDEXING_INTERVAL_MILLISEC);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	private void createFileJMS(String fileTitle) throws FileNotFoundException,
			IOException {

		quickResultsUser = new RestAPIUser(UserType.QUICKRESULTS);
		ServiceEntry filesServiceEntry = quickResultsUser.getService("files");
		try {
			fileService = new FilesService(quickResultsUser.getAbderaClient(),
					filesServiceEntry);
		} catch (LCServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		

		if (fileService != null) {
			FileEntry newFileEntry = createFileEntry(fileTitle);
			if (newFileEntry != null){
				logger.fine("createFileJMS newFileEntry: " + newFileEntry.toString());
				
			
			ExtensibleElement response = fileService.postFileToMyUserLibrary(newFileEntry);
			Element codeElement = response
					.getExtension(new QName("api", "code"));
			if (codeElement != null) {
				logger.log(Level.WARNING, "The file is not created:"+codeElement.toString());

			} else {

				logger.fine("File created: " + response.toString());
			}
			}else {

				logger.fine("File is not created created: Entry is NULL");
			}
			
			
		} else {
			logger.log(Level.WARNING, "createFileJMS: The Files service is NULL.");
		}

	}

	private FileEntry createFileEntry(String fileTitle) throws IOException {
		File tempFile = createTempFile(fileTitle, ".txt");
		FileEntry newFileEntry = new FileEntry(tempFile);
		newFileEntry.setTitle(fileTitle);
		newFileEntry.setContent(fileTitle);
		newFileEntry.setComment(fileTitle);
		newFileEntry.setPermission(StringConstants.Permissions.PRIVATE);
		logger.fine("Create file entry: " + newFileEntry.toString());
		return newFileEntry;
	}

	private File createTempFile(String title, String extention)
			throws IOException {
		File tempFile = File.createTempFile(title, extention);
		tempFile.deleteOnExit();
		BufferedWriter out = new BufferedWriter(new FileWriter(tempFile));
		out.write(title + "\n");
		out.flush();
		out.close();
		return tempFile;
	}

	private void sendPost(QuickResultsPostRequest quickResultsPostRequest)
			throws FileNotFoundException, IOException {
		quickResultsUser = new RestAPIUser(UserType.QUICKRESULTS);
		contentCreatorId = getViewerExternalId(quickResultsUser);
		search = quickResultsUser.getService("search");
		
		if (search == null) {
			logger.fine("Search not available for sendPost");
		} else {
			postService = new QuickResultsPostService(
					quickResultsUser.getAbderaClient(), search);
			postService.setAbderaClientCookies(quickResultsUser.getAbderaClient(),quickResultsUser.getProfData().getEmail(),quickResultsUser.getProfData().getPassword());
			ClientResponse cr = postService
					.postQuickResultsEvent(quickResultsPostRequest);
			logger.fine("responce to POST:"
					+ quickResultsPostRequest.toString() + ": response :"
					+ cr.getStatus());
		}

	}

	private String getViewerExternalId(RestAPIUser quickResultsUser) {

		String externalId = quickResultsUser.getProfData().getUserId();
		if (externalId == null) {
			externalId = "8df02bc0-f6df-1032-9b62-d02a14283ea9";
		}

		return externalId;
	}
}