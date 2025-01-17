package com.springboot.exceptions;

import jakarta.servlet.http.HttpServletRequest;

public class UserUnauthenticatedException extends Exception {

    private final String redirectUri;

    // TODO check best practice, naming exceptions. -> rename
    public UserUnauthenticatedException(String errorMessage, HttpServletRequest request) {
        super(errorMessage);
        redirectUri = request.getRequestURI();
    }

    public UserUnauthenticatedException(String errorMessage, String redirectUri) {
        super(errorMessage);
        if (redirectUri == null) {
            throw new IllegalArgumentException("redirectUri cannot be null");
        } else {
            this.redirectUri = redirectUri;
        }
    }

    public String getRedirectUri() {
        return redirectUri;
    }
}
