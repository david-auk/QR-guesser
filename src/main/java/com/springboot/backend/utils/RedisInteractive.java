package com.springboot.backend.utils;

import com.springboot.backend.progress.Progress;
import com.springboot.backend.progress.ProgressService;

import java.util.UUID;

public class RedisInteractive implements AsyncInteractive {

    private final ProgressService progressService;
    private final Progress progress;
    private final UUID progressUUID;

    public RedisInteractive(ProgressService progressService, Progress progress, UUID progressUUID) {
        this.progressService = progressService;
        this.progress = progress;
        this.progressUUID = progressUUID;
    }

    public RedisInteractive(ProgressService progressService){
        this.progressService = progressService;
        this.progress = new Progress();
        this.progressUUID = UUID.randomUUID();
    }

    public UUID getProgressUUID() {
        return progressUUID;
    }

    @Override
    public void update(String field, Object value) {
        progress.putInfo(field, value);
        progressService.updateProgress(progressUUID, progress);
    }

    @Override
    public boolean shouldCancel() {
        return false;
    }
}
