package com.hpy.RentHouse.controller;

import com.hpy.RentHouse.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author: beichenhpy
 * @Date: 2020/3/22 16:24
 */
@RestController
public class RedisController {

    @Autowired
    private RedisService redisService;

    @GetMapping("/RedisGet")
    public String get(@RequestParam("key") String key) {
        try {
            return redisService.get(key);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PutMapping("/RedisSet")
    public void set(@RequestParam("key") String key, @RequestParam("value") String value) {
        try {
            redisService.set(key, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PutMapping("/RedisSetMore")
    public void set(@RequestParam("key") String key,
                    @RequestParam("value") String value,
                    @RequestParam("expired") int expired,
                    @RequestParam("kind") TimeUnit kind) {
        try {
            redisService.set(key, value, expired, kind);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @DeleteMapping("/RedisDel")
    public void del(@RequestParam("key") String key) {
        try {
            redisService.delete(key);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @DeleteMapping("/RedisDel2")
    public void del(@RequestParam("key1") String key1, @RequestParam("key2") String key2) {
        try {
            redisService.delete(key1, key2);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @DeleteMapping("/RedisDel3")
    public void del(@RequestParam("key1") String key1, @RequestParam("key2") String key2, @RequestParam("key3") String key3) {
        try {
            redisService.delete(key1, key2, key3);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @DeleteMapping("/RedisDel4")
    public void del(@RequestParam("key1") String key1,
                    @RequestParam("key2") String key2,
                    @RequestParam("key3") String key3,
                    @RequestParam("key4") String key4
    ) {
        try {
            redisService.delete(key1, key2, key3,key4);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/keys")
    public Set<String> keys(@RequestParam("key") String key) {
        try {
            return redisService.keys(key);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}

