package org.kata.exception;

public class ContactMediumNotFoundException extends RuntimeException {
    public ContactMediumNotFoundException(String message) {
        super(message);
    }
}
