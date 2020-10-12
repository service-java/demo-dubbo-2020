package com.zksite.web.common.http.session;

import java.util.Enumeration;

public interface Session {

	public long getCreationTime();

	public String getId();

	public long getLastAccessedTime();

	public void setMaxInactiveInterval(int interval);

	public int getMaxInactiveInterval();

	public Object getAttribute(String name);

	public Object getValue(String name);

	public Enumeration<String> getAttributeNames();

	public String[] getValueNames();

	public void setAttribute(String name, Object value);

	public void putValue(String name, Object value);

	public void removeAttribute(String name);

	public void removeValue(String name);

	public void invalidate();

	public boolean isNew();
}
