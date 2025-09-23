package com.ibm.smartcloud.metrics.dashboard.core;
import com.ibm.json.java.*;

import java.io.InputStream;

import org.lightcouch.*;

import java.util.*;
import java.util.concurrent.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
/*
 * General Usage Notes:
 * Data rows will always have non-unique values first
 * Use an array of length 0 for date to indicate an all time query
 */
public class CloudantManager {
	//global variables
	private String propertiesFile;//location of properties file for lightcouch client
	private int numApps; //number of apps
	private int numRefs; //number of referrers (typically apps + 1 (external))
	private boolean[] usedApps;
	private ExecutorService myService; // service that manages threads;
	
	//Array to hold integers that are used in calendar methods
	private final static int[] calendarUnits = {Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, 
			Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND, Calendar.MILLISECOND};

	private Future<JSONArray> environmentsFuture;//to hold future when getting environments
	private JSONArray environments;//to hold environments
	private Map<String,String> responses;//used to store responses to save time on queries that have been run before
	private boolean old;//whether or not to use old queries
	private int maxRetries;//max number of retries allowed in CountUnique and CountNonUnique
	private final String IBM_ID = "13401";
	//constructors
	//default number of threads is 100, default value for old is false, default for maxRetries is 10
	//150 or more threads in known to cause problems, recommended value is 125
	public CloudantManager(String propertiesFile){
		this(propertiesFile,100,false,10);
	}
	public CloudantManager(String propertiesFile, int numThreads){
		this(propertiesFile,numThreads,false,10);
	}
	public CloudantManager(String propertiesFile, int numThreads, boolean old){
		this(propertiesFile,numThreads,old,10);
	}
	public CloudantManager(String propertiesFile, int numThreads, boolean old, int maxRetries){
		//set variables
		this.propertiesFile=propertiesFile;
		JSONArray apps = Service.getApplicationNames();
		this.numApps = apps.size();
		this.numRefs = this.numApps+1;
		usedApps = new boolean[numApps];
		for(int i=0;i<numApps;i++){
			usedApps[i]=apps.get(i)!=null;
		}
		this.old =old;
		this.maxRetries = maxRetries;
		this.myService = Executors.newFixedThreadPool(numThreads);//intialize myService
		responses = new HashMap<String,String>();//initialize responses
		//start request to get environments
		//Switch to E3 J3 G3
		environmentsFuture=myService.submit(new CountNonUnique(null,null,"GetPerEnv/AllTime",1));
	}
	//setter and getter methods
	public void setEnvironments(JSONArray environments){
		this.environments = environments;
	}
	public JSONArray getEnvironments(){
		if(environments==null){//if environments has not been initialized
			
			JSONArray rows = new JSONArray();
			environments = new JSONArray();//intialize
			try{
				rows = environmentsFuture.get();//get from future
			} catch(Exception e){System.out.println(e.getMessage());}
			for(int i=0;i<rows.size();i++){
				JSONObject row = (JSONObject) rows.get(i);
				JSONArray key = (JSONArray) row.get("key");
				if(key.get(0)!=null){//add to environments array
					environments.add(key.get(0).toString());
				}
			}
		}
		return environments;
	}
	//update value of environments array by submitting new request
	public void updateEnvironments(){
		environmentsFuture = myService.submit(new CountNonUnique(null,null,"GetPerEnv/AllTime",1));
		environments = null;
	}
	//properties setter and getter
	public void setPropertiesFile(String propertiesFile){
		this.propertiesFile = propertiesFile;
	}
	public String getPropertiesFile(){
		return propertiesFile;
	}
	//numApps setter and getter
	public void setNumApps(int numApps){
		this.numApps = numApps;
	}
	public int getNumApps(){
		return numApps;
	}
	//numRefs setter and getter
	public void setNumRefs(int numRefs){
		this.numRefs = numRefs;
	}
	public int getNumRefs(){
		return numRefs;
	}
	//old setter and getter
	public void setOld(boolean old){
		this.old = old;
	}
	public boolean getOld(){
		return old;
	}
	//myService setter and getter
	public void setMyService(ExecutorService service){
		myService = service;
		
	}
	public ExecutorService getMyService(){
		return myService;
	}
	//method to shutdown myService
	public void shutdownMyService(){
		try{
			myService.shutdown();
		} catch(Exception e){System.out.println(e.getMessage());}
	}
	//method to start myService
	public void startMyService(){
		if(myService.isShutdown()){//only start if shutdown
			myService = Executors.newFixedThreadPool(100);
		}
	}
	//maxRetries setter and getter
	public void setMaxRetries(int maxRetries){
		this.maxRetries = maxRetries;
	}
	public int getMaxRetries(){
		return maxRetries;
	}
	//Helper methods for dealing with dates in various formats
	
	//method to generate times for an overtime query, unit is 0-indexed, list does not include endDate
	private static List<Long> generateSegments(int[] startDate, int endDate[], int unit){
		//convert to calendars for comparisons
		Calendar curDate = toCalendar(startDate,false);
		Calendar endCal = toCalendar(endDate,false);
		//initialize segment array
		List<Long> segments = new ArrayList<Long>();
		while(curDate.compareTo(endCal)<0){//until we reach the endDate
			segments.add(curDate.getTimeInMillis()/1000);//we want the times to be in seconds
			curDate.add(calendarUnits[unit],1);//increment curDate
		}
		return segments;
	}
	//method for converting from an integer array date to seconds since epoch
	private static long toDate(int[] date,boolean hasApp){
		//convert to calendar
		Calendar cal = toCalendar(date,hasApp);
		//divide by 1000 to get seconds
		return cal.getTimeInMillis()/1000;
	}
	//method for converting from a JSONArray date to seconds since epoch
	private static long toDate(JSONArray date,boolean hasApp, boolean hasEnv){
		return toDate(date,hasApp,hasEnv,false);
	}
	private static long toDate(JSONArray date, boolean hasApp, boolean hasEnv,boolean hasCus){
		//convert to calendar
		Calendar cal = toCalendar(date,hasApp,hasEnv,hasCus);
		//divide by 1000 to get seconds
		return cal.getTimeInMillis()/1000;
	}
	//method for converting from an integer array date to a calendar
	private static Calendar toCalendar(int[] date, boolean hasApp){
		//intialize calendar, we always are working in GMT
		GregorianCalendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
		//last unit is the last calendar unit represented in the date array +1
		int lastUnit;
		if(hasApp)//if the date array contains an appId
			lastUnit = date.length-1;//
		else//if the date array does not contain an appId
			lastUnit = date.length;
		
		//for every unit in the calendar
		for(int i=0;i<calendarUnits.length;i++){
			if(i<lastUnit){//if the unit is in the date array
				if(i!=1)//if the unit is not months
					cal.set(calendarUnits[i],date[i]);
				else//months are 0 indexed in java, but we use 1 indexed months
					cal.set(calendarUnits[i],date[i]-1);
			}//if the unit is not in the date array, 0 out the values
			else if(i==2)//if the unit is days, we can't set the value to 0 since days are 1-indexed
				cal.set(calendarUnits[i],1);
			else
				cal.set(calendarUnits[i],0);
		}
		
		return cal;
		
	}
	//method to convert from a JSONArray date to a calendar
	private static Calendar toCalendar(JSONArray date, boolean hasApp){
		return toCalendar(date,hasApp,false,false);//default value of hasEnv is false
	}
	//method to convert from a JSONArray date to a calendar
	private static Calendar toCalendar(JSONArray date,boolean hasApp, boolean hasEnv){
		return toCalendar(date,hasApp,hasEnv,false);
	}
	private static Calendar toCalendar(JSONArray date, boolean hasApp, boolean hasEnv, boolean hasCus){
		int startIndex =0;//index to start at
		if(hasEnv)//if the date array contains an environment
			startIndex=1;
		int[] tempDate = new int[date.size()-startIndex];//temporary integer date array
		int cusInt =0;
		if(hasCus){
			cusInt = 1;
		}
		for(int i=startIndex;i<date.size()-cusInt;i++){//convert to an integer array
			tempDate[i-startIndex] = Integer.parseInt(date.get(i).toString());
		}
		//use integer array toCalendar method
		return toCalendar(tempDate,hasApp);
	}
	
	
	//method to convert from a calendar to an integer array
	private static int[] fromCalendar(Calendar cal, int numUnits){
		int[] date = new int[numUnits];//intialize array of correct size
		for(int i=0;i<numUnits;i++){//copy all values from calendar
			if(i==1)//months are 0 indexed in calendar, but we need them to be 1-indexed
				date[i]=cal.get(calendarUnits[i])+1;
			else
				date[i]=cal.get(calendarUnits[i]);
		}
		return date;
	}

	//method to convert from a calendar to a JSONArray
	private static JSONArray fromCalendarToJSON(Calendar cal, int numUnits, boolean hasEnv){
		JSONArray date = new JSONArray();//initialize JSONArray
		if(hasEnv)//Environment goes first
			date.add("");
		for(int i=0; i<numUnits;i++){//numUnits does not include environment
			if(i==1)//months must be converted from 0 indexed to 1 indexed
				date.add(cal.get(calendarUnits[i])+1);
			else//copy all other values exactly
				date.add(cal.get(calendarUnits[i]));
		}
		return date;
	}
	//method to increment the application at the end of a JSONArray
	//does not modify original, returns new JSONarray
	private static JSONArray incrementApp(JSONArray date){
		return incrementApp(date,false);
	}
	private static JSONArray incrementApp(JSONArray date, boolean hasCus){
		JSONArray newDate = new JSONArray();//create new JSONArray
		int cusInt = 1;
		if(hasCus){
			cusInt = 2;
		}
		for(int i=0; i<date.size()-cusInt;i++){//copy over all values except last
			newDate.add(date.get(i));
		}
		newDate.add(Integer.parseInt(date.get(date.size()-cusInt).toString())+1);//copy over last value, adding one
		if(hasCus){
			newDate.add(date.get(date.size()-1));
		}
		return newDate;
	}
	private static JSONArray increment(JSONArray date, int index, boolean hasApp, boolean hasEnv, boolean hasCus){
		Calendar cal = toCalendar(date, hasApp, hasEnv,hasCus);//Convert to Calendar
		JSONArray newDate = new JSONArray();//create new date
		if(index>=0){//increment specified unit
			cal.add(calendarUnits[index],1);
		}
		newDate = fromCalendarToJSON(cal,index+1,hasEnv);//return to JSONArray from incremented calendar
		if(hasEnv)//put same environment at beginning, if it was there
			newDate.set(0,date.get(0));
		if(hasApp&&hasCus){
			newDate.add(0);
		}
		if(hasCus){
			newDate.add(date.get(date.size()-1));
		}
		return newDate;
	}
	//method to increment the date on a JSONArray date
	//does not modify original, returns new JSONArray
	private static JSONArray increment(JSONArray date, int index, boolean hasApp, boolean hasEnv){
		return increment(date,index,hasApp,hasEnv,false);
	}
	//method to increment the date on an integer array date
	//does not modify original, returns new integer array
	private static int[] increment(int[] date, int index, boolean hasApp){
		Calendar cal = toCalendar(date,hasApp);//convert to calendar
		if(index>=0){//increment calendar
			cal.add(calendarUnits[index], 1);
		}
		if(hasApp){//return date array created from incremented calendar
			int[] newDate = fromCalendar(cal,index+2);
			newDate[index+1]=date[date.length-1];
			return newDate;
		}
		else
			return fromCalendar(cal,index+1);
	}
	//helper method for getting unit string from integer date array
	private static String getUnit(int[] date, boolean hasApp){
		if(hasApp)
			return getUnit(date.length-1);
		else
			return getUnit(date.length);
	}

