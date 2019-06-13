package org.spring.springboot.listener;

import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spring.springboot.config.DuckPorperties;
import org.spring.springboot.domain.TelecomSubscribe;
import org.spring.springboot.mapper.TelecomSubscribeMapper;
import org.spring.springboot.utils.Constant;
import org.spring.springboot.utils.HttpsUtil;
import org.spring.springboot.utils.JsonUtil;
import org.spring.springboot.utils.NotifyType;
import org.spring.springboot.utils.PubMethod;
import org.spring.springboot.utils.RedisConstant;
import org.spring.springboot.utils.StreamClosedHttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Order(2)
public class SubscribeListener implements CommandLineRunner {

    Logger log = LoggerFactory.getLogger(SubscribeListener.class);

    @Autowired
    private TelecomSubscribeMapper telecomSubscribeMapper;

    @Resource(name = "jedisPool")
    private JedisPool jedisPool;

    @Override
    public void run(String... args) throws Exception {
        int count = telecomSubscribeMapper.selectByNotifyType
                (DuckPorperties.getKey(DuckPorperties.TelecomType.SUBSCRIBEDATECHANGETYPE));
        if (count == 0) {
            List<Map<String, String>> subscribe = this.subscribe();
            subscribe.forEach(sub -> {
                this.telecomSubscribeMapper.insertSelective(new TelecomSubscribe(sub.get("subscriptionId"), sub.get("callbackUrl"), sub.get("notifyType")));
            });
        }
        SimpleHttpServer.startServer(Integer.parseInt(DuckPorperties.getKey(DuckPorperties.TelecomType.SOCKETPORT)));
    }

    private List<Map<String, String>> subscribe() throws Exception {
        HttpsUtil httpsUtil = new HttpsUtil();
        httpsUtil.initSSLConfigForTwoWay();

        String accessToken = getAccessToken(httpsUtil);
        String appId = Constant.APPID;
        String urlSubscribe = Constant.SUBSCRIBE_SERVICE_NOTIFYCATION;
        String callbackurl = NotifyType.CALLBACK_BASE_URL;


        List<String> serviceNotifyTypes = NotifyType.getServiceNotifyTypes();
        List<Map<String, String>> reslutList = new ArrayList<>();
        for (String serviceNotifyType : serviceNotifyTypes) {

            Map<String, Object> paramServiceSubscribe = new HashMap<>();
            paramServiceSubscribe.put("notifyType", serviceNotifyType);
            paramServiceSubscribe.put("callbackUrl", callbackurl);

            String jsonRequest = JsonUtil.jsonObj2Sting(paramServiceSubscribe);

            Map<String, String> header = new HashMap<>();
            header.put(Constant.HEADER_APP_KEY, appId);
            header.put(Constant.HEADER_APP_AUTH, "Bearer" + " " + accessToken);

            HttpResponse httpResponse = httpsUtil.doPostJson(urlSubscribe, header, jsonRequest);

            String bodySubscribe = httpsUtil.getHttpResponseBody(httpResponse);

            System.out.println("SubscribeServiceNotification, notifyType:" + serviceNotifyType + ", callbackurl:" + callbackurl + ", response content:");
            System.out.print(httpResponse.getStatusLine());
            System.out.println(bodySubscribe);
            System.out.println();
            reslutList.add(JsonUtil.jsonString2SimpleObj(bodySubscribe, Map.class));
        }
        return reslutList;
    }

    private String getAccessToken(HttpsUtil httpsUtil) {
        try (Jedis jedis = jedisPool.getResource()) {
            String authAccess = jedis.get(RedisConstant.RedisKey.AUTHACCESS);
            if (PubMethod.isEmpty(authAccess)) {
                String fresh = jedis.get(RedisConstant.RedisKey.AUTHREFRESHTOKEN);
                if (PubMethod.isEmpty(fresh)) {
                    Map<String, String> freshMap = getRefreshToken(httpsUtil);
                    jedis.set(RedisConstant.RedisKey.AUTHREFRESHTOKEN, freshMap.get("refreshToken"));
                    jedis.expire(RedisConstant.RedisKey.AUTHREFRESHTOKEN, RedisConstant.RedisKey.AUTHREFRESHTOKEN_TIMEOUT);
                    authAccess = freshMap.get("accessToken");
                } else {
                    Map<String, Object> paramReg = new HashMap<>();
                    paramReg.put("appId", Constant.APPID);
                    paramReg.put("secret", Constant.SECRET);
                    paramReg.put("refreshToken", fresh);

                    String jsonRequest = JsonUtil.jsonObj2Sting(paramReg);
                    StreamClosedHttpResponse bodyRefreshToken = httpsUtil.doPostJsonGetStatusLine(Constant.REFRESH_TOKEN, jsonRequest);
                    System.out.println("RefreshToken, response content:");
                    System.out.println(bodyRefreshToken.getStatusLine());
                    System.out.println(bodyRefreshToken.getContent());
                    log.info("RefreshToken, response content:");
                    log.info(bodyRefreshToken.getContent());

                    Map<String, String> data = JsonUtil.jsonString2SimpleObj(bodyRefreshToken.getContent(), Map.class);
                    authAccess = data.get("accessToken");
                }
                jedis.set(RedisConstant.RedisKey.AUTHACCESS, authAccess);
                jedis.expire(RedisConstant.RedisKey.AUTHACCESS, RedisConstant.RedisKey.AUTHACCESS_TIMEOUT);
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
