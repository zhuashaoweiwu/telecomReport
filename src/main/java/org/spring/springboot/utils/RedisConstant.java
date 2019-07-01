package org.spring.springboot.utils;

public class RedisConstant {

    public interface RedisKey {

        public static final String TELECONAUTH = "TELECONAUTH";/*cache name*/

        public static final String AUTHACCESS = "AUTH_ACCESS";/*ACCESS 设置*/

        public static final String AUTHREFRESHTOKEN = "AUTH_REFRESH_TOKEN"; /*刷新 ACCESS*/

        public static final Integer AUTHACCESS_TIMEOUT = 3300;

        public static final Integer AUTHREFRESHTOKEN_TIMEOUT = 3000;//鉴权参数，有效时间为1个月，但是电信平台不支持暂定为50分钟

    }
}
