package com.ibm.lconn.automation.datapop;

import com.ibm.lconn.automation.framework.services.activities.ActivitiesService;
import com.ibm.lconn.automation.framework.services.activities.nodes.Todo;
import com.ibm.lconn.automation.framework.services.common.nodes.LCEntry;
import org.apache.abdera.model.Entry;

public class ToDoAdapter extends AbstractComponent
{
    private Todo todo;
    private ActivitiesService service;
    private String title;
    private String content;
    private String tagString;
    private int position;
    private boolean isComplete;
    private boolean isPrivate;
    private String assignedToUserName;
    private String assignedToUserID;

    public ToDoAdapter(ActivitiesService service, String title, String content, String tagString, int position, boolean isComplete, boolean isPrivate, 
            String assignedToUserName, String assignedToUserID)
    {
        todo = null;
        this.title = title;
        this.position = position;
        this.content = content;
        this.tagString = tagString;
        this.assignedToUserID = assignedToUserID;
        this.assignedToUserName = assignedToUserName;
        this.isComplete = isComplete;
        this.isPrivate = isPrivate;
        this.service = service;
    }

    public LCEntry getUnderlyingComponent()
    {
        return todo;
    }

    public void attach(ComponentService parent)
    {
        addParent(parent);
        todo = new Todo(title, content, tagString, position, isComplete, isPrivate, parent.getEntry(), assignedToUserName, assignedToUserID);
        setEntry((Entry)service.addNodeToActivity(getGroup(), getUnderlyingComponent()));
    }
}
