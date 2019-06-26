package org.spring.springboot.listener;

import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spring.springboot.commons.AccessToken;
import org.spring.springboot.config.DuckPorperties;
import org.spring.springboot.domain.TelecomSubscribe;
import org.spring.springboot.mapper.TelecomSubscribeMapper;
import org.spring.springboot.utils.Constant;
import org.spring.springboot.utils.HttpsUtil;
import org.spring.springboot.utils.JsonUtil;
import org.spring.springboot.utils.NotifyType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
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
        String accessToken = AccessToken.getInstance().getAccessToken(httpsUtil);
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


}
