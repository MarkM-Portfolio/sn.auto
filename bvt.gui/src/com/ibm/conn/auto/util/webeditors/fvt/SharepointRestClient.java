package com.ibm.conn.auto.util.webeditors.fvt;

import static com.ibm.conn.auto.util.webeditors.fvt.FVT_WebeditorsProperties.*;
import static org.seleniumhq.jetty9.http.HttpScheme.HTTPS;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicHeader;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.gargoylesoftware.htmlunit.util.Cookie;

public class SharepointRestClient extends RestClient {

	private static final Logger log = LoggerFactory.getLogger(SharepointRestClient.class);

	private static final String
			// REST interface
			// https://msdn.microsoft.com/en-us/library/ee539976.aspx
			// https://msdn.microsoft.com/en-us/library/office/fp142380.aspx#bk_URLexamples
			// https://msdn.microsoft.com/en-us/library/office/dn903506.aspx
			REST_SERVICE_URL = "/_api", 
			GET_CONTEXT_INFO = "/contextinfo",
			GET_FOLDER_BY_RELATIVE_URL = "/Web/GetFolderByServerRelativeUrl('%s')",
			GET_FILE_BY_RELATIVE_URL = "/Web/GetFileByServerRelativeUrl('%s')",

			// Sharepoint Login Form selectors
			SP_LOGIN_FORM_ID = "aspnetForm",
			SP_LOGIN_FORM_PATH = "/_forms/default.aspx", 
			SP_SUBMIT_BTN_NAME = "ctl00$PlaceHolderMain$signInControl$login",
			SP_PASSWORD_TXTBX_NAME = "ctl00$PlaceHolderMain$signInControl$password",
			SP_USERNAME_TXTBX_NAME = "ctl00$PlaceHolderMain$signInControl$UserName",
	
			// Sharepoint API response XPath selectors
			// http://stackoverflow.com/questions/5698458/xpath-using-starts-with-function
			RESOURCE_WITH_NAMEPREFIX = "/feed/entry/content/properties[starts-with(Name,'%s')]/ServerRelativeUrl/text()"
			;

	private static BasicHeader sharepointFormDigestValue;
	private static final BasicHeader 
			ACCEPTS_JSON_CONTENT = new BasicHeader("accept", "application/json; odata=verbose"),
			DELETE_RESOURCE_REQUEST = new BasicHeader("X-HTTP-Method", "DELETE");

	public SharepointRestClient(String urlScheme, String serverHostName, int serverHostPort, boolean trustAnyCertificate, String userName, String userPass)
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, UnknownHostException {
		super(urlScheme, serverHostName, serverHostPort, trustAnyCertificate, userName, userPass);
	}

	private void setFormDigestValue() throws KeyManagementException, ClientProtocolException, NoSuchAlgorithmException,
			IOException, JSONException, URISyntaxException {

		String response = doRequest(
				new HttpPost( buildURI(SHAREPOINT_COLLECTION_PATH + REST_SERVICE_URL + GET_CONTEXT_INFO) ),
				Arrays.asList(new Header[] { ACCEPTS_JSON_CONTENT })); // returns JSON formatted content

		JSONObject jsonObject = new JSONObject(response);
		JSONObject contextWebInformation = jsonObject.getJSONObject("d").getJSONObject("GetContextWebInformation");
		sharepointFormDigestValue = new BasicHeader("X-RequestDigest", contextWebInformation.getString("FormDigestValue"));
	}

