package com.ibm.lconn.automation.datapop;

import com.ibm.lconn.automation.framework.services.common.nodes.LCEntry;
import com.ibm.lconn.automation.framework.services.wikis.WikisService;
import com.ibm.lconn.automation.framework.services.wikis.nodes.WikiPage;
import org.apache.abdera.model.Entry;

public class WikiPageAdapter extends AbstractComponent
{
    WikiPage wikipage;
    WikisService service;

    public WikiPageAdapter(WikisService service, String title, String content, String tagsString)
    {
        this.service = service;
        wikipage = new WikiPage(title, content, tagsString);
        wikipage.setTitle(title);
    }

    public void attach(ComponentService attach)
    {
        addParent(attach);
        setEntry((Entry)service.createWikiPage(attach.getEntry(), wikipage));
    }

    public LCEntry getUnderlyingComponent()
    {
        return wikipage;
    }
}
