package com.springboot.backend.progress;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProgressService {

    @Autowired
    private RedisTemplate<String, Progress> redisTemplate;

    public void updateProgress(UUID taskId, Progress progress) {
        redisTemplate.opsForValue().set(taskId.toString(), progress);
    }

    public Progress getProgress(UUID taskId) {
        return redisTemplate.opsForValue().get(taskId.toString());
    }
}