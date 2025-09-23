package com.ibm.lconn.automation.framework.services.search.data;

public enum Scope {
	allconnections("allconnections"), 
	activities("activities"),
	activities_entry("activities:entry"),
	activities_section("activities:section"),
	activities_task("activities:task"),
	activities_bookmark("activities:bookmark"),
	activities_activity("activities:activity"),
	activities_attachment("activities:attachment"),
	activities_reply("activities:reply"),
	blogs("blogs"), 
	communities("communities"), 
	communities_content("communities:content"), 
	communities_entry("communities:entry"),
	communities_files("communities:files"),
	communities_wikis("communities:wikis"),
	communities_forums("communities:forums"),
	dogear("dogear"), 
	forums("forums"), 
	forums_forum("forums:forum"), 
	forums_topic("forums:topic"),
	forums_file("forums:file"),
	profiles("profiles"), 
	wikis("wikis"),
	wikis_wiki("wikis:wiki"),
	wikis_page("wikis:page"),
	files("files"), 
	personalOnly("personalOnly"),
	stand_alone("stand-alone"),
	status_updates("status_updates");

	private final String text;

	/**
	 * 
	 * @param text
	 */
	private Scope(final String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return text;
	}
};
