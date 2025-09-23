package com.ibm.lconn.automation.framework.services.common;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;

public class SystemInfo {

	private static void DisplayMXBean() {
		System.out.println("==== System MXBean ====");
		OperatingSystemMXBean operatingSystemMXBean = ManagementFactory
				.getOperatingSystemMXBean();
		for (Method method : operatingSystemMXBean.getClass()
				.getDeclaredMethods()) {
			method.setAccessible(true);
			if (method.getName().startsWith("get")
					&& Modifier.isPublic(method.getModifiers())) {
				Object value;
				try {
					value = method.invoke(operatingSystemMXBean);
				} catch (Exception e) {
					value = e;
				} 
				System.out.println(method.getName() + " = " + value);
			} 
		} 

	}

	public static void DisplayJavaVersion() {
		Properties props = System.getProperties();
		Iterator<Object> enu = props.keySet().iterator();
		while (enu.hasNext()) {
			String next = enu.next().toString();
			if (next.equalsIgnoreCase("java.fullversion")
					|| next.equalsIgnoreCase("os.name")
					|| next.equalsIgnoreCase("java.vm.vendor")
					|| next.equalsIgnoreCase("java.runtime.version")) {
				System.out.print(next + "   =   ");
				System.out.println(props.getProperty(next));
			}
		}
	}

	public static void DisplayDiskSpace() {
		System.out.println("==== System DiskSpace ====");
		/* Get a list of all filesystem roots on this system */
		File[] roots = File.listRoots();

		/* For each filesystem root, print some info */
		for (File root : roots) {
			System.out.println("File system root: " + root.getAbsolutePath());
			System.out.println("Total space (bytes): " + root.getTotalSpace());
			System.out.println("Free space (bytes): " + root.getFreeSpace());
			//System.out.println("Usable space (bytes): " + root.getUsableSpace());
		}
	}

	public static void DisplayMemory() {
		System.out.println("==== System Processors/Memory ====");
		/* Total number of processors or cores available to the JVM */
		System.out.println("Available processors (cores): "
				+ Runtime.getRuntime().availableProcessors());

		/* Total amount of free memory available to the JVM */
		System.out.println("Free memory (bytes): "
				+ Runtime.getRuntime().freeMemory());

		/* This will return Long.MAX_VALUE if there is no preset limit */
		long maxMemory = Runtime.getRuntime().maxMemory();
		/* Maximum amount of memory the JVM will attempt to use */
		System.out.println("Maximum memory (bytes): "
				+ (maxMemory == Long.MAX_VALUE ? "no limit" : maxMemory));

		/* Total memory currently available to the JVM */
		System.out.println("Total memory available to JVM (bytes): "
				+ Runtime.getRuntime().totalMemory());
	}

	public static void DisplaySystemProperties() {
		System.out.println("==== System Properties ====");
		Properties props = System.getProperties();
		Iterator<Object> enu = props.keySet().iterator();
		while (enu.hasNext()) {
			String next = enu.next().toString();
			System.out.print(next + "   =   ");
			System.out.println(props.getProperty(next));

		}
	}
	
	public static void DisplayNetwork() {
		System.out.println("==== Network Interface ====");
		Enumeration<NetworkInterface> interfaces;
		try {
			interfaces = NetworkInterface.getNetworkInterfaces();
			while (interfaces.hasMoreElements()) {
				  NetworkInterface interf = interfaces.nextElement();
				  if (interf.isUp() && !interf.isLoopback())
				    System.out.println(interf.toString()+interf.getInterfaceAddresses());
				}
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void DisplayAll() {
		DisplaySystemProperties();
		DisplayMemory();
		DisplayDiskSpace();		
		DisplayMXBean();
		DisplayNetwork();
	}

	public static void main(String[] args) {
				
		SystemInfo.DisplayAll();
	}

}
