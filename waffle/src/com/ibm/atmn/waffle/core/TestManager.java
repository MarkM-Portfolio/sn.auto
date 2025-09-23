package com.ibm.atmn.waffle.core;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;

import com.ibm.atmn.waffle.core.TestConfiguration.TestTool;
import com.ibm.atmn.waffle.core.webdriver.WebDriverExecutor;

/**
 * Related directly to the TestNG concept of a Test ({@literal <test>}), and is tied to {@link ITestContext} so that it is available
 * where the context is injected and it persists for the duration of the test.  
 * <p />
 * The manager holds the {@link TestConfiguration}, {@link Executor} and Test level action listeners for a test.
 * 
 * @author Ruairi Pidgeon/Ireland/IBM
 *
 */
public class TestManager {

	private Executor exec;
	private RunConfiguration rConfig = RunConfiguration.getInstance();
	private TestConfiguration testConfig;
	private ITestContext testContext;
	private List<ExecutorActionListener> actionListenerList = new ArrayList<ExecutorActionListener>();
	
	private static final Logger log = LoggerFactory.getLogger(TestManager.class);

	private static volatile Map<ITestContext, TestManager> managers = Collections.synchronizedMap(new HashMap<ITestContext, TestManager>());
	
	private TestManager(ITestContext context) {

		log.info(this.toString());
		setTestContext(context);
		setTestConfig(TestConfiguration.getTestConfiguration(context));
		if(TestConfiguration.getTestConfiguration(context).testToolIs(TestTool.WEB_DRIVER)){
			setDriver(new WebDriverExecutor(this));
		} else{
			throw new InvalidParameterException("An executor implementation is not defined for this test tool: " + TestConfiguration.getTestConfiguration(context).getTestTool());
		}
		//log.debug("Manager has been created for test: " + context.getName());
	}

	public static TestManager getTestManager(ITestContext context){
		
		TestManager manager;
		if(managers.containsKey(context)){
			manager = managers.get(context);
		}
		else{
			manager = new TestManager(context);
			managers.put(context, manager);
		}
		return manager;
	}

	public void addTestActionListener(ExecutorActionListener listener){
		
		this.actionListenerList.add(listener);
	}
	
	public List<ExecutorActionListener> getTestActionListeners(){
		
		return this.actionListenerList;
	}
	
	public TestConfiguration getTestConfig() {

		return this.testConfig;
	}

	private void setTestConfig(TestConfiguration testConfig) {

		log.info(this.toString());
		this.testConfig = testConfig;
	}

	public Executor getExecutor() {

		return this.exec;
	}

	private void setDriver(Executor driver) {

		this.exec = driver;
	}

	public RunConfiguration getRunConfig() {

		return rConfig;
	}
	
	public ITestContext getTestContext() {
		
		return this.testContext;
	}

	private void setTestContext(ITestContext testContext) {
		
		this.testContext = testContext;
	}

}