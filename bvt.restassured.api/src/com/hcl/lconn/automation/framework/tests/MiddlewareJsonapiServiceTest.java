package com.hcl.lconn.automation.framework.tests;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.hcl.lconn.automation.framework.config.SetupMethodsAPI;
import com.hcl.lconn.automation.framework.services.ProfileService;
import com.hcl.lconn.automation.framework.payload.ProfileUpdateRequest;
import com.hcl.lconn.automation.framework.services.MiddlewareJsonapiService;
import com.hcl.lconn.automation.framework.utils.TestAPIConfigCustom;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.atmn.waffle.utils.Assert;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;


/**
 * This is a set of basic API Automation Tests for the middleware-jsonapi service 
 *  hitting some of the basic sample endpoints to test functionality
 */
public class MiddlewareJsonapiServiceTest extends SetupMethodsAPI {
	
	private TestAPIConfigCustom cfg;
	private ProfileService prof1;
	private static Logger log = LoggerFactory.getLogger(MiddlewareJsonapiServiceTest.class);
	private Assert cnxAssert;
	private User testUser1, testUser2;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		cfg = TestAPIConfigCustom.getInstance();
		testUser1 = cfg.getUserAllocator().getUser();
		testUser2 = cfg.getUserAllocator().getUser();
		prof1 = new ProfileService(cfg.getServerURL(), testUser1.getUid(), testUser1.getPassword()); 
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUpMethod() {
		cnxAssert = new Assert(log);
	}
	
	// titanproxy test
	@Test(groups = {"cplevel2", "mtlevel2"}) 
	public void getProfile() {                                                                          
		startTest();            
		
		MiddlewareJsonapiService middlewareJsonapiService = new MiddlewareJsonapiService(cfg.getServerURL(), testUser1.getUid(), testUser1.getPassword());     
		Response getResp = middlewareJsonapiService.getProfile(prof1.getProfileUUID(testUser1));  
		
		JsonPath jsonpath= new JsonPath(getResp.asString());
		log.info("getProfile response is "+ getResp.asPrettyString());
		
		middlewareJsonapiService.assertStatusCode(getResp, HttpStatus.SC_OK, "Jsonapi Get profile status code"); 
		cnxAssert.assertTrue(jsonpath.get("name").equals(testUser1.getDisplayName()),"Jsonapi verify profile info after getProfile response");
		                                                                                                                
		endTest();                                                                                                      
	}
	
	@Test(groups = {"cplevel2", "mtlevel2"}) 
	public void updateProfile() {                                                                          
		startTest();            
		
		MiddlewareJsonapiService middlewareJsonapiService = new MiddlewareJsonapiService(cfg.getServerURL(), testUser1.getUid(), testUser1.getPassword()); 
		ProfileUpdateRequest updatePayload = middlewareJsonapiService.createProfileUpdateRequestPayload("This is my test job title", "1231231234", "1231231234", "1231231234", "1231231234", "This is my test description", "10");

		Response updateResp = middlewareJsonapiService.updateProfile(prof1.getProfileUUID(testUser1), updatePayload);  
		
		middlewareJsonapiService.assertStatusCode(updateResp, HttpStatus.SC_OK, "Jsonapi Update profile status code");  
		
		Response getResp = middlewareJsonapiService.getProfile(prof1.getProfileUUID(testUser1));  
		
		middlewareJsonapiService.assertStatusCode(getResp, HttpStatus.SC_OK, "Jsonapi Get profile status code in update"); 
		
		JsonPath jsonpath= new JsonPath(getResp.asString());
		log.info("getProfile response is "+ getResp.asPrettyString());
		cnxAssert.assertTrue(jsonpath.get("title").equals("This is my test job title"),"Jsonapi verify a field for the profile was updated");
		                
		endTest();                                                                                                      
	} 
	
	@Test(groups = {"cplevel2", "mtlevel2"})
	public void inviteProfile() {                                                                          
		startTest();       
		
		// Note: this API returns 400 when running the 2nd time for the same user
		MiddlewareJsonapiService middlewareJsonapiService = new MiddlewareJsonapiService(cfg.getServerURL(), testUser1.getUid(), testUser1.getPassword());     
		Response resp = middlewareJsonapiService.inviteProfile(testUser2.getEmail());           
		middlewareJsonapiService.assertStatusCode(resp, HttpStatus.SC_CREATED, "Jsonapi invite profile status code");                                                                                                
		                                                                                                                
		endTest();                                                                                                      
	}
	
	@Test(groups = {"cplevel2", "mtlevel2"}) 
	public void getSearch() {                                                                          
		startTest();            
		
		MiddlewareJsonapiService middlewareJsonapiService = new MiddlewareJsonapiService(cfg.getServerURL(), testUser1.getUid(), testUser1.getPassword());     
		Response getResp = middlewareJsonapiService.getSearch(testUser1.getFirstName());  
		
		JsonPath jsonpath= new JsonPath(getResp.asString());
		//log.info("getSearch response is "+ getResp.asString());
		log.info("getSearch first entry title value is "+ jsonpath.get("entry[0].title"));
		middlewareJsonapiService.assertStatusCode(getResp, HttpStatus.SC_OK, "Jsonapi Get profile status code"); 
		cnxAssert.assertTrue(jsonpath.get("entry[0].title").toString() != null,"Jsonapi verify search returns results after getSearch response");
		                                                                                                                
		endTest();                                                                                                      
	}
}
