package com.ibm.lconn.automation.framework.activitiesStreamSearch.utils.nodes;

import static org.testng.AssertJUnit.assertTrue;

import java.util.ArrayList;

import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;

import com.ibm.lconn.automation.framework.services.blogs.BlogsService;
import com.ibm.lconn.automation.framework.services.blogs.nodes.Blog;
import com.ibm.lconn.automation.framework.services.common.StringConstants.BlogsType;
import com.ibm.lconn.automation.framework.services.communities.CommunitiesService;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class FvtPopulationUtils {

	// Method to get list of all existing communities while ArrayList of Entries
	public static ArrayList<Entry> getCommunitiesList(CommunitiesService service) {
		System.out.println("Service : " + service.toString());
		System.out.println("Getting all existing communities on the server");
		ArrayList<Entry> communities = new ArrayList<Entry>();
		ArrayList<Entry> totalCommunities = new ArrayList<Entry>();

		Feed communitiesFeed = (Feed) service.getMyCommunities(true, null, 0,
				0, null, null, null, null, null);
		assertTrue(communitiesFeed != null);
		for (Entry communityEntry : communitiesFeed.getEntries()) {
			communities.add(communityEntry);
			System.out.println("Community title: " + communityEntry.getTitle());
		}
		System.out.println("Number of existing communities: "
				+ communities.size());
		totalCommunities.addAll(communities);
		int index = 2;
		while (communities.size() == 10) {
			communities.clear();
			communitiesFeed = (Feed) service.getMyCommunities(true, null,
					index, 0, null, null, null, null, null);
			assertTrue(communitiesFeed != null);
			for (Entry communityEntry : communitiesFeed.getEntries()) {
				communities.add(communityEntry);
				System.out.println("Community title: "
						+ communityEntry.getTitle());
			}
			System.out.println("Index " + index + " = " + communities.size());
			totalCommunities.addAll(communities);
			index++;
		}

		System.out.println("Total count: " + totalCommunities.size());
		return totalCommunities;

	}

	// Method to get list of all existing communities while ArrayList of
	// Communities
	public static ArrayList<Community> getCommunitiesList1(
			CommunitiesService service) {
		System.out.println("Service : " + service.toString());
		System.out.println("Getting all existing communities on the server");
		ArrayList<Community> communities = new ArrayList<Community>();
		ArrayList<Community> totalCommunities = new ArrayList<Community>();

		Feed communitiesFeed = (Feed) service.getMyCommunities(true, null, 0,
				0, null, null, null, null, null);
		assertTrue(communitiesFeed != null);
		for (Entry communityEntry : communitiesFeed.getEntries()) {
			communities.add(new Community(communityEntry));
			System.out.println("Community title: " + communityEntry.getTitle());
		}
		System.out.println("Number of existing communities: "
				+ communities.size());
		totalCommunities.addAll(communities);
		int index = 2;
		while (communities.size() == 10) {
			communities.clear();
			communitiesFeed = (Feed) service.getMyCommunities(true, null,
					index, 0, null, null, null, null, null);
			assertTrue(communitiesFeed != null);
			for (Entry communityEntry : communitiesFeed.getEntries()) {
				communities.add(new Community(communityEntry));
				System.out.println("Community title: "
						+ communityEntry.getTitle());
			}
			System.out.println("Index " + index + " = " + communities.size());
			totalCommunities.addAll(communities);
			index++;
		}

		return totalCommunities;
	}

	// Method to get list of all existing Blogs while ArrayList of Blogs
	public static ArrayList<Blog> getBlogsList(BlogsService service) {
		System.out.println("Service : " + service.toString());
		System.out.println("Getting all existing blogs on the server");
		ArrayList<Blog> blogs = new ArrayList<Blog>();
		ArrayList<Blog> totalBlogs = new ArrayList<Blog>();

		blogs = service.getMyBlogs(null, null, 0, 0, null, null, null, null,
				BlogsType.BLOG, null, null);
		totalBlogs.addAll(blogs);
		int index = 2;
		while (blogs.size() == 10) {
			blogs.clear();
			blogs = service.getMyBlogs(null, null, index, 0, null, null, null,
					null, BlogsType.BLOG, null, null);
			System.out.println("Index " + index + " = " + blogs.size());
			totalBlogs.addAll(blogs);
			index++;
		}
		return totalBlogs;
	}

}
