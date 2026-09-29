package com.library.ebook.exception;

public class LicenseLimitExceededException extends RuntimeException {
    public LicenseLimitExceededException(String message) {
        super(message);
    }
}
