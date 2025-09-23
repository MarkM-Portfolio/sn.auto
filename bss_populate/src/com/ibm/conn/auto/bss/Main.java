package com.ibm.conn.auto.bss;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * 
 * Application to allow for call of BSS REST API
 * 
 * <p>
 * Ability for a user to create an org, add subscriptions and add users
 * 
 * <p>
 * Available switches
 * <li>-create_org
 * <li>-add_sub
 * <li>-add_user
 * <li>-create_org_add_sub
 * <li>-add_users_by_org_name
 * <li>-create_org_add_sub
 * <li>-create_org_add_sub_add_users
 * 
 * 
 * @author Liam Walsh (liamwals@ie.ibm.com)
 * @author Mark Mulcahy (markmulc@ie.ibm.com)
 * 
 */
public class Main {

	public static void main(String[] args) throws Exception {

		Date now = new Date(); // java.util.Date, NOT java.sql.Date or
								// java.sql.Timestamp!
		String format3 = new SimpleDateFormat("yyMMddHHmmss").format(now);

		FileWriter fw = null;
		BufferedWriter bw = null;

		if (args.length == 0) {
			logOut(bw,
					"Invalid input, no parameter(s) entered, to display available options pass -help");
			throw new Exception(
					"Invalid input, no parameter(s) entered, to display available options pass -help");
		}

		try {
			// Create a new file with the timestamp
			File file = new File(format3 + ".log");

			fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);
			Map<String, String> userIds = new HashMap<String, String>();

			String adminEmail = "";
			String server = "";
			String orgName = "";
			String partId = "";
			String user_email = "";
			String numParts = "100";
			String custid = "";
			String orgid = "";
			String users = "15";
			String csguser = "bssadmin@us.ibm.com";
			String csgpassword = "Pa88w0rd";
			String partnum = "";
			String firstname = "Amy";
			String lastname = "Jones";
			String filepath = "";
			// Sets parameters

			if (args[1].contentEquals("-properties")) {
				Properties prop = new Properties();
				InputStream input = null;
				try {

					input = new FileInputStream("config.properties");

					// load a properties file
					prop.load(input);

					adminEmail = prop.getProperty("adminEmail");
					server = prop.getProperty("server");
					orgName = prop.getProperty("orgName");
					partId = prop.getProperty("subscriptionID");
					user_email = prop.getProperty("userBaseMail");
					numParts = prop.getProperty("quantity");
					custid = prop.getProperty("customerID");
					orgid = prop.getProperty("orgID");
					users = prop.getProperty("numUsers");
					csguser = prop.getProperty("csgUser");
					csgpassword = prop.getProperty("csgPassword");
					partnum = prop.getProperty("partNum");
					firstname = prop.getProperty("firstName");
					lastname = prop.getProperty("lastName");
					filepath = prop.getProperty("csvFilePath");

				} catch (IOException ex) {
					ex.printStackTrace();
				}
			} else {
				for (int i = 1; i < args.length; i++) {
					if (args[i].contentEquals("-server")) {
						server = args[i + 1];
					}
					if (args[i].contentEquals("-adminemail")) {
						adminEmail = args[i + 1];
					}
					if (args[i].contentEquals("-orgname")) {
						orgName = args[i + 1];
					}
					if (args[i].contentEquals("-userbasemail")) {
						user_email = args[i + 1];
					}
					if (args[i].contentEquals("-qty")) {
						numParts = args[i + 1];
					}
					if (args[i].contentEquals("-custid")) {
						custid = args[i + 1];
					}
					if (args[i].contentEquals("-subid")) {
						partId = args[i + 1];
					}
					if (args[i].contentEquals("-orgid")) {
						orgid = args[i + 1];
					}
					if (args[i].contentEquals("-numusers")) {
						users = args[i + 1];
					}
					if (args[i].contentEquals("-csguser")) {
						csguser = args[i + 1];
					}
					if (args[i].contentEquals("-csgpassword")) {
						csgpassword = args[i + 1];
					}
					if (args[i].contentEquals("-partnum")) {
						partnum = args[i + 1];
					}
					if (args[i].contentEquals("-firstname")) {
						firstname = args[i + 1];
					}
					if (args[i].contentEquals("-lastname")) {
						lastname = args[i + 1];
					}
					if (args[i].contentEquals("-csv")) {
						filepath = args[i + 1];
					}
					if (args[i].contentEquals("-subscriber_email")) {
						user_email = args[i + 1];
					}
				}

			}

			bw.write("********************************************************************************************************************************************************* \n");
			bw.newLine();
			bw.write(format3);
			int numUsers = Integer.valueOf(users);

			// Creates an org
			if (args[0].contentEquals("-create_org")) {
				if (server.isEmpty() || orgName.isEmpty()
						|| adminEmail.isEmpty()) {
					bw.write("Invalid input, parameter(s) missing");
					if (server.isEmpty()) {
						logOut(bw, "Server parameter is missing.");
					}
					if (orgName.isEmpty()) {
						logOut(bw, "Org name parameter is missing.");
					}
					if (adminEmail.isEmpty()) {
						logOut(bw, "Admin email parameter is missing.");
					}
					throw new Exception("Invalid input, parameter(s) missing");
				}
				System.out.println("Creating org");
				logOut(bw, "Creating org using OrgName : " + orgName
						+ ", admin email :" + adminEmail + " on deployment : "
						+ "" + server + " \n");

				String retOrgId = addOrg(orgName, adminEmail, server, csguser,
						csgpassword);
				System.out
						.println("This does not activate the admin email or add a subscription");
				logOut(bw, "Org id returned as : " + retOrgId + " \n");
			}
			// Adds a subscription to an org
			else if (args[0].contentEquals("-add_sub")) {
				if (partnum.isEmpty() || custid.isEmpty() || server.isEmpty()) {
					logOut(bw, "Invalid input, parameter(s) missing");
					if (partnum.isEmpty()) {
						logOut(bw, "Part number parameter is missing.");
					}
					if (custid.isEmpty()) {
						logOut(bw, "Org id parameter is missing.");
					}
					if (server.isEmpty()) {
						logOut(bw, "Server parameter is missing.");
					}
					throw new Exception("Invalid input, parameter(s) missing");
				}
				System.out.println("Adding sub ");
				logOut(bw, "Adding a subscription using Part No : " + partnum
						+ " with quantity of : " + numParts + " for org id : "
						+ custid + " \n");

				addPart(partnum, numParts, custid, server, csguser, csgpassword);
			}
			// Adds users to an exisiting org
			else if (args[0].contentEquals("-add_user")) {
				if (orgid.isEmpty() || user_email.isEmpty() || server.isEmpty()) {
					bw.write("Invalid input, parameter(s) missing");
					if (server.isEmpty()) {
						logOut(bw, "Server parameter is missing.");
					}
					if (orgid.isEmpty()) {
						logOut(bw, "Org id parameter is missing.");
					}
					if (user_email.isEmpty()) {
						logOut(bw, "Users base email parameter is missing.");
					}
					throw new Exception("Invalid input, parameter(s) missing");
				}
				System.out.println("Adding user");
				logOut(bw, "Adding users to org : " + orgid);

				for (int i = 0; i < numUsers; i++) {
					String userID = addUser(orgid, user_email + i
							+ "@bluebox.lotus.com", server, csguser,
							csgpassword, firstname, lastname + i);
					userIds.put(user_email + i + "@bluebox.lotus.com", userID);
					logOut(bw, user_email + i + " added to org : " + orgid
							+ " \n");

				}
				Thread.sleep(15000);
				for (Entry<String, String> e : userIds.entrySet()) {
					String id = e.getValue();
					addSubscriberSub(id, partId, server, csguser, csgpassword);
				}
				logOut(bw, "Users activated. \n");
				for (int i = 0; i < numUsers; i++) {
					activateAccount(user_email + i + "@bluebox.lotus.com");
					logOut(bw, "Twill : " + user_email + i
							+ "@bluebox.lotus.com" + "\n");

				}

			}
			// Creates an org and adds a subscription
			else if (args[0].contentEquals("-create_org_add_sub")) {
				if (server.isEmpty() || orgName.isEmpty()
						|| adminEmail.isEmpty() || partnum.isEmpty()) {
					bw.write("Invalid input, parameter(s) missing");
					if (server.isEmpty()) {
						logOut(bw, "Server parameter is missing.");
					}
					if (orgName.isEmpty()) {
						logOut(bw, "Org name parameter is missing.");
					}
					if (adminEmail.isEmpty()) {
						logOut(bw, "Admin email parameter is missing.");
					}
					if (partnum.isEmpty()) {
						logOut(bw, "Part id parameter is missing.");
					}
					throw new Exception("Invalid input, parameter(s) missing");
				}
				System.out.println("Creating an org and adding a subscription");
				logOut(bw, "Creating an org and adding a subscription \n");
				logOut(bw, "Org name : " + orgName + " \n");

				System.out.println("Creating org");
				String retOrgId = addOrg(orgName, adminEmail, server, csguser,
						csgpassword);
				System.out.println("Creating subscription");
				logOut(bw, "Subscription part number : " + partnum + " with : "
						+ numParts + " seats" + " \n");

				addPart(partnum, numParts, retOrgId, server, csguser,
						csgpassword);
				logOut(bw, "Activating admin email account");
				Thread.sleep(30000);
				activateAccount(adminEmail);

			}
			// Creates an org, adds a subscription and creates users
			else if (args[0].contentEquals("-create_org_add_sub_add_users")) {
				if (server.isEmpty() || orgName.isEmpty()
						|| adminEmail.isEmpty() || partnum.isEmpty()
						|| user_email.isEmpty() || adminEmail.isEmpty()) {
					logOut(bw, "Invalid input, parameter(s) missing");
					if (adminEmail.isEmpty()) {
						logOut(bw, "Admin email parameter is missing.");
					}
					if (server.isEmpty()) {
						logOut(bw, "Server address parameter is missing.");
					}
					if (orgName.isEmpty()) {
						logOut(bw, "Org name parameter is missing.");
					}
					if (partnum.isEmpty()) {
						logOut(bw, "Part id parameter is missing.");
					}
					if (user_email.isEmpty()) {
						logOut(bw, "User base email parameter is missing.");
					}
					throw new Exception("Invalid input, parameter(s) missing");
				}
				System.out
						.println("Creating an org and adding a subscription with users");
				logOut(bw,
						"Creating an org and adding a subscription with users \n");

				System.out.println("Creating org");
				logOut(bw, "Org name : " + orgName + "\n");

				String retOrgId = addOrg(orgName, adminEmail, server, csguser,
						csgpassword);
				System.out.println("Creating subscription");
				logOut(bw, "Subscription part number : " + partnum + " with : "
						+ numParts + " seats \n");

				String subID = addPart(partnum, numParts, retOrgId, server,
						csguser, csgpassword);
				System.out.println("Activating admin email account");
				logOut(bw, "Activating admin email account : " + adminEmail
						+ " \n");

				Thread.sleep(30000);
				activateAccount(adminEmail);

				bw.write("Email accounts using : " + user_email);

				for (int i = 0; i < numUsers; i++) {
					String userID = addUser(retOrgId, user_email + i
							+ "@bluebox.lotus.com", server, csguser,
							csgpassword, firstname, lastname + i);
					userIds.put(user_email + i + "@bluebox.lotus.com", userID);
					logOut(bw, "Email account : " + user_email + i
							+ "@bluebox.lotus.com" + "\n");

				}
				Thread.sleep(15000);
				for (Entry<String, String> e : userIds.entrySet()) {
					String id = e.getValue();

					logOut(bw, "Adding sub for user id : " + id
							+ " , and subscription id " + subID + " \n");
					addSubscriberSub(id, subID, server, csguser, csgpassword);

				}

				for (int i = 0; i < numUsers; i++) {
					activateAccount(user_email + i + "@bluebox.lotus.com");
					logOut(bw, "Twill : " + user_email + i
							+ "@bluebox.lotus.com" + "\n");

				}

				logOut(bw, "Generating users csv file");
				generateUsersCSV(firstname, lastname, user_email, numUsers);
				logOut(bw, "Users csv file saved");
			}
			// Creates users and adds to an exisiting org
			else if (args[0].contentEquals("-add_users_by_org_name")) {
				if (server.isEmpty() || orgName.isEmpty()) {
					logOut(bw, "Invalid input, parameter(s) missing");
					if (server.isEmpty()) {
						logOut(bw, "Server parameter is missing.");
					}
					if (orgName.isEmpty()) {
						logOut(bw, "Org name parameter is missing.");
					}
					throw new Exception("Invalid input, parameter(s) missing");
				}
				logOut(bw, "Adding users to an existing org \n");

				orgid = getOrgID(orgName, server, csguser, csgpassword);
				for (int i = 0; i < numUsers; i++) {
					String userID = addUser(orgid, user_email + i
							+ "@bluebox.lotus.com", server, csguser,
							csgpassword, firstname, lastname + i);
					userIds.put(user_email + i + "@bluebox.lotus.com", userID);
					logOut(bw, "Email account : " + user_email + i
							+ "@bluebox.lotus.com" + "\n");

				}
				Thread.sleep(15000);
				for (Entry<String, String> e : userIds.entrySet()) {
					String id = e.getValue();
					logOut(bw, "Adding sub for user id : " + id
							+ " , and subscription id " + partId + " \n");

					addSubscriberSub(id, partId, server, csguser, csgpassword);
				}

				for (int i = 0; i < numUsers; i++) {
					activateAccount(user_email + i + "@bluebox.lotus.com");
					logOut(bw, "Twill : " + user_email + i
							+ "@bluebox.lotus.com" + "\n");

				}

				logOut(bw, "Generating users csv file");
				generateUsersCSV(firstname, lastname, user_email, numUsers);
				logOut(bw, "Users csv file saved");
			} else if (args[0].contentEquals("-add_users_by_org_name_csv")) {
				if (server.isEmpty() || orgName.isEmpty()) {
					logOut(bw, "Invalid input, parameter(s) missing");
					if (server.isEmpty()) {
						logOut(bw, "Server parameter is missing.");
					}
					if (orgName.isEmpty()) {
						logOut(bw, "Org name parameter is missing.");
					}
					throw new Exception("Invalid input, parameter(s) missing");
				}
				logOut(bw, "Adding users to an existing org \n");

				orgid = getOrgID(orgName, server, csguser, csgpassword);
				ArrayList<String> tempList = new ArrayList<String>();
				tempList = CSVhandler.readFileByLines(filepath);
				int size = tempList.size();
				System.out.println(tempList.size());
				System.out.println(tempList.get(size - 1));
				String[] tempArray;

				for (int i = 0; i < size - 1; i++) {

					tempArray = tempList.get(i + 1).split(",");
					user_email = tempArray[0];
					firstname = tempArray[1];
					lastname = tempArray[2];
					String userID = addUser(orgid, user_email
							+ "@bluebox.lotus.com", server, csguser,
							csgpassword, firstname, lastname);
					userIds.put(user_email + "@bluebox.lotus.com", userID);
					logOut(bw, "Email account : " + user_email
							+ "@bluebox.lotus.com" + "\n");

				}
				Thread.sleep(15000);
				for (Entry<String, String> e : userIds.entrySet()) {
					String id = e.getValue();
					logOut(bw, "Adding sub for user id : " + id
							+ " , and subscription id " + partId + " \n");

					addSubscriberSub(id, partId, server, csguser, csgpassword);
				}

				for (int i = 0; i < numUsers; i++) {
					activateAccount(user_email + i + "@bluebox.lotus.com");
					logOut(bw, "Twill : " + user_email + i
							+ "@bluebox.lotus.com" + "\n");

				}
			}

			// Deletes an org by name
			else if (args[0].contentEquals("-remove_org")) {
				if (server.isEmpty() || orgName.isEmpty()) {
					bw.write("Invalid input, parameter(s) missing");
					if (server.isEmpty()) {
						logOut(bw, "Server parameter is missing.");
					}
					if (orgName.isEmpty()) {
						logOut(bw, "Org name parameter is missing.");
					}
					throw new Exception("Invalid input, parameter(s) missing");
				}
				System.out.println("Getting org id");

				String retOrgId = getOrgID(orgName, server, csguser,
						csgpassword);

				logOut(bw, "Org id returned as : " + retOrgId + " \n");

				deleteOrg(retOrgId, server, csguser, csgpassword);

			}

			// Deletes a user by their email
			else if (args[0].contentEquals("-remove_subscriber")) {
				if (server.isEmpty() || user_email.isEmpty()) {
					bw.write("Invalid input, parameter(s) missing");
					if (server.isEmpty()) {
						logOut(bw, "Server parameter is missing.");
					}
					if (user_email.isEmpty()) {
						logOut(bw, "Subscriber email parameter is missing.");
					}
					throw new Exception("Invalid input, parameter(s) missing");
				}
				System.out.println("Getting user id");

				String subscriberId = getSubsciberIdByEmail(user_email, server,
						csguser, csgpassword);

				logOut(bw, "Subscriber id returned as : " + subscriberId
						+ " \n");

				deleteSubscriber(subscriberId, server, csguser, csgpassword);
			}

			else if (args[0].contentEquals("-help")) {
				System.out.println("Available switches : \n");

				System.out.println("Switch: -create_org");
				System.out.println(" [Description] Create a new org \n");
				System.out
						.println(" [Parameters] -orgname -adminemail -server\n");
				System.out
						.println(" [Example usage] -create_org -orgname Test_CL -adminemail test_cl_bss@bluebox.lotus.com -server https://apps.dailyz.swg.usma.ibm.com\n");

				System.out.println("Switch: -create_org_add_sub");
				System.out
						.println(" [Description] Create a new org and add a new subcription \n");
				System.out
						.println(" [Parameters] -orgname -adminemail -server -partnum -qty (default 100)\n");
				System.out
						.println(" [Example usage] -create_org_add_sub -orgname Test_Org_Name -adminemail admin_test_mail@bluebox.lotus.com -server https://apps.dailyz.swg.usma.ibm.com -partnum D09TFLL -qty 100\n");

				System.out.println("Switch: -create_org_add_sub_add_users");
				System.out
						.println(" [Description] Create a new org, add a new subscription. Populate with users and entitle them. \n");
				System.out
						.println(" [Parameters] -orgname -adminemail -server -partnum -qty (default 100) -userbasemail (do not include mail domain) -numusers (default 15)\n");
				System.out
						.println(" [Example usage] -create_org_add_sub_add_users -orgname Test_Org_Name -adminemail admin_test_mail@bluebox.lotus.com -server https://apps.dailyz.swg.usma.ibm.com -partnum D09TFLL -qty 100 -userbasemail useremail -numusers 30\n");

				System.out.println("Switch: -add_sub");
				System.out
						.println(" [Description] Add a subscription to an org \n");
				System.out
						.println(" [Parameters] -server -custid -partnum -qty \n");
				System.out
						.println(" [Example usage] -add_sub -server https://apps.dailyz.swg.usma.ibm.com -custid 20000248 -partnum D09TFLL -qty 100 \n");

				System.out.println("Switch: -add_user");
				System.out
						.println(" [Description] add a new user to an org using the org id \n");
				System.out
						.println(" [Parameters] -orgid -userbasemail (do not include mail domain) -server -subid \n");
				System.out
						.println(" [Example usage] -add_user -orgid 20000319 -userbasemail testbasemail1 -server https://apps.dailyz.swg.usma.ibm.com -subid 10143 \n");

				System.out.println("Switch: -add_users_by_org_name");
				System.out
						.println(" [Description] add users to an org using the org name. \n");
				System.out
						.println(" [Parameters] -orgname -server -userbasemail (do not include mail domain) -subid \n");
				System.out
						.println(" [Example usage] -add_users_by_org_name -orgname Test_Org_Name -server https://apps.dailyz.swg.usma.ibm.com -userbasemail useremail -subid 10151 \n");

				System.out.println("Switch: -add_users_by_org_name_csv");
				System.out
						.println(" [Description] add users to an org using the org name and user details in csv file. \n");
				System.out
						.println(" [Parameters] -orgname -server -csv (filepath to csv file) -subid \n");
				System.out
						.println(" [Example usage] -add_users_by_org_name -orgname Test_Org_Name -server https://apps.dailyz.swg.usma.ibm.com -csv C://csvfile.csv -subid 10151 \n");

				System.out.println("Switch: -remove_org");
				System.out.println(" [Description] removes an organistion. \n");
				System.out.println(" [Parameters] -orgname -server \n");
				System.out
						.println(" [Example usage] -remove_org -orgname Test_Org_Name -server https://apps.dailyz.swg.usma.ibm.com \n");
				System.out
						.println(" [Note] While this will remove the organisation the data associated with it may persist in the database \n");

				System.out.println("Switch: -remove_subscriber");
				System.out
						.println(" [Description] removes a subscriber from an org by their email address \n");
				System.out
						.println(" [Parameters] -subscriber_email -server \n");
				System.out
						.println(" [Example usage] -remove_subscriber -subscriber_email test_email@bluebox.lotus.com -server https://apps.dailyz.swg.usma.ibm.com \n");
				System.out
						.println(" [Note] While this will remove the subscriber the data associated with it may persist in the database \n");

				System.out.println("");

			} else {
				logOut(bw, "No options specified \n");
				System.out.println("No options specified");
			}
			format3 = new SimpleDateFormat("HHmmss").format(now);
			bw.write(format3);
			// Cleanup
			logOut(bw,
					"********************************************************************************************************************************************************* \n");
			bw.close();
		} catch (Exception e) {
			logOut(bw,
					"********************************************************************************************************************************************************* \n");
			bw.close();
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * Use the BSS REST API call to add a new subscription to an organisation
	 * 
	 * @param partNum
	 *            - part number for the subscription See here for part numbers
	 *            <a href=
	 *            'https://w3-connections.ibm.com/wikis/home?lang=en-us#!/wiki/Wbd869a7f61e3_4d65_9738_02aef0370e5d/page/PPA%20orders%20%20-%20part%20numbers'>Link<
	 *            / a >
	 * @param quantity
	 *            - number of seats in subscription
	 * @param customerID
	 *            - the organisation id
	 * @param server
	 *            - the https path to the server
	 * @throws Exception
	 */
	private static String addPart(String partNum, String quantity,
			String customerID, String server, String csguser, String csgpassword)
			throws Exception {

		if (customerID.isEmpty()) {
			throw new Exception("The customer ID is empty");
		}

		HttpClient _http = new org.apache.commons.httpclient.HttpClient();

		File file = new File("addsub.json");

		StringBuilder text = new StringBuilder();
		BufferedReader br = null;

		try {
			br = new BufferedReader(new FileReader(file));
			String line;

			while ((line = br.readLine()) != null) {
				text.append(line.trim());
			}
		} catch (IOException e) {
			// do exception handling
		} finally {
			try {
				br.close();
			} catch (Exception e) {
			}
		}
		System.out.println(partNum);
		System.out.println("Printing out trimmed json " + text);
		String jsonStr = text.toString();
		jsonStr = jsonStr.replace("partplaceholder", partNum);
		jsonStr = jsonStr.replace("customeridholder", customerID);
		jsonStr = jsonStr.replace("quantity", String.valueOf(quantity));
		System.out.println("Printing out replaced json " + jsonStr);
		AuthScope _aScope;
		Credentials _credentials;

		// Create a trust manager that does not validate certificate chains
		createTrustMgr();

		_aScope = new AuthScope(server, AuthScope.ANY_PORT,
				AuthScope.ANY_REALM, "ANY");
		_credentials = new UsernamePasswordCredentials(csguser, csgpassword);

		_http.getState().setCredentials(_aScope, _credentials);
		String auth = _http.getState().getCredentials(_aScope).toString();
		String auth_encoded = new String(Base64.encodeBase64(auth.getBytes()));
		HttpClientParams _params;
		_params = _http.getParams();

		PostMethod method = new PostMethod(server
				+ "/api/bss/resource/subscription");
		method.setFollowRedirects(false);

		method.setRequestBody(jsonStr);
		method.setRequestHeader("Content-Type", "application/json");
		method.setRequestHeader("Authorization", "Basic " + auth_encoded);
		method.setRequestHeader("charset", "utf-8");

		// Execute
		try {
			_http.executeMethod(method);
		} catch (HttpException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		System.out.println(method.getStatusCode() + " response code");
		String response3 = "";
		try {
			response3 = method.getResponseBodyAsString();
		} catch (IOException e1) {

			e1.printStackTrace();
		}
		System.out.println(response3);

		while (method.getStatusCode() == 302) {

			String response2 = "";
			try {
				response2 = method.getResponseBodyAsString();
			} catch (IOException e1) {

				e1.printStackTrace();
			}
			System.out.println(response2);
			Header[] headers = method.getRequestHeaders();
			method.releaseConnection();

			method = new PostMethod(server + "/api/bss/resource/subscription");
			// method.setFollowRedirects(true);

			for (int x = 0; x <= headers.length - 1; x++)
				method.setRequestHeader(headers[x]);

			// Execute POST again.
			try {
				_http.executeMethod(method);
			} catch (HttpException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		String partId = "";
		int code = method.getStatusCode();// != 204
		System.out.println("STATUS: " + code);
		String response = "";
		try {
			response = method.getResponseBodyAsString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		method.releaseConnection();
		if (code == 200) {
			System.out.println(response);
		}
		if (code == 404) {
			System.out.println(response);
			throw new Exception(
					"404 returned, check response above for error details");
		}

		String[] temp = response.split("\\[");
		String[] temp1 = temp[1].split(",");
		String[] temp2 = temp1[0].split(":");
		System.out.println(temp2[1].trim());
		return temp2[1].trim();

	}

	/**
	 * 
	 * Use the BSS REST API call to add a new organisation
	 * 
	 * @param orgName
	 *            - display name for the organisation
	 * @param email
	 *            - email of the new organisation administrator
	 * @param serverAddress
	 *            - https address of the server
	 * @return
	 * @throws Exception
	 */
	public static String addOrg(String orgName, String email,
			String serverAddress, String csguser, String csgpassword)
			throws Exception {
		System.out.println("Adding org named " + orgName + " to "
				+ serverAddress);
		HttpClient _http = new org.apache.commons.httpclient.HttpClient();

		File file = new File("addOrg.json");
		StringBuilder text = new StringBuilder();
		BufferedReader br = null;

		try {
			br = new BufferedReader(new FileReader(file));
			String line;

			while ((line = br.readLine()) != null) {
				text.append(line.trim());
			}
		} catch (IOException e) {
			// do exception handling
		} finally {
			try {
				br.close();
			} catch (Exception e) {
			}
		}

		System.out.println("Printing out trimmed json " + text);
		String jsonStr = text.toString();
		jsonStr = jsonStr.replace("orgnameplaceholder", orgName);
		jsonStr = jsonStr.replace("email_placeholder", email);
		System.out.println("Printing out replaced json " + jsonStr);
		AuthScope _aScope;
		Credentials _credentials;

		// Create a trust manager that does not validate certificate chains
		createTrustMgr();

		_aScope = new AuthScope(serverAddress, AuthScope.ANY_PORT,
				AuthScope.ANY_REALM, "ANY");
		_credentials = new UsernamePasswordCredentials(csguser, csgpassword);

		_http.getState().setCredentials(_aScope, _credentials);
		String auth = _http.getState().getCredentials(_aScope).toString();
		String auth_encoded = new String(Base64.encodeBase64(auth.getBytes()));
		HttpClientParams _params;
		_params = _http.getParams();

		PostMethod method = new PostMethod(serverAddress
				+ "/api/bss/resource/customer");
		method.setFollowRedirects(false);

		method.setRequestBody(jsonStr);
		method.setRequestHeader("Content-Type", "application/json");
		method.setRequestHeader("Authorization", "Basic " + auth_encoded);
		method.setRequestHeader("charset", "utf-8");

		// Execute
		try {
			_http.executeMethod(method);
		} catch (HttpException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		System.out.println(method.getStatusCode() + " response code");
		String response3 = "";
		try {
			response3 = method.getResponseBodyAsString();
		} catch (IOException e1) {

			e1.printStackTrace();
		}
		// System.out.println(response3);

		while (method.getStatusCode() == 302) {

			String response2 = "";
			try {
				response2 = method.getResponseBodyAsString();
			} catch (IOException e1) {

				e1.printStackTrace();
			}
			System.out.println(response2);
			Header[] headers = method.getRequestHeaders();
			method.releaseConnection();

			method = new PostMethod(serverAddress
					+ "/api/bss/resource/customer");
			// method.setFollowRedirects(true);

			for (int x = 0; x <= headers.length - 1; x++)
				method.setRequestHeader(headers[x]);

			// Execute POST again.
			try {
				_http.executeMethod(method);
			} catch (HttpException e) {

				e.printStackTrace();
			} catch (IOException e) {

				e.printStackTrace();
			}
		}
		String custId = "";
		int code = method.getStatusCode();// != 204
		System.out.println("STATUS: " + code);
		String response = "";
		try {
			response = method.getResponseBodyAsString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		method.releaseConnection();
		if (code == 400) {
			System.out.println(response);
			throw new Exception("Error creating the org " + orgName
					+ " see response above for error details");
		}
		if (code == 200) {
			System.out.println(response);
			int start = response.indexOf("Long") + 6;
			int end = start + 8;
			System.out.println("Customer ID is "
					+ response.substring(start, end));
			custId = response.substring(start, end);
		}
		return custId;
	}

	public static String activateAccount(String email) {

		HttpClient _http = new org.apache.commons.httpclient.HttpClient();

		AuthScope _aScope;
		Credentials _credentials;
		email = email.toLowerCase();
		// Create a trust manager that does not validate certificate chains
		createTrustMgr();

		_aScope = new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT,
				AuthScope.ANY_REALM, "ANY");

		HttpClientParams _params;
		_params = _http.getParams();
//		String twilString = "http://llnsvtsmtp.swg.usma.ibm.com/getTokenBlueboxSetPassword.php?mail="
//		+ email;
		String twilString = "http://llnsvtsmtp.swg.usma.ibm.com/getTokenBlueboxPassPassword.php?mail="
		+ email+"&pass=passw0rd";
		GetMethod method = new GetMethod(twilString);
		method.setFollowRedirects(false);
		method.setRequestHeader("charset", "utf-8");

		// Execute

		try {
			_http.executeMethod(method);
		} catch (HttpException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		System.out.println(method.getStatusCode() + " response code");
		String response3 = "";
		int code = method.getStatusCode();
		try {
			response3 = method.getResponseBodyAsString();
		} catch (IOException e1) {

			e1.printStackTrace();
		}
		System.out.println("LOOK HERE ------>" + response3);

		method.releaseConnection();
		if (code == 200) {
			System.out.println(response3);

		} else
			System.out.println("LOOK HERE ------>" + code + "\n" + response3);
		return "";
	}

	public static boolean retrieveMail(String serverUrl, String email,
			String expectedStr) throws IOException {

		Credentials creds = null;
		String baseUrl = null;
		String jsonUrl = "/bluebox/rest/json/inbox/";
		String jsonEndUrl = "/NORMAL/";

		// create a singular HttpClient object
		HttpClient client = new HttpClient();

		// establish a connection within 5 seconds
		client.getHttpConnectionManager().getParams()
				.setConnectionTimeout(5000);

		String url = serverUrl + jsonUrl + email + jsonEndUrl;
		System.out.println("Retrieving mails from " + serverUrl + " for user "
				+ email);
		System.out.println(url);

		GetMethod method = null;

		// String url =
		// "http://bluebox.lotus.com/bluebox/rest/json/inbox/liamwalsh03@bluebox.lotus.com/NORMAL/";

		// create a method object
		method = new GetMethod(url);
		method.setFollowRedirects(true);

		// execute the method
		String responseBody = null;
		try {
			client.executeMethod(method);
			responseBody = method.getResponseBodyAsString();
		} catch (HttpException he) {
			System.err.println("Http error connecting to '" + url + "'");
			System.err.println(he.getMessage());
			System.exit(-4);
		} catch (IOException ioe) {
			System.err.println("Unable to connect to '" + url + "'");
			System.exit(-3);
		}

		// write out the request headers
		System.out.println("*** Request ***");
		System.out.println("Request Path: " + method.getPath());
		System.out.println("Request Query: " + method.getQueryString());
		Header[] requestHeaders = method.getRequestHeaders();
		for (int i = 0; i < requestHeaders.length; i++) {
			System.out.println("" + requestHeaders[i]);
		}

		// write out the response headers
		System.out.println("*** Response ***");
		System.out.println("Status Line: " + method.getStatusLine());
		Header[] responseHeaders = method.getResponseHeaders();
		for (int i = 0; i < responseHeaders.length; i++) {
			System.out.println("" + responseHeaders[i]);
		}

		// write out the response body
		System.out.println("*** Response Body ***");
		System.out.println(responseBody);

		// clean up the connection resources
		method.releaseConnection();
		System.out.println("Connection released");
		// System.exit(0);
		if (responseBody.contains(expectedStr)) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * Use the BSS REST API to add a new user to an organisation
	 * 
	 * @param orgId
	 *            - the organisation ID
	 * @param email
	 *            - the user to be created's email address in lowercase
	 * @param serverAddress
	 *            - the deployment address
	 * @return
	 */
	public static String addUser(String orgId, String email,
			String serverAddress, String csguser, String csgpassword,
			String firstname, String lastname) {

		System.out.println("Adding user to " + serverAddress);
		System.out.println("User email is " + email);

		HttpClient _http = new org.apache.commons.httpclient.HttpClient();

		File file = new File("adduser.json");
		StringBuilder text = new StringBuilder();
		BufferedReader br = null;

		try {
			br = new BufferedReader(new FileReader(file));
			String line;

			while ((line = br.readLine()) != null) {
				text.append(line.trim());
			}
		} catch (IOException e) {
			// do exception handling
		} finally {
			try {
				br.close();
			} catch (Exception e) {
			}
		}

		System.out.println("Printing out trimmed json " + text);
		String jsonStr = text.toString();
		jsonStr = jsonStr.replace("orgnameplaceholder", orgId);
		jsonStr = jsonStr.replace("email_placeholder", email);
		jsonStr = jsonStr.replace("first_name_placeholder", firstname);
		jsonStr = jsonStr.replace("last_name_placeholder", lastname);
		System.out.println("Printing out replaced json " + jsonStr);
		AuthScope _aScope;
		Credentials _credentials;

		// Create a trust manager that does not validate certificate chains
		createTrustMgr();

		_aScope = new AuthScope(serverAddress, AuthScope.ANY_PORT,
				AuthScope.ANY_REALM, "ANY");
		_credentials = new UsernamePasswordCredentials(csguser, csgpassword);

		_http.getState().setCredentials(_aScope, _credentials);
		String auth = _http.getState().getCredentials(_aScope).toString();
		String auth_encoded = new String(Base64.encodeBase64(auth.getBytes()));
		HttpClientParams _params;
		_params = _http.getParams();

		PostMethod method = new PostMethod(serverAddress
				+ "/api/bss/resource/subscriber");
		method.setFollowRedirects(false);

		method.setRequestBody(jsonStr);
		method.setRequestHeader("Content-Type", "application/json");
		method.setRequestHeader("Authorization", "Basic " + auth_encoded);
		method.setRequestHeader("charset", "utf-8");

		// Execute
		try {
			_http.executeMethod(method);
		} catch (HttpException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		System.out.println(method.getStatusCode() + " response code");
		String response3 = "";
		try {
			response3 = method.getResponseBodyAsString();
		} catch (IOException e1) {

			e1.printStackTrace();
		}
		System.out.println(response3);

		while (method.getStatusCode() == 302) {

			String response2 = "";
			try {
				response2 = method.getResponseBodyAsString();
			} catch (IOException e1) {

				e1.printStackTrace();
			}
			System.out.println(response2);
			Header[] headers = method.getRequestHeaders();
			method.releaseConnection();

			method = new PostMethod(serverAddress
					+ "/api/bss/resource/subscriber");
			// method.setFollowRedirects(true);

			for (int x = 0; x <= headers.length - 1; x++)
				method.setRequestHeader(headers[x]);

			// Execute POST again.
			try {
				_http.executeMethod(method);
			} catch (HttpException e) {

				e.printStackTrace();
			} catch (IOException e) {

				e.printStackTrace();
			}
		}
		String custId = "";
		int code = method.getStatusCode();// != 204
		System.out.println("STATUS: " + code);
		String response = "";
		try {
			response = method.getResponseBodyAsString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		method.releaseConnection();
		if (code == 201) {
			System.out.println(response);
			int start = response.indexOf("Long") + 6;
			int end = start + 8;
			System.out.println("Subscriber ID is "
					+ response.substring(start, end));
			custId = response.substring(start, end);
		}
		return custId;
	}

	/**
	 * 
	 * Add a subscription to a users account using the BSS REST API
	 * 
	 * @param subscriberId
	 *            - the id of the user
	 * @param subscriptionId
	 *            - the subscription id for the part number
	 * @param server
	 *            - deployment name (use https)
	 * @param csguser
	 *            - CSG email
	 * @param csgpassword
	 *            - CSG password
	 * @throws Exception
	 */
	private static void addSubscriberSub(String subscriberId,
			String subscriptionId, String server, String csguser,
			String csgpassword) throws Exception {

		HttpClient _http = new org.apache.commons.httpclient.HttpClient();

		if (subscriptionId.isEmpty()) {
			throw new Exception("No subscription id was provided");
		}

		System.out.println(subscriberId);
		System.out.println(subscriptionId);

		AuthScope _aScope;
		Credentials _credentials;

		// Create a trust manager that does not validate certificate chains
		createTrustMgr();

		_aScope = new AuthScope(server, AuthScope.ANY_PORT,
				AuthScope.ANY_REALM, "ANY");
		_credentials = new UsernamePasswordCredentials(csguser, csgpassword);

		_http.getState().setCredentials(_aScope, _credentials);
		String auth = _http.getState().getCredentials(_aScope).toString();
		String auth_encoded = new String(Base64.encodeBase64(auth.getBytes()));
		HttpClientParams _params;
		_params = _http.getParams();

		PostMethod method = new PostMethod(server
				+ "/api/bss/resource/subscriber/" + subscriberId
				+ "/subscription/" + subscriptionId);
		method.setFollowRedirects(false);

		method.setRequestHeader("Content-Type", "application/json");
		method.setRequestHeader("x-operation", "entitleSubscriber");
		method.setRequestHeader("Authorization", "Basic " + auth_encoded);
		method.setRequestHeader("charset", "utf-8");

		// Execute
		try {
			_http.executeMethod(method);
		} catch (HttpException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		System.out.println(method.getStatusCode() + " response code");
		String response3 = "";
		try {
			response3 = method.getResponseBodyAsString();
		} catch (IOException e1) {

			e1.printStackTrace();
		}
		System.out.println(response3);

		while (method.getStatusCode() == 302) {

			String response2 = "";
			try {
				response2 = method.getResponseBodyAsString();
			} catch (IOException e1) {

				e1.printStackTrace();
			}
			System.out.println(response2);
			// Process the retry with the new URL, must also resend the request
			Header[] headers = method.getRequestHeaders();
			method.releaseConnection();

			method = new PostMethod(server + "/api/bss/resource/subscriber/"
					+ subscriberId + "/subscription/" + subscriptionId);

			for (int x = 0; x <= headers.length - 1; x++)
				method.setRequestHeader(headers[x]);

			// Execute POST again.
			try {
				_http.executeMethod(method);
			} catch (HttpException e) {

				e.printStackTrace();
			} catch (IOException e) {

				e.printStackTrace();
			}
		}

		int code = method.getStatusCode();// != 204
		System.out.println("STATUS: " + code);
		String response = "";
		try {
			response = method.getResponseBodyAsString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		method.releaseConnection();
		if (code == 200) {
			System.out.println(response);
		} else {
			System.err.println(response);
			throw new Exception("Invalid response code. Response was : " + code);
		}
	}

	/**
	 * 
	 * Returns the org id as a string
	 * 
	 * @param name
	 *            - name of the org
	 * @param server
	 *            - deployment name
	 * @param csguser
	 *            - CSG email
	 * @param csgpassword
	 *            - CSG password
	 * @return
	 */
	public static String getOrgID(String name, String server, String csguser,
			String csgpassword) {

		HttpClient _http = new org.apache.commons.httpclient.HttpClient();

		AuthScope _aScope;
		Credentials _credentials;
		String org = "";

		// Create a trust manager that does not validate certificate chains
		createTrustMgr();

		_aScope = new AuthScope(server, AuthScope.ANY_PORT,
				AuthScope.ANY_REALM, "ANY");
		_credentials = new UsernamePasswordCredentials(csguser, csgpassword);

		_http.getState().setCredentials(_aScope, _credentials);
		String auth = _http.getState().getCredentials(_aScope).toString();
		String auth_encoded = new String(Base64.encodeBase64(auth.getBytes()));
		HttpClientParams _params;
		_params = _http.getParams();
		String orgsID = server
				+ "/api/bss/resource/customer?_namedQuery=getCustomerByOrgName&orgName="
				+ name;
		GetMethod method = new GetMethod(orgsID);
		method.setFollowRedirects(false);
		method.setRequestHeader("Authorization", "Basic " + auth_encoded);
		method.setRequestHeader("charset", "utf-8");

		// Execute

		try {
			_http.executeMethod(method);
		} catch (HttpException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		System.out.println(method.getStatusCode() + " response code");
		String response3 = "";
		int code = method.getStatusCode();
		try {
			response3 = method.getResponseBodyAsString();
		} catch (IOException e1) {

			e1.printStackTrace();
		}
		// System.out.println(response3);

		method.releaseConnection();
		if (code == 200) {

			response3 = response3.trim();
			try {
				JsonObject temp = new JsonParser().parse(response3).getAsJsonObject();
				JsonArray list = temp.getAsJsonArray("List");
				int orgId = 0;

				for (int i = 0; i < list.size(); ++i) {
					JsonObject rec = list.get(i).getAsJsonObject();
					orgId = rec.getAsJsonPrimitive("RootCustomerId").getAsInt();
					System.out.println(name + "'s id number is " + orgId);
					org = Integer.toString(orgId);
				}
			} catch (JsonParseException e) {

				e.printStackTrace();
			}
		}
		return org;
	}

	private static void logOut(BufferedWriter b, String text)
			throws IOException {
		b.newLine();
		b.write(text);
		b.newLine();
	}

	public static void createTrustMgr() {
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(X509Certificate[] certs,
					String authType) {
			}

			public void checkServerTrusted(X509Certificate[] certs,
					String authType) {
			}
		} };

		// Install the all-trusting trust manager
		try {
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, trustAllCerts, new SecureRandom());
			HttpsURLConnection
					.setDefaultSSLSocketFactory(sc.getSocketFactory());
			SSLContext.setDefault(sc);
		} catch (Exception e) {
			;
		}
	}

	/**
	 * 
	 * Outputs a csv file containing the list of created users
	 * 
	 * @param firstname
	 *            - firstname of the users
	 * @param lastname
	 *            - lastname of the users
	 * @param useremail
	 *            - base email for each user
	 * @param numusers
	 *            - number of users to output to csv
	 */
	public static void generateUsersCSV(String firstname, String lastname,
			String useremail, int numusers) throws IOException {
		FileWriter csvFileWrite = null;
		BufferedWriter csvBuffer = null;

		try {

			File file = new File("users" + ".csv");
			csvFileWrite = new FileWriter(file.getAbsoluteFile());
			csvBuffer = new BufferedWriter(csvFileWrite);
			csvBuffer
					.write("uid,password,display name,email,first name,last name");
			csvBuffer.newLine();
			for (int i = 0; i < numusers; i++) {
				csvBuffer.write(useremail + i + "@bluebox.lotus.com,passw0rd,"
						+ firstname + " " + lastname + i + "," + useremail + i
						+ "@bluebox.lotus.com," + firstname + "," + lastname
						+ i);
				csvBuffer.newLine();
			}

			csvBuffer.close();

		} catch (Exception e) {
			csvBuffer.close();
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * Deletes the specified organisation using the corresponding customer ID
	 * 
	 * @param orgid
	 *            - ID value of the org to be deleted
	 * @param server
	 *            - deployment name
	 * @param csguser
	 *            - CSG email
	 * @param csgpassword
	 *            - CSG password
	 * @throws Exception
	 */
	public static void deleteOrg(String orgid, String server, String csguser,
			String csgpassword) throws Exception {

		if (orgid.isEmpty()) {
			throw new Exception("Customer/Org ID is empty");
		}

		HttpClient _http = new org.apache.commons.httpclient.HttpClient();

		AuthScope _aScope;
		Credentials _credentials;

		// Create a trust manager that does not validate certificate chains
		createTrustMgr();

		_aScope = new AuthScope(server, AuthScope.ANY_PORT,
				AuthScope.ANY_REALM, "ANY");
		_credentials = new UsernamePasswordCredentials(csguser, csgpassword);

		_http.getState().setCredentials(_aScope, _credentials);
		String auth = _http.getState().getCredentials(_aScope).toString();
		String auth_encoded = new String(Base64.encodeBase64(auth.getBytes()));
		HttpClientParams _params;
		_params = _http.getParams();
		String orgsID = server + "/api/bss/resource/customer/" + orgid;
		DeleteMethod method = new DeleteMethod(orgsID);
		method.setFollowRedirects(false);
		method.setRequestHeader("Authorization", "Basic " + auth_encoded);
		method.setRequestHeader("charset", "utf-8");

		// Execute

		try {
			_http.executeMethod(method);
		} catch (HttpException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		System.out.println(method.getStatusCode() + " response code");
		String response3 = "";
		int code = method.getStatusCode();
		try {
			response3 = method.getResponseBodyAsString();
		} catch (IOException e1) {

			e1.printStackTrace();
		}
		// System.out.println(response3);

		method.releaseConnection();
		if (code == 204) {
			System.out
					.println("The server has reported the deletion was submitted");
		} else
			System.out.println(response3);
	}

	/**
	 * 
	 * Deletes the specified organisation using the corresponding customer ID
	 * 
	 * @param useremail
	 *            - email of the user to lookup
	 * @param server
	 *            - deployment name
	 * @param csguser
	 *            - CSG email
	 * @param csgpassword
	 *            - CSG password
	 * @throws Exception
	 */
	public static String getSubsciberIdByEmail(String useremail, String server,
			String csguser, String csgpassword) throws Exception {

		HttpClient _http = new org.apache.commons.httpclient.HttpClient();

		AuthScope _aScope;
		Credentials _credentials;

		// Create a trust manager that does not validate certificate chains
		createTrustMgr();

		_aScope = new AuthScope(server, AuthScope.ANY_PORT,
				AuthScope.ANY_REALM, "ANY");
		_credentials = new UsernamePasswordCredentials(csguser, csgpassword);

		_http.getState().setCredentials(_aScope, _credentials);
		String auth = _http.getState().getCredentials(_aScope).toString();
		String auth_encoded = new String(Base64.encodeBase64(auth.getBytes()));
		HttpClientParams _params;
		_params = _http.getParams();
		String command = server
				+ "/api/bss/resource/subscriber?_namedQuery=getSubscriberByEmailAddress&emailAddress="
				+ useremail;
		GetMethod method = new GetMethod(command);
		method.setFollowRedirects(false);
		method.setRequestHeader("Authorization", "Basic " + auth_encoded);
		method.setRequestHeader("charset", "utf-8");

		// Execute

		try {
			_http.executeMethod(method);
		} catch (HttpException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		String subscriberId = "";
		System.out.println(method.getStatusCode() + " response code");
		String response3 = "";

		try {
			response3 = method.getResponseBodyAsString();
		} catch (IOException e1) {

			e1.printStackTrace();
		}
		try {
			JsonObject temp = new JsonParser().parse(response3).getAsJsonObject();;
			JsonArray list = temp.getAsJsonArray("List");
			int subId = 0;
			for (int i = 0; i < list.size(); ++i) {
				JsonObject rec = list.get(i).getAsJsonObject();
				subId = rec.getAsJsonPrimitive("Oid").getAsInt();
				System.out.println(useremail + "'s id number is " + subId);
				subscriberId = Integer.toString(subId);
			}
		} catch (JsonParseException e) {

			e.printStackTrace();
		}

		method.releaseConnection();
		System.out.println(response3);
		return subscriberId;
	}

	/**
	 * 
	 * Deletes the specified subscriber using the corresponding subscriber ID
	 * 
	 * @param subscriberId
	 *            - ID value of the subscriber to be deleted
	 * @param server
	 *            - deployment name
	 * @param csguser
	 *            - CSG email
	 * @param csgpassword
	 *            - CSG password
	 * @throws Exception
	 */
	public static void deleteSubscriber(String subscriberId, String server,
			String csguser, String csgpassword) throws Exception {

		if (subscriberId.isEmpty()) {
			throw new Exception("Subscriber ID is empty");
		}

		HttpClient _http = new org.apache.commons.httpclient.HttpClient();

		AuthScope _aScope;
		Credentials _credentials;

		// Create a trust manager that does not validate certificate chains
		createTrustMgr();

		_aScope = new AuthScope(server, AuthScope.ANY_PORT,
				AuthScope.ANY_REALM, "ANY");
		_credentials = new UsernamePasswordCredentials(csguser, csgpassword);

		_http.getState().setCredentials(_aScope, _credentials);
		String auth = _http.getState().getCredentials(_aScope).toString();
		String auth_encoded = new String(Base64.encodeBase64(auth.getBytes()));
		HttpClientParams _params;
		_params = _http.getParams();
		String command = server + "/api/bss/resource/subscriber/"
				+ subscriberId;
		DeleteMethod method = new DeleteMethod(command);
		method.setFollowRedirects(false);
		method.setRequestHeader("Authorization", "Basic " + auth_encoded);
		method.setRequestHeader("charset", "utf-8");

		// Execute

		try {
			_http.executeMethod(method);
		} catch (HttpException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		System.out.println(method.getStatusCode() + " response code");
		String response3 = "";
		int code = method.getStatusCode();
		try {
			response3 = method.getResponseBodyAsString();
		} catch (IOException e1) {

			e1.printStackTrace();
		}
		// System.out.println(response3);

		method.releaseConnection();
		if (code == 204) {
			System.out
					.println("The server has reported the deletion was submitted");
		} else
			System.out.println(response3);
	}

}
