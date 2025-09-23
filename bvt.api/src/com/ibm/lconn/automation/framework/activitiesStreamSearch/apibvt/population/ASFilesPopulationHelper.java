package com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.population;

/*
 * 16.04.2012 - Yakov Vilenchik
 * Added new parameter "String fileInfile" to the standaloneFileCreation method to allow creation of InputStream infile object
 * This object is passed to the fileMetaData as parameter due to changes made by API automation team for Files population
 */

import java.io.File;
import java.io.InputStream;
import java.util.logging.Logger;

import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;

import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.FVTUtilsWithDate;
import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.PopStringConstantsAS;
import com.ibm.lconn.automation.framework.activitiesStreamSearch.utils.nodes.FvtMasterLogsClassPopulation;
import com.ibm.lconn.automation.framework.search.rest.api.RestAPIUser;
import com.ibm.lconn.automation.framework.search.rest.api.RestAPIUser.UserType;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Notification;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Permissions;
import com.ibm.lconn.automation.framework.services.common.StringConstants.SharePermission;
import com.ibm.lconn.automation.framework.services.files.FilesService;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;

public class ASFilesPopulationHelper {
	
	private static FilesService service;
	private static String userUUID;
    private RestAPIUser restAPIUser;
	protected static Logger LOGGER = FvtMasterLogsClassPopulation.LOGGER;

	
	
	public ASFilesPopulationHelper() throws Exception {
		restAPIUser = new RestAPIUser(UserType.ASSEARCH);
		ServiceEntry filesServiceEntry = restAPIUser.getService("files");
		restAPIUser.addCredentials(filesServiceEntry);
		service = new FilesService(restAPIUser.getAbderaClient(),
				filesServiceEntry);
		
	}

	

	public void createStandaloneFiles() {
		standaloneFileCreation(".\\src\\resources\\"
				+ PopStringConstantsAS.PUBLIC_STANDALONE_FILE_PATH,
				PopStringConstantsAS.PUBLIC_STANDALONE_FILE_NAME + " "
						+ PopStringConstantsAS.eventIdent, Permissions.PUBLIC,
				SharePermission.EDIT,
				PopStringConstantsAS.PUBLIC_STANDALONE_FILE_PATH,
				PopStringConstantsAS.PUBLIC_STANDALONE_FILE_TAG,
				PopStringConstantsAS.COMMENT_TO_PUBLIC_STANDALONE_FILE,
				PopStringConstantsAS.FILE_CONTENT, true);
		standaloneFileCreation(".\\src\\resources\\"
				+ PopStringConstantsAS.PRIVATE_STANDALONE_FILE_PATH,
				PopStringConstantsAS.PRIVATE_STANDALONE_FILE_NAME + " "
						+ PopStringConstantsAS.eventIdent, Permissions.PRIVATE,
				SharePermission.VIEW,
				PopStringConstantsAS.PRIVATE_STANDALONE_FILE_PATH,
				PopStringConstantsAS.PRIVATE_STANDALONE_FILE_TAG,
				PopStringConstantsAS.COMMENT_TO_PRIVATE_STANDALONE_FILE,
				PopStringConstantsAS.FILE_CONTENT, false);
	}
	public void createStandalonePrivateFile() {
		
		standaloneFileCreation(".\\src\\resources\\"
				+ PopStringConstantsAS.PRIVATE_STANDALONE_FILE_PATH,
				PopStringConstantsAS.PRIVATE_STANDALONE_FILE_NAME + " "
						+ PopStringConstantsAS.eventIdent, Permissions.PRIVATE,
				SharePermission.VIEW,
				PopStringConstantsAS.PRIVATE_STANDALONE_FILE_PATH,
				PopStringConstantsAS.PRIVATE_STANDALONE_FILE_TAG,
				PopStringConstantsAS.COMMENT_TO_PRIVATE_STANDALONE_FILE,
				PopStringConstantsAS.FILE_CONTENT, false);
	}
public void createStandaloneFilesWithoutComment() {
		
		standaloneFileCreation(".\\src\\resources\\"
				+ PopStringConstantsAS.PRIVATE_STANDALONE_FILE_PATH,
				PopStringConstantsAS.PRIVATE_STANDALONE_FILE_NAME + " "
						+ PopStringConstantsAS.eventIdent, Permissions.PRIVATE,
				SharePermission.VIEW,
				PopStringConstantsAS.PRIVATE_STANDALONE_FILE_PATH,
				PopStringConstantsAS.PRIVATE_STANDALONE_FILE_TAG,
				"",
				PopStringConstantsAS.FILE_CONTENT, false);
		standaloneFileCreation(".\\src\\resources\\"
				+ PopStringConstantsAS.PUBLIC_STANDALONE_FILE_PATH,
				PopStringConstantsAS.PUBLIC_STANDALONE_FILE_NAME + " "
						+ PopStringConstantsAS.eventIdent, Permissions.PUBLIC,
				SharePermission.EDIT,
				PopStringConstantsAS.PUBLIC_STANDALONE_FILE_PATH,
				PopStringConstantsAS.PUBLIC_STANDALONE_FILE_TAG,
				"",
				PopStringConstantsAS.FILE_CONTENT, true);
	}
	public void createModeratedCommunityFile() {
		standaloneFileCreation(".\\src\\resources\\"
				+ PopStringConstantsAS.MODERATED_COMMUNITY_FILE_PATH,
				PopStringConstantsAS.MODERATED_COMMUNITY_FILE_NAME + " "
						+ PopStringConstantsAS.eventIdent, Permissions.PUBLIC,
				SharePermission.EDIT,
				PopStringConstantsAS.MODERATED_COMMUNITY_FILE_PATH,
				PopStringConstantsAS.MODERATED_COMMUNITY_FILE_TAG,
				PopStringConstantsAS.COMMENT_TO_MODERATED_COMMUNITY_FILE,
				PopStringConstantsAS.FILE_CONTENT, false);
	}

