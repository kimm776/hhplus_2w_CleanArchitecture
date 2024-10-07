package com.hhplus.CleanArchitecture.application.exception;

public class AlreadyAppliedException extends RuntimeException {
    public AlreadyAppliedException(String message) {
        super(message);
    }
}
