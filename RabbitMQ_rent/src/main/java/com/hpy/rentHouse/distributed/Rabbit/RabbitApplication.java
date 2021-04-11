package com.hpy.rentHouse.distributed.Rabbit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author: beichenhpy
 * @Date: 2020/2/26 19:26
 *
 * 中间件
 */
@SpringBootApplication
public class RabbitApplication {
    public static void main(String[] args) {
        SpringApplication.run(RabbitApplication.class,args);
    }
}
