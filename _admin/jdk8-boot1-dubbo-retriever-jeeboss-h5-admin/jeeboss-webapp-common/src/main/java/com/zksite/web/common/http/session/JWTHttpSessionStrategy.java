package com.zksite.web.common.http.session;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import com.alibaba.fastjson.JSON;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.zksite.web.common.jwt.JWTUtil;
import com.zksite.web.common.jwt.Payload;
import com.zksite.web.common.jwt.PayloadRepository;

/**
 * 通过解析jwt内容获取sessionId
 * 
 * @author hanjieHu
 *
 */
public class JWTHttpSessionStrategy implements HttpSessionStrategy {

    private static final String JWT_EXPIRED_TIME = "jwt_expired_time";

    @Autowired
    private PayloadRepository payloadRepository;

    @Autowired
    private Environment environment;


    public String getRequestedSessionId(HttpServletRequest request) {
        String token = request.getHeader("payload");// 获取 JWT token
        if (StringUtils.isNotBlank(token)) {
            JWTVerifier jwtVerifier = JWT.require(JWTUtil.getJWTAlgorithm()).build();
            try {
                DecodedJWT decodedJWT = jwtVerifier.verify(token);
                Payload payload = JSON.parseObject(decodedJWT.getSubject(), Payload.class);
                Payload dbPayload = payloadRepository.get(payload.getUid().toString());
                if (dbPayload != null) {
                    long lassAccessedTime = dbPayload.getLastAccessedTime().getTime() / 1000;
                    long nowTime = new Date().getTime() / 1000;
                    String expiredTime = environment.getProperty(JWT_EXPIRED_TIME);
                    int timeLong = 3600;
                    if (StringUtils.isNotBlank(expiredTime) && NumberUtils.isNumber(expiredTime)) {
                        timeLong = Integer.valueOf(expiredTime);
                    }
                    if ((nowTime - lassAccessedTime) < timeLong) {// 只有当前token在有效期才返回
                        return payload.getUid().toString();
                    }
                }
            } catch (Exception e) {
                // 如果抛出异常，token不合法，不做任何处理
            }
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        Payload payload = new Payload();
        payload.setCreateTime(new Date());
        payload.setUid(123);
        String sign = JWT.create().withSubject(JSON.toJSONString(payload))
                .sign(JWTUtil.getJWTAlgorithm());
        System.out.println(sign);
        JWTVerifier jwtVerifier = JWT.require(JWTUtil.getJWTAlgorithm()).build();
        DecodedJWT decodedJWT = jwtVerifier.verify(sign);
        System.out.println(decodedJWT);
    }
}
