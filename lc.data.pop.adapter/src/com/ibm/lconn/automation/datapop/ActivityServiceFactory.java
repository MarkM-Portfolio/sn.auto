package com.ibm.lconn.automation.datapop;

import com.ibm.lconn.automation.framework.services.activities.ActivitiesService;
import com.ibm.lconn.automation.framework.services.common.*;
import com.ibm.lconn.automation.framework.services.common.nodes.Member;
import com.ibm.lconn.automation.framework.services.profiles.ProfilesService;
import com.ibm.lconn.automation.framework.services.profiles.nodes.Profile;
import com.ibm.lconn.automation.framework.services.profiles.nodes.VCardEntry;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.*;
import org.apache.abdera.Abdera;
import org.apache.abdera.protocol.client.AbderaClient;
import org.w3c.dom.*;

public class ActivityServiceFactory
{
    private ActivitiesService service;
    private ProfilesService pservice;

    protected ActivityServiceFactory(String serverURL, String userName, String password)
    {
        try
        {
            AbderaClient client = new AbderaClient(new Abdera());
            AbderaClient.registerTrustManager();
            ServiceEntry activities = (new ServiceConfig(client, serverURL, true)).getService("activities");
            ServiceEntry prof = (new ServiceConfig(client, serverURL, true)).getService("profiles");

            Utils.addServiceCredentials(activities, client, userName, password);
            Utils.addServiceCredentials(prof, client, userName, password);
			service = new ActivitiesService(client, activities);
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
       
    }

    public ComponentService create(String type, Map<String, Object> fieldMap)throws DataPopAdapterException
    {
        if(type.equalsIgnoreCase("Activity"))
            return createActivity(fieldMap);
        if(type.equalsIgnoreCase("Section"))
            return createSection(fieldMap);
        if(type.equalsIgnoreCase("todo"))
            return createTodo(fieldMap);
        if(type.equalsIgnoreCase("activityEntry"))
            return createActivityEntry(fieldMap);
        if(type.equalsIgnoreCase("reply"))
            return createReply(fieldMap);
        
        String errMsg = "Requested an unknown Activity block type: " + type;
    	System.out.println(errMsg);
    	throw new DataPopAdapterException(errMsg);
    }

    public ComponentService create(Element node)
    {
        if(node.getNodeName().equalsIgnoreCase("activity"))
            return createActivity(node);
        if(node.getNodeName().equalsIgnoreCase("Section"))
            return createSection(node);
        if(node.getNodeName().equalsIgnoreCase("todo"))
            return createTodo(node);
        if(node.getNodeName().equalsIgnoreCase("activityEntry"))
            return createActivityEntry(node);
        if(node.getNodeName().equalsIgnoreCase("reply"))
            return createReply(node);
        else
            return null;
    }

    private ComponentService createReply(Element node)
    {
        ComponentService temp = null;
        temp = new ReplyAdapter(service, node.getAttribute("title"), node.getAttribute("content"), Integer.parseInt(node.getAttribute("position")), Boolean.parseBoolean(node.getAttribute("isPrivate")));
        return temp;
    }

    private ComponentService createActivityEntry(Element node)
    {
        ComponentService temp = null;
        temp = new ActivityEntryAdapter(service, node.getAttribute("title"), node.getAttribute("content"), node.getAttribute("tagsString"), Integer.parseInt(node.getAttribute("position")), Boolean.parseBoolean(node.getAttribute("isPrivate")), Boolean.parseBoolean(node.getAttribute("isTemplate")));
        return temp;
    }

    private ComponentService createTodo(Element node)
    {
        ComponentService temp = null;
        temp = new ToDoAdapter(service, node.getAttribute("title"), node.getAttribute("content"), node.getAttribute("tagsString"), Integer.parseInt(node.getAttribute("position")), Boolean.parseBoolean(node.getAttribute("isComplete")), Boolean.parseBoolean(node.getAttribute("isPrivate")), null, null);
        return temp;
    }

    private ComponentService createSection(Element node)
    {
        ComponentService temp = null;
        temp = new SectionAdapter(service, node.getAttribute("title"), Integer.parseInt(node.getAttribute("position")));
        return temp;
    }

    private ComponentService createActivity(Element node)
    {
        ComponentService temp = null;
        ArrayList<Member> members = null;
        if(node.hasAttribute("hasMembers"))
            members = getMembers(node);
        if(!Boolean.parseBoolean(node.getAttribute("isPrivate")))
        {
            if(members == null)
                members = new ArrayList<Member>();
            members.add(new Member("*", "*", com.ibm.lconn.automation.framework.services.common.StringConstants.Component.ACTIVITIES, com.ibm.lconn.automation.framework.services.common.StringConstants.Role.MEMBER, com.ibm.lconn.automation.framework.services.common.StringConstants.MemberType.GROUP));
        }
        DateFormat date = DateFormat.getDateInstance(3);
        try
        {
            temp = new ActivityAdapter(service, node.getAttribute("title"), node.getAttribute("content"), node.getAttribute("tagsString"), date.parse(node.getAttribute("dueDate")), Boolean.parseBoolean(node.getAttribute("isComplete")), Boolean.parseBoolean(node.getAttribute("isCommunityActivity")), members);
        }
        catch(ParseException e)
        {
            System.err.println(e.getLocalizedMessage());
            System.err.println("using current time");
            temp = new ActivityAdapter(service, node.getAttribute("title"), node.getAttribute("content"), node.getAttribute("tagsString"), new Date(), Boolean.parseBoolean(node.getAttribute("isComplete")), Boolean.parseBoolean(node.getAttribute("isCommunityActivity")), members);
        }
        return temp;
    }

    private ComponentService createReply(Map<String, Object> fieldMap)
    {
        ComponentService temp = null;
        if(fieldMap.containsKey("title") && fieldMap.containsKey("content") && fieldMap.containsKey("isPrivate") && fieldMap.containsKey("position"))
            temp = new ReplyAdapter(service, (String)fieldMap.get("title"), (String)fieldMap.get("content"), ((Integer)fieldMap.get("position")).intValue(), ((Boolean)fieldMap.get("isPrivate")).booleanValue());
        return temp;
    }

    private ComponentService createActivityEntry(Map<String, Object> fieldMap)
    {
        ComponentService temp = null;
        if(fieldMap.containsKey("title") && fieldMap.containsKey("content") && fieldMap.containsKey("tagsString") && fieldMap.containsKey("isTemplate") && fieldMap.containsKey("isPrivate") && fieldMap.containsKey("position"))
            temp = new ActivityEntryAdapter(service, (String)fieldMap.get("title"), (String)fieldMap.get("content"), (String)fieldMap.get("tagsString"), ((Integer)fieldMap.get("position")).intValue(), ((Boolean)fieldMap.get("isPrivate")).booleanValue(), ((Boolean)fieldMap.get("isTemplate")).booleanValue());
        return temp;
    }

    private ComponentService createActivity(Map<String, Object> fieldMap)
    {
        ComponentService temp = null;
        ArrayList<Member> members = null;
        if(fieldMap.containsKey("title") && fieldMap.containsKey("content") && fieldMap.containsKey("tagsString") && fieldMap.containsKey("isComplete") && fieldMap.containsKey("dueDate") && fieldMap.containsKey("isCommunityActivity"))
        {
            if(fieldMap.containsKey("members"))
                members = (ArrayList<Member>)fieldMap.get("members");
            if(fieldMap.containsKey("isPrivate") && !((Boolean)fieldMap.get("isPrivate")).booleanValue())
            {
                if(members == null)
                    members = new ArrayList<Member>();
                members.add(new Member("*", "*", com.ibm.lconn.automation.framework.services.common.StringConstants.Component.ACTIVITIES, com.ibm.lconn.automation.framework.services.common.StringConstants.Role.MEMBER, com.ibm.lconn.automation.framework.services.common.StringConstants.MemberType.GROUP));
            }
            temp = new ActivityAdapter(service, (String)fieldMap.get("title"), (String)fieldMap.get("content"), (String)fieldMap.get("tagsString"), (Date)fieldMap.get("dueDate"), ((Boolean)fieldMap.get("isComplete")).booleanValue(), ((Boolean)fieldMap.get("isCommunityActivity")).booleanValue(), members);
        }
        return temp;
    }

    private ComponentService createSection(Map<String, Object> fieldMap)
    {
        ComponentService temp = null;
        if(fieldMap.containsKey("title") && fieldMap.containsKey("position"))
            temp = new SectionAdapter(service, (String)fieldMap.get("title"), ((Integer)fieldMap.get("position")).intValue());
        return temp;
    }

    private ComponentService createTodo(Map<String, Object> fieldMap)
    {
        ComponentService temp = null;
        if(fieldMap.containsKey("title") && fieldMap.containsKey("content") && fieldMap.containsKey("tagsString") && fieldMap.containsKey("isComplete") && fieldMap.containsKey("isPrivate") && fieldMap.containsKey("position"))
            temp = new ToDoAdapter(service, (String)fieldMap.get("title"), (String)fieldMap.get("content"), (String)fieldMap.get("tagsString"), ((Integer)fieldMap.get("position")).intValue(), ((Boolean)fieldMap.get("isComplete")).booleanValue(), ((Boolean)fieldMap.get("isPrivate")).booleanValue(), (String)fieldMap.get("assignedToUserName"), (String)fieldMap.get("assignedToUserID"));
        return temp;
    }

    private ArrayList<Member> getMembers(Element node)
    {
        NodeList list = node.getChildNodes();
        ArrayList<Member> members = new ArrayList<Member>();
        for(int i = 0; i < list.getLength(); i++)
            if(list.item(i).getNodeName().equalsIgnoreCase("member"))
            {
                com.ibm.lconn.automation.framework.services.common.StringConstants.Role role = null;
                if(list.item(i).getAttributes().getNamedItem("type").getNodeValue().equalsIgnoreCase("author"))
                    role = com.ibm.lconn.automation.framework.services.common.StringConstants.Role.AUTHOR;
                else
                if(list.item(i).getAttributes().getNamedItem("type").getNodeValue().equalsIgnoreCase("all"))
                    role = com.ibm.lconn.automation.framework.services.common.StringConstants.Role.ALL;
                else
                if(list.item(i).getAttributes().getNamedItem("type").getNodeValue().equalsIgnoreCase("member"))
                    role = com.ibm.lconn.automation.framework.services.common.StringConstants.Role.MEMBER;
                else
                if(list.item(i).getAttributes().getNamedItem("type").getNodeValue().equalsIgnoreCase("owner"))
                    role = com.ibm.lconn.automation.framework.services.common.StringConstants.Role.OWNER;
                else
                if(list.item(i).getAttributes().getNamedItem("type").getNodeValue().equalsIgnoreCase("reader"))
                    role = com.ibm.lconn.automation.framework.services.common.StringConstants.Role.READER;
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

                members.add(new Member(list.item(i).getAttributes().getNamedItem("id").getNodeValue(), null, com.ibm.lconn.automation.framework.services.common.StringConstants.Component.ACTIVITIES, role, com.ibm.lconn.automation.framework.services.common.StringConstants.MemberType.GROUP));
            }

        return members;
    }
}
