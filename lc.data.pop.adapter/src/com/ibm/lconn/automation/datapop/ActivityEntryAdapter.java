package com.ibm.lconn.automation.datapop;

import com.ibm.lconn.automation.framework.services.activities.ActivitiesService;
import com.ibm.lconn.automation.framework.services.activities.nodes.ActivityEntry;
import com.ibm.lconn.automation.framework.services.common.nodes.LCEntry;
import org.apache.abdera.model.Entry;

public class ActivityEntryAdapter extends AbstractComponent
{
    private ActivityEntry entry;
    private ActivitiesService service;
    private String title;
    private String content;
    private String tagString;
    private int position;
    private boolean isPrivate;
    private boolean isTemplate;

    public ActivityEntryAdapter(ActivitiesService service, String title, String content, String tagString, int position, boolean isPrivate, boolean isTemplate)
    {
        entry = null;
        this.title = title;
        this.position = position;
        this.content = content;
        this.tagString = tagString;
        this.isTemplate = isTemplate;
        this.isPrivate = isPrivate;
        this.service = service;
    }

    public LCEntry getUnderlyingComponent()
    {
        return entry;
    }

    public void attach(ComponentService parent)
    {
        addParent(parent);
        entry = new ActivityEntry(title, content, tagString, position, isPrivate, null, parent.getEntry(), isTemplate);
        setEntry((Entry)service.addNodeToActivity(getGroup(), getUnderlyingComponent()));
    }
}
