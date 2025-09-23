package com.ibm.conn.auto.util;

import com.ibm.lconn.automation.framework.services.common.nodes.ForumReply;
import com.ibm.lconn.automation.framework.services.common.nodes.ForumTopic;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.forums.nodes.Forum;

public class ForumsUtils {
	/**
	 * return the exact community UUID
	 * @param commUUID -- like  "communityUuid=c122a3d7-f577-49dd-9911-294bcb822c13"
	 * @return -- c122a3d7-f577-49dd-9911-294bcb822c13
	 */
	public static String getCommunityUUID(String commUUID){
		int start = commUUID.indexOf("=");
		if(start!=-1){
			commUUID = commUUID.substring(start +1);
		}
		
		return commUUID;	
	}
	/**
	 * it returns the forums object(forum or topic)'s exact id, bb3ba41b-c8d8-49b0-a0c7-e3c6d86353d3
	 * @param id -- a forum/topic's id, like urn:lsid:ibm.com:forum:bb3ba41b-c8d8-49b0-a0c7-e3c6d86353d3
	 * @return string like 'bb3ba41b-c8d8-49b0-a0c7-e3c6d86353d3'
	 */
	public static String getForumUUID(String id){
		
		return id.substring(id.indexOf("forum")+6);
	}
	
	/**
	 * return the exact community UUID
	 * @param commUUID -- like  "communityUuid=c122a3d7-f577-49dd-9911-294bcb822c13"
	 * @return -- c122a3d7-f577-49dd-9911-294bcb822c13
	 */
	public static String getCommunityUUID(Community community){
		String commUUID = community.getUuid();
		int start = commUUID.indexOf("=");
		if(start!=-1){
			commUUID = commUUID.substring(start +1);
		}
		
		return commUUID;	
	}
	/**
	 * it returns the forums object(forum or topic)'s exact id, bb3ba41b-c8d8-49b0-a0c7-e3c6d86353d3
	 * @param id -- a forum/topic's id, like urn:lsid:ibm.com:forum:bb3ba41b-c8d8-49b0-a0c7-e3c6d86353d3
	 * @return string like 'bb3ba41b-c8d8-49b0-a0c7-e3c6d86353d3'
	 */
	public static String getForumUUID(Forum forum){
		String id = forum.getId().toString();
		return id.substring(id.indexOf("forum")+6);
	}
	
	/**
	 * it returns the forums object(forum or topic)'s exact id, bb3ba41b-c8d8-49b0-a0c7-e3c6d86353d3
	 * @param id -- a forum/topic's id, like urn:lsid:ibm.com:forum:bb3ba41b-c8d8-49b0-a0c7-e3c6d86353d3
	 * @return string like 'bb3ba41b-c8d8-49b0-a0c7-e3c6d86353d3'
	 */
	public static String getTopicUUID(ForumTopic topic){
		String id = topic.getId().toString();
		return id.substring(id.indexOf("forum")+6);
	}
	/**
	 * it returns the forums object(forum or topic)'s exact id, bb3ba41b-c8d8-49b0-a0c7-e3c6d86353d3
	 * @param id -- a forum/topic's id, like urn:lsid:ibm.com:forum:bb3ba41b-c8d8-49b0-a0c7-e3c6d86353d3
	 * @return string like 'bb3ba41b-c8d8-49b0-a0c7-e3c6d86353d3'
	 */
	public static String getTopicUUID(ForumReply reply){
		String id = reply.getId().toString();
		return id.substring(id.indexOf("forum")+6);
	}
}
