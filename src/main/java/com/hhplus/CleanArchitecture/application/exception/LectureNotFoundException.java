package com.hhplus.CleanArchitecture.application.exception;

public class LectureNotFoundException  extends RuntimeException {
    public LectureNotFoundException(String message) {
        super(message);
    }
}