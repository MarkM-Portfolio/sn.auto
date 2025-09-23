package com.ibm.lconn.automation.datapop;

import com.ibm.lconn.automation.framework.services.common.StringConstants.Permissions;
import com.ibm.lconn.automation.framework.services.common.nodes.Bookmark;
import com.ibm.lconn.automation.framework.services.common.nodes.ForumTopic;
import com.ibm.lconn.automation.framework.services.common.nodes.LCEntry;
import com.ibm.lconn.automation.framework.services.common.nodes.Member;
import com.ibm.lconn.automation.framework.services.communities.CommunitiesService;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import java.util.ArrayList;
import org.apache.abdera.model.Entry;

public class CommunityAdapter
  implements ComponentService
{
  private Community community;
  private Entry entry;
  private String group;
  private CommunitiesService service;

  public CommunityAdapter(CommunitiesService service, String title, String content, String tagsString, String permissions, ArrayList<Member> members) throws DataPopAdapterException
  {
	System.out.println("CommunityAdapter: Entering Constructor...");
    if (permissions.equalsIgnoreCase("public"))
    {
     System.out.println("CommunityAdapter: Constructor: About to new up \"public\" Community...");
     community = new Community(title, content, Permissions.PUBLIC, tagsString);
    }
    else if (permissions.equalsIgnoreCase("moderated"))
    {
      System.out.println("CommunityAdapter: Constructor: About to new up \"moderated\" Community...");
      community = new Community(title, content, Permissions.PUBLICINVITEONLY, tagsString);
    }
    else
    {
      System.out.println("CommunityAdapter: Constructor: About to new up \"private\" Community...");
      community = new Community(title, content, Permissions.PRIVATE, tagsString);
    }

    this.service = service;

    System.out.println("CommunityAdapter: Constructor: About to call createCommunity()...");
    entry = ((Entry)service.createCommunity(community));

    System.out.println("CommunityAdapter: Constructor: About to new up Community...");
    community = new Community(entry);

    System.out.println("CommunityAdapter: Constructor: About to call getEditLink()...");
    String editLink = community.getEditLink();
    if(editLink == null)
    {
    	String errMsg = "commmunity.getEditLink() returned a null string.";
    	System.out.println(errMsg);
    	throw new DataPopAdapterException(errMsg);	
    }
    
    System.out.println("CommunityAdapter: Constructor: About to call getCommunity(" + editLink + ")");
    community = new Community((Entry)service.getCommunity(editLink));

    group = title;
    if (members != null)
    {
      for (Member m : members)
      {
    	System.out.println("CommunityAdapter: Constructor: About to call addMemberToCommunity()for: " + m.getUserid() + "...");
        service.addMemberToCommunity(community, m);
      }
    }
	
    System.out.println("CommunityAdapter: Leaving Constructor...");
  }

  public void attach(ComponentService attach)
  {
    if ((attach instanceof BookmarkAdapter))
    {
      service.createCommunityBookmark(community, (Bookmark)attach.getUnderlyingComponent());
    }
    else if ((attach instanceof ForumTopicAdapter))
    {
      attach.setEntry((Entry)service.createForumTopic(community, (ForumTopic)attach.getUnderlyingComponent()));
    }
  }

  public LCEntry getUnderlyingComponent()
  {
    return community;
  }

  public void addParent(ComponentService parent)
  {
  }

  public Entry getEntry()
  {
    return entry;
  }

  public String getGroup()
  {
    return group;
  }

  public void setEntry(Entry entry)
  {
  }
}