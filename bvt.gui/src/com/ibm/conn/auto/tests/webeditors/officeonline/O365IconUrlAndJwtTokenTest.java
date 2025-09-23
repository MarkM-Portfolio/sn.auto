package com.ibm.conn.auto.tests.webeditors.officeonline;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.ibm.conn.auto.tests.webeditors.O365BaseTest;
import com.ibm.conn.auto.webui.FilesUI.FilesListView;
import com.ibm.conn.auto.webui.OfficeOnlineUI.FileSet;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;

public final class O365IconUrlAndJwtTokenTest extends O365BaseTest {

	private static final Map<String, String> iconNameForFileType;
	private static final String patternString = "[0-9a-zA-Z\\+\\/]{36}\\.[0-9a-zA-Z\\+\\/]{514}==\\.[0-9a-zA-Z\\+\\/]{43}=";
	private static final Pattern pattern = Pattern.compile(patternString);

	static {
		Hashtable<String, String> iconNameForFileTypeAux = new Hashtable<String, String>(3);
		iconNameForFileTypeAux.put("xlsx", "FavIcon_Excel.ico");
		iconNameForFileTypeAux.put("docx", "FavIcon_Word.ico");
		iconNameForFileTypeAux.put("pptx", "FavIcon_Ppt.ico");
		iconNameForFileType = Collections.unmodifiableMap(iconNameForFileTypeAux);
	}
	
	@Override
	protected FileSet getFileSet() {
		return FileSet.BVT;
	}
	 
	@Test(groups = {"WE_BVT", "OO_BVT"}, invocationCount = 1)
	public void iconPathMatchesFileType() throws URISyntaxException {

		log.info("INFO: switching to FilesListView LIST mode");
		officeOnlineUI.clickLinkWait(FilesListView.LIST.getActivateSelector());
		officeOnlineUI.fluentWaitPresent(FilesListView.LIST.getIsActiveSelector());
		
		for(FileEntry file : officeOnlineUI.testFiles) {
			
			String filename = file.getTitle();
			log.info("INFO: testing file '" + filename + "'...");
			
			URI iconUrl = officeOnlineUI.getFileIconUrl( filename );

			log.info("INFO: navigating back to Connections Files UI");
		    driver.navigate().to(getIcComponentUrl());
		    
			log.info("INFO: testing is filename and icon are a match...");
			String fileExtension = FilenameUtils.getExtension( filename ).toLowerCase();
			Assert.assertEquals(iconNameForFileType.get(fileExtension), FilenameUtils.getName(iconUrl.getPath()), 
					"For file '" + filename + "', the received icon filename does match the expected value (for filetype '" + fileExtension + "'):"); 
		}
		
	}

	@Test(groups = {"WE_BVT", "OO_BVT"}, invocationCount = 1)
	public void verifyWellFormedJwtToken() throws URISyntaxException {
		log.info("INFO: switching to FilesListView LIST mode");
		officeOnlineUI.clickLinkWait(FilesListView.LIST.getActivateSelector());
		officeOnlineUI.fluentWaitPresent(FilesListView.LIST.getIsActiveSelector());
		
		FileEntry file = officeOnlineUI.testFiles.get(0); // i don't care which file it is, as long as I can edit it in Office Online
		
		String filename = file.getTitle();
		log.info("INFO: testing file '" + filename + "'...");
		
		String jwtToken = officeOnlineUI.getJwtToken( filename );
		
		log.info("INFO: navigating back to Connections Files UI");
	    driver.navigate().to(getIcComponentUrl());
	    
	    log.info("Serialized Encrypted JWT: " + jwtToken);
		Matcher matcher = pattern.matcher(jwtToken);
		Assert.assertTrue(matcher.matches(), "Serialized Encrypted JWE '"+jwtToken+"' does not match the expected format: '" + pattern.pattern() + "'.");
		log.info("Serialized Encrypted JWT matches '" + pattern.pattern() + "'.");
	}

}
