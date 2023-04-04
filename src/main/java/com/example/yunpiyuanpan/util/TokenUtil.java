package com.example.yunpiyuanpan.util;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

//JWT加密算法介绍
//https://xie.infoq.cn/article/e55bb7e46be860902e39f9280

public class TokenUtil {

    private static String privateKey = "yunpiyuanpan";

    private static long expireTime = 2592000;


    public static String sign(Long userId, Long fileId){
        String token = null;
        Date expireAt = new Date(System.currentTimeMillis() + expireTime);
        token = JWT.create()
                .withClaim("userId", userId)
                .withClaim("fileId", fileId)
                .withExpiresAt(expireAt)
                .sign(Algorithm.HMAC256(privateKey));
        return token;
    }

    public static boolean verify(String token) {
        try {
            DecodedJWT decodedjwt = JWT.require(Algorithm.HMAC256(privateKey))
                    .build().verify(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static Map<String, Object> parseToken(String token){
        Map<String, Object> map = new HashMap<>();
        DecodedJWT decodedjwt = JWT.require(Algorithm.HMAC256(privateKey))
                .build().verify(token);
        Claim userId = decodedjwt.getClaim("userId");
        Claim fileId = decodedjwt.getClaim("fileId");
        Claim timeStamp = decodedjwt.getClaim("timeStamp");
        map.put("userId", userId.asLong());
        map.put("fileId", fileId.asLong());
        map.put("timeStamp", timeStamp.asString());
        return map;
    }
}