	//method for getting unit string from number of units
	private static String getUnit(int numUnits){
		if(numUnits==0)
			return "AllTime";
		else if(numUnits==1)
			return "Year";
		else if(numUnits==2)
			return "Month";
		else if(numUnits==3)
			return "Day";
		else
			return "Hour";
	}
	
	//helper method for getting doc from cloudant
	private JSONObject getDoc(int[] date,boolean customer){//date should have length 3
		CouchDbClient client = new CouchDbClient(propertiesFile);//initialize client
		JSONObject doc = null;
		String docId="";
		if(date.length!=3){//if bad date
			docId ="!CustomerReturnCache";
		} else { 
			String customerString="";
			if(customer){
				customerString = "Customer";
			}
			docId = "!CachedData"+customerString+date[0]+"-"+date[1]+"-"+date[2];//create docId
		}
		int index =0;//track number of tries
		while(true){//to allow for retries
			index++;//increment tries
			try{
				InputStream instream;
				doc = JSONObject.parse(instream=client.find(docId));
				instream.close();//release immmediately
				break;
			} catch(NoDocumentException e){
				return null;
			} catch(Exception e){
				System.out.println(e.getMessage());
			}
			if(index>=maxRetries){
				break;
			}
		}
		client.shutdown();
		return doc;
	}
	private static List<Long> merge(List<Long> list1, List<Long> list2){
		List<Long> mergedList = new ArrayList<Long>();
		int index1=0;
		int index2=0;
		while(index1<list1.size()||index2<list2.size()){
			if(list1.get(index1)<list2.get(index2)){
				mergedList.add(list1.get(index1));
				index1++;
			}else if(list1.get(index1)>list2.get(index2)){
				mergedList.add(list2.get(index2));
				index2++;
			} else {
				mergedList.add(list1.get(index1));
				index1++;
				index2++;
			}
		}
		return mergedList;
	}
	private static JSONArray toStringList(List<Long> values){
		JSONArray valueStrings = new JSONArray();
		for(int i=0;i<values.size();i++){
			valueStrings.add(Long.toString(values.get(i)));
		}
		return valueStrings;
	}
	private static String toMonthString(long date){
		DateFormat formatter = new SimpleDateFormat("MMMM yyyy");
		formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
		return formatter.format(date*1000);
	}
	//class for counting the number of unique users for a view
	//it works by counting the number of rows returned for a query, 
	//where each row has a key representing one user
	class CountUnique implements Callable<Integer> {
		private Object startkey;//Cloudant start key
		private Object endkey;//Cloudant end key
		private String viewName;//Cloudant viewName, of the form designdocname/view
		private int groupLevel;//Cloudant groupLevel
		private int unique;//integer to store answer
		private int[] uniqueCounts;//experimental/not being used
		private int index;//experimental/not being used
		private boolean insert;//triggers use of uniqueCounts and index
		//constructors
		public CountUnique(Object startkey, Object endkey, String viewName, int groupLevel){
			this.startkey = startkey;
			this.endkey = endkey;
			this.viewName = viewName;
			this.groupLevel = groupLevel;
			this.insert = false;//default value
		}
		public CountUnique(Object startkey, Object endkey, String viewName, int groupLevel, int[] uniqueCounts, int index){
			this.startkey = startkey;
			this.endkey = endkey;
			this.viewName = viewName;
			this.groupLevel = groupLevel;
			this.insert = true;//true when uniqueCoutns and index are specified
			this.uniqueCounts = uniqueCounts;
			this.index = index;
		}
		
		//method called when submitted to executorService
		public Integer call(){
			int tries = 0;//counter
			unique=0;//just in case
			while(true){//allows for retries when JSON parsing error
				try{
					CouchDbClient client = new CouchDbClient(propertiesFile);//new client each request
					View view = client.view(viewName)//
							.stale("ok");
					if(groupLevel>0)//if groupLevel is being used, add group level to query
						view.groupLevel(groupLevel);
					//null startkey or endkey means that it's not being used
					if(startkey!=null)
						view.startKey(startkey);
					if(endkey!=null)
						view.endKey(endkey);
					//declare instream
					InputStream instream;
					//seems to be quickest method to get data parsed
					JSONObject data = JSONObject.parse(instream=view.queryForStream());
					//release resources right away
					instream.close();
					client.shutdown();
					//unique counts is number of rows in data
					JSONArray rows = (JSONArray)data.get("rows");
					unique = rows.size();
					break;//if we had no errors, we exit the loop
				} catch(CouchDbException e){
					System.out.println(e.getMessage());
					e.printStackTrace();
					if(e.getMessage().indexOf("Bad Request")>=0){
						break;//bad request means that there is an error in our start keys or end keys
					}
				} catch(Exception e){//most common error is error parsing json, 
					//usually works if you try the request again, so doesn't exit loop
					System.out.println(e.getMessage());
					e.printStackTrace();
					if(e.getMessage().indexOf("Bad Request")>=0){//these should already be filtered out
						System.out.println("bad");
						break;
					}
					System.out.println(startkey);//print startkey and endkey where the issue occured
					System.out.println(endkey);
				}
				tries++;
				if(tries>=maxRetries){//don't get stuck in an infinite loop
					break;
				}
			}
			if(insert){//experimental/not being used
				uniqueCounts[index] = unique;
			}
			return unique;			
		}
	}
	
	//class for getting the rows from a view query
	class CountNonUnique implements Callable<JSONArray> {
		private Object startkey;//Cloudant startkey
		private Object endkey;//Cloudant endkey
		private String viewName;//Cloudant viewName
		private int groupLevel;//Cloudant groupLevel
		private JSONArray rows;//Holder for returned data
		
		//constructor
		public CountNonUnique(Object startkey, Object endkey, String viewName, int groupLevel){
			this.startkey = startkey;
			this.endkey = endkey;
			this.viewName = viewName;
			this.groupLevel = groupLevel;
		}
		//method called when submitted to ExecutorService
		public JSONArray call(){
			int tries = 0;//counter
			while(true){//loop to retry after errors
				try{//create client
					CouchDbClient client = new CouchDbClient(propertiesFile);
					View view = client.view(viewName)//initialize view
							.stale("ok");
					if(groupLevel>0)//if groupLevel is being used
						view.groupLevel(groupLevel);
					//don't use null endkeys or startkeys
					if(endkey!=null)
						view.endKey(endkey);
					if(startkey!=null)
						view.startKey(startkey);
					InputStream instream;//declare InputStream
					//parsing an InputStream seems to be fastest
					JSONObject data = JSONObject.parse(instream=view.queryForStream());
					//release resources immediately
					instream.close();
					client.shutdown();
					rows = (JSONArray)data.get("rows");//put in rows
					break;
				} catch(CouchDbException e){
					System.out.println(e.getMessage());
					e.printStackTrace();//bad request means that the startkey or endkey was bad, don't retry
					if(e.getMessage().indexOf("Bad Request")>=0){
						break;
					}
				}catch(Exception e){//all other errors seem to go away after retrying
					System.out.println(e.getMessage());
					e.printStackTrace();
					System.out.println(startkey);
					System.out.println(endkey);
				}
				tries++;
				if(tries>=maxRetries){//don't get stuck in infinite loop
					break;
				}
			}
			if(rows==null){//this shouldn't happen, but just in case
				rows = new JSONArray();
			}
			return rows;
		}
	}
	class GetDoc implements Callable<JSONObject>{
		private int[] date;
		private boolean customer;
		public GetDoc(int[] date, boolean customer){
			this.date = date;
			this.customer = customer;
		}
		public JSONObject call(){
			CouchDbClient client = new CouchDbClient(propertiesFile);//initialize client
			JSONObject doc = null;
			String docId="";
			if(date.length==2){//if customerReturn data
				docId = "!CustomerReturnCache"+date[0]+"-"+date[1];
			} else if(date.length!=3){//if bad date
				return null;
			} else { 
				String customerString="";
				if(customer){
					customerString = "Customer";
				}
				docId = "!CachedData"+customerString+date[0]+"-"+date[1]+"-"+date[2];//create docId
			}
			int index =0;//track number of tries
			while(true){//to allow for retries
				index++;//increment tries
				try{
					InputStream instream;
					doc = JSONObject.parse(instream=client.find(docId));
					instream.close();//release immmediately
					break;
				} catch(NoDocumentException e){
					return null;
				} catch(Exception e){
					System.out.println(e.getMessage());
				}
				if(index>=maxRetries){
					break;
				}
			}
			client.shutdown();
			return doc;
		}
	}
	
