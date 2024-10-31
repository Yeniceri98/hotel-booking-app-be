package org.application.hotelbookingappbe.exception;

public class RoomIsNotFoundException extends RuntimeException {
    public RoomIsNotFoundException(String message) {
        super(message);
    }
}
