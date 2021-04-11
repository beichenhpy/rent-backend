package com.hpy.RentHouse.user.service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author: beichenhpy
 * @Date: 2020/3/23 22:06
 */
@FeignClient("redis-service")
public interface RedisFeign {

    //get
    @GetMapping("/RedisGet")
    String get(@RequestParam("key") String key);
    //set
    @PutMapping("/RedisSetMore")
    void set(@RequestParam("key") String key,
             @RequestParam("value") String value,
             @RequestParam("expired") int expired,
             @RequestParam("kind") TimeUnit kind);


}