	//query methods
	//date must have length 2 or 3, i.e. refer to a specific month or day.
	//reload is for if there isn't pre-cached, whether to use data cached in manager
	// good bounds are: 20+, 12-19, 5-11,2-4,1
	public String getCachedCustomerReturnData(int[] date, boolean reload){
		if(date.length!=1){
			return "bad date";
		}
		int[] endDate = increment(date,0,false);
		return getCachedCustomerReturnData(date,endDate,reload);
	}
//	public String getCachedCustomerReturnData(int[] startDate, int[] endDate, boolean reload){
//		int[] buckets = {2,5,12,20};
//		JSONArray bucketStrings =  new JSONArray();
//		bucketStrings.add("Once per month");
//		bucketStrings.add("Once per week");
//		bucketStrings.add("Twice per week");
//		bucketStrings.add("3-4 times a week");
//		bucketStrings.add("Daily");
//		return getCachedCustomerReturnData(startDate,endDate,reload,buckets,bucketStrings);
//	}
	public String getCachedCustomerReturnData(int[] startDate,int[] endDate, boolean reload){
		if((!reload)&&(responses.get("getCachedCustomerReturnData"+toDate(startDate,false)+""+toDate(endDate,false))!=null&&old)){
			return responses.get("getCachedCustomerReturnData"+toDate(startDate,false)+""+toDate(endDate,false));
		}
		List<Long> times = generateSegments(startDate,endDate,1);
		JSONObject doc = getDoc(startDate,false);
		JSONArray months = new JSONArray();
		JSONObject toReturn = new JSONObject();
		JSONArray data = new JSONArray();
		JSONArray totals = new JSONArray();
		int[] curDate = fromCalendar(toCalendar(startDate,false),2);
		if(doc==null){
			return "CLOUDANT ERROR";
		}
		int[] bucketTotals = new int[31];
		for(int j=0;j<times.size();j++){
			months.add(toMonthString(times.get(j)));
			JSONArray counts = (JSONArray) doc.get(curDate[0]+"-"+curDate[1]);
			curDate = increment(curDate,1,false);
			if(counts==null){
				continue;
			}
			for(int i=0;i<31;i++){
				JSONArray entry = new JSONArray();
				entry.add(j);
				entry.add(i+1);
				int count=Integer.parseInt(counts.get(i+1).toString());
				bucketTotals[i]+=count;
				entry.add(count);
				data.add(entry);
			}
			JSONArray totalsEntry = new JSONArray();
			totalsEntry.add(j);
			totalsEntry.add(counts.get(0));
			totals.add(totalsEntry);
		}
		JSONArray pie = new JSONArray();
		int[] buckets = {1,2,5,12,20,32};
		for(int i=0;i<buckets.length-1;i++){
			JSONArray entry = new JSONArray();
			int bucketTotal=0;
			for(int j=buckets[i];j<buckets[i+1];j++){
				bucketTotal+=bucketTotals[j-1];
			}
			entry.add(i);
			entry.add(bucketTotal);
			pie.add(entry);
		}
		JSONArray bucketStrings =  new JSONArray();
		bucketStrings.add("Once per month");
		bucketStrings.add("Once per week");
		bucketStrings.add("Twice per week");
		bucketStrings.add("3-4 times a week");
		bucketStrings.add("Daily");
		toReturn.put("pie", pie);
		toReturn.put("data", data);
		toReturn.put("total", totals);
		toReturn.put("months",months);
		toReturn.put("buckets", bucketStrings);
		responses.put("getCachedCustomerReturnData"+toDate(startDate,false)+""+toDate(endDate,false),toReturn.toString());
		return toReturn.toString();
	}
	
