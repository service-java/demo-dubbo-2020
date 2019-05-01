package com.zksite.web.common.http.session;


public interface SessionRepository {

    Session createSession(String id);

    void save(Session session);

    Session getSession(String id);

    void delete(String id);

}
