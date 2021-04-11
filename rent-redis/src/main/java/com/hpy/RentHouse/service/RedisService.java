package com.hpy.RentHouse.service;


import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author: beichenhpy
 * @Date: 2020/3/22 16:20
 */
public interface RedisService {

    public void set(String key,String value);
    public void set(String key, String value, int expired, TimeUnit kind);

    public String get(String key);

    public Set<String> keys(String key);

    public void delete(String key);
    public void delete(String key1,String key2);
    public void delete(String key1,String key2,String key3);
    public void delete(String key1,String key2,String key3,String key4);
}

