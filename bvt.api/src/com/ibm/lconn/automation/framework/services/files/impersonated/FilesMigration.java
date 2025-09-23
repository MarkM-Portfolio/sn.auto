package com.ibm.lconn.automation.framework.services.files.impersonated;

import static org.testng.AssertJUnit.assertEquals;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.lconn.automation.framework.services.common.LCServiceException;
import com.ibm.lconn.automation.framework.services.common.ProfileData;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Component;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Permissions;
import com.ibm.lconn.automation.framework.services.common.StringConstants.SharePermission;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.common.UserPerspective;
import com.ibm.lconn.automation.framework.services.files.FilesService;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;

//
/**
 * JUnit Tests via Connections API for Blogs Service
 * 
 * @author Ping Wang - wangpin@us.ibm.com
 */
public class FilesMigration {

	private static UserPerspective impersonateByAdmin;

	protected final static Logger LOGGER = LoggerFactory
			.getLogger(FilesMigration.class.getName());
	
	protected static Properties properties; 
	static FilesService service;
	
	//@Test
	public void testFileImpersonatedRestore() {
		/*
		 * Tests file migration
		 * Step 1: read prop to get the File info 
		 * Step 2: create the File 
		 * Step 3: share the File to other users
		 * Step 4: share the File to communities	
		 */
		properties = new Properties();
		String PROPERTIES_FILE_PATH = "/resources/filesrestore.properties";

		InputStream in = ProfileData.class.getResourceAsStream(PROPERTIES_FILE_PATH);
		
		try {
			if (in != null){
				properties.load(in);
			} else {    // read properties from outside jar, under ./resources
				properties.load(new FileInputStream("."+PROPERTIES_FILE_PATH));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String serverUrl=properties.getProperty("serverUrl");
		String adminEmail=properties.getProperty("AdminEmail");
		String pwd=properties.getProperty("AdminPassword");
		String fileinfo, userId, filePath, params, shareUsers;
		
		String filename, description, tag_string,permissions,sharePermission;
		
		Permissions _permissions=null;
		SharePermission _sharePermission=null;
		
		for ( int i=1; ;i++){
			fileinfo =properties.getProperty("file"+i);
			if (fileinfo==null) break;
			
			//get the fileinfo
			StringTokenizer st = new StringTokenizer(fileinfo, ";");
			
			userId = st.nextToken();
			filePath = st.nextToken();
			params = st.nextToken();
			shareUsers = st.nextToken();
			
			StringTokenizer param = new StringTokenizer(params, "&");
			filename=param.nextToken();
			description=param.nextToken();
			tag_string=param.nextToken();
			permissions=param.nextToken();
			sharePermission=param.nextToken();
			
			if (permissions.equalsIgnoreCase("public")){
				_permissions = Permissions.PUBLIC;
			}else if (permissions.equalsIgnoreCase("private")){
				_permissions = Permissions.PRIVATE;
			}else if (permissions.equalsIgnoreCase("share")){
				_permissions = Permissions.SHARED;
			}else if (permissions.equalsIgnoreCase("PUBLICINVITEONLY")){
				_permissions = Permissions.PUBLICINVITEONLY;
			}
			
			if (sharePermission.equalsIgnoreCase("view")){
				_sharePermission = SharePermission.VIEW;
			}else if (sharePermission.equalsIgnoreCase("edit")){
				_sharePermission = SharePermission.EDIT;
			}
			
			StringTokenizer user = new StringTokenizer(shareUsers, "&");
			int size = user.countTokens();
			String shareUser=user.nextToken();
			
			
			//Restore file
			URLConstants.SERVER_URL = serverUrl;
			try {
				impersonateByAdmin = new UserPerspective(adminEmail, pwd, Component.FILES.toString(), userId);
				service = impersonateByAdmin.getFilesService();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (LCServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			InputStream infile = this.getClass().getResourceAsStream(
					"/resources/lamborghini_murcielago_lp640.jpg");

			FileEntry fileMetaData = new FileEntry(null, infile,
					filename, description, tag_string, _permissions,
					true, null, null, null, null, true, true,
					_sharePermission, null,shareUser);
			ExtensibleElement eEle = service.createFile(fileMetaData);
			assertEquals("create File", 201,service.getRespStatus());
			String fileUUID = ((Entry) eEle).getExtension(StringConstants.TD_UUID)
					.getText();
	
			for (int j=1; j<size; j++){
				shareUser=user.nextToken();
				
				FileEntry fileShareEntry = new FileEntry(null, filename, description,
						tag_string, _permissions, true, null, null, null, null, true, true,
						_sharePermission, null, shareUser, fileUUID, null);
				service.createFileShare(fileShareEntry);
				assertEquals("share with other user", 201, service.getRespStatus());
			}
		}
				
	}
}