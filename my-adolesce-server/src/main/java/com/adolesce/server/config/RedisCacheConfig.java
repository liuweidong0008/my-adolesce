package com.adolesce.server.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.google.common.collect.ImmutableMap;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Configuration
public class RedisCacheConfig extends CachingConfigurerSupport {
    private static final String DEFAULT_DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";
    private static final String DEFAULT_TIME_PATTERN = "HH:mm:ss";

    //设置失效时间
    private static final Map<String, Duration> cacheMap;

    static {
        cacheMap = ImmutableMap.<String, Duration>builder().put("videos", Duration.ofSeconds(30L)).build();
    }

    //配置RedisCacheManagerBuilderCustomizer对象
    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return (builder) -> {
            //根据不同的cachename设置不同的失效时间
            for (Map.Entry<String, Duration> entry : cacheMap.entrySet()) {
                builder.withCacheConfiguration(entry.getKey(),
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(entry.getValue()));
            }
        };
    }

    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory factory) {
        //自定义一个RedisTemplate<Object, Object>
        //RedisTemplate<Object, Object> template = new RedisTemplate<>();
        //template.setConnectionFactory(factory);

        StringRedisTemplate template = new StringRedisTemplate(factory);

        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);

        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);

        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_PATTERN)));
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_PATTERN)));
        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern(DEFAULT_TIME_PATTERN)));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_PATTERN)));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_PATTERN)));
        javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern(DEFAULT_TIME_PATTERN)));
        om.registerModule(javaTimeModule);

        jackson2JsonRedisSerializer.setObjectMapper(om);

        // 注册jackson序列化器
        // value序列化方式采用jackson
        template.setValueSerializer(jackson2JsonRedisSerializer);
        // hash的value序列化方式采用jackson
        template.setHashValueSerializer(jackson2JsonRedisSerializer);

        //也可以使用自带的json序列化器（和jackson序列化器任选其一，jackson序列化器更节省空间）
        //template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        //template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());


        // 为自定义的RedisTemplate<Object, Object>设置key序列化器，相当于StringRedisTemplate
        //template.setKeySerializer(new StringRedisSerializer());
        // hash的key也采用String的序列化方式
        //template.setHashKeySerializer(StringRedisSerializer.UTF_8);

        template.afterPropertiesSet();
        return template;
    }
}
