package com.ibm.lconn.automation.datapop;

import java.io.IOException;
import java.util.Map;
//import java.util.Scanner;
//import java.io.File;
import org.w3c.dom.Element;

public class ServiceFactory {

	private BlogServiceFactory blogFactory;
	private ActivityServiceFactory activityFactory;
	private BookmarkServiceFactory bookmarkFactory;
	private ForumsServiceFactory forumsServiceFactory;
	private WikiServiceFactory wikiServiceFactory;
	private CommunityServiceFactory communityServiceFactory;
	
	private String url;
	private String uname;
	private String password;
	
	public ServiceFactory(String url, String uname, String password)
	{		
		this.url = url;
		this.uname = uname;
		this.password = password;
	}
	
	public ServiceFactory(Element e) 
	{
		this(e.getAttribute("url"),e.getAttribute("uname"),e.getAttribute("password"));
	}

	public ComponentService create(Element e) throws DataPopAdapterException
	{
		String[] blackListedHosts = 
		{
				//"lc30linux3",
				"w3-connections",
				"connections",
				"greenhouse",
				"apps"
		};
		
		for(String blacklistedHost : blackListedHosts)
		{			
			// Want to strip out the http:// prefix and .foo.bar.com suffix.
			String[] urlTokens = url.trim().toLowerCase().split("\\.");
			String urlHost = urlTokens[0];
			urlTokens = urlHost.split("/");
			urlHost = urlTokens[urlTokens.length - 1];
			System.out.println("Blacklisted Host: " + blacklistedHost);
			System.out.println("URL Host: " + urlHost);
			if(urlHost.equals(blacklistedHost))
			{
				String errMsg = "Requested URL (" + url + ") contains a blacklisted host.";
				System.out.println(errMsg);
				throw new DataPopAdapterException(errMsg);
			}
		}
/*
		// This commented out code is here just to show how we might implement
		// a file based blacklist rather than a hard-coded blacklist.
		// I had printed out a lot of env vars just to see what was available
		// for forming file pathnames.
		try
		{

			String currentDir = System.getProperty("user.dir");
			System.out.println("currentDir: " + currentDir);
						
			Map<String, String> envVars = System.getenv();
			for (String envVar : envVars.keySet())
			{
				System.out.println(envVar + ": " + envVars.get(envVar));
			}
			
			String userInstallRoot = System.getenv("USER_INSTALL_ROOT");
			String wasCellName = System.getenv("WAS_CELL");
			String blacklistFilePath = "\"" + userInstallRoot + "/installedApps/" + wasCellName + "/Data Population.ear/black_list.txt\"";
			//Scanner fileScanner = new Scanner(new File("com/ibm/lconn/automation/datapop/black_list.txt"));
			//Scanner fileScanner = new Scanner(new File("/tmp/black_list.txt"));
			Scanner fileScanner = new Scanner(new File(blacklistFilePath));
		
			while(fileScanner.hasNext())
			{
				String blacklistedURL = fileScanner.next();
				System.out.println("blacklistedURL: " + blacklistedURL);
				if(url.toLowerCase().contains(blacklistedURL.toLowerCase()))
				{
					System.out.println("Got blacklistedURL: " + blacklistedURL);
					return null;
				}
			}
			
			
		} 
		catch (IOException ex) 
		{
			ex.printStackTrace();
			return null;
		}
*/					
		if(e.getNodeName().equalsIgnoreCase("blog") ||
				e.getNodeName().equalsIgnoreCase("blogentry") ||
				e.getNodeName().equalsIgnoreCase("blogcomment"))
		{
			blogFactory = new BlogServiceFactory(url,uname,password);
			return blogFactory.create(e);
		}
		
		if(e.getNodeName().equalsIgnoreCase("activity") ||
				e.getNodeName().equalsIgnoreCase("section") ||
				e.getNodeName().equalsIgnoreCase("todo") ||
				e.getNodeName().equalsIgnoreCase("activityentry") ||
				e.getNodeName().equalsIgnoreCase("reply"))	
		{
			activityFactory = new ActivityServiceFactory(url,uname,password);
			return activityFactory.create(e);
		}
		
		if(e.getNodeName().equalsIgnoreCase("bookmark"))
		{
			bookmarkFactory = new BookmarkServiceFactory(url,uname,password);
			return bookmarkFactory.create(e);
		}
		
		if(e.getNodeName().equalsIgnoreCase("forum") ||
				e.getNodeName().equalsIgnoreCase("forumtopic") ||
				e.getNodeName().equalsIgnoreCase("forumreply"))
		{
			forumsServiceFactory = new ForumsServiceFactory(url,uname,password);
			return forumsServiceFactory.create(e);
		}
		
		if(e.getNodeName().equalsIgnoreCase("wiki") ||
				e.getNodeName().equalsIgnoreCase("wikipage"))
		{
			wikiServiceFactory = new WikiServiceFactory(url,uname,password);
			return wikiServiceFactory.create(e);
		}
	    
		if (e.getNodeName().equalsIgnoreCase("community"))
	    {
		    communityServiceFactory = new CommunityServiceFactory(url,uname,password);
			return communityServiceFactory.create(e);
	    }
		
		String errMsg = "Unknown component block: " + e.getNodeName();
		System.out.println(errMsg);
		
		return null;
	}
}
