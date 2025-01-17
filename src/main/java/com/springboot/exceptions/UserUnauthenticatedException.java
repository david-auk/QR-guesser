package com.springboot.exceptions;

public class UserUnauthenticatedException extends Exception {

    // TODO check best practice, naming exceptions. -> rename
    public UserUnauthenticatedException(String errorMessage) {
        super(errorMessage);
    }
}
