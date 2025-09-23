package com.ibm.lconn.automation.datapop;

import org.apache.abdera.model.Entry;

public abstract class AbstractComponent
    implements ComponentService
{
    private Entry entry;
    private ComponentService parent;

    public AbstractComponent()
    {
    }

    public void setEntry(Entry entry)
    {
        this.entry = entry;
    }

    public String getGroup()
    {
        return parent.getGroup();
    }

    public Entry getEntry()
    {
        return entry;
    }

    public void addParent(ComponentService parent)
    {
        this.parent = parent;
    }
}