	public void performFormAuthentication() throws KeyManagementException, ClientProtocolException,
			NoSuchAlgorithmException, IOException, JSONException, URISyntaxException {

		log.info("Setting up the web client");
		final WebClient webClient = new WebClient();
		if (urlScheme.equalsIgnoreCase(HTTPS.toString())) {
			// If set to true, the client will accept connections to any host, regardless of whether they have valid certificates or not. 
			// This is especially useful when you are trying to connect to a server with expired or corrupt certificates.
			webClient.getOptions().setUseInsecureSSL(true); 
		}
		// This keeps the webClient going despite any JS error that may occur
		// http://stackoverflow.com/questions/3252859/impossible-site-for-htmlunit
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		
		final String sharepointLoginPageURI = buildURI(SP_LOGIN_FORM_PATH).toString();
		log.info("Get the login page:'" + sharepointLoginPageURI + "'");
		final HtmlPage sharepointLoginPage = webClient.getPage( sharepointLoginPageURI );
		
		log.info("Get the form that we are dealing with and within that form, find the fields that we want to change and the submit button.");
		HtmlForm sharepointLoginForm = null;
		for (HtmlForm currForm : sharepointLoginPage.getForms()) {
			if (currForm.getId().equals(SP_LOGIN_FORM_ID)) {
				sharepointLoginForm = currForm;
				break;
			}
		}
		Assert.assertNotNull(sharepointLoginForm, "Could not find a form with ID '" + SP_LOGIN_FORM_ID + "' in page '" + buildURI(SP_LOGIN_FORM_PATH).toString() + "'.");
		
		final HtmlTextInput userNameInputField = sharepointLoginForm.getInputByName(SP_USERNAME_TXTBX_NAME);
		final HtmlPasswordInput userPasswordField = sharepointLoginForm.getInputByName(SP_PASSWORD_TXTBX_NAME);
		final HtmlSubmitInput signInSubmitButton = sharepointLoginForm.getInputByName(SP_SUBMIT_BTN_NAME);
		
		log.info("Change the value of the text field");
		userNameInputField.setValueAttribute(getUserName());
		userPasswordField.setValueAttribute(getUserPass());
		
		log.info("Submit the form by clicking the button");
		final HtmlPage sharepointHomePage = signInSubmitButton.click();
		Assert.assertNotNull(sharepointHomePage, "While logging into Sharepoint, a null page was returned!");
		
		log.info("Transfer all resulting cookies to the http client's cookiestore");
		List<Cookie> loggedInCookies = new ArrayList<Cookie>();
		Iterator<Cookie> cookieIterator = webClient.getCookieManager().getCookies().iterator();
		while (cookieIterator.hasNext()) {
			loggedInCookies.add(cookieIterator.next());
		}
		for (org.apache.http.cookie.Cookie cookie : Cookie.toHttpClient(loggedInCookies))
			this.addCookie(cookie);
		
		webClient.close();
		
		log.info("Setting the FormDigestValue"); 
		setFormDigestValue();

	}

	public void deleteFolderByName(String contentBaseFolder, String folderName) throws ClientProtocolException, IOException, KeyManagementException,
			NoSuchAlgorithmException, MalformedURLException, URISyntaxException, ParserConfigurationException, SAXException, XPathExpressionException {
		
		log.info("Requesting Sharepoint to delete '" + contentBaseFolder + "/" + folderName + "'.");
		String deleteFolderStatusData = doRequest(
				new HttpPost( buildURI( SHAREPOINT_COLLECTION_PATH + REST_SERVICE_URL + 
						String.format(GET_FOLDER_BY_RELATIVE_URL, SHAREPOINT_COLLECTION_PATH + "/" + contentBaseFolder + "/" + folderName ))
						),
				Arrays.asList(new Header[] { ACCEPTS_JSON_CONTENT, DELETE_RESOURCE_REQUEST, sharepointFormDigestValue })
				);
		log.info("Result:'" + deleteFolderStatusData + "'");

	}

	public void deleteFileByName(String contentBaseFolder, String fileName) throws ClientProtocolException, IOException, KeyManagementException,
			NoSuchAlgorithmException, MalformedURLException, URISyntaxException, ParserConfigurationException, SAXException, XPathExpressionException {

		log.info("Requesting Sharepoint to delete '" + contentBaseFolder + "/" + fileName + "'.");
		String deleteFileStatusData = doRequest(
				new HttpPost( buildURI( SHAREPOINT_COLLECTION_PATH + REST_SERVICE_URL + 
						String.format(GET_FILE_BY_RELATIVE_URL, SHAREPOINT_COLLECTION_PATH + "/" + contentBaseFolder + "/" + fileName )) 
						),
				Arrays.asList(new Header[] { ACCEPTS_JSON_CONTENT, DELETE_RESOURCE_REQUEST, sharepointFormDigestValue })
				);
		log.info("Result:'" + deleteFileStatusData + "'");
	}	

