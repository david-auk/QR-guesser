package com.springboot.backend.progress;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Progress implements Serializable {
    private Integer percent;
    private boolean completed;
    private final Map<String, Object> info; // Use a specific type or a generic Object

    // Constructors, Getters, and Setters
    public Progress() {
        this.percent = 0;
        this.completed = false;
        this.info = new HashMap<>();
    }

    public Integer getPercent() {
        return percent;
    }

    public void setPercent(Integer percent) {
        this.percent = percent;
    }

    public Boolean isCompleted() {
        return completed;
    }

    public void complete() {
        this.completed = true;
    }

    public Map<String, Object> getInfo() {
        return info;
    }

    public void putInfo(String fieldName, Object data) {
        info.put(fieldName, data);
    }
}