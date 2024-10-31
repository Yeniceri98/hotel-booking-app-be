package org.application.hotelbookingappbe.exception;

public class BookingsNotFoundException extends RuntimeException {
    public BookingsNotFoundException(String message) {
        super(message);
    }
}
