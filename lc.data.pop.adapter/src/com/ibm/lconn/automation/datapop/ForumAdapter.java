package com.ibm.lconn.automation.datapop;

import com.ibm.lconn.automation.framework.services.common.nodes.LCEntry;
import com.ibm.lconn.automation.framework.services.forums.ForumsService;
import com.ibm.lconn.automation.framework.services.forums.nodes.Forum;
import org.apache.abdera.model.Entry;

public class ForumAdapter
    implements ComponentService
{
    private Forum forum;
    private Entry entry;
    private String group;

    public ForumAdapter(ForumsService service, String title, String content)
    {
        forum = new Forum(title, content);
        entry = (Entry)service.createForum(forum);
        forum = new Forum(entry);
        group = title;
    }

    public void attach(ComponentService componentservice)
    {
    }

    public LCEntry getUnderlyingComponent()
    {
        return forum;
    }

    public void addParent(ComponentService componentservice)
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

    public void setEntry(Entry entry1)
    {
    }
}
