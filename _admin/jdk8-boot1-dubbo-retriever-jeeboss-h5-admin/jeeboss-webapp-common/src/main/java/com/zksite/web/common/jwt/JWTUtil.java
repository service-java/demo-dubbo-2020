package com.zksite.web.common.jwt;

import java.io.UnsupportedEncodingException;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;

public class JWTUtil {
    static final String secret = "4c6d208097cc4dc39fecfd278da6eabd";
    static Algorithm algorithm = null;
    private static JWTVerifier jwtVerifier;
    static {
        try {
            algorithm = Algorithm.HMAC256(secret);
            jwtVerifier = JWT.require(JWTUtil.getJWTAlgorithm()).build();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    public static final Algorithm getJWTAlgorithm() {
        return algorithm;
    }

    public static final JWTVerifier getJWTVerifier() {
        return jwtVerifier;
    }
}
