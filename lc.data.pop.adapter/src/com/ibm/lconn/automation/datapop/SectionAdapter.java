package com.ibm.lconn.automation.datapop;

import com.ibm.lconn.automation.framework.services.activities.ActivitiesService;
import com.ibm.lconn.automation.framework.services.activities.nodes.Section;
import com.ibm.lconn.automation.framework.services.common.nodes.LCEntry;
import org.apache.abdera.model.Entry;

public class SectionAdapter extends AbstractComponent
{
    private ActivitiesService service;
    private Section section;
    private String title;
    private int position;

    public SectionAdapter(ActivitiesService service, String title, int position)
    {
        section = null;
        this.title = title;
        this.position = position;
        this.service = service;
    }

    public LCEntry getUnderlyingComponent()
    {
        return section;
    }

    public void attach(ComponentService parent)
    {
        addParent(parent);
        section = new Section(title, position, parent.getEntry());
        setEntry((Entry)service.addNodeToActivity(getGroup(), getUnderlyingComponent()));
    }
}
