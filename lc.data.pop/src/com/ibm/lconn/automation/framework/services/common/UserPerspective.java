package com.ibm.lconn.automation.framework.services.common;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.abdera.Abdera;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.commons.codec.binary.Base64;

import com.ibm.lconn.automation.framework.services.activities.ActivitiesService;
import com.ibm.lconn.automation.framework.services.authconnector.AuthConnectorService;
import com.ibm.lconn.automation.framework.services.blogs.BlogsService;
import com.ibm.lconn.automation.framework.services.catalog.CatalogService;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Component;
import com.ibm.lconn.automation.framework.services.communities.CommunitiesService;
import com.ibm.lconn.automation.framework.services.communities.ModerationService;
import com.ibm.lconn.automation.framework.services.cre.CREService;
import com.ibm.lconn.automation.framework.services.dogear.DogearService;
import com.ibm.lconn.automation.framework.services.files.FilesService;
import com.ibm.lconn.automation.framework.services.forums.ForumsService;
import com.ibm.lconn.automation.framework.services.gatekeeper.GateKeeperService;
import com.ibm.lconn.automation.framework.services.news.NewsService;
import com.ibm.lconn.automation.framework.services.opensocial.ActivitystreamsService;
import com.ibm.lconn.automation.framework.services.profiles.ProfilesService;
import com.ibm.lconn.automation.framework.services.profiles.admin.ProfilesAdminService;
import com.ibm.lconn.automation.framework.services.switchbox.SwitchBoxService;
import com.ibm.lconn.automation.framework.services.ublogs.UblogsService;
import com.ibm.lconn.automation.framework.services.wikis.WikisService;

public class UserPerspective {


	private String _email;
	private String _sPW;
	private String _key;
	private String _userId;
	private String _sUser;	
	private String _realName;
	
	private ServiceEntry _serviceEntry;
	
	private ActivitiesService _activitiesService;
	private ActivitystreamsService _asService;
	private AuthConnectorService _authConnectorService;
	private BlogsService _blogsService;
	private CatalogService _catalogService;
	private CommunitiesService _communitiesService;
	private CREService _CREService;
	private DogearService _dogearService;
	private GateKeeperService _gateKeeperService;
	private FilesService _filesService;
	private ForumsService _forumsService;
	private NewsService _newsService;
	private ProfilesService _profilesService;
	private ProfilesAdminService _profilesAdminService;
	private SwitchBoxService _switchboxService;
	private UblogsService _ublogsService;
	private WikisService _wikisService;
	private ModerationService _moderationService;
	private ServiceConfig config;
	
	private static String impersonationHeaderKey = "X-LConn-RunAs";
	private static String impersonationHeaderValue_userid = "userid";
	static boolean useSSL = true;
	private boolean anonymousUser = false;
	
	public UserPerspective(int userNumber, String compName) throws FileNotFoundException, IOException, LCServiceException{
		setUserService(userNumber, compName, -1);
	}
	
	public UserPerspective(int userNumber, String compName, int impersonatedUserNumber) throws FileNotFoundException, IOException, LCServiceException{
		if (userNumber == impersonatedUserNumber){
			setUserService(userNumber, compName, -1);
		} else {
			setUserService(userNumber, compName, impersonatedUserNumber);
		}
	}
	
	public UserPerspective(int userNumber, String compName, boolean useSSL) throws FileNotFoundException, IOException, LCServiceException{
		setUserService(userNumber, compName, useSSL, -1);
	}
	
	public UserPerspective(int userNumber, String compName, boolean useSSL, boolean isAnonymous) throws FileNotFoundException, IOException, LCServiceException{
		anonymousUser = isAnonymous;
		setUserService(userNumber, compName, useSSL, -1);
	}
	
	public UserPerspective(int userNumber, String compName, boolean useSSL, int impersonatedUserNumber) throws FileNotFoundException, IOException, LCServiceException{
		if (userNumber == impersonatedUserNumber){
			setUserService(userNumber, compName, useSSL, -1);
		} else {
			setUserService(userNumber, compName, useSSL, impersonatedUserNumber);
		}
	}
	
