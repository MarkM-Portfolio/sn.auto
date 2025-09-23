package com.ibm.conn.auto.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.EntityEnclosingMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.ITestResult;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.ibm.atmn.waffle.core.Executor;
import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.atmn.waffle.core.TestConfiguration;
import com.ibm.atmn.waffle.utils.FileIOHandler;
import com.ibm.conn.auto.config.SetUpMethods2;

import net.jsourcerer.webdriver.jserrorcollector.JavaScriptError;

/**
 * Miscellaneous supporting static methods
 *
 * @author Ilya
 *
 */
public class Helper {

	private static Logger log = LoggerFactory.getLogger(Helper.class);

	public static String USERNAME_FIELD = "//input[@id='username'] | //input[@name='USER'] | //input[@id='j_username'] | //input[@name='username'] | //input[@name='j_username']";
	public static String Password_FIELD = "//input[@id='password'] | //input[@name='PASSWORD'] | //input[@id='j_password'] | //input[@name='password'] | //input[@name='j_password']";
	public static String Login_Button = "//input[@value='Login'] | //input[@value='Log In'] | //input[@value='Log in'] | //a[@id='submitLink']";


	public static String genDateBasedRandVal() {

		SimpleDateFormat tmformat = new SimpleDateFormat("DDDHHmmss");
		return tmformat.format(new Date())+ Thread.currentThread().getId();
	}

	public static String genMonthDateBasedRandVal() {
		//Create format class
		SimpleDateFormat tmformat = new SimpleDateFormat("MMddHHmmss");
		return tmformat.format(new Date());
	}

	public static String genDateBasedRand() {

		SimpleDateFormat tmformat = new SimpleDateFormat("DDDHHmmssSS");
		return tmformat.format(new Date());
	}

	public static String genDateBasedRandVal2() {
		//Create format class
		SimpleDateFormat tmformat = new SimpleDateFormat("MMdd");

		return tmformat.format(new Date());
	}

	public static String genDateBasedRandVal3() {
		//Create format class
		SimpleDateFormat tmformat = new SimpleDateFormat("HHmmss");

		return tmformat.format(new Date());
	}

	// Generate a random string that uses Java's secure random number
	// generator rather than a time based approach which risks generating
	// duplicates. The default uses 72 bits which gets encoded into a
	// 12 character string.
	public static String genStrongRand() {
		return genStrongRand(9);
	}

	public static String genStrongRand(int nBytes) {
		Assert.assertTrue(nBytes > 0, "The length was " + nBytes + ". It must be greater than zero.");
		// We want the amount of bytes to be an even multiple of three so it
		// gets base64encoded in a tidy manner.
		String encodedRandBytes = "";
		int padding = nBytes % 3;
		if (padding != 0)
			padding = 3 - padding;
		int len = nBytes + padding;

		byte randBytes[] = new byte[len];
		SecureRandom random = new SecureRandom();
		random.nextBytes(randBytes);

		encodedRandBytes = Base64.encodeBase64URLSafeString(randBytes);

		return encodedRandBytes;
	}
	
	// Generate Random Alphabetic String With Java 8
	// @Param targetStringLength : Character string length.
	public static String genRandString(int targetStringLength) {
		int leftLimit = 97;
	    int rightLimit = 122;
	    Random random = new Random();
	 
	    String generatedString = random.ints(leftLimit, rightLimit + 1)
	      .limit(targetStringLength)
	      .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
	      .toString();
	 
		return generatedString;
	}

	public static String stamp(String start) {

		return start + genDateBasedRandVal();
	}

	// get form based authentication
	public static Cookie [] executeJLogin (String url, String user, String pw) throws HttpException, IOException {

		org.apache.commons.httpclient.Cookie[] cookies = apacheLogin(url, user, pw);

		ArrayList<Cookie> sCookies = new ArrayList<Cookie>();
		for(org.apache.commons.httpclient.Cookie c: cookies) {
			sCookies.add(new Cookie(c.getName(), c.getValue(), c.getDomain(), c.getPath(), c.getExpiryDate()));
		}
		return sCookies.toArray(new Cookie[sCookies.size()]);
	}

