package com.ibm.lconn.automation.datapop;

import com.ibm.lconn.automation.framework.services.activities.ActivitiesService;
import com.ibm.lconn.automation.framework.services.activities.nodes.Activity;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.nodes.LCEntry;
import com.ibm.lconn.automation.framework.services.common.nodes.Member;
import java.util.*;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Collection;

public class ActivityAdapter
    implements ComponentService
{

    private Activity activity;
    private Entry entry;
    private String group;

    public ActivityAdapter(ActivitiesService service, String title, String content, String tagsString, Date dueDate, boolean isComplete, boolean isCommunityActivity, 
            ArrayList<Member> members)
    {
        activity = new Activity(title, content, tagsString, dueDate, isComplete, isCommunityActivity);
        entry = (Entry)service.createActivity(activity);
        group = ((Collection)entry.getExtension(StringConstants.APP_COLLECTION)).getHref().toString();
        if(members != null)
        {
            Member mem;
            for(Iterator<Member> iterator = members.iterator(); iterator.hasNext(); service.addMemberToActivity(entry.getLink("http://www.ibm.com/xmlns/prod/sn/member-list").getHref().toString(), mem))
                mem = (Member)iterator.next();

        }
    }

    public void attach(ComponentService componentservice)
    {
    }

    public LCEntry getUnderlyingComponent()
    {
        return activity;
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
