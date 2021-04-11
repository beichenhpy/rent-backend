package com.hpy.RentHouse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * @author: beichenhpy
 * @Date: 2020/3/22 16:07
 */
@SpringBootApplication
@EnableEurekaClient
public class rentRedisApplication {
    public static void main(String[] args) {
        SpringApplication.run(rentRedisApplication.class,args);
    }
}
