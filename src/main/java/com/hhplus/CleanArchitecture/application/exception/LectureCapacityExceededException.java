package com.hhplus.CleanArchitecture.application.exception;

public class LectureCapacityExceededException extends RuntimeException {
    public LectureCapacityExceededException(String message) {
        super(message);
    }
}