package com.zksite.web.common.jwt;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.zksite.common.mybatis.Page;
import com.zksite.common.utils.JedisClient;
import com.zksite.web.common.http.session.SessionManager;

@Component
public class RedisPayloadRepository implements PayloadRepository {

    private static final String JWT_KEY_PREFIX = "jeeboss:jwt:";

    private static final String JWT_EXPIRED_TIME = "jwt_expired_time";

    private static final int DEFAULT_JWT_EXPIRD_TIME = 3600;

    @Autowired
    private Environment environment;

    @Autowired
    private JedisClient jedisClient;

    @Autowired(required = false)
    private SessionManager sessionManager;

    public void save(Payload payload) {
        String property = environment.getProperty(JWT_EXPIRED_TIME);
        if (StringUtils.isNotBlank(property) && NumberUtils.isNumber(property)) {
            jedisClient.set(getKey(payload.getUid().toString()), JSON.toJSONString(payload),
                    Integer.valueOf(property));
        }
        jedisClient.set(getKey(payload.getUid().toString()), JSON.toJSONString(payload),
                DEFAULT_JWT_EXPIRD_TIME);
    }

    public Payload get(String id) {
        if (id == null) {
            return null;
        }
        String string = jedisClient.get(getKey(id));
        return JSON.parseObject(string, Payload.class);
    }

    public void delete(String id) {
        if (sessionManager != null) {
            sessionManager.destroy(id);
        }
        jedisClient.del(getKey(id));
    }

    public void update(Payload payload) {
        save(payload);
    }

    private String getKey(String id) {
        return JWT_KEY_PREFIX + id;
    }

    @Override
    public Page<Payload> find(Page<Payload> page) {
        Set<String> keys = jedisClient.keys(JWT_KEY_PREFIX + "*");
        List<Payload> list = new ArrayList<Payload>();
        if (keys.size() < page.getPageSize()) {// 总数量少于每页数
            if (page.getPageNo() == 1) {
                for (String key : keys) {
                    String string = jedisClient.get(key);
                    Payload payload = JSON.parseObject(string, Payload.class);
                    list.add(payload);
                }
                page.setList(list);
                page.setCount(keys.size());
            } else {
                page.setCount(keys.size());
                page.setList(list);
            }
        } else {
            int start = page.getPageNo() - 1 * page.getPageSize();// 包含
            int end = page.getPageNo() * page.getPageSize();// 不包含
            if (keys.size() < start) {
                page.setCount(keys.size());
                page.setList(list);
            } else {
                if (keys.size() < end) {
                    String[] strs = new String[keys.size()];
                    keys.toArray(strs);
                    for (int i = start; i < strs.length; i++) {
                        String string = jedisClient.get(strs[i]);
                        Payload payload = JSON.parseObject(string, Payload.class);
                        list.add(payload);
                    }
                    page.setCount(keys.size());
                    page.setList(list);
                } else {
                    String[] strs = new String[keys.size()];
                    keys.toArray(strs);
                    for (int i = start; i < end; i++) {
                        String string = jedisClient.get(strs[i]);
                        Payload payload = JSON.parseObject(string, Payload.class);
                        list.add(payload);
                    }
                    page.setCount(keys.size());
                    page.setList(list);
                }
            }
        }
        return page;
    }

}