	public String getCachedTotalCountsOverTime(int[] date,String environment,boolean reload){
		if(date.length==3){
			if((!reload)&&(responses.get("getCachedTotalCountsOverTime"+toDate(date,false)+environment)!=null&&old)){
				return responses.get("getCachedTotalCountsOverTime"+toDate(date,false)+environment);
			}
			JSONArray toReturn = new JSONArray();
			JSONObject doc = getDoc(date,false);
			if(doc==null){
				return getTotalCountsOverTime(date,environment,reload);
			}
			if(environment==null){
				toReturn = (JSONArray) doc.get("IndividualSegments");
			} else {
				JSONArray tempData = (JSONArray) doc.get("IndividualSegments"+environment);
				for(int i=0;i<tempData.size();i++){
					JSONArray row = (JSONArray) tempData.get(i);
					row.remove(1);
					toReturn.add(row);
				}
			}
			responses.put("getCachedTotalCountsOverTime"+toDate(date,false)+environment, toReturn.toString());
			return toReturn.toString();
		} else if(date.length==2){
			int[] endDate = increment(date,1,false);
			return getCachedTotalCountsOverTime(date,endDate,environment,reload);
		} else {
			return getTotalCountsOverTime(date,environment,reload);
		}
	}
	public String getCachedTotalCountsOverTime(int[] startDate, int[] endDate,String environment,boolean reload){
		if((!reload)&&(responses.get("getCachedTotalCountsOverTime"+toDate(startDate,false)+""+toDate(endDate,false)+environment)!=null&&old)){
			return responses.get("getCachedTotalCountsOverTime"+toDate(startDate,false)+""+toDate(endDate,false)+environment);
		}
		JSONArray toReturn = new JSONArray();
		int[] curDate = fromCalendar(toCalendar(startDate,false),3);
		List<Long> times = generateSegments(startDate,endDate,2);
		List<Future<JSONObject>> docFutures = new ArrayList<Future<JSONObject>>();
		for(int i=0;i<times.size();i++){
			docFutures.add(myService.submit(new GetDoc(curDate,false)));
			curDate = increment(curDate,2,false);
		}
		for(int i=0;i<times.size();i++){
			JSONObject doc = new JSONObject();
			try{
				doc = docFutures.get(i).get();
			} catch(Exception e){System.out.println(e.getMessage());}
			if(doc!=null){
				if(environment==null){
					toReturn.add((JSONArray)doc.get("AllDay"));
				} else {
					JSONArray data = (JSONArray)doc.get("AllDay"+environment);
					data.remove(1);
					toReturn.add(data);
				}
			} else {
				JSONArray row = new JSONArray();
				row.add(times.get(i));
				row.add(0);
				row.add(0);
				toReturn.add(row);
			}
		}
		responses.put("getCachedTotalCountsOverTime"+toDate(startDate,false)+""+toDate(endDate,false)+environment,toReturn.toString());
		return toReturn.toString();
	}
	public String getCachedCountsOverTime(int[] date,String environment,boolean reload){
		if(date.length==3){
			if((!reload)&&(responses.get("getCachedCountsOverTime"+toDate(date,false)+environment)!=null&&old)){
				return responses.get("getCachedCountsOverTime"+toDate(date,false)+environment);
			}
			JSONObject doc = getDoc(date,false);
			if(doc==null){
				return getCountsOverTime(date,environment,reload);
			}
			JSONObject toReturn = new JSONObject();
			JSONArray tempData;
			if(environment!=null){
				tempData = (JSONArray) doc.get("IndividualSegmentsPerApp"+environment);
			} else {
				tempData= (JSONArray) doc.get("IndividualSegmentsPerApp");
			}
			JSONArray data= new JSONArray();
			for(int i=0;i<numApps;i++){
				if(usedApps[i]){
					data.add(new JSONArray());
				} else {
					data.add(null);
				}
			}
			for(int i=0;i<tempData.size();i++){
				JSONArray row = (JSONArray) tempData.get(i);
				int app = Integer.parseInt(row.get(1).toString());
				if(!usedApps[app]){
					continue;
				}
				JSONArray appData = (JSONArray)data.get(app);
				appData.add(row);
				data.set(app,appData);
			}
			JSONArray times = (JSONArray)doc.get("timeSegments");
			toReturn.put("data",data);
			toReturn.put("numSegments", times.size());//need to 
			responses.put("getCachedCountsOverTime"+toDate(date,false)+environment, toReturn.toString());
			return toReturn.toString();
		} else if(date.length==2){
			return getCachedCountsOverTime(date,increment(date,1,false),environment,reload);
		} else {
			return getCountsOverTime(date,environment,reload);
		}
	}
	public String getCachedCountsOverTime(int[] startDate,int[] endDate,String environment, boolean reload){
		if((!reload)&&(responses.get("getCachedCountsOverTime"+toDate(startDate,false)+""+toDate(endDate,false)+environment)!=null&&old)){
			return responses.get("getCachedCountsOverTime"+toDate(startDate,false)+""+toDate(endDate,false)+environment);
		}
		JSONObject toReturn = new JSONObject();
		int[] curDate = fromCalendar(toCalendar(startDate,false),3);
		List<Long> times = generateSegments(startDate,endDate,2);
		List<Future<JSONObject>> docFutures = new ArrayList<Future<JSONObject>>();
		for(int i=0;i<times.size();i++){
			docFutures.add(myService.submit(new GetDoc(curDate,false)));
			curDate = increment(curDate,2,false);
		}
		JSONArray data = new JSONArray();
		for(int i=0;i<numApps;i++){
			if(usedApps[i]){
				data.add(new JSONArray());
			} else {
				data.add(null);
			}
		}
		for(int i=0;i<times.size();i++){
			JSONObject doc = new JSONObject();
			try{
				doc = docFutures.get(i).get();
			} catch(Exception e){System.out.println(e.getMessage());}
			if(doc==null){
				for(int j=0;j<numApps;j++){
					if(usedApps[j]){
						JSONArray appRows = (JSONArray) data.get(j);
						JSONArray row = new JSONArray();
						row.add(times.get(i));
						row.add(j);
						row.add(0);
						row.add(0);
						appRows.add(row);
						data.set(j,appRows);
					}
				}
			} else{
				JSONArray dayRows;
				if(environment==null){
					dayRows=(JSONArray) doc.get("AllDayPerApp");
				} else{
					dayRows = (JSONArray) doc.get("AllDayPerApp"+environment);
				}
				for(int j=0;j<dayRows.size();j++){
					JSONArray row = (JSONArray)dayRows.get(j);
					int app = Integer.parseInt(row.get(1).toString());
					if(!usedApps[app]){
						continue;
					}
					JSONArray appRows = (JSONArray) data.get(app);
					appRows.add(row);
					data.set(app,appRows);
					
				}
			}
		}
		toReturn.put("data", data);
		toReturn.put("numSegments", times.size());
		responses.put("getCachedCountsOverTime"+toDate(startDate,false)+""+toDate(endDate,false)+environment,toReturn.toString());
		return toReturn.toString();
	}
	public String getCachedEnvCountsOverTime(int[] date, boolean reload){
		if(date.length==3){
			if((!reload)&&(responses.get("getCachedEnvCountsOverTime"+toDate(date,false))!=null&&old)){
				return responses.get("getCachedEnvCountsOverTime"+toDate(date,false));
			}
			JSONObject doc = getDoc(date,false);
			if(doc==null){
				return getEnvCountsOverTime(date,reload);
			}
			JSONObject toReturn = new JSONObject();
			toReturn.put("data", doc.get("IndividualSegmentsPerEnv"));
			toReturn.put("categories",environments);
			JSONArray times = (JSONArray) doc.get("timeSegments");
			toReturn.put("numSegments", times.size());//need to 
			responses.put("getCachedEnvCountsOverTime"+toDate(date,false),toReturn.toString());
			return toReturn.toString();
		} else if(date.length==2){
			return getCachedEnvCountsOverTime(date,increment(date,1,false),reload);
		} else{
			return getEnvCountsOverTime(date,reload);
		}
	}
	public String getCachedEnvCountsOverTime(int[] startDate, int[] endDate, boolean reload){
		if((!reload)&&(responses.get("getCachedEnvCountsOverTime"+toDate(startDate,false)+""+toDate(endDate,false))!=null&&old)){
			return responses.get("getCachedEnvCountsOverTime"+toDate(startDate,false)+""+toDate(endDate,false));
		}
		JSONObject toReturn = new JSONObject();
		int[] curDate = fromCalendar(toCalendar(startDate,false),3);
		List<Long> times = generateSegments(startDate,endDate,2);
		List<Future<JSONObject>> docFutures = new ArrayList<Future<JSONObject>>();
		for(int i=0;i<times.size();i++){
			docFutures.add(myService.submit(new GetDoc(curDate,false)));
			curDate = increment(curDate,2,false);
		}
		JSONArray tempData = new JSONArray();
		for(int i=0;i<times.size();i++){
			JSONObject doc = new JSONObject();
			try{
				doc = docFutures.get(i).get();
			} catch(Exception e){System.out.println(e.getMessage());}
			JSONArray dayRows = new JSONArray();
			if(doc==null){
				for(int j=0;j<environments.size();j++){
					JSONArray row  =new JSONArray();
					row.add(times.get(i));
					row.add(j);
					row.add(0);
					row.add(0);
					dayRows.add(row);
				}
			} else {
				dayRows = (JSONArray) doc.get("AllDayPerEnv");
			}
			tempData.add(dayRows);
		}
		JSONArray data = new JSONArray();
		for(int i=0;i<times.size();i++){
			for(int j=0;j<environments.size();j++){
				data.add(((JSONArray)tempData.get(i)).get(j));
			}
		}
		toReturn.put("data",data);
		toReturn.put("categories",environments);
		toReturn.put("numSegments", times.size());
		responses.put("getCachedEnvCountsOverTime"+toDate(startDate,false)+""+toDate(endDate,false),toReturn.toString());
		return toReturn.toString();
	}
	public String getCachedCustomerCountsOverTime(int[] date, boolean reload){
		return getCachedCustomerCountsOverTime(date,10,false,reload);
	}
	public String getCachedCustomerCountsOverTime(int[] date, int limit,boolean excludeIBM, boolean reload){
		if(date.length==3){
			if((!reload)&&(responses.get("getCachedCustomerCountsOverTime"+toDate(date,false)+"limit"+limit+excludeIBM)!=null&&old)){
				return responses.get("getCachedCustomerCountsOverTime"+toDate(date,false)+"limit"+limit+excludeIBM);
			}
			JSONObject toReturn = new JSONObject();
			JSONObject doc = getDoc(date,true);
			if(doc==null){
				return getCustomerCountsOverTime(date,reload);
			}
			JSONArray tempData = (JSONArray) doc.get("IndividualSegmentsPerCustomer");
			List<Long> customerLongs = (List<Long>) doc.get("customerList");
			List<String> customers = toStringList(customerLongs);
			int[] customerTotals = new int[customers.size()];
			List<Integer> topCus = new ArrayList<Integer>();
			JSONArray dataHolder = new JSONArray();
			for(int i=0;i<customers.size();i++){
				dataHolder.add(new JSONArray());
				JSONArray row = (JSONArray) doc.get("AllDay"+customers.get(i));
				customerTotals[i] = Integer.parseInt(row.get(2).toString());
			}
			JSONArray data = new JSONArray();
			for(int i=0;i<tempData.size();i++){
				JSONArray row = (JSONArray) tempData.get(i);
				Long customerLong = Long.parseLong(row.get(1).toString());				
				int index = customerLongs.indexOf(customerLong);
				JSONArray tempHolder = (JSONArray) dataHolder.get(index);
				if(tempHolder == null){
					tempHolder=new JSONArray();
				}
				row.set(1,index);
				tempHolder.add(row);
				dataHolder.set(index,tempHolder);
			}
			for(int i=0;i<customers.size();i++){
				if(excludeIBM&&customers.get(i).equals(IBM_ID)){
					continue;
				}
				int value = customerTotals[i];
				for(int j=0;j<limit;j++){
					if(j>=topCus.size()){
						topCus.add(i);
						break;
					}
					if(value>customerTotals[topCus.get(j)]){
						if(topCus.size()==limit){
							topCus.remove(limit-1);
						}
						topCus.add(j,i);
						break;
					}
				}
			}
			JSONArray categories = new JSONArray();
			JSONArray times = (JSONArray) doc.get("timeSegments");
			int numSegments = times.size();
			for(int i=0;i<topCus.size();i++){
				int cusIndex = topCus.get(i);
				categories.add(customers.get(cusIndex));
				JSONArray tempHolder = (JSONArray) dataHolder.get(cusIndex);
				int index = 0;
				if(tempHolder ==null){
					continue;
				}
				for(int j=0;j<numSegments;j++){
					JSONArray row = new JSONArray();
					if(index<tempHolder.size()){
						row = (JSONArray) tempHolder.get(index);
						long rowTime = Long.parseLong(row.get(0).toString());
						long curTime = Long.parseLong(times.get(j).toString());
						if(rowTime>curTime){
							row = new JSONArray();
							row.add(curTime);
							row.add(i);
							row.add(0);
							row.add(0);
						} else{
							index++;
						}
						row.set(1,i);
						data.add(row);
					}
				}
			}
			toReturn.put("data",data);
			toReturn.put("numSegments", numSegments);
			toReturn.put("categories",categories);
			toReturn.put("smallestValue", customerTotals[topCus.get(topCus.size()-1)]);
			responses.put("getCachedCustomerCountsOverTime"+toDate(date,false)+"limit"+limit+excludeIBM, toReturn.toString());
			return toReturn.toString();
		} else if(date.length==2){
			int[] endDate = increment(date,1,false);
			return getCachedCustomerCountsOverTime(date,endDate,limit,excludeIBM,reload);
		} else{
			return getCustomerCountsOverTime(date,reload);
		}
	}
	public String getCachedCustomerCountsOverTime(int[] startDate,int[] endDate,boolean excludeIBM,boolean reload){
		return getCachedCustomerCountsOverTime(startDate,endDate,10,excludeIBM,reload);
	}
	public String getCachedCustomerCountsOverTime(int[] startDate, int[] endDate, int limit, boolean excludeIBM, boolean reload){
		if((!reload)&&(responses.get("getCachedCustomerCountsOverTime"+toDate(startDate,false)+""+toDate(endDate,false)+"limit"+limit+excludeIBM)!=null&&old)){
			return responses.get("getCachedCustomerCountsOverTime"+toDate(startDate,false)+""+toDate(endDate,false)+"limit"+limit+excludeIBM);
		}
		List<Long> times = generateSegments(startDate,endDate,2);
		int[] curDate = fromCalendar(toCalendar(startDate,false),3);
		int numSegments = times.size();
		System.out.println(times);
		List<Future<JSONObject>> docFutures = new ArrayList<Future<JSONObject>>();
		for(int i=0;i<numSegments;i++){
			docFutures.add(myService.submit(new GetDoc(curDate,true)));
			curDate = increment(curDate,2,false);
		}
		TreeMap<String, JSONArray> dataHolder = new TreeMap<String,JSONArray>();
		TreeMap<String,Integer> customerTotals = new TreeMap<String,Integer>();
		for(int i=0;i<numSegments;i++){
			JSONObject doc = null;
			try{
				doc = docFutures.get(i).get();
			} catch(Exception e){System.out.println(e.getMessage());}
			if(doc==null){
				continue;
			}
			JSONArray docData = (JSONArray)doc.get("AllDayPerCustomer");
			for(int j=0;j<docData.size();j++){
				JSONArray row =(JSONArray) docData.get(j);
				String curCustomer = row.get(1).toString();
				JSONArray tempData =dataHolder.get(curCustomer);
				int total=0;
				if(customerTotals.get(curCustomer)!=null){
					total = customerTotals.get(curCustomer);
				}
				total+=Integer.parseInt(row.get(3).toString());
				customerTotals.put(curCustomer, total);
				if(tempData==null){
					tempData = new JSONArray();
				}
				tempData.add(row);
				dataHolder.put(curCustomer, tempData);
			}
		}
		List<Map.Entry<String, Integer>> topCus = new ArrayList<Map.Entry<String,Integer>>();
		Set<Map.Entry<String,Integer>> customerSet = customerTotals.entrySet();
		Iterator<Map.Entry<String, Integer>> customerIterator = customerSet.iterator();
		while(customerIterator.hasNext()){
			Map.Entry<String,Integer> customer = customerIterator.next();
			if(customer.getKey().equals(IBM_ID)&&excludeIBM){
				continue;
			}
			int value = customer.getValue();
			for(int j=0;j<limit;j++){
				if(j>=topCus.size()){
					topCus.add(customer);
					break;
				}
				if(value>topCus.get(j).getValue()){
					if(topCus.size()==limit){
						topCus.remove(limit-1);
					}
					topCus.add(j,customer);
					break;
				}
			}
		}
		JSONArray data = new JSONArray();
		JSONArray categories = new JSONArray();
		for(int i=0;i<topCus.size();i++){
			JSONArray tempData = dataHolder.get(topCus.get(i).getKey());
			categories.add(topCus.get(i).getKey());
			int index=0;
			for(int j=0;j<numSegments;j++){
				long curTime = times.get(j);
				JSONArray row = new JSONArray();
				if(index>=tempData.size()){
					row.add(curTime);
					row.add(i);
					row.add(0);
					row.add(0);
					data.add(row);
					continue;
				}
				row = (JSONArray) tempData.get(index);
				long rowTime = Long.parseLong(row.get(0).toString());
				if(rowTime>curTime){
					row = new JSONArray();
					row.add(curTime);
					row.add(i);
					row.add(0);
					row.add(0);
					data.add(row);
					continue;
				}
				row.set(1,i);
				data.add(row);
				index++;
			}
			
		}
		JSONObject toReturn = new JSONObject();
		toReturn.put("data", data);
		toReturn.put("numSegments",numSegments);
		toReturn.put("categories",categories);
		//toReturn.put("smallestValue",customerTotals.get(customers.get(topTenCus.get(topTenCus.size()-1))));
		responses.put("getCachedCustomerCountsOverTime"+toDate(startDate,false)+""+toDate(endDate,false)+"limit"+limit+excludeIBM,toReturn.toString());
		return toReturn.toString();
	}
	public String getCachedCustomerAppCountsOverTime(int[] date, long customer, boolean reload){
		if(date.length==3){
			if((!reload)&&(responses.get("getCachedCustomerAppCountsOverTime"+toDate(date,false)+"Customer"+customer)!=null&&old)){
				return responses.get("getCachedCustomerAppCountsOverTime"+toDate(date,false)+"Customer"+customer);
			}
			JSONObject toReturn = new JSONObject();
			JSONObject doc = getDoc(date,true);
			if(doc==null){
				return getCustomerAppCountsOverTime(date,customer,reload);
			}
			toReturn.put("data",doc.get("IndividualSegmentsPerApp"+customer));
			JSONArray times = (JSONArray) doc.get("timeSegments");
			toReturn.put("numSegments", times.size());
			toReturn.put("categories", getApps());
			responses.put("getCachedCustomerAppCountsOverTime"+toDate(date,false)+"Customer"+customer, toReturn.toString());
			return toReturn.toString();
		} else if(date.length==2){
			return getCachedCustomerAppCountsOverTime(date,increment(date,1,false),customer,reload);
		} else{
			return getCustomerAppCountsOverTime(date,customer,reload);
		}
	}
	public String getCachedCustomerAppCountsOverTime(int[] startDate, int[] endDate, long customer, boolean reload){
		if((!reload)&&(responses.get("getCachedCustomerAppCountsOverTime"+toDate(startDate,false)+""+toDate(endDate,false)+"Customer"+customer)!=null&&old)){
			return responses.get("getCachedCustomerAppCountsOverTime"+toDate(startDate,false)+""+toDate(endDate,false)+"Customer"+customer);
		}
		JSONObject toReturn = new JSONObject();
		List<Long> times = generateSegments(startDate,endDate,2);
		int numSegments = times.size();
		List<Future<JSONObject>> docFutures = new ArrayList<Future<JSONObject>>();
		int[] curDate = fromCalendar(toCalendar(startDate,false),3);
		for(int i=0;i<numSegments;i++){
			docFutures.add(myService.submit(new GetDoc(curDate,true)));
			curDate = increment(curDate,2,false);
		}
		JSONArray data = new JSONArray();
		for(int i=0;i<numSegments;i++){
			JSONObject doc = null;
			JSONArray docData = new JSONArray();
			try{
				doc = docFutures.get(i).get();
			} catch(Exception e){System.out.println(e.getMessage());}
			if(doc==null){
				for(int j=0;j<numApps;j++){
					JSONArray row = new JSONArray();
					row.add(times.get(i));
					row.add(j);
					row.add(0);
					row.add(0);
					data.add(row);
				}
				continue;
			}
			docData = (JSONArray)doc.get("AllDayPerApp"+customer);
			if(docData==null){
				docData = new JSONArray();
				for(int j=0;j<numApps;j++){
					JSONArray row = new JSONArray();
					row.add(times.get(i));
					row.add(j);
					row.add(0);
					row.add(0);
					data.add(row);
				}
				continue;
			}
			for(int j=0;j<docData.size();j++){
				data.add(docData.get(j));
			}
		}
		toReturn.put("data", data);
		toReturn.put("numSegments", numSegments);
		toReturn.put("categories",getApps());
		responses.put("getCachedCustomerAppCountsOverTime"+toDate(startDate,false)+""+toDate(endDate,false)+"Customer"+customer,toReturn.toString());
		return toReturn.toString();
	}
	public String getCachedCustomerTotalCountsOverTime(int[] date, long customer, boolean reload){
		if(date.length==3){
			if((!reload)&&(responses.get("getCachedCustomerTotalCountsOverTime"+toDate(date,false)+"Customer"+customer)!=null&&old)){
				return responses.get("getCachedCustomerTotalCountsOverTime"+toDate(date,false)+"Customer"+customer);
			}
			JSONObject toReturn = new JSONObject();
			JSONObject doc = getDoc(date,true);
			if(doc==null){
				return getCustomerTotalCountsOverTime(date,customer,reload);
			}
			JSONArray data = (JSONArray)doc.get("IndividualSegments"+customer);
			if(data==null){
				return getCustomerTotalCountsOverTime(date,customer,reload);
			}
			toReturn.put("data",data);
			JSONArray times = (JSONArray) doc.get("timeSegments");
			toReturn.put("numSegments", times.size());
			responses.put("getCachedCustomerTotalCountsOverTime"+toDate(date,false)+"Customer"+customer, toReturn.toString());
			return toReturn.toString();
		} else if(date.length==2){
			return getCachedCustomerTotalCountsOverTime(date,increment(date,1,false),customer,reload);
		} else{
			return getCustomerTotalCountsOverTime(date,customer,reload);
		}
	}
	public String getCachedCustomerTotalCountsOverTime(int[] startDate, int[] endDate, long customer, boolean reload){
		if((!reload)&&(responses.get("getCachedCustomerTotalCountsOverTime"+toDate(startDate,false)+""+toDate(endDate,false)+"Customer"+customer)!=null&&old)){
			return responses.get("getCachedCustomerTotalCountsOverTime"+toDate(startDate,false)+""+toDate(endDate,false)+"Customer"+customer);
		}
		JSONObject toReturn = new JSONObject();
		List<Long> times = generateSegments(startDate,endDate,2);
		int numSegments= times.size();
		List<Future<JSONObject>> docFutures = new ArrayList<Future<JSONObject>>();
		int[] curDate = fromCalendar(toCalendar(startDate,false),3);
		for(int i=0;i<numSegments;i++){
			docFutures.add(myService.submit(new GetDoc(curDate,true)));
			curDate = increment(curDate,2,false);
		}
		JSONArray data = new JSONArray();
		for(int i=0;i<numSegments;i++){
			JSONObject doc = null;
			try{
				doc = docFutures.get(i).get();
			} catch(Exception e){System.out.println(e.getMessage());}
			if(doc==null){
				JSONArray row = new JSONArray();
				row.add(times.get(i));
				row.add(0);
				row.add(0);
				data.add(row);
				continue;
			}
			if(doc.get("AllDay"+customer)==null){
				JSONArray row = new JSONArray();
				row.add(times.get(i));
				row.add(0);
				row.add(0);
				data.add(row);
				continue;
			}
			data.add(doc.get("AllDay"+customer));
		}
		toReturn.put("data", data);
		toReturn.put("numSegments", numSegments);
		responses.put("getCachedCustomerTotalCountsOverTime"+toDate(startDate,false)+""+toDate(endDate,false)+"Customer"+customer,toReturn.toString());
		return toReturn.toString();
	}
	//date must have length 3, i.e. refer to a specific day
	public String getCachedCounts(int[] date, String environment, boolean reload){
		if((!reload)&&(responses.get("getCachedCounts"+toDate(date,false)+environment)!=null&&old)){
			return responses.get("getCachedCounts"+toDate(date,false)+environment);
		}
		JSONObject doc = getDoc(date,false);
		if(doc==null){
			return getCounts(date,environment,reload);
		}
		JSONObject toReturn = new JSONObject();
		JSONArray data = new JSONArray();
		if(environment!=null){
			data = (JSONArray) doc.get("AllDayPerApp"+environment);
		} else{
			data = (JSONArray) doc.get("AllDayPerApp");
		}
		for(int i=0;i<data.size();i++){
			JSONArray row = (JSONArray) data.get(i);
			row.remove(0);
			data.set(i,row);
		}
		toReturn.put("data",data);
		toReturn.put("categories",getApps());
		responses.put("getCachedCounts"+toDate(date,false)+environment,toReturn.toString());
		return toReturn.toString();
	}
	//counts over time methods requires startDate and endDate to have length > 0, unit must be non-negative
	
