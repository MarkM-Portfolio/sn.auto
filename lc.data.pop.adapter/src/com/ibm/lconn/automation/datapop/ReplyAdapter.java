package com.ibm.lconn.automation.datapop;

import com.ibm.lconn.automation.framework.services.activities.ActivitiesService;
import com.ibm.lconn.automation.framework.services.activities.nodes.Reply;
import com.ibm.lconn.automation.framework.services.common.nodes.LCEntry;
import org.apache.abdera.model.Entry;

public class ReplyAdapter extends AbstractComponent
{
    private Reply reply;
    private ActivitiesService service;
    private String title;
    private String content;
    private int position;
    private boolean isPrivate;

    public ReplyAdapter(ActivitiesService service, String title, String content, int position, boolean isPrivate)
    {
        reply = null;
        this.title = title;
        this.position = position;
        this.content = content;
        this.isPrivate = isPrivate;
        this.service = service;
    }

    public LCEntry getUnderlyingComponent()
    {
        return reply;
    }

    public void attach(ComponentService parent)
    {
        addParent(parent);
        reply = new Reply(title, content, position, isPrivate, parent.getEntry());
        setEntry((Entry)service.addNodeToActivity(getGroup(), getUnderlyingComponent()));
    }
}
