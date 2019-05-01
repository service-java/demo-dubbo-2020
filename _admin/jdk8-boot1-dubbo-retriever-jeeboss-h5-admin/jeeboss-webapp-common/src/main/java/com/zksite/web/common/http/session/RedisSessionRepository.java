package com.zksite.web.common.http.session;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.zksite.common.utils.JedisClient;

public class RedisSessionRepository implements SessionRepository {

    @Autowired
    private RedisTemplate<String, RedisSession> redisTemplate;

    @Autowired
    private JedisClient jedisClient;

    private Map<String, Session> sessionContainer = new ConcurrentHashMap<String, Session>();

    private static final String BOUNDED_HASH_KEY_PREFIX = "jeeboss:session:sessions:";

    public Session createSession(String id) {
        RedisSession session = new RedisSession(id);
        sessionContainer.put(id, session);
        return session;
    }

    public void save(Session session) {
        if (session instanceof RedisSession) {
            RedisSession rs = (RedisSession) session;
            redisTemplate.boundHashOps(getKey(session.getId())).putAll(rs.getData());
            jedisClient.expire(getKey(session.getId()), rs.getMaxInactiveInterval());
            sessionContainer.remove(rs.getId());
        }
    }

    private String getKey(String id) {
        return BOUNDED_HASH_KEY_PREFIX + id;
    }

    public Session getSession(String id) {
        Session session = sessionContainer.get(id);
        if (session != null) {
            return session;
        }
        Map<Object, Object> entries = redisTemplate.boundHashOps(getKey(id)).entries();
        if (entries.isEmpty()) {
            return null;
        }
        Map<String, Object> data = new HashMap<String, Object>();
        for (Map.Entry<Object, Object> me : entries.entrySet()) {
            String key = me.getKey().toString();
            data.put(key, me.getValue());
        }
        RedisSession redisSession = new RedisSession(data);
        redisSession.setId(id);
        sessionContainer.put(id, redisSession);
        return redisSession;
    }

    public void delete(String id) {
        sessionContainer.remove(id);
        redisTemplate.delete(getKey(id));
    }

}
