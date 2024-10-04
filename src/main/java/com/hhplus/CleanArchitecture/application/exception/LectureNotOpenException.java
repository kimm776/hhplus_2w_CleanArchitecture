package com.hhplus.CleanArchitecture.application.exception;

public class LectureNotOpenException extends RuntimeException {
    public LectureNotOpenException(String message) {
        super(message);
    }
}
