package org.application.hotelbookingappbe.exception;

public class RoomIsNotAvailableException extends RuntimeException {
    public RoomIsNotAvailableException(String message) {
        super(message);
    }
}
