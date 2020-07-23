package com.zksite.web.common.http;

import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

import com.zksite.web.common.http.session.Session;

@SuppressWarnings("deprecation")
public class HttpSessionWrapper implements HttpSession {

	private Session session;

	private HttpSessionContext sessionContext;

	private ServletContext servletContext;

	public HttpSessionWrapper(Session session, HttpSessionContext sessionContext, ServletContext servletContext) {
		this.session = session;
		this.sessionContext = sessionContext;
		this.servletContext = servletContext;
	}

	public long getCreationTime() {
		return session.getCreationTime();
	}

	public String getId() {
		return session.getId();
	}

	public long getLastAccessedTime() {
		return session.getLastAccessedTime();
	}

	public ServletContext getServletContext() {
		return servletContext;
	}

	public void setMaxInactiveInterval(int interval) {
		session.setMaxInactiveInterval(interval);
	}

	public int getMaxInactiveInterval() {
		return session.getMaxInactiveInterval();
	}

	public HttpSessionContext getSessionContext() {
		return this.sessionContext;
	}

	public Object getAttribute(String name) {
		return session.getAttribute(name);
	}

	public Object getValue(String name) {
		return session.getValue(name);
	}

	public Enumeration<String> getAttributeNames() {
		return session.getAttributeNames();
	}

	public String[] getValueNames() {
		return session.getValueNames();
	}

	public void setAttribute(String name, Object value) {
		session.setAttribute(name, value);
	}

	public void putValue(String name, Object value) {
		session.putValue(name, value);
	}

	public void removeAttribute(String name) {
		session.removeAttribute(name);
	}

	public void removeValue(String name) {
		session.removeValue(name);
	}

	public void invalidate() {
		session.invalidate();
	}

	public boolean isNew() {
		return session.isNew();
	}

    public Session getSession() {
        return session;
    }

}