	public void deleteFoldersWithPrefix(String contentBaseFolder, String folderNamePrefix) throws ClientProtocolException, IOException, KeyManagementException,
			NoSuchAlgorithmException, MalformedURLException, URISyntaxException, ParserConfigurationException, SAXException, XPathExpressionException {
		
		log.info("Gathering the pathnames of the folders to be deleted");
		String xmlFoldersData = doRequest(
				new HttpGet( buildURI(SHAREPOINT_COLLECTION_PATH + REST_SERVICE_URL + String.format(GET_FOLDER_BY_RELATIVE_URL, contentBaseFolder) + "/Folders") ),
				null // returns XML formatted content
		);
		
		NodeList foldersWithNamePrefix = getResourcesByXPath(xmlFoldersData, String.format(RESOURCE_WITH_NAMEPREFIX, folderNamePrefix));
		
		log.info("Once gathered, the folders are deleted"); 
		for(int i = 0; i < foldersWithNamePrefix.getLength(); ++i) {
			
			log.info("Requesting Sharepoint to delete '" + foldersWithNamePrefix.item(i).getNodeValue() + "'." );
			String deleteFolderStatusData = doRequest(
					new HttpPost( buildURI(SHAREPOINT_COLLECTION_PATH + REST_SERVICE_URL + String.format(GET_FOLDER_BY_RELATIVE_URL, foldersWithNamePrefix.item(i).getNodeValue())) ),
					Arrays.asList( new Header[] { ACCEPTS_JSON_CONTENT, DELETE_RESOURCE_REQUEST, sharepointFormDigestValue } )
			);
			
			log.info("Result:'" + deleteFolderStatusData + "'");
		}
	}

	public void deleteFilesWithPrefix(String contentBaseFolder, String fileNamePrefix) throws ClientProtocolException, IOException, KeyManagementException,
			NoSuchAlgorithmException, MalformedURLException, URISyntaxException, ParserConfigurationException, SAXException, XPathExpressionException {
		
		log.info("Gathering the pathnames of the files to be deleted");
		String xmlFilesData = doRequest(
				new HttpGet( buildURI(SHAREPOINT_COLLECTION_PATH + REST_SERVICE_URL + String.format(GET_FOLDER_BY_RELATIVE_URL, contentBaseFolder) + "/Files") ),
				null // returns XML formatted content
		);
		
		NodeList filesWithNamePrefix = getResourcesByXPath(xmlFilesData, String.format(RESOURCE_WITH_NAMEPREFIX, fileNamePrefix));
		
		log.info("Once gathered, the files are deleted");
		for(int i = 0; i < filesWithNamePrefix.getLength(); ++i) {
			log.info("Requesting Sharepoint to delete '" + filesWithNamePrefix.item(i).getNodeValue() + "'." );
			String deleteFileStatusData = doRequest(
					new HttpPost( buildURI(SHAREPOINT_COLLECTION_PATH + REST_SERVICE_URL + 
							String.format(GET_FILE_BY_RELATIVE_URL, filesWithNamePrefix.item(i).getNodeValue()))
							),
					Arrays.asList( new Header[] { ACCEPTS_JSON_CONTENT, DELETE_RESOURCE_REQUEST, sharepointFormDigestValue } )
			);
			log.info("Result:'" + deleteFileStatusData + "'");
		}
	}

	private NodeList getResourcesByXPath(String xmlData, String resourceXpathSelector)
			throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
		
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document xmlDocument = builder.parse(new ByteArrayInputStream(xmlData.getBytes("UTF-8"))); // StandardCharsets.UTF_8
		
		XPath nodeLocator = XPathFactory.newInstance().newXPath();
		NodeList serverRelativeFilesPath = (NodeList) nodeLocator.evaluate( resourceXpathSelector, xmlDocument.getDocumentElement(), XPathConstants.NODESET);
		
		return serverRelativeFilesPath;
	}

	public void addFile(String contentBaseFolder, String testFilename) 
			throws KeyManagementException, ClientProtocolException, NoSuchAlgorithmException, IOException, URISyntaxException {
		
		File testFile = new File(testFilename);
		Assert.assertTrue(testFile.exists(), "File '" + testFile.getPath() + "' does not exist!");
		
		BufferedInputStream testFileReader = new BufferedInputStream(new FileInputStream(testFile));
		byte[] testFileBuffer = IOUtils.toByteArray(testFileReader);
		
		String deleteFileStatusData = doRequest(new HttpPost( buildURI( SHAREPOINT_COLLECTION_PATH + REST_SERVICE_URL 
																+ String.format(GET_FOLDER_BY_RELATIVE_URL, contentBaseFolder) + "/Files"
																+ "/add(overwrite=true,url='"+testFile.getName()+"')"
																)
															),
												Arrays.asList( new Header[] { ACCEPTS_JSON_CONTENT, sharepointFormDigestValue } ),
												testFileBuffer
											);

		log.info("Result:'" + deleteFileStatusData + "'");
	}

}
