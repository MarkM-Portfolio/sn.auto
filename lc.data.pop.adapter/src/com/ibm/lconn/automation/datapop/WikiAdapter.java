package com.ibm.lconn.automation.datapop;

import com.ibm.lconn.automation.framework.services.common.nodes.LCEntry;
import com.ibm.lconn.automation.framework.services.wikis.WikisService;
import com.ibm.lconn.automation.framework.services.wikis.nodes.Wiki;
import com.ibm.lconn.automation.framework.services.wikis.nodes.WikiMember;
import java.util.ArrayList;
import org.apache.abdera.model.Entry;

public class WikiAdapter
    implements ComponentService
{
    private Wiki wiki;
    private Entry entry;
    private String group;

    public WikiAdapter(WikisService service, String title, String summary, String tagsString, ArrayList<WikiMember> members)
    {
        System.out.println("making wiki object");
        wiki = new Wiki(title, summary, tagsString, members, false);
        System.out.println("posting wiki object");
        entry = (Entry)service.createWiki(wiki);
        System.out.println("making new wiki object from entry");
        wiki = new Wiki(entry);
        group = title;
    }

    public void attach(ComponentService componentservice)
    {
    }

    public LCEntry getUnderlyingComponent()
    {
        return wiki;
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
