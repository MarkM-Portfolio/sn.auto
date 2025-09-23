package com.ibm.atmn.waffle.core;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.ibm.atmn.waffle.log.LogManager;

/**
 * Helpers for determining and handling an environment. Local environment can be determined automatically. Remote
 * environments are determined from test configuration.
 * 
 * @author Ruairi Pidgeon/Ireland/IBM
 */
public class Environment {
	
	private static enum OSType {
		WINDOWS, LINUX, MAC
	}

	private static enum ArchType {
		BIT64, BIT32
	}

	private OSType os;
	private ArchType arch;

	private boolean isLocal = false;

	/**
	 * Constructs Environment based on local System
	 */
	public Environment() {

		this(true, "");
	}

	/**
	 * Use to construct a remote environment. Default arch is BIT32. 'Windows' or 'Unix' or 'Linux' or 'Mac' keyword is
	 * required.
	 * 
	 * @param os
	 *            String with OS info from configuration file. OS name,version,arch. e.g. Windows7x64
	 * 
	 */
	public Environment(String configOS) {

		this(false, configOS);
	}

	/**
	 * Use to construct local environment from configuration string instead of attempting to determine form System
	 * properties.
	 * 
	 * @param isLocal
	 * @param configOS
	 */
	public Environment(boolean isLocal, String configOS) {

		this.isLocal = isLocal;
		if (this.isLocal && configOS.length() == 0) {
			configOS = System.getProperty("os.name") + System.getProperty("os.version");
			
			// can't just use "os.arch" system property to determine OS bit since it returns x86 if JVM is 32 bit on Windows.
			if (configOS.toLowerCase().contains("win")) {
				String arch = System.getenv("PROCESSOR_ARCHITECTURE");
				String wow64Arch = System.getenv("PROCESSOR_ARCHITEW6432");
				configOS += arch != null && arch.endsWith("64")
						|| wow64Arch != null && wow64Arch.endsWith("64")
						? "64" : "32";
			} else {
				configOS += System.getProperty("os.arch");
			}
		}

		identifyOS(configOS);
		printProps();

	}

	/**
	 * Tries to determine os properties and set public boolean helpers.
	 * 
	 * @param os
	 */
	private void identifyOS(String configOS) {

		if(configOS == null) throw new InvalidParameterException("Null parameter");
		if (configOS.toLowerCase().contains("win")) {
			this.os = OSType.WINDOWS;
		}
		else if (configOS.toLowerCase().contains("nix") || configOS.toLowerCase().contains("nux")) {
			this.os = OSType.LINUX;
		}
		else if (configOS.toLowerCase().contains("mac")) {
			this.os = OSType.MAC;
		}
		else {
			throw new InvalidParameterException("The OS type could not be determined from the config provided: " + configOS);
		}

		// mac os.arch returns x86_64 so don't assume x86 is always 32 bit, set default to 64 bit
		if (configOS.toLowerCase().contains("x86") && !configOS.toLowerCase().contains("64")) {
			this.arch = ArchType.BIT32;
		}
		else { //Default
			this.arch = ArchType.BIT64;
		}
	}

	public boolean isWindows() {

		return this.os == OSType.WINDOWS ? true : false;
	}

	public boolean isLinux() {

		return this.os == OSType.LINUX ? true : false;
	}

	public boolean isMac() {

		return this.os == OSType.MAC ? true : false;
	}

	public boolean is64Bit() {

		return this.arch == ArchType.BIT64 ? true : false;
	}

	public boolean is32Bit() {

		return this.arch == ArchType.BIT32 ? true : false;
	}

	/**
	 * Prints out all of the environment properties
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	private void printProps() {
		Map<String, String> map = new HashMap<String, String>();
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream("version.txt"));
			map.put("BuildTime", properties.getProperty("Buildtime"));
			LogManager.printPropertyMap(map, "BUILD INFO");
			map.clear();
		} catch (IOException e) {
			// version.txt not found when running from eclipse, ignore
		}
		
		map.put("isLocalEnvironment", String.valueOf(this.isLocal));
		map.put(this.os.getDeclaringClass().getSimpleName(), this.os.name());
		map.put(this.arch.getDeclaringClass().getSimpleName(), this.arch.name());
		LogManager.printPropertyMap(map, "ENVIRONMENT");
	}

	/**
	 * Constructs an absolute path for this environment including correct separator and root.
	 * 
	 * @param folders
	 *            The directories in order, ending with target directory, starting from root (e.g. 'c:\' or '/')
	 * @return The absolute path without a trailing separator
	 */
	public String constructAbsolutePathToDirectoryFromRoot(String... folders) {

		String path = "";
		String separator = getSeparator();
		int pos = 0;

		if (folders[0].indexOf(":")==1 || folders[0].startsWith("/")) {
			path = folders[0];
			pos = 1;
			if (path.endsWith(separator)) {
				path = path.substring(0, path.lastIndexOf(separator));
			}
		}
		else {
			if (isWindows()) {
				path = "C:";
			}
		}

		while (pos < folders.length) {
			String folder = folders[pos];
			path = path + separator + folder;
			pos++;
		}
		//log.debug("Constructed environment path: " + path);
		return path;
	}

	/**
	 * 
	 * @param dir
	 *            An absolute path to directory.
	 * @param fileName
	 *            The name of the file to add to the path.
	 * @return Absolute path to the file
	 * @see {@link #constructAbsolutePathToDirectoryFromRoot(String...)}
	 */
	public String getAbsoluteFilePath(String dir, String fileName) {

		String separator = getSeparator();
		if (dir.endsWith(separator)) {
			return dir + fileName;
		}
		else {
			return dir + separator + fileName;
		}
	}

	/**
	 * Use instead of File.separator to construct paths for remote machines with possibly different OS.
	 * 
	 * @return Forward-slash or back-slash depending on OS
	 */
	private String getSeparator() {

		if (isWindows()) {
			return "\\";
		}
		else {
			return "/";
		}
	}

	public boolean isLocal() {

		return this.isLocal;
	}
}
