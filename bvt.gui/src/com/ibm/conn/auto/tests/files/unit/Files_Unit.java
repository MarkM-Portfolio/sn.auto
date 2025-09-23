package com.ibm.conn.auto.tests.files.unit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;

public class Files_Unit extends FilesUnitBaseSetUp{
	
	private static Logger log = LoggerFactory.getLogger(Files_Unit.class);
	
	@BeforeClass(alwaysRun=true)
	public void SetUpClass(){
		personalFilesSetUpClass();
	}
	
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {
		personalFilesSetUp();
	}	

	/**
	*<ul>SAMPLE TEST TO DEMONSTRATE SUCCESS 
	*<li><B>Info:</B> Perform actions on an uploaded file</li>
	*<li><B>Step:</B> Upload a file</li>
	*<li><B>Step:</B> Perform the following actions on the file: Pin/Comment/Recommend/Add Folder</li>
	*<li><B>Verify:</B> File is upload and all actions are performed</li>
	*</ul>
	*/
	
	@Test(groups = {"unit"})
	public void testUploadPrivateFile() throws Exception {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		BaseFile file = new BaseFile.Builder(Data.getData().file1)
									.extension(".jpg")
									.rename(Helper.genDateBasedRand())
									.build();
		
		ui.startTest();
		
		//upload file
		logger.strongStep("Upload the file");
		log.info("INFO: Upload the file");
		if(!cfg.getSecurityType().equalsIgnoreCase("false"))
			file.upload(ui,gkc);
		else
			file.upload(ui);

		
		//Validate message displays stating file was successfully uploaded
		logger.weakStep("Verify that the message displays stating the file was successfully uploaded");
		log.info("INFO: Verify message displays stating file was successfully uploaded");
		Assert.assertTrue(ui.fluentWaitTextPresent("Successfully uploaded " + file.getName()),
				  "ERROR: File was not uploaded");
		
		ui.endTest();
	}
	

}
