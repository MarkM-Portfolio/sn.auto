package com.ibm.lconn.automation.datapop;

import com.ibm.lconn.automation.framework.services.blogs.BlogsService;
import com.ibm.lconn.automation.framework.services.blogs.nodes.Blog;
import com.ibm.lconn.automation.framework.services.blogs.nodes.BlogComment;
import com.ibm.lconn.automation.framework.services.blogs.nodes.BlogPost;
import com.ibm.lconn.automation.framework.services.common.nodes.LCEntry;
import org.apache.abdera.model.Entry;

public class BlogCommentAdapter extends AbstractComponent
{
    BlogComment blogComment;
    BlogsService service;
    private String content;

    public BlogCommentAdapter(BlogsService service, String content)
    {
        this.content = content;
        this.service = service;
    }

    public void attach(ComponentService attach)
    {
        addParent(attach);
        Blog blog = new Blog(attach.getEntry());
        blogComment = new BlogComment(content, attach.getEntry());
        if(attach instanceof BlogAdapter)
            setEntry((Entry)service.createComment(blog, blogComment));
        else
            setEntry((Entry)service.createComment((BlogPost)attach.getUnderlyingComponent(), blogComment));
    }

    public LCEntry getUnderlyingComponent()
    {
        return blogComment;
    }
}
