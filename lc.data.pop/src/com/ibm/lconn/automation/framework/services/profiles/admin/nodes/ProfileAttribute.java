package com.ibm.lconn.automation.framework.services.profiles.admin.nodes;

import javax.xml.namespace.QName;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.ExtensibleElement;

public class ProfileAttribute {
	
	String key;
	String type;
	String data;
	
	public ProfileAttribute(String key, String type, String data) {
		setKey(key);
		setType(type);
		setData(data);
	}
	
	public ExtensibleElement toEntry() {
		Factory factory = Abdera.getNewFactory();
		ExtensibleElement entry = factory.newElement(new QName("http://ns.opensocial.org/2008/opensocial", "entry"));
	
		entry.addSimpleExtension(new QName("http://ns.opensocial.org/2008/opensocial", "key"), getKey());
		
		ExtensibleElement value = factory.newElement(new QName("http://ns.opensocial.org/2008/opensocial","value"));
		value.addSimpleExtension(new QName("http://ns.opensocial.org/2008/opensocial", "type"), getType());
		value.addSimpleExtension(new QName("http://ns.opensocial.org/2008/opensocial", "data"), getData());
		
		entry.addExtension(value);
		
		return entry;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

}