	//methods for getting total unique and non unique users for time segments
	public String getTotalCountsOverTime(int[] date,String environment){//default unit is hour for days, days for months, etc
		//default value of reload is true
		return getTotalCountsOverTime(date,increment(date,date.length-1,false),date.length,environment,true);
	}
	//same as above, with reload specified
	public String getTotalCountsOverTime(int[] date,String environment,boolean reload){
		return getTotalCountsOverTime(date,increment(date,date.length-1,false),date.length,environment,reload);
	}
	//unit is 0 indexed, reload=true always runs cloudant queries, false will get cached data if available
	public String getTotalCountsOverTime(int[] startDate, int[] endDate, int unit,String environment, boolean reload){
		//if the response is already stored, and we want to use it, return that instead
		if((!reload)&&(responses.get("getTotalCountsOverTime"+toDate(startDate,false)+""+toDate(endDate,false)+unit+environment)!=null&&old)){
			return responses.get("getTotalCountsOverTime"+toDate(startDate,false)+""+toDate(endDate,false)+unit+environment);
		}
		JSONArray toReturn = new JSONArray();//initialize toReturn Array
		//some time might be saved by completing non unique query first to check for time segments with no data
		//but the amount of time saved is less the more complete the data we have is
		
		//submit nonunique query
		boolean hasEnv = environment==null;
		int envInt = 0;
		JSONArray firstkey = fromCalendarToJSON(toCalendar(startDate,false),startDate.length,hasEnv);
		JSONArray finalkey = fromCalendarToJSON(toCalendar(endDate,false),endDate.length,hasEnv);
		String type="Get/"+getUnit(unit+1);
		if(hasEnv){
			envInt=1;
			type="GetPerEnv/"+getUnit(unit+1);
			firstkey.set(0,environment);
			finalkey.set(0,environment);
		}
		Future<JSONArray> nonUniqueFuture = myService.submit(new CountNonUnique(firstkey,finalkey,type,unit+1+envInt));
		//initialize list for unique futures
		List<Future<Integer>> uniqueFutures= new ArrayList<Future<Integer>>();
		//get time segments as longs
		List<Long> times = generateSegments(startDate,endDate,unit);
		int numSegs =times.size();
		//create startkey and endkey as new integer arrays
		JSONArray startkey = fromCalendarToJSON(toCalendar(startDate,false),unit+1,hasEnv);
		if(hasEnv){
			startkey.set(0, environment);
		}
		JSONArray endkey = increment(startkey,unit,false,hasEnv);
		for(int i=0;i<numSegs;i++){//add future for each unique count
			uniqueFutures.add(myService.submit(new CountUnique(startkey,endkey,type,unit+3+envInt)));
			startkey = increment(startkey,unit,false,hasEnv);//increment always returns a new array
			endkey = increment(endkey,unit,false,hasEnv);
		}
		//initialize rows
		JSONArray rows = new JSONArray();
		try{
			rows = nonUniqueFuture.get();//get rows from future
		} catch(Exception e){System.out.println(e.getMessage());}
		
		//index to track place in rows
		int index = 0;
		for(int i=0;i<numSegs;i++){
			JSONArray entry = new JSONArray();//entry will be a row in the toReturn array
			entry.add(times.get(i));//add time to entry row
			if(index>=rows.size()){//this means that there is no nonunique data for this time segment
				//so there is no unique data either, so add 0's, then add to toReturn, then continue for loop
				entry.add(0);
				entry.add(0);
				toReturn.add(entry);
				continue;
			}
			//get row and key from rows
			JSONObject row = (JSONObject) rows.get(index);
			JSONArray key = (JSONArray) row.get("key");
			
			if(times.get(i)<toDate(key,false,hasEnv)){//no data for this time segment, don't increment index
				entry.add(0);//add 0's then add to toReturn array
				entry.add(0);
				toReturn.add(entry);
			} else{//if there is data for the time segment
				//add nonunique count from row
				entry.add(Integer.parseInt(row.get("value").toString()));
				int ucount = 0;//just in case there is an error, initialize to 0
				try{//get ucount from future
					ucount = uniqueFutures.get(i).get();
				} catch(Exception e){
					System.out.println(e.getMessage());
				}//add unique count to entry
				entry.add(ucount);
				toReturn.add(entry);//add entry to toReturn array
				index++;//increment index, since we used this row of data
			}
		}
		//add to responses object as string
		responses.put("getTotalCountsOverTime"+toDate(startDate,false)+""+toDate(endDate,false)+unit+environment,toReturn.toString());
		return toReturn.toString();
	}
	
