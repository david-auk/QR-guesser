package com.springboot.backend.controllers;
import com.springboot.exceptions.JsonErrorResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.springboot.backend.progress.ProgressService;
import com.springboot.backend.progress.Progress;

import java.util.Map;
import java.util.UUID;


@RestController
@RequestMapping("/backend/progress")
public class ProgressController {

    @Autowired
    private ProgressService progressService;

    @GetMapping("/{taskId}")
    public Map<String, Object> getProgress(@PathVariable UUID taskId) throws JsonErrorResponseException {
        Progress progress = progressService.getProgress(taskId);
        if (progress == null) {
            throw new JsonErrorResponseException("Progress not found");
        }
        return Map.of(
                "percent", progress.getPercent(),
                "completed", progress.isCompleted(),
                "info", progress.getInfo()
        );
    }
}