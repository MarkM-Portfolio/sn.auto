package com.ibm.lconn.automation.framework.services.blogs.nodes;

import org.apache.abdera.model.Category;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;

import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.nodes.LCEntry;

/**
 * BlogComment object contains the elements that make up a Reply to a Blog Entry.
 * 
 * @author James Cunningham - jamcunni@ie.ibm.com
 */

public class BlogComment extends LCEntry{
	
	private Entry parent;					/** (Required) Identifies the parent post of this comment. */
	private Element thr_inReplyTo;			/** (Optional) Unique identifier of parent post, only used for backwards compatibility. */
	
	public BlogComment(String content, Entry parent) {
		super();
		
		setParent(parent);
		setContent(content);
	}
	
	public BlogComment(String content, Entry parent,  String contentType) {
		super();
		
		setParent(parent);
		setContent(content);
		setContentType(contentType);
	}
	
	public BlogComment(Entry entry) {
		super(entry);
	}

	@Override
	public Entry toEntry() {
		Element[] extensions = { getInReplyToElement() };

		Category[] categories = { };

		Entry entry = createBasicEntry(extensions, categories);
		entry.setContentAsHtml(getContent());
		
		return entry;
	}
	
	public Entry getParent() {
		return parent;
	}

	public void setParent(Entry parent) {
		this.parent = parent;
		// Reply to seems required despite above comment to the contrary
		setInReplyTo(parent);
	}
	
	/**
	 * @return the href to the parent activity node of this entry.
	 */
	public String getInReplyTo() {
		if(thr_inReplyTo != null) {
			return thr_inReplyTo.getAttributeValue(StringConstants.ATTR_HREF);
		}
		
		return null;
	}

	/**
	 * Set the inReplyTo from an Atom element, only used when an entry is retrieved from the server.
	 * 
	 * @param activityId an Atom Element that contains the activity id information.
	 */
	public void setInReplyTo(Element thr_inReplyTo) {
		this.thr_inReplyTo = thr_inReplyTo;
	}
	
	/**
	 * Set the inReplyTo based on a parent Atom Entry
	 * 
	 * @param entry an Atom Entry that is an activity node that this entry is in reply to.
	 */
	public void setInReplyTo(Entry parentEntry) {
		Element replyElement = null;
		
		if(parentEntry != null) {
			replyElement = getFactory().newExtensionElement(StringConstants.THR_IN_REPLY_TO);
			replyElement.setAttributeValue(StringConstants.ATTR_REF, parent.getId().toString());
			replyElement.setAttributeValue(StringConstants.ATTR_HREF, parent.getLink(StringConstants.REL_EDIT).getHref().toString());
		}
		this.thr_inReplyTo = replyElement;
	}
	
	/**
	 * @return the inReplyTo Atom Element
	 */
	public Element getInReplyToElement() {
		return thr_inReplyTo;
	}

}