	//getEnvCountsOverTime gets the number of unique and nonunique hits on each environment for each time segment
	public String getEnvCountsOverTime(int[] date){//default value of reload is true
		//default unit is same as getTotalCountsOverTime
		return getEnvCountsOverTime(date,increment(date,date.length-1,false),date.length,true);
	}
	public String getEnvCountsOverTime(int[] date,boolean reload){
		return getEnvCountsOverTime(date,increment(date,date.length-1,false),date.length,reload);
	}
	//unit is 0 indexed, reload is whether or not to use cached data, if it exists
	public String getEnvCountsOverTime(int[] startDate,int[] endDate,int unit,boolean reload){//non inclusive end date
		//check whether cached data exists, and return if we want it
		if((!reload)&&(responses.get("getEnvCountsOverTime"+toDate(startDate,false)+""+toDate(endDate,false)+""+unit)!=null&&old)){
			return responses.get("getEnvCountsOverTime"+toDate(startDate,false)+""+toDate(endDate,false)+""+unit);
		}
		//initialize toReturn object
		JSONObject toReturn = new JSONObject();
		//get environments and put in toReturn object
		JSONArray categories = this.getEnvironments();
		toReturn.put("categories",categories);
		
		//initialize futures lists
		List<Future<JSONArray>> nonUniqueFutures = new ArrayList<Future<JSONArray>>();
		List<Future<Integer>> uniqueFutures = new ArrayList<Future<Integer>>();
		//get time segments as longs
		List<Long> times = generateSegments(startDate,endDate,unit);
		int numSegs = times.size();
		
		
		for(int i = 0; i<categories.size();i++){
			JSONArray startkey = fromCalendarToJSON(toCalendar(startDate,false),unit+1,true);
			JSONArray endkey = fromCalendarToJSON(toCalendar(endDate,false),unit+1,true);
			startkey.set(0,categories.get(i));
			endkey.set(0,categories.get(i));
			nonUniqueFutures.add(myService.submit(new CountNonUnique(startkey,endkey,"GetPerEnv/"+getUnit(unit+1),unit+2)));
			endkey = increment(startkey,unit,false,true);
			for(int j=0;j<numSegs;j++){
				uniqueFutures.add(myService.submit(new CountUnique(startkey,endkey,"GetPerEnv/"+getUnit(unit+1),unit+4)));
				startkey = increment(startkey,unit,false,true);
				endkey = increment(startkey,unit,false,true);
			}
		}
		int index = 0;
		JSONArray data = new JSONArray();
		for(int i=0; i<categories.size();i++){
			JSONArray rows = new JSONArray();
			try{
				rows = nonUniqueFutures.get(i).get();
			} catch(Exception e){System.out.println(e.getMessage());}
			int currentRow = 0;
			for(int j=0;j<numSegs;j++){
				int unique = 0;
				try{
					unique = uniqueFutures.get(index).get();
				} catch(Exception e){System.out.println(e.getMessage());}
				JSONArray entry = new JSONArray();
				entry.add(times.get(j));
				entry.add(i);
				if(currentRow<rows.size()){
					JSONObject row = (JSONObject) rows.get(currentRow);
					JSONArray key = (JSONArray) row.get("key");
					if(toDate(key,false,true)>times.get(j)){
						entry.add(0);
						if(unique!=0){
							System.out.println("Unique>nonUnique at "+times.get(j));
						}
					} else{
						entry.add(row.get("value"));
						currentRow++;
					}
				} else{
					entry.add(0);
				}
				entry.add(unique);
				data.add(entry);
				index++;
			}
		}
		toReturn.put("data", data);
		toReturn.put("numSegments",numSegs);
		responses.put("getEnvCountsOverTime"+toDate(startDate,false)+""+toDate(endDate,false)+""+unit,toReturn.toString());

		return toReturn.toString();
	}
	public String getCountsOverTime(int[] date, String environment){
		return getCountsOverTime(date,increment(date,date.length-1,false),date.length,environment,true);
	}
	public String getCountsOverTime(int[] date, String environment,boolean reload){
		return getCountsOverTime(date,increment(date,date.length-1,false),date.length,environment,reload);
	}
	public String getCountsOverTime(int[] startDate,int[] endDate, int unit, String environment, boolean reload){
		if(startDate.length<1)
			return "Start date must be non-empty";
		if(endDate.length<1)
			return "End date must be non-empty";
		if(unit<0)
			return "Invalid unit "+ unit;
		if((!reload)&&(old&&responses.get("getCountsOverTime"+toDate(startDate,false)+""+toDate(endDate,false)+""+unit+environment)!=null)){
			return responses.get("getCountsOverTime"+toDate(startDate,false)+""+toDate(endDate,false)+""+unit+environment);
		}
		boolean hasEnv = environment!=null;
		int envInt= 0;
		String type;
		if(hasEnv){
			type = "GetPerEnvPerApp/";
			envInt = 1;
		} else
			type = "GetPerApp/";
		
		List<Future<Integer>> uniqueFutures = new ArrayList<Future<Integer>>();
		JSONArray firstkey = fromCalendarToJSON(toCalendar(startDate,false),unit+1,hasEnv);
		JSONArray finalkey = fromCalendarToJSON(toCalendar(endDate,false),unit+1,hasEnv);
		if(hasEnv){
			firstkey.set(0,environment);
			finalkey.set(0, environment);
		}
		Future<JSONArray> nonUniqueFuture = myService.submit(new CountNonUnique(firstkey,finalkey,
				type+getUnit(unit+1),unit+envInt+2));
		List<Long> times = generateSegments(startDate,endDate,unit);
		JSONArray startkey = fromCalendarToJSON(toCalendar(startDate,false),unit+1,hasEnv);
		startkey.add(0);
		if(hasEnv){
			startkey.set(0,environment);
		}
		JSONArray endkey = incrementApp(startkey);
		int numSegments = times.size();
		JSONArray rows = new JSONArray();
		try{
			rows = nonUniqueFuture.get();
		} catch(Exception e){System.out.println(e.getMessage());}
		int[][][] counts = new int[numApps][numSegments][2];
		for(int i=0;i<rows.size();i++){
			JSONObject row = (JSONObject) rows.get(i);
			JSONArray key = (JSONArray) row.get("key");
			int index = times.indexOf(toDate(key,true,hasEnv));
			int app = Integer.parseInt(key.get(key.size()-1).toString());
			if(app>=0&&index>=0){
				counts[app][index][0]=Integer.parseInt(row.get("value").toString());
			}
		}
		for(int j=0;j<numSegments;j++){
			for(int i=0;i<numApps;i++){
				if(counts[i][j][0]==0){
					uniqueFutures.add(null);
				} else{
					uniqueFutures.add(myService.submit(new CountUnique(startkey,endkey,type+getUnit(unit+1),unit+envInt+4)));
				}
				startkey = incrementApp(startkey);
				endkey = incrementApp(endkey);
			}
			startkey = increment(startkey,unit,true,hasEnv);
			endkey = increment(endkey,unit,true,hasEnv);
			startkey.add(0);
			endkey.add(1);
		}
		int index =0;
		for(int i=0;i<numSegments;i++){
			for(int j=0;j<numApps;j++){
				if(uniqueFutures.get(index)!=null){
					try{
						counts[j][i][1] = uniqueFutures.get(index).get();
					} catch(Exception e){System.out.println(e.getMessage());}
				}
				index++;
			}
		}
		JSONArray data = new JSONArray();
		for(int i=0; i<numApps;i++){
			for(int j=0; j<numSegments;j++){
				JSONArray row = new JSONArray();
				row.add(times.get(j));
				row.add(i);
				
				row.add(counts[i][j][0]);
				row.add(counts[i][j][1]);
				data.add(row);
			}
		}
		JSONObject toReturn = new JSONObject();
		toReturn.put("data", data);
		toReturn.put("categories",getApps());
		toReturn.put("numSegments", numSegments);
		responses.put("getCountsOverTime"+toDate(startDate,false)+""+toDate(endDate,false)+""+unit+environment,toReturn.toString());

		return toReturn.toString();
	}
	
	//NEED TO WRITE
	
	public String getCustomerCountsOverTime(int[] date,boolean reload){
		return getCustomerCountsOverTime(date,increment(date,date.length-1,false),date.length,reload);
	}
	public String getCustomerCountsOverTime(int[] startdate, int[] enddate, int unit, boolean reload){
		if((!reload)&&(responses.get("getCustomerCountsOverTime"+toDate(startdate,false)+""+toDate(enddate,false)+""+unit)!=null&&old)){
			return responses.get("getCustomerCountsOverTime"+toDate(startdate,false)+""+toDate(enddate,false)+""+unit);
		}
		JSONObject toReturn = new JSONObject();
		Future<JSONArray> nonUniqueFuture = myService.submit(new CountNonUnique(startdate,enddate,"Get/"+getUnit(unit+1),unit+2));
		JSONArray nonUnique = new JSONArray();
		try{
			nonUnique = nonUniqueFuture.get();
		} catch(Exception e){System.out.println(e.getMessage());}
		List<Long> times = generateSegments(startdate,enddate,unit);
		int numSegments= times.size();
		JSONArray tempData = new JSONArray();
		for(int i=0;i<numSegments;i++){
			tempData.add(new JSONObject());
		}
		List<Long> customerLongs = new ArrayList<Long>();
		for(int i=0;i<nonUnique.size();i++){
			JSONObject row = (JSONObject) nonUnique.get(i);
			JSONArray key = (JSONArray) row.get("key");
			long curTime = toDate(key,false,false,true);
			int index= times.indexOf(curTime);
			JSONObject temp =(JSONObject) tempData.get(index);
			long customer = Long.parseLong(key.get(unit+1).toString());
			temp.put(Long.toString(customer),row.get("value"));
			tempData.set(index,temp);
			if(customerLongs.indexOf(customer)<0){
				customerLongs.add(customer);
			}
		}
		JSONArray startkey = fromCalendarToJSON(toCalendar(startdate,false),unit+1,false);
		startkey.add(null);
		JSONArray endkey = new JSONArray();
		List<Future<Integer>> uniqueFutures = new ArrayList<Future<Integer>>();
		for(int i=0;i<numSegments;i++){
			for(int j=0;j<customerLongs.size();j++){
				if(((JSONObject)tempData.get(i)).get(Long.toString(customerLongs.get(j)))==null){
					uniqueFutures.add(null);
				} else{
					startkey = fromCalendarToJSON(toCalendar(startkey,false,false,true),unit+1,false);
					endkey = fromCalendarToJSON(toCalendar(startkey,false,false,true),unit+1,false);
					startkey.add(customerLongs.get(j));
					endkey.add(customerLongs.get(j)+1);
					uniqueFutures.add(myService.submit(new CountUnique(startkey,endkey,"Get/"+getUnit(unit+1),unit+2)));
				}
			}
			startkey = increment(startkey,unit,true,false);
		}
		List<String> customers = toStringList(customerLongs);
		JSONArray data = new JSONArray();
		for(int j=0;j<customerLongs.size();j++){
			for(int i=0;i<numSegments;i++){
				JSONArray entry = new JSONArray();
				entry.add(times.get(i));
				entry.add(j);
				if(uniqueFutures.get(i*customerLongs.size()+j)==null){
					entry.add(0);
					entry.add(0);
					data.add(entry);
					continue;
				}
				entry.add(((JSONObject)tempData.get(i)).get(customers.get(j)));
				int unique = 0;
				try{
					unique = uniqueFutures.get(i*customerLongs.size()+j).get();
				} catch(Exception e){System.out.println(e.getMessage());}
				entry.add(unique);
				data.add(entry);
			}
		}
		toReturn.put("data",data);
		toReturn.put("numSegments",numSegments);
		toReturn.put("categories",customers);
		responses.put("getCustomerCountsOverTime"+toDate(startdate,false)+""+toDate(enddate,false)+""+unit, toReturn.toString());
		return toReturn.toString();
	}
	
