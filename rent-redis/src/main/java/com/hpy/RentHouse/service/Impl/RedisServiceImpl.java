package com.hpy.RentHouse.service.Impl;

import com.hpy.RentHouse.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author: beichenhpy
 * @Date: 2020/3/22 16:22
 */
@Service
public class RedisServiceImpl implements RedisService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void set(String key, String value) {
        try {
            stringRedisTemplate.opsForValue().set(key,value);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void set(String key, String value, int expired, TimeUnit kind) {
        try {
            stringRedisTemplate.opsForValue().set(key,value,expired,kind);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public String get(String key) {
        String value;
        try {
            value = stringRedisTemplate.opsForValue().get(key);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
        return value;
    }

    @Override
    public Set<String> keys(String key) {
        return stringRedisTemplate.keys(key);
    }


    @Override
    public void delete(String key) {
        try {
            stringRedisTemplate.delete(key);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(String key1, String key2) {
        try {
            stringRedisTemplate.delete(key1);
            stringRedisTemplate.delete(key2);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(String key1, String key2, String key3) {
        try {
            stringRedisTemplate.delete(key1);
            stringRedisTemplate.delete(key2);
            stringRedisTemplate.delete(key3);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(String key1, String key2, String key3, String key4) {
        try {
            stringRedisTemplate.delete(key1);
            stringRedisTemplate.delete(key2);
            stringRedisTemplate.delete(key3);
            stringRedisTemplate.delete(key4);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
