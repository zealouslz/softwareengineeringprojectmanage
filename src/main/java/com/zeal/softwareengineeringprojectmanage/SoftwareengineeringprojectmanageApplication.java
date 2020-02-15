package com.zeal.softwareengineeringprojectmanage;

import org.apache.catalina.mapper.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@MapperScan("com.zeal.softwareengineeringprojectmanage.mapper")
@SpringBootApplication
@EnableCaching
public class SoftwareengineeringprojectmanageApplication {

    public static void main(String[] args) {
        SpringApplication.run(SoftwareengineeringprojectmanageApplication.class, args);
    }

}
