package com.hcl.lconn.automation.framework.tests;

import static org.hamcrest.Matchers.equalTo;

import java.io.StringReader;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.hcl.lconn.automation.framework.config.SetupMethodsAPI;
import com.hcl.lconn.automation.framework.payload.CommunityTemplateResponse;
import com.hcl.lconn.automation.framework.payload.ForumResponse;
import com.hcl.lconn.automation.framework.services.CommunityTemplateService;
import com.hcl.lconn.automation.framework.services.ForumService;
import com.hcl.lconn.automation.framework.services.MiddlewareJsonapiService;
import com.hcl.lconn.automation.framework.services.PDFExportService;
import com.hcl.lconn.automation.framework.services.ProfileService;
import com.hcl.lconn.automation.framework.utils.TestAPIConfigCustom;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.atmn.waffle.utils.Assert;

import io.restassured.parsing.Parser;
import io.restassured.path.json.JsonPath;
import io.restassured.path.xml.XmlPath;
import io.restassured.response.Response;


/**
 * Note: this class is just a sample of how to create TestNG tests to call API
 * When writing official tests, please do not hard code credentials / metadata in your tests.
 */
public class SampleTest extends SetupMethodsAPI {
	
	private TestAPIConfigCustom cfg;
	private static Logger log = LoggerFactory.getLogger(SampleTest.class);
	private Assert cnxAssert;
	private User testUser;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		cfg = TestAPIConfigCustom.getInstance();
		testUser = cfg.getUserAllocator().getUser();

	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUpMethod() {
		cnxAssert = new Assert(log);
	}
	
	
	@Test(groups = {"level2"})
	public void testCreateCommunityFromTemplate() {
		startTest();
		
		CommunityTemplateService commTemplateService = new CommunityTemplateService(cfg.getServerURL(), "jjones1", "passwordforldap");
		Response resp = commTemplateService.createCommunityFromTemplate("1624991540355_c56fdc94-6763-4299-a6bd-af7f233638d5", 
				"autohandle", "from template API " + new Date().toString());
		log.info(resp.asPrettyString());
		commTemplateService.assertStatusCode(resp, HttpStatus.SC_OK, "Create community from template status code");
		
		endTest();
	}
	
	@Test(groups = {"level2"})
	public void testDeleteCommunityTemplate() {
		startTest();
		
		CommunityTemplateService commTemplateService = new CommunityTemplateService(cfg.getServerURL(), "jjones1", "passwordforldap");
		Response resp = commTemplateService.deleteCommunityTemplate("1625604014448_8a50d355-6692-43f6-9084-6deacfa06cd5");
		commTemplateService.assertStatusCode(resp, HttpStatus.SC_NO_CONTENT, "Delete community template status code");
		
		endTest();
	}


