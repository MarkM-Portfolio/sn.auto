package com.ibm.conn.auto.util.browserstack;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Properties;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.remote.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestResult;

import com.ibm.atmn.waffle.base.BaseSetup;
import com.ibm.atmn.waffle.utils.Utils;
import com.ibm.conn.auto.util.Helper;

public class GetBrowserStackArtifacts{
	private static Logger log = LoggerFactory.getLogger(GetBrowserStackArtifacts.class);
	HttpResponse response;
	HttpClient httpClient;
	Properties prop;
	String buildHashedId;
	static String sessionHashedId;
	
	/**
	 * Constructor to load all properties from config file
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public GetBrowserStackArtifacts(ITestContext context) throws ClientProtocolException, IOException{
        File ofile = new File(context.getOutputDirectory()+"/..");
        BaseSetup.browserStackProps.setProperty("downloadPath",ofile.getCanonicalPath());
       
        BaseSetup.browserStackProps.setProperty("URL",BaseSetup.browserStackProps.getProperty("URL").replaceAll("%s_bsusername", BaseSetup.browserStackProps.getProperty("browserStackUser")).replaceAll("%s_bsaccesskey", BaseSetup.browserStackProps.getProperty("browserStackKey")));
        prop = BaseSetup.browserStackProps;
       
        if(BaseSetup.browserStackProps.getProperty("buildid").equals(""))
        {
            buildHashedId = bsGetBuildId();
            BaseSetup.browserStackProps.setProperty("buildid", buildHashedId);
        }
        sessionHashedId=bsGetSessionIdUsingBuildId(BaseSetup.browserStackProps.getProperty("buildid"));
    }
	
	public GetBrowserStackArtifacts() throws ClientProtocolException, IOException{
		prop = BaseSetup.browserStackProps;
	}
	
	/**
	 * This function fetches video URL from browserstack
	 * input parameter is session id
	 */
	//get video url
	public void bsDownloadExecutionVideo(ITestResult tr) throws Exception{	
		//proceed only if session id is not NULL
		if(sessionHashedId != null && !sessionHashedId.isEmpty()){
			//form URI
			String url = prop.getProperty("URL") + prop.getProperty("serviceURLVideo").replace("%s_build", prop.getProperty("buildid")).replace("%s_session", sessionHashedId);		
			
			String className = tr.getTestClass().getName();
			className = className.substring(className.lastIndexOf(".")+1, className.length());
			String methodName = tr.getMethod().getMethodName();
			
			//get JSON array from URI
			JSONObject responseJson=null;
			
			//get Current status of Execution from JSON array, Added Fluent wait of max 90sec with polling time of 2sec till status changed from RUNNING.
			String status = "";
			int counter=0;
			do
			{
				Thread.sleep(2000);
				responseJson=bsGetResponseJSON(url);
				status = getValueByJPath(responseJson, "/data[0]/automation_session/status");
				counter++;
			}while((status.equalsIgnoreCase("running")) && (counter<50));
			
			if(status.equalsIgnoreCase("running"))
			{
				log.error("Status in Browser Stack is still RUNNING even after wait of 90 Sec.");
			}
			
			// Check Video Folder availability if not present create the Folder.
			File directory = new File(prop.getProperty("downloadPath") + File.separator + "videos");
			if(!directory.exists())
			{
				directory.mkdir();
			}
			Thread.sleep(10000);	
			//get video from JSON array
			String videoUrl = getValueByJPath(responseJson, "/data[0]/automation_session/video_url");
			log.info("Video URL : " + videoUrl+'\n');
			if (!(downloadFilesFromURL(videoUrl, prop.getProperty("downloadPath")+File.separator+"videos" + 
					File.separator + prop.getProperty("buildName") + className + "_" + methodName + ".mp4"))){
				log.error("Issue during Downloading Video from Browser Stack");
			}
		}
		else{
			log.error("Session Id is Blank");
		}
	}
	
	/**
	 * This function updates status as passed/failed depending on test execution in browserstack
	 * input parameter is session id
	 */
	public void bsUpdateTestCaseStatusInBrowserStack(String status) throws ClientProtocolException, IOException {
		if(sessionHashedId != null && !sessionHashedId.isEmpty()){
			//form URI
			String url = prop.getProperty("URL") + prop.getProperty("serviceURLUpdate").replace("%s_session", sessionHashedId);		
			//get JSON array from URI
			if(Helper.putRequestString_BrowserStack(url,prop.getProperty("browserStackUser"),prop.getProperty("browserStackKey"), status)){
				log.info("Test case status updated to " + status);
			}
			else{
				log.error("Unable to update status in BrowserStack");
			}
			
		}
	}