	public String getCustomerAppCountsOverTime(int[] date, long customer, boolean reload){
		return getCustomerAppCountsOverTime(date, increment(date,date.length-1,false),date.length,customer,reload);
	}
	public String getCustomerAppCountsOverTime(int[] startdate, int[] enddate, int unit, long customer, boolean reload){
		if((!reload)&&(responses.get("getCustomerAppCountsOverTime"+toDate(startdate,false)+""+toDate(enddate,false)+""+unit+"Customer"+customer)!=null&&old)){
			return responses.get("getCustomerAppCountsOverTime"+toDate(startdate,false)+""+toDate(enddate,false)+""+unit+"Customer"+customer);
		}
		JSONObject toReturn = new JSONObject();
		List<Long> times = generateSegments(startdate,enddate,unit);
		int numSegments = times.size();
		Future<JSONArray> nonUniqueFuture = myService.submit(new CountNonUnique(startdate,enddate,"GetPerApp/"+getUnit(unit+1),unit+3));
		JSONArray rows = new JSONArray();
		try{
			rows = nonUniqueFuture.get();
		} catch(Exception e){
			System.out.println(e.getMessage());
		}
		int[][] nonUnique = new int[numSegments][numApps];
		for(int i=0;i<rows.size();i++){
			JSONObject row = (JSONObject) rows.get(i);
			JSONArray key =(JSONArray) row.get("key");
			if(Long.parseLong(key.get(unit+2).toString())!=customer){
				continue;
			}
			key.remove(unit+2);
			nonUnique[times.indexOf(toDate(key,true,false))][Integer.parseInt(key.get(unit+1).toString())]=Integer.parseInt(row.get("value").toString());
		}
		List<Future<Integer>> uniqueFutures = new ArrayList<Future<Integer>>();
		JSONArray startkey = fromCalendarToJSON(toCalendar(startdate,false),unit+1,false);
		JSONArray endkey = fromCalendarToJSON(toCalendar(startdate,false),unit+1,false);
		startkey.add(0);
		endkey.add(0);
		startkey.add(customer);
		endkey.add(customer+1);
		for(int i=0;i<numSegments;i++){
			for(int j=0;j<numApps;j++){
				if(nonUnique[i][j]>0){
					uniqueFutures.add(myService.submit(new CountUnique(startkey,endkey,"GetPerApp/"+getUnit(unit+1),unit+4)));
				} else{
					uniqueFutures.add(null);
				}
				startkey=incrementApp(startkey,true);
				endkey=incrementApp(endkey,true);
			}
			startkey=increment(startkey,unit,true,false,true);
			endkey=increment(endkey,unit,true,false,true);
		}
		JSONArray data = new JSONArray();
		for(int i=0;i<numApps;i++){
			for(int j=0;j<numSegments;j++){
				JSONArray entry = new JSONArray();
				entry.add(times.get(j));
				entry.add(i);
				entry.add(nonUnique[j][i]);
				int unique = 0;
				if(nonUnique[j][i]>0){
					try{
						unique = uniqueFutures.get(numSegments*i+j).get();
					} catch(Exception e){
						System.out.println(e.getMessage());
					}
				}
				entry.add(unique);
				data.add(entry);
			}
		}
		/////NEEDS TO BE COMPLETED
		toReturn.put("data",data);
		toReturn.put("numSegments", numSegments);
		toReturn.put("categories", getApps());
		responses.put("getCustomerAppCountsOverTime"+toDate(startdate,false)+""+toDate(enddate,false)+""+unit+"Customer"+customer, toReturn.toString());
		return toReturn.toString();
	}
	public String getCustomerTotalCountsOverTime(int[] date, long customer, boolean reload){
		return getCustomerTotalCountsOverTime(date, increment(date,date.length-1,false),date.length,customer,reload);
	}
	public String getCustomerTotalCountsOverTime(int[] startdate, int[] enddate, int unit, long customer, boolean reload){
		if((!reload)&&(responses.get("getCustomerTotalCountsOverTime"+toDate(startdate,false)+""+toDate(enddate,false)+""+unit+"Customer"+customer)!=null&&old)){
			return responses.get("getCustomerTotalCountsOverTime"+toDate(startdate,false)+""+toDate(enddate,false)+""+unit+"Customer"+customer);
		}
		JSONObject toReturn = new JSONObject();
		
		responses.put("getCustomerTotalCountsOverTime"+toDate(startdate,false)+""+toDate(enddate,false)+""+unit+"Customer"+customer, toReturn.toString());
		return toReturn.toString();
	}
	//date can be empty
	public String getCounts(int[] date, String environment){
		return getCounts(date,environment,true);
	}
	public String getCounts(int[] date, String environment,boolean reload){
		if((!reload)&&(responses.get("getCounts"+toDate(date,false)+""+date.length+environment)!=null&&old)){
			return responses.get("getCounts"+toDate(date,false)+""+date.length+environment);
		}
		List<Future<Integer>> uniqueFutures = new ArrayList<Future<Integer>>();
		boolean hasEnv = environment!=null;
		int envInt = 0;
		String type = "GetPerApp/"+getUnit(date,false);
		
		JSONArray startDate = fromCalendarToJSON(toCalendar(date,false),date.length,hasEnv);
		JSONArray endDate = increment(startDate,date.length-1,false,hasEnv);
		if(hasEnv){
			envInt = 1;
			type = "GetPerEnvPerApp/"+getUnit(date,false);
			startDate.set(0,environment);
			endDate.set(0, environment);
		}

		JSONArray startkey = fromCalendarToJSON(toCalendar(date,false),date.length,hasEnv);
		startkey.add(0);
		if(hasEnv){
			startkey.set(0, environment);
		}
		JSONArray endkey = incrementApp(startkey);
		for(int i=0; i<numApps; i++){
			uniqueFutures.add(myService.submit(new CountUnique(startkey, endkey, type, date.length+3+envInt)));
			startkey = incrementApp(startkey);
			endkey = incrementApp(endkey);
		}
		Future<JSONArray> nonUniqueFuture = myService.submit(new CountNonUnique(startDate,endDate,type,date.length+1+envInt));
		JSONObject toReturn = new JSONObject();
		JSONArray data = new JSONArray();
		JSONArray rows = new JSONArray();
		try{
			rows = nonUniqueFuture.get();
		} catch(Exception e){System.out.println(e.getMessage());}
		int[] nonUniqueCounts = new int[numApps];
		for(int i=0; i<rows.size();i++){
			JSONObject row = (JSONObject) rows.get(i);
			JSONArray key = (JSONArray) row.get("key");
			//System.out.println(key);
			int app = Integer.parseInt(key.get(date.length+envInt).toString());
			if(app>=0){
				int value = Integer.parseInt(row.get("value").toString());
				nonUniqueCounts[app] = value;
			}
		}
		for(int i=0; i<numApps;i++){
			JSONArray row = new JSONArray();
			row.add(i);
			//row.add(toDate(date,false));
			row.add(nonUniqueCounts[i]);
			try{
				row.add(uniqueFutures.get(i).get());
			} catch(Exception e){ System.out.println(e.getMessage());row.add(0);}
			data.add(row);
		}
		toReturn.put("data",data);
		toReturn.put("categories", getApps());
		responses.put("getCounts"+toDate(date,false)+""+date.length+environment,toReturn.toString());
		return toReturn.toString();
	}
	public String getOS(int[] date, String environment){
		return getNested(date,"OS",environment,-1,true);
	}
	public String getOS(int[] date, String environment,boolean reload){
		return getNested(date,"OS",environment,-1,reload);
	}
	public String getOSApp(int[] date, int application, String environment){
		return getNested(date,"OS",environment, application,true);
	}
	public String getOSApp(int[] date, int application, String environment,boolean reload){
		return getNested(date,"OS",environment, application,reload);
	}
	public String getClient(int[] date, String environment){
		return getNested(date,"Client",environment,-1,true);
	}
	public String getClient(int[] date, String environment,boolean reload){
		return getNested(date,"Client",environment,-1,reload);
	}
	public String getClientApp(int[] date, int application, String environment){
		return getNested(date,"Client",environment, application,true);
	}
	public String getClientApp(int[] date, int application, String environment,boolean reload){
		return getNested(date,"Client",environment, application,reload);
	}
	private String getNested(int[] date, String type, String environment, int app,boolean reload){
		if((!reload)&&(responses.get("getNested"+toDate(date,false)+""+date.length+type+environment+app)!=null&&old)){
			return responses.get("getNested"+toDate(date,false)+""+date.length+type+environment+app);
		}
		boolean hasEnv = environment!=null;
		boolean hasApp = app>=0;
		int envInt =0;
		int appInt = 0;
		String envString = "";
		String appString = "";
		JSONArray startDate = fromCalendarToJSON(toCalendar(date,false), date.length,hasEnv);
		JSONArray endDate;
		if(hasEnv){
			startDate.set(0,environment);
			envInt = 1;
			envString = "PerEnv";
			
		}
		if(hasApp){
			startDate.add(app);
			endDate = incrementApp(startDate);
			appString ="PerApp";
			appInt = 1;
		} else{
			endDate = increment(startDate,date.length-1,hasApp,hasEnv);
		}
		Future<JSONArray> nonUnique;
		String typeString = "Get"+type+envString+appString+"/"+getUnit(date,false);
		if(startDate.size()==0){
			startDate=null;
			endDate=null;
		}
		nonUnique = myService.submit(new CountNonUnique(startDate,endDate,typeString,date.length+2+appInt+envInt));
		JSONArray rows = new JSONArray();
		try{
			rows= nonUnique.get();
		} catch(Exception e){System.out.println(e.getMessage());}
		JSONArray categories = new JSONArray();
		JSONArray categoriesParent = new JSONArray();
		JSONArray indices = new JSONArray(); //start of each Os on categories
		JSONArray data = new JSONArray();
		String currentParent = null;
		//int[][] tempData= new int[][]
		for(int i=0; i<rows.size();i++){
			
			JSONObject row = (JSONObject) rows.get(i);
			JSONArray key = (JSONArray) row.get("key");
			JSONArray entry = new JSONArray();
			String tempParent = key.get(date.length+appInt+envInt).toString();
			if(tempParent.indexOf(";")>=0||key.get(date.length+1+appInt+envInt).toString().indexOf(";")>=0){
				continue;
			}
			if(currentParent==null){
				currentParent= tempParent;
				entry.add(0);
				entry.add(null);
				entry.add(0);
				entry.add(0);
				data.add(entry);
				entry = new JSONArray();
				categories.add(currentParent);
				indices.add(0);
			}
			if(!currentParent.equals(tempParent)){
				entry.add(categories.size());
				entry.add(null);
				entry.add(0);
				entry.add(0);
				data.add(entry);
				entry = new JSONArray();
				categoriesParent.add(currentParent);
				currentParent=tempParent;
				indices.add(categories.size());
				categories.add(currentParent);
			}
			entry.add(categories.size());
			entry.add(categoriesParent.size());
			entry.add(Integer.parseInt(row.get("value").toString()));
			data.add(entry);
			categories.add(key.get(date.length+1+appInt+envInt).toString());
		}
		categoriesParent.add(currentParent);
		indices.add(categories.size());
		List<Future<Integer>> uniqueFutures = new ArrayList<Future<Integer>>();
		for(int i=0; i<indices.size()-1;i++){
			uniqueFutures.add(null);
			for(int j=Integer.parseInt(indices.get(i).toString())+1;j<Integer.parseInt(indices.get(i+1).toString());j++){
				JSONArray startkey = fromCalendarToJSON(toCalendar(date,false),date.length,hasEnv);
				JSONArray endkey = fromCalendarToJSON(toCalendar(date,false),date.length,hasEnv);
				if(hasApp){
					startkey.add(app);
					endkey.add(app);
				}
				if(hasEnv){
					startkey.set(0,environment);
					endkey.set(0,environment);
				}
				startkey.add(categoriesParent.get(i).toString());
				endkey.add(categoriesParent.get(i).toString());
				startkey.add(categories.get(j).toString());
				endkey.add(categories.get(j).toString());
				endkey.add("a");
				
				uniqueFutures.add(myService.submit(new CountUnique(startkey, endkey,typeString,date.length+appInt+envInt+4)));
			}
		}
		for(int i=0; i<uniqueFutures.size();i++){
			if(uniqueFutures.get(i)!=null){
				int value = 0;
				try{
					value = uniqueFutures.get(i).get();
				} catch(Exception e){System.out.println(e.getMessage());}
				JSONArray entry = (JSONArray)data.get(i);
				entry.add(value);
				data.set(i,entry);
			}
		}
		JSONObject toReturn = new JSONObject();
		toReturn.put("categories",categories);
		toReturn.put("indices",indices);
		toReturn.put("categoriesParent",categoriesParent);
		toReturn.put("data", data);
		responses.put("getNested"+toDate(date,false)+""+date.length+type+environment+app, toReturn.toString());
		return toReturn.toString();
	}
	public String getList(String type){
		return getList(type,true);
	}
	public String getList(String type,boolean reload){
		if((!reload)&&(responses.get("getList"+type)!=null&&old)){
			return responses.get("getList"+type);
		}
		JSONObject toReturn = new JSONObject();
		Future<JSONArray> nonUnique = myService.submit(new CountNonUnique(null,null,"Get"+type+"/AllTime",2));
		String curParent = null;
		JSONArray currentVersion = new JSONArray();
		JSONArray rows = new JSONArray();
		try{
			rows = nonUnique.get();
		} catch (Exception e){System.out.println(e.getMessage());}
		for(int i=0;i<rows.size();i++){
			JSONObject row = (JSONObject) rows.get(i);
			JSONArray key = (JSONArray) row.get("key");
			if(key.get(0)==null)
				continue;
			if(curParent == null){
				curParent = key.get(0).toString();
			}
			if(curParent.equals(key.get(0).toString())){
				if(key.get(1)!=null){
					currentVersion.add(key.get(1).toString());
				}
			} else{
				toReturn.put(curParent,currentVersion);
				currentVersion = new JSONArray();
				curParent = key.get(0).toString();
				currentVersion.add(key.get(1).toString());
			}
			
		}
		responses.put("getList"+type, toReturn.toString());
		return toReturn.toString();
	}
	public String getAppBubble(int[] date, int application, String environment){
		return getAppBubble(date,application,environment,true);
	}
	public String getAppBubble(int[] date, int application,String environment,boolean reload){
		if((!reload)&&(responses.get("getAppBubble"+toDate(date,false)+""+date.length+""+application+environment)!=null&&old)){
			return responses.get("getAppBubble"+toDate(date,false)+""+date.length+""+application+environment);
		}
		boolean hasEnv = environment!=null;
		String type = "GetAppDataPerApp/"+getUnit(date,false);
		int envInt = 0;
		JSONArray startdate = fromCalendarToJSON(toCalendar(date,false),date.length,hasEnv);
		startdate.add(application);
		JSONArray enddate = fromCalendarToJSON(toCalendar(date,false),date.length,hasEnv);
		enddate.add(application+1);
		if(hasEnv){
			startdate.set(0,environment);
			enddate.set(0,environment);
			envInt = 1;
			type = "GetAppDataPerEnvPerApp/"+getUnit(date,false);
		}
		Future<JSONArray> nonUnique = myService.submit(new CountNonUnique(startdate,enddate,type,date.length+2+envInt));
		JSONArray rows = new JSONArray();
		try{
			rows = nonUnique.get();
		}catch(Exception e){System.out.println(e.getMessage());}
		JSONArray categories = new JSONArray();
		JSONArray data = new JSONArray();
		List<Future<Integer>> unique = new ArrayList<Future<Integer>>();
		for(int i=0;i<rows.size();i++){
			JSONObject row = (JSONObject) rows.get(i);
			JSONArray key = (JSONArray) row.get("key");
			if(key.get(date.length+1+envInt)==null){
				continue;
			}
			String appData = key.get(date.length+1+envInt).toString();
			if(!appData.equals("")){
				JSONArray startkey = fromCalendarToJSON(toCalendar(date,false),date.length,hasEnv);
				JSONArray endkey = fromCalendarToJSON(toCalendar(date,false),date.length,hasEnv);
				if(hasEnv){
					startkey.set(0,environment);
					endkey.set(0,environment);
				}
				startkey.add(application);
				endkey.add(application);
				startkey.add(appData);
				endkey.add(appData);
				endkey.add("a");
				unique.add(myService.submit(new CountUnique(startkey,endkey,type,date.length+envInt+4)));
				JSONArray entry = new JSONArray();
				Integer value = Integer.parseInt(row.get("value").toString());
				entry.add(categories.size());
				entry.add(Math.sqrt(value));
				entry.add(value);
				data.add(entry);
				categories.add(appData);
			}
		}
		for(int i=0; i<unique.size();i++){
			JSONArray entry = (JSONArray) data.get(i);
			int value = 0;
			try{
				value = unique.get(i).get();
			} catch(Exception e){System.out.println(e.getMessage());}
			entry.add(Math.sqrt(value));
			entry.add(value);
			data.set(i,entry);
		}
		
		JSONObject toReturn = new JSONObject();
		toReturn.put("data",data);
		toReturn.put("categories",categories);
		responses.put("getAppBubble"+toDate(date,false)+""+date.length+""+application+environment,toReturn.toString());
		return toReturn.toString();
	}
	public String getCustomerCounts(int[] date, boolean reload){
		if((!reload)&&(responses.get("getCustomerCounts"+toDate(date,false)+""+date.length)!=null&&old)){
			return responses.get("getCustomerCounts"+toDate(date,false)+""+date.length);
		}
		JSONArray toReturn = new JSONArray();
		int[] endDate;
		int[] startDate = date;
		if(date.length==0){
			startDate=null;
			endDate = null;
		} else
			endDate = increment(date,date.length-1,false);
		Future<JSONArray> nonUnique = myService.submit(new CountNonUnique(startDate, endDate,"Get/"+getUnit(date,false),date.length+1));
		JSONArray rows = new JSONArray();
		try{
			rows = nonUnique.get();
		} catch(Exception e){System.out.println(e.getMessage());}
		JSONArray customers = new JSONArray();
		for(int i=0; i<rows.size();i++){
			JSONObject row = (JSONObject) rows.get(i);
			JSONArray key = (JSONArray) row.get("key");
			int value = Integer.parseInt(row.get("value").toString());
			if(key.get(date.length)==null){
				continue;
			}
			int customer = Integer.parseInt(key.get(date.length).toString());
			JSONArray entry = new JSONArray();
			entry.add(customer);
			customers.add(customer);
			entry.add(value);
			toReturn.add(entry);
		}
		List<Future<Integer>> unique = new ArrayList<Future<Integer>>();
		for(int i=0; i<customers.size();i++){
			int[] startkey = fromCalendar(toCalendar(date,false),date.length+1);
			int[] endkey = fromCalendar(toCalendar(date,false),date.length+1);
			startkey[date.length]= Integer.parseInt(customers.get(i).toString());
			endkey[date.length] = startkey[date.length]+1;
			unique.add(myService.submit(new CountUnique(startkey,endkey,"Get/"+getUnit(date,false),date.length+2)));
		}
		for(int i=0; i<unique.size();i++){
			JSONArray entry = (JSONArray)toReturn.get(i);
			int count =0;
			try{
				count = unique.get(i).get();
			} catch(Exception e) {System.out.println(e.getMessage());};
			entry.add(count);
			toReturn.set(i,entry);
		}
		responses.put("getCustomerCounts"+toDate(date,false)+""+date.length, toReturn.toString());
		return toReturn.toString();
	}
	private static JSONArray getApps(){
		JSONArray appData = Service.getApplicationNames();
		JSONArray apps = new JSONArray();
		for(int i=0;i<appData.size();i++){
			JSONObject app = (JSONObject) appData.get(i);
			apps.add(app.get("name"));
		}
		return apps;
	}
	public static void main(String[] args){
		CloudantManager test = new CloudantManager("cloudant.properties",125,true);
		System.out.println(Service.getApplicationNames());
		//System.out.println(Service.getApplicationNames());
		int[] date = {2014};
		int[] date2 = {2014,9};
		int[] startDate = {2014};
		int[] endDate = {2015};
		CouchDbClient client = new CouchDbClient(test.getPropertiesFile());
		try{
		System.out.println(JSONObject.parse(client.find("!CustomerReturnCache")));
		} catch(Exception e){
			e.printStackTrace();
		}
		//System.out.println(test.getCachedCustomerCountsOverTime(date2,false));
		test.shutdownMyService();
//		int[] buckets = {2,5,12,20};
//		int[] mayBuckets = {2,4,6,10};
//		JSONArray bucketStrings =  new JSONArray();
//		bucketStrings.add("Once per month");
//		bucketStrings.add("Once per week");
//		bucketStrings.add("Twice per week");
//		bucketStrings.add("3-4 times a week");
//		bucketStrings.add("Daily");

	}
}