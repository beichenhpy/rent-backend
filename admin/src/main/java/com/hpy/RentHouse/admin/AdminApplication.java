package com.hpy.RentHouse.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import util.IdWorker;

/**
 * @author: beichenhpy
 * @Date: 2020/4/11 18:47
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableFeignClients
@EnableEurekaClient
@EnableTransactionManagement
@MapperScan("com.hpy.RentHouse.admin.dao")
public class AdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class,args);
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
