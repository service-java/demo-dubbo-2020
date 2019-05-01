package com.zksite.common.beans;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.zksite.common.utils.JedisClient;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
@PropertySources(value = {
        @PropertySource(value = "classpath:${spring.profiles.active}/application.properties")})
// 激活spring profile
public class JedisClientConfig {


    @Autowired
    private Environment env;

    @Bean(name = "jedisClient")
    public JedisClient getJedis() {
        JedisPool jedisPool = getJedisPool();
        if (jedisPool == null) {
            return null;
        }
        JedisClient jedisClient = new JedisClient(jedisPool);
        return jedisClient;
    }

    @Bean(name = {"redisConnectionFactory", "jedisConnectionFactory", "jedisConnFactory"})
    public RedisConnectionFactory getRedisConnectionFactory() {
        String application = env.getProperty("application_name", "");
        String suffix = "_redis_";
        String host = env.getProperty(application + suffix + "host");
        if (StringUtils.isBlank(host)) {
            return null;
        }
        String pwd = env.getProperty(application + suffix + "pwd", "");
        Integer port = Integer.valueOf(env.getProperty(application + suffix + "port"));
        JedisPoolConfig jedisPoolConfig = getJedisPoolConfig(application, suffix);
        JedisConnectionFactory redisConnectionFactory = new JedisConnectionFactory();
        redisConnectionFactory.setHostName(host);
        redisConnectionFactory.setPort(port);
        if (StringUtils.isNotBlank(pwd)) {
            redisConnectionFactory.setPassword(pwd);
        }
        redisConnectionFactory.setPoolConfig(jedisPoolConfig);
        return redisConnectionFactory;
    }

    @Bean
    public JedisPool getJedisPool() {

        String application = env.getProperty("application_name", "");
        String suffix = "_redis_";
        String host = env.getProperty(application + suffix + "host");
        if (StringUtils.isBlank(host)) {
            return null;
        }
        String pwd = env.getProperty(application + suffix + "pwd", "");
        Integer port = Integer.valueOf(env.getProperty(application + suffix + "port"));
        JedisPool jedisPool = null;
        if (StringUtils.isNotBlank(pwd)) {
            jedisPool =
                    new JedisPool(getJedisPoolConfig(application, suffix), host, port, 2000, pwd);
        } else {
            jedisPool = new JedisPool(getJedisPoolConfig(application, suffix), host, port);
        }
        return jedisPool;
    }


    private JedisPoolConfig getJedisPoolConfig(String application, String suffix) {
        JedisPoolConfig config = new JedisPoolConfig();

        JedisPollConfigEnvironment jedisPoolConfigEnvironment =
                getJedisPoolConfigEnvironment(application, suffix);
        if (jedisPoolConfigEnvironment.getMaxIdle() != null) {
            config.setMaxIdle(jedisPoolConfigEnvironment.getMaxIdle());
        }
        if (jedisPoolConfigEnvironment.getTestOnBorrow() != null) {
            config.setTestOnBorrow(jedisPoolConfigEnvironment.getTestOnBorrow());
        }
        if (jedisPoolConfigEnvironment.getMaxTotal() != null) {
            config.setMaxTotal(jedisPoolConfigEnvironment.getMaxTotal());
        }
        return config;
    }



    private JedisPollConfigEnvironment getJedisPoolConfigEnvironment(String application,
            String suffix) {
        JedisPollConfigEnvironment jpce = new JedisPollConfigEnvironment();
        String max_idle = env.getProperty(application + suffix + "max_idle", "");
        if (StringUtils.isNotBlank(max_idle)) {
            jpce.setMaxIdle(Integer.valueOf(max_idle));
        }
        String max_total = env.getProperty(application + suffix + "max_total", "");
        if (StringUtils.isNotBlank(max_total)) {
            jpce.setMaxTotal(Integer.valueOf(max_total));
        }
        String testOnBorrow = env.getProperty(application + suffix + "testOnBorrow", "");
        if (StringUtils.isNotBlank(testOnBorrow)) {
            jpce.setTestOnBorrow(Boolean.valueOf(testOnBorrow));
        }
        return jpce;
    }

    @Bean(name={"redisTemplateObject"})
    public RedisTemplate<String, Object> sessionRedisTemplate(
            RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setConnectionFactory(connectionFactory);
        return template;
    }

    class JedisPollConfigEnvironment {
        private Integer maxIdle;

        private Integer maxTotal;

        private Boolean testOnBorrow;



        public Integer getMaxIdle() {
            return maxIdle;
        }

        public void setMaxIdle(Integer maxIdle) {
            this.maxIdle = maxIdle;
        }

        public Integer getMaxTotal() {
            return maxTotal;
        }

        public void setMaxTotal(Integer maxTotal) {
            this.maxTotal = maxTotal;
        }

        public Boolean getTestOnBorrow() {
            return testOnBorrow;
        }

        public void setTestOnBorrow(Boolean testOnBorrow) {
            this.testOnBorrow = testOnBorrow;
        }

    }
}
