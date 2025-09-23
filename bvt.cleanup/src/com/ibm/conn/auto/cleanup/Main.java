/**
 * 
 */
package com.ibm.conn.auto.cleanup;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.text.ParseException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Service;
import org.apache.abdera.model.Workspace;
import org.apache.abdera.model.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.lconn.automation.framework.services.activities.ActivitiesService;
import com.ibm.lconn.automation.framework.services.activities.nodes.Activity;
import com.ibm.lconn.automation.framework.services.blogs.BlogsService;
import com.ibm.lconn.automation.framework.services.blogs.nodes.Blog;
import com.ibm.lconn.automation.framework.services.common.LCServiceException;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.SortBy;
import com.ibm.lconn.automation.framework.services.common.StringConstants.SortField;
import com.ibm.lconn.automation.framework.services.common.StringConstants.SortOrder;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.common.UserPerspective;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Component;
import com.ibm.lconn.automation.framework.services.communities.CommunitiesService;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.dogear.DogearService;
import com.ibm.lconn.automation.framework.services.files.FilesService;
import com.ibm.lconn.automation.framework.services.forums.ForumsService;
import com.ibm.lconn.automation.framework.services.profiles.ProfilesService;
import com.ibm.lconn.automation.framework.services.wikis.WikisService;


public class Main {
	
	private static Logger LOGGER = LoggerFactory.getLogger(Main.class.getName());
	
	private static String serverUrl = null;
	private static String userFile = null;
	private static boolean onPrem = true;
	// The size of the pages to request from the api services
	private static int pageSize = 250;
	// The number of asserts to keep per cleaned assert group
	private static int keepAmount = 2;
	
	private static Users users;
	
	enum ParamKey { None, ServerUrl, UserFilePath, PageSize, KeepAmount}; 

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Parse the arguments
		parseArgs(args);
		
		// Init 
		initializeEnv();
		
		// Load the users from the provided propteries file
		loadUsers();
		
