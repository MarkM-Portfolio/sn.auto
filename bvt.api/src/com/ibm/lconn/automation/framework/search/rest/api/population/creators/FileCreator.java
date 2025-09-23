package com.ibm.lconn.automation.framework.search.rest.api.population.creators;

import static org.testng.AssertJUnit.assertTrue;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.apache.abdera.model.Element;
import org.apache.abdera.model.ExtensibleElement;

import com.ibm.lconn.automation.framework.search.rest.api.RestAPIUser;
import com.ibm.lconn.automation.framework.search.rest.api.RestAPIUser.UserType;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils.Purpose;
import com.ibm.lconn.automation.framework.search.rest.api.population.PopulatedData;
import com.ibm.lconn.automation.framework.search.rest.api.population.Populator;
import com.ibm.lconn.automation.framework.services.common.LCServiceException;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Notification;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Permissions;
import com.ibm.lconn.automation.framework.services.common.StringConstants.SharePermission;
import com.ibm.lconn.automation.framework.services.files.FilesService;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;
import com.ibm.lconn.automation.framework.services.search.data.Application;

public class FileCreator {
	private final static Logger LOGGER = Populator.LOGGER_POPUILATOR;

	private FilesService fileService;

	public FileCreator() throws Exception {
		RestAPIUser restAPIUser = new RestAPIUser(UserType.LOGIN);
		ServiceEntry filesServiceEntry = restAPIUser.getService("files");
		try {
			fileService = new FilesService(restAPIUser.getAbderaClient(), filesServiceEntry);
		} catch (LCServiceException e) {

			LOGGER.log(Level.WARNING, "The file is not created" + " LCServiceException: " + e.toString());
			assertTrue("Files service problem, file is not created", false);
		}

	}

	public FileCreator(UserType userType, int userIndex) throws Exception {
		RestAPIUser restAPIUser = new RestAPIUser(userType, userIndex);
		ServiceEntry filesServiceEntry = restAPIUser.getService("files");
		try {
			fileService = new FilesService(restAPIUser.getAbderaClient(), filesServiceEntry);
		} catch (LCServiceException e) {

			LOGGER.log(Level.WARNING, "The file is not created" + " LCServiceException: " + e.toString());
			assertTrue("Files service problem, file is not created", false);
		}

	}

	public void createFile(Permissions permission) throws IOException {
		createFile(permission, Purpose.SEARCH);
	}

	public void createFile(Permissions permission, String idOfUserToShareWith) throws IOException {
		createFile(permission, Purpose.SEARCH, idOfUserToShareWith);
	}

	public void createFile(Permissions permission, Purpose purpose) throws IOException {
		createFile(permission, purpose, "510b99c0-0101-102e-893e-f78755f7e0ed 510b99c0-0101-102e-893f-f78755f7e0ed");
	}

	private void createFile(Permissions permission, Purpose purpose, String idOfUserToShareWith) throws IOException {
		String title = SearchRestAPIUtils.generateTitle(permission, Application.file, purpose);
		String tag = SearchRestAPIUtils.generateTagValue(purpose); // tag
		// is
		// inverted
		// execId
		String description = SearchRestAPIUtils.generateDescription(title);// Content
		// is
		// inverted
		// title

		File tempFile = createTempFile(SearchRestAPIUtils.getExecId(purpose), title, purpose);
		FileEntry newFileEntry = new FileEntry(tempFile, title, description, tag, permission, true, Notification.ON,
				Notification.ON, null, null, true, true, SharePermission.EDIT, "New share for REST API test",
				idOfUserToShareWith);

		newFileEntry.setComment(SearchRestAPIUtils.contentForSearchExtracted(purpose));

		LOGGER.fine("Create file entry: " + newFileEntry.toString());
		if (fileService != null) {
			ExtensibleElement response = fileService.postFileToMyUserLibrary(newFileEntry);
			Element codeElement = response.getExtension(new QName("api", "code"));
			if (codeElement != null) {
				LOGGER.log(Level.WARNING, "The file is not created");
				assertTrue("Files service problem, file is not created", false);
			} else {
				PopulatedData.getInstance().setPopulatedLcEntry(newFileEntry, permission, purpose);

				LOGGER.fine("File created: " + response.toString());
			}
		} else {
			LOGGER.log(Level.WARNING, "The Files service is NULL.");
			assertTrue("Files service problem, file is not created", false);
		}
	}

	private File createTempFile(String execId, String title, Purpose purpose) throws IOException {
		File tempFile = File.createTempFile(execId + "_", "restApi.txt");
		tempFile.deleteOnExit();
		BufferedWriter out = new BufferedWriter(new FileWriter(tempFile));
		out.write(SearchRestAPIUtils.contentForSearchExtracted(purpose) + "\n");
		out.flush();
		out.close();
		return tempFile;
	}
}
