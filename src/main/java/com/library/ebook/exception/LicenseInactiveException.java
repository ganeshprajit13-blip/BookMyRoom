package com.library.ebook.exception;

public class LicenseInactiveException extends RuntimeException {
    public LicenseInactiveException(String message) {
        super(message);
    }
}
