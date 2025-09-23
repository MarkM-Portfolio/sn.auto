package com.ibm.lconn.automation.framework.search.rest.api.population;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ibm.lconn.automation.framework.search.rest.api.RestAPIUser;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils.Purpose;
import com.ibm.lconn.automation.framework.services.activities.nodes.Activity;
import com.ibm.lconn.automation.framework.services.activities.nodes.ActivityEntry;
import com.ibm.lconn.automation.framework.services.blogs.nodes.Blog;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.DeploymentType;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Permissions;
import com.ibm.lconn.automation.framework.services.common.nodes.Bookmark;
import com.ibm.lconn.automation.framework.services.common.nodes.LCEntry;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;
import com.ibm.lconn.automation.framework.services.forums.nodes.Forum;
import com.ibm.lconn.automation.framework.services.profiles.nodes.Profile;
import com.ibm.lconn.automation.framework.services.profiles.nodes.Status;
import com.ibm.lconn.automation.framework.services.search.data.Application;
import com.ibm.lconn.automation.framework.services.wikis.nodes.Wiki;

public class PopulatedData {

	private static PopulatedData _instance;

	private Map<Purpose, HashMap<Permissions, HashMap<Application, List<LCEntry>>>> _purpose2acl2App2LcEntry = new HashMap<Purpose, HashMap<Permissions, HashMap<Application, List<LCEntry>>>>();

	private RestAPIUser peopleFinderProfileUser;

	private ArrayList<String> peopleFinderTags;

	private PopulatedData() {
		// TODO Auto-generated constructor stub
	}

	public static PopulatedData getInstance() {
		if (_instance == null) {
			_instance = new PopulatedData();
		}

		return _instance;
	}

	/**
	 * Add lcentry that was sent for indexing to populated data. Default purpose
	 * is Purpose.SEARCH
	 */
	public void setPopulatedLcEntry(LCEntry lcEntry, Permissions permissions) {
		setPopulatedLcEntry(lcEntry, permissions, Purpose.SEARCH);
	}

	/**
	 * Add lcentry that was sent for indexing to populated data.
	 */
	public void setPopulatedLcEntry(LCEntry lcEntry, Permissions permissions,
			Purpose purpose) {
		// if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
		//	permissions = Permissions.PRIVATE;
		//} 
		
		if (lcEntry instanceof Community) {
			addToMap(lcEntry, permissions, Application.community, purpose);
		} else if (lcEntry instanceof Activity) {
			Activity activity = (Activity) lcEntry;
			addToMap(activity, permissions, Application.activity, purpose);
		} else if (lcEntry instanceof ActivityEntry) {
			ActivityEntry activityEntry = (ActivityEntry) lcEntry;
			addToMap(activityEntry, permissions, Application.activityentry,
					purpose);
		} else if (lcEntry instanceof Blog) {
			addToMap(lcEntry, permissions, Application.blog, purpose);
		} else if (lcEntry instanceof FileEntry) {
			addToMap(lcEntry, permissions, Application.file, purpose);
		} else if (lcEntry instanceof Forum) {
			addToMap(lcEntry, permissions, Application.forum, purpose);
		} else if (lcEntry instanceof Wiki) {
			addToMap(lcEntry, permissions, Application.wiki, purpose);
		} else if (lcEntry instanceof Bookmark) {
			addToMap(lcEntry, permissions, Application.bookmark, purpose);
		} else if (lcEntry instanceof Status) {
			addToMap(lcEntry, permissions, Application.status_update, purpose);
		} else if (lcEntry instanceof Profile) {
			addToMap(lcEntry, permissions, Application.profile, purpose);
		}
	}

	private void addToMap(LCEntry lcEntry, Permissions permissions,
			Application appGroup, Purpose purpose) {
		HashMap<Permissions, HashMap<Application, List<LCEntry>>> acl2App2LcEntry = _purpose2acl2App2LcEntry
				.get(purpose);
		if (acl2App2LcEntry == null) {
			acl2App2LcEntry = new HashMap<Permissions, HashMap<Application, List<LCEntry>>>();
			_purpose2acl2App2LcEntry.put(purpose, acl2App2LcEntry);
		}

		HashMap<Application, List<LCEntry>> app2lcEntry = acl2App2LcEntry
				.get(permissions);
		if (app2lcEntry == null) {
			app2lcEntry = new HashMap<Application, List<LCEntry>>();
			acl2App2LcEntry.put(permissions, app2lcEntry);
		}

		List<LCEntry> list = app2lcEntry.get(appGroup);
		if (list == null) {
			list = (List<LCEntry>) new ArrayList<LCEntry>();
			app2lcEntry.put(appGroup, list);
		}
		list.add(lcEntry);
	}

