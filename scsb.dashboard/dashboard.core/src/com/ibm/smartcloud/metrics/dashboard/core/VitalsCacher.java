package com.ibm.smartcloud.metrics.dashboard.core;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.lang.String;
import java.lang.Object;
import java.text.SimpleDateFormat;

import com.ibm.json.java.*;

import org.lightcouch.*;
import org.apache.http.client.config.*;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.*;
import org.apache.http.auth.*;

public class VitalsCacher {
	final static int SEGMENTLENGTH = 30;
	final static int NUMSEGMENTS = 24*60/SEGMENTLENGTH;
	final static int NUMAPPS = Service.maxAppNumber()+1;
	final static List<String> ENVIRONMENTS = Arrays.asList("E3", "J3", "G3");
	final static int NUMENVS = ENVIRONMENTS.size();
	private static TreeMap<String, Integer> unknownServiceList = new TreeMap<String, Integer>();
	/**
	 * Adds a service (initial part of url path) to the list of unknown services
	 * @param String the name of the service
	 * @return boolean if there was a new service added.
	 */
	public static boolean addUnknownService(String service) {
		Integer count = unknownServiceList.get(service);
		if (count==null) {
			unknownServiceList.put(service, 1);
			return true;
		} else {
			unknownServiceList.put(service, count+1);
			return false;
		}
	}
	/**
	 * Converts the unknown service list to a string to be printed out.
	 * @return String the unknown service list
	 */
	public static String getUnknownServiceList() {
		return unknownServiceList.toString();
	}
	/**
	 * Sets a value at index1, index2 in a double JSONArray.
	 * @param JSONArray the array
	 * @param int first index
	 * @param int second index
	 * @param int value to set
	 */
	private static void setDoubleArray(JSONArray array, int index1, int index2, int value) {
		array.set(index1, ((JSONArray)array.get(index1)).set(index2, value));
	}
	/**
	 * Gets a value at index1, index2 in a double JSONArray.
	 * @param JSONArray the array
	 * @param int the first index
	 * @param int the second index
	 * @return int the value
	 */
	private static int getDoubleArray(JSONArray array, int index1, int index2) {
		return Integer.parseInt(((JSONArray)array.get(index1)).get(index2).toString());
	}
	/**
	 * Increments a value at index1, index 2 in a double JSONArray.
	 * This method is faster than using the combined get and set above.
	 * @param JSONArray the array
	 * @param int the first index
	 * @param int the second index
	 * @param int the increase
	 */
	private static void incDoubleArray(JSONArray array, int index1, int index2, int increment) {
		JSONArray row = (JSONArray)array.get(index1);
		row.set(index2, Integer.parseInt(row.get(index2).toString())+increment);
		array.set(index1, row);
	}
	/**
	 * List of customer IDs. IDs are in the same order as in the data cache below
	 */
	private static List<Long> customerIdList = new ArrayList<Long>();
	/**
	 * Each element in the list is a different customer.
	 * Each entry in the TreeMap is a different user, associated to their app data
	 */
	private static List<TreeMap<String, int[][]>> customerIdDataCache = new ArrayList<TreeMap<String, int[][]>>();
	private static List<int[][][]> customerIdDataCacheInts = new ArrayList<int[][][]>();
	/**
	 * Adds a hit to the customer data for a given time segment, app id, and customer id.
	 * Also updates the customerDataCacheInts structure (unique and non-unique).
	 * @param int the segment number of the day
	 * @param int the application id
	 * @param long the customer id (hashed)
	 * @param String the unique user id (hashed)
	 */
	public static void addCustomerIdValue(int segmentNumber, int appId, long customerId, String uuid) {
		int customerIdIndex = customerIdList.indexOf(customerId);
		int customerIdSetInts[][][]; // These are pulled/created per customer id.
		TreeMap<String, int[][]> customerIdSet;
		if (customerIdIndex==-1) { // A new customer id.
			customerIdIndex = customerIdList.size();
			customerIdList.add(customerId);
			customerIdSet = new TreeMap<String, int[][]>();
			customerIdDataCache.add(customerIdSet);
			customerIdSetInts = new int[2][NUMSEGMENTS+1][NUMAPPS+1];
			customerIdDataCacheInts.add(customerIdSetInts);
		} else {
			customerIdSet = customerIdDataCache.get(customerIdIndex);
			customerIdSetInts = customerIdDataCacheInts.get(customerIdIndex);
		}
		int userData[][] = customerIdSet.get(uuid);
		if (userData==null) {
			userData = new int[NUMSEGMENTS+1][NUMAPPS+1];
			customerIdSetInts[0][0][0]++;
		}
		if (userData[segmentNumber+1][appId+1]==0){
			customerIdSetInts[0][segmentNumber+1][appId+1]++;
		}
		if (userData[segmentNumber+1][0]==0) {
			customerIdSetInts[0][segmentNumber+1][0]++;
		}
		if (userData[0][appId+1]==0) {
			customerIdSetInts[0][0][appId+1]++;
		}
		userData[segmentNumber+1][appId+1]++;
		userData[segmentNumber+1][0]++;
		userData[0][appId+1]++;
		userData[0][0]++;
		customerIdSet.put(uuid, userData);
		customerIdDataCache.set(customerIdIndex, customerIdSet);
		customerIdSetInts[1][segmentNumber+1][appId+1]++;
		customerIdSetInts[1][0][appId+1]++;
		customerIdSetInts[1][segmentNumber+1][0]++;
		customerIdSetInts[1][0][0]++;
		customerIdDataCacheInts.set(customerIdIndex, customerIdSetInts);
	}
	/**
	 * Get an alphabetized list of the customer ids
	 * @return JSONArray the customer ids, in an alphabetized JSONArray
	 */
	public static JSONArray getCustomerIds() {
		TreeSet<Long> sortedCustomerIds = new TreeSet<Long>();
		sortedCustomerIds.addAll(customerIdList);
		JSONArray customerIds = new JSONArray();
		for (int i=0; i<customerIdList.size(); i++) {
			customerIds.add(sortedCustomerIds.pollFirst());
		}
		return customerIds;
	}
	/**
	 * Converts the ArrayList<int [][][]> to an int[][][][] (customer ids are, in fact, ints)
	 * From there, the array can be converted into rave format.
	 * Time segments are generated during the process, which is why a calendar needs to be passed.
	 * @param Calendar a calendar object representing the start date of the data.
	 * @return JSONObject most of the document to be uploaded. Blocks, or rave-style data entries, are associated with their name, and timeSegments are also added to the object.
	 */
	public static JSONObject getCustomerIdDataCounts(Calendar cal) {
		int numCustomerIds = customerIdList.size();
		int customerIdDataCacheIntsArray[][][][] = customerIdDataCacheInts.toArray(new int[NUMSEGMENTS+1][2][numCustomerIds][NUMAPPS+1]);
		JSONArray blocks[] = new JSONArray[4*numCustomerIds+2];
		for (int i=0; i<4*numCustomerIds+2; i++) {
			blocks[i] = new JSONArray();
		}
		JSONArray timeSegments = new JSONArray();
		for (int k=0; k<NUMAPPS+1; k++) {
			for (int j=0; j<numCustomerIds; j++) {
				for (int i=0; i<NUMSEGMENTS+1; i++) {
					if (i!=0 && j==0 && k==0) {// The first iteration of the loop--but not the totals value
						timeSegments.add(cal.getTimeInMillis()/1000);
					}
					for (int l=0; l<4*numCustomerIds+2; l++) { // -2 removes the cases where we have all customerIds and app data
						// l%2==0 (total time) skips if i is not 0 (specific time)
						// l%2==1 (all times) skips if i is 0 (total times)
						if ((l%2==0 && i!=0) || (l%2==1 && i==0)) {
							continue;
						}
						// l%4=0,1 (total apps) skips if k is not 0 (specific app)
						// l%4=2,3 (all apps) skips if k is 0 (total apps)
						if ((l%4<2 && k!=0) || (l%4>1 && k==0)) {
							continue;
						}
						// l>=4*numCustomerIds (all customerIds) does not skip
						// l<4*numCustomerIds (specific customerId) skips if j is not the same as l/4
						if (l<4*numCustomerIds && l/4!=j) {
							continue;
						}
						if (customerIdDataCacheIntsArray[j][1][i][k]==0) { // No data for the segment, omit the emit.
							continue;
						}
						JSONArray dataPoint = new JSONArray();
						dataPoint.add(cal.getTimeInMillis()/1000);
						if (k!=0) { // Add app id if specified
							dataPoint.add(k-1);
						} else if (l>=4*numCustomerIds) { // Add customer id if specified
							dataPoint.add(customerIdList.get(j));
						}
						dataPoint.add(customerIdDataCacheIntsArray[j][1][i][k]); // Listing non-unique first.
						dataPoint.add(customerIdDataCacheIntsArray[j][0][i][k]);
						if (l%4==0 && l!=4*numCustomerIds) { // AllDay and not PerApp or PerCustomer, therefore only a single array is needed
							blocks[l] = dataPoint;
						} else { // otherwise it will be a double array
							blocks[l].add(dataPoint);
						}
					}
					if (i!=0) { // The first iteration of the loop is for totals (all day), which has the same start time as the first segment (the second iteration).
						cal.add(Calendar.MINUTE, SEGMENTLENGTH);
					}
				}
				cal.add(Calendar.DAY_OF_MONTH, -1); // Go back to the beginning of the day.
			}
		}
		JSONObject toReturn = new JSONObject();
		for (int i=0; i<4*numCustomerIds+2; i++) {
			String requestName = "";
			if (i%2==0) {
				requestName += "AllDay";
			} else {
				requestName += "IndividualSegments";
			}
			if (i%4>1) {
				requestName += "PerApp";
			}
			if (i<4*numCustomerIds) { // specific customerId case
				requestName += customerIdList.get(i/4);
			} else {
				requestName += "PerCustomer";
			}
			toReturn.put(requestName, blocks[i]);
		}
		toReturn.put("timeSegments", timeSegments);
		return toReturn;
	}
	/**
	 * Each entry in the treemap is a different user.
	 * The triple array is for time segment, application, and environment, with the value representing number of hits. Entries in the 0th row represent a total for that row.
	 * For example, [0][0][3] represents a total across the day, a total across all apps, and G3 (environment index 2).
	 */
	private static TreeMap<String, int[][][]> dataCache = new TreeMap<String, int[][][]>();
	/**
	 * An integer array associated with the above TreeMap structure. This saves integer values for the number of unique users and total hits.
	 * The structure is similar to the one above, except that the first parameter refers to unique (0) or non-unique (1) hits.
	 */
	private static int dataCacheInts[][][][] = new int[2][NUMSEGMENTS+1][NUMAPPS+1][NUMENVS+1];
	/**
	 * Add a generic hit to the SC servers. As opposed to customerId, this contains per-environment data.
	 * The two structures (environment and customerId) are separate since no customer should be in multiple environments, but just in case.
	 * @param int the segment number of the day
	 * @param int the application id
	 * @param String the environment
	 * @param String the unique user id (hashed)
	 */
	public static void addEnvironmentValue(int segmentNumber, int appId, String environment, String uuid) {
		int envIndex = ENVIRONMENTS.indexOf(environment);
		int userData[][][] = dataCache.get(uuid);
		if (userData == null) {
			userData = new int[NUMSEGMENTS+1][NUMAPPS+1][NUMENVS+1];
			dataCacheInts[0][0][0][0]++;
		}
		// I could not find a more elegant way of doing this. What it is doing is this:
		// For each category that this hit falls into, if it is the first in that category, increase the unique count in the integer structure.
		if (userData[segmentNumber+1][appId+1][envIndex+1]==0) {
			dataCacheInts[0][segmentNumber+1][appId+1][envIndex+1]++;
		}
		if (userData[segmentNumber+1][appId+1][0]==0) {
			dataCacheInts[0][segmentNumber+1][appId+1][0]++;
		}
		if (userData[segmentNumber+1][0][envIndex+1]==0) {
			dataCacheInts[0][segmentNumber+1][0][envIndex+1]++;
		}
		if (userData[0][appId+1][envIndex+1]==0) {
			dataCacheInts[0][0][appId+1][envIndex+1]++;
		}
		if (userData[segmentNumber+1][0][0]==0) {
			dataCacheInts[0][segmentNumber+1][0][0]++;
		}
		if (userData[0][appId+1][0]==0) {
			dataCacheInts[0][0][appId+1][0]++;
		}
		if (userData[0][0][envIndex+1]==0) {
			dataCacheInts[0][0][0][envIndex+1]++;
		}
		userData[segmentNumber+1][appId+1][envIndex+1]++;
		userData[segmentNumber+1][appId+1][0]++;
		userData[segmentNumber+1][0][envIndex+1]++;
		userData[segmentNumber+1][0][0]++;
		userData[0][appId+1][envIndex+1]++;
		userData[0][appId+1][0]++;
		userData[0][0][envIndex+1]++;
		userData[0][0][0]++;
		dataCacheInts[1][segmentNumber+1][appId+1][envIndex+1]++;
		dataCacheInts[1][segmentNumber+1][appId+1][0]++;
		dataCacheInts[1][segmentNumber+1][0][envIndex+1]++;
		dataCacheInts[1][segmentNumber+1][0][0]++;
		dataCacheInts[1][0][appId+1][envIndex+1]++;
		dataCacheInts[1][0][appId+1][0]++;
		dataCacheInts[1][0][0][envIndex+1]++;
		dataCacheInts[1][0][0][0]++;
		dataCache.put(uuid, userData);
	}
	/**
	 * Using the existing environment data strucutre, rebuild the integer array.
	 * Useful in case the integer array gets /lost/.
	 */
	public static void regenerateEnvironmentValueInts() {
		dataCacheInts = new int[2][NUMSEGMENTS+1][NUMAPPS+1][NUMENVS+1];
		for (Object value : dataCache.values()) {
			int valuesArray[][][] = (int[][][])value;
			for (int i=0; i<NUMSEGMENTS+1; i++) {
				for (int j=0; j<NUMAPPS+1; j++) {
					for (int k=0; k<NUMENVS+1; k++) {
						if (valuesArray[i][j][k]!=0) {
							dataCacheInts[0][i][j][k]++;
							dataCacheInts[1][i][j][k]+=valuesArray[i][j][k];
						}
					}
				}
			}
		}
	}
	/**
	 * Converts the array into rave format, which is a double array where the indicies go first, followed by data.
	 * Time segments are generated during the process, which is why a calendar needs to be passed.
	 * @param Calendar a calendar object representing the start date of the data.
	 * @return JSONObject most of the document to be uploaded. Blocks, or rave-style data entries, are associated with their name, and timeSegments are also added to the object.
	 */
	public static JSONObject getEnvironmentValueInts(Calendar cal) {
		int numBlocks = 4*(NUMENVS+2)-2; // NUMENVS+2 (group envs, each env, all envs) *2 (Appdata) *2 (time data) -2 (Appdata and all envs does not work).
		JSONArray blocks[] = new JSONArray[numBlocks];
		for (int i=0; i<numBlocks; i++) {
			blocks[i] = new JSONArray();
		}
		JSONArray timeSegments = new JSONArray();
		for (int j=0; j<NUMAPPS+1; j++) {
			for (int k=0; k<NUMENVS+1; k++) {
				for (int i=0; i<NUMSEGMENTS+1; i++) {
					if (i!=0 && j==0 && k==0) {// The first iteration of the loop--but not the totals value
						timeSegments.add(cal.getTimeInMillis()/1000);
					}
					for (int l=0; l<numBlocks; l++) { // 18 returns total. This avoids the two cases where both the j and k blocks fail to trigger (all env and all apps)
						JSONArray dataPoint = new JSONArray();
						// l%2==0 (total time) skips if i is not 0 (specific time)
						// l%2==1 (all times) skips if i is 0 (total times)
						if ((l%2==0 && i!=0) || (l%2==1 && i==0)) {
							continue;
						}
						// l%4 = 0,1 (total apps) skips if j is not 0 (specific app)
						// l%4 = 2,3 (all apps) skips if j is 0 (total apps)
						if ((l%4<2 && j!=0) || (l%4>1 && j==0)) {
							continue;
						}
						// l/4==0 (total envs) skips if k is not 0 (k is a specific env)
						// l/4==1-3 (specific env) skips if k is not 1, 2, 3 (the same env)
						// l>15 (all envs) skips if k is 0 (total envs)
						if ((l>15 && k==0) || (l<16 && l/4!=k)) {
							continue;
						}
						dataPoint.add(cal.getTimeInMillis()/1000);
						if (j!=0) { // Add app id if specified
							dataPoint.add(j-1);
						} else if (k!=0) { // Add env id if specified
							dataPoint.add(k-1);
						}
						dataPoint.add(dataCacheInts[1][i][j][k]); // Listing non-unique first.
						dataPoint.add(dataCacheInts[0][i][j][k]);
						if (l%4==0 && l<16) { // AllDay and not PerApp or PerEnv, there is only one row, so a single array suffices
							blocks[l] = dataPoint;
						} else { // Otherwise you need a double array
							blocks[l].add(dataPoint);
						}
					}
					if (i!=0) { // The first iteration of the loop is for totals (all day), which has the same start time as the first segment (the second iteration).
						cal.add(Calendar.MINUTE, SEGMENTLENGTH);
					}
				}
				cal.add(Calendar.DAY_OF_MONTH, -1); // Go back to the beginning of the day.
			}
		}
		JSONObject toReturn = new JSONObject();
		for (int i=0; i<numBlocks; i++) {
			String requestName = "";
			if (i%2==0) {
				requestName += "AllDay";
			} else {
				requestName += "IndividualSegments";
			}
			if (i%4>1) {
				requestName += "PerApp";
			}
			switch (i/4) {
				case 0:
					break;
				case 1: case 2: case 3:
					requestName += ENVIRONMENTS.get(i/4-1);
					break;
				case 4:
					requestName += "PerEnv";
					break;
			}
			toReturn.put(requestName, blocks[i]);
		}
		toReturn.put("timeSegments", timeSegments);
		return toReturn;
	}
	private static JSONObject monthData = new JSONObject();
	private static int visitCounts[] = new int[32];
	private static int monthDataInts[][][] = new int[2][NUMENVS+1][NUMAPPS+1];
	/**
	 * Initializes the month data cache with data from earlier in the month.
	 * @param JSONObject the saved month data cache
	 */
	public static void setMonthData(JSONObject data) {
		monthData = data;
	}
	/**
	 * Initializes the month data cache ints with data from earlier in the month.
	 * This converts a JSONArray[JSONArray[JSONArray]] to an int[][][] through obvious means.
	 * @param JSONArray the saved month data cache ints
	 */
	public static void setMonthDataInts(JSONArray dataInts) {
		for (int i=0; i<dataInts.size(); i++) {
			JSONArray count = (JSONArray)dataInts.get(i);
			for (int j=0; j<count.size(); j++) {
				JSONArray env = (JSONArray)count.get(j);
				for (int k=0; k<env.size(); k++) {
					monthDataInts[i][j][k] = Integer.parseInt(env.get(k).toString());
				}
			}
		}
	}
	/**
	 * Initializes the visit counts (customer return cache) with data from earlier in the month.
	 * This converts a JSONArray to an int[] through obvious means.
	 * @param JSONArray the saved visit counts
	 */
	public static void setVisitCounts(JSONArray data) {
		visitCounts = new int[32];
		for (int i=0; i<32; i++) {
			visitCounts[i] = Integer.parseInt(data.get(i).toString());
		}
	}
	/**
	 * Gets the month data to save in a document.
	 * @return JSONObject the month data, to save
	 */
	public static JSONObject getMonthData() {
		return monthData;
	}
	/**
	 * Gets the month data ints to save in a document.
	 * This converts an int[][][] to JSONArray[JSONArray[JSONArray]] through obvious means.
	 * @return JSONArray the month data ints, to save
	 */
	public static JSONArray getMonthDataInts() {
		JSONArray monthDataJSON = new JSONArray();
		for (int i=0; i<2; i++) {
			JSONArray count = new JSONArray();
			for (int j=0; j<NUMENVS+1; j++) {
				JSONArray env = new JSONArray();
				for (int k=0; k<NUMAPPS+1; k++) {
					env.add(monthDataInts[i][j][k]);
				}
				count.add(env);
			}
			monthDataJSON.add(count);
		}
		return monthDataJSON;
	}
	/**
	 * Gets the visit counts (return cache) to save in a document.
	 * This converts an int[] to JSONArray through obvious means.
	 * @return JSONArray the visit counts, to save
	 */
	public static JSONArray getVisitCounts() {
		JSONArray visitCountsJSON = new JSONArray();
		for (int i : visitCounts) {
			visitCountsJSON.add(i);
		}
		return visitCountsJSON;
	}
	/**
	 * Using the existing month data, rebuilds the monthDataInts and visitCounts.
	 * Serves to deal with an edge case where two users updated the data structure and partially overwrote each other
	 * or if one supporting structure got lost, somehow...
	 */
	public static void regenerateMonthData() {
		visitCounts = new int[32];
		for (Object userData : monthData.values()) {
			JSONArray appData = (JSONArray)((JSONObject)userData).get("appData");
			for (int i=0; i<NUMENVS+1; i++) {
				for (int j=0; j<NUMAPPS+1; j++) {
					monthDataInts[0][i][j]++;
					monthDataInts[1][i][j]+=getDoubleArray(appData, i, j);
				}
			}
			Integer userVisits = Integer.parseInt(((JSONObject)userData).get("userVisits").toString());
			visitCounts[0]++;
			visitCounts[Integer.bitCount(userVisits)]++;
		}
	}
	/**
	 * Add a generic hit to the SC servers. This data is similar to environment, just calculated for days and the entire month.
	 * @param int the day of the month
	 * @param int the application id
	 * @param String the environment
	 * @param String the unique user id (hashed)
	 */
	public static void addUserVisit(int day, int appId, String environment, String user) {
		int envIndex = ENVIRONMENTS.indexOf(environment);
		JSONObject userData = (JSONObject)monthData.get(user);
		JSONArray appData;
		Integer userVisits; // We use an Integer here to represent the days of the month that the user has visited. An integer has 32 bytes, and there are 31 days in a month.
		if (userData == null) {
			userData = new JSONObject();
			visitCounts[0]++;
			visitCounts[1]++;
			appData = new JSONArray(NUMENVS+1);
			for (int i=0; i<NUMENVS+1; i++) {
				JSONArray row = new JSONArray(NUMAPPS+1);
				for (int j=0; j<NUMAPPS+1; j++) {
					row.add(0);
				}
				appData.add(row);
			}
			userVisits = 1 << day-1; // Initialize the user visits to the known day.
			monthDataInts[0][0][0]++;
		} else {
			appData = (JSONArray)userData.get("appData");
			userVisits = Integer.parseInt(userData.get("userVisits").toString());
		}
		if (getDoubleArray(appData, envIndex+1, appId+1)==0) {
			monthDataInts[0][envIndex+1][appId+1]++;
		}
		if (getDoubleArray(appData, envIndex+1, 0)==0) {
			monthDataInts[0][envIndex+1][0]++;
		}
		if (getDoubleArray(appData, 0, appId+1)==0) {
			monthDataInts[0][0][appId+1]++;
		}
		monthDataInts[1][0][0]++;
		monthDataInts[1][envIndex+1][0]++;
		monthDataInts[1][0][appId+1]++;
		monthDataInts[1][envIndex+1][appId+1]++;
		incDoubleArray(appData, 0, appId+1, 1);
		incDoubleArray(appData, envIndex+1, 0, 1);
		incDoubleArray(appData, envIndex+1, appId+1, 1);
		userData.put("appData", appData);
		if ((userVisits|(1 << day-1))!=userVisits) { // If the user is already listed for visiting on this day
			int numVisits = Integer.bitCount(userVisits); // The number of days visited
			visitCounts[numVisits]--; // Remove a visitor from the previous count
			visitCounts[numVisits+1]++; // and add to the next count
		}
		userData.put("userVisits", userVisits|(1 << day-1)); // Write that the user has visited this day.
		monthData.put(user, userData);
	}
	public static void main(String[] args){
		CouchDbClient cloudantClient = new CouchDbClient("cloudant.properties");
		String yearAndMonth = "2014-9";
		try{
			JSONObject tempMonthData = JSONObject.parse(cloudantClient.find("!CachedData"+yearAndMonth));
			JSONObject userDataList = (JSONObject) tempMonthData.get("userDataList");
			setMonthData(userDataList);
			regenerateMonthData();
			JSONArray tempVisits = getVisitCounts();
			JSONObject document = JSONObject.parse(cloudantClient.find("!CustomerReturnCache"));
			document.put(yearAndMonth, tempVisits);
			cloudantClient.update(document);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
}