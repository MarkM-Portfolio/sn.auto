package com.ibm.atmn.waffle.utils;

import java.security.InvalidParameterException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class Utils {

	static ThreadLocal<Map<String, Object>> threadLocalValue = new ThreadLocal<Map<String, Object>>();

	public static void milliSleep(long milliSeconds) {

		try {
			Thread.sleep(milliSeconds);
		} catch (InterruptedException e) {
		}
	}
	
	public static String genStamp() {

		SimpleDateFormat tmformat = new SimpleDateFormat("DDDHHmmss");
		return tmformat.format(new Date()) + Thread.currentThread().getId();
	}
	
	public static <E extends Enum<E> & PropertyNum> EnumMap<E, String> loadPropertyMapToEnum(Map<String, String> map, Class<E> numclass){
		
		EnumMap<E, String> enumMap = new EnumMap<E, String>(numclass);
		
		for (E num : numclass.getEnumConstants()) {
			String propertyValue = null;
			propertyValue = map.get(num.toString().toLowerCase());
			if (propertyValue != null) {
				enumMap.put(num, propertyValue);
			}
			else if (num.getDefaultValue() != null) {
				enumMap.put(num, num.getDefaultValue());
			}
			else {
				throw new InvalidParameterException("Required parameter not found in config: " + num.toString().toLowerCase());
			}
		}
		
		return enumMap;
	}


	public static void initialValue() {
		Map<String, Object> threadLocalMap = new HashMap<String, Object>();
		setThreadLocalMap(threadLocalMap);
	}
	
	//Added a thread level variable to store <class name><method name> 
	//This variable will be used to update test case status at browserstack and Selenoid
	public static void setThreadLocalUniqueTestName(String name) {
		Map<String, Object> threadDetails = threadLocalValue.get();
		threadDetails.put("testname", name);
		setThreadLocalMap(threadDetails);
	}
	
	public static void setThreadLocalImplictWait(long waitTime) {
		Map<String, Object> threadDetails = threadLocalValue.get();
		threadDetails.put("implicitwait", waitTime);
		setThreadLocalMap(threadDetails);
	}
	
	public static long getThreadLocalImplictWait(){
		return (long)getThreadLocalValue("implicitwait");
	}
	
	public static String getThreadLocalUniqueTestName(){
		try {
			return (String)getThreadLocalValue("testname");
		}
		catch (Exception e) {
			//Added the try-catch block because the ICBaseUI.load() method fails at line 76 if called from BeforeClass or BeforMethod methods of a test class
			return "testname" + ThreadLocalRandom.current().nextInt(100000, 1000000);
		}
	}
	
	public static Object getThreadLocalValue(String key){
		Map<String, Object> threadDetails = threadLocalValue.get();
		return threadDetails.get(key);
	}
	
	private static void setThreadLocalMap(Map<String, Object> threadDetails) {
		threadLocalValue.set(threadDetails);
	}

}
