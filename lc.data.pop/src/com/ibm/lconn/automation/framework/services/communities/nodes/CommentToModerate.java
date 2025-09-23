package com.ibm.lconn.automation.framework.services.communities.nodes;

import org.apache.abdera.model.Category;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Link;

import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.nodes.LCEntry;

public class CommentToModerate extends LCEntry {
	
	boolean approved = false;
	String inReplyTo = "";
	boolean isForumReply = false;
	
	public CommentToModerate(Entry entry) {
		super(entry);
		
		// get inReplyTo if it's there
		Element e = entry.getExtension(StringConstants.TD_UUID);
		if (e != null)
			inReplyTo = e.getText();
		else {
			isForumReply = true;
			String entryID = entry.getId().toString();
			entryID = entryID.substring(entryID.indexOf('=')+1);
			inReplyTo = "urn:lsid:ibm.com:forum:" + entryID;
		}
	}
	
	private Element getAcceptance() {
		Element snx_moderation = null;
		
		snx_moderation = getFactory().newElement(StringConstants.SNX_MODERATION);
		
		if(approved) {
			snx_moderation.setAttributeValue(StringConstants.ATTR_ACTION, "approve");
		} else {
			snx_moderation.setAttributeValue(StringConstants.ATTR_ACTION, "reject");
		}
		
		return snx_moderation;
	}

	private Element getInReplyTo() {
		if (inReplyTo.isEmpty())
			return null;
		
		Element snx_inrefto = null;
		
		snx_inrefto = getFactory().newElement(StringConstants.SNX_INREFTO);
		
		snx_inrefto.setAttributeValue("rel", "http://www.ibm.com/xmlns/prod/sn/report-item");
		if (isForumReply)
			snx_inrefto.setAttributeValue("ref-item-type","forum-reply");
		else
			snx_inrefto.setAttributeValue("ref-item-type","comment");
		snx_inrefto.setAttributeValue("ref", inReplyTo);

		return snx_inrefto;
	}

	@Override
	public Entry toEntry() {
		Element[] extensions = { getAcceptance() , getInReplyTo() };
		Category[] categories = { };
		
		Entry entry = createBasicEntry(extensions, categories);
		
		Link l = getLinks().get(StringConstants.REL_HISTORY + ":" + StringConstants.MIME_ATOM_XML);
		if (l != null)
			entry.addLink(l.getHref().toString(), StringConstants.REL_RELATED);
				
		return entry;
	}
	
	public void SetApproved(boolean isApproved) {
		approved = isApproved;
	}

}
