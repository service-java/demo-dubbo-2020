package com.zksite.web.common.http.session;



import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;

public class DefaultSessionManager implements SessionManager {


    @Autowired
    private HttpSessionStrategy httpSessionStrategy;

    @Autowired
    private SessionRepository sessionRepository;

    private ThreadLocal<Session> SESSION_LOCAL = new ThreadLocal<Session>();

    public Session getSession(boolean flag, HttpServletRequest request) {
        Session s = SESSION_LOCAL.get();
        if (s != null) {
            return s;
        }
        String sessionId = httpSessionStrategy.getRequestedSessionId(request);
        if (sessionId != null) {
            Session session = sessionRepository.getSession(sessionId);
            if (session == null) {
                session = createSession(request);
            } else {
                SESSION_LOCAL.set(session);
            }
            return session;
        }
        return null;

    }

    public Session createSession(HttpServletRequest request) {
        return createSession(httpSessionStrategy.getRequestedSessionId(request));
    }

    public Session createSession(String id) {
        Session session = sessionRepository.createSession(id);
        if (session != null) {
            SESSION_LOCAL.set(session);
        }
        return session;
    }

    public void destroy(String id) {
        sessionRepository.delete(id);
    }

    public void comminSession() {
        Session session = SESSION_LOCAL.get();
        sessionRepository.save(session);
        SESSION_LOCAL.remove();
    }
}
