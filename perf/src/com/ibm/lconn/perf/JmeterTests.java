package com.ibm.lconn.perf;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Runs one or more JMeter test plan sequentially
 * </br>
 * <b>Required:</b></br>
 * -Djmeter.home="D:\wspace\tools\apache-jmeter-2.9"</br> 
 * -Djmeter.server.name="lc45linux2.swg.usma.ibm.com"</br>
 * </br>
 * <b>Optional (+default value):</b></br>
 * -Djmeter.result.dir="D:\wspace\!jmeter\news"</br>
 * -Dfailure.property.name="jmeter.test.plan.failed"</br>
 * -Djmeter.server.http.port="80"</br>
 * -Djmeter.server.ssl.port=443</br>
 * 
 */
@RunWith(Suite.class)	
@Suite.SuiteClasses(
		{ 
			com.ibm.lconn.perf.jmeter.JMeterTestPlanRunner.class
		}
)
public class JmeterTests {

	
}
