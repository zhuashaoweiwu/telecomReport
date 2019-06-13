package org.spring.springboot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
@Order(1)
public class DuckPorperties implements CommandLineRunner {

    public static volatile Map<String, String> duckMap = null;


    public enum TelecomType {

        SUBSCRIBECALLBACK("subscribeCallback"),

        SUBSCRIBEDATECHANGETYPE("subscribeDateChangeType"),

        TELECOMBASEURL("telecomBaseUrl"),

        SOCKETPORT("socketPort"),

        APPID("APPID"),

        SECRET("SECRET");

        TelecomType(String code) {
            this.code = code;
        }

        TelecomType(String code, String val) {
            this.code = code;
            this.val = val;
        }

        private String code;

        private String val;

        public String getCode() {
            return this.code;
        }

        public String getVal() {
            return val;
        }

        public String getValue() {
            return this.val;
        }

        public static List<Map<String, Object>> list() {
            return Arrays.stream(TelecomType.values()).map(e -> {
                Map<String, Object> map = new HashMap(1);
                map.put("key", e.getCode());
                map.put("value", e.getValue());
                return map;
            }).collect(Collectors.toList());
        }

    }


    public static String getKey(TelecomType telecomType) {
        return duckMap.get(telecomType.getCode());
    }

    private static String subscribeCallback;

    private static String subscribeDateChangeType;

    private static String telecomBaseUrl;

    private static String APPID;

    private static String SECRET;

    private static String socketPort;

    @Value("${duck.socketPort}")
    public void setSocketPort(String socketPort) {
        DuckPorperties.socketPort = socketPort;
    }

    @Value("${duck.telecomBaseUrl}")
    public void setTelecomBaseUrl(String telecomBaseUrl) {
        this.telecomBaseUrl = telecomBaseUrl;
    }

    @Value("${duck.APPID}")
    public void setAPPID(String APPID) {
        this.APPID = APPID;
    }

    @Value("${duck.SECRET}")
    public void setSECRET(String SECRET) {
        this.SECRET = SECRET;
    }

    @Value("${duck.subscribeCallback}")
    public void setSubscribeCallback(String subscribeCallback) {
        this.subscribeCallback = subscribeCallback;
    }

    @Value("${duck.subscribeDateChangeType}")
    public void setSubscribeDateChangeType(String subscribeDateChangeType) {
        this.subscribeDateChangeType = subscribeDateChangeType;
    }

    @Override
    public void run(String... args) throws Exception {
        duckMap = new ConcurrentHashMap<String, String>(6) {{
            put("subscribeCallback", subscribeCallback);
            put("subscribeDateChangeType", subscribeDateChangeType);
            put("telecomBaseUrl", telecomBaseUrl);
            put("socketPort", socketPort);
            put("APPID", APPID);
            put("SECRET", SECRET);
        }};
    }
}
