package com.springboot.controllers.backend.utils;

import java.util.Map;

public class RequestBodyUtil {

    Map<String, String> requestBody;

    public RequestBodyUtil(Map<String, String> requestBody) {
        this.requestBody = requestBody;
    }

    public String getField(String fieldName) {

        String fieldValue = requestBody.get(fieldName);

        if (fieldValue == null) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
        return fieldValue;
    }
}