	public Community getCommunityByTitle(String title) {

		Community foundCommunity = null;
		HashMap<Permissions, HashMap<Application, List<LCEntry>>> acl2App2LcEntry = _purpose2acl2App2LcEntry
				.get(Purpose.SEARCH);
		if (acl2App2LcEntry != null) {
			for (HashMap<Application, List<LCEntry>> map : acl2App2LcEntry
					.values()) {
				List<LCEntry> communities = map.get(Application.community);
				for (LCEntry community : communities) {
					if (community.getTitle().equals(title)) {
						foundCommunity = (Community) community;
						break;
					}
				}
			}
		}
		return foundCommunity;
	}

	public LCEntry getLCEntryByTitleAndApp(String title, Application app,
			Permissions permission) {

		LCEntry foundLCEntry = null;
		HashMap<Permissions, HashMap<Application, List<LCEntry>>> acl2App2LcEntry = _purpose2acl2App2LcEntry
				.get(Purpose.SEARCH);
		if (acl2App2LcEntry != null) {
			HashMap<Application, List<LCEntry>> app2entry = acl2App2LcEntry
					.get(permission);
			List<LCEntry> list = app2entry.get(app);
			if (list == null) {
				System.out.println("permission: " + permission + "App: " + app);
			}
			for (LCEntry lcEntry : list) {
				if (app == Application.status_update) {
					if (lcEntry.getContent().equals(title)) {
						foundLCEntry = lcEntry;
						break;
					}
				} else {
					if (lcEntry.getTitle().equals(title)) {
						foundLCEntry = lcEntry;
						break;
					}
				}
			}
		}
		return foundLCEntry;
	}

	public int getNumOfEntriesByPermissions(Permissions permission) {
		int entriesNumber = 0;

		HashMap<Permissions, HashMap<Application, List<LCEntry>>> acl2App2LcEntry = _purpose2acl2App2LcEntry
				.get(Purpose.SEARCH);
		if (acl2App2LcEntry != null) {
			HashMap<Application, List<LCEntry>> app2entry = acl2App2LcEntry
					.get(permission);
			for (Application app : app2entry.keySet()) {
				entriesNumber = entriesNumber
						+ getExpectedNumOfEntriesByApp(permission, app);
				

			}
		}

		return entriesNumber;
	}

	private int getNumOfEntriesByApp(Permissions permission, Application app) {
		int numOfAppEntries = 0;

		HashMap<Permissions, HashMap<Application, List<LCEntry>>> acl2App2LcEntry = _purpose2acl2App2LcEntry
				.get(Purpose.SEARCH);
		if (acl2App2LcEntry != null) {
			HashMap<Application, List<LCEntry>> app2entry = acl2App2LcEntry
					.get(permission);
			List<LCEntry> lcEntry = app2entry.get(app);
			if (lcEntry != null) {
				numOfAppEntries = lcEntry.size();
			}
		}

		return numOfAppEntries;
	}

	/**
	 * @return number of populated entries for Purpose.Search
	 */
	public int getNumOfEntries() {
		return getNumOfEntries(Purpose.SEARCH);
	}

	/**
	 * @return number of populated entries for specified purpose
	 */
	public int getNumOfEntries(Purpose purpose) {
		int entriesNumber = 0;

		HashMap<Permissions, HashMap<Application, List<LCEntry>>> acl2App2LcEntry = _purpose2acl2App2LcEntry
				.get(purpose);
		if (acl2App2LcEntry != null) {
			Set<Permissions> permissions = acl2App2LcEntry.keySet();
			for (Permissions currPermissions : permissions) {
				HashMap<Application, List<LCEntry>> app2entry = acl2App2LcEntry
						.get(currPermissions);
				for (Application app : app2entry.keySet()) {
					entriesNumber += getExpectedNumOfEntriesByApp(
							currPermissions, app);
				}
			}
		}
		return entriesNumber;
	}

	/**
	 * calculates number of expected entries per application. Default purpose is
	 * Purpose.SEARCH
	 */
	public int getExpectedNumOfEntriesByApp(Application app) {
		return getExpectedNumOfEntriesByApp(app, Purpose.SEARCH);
	}

