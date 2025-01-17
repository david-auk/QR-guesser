package com.springboot.backend.utils;

public interface AsyncInteractive {
    default void update(String field, Object value){};
    boolean shouldCancel();
}
