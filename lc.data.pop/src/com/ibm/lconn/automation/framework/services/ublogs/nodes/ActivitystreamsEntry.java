package com.ibm.lconn.automation.framework.services.ublogs.nodes;

import java.util.Iterator;
import java.util.Set;

import org.apache.wink.json4j.OrderedJSONObject;

/**
 * activitystreams entry represents activitystreams information.
 * 
 * @author Ping Wang - wangpin@us.ibm.com
 */
public class ActivitystreamsEntry {
	
	private String published, url, title, content, id, updated, verb;
	private OrderedJSONObject object, target, provider, actor, connctions, openSocial;
		
	public String getPublished() {
		return published;
	}

	public void setPublished(String published) {
		this.published = published;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getUpdated() {
		return updated;
	}

	public void setUpdated(String updated) {
		this.updated = updated;
	}

	public String getVerb() {
		return verb;
	}

	public void setVerb(String verb) {
		this.verb = verb;
	}

	public OrderedJSONObject getProvider() {
		return provider;
	}

	public void setProvider(OrderedJSONObject provider) {
		this.provider = provider;
	}

	public OrderedJSONObject getConnctions() {
		return connctions;
	}

	public void setConnctions(OrderedJSONObject connctions) {
		this.connctions = connctions;
	}

	public OrderedJSONObject getOpenSocial() {
		return openSocial;
	}

	public void setOpenSocial(OrderedJSONObject openSocial) {
		this.openSocial = openSocial;
	}

	public OrderedJSONObject getTarget() {
		return target;
	}

	public void setTarget(OrderedJSONObject target) {
		this.target = target;
	}

	public OrderedJSONObject getActor() {
		return actor;
	}

	public OrderedJSONObject getObject() {
		return object;
	}

	public void setObject(OrderedJSONObject object) {
		this.object = object;
	}

	public void setActor(OrderedJSONObject actor) {
		this.actor = actor;
	}


	public ActivitystreamsEntry(OrderedJSONObject obj1) {
		try {

			@SuppressWarnings("unchecked")
			Set<String> set1 = obj1.keySet();
			Iterator<String> it1 = set1.iterator();
			while (it1.hasNext()) {
				String key1 = it1.next().toString();
				if (key1.contains("content")) {
					String value = obj1.getString(key1);
					setContent(value);
				}
				if (key1.contains("published")) {
					String value = obj1.getString(key1);
					setPublished(value);
				}
				if (key1.contains("url")) {
					String value = obj1.getString(key1);
					setUrl(value);
				}
				if (key1.contains("title")) {
					String value = obj1.getString(key1);
					setTitle(value);
				}
				if (key1.contains("id")) {
					String value = obj1.getString(key1);
					setId(value);
				}
				if (key1.contains("updated")) {
					String value = obj1.getString(key1);
					setUpdated(value);
				}
				if (key1.contains("actor")) {
					OrderedJSONObject obj2 = (OrderedJSONObject) obj1
							.getJSONObject(key1);
					setActor(obj2);
				}
				if (key1.contains("object")) {
					OrderedJSONObject obj2 = (OrderedJSONObject) obj1
							.getJSONObject(key1);
					setObject(obj2);
				}
				if (key1.contains("provider")) {
					OrderedJSONObject obj2 = (OrderedJSONObject) obj1
							.getJSONObject(key1);
					setProvider(obj2);
				}
				if (key1.contains("actor")) {
					OrderedJSONObject obj2 = (OrderedJSONObject) obj1
							.getJSONObject(key1);
					setActor(obj2);
				}
				if (key1.contains("connctions")) {
					OrderedJSONObject obj2 = (OrderedJSONObject) obj1
							.getJSONObject(key1);
					setConnctions(obj2);
				}
				if (key1.contains("openSocial")) {
					OrderedJSONObject obj2 = (OrderedJSONObject) obj1
							.getJSONObject(key1);
					setOpenSocial(obj2);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			// assertTrue(false);
		}

	}
	

}
