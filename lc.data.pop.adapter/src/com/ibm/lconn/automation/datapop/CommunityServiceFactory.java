package com.ibm.lconn.automation.datapop;

import com.ibm.lconn.automation.framework.services.common.LCServiceException;
import com.ibm.lconn.automation.framework.services.common.ServiceConfig;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Component;
import com.ibm.lconn.automation.framework.services.common.StringConstants.MemberType;
import com.ibm.lconn.automation.framework.services.common.StringConstants.ProfileFormat;
import com.ibm.lconn.automation.framework.services.common.StringConstants.ProfileOutput;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Role;
import com.ibm.lconn.automation.framework.services.common.Utils;
import com.ibm.lconn.automation.framework.services.common.nodes.Member;
import com.ibm.lconn.automation.framework.services.communities.CommunitiesService;
import com.ibm.lconn.automation.framework.services.profiles.ProfilesService;
import com.ibm.lconn.automation.framework.services.profiles.nodes.Profile;
import com.ibm.lconn.automation.framework.services.profiles.nodes.VCardEntry;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import org.apache.abdera.Abdera;
import org.apache.abdera.protocol.client.AbderaClient;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class CommunityServiceFactory
{
  private CommunitiesService service;
  private ProfilesService pservice;

  protected CommunityServiceFactory(String serverURL, String userName, String password)
  {

    try
    {
        AbderaClient client = new AbderaClient(new Abdera());
        AbderaClient.registerTrustManager();

        ServiceEntry dogear = new ServiceConfig(client, serverURL, true).getService("communities");
        ServiceEntry prof = new ServiceConfig(client, serverURL, true).getService("profiles");
        Utils.addServiceCredentials(dogear, client, userName, password);
        Utils.addServiceCredentials(prof, client, userName, password);
        
    	service = new CommunitiesService(client, dogear);
		pservice = new ProfilesService(client, prof);
    }
    catch (URISyntaxException e) {
    	e.printStackTrace();
    }
    catch (LCServiceException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    
  }

  public ComponentService create(Element node) throws DataPopAdapterException
  {
    System.out.println("CommunityServiceFactory: Entering CommunityServiceFactory:create()...");
	if (node.getNodeName().equalsIgnoreCase("community"))
    {
	    ComponentService temp = null;
	    temp = createCommunity(node);
	    
	    System.out.println("CommunityServiceFactory: Leaving CommunityServiceFactory:create()...");	    
	    return temp;
    }

    String errMsg = "Requested an unknown Community block type: " + node.getNodeName();
	System.out.println(errMsg);
	throw new DataPopAdapterException(errMsg);
  }

  private ComponentService createCommunity(Element node) throws DataPopAdapterException
  {
	System.out.println("CommunityServiceFactory: Entering CommunityServiceFactory:createCommunity()...");
    ComponentService temp = null;

    ArrayList<Member> members = null;

    if (node.hasAttribute("hasMembers"))
    {
      members = getMembers(node);
    }

    try
    {
    	temp = new CommunityAdapter(service, 
    			node.getAttribute("title"), 
    			node.getAttribute("content"), 
    			node.getAttribute("tagsString"), 
    			node.getAttribute("permission"), 
    			members);
    }
    catch(DataPopAdapterException DPAexception)
    {
    	throw DPAexception;
    }
	
    System.out.println("CommunityServiceFactory: Leaving CommunityServiceFactory:createCommunity()...");
    return temp;
  }

  private ArrayList<Member> getMembers(Element node)
  {
	System.out.println("CommunityServiceFactory: Entering CommunityServiceFactory:getMembers()...");
    NodeList list = node.getChildNodes();

    ArrayList<Member> members = new ArrayList<Member>();

    for (int i = 0; i < list.getLength(); i++)
    {
      if (list.item(i).getNodeName().equalsIgnoreCase("member"))
      {
        ArrayList<Profile> allProf = null;
        try {
          allProf = pservice.getAllProfiles(ProfileOutput.VCARD, false, null, null, null, null, ProfileFormat.FULL, null, URLEncoder.encode(list.item(i).getAttributes().getNamedItem("id").getNodeValue(), "UTF-8"), null, 0, 0, null, null, null, null, null, null);
       } catch (DOMException e) {
          e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
        }

        VCardEntry userCard = null;
        for (Profile p : allProf)
        {
          userCard = new VCardEntry(p.getContent(), null);

          if (((String)userCard.getVCardFields().get("FN")).equalsIgnoreCase(list.item(i).getAttributes().getNamedItem("id").getNodeValue()))
          {
            break;
          }
        }
        members.add(new Member(null, (String)userCard.getVCardFields().get("X_LCONN_USERID"), Component.COMMUNITIES, Role.MEMBER, MemberType.COMMUNITY));
      }
    }
    
    System.out.println("CommunityServiceFactory: Leaving CommunityServiceFactory:getMembers()...");
    return members;
  }
}