		// Start the process of cleaning up the users
		cleanUsers();
	}

	/**
	 * Given the arguments passed to the program, read them and assign them to
	 * internal setting fields.
	 * @param args The arguments passed to the main method
	 */
	private static void parseArgs(String[] args){
		ParamKey paramKey = ParamKey.None;
		
		for(String arg : args){
			if(arg.equalsIgnoreCase("-c") || arg.equalsIgnoreCase("--cloud"))
				onPrem = false;
			else if(arg.equals("-p") || arg.equalsIgnoreCase("--onprem"))
				onPrem = true;
			else if(arg.equalsIgnoreCase("-s") || arg.equalsIgnoreCase("--serverurl"))
				paramKey = ParamKey.ServerUrl;
			else if(arg.equalsIgnoreCase("-u") || arg.equalsIgnoreCase("--users"))
				paramKey = ParamKey.UserFilePath;
			else if(arg.equals("-P") || arg.equalsIgnoreCase("--pagesize"))
				paramKey = ParamKey.PageSize;
			else if(arg.equalsIgnoreCase("-k") || arg.equalsIgnoreCase("--keepamount"))
				paramKey = ParamKey.KeepAmount;
			else if(arg.equalsIgnoreCase("-h") || arg.equalsIgnoreCase("--help") || arg.equalsIgnoreCase("-?"))
				displayHelpMessage();
			else if(paramKey != ParamKey.None){
				switch (paramKey){
				case ServerUrl:
					serverUrl = arg;
					break;
				case UserFilePath:
					userFile = arg;
					break;
				case PageSize:
					pageSize = Integer.parseInt(arg);
					break;
				case KeepAmount:
					keepAmount = Integer.parseInt(arg);
					break;
				}
				paramKey = ParamKey.None;
			} else
				displayHelpMessage();
		}
		
		if(serverUrl == null || userFile == null){
			LOGGER.info("Missing server url or user file. Both of these need to be defined.");
			displayHelpMessage();
		} else if (pageSize < keepAmount){
			LOGGER.info("The passed page size is smaller than the keep amount size. Please fix these parameters and run again.");
			displayHelpMessage();
		}
	}
	
	/**
	 * Simple helper method to display the help message to the command prompt.
	 * After the message is printed to the terminal, the program exits with
	 * an exit return value of 0.
	 */
	private static void displayHelpMessage(){
		LOGGER.info("Usage: java -jar bvt.cleanup.jar [Options]");
		LOGGER.info("Example:");
		LOGGER.info("\tjava -jar bvt.cleanup.jar -c -p config/daily.csv -s http://apps.collabservdaily.swg.usma.ibm.com");
		LOGGER.info("Options:");
		LOGGER.info("\t-c, --cloud\tRunning against a cloud server");
		LOGGER.info("\t-p, --onprem\tRunning against an onprem server (default)");
		LOGGER.info("\t-s, --serverurl\tDefine the server url to run against [REQUIRED]");
		LOGGER.info("\t-u, --users\tDefine the user properties file path [REQUIRED]");
		LOGGER.info("\t-P, --pagesize\tDefine the size of pages to request from the server. The default is 250  This must be at least as large as the keep amount.[OPTIONAL]");
		LOGGER.info("\t-k, --keepamount\tDefine the number of items to keep from each component. The default is 0 [OPTIONAL]");
		LOGGER.info("\t-h,-?, --help\tDisplay this help message");
		System.exit(0);
	}

	private static void initializeEnv() {
		URLConstants.SERVER_URL = serverUrl;

		StringConstants.DEPLOYMENT_TYPE = onPrem == true ?
										  StringConstants.DeploymentType.ON_PREMISE :
										  StringConstants.DeploymentType.SMARTCLOUD;		
	}

	/**
	 * Read in from disk the users to clean up.
	 */
	private static void loadUsers() {
		LOGGER.info("Loading users...");
		
		users = new Users();
		try {
			users.loadUsers(userFile);
		} catch (IOException e) {
			LOGGER.error("There was an error while reading from the file: " + userFile);
			e.printStackTrace();
			System.exit(2);
		}
		

		LOGGER.info("Loaded users: " + users.getUsers().size());
	}
	
	/**
	 * Clean up the users loaded. This will entail:
	 * <ul>
	 * 	<li>Delete created communities</li>
	 * 	<li>Delete activities</li>
	 * 	<li>Delete files uploaded</li>
	 * 	<li><b>On Prem Only:</b> Delete Wikis</li>
	 * 	<li><b>On Prem Only:</b> Delete Forums</li>
	 * 	<li><b>On Prem Only:</b> Delete Blogs</li>
	 * </ul>
	 */
	private static void cleanUsers() {
		ArrayList<Future<?>> futures = new ArrayList<Future<?>>();
		// 4 * Procs because the main bottleneck is waiting on network responses
		// not cpu work.
		int idealThreadCount = 4 * Runtime.getRuntime().availableProcessors();
		
		// If there are less users than the ideal thread count, then only make
		// as many threads as users. There is no need created extra threads which
		// won't be used.
		ExecutorService pool = Executors.newFixedThreadPool(
												idealThreadCount < users.getUsers().size() ?
												idealThreadCount : users.getUsers().size());
		LOGGER.info("Starting clean up process...");
		for(User user : users.getUsers()){
			futures.add(
				pool.submit(new Runnable(){
					User user;
					
					Runnable setUser(User user){
						this.user = user;
						return this;
					}
					
					@Override
					public void run() {
						deleteCommunities(user);
						deleteActivities(user);
						deleteFiles(user);
						deleteProfilesConnections(user);
						if(onPrem){
							deleteWikis(user);
							deleteForums(user);
							deleteBlogs(user);
							deleteBookmarks(user);
						}
					}
				}.setUser(user)));
		}
		
		for(Future<?> future : futures){
			while(!future.isDone()){
				try {
					// Sleep for a 1 minute and check again
					Thread.sleep(60 * 1000);
				} catch (InterruptedException e) { }
			}
		}
		
		pool.shutdown();
		
		LOGGER.info("Finished cleaning up users");
	}

	/**
	 * Delete communities from the given user.
	 * @param user The user for the communities to delete from
	 * @throws ParseException 
	 */
	private static void deleteCommunities(User user) {
		LOGGER.info("****************************************************" );
		LOGGER.info("Deleting all communties from user: " + user.getEmail());
		LOGGER.info("****************************************************" );
		
		UserPerspective userPer = null;
		try {
			userPer = user.getUserPerspective(Component.COMMUNITIES.toString());
		} catch (IOException e) {
			LOGGER.error("Could not login with user: " + user.getEmail() +
						 " for " + Component.COMMUNITIES.toString());
			e.printStackTrace();
			return;
		} catch (LCServiceException e) {
			LOGGER.error("Could not login with user: " + user.getEmail() +
					 	 " for " + Component.COMMUNITIES.toString());
			e.printStackTrace();
			return;
		}
		
		
		CommunitiesService service = userPer.getCommunitiesService();
		int communitiesDeleted = 0;
		int communitiesFailed = 0;
		// Continue looping, getting communities by the 1000's.
		while(true){
			communitiesFailed = 0;
			// Sort by older first, that way we delete the oldest communities
			Feed userFeed = (Feed) service.getMyCommunities(true, null, 0,
									pageSize, null, null,null, null, null);
			
			if(userFeed == null){
				LOGGER.error("Couldn't retrieve the communities list");
				break;
			}
						
			if( communitiesFailed >= userFeed.getEntries().size()){
				break;
			}
						
			// TJB 8/18/15 Using purgeCommunity for hard delete (permanent).
			Community community;
			for (int i = 0; i < userFeed.getEntries().size(); i++) {
				community = new Community(userFeed.getEntries().get(i));
				if (service.purgeCommunity(community.getLinks()
											.get(StringConstants.REL_EDIT + ":"
													+ StringConstants.MIME_NULL).getHref()
											.toString())){
					communitiesDeleted++;
				} else {
					communitiesFailed++;
				}
			}
			

			if(communitiesFailed >= userFeed.getEntries().size()){
				LOGGER.warn("Couldn't delete all communities due to errors or an empty feed.");
				break;
			}
		} // End while for deleting Communities
		
		LOGGER.info("Number of deleted communities: " + communitiesDeleted);

		// Clean trash.  Even though we scrub the user's active communities, some 
		// communities still accumulate in the trash.  Here's code to clean
		// those.
		LOGGER.info("Clearing user's Communities Trash view. . . .");
		boolean done = false;
		while (!done){
			LOGGER.info("Get a feed of current user's community trash: ");
			Feed user2Feed = (Feed) service.getAnyFeed(URLConstants.SERVER_URL + "/communities/service/atom/communities/trash?ps=200");
			
			if ( user2Feed.getEntries().size() < 20 ){
				done = true;	
			}
		
			ArrayList<Community> user2Communities = new ArrayList<Community>();
			for (Entry communityEntry : user2Feed.getEntries()) {
				user2Communities.add(new Community(communityEntry));
			}

			/* Purge the community - this is a hard delete, the community 
			 * will be deleted in the database. 
			 */ 
			for (Community community : user2Communities) {
				if (service.purgeCommunity(community
						.getLinks()
						.get(StringConstants.REL_EDIT + ":"
								+ StringConstants.MIME_NULL).getHref()
						.toString()))
					LOGGER.info("Successfully deleted community in trash "
							+ community.getTitle());
				else
					LOGGER.info("Failed deleting community in trash "
							+ community.getTitle());
			}
		}		
		
		LOGGER.info("Finished deleting community trash. ");
		
		service.tearDown();
	}

	private static void deleteActivities(User user) {
		LOGGER.info("****************************************************" );
		LOGGER.info("Deleting all Activities from user: " + user.getEmail());
		LOGGER.info("****************************************************" );
		
		UserPerspective userPer = null;
		try {
			userPer = user.getUserPerspective(Component.ACTIVITIES.toString());
		} catch (IOException e) {
			LOGGER.error("Could not login with user: " + user.getEmail() +
						 " for " + Component.ACTIVITIES.toString());
			e.printStackTrace();
			return;
		} catch (LCServiceException e) {
			LOGGER.error("Could not login with user: " + user.getEmail() +
					 	 " for " + Component.ACTIVITIES.toString());
			e.printStackTrace();
			return;
		}
		
		ActivitiesService service = userPer.getActivitiesService();
		
		int deletedActivities = 0;
		int failedToDeleteActivities = 0;
		ArrayList<Activity> activities;
		while(true){
			failedToDeleteActivities = 0;
			activities = service.getMyActivities(null, null, null, 0, pageSize, null, null, null, SortField.CREATED,
					SortOrder.ASC, null, null, null, null, null, null);
			if (activities == null) {
				LOGGER.info("Couldn't retrieve activities");
				break;
			} else if (activities.size() <= 0){
				break;
			}
	
			for (int i = 0; i < activities.size(); i++) {
				Activity activity = activities.get(i);
				// Make sure we have permissions to delete activity before
				// trying to delete it
				if (activity.getPermissions().contains(
						StringConstants.PERMISSIONS_DELETE_ACTIVITY)) {
					LOGGER.info("Deleting activity: " + activity.getTitle());
					if (service.deleteActivity(activity.getEditHref())) {
						deletedActivities++;
					} else {
						failedToDeleteActivities++;
					}
				} else {
					failedToDeleteActivities++;
					LOGGER.info("Insufficient permissions to delete Activity: "
							+ activity.getTitle());
				}
			}
			
			if(failedToDeleteActivities >= activities.size()){
				LOGGER.warn("Couldn't finish cleaning up activities due to errors or empty feed.");
				break;
			}
		}

		LOGGER.info("Deleted Activities for user: " + user.getEmail());
		LOGGER.info("Number of deleted Activities: " + deletedActivities);
		LOGGER.info("Number of failed Activities deletion attempts: " + failedToDeleteActivities);
		
		service.tearDown();		
	}

	private static void deleteFiles(User user) {
		/* Cleaning up files is a two stage process.  The files
		 * must be deleted for each user, then deleted again from the trash.
		 * The documentation does not mention a hard delete from a user's 
		 * active view.  So, first let's loop through a user's files and 
		 * delete them all, then issue a single call to purge them from the 
		 * trash.
		 */
		LOGGER.info("****************************************************" );
		LOGGER.info("Deleting all files from user: " + user.getEmail());
		LOGGER.info("****************************************************" );
		
		UserPerspective userPer = null;
		try {
			userPer = user.getUserPerspective(Component.FILES.toString());
		} catch (IOException e) {
			LOGGER.error("Could not login with user: " + user.getEmail() +
						 " for " + Component.FILES.toString());
			e.printStackTrace();
			return;
		} catch (LCServiceException e) {
			LOGGER.error("Could not login with user: " + user.getEmail() +
					 	 " for " + Component.FILES.toString());
			e.printStackTrace();
			return;
		}
		
		FilesService service = userPer.getFilesService();
		
		String lnk = "";
		int deletedFiles = 0;
		int failedToDeleteFiles = 0;
		String myLibUrl = URLConstants.SERVER_URL+"/files/basic/api/myuserlibrary/feed?ps=200";
		Feed myFilesFeed = (Feed) service.getMyFeed(myLibUrl);
		int feedSize = myFilesFeed.getEntries().size();
		// This first loop is the soft delete.  These files will transfer to the Trash view
		// Keep looping while the entry size is less than the number of failed deletes.
		while((deletedFiles + failedToDeleteFiles) < feedSize ) {
			for (Entry e : myFilesFeed.getEntries()) {
				try {
					lnk = e.getEditLinkResolvedHref().toURL().toString();
				} catch (Exception e1 ) {
					e1.printStackTrace();
				}
				LOGGER.info("Deleting file: " + e.getTitle());
				if (service.deleteFileFullUrl(lnk)){
					deletedFiles++;
				} else {
					failedToDeleteFiles++;
				}
			}
			
			/* This next call purges all deleted files from the trash.
			 * Purge in batches otherwise, there could be 1000's of items 
			 * to purge - and that might create timeout errors.
			 */
			LOGGER.info("Single call to purge all items in the trash.");
			service.purgeAllFilesFromTrash();

			//  Generate the feed again.  Maybe there's more to delete.
			myFilesFeed = (Feed) service.getMyFeed(myLibUrl);
			feedSize = myFilesFeed.getEntries().size();
		}
		LOGGER.info("Deleted files from user: " + user.getEmail());
		LOGGER.info("Number of deleted files: " + deletedFiles);
		LOGGER.info("Number of unsuccessful delete attempts: " + failedToDeleteFiles);
		

		
		// Deleted created folders next
		LOGGER.info("Deleting folders. . . .");
		int deletedFolders = 0;
		int failedToDeleteFolders = 0;
		String editLnk = "";
		Feed folderFeed = (Feed) service.getFoldersFeed();
		int folderFeedSize = folderFeed.getEntries().size();

		// This first loop is the soft delete.  These files will transfer to the Trash view
		// Keep looping while the entry size is less than the number of failed deletes.
		while ((deletedFolders + failedToDeleteFolders) < folderFeedSize) {
			for(Entry ntry : folderFeed.getEntries()){
				try {
					editLnk = ntry.getEditLinkResolvedHref().toURL().toString();
					LOGGER.info("Deleting folder: " + ntry.getTitle());
					if(service.deleteFileFullUrl(editLnk)) {						
						deletedFolders++;
					} else {
						failedToDeleteFolders++;
					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}		
			}
			
			// Get the feed again in case there are more folders to delete.  Feed depth is about 100.
			folderFeed = (Feed) service.getFoldersFeed();
			folderFeedSize = folderFeed.getEntries().size();
		}
		LOGGER.info("Deleted folders from user: " + user.getEmail());
		LOGGER.info("Number of deleted folders: " + deletedFolders);
		LOGGER.info("Number of unsuccessful delete attempts to delete folders: " + failedToDeleteFolders);
		
		service.tearDown();
	}

	private static void deleteWikis(User user) {
		LOGGER.info("****************************************************" );
		LOGGER.info("Deleting all wikis from user: " + user.getEmail());
		LOGGER.info("****************************************************" );
		
		UserPerspective userPer = null;
		try {
			userPer = user.getUserPerspective(Component.WIKIS.toString());
		} catch (IOException e) {
			LOGGER.error("Could not login with user: " + user.getEmail() +
						 " for " + Component.WIKIS.toString());
			e.printStackTrace();
			return;
		} catch (LCServiceException e) {
			LOGGER.error("Could not login with user: " + user.getEmail() +
					 	 " for " + Component.WIKIS.toString());
			e.printStackTrace();
			return;
		}
		
		WikisService service = userPer.getWikisService();
		
		int deletedWikis = 0;
		int failedToDeleteWikis = 0;
		while(true){
			failedToDeleteWikis = 0;
			Feed myWikis = (Feed) service.getMyWikisFeed();
			List<Entry> wikis = myWikis.getEntries();
			if((wikis == null) || (wikis.size() <= 0)){
				break;
			}
			
			for (Entry ntry : myWikis.getEntries()) {
				LOGGER.info("Deleting Wiki: " +  ntry.getTitle());
				if(service.deleteWiki(ntry.getEditLinkResolvedHref().toString())){
					deletedWikis++;
				} else {
					failedToDeleteWikis++;
				}
			}
			
			if(failedToDeleteWikis >= wikis.size()){
				LOGGER.warn("Cleaning may be incomplete as there are wikis that can not be deleted.");
				break;
			}
			
		}
		
		LOGGER.info("Deleted Wikis for user: " + user.getEmail());
		LOGGER.info("Number of deleted wikis: " + deletedWikis);
		LOGGER.info("Number of wikis that failed deletion: " + failedToDeleteWikis);
		service.tearDown();
		
	}

	private static void deleteForums(User user) {
		LOGGER.info("****************************************************" );
		LOGGER.info("Deleting all forums from user: " + user.getEmail());
		LOGGER.info("****************************************************" );
		
		UserPerspective userPer = null;
		try {
			userPer = user.getUserPerspective(Component.FORUMS.toString());
		} catch (IOException e) {
			LOGGER.error("Could not login with user: " + user.getEmail() +
						 " for " + Component.FORUMS.toString());
			e.printStackTrace();
			return;
		} catch (LCServiceException e) {
			LOGGER.error("Could not login with user: " + user.getEmail() +
					 	 " for " + Component.FORUMS.toString());
			e.printStackTrace();
			return;
		}
		
		ForumsService service = userPer.getForumsService();
		
		int deletedForums = 0;
		int failedToDeleteForums = 0;
		while(true){
			failedToDeleteForums = 0;
			Feed userFeed = (Feed)service.getMyForums(null, null, 0, pageSize, null,
													  SortBy.CREATEDBY, SortOrder.ASC,
													  null, null, null, null);
			
			if(userFeed == null){
				LOGGER.error("Couldn't retrieve the forums list");
				break;
			} else if(userFeed.getEntries().size() <= 0){
				break;
			}
	

			for (Entry ntry : userFeed.getEntries()) {
					try {
						LOGGER.info("Deleting forum: " + ntry.getTitle() );
						if (service.deleteForum(ntry.getEditLinkResolvedHref().toURL().toString())){
							deletedForums++;
						}
						else {
							failedToDeleteForums++;
						}
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (URISyntaxException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
			
			if( failedToDeleteForums >= userFeed.getEntries().size() ){
				LOGGER.warn("Couldn't delete all forums due to failures or empty");
				break;
			}			
		}

		LOGGER.info("Forums deleted for user: " + user.getEmail());
		LOGGER.info("Number of deleted forums: " + deletedForums);
		LOGGER.info("Number of forums that failed to delete: " + failedToDeleteForums);
		
		service.tearDown();		
	}
	
	private static void deleteBlogs(User user) {
		LOGGER.info("****************************************************" );
		LOGGER.info("Deleting all blogs from user: " + user.getEmail());
		LOGGER.info("****************************************************" );
		
		UserPerspective userPer = null;
		try {
			userPer = user.getUserPerspective(Component.BLOGS.toString());
		} catch (IOException e) {
			LOGGER.error("Could not login with user: " + user.getEmail() +
						 " for " + Component.BLOGS.toString());
			e.printStackTrace();
			return;
		} catch (LCServiceException e) {
			LOGGER.error("Could not login with user: " + user.getEmail() +
					 	 " for " + Component.BLOGS.toString());
			e.printStackTrace();
			return;
		}
		
		BlogsService service = userPer.getBlogsService();
		
		int deletedBlogs = 0;
		int failedToDeleteBlogs = 0;
		while(true){
			failedToDeleteBlogs = 0;
			
			ArrayList<Blog> blogs = service.getMyBlogs(null, null, 0, pageSize, null,
													   null, SortBy.CREATEDBY,
													   SortOrder.ASC, null, null,
													   user.getUID());
			
			if(blogs == null){
				LOGGER.error("Couldn't retrieve the blogs list");
				break;
			} else if(blogs.size() <= 0){
				break;
			}
	
	
			Blog blog;
			for (int i = 0; i < blogs.size(); i++) {
				blog = blogs.get(i);
				LOGGER.info("Deleting blog: " + blog.getTitle());
				LOGGER.info("Deleting blog: HANDLE: " + blog.getHandleElement().getText());
				if (service.deleteBlog(blog.getEditHref())){
					deletedBlogs++;
				}
				else {
					failedToDeleteBlogs++;
				}
			}
			
			if(failedToDeleteBlogs >= blogs.size()){
				LOGGER.warn("Couldn't delete all forums due to errors or empty feed");
				break;
			}
			
		}
		
		LOGGER.info("Deleted blogs for user: " + user.getEmail());
		LOGGER.info("Number of deleted blogs: " + deletedBlogs);
		LOGGER.info("Number of failed blogs deletion attempts: " + failedToDeleteBlogs);
		
		service.tearDown();

	}
	
	private static void deleteProfilesConnections(User user) {
		LOGGER.info("****************************************************" );
		LOGGER.info("Deleting all Contacts and Connections for user: " + user.getEmail());
		LOGGER.info("****************************************************" );
		
		
		UserPerspective userPer = null;
		try {
			userPer = user.getUserPerspective(Component.PROFILES.toString());
		} catch (IOException e) {
			LOGGER.error("Could not login with user: " + user.getEmail() +
						 " for " + Component.PROFILES.toString());
			e.printStackTrace();
			return;
		} catch (LCServiceException e) {
			LOGGER.error("Could not login with user: " + user.getEmail() +
					 	 " for " + Component.PROFILES.toString());
			e.printStackTrace();
			return;
		}
		
		ProfilesService service = userPer.getProfilesService();
		
		//1 Get the current user's service document - /atom/profileService.do
		String colleagueUrl = "";
		String serviceUrl = URLConstants.SERVER_URL + "/profiles/atom/profileService.do";
		Service srvcDoc = (Service) service.getAnyFeed(serviceUrl);
		for (Workspace wrkspc : srvcDoc.getWorkspaces()){
			for (Element el : wrkspc.getElements()) {
				if(el.toString().startsWith("<atom:link")){
					if (el.getAttributeValue("rel").equalsIgnoreCase("http://www.ibm.com/xmlns/prod/sn/connections/colleague")){
						LOGGER.info("Found it " + el.getAttributeValue("href"));
						colleagueUrl = el.getAttributeValue("href");
					}
				}
			}
		}
		
		// Step 2 Get feed of user's contacts.  Get 3 feeds for accepted, pending and unconfirmed invitations
		Feed acceptedFeed = (Feed) service.getAnyFeed(colleagueUrl+"&status=accepted");
		Feed pendedFeed = (Feed) service.getAnyFeed(colleagueUrl+"&status=pending");
		Feed unconfirmedFeed = (Feed) service.getAnyFeed(colleagueUrl+"&status=unconfirmed");
		
		//3a Loop through the entries, get the edit link - Accepted relationships
		String acceptedUrl = "";
		for(Entry ntry : acceptedFeed.getEntries()){
			for (Element el : ntry.getElements()) {
				if(el.toString().startsWith("<link")){
					if (el.getAttributeValue("rel").equalsIgnoreCase("edit")){
						LOGGER.info("Found edit link " + el.getAttributeValue("href"));
						acceptedUrl = el.getAttributeValue("href");
					}
				}
			}
			
			// Delete the network affiliation.
			LOGGER.info("DELETE ACCEPTED " + acceptedUrl);
			service.deleteAnyFeed(acceptedUrl);
		}
		
		//3b Loop through the entries, get the edit link - Pended relationships.  These are pending invites for the current user only.
		// These are NOT invites the current user sent to others
		String pendedUrl = "";
		for(Entry ntry : pendedFeed.getEntries()){
			for (Element el : ntry.getElements()) {
				if(el.toString().startsWith("<link")){
					if (el.getAttributeValue("rel").equalsIgnoreCase("edit")){
						LOGGER.info("Found edit link " + el.getAttributeValue("href"));
						pendedUrl = el.getAttributeValue("href");
					}
				}
			}
			
			// Delete the pended affiliation
			LOGGER.info("DELETE PENDED " + pendedUrl);
			service.deleteAnyFeed(pendedUrl);
		}
		
		//3c Loop through the entries, get the edit link - unconfirmed.  These are pending invites the current user sent to others.
		String unconfirmedUrl = "";
		for(Entry ntry : unconfirmedFeed.getEntries()){
			for (Element el : ntry.getElements()) {
				if(el.toString().startsWith("<link")){
					if (el.getAttributeValue("rel").equalsIgnoreCase("edit")){
						LOGGER.info("Found edit link " + el.getAttributeValue("href"));
						unconfirmedUrl = el.getAttributeValue("href");
					}
				}
			}
			
			// Delete the unconfirmed affiliation.
			LOGGER.info("DELETE UNCONFIRMED " + unconfirmedUrl);
			service.deleteAnyFeed(unconfirmedUrl);
		}
		
		//Contacts API currently broken, disabling this code TJB 10/1/15
		service.addRequestOption("Content-Type", "application/json");
		LOGGER.info("Request Header Content-Type is: " + service.getRequestOption("Content-Type"));
		//Remove contacts from the page.
		// .../mucontacts/api/contacts/delete
		String contactUrl = URLConstants.SERVER_URL + "/mycontacts/api/contacts/delete";
		
		//This is the actual call to remove a contact!
		//service.postFeed(contactUrl,null);

		
	}
	
	private static void deleteBookmarks(User user) {
		LOGGER.info("****************************************************" );
		LOGGER.info("Deleting all bookmarks from user: " + user.getEmail());
		LOGGER.info("****************************************************" );
		
		UserPerspective userPer = null;
		try {
			userPer = user.getUserPerspective(Component.DOGEAR.toString());
		} catch (IOException e) {
			LOGGER.error("Could not login with user: " + user.getEmail() +
						 " for " + Component.DOGEAR.toString());
			e.printStackTrace();
			return;
		} catch (LCServiceException e) {
			LOGGER.error("Could not login with user: " + user.getEmail() +
					 	 " for " + Component.DOGEAR.toString());
			e.printStackTrace();
			return;
		}
		
		DogearService service = userPer.getDogearService();
		
		int deletedBookmarks = 0;
		int failedToDeleteBookmarks = 0;
		while(true){
			failedToDeleteBookmarks = 0;
			Feed userFeed = (Feed)service.getMyBookmarks();
			
			if(userFeed == null){
				LOGGER.error("Couldn't retrieve the bookmarks list");
				break;
			} else if(userFeed.getEntries().size() <= 0){
				break;
			}
	

			for (Entry ntry : userFeed.getEntries()) {
				try {
					LOGGER.info("Deleting bookmarks: " + ntry.getTitle() );
					if (service.deleteBookmark(ntry.getEditLinkResolvedHref().toURL().toString())){
						deletedBookmarks++;
					}
					else {
						failedToDeleteBookmarks++;
					}
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if(failedToDeleteBookmarks >= userFeed.getEntries().size()){
				LOGGER.warn("Couldn't delete all Bookmarks due to failures or empty feed.");
				break;
			}			
		}

		LOGGER.info("Bookmarks deleted for user: " + user.getEmail());
		LOGGER.info("Number of deleted bookmarks: " + deletedBookmarks);
		LOGGER.info("Number of bookmarks that failed to delete: " + failedToDeleteBookmarks);
		
		service.tearDown();		
	}	
}