	// get form based authentication
	public static org.apache.commons.httpclient.Cookie[] apacheLogin(String url, String user, String pw) throws HttpException, IOException {

		HttpClient http = new HttpClient();
		http.getState().clearCookies();

		url = url.replaceFirst("http:", "https:");
		url += "/j_security_check";
		String body = "j_username=" + user +"&" + "j_password=" + pw;
		PostMethod method = new PostMethod(url);

		((EntityEnclosingMethod)method).setRequestEntity(new StringRequestEntity(body, "application/x-www-form-urlencoded", "utf-8"));

		http.executeMethod(method);
		method.releaseConnection();

		return http.getState().getCookies();
	}

	public static boolean sendGetRequest(String url) {
		boolean success = true;
		HttpClient client = new HttpClient();
		GetMethod method = new GetMethod(url);
		// Provide custom retry handler is necessary
	    method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
	    		new DefaultHttpMethodRetryHandler(3, false));
		try {
			client.executeMethod(method);
		} catch (Exception e){
			success = false;
		} finally {
			method.releaseConnection();
		}

		return success;
	}

	public static String getRequestString(String url) {
		return getRequestString(url, null, null);
	}

	public static String getRequestString(String url, String user, String password) {
		String response = null;
		HttpClient client = new HttpClient();
		GetMethod method = new GetMethod(url);
		// Provide custom retry handler is necessary
	    method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
	    		new DefaultHttpMethodRetryHandler(3, false));
	    if (user != null && password != null) {
	    	org.apache.commons.httpclient.Cookie[] cookies;
			try {
				cookies = Helper.apacheLogin(url, user, password);
				client.getState().addCookies(cookies);
			} catch (HttpException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }
		try {
			client.executeMethod(method);
			response = method.getResponseBodyAsString();
		} catch (Exception e){
		} finally {
			method.releaseConnection();
		}
		return response;
	}

	public static String deleteRequestString_BrowserStack(String url, String user, String password) {
		String maskedAddress="";
		maskedAddress=url.replace((CharSequence)url.substring(url.indexOf("//")+2, url.indexOf(":",url.indexOf("//"))),"***************");
		maskedAddress=maskedAddress.replace(maskedAddress.substring(maskedAddress.indexOf(":",maskedAddress.indexOf("//"))+1, maskedAddress.indexOf("@")),"***************");
		log.info("URL - " + maskedAddress);

		DeleteMethod method = new DeleteMethod(url);
		HashMap response = makeBasicAuthRequest(method, user, password);
		return response.get("body").toString();
	}

	public static String getRequestString_BrowserStack(String url, String user, String password) {
		String maskedAddress="";
		maskedAddress=url.replace((CharSequence)url.substring(url.indexOf("//")+2, url.indexOf(":",url.indexOf("//"))),"***************");
		maskedAddress=maskedAddress.replace(maskedAddress.substring(maskedAddress.indexOf(":",maskedAddress.indexOf("//"))+1, maskedAddress.indexOf("@")),"***************");
		log.info("URL - " + maskedAddress);

		HttpClient client = new HttpClient();
		GetMethod method = new GetMethod(url);
		// Provide custom retry handler is necessary
		HashMap response = makeBasicAuthRequest(method, user, password);
		return response.get("body").toString();
	}

	public static boolean putRequestString_BrowserStack(String url, String user, String password, String statusExpression) {
		int response = -1;
		boolean flg=false;
		String maskedAddress="";
		maskedAddress=url.replace((CharSequence)url.substring(url.indexOf("//")+2, url.indexOf(":",url.indexOf("//"))),"***************");
		maskedAddress=maskedAddress.replace(maskedAddress.substring(maskedAddress.indexOf(":",maskedAddress.indexOf("//"))+1, maskedAddress.indexOf("@")),"***************");
		log.info("URL - " + maskedAddress);
		HttpClient client = new HttpClient();
		PutMethod method = new PutMethod(url);
		// Provide custom retry handler is necessary
		method.setRequestHeader("accept", "application/json");
	    method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
	    		new DefaultHttpMethodRetryHandler(3, false));
	    if (user != null && password != null) {
	    	String cred = user+":"+password;
			Base64 base64 = new Base64();
			String encodedString = new String(base64.encode(cred.getBytes())).replace("\n", "").replace("\r", "");
			method.setRequestHeader("Authorization", "Basic " + encodedString);
		}
		try {
			String inputJson = "{\"status\":\"" + statusExpression.toLowerCase() + "\", \"reason\":\"Test case status is marked " + statusExpression.toLowerCase() + " through test automation script\"}";
			StringRequestEntity requestEntity = new StringRequestEntity(
					inputJson,
				    "application/json",
				    "UTF-8");
			method.setRequestEntity(requestEntity);
			response = client.executeMethod(method);
			method.getResponseBodyAsString();
			if(response==200){
				flg=true;
			}
			else{
				log.error("HTTP response =" + response);
			}

		} catch (Exception e){
			e.printStackTrace();
			log.error("Failed to update test execution status in BrowserStack", e);
		} finally {
			method.releaseConnection();
		}
		return flg;
	}

	private static <T extends HttpMethodBase> HashMap makeBasicAuthRequest(T method, String user, String password) {
		// Provide custom retry handler is necessary
		HashMap<String, Object> hm = new HashMap<String, Object>();
		method.setRequestHeader("accept", "application/json");
		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler(3, false));
		if (user != null && password != null) {
			String cred = user+":"+password;
			Base64 base64 = new Base64();
			String encodedString = new String(base64.encode(cred.getBytes())).replace("\n", "").replace("\r", "");
			method.setRequestHeader("Authorization", "Basic " + encodedString);
		}

		HttpClient client = new HttpClient();
		String response = null;
		try {
			int statuscode=client.executeMethod(method);
			response = method.getResponseBodyAsString();
			Header[] header = method.getResponseHeaders();
			hm.put("statuscode", statuscode);
			hm.put("body", response);
			hm.put("header", header);

		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		} finally {
			method.releaseConnection();
		}
		return hm;
	}

	public static int getResponseCode(String url) {
		int response = -1;
		HttpClient client = new HttpClient();
		GetMethod method = new GetMethod(url);
		// Provide custom retry handler is necessary
	    method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
	    		new DefaultHttpMethodRetryHandler(3, false));
		try {
			response = client.executeMethod(method);
		} catch (IOException e){
		} finally {
			method.releaseConnection();
		}
		return response;
	}

	@SuppressWarnings("deprecation")
	public static int getResponseCodeHTMLUnit (String url, String user, String password)
	{
		WebDriver driver;
		int status = 500;

		String hub = TestConfigCustom.getInstance().getTestConfig().getServerHost();
		String port = TestConfigCustom.getInstance().getTestConfig().getServerPort();

		try {
			if(TestConfigCustom.getInstance().getTestConfig().serverIsGridHub())
		driver = new RemoteWebDriver(new URL("http://"+hub+":"+port+"/wd/hub"), DesiredCapabilities.firefox());
			else
		driver = new FirefoxDriver();

		driver.get(url);

        driver.findElement(By.cssSelector("input[name='username']")).sendKeys(user);
        driver.findElement(By.cssSelector("input[name='password']")).sendKeys(password);

		//Click the login button
        driver.findElement(By.cssSelector("input[value='Login']")).click();

      //wait for the html page to complete
        try {
			Thread.sleep(11);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        Set<Cookie> tamCookies =  driver.manage().getCookies();

        for(Cookie e:tamCookies)//this is to display all cookies
        {
        	System.out.println("The Cookie are as follows: "+e.toString());
        }

        driver.quit();
        WebClient driver2 = new WebClient();
        driver2.getOptions().setThrowExceptionOnFailingStatusCode(false);
        if (TestConfigCustom.getInstance().getSecurityType().equalsIgnoreCase("TAM"))
        {
        driver2.getOptions().setSSLInsecureProtocol(SSLSocketFactory.TLS);
        driver2.getOptions().setUseInsecureSSL(true);
        }


		String CookieName = "", CookieValue = "", CookieDomain = "";

        for(Cookie e:tamCookies)//this is to display all cookies
        {
        	CookieName = e.getName();
        	CookieValue = e.getValue();
        	CookieDomain = e.getDomain();

        	System.out.println("Adding a cookie.");
           	driver2.getCookieManager().addCookie(new com.gargoylesoftware.htmlunit.util.Cookie(CookieDomain, CookieName, CookieValue) );
            System.out.println("Added a cookie.");
        }



		try {
			 status = driver2.getPage(url).getWebResponse()
			        .getStatusCode();
			 System.out.println(status);

		} catch (FailingHttpStatusCodeException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		finally {
			driver2.close(); // to avoid resource leak
		}
		} catch (MalformedURLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		return status;
}

	public static JsonObject parseJson(String json) {
		if(json == null)
			return null;
		JsonObject obj = null;
		try {
			System.out.println("The json parseJson-- : "+json);
			obj = new JsonParser().parse(json).getAsJsonObject();
		} catch (Exception e) {
			return null;
		}
		return obj;
	}

	public static void recordJSErrors(Executor driver, ITestResult tr) {
		try {
			List<JavaScriptError> jsErrors = JavaScriptError.readErrors((WebDriver) driver.getBackingObject());
			if(jsErrors.isEmpty())
				return;
			String html = "<html><body><table style='border-collapse:collapse;text-align:center;'><tr><th style='border-style:solid;border-width:1px;'>Error Message</th><th style='border-style:solid;border-width:1px;'>Console Output</th><th style='border-style:solid;border-width:1px;'>Source</th></tr>";
			for(JavaScriptError error: jsErrors) {
				html += "<tr><td style='border-style:solid;border-width:1px;'>" + error.getErrorMessage() + "</td><td style='border-style:solid;border-width:1px;'>" + error.getConsole() + "</td><td style='border-style:solid;border-width:1px;'>" + error.getSourceName() + "</td></tr>";
			}
			html += "</table></body></html>";

			ITestContext context = tr.getTestContext();
			FileIOHandler.createFolderFromPath(context.getOutputDirectory());
			File jsDir = new File(context.getOutputDirectory() + File.separator + "jslog");
			String fileName = testFileName(tr, "html");
			createFile(jsDir, fileName, html);
			String relPath = ".."  + File.separator + context.getSuite().getName() + File.separator + "jslog" + File.separator + fileName;
			tr.setAttribute("jsErrorPath", relPath);
		} catch (Exception ex) {
			//Ignore errors
		}
	}

	public static void createDefectLog(ITestResult tr, DefectLogger dl) {
		if(tr.getStatus() != ITestResult.FAILURE)
			return;
		try {
			ITestContext context = tr.getTestContext();
			FileIOHandler.createFolderFromPath(context.getOutputDirectory());
			File defectsDir = new File(context.getOutputDirectory() + File.separator + "defectLog");
			String fileName = testFileName(tr, "log");
			String description = dl.print() + "\r\n\r\n TestNG error: " + tr.getThrowable().getMessage();
			createFile(defectsDir, fileName, description);
		} catch (Exception ex) {
			//Ignore errors
		}
	}

	public static void createVideoOutput(ITestResult tr, String URL, int vidNum) {
		try {
			ITestContext context = tr.getTestContext();
			FileIOHandler.createFolderFromPath(context.getOutputDirectory());
			File videosDir = new File(context.getOutputDirectory() + File.separator + "videos");
			String fileName = testFileName(tr, "html");
			fileName = "video_" + vidNum + "_" + fileName;
			String html = String.format("<html><a href=\"%s\">Test Video</a></html>", URL);
			createFile(videosDir, fileName, html);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static String getVideoURL(String session,TestConfiguration testConfig){
		// Create url to video
		TestConfigCustom cfg = TestConfigCustom.getInstance();
		String fileServer = cfg.getFileServer();
		String hub = Helper.getHubShort(testConfig.getServerHost());
		return String.format("http://%s/%s/%s.mp4", fileServer, hub, session);

	}

	public static String getGridURL(RCLocationExecutor driver, TestConfiguration testConfig){
		// Get url to video on grid
		String hub = Helper.getHubShort(testConfig.getServerHost());
		String session = ((RemoteWebDriver)driver.getBackingObject()).getSessionId().toString();
		String node = Helper.getGridNodeName((RemoteWebDriver)driver.getBackingObject(), testConfig.getServerHost(), Integer.parseInt(testConfig.getServerPort()));
		return String.format("http://%s:3000/upload_video?hub=%s&session=%s", node, hub, session);
	}

	public static void endSession(RCLocationExecutor driver, TestConfigCustom cfg){
		TestConfiguration testConfig = cfg.getTestConfig();
		if (!driver.isLoaded() || !cfg.getPushVideos() || !testConfig.serverIsGridHub()){
			return;
		}
		try{
			String currGridURL = Helper.getGridURL(driver, testConfig);
			String currSession = ((RemoteWebDriver)driver.getBackingObject()).getSessionId().toString();
			// Store object that associates gridURL with session ID
			SetUpMethods2.videoMap.get(Thread.currentThread().getId()).add(new Video(currGridURL, currSession));
		}
		catch (Exception e){
			log.error("Failed to capture session", e);
		}
	}

	private static String testFileName(ITestResult tr, String extension){
		String methodName = tr.getName();
		String className = tr.getTestClass().getName();
		className = className.substring(className.lastIndexOf(".")+1, className.length());
		return className + "_" + methodName + "." + extension;
	}

	private static void createFile(File dir, String fileName, String content) {
		FileIOHandler.createFolderFromPath(dir.getAbsolutePath());
		File targetFile = new File(dir + File.separator + fileName);
		PrintWriter out = null;
		try {
			out = new PrintWriter(targetFile);
			out.println(content);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			out.close();
		}
	}

	public static String getRequestStringSecurityJSON (String url, String user, String password)
	{
		String pageSource = "";
		//WebDriver driver = new FirefoxDriver();
		WebDriver driver;

		String hub = TestConfigCustom.getInstance().getTestConfig().getServerHost();
		String port = TestConfigCustom.getInstance().getTestConfig().getServerPort();

		try {
			if(TestConfigCustom.getInstance().getTestConfig().serverIsGridHub())
			driver = new RemoteWebDriver(new URL("http://"+hub+":"+port+"/wd/hub"), DesiredCapabilities.firefox());
		else
			driver = new FirefoxDriver();

			driver.get(url);

		if(TestConfigCustom.getInstance().getSecurityType().equalsIgnoreCase("TAM_SPNEGO")||TestConfigCustom.getInstance().getSecurityType().equalsIgnoreCase("TAM"))
        {driver.findElement(By.cssSelector("input[name='username']")).sendKeys(user);
        driver.findElement(By.cssSelector("input[name='password']")).sendKeys(password);
        }else if(TestConfigCustom.getInstance().getSecurityType().equalsIgnoreCase("SITEMINDER"))
        {
        	driver.findElement(By.cssSelector("input[name='USER']")).sendKeys(user);
            driver.findElement(By.cssSelector("input[name='PASSWORD']")).sendKeys(password);
        }

		//Click the login button
        driver.findElement(By.cssSelector("input[value='Login']")).click();
        pageSource = driver.getPageSource();
		driver.quit();

		pageSource = pageSource.substring(pageSource.indexOf("{"), pageSource.lastIndexOf("}")+1);
		System.out.println("The source of the page is : "+pageSource);

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return pageSource;
	}

	/**
     *
     * Method to return the node that a test is running against.
     *
     * @param dr - instance of WebDriver
     * @return - representing the string used to register the node
     * @author Liam Walsh
     */
	public static String getGridNodeName(WebDriver dr,String gridHub,Integer port){

    	String proxyID = null;
    	String retProxyID = null;

    	try {
			HttpClient http = new HttpClient();
			String url = "http://" + gridHub + ":" + port + "/grid/api/testsession?session=" +  ((RemoteWebDriver)dr).getSessionId();
			GetMethod method = new GetMethod(url);
			http.executeMethod(method);
			JsonObject object = new JsonParser().parse(method.getResponseBodyAsString()).getAsJsonObject();
			proxyID = object.get("proxyId").toString().replace("\"", "");

			if (proxyID.contains("://"))  {
				retProxyID = proxyID.split("//")[1].split(":")[0];
			} else {
				retProxyID = proxyID;
			}

			method.releaseConnection();

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JsonParseException e) {
			e.printStackTrace();
		}
		return retProxyID;
    }

	public static String getHubShort(String hub) {
		String[] hubUrlSplit = hub.split("\\.");
		return hubUrlSplit[0];
	}

	/**
	 * <b>This will prevent exceptions being thrown for null strings</b><br>
	 * <i>This is designed to minimize the code length and allow easier maintenance of the code</i>
	 * @param toCheck - The String that you are checking if it has text in it
	 * @return <b>true</b> if the String passed is not null and is not an empty String <br>
	 * <b>false</b> if the passed String is null or empty
	 * @author Matt Maffa
	 */
	public static boolean containsText(String toCheck){
		if (toCheck == null)
			return false;
		if (toCheck.isEmpty())
			return false;
		return true;
	}

}
