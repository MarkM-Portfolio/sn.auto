package com.ibm.lconn.automation.framework.services.gatekeeper;

import static org.testng.AssertJUnit.assertEquals;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Component;
import com.ibm.lconn.automation.framework.services.common.UserPerspective;
import com.ibm.lconn.automation.framework.services.common.UsersEnvironment;

/**
 * GateKeeper API
 * 
 * @author Ping Wang   wangpin@us.ibm.com
 */
public class GateKeeperAPI {

	static UserPerspective user;
	private static GateKeeperService service;

	protected final static Logger LOGGER = LoggerFactory
			.getLogger(GateKeeperAPI.class.getName());
	
	private final static String OnPremOrgId = "00000000-0000-0000-0000-000000000000";
	
	private String settingName1 = "NEWS_PUSH_NOTIFICATION_SERVICE";
	private String settingName2 = "CONNECTIONS_NOTIFICATION_CENTER";

	@BeforeClass
	public static void setUp() throws Exception {

		LOGGER.debug("Start Initializing GateKeeper Test");

		UsersEnvironment userEnv = new UsersEnvironment();
		user = userEnv.getLoginUserEnvironment(StringConstants.ADMIN_USER,
				Component.PROFILES.toString());
		service = user.getGateKeeperService();

		LOGGER.debug("Finished Initializing GateKeeper Test");
	}
	
	@Test
	public void getGatekeeperSettings (){
		String id = OnPremOrgId;
		service.getGateKeeperSetting(id, null);
		assertEquals(" get GateKeeper settings", 200, service.getRespStatus());
	}
	
	@Test
	public void getGatekeeperSetting (){
		String id = OnPremOrgId;
		String name = settingName1;
		service.getGateKeeperSetting(id, name);
		assertEquals(" get GateKeeper setting", 200, service.getRespStatus());
		
		service.getGateKeeperSetting(id, "WRONG_NAME");
		assertEquals(" get GateKeeper setting with wrong name", 500, service.getRespStatus());
	}
	
	@Test
	public void postGatekeeperVM(){
				
		String organization = "\"organization\":\""+ OnPremOrgId + "\",";
		String Begin = "{"+	organization + "\"settings\":[";
		String End = "]}";
		
		String name = "{\"name\":\"CONNECTIONS_VISITOR_MODEL\",";
		String value = "\"value\":\"true\",";
		String description = "\"description\":\"\",";
		String isDefault = "\"isDefault\":false}";
		String setting = name + value + description + isDefault;
		
		String name2 = "{\"name\":\"CONNECTIONS_VISITOR_MODEL_FOR_ORG\",";
		String value2 = "\"value\":\"true\",";
		String description2 = "\"description\":\"\",";
		String isDefault2 = "\"isDefault\":false}";
		String setting2 = name2 + value2 + description2 + isDefault2;
		
		String name1 = "{\"name\":\"CONNECTIONS_VISITOR_MODEL_FOR_ORG_TRANSITION\",";
		String setting1 = name1 + value2 + description2 + isDefault2;
		
		String name3 = "{\"name\":\"CONTACTS_VISIT_MODEL\",";
		String value3 = "\"value\":\"true\",";
		String description3 = "\"description\":\"\",";
		String isDefault3 = "\"isDefault\":false}";
		String setting3 = name3 + value3 + description3 + isDefault3;
			
		String name4 = "{\"name\":\"SEARCH_VISITOR_MODEL_CONTENT_FILTERING\",";
		String value4 = "\"value\":\"true\",";
		String description4 = "\"description\":\"\",";
		String isDefault4 = "\"isDefault\":false}";
		String setting4 = name4 + value4 + description4 + isDefault4;
		
		String postJSON = Begin + setting+","+ setting1 + ","+ setting2 + ","+ setting3 +","+ setting4 + End;
		
		service.postGateKeeperSetting(OnPremOrgId, postJSON);
		assertEquals(" Post GateKeeper settings", 200, service.getRespStatus());
		
	}

	@Test
	public void postGatekeeperSettings(){
				
		String organization = "\"organization\":\""+ OnPremOrgId + "\",";
		String Begin = "{"+	organization + "\"settings\":[";
		String End = "]}";
		
		String name = "{\"name\":\"NEWS_PUSH_NOTIFICATION_SERVICE\",";
		String value = "\"value\":\"true\",";
		String description = "\"description\":\"\",";
		String isDefault = "\"isDefault\":false}";
		String setting = name + value + description + isDefault;
		
		String name2 = "{\"name\":\"CONNECTIONS_NOTIFICATION_CENTER\",";
		String value2 = "\"value\":\"true\",";
		String description2 = "\"description\":\"\",";
		String isDefault2 = "\"isDefault\":false}";
		String setting2 = name2 + value2 + description2 + isDefault2;
		
		String postJSON = Begin + setting + End;
		String postJSON2 = Begin + setting +","+ setting2 + End;
		
		String name4 = "{\"name\":\"WRONG_NAME_NOTIFICATION_CENTER\",";
		//String value4 = "\"value\":\"true\",";
		//String description4 = "\"description\":\"\",";
		//String isDefault4 = "\"isDefault\":false}";
		String setting4 = name4 + value2 + description2 + isDefault2;
		String postJSON4 = Begin + setting+","+ setting4 + End;
		
		service.postGateKeeperSetting(OnPremOrgId, postJSON2);
		assertEquals(" Post GateKeeper settings", 200, service.getRespStatus());
		
		service.postGateKeeperSetting(OnPremOrgId, postJSON4);
		assertEquals(" Post non exist GateKeeper settings", 500, service.getRespStatus());
	}
	
	
	@Test
	public void postSHAREPOINTGatekeeperSettings(){
				
		String organization = "\"organization\":\""+ OnPremOrgId + "\",";
		String Begin = "{"+	organization + "\"settings\":[";
		String End = "]}";		
		
		String description = "{\"description\":\"Iwidget that contains sharepoint iframe\",";
		String isDefault = "\"isDefault\":\"true\",";
		String isGlobal = "\"isGlobal\":\"false\",";
		String javascriptName = "\"javascriptName isGlobal\":\"sharepoint_iframe_widget\",";
		String name = "\"name\":\"SHAREPOINT_FILES_WIDGET\",";
		String value = "\"value\":true}";
		String setting =  description + isDefault+isGlobal+javascriptName +name + value;

		String postJSON = Begin + setting + End;
				
		service.postGateKeeperSetting(OnPremOrgId, postJSON);
		assertEquals(" Post GateKeeper settings", 200, service.getRespStatus());
		
	}
	


	@AfterClass
	public static void tearDown() {
		service.tearDown();
	}

}
