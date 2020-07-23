package com.zksite.web.common.http.session;

import javax.servlet.http.HttpServletRequest;

public interface SessionManager {

    Session getSession(boolean flag, HttpServletRequest request);

    Session createSession(HttpServletRequest request);
    
    Session createSession(String id);

    void destroy(String id);

    void comminSession();
}
