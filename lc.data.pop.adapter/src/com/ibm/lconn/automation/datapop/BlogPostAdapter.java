package com.ibm.lconn.automation.datapop;

import com.ibm.lconn.automation.framework.services.blogs.BlogsService;
import com.ibm.lconn.automation.framework.services.blogs.nodes.Blog;
import com.ibm.lconn.automation.framework.services.blogs.nodes.BlogPost;
import com.ibm.lconn.automation.framework.services.common.nodes.LCEntry;
import org.apache.abdera.model.Entry;

public class BlogPostAdapter extends AbstractComponent
{
    BlogPost blogPost;
    BlogsService service;

    public BlogPostAdapter(BlogsService service, String title, String content, String tagsString, boolean allowComments, int numDaysCommentsallowed)
    {
        this.service = service;
        blogPost = new BlogPost(title, content, tagsString, allowComments, numDaysCommentsallowed);
    }

    public void attach(ComponentService attach)
    {
    	Blog blog = new Blog(attach.getEntry());
        addParent(attach);
        setEntry((Entry)service.createPost(blog, blogPost));
        blogPost = new BlogPost(getEntry());
    }

    public LCEntry getUnderlyingComponent()
    {
        return blogPost;
    }
}
