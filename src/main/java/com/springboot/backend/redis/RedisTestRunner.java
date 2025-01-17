package com.springboot.backend.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisTestRunner implements CommandLineRunner {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public void run(String... args){
        redisTemplate.opsForValue().set("test-key", "Hello, Redis!");
        String value = redisTemplate.opsForValue().get("test-key");
        System.out.println("Value from Redis: " + value);
    }
}