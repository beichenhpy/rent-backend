package com.hpy.RentHouse.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import util.IdWorker;

/**
 * @author: beichenhpy
 * @Date: 2020/3/14 13:32
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableFeignClients
@EnableEurekaClient
@EnableTransactionManagement
@EnableScheduling
@MapperScan("com.hpy.RentHouse.order.dao")
public class OrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class,args);
    }
    /**
     * 使用雪花算法生成随机id
     * @return 返回随机id
     */
    @Bean
    public IdWorker idWorker(){
        return new IdWorker(1,1);
    }
}
