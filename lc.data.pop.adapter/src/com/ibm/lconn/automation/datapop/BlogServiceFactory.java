package com.ibm.lconn.automation.datapop;

import com.ibm.lconn.automation.framework.services.blogs.BlogsService;
import com.ibm.lconn.automation.framework.services.common.*;

import java.net.URISyntaxException;
import java.util.Map;
import org.apache.abdera.Abdera;
import org.apache.abdera.protocol.client.AbderaClient;
import org.w3c.dom.Element;

public class BlogServiceFactory
{

    private BlogsService service;

    protected BlogServiceFactory(String serverURL, String userName, String password)
    {
        try
        {
            AbderaClient client = new AbderaClient(new Abdera());
            AbderaClient.registerTrustManager();
            ServiceEntry blogs = (new ServiceConfig(client, serverURL, true)).getService("blogs");

            Utils.addServiceCredentials(blogs, client, userName, password);
            service = new BlogsService(client, blogs);
        }
        catch(URISyntaxException e)
        {
            e.printStackTrace();
        }
        catch (LCServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public ComponentService create(String type, Map<String, Object> fieldMap) throws DataPopAdapterException
    {
        if(type.equalsIgnoreCase("blog"))
            return createBlog(fieldMap);
        if(type.equalsIgnoreCase("blogEntry"))
            return createBlogPost(fieldMap);
        if(type.equalsIgnoreCase("blogComment"))
            return createBlogComment(fieldMap);
        
        String errMsg = "Requested an unknown Community block type: " + type;
    	System.out.println(errMsg);
    	throw new DataPopAdapterException(errMsg);
    }

    private ComponentService createBlogComment(Map<String, Object> fieldMap) throws DataPopAdapterException
    {
        String errMsg = "Unimplemented method: createBlogComment()";
    	System.out.println(errMsg);
    	throw new DataPopAdapterException(errMsg);
    }

    private ComponentService createBlogPost(Map<String, Object> fieldMap) throws DataPopAdapterException
    {
        String errMsg = "Unimplemented method: createBlogPost()";
    	System.out.println(errMsg);
    	throw new DataPopAdapterException(errMsg);
    }

    public ComponentService create(Element node) throws DataPopAdapterException
    {
        if(node.getNodeName().equalsIgnoreCase("blog"))
            return createBlog(node);
        if(node.getNodeName().equalsIgnoreCase("blogEntry"))
            return createBlogEntry(node);
        if(node.getNodeName().equalsIgnoreCase("blogComment"))
            return createBlogComment(node);
        
        String errMsg = "Requested an unknown Blog block type: " + node.getNodeName();
    	System.out.println(errMsg);
    	throw new DataPopAdapterException(errMsg);
    }

    private ComponentService createBlogComment(Element node)
    {
        ComponentService temp = null;
        temp = new BlogCommentAdapter(service, node.getAttribute("content"));
        return temp;
    }

    private ComponentService createBlogEntry(Element node)
    {
        ComponentService temp = null;
        temp = new BlogPostAdapter(service, node.getAttribute("title"), node.getAttribute("content"), node.getAttribute("tagsString"), Boolean.parseBoolean(node.getAttribute("allowedComments")), Integer.parseInt(node.getAttribute("numDaysCommentsAllowed")));
        return temp;
    }

    private ComponentService createBlog(Map<String, Object> fieldMap)
    {
        ComponentService temp = null;
        temp = new BlogAdapter(service, (String)fieldMap.get("title"), (String)fieldMap.get("handle"), (String)fieldMap.get("tagsString"), (String)fieldMap.get("summary"), ((Integer)fieldMap.get("numDaysCommentsAllowed")).intValue(), ((Boolean)fieldMap.get("allowedComments")).booleanValue(), ((Boolean)fieldMap.get("commentModerated")).booleanValue());
        return temp;
    }

    private ComponentService createBlog(Element node)
    {
        ComponentService temp = null;
        temp = new BlogAdapter(service, node.getAttribute("title"), node.getAttribute("handle"), node.getAttribute("tagsString"), node.getAttribute("summary"), Integer.parseInt(node.getAttribute("numDaysCommentsAllowed")), Boolean.parseBoolean(node.getAttribute("allowedComments")), Boolean.parseBoolean(node.getAttribute("commentModerated")));
        return temp;
    }
}
