package com.ibm.lconn.automation.datapop;

import com.ibm.lconn.automation.framework.services.common.nodes.LCEntry;
import org.apache.abdera.model.Entry;

public interface ComponentService
{

    public abstract void attach(ComponentService componentservice);

    public abstract LCEntry getUnderlyingComponent();

    public abstract void addParent(ComponentService componentservice);

    public abstract Entry getEntry();

    public abstract String getGroup();

    public abstract void setEntry(Entry entry);
}