	/**
	 * This function will find build id and from given build name
	 * REST response returns a JSON array containing all build currently running at browserstack end
	 * while loop matches build names with current build
	 */
	public String bsGetBuildId() throws ClientProtocolException, IOException{
		int sessionIterator=0;
		String buildHashedId=null;
		//get JSON array from URI
		JSONObject responseJson=bsGetResponseJSON(prop.getProperty("URL") + prop.getProperty("serviceURLBuild"));
		//find number of elements present in the json array
		int jsonLength=0;
		try {
			jsonLength = ((JSONArray) (responseJson).get("data")).length();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error(e.toString());
		}
		
		//loop through array and find array element with desired build name
		while(jsonLength>sessionIterator){
		//get name from JSON array:
			String buildName = getValueByJPath(responseJson, "/data["+sessionIterator+"]/automation_build/name");
			if(buildName.equalsIgnoreCase(prop.getProperty("buildName"))){
				buildHashedId = getValueByJPath(responseJson, "/data["+sessionIterator+"]/automation_build/hashed_id");
				break;
			}
			sessionIterator=sessionIterator+1;
		}
		//fail test case if desired build name is not found in the json array
		if(jsonLength==sessionIterator){
			log.error("Build number is not found in browser stack");
		}
		log.info("build id : " + buildHashedId+'\n');
		return buildHashedId;
	}
	
