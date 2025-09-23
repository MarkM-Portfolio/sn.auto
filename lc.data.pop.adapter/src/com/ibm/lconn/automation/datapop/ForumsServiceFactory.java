package com.ibm.lconn.automation.datapop;

import com.ibm.lconn.automation.framework.services.common.*;
import com.ibm.lconn.automation.framework.services.forums.ForumsService;
import java.net.URISyntaxException;
import java.util.Map;
import org.apache.abdera.Abdera;
import org.apache.abdera.protocol.client.AbderaClient;
import org.w3c.dom.Element;

public class ForumsServiceFactory
{
	private ForumsService service;

    protected ForumsServiceFactory(String serverURL, String userName, String password)
    {
        try
        {
            AbderaClient client = new AbderaClient(new Abdera());
            AbderaClient.registerTrustManager();
            ServiceEntry forums = (new ServiceConfig(client, serverURL, true)).getService("forums");

            Utils.addServiceCredentials(forums, client, userName, password);
            service = new ForumsService(client, forums);
        }
        catch(URISyntaxException e)
        {
            e.printStackTrace();
        } catch (LCServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }

    public ComponentService create(String type, Map<String, Object> fieldMap) throws DataPopAdapterException
    {
        if(type.equalsIgnoreCase("forum"))
            return createForum(fieldMap);
        if(type.equalsIgnoreCase("forumpost"))
            return createForumpost(fieldMap);
        if(type.equalsIgnoreCase("forumreply"))
            return createForumreply(fieldMap);
		
        String errMsg = "Requested an unknown Forum block type: " + type;
		System.out.println(errMsg);
		throw new DataPopAdapterException(errMsg);
    }

    public ComponentService create(Element node) throws DataPopAdapterException
    {
        if(node.getNodeName().equalsIgnoreCase("forum"))
            return createForum(node);
        if(node.getNodeName().equalsIgnoreCase("forumtopic"))
            return createForumpost(node);
        if(node.getNodeName().equalsIgnoreCase("forumreply"))
            return createForumreply(node);
        
        String errMsg = "Requested an unknown Forum block type: " + node.getNodeName();
		System.out.println(errMsg);
		throw new DataPopAdapterException(errMsg);
    }

    private ComponentService createForum(Map<String, Object> fieldMap)
    {
        ComponentService temp = null;
        temp = new ForumAdapter(service, (String)fieldMap.get("title"), (String)fieldMap.get("content"));
        return temp;
    }

    private ComponentService createForum(Element node)
    {
        ComponentService temp = null;
        temp = new ForumAdapter(service, node.getAttribute("title"), node.getAttribute("content"));
        return temp;
    }

    private ComponentService createForumpost(Map<String, Object> fieldMap)
    {
        ComponentService temp = null;
        temp = new ForumTopicAdapter(service, (String)fieldMap.get("title"), (String)fieldMap.get("content"), ((Boolean)fieldMap.get("isPinned")).booleanValue(), ((Boolean)fieldMap.get("isLocked")).booleanValue(), ((Boolean)fieldMap.get("isQuestion")).booleanValue(), ((Boolean)fieldMap.get("isAnswer")).booleanValue());
        return temp;
    }

    private ComponentService createForumpost(Element node)
    {
        ComponentService temp = null;
        temp = new ForumTopicAdapter(service, node.getAttribute("title"), node.getAttribute("content"), Boolean.getBoolean("isPinned"), Boolean.getBoolean("isLocked"), Boolean.getBoolean("isQuestion"), Boolean.getBoolean("isAnswer"));
        return temp;
    }

    private ComponentService createForumreply(Map<String, Object> fieldMap)
    {
        ComponentService temp = null;
        temp = new ForumReplyAdapter(service, (String)fieldMap.get("title"), (String)fieldMap.get("content"), ((Boolean)fieldMap.get("isAnswer")).booleanValue());
        return temp;
    }

    private ComponentService createForumreply(Element node)
    {
        ComponentService temp = null;
        temp = new ForumReplyAdapter(service, node.getAttribute("title"), node.getAttribute("content"), Boolean.getBoolean("isAnswer"));
        return temp;
    }
}
