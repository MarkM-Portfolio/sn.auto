package com.hcl.lconn.automation.framework.payload;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="feed")
@XmlAccessorType(XmlAccessType.FIELD)

public class ForumResponse {
	
	@XmlElement(name = "updated")
	protected String updated;
	
	@XmlElement(name = "title")
	protected String title;
	
	public String getUpdated() {
		return updated;
	}

	public ForumResponse setUpdated(String updated) {
		this.updated = updated;
		return this;
	}

	public String getTitle() {
		return title;
	}

	public ForumResponse setTitle(String title) {
		this.title = title;
		return this;
	}

	@Override
    public String toString() {
        return "updated = " + updated + ", title = " + title;
    }
    

}
