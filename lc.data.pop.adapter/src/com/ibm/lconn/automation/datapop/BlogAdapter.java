package com.ibm.lconn.automation.datapop;

import com.ibm.lconn.automation.framework.services.blogs.BlogsService;
import com.ibm.lconn.automation.framework.services.blogs.nodes.Blog;
import com.ibm.lconn.automation.framework.services.common.nodes.LCEntry;
import java.util.TimeZone;
import org.apache.abdera.model.Entry;

public class BlogAdapter
    implements ComponentService
{
    private Blog blog;
    private Entry entry;
    private String group;

    public BlogAdapter(BlogsService service, String title, String handle, String tagsString, String summary, int numDaysCommentsAllowed, boolean allowComments, 
            boolean commentModerated)
    {
        blog = new Blog(title, handle, summary, tagsString, false, false, null, null, TimeZone.getDefault(), allowComments, numDaysCommentsAllowed, true, commentModerated, true, 0, -1, null, null, null, 0);
        entry = (Entry)service.createBlog(blog);
        group = title;
    }

    public void attach(ComponentService componentservice)
    {
    }

    public LCEntry getUnderlyingComponent()
    {
        return blog;
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