	public void createPrivateCommunityFile() {
		standaloneFileCreation(".\\src\\resources\\"
				+ PopStringConstantsAS.PRIVATE_COMMUNITY_FILE_PATH,
				PopStringConstantsAS.PRIVATE_COMMUNITY_FILE_NAME + " "
						+ PopStringConstantsAS.eventIdent, Permissions.PRIVATE,
				SharePermission.VIEW,
				PopStringConstantsAS.PRIVATE_COMMUNITY_FILE_PATH,
				PopStringConstantsAS.PRIVATE_COMMUNITY_FILE_TAG,
				PopStringConstantsAS.COMMENT_TO_PRIVATE_COMMUNITY_FILE,
				PopStringConstantsAS.FILE_CONTENT, false);
	}

	public void createPublicCommunityFile() {
		standaloneFileCreation(".\\src\\resources\\"
				+ PopStringConstantsAS.PUBLIC_COMMUNITY_FILE_PATH,
				PopStringConstantsAS.PUBLIC_COMMUNITY_FILE_NAME + " "
						+ PopStringConstantsAS.eventIdent, Permissions.PUBLIC,
				SharePermission.EDIT,
				PopStringConstantsAS.PUBLIC_COMMUNITY_FILE_PATH,
				PopStringConstantsAS.PUBLIC_COMMUNITY_FILE_TAG,
				PopStringConstantsAS.COMMENT_TO_PUBLIC_COMMUNITY_FILE,
				PopStringConstantsAS.FILE_CONTENT, false);
	}

	public void populate() {
		try {
			
			createPublicCommunityFile();
			createPrivateCommunityFile();
			createModeratedCommunityFile();
			createStandaloneFiles();
		} catch (Exception e) {
			LOGGER.fine("Exception in Files population: " + e.getMessage());
		}
	}

	// *******************************************************************************************************************
	// *******************************************************************************************************************
	// Working functions
	// *******************************************************************************************************************
	// *******************************************************************************************************************

	public void standaloneFileCreation(String filePath, String fileName,
			Permissions filePermissions, SharePermission fileSharePermissions,
			String fileInfile, String fileTags, String commentToFile,
			String fileContent, Boolean updateFile) {
		String userRealUUID;
		if (service != null) {
			
			if (StringConstants.DEPLOYMENT_TYPE == StringConstants.DeploymentType.SMARTCLOUD) {
				userRealUUID = restAPIUser.getProfData().getUserId();
			}else{
				userUUID = FVTUtilsWithDate
						.getUserId(restAPIUser.getProfData().getRealName());
				String[] userIdparts = userUUID.split(":"); 
				int numberOfElements = userIdparts.length; 
				userRealUUID = userIdparts[numberOfElements - 1];
			}

			File file = new File(filePath);
			InputStream infile = this.getClass().getResourceAsStream(
					"/resources/" + fileInfile);

			String fileUUID = null;
			FileEntry fileMetaData = new FileEntry(file, infile, fileName,
					fileContent, fileTags, filePermissions, true,
					Notification.ON, Notification.ON, null, null, true, true,
					fileSharePermissions, "Hello world, this is my new share!",
					"510b99c0-0101-102e-893e-f78755f7e0ed 510b99c0-0101-102e-893f-f78755f7e0ed");

			Entry result = (Entry) service.createFile(fileMetaData);

			ExtensibleElement test = result
					.getExtension(StringConstants.TD_UUID);
			if (test != null) {
				fileUUID = test.getText();

				if (result.getAttributeValue(StringConstants.API_ERROR) != null) {
					if (Integer.parseInt(result.getExtension(
							StringConstants.API_RESPONSE_CODE).getText()) == 409) {
						LOGGER.fine(" Conflict file: " + fileName + " exists");
					} else {
						LOGGER.fine("File creation error ");
					}
				}else {
				if (commentToFile != ""){
				FileEntry fileMetaDataComment = new FileEntry(
						file,
						fileName,
						fileContent,
						fileTags,
						filePermissions,
						true,
						Notification.ON,
						Notification.ON,
						null,
						null,
						true,
						true,
						fileSharePermissions,
						"Hello world, this is my new private share!",
						null,
						"510b99c0-0101-102e-893e-f78755f7e0ed 510b99c0-0101-102e-893f-f78755f7e0ed",
						commentToFile);

				Entry result2 = (Entry) service.createFileComment(
						fileMetaDataComment, fileUUID, userRealUUID);
				if (((Entry) result2).getContent().compareToIgnoreCase(
						commentToFile) != 0) {

					LOGGER.fine("Failed Create File Comment");
				}
				}

				if (updateFile) {
					FileEntry fileUpdatedMetaData = new FileEntry(
							file,
							fileName,
							PopStringConstantsAS.FILE_CONTENT
									+ " "
									+ PopStringConstantsAS.FILE_CONTENT_UPDATE_WORD,
							fileTags,
							Permissions.PUBLIC,
							true,
							Notification.OFF,
							Notification.OFF,
							null,
							null,
							true,
							true,
							SharePermission.EDIT,
							"Hello world, this is my new private share!",
							null,
							"510b99c0-0101-102e-893e-f78755f7e0ed 510b99c0-0101-102e-893f-f78755f7e0ed",
							"");
					service.updateFileMetaData(fileUUID, fileUpdatedMetaData);
				}
				}
				

			} else {
				LOGGER.fine(" File UUID is not available");
			}

		} else {
			LOGGER.fine(" File Service is not available");
		}
	}

}