	/**
	 * calculates number of expected entries per application.
	 */
	public int getExpectedNumOfEntriesByApp(Application app, Purpose purpose) {
		int entriesNumber = 0;
		HashMap<Permissions, HashMap<Application, List<LCEntry>>> acl2App2LcEntry = _purpose2acl2App2LcEntry
				.get(purpose);
		if (acl2App2LcEntry != null) {
			Set<Permissions> permissions = acl2App2LcEntry.keySet();
			for (Permissions currPermissions : permissions) {
				HashMap<Application, List<LCEntry>> app2LcEntry = acl2App2LcEntry
						.get(currPermissions);
				if (app2LcEntry != null) {
					List<LCEntry> lcEntries = app2LcEntry.get(app);
					if (lcEntries != null) {
						entriesNumber += lcEntries.size();
					}
				}
			}
		}
		return entriesNumber;
	}

	public int getExpectedNumOfEntriesByApp(Permissions permission,
			Application app) {
		if (app.equals(Application.community)) {
			return getNumOfEntriesByApp(permission, app)
					* ((StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) ? 4
							: 2);
		} else if (app.equals(Application.wiki)) {
			return getNumOfEntriesByApp(permission, app) * 2;
		} else if (app.equals(Application.forum)) {
			return getNumOfEntriesByApp(permission, app);
		}
		return getNumOfEntriesByApp(permission, app);
	}

	public void setPeopleFinderProfileTags(RestAPIUser profileUser,
			ArrayList<String> tags) {
		this.peopleFinderProfileUser = profileUser;
		this.peopleFinderTags = tags;

	}

	public RestAPIUser getPeopleFinderProfileUser() {
		return peopleFinderProfileUser;

	}

	public ArrayList<String> getPeopleFinderTags() {
		return peopleFinderTags;

	}

	public List<EntryWithPermission> getEntriesWithPermissions() {
		List<EntryWithPermission> entriesWithPermissions = new ArrayList<EntryWithPermission>();
		HashMap<Permissions, HashMap<Application, List<LCEntry>>> acl2App2LcEntry = _purpose2acl2App2LcEntry
				.get(Purpose.SEARCH);
		if (acl2App2LcEntry != null) {
			for (Permissions currentPermissions : acl2App2LcEntry.keySet()) {
				for (List<LCEntry> currentEntryList : acl2App2LcEntry.get(
						currentPermissions).values()) {
					for (LCEntry currentEntry : currentEntryList) {
						EntryWithPermission entryWithPermissions = new EntryWithPermission(
								currentEntry, currentPermissions);
						entriesWithPermissions.add(entryWithPermissions);
					}
				}
			}
		}
		return entriesWithPermissions;
	}

	/**
	 * Returns populated entries for passed application
	 */
	public List<LCEntry> getEntries(Application application,
			Permissions permissions) {
		return getEntries(application, permissions, Purpose.SEARCH);
	}

	public List<LCEntry> getEntries(Application application,
			Permissions permissions, Purpose purpose) {
		List<LCEntry> allEntries = new ArrayList<LCEntry>();
		HashMap<Permissions, HashMap<Application, List<LCEntry>>> acl2App2LcEntry = _purpose2acl2App2LcEntry
				.get(purpose);
		if (acl2App2LcEntry != null) {
			if (permissions == null) {
				for (Map<Application, List<LCEntry>> permissionMap : acl2App2LcEntry
						.values()) {
					if (application == null) {
						for (List<LCEntry> currentEntries : permissionMap
								.values()) {
							allEntries.addAll(currentEntries);
						}
					} else {
						List<LCEntry> currentEntries = permissionMap
								.get(application);
						if (currentEntries != null) {
							allEntries.addAll(currentEntries);
						}
					}
				}
			} else {
				Map<Application, List<LCEntry>> permissionMap = acl2App2LcEntry
						.get(permissions);
				if (application == null) {
					for (List<LCEntry> currentEntries : permissionMap.values()) {
						allEntries.addAll(currentEntries);
					}
				} else {
					List<LCEntry> currentEntries = permissionMap
							.get(application);
					if (currentEntries != null) {
						allEntries.addAll(currentEntries);
					}
				}
			}
		}
		return allEntries;
	}

	public class EntryWithPermission {
		private LCEntry entry;

		private Permissions permissions;

		public EntryWithPermission(LCEntry entry, Permissions permissions) {
			super();
			this.entry = entry;
			this.permissions = permissions;
		}

		public LCEntry getEntry() {
			return entry;
		}

		public Permissions getPermissions() {
			return permissions;
		}

	}

}
