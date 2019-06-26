package org.spring.springboot.utils;

public class RedisConstant {

    public interface RedisKey {

        public static final String TELECONAUTH = "TELECONAUTH";/*cache name*/

        public static final String AUTHACCESS = "AUTH_ACCESS";/*ACCESS 设置*/

        public static final String AUTHREFRESHTOKEN = "AUTH_REFRESH_TOKEN"; /*刷新 ACCESS*/

        public static final Integer AUTHACCESS_TIMEOUT = 3300;

        public static final Integer AUTHREFRESHTOKEN_TIMEOUT = 2419200;

    }
}
