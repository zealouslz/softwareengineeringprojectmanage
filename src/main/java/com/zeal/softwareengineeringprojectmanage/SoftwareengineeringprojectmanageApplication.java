package com.zeal.softwareengineeringprojectmanage;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.redis.connection.jedis.JedisConnection;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@MapperScan("com.zeal.softwareengineeringprojectmanage.mapper")
@SpringBootApplication
//@EnableCaching
//@EnableConfigurationProperties(RedisProperties.class)
//@EnableScheduling
public class SoftwareengineeringprojectmanageApplication {

    public static void main(String[] args) {
        SpringApplication.run(SoftwareengineeringprojectmanageApplication.class, args);
    }

}
