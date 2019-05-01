package com.zksite.web.common.http.session;

import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RedisSession implements Session {

	static final String CREATION_TIME = "creationTime";
	static final String MAX_INACTIVE = "maxInactiveInterval";
	static final String LAST_ACCESSED = "lastAccessedTime";

	private Map<String, Object> data = new HashMap<String, Object>();

	private String id;

	public RedisSession(String id) {
		data.put(LAST_ACCESSED, new Date());
		data.put(MAX_INACTIVE, SessionConfig.getMaxInactive());
		data.put(LAST_ACCESSED, new Date());
		this.id = id;
	}

	public RedisSession(Map<String, Object> data) {
		this.data = data;
	}

	public Map<String, Object> getData() {
		return data;
	}

	public long getCreationTime() {
		return ((Date) data.get(CREATION_TIME)).getTime();
	}

	public String getId() {
		return this.id;
	}

	public long getLastAccessedTime() {
		return ((Date) data.get(LAST_ACCESSED)).getTime();
	}

	public void setMaxInactiveInterval(int interval) {
		data.put(MAX_INACTIVE, interval);
	}

    public int getMaxInactiveInterval() {
		return (Integer) data.get(MAX_INACTIVE);
	}

	public Object getAttribute(String name) {
		return data.get(name);
	}

	public Object getValue(String name) {
		return data.get(name);
	}

	public Enumeration<String> getAttributeNames() {
		Set<String> set = new HashSet<String>(data.size());
		for (Map.Entry<String, Object> me : data.entrySet()) {
			set.add(me.getKey());
		}
		return Collections.enumeration(set);
	}

	public String[] getValueNames() {
		Set<String> set = new HashSet<String>(data.size());
		for (Map.Entry<String, Object> me : data.entrySet()) {
			set.add(me.getKey());
		}
		return set.toArray(new String[0]);
	}

	public void setAttribute(String name, Object value) {
		data.put(name, value);
	}

	public void putValue(String name, Object value) {
		data.put(name, value);
	}

	public void removeAttribute(String name) {
		data.remove(name);
	}

	public void removeValue(String name) {
		data.remove(name);
	}

	public void invalidate() {
		data.clear();
	}

	public boolean isNew() {
		return false;
	}

    public void setId(String id) {
        this.id = id;
    }
	
}
