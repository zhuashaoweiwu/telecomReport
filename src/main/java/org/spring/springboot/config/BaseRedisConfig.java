package org.spring.springboot.config;


import redis.clients.jedis.JedisPoolConfig;
public class BaseRedisConfig {

    protected JedisPoolConfig getJedisPoolConfig(int minIdle, int maxIdle, long maxWaitMillis, boolean blockWhenExhausted, int maxTotal) {
        JedisPoolConfig config= new JedisPoolConfig();
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);
        config.setBlockWhenExhausted(blockWhenExhausted);
        config.setMaxWaitMillis(maxWaitMillis);
        config.setJmxEnabled(true);
        config.setMaxTotal(maxTotal);
        return config;
    }
}