	@Test(groups = {"level2"})
	public void testGetCommunityTemplate() {
		startTest();
		
		CommunityTemplateService commTemplateService = new CommunityTemplateService(cfg.getServerURL(), "jjones1", "passwordforldap");
		Response resp = commTemplateService.getCommunityTemplate("1624991540355_c56fdc94-6763-4299-a6bd-af7f233638d5");
		
		commTemplateService.assertStatusCode(resp, HttpStatus.SC_OK, "Get community template status code");
		
		try {
			// Note: doing this will only record to logger if assert fails
			resp.then().assertThat().body("communityUuid", equalTo("c56fdc94-6763-4299-a6bd-af7f233638d5aaaaaa"));
		} catch (AssertionError ae)  {
			log.error(ae.getMessage());
		}
		
		// Example of deserializing response to pojo
		CommunityTemplateResponse comTemResponse=resp.as(CommunityTemplateResponse.class);
		
		// Note: doing this will record to logger even when assert passes hence provides evidence of assertion points
		cnxAssert.assertEquals(comTemResponse.getCommunityUuid(),"c56fdc94-6763-4299-a6bd-af7f233638d5", "Community UUID is correct");
		cnxAssert.assertEquals(comTemResponse.getName(),"test1", "Community Name is test1");

		endTest();
	}
	
	
	@Test(groups = {"level2"})
	public void testGetAllCommunityTemplate() {
		startTest();
		
		CommunityTemplateService commTemplateService = new CommunityTemplateService(cfg.getServerURL(), "jjones1", "passwordforldap");
		Response resp = commTemplateService.getCommunityTemplate();
		commTemplateService.assertStatusCode(resp, HttpStatus.SC_OK, "Get community template status code");
		
		CommunityTemplateResponse[] comTemResponse =resp.as(CommunityTemplateResponse[].class);
		
		for(CommunityTemplateResponse ctr:comTemResponse )
		{
			log.info("cTRes is "+ ctr);
			if(ctr.getTemplateId().equals("1624991540355_c56fdc94-6763-4299-a6bd-af7f233638d5"))
			{
				cnxAssert.assertEquals(ctr.getCommunityUuid(), "c56fdc94-6763-4299-a6bd-af7f233638d5", "Community UUID is correct.");
				cnxAssert.assertEquals(ctr.getName(),"test1" , "Community name is test1");
			}
			if(ctr.getTemplateId().equals("1625056714488_64241d94-11a5-4466-a5a2-210b79b3dd33"))
			{
				cnxAssert.assertEquals(ctr.getCommunityUuid(), "64241d94-11a5-4466-a5a2-210b79b3dd33", "Community UUID is correct.");
				cnxAssert.assertEquals(ctr.getName(),"test2", "Community name is test2");
			}
		}
		
		endTest();
	}
	
	
	@Test(groups = {"level2"})
	public void verifyManagePDFExportAcces() {
		startTest();
		
		PDFExportService pdfExportService = new PDFExportService(cfg.getServerURL(), "jjones1", "passwordforldap");
		Response resp = pdfExportService.managePDFExportAccess(true, "5a76928e-2d20-4458-a4ce-ae1acdded7e0");
		
		try {
			// Note: doing this will only record to logger if assert fails
			resp.then().assertThat().statusCode(HttpStatus.SC_ACCEPTED);
		} catch (AssertionError ae)  {
			log.error(ae.getMessage());
		}
		
		endTest();
	}
	
	
	// titanproxy test
	@Test(groups = {"level2"}) 
	public void getProfile() {                                                                          
		startTest();            
		
		MiddlewareJsonapiService middlewareJsonapiService = new MiddlewareJsonapiService(cfg.getServerURL(), "jjones1", "password");     
		Response resp = middlewareJsonapiService.getProfile("55d5747c-0f1c-103b-89fb-950feda5c71f");  
		
		middlewareJsonapiService.assertStatusCode(resp, HttpStatus.SC_OK, "Jsonapi Get profile status code");                                                                                                           
		                                                                                                                
		endTest();                                                                                                      
	}                      
	
	// titanproxy test
	@Test(groups = {"level2"})
	public void inviteProfile() {                                                                          
		startTest();       
		
		// Note: this API returns 400 when running the 2nd time for the same user
		MiddlewareJsonapiService middlewareJsonapiService = new MiddlewareJsonapiService(cfg.getServerURL(), "jjones1", "password");     
		Response resp = middlewareJsonapiService.inviteProfile("jjones6@connections.example.com");           
		middlewareJsonapiService.assertStatusCode(resp, HttpStatus.SC_CREATED, "Jsonapi invite profile status code");                                                                                                
		                                                                                                                
		endTest();                                                                                                      
	}      
	
	
	@Test(groups = {"level2"})
	public void verifyGetForums() throws JAXBException {
		startTest();
		
		ForumService forumService= new ForumService(cfg.getServerURL(),"jjones1", "passwordforldap");
		Response respGetForums = forumService.getForums();
		log.info("respGetForums is "+ respGetForums.asPrettyString());
		forumService.assertStatusCode(respGetForums, HttpStatus.SC_OK, "Get Forums status code");   
		
		cnxAssert.assertEquals(forumService, respGetForums, bsKey);
		respGetForums.then().body("entry[0].author.email",equalTo("jjones1@cnx.pnp-hcl.com"))
		.defaultParser(Parser.XML);
		
		XmlPath xmlp = new XmlPath(respGetForums.asString());
		cnxAssert.assertTrue(xmlp.get("feed.entry.size()").equals(8), "Feed size is 8");
		cnxAssert.assertTrue(xmlp.get("feed.updated").equals("2021-07-01T09:39:26.488Z"), "Feed updated field is correct.");

        StringReader reader = new StringReader(respGetForums.asString());
        JAXBContext jaxbContext = JAXBContext.newInstance(ForumResponse.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();  
        ForumResponse fr= (ForumResponse) jaxbUnmarshaller.unmarshal(reader);  
        log.info("jaxb updated value is "+fr.getUpdated());
        
        endTest();  
	}
	
	@Test(groups = {"level2"}) 
	public void getProfileUUID() {                                                                          
		startTest();            
		
		ProfileService profileService = new ProfileService(cfg.getServerURL(), "jjones1", "password");     
		Response resp = profileService.getProfileDetails(testUser.getEmail());  
		
		profileService.assertStatusCode(resp, HttpStatus.SC_OK, "Jsonapi Get profile UUID status code");                                                                                                           

		JsonPath jsonpath = new JsonPath(resp.asString());
		
		String profileUUID = jsonpath.getString("X_lconn_userid");
		
		log.info("profileUUID is " + profileUUID);

		endTest();                                                                                                      
	}     
	
}
