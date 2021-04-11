package com.hpy.RentHouse.admin.service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Set;

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
    @PutMapping("/RedisSet")
    void set(@RequestParam("key") String key, @RequestParam("value") String value);

    //del1
    @DeleteMapping("/RedisDel")
    void del(@RequestParam("key") String key);

    //keys
    @GetMapping("/keys")
    Set<String> keys(@RequestParam("key") String key);
}
