package com.ibm.atmn.waffle.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.core.Executor;
import com.ibm.atmn.waffle.core.ExecutorActionListener;
import com.ibm.atmn.waffle.core.JavaScriptLoader;
import com.ibm.atmn.waffle.core.JavaScriptLoader.Script;
import com.ibm.atmn.waffle.utils.Utils;

public class BasePCHListener implements ExecutorActionListener {

	private static final Logger log = LoggerFactory.getLogger(BasePCHListener.class);

	@Override
	public void afterElementEvent(Executor exec, Element elem, ElementEvent event) {

		//log.debug("BasePCHListener afterElementEvent for event " + event + ".");

		// the event may have closed the window, have to test for that
		// if(e.getMessage().toLowerCase().contains("unable to get window") || e.getMessage().toLowerCase().contains("unable to get browser")) return;

		Utils.milliSleep(exec.getTestManager().getTestConfig().getPostEventPause());
	}

	private void dojoOnPageLoad(Executor exec) {

		if (exec.getTestManager().getTestConfig().dojoPageLoadEnabled()) {
			@SuppressWarnings("unused")
			long start = System.currentTimeMillis();
			JavaScriptLoader.loadScript(exec, Script.WAFFLE_PCH);
			Object result = exec.executeAsyncScript("window.waffleCallback = arguments[arguments.length - 1];if(" + Script.WAFFLE_PCH.getHandle() + "){" + Script.WAFFLE_PCH.getHandle() + ".dojoOnLoad();}else{window.waffleCallback(true);};");
			boolean success = false;
			try {
				success = (Boolean) result;
			} catch (ClassCastException e) {
				log.warn("Unexpected return type from dojo onload.");
			}
			if (success != true) {
				log.warn("Dojo onload did not return successfully.");
			}
			// String dojoPageLoadFired = "false";
			// long deadline = System.currentTimeMillis() + 15000;
			// while (dojoPageLoadFired.equals("true") != true && System.currentTimeMillis() < deadline) {
			// dojoPageLoadFired = (String) exec.executeScript("if(dojo){return dojo.dojoPageLoadFired;}else{return 'true';}");
			// }
			//log.debug("Dojo addOnLoad returned after " + (System.currentTimeMillis() - start) + "milliseconds.");
		}
	}

	@Override
	public void afterExecutorEvent(Executor exec, ExecutorEvent event) {

	}

	@Override
	public void beforeElementEvent(Executor exec, Element elem, ElementEvent event) {

		//log.debug("BasePCHListener beforeElementEvent for event " + event + ".");

		if (event == ElementEvent.CLICK
				|| event == ElementEvent.HOVER
				|| event == ElementEvent.SELECT_OPTION
				|| event == ElementEvent.CLEAR
				|| event == ElementEvent.IS_VISIBLE_TEST
				|| event == ElementEvent.GET_ELEMENT_LOCATION
				|| event == ElementEvent.TYPE) {
			dojoOnPageLoad(exec);
		}
	}

	@Override
	public void beforeExecutorEvent(Executor exec, ExecutorEvent event) {

	}

	@Override
	public void beforeExecutorQuit(Executor exec) {

	}

	@Override
	public void onExecutorLoad(Executor exec) {

	}

}
