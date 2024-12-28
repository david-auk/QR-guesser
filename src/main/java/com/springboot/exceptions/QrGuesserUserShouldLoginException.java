package com.springboot.exceptions;

public class QrGuesserUserShouldLoginException extends Exception {

    // TODO check best practice, naming exceptions. -> rename
    public QrGuesserUserShouldLoginException(String errorMessage) {
        super(errorMessage);
    }
}
