package com.library.ebook.exception;

public class LicenseExpiredException extends RuntimeException {
    public LicenseExpiredException(String message) {
        super(message);
    }
}
