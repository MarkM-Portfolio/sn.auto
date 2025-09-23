package com.ibm.lconn.automation.framework.services.switchbox;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import java.util.Iterator;
import java.util.Set;

import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.OrderedJSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Component;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.common.UserPerspective;
import com.ibm.lconn.automation.framework.services.common.UsersEnvironment;
import com.ibm.lconn.automation.framework.services.ublogs.nodes.JsonEntries;

/**
 * SwitchBox API
 * 
 * @author Ping Wang   wangpin@us.ibm.com
 */
public class SwitchBoxAPI {

	static UserPerspective user;
	private static SwitchBoxService service;

	protected final static Logger LOGGER = LoggerFactory
			.getLogger(SwitchBoxAPI.class.getName());
	
	@BeforeClass
	public static void setUp() throws Exception {

		LOGGER.debug("Start Initializing Switchbox Test");

		UsersEnvironment userEnv = new UsersEnvironment();
		user = userEnv.getLoginUserEnvironment(StringConstants.ADMIN_USER,
				Component.PROFILES.toString());
		service = user.getSwitchBoxService();
		
		// Currently only support following SC server
		String configUrl = "http://devops.swg.usma.ibm.com/environmentstatus.php?environment="; //Daily
		
		StringConstants.MQ_SERVER = URLConstants.SERVER_URL.substring(8) ;
		String jsonResult=null;
		
		if (URLConstants.SERVER_URL.contains("lcscdev")){
			if (StringConstants.ORGID == null){
				assertTrue("lcscdev is not ready for SwitchboxAPI test", false);
			}
			URLConstants.DMGR_URL = "http://acdmgra.lcscdev.swg.usma.ibm.com:9084";
		} else if (URLConstants.SERVER_URL.contains("acdev1")){
			if (StringConstants.ORGID == null){
				assertTrue("acdev1 is not ready for SwitchboxAPI test", false);
			}
			URLConstants.DMGR_URL = "http://acdmgr.acdev1.swg.usma.ibm.com:9084";
		} else if (URLConstants.SERVER_URL.contains("acdev")){
			if (StringConstants.ORGID == null){
				assertTrue("acdev is not ready for SwitchboxAPI test", false);
			}
			URLConstants.DMGR_URL = "http://acdmgr.acdev.swg.usma.ibm.com:9084";
		} else if (URLConstants.SERVER_URL.contains("collabservdaily")){
			if (StringConstants.ORGID == null){
				assertTrue("collabservdaily is not ready for SwitchboxAPI test", false);
			}
			jsonResult = service.getResponseString(configUrl+"Daily");					
			URLConstants.DMGR_URL = "http://acdmgr"+getSideValue(jsonResult).toLowerCase()+".lotuslivedaily.swg.usma.ibm.com:9084";
			//StringConstants.ORGID="20273597";
		} else if (URLConstants.SERVER_URL.contains("collabservsvt1")){
			if (StringConstants.ORGID == null){
				assertTrue("collabservsvt1 is not ready for SwitchboxAPI test", false);
			}
			jsonResult = service.getResponseString(configUrl+"LLC1");					
			URLConstants.DMGR_URL = "http://acdmgr"+getSideValue(jsonResult).toLowerCase()+".lotuslivenotes.swg.usma.ibm.com:9084";
			//StringConstants.ORGID="41502806";
		} else if (URLConstants.SERVER_URL.contains("collabservsvt2")){
			if (StringConstants.ORGID == null){
				assertTrue("collabservsvt2 is not ready for SwitchboxAPI test", false);
			}
			jsonResult = service.getResponseString(configUrl+"BHT6");					
			URLConstants.DMGR_URL = "http://acdmgr"+getSideValue(jsonResult)+".bht6.swg.usma.ibm.com:9084";	
			//StringConstants.ORGID="20681620";
		} else {
			assertTrue(StringConstants.MQ_SERVER +" SwitchboxAPI is not available", false);
		}
		

		LOGGER.debug("Finished Initializing Switchbox Test");
	}

	private static String getSideValue( String jsonResult) {
		String side ="";
		try {

			OrderedJSONObject obj0 = new OrderedJSONObject(jsonResult);
			@SuppressWarnings("unchecked")
			Set<String> set0 = obj0.keySet();
			Iterator<String> it0 = set0.iterator();
			while (it0.hasNext()) {
				String key0 = it0.next().toString();
				/*if (key0.contains("active")) {   // for single key:value
					String value = obj0.getString(key0);
				}*/
				if (key0.contains("active")) {		// for multiple key:{key:value,key:value,..}
					OrderedJSONObject obj1 = (OrderedJSONObject) obj0.get("active");
					@SuppressWarnings("unchecked")
					Set<String> set1 = obj1.keySet();
					Iterator<String> it1 = set1.iterator();
					
					while (it1.hasNext()) {
						String key1 = it1.next().toString();
						if (key1.contains("side")) {
							side = obj1.getString(key1);
						}
					}
				}
				/*if (key0.contains("active")) {   // for JSONArray key::[{key:value,..}{...}...]
					JSONArray jsonEntryArray = obj0.getJSONArray(key0);
					@SuppressWarnings("unchecked")
					Iterator <OrderedJSONObject> it1 = jsonEntryArray.iterator();			
					while (it1.hasNext()) { // for each View
						OrderedJSONObject obj1 = (OrderedJSONObject) it1.next();
				}*/
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			// assertTrue(false);
		}
		return side;
	}
	
	@Test
	public void switchboxBVTTest (){
		service.switchboxBVT();
		if (service.getRespStatus() != 200){
			URLConstants.DMGR_URL = URLConstants.DMGR_URL.replace("9084", "9092");
			service.switchboxBVT();
		}
		
		assertEquals(" SwitchBox BVT"+service.getDetail(), 200, service.getRespStatus());
	}
	
	/*@Test
	public void switchboxUnitTest (){
		service.switchboxUnit();
		assertEquals(" SwitchBox Unit", 200, service.getRespStatus());
	}*/
	
	@AfterClass
	public static void tearDown() {
		service.tearDown();
	}

}
