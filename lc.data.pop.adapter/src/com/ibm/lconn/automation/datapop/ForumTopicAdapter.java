package com.ibm.lconn.automation.datapop;

import com.ibm.lconn.automation.framework.services.common.nodes.ForumTopic;
import com.ibm.lconn.automation.framework.services.common.nodes.LCEntry;
import com.ibm.lconn.automation.framework.services.forums.ForumsService;
import com.ibm.lconn.automation.framework.services.forums.nodes.Forum;
import org.apache.abdera.model.Entry;

public class ForumTopicAdapter extends AbstractComponent
{
    ForumTopic forumPost;
    ForumsService service;

    public ForumTopicAdapter(ForumsService service, String title, String content, boolean isPinned, boolean isLocked, boolean isQuestion, boolean isAnswered)
    {
        this.service = service;
        forumPost = new ForumTopic(title, content, isPinned, isLocked, isQuestion, isAnswered);
    }

    public void attach(ComponentService attach)
    {
        addParent(attach);
        if(attach instanceof CommunityAdapter)
            attach.attach(this);
        else
            setEntry((Entry)service.createForumTopic((Forum)attach.getUnderlyingComponent(), forumPost));
    }

    public LCEntry getUnderlyingComponent()
    {
        return forumPost;
    }
}
