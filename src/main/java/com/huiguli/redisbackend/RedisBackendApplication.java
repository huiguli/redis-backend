package com.huiguli.redisbackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.huiguli.redisbackend.mapper")
@SpringBootApplication
public class RedisBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedisBackendApplication.class, args);
    }

}
