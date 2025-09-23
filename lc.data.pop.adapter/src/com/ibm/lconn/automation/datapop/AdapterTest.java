package com.ibm.lconn.automation.datapop;

import com.ibm.lconn.automation.framework.services.activities.ActivitiesService;
import com.ibm.lconn.automation.framework.services.activities.nodes.Activity;
import com.ibm.lconn.automation.framework.services.common.*;
import com.ibm.lconn.automation.framework.services.common.nodes.Member;
import java.net.URISyntaxException;
import java.util.*;
import org.apache.abdera.Abdera;
import org.apache.abdera.protocol.client.AbderaClient;

public class AdapterTest
{

    public AdapterTest()
    {
    }

    public void deleteTests()
    {
    	ActivitiesService service=null;
        try
        {
            AbderaClient client = new AbderaClient(new Abdera());
            AbderaClient.registerTrustManager();
            ServiceEntry activities = (new ServiceConfig(client, "https://lcdatapop.swg.usma.ibm.com", true)).getService("activities");

            Utils.addServiceCredentials(activities, client, "fadams", "passw0rd");
            service = new ActivitiesService(client, activities);
        }
        catch(URISyntaxException e)
        {
            e.printStackTrace();
        }
        catch (LCServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        ArrayList<Activity> list = service.getAllActivities(null, null, null, null, 0, 0, null, null, null, null, null, null, null, null);
        Activity a;
        for(Iterator<Activity> iterator = list.iterator(); iterator.hasNext(); service.deleteActivity(a.getEditHref()))
            a = (Activity)iterator.next();

    }

    public void testActivity() throws DataPopAdapterException
    {
        String serverURL = "https://lcdatapop.swg.usma.ibm.com";
        Map<String, Object> fieldMap = new HashMap<String, Object>();
        fieldMap.put("title", "testing member interaction");
        fieldMap.put("content", "content");
        fieldMap.put("tagsString", "test1");
        fieldMap.put("dueDate", new Date(0x12d4cbL));
        fieldMap.put("isComplete", Boolean.valueOf(false));
        fieldMap.put("isPrivate", Boolean.valueOf(false));
        fieldMap.put("isTemplate", Boolean.valueOf(false));
        fieldMap.put("isCommunityActivity", Boolean.valueOf(false));
        fieldMap.put("position", Integer.valueOf(1));
        ArrayList<Member> steve = new ArrayList<Member>();
        steve.add(new Member("lsuarez@renovations.com", null, com.ibm.lconn.automation.framework.services.common.StringConstants.Component.ACTIVITIES, com.ibm.lconn.automation.framework.services.common.StringConstants.Role.MEMBER, com.ibm.lconn.automation.framework.services.common.StringConstants.MemberType.GROUP));
        fieldMap.put("members", steve);
        Map<String, Object> fieldMap2 = new HashMap<String, Object>();
        fieldMap2.put("position", Integer.valueOf(1));
        fieldMap2.put("title", "testing replies 1");
        fieldMap2.put("content", "comment 1");
        fieldMap2.put("isPrivate", Boolean.valueOf(false));
        Map<String, Object> commentMap = new HashMap<String, Object>();
        commentMap.put("position", Integer.valueOf(1));
        commentMap.put("title", "testing replies 1");
        commentMap.put("content", "You're wrong frank");
        commentMap.put("isPrivate", Boolean.valueOf(false));
        ActivityServiceFactory frankFactory = new ActivityServiceFactory(serverURL, "fadams", "passw0rd");
        ActivityServiceFactory tedFactory = new ActivityServiceFactory(serverURL, "tamado", "passw0rd");
        ActivityServiceFactory lucilleFactory = new ActivityServiceFactory(serverURL, "lsuarez", "passw0rd");
        System.out.println("creating frank's activity");
        ComponentService activity = frankFactory.create("activity", fieldMap);
        System.out.println("finished creating frank's activity");
        ComponentService section = frankFactory.create("section", fieldMap);
        ComponentService todo = frankFactory.create("todo", fieldMap);
        ComponentService entry = frankFactory.create("activityEntry", fieldMap);
        ComponentService reply = frankFactory.create("reply", fieldMap);
        ComponentService reply2 = frankFactory.create("reply", fieldMap2);
        System.out.println("creating ted's activity");
        ComponentService tedActivity = tedFactory.create("activity", fieldMap);
        System.out.println("finished creating ted's activity");
        ComponentService replyTed = tedFactory.create("reply", fieldMap);
        ComponentService tedTodo = tedFactory.create("todo", fieldMap);
        ComponentService tedSection = tedFactory.create("section", fieldMap);
        ComponentService lucilleComment = lucilleFactory.create("reply", commentMap);
        section.attach(activity);
        todo.attach(section);
        entry.attach(section);
        entry.attach(activity);
        reply.attach(todo);
        reply2.attach(entry);
        tedSection.attach(tedActivity);
        tedTodo.attach(tedSection);
        replyTed.attach(tedTodo);
        lucilleComment.attach(todo);
    }
}
