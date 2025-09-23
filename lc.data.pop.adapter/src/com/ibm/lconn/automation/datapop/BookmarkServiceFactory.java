package com.ibm.lconn.automation.datapop;

import com.ibm.lconn.automation.framework.services.common.*;
import com.ibm.lconn.automation.framework.services.dogear.DogearService;
import java.net.URISyntaxException;
import java.util.Map;
import org.apache.abdera.Abdera;
import org.apache.abdera.protocol.client.AbderaClient;
import org.w3c.dom.Element;

public class BookmarkServiceFactory
{
    private DogearService service;

    protected BookmarkServiceFactory(String serverURL, String userName, String password)
    {
        try
        {
            AbderaClient client = new AbderaClient(new Abdera());
            AbderaClient.registerTrustManager();
            ServiceEntry dogear = (new ServiceConfig(client, serverURL, true)).getService("dogear");

            Utils.addServiceCredentials(dogear, client, userName, password);
            service = new DogearService(client, dogear);

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
        if(type.equalsIgnoreCase("bookmark"))
            return createBookmark(fieldMap);
        
        String errMsg = "Requested an unknown Bookmark block type: " + type;
    	System.out.println(errMsg);
    	throw new DataPopAdapterException(errMsg);
    }

    public ComponentService create(Element node) throws DataPopAdapterException
    {
        if(node.getNodeName().equalsIgnoreCase("bookmark"))
            return createBookmark(node);
        
        String errMsg = "Requested an unknown Bookmark block type: " + node.getNodeName();
    	System.out.println(errMsg);
    	throw new DataPopAdapterException(errMsg);
    }

    private ComponentService createBookmark(Map<String, Object> fieldMap)
    {
        ComponentService temp = null;
        temp = new BookmarkAdapter(service, (String)fieldMap.get("title"), (String)fieldMap.get("content"), (String)fieldMap.get("tagsString"), (String)fieldMap.get("linkHref"));
        return temp;
    }

    private ComponentService createBookmark(Element node)
    {
        ComponentService temp = null;
        temp = new BookmarkAdapter(service, node.getAttribute("title"), node.getAttribute("content"), node.getAttribute("tagsString"), node.getAttribute("linkHref"));
        return temp;
    }
}
