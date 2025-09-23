package com.ibm.smartcloud.metrics.dashboard.core;

import java.io.*;
import java.util.*;
import java.lang.String;
import java.lang.Object;
import java.text.SimpleDateFormat;

import com.ibm.json.java.*;

import org.lightcouch.*;
import org.apache.http.client.config.*;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.*;
import org.apache.http.auth.*;

class VitalsCrawler {
	private static ThreadGroup uploadThreadGroup;
	private static JSONObject excludeFile;
	private static boolean foundExcludes = false;
	/**
	 * Returns the number of active upload threads.
	 * Each active upload thread holds files to be uploaded, which is a draw on RAM.
	 * Thus, this method can be used to limit the total number of upload/parsing threads to ensure that we do not overload the system.
	 * @return the number of active threads
	 */
	public static int activeCount() {
		return uploadThreadGroup.activeCount();
	}
	private static CouchDbClient cloudantClient = new CouchDbClient("properties/cloudant.properties");
	/**
	 * Verifies a particular data point (entry) against the server
	 * This allows for arbitrary requirements such as "exclude file downloads"
	 * @param JSONObject data to be verified
	 * @return boolean the validity of the data
	 */
	public static boolean verify(JSONObject data) {
		boolean valid = true;
		try {
			if (!foundExcludes) {
				excludeFile = JSONObject.parse(cloudantClient.find("!CustomerReturnExclude"));
			}
			Set<String> keys = excludeFile.keySet();
			for (String key : keys) {
				String verify = (String)data.get(key);
				JSONArray rules = (JSONArray)excludeFile.get(key);
				for (Object rule : rules) { // Iterator assumes objects, no clean way to override this.
					if (verify.matches("(.*)"+rule+"(.*)")) {
						valid = false; // This is an exclude file
					}
				}
			}
		} catch (java.io.IOException e) { // Error reading file
		} catch (org.lightcouch.NoDocumentException e) {
			excludeFile = new JSONObject();
			valid = true; // No exclude file exists
		} catch (Exception e) {
			e.printStackTrace();
		}
		foundExcludes = true; // We've found them -- or not, but don't look again.
		return valid;
	}
	/**
	 * A helper method to upload a file to cloudant.
	 * Passed a file, it will attempt to find an older revision, and overwrite it if necessary.
	 * @param JSONObject the file to upload
	 */
	private static void uploadFile(JSONObject file) {
		try {
			JSONObject old = JSONObject.parse(cloudantClient.find((String)file.get("_id")));
			file.put("_rev", old.get("_rev"));
			cloudantClient.update(file);
		} catch (org.lightcouch.NoDocumentException e) {
			try {
				cloudantClient.save(file);
			} catch (Exception e2) {e2.printStackTrace();}
		} catch (Exception e) {e.printStackTrace();}
	}
	/**
	 * A helper method to create the http client currently being used.
	 * Has a custom timeout, which is the primary usage (changing the timeout)
	 * @param int timeout
	 * @param Properties file
	 * @return CloseableHttpClient the generated client
	 */
	private static CloseableHttpClient generateVitalsClient(int timeout, Properties properties) {
		RequestConfig config = RequestConfig.custom()
				.setConnectTimeout(timeout)
				.setConnectionRequestTimeout(timeout)
				.setSocketTimeout(timeout)
				.build();
		String host = properties.getProperty("host");
		String vitalsUsername = properties.getProperty("username");
		String vitalsPassword = properties.getProperty("password");
		BasicCredentialsProvider vitalsProvider = new BasicCredentialsProvider();
		vitalsProvider.setCredentials(new AuthScope(host, -1), (new UsernamePasswordCredentials(vitalsUsername, vitalsPassword)));
		return HttpClientBuilder.create()
				.setDefaultCredentialsProvider(vitalsProvider)
				.setDefaultRequestConfig(config)
				.build();
	}
	/**
	 * Run data parsing and caching for a given day
	 * Command line parameters specify, in order: month, day, hour, and minute
	 * Month is used to deal with the duplicate data we had on the server (briefly)
	 * Day is used to get the name of the access log
	 * 
	 */
	public static void main (String args[]) throws Exception {
		int day = 0;
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss"); // This is the format used in the log files
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT")); // This is our global calendar. It is used to generate timestamps, count through the day, and for selecting ranges for sql queries
		cal.setTime(new Date());
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		try {
			switch (args.length) {
				case 4:
					cal.set(Calendar.MINUTE, Integer.parseInt(args[3]));
				case 3:
					cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(args[2]));
				case 2:
					day = Integer.parseInt(args[1]); // We save this as an integer so that we can compare to it in the for loop.
					cal.set(Calendar.DAY_OF_MONTH, day);
				case 1:
					cal.set(Calendar.MONTH, Integer.parseInt(args[0])-1);
					break;
				case 0: default:
					day = cal.get(Calendar.DAY_OF_MONTH);
			}
		} catch (NumberFormatException e) {
			System.out.println("Please ensure that input values are numbers.");
			System.exit(1);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(2);
		}
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream("properties/VitalsProperties.properties"));
		} catch (Exception e) {e.printStackTrace();}
		String DOWNLOADFROM = properties.getProperty("DOWNLOADFROM");
		String ACCESSLOGPREFIX = properties.getProperty("ACCESSLOGPREFIX");
		uploadThreadGroup = new ThreadGroup("uploadGroup");
		List<JSONObject> toUpload = new ArrayList<JSONObject>();
		int timeout = 35000;
		CloseableHttpClient vitalsClient = generateVitalsClient(timeout, properties);

		String yearAndMonth = cal.get(Calendar.YEAR)+"-"+(cal.get(Calendar.MONTH)+1); // Month is 0-indexed, so we are renaming it. This is used as a keyword.
		try {
			JSONObject monthData = JSONObject.parse(cloudantClient.find("!CachedData"+yearAndMonth)); // Try to get the file
			if (monthData != null) {
				JSONObject userDataList = (JSONObject)monthData.get("userDataList"); // Try to get the user data (we can always reconstruct the ints)
				if (userDataList!=null) {
					VitalsCacher.setMonthData(userDataList);
					JSONArray userDataInts = (JSONArray)monthData.get("userDataInts"); // Try to get the ints
					JSONObject returnCache = JSONObject.parse(cloudantClient.find("!CustomerReturnCache"));
					JSONArray visitCounts = (JSONArray)returnCache.get(yearAndMonth);
					if (userDataInts!=null && visitCounts!=null) {
						if(visitCounts.size()<32){
							VitalsCacher.regenerateMonthData();
						} else{
							VitalsCacher.setMonthDataInts(userDataInts); // Month data exists, as do int counts.
							VitalsCacher.setVisitCounts(visitCounts); 
						}
					} else {
						VitalsCacher.regenerateMonthData(); // Supporting structures don't exist, but we have data. Rebuild structures.
					}
				} else {
					// No user data for the month.
				}
			} else {
				// No data for the month.
			}
		} catch (org.lightcouch.NoDocumentException e) {
			// No data file for the month
		} catch (Exception e) {e.printStackTrace();}
		int increment = 3000; // Number of files to pull from vitals at a time
		int bufferUploadLimit = 30000; // Getting timeouts to cloudant at 100 000+
		for (int segmentNumber = 0; segmentNumber < VitalsCacher.NUMSEGMENTS; segmentNumber++, cal.add(Calendar.SECOND, 1)) { // We add a second here to account for the one removed later.
			String startTime = dateFormat.format(cal.getTime());
			cal.add(Calendar.MINUTE, VitalsCacher.SEGMENTLENGTH);
			cal.add(Calendar.SECOND, -1); // So we get 0:00->14:59, 15:00->29:59
			String endTime = dateFormat.format(cal.getTime());
			String query = "+from+"+ACCESSLOGPREFIX+(day<10?"0":"")+day+"+WHERE+request_time+BETWEEN+'"+startTime+"'+AND+'"+endTime+"'+AND+request_date+=+'"+yearAndMonth+"-"+(day<10?"0":"")+day+"'";
			// We divide the data up into blocks (according to VitalsCacher.SEGMENTLENGTH). However, we can only get so much at a time from VITALs (at least via http requests).
			// So, we run a count(*) first, to set up the limit for the for loop, then we count through by [increment].
			HttpGet vitalsGetCount = new HttpGet(DOWNLOADFROM+"?sql=Select+count(*)"+query);
			try {
				CloseableHttpResponse countResponse = vitalsClient.execute(vitalsGetCount);
				JSONArray rawData = JSONArray.parse(countResponse.getEntity().getContent());
				countResponse.close();
				int rawDataLength = Integer.parseInt((String)((JSONObject)rawData.get(0)).get("count(*)"));
				int lastUpload = 0;
				for (int i=0; i<=rawDataLength; i+=increment) {
					HttpGet vitalsGetData = new HttpGet(DOWNLOADFROM+"?sql=Select+*"+query+"+LIMIT+"+i+","+increment);
					CloseableHttpResponse dataResponse = vitalsClient.execute(vitalsGetData);
					JSONArray data = JSONArray.parse(dataResponse.getEntity().getContent());
					for (int j=0; j<data.size(); j++) {
						toUpload.add(VitalsParser.parseData((JSONObject)data.get(j), segmentNumber));
					}
					if (i-lastUpload>bufferUploadLimit || i>rawDataLength) { // If we hit the upload limit or the end of the block.
						//Thread uploadThread = new Thread(uploadThreadGroup, new CloudantUploader(toUpload, i-lastUpload+1), "docs "+(lastUpload+1)+" to "+(i>rawDataLength?rawDataLength:i)+" within "+startTime+" to "+endTime);
						//uploadThread.start();
						lastUpload = i;
						toUpload = new ArrayList<JSONObject>();
					}
				}
			} catch (java.net.SocketTimeoutException e) { // Failed to get a response from VITALs, likely due to system being busy.
				timeout*=2;
				System.out.println("Increasing timeout to "+timeout/1000+" seconds");
				try {
					vitalsClient.close();
				} catch (Exception e2) {}
				vitalsClient = generateVitalsClient(timeout, properties);
				cal.add(Calendar.MINUTE, -VitalsCacher.SEGMENTLENGTH); // Back up the time and try again.
				segmentNumber--;
				continue;
			} catch (java.io.IOException e) {
				String error = e.getMessage();
				if (error.charAt(22)=='Y') {
					System.out.println("You have an error in your SQL syntax.");
				} else if (error.charAt(22)=='<') {
					System.out.println("Incorrect authorization.");
				} else {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			vitalsClient.close();
		} catch (Exception e) {} // If it fails, nothing else we can do.
		cal.add(Calendar.DAY_OF_MONTH, -1); // To the beginning of the day, since we need to reuse this calendar
		
		// Start doing the caching finalization.
		JSONObject document = VitalsCacher.getEnvironmentValueInts(cal);
		document.put("_id", "!CachedData"+yearAndMonth+"-"+day);
		document.put("isCache", true);
		uploadFile(document);
		document = VitalsCacher.getCustomerIdDataCounts(cal);
		document.put("customerList", VitalsCacher.getCustomerIds());
		document.put("_id", "!CachedDataCustomer"+yearAndMonth+"-"+day);
		document.put("isCache", true);
		uploadFile(document);
		document = new JSONObject();
		document.put("userDataList", VitalsCacher.getMonthData());
		document.put("userDataInts", VitalsCacher.getMonthDataInts());
		document.put("_id", "!CachedData"+yearAndMonth);
		document.put("isCache", true);
		uploadFile(document);
		JSONArray visitCounts = VitalsCacher.getVisitCounts();
		try {
			document = JSONObject.parse(cloudantClient.find("!CustomerReturnCache"));
			document.put(yearAndMonth, visitCounts);
			cloudantClient.update(document);
		} catch (org.lightcouch.NoDocumentException e) { // File does not exist
			document = new JSONObject();
			document.put("_id", "!CustomerReturnCache");
			document.put("isCache", true);
			document.put(yearAndMonth, visitCounts);
			cloudantClient.save(document);
		}
		System.out.println("Unknown app names: "+VitalsCacher.getUnknownServiceList());
	}
}
