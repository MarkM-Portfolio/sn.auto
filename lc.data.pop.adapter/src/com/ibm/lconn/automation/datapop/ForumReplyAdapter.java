package com.ibm.lconn.automation.datapop;

import com.ibm.lconn.automation.framework.services.common.nodes.ForumReply;
import com.ibm.lconn.automation.framework.services.common.nodes.LCEntry;
import com.ibm.lconn.automation.framework.services.forums.ForumsService;
import org.apache.abdera.model.Entry;

public class ForumReplyAdapter extends AbstractComponent
{
    ForumReply forumReply;
    ForumsService service;
    private String content;
    private String title;
    private boolean isAnswer;

    public ForumReplyAdapter(ForumsService service, String content, String title, boolean isAnswer)
    {
        this.content = content;
        this.service = service;
        this.title = title;
        this.isAnswer = isAnswer;
    }

    public void attach(ComponentService attach)
    {
        addParent(attach);
        forumReply = new ForumReply(title, content, attach.getEntry(), isAnswer);
        setEntry((Entry)service.createForumReply(attach.getEntry(), forumReply));
    }

    public LCEntry getUnderlyingComponent()
    {
        return forumReply;
    }
}
