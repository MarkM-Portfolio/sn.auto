package com.ibm.atmn.waffle.core;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.atmn.waffle.utils.FileIOHandler;

/**
 * Responsible for executing waffle script files when they are needed. Function names are hard-coded.
 * 
 * @author Ruairi Pidgeon/Ireland/IBM
 *
 */
public class JavaScriptLoader {

	private static final Logger log = LoggerFactory.getLogger(JavaScriptLoader.class);

	private static final String JS_DIRECTORY_PATH = "test_config/core/resources/js";

	public enum Script {
		WAFFLE_LOCATION(JS_DIRECTORY_PATH + "/WaffleLoc.min.js", "window.ICCAWaffleLoc"),
		EXTRA_SIZZLE(JS_DIRECTORY_PATH + "/ExtraSizzle.min.js", "window.ICCAWaffleSizzle"),
		WAFFLE_MISC(JS_DIRECTORY_PATH + "/WaffleMisc.min.js", "window.ICCAWaffleMisc"),
		WAFFLE_PCH(JS_DIRECTORY_PATH + "/WafflePCH.min.js", "window.ICCAWafflePCH");

		String filePath;
		String handle;

		Script(String filePath, String handle) {

			this.filePath = filePath;
			this.handle = handle;
		}

		public String getFilePath() {

			return this.filePath;
		}
		
		public String getHandle() {

			return this.handle;
		}
	}

	public static void loadScript(Executor exec, Script scriptFile) {

		//log.debug("Attempting to load script file: " + scriptFile.getFilePath());
		String script = FileIOHandler.readFileToString(new File(scriptFile.getFilePath()));

		Object result = exec.executeAsyncScript(script);
		boolean success;
		try{
			success = (Boolean) result; 
		}catch(ClassCastException e){
			log.error("Unexpected return type loading script: " + scriptFile);
			throw new RuntimeException("Unexpected return type loading script: " + scriptFile);
		}
		if(success != true){
			log.error("Error loading script: " + scriptFile);
			throw new RuntimeException("Error loading script: " + scriptFile);
		}

	}

}