	public UserPerspective(String user, String pwd, String compName, boolean useSSL) throws FileNotFoundException, IOException, LCServiceException{
		setUserService(user, pwd, compName, useSSL);
	}
	
	private void setUserService(int userNumber, String compName, int impersonatedUserNumber)
			throws FileNotFoundException, IOException, LCServiceException{
		setUserService(userNumber, compName, useSSL, impersonatedUserNumber);
	}
	
	private void setUserService(String user, String pwd, String compName, boolean useSSL) throws LCServiceException{
		getClientAndService(-1, compName, useSSL, -1, user, pwd);
	}

	private void setUserService(int userNumber, String compName,
			boolean useSSL, int impersonatedUserNumber)
			throws FileNotFoundException, IOException, LCServiceException {
		ProfileData profileData = ProfileLoader.getProfile(userNumber);
		_userId = profileData.getUserId(); 
		_email = profileData.getEmail();
		_sUser = profileData.getUserName();
		_realName = profileData.getRealName();
		_sPW = profileData.getPassword();
		_key = profileData.getKey();

		getClientAndService(userNumber, compName, useSSL,
				impersonatedUserNumber, _email, _sPW);
	}

	private void getClientAndService(int userNumber, String compName,
			boolean useSSL, int impersonatedUserNumber, String _email, String _sPW) throws LCServiceException {
		Abdera abdera = new Abdera();
		AbderaClient client = new AbderaClient(abdera);
		AbderaClient.registerTrustManager();
		
		
		config = new ServiceConfig(client, URLConstants.SERVER_URL, useSSL, _email, _sPW);		
		_serviceEntry = config.getService(compName);	
		
		Map<String, String> headers = new HashMap<String, String>();
		if ( impersonatedUserNumber != -1 ){
			ProfileData impersonatedUserprofileData;
			try {
				impersonatedUserprofileData = ProfileLoader.getProfile(impersonatedUserNumber);
				headers.put(impersonationHeaderKey, impersonationHeaderValue_userid + "=" + impersonatedUserprofileData.getUserId()+ ",excludeRole=admin");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// did in  ServiceConfig, still need here to pass in components service
		if ( StringConstants.AUTHENTICATION.equalsIgnoreCase(StringConstants.Authentication.BASIC.toString()) ) {		
			String auth = _email+":"+_sPW;
			headers.put("Authorization", "Basic "+new String(Base64.encodeBase64(auth.getBytes())));
		}
		
		//If an anonymous user is needed, remove the credentials and headers.
		if (anonymousUser){
			headers.clear();
			client.clearState();
		}
		
		if(compName.equalsIgnoreCase(Component.ACTIVITIES.toString())) {
			_activitiesService = new ActivitiesService(client, _serviceEntry, headers);
		} else if(compName.equalsIgnoreCase(Component.AUTHCONNECTOR.toString())) {
			_authConnectorService =  new AuthConnectorService(client, _serviceEntry, headers);
		} else if(compName.equalsIgnoreCase(Component.BLOGS.toString())) {
			_blogsService = new BlogsService(client, _serviceEntry, headers);
			_moderationService =  new ModerationService(client, _serviceEntry, URLConstants.BLOGS_MODERATION_SERVICES);
		} else if(compName.equalsIgnoreCase(Component.COMMUNITIESCATALOG.toString())) {
			_catalogService =  new CatalogService(client);
		} else if(compName.equalsIgnoreCase(Component.COMMUNITIES.toString())) {
			_communitiesService =  new CommunitiesService(client, _serviceEntry, headers);
			_catalogService =  new CatalogService(client);
			
		} else if(compName.equalsIgnoreCase(Component.DOGEAR.toString())) {
			_dogearService = new DogearService (client,_serviceEntry, headers);
		} else if(compName.equalsIgnoreCase(Component.PROFILES.toString())) {
			_profilesService = new ProfilesService (client,_serviceEntry, headers);	
			if  ( _email.equalsIgnoreCase(StringConstants.ADMIN_USER_EMAIL)){
				_profilesAdminService = new ProfilesAdminService (client,_serviceEntry, headers);
				_gateKeeperService = new GateKeeperService (client,_serviceEntry, headers);
				_switchboxService = new SwitchBoxService (client,_serviceEntry, headers);
			}
		} else if(compName.equalsIgnoreCase(Component.FORUMS.toString())) {
			_forumsService = new ForumsService (client,_serviceEntry, headers);
		} else if(compName.equalsIgnoreCase(Component.FILES.toString())) {
			_filesService = new FilesService (client,_serviceEntry, headers);
			if (StringConstants.MODERATION_ENABLED && userNumber == StringConstants.CONNECTIONS_ADMIN_USER ){
				_moderationService =  new ModerationService(client, _serviceEntry, URLConstants.FILES_MODERATION_SERVICES);
			}
		} else if(compName.equalsIgnoreCase(Component.NEWS.toString())) {
			_newsService = new NewsService(client, _serviceEntry, headers);
		} else if(compName.equalsIgnoreCase(Component.WIKIS.toString())) {
			_wikisService = new WikisService(client, _serviceEntry, headers);
		} else if( compName.equalsIgnoreCase(Component.MICROBLOGGING.toString())) {
			_ublogsService = new UblogsService(client, _serviceEntry, headers);
		} else if(compName.equalsIgnoreCase(Component.OPENSOCIAL.toString())) {
			_asService = new ActivitystreamsService(client, _serviceEntry, headers);
			_CREService = new CREService(client, _serviceEntry, headers);
		//} else if(compName.equalsIgnoreCase(Component.SEARCH.toString())) {
		//	_searchService =  new SearchService(client);
		}
	}
	
	// for Files restore only
	public UserPerspective(String user, String pwd, String compName, String impersonatedUserId) throws FileNotFoundException, IOException, LCServiceException{
		//setUserService(user, pwd, compName, useSSL);
		getClientAndService(-1, compName, useSSL, impersonatedUserId, user, pwd);
	}
	
	// for Files restore only 
	private void getClientAndService(int userNumber, String compName,
			boolean useSSL, String impersonatedUserId, String _email, String _sPW) throws LCServiceException {
		Abdera abdera = new Abdera();
		AbderaClient client = new AbderaClient(abdera);
		AbderaClient.registerTrustManager();
		
		
		config = new ServiceConfig(client, URLConstants.SERVER_URL, useSSL, _email, _sPW);		
		_serviceEntry = config.getService(compName);	
		
		Map<String, String> headers = new HashMap<String, String>();
		if ( impersonatedUserId != null ){
			headers.put(impersonationHeaderKey, impersonationHeaderValue_userid + "=" + impersonatedUserId+ ",excludeRole=admin");	
		}

		// did in  ServiceConfig, still need here to pass in components service
		if ( StringConstants.AUTHENTICATION.equalsIgnoreCase(StringConstants.Authentication.BASIC.toString()) ) {		
			String auth = _email+":"+_sPW;
			headers.put("Authorization", "Basic "+new String(Base64.encodeBase64(auth.getBytes())));
		}
		
		if(compName.equalsIgnoreCase(Component.ACTIVITIES.toString())) {
			_activitiesService = new ActivitiesService(client, _serviceEntry, headers);
		} else if(compName.equalsIgnoreCase(Component.AUTHCONNECTOR.toString())) {
			_authConnectorService =  new AuthConnectorService(client, _serviceEntry, headers);
		} else if(compName.equalsIgnoreCase(Component.BLOGS.toString())) {
			_blogsService = new BlogsService(client, _serviceEntry, headers);
			_moderationService =  new ModerationService(client, _serviceEntry, URLConstants.BLOGS_MODERATION_SERVICES);
		} else if(compName.equalsIgnoreCase(Component.COMMUNITIESCATALOG.toString())) {
			_catalogService =  new CatalogService(client);
		} else if(compName.equalsIgnoreCase(Component.COMMUNITIES.toString())) {
			_communitiesService =  new CommunitiesService(client, _serviceEntry, headers);
			_catalogService =  new CatalogService(client);
			
		} else if(compName.equalsIgnoreCase(Component.DOGEAR.toString())) {
			_dogearService = new DogearService (client,_serviceEntry, headers);
		} else if(compName.equalsIgnoreCase(Component.PROFILES.toString())) {
			_profilesService = new ProfilesService (client,_serviceEntry, headers);	
			if  ( _email.equalsIgnoreCase(StringConstants.ADMIN_USER_EMAIL)){
				_profilesAdminService = new ProfilesAdminService (client,_serviceEntry, headers);
				_gateKeeperService = new GateKeeperService (client,_serviceEntry, headers);
				_switchboxService = new SwitchBoxService (client,_serviceEntry, headers);
			}
		} else if(compName.equalsIgnoreCase(Component.FORUMS.toString())) {
			_forumsService = new ForumsService (client,_serviceEntry, headers);
		} else if(compName.equalsIgnoreCase(Component.FILES.toString())) {
			_filesService = new FilesService (client,_serviceEntry, headers);
			if (StringConstants.MODERATION_ENABLED && userNumber == StringConstants.CONNECTIONS_ADMIN_USER ){
				_moderationService =  new ModerationService(client, _serviceEntry, URLConstants.FILES_MODERATION_SERVICES);
			}
		} else if(compName.equalsIgnoreCase(Component.NEWS.toString())) {
			_newsService = new NewsService(client, _serviceEntry, headers);
		} else if(compName.equalsIgnoreCase(Component.WIKIS.toString())) {
			_wikisService = new WikisService(client, _serviceEntry, headers);
		} else if( compName.equalsIgnoreCase(Component.MICROBLOGGING.toString())) {
			_ublogsService = new UblogsService(client, _serviceEntry, headers);
		} else if(compName.equalsIgnoreCase(Component.OPENSOCIAL.toString())) {
			_asService = new ActivitystreamsService(client, _serviceEntry, headers);
			_CREService = new CREService(client, _serviceEntry, headers);
		//} else if(compName.equalsIgnoreCase(Component.SEARCH.toString())) {
		//	_searchService =  new SearchService(client);
		}
	}
	
	public String getPassword() {
		return _sPW;
	}

	public ServiceEntry getServiceEntry() {
		return _serviceEntry;
	}

	public String getUserId() {
		return _userId;
	}

	public String getEmail(){
		return _email;
	}
	
	public String getKey() {
		return _key; 
	}
	
	public String getUserName(){
		return _sUser;
	}
	
	public String getRealName() {
		return _realName;
	}

	public ActivitiesService getActivitiesService() {
		return _activitiesService;
	}

	public AuthConnectorService getAuthConnectorService() {
		return _authConnectorService;
	}
	
	public ActivitystreamsService getAsService() {
		return _asService;
	}
	
	public BlogsService getBlogsService() {
		return _blogsService;
	}

	public CatalogService getCatalogService() {
		return _catalogService; 
	}
	
	public CommunitiesService getCommunitiesService() {
		return _communitiesService; 
	}
	
	public CREService getCREService() {
		return _CREService; 
	}
	
	public DogearService getDogearService() {
		return _dogearService;
	}
	
	public GateKeeperService getGateKeeperService() {
		return _gateKeeperService;
	}
	
	public NewsService getNewsService() {
		return _newsService;
	}

	public FilesService getFilesService() {
		return _filesService;
	}

	public ForumsService getForumsService() {
		return _forumsService;
	}
	
	public ProfilesService getProfilesService() {
		return _profilesService;
	}
	
	public ProfilesAdminService getProfilesAdminService() {
		return _profilesAdminService;
	}
	
	public SwitchBoxService getSwitchBoxService() {
		return _switchboxService;
	}

	public WikisService getWikisService() {
		return _wikisService;
	}

	public UblogsService getUblogsService() {
		return _ublogsService;
	}
		
	public ModerationService getModerationService() {
		return _moderationService;
	}
	
	public ServiceConfig getServiceConfig() {
		return config;
	}

}