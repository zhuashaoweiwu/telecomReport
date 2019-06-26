package org.spring.springboot.commons;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spring.springboot.listener.SimpleHttpServer;
import org.spring.springboot.listener.SubscribeListener;
import org.spring.springboot.mapper.LightingVolEleRecordMapper;
import org.spring.springboot.utils.Constant;
import org.spring.springboot.utils.HttpsUtil;
import org.spring.springboot.utils.JsonUtil;
import org.spring.springboot.utils.PubMethod;
import org.spring.springboot.utils.RedisConstant;
import org.spring.springboot.utils.SpringUtil;
import org.spring.springboot.utils.StreamClosedHttpResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

public class AccessToken {


    private static AccessToken instance = new AccessToken();

    private AccessToken() {
    }

    public static AccessToken getInstance() {
        return instance;
    }


    private JedisPool jedisPool;


    {
        this.jedisPool = SpringUtil.getBean(JedisPool.class);
    }


    Logger log = LoggerFactory.getLogger(AccessToken.class);

    public synchronized String getAccessToken(HttpsUtil... httpsUtils) {
        try (Jedis jedis = jedisPool.getResource()) {
            String authAccess = jedis.get(RedisConstant.RedisKey.AUTHACCESS);
            if (PubMethod.isEmpty(authAccess)) {
                String refreshToken = "";
                if (PubMethod.isEmpty(httpsUtils)) {
                    HttpsUtil httpsUtil = new HttpsUtil();
                    httpsUtil.initSSLConfigForTwoWay();
                    httpsUtils = new HttpsUtil[]{httpsUtil};
                }
                String fresh = jedis.get(RedisConstant.RedisKey.AUTHREFRESHTOKEN);
                if (PubMethod.isEmpty(fresh)) {
                    Map<String, String> freshMap = getRefreshToken(httpsUtils[0]);
                    refreshToken = freshMap.get("refreshToken");
                    authAccess   = freshMap.get("accessToken");
                } else {
                    Map<String, Object> paramReg = new HashMap<>();
                    paramReg.put("appId", Constant.APPID);
                    paramReg.put("secret", Constant.SECRET);
                    paramReg.put("refreshToken", fresh.trim());

                    String jsonRequest = JsonUtil.jsonObj2Sting(paramReg);
                    StreamClosedHttpResponse bodyRefreshToken = httpsUtils[0].doPostJsonGetStatusLine(Constant.REFRESH_TOKEN, jsonRequest);
                    System.out.println("RefreshToken, response content:");
                    System.out.println(bodyRefreshToken.getStatusLine());
                    System.out.println(bodyRefreshToken.getContent());
                    log.info("RefreshToken, response content:");
                    log.info(bodyRefreshToken.getContent());

                    Map<String, String> data = JsonUtil.jsonString2SimpleObj(bodyRefreshToken.getContent(), Map.class);
                    authAccess =   data.get("accessToken");
                    refreshToken = data.get("refreshToken");

                }
                jedis.set(RedisConstant.RedisKey.AUTHACCESS, authAccess);
                jedis.expire(RedisConstant.RedisKey.AUTHACCESS, RedisConstant.RedisKey.AUTHACCESS_TIMEOUT);
                jedis.set(RedisConstant.RedisKey.AUTHREFRESHTOKEN, refreshToken);
                jedis.expire(RedisConstant.RedisKey.AUTHREFRESHTOKEN, RedisConstant.RedisKey.AUTHREFRESHTOKEN_TIMEOUT);
            }
            return authAccess;
        } catch (Exception e) {
            log.error("获取Token error.");
            log.error(e.getMessage());
            e.printStackTrace();
            return "";
        }


    }

    private Map<String, String> getRefreshToken(HttpsUtil httpsUtil) throws Exception {
        String appId = Constant.APPID;
        String secret = Constant.SECRET;
        String urlLogin = Constant.APP_AUTH;

        Map<String, String> paramLogin = new HashMap<>();
        paramLogin.put("appId", appId);
        paramLogin.put("secret", secret);


        StreamClosedHttpResponse responseLogin = httpsUtil.doPostFormUrlEncodedGetStatusLine(urlLogin, paramLogin);

        System.out.println("app auth success,return accessToken:");
        System.out.println(responseLogin.getStatusLine());
        System.out.println(responseLogin.getContent());
        System.out.println();

        Map<String, String> data = new HashMap<>();
        data = JsonUtil.jsonString2SimpleObj(responseLogin.getContent(), data.getClass());
        return data;
    }
}
