package com.ibm.lconn.automation.datapop;

import com.ibm.lconn.automation.framework.services.common.*;
import com.ibm.lconn.automation.framework.services.profiles.ProfilesService;
import com.ibm.lconn.automation.framework.services.profiles.nodes.Profile;
import com.ibm.lconn.automation.framework.services.profiles.nodes.VCardEntry;
import com.ibm.lconn.automation.framework.services.wikis.WikisService;
import com.ibm.lconn.automation.framework.services.wikis.nodes.WikiMember;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.*;
import org.apache.abdera.Abdera;
import org.apache.abdera.protocol.client.AbderaClient;
import org.w3c.dom.*;

public class WikiServiceFactory
{
    private WikisService service;
    private ProfilesService pservice;

    protected WikiServiceFactory(String serverURL, String userName, String password)
    {
        try
        {
            AbderaClient client = new AbderaClient(new Abdera());
            AbderaClient.registerTrustManager();
            ServiceEntry wiki = (new ServiceConfig(client, serverURL, true)).getService("wikis");
            ServiceEntry prof = (new ServiceConfig(client, serverURL, true)).getService("profiles");

            Utils.addServiceCredentials(wiki, client, userName, password);
            Utils.addServiceCredentials(prof, client, userName, password);
            
            service = new WikisService(client, wiki);
            pservice = new ProfilesService(client, prof);
        }
        catch(URISyntaxException e)
        {
            e.printStackTrace();
        }
        catch (LCServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        service.getWikiFeed();
    }

    public ComponentService create(Element node)throws DataPopAdapterException
    {
        if(node.getNodeName().equalsIgnoreCase("wiki"))
            return createWiki(node);
        if(node.getNodeName().equalsIgnoreCase("wikipage"))
            return createWikiPage(node);
        
        String errMsg = "Requested an unknown Wiki block type: " + node.getNodeName();
		System.out.println(errMsg);
		throw new DataPopAdapterException(errMsg);
    }

    private ComponentService createWiki(Element node)
    {
        ComponentService temp = null;
        ArrayList<WikiMember> members = new ArrayList<WikiMember>();
        if(node.hasAttribute("hasMembers") && Boolean.parseBoolean(node.getAttribute("hasMembers")))
            members = getMembers(node);
        if(node.hasAttribute("isPublic") && Boolean.parseBoolean(node.getAttribute("isPublic")))
        {
            WikiMember virtualReader = new WikiMember("anonymous-user", com.ibm.lconn.automation.framework.services.common.StringConstants.WikiRole.READER, com.ibm.lconn.automation.framework.services.common.StringConstants.WikiMemberType.VIRTUAL);
            WikiMember virtualEditor = new WikiMember("all-authenticated-users", com.ibm.lconn.automation.framework.services.common.StringConstants.WikiRole.EDITOR, com.ibm.lconn.automation.framework.services.common.StringConstants.WikiMemberType.VIRTUAL);
            members.add(virtualReader);
            members.add(virtualEditor);
        }
        temp = new WikiAdapter(service, node.getAttribute("title"), node.getAttribute("summary"), node.getAttribute("tagsString"), members);
        return temp;
    }

    private ComponentService createWikiPage(Element node)
    {
        ComponentService temp = null;
        temp = new WikiPageAdapter(service, node.getAttribute("title"), node.getAttribute("content"), node.getAttribute("tagsString"));
        return temp;
    }

    private ArrayList<WikiMember> getMembers(Element node)
    {
        NodeList list = node.getChildNodes();
        ArrayList<WikiMember> members = new ArrayList<WikiMember>();
        for(int i = 0; i < list.getLength(); i++)
            if(list.item(i).getNodeName().equalsIgnoreCase("member"))
            {
                com.ibm.lconn.automation.framework.services.common.StringConstants.WikiRole role = null;
                if(list.item(i).getAttributes().getNamedItem("type").getNodeValue().equalsIgnoreCase("editor"))
                    role = com.ibm.lconn.automation.framework.services.common.StringConstants.WikiRole.EDITOR;
                else
                if(list.item(i).getAttributes().getNamedItem("type").getNodeValue().equalsIgnoreCase("manager"))
                    role = com.ibm.lconn.automation.framework.services.common.StringConstants.WikiRole.MANAGER;
                else
                if(list.item(i).getAttributes().getNamedItem("type").getNodeValue().equalsIgnoreCase("reader"))
                    role = com.ibm.lconn.automation.framework.services.common.StringConstants.WikiRole.READER;
                ArrayList<Profile> allProf = null;
                try
                {
                    allProf = pservice.getAllProfiles(com.ibm.lconn.automation.framework.services.common.StringConstants.ProfileOutput.VCARD, false, null, null, null, null, com.ibm.lconn.automation.framework.services.common.StringConstants.ProfileFormat.FULL, null, URLEncoder.encode(list.item(i).getAttributes().getNamedItem("id").getNodeValue(), "UTF-8"), null, 0, 0, null, null, null, null, null, null);
                }
                catch(DOMException e)
                {
                    e.printStackTrace();
                }
                catch(UnsupportedEncodingException e)
                {
                    e.printStackTrace();
                }
                VCardEntry userCard = null;
                for(Iterator<Profile> iterator = allProf.iterator(); iterator.hasNext();)
                {
                    Profile p = (Profile)iterator.next();
                    userCard = new VCardEntry(p.getContent(), null);
                    if(((String)userCard.getVCardFields().get("FN")).equalsIgnoreCase(list.item(i).getAttributes().getNamedItem("id").getNodeValue()))
                        break;
                }

                members.add(new WikiMember((String)userCard.getVCardFields().get("X_LCONN_USERID"), role, com.ibm.lconn.automation.framework.services.common.StringConstants.WikiMemberType.USER));
            }

        return members;
    }
}
