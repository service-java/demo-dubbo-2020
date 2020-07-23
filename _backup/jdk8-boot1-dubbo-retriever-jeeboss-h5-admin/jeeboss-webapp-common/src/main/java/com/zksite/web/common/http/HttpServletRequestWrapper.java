package com.zksite.web.common.http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.zksite.web.common.http.session.Session;
import com.zksite.web.common.http.session.SessionManager;

@SuppressWarnings("deprecation")
public class HttpServletRequestWrapper extends javax.servlet.http.HttpServletRequestWrapper {

    private SessionManager sessionManager;

    private HttpServletRequest request;

    private HttpSession httpSession;

    public HttpServletRequestWrapper(HttpServletRequest request, SessionManager sessionManager) {
        super(request);
        this.sessionManager = sessionManager;
        this.request = request;
        httpSession = request.getSession();
    }

    @Override
    public HttpSession getSession(boolean create) {
        Session session = sessionManager.getSession(create, this.request);
        if (session != null) {
            HttpSessionWrapper sessionWrapper = new HttpSessionWrapper(session,
                    request.getSession().getSessionContext(), request.getServletContext());
            return sessionWrapper;
        }
        return httpSession;// 如果获取不到自定义session，返回servlet容器session
    }

    @Override
    public HttpSession getSession() {
        return getSession(true);
    }

}
