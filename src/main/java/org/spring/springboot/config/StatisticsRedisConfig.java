package org.spring.springboot.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class StatisticsRedisConfig extends BaseRedisConfig {

    @Bean("jedis.config")
    public JedisPoolConfig jedisPoolConfig(
            @Value("${redis.jedis.pool.min-idle}") int minIdle,
            @Value("${redis.jedis.pool.max-idle}") int maxIdle,
            @Value("${redis.jedis.pool.max-wait}") long maxWaitMillis,
            @Value("${redis.jedis.pool.max-active}") int maxActive,
            @Value("${redis.jedis.pool.block-when-exhausted}") boolean blockWhenExhausted

    ){
        return getJedisPoolConfig(minIdle, maxIdle, maxWaitMillis, blockWhenExhausted,maxActive);
    }



    @Bean
    public JedisPool jedisPool(
            @Qualifier("jedis.config") JedisPoolConfig config,
            @Value("${redis.host}") String host,
            @Value("${redis.port}") int port,
            @Value("${redis.timeout}") int timeout,
            @Value("${redis.password}") String password){
        return new JedisPool(config,host,port,timeout,password);
    }
}
