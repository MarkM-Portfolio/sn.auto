package com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.population;

public class SyncCrawlerBySleeping30Sec {

	public static void run() {
		try {
			System.out.println("Going to nap for 30 sec");
			Thread.sleep(30 * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
