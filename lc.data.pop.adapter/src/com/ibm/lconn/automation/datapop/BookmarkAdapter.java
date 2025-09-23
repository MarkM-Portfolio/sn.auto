package com.ibm.lconn.automation.datapop;

import com.ibm.lconn.automation.framework.services.common.nodes.Bookmark;
import com.ibm.lconn.automation.framework.services.common.nodes.LCEntry;
import com.ibm.lconn.automation.framework.services.dogear.DogearService;
import org.apache.abdera.model.Entry;

public class BookmarkAdapter
    implements ComponentService
{
    private Bookmark bookmark;
    private Entry entry;
    private String group;

    public BookmarkAdapter(DogearService service, String title, String content, String tagsString, String linkHref)
    {
        bookmark = new Bookmark(title, content, linkHref, tagsString);
        entry = (Entry)service.createBookmark(bookmark);
        bookmark = new Bookmark(entry);
        group = title;
    }

    public void attach(ComponentService attach)
    {
        if(attach instanceof CommunityAdapter)
        {
            addParent(attach);
            attach.attach(this);
        }
    }

    public LCEntry getUnderlyingComponent()
    {
        return bookmark;
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