	/**
	 * This function will find session id and from given build id
	 * @throws IOException 
	 */
	public String bsGetSessionIdUsingBuildId(String buildHashedId) throws IOException{//proceed only if build id is not NULL
		String sessionHashedId=null;
		int sessionIterator=0;
		if(buildHashedId != null && !buildHashedId.isEmpty()){
			//form URI
			String url = prop.getProperty("URL") + prop.getProperty("serviceURLSession").replace("%s_build", buildHashedId);		
			//get JSON array from URI
			JSONObject responseJson=bsGetResponseJSON(url);
			
			int jsonLength=0;
			try {
				jsonLength = ((JSONArray) (responseJson).get("data")).length();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			//loop through array and find array element with desired session name
			while(jsonLength>sessionIterator){
			//get name from JSON array:
				String sessionName = getValueByJPath(responseJson, "/data["+sessionIterator+"]/automation_session/name");
				if(sessionName.equalsIgnoreCase(Utils.getThreadLocalUniqueTestName())){
					sessionHashedId = getValueByJPath(responseJson, "/data["+sessionIterator+"]/automation_session/hashed_id");
					log.info("Session id : " + sessionHashedId);
					break;
				}
				sessionIterator=sessionIterator+1;
			}
			//fail test case if desired build name is not found in the json array
			if(jsonLength==sessionIterator){
				log.error("Testcase session is not found in BrowserStack");
			}
		}
		else{
			log.error("Build Id is Blank");
		}
		return sessionHashedId;
	}
	
	/**
	 * This function takes URI as input and returns API GET response in JSONObject format
	 * input parameter is URI
	 */
	public JSONObject bsGetResponseJSON(String url) throws IOException{
		String val = Helper.getRequestString_BrowserStack(url,prop.getProperty("browserStackUser"),prop.getProperty("browserStackKey"));
		InputStream inputStream = new ByteArrayInputStream(val.getBytes(Charset.forName("UTF-8")));
		BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
		
		String output;
		String responseString="";
		while ((output = br.readLine()) != null) {
			responseString=output;
		}
		
		if(!(responseString.charAt(0)=='[')){
			responseString="[" + responseString;
			responseString=responseString+"]";
		}
		if(responseString.charAt(0)=='['){
		String responseString1=responseString.replace("[", "{\"data\":[");
		responseString=responseString1.replace("]", "]}");
		}
		
		JSONObject responseJson=null;
		try {
			responseJson = new JSONObject(responseString);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error(e.toString());
		}
		return responseJson;
		}
			
		/**
		 * This function extracts JSON nodes depending on input jpath
		 * input parameter is response json
		 * japth is a path of JSON by which a specific JSON node value will be returned
		 * returns value of a given JSON node
		 */	
		public static String getValueByJPath(JSONObject responsejson, String jpath){
			Object obj = responsejson;
			for(String s : jpath.split("/")) 
				if(!s.isEmpty()) 
					try {
					if(!(s.contains("[") || s.contains("]")))
						
							obj = ((JSONObject) obj).get(s);
						
					else if(s.contains("[") || s.contains("]"))
						obj = ((JSONArray) ((JSONObject) obj).get(s.split("\\[")[0])).get(Integer.parseInt(s.split("\\[")[1].replace("]", "")));
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							log.error(e.toString());
						}
			return obj.toString();
		}
		
		/**
		 * This function downloads file from a given URL
		 * input parameters are - Link address from where user wants to download file
		 * and output address where user wants to store video
		 * returns boolean value- true for success
		 */
		public static boolean downloadFilesFromURL(String inFileLink, String outFileLink) throws Exception {
			boolean success=false;
			try{
				URL url=new URL(inFileLink);
				HttpURLConnection http=(HttpURLConnection)url.openConnection();
				double fileSize=(double)http.getContentLengthLong();
				BufferedInputStream in=new BufferedInputStream(http.getInputStream());
				FileOutputStream fos=new FileOutputStream(outFileLink);
				BufferedOutputStream out=new BufferedOutputStream(fos,1024);
				byte[] buffer=new byte[1024];
				double download=0.00;
				int read=0;
				double percentDownload=0.00;
				while((read=in.read(buffer,0,1024))>=0){
					out.write(buffer,0,read);
					download+=read;
					percentDownload=(download*100)/fileSize;			
				}
				out.close();
				in.close();
				if((int)percentDownload==100){
					log.info("File Downloaded - " + outFileLink);
					success=true;
				}
			}catch(IOException ex){
				ex.printStackTrace();
				log.error(ex.toString());
				
			}
			return success;
		}
		
		/**
		 * This function fetches execution logs from browserstack
		 * It also fetches all screenshots taken in the current session
		 * input parameter is session id
		 */
		//get execution logs
		public void bsDownloadBrowserStackRawLogs(ITestResult tr) throws Exception{			
			//proceed only if session id is not NULL
			if(sessionHashedId != null && !sessionHashedId.isEmpty()){
				File directory = new File(prop.getProperty("downloadPath") + File.separator + "BS_Logs");
				String className = tr.getTestClass().getName();
				className = className.substring(className.lastIndexOf(".")+1, className.length());
				String methodName = tr.getMethod().getMethodName();
				if(!directory.exists())
				{
					directory.mkdir();
				}
				//form URI
				String url = prop.getProperty("URL") + prop.getProperty("serviceURLLogs").replace("%s_build", prop.getProperty("buildid")).replace("%s_session", sessionHashedId);		
				//get JSON array from URI
				String logs=Helper.getRequestString_BrowserStack(url, prop.getProperty("browserStackUser"),prop.getProperty("browserStackKey"));
								
				String bsLogPath = prop.getProperty("downloadPath") + File.separator + "BS_Logs" + 
						File.separator + prop.getProperty("buildName") + className + "_" + methodName + ".txt";
				try (PrintWriter out = new PrintWriter(bsLogPath)) {
				    out.println(logs);
				}
				log.info("Downloaded Browser Stack Logs at Location - " + bsLogPath);
			}
			else{
				log.error("Session is Blank");
			}
		}
		
		/**
		 * This function deletes all artifacts stored browserstack for current session
		 * input parameter is build id
		 */
		public void bsDeleteArtifactsFromBrowserStack() throws ClientProtocolException, IOException{
			if(prop.getProperty("deleteBSArtifacts").equalsIgnoreCase("true")){
				log.info("Delete test run from BrowserStack...");
				if(prop.getProperty("buildid") != null && !prop.getProperty("buildid").isEmpty()){
					//form URI
					String url = prop.getProperty("URL") + prop.getProperty("serviceURLDelete").replace("%s_build", prop.getProperty("buildid"));		
					String response = Helper.deleteRequestString_BrowserStack(url, prop.getProperty("browserStackUser"), prop.getProperty("browserStackKey"));
					if(!response.split(",")[0].split(":")[1].equalsIgnoreCase("\"ok\""))
					{
						log.error("Failed to Delete Artifacts from Browser Stack - " + response);
					}
					else
					{
						log.info(response);
					}	
				}
				else{
					log.error("Session is Blank");
				}
			}
		}
}
